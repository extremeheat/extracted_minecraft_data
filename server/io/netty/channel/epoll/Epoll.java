package io.netty.channel.epoll;

import io.netty.channel.unix.FileDescriptor;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;

public final class Epoll {
   private static final Throwable UNAVAILABILITY_CAUSE;

   public static boolean isAvailable() {
      return UNAVAILABILITY_CAUSE == null;
   }

   public static void ensureAvailability() {
      if (UNAVAILABILITY_CAUSE != null) {
         throw (Error)(new UnsatisfiedLinkError("failed to load the required native library")).initCause(UNAVAILABILITY_CAUSE);
      }
   }

   public static Throwable unavailabilityCause() {
      return UNAVAILABILITY_CAUSE;
   }

   private Epoll() {
      super();
   }

   static {
      Object var0 = null;
      if (SystemPropertyUtil.getBoolean("io.netty.transport.noNative", false)) {
         var0 = new UnsupportedOperationException("Native transport was explicit disabled with -Dio.netty.transport.noNative=true");
      } else {
         FileDescriptor var1 = null;
         FileDescriptor var2 = null;

         try {
            var1 = Native.newEpollCreate();
            var2 = Native.newEventFd();
         } catch (Throwable var16) {
            var0 = var16;
         } finally {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Exception var15) {
               }
            }

            if (var2 != null) {
               try {
                  var2.close();
               } catch (Exception var14) {
               }
            }

         }
      }

      if (var0 != null) {
         UNAVAILABILITY_CAUSE = (Throwable)var0;
      } else {
         UNAVAILABILITY_CAUSE = PlatformDependent.hasUnsafe() ? null : new IllegalStateException("sun.misc.Unsafe not available", PlatformDependent.getUnsafeUnavailabilityCause());
      }

   }
}
