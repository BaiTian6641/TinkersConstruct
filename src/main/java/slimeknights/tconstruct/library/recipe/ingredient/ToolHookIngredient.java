package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.definition.module.ToolHooks;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.tools.TinkerTools;

import javax.annotation.Nullable;
import java.util.stream.Stream;

/** Ingredient that only matches tools with a specific hook */
public class ToolHookIngredient implements net.neoforged.neoforge.common.crafting.ICustomIngredient {
  public static final MapCodec<ToolHookIngredient> CODEC = MapCodec.assumeMapUnsafe(Codec.PASSTHROUGH.flatXmap(
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

  public static final StreamCodec<RegistryFriendlyByteBuf, ToolHookIngredient> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

  private final TagKey<Item> tag;
  private final ModuleHook<?> hook;

  protected ToolHookIngredient(TagKey<Item> tag, ModuleHook<?> hook) {
    this.tag = tag;
    this.hook = hook;
  }

  public static Ingredient of(TagKey<Item> tag, ModuleHook<?> hook) {
    return new ToolHookIngredient(tag, hook).toVanilla();
  }

  public static Ingredient of(ModuleHook<?> hook) {
    return of(TinkerTags.Items.MODIFIABLE, hook);
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && stack.is(tag) && stack.getItem() instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook);
  }

  @Override
  public Stream<ItemStack> getItems() {
    return RegistryHelper.getTagValueStream(BuiltInRegistries.ITEM, tag)
                                 .filter(item -> item instanceof IModifiable modifiable && modifiable.getToolDefinition().getData().getHooks().hasHook(hook))
                                 .map(ItemStack::new);
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerTools.TOOL_HOOK_INGREDIENT.get();
  }

  private JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("tag", tag.location().toString());
    json.addProperty("hook", hook.getId().toString());
    return json;
  }

  private static ToolHookIngredient fromJson(JsonObject json) {
    return new ToolHookIngredient(
      Loadables.ITEM_TAG.getOrDefault(json, "tag", TinkerTags.Items.MODIFIABLE),
      ToolHooks.LOADER.getIfPresent(json, "hook")
    );
  }
}
