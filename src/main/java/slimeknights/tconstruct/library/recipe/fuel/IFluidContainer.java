package slimeknights.tconstruct.library.recipe.fuel;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import slimeknights.mantle.recipe.container.IEmptyContainer;

/**
 * Inventory containing just a single fluid
 */
public interface IFluidContainer extends IEmptyContainer, RecipeInput {
  /**
   * Gets the fluid contained in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();

  @Override
  default int size() {
    return getContainerSize();
  }

  @Override
  default boolean isEmpty() {
    return IEmptyContainer.super.isEmpty();
  }

  @Override
  default ItemStack getItem(int index) {
    return ItemStack.EMPTY;
  }
}
