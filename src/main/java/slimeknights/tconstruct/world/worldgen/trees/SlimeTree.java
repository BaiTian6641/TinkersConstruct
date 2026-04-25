package slimeknights.tconstruct.world.worldgen.trees;

import net.minecraft.world.level.block.grower.TreeGrower;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Optional;

public final class SlimeTree {
  private SlimeTree() {}

  public static TreeGrower create(FoliageType foliageType) {
    return switch (foliageType) {
      case EARTH -> new TreeGrower("tconstruct_earth_slime", Optional.empty(), Optional.of(TinkerStructures.earthSlimeTree), Optional.empty());
      case SKY -> new TreeGrower("tconstruct_sky_slime", Optional.empty(), Optional.of(TinkerStructures.skySlimeTree), Optional.empty());
      case ENDER -> new TreeGrower(
        "tconstruct_ender_slime",
        0.85f,
        Optional.empty(),
        Optional.empty(),
        Optional.of(TinkerStructures.enderSlimeTree),
        Optional.of(TinkerStructures.enderSlimeTreeTall),
        Optional.empty(),
        Optional.empty());
      case BLOOD -> new TreeGrower("tconstruct_blood_slime", Optional.empty(), Optional.of(TinkerStructures.bloodSlimeFungus), Optional.empty());
      case ICHOR -> new TreeGrower("tconstruct_ichor_slime", Optional.empty(), Optional.of(TinkerStructures.ichorSlimeFungus), Optional.empty());
    };
  }
}
