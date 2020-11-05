package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface BlockEntityRenderer<T extends BlockEntity> {
   void render(T var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6);

   default boolean shouldRenderOffScreen(T var1) {
      return false;
   }
}
