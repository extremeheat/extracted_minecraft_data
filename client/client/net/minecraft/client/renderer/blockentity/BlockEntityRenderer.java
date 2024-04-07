package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public interface BlockEntityRenderer<T extends BlockEntity> {
   void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6);

   default boolean shouldRenderOffScreen(T var1) {
      return false;
   }

   default int getViewDistance() {
      return 64;
   }

   default boolean shouldRender(T var1, Vec3 var2) {
      return Vec3.atCenterOf(var1.getBlockPos()).closerThan(var2, (double)this.getViewDistance());
   }
}
