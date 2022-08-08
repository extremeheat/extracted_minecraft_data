package net.minecraft.world.entity.projectile;

import net.minecraft.Util;
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
      if (!var1.is(this.getDefaultItem()) || var1.hasTag()) {
         this.getEntityData().set(DATA_ITEM_STACK, (ItemStack)Util.make(var1.copy(), (var0) -> {
            var0.setCount(1);
         }));
      }

   }

   protected abstract Item getDefaultItem();

   protected ItemStack getItemRaw() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   public ItemStack getItem() {
      ItemStack var1 = this.getItemRaw();
      return var1.isEmpty() ? new ItemStack(this.getDefaultItem()) : var1;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY);
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      ItemStack var2 = this.getItemRaw();
      if (!var2.isEmpty()) {
         var1.put("Item", var2.save(new CompoundTag()));
      }

   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      ItemStack var2 = ItemStack.of(var1.getCompound("Item"));
      this.setItem(var2);
   }

   static {
      DATA_ITEM_STACK = SynchedEntityData.defineId(ThrowableItemProjectile.class, EntityDataSerializers.ITEM_STACK);
   }
}
