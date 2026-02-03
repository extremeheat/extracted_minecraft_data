package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.CondiutRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

public class ConduitRenderer implements BlockEntityRenderer<ConduitBlockEntity, CondiutRenderState> {
   public static final MaterialMapper MAPPER;
   public static final Material SHELL_TEXTURE;
   public static final Material ACTIVE_SHELL_TEXTURE;
   public static final Material WIND_TEXTURE;
   public static final Material VERTICAL_WIND_TEXTURE;
   public static final Material OPEN_EYE_TEXTURE;
   public static final Material CLOSED_EYE_TEXTURE;
   private final MaterialSet materials;
   private final ModelPart eye;
   private final ModelPart wind;
   private final ModelPart shell;
   private final ModelPart cage;

   public ConduitRenderer(final BlockEntityRendererProvider.Context context) {
      super();
      this.materials = context.materials();
      this.eye = context.bakeLayer(ModelLayers.CONDUIT_EYE);
      this.wind = context.bakeLayer(ModelLayers.CONDUIT_WIND);
      this.shell = context.bakeLayer(ModelLayers.CONDUIT_SHELL);
      this.cage = context.bakeLayer(ModelLayers.CONDUIT_CAGE);
   }

   public static LayerDefinition createEyeLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.ZERO);
      return LayerDefinition.create(mesh, 16, 16);
   }

   public static LayerDefinition createWindLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
      return LayerDefinition.create(mesh, 64, 32);
   }

   public static LayerDefinition createShellLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);
      return LayerDefinition.create(mesh, 32, 16);
   }

   public static LayerDefinition createCageLayer() {
      MeshDefinition mesh = new MeshDefinition();
      PartDefinition root = mesh.getRoot();
      root.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      return LayerDefinition.create(mesh, 32, 16);
   }

   public CondiutRenderState createRenderState() {
      return new CondiutRenderState();
   }

   public void extractRenderState(final ConduitBlockEntity blockEntity, final CondiutRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.isActive = blockEntity.isActive();
      state.activeRotation = blockEntity.getActiveRotation(blockEntity.isActive() ? partialTicks : 0.0F);
      state.animTime = (float)blockEntity.tickCount + partialTicks;
      state.animationPhase = blockEntity.tickCount / 66 % 3;
      state.isHunting = blockEntity.isHunting();
   }

   public void submit(final CondiutRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      if (!state.isActive) {
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.5F, 0.5F);
         poseStack.mulPose((Quaternionfc)(new Quaternionf()).rotationY(state.activeRotation * 0.017453292F));
         submitNodeCollector.submitModelPart(this.shell, poseStack, SHELL_TEXTURE.renderType(RenderTypes::entitySolid), state.lightCoords, OverlayTexture.NO_OVERLAY, this.materials.get(SHELL_TEXTURE), -1, state.breakProgress);
         poseStack.popPose();
      } else {
         float rotation = state.activeRotation * 57.295776F;
         float hh = Mth.sin((double)(state.animTime * 0.1F)) / 2.0F + 0.5F;
         hh = hh * hh + hh;
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.3F + hh * 0.2F, 0.5F);
         Vector3f axis = (new Vector3f(0.5F, 1.0F, 0.5F)).normalize();
         poseStack.mulPose((Quaternionfc)(new Quaternionf()).rotationAxis(rotation * 0.017453292F, axis));
         submitNodeCollector.submitModelPart(this.cage, poseStack, ACTIVE_SHELL_TEXTURE.renderType(RenderTypes::entityCutoutNoCull), state.lightCoords, OverlayTexture.NO_OVERLAY, this.materials.get(ACTIVE_SHELL_TEXTURE), -1, state.breakProgress);
         poseStack.popPose();
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.5F, 0.5F);
         if (state.animationPhase == 1) {
            poseStack.mulPose((Quaternionfc)(new Quaternionf()).rotationX(1.5707964F));
         } else if (state.animationPhase == 2) {
            poseStack.mulPose((Quaternionfc)(new Quaternionf()).rotationZ(1.5707964F));
         }

         Material windMaterial = state.animationPhase == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE;
         RenderType windRenderType = windMaterial.renderType(RenderTypes::entityCutoutNoCull);
         TextureAtlasSprite windSprite = this.materials.get(windMaterial);
         submitNodeCollector.submitModelPart(this.wind, poseStack, windRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, windSprite);
         poseStack.popPose();
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.5F, 0.5F);
         poseStack.scale(0.875F, 0.875F, 0.875F);
         poseStack.mulPose((Quaternionfc)(new Quaternionf()).rotationXYZ(3.1415927F, 0.0F, 3.1415927F));
         submitNodeCollector.submitModelPart(this.wind, poseStack, windRenderType, state.lightCoords, OverlayTexture.NO_OVERLAY, windSprite);
         poseStack.popPose();
         poseStack.pushPose();
         poseStack.translate(0.5F, 0.3F + hh * 0.2F, 0.5F);
         poseStack.scale(0.5F, 0.5F, 0.5F);
         poseStack.mulPose((Quaternionfc)camera.orientation);
         poseStack.mulPose((Quaternionfc)(new Quaternionf()).rotationZ(3.1415927F).rotateY(3.1415927F));
         float scale = 1.3333334F;
         poseStack.scale(1.3333334F, 1.3333334F, 1.3333334F);
         Material eyeMaterial = state.isHunting ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE;
         submitNodeCollector.submitModelPart(this.eye, poseStack, eyeMaterial.renderType(RenderTypes::entityCutoutNoCull), state.lightCoords, OverlayTexture.NO_OVERLAY, this.materials.get(eyeMaterial));
         poseStack.popPose();
      }
   }

   static {
      MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "entity/conduit");
      SHELL_TEXTURE = MAPPER.defaultNamespaceApply("base");
      ACTIVE_SHELL_TEXTURE = MAPPER.defaultNamespaceApply("cage");
      WIND_TEXTURE = MAPPER.defaultNamespaceApply("wind");
      VERTICAL_WIND_TEXTURE = MAPPER.defaultNamespaceApply("wind_vertical");
      OPEN_EYE_TEXTURE = MAPPER.defaultNamespaceApply("open_eye");
      CLOSED_EYE_TEXTURE = MAPPER.defaultNamespaceApply("closed_eye");
   }
}
