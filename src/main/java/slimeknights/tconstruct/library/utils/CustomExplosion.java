package slimeknights.tconstruct.library.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
// NOTE: ProtectionEnchantment removed or relocated in 1.21.1
// import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.EntityBasedExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
// NOTE: ProtectionEnchantment and ForgeEventFactory removed or relocated in 1.21.1
// import net.minecraft.world.item.enchantment.ProtectionEnchantment;
// import net.neoforged.neoforge.event.EventHooks;
import slimeknights.tconstruct.library.tools.helper.ToolAttackUtil;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

/** Helper class for more control over explosions */
public class CustomExplosion extends Explosion {
  /** Size of the hollowed out cube determining the number of rays to cast */
  private static final int RAY_COUNT = 16;
  private static final int MAX_RAY = RAY_COUNT - 1;
  /** Default predicate for which entities to match */
  public static final Predicate<Entity> DEFAULT_ENTITY_PREDICATE = entity -> entity != null && entity.isAlive() && !entity.isSpectator();

  protected final Level customLevel;
  protected final double customX;
  protected final double customY;
  protected final double customZ;
  protected final float customRadius;
  protected final boolean customFire;
  @Nullable
  protected final Entity customSource;
  protected final ExplosionDamageCalculator customDamageCalculator;
  protected final DamageSource customDamageSource;

  /** Maximum damage to deal; setting to 7*2*radius will match the vanilla explosion. */
  protected final float damage;
  /** Entity knockback scale. May be 0 to prevent knockback or negative to reverse knockback */
  protected final float knockback;
  /** Determines which entities are affected by this explosion */
  protected final Predicate<Entity> entityPredicate;
  /** If true, explosion damage bypasses the invulnerability time */
  protected final boolean bypassInvulnerableTime;

  public CustomExplosion(Level level, Vec3 location, float radius, @Nullable Entity sourceEntity, @Nullable Predicate<Entity> entityPredicate, float damage, @Nullable DamageSource damageSource, float knockback, @Nullable ExplosionDamageCalculator damageCalculator, boolean placeFire, BlockInteraction blockInteraction, boolean bypassInvulnerableTime) {
    super(level, sourceEntity, location.x, location.y, location.z, radius, placeFire, blockInteraction);
    this.customLevel = level;
    this.customX = location.x;
    this.customY = location.y;
    this.customZ = location.z;
    this.customRadius = radius;
    this.customFire = placeFire;
    this.customSource = sourceEntity;
    this.customDamageCalculator = damageCalculator != null ? damageCalculator : (sourceEntity != null ? new EntityBasedExplosionDamageCalculator(sourceEntity) : new ExplosionDamageCalculator());
    this.customDamageSource = damageSource != null ? damageSource : level.damageSources().explosion(this);
    this.entityPredicate = Objects.requireNonNullElse(entityPredicate, DEFAULT_ENTITY_PREDICATE);
    this.damage = damage;
    this.knockback = knockback;
    this.bypassInvulnerableTime = bypassInvulnerableTime;
  }

  public CustomExplosion(Level level, Vec3 location, float radius, @Nullable Entity sourceEntity, @Nullable Predicate<Entity> entityPredicate, float damage, @Nullable DamageSource damageSource, float knockback, @Nullable ExplosionDamageCalculator damageCalculator, boolean placeFire, BlockInteraction blockInteraction) {
    this(level, location, radius, sourceEntity, entityPredicate ,damage, damageSource, knockback, damageCalculator, placeFire, blockInteraction, false);
  }

  @Override
  public void explode() {
    this.customLevel.gameEvent(this.customSource, GameEvent.EXPLODE, center());
    calculateHitBlocks();
    damageAndPushEntities();
  }

  /** Calculates the list of blocks to hit; the actual block damage won't happen until {@link #finalizeExplosion(boolean)} */
  protected void calculateHitBlocks() {
    // optimization: if we are not interacting with blocks, no need to calculate blocks
    if (!interactsWithBlocks() && !customFire) {
      return;
    }

    Set<BlockPos> set = new HashSet<>();
    // loop over a hollowed out 16x cube
    for (int rayX = 0; rayX < RAY_COUNT; rayX++) {
      for (int rayY = 0; rayY < RAY_COUNT; rayY++) {
        for (int rayZ = 0; rayZ < RAY_COUNT; rayZ++) {
          if (rayX == 0 || rayX == MAX_RAY || rayY == 0 || rayY == MAX_RAY || rayZ == 0 || rayZ == MAX_RAY) {
            // determine direction to go, then step in 0.3 unit vector increments
            double stepX = rayX * 2.0 / MAX_RAY - 1;
            double stepY = rayY * 2.0 / MAX_RAY - 1;
            double stepZ = rayZ * 2.0 / MAX_RAY - 1;
            double stepScale = 0.3f / Math.sqrt(stepX * stepX + stepY * stepY + stepZ * stepZ);
            stepX *= stepScale;
            stepY *= stepScale;
            stepZ *= stepScale;

            // keep moving in the direction of the ray until we run out of power; means blocks with high blast resistance shield those with less
            double targetX = this.customX;
            double targetY = this.customY;
            double targetZ = this.customZ;
            for (float power = this.customRadius * (0.7f + customLevel.random.nextFloat() * 0.6f); power > 0; power -= 0.225f) {
              BlockPos target = BlockPos.containing(targetX, targetY, targetZ);
              BlockState block = customLevel.getBlockState(target);
              FluidState fluid = customLevel.getFluidState(target);
              if (!customLevel.isInWorldBounds(target)) {
                break;
              }

              // reduce power based on blast resistance
              Optional<Float> resistance = customDamageCalculator.getBlockExplosionResistance(this, customLevel, target, block, fluid);
              if (resistance.isPresent()) {
                power -= (resistance.get() + 0.3f) * 0.3f;
              }

              // remove block if power is high enough
              // optimization: skip air if not placing fires to save network traffic
              if ((customFire || !block.isAir()) && power > 0 && customDamageCalculator.shouldBlockExplode(this, customLevel, target, block, power)) {
                set.add(target);
              }

              // vanilla difference - we moved the 0.3 multiplier to the original step variables to avoid needing to compute as often
              targetX += stepX;
              targetY += stepY;
              targetZ += stepZ;
            }
          }
        }
      }
    }
    getToBlow().addAll(set);
  }

