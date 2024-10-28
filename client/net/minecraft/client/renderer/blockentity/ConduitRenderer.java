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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ConduitRenderer implements BlockEntityRenderer<ConduitBlockEntity> {
   public static final Material SHELL_TEXTURE;
   public static final Material ACTIVE_SHELL_TEXTURE;
   public static final Material WIND_TEXTURE;
   public static final Material VERTICAL_WIND_TEXTURE;
   public static final Material OPEN_EYE_TEXTURE;
   public static final Material CLOSED_EYE_TEXTURE;
   private final ModelPart eye;
   private final ModelPart wind;
   private final ModelPart shell;
   private final ModelPart cage;
   private final BlockEntityRenderDispatcher renderer;

   public ConduitRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.renderer = var1.getBlockEntityRenderDispatcher();
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

   public void render(ConduitBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      float var7 = (float)var1.tickCount + var2;
      float var8;
      if (!var1.isActive()) {
         var8 = var1.getActiveRotation(0.0F);
         VertexConsumer var16 = SHELL_TEXTURE.buffer(var4, RenderType::entitySolid);
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.mulPose((new Quaternionf()).rotationY(var8 * 0.017453292F));
         this.shell.render(var3, var16, var5, var6);
         var3.popPose();
      } else {
         var8 = var1.getActiveRotation(var2) * 57.295776F;
         float var9 = Mth.sin(var7 * 0.1F) / 2.0F + 0.5F;
         var9 += var9 * var9;
         var3.pushPose();
         var3.translate(0.5F, 0.3F + var9 * 0.2F, 0.5F);
         Vector3f var10 = (new Vector3f(0.5F, 1.0F, 0.5F)).normalize();
         var3.mulPose((new Quaternionf()).rotationAxis(var8 * 0.017453292F, var10));
         this.cage.render(var3, ACTIVE_SHELL_TEXTURE.buffer(var4, RenderType::entityCutoutNoCull), var5, var6);
         var3.popPose();
         int var11 = var1.tickCount / 66 % 3;
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         if (var11 == 1) {
            var3.mulPose((new Quaternionf()).rotationX(1.5707964F));
         } else if (var11 == 2) {
            var3.mulPose((new Quaternionf()).rotationZ(1.5707964F));
         }

         VertexConsumer var12 = (var11 == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(var4, RenderType::entityCutoutNoCull);
         this.wind.render(var3, var12, var5, var6);
         var3.popPose();
         var3.pushPose();
         var3.translate(0.5F, 0.5F, 0.5F);
         var3.scale(0.875F, 0.875F, 0.875F);
         var3.mulPose((new Quaternionf()).rotationXYZ(3.1415927F, 0.0F, 3.1415927F));
         this.wind.render(var3, var12, var5, var6);
         var3.popPose();
         Camera var13 = this.renderer.camera;
         var3.pushPose();
         var3.translate(0.5F, 0.3F + var9 * 0.2F, 0.5F);
         var3.scale(0.5F, 0.5F, 0.5F);
         float var14 = -var13.getYRot();
         var3.mulPose((new Quaternionf()).rotationYXZ(var14 * 0.017453292F, var13.getXRot() * 0.017453292F, 3.1415927F));
         float var15 = 1.3333334F;
         var3.scale(1.3333334F, 1.3333334F, 1.3333334F);
         this.eye.render(var3, (var1.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(var4, RenderType::entityCutoutNoCull), var5, var6);
         var3.popPose();
      }
   }

   static {
      SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/base"));
      ACTIVE_SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/cage"));
      WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/wind"));
      VERTICAL_WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/wind_vertical"));
      OPEN_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/open_eye"));
      CLOSED_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("entity/conduit/closed_eye"));
   }
}
