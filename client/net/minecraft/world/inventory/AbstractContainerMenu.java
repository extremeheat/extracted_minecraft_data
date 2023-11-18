package net.minecraft.world.inventory;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

public abstract class AbstractContainerMenu {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int SLOT_CLICKED_OUTSIDE = -999;
   public static final int QUICKCRAFT_TYPE_CHARITABLE = 0;
   public static final int QUICKCRAFT_TYPE_GREEDY = 1;
   public static final int QUICKCRAFT_TYPE_CLONE = 2;
   public static final int QUICKCRAFT_HEADER_START = 0;
   public static final int QUICKCRAFT_HEADER_CONTINUE = 1;
   public static final int QUICKCRAFT_HEADER_END = 2;
   public static final int CARRIED_SLOT_SIZE = 2147483647;
   private final NonNullList<ItemStack> lastSlots = NonNullList.create();
   public final NonNullList<Slot> slots = NonNullList.create();
   private final List<DataSlot> dataSlots = Lists.newArrayList();
   private ItemStack carried = ItemStack.EMPTY;
   private final NonNullList<ItemStack> remoteSlots = NonNullList.create();
   private final IntList remoteDataSlots = new IntArrayList();
   private ItemStack remoteCarried = ItemStack.EMPTY;
   private int stateId;
   @Nullable
   private final MenuType<?> menuType;
   public final int containerId;
   private int quickcraftType = -1;
   private int quickcraftStatus;
   private final Set<Slot> quickcraftSlots = Sets.newHashSet();
   private final List<ContainerListener> containerListeners = Lists.newArrayList();
   @Nullable
   private ContainerSynchronizer synchronizer;
   private boolean suppressRemoteUpdates;

   protected AbstractContainerMenu(@Nullable MenuType<?> var1, int var2) {
      super();
      this.menuType = var1;
      this.containerId = var2;
   }

   protected static boolean stillValid(ContainerLevelAccess var0, Player var1, Block var2) {
      return var0.evaluate(
         (var2x, var3) -> !var2x.getBlockState(var3).is(var2)
               ? false
               : var1.distanceToSqr((double)var3.getX() + 0.5, (double)var3.getY() + 0.5, (double)var3.getZ() + 0.5) <= 64.0,
         true
      );
   }

   public MenuType<?> getType() {
      if (this.menuType == null) {
         throw new UnsupportedOperationException("Unable to construct this menu by type");
      } else {
         return this.menuType;
      }
   }

   protected static void checkContainerSize(Container var0, int var1) {
      int var2 = var0.getContainerSize();
      if (var2 < var1) {
         throw new IllegalArgumentException("Container size " + var2 + " is smaller than expected " + var1);
      }
   }

   protected static void checkContainerDataCount(ContainerData var0, int var1) {
      int var2 = var0.getCount();
      if (var2 < var1) {
         throw new IllegalArgumentException("Container data count " + var2 + " is smaller than expected " + var1);
      }
   }

   public boolean isValidSlotIndex(int var1) {
      return var1 == -1 || var1 == -999 || var1 < this.slots.size();
   }

   protected Slot addSlot(Slot var1) {
      var1.index = this.slots.size();
      this.slots.add(var1);
      this.lastSlots.add(ItemStack.EMPTY);
      this.remoteSlots.add(ItemStack.EMPTY);
      return var1;
   }

   protected DataSlot addDataSlot(DataSlot var1) {
      this.dataSlots.add(var1);
      this.remoteDataSlots.add(0);
      return var1;
   }

   protected void addDataSlots(ContainerData var1) {
      for(int var2 = 0; var2 < var1.getCount(); ++var2) {
         this.addDataSlot(DataSlot.forContainer(var1, var2));
      }
   }

   public void addSlotListener(ContainerListener var1) {
      if (!this.containerListeners.contains(var1)) {
         this.containerListeners.add(var1);
         this.broadcastChanges();
      }
   }

