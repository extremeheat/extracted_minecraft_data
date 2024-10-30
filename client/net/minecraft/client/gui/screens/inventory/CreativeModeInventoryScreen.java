package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.SessionSearchTrees;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

public class CreativeModeInventoryScreen extends AbstractContainerScreen<ItemPickerMenu> {
   private static final ResourceLocation SCROLLER_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
   private static final ResourceLocation SCROLLER_DISABLED_SPRITE = ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");
   private static final ResourceLocation[] UNSELECTED_TOP_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_unselected_7")};
   private static final ResourceLocation[] SELECTED_TOP_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_top_selected_7")};
   private static final ResourceLocation[] UNSELECTED_BOTTOM_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_unselected_7")};
   private static final ResourceLocation[] SELECTED_BOTTOM_TABS = new ResourceLocation[]{ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_1"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_2"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_3"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_4"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_5"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_6"), ResourceLocation.withDefaultNamespace("container/creative_inventory/tab_bottom_selected_7")};
   private static final int NUM_ROWS = 5;
   private static final int NUM_COLS = 9;
   private static final int TAB_WIDTH = 26;
   private static final int TAB_HEIGHT = 32;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   static final SimpleContainer CONTAINER = new SimpleContainer(45);
   private static final Component TRASH_SLOT_TOOLTIP = Component.translatable("inventory.binSlot");
   private static final int TEXT_COLOR = 16777215;
   private static CreativeModeTab selectedTab = CreativeModeTabs.getDefaultTab();
   private float scrollOffs;
   private boolean scrolling;
   private EditBox searchBox;
   @Nullable
   private List<Slot> originalSlots;
   @Nullable
   private Slot destroyItemSlot;
   private CreativeInventoryListener listener;
   private boolean ignoreTextInput;
   private boolean hasClickedOutside;
   private final Set<TagKey<Item>> visibleTags = new HashSet();
   private final boolean displayOperatorCreativeTab;
   private final EffectsInInventory effects;

   public CreativeModeInventoryScreen(LocalPlayer var1, FeatureFlagSet var2, boolean var3) {
      super(new ItemPickerMenu(var1), var1.getInventory(), CommonComponents.EMPTY);
      var1.containerMenu = this.menu;
      this.imageHeight = 136;
      this.imageWidth = 195;
      this.displayOperatorCreativeTab = var3;
      this.tryRebuildTabContents(var1.connection.searchTrees(), var2, this.hasPermissions(var1), var1.level().registryAccess());
      this.effects = new EffectsInInventory(this);
   }

   private boolean hasPermissions(Player var1) {
      return var1.canUseGameMasterBlocks() && this.displayOperatorCreativeTab;
   }

   private void tryRefreshInvalidatedTabs(FeatureFlagSet var1, boolean var2, HolderLookup.Provider var3) {
      ClientPacketListener var4 = this.minecraft.getConnection();
      if (this.tryRebuildTabContents(var4 != null ? var4.searchTrees() : null, var1, var2, var3)) {
         Iterator var5 = CreativeModeTabs.allTabs().iterator();

         while(true) {
            while(true) {
               CreativeModeTab var6;
               Collection var7;
               do {
                  if (!var5.hasNext()) {
                     return;
                  }

                  var6 = (CreativeModeTab)var5.next();
                  var7 = var6.getDisplayItems();
               } while(var6 != selectedTab);

               if (var6.getType() == CreativeModeTab.Type.CATEGORY && var7.isEmpty()) {
                  this.selectTab(CreativeModeTabs.getDefaultTab());
               } else {
                  this.refreshCurrentTabContents(var7);
               }
            }
         }
      }
   }

   private boolean tryRebuildTabContents(@Nullable SessionSearchTrees var1, FeatureFlagSet var2, boolean var3, HolderLookup.Provider var4) {
      if (!CreativeModeTabs.tryRebuildTabContents(var2, var3, var4)) {
         return false;
      } else {
         if (var1 != null) {
            List var5 = List.copyOf(CreativeModeTabs.searchTab().getDisplayItems());
            var1.updateCreativeTooltips(var4, var5);
            var1.updateCreativeTags(var5);
         }

         return true;
      }
   }

   private void refreshCurrentTabContents(Collection<ItemStack> var1) {
      int var2 = ((ItemPickerMenu)this.menu).getRowIndexForScroll(this.scrollOffs);
      ((ItemPickerMenu)this.menu).items.clear();
      if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
         this.refreshSearchResults();
      } else {
         ((ItemPickerMenu)this.menu).items.addAll(var1);
      }

      this.scrollOffs = ((ItemPickerMenu)this.menu).getScrollForRowIndex(var2);
      ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
   }

   public void containerTick() {
      super.containerTick();
      if (this.minecraft != null) {
         if (this.minecraft.player != null) {
            this.tryRefreshInvalidatedTabs(this.minecraft.player.connection.enabledFeatures(), this.hasPermissions(this.minecraft.player), this.minecraft.player.level().registryAccess());
         }

         if (!this.minecraft.gameMode.hasInfiniteItems()) {
            this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
         }

      }
   }

   protected void slotClicked(@Nullable Slot var1, int var2, int var3, ClickType var4) {
      if (this.isCreativeSlot(var1)) {
         this.searchBox.moveCursorToEnd(false);
         this.searchBox.setHighlightPos(0);
      }

      boolean var5 = var4 == ClickType.QUICK_MOVE;
      var4 = var2 == -999 && var4 == ClickType.PICKUP ? ClickType.THROW : var4;
      if (var4 != ClickType.THROW || this.minecraft.player.canDropItems()) {
         this.onMouseClickAction(var1, var4);
         ItemStack var6;
         if (var1 == null && selectedTab.getType() != CreativeModeTab.Type.INVENTORY && var4 != ClickType.QUICK_CRAFT) {
            if (!((ItemPickerMenu)this.menu).getCarried().isEmpty() && this.hasClickedOutside) {
               if (!this.minecraft.player.canDropItems()) {
                  return;
               }

               if (var3 == 0) {
                  this.minecraft.player.drop(((ItemPickerMenu)this.menu).getCarried(), true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(((ItemPickerMenu)this.menu).getCarried());
                  ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
               }

               if (var3 == 1) {
                  var6 = ((ItemPickerMenu)this.menu).getCarried().split(1);
                  this.minecraft.player.drop(var6, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(var6);
               }
            }
         } else {
            if (var1 != null && !var1.mayPickup(this.minecraft.player)) {
               return;
            }

            if (var1 == this.destroyItemSlot && var5) {
               for(int var9 = 0; var9 < this.minecraft.player.inventoryMenu.getItems().size(); ++var9) {
                  this.minecraft.player.inventoryMenu.getSlot(var9).set(ItemStack.EMPTY);
                  this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, var9);
               }
            } else {
               ItemStack var7;
               if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
                  if (var1 == this.destroyItemSlot) {
                     ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                  } else if (var4 == ClickType.THROW && var1 != null && var1.hasItem()) {
                     var6 = var1.remove(var3 == 0 ? 1 : var1.getItem().getMaxStackSize());
                     var7 = var1.getItem();
                     this.minecraft.player.drop(var6, true);
                     this.minecraft.gameMode.handleCreativeModeItemDrop(var6);
                     this.minecraft.gameMode.handleCreativeModeItemAdd(var7, ((SlotWrapper)var1).target.index);
                  } else if (var4 == ClickType.THROW && var2 == -999 && !((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                     this.minecraft.player.drop(((ItemPickerMenu)this.menu).getCarried(), true);
                     this.minecraft.gameMode.handleCreativeModeItemDrop(((ItemPickerMenu)this.menu).getCarried());
                     ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                  } else {
                     this.minecraft.player.inventoryMenu.clicked(var1 == null ? var2 : ((SlotWrapper)var1).target.index, var3, var4, this.minecraft.player);
                     this.minecraft.player.inventoryMenu.broadcastChanges();
                  }
               } else {
                  ItemStack var8;
                  if (var4 != ClickType.QUICK_CRAFT && var1.container == CONTAINER) {
                     var6 = ((ItemPickerMenu)this.menu).getCarried();
                     var7 = var1.getItem();
                     if (var4 == ClickType.SWAP) {
                        if (!var7.isEmpty()) {
                           this.minecraft.player.getInventory().setItem(var3, var7.copyWithCount(var7.getMaxStackSize()));
                           this.minecraft.player.inventoryMenu.broadcastChanges();
                        }

                        return;
                     }

                     if (var4 == ClickType.CLONE) {
                        if (((ItemPickerMenu)this.menu).getCarried().isEmpty() && var1.hasItem()) {
                           var8 = var1.getItem();
                           ((ItemPickerMenu)this.menu).setCarried(var8.copyWithCount(var8.getMaxStackSize()));
                        }

                        return;
                     }

                     if (var4 == ClickType.THROW) {
                        if (!var7.isEmpty()) {
                           var8 = var7.copyWithCount(var3 == 0 ? 1 : var7.getMaxStackSize());
                           this.minecraft.player.drop(var8, true);
                           this.minecraft.gameMode.handleCreativeModeItemDrop(var8);
                        }

                        return;
                     }

                     if (!var6.isEmpty() && !var7.isEmpty() && ItemStack.isSameItemSameComponents(var6, var7)) {
                        if (var3 == 0) {
                           if (var5) {
                              var6.setCount(var6.getMaxStackSize());
                           } else if (var6.getCount() < var6.getMaxStackSize()) {
                              var6.grow(1);
                           }
                        } else {
                           var6.shrink(1);
                        }
                     } else if (!var7.isEmpty() && var6.isEmpty()) {
                        int var11 = var5 ? var7.getMaxStackSize() : var7.getCount();
                        ((ItemPickerMenu)this.menu).setCarried(var7.copyWithCount(var11));
                     } else if (var3 == 0) {
                        ((ItemPickerMenu)this.menu).setCarried(ItemStack.EMPTY);
                     } else if (!((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                        ((ItemPickerMenu)this.menu).getCarried().shrink(1);
                     }
                  } else if (this.menu != null) {
                     var6 = var1 == null ? ItemStack.EMPTY : ((ItemPickerMenu)this.menu).getSlot(var1.index).getItem();
                     ((ItemPickerMenu)this.menu).clicked(var1 == null ? var2 : var1.index, var3, var4, this.minecraft.player);
                     int var10;
                     if (AbstractContainerMenu.getQuickcraftHeader(var3) == 2) {
                        for(var10 = 0; var10 < 9; ++var10) {
                           this.minecraft.gameMode.handleCreativeModeItemAdd(((ItemPickerMenu)this.menu).getSlot(45 + var10).getItem(), 36 + var10);
                        }
                     } else if (var1 != null && Inventory.isHotbarSlot(var1.getContainerSlot()) && selectedTab.getType() != CreativeModeTab.Type.INVENTORY) {
                        if (var4 == ClickType.THROW && !var6.isEmpty() && !((ItemPickerMenu)this.menu).getCarried().isEmpty()) {
                           var10 = var3 == 0 ? 1 : var6.getCount();
                           var8 = var6.copyWithCount(var10);
                           var6.shrink(var10);
                           this.minecraft.player.drop(var8, true);
                           this.minecraft.gameMode.handleCreativeModeItemDrop(var8);
                        }

                        this.minecraft.player.inventoryMenu.broadcastChanges();
                     }
                  }
               }
            }
         }

      }
   }

   private boolean isCreativeSlot(@Nullable Slot var1) {
      return var1 != null && var1.container == CONTAINER;
   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         super.init();
         Font var10003 = this.font;
         int var10004 = this.leftPos + 82;
         int var10005 = this.topPos + 6;
         Objects.requireNonNull(this.font);
         this.searchBox = new EditBox(var10003, var10004, var10005, 80, 9, Component.translatable("itemGroup.search"));
         this.searchBox.setMaxLength(50);
         this.searchBox.setBordered(false);
         this.searchBox.setVisible(false);
         this.searchBox.setTextColor(16777215);
         this.addWidget(this.searchBox);
         CreativeModeTab var1 = selectedTab;
         selectedTab = CreativeModeTabs.getDefaultTab();
         this.selectTab(var1);
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
         this.listener = new CreativeInventoryListener(this.minecraft);
         this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
         if (!selectedTab.shouldDisplay()) {
            this.selectTab(CreativeModeTabs.getDefaultTab());
         }
      } else {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      }

   }

   public void resize(Minecraft var1, int var2, int var3) {
      int var4 = ((ItemPickerMenu)this.menu).getRowIndexForScroll(this.scrollOffs);
      String var5 = this.searchBox.getValue();
      this.init(var1, var2, var3);
      this.searchBox.setValue(var5);
      if (!this.searchBox.getValue().isEmpty()) {
         this.refreshSearchResults();
      }

      this.scrollOffs = ((ItemPickerMenu)this.menu).getScrollForRowIndex(var4);
      ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
   }

   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.getInventory() != null) {
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
      }

   }

   public boolean charTyped(char var1, int var2) {
      if (this.ignoreTextInput) {
         return false;
      } else if (selectedTab.getType() != CreativeModeTab.Type.SEARCH) {
         return false;
      } else {
         String var3 = this.searchBox.getValue();
         if (this.searchBox.charTyped(var1, var2)) {
            if (!Objects.equals(var3, this.searchBox.getValue())) {
               this.refreshSearchResults();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      if (selectedTab.getType() != CreativeModeTab.Type.SEARCH) {
         if (this.minecraft.options.keyChat.matches(var1, var2)) {
            this.ignoreTextInput = true;
            this.selectTab(CreativeModeTabs.searchTab());
            return true;
         } else {
            return super.keyPressed(var1, var2, var3);
         }
      } else {
         boolean var4 = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot.hasItem();
         boolean var5 = InputConstants.getKey(var1, var2).getNumericKeyValue().isPresent();
         if (var4 && var5 && this.checkHotbarKeyPressed(var1, var2)) {
            this.ignoreTextInput = true;
            return true;
         } else {
            String var6 = this.searchBox.getValue();
            if (this.searchBox.keyPressed(var1, var2, var3)) {
               if (!Objects.equals(var6, this.searchBox.getValue())) {
                  this.refreshSearchResults();
               }

               return true;
            } else {
               return this.searchBox.isFocused() && this.searchBox.isVisible() && var1 != 256 ? true : super.keyPressed(var1, var2, var3);
            }
         }
      }
   }

   public boolean keyReleased(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      return super.keyReleased(var1, var2, var3);
   }

   private void refreshSearchResults() {
      ((ItemPickerMenu)this.menu).items.clear();
      this.visibleTags.clear();
      String var1 = this.searchBox.getValue();
      if (var1.isEmpty()) {
         ((ItemPickerMenu)this.menu).items.addAll(selectedTab.getDisplayItems());
      } else {
         ClientPacketListener var2 = this.minecraft.getConnection();
         if (var2 != null) {
            SessionSearchTrees var4 = var2.searchTrees();
            SearchTree var3;
            if (var1.startsWith("#")) {
               var1 = var1.substring(1);
               var3 = var4.creativeTagSearch();
               this.updateVisibleTags(var1);
            } else {
               var3 = var4.creativeNameSearch();
            }

            ((ItemPickerMenu)this.menu).items.addAll(var3.search(var1.toLowerCase(Locale.ROOT)));
         }
      }

      this.scrollOffs = 0.0F;
      ((ItemPickerMenu)this.menu).scrollTo(0.0F);
   }

   private void updateVisibleTags(String var1) {
      int var2 = var1.indexOf(58);
      Predicate var3;
      if (var2 == -1) {
         var3 = (var1x) -> {
            return var1x.getPath().contains(var1);
         };
      } else {
         String var4 = var1.substring(0, var2).trim();
         String var5 = var1.substring(var2 + 1).trim();
         var3 = (var2x) -> {
            return var2x.getNamespace().contains(var4) && var2x.getPath().contains(var5);
         };
      }

      Stream var10000 = BuiltInRegistries.ITEM.getTags().map(HolderSet.Named::key).filter((var1x) -> {
         return var3.test(var1x.location());
      });
      Set var10001 = this.visibleTags;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      if (selectedTab.showTitle()) {
         var1.drawString(this.font, (Component)selectedTab.getDisplayName(), 8, 6, 4210752, false);
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         double var6 = var1 - (double)this.leftPos;
         double var8 = var3 - (double)this.topPos;
         Iterator var10 = CreativeModeTabs.tabs().iterator();

         while(var10.hasNext()) {
            CreativeModeTab var11 = (CreativeModeTab)var10.next();
            if (this.checkTabClicked(var11, var6, var8)) {
               return true;
            }
         }

         if (selectedTab.getType() != CreativeModeTab.Type.INVENTORY && this.insideScrollbar(var1, var3)) {
            this.scrolling = this.canScroll();
            return true;
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0) {
         double var6 = var1 - (double)this.leftPos;
         double var8 = var3 - (double)this.topPos;
         this.scrolling = false;
         Iterator var10 = CreativeModeTabs.tabs().iterator();

         while(var10.hasNext()) {
            CreativeModeTab var11 = (CreativeModeTab)var10.next();
            if (this.checkTabClicked(var11, var6, var8)) {
               this.selectTab(var11);
               return true;
            }
         }
      }

      return super.mouseReleased(var1, var3, var5);
   }

   private boolean canScroll() {
      return selectedTab.canScroll() && ((ItemPickerMenu)this.menu).canScroll();
   }

   private void selectTab(CreativeModeTab var1) {
      CreativeModeTab var2 = selectedTab;
      selectedTab = var1;
      this.quickCraftSlots.clear();
      ((ItemPickerMenu)this.menu).items.clear();
      this.clearDraggingState();
      int var4;
      int var6;
      if (selectedTab.getType() == CreativeModeTab.Type.HOTBAR) {
         HotbarManager var3 = this.minecraft.getHotbarManager();

         for(var4 = 0; var4 < 9; ++var4) {
            Hotbar var5 = var3.get(var4);
            if (var5.isEmpty()) {
               for(var6 = 0; var6 < 9; ++var6) {
                  if (var6 == var4) {
                     ItemStack var7 = new ItemStack(Items.PAPER);
                     var7.set(DataComponents.CREATIVE_SLOT_LOCK, Unit.INSTANCE);
                     Component var8 = this.minecraft.options.keyHotbarSlots[var4].getTranslatedKeyMessage();
                     Component var9 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                     var7.set(DataComponents.ITEM_NAME, Component.translatable("inventory.hotbarInfo", var9, var8));
                     ((ItemPickerMenu)this.menu).items.add(var7);
                  } else {
                     ((ItemPickerMenu)this.menu).items.add(ItemStack.EMPTY);
                  }
               }
            } else {
               ((ItemPickerMenu)this.menu).items.addAll(var5.load(this.minecraft.level.registryAccess()));
            }
         }
      } else if (selectedTab.getType() == CreativeModeTab.Type.CATEGORY) {
         ((ItemPickerMenu)this.menu).items.addAll(selectedTab.getDisplayItems());
      }

      if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
         InventoryMenu var10 = this.minecraft.player.inventoryMenu;
         if (this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf(((ItemPickerMenu)this.menu).slots);
         }

         ((ItemPickerMenu)this.menu).slots.clear();

         for(var4 = 0; var4 < var10.slots.size(); ++var4) {
            int var11;
            int var12;
            int var14;
            int var15;
            if (var4 >= 5 && var4 < 9) {
               var12 = var4 - 5;
               var14 = var12 / 2;
               var15 = var12 % 2;
               var11 = 54 + var14 * 54;
               var6 = 6 + var15 * 27;
            } else if (var4 >= 0 && var4 < 5) {
               var11 = -2000;
               var6 = -2000;
            } else if (var4 == 45) {
               var11 = 35;
               var6 = 20;
            } else {
               var12 = var4 - 9;
               var14 = var12 % 9;
               var15 = var12 / 9;
               var11 = 9 + var14 * 18;
               if (var4 >= 36) {
                  var6 = 112;
               } else {
                  var6 = 54 + var15 * 18;
               }
            }

            SlotWrapper var13 = new SlotWrapper((Slot)var10.slots.get(var4), var4, var11, var6);
            ((ItemPickerMenu)this.menu).slots.add(var13);
         }

         this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
         ((ItemPickerMenu)this.menu).slots.add(this.destroyItemSlot);
      } else if (var2.getType() == CreativeModeTab.Type.INVENTORY) {
         ((ItemPickerMenu)this.menu).slots.clear();
         ((ItemPickerMenu)this.menu).slots.addAll(this.originalSlots);
         this.originalSlots = null;
      }

      if (selectedTab.getType() == CreativeModeTab.Type.SEARCH) {
         this.searchBox.setVisible(true);
         this.searchBox.setCanLoseFocus(false);
         this.searchBox.setFocused(true);
         if (var2 != var1) {
            this.searchBox.setValue("");
         }

         this.refreshSearchResults();
      } else {
         this.searchBox.setVisible(false);
         this.searchBox.setCanLoseFocus(true);
         this.searchBox.setFocused(false);
         this.searchBox.setValue("");
      }

      this.scrollOffs = 0.0F;
      ((ItemPickerMenu)this.menu).scrollTo(0.0F);
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (super.mouseScrolled(var1, var3, var5, var7)) {
         return true;
      } else if (!this.canScroll()) {
         return false;
      } else {
         this.scrollOffs = ((ItemPickerMenu)this.menu).subtractInputFromScroll(this.scrollOffs, var7);
         ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
         return true;
      }
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
      this.hasClickedOutside = var8 && !this.checkTabClicked(selectedTab, var1, var3);
      return this.hasClickedOutside;
   }

   protected boolean insideScrollbar(double var1, double var3) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      int var7 = var5 + 175;
      int var8 = var6 + 18;
      int var9 = var7 + 14;
      int var10 = var8 + 112;
      return var1 >= (double)var7 && var3 >= (double)var8 && var1 < (double)var9 && var3 < (double)var10;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.scrolling) {
         int var10 = this.topPos + 18;
         int var11 = var10 + 112;
         this.scrollOffs = ((float)var3 - (float)var10 - 7.5F) / ((float)(var11 - var10) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         ((ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.effects.render(var1, var2, var3, var4);
      Iterator var5 = CreativeModeTabs.tabs().iterator();

      while(var5.hasNext()) {
         CreativeModeTab var6 = (CreativeModeTab)var5.next();
         if (this.checkTabHovering(var1, var6, var2, var3)) {
            break;
         }
      }

      if (this.destroyItemSlot != null && selectedTab.getType() == CreativeModeTab.Type.INVENTORY && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, (double)var2, (double)var3)) {
         var1.renderTooltip(this.font, TRASH_SLOT_TOOLTIP, var2, var3);
      }

      this.renderTooltip(var1, var2, var3);
   }

   public boolean showsActiveEffects() {
      return this.effects.canSeeEffects();
   }

   public List<Component> getTooltipFromContainerItem(ItemStack var1) {
      boolean var2 = this.hoveredSlot != null && this.hoveredSlot instanceof CustomCreativeSlot;
      boolean var3 = selectedTab.getType() == CreativeModeTab.Type.CATEGORY;
      boolean var4 = selectedTab.getType() == CreativeModeTab.Type.SEARCH;
      TooltipFlag.Default var5 = this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL;
      TooltipFlag.Default var6 = var2 ? var5.asCreative() : var5;
      List var7 = var1.getTooltipLines(Item.TooltipContext.of(this.minecraft.level, this.minecraft.player), this.minecraft.player, var6);
      if (var3 && var2) {
         return var7;
      } else {
         ArrayList var8 = Lists.newArrayList(var7);
         if (var4 && var2) {
            this.visibleTags.forEach((var2x) -> {
               if (var1.is(var2x)) {
                  var8.add(1, Component.literal("#" + String.valueOf(var2x.location())).withStyle(ChatFormatting.DARK_PURPLE));
               }

            });
         }

         int var9 = 1;
         Iterator var10 = CreativeModeTabs.tabs().iterator();

         while(var10.hasNext()) {
            CreativeModeTab var11 = (CreativeModeTab)var10.next();
            if (var11.getType() != CreativeModeTab.Type.SEARCH && var11.contains(var1)) {
               var8.add(var9++, var11.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
            }
         }

         return var8;
      }
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      Iterator var5 = CreativeModeTabs.tabs().iterator();

      while(var5.hasNext()) {
         CreativeModeTab var6 = (CreativeModeTab)var5.next();
         if (var6 != selectedTab) {
            this.renderTabButton(var1, var6);
         }
      }

      var1.blit(RenderType::guiTextured, selectedTab.getBackgroundTexture(), this.leftPos, this.topPos, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 256);
      this.searchBox.render(var1, var3, var4, var2);
      int var9 = this.leftPos + 175;
      int var10 = this.topPos + 18;
      int var7 = var10 + 112;
      if (selectedTab.canScroll()) {
         ResourceLocation var8 = this.canScroll() ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE;
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var8, var9, var10 + (int)((float)(var7 - var10 - 17) * this.scrollOffs), 12, 15);
      }

      this.renderTabButton(var1, selectedTab);
      if (selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
         InventoryScreen.renderEntityInInventoryFollowsMouse(var1, this.leftPos + 73, this.topPos + 6, this.leftPos + 105, this.topPos + 49, 20, 0.0625F, (float)var3, (float)var4, this.minecraft.player);
      }

   }

   private int getTabX(CreativeModeTab var1) {
      int var2 = var1.column();
      boolean var3 = true;
      int var4 = 27 * var2;
      if (var1.isAlignedRight()) {
         var4 = this.imageWidth - 27 * (7 - var2) + 1;
      }

      return var4;
   }

   private int getTabY(CreativeModeTab var1) {
      int var2 = 0;
      if (var1.row() == CreativeModeTab.Row.TOP) {
         var2 -= 32;
      } else {
         var2 += this.imageHeight;
      }

      return var2;
   }

   protected boolean checkTabClicked(CreativeModeTab var1, double var2, double var4) {
      int var6 = this.getTabX(var1);
      int var7 = this.getTabY(var1);
      return var2 >= (double)var6 && var2 <= (double)(var6 + 26) && var4 >= (double)var7 && var4 <= (double)(var7 + 32);
   }

   protected boolean checkTabHovering(GuiGraphics var1, CreativeModeTab var2, int var3, int var4) {
      int var5 = this.getTabX(var2);
      int var6 = this.getTabY(var2);
      if (this.isHovering(var5 + 3, var6 + 3, 21, 27, (double)var3, (double)var4)) {
         var1.renderTooltip(this.font, var2.getDisplayName(), var3, var4);
         return true;
      } else {
         return false;
      }
   }

   protected void renderTabButton(GuiGraphics var1, CreativeModeTab var2) {
      boolean var3 = var2 == selectedTab;
      boolean var4 = var2.row() == CreativeModeTab.Row.TOP;
      int var5 = var2.column();
      int var6 = this.leftPos + this.getTabX(var2);
      int var7 = this.topPos - (var4 ? 28 : -(this.imageHeight - 4));
      ResourceLocation[] var8;
      if (var4) {
         var8 = var3 ? SELECTED_TOP_TABS : UNSELECTED_TOP_TABS;
      } else {
         var8 = var3 ? SELECTED_BOTTOM_TABS : UNSELECTED_BOTTOM_TABS;
      }

      var1.blitSprite(RenderType::guiTextured, (ResourceLocation)var8[Mth.clamp(var5, 0, var8.length)], var6, var7, 26, 32);
      var1.pose().pushPose();
      var1.pose().translate(0.0F, 0.0F, 100.0F);
      var6 += 5;
      var7 += 8 + (var4 ? 1 : -1);
      ItemStack var9 = var2.getIconItem();
      var1.renderItem(var9, var6, var7);
      var1.renderItemDecorations(this.font, var9, var6, var7);
      var1.pose().popPose();
   }

   public boolean isInventoryOpen() {
      return selectedTab.getType() == CreativeModeTab.Type.INVENTORY;
   }

   public static void handleHotbarLoadOrSave(Minecraft var0, int var1, boolean var2, boolean var3) {
      LocalPlayer var4 = var0.player;
      RegistryAccess var5 = var4.level().registryAccess();
      HotbarManager var6 = var0.getHotbarManager();
      Hotbar var7 = var6.get(var1);
      if (var2) {
         List var8 = var7.load(var5);

         for(int var9 = 0; var9 < Inventory.getSelectionSize(); ++var9) {
            ItemStack var10 = (ItemStack)var8.get(var9);
            var4.getInventory().setItem(var9, var10);
            var0.gameMode.handleCreativeModeItemAdd(var10, 36 + var9);
         }

         var4.inventoryMenu.broadcastChanges();
      } else if (var3) {
         var7.storeFrom(var4.getInventory(), var5);
         Component var12 = var0.options.keyHotbarSlots[var1].getTranslatedKeyMessage();
         Component var13 = var0.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
         MutableComponent var11 = Component.translatable("inventory.hotbarSaved", var13, var12);
         var0.gui.setOverlayMessage(var11, false);
         var0.getNarrator().sayNow((Component)var11);
         var6.save();
      }

   }

   public static class ItemPickerMenu extends AbstractContainerMenu {
      public final NonNullList<ItemStack> items = NonNullList.create();
      private final AbstractContainerMenu inventoryMenu;

      public ItemPickerMenu(Player var1) {
         super((MenuType)null, 0);
         this.inventoryMenu = var1.inventoryMenu;
         Inventory var2 = var1.getInventory();

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 9; ++var4) {
               this.addSlot(new CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, var3 * 9 + var4, 9 + var4 * 18, 18 + var3 * 18));
            }
         }

         this.addInventoryHotbarSlots(var2, 9, 112);
         this.scrollTo(0.0F);
      }

      public boolean stillValid(Player var1) {
         return true;
      }

      protected int calculateRowCount() {
         return Mth.positiveCeilDiv(this.items.size(), 9) - 5;
      }

      protected int getRowIndexForScroll(float var1) {
         return Math.max((int)((double)(var1 * (float)this.calculateRowCount()) + 0.5), 0);
      }

      protected float getScrollForRowIndex(int var1) {
         return Mth.clamp((float)var1 / (float)this.calculateRowCount(), 0.0F, 1.0F);
      }

      protected float subtractInputFromScroll(float var1, double var2) {
         return Mth.clamp(var1 - (float)(var2 / (double)this.calculateRowCount()), 0.0F, 1.0F);
      }

      public void scrollTo(float var1) {
         int var2 = this.getRowIndexForScroll(var1);

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 9; ++var4) {
               int var5 = var4 + (var3 + var2) * 9;
               if (var5 >= 0 && var5 < this.items.size()) {
                  CreativeModeInventoryScreen.CONTAINER.setItem(var4 + var3 * 9, (ItemStack)this.items.get(var5));
               } else {
                  CreativeModeInventoryScreen.CONTAINER.setItem(var4 + var3 * 9, ItemStack.EMPTY);
               }
            }
         }

      }

      public boolean canScroll() {
         return this.items.size() > 45;
      }

      public ItemStack quickMoveStack(Player var1, int var2) {
         if (var2 >= this.slots.size() - 9 && var2 < this.slots.size()) {
            Slot var3 = (Slot)this.slots.get(var2);
            if (var3 != null && var3.hasItem()) {
               var3.setByPlayer(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
         return var2.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public boolean canDragTo(Slot var1) {
         return var1.container != CreativeModeInventoryScreen.CONTAINER;
      }

      public ItemStack getCarried() {
         return this.inventoryMenu.getCarried();
      }

      public void setCarried(ItemStack var1) {
         this.inventoryMenu.setCarried(var1);
      }
   }

   static class SlotWrapper extends Slot {
      final Slot target;

      public SlotWrapper(Slot var1, int var2, int var3, int var4) {
         super(var1.container, var2, var3, var4);
         this.target = var1;
      }

      public void onTake(Player var1, ItemStack var2) {
         this.target.onTake(var1, var2);
      }

      public boolean mayPlace(ItemStack var1) {
         return this.target.mayPlace(var1);
      }

      public ItemStack getItem() {
         return this.target.getItem();
      }

      public boolean hasItem() {
         return this.target.hasItem();
      }

      public void setByPlayer(ItemStack var1, ItemStack var2) {
         this.target.setByPlayer(var1, var2);
      }

      public void set(ItemStack var1) {
         this.target.set(var1);
      }

      public void setChanged() {
         this.target.setChanged();
      }

      public int getMaxStackSize() {
         return this.target.getMaxStackSize();
      }

      public int getMaxStackSize(ItemStack var1) {
         return this.target.getMaxStackSize(var1);
      }

      @Nullable
      public ResourceLocation getNoItemIcon() {
         return this.target.getNoItemIcon();
      }

      public ItemStack remove(int var1) {
         return this.target.remove(var1);
      }

      public boolean isActive() {
         return this.target.isActive();
      }

      public boolean mayPickup(Player var1) {
         return this.target.mayPickup(var1);
      }
   }

   private static class CustomCreativeSlot extends Slot {
      public CustomCreativeSlot(Container var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean mayPickup(Player var1) {
         ItemStack var2 = this.getItem();
         if (super.mayPickup(var1) && !var2.isEmpty()) {
            return var2.isItemEnabled(var1.level().enabledFeatures()) && !var2.has(DataComponents.CREATIVE_SLOT_LOCK);
         } else {
            return var2.isEmpty();
         }
      }
   }
}
