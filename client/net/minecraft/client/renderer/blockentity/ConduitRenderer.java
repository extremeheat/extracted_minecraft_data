package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
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

public class ConduitRenderer implements BlockEntityRenderer<ConduitBlockEntity> {
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
   private final BlockEntityRenderDispatcher renderer;

   public ConduitRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.materials = var1.materials();
      this.renderer = var1.blockEntityRenderDispatcher();
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

   public void submit(ConduitBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      float var9 = (float)var1.tickCount + var2;
      if (!var1.isActive()) {
         float var21 = var1.getActiveRotation(0.0F);
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.mulPose((Quaternionfc)(new Quaternionf()).rotationY(var21 * 0.017453292F));
         var8.submitModelPart(this.shell, var3, SHELL_TEXTURE.renderType(RenderType::entitySolid), var4, var5, this.materials.get(SHELL_TEXTURE));
         var3.popPose();
      } else {
         float var10 = var1.getActiveRotation(var2) * 57.295776F;
         float var11 = Mth.sin(var9 * 0.1F) / 2.0F + 0.5F;
         var11 = var11 * var11 + var11;
         var3.pushPose();
         var3.translate(0.5F, 0.3F + var11 * 0.2F, 0.5F);
         Vector3f var12 = (new Vector3f(0.5F, 1.0F, 0.5F)).normalize();
         var3.mulPose((Quaternionfc)(new Quaternionf()).rotationAxis(var10 * 0.017453292F, var12));
         var8.submitModelPart(this.cage, var3, ACTIVE_SHELL_TEXTURE.renderType(RenderType::entityCutoutNoCull), var4, var5, this.materials.get(ACTIVE_SHELL_TEXTURE));
         var3.popPose();
         int var13 = var1.tickCount / 66 % 3;
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         if (var13 == 1) {
            var3.mulPose((Quaternionfc)(new Quaternionf()).rotationX(1.5707964F));
         } else if (var13 == 2) {
            var3.mulPose((Quaternionfc)(new Quaternionf()).rotationZ(1.5707964F));
         }

         Material var14 = var13 == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE;
         RenderType var15 = var14.renderType(RenderType::entityCutoutNoCull);
         TextureAtlasSprite var16 = this.materials.get(var14);
         var8.submitModelPart(this.wind, var3, var15, var4, var5, var16);
         var3.popPose();
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.scale(0.875F, 0.875F, 0.875F);
         var3.mulPose((Quaternionfc)(new Quaternionf()).rotationXYZ(3.1415927F, 0.0F, 3.1415927F));
         var8.submitModelPart(this.wind, var3, var15, var4, var5, var16);
         var3.popPose();
         Camera var17 = this.renderer.camera;
         var3.pushPose();
         var3.translate(0.5F, 0.3F + var11 * 0.2F, 0.5F);
         var3.scale(0.5F, 0.5F, 0.5F);
         float var18 = -var17.getYRot();
         var3.mulPose((Quaternionfc)(new Quaternionf()).rotationYXZ(var18 * 0.017453292F, var17.getXRot() * 0.017453292F, 3.1415927F));
         float var19 = 1.3333334F;
         var3.scale(1.3333334F, 1.3333334F, 1.3333334F);
         Material var20 = var1.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE;
         var8.submitModelPart(this.eye, var3, var20.renderType(RenderType::entityCutoutNoCull), var4, var5, this.materials.get(var20));
         var3.popPose();
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
