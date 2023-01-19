package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSources;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGenerators;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
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

public abstract class Registry<T> implements Keyable, IdMap<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   public static final ResourceLocation ROOT_REGISTRY_NAME = new ResourceLocation("root");
   protected static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry<>(
      createRegistryKey("root"), Lifecycle.experimental(), null
   );
   public static final Registry<? extends Registry<?>> REGISTRY = WRITABLE_REGISTRY;
   public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENT_REGISTRY = createRegistryKey("sound_event");
   public static final ResourceKey<Registry<Fluid>> FLUID_REGISTRY = createRegistryKey("fluid");
   public static final ResourceKey<Registry<MobEffect>> MOB_EFFECT_REGISTRY = createRegistryKey("mob_effect");
   public static final ResourceKey<Registry<Block>> BLOCK_REGISTRY = createRegistryKey("block");
   public static final ResourceKey<Registry<Enchantment>> ENCHANTMENT_REGISTRY = createRegistryKey("enchantment");
   public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPE_REGISTRY = createRegistryKey("entity_type");
   public static final ResourceKey<Registry<Item>> ITEM_REGISTRY = createRegistryKey("item");
   public static final ResourceKey<Registry<Potion>> POTION_REGISTRY = createRegistryKey("potion");
   public static final ResourceKey<Registry<ParticleType<?>>> PARTICLE_TYPE_REGISTRY = createRegistryKey("particle_type");
   public static final ResourceKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPE_REGISTRY = createRegistryKey("block_entity_type");
   public static final ResourceKey<Registry<PaintingVariant>> PAINTING_VARIANT_REGISTRY = createRegistryKey("painting_variant");
   public static final ResourceKey<Registry<ResourceLocation>> CUSTOM_STAT_REGISTRY = createRegistryKey("custom_stat");
   public static final ResourceKey<Registry<ChunkStatus>> CHUNK_STATUS_REGISTRY = createRegistryKey("chunk_status");
   public static final ResourceKey<Registry<RuleTestType<?>>> RULE_TEST_REGISTRY = createRegistryKey("rule_test");
   public static final ResourceKey<Registry<PosRuleTestType<?>>> POS_RULE_TEST_REGISTRY = createRegistryKey("pos_rule_test");
   public static final ResourceKey<Registry<MenuType<?>>> MENU_REGISTRY = createRegistryKey("menu");
   public static final ResourceKey<Registry<RecipeType<?>>> RECIPE_TYPE_REGISTRY = createRegistryKey("recipe_type");
   public static final ResourceKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZER_REGISTRY = createRegistryKey("recipe_serializer");
   public static final ResourceKey<Registry<Attribute>> ATTRIBUTE_REGISTRY = createRegistryKey("attribute");
   public static final ResourceKey<Registry<GameEvent>> GAME_EVENT_REGISTRY = createRegistryKey("game_event");
   public static final ResourceKey<Registry<PositionSourceType<?>>> POSITION_SOURCE_TYPE_REGISTRY = createRegistryKey("position_source_type");
   public static final ResourceKey<Registry<StatType<?>>> STAT_TYPE_REGISTRY = createRegistryKey("stat_type");
   public static final ResourceKey<Registry<VillagerType>> VILLAGER_TYPE_REGISTRY = createRegistryKey("villager_type");
   public static final ResourceKey<Registry<VillagerProfession>> VILLAGER_PROFESSION_REGISTRY = createRegistryKey("villager_profession");
   public static final ResourceKey<Registry<PoiType>> POINT_OF_INTEREST_TYPE_REGISTRY = createRegistryKey("point_of_interest_type");
   public static final ResourceKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE_REGISTRY = createRegistryKey("memory_module_type");
   public static final ResourceKey<Registry<SensorType<?>>> SENSOR_TYPE_REGISTRY = createRegistryKey("sensor_type");
   public static final ResourceKey<Registry<Schedule>> SCHEDULE_REGISTRY = createRegistryKey("schedule");
   public static final ResourceKey<Registry<Activity>> ACTIVITY_REGISTRY = createRegistryKey("activity");
   public static final ResourceKey<Registry<LootPoolEntryType>> LOOT_ENTRY_REGISTRY = createRegistryKey("loot_pool_entry_type");
   public static final ResourceKey<Registry<LootItemFunctionType>> LOOT_FUNCTION_REGISTRY = createRegistryKey("loot_function_type");
   public static final ResourceKey<Registry<LootItemConditionType>> LOOT_ITEM_REGISTRY = createRegistryKey("loot_condition_type");
   public static final ResourceKey<Registry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_REGISTRY = createRegistryKey("loot_number_provider_type");
   public static final ResourceKey<Registry<LootNbtProviderType>> LOOT_NBT_PROVIDER_REGISTRY = createRegistryKey("loot_nbt_provider_type");
   public static final ResourceKey<Registry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_REGISTRY = createRegistryKey("loot_score_provider_type");
   public static final ResourceKey<Registry<ArgumentTypeInfo<?, ?>>> COMMAND_ARGUMENT_TYPE_REGISTRY = createRegistryKey("command_argument_type");
   public static final ResourceKey<Registry<DimensionType>> DIMENSION_TYPE_REGISTRY = createRegistryKey("dimension_type");
   public static final ResourceKey<Registry<Level>> DIMENSION_REGISTRY = createRegistryKey("dimension");
   public static final ResourceKey<Registry<LevelStem>> LEVEL_STEM_REGISTRY = createRegistryKey("dimension");
   public static final DefaultedRegistry<GameEvent> GAME_EVENT = registerDefaulted(
      GAME_EVENT_REGISTRY, "step", GameEvent::builtInRegistryHolder, var0 -> GameEvent.STEP
   );
   public static final Registry<SoundEvent> SOUND_EVENT = registerSimple(SOUND_EVENT_REGISTRY, var0 -> SoundEvents.ITEM_PICKUP);
   public static final DefaultedRegistry<Fluid> FLUID = registerDefaulted(FLUID_REGISTRY, "empty", Fluid::builtInRegistryHolder, var0 -> Fluids.EMPTY);
   public static final Registry<MobEffect> MOB_EFFECT = registerSimple(MOB_EFFECT_REGISTRY, var0 -> MobEffects.LUCK);
   public static final DefaultedRegistry<Block> BLOCK = registerDefaulted(BLOCK_REGISTRY, "air", Block::builtInRegistryHolder, var0 -> Blocks.AIR);
   public static final Registry<Enchantment> ENCHANTMENT = registerSimple(ENCHANTMENT_REGISTRY, var0 -> Enchantments.BLOCK_FORTUNE);
   public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = registerDefaulted(
      ENTITY_TYPE_REGISTRY, "pig", EntityType::builtInRegistryHolder, var0 -> EntityType.PIG
   );
   public static final DefaultedRegistry<Item> ITEM = registerDefaulted(ITEM_REGISTRY, "air", Item::builtInRegistryHolder, var0 -> Items.AIR);
   public static final DefaultedRegistry<Potion> POTION = registerDefaulted(POTION_REGISTRY, "empty", var0 -> Potions.EMPTY);
   public static final Registry<ParticleType<?>> PARTICLE_TYPE = registerSimple(PARTICLE_TYPE_REGISTRY, var0 -> ParticleTypes.BLOCK);
   public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = registerSimple(BLOCK_ENTITY_TYPE_REGISTRY, var0 -> BlockEntityType.FURNACE);
   public static final DefaultedRegistry<PaintingVariant> PAINTING_VARIANT = registerDefaulted(PAINTING_VARIANT_REGISTRY, "kebab", PaintingVariants::bootstrap);
   public static final Registry<ResourceLocation> CUSTOM_STAT = registerSimple(CUSTOM_STAT_REGISTRY, var0 -> Stats.JUMP);
   public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = registerDefaulted(CHUNK_STATUS_REGISTRY, "empty", var0 -> ChunkStatus.EMPTY);
   public static final Registry<RuleTestType<?>> RULE_TEST = registerSimple(RULE_TEST_REGISTRY, var0 -> RuleTestType.ALWAYS_TRUE_TEST);
   public static final Registry<PosRuleTestType<?>> POS_RULE_TEST = registerSimple(POS_RULE_TEST_REGISTRY, var0 -> PosRuleTestType.ALWAYS_TRUE_TEST);
   public static final Registry<MenuType<?>> MENU = registerSimple(MENU_REGISTRY, var0 -> MenuType.ANVIL);
   public static final Registry<RecipeType<?>> RECIPE_TYPE = registerSimple(RECIPE_TYPE_REGISTRY, var0 -> RecipeType.CRAFTING);
   public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = registerSimple(RECIPE_SERIALIZER_REGISTRY, var0 -> RecipeSerializer.SHAPELESS_RECIPE);
   public static final Registry<Attribute> ATTRIBUTE = registerSimple(ATTRIBUTE_REGISTRY, var0 -> Attributes.LUCK);
   public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE = registerSimple(POSITION_SOURCE_TYPE_REGISTRY, var0 -> PositionSourceType.BLOCK);
   public static final Registry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = registerSimple(COMMAND_ARGUMENT_TYPE_REGISTRY, ArgumentTypeInfos::bootstrap);
   public static final Registry<StatType<?>> STAT_TYPE = registerSimple(STAT_TYPE_REGISTRY, var0 -> Stats.ITEM_USED);
   public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = registerDefaulted(VILLAGER_TYPE_REGISTRY, "plains", var0 -> VillagerType.PLAINS);
   public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = registerDefaulted(
      VILLAGER_PROFESSION_REGISTRY, "none", var0 -> VillagerProfession.NONE
   );
   public static final Registry<PoiType> POINT_OF_INTEREST_TYPE = registerSimple(POINT_OF_INTEREST_TYPE_REGISTRY, PoiTypes::bootstrap);
   public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = registerDefaulted(
      MEMORY_MODULE_TYPE_REGISTRY, "dummy", var0 -> MemoryModuleType.DUMMY
   );
   public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = registerDefaulted(SENSOR_TYPE_REGISTRY, "dummy", var0 -> SensorType.DUMMY);
   public static final Registry<Schedule> SCHEDULE = registerSimple(SCHEDULE_REGISTRY, var0 -> Schedule.EMPTY);
   public static final Registry<Activity> ACTIVITY = registerSimple(ACTIVITY_REGISTRY, var0 -> Activity.IDLE);
   public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = registerSimple(LOOT_ENTRY_REGISTRY, var0 -> LootPoolEntries.EMPTY);
   public static final Registry<LootItemFunctionType> LOOT_FUNCTION_TYPE = registerSimple(LOOT_FUNCTION_REGISTRY, var0 -> LootItemFunctions.SET_COUNT);
   public static final Registry<LootItemConditionType> LOOT_CONDITION_TYPE = registerSimple(LOOT_ITEM_REGISTRY, var0 -> LootItemConditions.INVERTED);
   public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = registerSimple(
      LOOT_NUMBER_PROVIDER_REGISTRY, var0 -> NumberProviders.CONSTANT
   );
   public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = registerSimple(LOOT_NBT_PROVIDER_REGISTRY, var0 -> NbtProviders.CONTEXT);
   public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = registerSimple(
      LOOT_SCORE_PROVIDER_REGISTRY, var0 -> ScoreboardNameProviders.CONTEXT
   );
   public static final ResourceKey<Registry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE_REGISTRY = createRegistryKey("float_provider_type");
   public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPES = registerSimple(FLOAT_PROVIDER_TYPE_REGISTRY, var0 -> FloatProviderType.CONSTANT);
   public static final ResourceKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE_REGISTRY = createRegistryKey("int_provider_type");
   public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPES = registerSimple(INT_PROVIDER_TYPE_REGISTRY, var0 -> IntProviderType.CONSTANT);
   public static final ResourceKey<Registry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE_REGISTRY = createRegistryKey("height_provider_type");
   public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPES = registerSimple(
      HEIGHT_PROVIDER_TYPE_REGISTRY, var0 -> HeightProviderType.CONSTANT
   );
   public static final ResourceKey<Registry<BlockPredicateType<?>>> BLOCK_PREDICATE_TYPE_REGISTRY = createRegistryKey("block_predicate_type");
   public static final Registry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPES = registerSimple(BLOCK_PREDICATE_TYPE_REGISTRY, var0 -> BlockPredicateType.NOT);
   public static final ResourceKey<Registry<NoiseGeneratorSettings>> NOISE_GENERATOR_SETTINGS_REGISTRY = createRegistryKey("worldgen/noise_settings");
   public static final ResourceKey<Registry<ConfiguredWorldCarver<?>>> CONFIGURED_CARVER_REGISTRY = createRegistryKey("worldgen/configured_carver");
   public static final ResourceKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE_REGISTRY = createRegistryKey("worldgen/configured_feature");
   public static final ResourceKey<Registry<PlacedFeature>> PLACED_FEATURE_REGISTRY = createRegistryKey("worldgen/placed_feature");
   public static final ResourceKey<Registry<Structure>> STRUCTURE_REGISTRY = createRegistryKey("worldgen/structure");
   public static final ResourceKey<Registry<StructureSet>> STRUCTURE_SET_REGISTRY = createRegistryKey("worldgen/structure_set");
   public static final ResourceKey<Registry<StructureProcessorList>> PROCESSOR_LIST_REGISTRY = createRegistryKey("worldgen/processor_list");
   public static final ResourceKey<Registry<StructureTemplatePool>> TEMPLATE_POOL_REGISTRY = createRegistryKey("worldgen/template_pool");
   public static final ResourceKey<Registry<Biome>> BIOME_REGISTRY = createRegistryKey("worldgen/biome");
   public static final ResourceKey<Registry<NormalNoise.NoiseParameters>> NOISE_REGISTRY = createRegistryKey("worldgen/noise");
   public static final ResourceKey<Registry<DensityFunction>> DENSITY_FUNCTION_REGISTRY = createRegistryKey("worldgen/density_function");
   public static final ResourceKey<Registry<WorldPreset>> WORLD_PRESET_REGISTRY = createRegistryKey("worldgen/world_preset");
   public static final ResourceKey<Registry<FlatLevelGeneratorPreset>> FLAT_LEVEL_GENERATOR_PRESET_REGISTRY = createRegistryKey(
      "worldgen/flat_level_generator_preset"
   );
   public static final ResourceKey<Registry<WorldCarver<?>>> CARVER_REGISTRY = createRegistryKey("worldgen/carver");
   public static final Registry<WorldCarver<?>> CARVER = registerSimple(CARVER_REGISTRY, var0 -> WorldCarver.CAVE);
   public static final ResourceKey<Registry<Feature<?>>> FEATURE_REGISTRY = createRegistryKey("worldgen/feature");
   public static final Registry<Feature<?>> FEATURE = registerSimple(FEATURE_REGISTRY, var0 -> Feature.ORE);
   public static final ResourceKey<Registry<StructurePlacementType<?>>> STRUCTURE_PLACEMENT_TYPE_REGISTRY = createRegistryKey("worldgen/structure_placement");
   public static final Registry<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPE = registerSimple(
      STRUCTURE_PLACEMENT_TYPE_REGISTRY, var0 -> StructurePlacementType.RANDOM_SPREAD
   );
   public static final ResourceKey<Registry<StructurePieceType>> STRUCTURE_PIECE_REGISTRY = createRegistryKey("worldgen/structure_piece");
   public static final Registry<StructurePieceType> STRUCTURE_PIECE = registerSimple(STRUCTURE_PIECE_REGISTRY, var0 -> StructurePieceType.MINE_SHAFT_ROOM);
   public static final ResourceKey<Registry<StructureType<?>>> STRUCTURE_TYPE_REGISTRY = createRegistryKey("worldgen/structure_type");
   public static final Registry<StructureType<?>> STRUCTURE_TYPES = registerSimple(STRUCTURE_TYPE_REGISTRY, var0 -> StructureType.JIGSAW);
   public static final ResourceKey<Registry<PlacementModifierType<?>>> PLACEMENT_MODIFIER_REGISTRY = createRegistryKey("worldgen/placement_modifier_type");
   public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIERS = registerSimple(
      PLACEMENT_MODIFIER_REGISTRY, var0 -> PlacementModifierType.COUNT
   );
   public static final ResourceKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE_REGISTRY = createRegistryKey(
      "worldgen/block_state_provider_type"
   );
   public static final ResourceKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/foliage_placer_type");
   public static final ResourceKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/trunk_placer_type");
   public static final ResourceKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE_REGISTRY = createRegistryKey("worldgen/tree_decorator_type");
   public static final ResourceKey<Registry<RootPlacerType<?>>> ROOT_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/root_placer_type");
   public static final ResourceKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_REGISTRY = createRegistryKey("worldgen/feature_size_type");
   public static final ResourceKey<Registry<Codec<? extends BiomeSource>>> BIOME_SOURCE_REGISTRY = createRegistryKey("worldgen/biome_source");
   public static final ResourceKey<Registry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_REGISTRY = createRegistryKey("worldgen/chunk_generator");
   public static final ResourceKey<Registry<Codec<? extends SurfaceRules.ConditionSource>>> CONDITION_REGISTRY = createRegistryKey(
      "worldgen/material_condition"
   );
   public static final ResourceKey<Registry<Codec<? extends SurfaceRules.RuleSource>>> RULE_REGISTRY = createRegistryKey("worldgen/material_rule");
   public static final ResourceKey<Registry<Codec<? extends DensityFunction>>> DENSITY_FUNCTION_TYPE_REGISTRY = createRegistryKey(
      "worldgen/density_function_type"
   );
   public static final ResourceKey<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR_REGISTRY = createRegistryKey("worldgen/structure_processor");
   public static final ResourceKey<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT_REGISTRY = createRegistryKey(
      "worldgen/structure_pool_element"
   );
   public static final Registry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPES = registerSimple(
      BLOCK_STATE_PROVIDER_TYPE_REGISTRY, var0 -> BlockStateProviderType.SIMPLE_STATE_PROVIDER
   );
   public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES = registerSimple(
      FOLIAGE_PLACER_TYPE_REGISTRY, var0 -> FoliagePlacerType.BLOB_FOLIAGE_PLACER
   );
   public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPES = registerSimple(
      TRUNK_PLACER_TYPE_REGISTRY, var0 -> TrunkPlacerType.STRAIGHT_TRUNK_PLACER
   );
   public static final Registry<RootPlacerType<?>> ROOT_PLACER_TYPES = registerSimple(ROOT_PLACER_TYPE_REGISTRY, var0 -> RootPlacerType.MANGROVE_ROOT_PLACER);
   public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES = registerSimple(TREE_DECORATOR_TYPE_REGISTRY, var0 -> TreeDecoratorType.LEAVE_VINE);
   public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPES = registerSimple(
      FEATURE_SIZE_TYPE_REGISTRY, var0 -> FeatureSizeType.TWO_LAYERS_FEATURE_SIZE
   );
   public static final Registry<Codec<? extends BiomeSource>> BIOME_SOURCE = registerSimple(BIOME_SOURCE_REGISTRY, Lifecycle.stable(), BiomeSources::bootstrap);
   public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR = registerSimple(
      CHUNK_GENERATOR_REGISTRY, Lifecycle.stable(), ChunkGenerators::bootstrap
   );
   public static final Registry<Codec<? extends SurfaceRules.ConditionSource>> CONDITION = registerSimple(
      CONDITION_REGISTRY, SurfaceRules.ConditionSource::bootstrap
   );
   public static final Registry<Codec<? extends SurfaceRules.RuleSource>> RULE = registerSimple(RULE_REGISTRY, SurfaceRules.RuleSource::bootstrap);
   public static final Registry<Codec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES = registerSimple(
      DENSITY_FUNCTION_TYPE_REGISTRY, DensityFunctions::bootstrap
   );
   public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR = registerSimple(
      STRUCTURE_PROCESSOR_REGISTRY, var0 -> StructureProcessorType.BLOCK_IGNORE
   );
   public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = registerSimple(
      STRUCTURE_POOL_ELEMENT_REGISTRY, var0 -> StructurePoolElementType.EMPTY
   );
   public static final ResourceKey<Registry<ChatType>> CHAT_TYPE_REGISTRY = createRegistryKey("chat_type");
   public static final ResourceKey<Registry<CatVariant>> CAT_VARIANT_REGISTRY = createRegistryKey("cat_variant");
   public static final Registry<CatVariant> CAT_VARIANT = registerSimple(CAT_VARIANT_REGISTRY, var0 -> CatVariant.BLACK);
   public static final ResourceKey<Registry<FrogVariant>> FROG_VARIANT_REGISTRY = createRegistryKey("frog_variant");
   public static final Registry<FrogVariant> FROG_VARIANT = registerSimple(FROG_VARIANT_REGISTRY, var0 -> FrogVariant.TEMPERATE);
   public static final ResourceKey<Registry<BannerPattern>> BANNER_PATTERN_REGISTRY = createRegistryKey("banner_pattern");
   public static final Registry<BannerPattern> BANNER_PATTERN = registerSimple(BANNER_PATTERN_REGISTRY, BannerPatterns::bootstrap);
   public static final ResourceKey<Registry<Instrument>> INSTRUMENT_REGISTRY = createRegistryKey("instrument");
   public static final Registry<Instrument> INSTRUMENT = registerSimple(INSTRUMENT_REGISTRY, Instruments::bootstrap);
   private final ResourceKey<? extends Registry<T>> key;
   private final Lifecycle lifecycle;

   private static <T> ResourceKey<Registry<T>> createRegistryKey(String var0) {
      return ResourceKey.createRegistryKey(new ResourceLocation(var0));
   }

   public static <T extends Registry<?>> void checkRegistry(Registry<T> var0) {
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

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> var0, Registry.RegistryBootstrap<T> var1) {
      return registerSimple(var0, Lifecycle.experimental(), var1);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(ResourceKey<? extends Registry<T>> var0, String var1, Registry.RegistryBootstrap<T> var2) {
      return registerDefaulted(var0, var1, Lifecycle.experimental(), var2);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(
      ResourceKey<? extends Registry<T>> var0, String var1, Function<T, Holder.Reference<T>> var2, Registry.RegistryBootstrap<T> var3
   ) {
      return registerDefaulted(var0, var1, Lifecycle.experimental(), var2, var3);
   }

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Registry.RegistryBootstrap<T> var2) {
      return internalRegister(var0, new MappedRegistry<>(var0, var1, null), var2, var1);
   }

   private static <T> Registry<T> registerSimple(
      ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Function<T, Holder.Reference<T>> var2, Registry.RegistryBootstrap<T> var3
   ) {
      return internalRegister(var0, new MappedRegistry<>(var0, var1, var2), var3, var1);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(
      ResourceKey<? extends Registry<T>> var0, String var1, Lifecycle var2, Registry.RegistryBootstrap<T> var3
   ) {
      return internalRegister(var0, new DefaultedRegistry<>(var1, var0, var2, null), var3, var2);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(
      ResourceKey<? extends Registry<T>> var0, String var1, Lifecycle var2, Function<T, Holder.Reference<T>> var3, Registry.RegistryBootstrap<T> var4
   ) {
      return internalRegister(var0, new DefaultedRegistry<>(var1, var0, var2, var3), var4, var2);
   }

   private static <T, R extends WritableRegistry<T>> R internalRegister(
      ResourceKey<? extends Registry<T>> var0, R var1, Registry.RegistryBootstrap<T> var2, Lifecycle var3
   ) {
      ResourceLocation var4 = var0.location();
      LOADERS.put(var4, () -> var2.run(var1));
      WRITABLE_REGISTRY.register(var0, var1, var3);
      return (R)var1;
   }

   protected Registry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2) {
      super();
      Bootstrap.checkBootstrapCalled(() -> "registry " + var1);
      this.key = var1;
      this.lifecycle = var2;
   }

   public static void freezeBuiltins() {
      for(Registry var1 : REGISTRY) {
         var1.freeze();
      }
   }

   public ResourceKey<? extends Registry<T>> key() {
      return this.key;
   }

   public Lifecycle lifecycle() {
      return this.lifecycle;
   }

   @Override
   public String toString() {
      return "Registry[" + this.key + " (" + this.lifecycle + ")]";
   }

   public Codec<T> byNameCodec() {
      Codec var1 = ResourceLocation.CODEC
         .flatXmap(
            var1x -> (DataResult)Optional.ofNullable(this.get(var1x))
                  .map(DataResult::success)
                  .orElseGet(() -> (T)DataResult.error("Unknown registry key in " + this.key + ": " + var1x)),
            var1x -> (DataResult)this.getResourceKey((T)var1x)
                  .map(ResourceKey::location)
                  .map(DataResult::success)
                  .orElseGet(() -> (T)DataResult.error("Unknown registry element in " + this.key + ":" + var1x))
         );
      Codec var2 = ExtraCodecs.idResolverCodec(var1x -> this.getResourceKey((T)var1x).isPresent() ? this.getId((T)var1x) : -1, this::byId, -1);
      return ExtraCodecs.overrideLifecycle(ExtraCodecs.orCompressed(var1, var2), this::lifecycle, var1x -> this.lifecycle);
   }

   public Codec<Holder<T>> holderByNameCodec() {
      Codec var1 = ResourceLocation.CODEC
         .flatXmap(
            var1x -> (DataResult)this.getHolder(ResourceKey.create(this.key, var1x))
                  .map(DataResult::success)
                  .orElseGet(() -> (T)DataResult.error("Unknown registry key in " + this.key + ": " + var1x)),
            var1x -> (DataResult)var1x.unwrapKey()
                  .map(ResourceKey::location)
                  .map(DataResult::success)
                  .orElseGet(() -> (T)DataResult.error("Unknown registry element in " + this.key + ":" + var1x))
         );
      return ExtraCodecs.overrideLifecycle(var1, var1x -> this.lifecycle(var1x.value()), var1x -> this.lifecycle);
   }

   public <U> Stream<U> keys(DynamicOps<U> var1) {
      return this.keySet().stream().map(var1x -> (U)var1.createString(var1x.toString()));
   }

   @Nullable
   public abstract ResourceLocation getKey(T var1);

   public abstract Optional<ResourceKey<T>> getResourceKey(T var1);

   @Override
   public abstract int getId(@Nullable T var1);

   @Nullable
   public abstract T get(@Nullable ResourceKey<T> var1);

   @Nullable
   public abstract T get(@Nullable ResourceLocation var1);

   public abstract Lifecycle lifecycle(T var1);

   public abstract Lifecycle elementsLifecycle();

   public Optional<T> getOptional(@Nullable ResourceLocation var1) {
      return Optional.ofNullable(this.get(var1));
   }

   public Optional<T> getOptional(@Nullable ResourceKey<T> var1) {
      return Optional.ofNullable(this.get(var1));
   }

   public T getOrThrow(ResourceKey<T> var1) {
      Object var2 = this.get(var1);
      if (var2 == null) {
         throw new IllegalStateException("Missing key in " + this.key + ": " + var1);
      } else {
         return (T)var2;
      }
   }

   public abstract Set<ResourceLocation> keySet();

   public abstract Set<Entry<ResourceKey<T>, T>> entrySet();

   public abstract Set<ResourceKey<T>> registryKeySet();

   public abstract Optional<Holder<T>> getRandom(RandomSource var1);

   public Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   public abstract boolean containsKey(ResourceLocation var1);

   public abstract boolean containsKey(ResourceKey<T> var1);

   public static <T> T register(Registry<? super T> var0, String var1, T var2) {
      return register(var0, new ResourceLocation(var1), (T)var2);
   }

   public static <V, T extends V> T register(Registry<V> var0, ResourceLocation var1, T var2) {
      return register(var0, ResourceKey.create(var0.key, var1), (T)var2);
   }

   public static <V, T extends V> T register(Registry<V> var0, ResourceKey<V> var1, T var2) {
      ((WritableRegistry)var0).register(var1, var2, Lifecycle.stable());
      return (T)var2;
   }

   public static <V, T extends V> T registerMapping(Registry<V> var0, int var1, String var2, T var3) {
      ((WritableRegistry)var0).registerMapping(var1, ResourceKey.create(var0.key, new ResourceLocation(var2)), var3, Lifecycle.stable());
      return (T)var3;
   }

   public abstract Registry<T> freeze();

   public abstract Holder<T> getOrCreateHolderOrThrow(ResourceKey<T> var1);

   public abstract DataResult<Holder<T>> getOrCreateHolder(ResourceKey<T> var1);

   public abstract Holder.Reference<T> createIntrusiveHolder(T var1);

   public abstract Optional<Holder<T>> getHolder(int var1);

   public abstract Optional<Holder<T>> getHolder(ResourceKey<T> var1);

   public Holder<T> getHolderOrThrow(ResourceKey<T> var1) {
      return this.getHolder(var1).orElseThrow(() -> new IllegalStateException("Missing key in " + this.key + ": " + var1));
   }

   public abstract Stream<Holder.Reference<T>> holders();

   public abstract Optional<HolderSet.Named<T>> getTag(TagKey<T> var1);

   public Iterable<Holder<T>> getTagOrEmpty(TagKey<T> var1) {
      return (Iterable<Holder<T>>)DataFixUtils.orElse(this.getTag(var1), List.of());
   }

   public abstract HolderSet.Named<T> getOrCreateTag(TagKey<T> var1);

   public abstract Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags();

   public abstract Stream<TagKey<T>> getTagNames();

   public abstract boolean isKnownTagName(TagKey<T> var1);

   public abstract void resetTags();

   public abstract void bindTags(Map<TagKey<T>, List<Holder<T>>> var1);

   public IdMap<Holder<T>> asHolderIdMap() {
      return new IdMap<Holder<T>>() {
         public int getId(Holder<T> var1) {
            return Registry.this.getId(var1.value());
         }

         @Nullable
         public Holder<T> byId(int var1) {
            return (Holder<T>)Registry.this.getHolder(var1).orElse((T)null);
         }

         @Override
         public int size() {
            return Registry.this.size();
         }

         @Override
         public Iterator<Holder<T>> iterator() {
            return Registry.this.holders().map(var0 -> var0).iterator();
         }
      };
   }

   static {
      BuiltinRegistries.bootstrap();
      LOADERS.forEach((var0, var1) -> {
         if (var1.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", var0);
         }
      });
      checkRegistry(WRITABLE_REGISTRY);
   }

   @FunctionalInterface
   interface RegistryBootstrap<T> {
      T run(Registry<T> var1);
   }
}
