package net.minecraft.world.entity.monster;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.ClimbOnTopOfPowderSnowGoal;
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
   private static final int MAX_LIFE = 2400;
   private int life;

   public Endermite(EntityType<? extends Endermite> var1, Level var2) {
      super(var1, var2);
      this.xpReward = 3;
   }

   @Override
   protected void registerGoals() {
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(1, new ClimbOnTopOfPowderSnowGoal(this, this.level()));
      this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0, false));
      this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
   }

   public static AttributeSupplier.Builder createAttributes() {
      return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0).add(Attributes.MOVEMENT_SPEED, 0.25).add(Attributes.ATTACK_DAMAGE, 2.0);
   }

   @Override
   protected Entity.MovementEmission getMovementEmission() {
      return Entity.MovementEmission.EVENTS;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENDERMITE_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ENDERMITE_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENDERMITE_DEATH;
   }

   @Override
   protected void playStepSound(BlockPos var1, BlockState var2) {
      this.playSound(SoundEvents.ENDERMITE_STEP, 0.15F, 1.0F);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.life = var1.getInt("Lifetime");
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Lifetime", this.life);
   }

   @Override
   public void tick() {
      this.yBodyRot = this.getYRot();
      super.tick();
   }

   @Override
   public void setYBodyRot(float var1) {
      this.setYRot(var1);
      super.setYBodyRot(var1);
   }

   @Override
   public void aiStep() {
      super.aiStep();
      if (this.level().isClientSide) {
         for (int var1 = 0; var1 < 2; var1++) {
            this.level()
               .addParticle(
                  ParticleTypes.PORTAL,
                  this.getRandomX(0.5),
                  this.getRandomY(),
                  this.getRandomZ(0.5),
                  (this.random.nextDouble() - 0.5) * 2.0,
                  -this.random.nextDouble(),
                  (this.random.nextDouble() - 0.5) * 2.0
               );
         }
      } else {
         if (!this.isPersistenceRequired()) {
            this.life++;
         }

         if (this.life >= 2400) {
            this.discard();
         }
      }
   }

   public static boolean checkEndermiteSpawnRules(EntityType<Endermite> var0, LevelAccessor var1, MobSpawnType var2, BlockPos var3, RandomSource var4) {
      if (checkAnyLightMonsterSpawnRules(var0, var1, var2, var3, var4)) {
         Player var5 = var1.getNearestPlayer((double)var3.getX() + 0.5, (double)var3.getY() + 0.5, (double)var3.getZ() + 0.5, 5.0, true);
         return var5 == null;
      } else {
         return false;
      }
   }
}
