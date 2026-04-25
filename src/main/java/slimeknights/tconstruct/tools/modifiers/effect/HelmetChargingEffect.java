package slimeknights.tconstruct.tools.modifiers.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientMobEffectExtensions;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.function.Consumer;

/** Effect for rendering the charge up when you start using a helmet */
public class HelmetChargingEffect extends MobEffect {
  public HelmetChargingEffect() {
    super(MobEffectCategory.NEUTRAL, -1);
  }

  @Override
  public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
    consumer.accept(new IClientMobEffectExtensions() {
      private static final ResourceLocation BAR_KEY = TConstruct.getResource("helmet_charging_bar");
      private static final ResourceLocation MOB_EFFECT_ATLAS = ResourceLocation.withDefaultNamespace("textures/atlas/mob_effects.png");
      private final Minecraft mc = Minecraft.getInstance();

      @Override
      public boolean isVisibleInInventory(MobEffectInstance instance) {
        return false;
      }

      @Override
      public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics graphics, int x, int y, float z, float alpha) {
        // start by drawing the original texture, skip alpha
        MobEffectTextureManager textures = mc.getMobEffectTextures();
        TextureAtlasSprite sprite = textures.get(instance.getEffect());
        graphics.blit(x + 3, y + 3, 0, 18, 18, sprite);

        // if the helmet has a drawtime, render the extra bar
        if (mc.player != null) {
          ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
          if (!helmet.isEmpty()) {
            // if we have a drawtime, draw the bar overlaying the icon
            int duration = instance.getDuration();
            int drawtime = ModifierUtil.getPersistentInt(helmet, GeneralInteractionModifierHook.KEY_DRAWTIME, 0);
            int dd = drawtime + 20;
            if (drawtime > 0 && duration < dd) {
              ModelManager modelManager = mc.getModelManager();
              sprite = modelManager.getAtlas(MOB_EFFECT_ATLAS).getSprite(BAR_KEY);
              int height;
              if (duration < 20) {
                height = 18;
              } else {
                height = (dd - duration) * 18 / drawtime;
              }
              float v0 = sprite.getV0(), v1 = sprite.getV1();
              int yOffset = (18 - height);
              graphics.blit(x + 3, y + 3 + yOffset, 0, 18, height, sprite, sprite.getU0(), sprite.getU1(), v0 + (v1 - v0) * yOffset / 18f, v1);
            }
          }
        }
        return true;
      }
    });
  }


  /* Helpers */

  /** Starts using the helmet with the charge time rendering */
  public static int startUsingHelmet(IToolStackView tool, LivingEntity living, float speedFactor) {
    int time = GeneralInteractionModifierHook.startDrawing(tool, living, speedFactor);
    living.addEffect(new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.wrapAsHolder(TinkerModifiers.helmetCharging.get()), time + 20, 0, true, false, true));
    return time;
  }
}
