package net.minecraft.core.registries;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
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
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentEntityEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentLocationBasedEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.EnchantmentProviderTypes;
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
   private static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY;
   public static final DefaultedRegistry<GameEvent> GAME_EVENT;
   public static final Registry<SoundEvent> SOUND_EVENT;
   public static final DefaultedRegistry<Fluid> FLUID;
   public static final Registry<MobEffect> MOB_EFFECT;
   public static final DefaultedRegistry<Block> BLOCK;
   public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE;
   public static final DefaultedRegistry<Item> ITEM;
   public static final Registry<Potion> POTION;
   public static final Registry<ParticleType<?>> PARTICLE_TYPE;
   public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE;
   public static final Registry<ResourceLocation> CUSTOM_STAT;
   public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS;
   public static final Registry<RuleTestType<?>> RULE_TEST;
   public static final Registry<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIER;
   public static final Registry<PosRuleTestType<?>> POS_RULE_TEST;
   public static final Registry<MenuType<?>> MENU;
   public static final Registry<RecipeType<?>> RECIPE_TYPE;
   public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER;
   public static final Registry<Attribute> ATTRIBUTE;
   public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE;
   public static final Registry<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE;
   public static final Registry<StatType<?>> STAT_TYPE;
   public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE;
   public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION;
   public static final Registry<PoiType> POINT_OF_INTEREST_TYPE;
   public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE;
   public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE;
   public static final Registry<Schedule> SCHEDULE;
   public static final Registry<Activity> ACTIVITY;
   public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE;
   public static final Registry<LootItemFunctionType<?>> LOOT_FUNCTION_TYPE;
   public static final Registry<LootItemConditionType> LOOT_CONDITION_TYPE;
   public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE;
   public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE;
   public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE;
   public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPE;
   public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPE;
   public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE;
   public static final Registry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE;
   public static final Registry<WorldCarver<?>> CARVER;
   public static final Registry<Feature<?>> FEATURE;
   public static final Registry<StructurePlacementType<?>> STRUCTURE_PLACEMENT;
   public static final Registry<StructurePieceType> STRUCTURE_PIECE;
   public static final Registry<StructureType<?>> STRUCTURE_TYPE;
   public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE;
   public static final Registry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPE;
   public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE;
   public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPE;
   public static final Registry<RootPlacerType<?>> ROOT_PLACER_TYPE;
   public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPE;
   public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPE;
   public static final Registry<MapCodec<? extends BiomeSource>> BIOME_SOURCE;
   public static final Registry<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATOR;
   public static final Registry<MapCodec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION;
   public static final Registry<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE;
   public static final Registry<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE;
   public static final Registry<MapCodec<? extends Block>> BLOCK_TYPE;
   public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR;
   public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT;
   public static final Registry<MapCodec<? extends PoolAliasBinding>> POOL_ALIAS_BINDING_TYPE;
   public static final Registry<CatVariant> CAT_VARIANT;
   public static final Registry<FrogVariant> FROG_VARIANT;
   public static final Registry<Instrument> INSTRUMENT;
   public static final Registry<String> DECORATED_POT_PATTERNS;
   public static final Registry<CreativeModeTab> CREATIVE_MODE_TAB;
   public static final Registry<CriterionTrigger<?>> TRIGGER_TYPES;
   public static final Registry<NumberFormatType<?>> NUMBER_FORMAT_TYPE;
   public static final Registry<ArmorMaterial> ARMOR_MATERIAL;
   public static final Registry<DataComponentType<?>> DATA_COMPONENT_TYPE;
   public static final Registry<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATE_TYPE;
   public static final Registry<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATE_TYPE;
   public static final Registry<MapDecorationType> MAP_DECORATION_TYPE;
   public static final Registry<DataComponentType<?>> ENCHANTMENT_EFFECT_COMPONENT_TYPE;
   public static final Registry<MapCodec<? extends LevelBasedValue>> ENCHANTMENT_LEVEL_BASED_VALUE_TYPE;
   public static final Registry<MapCodec<? extends EnchantmentEntityEffect>> ENCHANTMENT_ENTITY_EFFECT_TYPE;
   public static final Registry<MapCodec<? extends EnchantmentLocationBasedEffect>> ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE;
   public static final Registry<MapCodec<? extends EnchantmentValueEffect>> ENCHANTMENT_VALUE_EFFECT_TYPE;
   public static final Registry<MapCodec<? extends EnchantmentProvider>> ENCHANTMENT_PROVIDER_TYPE;
   public static final Registry<? extends Registry<?>> REGISTRY;

   public BuiltInRegistries() {
      super();
   }

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> var0, RegistryBootstrap<T> var1) {
      return internalRegister(var0, new MappedRegistry(var0, Lifecycle.stable(), false), var1);
   }

   private static <T> Registry<T> registerSimpleWithIntrusiveHolders(ResourceKey<? extends Registry<T>> var0, RegistryBootstrap<T> var1) {
      return internalRegister(var0, new MappedRegistry(var0, Lifecycle.stable(), true), var1);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(ResourceKey<? extends Registry<T>> var0, String var1, RegistryBootstrap<T> var2) {
      return (DefaultedRegistry)internalRegister(var0, new DefaultedMappedRegistry(var1, var0, Lifecycle.stable(), false), var2);
   }

   private static <T> DefaultedRegistry<T> registerDefaultedWithIntrusiveHolders(ResourceKey<? extends Registry<T>> var0, String var1, RegistryBootstrap<T> var2) {
      return (DefaultedRegistry)internalRegister(var0, new DefaultedMappedRegistry(var1, var0, Lifecycle.stable(), true), var2);
   }

   private static <T, R extends WritableRegistry<T>> R internalRegister(ResourceKey<? extends Registry<T>> var0, R var1, RegistryBootstrap<T> var2) {
      Bootstrap.checkBootstrapCalled(() -> {
         return "registry " + String.valueOf(var0);
      });
      ResourceLocation var3 = var0.location();
      LOADERS.put(var3, () -> {
         return var2.run(var1);
      });
      WRITABLE_REGISTRY.register(var0, var1, RegistrationInfo.BUILT_IN);
      return var1;
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
      Iterator var0 = REGISTRY.iterator();

      while(var0.hasNext()) {
         Registry var1 = (Registry)var0.next();
         var1.freeze();
      }

   }

   private static <T extends Registry<?>> void validate(Registry<T> var0) {
      var0.forEach((var1) -> {
         if (var1.keySet().isEmpty()) {
            ResourceLocation var10000 = var0.getKey(var1);
            Util.logAndPauseIfInIde("Registry '" + String.valueOf(var10000) + "' was empty after loading");
         }

         if (var1 instanceof DefaultedRegistry) {
            ResourceLocation var2 = ((DefaultedRegistry)var1).getDefaultKey();
            Validate.notNull(var1.get(var2), "Missing default of DefaultedMappedRegistry: " + String.valueOf(var2), new Object[0]);
         }

      });
   }

   static {
      WRITABLE_REGISTRY = new MappedRegistry(ResourceKey.createRegistryKey(Registries.ROOT_REGISTRY_NAME), Lifecycle.stable());
      GAME_EVENT = registerDefaulted(Registries.GAME_EVENT, "step", GameEvent::bootstrap);
      SOUND_EVENT = registerSimple(Registries.SOUND_EVENT, (var0) -> {
         return SoundEvents.ITEM_PICKUP;
      });
      FLUID = registerDefaultedWithIntrusiveHolders(Registries.FLUID, "empty", (var0) -> {
         return Fluids.EMPTY;
      });
      MOB_EFFECT = registerSimple(Registries.MOB_EFFECT, MobEffects::bootstrap);
      BLOCK = registerDefaultedWithIntrusiveHolders(Registries.BLOCK, "air", (var0) -> {
         return Blocks.AIR;
      });
      ENTITY_TYPE = registerDefaultedWithIntrusiveHolders(Registries.ENTITY_TYPE, "pig", (var0) -> {
         return EntityType.PIG;
      });
      ITEM = registerDefaultedWithIntrusiveHolders(Registries.ITEM, "air", (var0) -> {
         return Items.AIR;
      });
      POTION = registerSimple(Registries.POTION, Potions::bootstrap);
      PARTICLE_TYPE = registerSimple(Registries.PARTICLE_TYPE, (var0) -> {
         return ParticleTypes.BLOCK;
      });
      BLOCK_ENTITY_TYPE = registerSimpleWithIntrusiveHolders(Registries.BLOCK_ENTITY_TYPE, (var0) -> {
         return BlockEntityType.FURNACE;
      });
      CUSTOM_STAT = registerSimple(Registries.CUSTOM_STAT, (var0) -> {
         return Stats.JUMP;
      });
      CHUNK_STATUS = registerDefaulted(Registries.CHUNK_STATUS, "empty", (var0) -> {
         return ChunkStatus.EMPTY;
      });
      RULE_TEST = registerSimple(Registries.RULE_TEST, (var0) -> {
         return RuleTestType.ALWAYS_TRUE_TEST;
      });
      RULE_BLOCK_ENTITY_MODIFIER = registerSimple(Registries.RULE_BLOCK_ENTITY_MODIFIER, (var0) -> {
         return RuleBlockEntityModifierType.PASSTHROUGH;
      });
      POS_RULE_TEST = registerSimple(Registries.POS_RULE_TEST, (var0) -> {
         return PosRuleTestType.ALWAYS_TRUE_TEST;
      });
      MENU = registerSimple(Registries.MENU, (var0) -> {
         return MenuType.ANVIL;
      });
      RECIPE_TYPE = registerSimple(Registries.RECIPE_TYPE, (var0) -> {
         return RecipeType.CRAFTING;
      });
      RECIPE_SERIALIZER = registerSimple(Registries.RECIPE_SERIALIZER, (var0) -> {
         return RecipeSerializer.SHAPELESS_RECIPE;
      });
      ATTRIBUTE = registerSimple(Registries.ATTRIBUTE, Attributes::bootstrap);
      POSITION_SOURCE_TYPE = registerSimple(Registries.POSITION_SOURCE_TYPE, (var0) -> {
         return PositionSourceType.BLOCK;
      });
      COMMAND_ARGUMENT_TYPE = registerSimple(Registries.COMMAND_ARGUMENT_TYPE, ArgumentTypeInfos::bootstrap);
      STAT_TYPE = registerSimple(Registries.STAT_TYPE, (var0) -> {
         return Stats.ITEM_USED;
      });
      VILLAGER_TYPE = registerDefaulted(Registries.VILLAGER_TYPE, "plains", (var0) -> {
         return VillagerType.PLAINS;
      });
      VILLAGER_PROFESSION = registerDefaulted(Registries.VILLAGER_PROFESSION, "none", (var0) -> {
         return VillagerProfession.NONE;
      });
      POINT_OF_INTEREST_TYPE = registerSimple(Registries.POINT_OF_INTEREST_TYPE, PoiTypes::bootstrap);
      MEMORY_MODULE_TYPE = registerDefaulted(Registries.MEMORY_MODULE_TYPE, "dummy", (var0) -> {
         return MemoryModuleType.DUMMY;
      });
      SENSOR_TYPE = registerDefaulted(Registries.SENSOR_TYPE, "dummy", (var0) -> {
         return SensorType.DUMMY;
      });
      SCHEDULE = registerSimple(Registries.SCHEDULE, (var0) -> {
         return Schedule.EMPTY;
      });
      ACTIVITY = registerSimple(Registries.ACTIVITY, (var0) -> {
         return Activity.IDLE;
      });
      LOOT_POOL_ENTRY_TYPE = registerSimple(Registries.LOOT_POOL_ENTRY_TYPE, (var0) -> {
         return LootPoolEntries.EMPTY;
      });
      LOOT_FUNCTION_TYPE = registerSimple(Registries.LOOT_FUNCTION_TYPE, (var0) -> {
         return LootItemFunctions.SET_COUNT;
      });
      LOOT_CONDITION_TYPE = registerSimple(Registries.LOOT_CONDITION_TYPE, (var0) -> {
         return LootItemConditions.INVERTED;
      });
      LOOT_NUMBER_PROVIDER_TYPE = registerSimple(Registries.LOOT_NUMBER_PROVIDER_TYPE, (var0) -> {
         return NumberProviders.CONSTANT;
      });
      LOOT_NBT_PROVIDER_TYPE = registerSimple(Registries.LOOT_NBT_PROVIDER_TYPE, (var0) -> {
         return NbtProviders.CONTEXT;
      });
      LOOT_SCORE_PROVIDER_TYPE = registerSimple(Registries.LOOT_SCORE_PROVIDER_TYPE, (var0) -> {
         return ScoreboardNameProviders.CONTEXT;
      });
      FLOAT_PROVIDER_TYPE = registerSimple(Registries.FLOAT_PROVIDER_TYPE, (var0) -> {
         return FloatProviderType.CONSTANT;
      });
      INT_PROVIDER_TYPE = registerSimple(Registries.INT_PROVIDER_TYPE, (var0) -> {
         return IntProviderType.CONSTANT;
      });
      HEIGHT_PROVIDER_TYPE = registerSimple(Registries.HEIGHT_PROVIDER_TYPE, (var0) -> {
         return HeightProviderType.CONSTANT;
      });
      BLOCK_PREDICATE_TYPE = registerSimple(Registries.BLOCK_PREDICATE_TYPE, (var0) -> {
         return BlockPredicateType.NOT;
      });
      CARVER = registerSimple(Registries.CARVER, (var0) -> {
         return WorldCarver.CAVE;
      });
      FEATURE = registerSimple(Registries.FEATURE, (var0) -> {
         return Feature.ORE;
      });
      STRUCTURE_PLACEMENT = registerSimple(Registries.STRUCTURE_PLACEMENT, (var0) -> {
         return StructurePlacementType.RANDOM_SPREAD;
      });
      STRUCTURE_PIECE = registerSimple(Registries.STRUCTURE_PIECE, (var0) -> {
         return StructurePieceType.MINE_SHAFT_ROOM;
      });
      STRUCTURE_TYPE = registerSimple(Registries.STRUCTURE_TYPE, (var0) -> {
         return StructureType.JIGSAW;
      });
      PLACEMENT_MODIFIER_TYPE = registerSimple(Registries.PLACEMENT_MODIFIER_TYPE, (var0) -> {
         return PlacementModifierType.COUNT;
      });
      BLOCKSTATE_PROVIDER_TYPE = registerSimple(Registries.BLOCK_STATE_PROVIDER_TYPE, (var0) -> {
         return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
      });
      FOLIAGE_PLACER_TYPE = registerSimple(Registries.FOLIAGE_PLACER_TYPE, (var0) -> {
         return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
      });
      TRUNK_PLACER_TYPE = registerSimple(Registries.TRUNK_PLACER_TYPE, (var0) -> {
         return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
      });
      ROOT_PLACER_TYPE = registerSimple(Registries.ROOT_PLACER_TYPE, (var0) -> {
         return RootPlacerType.MANGROVE_ROOT_PLACER;
      });
      TREE_DECORATOR_TYPE = registerSimple(Registries.TREE_DECORATOR_TYPE, (var0) -> {
         return TreeDecoratorType.LEAVE_VINE;
      });
      FEATURE_SIZE_TYPE = registerSimple(Registries.FEATURE_SIZE_TYPE, (var0) -> {
         return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
      });
      BIOME_SOURCE = registerSimple(Registries.BIOME_SOURCE, BiomeSources::bootstrap);
      CHUNK_GENERATOR = registerSimple(Registries.CHUNK_GENERATOR, ChunkGenerators::bootstrap);
      MATERIAL_CONDITION = registerSimple(Registries.MATERIAL_CONDITION, SurfaceRules.ConditionSource::bootstrap);
      MATERIAL_RULE = registerSimple(Registries.MATERIAL_RULE, SurfaceRules.RuleSource::bootstrap);
      DENSITY_FUNCTION_TYPE = registerSimple(Registries.DENSITY_FUNCTION_TYPE, DensityFunctions::bootstrap);
      BLOCK_TYPE = registerSimple(Registries.BLOCK_TYPE, BlockTypes::bootstrap);
      STRUCTURE_PROCESSOR = registerSimple(Registries.STRUCTURE_PROCESSOR, (var0) -> {
         return StructureProcessorType.BLOCK_IGNORE;
      });
      STRUCTURE_POOL_ELEMENT = registerSimple(Registries.STRUCTURE_POOL_ELEMENT, (var0) -> {
         return StructurePoolElementType.EMPTY;
      });
      POOL_ALIAS_BINDING_TYPE = registerSimple(Registries.POOL_ALIAS_BINDING, PoolAliasBindings::bootstrap);
      CAT_VARIANT = registerSimple(Registries.CAT_VARIANT, CatVariant::bootstrap);
      FROG_VARIANT = registerSimple(Registries.FROG_VARIANT, FrogVariant::bootstrap);
      INSTRUMENT = registerSimple(Registries.INSTRUMENT, Instruments::bootstrap);
      DECORATED_POT_PATTERNS = registerSimple(Registries.DECORATED_POT_PATTERNS, DecoratedPotPatterns::bootstrap);
      CREATIVE_MODE_TAB = registerSimple(Registries.CREATIVE_MODE_TAB, CreativeModeTabs::bootstrap);
      TRIGGER_TYPES = registerSimple(Registries.TRIGGER_TYPE, CriteriaTriggers::bootstrap);
      NUMBER_FORMAT_TYPE = registerSimple(Registries.NUMBER_FORMAT_TYPE, NumberFormatTypes::bootstrap);
      ARMOR_MATERIAL = registerSimple(Registries.ARMOR_MATERIAL, ArmorMaterials::bootstrap);
      DATA_COMPONENT_TYPE = registerSimple(Registries.DATA_COMPONENT_TYPE, DataComponents::bootstrap);
      ENTITY_SUB_PREDICATE_TYPE = registerSimple(Registries.ENTITY_SUB_PREDICATE_TYPE, EntitySubPredicates::bootstrap);
      ITEM_SUB_PREDICATE_TYPE = registerSimple(Registries.ITEM_SUB_PREDICATE_TYPE, ItemSubPredicates::bootstrap);
      MAP_DECORATION_TYPE = registerSimple(Registries.MAP_DECORATION_TYPE, MapDecorationTypes::bootstrap);
      ENCHANTMENT_EFFECT_COMPONENT_TYPE = registerSimple(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, EnchantmentEffectComponents::bootstrap);
      ENCHANTMENT_LEVEL_BASED_VALUE_TYPE = registerSimple(Registries.ENCHANTMENT_LEVEL_BASED_VALUE_TYPE, LevelBasedValue::bootstrap);
      ENCHANTMENT_ENTITY_EFFECT_TYPE = registerSimple(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, EnchantmentEntityEffect::bootstrap);
      ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE = registerSimple(Registries.ENCHANTMENT_LOCATION_BASED_EFFECT_TYPE, EnchantmentLocationBasedEffect::bootstrap);
      ENCHANTMENT_VALUE_EFFECT_TYPE = registerSimple(Registries.ENCHANTMENT_VALUE_EFFECT_TYPE, EnchantmentValueEffect::bootstrap);
      ENCHANTMENT_PROVIDER_TYPE = registerSimple(Registries.ENCHANTMENT_PROVIDER_TYPE, EnchantmentProviderTypes::bootstrap);
      REGISTRY = WRITABLE_REGISTRY;
   }

   @FunctionalInterface
   interface RegistryBootstrap<T> {
      Object run(Registry<T> var1);
   }
}
