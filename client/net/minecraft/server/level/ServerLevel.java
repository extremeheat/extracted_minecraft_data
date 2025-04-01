package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportType;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.MineExitPools;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundChangeDimensionTypePacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateUnlockedEffectsPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.ServerTickRateManager;
import net.minecraft.server.TheGame;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.MineData;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.ItemExchangeValue;
import net.minecraft.world.item.component.WorldModifiers;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.UnlockCondition;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.MineCrafterBlockEntity;
import net.minecraft.world.level.block.entity.MineTravellingBlockEntity;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import net.minecraft.world.level.gameevent.DynamicGameEventListener;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.mines.MineEvent;
import net.minecraft.world.level.mines.MineEventData;
import net.minecraft.world.level.mines.MineSpawnStrategy;
import net.minecraft.world.level.mines.SpecialMine;
import net.minecraft.world.level.mines.UnlockMode;
import net.minecraft.world.level.mines.WorldEffect;
import net.minecraft.world.level.mines.WorldEffects;
import net.minecraft.world.level.pathfinder.PathTypeCache;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.LevelTicks;
import org.slf4j.Logger;

public class ServerLevel extends Level implements ServerEntityGetter, WorldGenLevel {
   public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
   public static final IntProvider RAIN_DELAY = UniformInt.of(12000, 180000);
   public static final IntProvider RAIN_DURATION = UniformInt.of(12000, 24000);
   private static final IntProvider THUNDER_DELAY = UniformInt.of(12000, 180000);
   public static final IntProvider THUNDER_DURATION = UniformInt.of(3600, 15600);
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int EMPTY_TIME_NO_TICK = 300;
   private static final int MAX_SCHEDULED_TICKS_PER_TICK = 65536;
   public static final float EXPERIENCE_KEEP_ON_DEATH_FACTOR = 0.0F;
   public static final int BASE_WIN_EXPERIENCE = 10;
   public BlockPos WARDEN_ARENA_POS = new BlockPos(0, 0, 0);
   public BlockPos DIRTY_ICE_BALL_POS = new BlockPos(0, 0, 0);
   final List<ServerPlayer> players = Lists.newArrayList();
   private final ServerChunkCache chunkSource;
   private final TheGame theGame;
   private final ServerLevelData serverLevelData;
   private int lastSpawnChunkRadius;
   final EntityTickList entityTickList = new EntityTickList();
   private final PersistentEntitySectionManager<Entity> entityManager;
   private final GameEventDispatcher gameEventDispatcher;
   public boolean noSave;
   private final SleepStatus sleepStatus;
   private int emptyTime;
   private final PortalForcer portalForcer;
   private final LevelTicks<Block> blockTicks = new LevelTicks<Block>(this::isPositionTickingWithEntitiesLoaded);
   private final LevelTicks<Fluid> fluidTicks = new LevelTicks<Fluid>(this::isPositionTickingWithEntitiesLoaded);
   private final PathTypeCache pathTypesByPosCache = new PathTypeCache();
   final Set<Mob> navigatingMobs = new ObjectOpenHashSet();
   volatile boolean isUpdatingNavigations;
   protected final Raids raids;
   protected final MineData mineData;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents = new ObjectLinkedOpenHashSet();
   private final List<BlockEventData> blockEventsToReschedule = new ArrayList(64);
   private boolean handlingTick;
   private final List<CustomSpawner> customSpawners;
   @Nullable
   public EndDragonFight dragonFight;
   final Int2ObjectMap<EnderDragonPart> dragonParts = new Int2ObjectOpenHashMap();
   private final StructureManager structureManager;
   private final StructureCheck structureCheck;
   private final boolean tickTime;
   private final RandomSequences randomSequences;
   private final Optional<SpecialMine> specialMine;
   private final Set<WorldEffect> activeEffects;
   private boolean eventsStarted;
   private final List<MineEvent> events;
   private final List<MineEvent> uncompletedEvents = new ArrayList();

   public ServerLevel(TheGame var1, Executor var2, LevelStorageSource.LevelStorageAccess var3, ServerLevelData var4, ResourceKey<Level> var5, Holder<DimensionType> var6, ChunkProgressListener var7, boolean var8, long var9, List<CustomSpawner> var11, boolean var12, @Nullable RandomSequences var13) {
      super(var4, var5, var1.registryAccess(), var6, false, var8, var9, var1.server().getMaxChainedNeighborUpdates());
      this.tickTime = var12;
      this.theGame = var1;
      this.customSpawners = var11;
      this.serverLevelData = var4;
      Optional var14 = this.registryAccess().get(Registries.levelToLevelStem(var5));
      this.activeEffects = (Set)var14.map(Holder::value).map(LevelStem::effects).map(ObjectArraySet::new).orElseGet(Set::of);
      this.specialMine = var14.map(Holder::value).flatMap(LevelStem::mine);
      ChunkGenerator var15 = LevelStem.generator(this.registryAccess(), (Holder.Reference)var14.get());
      MinecraftServer var16 = var1.server();
      boolean var17 = var16.forceSynchronousWrites();
      DataFixer var18 = var1.getFixerUpper();
      EntityStorage var19 = new EntityStorage(new SimpleRegionStorage(new RegionStorageInfo(var3.getLevelId(), var5, "entities"), var3.getDimensionPath(var5).resolve("entities"), var18, var17, DataFixTypes.ENTITY_CHUNK), this, var1.eventLoop());
      this.entityManager = new PersistentEntitySectionManager<Entity>(Entity.class, new EntityCallbacks(), var19);
      PlayerList var20 = var1.playerList();
      StructureTemplateManager var10006 = var1.getStructureManager();
      int var10009 = var20.getViewDistance();
      int var10010 = var20.getSimulationDistance();
      PersistentEntitySectionManager var10013 = this.entityManager;
      Objects.requireNonNull(var10013);
      this.chunkSource = new ServerChunkCache(this, var3, var18, var10006, var2, var15, var10009, var10010, var17, var7, var10013::updateChunkStatus, () -> var1.overworld().getDataStorage());
      this.chunkSource.getGeneratorState().ensureStructuresGenerated();
      this.portalForcer = new PortalForcer(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.getWorldBorder().setAbsoluteMaxSize(var16.getAbsoluteMaxWorldSize());
      this.raids = (Raids)this.getDataStorage().computeIfAbsent(Raids.getType(this.dimensionTypeRegistration()));
      this.mineData = (MineData)this.getDataStorage().computeIfAbsent(MineData.TYPE);
      this.mineData.setMine(!this.activeEffects.isEmpty());
      if (!var16.isSingleplayer()) {
         var4.setGameType(var1.getDefaultGameType());
      }

      long var21 = var1.getWorldData().worldGenOptions().seed();
      this.structureCheck = new StructureCheck(this.chunkSource.chunkScanner(), this.registryAccess(), var1.getStructureManager(), var5, var15, this.chunkSource.randomState(), this, var15.getBiomeSource(), var21, var18);
      this.structureManager = new StructureManager(this, var1.getWorldData().worldGenOptions(), this.structureCheck);
      if (this.dimension() == Level.END && this.dimensionTypeRegistration().is(BuiltinDimensionTypes.END)) {
         this.dragonFight = new EndDragonFight(this, var21, var1.getWorldData().endDragonFightData());
      } else {
         this.dragonFight = null;
      }

      MineEventData var23 = (MineEventData)var1.getWorldData().events().get(this.dimension());
      if (var23 != null) {
         this.eventsStarted = var23.started();
         this.events = new ArrayList(var23.events());
         this.uncompletedEvents.addAll(var23.events());
         this.uncompletedEvents.removeIf((var0) -> var0.getStatus() == MineEvent.Status.WON);
      } else {
         this.eventsStarted = false;
         this.events = new ArrayList();
      }

      this.sleepStatus = new SleepStatus();
      this.gameEventDispatcher = new GameEventDispatcher(this);
      this.randomSequences = (RandomSequences)Objects.requireNonNullElseGet(var13, () -> (RandomSequences)this.getDataStorage().computeIfAbsent(RandomSequences.TYPE));
   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void setDragonFight(@Nullable EndDragonFight var1) {
      this.dragonFight = var1;
   }

   public void setWeatherParameters(int var1, int var2, boolean var3, boolean var4) {
      this.serverLevelData.setClearWeatherTime(var1);
      this.serverLevelData.setRainTime(var2);
      this.serverLevelData.setThunderTime(var2);
      this.serverLevelData.setRaining(var3);
      this.serverLevelData.setThundering(var4);
   }

   public Holder<Biome> getUncachedNoiseBiome(int var1, int var2, int var3) {
      return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(var1, var2, var3, this.getChunkSource().randomState().sampler());
   }

   public StructureManager structureManager() {
      return this.structureManager;
   }

   public List<ItemStack> getRewardKeys(BlockPos var1) {
      return this.theGame.reloadableRegistries().getLootTable(BuiltInLootTables.ROOM_REWARD).getRandomItems((new LootParams.Builder(this)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(var1)).create(LootContextParamSets.HUB_REWARD));
   }

   public void tick(BooleanSupplier var1) {
      ProfilerFiller var2 = Profiler.get();
      this.handlingTick = true;
      ServerTickRateManager var3 = this.tickRateManager();
      boolean var4 = ((TickRateManager)var3).runsNormally();
      if (var4) {
         var2.push("world border");
         this.getWorldBorder().tick();
         var2.popPush("weather");
         this.advanceWeatherCycle();
         var2.pop();
      }

      int var5 = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
      if (this.sleepStatus.areEnoughSleeping(var5) && this.sleepStatus.areEnoughDeepSleeping(var5, this.players)) {
         if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            long var6 = this.levelData.getDayTime() + 24000L;
            this.setDayTime(var6 - var6 % 24000L);
         }

         this.wakeUpAllPlayers();
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && this.isRaining()) {
            this.resetWeatherCycle();
         }
      }

      this.updateSkyBrightness();
      if (var4) {
         this.tickTime();
         this.tickEvents();
      }

      var2.push("tickPending");
      if (!this.isDebug() && var4) {
         long var8 = this.getGameTime();
         var2.push("blockTicks");
         this.blockTicks.tick(var8, 65536, this::tickBlock);
         var2.popPush("fluidTicks");
         this.fluidTicks.tick(var8, 65536, this::tickFluid);
         var2.pop();
      }

      var2.popPush("raid");
      if (var4) {
         this.raids.tick(this);
      }

      var2.popPush("chunkSource");
      this.getChunkSource().tick(var1, true);
      var2.popPush("blockEvents");
      if (var4) {
         this.runBlockEvents();
      }

      this.handlingTick = false;
      var2.pop();
      boolean var9 = !this.players.isEmpty() || !this.getForceLoadedChunks().isEmpty();
      if (var9) {
         this.resetEmptyTime();
      }

      if (var9 || this.emptyTime++ < 300) {
         var2.push("entities");
         if (this.dragonFight != null && var4) {
            var2.push("dragonFight");
            this.dragonFight.tick();
            var2.pop();
         }

         this.entityTickList.forEach((var3x) -> {
            if (!var3x.isRemoved()) {
               if (!var3.isEntityFrozen(var3x)) {
                  var2.push("checkDespawn");
                  var3x.checkDespawn();
                  var2.pop();
                  if (var3x instanceof ServerPlayer || this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(var3x.chunkPosition().toLong())) {
                     Entity var4 = var3x.getVehicle();
                     if (var4 != null) {
                        if (!var4.isRemoved() && var4.hasPassenger(var3x)) {
                           return;
                        }

                        var3x.stopRiding();
                     }

                     var2.push("tick");
                     this.guardEntityTick(this::tickNonPassenger, var3x);
                     var2.pop();
                  }
               }
            }
         });
         var2.pop();
         this.tickBlockEntities();
      }

      var2.push("entityManagement");
      this.entityManager.tick();
      var2.pop();
      var2.push("handleInProgressMap");
      this.handleInProgressMine();
      var2.pop();
   }

