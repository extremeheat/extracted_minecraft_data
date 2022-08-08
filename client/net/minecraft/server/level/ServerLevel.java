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
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
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
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
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
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.server.players.SleepStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
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
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TickingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.EntityStorage;
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
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheck;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.portal.PortalForcer;
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
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import net.minecraft.world.ticks.LevelTicks;
import org.slf4j.Logger;

public class ServerLevel extends Level implements WorldGenLevel {
   public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
   private static final int MIN_RAIN_DELAY_TIME = 12000;
   private static final int MAX_RAIN_DELAY_TIME = 180000;
   private static final int MIN_RAIN_TIME = 12000;
   private static final int MAX_RAIN_TIME = 24000;
   private static final int MIN_THUNDER_DELAY_TIME = 12000;
   private static final int MAX_THUNDER_DELAY_TIME = 180000;
   private static final int MIN_THUNDER_TIME = 3600;
   private static final int MAX_THUNDER_TIME = 15600;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int EMPTY_TIME_NO_TICK = 300;
   private static final int MAX_SCHEDULED_TICKS_PER_TICK = 65536;
   final List<ServerPlayer> players;
   private final ServerChunkCache chunkSource;
   private final MinecraftServer server;
   private final ServerLevelData serverLevelData;
   final EntityTickList entityTickList;
   private final PersistentEntitySectionManager<Entity> entityManager;
   public boolean noSave;
   private final SleepStatus sleepStatus;
   private int emptyTime;
   private final PortalForcer portalForcer;
   private final LevelTicks<Block> blockTicks;
   private final LevelTicks<Fluid> fluidTicks;
   final Set<Mob> navigatingMobs;
   volatile boolean isUpdatingNavigations;
   protected final Raids raids;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents;
   private final List<BlockEventData> blockEventsToReschedule;
   private List<GameEvent.Message> gameEventMessages;
   private boolean handlingTick;
   private final List<CustomSpawner> customSpawners;
   @Nullable
   private final EndDragonFight dragonFight;
   final Int2ObjectMap<EnderDragonPart> dragonParts;
   private final StructureManager structureManager;
   private final StructureCheck structureCheck;
   private final boolean tickTime;

