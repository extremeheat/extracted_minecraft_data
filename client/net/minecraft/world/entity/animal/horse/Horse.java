package net.minecraft.world.entity.animal.horse;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.SoundType;

public class Horse extends AbstractHorse {
   private static final UUID ARMOR_MODIFIER_UUID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
   private static final EntityDataAccessor<Integer> DATA_ID_TYPE_VARIANT;

   public Horse(EntityType<? extends Horse> var1, Level var2) {
      super(var1, var2);
   }

   protected void randomizeAttributes() {
      this.getAttribute(Attributes.MAX_HEALTH).setBaseValue((double)this.generateRandomMaxHealth());
      this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.generateRandomSpeed());
      this.getAttribute(Attributes.JUMP_STRENGTH).setBaseValue(this.generateRandomJumpStrength());
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_ID_TYPE_VARIANT, 0);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("Variant", this.getTypeVariant());
      if (!this.inventory.getItem(1).isEmpty()) {
         var1.put("ArmorItem", this.inventory.getItem(1).save(new CompoundTag()));
      }

   }

   public ItemStack getArmor() {
      return this.getItemBySlot(EquipmentSlot.CHEST);
   }

   private void setArmor(ItemStack var1) {
      this.setItemSlot(EquipmentSlot.CHEST, var1);
      this.setDropChance(EquipmentSlot.CHEST, 0.0F);
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.setTypeVariant(var1.getInt("Variant"));
      if (var1.contains("ArmorItem", 10)) {
         ItemStack var2 = ItemStack.of(var1.getCompound("ArmorItem"));
         if (!var2.isEmpty() && this.isArmor(var2)) {
            this.inventory.setItem(1, var2);
         }
      }

      this.updateContainerEquipment();
   }

   private void setTypeVariant(int var1) {
      this.entityData.set(DATA_ID_TYPE_VARIANT, var1);
   }

   private int getTypeVariant() {
      return (Integer)this.entityData.get(DATA_ID_TYPE_VARIANT);
   }

   private void setVariantAndMarkings(Variant var1, Markings var2) {
      this.setTypeVariant(var1.getId() & 255 | var2.getId() << 8 & '\uff00');
   }

   public Variant getVariant() {
      return Variant.byId(this.getTypeVariant() & 255);
   }

   public Markings getMarkings() {
      return Markings.byId((this.getTypeVariant() & '\uff00') >> 8);
   }

   protected void updateContainerEquipment() {
      if (!this.level.isClientSide) {
         super.updateContainerEquipment();
         this.setArmorEquipment(this.inventory.getItem(1));
         this.setDropChance(EquipmentSlot.CHEST, 0.0F);
      }
   }

   private void setArmorEquipment(ItemStack var1) {
      this.setArmor(var1);
      if (!this.level.isClientSide) {
         this.getAttribute(Attributes.ARMOR).removeModifier(ARMOR_MODIFIER_UUID);
         if (this.isArmor(var1)) {
            int var2 = ((HorseArmorItem)var1.getItem()).getProtection();
            if (var2 != 0) {
               this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(ARMOR_MODIFIER_UUID, "Horse armor bonus", (double)var2, AttributeModifier.Operation.ADDITION));
            }
         }
      }

   }

   public void containerChanged(Container var1) {
      ItemStack var2 = this.getArmor();
      super.containerChanged(var1);
      ItemStack var3 = this.getArmor();
      if (this.tickCount > 20 && this.isArmor(var3) && var2 != var3) {
         this.playSound(SoundEvents.HORSE_ARMOR, 0.5F, 1.0F);
      }

   }

   protected void playGallopSound(SoundType var1) {
      super.playGallopSound(var1);
      if (this.random.nextInt(10) == 0) {
         this.playSound(SoundEvents.HORSE_BREATHE, var1.getVolume() * 0.6F, var1.getPitch());
      }

   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return SoundEvents.HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.HORSE_DEATH;
   }

   @Nullable
   protected SoundEvent getEatingSound() {
      return SoundEvents.HORSE_EAT;
   }

   protected SoundEvent getHurtSound(DamageSource var1) {
      super.getHurtSound(var1);
      return SoundEvents.HORSE_HURT;
   }

   protected SoundEvent getAngrySound() {
      super.getAngrySound();
      return SoundEvents.HORSE_ANGRY;
   }

   public InteractionResult mobInteract(Player var1, InteractionHand var2) {
      ItemStack var3 = var1.getItemInHand(var2);
      if (!this.isBaby()) {
         if (this.isTamed() && var1.isSecondaryUseActive()) {
            this.openInventory(var1);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }

         if (this.isVehicle()) {
            return super.mobInteract(var1, var2);
         }
      }

      if (!var3.isEmpty()) {
         if (this.isFood(var3)) {
            return this.fedFood(var1, var3);
         }

         InteractionResult var4 = var3.interactLivingEntity(var1, this, var2);
         if (var4.consumesAction()) {
            return var4;
         }

         if (!this.isTamed()) {
            this.makeMad();
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }

         boolean var5 = !this.isBaby() && !this.isSaddled() && var3.is(Items.SADDLE);
         if (this.isArmor(var3) || var5) {
            this.openInventory(var1);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
         }
      }

      if (this.isBaby()) {
         return super.mobInteract(var1, var2);
      } else {
         this.doPlayerRide(var1);
         return InteractionResult.sidedSuccess(this.level.isClientSide);
      }
   }

   public boolean canMate(Animal var1) {
      if (var1 == this) {
         return false;
      } else if (!(var1 instanceof Donkey) && !(var1 instanceof Horse)) {
         return false;
      } else {
         return this.canParent() && ((AbstractHorse)var1).canParent();
      }
   }

   public AgeableMob getBreedOffspring(ServerLevel var1, AgeableMob var2) {
      AbstractHorse var3;
      if (var2 instanceof Donkey) {
         var3 = (AbstractHorse)EntityType.MULE.create(var1);
      } else {
         Horse var4 = (Horse)var2;
         var3 = (AbstractHorse)EntityType.HORSE.create(var1);
         int var6 = this.random.nextInt(9);
         Variant var5;
         if (var6 < 4) {
            var5 = this.getVariant();
         } else if (var6 < 8) {
            var5 = var4.getVariant();
         } else {
            var5 = (Variant)Util.getRandom((Object[])Variant.values(), this.random);
         }

         int var8 = this.random.nextInt(5);
         Markings var7;
         if (var8 < 2) {
            var7 = this.getMarkings();
         } else if (var8 < 4) {
            var7 = var4.getMarkings();
         } else {
            var7 = (Markings)Util.getRandom((Object[])Markings.values(), this.random);
         }

         ((Horse)var3).setVariantAndMarkings(var5, var7);
      }

      this.setOffspringAttributes(var2, var3);
      return var3;
   }

   public boolean canWearArmor() {
      return true;
   }

   public boolean isArmor(ItemStack var1) {
      return var1.getItem() instanceof HorseArmorItem;
   }

   @Nullable
   public SpawnGroupData finalizeSpawn(ServerLevelAccessor var1, DifficultyInstance var2, MobSpawnType var3, @Nullable SpawnGroupData var4, @Nullable CompoundTag var5) {
      Variant var6;
      if (var4 instanceof Horse.HorseGroupData) {
         var6 = ((Horse.HorseGroupData)var4).variant;
      } else {
         var6 = (Variant)Util.getRandom((Object[])Variant.values(), this.random);
         var4 = new Horse.HorseGroupData(var6);
      }

      this.setVariantAndMarkings(var6, (Markings)Util.getRandom((Object[])Markings.values(), this.random));
      return super.finalizeSpawn(var1, var2, var3, (SpawnGroupData)var4, var5);
   }

   static {
      DATA_ID_TYPE_VARIANT = SynchedEntityData.defineId(Horse.class, EntityDataSerializers.INT);
   }

   public static class HorseGroupData extends AgeableMob.AgeableMobGroupData {
      public final Variant variant;

      public HorseGroupData(Variant var1) {
         super(true);
         this.variant = var1;
      }
   }
}
