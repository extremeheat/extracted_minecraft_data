package net.minecraft.world.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Shearable;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;

public class Bogged extends AbstractSkeleton implements Shearable {
   private static final int HARD_ATTACK_INTERVAL = 50;
   private static final int NORMAL_ATTACK_INTERVAL = 70;
   private static final EntityDataAccessor<Boolean> DATA_SHEARED = SynchedEntityData.defineId(Bogged.class, EntityDataSerializers.BOOLEAN);
   public static final String SHEARED_TAG_NAME = "sheared";

   public static AttributeSupplier.Builder createAttributes() {
      return AbstractSkeleton.createAttributes().add(Attributes.MAX_HEALTH, 16.0);
   }

   public Bogged(EntityType<? extends Bogged> var1, Level var2) {
      super(var1, var2);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      super.defineSynchedData(var1);
      var1.define(DATA_SHEARED, false);
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("sheared", this.isSheared());
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setSheared(var1.getBoolean("sheared"));
   }

   public boolean isSheared() {
      return this.entityData.get(DATA_SHEARED);
   }

   public void setSheared(boolean var1) {
      this.entityData.set(DATA_SHEARED, var1);
   }

   @Override
   protected InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (var3.is(Items.SHEARS) && this.readyForShearing()) {
         if (this.level() instanceof ServerLevel var4) {
            this.shear(var4, SoundSource.PLAYERS, var3);
            this.gameEvent(GameEvent.SHEAR, var1);
            var3.hurtAndBreak(1, var1, getSlotForHand(var2));
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.mobInteract(var1, var2);
      }
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return SoundEvents.BOGGED_AMBIENT;
   }

   @Override
   protected SoundEvent getHurtSound(DamageSource var1) {
      return SoundEvents.BOGGED_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.BOGGED_DEATH;
   }

   @Override
   protected SoundEvent getStepSound() {
      return SoundEvents.BOGGED_STEP;
   }

   @Override
   protected AbstractArrow getArrow(ItemStack var1, float var2, @Nullable ItemStack var3) {
      AbstractArrow var4 = super.getArrow(var1, var2, var3);
      if (var4 instanceof Arrow var5) {
         var5.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
      }

      return var4;
   }

   @Override
   protected int getHardAttackInterval() {
      return 50;
   }

   @Override
   protected int getAttackInterval() {
      return 70;
   }

   @Override
   public void shear(ServerLevel var1, SoundSource var2, ItemStack var3) {
      var1.playSound(null, this, SoundEvents.BOGGED_SHEAR, var2, 1.0F, 1.0F);
      this.spawnShearedMushrooms(var1, var3);
      this.setSheared(true);
   }

   private void spawnShearedMushrooms(ServerLevel var1, ItemStack var2) {
      this.dropFromShearingLootTable(var1, BuiltInLootTables.BOGGED_SHEAR, var2, (var1x, var2x) -> this.spawnAtLocation(var1x, var2x, this.getBbHeight()));
   }

   @Override
   public boolean readyForShearing() {
      return !this.isSheared() && this.isAlive();
   }
}
