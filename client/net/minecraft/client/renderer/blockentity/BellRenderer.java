package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.bell.BellModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BellRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.SpriteId;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class BellRenderer implements BlockEntityRenderer<BellBlockEntity, BellRenderState> {
   public static final SpriteId BELL_TEXTURE;
   private final SpriteGetter sprites;
   private final BellModel model;

   public BellRenderer(final BlockEntityRendererProvider.Context context) {
      super();
      this.sprites = context.sprites();
      this.model = new BellModel(context.bakeLayer(ModelLayers.BELL));
   }

   public BellRenderState createRenderState() {
      return new BellRenderState();
   }

   public void extractRenderState(final BellBlockEntity blockEntity, final BellRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.ticks = (float)blockEntity.ticks + partialTicks;
      state.shakeDirection = blockEntity.shaking ? blockEntity.clickDirection : null;
   }

   public void submit(final BellRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      BellModel.State modelState = new BellModel.State(state.ticks, state.shakeDirection);
      this.model.setupAnim(modelState);
      RenderType renderType = BELL_TEXTURE.renderType(RenderTypes::entitySolid);
      submitNodeCollector.submitModel(this.model, modelState, poseStack, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, -1, this.sprites.get(BELL_TEXTURE), 0, state.breakProgress);
   }

   static {
      BELL_TEXTURE = Sheets.BLOCK_ENTITIES_MAPPER.defaultNamespaceApply("bell/bell_body");
   }
}