   public ServerLevel(MinecraftServer var1, Executor var2, LevelStorageSource.LevelStorageAccess var3, ServerLevelData var4, ResourceKey<Level> var5, LevelStem var6, ChunkProgressListener var7, boolean var8, long var9, List<CustomSpawner> var11, boolean var12) {
      Holder var10003 = var6.typeHolder();
      Objects.requireNonNull(var1);
      super(var4, var5, var10003, var1::getProfiler, false, var8, var9, var1.getMaxChainedNeighborUpdates());
      this.players = Lists.newArrayList();
      this.entityTickList = new EntityTickList();
      this.blockTicks = new LevelTicks(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
      this.fluidTicks = new LevelTicks(this::isPositionTickingWithEntitiesLoaded, this.getProfilerSupplier());
      this.navigatingMobs = new ObjectOpenHashSet();
      this.blockEvents = new ObjectLinkedOpenHashSet();
      this.blockEventsToReschedule = new ArrayList(64);
      this.gameEventMessages = new ArrayList();
      this.dragonParts = new Int2ObjectOpenHashMap();
      this.tickTime = var12;
      this.server = var1;
      this.customSpawners = var11;
      this.serverLevelData = var4;
      ChunkGenerator var13 = var6.generator();
      boolean var14 = var1.forceSynchronousWrites();
      DataFixer var15 = var1.getFixerUpper();
      EntityStorage var16 = new EntityStorage(this, var3.getDimensionPath(var5).resolve("entities"), var15, var14, var1);
      this.entityManager = new PersistentEntitySectionManager(Entity.class, new EntityCallbacks(), var16);
      StructureTemplateManager var10006 = var1.getStructureManager();
      int var10009 = var1.getPlayerList().getViewDistance();
      int var10010 = var1.getPlayerList().getSimulationDistance();
      PersistentEntitySectionManager var10013 = this.entityManager;
      Objects.requireNonNull(var10013);
      this.chunkSource = new ServerChunkCache(this, var3, var15, var10006, var2, var13, var10009, var10010, var14, var7, var10013::updateChunkStatus, () -> {
         return var1.overworld().getDataStorage();
      });
      var13.ensureStructuresGenerated(this.chunkSource.randomState());
      this.portalForcer = new PortalForcer(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.getWorldBorder().setAbsoluteMaxSize(var1.getAbsoluteMaxWorldSize());
      this.raids = (Raids)this.getDataStorage().computeIfAbsent((var1x) -> {
         return Raids.load(this, var1x);
      }, () -> {
         return new Raids(this);
      }, Raids.getFileId(this.dimensionTypeRegistration()));
      if (!var1.isSingleplayer()) {
         var4.setGameType(var1.getDefaultGameType());
      }

      long var17 = var1.getWorldData().worldGenSettings().seed();
      this.structureCheck = new StructureCheck(this.chunkSource.chunkScanner(), this.registryAccess(), var1.getStructureManager(), var5, var13, this.chunkSource.randomState(), this, var13.getBiomeSource(), var17, var15);
      this.structureManager = new StructureManager(this, var1.getWorldData().worldGenSettings(), this.structureCheck);
      if (this.dimension() == Level.END && this.dimensionTypeRegistration().is(BuiltinDimensionTypes.END)) {
         this.dragonFight = new EndDragonFight(this, var17, var1.getWorldData().endDragonFightData());
      } else {
         this.dragonFight = null;
      }

      this.sleepStatus = new SleepStatus();
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

   public void tick(BooleanSupplier var1) {
      ProfilerFiller var2 = this.getProfiler();
      this.handlingTick = true;
      var2.push("world border");
      this.getWorldBorder().tick();
      var2.popPush("weather");
      this.advanceWeatherCycle();
      int var3 = this.getGameRules().getInt(GameRules.RULE_PLAYERS_SLEEPING_PERCENTAGE);
      long var4;
      if (this.sleepStatus.areEnoughSleeping(var3) && this.sleepStatus.areEnoughDeepSleeping(var3, this.players)) {
         if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            var4 = this.levelData.getDayTime() + 24000L;
            this.setDayTime(var4 - var4 % 24000L);
         }

         this.wakeUpAllPlayers();
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE) && this.isRaining()) {
            this.resetWeatherCycle();
         }
      }

      this.updateSkyBrightness();
      this.tickTime();
      var2.popPush("tickPending");
      if (!this.isDebug()) {
         var4 = this.getGameTime();
         var2.push("blockTicks");
         this.blockTicks.tick(var4, 65536, this::tickBlock);
         var2.popPush("fluidTicks");
         this.fluidTicks.tick(var4, 65536, this::tickFluid);
         var2.pop();
      }

      var2.popPush("raid");
      this.raids.tick();
      var2.popPush("chunkSource");
      this.getChunkSource().tick(var1, true);
      var2.popPush("blockEvents");
      this.runBlockEvents();
      this.handlingTick = false;
      var2.pop();
      boolean var6 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
      if (var6) {
         this.resetEmptyTime();
      }

      if (var6 || this.emptyTime++ < 300) {
         var2.push("entities");
         if (this.dragonFight != null) {
            var2.push("dragonFight");
            this.dragonFight.tick();
            var2.pop();
         }

         this.entityTickList.forEach((var2x) -> {
            if (!var2x.isRemoved()) {
               if (this.shouldDiscardEntity(var2x)) {
                  var2x.discard();
               } else {
                  var2.push("checkDespawn");
                  var2x.checkDespawn();
                  var2.pop();
                  if (this.chunkSource.chunkMap.getDistanceManager().inEntityTickingRange(var2x.chunkPosition().toLong())) {
                     Entity var3 = var2x.getVehicle();
                     if (var3 != null) {
                        if (!var3.isRemoved() && var3.hasPassenger(var2x)) {
                           return;
                        }

                        var2x.stopRiding();
                     }

                     var2.push("tick");
                     this.guardEntityTick(this::tickNonPassenger, var2x);
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
      var2.popPush("gameEvents");
      this.sendGameEvents();
      var2.pop();
   }

   public boolean shouldTickBlocksAt(long var1) {
      return this.chunkSource.chunkMap.getDistanceManager().inBlockTickingRange(var1);
   }

   protected void tickTime() {
      if (this.tickTime) {
         long var1 = this.levelData.getGameTime() + 1L;
         this.serverLevelData.setGameTime(var1);
         this.serverLevelData.getScheduledEvents().tick(this.server, var1);
         if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            this.setDayTime(this.levelData.getDayTime() + 1L);
         }

      }
   }

   public void setDayTime(long var1) {
      this.serverLevelData.setDayTime(var1);
   }

   public void tickCustomSpawners(boolean var1, boolean var2) {
      Iterator var3 = this.customSpawners.iterator();

      while(var3.hasNext()) {
         CustomSpawner var4 = (CustomSpawner)var3.next();
         var4.tick(this, var1, var2);
      }

   }

   private boolean shouldDiscardEntity(Entity var1) {
      if (this.server.isSpawningAnimals() || !(var1 instanceof Animal) && !(var1 instanceof WaterAnimal)) {
         return !this.server.areNpcsEnabled() && var1 instanceof Npc;
      } else {
         return true;
      }
   }

   private void wakeUpAllPlayers() {
      this.sleepStatus.removeAllSleepers();
      ((List)this.players.stream().filter(LivingEntity::isSleeping).collect(Collectors.toList())).forEach((var0) -> {
         var0.stopSleepInBed(false, false);
      });
   }

   public void tickChunk(LevelChunk var1, int var2) {
      ChunkPos var3 = var1.getPos();
      boolean var4 = this.isRaining();
      int var5 = var3.getMinBlockX();
      int var6 = var3.getMinBlockZ();
      ProfilerFiller var7 = this.getProfiler();
      var7.push("thunder");
      BlockPos var8;
      if (var4 && this.isThundering() && this.random.nextInt(100000) == 0) {
         var8 = this.findLightningTargetAround(this.getBlockRandomPos(var5, 0, var6, 15));
         if (this.isRainingAt(var8)) {
            DifficultyInstance var9 = this.getCurrentDifficultyAt(var8);
            boolean var10 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double)var9.getEffectiveDifficulty() * 0.01 && !this.getBlockState(var8.below()).is(Blocks.LIGHTNING_ROD);
            if (var10) {
               SkeletonHorse var11 = (SkeletonHorse)EntityType.SKELETON_HORSE.create(this);
               var11.setTrap(true);
               var11.setAge(0);
               var11.setPos((double)var8.getX(), (double)var8.getY(), (double)var8.getZ());
               this.addFreshEntity(var11);
            }

            LightningBolt var22 = (LightningBolt)EntityType.LIGHTNING_BOLT.create(this);
            var22.moveTo(Vec3.atBottomCenterOf(var8));
            var22.setVisualOnly(var10);
            this.addFreshEntity(var22);
         }
      }

      var7.popPush("iceandsnow");
      if (this.random.nextInt(16) == 0) {
         var8 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, this.getBlockRandomPos(var5, 0, var6, 15));
         BlockPos var18 = var8.below();
         Biome var20 = (Biome)this.getBiome(var8).value();
         if (var20.shouldFreeze(this, var18)) {
            this.setBlockAndUpdate(var18, Blocks.ICE.defaultBlockState());
         }

         if (var4) {
            if (var20.shouldSnow(this, var8)) {
               this.setBlockAndUpdate(var8, Blocks.SNOW.defaultBlockState());
            }

            BlockState var23 = this.getBlockState(var18);
            Biome.Precipitation var12 = var20.getPrecipitation();
            if (var12 == Biome.Precipitation.RAIN && var20.coldEnoughToSnow(var18)) {
               var12 = Biome.Precipitation.SNOW;
            }

            var23.getBlock().handlePrecipitation(var23, this, var18, var12);
         }
      }

      var7.popPush("tickBlocks");
      if (var2 > 0) {
         LevelChunkSection[] var17 = var1.getSections();
         int var19 = var17.length;

         for(int var21 = 0; var21 < var19; ++var21) {
            LevelChunkSection var24 = var17[var21];
            if (var24.isRandomlyTicking()) {
               int var25 = var24.bottomBlockY();

               for(int var13 = 0; var13 < var2; ++var13) {
                  BlockPos var14 = this.getBlockRandomPos(var5, var25, var6, 15);
                  var7.push("randomTick");
                  BlockState var15 = var24.getBlockState(var14.getX() - var5, var14.getY() - var25, var14.getZ() - var6);
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

   private Optional<BlockPos> findLightningRod(BlockPos var1) {
      Optional var2 = this.getPoiManager().findClosest((var0) -> {
         return var0.is(PoiTypes.LIGHTNING_ROD);
      }, (var1x) -> {
         return var1x.getY() == this.getHeight(Heightmap.Types.WORLD_SURFACE, var1x.getX(), var1x.getZ()) - 1;
      }, var1, 128, PoiManager.Occupancy.ANY);
      return var2.map((var0) -> {
         return var0.above(1);
      });
   }

   protected BlockPos findLightningTargetAround(BlockPos var1) {
      BlockPos var2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var1);
      Optional var3 = this.findLightningRod(var2);
      if (var3.isPresent()) {
         return (BlockPos)var3.get();
      } else {
         AABB var4 = (new AABB(var2, new BlockPos(var2.getX(), this.getMaxBuildHeight(), var2.getZ()))).inflate(3.0);
         List var5 = this.getEntitiesOfClass(LivingEntity.class, var4, (var1x) -> {
            return var1x != null && var1x.isAlive() && this.canSeeSky(var1x.blockPosition());
         });
         if (!var5.isEmpty()) {
            return ((LivingEntity)var5.get(this.random.nextInt(var5.size()))).blockPosition();
         } else {
            if (var2.getY() == this.getMinBuildHeight() - 1) {
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

            Iterator var3 = this.players.iterator();

            while(var3.hasNext()) {
               ServerPlayer var4 = (ServerPlayer)var3.next();
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
                  var3 = Mth.randomBetweenInclusive(this.random, 3600, 15600);
               } else {
                  var3 = Mth.randomBetweenInclusive(this.random, 12000, 180000);
               }

               if (var4 > 0) {
                  --var4;
                  if (var4 == 0) {
                     var6 = !var6;
                  }
               } else if (var6) {
                  var4 = Mth.randomBetweenInclusive(this.random, 12000, 24000);
               } else {
                  var4 = Mth.randomBetweenInclusive(this.random, 12000, 180000);
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
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
      }

      if (this.oThunderLevel != this.thunderLevel) {
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
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

   private void resetWeatherCycle() {
      this.serverLevelData.setRainTime(0);
      this.serverLevelData.setRaining(false);
      this.serverLevelData.setThunderTime(0);
      this.serverLevelData.setThundering(false);
   }

   public void resetEmptyTime() {
      this.emptyTime = 0;
   }

   private void tickFluid(BlockPos var1, Fluid var2) {
      FluidState var3 = this.getFluidState(var1);
      if (var3.is(var2)) {
         var3.tick(this, var1);
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
      ProfilerFiller var2 = this.getProfiler();
      ++var1.tickCount;
      this.getProfiler().push(() -> {
         return Registry.ENTITY_TYPE.getKey(var1.getType()).toString();
      });
      var2.incrementCounter("tickNonPassenger");
      var1.tick();
      this.getProfiler().pop();
      Iterator var3 = var1.getPassengers().iterator();

      while(var3.hasNext()) {
         Entity var4 = (Entity)var3.next();
         this.tickPassenger(var1, var4);
      }

   }

   private void tickPassenger(Entity var1, Entity var2) {
      if (!var2.isRemoved() && var2.getVehicle() == var1) {
         if (var2 instanceof Player || this.entityTickList.contains(var2)) {
            var2.setOldPosAndRot();
            ++var2.tickCount;
            ProfilerFiller var3 = this.getProfiler();
            var3.push(() -> {
               return Registry.ENTITY_TYPE.getKey(var2.getType()).toString();
            });
            var3.incrementCounter("tickPassenger");
            var2.rideTick();
            var3.pop();
            Iterator var4 = var2.getPassengers().iterator();

            while(var4.hasNext()) {
               Entity var5 = (Entity)var4.next();
               this.tickPassenger(var2, var5);
            }

         }
      } else {
         var2.stopRiding();
      }
   }

   public boolean mayInteract(Player var1, BlockPos var2) {
      return !this.server.isUnderSpawnProtection(this, var2, var1) && this.getWorldBorder().isWithinBounds(var2);
   }

   public void save(@Nullable ProgressListener var1, boolean var2, boolean var3) {
      ServerChunkCache var4 = this.getChunkSource();
      if (!var3) {
         if (var1 != null) {
            var1.progressStartNoAbort(Component.translatable("menu.savingLevel"));
         }

         this.saveLevelData();
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

   private void saveLevelData() {
      if (this.dragonFight != null) {
         this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
      }

      this.getChunkSource().getDataStorage().save();
   }

   public <T extends Entity> List<? extends T> getEntities(EntityTypeTest<Entity, T> var1, Predicate<? super T> var2) {
      ArrayList var3 = Lists.newArrayList();
      this.getEntities().get(var1, (var2x) -> {
         if (var2.test(var2x)) {
            var3.add(var2x);
         }

      });
      return var3;
   }

   public List<? extends EnderDragon> getDragons() {
      return this.getEntities(EntityType.ENDER_DRAGON, LivingEntity::isAlive);
   }

   public List<ServerPlayer> getPlayers(Predicate<? super ServerPlayer> var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.players.iterator();

      while(var3.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var3.next();
         if (var1.test(var4)) {
            var2.add(var4);
         }
      }

      return var2;
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
      this.addEntity(var1);
   }

   public void addDuringCommandTeleport(ServerPlayer var1) {
      this.addPlayer(var1);
   }

   public void addDuringPortalTeleport(ServerPlayer var1) {
      this.addPlayer(var1);
   }

   public void addNewPlayer(ServerPlayer var1) {
      this.addPlayer(var1);
   }

   public void addRespawnedPlayer(ServerPlayer var1) {
      this.addPlayer(var1);
   }

   private void addPlayer(ServerPlayer var1) {
      Entity var2 = (Entity)this.getEntities().get(var1.getUUID());
      if (var2 != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", var1.getUUID().toString());
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
      Iterator var4 = this.server.getPlayerList().getPlayers().iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
         if (var5 != null && var5.level == this && var5.getId() != var1) {
            double var6 = (double)var2.getX() - var5.getX();
            double var8 = (double)var2.getY() - var5.getY();
            double var10 = (double)var2.getZ() - var5.getZ();
            if (var6 * var6 + var8 * var8 + var10 * var10 < 1024.0) {
               var5.connection.send(new ClientboundBlockDestructionPacket(var1, var2, var3));
            }
         }
      }

   }

   public void playSeededSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9, float var10, float var11, long var12) {
      this.server.getPlayerList().broadcast(var1, var2, var4, var6, (double)var8.getRange(var10), this.dimension(), new ClientboundSoundPacket(var8, var9, var2, var4, var6, var10, var11, var12));
   }

   public void playSeededSound(@Nullable Player var1, Entity var2, SoundEvent var3, SoundSource var4, float var5, float var6, long var7) {
      this.server.getPlayerList().broadcast(var1, var2.getX(), var2.getY(), var2.getZ(), (double)var3.getRange(var5), this.dimension(), new ClientboundSoundEntityPacket(var3, var4, var2, var5, var6, var7));
   }

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket(var1, var2, var3, true));
   }

   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
      this.server.getPlayerList().broadcast(var1, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 64.0, this.dimension(), new ClientboundLevelEventPacket(var2, var3, var4, false));
   }

   public int getLogicalHeight() {
      return this.dimensionType().logicalHeight();
   }

   public void gameEvent(GameEvent var1, Vec3 var2, GameEvent.Context var3) {
      int var4 = var1.getNotificationRadius();
      BlockPos var5 = new BlockPos(var2);
      int var6 = SectionPos.blockToSectionCoord(var5.getX() - var4);
      int var7 = SectionPos.blockToSectionCoord(var5.getY() - var4);
      int var8 = SectionPos.blockToSectionCoord(var5.getZ() - var4);
      int var9 = SectionPos.blockToSectionCoord(var5.getX() + var4);
      int var10 = SectionPos.blockToSectionCoord(var5.getY() + var4);
      int var11 = SectionPos.blockToSectionCoord(var5.getZ() + var4);
      ArrayList var12 = new ArrayList();
      boolean var13 = false;

      for(int var14 = var6; var14 <= var9; ++var14) {
         for(int var15 = var8; var15 <= var11; ++var15) {
            LevelChunk var16 = this.getChunkSource().getChunkNow(var14, var15);
            if (var16 != null) {
               for(int var17 = var7; var17 <= var10; ++var17) {
                  var13 |= var16.getEventDispatcher(var17).walkListeners(var1, var2, var3, (var5x, var6x) -> {
                     (var5x.handleEventsImmediately() ? var12 : this.gameEventMessages).add(new GameEvent.Message(var1, var2, var3, var5x, var6x));
                  });
               }
            }
         }
      }

      if (!var12.isEmpty()) {
         this.handleGameEventMessagesInQueue(var12);
      }

      if (var13) {
         DebugPackets.sendGameEventInfo(this, var1, var2);
      }

   }

   private void sendGameEvents() {
      if (!this.gameEventMessages.isEmpty()) {
         List var1 = this.gameEventMessages;
         this.gameEventMessages = new ArrayList();
         this.handleGameEventMessagesInQueue(var1);
      }
   }

   private void handleGameEventMessagesInQueue(List<GameEvent.Message> var1) {
      Collections.sort(var1);
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         GameEvent.Message var3 = (GameEvent.Message)var2.next();
         GameEventListener var4 = var3.recipient();
         var4.handleGameEvent(this, var3);
      }

   }

   public void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4) {
      if (this.isUpdatingNavigations) {
         String var5 = "recursive call to sendBlockUpdated";
         Util.logAndPauseIfInIde("recursive call to sendBlockUpdated", new IllegalStateException("recursive call to sendBlockUpdated"));
      }

      this.getChunkSource().blockChanged(var1);
      VoxelShape var14 = var2.getCollisionShape(this, var1);
      VoxelShape var6 = var3.getCollisionShape(this, var1);
      if (Shapes.joinIsNotEmpty(var14, var6, BooleanOp.NOT_SAME)) {
         ObjectArrayList var7 = new ObjectArrayList();
         Iterator var8 = this.navigatingMobs.iterator();

         while(var8.hasNext()) {
            Mob var9 = (Mob)var8.next();
            PathNavigation var10 = var9.getNavigation();
            if (var10.shouldRecomputePath(var1)) {
               var7.add(var10);
            }
         }

         try {
            this.isUpdatingNavigations = true;
            var8 = var7.iterator();

            while(var8.hasNext()) {
               PathNavigation var15 = (PathNavigation)var8.next();
               var15.recomputePath();
            }
         } finally {
            this.isUpdatingNavigations = false;
         }

      }
   }

   public void updateNeighborsAt(BlockPos var1, Block var2) {
      this.neighborUpdater.updateNeighborsAtExceptFromFacing(var1, var2, (Direction)null);
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos var1, Block var2, Direction var3) {
      this.neighborUpdater.updateNeighborsAtExceptFromFacing(var1, var2, var3);
   }

   public void neighborChanged(BlockPos var1, Block var2, BlockPos var3) {
      this.neighborUpdater.neighborChanged(var1, var2, var3);
   }

   public void neighborChanged(BlockState var1, BlockPos var2, Block var3, BlockPos var4, boolean var5) {
      this.neighborUpdater.neighborChanged(var1, var2, var3, var4, var5);
   }

   public void broadcastEntityEvent(Entity var1, byte var2) {
      this.getChunkSource().broadcastAndSend(var1, new ClientboundEntityEventPacket(var1, var2));
   }

   public ServerChunkCache getChunkSource() {
      return this.chunkSource;
   }

   public Explosion explode(@Nullable Entity var1, @Nullable DamageSource var2, @Nullable ExplosionDamageCalculator var3, double var4, double var6, double var8, float var10, boolean var11, Explosion.BlockInteraction var12) {
      Explosion var13 = new Explosion(this, var1, var2, var3, var4, var6, var8, var10, var11, var12);
      var13.explode();
      var13.finalizeExplosion(false);
      if (var12 == Explosion.BlockInteraction.NONE) {
         var13.clearToBlow();
      }

      Iterator var14 = this.players.iterator();

      while(var14.hasNext()) {
         ServerPlayer var15 = (ServerPlayer)var14.next();
         if (var15.distanceToSqr(var4, var6, var8) < 4096.0) {
            var15.connection.send(new ClientboundExplodePacket(var4, var6, var8, var10, var13.getToBlow(), (Vec3)var13.getHitPlayers().get(var15)));
         }
      }

      return var13;
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
               this.server.getPlayerList().broadcast((Player)null, (double)var1.pos().getX(), (double)var1.pos().getY(), (double)var1.pos().getZ(), 64.0, this.dimension(), new ClientboundBlockEventPacket(var1.pos(), var1.block(), var1.paramA(), var1.paramB()));
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
   public MinecraftServer getServer() {
      return this.server;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public StructureTemplateManager getStructureManager() {
      return this.server.getStructureManager();
   }

   public <T extends ParticleOptions> int sendParticles(T var1, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15) {
      ClientboundLevelParticlesPacket var17 = new ClientboundLevelParticlesPacket(var1, false, var2, var4, var6, (float)var9, (float)var11, (float)var13, (float)var15, var8);
      int var18 = 0;

      for(int var19 = 0; var19 < this.players.size(); ++var19) {
         ServerPlayer var20 = (ServerPlayer)this.players.get(var19);
         if (this.sendParticles(var20, false, var2, var4, var6, var17)) {
            ++var18;
         }
      }

      return var18;
   }

   public <T extends ParticleOptions> boolean sendParticles(ServerPlayer var1, T var2, boolean var3, double var4, double var6, double var8, int var10, double var11, double var13, double var15, double var17) {
      ClientboundLevelParticlesPacket var19 = new ClientboundLevelParticlesPacket(var2, var3, var4, var6, var8, (float)var11, (float)var13, (float)var15, (float)var17, var10);
      return this.sendParticles(var1, var3, var4, var6, var8, var19);
   }

   private boolean sendParticles(ServerPlayer var1, boolean var2, double var3, double var5, double var7, Packet<?> var9) {
      if (var1.getLevel() != this) {
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

   @Nullable
   public Entity getEntity(UUID var1) {
      return (Entity)this.getEntities().get(var1);
   }

   @Nullable
   public BlockPos findNearestMapStructure(TagKey<Structure> var1, BlockPos var2, int var3, boolean var4) {
      if (!this.server.getWorldData().worldGenSettings().generateStructures()) {
         return null;
      } else {
         Optional var5 = this.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).getTag(var1);
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

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public boolean noSave() {
      return this.noSave;
   }

   public RegistryAccess registryAccess() {
      return this.server.registryAccess();
   }

   public DimensionDataStorage getDataStorage() {
      return this.getChunkSource().getDataStorage();
   }

   @Nullable
   public MapItemSavedData getMapData(String var1) {
      return (MapItemSavedData)this.getServer().overworld().getDataStorage().get(MapItemSavedData::load, var1);
   }

   public void setMapData(String var1, MapItemSavedData var2) {
      this.getServer().overworld().getDataStorage().set(var1, var2);
   }

   public int getFreeMapId() {
      return ((MapIndex)this.getServer().overworld().getDataStorage().computeIfAbsent(MapIndex::load, MapIndex::new, "idcounts")).getFreeAuxValueForMap();
   }

   public void setDefaultSpawnPos(BlockPos var1, float var2) {
      ChunkPos var3 = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
      this.levelData.setSpawn(var1, var2);
      this.getChunkSource().removeRegionTicket(TicketType.START, var3, 11, Unit.INSTANCE);
      this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(var1), 11, Unit.INSTANCE);
      this.getServer().getPlayerList().broadcastAll(new ClientboundSetDefaultSpawnPositionPacket(var1, var2));
   }

   public LongSet getForcedChunks() {
      ForcedChunksSavedData var1 = (ForcedChunksSavedData)this.getDataStorage().get(ForcedChunksSavedData::load, "chunks");
      return (LongSet)(var1 != null ? LongSets.unmodifiable(var1.getChunks()) : LongSets.EMPTY_SET);
   }

   public boolean setChunkForced(int var1, int var2, boolean var3) {
      ForcedChunksSavedData var4 = (ForcedChunksSavedData)this.getDataStorage().computeIfAbsent(ForcedChunksSavedData::load, ForcedChunksSavedData::new, "chunks");
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

   public List<ServerPlayer> players() {
      return this.players;
   }

   public void onBlockStateChange(BlockPos var1, BlockState var2, BlockState var3) {
      Optional var4 = PoiTypes.forState(var2);
      Optional var5 = PoiTypes.forState(var3);
      if (!Objects.equals(var4, var5)) {
         BlockPos var6 = var1.immutable();
         var4.ifPresent((var2x) -> {
            this.getServer().execute(() -> {
               this.getPoiManager().remove(var6);
               DebugPackets.sendPoiRemovedPacket(this, var6);
            });
         });
         var5.ifPresent((var2x) -> {
            this.getServer().execute(() -> {
               this.getPoiManager().add(var6, var2x);
               DebugPackets.sendPoiAddedPacket(this, var6);
            });
         });
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
         var3.write(String.format(Locale.ROOT, "spawning_chunks: %d\n", var2.getDistanceManager().getNaturalSpawnChunkCount()));
         NaturalSpawner.SpawnState var4 = this.getChunkSource().getLastSpawnState();
         if (var4 != null) {
            ObjectIterator var5 = var4.getMobCategoryCounts().object2IntEntrySet().iterator();

            while(var5.hasNext()) {
               Object2IntMap.Entry var6 = (Object2IntMap.Entry)var5.next();
               var3.write(String.format(Locale.ROOT, "spawn_count.%s: %d\n", ((MobCategory)var6.getKey()).getName(), var6.getIntValue()));
            }
         }

         var3.write(String.format(Locale.ROOT, "entities: %s\n", this.entityManager.gatherStats()));
         var3.write(String.format(Locale.ROOT, "block_entity_tickers: %d\n", this.blockEntityTickers.size()));
         var3.write(String.format(Locale.ROOT, "block_ticks: %d\n", this.getBlockTicks().count()));
         var3.write(String.format(Locale.ROOT, "fluid_ticks: %d\n", this.getFluidTicks().count()));
         var3.write("distance_manager: " + var2.getDistanceManager().getDebugStatus() + "\n");
         var3.write(String.format(Locale.ROOT, "pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
      } catch (Throwable var22) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var12) {
               var22.addSuppressed(var12);
            }
         }

         throw var22;
      }

      if (var3 != null) {
         var3.close();
      }

      CrashReport var23 = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails(var23);
      BufferedWriter var24 = Files.newBufferedWriter(var1.resolve("example_crash.txt"));

      try {
         var24.write(var23.getFriendlyReport());
      } catch (Throwable var19) {
         if (var24 != null) {
            try {
               var24.close();
            } catch (Throwable var14) {
               var19.addSuppressed(var14);
            }
         }

         throw var19;
      }

      if (var24 != null) {
         var24.close();
      }

      Path var25 = var1.resolve("chunks.csv");
      BufferedWriter var26 = Files.newBufferedWriter(var25);

      try {
         var2.dumpChunks(var26);
      } catch (Throwable var18) {
         if (var26 != null) {
            try {
               var26.close();
            } catch (Throwable var13) {
               var18.addSuppressed(var13);
            }
         }

         throw var18;
      }

      if (var26 != null) {
         var26.close();
      }

      Path var27 = var1.resolve("entity_chunks.csv");
      BufferedWriter var28 = Files.newBufferedWriter(var27);

      try {
         this.entityManager.dumpSections(var28);
      } catch (Throwable var17) {
         if (var28 != null) {
            try {
               var28.close();
            } catch (Throwable var11) {
               var17.addSuppressed(var11);
            }
         }

         throw var17;
      }

      if (var28 != null) {
         var28.close();
      }

      Path var29 = var1.resolve("entities.csv");
      BufferedWriter var7 = Files.newBufferedWriter(var29);

      try {
         dumpEntities(var7, this.getEntities().getAll());
      } catch (Throwable var21) {
         if (var7 != null) {
            try {
               var7.close();
            } catch (Throwable var16) {
               var21.addSuppressed(var16);
            }
         }

         throw var21;
      }

      if (var7 != null) {
         var7.close();
      }

      Path var30 = var1.resolve("block_entities.csv");
      BufferedWriter var8 = Files.newBufferedWriter(var30);

      try {
         this.dumpBlockEntityTickers(var8);
      } catch (Throwable var20) {
         if (var8 != null) {
            try {
               var8.close();
            } catch (Throwable var15) {
               var20.addSuppressed(var15);
            }
         }

         throw var20;
      }

      if (var8 != null) {
         var8.close();
      }

   }

   private static void dumpEntities(Writer var0, Iterable<Entity> var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("uuid").addColumn("type").addColumn("alive").addColumn("display_name").addColumn("custom_name").build(var0);
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         Entity var4 = (Entity)var3.next();
         Component var5 = var4.getCustomName();
         Component var6 = var4.getDisplayName();
         var2.writeRow(var4.getX(), var4.getY(), var4.getZ(), var4.getUUID(), Registry.ENTITY_TYPE.getKey(var4.getType()), var4.isAlive(), var6.getString(), var5 != null ? var5.getString() : null);
      }

   }

   private void dumpBlockEntityTickers(Writer var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(var1);
      Iterator var3 = this.blockEntityTickers.iterator();

      while(var3.hasNext()) {
         TickingBlockEntity var4 = (TickingBlockEntity)var3.next();
         BlockPos var5 = var4.getPos();
         var2.writeRow(var5.getX(), var5.getY(), var5.getZ(), var4.getType());
      }

   }

   @VisibleForTesting
   public void clearBlockEvents(BoundingBox var1) {
      this.blockEvents.removeIf((var1x) -> {
         return var1.isInside(var1x.pos());
      });
   }

   public void blockUpdated(BlockPos var1, Block var2) {
      if (!this.isDebug()) {
         this.updateNeighborsAt(var1, var2);
      }

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
      return this.server.getWorldData().worldGenSettings().isFlatWorld();
   }

   public long getSeed() {
      return this.server.getWorldData().worldGenSettings().seed();
   }

   @Nullable
   public EndDragonFight dragonFight() {
      return this.dragonFight;
   }

   public ServerLevel getLevel() {
      return this;
   }

   @VisibleForTesting
   public String getWatchdogStats() {
      return String.format(Locale.ROOT, "players: %s, entities: %s [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entityManager.gatherStats(), getTypeCount(this.entityManager.getEntityGetter().getAll(), (var0) -> {
         return Registry.ENTITY_TYPE.getKey(var0.getType()).toString();
      }), this.blockEntityTickers.size(), getTypeCount(this.blockEntityTickers, TickingBlockEntity::getType), this.getBlockTicks().count(), this.getFluidTicks().count(), this.gatherChunkSourceStats());
   }

   private static <T> String getTypeCount(Iterable<T> var0, Function<T, String> var1) {
      try {
         Object2IntOpenHashMap var2 = new Object2IntOpenHashMap();
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
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

   public static void makeObsidianPlatform(ServerLevel var0) {
      BlockPos var1 = END_SPAWN_POINT;
      int var2 = var1.getX();
      int var3 = var1.getY() - 2;
      int var4 = var1.getZ();
      BlockPos.betweenClosed(var2 - 2, var3 + 1, var4 - 2, var2 + 2, var3 + 3, var4 + 2).forEach((var1x) -> {
         var0.setBlockAndUpdate(var1x, Blocks.AIR.defaultBlockState());
      });
      BlockPos.betweenClosed(var2 - 2, var3, var4 - 2, var2 + 2, var3, var4 + 2).forEach((var1x) -> {
         var0.setBlockAndUpdate(var1x, Blocks.OBSIDIAN.defaultBlockState());
      });
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
      this.server.execute(() -> {
         this.structureCheck.onStructureLoad(var1.getPos(), var1.getAllStarts());
      });
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

   // $FF: synthetic method
   public Scoreboard getScoreboard() {
      return this.getScoreboard();
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
            EnderDragonPart[] var9 = var8.getSubEntities();
            int var4 = var9.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               EnderDragonPart var6 = var9[var5];
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
            EnderDragonPart[] var9 = var8.getSubEntities();
            int var4 = var9.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               EnderDragonPart var6 = var9[var5];
               ServerLevel.this.dragonParts.remove(var6.getId());
            }
         }

         var1.updateDynamicGameEventListener(DynamicGameEventListener::remove);
      }

      public void onSectionChange(Entity var1) {
         var1.updateDynamicGameEventListener(DynamicGameEventListener::move);
      }

      // $FF: synthetic method
      public void onSectionChange(Object var1) {
         this.onSectionChange((Entity)var1);
      }

      // $FF: synthetic method
      public void onTrackingEnd(Object var1) {
         this.onTrackingEnd((Entity)var1);
      }

      // $FF: synthetic method
      public void onTrackingStart(Object var1) {
         this.onTrackingStart((Entity)var1);
      }

      // $FF: synthetic method
      public void onTickingStart(Object var1) {
         this.onTickingStart((Entity)var1);
      }

      // $FF: synthetic method
      public void onDestroyed(Object var1) {
         this.onDestroyed((Entity)var1);
      }

      // $FF: synthetic method
      public void onCreated(Object var1) {
         this.onCreated((Entity)var1);
      }
   }
}
