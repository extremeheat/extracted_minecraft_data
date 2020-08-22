package net.minecraft.world.entity.monster;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;

public abstract class SpellcasterIllager extends AbstractIllager {
   private static final EntityDataAccessor DATA_SPELL_CASTING_ID;
   protected int spellCastingTickCount;
   private SpellcasterIllager.IllagerSpell currentSpell;

   protected SpellcasterIllager(EntityType var1, Level var2) {
      super(var1, var2);
      this.currentSpell = SpellcasterIllager.IllagerSpell.NONE;
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_SPELL_CASTING_ID, (byte)0);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.spellCastingTickCount = var1.getInt("SpellTicks");
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("SpellTicks", this.spellCastingTickCount);
   }

   public AbstractIllager.IllagerArmPose getArmPose() {
      if (this.isCastingSpell()) {
         return AbstractIllager.IllagerArmPose.SPELLCASTING;
      } else {
         return this.isCelebrating() ? AbstractIllager.IllagerArmPose.CELEBRATING : AbstractIllager.IllagerArmPose.CROSSED;
      }
   }

   public boolean isCastingSpell() {
      if (this.level.isClientSide) {
         return (Byte)this.entityData.get(DATA_SPELL_CASTING_ID) > 0;
      } else {
         return this.spellCastingTickCount > 0;
      }
   }

   public void setIsCastingSpell(SpellcasterIllager.IllagerSpell var1) {
      this.currentSpell = var1;
      this.entityData.set(DATA_SPELL_CASTING_ID, (byte)var1.id);
   }

   protected SpellcasterIllager.IllagerSpell getCurrentSpell() {
      return !this.level.isClientSide ? this.currentSpell : SpellcasterIllager.IllagerSpell.byId((Byte)this.entityData.get(DATA_SPELL_CASTING_ID));
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.spellCastingTickCount > 0) {
         --this.spellCastingTickCount;
      }

   }

   public void tick() {
      super.tick();
      if (this.level.isClientSide && this.isCastingSpell()) {
         SpellcasterIllager.IllagerSpell var1 = this.getCurrentSpell();
         double var2 = var1.spellColor[0];
         double var4 = var1.spellColor[1];
         double var6 = var1.spellColor[2];
         float var8 = this.yBodyRot * 0.017453292F + Mth.cos((float)this.tickCount * 0.6662F) * 0.25F;
         float var9 = Mth.cos(var8);
         float var10 = Mth.sin(var8);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() + (double)var9 * 0.6D, this.getY() + 1.8D, this.getZ() + (double)var10 * 0.6D, var2, var4, var6);
         this.level.addParticle(ParticleTypes.ENTITY_EFFECT, this.getX() - (double)var9 * 0.6D, this.getY() + 1.8D, this.getZ() - (double)var10 * 0.6D, var2, var4, var6);
      }

   }

   protected int getSpellCastingTime() {
      return this.spellCastingTickCount;
   }

   protected abstract SoundEvent getCastingSoundEvent();

   static {
      DATA_SPELL_CASTING_ID = SynchedEntityData.defineId(SpellcasterIllager.class, EntityDataSerializers.BYTE);
   }

   public static enum IllagerSpell {
      NONE(0, 0.0D, 0.0D, 0.0D),
      SUMMON_VEX(1, 0.7D, 0.7D, 0.8D),
      FANGS(2, 0.4D, 0.3D, 0.35D),
      WOLOLO(3, 0.7D, 0.5D, 0.2D),
      DISAPPEAR(4, 0.3D, 0.3D, 0.8D),
      BLINDNESS(5, 0.1D, 0.1D, 0.2D);

      private final int id;
      private final double[] spellColor;

      private IllagerSpell(int var3, double var4, double var6, double var8) {
         this.id = var3;
         this.spellColor = new double[]{var4, var6, var8};
      }

      public static SpellcasterIllager.IllagerSpell byId(int var0) {
         SpellcasterIllager.IllagerSpell[] var1 = values();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            SpellcasterIllager.IllagerSpell var4 = var1[var3];
            if (var0 == var4.id) {
               return var4;
            }
         }

         return NONE;
      }
   }

   public abstract class SpellcasterUseSpellGoal extends Goal {
      protected int attackWarmupDelay;
      protected int nextAttackTickCount;

      protected SpellcasterUseSpellGoal() {
      }

      public boolean canUse() {
         LivingEntity var1 = SpellcasterIllager.this.getTarget();
         if (var1 != null && var1.isAlive()) {
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
         LivingEntity var1 = SpellcasterIllager.this.getTarget();
         return var1 != null && var1.isAlive() && this.attackWarmupDelay > 0;
      }

      public void start() {
         this.attackWarmupDelay = this.getCastWarmupTime();
         SpellcasterIllager.this.spellCastingTickCount = this.getCastingTime();
         this.nextAttackTickCount = SpellcasterIllager.this.tickCount + this.getCastingInterval();
         SoundEvent var1 = this.getSpellPrepareSound();
         if (var1 != null) {
            SpellcasterIllager.this.playSound(var1, 1.0F, 1.0F);
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

      @Nullable
      protected abstract SoundEvent getSpellPrepareSound();

      protected abstract SpellcasterIllager.IllagerSpell getSpell();
   }

   public class SpellcasterCastingSpellGoal extends Goal {
      public SpellcasterCastingSpellGoal() {
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
}
