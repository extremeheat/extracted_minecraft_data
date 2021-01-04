package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.Mth;
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
import net.minecraft.world.level.levelgen.feature.EndGatewayConfiguration;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EndDragonFight {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Predicate<Entity> VALID_PLAYER;
   private final ServerBossEvent dragonEvent;
   private final ServerLevel level;
   private final List<Integer> gateways;
   private final BlockPattern exitPortalPattern;
   private int ticksSinceDragonSeen;
   private int crystalsAlive;
   private int ticksSinceCrystalsScanned;
   private int ticksSinceLastPlayerScan;
   private boolean dragonKilled;
   private boolean previouslyKilled;
   private UUID dragonUUID;
   private boolean needsStateScanning;
   private BlockPos portalLocation;
   private DragonRespawnAnimation respawnStage;
   private int respawnTime;
   private List<EndCrystal> respawnCrystals;

   public EndDragonFight(ServerLevel var1, CompoundTag var2) {
      super();
      this.dragonEvent = (ServerBossEvent)(new ServerBossEvent(new TranslatableComponent("entity.minecraft.ender_dragon", new Object[0]), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS)).setPlayBossMusic(true).setCreateWorldFog(true);
      this.gateways = Lists.newArrayList();
      this.needsStateScanning = true;
      this.level = var1;
      if (var2.contains("DragonKilled", 99)) {
         if (var2.hasUUID("DragonUUID")) {
            this.dragonUUID = var2.getUUID("DragonUUID");
         }

         this.dragonKilled = var2.getBoolean("DragonKilled");
         this.previouslyKilled = var2.getBoolean("PreviouslyKilled");
         if (var2.getBoolean("IsRespawning")) {
            this.respawnStage = DragonRespawnAnimation.START;
         }

         if (var2.contains("ExitPortalLocation", 10)) {
            this.portalLocation = NbtUtils.readBlockPos(var2.getCompound("ExitPortalLocation"));
         }
      } else {
         this.dragonKilled = true;
         this.previouslyKilled = true;
      }

      if (var2.contains("Gateways", 9)) {
         ListTag var3 = var2.getList("Gateways", 3);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            this.gateways.add(var3.getInt(var4));
         }
      } else {
         this.gateways.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         Collections.shuffle(this.gateways, new Random(var1.getSeed()));
      }

      this.exitPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK))).build();
   }

   public CompoundTag saveData() {
      CompoundTag var1 = new CompoundTag();
      if (this.dragonUUID != null) {
         var1.putUUID("DragonUUID", this.dragonUUID);
      }

      var1.putBoolean("DragonKilled", this.dragonKilled);
      var1.putBoolean("PreviouslyKilled", this.previouslyKilled);
      if (this.portalLocation != null) {
         var1.put("ExitPortalLocation", NbtUtils.writeBlockPos(this.portalLocation));
      }

      ListTag var2 = new ListTag();
      Iterator var3 = this.gateways.iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         var2.add(new IntTag(var4));
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
      boolean var1 = this.hasExitPortal();
      if (var1) {
         LOGGER.info("Found that the dragon has been killed in this world already.");
         this.previouslyKilled = true;
      } else {
         LOGGER.info("Found that the dragon has not yet been killed in this world.");
         this.previouslyKilled = false;
         this.spawnExitPortal(false);
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
            var3.remove();
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
            Iterator var3 = this.dragonEvent.getPlayers().iterator();

            while(var3.hasNext()) {
               ServerPlayer var4 = (ServerPlayer)var3.next();
               CriteriaTriggers.SUMMONED_ENTITY.trigger(var4, var2);
            }
         } else {
            this.respawnStage = var1;
         }

      }
   }

   private boolean hasExitPortal() {
      for(int var1 = -8; var1 <= 8; ++var1) {
         for(int var2 = -8; var2 <= 8; ++var2) {
            LevelChunk var3 = this.level.getChunk(var1, var2);
            Iterator var4 = var3.getBlockEntities().values().iterator();

            while(var4.hasNext()) {
               BlockEntity var5 = (BlockEntity)var4.next();
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
      int var1;
      int var2;
      for(var1 = -8; var1 <= 8; ++var1) {
         for(var2 = -8; var2 <= 8; ++var2) {
            LevelChunk var3 = this.level.getChunk(var1, var2);
            Iterator var4 = var3.getBlockEntities().values().iterator();

            while(var4.hasNext()) {
               BlockEntity var5 = (BlockEntity)var4.next();
               if (var5 instanceof TheEndPortalBlockEntity) {
                  BlockPattern.BlockPatternMatch var6 = this.exitPortalPattern.find(this.level, var5.getBlockPos());
                  if (var6 != null) {
                     BlockPos var7 = var6.getBlock(3, 3, 3).getPos();
                     if (this.portalLocation == null && var7.getX() == 0 && var7.getZ() == 0) {
                        this.portalLocation = var7;
                     }

                     return var6;
                  }
               }
            }
         }
      }

      var1 = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION).getY();

      for(var2 = var1; var2 >= 0; --var2) {
         BlockPattern.BlockPatternMatch var8 = this.exitPortalPattern.find(this.level, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION.getX(), var2, EndPodiumFeature.END_PODIUM_LOCATION.getZ()));
         if (var8 != null) {
            if (this.portalLocation == null) {
               this.portalLocation = var8.getBlock(3, 3, 3).getPos();
            }

            return var8;
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
      Iterator var2 = this.level.getPlayers(VALID_PLAYER).iterator();

      while(var2.hasNext()) {
         ServerPlayer var3 = (ServerPlayer)var2.next();
         this.dragonEvent.addPlayer(var3);
         var1.add(var3);
      }

      HashSet var5 = Sets.newHashSet(this.dragonEvent.getPlayers());
      var5.removeAll(var1);
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         ServerPlayer var4 = (ServerPlayer)var6.next();
         this.dragonEvent.removePlayer(var4);
      }

   }

   private void updateCrystalCount() {
      this.ticksSinceCrystalsScanned = 0;
      this.crystalsAlive = 0;

      SpikeFeature.EndSpike var2;
      for(Iterator var1 = SpikeFeature.getSpikesForLevel(this.level).iterator(); var1.hasNext(); this.crystalsAlive += this.level.getEntitiesOfClass(EndCrystal.class, var2.getTopBoundingBox()).size()) {
         var2 = (SpikeFeature.EndSpike)var1.next();
      }

      LOGGER.debug("Found {} end crystals still alive", this.crystalsAlive);
   }

   public void setDragonKilled(EnderDragon var1) {
      if (var1.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setPercent(0.0F);
         this.dragonEvent.setVisible(false);
         this.spawnExitPortal(true);
         this.spawnNewGateway();
         if (!this.previouslyKilled) {
            this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.defaultBlockState());
         }

         this.previouslyKilled = true;
         this.dragonKilled = true;
      }

   }

   private void spawnNewGateway() {
      if (!this.gateways.isEmpty()) {
         int var1 = (Integer)this.gateways.remove(this.gateways.size() - 1);
         int var2 = Mth.floor(96.0D * Math.cos(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double)var1)));
         int var3 = Mth.floor(96.0D * Math.sin(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double)var1)));
         this.spawnNewGateway(new BlockPos(var2, 75, var3));
      }
   }

   private void spawnNewGateway(BlockPos var1) {
      this.level.levelEvent(3000, var1, 0);
      Feature.END_GATEWAY.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), var1, EndGatewayConfiguration.delayedExitSearch());
   }

   private void spawnExitPortal(boolean var1) {
      EndPodiumFeature var2 = new EndPodiumFeature(var1);
      if (this.portalLocation == null) {
         for(this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION).below(); this.level.getBlockState(this.portalLocation).getBlock() == Blocks.BEDROCK && this.portalLocation.getY() > this.level.getSeaLevel(); this.portalLocation = this.portalLocation.below()) {
         }
      }

      var2.place(this.level, this.level.getChunkSource().getGenerator(), new Random(), this.portalLocation, (NoneFeatureConfiguration)FeatureConfiguration.NONE);
   }

   private EnderDragon createNewDragon() {
      this.level.getChunkAt(new BlockPos(0, 128, 0));
      EnderDragon var1 = (EnderDragon)EntityType.ENDER_DRAGON.create(this.level);
      var1.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
      var1.moveTo(0.0D, 128.0D, 0.0D, this.level.random.nextFloat() * 360.0F, 0.0F);
      this.level.addFreshEntity(var1);
      this.dragonUUID = var1.getUUID();
      return var1;
   }

   public void updateDragon(EnderDragon var1) {
      if (var1.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setPercent(var1.getHealth() / var1.getMaxHealth());
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
            ((EnderDragon)var3).onCrystalDestroyed(var1, new BlockPos(var1), var2);
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
               LOGGER.debug("Found the exit portal & temporarily using it.");
            }

            var1 = this.portalLocation;
         }

         ArrayList var7 = Lists.newArrayList();
         BlockPos var3 = var1.above(1);
         Iterator var4 = Direction.Plane.HORIZONTAL.iterator();

         while(var4.hasNext()) {
            Direction var5 = (Direction)var4.next();
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
                     if (var6.getState().getBlock() == Blocks.BEDROCK || var6.getState().getBlock() == Blocks.END_PORTAL) {
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
      Iterator var1 = SpikeFeature.getSpikesForLevel(this.level).iterator();

      while(var1.hasNext()) {
         SpikeFeature.EndSpike var2 = (SpikeFeature.EndSpike)var1.next();
         List var3 = this.level.getEntitiesOfClass(EndCrystal.class, var2.getTopBoundingBox());
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            EndCrystal var5 = (EndCrystal)var4.next();
            var5.setInvulnerable(false);
            var5.setBeamTarget((BlockPos)null);
         }
      }

   }

   static {
      VALID_PLAYER = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance(0.0D, 128.0D, 0.0D, 192.0D));
   }
}
