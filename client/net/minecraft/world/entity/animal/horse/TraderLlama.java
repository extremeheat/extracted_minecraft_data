package net.minecraft.world.entity.animal.horse;

import java.util.EnumSet;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class TraderLlama extends Llama {
   private int despawnDelay = 47999;

   public TraderLlama(EntityType<? extends TraderLlama> var1, Level var2) {
      super(var1, var2);
   }

   public boolean isTraderLlama() {
      return true;
   }

   protected Llama makeBabyLlama() {
      return (Llama)EntityType.TRADER_LLAMA.create(this.level);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("DespawnDelay", this.despawnDelay);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("DespawnDelay", 99)) {
         this.despawnDelay = var1.getInt("DespawnDelay");
      }

   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new PanicGoal(this, 2.0D));
      this.targetSelector.addGoal(1, new TraderLlama.TraderLlamaDefendWanderingTraderGoal(this));
   }

   public void setDespawnDelay(int var1) {
      this.despawnDelay = var1;
   }

   protected void doPlayerRide(Player var1) {
      Entity var2 = this.getLeashHolder();
      if (!(var2 instanceof WanderingTrader)) {
         super.doPlayerRide(var1);
      }
   }

   public void aiStep() {
      super.aiStep();
      if (!this.level.isClientSide) {
         this.maybeDespawn();
      }

   }

   private void maybeDespawn() {
      if (this.canDespawn()) {
         this.despawnDelay = this.isLeashedToWanderingTrader() ? ((WanderingTrader)this.getLeashHolder()).getDespawnDelay() - 1 : this.despawnDelay - 1;
         if (this.despawnDelay <= 0) {
            this.dropLeash(true, false);
            this.discard();
         }

      }
   }

   private boolean canDespawn() {
      return !this.isTamed() && !this.isLeashedToSomethingOtherThanTheWanderingTrader() && !this.hasExactlyOnePlayerPassenger();
   }

   private boolean isLeashedToWanderingTrader() {
      return this.getLeashHolder() instanceof WanderingTrader;
   }

   private boolean isLeashedToSomethingOtherThanTheWanderingTrader() {
      return this.isLeashed() && !this.isLeashedToWanderingTrader();
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      if (var3 == MobSpawnType.EVENT) {
         this.setAge(0);
      }

      if (var4 == null) {
         var4 = new AgeableMob.AgeableMobGroupData(false);
      }

      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
   }

   protected static class TraderLlamaDefendWanderingTraderGoal extends TargetGoal {
      private final Llama llama;
      private LivingEntity ownerLastHurtBy;
      private int timestamp;

      public TraderLlamaDefendWanderingTraderGoal(Llama var1) {
         super(var1, false);
         this.llama = var1;
         this.setFlags(EnumSet.of(Goal.Flag.TARGET));
      }

      public boolean canUse() {
         if (!this.llama.isLeashed()) {
            return false;
         } else {
            Entity var1 = this.llama.getLeashHolder();
            if (!(var1 instanceof WanderingTrader)) {
               return false;
            } else {
               WanderingTrader var2 = (WanderingTrader)var1;
               this.ownerLastHurtBy = var2.getLastHurtByMob();
               int var3 = var2.getLastHurtByMobTimestamp();
               return var3 != this.timestamp && this.canAttack(this.ownerLastHurtBy, TargetingConditions.DEFAULT);
            }
         }
      }

      public void start() {
         this.mob.setTarget(this.ownerLastHurtBy);
         Entity var1 = this.llama.getLeashHolder();
         if (var1 instanceof WanderingTrader) {
            this.timestamp = ((WanderingTrader)var1).getLastHurtByMobTimestamp();
         }

         super.start();
      }
   }
}
