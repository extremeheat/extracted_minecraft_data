package net.minecraft.world.entity.player;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class Inventory implements Container, Nameable {
   public static final int POP_TIME_DURATION = 5;
   public static final int INVENTORY_SIZE = 36;
   public static final int SELECTION_SIZE = 9;
   public static final int SLOT_OFFHAND = 40;
   public static final int NOT_FOUND_INDEX = -1;
   public final NonNullList<ItemStack> items = NonNullList.withSize(36, ItemStack.EMPTY);
   public final NonNullList<ItemStack> armor = NonNullList.withSize(4, ItemStack.EMPTY);
   public final NonNullList<ItemStack> offhand = NonNullList.withSize(1, ItemStack.EMPTY);
   private final List<NonNullList<ItemStack>> compartments = ImmutableList.of(this.items, this.armor, this.offhand);
   public int selected;
   public final Player player;
   private int timesChanged;

   public Inventory(Player var1) {
      super();
      this.player = var1;
   }

   public ItemStack getSelected() {
      return isHotbarSlot(this.selected) ? this.items.get(this.selected) : ItemStack.EMPTY;
   }

   public static int getSelectionSize() {
      return 9;
   }

   private boolean hasRemainingSpaceForItem(ItemStack var1, ItemStack var2) {
      return !var1.isEmpty() && ItemStack.isSameItemSameComponents(var1, var2) && var1.isStackable() && var1.getCount() < this.getMaxStackSize(var1);
   }

   public int getFreeSlot() {
      for (int var1 = 0; var1 < this.items.size(); var1++) {
         if (this.items.get(var1).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   public void setPickedItem(ItemStack var1) {
      int var2 = this.findSlotMatchingItem(var1);
      if (isHotbarSlot(var2)) {
         this.selected = var2;
      } else {
         if (var2 == -1) {
            this.selected = this.getSuitableHotbarSlot();
            if (!this.items.get(this.selected).isEmpty()) {
               int var3 = this.getFreeSlot();
               if (var3 != -1) {
                  this.items.set(var3, this.items.get(this.selected));
               }
            }

            this.items.set(this.selected, var1);
         } else {
            this.pickSlot(var2);
         }
      }
   }

   public void pickSlot(int var1) {
      this.selected = this.getSuitableHotbarSlot();
      ItemStack var2 = this.items.get(this.selected);
      this.items.set(this.selected, this.items.get(var1));
      this.items.set(var1, var2);
   }

   public static boolean isHotbarSlot(int var0) {
      return var0 >= 0 && var0 < 9;
   }

   public int findSlotMatchingItem(ItemStack var1) {
      for (int var2 = 0; var2 < this.items.size(); var2++) {
         if (!this.items.get(var2).isEmpty() && ItemStack.isSameItemSameComponents(var1, this.items.get(var2))) {
            return var2;
         }
      }

      return -1;
   }

   public static boolean isUsableForCrafting(ItemStack var0) {
      return !var0.isDamaged() && !var0.isEnchanted() && !var0.has(DataComponents.CUSTOM_NAME);
   }

   public int findSlotMatchingCraftingIngredient(Holder<Item> var1) {
      for (int var2 = 0; var2 < this.items.size(); var2++) {
         ItemStack var3 = this.items.get(var2);
         if (!var3.isEmpty() && var3.is(var1) && isUsableForCrafting(var3)) {
            return var2;
         }
      }

      return -1;
   }

   public int getSuitableHotbarSlot() {
      for (int var1 = 0; var1 < 9; var1++) {
         int var2 = (this.selected + var1) % 9;
         if (this.items.get(var2).isEmpty()) {
            return var2;
         }
      }

      for (int var3 = 0; var3 < 9; var3++) {
         int var4 = (this.selected + var3) % 9;
         if (!this.items.get(var4).isEnchanted()) {
            return var4;
         }
      }

      return this.selected;
   }

   public void setSelectedHotbarSlot(int var1) {
      this.selected = var1;
   }

   public int clearOrCountMatchingItems(Predicate<ItemStack> var1, int var2, Container var3) {
      int var4 = 0;
      boolean var5 = var2 == 0;
      var4 += ContainerHelper.clearOrCountMatchingItems(this, var1, var2 - var4, var5);
      var4 += ContainerHelper.clearOrCountMatchingItems(var3, var1, var2 - var4, var5);
      ItemStack var6 = this.player.containerMenu.getCarried();
      var4 += ContainerHelper.clearOrCountMatchingItems(var6, var1, var2 - var4, var5);
      if (var6.isEmpty()) {
         this.player.containerMenu.setCarried(ItemStack.EMPTY);
      }

      return var4;
   }

   private int addResource(ItemStack var1) {
      int var2 = this.getSlotWithRemainingSpace(var1);
      if (var2 == -1) {
         var2 = this.getFreeSlot();
      }

      return var2 == -1 ? var1.getCount() : this.addResource(var2, var1);
   }

   private int addResource(int var1, ItemStack var2) {
      int var3 = var2.getCount();
      ItemStack var4 = this.getItem(var1);
      if (var4.isEmpty()) {
         var4 = var2.copyWithCount(0);
         this.setItem(var1, var4);
      }

      int var5 = this.getMaxStackSize(var4) - var4.getCount();
      int var6 = Math.min(var3, var5);
      if (var6 == 0) {
         return var3;
      } else {
         var3 -= var6;
         var4.grow(var6);
         var4.setPopTime(5);
         return var3;
      }
   }

   public int getSlotWithRemainingSpace(ItemStack var1) {
      if (this.hasRemainingSpaceForItem(this.getItem(this.selected), var1)) {
         return this.selected;
      } else if (this.hasRemainingSpaceForItem(this.getItem(40), var1)) {
         return 40;
      } else {
         for (int var2 = 0; var2 < this.items.size(); var2++) {
            if (this.hasRemainingSpaceForItem(this.items.get(var2), var1)) {
               return var2;
            }
         }

         return -1;
      }
   }

   public void tick() {
      for (NonNullList var2 : this.compartments) {
         for (int var3 = 0; var3 < var2.size(); var3++) {
            if (!((ItemStack)var2.get(var3)).isEmpty()) {
               ((ItemStack)var2.get(var3)).inventoryTick(this.player.level(), this.player, var3, this.selected == var3);
            }
         }
      }
   }

   public boolean add(ItemStack var1) {
      return this.add(-1, var1);
   }

   public boolean add(int var1, ItemStack var2) {
      if (var2.isEmpty()) {
         return false;
      } else {
         try {
            if (var2.isDamaged()) {
               if (var1 == -1) {
                  var1 = this.getFreeSlot();
               }

               if (var1 >= 0) {
                  this.items.set(var1, var2.copyAndClear());
                  this.items.get(var1).setPopTime(5);
                  return true;
               } else if (this.player.hasInfiniteMaterials()) {
                  var2.setCount(0);
                  return true;
               } else {
                  return false;
               }
            } else {
               int var3;
               do {
                  var3 = var2.getCount();
                  if (var1 == -1) {
                     var2.setCount(this.addResource(var2));
                  } else {
                     var2.setCount(this.addResource(var1, var2));
                  }
               } while (!var2.isEmpty() && var2.getCount() < var3);

               if (var2.getCount() == var3 && this.player.hasInfiniteMaterials()) {
                  var2.setCount(0);
                  return true;
               } else {
                  return var2.getCount() < var3;
               }
            }
         } catch (Throwable var6) {
            CrashReport var4 = CrashReport.forThrowable(var6, "Adding item to inventory");
            CrashReportCategory var5 = var4.addCategory("Item being added");
            var5.setDetail("Item ID", Item.getId(var2.getItem()));
            var5.setDetail("Item data", var2.getDamageValue());
            var5.setDetail("Item name", () -> var2.getHoverName().getString());
            throw new ReportedException(var4);
         }
      }
   }

   public void placeItemBackInInventory(ItemStack var1) {
      this.placeItemBackInInventory(var1, true);
   }

   public void placeItemBackInInventory(ItemStack var1, boolean var2) {
      while (!var1.isEmpty()) {
         int var3 = this.getSlotWithRemainingSpace(var1);
         if (var3 == -1) {
            var3 = this.getFreeSlot();
         }

         if (var3 == -1) {
            this.player.drop(var1, false);
            break;
         }

         int var4 = var1.getMaxStackSize() - this.getItem(var3).getCount();
         if (this.add(var3, var1.split(var4)) && var2 && this.player instanceof ServerPlayer var5) {
            var5.connection.send(this.createInventoryUpdatePacket(var3));
         }
      }
   }

   public ClientboundSetPlayerInventoryPacket createInventoryUpdatePacket(int var1) {
      return new ClientboundSetPlayerInventoryPacket(var1, this.getItem(var1).copy());
   }

   @Override
   public ItemStack removeItem(int var1, int var2) {
      NonNullList var3 = null;

      for (NonNullList var5 : this.compartments) {
         if (var1 < var5.size()) {
            var3 = var5;
            break;
         }

         var1 -= var5.size();
      }

      return var3 != null && !((ItemStack)var3.get(var1)).isEmpty() ? ContainerHelper.removeItem(var3, var1, var2) : ItemStack.EMPTY;
   }

   public void removeItem(ItemStack var1) {
      for (NonNullList var3 : this.compartments) {
         for (int var4 = 0; var4 < var3.size(); var4++) {
            if (var3.get(var4) == var1) {
               var3.set(var4, ItemStack.EMPTY);
               break;
            }
         }
      }
   }

   @Override
   public ItemStack removeItemNoUpdate(int var1) {
      NonNullList var2 = null;

      for (NonNullList var4 : this.compartments) {
         if (var1 < var4.size()) {
            var2 = var4;
            break;
         }

         var1 -= var4.size();
      }

      if (var2 != null && !((ItemStack)var2.get(var1)).isEmpty()) {
         ItemStack var5 = (ItemStack)var2.get(var1);
         var2.set(var1, ItemStack.EMPTY);
         return var5;
      } else {
         return ItemStack.EMPTY;
      }
   }

   @Override
   public void setItem(int var1, ItemStack var2) {
      NonNullList var3 = null;

      for (NonNullList var5 : this.compartments) {
         if (var1 < var5.size()) {
            var3 = var5;
            break;
         }

         var1 -= var5.size();
      }

      if (var3 != null) {
         var3.set(var1, var2);
      }
   }

   public float getDestroySpeed(BlockState var1) {
      return this.items.get(this.selected).getDestroySpeed(var1);
   }

   public ListTag save(ListTag var1) {
      for (int var2 = 0; var2 < this.items.size(); var2++) {
         if (!this.items.get(var2).isEmpty()) {
            CompoundTag var3 = new CompoundTag();
            var3.putByte("Slot", (byte)var2);
            var1.add(this.items.get(var2).save(this.player.registryAccess(), var3));
         }
      }

      for (int var4 = 0; var4 < this.armor.size(); var4++) {
         if (!this.armor.get(var4).isEmpty()) {
            CompoundTag var6 = new CompoundTag();
            var6.putByte("Slot", (byte)(var4 + 100));
            var1.add(this.armor.get(var4).save(this.player.registryAccess(), var6));
         }
      }

      for (int var5 = 0; var5 < this.offhand.size(); var5++) {
         if (!this.offhand.get(var5).isEmpty()) {
            CompoundTag var7 = new CompoundTag();
            var7.putByte("Slot", (byte)(var5 + 150));
            var1.add(this.offhand.get(var5).save(this.player.registryAccess(), var7));
         }
      }

      return var1;
   }

   public void load(ListTag var1) {
      this.items.clear();
      this.armor.clear();
      this.offhand.clear();

      for (int var2 = 0; var2 < var1.size(); var2++) {
         CompoundTag var3 = var1.getCompound(var2);
         int var4 = var3.getByte("Slot") & 255;
         ItemStack var5 = ItemStack.parse(this.player.registryAccess(), var3).orElse(ItemStack.EMPTY);
         if (var4 >= 0 && var4 < this.items.size()) {
            this.items.set(var4, var5);
         } else if (var4 >= 100 && var4 < this.armor.size() + 100) {
            this.armor.set(var4 - 100, var5);
         } else if (var4 >= 150 && var4 < this.offhand.size() + 150) {
            this.offhand.set(var4 - 150, var5);
         }
      }
   }

   @Override
   public int getContainerSize() {
      return this.items.size() + this.armor.size() + this.offhand.size();
   }

   @Override
   public boolean isEmpty() {
      for (ItemStack var2 : this.items) {
         if (!var2.isEmpty()) {
            return false;
         }
      }

      for (ItemStack var5 : this.armor) {
         if (!var5.isEmpty()) {
            return false;
         }
      }

      for (ItemStack var6 : this.offhand) {
         if (!var6.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public ItemStack getItem(int var1) {
      NonNullList var2 = null;

      for (NonNullList var4 : this.compartments) {
         if (var1 < var4.size()) {
            var2 = var4;
            break;
         }

         var1 -= var4.size();
      }

      return var2 == null ? ItemStack.EMPTY : (ItemStack)var2.get(var1);
   }

   @Override
   public Component getName() {
      return Component.translatable("container.inventory");
   }

   public ItemStack getArmor(int var1) {
      return this.armor.get(var1);
   }

   public void dropAll() {
      for (List var2 : this.compartments) {
         for (int var3 = 0; var3 < var2.size(); var3++) {
            ItemStack var4 = (ItemStack)var2.get(var3);
            if (!var4.isEmpty()) {
               this.player.drop(var4, true, false);
               var2.set(var3, ItemStack.EMPTY);
            }
         }
      }
   }

   @Override
   public void setChanged() {
      this.timesChanged++;
   }

   public int getTimesChanged() {
      return this.timesChanged;
   }

   @Override
   public boolean stillValid(Player var1) {
      return var1.canInteractWithEntity(this.player, 4.0);
   }

   public boolean contains(ItemStack var1) {
      for (List var3 : this.compartments) {
         for (ItemStack var5 : var3) {
            if (!var5.isEmpty() && ItemStack.isSameItemSameComponents(var5, var1)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean contains(TagKey<Item> var1) {
      for (List var3 : this.compartments) {
         for (ItemStack var5 : var3) {
            if (!var5.isEmpty() && var5.is(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean contains(Predicate<ItemStack> var1) {
      for (List var3 : this.compartments) {
         for (ItemStack var5 : var3) {
            if (var1.test(var5)) {
               return true;
            }
         }
      }

      return false;
   }

   public void replaceWith(Inventory var1) {
      for (int var2 = 0; var2 < this.getContainerSize(); var2++) {
         this.setItem(var2, var1.getItem(var2));
      }

      this.selected = var1.selected;
   }

   @Override
   public void clearContent() {
      for (List var2 : this.compartments) {
         var2.clear();
      }
   }

   public void fillStackedContents(StackedItemContents var1) {
      for (ItemStack var3 : this.items) {
         var1.accountSimpleStack(var3);
      }
   }

   public ItemStack removeFromSelected(boolean var1) {
      ItemStack var2 = this.getSelected();
      return var2.isEmpty() ? ItemStack.EMPTY : this.removeItem(this.selected, var1 ? var2.getCount() : 1);
   }
}