   public void setSynchronizer(ContainerSynchronizer var1) {
      this.synchronizer = var1;
      this.sendAllDataToRemote();
   }

   public void sendAllDataToRemote() {
      int var1 = 0;

      for(int var2 = this.slots.size(); var1 < var2; ++var1) {
         this.remoteSlots.set(var1, this.slots.get(var1).getItem().copy());
      }

      this.remoteCarried = this.getCarried().copy();
      var1 = 0;

      for(int var4 = this.dataSlots.size(); var1 < var4; ++var1) {
         this.remoteDataSlots.set(var1, this.dataSlots.get(var1).get());
      }

      if (this.synchronizer != null) {
         this.synchronizer.sendInitialData(this, this.remoteSlots, this.remoteCarried, this.remoteDataSlots.toIntArray());
      }
   }

   public void removeSlotListener(ContainerListener var1) {
      this.containerListeners.remove(var1);
   }

   public NonNullList<ItemStack> getItems() {
      NonNullList var1 = NonNullList.create();

      for(Slot var3 : this.slots) {
         var1.add(var3.getItem());
      }

      return var1;
   }

   public void broadcastChanges() {
      for(int var1 = 0; var1 < this.slots.size(); ++var1) {
         ItemStack var2 = this.slots.get(var1).getItem();
         Supplier var3 = Suppliers.memoize(var2::copy);
         this.triggerSlotListeners(var1, var2, var3);
         this.synchronizeSlotToRemote(var1, var2, var3);
      }

      this.synchronizeCarriedToRemote();

      for(int var4 = 0; var4 < this.dataSlots.size(); ++var4) {
         DataSlot var5 = this.dataSlots.get(var4);
         int var6 = var5.get();
         if (var5.checkAndClearUpdateFlag()) {
            this.updateDataSlotListeners(var4, var6);
         }

         this.synchronizeDataSlotToRemote(var4, var6);
      }
   }

   public void broadcastFullState() {
      for(int var1 = 0; var1 < this.slots.size(); ++var1) {
         ItemStack var2 = this.slots.get(var1).getItem();
         this.triggerSlotListeners(var1, var2, var2::copy);
      }

      for(int var3 = 0; var3 < this.dataSlots.size(); ++var3) {
         DataSlot var4 = this.dataSlots.get(var3);
         if (var4.checkAndClearUpdateFlag()) {
            this.updateDataSlotListeners(var3, var4.get());
         }
      }

      this.sendAllDataToRemote();
   }

   private void updateDataSlotListeners(int var1, int var2) {
      for(ContainerListener var4 : this.containerListeners) {
         var4.dataChanged(this, var1, var2);
      }
   }

   private void triggerSlotListeners(int var1, ItemStack var2, java.util.function.Supplier<ItemStack> var3) {
      ItemStack var4 = this.lastSlots.get(var1);
      if (!ItemStack.matches(var4, var2)) {
         ItemStack var5 = (ItemStack)var3.get();
         this.lastSlots.set(var1, var5);

         for(ContainerListener var7 : this.containerListeners) {
            var7.slotChanged(this, var1, var5);
         }
      }
   }

   private void synchronizeSlotToRemote(int var1, ItemStack var2, java.util.function.Supplier<ItemStack> var3) {
      if (!this.suppressRemoteUpdates) {
         ItemStack var4 = this.remoteSlots.get(var1);
         if (!ItemStack.matches(var4, var2)) {
            ItemStack var5 = (ItemStack)var3.get();
            this.remoteSlots.set(var1, var5);
            if (this.synchronizer != null) {
               this.synchronizer.sendSlotChange(this, var1, var5);
            }
         }
      }
   }

   private void synchronizeDataSlotToRemote(int var1, int var2) {
      if (!this.suppressRemoteUpdates) {
         int var3 = this.remoteDataSlots.getInt(var1);
         if (var3 != var2) {
            this.remoteDataSlots.set(var1, var2);
            if (this.synchronizer != null) {
               this.synchronizer.sendDataChange(this, var1, var2);
            }
         }
      }
   }

