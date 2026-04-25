package slimeknights.tconstruct.common.data.loot;

import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import java.util.List;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MangrovePropaguleBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.loot.CanItemPerformAbility;
import slimeknights.mantle.loot.function.RetexturedLootFunction;
import slimeknights.mantle.registration.object.BuildingBlockObject;
import slimeknights.mantle.registration.object.FenceBuildingBlockObject;
import slimeknights.mantle.registration.object.WallBuildingBlockObject;
import slimeknights.mantle.registration.object.WoodBlockObject;
import slimeknights.tconstruct.TConstruct;
import slimeknights.tconstruct.common.registration.GeodeItemObject;
import slimeknights.tconstruct.common.registration.GeodeItemObject.BudSize;
import slimeknights.tconstruct.gadgets.TinkerGadgets;
import slimeknights.tconstruct.library.tools.part.IMaterialItem;
import slimeknights.tconstruct.library.utils.NBTTags;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.shared.block.ClearStainedGlassBlock;
import slimeknights.tconstruct.shared.block.SlimeType;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tables.TinkerTables;
import slimeknights.tconstruct.tables.block.entity.chest.TinkersChestBlockEntity;
import slimeknights.tconstruct.tools.TinkerToolParts;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.block.DirtType;
import slimeknights.tconstruct.world.block.FoliageType;

