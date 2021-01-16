package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;

final class ComposedLastHttpContent implements LastHttpContent {
   private final HttpHeaders trailingHeaders;
   private DecoderResult result;

   ComposedLastHttpContent(HttpHeaders var1) {
      super();
      this.trailingHeaders = var1;
   }

   public HttpHeaders trailingHeaders() {
      return this.trailingHeaders;
   }

   public LastHttpContent copy() {
      DefaultLastHttpContent var1 = new DefaultLastHttpContent(Unpooled.EMPTY_BUFFER);
      var1.trailingHeaders().set(this.trailingHeaders());
      return var1;
   }

   public LastHttpContent duplicate() {
      return this.copy();
   }

   public LastHttpContent retainedDuplicate() {
      return this.copy();
   }

   public LastHttpContent replace(ByteBuf var1) {
      DefaultLastHttpContent var2 = new DefaultLastHttpContent(var1);
      var2.trailingHeaders().setAll(this.trailingHeaders());
      return var2;
   }

   public LastHttpContent retain(int var1) {
      return this;
   }

   public LastHttpContent retain() {
      return this;
   }

   public LastHttpContent touch() {
      return this;
   }

   public LastHttpContent touch(Object var1) {
      return this;
   }

   public ByteBuf content() {
      return Unpooled.EMPTY_BUFFER;
   }

   public DecoderResult decoderResult() {
      return this.result;
   }

   public DecoderResult getDecoderResult() {
      return this.decoderResult();
   }

   public void setDecoderResult(DecoderResult var1) {
      this.result = var1;
   }

   public int refCnt() {
      return 1;
   }

   public boolean release() {
      return false;
   }

   public boolean release(int var1) {
      return false;
   }
}
