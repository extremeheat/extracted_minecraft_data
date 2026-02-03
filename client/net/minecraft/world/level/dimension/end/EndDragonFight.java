package net.minecraft.world.level.dimension.end;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.UUIDUtil;
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
import net.minecraft.util.Util;
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
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;
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
   private final ServerBossEvent dragonEvent;
   private final ServerLevel level;
   private final BlockPos origin;
   private final ObjectArrayList<Integer> gateways;
   private final BlockPattern exitPortalPattern;
   private int ticksSinceDragonSeen;
   private int crystalsAlive;
   private int ticksSinceCrystalsScanned;
   private int ticksSinceLastPlayerScan;
   private boolean dragonKilled;
   private boolean previouslyKilled;
   private boolean skipArenaLoadedCheck;
   private @Nullable UUID dragonUUID;
   private boolean needsStateScanning;
   private @Nullable BlockPos portalLocation;
   private @Nullable DragonRespawnAnimation respawnStage;
   private int respawnTime;
   private @Nullable List<EndCrystal> respawnCrystals;

   public EndDragonFight(final ServerLevel level, final long seed, final Data dragonFightData) {
      this(level, seed, dragonFightData, BlockPos.ZERO);
   }

   public EndDragonFight(final ServerLevel level, final long seed, final Data dragonFightData, final BlockPos origin) {
      super();
      this.dragonEvent = (ServerBossEvent)(new ServerBossEvent(Component.translatable("entity.minecraft.ender_dragon"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS)).setPlayBossMusic(true).setCreateWorldFog(true);
      this.gateways = new ObjectArrayList();
      this.ticksSinceLastPlayerScan = 21;
      this.skipArenaLoadedCheck = false;
      this.needsStateScanning = true;
      this.level = level;
      this.origin = origin;
      this.validPlayer = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance((double)origin.getX(), (double)(128 + origin.getY()), (double)origin.getZ(), 192.0));
      this.needsStateScanning = dragonFightData.needsStateScanning;
      this.dragonUUID = (UUID)dragonFightData.dragonUUID.orElse((Object)null);
      this.dragonKilled = dragonFightData.dragonKilled;
      this.previouslyKilled = dragonFightData.previouslyKilled;
      if (dragonFightData.isRespawning) {
         this.respawnStage = DragonRespawnAnimation.START;
      }

      this.portalLocation = (BlockPos)dragonFightData.exitPortalLocation.orElse((Object)null);
      this.gateways.addAll((Collection)dragonFightData.gateways.orElseGet(() -> {
         ObjectArrayList<Integer> gateways = new ObjectArrayList(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         Util.shuffle(gateways, RandomSource.create(seed));
         return gateways;
      }));
      this.exitPortalPattern = BlockPatternBuilder.start().aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").aisle("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").aisle("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK))).build();
   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void skipArenaLoadedCheck() {
      this.skipArenaLoadedCheck = true;
   }

   public Data saveData() {
      return new Data(this.needsStateScanning, this.dragonKilled, this.previouslyKilled, false, Optional.ofNullable(this.dragonUUID), Optional.ofNullable(this.portalLocation), Optional.of(this.gateways));
   }

   public void tick() {
      this.dragonEvent.setVisible(!this.dragonKilled);
      if (++this.ticksSinceLastPlayerScan >= 20) {
         this.updatePlayers();
         this.ticksSinceLastPlayerScan = 0;
      }

      if (!this.dragonEvent.getPlayers().isEmpty()) {
         this.level.getChunkSource().addTicketWithRadius(TicketType.DRAGON, new ChunkPos(0, 0), 9);
         boolean arenaLoaded = this.isArenaLoaded();
         if (this.needsStateScanning && arenaLoaded) {
            this.scanState();
            this.needsStateScanning = false;
         }

         if (this.respawnStage != null) {
            if (this.respawnCrystals == null && arenaLoaded) {
               this.respawnStage = null;
               this.tryRespawn();
            }

            this.respawnStage.tick(this.level, this, this.respawnCrystals, this.respawnTime++, this.portalLocation);
         }

         if (!this.dragonKilled) {
            if ((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= 1200) && arenaLoaded) {
               this.findOrCreateDragon();
               this.ticksSinceDragonSeen = 0;
            }

            if (++this.ticksSinceCrystalsScanned >= 100 && arenaLoaded) {
               this.updateCrystalCount();
               this.ticksSinceCrystalsScanned = 0;
            }
         }
      } else {
         this.level.getChunkSource().removeTicketWithRadius(TicketType.DRAGON, new ChunkPos(0, 0), 9);
      }

   }

   private void scanState() {
      LOGGER.info("Scanning for legacy world dragon fight...");
      boolean activePortalExists = this.hasActiveExitPortal();
      if (activePortalExists) {
         LOGGER.info("Found that the dragon has been killed in this world already.");
         this.previouslyKilled = true;
      } else {
         LOGGER.info("Found that the dragon has not yet been killed in this world.");
         this.previouslyKilled = false;
         if (this.findExitPortal() == null) {
            this.spawnExitPortal(false);
         }
      }

      List<? extends EnderDragon> entities = this.level.getDragons();
      if (entities.isEmpty()) {
         this.dragonKilled = true;
      } else {
         EnderDragon dragon = (EnderDragon)entities.get(0);
         this.dragonUUID = dragon.getUUID();
         LOGGER.info("Found that there's a dragon still alive ({})", dragon);
         this.dragonKilled = false;
         if (!activePortalExists) {
            LOGGER.info("But we didn't have a portal, let's remove it.");
            dragon.discard();
            this.dragonUUID = null;
         }
      }

      if (!this.previouslyKilled && this.dragonKilled) {
         this.dragonKilled = false;
      }

   }

   private void findOrCreateDragon() {
      List<? extends EnderDragon> entities = this.level.getDragons();
      if (entities.isEmpty()) {
         LOGGER.debug("Haven't seen the dragon, respawning it");
         this.createNewDragon();
      } else {
         LOGGER.debug("Haven't seen our dragon, but found another one to use.");
         this.dragonUUID = ((EnderDragon)entities.get(0)).getUUID();
      }

   }

   protected void setRespawnStage(final DragonRespawnAnimation stage) {
      if (this.respawnStage == null) {
         throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
      } else {
         this.respawnTime = 0;
         if (stage == DragonRespawnAnimation.END) {
            this.respawnStage = null;
            this.dragonKilled = false;
            EnderDragon dragon = this.createNewDragon();
            if (dragon != null) {
               for(ServerPlayer player : this.dragonEvent.getPlayers()) {
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(player, dragon);
               }
            }
         } else {
            this.respawnStage = stage;
         }

      }
   }

   private boolean hasActiveExitPortal() {
      for(int x = -8; x <= 8; ++x) {
         for(int z = -8; z <= 8; ++z) {
            LevelChunk chunk = this.level.getChunk(x, z);

            for(BlockEntity blockEntity : chunk.getBlockEntities().values()) {
               if (blockEntity instanceof TheEndPortalBlockEntity) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private BlockPattern.@Nullable BlockPatternMatch findExitPortal() {
      ChunkPos chunkOrigin = ChunkPos.containing(this.origin);

      for(int x = -8 + chunkOrigin.x(); x <= 8 + chunkOrigin.x(); ++x) {
         for(int z = -8 + chunkOrigin.z(); z <= 8 + chunkOrigin.z(); ++z) {
            LevelChunk chunk = this.level.getChunk(x, z);

            for(BlockEntity blockEntity : chunk.getBlockEntities().values()) {
               if (blockEntity instanceof TheEndPortalBlockEntity) {
                  BlockPattern.BlockPatternMatch match = this.exitPortalPattern.find(this.level, blockEntity.getBlockPos());
                  if (match != null) {
                     BlockPos posInWorld = match.getBlock(3, 3, 3).getPos();
                     if (this.portalLocation == null) {
                        this.portalLocation = posInWorld;
                     }

                     return match;
                  }
               }
            }
         }
      }

      BlockPos endPodiumLocation = EndPodiumFeature.getLocation(this.origin);
      int maxY = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, endPodiumLocation).getY();

      for(int y = maxY; y >= this.level.getMinY(); --y) {
         BlockPattern.BlockPatternMatch match = this.exitPortalPattern.find(this.level, new BlockPos(endPodiumLocation.getX(), y, endPodiumLocation.getZ()));
         if (match != null) {
            if (this.portalLocation == null) {
               this.portalLocation = match.getBlock(3, 3, 3).getPos();
            }

            return match;
         }
      }

      return null;
   }

   private boolean isArenaLoaded() {
      if (this.skipArenaLoadedCheck) {
         return true;
      } else {
         ChunkPos chunkOrigin = ChunkPos.containing(this.origin);

         for(int x = -8 + chunkOrigin.x(); x <= 8 + chunkOrigin.x(); ++x) {
            for(int z = 8 + chunkOrigin.z(); z <= 8 + chunkOrigin.z(); ++z) {
               ChunkAccess chunk = this.level.getChunk(x, z, ChunkStatus.FULL, false);
               if (!(chunk instanceof LevelChunk)) {
                  return false;
               }

               FullChunkStatus status = ((LevelChunk)chunk).getFullStatus();
               if (!status.isOrAfter(FullChunkStatus.BLOCK_TICKING)) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   private void updatePlayers() {
      Set<ServerPlayer> newPlayers = Sets.newHashSet();

      for(ServerPlayer player : this.level.getPlayers(this.validPlayer)) {
         this.dragonEvent.addPlayer(player);
         newPlayers.add(player);
      }

      Set<ServerPlayer> toRemove = Sets.newHashSet(this.dragonEvent.getPlayers());
      toRemove.removeAll(newPlayers);

      for(ServerPlayer player : toRemove) {
         this.dragonEvent.removePlayer(player);
      }

   }

   private void updateCrystalCount() {
      this.ticksSinceCrystalsScanned = 0;
      this.crystalsAlive = 0;

      for(SpikeFeature.EndSpike spike : SpikeFeature.getSpikesForLevel(this.level)) {
         this.crystalsAlive += this.level.getEntitiesOfClass(EndCrystal.class, spike.getTopBoundingBox()).size();
      }

      LOGGER.debug("Found {} end crystals still alive", this.crystalsAlive);
   }

   public void setDragonKilled(final EnderDragon dragon) {
      if (dragon.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setProgress(0.0F);
         this.dragonEvent.setVisible(false);
         this.spawnExitPortal(true);
         this.spawnNewGateway();
         if (!this.previouslyKilled) {
            this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.getLocation(this.origin)), Blocks.DRAGON_EGG.defaultBlockState());
         }

         this.previouslyKilled = true;
         this.dragonKilled = true;
      }

   }

   /** @deprecated */
   @Deprecated
   @VisibleForTesting
   public void removeAllGateways() {
      this.gateways.clear();
   }

   private void spawnNewGateway() {
      if (!this.gateways.isEmpty()) {
         int gateway = (Integer)this.gateways.remove(this.gateways.size() - 1);
         int x = Mth.floor(96.0 * Math.cos(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)gateway)));
         int z = Mth.floor(96.0 * Math.sin(2.0 * (-3.141592653589793 + 0.15707963267948966 * (double)gateway)));
         this.spawnNewGateway(new BlockPos(x, 75, z));
      }
   }

   private void spawnNewGateway(final BlockPos pos) {
      this.level.levelEvent(3000, pos, 0);
      this.level.registryAccess().lookup(Registries.CONFIGURED_FEATURE).flatMap((registry) -> registry.get(EndFeatures.END_GATEWAY_DELAYED)).ifPresent((endGateway) -> ((ConfiguredFeature)endGateway.value()).place(this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), pos));
   }

   private void spawnExitPortal(final boolean activated) {
      EndPodiumFeature feature = new EndPodiumFeature(activated);
      if (this.portalLocation == null) {
         for(this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(this.origin)).below(); this.level.getBlockState(this.portalLocation).is(Blocks.BEDROCK) && this.portalLocation.getY() > 63; this.portalLocation = this.portalLocation.below()) {
         }

         this.portalLocation = this.portalLocation.atY(Math.max(this.level.getMinY() + 1, this.portalLocation.getY()));
      }

      if (feature.place(FeatureConfiguration.NONE, this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), this.portalLocation)) {
         int chunkRadius = Mth.positiveCeilDiv(4, 16);
         this.level.getChunkSource().chunkMap.waitForLightBeforeSending(ChunkPos.containing(this.portalLocation), chunkRadius);
      }

   }

   private @Nullable EnderDragon createNewDragon() {
      this.level.getChunkAt(new BlockPos(this.origin.getX(), 128 + this.origin.getY(), this.origin.getZ()));
      EnderDragon dragon = EntityType.ENDER_DRAGON.create(this.level, EntitySpawnReason.EVENT);
      if (dragon != null) {
         dragon.setDragonFight(this);
         dragon.setFightOrigin(this.origin);
         dragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
         dragon.snapTo((double)this.origin.getX(), (double)(128 + this.origin.getY()), (double)this.origin.getZ(), this.level.getRandom().nextFloat() * 360.0F, 0.0F);
         this.level.addFreshEntity(dragon);
         this.dragonUUID = dragon.getUUID();
      }

      return dragon;
   }

   public void updateDragon(final EnderDragon dragon) {
      if (dragon.getUUID().equals(this.dragonUUID)) {
         this.dragonEvent.setProgress(dragon.getHealth() / dragon.getMaxHealth());
         this.ticksSinceDragonSeen = 0;
         if (dragon.hasCustomName()) {
            this.dragonEvent.setName(dragon.getDisplayName());
         }
      }

   }

   public int getCrystalsAlive() {
      return this.crystalsAlive;
   }

   public void onCrystalDestroyed(final EndCrystal crystal, final DamageSource source) {
      if (this.respawnStage != null && this.respawnCrystals.contains(crystal)) {
         LOGGER.debug("Aborting respawn sequence");
         this.respawnStage = null;
         this.respawnTime = 0;
         this.resetSpikeCrystals();
         this.spawnExitPortal(true);
      } else {
         this.updateCrystalCount();
         Entity dragon = this.level.getEntity(this.dragonUUID);
         if (dragon instanceof EnderDragon) {
            EnderDragon actuallyDragon = (EnderDragon)dragon;
            actuallyDragon.onCrystalDestroyed(this.level, crystal, crystal.blockPosition(), source);
         }
      }

   }

   public boolean hasPreviouslyKilledDragon() {
      return this.previouslyKilled;
   }

   public void tryRespawn() {
      if (this.dragonKilled && this.respawnStage == null) {
         BlockPos location = this.portalLocation;
         if (location == null) {
            LOGGER.debug("Tried to respawn, but need to find the portal first.");
            BlockPattern.BlockPatternMatch match = this.findExitPortal();
            if (match == null) {
               LOGGER.debug("Couldn't find a portal, so we made one.");
               this.spawnExitPortal(true);
            } else {
               LOGGER.debug("Found the exit portal & saved its location for next time.");
            }

            location = this.portalLocation;
         }

         List<EndCrystal> crystals = Lists.newArrayList();
         BlockPos center = location.above(1);

         for(Direction direction : Direction.Plane.HORIZONTAL) {
            List<EndCrystal> found = this.level.getEntitiesOfClass(EndCrystal.class, new AABB(center.relative((Direction)direction, 3)));
            if (found.isEmpty()) {
               return;
            }

            crystals.addAll(found);
         }

         LOGGER.debug("Found all crystals, respawning dragon.");
         this.respawnDragon(crystals);
      }

   }

   private void respawnDragon(final List<EndCrystal> crystals) {
      if (this.dragonKilled && this.respawnStage == null) {
         for(BlockPattern.BlockPatternMatch portal = this.findExitPortal(); portal != null; portal = this.findExitPortal()) {
            for(int x = 0; x < this.exitPortalPattern.getWidth(); ++x) {
               for(int y = 0; y < this.exitPortalPattern.getHeight(); ++y) {
                  for(int z = 0; z < this.exitPortalPattern.getDepth(); ++z) {
                     BlockInWorld block = portal.getBlock(x, y, z);
                     if (block.getState().is(Blocks.BEDROCK) || block.getState().is(Blocks.END_PORTAL)) {
                        this.level.setBlockAndUpdate(block.getPos(), Blocks.END_STONE.defaultBlockState());
                     }
                  }
               }
            }
         }

         this.respawnStage = DragonRespawnAnimation.START;
         this.respawnTime = 0;
         this.spawnExitPortal(false);
         this.respawnCrystals = crystals;
      }

   }

   public void resetSpikeCrystals() {
      for(SpikeFeature.EndSpike spike : SpikeFeature.getSpikesForLevel(this.level)) {
         for(EndCrystal crystal : this.level.getEntitiesOfClass(EndCrystal.class, spike.getTopBoundingBox())) {
            crystal.setInvulnerable(false);
            crystal.setBeamTarget((BlockPos)null);
         }
      }

   }

   public @Nullable UUID getDragonUUID() {
      return this.dragonUUID;
   }

   public static record Data(boolean needsStateScanning, boolean dragonKilled, boolean previouslyKilled, boolean isRespawning, Optional<UUID> dragonUUID, Optional<BlockPos> exitPortalLocation, Optional<List<Integer>> gateways) {
      public static final Codec<Data> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.BOOL.fieldOf("NeedsStateScanning").orElse(true).forGetter(Data::needsStateScanning), Codec.BOOL.fieldOf("DragonKilled").orElse(false).forGetter(Data::dragonKilled), Codec.BOOL.fieldOf("PreviouslyKilled").orElse(false).forGetter(Data::previouslyKilled), Codec.BOOL.lenientOptionalFieldOf("IsRespawning", false).forGetter(Data::isRespawning), UUIDUtil.CODEC.lenientOptionalFieldOf("Dragon").forGetter(Data::dragonUUID), BlockPos.CODEC.lenientOptionalFieldOf("ExitPortalLocation").forGetter(Data::exitPortalLocation), Codec.list(Codec.INT).lenientOptionalFieldOf("Gateways").forGetter(Data::gateways)).apply(i, Data::new));
      public static final Data DEFAULT = new Data(true, false, false, false, Optional.empty(), Optional.empty(), Optional.empty());

      public Data {
         super();
      }
   }
}
