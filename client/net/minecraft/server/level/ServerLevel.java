package net.minecraft.server.level;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
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
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.ClassInstanceMultiMap;
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
import net.minecraft.world.entity.ai.village.poi.PoiType;
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
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLevel extends Level implements WorldGenLevel {
   public static final BlockPos END_SPAWN_POINT = new BlockPos(100, 50, 0);
   private static final Logger LOGGER = LogManager.getLogger();
   private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap();
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   private final Queue<Entity> toAddAfterTick = Queues.newArrayDeque();
   private final List<ServerPlayer> players = Lists.newArrayList();
   private final ServerChunkCache chunkSource;
   boolean tickingEntities;
   private final MinecraftServer server;
   private final ServerLevelData serverLevelData;
   public boolean noSave;
   private boolean allPlayersSleeping;
   private int emptyTime;
   private final PortalForcer portalForcer;
   private final ServerTickList<Block> blockTicks;
   private final ServerTickList<Fluid> liquidTicks;
   private final Set<PathNavigation> navigations;
   protected final Raids raids;
   private final ObjectLinkedOpenHashSet<BlockEventData> blockEvents;
   private boolean handlingTick;
   private final List<CustomSpawner> customSpawners;
   @Nullable
   private final EndDragonFight dragonFight;
   private final StructureFeatureManager structureFeatureManager;
   private final boolean tickTime;

   public ServerLevel(MinecraftServer var1, Executor var2, LevelStorageSource.LevelStorageAccess var3, ServerLevelData var4, ResourceKey<Level> var5, DimensionType var6, ChunkProgressListener var7, ChunkGenerator var8, boolean var9, long var10, List<CustomSpawner> var12, boolean var13) {
      super(var4, var5, var6, var1::getProfiler, false, var9, var10);
      this.blockTicks = new ServerTickList(this, (var0) -> {
         return var0 == null || var0.defaultBlockState().isAir();
      }, Registry.BLOCK::getKey, this::tickBlock);
      this.liquidTicks = new ServerTickList(this, (var0) -> {
         return var0 == null || var0 == Fluids.EMPTY;
      }, Registry.FLUID::getKey, this::tickLiquid);
      this.navigations = Sets.newHashSet();
      this.blockEvents = new ObjectLinkedOpenHashSet();
      this.tickTime = var13;
      this.server = var1;
      this.customSpawners = var12;
      this.serverLevelData = var4;
      this.chunkSource = new ServerChunkCache(this, var3, var1.getFixerUpper(), var1.getStructureManager(), var2, var8, var1.getPlayerList().getViewDistance(), var1.forceSynchronousWrites(), var7, () -> {
         return var1.overworld().getDataStorage();
      });
      this.portalForcer = new PortalForcer(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.getWorldBorder().setAbsoluteMaxSize(var1.getAbsoluteMaxWorldSize());
      this.raids = (Raids)this.getDataStorage().computeIfAbsent(() -> {
         return new Raids(this);
      }, Raids.getFileId(this.dimensionType()));
      if (!var1.isSingleplayer()) {
         var4.setGameType(var1.getDefaultGameType());
      }

      this.structureFeatureManager = new StructureFeatureManager(this, var1.getWorldData().worldGenSettings());
      if (this.dimensionType().createDragonFight()) {
         this.dragonFight = new EndDragonFight(this, var1.getWorldData().worldGenSettings().seed(), var1.getWorldData().endDragonFightData());
      } else {
         this.dragonFight = null;
      }

   }

   public void setWeatherParameters(int var1, int var2, boolean var3, boolean var4) {
      this.serverLevelData.setClearWeatherTime(var1);
      this.serverLevelData.setRainTime(var2);
      this.serverLevelData.setThunderTime(var2);
      this.serverLevelData.setRaining(var3);
      this.serverLevelData.setThundering(var4);
   }

   public Biome getUncachedNoiseBiome(int var1, int var2, int var3) {
      return this.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(var1, var2, var3);
   }

   public StructureFeatureManager structureFeatureManager() {
      return this.structureFeatureManager;
   }

   public void tick(BooleanSupplier var1) {
      ProfilerFiller var2 = this.getProfiler();
      this.handlingTick = true;
      var2.push("world border");
      this.getWorldBorder().tick();
      var2.popPush("weather");
      boolean var3 = this.isRaining();
      if (this.dimensionType().hasSkyLight()) {
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            int var4 = this.serverLevelData.getClearWeatherTime();
            int var5 = this.serverLevelData.getThunderTime();
            int var6 = this.serverLevelData.getRainTime();
            boolean var7 = this.levelData.isThundering();
            boolean var8 = this.levelData.isRaining();
            if (var4 > 0) {
               --var4;
               var5 = var7 ? 0 : 1;
               var6 = var8 ? 0 : 1;
               var7 = false;
               var8 = false;
            } else {
               if (var5 > 0) {
                  --var5;
                  if (var5 == 0) {
                     var7 = !var7;
                  }
               } else if (var7) {
                  var5 = this.random.nextInt(12000) + 3600;
               } else {
                  var5 = this.random.nextInt(168000) + 12000;
               }

               if (var6 > 0) {
                  --var6;
                  if (var6 == 0) {
                     var8 = !var8;
                  }
               } else if (var8) {
                  var6 = this.random.nextInt(12000) + 12000;
               } else {
                  var6 = this.random.nextInt(168000) + 12000;
               }
            }

            this.serverLevelData.setThunderTime(var5);
            this.serverLevelData.setRainTime(var6);
            this.serverLevelData.setClearWeatherTime(var4);
            this.serverLevelData.setThundering(var7);
            this.serverLevelData.setRaining(var8);
         }

         this.oThunderLevel = this.thunderLevel;
         if (this.levelData.isThundering()) {
            this.thunderLevel = (float)((double)this.thunderLevel + 0.01D);
         } else {
            this.thunderLevel = (float)((double)this.thunderLevel - 0.01D);
         }

         this.thunderLevel = Mth.clamp(this.thunderLevel, 0.0F, 1.0F);
         this.oRainLevel = this.rainLevel;
         if (this.levelData.isRaining()) {
            this.rainLevel = (float)((double)this.rainLevel + 0.01D);
         } else {
            this.rainLevel = (float)((double)this.rainLevel - 0.01D);
         }

         this.rainLevel = Mth.clamp(this.rainLevel, 0.0F, 1.0F);
      }

      if (this.oRainLevel != this.rainLevel) {
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel), this.dimension());
      }

      if (this.oThunderLevel != this.thunderLevel) {
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel), this.dimension());
      }

      if (var3 != this.isRaining()) {
         if (var3) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.STOP_RAINING, 0.0F));
         } else {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.START_RAINING, 0.0F));
         }

         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.RAIN_LEVEL_CHANGE, this.rainLevel));
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(ClientboundGameEventPacket.THUNDER_LEVEL_CHANGE, this.thunderLevel));
      }

      if (this.allPlayersSleeping && this.players.stream().noneMatch((var0) -> {
         return !var0.isSpectator() && !var0.isSleepingLongEnough();
      })) {
         this.allPlayersSleeping = false;
         if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            long var9 = this.levelData.getDayTime() + 24000L;
            this.setDayTime(var9 - var9 % 24000L);
         }

         this.wakeUpAllPlayers();
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            this.stopWeather();
         }
      }

      this.updateSkyBrightness();
      this.tickTime();
      var2.popPush("chunkSource");
      this.getChunkSource().tick(var1);
      var2.popPush("tickPending");
      if (!this.isDebug()) {
         this.blockTicks.tick();
         this.liquidTicks.tick();
      }

      var2.popPush("raid");
      this.raids.tick();
      var2.popPush("blockEvents");
      this.runBlockEvents();
      this.handlingTick = false;
      var2.popPush("entities");
      boolean var10 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
      if (var10) {
         this.resetEmptyTime();
      }

      if (var10 || this.emptyTime++ < 300) {
         if (this.dragonFight != null) {
            this.dragonFight.tick();
         }

         this.tickingEntities = true;
         ObjectIterator var11 = this.entitiesById.int2ObjectEntrySet().iterator();

         label164:
         while(true) {
            Entity var13;
            while(true) {
               if (!var11.hasNext()) {
                  this.tickingEntities = false;

                  Entity var14;
                  while((var14 = (Entity)this.toAddAfterTick.poll()) != null) {
                     this.add(var14);
                  }

                  this.tickBlockEntities();
                  break label164;
               }

               Entry var12 = (Entry)var11.next();
               var13 = (Entity)var12.getValue();
               Entity var15 = var13.getVehicle();
               if (!this.server.isSpawningAnimals() && (var13 instanceof Animal || var13 instanceof WaterAnimal)) {
                  var13.remove();
               }

               if (!this.server.areNpcsEnabled() && var13 instanceof Npc) {
                  var13.remove();
               }

               var2.push("checkDespawn");
               if (!var13.removed) {
                  var13.checkDespawn();
               }

               var2.pop();
               if (var15 == null) {
                  break;
               }

               if (var15.removed || !var15.hasPassenger(var13)) {
                  var13.stopRiding();
                  break;
               }
            }

            var2.push("tick");
            if (!var13.removed && !(var13 instanceof EnderDragonPart)) {
               this.guardEntityTick(this::tickNonPassenger, var13);
            }

            var2.pop();
            var2.push("remove");
            if (var13.removed) {
               this.removeFromChunk(var13);
               var11.remove();
               this.onEntityRemoved(var13);
            }

            var2.pop();
         }
      }

      var2.pop();
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

   private void wakeUpAllPlayers() {
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
         var8 = this.findLightingTargetAround(this.getBlockRandomPos(var5, 0, var6, 15));
         if (this.isRainingAt(var8)) {
            DifficultyInstance var9 = this.getCurrentDifficultyAt(var8);
            boolean var10 = this.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING) && this.random.nextDouble() < (double)var9.getEffectiveDifficulty() * 0.01D;
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
         Biome var20 = this.getBiome(var8);
         if (var20.shouldFreeze(this, var18)) {
            this.setBlockAndUpdate(var18, Blocks.ICE.defaultBlockState());
         }

         if (var4 && var20.shouldSnow(this, var8)) {
            this.setBlockAndUpdate(var8, Blocks.SNOW.defaultBlockState());
         }

         if (var4 && this.getBiome(var18).getPrecipitation() == Biome.Precipitation.RAIN) {
            this.getBlockState(var18).getBlock().handleRain(this, var18);
         }
      }

      var7.popPush("tickBlocks");
      if (var2 > 0) {
         LevelChunkSection[] var17 = var1.getSections();
         int var19 = var17.length;

         for(int var21 = 0; var21 < var19; ++var21) {
            LevelChunkSection var23 = var17[var21];
            if (var23 != LevelChunk.EMPTY_SECTION && var23.isRandomlyTicking()) {
               int var12 = var23.bottomBlockY();

               for(int var13 = 0; var13 < var2; ++var13) {
                  BlockPos var14 = this.getBlockRandomPos(var5, var12, var6, 15);
                  var7.push("randomTick");
                  BlockState var15 = var23.getBlockState(var14.getX() - var5, var14.getY() - var12, var14.getZ() - var6);
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

   protected BlockPos findLightingTargetAround(BlockPos var1) {
      BlockPos var2 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var1);
      AABB var3 = (new AABB(var2, new BlockPos(var2.getX(), this.getMaxBuildHeight(), var2.getZ()))).inflate(3.0D);
      List var4 = this.getEntitiesOfClass(LivingEntity.class, var3, (var1x) -> {
         return var1x != null && var1x.isAlive() && this.canSeeSky(var1x.blockPosition());
      });
      if (!var4.isEmpty()) {
         return ((LivingEntity)var4.get(this.random.nextInt(var4.size()))).blockPosition();
      } else {
         if (var2.getY() == -1) {
            var2 = var2.above(2);
         }

         return var2;
      }
   }

   public boolean isHandlingTick() {
      return this.handlingTick;
   }

   public void updateSleepingPlayerList() {
      this.allPlayersSleeping = false;
      if (!this.players.isEmpty()) {
         int var1 = 0;
         int var2 = 0;
         Iterator var3 = this.players.iterator();

         while(var3.hasNext()) {
            ServerPlayer var4 = (ServerPlayer)var3.next();
            if (var4.isSpectator()) {
               ++var1;
            } else if (var4.isSleeping()) {
               ++var2;
            }
         }

         this.allPlayersSleeping = var2 > 0 && var2 >= this.players.size() - var1;
      }

   }

   public ServerScoreboard getScoreboard() {
      return this.server.getScoreboard();
   }

   private void stopWeather() {
      this.serverLevelData.setRainTime(0);
      this.serverLevelData.setRaining(false);
      this.serverLevelData.setThunderTime(0);
      this.serverLevelData.setThundering(false);
   }

   public void resetEmptyTime() {
      this.emptyTime = 0;
   }

   private void tickLiquid(TickNextTickData<Fluid> var1) {
      FluidState var2 = this.getFluidState(var1.pos);
      if (var2.getType() == var1.getType()) {
         var2.tick(this, var1.pos);
      }

   }

   private void tickBlock(TickNextTickData<Block> var1) {
      BlockState var2 = this.getBlockState(var1.pos);
      if (var2.is((Block)var1.getType())) {
         var2.tick(this, var1.pos, this.random);
      }

   }

   public void tickNonPassenger(Entity var1) {
      if (!(var1 instanceof Player) && !this.getChunkSource().isEntityTickingChunk(var1)) {
         this.updateChunkPos(var1);
      } else {
         var1.setPosAndOldPos(var1.getX(), var1.getY(), var1.getZ());
         var1.yRotO = var1.yRot;
         var1.xRotO = var1.xRot;
         if (var1.inChunk) {
            ++var1.tickCount;
            ProfilerFiller var2 = this.getProfiler();
            var2.push(() -> {
               return Registry.ENTITY_TYPE.getKey(var1.getType()).toString();
            });
            var2.incrementCounter("tickNonPassenger");
            var1.tick();
            var2.pop();
         }

         this.updateChunkPos(var1);
         if (var1.inChunk) {
            Iterator var4 = var1.getPassengers().iterator();

            while(var4.hasNext()) {
               Entity var3 = (Entity)var4.next();
               this.tickPassenger(var1, var3);
            }
         }

      }
   }

   public void tickPassenger(Entity var1, Entity var2) {
      if (!var2.removed && var2.getVehicle() == var1) {
         if (var2 instanceof Player || this.getChunkSource().isEntityTickingChunk(var2)) {
            var2.setPosAndOldPos(var2.getX(), var2.getY(), var2.getZ());
            var2.yRotO = var2.yRot;
            var2.xRotO = var2.xRot;
            if (var2.inChunk) {
               ++var2.tickCount;
               ProfilerFiller var3 = this.getProfiler();
               var3.push(() -> {
                  return Registry.ENTITY_TYPE.getKey(var2.getType()).toString();
               });
               var3.incrementCounter("tickPassenger");
               var2.rideTick();
               var3.pop();
            }

            this.updateChunkPos(var2);
            if (var2.inChunk) {
               Iterator var5 = var2.getPassengers().iterator();

               while(var5.hasNext()) {
                  Entity var4 = (Entity)var5.next();
                  this.tickPassenger(var2, var4);
               }
            }

         }
      } else {
         var2.stopRiding();
      }
   }

   public void updateChunkPos(Entity var1) {
      if (var1.checkAndResetUpdateChunkPos()) {
         this.getProfiler().push("chunkCheck");
         int var2 = Mth.floor(var1.getX() / 16.0D);
         int var3 = Mth.floor(var1.getY() / 16.0D);
         int var4 = Mth.floor(var1.getZ() / 16.0D);
         if (!var1.inChunk || var1.xChunk != var2 || var1.yChunk != var3 || var1.zChunk != var4) {
            if (var1.inChunk && this.hasChunk(var1.xChunk, var1.zChunk)) {
               this.getChunk(var1.xChunk, var1.zChunk).removeEntity(var1, var1.yChunk);
            }

            if (!var1.checkAndResetForcedChunkAdditionFlag() && !this.hasChunk(var2, var4)) {
               if (var1.inChunk) {
                  LOGGER.warn("Entity {} left loaded chunk area", var1);
               }

               var1.inChunk = false;
            } else {
               this.getChunk(var2, var4).addEntity(var1);
            }
         }

         this.getProfiler().pop();
      }
   }

   public boolean mayInteract(Player var1, BlockPos var2) {
      return !this.server.isUnderSpawnProtection(this, var2, var1) && this.getWorldBorder().isWithinBounds(var2);
   }

   public void save(@Nullable ProgressListener var1, boolean var2, boolean var3) {
      ServerChunkCache var4 = this.getChunkSource();
      if (!var3) {
         if (var1 != null) {
            var1.progressStartNoAbort(new TranslatableComponent("menu.savingLevel"));
         }

         this.saveLevelData();
         if (var1 != null) {
            var1.progressStage(new TranslatableComponent("menu.savingChunks"));
         }

         var4.save(var2);
      }
   }

   private void saveLevelData() {
      if (this.dragonFight != null) {
         this.server.getWorldData().setEndDragonFightData(this.dragonFight.saveData());
      }

      this.getChunkSource().getDataStorage().save();
   }

   public List<Entity> getEntities(@Nullable EntityType<?> var1, Predicate<? super Entity> var2) {
      ArrayList var3 = Lists.newArrayList();
      ServerChunkCache var4 = this.getChunkSource();
      ObjectIterator var5 = this.entitiesById.values().iterator();

      while(true) {
         Entity var6;
         do {
            if (!var5.hasNext()) {
               return var3;
            }

            var6 = (Entity)var5.next();
         } while(var1 != null && var6.getType() != var1);

         if (var4.hasChunk(Mth.floor(var6.getX()) >> 4, Mth.floor(var6.getZ()) >> 4) && var2.test(var6)) {
            var3.add(var6);
         }
      }
   }

   public List<EnderDragon> getDragons() {
      ArrayList var1 = Lists.newArrayList();
      ObjectIterator var2 = this.entitiesById.values().iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         if (var3 instanceof EnderDragon && var3.isAlive()) {
            var1.add((EnderDragon)var3);
         }
      }

      return var1;
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

   public void addFromAnotherDimension(Entity var1) {
      boolean var2 = var1.forcedLoading;
      var1.forcedLoading = true;
      this.addWithUUID(var1);
      var1.forcedLoading = var2;
      this.updateChunkPos(var1);
   }

   public void addDuringCommandTeleport(ServerPlayer var1) {
      this.addPlayer(var1);
      this.updateChunkPos(var1);
   }

   public void addDuringPortalTeleport(ServerPlayer var1) {
      this.addPlayer(var1);
      this.updateChunkPos(var1);
   }

   public void addNewPlayer(ServerPlayer var1) {
      this.addPlayer(var1);
   }

   public void addRespawnedPlayer(ServerPlayer var1) {
      this.addPlayer(var1);
   }

   private void addPlayer(ServerPlayer var1) {
      Entity var2 = (Entity)this.entitiesByUuid.get(var1.getUUID());
      if (var2 != null) {
         LOGGER.warn("Force-added player with duplicate UUID {}", var1.getUUID().toString());
         var2.unRide();
         this.removePlayerImmediately((ServerPlayer)var2);
      }

      this.players.add(var1);
      this.updateSleepingPlayerList();
      ChunkAccess var3 = this.getChunk(Mth.floor(var1.getX() / 16.0D), Mth.floor(var1.getZ() / 16.0D), ChunkStatus.FULL, true);
      if (var3 instanceof LevelChunk) {
         var3.addEntity(var1);
      }

      this.add(var1);
   }

   private boolean addEntity(Entity var1) {
      if (var1.removed) {
         LOGGER.warn("Tried to add entity {} but it was marked as removed already", EntityType.getKey(var1.getType()));
         return false;
      } else if (this.isUUIDUsed(var1)) {
         return false;
      } else {
         ChunkAccess var2 = this.getChunk(Mth.floor(var1.getX() / 16.0D), Mth.floor(var1.getZ() / 16.0D), ChunkStatus.FULL, var1.forcedLoading);
         if (!(var2 instanceof LevelChunk)) {
            return false;
         } else {
            var2.addEntity(var1);
            this.add(var1);
            return true;
         }
      }
   }

   public boolean loadFromChunk(Entity var1) {
      if (this.isUUIDUsed(var1)) {
         return false;
      } else {
         this.add(var1);
         return true;
      }
   }

   private boolean isUUIDUsed(Entity var1) {
      UUID var2 = var1.getUUID();
      Entity var3 = this.findAddedOrPendingEntity(var2);
      if (var3 == null) {
         return false;
      } else {
         LOGGER.warn("Trying to add entity with duplicated UUID {}. Existing {}#{}, new: {}#{}", var2, EntityType.getKey(var3.getType()), var3.getId(), EntityType.getKey(var1.getType()), var1.getId());
         return true;
      }
   }

   @Nullable
   private Entity findAddedOrPendingEntity(UUID var1) {
      Entity var2 = (Entity)this.entitiesByUuid.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         if (this.tickingEntities) {
            Iterator var3 = this.toAddAfterTick.iterator();

            while(var3.hasNext()) {
               Entity var4 = (Entity)var3.next();
               if (var4.getUUID().equals(var1)) {
                  return var4;
               }
            }
         }

         return null;
      }
   }

   public boolean tryAddFreshEntityWithPassengers(Entity var1) {
      if (var1.getSelfAndPassengers().anyMatch(this::isUUIDUsed)) {
         return false;
      } else {
         this.addFreshEntityWithPassengers(var1);
         return true;
      }
   }

   public void unload(LevelChunk var1) {
      this.blockEntitiesToUnload.addAll(var1.getBlockEntities().values());
      ClassInstanceMultiMap[] var2 = var1.getEntitySections();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ClassInstanceMultiMap var5 = var2[var4];
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            Entity var7 = (Entity)var6.next();
            if (!(var7 instanceof ServerPlayer)) {
               if (this.tickingEntities) {
                  throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Removing entity while ticking!"));
               }

               this.entitiesById.remove(var7.getId());
               this.onEntityRemoved(var7);
            }
         }
      }

   }

   public void onEntityRemoved(Entity var1) {
      if (var1 instanceof EnderDragon) {
         EnderDragonPart[] var2 = ((EnderDragon)var1).getSubEntities();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            EnderDragonPart var5 = var2[var4];
            var5.remove();
         }
      }

      this.entitiesByUuid.remove(var1.getUUID());
      this.getChunkSource().removeEntity(var1);
      if (var1 instanceof ServerPlayer) {
         ServerPlayer var6 = (ServerPlayer)var1;
         this.players.remove(var6);
      }

      this.getScoreboard().entityRemoved(var1);
      if (var1 instanceof Mob) {
         this.navigations.remove(((Mob)var1).getNavigation());
      }

   }

   private void add(Entity var1) {
      if (this.tickingEntities) {
         this.toAddAfterTick.add(var1);
      } else {
         this.entitiesById.put(var1.getId(), var1);
         if (var1 instanceof EnderDragon) {
            EnderDragonPart[] var2 = ((EnderDragon)var1).getSubEntities();
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               EnderDragonPart var5 = var2[var4];
               this.entitiesById.put(var5.getId(), var5);
            }
         }

         this.entitiesByUuid.put(var1.getUUID(), var1);
         this.getChunkSource().addEntity(var1);
         if (var1 instanceof Mob) {
            this.navigations.add(((Mob)var1).getNavigation());
         }
      }

   }

   public void despawn(Entity var1) {
      if (this.tickingEntities) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Removing entity while ticking!"));
      } else {
         this.removeFromChunk(var1);
         this.entitiesById.remove(var1.getId());
         this.onEntityRemoved(var1);
      }
   }

   private void removeFromChunk(Entity var1) {
      ChunkAccess var2 = this.getChunk(var1.xChunk, var1.zChunk, ChunkStatus.FULL, false);
      if (var2 instanceof LevelChunk) {
         ((LevelChunk)var2).removeEntity(var1);
      }

   }

   public void removePlayerImmediately(ServerPlayer var1) {
      var1.remove();
      this.despawn(var1);
      this.updateSleepingPlayerList();
   }

   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      Iterator var4 = this.server.getPlayerList().getPlayers().iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
         if (var5 != null && var5.level == this && var5.getId() != var1) {
            double var6 = (double)var2.getX() - var5.getX();
            double var8 = (double)var2.getY() - var5.getY();
            double var10 = (double)var2.getZ() - var5.getZ();
            if (var6 * var6 + var8 * var8 + var10 * var10 < 1024.0D) {
               var5.connection.send(new ClientboundBlockDestructionPacket(var1, var2, var3));
            }
         }
      }

   }

   public void playSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9, float var10, float var11) {
      this.server.getPlayerList().broadcast(var1, var2, var4, var6, var10 > 1.0F ? (double)(16.0F * var10) : 16.0D, this.dimension(), new ClientboundSoundPacket(var8, var9, var2, var4, var6, var10, var11));
   }

   public void playSound(@Nullable Player var1, Entity var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
      this.server.getPlayerList().broadcast(var1, var2.getX(), var2.getY(), var2.getZ(), var5 > 1.0F ? (double)(16.0F * var5) : 16.0D, this.dimension(), new ClientboundSoundEntityPacket(var3, var4, var2, var5, var6));
   }

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket(var1, var2, var3, true));
   }

   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
      this.server.getPlayerList().broadcast(var1, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 64.0D, this.dimension(), new ClientboundLevelEventPacket(var2, var3, var4, false));
   }

   public void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4) {
      this.getChunkSource().blockChanged(var1);
      VoxelShape var5 = var2.getCollisionShape(this, var1);
      VoxelShape var6 = var3.getCollisionShape(this, var1);
      if (Shapes.joinIsNotEmpty(var5, var6, BooleanOp.NOT_SAME)) {
         Iterator var7 = this.navigations.iterator();

         while(var7.hasNext()) {
            PathNavigation var8 = (PathNavigation)var7.next();
            if (!var8.hasDelayedRecomputation()) {
               var8.recomputePath(var1);
            }
         }

      }
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
         if (var15.distanceToSqr(var4, var6, var8) < 4096.0D) {
            var15.connection.send(new ClientboundExplodePacket(var4, var6, var8, var10, var13.getToBlow(), (Vec3)var13.getHitPlayers().get(var15)));
         }
      }

      return var13;
   }

   public void blockEvent(BlockPos var1, Block var2, int var3, int var4) {
      this.blockEvents.add(new BlockEventData(var1, var2, var3, var4));
   }

   private void runBlockEvents() {
      while(!this.blockEvents.isEmpty()) {
         BlockEventData var1 = (BlockEventData)this.blockEvents.removeFirst();
         if (this.doBlockEvent(var1)) {
            this.server.getPlayerList().broadcast((Player)null, (double)var1.getPos().getX(), (double)var1.getPos().getY(), (double)var1.getPos().getZ(), 64.0D, this.dimension(), new ClientboundBlockEventPacket(var1.getPos(), var1.getBlock(), var1.getParamA(), var1.getParamB()));
         }
      }

   }

   private boolean doBlockEvent(BlockEventData var1) {
      BlockState var2 = this.getBlockState(var1.getPos());
      return var2.is(var1.getBlock()) ? var2.triggerEvent(this, var1.getPos(), var1.getParamA(), var1.getParamB()) : false;
   }

   public ServerTickList<Block> getBlockTicks() {
      return this.blockTicks;
   }

   public ServerTickList<Fluid> getLiquidTicks() {
      return this.liquidTicks;
   }

   @Nonnull
   public MinecraftServer getServer() {
      return this.server;
   }

   public PortalForcer getPortalForcer() {
      return this.portalForcer;
   }

   public StructureManager getStructureManager() {
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
         if (var10.closerThan(new Vec3(var3, var5, var7), var2 ? 512.0D : 32.0D)) {
            var1.connection.send(var9);
            return true;
         } else {
            return false;
         }
      }
   }

   @Nullable
   public Entity getEntity(int var1) {
      return (Entity)this.entitiesById.get(var1);
   }

   @Nullable
   public Entity getEntity(UUID var1) {
      return (Entity)this.entitiesByUuid.get(var1);
   }

   @Nullable
   public BlockPos findNearestMapFeature(StructureFeature<?> var1, BlockPos var2, int var3, boolean var4) {
      return !this.server.getWorldData().worldGenSettings().generateFeatures() ? null : this.getChunkSource().getGenerator().findNearestMapFeature(this, var1, var2, var3, var4);
   }

   @Nullable
   public BlockPos findNearestBiome(Biome var1, BlockPos var2, int var3, int var4) {
      return this.getChunkSource().getGenerator().getBiomeSource().findBiomeHorizontal(var2.getX(), var2.getY(), var2.getZ(), var3, var4, (var1x) -> {
         return var1x == var1;
      }, this.random, true);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public TagContainer getTagManager() {
      return this.server.getTags();
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
      return (MapItemSavedData)this.getServer().overworld().getDataStorage().get(() -> {
         return new MapItemSavedData(var1);
      }, var1);
   }

   public void setMapData(MapItemSavedData var1) {
      this.getServer().overworld().getDataStorage().set(var1);
   }

   public int getFreeMapId() {
      return ((MapIndex)this.getServer().overworld().getDataStorage().computeIfAbsent(MapIndex::new, "idcounts")).getFreeAuxValueForMap();
   }

   public void setDefaultSpawnPos(BlockPos var1, float var2) {
      ChunkPos var3 = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
      this.levelData.setSpawn(var1, var2);
      this.getChunkSource().removeRegionTicket(TicketType.START, var3, 11, Unit.INSTANCE);
      this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(var1), 11, Unit.INSTANCE);
      this.getServer().getPlayerList().broadcastAll(new ClientboundSetDefaultSpawnPositionPacket(var1, var2));
   }

   public BlockPos getSharedSpawnPos() {
      BlockPos var1 = new BlockPos(this.levelData.getXSpawn(), this.levelData.getYSpawn(), this.levelData.getZSpawn());
      if (!this.getWorldBorder().isWithinBounds(var1)) {
         var1 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, new BlockPos(this.getWorldBorder().getCenterX(), 0.0D, this.getWorldBorder().getCenterZ()));
      }

      return var1;
   }

   public float getSharedSpawnAngle() {
      return this.levelData.getSpawnAngle();
   }

   public LongSet getForcedChunks() {
      ForcedChunksSavedData var1 = (ForcedChunksSavedData)this.getDataStorage().get(ForcedChunksSavedData::new, "chunks");
      return (LongSet)(var1 != null ? LongSets.unmodifiable(var1.getChunks()) : LongSets.EMPTY_SET);
   }

   public boolean setChunkForced(int var1, int var2, boolean var3) {
      ForcedChunksSavedData var4 = (ForcedChunksSavedData)this.getDataStorage().computeIfAbsent(ForcedChunksSavedData::new, "chunks");
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
      Optional var4 = PoiType.forState(var2);
      Optional var5 = PoiType.forState(var3);
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
      Throwable var4 = null;

      try {
         var3.write(String.format("spawning_chunks: %d\n", var2.getDistanceManager().getNaturalSpawnChunkCount()));
         NaturalSpawner.SpawnState var5 = this.getChunkSource().getLastSpawnState();
         if (var5 != null) {
            ObjectIterator var6 = var5.getMobCategoryCounts().object2IntEntrySet().iterator();

            while(var6.hasNext()) {
               it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var7 = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var6.next();
               var3.write(String.format("spawn_count.%s: %d\n", ((MobCategory)var7.getKey()).getName(), var7.getIntValue()));
            }
         }

         var3.write(String.format("entities: %d\n", this.entitiesById.size()));
         var3.write(String.format("block_entities: %d\n", this.blockEntityList.size()));
         var3.write(String.format("block_ticks: %d\n", this.getBlockTicks().size()));
         var3.write(String.format("fluid_ticks: %d\n", this.getLiquidTicks().size()));
         var3.write("distance_manager: " + var2.getDistanceManager().getDebugStatus() + "\n");
         var3.write(String.format("pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
      } catch (Throwable var121) {
         var4 = var121;
         throw var121;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var112) {
                  var4.addSuppressed(var112);
               }
            } else {
               var3.close();
            }
         }

      }

      CrashReport var123 = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails(var123);
      BufferedWriter var124 = Files.newBufferedWriter(var1.resolve("example_crash.txt"));
      Throwable var125 = null;

      try {
         var124.write(var123.getFriendlyReport());
      } catch (Throwable var116) {
         var125 = var116;
         throw var116;
      } finally {
         if (var124 != null) {
            if (var125 != null) {
               try {
                  var124.close();
               } catch (Throwable var111) {
                  var125.addSuppressed(var111);
               }
            } else {
               var124.close();
            }
         }

      }

      Path var126 = var1.resolve("chunks.csv");
      BufferedWriter var127 = Files.newBufferedWriter(var126);
      Throwable var128 = null;

      try {
         var2.dumpChunks(var127);
      } catch (Throwable var115) {
         var128 = var115;
         throw var115;
      } finally {
         if (var127 != null) {
            if (var128 != null) {
               try {
                  var127.close();
               } catch (Throwable var109) {
                  var128.addSuppressed(var109);
               }
            } else {
               var127.close();
            }
         }

      }

      Path var129 = var1.resolve("entities.csv");
      BufferedWriter var130 = Files.newBufferedWriter(var129);
      Throwable var131 = null;

      try {
         dumpEntities(var130, this.entitiesById.values());
      } catch (Throwable var114) {
         var131 = var114;
         throw var114;
      } finally {
         if (var130 != null) {
            if (var131 != null) {
               try {
                  var130.close();
               } catch (Throwable var108) {
                  var131.addSuppressed(var108);
               }
            } else {
               var130.close();
            }
         }

      }

      Path var132 = var1.resolve("block_entities.csv");
      BufferedWriter var133 = Files.newBufferedWriter(var132);
      Throwable var8 = null;

      try {
         this.dumpBlockEntities(var133);
      } catch (Throwable var113) {
         var8 = var113;
         throw var113;
      } finally {
         if (var133 != null) {
            if (var8 != null) {
               try {
                  var133.close();
               } catch (Throwable var110) {
                  var8.addSuppressed(var110);
               }
            } else {
               var133.close();
            }
         }

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

   private void dumpBlockEntities(Writer var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder().addColumn("x").addColumn("y").addColumn("z").addColumn("type").build(var1);
      Iterator var3 = this.blockEntityList.iterator();

      while(var3.hasNext()) {
         BlockEntity var4 = (BlockEntity)var3.next();
         BlockPos var5 = var4.getBlockPos();
         var2.writeRow(var5.getX(), var5.getY(), var5.getZ(), Registry.BLOCK_ENTITY_TYPE.getKey(var4.getType()));
      }

   }

   @VisibleForTesting
   public void clearBlockEvents(BoundingBox var1) {
      this.blockEvents.removeIf((var1x) -> {
         return var1.isInside(var1x.getPos());
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
      return Iterables.unmodifiableIterable(this.entitiesById.values());
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

   public Stream<? extends StructureStart<?>> startsForFeature(SectionPos var1, StructureFeature<?> var2) {
      return this.structureFeatureManager().startsForFeature(var1, var2);
   }

   public ServerLevel getLevel() {
      return this;
   }

   @VisibleForTesting
   public String getWatchdogStats() {
      return String.format("players: %s, entities: %d [%s], block_entities: %d [%s], block_ticks: %d, fluid_ticks: %d, chunk_source: %s", this.players.size(), this.entitiesById.size(), getTypeCount(this.entitiesById.values(), (var0) -> {
         return Registry.ENTITY_TYPE.getKey(var0.getType());
      }), this.tickableBlockEntities.size(), getTypeCount(this.tickableBlockEntities, (var0) -> {
         return Registry.BLOCK_ENTITY_TYPE.getKey(var0.getType());
      }), this.getBlockTicks().size(), this.getLiquidTicks().size(), this.gatherChunkSourceStats());
   }

   private static <T> String getTypeCount(Collection<T> var0, Function<T, ResourceLocation> var1) {
      try {
         Object2IntOpenHashMap var2 = new Object2IntOpenHashMap();
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            ResourceLocation var5 = (ResourceLocation)var1.apply(var4);
            var2.addTo(var5, 1);
         }

         return (String)var2.object2IntEntrySet().stream().sorted(Comparator.comparing(it.unimi.dsi.fastutil.objects.Object2IntMap.Entry::getIntValue).reversed()).limit(5L).map((var0x) -> {
            return var0x.getKey() + ":" + var0x.getIntValue();
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

   // $FF: synthetic method
   public Scoreboard getScoreboard() {
      return this.getScoreboard();
   }

   // $FF: synthetic method
   public ChunkSource getChunkSource() {
      return this.getChunkSource();
   }

   // $FF: synthetic method
   public TickList getLiquidTicks() {
      return this.getLiquidTicks();
   }

   // $FF: synthetic method
   public TickList getBlockTicks() {
      return this.getBlockTicks();
   }
}
