package net.minecraft.world.entity.projectile.throwableitemprojectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public abstract class ThrowableItemProjectile extends ThrowableProjectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> var1, Level var2) {
      super(var1, var2);
   }

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> var1, double var2, double var4, double var6, Level var8, ItemStack var9) {
      super(var1, var2, var4, var6, var8);
      this.setItem(var9);
   }

   public ThrowableItemProjectile(EntityType<? extends ThrowableItemProjectile> var1, LivingEntity var2, Level var3, ItemStack var4) {
      this(var1, var2.getX(), var2.getEyeY() - 0.10000000149011612, var2.getZ(), var3, var4);
      this.setOwner(var2);
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

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.store("Item", ItemStack.CODEC, this.getItem());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setItem((ItemStack)var1.read("Item", ItemStack.CODEC).orElseGet(() -> new ItemStack(this.getDefaultItem())));
   }

   static {
      DATA_ITEM_STACK = SynchedEntityData.<ItemStack>defineId(ThrowableItemProjectile.class, EntityDataSerializers.ITEM_STACK);
   }
}
