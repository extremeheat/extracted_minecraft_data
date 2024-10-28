package net.minecraft.world.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class Fireball extends AbstractHurtingProjectile implements ItemSupplier {
   private static final EntityDataAccessor<ItemStack> DATA_ITEM_STACK;

   public Fireball(EntityType<? extends Fireball> var1, Level var2) {
      super(var1, var2);
   }

   public Fireball(EntityType<? extends Fireball> var1, double var2, double var4, double var6, Vec3 var8, Level var9) {
      super(var1, var2, var4, var6, var8, var9);
   }

   public Fireball(EntityType<? extends Fireball> var1, LivingEntity var2, Vec3 var3, Level var4) {
      super(var1, var2, var3, var4);
   }

   public void setItem(ItemStack var1) {
      if (var1.isEmpty()) {
         this.getEntityData().set(DATA_ITEM_STACK, this.getDefaultItem());
      } else {
         this.getEntityData().set(DATA_ITEM_STACK, var1.copyWithCount(1));
      }

   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ITEM_STACK, this.getDefaultItem());
   }

   public void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.put("Item", this.getItem().save(this.registryAccess()));
   }

   public void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      if (var1.contains("Item", 10)) {
         this.setItem((ItemStack)ItemStack.parse(this.registryAccess(), var1.getCompound("Item")).orElse(this.getDefaultItem()));
      } else {
         this.setItem(this.getDefaultItem());
      }

   }

   private ItemStack getDefaultItem() {
      return new ItemStack(Items.FIRE_CHARGE);
   }

   public SlotAccess getSlot(int var1) {
      return var1 == 0 ? SlotAccess.of(this::getItem, this::setItem) : super.getSlot(var1);
   }

   public boolean hurt(DamageSource var1, float var2) {
      return false;
   }

   static {
      DATA_ITEM_STACK = SynchedEntityData.defineId(Fireball.class, EntityDataSerializers.ITEM_STACK);
   }
}
