package slimeknights.tconstruct.compat.neoforge.fml.javafmlmod;

import net.neoforged.bus.api.IEventBus;
import slimeknights.tconstruct.TConstruct;

/**
 * Compatibility bridge for legacy FMLJavaModLoadingContext usage.
 * Routes to the constructor-provided mod event bus captured by TConstruct.
 */
public final class FMLJavaModLoadingContext {
  private static final FMLJavaModLoadingContext INSTANCE = new FMLJavaModLoadingContext();

  private FMLJavaModLoadingContext() {}

  public static FMLJavaModLoadingContext get() {
    return INSTANCE;
  }

  public IEventBus getModEventBus() {
    return TConstruct.getModEventBus();
  }
}