   private void synchronizeCarriedToRemote() {
      if (!this.suppressRemoteUpdates) {
         if (!ItemStack.matches(this.getCarried(), this.remoteCarried)) {
            this.remoteCarried = this.getCarried().copy();
            if (this.synchronizer != null) {
               this.synchronizer.sendCarriedChange(this, this.remoteCarried);
            }
         }
      }
   }

   public void setRemoteSlot(int var1, ItemStack var2) {
      this.remoteSlots.set(var1, var2.copy());
   }

   public void setRemoteSlotNoCopy(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.remoteSlots.size()) {
         this.remoteSlots.set(var1, var2);
      } else {
         LOGGER.debug("Incorrect slot index: {} available slots: {}", var1, this.remoteSlots.size());
      }
   }

   public void setRemoteCarried(ItemStack var1) {
      this.remoteCarried = var1.copy();
   }

   public boolean clickMenuButton(Player var1, int var2) {
      return false;
   }

   public Slot getSlot(int var1) {
      return this.slots.get(var1);
   }

   public abstract ItemStack quickMoveStack(Player var1, int var2);

   public void clicked(int var1, int var2, ClickType var3, Player var4) {
      try {
         this.doClick(var1, var2, var3, var4);
      } catch (Exception var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Container click");
         CrashReportCategory var7 = var6.addCategory("Click info");
         var7.setDetail("Menu Type", () -> this.menuType != null ? BuiltInRegistries.MENU.getKey(this.menuType).toString() : "<no type>");
         var7.setDetail("Menu Class", () -> this.getClass().getCanonicalName());
         var7.setDetail("Slot Count", this.slots.size());
         var7.setDetail("Slot", var1);
         var7.setDetail("Button", var2);
         var7.setDetail("Type", var3);
         throw new ReportedException(var6);
      }
   }

   private void doClick(int var1, int var2, ClickType var3, Player var4) {
      Inventory var5 = var4.getInventory();
      if (var3 == ClickType.QUICK_CRAFT) {
         int var6 = this.quickcraftStatus;
         this.quickcraftStatus = getQuickcraftHeader(var2);
         if ((var6 != 1 || this.quickcraftStatus != 2) && var6 != this.quickcraftStatus) {
            this.resetQuickCraft();
         } else if (this.getCarried().isEmpty()) {
            this.resetQuickCraft();
         } else if (this.quickcraftStatus == 0) {
            this.quickcraftType = getQuickcraftType(var2);
            if (isValidQuickcraftType(this.quickcraftType, var4)) {
               this.quickcraftStatus = 1;
               this.quickcraftSlots.clear();
            } else {
               this.resetQuickCraft();
            }
         } else if (this.quickcraftStatus == 1) {
            Slot var7 = this.slots.get(var1);
            ItemStack var8 = this.getCarried();
            if (canItemQuickReplace(var7, var8, true)
               && var7.mayPlace(var8)
               && (this.quickcraftType == 2 || var8.getCount() > this.quickcraftSlots.size())
               && this.canDragTo(var7)) {
               this.quickcraftSlots.add(var7);
            }
         } else if (this.quickcraftStatus == 2) {
            if (!this.quickcraftSlots.isEmpty()) {
               if (this.quickcraftSlots.size() == 1) {
                  int var21 = this.quickcraftSlots.iterator().next().index;
                  this.resetQuickCraft();
                  this.doClick(var21, this.quickcraftType, ClickType.PICKUP, var4);
                  return;
               }

               ItemStack var20 = this.getCarried().copy();
               if (var20.isEmpty()) {
                  this.resetQuickCraft();
                  return;
               }

               int var28 = this.getCarried().getCount();

               for(Slot var10 : this.quickcraftSlots) {
                  ItemStack var11 = this.getCarried();
                  if (var10 != null
                     && canItemQuickReplace(var10, var11, true)
                     && var10.mayPlace(var11)
                     && (this.quickcraftType == 2 || var11.getCount() >= this.quickcraftSlots.size())
                     && this.canDragTo(var10)) {
                     int var12 = var10.hasItem() ? var10.getItem().getCount() : 0;
                     int var13 = Math.min(var20.getMaxStackSize(), var10.getMaxStackSize(var20));
                     int var14 = Math.min(getQuickCraftPlaceCount(this.quickcraftSlots, this.quickcraftType, var20) + var12, var13);
                     var28 -= var14 - var12;
                     var10.setByPlayer(var20.copyWithCount(var14));
                  }
               }

               var20.setCount(var28);
               this.setCarried(var20);
            }

            this.resetQuickCraft();
         } else {
            this.resetQuickCraft();
         }
      } else if (this.quickcraftStatus != 0) {
         this.resetQuickCraft();
      } else if ((var3 == ClickType.PICKUP || var3 == ClickType.QUICK_MOVE) && (var2 == 0 || var2 == 1)) {
         ClickAction var19 = var2 == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
         if (var1 == -999) {
            if (!this.getCarried().isEmpty()) {
               if (var19 == ClickAction.PRIMARY) {
                  var4.drop(this.getCarried(), true);
                  this.setCarried(ItemStack.EMPTY);
               } else {
                  var4.drop(this.getCarried().split(1), true);
               }
            }
         } else if (var3 == ClickType.QUICK_MOVE) {
            if (var1 < 0) {
               return;
            }

            Slot var26 = this.slots.get(var1);
            if (!var26.mayPickup(var4)) {
               return;
            }

            ItemStack var32 = this.quickMoveStack(var4, var1);

            while(!var32.isEmpty() && ItemStack.isSameItem(var26.getItem(), var32)) {
               var32 = this.quickMoveStack(var4, var1);
            }
         } else {
            if (var1 < 0) {
               return;
            }

            Slot var27 = this.slots.get(var1);
            ItemStack var33 = var27.getItem();
            ItemStack var37 = this.getCarried();
            var4.updateTutorialInventoryAction(var37, var27.getItem(), var19);
            if (!this.tryItemClickBehaviourOverride(var4, var19, var27, var33, var37)) {
               if (var33.isEmpty()) {
                  if (!var37.isEmpty()) {
                     int var39 = var19 == ClickAction.PRIMARY ? var37.getCount() : 1;
                     this.setCarried(var27.safeInsert(var37, var39));
                  }
               } else if (var27.mayPickup(var4)) {
                  if (var37.isEmpty()) {
                     int var40 = var19 == ClickAction.PRIMARY ? var33.getCount() : (var33.getCount() + 1) / 2;
                     Optional var44 = var27.tryRemove(var40, 2147483647, var4);
                     var44.ifPresent(var3x -> {
                        this.setCarried(var3x);
                        var27.onTake(var4, var3x);
                     });
                  } else if (var27.mayPlace(var37)) {
                     if (ItemStack.isSameItemSameTags(var33, var37)) {
                        int var41 = var19 == ClickAction.PRIMARY ? var37.getCount() : 1;
                        this.setCarried(var27.safeInsert(var37, var41));
                     } else if (var37.getCount() <= var27.getMaxStackSize(var37)) {
                        this.setCarried(var33);
                        var27.setByPlayer(var37);
                     }
                  } else if (ItemStack.isSameItemSameTags(var33, var37)) {
                     Optional var42 = var27.tryRemove(var33.getCount(), var37.getMaxStackSize() - var37.getCount(), var4);
                     var42.ifPresent(var3x -> {
                        var37.grow(var3x.getCount());
                        var27.onTake(var4, var3x);
                     });
                  }
               }
            }

            var27.setChanged();
         }
      } else if (var3 == ClickType.SWAP) {
         Slot var15 = this.slots.get(var1);
         ItemStack var22 = var5.getItem(var2);
         ItemStack var29 = var15.getItem();
         if (!var22.isEmpty() || !var29.isEmpty()) {
            if (var22.isEmpty()) {
               if (var15.mayPickup(var4)) {
                  var5.setItem(var2, var29);
                  var15.onSwapCraft(var29.getCount());
                  var15.setByPlayer(ItemStack.EMPTY);
                  var15.onTake(var4, var29);
               }
            } else if (var29.isEmpty()) {
               if (var15.mayPlace(var22)) {
                  int var34 = var15.getMaxStackSize(var22);
                  if (var22.getCount() > var34) {
                     var15.setByPlayer(var22.split(var34));
                  } else {
                     var5.setItem(var2, ItemStack.EMPTY);
                     var15.setByPlayer(var22);
                  }
               }
            } else if (var15.mayPickup(var4) && var15.mayPlace(var22)) {
               int var35 = var15.getMaxStackSize(var22);
               if (var22.getCount() > var35) {
                  var15.setByPlayer(var22.split(var35));
                  var15.onTake(var4, var29);
                  if (!var5.add(var29)) {
                     var4.drop(var29, true);
                  }
               } else {
                  var5.setItem(var2, var29);
                  var15.setByPlayer(var22);
                  var15.onTake(var4, var29);
               }
            }
         }
      } else if (var3 == ClickType.CLONE && var4.getAbilities().instabuild && this.getCarried().isEmpty() && var1 >= 0) {
         Slot var18 = this.slots.get(var1);
         if (var18.hasItem()) {
            ItemStack var25 = var18.getItem();
            this.setCarried(var25.copyWithCount(var25.getMaxStackSize()));
         }
      } else if (var3 == ClickType.THROW && this.getCarried().isEmpty() && var1 >= 0) {
         Slot var17 = this.slots.get(var1);
         int var24 = var2 == 0 ? 1 : var17.getItem().getCount();
         ItemStack var31 = var17.safeTake(var24, 2147483647, var4);
         var4.drop(var31, true);
      } else if (var3 == ClickType.PICKUP_ALL && var1 >= 0) {
         Slot var16 = this.slots.get(var1);
         ItemStack var23 = this.getCarried();
         if (!var23.isEmpty() && (!var16.hasItem() || !var16.mayPickup(var4))) {
            int var30 = var2 == 0 ? 0 : this.slots.size() - 1;
            int var36 = var2 == 0 ? 1 : -1;

            for(int var38 = 0; var38 < 2; ++var38) {
               for(int var43 = var30; var43 >= 0 && var43 < this.slots.size() && var23.getCount() < var23.getMaxStackSize(); var43 += var36) {
                  Slot var45 = this.slots.get(var43);
                  if (var45.hasItem() && canItemQuickReplace(var45, var23, true) && var45.mayPickup(var4) && this.canTakeItemForPickAll(var23, var45)) {
                     ItemStack var46 = var45.getItem();
                     if (var38 != 0 || var46.getCount() != var46.getMaxStackSize()) {
                        ItemStack var47 = var45.safeTake(var46.getCount(), var23.getMaxStackSize() - var23.getCount(), var4);
                        var23.grow(var47.getCount());
                     }
                  }
               }
            }
         }
      }
   }

   private boolean tryItemClickBehaviourOverride(Player var1, ClickAction var2, Slot var3, ItemStack var4, ItemStack var5) {
      FeatureFlagSet var6 = var1.level().enabledFeatures();
      if (var5.isItemEnabled(var6) && var5.overrideStackedOnOther(var3, var2, var1)) {
         return true;
      } else {
         return var4.isItemEnabled(var6) && var4.overrideOtherStackedOnMe(var5, var3, var2, var1, this.createCarriedSlotAccess());
      }
   }

   private SlotAccess createCarriedSlotAccess() {
      return new SlotAccess() {
         @Override
         public ItemStack get() {
            return AbstractContainerMenu.this.getCarried();
         }

         @Override
         public boolean set(ItemStack var1) {
            AbstractContainerMenu.this.setCarried(var1);
            return true;
         }
      };
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return true;
   }

   public void removed(Player var1) {
      if (var1 instanceof ServerPlayer) {
         ItemStack var2 = this.getCarried();
         if (!var2.isEmpty()) {
            if (var1.isAlive() && !((ServerPlayer)var1).hasDisconnected()) {
               var1.getInventory().placeItemBackInInventory(var2);
            } else {
               var1.drop(var2, false);
            }

            this.setCarried(ItemStack.EMPTY);
         }
      }
   }

   protected void clearContainer(Player var1, Container var2) {
      if (!var1.isAlive() || var1 instanceof ServerPlayer && ((ServerPlayer)var1).hasDisconnected()) {
         for(int var5 = 0; var5 < var2.getContainerSize(); ++var5) {
            var1.drop(var2.removeItemNoUpdate(var5), false);
         }
      } else {
         for(int var3 = 0; var3 < var2.getContainerSize(); ++var3) {
            Inventory var4 = var1.getInventory();
            if (var4.player instanceof ServerPlayer) {
               var4.placeItemBackInInventory(var2.removeItemNoUpdate(var3));
            }
         }
      }
   }

   public void slotsChanged(Container var1) {
      this.broadcastChanges();
   }

   public void setItem(int var1, int var2, ItemStack var3) {
      this.getSlot(var1).set(var3);
      this.stateId = var2;
   }

   public void initializeContents(int var1, List<ItemStack> var2, ItemStack var3) {
      for(int var4 = 0; var4 < var2.size(); ++var4) {
         this.getSlot(var4).set((ItemStack)var2.get(var4));
      }

      this.carried = var3;
      this.stateId = var1;
   }

   public void setData(int var1, int var2) {
      this.dataSlots.get(var1).set(var2);
   }

   public abstract boolean stillValid(Player var1);

   protected boolean moveItemStackTo(ItemStack var1, int var2, int var3, boolean var4) {
      boolean var5 = false;
      int var6 = var2;
      if (var4) {
         var6 = var3 - 1;
      }

      if (var1.isStackable()) {
         while(!var1.isEmpty() && (var4 ? var6 >= var2 : var6 < var3)) {
            Slot var7 = this.slots.get(var6);
            ItemStack var8 = var7.getItem();
            if (!var8.isEmpty() && ItemStack.isSameItemSameTags(var1, var8)) {
               int var9 = var8.getCount() + var1.getCount();
               if (var9 <= var1.getMaxStackSize()) {
                  var1.setCount(0);
                  var8.setCount(var9);
                  var7.setChanged();
                  var5 = true;
               } else if (var8.getCount() < var1.getMaxStackSize()) {
                  var1.shrink(var1.getMaxStackSize() - var8.getCount());
                  var8.setCount(var1.getMaxStackSize());
                  var7.setChanged();
                  var5 = true;
               }
            }

            if (var4) {
               --var6;
            } else {
               ++var6;
            }
         }
      }

      if (!var1.isEmpty()) {
         if (var4) {
            var6 = var3 - 1;
         } else {
            var6 = var2;
         }

         while(var4 ? var6 >= var2 : var6 < var3) {
            Slot var11 = this.slots.get(var6);
            ItemStack var12 = var11.getItem();
            if (var12.isEmpty() && var11.mayPlace(var1)) {
               if (var1.getCount() > var11.getMaxStackSize()) {
                  var11.setByPlayer(var1.split(var11.getMaxStackSize()));
               } else {
                  var11.setByPlayer(var1.split(var1.getCount()));
               }

               var11.setChanged();
               var5 = true;
               break;
            }

            if (var4) {
               --var6;
            } else {
               ++var6;
            }
         }
      }

      return var5;
   }

   public static int getQuickcraftType(int var0) {
      return var0 >> 2 & 3;
   }

   public static int getQuickcraftHeader(int var0) {
      return var0 & 3;
   }

   public static int getQuickcraftMask(int var0, int var1) {
      return var0 & 3 | (var1 & 3) << 2;
   }

   public static boolean isValidQuickcraftType(int var0, Player var1) {
      if (var0 == 0) {
         return true;
      } else if (var0 == 1) {
         return true;
      } else {
         return var0 == 2 && var1.getAbilities().instabuild;
      }
   }

   protected void resetQuickCraft() {
      this.quickcraftStatus = 0;
      this.quickcraftSlots.clear();
   }

   public static boolean canItemQuickReplace(@Nullable Slot var0, ItemStack var1, boolean var2) {
      boolean var3 = var0 == null || !var0.hasItem();
      if (!var3 && ItemStack.isSameItemSameTags(var1, var0.getItem())) {
         return var0.getItem().getCount() + (var2 ? 0 : var1.getCount()) <= var1.getMaxStackSize();
      } else {
         return var3;
      }
   }

   public static int getQuickCraftPlaceCount(Set<Slot> var0, int var1, ItemStack var2) {
      return switch(var1) {
         case 0 -> Mth.floor((float)var2.getCount() / (float)var0.size());
         case 1 -> 1;
         case 2 -> var2.getItem().getMaxStackSize();
         default -> var2.getCount();
      };
   }

   public boolean canDragTo(Slot var1) {
      return true;
   }

   public static int getRedstoneSignalFromBlockEntity(@Nullable BlockEntity var0) {
      return var0 instanceof Container ? getRedstoneSignalFromContainer((Container)var0) : 0;
   }

   public static int getRedstoneSignalFromContainer(@Nullable Container var0) {
      if (var0 == null) {
         return 0;
      } else {
         float var1 = 0.0F;

         for(int var2 = 0; var2 < var0.getContainerSize(); ++var2) {
            ItemStack var3 = var0.getItem(var2);
            if (!var3.isEmpty()) {
               var1 += (float)var3.getCount() / (float)Math.min(var0.getMaxStackSize(), var3.getMaxStackSize());
            }
         }

         var1 /= (float)var0.getContainerSize();
         return Mth.lerpDiscrete(var1, 0, 15);
      }
   }

   public void setCarried(ItemStack var1) {
      this.carried = var1;
   }

   public ItemStack getCarried() {
      return this.carried;
   }

   public void suppressRemoteUpdates() {
      this.suppressRemoteUpdates = true;
   }

   public void resumeRemoteUpdates() {
      this.suppressRemoteUpdates = false;
   }

   public void transferState(AbstractContainerMenu var1) {
      HashBasedTable var2 = HashBasedTable.create();

      for(int var3 = 0; var3 < var1.slots.size(); ++var3) {
         Slot var4 = var1.slots.get(var3);
         var2.put(var4.container, var4.getContainerSlot(), var3);
      }

      for(int var6 = 0; var6 < this.slots.size(); ++var6) {
         Slot var7 = this.slots.get(var6);
         Integer var5 = (Integer)var2.get(var7.container, var7.getContainerSlot());
         if (var5 != null) {
            this.lastSlots.set(var6, var1.lastSlots.get(var5));
            this.remoteSlots.set(var6, var1.remoteSlots.get(var5));
         }
      }
   }

   public OptionalInt findSlot(Container var1, int var2) {
      for(int var3 = 0; var3 < this.slots.size(); ++var3) {
         Slot var4 = this.slots.get(var3);
         if (var4.container == var1 && var2 == var4.getContainerSlot()) {
            return OptionalInt.of(var3);
         }
      }

      return OptionalInt.empty();
   }

   public int getStateId() {
      return this.stateId;
   }

   public int incrementStateId() {
      this.stateId = this.stateId + 1 & 32767;
      return this.stateId;
   }
}
