package slimeknights.tconstruct.tools.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.BlockDestructionProgress;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RenderHighlightEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import slimeknights.tconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator.AOEMatchType;
import slimeknights.tconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.utils.BlockSideHitListener;

import java.lang.reflect.Field;
import java.util.Iterator;

@EventBusSubscriber(modid = TConstruct.MOD_ID, value = Dist.CLIENT, bus = Bus.GAME)
public class ToolRenderEvents {
  /** Maximum number of blocks from the iterator to render. */
  private static final int MAX_BLOCKS = 60;

  /**
   * Renders the outline on the extra blocks
   *
   * @param event the highlight event
   */
  @SubscribeEvent
  static void renderBlockHighlights(RenderHighlightEvent.Block event) {
    Level world = Minecraft.getInstance().level;
    Player player = Minecraft.getInstance().player;
    if (world == null || player == null) {
      return;
    }
    ItemStack stack = player.getMainHandItem();
    if (stack.isEmpty() || !stack.is(TinkerTags.Items.MODIFIABLE)) {
      return;
    }
    HitResult result = Minecraft.getInstance().hitResult;
    if (result == null || result.getType() != Type.BLOCK) {
      return;
    }
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return;
    }
    BlockHitResult blockTrace = event.getTarget();
    BlockPos origin = blockTrace.getBlockPos();
    BlockState state = world.getBlockState(origin);
    AOEMatchType matchType = AOEMatchType.BREAKING;
    if (tool.getModifiers().has(TinkerTags.Modifiers.AOE_INTERACTION)) {
      matchType = AOEMatchType.DISPLAY;
    } else if (!IsEffectiveToolHook.isEffective(tool, state)) {
      return;
    }
    UseOnContext context = new UseOnContext(world, player, InteractionHand.MAIN_HAND, stack, blockTrace);
    Iterator<BlockPos> extraBlocks = tool.getHook(ToolHooks.AOE_ITERATOR).getBlocks(tool, context, state, matchType).iterator();
    if (!extraBlocks.hasNext()) {
      return;
    }

    LevelRenderer worldRender = event.getLevelRenderer();
    PoseStack matrices = event.getPoseStack();
    MultiBufferSource.BufferSource buffers = Minecraft.getInstance().renderBuffers().bufferSource();
    VertexConsumer vertexBuilder = buffers.getBuffer(RenderType.lines());
    matrices.pushPose();

    Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
    Entity viewEntity = renderInfo.getEntity();
    Vec3 vector3d = renderInfo.getPosition();
    double x = vector3d.x();
    double y = vector3d.y();
    double z = vector3d.z();
    int rendered = 0;
    do {
      BlockPos pos = extraBlocks.next();
      if (world.getWorldBorder().isWithinBounds(pos)) {
        rendered++;
        BlockState outlineState = world.getBlockState(pos);
        LevelRenderer.renderVoxelShape(
          matrices,
          vertexBuilder,
          outlineState.getShape(world, pos, CollisionContext.of(viewEntity)),
          pos.getX() - x,
          pos.getY() - y,
          pos.getZ() - z,
          0.0f,
          0.0f,
          0.0f,
          0.4f,
          false
        );
      }
    } while (rendered < MAX_BLOCKS && extraBlocks.hasNext());
    matrices.popPose();
    buffers.endBatch();
  }

  /** Renders the block damage process on the extra blocks */
  @SubscribeEvent
  static void renderBlockDamageProgress(RenderLevelStageEvent event) {
    if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
      return;
    }

    MultiPlayerGameMode controller = Minecraft.getInstance().gameMode;
    if (controller == null || !controller.isDestroying()) {
      return;
    }
    Level world = Minecraft.getInstance().level;
    Player player = Minecraft.getInstance().player;
    if (world == null || player == null || Minecraft.getInstance().getCameraEntity() == null) {
      return;
    }
    ItemStack stack = player.getMainHandItem();
    if (stack.isEmpty() || !stack.is(TinkerTags.Items.HARVEST)) {
      return;
    }
    HitResult result = Minecraft.getInstance().hitResult;
    if (result == null || result.getType() != Type.BLOCK) {
      return;
    }
    ToolStack tool = ToolStack.from(stack);
    if (tool.isBroken()) {
      return;
    }

    BlockHitResult blockTrace = (BlockHitResult)result;
    BlockPos target = blockTrace.getBlockPos();
    BlockDestructionProgress progress = findDestructionProgress(Minecraft.getInstance().levelRenderer, target);
    if (progress == null) {
      return;
    }

    BlockState state = world.getBlockState(target);
    if (!IsEffectiveToolHook.isEffective(tool, state)) {
      return;
    }
    UseOnContext context = new UseOnContext(world, player, InteractionHand.MAIN_HAND, stack, blockTrace.withDirection(BlockSideHitListener.getClientSideHit()));
    Iterator<BlockPos> extraBlocks = tool.getHook(ToolHooks.AOE_ITERATOR).getBlocks(tool, context, state, AreaOfEffectIterator.AOEMatchType.BREAKING).iterator();
    if (!extraBlocks.hasNext()) {
      return;
    }

    PoseStack matrices = event.getPoseStack();
    matrices.pushPose();
    MultiBufferSource.BufferSource vertices = Minecraft.getInstance().renderBuffers().crumblingBufferSource();
    VertexConsumer vertexBuilder = vertices.getBuffer(ModelBakery.DESTROY_TYPES.get(progress.getProgress()));

    Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
    double x = renderInfo.getPosition().x;
    double y = renderInfo.getPosition().y;
    double z = renderInfo.getPosition().z;
    BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
    int rendered = 0;
    do {
      BlockPos pos = extraBlocks.next();
      matrices.pushPose();
      matrices.translate(pos.getX() - x, pos.getY() - y, pos.getZ() - z);
      PoseStack.Pose entry = matrices.last();
      VertexConsumer blockBuilder = new SheetedDecalTextureGenerator(vertexBuilder, entry, 1.0f);
      dispatcher.renderBreakingTexture(world.getBlockState(pos), pos, world, matrices, blockBuilder);
      matrices.popPose();
      rendered++;
    } while (rendered < MAX_BLOCKS && extraBlocks.hasNext());
    matrices.popPose();
    vertices.endBatch();
  }

  /** Finds current block destruction progress map via reflection across mapping name drift. */
  @SuppressWarnings("unchecked")
  private static BlockDestructionProgress findDestructionProgress(LevelRenderer renderer, BlockPos target) {
    Int2ObjectMap<BlockDestructionProgress> map = null;
    try {
      Field field;
      try {
        field = LevelRenderer.class.getDeclaredField("destroyingBlocks");
      } catch (NoSuchFieldException ex) {
        field = LevelRenderer.class.getDeclaredField("destructionProgress");
      }
      field.setAccessible(true);
      Object value = field.get(renderer);
      if (value instanceof Int2ObjectMap<?>) {
        map = (Int2ObjectMap<BlockDestructionProgress>)value;
      }
    } catch (ReflectiveOperationException ignored) {
      return null;
    }
    if (map == null) {
      return null;
    }
    for (Int2ObjectMap.Entry<BlockDestructionProgress> entry : map.int2ObjectEntrySet()) {
      BlockDestructionProgress progress = entry.getValue();
      if (progress.getPos().equals(target)) {
        return progress;
      }
    }
    return null;
  }
}
