package net.minecraft.world.entity.animal;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Rabbit extends Animal {
   private static final EntityDataAccessor DATA_TYPE_ID;
   private static final ResourceLocation KILLER_BUNNY;
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int jumpDelayTicks;
   private int moreCarrotTicks;

   public Rabbit(EntityType var1, Level var2) {
      super(var1, var2);
      this.jumpControl = new Rabbit.RabbitJumpControl(this);
      this.moveControl = new Rabbit.RabbitMoveControl(this);
      this.setSpeedModifier(0.0D);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new Rabbit.RabbitPanicGoal(this, 2.2D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 0.8D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0D, Ingredient.of(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal(this, Player.class, 8.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal(this, Wolf.class, 10.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal(this, Monster.class, 4.0F, 2.2D, 2.2D));
      this.goalSelector.addGoal(5, new Rabbit.RaidGardenGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
   }

   protected float getJumpPower() {
      if (!this.horizontalCollision && (!this.moveControl.hasWanted() || this.moveControl.getWantedY() <= this.getY() + 0.5D)) {
         Path var1 = this.navigation.getPath();
         if (var1 != null && var1.getIndex() < var1.getSize()) {
            Vec3 var2 = var1.currentPos(this);
            if (var2.y > this.getY() + 0.5D) {
               return 0.5F;
            }
         }

         return this.moveControl.getSpeedModifier() <= 0.6D ? 0.2F : 0.3F;
      } else {
         return 0.5F;
      }
   }

   protected void jumpFromGround() {
      super.jumpFromGround();
      double var1 = this.moveControl.getSpeedModifier();
      if (var1 > 0.0D) {
         double var3 = getHorizontalDistanceSqr(this.getDeltaMovement());
         if (var3 < 0.01D) {
            this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
         }
      }

      if (!this.level.isClientSide) {
         this.level.broadcastEntityEvent(this, (byte)1);
      }

   }

   public float getJumpCompletion(float var1) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + var1) / (float)this.jumpDuration;
   }

   public void setSpeedModifier(double var1) {
      this.getNavigation().setSpeedModifier(var1);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), var1);
   }

   public void setJumping(boolean var1) {
      super.setJumping(var1);
      if (var1) {
         this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void startJumping() {
      this.setJumping(true);
      this.jumpDuration = 10;
      this.jumpTicks = 0;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE_ID, 0);
   }

   public void customServerAiStep() {
      if (this.jumpDelayTicks > 0) {
         --this.jumpDelayTicks;
      }

      if (this.moreCarrotTicks > 0) {
         this.moreCarrotTicks -= this.random.nextInt(3);
         if (this.moreCarrotTicks < 0) {
            this.moreCarrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.getRabbitType() == 99 && this.jumpDelayTicks == 0) {
            LivingEntity var1 = this.getTarget();
            if (var1 != null && this.distanceToSqr(var1) < 16.0D) {
               this.facePoint(var1.getX(), var1.getZ());
               this.moveControl.setWantedPosition(var1.getX(), var1.getY(), var1.getZ(), this.moveControl.getSpeedModifier());
               this.startJumping();
               this.wasOnGround = true;
            }
         }

         Rabbit.RabbitJumpControl var4 = (Rabbit.RabbitJumpControl)this.jumpControl;
         if (!var4.wantJump()) {
            if (this.moveControl.hasWanted() && this.jumpDelayTicks == 0) {
               Path var2 = this.navigation.getPath();
               Vec3 var3 = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
               if (var2 != null && var2.getIndex() < var2.getSize()) {
                  var3 = var2.currentPos(this);
               }

               this.facePoint(var3.x, var3.z);
               this.startJumping();
            }
         } else if (!var4.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround;
   }

   public void updateSprintingState() {
   }

   private void facePoint(double var1, double var3) {
      this.yRot = (float)(Mth.atan2(var3 - this.getZ(), var1 - this.getX()) * 57.2957763671875D) - 90.0F;
   }

   private void enableJumpControl() {
      ((Rabbit.RabbitJumpControl)this.jumpControl).setCanJump(true);
   }

   private void disableJumpControl() {
      ((Rabbit.RabbitJumpControl)this.jumpControl).setCanJump(false);
   }

   private void setLandingDelay() {
      if (this.moveControl.getSpeedModifier() < 2.2D) {
         this.jumpDelayTicks = 10;
      } else {
         this.jumpDelayTicks = 1;
      }

   }

   private void checkLandingDelay() {
      this.setLandingDelay();
      this.disableJumpControl();
   }

   public void aiStep() {
      super.aiStep();
      if (this.jumpTicks != this.jumpDuration) {
         ++this.jumpTicks;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
         this.setJumping(false);
      }

   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.30000001192092896D);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("RabbitType", this.getRabbitType());
      var1.putInt("MoreCarrotTicks", this.moreCarrotTicks);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setRabbitType(var1.getInt("RabbitType"));
      this.moreCarrotTicks = var1.getInt("MoreCarrotTicks");
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.RABBIT_JUMP;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.RABBIT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.RABBIT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.RABBIT_DEATH;
   }

   public boolean doHurtTarget(Entity var1) {
      if (this.getRabbitType() == 99) {
         this.playSound(SoundEvents.RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         return var1.hurt(DamageSource.mobAttack(this), 8.0F);
      } else {
         return var1.hurt(DamageSource.mobAttack(this), 3.0F);
      }
   }

   public SoundSource getSoundSource() {
      return this.getRabbitType() == 99 ? SoundSource.HOSTILE : SoundSource.NEUTRAL;
   }

   public boolean hurt(DamageSource var1, float var2) {
      return this.isInvulnerableTo(var1) ? false : super.hurt(var1, var2);
   }

   private boolean isTemptingItem(Item var1) {
      return var1 == Items.CARROT || var1 == Items.GOLDEN_CARROT || var1 == Blocks.DANDELION.asItem();
   }

   public Rabbit getBreedOffspring(AgableMob var1) {
      Rabbit var2 = (Rabbit)EntityType.RABBIT.create(this.level);
      int var3 = this.getRandomRabbitType(this.level);
      if (this.random.nextInt(20) != 0) {
         if (var1 instanceof Rabbit && this.random.nextBoolean()) {
            var3 = ((Rabbit)var1).getRabbitType();
         } else {
            var3 = this.getRabbitType();
         }
      }

      var2.setRabbitType(var3);
      return var2;
   }

   public boolean isFood(ItemStack var1) {
      return this.isTemptingItem(var1.getItem());
   }

   public int getRabbitType() {
      return (Integer)this.entityData.get(DATA_TYPE_ID);
   }

   public void setRabbitType(int var1) {
      if (var1 == 99) {
         this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(8.0D);
         this.goalSelector.addGoal(4, new Rabbit.EvilRabbitAttackGoal(this));
         this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Wolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomName(new TranslatableComponent(Util.makeDescriptionId("entity", KILLER_BUNNY), new Object[0]));
         }
      }

      this.entityData.set(DATA_TYPE_ID, var1);
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      int var6 = this.getRandomRabbitType(var1);
      if (var4 instanceof Rabbit.RabbitGroupData) {
         var6 = ((Rabbit.RabbitGroupData)var4).rabbitType;
      } else {
         var4 = new Rabbit.RabbitGroupData(var6);
      }

      this.setRabbitType(var6);
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
   }

   private int getRandomRabbitType(LevelAccessor var1) {
      Biome var2 = var1.getBiome(new BlockPos(this));
      int var3 = this.random.nextInt(100);
      if (var2.getPrecipitation() == Biome.Precipitation.SNOW) {
         return var3 < 80 ? 1 : 3;
      } else if (var2.getBiomeCategory() == Biome.BiomeCategory.DESERT) {
         return 4;
      } else {
         return var3 < 50 ? 0 : (var3 < 90 ? 5 : 2);
      }
   }

   public static boolean checkRabbitSpawnRules(EntityType var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      Block var5 = var1.getBlockState(var3.below()).getBlock();
      return (var5 == Blocks.GRASS_BLOCK || var5 == Blocks.SNOW || var5 == Blocks.SAND) && var1.getRawBrightness(var3, 0) > 8;
   }

   private boolean wantsMoreFood() {
      return this.moreCarrotTicks == 0;
   }

   public void handleEntityEvent(byte var1) {
      if (var1 == 1) {
         this.doSprintParticleEffect();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(var1);
      }

   }

   // $FF: synthetic method
   public AgableMob getBreedOffspring(AgableMob var1) {
      return this.getBreedOffspring(var1);
   }

   static {
      DATA_TYPE_ID = SynchedEntityData.defineId(Rabbit.class, EntityDataSerializers.INT);
      KILLER_BUNNY = new ResourceLocation("killer_bunny");
   }

   static class EvilRabbitAttackGoal extends MeleeAttackGoal {
      public EvilRabbitAttackGoal(Rabbit var1) {
         super(var1, 1.4D, true);
      }

      protected double getAttackReachSqr(LivingEntity var1) {
         return (double)(4.0F + var1.getBbWidth());
      }
   }

   static class RabbitPanicGoal extends PanicGoal {
      private final Rabbit rabbit;

      public RabbitPanicGoal(Rabbit var1, double var2) {
         super(var1, var2);
         this.rabbit = var1;
      }

      public void tick() {
         super.tick();
         this.rabbit.setSpeedModifier(this.speedModifier);
      }
   }

   static class RaidGardenGoal extends MoveToBlockGoal {
      private final Rabbit rabbit;
      private boolean wantsToRaid;
      private boolean canRaid;

      public RaidGardenGoal(Rabbit var1) {
         super(var1, 0.699999988079071D, 16);
         this.rabbit = var1;
      }

      public boolean canUse() {
         if (this.nextStartTick <= 0) {
            if (!this.rabbit.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.rabbit.wantsMoreFood();
            this.wantsToRaid = true;
         }

         return super.canUse();
      }

      public boolean canContinueToUse() {
         return this.canRaid && super.canContinueToUse();
      }

      public void tick() {
         super.tick();
         this.rabbit.getLookControl().setLookAt((double)this.blockPos.getX() + 0.5D, (double)(this.blockPos.getY() + 1), (double)this.blockPos.getZ() + 0.5D, 10.0F, (float)this.rabbit.getMaxHeadXRot());
         if (this.isReachedTarget()) {
            Level var1 = this.rabbit.level;
            BlockPos var2 = this.blockPos.above();
            BlockState var3 = var1.getBlockState(var2);
            Block var4 = var3.getBlock();
            if (this.canRaid && var4 instanceof CarrotBlock) {
               Integer var5 = (Integer)var3.getValue(CarrotBlock.AGE);
               if (var5 == 0) {
                  var1.setBlock(var2, Blocks.AIR.defaultBlockState(), 2);
                  var1.destroyBlock(var2, true, this.rabbit);
               } else {
                  var1.setBlock(var2, (BlockState)var3.setValue(CarrotBlock.AGE, var5 - 1), 2);
                  var1.levelEvent(2001, var2, Block.getId(var3));
               }

               this.rabbit.moreCarrotTicks = 40;
            }

            this.canRaid = false;
            this.nextStartTick = 10;
         }

      }

      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         Block var3 = var1.getBlockState(var2).getBlock();
         if (var3 == Blocks.FARMLAND && this.wantsToRaid && !this.canRaid) {
            var2 = var2.above();
            BlockState var4 = var1.getBlockState(var2);
            var3 = var4.getBlock();
            if (var3 instanceof CarrotBlock && ((CarrotBlock)var3).isMaxAge(var4)) {
               this.canRaid = true;
               return true;
            }
         }

         return false;
      }
   }

   static class RabbitAvoidEntityGoal extends AvoidEntityGoal {
      private final Rabbit rabbit;

      public RabbitAvoidEntityGoal(Rabbit var1, Class var2, float var3, double var4, double var6) {
         super(var1, var2, var3, var4, var6);
         this.rabbit = var1;
      }

      public boolean canUse() {
         return this.rabbit.getRabbitType() != 99 && super.canUse();
      }
   }

   static class RabbitMoveControl extends MoveControl {
      private final Rabbit rabbit;
      private double nextJumpSpeed;

      public RabbitMoveControl(Rabbit var1) {
         super(var1);
         this.rabbit = var1;
      }

      public void tick() {
         if (this.rabbit.onGround && !this.rabbit.jumping && !((Rabbit.RabbitJumpControl)this.rabbit.jumpControl).wantJump()) {
            this.rabbit.setSpeedModifier(0.0D);
         } else if (this.hasWanted()) {
            this.rabbit.setSpeedModifier(this.nextJumpSpeed);
         }

         super.tick();
      }

      public void setWantedPosition(double var1, double var3, double var5, double var7) {
         if (this.rabbit.isInWater()) {
            var7 = 1.5D;
         }

         super.setWantedPosition(var1, var3, var5, var7);
         if (var7 > 0.0D) {
            this.nextJumpSpeed = var7;
         }

      }
   }

   public class RabbitJumpControl extends JumpControl {
      private final Rabbit rabbit;
      private boolean canJump;

      public RabbitJumpControl(Rabbit var2) {
         super(var2);
         this.rabbit = var2;
      }

      public boolean wantJump() {
         return this.jump;
      }

      public boolean canJump() {
         return this.canJump;
      }

      public void setCanJump(boolean var1) {
         this.canJump = var1;
      }

      public void tick() {
         if (this.jump) {
            this.rabbit.startJumping();
            this.jump = false;
         }

      }
   }

   public static class RabbitGroupData extends AgableMob.AgableMobGroupData {
      public final int rabbitType;

      public RabbitGroupData(int var1) {
         this.rabbitType = var1;
         this.setBabySpawnChance(1.0F);
      }
   }
}
