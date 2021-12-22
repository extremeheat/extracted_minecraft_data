package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.PrioritizeChunkUpdates;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagContainer;
import net.minecraft.util.CubicSampler;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.BlackholeTickAccess;
import net.minecraft.world.ticks.LevelTickAccess;

public class ClientLevel extends Level {
   private static final double FLUID_PARTICLE_SPAWN_OFFSET = 0.05D;
   private static final int NORMAL_LIGHT_UPDATES_PER_FRAME = 10;
   private static final int LIGHT_UPDATE_QUEUE_SIZE_THRESHOLD = 1000;
   final EntityTickList tickingEntities = new EntityTickList();
   private final TransientEntitySectionManager<Entity> entityStorage = new TransientEntitySectionManager(Entity.class, new ClientLevel.EntityCallbacks());
   private final ClientPacketListener connection;
   private final LevelRenderer levelRenderer;
   private final ClientLevel.ClientLevelData clientLevelData;
   private final DimensionSpecialEffects effects;
   private final Minecraft minecraft = Minecraft.getInstance();
   final List<AbstractClientPlayer> players = Lists.newArrayList();
   private Scoreboard scoreboard = new Scoreboard();
   private final Map<String, MapItemSavedData> mapData = Maps.newHashMap();
   private static final long CLOUD_COLOR = 16777215L;
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
   private static final Set<Item> MARKER_PARTICLE_ITEMS;

