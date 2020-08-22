package net.minecraft.client.multiplayer;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.particle.FireworkParticles;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.EmptyTickList;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.TickList;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.scores.Scoreboard;

public class ClientLevel extends Level {
   private final List globalEntities = Lists.newArrayList();
   private final Int2ObjectMap entitiesById = new Int2ObjectOpenHashMap();
   private final ClientPacketListener connection;
   private final LevelRenderer levelRenderer;
   private final Minecraft minecraft = Minecraft.getInstance();
   private final List players = Lists.newArrayList();
   private int delayUntilNextMoodSound;
   private Scoreboard scoreboard;
   private final Map mapData;
   private int skyFlashTime;
   private final Object2ObjectArrayMap tintCaches;

   public ClientLevel(ClientPacketListener var1, LevelSettings var2, DimensionType var3, int var4, ProfilerFiller var5, LevelRenderer var6) {
      super(new LevelData(var2, "MpServer"), var3, (var1x, var2x) -> {
         return new ClientChunkCache((ClientLevel)var1x, var4);
      }, var5, true);
      this.delayUntilNextMoodSound = this.random.nextInt(12000);
      this.scoreboard = new Scoreboard();
      this.mapData = Maps.newHashMap();
      this.tintCaches = (Object2ObjectArrayMap)Util.make(new Object2ObjectArrayMap(3), (var0) -> {
         var0.put(BiomeColors.GRASS_COLOR_RESOLVER, new BlockTintCache());
         var0.put(BiomeColors.FOLIAGE_COLOR_RESOLVER, new BlockTintCache());
         var0.put(BiomeColors.WATER_COLOR_RESOLVER, new BlockTintCache());
      });
      this.connection = var1;
      this.levelRenderer = var6;
      this.setSpawnPos(new BlockPos(8, 64, 8));
      this.updateSkyBrightness();
      this.prepareWeather();
   }

   public void tick(BooleanSupplier var1) {
      this.getWorldBorder().tick();
      this.tickTime();
      this.getProfiler().push("blocks");
      this.chunkSource.tick(var1);
      this.playMoodSounds();
      this.getProfiler().pop();
   }

   public Iterable entitiesForRendering() {
      return Iterables.concat(this.entitiesById.values(), this.globalEntities);
   }

   public void tickEntities() {
      ProfilerFiller var1 = this.getProfiler();
      var1.push("entities");
      var1.push("global");

      for(int var2 = 0; var2 < this.globalEntities.size(); ++var2) {
         Entity var3 = (Entity)this.globalEntities.get(var2);
         this.guardEntityTick((var0) -> {
            ++var0.tickCount;
            var0.tick();
         }, var3);
         if (var3.removed) {
            this.globalEntities.remove(var2--);
         }
      }

      var1.popPush("regular");
      ObjectIterator var5 = this.entitiesById.int2ObjectEntrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         Entity var4 = (Entity)var6.getValue();
         if (!var4.isPassenger()) {
            var1.push("tick");
            if (!var4.removed) {
               this.guardEntityTick(this::tickNonPassenger, var4);
            }

            var1.pop();
            var1.push("remove");
            if (var4.removed) {
               var5.remove();
               this.onEntityRemoved(var4);
            }

            var1.pop();
         }
      }

