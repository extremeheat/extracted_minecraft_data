package net.minecraft.world.entity.monster.illager;

import java.util.EnumSet;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public abstract class SpellcasterIllager extends AbstractIllager {
   private static final EntityDataAccessor<Byte> DATA_SPELL_CASTING_ID;
   private static final int DEFAULT_SPELLCASTING_TICKS = 0;
   protected int spellCastingTickCount = 0;
   private IllagerSpell currentSpell;

   protected SpellcasterIllager(final EntityType<? extends SpellcasterIllager> type, final Level level) {
      super(type, level);
      this.currentSpell = SpellcasterIllager.IllagerSpell.NONE;
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.spellCastingTickCount = input.getIntOr("SpellTicks", 0);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("SpellTicks", this.spellCastingTickCount);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllager.IllagerArmPose.SPELLCASTING;
      } else {
         return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   public boolean isCastingSpell() {
      if (this.level().isClientSide()) {
         return (Byte)this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
      } else {
         return this.spellCastingTickCount > 0;
      }
   }

   public void setIsCastingSpell(final IllagerSpell spell) {
      this.currentSpell = spell;
      this.entityData.set(DATA_SPELL_CASTING_ID, (byte)spell.id);
   }

   protected IllagerSpell getCurrentSpell() {
      return !this.level().isClientSide() ? this.currentSpell : SpellcasterIllager.IllagerSpell.byId((Byte)this.entityData.get(DATA_SPELL_CASTING_ID));
   }

   protected void customServerAiStep(final ServerLevel level) {
      super.customServerAiStep(level);
      if (this.spellCastingTickCount > 0) {
         --this.spellCastingTickCount;
      }

   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide() && this.isCastingSpell()) {
         IllagerSpell spell = this.getCurrentSpell();
         float red = (float)spell.spellColor[0];
         float green = (float)spell.spellColor[1];
         float blue = (float)spell.spellColor[2];
         float bodyAngle = this.yBodyRot * 0.017453292F + Mth.cos((double)((float)this.tickCount * 0.6662F)) * 0.25F;
         float cos = Mth.cos((double)bodyAngle);
         float sin = Mth.sin((double)bodyAngle);
         double handDistance = 0.6 * (double)this.getScale();
         double handHeight = 1.8 * (double)this.getScale();
         this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, red, green, blue), this.getX() + (double)cos * handDistance, this.getY() + handHeight, this.getZ() + (double)sin * handDistance, 0.0, 0.0, 0.0);
         this.level().addParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, red, green, blue), this.getX() - (double)cos * handDistance, this.getY() + handHeight, this.getZ() - (double)sin * handDistance, 0.0, 0.0, 0.0);
      }

   }

   protected int getSpellCastingTime() {
      return this.spellCastingTickCount;
   }

   protected abstract SoundEvent getCastingSoundEvent();

   static {
      DATA_SPELL_CASTING_ID = SynchedEntityData.<Byte>defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
   }

   protected class SpellcasterCastingSpellGoal extends Goal {
      public SpellcasterCastingSpellGoal() {
         Objects.requireNonNull(SpellcasterIllager.this);
         super();
         this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
      }

      public boolean canUse() {
         return SpellcasterIllager.this.getSpellCastingTime() > 0;
      }

      public void start() {
         super.start();
         SpellcasterIllager.this.navigation.stop();
      }

      public void stop() {
         super.stop();
         SpellcasterIllager.this.setIsCastingSpell(SpellcasterIllager.IllagerSpell.NONE);
      }

      public void tick() {
         if (SpellcasterIllager.this.getTarget() != null) {
            SpellcasterIllager.this.getLookControl().setLookAt(SpellcasterIllager.this.getTarget(), (float)SpellcasterIllager.this.getMaxHeadYRot(), (float)SpellcasterIllager.this.getMaxHeadXRot());
         }

      }
   }

   protected abstract class SpellcasterUseSpellGoal extends Goal {
      protected int attackWarmupDelay;
      protected int nextAttackTickCount;

      protected SpellcasterUseSpellGoal() {
         Objects.requireNonNull(SpellcasterIllager.this);
         super();
      }

      public boolean canUse() {
         Entity target = SpellcasterIllager.this.getTarget();
         if (target != null && target.isAlive()) {
            if (SpellcasterIllager.this.isCastingSpell()) {
               return false;
            } else {
               return SpellcasterIllager.this.tickCount >= this.nextAttackTickCount;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         Entity target = SpellcasterIllager.this.getTarget();
         return target != null && target.isAlive() && this.attackWarmupDelay > 0;
      }

      public void start() {
         this.attackWarmupDelay = this.adjustedTickDelay(this.getCastWarmupTime());
         SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
         this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
         SoundEvent spellPrepareSound = this.getSpellPrepareSound();
         if (spellPrepareSound != null) {
            SpellcasterIllager.this.playSound(spellPrepareSound, 1.0F, 1.0F);
         }

         SpellcasterIllager.this.setIsCastingSpell(this.getSpell());
      }

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

      protected abstract @Nullable SoundEvent getSpellPrepareSound();

      protected abstract IllagerSpell getSpell();
   }

   protected static enum IllagerSpell {
      NONE(0, 0.0, 0.0, 0.0),
      SUMMON_VEX(1, 0.7, 0.7, 0.8),
      FANGS(2, 0.4, 0.3, 0.35),
      WOLOLO(3, 0.7, 0.5, 0.2),
      DISAPPEAR(4, 0.3, 0.3, 0.8),
      BLINDNESS(5, 0.1, 0.1, 0.2);

      private static final IntFunction<IllagerSpell> BY_ID = ByIdMap.<IllagerSpell>continuous((e) -> e.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
      private final int id;
      private final double[] spellColor;

      private IllagerSpell(final int id, final double red, final double green, final double blue) {
         this.id = id;
         this.spellColor = new double[]{red, green, blue};
      }

      public static IllagerSpell byId(final int id) {
         return (IllagerSpell)BY_ID.apply(id);
      }

      // $FF: synthetic method
      private static IllagerSpell[] $values() {
         return new IllagerSpell[]{NONE, SUMMON_VEX, FANGS, WOLOLO, DISAPPEAR, BLINDNESS};
      }
   }
}
