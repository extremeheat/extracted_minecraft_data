package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;

public class ChunkCompileTask implements Comparable<ChunkCompileTask> {
   private final RenderChunk chunk;
   private final ReentrantLock lock = new ReentrantLock();
   private final List<Runnable> cancelListeners = Lists.newArrayList();
   private final ChunkCompileTask.Type type;
   private final double distAtCreation;
   @Nullable
   private RenderChunkRegion region;
   private ChunkBufferBuilderPack builders;
   private CompiledChunk compiledChunk;
   private ChunkCompileTask.Status status;
   private boolean isCancelled;

   public ChunkCompileTask(RenderChunk var1, ChunkCompileTask.Type var2, double var3, @Nullable RenderChunkRegion var5) {
      super();
      this.status = ChunkCompileTask.Status.PENDING;
      this.chunk = var1;
      this.type = var2;
      this.distAtCreation = var3;
      this.region = var5;
   }

   public ChunkCompileTask.Status getStatus() {
      return this.status;
   }

   public RenderChunk getChunk() {
      return this.chunk;
   }

   @Nullable
   public RenderChunkRegion takeRegion() {
      RenderChunkRegion var1 = this.region;
      this.region = null;
      return var1;
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk var1) {
      this.compiledChunk = var1;
   }

   public ChunkBufferBuilderPack getBuilders() {
      return this.builders;
   }

   public void setBuilders(ChunkBufferBuilderPack var1) {
      this.builders = var1;
   }

   public void setStatus(ChunkCompileTask.Status var1) {
      this.lock.lock();

      try {
         this.status = var1;
      } finally {
         this.lock.unlock();
      }

   }

   public void cancel() {
      this.lock.lock();

      try {
         this.region = null;
         if (this.type == ChunkCompileTask.Type.REBUILD_CHUNK && this.status != ChunkCompileTask.Status.DONE) {
            this.chunk.setDirty(false);
         }

         this.isCancelled = true;
         this.status = ChunkCompileTask.Status.DONE;
         Iterator var1 = this.cancelListeners.iterator();

         while(var1.hasNext()) {
            Runnable var2 = (Runnable)var1.next();
            var2.run();
         }
      } finally {
         this.lock.unlock();
      }

   }

   public void addCancelListener(Runnable var1) {
      this.lock.lock();

      try {
         this.cancelListeners.add(var1);
         if (this.isCancelled) {
            var1.run();
         }
      } finally {
         this.lock.unlock();
      }

   }

   public ReentrantLock getStatusLock() {
      return this.lock;
   }

   public ChunkCompileTask.Type getType() {
      return this.type;
   }

   public boolean wasCancelled() {
      return this.isCancelled;
   }

   public int compareTo(ChunkCompileTask var1) {
      return Doubles.compare(this.distAtCreation, var1.distAtCreation);
   }

   public double getDistAtCreation() {
      return this.distAtCreation;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((ChunkCompileTask)var1);
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
