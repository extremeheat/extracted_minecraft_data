package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityWithBoundingBoxRenderState;
import net.minecraft.client.renderer.blockentity.state.TestInstanceRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.phys.Vec3;

public class TestInstanceRenderer implements BlockEntityRenderer<TestInstanceBlockEntity, TestInstanceRenderState> {
   private final BeaconRenderer<TestInstanceBlockEntity> beacon = new BeaconRenderer<TestInstanceBlockEntity>();
   private final BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity> box = new BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity>();

   public TestInstanceRenderer() {
      super();
   }

   public TestInstanceRenderState createRenderState() {
      return new TestInstanceRenderState();
   }

   public void extractRenderState(TestInstanceBlockEntity var1, TestInstanceRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.beaconRenderState = new BeaconRenderState();
      BlockEntityRenderState.extractBase(var1, var2.beaconRenderState, var5);
      BeaconRenderer.extract(var1, var2.beaconRenderState, var3, var4);
      var2.blockEntityWithBoundingBoxRenderState = new BlockEntityWithBoundingBoxRenderState();
      BlockEntityRenderState.extractBase(var1, var2.blockEntityWithBoundingBoxRenderState, var5);
      BlockEntityWithBoundingBoxRenderer.extract(var1, var2.blockEntityWithBoundingBoxRenderState);
   }

   public void submit(TestInstanceRenderState var1, PoseStack var2, SubmitNodeCollector var3) {
      this.beacon.submit(var1.beaconRenderState, var2, var3);
      this.box.submit(var1.blockEntityWithBoundingBoxRenderState, var2, var3);
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
