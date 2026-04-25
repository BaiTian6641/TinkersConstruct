package slimeknights.tconstruct.common.data.tags;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.data.tinkering.AbstractModifierTagProvider;
import slimeknights.tconstruct.tools.TinkerModifiers;
import slimeknights.tconstruct.tools.data.ModifierIds;

import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.AOE_INTERACTION;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ARMOR_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.ARMOR_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BLOCK_WHILE_CHARGING;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BONUS_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BOOT_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BOOT_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_EXTRA_DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_FROSTSHIELD;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_OVERSLIME;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_REINFORCED;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.BYPASS_TANNED;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHARGE_EMPTY_BOW_WITH_DRAWTIME;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHESTPLATE_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.CHESTPLATE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.COSMETIC_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DAMAGE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DRILL_ATTACKS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.DUAL_INTERACTION;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_MODIFIER_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_SLOTLESS_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.EXTRACT_UPGRADE_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GEMS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ARMOR_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_ARMOR_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.GENERAL_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HARVEST_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HARVEST_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HELMET_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.HELMET_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.INTERACTION_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.INVISIBLE_INK_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.KNOCKBACK_SLINGS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.LEGGING_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.LEGGING_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.MELEE_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.MELEE_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.OVERSLIME_FRIEND;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.PROTECTION_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.RANGED_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.RANGED_UPGRADES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.REMOVE_MODIFIER_BLACKLIST;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SECONDARY_DURABILITY;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SELF_KNOCKBACK_SLINGS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SHIELD_ABILITIES;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SLIME_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SLOTLESS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.SPECIAL_DEFENSE;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.TARGET_KNOCKBACK_SLINGS;
import static slimeknights.tconstruct.common.TinkerTags.Modifiers.UPGRADES;

