package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityWithBoundingBoxRenderState;
import net.minecraft.client.renderer.blockentity.state.TestInstanceRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.TextGizmo;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class TestInstanceRenderer implements BlockEntityRenderer<TestInstanceBlockEntity, TestInstanceRenderState> {
   private static final float ERROR_PADDING = 0.02F;
   private final BeaconRenderer<TestInstanceBlockEntity> beacon = new BeaconRenderer<TestInstanceBlockEntity>();
   private final BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity> box = new BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity>();

   public TestInstanceRenderer() {
      super();
   }

   public TestInstanceRenderState createRenderState() {
      return new TestInstanceRenderState();
   }

   public void extractRenderState(TestInstanceBlockEntity var1, TestInstanceRenderState var2, float var3, Vec3 var4, ModelFeatureRenderer.@Nullable CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.beaconRenderState = new BeaconRenderState();
      BlockEntityRenderState.extractBase(var1, var2.beaconRenderState, var5);
      BeaconRenderer.extract(var1, var2.beaconRenderState, var3, var4);
      var2.blockEntityWithBoundingBoxRenderState = new BlockEntityWithBoundingBoxRenderState();
      BlockEntityRenderState.extractBase(var1, var2.blockEntityWithBoundingBoxRenderState, var5);
      BlockEntityWithBoundingBoxRenderer.extract(var1, var2.blockEntityWithBoundingBoxRenderState);
      var2.errorMarkers.clear();

      for(TestInstanceBlockEntity.ErrorMarker var7 : var1.getErrorMarkers()) {
         var2.errorMarkers.add(new TestInstanceBlockEntity.ErrorMarker(var7.pos(), var7.text()));
      }

   }

   public void submit(TestInstanceRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      this.beacon.submit(var1.beaconRenderState, var2, var3, var4);
      this.box.submit(var1.blockEntityWithBoundingBoxRenderState, var2, var3, var4);

      for(TestInstanceBlockEntity.ErrorMarker var6 : var1.errorMarkers) {
         this.submitErrorMarker(var6);
      }

   }

   private void submitErrorMarker(TestInstanceBlockEntity.ErrorMarker var1) {
      BlockPos var2 = var1.pos();
      Gizmos.cuboid((new AABB(var2)).inflate(0.019999999552965164), GizmoStyle.fill(ARGB.colorFromFloat(0.375F, 1.0F, 0.0F, 0.0F)));
      String var3 = var1.text().getString();
      float var4 = 0.16F;
      Gizmos.billboardText(var3, Vec3.atLowerCornerWithOffset(var2, 0.5, 1.2, 0.5), TextGizmo.Style.whiteAndCentered().withScale(0.16F)).setAlwaysOnTop();
   }

   public boolean shouldRenderOffScreen() {
      return this.beacon.shouldRenderOffScreen() || this.box.shouldRenderOffScreen();
   }

   public int getViewDistance() {
      return Math.max(this.beacon.getViewDistance(), this.box.getViewDistance());
   }

   public boolean shouldRender(TestInstanceBlockEntity var1, Vec3 var2) {
      return this.beacon.shouldRender(var1, var2) || this.box.shouldRender(var1, var2);
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
