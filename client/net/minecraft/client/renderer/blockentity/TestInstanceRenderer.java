package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
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

   public void render(TestInstanceBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      this.beacon.render(var1, var2, var3, var4, var5, var6);
      this.box.render(var1, var2, var3, var4, var5, var6);
   }

   public boolean shouldRenderOffScreen(TestInstanceBlockEntity var1) {
      return this.beacon.shouldRenderOffScreen(var1) || this.box.shouldRenderOffScreen(var1);
   }

   public int getViewDistance() {
      return Math.max(this.beacon.getViewDistance(), this.box.getViewDistance());
   }

   public boolean shouldRender(TestInstanceBlockEntity var1, Vec3 var2) {
      return this.beacon.shouldRender(var1, var2) || this.box.shouldRender(var1, var2);
   }
}
