package slimeknights.tconstruct.compat.minecraft.world.item.alchemy;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.List;

/**
 * Compatibility helpers mirroring removed legacy PotionUtils methods on 1.21.1.
 */
public final class PotionUtils {
  public static final String TAG_POTION = "Potion";

  private PotionUtils() {}

  public static ItemStack setPotion(ItemStack stack, Potion potion) {
    if (potion == null) {
      return stack;
    }
    return setPotion(stack, BuiltInRegistries.POTION.wrapAsHolder(potion));
  }

  public static ItemStack setPotion(ItemStack stack, Holder<Potion> potion) {
    if (potion == null) {
      return stack;
    }
    stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
    return stack;
  }

  public static Potion getPotion(CompoundTag tag) {
    if (tag != null && tag.contains(TAG_POTION)) {
      ResourceLocation id = ResourceLocation.tryParse(tag.getString(TAG_POTION));
      if (id != null) {
        return BuiltInRegistries.POTION.get(id);
      }
    }
    return BuiltInRegistries.POTION.get(ResourceLocation.withDefaultNamespace("empty"));
  }

  public static List<MobEffectInstance> getAllEffects(CompoundTag tag) {
    return getPotion(tag).getEffects();
  }

  public static int getColor(Potion potion) {
    return new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potion)).getColor();
  }

  public static void addPotionTooltip(List<MobEffectInstance> effects, List<Component> tooltip, float durationFactor) {
    // TODO 1.21.1: migrate this callsite to consume PotionContents directly.
    if (effects.isEmpty()) {
      return;
    }
    for (MobEffectInstance effect : effects) {
      tooltip.add(effect.getEffect().value().getDisplayName());
    }
  }
}
