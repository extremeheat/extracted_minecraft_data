package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Illusioner extends SpellcasterIllager implements RangedAttackMob {
   private int clientSideIllusionTicks;
   private final Vec3[][] clientSideIllusionOffsets;

   public Illusioner(EntityType var1, Level var2) {
      super(var1, var2);
      this.xpReward = 5;
      this.clientSideIllusionOffsets = new Vec3[2][4];

      for(int var3 = 0; var3 < 4; ++var3) {
         this.clientSideIllusionOffsets[0][var3] = Vec3.ZERO;
         this.clientSideIllusionOffsets[1][var3] = Vec3.ZERO;
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.goalSelector.addGoal(1, new SpellcasterIllager.SpellcasterCastingSpellGoal());
      this.goalSelector.addGoal(4, new Illusioner.IllusionerMirrorSpellGoal());
      this.goalSelector.addGoal(5, new Illusioner.IllusionerBlindnessSpellGoal());
      this.goalSelector.addGoal(6, new RangedBowAttackGoal(this, 0.5D, 20, 15.0F));
      this.goalSelector.addGoal(8, new RandomStrollGoal(this, 0.6D));
      this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
      this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
      this.targetSelector.addGoal(1, (new HurtByTargetGoal(this, new Class[]{Raider.class})).setAlertOthers());
      this.targetSelector.addGoal(2, (new NearestAttackableTargetGoal(this, Player.class, true)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal(this, AbstractVillager.class, false)).setUnseenMemoryTicks(300));
      this.targetSelector.addGoal(3, (new NearestAttackableTargetGoal(this, IronGolem.class, false)).setUnseenMemoryTicks(300));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
      this.getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(18.0D);
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(32.0D);
   }

   public SpawnGroupData finalizeSpawn(LevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
      return super.finalizeSpawn(var1, var2, var3, var4, var5);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
   }

   public AABB getBoundingBoxForCulling() {
      return this.getBoundingBox().inflate(3.0D, 0.0D, 3.0D);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && this.isInvisible()) {
         --this.clientSideIllusionTicks;
         if (this.clientSideIllusionTicks < 0) {
            this.clientSideIllusionTicks = 0;
         }

         if (this.hurtTime != 1 && this.tickCount % 1200 != 0) {
            if (this.hurtTime == this.hurtDuration - 1) {
               this.clientSideIllusionTicks = 3;

               for(int var4 = 0; var4 < 4; ++var4) {
                  this.clientSideIllusionOffsets[0][var4] = this.clientSideIllusionOffsets[1][var4];
                  this.clientSideIllusionOffsets[1][var4] = new Vec3(0.0D, 0.0D, 0.0D);
               }
            }
         } else {
            this.clientSideIllusionTicks = 3;
            float var1 = -6.0F;
            boolean var2 = true;

            int var3;
            for(var3 = 0; var3 < 4; ++var3) {
               this.clientSideIllusionOffsets[0][var3] = this.clientSideIllusionOffsets[1][var3];
               this.clientSideIllusionOffsets[1][var3] = new Vec3((double)(-6.0F + (float)this.random.nextInt(13)) * 0.5D, (double)Math.max(0, this.random.nextInt(6) - 4), (double)(-6.0F + (float)this.random.nextInt(13)) * 0.5D);
            }

            for(var3 = 0; var3 < 16; ++var3) {
               this.level.addParticle(ParticleTypes.CLOUD, this.getRandomX(0.5D), this.getRandomY(), this.getZ(0.5D), 0.0D, 0.0D, 0.0D);
            }

            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.ILLUSIONER_MIRROR_MOVE, this.getSoundSource(), 1.0F, 1.0F, false);
         }
      }

   }

   public SoundEvent getCelebrateSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   public Vec3[] getIllusionOffsets(float var1) {
      if (this.clientSideIllusionTicks <= 0) {
         return this.clientSideIllusionOffsets[1];
      } else {
         double var2 = (double)(((float)this.clientSideIllusionTicks - var1) / 3.0F);
         var2 = Math.pow(var2, 0.25D);
         Vec3[] var4 = new Vec3[4];

         for(int var5 = 0; var5 < 4; ++var5) {
            var4[var5] = this.clientSideIllusionOffsets[1][var5].scale(1.0D - var2).add(this.clientSideIllusionOffsets[0][var5].scale(var2));
         }

         return var4;
      }
   }

   public boolean isAlliedTo(Entity var1) {
      if (super.isAlliedTo(var1)) {
         return true;
      } else if (var1 instanceof LivingEntity && ((LivingEntity)var1).getMobType() == MobType.ILLAGER) {
         return this.getTeam() == null && var1.getTeam() == null;
      } else {
         return false;
      }
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ILLUSIONER_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ILLUSIONER_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.ILLUSIONER_HURT;
   }

   protected SoundEvent getCastingSoundEvent() {
      return SoundEvents.ILLUSIONER_CAST_SPELL;
   }

   public void applyRaidBuffs(int var1, boolean var2) {
   }

   public void performRangedAttack(LivingEntity var1, float var2) {
      ItemStack var3 = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW)));
      AbstractArrow var4 = ProjectileUtil.getMobArrow(this, var3, var2);
      double var5 = var1.getX() - this.getX();
      double var7 = var1.getY(0.3333333333333333D) - var4.getY();
      double var9 = var1.getZ() - this.getZ();
      double var11 = (double)Mth.sqrt(var5 * var5 + var9 * var9);
      var4.shoot(var5, var7 + var11 * 0.20000000298023224D, var9, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
      this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
      this.level.addFreshEntity(var4);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllager.IllagerArmPose.SPELLCASTING;
      } else {
         return this.isAggressive() ? AbstractIllager.IllagerArmPose.BOW_AND_ARROW : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   class IllusionerBlindnessSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private int lastTargetId;

      private IllusionerBlindnessSpellGoal() {
         super();
      }

      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else if (Illusioner.this.getTarget() == null) {
            return false;
         } else if (Illusioner.this.getTarget().getId() == this.lastTargetId) {
            return false;
         } else {
            return Illusioner.this.level.getCurrentDifficultyAt(new BlockPos(Illusioner.this)).isHarderThan((float)Difficulty.NORMAL.ordinal());
         }
      }

      public void start() {
         super.start();
         this.lastTargetId = Illusioner.this.getTarget().getId();
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 180;
      }

      protected void performSpellCasting() {
         Illusioner.this.getTarget().addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400));
      }

      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_BLINDNESS;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.BLINDNESS;
      }

      // $FF: synthetic method
      IllusionerBlindnessSpellGoal(Object var2) {
         this();
      }
   }

   class IllusionerMirrorSpellGoal extends SpellcasterIllager.SpellcasterUseSpellGoal {
      private IllusionerMirrorSpellGoal() {
         super();
      }

      public boolean canUse() {
         if (!super.canUse()) {
            return false;
         } else {
            return !Illusioner.this.hasEffect(MobEffects.INVISIBILITY);
         }
      }

      protected int getCastingTime() {
         return 20;
      }

      protected int getCastingInterval() {
         return 340;
      }

      protected void performSpellCasting() {
         Illusioner.this.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200));
      }

      @Nullable
      protected SoundEvent getSpellPrepareSound() {
         return SoundEvents.ILLUSIONER_PREPARE_MIRROR;
      }

      protected SpellcasterIllager.IllagerSpell getSpell() {
         return SpellcasterIllager.IllagerSpell.DISAPPEAR;
      }

      // $FF: synthetic method
      IllusionerMirrorSpellGoal(Object var2) {
         this();
      }
   }
}
