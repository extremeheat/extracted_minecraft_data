package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.GameNarrator;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

public class RepeatedNarrator {
   private final float permitsPerSecond;
   private final AtomicReference<@Nullable Params> params = new AtomicReference();

   public RepeatedNarrator(final Duration repeatDelay) {
      super();
      this.permitsPerSecond = 1000.0F / (float)repeatDelay.toMillis();
   }

   public void narrate(final GameNarrator narrator, final Component narration) {
      Params params = (Params)this.params.updateAndGet((existing) -> existing != null && narration.equals(existing.narration) ? existing : new Params(narration, RateLimiter.create((double)this.permitsPerSecond)));
      if (params.rateLimiter.tryAcquire(1)) {
         narrator.saySystemNow(narration);
      }

   }

   private static record Params(Component narration, RateLimiter rateLimiter) {
      private Params {
         super();
      }
   }
}
