package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.slf4j.Logger;

public class ClientLevel extends Level {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final double FLUID_PARTICLE_SPAWN_OFFSET = 0.05;
   private static final int NORMAL_LIGHT_UPDATES_PER_FRAME = 10;
   private static final int LIGHT_UPDATE_QUEUE_SIZE_THRESHOLD = 1000;
   final EntityTickList tickingEntities = new EntityTickList();
   private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager(Entity.class, new EntityCallbacks());
   private final ClientPacketListener connection;
   private final LevelRenderer levelRenderer;
   private final LevelEventHandler levelEventHandler;
   private final ClientLevelData clientLevelData;
   private final DimensionSpecialEffects effects;
   private final TickRateManager tickRateManager;
   private final Minecraft minecraft = Minecraft.getInstance();
   final List<AbstractClientPlayer> players = Lists.newArrayList();
   private final Map<MapId, MapItemSavedData> mapData = Maps.newHashMap();
   private static final int CLOUD_COLOR = -1;
   private int skyFlashTime;
   private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = (Object2ObjectArrayMap)Util.make(new Object2ObjectArrayMap(3), (var1x) -> {
      var1x.put(BiomeColors.GRASS_COLOR_RESOLVER, new BlockTintCache((var1) -> {
         return this.calculateBlockTint(var1, BiomeColors.GRASS_COLOR_RESOLVER);
      }));
      var1x.put(BiomeColors.FOLIAGE_COLOR_RESOLVER, new BlockTintCache((var1) -> {
         return this.calculateBlockTint(var1, BiomeColors.FOLIAGE_COLOR_RESOLVER);
      }));
      var1x.put(BiomeColors.WATER_COLOR_RESOLVER, new BlockTintCache((var1) -> {
         return this.calculateBlockTint(var1, BiomeColors.WATER_COLOR_RESOLVER);
      }));
   });
   private final ClientChunkCache chunkSource;
   private final Deque<Runnable> lightUpdateQueue = Queues.newArrayDeque();
   private int serverSimulationDistance;
   private final BlockStatePredictionHandler blockStatePredictionHandler = new BlockStatePredictionHandler();
   private final int seaLevel;
   private boolean tickDayTime;
   private static final Set<Item> MARKER_PARTICLE_ITEMS;

   public void handleBlockChangedAck(int var1) {
      this.blockStatePredictionHandler.endPredictionsUpTo(var1, this);
   }

   public void setServerVerifiedBlockState(BlockPos var1, BlockState var2, int var3) {
      if (!this.blockStatePredictionHandler.updateKnownServerState(var1, var2)) {
         super.setBlock(var1, var2, var3, 512);
      }

   }

   public void syncBlockState(BlockPos var1, BlockState var2, Vec3 var3) {
      BlockState var4 = this.getBlockState(var1);
      if (var4 != var2) {
         this.setBlock(var1, var2, 19);
         LocalPlayer var5 = this.minecraft.player;
         if (this == ((Player)var5).level() && ((Player)var5).isColliding(var1, var2)) {
            ((Player)var5).absMoveTo(var3.x, var3.y, var3.z);
         }
      }

   }

   BlockStatePredictionHandler getBlockStatePredictionHandler() {
      return this.blockStatePredictionHandler;
   }

   public boolean setBlock(BlockPos var1, BlockState var2, int var3, int var4) {
      if (this.blockStatePredictionHandler.isPredicting()) {
         BlockState var5 = this.getBlockState(var1);
         boolean var6 = super.setBlock(var1, var2, var3, var4);
         if (var6) {
            this.blockStatePredictionHandler.retainKnownServerState(var1, var5, this.minecraft.player);
         }

         return var6;
      } else {
         return super.setBlock(var1, var2, var3, var4);
      }
   }

   public ClientLevel(ClientPacketListener var1, ClientLevelData var2, ResourceKey<Level> var3, Holder<DimensionType> var4, int var5, int var6, LevelRenderer var7, boolean var8, long var9, int var11) {
      super(var2, var3, var1.registryAccess(), var4, true, var8, var9, 1000000);
      this.connection = var1;
      this.chunkSource = new ClientChunkCache(this, var5);
      this.tickRateManager = new TickRateManager();
      this.clientLevelData = var2;
      this.levelRenderer = var7;
      this.seaLevel = var11;
      this.levelEventHandler = new LevelEventHandler(this.minecraft, this, var7);
      this.effects = DimensionSpecialEffects.forType((DimensionType)var4.value());
      this.setDefaultSpawnPos(new BlockPos(8, 64, 8), 0.0F);
      this.serverSimulationDistance = var6;
      this.updateSkyBrightness();
      this.prepareWeather();
   }

