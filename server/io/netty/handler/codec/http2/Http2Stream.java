package io.netty.handler.codec.http2;

public interface Http2Stream {
   int id();

   Http2Stream.State state();

   Http2Stream open(boolean var1) throws Http2Exception;

   Http2Stream close();

   Http2Stream closeLocalSide();

   Http2Stream closeRemoteSide();

   boolean isResetSent();

   Http2Stream resetSent();

   <V> V setProperty(Http2Connection.PropertyKey var1, V var2);

   <V> V getProperty(Http2Connection.PropertyKey var1);

   <V> V removeProperty(Http2Connection.PropertyKey var1);

   Http2Stream headersSent(boolean var1);

   boolean isHeadersSent();

   boolean isTrailersSent();

   Http2Stream headersReceived(boolean var1);

   boolean isHeadersReceived();

   boolean isTrailersReceived();

   Http2Stream pushPromiseSent();

   boolean isPushPromiseSent();

   public static enum State {
      IDLE(false, false),
      RESERVED_LOCAL(false, false),
      RESERVED_REMOTE(false, false),
      OPEN(true, true),
      HALF_CLOSED_LOCAL(false, true),
      HALF_CLOSED_REMOTE(true, false),
      CLOSED(false, false);

      private final boolean localSideOpen;
      private final boolean remoteSideOpen;

      private State(boolean var3, boolean var4) {
         this.localSideOpen = var3;
         this.remoteSideOpen = var4;
      }

      public boolean localSideOpen() {
         return this.localSideOpen;
      }

      public boolean remoteSideOpen() {
         return this.remoteSideOpen;
      }
   }
}
