package net.minecraft.world.level.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;

public abstract class RandomizableContainerBlockEntity extends BaseContainerBlockEntity implements RandomizableContainer {
   @Nullable
   protected ResourceKey<LootTable> lootTable;
   protected long lootTableSeed = 0L;

   protected RandomizableContainerBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
   }

   @Nullable
   public ResourceKey<LootTable> getLootTable() {
      return this.lootTable;
   }

   public void setLootTable(@Nullable ResourceKey<LootTable> var1) {
      this.lootTable = var1;
   }

   public long getLootTableSeed() {
      return this.lootTableSeed;
   }

   public void setLootTableSeed(long var1) {
      this.lootTableSeed = var1;
   }

   public boolean isEmpty() {
      this.unpackLootTable((Player)null);
      return super.isEmpty();
   }

   public ItemStack getItem(int var1) {
      this.unpackLootTable((Player)null);
      return super.getItem(var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      this.unpackLootTable((Player)null);
      return super.removeItem(var1, var2);
   }

   public ItemStack removeItemNoUpdate(int var1) {
      this.unpackLootTable((Player)null);
      return super.removeItemNoUpdate(var1);
   }

   public void setItem(int var1, ItemStack var2) {
      this.unpackLootTable((Player)null);
      super.setItem(var1, var2);
   }

   public boolean canOpen(Player var1) {
      return super.canOpen(var1) && (this.lootTable == null || !var1.isSpectator());
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      if (this.canOpen(var3)) {
         this.unpackLootTable(var2.player);
         return this.createMenu(var1, var2);
      } else {
         return null;
      }
   }

   protected void applyImplicitComponents(BlockEntity.DataComponentInput var1) {
      super.applyImplicitComponents(var1);
      SeededContainerLoot var2 = (SeededContainerLoot)var1.get(DataComponents.CONTAINER_LOOT);
      if (var2 != null) {
         this.lootTable = var2.lootTable();
         this.lootTableSeed = var2.seed();
      }

   }

   protected void collectImplicitComponents(DataComponentMap.Builder var1) {
      super.collectImplicitComponents(var1);
      if (this.lootTable != null) {
         var1.set(DataComponents.CONTAINER_LOOT, new SeededContainerLoot(this.lootTable, this.lootTableSeed));
      }

   }

   public void removeComponentsFromTag(CompoundTag var1) {
      super.removeComponentsFromTag(var1);
      var1.remove("LootTable");
      var1.remove("LootTableSeed");
   }
}
