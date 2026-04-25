package slimeknights.tconstruct.library.tools.capability;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.util.Lazy;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/** Capability registry and static dispatch for tool stacks. */
public class ToolCapabilityProvider {
  private static final List<BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider>> PROVIDER_CONSTRUCTORS = new ArrayList<>();

  private ToolCapabilityProvider() {}

  /**
   * Returns the capability value for the given stack and capability, or {@code null} if unavailable.
   */
  @Nullable
  public static <T> T getCapability(ItemStack stack, ItemCapability<T, Void> cap) {
    Lazy<ToolStack> toolLazy = Lazy.of(() -> ToolStack.from(stack));
    ToolStack toolStack = toolLazy.get();
    toolStack.refreshTag(stack);
    for (BiFunction<ItemStack, Supplier<? extends IToolStackView>, IToolCapabilityProvider> con : PROVIDER_CONSTRUCTORS) {
      IToolCapabilityProvider provider = con.apply(stack, toolLazy);
      if (provider != null) {
        provider.clearCache();
        T result = provider.getCapability(toolStack, cap);
        if (result != null) return result;
      }
    }
    return null;
  }

  /**
   * Registers item capabilities for the given items using the provider dispatch.
   * Call during {@code RegisterCapabilitiesEvent} for each relevant {@link ItemCapability}.
   */
  public static <T> void registerItemCap(RegisterCapabilitiesEvent event, ItemCapability<T, Void> cap, ItemLike... items) {
    event.registerItem(cap, (stack, ctx) -> getCapability(stack, cap), items);
  }

  /**
   * Registers a tool capability provider constructor. Called at mod setup time.
   */
  public static void register(BiFunction<ItemStack,Supplier<? extends IToolStackView>,IToolCapabilityProvider> constructor) {
    PROVIDER_CONSTRUCTORS.add(constructor);
  }

  /** Interface to dispatch a capability value on a tool item. */
  @FunctionalInterface
  public interface IToolCapabilityProvider {
    /** Returns the capability value for the tool, or {@code null} if not available. */
    @Nullable <T> T getCapability(IToolStackView tool, ItemCapability<T, Void> cap);

    /** Called to clear any cached state before a new capability query. */
    default void clearCache() {}
  }
}