import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockLootTableProvider extends BlockLootSubProvider {
  private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[] {0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

  protected BlockLootTableProvider(HolderLookup.Provider registries) {
    super(Set.<Item>of(), FeatureFlags.REGISTRY.allFlags(), registries);
  }

  @SuppressWarnings("deprecation")
  @Override
  protected Iterable<Block> getKnownBlocks() {
    return BuiltInRegistries.BLOCK.stream()
                                  .filter(block -> TConstruct.MOD_ID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace()))
                                  .collect(Collectors.toList());
  }

  @Override
  protected void generate() {
    this.addCommon();
    this.addDecorative();
    this.addGadgets();
    this.addWorld();
    this.addTools();
    this.addSmeltery();
    this.addFoundry();
  }

  private void addCommon() {
    this.registerFenceBuildingLootTables(TinkerMaterials.blazewood);
    this.registerFenceBuildingLootTables(TinkerMaterials.nahuatl);
    this.dropSelf(TinkerCommons.cheeseBlock.get());

    this.dropSelf(TinkerCommons.goldBars.get());
    this.dropSelf(TinkerCommons.goldPlatform.get());
    this.dropSelf(TinkerCommons.ironPlatform.get());
    this.dropSelf(TinkerCommons.cobaltPlatform.get());
    TinkerCommons.copperPlatform.forEach(this::dropSelf);
    TinkerCommons.waxedCopperPlatform.forEach(this::dropSelf);

    this.dropSelf(TinkerMaterials.cobalt.get());
    this.dropSelf(TinkerMaterials.steel.get());
    this.dropSelf(TinkerMaterials.slimesteel.get());
    this.dropSelf(TinkerMaterials.amethystBronze.get());
    this.dropSelf(TinkerMaterials.roseGold.get());
    this.dropSelf(TinkerMaterials.pigIron.get());
    this.dropSelf(TinkerMaterials.manyullyn.get());
    this.dropSelf(TinkerMaterials.hepatizon.get());
    this.dropSelf(TinkerMaterials.cinderslime.get());
    this.dropSelf(TinkerMaterials.queensSlime.get());
    this.dropSelf(TinkerMaterials.knightmetal.get());
    this.dropSelf(TinkerMaterials.soulsteel.get());
    this.dropSelf(TinkerMaterials.knightslime.get());
  }

  private void addDecorative() {
    this.dropSelf(TinkerCommons.obsidianPane.get());
    this.dropSelf(TinkerCommons.clearGlass.get());
    this.dropSelf(TinkerCommons.clearTintedGlass.get());
    this.dropSelf(TinkerCommons.clearGlassPane.get());
    for (ClearStainedGlassBlock.GlassColor color : ClearStainedGlassBlock.GlassColor.values()) {
      this.dropSelf(TinkerCommons.clearStainedGlass.get(color));
      this.dropSelf(TinkerCommons.clearStainedGlassPane.get(color));
    }
    this.dropSelf(TinkerCommons.soulGlass.get());
    this.dropSelf(TinkerCommons.soulGlassPane.get());
  }

  private void addTools() {
    this.add(TinkerTables.tinkersChest.get(), block -> droppingWithFunctions(block, builder ->
      builder.apply(COPY_NAME).apply(COPY_CUSTOM_DATA)));
    this.add(TinkerTables.partChest.get(), block -> droppingWithFunctions(block, builder -> builder.apply(COPY_NAME)));
    this.add(TinkerTables.castChest.get(), block -> droppingWithFunctions(block, builder ->
      builder.apply(COPY_NAME).apply(COPY_CUSTOM_DATA).apply(COPY_BLOCK_ENTITY_DATA)));

    this.dropTable(TinkerTables.craftingStation.get());
    this.dropTable(TinkerTables.partBuilder.get());
    this.dropTable(TinkerTables.tinkerStation.get());
    this.dropAnvil(TinkerTables.tinkersAnvil.get());
    this.dropTable(TinkerTables.modifierWorktable.get());
    this.dropAnvil(TinkerTables.scorchedAnvil.get());
    this.add(TinkerToolParts.fakeStorageBlock.get(), block -> droppingWithFunctions(block, builder -> builder.apply(COPY_MATERIAL)));
  }

  private void addWorld() {
    this.add(TinkerWorld.cobaltOre.get(), block -> createOreDrop(block, TinkerWorld.rawCobalt.asItem()));
    this.dropSelf(TinkerWorld.rawCobaltBlock.get());
    TinkerWorld.heads.forEach(this::dropSelf);

    TinkerWorld.slime.forEach((type, block) -> {
      if (type != SlimeType.EARTH) {
        this.dropSelf(block);
      }
    });
    TinkerWorld.congealedSlime.forEach((slime, block) -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerCommons.slimeball.get(slime), ConstantValue.exactly(4))));

    TinkerWorld.slimeDirt.forEach(this::dropSelf);
    TinkerWorld.vanillaSlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, Blocks.DIRT)));
    TinkerWorld.earthSlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerWorld.slimeDirt.get(DirtType.EARTH))));
    TinkerWorld.skySlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerWorld.slimeDirt.get(DirtType.SKY))));
    TinkerWorld.enderSlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerWorld.slimeDirt.get(DirtType.ENDER))));
    TinkerWorld.ichorSlimeGrass.forEach(block -> this.add(block, createSingleItemTableWithSilkTouch(block, TinkerWorld.slimeDirt.get(DirtType.ICHOR))));

    TinkerWorld.slimeSapling.forEach((type, block) -> {
      if (type != FoliageType.ENDER) {
        this.dropSelf(block);
      }
    });
    this.add(TinkerWorld.slimeSapling.get(FoliageType.ENDER), sapling -> applyExplosionDecay(
      sapling,
      LootTable.lootTable().withPool(LootPool.lootPool()
        .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(sapling).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MangrovePropaguleBlock.AGE, 4)))
        .add(LootItem.lootTableItem(sapling)))));
    TinkerWorld.pottedSlimeSapling.forEach(this::dropPottedContents);
    TinkerWorld.pottedSlimeFern.forEach(this::dropPottedContents);

    TinkerWorld.slimeTallGrass.forEach(block -> this.add(block, BlockLootTableProvider::onlyShears));
    for (FoliageType type : FoliageType.OVERWORLD) {
      this.add(TinkerWorld.slimeLeaves.get(type), block -> randomDropSlimeBallOrSapling(type, block, TinkerWorld.slimeSapling.get(type), NORMAL_LEAVES_SAPLING_CHANCES));
      this.add(TinkerWorld.slimeFern.get(type), BlockLootTableProvider::onlyShears);
    }
    for (FoliageType type : FoliageType.NETHER) {
      this.dropSelf(TinkerWorld.slimeLeaves.get(type));
      this.dropSelf(TinkerWorld.slimeFern.get(type));
    }
    this.add(TinkerWorld.slimeLeaves.get(FoliageType.ENDER), leaves -> droppingSilkOrShears(leaves,
      applyExplosionDecay(leaves, LootItem.lootTableItem(TinkerCommons.slimeball.get(SlimeType.ENDER)).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))
        .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune(), NORMAL_LEAVES_STICK_CHANCES))));
    this.add(TinkerWorld.slimeFern.get(FoliageType.ENDER), BlockLootTableProvider::onlyShears);

    this.add(TinkerWorld.skySlimeVine.get(), BlockLootTableProvider::onlyShears);
    this.add(TinkerWorld.enderSlimeVine.get(), BlockLootTableProvider::onlyShears);

    this.registerWoodLootTables(TinkerWorld.greenheart);
    this.registerWoodLootTables(TinkerWorld.skyroot);
    this.registerWoodLootTables(TinkerWorld.bloodshroom);
    this.registerWoodLootTables(TinkerWorld.enderbark);
    this.dropSelf(TinkerWorld.enderbarkRoots.get());
    TinkerWorld.slimyEnderbarkRoots.forEach(this::dropSelf);

    this.registerCluster(TinkerWorld.steelCluster.get(), TinkerWorld.steelShard);
    this.registerCluster(TinkerWorld.cobaltCluster.get(), TinkerWorld.cobaltShard);
    this.registerCluster(TinkerWorld.knightmetalCluster.get(), TinkerWorld.knightmetalShard);

    this.registerGeode(TinkerWorld.earthGeode);
    this.registerGeode(TinkerWorld.skyGeode);
    this.registerGeode(TinkerWorld.ichorGeode);
    this.registerGeode(TinkerWorld.enderGeode);
  }

  private void addGadgets() {
    this.dropSelf(TinkerGadgets.punji.get());
    TinkerGadgets.cake.forEach(block -> this.add(block, noDrop()));
    this.add(TinkerGadgets.magmaCake.get(), noDrop());
  }

  private void addSmeltery() {
    this.dropSelf(TinkerSmeltery.grout.get());
    this.dropSelf(TinkerSmeltery.searedMelter.get());
    this.dropSelf(TinkerSmeltery.searedHeater.get());
    this.dropTable(TinkerSmeltery.smelteryController.get());

    this.registerBuildingLootTables(TinkerSmeltery.searedStone);
    this.registerWallBuildingLootTables(TinkerSmeltery.searedCobble);
    this.registerBuildingLootTables(TinkerSmeltery.searedPaver);
    this.registerWallBuildingLootTables(TinkerSmeltery.searedBricks);
    this.dropSelf(TinkerSmeltery.searedCrackedBricks.get());
    this.dropSelf(TinkerSmeltery.searedFancyBricks.get());
    this.dropSelf(TinkerSmeltery.searedTriangleBricks.get());
    this.dropSelf(TinkerSmeltery.searedLamp.get());
    this.dropSelf(TinkerSmeltery.searedLadder.get());
    this.dropSelf(TinkerSmeltery.searedGlass.get());
    this.dropSelf(TinkerSmeltery.searedSoulGlass.get());
    this.dropSelf(TinkerSmeltery.searedTintedGlass.get());
    this.dropSelf(TinkerSmeltery.searedGlassPane.get());
    this.dropSelf(TinkerSmeltery.searedSoulGlassPane.get());
    this.dropTable(TinkerSmeltery.searedDrain.get());
    this.dropTable(TinkerSmeltery.searedChute.get());
    this.dropTable(TinkerSmeltery.searedDuct.get());

    Function<Block, LootTable.Builder> dropTank = block -> droppingWithFunctions(block, builder ->
      builder.apply(COPY_NAME).apply(COPY_CUSTOM_DATA).apply(COPY_BLOCK_ENTITY_DATA));
    TinkerSmeltery.searedTank.forEach(block -> this.add(block, dropTank));
    this.add(TinkerSmeltery.searedFluidCannon.get(), dropTank);
    this.add(TinkerSmeltery.scorchedFluidCannon.get(), dropTank);
    this.add(TinkerSmeltery.endFluidCannon.get(), dropTank);
    this.add(TinkerSmeltery.searedLantern.get(), dropTank);
    this.add(TinkerSmeltery.searedCastingTank.get(), dropTank);

    this.dropSelf(TinkerSmeltery.searedFaucet.get());
    this.dropSelf(TinkerSmeltery.searedChannel.get());
    this.dropSelf(TinkerSmeltery.searedBasin.get());
    this.dropSelf(TinkerSmeltery.searedTable.get());
    this.dropSelf(TinkerSmeltery.copperGauge.get());
    this.dropSelf(TinkerSmeltery.obsidianGauge.get());
  }

  private void addFoundry() {
    this.dropSelf(TinkerSmeltery.netherGrout.get());
    this.dropSelf(TinkerSmeltery.scorchedAlloyer.get());
    this.dropTable(TinkerSmeltery.foundryController.get());

    this.dropSelf(TinkerSmeltery.scorchedStone.get());
    this.dropSelf(TinkerSmeltery.polishedScorchedStone.get());
    this.registerFenceBuildingLootTables(TinkerSmeltery.scorchedBricks);
    this.dropSelf(TinkerSmeltery.chiseledScorchedBricks.get());
    this.registerBuildingLootTables(TinkerSmeltery.scorchedRoad);
    this.dropSelf(TinkerSmeltery.scorchedLamp.get());
    this.dropSelf(TinkerSmeltery.scorchedLadder.get());
    this.dropSelf(TinkerSmeltery.scorchedGlass.get());
    this.dropSelf(TinkerSmeltery.scorchedSoulGlass.get());
    this.dropSelf(TinkerSmeltery.scorchedTintedGlass.get());
    this.dropSelf(TinkerSmeltery.scorchedGlassPane.get());
    this.dropSelf(TinkerSmeltery.scorchedSoulGlassPane.get());
    this.dropTable(TinkerSmeltery.scorchedDrain.get());
    this.dropTable(TinkerSmeltery.scorchedChute.get());
    this.dropTable(TinkerSmeltery.scorchedDuct.get());

    Function<Block, LootTable.Builder> dropTank = block -> droppingWithFunctions(block, builder ->
      builder.apply(COPY_NAME).apply(COPY_CUSTOM_DATA).apply(COPY_BLOCK_ENTITY_DATA));
    TinkerSmeltery.scorchedTank.forEach(block -> this.add(block, dropTank));
    this.add(TinkerSmeltery.scorchedLantern.get(), dropTank);

    this.dropSelf(TinkerSmeltery.scorchedFaucet.get());
    this.dropSelf(TinkerSmeltery.scorchedChannel.get());
    this.dropSelf(TinkerSmeltery.scorchedBasin.get());
    this.dropSelf(TinkerSmeltery.scorchedTable.get());
    this.dropSelf(TinkerSmeltery.scorchedProxyTank.get());
  }

  private static final LootItemCondition.Builder SHEARS = CanItemPerformAbility.canItemPerformAbility(ItemAbilities.SHEARS_DIG);

  private HolderLookup.RegistryLookup<Enchantment> enchantments() {
    return this.registries.lookupOrThrow(Registries.ENCHANTMENT);
  }

  private net.minecraft.core.Holder<Enchantment> fortune() {
    return enchantments().getOrThrow(Enchantments.FORTUNE);
  }

  private LootItemCondition.Builder silkTouch() {
    return MatchTool.toolMatches(
      ItemPredicate.Builder.item().withSubPredicate(
        ItemSubPredicates.ENCHANTMENTS,
        ItemEnchantmentsPredicate.enchantments(
          List.of(new EnchantmentPredicate(enchantments().getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1)))
        )));
  }

  private LootItemCondition.Builder silkTouchOrShears() {
    return SHEARS.or(this.silkTouch());
  }

  private LootItemCondition.Builder hasNoShearsOrSilkTouch() {
    return this.silkTouchOrShears().invert();
  }

  protected static LootTable.Builder onlyShears(ItemLike item) {
    return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).when(SHEARS).add(LootItem.lootTableItem(item)));
  }

  private LootTable.Builder droppingSilkOrShears(Block block, LootPoolEntryContainer.Builder<?> alternativeLootEntry) {
    return createSelfDropDispatchTable(block, this.silkTouchOrShears(), alternativeLootEntry);
  }

  private LootTable.Builder dropSapling(Block leaves, Block sapling, float... fortune) {
    return droppingSilkOrShears(leaves, applyExplosionCondition(leaves, LootItem.lootTableItem(sapling))
      .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune(), fortune)));
  }

  private LootTable.Builder randomDropSlimeBallOrSapling(FoliageType foliageType, Block leaves, Block sapling, float... fortune) {
    LootTable.Builder builder = dropSapling(leaves, sapling, fortune);
    SlimeType slime = foliageType.asSlime();
    if (slime != null) {
      return builder.withPool(
        LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                .when(this.hasNoShearsOrSilkTouch())
                .add(applyExplosionCondition(leaves, LootItem.lootTableItem(TinkerCommons.slimeball.get(slime)))
                       .when(BonusLevelTableCondition.bonusLevelFlatChance(fortune(), 1 / 50f, 1 / 45f, 1 / 40f, 1 / 30f, 1 / 20f))));
    }
    return builder;
  }

  private LootTable.Builder droppingWithFunctions(Block block, Function<LootItem.Builder<?>, LootItem.Builder<?>> mapping) {
    return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(mapping.apply(LootItem.lootTableItem(block)))));
  }

  private void registerBuildingLootTables(BuildingBlockObject object) {
    this.dropSelf(object.get());
    this.add(object.getSlab(), this::createSlabItemTable);
    this.dropSelf(object.getStairs());
  }

  private void registerWallBuildingLootTables(WallBuildingBlockObject object) {
    registerBuildingLootTables(object);
    this.dropSelf(object.getWall());
  }

  private void registerFenceBuildingLootTables(FenceBuildingBlockObject object) {
    registerBuildingLootTables(object);
    this.dropSelf(object.getFence());
  }

  private void registerWoodLootTables(WoodBlockObject object) {
    registerFenceBuildingLootTables(object);
    this.dropSelf(object.getLog());
    this.dropSelf(object.getStrippedLog());
    this.dropSelf(object.getWood());
    this.dropSelf(object.getStrippedWood());
    this.dropSelf(object.getFenceGate());
    this.add(object.getDoor(), this::createDoorTable);
    this.dropSelf(object.getTrapdoor());
    this.dropSelf(object.getPressurePlate());
    this.dropSelf(object.getButton());
    this.dropSelf(object.getSign());
    this.dropSelf(object.getHangingSign());
  }

  private final LootItemFunction.Builder COPY_NAME = CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.CUSTOM_NAME);
  private final LootItemFunction.Builder COPY_CUSTOM_DATA = CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                                                 .include(DataComponents.CUSTOM_DATA);
  private final LootItemFunction.Builder COPY_BLOCK_ENTITY_DATA = CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                                                        .include(DataComponents.BLOCK_ENTITY_DATA);
  private final LootItemFunction.Builder COPY_MATERIAL = CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                               .include(DataComponents.CUSTOM_DATA);
  private final Function<Block, LootTable.Builder> ADD_TABLE = block -> droppingWithFunctions(block, builder -> builder.apply(COPY_NAME).apply(RetexturedLootFunction::new));
  private final Function<Block, LootTable.Builder> ADD_ANVIL = block -> droppingWithFunctions(block, builder -> builder.apply(COPY_NAME).apply(RetexturedLootFunction::new)).apply(COPY_MATERIAL);

  private void dropTable(Block table) {
    this.add(table, ADD_TABLE);
  }

  private void dropAnvil(Block table) {
    this.add(table, ADD_ANVIL);
  }

  private void registerCluster(Block cluster, ItemLike drop) {
    this.add(cluster, block -> createSilkTouchDispatchTable(
      block,
      LootItem.lootTableItem(drop)
        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4.0F)))
        .apply(ApplyBonusCount.addOreBonusCount(fortune()))
        .when(MatchTool.toolMatches(ItemPredicate.Builder.item().of(ItemTags.CLUSTER_MAX_HARVESTABLES)))
        .otherwise(applyExplosionDecay(block, LootItem.lootTableItem(drop).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))))));
  }

  private void registerGeode(GeodeItemObject geode) {
    this.dropSelf(geode.getBlock());
    registerCluster(geode.getBud(BudSize.CLUSTER), geode);
    for (BudSize size : BudSize.SIZES) {
      this.dropWhenSilkTouch(geode.getBud(size));
    }
    this.add(geode.getBudding(), noDrop());
  }
}
