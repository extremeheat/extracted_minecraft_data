package net.minecraft.client.renderer.chunk;

import com.mojang.blaze3d.platform.MemoryTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.Level;

public class ListedRenderChunk extends RenderChunk {
   private final int listId = MemoryTracker.genLists(BlockLayer.values().length);

   public ListedRenderChunk(Level var1, LevelRenderer var2) {
      super(var1, var2);
   }

   public int getGlListId(BlockLayer var1, CompiledChunk var2) {
      return !var2.isEmpty(var1) ? this.listId + var1.ordinal() : -1;
   }

   public void releaseBuffers() {
      super.releaseBuffers();
      MemoryTracker.releaseLists(this.listId, BlockLayer.values().length);
   }
}
