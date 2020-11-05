package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;

public class ConduitRenderer extends BlockEntityRenderer<ConduitBlockEntity> {
   public static final Material SHELL_TEXTURE;
   public static final Material ACTIVE_SHELL_TEXTURE;
   public static final Material WIND_TEXTURE;
   public static final Material VERTICAL_WIND_TEXTURE;
   public static final Material OPEN_EYE_TEXTURE;
   public static final Material CLOSED_EYE_TEXTURE;
   private final ModelPart eye = new ModelPart(16, 16, 0, 0);
   private final ModelPart wind;
   private final ModelPart shell;
   private final ModelPart cage;

   public ConduitRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
      this.eye.addBox(-4.0F, -4.0F, 0.0F, 8.0F, 8.0F, 0.0F, 0.01F);
      this.wind = new ModelPart(64, 32, 0, 0);
      this.wind.addBox(-8.0F, -8.0F, -8.0F, 16.0F, 16.0F, 16.0F);
      this.shell = new ModelPart(32, 16, 0, 0);
      this.shell.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
      this.cage = new ModelPart(32, 16, 0, 0);
      this.cage.addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F);
   }

   public void render(ConduitBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      float var7 = (float)var1.tickCount + var2;
      float var8;
      if (!var1.isActive()) {
         var8 = var1.getActiveRotation(0.0F);
         VertexConsumer var16 = SHELL_TEXTURE.buffer(var4, RenderType::entitySolid);
         var3.pushPose();
         var3.translate(0.5D, 0.5D, 0.5D);
         var3.mulPose(Vector3f.YP.rotationDegrees(var8));
         this.shell.render(var3, var16, var5, var6);
         var3.popPose();
      } else {
         var8 = var1.getActiveRotation(var2) * 57.295776F;
         float var9 = Mth.sin(var7 * 0.1F) / 2.0F + 0.5F;
         var9 += var9 * var9;
         var3.pushPose();
         var3.translate(0.5D, (double)(0.3F + var9 * 0.2F), 0.5D);
         Vector3f var10 = new Vector3f(0.5F, 1.0F, 0.5F);
         var10.normalize();
         var3.mulPose(new Quaternion(var10, var8, true));
         this.cage.render(var3, ACTIVE_SHELL_TEXTURE.buffer(var4, RenderType::entityCutoutNoCull), var5, var6);
         var3.popPose();
         int var11 = var1.tickCount / 66 % 3;
         var3.pushPose();
         var3.translate(0.5D, 0.5D, 0.5D);
         if (var11 == 1) {
            var3.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         } else if (var11 == 2) {
            var3.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
         }

         VertexConsumer var12 = (var11 == 1 ? VERTICAL_WIND_TEXTURE : WIND_TEXTURE).buffer(var4, RenderType::entityCutoutNoCull);
         this.wind.render(var3, var12, var5, var6);
         var3.popPose();
         var3.pushPose();
         var3.translate(0.5D, 0.5D, 0.5D);
         var3.scale(0.875F, 0.875F, 0.875F);
         var3.mulPose(Vector3f.XP.rotationDegrees(180.0F));
         var3.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         this.wind.render(var3, var12, var5, var6);
         var3.popPose();
         Camera var13 = this.renderer.camera;
         var3.pushPose();
         var3.translate(0.5D, (double)(0.3F + var9 * 0.2F), 0.5D);
         var3.scale(0.5F, 0.5F, 0.5F);
         float var14 = -var13.getYRot();
         var3.mulPose(Vector3f.YP.rotationDegrees(var14));
         var3.mulPose(Vector3f.XP.rotationDegrees(var13.getXRot()));
         var3.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
         float var15 = 1.3333334F;
         var3.scale(1.3333334F, 1.3333334F, 1.3333334F);
         this.eye.render(var3, (var1.isHunting() ? OPEN_EYE_TEXTURE : CLOSED_EYE_TEXTURE).buffer(var4, RenderType::entityCutoutNoCull), var5, var6);
         var3.popPose();
      }
   }

   static {
      SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/base"));
      ACTIVE_SHELL_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/cage"));
      WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind"));
      VERTICAL_WIND_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/wind_vertical"));
      OPEN_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/open_eye"));
      CLOSED_EYE_TEXTURE = new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation("entity/conduit/closed_eye"));
   }
}
