package slimeknights.tconstruct.plugin;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.TConstruct;

/** Event handlers to run when Immersive Engineering is present */
public class ImmersiveEngineeringPlugin {
  @SubscribeEvent
  public void commonSetup(FMLCommonSetupEvent event) {
    // BLOCKER NOTE: the only readily available IE artifact on this branch is Forge 1.20.1.
    // Its compat classes still initialize against net.minecraftforge.* registries, so keep this
    // isolated until a NeoForge 1.21.1 IE API surface exists to port against.
    event.enqueueWork(() -> TConstruct.LOG.warn("Immersive Engineering compat remains disabled on NeoForge 1.21.1: the available IE API artifact is still Forge 1.20.1-linked"));
  }
}
