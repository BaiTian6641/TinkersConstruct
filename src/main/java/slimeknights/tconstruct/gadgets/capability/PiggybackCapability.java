package slimeknights.tconstruct.gadgets.capability;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import slimeknights.tconstruct.compat.neoforge.fml.javafmlmod.FMLJavaModLoadingContext;
import slimeknights.tconstruct.TConstruct;

/** Capability logic */
public class PiggybackCapability {
  public static final EntityCapability<PiggybackHandler, Void> PIGGYBACK =
    EntityCapability.createVoid(TConstruct.getResource("piggyback"), PiggybackHandler.class);

  private PiggybackCapability() {}

  /** Registers this capability listener on the mod event bus. */
  public static void register() {
    FMLJavaModLoadingContext.get().getModEventBus().addListener(PiggybackCapability::registerCapabilities);
  }

  /** Registers the capability with the NeoForge capability event */
  public static void registerCapabilities(RegisterCapabilitiesEvent event) {
    event.registerEntity(PIGGYBACK, EntityType.PLAYER, (player, context) -> new PiggybackHandler(player));
  }
}
