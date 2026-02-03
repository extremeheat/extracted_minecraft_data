package net.minecraft.world.level.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jspecify.annotations.Nullable;

public abstract class RandomizableContainerBlockEntity extends BaseContainerBlockEntity implements RandomizableContainer {
   protected @Nullable ResourceKey<LootTable> lootTable;
   protected long lootTableSeed = 0L;

   protected RandomizableContainerBlockEntity(final BlockEntityType<?> type, final BlockPos worldPosition, final BlockState blockState) {
      super(type, worldPosition, blockState);
   }

   public @Nullable ResourceKey<LootTable> getLootTable() {
      return this.lootTable;
   }

   public void setLootTable(final @Nullable ResourceKey<LootTable> lootTable) {
      this.lootTable = lootTable;
   }

   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setLootTableSeed(final long lootTableSeed) {
      this.lootTableSeed = lootTableSeed;
   }

   public boolean isEmpty() {
      this.unpackLootTable((Player)null);
      return super.isEmpty();
   }

   public ItemStack getItem(final int slot) {
      this.unpackLootTable((Player)null);
      return super.getItem(slot);
   }

   public ItemStack removeItem(final int slot, final int count) {
      this.unpackLootTable((Player)null);
      return super.removeItem(slot, count);
   }

   public ItemStack removeItemNoUpdate(final int slot) {
      this.unpackLootTable((Player)null);
      return super.removeItemNoUpdate(slot);
   }

   public void setItem(final int slot, final ItemStack itemStack) {
      this.unpackLootTable((Player)null);
      super.setItem(slot, itemStack);
   }

   public boolean canOpen(final Player player) {
      return (this.lootTable == null || !player.isSpectator()) && super.canOpen(player);
   }

   public @Nullable AbstractContainerMenu createMenu(final int containerId, final Inventory inventory, final Player player) {
      if (this.canOpen(player)) {
         this.unpackLootTable(inventory.player);
         return this.createMenu(containerId, inventory);
      } else {
         if (!player.isSpectator()) {
            BaseContainerBlockEntity.sendChestLockedNotifications(this.getBlockPos().getCenter(), player, this.getDisplayName());
         }

         return null;
      }
   }

   protected void applyImplicitComponents(final DataComponentGetter components) {
      super.applyImplicitComponents(components);
      SeededContainerLoot loot = (SeededContainerLoot)components.get(DataComponents.CONTAINER_LOOT);
      if (loot != null) {
         this.lootTable = loot.lootTable();
         this.lootTableSeed = loot.seed();
      }

   }

   protected void collectImplicitComponents(final DataComponentMap.Builder components) {
      super.collectImplicitComponents(components);
      if (this.lootTable != null) {
         components.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.lootTable, this.lootTableSeed));
      }

   }

   public void removeComponentsFromTag(final ValueOutput output) {
      super.removeComponentsFromTag(output);
      output.discard("LootTable");
      output.discard("LootTableSeed");
   }
}
