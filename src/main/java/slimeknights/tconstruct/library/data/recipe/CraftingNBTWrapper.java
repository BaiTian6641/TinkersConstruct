package slimeknights.tconstruct.library.data.recipe;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import java.util.function.Consumer;

/**
 * Compatibility helper for recipe output wrapping in 1.21.1.
 * RecipeOutput no longer exposes a mutable FinishedRecipe JSON object, so this wrapper currently forwards unchanged.
 */
public final class CraftingNBTWrapper {
  private CraftingNBTWrapper() {}

  public static Consumer<RecipeOutput> wrap(Consumer<RecipeOutput> base, CompoundTag nbt) {
    return base;
  }

  public static RecipeOutput wrap(RecipeOutput base, CompoundTag nbt) {
    return base;
  }
}
