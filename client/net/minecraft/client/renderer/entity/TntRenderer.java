package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.entity.state.TntRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.item.PrimedTnt;
import org.joml.Quaternionfc;

public class TntRenderer extends EntityRenderer<PrimedTnt, TntRenderState> {
   public static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
   private final BlockModelResolver blockModelResolver;

   public TntRenderer(final EntityRendererProvider.Context context) {
      super(context);
      this.shadowRadius = 0.5F;
      this.blockModelResolver = context.getBlockModelResolver();
   }

   public void submit(final TntRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      poseStack.pushPose();
      poseStack.translate(0.0F, 0.5F, 0.0F);
      float fuse = state.fuseRemainingInTicks;
      if (state.fuseRemainingInTicks < 10.0F) {
         float g = 1.0F - state.fuseRemainingInTicks / 10.0F;
         g = Mth.clamp(g, 0.0F, 1.0F);
         g *= g;
         g *= g;
         float s = 1.0F + g * 0.3F;
         poseStack.scale(s, s, s);
      }

      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-90.0F));
      poseStack.translate(-0.5F, -0.5F, 0.5F);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(90.0F));
      if (!state.blockState.isEmpty()) {
         TntMinecartRenderer.submitWhiteSolidBlock(state.blockState, poseStack, submitNodeCollector, state.lightCoords, (int)fuse / 5 % 2 == 0, state.outlineColor);
      }

      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   public TntRenderState createRenderState() {
      return new TntRenderState();
   }

   public void extractRenderState(final PrimedTnt entity, final TntRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.fuseRemainingInTicks = (float)entity.getFuse() - partialTicks + 1.0F;
      this.blockModelResolver.update(state.blockState, entity.getBlockState(), BLOCK_DISPLAY_CONTEXT);
   }
}
