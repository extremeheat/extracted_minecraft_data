package net.minecraft.client.gui.screens.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T> {
   public static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");
   protected int imageWidth = 176;
   protected int imageHeight = 166;
   protected final T menu;
   protected final Inventory inventory;
   protected int leftPos;
   protected int topPos;
   protected Slot hoveredSlot;
   private Slot clickedSlot;
   private boolean isSplittingStack;
   private ItemStack draggingItem;
   private int snapbackStartX;
   private int snapbackStartY;
   private Slot snapbackEnd;
   private long snapbackTime;
   private ItemStack snapbackItem;
   private Slot quickdropSlot;
   private long quickdropTime;
   protected final Set<Slot> quickCraftSlots;
   protected boolean isQuickCrafting;
   private int quickCraftingType;
   private int quickCraftingButton;
   private boolean skipNextRelease;
   private int quickCraftingRemainder;
   private long lastClickTime;
   private Slot lastClickSlot;
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
      this.inventory = var2;
      this.skipNextRelease = true;
   }

   protected void init() {
      super.init();
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
   }

   public void render(int var1, int var2, float var3) {
      int var4 = this.leftPos;
      int var5 = this.topPos;
      this.renderBg(var3, var1, var2);
      GlStateManager.disableRescaleNormal();
      Lighting.turnOff();
      GlStateManager.disableLighting();
      GlStateManager.disableDepthTest();
      super.render(var1, var2, var3);
      Lighting.turnOnGui();
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var4, (float)var5, 0.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableRescaleNormal();
      this.hoveredSlot = null;
      boolean var6 = true;
      boolean var7 = true;
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

      int var11;
      for(int var8 = 0; var8 < this.menu.slots.size(); ++var8) {
         Slot var9 = (Slot)this.menu.slots.get(var8);
         if (var9.isActive()) {
            this.renderSlot(var9);
         }

         if (this.isHovering(var9, (double)var1, (double)var2) && var9.isActive()) {
            this.hoveredSlot = var9;
            GlStateManager.disableLighting();
            GlStateManager.disableDepthTest();
            int var10 = var9.x;
            var11 = var9.y;
            GlStateManager.colorMask(true, true, true, false);
            this.fillGradient(var10, var11, var10 + 16, var11 + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepthTest();
         }
      }

      Lighting.turnOff();
      this.renderLabels(var1, var2);
      Lighting.turnOnGui();
      Inventory var15 = this.minecraft.player.inventory;
      ItemStack var16 = this.draggingItem.isEmpty() ? var15.getCarried() : this.draggingItem;
      if (!var16.isEmpty()) {
         boolean var17 = true;
         var11 = this.draggingItem.isEmpty() ? 8 : 16;
         String var12 = null;
         if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
            var16 = var16.copy();
            var16.setCount(Mth.ceil((float)var16.getCount() / 2.0F));
         } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
            var16 = var16.copy();
            var16.setCount(this.quickCraftingRemainder);
            if (var16.isEmpty()) {
               var12 = "" + ChatFormatting.YELLOW + "0";
            }
         }

         this.renderFloatingItem(var16, var1 - var4 - 8, var2 - var5 - var11, var12);
      }

      if (!this.snapbackItem.isEmpty()) {
         float var18 = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
         if (var18 >= 1.0F) {
            var18 = 1.0F;
            this.snapbackItem = ItemStack.EMPTY;
         }

         var11 = this.snapbackEnd.x - this.snapbackStartX;
         int var19 = this.snapbackEnd.y - this.snapbackStartY;
         int var13 = this.snapbackStartX + (int)((float)var11 * var18);
         int var14 = this.snapbackStartY + (int)((float)var19 * var18);
         this.renderFloatingItem(this.snapbackItem, var13, var14, (String)null);
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
      GlStateManager.enableDepthTest();
      Lighting.turnOn();
   }

   protected void renderTooltip(int var1, int var2) {
      if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         this.renderTooltip(this.hoveredSlot.getItem(), var1, var2);
      }

   }

   private void renderFloatingItem(ItemStack var1, int var2, int var3, String var4) {
      GlStateManager.translatef(0.0F, 0.0F, 32.0F);
      this.blitOffset = 200;
      this.itemRenderer.blitOffset = 200.0F;
      this.itemRenderer.renderAndDecorateItem(var1, var2, var3);
      this.itemRenderer.renderGuiItemDecorations(this.font, var1, var2, var3 - (this.draggingItem.isEmpty() ? 0 : 8), var4);
      this.blitOffset = 0;
      this.itemRenderer.blitOffset = 0.0F;
   }

   protected void renderLabels(int var1, int var2) {
   }

   protected abstract void renderBg(float var1, int var2, int var3);

   private void renderSlot(Slot var1) {
      int var2 = var1.x;
      int var3 = var1.y;
      ItemStack var4 = var1.getItem();
      boolean var5 = false;
      boolean var6 = var1 == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
      ItemStack var7 = this.minecraft.player.inventory.getCarried();
      String var8 = null;
      if (var1 == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !var4.isEmpty()) {
         var4 = var4.copy();
         var4.setCount(var4.getCount() / 2);
      } else if (this.isQuickCrafting && this.quickCraftSlots.contains(var1) && !var7.isEmpty()) {
         if (this.quickCraftSlots.size() == 1) {
            return;
         }

         if (AbstractContainerMenu.canItemQuickReplace(var1, var7, true) && this.menu.canDragTo(var1)) {
            var4 = var7.copy();
            var5 = true;
            AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, var4, var1.getItem().isEmpty() ? 0 : var1.getItem().getCount());
            int var9 = Math.min(var4.getMaxStackSize(), var1.getMaxStackSize(var4));
            if (var4.getCount() > var9) {
               var8 = ChatFormatting.YELLOW.toString() + var9;
               var4.setCount(var9);
            }
         } else {
            this.quickCraftSlots.remove(var1);
            this.recalculateQuickCraftRemaining();
         }
      }

      this.blitOffset = 100;
      this.itemRenderer.blitOffset = 100.0F;
      if (var4.isEmpty() && var1.isActive()) {
         String var11 = var1.getNoItemIcon();
         if (var11 != null) {
            TextureAtlasSprite var10 = this.minecraft.getTextureAtlas().getTexture(var11);
            GlStateManager.disableLighting();
            this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
            blit(var2, var3, this.blitOffset, 16, 16, var10);
            GlStateManager.enableLighting();
            var6 = true;
         }
      }

      if (!var6) {
         if (var5) {
            fill(var2, var3, var2 + 16, var3 + 16, -2130706433);
         }

         GlStateManager.enableDepthTest();
         this.itemRenderer.renderAndDecorateItem(this.minecraft.player, var4, var2, var3);
         this.itemRenderer.renderGuiItemDecorations(this.font, var4, var2, var3, var8);
      }

      this.itemRenderer.blitOffset = 0.0F;
      this.blitOffset = 0;
   }

   private void recalculateQuickCraftRemaining() {
      ItemStack var1 = this.minecraft.player.inventory.getCarried();
      if (!var1.isEmpty() && this.isQuickCrafting) {
         if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = var1.getMaxStackSize();
         } else {
            this.quickCraftingRemainder = var1.getCount();

            ItemStack var4;
            int var6;
            for(Iterator var2 = this.quickCraftSlots.iterator(); var2.hasNext(); this.quickCraftingRemainder -= var4.getCount() - var6) {
               Slot var3 = (Slot)var2.next();
               var4 = var1.copy();
               ItemStack var5 = var3.getItem();
               var6 = var5.isEmpty() ? 0 : var5.getCount();
               AbstractContainerMenu.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, var4, var6);
               int var7 = Math.min(var4.getMaxStackSize(), var3.getMaxStackSize(var4));
               if (var4.getCount() > var7) {
                  var4.setCount(var7);
               }
            }

         }
      }
   }

   private Slot findSlot(double var1, double var3) {
      for(int var5 = 0; var5 < this.menu.slots.size(); ++var5) {
         Slot var6 = (Slot)this.menu.slots.get(var5);
         if (this.isHovering(var6, var1, var3) && var6.isActive()) {
            return var6;
         }
      }

      return null;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if (super.mouseClicked(var1, var3, var5)) {
         return true;
      } else {
         boolean var6 = this.minecraft.options.keyPickItem.matchesMouse(var5);
         Slot var7 = this.findSlot(var1, var3);
         long var8 = Util.getMillis();
         this.doubleclick = this.lastClickSlot == var7 && var8 - this.lastClickTime < 250L && this.lastClickButton == var5;
         this.skipNextRelease = false;
         if (var5 == 0 || var5 == 1 || var6) {
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

            if (this.minecraft.options.touchscreen && var12 && this.minecraft.player.inventory.getCarried().isEmpty()) {
               this.minecraft.setScreen((Screen)null);
               return true;
            }

            if (var13 != -1) {
               if (this.minecraft.options.touchscreen) {
                  if (var7 != null && var7.hasItem()) {
                     this.clickedSlot = var7;
                     this.draggingItem = ItemStack.EMPTY;
                     this.isSplittingStack = var5 == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.isQuickCrafting) {
                  if (this.minecraft.player.inventory.getCarried().isEmpty()) {
                     if (this.minecraft.options.keyPickItem.matchesMouse(var5)) {
                        this.slotClicked(var7, var13, var5, ClickType.CLONE);
                     } else {
                        boolean var14 = var13 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 344));
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
                     } else if (this.minecraft.options.keyPickItem.matchesMouse(var5)) {
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

   protected boolean hasClickedOutside(double var1, double var3, int var5, int var6, int var7) {
      return var1 < (double)var5 || var3 < (double)var6 || var1 >= (double)(var5 + this.imageWidth) || var3 >= (double)(var6 + this.imageHeight);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      Slot var10 = this.findSlot(var1, var3);
      ItemStack var11 = this.minecraft.player.inventory.getCarried();
      if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
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
      Slot var6 = this.findSlot(var1, var3);
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
         if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
            if (var5 == 0 || var5 == 1) {
               if (this.draggingItem.isEmpty() && var6 != this.clickedSlot) {
                  this.draggingItem = this.clickedSlot.getItem();
               }

               var11 = AbstractContainerMenu.canItemQuickReplace(var6, this.draggingItem, false);
               if (var10 != -1 && !this.draggingItem.isEmpty() && var11) {
                  this.slotClicked(this.clickedSlot, this.clickedSlot.index, var5, ClickType.PICKUP);
                  this.slotClicked(var6, var10, 0, ClickType.PICKUP);
                  if (this.minecraft.player.inventory.getCarried().isEmpty()) {
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

               this.draggingItem = ItemStack.EMPTY;
               this.clickedSlot = null;
            }
         } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);
            var13 = this.quickCraftSlots.iterator();

            while(var13.hasNext()) {
               var12 = (Slot)var13.next();
               this.slotClicked(var12, var12.index, AbstractContainerMenu.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
            }

            this.slotClicked((Slot)null, -999, AbstractContainerMenu.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
         } else if (!this.minecraft.player.inventory.getCarried().isEmpty()) {
            if (this.minecraft.options.keyPickItem.matchesMouse(var5)) {
               this.slotClicked(var6, var10, var5, ClickType.CLONE);
            } else {
               var11 = var10 != -999 && (InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 344));
               if (var11) {
                  this.lastQuickMoved = var6 != null && var6.hasItem() ? var6.getItem().copy() : ItemStack.EMPTY;
               }

               this.slotClicked(var6, var10, var5, var11 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
            }
         }
      }

      if (this.minecraft.player.inventory.getCarried().isEmpty()) {
         this.lastClickTime = 0L;
      }

      this.isQuickCrafting = false;
      return true;
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

   protected void slotClicked(Slot var1, int var2, int var3, ClickType var4) {
      if (var1 != null) {
         var2 = var1.index;
      }

      this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, var2, var3, var4, this.minecraft.player);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (super.keyPressed(var1, var2, var3)) {
         return true;
      } else {
         if (var1 == 256 || this.minecraft.options.keyInventory.matches(var1, var2)) {
            this.minecraft.player.closeContainer();
         }

         this.checkNumkeyPressed(var1, var2);
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

   protected boolean checkNumkeyPressed(int var1, int var2) {
      if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null) {
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

   public void tick() {
      super.tick();
      if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
         this.minecraft.player.closeContainer();
      }

   }

   public T getMenu() {
      return this.menu;
   }
}
