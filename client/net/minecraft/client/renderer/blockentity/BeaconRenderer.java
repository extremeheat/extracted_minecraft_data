package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;

public class BeaconRenderer implements BlockEntityRenderer<BeaconBlockEntity> {
   public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
   public static final int MAX_RENDER_Y = 1024;

   public BeaconRenderer(BlockEntityRendererProvider.Context var1) {
      super();
   }

   public void render(BeaconBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      long var7 = var1.getLevel().getGameTime();
      List var9 = var1.getBeamSections();
      int var10 = 0;

      for (int var11 = 0; var11 < var9.size(); var11++) {
         BeaconBlockEntity.BeaconBeamSection var12 = (BeaconBlockEntity.BeaconBeamSection)var9.get(var11);
         renderBeaconBeam(var3, var4, var2, var7, var10, var11 == var9.size() - 1 ? 1024 : var12.getHeight(), var12.getColor());
         var10 += var12.getHeight();
      }
   }

   private static void renderBeaconBeam(PoseStack var0, MultiBufferSource var1, float var2, long var3, int var5, int var6, float[] var7) {
      renderBeaconBeam(var0, var1, BEAM_LOCATION, var2, 1.0F, var3, var5, var6, var7, 0.2F, 0.25F);
   }

   public static void renderBeaconBeam(
      PoseStack var0,
      MultiBufferSource var1,
      ResourceLocation var2,
      float var3,
      float var4,
      long var5,
      int var7,
      int var8,
      float[] var9,
      float var10,
      float var11
   ) {
      int var12 = var7 + var8;
      var0.pushPose();
      var0.translate(0.5, 0.0, 0.5);
      float var13 = (float)Math.floorMod(var5, 40) + var3;
      float var14 = var8 < 0 ? var13 : -var13;
      float var15 = Mth.frac(var14 * 0.2F - (float)Mth.floor(var14 * 0.1F));
      float var16 = var9[0];
      float var17 = var9[1];
      float var18 = var9[2];
      var0.pushPose();
      var0.mulPose(Axis.YP.rotationDegrees(var13 * 2.25F - 45.0F));
      float var19 = 0.0F;
      float var22 = 0.0F;
      float var23 = -var10;
      float var24 = 0.0F;
      float var25 = 0.0F;
      float var26 = -var10;
      float var27 = 0.0F;
      float var28 = 1.0F;
      float var29 = -1.0F + var15;
      float var30 = (float)var8 * var4 * (0.5F / var10) + var29;
      renderPart(
         var0,
         var1.getBuffer(RenderType.beaconBeam(var2, false)),
         var16,
         var17,
         var18,
         1.0F,
         var7,
         var12,
         0.0F,
         var10,
         var10,
         0.0F,
         var23,
         0.0F,
         0.0F,
         var26,
         0.0F,
         1.0F,
         var30,
         var29
      );
      var0.popPose();
      var19 = -var11;
      float var20 = -var11;
      var22 = -var11;
      var23 = -var11;
      var27 = 0.0F;
      var28 = 1.0F;
      var29 = -1.0F + var15;
      var30 = (float)var8 * var4 + var29;
      renderPart(
         var0,
         var1.getBuffer(RenderType.beaconBeam(var2, true)),
         var16,
         var17,
         var18,
         0.125F,
         var7,
         var12,
         var19,
         var20,
         var11,
         var22,
         var23,
         var11,
         var11,
         var11,
         0.0F,
         1.0F,
         var30,
         var29
      );
      var0.popPose();
   }

   private static void renderPart(
      PoseStack var0,
      VertexConsumer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19
   ) {
      PoseStack.Pose var20 = var0.last();
      renderQuad(var20, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var16, var17, var18, var19);
      renderQuad(var20, var1, var2, var3, var4, var5, var6, var7, var14, var15, var12, var13, var16, var17, var18, var19);
      renderQuad(var20, var1, var2, var3, var4, var5, var6, var7, var10, var11, var14, var15, var16, var17, var18, var19);
      renderQuad(var20, var1, var2, var3, var4, var5, var6, var7, var12, var13, var8, var9, var16, var17, var18, var19);
   }

   private static void renderQuad(
      PoseStack.Pose var0,
      VertexConsumer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15
   ) {
      addVertex(var0, var1, var2, var3, var4, var5, var7, var8, var9, var13, var14);
      addVertex(var0, var1, var2, var3, var4, var5, var6, var8, var9, var13, var15);
      addVertex(var0, var1, var2, var3, var4, var5, var6, var10, var11, var12, var15);
      addVertex(var0, var1, var2, var3, var4, var5, var7, var10, var11, var12, var14);
   }

   private static void addVertex(
      PoseStack.Pose var0, VertexConsumer var1, float var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9, float var10
   ) {
      var1.vertex(var0, var7, (float)var6, var8)
         .color(var2, var3, var4, var5)
         .uv(var9, var10)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(15728880)
         .normal(var0, 0.0F, 1.0F, 0.0F)
         .endVertex();
   }

   public boolean shouldRenderOffScreen(BeaconBlockEntity var1) {
      return true;
   }

   @Override
   public int getViewDistance() {
      return 256;
   }

   public boolean shouldRender(BeaconBlockEntity var1, Vec3 var2) {
      return Vec3.atCenterOf(var1.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(var2.multiply(1.0, 0.0, 1.0), (double)this.getViewDistance());
   }
}
