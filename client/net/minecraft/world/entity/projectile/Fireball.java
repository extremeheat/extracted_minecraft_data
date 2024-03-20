package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public abstract class Fireball extends AbstractHurtingProjectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.ITEM_STACK);

   public Fireball(EntityType<? extends Fireball> var1, Level var2) {
      super(var1, var2);
   }

   public Fireball(EntityType<? extends Fireball> var1, double var2, double var4, double var6, double var8, double var10, double var12, Level var14) {
      super(var1, var2, var4, var6, var8, var10, var12, var14);
   }

   public Fireball(EntityType<? extends Fireball> var1, LivingEntity var2, double var3, double var5, double var7, Level var9) {
      super(var1, var2, var3, var5, var7, var9);
   }

   public void setItem(ItemStack var1) {
      if (var1.isEmpty()) {
         this.getEntityData().set(DATA_ITEM_STACK, this.getDefaultItem());
      } else {
         this.getEntityData().set(DATA_ITEM_STACK, var1.copyWithCount(1));
      }
   }

   @Override
   public ItemStack getItem() {
      return this.getEntityData().get(DATA_ITEM_STACK);
   }

   @Override
   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ITEM_STACK, this.getDefaultItem());
   }

   @Override
   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("Item", this.getItem().save(this.registryAccess()));
   }

   @Override
   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Item", 10)) {
         this.setItem(ItemStack.parse(this.registryAccess(), var1.getCompound("Item")).orElse(this.getDefaultItem()));
      } else {
         this.setItem(this.getDefaultItem());
      }
   }

   private ItemStack getDefaultItem() {
      return new ItemStack(Items.FIRE_CHARGE);
   }
}