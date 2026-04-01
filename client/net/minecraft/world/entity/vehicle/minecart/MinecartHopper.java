package net.minecraft.world.entity.vehicle.minecart;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MinecartHopper extends AbstractMinecartContainer implements Hopper {
   private static final boolean DEFAULT_ENABLED = true;
   private boolean enabled = true;
   private boolean consumedItemThisFrame = false;

   public MinecartHopper(final EntityType<? extends MinecartHopper> type, final Level level) {
      super(type, level);
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

   public void activateMinecart(final ServerLevel level, final int xt, final int yt, final int zt, final boolean state) {
      boolean newEnabled = !state;
      if (newEnabled != this.isEnabled()) {
         this.setEnabled(newEnabled);
      }

   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(final boolean enabled) {
      this.enabled = enabled;
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
      this.consumedItemThisFrame = false;
      super.tick();
      this.tryConsumeItems();
   }

   protected double makeStepAlongTrack(final BlockPos pos, final RailShape shape, final double movementLeft) {
      double left = super.makeStepAlongTrack(pos, shape, movementLeft);
      this.tryConsumeItems();
      return left;
   }

   private void tryConsumeItems() {
      if (!this.level().isClientSide() && this.isAlive() && this.isEnabled() && !this.consumedItemThisFrame && this.suckInItems()) {
         this.consumedItemThisFrame = true;
         this.setChanged();
      }

   }

   public boolean suckInItems() {
      if (HopperBlockEntity.suckInItems(this.level(), this)) {
         return true;
      } else {
         for(LivingBlock entity : this.level().getEntitiesOfClass(LivingBlock.class, this.getBoundingBox().inflate(0.25, 0.0, 0.25), EntitySelector.ENTITY_STILL_ALIVE)) {
            if (HopperBlockEntity.addItem(this, entity)) {
               return true;
            }
         }

         return false;
      }
   }

   protected Item getDropItem() {
      return Items.HOPPER_MINECART;
   }

   public ItemStack getPickResult() {
      return new ItemStack(Items.HOPPER_MINECART);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      output.putBoolean("Enabled", this.enabled);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.enabled = input.getBooleanOr("Enabled", true);
   }

   public AbstractContainerMenu createMenu(final int containerId, final Inventory inventory) {
      return new HopperMenu(containerId, inventory, this);
   }
}
