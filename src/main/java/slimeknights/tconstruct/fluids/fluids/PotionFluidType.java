package slimeknights.tconstruct.fluids.fluids;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import slimeknights.mantle.fluid.texture.ClientTextureFluidType;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.tconstruct.fluids.TinkerFluids;

import java.util.function.Consumer;

public class PotionFluidType extends FluidType {
  public PotionFluidType(Properties properties) {
    super(properties);
  }

  @Override
  public String getDescriptionId(FluidStack stack) {
    return Potion.getName(potionContents(stack).potion(), "item.minecraft.potion.effect.");
  }

  @Override
  public ItemStack getBucket(FluidStack fluidStack) {
    ItemStack itemStack = new ItemStack(fluidStack.getFluid().getBucket());
    PotionContents contents = potionContents(fluidStack);
    if (!contents.equals(PotionContents.EMPTY)) {
      itemStack.set(DataComponents.POTION_CONTENTS, contents);
    }
    return itemStack;
  }

  @Override
  public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
    consumer.accept(new ClientTextureFluidType(this) {
      @Override
      public int getTintColor(FluidStack stack) {
        PotionContents contents = potionContents(stack);
        if (contents.equals(PotionContents.EMPTY)) {
          return getTintColor();
        }
        return contents.getColor() | 0xFF000000;
      }
    });
  }

  private static PotionContents potionContents(FluidStack stack) {
    return stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
  }

  /** Creates a fluid stack for the given potion */
  public static FluidStack potionFluid(ResourceKey<Potion> potion, int size) {
    Holder<Potion> holder = BuiltInRegistries.POTION.getHolder(potion).orElseThrow();
    return potionFluid(holder, size);
  }

  /** Creates a fluid stack for the given potion */
  public static FluidStack potionFluid(Potion potion, int size) {
    return potionFluid(BuiltInRegistries.POTION.wrapAsHolder(potion), size);
  }

  private static FluidStack potionFluid(Holder<Potion> potion, int size) {
    FluidStack stack = new FluidStack(TinkerFluids.potion.get(), size);
    stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
    return stack;
  }

  /** Creates a fluid output for the given potion */
  public static FluidOutput potionResult(Potion potion, int size) {
    return FluidOutput.fromStack(potionFluid(potion, size));
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(ResourceKey<Potion> potion) {
    Holder<Potion> holder = BuiltInRegistries.POTION.getHolder(potion).orElseThrow();
    return potionBucket(holder);
  }

  /** Creates a potion bucket for the given potion */
  public static ItemStack potionBucket(Potion potion) {
    return potionBucket(BuiltInRegistries.POTION.wrapAsHolder(potion));
  }

  private static ItemStack potionBucket(Holder<Potion> potion) {
    ItemStack stack = new ItemStack(TinkerFluids.potion);
    stack.set(DataComponents.POTION_CONTENTS, new PotionContents(potion));
    return stack;
  }
}
