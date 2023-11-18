package net.minecraft.data.loot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import net.minecraft.advancements.critereon.BlockPredicate;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
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
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.BinomialDistributionGenerator;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public abstract class BlockLootSubProvider implements LootTableSubProvider {
   protected static final LootItemCondition.Builder HAS_SILK_TOUCH = MatchTool.toolMatches(
      ItemPredicate.Builder.item().hasEnchantment(new EnchantmentPredicate(Enchantments.SILK_TOUCH, MinMaxBounds.Ints.atLeast(1)))
   );
   protected static final LootItemCondition.Builder HAS_NO_SILK_TOUCH = HAS_SILK_TOUCH.invert();
   protected static final LootItemCondition.Builder HAS_SHEARS = MatchTool.toolMatches(ItemPredicate.Builder.item().of(Items.SHEARS));
   private static final LootItemCondition.Builder HAS_SHEARS_OR_SILK_TOUCH = HAS_SHEARS.or(HAS_SILK_TOUCH);
   private static final LootItemCondition.Builder HAS_NO_SHEARS_OR_SILK_TOUCH = HAS_SHEARS_OR_SILK_TOUCH.invert();
   protected final Set<Item> explosionResistant;
   protected final FeatureFlagSet enabledFeatures;
   protected final Map<ResourceLocation, LootTable.Builder> map;
   protected static final float[] NORMAL_LEAVES_SAPLING_CHANCES = new float[]{0.05F, 0.0625F, 0.083333336F, 0.1F};
   private static final float[] NORMAL_LEAVES_STICK_CHANCES = new float[]{0.02F, 0.022222223F, 0.025F, 0.033333335F, 0.1F};

   protected BlockLootSubProvider(Set<Item> var1, FeatureFlagSet var2) {
      this(var1, var2, new HashMap<>());
   }

   protected BlockLootSubProvider(Set<Item> var1, FeatureFlagSet var2, Map<ResourceLocation, LootTable.Builder> var3) {
      super();
      this.explosionResistant = var1;
      this.enabledFeatures = var2;
      this.map = var3;
   }

   protected <T extends FunctionUserBuilder<T>> T applyExplosionDecay(ItemLike var1, FunctionUserBuilder<T> var2) {
      return (T)(!this.explosionResistant.contains(var1.asItem()) ? var2.apply(ApplyExplosionDecay.explosionDecay()) : var2.unwrap());
   }

   protected <T extends ConditionUserBuilder<T>> T applyExplosionCondition(ItemLike var1, ConditionUserBuilder<T> var2) {
      return (T)(!this.explosionResistant.contains(var1.asItem()) ? var2.when(ExplosionCondition.survivesExplosion()) : var2.unwrap());
   }

   public LootTable.Builder createSingleItemTable(ItemLike var1) {
      return LootTable.lootTable()
         .withPool(this.applyExplosionCondition(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1))));
   }

   private static LootTable.Builder createSelfDropDispatchTable(Block var0, LootItemCondition.Builder var1, LootPoolEntryContainer.Builder<?> var2) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var0).when(var1)).otherwise(var2))
         );
   }

   protected static LootTable.Builder createSilkTouchDispatchTable(Block var0, LootPoolEntryContainer.Builder<?> var1) {
      return createSelfDropDispatchTable(var0, HAS_SILK_TOUCH, var1);
   }

   protected static LootTable.Builder createShearsDispatchTable(Block var0, LootPoolEntryContainer.Builder<?> var1) {
      return createSelfDropDispatchTable(var0, HAS_SHEARS, var1);
   }

   protected static LootTable.Builder createSilkTouchOrShearsDispatchTable(Block var0, LootPoolEntryContainer.Builder<?> var1) {
      return createSelfDropDispatchTable(var0, HAS_SHEARS_OR_SILK_TOUCH, var1);
   }

   protected LootTable.Builder createSingleItemTableWithSilkTouch(Block var1, ItemLike var2) {
      return createSilkTouchDispatchTable(var1, this.applyExplosionCondition(var1, LootItem.lootTableItem(var2)));
   }

   protected LootTable.Builder createSingleItemTable(ItemLike var1, NumberProvider var2) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(this.applyExplosionDecay(var1, LootItem.lootTableItem(var1).apply(SetItemCountFunction.setCount(var2))))
         );
   }

   protected LootTable.Builder createSingleItemTableWithSilkTouch(Block var1, ItemLike var2, NumberProvider var3) {
      return createSilkTouchDispatchTable(var1, this.applyExplosionDecay(var1, LootItem.lootTableItem(var2).apply(SetItemCountFunction.setCount(var3))));
   }

   private static LootTable.Builder createSilkTouchOnlyTable(ItemLike var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().when(HAS_SILK_TOUCH).setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var0)));
   }

   private LootTable.Builder createPotFlowerItemTable(ItemLike var1) {
      return LootTable.lootTable()
         .withPool(
            this.applyExplosionCondition(
               Blocks.FLOWER_POT, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.FLOWER_POT))
            )
         )
         .withPool(this.applyExplosionCondition(var1, LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var1))));
   }

   protected LootTable.Builder createSlabItemTable(Block var1) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  this.applyExplosionDecay(
                     var1,
                     LootItem.lootTableItem(var1)
                        .apply(
                           SetItemCountFunction.setCount(ConstantValue.exactly(2.0F))
                              .when(
                                 LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1)
                                    .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))
                              )
                        )
                  )
               )
         );
   }

   protected <T extends Comparable<T> & StringRepresentable> LootTable.Builder createSinglePropConditionTable(Block var1, Property<T> var2, T var3) {
      return LootTable.lootTable()
         .withPool(
            this.applyExplosionCondition(
               var1,
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(var1)
                        .when(
                           LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1)
                              .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(var2, (T)var3))
                        )
                  )
            )
         );
   }

   protected LootTable.Builder createNameableBlockEntityTable(Block var1) {
      return LootTable.lootTable()
         .withPool(
            this.applyExplosionCondition(
               var1,
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(var1).apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY)))
            )
         );
   }

   protected LootTable.Builder createShulkerBoxDrop(Block var1) {
      return LootTable.lootTable()
         .withPool(
            this.applyExplosionCondition(
               var1,
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(var1)
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                        .apply(
                           CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                              .copy("Lock", "BlockEntityTag.Lock")
                              .copy("LootTable", "BlockEntityTag.LootTable")
                              .copy("LootTableSeed", "BlockEntityTag.LootTableSeed")
                        )
                        .apply(SetContainerContents.setContents(BlockEntityType.SHULKER_BOX).withEntry(DynamicLoot.dynamicEntry(ShulkerBoxBlock.CONTENTS)))
                  )
            )
         );
   }

   protected LootTable.Builder createCopperOreDrops(Block var1) {
      return createSilkTouchDispatchTable(
         var1,
         this.applyExplosionDecay(
            var1,
            LootItem.lootTableItem(Items.RAW_COPPER)
               .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
               .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
         )
      );
   }

   protected LootTable.Builder createLapisOreDrops(Block var1) {
      return createSilkTouchDispatchTable(
         var1,
         this.applyExplosionDecay(
            var1,
            LootItem.lootTableItem(Items.LAPIS_LAZULI)
               .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 9.0F)))
               .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))
         )
      );
   }

   protected LootTable.Builder createRedstoneOreDrops(Block var1) {
      return createSilkTouchDispatchTable(
         var1,
         this.applyExplosionDecay(
            var1,
            LootItem.lootTableItem(Items.REDSTONE)
               .apply(SetItemCountFunction.setCount(UniformGenerator.between(4.0F, 5.0F)))
               .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE))
         )
      );
   }

   protected LootTable.Builder createBannerDrop(Block var1) {
      return LootTable.lootTable()
         .withPool(
            this.applyExplosionCondition(
               var1,
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     LootItem.lootTableItem(var1)
                        .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                        .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Patterns", "BlockEntityTag.Patterns"))
                  )
            )
         );
   }

   protected static LootTable.Builder createBeeNestDrop(Block var0) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .when(HAS_SILK_TOUCH)
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  LootItem.lootTableItem(var0)
                     .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees"))
                     .apply(CopyBlockState.copyState(var0).copy(BeehiveBlock.HONEY_LEVEL))
               )
         );
   }

   protected static LootTable.Builder createBeeHiveDrop(Block var0) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var0).when(HAS_SILK_TOUCH))
                     .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy("Bees", "BlockEntityTag.Bees"))
                     .apply(CopyBlockState.copyState(var0).copy(BeehiveBlock.HONEY_LEVEL))
                     .otherwise(LootItem.lootTableItem(var0))
               )
         );
   }

   protected static LootTable.Builder createCaveVinesDrop(Block var0) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .add(LootItem.lootTableItem(Items.GLOW_BERRIES))
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(var0)
                     .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CaveVines.BERRIES, true))
               )
         );
   }

   protected LootTable.Builder createOreDrop(Block var1, Item var2) {
      return createSilkTouchDispatchTable(
         var1, this.applyExplosionDecay(var1, LootItem.lootTableItem(var2).apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE)))
      );
   }

   protected LootTable.Builder createMushroomBlockDrop(Block var1, ItemLike var2) {
      return createSilkTouchDispatchTable(
         var1,
         this.applyExplosionDecay(
            var1,
            LootItem.lootTableItem(var2)
               .apply(SetItemCountFunction.setCount(UniformGenerator.between(-6.0F, 2.0F)))
               .apply(LimitCount.limitCount(IntRange.lowerBound(0)))
         )
      );
   }

   protected LootTable.Builder createGrassDrops(Block var1) {
      return createShearsDispatchTable(
         var1,
         this.applyExplosionDecay(
            var1,
            ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(Items.WHEAT_SEEDS).when(LootItemRandomChanceCondition.randomChance(0.125F)))
               .apply(ApplyBonusCount.addUniformBonusCount(Enchantments.BLOCK_FORTUNE, 2))
         )
      );
   }

   public LootTable.Builder createStemDrops(Block var1, Item var2) {
      return LootTable.lootTable()
         .withPool(
            this.applyExplosionDecay(
               var1,
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(
                     (LootPoolEntryContainer.Builder<?>)LootItem.lootTableItem(var2)
                        .apply(
                           StemBlock.AGE.getPossibleValues(),
                           var1x -> SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, (float)(var1x + 1) / 15.0F))
                                 .when(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1)
                                       .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(StemBlock.AGE, var1x.intValue()))
                                 )
                        )
                  )
            )
         );
   }

   public LootTable.Builder createAttachedStemDrops(Block var1, Item var2) {
      return LootTable.lootTable()
         .withPool(
            this.applyExplosionDecay(
               var1,
               LootPool.lootPool()
                  .setRolls(ConstantValue.exactly(1.0F))
                  .add(LootItem.lootTableItem(var2).apply(SetItemCountFunction.setCount(BinomialDistributionGenerator.binomial(3, 0.53333336F))))
            )
         );
   }

   protected static LootTable.Builder createShearsOnlyDrop(ItemLike var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).when(HAS_SHEARS).add(LootItem.lootTableItem(var0)));
   }

   protected LootTable.Builder createMultifaceBlockDrops(Block var1, LootItemCondition.Builder var2) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .add(
                  this.applyExplosionDecay(
                     var1,
                     ((LootPoolSingletonContainer.Builder)((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var1).when(var2))
                           .apply(
                              Direction.values(),
                              var1x -> SetItemCountFunction.setCount(ConstantValue.exactly(1.0F), true)
                                    .when(
                                       LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1)
                                          .setProperties(
                                             StatePropertiesPredicate.Builder.properties().hasProperty(MultifaceBlock.getFaceProperty(var1x), true)
                                          )
                                    )
                           ))
                        .apply(SetItemCountFunction.setCount(ConstantValue.exactly(-1.0F), true))
                  )
               )
         );
   }

   protected LootTable.Builder createLeavesDrops(Block var1, Block var2, float... var3) {
      return createSilkTouchOrShearsDispatchTable(
            var1,
            this.applyExplosionCondition(var1, LootItem.lootTableItem(var2))
               .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, var3))
         )
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
               .add(
                  this.applyExplosionDecay(
                        var1, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
                     )
                     .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))
               )
         );
   }

   protected LootTable.Builder createOakLeavesDrops(Block var1, Block var2, float... var3) {
      return this.createLeavesDrops(var1, var2, var3)
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .when(HAS_NO_SHEARS_OR_SILK_TOUCH)
               .add(
                  this.applyExplosionCondition(var1, LootItem.lootTableItem(Items.APPLE))
                     .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.005F, 0.0055555557F, 0.00625F, 0.008333334F, 0.025F))
               )
         );
   }

   protected LootTable.Builder createMangroveLeavesDrops(Block var1) {
      return createSilkTouchOrShearsDispatchTable(
         var1,
         this.applyExplosionDecay(
               Blocks.MANGROVE_LEAVES, LootItem.lootTableItem(Items.STICK).apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 2.0F)))
            )
            .when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, NORMAL_LEAVES_STICK_CHANCES))
      );
   }

   protected LootTable.Builder createCropDrops(Block var1, Item var2, Item var3, LootItemCondition.Builder var4) {
      return this.applyExplosionDecay(
         var1,
         LootTable.lootTable()
            .withPool(
               LootPool.lootPool().add(((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var2).when(var4)).otherwise(LootItem.lootTableItem(var3)))
            )
            .withPool(
               LootPool.lootPool()
                  .when(var4)
                  .add(LootItem.lootTableItem(var3).apply(ApplyBonusCount.addBonusBinomialDistributionCount(Enchantments.BLOCK_FORTUNE, 0.5714286F, 3)))
            )
      );
   }

   protected static LootTable.Builder createDoublePlantShearsDrop(Block var0) {
      return LootTable.lootTable()
         .withPool(LootPool.lootPool().when(HAS_SHEARS).add(LootItem.lootTableItem(var0).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))));
   }

   protected LootTable.Builder createDoublePlantWithSeedDrops(Block var1, Block var2) {
      AlternativesEntry.Builder var3 = ((LootPoolSingletonContainer.Builder)LootItem.lootTableItem(var2)
            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)))
            .when(HAS_SHEARS))
         .otherwise(this.applyExplosionCondition(var1, LootItem.lootTableItem(Items.WHEAT_SEEDS)).when(LootItemRandomChanceCondition.randomChance(0.125F)));
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .add(var3)
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1)
                     .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER))
               )
               .when(
                  LocationCheck.checkLocation(
                     LocationPredicate.Builder.location()
                        .setBlock(
                           BlockPredicate.Builder.block()
                              .of(var1)
                              .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER).build())
                              .build()
                        ),
                     new BlockPos(0, 1, 0)
                  )
               )
         )
         .withPool(
            LootPool.lootPool()
               .add(var3)
               .when(
                  LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1)
                     .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER))
               )
               .when(
                  LocationCheck.checkLocation(
                     LocationPredicate.Builder.location()
                        .setBlock(
                           BlockPredicate.Builder.block()
                              .of(var1)
                              .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DoublePlantBlock.HALF, DoubleBlockHalf.LOWER).build())
                              .build()
                        ),
                     new BlockPos(0, -1, 0)
                  )
               )
         );
   }

   protected LootTable.Builder createCandleDrops(Block var1) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  this.applyExplosionDecay(
                     var1,
                     LootItem.lootTableItem(var1)
                        .apply(
                           List.of(2, 3, 4),
                           var1x -> SetItemCountFunction.setCount(ConstantValue.exactly((float)var1x.intValue()))
                                 .when(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1)
                                       .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(CandleBlock.CANDLES, var1x.intValue()))
                                 )
                        )
                  )
               )
         );
   }

   protected LootTable.Builder createPetalsDrops(Block var1) {
      return LootTable.lootTable()
         .withPool(
            LootPool.lootPool()
               .setRolls(ConstantValue.exactly(1.0F))
               .add(
                  this.applyExplosionDecay(
                     var1,
                     LootItem.lootTableItem(var1)
                        .apply(
                           IntStream.rangeClosed(1, 4).boxed().toList(),
                           var1x -> SetItemCountFunction.setCount(ConstantValue.exactly((float)var1x.intValue()))
                                 .when(
                                    LootItemBlockStatePropertyCondition.hasBlockStateProperties(var1)
                                       .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(PinkPetalsBlock.AMOUNT, var1x.intValue()))
                                 )
                        )
                  )
               )
         );
   }

   protected static LootTable.Builder createCandleCakeDrops(Block var0) {
      return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(var0)));
   }

   public static LootTable.Builder noDrop() {
      return LootTable.lootTable();
   }

   protected abstract void generate();

   @Override
   public void generate(BiConsumer<ResourceLocation, LootTable.Builder> var1) {
      this.generate();
      HashSet var2 = new HashSet();

      for(Block var4 : BuiltInRegistries.BLOCK) {
         if (var4.isEnabled(this.enabledFeatures)) {
            ResourceLocation var5 = var4.getLootTable();
            if (var5 != BuiltInLootTables.EMPTY && var2.add(var5)) {
               LootTable.Builder var6 = this.map.remove(var5);
               if (var6 == null) {
                  throw new IllegalStateException(String.format(Locale.ROOT, "Missing loottable '%s' for '%s'", var5, BuiltInRegistries.BLOCK.getKey(var4)));
               }

               var1.accept(var5, var6);
            }
         }
      }

      if (!this.map.isEmpty()) {
         throw new IllegalStateException("Created block loot tables for non-blocks: " + this.map.keySet());
      }
   }

   protected void addNetherVinesDropTable(Block var1, Block var2) {
      LootTable.Builder var3 = createSilkTouchOrShearsDispatchTable(
         var1, LootItem.lootTableItem(var1).when(BonusLevelTableCondition.bonusLevelFlatChance(Enchantments.BLOCK_FORTUNE, 0.33F, 0.55F, 0.77F, 1.0F))
      );
      this.add(var1, var3);
      this.add(var2, var3);
   }

   protected LootTable.Builder createDoorTable(Block var1) {
      return this.createSinglePropConditionTable(var1, DoorBlock.HALF, DoubleBlockHalf.LOWER);
   }

   protected void dropPottedContents(Block var1) {
      this.add(var1, var1x -> this.createPotFlowerItemTable(((FlowerPotBlock)var1x).getContent()));
   }

   protected void otherWhenSilkTouch(Block var1, Block var2) {
      this.add(var1, createSilkTouchOnlyTable(var2));
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
}