   private void tickEvents() {
      if (!this.players.isEmpty()) {
         WorldData var1 = this.theGame.getWorldData();
         if (var1.events().get(this.dimension()) == null) {
            this.onMineEntered();
            this.eventsStarted = true;
            var1.events().put(this.dimension(), new MineEventData(true, this.events));
         }

         this.events.forEach((var1x) -> var1x.tick(this));
         if (this.events.stream().anyMatch((var0) -> var0.getStatus() == MineEvent.Status.FAILED)) {
            this.handleMineLoss();
         } else if (this.isActive(WorldEffects.EVENT_EXIT) && !this.uncompletedEvents.isEmpty() && this.uncompletedEvents.stream().allMatch((var0) -> var0.getStatus() == MineEvent.Status.WON)) {
            JigsawPlacement.generateJigsaw(this, this.registryAccess().getOrThrow(MineExitPools.STARTS), Optional.of(ResourceLocation.withDefaultNamespace("start")), 7, ((MineEvent)this.uncompletedEvents.getFirst()).getPosition(), false);
         }

         this.uncompletedEvents.removeIf((var0) -> var0.getStatus() == MineEvent.Status.WON);
      }

   }

   public void startEvent(MineEvent var1) {
      this.events.add(var1);
      this.uncompletedEvents.add(var1);
   }

   public List<MineEvent> events() {
      return this.events;
   }

   private void handleInProgressMine() {
      if (!this.players().isEmpty()) {
         if (!this.isMine()) {
            this.handleMineCountDown((var1x) -> this.leaveForMine(var1x, false, Optional.empty()));

            for(ServerPlayer var8 : this.players) {
               if (this.mineData.hasPlayerDied(var8.getUUID()) && this.theGame.isHardcore()) {
                  var8.setGameMode(GameType.SPECTATOR);
               }
            }

            if (!this.players.isEmpty()) {
               Optional var6 = Optional.empty();

               for(ServerPlayer var11 : this.theGame.playerList().getPlayers()) {
                  if (!var11.isRevisiting() && var11.serverLevel() != this && !var11.serverLevel().isMineCompleted()) {
                     var6 = Optional.of(var11.serverLevel());
                     break;
                  }
               }

               if (var6.isPresent()) {
                  for(ServerPlayer var12 : new ArrayList(this.players())) {
                     ((ServerLevel)var6.get()).teleportAllPlayersToMine(false, Optional.of(var12.getUUID()));
                  }
               }
            }

         } else {
            if (!this.isMineCompleted()) {
               boolean var1 = true;

               for(ServerPlayer var3 : this.players()) {
                  if (!this.mineData.hasPlayerDied(var3.getUUID())) {
                     var1 = false;
                     break;
                  }
               }

               if (var1) {
                  this.handleMineLoss();
               } else {
                  this.handleMineCountDown(this::handleMineWin);
               }
            } else {
               this.handleCompletedMine();
            }

            for(WorldEffect var7 : this.activeEffects) {
               var7.onMineTick().accept(this);
            }

         }
      }
   }

   private void handleMineCountDown(Consumer<BlockPos> var1) {
      if (!this.players().isEmpty()) {
         Optional var2 = this.mineData.getTravellingBlockActivated();
         if (var2.isPresent()) {
            if (this.mineData.countDown()) {
               var1.accept((BlockPos)var2.get());
               this.mineData.resetCountdown();
            } else if (this.mineData.getLeaveCountdown() % 20 == 0) {
               String var3 = this.isMine() ? "leave" : "enter";
               float var4 = 1.0F - (float)this.mineData.getLeaveCountdown() / 200.0F;
               float var5 = 0.75F + 6.0F * var4 * var4;
               if (this.isMine()) {
                  this.theGame.playerList().broadcastSystemMessage(Component.translatable("mine." + var3, this.mineData.getLeaveCountdown() / 20), true);
               } else {
                  this.theGame.playerList().broadcastSystemMessage(Component.translatable("mine." + var3, this.mineData.getLeaveCountdown() / 20).setStyle(Style.EMPTY.withScale(var5)), true);
               }

               this.playSound((Entity)null, (BlockPos)var2.get(), SoundEvents.END_PORTAL_SPAWN, SoundSource.AMBIENT, (float)(0.15 + 0.04 * (double)var5), (float)(0.3 + 0.08 * (double)var5));
            }
         } else if (this.mineData.getLeaveCountdown() != 200) {
            this.mineData.resetCountdown();
         }

      }
   }

   private void handleMineLoss() {
      List var10000 = this.players;
      PlayerTrigger var10001 = CriteriaTriggers.LEVEL_FAILED;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::trigger);
      if (!this.isMineCompleted()) {
         this.markMineCompleted(false);
      }

