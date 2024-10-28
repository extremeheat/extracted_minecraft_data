package net.minecraft.world.entity.animal;

import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class Pufferfish extends AbstractFish {
   private static final EntityDataAccessor<Integer> PUFF_STATE;
   int inflateCounter;
   int deflateTimer;
   private static final TargetingConditions.Selector SCARY_MOB;
   static final TargetingConditions TARGETING_CONDITIONS;
   public static final int STATE_SMALL = 0;
   public static final int STATE_MID = 1;
   public static final int STATE_FULL = 2;

   public Pufferfish(EntityType<? extends Pufferfish> var1, Level var2) {
      super(var1, var2);
      this.refreshDimensions();
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(PUFF_STATE, 0);
   }

   public int getPuffState() {
      return (Integer)this.entityData.get(PUFF_STATE);
   }

   public void setPuffState(int var1) {
      this.entityData.set(PUFF_STATE, var1);
   }

   public void onSyncedDataUpdated(EntityDataAccessor<?> var1) {
      if (PUFF_STATE.equals(var1)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(var1);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("PuffState", this.getPuffState());
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setPuffState(Math.min(var1.getInt("PuffState"), 2));
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PufferfishPuffGoal(this));
   }

   public void tick() {
      if (!this.level().isClientSide && this.isAlive() && this.isEffectiveAi()) {
         if (this.inflateCounter > 0) {
            if (this.getPuffState() == 0) {
               this.makeSound(SoundEvents.PUFFER_FISH_BLOW_UP);
               this.setPuffState(1);
            } else if (this.inflateCounter > 40 && this.getPuffState() == 1) {
               this.makeSound(SoundEvents.PUFFER_FISH_BLOW_UP);
               this.setPuffState(2);
            }

            ++this.inflateCounter;
         } else if (this.getPuffState() != 0) {
            if (this.deflateTimer > 60 && this.getPuffState() == 2) {
               this.makeSound(SoundEvents.PUFFER_FISH_BLOW_OUT);
               this.setPuffState(1);
            } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
               this.makeSound(SoundEvents.PUFFER_FISH_BLOW_OUT);
               this.setPuffState(0);
            }

            ++this.deflateTimer;
         }
      }

      super.tick();
   }

   public void aiStep() {
      super.aiStep();
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1) {
         if (this.isAlive() && this.getPuffState() > 0) {
            List var5 = this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(0.3), (var2x) -> {
               return TARGETING_CONDITIONS.test(var1, this, var2x);
            });
            Iterator var3 = var5.iterator();

            while(var3.hasNext()) {
               Mob var4 = (Mob)var3.next();
               if (var4.isAlive()) {
                  this.touch(var1, var4);
               }
            }
         }
      }

   }

   private void touch(ServerLevel var1, Mob var2) {
      int var3 = this.getPuffState();
      if (var2.hurtServer(var1, this.damageSources().mobAttack(this), (float)(1 + var3))) {
         var2.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * var3, 0), this);
         this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   public void playerTouch(Player var1) {
      int var2 = this.getPuffState();
      if (var1 instanceof ServerPlayer var3) {
         if (var2 > 0 && var1.hurtServer(var3.serverLevel(), this.damageSources().mobAttack(this), (float)(1 + var2))) {
            if (!this.isSilent()) {
               var3.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0F));
            }

            var1.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * var2, 0), this);
         }
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.PUFFER_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.PUFFER_FISH_FLOP;
   }

   public EntityDimensions getDefaultDimensions(Pose var1) {
      return super.getDefaultDimensions(var1).scale(getScale(this.getPuffState()));
   }

   private static float getScale(int var0) {
      switch (var0) {
         case 0 -> {
            return 0.5F;
         }
         case 1 -> {
            return 0.7F;
         }
         default -> {
            return 1.0F;
         }
      }
   }

   static {
      PUFF_STATE = SynchedEntityData.defineId(Pufferfish.class, EntityDataSerializers.INT);
      SCARY_MOB = (var0, var1) -> {
         if (var0 instanceof Player var2) {
            if (var2.isCreative()) {
               return false;
            }
         }

         return !var0.getType().is(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH);
      };
      TARGETING_CONDITIONS = TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight().selector(SCARY_MOB);
   }

   private static class PufferfishPuffGoal extends Goal {
      private final Pufferfish fish;

      public PufferfishPuffGoal(Pufferfish var1) {
         super();
         this.fish = var1;
      }

      public boolean canUse() {
         List var1 = this.fish.level().getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0), (var1x) -> {
            return Pufferfish.TARGETING_CONDITIONS.test(getServerLevel(this.fish), this.fish, var1x);
         });
         return !var1.isEmpty();
      }

      public void start() {
         this.fish.inflateCounter = 1;
         this.fish.deflateTimer = 0;
      }

      public void stop() {
         this.fish.inflateCounter = 0;
      }
   }
}
