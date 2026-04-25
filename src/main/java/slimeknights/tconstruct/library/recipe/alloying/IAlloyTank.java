package slimeknights.tconstruct.library.recipe.alloying;

import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import slimeknights.mantle.recipe.container.IEmptyContainer;

/**
 * Inventory interface for the sake of alloying
 */
public interface IAlloyTank extends IEmptyContainer, RecipeInput {
  /**
   * Gets the current temperature of this alloy tank
   * @return  Temperature
   */
  int getTemperature();

  /**
   * Gets the number of tanks in this alloy tank. Note any tank may be empty
   * @return number of tanks in the alloy tank
   */
  int getTanks();

  /**
   * Gets the fluid in teh given tank
   * @param tank  Tank number
   * @return  Fluid in tank, empty if the tank is empty or the index is invalid
   */
  FluidStack getFluidInTank(int tank);

  /**
   * Checks if the given recipe can fit
   * @param  fluid    Fluid to add
   * @param  removed  How much fluid this recipe will consume
   * @return true if the recipe will fit
   */
  boolean canFit(FluidStack fluid, int removed);

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
