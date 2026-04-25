package slimeknights.tconstruct.library;

import net.minecraft.world.item.ItemDisplayContext;

/** Custom transform types used for tinkers item rendering */
public class TinkerItemDisplays {
  private TinkerItemDisplays() {}

  public static void init() {
  }

  private static ItemDisplayContext get(String name) {
    return ItemDisplayContext.valueOf("TCONSTRUCT_" + name);
  }

  /** Used by the melter and smeltery for display of items its melting */
  public static ItemDisplayContext MELTER = get("MELTER");
  /** Used by the part builder, crafting station, tinkers station, and tinker anvil */
  public static ItemDisplayContext TABLE = get("TABLE");
  /** Used by the casting table for item rendering */
  public static ItemDisplayContext CASTING_TABLE = get("CASTING_TABLE");
  /** Used by the casting basin for item rendering */
  public static ItemDisplayContext CASTING_BASIN = get("CASTING_BASIN");
  /** Used by the fluid cannon for display of the item in front */
  public static ItemDisplayContext FLUID_CANNON = get("FLUID_CANNON");
  /** Used by throwing to allow adjusting the tool position */
  public static ItemDisplayContext THROWN = get("THROWN");
}
