package net.minecraft.core.registries;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.EntitySubPredicates;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.DefaultedMappedRegistry;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.network.chat.numbers.NumberFormatTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Instruments;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockTypes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPatterns;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerators;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBindings;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.saveddata.maps.MapDecorationTypes;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.nbt.NbtProviders;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import net.minecraft.world.level.storage.loot.providers.score.ScoreboardNameProviders;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class BuiltInRegistries {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   private static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry<>(
      ResourceKey.createRegistryKey(Registries.ROOT_REGISTRY_NAME), Lifecycle.stable()
   );
   public static final DefaultedRegistry<GameEvent> GAME_EVENT = registerDefaulted(Registries.GAME_EVENT, "step", GameEvent::bootstrap);
   public static final Registry<SoundEvent> SOUND_EVENT = registerSimple(Registries.SOUND_EVENT, var0 -> SoundEvents.ITEM_PICKUP);
   public static final DefaultedRegistry<Fluid> FLUID = registerDefaultedWithIntrusiveHolders(Registries.FLUID, "empty", var0 -> Fluids.EMPTY);
   public static final Registry<MobEffect> MOB_EFFECT = registerSimple(Registries.MOB_EFFECT, MobEffects::bootstrap);
   public static final DefaultedRegistry<Block> BLOCK = registerDefaultedWithIntrusiveHolders(Registries.BLOCK, "air", var0 -> Blocks.AIR);
   public static final Registry<Enchantment> ENCHANTMENT = registerSimpleWithIntrusiveHolders(Registries.ENCHANTMENT, var0 -> Enchantments.FORTUNE);
   public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = registerDefaultedWithIntrusiveHolders(
      Registries.ENTITY_TYPE, "pig", var0 -> EntityType.PIG
   );
   public static final DefaultedRegistry<Item> ITEM = registerDefaultedWithIntrusiveHolders(Registries.ITEM, "air", var0 -> Items.AIR);
   public static final Registry<Potion> POTION = registerSimple(Registries.POTION, Potions::bootstrap);
   public static final Registry<ParticleType<?>> PARTICLE_TYPE = registerSimple(Registries.PARTICLE_TYPE, var0 -> ParticleTypes.BLOCK);
   public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = registerSimpleWithIntrusiveHolders(
      Registries.BLOCK_ENTITY_TYPE, var0 -> BlockEntityType.FURNACE
   );
   public static final DefaultedRegistry<PaintingVariant> PAINTING_VARIANT = registerDefaulted(
      Registries.PAINTING_VARIANT, "kebab", PaintingVariants::bootstrap
   );
   public static final Registry<ResourceLocation> CUSTOM_STAT = registerSimple(Registries.CUSTOM_STAT, var0 -> Stats.JUMP);
   public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = registerDefaulted(Registries.CHUNK_STATUS, "empty", var0 -> ChunkStatus.EMPTY);
   public static final Registry<RuleTestType<?>> RULE_TEST = registerSimple(Registries.RULE_TEST, var0 -> RuleTestType.ALWAYS_TRUE_TEST);
   public static final Registry<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIER = registerSimple(
      Registries.RULE_BLOCK_ENTITY_MODIFIER, var0 -> RuleBlockEntityModifierType.PASSTHROUGH
   );
   public static final Registry<PosRuleTestType<?>> POS_RULE_TEST = registerSimple(Registries.POS_RULE_TEST, var0 -> PosRuleTestType.ALWAYS_TRUE_TEST);
   public static final Registry<MenuType<?>> MENU = registerSimple(Registries.MENU, var0 -> MenuType.ANVIL);
   public static final Registry<RecipeType<?>> RECIPE_TYPE = registerSimple(Registries.RECIPE_TYPE, var0 -> RecipeType.CRAFTING);
   public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = registerSimple(Registries.RECIPE_SERIALIZER, var0 -> RecipeSerializer.SHAPELESS_RECIPE);
   public static final Registry<Attribute> ATTRIBUTE = registerSimple(Registries.ATTRIBUTE, Attributes::bootstrap);
   public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE = registerSimple(Registries.POSITION_SOURCE_TYPE, var0 -> PositionSourceType.BLOCK);
   public static final Registry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = registerSimple(Registries.COMMAND_ARGUMENT_TYPE, ArgumentTypeInfos::bootstrap);
   public static final Registry<StatType<?>> STAT_TYPE = registerSimple(Registries.STAT_TYPE, var0 -> Stats.ITEM_USED);
   public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = registerDefaulted(Registries.VILLAGER_TYPE, "plains", var0 -> VillagerType.PLAINS);
   public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = registerDefaulted(
      Registries.VILLAGER_PROFESSION, "none", var0 -> VillagerProfession.NONE
   );
   public static final Registry<PoiType> POINT_OF_INTEREST_TYPE = registerSimple(Registries.POINT_OF_INTEREST_TYPE, PoiTypes::bootstrap);
   public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = registerDefaulted(
      Registries.MEMORY_MODULE_TYPE, "dummy", var0 -> MemoryModuleType.DUMMY
   );
   public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = registerDefaulted(Registries.SENSOR_TYPE, "dummy", var0 -> SensorType.DUMMY);
   public static final Registry<Schedule> SCHEDULE = registerSimple(Registries.SCHEDULE, var0 -> Schedule.EMPTY);
   public static final Registry<Activity> ACTIVITY = registerSimple(Registries.ACTIVITY, var0 -> Activity.IDLE);
   public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = registerSimple(Registries.LOOT_POOL_ENTRY_TYPE, var0 -> LootPoolEntries.EMPTY);
   public static final Registry<LootItemFunctionType> LOOT_FUNCTION_TYPE = registerSimple(Registries.LOOT_FUNCTION_TYPE, var0 -> LootItemFunctions.SET_COUNT);
   public static final Registry<LootItemConditionType> LOOT_CONDITION_TYPE = registerSimple(Registries.LOOT_CONDITION_TYPE, var0 -> LootItemConditions.INVERTED);
   public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = registerSimple(
      Registries.LOOT_NUMBER_PROVIDER_TYPE, var0 -> NumberProviders.CONSTANT
   );
   public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = registerSimple(Registries.LOOT_NBT_PROVIDER_TYPE, var0 -> NbtProviders.CONTEXT);
   public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = registerSimple(
      Registries.LOOT_SCORE_PROVIDER_TYPE, var0 -> ScoreboardNameProviders.CONTEXT
   );
   public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = registerSimple(Registries.FLOAT_PROVIDER_TYPE, var0 -> FloatProviderType.CONSTANT);
   public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPE = registerSimple(Registries.INT_PROVIDER_TYPE, var0 -> IntProviderType.CONSTANT);
   public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = registerSimple(
      Registries.HEIGHT_PROVIDER_TYPE, var0 -> HeightProviderType.CONSTANT
   );
   public static final Registry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = registerSimple(Registries.BLOCK_PREDICATE_TYPE, var0 -> BlockPredicateType.NOT);
   public static final Registry<WorldCarver<?>> CARVER = registerSimple(Registries.CARVER, var0 -> WorldCarver.CAVE);
   public static final Registry<Feature<?>> FEATURE = registerSimple(Registries.FEATURE, var0 -> Feature.ORE);
   public static final Registry<StructurePlacementType<?>> STRUCTURE_PLACEMENT = registerSimple(
      Registries.STRUCTURE_PLACEMENT, var0 -> StructurePlacementType.RANDOM_SPREAD
   );
   public static final Registry<StructurePieceType> STRUCTURE_PIECE = registerSimple(Registries.STRUCTURE_PIECE, var0 -> StructurePieceType.MINE_SHAFT_ROOM);
   public static final Registry<StructureType<?>> STRUCTURE_TYPE = registerSimple(Registries.STRUCTURE_TYPE, var0 -> StructureType.JIGSAW);
   public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = registerSimple(
      Registries.PLACEMENT_MODIFIER_TYPE, var0 -> PlacementModifierType.COUNT
   );
   public static final Registry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPE = registerSimple(
      Registries.BLOCK_STATE_PROVIDER_TYPE, var0 -> BlockStateProviderType.SIMPLE_STATE_PROVIDER
   );
   public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = registerSimple(
      Registries.FOLIAGE_PLACER_TYPE, var0 -> FoliagePlacerType.BLOB_FOLIAGE_PLACER
   );
   public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = registerSimple(
      Registries.TRUNK_PLACER_TYPE, var0 -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER
   );
   public static final Registry<RootPlacerType<?>> ROOT_PLACER_TYPE = registerSimple(Registries.ROOT_PLACER_TYPE, var0 -> RootPlacerType.MANGROVE_ROOT_PLACER);
   public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = registerSimple(Registries.TREE_DECORATOR_TYPE, var0 -> TreeDecoratorType.LEAVE_VINE);
   public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPE = registerSimple(
      Registries.FEATURE_SIZE_TYPE, var0 -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE
   );
   public static final Registry<MapCodec<? extends BiomeSource>> BIOME_SOURCE = registerSimple(Registries.BIOME_SOURCE, BiomeSources::bootstrap);
   public static final Registry<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATOR = registerSimple(Registries.CHUNK_GENERATOR, ChunkGenerators::bootstrap);
   public static final Registry<MapCodec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION = registerSimple(
      Registries.MATERIAL_CONDITION, SurfaceRules.ConditionSource::bootstrap
   );
   public static final Registry<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE = registerSimple(
      Registries.MATERIAL_RULE, SurfaceRules.RuleSource::bootstrap
   );
   public static final Registry<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = registerSimple(
      Registries.DENSITY_FUNCTION_TYPE, DensityFunctions::bootstrap
   );
   public static final Registry<MapCodec<? extends Block>> BLOCK_TYPE = registerSimple(Registries.BLOCK_TYPE, BlockTypes::bootstrap);
   public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = registerSimple(
      Registries.STRUCTURE_PROCESSOR, var0 -> StructureProcessorType.BLOCK_IGNORE
   );
   public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = registerSimple(
      Registries.STRUCTURE_POOL_ELEMENT, var0 -> StructurePoolElementType.EMPTY
   );
   public static final Registry<MapCodec<? extends PoolAliasBinding>> POOL_ALIAS_BINDING_TYPE = registerSimple(
      Registries.POOL_ALIAS_BINDING, PoolAliasBindings::bootstrap
   );
   public static final Registry<CatVariant> CAT_VARIANT = registerSimple(Registries.CAT_VARIANT, CatVariant::bootstrap);
   public static final Registry<FrogVariant> FROG_VARIANT = registerSimple(Registries.FROG_VARIANT, FrogVariant::bootstrap);
   public static final Registry<Instrument> INSTRUMENT = registerSimple(Registries.INSTRUMENT, Instruments::bootstrap);
   public static final Registry<String> DECORATED_POT_PATTERNS = registerSimple(Registries.DECORATED_POT_PATTERNS, DecoratedPotPatterns::bootstrap);
   public static final Registry<CreativeModeTab> CREATIVE_MODE_TAB = registerSimple(Registries.CREATIVE_MODE_TAB, CreativeModeTabs::bootstrap);
   public static final Registry<CriterionTrigger<?>> TRIGGER_TYPES = registerSimple(Registries.TRIGGER_TYPE, CriteriaTriggers::bootstrap);
   public static final Registry<NumberFormatType<?>> NUMBER_FORMAT_TYPE = registerSimple(Registries.NUMBER_FORMAT_TYPE, NumberFormatTypes::bootstrap);
   public static final Registry<ArmorMaterial> ARMOR_MATERIAL = registerSimple(Registries.ARMOR_MATERIAL, ArmorMaterials::bootstrap);
   public static final Registry<DataComponentType<?>> DATA_COMPONENT_TYPE = registerSimple(Registries.DATA_COMPONENT_TYPE, DataComponents::bootstrap);
   public static final Registry<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATE_TYPE = registerSimple(
      Registries.ENTITY_SUB_PREDICATE_TYPE, EntitySubPredicates::bootstrap
   );
   public static final Registry<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATE_TYPE = registerSimple(
      Registries.ITEM_SUB_PREDICATE_TYPE, ItemSubPredicates::bootstrap
   );
   public static final Registry<MapDecorationType> MAP_DECORATION_TYPE = registerSimple(Registries.MAP_DECORATION_TYPE, MapDecorationTypes::bootstrap);
   public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;

   public BuiltInRegistries() {
      super();
   }

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> var0, BuiltInRegistries.RegistryBootstrap<T> var1) {
      return internalRegister(var0, new MappedRegistry<>(var0, Lifecycle.stable(), false), var1);
   }

   private static <T> Registry<T> registerSimpleWithIntrusiveHolders(ResourceKey<? extends Registry<T>> var0, BuiltInRegistries.RegistryBootstrap<T> var1) {
      return internalRegister(var0, new MappedRegistry<>(var0, Lifecycle.stable(), true), var1);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(ResourceKey<? extends Registry<T>> var0, String var1, BuiltInRegistries.RegistryBootstrap<T> var2) {
      return internalRegister(var0, new DefaultedMappedRegistry<>(var1, var0, Lifecycle.stable(), false), var2);
   }

   private static <T> DefaultedRegistry<T> registerDefaultedWithIntrusiveHolders(
      ResourceKey<? extends Registry<T>> var0, String var1, BuiltInRegistries.RegistryBootstrap<T> var2
   ) {
      return internalRegister(var0, new DefaultedMappedRegistry<>(var1, var0, Lifecycle.stable(), true), var2);
   }

   private static <T, R extends WritableRegistry<T>> R internalRegister(
      ResourceKey<? extends Registry<T>> var0, R var1, BuiltInRegistries.RegistryBootstrap<T> var2
   ) {
      Bootstrap.checkBootstrapCalled(() -> "registry " + var0);
      ResourceLocation var3 = var0.location();
      LOADERS.put(var3, () -> var2.run(var1));
      WRITABLE_REGISTRY.register(var0, var1, RegistrationInfo.BUILT_IN);
      return (R)var1;
   }

   public static void bootStrap() {
      createContents();
      freeze();
      validate(REGISTRY);
   }

   private static void createContents() {
      LOADERS.forEach((var0, var1) -> {
         if (var1.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", var0);
         }
      });
   }

   private static void freeze() {
      REGISTRY.freeze();

      for(Registry var1 : REGISTRY) {
         var1.freeze();
      }
   }

   private static <T extends Registry<?>> void validate(Registry<T> var0) {
      var0.forEach(var1 -> {
         if (var1.keySet().isEmpty()) {
            Util.logAndPauseIfInIde("Registry '" + var0.getKey(var1) + "' was empty after loading");
         }

         if (var1 instanceof DefaultedRegistry) {
            ResourceLocation var2 = ((DefaultedRegistry)var1).getDefaultKey();
            Validate.notNull(var1.get(var2), "Missing default of DefaultedMappedRegistry: " + var2, new Object[0]);
         }
      });
   }

   @FunctionalInterface
   interface RegistryBootstrap<T> {
      Object run(Registry<T> var1);
   }
}
