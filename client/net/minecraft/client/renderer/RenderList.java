package net.minecraft.client.renderer;

import java.util.Iterator;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;

public class RenderList extends ChunkRenderContainer {
   public RenderList() {
      super();
   }

   public void func_178001_a(BlockRenderLayer var1) {
      if (this.field_178007_b) {
         Iterator var2 = this.field_178009_a.iterator();

         while(var2.hasNext()) {
            RenderChunk var3 = (RenderChunk)var2.next();
            ListedRenderChunk var4 = (ListedRenderChunk)var3;
            GlStateManager.func_179094_E();
            this.func_178003_a(var3);
            GlStateManager.func_179148_o(var4.func_178600_a(var1, var4.func_178571_g()));
            GlStateManager.func_179121_F();
         }

         GlStateManager.func_179117_G();
         this.field_178009_a.clear();
      }
   }
}
