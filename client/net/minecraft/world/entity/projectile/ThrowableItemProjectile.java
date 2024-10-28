package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public abstract class ThrowableItemProjectile extends ThrowableProjectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> var1, Level var2) {
      super(var1, var2);
   }

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> var1, double var2, double var4, double var6, Level var8) {
      super(var1, var2, var4, var6, var8);
   }

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> var1, LivingEntity var2, Level var3) {
      super(var1, var2, var3);
   }

   public void setItem(ItemStack var1) {
      this.getEntityData().set(DATA_ITEM_STACK, var1.copyWithCount(1));
   }

   protected abstract Item getDefaultItem();

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ITEM_STACK, new ItemStack(this.getDefaultItem()));
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("Item", this.getItem().save(this.registryAccess()));
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Item", 10)) {
         this.setItem((ItemStack)ItemStack.parse(this.registryAccess(), var1.getCompound("Item")).orElseGet(() -> {
            return new ItemStack(this.getDefaultItem());
         }));
      } else {
         this.setItem(new ItemStack(this.getDefaultItem()));
      }

   }

   static {
      DATA_ITEM_STACK = SynchedEntityData.defineId(ThrowableItemProjectile.class, EntityDataSerializers.ITEM_STACK);
   }
}
