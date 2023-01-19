package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class EndDragonFight {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int MAX_TICKS_BEFORE_DRAGON_RESPAWN = 1200;
   private static final int TIME_BETWEEN_CRYSTAL_SCANS = 100;
   private static final int TIME_BETWEEN_PLAYER_SCANS = 20;
   private static final int ARENA_SIZE_CHUNKS = 8;
   public static final int ARENA_TICKET_LEVEL = 9;
   private static final int GATEWAY_COUNT = 20;
   private static final int GATEWAY_DISTANCE = 96;
   public static final int DRAGON_SPAWN_Y = 128;
   private static final Predicate<Entity> VALID_PLAYER = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance(0.0, 128.0, 0.0, 192.0));
   private final ServerBossEvent dragonEvent = (ServerBossEvent)new ServerBossEvent(
         Component.translatable("entity.minecraft.ender_dragon"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS
      )
      .setPlayBossMusic(true)
      .setCreateWorldFog(true);
   private final ServerLevel level;
   private final ObjectArrayList<Integer> gateways = new ObjectArrayList();
   private final BlockPattern exitPortalPattern;
   private int ticksSinceDragonSeen;
   private int crystalsAlive;
   private int ticksSinceCrystalsScanned;
   private int ticksSinceLastPlayerScan;
   private boolean dragonKilled;
   private boolean previouslyKilled;
   @Nullable
   private UUID dragonUUID;
   private boolean needsStateScanning = true;
   @Nullable
   private BlockPos portalLocation;
   @Nullable
   private DragonRespawnAnimation respawnStage;
   private int respawnTime;
   @Nullable
   private List<EndCrystal> respawnCrystals;

   public EndDragonFight(ServerLevel var1, long var2, CompoundTag var4) {
      super();
      this.level = var1;
      if (var4.contains("NeedsStateScanning")) {
         this.needsStateScanning = var4.getBoolean("NeedsStateScanning");
      }

      if (var4.contains("DragonKilled", 99)) {
         if (var4.hasUUID("Dragon")) {
            this.dragonUUID = var4.getUUID("Dragon");
         }

         this.dragonKilled = var4.getBoolean("DragonKilled");
         this.previouslyKilled = var4.getBoolean("PreviouslyKilled");
         if (var4.getBoolean("IsRespawning")) {
            this.respawnStage = DragonRespawnAnimation.START;
         }

         if (var4.contains("ExitPortalLocation", 10)) {
            this.portalLocation = NbtUtils.readBlockPos(var4.getCompound("ExitPortalLocation"));
         }
      } else {
         this.dragonKilled = true;
         this.previouslyKilled = true;
      }

      if (var4.contains("Gateways", 9)) {
         ListTag var5 = var4.getList("Gateways", 3);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            this.gateways.add(var5.getInt(var6));
         }
      } else {
         this.gateways.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         Util.shuffle(this.gateways, RandomSource.create(var2));
      }

      this.exitPortalPattern = BlockPatternBuilder.start()
         .aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
         .aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
         .aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
         .aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ")
         .aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ")
         .where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK)))
         .build();
   }

   public CompoundTag saveData() {
      CompoundTag var1 = new CompoundTag();
      var1.putBoolean("NeedsStateScanning", this.needsStateScanning);
      if (this.dragonUUID != null) {
         var1.putUUID("Dragon", this.dragonUUID);
      }

      var1.putBoolean("DragonKilled", this.dragonKilled);
      var1.putBoolean("PreviouslyKilled", this.previouslyKilled);
      if (this.portalLocation != null) {
         var1.put("ExitPortalLocation", NbtUtils.writeBlockPos(this.portalLocation));
      }

      ListTag var2 = new ListTag();
      ObjectListIterator var3 = this.gateways.iterator();

      while(var3.hasNext()) {
         int var4 = var3.next();
         var2.add(IntTag.valueOf(var4));
      }

      var1.put("Gateways", var2);
      return var1;
   }

   public void tick() {
      this.dragonEvent.setVisible(!this.dragonKilled);
      if (++this.ticksSinceLastPlayerScan >= 20) {
         this.updatePlayers();
         this.ticksSinceLastPlayerScan = 0;
      }

      if (!this.dragonEvent.getPlayers().isEmpty()) {
         this.level.getChunkSource().addRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
         boolean var1 = this.isArenaLoaded();
         if (this.needsStateScanning && var1) {
            this.scanState();
            this.needsStateScanning = false;
         }

         if (this.respawnStage != null) {
            if (this.respawnCrystals == null && var1) {
               this.respawnStage = null;
               this.tryRespawn();
            }

            this.respawnStage.tick(this.level, this, this.respawnCrystals, this.respawnTime++, this.portalLocation);
         }

         if (!this.dragonKilled) {
            if ((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= 1200) && var1) {
               this.findOrCreateDragon();
               this.ticksSinceDragonSeen = 0;
            }

            if (++this.ticksSinceCrystalsScanned >= 100 && var1) {
               this.updateCrystalCount();
               this.ticksSinceCrystalsScanned = 0;
            }
         }
      } else {
         this.level.getChunkSource().removeRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
      }
   }

   private void scanState() {
      LOGGER.info("Scanning for legacy world dragon fight...");
      boolean var1 = this.hasActiveExitPortal();
      if (var1) {
         LOGGER.info("Found that the dragon has been killed in this world already.");
         this.previouslyKilled = true;
      } else {
         LOGGER.info("Found that the dragon has not yet been killed in this world.");
         this.previouslyKilled = false;
         if (this.findExitPortal() == null) {
            this.spawnExitPortal(false);
         }
      }

      List var2 = this.level.getDragons();
      if (var2.isEmpty()) {
         this.dragonKilled = true;
      } else {
         EnderDragon var3 = (EnderDragon)var2.get(0);
         this.dragonUUID = var3.getUUID();
         LOGGER.info("Found that there's a dragon still alive ({})", var3);
         this.dragonKilled = false;
         if (!var1) {
            LOGGER.info("But we didn't have a portal, let's remove it.");
            var3.discard();
            this.dragonUUID = null;
         }
      }

      if (!this.previouslyKilled && this.dragonKilled) {
         this.dragonKilled = false;
      }
   }

   private void findOrCreateDragon() {
      List var1 = this.level.getDragons();
      if (var1.isEmpty()) {
         LOGGER.debug("Haven't seen the dragon, respawning it");
         this.createNewDragon();
      } else {
         LOGGER.debug("Haven't seen our dragon, but found another one to use.");
         this.dragonUUID = ((EnderDragon)var1.get(0)).getUUID();
      }
   }

   protected void setRespawnStage(DragonRespawnAnimation var1) {
      if (this.respawnStage == null) {
         throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
      } else {
         this.respawnTime = 0;
         if (var1 == DragonRespawnAnimation.END) {
            this.respawnStage = null;
            this.dragonKilled = false;
            EnderDragon var2 = this.createNewDragon();
            if (var2 != null) {
               for(ServerPlayer var4 : this.dragonEvent.getPlayers()) {
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(var4, var2);
               }
            }
         } else {
            this.respawnStage = var1;
         }
      }
   }

   private boolean hasActiveExitPortal() {
      for(int var1 = -8; var1 <= 8; ++var1) {
         for(int var2 = -8; var2 <= 8; ++var2) {
            LevelChunk var3 = this.level.getChunk(var1, var2);

            for(BlockEntity var5 : var3.getBlockEntities().values()) {
               if (var5 instanceof TheEndPortalBlockEntity) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   private BlockPattern.BlockPatternMatch findExitPortal() {
      for(int var1 = -8; var1 <= 8; ++var1) {
         for(int var2 = -8; var2 <= 8; ++var2) {
            LevelChunk var3 = this.level.getChunk(var1, var2);

            for(BlockEntity var5 : var3.getBlockEntities().values()) {
               if (var5 instanceof TheEndPortalBlockEntity) {
                  BlockPattern.BlockPatternMatch var6 = this.exitPortalPattern.find(this.level, var5.getBlockPos());
                  if (var6 != null) {
                     BlockPos var7 = var6.getBlock(3, 3, 3).getPos();
                     if (this.portalLocation == null) {
                        this.portalLocation = var7;
                     }

                     return var6;
                  }
               }
            }
         }
      }

      int var8 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION).getY();

      for(int var9 = var8; var9 >= this.level.getMinBuildHeight(); --var9) {
         BlockPattern.BlockPatternMatch var10 = this.exitPortalPattern
            .find(this.level, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION.getX(), var9, EndPodiumFeature.END_PODIUM_LOCATION.getZ()));
         if (var10 != null) {
            if (this.portalLocation == null) {
               this.portalLocation = var10.getBlock(3, 3, 3).getPos();
            }

            return var10;
         }
      }

      return null;
   }

   private boolean isArenaLoaded() {
      for(int var1 = -8; var1 <= 8; ++var1) {
         for(int var2 = 8; var2 <= 8; ++var2) {
            ChunkAccess var3 = this.level.getChunk(var1, var2, ChunkStatus.FULL, false);
            if (!(var3 instanceof LevelChunk)) {
               return false;
            }

            ChunkHolder.FullChunkStatus var4 = ((LevelChunk)var3).getFullStatus();
            if (!var4.isOrAfter(ChunkHolder.FullChunkStatus.TICKING)) {
               return false;
            }
         }
      }

      return true;
   }

   private void updatePlayers() {
      HashSet var1 = Sets.newHashSet();

      for(ServerPlayer var3 : this.level.getPlayers(VALID_PLAYER)) {
         this.dragonEvent.addPlayer(var3);
         var1.add(var3);
      }

      HashSet var5 = Sets.newHashSet(this.dragonEvent.getPlayers());
      var5.removeAll(var1);

      for(ServerPlayer var4 : var5) {
         this.dragonEvent.removePlayer(var4);
      }
   }

   private void updateCrystalCount() {
      this.ticksSinceCrystalsScanned = 0;
      this.crystalsAlive = 0;

      for(SpikeFeature.EndSpike var2 : SpikeFeature.getSpikesForLevel(this.level)) {
         this.crystalsAlive += this.level.getEntitiesOfClass(EndCrystal.class, var2.getTopBoundingBox()).size();
      }

      LOGGER.debug("Found {} end crystals still alive", this.crystalsAlive);
   }

   public void setDragonKilled(EnderDragon var1) {
      if (var1.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setProgress(0.0F);
         this.dragonEvent.setVisible(false);
         this.spawnExitPortal(true);
         this.spawnNewGateway();
         if (!this.previouslyKilled) {
            this.level
               .setBlockAndUpdate(
                  this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.defaultBlockState()
               );
         }

         this.previouslyKilled = true;
         this.dragonKilled = true;
      }
   }

   private void spawnNewGateway() {
      if (!this.gateways.isEmpty()) {
         int var1 = this.gateways.remove(this.gateways.size() - 1);
         int var2 = Mth.floor(96.0 * Math.cos(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)var1)));
         int var3 = Mth.floor(96.0 * Math.sin(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)var1)));
         this.spawnNewGateway(new BlockPos(var2, 75, var3));
      }
   }

   private void spawnNewGateway(BlockPos var1) {
      this.level.levelEvent(3000, var1, 0);
      this.level
         .registryAccess()
         .registry(Registries.CONFIGURED_FEATURE)
         .flatMap(var0 -> var0.getHolder(EndFeatures.END_GATEWAY_DELAYED))
         .ifPresent(var2 -> var2.value().place(this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), var1));
   }

   private void spawnExitPortal(boolean var1) {
      EndPodiumFeature var2 = new EndPodiumFeature(var1);
      if (this.portalLocation == null) {
         this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION).below();

         while(this.level.getBlockState(this.portalLocation).is(Blocks.BEDROCK) && this.portalLocation.getY() > this.level.getSeaLevel()) {
            this.portalLocation = this.portalLocation.below();
         }
      }

      var2.place(FeatureConfiguration.NONE, this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), this.portalLocation);
   }

   @Nullable
   private EnderDragon createNewDragon() {
      this.level.getChunkAt(new BlockPos(0, 128, 0));
      EnderDragon var1 = EntityType.ENDER_DRAGON.create(this.level);
      if (var1 != null) {
         var1.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
         var1.moveTo(0.0, 128.0, 0.0, this.level.random.nextFloat() * 360.0F, 0.0F);
         this.level.addFreshEntity(var1);
         this.dragonUUID = var1.getUUID();
      }

      return var1;
   }

   public void updateDragon(EnderDragon var1) {
      if (var1.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setProgress(var1.getHealth() / var1.getMaxHealth());
         this.ticksSinceDragonSeen = 0;
         if (var1.hasCustomName()) {
            this.dragonEvent.setName(var1.getDisplayName());
         }
      }
   }

   public int getCrystalsAlive() {
      return this.crystalsAlive;
   }

   public void onCrystalDestroyed(EndCrystal var1, DamageSource var2) {
      if (this.respawnStage != null && this.respawnCrystals.contains(var1)) {
         LOGGER.debug("Aborting respawn sequence");
         this.respawnStage = null;
         this.respawnTime = 0;
         this.resetSpikeCrystals();
         this.spawnExitPortal(true);
      } else {
         this.updateCrystalCount();
         Entity var3 = this.level.getEntity(this.dragonUUID);
         if (var3 instanceof EnderDragon) {
            ((EnderDragon)var3).onCrystalDestroyed(var1, var1.blockPosition(), var2);
         }
      }
   }

   public boolean hasPreviouslyKilledDragon() {
      return this.previouslyKilled;
   }

   public void tryRespawn() {
      if (this.dragonKilled && this.respawnStage == null) {
         BlockPos var1 = this.portalLocation;
         if (var1 == null) {
            LOGGER.debug("Tried to respawn, but need to find the portal first.");
            BlockPattern.BlockPatternMatch var2 = this.findExitPortal();
            if (var2 == null) {
               LOGGER.debug("Couldn't find a portal, so we made one.");
               this.spawnExitPortal(true);
            } else {
               LOGGER.debug("Found the exit portal & saved its location for next time.");
            }

            var1 = this.portalLocation;
         }

         ArrayList var7 = Lists.newArrayList();
         BlockPos var3 = var1.above(1);

         for(Direction var5 : Direction.Plane.HORIZONTAL) {
            List var6 = this.level.getEntitiesOfClass(EndCrystal.class, new AABB(var3.relative(var5, 2)));
            if (var6.isEmpty()) {
               return;
            }

            var7.addAll(var6);
         }

         LOGGER.debug("Found all crystals, respawning dragon.");
         this.respawnDragon(var7);
      }
   }

   private void respawnDragon(List<EndCrystal> var1) {
      if (this.dragonKilled && this.respawnStage == null) {
         for(BlockPattern.BlockPatternMatch var2 = this.findExitPortal(); var2 != null; var2 = this.findExitPortal()) {
            for(int var3 = 0; var3 < this.exitPortalPattern.getWidth(); ++var3) {
               for(int var4 = 0; var4 < this.exitPortalPattern.getHeight(); ++var4) {
                  for(int var5 = 0; var5 < this.exitPortalPattern.getDepth(); ++var5) {
                     BlockInWorld var6 = var2.getBlock(var3, var4, var5);
                     if (var6.getState().is(Blocks.BEDROCK) || var6.getState().is(Blocks.END_PORTAL)) {
                        this.level.setBlockAndUpdate(var6.getPos(), Blocks.END_STONE.defaultBlockState());
                     }
                  }
               }
            }
         }

         this.respawnStage = DragonRespawnAnimation.START;
         this.respawnTime = 0;
         this.spawnExitPortal(false);
         this.respawnCrystals = var1;
      }
   }

   public void resetSpikeCrystals() {
      for(SpikeFeature.EndSpike var2 : SpikeFeature.getSpikesForLevel(this.level)) {
         for(EndCrystal var5 : this.level.getEntitiesOfClass(EndCrystal.class, var2.getTopBoundingBox())) {
            var5.setInvulnerable(false);
            var5.setBeamTarget(null);
         }
      }
   }
}
