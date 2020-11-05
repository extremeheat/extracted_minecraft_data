package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.Util;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;

public class RepeatedNarrator {
   private final float permitsPerSecond;
   private final AtomicReference<RepeatedNarrator.Params> params = new AtomicReference();

   public RepeatedNarrator(Duration var1) {
      super();
      this.permitsPerSecond = 1000.0F / (float)var1.toMillis();
   }

   public void narrate(String var1) {
      RepeatedNarrator.Params var2 = (RepeatedNarrator.Params)this.params.updateAndGet((var2x) -> {
         return var2x != null && var1.equals(var2x.narration) ? var2x : new RepeatedNarrator.Params(var1, RateLimiter.create((double)this.permitsPerSecond));
      });
      if (var2.rateLimiter.tryAcquire(1)) {
         NarratorChatListener.INSTANCE.handle(ChatType.SYSTEM, new TextComponent(var1), Util.NIL_UUID);
      }

   }

   static class Params {
      private final String narration;
      private final RateLimiter rateLimiter;

      Params(String var1, RateLimiter var2) {
         super();
         this.narration = var1;
         this.rateLimiter = var2;
      }
   }
}