      var1.pop();
      this.tickBlockEntities();
      var1.pop();
   }

   public void tickNonPassenger(Entity var1) {
      if (var1 instanceof Player || this.getChunkSource().isEntityTickingChunk(var1)) {
         var1.setPosAndOldPos(var1.getX(), var1.getY(), var1.getZ());
         var1.yRotO = var1.yRot;
         var1.xRotO = var1.xRot;
         if (var1.inChunk || var1.isSpectator()) {
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
            var2.setPosAndOldPos(var2.getX(), var2.getY(), var2.getZ());
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
      int var2 = Mth.floor(var1.getX() / 16.0D);
      int var3 = Mth.floor(var1.getY() / 16.0D);
      int var4 = Mth.floor(var1.getZ() / 16.0D);
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

   public void unload(LevelChunk var1) {
      this.blockEntitiesToUnload.addAll(var1.getBlockEntities().values());
      this.chunkSource.getLightEngine().enableLightSources(var1.getPos(), false);
   }

   public void onChunkLoaded(int var1, int var2) {
      this.tintCaches.forEach((var2x, var3) -> {
         var3.invalidateForChunk(var1, var2);
      });
   }

   public void clearTintCaches() {
      this.tintCaches.forEach((var0, var1) -> {
         var1.invalidateAll();
      });
   }

   public boolean hasChunk(int var1, int var2) {
      return true;
   }

   private void playMoodSounds() {
      if (this.minecraft.player != null) {
         if (this.delayUntilNextMoodSound > 0) {
            --this.delayUntilNextMoodSound;
         } else {
            BlockPos var1 = new BlockPos(this.minecraft.player);
            BlockPos var2 = var1.offset(4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1), 4 * (this.random.nextInt(3) - 1));
            double var3 = var1.distSqr(var2);
            if (var3 >= 4.0D && var3 <= 256.0D) {
               BlockState var5 = this.getBlockState(var2);
               if (var5.isAir() && this.getRawBrightness(var2, 0) <= this.random.nextInt(8) && this.getBrightness(LightLayer.SKY, var2) <= 0) {
                  this.playLocalSound((double)var2.getX() + 0.5D, (double)var2.getY() + 0.5D, (double)var2.getZ() + 0.5D, SoundEvents.AMBIENT_CAVE, SoundSource.AMBIENT, 0.7F, 0.8F + this.random.nextFloat() * 0.2F, false);
                  this.delayUntilNextMoodSound = this.random.nextInt(12000) + 6000;
               }
            }

         }
      }
   }

   public int getEntityCount() {
      return this.entitiesById.size();
   }

   public void addLightning(LightningBolt var1) {
      this.globalEntities.add(var1);
   }

   public void addPlayer(int var1, AbstractClientPlayer var2) {
      this.addEntity(var1, var2);
      this.players.add(var2);
   }

   public void putNonPlayerEntity(int var1, Entity var2) {
      this.addEntity(var1, var2);
   }

   private void addEntity(int var1, Entity var2) {
      this.removeEntity(var1);
      this.entitiesById.put(var1, var2);
      this.getChunkSource().getChunk(Mth.floor(var2.getX() / 16.0D), Mth.floor(var2.getZ() / 16.0D), ChunkStatus.FULL, true).addEntity(var2);
   }

   public void removeEntity(int var1) {
      Entity var2 = (Entity)this.entitiesById.remove(var1);
      if (var2 != null) {
         var2.remove();
         this.onEntityRemoved(var2);
      }

   }

   private void onEntityRemoved(Entity var1) {
      var1.unRide();
      if (var1.inChunk) {
         this.getChunk(var1.xChunk, var1.zChunk).removeEntity(var1);
      }

      this.players.remove(var1);
   }

   public void reAddEntitiesToChunk(LevelChunk var1) {
      ObjectIterator var2 = this.entitiesById.int2ObjectEntrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Entity var4 = (Entity)var3.getValue();
         int var5 = Mth.floor(var4.getX() / 16.0D);
         int var6 = Mth.floor(var4.getZ() / 16.0D);
         if (var5 == var1.getPos().x && var6 == var1.getPos().z) {
            var1.addEntity(var4);
         }
      }

   }

   @Nullable
   public Entity getEntity(int var1) {
      return (Entity)this.entitiesById.get(var1);
   }

   public void setKnownState(BlockPos var1, BlockState var2) {
      this.setBlock(var1, var2, 19);
   }

   public void disconnect() {
      this.connection.getConnection().disconnect(new TranslatableComponent("multiplayer.status.quitting", new Object[0]));
   }

   public void animateTick(int var1, int var2, int var3) {
      boolean var4 = true;
      Random var5 = new Random();
      boolean var6 = false;
      if (this.minecraft.gameMode.getPlayerMode() == GameType.CREATIVE) {
         Iterator var7 = this.minecraft.player.getHandSlots().iterator();

         while(var7.hasNext()) {
            ItemStack var8 = (ItemStack)var7.next();
            if (var8.getItem() == Blocks.BARRIER.asItem()) {
               var6 = true;
               break;
            }
         }
      }

      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = 0; var10 < 667; ++var10) {
         this.doAnimateTick(var1, var2, var3, 16, var5, var6, var9);
         this.doAnimateTick(var1, var2, var3, 32, var5, var6, var9);
      }

   }

   public void doAnimateTick(int var1, int var2, int var3, int var4, Random var5, boolean var6, BlockPos.MutableBlockPos var7) {
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

      if (var6 && var11.getBlock() == Blocks.BARRIER) {
         this.addParticle(ParticleTypes.BARRIER, (double)var8 + 0.5D, (double)var9 + 0.5D, (double)var10 + 0.5D, 0.0D, 0.0D, 0.0D);
      }

   }

   private void trySpawnDripParticles(BlockPos var1, BlockState var2, ParticleOptions var3, boolean var4) {
      if (var2.getFluidState().isEmpty()) {
         VoxelShape var5 = var2.getCollisionShape(this, var1);
         double var6 = var5.max(Direction.Axis.Y);
         if (var6 < 1.0D) {
            if (var4) {
               this.spawnFluidParticle((double)var1.getX(), (double)(var1.getX() + 1), (double)var1.getZ(), (double)(var1.getZ() + 1), (double)(var1.getY() + 1) - 0.05D, var3);
            }
         } else if (!var2.is(BlockTags.IMPERMEABLE)) {
            double var8 = var5.min(Direction.Axis.Y);
            if (var8 > 0.0D) {
               this.spawnParticle(var1, var3, var5, (double)var1.getY() + var8 - 0.05D);
            } else {
               BlockPos var10 = var1.below();
               BlockState var11 = this.getBlockState(var10);
               VoxelShape var12 = var11.getCollisionShape(this, var10);
               double var13 = var12.max(Direction.Axis.Y);
               if (var13 < 1.0D && var11.getFluidState().isEmpty()) {
                  this.spawnParticle(var1, var3, var5, (double)var1.getY() - 0.05D);
               }
            }
         }

      }
   }

   private void spawnParticle(BlockPos var1, ParticleOptions var2, VoxelShape var3, double var4) {
      this.spawnFluidParticle((double)var1.getX() + var3.min(Direction.Axis.X), (double)var1.getX() + var3.max(Direction.Axis.X), (double)var1.getZ() + var3.min(Direction.Axis.Z), (double)var1.getZ() + var3.max(Direction.Axis.Z), var4, var2);
   }

   private void spawnFluidParticle(double var1, double var3, double var5, double var7, double var9, ParticleOptions var11) {
      this.addParticle(var11, Mth.lerp(this.random.nextDouble(), var1, var3), var9, Mth.lerp(this.random.nextDouble(), var5, var7), 0.0D, 0.0D, 0.0D);
   }

   public void removeAllPendingEntityRemovals() {
      ObjectIterator var1 = this.entitiesById.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         Entity var3 = (Entity)var2.getValue();
         if (var3.removed) {
            var1.remove();
            this.onEntityRemoved(var3);
         }
      }

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
         this.minecraft.getSoundManager().play(new EntityBoundSoundInstance(var3, var4, var2));
      }

   }

   public void playLocalSound(BlockPos var1, SoundEvent var2, SoundSource var3, float var4, float var5, boolean var6) {
      this.playLocalSound((double)var1.getX() + 0.5D, (double)var1.getY() + 0.5D, (double)var1.getZ() + 0.5D, var2, var3, var4, var5, var6);
   }

   public void playLocalSound(double var1, double var3, double var5, SoundEvent var7, SoundSource var8, float var9, float var10, boolean var11) {
      double var12 = this.minecraft.gameRenderer.getMainCamera().getPosition().distanceToSqr(var1, var3, var5);
      SimpleSoundInstance var14 = new SimpleSoundInstance(var7, var8, var9, var10, (float)var1, (float)var3, (float)var5);
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

   public void sendPacketToServer(Packet var1) {
      this.connection.send(var1);
   }

   public RecipeManager getRecipeManager() {
      return this.connection.getRecipeManager();
   }

   public void setScoreboard(Scoreboard var1) {
      this.scoreboard = var1;
   }

   public void setDayTime(long var1) {
      if (var1 < 0L) {
         var1 = -var1;
         ((GameRules.BooleanValue)this.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(false, (MinecraftServer)null);
      } else {
         ((GameRules.BooleanValue)this.getGameRules().getRule(GameRules.RULE_DAYLIGHT)).set(true, (MinecraftServer)null);
      }

      super.setDayTime(var1);
   }

   public TickList getBlockTicks() {
      return EmptyTickList.empty();
   }

   public TickList getLiquidTicks() {
      return EmptyTickList.empty();
   }

   public ClientChunkCache getChunkSource() {
      return (ClientChunkCache)super.getChunkSource();
   }

   @Nullable
   public MapItemSavedData getMapData(String var1) {
      return (MapItemSavedData)this.mapData.get(var1);
   }

   public void setMapData(MapItemSavedData var1) {
      this.mapData.put(var1.getId(), var1);
   }

   public int getFreeMapId() {
      return 0;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public TagManager getTagManager() {
      return this.connection.getTags();
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
         var7.setDetail("Block coordinates", (Object)CrashReportCategory.formatLocation(var3));
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

   public List players() {
      return this.players;
   }

   public Biome getUncachedNoiseBiome(int var1, int var2, int var3) {
      return Biomes.PLAINS;
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

   public Vec3 getSkyColor(BlockPos var1, float var2) {
      float var3 = this.getTimeOfDay(var2);
      float var4 = Mth.cos(var3 * 6.2831855F) * 2.0F + 0.5F;
      var4 = Mth.clamp(var4, 0.0F, 1.0F);
      Biome var5 = this.getBiome(var1);
      int var6 = var5.getSkyColor();
      float var7 = (float)(var6 >> 16 & 255) / 255.0F;
      float var8 = (float)(var6 >> 8 & 255) / 255.0F;
      float var9 = (float)(var6 & 255) / 255.0F;
      var7 *= var4;
      var8 *= var4;
      var9 *= var4;
      float var10 = this.getRainLevel(var2);
      float var11;
      float var12;
      if (var10 > 0.0F) {
         var11 = (var7 * 0.3F + var8 * 0.59F + var9 * 0.11F) * 0.6F;
         var12 = 1.0F - var10 * 0.75F;
         var7 = var7 * var12 + var11 * (1.0F - var12);
         var8 = var8 * var12 + var11 * (1.0F - var12);
         var9 = var9 * var12 + var11 * (1.0F - var12);
      }

      var11 = this.getThunderLevel(var2);
      if (var11 > 0.0F) {
         var12 = (var7 * 0.3F + var8 * 0.59F + var9 * 0.11F) * 0.2F;
         float var13 = 1.0F - var11 * 0.75F;
         var7 = var7 * var13 + var12 * (1.0F - var13);
         var8 = var8 * var13 + var12 * (1.0F - var13);
         var9 = var9 * var13 + var12 * (1.0F - var13);
      }

      if (this.skyFlashTime > 0) {
         var12 = (float)this.skyFlashTime - var2;
         if (var12 > 1.0F) {
            var12 = 1.0F;
         }

         var12 *= 0.45F;
         var7 = var7 * (1.0F - var12) + 0.8F * var12;
         var8 = var8 * (1.0F - var12) + 0.8F * var12;
         var9 = var9 * (1.0F - var12) + 1.0F * var12;
      }

      return new Vec3((double)var7, (double)var8, (double)var9);
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

   public Vec3 getFogColor(float var1) {
      float var2 = this.getTimeOfDay(var1);
      return this.dimension.getFogColor(var2, var1);
   }

   public float getStarBrightness(float var1) {
      float var2 = this.getTimeOfDay(var1);
      float var3 = 1.0F - (Mth.cos(var2 * 6.2831855F) * 2.0F + 0.25F);
      var3 = Mth.clamp(var3, 0.0F, 1.0F);
      return var3 * var3 * 0.5F;
   }

   public double getHorizonHeight() {
      return this.levelData.getGeneratorType() == LevelType.FLAT ? 0.0D : 63.0D;
   }

   public int getSkyFlashTime() {
      return this.skyFlashTime;
   }

   public void setSkyFlashTime(int var1) {
      this.skyFlashTime = var1;
   }

   public int getBlockTint(BlockPos var1, ColorResolver var2) {
      BlockTintCache var3 = (BlockTintCache)this.tintCaches.get(var2);
      return var3.getColor(var1, () -> {
         return this.calculateBlockTint(var1, var2);
      });
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

   // $FF: synthetic method
   public ChunkSource getChunkSource() {
      return this.getChunkSource();
   }
}
