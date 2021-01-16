package io.netty.channel.kqueue;

import io.netty.channel.unix.Errors;
import io.netty.channel.unix.FileDescriptor;
import io.netty.channel.unix.Socket;
import io.netty.util.internal.NativeLibraryLoader;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.ThrowableUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.util.Locale;

final class Native {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(Native.class);
   static final short EV_ADD;
   static final short EV_ENABLE;
   static final short EV_DISABLE;
   static final short EV_DELETE;
   static final short EV_CLEAR;
   static final short EV_ERROR;
   static final short EV_EOF;
   static final int NOTE_READCLOSED;
   static final int NOTE_CONNRESET;
   static final int NOTE_DISCONNECTED;
   static final int NOTE_RDHUP;
   static final short EV_ADD_CLEAR_ENABLE;
   static final short EV_DELETE_DISABLE;
   static final short EVFILT_READ;
   static final short EVFILT_WRITE;
   static final short EVFILT_USER;
   static final short EVFILT_SOCK;

   static FileDescriptor newKQueue() {
      return new FileDescriptor(kqueueCreate());
   }

   static int keventWait(int var0, KQueueEventArray var1, KQueueEventArray var2, int var3, int var4) throws IOException {
      int var5 = keventWait(var0, var1.memoryAddress(), var1.size(), var2.memoryAddress(), var2.capacity(), var3, var4);
      if (var5 < 0) {
         throw Errors.newIOException("kevent", var5);
      } else {
         return var5;
      }
   }

   private static native int kqueueCreate();

   private static native int keventWait(int var0, long var1, int var3, long var4, int var6, int var7, int var8);

   static native int keventTriggerUserEvent(int var0, int var1);

   static native int keventAddUserEvent(int var0, int var1);

   static native int sizeofKEvent();

   static native int offsetofKEventIdent();

   static native int offsetofKEventFlags();

   static native int offsetofKEventFFlags();

   static native int offsetofKEventFilter();

   static native int offsetofKeventData();

   private static void loadNativeLibrary() {
      String var0 = SystemPropertyUtil.get("os.name").toLowerCase(Locale.UK).trim();
      if (!var0.startsWith("mac") && !var0.contains("bsd") && !var0.startsWith("darwin")) {
         throw new IllegalStateException("Only supported on BSD");
      } else {
         String var1 = "netty_transport_native_kqueue";
         String var2 = var1 + '_' + PlatformDependent.normalizedArch();
         ClassLoader var3 = PlatformDependent.getClassLoader(Native.class);

         try {
            NativeLibraryLoader.load(var2, var3);
         } catch (UnsatisfiedLinkError var7) {
            UnsatisfiedLinkError var4 = var7;

            try {
               NativeLibraryLoader.load(var1, var3);
               logger.debug("Failed to load {}", var2, var4);
            } catch (UnsatisfiedLinkError var6) {
               ThrowableUtil.addSuppressed(var7, (Throwable)var6);
               throw var7;
            }
         }

      }
   }

   private Native() {
      super();
   }

   static {
      try {
         sizeofKEvent();
      } catch (UnsatisfiedLinkError var1) {
         loadNativeLibrary();
      }

      Socket.initialize();
      EV_ADD = KQueueStaticallyReferencedJniMethods.evAdd();
      EV_ENABLE = KQueueStaticallyReferencedJniMethods.evEnable();
      EV_DISABLE = KQueueStaticallyReferencedJniMethods.evDisable();
      EV_DELETE = KQueueStaticallyReferencedJniMethods.evDelete();
      EV_CLEAR = KQueueStaticallyReferencedJniMethods.evClear();
      EV_ERROR = KQueueStaticallyReferencedJniMethods.evError();
      EV_EOF = KQueueStaticallyReferencedJniMethods.evEOF();
      NOTE_READCLOSED = KQueueStaticallyReferencedJniMethods.noteReadClosed();
      NOTE_CONNRESET = KQueueStaticallyReferencedJniMethods.noteConnReset();
      NOTE_DISCONNECTED = KQueueStaticallyReferencedJniMethods.noteDisconnected();
      NOTE_RDHUP = NOTE_READCLOSED | NOTE_CONNRESET | NOTE_DISCONNECTED;
      EV_ADD_CLEAR_ENABLE = (short)(EV_ADD | EV_CLEAR | EV_ENABLE);
      EV_DELETE_DISABLE = (short)(EV_DELETE | EV_DISABLE);
      EVFILT_READ = KQueueStaticallyReferencedJniMethods.evfiltRead();
      EVFILT_WRITE = KQueueStaticallyReferencedJniMethods.evfiltWrite();
      EVFILT_USER = KQueueStaticallyReferencedJniMethods.evfiltUser();
      EVFILT_SOCK = KQueueStaticallyReferencedJniMethods.evfiltSock();
   }
}
