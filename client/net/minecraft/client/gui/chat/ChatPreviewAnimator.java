package net.minecraft.client.gui.chat;

import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;

public class ChatPreviewAnimator {
   private static final long FADE_DURATION = 200L;
   @Nullable
   private Component residualPreview;
   private long fadeTime;
   private long lastTime;

   public ChatPreviewAnimator() {
      super();
   }

   public void reset(long var1) {
      this.residualPreview = null;
      this.fadeTime = 0L;
      this.lastTime = var1;
   }

   public ChatPreviewAnimator.State get(long var1, @Nullable Component var3) {
      long var4 = var1 - this.lastTime;
      this.lastTime = var1;
      return var3 != null ? this.getEnabled(var4, var3) : this.getDisabled(var4);
   }

   private ChatPreviewAnimator.State getEnabled(long var1, Component var3) {
      this.residualPreview = var3;
      if (this.fadeTime < 200L) {
         this.fadeTime = Math.min(this.fadeTime + var1, 200L);
      }

      return new ChatPreviewAnimator.State(var3, alpha(this.fadeTime));
   }

   private ChatPreviewAnimator.State getDisabled(long var1) {
      if (this.fadeTime > 0L) {
         this.fadeTime = Math.max(this.fadeTime - var1, 0L);
      }

      return this.fadeTime > 0L ? new ChatPreviewAnimator.State(this.residualPreview, alpha(this.fadeTime)) : ChatPreviewAnimator.State.DISABLED;
   }

   private static float alpha(long var0) {
      return (float)var0 / 200.0F;
   }

   public static record State(@Nullable Component b, float c) {
      @Nullable
      private final Component preview;
      private final float alpha;
      public static final ChatPreviewAnimator.State DISABLED = new ChatPreviewAnimator.State(null, 0.0F);

      public State(@Nullable Component var1, float var2) {
         super();
         this.preview = var1;
         this.alpha = var2;
      }
   }
}
