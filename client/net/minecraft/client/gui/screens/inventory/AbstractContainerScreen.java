package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.BundleMouseActions;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.ItemSlotMouseAction;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
   public static final ResourceLocation INVENTORY_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/inventory.png");
   private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_back");
   private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.withDefaultNamespace("container/slot_highlight_front");
   protected static final int BACKGROUND_TEXTURE_WIDTH = 256;
   protected static final int BACKGROUND_TEXTURE_HEIGHT = 256;
   private static final float SNAPBACK_SPEED = 100.0F;
   private static final int QUICKDROP_DELAY = 500;
   public static final int SLOT_ITEM_BLIT_OFFSET = 100;
   private static final int HOVER_ITEM_BLIT_OFFSET = 200;
   protected int imageWidth = 176;
   protected int imageHeight = 166;
   protected int titleLabelX;
   protected int titleLabelY;
   protected int inventoryLabelX;
   protected int inventoryLabelY;
   private final List<ItemSlotMouseAction> itemSlotMouseActions;
   protected final T menu;
   protected final Component playerInventoryTitle;
   @Nullable
   protected Slot hoveredSlot;
   @Nullable
   private Slot clickedSlot;
   @Nullable
   private Slot snapbackEnd;
   @Nullable
   private Slot quickdropSlot;
   @Nullable
   private Slot lastClickSlot;
   protected int leftPos;
   protected int topPos;
   private boolean isSplittingStack;
   private ItemStack draggingItem;
   private int snapbackStartX;
   private int snapbackStartY;
   private long snapbackTime;
   private ItemStack snapbackItem;
   private long quickdropTime;
   protected final Set<Slot> quickCraftSlots;
   protected boolean isQuickCrafting;
   private int quickCraftingType;
   private int quickCraftingButton;
   private boolean skipNextRelease;
   private int quickCraftingRemainder;
   private long lastClickTime;
   private int lastClickButton;
   private boolean doubleclick;
   private ItemStack lastQuickMoved;

   public AbstractContainerScreen(T var1, Inventory var2, Component var3) {
      super(var3);
      this.draggingItem = ItemStack.EMPTY;
      this.snapbackItem = ItemStack.EMPTY;
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
      int var5 = this.leftPos;
      int var6 = this.topPos;
      super.render(var1, var2, var3, var4);
      var1.pose().pushPose();
      var1.pose().translate((float)var5, (float)var6, 0.0F);
      Slot var7 = this.hoveredSlot;
      this.hoveredSlot = this.getHoveredSlot((double)var2, (double)var3);
      this.renderSlotHighlightBack(var1);
      this.renderSlots(var1);
      this.renderSlotHighlightFront(var1);
      if (var7 != null && var7 != this.hoveredSlot) {
         this.onStopHovering(var7);
      }

      this.renderLabels(var1, var2, var3);
      ItemStack var8 = this.draggingItem.isEmpty() ? this.menu.getCarried() : this.draggingItem;
      int var10;
      if (!var8.isEmpty()) {
         boolean var9 = true;
         var10 = this.draggingItem.isEmpty() ? 8 : 16;
         String var11 = null;
         if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
            var8 = var8.copyWithCount(Mth.ceil((float)var8.getCount() / 2.0F));
         } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
            var8 = var8.copyWithCount(this.quickCraftingRemainder);
            if (var8.isEmpty()) {
               var11 = String.valueOf(ChatFormatting.YELLOW) + "0";
            }
         }

         this.renderFloatingItem(var1, var8, var2 - var5 - 8, var3 - var6 - var10, var11);
      }

      if (!this.snapbackItem.isEmpty()) {
         float var14 = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
         if (var14 >= 1.0F) {
            var14 = 1.0F;
            this.snapbackItem = ItemStack.EMPTY;
         }

         var10 = this.snapbackEnd.x - this.snapbackStartX;
         int var15 = this.snapbackEnd.y - this.snapbackStartY;
         int var12 = this.snapbackStartX + (int)((float)var10 * var14);
         int var13 = this.snapbackStartY + (int)((float)var15 * var14);
         this.renderFloatingItem(var1, this.snapbackItem, var12, var13, (String)null);
      }

      var1.pose().popPose();
   }

   protected void renderSlots(GuiGraphics var1) {
      Iterator var2 = this.menu.slots.iterator();

      while(var2.hasNext()) {
         Slot var3 = (Slot)var2.next();
         if (var3.isActive()) {
            this.renderSlot(var1, var3);
         }
      }

   }

   public void renderBackground(GuiGraphics var1, int var2, int var3, float var4) {
      this.renderTransparentBackground(var1);
      this.renderBg(var1, var4, var2, var3);
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         Iterator var9 = this.itemSlotMouseActions.iterator();

         while(var9.hasNext()) {
            ItemSlotMouseAction var10 = (ItemSlotMouseAction)var9.next();
            if (var10.matches(this.hoveredSlot) && var10.onMouseScrolled(var5, var7, this.hoveredSlot.index, this.hoveredSlot.getItem())) {
               return true;
            }
         }
      }

      return false;
   }

   private void renderSlotHighlightBack(GuiGraphics var1) {
      if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
         var1.blitSprite(RenderType::guiTextured, (ResourceLocation)SLOT_HIGHLIGHT_BACK_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
      }

   }

   private void renderSlotHighlightFront(GuiGraphics var1) {
      if (this.hoveredSlot != null && this.hoveredSlot.isHighlightable()) {
         var1.blitSprite(RenderType::guiTexturedOverlay, (ResourceLocation)SLOT_HIGHLIGHT_FRONT_SPRITE, this.hoveredSlot.x - 4, this.hoveredSlot.y - 4, 24, 24);
      }

   }

   protected void renderTooltip(GuiGraphics var1, int var2, int var3) {
      if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         ItemStack var4 = this.hoveredSlot.getItem();
         if (this.menu.getCarried().isEmpty() || this.showTooltipWithItemInHand(var4)) {
            var1.renderTooltip(this.font, this.getTooltipFromContainerItem(var4), var4.getTooltipImage(), var2, var3, (ResourceLocation)var4.get(DataComponents.TOOLTIP_STYLE));
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
      var1.pose().pushPose();
      var1.pose().translate(0.0F, 0.0F, 232.0F);
      var1.renderItem(var2, var3, var4);
      var1.renderItemDecorations(this.font, var2, var3, var4 - (this.draggingItem.isEmpty() ? 0 : 8), var5);
      var1.pose().popPose();
   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      var1.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
      var1.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
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
      int var10;
      if (var2 == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !var5.isEmpty()) {
         var5 = var5.copyWithCount(var5.getCount() / 2);
      } else if (this.isQuickCrafting && this.quickCraftSlots.contains(var2) && !var8.isEmpty()) {
         if (this.quickCraftSlots.size() == 1) {
            return;
         }

         if (AbstractContainerMenu.canItemQuickReplace(var2, var8, true) && this.menu.canDragTo(var2)) {
            var6 = true;
            var10 = Math.min(var8.getMaxStackSize(), var2.getMaxStackSize(var8));
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

      var1.pose().pushPose();
      var1.pose().translate(0.0F, 0.0F, 100.0F);
      if (var5.isEmpty() && var2.isActive()) {
         Pair var13 = var2.getNoItemIcon();
         if (var13 != null) {
            TextureAtlasSprite var14 = (TextureAtlasSprite)this.minecraft.getTextureAtlas((ResourceLocation)var13.getFirst()).apply((ResourceLocation)var13.getSecond());
            var1.blitSprite(RenderType::guiTextured, (TextureAtlasSprite)var14, var3, var4, 16, 16);
            var7 = true;
         }
      }

      if (!var7) {
         if (var6) {
            var1.fill(var3, var4, var3 + 16, var4 + 16, -2130706433);
         }

         var10 = var2.x + var2.y * this.imageWidth;
         if (var2.isFake()) {
            var1.renderFakeItem(var5, var3, var4, var10);
         } else {
            var1.renderItem(var5, var3, var4, var10);
         }

         var1.renderItemDecorations(this.font, var5, var3, var4, var9);
      }

      var1.pose().popPose();
   }

   private void recalculateQuickCraftRemaining() {
      ItemStack var1 = this.menu.getCarried();
      if (!var1.isEmpty() && this.isQuickCrafting) {
         if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = var1.getMaxStackSize();
         } else {
            this.quickCraftingRemainder = var1.getCount();

            int var5;
            int var7;
            for(Iterator var2 = this.quickCraftSlots.iterator(); var2.hasNext(); this.quickCraftingRemainder -= var7 - var5) {
               Slot var3 = (Slot)var2.next();
               ItemStack var4 = var3.getItem();
               var5 = var4.isEmpty() ? 0 : var4.getCount();
               int var6 = Math.min(var1.getMaxStackSize(), var3.getMaxStackSize(var1));
               var7 = Math.min(AbstractContainerMenu.getQuickCraftPlaceCount(this.quickCraftSlots, this.quickCraftingType, var1) + var5, var6);
            }

         }
      }
   }

   @Nullable
   private Slot getHoveredSlot(double var1, double var3) {
      Iterator var5 = this.menu.slots.iterator();

      Slot var6;
      do {
         if (!var5.hasNext()) {
            return null;
         }

         var6 = (Slot)var5.next();
      } while(!var6.isActive() || !this.isHovering(var6, var1, var3));

      return var6;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         boolean var6 = this.minecraft.options.keyPickItem.matchesMouse(var5) && this.minecraft.gameMode.hasInfiniteItems();
         Slot var7 = this.getHoveredSlot(var1, var3);
         long var8 = Util.getMillis();
         this.doubleclick = this.lastClickSlot == var7 && var8 - this.lastClickTime < 250L && this.lastClickButton == var5;
         this.skipNextRelease = false;
         if (var5 != 0 && var5 != 1 && !var6) {
            this.checkHotbarMouseClicked(var5);
         } else {
            int var10 = this.leftPos;
            int var11 = this.topPos;
            boolean var12 = this.hasClickedOutside(var1, var3, var10, var11, var5);
            int var13 = -1;
            if (var7 != null) {
               var13 = var7.index;
            }

            if (var12) {
               var13 = -999;
            }

            if ((Boolean)this.minecraft.options.touchscreen().get() && var12 && this.menu.getCarried().isEmpty()) {
               this.onClose();
               return true;
            }

            if (var13 != -1) {
               if ((Boolean)this.minecraft.options.touchscreen().get()) {
                  if (var7 != null && var7.hasItem()) {
                     this.clickedSlot = var7;
                     this.draggingItem = ItemStack.EMPTY;
                     this.isSplittingStack = var5 == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.isQuickCrafting) {
                  if (this.menu.getCarried().isEmpty()) {
                     if (var6) {
                        this.slotClicked(var7, var13, var5, ClickType.CLONE);
                     } else {
                        boolean var14 = var13 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                        ClickType var15 = ClickType.PICKUP;
                        if (var14) {
                           this.lastQuickMoved = var7 != null && var7.hasItem() ? var7.getItem().copy() : ItemStack.EMPTY;
                           var15 = ClickType.QUICK_MOVE;
                        } else if (var13 == -999) {
                           var15 = ClickType.THROW;
                        }

                        this.slotClicked(var7, var13, var5, var15);
                     }

                     this.skipNextRelease = true;
                  } else {
                     this.isQuickCrafting = true;
                     this.quickCraftingButton = var5;
                     this.quickCraftSlots.clear();
                     if (var5 == 0) {
                        this.quickCraftingType = 0;
                     } else if (var5 == 1) {
                        this.quickCraftingType = 1;
                     } else if (var6) {
                        this.quickCraftingType = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = var7;
         this.lastClickTime = var8;
         this.lastClickButton = var5;
         return true;
      }
   }

   private void checkHotbarMouseClicked(int var1) {
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

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      Slot var10 = this.getHoveredSlot(var1, var3);
      ItemStack var11 = this.menu.getCarried();
      if (this.clickedSlot != null && (Boolean)this.minecraft.options.touchscreen().get()) {
         if (var5 == 0 || var5 == 1) {
            if (this.draggingItem.isEmpty()) {
               if (var10 != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                  this.draggingItem = this.clickedSlot.getItem().copy();
               }
            } else if (this.draggingItem.getCount() > 1 && var10 != null && AbstractContainerMenu.canItemQuickReplace(var10, this.draggingItem, false)) {
               long var12 = Util.getMillis();
               if (this.quickdropSlot == var10) {
                  if (var12 - this.quickdropTime > 500L) {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.slotClicked(var10, var10.index, 1, ClickType.PICKUP);
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.quickdropTime = var12 + 750L;
                     this.draggingItem.shrink(1);
                  }
               } else {
                  this.quickdropSlot = var10;
                  this.quickdropTime = var12;
               }
            }
         }
      } else if (this.isQuickCrafting && var10 != null && !var11.isEmpty() && (var11.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && AbstractContainerMenu.canItemQuickReplace(var10, var11, true) && var10.mayPlace(var11) && this.menu.canDragTo(var10)) {
         this.quickCraftSlots.add(var10);
         this.recalculateQuickCraftRemaining();
      }

      return true;
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      Slot var6 = this.getHoveredSlot(var1, var3);
      int var7 = this.leftPos;
      int var8 = this.topPos;
      boolean var9 = this.hasClickedOutside(var1, var3, var7, var8, var5);
      int var10 = -1;
      if (var6 != null) {
         var10 = var6.index;
      }

      if (var9) {
         var10 = -999;
      }

      Slot var12;
      Iterator var13;
      if (this.doubleclick && var6 != null && var5 == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, var6)) {
         if (hasShiftDown()) {
            if (!this.lastQuickMoved.isEmpty()) {
               var13 = this.menu.slots.iterator();

               while(var13.hasNext()) {
                  var12 = (Slot)var13.next();
                  if (var12 != null && var12.mayPickup(this.minecraft.player) && var12.hasItem() && var12.container == var6.container && AbstractContainerMenu.canItemQuickReplace(var12, this.lastQuickMoved, true)) {
                     this.slotClicked(var12, var12.index, var5, ClickType.QUICK_MOVE);
                  }
               }
            }
         } else {
            this.slotClicked(var6, var10, var5, ClickType.PICKUP_ALL);
         }

         this.doubleclick = false;
         this.lastClickTime = 0L;
      } else {
         if (this.isQuickCrafting && this.quickCraftingButton != var5) {
            this.isQuickCrafting = false;
            this.quickCraftSlots.clear();
            this.skipNextRelease = true;
            return true;
         }

         if (this.skipNextRelease) {
            this.skipNextRelease = false;
            return true;
         }

         boolean var11;
         if (this.clickedSlot != null && (Boolean)this.minecraft.options.touchscreen().get()) {
            if (var5 == 0 || var5 == 1) {
               if (this.draggingItem.isEmpty() && var6 != this.clickedSlot) {
                  this.draggingItem = this.clickedSlot.getItem();
               }

               var11 = AbstractContainerMenu.canItemQuickReplace(var6, this.draggingItem, false);
               if (var10 != -1 && !this.draggingItem.isEmpty() && var11) {
                  this.slotClicked(this.clickedSlot, this.clickedSlot.index, var5, ClickType.PICKUP);
                  this.slotClicked(var6, var10, 0, ClickType.PICKUP);
                  if (this.menu.getCarried().isEmpty()) {
                     this.snapbackItem = ItemStack.EMPTY;
                  } else {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, var5, ClickType.PICKUP);
                     this.snapbackStartX = Mth.floor(var1 - (double)var7);
                     this.snapbackStartY = Mth.floor(var3 - (double)var8);
                     this.snapbackEnd = this.clickedSlot;
                     this.snapbackItem = this.draggingItem;
                     this.snapbackTime = Util.getMillis();
                  }
               } else if (!this.draggingItem.isEmpty()) {
                  this.snapbackStartX = Mth.floor(var1 - (double)var7);
                  this.snapbackStartY = Mth.floor(var3 - (double)var8);
                  this.snapbackEnd = this.clickedSlot;
                  this.snapbackItem = this.draggingItem;
                  this.snapbackTime = Util.getMillis();
               }

               this.clearDraggingState();
            }
         } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);
            var13 = this.quickCraftSlots.iterator();

            while(var13.hasNext()) {
               var12 = (Slot)var13.next();
               this.slotClicked(var12, var12.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
            }

            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
         } else if (!this.menu.getCarried().isEmpty()) {
            if (this.minecraft.options.keyPickItem.matchesMouse(var5)) {
               this.slotClicked(var6, var10, var5, ClickType.CLONE);
            } else {
               var11 = var10 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
               if (var11) {
                  this.lastQuickMoved = var6 != null && var6.hasItem() ? var6.getItem().copy() : ItemStack.EMPTY;
               }

               this.slotClicked(var6, var10, var5, var11 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
            }
         }
      }

      if (this.menu.getCarried().isEmpty()) {
         this.lastClickTime = 0L;
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
         Iterator var2 = this.itemSlotMouseActions.iterator();

         while(var2.hasNext()) {
            ItemSlotMouseAction var3 = (ItemSlotMouseAction)var2.next();
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
         Iterator var3 = this.itemSlotMouseActions.iterator();

         while(var3.hasNext()) {
            ItemSlotMouseAction var4 = (ItemSlotMouseAction)var3.next();
            if (var4.matches(var1)) {
               var4.onSlotClicked(var1, var2);
            }
         }
      }

   }

   protected void handleSlotStateChanged(int var1, int var2, boolean var3) {
      this.minecraft.gameMode.handleSlotStateChanged(var1, var2, var3);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else if (this.minecraft.options.keyInventory.matches(var1, var2)) {
         this.onClose();
         return true;
      } else {
         this.checkHotbarKeyPressed(var1, var2);
         if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.matches(var1, var2)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
            } else if (this.minecraft.options.keyDrop.matches(var1, var2)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, hasControlDown() ? 1 : 0, ClickType.THROW);
            }
         }

         return true;
      }
   }

   protected boolean checkHotbarKeyPressed(int var1, int var2) {
      if (this.menu.getCarried().isEmpty() && this.hoveredSlot != null) {
         if (this.minecraft.options.keySwapOffhand.matches(var1, var2)) {
            this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 40, ClickType.SWAP);
            return true;
         }

         for(int var3 = 0; var3 < 9; ++var3) {
            if (this.minecraft.options.keyHotbarSlots[var3].matches(var1, var2)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, var3, ClickType.SWAP);
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
}
