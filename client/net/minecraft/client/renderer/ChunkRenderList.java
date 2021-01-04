package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockLayer;

public abstract class ChunkRenderList {
   private double xOff;
   private double yOff;
   private double zOff;
   protected final List<RenderChunk> chunks = Lists.newArrayListWithCapacity(17424);
   protected boolean ready;

   public ChunkRenderList() {
      super();
   }

   public void setCameraLocation(double var1, double var3, double var5) {
      this.ready = true;
      this.chunks.clear();
      this.xOff = var1;
      this.yOff = var3;
      this.zOff = var5;
   }

   public void translateToRelativeChunkPosition(RenderChunk var1) {
      BlockPos var2 = var1.getOrigin();
      GlStateManager.translatef((float)((double)var2.getX() - this.xOff), (float)((double)var2.getY() - this.yOff), (float)((double)var2.getZ() - this.zOff));
   }

   public void add(RenderChunk var1, BlockLayer var2) {
      this.chunks.add(var1);
   }

   public abstract void render(BlockLayer var1);
}
