package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantScreen extends AbstractContainerScreen<MerchantMenu> {
   private static final Identifier OUT_OF_STOCK_SPRITE = Identifier.withDefaultNamespace("container/villager/out_of_stock");
   private static final Identifier EXPERIENCE_BAR_BACKGROUND_SPRITE = Identifier.withDefaultNamespace("container/villager/experience_bar_background");
   private static final Identifier EXPERIENCE_BAR_CURRENT_SPRITE = Identifier.withDefaultNamespace("container/villager/experience_bar_current");
   private static final Identifier EXPERIENCE_BAR_RESULT_SPRITE = Identifier.withDefaultNamespace("container/villager/experience_bar_result");
   private static final Identifier SCROLLER_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller");
   private static final Identifier SCROLLER_DISABLED_SPRITE = Identifier.withDefaultNamespace("container/villager/scroller_disabled");
   private static final Identifier TRADE_ARROW_OUT_OF_STOCK_SPRITE = Identifier.withDefaultNamespace("container/villager/trade_arrow_out_of_stock");
   private static final Identifier TRADE_ARROW_SPRITE = Identifier.withDefaultNamespace("container/villager/trade_arrow");
   private static final Identifier DISCOUNT_STRIKETHRUOGH_SPRITE = Identifier.withDefaultNamespace("container/villager/discount_strikethrough");
   private static final Identifier VILLAGER_LOCATION = Identifier.withDefaultNamespace("textures/gui/container/villager.png");
   private static final int TEXTURE_WIDTH = 512;
   private static final int TEXTURE_HEIGHT = 256;
   private static final int MERCHANT_MENU_PART_X = 99;
   private static final int PROGRESS_BAR_X = 136;
   private static final int PROGRESS_BAR_Y = 16;
   private static final int SELL_ITEM_1_X = 5;
   private static final int SELL_ITEM_2_X = 35;
   private static final int BUY_ITEM_X = 68;
   private static final int LABEL_Y = 6;
   private static final int NUMBER_OF_OFFER_BUTTONS = 7;
   private static final int TRADE_BUTTON_X = 5;
   private static final int TRADE_BUTTON_HEIGHT = 20;
   private static final int TRADE_BUTTON_WIDTH = 88;
   private static final int SCROLLER_HEIGHT = 27;
   private static final int SCROLLER_WIDTH = 6;
   private static final int SCROLL_BAR_HEIGHT = 139;
   private static final int SCROLL_BAR_TOP_POS_Y = 18;
   private static final int SCROLL_BAR_START_X = 94;
   private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
   private static final Component DEPRECATED_TOOLTIP = Component.translatable("merchant.deprecated");
   private int shopItem;
   private final TradeOfferButton[] tradeOfferButtons = new TradeOfferButton[7];
   int scrollOff;
   private boolean isDragging;

   public MerchantScreen(MerchantMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.imageWidth = 276;
      this.inventoryLabelX = 107;
   }

   private void postButtonClick() {
      ((MerchantMenu)this.menu).setSelectionHint(this.shopItem);
      ((MerchantMenu)this.menu).tryMoveItems(this.shopItem);
      this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
   }

   protected void init() {
      super.init();
      int var1 = (this.width - this.imageWidth) / 2;
      int var2 = (this.height - this.imageHeight) / 2;
      int var3 = var2 + 16 + 2;

      for(int var4 = 0; var4 < 7; ++var4) {
         this.tradeOfferButtons[var4] = (TradeOfferButton)this.addRenderableWidget(new TradeOfferButton(var1 + 5, var3, var4, (var1x) -> {
            if (var1x instanceof TradeOfferButton) {
               this.shopItem = ((TradeOfferButton)var1x).getIndex() + this.scrollOff;
               this.postButtonClick();
            }

         }));
         var3 += 20;
      }

   }

   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      int var4 = ((MerchantMenu)this.menu).getTraderLevel();
      if (var4 > 0 && var4 <= 5 && ((MerchantMenu)this.menu).showProgressBar()) {
         MutableComponent var5 = Component.translatable("merchant.title", this.title, Component.translatable("merchant.level." + var4));
         int var6 = this.font.width((FormattedText)var5);
         int var7 = 49 + this.imageWidth / 2 - var6 / 2;
         var1.drawString(this.font, (Component)var5, var7, 6, -12566464, false);
      } else {
         var1.drawString(this.font, (Component)this.title, 49 + this.imageWidth / 2 - this.font.width((FormattedText)this.title) / 2, 6, -12566464, false);
      }

      var1.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, -12566464, false);
      int var8 = this.font.width((FormattedText)TRADES_LABEL);
      var1.drawString(this.font, (Component)TRADES_LABEL, 5 - var8 / 2 + 48, 6, -12566464, false);
   }

   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(RenderPipelines.GUI_TEXTURED, VILLAGER_LOCATION, var5, var6, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
      MerchantOffers var7 = ((MerchantMenu)this.menu).getOffers();
      if (!var7.isEmpty()) {
         int var8 = this.shopItem;
         if (var8 < 0 || var8 >= var7.size()) {
            return;
         }

         MerchantOffer var9 = (MerchantOffer)var7.get(var8);
         if (var9.isOutOfStock()) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)OUT_OF_STOCK_SPRITE, this.leftPos + 83 + 99, this.topPos + 35, 28, 21);
         }
      }

   }

   private void renderProgressBar(GuiGraphics var1, int var2, int var3, MerchantOffer var4) {
      int var5 = ((MerchantMenu)this.menu).getTraderLevel();
      int var6 = ((MerchantMenu)this.menu).getTraderXp();
      if (var5 < 5) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)EXPERIENCE_BAR_BACKGROUND_SPRITE, var2 + 136, var3 + 16, 102, 5);
         int var7 = VillagerData.getMinXpPerLevel(var5);
         if (var6 >= var7 && VillagerData.canLevelUp(var5)) {
            boolean var8 = true;
            float var9 = 102.0F / (float)(VillagerData.getMaxXpPerLevel(var5) - var7);
            int var10 = Math.min(Mth.floor(var9 * (float)(var6 - var7)), 102);
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_CURRENT_SPRITE, 102, 5, 0, 0, var2 + 136, var3 + 16, var10, 5);
            int var11 = ((MerchantMenu)this.menu).getFutureTraderXp();
            if (var11 > 0) {
               int var12 = Math.min(Mth.floor((float)var11 * var9), 102 - var10);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, EXPERIENCE_BAR_RESULT_SPRITE, 102, 5, var10, 0, var2 + 136 + var10, var3 + 16, var12, 5);
            }

         }
      }
   }

   private void renderScroller(GuiGraphics var1, int var2, int var3, int var4, int var5, MerchantOffers var6) {
      int var7 = var6.size() + 1 - 7;
      if (var7 > 1) {
         int var8 = 139 - (27 + (var7 - 1) * 139 / var7);
         int var9 = 1 + var8 / var7 + 139 / var7;
         boolean var10 = true;
         int var11 = Math.min(113, this.scrollOff * var9);
         if (this.scrollOff == var7 - 1) {
            var11 = 113;
         }

         int var12 = var2 + 94;
         int var13 = var3 + 18 + var11;
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SCROLLER_SPRITE, var12, var13, 6, 27);
         if (var4 >= var12 && var4 < var2 + 94 + 6 && var5 >= var13 && var5 <= var13 + 27) {
            var1.requestCursor(this.isDragging ? CursorTypes.RESIZE_NS : CursorTypes.POINTING_HAND);
         }
      } else {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)SCROLLER_DISABLED_SPRITE, var2 + 94, var3 + 18, 6, 27);
      }

   }

   public void renderContents(GuiGraphics var1, int var2, int var3, float var4) {
      super.renderContents(var1, var2, var3, var4);
      MerchantOffers var5 = ((MerchantMenu)this.menu).getOffers();
      if (!var5.isEmpty()) {
         int var6 = (this.width - this.imageWidth) / 2;
         int var7 = (this.height - this.imageHeight) / 2;
         int var8 = var7 + 16 + 1;
         int var9 = var6 + 5 + 5;
         this.renderScroller(var1, var6, var7, var2, var3, var5);
         int var10 = 0;

         for(MerchantOffer var12 : var5) {
            if (!this.canScroll(var5.size()) || var10 >= this.scrollOff && var10 < 7 + this.scrollOff) {
               ItemStack var13 = var12.getBaseCostA();
               ItemStack var14 = var12.getCostA();
               ItemStack var15 = var12.getCostB();
               ItemStack var16 = var12.getResult();
               int var17 = var8 + 2;
               this.renderAndDecorateCostA(var1, var14, var13, var9, var17);
               if (!var15.isEmpty()) {
                  var1.renderFakeItem(var15, var6 + 5 + 35, var17);
                  var1.renderItemDecorations(this.font, var15, var6 + 5 + 35, var17);
               }

               this.renderButtonArrows(var1, var12, var6, var17);
               var1.renderFakeItem(var16, var6 + 5 + 68, var17);
               var1.renderItemDecorations(this.font, var16, var6 + 5 + 68, var17);
               var8 += 20;
               ++var10;
            } else {
               ++var10;
            }
         }

         int var18 = this.shopItem;
         MerchantOffer var19 = (MerchantOffer)var5.get(var18);
         if (((MerchantMenu)this.menu).showProgressBar()) {
            this.renderProgressBar(var1, var6, var7, var19);
         }

         if (var19.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)var2, (double)var3) && ((MerchantMenu)this.menu).canRestock()) {
            var1.setTooltipForNextFrame(this.font, DEPRECATED_TOOLTIP, var2, var3);
         }

         for(TradeOfferButton var23 : this.tradeOfferButtons) {
            if (var23.isHoveredOrFocused()) {
               var23.renderToolTip(var1, var2, var3);
            }

            var23.visible = var23.index < ((MerchantMenu)this.menu).getOffers().size();
         }
      }

      this.renderTooltip(var1, var2, var3);
   }

   private void renderButtonArrows(GuiGraphics var1, MerchantOffer var2, int var3, int var4) {
      if (var2.isOutOfStock()) {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)TRADE_ARROW_OUT_OF_STOCK_SPRITE, var3 + 5 + 35 + 20, var4 + 3, 10, 9);
      } else {
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)TRADE_ARROW_SPRITE, var3 + 5 + 35 + 20, var4 + 3, 10, 9);
      }

   }

   private void renderAndDecorateCostA(GuiGraphics var1, ItemStack var2, ItemStack var3, int var4, int var5) {
      var1.renderFakeItem(var2, var4, var5);
      if (var3.getCount() == var2.getCount()) {
         var1.renderItemDecorations(this.font, var2, var4, var5);
      } else {
         var1.renderItemDecorations(this.font, var3, var4, var5, var3.getCount() == 1 ? "1" : null);
         var1.renderItemDecorations(this.font, var2, var4 + 14, var5, var2.getCount() == 1 ? "1" : null);
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (Identifier)DISCOUNT_STRIKETHRUOGH_SPRITE, var4 + 7, var5 + 12, 9, 2);
      }

   }

   private boolean canScroll(int var1) {
      return var1 > 7;
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      if (super.mouseScrolled(var1, var3, var5, var7)) {
         return true;
      } else {
         int var9 = ((MerchantMenu)this.menu).getOffers().size();
         if (this.canScroll(var9)) {
            int var10 = var9 - 7;
            this.scrollOff = Mth.clamp((int)((double)this.scrollOff - var7), 0, var10);
         }

         return true;
      }
   }

   public boolean mouseDragged(MouseButtonEvent var1, double var2, double var4) {
      int var6 = ((MerchantMenu)this.menu).getOffers().size();
      if (this.isDragging) {
         int var7 = this.topPos + 18;
         int var8 = var7 + 139;
         int var9 = var6 - 7;
         float var10 = ((float)var1.y() - (float)var7 - 13.5F) / ((float)(var8 - var7) - 27.0F);
         var10 = var10 * (float)var9 + 0.5F;
         this.scrollOff = Mth.clamp((int)var10, 0, var9);
         return true;
      } else {
         return super.mouseDragged(var1, var2, var4);
      }
   }

   public boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      int var3 = (this.width - this.imageWidth) / 2;
      int var4 = (this.height - this.imageHeight) / 2;
      if (this.canScroll(((MerchantMenu)this.menu).getOffers().size()) && var1.x() > (double)(var3 + 94) && var1.x() < (double)(var3 + 94 + 6) && var1.y() > (double)(var4 + 18) && var1.y() <= (double)(var4 + 18 + 139 + 1)) {
         this.isDragging = true;
      }

      return super.mouseClicked(var1, var2);
   }

   public boolean mouseReleased(MouseButtonEvent var1) {
      this.isDragging = false;
      return super.mouseReleased(var1);
   }

   class TradeOfferButton extends Button.Plain {
      final int index;

      public TradeOfferButton(final int var2, final int var3, final int var4, final Button.OnPress var5) {
         super(var2, var3, 88, 20, CommonComponents.EMPTY, var5, DEFAULT_NARRATION);
         this.index = var4;
         this.visible = false;
      }

      public int getIndex() {
         return this.index;
      }

      public void renderToolTip(GuiGraphics var1, int var2, int var3) {
         if (this.isHovered && ((MerchantMenu)MerchantScreen.this.menu).getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
            if (var2 < this.getX() + 20) {
               ItemStack var4 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostA();
               var1.setTooltipForNextFrame(MerchantScreen.this.font, var4, var2, var3);
            } else if (var2 < this.getX() + 50 && var2 > this.getX() + 30) {
               ItemStack var6 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostB();
               if (!var6.isEmpty()) {
                  var1.setTooltipForNextFrame(MerchantScreen.this.font, var6, var2, var3);
               }
            } else if (var2 > this.getX() + 65) {
               ItemStack var5 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getResult();
               var1.setTooltipForNextFrame(MerchantScreen.this.font, var5, var2, var3);
            }
         }

      }
   }
}
