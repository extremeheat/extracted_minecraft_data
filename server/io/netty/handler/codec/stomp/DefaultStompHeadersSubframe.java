package io.netty.handler.codec.stomp;

import io.netty.handler.codec.DecoderResult;

public class DefaultStompHeadersSubframe implements StompHeadersSubframe {
   protected final StompCommand command;
   protected DecoderResult decoderResult;
   protected final DefaultStompHeaders headers;

   public DefaultStompHeadersSubframe(StompCommand var1) {
      this(var1, (DefaultStompHeaders)null);
   }

   DefaultStompHeadersSubframe(StompCommand var1, DefaultStompHeaders var2) {
      super();
      this.decoderResult = DecoderResult.SUCCESS;
      if (var1 == null) {
         throw new NullPointerException("command");
      } else {
         this.command = var1;
         this.headers = var2 == null ? new DefaultStompHeaders() : var2;
      }
   }

   public StompCommand command() {
      return this.command;
   }

   public StompHeaders headers() {
      return this.headers;
   }

   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   public void setDecoderResult(DecoderResult var1) {
      this.decoderResult = var1;
   }

   public String toString() {
      return "StompFrame{command=" + this.command + ", headers=" + this.headers + '}';
   }
}
