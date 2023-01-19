package net.minecraft.network.chat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

public class ChatPreviewThrottler {
   private final AtomicReference<ChatPreviewThrottler.Request> scheduledRequest = new AtomicReference<>();
   @Nullable
   private CompletableFuture<?> runningRequest;

   public ChatPreviewThrottler() {
      super();
   }

   public void tick() {
      if (this.runningRequest != null && this.runningRequest.isDone()) {
         this.runningRequest = null;
      }

      if (this.runningRequest == null) {
         this.tickIdle();
      }
   }

   private void tickIdle() {
      ChatPreviewThrottler.Request var1 = this.scheduledRequest.getAndSet(null);
      if (var1 != null) {
         this.runningRequest = var1.run();
      }
   }

   public void schedule(ChatPreviewThrottler.Request var1) {
      this.scheduledRequest.set(var1);
   }

   @FunctionalInterface
   public interface Request {
      CompletableFuture<?> run();
   }
}
