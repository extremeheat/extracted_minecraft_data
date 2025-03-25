package net.minecraft.world.entity.player;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
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
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Inventory implements Container, Nameable {
   public static final int POP_TIME_DURATION = 5;
   public static final int INVENTORY_SIZE = 36;
   public static final int SELECTION_SIZE = 9;
   public static final int SLOT_OFFHAND = 40;
   public static final int NOT_FOUND_INDEX = -1;
   public static final Int2ObjectMap<EquipmentSlot> EQUIPMENT_SLOT_MAPPING;
   private final NonNullList<ItemStack> items;
   private int selected;
   public final Player player;
   private final EntityEquipment equipment;
   private int timesChanged;

   public Inventory(Player var1, EntityEquipment var2) {
      super();
      this.items = NonNullList.<ItemStack>withSize(36, ItemStack.EMPTY);
      this.player = var1;
      this.equipment = var2;
   }

   public int getSelectedSlot() {
      return this.selected;
   }

   public void setSelectedSlot(int var1) {
      if (!isHotbarSlot(var1)) {
         throw new IllegalArgumentException("Invalid selected slot");
      } else {
         this.selected = var1;
      }
   }

   public ItemStack getSelectedItem() {
      return this.items.get(this.selected);
   }

   public ItemStack setSelectedItem(ItemStack var1) {
      return this.items.set(this.selected, var1);
   }

   public static int getSelectionSize() {
      return 9;
   }

   public NonNullList<ItemStack> getNonEquipmentItems() {
      return this.items;
   }

   private boolean hasRemainingSpaceForItem(ItemStack var1, ItemStack var2) {
      return !var1.isEmpty() && ItemStack.isSameItemSameComponents(var1, var2) && var1.isStackable() && var1.getCount() < this.getMaxStackSize(var1);
   }

   public int getFreeSlot() {
      for(int var1 = 0; var1 < this.items.size(); ++var1) {
         if (((ItemStack)this.items.get(var1)).isEmpty()) {
            return var1;
         }
      }

      return -1;
   }

   public void addAndPickItem(ItemStack var1) {
      this.setSelectedSlot(this.getSuitableHotbarSlot());
      if (!((ItemStack)this.items.get(this.selected)).isEmpty()) {
         int var2 = this.getFreeSlot();
         if (var2 != -1) {
            this.items.set(var2, this.items.get(this.selected));
         }
      }

      this.items.set(this.selected, var1);
   }

   public void pickSlot(int var1) {
      this.setSelectedSlot(this.getSuitableHotbarSlot());
      ItemStack var2 = this.items.get(this.selected);
      this.items.set(this.selected, this.items.get(var1));
      this.items.set(var1, var2);
   }

   public static boolean isHotbarSlot(int var0) {
      return var0 >= 0 && var0 < 9;
   }

   public int findSlotMatchingItem(ItemStack var1) {
      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         if (!((ItemStack)this.items.get(var2)).isEmpty() && ItemStack.isSameItemSameComponents(var1, this.items.get(var2))) {
            return var2;
         }
      }

      return -1;
   }

   public static boolean isUsableForCrafting(ItemStack var0) {
      return !var0.isDamaged() && !var0.isEnchanted() && !var0.has(DataComponents.CUSTOM_NAME);
   }

   public int findSlotMatchingCraftingIngredient(Holder<Item> var1, ItemStack var2) {
      for(int var3 = 0; var3 < this.items.size(); ++var3) {
         ItemStack var4 = this.items.get(var3);
         if (!var4.isEmpty() && var4.is(var1) && isUsableForCrafting(var4) && (var2.isEmpty() || ItemStack.isSameItemSameComponents(var2, var4))) {
            return var3;
         }
      }

      return -1;
   }

   public int getSuitableHotbarSlot() {
      for(int var1 = 0; var1 < 9; ++var1) {
         int var2 = (this.selected + var1) % 9;
         if (((ItemStack)this.items.get(var2)).isEmpty()) {
            return var2;
         }
      }

      for(int var3 = 0; var3 < 9; ++var3) {
         int var4 = (this.selected + var3) % 9;
         if (!((ItemStack)this.items.get(var4)).isEnchanted()) {
            return var4;
         }
      }

      return this.selected;
   }

   public int clearOrCountMatchingItems(Predicate<ItemStack> var1, int var2, Container var3) {
      int var4 = 0;
      boolean var5 = var2 == 0;
      var4 += ContainerHelper.clearOrCountMatchingItems((Container)this, var1, var2 - var4, var5);
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
         for(int var2 = 0; var2 < this.items.size(); ++var2) {
            if (this.hasRemainingSpaceForItem(this.items.get(var2), var1)) {
               return var2;
            }
         }

         return -1;
      }
   }

   public void tick() {
      for(int var1 = 0; var1 < this.items.size(); ++var1) {
         ItemStack var2 = this.getItem(var1);
         if (!var2.isEmpty()) {
            var2.inventoryTick(this.player.level(), this.player, var1 == this.selected ? EquipmentSlot.MAINHAND : null);
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
                  ((ItemStack)this.items.get(var1)).setPopTime(5);
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
               } while(!var2.isEmpty() && var2.getCount() < var3);

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
            var5.setDetail("Item name", (CrashReportDetail)(() -> var2.getHoverName().getString()));
            throw new ReportedException(var4);
         }
      }
   }

   public void placeItemBackInInventory(ItemStack var1) {
      this.placeItemBackInInventory(var1, true);
   }

   public void placeItemBackInInventory(ItemStack var1, boolean var2) {
      while(true) {
         if (!var1.isEmpty()) {
            int var3 = this.getSlotWithRemainingSpace(var1);
            if (var3 == -1) {
               var3 = this.getFreeSlot();
            }

            if (var3 != -1) {
               int var4 = var1.getMaxStackSize() - this.getItem(var3).getCount();
               if (!this.add(var3, var1.split(var4)) || !var2) {
                  continue;
               }

               Player var6 = this.player;
               if (var6 instanceof ServerPlayer) {
                  ServerPlayer var5 = (ServerPlayer)var6;
                  var5.connection.send(this.createInventoryUpdatePacket(var3));
               }
               continue;
            }

            this.player.drop(var1, false);
         }

         return;
      }
   }

   public ClientboundSetPlayerInventoryPacket createInventoryUpdatePacket(int var1) {
      return new ClientboundSetPlayerInventoryPacket(var1, this.getItem(var1).copy());
   }

   public ItemStack removeItem(int var1, int var2) {
      if (var1 < this.items.size()) {
         return ContainerHelper.removeItem(this.items, var1, var2);
      } else {
         EquipmentSlot var3 = (EquipmentSlot)EQUIPMENT_SLOT_MAPPING.get(var1);
         if (var3 != null) {
            ItemStack var4 = this.equipment.get(var3);
            if (!var4.isEmpty()) {
               return var4.split(var2);
            }
         }

         return ItemStack.EMPTY;
      }
   }

   public void removeItem(ItemStack var1) {
      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         if (this.items.get(var2) == var1) {
            this.items.set(var2, ItemStack.EMPTY);
            return;
         }
      }

      ObjectIterator var5 = EQUIPMENT_SLOT_MAPPING.values().iterator();

      while(var5.hasNext()) {
         EquipmentSlot var3 = (EquipmentSlot)var5.next();
         ItemStack var4 = this.equipment.get(var3);
         if (var4 == var1) {
            this.equipment.set(var3, ItemStack.EMPTY);
            return;
         }
      }

   }

   public ItemStack removeItemNoUpdate(int var1) {
      if (var1 < this.items.size()) {
         ItemStack var3 = this.items.get(var1);
         this.items.set(var1, ItemStack.EMPTY);
         return var3;
      } else {
         EquipmentSlot var2 = (EquipmentSlot)EQUIPMENT_SLOT_MAPPING.get(var1);
         return var2 != null ? this.equipment.set(var2, ItemStack.EMPTY) : ItemStack.EMPTY;
      }
   }

   public void setItem(int var1, ItemStack var2) {
      if (var1 < this.items.size()) {
         this.items.set(var1, var2);
      }

      EquipmentSlot var3 = (EquipmentSlot)EQUIPMENT_SLOT_MAPPING.get(var1);
      if (var3 != null) {
         this.equipment.set(var3, var2);
      }

   }

   public ListTag save(ListTag var1) {
      for(int var2 = 0; var2 < this.items.size(); ++var2) {
         if (!((ItemStack)this.items.get(var2)).isEmpty()) {
            CompoundTag var3 = new CompoundTag();
            var3.putByte("Slot", (byte)var2);
            var1.add(((ItemStack)this.items.get(var2)).save(this.player.registryAccess(), var3));
         }
      }

      return var1;
   }

   public void load(ListTag var1) {
      this.items.clear();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         CompoundTag var3 = var1.getCompoundOrEmpty(var2);
         int var4 = var3.getByteOr("Slot", (byte)0) & 255;
         ItemStack var5 = (ItemStack)ItemStack.parse(this.player.registryAccess(), var3).orElse(ItemStack.EMPTY);
         if (var4 < this.items.size()) {
            this.setItem(var4, var5);
         }
      }

   }

   public int getContainerSize() {
      return this.items.size() + EQUIPMENT_SLOT_MAPPING.size();
   }

   public boolean isEmpty() {
      for(ItemStack var2 : this.items) {
         if (!var2.isEmpty()) {
            return false;
         }
      }

      ObjectIterator var3 = EQUIPMENT_SLOT_MAPPING.values().iterator();

      while(var3.hasNext()) {
         EquipmentSlot var4 = (EquipmentSlot)var3.next();
         if (!this.equipment.get(var4).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int var1) {
      if (var1 < this.items.size()) {
         return this.items.get(var1);
      } else {
         EquipmentSlot var2 = (EquipmentSlot)EQUIPMENT_SLOT_MAPPING.get(var1);
         return var2 != null ? this.equipment.get(var2) : ItemStack.EMPTY;
      }
   }

   public Component getName() {
      return Component.translatable("container.inventory");
   }

   public void dropAll() {
      for(int var1 = 0; var1 < this.items.size(); ++var1) {
         ItemStack var2 = this.items.get(var1);
         if (!var2.isEmpty()) {
            this.player.drop(var2, true, false);
            this.items.set(var1, ItemStack.EMPTY);
         }
      }

      this.equipment.dropAll(this.player);
   }

   public void setChanged() {
      ++this.timesChanged;
   }

   public int getTimesChanged() {
      return this.timesChanged;
   }

   public boolean stillValid(Player var1) {
      return true;
   }

   public boolean contains(ItemStack var1) {
      for(ItemStack var3 : this) {
         if (!var3.isEmpty() && ItemStack.isSameItemSameComponents(var3, var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(TagKey<Item> var1) {
      for(ItemStack var3 : this) {
         if (!var3.isEmpty() && var3.is(var1)) {
            return true;
         }
      }

      return false;
   }

   public boolean contains(Predicate<ItemStack> var1) {
      for(ItemStack var3 : this) {
         if (var1.test(var3)) {
            return true;
         }
      }

      return false;
   }

   public void replaceWith(Inventory var1) {
      for(int var2 = 0; var2 < this.getContainerSize(); ++var2) {
         this.setItem(var2, var1.getItem(var2));
      }

      this.setSelectedSlot(var1.getSelectedSlot());
   }

   public void clearContent() {
      this.items.clear();
      this.equipment.clear();
   }

   public void fillStackedContents(StackedItemContents var1) {
      for(ItemStack var3 : this.items) {
         var1.accountSimpleStack(var3);
      }

   }

   public ItemStack removeFromSelected(boolean var1) {
      ItemStack var2 = this.getSelectedItem();
      return var2.isEmpty() ? ItemStack.EMPTY : this.removeItem(this.selected, var1 ? var2.getCount() : 1);
   }

   static {
      EQUIPMENT_SLOT_MAPPING = new Int2ObjectArrayMap(Map.of(EquipmentSlot.FEET.getIndex(36), EquipmentSlot.FEET, EquipmentSlot.LEGS.getIndex(36), EquipmentSlot.LEGS, EquipmentSlot.CHEST.getIndex(36), EquipmentSlot.CHEST, EquipmentSlot.HEAD.getIndex(36), EquipmentSlot.HEAD, 40, EquipmentSlot.OFFHAND));
   }
}
