package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.multiplayer.prediction.BlockStatePredictionHandler;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.EndFlashState;
import net.minecraft.client.renderer.LevelEventHandler;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.sounds.DirectionalSoundInstance;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ExplosionParticleInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.Zone;
import net.minecraft.util.profiling.jfr.JvmProfiler;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.attribute.AmbientParticle;
import net.minecraft.world.attribute.EnvironmentAttributeReader;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
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
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FuelValues;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class ClientLevel extends Level implements CacheSlot.Cleaner<ClientLevel> {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final Component DEFAULT_QUIT_MESSAGE = Component.translatable("multiplayer.status.quitting");
   private static final double FLUID_PARTICLE_SPAWN_OFFSET = 0.05;
   private static final int NORMAL_LIGHT_UPDATES_PER_FRAME = 10;
   private static final int LIGHT_UPDATE_QUEUE_SIZE_THRESHOLD = 1000;
   final EntityTickList tickingEntities = new EntityTickList();
   private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager<Entity>(Entity.class, new EntityCallbacks());
   private final ClientPacketListener connection;
   private final LevelRenderer levelRenderer;
   private final LevelEventHandler levelEventHandler;
   private final ClientLevelData clientLevelData;
   private final TickRateManager tickRateManager;
   private final @Nullable EndFlashState endFlashState;
   private final Minecraft minecraft = Minecraft.getInstance();
   final List<AbstractClientPlayer> players = Lists.newArrayList();
   final List<EnderDragonPart> dragonParts = Lists.newArrayList();
   private final Map<MapId, MapItemSavedData> mapData = Maps.newHashMap();
   private int skyFlashTime;
   private final Object2ObjectArrayMap<ColorResolver, BlockTintCache> tintCaches = (Object2ObjectArrayMap)Util.make(new Object2ObjectArrayMap(3), (var1x) -> {
      var1x.put(BiomeColors.GRASS_COLOR_RESOLVER, new BlockTintCache((var1) -> this.calculateBlockTint(var1, BiomeColors.GRASS_COLOR_RESOLVER)));
      var1x.put(BiomeColors.FOLIAGE_COLOR_RESOLVER, new BlockTintCache((var1) -> this.calculateBlockTint(var1, BiomeColors.FOLIAGE_COLOR_RESOLVER)));
      var1x.put(BiomeColors.DRY_FOLIAGE_COLOR_RESOLVER, new BlockTintCache((var1) -> this.calculateBlockTint(var1, BiomeColors.DRY_FOLIAGE_COLOR_RESOLVER)));
      var1x.put(BiomeColors.WATER_COLOR_RESOLVER, new BlockTintCache((var1) -> this.calculateBlockTint(var1, BiomeColors.WATER_COLOR_RESOLVER)));
   });
   private final ClientChunkCache chunkSource;
   private final Deque<Runnable> lightUpdateQueue = Queues.newArrayDeque();
   private int serverSimulationDistance;
   private final BlockStatePredictionHandler blockStatePredictionHandler = new BlockStatePredictionHandler();
   private final Set<BlockEntity> globallyRenderedBlockEntities = new ReferenceOpenHashSet();
   private final ClientExplosionTracker explosionTracker = new ClientExplosionTracker();
   private final WorldBorder worldBorder = new WorldBorder();
   private final EnvironmentAttributeSystem environmentAttributes;
   private final int seaLevel;
   private boolean tickDayTime;
   private static final Set<Item> MARKER_PARTICLE_ITEMS;

   public void handleBlockChangedAck(int var1) {
      if (SharedConstants.DEBUG_BLOCK_BREAK) {
         LOGGER.debug("ACK {}", var1);
      }

      this.blockStatePredictionHandler.endPredictionsUpTo(var1, this);
   }

   public void onBlockEntityAdded(BlockEntity var1) {
      BlockEntityRenderer var2 = this.minecraft.getBlockEntityRenderDispatcher().getRenderer(var1);
      if (var2 != null && var2.shouldRenderOffScreen()) {
         this.globallyRenderedBlockEntities.add(var1);
      }

   }

   public Set<BlockEntity> getGloballyRenderedBlockEntities() {
      return this.globallyRenderedBlockEntities;
   }

   public void setServerVerifiedBlockState(BlockPos var1, BlockState var2, @Block.UpdateFlags int var3) {
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
            ((Player)var5).absSnapTo(var3.x, var3.y, var3.z);
         }
      }

   }

   BlockStatePredictionHandler getBlockStatePredictionHandler() {
      return this.blockStatePredictionHandler;
   }

   public boolean setBlock(BlockPos var1, BlockState var2, @Block.UpdateFlags int var3, int var4) {
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
      this.levelEventHandler = new LevelEventHandler(this.minecraft, this);
      this.endFlashState = ((DimensionType)var4.value()).hasEndFlashes() ? new EndFlashState() : null;
      this.setRespawnData(LevelData.RespawnData.of(var3, new BlockPos(8, 64, 8), 0.0F, 0.0F));
      this.serverSimulationDistance = var6;
      this.environmentAttributes = this.addEnvironmentAttributeLayers(EnvironmentAttributeSystem.builder()).build();
      this.updateSkyBrightness();
      this.prepareWeather();
   }

   private EnvironmentAttributeSystem.Builder addEnvironmentAttributeLayers(EnvironmentAttributeSystem.Builder var1) {
      var1.addDefaultLayers(this);
      int var2 = ARGB.color(204, 204, 255);
      var1.addTimeBasedLayer(EnvironmentAttributes.SKY_COLOR, (var2x, var3) -> this.getSkyFlashTime() > 0 ? ARGB.srgbLerp(0.22F, var2x, var2) : var2x);
      var1.addTimeBasedLayer(EnvironmentAttributes.SKY_LIGHT_FACTOR, (var1x, var2x) -> this.getSkyFlashTime() > 0 ? 1.0F : var1x);
      return var1;
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

   public @Nullable EndFlashState endFlashState() {
      return this.endFlashState;
   }

   public void tick(BooleanSupplier var1) {
      this.updateSkyBrightness();
      if (this.tickRateManager().runsNormally()) {
         this.getWorldBorder().tick();
         this.tickTime();
      }

      if (this.skyFlashTime > 0) {
         this.setSkyFlashTime(this.skyFlashTime - 1);
      }

      if (this.endFlashState != null) {
         this.endFlashState.tick(this.getGameTime());
         if (this.endFlashState.flashStartedThisTick() && !(this.minecraft.screen instanceof WinScreen)) {
            this.minecraft.getSoundManager().playDelayed(new DirectionalSoundInstance(SoundEvents.WEATHER_END_FLASH, SoundSource.WEATHER, this.random, this.minecraft.gameRenderer.getMainCamera(), this.endFlashState.getXAngle(), this.endFlashState.getYAngle()), 30);
         }
      }

      this.explosionTracker.tick(this);

      try (Zone var2 = Profiler.get().zone("blocks")) {
         this.chunkSource.tick(var1, true);
      }

      JvmProfiler.INSTANCE.onClientTick(this.minecraft.getFps());
      this.environmentAttributes().invalidateTickCache();
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
      this.tickingEntities.forEach((var1) -> {
         if (!var1.isRemoved() && !var1.isPassenger() && !this.tickRateManager.isEntityFrozen(var1)) {
            this.guardEntityTick(this::tickNonPassenger, var1);
         }
      });
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
      Profiler.get().push((Supplier)(() -> BuiltInRegistries.ENTITY_TYPE.getKey(var1.getType()).toString()));
      var1.tick();
      Profiler.get().pop();

      for(Entity var3 : var1.getPassengers()) {
         this.tickPassenger(var1, var3);
      }

   }

   private void tickPassenger(Entity var1, Entity var2) {
      if (!var2.isRemoved() && var2.getVehicle() == var1) {
         if (var2 instanceof Player || this.tickingEntities.contains(var2)) {
            var2.setOldPosAndRot();
            ++var2.tickCount;
            var2.rideTick();

            for(Entity var4 : var2.getPassengers()) {
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
      this.tintCaches.forEach((var1x, var2) -> var2.invalidateForChunk(var1.x, var1.z));
      this.entityStorage.startTicking(var1);
   }

   public void onSectionBecomingNonEmpty(long var1) {
      this.levelRenderer.onSectionBecomingNonEmpty(var1);
   }

   public void clearTintCaches() {
      this.tintCaches.forEach((var0, var1) -> var1.invalidateAll());
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

   public List<Entity> getPushableEntities(Entity var1, AABB var2) {
      LocalPlayer var3 = this.minecraft.player;
      return var3 != null && var3 != var1 && var3.getBoundingBox().intersects(var2) && EntitySelector.pushableBy(var1).test(var3) ? List.of(var3) : List.of();
   }

   public @Nullable Entity getEntity(int var1) {
      return (Entity)this.getEntities().get(var1);
   }

   public void disconnect(Component var1) {
      this.connection.getConnection().disconnect(var1);
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

   private @Nullable Block getMarkerParticleTarget() {
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
         for(AmbientParticle var17 : (List)this.environmentAttributes().getValue(EnvironmentAttributes.AMBIENT_PARTICLES, var7)) {
            if (var17.canSpawn(this.random)) {
               this.addParticle(var17.particle(), (double)var7.getX() + this.random.nextDouble(), (double)var7.getY() + this.random.nextDouble(), (double)var7.getZ() + this.random.nextDouble(), 0.0, 0.0, 0.0);
            }
         }
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
      var2.setDetail("Server brand", (CrashReportDetail)(() -> this.minecraft.player.connection.serverBrand()));
      var2.setDetail("Server type", (CrashReportDetail)(() -> this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server"));
      var2.setDetail("Tracked entity count", (CrashReportDetail)(() -> String.valueOf(this.getEntityCount())));
      return var2;
   }

   public void playSeededSound(@Nullable Entity var1, double var2, double var4, double var6, Holder<SoundEvent> var8, SoundSource var9, float var10, float var11, long var12) {
      if (var1 == this.minecraft.player) {
         this.playSound(var2, var4, var6, (SoundEvent)var8.value(), var9, var10, var11, false, var12);
      }

   }

   public void playSeededSound(@Nullable Entity var1, Entity var2, Holder<SoundEvent> var3, SoundSource var4, float var5, float var6, long var7) {
      if (var1 == this.minecraft.player) {
         this.minecraft.getSoundManager().play(new EntityBoundSoundInstance((SoundEvent)var3.value(), var4, var5, var6, var2, var7));
      }

   }

   public void playLocalSound(Entity var1, SoundEvent var2, SoundSource var3, float var4, float var5) {
      this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(var2, var3, var4, var5, var1, this.random.nextLong()));
   }

   public void playPlayerSound(SoundEvent var1, SoundSource var2, float var3, float var4) {
      if (this.minecraft.player != null) {
         this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(var1, var2, var3, var4, this.minecraft.player, this.random.nextLong()));
      }

   }

   public void playLocalSound(double var1, double var3, double var5, SoundEvent var7, SoundSource var8, float var9, float var10, boolean var11) {
      this.playSound(var1, var3, var5, var7, var8, var9, var10, var11, this.random.nextLong());
   }

   private void playSound(double var1, double var3, double var5, SoundEvent var7, SoundSource var8, float var9, float var10, boolean var11, long var12) {
      double var14 = this.minecraft.gameRenderer.getMainCamera().position().distanceToSqr(var1, var3, var5);
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

   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public RecipeAccess recipeAccess() {
      return this.connection.recipes();
   }

   public TickRateManager tickRateManager() {
      return this.tickRateManager;
   }

   public EnvironmentAttributeSystem environmentAttributes() {
      return this.environmentAttributes;
   }

   public LevelTickAccess<Block> getBlockTicks() {
      return BlackholeTickAccess.<Block>emptyLevelList();
   }

   public LevelTickAccess<Fluid> getFluidTicks() {
      return BlackholeTickAccess.<Fluid>emptyLevelList();
   }

   public ClientChunkCache getChunkSource() {
      return this.chunkSource;
   }

   public @Nullable MapItemSavedData getMapData(MapId var1) {
      return (MapItemSavedData)this.mapData.get(var1);
   }

   public void overrideMapData(MapId var1, MapItemSavedData var2) {
      this.mapData.put(var1, var2);
   }

   public Scoreboard getScoreboard() {
      return this.connection.scoreboard();
   }

   public void sendBlockUpdated(BlockPos var1, BlockState var2, BlockState var3, @Block.UpdateFlags int var4) {
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

   public void levelEvent(@Nullable Entity var1, int var2, BlockPos var3, int var4) {
      try {
         this.levelEventHandler.levelEvent(var2, var3, var4);
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

   public void addParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.doAddParticle(var1, var1.getType().getOverrideLimiter(), false, var2, var4, var6, var8, var10, var12);
   }

   public void addParticle(ParticleOptions var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      this.doAddParticle(var1, var1.getType().getOverrideLimiter() || var2, var3, var4, var6, var8, var10, var12, var14);
   }

   public void addAlwaysVisibleParticle(ParticleOptions var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.doAddParticle(var1, false, true, var2, var4, var6, var8, var10, var12);
   }

   public void addAlwaysVisibleParticle(ParticleOptions var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.doAddParticle(var1, var1.getType().getOverrideLimiter() || var2, true, var3, var5, var7, var9, var11, var13);
   }

   private void doAddParticle(ParticleOptions var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      try {
         Camera var16 = this.minecraft.gameRenderer.getMainCamera();
         ParticleStatus var20 = this.calculateParticleLevel(var3);
         if (var2) {
            this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
         } else if (!(var16.position().distanceToSqr(var4, var6, var8) > 1024.0)) {
            if (var20 != ParticleStatus.MINIMAL) {
               this.minecraft.particleEngine.createParticle(var1, var4, var6, var8, var10, var12, var14);
            }
         }
      } catch (Throwable var19) {
         CrashReport var17 = CrashReport.forThrowable(var19, "Exception while adding particle");
         CrashReportCategory var18 = var17.addCategory("Particle being added");
         var18.setDetail("ID", BuiltInRegistries.PARTICLE_TYPE.getKey(var1.getType()));
         var18.setDetail("Parameters", (CrashReportDetail)(() -> ParticleTypes.CODEC.encodeStart(this.registryAccess().createSerializationContext(NbtOps.INSTANCE), var1).toString()));
         var18.setDetail("Position", (CrashReportDetail)(() -> CrashReportCategory.formatLocation(this, var4, var6, var8)));
         throw new ReportedException(var17);
      }
   }

   private ParticleStatus calculateParticleLevel(boolean var1) {
      ParticleStatus var2 = (ParticleStatus)this.minecraft.options.particles().get();
      if (var1 && var2 == ParticleStatus.MINIMAL && this.random.nextInt(10) == 0) {
         var2 = ParticleStatus.DECREASED;
      }

      if (var2 == ParticleStatus.DECREASED && this.random.nextInt(3) == 0) {
         var2 = ParticleStatus.MINIMAL;
      }

      return var2;
   }

   public List<AbstractClientPlayer> players() {
      return this.players;
   }

   public List<EnderDragonPart> dragonParts() {
      return this.dragonParts;
   }

   public Holder<Biome> getUncachedNoiseBiome(int var1, int var2, int var3) {
      return this.registryAccess().lookupOrThrow(Registries.BIOME).getOrThrow(Biomes.PLAINS);
   }

   private int getSkyFlashTime() {
      return (Boolean)this.minecraft.options.hideLightningFlash().get() ? 0 : this.skyFlashTime;
   }

   public void setSkyFlashTime(int var1) {
      this.skyFlashTime = var1;
   }

   public float getShade(Direction var1, boolean var2) {
      DimensionType.CardinalLightType var3 = this.dimensionType().cardinalLightType();
      if (!var2) {
         return var3 == DimensionType.CardinalLightType.NETHER ? 0.9F : 1.0F;
      } else {
         float var10000;
         switch (var1) {
            case DOWN:
               var10000 = var3 == DimensionType.CardinalLightType.NETHER ? 0.9F : 0.5F;
               break;
            case UP:
               var10000 = var3 == DimensionType.CardinalLightType.NETHER ? 0.9F : 1.0F;
               break;
            case NORTH:
            case SOUTH:
               var10000 = 0.8F;
               break;
            case WEST:
            case EAST:
               var10000 = 0.6F;
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
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

   public void setRespawnData(LevelData.RespawnData var1) {
      this.levelData.setSpawn(this.getWorldBorderAdjustedRespawnData(var1));
   }

   public LevelData.RespawnData getRespawnData() {
      return this.levelData.getRespawnData();
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
      if (!var2.isAir() && var2.shouldSpawnTerrainParticles()) {
         VoxelShape var3 = var2.getShape(this, var1);
         double var4 = 0.25;
         var3.forAllBoxes((var3x, var5, var7, var9, var11, var13) -> {
            double var15 = Math.min(1.0, var9 - var3x);
            double var17 = Math.min(1.0, var11 - var5);
            double var19 = Math.min(1.0, var13 - var7);
            int var21 = Math.max(2, Mth.ceil(var15 / 0.25));
            int var22 = Math.max(2, Mth.ceil(var17 / 0.25));
            int var23 = Math.max(2, Mth.ceil(var19 / 0.25));

            for(int var24 = 0; var24 < var21; ++var24) {
               for(int var25 = 0; var25 < var22; ++var25) {
                  for(int var26 = 0; var26 < var23; ++var26) {
                     double var27 = ((double)var24 + 0.5) / (double)var21;
                     double var29 = ((double)var25 + 0.5) / (double)var22;
                     double var31 = ((double)var26 + 0.5) / (double)var23;
                     double var33 = var27 * var15 + var3x;
                     double var35 = var29 * var17 + var5;
                     double var37 = var31 * var19 + var7;
                     this.minecraft.particleEngine.add(new TerrainParticle(this, (double)var1.getX() + var33, (double)var1.getY() + var35, (double)var1.getZ() + var37, var27 - 0.5, var29 - 0.5, var31 - 0.5, var2, var1));
                  }
               }
            }

         });
      }
   }

   public void addBreakingBlockEffect(BlockPos var1, Direction var2) {
      BlockState var3 = this.getBlockState(var1);
      if (var3.getRenderShape() != RenderShape.INVISIBLE && var3.shouldSpawnTerrainParticles()) {
         int var4 = var1.getX();
         int var5 = var1.getY();
         int var6 = var1.getZ();
         float var7 = 0.1F;
         AABB var8 = var3.getShape(this, var1).bounds();
         double var9 = (double)var4 + this.random.nextDouble() * (var8.maxX - var8.minX - 0.20000000298023224) + 0.10000000149011612 + var8.minX;
         double var11 = (double)var5 + this.random.nextDouble() * (var8.maxY - var8.minY - 0.20000000298023224) + 0.10000000149011612 + var8.minY;
         double var13 = (double)var6 + this.random.nextDouble() * (var8.maxZ - var8.minZ - 0.20000000298023224) + 0.10000000149011612 + var8.minZ;
         if (var2 == Direction.DOWN) {
            var11 = (double)var5 + var8.minY - 0.10000000149011612;
         }

         if (var2 == Direction.UP) {
            var11 = (double)var5 + var8.maxY + 0.10000000149011612;
         }

         if (var2 == Direction.NORTH) {
            var13 = (double)var6 + var8.minZ - 0.10000000149011612;
         }

         if (var2 == Direction.SOUTH) {
            var13 = (double)var6 + var8.maxZ + 0.10000000149011612;
         }

         if (var2 == Direction.WEST) {
            var9 = (double)var4 + var8.minX - 0.10000000149011612;
         }

         if (var2 == Direction.EAST) {
            var9 = (double)var4 + var8.maxX + 0.10000000149011612;
         }

         this.minecraft.particleEngine.add((new TerrainParticle(this, var9, var11, var13, 0.0, 0.0, 0.0, var3, var1)).setPower(0.2F).scale(0.6F));
      }
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

   public void explode(@Nullable Entity var1, @Nullable DamageSource var2, @Nullable ExplosionDamageCalculator var3, double var4, double var6, double var8, float var10, boolean var11, Level.ExplosionInteraction var12, ParticleOptions var13, ParticleOptions var14, WeightedList<ExplosionParticleInfo> var15, Holder<SoundEvent> var16) {
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   public int getClientLeafTintColor(BlockPos var1) {
      return Minecraft.getInstance().getBlockColors().getColor(this.getBlockState(var1), this, var1, 0);
   }

   public void registerForCleaning(CacheSlot<ClientLevel, ?> var1) {
      this.connection.registerForCleaning(var1);
   }

   public void trackExplosionEffects(Vec3 var1, float var2, int var3, WeightedList<ExplosionParticleInfo> var4) {
      this.explosionTracker.track(var1, var2, var3, var4);
   }

   // $FF: synthetic method
   public LevelData getLevelData() {
      return this.getLevelData();
   }

   // $FF: synthetic method
   public Collection dragonParts() {
      return this.dragonParts();
   }

   // $FF: synthetic method
   public ChunkSource getChunkSource() {
      return this.getChunkSource();
   }

   // $FF: synthetic method
   public EnvironmentAttributeReader environmentAttributes() {
      return this.environmentAttributes();
   }

   static {
      MARKER_PARTICLE_ITEMS = Set.of(Items.BARRIER, Items.LIGHT);
   }

   public static class ClientLevelData implements WritableLevelData {
      private final boolean hardcore;
      private final boolean isFlat;
      private LevelData.RespawnData respawnData;
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

      public LevelData.RespawnData getRespawnData() {
         return this.respawnData;
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

      public void setSpawn(LevelData.RespawnData var1) {
         this.respawnData = var1;
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

      public float voidDarknessOnsetRange() {
         return this.isFlat ? 1.0F : 32.0F;
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
         Objects.requireNonNull(var1);
         byte var3 = 0;
         //$FF: var3->value
         //0->net/minecraft/client/player/AbstractClientPlayer
         //1->net/minecraft/world/entity/boss/enderdragon/EnderDragon
         switch (var1.typeSwitch<invokedynamic>(var1, var3)) {
            case 0:
               AbstractClientPlayer var4 = (AbstractClientPlayer)var1;
               ClientLevel.this.players.add(var4);
               break;
            case 1:
               EnderDragon var5 = (EnderDragon)var1;
               ClientLevel.this.dragonParts.addAll(Arrays.asList(var5.getSubEntities()));
         }

      }

      public void onTrackingEnd(Entity var1) {
         var1.unRide();
         Objects.requireNonNull(var1);
         byte var3 = 0;
         //$FF: var3->value
         //0->net/minecraft/client/player/AbstractClientPlayer
         //1->net/minecraft/world/entity/boss/enderdragon/EnderDragon
         switch (var1.typeSwitch<invokedynamic>(var1, var3)) {
            case 0:
               AbstractClientPlayer var4 = (AbstractClientPlayer)var1;
               ClientLevel.this.players.remove(var4);
               break;
            case 1:
               EnderDragon var5 = (EnderDragon)var1;
               ClientLevel.this.dragonParts.removeAll(Arrays.asList(var5.getSubEntities()));
         }

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
}
