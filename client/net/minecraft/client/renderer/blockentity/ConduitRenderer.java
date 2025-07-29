package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
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

   public void render(ConduitBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      float var8 = (float)var1.tickCount + var2;
      if (!var1.isActive()) {
         float var17 = var1.getActiveRotation(0.0F);
         VertexConsumer var19 = SHELL_TEXTURE.buffer(this.materials, var4, RenderType::entitySolid);
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.mulPose((Quaternionfc)(new Quaternionf()).rotationY(var17 * 0.017453292F));
         this.shell.render(var3, var19, var5, var6);
         var3.popPose();
      } else {
         float var9 = var1.getActiveRotation(var2) * 57.295776F;
         float var10 = Mth.sin(var8 * 0.1F) / 2.0F + 0.5F;
         var10 = var10 * var10 + var10;
         var3.pushPose();
         var3.translate(0.5F, 0.3F + var10 * 0.2F, 0.5F);
         Vector3f var11 = (new Vector3f(0.5F, 1.0F, 0.5F)).normalize();
         var3.mulPose((Quaternionfc)(new Quaternionf()).rotationAxis(var9 * 0.017453292F, var11));
         this.cage.render(var3, ACTIVE_SHELL_TEXTURE.buffer(this.materials, var4, RenderType::entityCutoutNoCull), var5, var6);
         var3.popPose();
         int var12 = var1.tickCount / 66 % 3;
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         if (var12 == 1) {
            var3.mulPose((Quaternionfc)(new Quaternionf()).rotationX(1.5707964F));
         } else if (var12 == 2) {
            var3.mulPose((Quaternionfc)(new Quaternionf()).rotationZ(1.5707964F));
         }

         VertexConsumer var13 = (var12 == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(this.materials, var4, RenderType::entityCutoutNoCull);
         this.wind.render(var3, var13, var5, var6);
         var3.popPose();
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.scale(0.875F, 0.875F, 0.875F);
         var3.mulPose((Quaternionfc)(new Quaternionf()).rotationXYZ(3.1415927F, 0.0F, 3.1415927F));
         this.wind.render(var3, var13, var5, var6);
         var3.popPose();
         Camera var14 = this.renderer.camera;
         var3.pushPose();
         var3.translate(0.5F, 0.3F + var10 * 0.2F, 0.5F);
         var3.scale(0.5F, 0.5F, 0.5F);
         float var15 = -var14.getYRot();
         var3.mulPose((Quaternionfc)(new Quaternionf()).rotationYXZ(var15 * 0.017453292F, var14.getXRot() * 0.017453292F, 3.1415927F));
         float var16 = 1.3333334F;
         var3.scale(1.3333334F, 1.3333334F, 1.3333334F);
         this.eye.render(var3, (var1.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(this.materials, var4, RenderType::entityCutoutNoCull), var5, var6);
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
