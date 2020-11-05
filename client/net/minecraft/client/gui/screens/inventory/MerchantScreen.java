package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Iterator;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
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
   private static final Component TRADES_LABEL = new TranslatableComponent("merchant.trades");
   private static final Component LEVEL_SEPARATOR = new TextComponent(" - ");
   private static final Component DEPRECATED_TOOLTIP = new TranslatableComponent("merchant.deprecated");
   private int shopItem;
   private final MerchantScreen.TradeOfferButton[] tradeOfferButtons = new MerchantScreen.TradeOfferButton[7];
   private int scrollOff;
   private boolean isDragging;

   public MerchantScreen(MerchantMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.imageWidth = 276;
      this.inventoryLabelX = 107;
   }

   private void postButtonClick() {
      ((MerchantMenu)this.menu).setSelectionHint(this.shopItem);
      ((MerchantMenu)this.menu).tryMoveItems(this.shopItem);
      this.minecraft.getConnection().send((Packet)(new ServerboundSelectTradePacket(this.shopItem)));
   }

   protected void init() {
      super.init();
      int var1 = (this.width - this.imageWidth) / 2;
      int var2 = (this.height - this.imageHeight) / 2;
      int var3 = var2 + 16 + 2;

      for(int var4 = 0; var4 < 7; ++var4) {
         this.tradeOfferButtons[var4] = (MerchantScreen.TradeOfferButton)this.addButton(new MerchantScreen.TradeOfferButton(var1 + 5, var3, var4, (var1x) -> {
            if (var1x instanceof MerchantScreen.TradeOfferButton) {
               this.shopItem = ((MerchantScreen.TradeOfferButton)var1x).getIndex() + this.scrollOff;
               this.postButtonClick();
            }

         }));
         var3 += 20;
      }

   }

   protected void renderLabels(PoseStack var1, int var2, int var3) {
      int var4 = ((MerchantMenu)this.menu).getTraderLevel();
      if (var4 > 0 && var4 <= 5 && ((MerchantMenu)this.menu).showProgressBar()) {
         MutableComponent var5 = this.title.copy().append(LEVEL_SEPARATOR).append((Component)(new TranslatableComponent("merchant.level." + var4)));
         int var6 = this.font.width((FormattedText)var5);
         int var7 = 49 + this.imageWidth / 2 - var6 / 2;
         this.font.draw(var1, (Component)var5, (float)var7, 6.0F, 4210752);
      } else {
         this.font.draw(var1, this.title, (float)(49 + this.imageWidth / 2 - this.font.width((FormattedText)this.title) / 2), 6.0F, 4210752);
      }

      this.font.draw(var1, this.inventory.getDisplayName(), (float)this.inventoryLabelX, (float)this.inventoryLabelY, 4210752);
      int var8 = this.font.width((FormattedText)TRADES_LABEL);
      this.font.draw(var1, TRADES_LABEL, (float)(5 - var8 / 2 + 48), 6.0F, 4210752);
   }

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      blit(var1, var5, var6, this.getBlitOffset(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 512);
      MerchantOffers var7 = ((MerchantMenu)this.menu).getOffers();
      if (!var7.isEmpty()) {
         int var8 = this.shopItem;
         if (var8 < 0 || var8 >= var7.size()) {
            return;
         }

         MerchantOffer var9 = (MerchantOffer)var7.get(var8);
         if (var9.isOutOfStock()) {
            this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            blit(var1, this.leftPos + 83 + 99, this.topPos + 35, this.getBlitOffset(), 311.0F, 0.0F, 28, 21, 256, 512);
         }
      }

   }

   private void renderProgressBar(PoseStack var1, int var2, int var3, MerchantOffer var4) {
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      int var5 = ((MerchantMenu)this.menu).getTraderLevel();
      int var6 = ((MerchantMenu)this.menu).getTraderXp();
      if (var5 < 5) {
         blit(var1, var2 + 136, var3 + 16, this.getBlitOffset(), 0.0F, 186.0F, 102, 5, 256, 512);
         int var7 = VillagerData.getMinXpPerLevel(var5);
         if (var6 >= var7 && VillagerData.canLevelUp(var5)) {
            boolean var8 = true;
            float var9 = 100.0F / (float)(VillagerData.getMaxXpPerLevel(var5) - var7);
            int var10 = Math.min(Mth.floor(var9 * (float)(var6 - var7)), 100);
            blit(var1, var2 + 136, var3 + 16, this.getBlitOffset(), 0.0F, 191.0F, var10 + 1, 5, 256, 512);
            int var11 = ((MerchantMenu)this.menu).getFutureTraderXp();
            if (var11 > 0) {
               int var12 = Math.min(Mth.floor((float)var11 * var9), 100 - var10);
               blit(var1, var2 + 136 + var10 + 1, var3 + 16 + 1, this.getBlitOffset(), 2.0F, 182.0F, var12, 3, 256, 512);
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

         blit(var1, var2 + 94, var3 + 18 + var9, this.getBlitOffset(), 0.0F, 199.0F, 6, 27, 256, 512);
      } else {
         blit(var1, var2 + 94, var3 + 18, this.getBlitOffset(), 6.0F, 199.0F, 6, 27, 256, 512);
      }

   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      super.render(var1, var2, var3, var4);
      MerchantOffers var5 = ((MerchantMenu)this.menu).getOffers();
      if (!var5.isEmpty()) {
         int var6 = (this.width - this.imageWidth) / 2;
         int var7 = (this.height - this.imageHeight) / 2;
         int var8 = var7 + 16 + 1;
         int var9 = var6 + 5 + 5;
         RenderSystem.pushMatrix();
         RenderSystem.enableRescaleNormal();
         this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
         this.renderScroller(var1, var6, var7, var5);
         int var10 = 0;
         Iterator var11 = var5.iterator();

         while(true) {
            MerchantOffer var12;
            while(var11.hasNext()) {
               var12 = (MerchantOffer)var11.next();
               if (this.canScroll(var5.size()) && (var10 < this.scrollOff || var10 >= 7 + this.scrollOff)) {
                  ++var10;
               } else {
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
               }
            }

            int var18 = this.shopItem;
            var12 = (MerchantOffer)var5.get(var18);
            if (((MerchantMenu)this.menu).showProgressBar()) {
               this.renderProgressBar(var1, var6, var7, var12);
            }

            if (var12.isOutOfStock() && this.isHovering(186, 35, 22, 21, (double)var2, (double)var3) && ((MerchantMenu)this.menu).canRestock()) {
               this.renderTooltip(var1, DEPRECATED_TOOLTIP, var2, var3);
            }

            MerchantScreen.TradeOfferButton[] var19 = this.tradeOfferButtons;
            int var20 = var19.length;

            for(int var21 = 0; var21 < var20; ++var21) {
               MerchantScreen.TradeOfferButton var22 = var19[var21];
               if (var22.isHovered()) {
                  var22.renderToolTip(var1, var2, var3);
               }

               var22.visible = var22.index < ((MerchantMenu)this.menu).getOffers().size();
            }

            RenderSystem.popMatrix();
            RenderSystem.enableDepthTest();
            break;
         }
      }

      this.renderTooltip(var1, var2, var3);
   }

   private void renderButtonArrows(PoseStack var1, MerchantOffer var2, int var3, int var4) {
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
      if (var2.isOutOfStock()) {
         blit(var1, var3 + 5 + 35 + 20, var4 + 3, this.getBlitOffset(), 25.0F, 171.0F, 10, 9, 256, 512);
      } else {
         blit(var1, var3 + 5 + 35 + 20, var4 + 3, this.getBlitOffset(), 15.0F, 171.0F, 10, 9, 256, 512);
      }

   }

   private void renderAndDecorateCostA(PoseStack var1, ItemStack var2, ItemStack var3, int var4, int var5) {
      this.itemRenderer.renderAndDecorateFakeItem(var2, var4, var5);
      if (var3.getCount() == var2.getCount()) {
         this.itemRenderer.renderGuiItemDecorations(this.font, var2, var4, var5);
      } else {
         this.itemRenderer.renderGuiItemDecorations(this.font, var3, var4, var5, var3.getCount() == 1 ? "1" : null);
         this.itemRenderer.renderGuiItemDecorations(this.font, var2, var4 + 14, var5, var2.getCount() == 1 ? "1" : null);
         this.minecraft.getTextureManager().bind(VILLAGER_LOCATION);
         this.setBlitOffset(this.getBlitOffset() + 300);
         blit(var1, var4 + 7, var5 + 12, this.getBlitOffset(), 0.0F, 176.0F, 9, 2, 256, 512);
         this.setBlitOffset(this.getBlitOffset() - 300);
      }

   }

   private boolean canScroll(int var1) {
      return var1 > 7;
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      int var7 = ((MerchantMenu)this.menu).getOffers().size();
      if (this.canScroll(var7)) {
         int var8 = var7 - 7;
         this.scrollOff = (int)((double)this.scrollOff - var5);
         this.scrollOff = Mth.clamp(this.scrollOff, 0, var8);
      }

      return true;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      int var10 = ((MerchantMenu)this.menu).getOffers().size();
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

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.isDragging = false;
      int var6 = (this.width - this.imageWidth) / 2;
      int var7 = (this.height - this.imageHeight) / 2;
      if (this.canScroll(((MerchantMenu)this.menu).getOffers().size()) && var1 > (double)(var6 + 94) && var1 < (double)(var6 + 94 + 6) && var3 > (double)(var7 + 18) && var3 <= (double)(var7 + 18 + 139 + 1)) {
         this.isDragging = true;
      }

      return super.mouseClicked(var1, var3, var5);
   }

   class TradeOfferButton extends Button {
      final int index;

      public TradeOfferButton(int var2, int var3, int var4, Button.OnPress var5) {
         super(var2, var3, 89, 20, TextComponent.EMPTY, var5);
         this.index = var4;
         this.visible = false;
      }

      public int getIndex() {
         return this.index;
      }

      public void renderToolTip(PoseStack var1, int var2, int var3) {
         if (this.isHovered && ((MerchantMenu)MerchantScreen.this.menu).getOffers().size() > this.index + MerchantScreen.this.scrollOff) {
            ItemStack var4;
            if (var2 < this.x + 20) {
               var4 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostA();
               MerchantScreen.this.renderTooltip(var1, var4, var2, var3);
            } else if (var2 < this.x + 50 && var2 > this.x + 30) {
               var4 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getCostB();
               if (!var4.isEmpty()) {
                  MerchantScreen.this.renderTooltip(var1, var4, var2, var3);
               }
            } else if (var2 > this.x + 65) {
               var4 = ((MerchantOffer)((MerchantMenu)MerchantScreen.this.menu).getOffers().get(this.index + MerchantScreen.this.scrollOff)).getResult();
               MerchantScreen.this.renderTooltip(var1, var4, var2, var3);
            }
         }

      }
   }
}
