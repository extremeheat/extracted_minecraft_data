package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyScreen extends AbstractContainerScreen<CartographyMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");

   public CartographyScreen(CartographyMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
   }

   public void render(int var1, int var2, float var3) {
      super.render(var1, var2, var3);
      this.renderTooltip(var1, var2);
   }

   protected void renderLabels(int var1, int var2) {
      this.font.draw(this.title.getColoredString(), 8.0F, 4.0F, 4210752);
      this.font.draw(this.inventory.getDisplayName().getColoredString(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void renderBg(float var1, int var2, int var3) {
      this.renderBackground();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int var4 = this.leftPos;
      int var5 = this.topPos;
      this.blit(var4, var5, 0, 0, this.imageWidth, this.imageHeight);
      Item var6 = ((CartographyMenu)this.menu).getSlot(1).getItem().getItem();
      boolean var7 = var6 == Items.MAP;
      boolean var8 = var6 == Items.PAPER;
      boolean var9 = var6 == Items.GLASS_PANE;
      ItemStack var10 = ((CartographyMenu)this.menu).getSlot(0).getItem();
      boolean var12 = false;
      MapItemSavedData var11;
      if (var10.getItem() == Items.FILLED_MAP) {
         var11 = MapItem.getSavedData(var10, this.minecraft.level);
         if (var11 != null) {
            if (var11.locked) {
               var12 = true;
               if (var8 || var9) {
                  this.blit(var4 + 35, var5 + 31, this.imageWidth + 50, 132, 28, 21);
               }
            }

            if (var8 && var11.scale >= 4) {
               var12 = true;
               this.blit(var4 + 35, var5 + 31, this.imageWidth + 50, 132, 28, 21);
            }
         }
      } else {
         var11 = null;
      }

      this.renderResultingMap(var11, var7, var8, var9, var12);
   }

   private void renderResultingMap(@Nullable MapItemSavedData var1, boolean var2, boolean var3, boolean var4, boolean var5) {
      int var6 = this.leftPos;
      int var7 = this.topPos;
      if (var3 && !var5) {
         this.blit(var6 + 67, var7 + 13, this.imageWidth, 66, 66, 66);
         this.renderMap(var1, var6 + 85, var7 + 31, 0.226F);
      } else if (var2) {
         this.blit(var6 + 67 + 16, var7 + 13, this.imageWidth, 132, 50, 66);
         this.renderMap(var1, var6 + 86, var7 + 16, 0.34F);
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, 1.0F);
         this.blit(var6 + 67, var7 + 13 + 16, this.imageWidth, 132, 50, 66);
         this.renderMap(var1, var6 + 70, var7 + 32, 0.34F);
         GlStateManager.popMatrix();
      } else if (var4) {
         this.blit(var6 + 67, var7 + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(var1, var6 + 71, var7 + 17, 0.45F);
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, 0.0F, 1.0F);
         this.blit(var6 + 66, var7 + 12, 0, this.imageHeight, 66, 66);
         GlStateManager.popMatrix();
      } else {
         this.blit(var6 + 67, var7 + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(var1, var6 + 71, var7 + 17, 0.45F);
      }

   }

   private void renderMap(@Nullable MapItemSavedData var1, int var2, int var3, float var4) {
      if (var1 != null) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2, (float)var3, 1.0F);
         GlStateManager.scalef(var4, var4, 1.0F);
         this.minecraft.gameRenderer.getMapRenderer().render(var1, true);
         GlStateManager.popMatrix();
      }

   }
}
