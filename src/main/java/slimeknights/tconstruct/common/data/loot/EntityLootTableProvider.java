package slimeknights.tconstruct.common.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;

import java.util.stream.Stream;

/** Minimal 1.21.1-compatible stub while entity loot datagen is migrated. */
public class EntityLootTableProvider extends EntityLootSubProvider {
  protected EntityLootTableProvider(HolderLookup.Provider registries) {
    super(FeatureFlags.REGISTRY.allFlags(), registries);
  }

  @Override
  protected Stream<EntityType<?>> getKnownEntityTypes() {
    return Stream.empty();
  }

  @Override
  public void generate() {
  }
}
