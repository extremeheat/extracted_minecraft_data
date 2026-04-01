package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.VaultRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.vault.VaultBlockEntity;
import net.minecraft.world.level.block.entity.vault.VaultClientData;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public class VaultRenderer implements BlockEntityRenderer<VaultBlockEntity, VaultRenderState> {
   public static final float ITEM_BUNDLE_OFFSET_SCALE = 0.15F;
   public static final float FLAT_ITEM_DEPTH_THRESHOLD = 0.0625F;
   private final ItemModelResolver itemModelResolver;
   private final RandomSource random = RandomSource.create();

   public VaultRenderer(final BlockEntityRendererProvider.Context context) {
      super();
      this.itemModelResolver = context.itemModelResolver();
   }

   public static void renderMultipleFromCount(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final ItemClusterRenderState state, final RandomSource random) {
      AABB modelBoundingBox = state.item.getModelBoundingBox();
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

   public static void submitMultipleFromCount(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final ItemClusterRenderState state, final RandomSource random) {
      submitMultipleFromCount(poseStack, submitNodeCollector, lightCoords, state, random, state.item.getModelBoundingBox());
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

   public VaultRenderState createRenderState() {
      return new VaultRenderState();
   }

   public void extractRenderState(final VaultBlockEntity blockEntity, final VaultRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      ItemStack displayItem = blockEntity.getSharedData().getDisplayItem();
      if (VaultBlockEntity.Client.shouldDisplayActiveEffects(blockEntity.getSharedData()) && !displayItem.isEmpty() && blockEntity.getLevel() != null) {
         state.displayItem = new ItemClusterRenderState();
         this.itemModelResolver.updateForTopItem(state.displayItem.item, displayItem, ItemDisplayContext.GROUND, blockEntity.getLevel(), (ItemOwner)null, 0);
         state.displayItem.count = ItemClusterRenderState.getRenderedAmount(displayItem.getCount());
         state.displayItem.seed = ItemClusterRenderState.getSeedForItemStack(displayItem);
         VaultClientData clientData = blockEntity.getClientData();
         state.spin = Mth.rotLerp(partialTicks, clientData.previousSpin(), clientData.currentSpin());
      }
   }

   public void submit(final VaultRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      if (state.displayItem != null) {
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.4F, 0.5F);
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(state.spin));
         renderMultipleFromCount(poseStack, submitNodeCollector, state.lightCoords, state.displayItem, this.random);
         poseStack.popPose();
      }
   }
}
