package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreakDoorGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveThroughVillageGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RemoveBlockGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Zombie extends Monster {
   private static final UUID SPEED_MODIFIER_BABY_UUID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
   private static final AttributeModifier SPEED_MODIFIER_BABY;
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID;
   private static final EntityDataAccessor<Integer> DATA_SPECIAL_TYPE_ID;
   private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID;
   private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE;
   private final BreakDoorGoal breakDoorGoal;
   private boolean canBreakDoors;
   private int inWaterTime;
   private int conversionTime;

   public Zombie(EntityType<? extends Zombie> var1, Level var2) {
      super(var1, var2);
      this.breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE);
   }

   public Zombie(Level var1) {
      this(EntityType.ZOMBIE, var1);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new Zombie.ZombieAttackTurtleEggGoal(this, 1.0D, 3));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new ZombieAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(ZombifiedPiglin.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0D).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513D).add(Attributes.ATTACK_DAMAGE, 3.0D).add(Attributes.ARMOR, 2.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.getEntityData().define(DATA_BABY_ID, false);
      this.getEntityData().define(DATA_SPECIAL_TYPE_ID, 0);
      this.getEntityData().define(DATA_DROWNED_CONVERSION_ID, false);
   }

   public boolean isUnderWaterConverting() {
      return (Boolean)this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
   }

   public boolean canBreakDoors() {
      return this.canBreakDoors;
   }

   public void setCanBreakDoors(boolean var1) {
      if (this.supportsBreakDoorGoal() && GoalUtils.hasGroundPathNavigation(this)) {
         if (this.canBreakDoors != var1) {
            this.canBreakDoors = var1;
            ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(var1);
            if (var1) {
               this.goalSelector.addGoal(1, this.breakDoorGoal);
            } else {
               this.goalSelector.removeGoal(this.breakDoorGoal);
            }
         }
      } else if (this.canBreakDoors) {
         this.goalSelector.removeGoal(this.breakDoorGoal);
         this.canBreakDoors = false;
      }

   }

   protected boolean supportsBreakDoorGoal() {
      return true;
   }

   public boolean isBaby() {
      return (Boolean)this.getEntityData().get(DATA_BABY_ID);
   }

   protected int getExperienceReward(Player var1) {
      if (this.isBaby()) {
         this.xpReward = (int)((float)this.xpReward * 2.5F);
      }

      return super.getExperienceReward(var1);
   }

   public void setBaby(boolean var1) {
      this.getEntityData().set(DATA_BABY_ID, var1);
      if (this.level != null && !this.level.isClientSide) {
         AttributeInstance var2 = this.getAttribute(Attributes.MOVEMENT_SPEED);
         var2.removeModifier(SPEED_MODIFIER_BABY);
         if (var1) {
            var2.addTransientModifier(SPEED_MODIFIER_BABY);
         }
      }

   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (DATA_BABY_ID.equals(var1)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(var1);
   }

   protected boolean convertsInWater() {
      return true;
   }

   public void tick() {
      if (!this.level.isClientSide && this.isAlive() && !this.isNoAi()) {
         if (this.isUnderWaterConverting()) {
            --this.conversionTime;
            if (this.conversionTime < 0) {
               this.doUnderWaterConversion();
            }
         } else if (this.convertsInWater()) {
            if (this.isEyeInFluid(FluidTags.WATER)) {
               ++this.inWaterTime;
               if (this.inWaterTime >= 600) {
                  this.startUnderWaterConversion(300);
               }
            } else {
               this.inWaterTime = -1;
            }
         }
      }

      super.tick();
   }

   public void aiStep() {
      if (this.isAlive()) {
         boolean var1 = this.isSunSensitive() && this.isSunBurnTick();
         if (var1) {
            ItemStack var2 = this.getItemBySlot(EquipmentSlot.HEAD);
            if (!var2.isEmpty()) {
               if (var2.isDamageableItem()) {
                  var2.setDamageValue(var2.getDamageValue() + this.random.nextInt(2));
                  if (var2.getDamageValue() >= var2.getMaxDamage()) {
                     this.broadcastBreakEvent(EquipmentSlot.HEAD);
                     this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                  }
               }

               var1 = false;
            }

            if (var1) {
               this.setSecondsOnFire(8);
            }
         }
      }

      super.aiStep();
   }

   private void startUnderWaterConversion(int var1) {
      this.conversionTime = var1;
      this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
   }

   protected void doUnderWaterConversion() {
      this.convertToZombieType(EntityType.DROWNED);
      if (!this.isSilent()) {
         this.level.levelEvent((Player)null, 1040, this.blockPosition(), 0);
      }

   }

   protected void convertToZombieType(EntityType<? extends Zombie> var1) {
      Zombie var2 = (Zombie)this.convertTo(var1, true);
      if (var2 != null) {
         var2.handleAttributes(var2.level.getCurrentDifficultyAt(var2.blockPosition()).getSpecialMultiplier());
         var2.setCanBreakDoors(var2.supportsBreakDoorGoal() && this.canBreakDoors());
      }

   }

   protected boolean isSunSensitive() {
      return true;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (!super.hurt(var1, var2)) {
         return false;
      } else if (!(this.level instanceof ServerLevel)) {
         return false;
      } else {
         ServerLevel var3 = (ServerLevel)this.level;
         LivingEntity var4 = this.getTarget();
         if (var4 == null && var1.getEntity() instanceof LivingEntity) {
            var4 = (LivingEntity)var1.getEntity();
         }

         if (var4 != null && this.level.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE) && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            int var5 = Mth.floor(this.getX());
            int var6 = Mth.floor(this.getY());
            int var7 = Mth.floor(this.getZ());
            Zombie var8 = new Zombie(this.level);

            for(int var9 = 0; var9 < 50; ++var9) {
               int var10 = var5 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int var11 = var6 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int var12 = var7 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               BlockPos var13 = new BlockPos(var10, var11, var12);
               EntityType var14 = var8.getType();
               SpawnPlacements.Type var15 = SpawnPlacements.getPlacementType(var14);
               if (NaturalSpawner.isSpawnPositionOk(var15, this.level, var13, var14) && SpawnPlacements.checkSpawnRules(var14, var3, MobSpawnType.REINFORCEMENT, var13, this.level.random)) {
                  var8.setPos((double)var10, (double)var11, (double)var12);
                  if (!this.level.hasNearbyAlivePlayer((double)var10, (double)var11, (double)var12, 7.0D) && this.level.isUnobstructed(var8) && this.level.noCollision(var8) && !this.level.containsAnyLiquid(var8.getBoundingBox())) {
                     var8.setTarget(var4);
                     var8.finalizeSpawn(var3, this.level.getCurrentDifficultyAt(var8.blockPosition()), MobSpawnType.REINFORCEMENT, (SpawnGroupData)null, (CompoundTag)null);
                     var3.addFreshEntityWithPassengers(var8);
                     this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                     var8.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                     break;
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean doHurtTarget(Entity var1) {
      boolean var2 = super.doHurtTarget(var1);
      if (var2) {
         float var3 = this.level.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < var3 * 0.3F) {
            var1.setSecondsOnFire(2 * (int)var3);
         }
      }

      return var2;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ZOMBIE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ZOMBIE_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ZOMBIE_STEP;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(this.getStepSound(), 0.15F, 1.0F);
   }

   public MobType getMobType() {
      return MobType.UNDEAD;
   }

   protected void populateDefaultEquipmentSlots(DifficultyInstance var1) {
      super.populateDefaultEquipmentSlots(var1);
      if (this.random.nextFloat() < (this.level.getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
         int var2 = this.random.nextInt(3);
         if (var2 == 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("IsBaby", this.isBaby());
      var1.putBoolean("CanBreakDoors", this.canBreakDoors());
      var1.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      var1.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setBaby(var1.getBoolean("IsBaby"));
      this.setCanBreakDoors(var1.getBoolean("CanBreakDoors"));
      this.inWaterTime = var1.getInt("InWaterTime");
      if (var1.contains("DrownedConversionTime", 99) && var1.getInt("DrownedConversionTime") > -1) {
         this.startUnderWaterConversion(var1.getInt("DrownedConversionTime"));
      }

   }

   public void killed(ServerLevel var1, LivingEntity var2) {
      super.killed(var1, var2);
      if ((var1.getDifficulty() == Difficulty.NORMAL || var1.getDifficulty() == Difficulty.HARD) && var2 instanceof Villager) {
         if (var1.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
            return;
         }

         Villager var3 = (Villager)var2;
         ZombieVillager var4 = (ZombieVillager)var3.convertTo(EntityType.ZOMBIE_VILLAGER, false);
         var4.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var4.blockPosition()), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false, true), (CompoundTag)null);
         var4.setVillagerData(var3.getVillagerData());
         var4.setGossips((Tag)var3.getGossips().store(NbtOps.INSTANCE).getValue());
         var4.setTradeOffers(var3.getOffers().createTag());
         var4.setVillagerXp(var3.getVillagerXp());
         if (!this.isSilent()) {
            var1.levelEvent((Player)null, 1026, this.blockPosition(), 0);
         }
      }

   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return this.isBaby() ? 0.93F : 1.74F;
   }

   public boolean canHoldItem(ItemStack var1) {
      return var1.is(Items.EGG) && this.isBaby() && this.isPassenger() ? false : super.canHoldItem(var1);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      Object var10 = super.finalizeSpawn(var1, var2, var3, var4, var5);
      float var6 = var2.getSpecialMultiplier();
      this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * var6);
      if (var10 == null) {
         var10 = new Zombie.ZombieGroupData(getSpawnAsBabyOdds(var1.getRandom()), true);
      }

      if (var10 instanceof Zombie.ZombieGroupData) {
         Zombie.ZombieGroupData var7 = (Zombie.ZombieGroupData)var10;
         if (var7.isBaby) {
            this.setBaby(true);
            if (var7.canSpawnJockey) {
               if ((double)var1.getRandom().nextFloat() < 0.05D) {
                  List var8 = var1.getEntitiesOfClass(Chicken.class, this.getBoundingBox().inflate(5.0D, 3.0D, 5.0D), EntitySelector.ENTITY_NOT_BEING_RIDDEN);
                  if (!var8.isEmpty()) {
                     Chicken var9 = (Chicken)var8.get(0);
                     var9.setChickenJockey(true);
                     this.startRiding(var9);
                  }
               } else if ((double)var1.getRandom().nextFloat() < 0.05D) {
                  Chicken var12 = (Chicken)EntityType.CHICKEN.create(this.level);
                  var12.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, 0.0F);
                  var12.finalizeSpawn(var1, var2, MobSpawnType.JOCKEY, (SpawnGroupData)null, (CompoundTag)null);
                  var12.setChickenJockey(true);
                  this.startRiding(var12);
                  var1.addFreshEntity(var12);
               }
            }
         }

         this.setCanBreakDoors(this.supportsBreakDoorGoal() && this.random.nextFloat() < var6 * 0.1F);
         this.populateDefaultEquipmentSlots(var2);
         this.populateDefaultEquipmentEnchantments(var2);
      }

      if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
         LocalDate var11 = LocalDate.now();
         int var13 = var11.get(ChronoField.DAY_OF_MONTH);
         int var14 = var11.get(ChronoField.MONTH_OF_YEAR);
         if (var14 == 10 && var13 == 31 && this.random.nextFloat() < 0.25F) {
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(this.random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
            this.armorDropChances[EquipmentSlot.HEAD.getIndex()] = 0.0F;
         }
      }

      this.handleAttributes(var6);
      return (SpawnGroupData)var10;
   }

   public static boolean getSpawnAsBabyOdds(Random var0) {
      return var0.nextFloat() < 0.05F;
   }

   protected void handleAttributes(float var1) {
      this.randomizeReinforcementsChance();
      this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addPermanentModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806D, AttributeModifier.Operation.ADDITION));
      double var2 = this.random.nextDouble() * 1.5D * (double)var1;
      if (var2 > 1.0D) {
         this.getAttribute(Attributes.FOLLOW_RANGE).addPermanentModifier(new AttributeModifier("Random zombie-spawn bonus", var2, AttributeModifier.Operation.MULTIPLY_TOTAL));
      }

      if (this.random.nextFloat() < var1 * 0.05F) {
         this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
         this.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
         this.setCanBreakDoors(this.supportsBreakDoorGoal());
      }

   }

   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * 0.10000000149011612D);
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? 0.0D : -0.45D;
   }

   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      Entity var4 = var1.getEntity();
      if (var4 instanceof Creeper) {
         Creeper var5 = (Creeper)var4;
         if (var5.canDropMobsSkull()) {
            ItemStack var6 = this.getSkull();
            if (!var6.isEmpty()) {
               var5.increaseDroppedSkulls();
               this.spawnAtLocation(var6);
            }
         }
      }

   }

   protected ItemStack getSkull() {
      return new ItemStack(Items.ZOMBIE_HEAD);
   }

   static {
      SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_UUID, "Baby speed boost", 0.5D, AttributeModifier.Operation.MULTIPLY_BASE);
      DATA_BABY_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
      DATA_SPECIAL_TYPE_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.INT);
      DATA_DROWNED_CONVERSION_ID = SynchedEntityData.defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
      DOOR_BREAKING_PREDICATE = (var0) -> {
         return var0 == Difficulty.HARD;
      };
   }

   class ZombieAttackTurtleEggGoal extends RemoveBlockGoal {
      ZombieAttackTurtleEggGoal(PathfinderMob var2, double var3, int var5) {
         super(Blocks.TURTLE_EGG, var2, var3, var5);
      }

      public void playDestroyProgressSound(LevelAccessor var1, BlockPos var2) {
         var1.playSound((Player)null, var2, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5F, 0.9F + Zombie.this.random.nextFloat() * 0.2F);
      }

      public void playBreakSound(Level var1, BlockPos var2) {
         var1.playSound((Player)null, (BlockPos)var2, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + var1.random.nextFloat() * 0.2F);
      }

      public double acceptedDistance() {
         return 1.14D;
      }
   }

   public static class ZombieGroupData implements SpawnGroupData {
      public final boolean isBaby;
      public final boolean canSpawnJockey;

      public ZombieGroupData(boolean var1, boolean var2) {
         super();
         this.isBaby = var1;
         this.canSpawnJockey = var2;
      }
   }
}
