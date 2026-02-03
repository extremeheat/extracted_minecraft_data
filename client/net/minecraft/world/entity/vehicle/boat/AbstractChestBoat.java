package net.minecraft.world.entity.vehicle.boat;

import java.util.function.Supplier;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.HasCustomInventoryScreen;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class AbstractChestBoat extends AbstractBoat implements HasCustomInventoryScreen, ContainerEntity {
   private static final int CONTAINER_SIZE = 27;
   private NonNullList<ItemStack> itemStacks;
   private @Nullable ResourceKey<LootTable> lootTable;
   private long lootTableSeed;

   public AbstractChestBoat(final EntityType<? extends AbstractChestBoat> type, final Level level, final Supplier<Item> dropItem) {
      super(type, level, dropItem);
      this.itemStacks = NonNullList.<ItemStack>withSize(27, ItemStack.EMPTY);
   }

   protected float getSinglePassengerXOffset() {
      return 0.15F;
   }

   protected int getMaxPassengers() {
      return 1;
   }

   protected void addAdditionalSaveData(final ValueOutput output) {
      super.addAdditionalSaveData(output);
      this.addChestVehicleSaveData(output);
   }

   protected void readAdditionalSaveData(final ValueInput input) {
      super.readAdditionalSaveData(input);
      this.readChestVehicleSaveData(input);
   }

   public void destroy(final ServerLevel level, final DamageSource source) {
      this.destroy(level, this.getDropItem());
      this.chestVehicleDestroyed(source, level, this);
   }

   public void remove(final Entity.RemovalReason reason) {
      if (!this.level().isClientSide() && reason.shouldDestroy()) {
         Containers.dropContents(this.level(), (Entity)this, this);
      }

      super.remove(reason);
   }

   public InteractionResult interact(final Player player, final InteractionHand hand, final Vec3 location) {
      InteractionResult superInteraction = super.interact(player, hand, location);
      if (superInteraction != InteractionResult.PASS) {
         return superInteraction;
      } else if (this.canAddPassenger(player) && !player.isSecondaryUseActive()) {
         return InteractionResult.PASS;
      } else {
         InteractionResult result = this.interactWithContainerVehicle(player);
         if (result.consumesAction()) {
            Level var7 = player.level();
            if (var7 instanceof ServerLevel) {
               ServerLevel serverLevel = (ServerLevel)var7;
               this.gameEvent(GameEvent.CONTAINER_OPEN, player);
               PiglinAi.angerNearbyPiglins(serverLevel, player, true);
            }
         }

         return result;
      }
   }

   public void openCustomInventoryScreen(final Player player) {
      player.openMenu(this);
      Level var3 = player.level();
      if (var3 instanceof ServerLevel level) {
         this.gameEvent(GameEvent.CONTAINER_OPEN, player);
         PiglinAi.angerNearbyPiglins(level, player, true);
      }

   }

   public void clearContent() {
      this.clearChestVehicleContent();
   }

   public int getContainerSize() {
      return 27;
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

   public @Nullable AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
      if (this.lootTable != null && player.isSpectator()) {
         return null;
      } else {
         this.unpackLootTable(inventory.player);
         return ChestMenu.threeRows(containerId, inventory, this);
      }
   }

   public void unpackLootTable(final @Nullable Player player) {
      this.unpackChestVehicleLootTable(player);
   }

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

   public void stopOpen(final ContainerUser containerUser) {
      this.level().gameEvent(GameEvent.CONTAINER_CLOSE, this.position(), GameEvent.Context.of((Entity)containerUser.getLivingEntity()));
   }
}
