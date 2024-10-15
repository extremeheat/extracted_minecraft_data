package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MapDecorationTextureManager;
import net.minecraft.client.resources.MapTextureManager;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Matrix4f;

public class MapRenderer {
   private static final float MAP_Z_OFFSET = -0.01F;
   private static final float DECORATION_Z_OFFSET = -0.001F;
   private static final int WIDTH = 128;
   private static final int HEIGHT = 128;
   private final MapTextureManager mapTextureManager;
   private final MapDecorationTextureManager decorationTextures;

   public MapRenderer(MapDecorationTextureManager var1, MapTextureManager var2) {
      super();
      this.decorationTextures = var1;
      this.mapTextureManager = var2;
   }

   public void render(MapRenderState var1, PoseStack var2, MultiBufferSource var3, boolean var4, int var5) {
      Matrix4f var6 = var2.last().pose();
      VertexConsumer var7 = var3.getBuffer(RenderType.text(var1.texture));
      var7.addVertex(var6, 0.0F, 128.0F, -0.01F).setColor(-1).setUv(0.0F, 1.0F).setLight(var5);
      var7.addVertex(var6, 128.0F, 128.0F, -0.01F).setColor(-1).setUv(1.0F, 1.0F).setLight(var5);
      var7.addVertex(var6, 128.0F, 0.0F, -0.01F).setColor(-1).setUv(1.0F, 0.0F).setLight(var5);
      var7.addVertex(var6, 0.0F, 0.0F, -0.01F).setColor(-1).setUv(0.0F, 0.0F).setLight(var5);
      int var8 = 0;

      for (MapRenderState.MapDecorationRenderState var10 : var1.decorations) {
         if (!var4 || var10.renderOnFrame) {
            var2.pushPose();
            var2.translate((float)var10.x / 2.0F + 64.0F, (float)var10.y / 2.0F + 64.0F, -0.02F);
            var2.mulPose(Axis.ZP.rotationDegrees((float)(var10.rot * 360) / 16.0F));
            var2.scale(4.0F, 4.0F, 3.0F);
            var2.translate(-0.125F, 0.125F, 0.0F);
            Matrix4f var11 = var2.last().pose();
            TextureAtlasSprite var12 = var10.atlasSprite;
            if (var12 != null) {
               VertexConsumer var13 = var3.getBuffer(RenderType.text(var12.atlasLocation()));
               var13.addVertex(var11, -1.0F, 1.0F, (float)var8 * -0.001F).setColor(-1).setUv(var12.getU0(), var12.getV0()).setLight(var5);
               var13.addVertex(var11, 1.0F, 1.0F, (float)var8 * -0.001F).setColor(-1).setUv(var12.getU1(), var12.getV0()).setLight(var5);
               var13.addVertex(var11, 1.0F, -1.0F, (float)var8 * -0.001F).setColor(-1).setUv(var12.getU1(), var12.getV1()).setLight(var5);
               var13.addVertex(var11, -1.0F, -1.0F, (float)var8 * -0.001F).setColor(-1).setUv(var12.getU0(), var12.getV1()).setLight(var5);
               var2.popPose();
            }

            if (var10.name != null) {
               Font var16 = Minecraft.getInstance().font;
               float var14 = (float)var16.width(var10.name);
               float var15 = Mth.clamp(25.0F / var14, 0.0F, 6.0F / 9.0F);
               var2.pushPose();
               var2.translate((float)var10.x / 2.0F + 64.0F - var14 * var15 / 2.0F, (float)var10.y / 2.0F + 64.0F + 4.0F, -0.025F);
               var2.scale(var15, var15, 1.0F);
               var2.translate(0.0F, 0.0F, -0.1F);
               var16.drawInBatch(var10.name, 0.0F, 0.0F, -1, false, var2.last().pose(), var3, Font.DisplayMode.NORMAL, -2147483648, var5, false);
               var2.popPose();
            }

            var8++;
         }
      }
   }

   public void extractRenderState(MapId var1, MapItemSavedData var2, MapRenderState var3) {
      var3.texture = this.mapTextureManager.prepareMapTexture(var1, var2);
      var3.decorations.clear();

      for (MapDecoration var5 : var2.getDecorations()) {
         var3.decorations.add(this.extractDecorationRenderState(var5));
      }
   }

   private MapRenderState.MapDecorationRenderState extractDecorationRenderState(MapDecoration var1) {
      MapRenderState.MapDecorationRenderState var2 = new MapRenderState.MapDecorationRenderState();
      var2.atlasSprite = this.decorationTextures.get(var1);
      var2.x = var1.x();
      var2.y = var1.y();
      var2.rot = var1.rot();
      var2.name = var1.name().orElse(null);
      var2.renderOnFrame = var1.renderOnFrame();
      return var2;
   }
}
