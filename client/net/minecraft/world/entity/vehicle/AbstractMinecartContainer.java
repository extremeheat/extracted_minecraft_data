package net.minecraft.world.entity.vehicle;

import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractMinecartContainer extends AbstractMinecart implements ContainerEntity {
   private NonNullList<ItemStack> itemStacks;
   @Nullable
   private ResourceKey<LootTable> lootTable;
   private long lootTableSeed;

   protected AbstractMinecartContainer(EntityType<?> var1, Level var2) {
      super(var1, var2);
      this.itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
   }

   public void destroy(ServerLevel var1, DamageSource var2) {
      super.destroy(var1, var2);
      this.chestVehicleDestroyed(var2, var1, this);
   }

   public ItemStack getItem(int var1) {
      return this.getChestVehicleItem(var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      return this.removeChestVehicleItem(var1, var2);
   }

   public ItemStack removeItemNoUpdate(int var1) {
      return this.removeChestVehicleItemNoUpdate(var1);
   }

   public void setItem(int var1, ItemStack var2) {
      this.setChestVehicleItem(var1, var2);
   }

   public SlotAccess getSlot(int var1) {
      return this.getChestVehicleSlot(var1);
   }

   public void setChanged() {
   }

   public boolean stillValid(Player var1) {
      return this.isChestVehicleStillValid(var1);
   }

   public void remove(Entity.RemovalReason var1) {
      if (!this.level().isClientSide && var1.shouldDestroy()) {
         Containers.dropContents(this.level(), (Entity)this, (Container)this);
      }

      super.remove(var1);
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.addChestVehicleSaveData(var1, this.registryAccess());
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.readChestVehicleSaveData(var1, this.registryAccess());
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      return this.interactWithContainerVehicle(var1);
   }

   protected Vec3 applyNaturalSlowdown(Vec3 var1) {
      float var2 = 0.98F;
      if (this.lootTable == null) {
         int var3 = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
         var2 += (float)var3 * 0.001F;
      }

      if (this.isInWater()) {
         var2 *= 0.95F;
      }

      return var1.multiply((double)var2, 0.0, (double)var2);
   }

   public void clearContent() {
      this.clearChestVehicleContent();
   }

   public void setLootTable(ResourceKey<LootTable> var1, long var2) {
      this.lootTable = var1;
      this.lootTableSeed = var2;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      if (this.lootTable != null && var3.isSpectator()) {
         return null;
      } else {
         this.unpackChestVehicleLootTable(var2.player);
         return this.createMenu(var1, var2);
      }
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);

   @Nullable
   public ResourceKey<LootTable> getContainerLootTable() {
      return this.lootTable;
   }

   public void setContainerLootTable(@Nullable ResourceKey<LootTable> var1) {
      this.lootTable = var1;
   }

   public long getContainerLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setContainerLootTableSeed(long var1) {
      this.lootTableSeed = var1;
   }

   public NonNullList<ItemStack> getItemStacks() {
      return this.itemStacks;
   }

   public void clearItemStacks() {
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
   }
}
