package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;

public class EndCrystalRenderer extends EntityRenderer<EndCrystal> {
   private static final ResourceLocation END_CRYSTAL_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(END_CRYSTAL_LOCATION);
   private static final float SIN_45 = (float)Math.sin(0.7853981633974483);
   private static final String GLASS = "glass";
   private static final String BASE = "base";
   private final ModelPart cube;
   private final ModelPart glass;
   private final ModelPart base;

   public EndCrystalRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      ModelPart var2 = var1.bakeLayer(ModelLayers.END_CRYSTAL);
      this.glass = var2.getChild("glass");
      this.cube = var2.getChild("cube");
      this.base = var2.getChild("base");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("glass", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      var1.addOrReplaceChild("cube", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
      var1.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, 0.0F, -6.0F, 12.0F, 4.0F, 12.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 32);
   }

   public void render(EndCrystal var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      float var7 = getY(var1, var3);
      float var8 = ((float)var1.time + var3) * 3.0F;
      VertexConsumer var9 = var5.getBuffer(RENDER_TYPE);
      var4.pushPose();
      var4.scale(2.0F, 2.0F, 2.0F);
      var4.translate(0.0, -0.5, 0.0);
      int var10 = OverlayTexture.NO_OVERLAY;
      if (var1.showsBottom()) {
         this.base.render(var4, var9, var6, var10);
      }

      var4.mulPose(Vector3f.YP.rotationDegrees(var8));
      var4.translate(0.0, (double)(1.5F + var7 / 2.0F), 0.0);
      var4.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      this.glass.render(var4, var9, var6, var10);
      float var11 = 0.875F;
      var4.scale(0.875F, 0.875F, 0.875F);
      var4.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      var4.mulPose(Vector3f.YP.rotationDegrees(var8));
      this.glass.render(var4, var9, var6, var10);
      var4.scale(0.875F, 0.875F, 0.875F);
      var4.mulPose(new Quaternion(new Vector3f(SIN_45, 0.0F, SIN_45), 60.0F, true));
      var4.mulPose(Vector3f.YP.rotationDegrees(var8));
      this.cube.render(var4, var9, var6, var10);
      var4.popPose();
      var4.popPose();
      BlockPos var12 = var1.getBeamTarget();
      if (var12 != null) {
         float var13 = (float)var12.getX() + 0.5F;
         float var14 = (float)var12.getY() + 0.5F;
         float var15 = (float)var12.getZ() + 0.5F;
         float var16 = (float)((double)var13 - var1.getX());
         float var17 = (float)((double)var14 - var1.getY());
         float var18 = (float)((double)var15 - var1.getZ());
         var4.translate((double)var16, (double)var17, (double)var18);
         EnderDragonRenderer.renderCrystalBeams(-var16, -var17 + var7, -var18, var3, var1.time, var4, var5, var6);
      }

      super.render(var1, var2, var3, var4, var5, var6);
   }

   public static float getY(EndCrystal var0, float var1) {
      float var2 = (float)var0.time + var1;
      float var3 = Mth.sin(var2 * 0.2F) / 2.0F + 0.5F;
      var3 = (var3 * var3 + var3) * 0.4F;
      return var3 - 1.4F;
   }

   public ResourceLocation getTextureLocation(EndCrystal var1) {
      return END_CRYSTAL_LOCATION;
   }

   public boolean shouldRender(EndCrystal var1, Frustum var2, double var3, double var5, double var7) {
      return super.shouldRender(var1, var2, var3, var5, var7) || var1.getBeamTarget() != null;
   }
}
