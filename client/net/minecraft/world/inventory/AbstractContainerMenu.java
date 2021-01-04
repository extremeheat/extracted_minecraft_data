package net.minecraft.world.inventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractContainerMenu {
   private final NonNullList<ItemStack> lastSlots = NonNullList.create();
   public final List<Slot> slots = Lists.newArrayList();
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
         return var2x.getBlockState(var3).getBlock() != var2 ? false : var1.distanceToSqr((double)var3.getX() + 0.5D, (double)var3.getY() + 0.5D, (double)var3.getZ() + 0.5D) <= 64.0D;
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
            var3 = var2.isEmpty() ? ItemStack.EMPTY : var2.copy();
            this.lastSlots.set(var1, var3);
            Iterator var4 = this.containerListeners.iterator();

            while(var4.hasNext()) {
               ContainerListener var5 = (ContainerListener)var4.next();
               var5.slotChanged(this, var1, var3);
            }
         }
      }

      for(var1 = 0; var1 < this.dataSlots.size(); ++var1) {
         DataSlot var6 = (DataSlot)this.dataSlots.get(var1);
         if (var6.checkAndClearUpdateFlag()) {
            Iterator var7 = this.containerListeners.iterator();

            while(var7.hasNext()) {
               ContainerListener var8 = (ContainerListener)var7.next();
               var8.setContainerData(this, var1, var6.get());
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
      Slot var3 = (Slot)this.slots.get(var2);
      return var3 != null ? var3.getItem() : ItemStack.EMPTY;
   }

   public ItemStack clicked(int var1, int var2, ClickType var3, Player var4) {
      ItemStack var5 = ItemStack.EMPTY;
      Inventory var6 = var4.inventory;
      ItemStack var8;
      ItemStack var9;
      int var15;
      int var18;
      if (var3 == ClickType.QUICK_CRAFT) {
         int var17 = this.quickcraftStatus;
         this.quickcraftStatus = getQuickcraftHeader(var2);
         if ((var17 != 1 || this.quickcraftStatus != 2) && var17 != this.quickcraftStatus) {
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
            Slot var19 = (Slot)this.slots.get(var1);
            var9 = var6.getCarried();
            if (var19 != null && canItemQuickReplace(var19, var9, true) && var19.mayPlace(var9) && (this.quickcraftType == 2 || var9.getCount() > this.quickcraftSlots.size()) && this.canDragTo(var19)) {
               this.quickcraftSlots.add(var19);
            }
         } else if (this.quickcraftStatus == 2) {
            if (!this.quickcraftSlots.isEmpty()) {
               var8 = var6.getCarried().copy();
               var18 = var6.getCarried().getCount();
               Iterator var23 = this.quickcraftSlots.iterator();

               label342:
               while(true) {
                  Slot var20;
                  ItemStack var21;
                  do {
                     do {
                        do {
                           do {
                              if (!var23.hasNext()) {
                                 var8.setCount(var18);
                                 var6.setCarried(var8);
                                 break label342;
                              }

                              var20 = (Slot)var23.next();
                              var21 = var6.getCarried();
                           } while(var20 == null);
                        } while(!canItemQuickReplace(var20, var21, true));
                     } while(!var20.mayPlace(var21));
                  } while(this.quickcraftType != 2 && var21.getCount() < this.quickcraftSlots.size());

                  if (this.canDragTo(var20)) {
                     ItemStack var22 = var8.copy();
                     int var24 = var20.hasItem() ? var20.getItem().getCount() : 0;
                     getQuickCraftSlotCount(this.quickcraftSlots, this.quickcraftType, var22, var24);
                     var15 = Math.min(var22.getMaxStackSize(), var20.getMaxStackSize(var22));
                     if (var22.getCount() > var15) {
                        var22.setCount(var15);
                     }

                     var18 -= var22.getCount() - var24;
                     var20.set(var22);
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
         Slot var7;
         int var10;
         if (var3 != ClickType.PICKUP && var3 != ClickType.QUICK_MOVE || var2 != 0 && var2 != 1) {
            if (var3 == ClickType.SWAP && var2 >= 0 && var2 < 9) {
               var7 = (Slot)this.slots.get(var1);
               var8 = var6.getItem(var2);
               var9 = var7.getItem();
               if (!var8.isEmpty() || !var9.isEmpty()) {
                  if (var8.isEmpty()) {
                     if (var7.mayPickup(var4)) {
                        var6.setItem(var2, var9);
                        var7.onSwapCraft(var9.getCount());
                        var7.set(ItemStack.EMPTY);
                        var7.onTake(var4, var9);
                     }
                  } else if (var9.isEmpty()) {
                     if (var7.mayPlace(var8)) {
                        var10 = var7.getMaxStackSize(var8);
                        if (var8.getCount() > var10) {
                           var7.set(var8.split(var10));
                        } else {
                           var7.set(var8);
                           var6.setItem(var2, ItemStack.EMPTY);
                        }
                     }
                  } else if (var7.mayPickup(var4) && var7.mayPlace(var8)) {
                     var10 = var7.getMaxStackSize(var8);
                     if (var8.getCount() > var10) {
                        var7.set(var8.split(var10));
                        var7.onTake(var4, var9);
                        if (!var6.add(var9)) {
                           var4.drop(var9, true);
                        }
                     } else {
                        var7.set(var8);
                        var6.setItem(var2, var9);
                        var7.onTake(var4, var9);
                     }
                  }
               }
            } else if (var3 == ClickType.CLONE && var4.abilities.instabuild && var6.getCarried().isEmpty() && var1 >= 0) {
               var7 = (Slot)this.slots.get(var1);
               if (var7 != null && var7.hasItem()) {
                  var8 = var7.getItem().copy();
                  var8.setCount(var8.getMaxStackSize());
                  var6.setCarried(var8);
               }
            } else if (var3 == ClickType.THROW && var6.getCarried().isEmpty() && var1 >= 0) {
               var7 = (Slot)this.slots.get(var1);
               if (var7 != null && var7.hasItem() && var7.mayPickup(var4)) {
                  var8 = var7.remove(var2 == 0 ? 1 : var7.getItem().getCount());
                  var7.onTake(var4, var8);
                  var4.drop(var8, true);
               }
            } else if (var3 == ClickType.PICKUP_ALL && var1 >= 0) {
               var7 = (Slot)this.slots.get(var1);
               var8 = var6.getCarried();
               if (!var8.isEmpty() && (var7 == null || !var7.hasItem() || !var7.mayPickup(var4))) {
                  var18 = var2 == 0 ? 0 : this.slots.size() - 1;
                  var10 = var2 == 0 ? 1 : -1;

                  for(int var11 = 0; var11 < 2; ++var11) {
                     for(int var12 = var18; var12 >= 0 && var12 < this.slots.size() && var8.getCount() < var8.getMaxStackSize(); var12 += var10) {
                        Slot var13 = (Slot)this.slots.get(var12);
                        if (var13.hasItem() && canItemQuickReplace(var13, var8, true) && var13.mayPickup(var4) && this.canTakeItemForPickAll(var8, var13)) {
                           ItemStack var14 = var13.getItem();
                           if (var11 != 0 || var14.getCount() != var14.getMaxStackSize()) {
                              var15 = Math.min(var8.getMaxStackSize() - var8.getCount(), var14.getCount());
                              ItemStack var16 = var13.remove(var15);
                              var8.grow(var15);
                              if (var16.isEmpty()) {
                                 var13.set(ItemStack.EMPTY);
                              }

                              var13.onTake(var4, var16);
                           }
                        }
                     }
                  }
               }

               this.broadcastChanges();
            }
         } else if (var1 == -999) {
            if (!var6.getCarried().isEmpty()) {
               if (var2 == 0) {
                  var4.drop(var6.getCarried(), true);
                  var6.setCarried(ItemStack.EMPTY);
               }

               if (var2 == 1) {
                  var4.drop(var6.getCarried().split(1), true);
               }
            }
         } else if (var3 == ClickType.QUICK_MOVE) {
            if (var1 < 0) {
               return ItemStack.EMPTY;
            }

            var7 = (Slot)this.slots.get(var1);
            if (var7 == null || !var7.mayPickup(var4)) {
               return ItemStack.EMPTY;
            }

            for(var8 = this.quickMoveStack(var4, var1); !var8.isEmpty() && ItemStack.isSame(var7.getItem(), var8); var8 = this.quickMoveStack(var4, var1)) {
               var5 = var8.copy();
            }
         } else {
            if (var1 < 0) {
               return ItemStack.EMPTY;
            }

            var7 = (Slot)this.slots.get(var1);
            if (var7 != null) {
               var8 = var7.getItem();
               var9 = var6.getCarried();
               if (!var8.isEmpty()) {
                  var5 = var8.copy();
               }

               if (var8.isEmpty()) {
                  if (!var9.isEmpty() && var7.mayPlace(var9)) {
                     var10 = var2 == 0 ? var9.getCount() : 1;
                     if (var10 > var7.getMaxStackSize(var9)) {
                        var10 = var7.getMaxStackSize(var9);
                     }

                     var7.set(var9.split(var10));
                  }
               } else if (var7.mayPickup(var4)) {
                  if (var9.isEmpty()) {
                     if (var8.isEmpty()) {
                        var7.set(ItemStack.EMPTY);
                        var6.setCarried(ItemStack.EMPTY);
                     } else {
                        var10 = var2 == 0 ? var8.getCount() : (var8.getCount() + 1) / 2;
                        var6.setCarried(var7.remove(var10));
                        if (var8.isEmpty()) {
                           var7.set(ItemStack.EMPTY);
                        }

                        var7.onTake(var4, var6.getCarried());
                     }
                  } else if (var7.mayPlace(var9)) {
                     if (consideredTheSameItem(var8, var9)) {
                        var10 = var2 == 0 ? var9.getCount() : 1;
                        if (var10 > var7.getMaxStackSize(var9) - var8.getCount()) {
                           var10 = var7.getMaxStackSize(var9) - var8.getCount();
                        }

                        if (var10 > var9.getMaxStackSize() - var8.getCount()) {
                           var10 = var9.getMaxStackSize() - var8.getCount();
                        }

                        var9.shrink(var10);
                        var8.grow(var10);
                     } else if (var9.getCount() <= var7.getMaxStackSize(var9)) {
                        var7.set(var9);
                        var6.setCarried(var8);
                     }
                  } else if (var9.getMaxStackSize() > 1 && consideredTheSameItem(var8, var9) && !var8.isEmpty()) {
                     var10 = var8.getCount();
                     if (var10 + var9.getCount() <= var9.getMaxStackSize()) {
                        var9.grow(var10);
                        var8 = var7.remove(var10);
                        if (var8.isEmpty()) {
                           var7.set(ItemStack.EMPTY);
                        }

                        var7.onTake(var4, var6.getCarried());
                     }
                  }
               }

               var7.setChanged();
            }
         }
      }

      return var5;
   }

   public static boolean consideredTheSameItem(ItemStack var0, ItemStack var1) {
      return var0.getItem() == var1.getItem() && ItemStack.tagMatches(var0, var1);
   }

   public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
      return true;
   }

   public void removed(Player var1) {
      Inventory var2 = var1.inventory;
      if (!var2.getCarried().isEmpty()) {
         var1.drop(var2.getCarried(), false);
         var2.setCarried(ItemStack.EMPTY);
      }

   }

   protected void clearContainer(Player var1, Level var2, Container var3) {
      int var4;
      if (!var1.isAlive() || var1 instanceof ServerPlayer && ((ServerPlayer)var1).hasDisconnected()) {
         for(var4 = 0; var4 < var3.getContainerSize(); ++var4) {
            var1.drop(var3.removeItemNoUpdate(var4), false);
         }

      } else {
         for(var4 = 0; var4 < var3.getContainerSize(); ++var4) {
            var1.inventory.placeItemBackInInventory(var2, var3.removeItemNoUpdate(var4));
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
         return var0 == 2 && var1.abilities.instabuild;
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
