package slimeknights.tconstruct.library.recipe.material;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import slimeknights.mantle.recipe.data.ConsumerWrapperBuilder;
import slimeknights.tconstruct.library.materials.definition.MaterialVariantId;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/** Special variant of {@link ConsumerWrapperBuilder} for {@link ShapedMaterialsRecipe} and {@link ShapelessMaterialsRecipe} */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MaterialsConsumerBuilder {
  @Nullable
  private static final Field PATTERN_DATA_FIELD = findPatternDataField();

  private final String parts;
  private final int partCount;
  private final List<MaterialVariantId> materials = new ArrayList<>();

  /** Creates a new shaped recipe with the given ingredients as parts */
  public static MaterialsConsumerBuilder shaped(String parts) {
    if (parts.isEmpty()) {
      throw new IllegalArgumentException("Parts may not be empty");
    }
    return new MaterialsConsumerBuilder(parts, 0);
  }

  /** Creates a new shapeless recipe with the first ingredients as parts */
  public static MaterialsConsumerBuilder shapeless(int parts) {
    if (parts <= 0) {
      throw new IllegalArgumentException("Parts must be greater than 0");
    }
    return new MaterialsConsumerBuilder("", parts);
  }

  /** Adds a material to the builder */
  public MaterialsConsumerBuilder material(MaterialVariantId material) {
    materials.add(material);
    return this;
  }

  /** Builds the wrapped consumer */
  public Consumer<RecipeOutput> build(Consumer<RecipeOutput> consumer) {
    return recipeOutput -> consumer.accept(build(recipeOutput));
  }

  public RecipeOutput build(RecipeOutput consumer) {
    List<MaterialVariantId> extraMaterials = List.copyOf(materials);
    String partSymbols = parts;
    int shapelessPartCount = partCount;
    return new RecipeOutput() {
      @Override
      public net.minecraft.advancements.Advancement.Builder advancement() {
        return consumer.advancement();
      }

      @Override
      public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement) {
        consumer.accept(id, wrapRecipe(recipe, partSymbols, shapelessPartCount, extraMaterials), advancement);
      }

      @Override
      public void accept(ResourceLocation id, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
        consumer.accept(id, wrapRecipe(recipe, partSymbols, shapelessPartCount, extraMaterials), advancement, conditions);
      }

      @Override
      public RecipeOutput withConditions(ICondition... conditions) {
        return build(consumer.withConditions(conditions));
      }
    };
  }

  private static Recipe<?> wrapRecipe(Recipe<?> recipe, String parts, int partCount, List<MaterialVariantId> materials) {
    if (partCount > 0 && recipe instanceof ShapelessRecipe shapeless) {
      return new ShapelessMaterialsRecipe(shapeless, partCount, materials);
    }
    if (!parts.isEmpty() && recipe instanceof ShapedRecipe shaped) {
      return new ShapedMaterialsRecipe(shaped, getPartIngredients(shaped, parts), materials);
    }
    return recipe;
  }

  private static List<Ingredient> getPartIngredients(ShapedRecipe recipe, String parts) {
    Optional<?> patternData = getPatternData(recipe.pattern);
    if (patternData.isEmpty()) {
      throw new IllegalStateException("Missing shaped recipe key data for material recipe wrapper: " + recipe);
    }
    Object data = patternData.get();
    @SuppressWarnings("unchecked")
    Map<Character, Ingredient> key = ((ShapedRecipePattern.Data) data).key();
    List<Ingredient> ingredients = new ArrayList<>(parts.length());
    for (int index = 0; index < parts.length(); index++) {
      char symbol = parts.charAt(index);
      Ingredient ingredient = key.get(symbol);
      if (ingredient == null) {
        throw new IllegalArgumentException("Parts references symbol '" + symbol + "' but it is not defined in the shaped recipe key");
      }
      ingredients.add(ingredient);
    }
    return List.copyOf(ingredients);
  }

  private static Optional<?> getPatternData(ShapedRecipePattern pattern) {
    if (PATTERN_DATA_FIELD == null) {
      return Optional.empty();
    }
    try {
      Object value = PATTERN_DATA_FIELD.get(pattern);
      return value instanceof Optional<?> optional ? optional : Optional.empty();
    } catch (IllegalAccessException exception) {
      throw new IllegalStateException("Failed to read shaped recipe key data", exception);
    }
  }

  @Nullable
  private static Field findPatternDataField() {
    try {
      Field field = ShapedRecipePattern.class.getDeclaredField("data");
      field.setAccessible(true);
      return field;
    } catch (ReflectiveOperationException exception) {
      return null;
    }
  }
}