   public void queueLightUpdate(Runnable var1) {
      this.lightUpdateQueue.add(var1);
   }

   public void pollLightUpdates() {
      int var1 = this.lightUpdateQueue.size();
      int var2 = var1 < 1000 ? Math.max(10, var1 / 10) : var1;

      for(int var3 = 0; var3 < var2; ++var3) {
         Runnable var4 = (Runnable)this.lightUpdateQueue.poll();
         if (var4 == null) {
            break;
         }

         var4.run();
      }

   }

   public DimensionSpecialEffects effects() {
      return this.effects;
   }

   public void tick(BooleanSupplier var1) {
      this.getWorldBorder().tick();
      this.updateSkyBrightness();
      if (this.tickRateManager().runsNormally()) {
         this.tickTime();
      }

      if (this.skyFlashTime > 0) {
         this.setSkyFlashTime(this.skyFlashTime - 1);
      }

      Zone var2 = Profiler.get().zone("blocks");

      try {
         this.chunkSource.tick(var1, true);
      } catch (Throwable var6) {
         if (var2 != null) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }
         }

         throw var6;
      }

      if (var2 != null) {
         var2.close();
      }

   }

   private void tickTime() {
      this.clientLevelData.setGameTime(this.clientLevelData.getGameTime() + 1L);
      if (this.tickDayTime) {
         this.clientLevelData.setDayTime(this.clientLevelData.getDayTime() + 1L);
      }

   }

   public void setTimeFromServer(long var1, long var3, boolean var5) {
      this.clientLevelData.setGameTime(var1);
      this.clientLevelData.setDayTime(var3);
      this.tickDayTime = var5;
   }

   public Iterable<Entity> entitiesForRendering() {
      return this.getEntities().getAll();
   }

   public void tickEntities() {
      ProfilerFiller var1 = Profiler.get();
      var1.push("entities");
      this.tickingEntities.forEach((var1x) -> {
         if (!var1x.isRemoved() && !var1x.isPassenger() && !this.tickRateManager.isEntityFrozen(var1x)) {
            this.guardEntityTick(this::tickNonPassenger, var1x);
         }
      });
      var1.pop();
      this.tickBlockEntities();
   }

   public boolean isTickingEntity(Entity var1) {
      return this.tickingEntities.contains(var1);
   }

   public boolean shouldTickDeath(Entity var1) {
      return var1.chunkPosition().getChessboardDistance(this.minecraft.player.chunkPosition()) <= this.serverSimulationDistance;
   }

   public void tickNonPassenger(Entity var1) {
      var1.setOldPosAndRot();
      ++var1.tickCount;
      Profiler.get().push(() -> {
         return BuiltInRegistries.ENTITY_TYPE.getKey(var1.getType()).toString();
      });
      var1.tick();
      Profiler.get().pop();
      Iterator var2 = var1.getPassengers().iterator();

      while(var2.hasNext()) {
         Entity var3 = (Entity)var2.next();
         this.tickPassenger(var1, var3);
      }

   }

   private void tickPassenger(Entity var1, Entity var2) {
      if (!var2.isRemoved() && var2.getVehicle() == var1) {
         if (var2 instanceof Player || this.tickingEntities.contains(var2)) {
            var2.setOldPosAndRot();
            ++var2.tickCount;
            var2.rideTick();
            Iterator var3 = var2.getPassengers().iterator();

            while(var3.hasNext()) {
               Entity var4 = (Entity)var3.next();
               this.tickPassenger(var2, var4);
            }

         }
      } else {
         var2.stopRiding();
      }
   }

   public void unload(LevelChunk var1) {
      var1.clearAllBlockEntities();
      this.chunkSource.getLightEngine().setLightEnabled(var1.getPos(), false);
      this.entityStorage.stopTicking(var1.getPos());
   }

   public void onChunkLoaded(ChunkPos var1) {
      this.tintCaches.forEach((var1x, var2) -> {
         var2.invalidateForChunk(var1.x, var1.z);
      });
      this.entityStorage.startTicking(var1);
      this.levelRenderer.onChunkLoaded(var1);
   }

   public void onSectionBecomingNonEmpty(long var1) {
      this.levelRenderer.onSectionBecomingNonEmpty(var1);
   }

   public void clearTintCaches() {
      this.tintCaches.forEach((var0, var1) -> {
         var1.invalidateAll();
      });
   }

   public boolean hasChunk(int var1, int var2) {
      return true;
   }

   public int getEntityCount() {
      return this.entityStorage.count();
   }

   public void addEntity(Entity var1) {
      this.removeEntity(var1.getId(), Entity.RemovalReason.DISCARDED);
      this.entityStorage.addEntity(var1);
   }

   public void removeEntity(int var1, Entity.RemovalReason var2) {
      Entity var3 = (Entity)this.getEntities().get(var1);
      if (var3 != null) {
         var3.setRemoved(var2);
         var3.onClientRemoval();
      }

   }

   @Nullable
   public Entity getEntity(int var1) {
      return (Entity)this.getEntities().get(var1);
   }

   public void disconnect() {
      this.connection.getConnection().disconnect((Component)Component.translatable("multiplayer.status.quitting"));
   }

   public void animateTick(int var1, int var2, int var3) {
      boolean var4 = true;
      RandomSource var5 = RandomSource.create();
      Block var6 = this.getMarkerParticleTarget();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for(int var8 = 0; var8 < 667; ++var8) {
         this.doAnimateTick(var1, var2, var3, 16, var5, var6, var7);
         this.doAnimateTick(var1, var2, var3, 32, var5, var6, var7);
      }

   }

   @Nullable
   private Block getMarkerParticleTarget() {
      if (this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE) {
         ItemStack var1 = this.minecraft.player.getMainHandItem();
         Item var2 = var1.getItem();
         if (MARKER_PARTICLE_ITEMS.contains(var2) && var2 instanceof BlockItem) {
            BlockItem var3 = (BlockItem)var2;
            return var3.getBlock();
         }
      }

      return null;
   }

   public void doAnimateTick(int var1, int var2, int var3, int var4, RandomSource var5, @Nullable Block var6, BlockPos.MutableBlockPos var7) {
      int var8 = var1 + this.random.nextInt(var4) - this.random.nextInt(var4);
      int var9 = var2 + this.random.nextInt(var4) - this.random.nextInt(var4);
      int var10 = var3 + this.random.nextInt(var4) - this.random.nextInt(var4);
      var7.set(var8, var9, var10);
      BlockState var11 = this.getBlockState(var7);
      var11.getBlock().animateTick(var11, this, var7, var5);
      FluidState var12 = this.getFluidState(var7);
      if (!var12.isEmpty()) {
         var12.animateTick(this, var7, var5);
         ParticleOptions var13 = var12.getDripParticle();
         if (var13 != null && this.random.nextInt(10) == 0) {
            boolean var14 = var11.isFaceSturdy(this, var7, Direction.DOWN);
            BlockPos var15 = var7.below();
            this.trySpawnDripParticles(var15, this.getBlockState(var15), var13, var14);
         }
      }

      if (var6 == var11.getBlock()) {
         this.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, var11), (double)var8 + 0.5, (double)var9 + 0.5, (double)var10 + 0.5, 0.0, 0.0, 0.0);
      }

      if (!var11.isCollisionShapeFullBlock(this, var7)) {
         ((Biome)this.getBiome(var7).value()).getAmbientParticle().ifPresent((var2x) -> {
            if (var2x.canSpawn(this.random)) {
               this.addParticle(var2x.getOptions(), (double)var7.getX() + this.random.nextDouble(), (double)var7.getY() + this.random.nextDouble(), (double)var7.getZ() + this.random.nextDouble(), 0.0, 0.0, 0.0);
            }

         });
      }

   }

   private void trySpawnDripParticles(BlockPos var1, BlockState var2, ParticleOptions var3, boolean var4) {
      if (var2.getFluidState().isEmpty()) {
         VoxelShape var5 = var2.getCollisionShape(this, var1);
         double var6 = var5.max(Direction.Axis.Y);
         if (var6 < 1.0) {
            if (var4) {
               this.spawnFluidParticle((double)var1.getX(), (double)(var1.getX() + 1), (double)var1.getZ(), (double)(var1.getZ() + 1), (double)(var1.getY() + 1) - 0.05, var3);
            }
         } else if (!var2.is(BlockTags.IMPERMEABLE)) {
            double var8 = var5.min(Direction.Axis.Y);
            if (var8 > 0.0) {
               this.spawnParticle(var1, var3, var5, (double)var1.getY() + var8 - 0.05);
            } else {
               BlockPos var10 = var1.below();
               BlockState var11 = this.getBlockState(var10);
               VoxelShape var12 = var11.getCollisionShape(this, var10);
               double var13 = var12.max(Direction.Axis.Y);
               if (var13 < 1.0 && var11.getFluidState().isEmpty()) {
                  this.spawnParticle(var1, var3, var5, (double)var1.getY() - 0.05);
               }
            }
         }

      }
   }

   private void spawnParticle(BlockPos var1, ParticleOptions var2, VoxelShape var3, double var4) {
      this.spawnFluidParticle((double)var1.getX() + var3.min(Direction.Axis.X), (double)var1.getX() + var3.max(Direction.Axis.X), (double)var1.getZ() + var3.min(Direction.Axis.Z), (double)var1.getZ() + var3.max(Direction.Axis.Z), var4, var2);
   }

   private void spawnFluidParticle(double var1, double var3, double var5, double var7, double var9, ParticleOptions var11) {
      this.addParticle(var11, Mth.lerp(this.random.nextDouble(), var1, var3), var9, Mth.lerp(this.random.nextDouble(), var5, var7), 0.0, 0.0, 0.0);
   }

   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = super.fillReportDetails(var1);
      var2.setDetail("Server brand", () -> {
         return this.minecraft.player.connection.serverBrand();
      });
      var2.setDetail("Server type", () -> {
         return this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      var2.setDetail("Tracked entity count", () -> {
         return String.valueOf(this.getEntityCount());
      });
      return var2;
   }

   public void playSeededSound(@Nullable Player var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12) {
      if (var1 == this.minecraft.player) {
         this.playSound(var2, var4, var6, (SoundEvent)var8.value(), var9, var10, var11, false, var12);
      }

   }

   public void playSeededSound(@Nullable Player var1, Entity var2, Holder<SoundEvent> var3, SoundSource var4, float var5, float var6, long var7) {
      if (var1 == this.minecraft.player) {
         this.minecraft.getSoundManager().play(new EntityBoundSoundInstance((SoundEvent)var3.value(), var4, var5, var6, var2, var7));
      }

   }

   public void playLocalSound(Entity var1, SoundEvent var2, SoundSource var3, float var4, float var5) {
      this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(var2, var3, var4, var5, var1, this.random.nextLong()));
   }

   public void playLocalSound(double var1, double var3, double var5, SoundEvent var7, SoundSource var8, float var9, float var10, boolean var11) {
      this.playSound(var1, var3, var5, var7, var8, var9, var10, var11, this.random.nextLong());
   }

   private void playSound(double var1, double var3, double var5, SoundEvent var7, SoundSource var8, float var9, float var10, boolean var11, long var12) {
      double var14 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(var1, var3, var5);
      SimpleSoundInstance var16 = new SimpleSoundInstance(var7, var8, var9, var10, RandomSource.create(var12), var1, var3, var5);
      if (var11 && var14 > 100.0) {
         double var17 = Math.sqrt(var14) / 40.0;
         this.minecraft.getSoundManager().playDelayed(var16, (int)(var17 * 20.0));
      } else {
         this.minecraft.getSoundManager().play(var16);
      }

   }

   public void createFireworks(double var1, double var3, double var5, double var7, double var9, double var11, List<FireworkExplosion> var13) {
      if (var13.isEmpty()) {
         for(int var14 = 0; var14 < this.random.nextInt(3) + 2; ++var14) {
            this.addParticle(ParticleTypes.POOF, var1, var3, var5, this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
         }
      } else {
         this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, var1, var3, var5, var7, var9, var11, this.minecraft.particleEngine, var13));
      }

   }

   public void sendPacketToServer(Packet<?> var1) {
      this.connection.send(var1);
   }

   public RecipeAccess recipeAccess() {
      return this.connection.recipes();
   }

   public TickRateManager tickRateManager() {
      return this.tickRateManager;
   }

   public LevelTickAccess<Block> getBlockTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   public LevelTickAccess<Fluid> getFluidTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   public ClientChunkCache getChunkSource() {
      return this.chunkSource;
   }

   @Nullable
   public MapItemSavedData getMapData(MapId var1) {
      return (MapItemSavedData)this.mapData.get(var1);
   }

   public void overrideMapData(MapId var1, MapItemSavedData var2) {
      this.mapData.put(var1, var2);
   }

   public void setMapData(MapId var1, MapItemSavedData var2) {
   }

   public MapId getFreeMapId() {
      return new MapId(0);
   }

   public Scoreboard getScoreboard() {
      return this.connection.scoreboard();
   }

   public void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4) {
      this.levelRenderer.blockChanged(this, var1, var2, var3, var4);
   }

   public void setBlocksDirty(BlockPos var1, BlockState var2, BlockState var3) {
      this.levelRenderer.setBlockDirty(var1, var2, var3);
   }

   public void setSectionDirtyWithNeighbors(int var1, int var2, int var3) {
      this.levelRenderer.setSectionDirtyWithNeighbors(var1, var2, var3);
   }

   public void setSectionRangeDirty(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.levelRenderer.setSectionRangeDirty(var1, var2, var3, var4, var5, var6);
   }

   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      this.levelRenderer.destroyBlockProgress(var1, var2, var3);
   }

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      this.levelEventHandler.globalLevelEvent(var1, var2, var3);
   }

   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
      try {
         this.levelEventHandler.levelEvent(var2, var3, var4);
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Playing level event");
         CrashReportCategory var7 = var6.addCategory("Level event being played");
         var7.setDetail("Block coordinates", (Object)CrashReportCategory.formatLocation(this, var3));
         var7.setDetail("Event source", (Object)var1);
         var7.setDetail("Event type", (Object)var2);
         var7.setDetail("Event data", (Object)var4);
         throw new ReportedException(var6);
      }
   }

   public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.levelRenderer.addParticle(var1, var1.getType().getOverrideLimiter(), var2, var4, var6, var8, var10, var12);
   }

   public void addParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.levelRenderer.addParticle(var1, var1.getType().getOverrideLimiter() || var2, var3, var5, var7, var9, var11, var13);
   }

   public void addAlwaysVisibleParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.levelRenderer.addParticle(var1, false, true, var2, var4, var6, var8, var10, var12);
   }

   public void addAlwaysVisibleParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.levelRenderer.addParticle(var1, var1.getType().getOverrideLimiter() || var2, true, var3, var5, var7, var9, var11, var13);
   }

   public List<AbstractClientPlayer> players() {
      return this.players;
   }

   public Holder<Biome> getUncachedNoiseBiome(int var1, int var2, int var3) {
      return this.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
   }

   public float getSkyDarken(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.2F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 *= 1.0F - this.getRainLevel(var1) * 5.0F / 16.0F;
      var3 *= 1.0F - this.getThunderLevel(var1) * 5.0F / 16.0F;
      return var3 * 0.8F + 0.2F;
   }

   public int getSkyColor(Vec3 var1, float var2) {
      float var3 = this.getTimeOfDay(var2);
      Vec3 var4 = var1.subtract(2.0, 2.0, 2.0).scale(0.25);
      Vec3 var5 = CubicSampler.gaussianSampleVec3(var4, (var1x, var2x, var3x) -> {
         return Vec3.fromRGB24(((Biome)this.getBiomeManager().getNoiseBiomeAtQuart(var1x, var2x, var3x).value()).getSkyColor());
      });
      float var6 = Mth.cos(var3 * 6.2831855F) * 2.0F + 0.5F;
      var6 = Mth.clamp(var6, 0.0F, 1.0F);
      var5 = var5.scale((double)var6);
      int var7 = ARGB.color(var5);
      float var8 = this.getRainLevel(var2);
      float var9;
      float var10;
      if (var8 > 0.0F) {
         var9 = 0.6F;
         var10 = var8 * 0.75F;
         int var11 = ARGB.scaleRGB(ARGB.greyscale(var7), 0.6F);
         var7 = ARGB.lerp(var10, var7, var11);
      }

      var9 = this.getThunderLevel(var2);
      float var14;
      if (var9 > 0.0F) {
         var10 = 0.2F;
         var14 = var9 * 0.75F;
         int var12 = ARGB.scaleRGB(ARGB.greyscale(var7), 0.2F);
         var7 = ARGB.lerp(var14, var7, var12);
      }

      int var13 = this.getSkyFlashTime();
      if (var13 > 0) {
         var14 = Math.min((float)var13 - var2, 1.0F);
         var14 *= 0.45F;
         var7 = ARGB.lerp(var14, var7, ARGB.color(204, 204, 255));
      }

      return var7;
   }

   public int getCloudColor(float var1) {
      int var2 = -1;
      float var3 = this.getRainLevel(var1);
      if (var3 > 0.0F) {
         int var4 = ARGB.scaleRGB(ARGB.greyscale(var2), 0.6F);
         var2 = ARGB.lerp(var3 * 0.95F, var2, var4);
      }

      float var8 = this.getTimeOfDay(var1);
      float var5 = Mth.cos(var8 * 6.2831855F) * 2.0F + 0.5F;
      var5 = Mth.clamp(var5, 0.0F, 1.0F);
      var2 = ARGB.multiply(var2, ARGB.colorFromFloat(1.0F, var5 * 0.9F + 0.1F, var5 * 0.9F + 0.1F, var5 * 0.85F + 0.15F));
      float var6 = this.getThunderLevel(var1);
      if (var6 > 0.0F) {
         int var7 = ARGB.scaleRGB(ARGB.greyscale(var2), 0.2F);
         var2 = ARGB.lerp(var6 * 0.95F, var2, var7);
      }

      return var2;
   }

   public float getStarBrightness(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.25F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public int getSkyFlashTime() {
      return (Boolean)this.minecraft.options.hideLightningFlash().get() ? 0 : this.skyFlashTime;
   }

   public void setSkyFlashTime(int var1) {
      this.skyFlashTime = var1;
   }

   public float getShade(Direction var1, boolean var2) {
      boolean var3 = this.effects().constantAmbientLight();
      if (!var2) {
         return var3 ? 0.9F : 1.0F;
      } else {
         switch (var1) {
            case DOWN:
               return var3 ? 0.9F : 0.5F;
            case UP:
               return var3 ? 0.9F : 1.0F;
            case NORTH:
            case SOUTH:
               return 0.8F;
            case WEST:
            case EAST:
               return 0.6F;
            default:
               return 1.0F;
         }
      }
   }

   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      BlockTintCache var3 = (BlockTintCache)this.tintCaches.get(var2);
      return var3.getColor(var1);
   }

   public int calculateBlockTint(BlockPos var1, ColorResolver var2) {
      int var3 = (Integer)Minecraft.getInstance().options.biomeBlendRadius().get();
      if (var3 == 0) {
         return var2.getColor((Biome)this.getBiome(var1).value(), (double)var1.getX(), (double)var1.getZ());
      } else {
         int var4 = (var3 * 2 + 1) * (var3 * 2 + 1);
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;
         Cursor3D var8 = new Cursor3D(var1.getX() - var3, var1.getY(), var1.getZ() - var3, var1.getX() + var3, var1.getY(), var1.getZ() + var3);

         int var10;
         for(BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(); var8.advance(); var7 += var10 & 255) {
            var9.set(var8.nextX(), var8.nextY(), var8.nextZ());
            var10 = var2.getColor((Biome)this.getBiome(var9).value(), (double)var9.getX(), (double)var9.getZ());
            var5 += (var10 & 16711680) >> 16;
            var6 += (var10 & '\uff00') >> 8;
         }

         return (var5 / var4 & 255) << 16 | (var6 / var4 & 255) << 8 | var7 / var4 & 255;
      }
   }

   public void setDefaultSpawnPos(BlockPos var1, float var2) {
      this.levelData.setSpawn(var1, var2);
   }

   public String toString() {
      return "ClientLevel";
   }

   public ClientLevelData getLevelData() {
      return this.clientLevelData;
   }

   public void gameEvent(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3) {
   }

   protected Map<MapId, MapItemSavedData> getAllMapData() {
      return ImmutableMap.copyOf(this.mapData);
   }

   protected void addMapData(Map<MapId, MapItemSavedData> var1) {
      this.mapData.putAll(var1);
   }

   protected LevelEntityGetter<Entity> getEntities() {
      return this.entityStorage.getEntityGetter();
   }

   public String gatherChunkSourceStats() {
      String var10000 = this.chunkSource.gatherStats();
      return "Chunks[C] W: " + var10000 + " E: " + this.entityStorage.gatherStats();
   }

   public void addDestroyBlockEffect(BlockPos var1, BlockState var2) {
      this.minecraft.particleEngine.destroy(var1, var2);
   }

   public void setServerSimulationDistance(int var1) {
      this.serverSimulationDistance = var1;
   }

   public int getServerSimulationDistance() {
      return this.serverSimulationDistance;
   }

   public FeatureFlagSet enabledFeatures() {
      return this.connection.enabledFeatures();
   }

   public PotionBrewing potionBrewing() {
      return this.connection.potionBrewing();
   }

   public FuelValues fuelValues() {
      return this.connection.fuelValues();
   }

   public void explode(@Nullable Entity var1, @Nullable DamageSource var2, @Nullable ExplosionDamageCalculator var3, double var4, double var6, double var8, float var10, boolean var11, Level.ExplosionInteraction var12, ParticleOptions var13, ParticleOptions var14, Holder<SoundEvent> var15) {
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   // $FF: synthetic method
   public LevelData getLevelData() {
      return this.getLevelData();
   }

   // $FF: synthetic method
   public ChunkSource getChunkSource() {
      return this.getChunkSource();
   }

   static {
      MARKER_PARTICLE_ITEMS = Set.of(Items.BARRIER, Items.LIGHT);
   }

   private final class EntityCallbacks implements LevelCallback<Entity> {
      EntityCallbacks() {
         super();
      }

      public void onCreated(Entity var1) {
      }

      public void onDestroyed(Entity var1) {
      }

      public void onTickingStart(Entity var1) {
         ClientLevel.this.tickingEntities.add(var1);
      }

      public void onTickingEnd(Entity var1) {
         ClientLevel.this.tickingEntities.remove(var1);
      }

      public void onTrackingStart(Entity var1) {
         if (var1 instanceof AbstractClientPlayer) {
            ClientLevel.this.players.add((AbstractClientPlayer)var1);
         }

      }

      public void onTrackingEnd(Entity var1) {
         var1.unRide();
         ClientLevel.this.players.remove(var1);
      }

      public void onSectionChange(Entity var1) {
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

   public static class ClientLevelData implements WritableLevelData {
      private final boolean hardcore;
      private final boolean isFlat;
      private BlockPos spawnPos;
      private float spawnAngle;
      private long gameTime;
      private long dayTime;
      private boolean raining;
      private Difficulty difficulty;
      private boolean difficultyLocked;

      public ClientLevelData(Difficulty var1, boolean var2, boolean var3) {
         super();
         this.difficulty = var1;
         this.hardcore = var2;
         this.isFlat = var3;
      }

      public BlockPos getSpawnPos() {
         return this.spawnPos;
      }

      public float getSpawnAngle() {
         return this.spawnAngle;
      }

      public long getGameTime() {
         return this.gameTime;
      }

      public long getDayTime() {
         return this.dayTime;
      }

      public void setGameTime(long var1) {
         this.gameTime = var1;
      }

      public void setDayTime(long var1) {
         this.dayTime = var1;
      }

      public void setSpawn(BlockPos var1, float var2) {
         this.spawnPos = var1.immutable();
         this.spawnAngle = var2;
      }

      public boolean isThundering() {
         return false;
      }

      public boolean isRaining() {
         return this.raining;
      }

      public void setRaining(boolean var1) {
         this.raining = var1;
      }

      public boolean isHardcore() {
         return this.hardcore;
      }

      public Difficulty getDifficulty() {
         return this.difficulty;
      }

      public boolean isDifficultyLocked() {
         return this.difficultyLocked;
      }

      public void fillCrashReportCategory(CrashReportCategory var1, LevelHeightAccessor var2) {
         WritableLevelData.super.fillCrashReportCategory(var1, var2);
      }

      public void setDifficulty(Difficulty var1) {
         this.difficulty = var1;
      }

      public void setDifficultyLocked(boolean var1) {
         this.difficultyLocked = var1;
      }

      public double getHorizonHeight(LevelHeightAccessor var1) {
         return this.isFlat ? (double)var1.getMinY() : 63.0;
      }

      public float getClearColorScale() {
         return this.isFlat ? 1.0F : 0.03125F;
      }
   }
}
