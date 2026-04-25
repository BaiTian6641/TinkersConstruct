package slimeknights.tconstruct.tables.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import slimeknights.mantle.util.BlockEntityHelper;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;

import javax.annotation.Nullable;

/** Dyeable chest block */
public class TinkersChestBlockItem extends BlockItem  {
  private static CompoundTag getCustomTag(ItemStack stack) {
    return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
  }

  private static void setCustomTag(ItemStack stack, CompoundTag tag) {
    if (tag.isEmpty()) {
      stack.remove(DataComponents.CUSTOM_DATA);
    } else {
      stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }
  }

  private static CompoundTag getDisplayTag(ItemStack stack) {
    CompoundTag root = getCustomTag(stack);
    if (root.contains("display", Tag.TAG_COMPOUND)) {
      return root.getCompound("display");
    }
    return new CompoundTag();
  }

  public TinkersChestBlockItem(Block blockIn, Properties builder) {
    super(blockIn, builder);
  }

  public static int getColor(ItemStack stack) {
    CompoundTag tag = getDisplayTag(stack);
    return tag != null && tag.contains("color", Tag.TAG_ANY_NUMERIC) ? tag.getInt("color") : TinkersChestBlockEntity.DEFAULT_COLOR;
  }

  public static boolean hasCustomColor(ItemStack stack) {
    CompoundTag tag = getDisplayTag(stack);
    return tag != null && tag.contains("color", Tag.TAG_ANY_NUMERIC);
  }

  public static void setColor(ItemStack stack, int color) {
    CompoundTag root = getCustomTag(stack);
    CompoundTag display = root.contains("display", Tag.TAG_COMPOUND) ? root.getCompound("display") : new CompoundTag();
    display.putInt("color", color);
    root.put("display", display);
    setCustomTag(stack, root);
  }

  @Override
  protected boolean updateCustomBlockEntityTag(BlockPos pos, Level worldIn, @Nullable Player player, ItemStack stack, BlockState state) {
    boolean result = super.updateCustomBlockEntityTag(pos, worldIn, player, stack, state);
    if (hasCustomColor(stack)) {
      int color = getColor(stack);
      BlockEntityHelper.get(TinkersChestBlockEntity.class, worldIn, pos).ifPresent(te -> te.setColor(color));
    }
    return result;
  }
}
