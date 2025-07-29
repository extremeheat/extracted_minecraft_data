package net.minecraft.world.entity.raid;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.SectionPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Raid {
   public static final SpawnPlacementType RAVAGER_SPAWN_PLACEMENT_TYPE;
   public static final MapCodec<Raid> MAP_CODEC;
   private static final int ALLOW_SPAWNING_WITHIN_VILLAGE_SECONDS_THRESHOLD = 7;
   private static final int SECTION_RADIUS_FOR_FINDING_NEW_VILLAGE_CENTER = 2;
   private static final int VILLAGE_SEARCH_RADIUS = 32;
   private static final int RAID_TIMEOUT_TICKS = 48000;
   private static final int NUM_SPAWN_ATTEMPTS = 5;
   private static final Component OMINOUS_BANNER_PATTERN_NAME;
   private static final String RAIDERS_REMAINING = "event.minecraft.raid.raiders_remaining";
   public static final int VILLAGE_RADIUS_BUFFER = 16;
   private static final int POST_RAID_TICK_LIMIT = 40;
   private static final int DEFAULT_PRE_RAID_TICKS = 300;
   public static final int MAX_NO_ACTION_TIME = 2400;
   public static final int MAX_CELEBRATION_TICKS = 600;
   private static final int OUTSIDE_RAID_BOUNDS_TIMEOUT = 30;
   public static final int TICKS_PER_DAY = 24000;
   public static final int DEFAULT_MAX_RAID_OMEN_LEVEL = 5;
   private static final int LOW_MOB_THRESHOLD = 2;
   private static final Component RAID_NAME_COMPONENT;
   private static final Component RAID_BAR_VICTORY_COMPONENT;
   private static final Component RAID_BAR_DEFEAT_COMPONENT;
   private static final int HERO_OF_THE_VILLAGE_DURATION = 48000;
   private static final int VALID_RAID_RADIUS = 96;
   public static final int VALID_RAID_RADIUS_SQR = 9216;
   public static final int RAID_REMOVAL_THRESHOLD_SQR = 12544;
   private final Map<Integer, Raider> groupToLeaderMap = Maps.newHashMap();
   private final Map<Integer, Set<Raider>> groupRaiderMap = Maps.newHashMap();
   private final Set<UUID> heroesOfTheVillage = Sets.newHashSet();
   private long ticksActive;
   private BlockPos center;
   private boolean started;
   private float totalHealth;
   private int raidOmenLevel;
   private boolean active;
   private int groupsSpawned;
   private final ServerBossEvent raidEvent;
   private int postRaidTicks;
   private int raidCooldownTicks;
   private final RandomSource random;
   private final int numGroups;
   private RaidStatus status;
   private int celebrationTicks;
   private Optional<BlockPos> waveSpawnPos;

   public Raid(BlockPos var1, Difficulty var2) {
      super();
      this.raidEvent = new ServerBossEvent(RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
      this.random = RandomSource.create();
      this.waveSpawnPos = Optional.empty();
      this.active = true;
      this.raidCooldownTicks = 300;
      this.raidEvent.setProgress(0.0F);
      this.center = var1;
      this.numGroups = this.getNumGroups(var2);
      this.status = Raid.RaidStatus.ONGOING;
   }

   private Raid(boolean var1, boolean var2, long var3, int var5, int var6, int var7, int var8, float var9, int var10, RaidStatus var11, BlockPos var12, Set<UUID> var13) {
      super();
      this.raidEvent = new ServerBossEvent(RAID_NAME_COMPONENT, BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
      this.random = RandomSource.create();
      this.waveSpawnPos = Optional.empty();
      this.started = var1;
      this.active = var2;
      this.ticksActive = var3;
      this.raidOmenLevel = var5;
      this.groupsSpawned = var6;
      this.raidCooldownTicks = var7;
      this.postRaidTicks = var8;
      this.totalHealth = var9;
      this.center = var12;
      this.numGroups = var10;
      this.status = var11;
      this.heroesOfTheVillage.addAll(var13);
   }

   public boolean isOver() {
      return this.isVictory() || this.isLoss();
   }

   public boolean isBetweenWaves() {
      return this.hasFirstWaveSpawned() && this.getTotalRaidersAlive() == 0 && this.raidCooldownTicks > 0;
   }

   public boolean hasFirstWaveSpawned() {
      return this.groupsSpawned > 0;
   }

   public boolean isStopped() {
      return this.status == Raid.RaidStatus.STOPPED;
   }

   public boolean isVictory() {
      return this.status == Raid.RaidStatus.VICTORY;
   }

   public boolean isLoss() {
      return this.status == Raid.RaidStatus.LOSS;
   }

   public float getTotalHealth() {
      return this.totalHealth;
   }

   public Set<Raider> getAllRaiders() {
      HashSet var1 = Sets.newHashSet();

      for(Set var3 : this.groupRaiderMap.values()) {
         var1.addAll(var3);
      }

      return var1;
   }

   public boolean isStarted() {
      return this.started;
   }

   public int getGroupsSpawned() {
      return this.groupsSpawned;
   }

   private Predicate<ServerPlayer> validPlayer() {
      return (var1) -> {
         BlockPos var2 = var1.blockPosition();
         return var1.isAlive() && var1.level().getRaidAt(var2) == this;
      };
   }

   private void updatePlayers(ServerLevel var1) {
      HashSet var2 = Sets.newHashSet(this.raidEvent.getPlayers());
      List var3 = var1.getPlayers(this.validPlayer());

      for(ServerPlayer var5 : var3) {
         if (!var2.contains(var5)) {
            this.raidEvent.addPlayer(var5);
         }
      }

      for(ServerPlayer var7 : var2) {
         if (!var3.contains(var7)) {
            this.raidEvent.removePlayer(var7);
         }
      }

   }

   public int getMaxRaidOmenLevel() {
      return 5;
   }

   public int getRaidOmenLevel() {
      return this.raidOmenLevel;
   }

   public void setRaidOmenLevel(int var1) {
      this.raidOmenLevel = var1;
   }

   public boolean absorbRaidOmen(ServerPlayer var1) {
      MobEffectInstance var2 = var1.getEffect(MobEffects.RAID_OMEN);
      if (var2 == null) {
         return false;
      } else {
         this.raidOmenLevel += var2.getAmplifier() + 1;
         this.raidOmenLevel = Mth.clamp(this.raidOmenLevel, 0, this.getMaxRaidOmenLevel());
         if (!this.hasFirstWaveSpawned()) {
            var1.awardStat(Stats.RAID_TRIGGER);
            CriteriaTriggers.RAID_OMEN.trigger(var1);
         }

         return true;
      }
   }

   public void stop() {
      this.active = false;
      this.raidEvent.removeAllPlayers();
      this.status = Raid.RaidStatus.STOPPED;
   }

   public void tick(ServerLevel var1) {
      if (!this.isStopped()) {
         if (this.status == Raid.RaidStatus.ONGOING) {
            boolean var2 = this.active;
            this.active = var1.hasChunkAt(this.center);
            if (var1.getDifficulty() == Difficulty.PEACEFUL) {
               this.stop();
               return;
            }

            if (var2 != this.active) {
               this.raidEvent.setVisible(this.active);
            }

            if (!this.active) {
               return;
            }

            if (!var1.isVillage(this.center)) {
               this.moveRaidCenterToNearbyVillageSection(var1);
            }

            if (!var1.isVillage(this.center)) {
               if (this.groupsSpawned > 0) {
                  this.status = Raid.RaidStatus.LOSS;
               } else {
                  this.stop();
               }
            }

            ++this.ticksActive;
            if (this.ticksActive >= 48000L) {
               this.stop();
               return;
            }

            int var3 = this.getTotalRaidersAlive();
            if (var3 == 0 && this.hasMoreWaves()) {
               if (this.raidCooldownTicks <= 0) {
                  if (this.raidCooldownTicks == 0 && this.groupsSpawned > 0) {
                     this.raidCooldownTicks = 300;
                     this.raidEvent.setName(RAID_NAME_COMPONENT);
                     return;
                  }
               } else {
                  boolean var4 = this.waveSpawnPos.isPresent();
                  boolean var5 = !var4 && this.raidCooldownTicks % 5 == 0;
                  if (var4 && !var1.isPositionEntityTicking((BlockPos)this.waveSpawnPos.get())) {
                     var5 = true;
                  }

                  if (var5) {
                     this.waveSpawnPos = this.getValidSpawnPos(var1);
                  }

                  if (this.raidCooldownTicks == 300 || this.raidCooldownTicks % 20 == 0) {
                     this.updatePlayers(var1);
                  }

                  --this.raidCooldownTicks;
                  this.raidEvent.setProgress(Mth.clamp((float)(300 - this.raidCooldownTicks) / 300.0F, 0.0F, 1.0F));
               }
            }

            if (this.ticksActive % 20L == 0L) {
               this.updatePlayers(var1);
               this.updateRaiders(var1);
               if (var3 > 0) {
                  if (var3 <= 2) {
                     this.raidEvent.setName(RAID_NAME_COMPONENT.copy().append(" - ").append((Component)Component.translatable("event.minecraft.raid.raiders_remaining", var3)));
                  } else {
                     this.raidEvent.setName(RAID_NAME_COMPONENT);
                  }
               } else {
                  this.raidEvent.setName(RAID_NAME_COMPONENT);
               }
            }

            boolean var11 = false;
            int var12 = 0;

            while(this.shouldSpawnGroup()) {
               BlockPos var6 = (BlockPos)this.waveSpawnPos.orElseGet(() -> this.findRandomSpawnPos(var1, 20));
               if (var6 != null) {
                  this.started = true;
                  this.spawnGroup(var1, var6);
                  if (!var11) {
                     this.playSound(var1, var6);
                     var11 = true;
                  }
               } else {
                  ++var12;
               }

               if (var12 > 5) {
                  this.stop();
                  break;
               }
            }

            if (this.isStarted() && !this.hasMoreWaves() && var3 == 0) {
               if (this.postRaidTicks < 40) {
                  ++this.postRaidTicks;
               } else {
                  this.status = Raid.RaidStatus.VICTORY;

                  for(UUID var7 : this.heroesOfTheVillage) {
                     Entity var8 = var1.getEntity(var7);
                     if (var8 instanceof LivingEntity) {
                        LivingEntity var9 = (LivingEntity)var8;
                        if (!var8.isSpectator()) {
                           var9.addEffect(new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 48000, this.raidOmenLevel - 1, false, false, true));
                           if (var9 instanceof ServerPlayer) {
                              ServerPlayer var10 = (ServerPlayer)var9;
                              var10.awardStat(Stats.RAID_WIN);
                              CriteriaTriggers.RAID_WIN.trigger(var10);
                           }
                        }
                     }
                  }
               }
            }

            this.setDirty(var1);
         } else if (this.isOver()) {
            ++this.celebrationTicks;
            if (this.celebrationTicks >= 600) {
               this.stop();
               return;
            }

            if (this.celebrationTicks % 20 == 0) {
               this.updatePlayers(var1);
               this.raidEvent.setVisible(true);
               if (this.isVictory()) {
                  this.raidEvent.setProgress(0.0F);
                  this.raidEvent.setName(RAID_BAR_VICTORY_COMPONENT);
               } else {
                  this.raidEvent.setName(RAID_BAR_DEFEAT_COMPONENT);
               }
            }
         }

      }
   }

   private void moveRaidCenterToNearbyVillageSection(ServerLevel var1) {
      Stream var2 = SectionPos.cube(SectionPos.of(this.center), 2);
      Objects.requireNonNull(var1);
      var2.filter(var1::isVillage).map(SectionPos::center).min(Comparator.comparingDouble((var1x) -> var1x.distSqr(this.center))).ifPresent(this::setCenter);
   }

   private Optional<BlockPos> getValidSpawnPos(ServerLevel var1) {
      BlockPos var2 = this.findRandomSpawnPos(var1, 8);
      return var2 != null ? Optional.of(var2) : Optional.empty();
   }

   private boolean hasMoreWaves() {
      if (this.hasBonusWave()) {
         return !this.hasSpawnedBonusWave();
      } else {
         return !this.isFinalWave();
      }
   }

   private boolean isFinalWave() {
      return this.getGroupsSpawned() == this.numGroups;
   }

   private boolean hasBonusWave() {
      return this.raidOmenLevel > 1;
   }

   private boolean hasSpawnedBonusWave() {
      return this.getGroupsSpawned() > this.numGroups;
   }

   private boolean shouldSpawnBonusGroup() {
      return this.isFinalWave() && this.getTotalRaidersAlive() == 0 && this.hasBonusWave();
   }

   private void updateRaiders(ServerLevel var1) {
      Iterator var2 = this.groupRaiderMap.values().iterator();
      HashSet var3 = Sets.newHashSet();

      while(var2.hasNext()) {
         Set var4 = (Set)var2.next();

         for(Raider var6 : var4) {
            BlockPos var7 = var6.blockPosition();
            if (!var6.isRemoved() && var6.level().dimension() == var1.dimension() && !(this.center.distSqr(var7) >= 12544.0)) {
               if (var6.tickCount > 600) {
                  if (var1.getEntity(var6.getUUID()) == null) {
                     var3.add(var6);
                  }

                  if (!var1.isVillage(var7) && var6.getNoActionTime() > 2400) {
                     var6.setTicksOutsideRaid(var6.getTicksOutsideRaid() + 1);
                  }

                  if (var6.getTicksOutsideRaid() >= 30) {
                     var3.add(var6);
                  }
               }
            } else {
               var3.add(var6);
            }
         }
      }

      for(Raider var9 : var3) {
         this.removeFromRaid(var1, var9, true);
         if (var9.isPatrolLeader()) {
            this.removeLeader(var9.getWave());
         }
      }

   }

   private void playSound(ServerLevel var1, BlockPos var2) {
      float var3 = 13.0F;
      boolean var4 = true;
      Collection var5 = this.raidEvent.getPlayers();
      long var6 = this.random.nextLong();

      for(ServerPlayer var9 : var1.players()) {
         Vec3 var10 = var9.position();
         Vec3 var11 = Vec3.atCenterOf(var2);
         double var12 = Math.sqrt((var11.x - var10.x) * (var11.x - var10.x) + (var11.z - var10.z) * (var11.z - var10.z));
         double var14 = var10.x + 13.0 / var12 * (var11.x - var10.x);
         double var16 = var10.z + 13.0 / var12 * (var11.z - var10.z);
         if (var12 <= 64.0 || var5.contains(var9)) {
            var9.connection.send(new ClientboundSoundPacket(SoundEvents.RAID_HORN, SoundSource.NEUTRAL, var14, var9.getY(), var16, 64.0F, 1.0F, var6));
         }
      }

   }

   private void spawnGroup(ServerLevel var1, BlockPos var2) {
      boolean var3 = false;
      int var4 = this.groupsSpawned + 1;
      this.totalHealth = 0.0F;
      DifficultyInstance var5 = var1.getCurrentDifficultyAt(var2);
      boolean var6 = this.shouldSpawnBonusGroup();

      for(RaiderType var10 : Raid.RaiderType.VALUES) {
         int var11 = this.getDefaultNumSpawns(var10, var4, var6) + this.getPotentialBonusSpawns(var10, this.random, var4, var5, var6);
         int var12 = 0;

         for(int var13 = 0; var13 < var11; ++var13) {
            Raider var14 = var10.entityType.create(var1, EntitySpawnReason.EVENT);
            if (var14 == null) {
               break;
            }

            if (!var3 && var14.canBeLeader()) {
               var14.setPatrolLeader(true);
               this.setLeader(var4, var14);
               var3 = true;
            }

            this.joinRaid(var1, var4, var14, var2, false);
            if (var10.entityType == EntityType.RAVAGER) {
               Raider var15 = null;
               if (var4 == this.getNumGroups(Difficulty.NORMAL)) {
                  var15 = EntityType.PILLAGER.create(var1, EntitySpawnReason.EVENT);
               } else if (var4 >= this.getNumGroups(Difficulty.HARD)) {
                  if (var12 == 0) {
                     var15 = EntityType.EVOKER.create(var1, EntitySpawnReason.EVENT);
                  } else {
                     var15 = EntityType.VINDICATOR.create(var1, EntitySpawnReason.EVENT);
                  }
               }

               ++var12;
               if (var15 != null) {
                  this.joinRaid(var1, var4, var15, var2, false);
                  var15.snapTo(var2, 0.0F, 0.0F);
                  var15.startRiding(var14, false, false);
               }
            }
         }
      }

      this.waveSpawnPos = Optional.empty();
      ++this.groupsSpawned;
      this.updateBossbar();
      this.setDirty(var1);
   }

   public void joinRaid(ServerLevel var1, int var2, Raider var3, @Nullable BlockPos var4, boolean var5) {
      boolean var6 = this.addWaveMob(var1, var2, var3);
      if (var6) {
         var3.setCurrentRaid(this);
         var3.setWave(var2);
         var3.setCanJoinRaid(true);
         var3.setTicksOutsideRaid(0);
         if (!var5 && var4 != null) {
            var3.setPos((double)var4.getX() + 0.5, (double)var4.getY() + 1.0, (double)var4.getZ() + 0.5);
            var3.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var4), EntitySpawnReason.EVENT, (SpawnGroupData)null);
            var3.applyRaidBuffs(var1, var2, false);
            var3.setOnGround(true);
            var1.addFreshEntityWithPassengers(var3);
         }
      }

   }

   public void updateBossbar() {
      this.raidEvent.setProgress(Mth.clamp(this.getHealthOfLivingRaiders() / this.totalHealth, 0.0F, 1.0F));
   }

   public float getHealthOfLivingRaiders() {
      float var1 = 0.0F;

      for(Set var3 : this.groupRaiderMap.values()) {
         for(Raider var5 : var3) {
            var1 += var5.getHealth();
         }
      }

      return var1;
   }

   private boolean shouldSpawnGroup() {
      return this.raidCooldownTicks == 0 && (this.groupsSpawned < this.numGroups || this.shouldSpawnBonusGroup()) && this.getTotalRaidersAlive() == 0;
   }

   public int getTotalRaidersAlive() {
      return this.groupRaiderMap.values().stream().mapToInt(Set::size).sum();
   }

   public void removeFromRaid(ServerLevel var1, Raider var2, boolean var3) {
      Set var4 = (Set)this.groupRaiderMap.get(var2.getWave());
      if (var4 != null) {
         boolean var5 = var4.remove(var2);
         if (var5) {
            if (var3) {
               this.totalHealth -= var2.getHealth();
            }

            var2.setCurrentRaid((Raid)null);
            this.updateBossbar();
            this.setDirty(var1);
         }
      }

   }

   private void setDirty(ServerLevel var1) {
      var1.getRaids().setDirty();
   }

   public static ItemStack getOminousBannerInstance(HolderGetter<BannerPattern> var0) {
      ItemStack var1 = new ItemStack(Items.WHITE_BANNER);
      BannerPatternLayers var2 = (new BannerPatternLayers.Builder()).addIfRegistered(var0, BannerPatterns.RHOMBUS_MIDDLE, DyeColor.CYAN).addIfRegistered(var0, BannerPatterns.STRIPE_BOTTOM, DyeColor.LIGHT_GRAY).addIfRegistered(var0, BannerPatterns.STRIPE_CENTER, DyeColor.GRAY).addIfRegistered(var0, BannerPatterns.BORDER, DyeColor.LIGHT_GRAY).addIfRegistered(var0, BannerPatterns.STRIPE_MIDDLE, DyeColor.BLACK).addIfRegistered(var0, BannerPatterns.HALF_HORIZONTAL, DyeColor.LIGHT_GRAY).addIfRegistered(var0, BannerPatterns.CIRCLE_MIDDLE, DyeColor.LIGHT_GRAY).addIfRegistered(var0, BannerPatterns.BORDER, DyeColor.BLACK).build();
      var1.set(DataComponents.BANNER_PATTERNS, var2);
      var1.set(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT.withHidden(DataComponents.BANNER_PATTERNS, true));
      var1.set(DataComponents.ITEM_NAME, OMINOUS_BANNER_PATTERN_NAME);
      var1.set(DataComponents.RARITY, Rarity.UNCOMMON);
      return var1;
   }

   @Nullable
   public Raider getLeader(int var1) {
      return (Raider)this.groupToLeaderMap.get(var1);
   }

   @Nullable
   private BlockPos findRandomSpawnPos(ServerLevel var1, int var2) {
      int var3 = this.raidCooldownTicks / 20;
      float var4 = 0.22F * (float)var3 - 0.24F;
      BlockPos.MutableBlockPos var8 = new BlockPos.MutableBlockPos();
      float var9 = var1.random.nextFloat() * 6.2831855F;

      for(int var10 = 0; var10 < var2; ++var10) {
         float var11 = var9 + 3.1415927F * (float)var10 / 8.0F;
         int var5 = this.center.getX() + Mth.floor(Mth.cos(var11) * 32.0F * var4) + var1.random.nextInt(3) * Mth.floor(var4);
         int var7 = this.center.getZ() + Mth.floor(Mth.sin(var11) * 32.0F * var4) + var1.random.nextInt(3) * Mth.floor(var4);
         int var6 = var1.getHeight(Heightmap.Types.WORLD_SURFACE, var5, var7);
         if (Mth.abs(var6 - this.center.getY()) <= 96) {
            var8.set(var5, var6, var7);
            if (!var1.isVillage((BlockPos)var8) || var3 <= 7) {
               boolean var12 = true;
               if (var1.hasChunksAt(var8.getX() - 10, var8.getZ() - 10, var8.getX() + 10, var8.getZ() + 10) && var1.isPositionEntityTicking(var8) && (RAVAGER_SPAWN_PLACEMENT_TYPE.isSpawnPositionOk(var1, var8, EntityType.RAVAGER) || var1.getBlockState(var8.below()).is(Blocks.SNOW) && var1.getBlockState(var8).isAir())) {
                  return var8;
               }
            }
         }
      }

      return null;
   }

   private boolean addWaveMob(ServerLevel var1, int var2, Raider var3) {
      return this.addWaveMob(var1, var2, var3, true);
   }

   public boolean addWaveMob(ServerLevel var1, int var2, Raider var3, boolean var4) {
      this.groupRaiderMap.computeIfAbsent(var2, (var0) -> Sets.newHashSet());
      Set var5 = (Set)this.groupRaiderMap.get(var2);
      Raider var6 = null;

      for(Raider var8 : var5) {
         if (var8.getUUID().equals(var3.getUUID())) {
            var6 = var8;
            break;
         }
      }

      if (var6 != null) {
         var5.remove(var6);
         var5.add(var3);
      }

      var5.add(var3);
      if (var4) {
         this.totalHealth += var3.getHealth();
      }

      this.updateBossbar();
      this.setDirty(var1);
      return true;
   }

   public void setLeader(int var1, Raider var2) {
      this.groupToLeaderMap.put(var1, var2);
      var2.setItemSlot(EquipmentSlot.HEAD, getOminousBannerInstance(var2.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN)));
      var2.setDropChance(EquipmentSlot.HEAD, 2.0F);
   }

   public void removeLeader(int var1) {
      this.groupToLeaderMap.remove(var1);
   }

   public BlockPos getCenter() {
      return this.center;
   }

   private void setCenter(BlockPos var1) {
      this.center = var1;
   }

   private int getDefaultNumSpawns(RaiderType var1, int var2, boolean var3) {
      return var3 ? var1.spawnsPerWaveBeforeBonus[this.numGroups] : var1.spawnsPerWaveBeforeBonus[var2];
   }

   private int getPotentialBonusSpawns(RaiderType var1, RandomSource var2, int var3, DifficultyInstance var4, boolean var5) {
      Difficulty var6 = var4.getDifficulty();
      boolean var7 = var6 == Difficulty.EASY;
      boolean var8 = var6 == Difficulty.NORMAL;
      int var9;
      switch (var1.ordinal()) {
         case 0:
         case 2:
            if (var7) {
               var9 = var2.nextInt(2);
            } else if (var8) {
               var9 = 1;
            } else {
               var9 = 2;
            }
            break;
         case 1:
         default:
            return 0;
         case 3:
            if (var7 || var3 <= 2 || var3 == 4) {
               return 0;
            }

            var9 = 1;
            break;
         case 4:
            var9 = !var7 && var5 ? 1 : 0;
      }

      return var9 > 0 ? var2.nextInt(var9 + 1) : 0;
   }

   public boolean isActive() {
      return this.active;
   }

   public int getNumGroups(Difficulty var1) {
      byte var10000;
      switch (var1) {
         case PEACEFUL -> var10000 = 0;
         case EASY -> var10000 = 3;
         case NORMAL -> var10000 = 5;
         case HARD -> var10000 = 7;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public float getEnchantOdds() {
      int var1 = this.getRaidOmenLevel();
      if (var1 == 2) {
         return 0.1F;
      } else if (var1 == 3) {
         return 0.25F;
      } else if (var1 == 4) {
         return 0.5F;
      } else {
         return var1 == 5 ? 0.75F : 0.0F;
      }
   }

   public void addHeroOfTheVillage(Entity var1) {
      this.heroesOfTheVillage.add(var1.getUUID());
   }

   static {
      RAVAGER_SPAWN_PLACEMENT_TYPE = SpawnPlacements.getPlacementType(EntityType.RAVAGER);
      MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.BOOL.fieldOf("started").forGetter((var0x) -> var0x.started), Codec.BOOL.fieldOf("active").forGetter((var0x) -> var0x.active), Codec.LONG.fieldOf("ticks_active").forGetter((var0x) -> var0x.ticksActive), Codec.INT.fieldOf("raid_omen_level").forGetter((var0x) -> var0x.raidOmenLevel), Codec.INT.fieldOf("groups_spawned").forGetter((var0x) -> var0x.groupsSpawned), Codec.INT.fieldOf("cooldown_ticks").forGetter((var0x) -> var0x.raidCooldownTicks), Codec.INT.fieldOf("post_raid_ticks").forGetter((var0x) -> var0x.postRaidTicks), Codec.FLOAT.fieldOf("total_health").forGetter((var0x) -> var0x.totalHealth), Codec.INT.fieldOf("group_count").forGetter((var0x) -> var0x.numGroups), Raid.RaidStatus.CODEC.fieldOf("status").forGetter((var0x) -> var0x.status), BlockPos.CODEC.fieldOf("center").forGetter((var0x) -> var0x.center), UUIDUtil.CODEC_SET.fieldOf("heroes_of_the_village").forGetter((var0x) -> var0x.heroesOfTheVillage)).apply(var0, Raid::new));
      OMINOUS_BANNER_PATTERN_NAME = Component.translatable("block.minecraft.ominous_banner");
      RAID_NAME_COMPONENT = Component.translatable("event.minecraft.raid");
      RAID_BAR_VICTORY_COMPONENT = Component.translatable("event.minecraft.raid.victory.full");
      RAID_BAR_DEFEAT_COMPONENT = Component.translatable("event.minecraft.raid.defeat.full");
   }

   static enum RaidStatus implements StringRepresentable {
      ONGOING("ongoing"),
      VICTORY("victory"),
      LOSS("loss"),
      STOPPED("stopped");

      public static final Codec<RaidStatus> CODEC = StringRepresentable.<RaidStatus>fromEnum(RaidStatus::values);
      private final String name;

      private RaidStatus(final String var3) {
         this.name = var3;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static RaidStatus[] $values() {
         return new RaidStatus[]{ONGOING, VICTORY, LOSS, STOPPED};
      }
   }

   static enum RaiderType {
      VINDICATOR(EntityType.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}),
      EVOKER(EntityType.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}),
      PILLAGER(EntityType.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}),
      WITCH(EntityType.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}),
      RAVAGER(EntityType.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

      static final RaiderType[] VALUES = values();
      final EntityType<? extends Raider> entityType;
      final int[] spawnsPerWaveBeforeBonus;

      private RaiderType(final EntityType<? extends Raider> var3, final int[] var4) {
         this.entityType = var3;
         this.spawnsPerWaveBeforeBonus = var4;
      }

      // $FF: synthetic method
      private static RaiderType[] $values() {
         return new RaiderType[]{VINDICATOR, EVOKER, PILLAGER, WITCH, RAVAGER};
      }
   }
}
