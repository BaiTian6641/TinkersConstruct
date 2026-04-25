package slimeknights.tconstruct.compat.neoforge.common;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Compatibility layer for removed TierSortingRegistry helpers.
 */
public final class TierSortingRegistry {
  private static final List<Tier> SORTED = List.of(Tiers.WOOD, Tiers.STONE, Tiers.IRON, Tiers.DIAMOND, Tiers.NETHERITE);

  private TierSortingRegistry() {}

  public static Tier byName(ResourceLocation id) {
    if (id == null) {
      return Tiers.WOOD;
    }
    String path = id.getPath().toLowerCase(Locale.ROOT);
    return switch (path) {
      case "wood", "wooden" -> Tiers.WOOD;
      case "stone" -> Tiers.STONE;
      case "iron" -> Tiers.IRON;
      case "gold", "golden" -> Tiers.GOLD;
      case "diamond" -> Tiers.DIAMOND;
      case "netherite" -> Tiers.NETHERITE;
      default -> Tiers.WOOD;
    };
  }

  public static ResourceLocation getName(Tier tier) {
    if (tier == Tiers.WOOD) return ResourceLocation.withDefaultNamespace("wood");
    if (tier == Tiers.STONE) return ResourceLocation.withDefaultNamespace("stone");
    if (tier == Tiers.IRON) return ResourceLocation.withDefaultNamespace("iron");
    if (tier == Tiers.GOLD) return ResourceLocation.withDefaultNamespace("gold");
    if (tier == Tiers.DIAMOND) return ResourceLocation.withDefaultNamespace("diamond");
    if (tier == Tiers.NETHERITE) return ResourceLocation.withDefaultNamespace("netherite");
    return BuiltInRegistries.ITEM.getKey(tier.getRepairIngredient().getItems().length > 0 ? tier.getRepairIngredient().getItems()[0].getItem() : net.minecraft.world.item.Items.AIR);
  }

  public static List<Tier> getSortedTiers() {
    return new ArrayList<>(SORTED);
  }

  public static boolean isCorrectTierForDrops(Tier tier, BlockState state) {
    if (!state.requiresCorrectToolForDrops()) {
      return true;
    }
    int level = getTierLevel(tier);
    if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
      return level >= getTierLevel(Tiers.DIAMOND);
    }
    if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
      return level >= getTierLevel(Tiers.IRON);
    }
    if (state.is(BlockTags.NEEDS_STONE_TOOL)) {
      return level >= getTierLevel(Tiers.STONE);
    }
    return true;
  }

  private static int getTierLevel(Tier tier) {
    if (tier == Tiers.WOOD || tier == Tiers.GOLD) return 0;
    if (tier == Tiers.STONE) return 1;
    if (tier == Tiers.IRON) return 2;
    if (tier == Tiers.DIAMOND) return 3;
    if (tier == Tiers.NETHERITE) return 4;
    return 0;
  }
}
