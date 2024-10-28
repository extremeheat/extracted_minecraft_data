package net.minecraft.world.inventory;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BannerPatternTags;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class LoomMenu extends AbstractContainerMenu {
   private static final int PATTERN_NOT_SET = -1;
   private static final int INV_SLOT_START = 4;
   private static final int INV_SLOT_END = 31;
   private static final int USE_ROW_SLOT_START = 31;
   private static final int USE_ROW_SLOT_END = 40;
   private final ContainerLevelAccess access;
   final DataSlot selectedBannerPatternIndex;
   private List<Holder<BannerPattern>> selectablePatterns;
   Runnable slotUpdateListener;
   private final HolderGetter<BannerPattern> patternGetter;
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
      this.selectablePatterns = List.of();
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
      this.bannerSlot = this.addSlot(new Slot(this, this.inputContainer, 0, 13, 26) {
         public boolean mayPlace(ItemStack var1) {
            return var1.getItem() instanceof BannerItem;
         }
      });
      this.dyeSlot = this.addSlot(new Slot(this, this.inputContainer, 1, 33, 26) {
         public boolean mayPlace(ItemStack var1) {
            return var1.getItem() instanceof DyeItem;
         }
      });
      this.patternSlot = this.addSlot(new Slot(this, this.inputContainer, 2, 23, 45) {
         public boolean mayPlace(ItemStack var1) {
            return var1.getItem() instanceof BannerPatternItem;
         }
      });
      this.resultSlot = this.addSlot(new Slot(this.outputContainer, 0, 143, 57) {
         public boolean mayPlace(ItemStack var1) {
            return false;
         }

         public void onTake(Player var1, ItemStack var2) {
            LoomMenu.this.bannerSlot.remove(1);
            LoomMenu.this.dyeSlot.remove(1);
            if (!LoomMenu.this.bannerSlot.hasItem() || !LoomMenu.this.dyeSlot.hasItem()) {
               LoomMenu.this.selectedBannerPatternIndex.set(-1);
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
      this.patternGetter = var2.player.registryAccess().lookupOrThrow(Registries.BANNER_PATTERN);
   }

   public boolean stillValid(Player var1) {
      return stillValid(this.access, var1, Blocks.LOOM);
   }

   public boolean clickMenuButton(Player var1, int var2) {
      if (var2 >= 0 && var2 < this.selectablePatterns.size()) {
         this.selectedBannerPatternIndex.set(var2);
         this.setupResultSlot((Holder)this.selectablePatterns.get(var2));
         return true;
      } else {
         return false;
      }
   }

   private List<Holder<BannerPattern>> getSelectablePatterns(ItemStack var1) {
      if (var1.isEmpty()) {
         return (List)this.patternGetter.get(BannerPatternTags.NO_ITEM_REQUIRED).map(ImmutableList::copyOf).orElse(ImmutableList.of());
      } else {
         Item var3 = var1.getItem();
         if (var3 instanceof BannerPatternItem) {
            BannerPatternItem var2 = (BannerPatternItem)var3;
            return (List)this.patternGetter.get(var2.getBannerPattern()).map(ImmutableList::copyOf).orElse(ImmutableList.of());
         } else {
            return List.of();
         }
      }
   }

   private boolean isValidPatternIndex(int var1) {
      return var1 >= 0 && var1 < this.selectablePatterns.size();
   }

   public void slotsChanged(Container var1) {
      ItemStack var2 = this.bannerSlot.getItem();
      ItemStack var3 = this.dyeSlot.getItem();
      ItemStack var4 = this.patternSlot.getItem();
      if (!var2.isEmpty() && !var3.isEmpty()) {
         int var5 = this.selectedBannerPatternIndex.get();
         boolean var6 = this.isValidPatternIndex(var5);
         List var7 = this.selectablePatterns;
         this.selectablePatterns = this.getSelectablePatterns(var4);
         Holder var8;
         if (this.selectablePatterns.size() == 1) {
            this.selectedBannerPatternIndex.set(0);
            var8 = (Holder)this.selectablePatterns.get(0);
         } else if (!var6) {
            this.selectedBannerPatternIndex.set(-1);
            var8 = null;
         } else {
            Holder var9 = (Holder)var7.get(var5);
            int var10 = this.selectablePatterns.indexOf(var9);
            if (var10 != -1) {
               var8 = var9;
               this.selectedBannerPatternIndex.set(var10);
            } else {
               var8 = null;
               this.selectedBannerPatternIndex.set(-1);
            }
         }

         if (var8 != null) {
            BannerPatternLayers var11 = (BannerPatternLayers)var2.getOrDefault(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY);
            boolean var12 = var11.layers().size() >= 6;
            if (var12) {
               this.selectedBannerPatternIndex.set(-1);
               this.resultSlot.set(ItemStack.EMPTY);
            } else {
               this.setupResultSlot(var8);
            }
         } else {
            this.resultSlot.set(ItemStack.EMPTY);
         }

         this.broadcastChanges();
      } else {
         this.resultSlot.set(ItemStack.EMPTY);
         this.selectablePatterns = List.of();
         this.selectedBannerPatternIndex.set(-1);
      }
   }

   public List<Holder<BannerPattern>> getSelectablePatterns() {
      return this.selectablePatterns;
   }

   public int getSelectedBannerPatternIndex() {
      return this.selectedBannerPatternIndex.get();
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
            var4.setByPlayer(ItemStack.EMPTY);
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

   private void setupResultSlot(Holder<BannerPattern> var1) {
      ItemStack var2 = this.bannerSlot.getItem();
      ItemStack var3 = this.dyeSlot.getItem();
      ItemStack var4 = ItemStack.EMPTY;
      if (!var2.isEmpty() && !var3.isEmpty()) {
         var4 = var2.copyWithCount(1);
         DyeColor var5 = ((DyeItem)var3.getItem()).getDyeColor();
         var4.update(DataComponents.BANNER_PATTERNS, BannerPatternLayers.EMPTY, (var2x) -> {
            return (new BannerPatternLayers.Builder()).addAll(var2x).add(var1, var5).build();
         });
      }

      if (!ItemStack.matches(var4, this.resultSlot.getItem())) {
         this.resultSlot.set(var4);
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
