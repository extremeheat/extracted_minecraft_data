package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.HoveringItemRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.CraftingGrid;
import net.minecraft.world.phys.AABB;
import org.joml.Quaternionfc;

public class HoveringItemRenderer extends EntityRenderer<CraftingGrid.HoveringItem, HoveringItemRenderState> {
   private static final float ITEM_MIN_HOVER_HEIGHT = 0.1F;
   private static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
   private static final float FLAT_ITEM_DEPTH_THRESHOLD = 0.0625F;
   private static final float ITEM_RENDER_SCALE = 1.5F;
   private final ItemModelResolver itemModelResolver;
   private final RandomSource random = RandomSource.create();

   public HoveringItemRenderer(final EntityRendererProvider.Context context) {
      super(context);
      this.itemModelResolver = context.getItemModelResolver();
      this.shadowRadius = 0.15F;
      this.shadowStrength = 0.75F;
   }

   public HoveringItemRenderState createRenderState() {
      return new HoveringItemRenderState();
   }

   public void extractRenderState(final CraftingGrid.HoveringItem entity, final HoveringItemRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.extractItemGroupRenderState(entity, entity.getItem(), this.itemModelResolver);
      state.outlineColor = -4022942;
   }

   public void submit(final HoveringItemRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      if (!state.item.isEmpty()) {
         poseStack.pushPose();
         AABB boundingBox = state.item.getModelBoundingBox();
         float minOffsetY = -((float)boundingBox.minY) + 0.1F;
         float bob = Mth.sin((double)(state.ageInTicks / 10.0F)) * 0.1F + 0.1F;
         poseStack.translate(0.0F, bob + minOffsetY, 0.0F);
         poseStack.scale(1.5F, 1.5F, 1.5F);
         float spin = state.ageInTicks / 8.0F;
         poseStack.mulPose((Quaternionfc)Axis.YP.rotation(spin));
         submitMultipleFromCount(poseStack, submitNodeCollector, state.lightCoords, state, this.random, boundingBox);
         poseStack.popPose();
         super.submit(state, poseStack, submitNodeCollector, camera);
      }
   }

   public static void submitMultipleFromCount(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final ItemClusterRenderState state, final RandomSource random, final AABB modelBoundingBox) {
      int amount = state.count;
      if (amount != 0) {
         random.setSeed((long)state.seed);
         ItemStackRenderState item = state.item;
         float modelDepth = (float)modelBoundingBox.getZsize();
         if (modelDepth > 0.0625F) {
            item.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

            for(int i = 1; i < amount; ++i) {
               poseStack.pushPose();
               float xo = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float yo = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               float zo = (random.nextFloat() * 2.0F - 1.0F) * 0.15F;
               poseStack.translate(xo, yo, zo);
               item.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
               poseStack.popPose();
            }
         } else {
            float offsetZ = modelDepth * 1.5F;
            poseStack.translate(0.0F, 0.0F, -(offsetZ * (float)(amount - 1) / 2.0F));
            item.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.translate(0.0F, 0.0F, offsetZ);

            for(int i = 1; i < amount; ++i) {
               poseStack.pushPose();
               float xo = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               float yo = (random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
               poseStack.translate(xo, yo, 0.0F);
               item.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);
               poseStack.popPose();
               poseStack.translate(0.0F, 0.0F, offsetZ);
            }
         }

      }
   }
}
