package slimeknights.tconstruct.world.client;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.world.block.FoliageType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Color reload listener for all slime foliage types
 */
public class SlimeColorReloadListener extends SimplePreparableReloadListener<int[]> {
  private final FoliageType color;
  private final ResourceLocation path;
  public SlimeColorReloadListener(FoliageType color) {
    this.color = color;
    this.path = ResourceLocation.fromNamespaceAndPath(TConstruct.MOD_ID, "textures/colormap/" + color.getSerializedName() + "_grass_color.png");
  }

  /**
   * Performs any reloading that can be done off-thread, such as file IO
   */
  @Override
  protected int[] prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
    try (InputStream stream = resourceManager.open(path)) {
      BufferedImage image = ImageIO.read(stream);
      if (image == null) {
        return new int[0];
      }
      int width = image.getWidth();
      int height = image.getHeight();
      int[] pixels = new int[width * height];
      image.getRGB(0, 0, width, height, pixels, 0, width);
      return pixels;
    } catch (IOException exception) {
      TConstruct.LOG.error("Failed to load slime colors from {}", path, exception);
      return new int[0];
    }
  }

  @Override
  protected void apply(int[] buffer, ResourceManager resourceManager, ProfilerFiller profiler) {
    if (buffer.length != 0) {
      SlimeColorizer.setGrassColor(color, buffer);
    }
  }
}
