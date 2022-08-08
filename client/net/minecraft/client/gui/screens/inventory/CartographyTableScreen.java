package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.GameRenderer;
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
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, BG_LOCATION);
      int var5 = this.leftPos;
      int var6 = this.topPos;
      this.blit(var1, var5, var6, 0, 0, this.imageWidth, this.imageHeight);
      ItemStack var7 = ((CartographyTableMenu)this.menu).getSlot(1).getItem();
      boolean var8 = var7.is(Items.MAP);
      boolean var9 = var7.is(Items.PAPER);
      boolean var10 = var7.is(Items.GLASS_PANE);
      ItemStack var11 = ((CartographyTableMenu)this.menu).getSlot(0).getItem();
      boolean var14 = false;
      Integer var12;
      MapItemSavedData var13;
      if (var11.is(Items.FILLED_MAP)) {
         var12 = MapItem.getMapId(var11);
         var13 = MapItem.getSavedData((Integer)var12, this.minecraft.level);
         if (var13 != null) {
            if (var13.locked) {
               var14 = true;
               if (var9 || var10) {
                  this.blit(var1, var5 + 35, var6 + 31, this.imageWidth + 50, 132, 28, 21);
               }
            }

            if (var9 && var13.scale >= 4) {
               var14 = true;
               this.blit(var1, var5 + 35, var6 + 31, this.imageWidth + 50, 132, 28, 21);
            }
         }
      } else {
         var12 = null;
         var13 = null;
      }

      this.renderResultingMap(var1, var12, var13, var8, var9, var10, var14);
   }

   private void renderResultingMap(PoseStack var1, @Nullable Integer var2, @Nullable MapItemSavedData var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      int var8 = this.leftPos;
      int var9 = this.topPos;
      if (var5 && !var7) {
         this.blit(var1, var8 + 67, var9 + 13, this.imageWidth, 66, 66, 66);
         this.renderMap(var1, var2, var3, var8 + 85, var9 + 31, 0.226F);
      } else if (var4) {
         this.blit(var1, var8 + 67 + 16, var9 + 13, this.imageWidth, 132, 50, 66);
         this.renderMap(var1, var2, var3, var8 + 86, var9 + 16, 0.34F);
         RenderSystem.setShaderTexture(0, BG_LOCATION);
         var1.pushPose();
         var1.translate(0.0, 0.0, 1.0);
         this.blit(var1, var8 + 67, var9 + 13 + 16, this.imageWidth, 132, 50, 66);
         this.renderMap(var1, var2, var3, var8 + 70, var9 + 32, 0.34F);
         var1.popPose();
      } else if (var6) {
         this.blit(var1, var8 + 67, var9 + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(var1, var2, var3, var8 + 71, var9 + 17, 0.45F);
         RenderSystem.setShaderTexture(0, BG_LOCATION);
         var1.pushPose();
         var1.translate(0.0, 0.0, 1.0);
         this.blit(var1, var8 + 66, var9 + 12, 0, this.imageHeight, 66, 66);
         var1.popPose();
      } else {
         this.blit(var1, var8 + 67, var9 + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(var1, var2, var3, var8 + 71, var9 + 17, 0.45F);
      }

   }

   private void renderMap(PoseStack var1, @Nullable Integer var2, @Nullable MapItemSavedData var3, int var4, int var5, float var6) {
      if (var2 != null && var3 != null) {
         var1.pushPose();
         var1.translate((double)var4, (double)var5, 1.0);
         var1.scale(var6, var6, 1.0F);
         MultiBufferSource.BufferSource var7 = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         this.minecraft.gameRenderer.getMapRenderer().render(var1, var7, var2, var3, true, 15728880);
         var7.endBatch();
         var1.popPose();
      }

   }
}
