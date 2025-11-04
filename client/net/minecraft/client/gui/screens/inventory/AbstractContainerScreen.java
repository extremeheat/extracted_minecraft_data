package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
   public static final Identifier INVENTORY_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/inventory.png");
   private static final Identifier SLOT_HIGHLIGHT_BACK_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_back");
   private static final Identifier SLOT_HIGHLIGHT_FRONT_SPRITE = Identifier.withDefaultNamespace("container/slot_highlight_front");
   protected static final int BACKGROUND_TEXTURE_WIDTH = 256;
   protected static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final float SNAPBACK_SPEED = 100.0F;
   private static final int QUICKDROP_DELAY = 500;
   protected int imageWidth = 176;
   protected int imageHeight = 166;
   protected int titleLabelX;
   protected int titleLabelY;
   protected int inventoryLabelX;
   protected int inventoryLabelY;
   private final List<ItemSlotMouseAction> itemSlotMouseActions;
   protected final T menu;
   protected final Component playerInventoryTitle;
   protected @Nullable Slot hoveredSlot;
   private @Nullable Slot clickedSlot;
   private @Nullable Slot quickdropSlot;
   private @Nullable Slot lastClickSlot;
   private @Nullable SnapbackData snapbackData;
   protected int leftPos;
   protected int topPos;
   private boolean isSplittingStack;
   private ItemStack draggingItem;
   private long quickdropTime;
   protected final Set<Slot> quickCraftSlots;
   protected boolean isQuickCrafting;
   private int quickCraftingType;
   private int quickCraftingButton;
   private boolean skipNextRelease;
   private int quickCraftingRemainder;
   private boolean doubleclick;
   private ItemStack lastQuickMoved;

   public AbstractContainerScreen(T var1, Inventory var2, Component var3) {
      super(var3);
      this.draggingItem = ItemStack.EMPTY;
      this.quickCraftSlots = Sets.newHashSet();
      this.lastQuickMoved = ItemStack.EMPTY;
      this.menu = var1;
      this.playerInventoryTitle = var2.getDisplayName();
      this.skipNextRelease = true;
      this.titleLabelX = 8;
      this.titleLabelY = 6;
      this.inventoryLabelX = 8;
      this.inventoryLabelY = this.imageHeight - 94;
      this.itemSlotMouseActions = new ArrayList();
   }

   protected void init() {
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
      this.itemSlotMouseActions.clear();
      this.addItemSlotMouseAction(new BundleMouseActions(this.minecraft));
   }

   protected void addItemSlotMouseAction(ItemSlotMouseAction var1) {
      this.itemSlotMouseActions.add(var1);
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderContents(var1, var2, var3, var4);
      this.renderCarriedItem(var1, var2, var3);
      this.renderSnapbackItem(var1);
   }

   public void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = this.leftPos;
      int var6 = this.topPos;
      super.render(var1, var2, var3, var4);
      var1.pose().pushMatrix();
      var1.pose().translate((float)var5, (float)var6);
      this.renderLabels(var1, var2, var3);
      Slot var7 = this.hoveredSlot;
      this.hoveredSlot = this.getHoveredSlot((double)var2, (double)var3);
      this.renderSlotHighlightBack(var1);
      this.renderSlots(var1);
      this.renderSlotHighlightFront(var1);
      if (var7 != null && var7 != this.hoveredSlot) {
         this.onStopHovering(var7);
      }

      var1.pose().popMatrix();
   }

   public void renderCarriedItem(GuiGraphics var1, int var2, int var3) {
      ItemStack var4 = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
      if (!var4.isEmpty()) {
         boolean var5 = true;
         int var6 = this.draggingItem.isEmpty() ? 8 : 16;
         String var7 = null;
         if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
            var4 = var4.copyWithCount(Mth.ceil((float)var4.getCount() / 2.0F));
         } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
            var4 = var4.copyWithCount(this.quickCraftingRemainder);
            if (var4.isEmpty()) {
               var7 = String.valueOf(ChatFormatting.YELLOW) + "0";
            }
         }

         var1.nextStratum();
         this.renderFloatingItem(var1, var4, var2 - 8, var3 - var6, var7);
      }

   }

   public void renderSnapbackItem(GuiGraphics var1) {
      if (this.snapbackData != null) {
         float var2 = Mth.clamp((float)(Util.getMillis() - this.snapbackData.time) / 100.0F, 0.0F, 1.0F);
         int var3 = this.snapbackData.end.x - this.snapbackData.start.x;
         int var4 = this.snapbackData.end.y - this.snapbackData.start.y;
         int var5 = this.snapbackData.start.x + (int)((float)var3 * var2);
         int var6 = this.snapbackData.start.y + (int)((float)var4 * var2);
         var1.nextStratum();
         this.renderFloatingItem(var1, this.snapbackData.item, var5, var6, (String)null);
         if (var2 >= 1.0F) {
            this.snapbackData = null;
         }
      }

   }

   protected void renderSlots(GuiGraphics var1) {
      for(Slot var3 : this.menu.slots) {
         if (var3.isActive()) {
            this.renderSlot(var1, var3);
         }
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderBackground(var1, var2, var3, var4);
      this.renderBg(var1, var4, var2, var3);
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         for(ItemSlotMouseAction var10 : this.itemSlotMouseActions) {
            if (var10.matches(this.hoveredSlot) && var10.onMouseScrolled(var5, var7, this.hoveredSlot.index, this.hoveredSlot.getItem())) {
               return true;
            }
         }
      }

      return false;
   }

   private void renderSlotHighlightBack(GuiGraphics var1) {
      if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SLOT_HIGHLIGHT_BACK_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
      }

   }

   private void renderSlotHighlightFront(GuiGraphics var1) {
      if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SLOT_HIGHLIGHT_FRONT_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
      }

   }

   protected void renderTooltip(GuiGraphics var1, int var2, int var3) {
      if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         ItemStack var4 = this.hoveredSlot.getItem();
         if (this.menu.getCarried().isEmpty() || this.showTooltipWithItemInHand(var4)) {
            var1.setTooltipForNextFrame(this.font, this.getTooltipFromContainerItem(var4), var4.getTooltipImage(), var2, var3, (Identifier)var4.get(DataComponents.TOOLTIP_STYLE));
         }

      }
   }

   private boolean showTooltipWithItemInHand(ItemStack var1) {
      return (Boolean)var1.getTooltipImage().map(ClientTooltipComponent::create).map(ClientTooltipComponent::showTooltipWithItemInHand).orElse(false);
   }

   protected List<Component> getTooltipFromContainerItem(ItemStack var1) {
      return getTooltipFromItem(this.minecraft, var1);
   }

   private void renderFloatingItem(GuiGraphics var1, ItemStack var2, int var3, int var4, @Nullable String var5) {
      var1.renderItem(var2, var3, var4);
      var1.renderItemDecorations(this.font, var2, var3, var4 - (this.draggingItem.isEmpty() ? 0 : 8), var5);
   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      var1.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
      var1.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
   }

   protected abstract void renderBg(GuiGraphics var1, float var2, int var3, int var4);

   protected void renderSlot(GuiGraphics var1, Slot var2) {
      int var3 = var2.x;
      int var4 = var2.y;
      ItemStack var5 = var2.getItem();
      boolean var6 = false;
      boolean var7 = var2 == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
      ItemStack var8 = this.menu.getCarried();
      String var9 = null;
      if (var2 == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !var5.isEmpty()) {
         var5 = var5.copyWithCount(var5.getCount() / 2);
      } else if (this.isQuickCrafting && this.quickCraftSlots.contains(var2) && !var8.isEmpty()) {
         if (this.quickCraftSlots.size() == 1) {
            return;
         }

         if (AbstractContainerMenu.canItemQuickReplace(var2, var8, true) && this.menu.canDragTo(var2)) {
            var6 = true;
            int var10 = Math.min(var8.getMaxStackSize(), var2.getMaxStackSize(var8));
            int var11 = var2.getItem().isEmpty() ? 0 : var2.getItem().getCount();
            int var12 = AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, var8) + var11;
            if (var12 > var10) {
               var12 = var10;
               String var10000 = ChatFormatting.YELLOW.toString();
               var9 = var10000 + var10;
            }

            var5 = var8.copyWithCount(var12);
         } else {
            this.quickCraftSlots.remove(var2);
            this.recalculateQuickCraftRemaining();
         }
      }

      if (var5.isEmpty() && var2.isActive()) {
         Identifier var13 = var2.getNoItemIcon();
         if (var13 != null) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)var13, var3, var4, 16, 16);
            var7 = true;
         }
      }

      if (!var7) {
         if (var6) {
            var1.fill(var3, var4, var3 + 16, var4 + 16, -2130706433);
         }

         int var14 = var2.x + var2.y * this.imageWidth;
         if (var2.isFake()) {
            var1.renderFakeItem(var5, var3, var4, var14);
         } else {
            var1.renderItem(var5, var3, var4, var14);
         }

         var1.renderItemDecorations(this.font, var5, var3, var4, var9);
      }

   }

   private void recalculateQuickCraftRemaining() {
      ItemStack var1 = this.menu.getCarried();
      if (!var1.isEmpty() && this.isQuickCrafting) {
         if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = var1.getMaxStackSize();
         } else {
            this.quickCraftingRemainder = var1.getCount();

            for(Slot var3 : this.quickCraftSlots) {
               ItemStack var4 = var3.getItem();
               int var5 = var4.isEmpty() ? 0 : var4.getCount();
               int var6 = Math.min(var1.getMaxStackSize(), var3.getMaxStackSize(var1));
               int var7 = Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, var1) + var5, var6);
               this.quickCraftingRemainder -= var7 - var5;
            }

         }
      }
   }

   private @Nullable Slot getHoveredSlot(double var1, double var3) {
      for(Slot var6 : this.menu.slots) {
         if (var6.isActive() && this.isHovering(var6, var1, var3)) {
            return var6;
         }
      }

      return null;
   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      if (super.mouseClicked(var1, var2)) {
         return true;
      } else {
         boolean var3 = this.minecraft.options.keyPickItem.matchesMouse(var1) && this.minecraft.player.hasInfiniteMaterials();
         Slot var4 = this.getHoveredSlot(var1.x(), var1.y());
         this.doubleclick = this.lastClickSlot == var4 && var2;
         this.skipNextRelease = false;
         if (var1.button() != 0 && var1.button() != 1 && !var3) {
            this.checkHotbarMouseClicked(var1);
         } else {
            int var5 = this.leftPos;
            int var6 = this.topPos;
            boolean var7 = this.hasClickedOutside(var1.x(), var1.y(), var5, var6);
            int var8 = -1;
            if (var4 != null) {
               var8 = var4.index;
            }

            if (var7) {
               var8 = -999;
            }

            if ((Boolean)this.minecraft.options.touchscreen().get() && var7 && this.menu.getCarried().isEmpty()) {
               this.onClose();
               return true;
            }

            if (var8 != -1) {
               if ((Boolean)this.minecraft.options.touchscreen().get()) {
                  if (var4 != null && var4.hasItem()) {
                     this.clickedSlot = var4;
                     this.draggingItem = ItemStack.EMPTY;
                     this.isSplittingStack = var1.button() == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.isQuickCrafting) {
                  if (this.menu.getCarried().isEmpty()) {
                     if (var3) {
                        this.slotClicked(var4, var8, var1.button(), ClickType.CLONE);
                     } else {
                        boolean var9 = var8 != -999 && var1.hasShiftDown();
                        ClickType var10 = ClickType.PICKUP;
                        if (var9) {
                           this.lastQuickMoved = var4 != null && var4.hasItem() ? var4.getItem().copy() : ItemStack.EMPTY;
                           var10 = ClickType.QUICK_MOVE;
                        } else if (var8 == -999) {
                           var10 = ClickType.THROW;
                        }

                        this.slotClicked(var4, var8, var1.button(), var10);
                     }

                     this.skipNextRelease = true;
                  } else {
                     this.isQuickCrafting = true;
                     this.quickCraftingButton = var1.button();
                     this.quickCraftSlots.clear();
                     if (var1.button() == 0) {
                        this.quickCraftingType = 0;
                     } else if (var1.button() == 1) {
                        this.quickCraftingType = 1;
                     } else if (var3) {
                        this.quickCraftingType = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = var4;
         return true;
      }
   }

   private void checkHotbarMouseClicked(MouseButtonEvent var1) {
      if (this.hoveredSlot != null && this.menu.getCarried().isEmpty()) {
         if (this.minecraft.options.keySwapOffhand.matchesMouse(var1)) {
            this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
            return;
         }

         for(int var2 = 0; var2 < 9; ++var2) {
            if (this.minecraft.options.keyHotbarSlots[var2].matchesMouse(var1)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, var2, ClickType.SWAP);
            }
         }
      }

   }

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   public boolean mouseDragged(MouseButtonEvent var1, double var2, double var4) {
      Slot var6 = this.getHoveredSlot(var1.x(), var1.y());
      ItemStack var7 = this.menu.getCarried();
      if (this.clickedSlot != null && (Boolean)this.minecraft.options.touchscreen().get()) {
         if (var1.button() == 0 || var1.button() == 1) {
            if (this.draggingItem.isEmpty()) {
               if (var6 != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                  this.draggingItem = this.clickedSlot.getItem().copy();
               }
            } else if (this.draggingItem.getCount() > 1 && var6 != null && AbstractContainerMenu.canItemQuickReplace(var6, this.draggingItem, false)) {
               long var8 = Util.getMillis();
               if (this.quickdropSlot == var6) {
                  if (var8 - this.quickdropTime > 500L) {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.slotClicked(var6, var6.index, 1, ClickType.PICKUP);
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.quickdropTime = var8 + 750L;
                     this.draggingItem.shrink(1);
                  }
               } else {
                  this.quickdropSlot = var6;
                  this.quickdropTime = var8;
               }
            }
         }

         return true;
      } else if (this.isQuickCrafting && var6 != null && !var7.isEmpty() && (var7.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(var6, var7, true) && var6.mayPlace(var7) && this.menu.canDragTo(var6)) {
         this.quickCraftSlots.add(var6);
         this.recalculateQuickCraftRemaining();
         return true;
      } else {
         return var6 == null && this.menu.getCarried().isEmpty() ? super.mouseDragged(var1, var2, var4) : true;
      }
   }

   public boolean mouseReleased(MouseButtonEvent var1) {
      Slot var2 = this.getHoveredSlot(var1.x(), var1.y());
      int var3 = this.leftPos;
      int var4 = this.topPos;
      boolean var5 = this.hasClickedOutside(var1.x(), var1.y(), var3, var4);
      int var6 = -1;
      if (var2 != null) {
         var6 = var2.index;
      }

      if (var5) {
         var6 = -999;
      }

      if (this.doubleclick && var2 != null && var1.button() == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, var2)) {
         if (var1.hasShiftDown()) {
            if (!this.lastQuickMoved.isEmpty()) {
               for(Slot var12 : this.menu.slots) {
                  if (var12 != null && var12.mayPickup(this.minecraft.player) && var12.hasItem() && var12.container == var2.container && AbstractContainerMenu.canItemQuickReplace(var12, this.lastQuickMoved, true)) {
                     this.slotClicked(var12, var12.index, var1.button(), ClickType.QUICK_MOVE);
                  }
               }
            }
         } else {
            this.slotClicked(var2, var6, var1.button(), ClickType.PICKUP_ALL);
         }

         this.doubleclick = false;
      } else {
         if (this.isQuickCrafting && this.quickCraftingButton != var1.button()) {
            this.isQuickCrafting = false;
            this.quickCraftSlots.clear();
            this.skipNextRelease = true;
            return true;
         }

         if (this.skipNextRelease) {
            this.skipNextRelease = false;
            return true;
         }

         if (this.clickedSlot != null && (Boolean)this.minecraft.options.touchscreen().get()) {
            if (var1.button() == 0 || var1.button() == 1) {
               if (this.draggingItem.isEmpty() && var2 != this.clickedSlot) {
                  this.draggingItem = this.clickedSlot.getItem();
               }

               boolean var10 = AbstractContainerMenu.canItemQuickReplace(var2, this.draggingItem, false);
               if (var6 != -1 && !this.draggingItem.isEmpty() && var10) {
                  this.slotClicked(this.clickedSlot, this.clickedSlot.index, var1.button(), ClickType.PICKUP);
                  this.slotClicked(var2, var6, 0, ClickType.PICKUP);
                  if (this.menu.getCarried().isEmpty()) {
                     this.snapbackData = null;
                  } else {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, var1.button(), ClickType.PICKUP);
                     this.snapbackData = new SnapbackData(this.draggingItem, new Vector2i((int)var1.x(), (int)var1.y()), new Vector2i(this.clickedSlot.x + var3, this.clickedSlot.y + var4), Util.getMillis());
                  }
               } else if (!this.draggingItem.isEmpty()) {
                  this.snapbackData = new SnapbackData(this.draggingItem, new Vector2i((int)var1.x(), (int)var1.y()), new Vector2i(this.clickedSlot.x + var3, this.clickedSlot.y + var4), Util.getMillis());
               }

               this.clearDraggingState();
            }
         } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);

            for(Slot var8 : this.quickCraftSlots) {
               this.slotClicked(var8, var8.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
            }

            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
         } else if (!this.menu.getCarried().isEmpty()) {
            if (this.minecraft.options.keyPickItem.matchesMouse(var1)) {
               this.slotClicked(var2, var6, var1.button(), ClickType.CLONE);
            } else {
               boolean var7 = var6 != -999 && var1.hasShiftDown();
               if (var7) {
                  this.lastQuickMoved = var2 != null && var2.hasItem() ? var2.getItem().copy() : ItemStack.EMPTY;
               }

               this.slotClicked(var2, var6, var1.button(), var7 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
            }
         }
      }

      this.isQuickCrafting = false;
      return true;
   }

   public void clearDraggingState() {
      this.draggingItem = ItemStack.EMPTY;
      this.clickedSlot = null;
   }

   private boolean isHovering(Slot var1, double var2, double var4) {
      return this.isHovering(var1.x, var1.y, 16, 16, var2, var4);
   }

   protected boolean isHovering(int var1, int var2, int var3, int var4, double var5, double var7) {
      int var9 = this.leftPos;
      int var10 = this.topPos;
      var5 -= (double)var9;
      var7 -= (double)var10;
      return var5 >= (double)(var1 - 1) && var5 < (double)(var1 + var3 + 1) && var7 >= (double)(var2 - 1) && var7 < (double)(var2 + var4 + 1);
   }

   private void onStopHovering(Slot var1) {
      if (var1.hasItem()) {
         for(ItemSlotMouseAction var3 : this.itemSlotMouseActions) {
            if (var3.matches(var1)) {
               var3.onStopHovering(var1);
            }
         }
      }

   }

   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      if (var1 != null) {
         var2 = var1.index;
      }

      this.onMouseClickAction(var1, var4);
      this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, var2, var3, var4, this.minecraft.player);
   }

   void onMouseClickAction(@Nullable Slot var1, ClickType var2) {
      if (var1 != null && var1.hasItem()) {
         for(ItemSlotMouseAction var4 : this.itemSlotMouseActions) {
            if (var4.matches(var1)) {
               var4.onSlotClicked(var1, var2);
            }
         }
      }

   }

   protected void handleSlotStateChanged(int var1, int var2, boolean var3) {
      this.minecraft.gameMode.handleSlotStateChanged(var1, var2, var3);
   }

   public boolean keyPressed(KeyEvent var1) {
      if (super.keyPressed(var1)) {
         return true;
      } else if (this.minecraft.options.keyInventory.matches(var1)) {
         this.onClose();
         return true;
      } else {
         this.checkHotbarKeyPressed(var1);
         if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.matches(var1)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
            } else if (this.minecraft.options.keyDrop.matches(var1)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, var1.hasControlDown() ? 1 : 0, ClickType.THROW);
            }
         }

         return false;
      }
   }

   protected boolean checkHotbarKeyPressed(KeyEvent var1) {
      if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
         if (this.minecraft.options.keySwapOffhand.matches(var1)) {
            this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
            return true;
         }

         for(int var2 = 0; var2 < 9; ++var2) {
            if (this.minecraft.options.keyHotbarSlots[var2].matches(var1)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, var2, ClickType.SWAP);
               return true;
            }
         }
      }

      return false;
   }

   public void removed() {
      if (this.minecraft.player != null) {
         this.menu.removed(this.minecraft.player);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean isInGameUi() {
      return true;
   }

   public final void tick() {
      super.tick();
      if (this.minecraft.player.isAlive() && !this.minecraft.player.isRemoved()) {
         this.containerTick();
      } else {
         this.minecraft.player.closeContainer();
      }

   }

   protected void containerTick() {
   }

   public T getMenu() {
      return this.menu;
   }

   public void onClose() {
      this.minecraft.player.closeContainer();
      if (this.hoveredSlot != null) {
         this.onStopHovering(this.hoveredSlot);
      }

      super.onClose();
   }

   static record SnapbackData(ItemStack item, Vector2i start, Vector2i end, long time) {
      final ItemStack item;
      final Vector2i start;
      final Vector2i end;
      final long time;

      SnapbackData(ItemStack var1, Vector2i var2, Vector2i var3, long var4) {
         super();
         this.item = var1;
         this.start = var2;
         this.end = var3;
         this.time = var4;
      }
   }
}
