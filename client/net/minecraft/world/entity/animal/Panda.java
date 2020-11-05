package net.minecraft.world.entity.animal;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class Panda extends Animal {
   private static final EntityDataAccessor<Integer> UNHAPPY_COUNTER;
   private static final EntityDataAccessor<Integer> SNEEZE_COUNTER;
   private static final EntityDataAccessor<Integer> EAT_COUNTER;
   private static final EntityDataAccessor<Byte> MAIN_GENE_ID;
   private static final EntityDataAccessor<Byte> HIDDEN_GENE_ID;
   private static final EntityDataAccessor<Byte> DATA_ID_FLAGS;
   private static final TargetingConditions BREED_TARGETING;
   private boolean gotBamboo;
   private boolean didBite;
   public int rollCounter;
   private Vec3 rollDelta;
   private float sitAmount;
   private float sitAmountO;
   private float onBackAmount;
   private float onBackAmountO;
   private float rollAmount;
   private float rollAmountO;
   private Panda.PandaLookAtPlayerGoal lookAtPlayerGoal;
   private static final Predicate<ItemEntity> PANDA_ITEMS;

   public Panda(EntityType<? extends Panda> var1, Level var2) {
      super(var1, var2);
      this.moveControl = new Panda.PandaMoveControl(this);
      if (!this.isBaby()) {
         this.setCanPickUpLoot(true);
      }

   }

   public boolean canTakeItem(ItemStack var1) {
      EquipmentSlot var2 = Mob.getEquipmentSlotForItem(var1);
      if (!this.getItemBySlot(var2).isEmpty()) {
         return false;
      } else {
         return var2 == EquipmentSlot.MAINHAND && super.canTakeItem(var1);
      }
   }

   public int getUnhappyCounter() {
      return (Integer)this.entityData.get(UNHAPPY_COUNTER);
   }

   public void setUnhappyCounter(int var1) {
      this.entityData.set(UNHAPPY_COUNTER, var1);
   }

   public boolean isSneezing() {
      return this.getFlag(2);
   }

   public boolean isSitting() {
      return this.getFlag(8);
   }

   public void sit(boolean var1) {
      this.setFlag(8, var1);
   }

   public boolean isOnBack() {
      return this.getFlag(16);
   }

   public void setOnBack(boolean var1) {
      this.setFlag(16, var1);
   }

   public boolean isEating() {
      return (Integer)this.entityData.get(EAT_COUNTER) > 0;
   }

   public void eat(boolean var1) {
      this.entityData.set(EAT_COUNTER, var1 ? 1 : 0);
   }

   private int getEatCounter() {
      return (Integer)this.entityData.get(EAT_COUNTER);
   }

   private void setEatCounter(int var1) {
      this.entityData.set(EAT_COUNTER, var1);
   }

   public void sneeze(boolean var1) {
      this.setFlag(2, var1);
      if (!var1) {
         this.setSneezeCounter(0);
      }

   }

   public int getSneezeCounter() {
      return (Integer)this.entityData.get(SNEEZE_COUNTER);
   }

   public void setSneezeCounter(int var1) {
      this.entityData.set(SNEEZE_COUNTER, var1);
   }

   public Panda.Gene getMainGene() {
      return Panda.Gene.byId((Byte)this.entityData.get(MAIN_GENE_ID));
   }

   public void setMainGene(Panda.Gene var1) {
      if (var1.getId() > 6) {
         var1 = Panda.Gene.getRandom(this.random);
      }

      this.entityData.set(MAIN_GENE_ID, (byte)var1.getId());
   }

   public Panda.Gene getHiddenGene() {
      return Panda.Gene.byId((Byte)this.entityData.get(HIDDEN_GENE_ID));
   }

   public void setHiddenGene(Panda.Gene var1) {
      if (var1.getId() > 6) {
         var1 = Panda.Gene.getRandom(this.random);
      }

      this.entityData.set(HIDDEN_GENE_ID, (byte)var1.getId());
   }

   public boolean isRolling() {
      return this.getFlag(4);
   }

   public void roll(boolean var1) {
      this.setFlag(4, var1);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(UNHAPPY_COUNTER, 0);
      this.entityData.define(SNEEZE_COUNTER, 0);
      this.entityData.define(MAIN_GENE_ID, (byte)0);
      this.entityData.define(HIDDEN_GENE_ID, (byte)0);
      this.entityData.define(DATA_ID_FLAGS, (byte)0);
      this.entityData.define(EAT_COUNTER, 0);
   }

   private boolean getFlag(int var1) {
      return ((Byte)this.entityData.get(DATA_ID_FLAGS) & var1) != 0;
   }

   private void setFlag(int var1, boolean var2) {
      byte var3 = (Byte)this.entityData.get(DATA_ID_FLAGS);
      if (var2) {
         this.entityData.set(DATA_ID_FLAGS, (byte)(var3 | var1));
      } else {
         this.entityData.set(DATA_ID_FLAGS, (byte)(var3 & ~var1));
      }

   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putString("MainGene", this.getMainGene().getName());
      var1.putString("HiddenGene", this.getHiddenGene().getName());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setMainGene(Panda.Gene.byName(var1.getString("MainGene")));
      this.setHiddenGene(Panda.Gene.byName(var1.getString("HiddenGene")));
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Panda var3 = (Panda)EntityType.PANDA.create(var1);
      if (var2 instanceof Panda) {
         var3.setGeneFromParents(this, (Panda)var2);
      }

      var3.setAttributes();
      return var3;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(2, new Panda.PandaPanicGoal(this, 2.0D));
      this.goalSelector.addGoal(2, new Panda.PandaBreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new Panda.PandaAttackGoal(this, 1.2000000476837158D, true));
      this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(Blocks.BAMBOO.asItem()), false));
      this.goalSelector.addGoal(6, new Panda.PandaAvoidGoal(this, Player.class, 8.0F, 2.0D, 2.0D));
      this.goalSelector.addGoal(6, new Panda.PandaAvoidGoal(this, Monster.class, 4.0F, 2.0D, 2.0D));
      this.goalSelector.addGoal(7, new Panda.PandaSitGoal());
      this.goalSelector.addGoal(8, new Panda.PandaLieOnBackGoal(this));
      this.goalSelector.addGoal(8, new Panda.PandaSneezeGoal(this));
      this.lookAtPlayerGoal = new Panda.PandaLookAtPlayerGoal(this, Player.class, 6.0F);
      this.goalSelector.addGoal(9, this.lookAtPlayerGoal);
      this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));
      this.goalSelector.addGoal(12, new Panda.PandaRollGoal(this));
      this.goalSelector.addGoal(13, new FollowParentGoal(this, 1.25D));
      this.goalSelector.addGoal(14, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.targetSelector.addGoal(1, (new Panda.PandaHurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MOVEMENT_SPEED, 0.15000000596046448D).add(Attributes.ATTACK_DAMAGE, 6.0D);
   }

   public Panda.Gene getVariant() {
      return Panda.Gene.getVariantFromGenes(this.getMainGene(), this.getHiddenGene());
   }

   public boolean isLazy() {
      return this.getVariant() == Panda.Gene.LAZY;
   }

   public boolean isWorried() {
      return this.getVariant() == Panda.Gene.WORRIED;
   }

   public boolean isPlayful() {
      return this.getVariant() == Panda.Gene.PLAYFUL;
   }

   public boolean isWeak() {
      return this.getVariant() == Panda.Gene.WEAK;
   }

   public boolean isAggressive() {
      return this.getVariant() == Panda.Gene.AGGRESSIVE;
   }

   public boolean canBeLeashed(Player var1) {
      return false;
   }

   public boolean doHurtTarget(Entity var1) {
      this.playSound(SoundEvents.PANDA_BITE, 1.0F, 1.0F);
      if (!this.isAggressive()) {
         this.didBite = true;
      }

      return super.doHurtTarget(var1);
   }

   public void tick() {
      super.tick();
      if (this.isWorried()) {
         if (this.level.isThundering() && !this.isInWater()) {
            this.sit(true);
            this.eat(false);
         } else if (!this.isEating()) {
            this.sit(false);
         }
      }

      if (this.getTarget() == null) {
         this.gotBamboo = false;
         this.didBite = false;
      }

      if (this.getUnhappyCounter() > 0) {
         if (this.getTarget() != null) {
            this.lookAt(this.getTarget(), 90.0F, 90.0F);
         }

         if (this.getUnhappyCounter() == 29 || this.getUnhappyCounter() == 14) {
            this.playSound(SoundEvents.PANDA_CANT_BREED, 1.0F, 1.0F);
         }

         this.setUnhappyCounter(this.getUnhappyCounter() - 1);
      }

      if (this.isSneezing()) {
         this.setSneezeCounter(this.getSneezeCounter() + 1);
         if (this.getSneezeCounter() > 20) {
            this.sneeze(false);
            this.afterSneeze();
         } else if (this.getSneezeCounter() == 1) {
            this.playSound(SoundEvents.PANDA_PRE_SNEEZE, 1.0F, 1.0F);
         }
      }

      if (this.isRolling()) {
         this.handleRoll();
      } else {
         this.rollCounter = 0;
      }

      if (this.isSitting()) {
         this.xRot = 0.0F;
      }

      this.updateSitAmount();
      this.handleEating();
      this.updateOnBackAnimation();
      this.updateRollAmount();
   }

   public boolean isScared() {
      return this.isWorried() && this.level.isThundering();
   }

   private void handleEating() {
      if (!this.isEating() && this.isSitting() && !this.isScared() && !this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
         this.eat(true);
      } else if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() || !this.isSitting()) {
         this.eat(false);
      }

      if (this.isEating()) {
         this.addEatingParticles();
         if (!this.level.isClientSide && this.getEatCounter() > 80 && this.random.nextInt(20) == 1) {
            if (this.getEatCounter() > 100 && this.isFoodOrCake(this.getItemBySlot(EquipmentSlot.MAINHAND))) {
               if (!this.level.isClientSide) {
                  this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
               }

               this.sit(false);
            }

            this.eat(false);
            return;
         }

         this.setEatCounter(this.getEatCounter() + 1);
      }

   }

   private void addEatingParticles() {
      if (this.getEatCounter() % 5 == 0) {
         this.playSound(SoundEvents.PANDA_EAT, 0.5F + 0.5F * (float)this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

         for(int var1 = 0; var1 < 6; ++var1) {
            Vec3 var2 = new Vec3(((double)this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double)this.random.nextFloat() - 0.5D) * 0.1D);
            var2 = var2.xRot(-this.xRot * 0.017453292F);
            var2 = var2.yRot(-this.yRot * 0.017453292F);
            double var3 = (double)(-this.random.nextFloat()) * 0.6D - 0.3D;
            Vec3 var5 = new Vec3(((double)this.random.nextFloat() - 0.5D) * 0.8D, var3, 1.0D + ((double)this.random.nextFloat() - 0.5D) * 0.4D);
            var5 = var5.yRot(-this.yBodyRot * 0.017453292F);
            var5 = var5.add(this.getX(), this.getEyeY() + 1.0D, this.getZ());
            this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItemBySlot(EquipmentSlot.MAINHAND)), var5.x, var5.y, var5.z, var2.x, var2.y + 0.05D, var2.z);
         }
      }

   }

   private void updateSitAmount() {
      this.sitAmountO = this.sitAmount;
      if (this.isSitting()) {
         this.sitAmount = Math.min(1.0F, this.sitAmount + 0.15F);
      } else {
         this.sitAmount = Math.max(0.0F, this.sitAmount - 0.19F);
      }

   }

   private void updateOnBackAnimation() {
      this.onBackAmountO = this.onBackAmount;
      if (this.isOnBack()) {
         this.onBackAmount = Math.min(1.0F, this.onBackAmount + 0.15F);
      } else {
         this.onBackAmount = Math.max(0.0F, this.onBackAmount - 0.19F);
      }

   }

   private void updateRollAmount() {
      this.rollAmountO = this.rollAmount;
      if (this.isRolling()) {
         this.rollAmount = Math.min(1.0F, this.rollAmount + 0.15F);
      } else {
         this.rollAmount = Math.max(0.0F, this.rollAmount - 0.19F);
      }

   }

   public float getSitAmount(float var1) {
      return Mth.lerp(var1, this.sitAmountO, this.sitAmount);
   }

   public float getLieOnBackAmount(float var1) {
      return Mth.lerp(var1, this.onBackAmountO, this.onBackAmount);
   }

   public float getRollAmount(float var1) {
      return Mth.lerp(var1, this.rollAmountO, this.rollAmount);
   }

   private void handleRoll() {
      ++this.rollCounter;
      if (this.rollCounter > 32) {
         this.roll(false);
      } else {
         if (!this.level.isClientSide) {
            Vec3 var1 = this.getDeltaMovement();
            if (this.rollCounter == 1) {
               float var2 = this.yRot * 0.017453292F;
               float var3 = this.isBaby() ? 0.1F : 0.2F;
               this.rollDelta = new Vec3(var1.x + (double)(-Mth.sin(var2) * var3), 0.0D, var1.z + (double)(Mth.cos(var2) * var3));
               this.setDeltaMovement(this.rollDelta.add(0.0D, 0.27D, 0.0D));
            } else if ((float)this.rollCounter != 7.0F && (float)this.rollCounter != 15.0F && (float)this.rollCounter != 23.0F) {
               this.setDeltaMovement(this.rollDelta.x, var1.y, this.rollDelta.z);
            } else {
               this.setDeltaMovement(0.0D, this.onGround ? 0.27D : var1.y, 0.0D);
            }
         }

      }
   }

   private void afterSneeze() {
      Vec3 var1 = this.getDeltaMovement();
      this.level.addParticle(ParticleTypes.SNEEZE, this.getX() - (double)(this.getBbWidth() + 1.0F) * 0.5D * (double)Mth.sin(this.yBodyRot * 0.017453292F), this.getEyeY() - 0.10000000149011612D, this.getZ() + (double)(this.getBbWidth() + 1.0F) * 0.5D * (double)Mth.cos(this.yBodyRot * 0.017453292F), var1.x, 0.0D, var1.z);
      this.playSound(SoundEvents.PANDA_SNEEZE, 1.0F, 1.0F);
      List var2 = this.level.getEntitiesOfClass(Panda.class, this.getBoundingBox().inflate(10.0D));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         Panda var4 = (Panda)var3.next();
         if (!var4.isBaby() && var4.onGround && !var4.isInWater() && var4.canPerformAction()) {
            var4.jumpFromGround();
         }
      }

      if (!this.level.isClientSide() && this.random.nextInt(700) == 0 && this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         this.spawnAtLocation(Items.SLIME_BALL);
      }

   }

   protected void pickUpItem(ItemEntity var1) {
      if (this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && PANDA_ITEMS.test(var1)) {
         this.onItemPickup(var1);
         ItemStack var2 = var1.getItem();
         this.setItemSlot(EquipmentSlot.MAINHAND, var2);
         this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0F;
         this.take(var1, var2.getCount());
         var1.discard();
      }

   }

   public boolean hurt(DamageSource var1, float var2) {
      this.sit(false);
      return super.hurt(var1, var2);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.setMainGene(Panda.Gene.getRandom(this.random));
      this.setHiddenGene(Panda.Gene.getRandom(this.random));
      this.setAttributes();
      if (var4 == null) {
         var4 = new AgeableMob.AgeableMobGroupData(0.2F);
      }

      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
   }

   public void setGeneFromParents(Panda var1, @Nullable Panda var2) {
      if (var2 == null) {
         if (this.random.nextBoolean()) {
            this.setMainGene(var1.getOneOfGenesRandomly());
            this.setHiddenGene(Panda.Gene.getRandom(this.random));
         } else {
            this.setMainGene(Panda.Gene.getRandom(this.random));
            this.setHiddenGene(var1.getOneOfGenesRandomly());
         }
      } else if (this.random.nextBoolean()) {
         this.setMainGene(var1.getOneOfGenesRandomly());
         this.setHiddenGene(var2.getOneOfGenesRandomly());
      } else {
         this.setMainGene(var2.getOneOfGenesRandomly());
         this.setHiddenGene(var1.getOneOfGenesRandomly());
      }

      if (this.random.nextInt(32) == 0) {
         this.setMainGene(Panda.Gene.getRandom(this.random));
      }

      if (this.random.nextInt(32) == 0) {
         this.setHiddenGene(Panda.Gene.getRandom(this.random));
      }

   }

   private Panda.Gene getOneOfGenesRandomly() {
      return this.random.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
   }

   public void setAttributes() {
      if (this.isWeak()) {
         this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(10.0D);
      }

      if (this.isLazy()) {
         this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.07000000029802322D);
      }

   }

   private void tryToSit() {
      if (!this.isInWater()) {
         this.setZza(0.0F);
         this.getNavigation().stop();
         this.sit(true);
      }

   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (this.isScared()) {
         return InteractionResult.PASS;
      } else if (this.isOnBack()) {
         this.setOnBack(false);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      } else if (this.isFood(var3)) {
         if (this.getTarget() != null) {
            this.gotBamboo = true;
         }

         if (this.isBaby()) {
            this.usePlayerItem(var1, var3);
            this.ageUp((int)((float)(-this.getAge() / 20) * 0.1F), true);
         } else if (!this.level.isClientSide && this.getAge() == 0 && this.canFallInLove()) {
            this.usePlayerItem(var1, var3);
            this.setInLove(var1);
         } else {
            if (this.level.isClientSide || this.isSitting() || this.isInWater()) {
               return InteractionResult.PASS;
            }

            this.tryToSit();
            this.eat(true);
            ItemStack var4 = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!var4.isEmpty() && !var1.getAbilities().instabuild) {
               this.spawnAtLocation(var4);
            }

            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(var3.getItem(), 1));
            this.usePlayerItem(var1, var3);
         }

         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.PASS;
      }
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      if (this.isAggressive()) {
         return SoundEvents.PANDA_AGGRESSIVE_AMBIENT;
      } else {
         return this.isWorried() ? SoundEvents.PANDA_WORRIED_AMBIENT : SoundEvents.PANDA_AMBIENT;
      }
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.PANDA_STEP, 0.15F, 1.0F);
   }

   public boolean isFood(ItemStack var1) {
      return var1.is(Blocks.BAMBOO.asItem());
   }

   private boolean isFoodOrCake(ItemStack var1) {
      return this.isFood(var1) || var1.is(Blocks.CAKE.asItem());
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.PANDA_DEATH;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PANDA_HURT;
   }

   public boolean canPerformAction() {
      return !this.isOnBack() && !this.isScared() && !this.isEating() && !this.isRolling() && !this.isSitting();
   }

   static {
      UNHAPPY_COUNTER = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.INT);
      SNEEZE_COUNTER = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.INT);
      EAT_COUNTER = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.INT);
      MAIN_GENE_ID = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.BYTE);
      HIDDEN_GENE_ID = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.BYTE);
      DATA_ID_FLAGS = SynchedEntityData.defineId(Panda.class, EntityDataSerializers.BYTE);
      BREED_TARGETING = (new TargetingConditions()).range(8.0D).allowSameTeam().allowInvulnerable();
      PANDA_ITEMS = (var0) -> {
         ItemStack var1 = var0.getItem();
         return (var1.is(Blocks.BAMBOO.asItem()) || var1.is(Blocks.CAKE.asItem())) && var0.isAlive() && !var0.hasPickUpDelay();
      };
   }

   static class PandaPanicGoal extends PanicGoal {
      private final Panda panda;

      public PandaPanicGoal(Panda var1, double var2) {
         super(var1, var2);
         this.panda = var1;
      }

      public boolean canUse() {
         if (!this.panda.isOnFire()) {
            return false;
         } else {
            BlockPos var1 = this.lookForWater(this.mob.level, this.mob, 5, 4);
            if (var1 != null) {
               this.posX = (double)var1.getX();
               this.posY = (double)var1.getY();
               this.posZ = (double)var1.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }

      public boolean canContinueToUse() {
         if (this.panda.isSitting()) {
            this.panda.getNavigation().stop();
            return false;
         } else {
            return super.canContinueToUse();
         }
      }
   }

   static class PandaHurtByTargetGoal extends HurtByTargetGoal {
      private final Panda panda;

      public PandaHurtByTargetGoal(Panda var1, Class<?>... var2) {
         super(var1, var2);
         this.panda = var1;
      }

      public boolean canContinueToUse() {
         if (!this.panda.gotBamboo && !this.panda.didBite) {
            return super.canContinueToUse();
         } else {
            this.panda.setTarget((LivingEntity)null);
            return false;
         }
      }

      protected void alertOther(Mob var1, LivingEntity var2) {
         if (var1 instanceof Panda && ((Panda)var1).isAggressive()) {
            var1.setTarget(var2);
         }

      }
   }

   static class PandaLieOnBackGoal extends Goal {
      private final Panda panda;
      private int cooldown;

      public PandaLieOnBackGoal(Panda var1) {
         super();
         this.panda = var1;
      }

      public boolean canUse() {
         return this.cooldown < this.panda.tickCount && this.panda.isLazy() && this.panda.canPerformAction() && this.panda.random.nextInt(400) == 1;
      }

      public boolean canContinueToUse() {
         if (!this.panda.isInWater() && (this.panda.isLazy() || this.panda.random.nextInt(600) != 1)) {
            return this.panda.random.nextInt(2000) != 1;
         } else {
            return false;
         }
      }

      public void start() {
         this.panda.setOnBack(true);
         this.cooldown = 0;
      }

      public void stop() {
         this.panda.setOnBack(false);
         this.cooldown = this.panda.tickCount + 200;
      }
   }

   class PandaSitGoal extends Goal {
      private int cooldown;

      public PandaSitGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      }

      public boolean canUse() {
         if (this.cooldown <= Panda.this.tickCount && !Panda.this.isBaby() && !Panda.this.isInWater() && Panda.this.canPerformAction() && Panda.this.getUnhappyCounter() <= 0) {
            List var1 = Panda.this.level.getEntitiesOfClass(ItemEntity.class, Panda.this.getBoundingBox().inflate(6.0D, 6.0D, 6.0D), Panda.PANDA_ITEMS);
            return !var1.isEmpty() || !Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         if (!Panda.this.isInWater() && (Panda.this.isLazy() || Panda.this.random.nextInt(600) != 1)) {
            return Panda.this.random.nextInt(2000) != 1;
         } else {
            return false;
         }
      }

      public void tick() {
         if (!Panda.this.isSitting() && !Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            Panda.this.tryToSit();
         }

      }

      public void start() {
         List var1 = Panda.this.level.getEntitiesOfClass(ItemEntity.class, Panda.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), Panda.PANDA_ITEMS);
         if (!var1.isEmpty() && Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            Panda.this.getNavigation().moveTo((Entity)var1.get(0), 1.2000000476837158D);
         } else if (!Panda.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()) {
            Panda.this.tryToSit();
         }

         this.cooldown = 0;
      }

      public void stop() {
         ItemStack var1 = Panda.this.getItemBySlot(EquipmentSlot.MAINHAND);
         if (!var1.isEmpty()) {
            Panda.this.spawnAtLocation(var1);
            Panda.this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
            int var2 = Panda.this.isLazy() ? Panda.this.random.nextInt(50) + 10 : Panda.this.random.nextInt(150) + 10;
            this.cooldown = Panda.this.tickCount + var2 * 20;
         }

         Panda.this.sit(false);
      }
   }

   static class PandaAvoidGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final Panda panda;

      public PandaAvoidGoal(Panda var1, Class<T> var2, float var3, double var4, double var6) {
         Predicate var10006 = EntitySelector.NO_SPECTATORS;
         super(var1, var2, var3, var4, var6, var10006::test);
         this.panda = var1;
      }

      public boolean canUse() {
         return this.panda.isWorried() && this.panda.canPerformAction() && super.canUse();
      }
   }

   class PandaBreedGoal extends BreedGoal {
      private final Panda panda;
      private int unhappyCooldown;

      public PandaBreedGoal(Panda var2, double var3) {
         super(var2, var3);
         this.panda = var2;
      }

      public boolean canUse() {
         if (super.canUse() && this.panda.getUnhappyCounter() == 0) {
            if (!this.canFindBamboo()) {
               if (this.unhappyCooldown <= this.panda.tickCount) {
                  this.panda.setUnhappyCounter(32);
                  this.unhappyCooldown = this.panda.tickCount + 600;
                  if (this.panda.isEffectiveAi()) {
                     Player var1 = this.level.getNearestPlayer(Panda.BREED_TARGETING, this.panda);
                     this.panda.lookAtPlayerGoal.setTarget(var1);
                  }
               }

               return false;
            } else {
               return true;
            }
         } else {
            return false;
         }
      }

      private boolean canFindBamboo() {
         BlockPos var1 = this.panda.blockPosition();
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();

         for(int var3 = 0; var3 < 3; ++var3) {
            for(int var4 = 0; var4 < 8; ++var4) {
               for(int var5 = 0; var5 <= var4; var5 = var5 > 0 ? -var5 : 1 - var5) {
                  for(int var6 = var5 < var4 && var5 > -var4 ? var4 : 0; var6 <= var4; var6 = var6 > 0 ? -var6 : 1 - var6) {
                     var2.setWithOffset(var1, var5, var3, var6);
                     if (this.level.getBlockState(var2).is(Blocks.BAMBOO)) {
                        return true;
                     }
                  }
               }
            }
         }

         return false;
      }
   }

   static class PandaSneezeGoal extends Goal {
      private final Panda panda;

      public PandaSneezeGoal(Panda var1) {
         super();
         this.panda = var1;
      }

      public boolean canUse() {
         if (this.panda.isBaby() && this.panda.canPerformAction()) {
            if (this.panda.isWeak() && this.panda.random.nextInt(500) == 1) {
               return true;
            } else {
               return this.panda.random.nextInt(6000) == 1;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         this.panda.sneeze(true);
      }
   }

   static class PandaRollGoal extends Goal {
      private final Panda panda;

      public PandaRollGoal(Panda var1) {
         super();
         this.panda = var1;
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK, Goal.Flag.JUMP));
      }

      public boolean canUse() {
         if ((this.panda.isBaby() || this.panda.isPlayful()) && this.panda.onGround) {
            if (!this.panda.canPerformAction()) {
               return false;
            } else {
               float var1 = this.panda.yRot * 0.017453292F;
               int var2 = 0;
               int var3 = 0;
               float var4 = -Mth.sin(var1);
               float var5 = Mth.cos(var1);
               if ((double)Math.abs(var4) > 0.5D) {
                  var2 = (int)((float)var2 + var4 / Math.abs(var4));
               }

               if ((double)Math.abs(var5) > 0.5D) {
                  var3 = (int)((float)var3 + var5 / Math.abs(var5));
               }

               if (this.panda.level.getBlockState(this.panda.blockPosition().offset(var2, -1, var3)).isAir()) {
                  return true;
               } else if (this.panda.isPlayful() && this.panda.random.nextInt(60) == 1) {
                  return true;
               } else {
                  return this.panda.random.nextInt(500) == 1;
               }
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         return false;
      }

      public void start() {
         this.panda.roll(true);
      }

      public boolean isInterruptable() {
         return false;
      }
   }

   static class PandaLookAtPlayerGoal extends LookAtPlayerGoal {
      private final Panda panda;

      public PandaLookAtPlayerGoal(Panda var1, Class<? extends LivingEntity> var2, float var3) {
         super(var1, var2, var3);
         this.panda = var1;
      }

      public void setTarget(LivingEntity var1) {
         this.lookAt = var1;
      }

      public boolean canContinueToUse() {
         return this.lookAt != null && super.canContinueToUse();
      }

      public boolean canUse() {
         if (this.mob.getRandom().nextFloat() >= this.probability) {
            return false;
         } else {
            if (this.lookAt == null) {
               if (this.lookAtType == Player.class) {
                  this.lookAt = this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
               } else {
                  this.lookAt = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.lookAtType, this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0D, (double)this.lookDistance), (var0) -> {
                     return true;
                  }), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
               }
            }

            return this.panda.canPerformAction() && this.lookAt != null;
         }
      }

      public void tick() {
         if (this.lookAt != null) {
            super.tick();
         }

      }
   }

   static class PandaAttackGoal extends MeleeAttackGoal {
      private final Panda panda;

      public PandaAttackGoal(Panda var1, double var2, boolean var4) {
         super(var1, var2, var4);
         this.panda = var1;
      }

      public boolean canUse() {
         return this.panda.canPerformAction() && super.canUse();
      }
   }

   static class PandaMoveControl extends MoveControl {
      private final Panda panda;

      public PandaMoveControl(Panda var1) {
         super(var1);
         this.panda = var1;
      }

      public void tick() {
         if (this.panda.canPerformAction()) {
            super.tick();
         }
      }
   }

   public static enum Gene {
      NORMAL(0, "normal", false),
      LAZY(1, "lazy", false),
      WORRIED(2, "worried", false),
      PLAYFUL(3, "playful", false),
      BROWN(4, "brown", true),
      WEAK(5, "weak", true),
      AGGRESSIVE(6, "aggressive", false);

      private static final Panda.Gene[] BY_ID = (Panda.Gene[])Arrays.stream(values()).sorted(Comparator.comparingInt(Panda.Gene::getId)).toArray((var0) -> {
         return new Panda.Gene[var0];
      });
      private final int id;
      private final String name;
      private final boolean isRecessive;

      private Gene(int var3, String var4, boolean var5) {
         this.id = var3;
         this.name = var4;
         this.isRecessive = var5;
      }

      public int getId() {
         return this.id;
      }

      public String getName() {
         return this.name;
      }

      public boolean isRecessive() {
         return this.isRecessive;
      }

      private static Panda.Gene getVariantFromGenes(Panda.Gene var0, Panda.Gene var1) {
         if (var0.isRecessive()) {
            return var0 == var1 ? var0 : NORMAL;
         } else {
            return var0;
         }
      }

      public static Panda.Gene byId(int var0) {
         if (var0 < 0 || var0 >= BY_ID.length) {
            var0 = 0;
         }

         return BY_ID[var0];
      }

      public static Panda.Gene byName(String var0) {
         Panda.Gene[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Panda.Gene var4 = var1[var3];
            if (var4.name.equals(var0)) {
               return var4;
            }
         }

         return NORMAL;
      }

      public static Panda.Gene getRandom(Random var0) {
         int var1 = var0.nextInt(16);
         if (var1 == 0) {
            return LAZY;
         } else if (var1 == 1) {
            return WORRIED;
         } else if (var1 == 2) {
            return PLAYFUL;
         } else if (var1 == 4) {
            return AGGRESSIVE;
         } else if (var1 < 9) {
            return WEAK;
         } else {
            return var1 < 11 ? BROWN : NORMAL;
         }
      }
   }
}
