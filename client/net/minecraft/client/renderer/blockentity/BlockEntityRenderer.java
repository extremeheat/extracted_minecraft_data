package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public interface BlockEntityRenderer<T extends BlockEntity, S extends BlockEntityRenderState> {
   S createRenderState();

   default void extractRenderState(T var1, S var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderState.extractBase(var1, var2, var5);
   }

   void submit(S var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4);

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
