package net.minecraft.core;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ExtraCodecs;
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
import net.minecraft.world.entity.decoration.Motive;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements Keyable, IdMap<T> {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   public static final ResourceLocation ROOT_REGISTRY_NAME = new ResourceLocation("root");
   protected static final WritableRegistry<WritableRegistry<?>> WRITABLE_REGISTRY = new MappedRegistry(createRegistryKey("root"), Lifecycle.experimental());
   public static final Registry<? extends Registry<?>> REGISTRY;
   public static final ResourceKey<Registry<SoundEvent>> SOUND_EVENT_REGISTRY;
   public static final ResourceKey<Registry<Fluid>> FLUID_REGISTRY;
   public static final ResourceKey<Registry<MobEffect>> MOB_EFFECT_REGISTRY;
   public static final ResourceKey<Registry<Block>> BLOCK_REGISTRY;
   public static final ResourceKey<Registry<Enchantment>> ENCHANTMENT_REGISTRY;
   public static final ResourceKey<Registry<EntityType<?>>> ENTITY_TYPE_REGISTRY;
   public static final ResourceKey<Registry<Item>> ITEM_REGISTRY;
   public static final ResourceKey<Registry<Potion>> POTION_REGISTRY;
   public static final ResourceKey<Registry<ParticleType<?>>> PARTICLE_TYPE_REGISTRY;
   public static final ResourceKey<Registry<BlockEntityType<?>>> BLOCK_ENTITY_TYPE_REGISTRY;
   public static final ResourceKey<Registry<Motive>> MOTIVE_REGISTRY;
   public static final ResourceKey<Registry<ResourceLocation>> CUSTOM_STAT_REGISTRY;
   public static final ResourceKey<Registry<ChunkStatus>> CHUNK_STATUS_REGISTRY;
   public static final ResourceKey<Registry<RuleTestType<?>>> RULE_TEST_REGISTRY;
   public static final ResourceKey<Registry<PosRuleTestType<?>>> POS_RULE_TEST_REGISTRY;
   public static final ResourceKey<Registry<MenuType<?>>> MENU_REGISTRY;
   public static final ResourceKey<Registry<RecipeType<?>>> RECIPE_TYPE_REGISTRY;
   public static final ResourceKey<Registry<RecipeSerializer<?>>> RECIPE_SERIALIZER_REGISTRY;
   public static final ResourceKey<Registry<Attribute>> ATTRIBUTE_REGISTRY;
   public static final ResourceKey<Registry<GameEvent>> GAME_EVENT_REGISTRY;
   public static final ResourceKey<Registry<PositionSourceType<?>>> POSITION_SOURCE_TYPE_REGISTRY;
   public static final ResourceKey<Registry<StatType<?>>> STAT_TYPE_REGISTRY;
   public static final ResourceKey<Registry<VillagerType>> VILLAGER_TYPE_REGISTRY;
   public static final ResourceKey<Registry<VillagerProfession>> VILLAGER_PROFESSION_REGISTRY;
   public static final ResourceKey<Registry<PoiType>> POINT_OF_INTEREST_TYPE_REGISTRY;
   public static final ResourceKey<Registry<MemoryModuleType<?>>> MEMORY_MODULE_TYPE_REGISTRY;
   public static final ResourceKey<Registry<SensorType<?>>> SENSOR_TYPE_REGISTRY;
   public static final ResourceKey<Registry<Schedule>> SCHEDULE_REGISTRY;
   public static final ResourceKey<Registry<Activity>> ACTIVITY_REGISTRY;
   public static final ResourceKey<Registry<LootPoolEntryType>> LOOT_ENTRY_REGISTRY;
   public static final ResourceKey<Registry<LootItemFunctionType>> LOOT_FUNCTION_REGISTRY;
   public static final ResourceKey<Registry<LootItemConditionType>> LOOT_ITEM_REGISTRY;
   public static final ResourceKey<Registry<LootNumberProviderType>> LOOT_NUMBER_PROVIDER_REGISTRY;
   public static final ResourceKey<Registry<LootNbtProviderType>> LOOT_NBT_PROVIDER_REGISTRY;
   public static final ResourceKey<Registry<LootScoreProviderType>> LOOT_SCORE_PROVIDER_REGISTRY;
   public static final ResourceKey<Registry<DimensionType>> DIMENSION_TYPE_REGISTRY;
   public static final ResourceKey<Registry<Level>> DIMENSION_REGISTRY;
   public static final ResourceKey<Registry<LevelStem>> LEVEL_STEM_REGISTRY;
   public static final DefaultedRegistry<GameEvent> GAME_EVENT;
   public static final Registry<SoundEvent> SOUND_EVENT;
   public static final DefaultedRegistry<Fluid> FLUID;
   public static final Registry<MobEffect> MOB_EFFECT;
   public static final DefaultedRegistry<Block> BLOCK;
   public static final Registry<Enchantment> ENCHANTMENT;
   public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE;
   public static final DefaultedRegistry<Item> ITEM;
   public static final DefaultedRegistry<Potion> POTION;
   public static final Registry<ParticleType<?>> PARTICLE_TYPE;
   public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE;
   public static final DefaultedRegistry<Motive> MOTIVE;
   public static final Registry<ResourceLocation> CUSTOM_STAT;
   public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS;
   public static final Registry<RuleTestType<?>> RULE_TEST;
   public static final Registry<PosRuleTestType<?>> POS_RULE_TEST;
   public static final Registry<MenuType<?>> MENU;
   public static final Registry<RecipeType<?>> RECIPE_TYPE;
   public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER;
   public static final Registry<Attribute> ATTRIBUTE;
   public static final Registry<PositionSourceType<?>> POSITION_SOURCE_TYPE;
   public static final Registry<StatType<?>> STAT_TYPE;
   public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE;
   public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION;
   public static final DefaultedRegistry<PoiType> POINT_OF_INTEREST_TYPE;
   public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE;
   public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE;
   public static final Registry<Schedule> SCHEDULE;
   public static final Registry<Activity> ACTIVITY;
   public static final Registry<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE;
   public static final Registry<LootItemFunctionType> LOOT_FUNCTION_TYPE;
   public static final Registry<LootItemConditionType> LOOT_CONDITION_TYPE;
   public static final Registry<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE;
   public static final Registry<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE;
   public static final Registry<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE;
   public static final ResourceKey<Registry<FloatProviderType<?>>> FLOAT_PROVIDER_TYPE_REGISTRY;
   public static final Registry<FloatProviderType<?>> FLOAT_PROVIDER_TYPES;
   public static final ResourceKey<Registry<IntProviderType<?>>> INT_PROVIDER_TYPE_REGISTRY;
   public static final Registry<IntProviderType<?>> INT_PROVIDER_TYPES;
   public static final ResourceKey<Registry<HeightProviderType<?>>> HEIGHT_PROVIDER_TYPE_REGISTRY;
   public static final Registry<HeightProviderType<?>> HEIGHT_PROVIDER_TYPES;
   public static final ResourceKey<Registry<BlockPredicateType<?>>> BLOCK_PREDICATE_TYPE_REGISTRY;
   public static final Registry<BlockPredicateType<?>> BLOCK_PREDICATE_TYPES;
   public static final ResourceKey<Registry<NoiseGeneratorSettings>> NOISE_GENERATOR_SETTINGS_REGISTRY;
   public static final ResourceKey<Registry<ConfiguredWorldCarver<?>>> CONFIGURED_CARVER_REGISTRY;
   public static final ResourceKey<Registry<ConfiguredFeature<?, ?>>> CONFIGURED_FEATURE_REGISTRY;
   public static final ResourceKey<Registry<PlacedFeature>> PLACED_FEATURE_REGISTRY;
   public static final ResourceKey<Registry<ConfiguredStructureFeature<?, ?>>> CONFIGURED_STRUCTURE_FEATURE_REGISTRY;
   public static final ResourceKey<Registry<StructureProcessorList>> PROCESSOR_LIST_REGISTRY;
   public static final ResourceKey<Registry<StructureTemplatePool>> TEMPLATE_POOL_REGISTRY;
   public static final ResourceKey<Registry<Biome>> BIOME_REGISTRY;
   public static final ResourceKey<Registry<NormalNoise.NoiseParameters>> NOISE_REGISTRY;
   public static final ResourceKey<Registry<WorldCarver<?>>> CARVER_REGISTRY;
   public static final Registry<WorldCarver<?>> CARVER;
   public static final ResourceKey<Registry<Feature<?>>> FEATURE_REGISTRY;
   public static final Registry<Feature<?>> FEATURE;
   public static final ResourceKey<Registry<StructureFeature<?>>> STRUCTURE_FEATURE_REGISTRY;
   public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE;
   public static final ResourceKey<Registry<StructurePieceType>> STRUCTURE_PIECE_REGISTRY;
   public static final Registry<StructurePieceType> STRUCTURE_PIECE;
   public static final ResourceKey<Registry<PlacementModifierType<?>>> PLACEMENT_MODIFIER_REGISTRY;
   public static final Registry<PlacementModifierType<?>> PLACEMENT_MODIFIERS;
   public static final ResourceKey<Registry<BlockStateProviderType<?>>> BLOCK_STATE_PROVIDER_TYPE_REGISTRY;
   public static final ResourceKey<Registry<FoliagePlacerType<?>>> FOLIAGE_PLACER_TYPE_REGISTRY;
   public static final ResourceKey<Registry<TrunkPlacerType<?>>> TRUNK_PLACER_TYPE_REGISTRY;
   public static final ResourceKey<Registry<TreeDecoratorType<?>>> TREE_DECORATOR_TYPE_REGISTRY;
   public static final ResourceKey<Registry<FeatureSizeType<?>>> FEATURE_SIZE_TYPE_REGISTRY;
   public static final ResourceKey<Registry<Codec<? extends BiomeSource>>> BIOME_SOURCE_REGISTRY;
   public static final ResourceKey<Registry<Codec<? extends ChunkGenerator>>> CHUNK_GENERATOR_REGISTRY;
   public static final ResourceKey<Registry<Codec<? extends SurfaceRules.ConditionSource>>> CONDITION_REGISTRY;
   public static final ResourceKey<Registry<Codec<? extends SurfaceRules.RuleSource>>> RULE_REGISTRY;
   public static final ResourceKey<Registry<StructureProcessorType<?>>> STRUCTURE_PROCESSOR_REGISTRY;
   public static final ResourceKey<Registry<StructurePoolElementType<?>>> STRUCTURE_POOL_ELEMENT_REGISTRY;
   public static final Registry<BlockStateProviderType<?>> BLOCKSTATE_PROVIDER_TYPES;
   public static final Registry<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPES;
   public static final Registry<TrunkPlacerType<?>> TRUNK_PLACER_TYPES;
   public static final Registry<TreeDecoratorType<?>> TREE_DECORATOR_TYPES;
   public static final Registry<FeatureSizeType<?>> FEATURE_SIZE_TYPES;
   public static final Registry<Codec<? extends BiomeSource>> BIOME_SOURCE;
   public static final Registry<Codec<? extends ChunkGenerator>> CHUNK_GENERATOR;
   public static final Registry<Codec<? extends SurfaceRules.ConditionSource>> CONDITION;
   public static final Registry<Codec<? extends SurfaceRules.RuleSource>> RULE;
   public static final Registry<StructureProcessorType<?>> STRUCTURE_PROCESSOR;
   public static final Registry<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT;
   private final ResourceKey<? extends Registry<T>> key;
   private final Lifecycle lifecycle;

   private static <T> ResourceKey<Registry<T>> createRegistryKey(String var0) {
      return ResourceKey.createRegistryKey(new ResourceLocation(var0));
   }

   public static <T extends WritableRegistry<?>> void checkRegistry(WritableRegistry<T> var0) {
      var0.forEach((var1) -> {
         if (var1.keySet().isEmpty()) {
            ResourceLocation var10000 = var0.getKey(var1);
            Util.logAndPauseIfInIde("Registry '" + var10000 + "' was empty after loading");
         }

         if (var1 instanceof DefaultedRegistry) {
            ResourceLocation var2 = ((DefaultedRegistry)var1).getDefaultKey();
            Validate.notNull(var1.get(var2), "Missing default of DefaultedMappedRegistry: " + var2, new Object[0]);
         }

      });
   }

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> var0, Supplier<T> var1) {
      return registerSimple(var0, Lifecycle.experimental(), var1);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(ResourceKey<? extends Registry<T>> var0, String var1, Supplier<T> var2) {
      return registerDefaulted(var0, var1, Lifecycle.experimental(), var2);
   }

   private static <T> Registry<T> registerSimple(ResourceKey<? extends Registry<T>> var0, Lifecycle var1, Supplier<T> var2) {
      return internalRegister(var0, new MappedRegistry(var0, var1), var2, var1);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(ResourceKey<? extends Registry<T>> var0, String var1, Lifecycle var2, Supplier<T> var3) {
      return (DefaultedRegistry)internalRegister(var0, new DefaultedRegistry(var1, var0, var2), var3, var2);
   }

   private static <T, R extends WritableRegistry<T>> R internalRegister(ResourceKey<? extends Registry<T>> var0, R var1, Supplier<T> var2, Lifecycle var3) {
      ResourceLocation var4 = var0.location();
      LOADERS.put(var4, var2);
      WritableRegistry var5 = WRITABLE_REGISTRY;
      return (WritableRegistry)var5.register(var0, var1, var3);
   }

   protected Registry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2) {
      super();
      Bootstrap.checkBootstrapCalled(() -> {
         return "registry " + var1;
      });
      this.key = var1;
      this.lifecycle = var2;
   }

   public ResourceKey<? extends Registry<T>> key() {
      return this.key;
   }

   public Lifecycle lifecycle() {
      return this.lifecycle;
   }

   public String toString() {
      return "Registry[" + this.key + " (" + this.lifecycle + ")]";
   }

   public Codec<T> byNameCodec() {
      Codec var1 = ResourceLocation.CODEC.flatXmap((var1x) -> {
         return (DataResult)Optional.ofNullable(this.get(var1x)).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown registry key in " + this.key + ": " + var1x);
         });
      }, (var1x) -> {
         return (DataResult)this.getResourceKey(var1x).map(ResourceKey::location).map(DataResult::success).orElseGet(() -> {
            return DataResult.error("Unknown registry element in " + this.key + ":" + var1x);
         });
      });
      Codec var2 = ExtraCodecs.idResolverCodec((var1x) -> {
         return this.getResourceKey(var1x).isPresent() ? this.getId(var1x) : -1;
      }, this::byId, -1);
      return ExtraCodecs.overrideLifecycle(ExtraCodecs.orCompressed(var1, var2), this::lifecycle, (var1x) -> {
         return this.lifecycle;
      });
   }

   public <U> Stream<U> keys(DynamicOps<U> var1) {
      return this.keySet().stream().map((var1x) -> {
         return var1.createString(var1x.toString());
      });
   }

   @Nullable
   public abstract ResourceLocation getKey(T var1);

   public abstract Optional<ResourceKey<T>> getResourceKey(T var1);

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
         return var2;
      }
   }

   public abstract Set<ResourceLocation> keySet();

   public abstract Set<Entry<ResourceKey<T>, T>> entrySet();

   @Nullable
   public abstract T getRandom(Random var1);

   public Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   public abstract boolean containsKey(ResourceLocation var1);

   public abstract boolean containsKey(ResourceKey<T> var1);

   public static <T> T register(Registry<? super T> var0, String var1, T var2) {
      return register(var0, new ResourceLocation(var1), var2);
   }

   public static <V, T extends V> T register(Registry<V> var0, ResourceLocation var1, T var2) {
      return register(var0, ResourceKey.create(var0.key, var1), var2);
   }

   public static <V, T extends V> T register(Registry<V> var0, ResourceKey<V> var1, T var2) {
      return ((WritableRegistry)var0).register(var1, var2, Lifecycle.stable());
   }

   public static <V, T extends V> T registerMapping(Registry<V> var0, int var1, String var2, T var3) {
      return ((WritableRegistry)var0).registerMapping(var1, ResourceKey.create(var0.key, new ResourceLocation(var2)), var3, Lifecycle.stable());
   }

   static {
      REGISTRY = WRITABLE_REGISTRY;
      SOUND_EVENT_REGISTRY = createRegistryKey("sound_event");
      FLUID_REGISTRY = createRegistryKey("fluid");
      MOB_EFFECT_REGISTRY = createRegistryKey("mob_effect");
      BLOCK_REGISTRY = createRegistryKey("block");
      ENCHANTMENT_REGISTRY = createRegistryKey("enchantment");
      ENTITY_TYPE_REGISTRY = createRegistryKey("entity_type");
      ITEM_REGISTRY = createRegistryKey("item");
      POTION_REGISTRY = createRegistryKey("potion");
      PARTICLE_TYPE_REGISTRY = createRegistryKey("particle_type");
      BLOCK_ENTITY_TYPE_REGISTRY = createRegistryKey("block_entity_type");
      MOTIVE_REGISTRY = createRegistryKey("motive");
      CUSTOM_STAT_REGISTRY = createRegistryKey("custom_stat");
      CHUNK_STATUS_REGISTRY = createRegistryKey("chunk_status");
      RULE_TEST_REGISTRY = createRegistryKey("rule_test");
      POS_RULE_TEST_REGISTRY = createRegistryKey("pos_rule_test");
      MENU_REGISTRY = createRegistryKey("menu");
      RECIPE_TYPE_REGISTRY = createRegistryKey("recipe_type");
      RECIPE_SERIALIZER_REGISTRY = createRegistryKey("recipe_serializer");
      ATTRIBUTE_REGISTRY = createRegistryKey("attribute");
      GAME_EVENT_REGISTRY = createRegistryKey("game_event");
      POSITION_SOURCE_TYPE_REGISTRY = createRegistryKey("position_source_type");
      STAT_TYPE_REGISTRY = createRegistryKey("stat_type");
      VILLAGER_TYPE_REGISTRY = createRegistryKey("villager_type");
      VILLAGER_PROFESSION_REGISTRY = createRegistryKey("villager_profession");
      POINT_OF_INTEREST_TYPE_REGISTRY = createRegistryKey("point_of_interest_type");
      MEMORY_MODULE_TYPE_REGISTRY = createRegistryKey("memory_module_type");
      SENSOR_TYPE_REGISTRY = createRegistryKey("sensor_type");
      SCHEDULE_REGISTRY = createRegistryKey("schedule");
      ACTIVITY_REGISTRY = createRegistryKey("activity");
      LOOT_ENTRY_REGISTRY = createRegistryKey("loot_pool_entry_type");
      LOOT_FUNCTION_REGISTRY = createRegistryKey("loot_function_type");
      LOOT_ITEM_REGISTRY = createRegistryKey("loot_condition_type");
      LOOT_NUMBER_PROVIDER_REGISTRY = createRegistryKey("loot_number_provider_type");
      LOOT_NBT_PROVIDER_REGISTRY = createRegistryKey("loot_nbt_provider_type");
      LOOT_SCORE_PROVIDER_REGISTRY = createRegistryKey("loot_score_provider_type");
      DIMENSION_TYPE_REGISTRY = createRegistryKey("dimension_type");
      DIMENSION_REGISTRY = createRegistryKey("dimension");
      LEVEL_STEM_REGISTRY = createRegistryKey("dimension");
      GAME_EVENT = registerDefaulted(GAME_EVENT_REGISTRY, "step", () -> {
         return GameEvent.STEP;
      });
      SOUND_EVENT = registerSimple(SOUND_EVENT_REGISTRY, () -> {
         return SoundEvents.ITEM_PICKUP;
      });
      FLUID = registerDefaulted(FLUID_REGISTRY, "empty", () -> {
         return Fluids.EMPTY;
      });
      MOB_EFFECT = registerSimple(MOB_EFFECT_REGISTRY, () -> {
         return MobEffects.LUCK;
      });
      BLOCK = registerDefaulted(BLOCK_REGISTRY, "air", () -> {
         return Blocks.AIR;
      });
      ENCHANTMENT = registerSimple(ENCHANTMENT_REGISTRY, () -> {
         return Enchantments.BLOCK_FORTUNE;
      });
      ENTITY_TYPE = registerDefaulted(ENTITY_TYPE_REGISTRY, "pig", () -> {
         return EntityType.PIG;
      });
      ITEM = registerDefaulted(ITEM_REGISTRY, "air", () -> {
         return Items.AIR;
      });
      POTION = registerDefaulted(POTION_REGISTRY, "empty", () -> {
         return Potions.EMPTY;
      });
      PARTICLE_TYPE = registerSimple(PARTICLE_TYPE_REGISTRY, () -> {
         return ParticleTypes.BLOCK;
      });
      BLOCK_ENTITY_TYPE = registerSimple(BLOCK_ENTITY_TYPE_REGISTRY, () -> {
         return BlockEntityType.FURNACE;
      });
      MOTIVE = registerDefaulted(MOTIVE_REGISTRY, "kebab", () -> {
         return Motive.KEBAB;
      });
      CUSTOM_STAT = registerSimple(CUSTOM_STAT_REGISTRY, () -> {
         return Stats.JUMP;
      });
      CHUNK_STATUS = registerDefaulted(CHUNK_STATUS_REGISTRY, "empty", () -> {
         return ChunkStatus.EMPTY;
      });
      RULE_TEST = registerSimple(RULE_TEST_REGISTRY, () -> {
         return RuleTestType.ALWAYS_TRUE_TEST;
      });
      POS_RULE_TEST = registerSimple(POS_RULE_TEST_REGISTRY, () -> {
         return PosRuleTestType.ALWAYS_TRUE_TEST;
      });
      MENU = registerSimple(MENU_REGISTRY, () -> {
         return MenuType.ANVIL;
      });
      RECIPE_TYPE = registerSimple(RECIPE_TYPE_REGISTRY, () -> {
         return RecipeType.CRAFTING;
      });
      RECIPE_SERIALIZER = registerSimple(RECIPE_SERIALIZER_REGISTRY, () -> {
         return RecipeSerializer.SHAPELESS_RECIPE;
      });
      ATTRIBUTE = registerSimple(ATTRIBUTE_REGISTRY, () -> {
         return Attributes.LUCK;
      });
      POSITION_SOURCE_TYPE = registerSimple(POSITION_SOURCE_TYPE_REGISTRY, () -> {
         return PositionSourceType.BLOCK;
      });
      STAT_TYPE = registerSimple(STAT_TYPE_REGISTRY, () -> {
         return Stats.ITEM_USED;
      });
      VILLAGER_TYPE = registerDefaulted(VILLAGER_TYPE_REGISTRY, "plains", () -> {
         return VillagerType.PLAINS;
      });
      VILLAGER_PROFESSION = registerDefaulted(VILLAGER_PROFESSION_REGISTRY, "none", () -> {
         return VillagerProfession.NONE;
      });
      POINT_OF_INTEREST_TYPE = registerDefaulted(POINT_OF_INTEREST_TYPE_REGISTRY, "unemployed", () -> {
         return PoiType.UNEMPLOYED;
      });
      MEMORY_MODULE_TYPE = registerDefaulted(MEMORY_MODULE_TYPE_REGISTRY, "dummy", () -> {
         return MemoryModuleType.DUMMY;
      });
      SENSOR_TYPE = registerDefaulted(SENSOR_TYPE_REGISTRY, "dummy", () -> {
         return SensorType.DUMMY;
      });
      SCHEDULE = registerSimple(SCHEDULE_REGISTRY, () -> {
         return Schedule.EMPTY;
      });
      ACTIVITY = registerSimple(ACTIVITY_REGISTRY, () -> {
         return Activity.IDLE;
      });
      LOOT_POOL_ENTRY_TYPE = registerSimple(LOOT_ENTRY_REGISTRY, () -> {
         return LootPoolEntries.EMPTY;
      });
      LOOT_FUNCTION_TYPE = registerSimple(LOOT_FUNCTION_REGISTRY, () -> {
         return LootItemFunctions.SET_COUNT;
      });
      LOOT_CONDITION_TYPE = registerSimple(LOOT_ITEM_REGISTRY, () -> {
         return LootItemConditions.INVERTED;
      });
      LOOT_NUMBER_PROVIDER_TYPE = registerSimple(LOOT_NUMBER_PROVIDER_REGISTRY, () -> {
         return NumberProviders.CONSTANT;
      });
      LOOT_NBT_PROVIDER_TYPE = registerSimple(LOOT_NBT_PROVIDER_REGISTRY, () -> {
         return NbtProviders.CONTEXT;
      });
      LOOT_SCORE_PROVIDER_TYPE = registerSimple(LOOT_SCORE_PROVIDER_REGISTRY, () -> {
         return ScoreboardNameProviders.CONTEXT;
      });
      FLOAT_PROVIDER_TYPE_REGISTRY = createRegistryKey("float_provider_type");
      FLOAT_PROVIDER_TYPES = registerSimple(FLOAT_PROVIDER_TYPE_REGISTRY, () -> {
         return FloatProviderType.CONSTANT;
      });
      INT_PROVIDER_TYPE_REGISTRY = createRegistryKey("int_provider_type");
      INT_PROVIDER_TYPES = registerSimple(INT_PROVIDER_TYPE_REGISTRY, () -> {
         return IntProviderType.CONSTANT;
      });
      HEIGHT_PROVIDER_TYPE_REGISTRY = createRegistryKey("height_provider_type");
      HEIGHT_PROVIDER_TYPES = registerSimple(HEIGHT_PROVIDER_TYPE_REGISTRY, () -> {
         return HeightProviderType.CONSTANT;
      });
      BLOCK_PREDICATE_TYPE_REGISTRY = createRegistryKey("block_predicate_type");
      BLOCK_PREDICATE_TYPES = registerSimple(BLOCK_PREDICATE_TYPE_REGISTRY, () -> {
         return BlockPredicateType.NOT;
      });
      NOISE_GENERATOR_SETTINGS_REGISTRY = createRegistryKey("worldgen/noise_settings");
      CONFIGURED_CARVER_REGISTRY = createRegistryKey("worldgen/configured_carver");
      CONFIGURED_FEATURE_REGISTRY = createRegistryKey("worldgen/configured_feature");
      PLACED_FEATURE_REGISTRY = createRegistryKey("worldgen/placed_feature");
      CONFIGURED_STRUCTURE_FEATURE_REGISTRY = createRegistryKey("worldgen/configured_structure_feature");
      PROCESSOR_LIST_REGISTRY = createRegistryKey("worldgen/processor_list");
      TEMPLATE_POOL_REGISTRY = createRegistryKey("worldgen/template_pool");
      BIOME_REGISTRY = createRegistryKey("worldgen/biome");
      NOISE_REGISTRY = createRegistryKey("worldgen/noise");
      CARVER_REGISTRY = createRegistryKey("worldgen/carver");
      CARVER = registerSimple(CARVER_REGISTRY, () -> {
         return WorldCarver.CAVE;
      });
      FEATURE_REGISTRY = createRegistryKey("worldgen/feature");
      FEATURE = registerSimple(FEATURE_REGISTRY, () -> {
         return Feature.ORE;
      });
      STRUCTURE_FEATURE_REGISTRY = createRegistryKey("worldgen/structure_feature");
      STRUCTURE_FEATURE = registerSimple(STRUCTURE_FEATURE_REGISTRY, () -> {
         return StructureFeature.MINESHAFT;
      });
      STRUCTURE_PIECE_REGISTRY = createRegistryKey("worldgen/structure_piece");
      STRUCTURE_PIECE = registerSimple(STRUCTURE_PIECE_REGISTRY, () -> {
         return StructurePieceType.MINE_SHAFT_ROOM;
      });
      PLACEMENT_MODIFIER_REGISTRY = createRegistryKey("worldgen/placement_modifier_type");
      PLACEMENT_MODIFIERS = registerSimple(PLACEMENT_MODIFIER_REGISTRY, () -> {
         return PlacementModifierType.COUNT;
      });
      BLOCK_STATE_PROVIDER_TYPE_REGISTRY = createRegistryKey("worldgen/block_state_provider_type");
      FOLIAGE_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/foliage_placer_type");
      TRUNK_PLACER_TYPE_REGISTRY = createRegistryKey("worldgen/trunk_placer_type");
      TREE_DECORATOR_TYPE_REGISTRY = createRegistryKey("worldgen/tree_decorator_type");
      FEATURE_SIZE_TYPE_REGISTRY = createRegistryKey("worldgen/feature_size_type");
      BIOME_SOURCE_REGISTRY = createRegistryKey("worldgen/biome_source");
      CHUNK_GENERATOR_REGISTRY = createRegistryKey("worldgen/chunk_generator");
      CONDITION_REGISTRY = createRegistryKey("worldgen/material_condition");
      RULE_REGISTRY = createRegistryKey("worldgen/material_rule");
      STRUCTURE_PROCESSOR_REGISTRY = createRegistryKey("worldgen/structure_processor");
      STRUCTURE_POOL_ELEMENT_REGISTRY = createRegistryKey("worldgen/structure_pool_element");
      BLOCKSTATE_PROVIDER_TYPES = registerSimple(BLOCK_STATE_PROVIDER_TYPE_REGISTRY, () -> {
         return BlockStateProviderType.SIMPLE_STATE_PROVIDER;
      });
      FOLIAGE_PLACER_TYPES = registerSimple(FOLIAGE_PLACER_TYPE_REGISTRY, () -> {
         return FoliagePlacerType.BLOB_FOLIAGE_PLACER;
      });
      TRUNK_PLACER_TYPES = registerSimple(TRUNK_PLACER_TYPE_REGISTRY, () -> {
         return TrunkPlacerType.STRAIGHT_TRUNK_PLACER;
      });
      TREE_DECORATOR_TYPES = registerSimple(TREE_DECORATOR_TYPE_REGISTRY, () -> {
         return TreeDecoratorType.LEAVE_VINE;
      });
      FEATURE_SIZE_TYPES = registerSimple(FEATURE_SIZE_TYPE_REGISTRY, () -> {
         return FeatureSizeType.TWO_LAYERS_FEATURE_SIZE;
      });
      BIOME_SOURCE = registerSimple(BIOME_SOURCE_REGISTRY, Lifecycle.stable(), () -> {
         return BiomeSource.CODEC;
      });
      CHUNK_GENERATOR = registerSimple(CHUNK_GENERATOR_REGISTRY, Lifecycle.stable(), () -> {
         return ChunkGenerator.CODEC;
      });
      CONDITION = registerSimple(CONDITION_REGISTRY, SurfaceRules.ConditionSource::bootstrap);
      RULE = registerSimple(RULE_REGISTRY, SurfaceRules.RuleSource::bootstrap);
      STRUCTURE_PROCESSOR = registerSimple(STRUCTURE_PROCESSOR_REGISTRY, () -> {
         return StructureProcessorType.BLOCK_IGNORE;
      });
      STRUCTURE_POOL_ELEMENT = registerSimple(STRUCTURE_POOL_ELEMENT_REGISTRY, () -> {
         return StructurePoolElementType.EMPTY;
      });
      BuiltinRegistries.bootstrap();
      LOADERS.forEach((var0, var1) -> {
         if (var1.get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", var0);
         }

      });
      checkRegistry(WRITABLE_REGISTRY);
   }
}
