package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.searchtree.MutableSearchTree;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class CreativeModeInventoryScreen extends EffectRenderingInventoryScreen {
   private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final SimpleContainer CONTAINER = new SimpleContainer(45);
   private static int selectedTab;
   private float scrollOffs;
   private boolean scrolling;
   private EditBox searchBox;
   @Nullable
   private List originalSlots;
   @Nullable
   private Slot destroyItemSlot;
   private CreativeInventoryListener listener;
   private boolean ignoreTextInput;
   private boolean hasClickedOutside;
   private final Map visibleTags = Maps.newTreeMap();

   public CreativeModeInventoryScreen(Player var1) {
      super(new CreativeModeInventoryScreen.ItemPickerMenu(var1), var1.inventory, new TextComponent(""));
      var1.containerMenu = this.menu;
      this.passEvents = true;
      this.imageHeight = 136;
      this.imageWidth = 195;
   }

   public void tick() {
      if (!this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      } else if (this.searchBox != null) {
         this.searchBox.tick();
      }

   }

   protected void slotClicked(@Nullable Slot var1, int var2, int var3, ClickType var4) {
      if (this.isCreativeSlot(var1)) {
         this.searchBox.moveCursorToEnd();
         this.searchBox.setHighlightPos(0);
      }

      boolean var5 = var4 == ClickType.QUICK_MOVE;
      var4 = var2 == -999 && var4 == ClickType.PICKUP ? ClickType.THROW : var4;
      ItemStack var7;
      Inventory var10;
      if (var1 == null && selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && var4 != ClickType.QUICK_CRAFT) {
         var10 = this.minecraft.player.inventory;
         if (!var10.getCarried().isEmpty() && this.hasClickedOutside) {
            if (var3 == 0) {
               this.minecraft.player.drop(var10.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(var10.getCarried());
               var10.setCarried(ItemStack.EMPTY);
            }

            if (var3 == 1) {
               var7 = var10.getCarried().split(1);
               this.minecraft.player.drop(var7, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(var7);
            }
         }
      } else {
         if (var1 != null && !var1.mayPickup(this.minecraft.player)) {
            return;
         }

         if (var1 == this.destroyItemSlot && var5) {
            for(int var11 = 0; var11 < this.minecraft.player.inventoryMenu.getItems().size(); ++var11) {
               this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, var11);
            }
         } else {
            ItemStack var6;
            if (selectedTab == CreativeModeTab.TAB_INVENTORY.getId()) {
               if (var1 == this.destroyItemSlot) {
                  this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
               } else if (var4 == ClickType.THROW && var1 != null && var1.hasItem()) {
                  var6 = var1.remove(var3 == 0 ? 1 : var1.getItem().getMaxStackSize());
                  var7 = var1.getItem();
                  this.minecraft.player.drop(var6, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(var6);
                  this.minecraft.gameMode.handleCreativeModeItemAdd(var7, ((CreativeModeInventoryScreen.SlotWrapper)var1).target.index);
               } else if (var4 == ClickType.THROW && !this.minecraft.player.inventory.getCarried().isEmpty()) {
                  this.minecraft.player.drop(this.minecraft.player.inventory.getCarried(), true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(this.minecraft.player.inventory.getCarried());
                  this.minecraft.player.inventory.setCarried(ItemStack.EMPTY);
               } else {
                  this.minecraft.player.inventoryMenu.clicked(var1 == null ? var2 : ((CreativeModeInventoryScreen.SlotWrapper)var1).target.index, var3, var4, this.minecraft.player);
                  this.minecraft.player.inventoryMenu.broadcastChanges();
               }
            } else {
               ItemStack var9;
               if (var4 != ClickType.QUICK_CRAFT && var1.container == CONTAINER) {
                  var10 = this.minecraft.player.inventory;
                  var7 = var10.getCarried();
                  ItemStack var13 = var1.getItem();
                  if (var4 == ClickType.SWAP) {
                     if (!var13.isEmpty() && var3 >= 0 && var3 < 9) {
                        var9 = var13.copy();
                        var9.setCount(var9.getMaxStackSize());
                        this.minecraft.player.inventory.setItem(var3, var9);
                        this.minecraft.player.inventoryMenu.broadcastChanges();
                     }

                     return;
                  }

                  if (var4 == ClickType.CLONE) {
                     if (var10.getCarried().isEmpty() && var1.hasItem()) {
                        var9 = var1.getItem().copy();
                        var9.setCount(var9.getMaxStackSize());
                        var10.setCarried(var9);
                     }

                     return;
                  }

                  if (var4 == ClickType.THROW) {
                     if (!var13.isEmpty()) {
                        var9 = var13.copy();
                        var9.setCount(var3 == 0 ? 1 : var9.getMaxStackSize());
                        this.minecraft.player.drop(var9, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop(var9);
                     }

                     return;
                  }

                  if (!var7.isEmpty() && !var13.isEmpty() && var7.sameItem(var13) && ItemStack.tagMatches(var7, var13)) {
                     if (var3 == 0) {
                        if (var5) {
                           var7.setCount(var7.getMaxStackSize());
                        } else if (var7.getCount() < var7.getMaxStackSize()) {
                           var7.grow(1);
                        }
                     } else {
                        var7.shrink(1);
                     }
                  } else if (!var13.isEmpty() && var7.isEmpty()) {
                     var10.setCarried(var13.copy());
                     var7 = var10.getCarried();
                     if (var5) {
                        var7.setCount(var7.getMaxStackSize());
                     }
                  } else if (var3 == 0) {
                     var10.setCarried(ItemStack.EMPTY);
                  } else {
                     var10.getCarried().shrink(1);
                  }
               } else if (this.menu != null) {
                  var6 = var1 == null ? ItemStack.EMPTY : ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(var1.index).getItem();
                  ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).clicked(var1 == null ? var2 : var1.index, var3, var4, this.minecraft.player);
                  if (AbstractContainerMenu.getQuickcraftHeader(var3) == 2) {
                     for(int var12 = 0; var12 < 9; ++var12) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(45 + var12).getItem(), 36 + var12);
                     }
                  } else if (var1 != null) {
                     var7 = ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).getSlot(var1.index).getItem();
                     this.minecraft.gameMode.handleCreativeModeItemAdd(var7, var1.index - ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.size() + 9 + 36);
                     int var8 = 45 + var3;
                     if (var4 == ClickType.SWAP) {
                        this.minecraft.gameMode.handleCreativeModeItemAdd(var6, var8 - ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.size() + 9 + 36);
                     } else if (var4 == ClickType.THROW && !var6.isEmpty()) {
                        var9 = var6.copy();
                        var9.setCount(var3 == 0 ? 1 : var9.getMaxStackSize());
                        this.minecraft.player.drop(var9, true);
                        this.minecraft.gameMode.handleCreativeModeItemDrop(var9);
                     }

                     this.minecraft.player.inventoryMenu.broadcastChanges();
                  }
               }
            }
         }
      }

   }

   private boolean isCreativeSlot(@Nullable Slot var1) {
      return var1 != null && var1.container == CONTAINER;
   }

   protected void checkEffectRendering() {
      int var1 = this.leftPos;
      super.checkEffectRendering();
      if (this.searchBox != null && this.leftPos != var1) {
         this.searchBox.setX(this.leftPos + 82);
      }

   }

   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         super.init();
         this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
         Font var10003 = this.font;
         int var10004 = this.leftPos + 82;
         int var10005 = this.topPos + 6;
         this.font.getClass();
         this.searchBox = new EditBox(var10003, var10004, var10005, 80, 9, I18n.get("itemGroup.search"));
         this.searchBox.setMaxLength(50);
         this.searchBox.setBordered(false);
         this.searchBox.setVisible(false);
         this.searchBox.setTextColor(16777215);
         this.children.add(this.searchBox);
         int var1 = selectedTab;
         selectedTab = -1;
         this.selectTab(CreativeModeTab.TABS[var1]);
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
         this.listener = new CreativeInventoryListener(this.minecraft);
         this.minecraft.player.inventoryMenu.addSlotListener(this.listener);
      } else {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      }

   }

   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.searchBox.getValue();
      this.init(var1, var2, var3);
      this.searchBox.setValue(var4);
      if (!this.searchBox.getValue().isEmpty()) {
         this.refreshSearchResults();
      }

   }

   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.inventory != null) {
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
      }

      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public boolean charTyped(char var1, int var2) {
      if (this.ignoreTextInput) {
         return false;
      } else if (selectedTab != CreativeModeTab.TAB_SEARCH.getId()) {
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
      if (selectedTab != CreativeModeTab.TAB_SEARCH.getId()) {
         if (this.minecraft.options.keyChat.matches(var1, var2)) {
            this.ignoreTextInput = true;
            this.selectTab(CreativeModeTab.TAB_SEARCH);
            return true;
         } else {
            return super.keyPressed(var1, var2, var3);
         }
      } else {
         boolean var4 = !this.isCreativeSlot(this.hoveredSlot) || this.hoveredSlot != null && this.hoveredSlot.hasItem();
         if (var4 && this.checkNumkeyPressed(var1, var2)) {
            this.ignoreTextInput = true;
            return true;
         } else {
            String var5 = this.searchBox.getValue();
            if (this.searchBox.keyPressed(var1, var2, var3)) {
               if (!Objects.equals(var5, this.searchBox.getValue())) {
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
      ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.clear();
      this.visibleTags.clear();
      String var1 = this.searchBox.getValue();
      if (var1.isEmpty()) {
         Iterator var2 = Registry.ITEM.iterator();

         while(var2.hasNext()) {
            Item var3 = (Item)var2.next();
            var3.fillItemCategory(CreativeModeTab.TAB_SEARCH, ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items);
         }
      } else {
         MutableSearchTree var4;
         if (var1.startsWith("#")) {
            var1 = var1.substring(1);
            var4 = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS);
            this.updateVisibleTags(var1);
         } else {
            var4 = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_NAMES);
         }

         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.addAll(var4.search(var1.toLowerCase(Locale.ROOT)));
      }

      this.scrollOffs = 0.0F;
      ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(0.0F);
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

      TagCollection var6 = ItemTags.getAllTags();
      var6.getAvailableTags().stream().filter(var3).forEach((var2x) -> {
         Tag var10000 = (Tag)this.visibleTags.put(var2x, var6.getTag(var2x));
      });
   }

   protected void renderLabels(int var1, int var2) {
      CreativeModeTab var3 = CreativeModeTab.TABS[selectedTab];
      if (var3.showTitle()) {
         RenderSystem.disableBlend();
         this.font.draw(I18n.get(var3.getName()), 8.0F, 6.0F, 4210752);
      }

   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         double var6 = var1 - (double)this.leftPos;
         double var8 = var3 - (double)this.topPos;
         CreativeModeTab[] var10 = CreativeModeTab.TABS;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            CreativeModeTab var13 = var10[var12];
            if (this.checkTabClicked(var13, var6, var8)) {
               return true;
            }
         }

         if (selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && this.insideScrollbar(var1, var3)) {
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
         CreativeModeTab[] var10 = CreativeModeTab.TABS;
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            CreativeModeTab var13 = var10[var12];
            if (this.checkTabClicked(var13, var6, var8)) {
               this.selectTab(var13);
               return true;
            }
         }
      }

      return super.mouseReleased(var1, var3, var5);
   }

   private boolean canScroll() {
      return selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && CreativeModeTab.TABS[selectedTab].canScroll() && ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).canScroll();
   }

   private void selectTab(CreativeModeTab var1) {
      int var2 = selectedTab;
      selectedTab = var1.getId();
      this.quickCraftSlots.clear();
      ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.clear();
      int var4;
      int var6;
      if (var1 == CreativeModeTab.TAB_HOTBAR) {
         HotbarManager var3 = this.minecraft.getHotbarManager();

         for(var4 = 0; var4 < 9; ++var4) {
            Hotbar var5 = var3.get(var4);
            if (var5.isEmpty()) {
               for(var6 = 0; var6 < 9; ++var6) {
                  if (var6 == var4) {
                     ItemStack var7 = new ItemStack(Items.PAPER);
                     var7.getOrCreateTagElement("CustomCreativeLock");
                     String var8 = this.minecraft.options.keyHotbarSlots[var4].getTranslatedKeyMessage();
                     String var9 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                     var7.setHoverName(new TranslatableComponent("inventory.hotbarInfo", new Object[]{var9, var8}));
                     ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.add(var7);
                  } else {
                     ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.add(ItemStack.EMPTY);
                  }
               }
            } else {
               ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.addAll(var5);
            }
         }
      } else if (var1 != CreativeModeTab.TAB_SEARCH) {
         var1.fillItemList(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items);
      }

      if (var1 == CreativeModeTab.TAB_INVENTORY) {
         InventoryMenu var10 = this.minecraft.player.inventoryMenu;
         if (this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf(((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots);
         }

         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.clear();

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

            CreativeModeInventoryScreen.SlotWrapper var13 = new CreativeModeInventoryScreen.SlotWrapper((Slot)var10.slots.get(var4), var4, var11, var6);
            ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(var13);
         }

         this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.add(this.destroyItemSlot);
      } else if (var2 == CreativeModeTab.TAB_INVENTORY.getId()) {
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.clear();
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).slots.addAll(this.originalSlots);
         this.originalSlots = null;
      }

      if (this.searchBox != null) {
         if (var1 == CreativeModeTab.TAB_SEARCH) {
            this.searchBox.setVisible(true);
            this.searchBox.setCanLoseFocus(false);
            this.searchBox.setFocus(true);
            if (var2 != var1.getId()) {
               this.searchBox.setValue("");
            }

            this.refreshSearchResults();
         } else {
            this.searchBox.setVisible(false);
            this.searchBox.setCanLoseFocus(true);
            this.searchBox.setFocus(false);
            this.searchBox.setValue("");
         }
      }

      this.scrollOffs = 0.0F;
      ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(0.0F);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (!this.canScroll()) {
         return false;
      } else {
         int var7 = (((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).items.size() + 9 - 1) / 9 - 5;
         this.scrollOffs = (float)((double)this.scrollOffs - var5 / (double)var7);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
         return true;
      }
   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      boolean var8 = var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
      this.hasClickedOutside = var8 && !this.checkTabClicked(CreativeModeTab.TABS[selectedTab], var1, var3);
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
         ((CreativeModeInventoryScreen.ItemPickerMenu)this.menu).scrollTo(this.scrollOffs);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      super.render(var1, var2, var3);
      CreativeModeTab[] var4 = CreativeModeTab.TABS;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         CreativeModeTab var7 = var4[var6];
         if (this.checkTabHovering(var7, var1, var2)) {
            break;
         }
      }

      if (this.destroyItemSlot != null && selectedTab == CreativeModeTab.TAB_INVENTORY.getId() && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, (double)var1, (double)var2)) {
         this.renderTooltip(I18n.get("inventory.binSlot"), var1, var2);
      }

      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderTooltip(var1, var2);
   }

   protected void renderTooltip(ItemStack var1, int var2, int var3) {
      if (selectedTab == CreativeModeTab.TAB_SEARCH.getId()) {
         List var4 = var1.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
         ArrayList var5 = Lists.newArrayListWithCapacity(var4.size());
         Iterator var6 = var4.iterator();

         while(var6.hasNext()) {
            Component var7 = (Component)var6.next();
            var5.add(var7.getColoredString());
         }

         Item var14 = var1.getItem();
         CreativeModeTab var15 = var14.getItemCategory();
         if (var15 == null && var14 == Items.ENCHANTED_BOOK) {
            Map var8 = EnchantmentHelper.getEnchantments(var1);
            if (var8.size() == 1) {
               Enchantment var9 = (Enchantment)var8.keySet().iterator().next();
               CreativeModeTab[] var10 = CreativeModeTab.TABS;
               int var11 = var10.length;

               for(int var12 = 0; var12 < var11; ++var12) {
                  CreativeModeTab var13 = var10[var12];
                  if (var13.hasEnchantmentCategory(var9.category)) {
                     var15 = var13;
                     break;
                  }
               }
            }
         }

         this.visibleTags.forEach((var2x, var3x) -> {
            if (var3x.contains(var14)) {
               var5.add(1, "" + ChatFormatting.BOLD + ChatFormatting.DARK_PURPLE + "#" + var2x);
            }

         });
         if (var15 != null) {
            var5.add(1, "" + ChatFormatting.BOLD + ChatFormatting.BLUE + I18n.get(var15.getName()));
         }

         for(int var16 = 0; var16 < var5.size(); ++var16) {
            if (var16 == 0) {
               var5.set(var16, var1.getRarity().color + (String)var5.get(var16));
            } else {
               var5.set(var16, ChatFormatting.GRAY + (String)var5.get(var16));
            }
         }

         this.renderTooltip(var5, var2, var3);
      } else {
         super.renderTooltip(var1, var2, var3);
      }

   }

   protected void renderBg(float var1, int var2, int var3) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      CreativeModeTab var4 = CreativeModeTab.TABS[selectedTab];
      CreativeModeTab[] var5 = CreativeModeTab.TABS;
      int var6 = var5.length;

      int var7;
      for(var7 = 0; var7 < var6; ++var7) {
         CreativeModeTab var8 = var5[var7];
         this.minecraft.getTextureManager().bind(CREATIVE_TABS_LOCATION);
         if (var8.getId() != selectedTab) {
            this.renderTabButton(var8);
         }
      }

      this.minecraft.getTextureManager().bind(new ResourceLocation("textures/gui/container/creative_inventory/tab_" + var4.getBackgroundSuffix()));
      this.blit(this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.searchBox.render(var2, var3, var1);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var9 = this.leftPos + 175;
      var6 = this.topPos + 18;
      var7 = var6 + 112;
      this.minecraft.getTextureManager().bind(CREATIVE_TABS_LOCATION);
      if (var4.canScroll()) {
         this.blit(var9, var6 + (int)((float)(var7 - var6 - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
      }

      this.renderTabButton(var4);
      if (var4 == CreativeModeTab.TAB_INVENTORY) {
         InventoryScreen.renderEntityInInventory(this.leftPos + 88, this.topPos + 45, 20, (float)(this.leftPos + 88 - var2), (float)(this.topPos + 45 - 30 - var3), this.minecraft.player);
      }

   }

   protected boolean checkTabClicked(CreativeModeTab var1, double var2, double var4) {
      int var6 = var1.getColumn();
      int var7 = 28 * var6;
      byte var8 = 0;
      if (var1.isAlignedRight()) {
         var7 = this.imageWidth - 28 * (6 - var6) + 2;
      } else if (var6 > 0) {
         var7 += var6;
      }

      int var9;
      if (var1.isTopRow()) {
         var9 = var8 - 32;
      } else {
         var9 = var8 + this.imageHeight;
      }

      return var2 >= (double)var7 && var2 <= (double)(var7 + 28) && var4 >= (double)var9 && var4 <= (double)(var9 + 32);
   }

   protected boolean checkTabHovering(CreativeModeTab var1, int var2, int var3) {
      int var4 = var1.getColumn();
      int var5 = 28 * var4;
      byte var6 = 0;
      if (var1.isAlignedRight()) {
         var5 = this.imageWidth - 28 * (6 - var4) + 2;
      } else if (var4 > 0) {
         var5 += var4;
      }

      int var7;
      if (var1.isTopRow()) {
         var7 = var6 - 32;
      } else {
         var7 = var6 + this.imageHeight;
      }

      if (this.isHovering(var5 + 3, var7 + 3, 23, 27, (double)var2, (double)var3)) {
         this.renderTooltip(I18n.get(var1.getName()), var2, var3);
         return true;
      } else {
         return false;
      }
   }

   protected void renderTabButton(CreativeModeTab var1) {
      boolean var2 = var1.getId() == selectedTab;
      boolean var3 = var1.isTopRow();
      int var4 = var1.getColumn();
      int var5 = var4 * 28;
      int var6 = 0;
      int var7 = this.leftPos + 28 * var4;
      int var8 = this.topPos;
      boolean var9 = true;
      if (var2) {
         var6 += 32;
      }

      if (var1.isAlignedRight()) {
         var7 = this.leftPos + this.imageWidth - 28 * (6 - var4);
      } else if (var4 > 0) {
         var7 += var4;
      }

      if (var3) {
         var8 -= 28;
      } else {
         var6 += 64;
         var8 += this.imageHeight - 4;
      }

      this.blit(var7, var8, var5, var6, 28, 32);
      this.setBlitOffset(100);
      this.itemRenderer.blitOffset = 100.0F;
      var7 += 6;
      var8 += 8 + (var3 ? 1 : -1);
      RenderSystem.enableRescaleNormal();
      ItemStack var10 = var1.getIconItem();
      this.itemRenderer.renderAndDecorateItem(var10, var7, var8);
      this.itemRenderer.renderGuiItemDecorations(this.font, var10, var7, var8);
      this.itemRenderer.blitOffset = 0.0F;
      this.setBlitOffset(0);
   }

   public int getSelectedTab() {
      return selectedTab;
   }

   public static void handleHotbarLoadOrSave(Minecraft var0, int var1, boolean var2, boolean var3) {
      LocalPlayer var4 = var0.player;
      HotbarManager var5 = var0.getHotbarManager();
      Hotbar var6 = var5.get(var1);
      int var7;
      if (var2) {
         for(var7 = 0; var7 < Inventory.getSelectionSize(); ++var7) {
            ItemStack var8 = ((ItemStack)var6.get(var7)).copy();
            var4.inventory.setItem(var7, var8);
            var0.gameMode.handleCreativeModeItemAdd(var8, 36 + var7);
         }

         var4.inventoryMenu.broadcastChanges();
      } else if (var3) {
         for(var7 = 0; var7 < Inventory.getSelectionSize(); ++var7) {
            var6.set(var7, var4.inventory.getItem(var7).copy());
         }

         String var9 = var0.options.keyHotbarSlots[var1].getTranslatedKeyMessage();
         String var10 = var0.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
         var0.gui.setOverlayMessage((Component)(new TranslatableComponent("inventory.hotbarSaved", new Object[]{var10, var9})), false);
         var5.save();
      }

   }

   static {
      selectedTab = CreativeModeTab.TAB_BUILDING_BLOCKS.getId();
   }

   static class CustomCreativeSlot extends Slot {
      public CustomCreativeSlot(Container var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      public boolean mayPickup(Player var1) {
         if (super.mayPickup(var1) && this.hasItem()) {
            return this.getItem().getTagElement("CustomCreativeLock") == null;
         } else {
            return !this.hasItem();
         }
      }
   }

   static class SlotWrapper extends Slot {
      private final Slot target;

      public SlotWrapper(Slot var1, int var2, int var3, int var4) {
         super(var1.container, var2, var3, var4);
         this.target = var1;
      }

      public ItemStack onTake(Player var1, ItemStack var2) {
         return this.target.onTake(var1, var2);
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
      public Pair getNoItemIcon() {
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

   public static class ItemPickerMenu extends AbstractContainerMenu {
      public final NonNullList items = NonNullList.create();

      public ItemPickerMenu(Player var1) {
         super((MenuType)null, 0);
         Inventory var2 = var1.inventory;

         int var3;
         for(var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 9; ++var4) {
               this.addSlot(new CreativeModeInventoryScreen.CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, var3 * 9 + var4, 9 + var4 * 18, 18 + var3 * 18));
            }
         }

         for(var3 = 0; var3 < 9; ++var3) {
            this.addSlot(new Slot(var2, var3, 9 + var3 * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      public boolean stillValid(Player var1) {
         return true;
      }

      public void scrollTo(float var1) {
         int var2 = (this.items.size() + 9 - 1) / 9 - 5;
         int var3 = (int)((double)(var1 * (float)var2) + 0.5D);
         if (var3 < 0) {
            var3 = 0;
         }

         for(int var4 = 0; var4 < 5; ++var4) {
            for(int var5 = 0; var5 < 9; ++var5) {
               int var6 = var5 + (var4 + var3) * 9;
               if (var6 >= 0 && var6 < this.items.size()) {
                  CreativeModeInventoryScreen.CONTAINER.setItem(var5 + var4 * 9, (ItemStack)this.items.get(var6));
               } else {
                  CreativeModeInventoryScreen.CONTAINER.setItem(var5 + var4 * 9, ItemStack.EMPTY);
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
               var3.set(ItemStack.EMPTY);
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
   }
}
