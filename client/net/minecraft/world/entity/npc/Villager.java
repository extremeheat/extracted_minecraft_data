package net.minecraft.world.entity.npc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SerializableLong;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.global.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;

public class Villager extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {
   private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA;
   public static final Map<Item, Integer> FOOD_POINTS;
   private static final Set<Item> WANTED_ITEMS;
   private int updateMerchantTimer;
   private boolean increaseProfessionLevelOnUpdate;
   @Nullable
   private Player lastTradedPlayer;
   private byte foodLevel;
   private final GossipContainer gossips;
   private long lastGossipTime;
   private long lastGossipDecayTime;
   private int villagerXp;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;
   private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES;
   public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<Villager, PoiType>> POI_MEMORIES;

   public Villager(EntityType<? extends Villager> var1, Level var2) {
      this(var1, var2, VillagerType.PLAINS);
   }

   public Villager(EntityType<? extends Villager> var1, Level var2, VillagerType var3) {
      super(var1, var2);
      this.gossips = new GossipContainer();
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      this.getNavigation().setCanFloat(true);
      this.setCanPickUpLoot(true);
      this.setVillagerData(this.getVillagerData().setType(var3).setProfession(VillagerProfession.NONE));
      this.brain = this.makeBrain(new Dynamic(NbtOps.INSTANCE, new CompoundTag()));
   }

   public Brain<Villager> getBrain() {
      return super.getBrain();
   }

   protected Brain<?> makeBrain(Dynamic<?> var1) {
      Brain var2 = new Brain(MEMORY_TYPES, SENSOR_TYPES, var1);
      this.registerBrainGoals(var2);
      return var2;
   }

   public void refreshBrain(ServerLevel var1) {
      Brain var2 = this.getBrain();
      var2.stopAll(var1, this);
      this.brain = var2.copyWithoutGoals();
      this.registerBrainGoals(this.getBrain());
   }

