package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
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
   private static final ResourceLocation VILLAGER_LOCATION = new ResourceLocation("textures/gui/container/villager2.png");
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
   private static final int TRADE_BUTTON_WIDTH = 89;
   private static final int SCROLLER_HEIGHT = 27;
   private static final int SCROLLER_WIDTH = 6;
   private static final int SCROLL_BAR_HEIGHT = 139;
   private static final int SCROLL_BAR_TOP_POS_Y = 18;
   private static final int SCROLL_BAR_START_X = 94;
   private static final Component TRADES_LABEL = Component.translatable("merchant.trades");
   private static final Component LEVEL_SEPARATOR = Component.literal(" - ");
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

      for(int var4 = 0; var4 < 7; ++var4) {
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
   protected void renderLabels(PoseStack var1, int var2, int var3) {
      int var4 = this.menu.getTraderLevel();
      if (var4 > 0 && var4 <= 5 && this.menu.showProgressBar()) {
         MutableComponent var5 = this.title.copy().append(LEVEL_SEPARATOR).append(Component.translatable("merchant.level." + var4));
         int var6 = this.font.width(var5);
         int var7 = 49 + this.imageWidth / 2 - var6 / 2;
         this.font.draw(var1, var5, (float)var7, 6.0F, 4210752);
      } else {
         this.font.draw(var1, this.title, (float)(49 + this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0F, 4210752);
      }

      this.font.draw(var1, this.playerInventoryTitle, (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
      int var8 = this.font.width(TRADES_LABEL);
      this.font.draw(var1, TRADES_LABEL, (float)(5 - var8 / 2 + 48), 6.0F, 4210752);
   }

   @Override
   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      blit(var1, var5, var6, this.getBlitOffset(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 256);
      MerchantOffers var7 = this.menu.getOffers();
      if (!var7.isEmpty()) {
         int var8 = this.shopItem;
         if (var8 < 0 || var8 >= var7.size()) {
            return;
         }

         MerchantOffer var9 = var7.get(var8);
         if (var9.isOutOfStock()) {
            RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            blit(var1, this.leftPos + 83 + 99, this.topPos + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 512, 256);
         }
      }
   }

   private void renderProgressBar(PoseStack var1, int var2, int var3, MerchantOffer var4) {
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
      int var5 = this.menu.getTraderLevel();
      int var6 = this.menu.getTraderXp();
      if (var5 < 5) {
         blit(var1, var2 + 136, var3 + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 512, 256);
         int var7 = VillagerData.getMinXpPerLevel(var5);
         if (var6 >= var7 && VillagerData.canLevelUp(var5)) {
            boolean var8 = true;
            float var9 = 100.0F / (float)(VillagerData.getMaxXpPerLevel(var5) - var7);
            int var10 = Math.min(Mth.floor(var9 * (float)(var6 - var7)), 100);
            blit(var1, var2 + 136, var3 + 16, this.getBlitOffset(), 0.0F, 191.0F, var10 + 1, 5, 512, 256);
            int var11 = this.menu.getFutureTraderXp();
            if (var11 > 0) {
               int var12 = Math.min(Mth.floor((float)var11 * var9), 100 - var10);
               blit(var1, var2 + 136 + var10 + 1, var3 + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, var12, 3, 512, 256);
            }
         }
      }
   }

   private void renderScroller(PoseStack var1, int var2, int var3, MerchantOffers var4) {
      int var5 = var4.size() + 1 - 7;
      if (var5 > 1) {
         int var6 = 139 - (27 + (var5 - 1) * 139 / var5);
         int var7 = 1 + var6 / var5 + 139 / var5;
         boolean var8 = true;
         int var9 = Math.min(113, this.scrollOff * var7);
         if (this.scrollOff == var5 - 1) {
            var9 = 113;
         }

         blit(var1, var2 + 94, var3 + 18 + var9, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 512, 256);
      } else {
         blit(var1, var2 + 94, var3 + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 512, 256);
      }
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      MerchantOffers var5 = this.menu.getOffers();
      if (!var5.isEmpty()) {
         int var6 = (this.width - this.imageWidth) / 2;
         int var7 = (this.height - this.imageHeight) / 2;
         int var8 = var7 + 16 + 1;
         int var9 = var6 + 5 + 5;
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
         this.renderScroller(var1, var6, var7, var5);
         int var10 = 0;

         for(MerchantOffer var12 : var5) {
            if (!this.canScroll(var5.size()) || var10 >= this.scrollOff && var10 < 7 + this.scrollOff) {
               ItemStack var13 = var12.getBaseCostA();
               ItemStack var14 = var12.getCostA();
               ItemStack var15 = var12.getCostB();
               ItemStack var16 = var12.getResult();
               this.itemRenderer.blitOffset = 100.0F;
               int var17 = var8 + 2;
               this.renderAndDecorateCostA(var1, var14, var13, var9, var17);
               if (!var15.isEmpty()) {
                  this.itemRenderer.renderAndDecorateFakeItem(var15, var6 + 5 + 35, var17);
                  this.itemRenderer.renderGuiItemDecorations(this.font, var15, var6 + 5 + 35, var17);
               }

               this.renderButtonArrows(var1, var12, var6, var17);
               this.itemRenderer.renderAndDecorateFakeItem(var16, var6 + 5 + 68, var17);
               this.itemRenderer.renderGuiItemDecorations(this.font, var16, var6 + 5 + 68, var17);
               this.itemRenderer.blitOffset = 0.0F;
               var8 += 20;
               ++var10;
            } else {
               ++var10;
            }
         }

         int var18 = this.shopItem;
         MerchantOffer var19 = var5.get(var18);
         if (this.menu.showProgressBar()) {
            this.renderProgressBar(var1, var6, var7, var19);
         }

         if (var19.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)var2, (double)var3) && this.menu.canRestock()) {
            this.renderTooltip(var1, DEPRECATED_TOOLTIP, var2, var3);
         }

         for(MerchantScreen.TradeOfferButton var23 : this.tradeOfferButtons) {
            if (var23.isHoveredOrFocused()) {
               var23.renderToolTip(var1, var2, var3);
            }

            var23.visible = var23.index < this.menu.getOffers().size();
         }

         RenderSystem.enableDepthTest();
      }

      this.renderTooltip(var1, var2, var3);
   }

   private void renderButtonArrows(PoseStack var1, MerchantOffer var2, int var3, int var4) {
      RenderSystem.enableBlend();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
      if (var2.isOutOfStock()) {
         blit(var1, var3 + 5 + 35 + 20, var4 + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 512, 256);
      } else {
         blit(var1, var3 + 5 + 35 + 20, var4 + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 512, 256);
      }
   }

   private void renderAndDecorateCostA(PoseStack var1, ItemStack var2, ItemStack var3, int var4, int var5) {
      this.itemRenderer.renderAndDecorateFakeItem(var2, var4, var5);
      if (var3.getCount() == var2.getCount()) {
         this.itemRenderer.renderGuiItemDecorations(this.font, var2, var4, var5);
      } else {
         this.itemRenderer.renderGuiItemDecorations(this.font, var3, var4, var5, var3.getCount() == 1 ? "1" : null);
         this.itemRenderer.renderGuiItemDecorations(this.font, var2, var4 + 14, var5, var2.getCount() == 1 ? "1" : null);
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderTexture(0, VILLAGER_LOCATION);
         this.setBlitOffset(this.getBlitOffset() + 300);
         blit(var1, var4 + 7, var5 + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 512, 256);
         this.setBlitOffset(this.getBlitOffset() - 300);
      }
   }

   private boolean canScroll(int var1) {
      return var1 > 7;
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5) {
      int var7 = this.menu.getOffers().size();
      if (this.canScroll(var7)) {
         int var8 = var7 - 7;
         this.scrollOff = Mth.clamp((int)((double)this.scrollOff - var5), 0, var8);
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

      public TradeOfferButton(int var2, int var3, int var4, Button.OnPress var5) {
         super(var2, var3, 89, 20, CommonComponents.EMPTY, var5, DEFAULT_NARRATION);
         this.index = var4;
         this.visible = false;
      }

      public int getIndex() {
         return this.index;
      }

      public void renderToolTip(PoseStack var1, int var2, int var3) {
         if (this.isHovered && MerchantScreen.this.menu.getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
            if (var2 < this.getX() + 20) {
               ItemStack var4 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostA();
               MerchantScreen.this.renderTooltip(var1, var4, var2, var3);
            } else if (var2 < this.getX() + 50 && var2 > this.getX() + 30) {
               ItemStack var6 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getCostB();
               if (!var6.isEmpty()) {
                  MerchantScreen.this.renderTooltip(var1, var6, var2, var3);
               }
            } else if (var2 > this.getX() + 65) {
               ItemStack var5 = MerchantScreen.this.menu.getOffers().get(this.index + MerchantScreen.this.scrollOff).getResult();
               MerchantScreen.this.renderTooltip(var1, var5, var2, var3);
            }
         }
      }
   }
}
