package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;

public class ChunkCompileTaskGenerator {
   private final RenderChunk field_178553_a;
   private final ReentrantLock field_178551_b = new ReentrantLock();
   private final List<Runnable> field_178552_c = Lists.newArrayList();
   private final ChunkCompileTaskGenerator.Type field_178549_d;
   private RegionRenderCacheBuilder field_178550_e;
   private CompiledChunk field_178547_f;
   private ChunkCompileTaskGenerator.Status field_178548_g;
   private boolean field_178554_h;

   public ChunkCompileTaskGenerator(RenderChunk var1, ChunkCompileTaskGenerator.Type var2) {
      super();
      this.field_178548_g = ChunkCompileTaskGenerator.Status.PENDING;
      this.field_178553_a = var1;
      this.field_178549_d = var2;
   }

   public ChunkCompileTaskGenerator.Status func_178546_a() {
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

   public void func_178535_a(ChunkCompileTaskGenerator.Status var1) {
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
         if (this.field_178549_d == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK && this.field_178548_g != ChunkCompileTaskGenerator.Status.DONE) {
            this.field_178553_a.func_178575_a(true);
         }

         this.field_178554_h = true;
         this.field_178548_g = ChunkCompileTaskGenerator.Status.DONE;
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

   public ChunkCompileTaskGenerator.Type func_178538_g() {
      return this.field_178549_d;
   }

   public boolean func_178537_h() {
      return this.field_178554_h;
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
