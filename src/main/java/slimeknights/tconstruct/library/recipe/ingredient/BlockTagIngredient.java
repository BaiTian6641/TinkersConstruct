package slimeknights.tconstruct.library.recipe.ingredient;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.crafting.IngredientType;
import slimeknights.mantle.data.loadable.Loadables;
import slimeknights.mantle.util.RegistryHelper;
import slimeknights.tconstruct.shared.TinkerCommons;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Item ingredient matching items with a block form in the given tag */
public class BlockTagIngredient implements net.neoforged.neoforge.common.crafting.ICustomIngredient {
  public static final MapCodec<BlockTagIngredient> CODEC = MapCodec.assumeMapUnsafe(Codec.PASSTHROUGH.flatXmap(
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

  public static final StreamCodec<RegistryFriendlyByteBuf, BlockTagIngredient> STREAM_CODEC = ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

  private final TagKey<Block> tag;

  public BlockTagIngredient(TagKey<Block> tag) {
    this.tag = tag;
  }

  public static Ingredient of(TagKey<Block> tag) {
    return new BlockTagIngredient(tag).toVanilla();
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    return stack != null && getMatchingItems().contains(stack.getItem());
  }

  @Override
  public Stream<ItemStack> getItems() {
    return getMatchingItems().stream().map(ItemStack::new);
  }

  @Override
  public boolean isSimple() {
    return true;
  }

  @Override
  public IngredientType<?> getType() {
    return TinkerCommons.BLOCK_TAG_INGREDIENT.get();
  }

  /** Gets the ordered matching items set */
  private Set<Item> getMatchingItems() {
    return RegistryHelper.getTagValueStream(BuiltInRegistries.BLOCK, tag)
                         .map(Block::asItem)
                         .filter(item -> item != Items.AIR)
                         .collect(Collectors.toCollection(LinkedHashSet::new));
  }

  private static BlockTagIngredient fromJson(JsonObject json) {
    return new BlockTagIngredient(Loadables.BLOCK_TAG.getIfPresent(json, "tag"));
  }

  private JsonObject toJson() {
    JsonObject json = new JsonObject();
    json.add("tag", Loadables.BLOCK_TAG.serialize(tag));
    return json;
  }
}
