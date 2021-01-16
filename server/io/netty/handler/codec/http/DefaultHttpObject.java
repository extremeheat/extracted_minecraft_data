package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;

public class DefaultHttpObject implements HttpObject {
   private static final int HASH_CODE_PRIME = 31;
   private DecoderResult decoderResult;

   protected DefaultHttpObject() {
      super();
      this.decoderResult = DecoderResult.SUCCESS;
   }

   public DecoderResult decoderResult() {
      return this.decoderResult;
   }

   /** @deprecated */
   @Deprecated
   public DecoderResult getDecoderResult() {
      return this.decoderResult();
   }

   public void setDecoderResult(DecoderResult var1) {
      if (var1 == null) {
         throw new NullPointerException("decoderResult");
      } else {
         this.decoderResult = var1;
      }
   }

   public int hashCode() {
      byte var1 = 1;
      int var2 = 31 * var1 + this.decoderResult.hashCode();
      return var2;
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof DefaultHttpObject)) {
         return false;
      } else {
         DefaultHttpObject var2 = (DefaultHttpObject)var1;
         return this.decoderResult().equals(var2.decoderResult());
      }
   }
}
