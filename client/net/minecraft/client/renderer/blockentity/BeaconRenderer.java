package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class BeaconRenderer<T extends BlockEntity & BeaconBeamOwner> implements BlockEntityRenderer<T> {
   public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");
   public static final int MAX_RENDER_Y = 2048;
   private static final float BEAM_SCALE_THRESHOLD = 96.0F;
   public static final float SOLID_BEAM_RADIUS = 0.2F;
   public static final float BEAM_GLOW_RADIUS = 0.25F;

   public BeaconRenderer(BlockEntityRendererProvider.Context var1) {
      super();
   }

   public void submit(T var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      long var9 = var1.getLevel().getGameTime();
      float var11 = (float)var6.subtract(var1.getBlockPos().getCenter()).horizontalDistance();
      LocalPlayer var12 = Minecraft.getInstance().player;
      float var13 = var12 != null && var12.isScoping() ? 1.0F : Math.max(1.0F, var11 / 96.0F);
      List var14 = ((BeaconBeamOwner)var1).getBeamSections();
      int var15 = 0;

      for(int var16 = 0; var16 < var14.size(); ++var16) {
         BeaconBeamOwner.Section var17 = (BeaconBeamOwner.Section)var14.get(var16);
         submitBeaconBeam(var3, var8, var2, var13, var9, var15, var16 == var14.size() - 1 ? 2048 : var17.getHeight(), var17.getColor());
         var15 += var17.getHeight();
      }

   }

   private static void submitBeaconBeam(PoseStack var0, SubmitNodeCollector var1, float var2, float var3, long var4, int var6, int var7, int var8) {
      submitBeaconBeam(var0, var1, BEAM_LOCATION, var2, 1.0F, var4, var6, var7, var8, 0.2F * var3, 0.25F * var3);
   }

   public static void submitBeaconBeam(PoseStack var0, SubmitNodeCollector var1, ResourceLocation var2, float var3, float var4, long var5, int var7, int var8, int var9, float var10, float var11) {
      int var12 = var7 + var8;
      var0.pushPose();
      var0.translate(0.5, 0.0, 0.5);
      float var13 = (float)Math.floorMod(var5, 40) + var3;
      float var14 = var8 < 0 ? var13 : -var13;
      float var15 = Mth.frac(var14 * 0.2F - (float)Mth.floor(var14 * 0.1F));
      var0.pushPose();
      var0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var13 * 2.25F - 45.0F));
      float var16 = 0.0F;
      float var19 = 0.0F;
      float var20 = -var10;
      float var21 = 0.0F;
      float var22 = 0.0F;
      float var23 = -var10;
      float var24 = 0.0F;
      float var25 = 1.0F;
      float var26 = -1.0F + var15;
      float var27 = (float)var8 * var4 * (0.5F / var10) + var26;
      var1.submitCustomGeometry(var0, RenderType.beaconBeam(var2, false), (var9x, var10x) -> renderPart(var9x, var10x, var9, var7, var12, 0.0F, var10, var10, 0.0F, var20, 0.0F, 0.0F, var23, 0.0F, 1.0F, var27, var26));
      var0.popPose();
      var16 = -var11;
      float var17 = -var11;
      var19 = -var11;
      var20 = -var11;
      var24 = 0.0F;
      var25 = 1.0F;
      var26 = -1.0F + var15;
      var27 = (float)var8 * var4 + var26;
      var1.submitCustomGeometry(var0, RenderType.beaconBeam(var2, true), (var13x, var14x) -> renderPart(var13x, var14x, ARGB.color(32, var9), var7, var12, var16, var17, var11, var19, var20, var11, var11, var11, 0.0F, 1.0F, var27, var26));
      var0.popPose();
   }

   private static void renderPart(PoseStack.Pose var0, VertexConsumer var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12, float var13, float var14, float var15, float var16) {
      renderQuad(var0, var1, var2, var3, var4, var5, var6, var7, var8, var13, var14, var15, var16);
      renderQuad(var0, var1, var2, var3, var4, var11, var12, var9, var10, var13, var14, var15, var16);
      renderQuad(var0, var1, var2, var3, var4, var7, var8, var11, var12, var13, var14, var15, var16);
      renderQuad(var0, var1, var2, var3, var4, var9, var10, var5, var6, var13, var14, var15, var16);
   }

   private static void renderQuad(PoseStack.Pose var0, VertexConsumer var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, float var12) {
      addVertex(var0, var1, var2, var4, var5, var6, var10, var11);
      addVertex(var0, var1, var2, var3, var5, var6, var10, var12);
      addVertex(var0, var1, var2, var3, var7, var8, var9, var12);
      addVertex(var0, var1, var2, var4, var7, var8, var9, var11);
   }

   private static void addVertex(PoseStack.Pose var0, VertexConsumer var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      var1.addVertex(var0, var4, (float)var3, var5).setColor(var2).setUv(var6, var7).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(var0, 0.0F, 1.0F, 0.0F);
   }

   public boolean shouldRenderOffScreen() {
      return true;
   }

   public int getViewDistance() {
      return Minecraft.getInstance().options.getEffectiveRenderDistance() * 16;
   }

   public boolean shouldRender(T var1, Vec3 var2) {
      return Vec3.atCenterOf(var1.getBlockPos()).multiply(1.0, 0.0, 1.0).closerThan(var2.multiply(1.0, 0.0, 1.0), (double)this.getViewDistance());
   }
}
