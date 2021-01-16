package io.netty.handler.codec.socksx;

import io.netty.handler.codec.DecoderResult;

public abstract class AbstractSocksMessage implements SocksMessage {
   private DecoderResult decoderResult;

   public AbstractSocksMessage() {
      super();
      this.decoderResult = DecoderResult.SUCCESS;
   }

   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   public void setDecoderResult(DecoderResult var1) {
      if (var1 == null) {
         throw new NullPointerException("decoderResult");
      } else {
         this.decoderResult = var1;
      }
   }
}
