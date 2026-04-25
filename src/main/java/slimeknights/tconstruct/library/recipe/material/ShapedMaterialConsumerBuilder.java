package slimeknights.tconstruct.library.recipe.material;

import lombok.NoArgsConstructor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/** Special variant of {@link ConsumerWrapperBuilder} for {@link ShapedMaterialRecipe} */
@Deprecated
@NoArgsConstructor(staticName = "wrap")
public class ShapedMaterialConsumerBuilder {
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Adds a material to the builder */
  public ShapedMaterialConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Builds the wrapped consumer */
  public Consumer<RecipeOutput> build(Consumer<RecipeOutput> consumer) {
    return recipeOutput -> consumer.accept(build(recipeOutput));
  }

  public RecipeOutput build(RecipeOutput consumer) {
    List<MaterialVariantId> extraMaterials = List.copyOf(materials);
    return new RecipeOutput() {
      @Override
      public net.minecraft.advancements.Advancement.Builder advancement() {
        return consumer.advancement();
      }

      @Override
      public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
        consumer.accept(id, wrapRecipe(recipe, extraMaterials), advancement);
      }

      @Override
      public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
        consumer.accept(id, wrapRecipe(recipe, extraMaterials), advancement, conditions);
      }

      @Override
      public RecipeOutput withConditions(ICondition... conditions) {
        return build(consumer.withConditions(conditions));
      }
    };
  }

  private static Recipe<?> wrapRecipe(Recipe<?> recipe, List<MaterialVariantId> materials) {
    return recipe instanceof ShapedRecipe shaped ? new ShapedMaterialRecipe(shaped, materials) : recipe;
  }
}
