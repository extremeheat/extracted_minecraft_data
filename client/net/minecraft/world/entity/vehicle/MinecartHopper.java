package net.minecraft.world.entity.vehicle;

import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartHopper extends AbstractMinecartContainer implements Hopper {
   private boolean enabled = true;

   public MinecartHopper(EntityType<? extends MinecartHopper> var1, Level var2) {
      super(var1, var2);
   }

   public MinecartHopper(Level var1, double var2, double var4, double var6) {
      super(EntityType.HOPPER_MINECART, var2, var4, var6, var1);
   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.HOPPER;
   }

   public BlockState getDefaultDisplayBlockState() {
      return Blocks.HOPPER.defaultBlockState();
   }

   public int getDefaultDisplayOffset() {
      return 1;
   }

   public int getContainerSize() {
      return 5;
   }

   public void activateMinecart(int var1, int var2, int var3, boolean var4) {
      boolean var5 = !var4;
      if (var5 != this.isEnabled()) {
         this.setEnabled(var5);
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean var1) {
      this.enabled = var1;
   }

   public double getLevelX() {
      return this.getX();
   }

   public double getLevelY() {
      return this.getY() + 0.5;
   }

   public double getLevelZ() {
      return this.getZ();
   }

   public boolean isGridAligned() {
      return false;
   }

   public void tick() {
      super.tick();
      if (!this.level().isClientSide && this.isAlive() && this.isEnabled() && this.suckInItems()) {
         this.setChanged();
      }

   }

   public boolean suckInItems() {
      if (HopperBlockEntity.suckInItems(this.level(), this)) {
         return true;
      } else {
         List var1 = this.level().getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25, 0.0, 0.25), EntitySelector.ENTITY_STILL_ALIVE);
         Iterator var2 = var1.iterator();

         ItemEntity var3;
         do {
            if (!var2.hasNext()) {
               return false;
            }

            var3 = (ItemEntity)var2.next();
         } while(!HopperBlockEntity.addItem(this, var3));

         return true;
      }
   }

   protected Item getDropItem() {
      return Items.HOPPER_MINECART;
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("Enabled", this.enabled);
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.enabled = var1.contains("Enabled") ? var1.getBoolean("Enabled") : true;
   }

   public AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new HopperMenu(var1, var2, this);
   }
}