public class ModifierTagProvider extends AbstractModifierTagProvider {
  public ModifierTagProvider(PackOutput packOutput, ExistingFileHelper existingFileHelper) {
    super(packOutput, TConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(GEMS).add(ModifierIds.diamond.getLocation(), ModifierIds.emerald.getLocation());
    tag(INVISIBLE_INK_BLACKLIST).add(
      TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.trim.getId(),
      TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId(),
      ModifierIds.shiny.getLocation(), TinkerModifiers.golden.getId()
    );
    tag(REMOVE_MODIFIER_BLACKLIST).add(TinkerModifiers.creativeSlot.getId(), TinkerModifiers.statOverride.getId());
    tag(EXTRACT_MODIFIER_BLACKLIST).add(
      TinkerModifiers.embellishment.getId(), TinkerModifiers.dyed.getId(), TinkerModifiers.trim.getId(),
      ModifierIds.rebalanced.getLocation(), TinkerModifiers.overslime.getId()
    ).addTag(REMOVE_MODIFIER_BLACKLIST);
    // blacklist modifiers that are not really slotless, they just have a slotless recipe
    tag(EXTRACT_SLOTLESS_BLACKLIST).add(ModifierIds.luck.getLocation(), ModifierIds.toolBelt.getLocation());
    tag(EXTRACT_UPGRADE_BLACKLIST);

    // modifiers in this tag support both left click and right click interaction
    tag(DUAL_INTERACTION).add(
      ModifierIds.bucketing.getLocation(), ModifierIds.splashing.getLocation(),
      ModifierIds.glowing.getLocation(), ModifierIds.firestarter.getLocation(),
      ModifierIds.stripping.getLocation(), ModifierIds.tilling.getLocation(), ModifierIds.pathing.getLocation(),
      ModifierIds.shears.getLocation(), ModifierIds.silkyShears.getLocation(),
      ModifierIds.harvest.getLocation(), ModifierIds.fishing.getLocation(),
      ModifierIds.slimeball.getLocation(), ModifierIds.sliver.getLocation(),
      ModifierIds.pockets.getLocation()
    );
    tag(BLOCK_WHILE_CHARGING).add(
      ModifierIds.flinging.getLocation(), ModifierIds.springing.getLocation(), ModifierIds.bonking.getLocation(), ModifierIds.warping.getLocation(),
      ModifierIds.spitting.getLocation(), ModifierIds.scope.getLocation(), ModifierIds.zoom.getLocation(), ModifierIds.brushing.getLocation(), ModifierIds.throwing.getLocation()
    );
    tag(SLIME_DEFENSE).add(
      ModifierIds.meleeProtection.getLocation(), ModifierIds.projectileProtection.getLocation(),
      ModifierIds.fireProtection.getLocation(), ModifierIds.magicProtection.getLocation(),
      ModifierIds.blastProtection.getLocation()
    );
    tag(OVERSLIME_FRIEND).add(
      ModifierIds.overgrowth.getLocation(), ModifierIds.overcast.getLocation(), ModifierIds.overburn.getLocation(), ModifierIds.overlord.getLocation(), ModifierIds.overshield.getLocation(), ModifierIds.overwield.getLocation(),
      ModifierIds.overforced.getLocation(), ModifierIds.overslimeFriend.getLocation(), TinkerModifiers.overworked.getId()
    );
    tag(AOE_INTERACTION).add(ModifierIds.pathing.getLocation(), ModifierIds.stripping.getLocation(), ModifierIds.tilling.getLocation(), ModifierIds.brushing.getLocation(), ModifierIds.splashing.getLocation(), ModifierIds.harvest.getLocation());
    tag(CHARGE_EMPTY_BOW_WITH_DRAWTIME).add(ModifierIds.flinging.getLocation(), ModifierIds.springing.getLocation(), ModifierIds.bonking.getLocation(), ModifierIds.warping.getLocation(), ModifierIds.throwing.getLocation());
    tag(CHARGE_EMPTY_BOW_WITHOUT_DRAWTIME).add(ModifierIds.blocking.getLocation(), ModifierIds.scope.getLocation(), ModifierIds.zoom.getLocation(), ModifierIds.slurping.getLocation(), ModifierIds.tasty.getLocation());
    tag(DRILL_ATTACKS).add(ModifierIds.flinging.getLocation(), ModifierIds.springing.getLocation(), ModifierIds.grapple.getLocation());
    tag(SELF_KNOCKBACK_SLINGS).add(ModifierIds.flinging.getLocation(), ModifierIds.springing.getLocation());
    tag(TARGET_KNOCKBACK_SLINGS).add(ModifierIds.bonking.getLocation());
    tag(KNOCKBACK_SLINGS).addTag(SELF_KNOCKBACK_SLINGS, TARGET_KNOCKBACK_SLINGS);

    // durability tags
    tag(BYPASS_TANNED).addTag(SECONDARY_DURABILITY);
    tag(SECONDARY_DURABILITY).add(
      // protection is used for the damage correction on armor, which tanned should prevent
      ModifierIds.protection.getLocation(),
      // counter-attack
      ModifierIds.thorns.getLocation(), ModifierIds.fiery.getLocation(), ModifierIds.freezing.getLocation(), ModifierIds.springy.getLocation(),
      ModifierIds.pierce.getLocation(), ModifierIds.venom.getLocation(), ModifierIds.conductive.getLocation(), ModifierIds.shock.getLocation(),
      // special effects
      ModifierIds.necrotic.getLocation(), ModifierIds.restore.getLocation(), TinkerModifiers.enderporting.getId()
    );
    tag(BYPASS_REINFORCED).add(ModifierIds.glowing.getLocation());
    tag(BYPASS_EXTRA_DURABILITY);
    tag(BYPASS_OVERSLIME).addTag(BYPASS_EXTRA_DURABILITY).add(ModifierIds.glowing.getLocation());
    tag(BYPASS_FROSTSHIELD).addTag(BYPASS_EXTRA_DURABILITY).add(ModifierIds.glowing.getLocation());

    // book tags
    this.tag(UPGRADES).addTag(GENERAL_UPGRADES, MELEE_UPGRADES, DAMAGE_UPGRADES, HARVEST_UPGRADES, ARMOR_UPGRADES, RANGED_UPGRADES);
    this.tag(ARMOR_UPGRADES).addTag(GENERAL_ARMOR_UPGRADES, HELMET_UPGRADES, CHESTPLATE_UPGRADES, LEGGING_UPGRADES, BOOT_UPGRADES);
    this.tag(ABILITIES).addTag(GENERAL_ABILITIES, INTERACTION_ABILITIES, MELEE_ABILITIES, HARVEST_ABILITIES, ARMOR_ABILITIES, RANGED_ABILITIES);
    this.tag(ARMOR_ABILITIES).addTag(GENERAL_ARMOR_ABILITIES, HELMET_ABILITIES, CHESTPLATE_ABILITIES, LEGGING_ABILITIES, BOOT_ABILITIES, SHIELD_ABILITIES);
    this.tag(DEFENSE).addTag(PROTECTION_DEFENSE, SPECIAL_DEFENSE);
    this.tag(SLOTLESS).addTag(GENERAL_SLOTLESS, BONUS_SLOTLESS, COSMETIC_SLOTLESS);

    // upgrades
    this.tag(GENERAL_UPGRADES).add(
      ModifierIds.diamond.getLocation(), ModifierIds.emerald.getLocation(), ModifierIds.netherite.getLocation(),
      ModifierIds.reinforced.getLocation(), ModifierIds.overforced.getLocation(), ModifierIds.soulbound.getLocation(),
      ModifierIds.experienced.getLocation(), ModifierIds.magnetic.getLocation(), ModifierIds.scope.getLocation(), ModifierIds.zoom.getLocation(),
      ModifierIds.tank.getLocation(), ModifierIds.smelting.getLocation(), ModifierIds.fireprimer.getLocation())
        .addOptional(ModifierIds.theOneProbe.getLocation());

    this.tag(MELEE_UPGRADES).add(
      ModifierIds.knockback.getLocation(), ModifierIds.padded.getLocation(),
      TinkerModifiers.severing.getId(), ModifierIds.necrotic.getLocation(), ModifierIds.sweeping.getLocation(),
      ModifierIds.fiery.getLocation(), ModifierIds.freezing.getLocation());
    this.tag(DAMAGE_UPGRADES).add(
      ModifierIds.sharpness.getLocation(), ModifierIds.pierce.getLocation(), ModifierIds.swiftstrike.getLocation(),
      ModifierIds.antiaquatic.getLocation(), ModifierIds.baneOfSssss.getLocation(), ModifierIds.cooling.getLocation(), ModifierIds.killager.getLocation(), ModifierIds.smite.getLocation());

    this.tag(HARVEST_UPGRADES).add(ModifierIds.haste.getLocation(), ModifierIds.blasting.getLocation(), ModifierIds.hydraulic.getLocation(), ModifierIds.lightspeed.getLocation());

    this.tag(GENERAL_ARMOR_UPGRADES).add(
      ModifierIds.fiery.getLocation(), ModifierIds.freezing.getLocation(), ModifierIds.thorns.getLocation(),
      ModifierIds.ricochet.getLocation(), ModifierIds.springy.getLocation(), ModifierIds.blockade.getLocation());
    this.tag(HELMET_UPGRADES).add(TinkerModifiers.itemFrame.getId(), ModifierIds.respiration.getLocation(), ModifierIds.minimap.getLocation()).addOptional(ModifierIds.headlight.getLocation());
    this.tag(CHESTPLATE_UPGRADES).add(ModifierIds.haste.getLocation(), ModifierIds.knockback.getLocation(), TinkerModifiers.sleeves.getId());
    this.tag(LEGGING_UPGRADES).add(ModifierIds.leaping.getLocation(), TinkerModifiers.shieldStrap.getId(), ModifierIds.speedy.getLocation(), ModifierIds.swiftSneak.getLocation(), ModifierIds.stepUp.getLocation());
    this.tag(BOOT_UPGRADES).add(ModifierIds.depthStrider.getLocation(), ModifierIds.featherFalling.getLocation(), ModifierIds.longFall.getLocation(), ModifierIds.lightspeed.getLocation(), ModifierIds.soulspeed.getLocation());

    this.tag(RANGED_UPGRADES).add(
      ModifierIds.pierce.getLocation(), ModifierIds.power.getLocation(), ModifierIds.punch.getLocation(), ModifierIds.quickCharge.getLocation(),
      TinkerModifiers.sinistral.getId(), ModifierIds.trueshot.getLocation(),
      ModifierIds.fiery.getLocation(), ModifierIds.freezing.getLocation(),
      ModifierIds.arrowPierce.getLocation(), ModifierIds.bounce.getLocation(), ModifierIds.necrotic.getLocation(),
      ModifierIds.lure.getLocation(), ModifierIds.collecting.getLocation(), ModifierIds.fins.getLocation());

    // abilities
    this.tag(GENERAL_ABILITIES).add(
      ModifierIds.expanded.getLocation(), ModifierIds.gilded.getLocation(), ModifierIds.unbreakable.getLocation(),
      ModifierIds.luck.getLocation(), TinkerModifiers.melting.getId());
    this.tag(MELEE_ABILITIES).add(
      ModifierIds.blocking.getLocation(), TinkerModifiers.parrying.getId(),
      TinkerModifiers.dualWielding.getId(), ModifierIds.spilling.getLocation());
    this.tag(HARVEST_ABILITIES).add(ModifierIds.autosmelt.getLocation(), TinkerModifiers.exchanging.getId(), ModifierIds.silky.getLocation());
    this.tag(RANGED_ABILITIES).add(
      ModifierIds.bulkQuiver.getLocation(), ModifierIds.trickQuiver.getLocation(),
      ModifierIds.crystalshot.getLocation(), ModifierIds.multishot.getLocation(), ModifierIds.ballista.getLocation(),
      ModifierIds.grapple.getLocation(),
      ModifierIds.channeling.getLocation(), ModifierIds.returning.getLocation(),
      ModifierIds.slimeball.getLocation(), ModifierIds.sliver.getLocation());
    this.tag(INTERACTION_ABILITIES).add(
      ModifierIds.bucketing.getLocation(), ModifierIds.firestarter.getLocation(), ModifierIds.glowing.getLocation(),
      ModifierIds.pathing.getLocation(), ModifierIds.stripping.getLocation(), ModifierIds.tilling.getLocation(), ModifierIds.brushing.getLocation(),
      ModifierIds.spitting.getLocation(), ModifierIds.splashing.getLocation(), ModifierIds.slurping.getLocation(),
      ModifierIds.bonking.getLocation(), ModifierIds.flinging.getLocation(), ModifierIds.springing.getLocation(), ModifierIds.warping.getLocation(),
      ModifierIds.throwing.getLocation(), ModifierIds.drillAttack.getLocation());
    // armor
    this.tag(GENERAL_ARMOR_ABILITIES).add(ModifierIds.protection.getLocation(), TinkerModifiers.bursting.getId(), TinkerModifiers.wetting.getId());
    this.tag(HELMET_ABILITIES).add(ModifierIds.aquaAffinity.getLocation(), ModifierIds.slurping.getLocation());
    this.tag(CHESTPLATE_ABILITIES).add(TinkerModifiers.ambidextrous.getId(), ModifierIds.reach.getLocation(), ModifierIds.strength.getLocation(), ModifierIds.wings.getLocation());
    this.tag(LEGGING_ABILITIES).add(ModifierIds.pockets.getLocation(), ModifierIds.soulBelt.getLocation(), ModifierIds.toolBelt.getLocation(), ModifierIds.craftingTable.getLocation());
    this.tag(BOOT_ABILITIES).add(
      ModifierIds.bouncy.getLocation(), ModifierIds.doubleJump.getLocation(),
      ModifierIds.flamewake.getLocation(), ModifierIds.snowdrift.getLocation(), ModifierIds.tilling.getLocation(), ModifierIds.pathing.getLocation(), ModifierIds.frostWalker.getLocation(), ModifierIds.glowing.getLocation());
    this.tag(SHIELD_ABILITIES).add(ModifierIds.boundless.getLocation(), ModifierIds.reflecting.getLocation());

    // defense
    this.tag(PROTECTION_DEFENSE).add(
      ModifierIds.blastProtection.getLocation(), ModifierIds.fireProtection.getLocation(), ModifierIds.magicProtection.getLocation(),
      ModifierIds.meleeProtection.getLocation(), ModifierIds.projectileProtection.getLocation(),
      ModifierIds.dragonborn.getLocation(), ModifierIds.shulking.getLocation(), ModifierIds.turtleShell.getLocation());
    this.tag(SPECIAL_DEFENSE).add(ModifierIds.knockbackResistance.getLocation(), ModifierIds.revitalizing.getLocation());

    // slotless
    this.tag(GENERAL_SLOTLESS).add(
      TinkerModifiers.overslime.getId(), ModifierIds.worldbound.getLocation(),
      ModifierIds.offhanded.getLocation(), ModifierIds.blunted.getLocation(), ModifierIds.workbench.getLocation(),
      ModifierIds.blindshot.getLocation(), ModifierIds.barebow.getLocation());
    this.tag(BONUS_SLOTLESS).add(
      ModifierIds.draconic.getLocation(), ModifierIds.rebalanced.getLocation(), ModifierIds.redirected.getLocation(), TinkerModifiers.trim.getId(),
      ModifierIds.harmonious.getLocation(), ModifierIds.recapitated.getLocation(), ModifierIds.forecast.getLocation(), ModifierIds.writable.getLocation())
      .addOptional(ModifierIds.embossed.getLocation());
    this.tag(COSMETIC_SLOTLESS).add(
      ModifierIds.shiny.getLocation(),
      TinkerModifiers.dyed.getId(), TinkerModifiers.embellishment.getId(), TinkerModifiers.banner.getId(),
      ModifierIds.farsighted.getLocation(), ModifierIds.nearsighted.getLocation());
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Modifier Tag Provider";
  }
}

