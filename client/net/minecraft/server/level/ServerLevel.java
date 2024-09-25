package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportType;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetDefaultSpawnPositionPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.EntityStorage;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
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
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathTypeCache;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.redstone.ExperimentalRedstoneUtils;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
   final List<ServerPlayer> players = Lists.newArrayList();
   private final ServerChunkCache chunkSource;
   private final MinecraftServer server;
   private final ServerLevelData serverLevelData;
   private int lastSpawnChunkRadius;
   final EntityTickList entityTickList = new EntityTickList();
   private final PersistentEntitySectionManager<Entity> entityManager;
   private final GameEventDispatcher gameEventDispatcher;
   public boolean noSave;
   private final SleepStatus sleepStatus;
   private int emptyTime;
   private final PortalForcer portalForcer;
   private final LevelTicks<Block> blockTicks = new LevelTicks<>(this::isPositionTickingWithEntitiesLoaded);
   private final LevelTicks<Fluid> fluidTicks = new LevelTicks<>(this::isPositionTickingWithEntitiesLoaded);
   private final PathTypeCache pathTypesByPosCache = new PathTypeCache();
   final Set<Mob> navigatingMobs = new ObjectOpenHashSet();
   volatile boolean isUpdatingNavigations;
   protected final Raids raids;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents = new ObjectLinkedOpenHashSet();
   private final List<BlockEventData> blockEventsToReschedule = new ArrayList<>(64);
   private boolean handlingTick;
   private final List<CustomSpawner> customSpawners;
   @Nullable
   private EndDragonFight dragonFight;
   final Int2ObjectMap<EnderDragonPart> dragonParts = new Int2ObjectOpenHashMap();
   private final StructureManager structureManager;
   private final StructureCheck structureCheck;
   private final boolean tickTime;
   private final RandomSequences randomSequences;

   public ServerLevel(
      MinecraftServer var1,
      Executor var2,
      LevelStorageSource.LevelStorageAccess var3,
      ServerLevelData var4,
      ResourceKey<Level> var5,
      LevelStem var6,
      ChunkProgressListener var7,
      boolean var8,
      long var9,
      List<CustomSpawner> var11,
      boolean var12,
      @Nullable RandomSequences var13
   ) {
      super(var4, var5, var1.registryAccess(), var6.type(), false, var8, var9, var1.getMaxChainedNeighborUpdates());
      this.tickTime = var12;
      this.server = var1;
      this.customSpawners = var11;
      this.serverLevelData = var4;
      ChunkGenerator var14 = var6.generator();
      boolean var15 = var1.forceSynchronousWrites();
      DataFixer var16 = var1.getFixerUpper();
      EntityStorage var17 = new EntityStorage(
         new SimpleRegionStorage(
            new RegionStorageInfo(var3.getLevelId(), var5, "entities"),
            var3.getDimensionPath(var5).resolve("entities"),
            var16,
            var15,
            DataFixTypes.ENTITY_CHUNK
         ),
         this,
         var1
      );
      this.entityManager = new PersistentEntitySectionManager<>(Entity.class, new ServerLevel.EntityCallbacks(), var17);
      this.chunkSource = new ServerChunkCache(
         this,
         var3,
         var16,
         var1.getStructureManager(),
         var2,
         var14,
         var1.getPlayerList().getViewDistance(),
         var1.getPlayerList().getSimulationDistance(),
         var15,
         var7,
         this.entityManager::updateChunkStatus,
         () -> var1.overworld().getDataStorage()
      );
      this.chunkSource.getGeneratorState().ensureStructuresGenerated();
      this.portalForcer = new PortalForcer(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.getWorldBorder().setAbsoluteMaxSize(var1.getAbsoluteMaxWorldSize());
      this.raids = this.getDataStorage().computeIfAbsent(Raids.factory(this), Raids.getFileId(this.dimensionTypeRegistration()));
      if (!var1.isSingleplayer()) {
         var4.setGameType(var1.getDefaultGameType());
      }

      long var18 = var1.getWorldData().worldGenOptions().seed();
      this.structureCheck = new StructureCheck(
         this.chunkSource.chunkScanner(),
         this.registryAccess(),
         var1.getStructureManager(),
         var5,
         var14,
         this.chunkSource.randomState(),
         this,
         var14.getBiomeSource(),
         var18,
         var16
      );
      this.structureManager = new StructureManager(this, var1.getWorldData().worldGenOptions(), this.structureCheck);
      if (this.dimension() == Level.END && this.dimensionTypeRegistration().is(BuiltinDimensionTypes.END)) {
         this.dragonFight = new EndDragonFight(this, var18, var1.getWorldData().endDragonFightData());
      } else {
         this.dragonFight = null;
      }

      this.sleepStatus = new SleepStatus();
      this.gameEventDispatcher = new GameEventDispatcher(this);
      this.randomSequences = Objects.requireNonNullElseGet(
         var13, () -> this.getDataStorage().computeIfAbsent(RandomSequences.factory(var18), "random_sequences")
      );
   }

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

   @Override
   public Holder<Biome> getUncachedNoiseBiome(int var1, int var2, int var3) {
      return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(var1, var2, var3, this.getChunkSource().randomState().sampler());
   }

   public StructureManager structureManager() {
      return this.structureManager;
   }

   public void tick(BooleanSupplier var1) {
      ProfilerFiller var2 = Profiler.get();
      this.handlingTick = true;
      TickRateManager var3 = this.tickRateManager();
      boolean var4 = var3.runsNormally();
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
         this.raids.tick();
      }

      var2.popPush("chunkSource");
      this.getChunkSource().tick(var1, true);
      var2.popPush("blockEvents");
      if (var4) {
         this.runBlockEvents();
      }

      this.handlingTick = false;
      var2.pop();
      boolean var9 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
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

         this.entityTickList.forEach(var3x -> {
            if (!var3x.isRemoved()) {
               if (!var3.isEntityFrozen(var3x)) {
                  var2.push("checkDespawn");
                  var3x.checkDespawn();
                  var2.pop();
                  if (var3x instanceof ServerPlayer || this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(var3x.chunkPosition().toLong())) {
                     Entity var4x = var3x.getVehicle();
                     if (var4x != null) {
                        if (!var4x.isRemoved() && var4x.hasPassenger(var3x)) {
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
   }

   @Override
   public boolean shouldTickBlocksAt(long var1) {
      return this.chunkSource.chunkMap.getDistanceManager().inBlockTickingRange(var1);
   }

   protected void tickTime() {
      if (this.tickTime) {
         long var1 = this.levelData.getGameTime() + 1L;
         this.serverLevelData.setGameTime(var1);
         Profiler.get().push("scheduledFunctions");
         this.serverLevelData.getScheduledEvents().tick(this.server, var1);
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
      for (CustomSpawner var4 : this.customSpawners) {
         var4.tick(this, var1, var2);
      }
   }

   private void wakeUpAllPlayers() {
      this.sleepStatus.removeAllSleepers();
      this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList()).forEach(var0 -> var0.stopSleepInBed(false, false));
   }

   public void tickChunk(LevelChunk var1, int var2) {
      ChunkPos var3 = var1.getPos();
      boolean var4 = this.isRaining();
      int var5 = var3.getMinBlockX();
      int var6 = var3.getMinBlockZ();
      ProfilerFiller var7 = Profiler.get();
      var7.push("thunder");
      if (var4 && this.isThundering() && this.random.nextInt(100000) == 0) {
         BlockPos var8 = this.findLightningTargetAround(this.getBlockRandomPos(var5, 0, var6, 15));
         if (this.isRainingAt(var8)) {
            DifficultyInstance var9 = this.getCurrentDifficultyAt(var8);
            boolean var10 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)
               && this.random.nextDouble() < (double)var9.getEffectiveDifficulty() * 0.01
               && !this.getBlockState(var8.below()).is(Blocks.LIGHTNING_ROD);
            if (var10) {
               SkeletonHorse var11 = EntityType.SKELETON_HORSE.create(this, EntitySpawnReason.EVENT);
               if (var11 != null) {
                  var11.setTrap(true);
                  var11.setAge(0);
                  var11.setPos((double)var8.getX(), (double)var8.getY(), (double)var8.getZ());
                  this.addFreshEntity(var11);
               }
            }

            LightningBolt var21 = EntityType.LIGHTNING_BOLT.create(this, EntitySpawnReason.EVENT);
            if (var21 != null) {
               var21.moveTo(Vec3.atBottomCenterOf(var8));
               var21.setVisualOnly(var10);
               this.addFreshEntity(var21);
            }
         }
      }

      var7.popPush("iceandsnow");

      for (int var17 = 0; var17 < var2; var17++) {
         if (this.random.nextInt(48) == 0) {
            this.tickPrecipitation(this.getBlockRandomPos(var5, 0, var6, 15));
         }
      }

      var7.popPush("tickBlocks");
      if (var2 > 0) {
         LevelChunkSection[] var18 = var1.getSections();

         for (int var19 = 0; var19 < var18.length; var19++) {
            LevelChunkSection var20 = var18[var19];
            if (var20.isRandomlyTicking()) {
               int var22 = var1.getSectionYFromSectionIndex(var19);
               int var12 = SectionPos.sectionToBlockCoord(var22);

               for (int var13 = 0; var13 < var2; var13++) {
                  BlockPos var14 = this.getBlockRandomPos(var5, var12, var6, 15);
                  var7.push("randomTick");
                  BlockState var15 = var20.getBlockState(var14.getX() - var5, var14.getY() - var12, var14.getZ() - var6);
                  if (var15.isRandomlyTicking()) {
                     var15.randomTick(this, var14, this.random);
                  }

                  FluidState var16 = var15.getFluidState();
                  if (var16.isRandomlyTicking()) {
                     var16.randomTick(this, var14, this.random);
                  }

                  var7.pop();
               }
            }
         }
      }

      var7.pop();
   }

   @VisibleForTesting
   public void tickPrecipitation(BlockPos var1) {
      BlockPos var2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var1);
      BlockPos var3 = var2.below();
      Biome var4 = this.getBiome(var2).value();
      if (var4.shouldFreeze(this, var3)) {
         this.setBlockAndUpdate(var3, Blocks.ICE.defaultBlockState());
      }

      if (this.isRaining()) {
         int var5 = this.getGameRules().getInt(GameRules.RULE_SNOW_ACCUMULATION_HEIGHT);
         if (var5 > 0 && var4.shouldSnow(this, var2)) {
            BlockState var6 = this.getBlockState(var2);
            if (var6.is(Blocks.SNOW)) {
               int var7 = var6.getValue(SnowLayerBlock.LAYERS);
               if (var7 < Math.min(var5, 8)) {
                  BlockState var8 = var6.setValue(SnowLayerBlock.LAYERS, Integer.valueOf(var7 + 1));
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
      Optional var2 = this.getPoiManager()
         .findClosest(
            var0 -> var0.is(PoiTypes.LIGHTNING_ROD),
            var1x -> var1x.getY() == this.getHeight(Heightmap.Types.WORLD_SURFACE, var1x.getX(), var1x.getZ()) - 1,
            var1,
            128,
            PoiManager.Occupancy.ANY
         );
      return var2.map(var0 -> var0.above(1));
   }

   protected BlockPos findLightningTargetAround(BlockPos var1) {
      BlockPos var2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var1);
      Optional var3 = this.findLightningRod(var2);
      if (var3.isPresent()) {
         return (BlockPos)var3.get();
      } else {
         AABB var4 = AABB.encapsulatingFullBlocks(var2, var2.atY(this.getMaxY() + 1)).inflate(3.0);
         List var5 = this.getEntitiesOfClass(LivingEntity.class, var4, var1x -> var1x != null && var1x.isAlive() && this.canSeeSky(var1x.blockPosition()));
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
         if (!this.getServer().isSingleplayer() || this.getServer().isPublished()) {
            int var1 = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
            MutableComponent var2;
            if (this.sleepStatus.areEnoughSleeping(var1)) {
               var2 = Component.translatable("sleep.skipping_night");
            } else {
               var2 = Component.translatable("sleep.players_sleeping", this.sleepStatus.amountSleeping(), this.sleepStatus.sleepersNeeded(var1));
            }

            for (ServerPlayer var4 : this.players) {
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
      return this.server.getScoreboard();
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
               var2--;
               var3 = var5 ? 0 : 1;
               var4 = var6 ? 0 : 1;
               var5 = false;
               var6 = false;
            } else {
               if (var3 > 0) {
                  if (--var3 == 0) {
                     var5 = !var5;
                  }
               } else if (var5) {
                  var3 = THUNDER_DURATION.sample(this.random);
               } else {
                  var3 = THUNDER_DELAY.sample(this.random);
               }

               if (var4 > 0) {
                  if (--var4 == 0) {
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
         this.server
            .getPlayerList()
            .broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
      }

      if (this.oThunderLevel != this.thunderLevel) {
         this.server
            .getPlayerList()
            .broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
      }

      if (var1 != this.isRaining()) {
         if (var1) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F));
         } else {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
         }

         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel));
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel));
      }
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
      var1.tickCount++;
      var2.push(() -> BuiltInRegistries.ENTITY_TYPE.getKey(var1.getType()).toString());
      var2.incrementCounter("tickNonPassenger");
      var1.tick();
      var2.pop();

      for (Entity var4 : var1.getPassengers()) {
         this.tickPassenger(var1, var4);
      }
   }

   private void tickPassenger(Entity var1, Entity var2) {
      if (var2.isRemoved() || var2.getVehicle() != var1) {
         var2.stopRiding();
      } else if (var2 instanceof Player || this.entityTickList.contains(var2)) {
         var2.setOldPosAndRot();
         var2.tickCount++;
         ProfilerFiller var3 = Profiler.get();
         var3.push(() -> BuiltInRegistries.ENTITY_TYPE.getKey(var2.getType()).toString());
         var3.incrementCounter("tickPassenger");
         var2.rideTick();
         var3.pop();

         for (Entity var5 : var2.getPassengers()) {
            this.tickPassenger(var2, var5);
         }
      }
   }

   @Override
   public boolean mayInteract(Player var1, BlockPos var2) {
      return !this.server.isUnderSpawnProtection(this, var2, var1) && this.getWorldBorder().isWithinBounds(var2);
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
         this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
      }

      DimensionDataStorage var2 = this.getChunkSource().getDataStorage();
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
      this.getEntities().get(var1, var3x -> {
         if (var2.test(var3x)) {
            var3.add(var3x);
            if (var3.size() >= var4) {
               return AbortableIterationConsumer.Continuation.ABORT;
            }
         }

         return AbortableIterationConsumer.Continuation.CONTINUE;
      });
   }

   public List<? extends EnderDragon> getDragons() {
      return this.getEntities(EntityType.ENDER_DRAGON, LivingEntity::isAlive);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> var1) {
      return this.getPlayers(var1, 2147483647);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> var1, int var2) {
      ArrayList var3 = Lists.newArrayList();

      for (ServerPlayer var5 : this.players) {
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

   @Override
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
      Entity var2 = this.getEntities().get(var1.getUUID());
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
      if (var1.getSelfAndPassengers().map(Entity::getUUID).anyMatch(this.entityManager::isLoaded)) {
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

   @Override
   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      for (ServerPlayer var5 : this.server.getPlayerList().getPlayers()) {
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

   @Override
   public void playSeededSound(
      @Nullable Player var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12
   ) {
      this.server
         .getPlayerList()
         .broadcast(
            var1,
            var2,
            var4,
            var6,
            (double)((SoundEvent)var8.value()).getRange(var10),
            this.dimension(),
            new ClientboundSoundPacket(var8, var9, var2, var4, var6, var10, var11, var12)
         );
   }

   @Override
   public void playSeededSound(@Nullable Player var1, Entity var2, Holder<SoundEvent> var3, SoundSource var4, float var5, float var6, long var7) {
      this.server
         .getPlayerList()
         .broadcast(
            var1,
            var2.getX(),
            var2.getY(),
            var2.getZ(),
            (double)((SoundEvent)var3.value()).getRange(var5),
            this.dimension(),
            new ClientboundSoundEntityPacket(var3, var4, var2, var5, var6, var7)
         );
   }

   @Override
   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      if (this.getGameRules().getBoolean(GameRules.RULE_GLOBAL_SOUND_EVENTS)) {
         this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket(var1, var2, var3, true));
      } else {
         this.levelEvent(null, var1, var2, var3);
      }
   }

   @Override
   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
      this.server
         .getPlayerList()
         .broadcast(
            var1,
            (double)var3.getX(),
            (double)var3.getY(),
            (double)var3.getZ(),
            64.0,
            this.dimension(),
            new ClientboundLevelEventPacket(var2, var3, var4, false)
         );
   }

   public int getLogicalHeight() {
      return this.dimensionType().logicalHeight();
   }

   @Override
   public void gameEvent(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3) {
      this.gameEventDispatcher.post(var1, var2, var3);
   }

   @Override
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

         for (Mob var9 : this.navigatingMobs) {
            PathNavigation var10 = var9.getNavigation();
            if (var10.shouldRecomputePath(var1)) {
               var7.add(var10);
            }
         }

         try {
            this.isUpdatingNavigations = true;

            for (PathNavigation var16 : var7) {
               var16.recomputePath();
            }
         } finally {
            this.isUpdatingNavigations = false;
         }
      }
   }

   @Override
   public void updateNeighborsAt(BlockPos var1, Block var2) {
      this.updateNeighborsAt(var1, var2, ExperimentalRedstoneUtils.initialOrientation(this, null, null));
   }

   @Override
   public void updateNeighborsAt(BlockPos var1, Block var2, @Nullable Orientation var3) {
      this.neighborUpdater.updateNeighborsAtExceptFromFacing(var1, var2, null, var3);
   }

   @Override
   public void updateNeighborsAtExceptFromFacing(BlockPos var1, Block var2, Direction var3, @Nullable Orientation var4) {
      this.neighborUpdater.updateNeighborsAtExceptFromFacing(var1, var2, var3, var4);
   }

   @Override
   public void neighborChanged(BlockPos var1, Block var2, @Nullable Orientation var3) {
      this.neighborUpdater.neighborChanged(var1, var2, var3);
   }

   @Override
   public void neighborChanged(BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5) {
      this.neighborUpdater.neighborChanged(var1, var2, var3, var4, var5);
   }

   @Override
   public void broadcastEntityEvent(Entity var1, byte var2) {
      this.getChunkSource().broadcastAndSend(var1, new ClientboundEntityEventPacket(var1, var2));
   }

   @Override
   public void broadcastDamageEvent(Entity var1, DamageSource var2) {
      this.getChunkSource().broadcastAndSend(var1, new ClientboundDamageEventPacket(var1, var2));
   }

   public ServerChunkCache getChunkSource() {
      return this.chunkSource;
   }

   @Override
   public void explode(
      @Nullable Entity var1,
      @Nullable DamageSource var2,
      @Nullable ExplosionDamageCalculator var3,
      double var4,
      double var6,
      double var8,
      float var10,
      boolean var11,
      Level.ExplosionInteraction var12,
      ParticleOptions var13,
      ParticleOptions var14,
      Holder<SoundEvent> var15
   ) {
      Explosion.BlockInteraction var16 = switch (var12) {
         case NONE -> Explosion.BlockInteraction.KEEP;
         case BLOCK -> this.getDestroyType(GameRules.RULE_BLOCK_EXPLOSION_DROP_DECAY);
         case MOB -> this.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)
         ? this.getDestroyType(GameRules.RULE_MOB_EXPLOSION_DROP_DECAY)
         : Explosion.BlockInteraction.KEEP;
         case TNT -> this.getDestroyType(GameRules.RULE_TNT_EXPLOSION_DROP_DECAY);
         case TRIGGER -> Explosion.BlockInteraction.TRIGGER_BLOCK;
      };
      Vec3 var17 = new Vec3(var4, var6, var8);
      ServerExplosion var18 = new ServerExplosion(this, var1, var2, var3, var17, var10, var11, var16);
      var18.explode();
      ParticleOptions var19 = var18.isSmall() ? var13 : var14;

      for (ServerPlayer var21 : this.players) {
         if (var21.distanceToSqr(var17) < 4096.0) {
            Optional var22 = Optional.ofNullable(var18.getHitPlayers().get(var21));
            var21.connection.send(new ClientboundExplodePacket(var17, var22, var19, var15));
         }
      }
   }

   private Explosion.BlockInteraction getDestroyType(GameRules.Key<GameRules.BooleanValue> var1) {
      return this.getGameRules().getBoolean(var1) ? Explosion.BlockInteraction.DESTROY_WITH_DECAY : Explosion.BlockInteraction.DESTROY;
   }

   @Override
   public void blockEvent(BlockPos var1, Block var2, int var3, int var4) {
      this.blockEvents.add(new BlockEventData(var1, var2, var3, var4));
   }

   private void runBlockEvents() {
      this.blockEventsToReschedule.clear();

      while (!this.blockEvents.isEmpty()) {
         BlockEventData var1 = (BlockEventData)this.blockEvents.removeFirst();
         if (this.shouldTickBlocksAt(var1.pos())) {
            if (this.doBlockEvent(var1)) {
               this.server
                  .getPlayerList()
                  .broadcast(
                     null,
                     (double)var1.pos().getX(),
                     (double)var1.pos().getY(),
                     (double)var1.pos().getZ(),
                     64.0,
                     this.dimension(),
                     new ClientboundBlockEventPacket(var1.pos(), var1.block(), var1.paramA(), var1.paramB())
                  );
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

   @Nonnull
   @Override
   public MinecraftServer getServer() {
      return this.server;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public StructureTemplateManager getStructureManager() {
      return this.server.getStructureManager();
   }

   public <T extends ParticleOptions> int sendParticles(
      T var1, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15
   ) {
      ClientboundLevelParticlesPacket var17 = new ClientboundLevelParticlesPacket(
         var1, false, var2, var4, var6, (float)var9, (float)var11, (float)var13, (float)var15, var8
      );
      int var18 = 0;

      for (int var19 = 0; var19 < this.players.size(); var19++) {
         ServerPlayer var20 = this.players.get(var19);
         if (this.sendParticles(var20, false, var2, var4, var6, var17)) {
            var18++;
         }
      }

      return var18;
   }

   public <T extends ParticleOptions> boolean sendParticles(
      ServerPlayer var1, T var2, boolean var3, double var4, double var6, double var8, int var10, double var11, double var13, double var15, double var17
   ) {
      ClientboundLevelParticlesPacket var19 = new ClientboundLevelParticlesPacket(
         var2, var3, var4, var6, var8, (float)var11, (float)var13, (float)var15, (float)var17, var10
      );
      return this.sendParticles(var1, var3, var4, var6, var8, var19);
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
   @Override
   public Entity getEntity(int var1) {
      return this.getEntities().get(var1);
   }

   @Deprecated
   @Nullable
   public Entity getEntityOrPart(int var1) {
      Entity var2 = this.getEntities().get(var1);
      return var2 != null ? var2 : (Entity)this.dragonParts.get(var1);
   }

   @Nullable
   public Entity getEntity(UUID var1) {
      return this.getEntities().get(var1);
   }

   @Nullable
   public BlockPos findNearestMapStructure(TagKey<Structure> var1, BlockPos var2, int var3, boolean var4) {
      if (!this.server.getWorldData().worldGenOptions().generateStructures()) {
         return null;
      } else {
         Optional var5 = this.registryAccess().lookupOrThrow(Registries.STRUCTURE).get(var1);
         if (var5.isEmpty()) {
            return null;
         } else {
            Pair var6 = this.getChunkSource().getGenerator().findNearestMapStructure(this, (HolderSet<Structure>)var5.get(), var2, var3, var4);
            return var6 != null ? (BlockPos)var6.getFirst() : null;
         }
      }
   }

   @Nullable
   public Pair<BlockPos, Holder<Biome>> findClosestBiome3d(Predicate<Holder<Biome>> var1, BlockPos var2, int var3, int var4, int var5) {
      return this.getChunkSource()
         .getGenerator()
         .getBiomeSource()
         .findClosestBiome3d(var2, var3, var4, var5, var1, this.getChunkSource().randomState().sampler(), this);
   }

   @Override
   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   @Override
   public TickRateManager tickRateManager() {
      return this.server.tickRateManager();
   }

   @Override
   public boolean noSave() {
      return this.noSave;
   }

   public DimensionDataStorage getDataStorage() {
      return this.getChunkSource().getDataStorage();
   }

   @Nullable
   @Override
   public MapItemSavedData getMapData(MapId var1) {
      return this.getServer().overworld().getDataStorage().get(MapItemSavedData.factory(), var1.key());
   }

   @Override
   public void setMapData(MapId var1, MapItemSavedData var2) {
      this.getServer().overworld().getDataStorage().set(var1.key(), var2);
   }

   @Override
   public MapId getFreeMapId() {
      return this.getServer().overworld().getDataStorage().computeIfAbsent(MapIndex.factory(), "idcounts").getFreeAuxValueForMap();
   }

   public void setDefaultSpawnPos(BlockPos var1, float var2) {
      BlockPos var3 = this.levelData.getSpawnPos();
      float var4 = this.levelData.getSpawnAngle();
      if (!var3.equals(var1) || var4 != var2) {
         this.levelData.setSpawn(var1, var2);
         this.getServer().getPlayerList().broadcastAll(new ClientboundSetDefaultSpawnPositionPacket(var1, var2));
      }

      if (this.lastSpawnChunkRadius > 1) {
         this.getChunkSource().removeRegionTicket(TicketType.START, new ChunkPos(var3), this.lastSpawnChunkRadius, Unit.INSTANCE);
      }

      int var5 = this.getGameRules().getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS) + 1;
      if (var5 > 1) {
         this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(var1), var5, Unit.INSTANCE);
      }

      this.lastSpawnChunkRadius = var5;
   }

   public LongSet getForcedChunks() {
      ForcedChunksSavedData var1 = this.getDataStorage().get(ForcedChunksSavedData.factory(), "chunks");
      return (LongSet)(var1 != null ? LongSets.unmodifiable(var1.getChunks()) : LongSets.EMPTY_SET);
   }

   public boolean setChunkForced(int var1, int var2, boolean var3) {
      ForcedChunksSavedData var4 = this.getDataStorage().computeIfAbsent(ForcedChunksSavedData.factory(), "chunks");
      ChunkPos var5 = new ChunkPos(var1, var2);
      long var6 = var5.toLong();
      boolean var8;
      if (var3) {
         var8 = var4.getChunks().add(var6);
         if (var8) {
            this.getChunk(var1, var2);
         }
      } else {
         var8 = var4.getChunks().remove(var6);
      }

      var4.setDirty(var8);
      if (var8) {
         this.getChunkSource().updateChunkForced(var5, var3);
      }

      return var8;
   }

   @Override
   public List<ServerPlayer> players() {
      return this.players;
   }

   @Override
   public void onBlockStateChange(BlockPos var1, BlockState var2, BlockState var3) {
      Optional var4 = PoiTypes.forState(var2);
      Optional var5 = PoiTypes.forState(var3);
      if (!Objects.equals(var4, var5)) {
         BlockPos var6 = var1.immutable();
         var4.ifPresent(var2x -> this.getServer().execute(() -> {
               this.getPoiManager().remove(var6);
               DebugPackets.sendPoiRemovedPacket(this, var6);
            }));
         var5.ifPresent(var2x -> this.getServer().execute(() -> {
               this.getPoiManager().add(var6, (Holder<PoiType>)var2x);
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
      return var2 > 6 ? false : this.sectionsToVillage(SectionPos.of(var1)) <= var2;
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

      try (BufferedWriter var3 = Files.newBufferedWriter(var1.resolve("stats.txt"))) {
         var3.write(String.format(Locale.ROOT, "spawning_chunks: %d\n", var2.getDistanceManager().getNaturalSpawnChunkCount()));
         NaturalSpawner.SpawnState var4 = this.getChunkSource().getLastSpawnState();
         if (var4 != null) {
            ObjectIterator var5 = var4.getMobCategoryCounts().object2IntEntrySet().iterator();

            while (var5.hasNext()) {
               Entry var6 = (Entry)var5.next();
               var3.write(String.format(Locale.ROOT, "spawn_count.%s: %d\n", ((MobCategory)var6.getKey()).getName(), var6.getIntValue()));
            }
         }

         var3.write(String.format(Locale.ROOT, "entities: %s\n", this.entityManager.gatherStats()));
         var3.write(String.format(Locale.ROOT, "block_entity_tickers: %d\n", this.blockEntityTickers.size()));
         var3.write(String.format(Locale.ROOT, "block_ticks: %d\n", this.getBlockTicks().count()));
         var3.write(String.format(Locale.ROOT, "fluid_ticks: %d\n", this.getFluidTicks().count()));
         var3.write("distance_manager: " + var2.getDistanceManager().getDebugStatus() + "\n");
         var3.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
      }

      CrashReport var23 = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails(var23);

      try (BufferedWriter var24 = Files.newBufferedWriter(var1.resolve("example_crash.txt"))) {
         var24.write(var23.getFriendlyReport(ReportType.TEST));
      }

      Path var25 = var1.resolve("chunks.csv");

      try (BufferedWriter var26 = Files.newBufferedWriter(var25)) {
         var2.dumpChunks(var26);
      }

      Path var27 = var1.resolve("entity_chunks.csv");

      try (BufferedWriter var28 = Files.newBufferedWriter(var27)) {
         this.entityManager.dumpSections(var28);
      }

      Path var29 = var1.resolve("entities.csv");

      try (BufferedWriter var7 = Files.newBufferedWriter(var29)) {
         dumpEntities(var7, this.getEntities().getAll());
      }

      Path var30 = var1.resolve("block_entities.csv");

      try (BufferedWriter var8 = Files.newBufferedWriter(var30)) {
         this.dumpBlockEntityTickers(var8);
      }
   }

   private static void dumpEntities(Writer var0, Iterable<Entity> var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder()
         .addColumn("x")
         .addColumn("y")
         .addColumn("z")
         .addColumn("uuid")
         .addColumn("type")
         .addColumn("alive")
         .addColumn("display_name")
         .addColumn("custom_name")
         .build(var0);

      for (Entity var4 : var1) {
         Component var5 = var4.getCustomName();
         Component var6 = var4.getDisplayName();
         var2.writeRow(
            var4.getX(),
            var4.getY(),
            var4.getZ(),
            var4.getUUID(),
            BuiltInRegistries.ENTITY_TYPE.getKey(var4.getType()),
            var4.isAlive(),
            var6.getString(),
            var5 != null ? var5.getString() : null
         );
      }
   }

   private void dumpBlockEntityTickers(Writer var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(var1);

      for (TickingBlockEntity var4 : this.blockEntityTickers) {
         BlockPos var5 = var4.getPos();
         var2.writeRow(var5.getX(), var5.getY(), var5.getZ(), var4.getType());
      }
   }

   @VisibleForTesting
   public void clearBlockEvents(BoundingBox var1) {
      this.blockEvents.removeIf(var1x -> var1.isInside(var1x.pos()));
   }

   @Override
   public void blockUpdated(BlockPos var1, Block var2) {
      if (!this.isDebug()) {
         this.updateNeighborsAt(var1, var2);
      }
   }

   @Override
   public float getShade(Direction var1, boolean var2) {
      return 1.0F;
   }

   public Iterable<Entity> getAllEntities() {
      return this.getEntities().getAll();
   }

   @Override
   public String toString() {
      return "ServerLevel[" + this.serverLevelData.getLevelName() + "]";
   }

   public boolean isFlat() {
      return this.server.getWorldData().isFlatWorld();
   }

   @Override
   public long getSeed() {
      return this.server.getWorldData().worldGenOptions().seed();
   }

   @Nullable
   public EndDragonFight getDragonFight() {
      return this.dragonFight;
   }

   @Override
   public ServerLevel getLevel() {
      return this;
   }

   @VisibleForTesting
   public String getWatchdogStats() {
      return String.format(
         Locale.ROOT,
         "players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s",
         this.players.size(),
         this.entityManager.gatherStats(),
         getTypeCount(this.entityManager.getEntityGetter().getAll(), var0 -> BuiltInRegistries.ENTITY_TYPE.getKey(var0.getType()).toString()),
         this.blockEntityTickers.size(),
         getTypeCount(this.blockEntityTickers, TickingBlockEntity::getType),
         this.getBlockTicks().count(),
         this.getFluidTicks().count(),
         this.gatherChunkSourceStats()
      );
   }

   private static <T> String getTypeCount(Iterable<T> var0, Function<T, String> var1) {
      try {
         Object2IntOpenHashMap var2 = new Object2IntOpenHashMap();

         for (Object var4 : var0) {
            String var5 = (String)var1.apply(var4);
            var2.addTo(var5, 1);
         }

         return var2.object2IntEntrySet()
            .stream()
            .sorted(Comparator.comparing(Entry::getIntValue).reversed())
            .limit(5L)
            .map(var0x -> (String)var0x.getKey() + ":" + var0x.getIntValue())
            .collect(Collectors.joining(","));
      } catch (Exception var6) {
         return "";
      }
   }

   @Override
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
      this.server.execute(() -> this.structureCheck.onStructureLoad(var1.getPos(), var1.getAllStarts()));
   }

   public PathTypeCache getPathTypeCache() {
      return this.pathTypesByPosCache;
   }

   @Override
   public void close() throws IOException {
      super.close();
      this.entityManager.close();
   }

   @Override
   public String gatherChunkSourceStats() {
      return "Chunks[S] W: " + this.chunkSource.gatherStats() + " E: " + this.entityManager.gatherStats();
   }

   public boolean areEntitiesLoaded(long var1) {
      return this.entityManager.areEntitiesLoaded(var1);
   }

   private boolean isPositionTickingWithEntitiesLoaded(long var1) {
      return this.areEntitiesLoaded(var1) && this.chunkSource.isPositionTicking(var1);
   }

   public boolean isPositionEntityTicking(BlockPos var1) {
      return this.entityManager.canPositionTick(var1) && this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(ChunkPos.asLong(var1));
   }

   public boolean isNaturalSpawningAllowed(BlockPos var1) {
      return this.entityManager.canPositionTick(var1);
   }

   public boolean isNaturalSpawningAllowed(ChunkPos var1) {
      return this.entityManager.canPositionTick(var1);
   }

   @Override
   public FeatureFlagSet enabledFeatures() {
      return this.server.getWorldData().enabledFeatures();
   }

   @Override
   public PotionBrewing potionBrewing() {
      return this.server.potionBrewing();
   }

   @Override
   public FuelValues fuelValues() {
      return this.server.fuelValues();
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

   @Override
   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = super.fillReportDetails(var1);
      var2.setDetail("Loaded entity count", () -> String.valueOf(this.entityManager.count()));
      return var2;
   }

   @Override
   public int getSeaLevel() {
      return this.chunkSource.getGenerator().getSeaLevel();
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
               Util.logAndPauseIfInIde(
                  "onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration")
               );
            }

            ServerLevel.this.navigatingMobs.add(var7);
         }

         if (var1 instanceof EnderDragon var8) {
            for (EnderDragonPart var6 : var8.getSubEntities()) {
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
               Util.logAndPauseIfInIde(
                  "onTrackingStart called during navigation iteration", new IllegalStateException("onTrackingStart called during navigation iteration")
               );
            }

            ServerLevel.this.navigatingMobs.remove(var7);
         }

         if (var1 instanceof EnderDragon var8) {
            for (EnderDragonPart var6 : var8.getSubEntities()) {
               ServerLevel.this.dragonParts.remove(var6.getId());
            }
         }

         var1.updateDynamicGameEventListener(DynamicGameEventListener::remove);
      }

      public void onSectionChange(Entity var1) {
         var1.updateDynamicGameEventListener(DynamicGameEventListener::move);
      }
   }
}
