package net.minecraft.data.loot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.CaveVines;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LimitCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.BonusLevelTableCondition;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public abstract class BlockLootSubProvider implements LootTableSubProvider {
   protected static final LootItemCondition.Builder HAS_SHEARS;
   protected final HolderLookup.Provider registries;
   protected final Set<Item> explosionResistant;
   protected final FeatureFlagSet enabledFeatures;
   protected final Map<ResourceKey<LootTable>, LootTable.Builder> map;
   protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES;
   private static final float[] NORMAL_LEAVES_STICK_CHANCES;

   protected LootItemCondition.Builder hasSilkTouch() {
      HolderLookup.RegistryLookup var1 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return MatchTool.toolMatches(ItemPredicate.Builder.item().withSubPredicate(ItemSubPredicates.ENCHANTMENTS, ItemEnchantmentsPredicate.enchantments(List.of(new EnchantmentPredicate(var1.getOrThrow(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1))))));
   }

   protected LootItemCondition.Builder doesNotHaveSilkTouch() {
      return this.hasSilkTouch().invert();
   }

   private LootItemCondition.Builder hasShearsOrSilkTouch() {
      return HAS_SHEARS.or(this.hasSilkTouch());
   }

   private LootItemCondition.Builder doesNotHaveShearsOrSilkTouch() {
      return this.hasShearsOrSilkTouch().invert();
   }

   protected BlockLootSubProvider(Set<Item> var1, FeatureFlagSet var2, HolderLookup.Provider var3) {
      this(var1, var2, new HashMap(), var3);
   }

   protected BlockLootSubProvider(Set<Item> var1, FeatureFlagSet var2, Map<ResourceKey<LootTable>, LootTable.Builder> var3, HolderLookup.Provider var4) {
      super();
      this.explosionResistant = var1;
      this.enabledFeatures = var2;
      this.map = var3;
      this.registries = var4;
   }

   protected <T extends FunctionUserBuilder<T>> T applyExplosionDecay(ItemLike var1, FunctionUserBuilder<T> var2) {
      return !this.explosionResistant.contains(var1.asItem()) ? var2.apply(ApplyExplosionDecay.explosionDecay()) : var2.unwrap();
   }

   protected <T extends ConditionUserBuilder<T>> T applyExplosionCondition(ItemLike var1, ConditionUserBuilder<T> var2) {
      return !this.explosionResistant.contains(var1.asItem()) ? var2.when(ExplosionCondition.survivesExplosion()) : var2.unwrap();
   }

   public LootTable.Builder createSingleItemTable(ItemLike var1) {
      return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1))));
   }

   private static LootTable.Builder createSelfDropDispatchTable(Block var0, LootItemCondition.Builder var1, LootPoolEntryContainer.Builder<?> var2) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var0).when(var1)).otherwise(var2)));
   }

   protected LootTable.Builder createSilkTouchDispatchTable(Block var1, LootPoolEntryContainer.Builder<?> var2) {
      return createSelfDropDispatchTable(var1, this.hasSilkTouch(), var2);
   }

   protected LootTable.Builder createShearsDispatchTable(Block var1, LootPoolEntryContainer.Builder<?> var2) {
      return createSelfDropDispatchTable(var1, HAS_SHEARS, var2);
   }

   protected LootTable.Builder createSilkTouchOrShearsDispatchTable(Block var1, LootPoolEntryContainer.Builder<?> var2) {
      return createSelfDropDispatchTable(var1, this.hasShearsOrSilkTouch(), var2);
   }

   protected LootTable.Builder createSingleItemTableWithSilkTouch(Block var1, ItemLike var2) {
      return this.createSilkTouchDispatchTable(var1, (LootPoolEntryContainer.Builder)this.applyExplosionCondition(var1, LootItem.lootTableItem(var2)));
   }

   protected LootTable.Builder createSingleItemTable(ItemLike var1, NumberProvider var2) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add((LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(var2)))));
   }

   protected LootTable.Builder createSingleItemTableWithSilkTouch(Block var1, ItemLike var2, NumberProvider var3) {
      return this.createSilkTouchDispatchTable(var1, (LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(var2).apply(SetItemCountFunction.setCount(var3))));
   }

   private LootTable.Builder createSilkTouchOnlyTable(ItemLike var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(this.hasSilkTouch()).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1)));
   }

   private LootTable.Builder createPotFlowerItemTable(ItemLike var1) {
      return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition(Blocks.FLOWER_POT, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.FLOWER_POT)))).withPool((LootPool.Builder)this.applyExplosionCondition(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1))));
   }

   protected LootTable.Builder createSlabItemTable(Block var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add((LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, (Comparable)SlabType.DOUBLE)))))));
   }

   protected <T extends Comparable<T> & StringRepresentable> LootTable.Builder createSinglePropConditionTable(Block var1, Property<T> var2, T var3) {
      return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(var2, var3))))));
   }

   protected LootTable.Builder createNameableBlockEntityTable(Block var1) {
      return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.CUSTOM_NAME)))));
   }

   protected LootTable.Builder createShulkerBoxDrop(Block var1) {
      return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.CUSTOM_NAME).include(DataComponents.CONTAINER).include(DataComponents.LOCK).include(DataComponents.CONTAINER_LOOT)))));
   }

   protected LootTable.Builder createCopperOreDrops(Block var1) {
      HolderLookup.RegistryLookup var2 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return this.createSilkTouchDispatchTable(var1, (LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(Items.RAW_COPPER).apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F))).apply(ApplyBonusCount.addOreBonusCount(var2.getOrThrow(Enchantments.FORTUNE)))));
   }

   protected LootTable.Builder createLapisOreDrops(Block var1) {
      HolderLookup.RegistryLookup var2 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return this.createSilkTouchDispatchTable(var1, (LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(Items.LAPIS_LAZULI).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 9.0F))).apply(ApplyBonusCount.addOreBonusCount(var2.getOrThrow(Enchantments.FORTUNE)))));
   }

   protected LootTable.Builder createRedstoneOreDrops(Block var1) {
      HolderLookup.RegistryLookup var2 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return this.createSilkTouchDispatchTable(var1, (LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(Items.REDSTONE).apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F))).apply(ApplyBonusCount.addUniformBonusCount(var2.getOrThrow(Enchantments.FORTUNE)))));
   }

   protected LootTable.Builder createBannerDrop(Block var1) {
      return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionCondition(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.CUSTOM_NAME).include(DataComponents.ITEM_NAME).include(DataComponents.HIDE_ADDITIONAL_TOOLTIP).include(DataComponents.BANNER_PATTERNS)))));
   }

   protected LootTable.Builder createBeeNestDrop(Block var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(this.hasSilkTouch()).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES)).apply(CopyBlockState.copyState(var1).copy(BeehiveBlock.HONEY_LEVEL))));
   }

   protected LootTable.Builder createBeeHiveDrop(Block var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var1).when(this.hasSilkTouch())).apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY).include(DataComponents.BEES)).apply(CopyBlockState.copyState(var1).copy(BeehiveBlock.HONEY_LEVEL)).otherwise(LootItem.lootTableItem(var1))));
   }

   protected LootTable.Builder createCaveVinesDrop(Block var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().add(LootItem.lootTableItem(Items.GLOW_BERRIES)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CaveVines.BERRIES, true))));
   }

   protected LootTable.Builder createOreDrop(Block var1, Item var2) {
      HolderLookup.RegistryLookup var3 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return this.createSilkTouchDispatchTable(var1, (LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(var2).apply(ApplyBonusCount.addOreBonusCount(var3.getOrThrow(Enchantments.FORTUNE)))));
   }

   protected LootTable.Builder createMushroomBlockDrop(Block var1, ItemLike var2) {
      return this.createSilkTouchDispatchTable(var1, (LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(var2).apply(SetItemCountFunction.setCount(UniformGenerator.between(-6.0F, 2.0F))).apply(LimitCount.limitCount(IntRange.lowerBound(0)))));
   }

   protected LootTable.Builder createGrassDrops(Block var1) {
      HolderLookup.RegistryLookup var2 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return this.createShearsDispatchTable(var1, (LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.WHEAT_SEEDS).when(LootItemRandomChanceCondition.randomChance(0.125F))).apply(ApplyBonusCount.addUniformBonusCount(var2.getOrThrow(Enchantments.FORTUNE), 2))));
   }

   public LootTable.Builder createStemDrops(Block var1, Item var2) {
      return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionDecay(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add((LootPoolEntryContainer.Builder)LootItem.lootTableItem(var2).apply(StemBlock.AGE.getPossibleValues(), (var1x) -> {
         return SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, (float)(var1x + 1) / 15.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, var1x)));
      }))));
   }

   public LootTable.Builder createAttachedStemDrops(Block var1, Item var2) {
      return LootTable.lootTable().withPool((LootPool.Builder)this.applyExplosionDecay(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var2).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.53333336F))))));
   }

   protected static LootTable.Builder createShearsOnlyDrop(ItemLike var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_SHEARS).add(LootItem.lootTableItem(var0)));
   }

   protected LootTable.Builder createMultifaceBlockDrops(Block var1, LootItemCondition.Builder var2) {
      return LootTable.lootTable().withPool(LootPool.lootPool().add((LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, ((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var1).when(var2)).apply(Direction.values(), (var1x) -> {
         return SetItemCountFunction.setCount(ConstantValue.exactly(1.0F), true).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MultifaceBlock.getFaceProperty(var1x), true)));
      })).apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1.0F), true)))));
   }

   protected LootTable.Builder createLeavesDrops(Block var1, Block var2, float... var3) {
      HolderLookup.RegistryLookup var4 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return this.createSilkTouchOrShearsDispatchTable(var1, ((LootPoolSingletonContainer.Builder)this.applyExplosionCondition(var1, LootItem.lootTableItem(var2))).when(BonusLevelTableCondition.bonusLevelFlatChance(var4.getOrThrow(Enchantments.FORTUNE), var3))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(this.doesNotHaveShearsOrSilkTouch()).add(((LootPoolSingletonContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))).when(BonusLevelTableCondition.bonusLevelFlatChance(var4.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_STICK_CHANCES))));
   }

   protected LootTable.Builder createOakLeavesDrops(Block var1, Block var2, float... var3) {
      HolderLookup.RegistryLookup var4 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return this.createLeavesDrops(var1, var2, var3).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(this.doesNotHaveShearsOrSilkTouch()).add(((LootPoolSingletonContainer.Builder)this.applyExplosionCondition(var1, LootItem.lootTableItem(Items.APPLE))).when(BonusLevelTableCondition.bonusLevelFlatChance(var4.getOrThrow(Enchantments.FORTUNE), 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))));
   }

   protected LootTable.Builder createMangroveLeavesDrops(Block var1) {
      HolderLookup.RegistryLookup var2 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return this.createSilkTouchOrShearsDispatchTable(var1, ((LootPoolSingletonContainer.Builder)this.applyExplosionDecay(Blocks.MANGROVE_LEAVES, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F))))).when(BonusLevelTableCondition.bonusLevelFlatChance(var2.getOrThrow(Enchantments.FORTUNE), NORMAL_LEAVES_STICK_CHANCES)));
   }

   protected LootTable.Builder createCropDrops(Block var1, Item var2, Item var3, LootItemCondition.Builder var4) {
      HolderLookup.RegistryLookup var5 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      return (LootTable.Builder)this.applyExplosionDecay(var1, LootTable.lootTable().withPool(LootPool.lootPool().add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var2).when(var4)).otherwise(LootItem.lootTableItem(var3)))).withPool(LootPool.lootPool().when(var4).add(LootItem.lootTableItem(var3).apply(ApplyBonusCount.addBonusBinomialDistributionCount(var5.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3)))));
   }

   protected LootTable.Builder createDoublePlantShearsDrop(Block var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SHEARS).add(LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))));
   }

   protected LootTable.Builder createDoublePlantWithSeedDrops(Block var1, Block var2) {
      AlternativesEntry.Builder var3 = ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var2).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))).when(HAS_SHEARS)).otherwise(((LootPoolSingletonContainer.Builder)this.applyExplosionCondition(var1, LootItem.lootTableItem(Items.WHEAT_SEEDS))).when(LootItemRandomChanceCondition.randomChance(0.125F)));
      return LootTable.lootTable().withPool(LootPool.lootPool().add(var3).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, (Comparable)DoubleBlockHalf.LOWER))).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, (Comparable)DoubleBlockHalf.UPPER))), new BlockPos(0, 1, 0)))).withPool(LootPool.lootPool().add(var3).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, (Comparable)DoubleBlockHalf.UPPER))).when(LocationCheck.checkLocation(LocationPredicate.Builder.location().setBlock(BlockPredicate.Builder.block().of(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, (Comparable)DoubleBlockHalf.LOWER))), new BlockPos(0, -1, 0))));
   }

   protected LootTable.Builder createCandleDrops(Block var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add((LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(var1).apply(List.of(2, 3, 4), (var1x) -> {
         return SetItemCountFunction.setCount(ConstantValue.exactly((float)var1x)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CandleBlock.CANDLES, var1x)));
      }))));
   }

   protected LootTable.Builder createPetalsDrops(Block var1) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add((LootPoolEntryContainer.Builder)this.applyExplosionDecay(var1, LootItem.lootTableItem(var1).apply(IntStream.rangeClosed(1, 4).boxed().toList(), (var1x) -> {
         return SetItemCountFunction.setCount(ConstantValue.exactly((float)var1x)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PinkPetalsBlock.AMOUNT, var1x)));
      }))));
   }

   protected static LootTable.Builder createCandleCakeDrops(Block var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var0)));
   }

   public static LootTable.Builder noDrop() {
      return LootTable.lootTable();
   }

   protected abstract void generate();

   public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> var1) {
      this.generate();
      HashSet var2 = new HashSet();
      Iterator var3 = BuiltInRegistries.BLOCK.iterator();

      while(var3.hasNext()) {
         Block var4 = (Block)var3.next();
         if (var4.isEnabled(this.enabledFeatures)) {
            ResourceKey var5 = var4.getLootTable();
            if (var5 != BuiltInLootTables.EMPTY && var2.add(var5)) {
               LootTable.Builder var6 = (LootTable.Builder)this.map.remove(var5);
               if (var6 == null) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", var5.location(), BuiltInRegistries.BLOCK.getKey(var4)));
               }

               var1.accept(var5, var6);
            }
         }
      }

      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created block loot tables for non-blocks: " + String.valueOf(this.map.keySet()));
      }
   }

   protected void addNetherVinesDropTable(Block var1, Block var2) {
      HolderLookup.RegistryLookup var3 = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
      LootTable.Builder var4 = this.createSilkTouchOrShearsDispatchTable(var1, LootItem.lootTableItem(var1).when(BonusLevelTableCondition.bonusLevelFlatChance(var3.getOrThrow(Enchantments.FORTUNE), 0.33F, 0.55F, 0.77F, 1.0F)));
      this.add(var1, var4);
      this.add(var2, var4);
   }

   protected LootTable.Builder createDoorTable(Block var1) {
      return this.createSinglePropConditionTable(var1, DoorBlock.HALF, DoubleBlockHalf.LOWER);
   }

   protected void dropPottedContents(Block var1) {
      this.add(var1, (var1x) -> {
         return this.createPotFlowerItemTable(((FlowerPotBlock)var1x).getPotted());
      });
   }

   protected void otherWhenSilkTouch(Block var1, Block var2) {
      this.add(var1, this.createSilkTouchOnlyTable(var2));
   }

   protected void dropOther(Block var1, ItemLike var2) {
      this.add(var1, this.createSingleItemTable(var2));
   }

   protected void dropWhenSilkTouch(Block var1) {
      this.otherWhenSilkTouch(var1, var1);
   }

   protected void dropSelf(Block var1) {
      this.dropOther(var1, var1);
   }

   protected void add(Block var1, Function<Block, LootTable.Builder> var2) {
      this.add(var1, (LootTable.Builder)var2.apply(var1));
   }

   protected void add(Block var1, LootTable.Builder var2) {
      this.map.put(var1.getLootTable(), var2);
   }

   static {
      HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
      NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
      NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};
   }
}
