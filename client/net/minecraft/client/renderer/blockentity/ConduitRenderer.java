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
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
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

   public ConduitRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.eye = var1.bakeLayer(ModelLayers.CONDUIT_EYE);
      this.wind = var1.bakeLayer(ModelLayers.CONDUIT_WIND);
      this.shell = var1.bakeLayer(ModelLayers.CONDUIT_SHELL);
      this.cage = var1.bakeLayer(ModelLayers.CONDUIT_CAGE);
   }

   public static LayerDefinition createEyeLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("eye", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, new CubeDeformation(0.01F)), PartPose.ZERO);
      return LayerDefinition.create(var0, 16, 16);
   }

   public static LayerDefinition createWindLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("wind", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 32);
   }

   public static LayerDefinition createShellLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 32, 16);
   }

   public static LayerDefinition createCageLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 32, 16);
   }

   public CondiutRenderState createRenderState() {
      return new CondiutRenderState();
   }

   public void extractRenderState(ConduitBlockEntity var1, CondiutRenderState var2, float var3, Vec3 var4, ModelFeatureRenderer.@Nullable CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.isActive = var1.isActive();
      var2.activeRotation = var1.getActiveRotation(var1.isActive() ? var3 : 0.0F);
      var2.animTime = (float)var1.tickCount + var3;
      var2.animationPhase = var1.tickCount / 66 % 3;
      var2.isHunting = var1.isHunting();
   }

   public void submit(CondiutRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      if (!var1.isActive) {
         var2.pushPose();
         var2.translate(0.5F, 0.5F, 0.5F);
         var2.mulPose((Quaternionfc)(new Quaternionf()).rotationY(var1.activeRotation * 0.017453292F));
         var3.submitModelPart(this.shell, var2, SHELL_TEXTURE.renderType(RenderTypes::entitySolid), var1.lightCoords, OverlayTexture.NO_OVERLAY, this.materials.get(SHELL_TEXTURE), -1, var1.breakProgress);
         var2.popPose();
      } else {
         float var5 = var1.activeRotation * 57.295776F;
         float var6 = Mth.sin(var1.animTime * 0.1F) / 2.0F + 0.5F;
         var6 = var6 * var6 + var6;
         var2.pushPose();
         var2.translate(0.5F, 0.3F + var6 * 0.2F, 0.5F);
         Vector3f var7 = (new Vector3f(0.5F, 1.0F, 0.5F)).normalize();
         var2.mulPose((Quaternionfc)(new Quaternionf()).rotationAxis(var5 * 0.017453292F, var7));
         var3.submitModelPart(this.cage, var2, ACTIVE_SHELL_TEXTURE.renderType(RenderTypes::entityCutoutNoCull), var1.lightCoords, OverlayTexture.NO_OVERLAY, this.materials.get(ACTIVE_SHELL_TEXTURE), -1, var1.breakProgress);
         var2.popPose();
         var2.pushPose();
         var2.translate(0.5F, 0.5F, 0.5F);
         if (var1.animationPhase == 1) {
            var2.mulPose((Quaternionfc)(new Quaternionf()).rotationX(1.5707964F));
         } else if (var1.animationPhase == 2) {
            var2.mulPose((Quaternionfc)(new Quaternionf()).rotationZ(1.5707964F));
         }

         Material var8 = var1.animationPhase == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE;
         RenderType var9 = var8.renderType(RenderTypes::entityCutoutNoCull);
         TextureAtlasSprite var10 = this.materials.get(var8);
         var3.submitModelPart(this.wind, var2, var9, var1.lightCoords, OverlayTexture.NO_OVERLAY, var10);
         var2.popPose();
         var2.pushPose();
         var2.translate(0.5F, 0.5F, 0.5F);
         var2.scale(0.875F, 0.875F, 0.875F);
         var2.mulPose((Quaternionfc)(new Quaternionf()).rotationXYZ(3.1415927F, 0.0F, 3.1415927F));
         var3.submitModelPart(this.wind, var2, var9, var1.lightCoords, OverlayTexture.NO_OVERLAY, var10);
         var2.popPose();
         var2.pushPose();
         var2.translate(0.5F, 0.3F + var6 * 0.2F, 0.5F);
         var2.scale(0.5F, 0.5F, 0.5F);
         var2.mulPose((Quaternionfc)var4.orientation);
         var2.mulPose((Quaternionfc)(new Quaternionf()).rotationZ(3.1415927F).rotateY(3.1415927F));
         float var11 = 1.3333334F;
         var2.scale(1.3333334F, 1.3333334F, 1.3333334F);
         Material var12 = var1.isHunting ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE;
         var3.submitModelPart(this.eye, var2, var12.renderType(RenderTypes::entityCutoutNoCull), var1.lightCoords, OverlayTexture.NO_OVERLAY, this.materials.get(var12));
         var2.popPose();
      }
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
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
