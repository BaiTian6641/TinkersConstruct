package slimeknights.tconstruct.common.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
// NOTE: ForgeRegistries/IForgeRegistry removed in NeoForge 1.21.1 - registry API refactored
// import net.neoforged.neoforge.registries.NeoForgeRegistries;
// import net.neoforged.neoforge.registries.NeoForgeRegistry;
// import slimeknights.tconstruct.compat.neoforge.registries.IForgeRegistry;
import slimeknights.tconstruct.common.TinkerEffect;

import java.util.Objects;
import java.util.function.Supplier;

/** Handles creating fake registry entries to datagen entries based on other mods */
public class FakeRegistryEntry {
  /** Creates a dummy registry entry */
  // NOTE: IForgeRegistry API removed in NeoForge 1.21.1
  // Registry freezing/unfreezing no longer works - this entire approach is deprecated
  /*
  @SuppressWarnings("UnstableApiUsage")
  private static <T> T getOrCreate(IForgeRegistry<T> registry, ResourceLocation id, Supplier<T> constructor) {
    if (!registry.containsKey(id)) {
      ((ForgeRegistry<T>)registry).unfreeze();
      T value = constructor.get();
      registry.register(id, value);
      return value;
    }
    return Objects.requireNonNull(registry.getValue(id));
  }
  */

  /** Gets or creates a fake block with the given ID */
  // NOTE: Disabled - getOrCreate requires IForgeRegistry which no longer exists in NeoForge 1.21.1
  /*
  public static Block block(ResourceLocation id) {
    return getOrCreate(NeoForgeRegistries.BLOCKS, id, () -> new Block(BlockBehaviour.Properties.of()));
  }

  */
  public static Block block(ResourceLocation id) {
    throw new UnsupportedOperationException("FakeRegistryEntry.block() disabled in NeoForge 1.21.1 - registry API changed");
  }

  /** Gets or creates a fake item with the given ID */
  // NOTE: Disabled - getOrCreate requires IForgeRegistry which no longer exists in NeoForge 1.21.1
  /*
  public static Item item(ResourceLocation id) {
    return getOrCreate(NeoForgeRegistries.ITEMS, id, () -> new Item(new Item.Properties()));
  }
  */
  public static Item item(ResourceLocation id) {
    throw new UnsupportedOperationException("FakeRegistryEntry.item() disabled in NeoForge 1.21.1 - registry API changed");
  }

  /** Gets or creates a fake mob effect with the given ID */
  // NOTE: Disabled - getOrCreate requires IForgeRegistry which no longer exists in NeoForge 1.21.1
  /*
  public static MobEffect effect(ResourceLocation id) {
    return getOrCreate(NeoForgeRegistries.MOB_EFFECTS, id, () -> new TinkerEffect(MobEffectCategory.NEUTRAL, false));
  }
  */
  public static MobEffect effect(ResourceLocation id) {
    throw new UnsupportedOperationException("FakeRegistryEntry.effect() disabled in NeoForge 1.21.1 - registry API changed");
  }

  /** Gets or creates a fake entity with the given ID */
  // NOTE: Disabled - getOrCreate requires IForgeRegistry which no longer exists in NeoForge 1.21.1
  /*
  public static <T extends Entity> EntityType<?> entity(ResourceLocation id) {
    return getOrCreate(NeoForgeRegistries.ENTITY_TYPES, id, () ->
      EntityType.Builder.of((type, level) -> {
        throw new UnsupportedOperationException("Cannot create instance of fake entity");
      }, MobCategory.MISC).build(id.toString()));
  }
  */
  public static <T extends Entity> EntityType<?> entity(ResourceLocation id) {
    throw new UnsupportedOperationException("FakeRegistryEntry.entity() disabled in NeoForge 1.21.1 - registry API changed");
  }
}
