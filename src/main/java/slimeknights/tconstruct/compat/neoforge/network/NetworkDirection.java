package slimeknights.tconstruct.compat.neoforge.network;

/**
 * Compatibility enum for legacy packet registration calls.
 * TConstruct networking is being migrated to payload-based channels.
 */
public enum NetworkDirection {
  PLAY_TO_CLIENT,
  PLAY_TO_SERVER
}
