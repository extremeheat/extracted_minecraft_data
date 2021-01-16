package io.netty.channel.unix;

import io.netty.util.internal.EmptyArrays;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;

public final class Errors {
   public static final int ERRNO_ENOENT_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoENOENT();
   public static final int ERRNO_ENOTCONN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoENOTCONN();
   public static final int ERRNO_EBADF_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEBADF();
   public static final int ERRNO_EPIPE_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEPIPE();
   public static final int ERRNO_ECONNRESET_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoECONNRESET();
   public static final int ERRNO_EAGAIN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEAGAIN();
   public static final int ERRNO_EWOULDBLOCK_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEWOULDBLOCK();
   public static final int ERRNO_EINPROGRESS_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errnoEINPROGRESS();
   public static final int ERROR_ECONNREFUSED_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorECONNREFUSED();
   public static final int ERROR_EISCONN_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEISCONN();
   public static final int ERROR_EALREADY_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorEALREADY();
   public static final int ERROR_ENETUNREACH_NEGATIVE = -ErrorsStaticallyReferencedJniMethods.errorENETUNREACH();
   private static final String[] ERRORS = new String[512];

   static void throwConnectException(String var0, Errors.NativeConnectException var1, int var2) throws IOException {
      if (var2 == var1.expectedErr()) {
         throw var1;
      } else if (var2 == ERROR_EALREADY_NEGATIVE) {
         throw new ConnectionPendingException();
      } else if (var2 == ERROR_ENETUNREACH_NEGATIVE) {
         throw new NoRouteToHostException();
      } else if (var2 == ERROR_EISCONN_NEGATIVE) {
         throw new AlreadyConnectedException();
      } else if (var2 == ERRNO_ENOENT_NEGATIVE) {
         throw new FileNotFoundException();
      } else {
         throw new ConnectException(var0 + "(..) failed: " + ERRORS[-var2]);
      }
   }

   public static Errors.NativeIoException newConnectionResetException(String var0, int var1) {
      Errors.NativeIoException var2 = newIOException(var0, var1);
      var2.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      return var2;
   }

   public static Errors.NativeIoException newIOException(String var0, int var1) {
      return new Errors.NativeIoException(var0, var1);
   }

   public static int ioResult(String var0, int var1, Errors.NativeIoException var2, ClosedChannelException var3) throws IOException {
      if (var1 != ERRNO_EAGAIN_NEGATIVE && var1 != ERRNO_EWOULDBLOCK_NEGATIVE) {
         if (var1 == var2.expectedErr()) {
            throw var2;
         } else if (var1 == ERRNO_EBADF_NEGATIVE) {
            throw var3;
         } else if (var1 == ERRNO_ENOTCONN_NEGATIVE) {
            throw new NotYetConnectedException();
         } else if (var1 == ERRNO_ENOENT_NEGATIVE) {
            throw new FileNotFoundException();
         } else {
            throw newIOException(var0, var1);
         }
      } else {
         return 0;
      }
   }

   private Errors() {
      super();
   }

   static {
      for(int var0 = 0; var0 < ERRORS.length; ++var0) {
         ERRORS[var0] = ErrorsStaticallyReferencedJniMethods.strError(var0);
      }

   }

   static final class NativeConnectException extends ConnectException {
      private static final long serialVersionUID = -5532328671712318161L;
      private final int expectedErr;

      NativeConnectException(String var1, int var2) {
         super(var1 + "(..) failed: " + Errors.ERRORS[-var2]);
         this.expectedErr = var2;
      }

      int expectedErr() {
         return this.expectedErr;
      }
   }

   public static final class NativeIoException extends IOException {
      private static final long serialVersionUID = 8222160204268655526L;
      private final int expectedErr;

      public NativeIoException(String var1, int var2) {
         super(var1 + "(..) failed: " + Errors.ERRORS[-var2]);
         this.expectedErr = var2;
      }

      public int expectedErr() {
         return this.expectedErr;
      }
   }
}
