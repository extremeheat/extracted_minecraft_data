package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundSelectTradePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class MerchantScreen extends AbstractContainerScreen<MerchantMenu> {
   private static final ResourceLocation OUT_OF_STOCK_SPRITE = new ResourceLocation("container/villager/out_of_stock");
   private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = new ResourceLocation("container/villager/experience_bar_background");
   private static final ResourceLocation EXPERIENCE_BAR_CURRENT_SPRITE = new ResourceLocation("container/villager/experience_bar_current");
   private static final ResourceLocation EXPERIENCE_BAR_RESULT_SPRITE = new ResourceLocation("container/villager/experience_bar_result");
   private static final ResourceLocation SCROLLER_SPRITE = new ResourceLocation("container/villager/scroller");
   private static final ResourceLocation SCROLLER_DISABLED_SPRITE = new ResourceLocation("container/villager/scroller_disabled");
   private static final ResourceLocation TRADE_ARROW_OUT_OF_STOCK_SPRITE = new ResourceLocation("container/villager/trade_arrow_out_of_stock");
   private static final ResourceLocation TRADE_ARROW_SPRITE = new ResourceLocation("container/villager/trade_arrow");
   private static final ResourceLocation DISCOUNT_STRIKETHRUOGH_SPRITE = new ResourceLocation("container/villager/discount_strikethrough");
   private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager.png");
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
   private final MerchantScreen.TradeOfferButton[] tradeOfferButtons = new MerchantScreen.TradeOfferButton[7];
   int scrollOff;
   private boolean isDragging;

   public MerchantScreen(MerchantMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.imageWidth = 276;
      this.inventoryLabelX = 107;
   }

   private void postButtonClick() {
      this.menu.setSelectionHint(this.shopItem);
      this.menu.tryMoveItems(this.shopItem);
      this.minecraft.getConnection().send(new ServerboundSelectTradePacket(this.shopItem));
   }

   @Override
   protected void init() {
      super.init();
      int var1 = (this.width - this.imageWidth) / 2;
      int var2 = (this.height - this.imageHeight) / 2;
      int var3 = var2 + 16 + 2;

      for (int var4 = 0; var4 < 7; var4++) {
         this.tradeOfferButtons[var4] = this.addRenderableWidget(new MerchantScreen.TradeOfferButton(var1 + 5, var3, var4, var1x -> {
            if (var1x instanceof MerchantScreen.TradeOfferButton) {
               this.shopItem = ((MerchantScreen.TradeOfferButton)var1x).getIndex() + this.scrollOff;
               this.postButtonClick();
            }
         }));
         var3 += 20;
      }
   }

   @Override
   protected void renderLabels(GuiGraphics var1, int var2, int var3) {
      int var4 = this.menu.getTraderLevel();
      if (var4 > 0 && var4 <= 5 && this.menu.showProgressBar()) {
         MutableComponent var5 = Component.translatable("merchant.title", this.title, Component.translatable("merchant.level." + var4));
         int var6 = this.font.width(var5);
         int var7 = 49 + this.imageWidth / 2 - var6 / 2;
         var1.drawString(this.font, var5, var7, 6, 4210752, false);
      } else {
         var1.drawString(this.font, this.title, 49 + this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 4210752, false);
      }

      var1.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
      int var8 = this.font.width(TRADES_LABEL);
      var1.drawString(this.font, TRADES_LABEL, 5 - var8 / 2 + 48, 6, 4210752, false);
   }

   @Override
   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      var1.blit(VILLAGER_LOCATION, var5, var6, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
      MerchantOffers var7 = this.menu.getOffers();
      if (!var7.isEmpty()) {
         int var8 = this.shopItem;
         if (var8 < 0 || var8 >= var7.size()) {
            return;
         }

         MerchantOffer var9 = var7.get(var8);
         if (var9.isOutOfStock()) {
            var1.blitSprite(OUT_OF_STOCK_SPRITE, this.leftPos + 83 + 99, this.topPos + 35, 0, 28, 21);
         }
      }
   }

   private void renderProgressBar(GuiGraphics var1, int var2, int var3, MerchantOffer var4) {
      int var5 = this.menu.getTraderLevel();
      int var6 = this.menu.getTraderXp();
      if (var5 < 5) {
         var1.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, var2 + 136, var3 + 16, 0, 102, 5);
         int var7 = VillagerData.getMinXpPerLevel(var5);
         if (var6 >= var7 && VillagerData.canLevelUp(var5)) {
            byte var8 = 102;
            float var9 = 102.0F / (float)(VillagerData.getMaxXpPerLevel(var5) - var7);
            int var10 = Math.min(Mth.floor(var9 * (float)(var6 - var7)), 102);
            var1.blitSprite(EXPERIENCE_BAR_CURRENT_SPRITE, 102, 5, 0, 0, var2 + 136, var3 + 16, 0, var10, 5);
            int var11 = this.menu.getFutureTraderXp();
            if (var11 > 0) {
               int var12 = Math.min(Mth.floor((float)var11 * var9), 102 - var10);
               var1.blitSprite(EXPERIENCE_BAR_RESULT_SPRITE, 102, 5, var10, 0, var2 + 136 + var10, var3 + 16, 0, var12, 5);
            }
         }
      }
   }

   private void renderScroller(GuiGraphics var1, int var2, int var3, MerchantOffers var4) {
      int var5 = var4.size() + 1 - 7;
      if (var5 > 1) {
         int var6 = 139 - (27 + (var5 - 1) * 139 / var5);
         int var7 = 1 + var6 / var5 + 139 / var5;
         byte var8 = 113;
         int var9 = Math.min(113, this.scrollOff * var7);
         if (this.scrollOff == var5 - 1) {
            var9 = 113;
         }

         var1.blitSprite(SCROLLER_SPRITE, var2 + 94, var3 + 18 + var9, 0, 6, 27);
      } else {
         var1.blitSprite(SCROLLER_DISABLED_SPRITE, var2 + 94, var3 + 18, 0, 6, 27);
      }
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      MerchantOffers var5 = this.menu.getOffers();
      if (!var5.isEmpty()) {
         int var6 = (this.width - this.imageWidth) / 2;
         int var7 = (this.height - this.imageHeight) / 2;
         int var8 = var7 + 16 + 1;
         int var9 = var6 + 5 + 5;
         this.renderScroller(var1, var6, var7, var5);
         int var10 = 0;

         for (MerchantOffer var12 : var5) {
            if (!this.canScroll(var5.size()) || var10 >= this.scrollOff && var10 < 7 + this.scrollOff) {
               ItemStack var13 = var12.getBaseCostA();
               ItemStack var14 = var12.getCostA();
               ItemStack var15 = var12.getCostB();
               ItemStack var16 = var12.getResult();
               var1.pose().pushPose();
               var1.pose().translate(0.0F, 0.0F, 100.0F);
               int var17 = var8 + 2;
               this.renderAndDecorateCostA(var1, var14, var13, var9, var17);
               if (!var15.isEmpty()) {
                  var1.renderFakeItem(var15, var6 + 5 + 35, var17);
                  var1.renderItemDecorations(this.font, var15, var6 + 5 + 35, var17);
               }

               this.renderButtonArrows(var1, var12, var6, var17);
               var1.renderFakeItem(var16, var6 + 5 + 68, var17);
               var1.renderItemDecorations(this.font, var16, var6 + 5 + 68, var17);
               var1.pose().popPose();
               var8 += 20;
               var10++;
            } else {
               var10++;
            }
         }

         int var18 = this.shopItem;
         MerchantOffer var19 = var5.get(var18);
         if (this.menu.showProgressBar()) {
            this.renderProgressBar(var1, var6, var7, var19);
         }

         if (var19.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)var2, (double)var3) && this.menu.canRestock()) {
            var1.renderTooltip(this.font, DEPRECATED_TOOLTIP, var2, var3);
         }

         for (MerchantScreen.TradeOfferButton var23 : this.tradeOfferButtons) {
            if (var23.isHoveredOrFocused()) {
               var23.renderToolTip(var1, var2, var3);
            }

            var23.visible = var23.index < this.menu.getOffers().size();
         }

         RenderSystem.enableDepthTest();
      }

      this.renderTooltip(var1, var2, var3);
   }

   private void renderButtonArrows(GuiGraphics var1, MerchantOffer var2, int var3, int var4) {
      RenderSystem.enableBlend();
      if (var2.isOutOfStock()) {
         var1.blitSprite(TRADE_ARROW_OUT_OF_STOCK_SPRITE, var3 + 5 + 35 + 20, var4 + 3, 0, 10, 9);
      } else {
         var1.blitSprite(TRADE_ARROW_SPRITE, var3 + 5 + 35 + 20, var4 + 3, 0, 10, 9);
      }
   }

   private void renderAndDecorateCostA(GuiGraphics var1, ItemStack var2, ItemStack var3, int var4, int var5) {
      var1.renderFakeItem(var2, var4, var5);
      if (var3.getCount() == var2.getCount()) {
         var1.renderItemDecorations(this.font, var2, var4, var5);
      } else {
         var1.renderItemDecorations(this.font, var3, var4, var5, var3.getCount() == 1 ? "1" : null);
         var1.renderItemDecorations(this.font, var2, var4 + 14, var5, var2.getCount() == 1 ? "1" : null);
         var1.pose().pushPose();
         var1.pose().translate(0.0F, 0.0F, 300.0F);
         var1.blitSprite(DISCOUNT_STRIKETHRUOGH_SPRITE, var4 + 7, var5 + 12, 0, 9, 2);
         var1.pose().popPose();
      }
   }

   private boolean canScroll(int var1) {
      return var1 > 7;
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      int var9 = this.menu.getOffers().size();
      if (this.canScroll(var9)) {
         int var10 = var9 - 7;
         this.scrollOff = Mth.clamp((int)((double)this.scrollOff - var7), 0, var10);
      }

      return true;
   }

   @Override
   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      int var10 = this.menu.getOffers().size();
      if (this.isDragging) {
         int var11 = this.topPos + 18;
         int var12 = var11 + 139;
         int var13 = var10 - 7;
         float var14 = ((float)var3 - (float)var11 - 13.5F) / ((float)(var12 - var11) - 27.0F);
         var14 = var14 * (float)var13 + 0.5F;
         this.scrollOff = Mth.clamp((int)var14, 0, var13);
         return true;
      } else {
         return super.mouseDragged(var1, var3, var5, var6, var8);
      }
   }

   @Override
   public boolean mouseClicked(double var1, double var3, int var5) {
      this.isDragging = false;
      int var6 = (this.width - this.imageWidth) / 2;
      int var7 = (this.height - this.imageHeight) / 2;
      if (this.canScroll(this.menu.getOffers().size())
         && var1 > (double)(var6 + 94)
         && var1 < (double)(var6 + 94 + 6)
         && var3 > (double)(var7 + 18)
         && var3 <= (double)(var7 + 18 + 139 + 1)) {
         this.isDragging = true;
      }

      return super.mouseClicked(var1, var3, var5);
   }

   class TradeOfferButton extends Button {
      final int index;

      public TradeOfferButton(final int param2, final int param3, final int param4, final Button.OnPress param5) {
         super(nullx, nullxx, 88, 20, CommonComponents.EMPTY, nullxxxx, DEFAULT_NARRATION);
         this.index = nullxxx;
         this.visible = false;
      }

      public int getIndex() {
         return this.index;
      }

      public void renderToolTip(GuiGraphics var1, int var2, int var3) {
         if (this.isHovered && MerchantScreen.this.menu.getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
            if (var2 < this.getX() + 20) {
               ItemStack var4 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostA();
               var1.renderTooltip(MerchantScreen.this.font, var4, var2, var3);
            } else if (var2 < this.getX() + 50 && var2 > this.getX() + 30) {
               ItemStack var6 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostB();
               if (!var6.isEmpty()) {
                  var1.renderTooltip(MerchantScreen.this.font, var6, var2, var3);
               }
            } else if (var2 > this.getX() + 65) {
               ItemStack var5 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getResult();
               var1.renderTooltip(MerchantScreen.this.font, var5, var2, var3);
            }
         }
      }
   }
}
