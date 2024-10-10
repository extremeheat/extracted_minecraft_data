package net.minecraft.world.level.dimension.end;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.FullChunkStatus;
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
import net.minecraft.world.entity.EntitySpawnReason;
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
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
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
   public static final int TIME_BETWEEN_PLAYER_SCANS = 20;
   private static final int ARENA_SIZE_CHUNKS = 8;
   public static final int ARENA_TICKET_LEVEL = 9;
   private static final int GATEWAY_COUNT = 20;
   private static final int GATEWAY_DISTANCE = 96;
   public static final int DRAGON_SPAWN_Y = 128;
   private final Predicate<Entity> validPlayer;
   private final ServerBossEvent dragonEvent = (ServerBossEvent)new ServerBossEvent(
         Component.translatable("entity.minecraft.ender_dragon"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS
      )
      .setPlayBossMusic(true)
      .setCreateWorldFog(true);
   private final ServerLevel level;
   private final BlockPos origin;
   private final ObjectArrayList<Integer> gateways = new ObjectArrayList();
   private final BlockPattern exitPortalPattern;
   private int ticksSinceDragonSeen;
   private int crystalsAlive;
   private int ticksSinceCrystalsScanned;
   private int ticksSinceLastPlayerScan = 21;
   private boolean dragonKilled;
   private boolean previouslyKilled;
   private boolean skipArenaLoadedCheck = false;
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

   public EndDragonFight(ServerLevel var1, long var2, EndDragonFight.Data var4) {
      this(var1, var2, var4, BlockPos.ZERO);
   }

   public EndDragonFight(ServerLevel var1, long var2, EndDragonFight.Data var4, BlockPos var5) {
      super();
      this.level = var1;
      this.origin = var5;
      this.validPlayer = EntitySelector.ENTITY_STILL_ALIVE
         .and(EntitySelector.withinDistance((double)var5.getX(), (double)(128 + var5.getY()), (double)var5.getZ(), 192.0));
      this.needsStateScanning = var4.needsStateScanning;
      this.dragonUUID = var4.dragonUUID.orElse(null);
      this.dragonKilled = var4.dragonKilled;
      this.previouslyKilled = var4.previouslyKilled;
      if (var4.isRespawning) {
         this.respawnStage = DragonRespawnAnimation.START;
      }

      this.portalLocation = var4.exitPortalLocation.orElse(null);
      this.gateways.addAll(var4.gateways.orElseGet(() -> {
         ObjectArrayList var2x = new ObjectArrayList(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         Util.shuffle(var2x, RandomSource.create(var2));
         return var2x;
      }));
      this.exitPortalPattern = BlockPatternBuilder.start()
         .aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
         .aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
         .aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ")
         .aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ")
         .aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ")
         .where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK)))
         .build();
   }

   @Deprecated
   @VisibleForTesting
   public void skipArenaLoadedCheck() {
      this.skipArenaLoadedCheck = true;
   }

   public EndDragonFight.Data saveData() {
      return new EndDragonFight.Data(
         this.needsStateScanning,
         this.dragonKilled,
         this.previouslyKilled,
         false,
         Optional.ofNullable(this.dragonUUID),
         Optional.ofNullable(this.portalLocation),
         Optional.of(this.gateways)
      );
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
               for (ServerPlayer var4 : this.dragonEvent.getPlayers()) {
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(var4, var2);
               }
            }
         } else {
            this.respawnStage = var1;
         }
      }
   }

   private boolean hasActiveExitPortal() {
      for (int var1 = -8; var1 <= 8; var1++) {
         for (int var2 = -8; var2 <= 8; var2++) {
            LevelChunk var3 = this.level.getChunk(var1, var2);

            for (BlockEntity var5 : var3.getBlockEntities().values()) {
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
      ChunkPos var1 = new ChunkPos(this.origin);

      for (int var2 = -8 + var1.x; var2 <= 8 + var1.x; var2++) {
         for (int var3 = -8 + var1.z; var3 <= 8 + var1.z; var3++) {
            LevelChunk var4 = this.level.getChunk(var2, var3);

            for (BlockEntity var6 : var4.getBlockEntities().values()) {
               if (var6 instanceof TheEndPortalBlockEntity) {
                  BlockPattern.BlockPatternMatch var7 = this.exitPortalPattern.find(this.level, var6.getBlockPos());
                  if (var7 != null) {
                     BlockPos var8 = var7.getBlock(3, 3, 3).getPos();
                     if (this.portalLocation == null) {
                        this.portalLocation = var8;
                     }

                     return var7;
                  }
               }
            }
         }
      }

      BlockPos var9 = EndPodiumFeature.getLocation(this.origin);
      int var10 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var9).getY();

      for (int var11 = var10; var11 >= this.level.getMinY(); var11--) {
         BlockPattern.BlockPatternMatch var12 = this.exitPortalPattern.find(this.level, new BlockPos(var9.getX(), var11, var9.getZ()));
         if (var12 != null) {
            if (this.portalLocation == null) {
               this.portalLocation = var12.getBlock(3, 3, 3).getPos();
            }

            return var12;
         }
      }

      return null;
   }

   private boolean isArenaLoaded() {
      if (this.skipArenaLoadedCheck) {
         return true;
      } else {
         ChunkPos var1 = new ChunkPos(this.origin);

         for (int var2 = -8 + var1.x; var2 <= 8 + var1.x; var2++) {
            for (int var3 = 8 + var1.z; var3 <= 8 + var1.z; var3++) {
               ChunkAccess var4 = this.level.getChunk(var2, var3, ChunkStatus.FULL, false);
               if (!(var4 instanceof LevelChunk)) {
                  return false;
               }

               FullChunkStatus var5 = ((LevelChunk)var4).getFullStatus();
               if (!var5.isOrAfter(FullChunkStatus.BLOCK_TICKING)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private void updatePlayers() {
      HashSet var1 = Sets.newHashSet();

      for (ServerPlayer var3 : this.level.getPlayers(this.validPlayer)) {
         this.dragonEvent.addPlayer(var3);
         var1.add(var3);
      }

      HashSet var5 = Sets.newHashSet(this.dragonEvent.getPlayers());
      var5.removeAll(var1);

      for (ServerPlayer var4 : var5) {
         this.dragonEvent.removePlayer(var4);
      }
   }

   private void updateCrystalCount() {
      this.ticksSinceCrystalsScanned = 0;
      this.crystalsAlive = 0;

      for (SpikeFeature.EndSpike var2 : SpikeFeature.getSpikesForLevel(this.level)) {
         this.crystalsAlive = this.crystalsAlive + this.level.getEntitiesOfClass(EndCrystal.class, var2.getTopBoundingBox()).size();
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
                  this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.getLocation(this.origin)), Blocks.DRAGON_EGG.defaultBlockState()
               );
         }

         this.previouslyKilled = true;
         this.dragonKilled = true;
      }
   }

   @Deprecated
   @VisibleForTesting
   public void removeAllGateways() {
      this.gateways.clear();
   }

   private void spawnNewGateway() {
      if (!this.gateways.isEmpty()) {
         int var1 = (Integer)this.gateways.remove(this.gateways.size() - 1);
         int var2 = Mth.floor(96.0 * Math.cos(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)var1)));
         int var3 = Mth.floor(96.0 * Math.sin(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)var1)));
         this.spawnNewGateway(new BlockPos(var2, 75, var3));
      }
   }

   private void spawnNewGateway(BlockPos var1) {
      this.level.levelEvent(3000, var1, 0);
      this.level
         .registryAccess()
         .lookup(Registries.CONFIGURED_FEATURE)
         .flatMap(var0 -> var0.get(EndFeatures.END_GATEWAY_DELAYED))
         .ifPresent(var2 -> var2.value().place(this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), var1));
   }

   private void spawnExitPortal(boolean var1) {
      EndPodiumFeature var2 = new EndPodiumFeature(var1);
      if (this.portalLocation == null) {
         this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.origin)).below();

         while (this.level.getBlockState(this.portalLocation).is(Blocks.BEDROCK) && this.portalLocation.getY() > 63) {
            this.portalLocation = this.portalLocation.below();
         }
      }

      if (var2.place(FeatureConfiguration.NONE, this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), this.portalLocation)) {
         int var3 = Mth.positiveCeilDiv(4, 16);
         this.level.getChunkSource().chunkMap.waitForLightBeforeSending(new ChunkPos(this.portalLocation), var3);
      }
   }

   @Nullable
   private EnderDragon createNewDragon() {
      this.level.getChunkAt(new BlockPos(this.origin.getX(), 128 + this.origin.getY(), this.origin.getZ()));
      EnderDragon var1 = EntityType.ENDER_DRAGON.create(this.level, EntitySpawnReason.EVENT);
      if (var1 != null) {
         var1.setDragonFight(this);
         var1.setFightOrigin(this.origin);
         var1.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
         var1.moveTo((double)this.origin.getX(), (double)(128 + this.origin.getY()), (double)this.origin.getZ(), this.level.random.nextFloat() * 360.0F, 0.0F);
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
         if (this.level.getEntity(this.dragonUUID) instanceof EnderDragon var4) {
            var4.onCrystalDestroyed(this.level, var1, var1.blockPosition(), var2);
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

         for (Direction var5 : Direction.Plane.HORIZONTAL) {
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
         for (BlockPattern.BlockPatternMatch var2 = this.findExitPortal(); var2 != null; var2 = this.findExitPortal()) {
            for (int var3 = 0; var3 < this.exitPortalPattern.getWidth(); var3++) {
               for (int var4 = 0; var4 < this.exitPortalPattern.getHeight(); var4++) {
                  for (int var5 = 0; var5 < this.exitPortalPattern.getDepth(); var5++) {
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
      for (SpikeFeature.EndSpike var2 : SpikeFeature.getSpikesForLevel(this.level)) {
         for (EndCrystal var5 : this.level.getEntitiesOfClass(EndCrystal.class, var2.getTopBoundingBox())) {
            var5.setInvulnerable(false);
            var5.setBeamTarget(null);
         }
      }
   }

   @Nullable
   public UUID getDragonUUID() {
      return this.dragonUUID;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
