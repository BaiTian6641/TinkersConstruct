package slimeknights.tconstruct.tools.modifiers.loot;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.BinomialWithBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.Formula;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.OreDrops;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount.UniformBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import slimeknights.tconstruct.library.modifiers.ModifierId;
import slimeknights.tconstruct.library.tools.helper.ModifierUtil;
import slimeknights.tconstruct.tools.TinkerModifiers;

import java.util.List;
import java.util.Set;

/** Boosts drop rates based on modifier level */
public class ModifierBonusLootFunction extends LootItemConditionalFunction {
  private static final ResourceLocation FORMULA_ORE_DROPS = ResourceLocation.withDefaultNamespace("ore_drops");
  private static final ResourceLocation FORMULA_UNIFORM = ResourceLocation.withDefaultNamespace("uniform_bonus_count");
  private static final ResourceLocation FORMULA_BINOMIAL = ResourceLocation.withDefaultNamespace("binomial_with_bonus_count");
  public static final MapCodec<ModifierBonusLootFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
    LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(function -> function.predicates),
    ResourceLocation.CODEC.fieldOf("modifier").forGetter(function -> function.modifier.getLocation()),
    ResourceLocation.CODEC.fieldOf("formula").forGetter(function -> formulaId(function.formula)),
    Codec.PASSTHROUGH.optionalFieldOf("parameters", new Dynamic<>(JsonOps.INSTANCE, new JsonObject())).forGetter(function -> new Dynamic<>(JsonOps.INSTANCE, formulaParameters(function.formula))),
    Codec.BOOL.optionalFieldOf("include_base", true).forGetter(function -> function.includeBase)
  ).apply(instance, (conditions, modifier, formula, parameters, includeBase) ->
    new ModifierBonusLootFunction(conditions, new ModifierId(modifier), decodeFormula(formula, toJson(parameters)), includeBase)));

  /** Modifier ID to use for multiplier bonus */
  private final ModifierId modifier;
  /** Formula to apply */
  private final Formula formula;
  /** If true, considers level 1 as bonus, if false considers level 1 as no bonus */
  private final boolean includeBase;

  protected ModifierBonusLootFunction(List<LootItemCondition> conditions, ModifierId modifier, Formula formula, boolean includeBase) {
    super(conditions);
    this.modifier = modifier;
    this.formula = formula;
    this.includeBase = includeBase;
  }

  /** Creates a generic builder */
  public static Builder<?> builder(ModifierId modifier, Formula formula, boolean includeBase) {
    return simpleBuilder(conditions -> new ModifierBonusLootFunction(conditions, modifier, formula, includeBase));
  }

  /** Creates a builder for the binomial with bonus formula */
  public static Builder<?> binomialWithBonusCount(ModifierId modifier, float probability, int extra, boolean includeBase) {
    return builder(modifier, new BinomialWithBonusCount(extra, probability), includeBase);
  }

  /** Creates a builder for the ore drops formula */
  public static Builder<?> oreDrops(ModifierId modifier, boolean includeBase) {
    return builder(modifier, new OreDrops(), includeBase);
  }

  /** Creates a builder for the uniform bonus count */
  public static Builder<?> uniformBonusCount(ModifierId modifier, int bonusMultiplier, boolean includeBase) {
    return builder(modifier, new UniformBonusCount(bonusMultiplier), includeBase);
  }

  @Override
  public LootItemFunctionType getType() {
    return TinkerModifiers.modifierBonusFunction.get();
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootContextParams.TOOL);
  }

  @Override
  protected ItemStack run(ItemStack stack, LootContext context) {
    int level = ModifierUtil.getModifierLevel(context.getParam(LootContextParams.TOOL), modifier);
    if (!includeBase) {
      level--;
    }
    if (level > 0) {
      stack.setCount(formula.calculateNewCount(context.getRandom(), stack.getCount(), level));
    }
    return stack;
  }

  private static JsonElement toJson(Dynamic<?> dynamic) {
    return (JsonElement)dynamic.convert(JsonOps.INSTANCE).getValue();
  }

  private static ResourceLocation formulaId(Formula formula) {
    if (formula instanceof OreDrops) {
      return FORMULA_ORE_DROPS;
    }
    if (formula instanceof UniformBonusCount) {
      return FORMULA_UNIFORM;
    }
    if (formula instanceof BinomialWithBonusCount) {
      return FORMULA_BINOMIAL;
    }
    throw new IllegalArgumentException("Unsupported bonus formula type: " + formula.getClass().getName());
  }

  private static JsonElement formulaParameters(Formula formula) {
    JsonObject parameters = new JsonObject();
    if (formula instanceof UniformBonusCount uniform) {
      parameters.addProperty("bonusMultiplier", uniform.bonusMultiplier());
    } else if (formula instanceof BinomialWithBonusCount binomial) {
      parameters.addProperty("extra", binomial.extraRounds());
      parameters.addProperty("probability", binomial.probability());
    }
    return parameters;
  }

  private static Formula decodeFormula(ResourceLocation id, JsonElement parametersElement) {
    JsonObject parameters = parametersElement != null && parametersElement.isJsonObject() ? parametersElement.getAsJsonObject() : new JsonObject();
    if (FORMULA_ORE_DROPS.equals(id)) {
      return new OreDrops();
    }
    if (FORMULA_UNIFORM.equals(id)) {
      return new UniformBonusCount(GsonHelper.getAsInt(parameters, "bonusMultiplier"));
    }
    if (FORMULA_BINOMIAL.equals(id)) {
      return new BinomialWithBonusCount(GsonHelper.getAsInt(parameters, "extra"), GsonHelper.getAsFloat(parameters, "probability"));
    }
    throw new IllegalArgumentException("Invalid formula id: " + id);
  }
}
