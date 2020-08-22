package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityRenderer {
   protected final BlockEntityRenderDispatcher renderer;

   public BlockEntityRenderer(BlockEntityRenderDispatcher var1) {
      this.renderer = var1;
   }

   public abstract void render(BlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6);

   public boolean shouldRenderOffScreen(BlockEntity var1) {
      return false;
   }
}
