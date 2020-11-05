package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.CartographyTableMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public class CartographyTableScreen extends AbstractContainerScreen<CartographyTableMenu> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");

   public CartographyTableScreen(CartographyTableMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.titleLabelY -= 2;
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   protected void renderBg(PoseStack var1, float var2, int var3, int var4) {
      this.renderBackground(var1);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int var5 = this.leftPos;
      int var6 = this.topPos;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      ItemStack var7 = ((CartographyTableMenu)this.menu).getSlot(1).getItem();
      boolean var8 = var7.is(Items.MAP);
      boolean var9 = var7.is(Items.PAPER);
      boolean var10 = var7.is(Items.GLASS_PANE);
      ItemStack var11 = ((CartographyTableMenu)this.menu).getSlot(0).getItem();
      boolean var13 = false;
      MapItemSavedData var12;
      if (var11.is(Items.FILLED_MAP)) {
         var12 = MapItem.getSavedData(var11, this.minecraft.level);
         if (var12 != null) {
            if (var12.locked) {
               var13 = true;
               if (var9 || var10) {
                  this.blit(var1, var5 + 35, var6 + 31, this.imageWidth + 50, 132, 28, 21);
               }
            }

            if (var9 && var12.scale >= 4) {
               var13 = true;
               this.blit(var1, var5 + 35, var6 + 31, this.imageWidth + 50, 132, 28, 21);
            }
         }
      } else {
         var12 = null;
      }

      this.renderResultingMap(var1, var12, var8, var9, var10, var13);
   }

   private void renderResultingMap(PoseStack var1, @Nullable MapItemSavedData var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      int var7 = this.leftPos;
      int var8 = this.topPos;
      if (var4 && !var6) {
         this.blit(var1, var7 + 67, var8 + 13, this.imageWidth, 66, 66, 66);
         this.renderMap(var2, var7 + 85, var8 + 31, 0.226F);
      } else if (var3) {
         this.blit(var1, var7 + 67 + 16, var8 + 13, this.imageWidth, 132, 50, 66);
         this.renderMap(var2, var7 + 86, var8 + 16, 0.34F);
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 1.0F);
         this.blit(var1, var7 + 67, var8 + 13 + 16, this.imageWidth, 132, 50, 66);
         this.renderMap(var2, var7 + 70, var8 + 32, 0.34F);
         RenderSystem.popMatrix();
      } else if (var5) {
         this.blit(var1, var7 + 67, var8 + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(var2, var7 + 71, var8 + 17, 0.45F);
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 1.0F);
         this.blit(var1, var7 + 66, var8 + 12, 0, this.imageHeight, 66, 66);
         RenderSystem.popMatrix();
      } else {
         this.blit(var1, var7 + 67, var8 + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(var2, var7 + 71, var8 + 17, 0.45F);
      }

   }

   private void renderMap(@Nullable MapItemSavedData var1, int var2, int var3, float var4) {
      if (var1 != null) {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)var2, (float)var3, 1.0F);
         RenderSystem.scalef(var4, var4, 1.0F);
         MultiBufferSource.BufferSource var5 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         this.minecraft.gameRenderer.getMapRenderer().render(new PoseStack(), var5, var1, true, 15728880);
         var5.endBatch();
         RenderSystem.popMatrix();
      }

   }
}
