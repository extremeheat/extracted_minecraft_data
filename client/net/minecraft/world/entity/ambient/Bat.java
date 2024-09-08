package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Bat extends AmbientCreature {
   public static final float FLAP_LENGTH_SECONDS = 0.5F;
   public static final float TICKS_PER_FLAP = 10.0F;
   private static final EntityDataAccessor<Byte> DATA_ID_FLAGS = SynchedEntityData.defineId(Bat.class, EntityDataSerializers.BYTE);
   private static final int FLAG_RESTING = 1;
   private static final TargetingConditions BAT_RESTING_TARGETING = TargetingConditions.forNonCombat().range(4.0);
   public final AnimationState flyAnimationState = new AnimationState();
   public final AnimationState restAnimationState = new AnimationState();
   @Nullable
   private BlockPos targetPosition;

   public Bat(EntityType<? extends Bat> var1, Level var2) {
      super(var1, var2);
      if (!var2.isClientSide) {
         this.setResting(true);
      }
   }

   @Override
   public boolean isFlapping() {
      return !this.isResting() && (float)this.tickCount % 10.0F == 0.0F;
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_FLAGS, (byte)0);
   }

   @Override
   protected float getSoundVolume() {
      return 0.1F;
   }

   @Override
   public float getVoicePitch() {
      return super.getVoicePitch() * 0.95F;
   }

   @Nullable
   @Override
   public SoundEvent getAmbientSound() {
      return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.BAT_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.BAT_DEATH;
   }

   @Override
   public boolean isPushable() {
      return false;
   }

   @Override
   protected void doPush(Entity var1) {
   }

   @Override
   protected void pushEntities() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0);
   }

   public boolean isResting() {
      return (this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
   }

   public void setResting(boolean var1) {
      byte var2 = this.entityData.get(DATA_ID_FLAGS);
      if (var1) {
         this.entityData.set(DATA_ID_FLAGS, (byte)(var2 | 1));
      } else {
         this.entityData.set(DATA_ID_FLAGS, (byte)(var2 & -2));
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.isResting()) {
         this.setDeltaMovement(Vec3.ZERO);
         this.setPosRaw(this.getX(), (double)Mth.floor(this.getY()) + 1.0 - (double)this.getBbHeight(), this.getZ());
      } else {
         this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.6, 1.0));
      }

      this.setupAnimationStates();
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      BlockPos var1 = this.blockPosition();
      BlockPos var2 = var1.above();
      if (this.isResting()) {
         boolean var3 = this.isSilent();
         if (this.level().getBlockState(var2).isRedstoneConductor(this.level(), var1)) {
            if (this.random.nextInt(200) == 0) {
               this.yHeadRot = (float)this.random.nextInt(360);
            }

            if (this.level().getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
               this.setResting(false);
               if (!var3) {
                  this.level().levelEvent(null, 1025, var1, 0);
               }
            }
         } else {
            this.setResting(false);
            if (!var3) {
               this.level().levelEvent(null, 1025, var1, 0);
            }
         }
      } else {
         if (this.targetPosition != null && (!this.level().isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= this.level().getMinY())) {
            this.targetPosition = null;
         }

         if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0)) {
            this.targetPosition = BlockPos.containing(
               this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7),
               this.getY() + (double)this.random.nextInt(6) - 2.0,
               this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7)
            );
         }

         double var13 = (double)this.targetPosition.getX() + 0.5 - this.getX();
         double var5 = (double)this.targetPosition.getY() + 0.1 - this.getY();
         double var7 = (double)this.targetPosition.getZ() + 0.5 - this.getZ();
         Vec3 var9 = this.getDeltaMovement();
         Vec3 var10 = var9.add(
            (Math.signum(var13) * 0.5 - var9.x) * 0.10000000149011612,
            (Math.signum(var5) * 0.699999988079071 - var9.y) * 0.10000000149011612,
            (Math.signum(var7) * 0.5 - var9.z) * 0.10000000149011612
         );
         this.setDeltaMovement(var10);
         float var11 = (float)(Mth.atan2(var10.z, var10.x) * 57.2957763671875) - 90.0F;
         float var12 = Mth.wrapDegrees(var11 - this.getYRot());
         this.zza = 0.5F;
         this.setYRot(this.getYRot() + var12);
         if (this.random.nextInt(100) == 0 && this.level().getBlockState(var2).isRedstoneConductor(this.level(), var2)) {
            this.setResting(true);
         }
      }
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   @Override
   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   @Override
   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   @Override
   public boolean hurt(DamageSource var1, float var2) {
      if (this.isInvulnerableTo(var1)) {
         return false;
      } else {
         if (!this.level().isClientSide && this.isResting()) {
            this.setResting(false);
         }

         return super.hurt(var1, var2);
      }
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.entityData.set(DATA_ID_FLAGS, var1.getByte("BatFlags"));
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putByte("BatFlags", this.entityData.get(DATA_ID_FLAGS));
   }

   public static boolean checkBatSpawnRules(EntityType<Bat> var0, LevelAccessor var1, EntitySpawnReason var2, BlockPos var3, RandomSource var4) {
      if (var3.getY() >= var1.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, var3).getY()) {
         return false;
      } else {
         int var5 = var1.getMaxLocalRawBrightness(var3);
         byte var6 = 4;
         if (isHalloween()) {
            var6 = 7;
         } else if (var4.nextBoolean()) {
            return false;
         }

         if (var5 > var4.nextInt(var6)) {
            return false;
         } else {
            return !var1.getBlockState(var3.below()).is(BlockTags.BATS_SPAWNABLE_ON) ? false : checkMobSpawnRules(var0, var1, var2, var3, var4);
         }
      }
   }

   private static boolean isHalloween() {
      LocalDate var0 = LocalDate.now();
      int var1 = var0.get(ChronoField.DAY_OF_MONTH);
      int var2 = var0.get(ChronoField.MONTH_OF_YEAR);
      return var2 == 10 && var1 >= 20 || var2 == 11 && var1 <= 3;
   }

   private void setupAnimationStates() {
      if (this.isResting()) {
         this.flyAnimationState.stop();
         this.restAnimationState.startIfStopped(this.tickCount);
      } else {
         this.restAnimationState.stop();
         this.flyAnimationState.startIfStopped(this.tickCount);
      }
   }
}
