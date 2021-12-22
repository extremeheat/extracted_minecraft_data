package net.minecraft.world.entity.monster.piglin;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.GoalUtils;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public abstract class AbstractPiglin extends Monster {
   protected static final EntityDataAccessor<Boolean> DATA_IMMUNE_TO_ZOMBIFICATION;
   protected static final int CONVERSION_TIME = 300;
   protected int timeInOverworld;

   public AbstractPiglin(EntityType<? extends AbstractPiglin> var1, Level var2) {
      super(var1, var2);
      this.setCanPickUpLoot(true);
      this.applyOpenDoorsAbility();
      this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 16.0F);
      this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, -1.0F);
   }

   private void applyOpenDoorsAbility() {
      if (GoalUtils.hasGroundPathNavigation(this)) {
         ((GroundPathNavigation)this.getNavigation()).setCanOpenDoors(true);
      }

   }

   protected abstract boolean canHunt();

   public void setImmuneToZombification(boolean var1) {
      this.getEntityData().set(DATA_IMMUNE_TO_ZOMBIFICATION, var1);
   }

   protected boolean isImmuneToZombification() {
      return (Boolean)this.getEntityData().get(DATA_IMMUNE_TO_ZOMBIFICATION);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_IMMUNE_TO_ZOMBIFICATION, false);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.isImmuneToZombification()) {
         var1.putBoolean("IsImmuneToZombification", true);
      }

      var1.putInt("TimeInOverworld", this.timeInOverworld);
   }

   public double getMyRidingOffset() {
      return this.isBaby() ? -0.05D : -0.45D;
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setImmuneToZombification(var1.getBoolean("IsImmuneToZombification"));
      this.timeInOverworld = var1.getInt("TimeInOverworld");
   }

   protected void customServerAiStep() {
      super.customServerAiStep();
      if (this.isConverting()) {
         ++this.timeInOverworld;
      } else {
         this.timeInOverworld = 0;
      }

      if (this.timeInOverworld > 300) {
         this.playConvertedSound();
         this.finishConversion((ServerLevel)this.level);
      }

   }

   public boolean isConverting() {
      return !this.level.dimensionType().piglinSafe() && !this.isImmuneToZombification() && !this.isNoAi();
   }

   protected void finishConversion(ServerLevel var1) {
      ZombifiedPiglin var2 = (ZombifiedPiglin)this.convertTo(EntityType.ZOMBIFIED_PIGLIN, true);
      if (var2 != null) {
         var2.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));
      }

   }

   public boolean isAdult() {
      return !this.isBaby();
   }

   public abstract PiglinArmPose getArmPose();

   @Nullable
   public LivingEntity getTarget() {
      return (LivingEntity)this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse((Object)null);
   }

   protected boolean isHoldingMeleeWeapon() {
      return this.getMainHandItem().getItem() instanceof TieredItem;
   }

   public void playAmbientSound() {
      if (PiglinAi.isIdle(this)) {
         super.playAmbientSound();
      }

   }

   protected void sendDebugPackets() {
      super.sendDebugPackets();
      DebugPackets.sendEntityBrain(this);
   }

   protected abstract void playConvertedSound();

   static {
      DATA_IMMUNE_TO_ZOMBIFICATION = SynchedEntityData.defineId(AbstractPiglin.class, EntityDataSerializers.BOOLEAN);
   }
}
