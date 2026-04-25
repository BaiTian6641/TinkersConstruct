package slimeknights.tconstruct.fluids.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.BrewingRecipe;

/** Recipe for transforming a bottle, depending on a vanilla brewing recipe to get the ingredient */
public class BottleBrewingRecipe extends BrewingRecipe {
  private final Ingredient ingredient;

  public BottleBrewingRecipe(Ingredient input, Item from, Item to, ItemStack output) {
    super(input, Ingredient.EMPTY, output);
    this.ingredient = input;
  }

  @Override
  public boolean isIngredient(ItemStack stack) {
    return ingredient.test(stack);
  }

  @Override
  public Ingredient getIngredient() {
    return ingredient;
  }
}
