package net.minecraft.world.entity.vehicle;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class MinecartHopper extends AbstractMinecartContainer implements Hopper {
   public static final int MOVE_ITEM_SPEED = 4;
   private boolean enabled = true;
   private int cooldownTime = -1;
   private final BlockPos lastPosition;

   public MinecartHopper(EntityType<? extends MinecartHopper> var1, Level var2) {
      super(var1, var2);
      this.lastPosition = BlockPos.ZERO;
   }

   public MinecartHopper(Level var1, double var2, double var4, double var6) {
      super(EntityType.HOPPER_MINECART, var2, var4, var6, var1);
      this.lastPosition = BlockPos.ZERO;
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
      return this.getY() + 0.5D;
   }

   public double getLevelZ() {
      return this.getZ();
   }

   public void tick() {
      super.tick();
      if (!this.level.isClientSide && this.isAlive() && this.isEnabled()) {
         BlockPos var1 = this.blockPosition();
         if (var1.equals(this.lastPosition)) {
            --this.cooldownTime;
         } else {
            this.setCooldown(0);
         }

         if (!this.isOnCooldown()) {
            this.setCooldown(0);
            if (this.suckInItems()) {
               this.setCooldown(4);
               this.setChanged();
            }
         }
      }

   }

   public boolean suckInItems() {
      if (HopperBlockEntity.suckInItems(this.level, this)) {
         return true;
      } else {
         List var1 = this.level.getEntitiesOfClass(ItemEntity.class, this.getBoundingBox().inflate(0.25D, 0.0D, 0.25D), EntitySelector.ENTITY_STILL_ALIVE);
         if (!var1.isEmpty()) {
            HopperBlockEntity.addItem(this, (ItemEntity)var1.get(0));
         }

         return false;
      }
   }

   public void destroy(DamageSource var1) {
      super.destroy(var1);
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         this.spawnAtLocation(Blocks.HOPPER);
      }

   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      var1.putInt("TransferCooldown", this.cooldownTime);
      var1.putBoolean("Enabled", this.enabled);
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.cooldownTime = var1.getInt("TransferCooldown");
      this.enabled = var1.contains("Enabled") ? var1.getBoolean("Enabled") : true;
   }

   public void setCooldown(int var1) {
      this.cooldownTime = var1;
   }

   public boolean isOnCooldown() {
      return this.cooldownTime > 0;
   }

   public AbstractContainerMenu createMenu(int var1, Inventory var2) {
      return new HopperMenu(var1, var2, this);
   }
}
