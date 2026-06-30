package net.minecraft.resources;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySynchronization;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestInstance;
import net.minecraft.gametest.framework.TestEnvironmentDefinition;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.tags.TagNetworkSerialization;
import net.minecraft.world.clock.WorldClock;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.SulfurCubeArchetype;
import net.minecraft.world.entity.animal.chicken.ChickenSoundVariant;
import net.minecraft.world.entity.animal.chicken.ChickenVariant;
import net.minecraft.world.entity.animal.cow.CowSoundVariant;
import net.minecraft.world.entity.animal.cow.CowVariant;
import net.minecraft.world.entity.animal.feline.CatSoundVariant;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.nautilus.ZombieNautilusVariant;
import net.minecraft.world.entity.animal.pig.PigSoundVariant;
import net.minecraft.world.entity.animal.pig.PigVariant;
import net.minecraft.world.entity.animal.wolf.WolfSoundVariant;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.decoration.painting.PaintingVariant;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.block.entity.trialspawner.TrialSpawnerConfig;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorPreset;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.timeline.Timeline;
import org.slf4j.Logger;

public class RegistryDataLoader {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Comparator<ResourceKey<?>> ERROR_KEY_COMPARATOR = Comparator.comparing(ResourceKey::registry).thenComparing(ResourceKey::identifier);
   public static final List<RegistryData<?>> WORLDGEN_REGISTRIES;
   public static final List<RegistryData<?>> DIMENSION_REGISTRIES;
   public static final List<RegistryData<?>> SYNCHRONIZED_REGISTRIES;

   public RegistryDataLoader() {
      super();
   }

   public static CompletableFuture<RegistryAccess.Frozen> load(final ResourceManager resourceManager, final List<HolderLookup.RegistryLookup<?>> contextRegistries, final List<RegistryData<?>> registriesToLoad, final Executor executor) {
      LoaderFactory loaderFactory = new LoaderFactory() {
         public <T> RegistryLoadTask<T> create(final RegistryData<T> data, final Map<ResourceKey<?>, Exception> loadingErrors) {
            return new ResourceManagerRegistryLoadTask<T>(data, Lifecycle.stable(), loadingErrors, resourceManager);
         }
      };
      return load(loaderFactory, contextRegistries, registriesToLoad, executor);
   }

   public static CompletableFuture<RegistryAccess.Frozen> load(final Map<ResourceKey<? extends Registry<?>>, NetworkedRegistryData> entries, final ResourceProvider knownDataSource, final List<HolderLookup.RegistryLookup<?>> contextRegistries, final List<RegistryData<?>> registriesToLoad, final Executor executor) {
      LoaderFactory loaderFactory = new LoaderFactory() {
         public <T> RegistryLoadTask<T> create(final RegistryData<T> data, final Map<ResourceKey<?>, Exception> loadingErrors) {
            return new NetworkRegistryLoadTask<T>(data, Lifecycle.stable(), loadingErrors, entries, knownDataSource);
         }
      };
      return load(loaderFactory, contextRegistries, registriesToLoad, executor);
   }

