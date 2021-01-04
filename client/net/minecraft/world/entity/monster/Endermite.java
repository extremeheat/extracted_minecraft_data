package net.minecraft.world.entity.monster;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class Endermite extends Monster {
   private int life;
   private boolean playerSpawned;

   public Endermite(EntityType<? extends Endermite> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 3;
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   protected float getStandingEyeHeight(Pose var1, EntityDimensions var2) {
      return 0.1F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   protected boolean makeStepSound() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDERMITE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMITE_DEATH;
   }

   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.ENDERMITE_STEP, 0.15F, 1.0F);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.life = var1.getInt("Lifetime");
      this.playerSpawned = var1.getBoolean("PlayerSpawned");
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Lifetime", this.life);
      var1.putBoolean("PlayerSpawned", this.playerSpawned);
   }

   public void tick() {
      this.yBodyRot = this.yRot;
      super.tick();
   }

   public void setYBodyRot(float var1) {
      this.yRot = var1;
      super.setYBodyRot(var1);
   }

   public double getRidingHeight() {
      return 0.1D;
   }

   public boolean isPlayerSpawned() {
      return this.playerSpawned;
   }

   public void setPlayerSpawned(boolean var1) {
      this.playerSpawned = var1;
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         for(int var1 = 0; var1 < 2; ++var1) {
            this.level.addParticle(ParticleTypes.PORTAL, this.x + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), this.y + this.random.nextDouble() * (double)this.getBbHeight(), this.z + (this.random.nextDouble() - 0.5D) * (double)this.getBbWidth(), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
         }
      } else {
         if (!this.isPersistenceRequired()) {
            ++this.life;
         }

         if (this.life >= 2400) {
            this.remove();
         }
      }

   }

   public static boolean checkEndermiteSpawnRules(EntityType<Endermite> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, Random var4) {
      if (checkAnyLightMonsterSpawnRules(var0, var1, var2, var3, var4)) {
         Player var5 = var1.getNearestPlayer((double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D, 5.0D, true);
         return var5 == null;
      } else {
         return false;
      }
   }

   public MobType getMobType() {
      return MobType.ARTHROPOD;
   }
}
