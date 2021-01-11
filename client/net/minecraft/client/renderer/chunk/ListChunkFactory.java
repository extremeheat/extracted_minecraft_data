package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ListChunkFactory implements IRenderChunkFactory {
   public ListChunkFactory() {
      super();
   }

   public RenderChunk func_178602_a(World var1, RenderGlobal var2, BlockPos var3, int var4) {
      return new ListedRenderChunk(var1, var2, var3, var4);
   }
}
