package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
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
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
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
   private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager<>(Entity.class, new ClientLevel.EntityCallbacks());
   private final ClientPacketListener connection;
   private final LevelRenderer levelRenderer;
   private final ClientLevel.ClientLevelData clientLevelData;
   private final DimensionSpecialEffects effects;
   private final TickRateManager tickRateManager;
   private final Minecraft minecraft = Minecraft.getInstance();
   final List<AbstractClientPlayer> players = Lists.newArrayList();
   private final Map<MapId, MapItemSavedData> mapData = Maps.newHashMap();
   private static final long CLOUD_COLOR = 16777215L;
   private int skyFlashTime;
   private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = Util.make(new Object2ObjectArrayMap(3), var1x -> {
      var1x.put(BiomeColors.GRASS_COLOR_RESOLVER, new BlockTintCache(var1xx -> this.calculateBlockTint(var1xx, BiomeColors.GRASS_COLOR_RESOLVER)));
      var1x.put(BiomeColors.FOLIAGE_COLOR_RESOLVER, new BlockTintCache(var1xx -> this.calculateBlockTint(var1xx, BiomeColors.FOLIAGE_COLOR_RESOLVER)));
      var1x.put(BiomeColors.WATER_COLOR_RESOLVER, new BlockTintCache(var1xx -> this.calculateBlockTint(var1xx, BiomeColors.WATER_COLOR_RESOLVER)));
   });
   private final ClientChunkCache chunkSource;
   private final Deque<Runnable> lightUpdateQueue = Queues.newArrayDeque();
   private int serverSimulationDistance;
   private final BlockStatePredictionHandler blockStatePredictionHandler = new BlockStatePredictionHandler();
   private static final Set<Item> MARKER_PARTICLE_ITEMS = Set.of(Items.BARRIER, Items.LIGHT);

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
         if (this == var5.level() && var5.isColliding(var1, var2)) {
            var5.absMoveTo(var3.x, var3.y, var3.z);
         }
      }
   }

   BlockStatePredictionHandler getBlockStatePredictionHandler() {
      return this.blockStatePredictionHandler;
   }

   @Override
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

   public ClientLevel(
      ClientPacketListener var1,
      ClientLevel.ClientLevelData var2,
      ResourceKey<Level> var3,
      Holder<DimensionType> var4,
      int var5,
      int var6,
      Supplier<ProfilerFiller> var7,
      LevelRenderer var8,
      boolean var9,
      long var10
   ) {
      super(var2, var3, var1.registryAccess(), var4, var7, true, var9, var10, 1000000);
      this.connection = var1;
      this.chunkSource = new ClientChunkCache(this, var5);
      this.tickRateManager = new TickRateManager();
      this.clientLevelData = var2;
      this.levelRenderer = var8;
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

      for (int var3 = 0; var3 < var2; var3++) {
         Runnable var4 = this.lightUpdateQueue.poll();
         if (var4 == null) {
            break;
         }

         var4.run();
      }
   }

   public boolean isLightUpdateQueueEmpty() {
      return this.lightUpdateQueue.isEmpty();
   }

   public DimensionSpecialEffects effects() {
      return this.effects;
   }

   public void tick(BooleanSupplier var1) {
      this.getWorldBorder().tick();
      if (this.tickRateManager().runsNormally()) {
         this.tickTime();
      }

      if (this.skyFlashTime > 0) {
         this.setSkyFlashTime(this.skyFlashTime - 1);
      }

      this.getProfiler().push("blocks");
      this.chunkSource.tick(var1, true);
      this.getProfiler().pop();
   }

   private void tickTime() {
      this.setGameTime(this.levelData.getGameTime() + 1L);
      if (this.levelData.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
         this.setDayTime(this.levelData.getDayTime() + 1L);
      }
   }

   public void setGameTime(long var1) {
      this.clientLevelData.setGameTime(var1);
   }

   public void setDayTime(long var1) {
      if (var1 < 0L) {
         var1 = -var1;
         this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, null);
      } else {
         this.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(true, null);
      }

      this.clientLevelData.setDayTime(var1);
   }

   public Iterable<Entity> entitiesForRendering() {
      return this.getEntities().getAll();
   }

   public void tickEntities() {
      ProfilerFiller var1 = this.getProfiler();
      var1.push("entities");
      this.tickingEntities.forEach(var1x -> {
         if (!var1x.isRemoved() && !var1x.isPassenger() && !this.tickRateManager.isEntityFrozen(var1x)) {
            this.guardEntityTick(this::tickNonPassenger, var1x);
         }
      });
      var1.pop();
      this.tickBlockEntities();
   }

   @Override
   public boolean shouldTickDeath(Entity var1) {
      return var1.chunkPosition().getChessboardDistance(this.minecraft.player.chunkPosition()) <= this.serverSimulationDistance;
   }

   public void tickNonPassenger(Entity var1) {
      var1.setOldPosAndRot();
      var1.tickCount++;
      this.getProfiler().push(() -> BuiltInRegistries.ENTITY_TYPE.getKey(var1.getType()).toString());
      var1.tick();
      this.getProfiler().pop();

      for (Entity var3 : var1.getPassengers()) {
         this.tickPassenger(var1, var3);
      }
   }

   private void tickPassenger(Entity var1, Entity var2) {
      if (var2.isRemoved() || var2.getVehicle() != var1) {
         var2.stopRiding();
      } else if (var2 instanceof Player || this.tickingEntities.contains(var2)) {
         var2.setOldPosAndRot();
         var2.tickCount++;
         var2.rideTick();

         for (Entity var4 : var2.getPassengers()) {
            this.tickPassenger(var2, var4);
         }
      }
   }

   public void unload(LevelChunk var1) {
      var1.clearAllBlockEntities();
      this.chunkSource.getLightEngine().setLightEnabled(var1.getPos(), false);
      this.entityStorage.stopTicking(var1.getPos());
   }

   public void onChunkLoaded(ChunkPos var1) {
      this.tintCaches.forEach((var1x, var2) -> var2.invalidateForChunk(var1.x, var1.z));
      this.entityStorage.startTicking(var1);
      this.levelRenderer.onChunkLoaded(var1);
   }

   public void clearTintCaches() {
      this.tintCaches.forEach((var0, var1) -> var1.invalidateAll());
   }

   @Override
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
      Entity var3 = this.getEntities().get(var1);
      if (var3 != null) {
         var3.setRemoved(var2);
         var3.onClientRemoval();
      }
   }

   @Nullable
   @Override
   public Entity getEntity(int var1) {
      return this.getEntities().get(var1);
   }

   @Override
   public void disconnect() {
      this.connection.getConnection().disconnect(Component.translatable("multiplayer.status.quitting"));
   }

   public void animateTick(int var1, int var2, int var3) {
      byte var4 = 32;
      RandomSource var5 = RandomSource.create();
      Block var6 = this.getMarkerParticleTarget();
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      for (int var8 = 0; var8 < 667; var8++) {
         this.doAnimateTick(var1, var2, var3, 16, var5, var6, var7);
         this.doAnimateTick(var1, var2, var3, 32, var5, var6, var7);
      }
   }

   @Nullable
   private Block getMarkerParticleTarget() {
      if (this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE) {
         ItemStack var1 = this.minecraft.player.getMainHandItem();
         Item var2 = var1.getItem();
         if (MARKER_PARTICLE_ITEMS.contains(var2) && var2 instanceof BlockItem var3) {
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
         this.addParticle(
            new BlockParticleOption(ParticleTypes.BLOCK_MARKER, var11), (double)var8 + 0.5, (double)var9 + 0.5, (double)var10 + 0.5, 0.0, 0.0, 0.0
         );
      }

      if (!var11.isCollisionShapeFullBlock(this, var7)) {
         this.getBiome(var7)
            .value()
            .getAmbientParticle()
            .ifPresent(
               var2x -> {
                  if (var2x.canSpawn(this.random)) {
                     this.addParticle(
                        var2x.getOptions(),
                        (double)var7.getX() + this.random.nextDouble(),
                        (double)var7.getY() + this.random.nextDouble(),
                        (double)var7.getZ() + this.random.nextDouble(),
                        0.0,
                        0.0,
                        0.0
                     );
                  }
               }
            );
      }
   }

   private void trySpawnDripParticles(BlockPos var1, BlockState var2, ParticleOptions var3, boolean var4) {
      if (var2.getFluidState().isEmpty()) {
         VoxelShape var5 = var2.getCollisionShape(this, var1);
         double var6 = var5.max(Direction.Axis.Y);
         if (var6 < 1.0) {
            if (var4) {
               this.spawnFluidParticle(
                  (double)var1.getX(), (double)(var1.getX() + 1), (double)var1.getZ(), (double)(var1.getZ() + 1), (double)(var1.getY() + 1) - 0.05, var3
               );
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
      this.spawnFluidParticle(
         (double)var1.getX() + var3.min(Direction.Axis.X),
         (double)var1.getX() + var3.max(Direction.Axis.X),
         (double)var1.getZ() + var3.min(Direction.Axis.Z),
         (double)var1.getZ() + var3.max(Direction.Axis.Z),
         var4,
         var2
      );
   }

   private void spawnFluidParticle(double var1, double var3, double var5, double var7, double var9, ParticleOptions var11) {
      this.addParticle(var11, Mth.lerp(this.random.nextDouble(), var1, var3), var9, Mth.lerp(this.random.nextDouble(), var5, var7), 0.0, 0.0, 0.0);
   }

   @Override
   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = super.fillReportDetails(var1);
      var2.setDetail("Server brand", () -> this.minecraft.player.connection.serverBrand());
      var2.setDetail(
         "Server type", () -> this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server"
      );
      var2.setDetail("Tracked entity count", () -> String.valueOf(this.getEntityCount()));
      return var2;
   }

   @Override
   public void playSeededSound(
      @Nullable Player var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12
   ) {
      if (var1 == this.minecraft.player) {
         this.playSound(var2, var4, var6, (SoundEvent)var8.value(), var9, var10, var11, false, var12);
      }
   }

   @Override
   public void playSeededSound(@Nullable Player var1, Entity var2, Holder<SoundEvent> var3, SoundSource var4, float var5, float var6, long var7) {
      if (var1 == this.minecraft.player) {
         this.minecraft.getSoundManager().play(new EntityBoundSoundInstance((SoundEvent)var3.value(), var4, var5, var6, var2, var7));
      }
   }

   @Override
   public void playLocalSound(Entity var1, SoundEvent var2, SoundSource var3, float var4, float var5) {
      this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(var2, var3, var4, var5, var1, this.random.nextLong()));
   }

   @Override
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

   @Override
   public void createFireworks(double var1, double var3, double var5, double var7, double var9, double var11, List<FireworkExplosion> var13) {
      if (var13.isEmpty()) {
         for (int var14 = 0; var14 < this.random.nextInt(3) + 2; var14++) {
            this.addParticle(ParticleTypes.POOF, var1, var3, var5, this.random.nextGaussian() * 0.05, 0.005, this.random.nextGaussian() * 0.05);
         }
      } else {
         this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, var1, var3, var5, var7, var9, var11, this.minecraft.particleEngine, var13));
      }
   }

   @Override
   public void sendPacketToServer(Packet<?> var1) {
      this.connection.send(var1);
   }

   @Override
   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   @Override
   public TickRateManager tickRateManager() {
      return this.tickRateManager;
   }

   @Override
   public LevelTickAccess<Block> getBlockTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   @Override
   public LevelTickAccess<Fluid> getFluidTicks() {
      return BlackholeTickAccess.emptyLevelList();
   }

   public ClientChunkCache getChunkSource() {
      return this.chunkSource;
   }

   @Nullable
   @Override
   public MapItemSavedData getMapData(MapId var1) {
      return this.mapData.get(var1);
   }

   public void overrideMapData(MapId var1, MapItemSavedData var2) {
      this.mapData.put(var1, var2);
   }

   @Override
   public void setMapData(MapId var1, MapItemSavedData var2) {
   }

   @Override
   public MapId getFreeMapId() {
      return new MapId(0);
   }

   @Override
   public Scoreboard getScoreboard() {
      return this.connection.scoreboard();
   }

   @Override
   public void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, int var4) {
      this.levelRenderer.blockChanged(this, var1, var2, var3, var4);
   }

   @Override
   public void setBlocksDirty(BlockPos var1, BlockState var2, BlockState var3) {
      this.levelRenderer.setBlockDirty(var1, var2, var3);
   }

   public void setSectionDirtyWithNeighbors(int var1, int var2, int var3) {
      this.levelRenderer.setSectionDirtyWithNeighbors(var1, var2, var3);
   }

   @Override
   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      this.levelRenderer.destroyBlockProgress(var1, var2, var3);
   }

   @Override
   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      this.levelRenderer.globalLevelEvent(var1, var2, var3);
   }

   @Override
   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
      try {
         this.levelRenderer.levelEvent(var2, var3, var4);
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Playing level event");
         CrashReportCategory var7 = var6.addCategory("Level event being played");
         var7.setDetail("Block coordinates", CrashReportCategory.formatLocation(this, var3));
         var7.setDetail("Event source", var1);
         var7.setDetail("Event type", var2);
         var7.setDetail("Event data", var4);
         throw new ReportedException(var6);
      }
   }

   @Override
   public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.levelRenderer.addParticle(var1, var1.getType().getOverrideLimiter(), var2, var4, var6, var8, var10, var12);
   }

   @Override
   public void addParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.levelRenderer.addParticle(var1, var1.getType().getOverrideLimiter() || var2, var3, var5, var7, var9, var11, var13);
   }

   @Override
   public void addAlwaysVisibleParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.levelRenderer.addParticle(var1, false, true, var2, var4, var6, var8, var10, var12);
   }

   @Override
   public void addAlwaysVisibleParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.levelRenderer.addParticle(var1, var1.getType().getOverrideLimiter() || var2, true, var3, var5, var7, var9, var11, var13);
   }

   @Override
   public List<AbstractClientPlayer> players() {
      return this.players;
   }

   @Override
   public Holder<Biome> getUncachedNoiseBiome(int var1, int var2, int var3) {
      return this.registryAccess().registryOrThrow(Registries.BIOME).getHolderOrThrow(Biomes.PLAINS);
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

   public Vec3 getSkyColor(Vec3 var1, float var2) {
      float var3 = this.getTimeOfDay(var2);
      Vec3 var4 = var1.subtract(2.0, 2.0, 2.0).scale(0.25);
      BiomeManager var5 = this.getBiomeManager();
      Vec3 var6 = CubicSampler.gaussianSampleVec3(
         var4, (var1x, var2x, var3x) -> Vec3.fromRGB24(var5.getNoiseBiomeAtQuart(var1x, var2x, var3x).value().getSkyColor())
      );
      float var7 = Mth.cos(var3 * 6.2831855F) * 2.0F + 0.5F;
      var7 = Mth.clamp(var7, 0.0F, 1.0F);
      float var8 = (float)var6.x * var7;
      float var9 = (float)var6.y * var7;
      float var10 = (float)var6.z * var7;
      float var11 = this.getRainLevel(var2);
      if (var11 > 0.0F) {
         float var12 = (var8 * 0.3F + var9 * 0.59F + var10 * 0.11F) * 0.6F;
         float var13 = 1.0F - var11 * 0.75F;
         var8 = var8 * var13 + var12 * (1.0F - var13);
         var9 = var9 * var13 + var12 * (1.0F - var13);
         var10 = var10 * var13 + var12 * (1.0F - var13);
      }

      float var16 = this.getThunderLevel(var2);
      if (var16 > 0.0F) {
         float var17 = (var8 * 0.3F + var9 * 0.59F + var10 * 0.11F) * 0.2F;
         float var14 = 1.0F - var16 * 0.75F;
         var8 = var8 * var14 + var17 * (1.0F - var14);
         var9 = var9 * var14 + var17 * (1.0F - var14);
         var10 = var10 * var14 + var17 * (1.0F - var14);
      }

      int var18 = this.getSkyFlashTime();
      if (var18 > 0) {
         float var19 = (float)var18 - var2;
         if (var19 > 1.0F) {
            var19 = 1.0F;
         }

         var19 *= 0.45F;
         var8 = var8 * (1.0F - var19) + 0.8F * var19;
         var9 = var9 * (1.0F - var19) + 0.8F * var19;
         var10 = var10 * (1.0F - var19) + 1.0F * var19;
      }

      return new Vec3((double)var8, (double)var9, (double)var10);
   }

   public Vec3 getCloudColor(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = Mth.cos(var2 * 6.2831855F) * 2.0F + 0.5F;
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      float var4 = 1.0F;
      float var5 = 1.0F;
      float var6 = 1.0F;
      float var7 = this.getRainLevel(var1);
      if (var7 > 0.0F) {
         float var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
         float var9 = 1.0F - var7 * 0.95F;
         var4 = var4 * var9 + var8 * (1.0F - var9);
         var5 = var5 * var9 + var8 * (1.0F - var9);
         var6 = var6 * var9 + var8 * (1.0F - var9);
      }

      var4 *= var3 * 0.9F + 0.1F;
      var5 *= var3 * 0.9F + 0.1F;
      var6 *= var3 * 0.85F + 0.15F;
      float var15 = this.getThunderLevel(var1);
      if (var15 > 0.0F) {
         float var16 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
         float var10 = 1.0F - var15 * 0.95F;
         var4 = var4 * var10 + var16 * (1.0F - var10);
         var5 = var5 * var10 + var16 * (1.0F - var10);
         var6 = var6 * var10 + var16 * (1.0F - var10);
      }

      return new Vec3((double)var4, (double)var5, (double)var6);
   }

   public float getStarBrightness(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.25F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public int getSkyFlashTime() {
      return this.minecraft.options.hideLightningFlash().get() ? 0 : this.skyFlashTime;
   }

   @Override
   public void setSkyFlashTime(int var1) {
      this.skyFlashTime = var1;
   }

   @Override
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

   @Override
   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      BlockTintCache var3 = (BlockTintCache)this.tintCaches.get(var2);
      return var3.getColor(var1);
   }

   public int calculateBlockTint(BlockPos var1, ColorResolver var2) {
      int var3 = Minecraft.getInstance().options.biomeBlendRadius().get();
      if (var3 == 0) {
         return var2.getColor(this.getBiome(var1).value(), (double)var1.getX(), (double)var1.getZ());
      } else {
         int var4 = (var3 * 2 + 1) * (var3 * 2 + 1);
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;
         Cursor3D var8 = new Cursor3D(var1.getX() - var3, var1.getY(), var1.getZ() - var3, var1.getX() + var3, var1.getY(), var1.getZ() + var3);
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

         while (var8.advance()) {
            var9.set(var8.nextX(), var8.nextY(), var8.nextZ());
            int var10 = var2.getColor(this.getBiome(var9).value(), (double)var9.getX(), (double)var9.getZ());
            var5 += (var10 & 0xFF0000) >> 16;
            var6 += (var10 & 0xFF00) >> 8;
            var7 += var10 & 0xFF;
         }

         return (var5 / var4 & 0xFF) << 16 | (var6 / var4 & 0xFF) << 8 | var7 / var4 & 0xFF;
      }
   }

   public void setDefaultSpawnPos(BlockPos var1, float var2) {
      this.levelData.setSpawn(var1, var2);
   }

   @Override
   public String toString() {
      return "ClientLevel";
   }

   public ClientLevel.ClientLevelData getLevelData() {
      return this.clientLevelData;
   }

   @Override
   public void gameEvent(Holder<GameEvent> var1, Vec3 var2, GameEvent.Context var3) {
   }

   protected Map<MapId, MapItemSavedData> getAllMapData() {
      return ImmutableMap.copyOf(this.mapData);
   }

   protected void addMapData(Map<MapId, MapItemSavedData> var1) {
      this.mapData.putAll(var1);
   }

   @Override
   protected LevelEntityGetter<Entity> getEntities() {
      return this.entityStorage.getEntityGetter();
   }

   @Override
   public String gatherChunkSourceStats() {
      return "Chunks[C] W: " + this.chunkSource.gatherStats() + " E: " + this.entityStorage.gatherStats();
   }

   @Override
   public void addDestroyBlockEffect(BlockPos var1, BlockState var2) {
      this.minecraft.particleEngine.destroy(var1, var2);
   }

   public void setServerSimulationDistance(int var1) {
      this.serverSimulationDistance = var1;
   }

   public int getServerSimulationDistance() {
      return this.serverSimulationDistance;
   }

   @Override
   public FeatureFlagSet enabledFeatures() {
      return this.connection.enabledFeatures();
   }

   @Override
   public PotionBrewing potionBrewing() {
      return this.connection.potionBrewing();
   }

   public static class ClientLevelData implements WritableLevelData {
      private final boolean hardcore;
      private final GameRules gameRules;
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
         this.gameRules = new GameRules();
      }

      @Override
      public BlockPos getSpawnPos() {
         return this.spawnPos;
      }

      @Override
      public float getSpawnAngle() {
         return this.spawnAngle;
      }

      @Override
      public long getGameTime() {
         return this.gameTime;
      }

      @Override
      public long getDayTime() {
         return this.dayTime;
      }

      public void setGameTime(long var1) {
         this.gameTime = var1;
      }

      public void setDayTime(long var1) {
         this.dayTime = var1;
      }

      @Override
      public void setSpawn(BlockPos var1, float var2) {
         this.spawnPos = var1.immutable();
         this.spawnAngle = var2;
      }

      @Override
      public boolean isThundering() {
         return false;
      }

      @Override
      public boolean isRaining() {
         return this.raining;
      }

      @Override
      public void setRaining(boolean var1) {
         this.raining = var1;
      }

      @Override
      public boolean isHardcore() {
         return this.hardcore;
      }

      @Override
      public GameRules getGameRules() {
         return this.gameRules;
      }

      @Override
      public Difficulty getDifficulty() {
         return this.difficulty;
      }

      @Override
      public boolean isDifficultyLocked() {
         return this.difficultyLocked;
      }

      @Override
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
         return this.isFlat ? (double)var1.getMinBuildHeight() : 63.0;
      }

      public float getClearColorScale() {
         return this.isFlat ? 1.0F : 0.03125F;
      }
   }

   final class EntityCallbacks implements LevelCallback<Entity> {
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
   }
}
