package net.minecraft.realms;

import com.google.common.util.concurrent.RateLimiter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;

class RepeatedNarrator {
   final Duration repeatDelay;
   private final float permitsPerSecond;
   final AtomicReference<RepeatedNarrator.Params> params;

   public RepeatedNarrator(Duration var1) {
      super();
      this.repeatDelay = var1;
      this.params = new AtomicReference();
      float var2 = (float)var1.toMillis() / 1000.0F;
      this.permitsPerSecond = 1.0F / var2;
   }

   public void narrate(String var1) {
      RepeatedNarrator.Params var2 = (RepeatedNarrator.Params)this.params.updateAndGet((var2x) -> {
         return var2x != null && var1.equals(var2x.narration) ? var2x : new RepeatedNarrator.Params(var1, RateLimiter.create((double)this.permitsPerSecond));
      });
      if (var2.rateLimiter.tryAcquire(1)) {
         NarratorChatListener var3 = NarratorChatListener.INSTANCE;
         var3.handle(ChatType.SYSTEM, new TextComponent(var1));
      }

   }

   static class Params {
      String narration;
      RateLimiter rateLimiter;

      Params(String var1, RateLimiter var2) {
         super();
         this.narration = var1;
         this.rateLimiter = var2;
      }
   }
}
