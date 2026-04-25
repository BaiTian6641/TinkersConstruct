package slimeknights.tconstruct.shared.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock.GlassColor;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ClearStainedGlassPaneBlock extends ClearGlassPaneBlock {

  private final GlassColor glassColor;
  public ClearStainedGlassPaneBlock(Properties builder, GlassColor glassColor) {
    super(builder);
    this.glassColor = glassColor;
  }

  @Nullable
  @Override
  public Integer getBeaconColorMultiplier(BlockState state, LevelReader world, BlockPos pos, BlockPos beaconPos) {
    float[] rgb = this.glassColor.getRgb();
    return ((int)(rgb[0] * 255.0F) << 16) | ((int)(rgb[1] * 255.0F) << 8) | (int)(rgb[2] * 255.0F);
  }
}
