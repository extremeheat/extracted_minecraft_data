package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.VariantHolder;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarrotBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class Rabbit extends Animal implements VariantHolder<Rabbit.Variant> {
   public static final double STROLL_SPEED_MOD = 0.6;
   public static final double BREED_SPEED_MOD = 0.8;
   public static final double FOLLOW_SPEED_MOD = 1.0;
   public static final double FLEE_SPEED_MOD = 2.2;
   public static final double ATTACK_SPEED_MOD = 1.4;
   private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(Rabbit.class, EntityDataSerializers.INT);
   private static final ResourceLocation KILLER_BUNNY = new ResourceLocation("killer_bunny");
   public static final int EVIL_ATTACK_POWER = 8;
   public static final int EVIL_ARMOR_VALUE = 8;
   private static final int MORE_CARROTS_DELAY = 40;
   private int jumpTicks;
   private int jumpDuration;
   private boolean wasOnGround;
   private int jumpDelayTicks;
   int moreCarrotTicks;

   public Rabbit(EntityType<? extends Rabbit> var1, Level var2) {
      super(var1, var2);
      this.jumpControl = new Rabbit.RabbitJumpControl(this);
      this.moveControl = new Rabbit.RabbitMoveControl(this);
      this.setSpeedModifier(0.0);
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
      this.goalSelector.addGoal(1, new Rabbit.RabbitPanicGoal(this, 2.2));
      this.goalSelector.addGoal(2, new BreedGoal(this, 0.8));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.0, Ingredient.of(Items.CARROT, Items.GOLDEN_CARROT, Blocks.DANDELION), false));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal<>(this, Player.class, 8.0F, 2.2, 2.2));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal<>(this, Wolf.class, 10.0F, 2.2, 2.2));
      this.goalSelector.addGoal(4, new Rabbit.RabbitAvoidEntityGoal<>(this, Monster.class, 4.0F, 2.2, 2.2));
      this.goalSelector.addGoal(5, new Rabbit.RaidGardenGoal(this));
      this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.6));
      this.goalSelector.addGoal(11, new LookAtPlayerGoal(this, Player.class, 10.0F));
   }

   @Override
   protected float getJumpPower() {
      float var1 = 0.3F;
      if (this.horizontalCollision || this.moveControl.hasWanted() && this.moveControl.getWantedY() > this.getY() + 0.5) {
         var1 = 0.5F;
      }

      Path var2 = this.navigation.getPath();
      if (var2 != null && !var2.isDone()) {
         Vec3 var3 = var2.getNextEntityPos(this);
         if (var3.y > this.getY() + 0.5) {
            var1 = 0.5F;
         }
      }

      if (this.moveControl.getSpeedModifier() <= 0.6) {
         var1 = 0.2F;
      }

      return super.getJumpPower(var1 / 0.42F);
   }

   @Override
   protected void jumpFromGround() {
      super.jumpFromGround();
      double var1 = this.moveControl.getSpeedModifier();
      if (var1 > 0.0) {
         double var3 = this.getDeltaMovement().horizontalDistanceSqr();
         if (var3 < 0.01) {
            this.moveRelative(0.1F, new Vec3(0.0, 0.0, 1.0));
         }
      }

      if (!this.level().isClientSide) {
         this.level().broadcastEntityEvent(this, (byte)1);
      }
   }

   public float getJumpCompletion(float var1) {
      return this.jumpDuration == 0 ? 0.0F : ((float)this.jumpTicks + var1) / (float)this.jumpDuration;
   }

   public void setSpeedModifier(double var1) {
      this.getNavigation().setSpeedModifier(var1);
      this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), var1);
   }

   @Override
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

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_TYPE_ID, Rabbit.Variant.BROWN.id);
   }

   @Override
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

      if (this.onGround()) {
         if (!this.wasOnGround) {
            this.setJumping(false);
            this.checkLandingDelay();
         }

         if (this.getVariant() == Rabbit.Variant.EVIL && this.jumpDelayTicks == 0) {
            LivingEntity var1 = this.getTarget();
            if (var1 != null && this.distanceToSqr(var1) < 16.0) {
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
               if (var2 != null && !var2.isDone()) {
                  var3 = var2.getNextEntityPos(this);
               }

               this.facePoint(var3.x, var3.z);
               this.startJumping();
            }
         } else if (!var4.canJump()) {
            this.enableJumpControl();
         }
      }

      this.wasOnGround = this.onGround();
   }

   @Override
   public boolean canSpawnSprintParticle() {
      return false;
   }

   private void facePoint(double var1, double var3) {
      this.setYRot((float)(Mth.atan2(var3 - this.getZ(), var1 - this.getX()) * 57.2957763671875) - 90.0F);
   }

   private void enableJumpControl() {
      ((Rabbit.RabbitJumpControl)this.jumpControl).setCanJump(true);
   }

   private void disableJumpControl() {
      ((Rabbit.RabbitJumpControl)this.jumpControl).setCanJump(false);
   }

   private void setLandingDelay() {
      if (this.moveControl.getSpeedModifier() < 2.2) {
         this.jumpDelayTicks = 10;
      } else {
         this.jumpDelayTicks = 1;
      }
   }

   private void checkLandingDelay() {
      this.setLandingDelay();
      this.disableJumpControl();
   }

   @Override
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

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 3.0).add(Attributes.MOVEMENT_SPEED, 0.30000001192092896);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("RabbitType", this.getVariant().id);
      var1.putInt("MoreCarrotTicks", this.moreCarrotTicks);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setVariant(Rabbit.Variant.byId(var1.getInt("RabbitType")));
      this.moreCarrotTicks = var1.getInt("MoreCarrotTicks");
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.RABBIT_JUMP;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.RABBIT_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.RABBIT_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.RABBIT_DEATH;
   }

   @Override
   public boolean doHurtTarget(Entity var1) {
      if (this.getVariant() == Rabbit.Variant.EVIL) {
         this.playSound(SoundEvents.RABBIT_ATTACK, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
         return var1.hurt(this.damageSources().mobAttack(this), 8.0F);
      } else {
         return var1.hurt(this.damageSources().mobAttack(this), 3.0F);
      }
   }

   @Override
   public SoundSource getSoundSource() {
      return this.getVariant() == Rabbit.Variant.EVIL ? SoundSource.HOSTILE : SoundSource.NEUTRAL;
   }

   private static boolean isTemptingItem(ItemStack var0) {
      return var0.is(Items.CARROT) || var0.is(Items.GOLDEN_CARROT) || var0.is(Blocks.DANDELION.asItem());
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Nullable
   public Rabbit getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      Rabbit var3 = EntityType.RABBIT.create(var1);
      if (var3 != null) {
         Rabbit.Variant var4;
         var4 = getRandomRabbitVariant(var1, this.blockPosition());
         label16:
         if (this.random.nextInt(20) != 0) {
            if (var2 instanceof Rabbit var5 && this.random.nextBoolean()) {
               var4 = var5.getVariant();
               break label16;
            }

            var4 = this.getVariant();
         }

         var3.setVariant(var4);
      }

      return var3;
   }

   @Override
   public boolean isFood(ItemStack var1) {
      return isTemptingItem(var1);
   }

   public Rabbit.Variant getVariant() {
      return Rabbit.Variant.byId(this.entityData.get(DATA_TYPE_ID));
   }

   public void setVariant(Rabbit.Variant var1) {
      if (var1 == Rabbit.Variant.EVIL) {
         this.getAttribute(Attributes.ARMOR).setBaseValue(8.0);
         this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.4, true));
         this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
         this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Wolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomName(Component.translatable(Util.makeDescriptionId("entity", KILLER_BUNNY)));
         }
      }

      this.entityData.set(DATA_TYPE_ID, var1.id);
   }

   @Nullable
   @Override
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4) {
      Rabbit.Variant var5 = getRandomRabbitVariant(var1, this.blockPosition());
      if (var4 instanceof Rabbit.RabbitGroupData) {
         var5 = ((Rabbit.RabbitGroupData)var4).variant;
      } else {
         var4 = new Rabbit.RabbitGroupData(var5);
      }

      this.setVariant(var5);
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4);
   }

   private static Rabbit.Variant getRandomRabbitVariant(LevelAccessor var0, BlockPos var1) {
      Holder var2 = var0.getBiome(var1);
      int var3 = var0.getRandom().nextInt(100);
      if (var2.is(BiomeTags.SPAWNS_WHITE_RABBITS)) {
         return var3 < 80 ? Rabbit.Variant.WHITE : Rabbit.Variant.WHITE_SPLOTCHED;
      } else if (var2.is(BiomeTags.SPAWNS_GOLD_RABBITS)) {
         return Rabbit.Variant.GOLD;
      } else {
         return var3 < 50 ? Rabbit.Variant.BROWN : (var3 < 90 ? Rabbit.Variant.SALT : Rabbit.Variant.BLACK);
      }
   }

   public static boolean checkRabbitSpawnRules(EntityType<Rabbit> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      return var1.getBlockState(var3.below()).is(BlockTags.RABBITS_SPAWNABLE_ON) && isBrightEnoughToSpawn(var1, var3);
   }

   boolean wantsMoreFood() {
      return this.moreCarrotTicks <= 0;
   }

   @Override
   public void handleEntityEvent(byte var1) {
      if (var1 == 1) {
         this.spawnSprintParticle();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleEntityEvent(var1);
      }
   }

   @Override
   public Vec3 getLeashOffset() {
      return new Vec3(0.0, (double)(0.6F * this.getEyeHeight()), (double)(this.getBbWidth() * 0.4F));
   }

   static class RabbitAvoidEntityGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {
      private final Rabbit rabbit;

      public RabbitAvoidEntityGoal(Rabbit var1, Class<T> var2, float var3, double var4, double var6) {
         super(var1, var2, var3, var4, var6);
         this.rabbit = var1;
      }

      @Override
      public boolean canUse() {
         return this.rabbit.getVariant() != Rabbit.Variant.EVIL && super.canUse();
      }
   }

   public static class RabbitGroupData extends AgeableMob.AgeableMobGroupData {
      public final Rabbit.Variant variant;

      public RabbitGroupData(Rabbit.Variant var1) {
         super(1.0F);
         this.variant = var1;
      }
   }

   public static class RabbitJumpControl extends JumpControl {
      private final Rabbit rabbit;
      private boolean canJump;

      public RabbitJumpControl(Rabbit var1) {
         super(var1);
         this.rabbit = var1;
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

      @Override
      public void tick() {
         if (this.jump) {
            this.rabbit.startJumping();
            this.jump = false;
         }
      }
   }

   static class RabbitMoveControl extends MoveControl {
      private final Rabbit rabbit;
      private double nextJumpSpeed;

      public RabbitMoveControl(Rabbit var1) {
         super(var1);
         this.rabbit = var1;
      }

      @Override
      public void tick() {
         if (this.rabbit.onGround() && !this.rabbit.jumping && !((Rabbit.RabbitJumpControl)this.rabbit.jumpControl).wantJump()) {
            this.rabbit.setSpeedModifier(0.0);
         } else if (this.hasWanted()) {
            this.rabbit.setSpeedModifier(this.nextJumpSpeed);
         }

         super.tick();
      }

      @Override
      public void setWantedPosition(double var1, double var3, double var5, double var7) {
         if (this.rabbit.isInWater()) {
            var7 = 1.5;
         }

         super.setWantedPosition(var1, var3, var5, var7);
         if (var7 > 0.0) {
            this.nextJumpSpeed = var7;
         }
      }
   }

   static class RabbitPanicGoal extends PanicGoal {
      private final Rabbit rabbit;

      public RabbitPanicGoal(Rabbit var1, double var2) {
         super(var1, var2);
         this.rabbit = var1;
      }

      @Override
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
         super(var1, 0.699999988079071, 16);
         this.rabbit = var1;
      }

      @Override
      public boolean canUse() {
         if (this.nextStartTick <= 0) {
            if (!this.rabbit.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
               return false;
            }

            this.canRaid = false;
            this.wantsToRaid = this.rabbit.wantsMoreFood();
         }

         return super.canUse();
      }

      @Override
      public boolean canContinueToUse() {
         return this.canRaid && super.canContinueToUse();
      }

      @Override
      public void tick() {
         super.tick();
         this.rabbit
            .getLookControl()
            .setLookAt(
               (double)this.blockPos.getX() + 0.5,
               (double)(this.blockPos.getY() + 1),
               (double)this.blockPos.getZ() + 0.5,
               10.0F,
               (float)this.rabbit.getMaxHeadXRot()
            );
         if (this.isReachedTarget()) {
            Level var1 = this.rabbit.level();
            BlockPos var2 = this.blockPos.above();
            BlockState var3 = var1.getBlockState(var2);
            Block var4 = var3.getBlock();
            if (this.canRaid && var4 instanceof CarrotBlock) {
               int var5 = var3.getValue(CarrotBlock.AGE);
               if (var5 == 0) {
                  var1.setBlock(var2, Blocks.AIR.defaultBlockState(), 2);
                  var1.destroyBlock(var2, true, this.rabbit);
               } else {
                  var1.setBlock(var2, var3.setValue(CarrotBlock.AGE, Integer.valueOf(var5 - 1)), 2);
                  var1.gameEvent(GameEvent.BLOCK_CHANGE, var2, GameEvent.Context.of(this.rabbit));
                  var1.levelEvent(2001, var2, Block.getId(var3));
               }

               this.rabbit.moreCarrotTicks = 40;
            }

            this.canRaid = false;
            this.nextStartTick = 10;
         }
      }

      @Override
      protected boolean isValidTarget(LevelReader var1, BlockPos var2) {
         BlockState var3 = var1.getBlockState(var2);
         if (var3.is(Blocks.FARMLAND) && this.wantsToRaid && !this.canRaid) {
            var3 = var1.getBlockState(var2.above());
            if (var3.getBlock() instanceof CarrotBlock && ((CarrotBlock)var3.getBlock()).isMaxAge(var3)) {
               this.canRaid = true;
               return true;
            }
         }

         return false;
      }
   }

   public static enum Variant implements StringRepresentable {
      BROWN(0, "brown"),
      WHITE(1, "white"),
      BLACK(2, "black"),
      WHITE_SPLOTCHED(3, "white_splotched"),
      GOLD(4, "gold"),
      SALT(5, "salt"),
      EVIL(99, "evil");

      private static final IntFunction<Rabbit.Variant> BY_ID = ByIdMap.sparse(Rabbit.Variant::id, values(), BROWN);
      public static final Codec<Rabbit.Variant> CODEC = StringRepresentable.fromEnum(Rabbit.Variant::values);
      final int id;
      private final String name;

      private Variant(int var3, String var4) {
         this.id = var3;
         this.name = var4;
      }

      @Override
      public String getSerializedName() {
         return this.name;
      }

      public int id() {
         return this.id;
      }

      public static Rabbit.Variant byId(int var0) {
         return BY_ID.apply(var0);
      }
   }
}
