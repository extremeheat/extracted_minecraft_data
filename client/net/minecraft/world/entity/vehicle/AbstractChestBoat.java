package net.minecraft.world.entity.vehicle;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class AbstractChestBoat extends AbstractBoat implements HasCustomInventoryScreen, ContainerEntity {
   private static final int CONTAINER_SIZE = 27;
   private NonNullList<ItemStack> itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
   @Nullable
   private ResourceKey<LootTable> lootTable;
   private long lootTableSeed;

   public AbstractChestBoat(EntityType<? extends AbstractChestBoat> var1, Level var2, Supplier<Item> var3) {
      super(var1, var2, var3);
   }

   @Override
   protected float getSinglePassengerXOffset() {
      return 0.15F;
   }

   @Override
   protected int getMaxPassengers() {
      return 1;
   }

   @Override
   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.addChestVehicleSaveData(var1, this.registryAccess());
   }

   @Override
   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.readChestVehicleSaveData(var1, this.registryAccess());
   }

   @Override
   public void destroy(ServerLevel var1, DamageSource var2) {
      this.destroy(var1, this.getDropItem());
      this.chestVehicleDestroyed(var2, var1, this);
   }

   @Override
   public void remove(Entity.RemovalReason var1) {
      if (!this.level().isClientSide && var1.shouldDestroy()) {
         Containers.dropContents(this.level(), this, this);
      }

      super.remove(var1);
   }

   @Override
   public InteractionResult interact(Player var1, InteractionHand var2) {
      if (!var1.isSecondaryUseActive()) {
         InteractionResult var3 = super.interact(var1, var2);
         if (var3 != InteractionResult.PASS) {
            return var3;
         }
      }

      if (this.canAddPassenger(var1) && !var1.isSecondaryUseActive()) {
         return InteractionResult.PASS;
      } else {
         InteractionResult var6 = this.interactWithContainerVehicle(var1);
         if (var6.consumesAction() && var1.level() instanceof ServerLevel var4) {
            this.gameEvent(GameEvent.CONTAINER_OPEN, var1);
            PiglinAi.angerNearbyPiglins(var4, var1, true);
         }

         return var6;
      }
   }

   @Override
   public void openCustomInventoryScreen(Player var1) {
      var1.openMenu(this);
      if (var1.level() instanceof ServerLevel var2) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, var1);
         PiglinAi.angerNearbyPiglins(var2, var1, true);
      }
   }

   @Override
   public void clearContent() {
      this.clearChestVehicleContent();
   }

   @Override
   public int getContainerSize() {
      return 27;
   }

   @Override
   public ItemStack getItem(int var1) {
      return this.getChestVehicleItem(var1);
   }

   @Override
   public ItemStack removeItem(int var1, int var2) {
      return this.removeChestVehicleItem(var1, var2);
   }

   @Override
   public ItemStack removeItemNoUpdate(int var1) {
      return this.removeChestVehicleItemNoUpdate(var1);
   }

   @Override
   public void setItem(int var1, ItemStack var2) {
      this.setChestVehicleItem(var1, var2);
   }

   @Override
   public SlotAccess getSlot(int var1) {
      return this.getChestVehicleSlot(var1);
   }

   @Override
   public void setChanged() {
   }

   @Override
   public boolean stillValid(Player var1) {
      return this.isChestVehicleStillValid(var1);
   }

   @Nullable
   @Override
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      if (this.lootTable != null && var3.isSpectator()) {
         return null;
      } else {
         this.unpackLootTable(var2.player);
         return ChestMenu.threeRows(var1, var2, this);
      }
   }

   public void unpackLootTable(@Nullable Player var1) {
      this.unpackChestVehicleLootTable(var1);
   }

   @Nullable
   @Override
   public ResourceKey<LootTable> getContainerLootTable() {
      return this.lootTable;
   }

   @Override
   public void setContainerLootTable(@Nullable ResourceKey<LootTable> var1) {
      this.lootTable = var1;
   }

   @Override
   public long getContainerLootTableSeed() {
      return this.lootTableSeed;
   }

   @Override
   public void setContainerLootTableSeed(long var1) {
      this.lootTableSeed = var1;
   }

   @Override
   public NonNullList<ItemStack> getItemStacks() {
      return this.itemStacks;
   }

   @Override
   public void clearItemStacks() {
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
   }

   @Override
   public void stopOpen(Player var1) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of(var1));
   }
}