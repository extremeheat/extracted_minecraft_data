package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;

public class ChunkRenderTask implements Comparable<ChunkRenderTask> {
   private final RenderChunk field_178553_a;
   private final ReentrantLock field_178551_b = new ReentrantLock();
   private final List<Runnable> field_178552_c = Lists.newArrayList();
   private final ChunkRenderTask.Type field_178549_d;
   private final double field_188229_e;
   private RegionRenderCacheBuilder field_178550_e;
   private CompiledChunk field_178547_f;
   private ChunkRenderTask.Status field_178548_g;
   private boolean field_178554_h;

   public ChunkRenderTask(RenderChunk var1, ChunkRenderTask.Type var2, double var3) {
      super();
      this.field_178548_g = ChunkRenderTask.Status.PENDING;
      this.field_178553_a = var1;
      this.field_178549_d = var2;
      this.field_188229_e = var3;
   }

   public ChunkRenderTask.Status func_178546_a() {
      return this.field_178548_g;
   }

   public RenderChunk func_178536_b() {
      return this.field_178553_a;
   }

   public CompiledChunk func_178544_c() {
      return this.field_178547_f;
   }

   public void func_178543_a(CompiledChunk var1) {
      this.field_178547_f = var1;
   }

   public RegionRenderCacheBuilder func_178545_d() {
      return this.field_178550_e;
   }

   public void func_178541_a(RegionRenderCacheBuilder var1) {
      this.field_178550_e = var1;
   }

   public void func_178535_a(ChunkRenderTask.Status var1) {
      this.field_178551_b.lock();

      try {
         this.field_178548_g = var1;
      } finally {
         this.field_178551_b.unlock();
      }

   }

   public void func_178542_e() {
      this.field_178551_b.lock();

      try {
         if (this.field_178549_d == ChunkRenderTask.Type.REBUILD_CHUNK && this.field_178548_g != ChunkRenderTask.Status.DONE) {
            this.field_178553_a.func_178575_a(false);
         }

         this.field_178554_h = true;
         this.field_178548_g = ChunkRenderTask.Status.DONE;
         Iterator var1 = this.field_178552_c.iterator();

         while(var1.hasNext()) {
            Runnable var2 = (Runnable)var1.next();
            var2.run();
         }
      } finally {
         this.field_178551_b.unlock();
      }

   }

   public void func_178539_a(Runnable var1) {
      this.field_178551_b.lock();

      try {
         this.field_178552_c.add(var1);
         if (this.field_178554_h) {
            var1.run();
         }
      } finally {
         this.field_178551_b.unlock();
      }

   }

   public ReentrantLock func_178540_f() {
      return this.field_178551_b;
   }

   public ChunkRenderTask.Type func_178538_g() {
      return this.field_178549_d;
   }

   public boolean func_178537_h() {
      return this.field_178554_h;
   }

   public int compareTo(ChunkRenderTask var1) {
      return Doubles.compare(this.field_188229_e, var1.field_188229_e);
   }

   public double func_188228_i() {
      return this.field_188229_e;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((ChunkRenderTask)var1);
   }

   public static enum Status {
      PENDING,
      COMPILING,
      UPLOADING,
      DONE;

      private Status() {
      }
   }

   public static enum Type {
      REBUILD_CHUNK,
      RESORT_TRANSPARENCY;

      private Type() {
      }
   }
}
