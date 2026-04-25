package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;

/** Ingredient matching an item with no container item, used to ensure NBT fluid items are empty */
public class NoContainerIngredient extends NestedIngredient {
  public static final MapCodec<NoContainerIngredient> CODEC = MapCodec.assumeMapUnsafe(Codec.PASSTHROUGH.flatXmap(
      dynamic -> {
        try {
          JsonObject json = dynamic.convert(JsonOps.INSTANCE).getValue().getAsJsonObject();
          return DataResult.success(fromJson(json));
        } catch (RuntimeException ex) {
          return DataResult.error(ex::getMessage);
        }
      },
      ingredient -> DataResult.success(new Dynamic<>(JsonOps.INSTANCE, ingredient.toJson()))
  ));

  public static final StreamCodec<RegistryFriendlyByteBuf, NoContainerIngredient> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

  protected NoContainerIngredient(Ingredient nested) {
    super(nested);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && super.test(stack) && !stack.hasCraftingRemainingItem();
  }

  @Override
  public boolean isSimple() {
    return false;
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerCommons.NO_CONTAINER_INGREDIENT.get();
  }

  private JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", "tconstruct:no_container");
    JsonElement nestedElement = Ingredient.CODEC.encodeStart(JsonOps.INSTANCE, nested).getOrThrow(IllegalStateException::new);
    json.add("match", nestedElement);
    return json;
  }

  private static NoContainerIngredient fromJson(JsonObject json) {
    Ingredient ingredient;
    if (json.has("match")) {
      ingredient = Ingredient.CODEC.parse(JsonOps.INSTANCE, json.get("match")).getOrThrow(IllegalArgumentException::new);
    } else {
      JsonObject nested = json.deepCopy();
      nested.remove("type");
      ingredient = Ingredient.CODEC.parse(JsonOps.INSTANCE, nested).getOrThrow(IllegalArgumentException::new);
    }
    return new NoContainerIngredient(ingredient);
  }


  /* Static constructors */

  /** Creates an instance from the given nested ingredient */
  public static Ingredient of(Ingredient ingredient) {
    return new NoContainerIngredient(ingredient).toVanilla();
  }

  /** Creates an instance from the given items */
  public static Ingredient of(ItemLike... items) {
    return of(Ingredient.of(items));
  }

  /** Creates an instance from the given stacks */
  public static Ingredient of(ItemStack... stacks) {
    return of(Ingredient.of(stacks));
  }

  /** Creates an instance from the given tag */
  public static Ingredient of(TagKey<Item> tag) {
    return of(Ingredient.of(tag));
  }
}
