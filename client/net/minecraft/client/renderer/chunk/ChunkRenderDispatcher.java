package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderDispatcher {
   private static final Logger field_178523_a = LogManager.getLogger();
   private static final ThreadFactory field_178521_b;
   private final int field_188249_c;
   private final List<Thread> field_188250_d = Lists.newArrayList();
   private final List<ChunkRenderWorker> field_178522_c = Lists.newArrayList();
   private final PriorityBlockingQueue<ChunkRenderTask> field_178519_d = Queues.newPriorityBlockingQueue();
   private final BlockingQueue<RegionRenderCacheBuilder> field_178520_e;
   private final WorldVertexBufferUploader field_178517_f = new WorldVertexBufferUploader();
   private final VertexBufferUploader field_178518_g = new VertexBufferUploader();
   private final Queue<ChunkRenderDispatcher.PendingUpload> field_178524_h = Queues.newPriorityQueue();
   private final ChunkRenderWorker field_178525_i;

   public ChunkRenderDispatcher() {
      super();
      int var1 = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / 10485760);
      int var2 = Math.max(1, MathHelper.func_76125_a(Runtime.getRuntime().availableProcessors(), 1, var1 / 5));
      this.field_188249_c = MathHelper.func_76125_a(var2 * 10, 1, var1);
      int var3;
      if (var2 > 1) {
         for(var3 = 0; var3 < var2; ++var3) {
            ChunkRenderWorker var4 = new ChunkRenderWorker(this);
            Thread var5 = field_178521_b.newThread(var4);
            var5.start();
            this.field_178522_c.add(var4);
            this.field_188250_d.add(var5);
         }
      }

      this.field_178520_e = Queues.newArrayBlockingQueue(this.field_188249_c);

      for(var3 = 0; var3 < this.field_188249_c; ++var3) {
         this.field_178520_e.add(new RegionRenderCacheBuilder());
      }

      this.field_178525_i = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
   }

   public String func_178504_a() {
      return this.field_188250_d.isEmpty() ? String.format("pC: %03d, single-threaded", this.field_178519_d.size()) : String.format("pC: %03d, pU: %1d, aB: %1d", this.field_178519_d.size(), this.field_178524_h.size(), this.field_178520_e.size());
   }

   public boolean func_178516_a(long var1) {
      boolean var3 = false;

      boolean var4;
      do {
         var4 = false;
         if (this.field_188250_d.isEmpty()) {
            ChunkRenderTask var5 = (ChunkRenderTask)this.field_178519_d.poll();
            if (var5 != null) {
               try {
                  this.field_178525_i.func_178474_a(var5);
                  var4 = true;
               } catch (InterruptedException var8) {
                  field_178523_a.warn("Skipped task due to interrupt");
               }
            }
         }

         synchronized(this.field_178524_h) {
            if (!this.field_178524_h.isEmpty()) {
               ((ChunkRenderDispatcher.PendingUpload)this.field_178524_h.poll()).field_188241_b.run();
               var4 = true;
               var3 = true;
            }
         }
      } while(var1 != 0L && var4 && var1 >= Util.func_211178_c());

      return var3;
   }

   public boolean func_178507_a(RenderChunk var1) {
      var1.func_178579_c().lock();

      boolean var4;
      try {
         ChunkRenderTask var2 = var1.func_178574_d();
         var2.func_178539_a(() -> {
            this.field_178519_d.remove(var2);
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
         ChunkRenderTask var2 = var1.func_178574_d();

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
      ArrayList var1 = Lists.newArrayList();

      while(var1.size() != this.field_188249_c) {
         this.func_178516_a(9223372036854775807L);

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

   public ChunkRenderTask func_178511_d() throws InterruptedException {
      return (ChunkRenderTask)this.field_178519_d.take();
   }

   public boolean func_178509_c(RenderChunk var1) {
      var1.func_178579_c().lock();

      boolean var3;
      try {
         ChunkRenderTask var2 = var1.func_178582_e();
         if (var2 != null) {
            var2.func_178539_a(() -> {
               this.field_178519_d.remove(var2);
            });
            var3 = this.field_178519_d.offer(var2);
            return var3;
         }

         var3 = true;
      } finally {
         var1.func_178579_c().unlock();
      }

      return var3;
   }

   public ListenableFuture<Object> func_188245_a(BlockRenderLayer var1, BufferBuilder var2, RenderChunk var3, CompiledChunk var4, double var5) {
      if (Minecraft.func_71410_x().func_152345_ab()) {
         if (OpenGlHelper.func_176075_f()) {
            this.func_178506_a(var2, var3.func_178565_b(var1.ordinal()));
         } else {
            this.func_178510_a(var2, ((ListedRenderChunk)var3).func_178600_a(var1, var4), var3);
         }

         var2.func_178969_c(0.0D, 0.0D, 0.0D);
         return Futures.immediateFuture((Object)null);
      } else {
         ListenableFutureTask var7 = ListenableFutureTask.create(() -> {
            this.func_188245_a(var1, var2, var3, var4, var5);
         }, (Object)null);
         synchronized(this.field_178524_h) {
            this.field_178524_h.add(new ChunkRenderDispatcher.PendingUpload(var7, var5));
            return var7;
         }
      }
   }

   private void func_178510_a(BufferBuilder var1, int var2, RenderChunk var3) {
      GlStateManager.func_187423_f(var2, 4864);
      GlStateManager.func_179094_E();
      var3.func_178572_f();
      this.field_178517_f.func_181679_a(var1);
      GlStateManager.func_179121_F();
      GlStateManager.func_187415_K();
   }

   private void func_178506_a(BufferBuilder var1, VertexBuffer var2) {
      this.field_178518_g.func_178178_a(var2);
      this.field_178518_g.func_181679_a(var1);
   }

   public void func_178513_e() {
      while(!this.field_178519_d.isEmpty()) {
         ChunkRenderTask var1 = (ChunkRenderTask)this.field_178519_d.poll();
         if (var1 != null) {
            var1.func_178542_e();
         }
      }

   }

   public boolean func_188247_f() {
      return this.field_178519_d.isEmpty() && this.field_178524_h.isEmpty();
   }

   public void func_188244_g() {
      this.func_178513_e();
      Iterator var1 = this.field_178522_c.iterator();

      while(var1.hasNext()) {
         ChunkRenderWorker var2 = (ChunkRenderWorker)var1.next();
         var2.func_188264_a();
      }

      var1 = this.field_188250_d.iterator();

      while(var1.hasNext()) {
         Thread var5 = (Thread)var1.next();

         try {
            var5.interrupt();
            var5.join();
         } catch (InterruptedException var4) {
            field_178523_a.warn("Interrupted whilst waiting for worker to die", var4);
         }
      }

      this.field_178520_e.clear();
   }

   public boolean func_188248_h() {
      return this.field_178520_e.isEmpty();
   }

   static {
      field_178521_b = (new ThreadFactoryBuilder()).setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(field_178523_a)).build();
   }

   class PendingUpload implements Comparable<ChunkRenderDispatcher.PendingUpload> {
      private final ListenableFutureTask<Object> field_188241_b;
      private final double field_188242_c;

      public PendingUpload(ListenableFutureTask<Object> var2, double var3) {
         super();
         this.field_188241_b = var2;
         this.field_188242_c = var3;
      }

      public int compareTo(ChunkRenderDispatcher.PendingUpload var1) {
         return Doubles.compare(this.field_188242_c, var1.field_188242_c);
      }

      // $FF: synthetic method
      public int compareTo(Object var1) {
         return this.compareTo((ChunkRenderDispatcher.PendingUpload)var1);
      }
   }
}
