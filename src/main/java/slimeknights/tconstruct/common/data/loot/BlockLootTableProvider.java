package slimeknights.tconstruct.common.data.loot;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;

/** Minimal 1.21.1-compatible stub while block loot datagen is migrated. */
public class BlockLootTableProvider extends BlockLootSubProvider {
  protected BlockLootTableProvider(HolderLookup.Provider registries) {
    super(Set.<Item>of(), FeatureFlags.REGISTRY.allFlags(), registries);
  }

  @Override
  protected Iterable<Block> getKnownBlocks() {
    return List.of();
  }

  @Override
  protected void generate() {
  }
}
