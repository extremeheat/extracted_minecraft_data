package io.netty.handler.codec.http2;

import io.netty.handler.codec.EmptyHeaders;

public final class EmptyHttp2Headers extends EmptyHeaders<CharSequence, CharSequence, Http2Headers> implements Http2Headers {
   public static final EmptyHttp2Headers INSTANCE = new EmptyHttp2Headers();

   private EmptyHttp2Headers() {
      super();
   }

   public EmptyHttp2Headers method(CharSequence var1) {
      throw new UnsupportedOperationException();
   }

   public EmptyHttp2Headers scheme(CharSequence var1) {
      throw new UnsupportedOperationException();
   }

   public EmptyHttp2Headers authority(CharSequence var1) {
      throw new UnsupportedOperationException();
   }

   public EmptyHttp2Headers path(CharSequence var1) {
      throw new UnsupportedOperationException();
   }

   public EmptyHttp2Headers status(CharSequence var1) {
      throw new UnsupportedOperationException();
   }

   public CharSequence method() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.METHOD.value());
   }

   public CharSequence scheme() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.SCHEME.value());
   }

   public CharSequence authority() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.AUTHORITY.value());
   }

   public CharSequence path() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.PATH.value());
   }

   public CharSequence status() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.STATUS.value());
   }

   public boolean contains(CharSequence var1, CharSequence var2, boolean var3) {
      return false;
   }
}
