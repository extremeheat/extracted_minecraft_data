package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class ShadowFeatureRenderer {
   private static final RenderType SHADOW_RENDER_TYPE = RenderTypes.entityShadow(ResourceLocation.withDefaultNamespace("textures/misc/shadow.png"));

   public ShadowFeatureRenderer() {
      super();
   }

   public void render(SubmitNodeCollection var1, MultiBufferSource.BufferSource var2) {
      VertexConsumer var3 = var2.getBuffer(SHADOW_RENDER_TYPE);

      for(SubmitNodeStorage.ShadowSubmit var5 : var1.getShadowSubmits()) {
         for(EntityRenderState.ShadowPiece var7 : var5.pieces()) {
            AABB var8 = var7.shapeBelow().bounds();
            float var9 = var7.relativeX() + (float)var8.minX;
            float var10 = var7.relativeX() + (float)var8.maxX;
            float var11 = var7.relativeY() + (float)var8.minY;
            float var12 = var7.relativeZ() + (float)var8.minZ;
            float var13 = var7.relativeZ() + (float)var8.maxZ;
            float var14 = var5.radius();
            float var15 = -var9 / 2.0F / var14 + 0.5F;
            float var16 = -var10 / 2.0F / var14 + 0.5F;
            float var17 = -var12 / 2.0F / var14 + 0.5F;
            float var18 = -var13 / 2.0F / var14 + 0.5F;
            int var19 = ARGB.white(var7.alpha());
            shadowVertex(var5.pose(), var3, var19, var9, var11, var12, var15, var17);
            shadowVertex(var5.pose(), var3, var19, var9, var11, var13, var15, var18);
            shadowVertex(var5.pose(), var3, var19, var10, var11, var13, var16, var18);
            shadowVertex(var5.pose(), var3, var19, var10, var11, var12, var16, var17);
         }
      }

   }

   private static void shadowVertex(Matrix4f var0, VertexConsumer var1, int var2, float var3, float var4, float var5, float var6, float var7) {
      Vector3f var8 = var0.transformPosition(var3, var4, var5, new Vector3f());
      var1.addVertex(var8.x(), var8.y(), var8.z(), var2, var6, var7, OverlayTexture.NO_OVERLAY, 15728880, 0.0F, 1.0F, 0.0F);
   }
}
