package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.MapRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MapTextureManager;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.data.AtlasIds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.Mth;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.joml.Quaternionfc;

public class MapRenderer {
   private static final float MAP_Z_OFFSET = -0.01F;
   private static final float DECORATION_Z_OFFSET = -0.001F;
   public static final int WIDTH = 128;
   public static final int HEIGHT = 128;
   private final TextureAtlas decorationSprites;
   private final MapTextureManager mapTextureManager;

   public MapRenderer(AtlasManager var1, MapTextureManager var2) {
      super();
      this.decorationSprites = var1.getAtlasOrThrow(AtlasIds.MAP_DECORATIONS);
      this.mapTextureManager = var2;
   }

   public void render(MapRenderState var1, PoseStack var2, SubmitNodeCollector var3, boolean var4, int var5) {
      var3.submitCustomGeometry(var2, RenderTypes.text(var1.texture), (var1x, var2x) -> {
         var2x.addVertex(var1x, 0.0F, 128.0F, -0.01F).setColor(-1).setUv(0.0F, 1.0F).setLight(var5);
         var2x.addVertex(var1x, 128.0F, 128.0F, -0.01F).setColor(-1).setUv(1.0F, 1.0F).setLight(var5);
         var2x.addVertex(var1x, 128.0F, 0.0F, -0.01F).setColor(-1).setUv(1.0F, 0.0F).setLight(var5);
         var2x.addVertex(var1x, 0.0F, 0.0F, -0.01F).setColor(-1).setUv(0.0F, 0.0F).setLight(var5);
      });
      int var6 = 0;

      for(MapRenderState.MapDecorationRenderState var8 : var1.decorations) {
         if (!var4 || var8.renderOnFrame) {
            var2.pushPose();
            var2.translate((float)var8.x / 2.0F + 64.0F, (float)var8.y / 2.0F + 64.0F, -0.02F);
            var2.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)(var8.rot * 360) / 16.0F));
            var2.scale(4.0F, 4.0F, 3.0F);
            var2.translate(-0.125F, 0.125F, 0.0F);
            TextureAtlasSprite var9 = var8.atlasSprite;
            if (var9 != null) {
               float var10 = (float)var6 * -0.001F;
               var3.submitCustomGeometry(var2, RenderTypes.text(var9.atlasLocation()), (var3x, var4x) -> {
                  var4x.addVertex(var3x, -1.0F, 1.0F, var10).setColor(-1).setUv(var9.getU0(), var9.getV0()).setLight(var5);
                  var4x.addVertex(var3x, 1.0F, 1.0F, var10).setColor(-1).setUv(var9.getU1(), var9.getV0()).setLight(var5);
                  var4x.addVertex(var3x, 1.0F, -1.0F, var10).setColor(-1).setUv(var9.getU1(), var9.getV1()).setLight(var5);
                  var4x.addVertex(var3x, -1.0F, -1.0F, var10).setColor(-1).setUv(var9.getU0(), var9.getV1()).setLight(var5);
               });
               var2.popPose();
            }

            if (var8.name != null) {
               Font var13 = Minecraft.getInstance().font;
               float var11 = (float)var13.width((FormattedText)var8.name);
               float var10000 = 25.0F / var11;
               Objects.requireNonNull(var13);
               float var12 = Mth.clamp(var10000, 0.0F, 6.0F / 9.0F);
               var2.pushPose();
               var2.translate((float)var8.x / 2.0F + 64.0F - var11 * var12 / 2.0F, (float)var8.y / 2.0F + 64.0F + 4.0F, -0.025F);
               var2.scale(var12, var12, -1.0F);
               var2.translate(0.0F, 0.0F, 0.1F);
               var3.order(1).submitText(var2, 0.0F, 0.0F, var8.name.getVisualOrderText(), false, Font.DisplayMode.NORMAL, var5, -1, -2147483648, 0);
               var2.popPose();
            }

            ++var6;
         }
      }

   }

   public void extractRenderState(MapId var1, MapItemSavedData var2, MapRenderState var3) {
      var3.texture = this.mapTextureManager.prepareMapTexture(var1, var2);
      var3.decorations.clear();

      for(MapDecoration var5 : var2.getDecorations()) {
         var3.decorations.add(this.extractDecorationRenderState(var5));
      }

   }

   private MapRenderState.MapDecorationRenderState extractDecorationRenderState(MapDecoration var1) {
      MapRenderState.MapDecorationRenderState var2 = new MapRenderState.MapDecorationRenderState();
      var2.atlasSprite = this.decorationSprites.getSprite(var1.getSpriteLocation());
      var2.x = var1.x();
      var2.y = var1.y();
      var2.rot = var1.rot();
      var2.name = (Component)var1.name().orElse((Object)null);
      var2.renderOnFrame = var1.renderOnFrame();
      return var2;
   }
}
