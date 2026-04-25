package slimeknights.tconstruct.fluids;

import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.fml.common.EventBusSubscriber;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.fluids.item.PotionBucketItem;
import slimeknights.tconstruct.fluids.util.ConstantFluidContainerWrapper;
import slimeknights.tconstruct.library.recipe.FluidValues;
import slimeknights.tconstruct.shared.block.SlimeType;

/**
 * Event subscriber for modifier events
 * Note the way the subscribers are set up, technically works on anything that has the tic_modifiers tag
 */
@SuppressWarnings("unused")
@EventBusSubscriber(modid = TConstruct.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class FluidEvents {
  @SubscribeEvent
  static void onFurnaceFuel(FurnaceFuelBurnTimeEvent event) {
    if (event.getItemStack().getItem() == TinkerFluids.blazingBlood.asItem()) {
      // 150% efficiency compared to lava bucket, compare to casting blaze rods, which cast into 120%
      event.setBurnTime(30000);
    }
  }

  @EventBusSubscriber(modid = TConstruct.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
  public static class ModCapabilities {
    @SubscribeEvent
    static void registerCapabilities(RegisterCapabilitiesEvent event) {
      event.registerItem(
        Capabilities.FluidHandler.ITEM,
        (stack, context) -> new ConstantFluidContainerWrapper(new FluidStack(TinkerFluids.powderedSnow.get(), FluidType.BUCKET_VOLUME), stack, Items.BUCKET.getDefaultInstance()),
        Items.POWDER_SNOW_BUCKET);

      event.registerItem(
        Capabilities.FluidHandler.ITEM,
        (stack, context) -> new ConstantFluidContainerWrapper(new FluidStack(TinkerFluids.venom.get(), FluidValues.BOTTLE), stack),
        TinkerFluids.venomBottle.get());

      event.registerItem(
        Capabilities.FluidHandler.ITEM,
        (stack, context) -> new ConstantFluidContainerWrapper(new FluidStack(TinkerFluids.magma.get(), FluidValues.BOTTLE), stack),
        TinkerFluids.magmaBottle.get());

      for (SlimeType type : SlimeType.values()) {
        event.registerItem(
          Capabilities.FluidHandler.ITEM,
          (stack, context) -> new ConstantFluidContainerWrapper(new FluidStack(TinkerFluids.slime.get(type), FluidValues.BOTTLE), stack),
          TinkerFluids.slimeBottle.get(type));
      }

      event.registerItem(
        Capabilities.FluidHandler.ITEM,
        (stack, context) -> new PotionBucketItem.PotionBucketWrapper(stack),
        TinkerFluids.potion.asItem());
    }
  }

}
