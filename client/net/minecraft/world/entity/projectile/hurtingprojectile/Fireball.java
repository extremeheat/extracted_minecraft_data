package net.minecraft.world.entity.projectile.hurtingprojectile;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class Fireball extends AbstractHurtingProjectile implements ItemSupplier {
   private static final float MIN_CAMERA_DISTANCE_SQUARED = 12.25F;
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

   protected void playEntityOnFireExtinguishedSound() {
   }

   public ItemStack getItem() {
      return (ItemStack)this.getEntityData().get(DATA_ITEM_STACK);
   }

   protected void defineSynchedData(SynchedEntityData.Builder var1) {
      var1.define(DATA_ITEM_STACK, this.getDefaultItem());
   }

   protected void addAdditionalSaveData(ValueOutput var1) {
      super.addAdditionalSaveData(var1);
      var1.store("Item", ItemStack.CODEC, this.getItem());
   }

   protected void readAdditionalSaveData(ValueInput var1) {
      super.readAdditionalSaveData(var1);
      this.setItem((ItemStack)var1.read("Item", ItemStack.CODEC).orElse(this.getDefaultItem()));
   }

   private ItemStack getDefaultItem() {
      return new ItemStack(Items.FIRE_CHARGE);
   }

   public @Nullable SlotAccess getSlot(int var1) {
      return var1 == 0 ? SlotAccess.of(this::getItem, this::setItem) : super.getSlot(var1);
   }

   public boolean shouldRenderAtSqrDistance(double var1) {
      return this.tickCount < 2 && var1 < 12.25 ? false : super.shouldRenderAtSqrDistance(var1);
   }

   static {
      DATA_ITEM_STACK = SynchedEntityData.<ItemStack>defineId(Fireball.class, EntityDataSerializers.ITEM_STACK);
   }
}
