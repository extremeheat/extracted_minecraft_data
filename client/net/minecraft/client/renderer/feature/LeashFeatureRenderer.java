package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class LeashFeatureRenderer {
   private static final int LEASH_RENDER_STEPS = 24;
   private static final float LEASH_WIDTH = 0.05F;

   public LeashFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2) {
      for(SubmitNodeStorage.LeashSubmit var4 : var1.getLeashSubmits()) {
         renderLeash(var4.pose(), var2, var4.leashState());
      }

   }

   private static void renderLeash(Matrix4f var0, MultiBufferSource var1, EntityRenderState.LeashState var2) {
      float var3 = (float)(var2.end.x - var2.start.x);
      float var4 = (float)(var2.end.y - var2.start.y);
      float var5 = (float)(var2.end.z - var2.start.z);
      float var6 = Mth.invSqrt(var3 * var3 + var5 * var5) * 0.05F / 2.0F;
      float var7 = var5 * var6;
      float var8 = var3 * var6;
      var0.translate((float)var2.offset.x, (float)var2.offset.y, (float)var2.offset.z);
      VertexConsumer var9 = var1.getBuffer(RenderTypes.leash());

      for(int var10 = 0; var10 <= 24; ++var10) {
         addVertexPair(var9, var0, var3, var4, var5, 0.05F, var7, var8, var10, false, var2);
      }

      for(int var11 = 24; var11 >= 0; --var11) {
         addVertexPair(var9, var0, var3, var4, var5, 0.0F, var7, var8, var11, true, var2);
      }

   }

   private static void addVertexPair(VertexConsumer var0, Matrix4f var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8, boolean var9, EntityRenderState.LeashState var10) {
      float var11 = (float)var8 / 24.0F;
      int var12 = (int)Mth.lerp(var11, (float)var10.startBlockLight, (float)var10.endBlockLight);
      int var13 = (int)Mth.lerp(var11, (float)var10.startSkyLight, (float)var10.endSkyLight);
      int var14 = LightTexture.pack(var12, var13);
      float var15 = var8 % 2 == (var9 ? 1 : 0) ? 0.7F : 1.0F;
      float var16 = 0.5F * var15;
      float var17 = 0.4F * var15;
      float var18 = 0.3F * var15;
      float var19 = var2 * var11;
      float var20;
      if (var10.slack) {
         var20 = var3 > 0.0F ? var3 * var11 * var11 : var3 - var3 * (1.0F - var11) * (1.0F - var11);
      } else {
         var20 = var3 * var11;
      }

      float var21 = var4 * var11;
      var0.addVertex((Matrix4fc)var1, var19 - var6, var20 + var5, var21 + var7).setColor(var16, var17, var18, 1.0F).setLight(var14);
      var0.addVertex((Matrix4fc)var1, var19 + var6, var20 + 0.05F - var5, var21 - var7).setColor(var16, var17, var18, 1.0F).setLight(var14);
   }
}
