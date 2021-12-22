package net.minecraft.world.level.block.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public abstract class RandomizableContainerBlockEntity extends BaseContainerBlockEntity {
   public static final String LOOT_TABLE_TAG = "LootTable";
   public static final String LOOT_TABLE_SEED_TAG = "LootTableSeed";
   @Nullable
   protected ResourceLocation lootTable;
   protected long lootTableSeed;

   protected RandomizableContainerBlockEntity(BlockEntityType<?> var1, BlockPos var2, BlockState var3) {
      super(var1, var2, var3);
   }

   public static void setLootTable(BlockGetter var0, Random var1, BlockPos var2, ResourceLocation var3) {
      BlockEntity var4 = var0.getBlockEntity(var2);
      if (var4 instanceof RandomizableContainerBlockEntity) {
         ((RandomizableContainerBlockEntity)var4).setLootTable(var3, var1.nextLong());
      }

   }

   protected boolean tryLoadLootTable(CompoundTag var1) {
      if (var1.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(var1.getString("LootTable"));
         this.lootTableSeed = var1.getLong("LootTableSeed");
         return true;
      } else {
         return false;
      }
   }

   protected boolean trySaveLootTable(CompoundTag var1) {
      if (this.lootTable == null) {
         return false;
      } else {
         var1.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            var1.putLong("LootTableSeed", this.lootTableSeed);
         }

         return true;
      }
   }

   public void unpackLootTable(@Nullable Player var1) {
      if (this.lootTable != null && this.level.getServer() != null) {
         LootTable var2 = this.level.getServer().getLootTables().get(this.lootTable);
         if (var1 instanceof ServerPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)var1, this.lootTable);
         }

         this.lootTable = null;
         LootContext.Builder var3 = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition)).withOptionalRandomSeed(this.lootTableSeed);
         if (var1 != null) {
            var3.withLuck(var1.getLuck()).withParameter(LootContextParams.THIS_ENTITY, var1);
         }

         var2.fill(this, var3.create(LootContextParamSets.CHEST));
      }

   }

   public void setLootTable(ResourceLocation var1, long var2) {
      this.lootTable = var1;
      this.lootTableSeed = var2;
   }

   public boolean isEmpty() {
      this.unpackLootTable((Player)null);
      return this.getItems().stream().allMatch(ItemStack::isEmpty);
   }

   public ItemStack getItem(int var1) {
      this.unpackLootTable((Player)null);
      return (ItemStack)this.getItems().get(var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      this.unpackLootTable((Player)null);
      ItemStack var3 = ContainerHelper.removeItem(this.getItems(), var1, var2);
      if (!var3.isEmpty()) {
         this.setChanged();
      }

      return var3;
   }

   public ItemStack removeItemNoUpdate(int var1) {
      this.unpackLootTable((Player)null);
      return ContainerHelper.takeItem(this.getItems(), var1);
   }

   public void setItem(int var1, ItemStack var2) {
      this.unpackLootTable((Player)null);
      this.getItems().set(var1, var2);
      if (var2.getCount() > this.getMaxStackSize()) {
         var2.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   public boolean stillValid(Player var1) {
      if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         return !(var1.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
      }
   }

   public void clearContent() {
      this.getItems().clear();
   }

   protected abstract NonNullList<ItemStack> getItems();

   protected abstract void setItems(NonNullList<ItemStack> var1);

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
}