   private void registerBrainGoals(Brain<Villager> var1) {
      VillagerProfession var2 = this.getVillagerData().getProfession();
      float var3 = (float)this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue();
      if (this.isBaby()) {
         var1.setSchedule(Schedule.VILLAGER_BABY);
         var1.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(var3));
      } else {
         var1.setSchedule(Schedule.VILLAGER_DEFAULT);
         var1.addActivity(Activity.WORK, VillagerGoalPackages.getWorkPackage(var2, var3), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT)));
      }

      var1.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(var2, var3));
      var1.addActivity(Activity.MEET, VillagerGoalPackages.getMeetPackage(var2, var3), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT)));
      var1.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(var2, var3));
      var1.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(var2, var3));
      var1.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(var2, var3));
      var1.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(var2, var3));
      var1.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(var2, var3));
      var1.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(var2, var3));
      var1.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var1.setDefaultActivity(Activity.IDLE);
      var1.setActivity(Activity.IDLE);
      var1.updateActivity(this.level.getDayTime(), this.level.getGameTime());
   }

   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (this.level instanceof ServerLevel) {
         this.refreshBrain((ServerLevel)this.level);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(48.0D);
   }

   protected void customServerAiStep() {
      this.level.getProfiler().push("brain");
      this.getBrain().tick((ServerLevel)this.level, this);
      this.level.getProfiler().pop();
      if (!this.isTrading() && this.updateMerchantTimer > 0) {
         --this.updateMerchantTimer;
         if (this.updateMerchantTimer <= 0) {
            if (this.increaseProfessionLevelOnUpdate) {
               this.increaseMerchantCareer();
               this.increaseProfessionLevelOnUpdate = false;
            }

            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 0));
         }
      }

      if (this.lastTradedPlayer != null && this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).onReputationEvent(ReputationEventType.TRADE, this.lastTradedPlayer, this);
         this.level.broadcastEntityEvent(this, (byte)14);
         this.lastTradedPlayer = null;
      }

      if (!this.isNoAi() && this.random.nextInt(100) == 0) {
         Raid var1 = ((ServerLevel)this.level).getRaidAt(new BlockPos(this));
         if (var1 != null && var1.isActive() && !var1.isOver()) {
            this.level.broadcastEntityEvent(this, (byte)42);
         }
      }

      if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.isTrading()) {
         this.stopTrading();
      }

      super.customServerAiStep();
   }

   public void tick() {
      super.tick();
      if (this.getUnhappyCounter() > 0) {
         this.setUnhappyCounter(this.getUnhappyCounter() - 1);
      }

      this.maybeDecayGossip();
   }

   public boolean mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      boolean var4 = var3.getItem() == Items.NAME_TAG;
      if (var4) {
         var3.interactEnemy(var1, this, var2);
         return true;
      } else if (var3.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.isTrading() && !this.isSleeping()) {
         if (this.isBaby()) {
            this.setUnhappy();
            return super.mobInteract(var1, var2);
         } else {
            boolean var5 = this.getOffers().isEmpty();
            if (var2 == InteractionHand.MAIN_HAND) {
               if (var5 && !this.level.isClientSide) {
                  this.setUnhappy();
               }

               var1.awardStat(Stats.TALKED_TO_VILLAGER);
            }

            if (var5) {
               return super.mobInteract(var1, var2);
            } else {
               if (!this.level.isClientSide && !this.offers.isEmpty()) {
                  this.startTrading(var1);
               }

               return true;
            }
         }
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   private void setUnhappy() {
      this.setUnhappyCounter(40);
      if (!this.level.isClientSide()) {
         this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   private void startTrading(Player var1) {
      this.updateSpecialPrices(var1);
      this.setTradingPlayer(var1);
      this.openTradingScreen(var1, this.getDisplayName(), this.getVillagerData().getLevel());
   }

   public void setTradingPlayer(@Nullable Player var1) {
      boolean var2 = this.getTradingPlayer() != null && var1 == null;
      super.setTradingPlayer(var1);
      if (var2) {
         this.stopTrading();
      }

   }

   protected void stopTrading() {
      super.stopTrading();
      this.resetSpecialPrices();
   }

   private void resetSpecialPrices() {
      Iterator var1 = this.getOffers().iterator();

      while(var1.hasNext()) {
         MerchantOffer var2 = (MerchantOffer)var1.next();
         var2.resetSpecialPriceDiff();
      }

   }

   public boolean canRestock() {
      return true;
   }

   public void restock() {
      this.updateDemand();
      Iterator var1 = this.getOffers().iterator();

      while(var1.hasNext()) {
         MerchantOffer var2 = (MerchantOffer)var1.next();
         var2.resetUses();
      }

      if (this.getVillagerData().getProfession() == VillagerProfession.FARMER) {
         this.makeBread();
      }

      this.lastRestockGameTime = this.level.getGameTime();
      ++this.numberOfRestocksToday;
   }

   private boolean needsToRestock() {
      Iterator var1 = this.getOffers().iterator();

      MerchantOffer var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (MerchantOffer)var1.next();
      } while(!var2.isOutOfStock());

      return true;
   }

   private boolean allowedToRestock() {
      return this.numberOfRestocksToday < 2 && this.level.getGameTime() > this.lastRestockGameTime + 2400L;
   }

   public boolean shouldRestock() {
      long var1 = this.lastRestockGameTime + 12000L;
      boolean var3 = this.level.getGameTime() > var1;
      long var4 = this.level.getDayTime();
      if (this.lastRestockCheckDayTime > 0L) {
         long var6 = this.lastRestockCheckDayTime / 24000L;
         long var8 = var4 / 24000L;
         var3 |= var8 > var6;
      }

      this.lastRestockCheckDayTime = var4;
      if (var3) {
         this.resetNumberOfRestocks();
      }

      return this.allowedToRestock() && this.needsToRestock();
   }

   private void catchUpDemand() {
      int var1 = 2 - this.numberOfRestocksToday;
      if (var1 > 0) {
         Iterator var2 = this.getOffers().iterator();

         while(var2.hasNext()) {
            MerchantOffer var3 = (MerchantOffer)var2.next();
            var3.resetUses();
         }
      }

      for(int var4 = 0; var4 < var1; ++var4) {
         this.updateDemand();
      }

   }

   private void updateDemand() {
      Iterator var1 = this.getOffers().iterator();

      while(var1.hasNext()) {
         MerchantOffer var2 = (MerchantOffer)var1.next();
         var2.updateDemand();
      }

   }

   private void updateSpecialPrices(Player var1) {
      int var2 = this.getPlayerReputation(var1);
      if (var2 != 0) {
         Iterator var3 = this.getOffers().iterator();

         while(var3.hasNext()) {
            MerchantOffer var4 = (MerchantOffer)var3.next();
            var4.addToSpecialPriceDiff(-Mth.floor((float)var2 * var4.getPriceMultiplier()));
         }
      }

      if (var1.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
         MobEffectInstance var10 = var1.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
         int var11 = var10.getAmplifier();
         Iterator var5 = this.getOffers().iterator();

         while(var5.hasNext()) {
            MerchantOffer var6 = (MerchantOffer)var5.next();
            double var7 = 0.3D + 0.0625D * (double)var11;
            int var9 = (int)Math.floor(var7 * (double)var6.getBaseCostA().getCount());
            var6.addToSpecialPriceDiff(-Math.max(var9, 1));
         }
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("VillagerData", (Tag)this.getVillagerData().serialize(NbtOps.INSTANCE));
      var1.putByte("FoodLevel", this.foodLevel);
      var1.put("Gossips", (Tag)this.gossips.store(NbtOps.INSTANCE).getValue());
      var1.putInt("Xp", this.villagerXp);
      var1.putLong("LastRestock", this.lastRestockGameTime);
      var1.putLong("LastGossipDecay", this.lastGossipDecayTime);
      var1.putInt("RestocksToday", this.numberOfRestocksToday);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("VillagerData", 10)) {
         this.setVillagerData(new VillagerData(new Dynamic(NbtOps.INSTANCE, var1.get("VillagerData"))));
      }

      if (var1.contains("Offers", 10)) {
         this.offers = new MerchantOffers(var1.getCompound("Offers"));
      }

      if (var1.contains("FoodLevel", 1)) {
         this.foodLevel = var1.getByte("FoodLevel");
      }

      ListTag var2 = var1.getList("Gossips", 10);
      this.gossips.update(new Dynamic(NbtOps.INSTANCE, var2));
      if (var1.contains("Xp", 3)) {
         this.villagerXp = var1.getInt("Xp");
      }

      this.lastRestockGameTime = var1.getLong("LastRestock");
      this.lastGossipDecayTime = var1.getLong("LastGossipDecay");
      this.setCanPickUpLoot(true);
      this.refreshBrain((ServerLevel)this.level);
      this.numberOfRestocksToday = var1.getInt("RestocksToday");
   }

   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return null;
      } else {
         return this.isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
      }
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.VILLAGER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.VILLAGER_DEATH;
   }

   public void playWorkSound() {
      SoundEvent var1 = this.getVillagerData().getProfession().getJobPoiType().getUseSound();
      if (var1 != null) {
         this.playSound(var1, this.getSoundVolume(), this.getVoicePitch());
      }

   }

   public void setVillagerData(VillagerData var1) {
      VillagerData var2 = this.getVillagerData();
      if (var2.getProfession() != var1.getProfession()) {
         this.offers = null;
      }

      this.entityData.set(DATA_VILLAGER_DATA, var1);
   }

   public VillagerData getVillagerData() {
      return (VillagerData)this.entityData.get(DATA_VILLAGER_DATA);
   }

   protected void rewardTradeXp(MerchantOffer var1) {
      int var2 = 3 + this.random.nextInt(4);
      this.villagerXp += var1.getXp();
      this.lastTradedPlayer = this.getTradingPlayer();
      if (this.shouldIncreaseLevel()) {
         this.updateMerchantTimer = 40;
         this.increaseProfessionLevelOnUpdate = true;
         var2 += 5;
      }

      if (var1.shouldRewardExp()) {
         this.level.addFreshEntity(new ExperienceOrb(this.level, this.x, this.y + 0.5D, this.z, var2));
      }

   }

   public void setLastHurtByMob(@Nullable LivingEntity var1) {
      if (var1 != null && this.level instanceof ServerLevel) {
         ((ServerLevel)this.level).onReputationEvent(ReputationEventType.VILLAGER_HURT, var1, this);
         if (this.isAlive() && var1 instanceof Player) {
            this.level.broadcastEntityEvent(this, (byte)13);
         }
      }

      super.setLastHurtByMob(var1);
   }

   public void die(DamageSource var1) {
      Entity var2 = var1.getEntity();
      if (var2 != null) {
         this.tellWitnessesThatIWasMurdered(var2);
      }

      this.releasePoi(MemoryModuleType.HOME);
      this.releasePoi(MemoryModuleType.JOB_SITE);
      this.releasePoi(MemoryModuleType.MEETING_POINT);
      super.die(var1);
   }

   private void tellWitnessesThatIWasMurdered(Entity var1) {
      if (this.level instanceof ServerLevel) {
         Optional var2 = this.brain.getMemory(MemoryModuleType.VISIBLE_LIVING_ENTITIES);
         if (var2.isPresent()) {
            ServerLevel var3 = (ServerLevel)this.level;
            ((List)var2.get()).stream().filter((var0) -> {
               return var0 instanceof ReputationEventHandler;
            }).forEach((var2x) -> {
               var3.onReputationEvent(ReputationEventType.VILLAGER_KILLED, var1, (ReputationEventHandler)var2x);
            });
         }
      }
   }

   public void releasePoi(MemoryModuleType<GlobalPos> var1) {
      if (this.level instanceof ServerLevel) {
         MinecraftServer var2 = ((ServerLevel)this.level).getServer();
         this.brain.getMemory(var1).ifPresent((var3) -> {
            ServerLevel var4 = var2.getLevel(var3.dimension());
            PoiManager var5 = var4.getPoiManager();
            Optional var6 = var5.getType(var3.pos());
            BiPredicate var7 = (BiPredicate)POI_MEMORIES.get(var1);
            if (var6.isPresent() && var7.test(this, var6.get())) {
               var5.release(var3.pos());
               DebugPackets.sendPoiTicketCountPacket(var4, var3.pos());
            }

         });
      }
   }

   public boolean canBreed() {
      return this.foodLevel + this.countFoodPointsInInventory() >= 12 && this.getAge() == 0;
   }

   private boolean hungry() {
      return this.foodLevel < 12;
   }

   private void eatUntilFull() {
      if (this.hungry() && this.countFoodPointsInInventory() != 0) {
         for(int var1 = 0; var1 < this.getInventory().getContainerSize(); ++var1) {
            ItemStack var2 = this.getInventory().getItem(var1);
            if (!var2.isEmpty()) {
               Integer var3 = (Integer)FOOD_POINTS.get(var2.getItem());
               if (var3 != null) {
                  int var4 = var2.getCount();

                  for(int var5 = var4; var5 > 0; --var5) {
                     this.foodLevel = (byte)(this.foodLevel + var3);
                     this.getInventory().removeItem(var1, 1);
                     if (!this.hungry()) {
                        return;
                     }
                  }
               }
            }
         }

      }
   }

   public int getPlayerReputation(Player var1) {
      return this.gossips.getReputation(var1.getUUID(), (var0) -> {
         return true;
      });
   }

   private void digestFood(int var1) {
      this.foodLevel = (byte)(this.foodLevel - var1);
   }

   public void eatAndDigestFood() {
      this.eatUntilFull();
      this.digestFood(12);
   }

   public void setOffers(MerchantOffers var1) {
      this.offers = var1;
   }

   private boolean shouldIncreaseLevel() {
      int var1 = this.getVillagerData().getLevel();
      return VillagerData.canLevelUp(var1) && this.villagerXp >= VillagerData.getMaxXpPerLevel(var1);
   }

   private void increaseMerchantCareer() {
      this.setVillagerData(this.getVillagerData().setLevel(this.getVillagerData().getLevel() + 1));
      this.updateTrades();
   }

   public Component getDisplayName() {
      Team var1 = this.getTeam();
      Component var2 = this.getCustomName();
      if (var2 != null) {
         return PlayerTeam.formatNameForTeam(var1, var2).withStyle((var1x) -> {
            var1x.setHoverEvent(this.createHoverEvent()).setInsertion(this.getStringUUID());
         });
      } else {
         VillagerProfession var3 = this.getVillagerData().getProfession();
         Component var4 = (new TranslatableComponent(this.getType().getDescriptionId() + '.' + Registry.VILLAGER_PROFESSION.getKey(var3).getPath(), new Object[0])).withStyle((var1x) -> {
            var1x.setHoverEvent(this.createHoverEvent()).setInsertion(this.getStringUUID());
         });
         if (var1 != null) {
            var4.withStyle(var1.getColor());
         }

         return var4;
      }
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 12) {
         this.addParticlesAroundSelf(ParticleTypes.HEART);
      } else if (var1 == 13) {
         this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
      } else if (var1 == 14) {
         this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
      } else if (var1 == 42) {
         this.addParticlesAroundSelf(ParticleTypes.SPLASH);
      } else {
         super.handleEntityEvent(var1);
      }

   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      if (var3 == MobSpawnType.BREEDING) {
         this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));
      }

      if (var3 == MobSpawnType.COMMAND || var3 == MobSpawnType.SPAWN_EGG || var3 == MobSpawnType.SPAWNER) {
         this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(var1.getBiome(new BlockPos(this)))));
      }

      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   public Villager getBreedOffspring(AgableMob var1) {
      double var3 = this.random.nextDouble();
      VillagerType var2;
      if (var3 < 0.5D) {
         var2 = VillagerType.byBiome(this.level.getBiome(new BlockPos(this)));
      } else if (var3 < 0.75D) {
         var2 = this.getVillagerData().getType();
      } else {
         var2 = ((Villager)var1).getVillagerData().getType();
      }

      Villager var5 = new Villager(EntityType.VILLAGER, this.level, var2);
      var5.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(var5)), MobSpawnType.BREEDING, (SpawnGroupData)null, (CompoundTag)null);
      return var5;
   }

   public void thunderHit(LightningBolt var1) {
      Witch var2 = (Witch)EntityType.WITCH.create(this.level);
      var2.moveTo(this.x, this.y, this.z, this.yRot, this.xRot);
      var2.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(var2)), MobSpawnType.CONVERSION, (SpawnGroupData)null, (CompoundTag)null);
      var2.setNoAi(this.isNoAi());
      if (this.hasCustomName()) {
         var2.setCustomName(this.getCustomName());
         var2.setCustomNameVisible(this.isCustomNameVisible());
      }

      this.level.addFreshEntity(var2);
      this.remove();
   }

   protected void pickUpItem(ItemEntity var1) {
      ItemStack var2 = var1.getItem();
      Item var3 = var2.getItem();
      if (this.wantToPickUp(var3)) {
         SimpleContainer var4 = this.getInventory();
         boolean var5 = false;

         int var6;
         ItemStack var7;
         for(var6 = 0; var6 < var4.getContainerSize(); ++var6) {
            var7 = var4.getItem(var6);
            if (var7.isEmpty() || var7.getItem() == var3 && var7.getCount() < var7.getMaxStackSize()) {
               var5 = true;
               break;
            }
         }

         if (!var5) {
            return;
         }

         var6 = var4.countItem(var3);
         if (var6 == 256) {
            return;
         }

         if (var6 > 256) {
            var4.removeItemType(var3, var6 - 256);
            return;
         }

         this.take(var1, var2.getCount());
         var7 = var4.addItem(var2);
         if (var7.isEmpty()) {
            var1.remove();
         } else {
            var2.setCount(var7.getCount());
         }
      }

   }

   public boolean wantToPickUp(Item var1) {
      return WANTED_ITEMS.contains(var1) || this.getVillagerData().getProfession().getRequestedItems().contains(var1);
   }

   public boolean hasExcessFood() {
      return this.countFoodPointsInInventory() >= 24;
   }

   public boolean wantsMoreFood() {
      return this.countFoodPointsInInventory() < 12;
   }

   private int countFoodPointsInInventory() {
      SimpleContainer var1 = this.getInventory();
      return FOOD_POINTS.entrySet().stream().mapToInt((var1x) -> {
         return var1.countItem((Item)var1x.getKey()) * (Integer)var1x.getValue();
      }).sum();
   }

   private void makeBread() {
      SimpleContainer var1 = this.getInventory();
      int var2 = var1.countItem(Items.WHEAT);
      int var3 = var2 / 3;
      if (var3 != 0) {
         int var4 = var3 * 3;
         var1.removeItemType(Items.WHEAT, var4);
         ItemStack var5 = var1.addItem(new ItemStack(Items.BREAD, var3));
         if (!var5.isEmpty()) {
            this.spawnAtLocation(var5, 0.5F);
         }

      }
   }

   public boolean hasFarmSeeds() {
      SimpleContainer var1 = this.getInventory();
      return var1.hasAnyOf(ImmutableSet.of(Items.WHEAT_SEEDS, Items.POTATO, Items.CARROT, Items.BEETROOT_SEEDS));
   }

   protected void updateTrades() {
      VillagerData var1 = this.getVillagerData();
      Int2ObjectMap var2 = (Int2ObjectMap)VillagerTrades.TRADES.get(var1.getProfession());
      if (var2 != null && !var2.isEmpty()) {
         VillagerTrades.ItemListing[] var3 = (VillagerTrades.ItemListing[])var2.get(var1.getLevel());
         if (var3 != null) {
            MerchantOffers var4 = this.getOffers();
            this.addOffersFromItemListings(var4, var3, 2);
         }
      }
   }

   public void gossip(Villager var1, long var2) {
      if ((var2 < this.lastGossipTime || var2 >= this.lastGossipTime + 1200L) && (var2 < var1.lastGossipTime || var2 >= var1.lastGossipTime + 1200L)) {
         this.gossips.transferFrom(var1.gossips, this.random, 10);
         this.lastGossipTime = var2;
         var1.lastGossipTime = var2;
         this.spawnGolemIfNeeded(var2, 5);
      }
   }

   private void maybeDecayGossip() {
      long var1 = this.level.getGameTime();
      if (this.lastGossipDecayTime == 0L) {
         this.lastGossipDecayTime = var1;
      } else if (var1 >= this.lastGossipDecayTime + 24000L) {
         this.gossips.decay();
         this.lastGossipDecayTime = var1;
      }
   }

   public void spawnGolemIfNeeded(long var1, int var3) {
      if (this.wantsToSpawnGolem(var1)) {
         AABB var4 = this.getBoundingBox().inflate(10.0D, 10.0D, 10.0D);
         List var5 = this.level.getEntitiesOfClass(Villager.class, var4);
         List var6 = (List)var5.stream().filter((var2) -> {
            return var2.wantsToSpawnGolem(var1);
         }).limit(5L).collect(Collectors.toList());
         if (var6.size() >= var3) {
            IronGolem var7 = this.trySpawnGolem();
            if (var7 != null) {
               var5.forEach((var2) -> {
                  var2.sawGolem(var1);
               });
            }
         }
      }
   }

   private void sawGolem(long var1) {
      this.brain.setMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME, (Object)var1);
   }

   private boolean hasSeenGolemRecently(long var1) {
      Optional var3 = this.brain.getMemory(MemoryModuleType.GOLEM_LAST_SEEN_TIME);
      if (!var3.isPresent()) {
         return false;
      } else {
         Long var4 = (Long)var3.get();
         return var1 - var4 <= 600L;
      }
   }

   public boolean wantsToSpawnGolem(long var1) {
      VillagerData var3 = this.getVillagerData();
      if (var3.getProfession() != VillagerProfession.NONE && var3.getProfession() != VillagerProfession.NITWIT) {
         if (!this.golemSpawnConditionsMet(this.level.getGameTime())) {
            return false;
         } else {
            return !this.hasSeenGolemRecently(var1);
         }
      } else {
         return false;
      }
   }

   @Nullable
   private IronGolem trySpawnGolem() {
      BlockPos var1 = new BlockPos(this);

      for(int var2 = 0; var2 < 10; ++var2) {
         double var3 = (double)(this.level.random.nextInt(16) - 8);
         double var5 = (double)(this.level.random.nextInt(16) - 8);
         double var7 = 6.0D;

         for(int var9 = 0; var9 >= -12; --var9) {
            BlockPos var10 = var1.offset(var3, var7 + (double)var9, var5);
            if ((this.level.getBlockState(var10).isAir() || this.level.getBlockState(var10).getMaterial().isLiquid()) && this.level.getBlockState(var10.below()).getMaterial().isSolidBlocking()) {
               var7 += (double)var9;
               break;
            }
         }

         BlockPos var11 = var1.offset(var3, var7, var5);
         IronGolem var12 = (IronGolem)EntityType.IRON_GOLEM.create(this.level, (CompoundTag)null, (Component)null, (Player)null, var11, MobSpawnType.MOB_SUMMONED, false, false);
         if (var12 != null) {
            if (var12.checkSpawnRules(this.level, MobSpawnType.MOB_SUMMONED) && var12.checkSpawnObstruction(this.level)) {
               this.level.addFreshEntity(var12);
               return var12;
            }

            var12.remove();
         }
      }

      return null;
   }

   public void onReputationEventFrom(ReputationEventType var1, Entity var2) {
      if (var1 == ReputationEventType.ZOMBIE_VILLAGER_CURED) {
         this.gossips.add(var2.getUUID(), GossipType.MAJOR_POSITIVE, 20);
         this.gossips.add(var2.getUUID(), GossipType.MINOR_POSITIVE, 25);
      } else if (var1 == ReputationEventType.TRADE) {
         this.gossips.add(var2.getUUID(), GossipType.TRADING, 2);
      } else if (var1 == ReputationEventType.VILLAGER_HURT) {
         this.gossips.add(var2.getUUID(), GossipType.MINOR_NEGATIVE, 25);
      } else if (var1 == ReputationEventType.VILLAGER_KILLED) {
         this.gossips.add(var2.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
      }

   }

   public int getVillagerXp() {
      return this.villagerXp;
   }

   public void setVillagerXp(int var1) {
      this.villagerXp = var1;
   }

   private void resetNumberOfRestocks() {
      this.catchUpDemand();
      this.numberOfRestocksToday = 0;
   }

   public GossipContainer getGossips() {
      return this.gossips;
   }

   public void setGossips(Tag var1) {
      this.gossips.update(new Dynamic(NbtOps.INSTANCE, var1));
   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   public void startSleeping(BlockPos var1) {
      super.startSleeping(var1);
      this.brain.setMemory(MemoryModuleType.LAST_SLEPT, (Object)SerializableLong.of(this.level.getGameTime()));
   }

   private boolean golemSpawnConditionsMet(long var1) {
      Optional var3 = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);
      Optional var4 = this.brain.getMemory(MemoryModuleType.LAST_WORKED_AT_POI);
      if (var3.isPresent() && var4.isPresent()) {
         return var1 - ((SerializableLong)var3.get()).value() < 24000L && var1 - ((SerializableLong)var4.get()).value() < 36000L;
      } else {
         return false;
      }
   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   static {
      DATA_VILLAGER_DATA = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.VILLAGER_DATA);
      FOOD_POINTS = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
      WANTED_ITEMS = ImmutableSet.of(Items.BREAD, Items.POTATO, Items.CARROT, Items.WHEAT, Items.WHEAT_SEEDS, Items.BEETROOT, new Item[]{Items.BEETROOT_SEEDS});
      MEMORY_TYPES = ImmutableList.of(MemoryModuleType.HOME, MemoryModuleType.JOB_SITE, MemoryModuleType.MEETING_POINT, MemoryModuleType.LIVING_ENTITIES, MemoryModuleType.VISIBLE_LIVING_ENTITIES, MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryModuleType.NEAREST_PLAYERS, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryModuleType.WALK_TARGET, MemoryModuleType.LOOK_TARGET, MemoryModuleType.INTERACTION_TARGET, MemoryModuleType.BREED_TARGET, new MemoryModuleType[]{MemoryModuleType.PATH, MemoryModuleType.INTERACTABLE_DOORS, MemoryModuleType.OPENED_DOORS, MemoryModuleType.NEAREST_BED, MemoryModuleType.HURT_BY, MemoryModuleType.HURT_BY_ENTITY, MemoryModuleType.NEAREST_HOSTILE, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleType.HIDING_PLACE, MemoryModuleType.HEARD_BELL_TIME, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, MemoryModuleType.LAST_SLEPT, MemoryModuleType.LAST_WORKED_AT_POI, MemoryModuleType.GOLEM_LAST_SEEN_TIME});
      SENSOR_TYPES = ImmutableList.of(SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.INTERACTABLE_DOORS, SensorType.NEAREST_BED, SensorType.HURT_BY, SensorType.VILLAGER_HOSTILES, SensorType.VILLAGER_BABIES, SensorType.SECONDARY_POIS, SensorType.GOLEM_LAST_SEEN);
      POI_MEMORIES = ImmutableMap.of(MemoryModuleType.HOME, (var0, var1) -> {
         return var1 == PoiType.HOME;
      }, MemoryModuleType.JOB_SITE, (var0, var1) -> {
         return var0.getVillagerData().getProfession().getJobPoiType() == var1;
      }, MemoryModuleType.MEETING_POINT, (var0, var1) -> {
         return var1 == PoiType.MEETING;
      });
   }
}
