package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

public abstract class SpellcasterIllager extends AbstractIllager {
   private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
   protected int spellCastingTickCount;
   private SpellcasterIllager.IllagerSpell currentSpell = SpellcasterIllager.IllagerSpell.NONE;

   protected SpellcasterIllager(EntityType<? extends SpellcasterIllager> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_SPELL_CASTING_ID, (byte)0);
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.spellCastingTickCount = var1.getInt("SpellTicks");
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("SpellTicks", this.spellCastingTickCount);
   }

   @Override
   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllager.IllagerArmPose.SPELLCASTING;
      } else {
         return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   public boolean isCastingSpell() {
      if (this.level().isClientSide) {
         return this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
      } else {
         return this.spellCastingTickCount > 0;
      }
   }

   public void setIsCastingSpell(SpellcasterIllager.IllagerSpell var1) {
      this.currentSpell = var1;
      this.entityData.set(DATA_SPELL_CASTING_ID, (byte)var1.id);
   }

   protected SpellcasterIllager.IllagerSpell getCurrentSpell() {
      return !this.level().isClientSide ? this.currentSpell : SpellcasterIllager.IllagerSpell.byId(this.entityData.get(DATA_SPELL_CASTING_ID));
   }

   @Override
   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.spellCastingTickCount > 0) {
         --this.spellCastingTickCount;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (this.level().isClientSide && this.isCastingSpell()) {
         SpellcasterIllager.IllagerSpell var1 = this.getCurrentSpell();
         float var2 = (float)var1.spellColor[0];
         float var3 = (float)var1.spellColor[1];
         float var4 = (float)var1.spellColor[2];
         float var5 = this.yBodyRot * 0.017453292F + Mth.cos((float)this.tickCount * 0.6662F) * 0.25F;
         float var6 = Mth.cos(var5);
         float var7 = Mth.sin(var5);
         double var8 = 0.6 * (double)this.getScale();
         double var10 = 1.8 * (double)this.getScale();
         this.level()
            .addParticle(
               ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, var2, var3, var4),
               this.getX() + (double)var6 * var8,
               this.getY() + var10,
               this.getZ() + (double)var7 * var8,
               0.0,
               0.0,
               0.0
            );
         this.level()
            .addParticle(
               ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, var2, var3, var4),
               this.getX() - (double)var6 * var8,
               this.getY() + var10,
               this.getZ() - (double)var7 * var8,
               0.0,
               0.0,
               0.0
            );
      }
   }

   protected int getSpellCastingTime() {
      return this.spellCastingTickCount;
   }

   protected abstract SoundEvent getCastingSoundEvent();

   protected static enum IllagerSpell {
      NONE(0, 0.0, 0.0, 0.0),
      SUMMON_VEX(1, 0.7, 0.7, 0.8),
      FANGS(2, 0.4, 0.3, 0.35),
      WOLOLO(3, 0.7, 0.5, 0.2),
      DISAPPEAR(4, 0.3, 0.3, 0.8),
      BLINDNESS(5, 0.1, 0.1, 0.2);

      private static final IntFunction<SpellcasterIllager.IllagerSpell> BY_ID = ByIdMap.continuous(var0 -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      final int id;
      final double[] spellColor;

      private IllagerSpell(int var3, double var4, double var6, double var8) {
         this.id = var3;
         this.spellColor = new double[]{var4, var6, var8};
      }

      public static SpellcasterIllager.IllagerSpell byId(int var0) {
         return BY_ID.apply(var0);
      }
   }

   protected class SpellcasterCastingSpellGoal extends Goal {
      public SpellcasterCastingSpellGoal() {
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      @Override
      public boolean canUse() {
         return SpellcasterIllager.this.getSpellCastingTime() > 0;
      }

      @Override
      public void start() {
         super.start();
         SpellcasterIllager.this.navigation.stop();
      }

      @Override
      public void stop() {
         super.stop();
         SpellcasterIllager.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
      }

      @Override
      public void tick() {
         if (SpellcasterIllager.this.getTarget() != null) {
            SpellcasterIllager.this.getLookControl()
               .setLookAt(SpellcasterIllager.this.getTarget(), (float)SpellcasterIllager.this.getMaxHeadYRot(), (float)SpellcasterIllager.this.getMaxHeadXRot());
         }
      }
   }

   protected abstract class SpellcasterUseSpellGoal extends Goal {
      protected int attackWarmupDelay;
      protected int nextAttackTickCount;

      protected SpellcasterUseSpellGoal() {
         super();
      }

      @Override
      public boolean canUse() {
         LivingEntity var1 = SpellcasterIllager.this.getTarget();
         if (var1 == null || !var1.isAlive()) {
            return false;
         } else if (SpellcasterIllager.this.isCastingSpell()) {
            return false;
         } else {
            return SpellcasterIllager.this.tickCount >= this.nextAttackTickCount;
         }
      }

      @Override
      public boolean canContinueToUse() {
         LivingEntity var1 = SpellcasterIllager.this.getTarget();
         return var1 != null && var1.isAlive() && this.attackWarmupDelay > 0;
      }

      @Override
      public void start() {
         this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
         SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
         this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
         SoundEvent var1 = this.getSpellPrepareSound();
         if (var1 != null) {
            SpellcasterIllager.this.playSound(var1, 1.0F, 1.0F);
         }

         SpellcasterIllager.this.setIsCastingSpell(this.getSpell());
      }

      @Override
      public void tick() {
         --this.attackWarmupDelay;
         if (this.attackWarmupDelay == 0) {
            this.performSpellCasting();
            SpellcasterIllager.this.playSound(SpellcasterIllager.this.getCastingSoundEvent(), 1.0F, 1.0F);
         }
      }

      protected abstract void performSpellCasting();

      protected int getCastWarmupTime() {
         return 20;
      }

      protected abstract int getCastingTime();

      protected abstract int getCastingInterval();

      @Nullable
      protected abstract SoundEvent getSpellPrepareSound();

      protected abstract SpellcasterIllager.IllagerSpell getSpell();
   }
}
