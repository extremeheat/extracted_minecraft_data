package io.netty.handler.codec;

public class MessageAggregationException extends IllegalStateException {
   private static final long serialVersionUID = -1995826182950310255L;

   public MessageAggregationException() {
      super();
   }

   public MessageAggregationException(String var1) {
      super(var1);
   }

   public MessageAggregationException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public MessageAggregationException(Throwable var1) {
      super(var1);
   }
}
