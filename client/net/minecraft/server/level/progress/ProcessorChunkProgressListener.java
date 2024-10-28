package net.minecraft.server.level.progress;

import java.util.Objects;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class ProcessorChunkProgressListener implements ChunkProgressListener {
   private final ChunkProgressListener delegate;
   private final ProcessorMailbox<Runnable> mailbox;
   private boolean started;

   private ProcessorChunkProgressListener(ChunkProgressListener var1, Executor var2) {
      super();
      this.delegate = var1;
      this.mailbox = ProcessorMailbox.create(var2, "progressListener");
   }

   public static ProcessorChunkProgressListener createStarted(ChunkProgressListener var0, Executor var1) {
      ProcessorChunkProgressListener var2 = new ProcessorChunkProgressListener(var0, var1);
      var2.start();
      return var2;
   }

   public void updateSpawnPos(ChunkPos var1) {
      this.mailbox.tell(() -> {
         this.delegate.updateSpawnPos(var1);
      });
   }

   public void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2) {
      if (this.started) {
         this.mailbox.tell(() -> {
            this.delegate.onStatusChange(var1, var2);
         });
      }

   }

   public void start() {
      this.started = true;
      ProcessorMailbox var10000 = this.mailbox;
      ChunkProgressListener var10001 = this.delegate;
      Objects.requireNonNull(var10001);
      var10000.tell(var10001::start);
   }

   public void stop() {
      this.started = false;
      ProcessorMailbox var10000 = this.mailbox;
      ChunkProgressListener var10001 = this.delegate;
      Objects.requireNonNull(var10001);
      var10000.tell(var10001::stop);
   }
}
