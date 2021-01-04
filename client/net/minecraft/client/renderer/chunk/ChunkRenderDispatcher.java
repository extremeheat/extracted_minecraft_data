package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexBufferUploader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import net.minecraft.DefaultUncaughtExceptionHandler;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadFactory THREAD_FACTORY;
   private final int bufferCount;
   private final List<Thread> threads = Lists.newArrayList();
   private final List<ChunkRenderWorker> workers = Lists.newArrayList();
   private final PriorityBlockingQueue<ChunkCompileTask> chunksToBatch = Queues.newPriorityBlockingQueue();
   private final BlockingQueue<ChunkBufferBuilderPack> availableChunkBuffers;
   private final BufferUploader uploader = new BufferUploader();
   private final VertexBufferUploader vboUploader = new VertexBufferUploader();
   private final Queue<ChunkRenderDispatcher.PendingUpload> pendingUploads = Queues.newPriorityQueue();
   private final ChunkRenderWorker localWorker;
   private Vec3 camera;

   public ChunkRenderDispatcher(boolean var1) {
      super();
      this.camera = Vec3.ZERO;
      int var2 = Math.max(1, (int)((double)Runtime.getRuntime().maxMemory() * 0.3D) / 10485760 - 1);
      int var3 = Runtime.getRuntime().availableProcessors();
      int var4 = var1 ? var3 : Math.min(var3, 4);
      int var5 = Math.max(1, Math.min(var4 * 2, var2));
      this.localWorker = new ChunkRenderWorker(this, new ChunkBufferBuilderPack());
      ArrayList var6 = Lists.newArrayListWithExpectedSize(var5);

      int var7;
      int var8;
      try {
         for(var7 = 0; var7 < var5; ++var7) {
            var6.add(new ChunkBufferBuilderPack());
         }
      } catch (OutOfMemoryError var11) {
         LOGGER.warn("Allocated only {}/{} buffers", var6.size(), var5);
         var8 = var6.size() * 2 / 3;

         for(int var9 = 0; var9 < var8; ++var9) {
            var6.remove(var6.size() - 1);
         }

         System.gc();
      }

      this.bufferCount = var6.size();
      this.availableChunkBuffers = Queues.newArrayBlockingQueue(this.bufferCount);
      this.availableChunkBuffers.addAll(var6);
      var7 = Math.min(var4, this.bufferCount);
      if (var7 > 1) {
         for(var8 = 0; var8 < var7; ++var8) {
            ChunkRenderWorker var12 = new ChunkRenderWorker(this);
            Thread var10 = THREAD_FACTORY.newThread(var12);
            var10.start();
            this.workers.add(var12);
            this.threads.add(var10);
         }
      }

   }

   public String getStats() {
      return this.threads.isEmpty() ? String.format("pC: %03d, single-threaded", this.chunksToBatch.size()) : String.format("pC: %03d, pU: %02d, aB: %02d", this.chunksToBatch.size(), this.pendingUploads.size(), this.availableChunkBuffers.size());
   }

   public void setCamera(Vec3 var1) {
      this.camera = var1;
   }

   public Vec3 getCameraPosition() {
      return this.camera;
   }

   public boolean uploadAllPendingUploadsUntil(long var1) {
      boolean var3 = false;

      boolean var4;
      do {
         var4 = false;
         if (this.threads.isEmpty()) {
            ChunkCompileTask var5 = (ChunkCompileTask)this.chunksToBatch.poll();
            if (var5 != null) {
               try {
                  this.localWorker.doTask(var5);
                  var4 = true;
               } catch (InterruptedException var9) {
                  LOGGER.warn("Skipped task due to interrupt");
               }
            }
         }

         int var11 = 0;
         synchronized(this.pendingUploads) {
            while(var11 < 10) {
               ChunkRenderDispatcher.PendingUpload var7 = (ChunkRenderDispatcher.PendingUpload)this.pendingUploads.poll();
               if (var7 == null) {
                  break;
               }

               if (!var7.future.isDone()) {
                  var7.future.run();
                  var4 = true;
                  var3 = true;
                  ++var11;
               }
            }
         }
      } while(var1 != 0L && var4 && var1 >= Util.getNanos());

      return var3;
   }

   public boolean rebuildChunkAsync(RenderChunk var1) {
      var1.getTaskLock().lock();

      boolean var4;
      try {
         ChunkCompileTask var2 = var1.createCompileTask();
         var2.addCancelListener(() -> {
            this.chunksToBatch.remove(var2);
         });
         boolean var3 = this.chunksToBatch.offer(var2);
         if (!var3) {
            var2.cancel();
         }

         var4 = var3;
      } finally {
         var1.getTaskLock().unlock();
      }

      return var4;
   }

   public boolean rebuildChunkSync(RenderChunk var1) {
      var1.getTaskLock().lock();

      boolean var3;
      try {
         ChunkCompileTask var2 = var1.createCompileTask();

         try {
            this.localWorker.doTask(var2);
         } catch (InterruptedException var7) {
         }

         var3 = true;
      } finally {
         var1.getTaskLock().unlock();
      }

      return var3;
   }

   public void blockUntilClear() {
      this.clearBatchQueue();
      ArrayList var1 = Lists.newArrayList();

      while(var1.size() != this.bufferCount) {
         this.uploadAllPendingUploadsUntil(9223372036854775807L);

         try {
            var1.add(this.takeChunkBufferBuilder());
         } catch (InterruptedException var3) {
         }
      }

      this.availableChunkBuffers.addAll(var1);
   }

   public void releaseChunkBufferBuilder(ChunkBufferBuilderPack var1) {
      this.availableChunkBuffers.add(var1);
   }

   public ChunkBufferBuilderPack takeChunkBufferBuilder() throws InterruptedException {
      return (ChunkBufferBuilderPack)this.availableChunkBuffers.take();
   }

   public ChunkCompileTask takeChunk() throws InterruptedException {
      return (ChunkCompileTask)this.chunksToBatch.take();
   }

   public boolean resortChunkTransparencyAsync(RenderChunk var1) {
      var1.getTaskLock().lock();

      boolean var3;
      try {
         ChunkCompileTask var2 = var1.createTransparencySortTask();
         if (var2 == null) {
            var3 = true;
            return var3;
         }

         var2.addCancelListener(() -> {
            this.chunksToBatch.remove(var2);
         });
         var3 = this.chunksToBatch.offer(var2);
      } finally {
         var1.getTaskLock().unlock();
      }

      return var3;
   }

   public ListenableFuture<Void> uploadChunkLayer(BlockLayer var1, BufferBuilder var2, RenderChunk var3, CompiledChunk var4, double var5) {
      if (Minecraft.getInstance().isSameThread()) {
         if (GLX.useVbo()) {
            this.uploadChunkLayer(var2, var3.getBuffer(var1.ordinal()));
         } else {
            this.compileChunkLayerIntoGlList(var2, ((ListedRenderChunk)var3).getGlListId(var1, var4));
         }

         var2.offset(0.0D, 0.0D, 0.0D);
         return Futures.immediateFuture((Object)null);
      } else {
         ListenableFutureTask var7 = ListenableFutureTask.create(() -> {
            this.uploadChunkLayer(var1, var2, var3, var4, var5);
         }, (Object)null);
         synchronized(this.pendingUploads) {
            this.pendingUploads.add(new ChunkRenderDispatcher.PendingUpload(var7, var5));
            return var7;
         }
      }
   }

   private void compileChunkLayerIntoGlList(BufferBuilder var1, int var2) {
      GlStateManager.newList(var2, 4864);
      this.uploader.end(var1);
      GlStateManager.endList();
   }

   private void uploadChunkLayer(BufferBuilder var1, VertexBuffer var2) {
      this.vboUploader.setBuffer(var2);
      this.vboUploader.end(var1);
   }

   public void clearBatchQueue() {
      while(!this.chunksToBatch.isEmpty()) {
         ChunkCompileTask var1 = (ChunkCompileTask)this.chunksToBatch.poll();
         if (var1 != null) {
            var1.cancel();
         }
      }

   }

   public boolean isQueueEmpty() {
      return this.chunksToBatch.isEmpty() && this.pendingUploads.isEmpty();
   }

   public void dispose() {
      this.clearBatchQueue();
      Iterator var1 = this.workers.iterator();

      while(var1.hasNext()) {
         ChunkRenderWorker var2 = (ChunkRenderWorker)var1.next();
         var2.stop();
      }

      var1 = this.threads.iterator();

      while(var1.hasNext()) {
         Thread var5 = (Thread)var1.next();

         try {
            var5.interrupt();
            var5.join();
         } catch (InterruptedException var4) {
            LOGGER.warn("Interrupted whilst waiting for worker to die", var4);
         }
      }

      this.availableChunkBuffers.clear();
   }

   static {
      THREAD_FACTORY = (new ThreadFactoryBuilder()).setNameFormat("Chunk Batcher %d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build();
   }

   class PendingUpload implements Comparable<ChunkRenderDispatcher.PendingUpload> {
      private final ListenableFutureTask<Void> future;
      private final double dist;

      public PendingUpload(ListenableFutureTask<Void> var2, double var3) {
         super();
         this.future = var2;
         this.dist = var3;
      }

      public int compareTo(ChunkRenderDispatcher.PendingUpload var1) {
         return Doubles.compare(this.dist, var1.dist);
      }

      // $FF: synthetic method
      public int compareTo(Object var1) {
         return this.compareTo((ChunkRenderDispatcher.PendingUpload)var1);
      }
   }
}
