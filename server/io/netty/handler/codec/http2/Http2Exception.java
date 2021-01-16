package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Http2Exception extends Exception {
   private static final long serialVersionUID = -6941186345430164209L;
   private final Http2Error error;
   private final Http2Exception.ShutdownHint shutdownHint;

   public Http2Exception(Http2Error var1) {
      this(var1, Http2Exception.ShutdownHint.HARD_SHUTDOWN);
   }

   public Http2Exception(Http2Error var1, Http2Exception.ShutdownHint var2) {
      super();
      this.error = (Http2Error)ObjectUtil.checkNotNull(var1, "error");
      this.shutdownHint = (Http2Exception.ShutdownHint)ObjectUtil.checkNotNull(var2, "shutdownHint");
   }

   public Http2Exception(Http2Error var1, String var2) {
      this(var1, var2, Http2Exception.ShutdownHint.HARD_SHUTDOWN);
   }

   public Http2Exception(Http2Error var1, String var2, Http2Exception.ShutdownHint var3) {
      super(var2);
      this.error = (Http2Error)ObjectUtil.checkNotNull(var1, "error");
      this.shutdownHint = (Http2Exception.ShutdownHint)ObjectUtil.checkNotNull(var3, "shutdownHint");
   }

   public Http2Exception(Http2Error var1, String var2, Throwable var3) {
      this(var1, var2, var3, Http2Exception.ShutdownHint.HARD_SHUTDOWN);
   }

   public Http2Exception(Http2Error var1, String var2, Throwable var3, Http2Exception.ShutdownHint var4) {
      super(var2, var3);
      this.error = (Http2Error)ObjectUtil.checkNotNull(var1, "error");
      this.shutdownHint = (Http2Exception.ShutdownHint)ObjectUtil.checkNotNull(var4, "shutdownHint");
   }

   public Http2Error error() {
      return this.error;
   }

   public Http2Exception.ShutdownHint shutdownHint() {
      return this.shutdownHint;
   }

   public static Http2Exception connectionError(Http2Error var0, String var1, Object... var2) {
      return new Http2Exception(var0, String.format(var1, var2));
   }

   public static Http2Exception connectionError(Http2Error var0, Throwable var1, String var2, Object... var3) {
      return new Http2Exception(var0, String.format(var2, var3), var1);
   }

   public static Http2Exception closedStreamError(Http2Error var0, String var1, Object... var2) {
      return new Http2Exception.ClosedStreamCreationException(var0, String.format(var1, var2));
   }

   public static Http2Exception streamError(int var0, Http2Error var1, String var2, Object... var3) {
      return (Http2Exception)(0 == var0 ? connectionError(var1, var2, var3) : new Http2Exception.StreamException(var0, var1, String.format(var2, var3)));
   }

   public static Http2Exception streamError(int var0, Http2Error var1, Throwable var2, String var3, Object... var4) {
      return (Http2Exception)(0 == var0 ? connectionError(var1, var2, var3, var4) : new Http2Exception.StreamException(var0, var1, String.format(var3, var4), var2));
   }

   public static Http2Exception headerListSizeError(int var0, Http2Error var1, boolean var2, String var3, Object... var4) {
      return (Http2Exception)(0 == var0 ? connectionError(var1, var3, var4) : new Http2Exception.HeaderListSizeException(var0, var1, String.format(var3, var4), var2));
   }

   public static boolean isStreamError(Http2Exception var0) {
      return var0 instanceof Http2Exception.StreamException;
   }

   public static int streamId(Http2Exception var0) {
      return isStreamError(var0) ? ((Http2Exception.StreamException)var0).streamId() : 0;
   }

   public static final class CompositeStreamException extends Http2Exception implements Iterable<Http2Exception.StreamException> {
      private static final long serialVersionUID = 7091134858213711015L;
      private final List<Http2Exception.StreamException> exceptions;

      public CompositeStreamException(Http2Error var1, int var2) {
         super(var1, Http2Exception.ShutdownHint.NO_SHUTDOWN);
         this.exceptions = new ArrayList(var2);
      }

      public void add(Http2Exception.StreamException var1) {
         this.exceptions.add(var1);
      }

      public Iterator<Http2Exception.StreamException> iterator() {
         return this.exceptions.iterator();
      }
   }

   public static final class HeaderListSizeException extends Http2Exception.StreamException {
      private static final long serialVersionUID = -8807603212183882637L;
      private final boolean decode;

      HeaderListSizeException(int var1, Http2Error var2, String var3, boolean var4) {
         super(var1, var2, var3);
         this.decode = var4;
      }

      public boolean duringDecode() {
         return this.decode;
      }
   }

   public static class StreamException extends Http2Exception {
      private static final long serialVersionUID = 602472544416984384L;
      private final int streamId;

      StreamException(int var1, Http2Error var2, String var3) {
         super(var2, var3, Http2Exception.ShutdownHint.NO_SHUTDOWN);
         this.streamId = var1;
      }

      StreamException(int var1, Http2Error var2, String var3, Throwable var4) {
         super(var2, var3, var4, Http2Exception.ShutdownHint.NO_SHUTDOWN);
         this.streamId = var1;
      }

      public int streamId() {
         return this.streamId;
      }
   }

   public static final class ClosedStreamCreationException extends Http2Exception {
      private static final long serialVersionUID = -6746542974372246206L;

      public ClosedStreamCreationException(Http2Error var1) {
         super(var1);
      }

      public ClosedStreamCreationException(Http2Error var1, String var2) {
         super(var1, var2);
      }

      public ClosedStreamCreationException(Http2Error var1, String var2, Throwable var3) {
         super(var1, var2, var3);
      }
   }

   public static enum ShutdownHint {
      NO_SHUTDOWN,
      GRACEFUL_SHUTDOWN,
      HARD_SHUTDOWN;

      private ShutdownHint() {
      }
   }
}
