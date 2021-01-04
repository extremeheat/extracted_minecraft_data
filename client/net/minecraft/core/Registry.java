package net.minecraft.core;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSourceType;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGeneratorType;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElementType;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.structure.StructureFeatureIO;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Registry<T> implements IdMap<T> {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final Map<ResourceLocation, Supplier<?>> LOADERS = Maps.newLinkedHashMap();
   public static final WritableRegistry<WritableRegistry<?>> REGISTRY = new MappedRegistry();
   public static final Registry<SoundEvent> SOUND_EVENT = registerSimple("sound_event", () -> {
      return SoundEvents.ITEM_PICKUP;
   });
   public static final DefaultedRegistry<Fluid> FLUID = registerDefaulted("fluid", "empty", () -> {
      return Fluids.EMPTY;
   });
   public static final Registry<MobEffect> MOB_EFFECT = registerSimple("mob_effect", () -> {
      return MobEffects.LUCK;
   });
   public static final DefaultedRegistry<Block> BLOCK = registerDefaulted("block", "air", () -> {
      return Blocks.AIR;
   });
   public static final Registry<Enchantment> ENCHANTMENT = registerSimple("enchantment", () -> {
      return Enchantments.BLOCK_FORTUNE;
   });
   public static final DefaultedRegistry<EntityType<?>> ENTITY_TYPE = registerDefaulted("entity_type", "pig", () -> {
      return EntityType.PIG;
   });
   public static final DefaultedRegistry<Item> ITEM = registerDefaulted("item", "air", () -> {
      return Items.AIR;
   });
   public static final DefaultedRegistry<Potion> POTION = registerDefaulted("potion", "empty", () -> {
      return Potions.EMPTY;
   });
   public static final Registry<WorldCarver<?>> CARVER = registerSimple("carver", () -> {
      return WorldCarver.CAVE;
   });
   public static final Registry<SurfaceBuilder<?>> SURFACE_BUILDER = registerSimple("surface_builder", () -> {
      return SurfaceBuilder.DEFAULT;
   });
   public static final Registry<Feature<?>> FEATURE = registerSimple("feature", () -> {
      return Feature.ORE;
   });
   public static final Registry<FeatureDecorator<?>> DECORATOR = registerSimple("decorator", () -> {
      return FeatureDecorator.NOPE;
   });
   public static final Registry<Biome> BIOME = registerSimple("biome", () -> {
      return Biomes.DEFAULT;
   });
   public static final Registry<ParticleType<? extends ParticleOptions>> PARTICLE_TYPE = registerSimple("particle_type", () -> {
      return ParticleTypes.BLOCK;
   });
   public static final Registry<BiomeSourceType<?, ?>> BIOME_SOURCE_TYPE = registerSimple("biome_source_type", () -> {
      return BiomeSourceType.VANILLA_LAYERED;
   });
   public static final Registry<BlockEntityType<?>> BLOCK_ENTITY_TYPE = registerSimple("block_entity_type", () -> {
      return BlockEntityType.FURNACE;
   });
   public static final Registry<ChunkGeneratorType<?, ?>> CHUNK_GENERATOR_TYPE = registerSimple("chunk_generator_type", () -> {
      return ChunkGeneratorType.FLAT;
   });
   public static final Registry<DimensionType> DIMENSION_TYPE = registerSimple("dimension_type", () -> {
      return DimensionType.OVERWORLD;
   });
   public static final DefaultedRegistry<Motive> MOTIVE = registerDefaulted("motive", "kebab", () -> {
      return Motive.KEBAB;
   });
   public static final Registry<ResourceLocation> CUSTOM_STAT = registerSimple("custom_stat", () -> {
      return Stats.JUMP;
   });
   public static final DefaultedRegistry<ChunkStatus> CHUNK_STATUS = registerDefaulted("chunk_status", "empty", () -> {
      return ChunkStatus.EMPTY;
   });
   public static final Registry<StructureFeature<?>> STRUCTURE_FEATURE = registerSimple("structure_feature", () -> {
      return StructureFeatureIO.MINESHAFT;
   });
   public static final Registry<StructurePieceType> STRUCTURE_PIECE = registerSimple("structure_piece", () -> {
      return StructurePieceType.MINE_SHAFT_ROOM;
   });
   public static final Registry<RuleTestType> RULE_TEST = registerSimple("rule_test", () -> {
      return RuleTestType.ALWAYS_TRUE_TEST;
   });
   public static final Registry<StructureProcessorType> STRUCTURE_PROCESSOR = registerSimple("structure_processor", () -> {
      return StructureProcessorType.BLOCK_IGNORE;
   });
   public static final Registry<StructurePoolElementType> STRUCTURE_POOL_ELEMENT = registerSimple("structure_pool_element", () -> {
      return StructurePoolElementType.EMPTY;
   });
   public static final Registry<MenuType<?>> MENU = registerSimple("menu", () -> {
      return MenuType.ANVIL;
   });
   public static final Registry<RecipeType<?>> RECIPE_TYPE = registerSimple("recipe_type", () -> {
      return RecipeType.CRAFTING;
   });
   public static final Registry<RecipeSerializer<?>> RECIPE_SERIALIZER = registerSimple("recipe_serializer", () -> {
      return RecipeSerializer.SHAPELESS_RECIPE;
   });
   public static final Registry<StatType<?>> STAT_TYPE = registerSimple("stat_type", () -> {
      return Stats.ITEM_USED;
   });
   public static final DefaultedRegistry<VillagerType> VILLAGER_TYPE = registerDefaulted("villager_type", "plains", () -> {
      return VillagerType.PLAINS;
   });
   public static final DefaultedRegistry<VillagerProfession> VILLAGER_PROFESSION = registerDefaulted("villager_profession", "none", () -> {
      return VillagerProfession.NONE;
   });
   public static final DefaultedRegistry<PoiType> POINT_OF_INTEREST_TYPE = registerDefaulted("point_of_interest_type", "unemployed", () -> {
      return PoiType.UNEMPLOYED;
   });
   public static final DefaultedRegistry<MemoryModuleType<?>> MEMORY_MODULE_TYPE = registerDefaulted("memory_module_type", "dummy", () -> {
      return MemoryModuleType.DUMMY;
   });
   public static final DefaultedRegistry<SensorType<?>> SENSOR_TYPE = registerDefaulted("sensor_type", "dummy", () -> {
      return SensorType.DUMMY;
   });
   public static final Registry<Schedule> SCHEDULE = registerSimple("schedule", () -> {
      return Schedule.EMPTY;
   });
   public static final Registry<Activity> ACTIVITY = registerSimple("activity", () -> {
      return Activity.IDLE;
   });

   public Registry() {
      super();
   }

   private static <T> Registry<T> registerSimple(String var0, Supplier<T> var1) {
      return internalRegister(var0, new MappedRegistry(), var1);
   }

   private static <T> DefaultedRegistry<T> registerDefaulted(String var0, String var1, Supplier<T> var2) {
      return (DefaultedRegistry)internalRegister(var0, new DefaultedRegistry(var1), var2);
   }

   private static <T, R extends WritableRegistry<T>> R internalRegister(String var0, R var1, Supplier<T> var2) {
      ResourceLocation var3 = new ResourceLocation(var0);
      LOADERS.put(var3, var2);
      return (WritableRegistry)REGISTRY.register(var3, var1);
   }

   @Nullable
   public abstract ResourceLocation getKey(T var1);

   public abstract int getId(@Nullable T var1);

   @Nullable
   public abstract T get(@Nullable ResourceLocation var1);

   public abstract Optional<T> getOptional(@Nullable ResourceLocation var1);

   public abstract Set<ResourceLocation> keySet();

   @Nullable
   public abstract T getRandom(Random var1);

   public Stream<T> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   public abstract boolean containsKey(ResourceLocation var1);

   public static <T> T register(Registry<? super T> var0, String var1, T var2) {
      return register(var0, new ResourceLocation(var1), var2);
   }

   public static <T> T register(Registry<? super T> var0, ResourceLocation var1, T var2) {
      return ((WritableRegistry)var0).register(var1, var2);
   }

   public static <T> T registerMapping(Registry<? super T> var0, int var1, String var2, T var3) {
      return ((WritableRegistry)var0).registerMapping(var1, new ResourceLocation(var2), var3);
   }

   static {
      LOADERS.entrySet().forEach((var0) -> {
         if (((Supplier)var0.getValue()).get() == null) {
            LOGGER.error("Unable to bootstrap registry '{}'", var0.getKey());
         }

      });
      REGISTRY.forEach((var0) -> {
         if (var0.isEmpty()) {
            LOGGER.error("Registry '{}' was empty after loading", REGISTRY.getKey(var0));
            if (SharedConstants.IS_RUNNING_IN_IDE) {
               throw new IllegalStateException("Registry: '" + REGISTRY.getKey(var0) + "' is empty, not allowed, fix me!");
            }
         }

         if (var0 instanceof DefaultedRegistry) {
            ResourceLocation var1 = ((DefaultedRegistry)var0).getDefaultKey();
            Validate.notNull(var0.get(var1), "Missing default of DefaultedMappedRegistry: " + var1, new Object[0]);
         }

      });
   }
}
