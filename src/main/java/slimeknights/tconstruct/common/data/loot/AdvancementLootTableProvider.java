package slimeknights.tconstruct.common.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import java.util.function.BiConsumer;

/** Minimal 1.21.1-compatible stub while advancement loot datagen is migrated. */
public class AdvancementLootTableProvider implements LootTableSubProvider {
  public AdvancementLootTableProvider(HolderLookup.Provider registries) {
  }

  @Override
  public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
  }
}
