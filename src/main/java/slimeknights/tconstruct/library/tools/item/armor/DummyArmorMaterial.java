package slimeknights.tconstruct.library.tools.item.armor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import slimeknights.mantle.registration.object.IdAwareObject;

/** Armor material that returns 0 except for name, since we bypass all the usages */
@RequiredArgsConstructor
@Getter
public class DummyArmorMaterial implements IdAwareObject {
  private final ResourceLocation id;
  private final SoundEvent equipSound;
  private ArmorMaterial armorMaterial;

  public ArmorMaterial armorMaterial() {
    if (armorMaterial == null) {
      EnumMap<ArmorItem.Type,Integer> defense = new EnumMap<>(ArmorItem.Type.class);
      for (ArmorItem.Type type : ArmorItem.Type.values()) {
        defense.put(type, 0);
      }
      armorMaterial = new ArmorMaterial(
        defense,
        0,
        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(equipSound),
        () -> Ingredient.EMPTY,
        List.of(new ArmorMaterial.Layer(id)),
        0,
        0
      );
    }
    return armorMaterial;
  }
}
