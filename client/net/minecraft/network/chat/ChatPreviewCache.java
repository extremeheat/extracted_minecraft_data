package net.minecraft.network.chat;

import javax.annotation.Nullable;

public class ChatPreviewCache {
   @Nullable
   private Result result;

   public ChatPreviewCache() {
      super();
   }

   public void set(String var1, Component var2) {
      this.result = new Result(var1, var2);
   }

   @Nullable
   public Component pull(String var1) {
      Result var2 = this.result;
      if (var2 != null && var2.matches(var1)) {
         this.result = null;
         return var2.preview();
      } else {
         return null;
      }
   }

   static record Result(String a, Component b) {
      private final String query;
      private final Component preview;

      Result(String var1, Component var2) {
         super();
         this.query = var1;
         this.preview = var2;
      }

      public boolean matches(String var1) {
         return this.query.equals(var1);
      }

      public String query() {
         return this.query;
      }

      public Component preview() {
         return this.preview;
      }
   }
}
