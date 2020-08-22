package net.minecraft.world.entity.vehicle;

import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public abstract class AbstractMinecartContainer extends AbstractMinecart implements Container, MenuProvider {
   private NonNullList itemStacks;
   private boolean dropEquipment;
   @Nullable
   private ResourceLocation lootTable;
   private long lootTableSeed;

   protected AbstractMinecartContainer(EntityType var1, Level var2) {
      super(var1, var2);
      this.itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
      this.dropEquipment = true;
   }

   protected AbstractMinecartContainer(EntityType var1, double var2, double var4, double var6, Level var8) {
      super(var1, var8, var2, var4, var6);
      this.itemStacks = NonNullList.withSize(36, ItemStack.EMPTY);
      this.dropEquipment = true;
   }

   public void destroy(DamageSource var1) {
      super.destroy(var1);
      if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
         Containers.dropContents(this.level, (Entity)this, (Container)this);
      }

   }

   public boolean isEmpty() {
      Iterator var1 = this.itemStacks.iterator();

      ItemStack var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (ItemStack)var1.next();
      } while(var2.isEmpty());

      return false;
   }

   public ItemStack getItem(int var1) {
      this.unpackLootTable((Player)null);
      return (ItemStack)this.itemStacks.get(var1);
   }

   public ItemStack removeItem(int var1, int var2) {
      this.unpackLootTable((Player)null);
      return ContainerHelper.removeItem(this.itemStacks, var1, var2);
   }

   public ItemStack removeItemNoUpdate(int var1) {
      this.unpackLootTable((Player)null);
      ItemStack var2 = (ItemStack)this.itemStacks.get(var1);
      if (var2.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.itemStacks.set(var1, ItemStack.EMPTY);
         return var2;
      }
   }

   public void setItem(int var1, ItemStack var2) {
      this.unpackLootTable((Player)null);
      this.itemStacks.set(var1, var2);
      if (!var2.isEmpty() && var2.getCount() > this.getMaxStackSize()) {
         var2.setCount(this.getMaxStackSize());
      }

   }

   public boolean setSlot(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.getContainerSize()) {
         this.setItem(var1, var2);
         return true;
      } else {
         return false;
      }
   }

   public void setChanged() {
   }

   public boolean stillValid(Player var1) {
      if (this.removed) {
         return false;
      } else {
         return var1.distanceToSqr(this) <= 64.0D;
      }
   }

   @Nullable
   public Entity changeDimension(DimensionType var1) {
      this.dropEquipment = false;
      return super.changeDimension(var1);
   }

   public void remove() {
      if (!this.level.isClientSide && this.dropEquipment) {
         Containers.dropContents(this.level, (Entity)this, (Container)this);
      }

      super.remove();
   }

   protected void addAdditionalSaveData(CompoundTag var1) {
      super.addAdditionalSaveData(var1);
      if (this.lootTable != null) {
         var1.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            var1.putLong("LootTableSeed", this.lootTableSeed);
         }
      } else {
         ContainerHelper.saveAllItems(var1, this.itemStacks);
      }

   }

   protected void readAdditionalSaveData(CompoundTag var1) {
      super.readAdditionalSaveData(var1);
      this.itemStacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      if (var1.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(var1.getString("LootTable"));
         this.lootTableSeed = var1.getLong("LootTableSeed");
      } else {
         ContainerHelper.loadAllItems(var1, this.itemStacks);
      }

   }

   public boolean interact(Player var1, InteractionHand var2) {
      var1.openMenu(this);
      return true;
   }

   protected void applyNaturalSlowdown() {
      float var1 = 0.98F;
      if (this.lootTable == null) {
         int var2 = 15 - AbstractContainerMenu.getRedstoneSignalFromContainer(this);
         var1 += (float)var2 * 0.001F;
      }

      this.setDeltaMovement(this.getDeltaMovement().multiply((double)var1, 0.0D, (double)var1));
   }

   public void unpackLootTable(@Nullable Player var1) {
      if (this.lootTable != null && this.level.getServer() != null) {
         LootTable var2 = this.level.getServer().getLootTables().get(this.lootTable);
         this.lootTable = null;
         LootContext.Builder var3 = (new LootContext.Builder((ServerLevel)this.level)).withParameter(LootContextParams.BLOCK_POS, new BlockPos(this)).withOptionalRandomSeed(this.lootTableSeed);
         if (var1 != null) {
            var3.withLuck(var1.getLuck()).withParameter(LootContextParams.THIS_ENTITY, var1);
         }

         var2.fill(this, var3.create(LootContextParamSets.CHEST));
      }

   }

   public void clearContent() {
      this.unpackLootTable((Player)null);
      this.itemStacks.clear();
   }

   public void setLootTable(ResourceLocation var1, long var2) {
      this.lootTable = var1;
      this.lootTableSeed = var2;
   }

   @Nullable
   public AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3) {
      if (this.lootTable != null && var3.isSpectator()) {
         return null;
      } else {
         this.unpackLootTable(var2.player);
         return this.createMenu(var1, var2);
      }
   }

   protected abstract AbstractContainerMenu createMenu(int var1, Inventory var2);
}
