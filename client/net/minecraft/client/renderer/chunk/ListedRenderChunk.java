package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.world.World;

public class ListedRenderChunk extends RenderChunk {
   private final int field_178601_d = GLAllocation.func_74526_a(BlockRenderLayer.values().length);

   public ListedRenderChunk(World var1, WorldRenderer var2) {
      super(var1, var2);
   }

   public int func_178600_a(BlockRenderLayer var1, CompiledChunk var2) {
      return !var2.func_178491_b(var1) ? this.field_178601_d + var1.ordinal() : -1;
   }

   public void func_178566_a() {
      super.func_178566_a();
      GLAllocation.func_178874_a(this.field_178601_d, BlockRenderLayer.values().length);
   }
}
