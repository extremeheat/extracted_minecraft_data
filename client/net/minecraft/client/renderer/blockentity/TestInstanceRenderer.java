package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.phys.Vec3;

public class TestInstanceRenderer implements BlockEntityRenderer<TestInstanceBlockEntity> {
   private final BeaconRenderer<TestInstanceBlockEntity> beacon;
   private final BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity> box;

   public TestInstanceRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.beacon = new BeaconRenderer<TestInstanceBlockEntity>(var1);
      this.box = new BlockEntityWithBoundingBoxRenderer<TestInstanceBlockEntity>(var1);
   }

   public void submit(TestInstanceBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      this.beacon.submit(var1, var2, var3, var4, var5, var6, var7, var8);
      this.box.submit(var1, var2, var3, var4, var5, var6, var7, var8);
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
}
