package io.netty.channel;

public final class ChannelMetadata {
   private final boolean hasDisconnect;
   private final int defaultMaxMessagesPerRead;

   public ChannelMetadata(boolean var1) {
      this(var1, 1);
   }

   public ChannelMetadata(boolean var1, int var2) {
      super();
      if (var2 <= 0) {
         throw new IllegalArgumentException("defaultMaxMessagesPerRead: " + var2 + " (expected > 0)");
      } else {
         this.hasDisconnect = var1;
         this.defaultMaxMessagesPerRead = var2;
      }
   }

   public boolean hasDisconnect() {
      return this.hasDisconnect;
   }

   public int defaultMaxMessagesPerRead() {
      return this.defaultMaxMessagesPerRead;
   }
}
