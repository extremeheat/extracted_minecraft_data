package net.minecraft.data.loot;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.BeetrootBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.PotatoBlock;
import net.minecraft.world.level.block.SeaPickleBlock;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.SweetBerryBushBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.ConstantIntValue;
import net.minecraft.world.level.storage.loot.IntLimiter;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.RandomIntGenerator;
import net.minecraft.world.level.storage.loot.RandomValueBounds;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class BlockLoot implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   private static final LootItemCondition.Builder HAS_SILK_TOUCH;
   private static final LootItemCondition.Builder HAS_NO_SILK_TOUCH;
   private static final LootItemCondition.Builder HAS_SHEARS;
   private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH;
   private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH;
   private static final Set<Item> EXPLOSION_RESISTANT;
   private static final float[] NORMAL_LEAVES_SAPLING_CHANCES;
   private static final float[] JUNGLE_LEAVES_SAPLING_CHANGES;
   private final Map<ResourceLocation, LootTable.Builder> map = Maps.newHashMap();

   public BlockLoot() {
      super();
   }

   private static <T> T applyExplosionDecay(ItemLike var0, FunctionUserBuilder<T> var1) {
      return !EXPLOSION_RESISTANT.contains(var0.asItem()) ? var1.apply(ApplyExplosionDecay.explosionDecay()) : var1.unwrap();
   }

   private static <T> T applyExplosionCondition(ItemLike var0, ConditionUserBuilder<T> var1) {
      return !EXPLOSION_RESISTANT.contains(var0.asItem()) ? var1.when(ExplosionCondition.survivesExplosion()) : var1.unwrap();
   }

   private static LootTable.Builder createSingleItemTable(ItemLike var0) {
      return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionCondition(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0))));
   }

   private static LootTable.Builder createSelfDropDispatchTable(Block var0, LootItemCondition.Builder var1, LootPoolEntryContainer.Builder<?> var2) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var0).when(var1)).otherwise(var2)));
   }

   private static LootTable.Builder createSilkTouchDispatchTable(Block var0, LootPoolEntryContainer.Builder<?> var1) {
      return createSelfDropDispatchTable(var0, HAS_SILK_TOUCH, var1);
   }

   private static LootTable.Builder createShearsDispatchTable(Block var0, LootPoolEntryContainer.Builder<?> var1) {
      return createSelfDropDispatchTable(var0, HAS_SHEARS, var1);
   }

   private static LootTable.Builder createSilkTouchOrShearsDispatchTable(Block var0, LootPoolEntryContainer.Builder<?> var1) {
      return createSelfDropDispatchTable(var0, HAS_SHEARS_OR_SILK_TOUCH, var1);
   }

   private static LootTable.Builder createSingleItemTableWithSilkTouch(Block var0, ItemLike var1) {
      return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionCondition(var0, LootItem.lootTableItem(var1)));
   }

   private static LootTable.Builder createSingleItemTable(ItemLike var0, RandomIntGenerator var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(var0).apply(SetItemCountFunction.setCount(var1)))));
   }

   private static LootTable.Builder createSingleItemTableWithSilkTouch(Block var0, ItemLike var1, RandomIntGenerator var2) {
      return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(var2))));
   }

   private static LootTable.Builder createSilkTouchOnlyTable(ItemLike var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0)));
   }

   private static LootTable.Builder createPotFlowerItemTable(ItemLike var0) {
      return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionCondition(Blocks.FLOWER_POT, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Blocks.FLOWER_POT)))).withPool((LootPool.Builder)applyExplosionCondition(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0))));
   }

   private static LootTable.Builder createSlabItemTable(Block var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(var0).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, (Comparable)SlabType.DOUBLE)))))));
   }

   private static <T extends Comparable<T> & StringRepresentable> LootTable.Builder createSinglePropConditionTable(Block var0, Property<T> var1, T var2) {
      return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionCondition(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(var1, var2))))));
   }

   private static LootTable.Builder createNameableBlockEntityTable(Block var0) {
      return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionCondition(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)))));
   }

   private static LootTable.Builder createShulkerBoxDrop(Block var0) {
      return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionCondition(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)).apply(CopyNbtFunction.copyData(CopyNbtFunction.DataSource.BLOCK_ENTITY).copy("Lock", "BlockEntityTag.Lock").copy("LootTable", "BlockEntityTag.LootTable").copy("LootTableSeed", "BlockEntityTag.LootTableSeed")).apply(SetContainerContents.setContents().withEntry(DynamicLoot.dynamicEntry(ShulkerBoxBlock.CONTENTS))))));
   }

   private static LootTable.Builder createBannerDrop(Block var0) {
      return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionCondition(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)).apply(CopyNbtFunction.copyData(CopyNbtFunction.DataSource.BLOCK_ENTITY).copy("Patterns", "BlockEntityTag.Patterns")))));
   }

   private static LootTable.Builder createBeeNestDrop(Block var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0).apply(CopyNbtFunction.copyData(CopyNbtFunction.DataSource.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees")).apply(CopyBlockState.copyState(var0).copy(BeehiveBlock.HONEY_LEVEL))));
   }

   private static LootTable.Builder createBeeHiveDrop(Block var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var0).when(HAS_SILK_TOUCH)).apply(CopyNbtFunction.copyData(CopyNbtFunction.DataSource.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees")).apply(CopyBlockState.copyState(var0).copy(BeehiveBlock.HONEY_LEVEL)).otherwise(LootItem.lootTableItem(var0))));
   }

   private static LootTable.Builder createOreDrop(Block var0, Item var1) {
      return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(var1).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
   }

   private static LootTable.Builder createMushroomBlockDrop(Block var0, ItemLike var1) {
      return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(RandomValueBounds.between(-6.0F, 2.0F))).apply(LimitCount.limitCount(IntLimiter.lowerBound(0)))));
   }

   private static LootTable.Builder createGrassDrops(Block var0) {
      return createShearsDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.WHEAT_SEEDS).when(LootItemRandomChanceCondition.randomChance(0.125F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2))));
   }

   private static LootTable.Builder createStemDrops(Block var0, Item var1) {
      return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionDecay(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.06666667F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 0)))).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.13333334F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 1)))).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.2F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 2)))).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.26666668F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 3)))).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.33333334F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 4)))).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.4F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 5)))).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.46666667F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 6)))).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.53333336F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, 7)))))));
   }

   private static LootTable.Builder createAttachedStemDrops(Block var0, Item var1) {
      return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionDecay(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.53333336F))))));
   }

   private static LootTable.Builder createShearsOnlyDrop(ItemLike var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).when(HAS_SHEARS).add(LootItem.lootTableItem(var0)));
   }

   private static LootTable.Builder createLeavesDrops(Block var0, Block var1, float... var2) {
      return createSilkTouchOrShearsDispatchTable(var0, ((LootPoolSingletonContainer.Builder)applyExplosionCondition(var0, LootItem.lootTableItem(var1))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, var2))).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(((LootPoolSingletonContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0F, 2.0F))))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F))));
   }

   private static LootTable.Builder createOakLeavesDrops(Block var0, Block var1, float... var2) {
      return createLeavesDrops(var0, var1, var2).withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).when(HAS_NO_SHEARS_OR_SILK_TOUCH).add(((LootPoolSingletonContainer.Builder)applyExplosionCondition(var0, LootItem.lootTableItem(Items.APPLE))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
   }

   private static LootTable.Builder createCropDrops(Block var0, Item var1, Item var2, LootItemCondition.Builder var3) {
      return (LootTable.Builder)applyExplosionDecay(var0, LootTable.lootTable().withPool(LootPool.lootPool().add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var1).when(var3)).otherwise(LootItem.lootTableItem(var2)))).withPool(LootPool.lootPool().when(var3).add(LootItem.lootTableItem(var2).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))));
   }

   private static LootTable.Builder createDoublePlantShearsDrop(Block var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SHEARS).add(LootItem.lootTableItem(var0).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2)))));
   }

   private static LootTable.Builder createDoublePlantWithSeedDrops(Block var0, Block var1) {
      AlternativesEntry.Builder var2 = ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2))).when(HAS_SHEARS)).otherwise(((LootPoolSingletonContainer.Builder)applyExplosionCondition(var0, LootItem.lootTableItem(Items.WHEAT_SEEDS))).when(LootItemRandomChanceCondition.randomChance(0.125F)));
      return LootTable.lootTable().withPool(LootPool.lootPool().add(var2).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, (Comparable)DoubleBlockHalf.LOWER))).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, (Comparable)DoubleBlockHalf.UPPER).build()).build()), new BlockPos(0, 1, 0)))).withPool(LootPool.lootPool().add(var2).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, (Comparable)DoubleBlockHalf.UPPER))).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, (Comparable)DoubleBlockHalf.LOWER).build()).build()), new BlockPos(0, -1, 0))));
   }

   private static LootTable.Builder createCandleDrops(Block var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(var0).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CandleBlock.CANDLES, 2)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(3)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CandleBlock.CANDLES, 3)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(4)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CandleBlock.CANDLES, 4)))))));
   }

   private static LootTable.Builder createCandleCakeDrops(Block var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0)));
   }

   public static LootTable.Builder noDrop() {
      return LootTable.lootTable();
   }

   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> var1) {
      this.dropSelf(Blocks.GRANITE);
      this.dropSelf(Blocks.POLISHED_GRANITE);
      this.dropSelf(Blocks.DIORITE);
      this.dropSelf(Blocks.POLISHED_DIORITE);
      this.dropSelf(Blocks.ANDESITE);
      this.dropSelf(Blocks.POLISHED_ANDESITE);
      this.dropSelf(Blocks.DIRT);
      this.dropSelf(Blocks.COARSE_DIRT);
      this.dropSelf(Blocks.COBBLESTONE);
      this.dropSelf(Blocks.OAK_PLANKS);
      this.dropSelf(Blocks.SPRUCE_PLANKS);
      this.dropSelf(Blocks.BIRCH_PLANKS);
      this.dropSelf(Blocks.JUNGLE_PLANKS);
      this.dropSelf(Blocks.ACACIA_PLANKS);
      this.dropSelf(Blocks.DARK_OAK_PLANKS);
      this.dropSelf(Blocks.OAK_SAPLING);
      this.dropSelf(Blocks.SPRUCE_SAPLING);
      this.dropSelf(Blocks.BIRCH_SAPLING);
      this.dropSelf(Blocks.JUNGLE_SAPLING);
      this.dropSelf(Blocks.ACACIA_SAPLING);
      this.dropSelf(Blocks.DARK_OAK_SAPLING);
      this.dropSelf(Blocks.SAND);
      this.dropSelf(Blocks.RED_SAND);
      this.dropSelf(Blocks.GOLD_ORE);
      this.dropSelf(Blocks.IRON_ORE);
      this.dropSelf(Blocks.OAK_LOG);
      this.dropSelf(Blocks.SPRUCE_LOG);
      this.dropSelf(Blocks.BIRCH_LOG);
      this.dropSelf(Blocks.JUNGLE_LOG);
      this.dropSelf(Blocks.ACACIA_LOG);
      this.dropSelf(Blocks.DARK_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_SPRUCE_LOG);
      this.dropSelf(Blocks.STRIPPED_BIRCH_LOG);
      this.dropSelf(Blocks.STRIPPED_JUNGLE_LOG);
      this.dropSelf(Blocks.STRIPPED_ACACIA_LOG);
      this.dropSelf(Blocks.STRIPPED_DARK_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_OAK_LOG);
      this.dropSelf(Blocks.STRIPPED_WARPED_STEM);
      this.dropSelf(Blocks.STRIPPED_CRIMSON_STEM);
      this.dropSelf(Blocks.OAK_WOOD);
      this.dropSelf(Blocks.SPRUCE_WOOD);
      this.dropSelf(Blocks.BIRCH_WOOD);
      this.dropSelf(Blocks.JUNGLE_WOOD);
      this.dropSelf(Blocks.ACACIA_WOOD);
      this.dropSelf(Blocks.DARK_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_SPRUCE_WOOD);
      this.dropSelf(Blocks.STRIPPED_BIRCH_WOOD);
      this.dropSelf(Blocks.STRIPPED_JUNGLE_WOOD);
      this.dropSelf(Blocks.STRIPPED_ACACIA_WOOD);
      this.dropSelf(Blocks.STRIPPED_DARK_OAK_WOOD);
      this.dropSelf(Blocks.STRIPPED_CRIMSON_HYPHAE);
      this.dropSelf(Blocks.STRIPPED_WARPED_HYPHAE);
      this.dropSelf(Blocks.SPONGE);
      this.dropSelf(Blocks.WET_SPONGE);
      this.dropSelf(Blocks.LAPIS_BLOCK);
      this.dropSelf(Blocks.SANDSTONE);
      this.dropSelf(Blocks.CHISELED_SANDSTONE);
      this.dropSelf(Blocks.CUT_SANDSTONE);
      this.dropSelf(Blocks.NOTE_BLOCK);
      this.dropSelf(Blocks.POWERED_RAIL);
      this.dropSelf(Blocks.DETECTOR_RAIL);
      this.dropSelf(Blocks.STICKY_PISTON);
      this.dropSelf(Blocks.PISTON);
      this.dropSelf(Blocks.WHITE_WOOL);
      this.dropSelf(Blocks.ORANGE_WOOL);
      this.dropSelf(Blocks.MAGENTA_WOOL);
      this.dropSelf(Blocks.LIGHT_BLUE_WOOL);
      this.dropSelf(Blocks.YELLOW_WOOL);
      this.dropSelf(Blocks.LIME_WOOL);
      this.dropSelf(Blocks.PINK_WOOL);
      this.dropSelf(Blocks.GRAY_WOOL);
      this.dropSelf(Blocks.LIGHT_GRAY_WOOL);
      this.dropSelf(Blocks.CYAN_WOOL);
      this.dropSelf(Blocks.PURPLE_WOOL);
      this.dropSelf(Blocks.BLUE_WOOL);
      this.dropSelf(Blocks.BROWN_WOOL);
      this.dropSelf(Blocks.GREEN_WOOL);
      this.dropSelf(Blocks.RED_WOOL);
      this.dropSelf(Blocks.BLACK_WOOL);
      this.dropSelf(Blocks.DANDELION);
      this.dropSelf(Blocks.POPPY);
      this.dropSelf(Blocks.BLUE_ORCHID);
      this.dropSelf(Blocks.ALLIUM);
      this.dropSelf(Blocks.AZURE_BLUET);
      this.dropSelf(Blocks.RED_TULIP);
      this.dropSelf(Blocks.ORANGE_TULIP);
      this.dropSelf(Blocks.WHITE_TULIP);
      this.dropSelf(Blocks.PINK_TULIP);
      this.dropSelf(Blocks.OXEYE_DAISY);
      this.dropSelf(Blocks.CORNFLOWER);
      this.dropSelf(Blocks.WITHER_ROSE);
      this.dropSelf(Blocks.LILY_OF_THE_VALLEY);
      this.dropSelf(Blocks.BROWN_MUSHROOM);
      this.dropSelf(Blocks.RED_MUSHROOM);
      this.dropSelf(Blocks.GOLD_BLOCK);
      this.dropSelf(Blocks.IRON_BLOCK);
      this.dropSelf(Blocks.BRICKS);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE);
      this.dropSelf(Blocks.OBSIDIAN);
      this.dropSelf(Blocks.CRYING_OBSIDIAN);
      this.dropSelf(Blocks.TORCH);
      this.dropSelf(Blocks.OAK_STAIRS);
      this.dropSelf(Blocks.REDSTONE_WIRE);
      this.dropSelf(Blocks.DIAMOND_BLOCK);
      this.dropSelf(Blocks.CRAFTING_TABLE);
      this.dropSelf(Blocks.OAK_SIGN);
      this.dropSelf(Blocks.SPRUCE_SIGN);
      this.dropSelf(Blocks.BIRCH_SIGN);
      this.dropSelf(Blocks.ACACIA_SIGN);
      this.dropSelf(Blocks.JUNGLE_SIGN);
      this.dropSelf(Blocks.DARK_OAK_SIGN);
      this.dropSelf(Blocks.LADDER);
      this.dropSelf(Blocks.RAIL);
      this.dropSelf(Blocks.COBBLESTONE_STAIRS);
      this.dropSelf(Blocks.LEVER);
      this.dropSelf(Blocks.STONE_PRESSURE_PLATE);
      this.dropSelf(Blocks.OAK_PRESSURE_PLATE);
      this.dropSelf(Blocks.SPRUCE_PRESSURE_PLATE);
      this.dropSelf(Blocks.BIRCH_PRESSURE_PLATE);
      this.dropSelf(Blocks.JUNGLE_PRESSURE_PLATE);
      this.dropSelf(Blocks.ACACIA_PRESSURE_PLATE);
      this.dropSelf(Blocks.DARK_OAK_PRESSURE_PLATE);
      this.dropSelf(Blocks.REDSTONE_TORCH);
      this.dropSelf(Blocks.STONE_BUTTON);
      this.dropSelf(Blocks.CACTUS);
      this.dropSelf(Blocks.SUGAR_CANE);
      this.dropSelf(Blocks.JUKEBOX);
      this.dropSelf(Blocks.OAK_FENCE);
      this.dropSelf(Blocks.PUMPKIN);
      this.dropSelf(Blocks.NETHERRACK);
      this.dropSelf(Blocks.SOUL_SAND);
      this.dropSelf(Blocks.SOUL_SOIL);
      this.dropSelf(Blocks.BASALT);
      this.dropSelf(Blocks.POLISHED_BASALT);
      this.dropSelf(Blocks.SOUL_TORCH);
      this.dropSelf(Blocks.CARVED_PUMPKIN);
      this.dropSelf(Blocks.JACK_O_LANTERN);
      this.dropSelf(Blocks.REPEATER);
      this.dropSelf(Blocks.OAK_TRAPDOOR);
      this.dropSelf(Blocks.SPRUCE_TRAPDOOR);
      this.dropSelf(Blocks.BIRCH_TRAPDOOR);
      this.dropSelf(Blocks.JUNGLE_TRAPDOOR);
      this.dropSelf(Blocks.ACACIA_TRAPDOOR);
      this.dropSelf(Blocks.DARK_OAK_TRAPDOOR);
      this.dropSelf(Blocks.STONE_BRICKS);
      this.dropSelf(Blocks.MOSSY_STONE_BRICKS);
      this.dropSelf(Blocks.CRACKED_STONE_BRICKS);
      this.dropSelf(Blocks.CHISELED_STONE_BRICKS);
      this.dropSelf(Blocks.IRON_BARS);
      this.dropSelf(Blocks.OAK_FENCE_GATE);
      this.dropSelf(Blocks.BRICK_STAIRS);
      this.dropSelf(Blocks.STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.LILY_PAD);
      this.dropSelf(Blocks.NETHER_BRICKS);
      this.dropSelf(Blocks.NETHER_BRICK_FENCE);
      this.dropSelf(Blocks.NETHER_BRICK_STAIRS);
      this.dropSelf(Blocks.CAULDRON);
      this.dropSelf(Blocks.END_STONE);
      this.dropSelf(Blocks.REDSTONE_LAMP);
      this.dropSelf(Blocks.SANDSTONE_STAIRS);
      this.dropSelf(Blocks.TRIPWIRE_HOOK);
      this.dropSelf(Blocks.EMERALD_BLOCK);
      this.dropSelf(Blocks.SPRUCE_STAIRS);
      this.dropSelf(Blocks.BIRCH_STAIRS);
      this.dropSelf(Blocks.JUNGLE_STAIRS);
      this.dropSelf(Blocks.COBBLESTONE_WALL);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE_WALL);
      this.dropSelf(Blocks.FLOWER_POT);
      this.dropSelf(Blocks.OAK_BUTTON);
      this.dropSelf(Blocks.SPRUCE_BUTTON);
      this.dropSelf(Blocks.BIRCH_BUTTON);
      this.dropSelf(Blocks.JUNGLE_BUTTON);
      this.dropSelf(Blocks.ACACIA_BUTTON);
      this.dropSelf(Blocks.DARK_OAK_BUTTON);
      this.dropSelf(Blocks.SKELETON_SKULL);
      this.dropSelf(Blocks.WITHER_SKELETON_SKULL);
      this.dropSelf(Blocks.ZOMBIE_HEAD);
      this.dropSelf(Blocks.CREEPER_HEAD);
      this.dropSelf(Blocks.DRAGON_HEAD);
      this.dropSelf(Blocks.ANVIL);
      this.dropSelf(Blocks.CHIPPED_ANVIL);
      this.dropSelf(Blocks.DAMAGED_ANVIL);
      this.dropSelf(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE);
      this.dropSelf(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE);
      this.dropSelf(Blocks.COMPARATOR);
      this.dropSelf(Blocks.DAYLIGHT_DETECTOR);
      this.dropSelf(Blocks.REDSTONE_BLOCK);
      this.dropSelf(Blocks.QUARTZ_BLOCK);
      this.dropSelf(Blocks.CHISELED_QUARTZ_BLOCK);
      this.dropSelf(Blocks.QUARTZ_PILLAR);
      this.dropSelf(Blocks.QUARTZ_STAIRS);
      this.dropSelf(Blocks.ACTIVATOR_RAIL);
      this.dropSelf(Blocks.WHITE_TERRACOTTA);
      this.dropSelf(Blocks.ORANGE_TERRACOTTA);
      this.dropSelf(Blocks.MAGENTA_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_BLUE_TERRACOTTA);
      this.dropSelf(Blocks.YELLOW_TERRACOTTA);
      this.dropSelf(Blocks.LIME_TERRACOTTA);
      this.dropSelf(Blocks.PINK_TERRACOTTA);
      this.dropSelf(Blocks.GRAY_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_GRAY_TERRACOTTA);
      this.dropSelf(Blocks.CYAN_TERRACOTTA);
      this.dropSelf(Blocks.PURPLE_TERRACOTTA);
      this.dropSelf(Blocks.BLUE_TERRACOTTA);
      this.dropSelf(Blocks.BROWN_TERRACOTTA);
      this.dropSelf(Blocks.GREEN_TERRACOTTA);
      this.dropSelf(Blocks.RED_TERRACOTTA);
      this.dropSelf(Blocks.BLACK_TERRACOTTA);
      this.dropSelf(Blocks.ACACIA_STAIRS);
      this.dropSelf(Blocks.DARK_OAK_STAIRS);
      this.dropSelf(Blocks.SLIME_BLOCK);
      this.dropSelf(Blocks.IRON_TRAPDOOR);
      this.dropSelf(Blocks.PRISMARINE);
      this.dropSelf(Blocks.PRISMARINE_BRICKS);
      this.dropSelf(Blocks.DARK_PRISMARINE);
      this.dropSelf(Blocks.PRISMARINE_STAIRS);
      this.dropSelf(Blocks.PRISMARINE_BRICK_STAIRS);
      this.dropSelf(Blocks.DARK_PRISMARINE_STAIRS);
      this.dropSelf(Blocks.HAY_BLOCK);
      this.dropSelf(Blocks.WHITE_CARPET);
      this.dropSelf(Blocks.ORANGE_CARPET);
      this.dropSelf(Blocks.MAGENTA_CARPET);
      this.dropSelf(Blocks.LIGHT_BLUE_CARPET);
      this.dropSelf(Blocks.YELLOW_CARPET);
      this.dropSelf(Blocks.LIME_CARPET);
      this.dropSelf(Blocks.PINK_CARPET);
      this.dropSelf(Blocks.GRAY_CARPET);
      this.dropSelf(Blocks.LIGHT_GRAY_CARPET);
      this.dropSelf(Blocks.CYAN_CARPET);
      this.dropSelf(Blocks.PURPLE_CARPET);
      this.dropSelf(Blocks.BLUE_CARPET);
      this.dropSelf(Blocks.BROWN_CARPET);
      this.dropSelf(Blocks.GREEN_CARPET);
      this.dropSelf(Blocks.RED_CARPET);
      this.dropSelf(Blocks.BLACK_CARPET);
      this.dropSelf(Blocks.TERRACOTTA);
      this.dropSelf(Blocks.COAL_BLOCK);
      this.dropSelf(Blocks.RED_SANDSTONE);
      this.dropSelf(Blocks.CHISELED_RED_SANDSTONE);
      this.dropSelf(Blocks.CUT_RED_SANDSTONE);
      this.dropSelf(Blocks.RED_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_STONE);
      this.dropSelf(Blocks.SMOOTH_SANDSTONE);
      this.dropSelf(Blocks.SMOOTH_QUARTZ);
      this.dropSelf(Blocks.SMOOTH_RED_SANDSTONE);
      this.dropSelf(Blocks.SPRUCE_FENCE_GATE);
      this.dropSelf(Blocks.BIRCH_FENCE_GATE);
      this.dropSelf(Blocks.JUNGLE_FENCE_GATE);
      this.dropSelf(Blocks.ACACIA_FENCE_GATE);
      this.dropSelf(Blocks.DARK_OAK_FENCE_GATE);
      this.dropSelf(Blocks.SPRUCE_FENCE);
      this.dropSelf(Blocks.BIRCH_FENCE);
      this.dropSelf(Blocks.JUNGLE_FENCE);
      this.dropSelf(Blocks.ACACIA_FENCE);
      this.dropSelf(Blocks.DARK_OAK_FENCE);
      this.dropSelf(Blocks.END_ROD);
      this.dropSelf(Blocks.PURPUR_BLOCK);
      this.dropSelf(Blocks.PURPUR_PILLAR);
      this.dropSelf(Blocks.PURPUR_STAIRS);
      this.dropSelf(Blocks.END_STONE_BRICKS);
      this.dropSelf(Blocks.MAGMA_BLOCK);
      this.dropSelf(Blocks.NETHER_WART_BLOCK);
      this.dropSelf(Blocks.RED_NETHER_BRICKS);
      this.dropSelf(Blocks.BONE_BLOCK);
      this.dropSelf(Blocks.OBSERVER);
      this.dropSelf(Blocks.TARGET);
      this.dropSelf(Blocks.WHITE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.ORANGE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.MAGENTA_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.YELLOW_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIME_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.PINK_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.GRAY_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.CYAN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.PURPLE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BLUE_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BROWN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.GREEN_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.RED_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.BLACK_GLAZED_TERRACOTTA);
      this.dropSelf(Blocks.WHITE_CONCRETE);
      this.dropSelf(Blocks.ORANGE_CONCRETE);
      this.dropSelf(Blocks.MAGENTA_CONCRETE);
      this.dropSelf(Blocks.LIGHT_BLUE_CONCRETE);
      this.dropSelf(Blocks.YELLOW_CONCRETE);
      this.dropSelf(Blocks.LIME_CONCRETE);
      this.dropSelf(Blocks.PINK_CONCRETE);
      this.dropSelf(Blocks.GRAY_CONCRETE);
      this.dropSelf(Blocks.LIGHT_GRAY_CONCRETE);
      this.dropSelf(Blocks.CYAN_CONCRETE);
      this.dropSelf(Blocks.PURPLE_CONCRETE);
      this.dropSelf(Blocks.BLUE_CONCRETE);
      this.dropSelf(Blocks.BROWN_CONCRETE);
      this.dropSelf(Blocks.GREEN_CONCRETE);
      this.dropSelf(Blocks.RED_CONCRETE);
      this.dropSelf(Blocks.BLACK_CONCRETE);
      this.dropSelf(Blocks.WHITE_CONCRETE_POWDER);
      this.dropSelf(Blocks.ORANGE_CONCRETE_POWDER);
      this.dropSelf(Blocks.MAGENTA_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIGHT_BLUE_CONCRETE_POWDER);
      this.dropSelf(Blocks.YELLOW_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIME_CONCRETE_POWDER);
      this.dropSelf(Blocks.PINK_CONCRETE_POWDER);
      this.dropSelf(Blocks.GRAY_CONCRETE_POWDER);
      this.dropSelf(Blocks.LIGHT_GRAY_CONCRETE_POWDER);
      this.dropSelf(Blocks.CYAN_CONCRETE_POWDER);
      this.dropSelf(Blocks.PURPLE_CONCRETE_POWDER);
      this.dropSelf(Blocks.BLUE_CONCRETE_POWDER);
      this.dropSelf(Blocks.BROWN_CONCRETE_POWDER);
      this.dropSelf(Blocks.GREEN_CONCRETE_POWDER);
      this.dropSelf(Blocks.RED_CONCRETE_POWDER);
      this.dropSelf(Blocks.BLACK_CONCRETE_POWDER);
      this.dropSelf(Blocks.KELP);
      this.dropSelf(Blocks.DRIED_KELP_BLOCK);
      this.dropSelf(Blocks.DEAD_TUBE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_BRAIN_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_FIRE_CORAL_BLOCK);
      this.dropSelf(Blocks.DEAD_HORN_CORAL_BLOCK);
      this.dropSelf(Blocks.CONDUIT);
      this.dropSelf(Blocks.DRAGON_EGG);
      this.dropSelf(Blocks.BAMBOO);
      this.dropSelf(Blocks.POLISHED_GRANITE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_RED_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.MOSSY_STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.POLISHED_DIORITE_STAIRS);
      this.dropSelf(Blocks.MOSSY_COBBLESTONE_STAIRS);
      this.dropSelf(Blocks.END_STONE_BRICK_STAIRS);
      this.dropSelf(Blocks.STONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_SANDSTONE_STAIRS);
      this.dropSelf(Blocks.SMOOTH_QUARTZ_STAIRS);
      this.dropSelf(Blocks.GRANITE_STAIRS);
      this.dropSelf(Blocks.ANDESITE_STAIRS);
      this.dropSelf(Blocks.RED_NETHER_BRICK_STAIRS);
      this.dropSelf(Blocks.POLISHED_ANDESITE_STAIRS);
      this.dropSelf(Blocks.DIORITE_STAIRS);
      this.dropSelf(Blocks.BRICK_WALL);
      this.dropSelf(Blocks.PRISMARINE_WALL);
      this.dropSelf(Blocks.RED_SANDSTONE_WALL);
      this.dropSelf(Blocks.MOSSY_STONE_BRICK_WALL);
      this.dropSelf(Blocks.GRANITE_WALL);
      this.dropSelf(Blocks.STONE_BRICK_WALL);
      this.dropSelf(Blocks.NETHER_BRICK_WALL);
      this.dropSelf(Blocks.ANDESITE_WALL);
      this.dropSelf(Blocks.RED_NETHER_BRICK_WALL);
      this.dropSelf(Blocks.SANDSTONE_WALL);
      this.dropSelf(Blocks.END_STONE_BRICK_WALL);
      this.dropSelf(Blocks.DIORITE_WALL);
      this.dropSelf(Blocks.LOOM);
      this.dropSelf(Blocks.SCAFFOLDING);
      this.dropSelf(Blocks.HONEY_BLOCK);
      this.dropSelf(Blocks.HONEYCOMB_BLOCK);
      this.dropSelf(Blocks.RESPAWN_ANCHOR);
      this.dropSelf(Blocks.LODESTONE);
      this.dropSelf(Blocks.WARPED_STEM);
      this.dropSelf(Blocks.WARPED_HYPHAE);
      this.dropSelf(Blocks.WARPED_FUNGUS);
      this.dropSelf(Blocks.WARPED_WART_BLOCK);
      this.dropSelf(Blocks.CRIMSON_STEM);
      this.dropSelf(Blocks.CRIMSON_HYPHAE);
      this.dropSelf(Blocks.CRIMSON_FUNGUS);
      this.dropSelf(Blocks.SHROOMLIGHT);
      this.dropSelf(Blocks.CRIMSON_PLANKS);
      this.dropSelf(Blocks.WARPED_PLANKS);
      this.dropSelf(Blocks.WARPED_PRESSURE_PLATE);
      this.dropSelf(Blocks.WARPED_FENCE);
      this.dropSelf(Blocks.WARPED_TRAPDOOR);
      this.dropSelf(Blocks.WARPED_FENCE_GATE);
      this.dropSelf(Blocks.WARPED_STAIRS);
      this.dropSelf(Blocks.WARPED_BUTTON);
      this.dropSelf(Blocks.WARPED_SIGN);
      this.dropSelf(Blocks.CRIMSON_PRESSURE_PLATE);
      this.dropSelf(Blocks.CRIMSON_FENCE);
      this.dropSelf(Blocks.CRIMSON_TRAPDOOR);
      this.dropSelf(Blocks.CRIMSON_FENCE_GATE);
      this.dropSelf(Blocks.CRIMSON_STAIRS);
      this.dropSelf(Blocks.CRIMSON_BUTTON);
      this.dropSelf(Blocks.CRIMSON_SIGN);
      this.dropSelf(Blocks.NETHERITE_BLOCK);
      this.dropSelf(Blocks.ANCIENT_DEBRIS);
      this.dropSelf(Blocks.BLACKSTONE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICKS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS);
      this.dropSelf(Blocks.BLACKSTONE_STAIRS);
      this.dropSelf(Blocks.BLACKSTONE_WALL);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BRICK_WALL);
      this.dropSelf(Blocks.CHISELED_POLISHED_BLACKSTONE);
      this.dropSelf(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_STAIRS);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_BUTTON);
      this.dropSelf(Blocks.POLISHED_BLACKSTONE_WALL);
      this.dropSelf(Blocks.CHISELED_NETHER_BRICKS);
      this.dropSelf(Blocks.CRACKED_NETHER_BRICKS);
      this.dropSelf(Blocks.QUARTZ_BRICKS);
      this.dropSelf(Blocks.CHAIN);
      this.dropSelf(Blocks.WARPED_ROOTS);
      this.dropSelf(Blocks.CRIMSON_ROOTS);
      this.dropSelf(Blocks.AMETHYST_BLOCK);
      this.dropSelf(Blocks.CALCITE);
      this.dropSelf(Blocks.TUFF);
      this.dropSelf(Blocks.TINTED_GLASS);
      this.dropSelf(Blocks.COPPER_BLOCK);
      this.dropSelf(Blocks.LIGHTLY_WEATHERED_COPPER_BLOCK);
      this.dropSelf(Blocks.SEMI_WEATHERED_COPPER_BLOCK);
      this.dropSelf(Blocks.WEATHERED_COPPER_BLOCK);
      this.dropSelf(Blocks.COPPER_ORE);
      this.dropSelf(Blocks.CUT_COPPER);
      this.dropSelf(Blocks.LIGHTLY_WEATHERED_CUT_COPPER);
      this.dropSelf(Blocks.SEMI_WEATHERED_CUT_COPPER);
      this.dropSelf(Blocks.WEATHERED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_COPPER);
      this.dropSelf(Blocks.WAXED_SEMI_WEATHERED_COPPER);
      this.dropSelf(Blocks.WAXED_LIGHTLY_WEATHERED_COPPER);
      this.dropSelf(Blocks.WAXED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER);
      this.dropSelf(Blocks.WAXED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.WAXED_CUT_COPPER_SLAB);
      this.dropSelf(Blocks.WAXED_LIGHTLY_WEATHERED_CUT_COPPER_SLAB);
      this.dropSelf(Blocks.WAXED_SEMI_WEATHERED_CUT_COPPER_SLAB);
      this.dropSelf(Blocks.CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.LIGHTLY_WEATHERED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.SEMI_WEATHERED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.WEATHERED_CUT_COPPER_STAIRS);
      this.dropSelf(Blocks.CUT_COPPER_SLAB);
      this.dropSelf(Blocks.LIGHTLY_WEATHERED_CUT_COPPER_SLAB);
      this.dropSelf(Blocks.SEMI_WEATHERED_CUT_COPPER_SLAB);
      this.dropSelf(Blocks.WEATHERED_CUT_COPPER_SLAB);
      this.dropSelf(Blocks.LIGHTNING_ROD);
      this.dropOther(Blocks.FARMLAND, Blocks.DIRT);
      this.dropOther(Blocks.TRIPWIRE, Items.STRING);
      this.dropOther(Blocks.DIRT_PATH, Blocks.DIRT);
      this.dropOther(Blocks.KELP_PLANT, Blocks.KELP);
      this.dropOther(Blocks.BAMBOO_SAPLING, Blocks.BAMBOO);
      this.dropOther(Blocks.WATER_CAULDRON, Blocks.CAULDRON);
      this.dropOther(Blocks.LAVA_CAULDRON, Blocks.CAULDRON);
      this.add(Blocks.STONE, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.COBBLESTONE);
      });
      this.add(Blocks.GRASS_BLOCK, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.DIRT);
      });
      this.add(Blocks.PODZOL, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.DIRT);
      });
      this.add(Blocks.MYCELIUM, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.DIRT);
      });
      this.add(Blocks.TUBE_CORAL_BLOCK, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.DEAD_TUBE_CORAL_BLOCK);
      });
      this.add(Blocks.BRAIN_CORAL_BLOCK, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.DEAD_BRAIN_CORAL_BLOCK);
      });
      this.add(Blocks.BUBBLE_CORAL_BLOCK, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.DEAD_BUBBLE_CORAL_BLOCK);
      });
      this.add(Blocks.FIRE_CORAL_BLOCK, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.DEAD_FIRE_CORAL_BLOCK);
      });
      this.add(Blocks.HORN_CORAL_BLOCK, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.DEAD_HORN_CORAL_BLOCK);
      });
      this.add(Blocks.CRIMSON_NYLIUM, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.NETHERRACK);
      });
      this.add(Blocks.WARPED_NYLIUM, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.NETHERRACK);
      });
      this.add(Blocks.BOOKSHELF, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Items.BOOK, ConstantIntValue.exactly(3));
      });
      this.add(Blocks.CLAY, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Items.CLAY_BALL, ConstantIntValue.exactly(4));
      });
      this.add(Blocks.ENDER_CHEST, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Blocks.OBSIDIAN, ConstantIntValue.exactly(8));
      });
      this.add(Blocks.SNOW_BLOCK, (var0) -> {
         return createSingleItemTableWithSilkTouch(var0, Items.SNOWBALL, ConstantIntValue.exactly(4));
      });
      this.add(Blocks.CHORUS_PLANT, createSingleItemTable(Items.CHORUS_FRUIT, RandomValueBounds.between(0.0F, 1.0F)));
      this.dropPottedContents(Blocks.POTTED_OAK_SAPLING);
      this.dropPottedContents(Blocks.POTTED_SPRUCE_SAPLING);
      this.dropPottedContents(Blocks.POTTED_BIRCH_SAPLING);
      this.dropPottedContents(Blocks.POTTED_JUNGLE_SAPLING);
      this.dropPottedContents(Blocks.POTTED_ACACIA_SAPLING);
      this.dropPottedContents(Blocks.POTTED_DARK_OAK_SAPLING);
      this.dropPottedContents(Blocks.POTTED_FERN);
      this.dropPottedContents(Blocks.POTTED_DANDELION);
      this.dropPottedContents(Blocks.POTTED_POPPY);
      this.dropPottedContents(Blocks.POTTED_BLUE_ORCHID);
      this.dropPottedContents(Blocks.POTTED_ALLIUM);
      this.dropPottedContents(Blocks.POTTED_AZURE_BLUET);
      this.dropPottedContents(Blocks.POTTED_RED_TULIP);
      this.dropPottedContents(Blocks.POTTED_ORANGE_TULIP);
      this.dropPottedContents(Blocks.POTTED_WHITE_TULIP);
      this.dropPottedContents(Blocks.POTTED_PINK_TULIP);
      this.dropPottedContents(Blocks.POTTED_OXEYE_DAISY);
      this.dropPottedContents(Blocks.POTTED_CORNFLOWER);
      this.dropPottedContents(Blocks.POTTED_LILY_OF_THE_VALLEY);
      this.dropPottedContents(Blocks.POTTED_WITHER_ROSE);
      this.dropPottedContents(Blocks.POTTED_RED_MUSHROOM);
      this.dropPottedContents(Blocks.POTTED_BROWN_MUSHROOM);
      this.dropPottedContents(Blocks.POTTED_DEAD_BUSH);
      this.dropPottedContents(Blocks.POTTED_CACTUS);
      this.dropPottedContents(Blocks.POTTED_BAMBOO);
      this.dropPottedContents(Blocks.POTTED_CRIMSON_FUNGUS);
      this.dropPottedContents(Blocks.POTTED_WARPED_FUNGUS);
      this.dropPottedContents(Blocks.POTTED_CRIMSON_ROOTS);
      this.dropPottedContents(Blocks.POTTED_WARPED_ROOTS);
      this.add(Blocks.ACACIA_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.BIRCH_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.BRICK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.COBBLESTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.DARK_OAK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.DARK_PRISMARINE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.JUNGLE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.NETHER_BRICK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.OAK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.PETRIFIED_OAK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.PRISMARINE_BRICK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.PRISMARINE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.PURPUR_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.QUARTZ_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.RED_SANDSTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.SANDSTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.CUT_RED_SANDSTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.CUT_SANDSTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.SPRUCE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.STONE_BRICK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.STONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.SMOOTH_STONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.POLISHED_GRANITE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.SMOOTH_RED_SANDSTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.MOSSY_STONE_BRICK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.POLISHED_DIORITE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.MOSSY_COBBLESTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.END_STONE_BRICK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.SMOOTH_SANDSTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.SMOOTH_QUARTZ_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.GRANITE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.ANDESITE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.RED_NETHER_BRICK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.POLISHED_ANDESITE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.DIORITE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.CRIMSON_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.WARPED_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.BLACKSTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.POLISHED_BLACKSTONE_SLAB, BlockLoot::createSlabItemTable);
      this.add(Blocks.ACACIA_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.BIRCH_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.DARK_OAK_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.IRON_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.JUNGLE_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.OAK_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.SPRUCE_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.WARPED_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.CRIMSON_DOOR, BlockLoot::createDoorTable);
      this.add(Blocks.BLACK_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.BLUE_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.BROWN_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.CYAN_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.GRAY_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.GREEN_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIGHT_BLUE_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIGHT_GRAY_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LIME_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.MAGENTA_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.PURPLE_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.ORANGE_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.PINK_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.RED_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.WHITE_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.YELLOW_BED, (var0) -> {
         return createSinglePropConditionTable(var0, BedBlock.PART, BedPart.HEAD);
      });
      this.add(Blocks.LILAC, (var0) -> {
         return createSinglePropConditionTable(var0, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.SUNFLOWER, (var0) -> {
         return createSinglePropConditionTable(var0, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.PEONY, (var0) -> {
         return createSinglePropConditionTable(var0, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.ROSE_BUSH, (var0) -> {
         return createSinglePropConditionTable(var0, DoublePlantBlock.HALF, DoubleBlockHalf.LOWER);
      });
      this.add(Blocks.TNT, LootTable.lootTable().withPool((LootPool.Builder)applyExplosionCondition(Blocks.TNT, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Blocks.TNT).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.TNT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(TntBlock.UNSTABLE, false)))))));
      this.add(Blocks.COCOA, (var0) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.COCOA_BEANS).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(3)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CocoaBlock.AGE, 2)))))));
      });
      this.add(Blocks.SEA_PICKLE, (var0) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add((LootPoolEntryContainer.Builder)applyExplosionDecay(Blocks.SEA_PICKLE, LootItem.lootTableItem(var0).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeaPickleBlock.PICKLES, 2)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(3)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeaPickleBlock.PICKLES, 3)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(4)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SeaPickleBlock.PICKLES, 4)))))));
      });
      this.add(Blocks.COMPOSTER, (var0) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().add((LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.COMPOSTER)))).withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.BONE_MEAL)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ComposterBlock.LEVEL, 8))));
      });
      this.add(Blocks.CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.WHITE_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.ORANGE_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.MAGENTA_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.LIGHT_BLUE_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.YELLOW_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.LIME_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.PINK_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.GRAY_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.LIGHT_GRAY_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.CYAN_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.PURPLE_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.BLUE_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.BROWN_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.GREEN_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.RED_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.BLACK_CANDLE, BlockLoot::createCandleDrops);
      this.add(Blocks.BEACON, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.BREWING_STAND, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.CHEST, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.DISPENSER, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.DROPPER, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.ENCHANTING_TABLE, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.FURNACE, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.HOPPER, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.TRAPPED_CHEST, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.SMOKER, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.BLAST_FURNACE, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.BARREL, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.CARTOGRAPHY_TABLE, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.FLETCHING_TABLE, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.GRINDSTONE, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.LECTERN, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.SMITHING_TABLE, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.STONECUTTER, BlockLoot::createNameableBlockEntityTable);
      this.add(Blocks.BELL, BlockLoot::createSingleItemTable);
      this.add(Blocks.LANTERN, BlockLoot::createSingleItemTable);
      this.add(Blocks.SOUL_LANTERN, BlockLoot::createSingleItemTable);
      this.add(Blocks.SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.BLACK_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.BLUE_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.BROWN_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.CYAN_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.GRAY_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.GREEN_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.LIGHT_BLUE_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.LIGHT_GRAY_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.LIME_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.MAGENTA_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.ORANGE_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.PINK_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.PURPLE_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.RED_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.WHITE_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.YELLOW_SHULKER_BOX, BlockLoot::createShulkerBoxDrop);
      this.add(Blocks.BLACK_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.BLUE_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.BROWN_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.CYAN_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.GRAY_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.GREEN_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.LIGHT_BLUE_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.LIGHT_GRAY_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.LIME_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.MAGENTA_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.ORANGE_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.PINK_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.PURPLE_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.RED_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.WHITE_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.YELLOW_BANNER, BlockLoot::createBannerDrop);
      this.add(Blocks.PLAYER_HEAD, (var0) -> {
         return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionCondition(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(var0).apply(CopyNbtFunction.copyData(CopyNbtFunction.DataSource.BLOCK_ENTITY).copy("SkullOwner", "SkullOwner")))));
      });
      this.add(Blocks.BEE_NEST, BlockLoot::createBeeNestDrop);
      this.add(Blocks.BEEHIVE, BlockLoot::createBeeHiveDrop);
      this.add(Blocks.BIRCH_LEAVES, (var0) -> {
         return createLeavesDrops(var0, Blocks.BIRCH_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.ACACIA_LEAVES, (var0) -> {
         return createLeavesDrops(var0, Blocks.ACACIA_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.JUNGLE_LEAVES, (var0) -> {
         return createLeavesDrops(var0, Blocks.JUNGLE_SAPLING, JUNGLE_LEAVES_SAPLING_CHANGES);
      });
      this.add(Blocks.SPRUCE_LEAVES, (var0) -> {
         return createLeavesDrops(var0, Blocks.SPRUCE_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.OAK_LEAVES, (var0) -> {
         return createOakLeavesDrops(var0, Blocks.OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      this.add(Blocks.DARK_OAK_LEAVES, (var0) -> {
         return createOakLeavesDrops(var0, Blocks.DARK_OAK_SAPLING, NORMAL_LEAVES_SAPLING_CHANCES);
      });
      LootItemBlockStatePropertyCondition.Builder var2 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.BEETROOTS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BeetrootBlock.AGE, 3));
      this.add(Blocks.BEETROOTS, createCropDrops(Blocks.BEETROOTS, Items.BEETROOT, Items.BEETROOT_SEEDS, var2));
      LootItemBlockStatePropertyCondition.Builder var3 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.WHEAT).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CropBlock.AGE, 7));
      this.add(Blocks.WHEAT, createCropDrops(Blocks.WHEAT, Items.WHEAT, Items.WHEAT_SEEDS, var3));
      LootItemBlockStatePropertyCondition.Builder var4 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.CARROTS).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CarrotBlock.AGE, 7));
      this.add(Blocks.CARROTS, (LootTable.Builder)applyExplosionDecay(Blocks.CARROTS, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.CARROT))).withPool(LootPool.lootPool().when(var4).add(LootItem.lootTableItem(Items.CARROT).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3))))));
      LootItemBlockStatePropertyCondition.Builder var5 = LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.POTATOES).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PotatoBlock.AGE, 7));
      this.add(Blocks.POTATOES, (LootTable.Builder)applyExplosionDecay(Blocks.POTATOES, LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.POTATO))).withPool(LootPool.lootPool().when(var5).add(LootItem.lootTableItem(Items.POTATO).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))).withPool(LootPool.lootPool().when(var5).add(LootItem.lootTableItem(Items.POISONOUS_POTATO).when(LootItemRandomChanceCondition.randomChance(0.02F))))));
      this.add(Blocks.SWEET_BERRY_BUSH, (var0) -> {
         return (LootTable.Builder)applyExplosionDecay(var0, LootTable.lootTable().withPool(LootPool.lootPool().when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SWEET_BERRY_BUSH).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 3))).add(LootItem.lootTableItem(Items.SWEET_BERRIES)).apply(SetItemCountFunction.setCount(RandomValueBounds.between(2.0F, 3.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))).withPool(LootPool.lootPool().when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.SWEET_BERRY_BUSH).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SweetBerryBushBlock.AGE, 2))).add(LootItem.lootTableItem(Items.SWEET_BERRIES)).apply(SetItemCountFunction.setCount(RandomValueBounds.between(1.0F, 2.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.BROWN_MUSHROOM_BLOCK, (var0) -> {
         return createMushroomBlockDrop(var0, Blocks.BROWN_MUSHROOM);
      });
      this.add(Blocks.RED_MUSHROOM_BLOCK, (var0) -> {
         return createMushroomBlockDrop(var0, Blocks.RED_MUSHROOM);
      });
      this.add(Blocks.COAL_ORE, (var0) -> {
         return createOreDrop(var0, Items.COAL);
      });
      this.add(Blocks.EMERALD_ORE, (var0) -> {
         return createOreDrop(var0, Items.EMERALD);
      });
      this.add(Blocks.NETHER_QUARTZ_ORE, (var0) -> {
         return createOreDrop(var0, Items.QUARTZ);
      });
      this.add(Blocks.DIAMOND_ORE, (var0) -> {
         return createOreDrop(var0, Items.DIAMOND);
      });
      this.add(Blocks.NETHER_GOLD_ORE, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.GOLD_NUGGET).apply(SetItemCountFunction.setCount(RandomValueBounds.between(2.0F, 6.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.LAPIS_ORE, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.LAPIS_LAZULI).apply(SetItemCountFunction.setCount(RandomValueBounds.between(4.0F, 9.0F))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.COBWEB, (var0) -> {
         return createSilkTouchOrShearsDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionCondition(var0, LootItem.lootTableItem(Items.STRING)));
      });
      this.add(Blocks.DEAD_BUSH, (var0) -> {
         return createShearsDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(RandomValueBounds.between(0.0F, 2.0F)))));
      });
      this.add(Blocks.NETHER_SPROUTS, BlockLoot::createShearsOnlyDrop);
      this.add(Blocks.SEAGRASS, BlockLoot::createShearsOnlyDrop);
      this.add(Blocks.VINE, BlockLoot::createShearsOnlyDrop);
      this.add(Blocks.TALL_SEAGRASS, createDoublePlantShearsDrop(Blocks.SEAGRASS));
      this.add(Blocks.LARGE_FERN, (var0) -> {
         return createDoublePlantWithSeedDrops(var0, Blocks.FERN);
      });
      this.add(Blocks.TALL_GRASS, (var0) -> {
         return createDoublePlantWithSeedDrops(var0, Blocks.GRASS);
      });
      this.add(Blocks.MELON_STEM, (var0) -> {
         return createStemDrops(var0, Items.MELON_SEEDS);
      });
      this.add(Blocks.ATTACHED_MELON_STEM, (var0) -> {
         return createAttachedStemDrops(var0, Items.MELON_SEEDS);
      });
      this.add(Blocks.PUMPKIN_STEM, (var0) -> {
         return createStemDrops(var0, Items.PUMPKIN_SEEDS);
      });
      this.add(Blocks.ATTACHED_PUMPKIN_STEM, (var0) -> {
         return createAttachedStemDrops(var0, Items.PUMPKIN_SEEDS);
      });
      this.add(Blocks.CHORUS_FLOWER, (var0) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(((LootPoolSingletonContainer.Builder)applyExplosionCondition(var0, LootItem.lootTableItem(var0))).when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS))));
      });
      this.add(Blocks.FERN, BlockLoot::createGrassDrops);
      this.add(Blocks.GRASS, BlockLoot::createGrassDrops);
      this.add(Blocks.GLOWSTONE, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.GLOWSTONE_DUST).apply(SetItemCountFunction.setCount(RandomValueBounds.between(2.0F, 4.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntLimiter.clamp(1, 4)))));
      });
      this.add(Blocks.MELON, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.MELON_SLICE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(3.0F, 7.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntLimiter.upperBound(9)))));
      });
      this.add(Blocks.REDSTONE_ORE, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.REDSTONE).apply(SetItemCountFunction.setCount(RandomValueBounds.between(4.0F, 5.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.add(Blocks.SEA_LANTERN, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).apply(SetItemCountFunction.setCount(RandomValueBounds.between(2.0F, 3.0F))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE)).apply(LimitCount.limitCount(IntLimiter.clamp(1, 5)))));
      });
      this.add(Blocks.NETHER_WART, (var0) -> {
         return LootTable.lootTable().withPool((LootPool.Builder)applyExplosionDecay(var0, LootPool.lootPool().setRolls(ConstantIntValue.exactly(1)).add(LootItem.lootTableItem(Items.NETHER_WART).apply(SetItemCountFunction.setCount(RandomValueBounds.between(2.0F, 4.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(NetherWartBlock.AGE, 3)))).apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(NetherWartBlock.AGE, 3)))))));
      });
      this.add(Blocks.SNOW, (var0) -> {
         return LootTable.lootTable().withPool(LootPool.lootPool().when(LootItemEntityPropertyCondition.entityPresent(LootContext.EntityTarget.THIS)).add(AlternativesEntry.alternatives(AlternativesEntry.alternatives(LootItem.lootTableItem(Items.SNOWBALL).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 1))), ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SNOWBALL).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 2)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2))), ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SNOWBALL).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 3)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(3))), ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SNOWBALL).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 4)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(4))), ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SNOWBALL).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 5)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(5))), ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SNOWBALL).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 6)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(6))), ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.SNOWBALL).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 7)))).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(7))), LootItem.lootTableItem(Items.SNOWBALL).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(8)))).when(HAS_NO_SILK_TOUCH), AlternativesEntry.alternatives(LootItem.lootTableItem(Blocks.SNOW).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 1))), LootItem.lootTableItem(Blocks.SNOW).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 2))), LootItem.lootTableItem(Blocks.SNOW).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(3))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 3))), LootItem.lootTableItem(Blocks.SNOW).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(4))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 4))), LootItem.lootTableItem(Blocks.SNOW).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(5))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 5))), LootItem.lootTableItem(Blocks.SNOW).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(6))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 6))), LootItem.lootTableItem(Blocks.SNOW).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(7))).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SnowLayerBlock.LAYERS, 7))), LootItem.lootTableItem(Blocks.SNOW_BLOCK)))));
      });
      this.add(Blocks.GRAVEL, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionCondition(var0, ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.FLINT).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))).otherwise(LootItem.lootTableItem(var0))));
      });
      this.add(Blocks.CAMPFIRE, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionCondition(var0, LootItem.lootTableItem(Items.CHARCOAL).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(2)))));
      });
      this.add(Blocks.GILDED_BLACKSTONE, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionCondition(var0, ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.GOLD_NUGGET).apply(SetItemCountFunction.setCount(RandomValueBounds.between(2.0F, 5.0F))).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.1F, 0.14285715F, 0.25F, 1.0F))).otherwise(LootItem.lootTableItem(var0))));
      });
      this.add(Blocks.SOUL_CAMPFIRE, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionCondition(var0, LootItem.lootTableItem(Items.SOUL_SOIL).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(1)))));
      });
      this.add(Blocks.AMETHYST_CLUSTER, (var0) -> {
         return createSilkTouchDispatchTable(var0, (LootPoolEntryContainer.Builder)applyExplosionDecay(var0, LootItem.lootTableItem(Items.AMETHYST_SHARD).apply(SetItemCountFunction.setCount(ConstantIntValue.exactly(4))).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
      });
      this.dropWhenSilkTouch(Blocks.SMALL_AMETHYST_BUD);
      this.dropWhenSilkTouch(Blocks.MEDIUM_AMETHYST_BUD);
      this.dropWhenSilkTouch(Blocks.LARGE_AMETHYST_BUD);
      this.dropWhenSilkTouch(Blocks.GLASS);
      this.dropWhenSilkTouch(Blocks.WHITE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.ORANGE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.MAGENTA_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.YELLOW_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIME_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.PINK_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GRAY_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.CYAN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.PURPLE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BLUE_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BROWN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GREEN_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.RED_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.BLACK_STAINED_GLASS);
      this.dropWhenSilkTouch(Blocks.GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.WHITE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.ORANGE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.MAGENTA_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.YELLOW_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIME_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.PINK_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.GRAY_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.CYAN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.PURPLE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BLUE_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BROWN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.GREEN_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.RED_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.BLACK_STAINED_GLASS_PANE);
      this.dropWhenSilkTouch(Blocks.ICE);
      this.dropWhenSilkTouch(Blocks.PACKED_ICE);
      this.dropWhenSilkTouch(Blocks.BLUE_ICE);
      this.dropWhenSilkTouch(Blocks.TURTLE_EGG);
      this.dropWhenSilkTouch(Blocks.MUSHROOM_STEM);
      this.dropWhenSilkTouch(Blocks.DEAD_TUBE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_BRAIN_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_BUBBLE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_FIRE_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_HORN_CORAL);
      this.dropWhenSilkTouch(Blocks.TUBE_CORAL);
      this.dropWhenSilkTouch(Blocks.BRAIN_CORAL);
      this.dropWhenSilkTouch(Blocks.BUBBLE_CORAL);
      this.dropWhenSilkTouch(Blocks.FIRE_CORAL);
      this.dropWhenSilkTouch(Blocks.HORN_CORAL);
      this.dropWhenSilkTouch(Blocks.DEAD_TUBE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_BRAIN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_BUBBLE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_FIRE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.DEAD_HORN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.TUBE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.BRAIN_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.BUBBLE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.FIRE_CORAL_FAN);
      this.dropWhenSilkTouch(Blocks.HORN_CORAL_FAN);
      this.otherWhenSilkTouch(Blocks.INFESTED_STONE, Blocks.STONE);
      this.otherWhenSilkTouch(Blocks.INFESTED_COBBLESTONE, Blocks.COBBLESTONE);
      this.otherWhenSilkTouch(Blocks.INFESTED_STONE_BRICKS, Blocks.STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_MOSSY_STONE_BRICKS, Blocks.MOSSY_STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_CRACKED_STONE_BRICKS, Blocks.CRACKED_STONE_BRICKS);
      this.otherWhenSilkTouch(Blocks.INFESTED_CHISELED_STONE_BRICKS, Blocks.CHISELED_STONE_BRICKS);
      this.addNetherVinesDropTable(Blocks.WEEPING_VINES, Blocks.WEEPING_VINES_PLANT);
      this.addNetherVinesDropTable(Blocks.TWISTING_VINES, Blocks.TWISTING_VINES_PLANT);
      this.add(Blocks.CAKE, noDrop());
      this.add(Blocks.CANDLE_CAKE, createCandleCakeDrops(Blocks.CANDLE));
      this.add(Blocks.WHITE_CANDLE_CAKE, createCandleCakeDrops(Blocks.WHITE_CANDLE));
      this.add(Blocks.ORANGE_CANDLE_CAKE, createCandleCakeDrops(Blocks.ORANGE_CANDLE));
      this.add(Blocks.MAGENTA_CANDLE_CAKE, createCandleCakeDrops(Blocks.MAGENTA_CANDLE));
      this.add(Blocks.LIGHT_BLUE_CANDLE_CAKE, createCandleCakeDrops(Blocks.LIGHT_BLUE_CANDLE));
      this.add(Blocks.YELLOW_CANDLE_CAKE, createCandleCakeDrops(Blocks.YELLOW_CANDLE));
      this.add(Blocks.LIME_CANDLE_CAKE, createCandleCakeDrops(Blocks.LIME_CANDLE));
      this.add(Blocks.PINK_CANDLE_CAKE, createCandleCakeDrops(Blocks.PINK_CANDLE));
      this.add(Blocks.GRAY_CANDLE_CAKE, createCandleCakeDrops(Blocks.GRAY_CANDLE));
      this.add(Blocks.LIGHT_GRAY_CANDLE_CAKE, createCandleCakeDrops(Blocks.LIGHT_GRAY_CANDLE));
      this.add(Blocks.CYAN_CANDLE_CAKE, createCandleCakeDrops(Blocks.CYAN_CANDLE));
      this.add(Blocks.PURPLE_CANDLE_CAKE, createCandleCakeDrops(Blocks.PURPLE_CANDLE));
      this.add(Blocks.BLUE_CANDLE_CAKE, createCandleCakeDrops(Blocks.BLUE_CANDLE));
      this.add(Blocks.BROWN_CANDLE_CAKE, createCandleCakeDrops(Blocks.BROWN_CANDLE));
      this.add(Blocks.GREEN_CANDLE_CAKE, createCandleCakeDrops(Blocks.GREEN_CANDLE));
      this.add(Blocks.RED_CANDLE_CAKE, createCandleCakeDrops(Blocks.RED_CANDLE));
      this.add(Blocks.BLACK_CANDLE_CAKE, createCandleCakeDrops(Blocks.BLACK_CANDLE));
      this.add(Blocks.FROSTED_ICE, noDrop());
      this.add(Blocks.SPAWNER, noDrop());
      this.add(Blocks.FIRE, noDrop());
      this.add(Blocks.SOUL_FIRE, noDrop());
      this.add(Blocks.NETHER_PORTAL, noDrop());
      this.add(Blocks.BUDDING_AMETHYST, noDrop());
      HashSet var6 = Sets.newHashSet();
      Iterator var7 = Registry.BLOCK.iterator();

      while(var7.hasNext()) {
         Block var8 = (Block)var7.next();
         ResourceLocation var9 = var8.getLootTable();
         if (var9 != BuiltInLootTables.EMPTY && var6.add(var9)) {
            LootTable.Builder var10 = (LootTable.Builder)this.map.remove(var9);
            if (var10 == null) {
               throw new IllegalStateException(String.format("Missing loottable '%s' for '%s'", var9, Registry.BLOCK.getKey(var8)));
            }

            var1.accept(var9, var10);
         }
      }

      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
      }
   }

   private void addNetherVinesDropTable(Block var1, Block var2) {
      LootTable.Builder var3 = createSilkTouchOrShearsDispatchTable(var1, LootItem.lootTableItem(var1).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.33F, 0.55F, 0.77F, 1.0F)));
      this.add(var1, var3);
      this.add(var2, var3);
   }

   public static LootTable.Builder createDoorTable(Block var0) {
      return createSinglePropConditionTable(var0, DoorBlock.HALF, DoubleBlockHalf.LOWER);
   }

   public void dropPottedContents(Block var1) {
      this.add(var1, (var0) -> {
         return createPotFlowerItemTable(((FlowerPotBlock)var0).getContent());
      });
   }

   public void otherWhenSilkTouch(Block var1, Block var2) {
      this.add(var1, createSilkTouchOnlyTable(var2));
   }

   public void dropOther(Block var1, ItemLike var2) {
      this.add(var1, createSingleItemTable(var2));
   }

   public void dropWhenSilkTouch(Block var1) {
      this.otherWhenSilkTouch(var1, var1);
   }

   public void dropSelf(Block var1) {
      this.dropOther(var1, var1);
   }

   private void add(Block var1, Function<Block, LootTable.Builder> var2) {
      this.add(var1, (LootTable.Builder)var2.apply(var1));
   }

   private void add(Block var1, LootTable.Builder var2) {
      this.map.put(var1.getLootTable(), var2);
   }

   // $FF: synthetic method
   public void accept(Object var1) {
      this.accept((BiConsumer)var1);
   }

   static {
      HAS_SILK_TOUCH = MatchTool.toolMatches(ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1))));
      HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();
      HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of((ItemLike)Items.SHEARS));
      HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
      HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
      EXPLOSION_RESISTANT = (Set)Stream.of(Blocks.DRAGON_EGG, Blocks.BEACON, Blocks.CONDUIT, Blocks.SKELETON_SKULL, Blocks.WITHER_SKELETON_SKULL, Blocks.PLAYER_HEAD, Blocks.ZOMBIE_HEAD, Blocks.CREEPER_HEAD, Blocks.DRAGON_HEAD, Blocks.SHULKER_BOX, Blocks.BLACK_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.LIGHT_GRAY_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.WHITE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX).map(ItemLike::asItem).collect(ImmutableSet.toImmutableSet());
      NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
      JUNGLE_LEAVES_SAPLING_CHANGES = new float[]{0.025F, 0.027777778F, 0.03125F, 0.041666668F, 0.1F};
   }
}