      this.respawnPlayersIntoHub((var0) -> {
      });
   }

   private void respawnPlayersIntoHub(Consumer<ServerPlayer> var1) {
      for(ServerPlayer var3 : new LinkedList(this.players())) {
         if (!var3.isRevisiting()) {
            TeleportTransition var4 = this.getHubTeleport();
            if (var3.isDeadOrDying() || var3.isSpectator()) {
               var4.newLevel().mineData.addExperienceToDrop((int)((float)var3.getTotalExperienceBasedOnLevels() * 0.0F));
               var3.setExperienceLevels(0);
               var3.setExperiencePoints(0);
            }

            if (this.isMineWon()) {
               CriteriaTriggers.LEVEL_COMPLETED.trigger(var3);
               if (this.isSpecialMine()) {
                  CriteriaTriggers.SPECIAL_MINE_COMPLETED.trigger(var3);
               }
            }

            if (var3.isSpectator() || this.isMineWon()) {
               this.respawnPlayerIntoHub(var3, var1);
            }
         }
      }

   }

   public void respawnPlayerIntoHub(ServerPlayer var1, Consumer<ServerPlayer> var2) {
      if (var1.isDeadOrDying() || var1.isSpectator()) {
         if (this.theGame.server().isHardcore(this.theGame)) {
            var1.setGameMode(GameType.SPECTATOR);
         } else {
            var1.setGameMode(GameType.SURVIVAL);
         }
      }

      ServerPlayer var3 = var1.teleport(this.getHubTeleport());
      if (var3 != null) {
         if (!var1.isRevisiting()) {
            MutableComponent var4 = this.isMineWon() ? Component.translatable("mine.won") : Component.translatable("mine.lost");
            var4.setStyle(Style.EMPTY.withBold(true).withMEGA(true));
            if (this.isMineWon()) {
               var4.withColor(-11010079);
            } else {
               var4.withColor(-65536);
            }

            var1.displayClientMessage(var4, true);
         } else if (!this.theGame.overworld().mineData.hasPlayerDied(var1.getUUID())) {
            var1.setGameMode(GameType.SURVIVAL);
         }

         var3.getFoodData().setFoodLevel(20);
         var3.setHealth(var3.getMaxHealth());
         var3.setRevisiting(false);
         var3.connection.resetPosition();
         var2.accept(var3);
         var3.swapInventoryToHub();
      }
   }

   private TeleportTransition getHubTeleport() {
      ServerLevel var1 = this.theGame.overworld();
      return new TeleportTransition(var1, var1.getSharedSpawnPos().getBottomCenter(), Vec3.ZERO, var1.getSharedSpawnAngle(), 0.0F, TeleportTransition.DO_NOTHING);
   }

   public void handleMineWin(BlockPos var1) {
      this.markMineCompleted(true);
      this.respawnPlayersIntoHub((var1x) -> this.cleanInventoryAndReward(var1x, 0.0F));
   }

   private void cleanInventoryAndReward(ServerPlayer var1, float var2) {
      double var3 = 10.0;
      ArrayList var5 = new ArrayList();

      for(ItemStack var7 : var1.getInventory()) {
         WorldModifiers var8 = (WorldModifiers)var7.get(DataComponents.WORLD_MODIFIERS);
         if (var8 != null) {
            for(WorldEffect var10 : var8.effects()) {
               if (var7.has(DataComponents.WORLD_EFFECT_UNLOCK)) {
                  this.unlockEffect(var10);
               }
            }
         } else if (var7.is(ItemTags.CARRY_OVER)) {
            var5.add(var7.copy());
         } else {
            float var9 = ((ItemExchangeValue)var7.getOrDefault(DataComponents.EXCHANGE_VALUE, Item.NO_EXCHANGE)).getValue(var1, var7);
            var3 += (double)((float)var7.getCount() * var9);
         }

         CriteriaTriggers.INVENTORY_CASHED_IN.trigger(var1, var1.blockPosition(), var7);
      }

      var1.getInventory().clearContent();
      float var13 = 1.0F;

      for(WorldEffect var16 : this.activeEffects) {
         var13 += var16.experienceModifier();
      }

      var3 *= (double)var13;
      var3 *= var1.getAttributeValue(Attributes.EXPERIENCE_GAIN_MODIFIER);
      int var15 = (int)(var3 * (double)var2);
      int var17 = (int)(var3 * (double)(1.0F - var2));
      var1.giveExperiencePoints(var15);

      for(ItemStack var20 : var5) {
         var1.addHubReward(var20);
      }

      var1.serverLevel().mineData.addExperienceToDrop(var17);
      this.theGame.playerList().broadcastSystemMessage(Component.translatable("mine.won.rewards", var1.getName(), var17), false);
   }

   private void handleCompletedMine() {
      if (!this.isMineWon()) {
         this.respawnPlayersIntoHub((var0) -> var0.getInventory().clearContent());
      } else {
         this.respawnPlayersIntoHub((var1) -> this.cleanInventoryAndReward(var1, 1.0F));
      }
   }

   public void dropRewards(BlockPos var1) {
      int var2 = this.mineData.getExperienceToDrop();
      int var3 = this.mineData.getKeysToRoll();
      if (var2 > 0 || var3 > 0) {
         this.addExperienceToMineCrafter(60);
         int var4 = Mth.ceil((float)var2 / 20.0F);

         while(var2 > 0) {
            var2 -= var4;
            ExperienceOrb.awardWithDirection(this, var1.getCenter().add(new Vec3(0.0, 2.0000100135803223, 0.0)), new Vec3(0.0, 1.0, 0.0), var4);
         }

         for(int var5 = 0; var5 < var3; ++var5) {
            List var6 = this.getRewardKeys(var1);
            if (var6.isEmpty()) {
               break;
            }

            for(ItemStack var8 : var6) {
               ItemEntity var9 = new ItemEntity(this, (double)var1.getX(), (double)var1.getY() + 2.5 + 9.999999747378752E-6, (double)var1.getZ(), var8);
               Vec3 var10 = new Vec3((this.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0, this.random.nextDouble() * 0.4, (this.random.nextDouble() * 0.20000000298023224 - 0.10000000149011612) * 2.0);
               var9.push(var10);
               this.addFreshEntity(var9);
            }
         }

         this.mineData.resetKeysToRoll();
         this.mineData.resetExperienceToDrop();
      }
   }

   public void leaveForMine(BlockPos var1, boolean var2, Optional<UUID> var3) {
      if (!var2) {
         this.mineData.resetMineTravvelingBlock();
      }

      BlockEntity var4 = this.getBlockEntity(var1);
      if (var4 instanceof MineTravellingBlockEntity var5) {
         ResourceKey var6 = var5.getTargetDimension();
         ServerLevel var7 = this.theGame.getLevel(var6);

         for(ServerPlayer var9 : this.players()) {
            if ((!var3.isPresent() || ((UUID)var3.get()).equals(var9.getUUID())) && !var9.isSpectator()) {
               var9.swapInventoryFromHub();
            }
         }

         if (var7 == null) {
            this.theGame.server().sayGoodbye().thenAcceptAsync((var3x) -> {
               ServerLevel var4 = var3x.theGame().getLevel(var6);
               if (var4 != null) {
                  var4.teleportAllPlayersToMine(var2, var3);
               }

            }, this.theGame.server());
         } else {
            var7.teleportAllPlayersToMine(var2, var3);
         }

      }
   }

   public void teleportAllPlayersToMine(boolean var1, Optional<UUID> var2) {
      MineSpawnStrategy var3 = ((LevelStem)this.registryAccess().lookupOrThrow(Registries.LEVEL_STEM).getOrThrow(Registries.levelToLevelStem(this.dimension())).value()).spawn();
      Vec3 var4 = var3.getSpawnPosition(this);
      BlockPos.MutableBlockPos var5 = BlockPos.containing(var4).mutable().move(Direction.DOWN);
      if (var5.getY() < this.getMinY()) {
         var5.setY(this.getMinY());
      }

      boolean var6 = !var1 && !this.mineData.hasPlacedStartStructures() && this.mineData.isMine();
      boolean var7 = var3 == MineSpawnStrategy.SURFACE;
      if (var6) {
         this.mineData.setHasPlacedStartStructures(true);
         if (this.isActive(WorldEffects.WARDEN_BOSS_FIGHT)) {
            this.WARDEN_ARENA_POS = new BlockPos(var5.getX() + 40, var5.getY(), var5.getZ());
            ((ConfiguredFeature)this.registryAccess().getOrThrow(MiscOverworldFeatures.WARDEN_ARENA).value()).place(this, this.chunkSource.getGenerator(), this.random, this.WARDEN_ARENA_POS);
            var7 = false;
         }

         if (this.isActive(WorldEffects.KUIPER_WORLD)) {
            int var8 = var5.getX() + 15;
            int var9 = var5.getY();
            int var10 = var5.getZ() - 100;
            int var11 = var5.getX() + 225;
            int var12 = var5.getY() + 200;
            int var13 = var5.getZ() + 100;

            for(int var14 = 0; var14 < 150; ++var14) {
               this.DIRTY_ICE_BALL_POS = new BlockPos(this.random.nextInt(var8, var11), this.random.nextInt(var9, var12), this.random.nextInt(var10, var13));
               ((ConfiguredFeature)this.registryAccess().getOrThrow(MiscOverworldFeatures.DIRTY_ICE_BALL).value()).place(this, this.chunkSource.getGenerator(), this.random, this.DIRTY_ICE_BALL_POS);
               this.DIRTY_ICE_BALL_POS = new BlockPos(this.random.nextInt(var8, var11), this.random.nextInt(var9, var12), this.random.nextInt(var10, var13));
               ((ConfiguredFeature)this.registryAccess().getOrThrow(MiscOverworldFeatures.DIRTY_ICE_BALL_GOLEMS).value()).place(this, this.chunkSource.getGenerator(), this.random, this.DIRTY_ICE_BALL_POS);
               this.DIRTY_ICE_BALL_POS = new BlockPos(this.random.nextInt(var8, var11), this.random.nextInt(var9, var12), this.random.nextInt(var10, var13));
               ((ConfiguredFeature)this.registryAccess().getOrThrow(MiscOverworldFeatures.DIRTY_ICE_BALL_FOX).value()).place(this, this.chunkSource.getGenerator(), this.random, this.DIRTY_ICE_BALL_POS);
            }

            this.DIRTY_ICE_BALL_POS = var5.immutable();
            ((ConfiguredFeature)this.registryAccess().getOrThrow(MiscOverworldFeatures.SPACE_IGLOO).value()).place(this, this.chunkSource.getGenerator(), this.random, this.DIRTY_ICE_BALL_POS);
            JigsawPlacement.generateJigsaw(this, this.registryAccess().getOrThrow(MineExitPools.STARTS), Optional.of(ResourceLocation.withDefaultNamespace("start")), 7, new BlockPos(var5.getX() + 200, var5.getY() + 180, var5.getZ()), false);
            var5.move(4, 10, 0);
            var7 = false;
            var6 = false;
         }

         if (this.isActive(WorldEffects.RAID)) {
            Holder.Reference var17 = this.registryAccess().getOrThrow(BuiltinStructures.VILLAGE_PLAINS);
            ChunkGenerator var19 = this.getChunkSource().getGenerator();
            StructureStart var21 = ((Structure)var17.value()).generate(var17, this.dimension(), this.registryAccess(), var19, var19.getBiomeSource(), this.chunkSource.randomState(), this.getStructureManager(), this.getSeed(), new ChunkPos(var5.immutable()), 0, this, (var0) -> true);
            BoundingBox var23 = var21.getBoundingBox();
            ChunkPos var24 = new ChunkPos(SectionPos.blockToSectionCoord(var23.minX()), SectionPos.blockToSectionCoord(var23.minZ()));
            ChunkPos var26 = new ChunkPos(SectionPos.blockToSectionCoord(var23.maxX()), SectionPos.blockToSectionCoord(var23.maxZ()));
            ChunkPos.rangeClosed(var24, var26).forEach((var3x) -> {
               this.setChunkForced(var3x.x, var3x.z, true);
               var21.placeInChunk(this, this.structureManager(), var19, this.getRandom(), new BoundingBox(var3x.getMinBlockX(), this.getMinY(), var3x.getMinBlockZ(), var3x.getMaxBlockX(), this.getMaxY() + 1, var3x.getMaxBlockZ()), var3x);
            });
         }
      }

      while(var7 && this.getHeight(Heightmap.Types.WORLD_SURFACE, var5) <= this.getMinY()) {
         var5.move(Direction.NORTH);
         this.getBlockState(var5);
      }

      if (var7) {
         var5.setY(this.getHeight(Heightmap.Types.WORLD_SURFACE, var5));
      }

      BlockState var18;
      for(var18 = this.getBlockState(var5); var18.isCollisionShapeFullBlock(this, var5); var18 = this.getBlockState(var5)) {
         var5.move(Direction.UP);
      }

      if (var6) {
         ((ConfiguredFeature)this.registryAccess().getOrThrow(MiscOverworldFeatures.MINE_START).value()).place(this, this.chunkSource.getGenerator(), this.random, var5.below());
      }

      VoxelShape var20 = var18.getCollisionShape(this, var5);
      double var22 = var20.isEmpty() ? 0.0 : var20.max(Direction.Axis.Y);
      if (!Double.isFinite(var22)) {
         var22 = 0.0;
      }

      var4 = new Vec3((double)var5.getX() + 0.5, (double)var5.getY() + var22, (double)var5.getZ() + 0.5);

      for(ServerPlayer var27 : this.theGame.playerList().getPlayers()) {
         if ((!var2.isPresent() || ((UUID)var2.get()).equals(var27.getUUID())) && !var27.isSpectator()) {
            var27.setGameMode(GameType.SURVIVAL);
            TeleportTransition var28 = new TeleportTransition(this, var4, Vec3.ZERO, 0.0F, 0.0F, Relative.DELTA, TeleportTransition.PLACE_PORTAL_TICKET);
            ServerPlayer var15 = var27.teleport(var28);
            if (var15 == null) {
               return;
            }

            var15.connection.resetPosition();
            if (var1) {
               var15.setGameMode(GameType.ADVENTURE);
               if (this.isMineWon()) {
                  var15.sendSystemMessage(Component.translatable("world.mine.revisit.won"), true);
               } else {
                  var15.sendSystemMessage(Component.translatable("world.mine.revisit.lost"), true);
               }

               var15.setRevisiting(true);
               var15.addOrDropItem(Items.EXIT_EYE.getDefaultInstance().copyWithCount(1));
            } else {
               var15.onMineEntered();
            }
         }
      }

   }

   private void onMineEntered() {
      this.theGame.overworld().setDayTime(1000L);

      for(WorldEffect var2 : this.activeEffects) {
         var2.onMineEnter().accept(this);
      }

   }

   private void onMineLeave() {
      for(WorldEffect var2 : this.activeEffects) {
         var2.onMineLeave().accept(this);
      }

      this.theGame.overworld().setDayTime(1000L);
   }

   public boolean isMine() {
      return this.mineData.isMine();
   }

   public boolean isSpecialMine() {
      return this.specialMine.isPresent();
   }

   public Optional<SpecialMine> specialMine() {
      return this.specialMine;
   }

   public boolean isMineCompleted() {
      return this.mineData.getMineState() != MineData.MineState.ONGOING;
   }

   public boolean isMineWon() {
      return this.mineData.getMineState() == MineData.MineState.WON;
   }

   public void markMineCompleted(boolean var1) {
      this.serverLevelData.mineCompleted(this.specialMine, var1);
      this.mineData.setMineState(var1 ? MineData.MineState.WON : MineData.MineState.FAILED);

      for(ServerPlayer var3 : this.theGame.playerList().getPlayers()) {
         UnlockCondition.onMapCompleted(this, var3, this.activeEffects, var1);
         this.specialMine.ifPresent((var3x) -> UnlockCondition.onSpecialMineCompleted(this, var3, var3x, var1));
      }

      this.events.forEach((var2) -> var2.end(this, var1));
      if (var1) {
         for(WorldEffect var5 : this.activeEffects) {
            if (var5.unlockMode() == UnlockMode.UNLOCKED_ON_WIN) {
               this.unlockEffect(var5);
            }
         }

         if (this.isSpecialMine()) {
            this.theGame.overworld().mineData.addKeysToRoll(1);
         } else if (this.random.nextInt(10) == 0) {
            this.theGame.overworld().mineData.addKeysToRoll(1);
         }
      }

      this.onMineLeave();
   }

   public void toggledMineTravellingBlock(BlockPos var1) {
      boolean var2 = this.mineData.toggledMineTravellingBlock(var1.immutable());
      String var3 = this.isMine() ? "leave" : "enter";
      if (var2) {
         this.theGame.playerList().broadcastSystemMessage(Component.translatable("mine." + var3 + ".started").withStyle(Style.EMPTY.withScale(this.isMine() ? 1.0F : 0.75F)), true);
      } else {
         this.theGame.playerList().broadcastSystemMessage(Component.translatable("mine." + var3 + ".aborted").withStyle(Style.EMPTY.withScale(this.isMine() ? 1.0F : 0.75F)), true);
      }

   }

   public void unlockEffect(WorldEffect var1) {
      if (!this.serverLevelData.isEffectUnlocked(var1) && var1.unlockMode() != UnlockMode.NEVER_UNLOCKED) {
         this.theGame.playerList().broadcastSystemMessage(Component.translatable("world.effect.unlocked", var1.name()), true);
         this.theGame.playerList().broadcastSystemMessage(Component.translatable("world.effect.unlocked", var1.name()), false);
         this.serverLevelData.unlockEffect(var1);

         for(ServerPlayer var3 : this.theGame.playerList().getPlayers()) {
            UnlockCondition.onUnlockedMapEffect(this, var3, var1);
            var3.connection.send(new ClientboundUpdateUnlockedEffectsPacket(this.getUnlockedEffects()));
         }

      }
   }

   public boolean isEffectUnlocked(WorldEffect var1) {
      return this.serverLevelData.isEffectUnlocked(var1);
   }

   public boolean isActive(WorldEffect var1) {
      return this.activeEffects.contains(var1);
   }

   public List<WorldEffect> getActiveEffects() {
      return new ArrayList(this.activeEffects);
   }

   public List<WorldEffect> getUnlockedEffects() {
      return BuiltInRegistries.WORLD_EFFECT.stream().filter(this::isEffectUnlocked).toList();
   }

   public void dropUnlockEffect(Vec3 var1, WorldEffect var2, @Nullable ServerPlayer var3) {
      if (this.isMineCompleted()) {
         this.unlockEffect(var2);
      } else if (var3 == null || !var3.getInventory().getNonEquipmentItems().stream().anyMatch((var1x) -> var1x.is(Items.MINE_INGREDIENT) && ((WorldModifiers)var1x.getOrDefault(DataComponents.WORLD_MODIFIERS, WorldModifiers.EMPTY)).effects().contains(var2))) {
         this.addFreshEntity(new ItemEntity(this, var1.x, var1.y, var1.z, WorldEffects.createEffectItem(var2, true)));
      }
   }

   public int getMineCrafterLevel() {
      return this.serverLevelData.getMineCrafterLevel();
   }

   public int getMineCrafterExp() {
      return this.serverLevelData.getMineCrafterExp();
   }

   public void addExperienceToMineCrafter(int var1) {
      int var2 = this.serverLevelData.getMineCrafterLevel();
      this.serverLevelData.addExperienceToMineCrafter(var1);
      if (var2 != this.serverLevelData.getMineCrafterLevel()) {
         for(ServerPlayer var4 : this.players) {
            CriteriaTriggers.MINE_CRAFTER_UPGRADED.trigger(var4);
         }
      }

      this.syncContainerMenuData(MenuType.MAP_MAKING, MineCrafterBlockEntity.getAdditionalData(this));
   }

   public void syncContainerMenuData(MenuType<? extends AbstractContainerMenu> var1, List<Integer> var2) {
      this.theGame.playerList().syncContainerMenuData(var1, var2);
   }

   public void unlockSpecialMine(SpecialMine var1) {
      this.serverLevelData.unlockSpecialMine(var1);
   }

   public boolean isSpecialMineUnlocked(SpecialMine var1) {
      return this.serverLevelData.isSpecialMineUnlocked(var1);
   }

   public Optional<SpecialMine> getNextSpecialMine() {
      return this.serverLevelData.getNextSpecialMine(this.random);
   }

   public boolean shouldTickBlocksAt(long var1) {
      return this.chunkSource.chunkMap.getDistanceManager().inBlockTickingRange(var1);
   }

   protected void tickTime() {
      if (this.tickTime) {
         long var1 = this.levelData.getGameTime() + 1L;
         this.serverLevelData.setGameTime(var1);
         Profiler.get().push("scheduledFunctions");
         this.serverLevelData.getScheduledEvents().tick(this.theGame, var1);
         Profiler.get().pop();
         if (this.serverLevelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
         }

      }
   }

   public void setDayTime(long var1) {
      this.serverLevelData.setDayTime(var1);
   }

   public void tickCustomSpawners(boolean var1, boolean var2) {
      for(CustomSpawner var4 : this.customSpawners) {
         var4.tick(this, var1, var2);
      }

   }

   private void wakeUpAllPlayers() {
      this.sleepStatus.removeAllSleepers();
      ((List)this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList())).forEach((var0) -> var0.stopSleepInBed(false, false));
   }

   public void tickChunk(LevelChunk var1, int var2) {
      ChunkPos var3 = var1.getPos();
      int var4 = var3.getMinBlockX();
      int var5 = var3.getMinBlockZ();
      ProfilerFiller var6 = Profiler.get();
      var6.push("iceandsnow");

      for(int var7 = 0; var7 < var2; ++var7) {
         if (this.random.nextInt(48) == 0) {
            this.tickPrecipitation(this.getBlockRandomPos(var4, 0, var5, 15));
         }
      }

      var6.popPush("tickBlocks");
      if (var2 > 0) {
         LevelChunkSection[] var16 = var1.getSections();

         for(int var8 = 0; var8 < var16.length; ++var8) {
            LevelChunkSection var9 = var16[var8];
            if (var9.isRandomlyTicking()) {
               int var10 = var1.getSectionYFromSectionIndex(var8);
               int var11 = SectionPos.sectionToBlockCoord(var10);

               for(int var12 = 0; var12 < var2; ++var12) {
                  BlockPos var13 = this.getBlockRandomPos(var4, var11, var5, 15);
                  var6.push("randomTick");
                  BlockState var14 = var9.getBlockState(var13.getX() - var4, var13.getY() - var11, var13.getZ() - var5);
                  if (var14.isRandomlyTicking()) {
                     var14.randomTick(this, var13, this.random);
                  }

                  FluidState var15 = var14.getFluidState();
                  if (var15.isRandomlyTicking()) {
                     var15.randomTick(this, var13, this.random);
                  }

                  var6.pop();
               }
            }
         }
      }

      var6.pop();
   }

   public void tickThunder(LevelChunk var1) {
      ChunkPos var2 = var1.getPos();
      boolean var3 = this.isRaining();
      int var4 = var2.getMinBlockX();
      int var5 = var2.getMinBlockZ();
      ProfilerFiller var6 = Profiler.get();
      var6.push("thunder");
      int var7 = 100000;
      boolean var8 = this.isActive(WorldEffects.ETERNAL_LIGHTNING);
      if (var8) {
         var7 = 100;
      }

      if ((var3 || var8) && this.isThundering() && this.random.nextInt(var7) == 0) {
         BlockPos var9 = this.findLightningTargetAround(this.getBlockRandomPos(var4, 0, var5, 15));
         if (this.isRainingAt(var9) || var8) {
            DifficultyInstance var10 = this.getCurrentDifficultyAt(var9);
            boolean var11 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double)var10.getEffectiveDifficulty() * 0.01 && !this.getBlockState(var9.below()).is(Blocks.LIGHTNING_ROD);
            if (var11) {
               SkeletonHorse var12 = EntityType.SKELETON_HORSE.create(this, EntitySpawnReason.EVENT);
               if (var12 != null) {
                  var12.setTrap(true);
                  var12.setAge(0);
                  var12.setPos((double)var9.getX(), (double)var9.getY(), (double)var9.getZ());
                  this.addFreshEntity(var12);
               }
            }

            LightningBolt var13 = EntityType.LIGHTNING_BOLT.create(this, EntitySpawnReason.EVENT);
            if (var13 != null) {
               var13.snapTo(Vec3.atBottomCenterOf(var9));
               var13.setVisualOnly(var11);
               this.addFreshEntity(var13);
            }
         }
      }

      var6.pop();
   }

   @VisibleForTesting
   public void tickPrecipitation(BlockPos var1) {
      BlockPos var2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var1);
      BlockPos var3 = var2.below();
      Biome var4 = (Biome)this.getBiome(var2).value();
      if (var4.shouldFreeze(this, var3)) {
         this.setBlockAndUpdate(var3, Blocks.ICE.defaultBlockState());
      }

      if (this.isRaining()) {
         int var5 = this.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
         if (var5 > 0 && var4.shouldSnow(this, var2)) {
            BlockState var6 = this.getBlockState(var2);
            if (var6.is(Blocks.SNOW)) {
               int var7 = (Integer)var6.getValue(SnowLayerBlock.LAYERS);
               if (var7 < Math.min(var5, 8)) {
                  BlockState var8 = (BlockState)var6.setValue(SnowLayerBlock.LAYERS, var7 + 1);
                  Block.pushEntitiesUp(var6, var8, this, var2);
                  this.setBlockAndUpdate(var2, var8);
               }
            } else {
               this.setBlockAndUpdate(var2, Blocks.SNOW.defaultBlockState());
            }
         }

         Biome.Precipitation var9 = var4.getPrecipitationAt(var3, this.getSeaLevel());
         if (var9 != Biome.Precipitation.NONE) {
            BlockState var10 = this.getBlockState(var3);
            var10.getBlock().handlePrecipitation(var10, this, var3, var9);
         }
      }

   }

   private Optional<BlockPos> findLightningRod(BlockPos var1) {
      Optional var2 = this.getPoiManager().findClosest((var0) -> var0.is(PoiTypes.LIGHTNING_ROD), (var1x) -> var1x.getY() == this.getHeight(Heightmap.Types.WORLD_SURFACE, var1x.getX(), var1x.getZ()) - 1, var1, 128, PoiManager.Occupancy.ANY);
      return var2.map((var0) -> var0.above(1));
   }

   protected BlockPos findLightningTargetAround(BlockPos var1) {
      BlockPos var2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var1);
      Optional var3 = this.findLightningRod(var2);
      if (var3.isPresent()) {
         return (BlockPos)var3.get();
      } else {
         AABB var4 = AABB.encapsulatingFullBlocks(var2, var2.atY(this.getMaxY() + 1)).inflate(3.0);
         List var5 = this.getEntitiesOfClass(LivingEntity.class, var4, (var1x) -> var1x != null && var1x.isAlive() && this.canSeeSky(var1x.blockPosition()));
         if (!var5.isEmpty()) {
            return ((LivingEntity)var5.get(this.random.nextInt(var5.size()))).blockPosition();
         } else {
            if (var2.getY() == this.getMinY() - 1) {
               var2 = var2.above(2);
            }

            return var2;
         }
      }
   }

   public boolean isHandlingTick() {
      return this.handlingTick;
   }

   public boolean canSleepThroughNights() {
      return this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE) <= 100;
   }

   private void announceSleepStatus() {
      if (this.canSleepThroughNights()) {
         if (!this.theGame.server().isSingleplayer() || this.theGame.server().isPublished()) {
            int var1 = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
            MutableComponent var2;
            if (this.sleepStatus.areEnoughSleeping(var1)) {
               var2 = Component.translatable("sleep.skipping_night");
            } else {
               var2 = Component.translatable("sleep.players_sleeping", this.sleepStatus.amountSleeping(), this.sleepStatus.sleepersNeeded(var1));
            }

            for(ServerPlayer var4 : this.players) {
               var4.displayClientMessage(var2, true);
            }

         }
      }
   }

   public void updateSleepingPlayerList() {
      if (!this.players.isEmpty() && this.sleepStatus.update(this.players)) {
         this.announceSleepStatus();
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.theGame.getScoreboard();
   }

   private void advanceWeatherCycle() {
      boolean var1 = this.isRaining();
      if (this.dimensionType().hasSkyLight()) {
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            int var2 = this.serverLevelData.getClearWeatherTime();
            int var3 = this.serverLevelData.getThunderTime();
            int var4 = this.serverLevelData.getRainTime();
            boolean var5 = this.levelData.isThundering();
            boolean var6 = this.levelData.isRaining();
            if (var2 > 0) {
               --var2;
               var3 = var5 ? 0 : 1;
               var4 = var6 ? 0 : 1;
               var5 = false;
               var6 = false;
            } else {
               if (var3 > 0) {
                  --var3;
                  if (var3 == 0) {
                     var5 = !var5;
                  }
               } else if (var5) {
                  var3 = THUNDER_DURATION.sample(this.random);
               } else {
                  var3 = THUNDER_DELAY.sample(this.random);
               }

               if (var4 > 0) {
                  --var4;
                  if (var4 == 0) {
                     var6 = !var6;
                  }
               } else if (var6) {
                  var4 = RAIN_DURATION.sample(this.random);
               } else {
                  var4 = RAIN_DELAY.sample(this.random);
               }
            }

            this.serverLevelData.setThunderTime(var3);
            this.serverLevelData.setRainTime(var4);
            this.serverLevelData.setClearWeatherTime(var2);
            this.serverLevelData.setThundering(var5);
            this.serverLevelData.setRaining(var6);
         }

         this.oThunderLevel = this.thunderLevel;
         if (this.levelData.isThundering()) {
            this.thunderLevel += 0.01F;
         } else {
            this.thunderLevel -= 0.01F;
         }

         this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0F, 1.0F);
         this.oRainLevel = this.rainLevel;
         if (this.levelData.isRaining()) {
            this.rainLevel += 0.01F;
         } else {
            this.rainLevel -= 0.01F;
         }

         this.rainLevel = Mth.clamp(this.rainLevel, 0.0F, 1.0F);
      }

      if (this.oRainLevel != this.rainLevel) {
         this.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
      }

      if (this.oThunderLevel != this.thunderLevel) {
         this.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
      }

      if (var1 != this.isRaining()) {
         if (var1) {
            this.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F));
         } else {
            this.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
         }

         this.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel));
         this.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel));
      }

   }

   private PlayerList getPlayerList() {
      return this.theGame.playerList();
   }

   @VisibleForTesting
   public void resetWeatherCycle() {
      this.serverLevelData.setRainTime(0);
      this.serverLevelData.setRaining(false);
      this.serverLevelData.setThunderTime(0);
      this.serverLevelData.setThundering(false);
   }

   public void resetEmptyTime() {
      this.emptyTime = 0;
   }

   private void tickFluid(BlockPos var1, Fluid var2) {
      BlockState var3 = this.getBlockState(var1);
      FluidState var4 = var3.getFluidState();
      if (var4.is(var2)) {
         var4.tick(this, var1, var3);
      }

   }

   private void tickBlock(BlockPos var1, Block var2) {
      BlockState var3 = this.getBlockState(var1);
      if (var3.is(var2)) {
         var3.tick(this, var1, this.random);
      }

   }

   public void tickNonPassenger(Entity var1) {
      var1.setOldPosAndRot();
      ProfilerFiller var2 = Profiler.get();
      ++var1.tickCount;
      var2.push((Supplier)(() -> BuiltInRegistries.ENTITY_TYPE.getKey(var1.getType()).toString()));
      var2.incrementCounter("tickNonPassenger");
      var1.tick();
      var2.pop();

      for(Entity var4 : var1.getPassengers()) {
         this.tickPassenger(var1, var4);
      }

   }

   private void tickPassenger(Entity var1, Entity var2) {
      if (!var2.isRemoved() && var2.getVehicle() == var1) {
         if (var2 instanceof Player || this.entityTickList.contains(var2)) {
            var2.setOldPosAndRot();
            ++var2.tickCount;
            ProfilerFiller var3 = Profiler.get();
            var3.push((Supplier)(() -> BuiltInRegistries.ENTITY_TYPE.getKey(var2.getType()).toString()));
            var3.incrementCounter("tickPassenger");
            var2.rideTick();
            var3.pop();

            for(Entity var5 : var2.getPassengers()) {
               this.tickPassenger(var2, var5);
            }

         }
      } else {
         var2.stopRiding();
      }
   }

   public boolean mayInteract(Entity var1, BlockPos var2) {
      boolean var10000;
      if (var1 instanceof Player var3) {
         if (this.theGame.server().isUnderSpawnProtection(this, var2, var3) || !this.getWorldBorder().isWithinBounds(var2)) {
            var10000 = false;
            return var10000;
         }
      }

      var10000 = true;
      return var10000;
   }

   public void save(@Nullable ProgressListener var1, boolean var2, boolean var3) {
      ServerChunkCache var4 = this.getChunkSource();
      if (!var3) {
         if (var1 != null) {
            var1.progressStartNoAbort(Component.translatable("menu.savingLevel"));
         }

         this.saveLevelData(var2);
         if (var1 != null) {
            var1.progressStage(Component.translatable("menu.savingChunks"));
         }

         var4.save(var2);
         if (var2) {
            this.entityManager.saveAll();
         } else {
            this.entityManager.autoSave();
         }

      }
   }

   private void saveLevelData(boolean var1) {
      if (this.dragonFight != null) {
         this.theGame.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
      }

      DimensionDataStorage var2 = this.getChunkSource().getDataStorage();
      this.theGame.getWorldData().events().put(this.dimension(), new MineEventData(this.eventsStarted, this.events));
      if (var1) {
         var2.saveAndJoin();
      } else {
         var2.scheduleSave();
      }

   }

   public <T extends Entity> List<? extends T> getEntities(EntityTypeTest<Entity, T> var1, Predicate<? super T> var2) {
      ArrayList var3 = Lists.newArrayList();
      this.getEntities(var1, var2, var3);
      return var3;
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> var1, Predicate<? super T> var2, List<? super T> var3) {
      this.getEntities(var1, var2, var3, 2147483647);
   }

   public <T extends Entity> void getEntities(EntityTypeTest<Entity, T> var1, Predicate<? super T> var2, List<? super T> var3, int var4) {
      this.getEntities().get(var1, (AbortableIterationConsumer)((var3x) -> {
         if (var2.test(var3x)) {
            var3.add(var3x);
            if (var3.size() >= var4) {
               return AbortableIterationConsumer.Continuation.ABORT;
            }
         }

         return AbortableIterationConsumer.Continuation.CONTINUE;
      }));
   }

   public List<? extends EnderDragon> getDragons() {
      return this.<EnderDragon>getEntities(EntityType.ENDER_DRAGON, LivingEntity::isAlive);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> var1) {
      return this.getPlayers(var1, 2147483647);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> var1, int var2) {
      ArrayList var3 = Lists.newArrayList();

      for(ServerPlayer var5 : this.players) {
         if (var1.test(var5)) {
            var3.add(var5);
            if (var3.size() >= var2) {
               return var3;
            }
         }
      }

      return var3;
   }

   @Nullable
   public ServerPlayer getRandomPlayer() {
      List var1 = this.getPlayers(LivingEntity::isAlive);
      return var1.isEmpty() ? null : (ServerPlayer)var1.get(this.random.nextInt(var1.size()));
   }

   public boolean addFreshEntity(Entity var1) {
      return this.addEntity(var1);
   }

   public boolean addWithUUID(Entity var1) {
      return this.addEntity(var1);
   }

   public void addDuringTeleport(Entity var1) {
      if (var1 instanceof ServerPlayer var2) {
         this.addPlayer(var2);
      } else {
         this.addEntity(var1);
      }

   }

   public void addNewPlayer(ServerPlayer var1) {
      this.addPlayer(var1);
   }

   public void addRespawnedPlayer(ServerPlayer var1) {
      this.addPlayer(var1);
   }

   private void addPlayer(ServerPlayer var1) {
      Entity var2 = this.getEntity(var1.getUUID());
      if (var2 != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", var1.getUUID());
         var2.unRide();
         this.removePlayerImmediately((ServerPlayer)var2, Entity.RemovalReason.DISCARDED);
      }

      this.entityManager.addNewEntity(var1);
   }

   private boolean addEntity(Entity var1) {
      if (var1.isRemoved()) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getKey(var1.getType()));
         return false;
      } else {
         return this.entityManager.addNewEntity(var1);
      }
   }

   public boolean tryAddFreshEntityWithPassengers(Entity var1) {
      Stream var10000 = var1.getSelfAndPassengers().map(Entity::getUUID);
      PersistentEntitySectionManager var10001 = this.entityManager;
      Objects.requireNonNull(var10001);
      if (var10000.anyMatch(var10001::isLoaded)) {
         return false;
      } else {
         this.addFreshEntityWithPassengers(var1);
         return true;
      }
   }

   public void unload(LevelChunk var1) {
      var1.clearAllBlockEntities();
      var1.unregisterTickContainerFromLevel(this);
   }

   public void removePlayerImmediately(ServerPlayer var1, Entity.RemovalReason var2) {
      var1.remove(var2);
   }

   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      for(ServerPlayer var5 : this.getPlayerList().getPlayers()) {
         if (var5 != null && var5.level() == this && var5.getId() != var1) {
            double var6 = (double)var2.getX() - var5.getX();
            double var8 = (double)var2.getY() - var5.getY();
            double var10 = (double)var2.getZ() - var5.getZ();
            if (var6 * var6 + var8 * var8 + var10 * var10 < 1024.0) {
               var5.connection.send(new ClientboundBlockDestructionPacket(var1, var2, var3));
            }
         }
      }

   }

   public void playSeededSound(@Nullable Entity var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12) {
      PlayerList var10000 = this.getPlayerList();
      Player var10001;
      if (var1 instanceof Player var14) {
         var10001 = var14;
      } else {
         var10001 = null;
      }

      var10000.broadcast(var10001, var2, var4, var6, (double)((SoundEvent)var8.value()).getRange(var10), this.dimension(), new ClientboundSoundPacket(var8, var9, var2, var4, var6, var10, var11, var12));
   }

   public void playSeededSound(@Nullable Entity var1, Entity var2, Holder<SoundEvent> var3, SoundSource var4, float var5, float var6, long var7) {
      PlayerList var10000 = this.getPlayerList();
      Player var10001;
      if (var1 instanceof Player var9) {
         var10001 = var9;
      } else {
         var10001 = null;
      }

      var10000.broadcast(var10001, var2.getX(), var2.getY(), var2.getZ(), (double)((SoundEvent)var3.value()).getRange(var5), this.dimension(), new ClientboundSoundEntityPacket(var3, var4, var2, var5, var6, var7));
   }

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      if (this.getGameRules().getBoolean(GameRules.RULE_GLOBAL_SOUND_EVENTS)) {
         this.getPlayerList().getPlayers().forEach((var4) -> {
            Vec3 var5;
            if (var4.level() == this) {
               Vec3 var6 = Vec3.atCenterOf(var2);
               if (var4.distanceToSqr(var6) < (double)Mth.square(32)) {
                  var5 = var6;
               } else {
                  Vec3 var7 = var6.subtract(var4.position()).normalize();
                  var5 = var4.position().add(var7.scale(32.0));
               }
            } else {
               var5 = var4.position();
            }

            var4.connection.send(new ClientboundLevelEventPacket(var1, BlockPos.containing(var5), var3, true));
         });
      } else {
         this.levelEvent((Entity)null, var1, var2, var3);
      }

   }

   public void levelEvent(@Nullable Entity var1, int var2, BlockPos var3, int var4) {
      PlayerList var10000 = this.getPlayerList();
      Player var10001;
      if (var1 instanceof Player var5) {
         var10001 = var5;
      } else {
         var10001 = null;
      }

      var10000.broadcast(var10001, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 64.0, this.dimension(), new ClientboundLevelEventPacket(var2, var3, var4, false));
   }

   public int getLogicalHeight() {
      return this.dimensionType().logicalHeight();
   }

   public void gameEvent(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3) {
      this.gameEventDispatcher.post(var1, var2, var3);
   }

   public void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4) {
      if (this.isUpdatingNavigations) {
         String var5 = "recursive call to sendBlockUpdated";
         Util.logAndPauseIfInIde("recursive call to sendBlockUpdated", new IllegalStateException("recursive call to sendBlockUpdated"));
      }

      this.getChunkSource().blockChanged(var1);
      this.pathTypesByPosCache.invalidate(var1);
      VoxelShape var14 = var2.getCollisionShape(this, var1);
      VoxelShape var6 = var3.getCollisionShape(this, var1);
      if (Shapes.joinIsNotEmpty(var14, var6, BooleanOp.NOT_SAME)) {
         ObjectArrayList var7 = new ObjectArrayList();

         for(Mob var9 : this.navigatingMobs) {
            PathNavigation var10 = var9.getNavigation();
            if (var10.shouldRecomputePath(var1)) {
               var7.add(var10);
            }
         }

         try {
            this.isUpdatingNavigations = true;

            for(PathNavigation var16 : var7) {
               var16.recomputePath();
            }
         } finally {
            this.isUpdatingNavigations = false;
         }

      }
   }

   public void updateNeighborsAt(BlockPos var1, Block var2) {
      this.updateNeighborsAt(var1, var2, ExperimentalRedstoneUtils.initialOrientation(this, (Direction)null, (Direction)null));
   }

   public void updateNeighborsAt(BlockPos var1, Block var2, @Nullable Orientation var3) {
      this.neighborUpdater.updateNeighborsAtExceptFromFacing(var1, var2, (Direction)null, var3);
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos var1, Block var2, Direction var3, @Nullable Orientation var4) {
      this.neighborUpdater.updateNeighborsAtExceptFromFacing(var1, var2, var3, var4);
   }

   public void neighborChanged(BlockPos var1, Block var2, @Nullable Orientation var3) {
      this.neighborUpdater.neighborChanged(var1, var2, var3);
   }

   public void neighborChanged(BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5) {
      this.neighborUpdater.neighborChanged(var1, var2, var3, var4, var5);
   }

   public void broadcastEntityEvent(Entity var1, byte var2) {
      this.getChunkSource().broadcastAndSend(var1, new ClientboundEntityEventPacket(var1, var2));
   }

   public void broadcastDamageEvent(Entity var1, DamageSource var2) {
      this.getChunkSource().broadcastAndSend(var1, new ClientboundDamageEventPacket(var1, var2));
   }

   public ServerChunkCache getChunkSource() {
      return this.chunkSource;
   }

   public void explode(@Nullable Entity var1, @Nullable DamageSource var2, @Nullable ExplosionDamageCalculator var3, double var4, double var6, double var8, float var10, boolean var11, Level.ExplosionInteraction var12, ParticleOptions var13, ParticleOptions var14, Holder<SoundEvent> var15) {
      Explosion.BlockInteraction var10000;
      switch (var12) {
         case NONE -> var10000 = Explosion.BlockInteraction.KEEP;
         case BLOCK -> var10000 = this.getDestroyType(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
         case MOB -> var10000 = this.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING) ? this.getDestroyType(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY) : Explosion.BlockInteraction.KEEP;
         case TNT -> var10000 = this.getDestroyType(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
         case TRIGGER -> var10000 = Explosion.BlockInteraction.TRIGGER_BLOCK;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      Explosion.BlockInteraction var16 = var10000;
      Vec3 var17 = new Vec3(var4, var6, var8);
      ServerExplosion var18 = new ServerExplosion(this, var1, var2, var3, var17, var10, var11, var16);
      var18.explode();
      ParticleOptions var19 = var18.isSmall() ? var13 : var14;

      for(ServerPlayer var21 : this.players) {
         if (var21.distanceToSqr(var17) < 4096.0) {
            Optional var22 = Optional.ofNullable((Vec3)var18.getHitPlayers().get(var21));
            var21.connection.send(new ClientboundExplodePacket(var17, var22, var19, var15));
         }
      }

   }

   private Explosion.BlockInteraction getDestroyType(GameRules.Key<GameRules.BooleanValue> var1) {
      return this.getGameRules().getBoolean(var1) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
   }

   public void blockEvent(BlockPos var1, Block var2, int var3, int var4) {
      this.blockEvents.add(new BlockEventData(var1, var2, var3, var4));
   }

   private void runBlockEvents() {
      this.blockEventsToReschedule.clear();

      while(!this.blockEvents.isEmpty()) {
         BlockEventData var1 = (BlockEventData)this.blockEvents.removeFirst();
         if (this.shouldTickBlocksAt(var1.pos())) {
            if (this.doBlockEvent(var1)) {
               this.getPlayerList().broadcast((Player)null, (double)var1.pos().getX(), (double)var1.pos().getY(), (double)var1.pos().getZ(), 64.0, this.dimension(), new ClientboundBlockEventPacket(var1.pos(), var1.block(), var1.paramA(), var1.paramB()));
            }
         } else {
            this.blockEventsToReschedule.add(var1);
         }
      }

      this.blockEvents.addAll(this.blockEventsToReschedule);
   }

   private boolean doBlockEvent(BlockEventData var1) {
      BlockState var2 = this.getBlockState(var1.pos());
      return var2.is(var1.block()) ? var2.triggerEvent(this, var1.pos(), var1.paramA(), var1.paramB()) : false;
   }

   public LevelTicks<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public LevelTicks<Fluid> getFluidTicks() {
      return this.fluidTicks;
   }

   public ChunkIOErrorReporter chunkIOErrorReporter() {
      return this.theGame().chunkIOErrorReporter();
   }

   public BlockableEventLoop<?> eventLoop() {
      return this.theGame().eventLoop();
   }

   @Nonnull
   public TheGame theGame() {
      return this.theGame;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public StructureTemplateManager getStructureManager() {
      return this.theGame.getStructureManager();
   }

   public <T extends ParticleOptions> int sendParticles(T var1, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15) {
      return this.sendParticles(var1, false, false, var2, var4, var6, var8, var9, var11, var13, var15);
   }

   public <T extends ParticleOptions> int sendParticles(T var1, boolean var2, boolean var3, double var4, double var6, double var8, int var10, double var11, double var13, double var15, double var17) {
      ClientboundLevelParticlesPacket var19 = new ClientboundLevelParticlesPacket(var1, var2, var3, var4, var6, var8, (float)var11, (float)var13, (float)var15, (float)var17, var10);
      int var20 = 0;

      for(int var21 = 0; var21 < this.players.size(); ++var21) {
         ServerPlayer var22 = (ServerPlayer)this.players.get(var21);
         if (this.sendParticles(var22, var2, var4, var6, var8, var19)) {
            ++var20;
         }
      }

      return var20;
   }

   public <T extends ParticleOptions> boolean sendParticles(ServerPlayer var1, T var2, boolean var3, boolean var4, double var5, double var7, double var9, int var11, double var12, double var14, double var16, double var18) {
      ClientboundLevelParticlesPacket var20 = new ClientboundLevelParticlesPacket(var2, var3, var4, var5, var7, var9, (float)var12, (float)var14, (float)var16, (float)var18, var11);
      return this.sendParticles(var1, var3, var5, var7, var9, var20);
   }

   private boolean sendParticles(ServerPlayer var1, boolean var2, double var3, double var5, double var7, Packet<?> var9) {
      if (var1.level() != this) {
         return false;
      } else {
         BlockPos var10 = var1.blockPosition();
         if (var10.closerToCenterThan(new Vec3(var3, var5, var7), var2 ? 512.0 : 32.0)) {
            var1.connection.send(var9);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getEntity(int var1) {
      return (Entity)this.getEntities().get(var1);
   }

   /** @deprecated */
   @Deprecated
   @Nullable
   public Entity getEntityOrPart(int var1) {
      Entity var2 = (Entity)this.getEntities().get(var1);
      return var2 != null ? var2 : (Entity)this.dragonParts.get(var1);
   }

   public Collection<EnderDragonPart> dragonParts() {
      return this.dragonParts.values();
   }

   @Nullable
   public BlockPos findNearestMapStructure(TagKey<Structure> var1, BlockPos var2, int var3, boolean var4) {
      if (!this.theGame.getWorldData().worldGenOptions().generateStructures()) {
         return null;
      } else {
         Optional var5 = this.registryAccess().lookupOrThrow(Registries.STRUCTURE).get(var1);
         if (var5.isEmpty()) {
            return null;
         } else {
            Pair var6 = this.getChunkSource().getGenerator().findNearestMapStructure(this, (HolderSet)var5.get(), var2, var3, var4);
            return var6 != null ? (BlockPos)var6.getFirst() : null;
         }
      }
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(Predicate<Holder<Biome>> var1, BlockPos var2, int var3, int var4, int var5) {
      return this.getChunkSource().getGenerator().getBiomeSource().findClosestBiome3d(var2, var3, var4, var5, var1, this.getChunkSource().randomState().sampler(), this);
   }

   public RecipeManager recipeAccess() {
      return this.theGame.getRecipeManager();
   }

   public ServerTickRateManager tickRateManager() {
      return this.theGame.tickRateManager();
   }

   public boolean noSave() {
      return this.noSave;
   }

   public DimensionDataStorage getDataStorage() {
      return this.getChunkSource().getDataStorage();
   }

   @Nullable
   public MapItemSavedData getMapData(MapId var1) {
      return (MapItemSavedData)this.theGame.overworld().getDataStorage().get(MapItemSavedData.type(var1));
   }

   public void setMapData(MapId var1, MapItemSavedData var2) {
      this.theGame.overworld().getDataStorage().set(MapItemSavedData.type(var1), var2);
   }

   public MapId getFreeMapId() {
      return ((MapIndex)this.theGame.overworld().getDataStorage().computeIfAbsent(MapIndex.TYPE)).getNextMapId();
   }

   public void setDefaultSpawnPos(BlockPos var1, float var2) {
      BlockPos var3 = this.levelData.getSpawnPos();
      float var4 = this.levelData.getSpawnAngle();
      if (!var3.equals(var1) || var4 != var2) {
         this.levelData.setSpawn(var1, var2);
         this.getPlayerList().broadcastAll(new ClientboundSetDefaultSpawnPositionPacket(var1, var2));
      }

      if (this.lastSpawnChunkRadius > 1) {
         this.getChunkSource().removeTicketWithRadius(TicketType.START, new ChunkPos(var3), this.lastSpawnChunkRadius);
      }

      int var5 = this.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS) + 1;
      if (var5 > 1) {
         this.getChunkSource().addTicketWithRadius(TicketType.START, new ChunkPos(var1), var5);
      }

      this.lastSpawnChunkRadius = var5;
   }

   public LongSet getForceLoadedChunks() {
      return this.chunkSource.getForceLoadedChunks();
   }

   public boolean setChunkForced(int var1, int var2, boolean var3) {
      boolean var4 = this.chunkSource.updateChunkForced(new ChunkPos(var1, var2), var3);
      if (var3 && var4) {
         this.getChunk(var1, var2);
      }

      return var4;
   }

   public List<ServerPlayer> players() {
      return this.players;
   }

   public void updatePOIOnBlockStateChange(BlockPos var1, BlockState var2, BlockState var3) {
      Optional var4 = PoiTypes.forState(var2);
      Optional var5 = PoiTypes.forState(var3);
      if (!Objects.equals(var4, var5)) {
         BlockPos var6 = var1.immutable();
         var4.ifPresent((var2x) -> this.eventLoop().execute(() -> {
               this.getPoiManager().remove(var6);
               DebugPackets.sendPoiRemovedPacket(this, var6);
            }));
         var5.ifPresent((var2x) -> this.eventLoop().execute(() -> {
               this.getPoiManager().add(var6, var2x);
               DebugPackets.sendPoiAddedPacket(this, var6);
            }));
      }
   }

   public PoiManager getPoiManager() {
      return this.getChunkSource().getPoiManager();
   }

   public boolean isVillage(BlockPos var1) {
      return this.isCloseToVillage(var1, 1);
   }

   public boolean isVillage(SectionPos var1) {
      return this.isVillage(var1.center());
   }

   public boolean isCloseToVillage(BlockPos var1, int var2) {
      if (var2 > 6) {
         return false;
      } else {
         return this.sectionsToVillage(SectionPos.of(var1)) <= var2;
      }
   }

   public int sectionsToVillage(SectionPos var1) {
      return this.getPoiManager().sectionsToVillage(var1);
   }

   public Raids getRaids() {
      return this.raids;
   }

   @Nullable
   public Raid getRaidAt(BlockPos var1) {
      return this.raids.getNearbyRaid(var1, 9216);
   }

   public boolean isRaided(BlockPos var1) {
      return this.getRaidAt(var1) != null;
   }

   public void onReputationEvent(ReputationEventType var1, Entity var2, ReputationEventHandler var3) {
      var3.onReputationEventFrom(var1, var2);
   }

   public void saveDebugReport(Path var1) throws IOException {
      ChunkMap var2 = this.getChunkSource().chunkMap;
      BufferedWriter var3 = Files.newBufferedWriter(var1.resolve("stats.txt"));

      try {
         ((Writer)var3).write(String.format(Locale.ROOT, "spawning_chunks: %d\n", var2.getDistanceManager().getNaturalSpawnChunkCount()));
         NaturalSpawner.SpawnState var4 = this.getChunkSource().getLastSpawnState();
         if (var4 != null) {
            ObjectIterator var5 = var4.getMobCategoryCounts().object2IntEntrySet().iterator();

            while(var5.hasNext()) {
               Object2IntMap.Entry var6 = (Object2IntMap.Entry)var5.next();
               ((Writer)var3).write(String.format(Locale.ROOT, "spawn_count.%s: %d\n", ((MobCategory)var6.getKey()).getName(), var6.getIntValue()));
            }
         }

         ((Writer)var3).write(String.format(Locale.ROOT, "entities: %s\n", this.entityManager.gatherStats()));
         ((Writer)var3).write(String.format(Locale.ROOT, "block_entity_tickers: %d\n", this.blockEntityTickers.size()));
         ((Writer)var3).write(String.format(Locale.ROOT, "block_ticks: %d\n", this.getBlockTicks().count()));
         ((Writer)var3).write(String.format(Locale.ROOT, "fluid_ticks: %d\n", this.getFluidTicks().count()));
         ((Writer)var3).write("distance_manager: " + var2.getDistanceManager().getDebugStatus() + "\n");
         ((Writer)var3).write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
      } catch (Throwable var22) {
         if (var3 != null) {
            try {
               ((Writer)var3).close();
            } catch (Throwable var16) {
               var22.addSuppressed(var16);
            }
         }

         throw var22;
      }

      if (var3 != null) {
         ((Writer)var3).close();
      }

      CrashReport var23 = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails(var23);
      BufferedWriter var24 = Files.newBufferedWriter(var1.resolve("example_crash.txt"));

      try {
         ((Writer)var24).write(var23.getFriendlyReport(ReportType.TEST));
      } catch (Throwable var21) {
         if (var24 != null) {
            try {
               ((Writer)var24).close();
            } catch (Throwable var15) {
               var21.addSuppressed(var15);
            }
         }

         throw var21;
      }

      if (var24 != null) {
         ((Writer)var24).close();
      }

      Path var25 = var1.resolve("chunks.csv");
      BufferedWriter var26 = Files.newBufferedWriter(var25);

      try {
         var2.dumpChunks(var26);
      } catch (Throwable var20) {
         if (var26 != null) {
            try {
               ((Writer)var26).close();
            } catch (Throwable var14) {
               var20.addSuppressed(var14);
            }
         }

         throw var20;
      }

      if (var26 != null) {
         ((Writer)var26).close();
      }

      Path var27 = var1.resolve("entity_chunks.csv");
      BufferedWriter var28 = Files.newBufferedWriter(var27);

      try {
         this.entityManager.dumpSections(var28);
      } catch (Throwable var19) {
         if (var28 != null) {
            try {
               ((Writer)var28).close();
            } catch (Throwable var13) {
               var19.addSuppressed(var13);
            }
         }

         throw var19;
      }

      if (var28 != null) {
         ((Writer)var28).close();
      }

      Path var29 = var1.resolve("entities.csv");
      BufferedWriter var7 = Files.newBufferedWriter(var29);

      try {
         dumpEntities(var7, this.getEntities().getAll());
      } catch (Throwable var18) {
         if (var7 != null) {
            try {
               ((Writer)var7).close();
            } catch (Throwable var12) {
               var18.addSuppressed(var12);
            }
         }

         throw var18;
      }

      if (var7 != null) {
         ((Writer)var7).close();
      }

      Path var30 = var1.resolve("block_entities.csv");
      BufferedWriter var8 = Files.newBufferedWriter(var30);

      try {
         this.dumpBlockEntityTickers(var8);
      } catch (Throwable var17) {
         if (var8 != null) {
            try {
               ((Writer)var8).close();
            } catch (Throwable var11) {
               var17.addSuppressed(var11);
            }
         }

         throw var17;
      }

      if (var8 != null) {
         ((Writer)var8).close();
      }

   }

   private static void dumpEntities(Writer var0, Iterable<Entity> var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build(var0);

      for(Entity var4 : var1) {
         Component var5 = var4.getCustomName();
         Component var6 = var4.getDisplayName();
         var2.writeRow(var4.getX(), var4.getY(), var4.getZ(), var4.getUUID(), BuiltInRegistries.ENTITY_TYPE.getKey(var4.getType()), var4.isAlive(), var6.getString(), var5 != null ? var5.getString() : null);
      }

   }

   private void dumpBlockEntityTickers(Writer var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(var1);

      for(TickingBlockEntity var4 : this.blockEntityTickers) {
         BlockPos var5 = var4.getPos();
         var2.writeRow(var5.getX(), var5.getY(), var5.getZ(), var4.getType());
      }

   }

   @VisibleForTesting
   public void clearBlockEvents(BoundingBox var1) {
      this.blockEvents.removeIf((var1x) -> var1.isInside(var1x.pos()));
   }

   public float getShade(Direction var1, boolean var2) {
      return 1.0F;
   }

   public Iterable<Entity> getAllEntities() {
      return this.getEntities().getAll();
   }

   public String toString() {
      return "ServerLevel[" + this.serverLevelData.getLevelName() + "]";
   }

   public boolean isFlat() {
      return this.theGame.getWorldData().isFlatWorld();
   }

   public long getSeed() {
      return this.theGame.getWorldData().worldGenOptions().seed();
   }

   @Nullable
   public EndDragonFight getDragonFight() {
      return this.dragonFight;
   }

   public ServerLevel getLevel() {
      return this;
   }

   @VisibleForTesting
   public String getWatchdogStats() {
      return String.format(Locale.ROOT, "players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entityManager.gatherStats(), getTypeCount(this.entityManager.getEntityGetter().getAll(), (var0) -> BuiltInRegistries.ENTITY_TYPE.getKey(var0.getType()).toString()), this.blockEntityTickers.size(), getTypeCount(this.blockEntityTickers, TickingBlockEntity::getType), this.getBlockTicks().count(), this.getFluidTicks().count(), this.gatherChunkSourceStats());
   }

   private static <T> String getTypeCount(Iterable<T> var0, Function<T, String> var1) {
      try {
         Object2IntOpenHashMap var2 = new Object2IntOpenHashMap();

         for(Object var4 : var0) {
            String var5 = (String)var1.apply(var4);
            var2.addTo(var5, 1);
         }

         return (String)var2.object2IntEntrySet().stream().sorted(Comparator.comparing(Object2IntMap.Entry::getIntValue).reversed()).limit(5L).map((var0x) -> {
            String var10000 = (String)var0x.getKey();
            return var10000 + ":" + var0x.getIntValue();
         }).collect(Collectors.joining(","));
      } catch (Exception var6) {
         return "";
      }
   }

   protected LevelEntityGetter<Entity> getEntities() {
      return this.entityManager.getEntityGetter();
   }

   public void addLegacyChunkEntities(Stream<Entity> var1) {
      this.entityManager.addLegacyChunkEntities(var1);
   }

   public void addWorldGenChunkEntities(Stream<Entity> var1) {
      this.entityManager.addWorldGenChunkEntities(var1);
   }

   public void startTickingChunk(LevelChunk var1) {
      var1.unpackTicks(this.getLevelData().getGameTime());
   }

   public void onStructureStartsAvailable(ChunkAccess var1) {
      this.eventLoop().execute(() -> this.structureCheck.onStructureLoad(var1.getPos(), var1.getAllStarts()));
   }

   public PathTypeCache getPathTypeCache() {
      return this.pathTypesByPosCache;
   }

   public void close() throws IOException {
      super.close();
      this.entityManager.close();
   }

   public String gatherChunkSourceStats() {
      String var10000 = this.chunkSource.gatherStats();
      return "Chunks[S] W: " + var10000 + " E: " + this.entityManager.gatherStats();
   }

   public boolean areEntitiesLoaded(long var1) {
      return this.entityManager.areEntitiesLoaded(var1);
   }

   public boolean isPositionTickingWithEntitiesLoaded(long var1) {
      return this.areEntitiesLoaded(var1) && this.chunkSource.isPositionTicking(var1);
   }

   public boolean isPositionEntityTicking(BlockPos var1) {
      return this.entityManager.canPositionTick(var1) && this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(ChunkPos.asLong(var1));
   }

   public boolean areEntitiesActuallyLoadedAndTicking(ChunkPos var1) {
      return this.entityManager.isTicking(var1) && this.entityManager.areEntitiesLoaded(var1.toLong());
   }

   public boolean anyPlayerCloseEnoughForSpawning(BlockPos var1) {
      return this.anyPlayerCloseEnoughForSpawning(new ChunkPos(var1));
   }

   public boolean anyPlayerCloseEnoughForSpawning(ChunkPos var1) {
      return this.chunkSource.chunkMap.anyPlayerCloseEnoughForSpawning(var1);
   }

   public boolean canSpawnEntitiesInChunk(ChunkPos var1) {
      return this.entityManager.canPositionTick(var1) && this.getWorldBorder().isWithinBounds(var1);
   }

   public FeatureFlagSet enabledFeatures() {
      return this.theGame.getWorldData().enabledFeatures();
   }

   public PotionBrewing potionBrewing() {
      return this.theGame.potionBrewing();
   }

   public FuelValues fuelValues() {
      return this.theGame.fuelValues();
   }

   public RandomSource getRandomSequence(ResourceLocation var1) {
      return this.randomSequences.get(var1);
   }

   public RandomSequences getRandomSequences() {
      return this.randomSequences;
   }

   public GameRules getGameRules() {
      return this.serverLevelData.getGameRules();
   }

   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = super.fillReportDetails(var1);
      var2.setDetail("Loaded entity count", (CrashReportDetail)(() -> String.valueOf(this.entityManager.count())));
      return var2;
   }

   public void setDimensionType(Holder<DimensionType> var1) {
      super.setDimensionType(var1);
      if (this.dimension() == Level.OVERWORLD) {
         this.theGame.getWorldData().setHubDimensionType(var1);
      }

      this.players.forEach((var1x) -> var1x.connection.send(new ClientboundChangeDimensionTypePacket(var1)));
   }

   public int getSeaLevel() {
      return this.chunkSource.getGenerator().getSeaLevel();
   }

   // $FF: synthetic method
   public RecipeAccess recipeAccess() {
      return this.recipeAccess();
   }

   // $FF: synthetic method
   public Scoreboard getScoreboard() {
      return this.getScoreboard();
   }

   // $FF: synthetic method
   public TickRateManager tickRateManager() {
      return this.tickRateManager();
   }

   // $FF: synthetic method
   public ChunkSource getChunkSource() {
      return this.getChunkSource();
   }

   // $FF: synthetic method
   public LevelTickAccess getFluidTicks() {
      return this.getFluidTicks();
   }

   // $FF: synthetic method
   public LevelTickAccess getBlockTicks() {
      return this.getBlockTicks();
   }

   final class EntityCallbacks implements LevelCallback<Entity> {
      EntityCallbacks() {
         super();
      }

      public void onCreated(Entity var1) {
      }

      public void onDestroyed(Entity var1) {
         ServerLevel.this.getScoreboard().entityRemoved(var1);
      }

      public void onTickingStart(Entity var1) {
         ServerLevel.this.entityTickList.add(var1);
      }

      public void onTickingEnd(Entity var1) {
         ServerLevel.this.entityTickList.remove(var1);
      }

      public void onTrackingStart(Entity var1) {
         ServerLevel.this.getChunkSource().addEntity(var1);
         if (var1 instanceof ServerPlayer var2) {
            ServerLevel.this.players.add(var2);
            ServerLevel.this.updateSleepingPlayerList();
         }

         if (var1 instanceof Mob var7) {
            if (ServerLevel.this.isUpdatingNavigations) {
               String var3 = "onTrackingStart called during navigation iteration";
               Util.logAndPauseIfInIde("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
            }

            ServerLevel.this.navigatingMobs.add(var7);
         }

         if (var1 instanceof EnderDragon var8) {
            for(EnderDragonPart var6 : var8.getSubEntities()) {
               ServerLevel.this.dragonParts.put(var6.getId(), var6);
            }
         }

         var1.updateDynamicGameEventListener(DynamicGameEventListener::add);
      }

      public void onTrackingEnd(Entity var1) {
         ServerLevel.this.getChunkSource().removeEntity(var1);
         if (var1 instanceof ServerPlayer var2) {
            ServerLevel.this.players.remove(var2);
            ServerLevel.this.updateSleepingPlayerList();
         }

         if (var1 instanceof Mob var7) {
            if (ServerLevel.this.isUpdatingNavigations) {
               String var3 = "onTrackingStart called during navigation iteration";
               Util.logAndPauseIfInIde("onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration"));
            }

            ServerLevel.this.navigatingMobs.remove(var7);
         }

         if (var1 instanceof EnderDragon var8) {
            for(EnderDragonPart var6 : var8.getSubEntities()) {
               ServerLevel.this.dragonParts.remove(var6.getId());
            }
         }

         var1.updateDynamicGameEventListener(DynamicGameEventListener::remove);
      }

      public void onSectionChange(Entity var1) {
         var1.updateDynamicGameEventListener(DynamicGameEventListener::move);
      }

      // $FF: synthetic method
      public void onSectionChange(final Object var1) {
         this.onSectionChange((Entity)var1);
      }

      // $FF: synthetic method
      public void onTrackingEnd(final Object var1) {
         this.onTrackingEnd((Entity)var1);
      }

      // $FF: synthetic method
      public void onTrackingStart(final Object var1) {
         this.onTrackingStart((Entity)var1);
      }

      // $FF: synthetic method
      public void onTickingStart(final Object var1) {
         this.onTickingStart((Entity)var1);
      }

      // $FF: synthetic method
      public void onDestroyed(final Object var1) {
         this.onDestroyed((Entity)var1);
      }

      // $FF: synthetic method
      public void onCreated(final Object var1) {
         this.onCreated((Entity)var1);
      }
   }
}
