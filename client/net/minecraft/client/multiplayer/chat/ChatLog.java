package net.minecraft.client.multiplayer.chat;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class ChatLog {
   private final LoggedChatEvent[] buffer;
   private int nextId;

   public static Codec<ChatLog> codec(int var0) {
      return Codec.list(LoggedChatEvent.CODEC).comapFlatMap((var1) -> {
         int var2 = var1.size();
         return var2 > var0 ? DataResult.error(() -> {
            return "Expected: a buffer of size less than or equal to " + var0 + " but: " + var2 + " is greater than " + var0;
         }) : DataResult.success(new ChatLog(var0, var1));
      }, ChatLog::loggedChatEvents);
   }

   public ChatLog(int var1) {
      super();
      this.buffer = new LoggedChatEvent[var1];
   }

   private ChatLog(int var1, List<LoggedChatEvent> var2) {
      super();
      this.buffer = (LoggedChatEvent[])var2.toArray((var1x) -> {
         return new LoggedChatEvent[var1];
      });
      this.nextId = var2.size();
   }

   private List<LoggedChatEvent> loggedChatEvents() {
      ArrayList var1 = new ArrayList(this.size());

      for(int var2 = this.start(); var2 <= this.end(); ++var2) {
         var1.add(this.lookup(var2));
      }

      return var1;
   }

   public void push(LoggedChatEvent var1) {
      this.buffer[this.index(this.nextId++)] = var1;
   }

   @Nullable
   public LoggedChatEvent lookup(int var1) {
      return var1 >= this.start() && var1 <= this.end() ? this.buffer[this.index(var1)] : null;
   }

   private int index(int var1) {
      return var1 % this.buffer.length;
   }

   public int start() {
      return Math.max(this.nextId - this.buffer.length, 0);
   }

   public int end() {
      return this.nextId - 1;
   }

   private int size() {
      return this.end() - this.start() + 1;
   }
}
