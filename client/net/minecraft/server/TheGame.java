package net.minecraft.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SystemReport;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ClientboundSetTimePacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.level.progress.ChunkProgressListenerFactory;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.players.PlayerList;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.profiling.jfr.callback.ProfiledDuration;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.Difficulty;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.TicketStorage;
import net.minecraft.world.level.WorldDataConfiguration;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.CommandStorage;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class TheGame implements AutoCloseable {
   private static final int SPAWN_POSITION_SEARCH_RADIUS = 5;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final BlockPos DEFAULT_SPAWN = new BlockPos(13, 2, 8);
   public static final float SPAWN_ANGLE = 90.0F;
   private final LevelStorageSource.LevelStorageAccess storageSource;
   private final LayeredRegistryAccess<RegistryLayer> registries;
   private final Map<ResourceKey<Level>, ServerLevel> levels = new LinkedHashMap();
   private final PackRepository packRepository;
   private final ServerFunctionManager functionManager;
   private ReloadableResources resources;
   private final StructureTemplateManager structureTemplateManager;
   private final WorldData worldData;
   private final PotionBrewing potionBrewing;
   private FuelValues fuelValues;
   private final ServerScoreboard scoreboard;
   @Nullable
   private CommandStorage commandStorage;
   private final CustomBossEvents customBossEvents = new CustomBossEvents();
   private final MinecraftServer server;
   private final PlayerList playerList;
   private final ServerTickRateManager tickRateManager;

   private TheGame(MinecraftServer var1, PackRepository var2, WorldStem var3, LevelStorageSource.LevelStorageAccess var4, Function<TheGame, PlayerList> var5) {
      super();
      this.storageSource = var4;
      this.server = var1;
      this.registries = var3.registries();
      this.worldData = var3.worldData();
      this.playerList = (PlayerList)var5.apply(this);
      PlayerList var10003 = this.playerList;
      Objects.requireNonNull(var10003);
      this.scoreboard = new ServerScoreboard(var10003::broadcastAll);
      if (!this.registries.compositeAccess().lookupOrThrow(Registries.LEVEL_STEM).containsKey(LevelStem.OVERWORLD)) {
         throw new IllegalStateException("Missing Overworld dimension data");
      } else {
         this.packRepository = var2;
         this.resources = new ReloadableResources(var3.resourceManager(), var3.dataPackResources());
         this.functionManager = new ServerFunctionManager(this, this.resources.managers.getFunctionLibrary());
         HolderLookup.RegistryLookup var6 = this.registries.compositeAccess().lookupOrThrow(Registries.BLOCK).filterFeatures(this.worldData.enabledFeatures());
         this.structureTemplateManager = new StructureTemplateManager(var3.resourceManager(), var4, var1.getFixerUpper(), var6);
         this.potionBrewing = PotionBrewing.bootstrap(this.worldData.enabledFeatures());
         this.resources.managers.getRecipeManager().finalizeRecipeLoading(this.worldData.enabledFeatures());
         this.fuelValues = FuelValues.vanillaBurnTimes(this.registries.compositeAccess(), this.worldData.enabledFeatures());
         this.tickRateManager = new ServerTickRateManager(this);
      }
   }

   public MinecraftServer server() {
      return this.server;
   }

   public BlockableEventLoop<?> eventLoop() {
      return this.server;
   }

   public ChunkIOErrorReporter chunkIOErrorReporter() {
      return this.server;
   }

   public PlayerList playerList() {
      return this.playerList;
   }

   public void close() {
      for(ServerLevel var2 : this.levels.values()) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (IOException var4) {
               LOGGER.error("Exception closing the level", var4);
            }
         }
      }

      this.resources.close();
   }

   public PackRepository getPackRepository() {
      return this.packRepository;
   }

   public Commands getCommands() {
      return this.resources.managers.getCommands();
   }

   public ServerAdvancementManager getAdvancements() {
      return this.resources.managers.getAdvancements();
   }

   public ServerFunctionManager getFunctions() {
      return this.functionManager;
   }

   public final ServerLevel overworld() {
      return (ServerLevel)this.levels.get(Level.OVERWORLD);
   }

   @Nullable
   public ServerLevel getLevel(ResourceKey<Level> var1) {
      return (ServerLevel)this.levels.get(var1);
   }

   public Set<ResourceKey<Level>> levelKeys() {
      return this.levels.keySet();
   }

   public Collection<ServerLevel> getAllLevels() {
      return this.levels.values();
   }

   public GameType getDefaultGameType() {
      return this.worldData.getGameType();
   }

   public boolean isHardcore() {
      return this.worldData.isHardcore();
   }

   public GameRules getGameRules() {
      return this.overworld().getGameRules();
   }

   public StructureTemplateManager getStructureManager() {
      return this.structureTemplateManager;
   }

   public WorldData getWorldData() {
      return this.worldData;
   }

   public RegistryAccess.Frozen registryAccess() {
      return this.registries.compositeAccess();
   }

   public LayeredRegistryAccess<RegistryLayer> registries() {
      return this.registries;
   }

   public ReloadableServerRegistries.Holder reloadableRegistries() {
      return this.resources.managers.fullRegistries();
   }

   public ResourceManager getResourceManager() {
      return this.resources.resourceManager;
   }

   public RecipeManager getRecipeManager() {
      return this.resources.managers.getRecipeManager();
   }

   public boolean isSpawningMonsters() {
      return this.worldData.getDifficulty() != Difficulty.PEACEFUL;
   }

   public void setDefaultGameType(GameType var1) {
      this.worldData.setGameType(var1);
   }

   public PotionBrewing potionBrewing() {
      return this.potionBrewing;
   }

   public FuelValues fuelValues() {
      return this.fuelValues;
   }

   public ServerScoreboard getScoreboard() {
      return this.scoreboard;
   }

   public ServerTickRateManager tickRateManager() {
      return this.tickRateManager;
   }

   public DataFixer getFixerUpper() {
      return this.server.getFixerUpper();
   }

   public CommandStorage getCommandStorage() {
      if (this.commandStorage == null) {
         throw new NullPointerException("Called before server init");
      } else {
         return this.commandStorage;
      }
   }

   public CustomBossEvents getCustomBossEvents() {
      return this.customBossEvents;
   }

   public static TheGame create(MinecraftServer var0, PackRepository var1, WorldStem var2, LevelStorageSource.LevelStorageAccess var3, ChunkProgressListenerFactory var4, Function<TheGame, PlayerList> var5) {
      TheGame var6 = new TheGame(var0, var1, var2, var3, var5);
      if (!JvmProfiler.INSTANCE.isRunning()) {
      }

      boolean var7 = false;
      ProfiledDuration var8 = JvmProfiler.INSTANCE.onWorldLoadedStarted();
      var6.worldData.setModdedInfo(var0.getServerModName(), var0.getModdedStatus().shouldReportAsModified());
      ChunkProgressListener var9 = var4.create(var6.worldData.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS));
      var6.createLevels(var9);
      var0.forceDifficulty(var6);
      var6.prepareLevels(var0, var9);
      if (var8 != null) {
         var8.finish(true);
      }

      if (var7) {
         try {
            JvmProfiler.INSTANCE.stop();
         } catch (Throwable var11) {
            LOGGER.warn("Failed to stop JFR profiling", var11);
         }
      }

      return var6;
   }

   private void createLevels(ChunkProgressListener var1) {
      ServerLevelData var2 = this.worldData.overworldData();
      boolean var3 = this.worldData.isDebugWorld();
      Registry var4 = this.registries.compositeAccess().lookupOrThrow(Registries.LEVEL_STEM);
      WorldOptions var5 = this.worldData.worldGenOptions();
      long var6 = var5.seed();
      long var8 = BiomeManager.obfuscateSeed(var6);
      ImmutableList var10 = ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new VillageSiege(), new WanderingTraderSpawner(var2));
      LevelStem var11 = (LevelStem)var4.getValue(LevelStem.OVERWORLD);
      Holder var12 = (Holder)this.worldData.hubDimensionType().orElse(var11.type());
      ServerLevel var13 = new ServerLevel(this, this.server.taskExecutor(), this.storageSource, var2, Level.OVERWORLD, var12, var1, var3, var8, var10, true, (RandomSequences)null);
      this.levels.put(Level.OVERWORLD, var13);
      DimensionDataStorage var14 = var13.getDataStorage();
      this.readScoreboard(var14);
      this.commandStorage = new CommandStorage(var14);
      WorldBorder var15 = var13.getWorldBorder();
      if (!var2.isInitialized()) {
         try {
            setInitialSpawn(var13, var2, var5.generateBonusChest(), var3);
            var2.setInitialized(true);
            if (var3) {
               this.setupDebugLevel(this.worldData);
            }
         } catch (Throwable var24) {
            CrashReport var17 = CrashReport.forThrowable(var24, "Exception initializing level");

            try {
               var13.fillReportDetails(var17);
            } catch (Throwable var23) {
            }

            throw new ReportedException(var17);
         }

         var2.setInitialized(true);
      }

      this.playerList().addWorldborderListener(var13);
      if (this.worldData.getCustomBossEvents() != null) {
         this.getCustomBossEvents().load(this.worldData.getCustomBossEvents(), this.registryAccess());
      }

      RandomSequences var16 = var13.getRandomSequences();

      for(Map.Entry var18 : var4.entrySet()) {
         ResourceKey var19 = (ResourceKey)var18.getKey();
         if (var19 != LevelStem.OVERWORLD) {
            ResourceKey var20 = ResourceKey.create(Registries.DIMENSION, var19.location());
            DerivedLevelData var21 = new DerivedLevelData(this.worldData, var2);
            ServerLevel var22 = new ServerLevel(this, this.server.taskExecutor(), this.storageSource, var21, var20, ((LevelStem)var18.getValue()).type(), var1, var3, var8, ImmutableList.of(), false, var16);
            var15.addListener(new BorderChangeListener.DelegateBorderChangeListener(var22.getWorldBorder()));
            this.levels.put(var20, var22);
         }
      }

      var15.applySettings(var2.getWorldBorder());
   }

   private void readScoreboard(DimensionDataStorage var1) {
      var1.computeIfAbsent(ServerScoreboard.TYPE);
   }

   private static void setInitialSpawn(ServerLevel var0, ServerLevelData var1, boolean var2, boolean var3) {
      var1.setSpawn(DEFAULT_SPAWN, 90.0F);
      if (var2) {
         ServerChunkCache var4 = var0.getChunkSource();
         var0.registryAccess().lookup(Registries.CONFIGURED_FEATURE).flatMap((var0x) -> var0x.get(MiscOverworldFeatures.BONUS_CHEST)).ifPresent((var3x) -> ((ConfiguredFeature)var3x.value()).place(var0, var4.getGenerator(), var0.random, var1.getSpawnPos()));
      }

   }

   private void setupDebugLevel(WorldData var1) {
      var1.setDifficulty(Difficulty.PEACEFUL);
      var1.setDifficultyLocked(true);
      ServerLevelData var2 = var1.overworldData();
      var2.setRaining(false);
      var2.setThundering(false);
      var2.setClearWeatherTime(1000000000);
      var2.setDayTime(6000L);
      var2.setGameType(GameType.SPECTATOR);
   }

   private void prepareLevels(MinecraftServer var1, ChunkProgressListener var2) {
      ServerLevel var3 = this.overworld();
      LOGGER.info("Preparing start region for dimension {}", var3.dimension().location());
      BlockPos var4 = var3.getSharedSpawnPos();
      var2.updateSpawnPos(new ChunkPos(var4));
      var1.theGameButSuperSpecialOneForLoading = this;
      ServerChunkCache var5 = var3.getChunkSource();
      var1.runNextTickNow();
      var3.setDefaultSpawnPos(var4, var3.getSharedSpawnAngle());
      int var6 = this.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS);
      int var7 = var6 > 0 ? Mth.square(ChunkProgressListener.calculateDiameter(var6)) : 0;

      while(var5.getTickingGenerated() < var7) {
         var1.runTicksDuringServerPrepare();
      }

      var1.runTicksDuringServerPrepare();

      for(ServerLevel var9 : this.levels.values()) {
         TicketStorage var10 = (TicketStorage)var9.getDataStorage().get(TicketStorage.TYPE);
         if (var10 != null) {
            var10.activateAllDeactivatedTickets();
         }
      }

      var1.runTicksDuringServerPrepare();
      var1.theGameButSuperSpecialOneForLoading = null;
      var2.stop();
      this.updateMobSpawningFlags();
   }

   public boolean saveAllChunks(boolean var1, boolean var2, boolean var3) {
      boolean var4 = false;

      for(ServerLevel var6 : this.getAllLevels()) {
         if (!var1) {
            LOGGER.info("Saving chunks for level '{}'/{}", var6, var6.dimension().location());
         }

         var6.save((ProgressListener)null, var2, var6.noSave && !var3);
         var4 = true;
      }

      ServerLevel var7 = this.overworld();
      ServerLevelData var8 = this.worldData.overworldData();
      var8.setWorldBorder(var7.getWorldBorder().createSettings());
      this.worldData.setCustomBossEvents(this.getCustomBossEvents().save(this.registryAccess()));
      this.storageSource.saveDataTag(this.registryAccess(), this.worldData, this.playerList().getSingleplayerData());
      if (var2) {
         Stream.concat(Stream.of("world", "DIM-1", "DIM1", "Clive", "Sardines"), this.getAllLevels().stream().map((var0) -> var0.getChunkSource().chunkMap.getStorageName())).distinct().forEach((var0) -> LOGGER.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", var0));
         LOGGER.info("ThreadedAnvilChunkStorage: All dimensions are saved");
      }

      return var4;
   }

   public void fillSystemReport(SystemReport var1) {
      var1.setDetail("Active Data Packs", (Supplier)(() -> PackRepository.displayPackList(this.packRepository.getSelectedPacks())));
      var1.setDetail("Available Data Packs", (Supplier)(() -> PackRepository.displayPackList(this.packRepository.getAvailablePacks())));
      var1.setDetail("Enabled Feature Flags", (Supplier)(() -> (String)FeatureFlags.REGISTRY.toNames(this.worldData.enabledFeatures()).stream().map(ResourceLocation::toString).collect(Collectors.joining(", "))));
      var1.setDetail("World Generation", (Supplier)(() -> this.worldData.worldGenSettingsLifecycle().toString()));
      var1.setDetail("World Seed", (Supplier)(() -> String.valueOf(this.worldData.worldGenOptions().seed())));
   }

   public void saveDebugReport(Path var1) throws IOException {
      Path var2 = var1.resolve("levels");

      try {
         for(Map.Entry var4 : this.levels.entrySet()) {
            ResourceLocation var5 = ((ResourceKey)var4.getKey()).location();
            Path var6 = var2.resolve(var5.getNamespace()).resolve(var5.getPath());
            Files.createDirectories(var6);
            ((ServerLevel)var4.getValue()).saveDebugReport(var6);
         }

         this.dumpGameRules(var1.resolve("gamerules.txt"));
      } catch (IOException var7) {
         LOGGER.warn("Failed to save debug report", var7);
      }

   }

   private void dumpGameRules(Path var1) throws IOException {
      BufferedWriter var2 = Files.newBufferedWriter(var1);

      try {
         final ArrayList var3 = Lists.newArrayList();
         final GameRules var4 = this.getGameRules();
         var4.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
            public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1, GameRules.Type<T> var2) {
               var3.add(String.format(Locale.ROOT, "%s=%s\n", var1.getId(), var4.getRule(var1)));
            }
         });

         for(String var6 : var3) {
            ((Writer)var2).write(var6);
         }
      } catch (Throwable var8) {
         if (var2 != null) {
            try {
               ((Writer)var2).close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var2 != null) {
         ((Writer)var2).close();
      }

   }

   public void setDifficulty(Difficulty var1, boolean var2) {
      if (var2 || !this.worldData.isDifficultyLocked()) {
         this.worldData.setDifficulty(this.worldData.isHardcore() ? Difficulty.HARD : var1);
         this.updateMobSpawningFlags();
         this.playerList().getPlayers().forEach(TheGame::sendDifficultyUpdate);
      }
   }

   public void setDifficultyLocked(PlayerList var1, boolean var2) {
      this.worldData.setDifficultyLocked(var2);
      var1.getPlayers().forEach(TheGame::sendDifficultyUpdate);
   }

   public static void sendDifficultyUpdate(ServerPlayer var0) {
      LevelData var1 = var0.level().getLevelData();
      var0.connection.send(new ClientboundChangeDifficultyPacket(var1.getDifficulty(), var1.isDifficultyLocked()));
   }

   private void updateMobSpawningFlags() {
      for(ServerLevel var2 : this.getAllLevels()) {
         var2.setSpawnSettings(this.server.isSpawningMonsters(this));
      }

   }

   public CompletableFuture<Void> reloadResources(Collection<String> var1) {
      CompletableFuture var2 = CompletableFuture.supplyAsync(() -> {
         Stream var10000 = var1.stream();
         PackRepository var10001 = this.packRepository;
         Objects.requireNonNull(var10001);
         return (ImmutableList)var10000.map(var10001::getPack).filter(Objects::nonNull).map(Pack::open).collect(ImmutableList.toImmutableList());
      }, this.server).thenCompose((var1x) -> {
         MultiPackResourceManager var2 = new MultiPackResourceManager(PackType.SERVER_DATA, var1x);
         List var3 = TagLoader.loadTagsForExistingRegistries(var2, this.registries.compositeAccess());
         return ReloadableServerResources.loadResources(var2, this.registries, var3, this.worldData.enabledFeatures(), this.server.isDedicatedServer() ? Commands.CommandSelection.DEDICATED : Commands.CommandSelection.INTEGRATED, this.server.getFunctionCompilationLevel(), this.server.taskExecutor(), this.server).whenComplete((var1, var2x) -> {
            if (var2x != null) {
               var2.close();
            }

         }).thenApply((var1) -> new ReloadableResources(var2, var1));
      }).thenAcceptAsync((var2x) -> {
         this.resources.close();
         this.resources = var2x;
         this.packRepository.setSelected(var1);
         WorldDataConfiguration var3 = new WorldDataConfiguration(PackStuff.getSelectedPacks(this.packRepository, true), this.worldData.enabledFeatures());
         this.worldData.setDataConfiguration(var3);
         this.resources.managers.updateStaticRegistryTags();
         this.resources.managers.getRecipeManager().finalizeRecipeLoading(this.worldData.enabledFeatures());
         this.playerList().saveAll();
         this.playerList().reloadResources(this);
         this.functionManager.replaceLibrary(this.resources.managers.getFunctionLibrary());
         this.structureTemplateManager.onResourceManagerReload(this.resources.resourceManager);
         this.fuelValues = FuelValues.vanillaBurnTimes(this.registries.compositeAccess(), this.worldData.enabledFeatures());
      }, this.server);
      if (this.server.isSameThread()) {
         MinecraftServer var10000 = this.server;
         Objects.requireNonNull(var2);
         var10000.managedBlock(var2::isDone);
      }

      return var2;
   }

   public CommandSourceStack createCommandSourceStack() {
      ServerLevel var1 = this.overworld();
      return new CommandSourceStack(this.server, var1 == null ? Vec3.ZERO : Vec3.atLowerCornerOf(var1.getSharedSpawnPos()), Vec2.ZERO, var1, 4, "Server", Component.literal("Server"), this, (Entity)null);
   }

   public void synchronizeTime(ServerLevel var1) {
      this.playerList().broadcastAll(new ClientboundSetTimePacket(var1.getGameTime(), var1.getDayTime(), var1.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)), var1.dimension());
   }

   public void forceTimeSynchronization() {
      ProfilerFiller var1 = Profiler.get();
      var1.push("timeSync");

      for(ServerLevel var3 : this.getAllLevels()) {
         this.synchronizeTime(var3);
      }

      var1.pop();
   }

   static record ReloadableResources(CloseableResourceManager resourceManager, ReloadableServerResources managers) implements AutoCloseable {
      final CloseableResourceManager resourceManager;
      final ReloadableServerResources managers;

      ReloadableResources(CloseableResourceManager var1, ReloadableServerResources var2) {
         super();
         this.resourceManager = var1;
         this.managers = var2;
      }

      public void close() {
         this.resourceManager.close();
      }
   }
}
