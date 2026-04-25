package slimeknights.tconstruct.library.client.item;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import slimeknights.tconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;

import javax.annotation.Nullable;

/** Client extensions for modifiable crossbows. Adds in the arm pose when charged. */
public class ModifiableCrossbowClientExtension extends ModifiableItemClientExtension {
  public static final ModifiableCrossbowClientExtension INSTANCE = new ModifiableCrossbowClientExtension();

  protected ModifiableCrossbowClientExtension() {}

  @Nullable
  @Override
  public ArmPose getArmPose(LivingEntity living, InteractionHand hand, ItemStack stack) {
    if (!living.swinging) {
      CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
      CompoundTag tag = customData != null ? customData.copyTag() : null;
      // must have ammo in persistent data
      if (tag != null && tag.getCompound(ToolStack.TAG_PERSISTENT_MOD_DATA).contains(ModifiableCrossbowItem.KEY_CROSSBOW_AMMO.toString(), CompoundTag.TAG_COMPOUND)) {
        return ArmPose.CROSSBOW_HOLD;
      }
    }
    return ArmPose.ITEM;
  }
}
