package net.minecraft.client.multiplayer.chat;

import javax.annotation.Nullable;

public class RollingMemoryChatLog implements ChatLog {
   private final LoggedChatEvent[] buffer;
   private int newestId = -1;
   private int oldestId = -1;

   public RollingMemoryChatLog(int var1) {
      super();
      this.buffer = new LoggedChatEvent[var1];
   }

   public void push(LoggedChatEvent var1) {
      int var2 = this.nextId();
      this.buffer[this.index(var2)] = var1;
   }

   private int nextId() {
      int var1 = ++this.newestId;
      if (var1 >= this.buffer.length) {
         ++this.oldestId;
      } else {
         this.oldestId = 0;
      }

      return var1;
   }

   @Nullable
   public LoggedChatEvent lookup(int var1) {
      return this.contains(var1) ? this.buffer[this.index(var1)] : null;
   }

   private int index(int var1) {
      return var1 % this.buffer.length;
   }

   public boolean contains(int var1) {
      return var1 >= this.oldestId && var1 <= this.newestId;
   }

   public int offset(int var1, int var2) {
      int var3 = var1 + var2;
      return this.contains(var3) ? var3 : -1;
   }

   public int newest() {
      return this.newestId;
   }

   public int oldest() {
      return this.oldestId;
   }
}
