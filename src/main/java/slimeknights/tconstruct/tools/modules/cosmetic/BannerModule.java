package slimeknights.tconstruct.tools.modules.cosmetic;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.mantle.data.loadable.record.RecordLoadable;
import slimeknights.mantle.data.loadable.record.SingletonLoader;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierHooks;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import slimeknights.tconstruct.library.modifiers.hook.display.TooltipModifierHook;
import slimeknights.tconstruct.library.modifiers.modules.ModifierModule;
import slimeknights.tconstruct.library.module.HookProvider;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.utils.TinkerTooltipFlags;
import slimeknights.tconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Module for banner pattern tooltips */
public enum BannerModule implements ModifierModule, DisplayNameModifierHook, TooltipModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<BannerModule>defaultHooks(ModifierHooks.DISPLAY_NAME, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<BannerModule> LOADER = new SingletonLoader<>(INSTANCE);
  /** Key for a dye color, stored as its ID */
  public static final String KEY_DYE = "dye";
  /** Key for a pattern color, as a 24 bit integer */
  public static final String KEY_COLOR = "color";
  /** Key for a pattern hash, from {@link BannerPattern#getHashname()} */
  public static final String KEY_PATTERN = "pattern";
  /** Tooltip key saying hold shift for patterns */
  private static final Component HOLD_SHIFT = TConstruct.makeTranslation("modifier", "banner.hold_shift").withStyle(ChatFormatting.GRAY);
  /** Mapping of legacy banner hash codes to modern registry IDs. */
  private static final Map<String,ResourceLocation> LEGACY_PATTERN_IDS = createLegacyPatternIds();

  @Override
  public RecordLoadable<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name, @Nullable RegistryAccess access) {
    // color the tooltip the color of the first pattern
    ListTag patterns = tool.getPersistentData().getList(patternKey(entry.getId()), ListTag.TAG_COMPOUND);
    if (!patterns.isEmpty()) {
      return name.copy().withStyle(name.getStyle().withColor(DyeColor.byId(patterns.getCompound(0).getInt(KEY_DYE)).getTextColor()));
    }
    return name;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    // add all patterns in a tinker station when holding
    if (tooltipFlag == TinkerTooltipFlags.TINKER_STATION) {
      if (tooltipKey == TooltipKey.SHIFT) {
        ListTag patterns = tool.getPersistentData().getList(patternKey(modifier.getId()), ListTag.TAG_COMPOUND);
        for (int i = 0; i < patterns.size(); i++) {
          CompoundTag tag = patterns.getCompound(i);
          DyeColor dye = DyeColor.byId(tag.getInt(KEY_DYE));
          Holder<BannerPattern> holder = getPattern(tag.getString(KEY_PATTERN));
          if (holder != null) {
            // note that Forge is dumb in BannerItem with their patch - mojang already adds the mod ID to the tooltip key
            holder.unwrapKey().ifPresent(key ->
              tooltip.add(Component.translatable("block.minecraft.banner." + key.location().toShortLanguageKey() + '.' + dye.getName()).withStyle(ChatFormatting.GRAY)));

          }
        }
      } else {
        tooltip.add(HOLD_SHIFT);
      }
    }
  }

  /** Gets the key for the cache used in the model */
  public static ResourceLocation cacheKey(ModifierId modifier) {
    return modifier.withSuffix("_cache");
  }

  /** Gets the key for the pattern list in NBT */
  public static ResourceLocation patternKey(ModifierId modifier) {
    return modifier.withSuffix("_patterns");
  }

  /** Resolves a stored pattern string to the current banner pattern holder. */
  @Nullable
  public static Holder<BannerPattern> getPattern(String pattern) {
    ResourceLocation id = ResourceLocation.tryParse(pattern);
    if (id == null) {
      id = LEGACY_PATTERN_IDS.get(pattern);
    }
    return null;
  }

  /** Gets the normalized set of banner pattern IDs used by the legacy banner importer. */
  public static Collection<ResourceLocation> getKnownPatternIds() {
    return LEGACY_PATTERN_IDS.values();
  }

  /** Copies the given list of patterns from banner format to the tool's NBT */
  public static void copyPatterns(ModDataNBT data, ModifierId id, DyeColor dye, ListTag banner) {
    int baseColor = Util.getColor(dye);
    ListTag patterns = new ListTag();

    // add in the base pattern, it only exists on shields and we copy from banners
    CompoundTag basePattern = new CompoundTag();
    basePattern.putString(KEY_PATTERN, BannerPatterns.BASE.location().toString());
    basePattern.putInt(KEY_DYE, dye.getId());
    basePattern.putInt(KEY_COLOR, baseColor);
    patterns.add(basePattern);

    // need a cache key, but it's just going to get hashed anyway, so store its hash
    int hashCode = baseColor;

    // add in all other patterns
    for (int i = 0; i < banner.size(); i++) {
      CompoundTag original = banner.getCompound(i);
      CompoundTag copy = new CompoundTag();
      // translate legacy banner hash names to persistent registry IDs
      String pattern = original.getString("Pattern");
      ResourceLocation patternId = LEGACY_PATTERN_IDS.get(pattern);
      if (patternId == null) {
        patternId = ResourceLocation.tryParse(pattern);
      }
      if (patternId == null) {
        continue;
      }
      copy.putString(KEY_PATTERN, patternId.toString());
      // convert the color from a dye color to an integer
      dye = DyeColor.byId(original.getInt("Color"));
      int color = Util.getColor(dye);
      copy.putInt(KEY_DYE, dye.getId()); // dye for the tooltip
      copy.putInt(KEY_COLOR, color); // color for the model
      // add the values
      patterns.add(copy);
      // update the hash code with the new information
      hashCode = 31 * (31 * hashCode + color) + pattern.hashCode();
    }

    // add to tool NBT
    data.put(patternKey(id), patterns);
    data.putInt(cacheKey(id), hashCode);
  }

  /** Builds the mapping from legacy short codes to modern banner pattern IDs. */
  private static Map<String,ResourceLocation> createLegacyPatternIds() {
    Map<String,ResourceLocation> ids = new HashMap<>();
    ids.put("b", BannerPatterns.BASE.location());
    ids.put("bl", BannerPatterns.SQUARE_BOTTOM_LEFT.location());
    ids.put("br", BannerPatterns.SQUARE_BOTTOM_RIGHT.location());
    ids.put("tl", BannerPatterns.SQUARE_TOP_LEFT.location());
    ids.put("tr", BannerPatterns.SQUARE_TOP_RIGHT.location());
    ids.put("bs", BannerPatterns.STRIPE_BOTTOM.location());
    ids.put("ts", BannerPatterns.STRIPE_TOP.location());
    ids.put("ls", BannerPatterns.STRIPE_LEFT.location());
    ids.put("rs", BannerPatterns.STRIPE_RIGHT.location());
    ids.put("cs", BannerPatterns.STRIPE_CENTER.location());
    ids.put("ms", BannerPatterns.STRIPE_MIDDLE.location());
    ids.put("drs", BannerPatterns.STRIPE_DOWNRIGHT.location());
    ids.put("dls", BannerPatterns.STRIPE_DOWNLEFT.location());
    ids.put("ss", BannerPatterns.STRIPE_SMALL.location());
    ids.put("cr", BannerPatterns.CROSS.location());
    ids.put("sc", BannerPatterns.STRAIGHT_CROSS.location());
    ids.put("bt", BannerPatterns.TRIANGLE_BOTTOM.location());
    ids.put("tt", BannerPatterns.TRIANGLE_TOP.location());
    ids.put("bts", BannerPatterns.TRIANGLES_BOTTOM.location());
    ids.put("tts", BannerPatterns.TRIANGLES_TOP.location());
    ids.put("ld", BannerPatterns.DIAGONAL_LEFT.location());
    ids.put("rd", BannerPatterns.DIAGONAL_RIGHT.location());
    ids.put("lud", BannerPatterns.DIAGONAL_LEFT_MIRROR.location());
    ids.put("rud", BannerPatterns.DIAGONAL_RIGHT_MIRROR.location());
    ids.put("mc", BannerPatterns.CIRCLE_MIDDLE.location());
    ids.put("mr", BannerPatterns.RHOMBUS_MIDDLE.location());
    ids.put("vh", BannerPatterns.HALF_VERTICAL.location());
    ids.put("hh", BannerPatterns.HALF_HORIZONTAL.location());
    ids.put("vhr", BannerPatterns.HALF_VERTICAL_MIRROR.location());
    ids.put("hhb", BannerPatterns.HALF_HORIZONTAL_MIRROR.location());
    ids.put("bo", BannerPatterns.BORDER.location());
    ids.put("cbo", BannerPatterns.CURLY_BORDER.location());
    ids.put("gra", BannerPatterns.GRADIENT.location());
    ids.put("gru", BannerPatterns.GRADIENT_UP.location());
    ids.put("bri", BannerPatterns.BRICKS.location());
    ids.put("glb", BannerPatterns.GLOBE.location());
    ids.put("cre", BannerPatterns.CREEPER.location());
    ids.put("sku", BannerPatterns.SKULL.location());
    ids.put("flo", BannerPatterns.FLOWER.location());
    ids.put("moj", BannerPatterns.MOJANG.location());
    ids.put("pig", BannerPatterns.PIGLIN.location());
    ids.put("flw", BannerPatterns.FLOW.location());
    ids.put("gus", BannerPatterns.GUSTER.location());
    return Map.copyOf(ids);
  }
}
