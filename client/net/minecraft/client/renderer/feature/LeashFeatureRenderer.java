package net.minecraft.client.renderer.feature;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class LeashFeatureRenderer {
   private static final int LEASH_RENDER_STEPS = 24;
   private static final float LEASH_WIDTH = 0.05F;

   public LeashFeatureRenderer() {
      super();
   }

   public void renderSolid(final SubmitNodeCollection nodeCollection, final FeatureFrameContext context) {
      for(Submit submit : nodeCollection.getLeashSubmits()) {
         renderLeash(submit.pose(), context.bufferSource(), submit.leashState());
      }

   }

   private static void renderLeash(final Matrix4f pose, final MultiBufferSource bufferSource, final EntityRenderState.LeashState leashState) {
      float dx = (float)(leashState.end.x - leashState.start.x);
      float dy = (float)(leashState.end.y - leashState.start.y);
      float dz = (float)(leashState.end.z - leashState.start.z);
      float offsetFactor = Mth.invSqrt(dx * dx + dz * dz) * 0.05F / 2.0F;
      float dxOff = dz * offsetFactor;
      float dzOff = dx * offsetFactor;
      pose.translate((float)leashState.offset.x, (float)leashState.offset.y, (float)leashState.offset.z);
      VertexConsumer builder = bufferSource.getBuffer(RenderTypes.leash());

      for(int k = 0; k <= 24; ++k) {
         addVertexPair(builder, pose, dx, dy, dz, 0.05F, dxOff, dzOff, k, false, leashState);
      }

      for(int k = 24; k >= 0; --k) {
         addVertexPair(builder, pose, dx, dy, dz, 0.0F, dxOff, dzOff, k, true, leashState);
      }

   }

   private static void addVertexPair(final VertexConsumer builder, final Matrix4fc pose, final float dx, final float dy, final float dz, final float fudge, final float dxOff, final float dzOff, final int k, final boolean backwards, final EntityRenderState.LeashState state) {
      float progress = (float)k / 24.0F;
      int block = (int)Mth.lerp(progress, (float)state.startBlockLight, (float)state.endBlockLight);
      int sky = (int)Mth.lerp(progress, (float)state.startSkyLight, (float)state.endSkyLight);
      int lightCoords = LightCoordsUtil.pack(block, sky);
      float colorModifier = k % 2 == (backwards ? 1 : 0) ? 0.7F : 1.0F;
      float r = 0.5F * colorModifier;
      float g = 0.4F * colorModifier;
      float b = 0.3F * colorModifier;
      float x = dx * progress;
      float y;
      if (state.slack) {
         y = dy > 0.0F ? dy * progress * progress : dy - dy * (1.0F - progress) * (1.0F - progress);
      } else {
         y = dy * progress;
      }

      float z = dz * progress;
      builder.addVertex(pose, x - dxOff, y + fudge, z + dzOff).setColor(r, g, b, 1.0F).setLight(lightCoords);
      builder.addVertex(pose, x + dxOff, y + 0.05F - fudge, z - dzOff).setColor(r, g, b, 1.0F).setLight(lightCoords);
   }

   public static record Submit(Matrix4f pose, EntityRenderState.LeashState leashState) {
      public Submit {
         super();
      }
   }
}
