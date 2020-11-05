package net.minecraft.world.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractContainerMenu {
   private final NonNullList<ItemStack> lastSlots = NonNullList.create();
   public final NonNullList<Slot> slots = NonNullList.create();
   private final List<DataSlot> dataSlots = Lists.newArrayList();
   @Nullable
   private final MenuType<?> menuType;
   public final int containerId;
   private short changeUid;
   private int quickcraftType = -1;
   private int quickcraftStatus;
   private final Set<Slot> quickcraftSlots = Sets.newHashSet();
   private final List<ContainerListener> containerListeners = Lists.newArrayList();
   private final Set<Player> unSynchedPlayers = Sets.newHashSet();

   protected AbstractContainerMenu(@Nullable MenuType<?> var1, int var2) {
      super();
      this.menuType = var1;
      this.containerId = var2;
   }

   protected static boolean stillValid(ContainerLevelAccess var0, Player var1, Block var2) {
      return (Boolean)var0.evaluate((var2x, var3) -> {
         return !var2x.getBlockState(var3).is(var2) ? false : var1.distanceToSqr((double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D) <= 64.0D;
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

   protected Slot addSlot(Slot var1) {
      var1.index = this.slots.size();
      this.slots.add(var1);
      this.lastSlots.add(ItemStack.EMPTY);
      return var1;
   }

   protected DataSlot addDataSlot(DataSlot var1) {
      this.dataSlots.add(var1);
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
         var1.refreshContainer(this, this.getItems());
         this.broadcastChanges();
      }
   }

   public void removeSlotListener(ContainerListener var1) {
      this.containerListeners.remove(var1);
   }

   public NonNullList<ItemStack> getItems() {
      NonNullList var1 = NonNullList.create();

      for(int var2 = 0; var2 < this.slots.size(); ++var2) {
         var1.add(((Slot)this.slots.get(var2)).getItem());
      }

      return var1;
   }

   public void broadcastChanges() {
      int var1;
      for(var1 = 0; var1 < this.slots.size(); ++var1) {
         ItemStack var2 = ((Slot)this.slots.get(var1)).getItem();
         ItemStack var3 = (ItemStack)this.lastSlots.get(var1);
         if (!ItemStack.matches(var3, var2)) {
            ItemStack var4 = var2.copy();
            this.lastSlots.set(var1, var4);
            Iterator var5 = this.containerListeners.iterator();

            while(var5.hasNext()) {
               ContainerListener var6 = (ContainerListener)var5.next();
               var6.slotChanged(this, var1, var4);
            }
         }
      }

      for(var1 = 0; var1 < this.dataSlots.size(); ++var1) {
         DataSlot var7 = (DataSlot)this.dataSlots.get(var1);
         if (var7.checkAndClearUpdateFlag()) {
            Iterator var8 = this.containerListeners.iterator();

            while(var8.hasNext()) {
               ContainerListener var9 = (ContainerListener)var8.next();
               var9.setContainerData(this, var1, var7.get());
            }
         }
      }

   }

   public boolean clickMenuButton(Player var1, int var2) {
      return false;
   }

   public Slot getSlot(int var1) {
      return (Slot)this.slots.get(var1);
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      return ((Slot)this.slots.get(var2)).getItem();
   }

   public ItemStack clicked(int var1, int var2, ClickType var3, Player var4) {
      try {
         return this.doClick(var1, var2, var3, var4);
      } catch (Exception var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Container click");
         CrashReportCategory var7 = var6.addCategory("Click info");
         var7.setDetail("Menu Type", () -> {
            return this.menuType != null ? Registry.MENU.getKey(this.menuType).toString() : "<no type>";
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

   private ItemStack doClick(int var1, int var2, ClickType var3, Player var4) {
      ItemStack var5 = ItemStack.EMPTY;
      Inventory var6 = var4.getInventory();
      Slot var8;
      ItemStack var9;
      int var15;
      ItemStack var19;
      int var20;
      if (var3 == ClickType.QUICK_CRAFT) {
         int var7 = this.quickcraftStatus;
         this.quickcraftStatus = getQuickcraftHeader(var2);
         if ((var7 != 1 || this.quickcraftStatus != 2) && var7 != this.quickcraftStatus) {
            this.resetQuickCraft();
         } else if (var6.getCarried().isEmpty()) {
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
            var8 = (Slot)this.slots.get(var1);
            var9 = var6.getCarried();
            if (canItemQuickReplace(var8, var9, true) && var8.mayPlace(var9) && (this.quickcraftType == 2 || var9.getCount() > this.quickcraftSlots.size()) && this.canDragTo(var8)) {
               this.quickcraftSlots.add(var8);
            }
         } else if (this.quickcraftStatus == 2) {
            if (!this.quickcraftSlots.isEmpty()) {
               var19 = var6.getCarried().copy();
               var20 = var6.getCarried().getCount();
               Iterator var10 = this.quickcraftSlots.iterator();

               label340:
               while(true) {
                  Slot var11;
                  ItemStack var12;
                  do {
                     do {
                        do {
                           do {
                              if (!var10.hasNext()) {
                                 var19.setCount(var20);
                                 var6.setCarried(var19);
                                 break label340;
                              }

                              var11 = (Slot)var10.next();
                              var12 = var6.getCarried();
                           } while(var11 == null);
                        } while(!canItemQuickReplace(var11, var12, true));
                     } while(!var11.mayPlace(var12));
                  } while(this.quickcraftType != 2 && var12.getCount() < this.quickcraftSlots.size());

                  if (this.canDragTo(var11)) {
                     ItemStack var13 = var19.copy();
                     int var14 = var11.hasItem() ? var11.getItem().getCount() : 0;
                     getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, var13, var14);
                     var15 = Math.min(var13.getMaxStackSize(), var11.getMaxStackSize(var13));
                     if (var13.getCount() > var15) {
                        var13.setCount(var15);
                     }

                     var20 -= var13.getCount() - var14;
                     var11.set(var13);
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
         if (var3 != ClickType.PICKUP && var3 != ClickType.QUICK_MOVE || var2 != 0 && var2 != 1) {
            Slot var18;
            int var22;
            if (var3 == ClickType.SWAP) {
               var18 = (Slot)this.slots.get(var1);
               var19 = var6.getItem(var2);
               var9 = var18.getItem();
               if (!var19.isEmpty() || !var9.isEmpty()) {
                  if (var19.isEmpty()) {
                     if (var18.mayPickup(var4)) {
                        var6.setItem(var2, var9);
                        var18.onSwapCraft(var9.getCount());
                        var18.set(ItemStack.EMPTY);
                        var18.onTake(var4, var9);
                     }
                  } else if (var9.isEmpty()) {
                     if (var18.mayPlace(var19)) {
                        var22 = var18.getMaxStackSize(var19);
                        if (var19.getCount() > var22) {
                           var18.set(var19.split(var22));
                        } else {
                           var18.set(var19);
                           var6.setItem(var2, ItemStack.EMPTY);
                        }
                     }
                  } else if (var18.mayPickup(var4) && var18.mayPlace(var19)) {
                     var22 = var18.getMaxStackSize(var19);
                     if (var19.getCount() > var22) {
                        var18.set(var19.split(var22));
                        var18.onTake(var4, var9);
                        if (!var6.add(var9)) {
                           var4.drop(var9, true);
                        }
                     } else {
                        var18.set(var19);
                        var6.setItem(var2, var9);
                        var18.onTake(var4, var9);
                     }
                  }
               }
            } else if (var3 == ClickType.CLONE && var4.getAbilities().instabuild && var6.getCarried().isEmpty() && var1 >= 0) {
               var18 = (Slot)this.slots.get(var1);
               if (var18.hasItem()) {
                  var19 = var18.getItem().copy();
                  var19.setCount(var19.getMaxStackSize());
                  var6.setCarried(var19);
               }
            } else if (var3 == ClickType.THROW && var6.getCarried().isEmpty() && var1 >= 0) {
               var18 = (Slot)this.slots.get(var1);
               if (var18.hasItem() && var18.mayPickup(var4)) {
                  var19 = var18.remove(var2 == 0 ? 1 : var18.getItem().getCount());
                  var18.onTake(var4, var19);
                  var4.drop(var19, true);
               }
            } else if (var3 == ClickType.PICKUP_ALL && var1 >= 0) {
               var18 = (Slot)this.slots.get(var1);
               var19 = var6.getCarried();
               if (!var19.isEmpty() && (!var18.hasItem() || !var18.mayPickup(var4))) {
                  var20 = var2 == 0 ? 0 : this.slots.size() - 1;
                  var22 = var2 == 0 ? 1 : -1;

                  for(var23 = 0; var23 < 2; ++var23) {
                     for(int var24 = var20; var24 >= 0 && var24 < this.slots.size() && var19.getCount() < var19.getMaxStackSize(); var24 += var22) {
                        Slot var25 = (Slot)this.slots.get(var24);
                        if (var25.hasItem() && canItemQuickReplace(var25, var19, true) && var25.mayPickup(var4) && this.canTakeItemForPickAll(var19, var25)) {
                           ItemStack var26 = var25.getItem();
                           if (var23 != 0 || var26.getCount() != var26.getMaxStackSize()) {
                              var15 = Math.min(var19.getMaxStackSize() - var19.getCount(), var26.getCount());
                              ItemStack var16 = var25.remove(var15);
                              var19.grow(var15);
                              if (var16.isEmpty()) {
                                 var25.set(ItemStack.EMPTY);
                              }

                              var25.onTake(var4, var16);
                           }
                        }
                     }
                  }
               }

               this.broadcastChanges();
            }
         } else {
            ClickAction var17 = var2 == 0 ? ClickAction.PRIMARY : ClickAction.SECONDARY;
            if (var1 == -999) {
               if (!var6.getCarried().isEmpty()) {
                  if (var17 == ClickAction.PRIMARY) {
                     var4.drop(var6.getCarried(), true);
                     var6.setCarried(ItemStack.EMPTY);
                  } else {
                     var4.drop(var6.getCarried().split(1), true);
                  }
               }
            } else if (var3 == ClickType.QUICK_MOVE) {
               if (var1 < 0) {
                  return ItemStack.EMPTY;
               }

               var8 = (Slot)this.slots.get(var1);
               if (!var8.mayPickup(var4)) {
                  return ItemStack.EMPTY;
               }

               for(var9 = this.quickMoveStack(var4, var1); !var9.isEmpty() && ItemStack.isSame(var8.getItem(), var9); var9 = this.quickMoveStack(var4, var1)) {
                  var5 = var9.copy();
               }
            } else {
               if (var1 < 0) {
                  return ItemStack.EMPTY;
               }

               var8 = (Slot)this.slots.get(var1);
               var9 = var8.getItem();
               ItemStack var21 = var6.getCarried();
               if (!var9.isEmpty()) {
                  var5 = var9.copy();
               }

               if (var9.isEmpty()) {
                  if (!var21.isEmpty() && var8.mayPlace(var21)) {
                     var23 = var17 == ClickAction.PRIMARY ? var21.getCount() : 1;
                     if (var23 > var8.getMaxStackSize(var21)) {
                        var23 = var8.getMaxStackSize(var21);
                     }

                     var8.set(var21.split(var23));
                  }
               } else if (var8.mayPickup(var4)) {
                  if (var21.isEmpty()) {
                     if (!var9.overrideOtherStackedOnMe(var21, var17, var6)) {
                        var23 = var17 == ClickAction.PRIMARY ? var9.getCount() : (var9.getCount() + 1) / 2;
                        var6.setCarried(var8.remove(var23));
                        if (var9.isEmpty()) {
                           var8.set(ItemStack.EMPTY);
                        }

                        var8.onTake(var4, var6.getCarried());
                     }
                  } else if (var8.mayPlace(var21)) {
                     if (!var21.overrideStackedOnOther(var9, var17, var6) && !var9.overrideOtherStackedOnMe(var21, var17, var6)) {
                        if (consideredTheSameItem(var9, var21)) {
                           var23 = var17 == ClickAction.PRIMARY ? var21.getCount() : 1;
                           if (var23 > var8.getMaxStackSize(var21) - var9.getCount()) {
                              var23 = var8.getMaxStackSize(var21) - var9.getCount();
                           }

                           if (var23 > var21.getMaxStackSize() - var9.getCount()) {
                              var23 = var21.getMaxStackSize() - var9.getCount();
                           }

                           var21.shrink(var23);
                           var9.grow(var23);
                        } else if (var21.getCount() <= var8.getMaxStackSize(var21)) {
                           var8.set(var21);
                           var6.setCarried(var9);
                        }
                     }
                  } else if (consideredTheSameItem(var9, var21)) {
                     var23 = var9.getCount();
                     if (var23 + var21.getCount() <= var21.getMaxStackSize()) {
                        var21.grow(var23);
                        var8.remove(var23);
                        var8.set(ItemStack.EMPTY);
                        var8.onTake(var4, var6.getCarried());
                     }
                  }
               }

               var8.setChanged();
            }
         }
      }

      return var5;
   }

   public static boolean consideredTheSameItem(ItemStack var0, ItemStack var1) {
      return var0.is(var1.getItem()) && ItemStack.tagMatches(var0, var1);
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return true;
   }

   public void removed(Player var1) {
      Inventory var2 = var1.getInventory();
      if (!var2.getCarried().isEmpty()) {
         var1.drop(var2.getCarried(), false);
         var2.setCarried(ItemStack.EMPTY);
      }

   }

   protected void clearContainer(Player var1, Container var2) {
      int var3;
      if (!var1.isAlive() || var1 instanceof ServerPlayer && ((ServerPlayer)var1).hasDisconnected()) {
         for(var3 = 0; var3 < var2.getContainerSize(); ++var3) {
            var1.drop(var2.removeItemNoUpdate(var3), false);
         }

      } else {
         for(var3 = 0; var3 < var2.getContainerSize(); ++var3) {
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

   public void setItem(int var1, ItemStack var2) {
      this.getSlot(var1).set(var2);
   }

   public void setAll(List<ItemStack> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.getSlot(var2).set((ItemStack)var1.get(var2));
      }

   }

   public void setData(int var1, int var2) {
      ((DataSlot)this.dataSlots.get(var1)).set(var2);
   }

   public short backup(Inventory var1) {
      ++this.changeUid;
      return this.changeUid;
   }

   public boolean isSynched(Player var1) {
      return !this.unSynchedPlayers.contains(var1);
   }

   public void setSynched(Player var1, boolean var2) {
      if (var2) {
         this.unSynchedPlayers.remove(var1);
      } else {
         this.unSynchedPlayers.add(var1);
      }

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
            if (!var8.isEmpty() && consideredTheSameItem(var1, var8)) {
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
               if (var1.getCount() > var7.getMaxStackSize()) {
                  var7.set(var1.split(var7.getMaxStackSize()));
               } else {
                  var7.set(var1.split(var1.getCount()));
               }

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
         return var0 == 2 && var1.getAbilities().instabuild;
      }
   }

   protected void resetQuickCraft() {
      this.quickcraftStatus = 0;
      this.quickcraftSlots.clear();
   }

   public static boolean canItemQuickReplace(@Nullable Slot var0, ItemStack var1, boolean var2) {
      boolean var3 = var0 == null || !var0.hasItem();
      if (!var3 && var1.sameItem(var0.getItem()) && ItemStack.tagMatches(var0.getItem(), var1)) {
         return var0.getItem().getCount() + (var2 ? 0 : var1.getCount()) <= var1.getMaxStackSize();
      } else {
         return var3;
      }
   }

   public static void getQuickCraftSlotCount(Set<Slot> var0, int var1, ItemStack var2, int var3) {
      switch(var1) {
      case 0:
         var2.setCount(Mth.floor((float)var2.getCount() / (float)var0.size()));
         break;
      case 1:
         var2.setCount(1);
         break;
      case 2:
         var2.setCount(var2.getItem().getMaxStackSize());
      }

      var2.grow(var3);
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
         int var1 = 0;
         float var2 = 0.0F;

         for(int var3 = 0; var3 < var0.getContainerSize(); ++var3) {
            ItemStack var4 = var0.getItem(var3);
            if (!var4.isEmpty()) {
               var2 += (float)var4.getCount() / (float)Math.min(var0.getMaxStackSize(), var4.getMaxStackSize());
               ++var1;
            }
         }

         var2 /= (float)var0.getContainerSize();
         return Mth.floor(var2 * 14.0F) + (var1 > 0 ? 1 : 0);
      }
   }
}
