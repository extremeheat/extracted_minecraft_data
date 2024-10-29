package net.minecraft.world.entity.ambient;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class Bat extends AmbientCreature {
   public static final float FLAP_LENGTH_SECONDS = 0.5F;
   public static final float TICKS_PER_FLAP = 10.0F;
   private static final EntityDataAccessor<Byte> DATA_ID_FLAGS;
   private static final int FLAG_RESTING = 1;
   private static final TargetingConditions BAT_RESTING_TARGETING;
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

   public boolean isFlapping() {
      return !this.isResting() && (float)this.tickCount % 10.0F == 0.0F;
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_ID_FLAGS, (byte)0);
   }

   protected float getSoundVolume() {
      return 0.1F;
   }

   public float getVoicePitch() {
      return super.getVoicePitch() * 0.95F;
   }

   @Nullable
   public SoundEvent getAmbientSound() {
      return this.isResting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.BAT_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.BAT_DEATH;
   }

   public boolean isPushable() {
      return false;
   }

   protected void doPush(Entity var1) {
   }

   protected void pushEntities() {
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 6.0);
   }

   public boolean isResting() {
      return ((Byte)this.entityData.get(DATA_ID_FLAGS) & 1) != 0;
   }

   public void setResting(boolean var1) {
      byte var2 = (Byte)this.entityData.get(DATA_ID_FLAGS);
      if (var1) {
         this.entityData.set(DATA_ID_FLAGS, (byte)(var2 | 1));
      } else {
         this.entityData.set(DATA_ID_FLAGS, (byte)(var2 & -2));
      }

   }

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

   protected void customServerAiStep(ServerLevel var1) {
      super.customServerAiStep(var1);
      BlockPos var2 = this.blockPosition();
      BlockPos var3 = var2.above();
      if (this.isResting()) {
         boolean var4 = this.isSilent();
         if (var1.getBlockState(var3).isRedstoneConductor(var1, var2)) {
            if (this.random.nextInt(200) == 0) {
               this.yHeadRot = (float)this.random.nextInt(360);
            }

            if (var1.getNearestPlayer(BAT_RESTING_TARGETING, this) != null) {
               this.setResting(false);
               if (!var4) {
                  var1.levelEvent((Player)null, 1025, var2, 0);
               }
            }
         } else {
            this.setResting(false);
            if (!var4) {
               var1.levelEvent((Player)null, 1025, var2, 0);
            }
         }
      } else {
         if (this.targetPosition != null && (!var1.isEmptyBlock(this.targetPosition) || this.targetPosition.getY() <= var1.getMinY())) {
            this.targetPosition = null;
         }

         if (this.targetPosition == null || this.random.nextInt(30) == 0 || this.targetPosition.closerToCenterThan(this.position(), 2.0)) {
            this.targetPosition = BlockPos.containing(this.getX() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7), this.getY() + (double)this.random.nextInt(6) - 2.0, this.getZ() + (double)this.random.nextInt(7) - (double)this.random.nextInt(7));
         }

         double var14 = (double)this.targetPosition.getX() + 0.5 - this.getX();
         double var6 = (double)this.targetPosition.getY() + 0.1 - this.getY();
         double var8 = (double)this.targetPosition.getZ() + 0.5 - this.getZ();
         Vec3 var10 = this.getDeltaMovement();
         Vec3 var11 = var10.add((Math.signum(var14) * 0.5 - var10.x) * 0.10000000149011612, (Math.signum(var6) * 0.699999988079071 - var10.y) * 0.10000000149011612, (Math.signum(var8) * 0.5 - var10.z) * 0.10000000149011612);
         this.setDeltaMovement(var11);
         float var12 = (float)(Mth.atan2(var11.z, var11.x) * 57.2957763671875) - 90.0F;
         float var13 = Mth.wrapDegrees(var12 - this.getYRot());
         this.zza = 0.5F;
         this.setYRot(this.getYRot() + var13);
         if (this.random.nextInt(100) == 0 && var1.getBlockState(var3).isRedstoneConductor(var1, var3)) {
            this.setResting(true);
         }
      }

   }

   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   protected void checkFallDamage(double var1, boolean var3, BlockState var4, BlockPos var5) {
   }

   public boolean isIgnoringBlockTriggers() {
      return true;
   }

   public boolean hurtServer(ServerLevel var1, DamageSource var2, float var3) {
      if (this.isInvulnerableTo(var1, var2)) {
         return false;
      } else {
         if (this.isResting()) {
            this.setResting(false);
         }

         return super.hurtServer(var1, var2, var3);
      }
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.entityData.set(DATA_ID_FLAGS, var1.getByte("BatFlags"));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putByte("BatFlags", (Byte)this.entityData.get(DATA_ID_FLAGS));
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

   static {
      DATA_ID_FLAGS = SynchedEntityData.defineId(Bat.class, EntityDataSerializers.BYTE);
      BAT_RESTING_TARGETING = TargetingConditions.forNonCombat().range(4.0);
   }
}
