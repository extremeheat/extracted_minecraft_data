package net.minecraft.world.entity.monster.zombie;

import com.google.common.annotations.VisibleForTesting;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.SpecialDates;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ConversionParams;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.entity.ai.goal.SpearUseGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.ZombieAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class Zombie extends Monster {
   private static final Identifier SPEED_MODIFIER_BABY_ID = Identifier.withDefaultNamespace("baby");
   private static final AttributeModifier SPEED_MODIFIER_BABY;
   private static final Identifier REINFORCEMENT_CALLER_CHARGE_ID;
   private static final AttributeModifier ZOMBIE_REINFORCEMENT_CALLEE_CHARGE;
   private static final Identifier LEADER_ZOMBIE_BONUS_ID;
   private static final Identifier ZOMBIE_RANDOM_SPAWN_BONUS_ID;
   private static final EntityDataAccessor<Boolean> DATA_BABY_ID;
   private static final EntityDataAccessor<Integer> DATA_SPECIAL_TYPE_ID;
   private static final EntityDataAccessor<Boolean> DATA_DROWNED_CONVERSION_ID;
   public static final float ZOMBIE_LEADER_CHANCE = 0.05F;
   public static final int REINFORCEMENT_ATTEMPTS = 50;
   public static final int REINFORCEMENT_RANGE_MAX = 40;
   public static final int REINFORCEMENT_RANGE_MIN = 7;
   private static final int NOT_CONVERTING = -1;
   private static final EntityDimensions BABY_DIMENSIONS;
   private static final float BREAK_DOOR_CHANCE = 0.1F;
   private static final Predicate<Difficulty> DOOR_BREAKING_PREDICATE;
   private static final boolean DEFAULT_BABY = false;
   private static final boolean DEFAULT_CAN_BREAK_DOORS = false;
   private static final int DEFAULT_IN_WATER_TIME = 0;
   private final BreakDoorGoal breakDoorGoal;
   private boolean canBreakDoors;
   private int inWaterTime;
   private int conversionTime;

   public Zombie(EntityType<? extends Zombie> var1, Level var2) {
      super(var1, var2);
      this.breakDoorGoal = new BreakDoorGoal(this, DOOR_BREAKING_PREDICATE);
      this.canBreakDoors = false;
      this.inWaterTime = 0;
   }

   public Zombie(Level var1) {
      this(EntityType.ZOMBIE, var1);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(4, new ZombieAttackTurtleEggGoal(this, 1.0, 3));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.addBehaviourGoals();
   }

   protected void addBehaviourGoals() {
      this.goalSelector.addGoal(2, new SpearUseGoal(this, 1.0, 1.0, 10.0F, 2.0F));
      this.goalSelector.addGoal(3, new ZombieAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
      this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(ZombifiedPiglin.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.23000000417232513).add(Attributes.ATTACK_DAMAGE, 3.0).add(Attributes.ARMOR, 2.0).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_BABY_ID, false);
      var1.define(DATA_SPECIAL_TYPE_ID, 0);
      var1.define(DATA_DROWNED_CONVERSION_ID, false);
   }

   public boolean isUnderWaterConverting() {
      return (Boolean)this.getEntityData().get(DATA_DROWNED_CONVERSION_ID);
   }

   public boolean canBreakDoors() {
      return this.canBreakDoors;
   }

   public void setCanBreakDoors(boolean var1) {
      if (this.navigation.canNavigateGround()) {
         if (this.canBreakDoors != var1) {
            this.canBreakDoors = var1;
            this.navigation.setCanOpenDoors(var1);
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

   public boolean isBaby() {
      return (Boolean)this.getEntityData().get(DATA_BABY_ID);
   }

   protected int getBaseExperienceReward(ServerLevel var1) {
      if (this.isBaby()) {
         this.xpReward = (int)((double)this.xpReward * 2.5);
      }

      return super.getBaseExperienceReward(var1);
   }

   public void setBaby(boolean var1) {
      this.getEntityData().set(DATA_BABY_ID, var1);
      if (this.level() != null && !this.level().isClientSide()) {
         AttributeInstance var2 = this.getAttribute(Attributes.MOVEMENT_SPEED);
         var2.removeModifier(SPEED_MODIFIER_BABY_ID);
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
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         if (this.isAlive() && !this.isNoAi()) {
            if (this.isUnderWaterConverting()) {
               --this.conversionTime;
               if (this.conversionTime < 0) {
                  this.doUnderWaterConversion(var1);
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
      }

      super.tick();
   }

   private void startUnderWaterConversion(int var1) {
      this.conversionTime = var1;
      this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, true);
   }

   protected void doUnderWaterConversion(ServerLevel var1) {
      this.convertToZombieType(var1, EntityType.DROWNED);
      if (!this.isSilent()) {
         var1.levelEvent((Entity)null, 1040, this.blockPosition(), 0);
      }

   }

   protected void convertToZombieType(ServerLevel var1, EntityType<? extends Zombie> var2) {
      this.convertTo(var2, ConversionParams.single(this, true, true), (var1x) -> var1x.handleAttributes(var1.getCurrentDifficultyAt(var1x.blockPosition()).getSpecialMultiplier()));
   }

   @VisibleForTesting
   public boolean convertVillagerToZombieVillager(ServerLevel var1, Villager var2) {
      ZombieVillager var3 = (ZombieVillager)var2.convertTo(EntityType.ZOMBIE_VILLAGER, ConversionParams.single(var2, true, true), (var3x) -> {
         var3x.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var3x.blockPosition()), EntitySpawnReason.CONVERSION, new ZombieGroupData(false, true));
         var3x.setVillagerData(var2.getVillagerData());
         var3x.setGossips(var2.getGossips().copy());
         var3x.setTradeOffers(var2.getOffers().copy());
         var3x.setVillagerXp(var2.getVillagerXp());
         if (!this.isSilent()) {
            var1.levelEvent((Entity)null, 1026, this.blockPosition(), 0);
         }

      });
      return var3 != null;
   }

   protected boolean isSunSensitive() {
      return true;
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (!super.hurtServer(var1, var2, var3)) {
         return false;
      } else {
         LivingEntity var4 = this.getTarget();
         if (var4 == null && var2.getEntity() instanceof LivingEntity) {
            var4 = (LivingEntity)var2.getEntity();
         }

         if (var4 != null && var1.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttributeValue(Attributes.SPAWN_REINFORCEMENTS_CHANCE) && var1.isSpawningMonsters()) {
            int var5 = Mth.floor(this.getX());
            int var6 = Mth.floor(this.getY());
            int var7 = Mth.floor(this.getZ());
            EntityType var8 = this.getType();
            Zombie var9 = (Zombie)var8.create(var1, EntitySpawnReason.REINFORCEMENT);
            if (var9 == null) {
               return true;
            }

            for(int var10 = 0; var10 < 50; ++var10) {
               int var11 = var5 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int var12 = var6 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int var13 = var7 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               BlockPos var14 = new BlockPos(var11, var12, var13);
               if (SpawnPlacements.isSpawnPositionOk(var8, var1, var14) && SpawnPlacements.checkSpawnRules(var8, var1, EntitySpawnReason.REINFORCEMENT, var14, var1.random)) {
                  var9.setPos((double)var11, (double)var12, (double)var13);
                  if (!var1.hasNearbyAlivePlayer((double)var11, (double)var12, (double)var13, 7.0) && var1.isUnobstructed(var9) && var1.noCollision(var9) && (var9.canSpawnInLiquids() || !var1.containsAnyLiquid(var9.getBoundingBox()))) {
                     var9.setTarget(var4);
                     var9.finalizeSpawn(var1, var1.getCurrentDifficultyAt(var9.blockPosition()), EntitySpawnReason.REINFORCEMENT, (SpawnGroupData)null);
                     var1.addFreshEntityWithPassengers(var9);
                     AttributeInstance var15 = this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
                     AttributeModifier var16 = var15.getModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                     double var17 = var16 != null ? var16.amount() : 0.0;
                     var15.removeModifier(REINFORCEMENT_CALLER_CHARGE_ID);
                     var15.addPermanentModifier(new AttributeModifier(REINFORCEMENT_CALLER_CHARGE_ID, var17 - 0.05, AttributeModifier.Operation.ADD_VALUE));
                     var9.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addPermanentModifier(ZOMBIE_REINFORCEMENT_CALLEE_CHARGE);
                     break;
                  }
               }
            }
         }

         return true;
      }
   }

   public boolean doHurtTarget(ServerLevel var1, Entity var2) {
      boolean var3 = super.doHurtTarget(var1, var2);
      if (var3) {
         float var4 = var1.getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
         if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < var4 * 0.3F) {
            var2.igniteForSeconds((float)(2 * (int)var4));
         }
      }

      return var3;
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

   public EntityType<? extends Zombie> getType() {
      return super.getType();
   }

   protected boolean canSpawnInLiquids() {
      return false;
   }

   protected void populateDefaultEquipmentSlots(RandomSource var1, DifficultyInstance var2) {
      super.populateDefaultEquipmentSlots(var1, var2);
      if (var1.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
         int var3 = var1.nextInt(6);
         if (var3 == 0) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
         } else if (var3 == 1) {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SPEAR));
         } else {
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
         }
      }

   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("IsBaby", this.isBaby());
      var1.putBoolean("CanBreakDoors", this.canBreakDoors());
      var1.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      var1.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setBaby(var1.getBooleanOr("IsBaby", false));
      this.setCanBreakDoors(var1.getBooleanOr("CanBreakDoors", false));
      this.inWaterTime = var1.getIntOr("InWaterTime", 0);
      int var2 = var1.getIntOr("DrownedConversionTime", -1);
      if (var2 != -1) {
         this.startUnderWaterConversion(var2);
      } else {
         this.getEntityData().set(DATA_DROWNED_CONVERSION_ID, false);
      }

   }

   public boolean killedEntity(ServerLevel var1, LivingEntity var2, DamageSource var3) {
      boolean var4 = super.killedEntity(var1, var2, var3);
      if ((var1.getDifficulty() == Difficulty.NORMAL || var1.getDifficulty() == Difficulty.HARD) && var2 instanceof Villager var5) {
         if (var1.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
            return var4;
         }

         if (this.convertVillagerToZombieVillager(var1, var5)) {
            var4 = false;
         }
      }

      return var4;
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return this.isBaby() ? BABY_DIMENSIONS : super.getDefaultDimensions(var1);
   }

   public boolean canHoldItem(ItemStack var1) {
      return var1.is(ItemTags.EGGS) && this.isBaby() && this.isPassenger() ? false : super.canHoldItem(var1);
   }

   public boolean wantsToPickUp(ServerLevel var1, ItemStack var2) {
      return var2.is(Items.GLOW_INK_SAC) ? false : super.wantsToPickUp(var1, var2);
   }

   public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, EntitySpawnReason var3, @Nullable SpawnGroupData var4) {
      RandomSource var5 = var1.getRandom();
      var4 = super.finalizeSpawn(var1, var2, var3, var4);
      float var6 = var2.getSpecialMultiplier();
      if (var3 != EntitySpawnReason.CONVERSION) {
         this.setCanPickUpLoot(var5.nextFloat() < 0.55F * var6);
      }

      if (var4 == null) {
         var4 = new ZombieGroupData(getSpawnAsBabyOdds(var5), true);
      }

      if (var4 instanceof ZombieGroupData var7) {
         if (var7.isBaby) {
            this.setBaby(true);
            if (var7.canSpawnJockey) {
               if ((double)var5.nextFloat() < 0.05) {
                  List var8 = var1.getEntitiesOfClass(Chicken.class, this.getBoundingBox().inflate(5.0, 3.0, 5.0), EntitySelector.ENTITY_NOT_BEING_RIDDEN);
                  if (!var8.isEmpty()) {
                     Chicken var9 = (Chicken)var8.get(0);
                     var9.setChickenJockey(true);
                     this.startRiding(var9, false, false);
                  }
               } else if ((double)var5.nextFloat() < 0.05) {
                  Chicken var11 = EntityType.CHICKEN.create(this.level(), EntitySpawnReason.JOCKEY);
                  if (var11 != null) {
                     var11.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                     var11.finalizeSpawn(var1, var2, EntitySpawnReason.JOCKEY, (SpawnGroupData)null);
                     var11.setChickenJockey(true);
                     this.startRiding(var11, false, false);
                     var1.addFreshEntity(var11);
                  }
               }
            }
         }

         this.setCanBreakDoors(var5.nextFloat() < var6 * 0.1F);
         if (var3 != EntitySpawnReason.CONVERSION) {
            this.populateDefaultEquipmentSlots(var5, var2);
            this.populateDefaultEquipmentEnchantments(var1, var5, var2);
         }
      }

      if (this.getItemBySlot(EquipmentSlot.HEAD).isEmpty() && SpecialDates.isHalloween() && var5.nextFloat() < 0.25F) {
         this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(var5.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
         this.setDropChance(EquipmentSlot.HEAD, 0.0F);
      }

      this.handleAttributes(var6);
      return var4;
   }

   @VisibleForTesting
   public void setInWaterTime(int var1) {
      this.inWaterTime = var1;
   }

   @VisibleForTesting
   public void setConversionTime(int var1) {
      this.conversionTime = var1;
   }

   public static boolean getSpawnAsBabyOdds(RandomSource var0) {
      return var0.nextFloat() < 0.05F;
   }

   protected void handleAttributes(float var1) {
      this.randomizeReinforcementsChance();
      this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).addOrReplacePermanentModifier(new AttributeModifier(RANDOM_SPAWN_BONUS_ID, this.random.nextDouble() * 0.05000000074505806, AttributeModifier.Operation.ADD_VALUE));
      double var2 = this.random.nextDouble() * 1.5 * (double)var1;
      if (var2 > 1.0) {
         this.getAttribute(Attributes.FOLLOW_RANGE).addOrReplacePermanentModifier(new AttributeModifier(ZOMBIE_RANDOM_SPAWN_BONUS_ID, var2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      }

      if (this.random.nextFloat() < var1 * 0.05F) {
         this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).addOrReplacePermanentModifier(new AttributeModifier(LEADER_ZOMBIE_BONUS_ID, this.random.nextDouble() * 0.25 + 0.5, AttributeModifier.Operation.ADD_VALUE));
         this.getAttribute(Attributes.MAX_HEALTH).addOrReplacePermanentModifier(new AttributeModifier(LEADER_ZOMBIE_BONUS_ID, this.random.nextDouble() * 3.0 + 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
         this.setCanBreakDoors(true);
      }

   }

   protected void randomizeReinforcementsChance() {
      this.getAttribute(Attributes.SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * 0.10000000149011612);
   }

   static {
      SPEED_MODIFIER_BABY = new AttributeModifier(SPEED_MODIFIER_BABY_ID, 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
      REINFORCEMENT_CALLER_CHARGE_ID = Identifier.withDefaultNamespace("reinforcement_caller_charge");
      ZOMBIE_REINFORCEMENT_CALLEE_CHARGE = new AttributeModifier(Identifier.withDefaultNamespace("reinforcement_callee_charge"), -0.05000000074505806, AttributeModifier.Operation.ADD_VALUE);
      LEADER_ZOMBIE_BONUS_ID = Identifier.withDefaultNamespace("leader_zombie_bonus");
      ZOMBIE_RANDOM_SPAWN_BONUS_ID = Identifier.withDefaultNamespace("zombie_random_spawn_bonus");
      DATA_BABY_ID = SynchedEntityData.<Boolean>defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
      DATA_SPECIAL_TYPE_ID = SynchedEntityData.<Integer>defineId(Zombie.class, EntityDataSerializers.INT);
      DATA_DROWNED_CONVERSION_ID = SynchedEntityData.<Boolean>defineId(Zombie.class, EntityDataSerializers.BOOLEAN);
      BABY_DIMENSIONS = EntityType.ZOMBIE.getDimensions().scale(0.5F).withEyeHeight(0.93F);
      DOOR_BREAKING_PREDICATE = (var0) -> var0 == Difficulty.HARD;
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

   class ZombieAttackTurtleEggGoal extends RemoveBlockGoal {
      ZombieAttackTurtleEggGoal(final PathfinderMob var2, final double var3, final int var5) {
         super(Blocks.TURTLE_EGG, var2, var3, var5);
      }

      public void playDestroyProgressSound(LevelAccessor var1, BlockPos var2) {
         var1.playSound((Entity)null, var2, SoundEvents.ZOMBIE_DESTROY_EGG, SoundSource.HOSTILE, 0.5F, 0.9F + Zombie.this.random.nextFloat() * 0.2F);
      }

      public void playBreakSound(Level var1, BlockPos var2) {
         var1.playSound((Entity)null, (BlockPos)var2, SoundEvents.TURTLE_EGG_BREAK, SoundSource.BLOCKS, 0.7F, 0.9F + var1.random.nextFloat() * 0.2F);
      }

      public double acceptedDistance() {
         return 1.14;
      }
   }
}
