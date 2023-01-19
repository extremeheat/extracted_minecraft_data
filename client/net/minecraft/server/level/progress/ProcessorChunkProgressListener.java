package net.minecraft.server.level.progress;

import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkStatus;

public class ProcessorChunkProgressListener implements ChunkProgressListener {
   private final ChunkProgressListener delegate;
   private final ProcessorMailbox<Runnable> mailbox;

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

   @Override
   public void updateSpawnPos(ChunkPos var1) {
      this.mailbox.tell(() -> this.delegate.updateSpawnPos(var1));
   }

   @Override
   public void onStatusChange(ChunkPos var1, @Nullable ChunkStatus var2) {
      this.mailbox.tell(() -> this.delegate.onStatusChange(var1, var2));
   }

   @Override
   public void start() {
      this.mailbox.tell(this.delegate::start);
   }

   @Override
   public void stop() {
      this.mailbox.tell(this.delegate::stop);
   }
}
