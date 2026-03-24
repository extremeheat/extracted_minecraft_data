package net.minecraft.world.entity.vehicle.minecart;

import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class AbstractMinecartContainer extends AbstractMinecart implements ContainerEntity {
   private NonNullList<ItemStack> itemStacks;
   private @Nullable ResourceKey<LootTable> lootTable;
   private long lootTableSeed;

   protected AbstractMinecartContainer(final EntityType<?> type, final Level level) {
      super(type, level);
      this.itemStacks = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
   }

   public void destroy(final ServerLevel level, final DamageSource source) {
      super.destroy(level, source);
      this.chestVehicleDestroyed(source, level, this);
   }

   public ItemStack getItem(final int slot) {
      return this.getChestVehicleItem(slot);
   }

   public ItemStack removeItem(final int slot, final int count) {
      return this.removeChestVehicleItem(slot, count);
   }

   public ItemStack removeItemNoUpdate(final int slot) {
      return this.removeChestVehicleItemNoUpdate(slot);
   }

   public void setItem(final int slot, final ItemStack itemStack) {
      this.setChestVehicleItem(slot, itemStack);
   }

   public SlotAccess getSlot(final int slot) {
      return this.getChestVehicleSlot(slot);
   }

   public void setChanged() {
   }

   public boolean stillValid(final Player player) {
      return this.isChestVehicleStillValid(player);
   }

   public void remove(final Entity.RemovalReason reason) {
      if (!this.level().isClientSide() && reason.shouldDestroy()) {
         Containers.dropContents(this.level(), (Entity)this, this);
      }

      super.remove(reason);
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      this.addChestVehicleSaveData(output);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.readChestVehicleSaveData(input);
   }

   public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
      return this.interactWithContainerVehicle(player);
   }

   protected Vec3 applyNaturalSlowdown(final Vec3 deltaMovement) {
      float keep = 0.98F;
      if (this.lootTable == null) {
         int emptiness = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
         keep += (float)emptiness * 0.001F;
      }

      if (this.isInWater()) {
         keep *= 0.95F;
      }

      return deltaMovement.multiply((double)keep, 0.0, (double)keep);
   }

   public void clearContent() {
      this.clearChestVehicleContent();
   }

   public void setLootTable(final ResourceKey<LootTable> lootTable, final long seed) {
      this.lootTable = lootTable;
      this.lootTableSeed = seed;
   }

   public @Nullable AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
      if (this.lootTable != null && player.isSpectator()) {
         return null;
      } else {
         this.unpackChestVehicleLootTable(inventory.player);
         return this.createMenu(containerId, inventory);
      }
   }

   protected abstract AbstractContainerMenu createMenu(final int containerId, final Inventory inventory);

   public @Nullable ResourceKey<LootTable> getContainerLootTable() {
      return this.lootTable;
   }

   public void setContainerLootTable(final @Nullable ResourceKey<LootTable> lootTable) {
      this.lootTable = lootTable;
   }

   public long getContainerLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setContainerLootTableSeed(final long lootTableSeed) {
      this.lootTableSeed = lootTableSeed;
   }

   public NonNullList<ItemStack> getItemStacks() {
      return this.itemStacks;
   }

   public void clearItemStacks() {
      this.itemStacks = NonNullList.<ItemStack>withSize(this.getContainerSize(), ItemStack.EMPTY);
   }
}
