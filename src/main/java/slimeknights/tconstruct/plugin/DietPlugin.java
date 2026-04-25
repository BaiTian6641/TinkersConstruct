package slimeknights.tconstruct.plugin;

import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;

import java.lang.reflect.Method;

/** Plugin to enable compat with the Diet mod */
public class DietPlugin {
  /** Call on mod construct to enable the compat */
  public static void onConstruct() {
    ModifierUtil.foodConsumer = (player, stack, hunger, saturation) -> {
      try {
        Class<?> dietCapability = Class.forName("com.illusivesoulworks.diet.common.capability.DietCapability");
        Object lazy = dietCapability.getMethod("get", net.minecraft.world.entity.player.Player.class).invoke(null, player);
        Method ifPresent = lazy.getClass().getMethod("ifPresent", java.util.function.Consumer.class);
        ifPresent.invoke(lazy, (java.util.function.Consumer<Object>) cap -> {
          try {
            Method consume = cap.getClass().getMethod("consume", stack.getClass(), int.class, float.class);
            consume.invoke(cap, stack, hunger, saturation);
          } catch (ReflectiveOperationException ignored) {
            // Diet API mismatch on this loader/version, skip compat logic.
          }
        });
      } catch (Throwable t) {
        TConstruct.LOG.debug("Diet compat unavailable on this environment", t);
      }
    };
  }
}
