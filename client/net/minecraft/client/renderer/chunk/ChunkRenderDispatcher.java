package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ChunkRenderDispatcher {
   private static final Logger field_178523_a = LogManager.getLogger();
   private static final ThreadFactory field_178521_b = (new ThreadFactoryBuilder()).setNameFormat("Chunk Batcher %d").setDaemon(true).build();
   private final List<ChunkRenderWorker> field_178522_c = Lists.newArrayList();
   private final BlockingQueue<ChunkCompileTaskGenerator> field_178519_d = Queues.newArrayBlockingQueue(100);
   private final BlockingQueue<RegionRenderCacheBuilder> field_178520_e = Queues.newArrayBlockingQueue(5);
   private final WorldVertexBufferUploader field_178517_f = new WorldVertexBufferUploader();
   private final VertexBufferUploader field_178518_g = new VertexBufferUploader();
   private final Queue<ListenableFutureTask<?>> field_178524_h = Queues.newArrayDeque();
   private final ChunkRenderWorker field_178525_i;

   public ChunkRenderDispatcher() {
      super();

      int var1;
      for(var1 = 0; var1 < 2; ++var1) {
         ChunkRenderWorker var2 = new ChunkRenderWorker(this);
         Thread var3 = field_178521_b.newThread(var2);
         var3.start();
         this.field_178522_c.add(var2);
      }

      for(var1 = 0; var1 < 5; ++var1) {
         this.field_178520_e.add(new RegionRenderCacheBuilder());
      }

      this.field_178525_i = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
   }

   public String func_178504_a() {
      return String.format("pC: %03d, pU: %1d, aB: %1d", this.field_178519_d.size(), this.field_178524_h.size(), this.field_178520_e.size());
   }

   public boolean func_178516_a(long var1) {
      boolean var3 = false;

      long var5;
      do {
         boolean var4 = false;
         synchronized(this.field_178524_h) {
            if (!this.field_178524_h.isEmpty()) {
               ((ListenableFutureTask)this.field_178524_h.poll()).run();
               var4 = true;
               var3 = true;
            }
         }

         if (var1 == 0L || !var4) {
            break;
         }

         var5 = var1 - System.nanoTime();
      } while(var5 >= 0L);

      return var3;
   }

   public boolean func_178507_a(RenderChunk var1) {
      var1.func_178579_c().lock();

      boolean var4;
      try {
         final ChunkCompileTaskGenerator var2 = var1.func_178574_d();
         var2.func_178539_a(new Runnable() {
            public void run() {
               ChunkRenderDispatcher.this.field_178519_d.remove(var2);
            }
         });
         boolean var3 = this.field_178519_d.offer(var2);
         if (!var3) {
            var2.func_178542_e();
         }

         var4 = var3;
      } finally {
         var1.func_178579_c().unlock();
      }

      return var4;
   }

   public boolean func_178505_b(RenderChunk var1) {
      var1.func_178579_c().lock();

      boolean var3;
      try {
         ChunkCompileTaskGenerator var2 = var1.func_178574_d();

         try {
            this.field_178525_i.func_178474_a(var2);
         } catch (InterruptedException var7) {
         }

         var3 = true;
      } finally {
         var1.func_178579_c().unlock();
      }

      return var3;
   }

   public void func_178514_b() {
      this.func_178513_e();

      while(this.func_178516_a(0L)) {
      }

      ArrayList var1 = Lists.newArrayList();

      while(var1.size() != 5) {
         try {
            var1.add(this.func_178515_c());
         } catch (InterruptedException var3) {
         }
      }

      this.field_178520_e.addAll(var1);
   }

   public void func_178512_a(RegionRenderCacheBuilder var1) {
      this.field_178520_e.add(var1);
   }

   public RegionRenderCacheBuilder func_178515_c() throws InterruptedException {
      return (RegionRenderCacheBuilder)this.field_178520_e.take();
   }

   public ChunkCompileTaskGenerator func_178511_d() throws InterruptedException {
      return (ChunkCompileTaskGenerator)this.field_178519_d.take();
   }

   public boolean func_178509_c(RenderChunk var1) {
      var1.func_178579_c().lock();

      boolean var3;
      try {
         final ChunkCompileTaskGenerator var2 = var1.func_178582_e();
         if (var2 == null) {
            var3 = true;
            return var3;
         }

         var2.func_178539_a(new Runnable() {
            public void run() {
               ChunkRenderDispatcher.this.field_178519_d.remove(var2);
            }
         });
         var3 = this.field_178519_d.offer(var2);
      } finally {
         var1.func_178579_c().unlock();
      }

      return var3;
   }

   public ListenableFuture<Object> func_178503_a(final EnumWorldBlockLayer var1, final WorldRenderer var2, final RenderChunk var3, final CompiledChunk var4) {
      if (Minecraft.func_71410_x().func_152345_ab()) {
         if (OpenGlHelper.func_176075_f()) {
            this.func_178506_a(var2, var3.func_178565_b(var1.ordinal()));
         } else {
            this.func_178510_a(var2, ((ListedRenderChunk)var3).func_178600_a(var1, var4), var3);
         }

         var2.func_178969_c(0.0D, 0.0D, 0.0D);
         return Futures.immediateFuture((Object)null);
      } else {
         ListenableFutureTask var5 = ListenableFutureTask.create(new Runnable() {
            public void run() {
               ChunkRenderDispatcher.this.func_178503_a(var1, var2, var3, var4);
            }
         }, (Object)null);
         synchronized(this.field_178524_h) {
            this.field_178524_h.add(var5);
            return var5;
         }
      }
   }

   private void func_178510_a(WorldRenderer var1, int var2, RenderChunk var3) {
      GL11.glNewList(var2, 4864);
      GlStateManager.func_179094_E();
      var3.func_178572_f();
      this.field_178517_f.func_181679_a(var1);
      GlStateManager.func_179121_F();
      GL11.glEndList();
   }

   private void func_178506_a(WorldRenderer var1, VertexBuffer var2) {
      this.field_178518_g.func_178178_a(var2);
      this.field_178518_g.func_181679_a(var1);
   }

   public void func_178513_e() {
      while(!this.field_178519_d.isEmpty()) {
         ChunkCompileTaskGenerator var1 = (ChunkCompileTaskGenerator)this.field_178519_d.poll();
         if (var1 != null) {
            var1.func_178542_e();
         }
      }

   }
}
