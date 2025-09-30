package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.ModelBakery;
import org.joml.Quaternionf;

public class FlameFeatureRenderer {
   public FlameFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2, AtlasManager var3) {
      for(SubmitNodeStorage.FlameSubmit var5 : var1.getFlameSubmits()) {
         this.renderFlame(var5.pose(), var2, var5.entityRenderState(), var5.rotation(), var3);
      }

   }

   private void renderFlame(PoseStack.Pose var1, MultiBufferSource var2, EntityRenderState var3, Quaternionf var4, AtlasManager var5) {
      TextureAtlasSprite var6 = var5.get(ModelBakery.FIRE_0);
      TextureAtlasSprite var7 = var5.get(ModelBakery.FIRE_1);
      float var8 = var3.boundingBoxWidth * 1.4F;
      var1.scale(var8, var8, var8);
      float var9 = 0.5F;
      float var10 = 0.0F;
      float var11 = var3.boundingBoxHeight / var8;
      float var12 = 0.0F;
      var1.rotate(var4);
      var1.translate(0.0F, 0.0F, 0.3F - (float)((int)var11) * 0.02F);
      float var13 = 0.0F;
      int var14 = 0;

      for(VertexConsumer var15 = var2.getBuffer(Sheets.cutoutBlockSheet()); var11 > 0.0F; ++var14) {
         TextureAtlasSprite var16 = var14 % 2 == 0 ? var6 : var7;
         float var17 = var16.getU0();
         float var18 = var16.getV0();
         float var19 = var16.getU1();
         float var20 = var16.getV1();
         if (var14 / 2 % 2 == 0) {
            float var21 = var19;
            var19 = var17;
            var17 = var21;
         }

         fireVertex(var1, var15, -var9 - 0.0F, 0.0F - var12, var13, var19, var20);
         fireVertex(var1, var15, var9 - 0.0F, 0.0F - var12, var13, var17, var20);
         fireVertex(var1, var15, var9 - 0.0F, 1.4F - var12, var13, var17, var18);
         fireVertex(var1, var15, -var9 - 0.0F, 1.4F - var12, var13, var19, var18);
         var11 -= 0.45F;
         var12 -= 0.45F;
         var9 *= 0.9F;
         var13 -= 0.03F;
      }

   }

   private static void fireVertex(PoseStack.Pose var0, VertexConsumer var1, float var2, float var3, float var4, float var5, float var6) {
      var1.addVertex(var0, var2, var3, var4).setColor(-1).setUv(var5, var6).setUv1(0, 10).setLight(240).setNormal(var0, 0.0F, 1.0F, 0.0F);
   }
}
