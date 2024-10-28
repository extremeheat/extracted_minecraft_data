package net.minecraft.server.level.progress;

import java.util.Objects;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.thread.ConsecutiveExecutor;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class ProcessorChunkProgressListener implements ChunkProgressListener {
   private final ChunkProgressListener delegate;
   private final ConsecutiveExecutor consecutiveExecutor;
   private boolean started;

   private ProcessorChunkProgressListener(ChunkProgressListener var1, Executor var2) {
      super();
      this.delegate = var1;
      this.consecutiveExecutor = new ConsecutiveExecutor(var2, "progressListener");
   }

   public static ProcessorChunkProgressListener createStarted(ChunkProgressListener var0, Executor var1) {
      ProcessorChunkProgressListener var2 = new ProcessorChunkProgressListener(var0, var1);
      var2.start();
      return var2;
   }

   public void updateSpawnPos(ChunkPos var1) {
      this.consecutiveExecutor.schedule(() -> {
         this.delegate.updateSpawnPos(var1);
      });
   }

   public void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2) {
      if (this.started) {
         this.consecutiveExecutor.schedule(() -> {
            this.delegate.onStatusChange(var1, var2);
         });
      }

   }

   public void start() {
      this.started = true;
      ConsecutiveExecutor var10000 = this.consecutiveExecutor;
      ChunkProgressListener var10001 = this.delegate;
      Objects.requireNonNull(var10001);
      var10000.schedule(var10001::start);
   }

   public void stop() {
      this.started = false;
      ConsecutiveExecutor var10000 = this.consecutiveExecutor;
      ChunkProgressListener var10001 = this.delegate;
      Objects.requireNonNull(var10001);
      var10000.schedule(var10001::stop);
   }
}
