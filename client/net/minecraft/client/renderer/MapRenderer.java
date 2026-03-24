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
import net.minecraft.client.resources.model.sprite.AtlasManager;
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

   public MapRenderer(final AtlasManager atlasManager, final MapTextureManager mapTextureManager) {
      super();
      this.decorationSprites = atlasManager.getAtlasOrThrow(AtlasIds.MAP_DECORATIONS);
      this.mapTextureManager = mapTextureManager;
   }

   public void render(final MapRenderState mapRenderState, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final boolean showOnlyFrame, final int lightCoords) {
      submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.text(mapRenderState.texture), (pose, buffer) -> {
         buffer.addVertex(pose, 0.0F, 128.0F, -0.01F).setColor(-1).setUv(0.0F, 1.0F).setLight(lightCoords);
         buffer.addVertex(pose, 128.0F, 128.0F, -0.01F).setColor(-1).setUv(1.0F, 1.0F).setLight(lightCoords);
         buffer.addVertex(pose, 128.0F, 0.0F, -0.01F).setColor(-1).setUv(1.0F, 0.0F).setLight(lightCoords);
         buffer.addVertex(pose, 0.0F, 0.0F, -0.01F).setColor(-1).setUv(0.0F, 0.0F).setLight(lightCoords);
      });
      int count = 0;

      for(MapRenderState.MapDecorationRenderState decoration : mapRenderState.decorations) {
         if (!showOnlyFrame || decoration.renderOnFrame) {
            poseStack.pushPose();
            poseStack.translate((float)decoration.x / 2.0F + 64.0F, (float)decoration.y / 2.0F + 64.0F, -0.02F);
            poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees((float)(decoration.rot * 360) / 16.0F));
            poseStack.scale(4.0F, 4.0F, 3.0F);
            poseStack.translate(-0.125F, 0.125F, 0.0F);
            TextureAtlasSprite atlasSprite = decoration.atlasSprite;
            if (atlasSprite != null) {
               float z = (float)count * -0.001F;
               submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.text(atlasSprite.atlasLocation()), (pose, buffer) -> {
                  buffer.addVertex(pose, -1.0F, 1.0F, z).setColor(-1).setUv(atlasSprite.getU0(), atlasSprite.getV0()).setLight(lightCoords);
                  buffer.addVertex(pose, 1.0F, 1.0F, z).setColor(-1).setUv(atlasSprite.getU1(), atlasSprite.getV0()).setLight(lightCoords);
                  buffer.addVertex(pose, 1.0F, -1.0F, z).setColor(-1).setUv(atlasSprite.getU1(), atlasSprite.getV1()).setLight(lightCoords);
                  buffer.addVertex(pose, -1.0F, -1.0F, z).setColor(-1).setUv(atlasSprite.getU0(), atlasSprite.getV1()).setLight(lightCoords);
               });
               poseStack.popPose();
            }

            if (decoration.name != null) {
               Font font = Minecraft.getInstance().font;
               float width = (float)font.width((FormattedText)decoration.name);
               float var10000 = 25.0F / width;
               Objects.requireNonNull(font);
               float scale = Mth.clamp(var10000, 0.0F, 6.0F / 9.0F);
               poseStack.pushPose();
               poseStack.translate((float)decoration.x / 2.0F + 64.0F - width * scale / 2.0F, (float)decoration.y / 2.0F + 64.0F + 4.0F, -0.025F);
               poseStack.scale(scale, scale, -1.0F);
               poseStack.translate(0.0F, 0.0F, 0.1F);
               submitNodeCollector.order(1).submitText(poseStack, 0.0F, 0.0F, decoration.name.getVisualOrderText(), false, Font.DisplayMode.NORMAL, lightCoords, -1, -2147483648, 0);
               poseStack.popPose();
            }

            ++count;
         }
      }

   }

   public void extractRenderState(final MapId mapId, final MapItemSavedData mapData, final MapRenderState mapRenderState) {
      mapRenderState.texture = this.mapTextureManager.prepareMapTexture(mapId, mapData);
      mapRenderState.decorations.clear();

      for(MapDecoration decoration : mapData.getDecorations()) {
         mapRenderState.decorations.add(this.extractDecorationRenderState(decoration));
      }

   }

   private MapRenderState.MapDecorationRenderState extractDecorationRenderState(final MapDecoration decoration) {
      MapRenderState.MapDecorationRenderState state = new MapRenderState.MapDecorationRenderState();
      state.atlasSprite = this.decorationSprites.getSprite(decoration.getSpriteLocation());
      state.x = decoration.x();
      state.y = decoration.y();
      state.rot = decoration.rot();
      state.name = (Component)decoration.name().orElse((Object)null);
      state.renderOnFrame = decoration.renderOnFrame();
      return state;
   }
}
