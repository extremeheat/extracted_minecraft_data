package net.minecraft.world.entity.animal.fish;

import java.util.List;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class Pufferfish extends AbstractFish {
   private static final EntityDataAccessor<Integer> PUFF_STATE;
   private int inflateCounter;
   private int deflateTimer;
   private static final TargetingConditions.Selector SCARY_MOB;
   private static final TargetingConditions TARGETING_CONDITIONS;
   public static final int STATE_SMALL = 0;
   public static final int STATE_MID = 1;
   public static final int STATE_FULL = 2;
   private static final int DEFAULT_PUFF_STATE = 0;

   public Pufferfish(final EntityType<? extends Pufferfish> type, final Level level) {
      super(type, level);
      this.refreshDimensions();
   }

   protected void defineSynchedData(final SynchedEntityData.Builder entityData) {
      super.defineSynchedData(entityData);
      entityData.define(PUFF_STATE, 0);
   }

   public int getPuffState() {
      return (Integer)this.entityData.get(PUFF_STATE);
   }

   public void setPuffState(final int state) {
      this.entityData.set(PUFF_STATE, state);
   }

   public void onSyncedDataUpdated(final EntityDataAccessor<?> accessor) {
      if (PUFF_STATE.equals(accessor)) {
         this.refreshDimensions();
      }

      super.onSyncedDataUpdated(accessor);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putInt("PuffState", this.getPuffState());
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.setPuffState(Math.min(input.getIntOr("PuffState", 0), 2));
   }

   public ItemStack getBucketItemStack() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PufferfishPuffGoal(this));
   }

   public void tick() {
      if (!this.level().isClientSide() && this.isAlive() && this.isEffectiveAi()) {
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
      if (var2 instanceof ServerLevel level) {
         if (this.isAlive() && this.getPuffState() > 0) {
            for(Mob mob : this.level().getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(0.3), (target) -> TARGETING_CONDITIONS.test(level, this, target))) {
               if (mob.isAlive()) {
                  this.touch(level, mob);
               }
            }
         }
      }

   }

   private void touch(final ServerLevel level, final Mob mob) {
      int puffState = this.getPuffState();
      if (mob.hurtServer(level, this.damageSources().mobAttack(this), (float)(1 + puffState))) {
         mob.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * puffState, 0), this);
         this.playSound(SoundEvents.PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   public void playerTouch(final Player player) {
      int puffState = this.getPuffState();
      if (player instanceof ServerPlayer serverPlayer) {
         if (puffState > 0 && player.hurtServer(serverPlayer.level(), this.damageSources().mobAttack(this), (float)(1 + puffState))) {
            if (!this.isSilent()) {
               serverPlayer.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.PUFFER_FISH_STING, 0.0F));
            }

            player.addEffect(new MobEffectInstance(MobEffects.POISON, 60 * puffState, 0), this);
         }
      }

   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(final DamageSource source) {
      return SoundEvents.PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.PUFFER_FISH_FLOP;
   }

   public EntityDimensions getDefaultDimensions(final Pose pose) {
      return super.getDefaultDimensions(pose).scale(getScale(this.getPuffState()));
   }

   private static float getScale(final int state) {
      float var10000;
      switch (state) {
         case 0 -> var10000 = 0.5F;
         case 1 -> var10000 = 0.7F;
         default -> var10000 = 1.0F;
      }

      return var10000;
   }

   static {
      PUFF_STATE = SynchedEntityData.<Integer>defineId(Pufferfish.class, EntityDataSerializers.INT);
      SCARY_MOB = (target, level) -> {
         if (target instanceof Player player) {
            if (player.isCreative()) {
               return false;
            }
         }

         return !target.is(EntityTypeTags.NOT_SCARY_FOR_PUFFERFISH);
      };
      TARGETING_CONDITIONS = TargetingConditions.forNonCombat().ignoreInvisibilityTesting().ignoreLineOfSight().selector(SCARY_MOB);
   }

   private static class PufferfishPuffGoal extends Goal {
      private final Pufferfish fish;

      public PufferfishPuffGoal(final Pufferfish fish) {
         super();
         this.fish = fish;
      }

      public boolean canUse() {
         List<LivingEntity> entities = this.fish.level().getEntitiesOfClass(LivingEntity.class, this.fish.getBoundingBox().inflate(2.0), (target) -> Pufferfish.TARGETING_CONDITIONS.test(getServerLevel(this.fish), this.fish, target));
         return !entities.isEmpty();
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
