package net.minecraft.world.entity.npc;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.SpawnUtil;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ReputationEventHandler;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.VillagerGoalPackages;
import net.minecraft.world.entity.ai.gossip.GossipContainer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.NearestVisibleLivingEntities;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.sensing.GolemSensor;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.ReputationEventType;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;

public class Villager extends AbstractVillager implements ReputationEventHandler, VillagerDataHolder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final EntityDataAccessor<VillagerData> DATA_VILLAGER_DATA = SynchedEntityData.defineId(Villager.class, EntityDataSerializers.VILLAGER_DATA);
   public static final int BREEDING_FOOD_THRESHOLD = 12;
   public static final Map<Item, Integer> FOOD_POINTS = ImmutableMap.of(Items.BREAD, 4, Items.POTATO, 1, Items.CARROT, 1, Items.BEETROOT, 1);
   private static final int TRADES_PER_LEVEL = 2;
   private static final Set<Item> WANTED_ITEMS = ImmutableSet.of(
      Items.BREAD,
      Items.POTATO,
      Items.CARROT,
      Items.WHEAT,
      Items.WHEAT_SEEDS,
      Items.BEETROOT,
      new Item[]{Items.BEETROOT_SEEDS, Items.TORCHFLOWER_SEEDS, Items.PITCHER_POD}
   );
   private static final int MAX_GOSSIP_TOPICS = 10;
   private static final int GOSSIP_COOLDOWN = 1200;
   private static final int GOSSIP_DECAY_INTERVAL = 24000;
   private static final int REPUTATION_CHANGE_PER_EVENT = 25;
   private static final int HOW_FAR_AWAY_TO_TALK_TO_OTHER_VILLAGERS_ABOUT_GOLEMS = 10;
   private static final int HOW_MANY_VILLAGERS_NEED_TO_AGREE_TO_SPAWN_A_GOLEM = 5;
   private static final long TIME_SINCE_SLEEPING_FOR_GOLEM_SPAWNING = 24000L;
   @VisibleForTesting
   public static final float SPEED_MODIFIER = 0.5F;
   private int updateMerchantTimer;
   private boolean increaseProfessionLevelOnUpdate;
   @Nullable
   private Player lastTradedPlayer;
   private boolean chasing;
   private int foodLevel;
   private final GossipContainer gossips = new GossipContainer();
   private long lastGossipTime;
   private long lastGossipDecayTime;
   private int villagerXp;
   private long lastRestockGameTime;
   private int numberOfRestocksToday;
   private long lastRestockCheckDayTime;
   private boolean assignProfessionWhenSpawned;
   private static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
      MemoryModuleType.HOME,
      MemoryModuleType.JOB_SITE,
      MemoryModuleType.POTENTIAL_JOB_SITE,
      MemoryModuleType.MEETING_POINT,
      MemoryModuleType.NEAREST_LIVING_ENTITIES,
      MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
      MemoryModuleType.VISIBLE_VILLAGER_BABIES,
      MemoryModuleType.NEAREST_PLAYERS,
      MemoryModuleType.NEAREST_VISIBLE_PLAYER,
      MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
      MemoryModuleType.NEAREST_VISIBLE_WANTED_ITEM,
      MemoryModuleType.ITEM_PICKUP_COOLDOWN_TICKS,
      new MemoryModuleType[]{
         MemoryModuleType.WALK_TARGET,
         MemoryModuleType.LOOK_TARGET,
         MemoryModuleType.INTERACTION_TARGET,
         MemoryModuleType.BREED_TARGET,
         MemoryModuleType.PATH,
         MemoryModuleType.DOORS_TO_CLOSE,
         MemoryModuleType.NEAREST_BED,
         MemoryModuleType.HURT_BY,
         MemoryModuleType.HURT_BY_ENTITY,
         MemoryModuleType.NEAREST_HOSTILE,
         MemoryModuleType.SECONDARY_JOB_SITE,
         MemoryModuleType.HIDING_PLACE,
         MemoryModuleType.HEARD_BELL_TIME,
         MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
         MemoryModuleType.LAST_SLEPT,
         MemoryModuleType.LAST_WOKEN,
         MemoryModuleType.LAST_WORKED_AT_POI,
         MemoryModuleType.GOLEM_DETECTED_RECENTLY
      }
   );
   private static final ImmutableList<SensorType<? extends Sensor<? super Villager>>> SENSOR_TYPES = ImmutableList.of(
      SensorType.NEAREST_LIVING_ENTITIES,
      SensorType.NEAREST_PLAYERS,
      SensorType.NEAREST_ITEMS,
      SensorType.NEAREST_BED,
      SensorType.HURT_BY,
      SensorType.VILLAGER_HOSTILES,
      SensorType.VILLAGER_BABIES,
      SensorType.SECONDARY_POIS,
      SensorType.GOLEM_DETECTED
   );
   public static final Map<MemoryModuleType<GlobalPos>, BiPredicate<Villager, Holder<PoiType>>> POI_MEMORIES = ImmutableMap.of(
      MemoryModuleType.HOME,
      (BiPredicate<Villager, Holder>)(var0, var1) -> var1.is(PoiTypes.HOME),
      MemoryModuleType.JOB_SITE,
      (BiPredicate<Villager, Holder>)(var0, var1) -> var0.getVillagerData().getProfession().heldJobSite().test(var1),
      MemoryModuleType.POTENTIAL_JOB_SITE,
      (BiPredicate<Villager, Holder>)(var0, var1) -> VillagerProfession.ALL_ACQUIRABLE_JOBS.test(var1),
      MemoryModuleType.MEETING_POINT,
      (BiPredicate<Villager, Holder>)(var0, var1) -> var1.is(PoiTypes.MEETING)
   );

   public Villager(EntityType<? extends Villager> var1, Level var2) {
      this(var1, var2, VillagerType.PLAINS);
   }

   public Villager(EntityType<? extends Villager> var1, Level var2, VillagerType var3) {
      super(var1, var2);
      ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      this.getNavigation().setCanFloat(true);
      this.setCanPickUpLoot(true);
      this.setVillagerData(this.getVillagerData().setType(var3).setProfession(VillagerProfession.NONE));
   }

   @Override
   public Brain<Villager> getBrain() {
      return super.getBrain();
   }

   @Override
   protected Brain.Provider<Villager> brainProvider() {
      return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
   }

   @Override
   protected Brain<?> makeBrain(Dynamic<?> var1) {
      Brain var2 = this.brainProvider().makeBrain(var1);
      this.registerBrainGoals(var2);
      return var2;
   }

   public void refreshBrain(ServerLevel var1) {
      Brain var2 = this.getBrain();
      var2.stopAll(var1, this);
      this.brain = var2.copyWithoutBehaviors();
      this.registerBrainGoals(this.getBrain());
   }

   private void registerBrainGoals(Brain<Villager> var1) {
      VillagerProfession var2 = this.getVillagerData().getProfession();
      if (this.isBaby()) {
         var1.setSchedule(Schedule.VILLAGER_BABY);
         var1.addActivity(Activity.PLAY, VillagerGoalPackages.getPlayPackage(0.5F));
      } else {
         var1.setSchedule(Schedule.VILLAGER_DEFAULT);
         var1.addActivityWithConditions(
            Activity.WORK, VillagerGoalPackages.getWorkPackage(var2, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT))
         );
      }

      var1.addActivity(Activity.CORE, VillagerGoalPackages.getCorePackage(var2, 0.5F));
      var1.addActivityWithConditions(
         Activity.MEET, VillagerGoalPackages.getMeetPackage(var2, 0.5F), ImmutableSet.of(Pair.of(MemoryModuleType.MEETING_POINT, MemoryStatus.VALUE_PRESENT))
      );
      var1.addActivity(Activity.REST, VillagerGoalPackages.getRestPackage(var2, 0.5F));
      var1.addActivity(Activity.IDLE, VillagerGoalPackages.getIdlePackage(var2, 0.5F));
      var1.addActivity(Activity.PANIC, VillagerGoalPackages.getPanicPackage(var2, 0.5F));
      var1.addActivity(Activity.PRE_RAID, VillagerGoalPackages.getPreRaidPackage(var2, 0.5F));
      var1.addActivity(Activity.RAID, VillagerGoalPackages.getRaidPackage(var2, 0.5F));
      var1.addActivity(Activity.HIDE, VillagerGoalPackages.getHidePackage(var2, 0.5F));
      var1.setCoreActivities(ImmutableSet.of(Activity.CORE));
      var1.setDefaultActivity(Activity.IDLE);
      var1.setActiveActivityIfPossible(Activity.IDLE);
      var1.updateActivityFromSchedule(this.level().getDayTime(), this.level().getGameTime());
   }

   @Override
   protected void ageBoundaryReached() {
      super.ageBoundaryReached();
      if (this.level() instanceof ServerLevel) {
         this.refreshBrain((ServerLevel)this.level());
      }
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.FOLLOW_RANGE, 48.0);
   }

   public boolean assignProfessionWhenSpawned() {
      return this.assignProfessionWhenSpawned;
   }

   @Override
   protected void customServerAiStep() {
      this.level().getProfiler().push("villagerBrain");
      this.getBrain().tick((ServerLevel)this.level(), this);
      this.level().getProfiler().pop();
      if (this.assignProfessionWhenSpawned) {
         this.assignProfessionWhenSpawned = false;
      }

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

      if (this.lastTradedPlayer != null && this.level() instanceof ServerLevel) {
         ((ServerLevel)this.level()).onReputationEvent(ReputationEventType.TRADE, this.lastTradedPlayer, this);
         this.level().broadcastEntityEvent(this, (byte)14);
         this.lastTradedPlayer = null;
      }

      if (!this.isNoAi() && this.random.nextInt(100) == 0) {
         Raid var1 = ((ServerLevel)this.level()).getRaidAt(this.blockPosition());
         if (var1 != null && var1.isActive() && !var1.isOver()) {
            this.level().broadcastEntityEvent(this, (byte)42);
         }
      }

      if (this.getVillagerData().getProfession() == VillagerProfession.NONE && this.isTrading()) {
         this.stopTrading();
      }

      super.customServerAiStep();
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getUnhappyCounter() > 0) {
         this.setUnhappyCounter(this.getUnhappyCounter() - 1);
      }

      this.maybeDecayGossip();
   }

   @Override
   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.VILLAGER_SPAWN_EGG) || !this.isAlive() || this.isTrading() || this.isSleeping()) {
         return super.mobInteract(var1, var2);
      } else if (this.isBaby()) {
         this.setUnhappy();
         return InteractionResult.sidedSuccess(this.level().isClientSide);
      } else {
         boolean var4 = this.getOffers().isEmpty();
         if (var2 == InteractionHand.MAIN_HAND) {
            if (var4 && !this.level().isClientSide) {
               this.setUnhappy();
            }

            var1.awardStat(Stats.TALKED_TO_VILLAGER);
         }

         if (var4) {
            return InteractionResult.sidedSuccess(this.level().isClientSide);
         } else {
            if (!this.level().isClientSide && !this.offers.isEmpty()) {
               this.startTrading(var1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
         }
      }
   }

   private void setUnhappy() {
      this.setUnhappyCounter(40);
      if (!this.level().isClientSide()) {
         this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
      }
   }

   private void startTrading(Player var1) {
      this.updateSpecialPrices(var1);
      this.setTradingPlayer(var1);
      this.openTradingScreen(var1, this.getDisplayName(), this.getVillagerData().getLevel());
   }

   @Override
   public void setTradingPlayer(@Nullable Player var1) {
      boolean var2 = this.getTradingPlayer() != null && var1 == null;
      super.setTradingPlayer(var1);
      if (var2) {
         this.stopTrading();
      }
   }

   @Override
   protected void stopTrading() {
      super.stopTrading();
      this.resetSpecialPrices();
   }

   private void resetSpecialPrices() {
      for(MerchantOffer var2 : this.getOffers()) {
         var2.resetSpecialPriceDiff();
      }
   }

   @Override
   public boolean canRestock() {
      return true;
   }

   @Override
   public boolean isClientSide() {
      return this.level().isClientSide;
   }

   public void restock() {
      this.updateDemand();

      for(MerchantOffer var2 : this.getOffers()) {
         var2.resetUses();
      }

      this.resendOffersToTradingPlayer();
      this.lastRestockGameTime = this.level().getGameTime();
      ++this.numberOfRestocksToday;
   }

   private void resendOffersToTradingPlayer() {
      MerchantOffers var1 = this.getOffers();
      Player var2 = this.getTradingPlayer();
      if (var2 != null && !var1.isEmpty()) {
         var2.sendMerchantOffers(
            var2.containerMenu.containerId, var1, this.getVillagerData().getLevel(), this.getVillagerXp(), this.showProgressBar(), this.canRestock()
         );
      }
   }

   private boolean needsToRestock() {
      for(MerchantOffer var2 : this.getOffers()) {
         if (var2.needsRestock()) {
            return true;
         }
      }

      return false;
   }

   private boolean allowedToRestock() {
      return this.numberOfRestocksToday == 0 || this.numberOfRestocksToday < 2 && this.level().getGameTime() > this.lastRestockGameTime + 2400L;
   }

   public boolean shouldRestock() {
      long var1 = this.lastRestockGameTime + 12000L;
      long var3 = this.level().getGameTime();
      boolean var5 = var3 > var1;
      long var6 = this.level().getDayTime();
      if (this.lastRestockCheckDayTime > 0L) {
         long var8 = this.lastRestockCheckDayTime / 24000L;
         long var10 = var6 / 24000L;
         var5 |= var10 > var8;
      }

      this.lastRestockCheckDayTime = var6;
      if (var5) {
         this.lastRestockGameTime = var3;
         this.resetNumberOfRestocks();
      }

      return this.allowedToRestock() && this.needsToRestock();
   }

   private void catchUpDemand() {
      int var1 = 2 - this.numberOfRestocksToday;
      if (var1 > 0) {
         for(MerchantOffer var3 : this.getOffers()) {
            var3.resetUses();
         }
      }

      for(int var4 = 0; var4 < var1; ++var4) {
         this.updateDemand();
      }

      this.resendOffersToTradingPlayer();
   }

   private void updateDemand() {
      for(MerchantOffer var2 : this.getOffers()) {
         var2.updateDemand();
      }
   }

   private void updateSpecialPrices(Player var1) {
      int var2 = this.getPlayerReputation(var1);
      if (var2 != 0) {
         for(MerchantOffer var4 : this.getOffers()) {
            var4.addToSpecialPriceDiff(-Mth.floor((float)var2 * var4.getPriceMultiplier()));
         }
      }

      if (var1.hasEffect(MobEffects.HERO_OF_THE_VILLAGE)) {
         MobEffectInstance var10 = var1.getEffect(MobEffects.HERO_OF_THE_VILLAGE);
         int var11 = var10.getAmplifier();

         for(MerchantOffer var6 : this.getOffers()) {
            double var7 = 0.3 + 0.0625 * (double)var11;
            int var9 = (int)Math.floor(var7 * (double)var6.getBaseCostA().getCount());
            var6.addToSpecialPriceDiff(-Math.max(var9, 1));
         }
      }
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_VILLAGER_DATA, new VillagerData(VillagerType.PLAINS, VillagerProfession.NONE, 1));
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      VillagerData.CODEC
         .encodeStart(NbtOps.INSTANCE, this.getVillagerData())
         .resultOrPartial(LOGGER::error)
         .ifPresent(var1x -> var1.put("VillagerData", var1x));
      var1.putByte("FoodLevel", (byte)this.foodLevel);
      var1.put("Gossips", this.gossips.store(NbtOps.INSTANCE));
      var1.putInt("Xp", this.villagerXp);
      var1.putLong("LastRestock", this.lastRestockGameTime);
      var1.putLong("LastGossipDecay", this.lastGossipDecayTime);
      var1.putInt("RestocksToday", this.numberOfRestocksToday);
      if (this.assignProfessionWhenSpawned) {
         var1.putBoolean("AssignProfessionWhenSpawned", true);
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("VillagerData", 10)) {
         DataResult var2 = VillagerData.CODEC.parse(new Dynamic(NbtOps.INSTANCE, var1.get("VillagerData")));
         var2.resultOrPartial(LOGGER::error).ifPresent(this::setVillagerData);
      }

      if (var1.contains("Offers", 10)) {
         this.offers = new MerchantOffers(var1.getCompound("Offers"));
      }

      if (var1.contains("FoodLevel", 1)) {
         this.foodLevel = var1.getByte("FoodLevel");
      }

      ListTag var3 = var1.getList("Gossips", 10);
      this.gossips.update(new Dynamic(NbtOps.INSTANCE, var3));
      if (var1.contains("Xp", 3)) {
         this.villagerXp = var1.getInt("Xp");
      }

      this.lastRestockGameTime = var1.getLong("LastRestock");
      this.lastGossipDecayTime = var1.getLong("LastGossipDecay");
      this.setCanPickUpLoot(true);
      if (this.level() instanceof ServerLevel) {
         this.refreshBrain((ServerLevel)this.level());
      }

      this.numberOfRestocksToday = var1.getInt("RestocksToday");
      if (var1.contains("AssignProfessionWhenSpawned")) {
         this.assignProfessionWhenSpawned = var1.getBoolean("AssignProfessionWhenSpawned");
      }
   }

   @Override
   public boolean removeWhenFarAway(double var1) {
      return false;
   }

   @Nullable
   @Override
   protected SoundEvent getAmbientSound() {
      if (this.isSleeping()) {
         return null;
      } else {
         return this.isTrading() ? SoundEvents.VILLAGER_TRADE : SoundEvents.VILLAGER_AMBIENT;
      }
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.VILLAGER_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.VILLAGER_DEATH;
   }

   public void playWorkSound() {
      SoundEvent var1 = this.getVillagerData().getProfession().workSound();
      if (var1 != null) {
         this.playSound(var1, this.getSoundVolume(), this.getVoicePitch());
      }
   }

   @Override
   public void setVillagerData(VillagerData var1) {
      VillagerData var2 = this.getVillagerData();
      if (var2.getProfession() != var1.getProfession()) {
         this.offers = null;
      }

      this.entityData.set(DATA_VILLAGER_DATA, var1);
   }

   @Override
   public VillagerData getVillagerData() {
      return this.entityData.get(DATA_VILLAGER_DATA);
   }

   @Override
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
         this.level().addFreshEntity(new ExperienceOrb(this.level(), this.getX(), this.getY() + 0.5, this.getZ(), var2));
      }
   }

   public void setChasing(boolean var1) {
      this.chasing = var1;
   }

   public boolean isChasing() {
      return this.chasing;
   }

   @Override
   public void setLastHurtByMob(@Nullable LivingEntity var1) {
      if (var1 != null && this.level() instanceof ServerLevel) {
         ((ServerLevel)this.level()).onReputationEvent(ReputationEventType.VILLAGER_HURT, var1, this);
         if (this.isAlive() && var1 instanceof Player) {
            this.level().broadcastEntityEvent(this, (byte)13);
         }
      }

      super.setLastHurtByMob(var1);
   }

   @Override
   public void die(DamageSource var1) {
      LOGGER.info("Villager {} died, message: '{}'", this, var1.getLocalizedDeathMessage(this).getString());
      Entity var2 = var1.getEntity();
      if (var2 != null) {
         this.tellWitnessesThatIWasMurdered(var2);
      }

      this.releaseAllPois();
      super.die(var1);
   }

   private void releaseAllPois() {
      this.releasePoi(MemoryModuleType.HOME);
      this.releasePoi(MemoryModuleType.JOB_SITE);
      this.releasePoi(MemoryModuleType.POTENTIAL_JOB_SITE);
      this.releasePoi(MemoryModuleType.MEETING_POINT);
   }

   private void tellWitnessesThatIWasMurdered(Entity var1) {
      Level var3 = this.level();
      if (var3 instanceof ServerLevel var2) {
         Optional var4 = this.brain.getMemory(MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES);
         if (!var4.isEmpty()) {
            ((NearestVisibleLivingEntities)var4.get())
               .findAll(ReputationEventHandler.class::isInstance)
               .forEach(var2x -> var2.onReputationEvent(ReputationEventType.VILLAGER_KILLED, var1, (ReputationEventHandler)var2x));
         }
      }
   }

   public void releasePoi(MemoryModuleType<GlobalPos> var1) {
      if (this.level() instanceof ServerLevel) {
         MinecraftServer var2 = ((ServerLevel)this.level()).getServer();
         this.brain.<GlobalPos>getMemory(var1).ifPresent(var3 -> {
            ServerLevel var4 = var2.getLevel(var3.dimension());
            if (var4 != null) {
               PoiManager var5 = var4.getPoiManager();
               Optional var6 = var5.getType(var3.pos());
               BiPredicate var7 = POI_MEMORIES.get(var1);
               if (var6.isPresent() && var7.test(this, (Holder)var6.get())) {
                  var5.release(var3.pos());
                  DebugPackets.sendPoiTicketCountPacket(var4, var3.pos());
               }
            }
         });
      }
   }

   @Override
   public boolean canBreed() {
      return this.foodLevel + this.countFoodPointsInInventory() >= 12 && !this.isSleeping() && this.getAge() == 0;
   }

   private boolean hungry() {
      return this.foodLevel < 12;
   }

   private void eatUntilFull() {
      if (this.hungry() && this.countFoodPointsInInventory() != 0) {
         for(int var1 = 0; var1 < this.getInventory().getContainerSize(); ++var1) {
            ItemStack var2 = this.getInventory().getItem(var1);
            if (!var2.isEmpty()) {
               Integer var3 = FOOD_POINTS.get(var2.getItem());
               if (var3 != null) {
                  int var4 = var2.getCount();

                  for(int var5 = var4; var5 > 0; --var5) {
                     this.foodLevel += var3;
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
      return this.gossips.getReputation(var1.getUUID(), var0 -> true);
   }

   private void digestFood(int var1) {
      this.foodLevel -= var1;
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

   @Override
   protected Component getTypeName() {
      return Component.translatable(
         this.getType().getDescriptionId() + "." + BuiltInRegistries.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession()).getPath()
      );
   }

   @Override
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
   @Override
   public SpawnGroupData finalizeSpawn(
      ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5
   ) {
      if (var3 == MobSpawnType.BREEDING) {
         this.setVillagerData(this.getVillagerData().setProfession(VillagerProfession.NONE));
      }

      if (var3 == MobSpawnType.COMMAND || var3 == MobSpawnType.SPAWN_EGG || var3 == MobSpawnType.SPAWNER || var3 == MobSpawnType.DISPENSER) {
         this.setVillagerData(this.getVillagerData().setType(VillagerType.byBiome(var1.getBiome(this.blockPosition()))));
      }

      if (var3 == MobSpawnType.STRUCTURE) {
         this.assignProfessionWhenSpawned = true;
      }

      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   @Nullable
   public Villager getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      double var4 = this.random.nextDouble();
      VillagerType var3;
      if (var4 < 0.5) {
         var3 = VillagerType.byBiome(var1.getBiome(this.blockPosition()));
      } else if (var4 < 0.75) {
         var3 = this.getVillagerData().getType();
      } else {
         var3 = ((Villager)var2).getVillagerData().getType();
      }

      Villager var6 = new Villager(EntityType.VILLAGER, var1, var3);
      var6.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var6.blockPosition()), MobSpawnType.BREEDING, null, null);
      return var6;
   }

   @Override
   public void thunderHit(ServerLevel var1, LightningBolt var2) {
      if (var1.getDifficulty() != Difficulty.PEACEFUL) {
         LOGGER.info("Villager {} was struck by lightning {}.", this, var2);
         Witch var3 = EntityType.WITCH.create(var1);
         if (var3 != null) {
            var3.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            var3.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var3.blockPosition()), MobSpawnType.CONVERSION, null, null);
            var3.setNoAi(this.isNoAi());
            if (this.hasCustomName()) {
               var3.setCustomName(this.getCustomName());
               var3.setCustomNameVisible(this.isCustomNameVisible());
            }

            var3.setPersistenceRequired();
            var1.addFreshEntityWithPassengers(var3);
            this.releaseAllPois();
            this.discard();
         } else {
            super.thunderHit(var1, var2);
         }
      } else {
         super.thunderHit(var1, var2);
      }
   }

   @Override
   protected void pickUpItem(ItemEntity var1) {
      InventoryCarrier.pickUpItem(this, this, var1);
   }

   @Override
   public boolean wantsToPickUp(ItemStack var1) {
      Item var2 = var1.getItem();
      return (WANTED_ITEMS.contains(var2) || this.getVillagerData().getProfession().requestedItems().contains(var2)) && this.getInventory().canAddItem(var1);
   }

   public boolean hasExcessFood() {
      return this.countFoodPointsInInventory() >= 24;
   }

   public boolean wantsMoreFood() {
      return this.countFoodPointsInInventory() < 12;
   }

   private int countFoodPointsInInventory() {
      SimpleContainer var1 = this.getInventory();
      return FOOD_POINTS.entrySet().stream().mapToInt(var1x -> var1.countItem(var1x.getKey()) * var1x.getValue()).sum();
   }

   public boolean hasFarmSeeds() {
      return this.getInventory().hasAnyMatching(var0 -> var0.is(ItemTags.VILLAGER_PLANTABLE_SEEDS));
   }

   @Override
   protected void updateTrades() {
      VillagerData var1 = this.getVillagerData();
      Int2ObjectMap var2;
      if (this.level().enabledFeatures().contains(FeatureFlags.TRADE_REBALANCE)) {
         Int2ObjectMap var3 = (Int2ObjectMap)VillagerTrades.EXPERIMENTAL_TRADES.get(var1.getProfession());
         var2 = var3 != null ? var3 : (Int2ObjectMap)VillagerTrades.TRADES.get(var1.getProfession());
      } else {
         var2 = (Int2ObjectMap)VillagerTrades.TRADES.get(var1.getProfession());
      }

      if (var2 != null && !var2.isEmpty()) {
         VillagerTrades.ItemListing[] var5 = (VillagerTrades.ItemListing[])var2.get(var1.getLevel());
         if (var5 != null) {
            MerchantOffers var4 = this.getOffers();
            this.addOffersFromItemListings(var4, var5, 2);
         }
      }
   }

   public void gossip(ServerLevel var1, Villager var2, long var3) {
      if ((var3 < this.lastGossipTime || var3 >= this.lastGossipTime + 1200L) && (var3 < var2.lastGossipTime || var3 >= var2.lastGossipTime + 1200L)) {
         this.gossips.transferFrom(var2.gossips, this.random, 10);
         this.lastGossipTime = var3;
         var2.lastGossipTime = var3;
         this.spawnGolemIfNeeded(var1, var3, 5);
      }
   }

   private void maybeDecayGossip() {
      long var1 = this.level().getGameTime();
      if (this.lastGossipDecayTime == 0L) {
         this.lastGossipDecayTime = var1;
      } else if (var1 >= this.lastGossipDecayTime + 24000L) {
         this.gossips.decay();
         this.lastGossipDecayTime = var1;
      }
   }

   public void spawnGolemIfNeeded(ServerLevel var1, long var2, int var4) {
      if (this.wantsToSpawnGolem(var2)) {
         AABB var5 = this.getBoundingBox().inflate(10.0, 10.0, 10.0);
         List var6 = var1.getEntitiesOfClass(Villager.class, var5);
         List var7 = var6.stream().filter(var2x -> var2x.wantsToSpawnGolem(var2)).limit(5L).collect(Collectors.toList());
         if (var7.size() >= var4) {
            if (!SpawnUtil.trySpawnMob(
                  EntityType.IRON_GOLEM, MobSpawnType.MOB_SUMMONED, var1, this.blockPosition(), 10, 8, 6, SpawnUtil.Strategy.LEGACY_IRON_GOLEM
               )
               .isEmpty()) {
               var6.forEach(GolemSensor::golemDetected);
            }
         }
      }
   }

   public boolean wantsToSpawnGolem(long var1) {
      if (!this.golemSpawnConditionsMet(this.level().getGameTime())) {
         return false;
      } else {
         return !this.brain.hasMemoryValue(MemoryModuleType.GOLEM_DETECTED_RECENTLY);
      }
   }

   @Override
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

   @Override
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

   @Override
   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   @Override
   public void startSleeping(BlockPos var1) {
      super.startSleeping(var1);
      this.brain.setMemory(MemoryModuleType.LAST_SLEPT, this.level().getGameTime());
      this.brain.eraseMemory(MemoryModuleType.WALK_TARGET);
      this.brain.eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
   }

   @Override
   public void stopSleeping() {
      super.stopSleeping();
      this.brain.setMemory(MemoryModuleType.LAST_WOKEN, this.level().getGameTime());
   }

   private boolean golemSpawnConditionsMet(long var1) {
      Optional var3 = this.brain.getMemory(MemoryModuleType.LAST_SLEPT);
      if (var3.isPresent()) {
         return var1 - var3.get() < 24000L;
      } else {
         return false;
      }
   }
}
