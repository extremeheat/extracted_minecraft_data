package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBeamOwner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class BeaconRenderer<T extends BlockEntity & BeaconBeamOwner> implements BlockEntityRenderer<T, BeaconRenderState> {
   public static final ResourceLocation BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/beacon_beam.png");
   public static final int MAX_RENDER_Y = 2048;
   private static final float BEAM_SCALE_THRESHOLD = 96.0F;
   public static final float SOLID_BEAM_RADIUS = 0.2F;
   public static final float BEAM_GLOW_RADIUS = 0.25F;

   public BeaconRenderer() {
      super();
   }

   public BeaconRenderState createRenderState() {
      return new BeaconRenderState();
   }

   public void extractRenderState(T var1, BeaconRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      extract(var1, var2, var3, var4);
   }

   public static <T extends BlockEntity & BeaconBeamOwner> void extract(T var0, BeaconRenderState var1, float var2, Vec3 var3) {
      var1.animationTime = var0.getLevel() != null ? (float)Math.floorMod(var0.getLevel().getGameTime(), 40) + var2 : 0.0F;
      var1.sections = ((BeaconBeamOwner)var0).getBeamSections().stream().map((var0x) -> new BeaconRenderState.Section(var0x.getColor(), var0x.getHeight())).toList();
      float var4 = (float)var3.subtract(var1.blockPos.getCenter()).horizontalDistance();
      LocalPlayer var5 = Minecraft.getInstance().player;
      var1.beamRadiusScale = var5 != null && var5.isScoping() ? 1.0F : Math.max(1.0F, var4 / 96.0F);
   }

   public void submit(BeaconRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      int var5 = 0;

      for(int var6 = 0; var6 < var1.sections.size(); ++var6) {
         BeaconRenderState.Section var7 = (BeaconRenderState.Section)var1.sections.get(var6);
         submitBeaconBeam(var2, var3, var1.beamRadiusScale, var1.animationTime, var5, var6 == var1.sections.size() - 1 ? 2048 : var7.height(), var7.color());
         var5 += var7.height();
      }

   }

   private static void submitBeaconBeam(PoseStack var0, SubmitNodeCollector var1, float var2, float var3, int var4, int var5, int var6) {
      submitBeaconBeam(var0, var1, BEAM_LOCATION, 1.0F, var3, var4, var5, var6, 0.2F * var2, 0.25F * var2);
   }

   public static void submitBeaconBeam(PoseStack var0, SubmitNodeCollector var1, ResourceLocation var2, float var3, float var4, int var5, int var6, int var7, float var8, float var9) {
      int var10 = var5 + var6;
      var0.pushPose();
      var0.translate(0.5, 0.0, 0.5);
      float var11 = var6 < 0 ? var4 : -var4;
      float var12 = Mth.frac(var11 * 0.2F - (float)Mth.floor(var11 * 0.1F));
      var0.pushPose();
      var0.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var4 * 2.25F - 45.0F));
      float var13 = 0.0F;
      float var16 = 0.0F;
      float var17 = -var8;
      float var18 = 0.0F;
      float var19 = 0.0F;
      float var20 = -var8;
      float var21 = 0.0F;
      float var22 = 1.0F;
      float var23 = -1.0F + var12;
      float var24 = (float)var6 * var3 * (0.5F / var8) + var23;
      var1.submitCustomGeometry(var0, RenderType.beaconBeam(var2, false), (var9x, var10x) -> renderPart(var9x, var10x, var7, var5, var10, 0.0F, var8, var8, 0.0F, var17, 0.0F, 0.0F, var20, 0.0F, 1.0F, var24, var23));
      var0.popPose();
      var13 = -var9;
      float var14 = -var9;
      var16 = -var9;
      var17 = -var9;
      var21 = 0.0F;
      var22 = 1.0F;
      var23 = -1.0F + var12;
      var24 = (float)var6 * var3 + var23;
      var1.submitCustomGeometry(var0, RenderType.beaconBeam(var2, true), (var13x, var14x) -> renderPart(var13x, var14x, ARGB.color(32, var7), var5, var10, var13, var14, var9, var16, var17, var9, var9, var9, 0.0F, 1.0F, var24, var23));
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

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
