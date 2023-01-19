package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.HotbarManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.inventory.Hotbar;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class CreativeModeInventoryScreen extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
   private static final ResourceLocation CREATIVE_TABS_LOCATION = new ResourceLocation("textures/gui/container/creative_inventory/tabs.png");
   private static final String GUI_CREATIVE_TAB_PREFIX = "textures/gui/container/creative_inventory/tab_";
   private static final String CUSTOM_SLOT_LOCK = "CustomCreativeLock";
   private static final int NUM_ROWS = 5;
   private static final int NUM_COLS = 9;
   private static final int TAB_WIDTH = 28;
   private static final int TAB_HEIGHT = 32;
   private static final int SCROLLER_WIDTH = 12;
   private static final int SCROLLER_HEIGHT = 15;
   static final SimpleContainer CONTAINER = new SimpleContainer(45);
   private static final Component TRASH_SLOT_TOOLTIP = Component.translatable("inventory.binSlot");
   private static final int TEXT_COLOR = 16777215;
   private static int selectedTab = CreativeModeTab.TAB_BUILDING_BLOCKS.getId();
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
   private final Set<TagKey<Item>> visibleTags = new HashSet<>();

   public CreativeModeInventoryScreen(Player var1) {
      super(new CreativeModeInventoryScreen.ItemPickerMenu(var1), var1.getInventory(), CommonComponents.EMPTY);
      var1.containerMenu = this.menu;
      this.passEvents = true;
      this.imageHeight = 136;
      this.imageWidth = 195;
   }

   @Override
   public void containerTick() {
      super.containerTick();
      if (!this.minecraft.gameMode.hasInfiniteItems()) {
         this.minecraft.setScreen(new InventoryScreen(this.minecraft.player));
      } else if (this.searchBox != null) {
         this.searchBox.tick();
      }
   }

   @Override
   protected void slotClicked(@Nullable Slot var1, int var2, int var3, ClickType var4) {
      if (this.isCreativeSlot(var1)) {
         this.searchBox.moveCursorToEnd();
         this.searchBox.setHighlightPos(0);
      }

      boolean var5 = var4 == ClickType.QUICK_MOVE;
      var4 = var2 == -999 && var4 == ClickType.PICKUP ? ClickType.THROW : var4;
      if (var1 == null && selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && var4 != ClickType.QUICK_CRAFT) {
         if (!this.menu.getCarried().isEmpty() && this.hasClickedOutside) {
            if (var3 == 0) {
               this.minecraft.player.drop(this.menu.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
               this.menu.setCarried(ItemStack.EMPTY);
            }

            if (var3 == 1) {
               ItemStack var15 = this.menu.getCarried().split(1);
               this.minecraft.player.drop(var15, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(var15);
            }
         }
      } else {
         if (var1 != null && !var1.mayPickup(this.minecraft.player)) {
            return;
         }

         if (var1 == this.destroyItemSlot && var5) {
            for(int var14 = 0; var14 < this.minecraft.player.inventoryMenu.getItems().size(); ++var14) {
               this.minecraft.gameMode.handleCreativeModeItemAdd(ItemStack.EMPTY, var14);
            }
         } else if (selectedTab == CreativeModeTab.TAB_INVENTORY.getId()) {
            if (var1 == this.destroyItemSlot) {
               this.menu.setCarried(ItemStack.EMPTY);
            } else if (var4 == ClickType.THROW && var1 != null && var1.hasItem()) {
               ItemStack var6 = var1.remove(var3 == 0 ? 1 : var1.getItem().getMaxStackSize());
               ItemStack var7 = var1.getItem();
               this.minecraft.player.drop(var6, true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(var6);
               this.minecraft.gameMode.handleCreativeModeItemAdd(var7, ((CreativeModeInventoryScreen.SlotWrapper)var1).target.index);
            } else if (var4 == ClickType.THROW && !this.menu.getCarried().isEmpty()) {
               this.minecraft.player.drop(this.menu.getCarried(), true);
               this.minecraft.gameMode.handleCreativeModeItemDrop(this.menu.getCarried());
               this.menu.setCarried(ItemStack.EMPTY);
            } else {
               this.minecraft
                  .player
                  .inventoryMenu
                  .clicked(var1 == null ? var2 : ((CreativeModeInventoryScreen.SlotWrapper)var1).target.index, var3, var4, this.minecraft.player);
               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         } else if (var4 != ClickType.QUICK_CRAFT && var1.container == CONTAINER) {
            ItemStack var12 = this.menu.getCarried();
            ItemStack var18 = var1.getItem();
            if (var4 == ClickType.SWAP) {
               if (!var18.isEmpty()) {
                  ItemStack var21 = var18.copy();
                  var21.setCount(var21.getMaxStackSize());
                  this.minecraft.player.getInventory().setItem(var3, var21);
                  this.minecraft.player.inventoryMenu.broadcastChanges();
               }

               return;
            }

            if (var4 == ClickType.CLONE) {
               if (this.menu.getCarried().isEmpty() && var1.hasItem()) {
                  ItemStack var20 = var1.getItem().copy();
                  var20.setCount(var20.getMaxStackSize());
                  this.menu.setCarried(var20);
               }

               return;
            }

            if (var4 == ClickType.THROW) {
               if (!var18.isEmpty()) {
                  ItemStack var19 = var18.copy();
                  var19.setCount(var3 == 0 ? 1 : var19.getMaxStackSize());
                  this.minecraft.player.drop(var19, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(var19);
               }

               return;
            }

            if (!var12.isEmpty() && !var18.isEmpty() && var12.sameItem(var18) && ItemStack.tagMatches(var12, var18)) {
               if (var3 == 0) {
                  if (var5) {
                     var12.setCount(var12.getMaxStackSize());
                  } else if (var12.getCount() < var12.getMaxStackSize()) {
                     var12.grow(1);
                  }
               } else {
                  var12.shrink(1);
               }
            } else if (!var18.isEmpty() && var12.isEmpty()) {
               this.menu.setCarried(var18.copy());
               var12 = this.menu.getCarried();
               if (var5) {
                  var12.setCount(var12.getMaxStackSize());
               }
            } else if (var3 == 0) {
               this.menu.setCarried(ItemStack.EMPTY);
            } else {
               this.menu.getCarried().shrink(1);
            }
         } else if (this.menu != null) {
            ItemStack var11 = var1 == null ? ItemStack.EMPTY : this.menu.getSlot(var1.index).getItem();
            this.menu.clicked(var1 == null ? var2 : var1.index, var3, var4, this.minecraft.player);
            if (AbstractContainerMenu.getQuickcraftHeader(var3) == 2) {
               for(int var16 = 0; var16 < 9; ++var16) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(this.menu.getSlot(45 + var16).getItem(), 36 + var16);
               }
            } else if (var1 != null) {
               ItemStack var17 = this.menu.getSlot(var1.index).getItem();
               this.minecraft.gameMode.handleCreativeModeItemAdd(var17, var1.index - this.menu.slots.size() + 9 + 36);
               int var8 = 45 + var3;
               if (var4 == ClickType.SWAP) {
                  this.minecraft.gameMode.handleCreativeModeItemAdd(var11, var8 - this.menu.slots.size() + 9 + 36);
               } else if (var4 == ClickType.THROW && !var11.isEmpty()) {
                  ItemStack var9 = var11.copy();
                  var9.setCount(var3 == 0 ? 1 : var9.getMaxStackSize());
                  this.minecraft.player.drop(var9, true);
                  this.minecraft.gameMode.handleCreativeModeItemDrop(var9);
               }

               this.minecraft.player.inventoryMenu.broadcastChanges();
            }
         }
      }
   }

   private boolean isCreativeSlot(@Nullable Slot var1) {
      return var1 != null && var1.container == CONTAINER;
   }

   @Override
   protected void init() {
      if (this.minecraft.gameMode.hasInfiniteItems()) {
         super.init();
         this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
         this.searchBox = new EditBox(this.font, this.leftPos + 82, this.topPos + 6, 80, 9, Component.translatable("itemGroup.search"));
         this.searchBox.setMaxLength(50);
         this.searchBox.setBordered(false);
         this.searchBox.setVisible(false);
         this.searchBox.setTextColor(16777215);
         this.addWidget(this.searchBox);
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

   @Override
   public void resize(Minecraft var1, int var2, int var3) {
      String var4 = this.searchBox.getValue();
      this.init(var1, var2, var3);
      this.searchBox.setValue(var4);
      if (!this.searchBox.getValue().isEmpty()) {
         this.refreshSearchResults();
      }
   }

   @Override
   public void removed() {
      super.removed();
      if (this.minecraft.player != null && this.minecraft.player.getInventory() != null) {
         this.minecraft.player.inventoryMenu.removeSlotListener(this.listener);
      }

      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   @Override
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

   @Override
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

   @Override
   public boolean keyReleased(int var1, int var2, int var3) {
      this.ignoreTextInput = false;
      return super.keyReleased(var1, var2, var3);
   }

   private void refreshSearchResults() {
      this.menu.items.clear();
      this.visibleTags.clear();
      String var1 = this.searchBox.getValue();
      if (var1.isEmpty()) {
         for(Item var3 : Registry.ITEM) {
            var3.fillItemCategory(CreativeModeTab.TAB_SEARCH, this.menu.items);
         }
      } else {
         SearchTree var4;
         if (var1.startsWith("#")) {
            var1 = var1.substring(1);
            var4 = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_TAGS);
            this.updateVisibleTags(var1);
         } else {
            var4 = this.minecraft.getSearchTree(SearchRegistry.CREATIVE_NAMES);
         }

         this.menu.items.addAll(var4.search(var1.toLowerCase(Locale.ROOT)));
      }

      this.scrollOffs = 0.0F;
      this.menu.scrollTo(0.0F);
   }

   private void updateVisibleTags(String var1) {
      int var2 = var1.indexOf(58);
      Predicate var3;
      if (var2 == -1) {
         var3 = var1x -> var1x.getPath().contains(var1);
      } else {
         String var4 = var1.substring(0, var2).trim();
         String var5 = var1.substring(var2 + 1).trim();
         var3 = var2x -> var2x.getNamespace().contains(var4) && var2x.getPath().contains(var5);
      }

      Registry.ITEM.getTagNames().filter(var1x -> var3.test(var1x.location())).forEach(this.visibleTags::add);
   }

   @Override
   protected void renderLabels(PoseStack var1, int var2, int var3) {
      CreativeModeTab var4 = CreativeModeTab.TABS[selectedTab];
      if (var4.showTitle()) {
         RenderSystem.disableBlend();
         this.font.draw(var1, var4.getDisplayName(), 8.0F, 6.0F, 4210752);
      }
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      if (var5 == 0) {
         double var6 = var1 - (double)this.leftPos;
         double var8 = var3 - (double)this.topPos;

         for(CreativeModeTab var13 : CreativeModeTab.TABS) {
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

   @Override
   public boolean mouseReleased(double var1, double var3, int var5) {
      if (var5 == 0) {
         double var6 = var1 - (double)this.leftPos;
         double var8 = var3 - (double)this.topPos;
         this.scrolling = false;

         for(CreativeModeTab var13 : CreativeModeTab.TABS) {
            if (this.checkTabClicked(var13, var6, var8)) {
               this.selectTab(var13);
               return true;
            }
         }
      }

      return super.mouseReleased(var1, var3, var5);
   }

   private boolean canScroll() {
      return selectedTab != CreativeModeTab.TAB_INVENTORY.getId() && CreativeModeTab.TABS[selectedTab].canScroll() && this.menu.canScroll();
   }

   private void selectTab(CreativeModeTab var1) {
      int var2 = selectedTab;
      selectedTab = var1.getId();
      this.quickCraftSlots.clear();
      this.menu.items.clear();
      this.clearDraggingState();
      if (var1 == CreativeModeTab.TAB_HOTBAR) {
         HotbarManager var3 = this.minecraft.getHotbarManager();

         for(int var4 = 0; var4 < 9; ++var4) {
            Hotbar var5 = var3.get(var4);
            if (var5.isEmpty()) {
               for(int var6 = 0; var6 < 9; ++var6) {
                  if (var6 == var4) {
                     ItemStack var7 = new ItemStack(Items.PAPER);
                     var7.getOrCreateTagElement("CustomCreativeLock");
                     Component var8 = this.minecraft.options.keyHotbarSlots[var4].getTranslatedKeyMessage();
                     Component var9 = this.minecraft.options.keySaveHotbarActivator.getTranslatedKeyMessage();
                     var7.setHoverName(Component.translatable("inventory.hotbarInfo", var9, var8));
                     this.menu.items.add(var7);
                  } else {
                     this.menu.items.add(ItemStack.EMPTY);
                  }
               }
            } else {
               this.menu.items.addAll(var5);
            }
         }
      } else if (var1 != CreativeModeTab.TAB_SEARCH) {
         var1.fillItemList(this.menu.items);
      }

      if (var1 == CreativeModeTab.TAB_INVENTORY) {
         InventoryMenu var10 = this.minecraft.player.inventoryMenu;
         if (this.originalSlots == null) {
            this.originalSlots = ImmutableList.copyOf(this.menu.slots);
         }

         this.menu.slots.clear();

         for(int var11 = 0; var11 < var10.slots.size(); ++var11) {
            int var12;
            int var13;
            if (var11 >= 5 && var11 < 9) {
               int var15 = var11 - 5;
               int var18 = var15 / 2;
               int var20 = var15 % 2;
               var12 = 54 + var18 * 54;
               var13 = 6 + var20 * 27;
            } else if (var11 >= 0 && var11 < 5) {
               var12 = -2000;
               var13 = -2000;
            } else if (var11 == 45) {
               var12 = 35;
               var13 = 20;
            } else {
               int var14 = var11 - 9;
               int var17 = var14 % 9;
               int var19 = var14 / 9;
               var12 = 9 + var17 * 18;
               if (var11 >= 36) {
                  var13 = 112;
               } else {
                  var13 = 54 + var19 * 18;
               }
            }

            CreativeModeInventoryScreen.SlotWrapper var16 = new CreativeModeInventoryScreen.SlotWrapper(var10.slots.get(var11), var11, var12, var13);
            this.menu.slots.add(var16);
         }

         this.destroyItemSlot = new Slot(CONTAINER, 0, 173, 112);
         this.menu.slots.add(this.destroyItemSlot);
      } else if (var2 == CreativeModeTab.TAB_INVENTORY.getId()) {
         this.menu.slots.clear();
         this.menu.slots.addAll(this.originalSlots);
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
      this.menu.scrollTo(0.0F);
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      if (!this.canScroll()) {
         return false;
      } else {
         int var7 = (this.menu.items.size() + 9 - 1) / 9 - 5;
         float var8 = (float)(var5 / (double)var7);
         this.scrollOffs = Mth.clamp(this.scrollOffs - var8, 0.0F, 1.0F);
         this.menu.scrollTo(this.scrollOffs);
         return true;
      }
   }

   @Override
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

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if (this.scrolling) {
         int var10 = this.topPos + 18;
         int var11 = var10 + 112;
         this.scrollOffs = ((float)var3 - (float)var10 - 7.5F) / ((float)(var11 - var10) - 15.0F);
         this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.menu.scrollTo(this.scrollOffs);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);

      for(CreativeModeTab var8 : CreativeModeTab.TABS) {
         if (this.checkTabHovering(var1, var8, var2, var3)) {
            break;
         }
      }

      if (this.destroyItemSlot != null
         && selectedTab == CreativeModeTab.TAB_INVENTORY.getId()
         && this.isHovering(this.destroyItemSlot.x, this.destroyItemSlot.y, 16, 16, (double)var2, (double)var3)) {
         this.renderTooltip(var1, TRASH_SLOT_TOOLTIP, var2, var3);
      }

      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      this.renderTooltip(var1, var2, var3);
   }

   @Override
   protected void renderTooltip(PoseStack var1, ItemStack var2, int var3, int var4) {
      if (selectedTab == CreativeModeTab.TAB_SEARCH.getId()) {
         List var5 = var2.getTooltipLines(
            this.minecraft.player, this.minecraft.options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL
         );
         ArrayList var6 = Lists.newArrayList(var5);
         Item var7 = var2.getItem();
         CreativeModeTab var8 = var7.getItemCategory();
         if (var8 == null && var2.is(Items.ENCHANTED_BOOK)) {
            Map var9 = EnchantmentHelper.getEnchantments(var2);
            if (var9.size() == 1) {
               Enchantment var10 = (Enchantment)var9.keySet().iterator().next();

               for(CreativeModeTab var14 : CreativeModeTab.TABS) {
                  if (var14.hasEnchantmentCategory(var10.category)) {
                     var8 = var14;
                     break;
                  }
               }
            }
         }

         this.visibleTags.forEach(var2x -> {
            if (var2.is(var2x)) {
               var6.add(1, Component.literal("#" + var2x.location()).withStyle(ChatFormatting.DARK_PURPLE));
            }
         });
         if (var8 != null) {
            var6.add(1, var8.getDisplayName().copy().withStyle(ChatFormatting.BLUE));
         }

         this.renderTooltip(var1, var6, var2.getTooltipImage(), var3, var4);
      } else {
         super.renderTooltip(var1, var2, var3, var4);
      }
   }

   @Override
   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      CreativeModeTab var5 = CreativeModeTab.TABS[selectedTab];

      for(CreativeModeTab var9 : CreativeModeTab.TABS) {
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, CREATIVE_TABS_LOCATION);
         if (var9.getId() != selectedTab) {
            this.renderTabButton(var1, var9);
         }
      }

      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, new ResourceLocation("textures/gui/container/creative_inventory/tab_" + var5.getBackgroundSuffix()));
      this.blit(var1, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
      this.searchBox.render(var1, var3, var4, var2);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int var10 = this.leftPos + 175;
      int var11 = this.topPos + 18;
      int var12 = var11 + 112;
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, CREATIVE_TABS_LOCATION);
      if (var5.canScroll()) {
         this.blit(var1, var10, var11 + (int)((float)(var12 - var11 - 17) * this.scrollOffs), 232 + (this.canScroll() ? 0 : 12), 0, 12, 15);
      }

      this.renderTabButton(var1, var5);
      if (var5 == CreativeModeTab.TAB_INVENTORY) {
         InventoryScreen.renderEntityInInventory(
            this.leftPos + 88, this.topPos + 45, 20, (float)(this.leftPos + 88 - var3), (float)(this.topPos + 45 - 30 - var4), this.minecraft.player
         );
      }
   }

   protected boolean checkTabClicked(CreativeModeTab var1, double var2, double var4) {
      int var6 = var1.getColumn();
      int var7 = 28 * var6;
      int var8 = 0;
      if (var1.isAlignedRight()) {
         var7 = this.imageWidth - 28 * (6 - var6) + 2;
      } else if (var6 > 0) {
         var7 += var6;
      }

      if (var1.isTopRow()) {
         var8 -= 32;
      } else {
         var8 += this.imageHeight;
      }

      return var2 >= (double)var7 && var2 <= (double)(var7 + 28) && var4 >= (double)var8 && var4 <= (double)(var8 + 32);
   }

   protected boolean checkTabHovering(PoseStack var1, CreativeModeTab var2, int var3, int var4) {
      int var5 = var2.getColumn();
      int var6 = 28 * var5;
      int var7 = 0;
      if (var2.isAlignedRight()) {
         var6 = this.imageWidth - 28 * (6 - var5) + 2;
      } else if (var5 > 0) {
         var6 += var5;
      }

      if (var2.isTopRow()) {
         var7 -= 32;
      } else {
         var7 += this.imageHeight;
      }

      if (this.isHovering(var6 + 3, var7 + 3, 23, 27, (double)var3, (double)var4)) {
         this.renderTooltip(var1, var2.getDisplayName(), var3, var4);
         return true;
      } else {
         return false;
      }
   }

   protected void renderTabButton(PoseStack var1, CreativeModeTab var2) {
      boolean var3 = var2.getId() == selectedTab;
      boolean var4 = var2.isTopRow();
      int var5 = var2.getColumn();
      int var6 = var5 * 28;
      int var7 = 0;
      int var8 = this.leftPos + 28 * var5;
      int var9 = this.topPos;
      boolean var10 = true;
      if (var3) {
         var7 += 32;
      }

      if (var2.isAlignedRight()) {
         var8 = this.leftPos + this.imageWidth - 28 * (6 - var5);
      } else if (var5 > 0) {
         var8 += var5;
      }

      if (var4) {
         var9 -= 28;
      } else {
         var7 += 64;
         var9 += this.imageHeight - 4;
      }

      this.blit(var1, var8, var9, var6, var7, 28, 32);
      this.itemRenderer.blitOffset = 100.0F;
      var8 += 6;
      var9 += 8 + (var4 ? 1 : -1);
      ItemStack var11 = var2.getIconItem();
      this.itemRenderer.renderAndDecorateItem(var11, var8, var9);
      this.itemRenderer.renderGuiItemDecorations(this.font, var11, var8, var9);
      this.itemRenderer.blitOffset = 0.0F;
   }

   public int getSelectedTab() {
      return selectedTab;
   }

   public static void handleHotbarLoadOrSave(Minecraft var0, int var1, boolean var2, boolean var3) {
      LocalPlayer var4 = var0.player;
      HotbarManager var5 = var0.getHotbarManager();
      Hotbar var6 = var5.get(var1);
      if (var2) {
         for(int var7 = 0; var7 < Inventory.getSelectionSize(); ++var7) {
            ItemStack var8 = ((ItemStack)var6.get(var7)).copy();
            var4.getInventory().setItem(var7, var8);
            var0.gameMode.handleCreativeModeItemAdd(var8, 36 + var7);
         }

         var4.inventoryMenu.broadcastChanges();
      } else if (var3) {
         for(int var9 = 0; var9 < Inventory.getSelectionSize(); ++var9) {
            var6.set(var9, var4.getInventory().getItem(var9).copy());
         }

         Component var10 = var0.options.keyHotbarSlots[var1].getTranslatedKeyMessage();
         Component var11 = var0.options.keyLoadHotbarActivator.getTranslatedKeyMessage();
         var0.gui.setOverlayMessage(Component.translatable("inventory.hotbarSaved", var11, var10), false);
         var5.save();
      }
   }

   static class CustomCreativeSlot extends Slot {
      public CustomCreativeSlot(Container var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      @Override
      public boolean mayPickup(Player var1) {
         if (super.mayPickup(var1) && this.hasItem()) {
            return this.getItem().getTagElement("CustomCreativeLock") == null;
         } else {
            return !this.hasItem();
         }
      }
   }

   public static class ItemPickerMenu extends AbstractContainerMenu {
      public final NonNullList<ItemStack> items = NonNullList.create();
      private final AbstractContainerMenu inventoryMenu;

      public ItemPickerMenu(Player var1) {
         super(null, 0);
         this.inventoryMenu = var1.inventoryMenu;
         Inventory var2 = var1.getInventory();

         for(int var3 = 0; var3 < 5; ++var3) {
            for(int var4 = 0; var4 < 9; ++var4) {
               this.addSlot(
                  new CreativeModeInventoryScreen.CustomCreativeSlot(CreativeModeInventoryScreen.CONTAINER, var3 * 9 + var4, 9 + var4 * 18, 18 + var3 * 18)
               );
            }
         }

         for(int var5 = 0; var5 < 9; ++var5) {
            this.addSlot(new Slot(var2, var5, 9 + var5 * 18, 112));
         }

         this.scrollTo(0.0F);
      }

      @Override
      public boolean stillValid(Player var1) {
         return true;
      }

      public void scrollTo(float var1) {
         int var2 = (this.items.size() + 9 - 1) / 9 - 5;
         int var3 = (int)((double)(var1 * (float)var2) + 0.5);
         if (var3 < 0) {
            var3 = 0;
         }

         for(int var4 = 0; var4 < 5; ++var4) {
            for(int var5 = 0; var5 < 9; ++var5) {
               int var6 = var5 + (var4 + var3) * 9;
               if (var6 >= 0 && var6 < this.items.size()) {
                  CreativeModeInventoryScreen.CONTAINER.setItem(var5 + var4 * 9, this.items.get(var6));
               } else {
                  CreativeModeInventoryScreen.CONTAINER.setItem(var5 + var4 * 9, ItemStack.EMPTY);
               }
            }
         }
      }

      public boolean canScroll() {
         return this.items.size() > 45;
      }

      @Override
      public ItemStack quickMoveStack(Player var1, int var2) {
         if (var2 >= this.slots.size() - 9 && var2 < this.slots.size()) {
            Slot var3 = this.slots.get(var2);
            if (var3 != null && var3.hasItem()) {
               var3.set(ItemStack.EMPTY);
            }
         }

         return ItemStack.EMPTY;
      }

      @Override
      public boolean canTakeItemForPickAll(ItemStack var1, Slot var2) {
         return var2.container != CreativeModeInventoryScreen.CONTAINER;
      }

      @Override
      public boolean canDragTo(Slot var1) {
         return var1.container != CreativeModeInventoryScreen.CONTAINER;
      }

      @Override
      public ItemStack getCarried() {
         return this.inventoryMenu.getCarried();
      }

      @Override
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

      @Override
      public void onTake(Player var1, ItemStack var2) {
         this.target.onTake(var1, var2);
      }

      @Override
      public boolean mayPlace(ItemStack var1) {
         return this.target.mayPlace(var1);
      }

      @Override
      public ItemStack getItem() {
         return this.target.getItem();
      }

      @Override
      public boolean hasItem() {
         return this.target.hasItem();
      }

      @Override
      public void set(ItemStack var1) {
         this.target.set(var1);
      }

      @Override
      public void setChanged() {
         this.target.setChanged();
      }

      @Override
      public int getMaxStackSize() {
         return this.target.getMaxStackSize();
      }

      @Override
      public int getMaxStackSize(ItemStack var1) {
         return this.target.getMaxStackSize(var1);
      }

      @Nullable
      @Override
      public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
         return this.target.getNoItemIcon();
      }

      @Override
      public ItemStack remove(int var1) {
         return this.target.remove(var1);
      }

      @Override
      public boolean isActive() {
         return this.target.isActive();
      }

      @Override
      public boolean mayPickup(Player var1) {
         return this.target.mayPickup(var1);
      }
   }
}
