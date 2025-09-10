package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BeaconRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityWithBoundingBoxRenderState;
import net.minecraft.client.renderer.blockentity.state.TestInstanceRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;

public class TestInstanceRenderer implements BlockEntityRenderer<TestInstanceBlockEntity, TestInstanceRenderState> {
   private static final float ERROR_PADDING = 0.02F;
   private final BeaconRenderer<TestInstanceBlockEntity> beacon = new BeaconRenderer<TestInstanceBlockEntity>();
   private final BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity> box = new BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity>();
   private final Font font;
   private final EntityRenderDispatcher entityRenderer;

   public TestInstanceRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.font = var1.font();
      this.entityRenderer = var1.entityRenderer();
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
      var2.errorMarkers.clear();

      for(TestInstanceBlockEntity.ErrorMarker var7 : var1.getErrorMarkers()) {
         var2.errorMarkers.add(new TestInstanceBlockEntity.ErrorMarker(var7.pos().subtract(var1.getBlockPos()), var7.text()));
      }

   }

   public void submit(TestInstanceRenderState var1, PoseStack var2, SubmitNodeCollector var3) {
      this.beacon.submit(var1.beaconRenderState, var2, var3);
      this.box.submit(var1.blockEntityWithBoundingBoxRenderState, var2, var3);

      for(TestInstanceBlockEntity.ErrorMarker var5 : var1.errorMarkers) {
         this.submitErrorMarker(var2, var3, var5);
      }

   }

   private void submitErrorMarker(PoseStack var1, SubmitNodeCollector var2, TestInstanceBlockEntity.ErrorMarker var3) {
      BlockPos var4 = var3.pos();
      var2.order(1).submitCustomGeometry(var1, RenderType.debugFilledBox(), (var1x, var2x) -> {
         float var3 = (float)var4.getX() - 0.02F;
         float var4x = (float)var4.getY() - 0.02F;
         float var5 = (float)var4.getZ() - 0.02F;
         float var6 = (float)var4.getX() + 1.0F + 0.02F;
         float var7 = (float)var4.getY() + 1.0F + 0.02F;
         float var8 = (float)var4.getZ() + 1.0F + 0.02F;
         PoseStack var9 = new PoseStack();
         var9.last().set(var1x);
         ShapeRenderer.addChainedFilledBoxVertices(var9, var2x, var3, var4x, var5, var6, var7, var8, 1.0F, 0.0F, 0.0F, 0.375F);
      });
      FormattedCharSequence var5 = var3.text().getVisualOrderText();
      int var6 = this.font.width(var5);
      float var7 = 0.01F;
      var1.pushPose();
      var1.translate((float)var4.getX() + 0.5F, (float)var4.getY() + 1.2F, (float)var4.getZ() + 0.5F);
      var1.mulPose((Quaternionfc)this.entityRenderer.cameraOrientation());
      var1.scale(0.01F, -0.01F, 0.01F);
      var2.order(2).submitText(var1, (float)(-var6) / 2.0F, 0.0F, var5, false, Font.DisplayMode.SEE_THROUGH, 15728880, -1, 0, 0);
      var1.popPose();
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