   private static CompletableFuture<RegistryAccess.Frozen> load(final LoaderFactory loaderFactory, final List<HolderLookup.RegistryLookup<?>> contextRegistries, final List<RegistryData<?>> registriesToLoad, final Executor executor) {
      return CompletableFuture.supplyAsync(() -> {
         Map<ResourceKey<?>, Exception> loadingErrors = new ConcurrentHashMap();
         List<RegistryLoadTask<?>> loadTasks = (List)registriesToLoad.stream().map((r) -> loaderFactory.create(r, loadingErrors)).collect(Collectors.toUnmodifiableList());
         RegistryOps.RegistryInfoLookup contextAndNewRegistries = createContext(contextRegistries, loadTasks);
         int taskCount = loadTasks.size();
         CompletableFuture<?>[] loadCompletions = new CompletableFuture[taskCount];

         for(int i = 0; i < taskCount; ++i) {
            loadCompletions[i] = ((RegistryLoadTask)loadTasks.get(i)).load(contextAndNewRegistries, executor);
         }

         return CompletableFuture.allOf(loadCompletions).thenApplyAsync((ignored) -> {
            List<RegistryLoadTask<?>> frozenRegistries = loadTasks.stream().filter((task) -> task.freezeRegistry(loadingErrors)).toList();
            if (!loadingErrors.isEmpty()) {
               throw logErrors(loadingErrors);
            } else {
               List<? extends Registry<?>> registries = frozenRegistries.stream().flatMap((task) -> task.validateRegistry(loadingErrors).stream()).toList();
               if (!loadingErrors.isEmpty()) {
                  throw logErrors(loadingErrors);
               } else {
                  return (new RegistryAccess.ImmutableRegistryAccess(registries)).freeze();
               }
            }
         }, executor);
      }, executor).thenCompose((c) -> c);
   }

