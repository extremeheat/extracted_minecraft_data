package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;

public abstract class ChunkRenderContainer {
   private double field_178008_c;
   private double field_178005_d;
   private double field_178006_e;
   protected List<RenderChunk> field_178009_a = Lists.newArrayListWithCapacity(17424);
   protected boolean field_178007_b;

   public ChunkRenderContainer() {
      super();
   }

   public void func_178004_a(double var1, double var3, double var5) {
      this.field_178007_b = true;
      this.field_178009_a.clear();
      this.field_178008_c = var1;
      this.field_178005_d = var3;
      this.field_178006_e = var5;
   }

   public void func_178003_a(RenderChunk var1) {
      BlockPos var2 = var1.func_178568_j();
      GlStateManager.func_179109_b((float)((double)var2.func_177958_n() - this.field_178008_c), (float)((double)var2.func_177956_o() - this.field_178005_d), (float)((double)var2.func_177952_p() - this.field_178006_e));
   }

   public void func_178002_a(RenderChunk var1, BlockRenderLayer var2) {
      this.field_178009_a.add(var1);
   }

   public abstract void func_178001_a(BlockRenderLayer var1);
}