   public ClientLevel(ClientPacketListener var1, ClientLevel.ClientLevelData var2, ResourceKey<Level> var3, DimensionType var4, int var5, int var6, Supplier<ProfilerFiller> var7, LevelRenderer var8, boolean var9, long var10) {
      super(var2, var3, var4, var7, true, var9, var10);
      this.connection = var1;
      this.chunkSource = new ClientChunkCache(this, var5);
      this.clientLevelData = var2;
      this.levelRenderer = var8;
      this.effects = DimensionSpecialEffects.forType(var4);
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

   public boolean isLightUpdateQueueEmpty() {
      return this.lightUpdateQueue.isEmpty();
   }

   public DimensionSpecialEffects effects() {
      return this.effects;
   }

   public void tick(BooleanSupplier var1) {
      this.getWorldBorder().tick();
      this.tickTime();
      this.getProfiler().push("blocks");
      this.chunkSource.tick(var1);
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
         ((GameRules.BooleanValue)this.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(false, (MinecraftServer)null);
      } else {
         ((GameRules.BooleanValue)this.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(true, (MinecraftServer)null);
      }

      this.clientLevelData.setDayTime(var1);
   }

   public Iterable<Entity> entitiesForRendering() {
      return this.getEntities().getAll();
   }

   public void tickEntities() {
      ProfilerFiller var1 = this.getProfiler();
      var1.push("entities");
      this.tickingEntities.forEach((var1x) -> {
         if (!var1x.isRemoved() && !var1x.isPassenger()) {
            this.guardEntityTick(this::tickNonPassenger, var1x);
         }
      });
      var1.pop();
      this.tickBlockEntities();
   }

   public boolean shouldTickDeath(Entity var1) {
      return var1.chunkPosition().getChessboardDistance(this.minecraft.player.chunkPosition()) <= this.serverSimulationDistance;
   }

   public void tickNonPassenger(Entity var1) {
      var1.setOldPosAndRot();
      ++var1.tickCount;
      this.getProfiler().push(() -> {
         return Registry.ENTITY_TYPE.getKey(var1.getType()).toString();
      });
      var1.tick();
      this.getProfiler().pop();
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
      this.chunkSource.getLightEngine().enableLightSources(var1.getPos(), false);
      this.entityStorage.stopTicking(var1.getPos());
   }

   public void onChunkLoaded(ChunkPos var1) {
      this.tintCaches.forEach((var1x, var2) -> {
         var2.invalidateForChunk(var1.field_504, var1.field_505);
      });
      this.entityStorage.startTicking(var1);
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

   public void addPlayer(int var1, AbstractClientPlayer var2) {
      this.addEntity(var1, var2);
   }

   public void putNonPlayerEntity(int var1, Entity var2) {
      this.addEntity(var1, var2);
   }

   private void addEntity(int var1, Entity var2) {
      this.removeEntity(var1, Entity.RemovalReason.DISCARDED);
      this.entityStorage.addEntity(var2);
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

   public void setKnownState(BlockPos var1, BlockState var2) {
      this.setBlock(var1, var2, 19);
   }

   public void disconnect() {
      this.connection.getConnection().disconnect(new TranslatableComponent("multiplayer.status.quitting"));
   }

   public void animateTick(int var1, int var2, int var3) {
      boolean var4 = true;
      Random var5 = new Random();
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

   public void doAnimateTick(int var1, int var2, int var3, int var4, Random var5, @Nullable Block var6, BlockPos.MutableBlockPos var7) {
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
         this.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, var11), (double)var8 + 0.5D, (double)var9 + 0.5D, (double)var10 + 0.5D, 0.0D, 0.0D, 0.0D);
      }

      if (!var11.isCollisionShapeFullBlock(this, var7)) {
         this.getBiome(var7).getAmbientParticle().ifPresent((var2x) -> {
            if (var2x.canSpawn(this.random)) {
               this.addParticle(var2x.getOptions(), (double)var7.getX() + this.random.nextDouble(), (double)var7.getY() + this.random.nextDouble(), (double)var7.getZ() + this.random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }

         });
      }

   }

   private void trySpawnDripParticles(BlockPos var1, BlockState var2, ParticleOptions var3, boolean var4) {
      if (var2.getFluidState().isEmpty()) {
         VoxelShape var5 = var2.getCollisionShape(this, var1);
         double var6 = var5.max(Direction.Axis.field_501);
         if (var6 < 1.0D) {
            if (var4) {
               this.spawnFluidParticle((double)var1.getX(), (double)(var1.getX() + 1), (double)var1.getZ(), (double)(var1.getZ() + 1), (double)(var1.getY() + 1) - 0.05D, var3);
            }
         } else if (!var2.is(BlockTags.IMPERMEABLE)) {
            double var8 = var5.min(Direction.Axis.field_501);
            if (var8 > 0.0D) {
               this.spawnParticle(var1, var3, var5, (double)var1.getY() + var8 - 0.05D);
            } else {
               BlockPos var10 = var1.below();
               BlockState var11 = this.getBlockState(var10);
               VoxelShape var12 = var11.getCollisionShape(this, var10);
               double var13 = var12.max(Direction.Axis.field_501);
               if (var13 < 1.0D && var11.getFluidState().isEmpty()) {
                  this.spawnParticle(var1, var3, var5, (double)var1.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void spawnParticle(BlockPos var1, ParticleOptions var2, VoxelShape var3, double var4) {
      this.spawnFluidParticle((double)var1.getX() + var3.min(Direction.Axis.field_500), (double)var1.getX() + var3.max(Direction.Axis.field_500), (double)var1.getZ() + var3.min(Direction.Axis.field_502), (double)var1.getZ() + var3.max(Direction.Axis.field_502), var4, var2);
   }

   private void spawnFluidParticle(double var1, double var3, double var5, double var7, double var9, ParticleOptions var11) {
      this.addParticle(var11, Mth.lerp(this.random.nextDouble(), var1, var3), var9, Mth.lerp(this.random.nextDouble(), var5, var7), 0.0D, 0.0D, 0.0D);
   }

   public CrashReportCategory fillReportDetails(CrashReport var1) {
      CrashReportCategory var2 = super.fillReportDetails(var1);
      var2.setDetail("Server brand", () -> {
         return this.minecraft.player.getServerBrand();
      });
      var2.setDetail("Server type", () -> {
         return this.minecraft.getSingleplayerServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
      });
      return var2;
   }

   public void playSound(@Nullable Player var1, double var2, double var4, double var6, SoundEvent var8, SoundSource var9, float var10, float var11) {
      if (var1 == this.minecraft.player) {
         this.playLocalSound(var2, var4, var6, var8, var9, var10, var11, false);
      }

   }

   public void playSound(@Nullable Player var1, Entity var2, SoundEvent var3, SoundSource var4, float var5, float var6) {
      if (var1 == this.minecraft.player) {
         this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(var3, var4, var5, var6, var2));
      }

   }

   public void playLocalSound(BlockPos var1, SoundEvent var2, SoundSource var3, float var4, float var5, boolean var6) {
      this.playLocalSound((double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D, var2, var3, var4, var5, var6);
   }

   public void playLocalSound(double var1, double var3, double var5, SoundEvent var7, SoundSource var8, float var9, float var10, boolean var11) {
      double var12 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(var1, var3, var5);
      SimpleSoundInstance var14 = new SimpleSoundInstance(var7, var8, var9, var10, var1, var3, var5);
      if (var11 && var12 > 100.0D) {
         double var15 = Math.sqrt(var12) / 40.0D;
         this.minecraft.getSoundManager().playDelayed(var14, (int)(var15 * 20.0D));
      } else {
         this.minecraft.getSoundManager().play(var14);
      }

   }

   public void createFireworks(double var1, double var3, double var5, double var7, double var9, double var11, @Nullable CompoundTag var13) {
      this.minecraft.particleEngine.add(new FireworkParticles.Starter(this, var1, var3, var5, var7, var9, var11, this.minecraft.particleEngine, var13));
   }

   public void sendPacketToServer(Packet<?> var1) {
      this.connection.send(var1);
   }

   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   public void setScoreboard(Scoreboard var1) {
      this.scoreboard = var1;
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
   public MapItemSavedData getMapData(String var1) {
      return (MapItemSavedData)this.mapData.get(var1);
   }

   public void setMapData(String var1, MapItemSavedData var2) {
      this.mapData.put(var1, var2);
   }

   public int getFreeMapId() {
      return 0;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public TagContainer getTagManager() {
      return this.connection.getTags();
   }

   public RegistryAccess registryAccess() {
      return this.connection.registryAccess();
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

   public void setLightReady(int var1, int var2) {
      LevelChunk var3 = this.chunkSource.getChunk(var1, var2, false);
      if (var3 != null) {
         var3.setClientLightReady(true);
      }

   }

   public void destroyBlockProgress(int var1, BlockPos var2, int var3) {
      this.levelRenderer.destroyBlockProgress(var1, var2, var3);
   }

   public void globalLevelEvent(int var1, BlockPos var2, int var3) {
      this.levelRenderer.globalLevelEvent(var1, var2, var3);
   }

   public void levelEvent(@Nullable Player var1, int var2, BlockPos var3, int var4) {
      try {
         this.levelRenderer.levelEvent(var1, var2, var3, var4);
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

   public Biome getUncachedNoiseBiome(int var1, int var2, int var3) {
      return (Biome)this.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOrThrow(Biomes.PLAINS);
   }

   public float getSkyDarken(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.2F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      var3 = 1.0F - var3;
      var3 = (float)((double)var3 * (1.0D - (double)(this.getRainLevel(var1) * 5.0F) / 16.0D));
      var3 = (float)((double)var3 * (1.0D - (double)(this.getThunderLevel(var1) * 5.0F) / 16.0D));
      return var3 * 0.8F + 0.2F;
   }

   public Vec3 getSkyColor(Vec3 var1, float var2) {
      float var3 = this.getTimeOfDay(var2);
      Vec3 var4 = var1.subtract(2.0D, 2.0D, 2.0D).scale(0.25D);
      BiomeManager var5 = this.getBiomeManager();
      Vec3 var6 = CubicSampler.gaussianSampleVec3(var4, (var1x, var2x, var3x) -> {
         return Vec3.fromRGB24(var5.getNoiseBiomeAtQuart(var1x, var2x, var3x).getSkyColor());
      });
      float var7 = Mth.cos(var3 * 6.2831855F) * 2.0F + 0.5F;
      var7 = Mth.clamp(var7, 0.0F, 1.0F);
      float var8 = (float)var6.field_414 * var7;
      float var9 = (float)var6.field_415 * var7;
      float var10 = (float)var6.field_416 * var7;
      float var11 = this.getRainLevel(var2);
      float var12;
      float var13;
      if (var11 > 0.0F) {
         var12 = (var8 * 0.3F + var9 * 0.59F + var10 * 0.11F) * 0.6F;
         var13 = 1.0F - var11 * 0.75F;
         var8 = var8 * var13 + var12 * (1.0F - var13);
         var9 = var9 * var13 + var12 * (1.0F - var13);
         var10 = var10 * var13 + var12 * (1.0F - var13);
      }

      var12 = this.getThunderLevel(var2);
      if (var12 > 0.0F) {
         var13 = (var8 * 0.3F + var9 * 0.59F + var10 * 0.11F) * 0.2F;
         float var14 = 1.0F - var12 * 0.75F;
         var8 = var8 * var14 + var13 * (1.0F - var14);
         var9 = var9 * var14 + var13 * (1.0F - var14);
         var10 = var10 * var14 + var13 * (1.0F - var14);
      }

      if (!this.minecraft.options.hideLightningFlashes && this.skyFlashTime > 0) {
         var13 = (float)this.skyFlashTime - var2;
         if (var13 > 1.0F) {
            var13 = 1.0F;
         }

         var13 *= 0.45F;
         var8 = var8 * (1.0F - var13) + 0.8F * var13;
         var9 = var9 * (1.0F - var13) + 0.8F * var13;
         var10 = var10 * (1.0F - var13) + 1.0F * var13;
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
      float var8;
      float var9;
      if (var7 > 0.0F) {
         var8 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.6F;
         var9 = 1.0F - var7 * 0.95F;
         var4 = var4 * var9 + var8 * (1.0F - var9);
         var5 = var5 * var9 + var8 * (1.0F - var9);
         var6 = var6 * var9 + var8 * (1.0F - var9);
      }

      var4 *= var3 * 0.9F + 0.1F;
      var5 *= var3 * 0.9F + 0.1F;
      var6 *= var3 * 0.85F + 0.15F;
      var8 = this.getThunderLevel(var1);
      if (var8 > 0.0F) {
         var9 = (var4 * 0.3F + var5 * 0.59F + var6 * 0.11F) * 0.2F;
         float var10 = 1.0F - var8 * 0.95F;
         var4 = var4 * var10 + var9 * (1.0F - var10);
         var5 = var5 * var10 + var9 * (1.0F - var10);
         var6 = var6 * var10 + var9 * (1.0F - var10);
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
      return this.skyFlashTime;
   }

   public void setSkyFlashTime(int var1) {
      this.skyFlashTime = var1;
   }

   public float getShade(Direction var1, boolean var2) {
      boolean var3 = this.effects().constantAmbientLight();
      if (!var2) {
         return var3 ? 0.9F : 1.0F;
      } else {
         switch(var1) {
         case DOWN:
            return var3 ? 0.9F : 0.5F;
         case field_526:
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
      int var3 = Minecraft.getInstance().options.biomeBlendRadius;
      if (var3 == 0) {
         return var2.getColor(this.getBiome(var1), (double)var1.getX(), (double)var1.getZ());
      } else {
         int var4 = (var3 * 2 + 1) * (var3 * 2 + 1);
         int var5 = 0;
         int var6 = 0;
         int var7 = 0;
         Cursor3D var8 = new Cursor3D(var1.getX() - var3, var1.getY(), var1.getZ() - var3, var1.getX() + var3, var1.getY(), var1.getZ() + var3);

         int var10;
         for(BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(); var8.advance(); var7 += var10 & 255) {
            var9.set(var8.nextX(), var8.nextY(), var8.nextZ());
            var10 = var2.getColor(this.getBiome(var9), (double)var9.getX(), (double)var9.getZ());
            var5 += (var10 & 16711680) >> 16;
            var6 += (var10 & '\uff00') >> 8;
         }

         return (var5 / var4 & 255) << 16 | (var6 / var4 & 255) << 8 | var7 / var4 & 255;
      }
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

   public void setDefaultSpawnPos(BlockPos var1, float var2) {
      this.levelData.setSpawn(var1, var2);
   }

   public String toString() {
      return "ClientLevel";
   }

   public ClientLevel.ClientLevelData getLevelData() {
      return this.clientLevelData;
   }

   public void gameEvent(@Nullable Entity var1, GameEvent var2, BlockPos var3) {
   }

   protected Map<String, MapItemSavedData> getAllMapData() {
      return ImmutableMap.copyOf(this.mapData);
   }

   protected void addMapData(Map<String, MapItemSavedData> var1) {
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

   public boolean shouldDelayFallingBlockEntityRemoval(Entity.RemovalReason var1) {
      return var1 == Entity.RemovalReason.DISCARDED && this.minecraft.options.prioritizeChunkUpdates != PrioritizeChunkUpdates.NEARBY;
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

      // $FF: synthetic method
      public void onTrackingEnd(Object var1) {
         this.onTrackingEnd((Entity)var1);
      }

      // $FF: synthetic method
      public void onTrackingStart(Object var1) {
         this.onTrackingStart((Entity)var1);
      }

      // $FF: synthetic method
      public void onTickingEnd(Object var1) {
         this.onTickingEnd((Entity)var1);
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

   public static class ClientLevelData implements WritableLevelData {
      private final boolean hardcore;
      private final GameRules gameRules;
      private final boolean isFlat;
      private int xSpawn;
      private int ySpawn;
      private int zSpawn;
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

      public int getXSpawn() {
         return this.xSpawn;
      }

      public int getYSpawn() {
         return this.ySpawn;
      }

      public int getZSpawn() {
         return this.zSpawn;
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

      public void setXSpawn(int var1) {
         this.xSpawn = var1;
      }

      public void setYSpawn(int var1) {
         this.ySpawn = var1;
      }

      public void setZSpawn(int var1) {
         this.zSpawn = var1;
      }

      public void setSpawnAngle(float var1) {
         this.spawnAngle = var1;
      }

      public void setGameTime(long var1) {
         this.gameTime = var1;
      }

      public void setDayTime(long var1) {
         this.dayTime = var1;
      }

      public void setSpawn(BlockPos var1, float var2) {
         this.xSpawn = var1.getX();
         this.ySpawn = var1.getY();
         this.zSpawn = var1.getZ();
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

      public GameRules getGameRules() {
         return this.gameRules;
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
         return this.isFlat ? (double)var1.getMinBuildHeight() : 63.0D;
      }

      public double getClearColorScale() {
         return this.isFlat ? 1.0D : 0.03125D;
      }
   }
}
