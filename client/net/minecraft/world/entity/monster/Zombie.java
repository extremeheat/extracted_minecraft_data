package net.minecraft.world.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
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
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class Zombie extends Monster {
   protected static final Attribute SPAWN_REINFORCEMENTS_CHANCE = (new RangedAttribute((Attribute)null, "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D)).importLegacyName("Spawn Reinforcements Chance");
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
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(PigZombie.class));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, AbstractVillager.class, false));
      this.targetSelector.addGoal(3, new NearestAttackableTargetGoal(this, IronGolem.class, true));
      this.targetSelector.addGoal(5, new NearestAttackableTargetGoal(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23000000417232513D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
      this.getAttributes().registerAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(this.random.nextDouble() * 0.10000000149011612D);
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
      if (this.supportsBreakDoorGoal()) {
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
         AttributeInstance var2 = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
         var2.removeModifier(SPEED_MODIFIER_BABY);
         if (var1) {
            var2.addModifier(SPEED_MODIFIER_BABY);
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
      if (!this.level.isClientSide && this.isAlive()) {
         if (this.isUnderWaterConverting()) {
            --this.conversionTime;
            if (this.conversionTime < 0) {
               this.doUnderWaterConversion();
            }
         } else if (this.convertsInWater()) {
            if (this.isUnderLiquid(FluidTags.WATER)) {
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
      this.convertTo(EntityType.DROWNED);
      this.level.levelEvent((Player)null, 1040, new BlockPos(this), 0);
   }

   protected void convertTo(EntityType<? extends Zombie> var1) {
      if (!this.removed) {
         Zombie var2 = (Zombie)var1.create(this.level);
         var2.copyPosition(this);
         var2.setCanPickUpLoot(this.canPickUpLoot());
         var2.setCanBreakDoors(var2.supportsBreakDoorGoal() && this.canBreakDoors());
         var2.handleAttributes(var2.level.getCurrentDifficultyAt(new BlockPos(var2)).getSpecialMultiplier());
         var2.setBaby(this.isBaby());
         var2.setNoAi(this.isNoAi());
         EquipmentSlot[] var3 = EquipmentSlot.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EquipmentSlot var6 = var3[var5];
            ItemStack var7 = this.getItemBySlot(var6);
            if (!var7.isEmpty()) {
               var2.setItemSlot(var6, var7.copy());
               var2.setDropChance(var6, this.getEquipmentDropChance(var6));
               var7.setCount(0);
            }
         }

         if (this.hasCustomName()) {
            var2.setCustomName(this.getCustomName());
            var2.setCustomNameVisible(this.isCustomNameVisible());
         }

         this.level.addFreshEntity(var2);
         this.remove();
      }
   }

   protected boolean isSunSensitive() {
      return true;
   }

   public boolean hurt(DamageSource var1, float var2) {
      if (super.hurt(var1, var2)) {
         LivingEntity var3 = this.getTarget();
         if (var3 == null && var1.getEntity() instanceof LivingEntity) {
            var3 = (LivingEntity)var1.getEntity();
         }

         if (var3 != null && this.level.getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).getValue() && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            int var4 = Mth.floor(this.x);
            int var5 = Mth.floor(this.y);
            int var6 = Mth.floor(this.z);
            Zombie var7 = new Zombie(this.level);

            for(int var8 = 0; var8 < 50; ++var8) {
               int var9 = var4 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int var10 = var5 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               int var11 = var6 + Mth.nextInt(this.random, 7, 40) * Mth.nextInt(this.random, -1, 1);
               BlockPos var12 = new BlockPos(var9, var10 - 1, var11);
               if (this.level.getBlockState(var12).entityCanStandOn(this.level, var12, var7) && this.level.getMaxLocalRawBrightness(new BlockPos(var9, var10, var11)) < 10) {
                  var7.setPos((double)var9, (double)var10, (double)var11);
                  if (!this.level.hasNearbyAlivePlayer((double)var9, (double)var10, (double)var11, 7.0D) && this.level.isUnobstructed(var7) && this.level.noCollision(var7) && !this.level.containsAnyLiquid(var7.getBoundingBox())) {
                     this.level.addFreshEntity(var7);
                     var7.setTarget(var3);
                     var7.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(var7)), MobSpawnType.REINFORCEMENT, (SpawnGroupData)null, (CompoundTag)null);
                     this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                     var7.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806D, AttributeModifier.Operation.ADDITION));
                     break;
                  }
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean doHurtTarget(Entity var1) {
      boolean var2 = super.doHurtTarget(var1);
      if (var2) {
         float var3 = this.level.getCurrentDifficultyAt(new BlockPos(this)).getEffectiveDifficulty();
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
      if (this.isBaby()) {
         var1.putBoolean("IsBaby", true);
      }

      var1.putBoolean("CanBreakDoors", this.canBreakDoors());
      var1.putInt("InWaterTime", this.isInWater() ? this.inWaterTime : -1);
      var1.putInt("DrownedConversionTime", this.isUnderWaterConverting() ? this.conversionTime : -1);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.getBoolean("IsBaby")) {
         this.setBaby(true);
      }

      this.setCanBreakDoors(var1.getBoolean("CanBreakDoors"));
      this.inWaterTime = var1.getInt("InWaterTime");
      if (var1.contains("DrownedConversionTime", 99) && var1.getInt("DrownedConversionTime") > -1) {
         this.startUnderWaterConversion(var1.getInt("DrownedConversionTime"));
      }

   }

   public void killed(LivingEntity var1) {
      super.killed(var1);
      if ((this.level.getDifficulty() == Difficulty.NORMAL || this.level.getDifficulty() == Difficulty.HARD) && var1 instanceof Villager) {
         if (this.level.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
            return;
         }

         Villager var2 = (Villager)var1;
         ZombieVillager var3 = (ZombieVillager)EntityType.ZOMBIE_VILLAGER.create(this.level);
         var3.copyPosition(var2);
         var2.remove();
         var3.finalizeSpawn(this.level, this.level.getCurrentDifficultyAt(new BlockPos(var3)), MobSpawnType.CONVERSION, new Zombie.ZombieGroupData(false), (CompoundTag)null);
         var3.setVillagerData(var2.getVillagerData());
         var3.setGossips((Tag)var2.getGossips().store(NbtOps.INSTANCE).getValue());
         var3.setTradeOffers(var2.getOffers().createTag());
         var3.setVillagerXp(var2.getVillagerXp());
         var3.setBaby(var2.isBaby());
         var3.setNoAi(var2.isNoAi());
         if (var2.hasCustomName()) {
            var3.setCustomName(var2.getCustomName());
            var3.setCustomNameVisible(var2.isCustomNameVisible());
         }

         this.level.addFreshEntity(var3);
         this.level.levelEvent((Player)null, 1026, new BlockPos(this), 0);
      }

   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return this.isBaby() ? 0.93F : 1.74F;
   }

   protected boolean canHoldItem(ItemStack var1) {
      return var1.getItem() == Items.EGG && this.isBaby() && this.isPassenger() ? false : super.canHoldItem(var1);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      Object var10 = super.finalizeSpawn(var1, var2, var3, var4, var5);
      float var6 = var2.getSpecialMultiplier();
      this.setCanPickUpLoot(this.random.nextFloat() < 0.55F * var6);
      if (var10 == null) {
         var10 = new Zombie.ZombieGroupData(var1.getRandom().nextFloat() < 0.05F);
      }

      if (var10 instanceof Zombie.ZombieGroupData) {
         Zombie.ZombieGroupData var7 = (Zombie.ZombieGroupData)var10;
         if (var7.isBaby) {
            this.setBaby(true);
            if ((double)var1.getRandom().nextFloat() < 0.05D) {
               List var8 = var1.getEntitiesOfClass(Chicken.class, this.getBoundingBox().inflate(5.0D, 3.0D, 5.0D), EntitySelector.ENTITY_NOT_BEING_RIDDEN);
               if (!var8.isEmpty()) {
                  Chicken var9 = (Chicken)var8.get(0);
                  var9.setChickenJockey(true);
                  this.startRiding(var9);
               }
            } else if ((double)var1.getRandom().nextFloat() < 0.05D) {
               Chicken var12 = (Chicken)EntityType.CHICKEN.create(this.level);
               var12.moveTo(this.x, this.y, this.z, this.yRot, 0.0F);
               var12.finalizeSpawn(var1, var2, MobSpawnType.JOCKEY, (SpawnGroupData)null, (CompoundTag)null);
               var12.setChickenJockey(true);
               var1.addFreshEntity(var12);
               this.startRiding(var12);
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

   protected void handleAttributes(float var1) {
      this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).addModifier(new AttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806D, AttributeModifier.Operation.ADDITION));
      double var2 = this.random.nextDouble() * 1.5D * (double)var1;
      if (var2 > 1.0D) {
         this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).addModifier(new AttributeModifier("Random zombie-spawn bonus", var2, AttributeModifier.Operation.MULTIPLY_TOTAL));
      }

      if (this.random.nextFloat() < var1 * 0.05F) {
         this.getAttribute(SPAWN_REINFORCEMENTS_CHANCE).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25D + 0.5D, AttributeModifier.Operation.ADDITION));
         this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).addModifier(new AttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0D + 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
         this.setCanBreakDoors(this.supportsBreakDoorGoal());
      }

   }

   public double getRidingHeight() {
      return this.isBaby() ? 0.0D : -0.45D;
   }

   protected void dropCustomDeathLoot(DamageSource var1, int var2, boolean var3) {
      super.dropCustomDeathLoot(var1, var2, var3);
      Entity var4 = var1.getEntity();
      if (var4 instanceof Creeper) {
         Creeper var5 = (Creeper)var4;
         if (var5.canDropMobsSkull()) {
            var5.increaseDroppedSkulls();
            ItemStack var6 = this.getSkull();
            if (!var6.isEmpty()) {
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

   public class ZombieGroupData implements SpawnGroupData {
      public final boolean isBaby;

      private ZombieGroupData(boolean var2) {
         super();
         this.isBaby = var2;
      }

      // $FF: synthetic method
      ZombieGroupData(boolean var2, Object var3) {
         this(var2);
      }
   }
}
