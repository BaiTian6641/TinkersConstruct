package slimeknights.tconstruct.tools.recipe.severing;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.tconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import slimeknights.tconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Supplier;

/** Builder for severing recipes that have only the base chance and looting bonus as fields */
@Setter
@Accessors(chain = true)
@RequiredArgsConstructor(staticName = "serializer")
public class SpecialSeveringRecipeBuilder extends AbstractRecipeBuilder<SpecialSeveringRecipeBuilder> {
  private final RecipeSerializer<? extends SeveringRecipe> serializer;
  private float baseChance = 0.05f;
  private float lootingBonus = 0.01f;

  /** Creates a new builder for the given serializer. */
  public static SpecialSeveringRecipeBuilder serializer(Supplier<? extends RecipeSerializer<? extends SeveringRecipe>> supplier) {
    return serializer(supplier.get());
  }

  /** Doubles the drop chances for this rare mob */
  public SpecialSeveringRecipeBuilder rareMob() {
    baseChance = 0.1f;
    lootingBonus = 0.02f;
    return this;
  }

  @SuppressWarnings("deprecation")
  @Override
  public void save(RecipeOutput consumer) {
    save(consumer, Objects.requireNonNull(BuiltInRegistries.RECIPE_SERIALIZER.getKey(serializer)));
  }

  @Override
  public void save(RecipeOutput consumer, ResourceLocation id) {
    consumer.accept(id, createRecipe(id), this.buildOptionalAdvancement(id, "severing"));
  }

  private SeveringRecipe createRecipe(ResourceLocation id) {
    if (serializer == TinkerModifiers.playerBeheadingSerializer.get()) {
      return new PlayerBeheadingRecipe(id, baseChance, lootingBonus);
    }
    if (serializer == TinkerModifiers.snowGolemBeheadingSerializer.get()) {
      return new SnowGolemBeheadingRecipe(id, baseChance, lootingBonus);
    }
    if (serializer == TinkerModifiers.mooshroomDemushroomingSerializer.get()) {
      return new MooshroomDemushroomingRecipe(id, baseChance, lootingBonus);
    }
    if (serializer == TinkerModifiers.sheepShearing.get()) {
      return new SheepShearingRecipe(id, baseChance, lootingBonus);
    }
    throw new IllegalStateException("Unsupported special severing serializer: " + serializer);
  }

  private RecordLoadable<? extends SeveringRecipe> getLoader() {
    if (serializer == TinkerModifiers.playerBeheadingSerializer.get()) {
      return PlayerBeheadingRecipe.LOADER;
    }
    if (serializer == TinkerModifiers.snowGolemBeheadingSerializer.get()) {
      return SnowGolemBeheadingRecipe.LOADER;
    }
    if (serializer == TinkerModifiers.mooshroomDemushroomingSerializer.get()) {
      return MooshroomDemushroomingRecipe.LOADER;
    }
    if (serializer == TinkerModifiers.sheepShearing.get()) {
      return SheepShearingRecipe.LOADER;
    }
    throw new IllegalStateException("Unsupported special severing serializer: " + serializer);
  }
}