  /** Called to run the logic for damaging and blasting back entities in range */
  protected void damageAndPushEntities() {
    // skip running if we disabled both damage and knockback as there is nothing left to do
    if (damage <= 0 && knockback == 0) {
      return;
    }

    float diameter = this.customRadius * 2;
    // small behavior change: we filter the list of entities on fetch, meaning the forge event gets the filtered list
    List<Entity> list = this.customLevel.getEntities(
      this.customSource,
      new AABB(Math.floor(this.customX - diameter - 1),
               Math.floor(this.customY - diameter - 1),
               Math.floor(this.customZ - diameter - 1),
               Math.floor(this.customX + diameter + 1),
               Math.floor(this.customY + diameter + 1),
               Math.floor(this.customZ + diameter + 1)),
      entityPredicate);
    // NOTE: EventHooks.onExplosionDetonate() removed - explosion detonate event disabled
    // EventHooks.onExplosionDetonate(this.level, this, list, diameter);

    // start pushing entities
    // this logic is for the most part identical to vanilla, except taking better advantage of vec3
    Vec3 center = center();
    for (Entity entity : list) {
      Vec3 dir = entity.position().subtract(center);
      double length = dir.length();
      double distance = length / diameter;
      if (distance <= 1) {
        // non-TNT uses eye height for explosion direction
        if (!(entity instanceof PrimedTnt)) {
          dir = dir.add(0, entity.getEyeY() - entity.getY(), 0);
          length = dir.length();
        }
        // vanilla change: a bit of tolerance on the length check to match normalize
        if (length > 1.0E-4D) {
          double strength = (1 - distance) * getSeenPercent(center, entity);
          // vanilla change: instead of multiplying the damage by 7, we make that a parameter, which can be 0 for no damage
          if (damage > 0) {
            int toDeal = (int) ((strength * strength + strength) / 2 * damage + 1);
            if (bypassInvulnerableTime) {
              ToolAttackUtil.hurtNoInvulnerableTime(entity, customDamageSource, toDeal);
            } else {
              entity.hurt(customDamageSource, toDeal);
            }
          }

          // apply enchantment to reduce knockback
          if (knockback != 0) {
            double adjustedStrength = strength * knockback;
            // NOTE: ProtectionEnchantment.getExplosionKnockbackAfterDampener() removed - enchantment knockback dampening disabled
            // if (entity instanceof LivingEntity living) {
            //   adjustedStrength = ProtectionEnchantment.getExplosionKnockbackAfterDampener(living, adjustedStrength);
            // }
            Vec3 velocity = dir.scale(adjustedStrength / length);
            entity.setDeltaMovement(entity.getDeltaMovement().add(velocity));
            if (entity instanceof Player player) {
              if (!player.isCreative() || !player.getAbilities().flying) {
                getHitPlayers().put(player, velocity);
              }
            }
          }
        }
      }
    }
  }

  /** Runs the logic on the server, syncing to the client. Based on {@link ServerLevel#explode(Entity, DamageSource, ExplosionDamageCalculator, double, double, double, float, boolean, ExplosionInteraction)}*/
  public void handleServer() {
    // based on ServerLevel#explode
    if (!customLevel.isClientSide) {
      // NOTE: EventHooks.onExplosionStart() removed - explosion start event disabled
      // if (!EventHooks.onExplosionStart(level, this)) {
      explode();
      finalizeExplosion(false);
      syncToClient();
      // }
    }
  }

  /** Runs the logic on both sides */
  public void doDualSide(Level level, boolean spawnParticles) {
    // NOTE: EventHooks.onExplosionStart() removed - explosion start event disabled
    // if (!EventHooks.onExplosionStart(level, this)) {
    explode();
    finalizeExplosion(spawnParticles);
    // }
  }

  /** Syncs this explosion to the client */
  public void syncToClient() {
    if (!customLevel.isClientSide && customLevel instanceof ServerLevel server) {
      // skip position sync if there are no blocks to be removed
      List<BlockPos> toBlow = interactsWithBlocks() ? getToBlow() : List.of();
      Vec3 position = center();
      for (ServerPlayer player : server.players()) {
        if (player.distanceToSqr(position) < 4096.0D) {
          player.connection.send(new ClientboundExplodePacket(
            customX,
            customY,
            customZ,
            customRadius,
            toBlow,
            getHitPlayers().get(player),
            getBlockInteraction(),
            getSmallExplosionParticles(),
            getLargeExplosionParticles(),
            getExplosionSound()));
        }
      }
    }
  }
}
