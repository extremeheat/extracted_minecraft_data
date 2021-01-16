package io.netty.handler.codec.memcache;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.AbstractReferenceCounted;

public abstract class AbstractMemcacheObject extends AbstractReferenceCounted implements MemcacheObject {
   private DecoderResult decoderResult;

   protected AbstractMemcacheObject() {
      super();
      this.decoderResult = DecoderResult.SUCCESS;
   }

   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   public void setDecoderResult(DecoderResult var1) {
      if (var1 == null) {
         throw new NullPointerException("DecoderResult should not be null.");
      } else {
         this.decoderResult = var1;
      }
   }
}
