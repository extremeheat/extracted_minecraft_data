package net.minecraft.world.entity.vehicle;

import java.util.function.Supplier;
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
   private NonNullList<ItemStack> itemStacks;
   @Nullable
   private ResourceKey<LootTable> lootTable;
   private long lootTableSeed;

   public AbstractChestBoat(EntityType<? extends AbstractChestBoat> var1, Level var2, Supplier<Item> var3) {
      super(var1, var2, var3);
      this.itemStacks = NonNullList.withSize(27, ItemStack.EMPTY);
   }

   protected float getSinglePassengerXOffset() {
      return 0.15F;
   }

   protected int getMaxPassengers() {
      return 1;
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      this.addChestVehicleSaveData(var1, this.registryAccess());
   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.readChestVehicleSaveData(var1, this.registryAccess());
   }

   public void destroy(ServerLevel var1, DamageSource var2) {
      this.destroy(var1, this.getDropItem());
      this.chestVehicleDestroyed(var2, var1, this);
   }

   public void remove(Entity.RemovalReason var1) {
      if (!this.level().isClientSide && var1.shouldDestroy()) {
         Containers.dropContents(this.level(), (Entity)this, (Container)this);
      }

      super.remove(var1);
   }

   public InteractionResult interact(Player var1, InteractionHand var2) {
      InteractionResult var3;
      if (!var1.isSecondaryUseActive()) {
         var3 = super.interact(var1, var2);
         if (var3 != InteractionResult.PASS) {
            return var3;
         }
      }

      if (this.canAddPassenger(var1) && !var1.isSecondaryUseActive()) {
         return InteractionResult.PASS;
      } else {
         var3 = this.interactWithContainerVehicle(var1);
         if (var3.consumesAction()) {
            Level var5 = var1.level();
            if (var5 instanceof ServerLevel) {
               ServerLevel var4 = (ServerLevel)var5;
               this.gameEvent(GameEvent.CONTAINER_OPEN, var1);
               PiglinAi.angerNearbyPiglins(var4, var1, true);
            }
         }

         return var3;
      }
   }

   public void openCustomInventoryScreen(Player var1) {
      var1.openMenu(this);
      Level var3 = var1.level();
      if (var3 instanceof ServerLevel var2) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, var1);
         PiglinAi.angerNearbyPiglins(var2, var1, true);
      }

   }

   public void clearContent() {
      this.clearChestVehicleContent();
   }

   public int getContainerSize() {
      return 27;
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

   @Nullable
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

   public void stopOpen(Player var1) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of((Entity)var1));
   }
}
