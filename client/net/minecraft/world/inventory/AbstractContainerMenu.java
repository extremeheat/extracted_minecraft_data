package net.minecraft.world.inventory;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.BundleItem;
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
   public static final int SLOTS_PER_ROW = 9;
   public static final int SLOT_SIZE = 18;
   private final NonNullList<ItemStack> lastSlots = NonNullList.create();
   public final NonNullList<Slot> slots = NonNullList.create();
   private final List<DataSlot> dataSlots = Lists.newArrayList();
   private ItemStack carried;
   private final NonNullList<ItemStack> remoteSlots;
   private final IntList remoteDataSlots;
   private ItemStack remoteCarried;
   private int stateId;
   @Nullable
   private final MenuType<?> menuType;
   public final int containerId;
   private int quickcraftType;
   private int quickcraftStatus;
   private final Set<Slot> quickcraftSlots;
   private final List<ContainerListener> containerListeners;
   @Nullable
   private ContainerSynchronizer synchronizer;
   private boolean suppressRemoteUpdates;

   protected AbstractContainerMenu(@Nullable MenuType<?> var1, int var2) {
      super();
      this.carried = ItemStack.EMPTY;
      this.remoteSlots = NonNullList.create();
      this.remoteDataSlots = new IntArrayList();
      this.remoteCarried = ItemStack.EMPTY;
      this.quickcraftType = -1;
      this.quickcraftSlots = Sets.newHashSet();
      this.containerListeners = Lists.newArrayList();
      this.menuType = var1;
      this.containerId = var2;
   }

   protected void addInventoryHotbarSlots(Container var1, int var2, int var3) {
      for(int var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(var1, var4, var2 + var4 * 18, var3));
      }

   }

   protected void addInventoryExtendedSlots(Container var1, int var2, int var3) {
      for(int var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var1, var5 + (var4 + 1) * 9, var2 + var5 * 18, var3 + var4 * 18));
         }
      }

   }

   protected void addStandardInventorySlots(Container var1, int var2, int var3) {
      this.addInventoryExtendedSlots(var1, var2, var3);
      boolean var4 = true;
      boolean var5 = true;
      this.addInventoryHotbarSlots(var1, var2, var3 + 58);
   }

   protected static boolean stillValid(ContainerLevelAccess var0, Player var1, Block var2) {
      return (Boolean)var0.evaluate((var2x, var3) -> {
         return !var2x.getBlockState(var3).is(var2) ? false : var1.canInteractWithBlock(var3, 4.0);
      }, true);
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

      int var2;
      for(var2 = this.slots.size(); var1 < var2; ++var1) {
         this.remoteSlots.set(var1, ((Slot)this.slots.get(var1)).getItem().copy());
      }

      this.remoteCarried = this.getCarried().copy();
      var1 = 0;

      for(var2 = this.dataSlots.size(); var1 < var2; ++var1) {
         this.remoteDataSlots.set(var1, ((DataSlot)this.dataSlots.get(var1)).get());
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
      Iterator var2 = this.slots.iterator();

      while(var2.hasNext()) {
         Slot var3 = (Slot)var2.next();
         var1.add(var3.getItem());
      }

      return var1;
   }

   public void broadcastChanges() {
      int var1;
      for(var1 = 0; var1 < this.slots.size(); ++var1) {
         ItemStack var2 = ((Slot)this.slots.get(var1)).getItem();
         Objects.requireNonNull(var2);
         Supplier var3 = Suppliers.memoize(var2::copy);
         this.triggerSlotListeners(var1, var2, var3);
         this.synchronizeSlotToRemote(var1, var2, var3);
      }

      this.synchronizeCarriedToRemote();

      for(var1 = 0; var1 < this.dataSlots.size(); ++var1) {
         DataSlot var4 = (DataSlot)this.dataSlots.get(var1);
         int var5 = var4.get();
         if (var4.checkAndClearUpdateFlag()) {
            this.updateDataSlotListeners(var1, var5);
         }

         this.synchronizeDataSlotToRemote(var1, var5);
      }

   }

   public void broadcastFullState() {
      int var1;
      for(var1 = 0; var1 < this.slots.size(); ++var1) {
         ItemStack var2 = ((Slot)this.slots.get(var1)).getItem();
         Objects.requireNonNull(var2);
         this.triggerSlotListeners(var1, var2, var2::copy);
      }

      for(var1 = 0; var1 < this.dataSlots.size(); ++var1) {
         DataSlot var3 = (DataSlot)this.dataSlots.get(var1);
         if (var3.checkAndClearUpdateFlag()) {
            this.updateDataSlotListeners(var1, var3.get());
         }
      }

      this.sendAllDataToRemote();
   }

   private void updateDataSlotListeners(int var1, int var2) {
      Iterator var3 = this.containerListeners.iterator();

      while(var3.hasNext()) {
         ContainerListener var4 = (ContainerListener)var3.next();
         var4.dataChanged(this, var1, var2);
      }

   }

   private void triggerSlotListeners(int var1, ItemStack var2, java.util.function.Supplier<ItemStack> var3) {
      ItemStack var4 = (ItemStack)this.lastSlots.get(var1);
      if (!ItemStack.matches(var4, var2)) {
         ItemStack var5 = (ItemStack)var3.get();
         this.lastSlots.set(var1, var5);
         Iterator var6 = this.containerListeners.iterator();

         while(var6.hasNext()) {
            ContainerListener var7 = (ContainerListener)var6.next();
            var7.slotChanged(this, var1, var5);
         }
      }

   }

   private void synchronizeSlotToRemote(int var1, ItemStack var2, java.util.function.Supplier<ItemStack> var3) {
      if (!this.suppressRemoteUpdates) {
         ItemStack var4 = (ItemStack)this.remoteSlots.get(var1);
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
      return (Slot)this.slots.get(var1);
   }

   public abstract ItemStack quickMoveStack(Player var1, int var2);

   public void setSelectedBundleItemIndex(int var1, int var2) {
      if (var1 >= 0 && var1 < this.slots.size()) {
         ItemStack var3 = ((Slot)this.slots.get(var1)).getItem();
         BundleItem.toggleSelectedItem(var3, var2);
      }

   }

   public void clicked(int var1, int var2, ClickType var3, Player var4) {
      try {
         this.doClick(var1, var2, var3, var4);
      } catch (Exception var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Container click");
         CrashReportCategory var7 = var6.addCategory("Click info");
         var7.setDetail("Menu Type", () -> {
            return this.menuType != null ? BuiltInRegistries.MENU.getKey(this.menuType).toString() : "<no type>";
         });
         var7.setDetail("Menu Class", () -> {
            return this.getClass().getCanonicalName();
         });
         var7.setDetail("Slot Count", (Object)this.slots.size());
         var7.setDetail("Slot", (Object)var1);
         var7.setDetail("Button", (Object)var2);
         var7.setDetail("Type", (Object)var3);
         throw new ReportedException(var6);
      }
   }

   private void doClick(int var1, int var2, ClickType var3, Player var4) {
      Inventory var5 = var4.getInventory();
      Slot var7;
      ItemStack var8;
      ItemStack var17;
      int var19;
      int var20;
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
            var7 = (Slot)this.slots.get(var1);
            var8 = this.getCarried();
            if (canItemQuickReplace(var7, var8, true) && var7.mayPlace(var8) && (this.quickcraftType == 2 || var8.getCount() > this.quickcraftSlots.size()) && this.canDragTo(var7)) {
               this.quickcraftSlots.add(var7);
            }
         } else if (this.quickcraftStatus == 2) {
            if (!this.quickcraftSlots.isEmpty()) {
               if (this.quickcraftSlots.size() == 1) {
                  var19 = ((Slot)this.quickcraftSlots.iterator().next()).index;
                  this.resetQuickCraft();
                  this.doClick(var19, this.quickcraftType, ClickType.PICKUP, var4);
                  return;
               }

               var17 = this.getCarried().copy();
               if (var17.isEmpty()) {
                  this.resetQuickCraft();
                  return;
               }

               var20 = this.getCarried().getCount();
               Iterator var9 = this.quickcraftSlots.iterator();

               label336:
               while(true) {
                  Slot var10;
                  ItemStack var11;
                  do {
                     do {
                        do {
                           do {
                              if (!var9.hasNext()) {
                                 var17.setCount(var20);
                                 this.setCarried(var17);
                                 break label336;
                              }

                              var10 = (Slot)var9.next();
                              var11 = this.getCarried();
                           } while(var10 == null);
                        } while(!canItemQuickReplace(var10, var11, true));
                     } while(!var10.mayPlace(var11));
                  } while(this.quickcraftType != 2 && var11.getCount() < this.quickcraftSlots.size());

                  if (this.canDragTo(var10)) {
                     int var12 = var10.hasItem() ? var10.getItem().getCount() : 0;
                     int var13 = Math.min(var17.getMaxStackSize(), var10.getMaxStackSize(var17));
                     int var14 = Math.min(getQuickCraftPlaceCount(this.quickcraftSlots, this.quickcraftType, var17) + var12, var13);
                     var20 -= var14 - var12;
                     var10.setByPlayer(var17.copyWithCount(var14));
                  }
               }
            }

            this.resetQuickCraft();
         } else {
            this.resetQuickCraft();
         }
      } else if (this.quickcraftStatus != 0) {
         this.resetQuickCraft();
      } else {
         int var23;
         if ((var3 == ClickType.PICKUP || var3 == ClickType.QUICK_MOVE) && (var2 == 0 || var2 == 1)) {
            ClickAction var18 = var2 == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
            if (var1 == -999) {
               if (!this.getCarried().isEmpty()) {
                  if (var18 == ClickAction.PRIMARY) {
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

               var7 = (Slot)this.slots.get(var1);
               if (!var7.mayPickup(var4)) {
                  return;
               }

               for(var8 = this.quickMoveStack(var4, var1); !var8.isEmpty() && ItemStack.isSameItem(var7.getItem(), var8); var8 = this.quickMoveStack(var4, var1)) {
               }
            } else {
               if (var1 < 0) {
                  return;
               }

               var7 = (Slot)this.slots.get(var1);
               var8 = var7.getItem();
               ItemStack var22 = this.getCarried();
               var4.updateTutorialInventoryAction(var22, var7.getItem(), var18);
               if (!this.tryItemClickBehaviourOverride(var4, var18, var7, var8, var22)) {
                  if (var8.isEmpty()) {
                     if (!var22.isEmpty()) {
                        var23 = var18 == ClickAction.PRIMARY ? var22.getCount() : 1;
                        this.setCarried(var7.safeInsert(var22, var23));
                     }
                  } else if (var7.mayPickup(var4)) {
                     if (var22.isEmpty()) {
                        var23 = var18 == ClickAction.PRIMARY ? var8.getCount() : (var8.getCount() + 1) / 2;
                        Optional var25 = var7.tryRemove(var23, 2147483647, var4);
                        var25.ifPresent((var3x) -> {
                           this.setCarried(var3x);
                           var7.onTake(var4, var3x);
                        });
                     } else if (var7.mayPlace(var22)) {
                        if (ItemStack.isSameItemSameComponents(var8, var22)) {
                           var23 = var18 == ClickAction.PRIMARY ? var22.getCount() : 1;
                           this.setCarried(var7.safeInsert(var22, var23));
                        } else if (var22.getCount() <= var7.getMaxStackSize(var22)) {
                           this.setCarried(var8);
                           var7.setByPlayer(var22);
                        }
                     } else if (ItemStack.isSameItemSameComponents(var8, var22)) {
                        Optional var26 = var7.tryRemove(var8.getCount(), var22.getMaxStackSize() - var22.getCount(), var4);
                        var26.ifPresent((var3x) -> {
                           var22.grow(var3x.getCount());
                           var7.onTake(var4, var3x);
                        });
                     }
                  }
               }

               var7.setChanged();
            }
         } else {
            int var21;
            if (var3 == ClickType.SWAP && (var2 >= 0 && var2 < 9 || var2 == 40)) {
               ItemStack var16 = var5.getItem(var2);
               var7 = (Slot)this.slots.get(var1);
               var8 = var7.getItem();
               if (!var16.isEmpty() || !var8.isEmpty()) {
                  if (var16.isEmpty()) {
                     if (var7.mayPickup(var4)) {
                        var5.setItem(var2, var8);
                        var7.onSwapCraft(var8.getCount());
                        var7.setByPlayer(ItemStack.EMPTY);
                        var7.onTake(var4, var8);
                     }
                  } else if (var8.isEmpty()) {
                     if (var7.mayPlace(var16)) {
                        var21 = var7.getMaxStackSize(var16);
                        if (var16.getCount() > var21) {
                           var7.setByPlayer(var16.split(var21));
                        } else {
                           var5.setItem(var2, ItemStack.EMPTY);
                           var7.setByPlayer(var16);
                        }
                     }
                  } else if (var7.mayPickup(var4) && var7.mayPlace(var16)) {
                     var21 = var7.getMaxStackSize(var16);
                     if (var16.getCount() > var21) {
                        var7.setByPlayer(var16.split(var21));
                        var7.onTake(var4, var8);
                        if (!var5.add(var8)) {
                           var4.drop(var8, true);
                        }
                     } else {
                        var5.setItem(var2, var8);
                        var7.setByPlayer(var16);
                        var7.onTake(var4, var8);
                     }
                  }
               }
            } else {
               Slot var15;
               if (var3 == ClickType.CLONE && var4.hasInfiniteMaterials() && this.getCarried().isEmpty() && var1 >= 0) {
                  var15 = (Slot)this.slots.get(var1);
                  if (var15.hasItem()) {
                     var17 = var15.getItem();
                     this.setCarried(var17.copyWithCount(var17.getMaxStackSize()));
                  }
               } else if (var3 == ClickType.THROW && this.getCarried().isEmpty() && var1 >= 0) {
                  var15 = (Slot)this.slots.get(var1);
                  var19 = var2 == 0 ? 1 : var15.getItem().getCount();
                  if (!var4.canDropItems()) {
                     return;
                  }

                  var8 = var15.safeTake(var19, 2147483647, var4);
                  var4.drop(var8, true);
                  var4.handleCreativeModeItemDrop(var8);
                  if (var2 == 1) {
                     while(!var8.isEmpty() && ItemStack.isSameItem(var15.getItem(), var8)) {
                        if (!var4.canDropItems()) {
                           return;
                        }

                        var8 = var15.safeTake(var19, 2147483647, var4);
                        var4.drop(var8, true);
                        var4.handleCreativeModeItemDrop(var8);
                     }
                  }
               } else if (var3 == ClickType.PICKUP_ALL && var1 >= 0) {
                  var15 = (Slot)this.slots.get(var1);
                  var17 = this.getCarried();
                  if (!var17.isEmpty() && (!var15.hasItem() || !var15.mayPickup(var4))) {
                     var20 = var2 == 0 ? 0 : this.slots.size() - 1;
                     var21 = var2 == 0 ? 1 : -1;

                     for(var23 = 0; var23 < 2; ++var23) {
                        for(int var24 = var20; var24 >= 0 && var24 < this.slots.size() && var17.getCount() < var17.getMaxStackSize(); var24 += var21) {
                           Slot var27 = (Slot)this.slots.get(var24);
                           if (var27.hasItem() && canItemQuickReplace(var27, var17, true) && var27.mayPickup(var4) && this.canTakeItemForPickAll(var17, var27)) {
                              ItemStack var28 = var27.getItem();
                              if (var23 != 0 || var28.getCount() != var28.getMaxStackSize()) {
                                 ItemStack var29 = var27.safeTake(var28.getCount(), var17.getMaxStackSize() - var17.getCount(), var4);
                                 var17.grow(var29.getCount());
                              }
                           }
                        }
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
         public ItemStack get() {
            return AbstractContainerMenu.this.getCarried();
         }

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
            dropOrPlaceInInventory(var1, var2);
            this.setCarried(ItemStack.EMPTY);
         }

      }
   }

   private static void dropOrPlaceInInventory(Player var0, ItemStack var1) {
      boolean var10000;
      boolean var2;
      label27: {
         var2 = var0.isRemoved() && var0.getRemovalReason() != Entity.RemovalReason.CHANGED_DIMENSION;
         if (var0 instanceof ServerPlayer var4) {
            if (var4.hasDisconnected()) {
               var10000 = true;
               break label27;
            }
         }

         var10000 = false;
      }

      boolean var3 = var10000;
      if (!var2 && !var3) {
         if (var0 instanceof ServerPlayer) {
            var0.getInventory().placeItemBackInInventory(var1);
         }
      } else {
         var0.drop(var1, false);
      }

   }

   protected void clearContainer(Player var1, Container var2) {
      for(int var3 = 0; var3 < var2.getContainerSize(); ++var3) {
         dropOrPlaceInInventory(var1, var2.removeItemNoUpdate(var3));
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
      ((DataSlot)this.dataSlots.get(var1)).set(var2);
   }

   public abstract boolean stillValid(Player var1);

   protected boolean moveItemStackTo(ItemStack var1, int var2, int var3, boolean var4) {
      boolean var5 = false;
      int var6 = var2;
      if (var4) {
         var6 = var3 - 1;
      }

      Slot var7;
      ItemStack var8;
      int var9;
      if (var1.isStackable()) {
         while(!var1.isEmpty()) {
            if (var4) {
               if (var6 < var2) {
                  break;
               }
            } else if (var6 >= var3) {
               break;
            }

            var7 = (Slot)this.slots.get(var6);
            var8 = var7.getItem();
            if (!var8.isEmpty() && ItemStack.isSameItemSameComponents(var1, var8)) {
               var9 = var8.getCount() + var1.getCount();
               int var10 = var7.getMaxStackSize(var8);
               if (var9 <= var10) {
                  var1.setCount(0);
                  var8.setCount(var9);
                  var7.setChanged();
                  var5 = true;
               } else if (var8.getCount() < var10) {
                  var1.shrink(var10 - var8.getCount());
                  var8.setCount(var10);
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

         while(true) {
            if (var4) {
               if (var6 < var2) {
                  break;
               }
            } else if (var6 >= var3) {
               break;
            }

            var7 = (Slot)this.slots.get(var6);
            var8 = var7.getItem();
            if (var8.isEmpty() && var7.mayPlace(var1)) {
               var9 = var7.getMaxStackSize(var1);
               var7.setByPlayer(var1.split(Math.min(var1.getCount(), var9)));
               var7.setChanged();
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
         return var0 == 2 && var1.hasInfiniteMaterials();
      }
   }

   protected void resetQuickCraft() {
      this.quickcraftStatus = 0;
      this.quickcraftSlots.clear();
   }

   public static boolean canItemQuickReplace(@Nullable Slot var0, ItemStack var1, boolean var2) {
      boolean var3 = var0 == null || !var0.hasItem();
      if (!var3 && ItemStack.isSameItemSameComponents(var1, var0.getItem())) {
         return var0.getItem().getCount() + (var2 ? 0 : var1.getCount()) <= var1.getMaxStackSize();
      } else {
         return var3;
      }
   }

   public static int getQuickCraftPlaceCount(Set<Slot> var0, int var1, ItemStack var2) {
      int var10000;
      switch (var1) {
         case 0 -> var10000 = Mth.floor((float)var2.getCount() / (float)var0.size());
         case 1 -> var10000 = 1;
         case 2 -> var10000 = var2.getMaxStackSize();
         default -> var10000 = var2.getCount();
      }

      return var10000;
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
               var1 += (float)var3.getCount() / (float)var0.getMaxStackSize(var3);
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

      int var3;
      Slot var4;
      for(var3 = 0; var3 < var1.slots.size(); ++var3) {
         var4 = (Slot)var1.slots.get(var3);
         var2.put(var4.container, var4.getContainerSlot(), var3);
      }

      for(var3 = 0; var3 < this.slots.size(); ++var3) {
         var4 = (Slot)this.slots.get(var3);
         Integer var5 = (Integer)var2.get(var4.container, var4.getContainerSlot());
         if (var5 != null) {
            this.lastSlots.set(var3, (ItemStack)var1.lastSlots.get(var5));
            this.remoteSlots.set(var3, (ItemStack)var1.remoteSlots.get(var5));
         }
      }

   }

   public OptionalInt findSlot(Container var1, int var2) {
      for(int var3 = 0; var3 < this.slots.size(); ++var3) {
         Slot var4 = (Slot)this.slots.get(var3);
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
