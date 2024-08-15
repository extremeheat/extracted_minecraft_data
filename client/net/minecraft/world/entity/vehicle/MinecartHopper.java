package net.minecraft.world.entity.vehicle;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.properties.RailShape;

public class MinecartHopper extends AbstractMinecartContainer implements Hopper {
   private boolean enabled = true;
   private boolean consumedItemThisFrame = false;

   public MinecartHopper(EntityType<? extends MinecartHopper> var1, Level var2) {
      super(var1, var2);
   }

   public MinecartHopper(Level var1, double var2, double var4, double var6) {
      super(EntityType.HOPPER_MINECART, var2, var4, var6, var1);
   }

   @Override
   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.HOPPER;
   }

   @Override
   public BlockState getDefaultDisplayBlockState() {
      return Blocks.HOPPER.defaultBlockState();
   }

   @Override
   public int getDefaultDisplayOffset() {
      return 1;
   }

   @Override
   public int getContainerSize() {
      return 5;
   }

   @Override
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

   @Override
   public double getLevelX() {
      return this.getX();
   }

   @Override
   public double getLevelY() {
      return this.getY() + 0.5;
   }

   @Override
   public double getLevelZ() {
      return this.getZ();
   }

   @Override
   public boolean isGridAligned() {
      return false;
   }

   @Override
   public void tick() {
      this.consumedItemThisFrame = false;
      super.tick();
      this.tryConsumeItems();
   }

   @Override
   protected double makeStepAlongTrack(BlockPos var1, RailShape var2, double var3) {
      double var5 = super.makeStepAlongTrack(var1, var2, var3);
      this.tryConsumeItems();
      return var5;
   }

   private void tryConsumeItems() {
      if (!this.level().isClientSide && this.isAlive() && this.isEnabled() && !this.consumedItemThisFrame && this.suckInItems()) {
         this.consumedItemThisFrame = true;
         this.setChanged();
      }
   }

   public boolean suckInItems() {
      if (HopperBlockEntity.suckInItems(this.level(), this)) {
         return true;
      } else {
         for (ItemEntity var3 : this.level()
            .getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25, 0.0, 0.25), EntitySelector.ENTITY_STILL_ALIVE)) {
            if (HopperBlockEntity.addItem(this, var3)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   protected Item getDropItem() {
      return Items.HOPPER_MINECART;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putBoolean("Enabled", this.enabled);
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.enabled = var1.contains("Enabled") ? var1.getBoolean("Enabled") : true;
   }

   @Override
   public AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new HopperMenu(var1, var2, this);
   }
}
