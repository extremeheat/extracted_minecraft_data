package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.ShulkerBoxRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class ShulkerBoxRenderer implements BlockEntityRenderer<ShulkerBoxBlockEntity, ShulkerBoxRenderState> {
   private final MaterialSet materials;
   private final ShulkerBoxModel model;

   public ShulkerBoxRenderer(final BlockEntityRendererProvider.Context context) {
      this(context.entityModelSet(), context.materials());
   }

   public ShulkerBoxRenderer(final SpecialModelRenderer.BakingContext context) {
      this(context.entityModelSet(), context.materials());
   }

   public ShulkerBoxRenderer(final EntityModelSet context, final MaterialSet materials) {
      super();
      this.materials = materials;
      this.model = new ShulkerBoxModel(context.bakeLayer(ModelLayers.SHULKER_BOX));
   }

   public ShulkerBoxRenderState createRenderState() {
      return new ShulkerBoxRenderState();
   }

   public void extractRenderState(final ShulkerBoxBlockEntity blockEntity, final ShulkerBoxRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.direction = (Direction)blockEntity.getBlockState().getValueOrElse(ShulkerBoxBlock.FACING, Direction.UP);
      state.color = blockEntity.getColor();
      state.progress = blockEntity.getProgress(partialTicks);
   }

   public void submit(final ShulkerBoxRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      DyeColor color = state.color;
      Material material;
      if (color == null) {
         material = Sheets.DEFAULT_SHULKER_TEXTURE_LOCATION;
      } else {
         material = Sheets.getShulkerBoxMaterial(color);
      }

      this.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.direction, state.progress, state.breakProgress, material, 0);
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final Direction direction, final float progress, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final Material material, final int outlineColor) {
      poseStack.pushPose();
      this.prepareModel(poseStack, direction, progress);
      ShulkerBoxModel var10001 = this.model;
      Float var10002 = progress;
      ShulkerBoxModel var10005 = this.model;
      Objects.requireNonNull(var10005);
      submitNodeCollector.submitModel(var10001, var10002, poseStack, material.renderType(var10005::renderType), lightCoords, overlayCoords, -1, this.materials.get(material), outlineColor, breakProgress);
      poseStack.popPose();
   }

   private void prepareModel(final PoseStack poseStack, final Direction direction, final float progress) {
      poseStack.translate(0.5F, 0.5F, 0.5F);
      float scale = 0.9995F;
      poseStack.scale(0.9995F, 0.9995F, 0.9995F);
      poseStack.mulPose((Quaternionfc)direction.getRotation());
      poseStack.scale(1.0F, -1.0F, -1.0F);
      poseStack.translate(0.0F, -1.0F, 0.0F);
      this.model.setupAnim(progress);
   }

   public void getExtents(final Direction direction, final float progress, final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      this.prepareModel(poseStack, direction, progress);
      this.model.root().getExtentsForGui(poseStack, output);
   }

   private static class ShulkerBoxModel extends Model<Float> {
      private final ModelPart lid;

      public ShulkerBoxModel(final ModelPart root) {
         super(root, RenderTypes::entityCutout);
         this.lid = root.getChild("lid");
      }

      public void setupAnim(final Float progress) {
         super.setupAnim(progress);
         this.lid.setPos(0.0F, 24.0F - progress * 0.5F * 16.0F, 0.0F);
         this.lid.yRot = 270.0F * progress * 0.017453292F;
      }
   }
}
