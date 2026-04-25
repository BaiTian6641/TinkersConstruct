package slimeknights.tconstruct.tools.modules.interaction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.fluid.FluidTransferHelper;
import slimeknights.tconstruct.library.json.TinkerLoadables;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.hook.interaction.BlockInteractionModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.interaction.InteractionSource;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.List;

/** Module allowing a tool to interact with a tank beyond the normal block interaction behavior. */
public record TankInteractionModule(@Nullable InteractionSource source) implements ModifierModule, BlockInteractionModifierHook {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<TankInteractionModule>defaultHooks(ModifierHooks.BLOCK_INTERACT);
  public static final RecordLoadable<TankInteractionModule> LOADER = RecordLoadable.create(TinkerLoadables.INTERACTION_SOURCE.nullableField("interaction_source", TankInteractionModule::source), TankInteractionModule::new);

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public InteractionResult beforeBlockUse(IToolStackView tool, ModifierEntry modifier, UseOnContext context, InteractionSource source) {
    // if source is not null, its a filter and only that source may use this. Used mainly for armor
    if ((this.source != null && this.source != source) || !tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      return InteractionResult.PASS;
    }

    Level world = context.getLevel();
    BlockPos target = context.getClickedPos();
    Player player = context.getPlayer();
    if (player == null) {
      return InteractionResult.PASS;
    }
    BlockHitResult hit = new BlockHitResult(context.getClickLocation(), context.getClickedFace(), target, false);
    if (FluidTransferHelper.interactWithTank(world, target, player, context.getHand(), hit)) {
      return InteractionResult.sidedSuccess(world.isClientSide);
    }
    return InteractionResult.PASS;
  }
}
