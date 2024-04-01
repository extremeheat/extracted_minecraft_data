package net.minecraft.world.entity.monster;

import it.unimi.dsi.fastutil.objects.ObjectListIterator;
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
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

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
   public boolean hasPotatoVariant() {
      return true;
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
         this.shear(SoundSource.PLAYERS);
         this.gameEvent(GameEvent.SHEAR, var1);
         if (!this.level().isClientSide) {
            var3.hurtAndBreak(1, var1, getSlotForHand(var2));
         }

         return InteractionResult.sidedSuccess(this.level().isClientSide);
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

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   protected AbstractArrow getArrow(ItemStack var1, float var2) {
      AbstractArrow var3 = super.getArrow(var1, var2);
      if (var3 instanceof Arrow var4) {
         var4.addEffect(new MobEffectInstance(MobEffects.POISON, 100));
      }

      return var3;
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
   public void shear(SoundSource var1) {
      this.level().playSound(null, this, SoundEvents.BOGGED_SHEAR, var1, 1.0F, 1.0F);
      this.spawnShearedMushrooms();
      this.setSheared(true);
   }

   private void spawnShearedMushrooms() {
      Level var2 = this.level();
      if (var2 instanceof ServerLevel var1 && ((ServerLevel)var1).getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
         LootTable var6 = ((ServerLevel)var1).getServer().reloadableRegistries().getLootTable(BuiltInLootTables.BOGGED_SHEAR);
         LootParams var3 = new LootParams.Builder((ServerLevel)var1)
            .withParameter(LootContextParams.ORIGIN, this.position())
            .withParameter(LootContextParams.THIS_ENTITY, this)
            .create(LootContextParamSets.SHEARING);
         ObjectListIterator var4 = var6.getRandomItems(var3).iterator();

         while(var4.hasNext()) {
            ItemStack var5 = (ItemStack)var4.next();
            this.spawnAtLocation(var5);
         }
      }
   }

   @Override
   public boolean readyForShearing() {
      return !this.isSheared() && this.isAlive();
   }
}
