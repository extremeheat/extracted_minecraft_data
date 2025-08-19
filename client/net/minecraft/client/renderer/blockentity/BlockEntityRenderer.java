package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public interface BlockEntityRenderer<T extends BlockEntity> {
   void submit(T var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8);

   default boolean shouldRenderOffScreen() {
      return false;
   }

   default int getViewDistance() {
      return 64;
   }

   default boolean shouldRender(T var1, Vec3 var2) {
      return Vec3.atCenterOf(var1.getBlockPos()).closerThan(var2, (double)this.getViewDistance());
   }
}