   private static RegistryOps.RegistryInfoLookup createContext(final List<HolderLookup.RegistryLookup<?>> contextRegistries, final List<RegistryLoadTask<?>> newRegistriesAndLoaders) {
      final Map<ResourceKey<? extends Registry<?>>, RegistryOps.RegistryInfo<?>> result = new HashMap();
      contextRegistries.forEach((e) -> result.put(e.key(), createInfoForContextRegistry(e)));
      newRegistriesAndLoaders.forEach((e) -> result.put(e.registryKey(), e.createRegistryInfo()));
      return new RegistryOps.RegistryInfoLookup() {
         public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key) {
            return Optional.ofNullable((RegistryOps.RegistryInfo)result.get(key));
         }
      };
   }

   private static <T> RegistryOps.RegistryInfo<T> createInfoForContextRegistry(final HolderLookup.RegistryLookup<T> lookup) {
      return new RegistryOps.RegistryInfo<T>(lookup, lookup, lookup.registryLifecycle());
   }

   private static ReportedException logErrors(final Map<ResourceKey<?>, Exception> loadingErrors) {
      printFullDetailsToLog(loadingErrors);
      return createReportWithBriefInfo(loadingErrors);
   }

   private static void printFullDetailsToLog(final Map<ResourceKey<?>, Exception> loadingErrors) {
      StringWriter collectedErrors = new StringWriter();
      PrintWriter errorPrinter = new PrintWriter(collectedErrors);
      Map<Identifier, Map<Identifier, Exception>> errorsByRegistry = (Map)loadingErrors.entrySet().stream().collect(Collectors.groupingBy((e) -> ((ResourceKey)e.getKey()).registry(), Collectors.toMap((e) -> ((ResourceKey)e.getKey()).identifier(), Map.Entry::getValue)));
      errorsByRegistry.entrySet().stream().sorted(Entry.comparingByKey()).forEach((registryEntry) -> {
         errorPrinter.printf(Locale.ROOT, "> Errors in registry %s:%n", registryEntry.getKey());
         ((Map)registryEntry.getValue()).entrySet().stream().sorted(Entry.comparingByKey()).forEach((elementError) -> {
            errorPrinter.printf(Locale.ROOT, ">> Errors in element %s:%n", elementError.getKey());
            ((Exception)elementError.getValue()).printStackTrace(errorPrinter);
         });
      });
      errorPrinter.flush();
      LOGGER.error("Registry loading errors:\n{}", collectedErrors);
   }

   private static ReportedException createReportWithBriefInfo(final Map<ResourceKey<?>, Exception> loadingErrors) {
      CrashReport report = CrashReport.forThrowable(new IllegalStateException("Failed to load registries due to errors"), "Registry Loading");
      CrashReportCategory errors = report.addCategory("Loading info");
      errors.setDetail("Errors", (CrashReportDetail)(() -> {
         StringBuilder briefDetails = new StringBuilder();
         loadingErrors.entrySet().stream().sorted(Entry.comparingByKey(ERROR_KEY_COMPARATOR)).forEach((e) -> briefDetails.append("\n\t\t").append(((ResourceKey)e.getKey()).registry()).append("/").append(((ResourceKey)e.getKey()).identifier()).append(": ").append(((Exception)e.getValue()).getMessage()));
         return briefDetails.toString();
      }));
      return new ReportedException(report);
   }

   static {
      WORLDGEN_REGISTRIES = List.of(new RegistryData(Registries.DIMENSION_TYPE, DimensionType.DIRECT_CODEC), new RegistryData(Registries.BIOME, Biome.DIRECT_CODEC), new RegistryData(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC), new RegistryData(Registries.CARVER, WorldCarver.DIRECT_CODEC), new RegistryData(Registries.FEATURE, Feature.DIRECT_CODEC), new RegistryData(Registries.PLACED_FEATURE, PlacedFeature.DIRECT_CODEC), new RegistryData(Registries.STRUCTURE, Structure.DIRECT_CODEC), new RegistryData(Registries.STRUCTURE_SET, StructureSet.DIRECT_CODEC), new RegistryData(Registries.PROCESSOR_LIST, StructureProcessorType.DIRECT_CODEC), new RegistryData(Registries.TEMPLATE_POOL, StructureTemplatePool.DIRECT_CODEC), new RegistryData(Registries.NOISE_SETTINGS, NoiseGeneratorSettings.DIRECT_CODEC), new RegistryData(Registries.NOISE, NormalNoise.NoiseParameters.DIRECT_CODEC), new RegistryData(Registries.DENSITY_FUNCTION, DensityFunctions.DIRECT_CODEC), new RegistryData(Registries.MATERIAL_RULE, SurfaceRules.RuleSource.DIRECT_CODEC), new RegistryData(Registries.MATERIAL_CONDITION, SurfaceRules.ConditionSource.DIRECT_CODEC), new RegistryData(Registries.WORLD_PRESET, WorldPreset.DIRECT_CODEC), new RegistryData(Registries.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPreset.DIRECT_CODEC), new RegistryData(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC), new RegistryData(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC), new RegistryData(Registries.TRIAL_SPAWNER_CONFIG, TrialSpawnerConfig.DIRECT_CODEC), new RegistryData(Registries.WOLF_VARIANT, WolfVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.WOLF_SOUND_VARIANT, WolfSoundVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.PIG_VARIANT, PigVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.PIG_SOUND_VARIANT, PigSoundVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.FROG_VARIANT, FrogVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.CAT_VARIANT, CatVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.CAT_SOUND_VARIANT, CatSoundVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.COW_VARIANT, CowVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.COW_SOUND_VARIANT, CowSoundVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.SULFUR_CUBE_ARCHETYPE, SulfurCubeArchetype.DIRECT_CODEC), new RegistryData(Registries.CHICKEN_VARIANT, ChickenVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.CHICKEN_SOUND_VARIANT, ChickenSoundVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.ZOMBIE_NAUTILUS_VARIANT, ZombieNautilusVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC), new RegistryData(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterList.DIRECT_CODEC), new RegistryData(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC), new RegistryData(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC), new RegistryData(Registries.ENCHANTMENT_PROVIDER, EnchantmentProvider.DIRECT_CODEC), new RegistryData(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC), new RegistryData(Registries.INSTRUMENT, Instrument.DIRECT_CODEC), new RegistryData(Registries.TEST_ENVIRONMENT, TestEnvironmentDefinition.DIRECT_CODEC), new RegistryData(Registries.TEST_INSTANCE, GameTestInstance.DIRECT_CODEC), new RegistryData(Registries.DIALOG, Dialog.DIRECT_CODEC), new RegistryData(Registries.WORLD_CLOCK, WorldClock.DIRECT_CODEC), new RegistryData(Registries.TIMELINE, Timeline.DIRECT_CODEC, Timeline::validateRegistry), new RegistryData(Registries.VILLAGER_TRADE, VillagerTrade.CODEC), new RegistryData(Registries.TRADE_SET, TradeSet.CODEC), new RegistryData(Registries.DECORATED_POT_PATTERN, DecoratedPotPattern.CODEC));
      DIMENSION_REGISTRIES = List.of(new RegistryData(Registries.LEVEL_STEM, LevelStem.CODEC));
      SYNCHRONIZED_REGISTRIES = List.of(new RegistryData(Registries.BIOME, Biome.NETWORK_CODEC), new RegistryData(Registries.CHAT_TYPE, ChatType.DIRECT_CODEC), new RegistryData(Registries.TRIM_PATTERN, TrimPattern.DIRECT_CODEC), new RegistryData(Registries.TRIM_MATERIAL, TrimMaterial.DIRECT_CODEC), new RegistryData(Registries.WOLF_VARIANT, WolfVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.WOLF_SOUND_VARIANT, WolfSoundVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.PIG_VARIANT, PigVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.PIG_SOUND_VARIANT, PigSoundVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.FROG_VARIANT, FrogVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.CAT_VARIANT, CatVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.CAT_SOUND_VARIANT, CatSoundVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.COW_SOUND_VARIANT, CowSoundVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.COW_VARIANT, CowVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.CHICKEN_SOUND_VARIANT, ChickenSoundVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.CHICKEN_VARIANT, ChickenVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.ZOMBIE_NAUTILUS_VARIANT, ZombieNautilusVariant.NETWORK_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.PAINTING_VARIANT, PaintingVariant.DIRECT_CODEC, RegistryValidator.nonEmpty()), new RegistryData(Registries.SULFUR_CUBE_ARCHETYPE, SulfurCubeArchetype.DIRECT_CODEC), new RegistryData(Registries.DIMENSION_TYPE, DimensionType.NETWORK_CODEC), new RegistryData(Registries.DAMAGE_TYPE, DamageType.DIRECT_CODEC), new RegistryData(Registries.BANNER_PATTERN, BannerPattern.DIRECT_CODEC), new RegistryData(Registries.ENCHANTMENT, Enchantment.DIRECT_CODEC), new RegistryData(Registries.JUKEBOX_SONG, JukeboxSong.DIRECT_CODEC), new RegistryData(Registries.INSTRUMENT, Instrument.DIRECT_CODEC), new RegistryData(Registries.TEST_ENVIRONMENT, TestEnvironmentDefinition.DIRECT_CODEC), new RegistryData(Registries.TEST_INSTANCE, GameTestInstance.DIRECT_CODEC), new RegistryData(Registries.DIALOG, Dialog.DIRECT_CODEC), new RegistryData(Registries.WORLD_CLOCK, WorldClock.DIRECT_CODEC), new RegistryData(Registries.TIMELINE, Timeline.NETWORK_CODEC), new RegistryData(Registries.DECORATED_POT_PATTERN, DecoratedPotPattern.CODEC));
   }

   public static record RegistryData<T>(ResourceKey<? extends Registry<T>> key, Codec<T> elementCodec, RegistryValidator<T> validator) {
      private RegistryData(final ResourceKey<? extends Registry<T>> key, final Codec<T> elementCodec) {
         this(key, elementCodec, RegistryValidator.none());
      }

      public RegistryData {
         super();
      }

      public void runWithArguments(final BiConsumer<ResourceKey<? extends Registry<T>>, Codec<T>> output) {
         output.accept(this.key, this.elementCodec);
      }
   }

   public static record NetworkedRegistryData(List<RegistrySynchronization.PackedRegistryEntry> elements, TagNetworkSerialization.NetworkPayload tags) {
      public NetworkedRegistryData {
         super();
      }
   }

   @FunctionalInterface
   private interface LoaderFactory {
      <T> RegistryLoadTask<T> create(RegistryData<T> data, Map<ResourceKey<?>, Exception> loadingErrors);
   }
}
