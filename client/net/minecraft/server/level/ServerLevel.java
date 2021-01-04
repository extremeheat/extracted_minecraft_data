package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddGlobalEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket;
import net.minecraft.network.protocol.game.ClientboundEntityEventPacket;
import net.minecraft.network.protocol.game.ClientboundExplodePacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagManager;
import net.minecraft.util.ClassInstanceMultiMap;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.Mth;
import net.minecraft.util.ProgressListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.entity.npc.WanderingTraderSpawner;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.BlockEventData;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ForcedChunksSavedData;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelConflictException;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.PortalForcer;
import net.minecraft.world.level.ServerTickList;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.TickNextTickData;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.BonusChestFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.maps.MapIndex;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerLevel extends Level {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<Entity> globalEntities = Lists.newArrayList();
   private final Int2ObjectMap<Entity> entitiesById = new Int2ObjectLinkedOpenHashMap();
   private final Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   private final Queue<Entity> toAddAfterTick = Queues.newArrayDeque();
   private final List<ServerPlayer> players = Lists.newArrayList();
   boolean tickingEntities;
   private final MinecraftServer server;
   private final LevelStorage levelStorage;
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
   @Nullable
   private final WanderingTraderSpawner wanderingTraderSpawner;

   public ServerLevel(MinecraftServer var1, Executor var2, LevelStorage var3, LevelData var4, DimensionType var5, ProfilerFiller var6, ChunkProgressListener var7) {
      super(var4, var5, (var4x, var5x) -> {
         return new ServerChunkCache((ServerLevel)var4x, var3.getFolder(), var3.getFixerUpper(), var3.getStructureManager(), var2, var5x.createRandomLevelGenerator(), var1.getPlayerList().getViewDistance(), var7, () -> {
            return var1.getLevel(DimensionType.OVERWORLD).getDataStorage();
         });
      }, var6, false);
      this.blockTicks = new ServerTickList(this, (var0) -> {
         return var0 == null || var0.defaultBlockState().isAir();
      }, Registry.BLOCK::getKey, Registry.BLOCK::get, this::tickBlock);
      this.liquidTicks = new ServerTickList(this, (var0) -> {
         return var0 == null || var0 == Fluids.EMPTY;
      }, Registry.FLUID::getKey, Registry.FLUID::get, this::tickLiquid);
      this.navigations = Sets.newHashSet();
      this.blockEvents = new ObjectLinkedOpenHashSet();
      this.levelStorage = var3;
      this.server = var1;
      this.portalForcer = new PortalForcer(this);
      this.updateSkyBrightness();
      this.prepareWeather();
      this.getWorldBorder().setAbsoluteMaxSize(var1.getAbsoluteMaxWorldSize());
      this.raids = (Raids)this.getDataStorage().computeIfAbsent(() -> {
         return new Raids(this);
      }, Raids.getFileId(this.dimension));
      if (!var1.isSingleplayer()) {
         this.getLevelData().setGameType(var1.getDefaultGameType());
      }

      this.wanderingTraderSpawner = this.dimension.getType() == DimensionType.OVERWORLD ? new WanderingTraderSpawner(this) : null;
   }

   public void tick(BooleanSupplier var1) {
      ProfilerFiller var2 = this.getProfiler();
      this.handlingTick = true;
      var2.push("world border");
      this.getWorldBorder().tick();
      var2.popPush("weather");
      boolean var3 = this.isRaining();
      int var5;
      if (this.dimension.isHasSkyLight()) {
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            int var4 = this.levelData.getClearWeatherTime();
            var5 = this.levelData.getThunderTime();
            int var6 = this.levelData.getRainTime();
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

            this.levelData.setThunderTime(var5);
            this.levelData.setRainTime(var6);
            this.levelData.setClearWeatherTime(var4);
            this.levelData.setThundering(var7);
            this.levelData.setRaining(var8);
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
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(7, this.rainLevel), this.dimension.getType());
      }

      if (this.oThunderLevel != this.thunderLevel) {
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(8, this.thunderLevel), this.dimension.getType());
      }

      if (var3 != this.isRaining()) {
         if (var3) {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(2, 0.0F));
         } else {
            this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(1, 0.0F));
         }

         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(7, this.rainLevel));
         this.server.getPlayerList().broadcastAll(new ClientboundGameEventPacket(8, this.thunderLevel));
      }

      if (this.getLevelData().isHardcore() && this.getDifficulty() != Difficulty.HARD) {
         this.getLevelData().setDifficulty(Difficulty.HARD);
      }

      if (this.allPlayersSleeping && this.players.stream().noneMatch((var0) -> {
         return !var0.isSpectator() && !var0.isSleepingLongEnough();
      })) {
         this.allPlayersSleeping = false;
         if (this.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
            long var9 = this.levelData.getDayTime() + 24000L;
            this.setDayTime(var9 - var9 % 24000L);
         }

         this.players.stream().filter(LivingEntity::isSleeping).forEach((var0) -> {
            var0.stopSleepInBed(false, false, true);
         });
         if (this.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
            this.stopWeather();
         }
      }

      this.updateSkyBrightness();
      this.tickTime();
      var2.popPush("chunkSource");
      this.getChunkSource().tick(var1);
      var2.popPush("tickPending");
      if (this.levelData.getGeneratorType() != LevelType.DEBUG_ALL_BLOCK_STATES) {
         this.blockTicks.tick();
         this.liquidTicks.tick();
      }

      var2.popPush("portalForcer");
      this.portalForcer.tick(this.getGameTime());
      var2.popPush("raid");
      this.raids.tick();
      if (this.wanderingTraderSpawner != null) {
         this.wanderingTraderSpawner.tick();
      }

      var2.popPush("blockEvents");
      this.runBlockEvents();
      this.handlingTick = false;
      var2.popPush("entities");
      boolean var10 = !this.players.isEmpty() || !this.getForcedChunks().isEmpty();
      if (var10) {
         this.resetEmptyTime();
      }

      if (var10 || this.emptyTime++ < 300) {
         this.dimension.tick();
         var2.push("global");

         Entity var11;
         for(var5 = 0; var5 < this.globalEntities.size(); ++var5) {
            var11 = (Entity)this.globalEntities.get(var5);
            this.guardEntityTick((var0) -> {
               ++var0.tickCount;
               var0.tick();
            }, var11);
            if (var11.removed) {
               this.globalEntities.remove(var5--);
            }
         }

         var2.popPush("regular");
         this.tickingEntities = true;
         ObjectIterator var12 = this.entitiesById.int2ObjectEntrySet().iterator();

         label168:
         while(true) {
            Entity var14;
            while(true) {
               if (!var12.hasNext()) {
                  this.tickingEntities = false;

                  while((var11 = (Entity)this.toAddAfterTick.poll()) != null) {
                     this.add(var11);
                  }

                  var2.pop();
                  this.tickBlockEntities();
                  break label168;
               }

               Entry var13 = (Entry)var12.next();
               var14 = (Entity)var13.getValue();
               Entity var15 = var14.getVehicle();
               if (!this.server.isAnimals() && (var14 instanceof Animal || var14 instanceof WaterAnimal)) {
                  var14.remove();
               }

               if (!this.server.isNpcsEnabled() && var14 instanceof Npc) {
                  var14.remove();
               }

               if (var15 == null) {
                  break;
               }

               if (var15.removed || !var15.hasPassenger(var14)) {
                  var14.stopRiding();
                  break;
               }
            }

            var2.push("tick");
            if (!var14.removed && !(var14 instanceof EnderDragonPart)) {
               this.guardEntityTick(this::tickNonPassenger, var14);
            }

            var2.pop();
            var2.push("remove");
            if (var14.removed) {
               this.removeFromChunk(var14);
               var12.remove();
               this.onEntityRemoved(var14);
            }

            var2.pop();
         }
      }

      var2.pop();
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

            this.addGlobalEntity(new LightningBolt(this, (double)var8.getX() + 0.5D, (double)var8.getY(), (double)var8.getZ() + 0.5D, var10));
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
            LevelChunkSection var22 = var17[var21];
            if (var22 != LevelChunk.EMPTY_SECTION && var22.isRandomlyTicking()) {
               int var12 = var22.bottomBlockY();

               for(int var13 = 0; var13 < var2; ++var13) {
                  BlockPos var14 = this.getBlockRandomPos(var5, var12, var6, 15);
                  var7.push("randomTick");
                  BlockState var15 = var22.getBlockState(var14.getX() - var5, var14.getY() - var12, var14.getZ() - var6);
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
         return var1x != null && var1x.isAlive() && this.canSeeSky(var1x.getCommandSenderBlockPosition());
      });
      if (!var4.isEmpty()) {
         return ((LivingEntity)var4.get(this.random.nextInt(var4.size()))).getCommandSenderBlockPosition();
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
      this.levelData.setRainTime(0);
      this.levelData.setRaining(false);
      this.levelData.setThunderTime(0);
      this.levelData.setThundering(false);
   }

   public void validateSpawn() {
      if (this.levelData.getYSpawn() <= 0) {
         this.levelData.setYSpawn(this.getSeaLevel() + 1);
      }

      int var1 = this.levelData.getXSpawn();
      int var2 = this.levelData.getZSpawn();
      int var3 = 0;

      while(this.getTopBlockState(new BlockPos(var1, 0, var2)).isAir()) {
         var1 += this.random.nextInt(8) - this.random.nextInt(8);
         var2 += this.random.nextInt(8) - this.random.nextInt(8);
         ++var3;
         if (var3 == 10000) {
            break;
         }
      }

      this.levelData.setXSpawn(var1);
      this.levelData.setZSpawn(var2);
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
      if (var2.getBlock() == var1.getType()) {
         var2.tick(this, var1.pos, this.random);
      }

   }

   public void tickNonPassenger(Entity var1) {
      if (var1 instanceof Player || this.getChunkSource().isEntityTickingChunk(var1)) {
         var1.xOld = var1.x;
         var1.yOld = var1.y;
         var1.zOld = var1.z;
         var1.yRotO = var1.yRot;
         var1.xRotO = var1.xRot;
         if (var1.inChunk) {
            ++var1.tickCount;
            this.getProfiler().push(() -> {
               return Registry.ENTITY_TYPE.getKey(var1.getType()).toString();
            });
            var1.tick();
            this.getProfiler().pop();
         }

         this.updateChunkPos(var1);
         if (var1.inChunk) {
            Iterator var2 = var1.getPassengers().iterator();

            while(var2.hasNext()) {
               Entity var3 = (Entity)var2.next();
               this.tickPassenger(var1, var3);
            }
         }

      }
   }

   public void tickPassenger(Entity var1, Entity var2) {
      if (!var2.removed && var2.getVehicle() == var1) {
         if (var2 instanceof Player || this.getChunkSource().isEntityTickingChunk(var2)) {
            var2.xOld = var2.x;
            var2.yOld = var2.y;
            var2.zOld = var2.z;
            var2.yRotO = var2.yRot;
            var2.xRotO = var2.xRot;
            if (var2.inChunk) {
               ++var2.tickCount;
               var2.rideTick();
            }

            this.updateChunkPos(var2);
            if (var2.inChunk) {
               Iterator var3 = var2.getPassengers().iterator();

               while(var3.hasNext()) {
                  Entity var4 = (Entity)var3.next();
                  this.tickPassenger(var2, var4);
               }
            }

         }
      } else {
         var2.stopRiding();
      }
   }

   public void updateChunkPos(Entity var1) {
      this.getProfiler().push("chunkCheck");
      int var2 = Mth.floor(var1.x / 16.0D);
      int var3 = Mth.floor(var1.y / 16.0D);
      int var4 = Mth.floor(var1.z / 16.0D);
      if (!var1.inChunk || var1.xChunk != var2 || var1.yChunk != var3 || var1.zChunk != var4) {
         if (var1.inChunk && this.hasChunk(var1.xChunk, var1.zChunk)) {
            this.getChunk(var1.xChunk, var1.zChunk).removeEntity(var1, var1.yChunk);
         }

         if (!var1.checkAndResetTeleportedFlag() && !this.hasChunk(var2, var4)) {
            var1.inChunk = false;
         } else {
            this.getChunk(var2, var4).addEntity(var1);
         }
      }

      this.getProfiler().pop();
   }

   public boolean mayInteract(Player var1, BlockPos var2) {
      return !this.server.isUnderSpawnProtection(this, var2, var1) && this.getWorldBorder().isWithinBounds(var2);
   }

   public void setInitialSpawn(LevelSettings var1) {
      if (!this.dimension.mayRespawn()) {
         this.levelData.setSpawn(BlockPos.ZERO.above(this.chunkSource.getGenerator().getSpawnHeight()));
      } else if (this.levelData.getGeneratorType() == LevelType.DEBUG_ALL_BLOCK_STATES) {
         this.levelData.setSpawn(BlockPos.ZERO.above());
      } else {
         BiomeSource var2 = this.chunkSource.getGenerator().getBiomeSource();
         List var3 = var2.getPlayerSpawnBiomes();
         Random var4 = new Random(this.getSeed());
         BlockPos var5 = var2.findBiome(0, 0, 256, var3, var4);
         ChunkPos var6 = var5 == null ? new ChunkPos(0, 0) : new ChunkPos(var5);
         if (var5 == null) {
            LOGGER.warn("Unable to find spawn biome");
         }

         boolean var7 = false;
         Iterator var8 = BlockTags.VALID_SPAWN.getValues().iterator();

         while(var8.hasNext()) {
            Block var9 = (Block)var8.next();
            if (var2.getSurfaceBlocks().contains(var9.defaultBlockState())) {
               var7 = true;
               break;
            }
         }

         this.levelData.setSpawn(var6.getWorldPosition().offset(8, this.chunkSource.getGenerator().getSpawnHeight(), 8));
         int var15 = 0;
         int var16 = 0;
         int var10 = 0;
         int var11 = -1;
         boolean var12 = true;

         for(int var13 = 0; var13 < 1024; ++var13) {
            if (var15 > -16 && var15 <= 16 && var16 > -16 && var16 <= 16) {
               BlockPos var14 = this.dimension.getSpawnPosInChunk(new ChunkPos(var6.x + var15, var6.z + var16), var7);
               if (var14 != null) {
                  this.levelData.setSpawn(var14);
                  break;
               }
            }

            if (var15 == var16 || var15 < 0 && var15 == -var16 || var15 > 0 && var15 == 1 - var16) {
               int var17 = var10;
               var10 = -var11;
               var11 = var17;
            }

            var15 += var10;
            var16 += var11;
         }

         if (var1.hasStartingBonusItems()) {
            this.generateBonusItemsNearSpawn();
         }

      }
   }

   protected void generateBonusItemsNearSpawn() {
      BonusChestFeature var1 = Feature.BONUS_CHEST;

      for(int var2 = 0; var2 < 10; ++var2) {
         int var3 = this.levelData.getXSpawn() + this.random.nextInt(6) - this.random.nextInt(6);
         int var4 = this.levelData.getZSpawn() + this.random.nextInt(6) - this.random.nextInt(6);
         BlockPos var5 = this.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(var3, 0, var4)).above();
         if (var1.place(this, this.chunkSource.getGenerator(), this.random, var5, (NoneFeatureConfiguration)FeatureConfiguration.NONE)) {
            break;
         }
      }

   }

   @Nullable
   public BlockPos getDimensionSpecificSpawn() {
      return this.dimension.getDimensionSpecificSpawn();
   }

   public void save(@Nullable ProgressListener var1, boolean var2, boolean var3) throws LevelConflictException {
      ServerChunkCache var4 = this.getChunkSource();
      if (!var3) {
         if (var1 != null) {
            var1.progressStartNoAbort(new TranslatableComponent("menu.savingLevel", new Object[0]));
         }

         this.saveLevelData();
         if (var1 != null) {
            var1.progressStage(new TranslatableComponent("menu.savingChunks", new Object[0]));
         }

         var4.save(var2);
      }
   }

   protected void saveLevelData() throws LevelConflictException {
      this.checkSession();
      this.dimension.saveData();
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

         if (var4.hasChunk(Mth.floor(var6.x) >> 4, Mth.floor(var6.z) >> 4) && var2.test(var6)) {
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

   public Object2IntMap<MobCategory> getMobCategoryCounts() {
      Object2IntOpenHashMap var1 = new Object2IntOpenHashMap();
      ObjectIterator var2 = this.entitiesById.values().iterator();

      while(true) {
         Entity var3;
         Mob var4;
         do {
            if (!var2.hasNext()) {
               return var1;
            }

            var3 = (Entity)var2.next();
            if (!(var3 instanceof Mob)) {
               break;
            }

            var4 = (Mob)var3;
         } while(var4.isPersistenceRequired() || var4.requiresCustomPersistence());

         MobCategory var5 = var3.getType().getCategory();
         if (var5 != MobCategory.MISC && this.getChunkSource().isInAccessibleChunk(var3)) {
            var1.mergeInt(var5, 1, Integer::sum);
         }
      }
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
      ChunkAccess var3 = this.getChunk(Mth.floor(var1.x / 16.0D), Mth.floor(var1.z / 16.0D), ChunkStatus.FULL, true);
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
         ChunkAccess var2 = this.getChunk(Mth.floor(var1.x / 16.0D), Mth.floor(var1.z / 16.0D), ChunkStatus.FULL, var1.forcedLoading);
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
      Entity var2 = (Entity)this.entitiesByUuid.get(var1.getUUID());
      if (var2 == null) {
         return false;
      } else {
         LOGGER.warn("Keeping entity {} that already exists with UUID {}", EntityType.getKey(var2.getType()), var1.getUUID().toString());
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
                  throw new IllegalStateException("Removing entity while ticking!");
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
         throw new IllegalStateException("Removing entity while ticking!");
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

   public void addGlobalEntity(LightningBolt var1) {
      this.globalEntities.add(var1);
      this.server.getPlayerList().broadcast((Player)null, var1.x, var1.y, var1.z, 512.0D, this.dimension.getType(), new ClientboundAddGlobalEntityPacket(var1));
   }

   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      Iterator var4 = this.server.getPlayerList().getPlayers().iterator();

      while(var4.hasNext()) {
         ServerPlayer var5 = (ServerPlayer)var4.next();
         if (var5 != null && var5.level == this && var5.getId() != var1) {
            double var6 = (double)var2.getX() - var5.x;
            double var8 = (double)var2.getY() - var5.y;
            double var10 = (double)var2.getZ() - var5.z;
            if (var6 * var6 + var8 * var8 + var10 * var10 < 1024.0D) {
               var5.connection.send(new ClientboundBlockDestructionPacket(var1, var2, var3));
            }
         }
      }

   }

   public void playSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9, float var10, float var11) {
      this.server.getPlayerList().broadcast(var1, var2, var4, var6, var10 > 1.0F ? (double)(16.0F * var10) : 16.0D, this.dimension.getType(), new ClientboundSoundPacket(var8, var9, var2, var4, var6, var10, var11));
   }

   public void playSound(@Nullable Player var1, Entity var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
      this.server.getPlayerList().broadcast(var1, var2.x, var2.y, var2.z, var5 > 1.0F ? (double)(16.0F * var5) : 16.0D, this.dimension.getType(), new ClientboundSoundEntityPacket(var3, var4, var2, var5, var6));
   }

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      this.server.getPlayerList().broadcastAll(new ClientboundLevelEventPacket(var1, var2, var3, true));
   }

   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
      this.server.getPlayerList().broadcast(var1, (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), 64.0D, this.dimension.getType(), new ClientboundLevelEventPacket(var2, var3, var4, false));
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
      return (ServerChunkCache)super.getChunkSource();
   }

   public Explosion explode(@Nullable Entity var1, DamageSource var2, double var3, double var5, double var7, float var9, boolean var10, Explosion.BlockInteraction var11) {
      Explosion var12 = new Explosion(this, var1, var3, var5, var7, var9, var10, var11);
      if (var2 != null) {
         var12.setDamageSource(var2);
      }

      var12.explode();
      var12.finalizeExplosion(false);
      if (var11 == Explosion.BlockInteraction.NONE) {
         var12.clearToBlow();
      }

      Iterator var13 = this.players.iterator();

      while(var13.hasNext()) {
         ServerPlayer var14 = (ServerPlayer)var13.next();
         if (var14.distanceToSqr(var3, var5, var7) < 4096.0D) {
            var14.connection.send(new ClientboundExplodePacket(var3, var5, var7, var9, var12.getToBlow(), (Vec3)var12.getHitPlayers().get(var14)));
         }
      }

      return var12;
   }

   public void blockEvent(BlockPos var1, Block var2, int var3, int var4) {
      this.blockEvents.add(new BlockEventData(var1, var2, var3, var4));
   }

   private void runBlockEvents() {
      while(!this.blockEvents.isEmpty()) {
         BlockEventData var1 = (BlockEventData)this.blockEvents.removeFirst();
         if (this.doBlockEvent(var1)) {
            this.server.getPlayerList().broadcast((Player)null, (double)var1.getPos().getX(), (double)var1.getPos().getY(), (double)var1.getPos().getZ(), 64.0D, this.dimension.getType(), new ClientboundBlockEventPacket(var1.getPos(), var1.getBlock(), var1.getParamA(), var1.getParamB()));
         }
      }

   }

   private boolean doBlockEvent(BlockEventData var1) {
      BlockState var2 = this.getBlockState(var1.getPos());
      return var2.getBlock() == var1.getBlock() ? var2.triggerEvent(this, var1.getPos(), var1.getParamA(), var1.getParamB()) : false;
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
      return this.levelStorage.getStructureManager();
   }

   public <T extends ParticleOptions> int sendParticles(T var1, double var2, double var4, double var6, int var8, double var9, double var11, double var13, double var15) {
      ClientboundLevelParticlesPacket var17 = new ClientboundLevelParticlesPacket(var1, false, (float)var2, (float)var4, (float)var6, (float)var9, (float)var11, (float)var13, (float)var15, var8);
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
      ClientboundLevelParticlesPacket var19 = new ClientboundLevelParticlesPacket(var2, var3, (float)var4, (float)var6, (float)var8, (float)var11, (float)var13, (float)var15, (float)var17, var10);
      return this.sendParticles(var1, var3, var4, var6, var8, var19);
   }

   private boolean sendParticles(ServerPlayer var1, boolean var2, double var3, double var5, double var7, Packet<?> var9) {
      if (var1.getLevel() != this) {
         return false;
      } else {
         BlockPos var10 = var1.getCommandSenderBlockPosition();
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
   public BlockPos findNearestMapFeature(String var1, BlockPos var2, int var3, boolean var4) {
      return this.getChunkSource().getGenerator().findNearestMapFeature(this, var1, var2, var3, var4);
   }

   public RecipeManager getRecipeManager() {
      return this.server.getRecipeManager();
   }

   public TagManager getTagManager() {
      return this.server.getTags();
   }

   public void setGameTime(long var1) {
      super.setGameTime(var1);
      this.levelData.getScheduledEvents().tick(this.server, var1);
   }

   public boolean noSave() {
      return this.noSave;
   }

   public void checkSession() throws LevelConflictException {
      this.levelStorage.checkSession();
   }

   public LevelStorage getLevelStorage() {
      return this.levelStorage;
   }

   public DimensionDataStorage getDataStorage() {
      return this.getChunkSource().getDataStorage();
   }

   @Nullable
   public MapItemSavedData getMapData(String var1) {
      return (MapItemSavedData)this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().get(() -> {
         return new MapItemSavedData(var1);
      }, var1);
   }

   public void setMapData(MapItemSavedData var1) {
      this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().set(var1);
   }

   public int getFreeMapId() {
      return ((MapIndex)this.getServer().getLevel(DimensionType.OVERWORLD).getDataStorage().computeIfAbsent(MapIndex::new, "idcounts")).getFreeAuxValueForMap();
   }

   public void setSpawnPos(BlockPos var1) {
      ChunkPos var2 = new ChunkPos(new BlockPos(this.levelData.getXSpawn(), 0, this.levelData.getZSpawn()));
      super.setSpawnPos(var1);
      this.getChunkSource().removeRegionTicket(TicketType.START, var2, 11, Unit.INSTANCE);
      this.getChunkSource().addRegionTicket(TicketType.START, new ChunkPos(var1), 11, Unit.INSTANCE);
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
      return this.closeToVillage(var1, 1);
   }

   public boolean isVillage(SectionPos var1) {
      return this.isVillage(var1.center());
   }

   public boolean closeToVillage(BlockPos var1, int var2) {
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
         ObjectIterator var5 = this.getMobCategoryCounts().object2IntEntrySet().iterator();

         while(true) {
            if (!var5.hasNext()) {
               var3.write(String.format("entities: %d\n", this.entitiesById.size()));
               var3.write(String.format("block_entities: %d\n", this.blockEntityList.size()));
               var3.write(String.format("block_ticks: %d\n", this.getBlockTicks().size()));
               var3.write(String.format("fluid_ticks: %d\n", this.getLiquidTicks().size()));
               var3.write("distance_manager: " + var2.getDistanceManager().getDebugStatus() + "\n");
               var3.write(String.format("pending_tasks: %d\n", this.getChunkSource().getPendingTasksCount()));
               break;
            }

            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry var6 = (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry)var5.next();
            var3.write(String.format("spawn_count.%s: %d\n", ((MobCategory)var6.getKey()).getName(), var6.getIntValue()));
         }
      } catch (Throwable var164) {
         var4 = var164;
         throw var164;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var153) {
                  var4.addSuppressed(var153);
               }
            } else {
               var3.close();
            }
         }

      }

      CrashReport var166 = new CrashReport("Level dump", new Exception("dummy"));
      this.fillReportDetails(var166);
      BufferedWriter var167 = Files.newBufferedWriter(var1.resolve("example_crash.txt"));
      Throwable var168 = null;

      try {
         var167.write(var166.getFriendlyReport());
      } catch (Throwable var158) {
         var168 = var158;
         throw var158;
      } finally {
         if (var167 != null) {
            if (var168 != null) {
               try {
                  var167.close();
               } catch (Throwable var148) {
                  var168.addSuppressed(var148);
               }
            } else {
               var167.close();
            }
         }

      }

      Path var169 = var1.resolve("chunks.csv");
      BufferedWriter var170 = Files.newBufferedWriter(var169);
      Throwable var171 = null;

      try {
         var2.dumpChunks(var170);
      } catch (Throwable var157) {
         var171 = var157;
         throw var157;
      } finally {
         if (var170 != null) {
            if (var171 != null) {
               try {
                  var170.close();
               } catch (Throwable var149) {
                  var171.addSuppressed(var149);
               }
            } else {
               var170.close();
            }
         }

      }

      Path var172 = var1.resolve("entities.csv");
      BufferedWriter var173 = Files.newBufferedWriter(var172);
      Throwable var7 = null;

      try {
         dumpEntities(var173, this.entitiesById.values());
      } catch (Throwable var156) {
         var7 = var156;
         throw var156;
      } finally {
         if (var173 != null) {
            if (var7 != null) {
               try {
                  var173.close();
               } catch (Throwable var151) {
                  var7.addSuppressed(var151);
               }
            } else {
               var173.close();
            }
         }

      }

      Path var174 = var1.resolve("global_entities.csv");
      BufferedWriter var175 = Files.newBufferedWriter(var174);
      Throwable var8 = null;

      try {
         dumpEntities(var175, this.globalEntities);
      } catch (Throwable var155) {
         var8 = var155;
         throw var155;
      } finally {
         if (var175 != null) {
            if (var8 != null) {
               try {
                  var175.close();
               } catch (Throwable var150) {
                  var8.addSuppressed(var150);
               }
            } else {
               var175.close();
            }
         }

      }

      Path var176 = var1.resolve("block_entities.csv");
      BufferedWriter var177 = Files.newBufferedWriter(var176);
      Throwable var9 = null;

      try {
         this.dumpBlockEntities(var177);
      } catch (Throwable var154) {
         var9 = var154;
         throw var154;
      } finally {
         if (var177 != null) {
            if (var9 != null) {
               try {
                  var177.close();
               } catch (Throwable var152) {
                  var9.addSuppressed(var152);
               }
            } else {
               var177.close();
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
         var2.writeRow(var4.x, var4.y, var4.z, var4.getUUID(), Registry.ENTITY_TYPE.getKey(var4.getType()), var4.isAlive(), var6.getString(), var5 != null ? var5.getString() : null);
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
