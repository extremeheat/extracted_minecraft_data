package net.minecraft.world.inventory;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class LoomMenu extends AbstractContainerMenu {
   private static final int INV_SLOT_START = 4;
   private static final int INV_SLOT_END = 31;
   private static final int USE_ROW_SLOT_START = 31;
   private static final int USE_ROW_SLOT_END = 40;
   private final ContainerLevelAccess access;
   final DataSlot selectedBannerPatternIndex;
   Runnable slotUpdateListener;
   final Slot bannerSlot;
   final Slot dyeSlot;
   private final Slot patternSlot;
   private final Slot resultSlot;
   long lastSoundTime;
   private final Container inputContainer;
   private final Container outputContainer;

   public LoomMenu(int var1, Inventory var2) {
      this(var1, var2, ContainerLevelAccess.NULL);
   }

   public LoomMenu(int var1, Inventory var2, final ContainerLevelAccess var3) {
      super(MenuType.LOOM, var1);
      this.selectedBannerPatternIndex = DataSlot.standalone();
      this.slotUpdateListener = () -> {
      };
      this.inputContainer = new SimpleContainer(3) {
         public void setChanged() {
            super.setChanged();
            LoomMenu.this.slotsChanged(this);
            LoomMenu.this.slotUpdateListener.run();
         }
      };
      this.outputContainer = new SimpleContainer(1) {
         public void setChanged() {
            super.setChanged();
            LoomMenu.this.slotUpdateListener.run();
         }
      };
      this.access = var3;
      this.bannerSlot = this.addSlot(new Slot(this.inputContainer, 0, 13, 26) {
         public boolean mayPlace(ItemStack var1) {
            return var1.getItem() instanceof BannerItem;
         }
      });
      this.dyeSlot = this.addSlot(new Slot(this.inputContainer, 1, 33, 26) {
         public boolean mayPlace(ItemStack var1) {
            return var1.getItem() instanceof DyeItem;
         }
      });
      this.patternSlot = this.addSlot(new Slot(this.inputContainer, 2, 23, 45) {
         public boolean mayPlace(ItemStack var1) {
            return var1.getItem() instanceof BannerPatternItem;
         }
      });
      this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 58) {
         public boolean mayPlace(ItemStack var1) {
            return false;
         }

         public void onTake(Player var1, ItemStack var2) {
            LoomMenu.this.bannerSlot.remove(1);
            LoomMenu.this.dyeSlot.remove(1);
            if (!LoomMenu.this.bannerSlot.hasItem() || !LoomMenu.this.dyeSlot.hasItem()) {
               LoomMenu.this.selectedBannerPatternIndex.set(0);
            }

            var3.execute((var1x, var2x) -> {
               long var3x = var1x.getGameTime();
               if (LoomMenu.this.lastSoundTime != var3x) {
                  var1x.playSound((Player)null, (BlockPos)var2x, SoundEvents.UI_LOOM_TAKE_RESULT, SoundSource.BLOCKS, 1.0F, 1.0F);
                  LoomMenu.this.lastSoundTime = var3x;
               }

            });
            super.onTake(var1, var2);
         }
      });

      int var4;
      for(var4 = 0; var4 < 3; ++var4) {
         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var2, var5 + var4 * 9 + 9, 8 + var5 * 18, 84 + var4 * 18));
         }
      }

      for(var4 = 0; var4 < 9; ++var4) {
         this.addSlot(new Slot(var2, var4, 8 + var4 * 18, 142));
      }

      this.addDataSlot(this.selectedBannerPatternIndex);
   }

   public int getSelectedBannerPatternIndex() {
      return this.selectedBannerPatternIndex.get();
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.LOOM);
   }

   public boolean clickMenuButton(Player var1, int var2) {
      if (var2 > 0 && var2 <= BannerPattern.AVAILABLE_PATTERNS) {
         this.selectedBannerPatternIndex.set(var2);
         this.setupResultSlot();
         return true;
      } else {
         return false;
      }
   }

   public void slotsChanged(Container var1) {
      ItemStack var2 = this.bannerSlot.getItem();
      ItemStack var3 = this.dyeSlot.getItem();
      ItemStack var4 = this.patternSlot.getItem();
      ItemStack var5 = this.resultSlot.getItem();
      if (!var5.isEmpty() && (var2.isEmpty() || var3.isEmpty() || this.selectedBannerPatternIndex.get() <= 0 || this.selectedBannerPatternIndex.get() >= BannerPattern.COUNT - BannerPattern.PATTERN_ITEM_COUNT && var4.isEmpty())) {
         this.resultSlot.set(ItemStack.EMPTY);
         this.selectedBannerPatternIndex.set(0);
      } else if (!var4.isEmpty() && var4.getItem() instanceof BannerPatternItem) {
         CompoundTag var6 = BlockItem.getBlockEntityData(var2);
         boolean var7 = var6 != null && var6.contains("Patterns", 9) && !var2.isEmpty() && var6.getList("Patterns", 10).size() >= 6;
         if (var7) {
            this.selectedBannerPatternIndex.set(0);
         } else {
            this.selectedBannerPatternIndex.set(((BannerPatternItem)var4.getItem()).getBannerPattern().ordinal());
         }
      }

      this.setupResultSlot();
      this.broadcastChanges();
   }

   public void registerUpdateListener(Runnable var1) {
      this.slotUpdateListener = var1;
   }

   public ItemStack quickMoveStack(Player var1, int var2) {
      ItemStack var3 = ItemStack.EMPTY;
      Slot var4 = (Slot)this.slots.get(var2);
      if (var4 != null && var4.hasItem()) {
         ItemStack var5 = var4.getItem();
         var3 = var5.copy();
         if (var2 == this.resultSlot.index) {
            if (!this.moveItemStackTo(var5, 4, 40, true)) {
               return ItemStack.EMPTY;
            }

            var4.onQuickCraft(var5, var3);
         } else if (var2 != this.dyeSlot.index && var2 != this.bannerSlot.index && var2 != this.patternSlot.index) {
            if (var5.getItem() instanceof BannerItem) {
               if (!this.moveItemStackTo(var5, this.bannerSlot.index, this.bannerSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var5.getItem() instanceof DyeItem) {
               if (!this.moveItemStackTo(var5, this.dyeSlot.index, this.dyeSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var5.getItem() instanceof BannerPatternItem) {
               if (!this.moveItemStackTo(var5, this.patternSlot.index, this.patternSlot.index + 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 4 && var2 < 31) {
               if (!this.moveItemStackTo(var5, 31, 40, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (var2 >= 31 && var2 < 40 && !this.moveItemStackTo(var5, 4, 31, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(var5, 4, 40, false)) {
            return ItemStack.EMPTY;
         }

         if (var5.isEmpty()) {
            var4.set(ItemStack.EMPTY);
         } else {
            var4.setChanged();
         }

         if (var5.getCount() == var3.getCount()) {
            return ItemStack.EMPTY;
         }

         var4.onTake(var1, var5);
      }

      return var3;
   }

   public void removed(Player var1) {
      super.removed(var1);
      this.access.execute((var2, var3) -> {
         this.clearContainer(var1, this.inputContainer);
      });
   }

   private void setupResultSlot() {
      if (this.selectedBannerPatternIndex.get() > 0) {
         ItemStack var1 = this.bannerSlot.getItem();
         ItemStack var2 = this.dyeSlot.getItem();
         ItemStack var3 = ItemStack.EMPTY;
         if (!var1.isEmpty() && !var2.isEmpty()) {
            var3 = var1.copy();
            var3.setCount(1);
            BannerPattern var4 = BannerPattern.values()[this.selectedBannerPatternIndex.get()];
            DyeColor var5 = ((DyeItem)var2.getItem()).getDyeColor();
            CompoundTag var6 = BlockItem.getBlockEntityData(var3);
            ListTag var7;
            if (var6 != null && var6.contains("Patterns", 9)) {
               var7 = var6.getList("Patterns", 10);
            } else {
               var7 = new ListTag();
               if (var6 == null) {
                  var6 = new CompoundTag();
               }

               var6.put("Patterns", var7);
            }

            CompoundTag var8 = new CompoundTag();
            var8.putString("Pattern", var4.getHashname());
            var8.putInt("Color", var5.getId());
            var7.add(var8);
            BlockItem.setBlockEntityData(var3, BlockEntityType.BANNER, var6);
         }

         if (!ItemStack.matches(var3, this.resultSlot.getItem())) {
            this.resultSlot.set(var3);
         }
      }

   }

   public Slot getBannerSlot() {
      return this.bannerSlot;
   }

   public Slot getDyeSlot() {
      return this.dyeSlot;
   }

   public Slot getPatternSlot() {
      return this.patternSlot;
   }

   public Slot getResultSlot() {
      return this.resultSlot;
   }
}
