package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ChunkRenderDispatcher dispatcher;
   private final ChunkBufferBuilderPack fixedBuffers;
   private boolean running;

   public ChunkRenderWorker(ChunkRenderDispatcher var1) {
      this(var1, (ChunkBufferBuilderPack)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher var1, @Nullable ChunkBufferBuilderPack var2) {
      super();
      this.running = true;
      this.dispatcher = var1;
      this.fixedBuffers = var2;
   }

   public void run() {
      while(this.running) {
         try {
            this.doTask(this.dispatcher.takeChunk());
         } catch (InterruptedException var3) {
            LOGGER.debug("Stopping chunk worker due to interrupt");
            return;
         } catch (Throwable var4) {
            CrashReport var2 = CrashReport.forThrowable(var4, "Batching chunks");
            Minecraft.getInstance().delayCrash(Minecraft.getInstance().fillReport(var2));
            return;
         }
      }

   }

   void doTask(final ChunkCompileTask var1) throws InterruptedException {
      var1.getStatusLock().lock();

      try {
         if (!checkState(var1, ChunkCompileTask.Status.PENDING)) {
            return;
         }

         if (!var1.getChunk().hasAllNeighbors()) {
            var1.cancel();
            return;
         }

         var1.setStatus(ChunkCompileTask.Status.COMPILING);
      } finally {
         var1.getStatusLock().unlock();
      }

      final ChunkBufferBuilderPack var2 = this.takeBuffers();
      var1.getStatusLock().lock();

      try {
         if (!checkState(var1, ChunkCompileTask.Status.COMPILING)) {
            this.releaseBuffers(var2);
            return;
         }
      } finally {
         var1.getStatusLock().unlock();
      }

      var1.setBuilders(var2);
      Vec3 var3 = this.dispatcher.getCameraPosition();
      float var4 = (float)var3.x;
      float var5 = (float)var3.y;
      float var6 = (float)var3.z;
      ChunkCompileTask.Type var7 = var1.getType();
      if (var7 == ChunkCompileTask.Type.REBUILD_CHUNK) {
         var1.getChunk().compile(var4, var5, var6, var1);
      } else if (var7 == ChunkCompileTask.Type.RESORT_TRANSPARENCY) {
         var1.getChunk().rebuildTransparent(var4, var5, var6, var1);
      }

      var1.getStatusLock().lock();

      label321: {
         try {
            if (checkState(var1, ChunkCompileTask.Status.COMPILING)) {
               var1.setStatus(ChunkCompileTask.Status.UPLOADING);
               break label321;
            }

            this.releaseBuffers(var2);
         } finally {
            var1.getStatusLock().unlock();
         }

         return;
      }

      final CompiledChunk var8 = var1.getCompiledChunk();
      ArrayList var9 = Lists.newArrayList();
      if (var7 == ChunkCompileTask.Type.REBUILD_CHUNK) {
         BlockLayer[] var10 = BlockLayer.values();
         int var11 = var10.length;

         for(int var12 = 0; var12 < var11; ++var12) {
            BlockLayer var13 = var10[var12];
            if (var8.hasLayer(var13)) {
               var9.add(this.dispatcher.uploadChunkLayer(var13, var1.getBuilders().builder(var13), var1.getChunk(), var8, var1.getDistAtCreation()));
            }
         }
      } else if (var7 == ChunkCompileTask.Type.RESORT_TRANSPARENCY) {
         var9.add(this.dispatcher.uploadChunkLayer(BlockLayer.TRANSLUCENT, var1.getBuilders().builder(BlockLayer.TRANSLUCENT), var1.getChunk(), var8, var1.getDistAtCreation()));
      }

      ListenableFuture var26 = Futures.allAsList(var9);
      var1.addCancelListener(() -> {
         var26.cancel(false);
      });
      Futures.addCallback(var26, new FutureCallback<List<Void>>() {
         public void onSuccess(@Nullable List<Void> var1x) {
            ChunkRenderWorker.this.releaseBuffers(var2);
            var1.getStatusLock().lock();

            label38: {
               try {
                  if (ChunkRenderWorker.checkState(var1, ChunkCompileTask.Status.UPLOADING)) {
                     var1.setStatus(ChunkCompileTask.Status.DONE);
                     break label38;
                  }
               } finally {
                  var1.getStatusLock().unlock();
               }

               return;
            }

            var1.getChunk().setCompiledChunk(var8);
         }

         public void onFailure(Throwable var1x) {
            ChunkRenderWorker.this.releaseBuffers(var2);
            if (!(var1x instanceof CancellationException) && !(var1x instanceof InterruptedException)) {
               Minecraft.getInstance().delayCrash(CrashReport.forThrowable(var1x, "Rendering chunk"));
            }

         }

         // $FF: synthetic method
         public void onSuccess(@Nullable Object var1x) {
            this.onSuccess((List)var1x);
         }
      });
   }

   private static boolean checkState(ChunkCompileTask var0, ChunkCompileTask.Status var1) {
      if (var0.getStatus() != var1) {
         if (!var0.wasCancelled()) {
            LOGGER.warn("Chunk render task was {} when I expected it to be {}; ignoring task", var0.getStatus(), var1);
         }

         return false;
      } else {
         return true;
      }
   }

   private ChunkBufferBuilderPack takeBuffers() throws InterruptedException {
      return this.fixedBuffers != null ? this.fixedBuffers : this.dispatcher.takeChunkBufferBuilder();
   }

   private void releaseBuffers(ChunkBufferBuilderPack var1) {
      if (var1 != this.fixedBuffers) {
         this.dispatcher.releaseChunkBufferBuilder(var1);
      }

   }

   public void stop() {
      this.running = false;
   }
}
