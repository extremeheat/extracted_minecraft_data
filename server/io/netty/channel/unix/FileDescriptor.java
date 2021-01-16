package io.netty.channel.unix;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ThrowableUtil;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class FileDescriptor {
   private static final ClosedChannelException WRITE_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), FileDescriptor.class, "write(..)");
   private static final ClosedChannelException WRITE_ADDRESS_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), FileDescriptor.class, "writeAddress(..)");
   private static final ClosedChannelException WRITEV_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), FileDescriptor.class, "writev(..)");
   private static final ClosedChannelException WRITEV_ADDRESSES_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), FileDescriptor.class, "writevAddresses(..)");
   private static final ClosedChannelException READ_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), FileDescriptor.class, "read(..)");
   private static final ClosedChannelException READ_ADDRESS_CLOSED_CHANNEL_EXCEPTION = (ClosedChannelException)ThrowableUtil.unknownStackTrace(new ClosedChannelException(), FileDescriptor.class, "readAddress(..)");
   private static final Errors.NativeIoException WRITE_CONNECTION_RESET_EXCEPTION;
   private static final Errors.NativeIoException WRITE_ADDRESS_CONNECTION_RESET_EXCEPTION;
   private static final Errors.NativeIoException WRITEV_CONNECTION_RESET_EXCEPTION;
   private static final Errors.NativeIoException WRITEV_ADDRESSES_CONNECTION_RESET_EXCEPTION;
   private static final Errors.NativeIoException READ_CONNECTION_RESET_EXCEPTION;
   private static final Errors.NativeIoException READ_ADDRESS_CONNECTION_RESET_EXCEPTION;
   private static final AtomicIntegerFieldUpdater<FileDescriptor> stateUpdater;
   private static final int STATE_CLOSED_MASK = 1;
   private static final int STATE_INPUT_SHUTDOWN_MASK = 2;
   private static final int STATE_OUTPUT_SHUTDOWN_MASK = 4;
   private static final int STATE_ALL_MASK = 7;
   volatile int state;
   final int fd;

   public FileDescriptor(int var1) {
      super();
      if (var1 < 0) {
         throw new IllegalArgumentException("fd must be >= 0");
      } else {
         this.fd = var1;
      }
   }

   public final int intValue() {
      return this.fd;
   }

   public void close() throws IOException {
      int var1;
      do {
         var1 = this.state;
         if (isClosed(var1)) {
            return;
         }
      } while(!this.casState(var1, var1 | 7));

      var1 = close(this.fd);
      if (var1 < 0) {
         throw Errors.newIOException("close", var1);
      }
   }

   public boolean isOpen() {
      return !isClosed(this.state);
   }

   public final int write(ByteBuffer var1, int var2, int var3) throws IOException {
      int var4 = write(this.fd, var1, var2, var3);
      return var4 >= 0 ? var4 : Errors.ioResult("write", var4, WRITE_CONNECTION_RESET_EXCEPTION, WRITE_CLOSED_CHANNEL_EXCEPTION);
   }

   public final int writeAddress(long var1, int var3, int var4) throws IOException {
      int var5 = writeAddress(this.fd, var1, var3, var4);
      return var5 >= 0 ? var5 : Errors.ioResult("writeAddress", var5, WRITE_ADDRESS_CONNECTION_RESET_EXCEPTION, WRITE_ADDRESS_CLOSED_CHANNEL_EXCEPTION);
   }

   public final long writev(ByteBuffer[] var1, int var2, int var3, long var4) throws IOException {
      long var6 = writev(this.fd, var1, var2, Math.min(Limits.IOV_MAX, var3), var4);
      return var6 >= 0L ? var6 : (long)Errors.ioResult("writev", (int)var6, WRITEV_CONNECTION_RESET_EXCEPTION, WRITEV_CLOSED_CHANNEL_EXCEPTION);
   }

   public final long writevAddresses(long var1, int var3) throws IOException {
      long var4 = writevAddresses(this.fd, var1, var3);
      return var4 >= 0L ? var4 : (long)Errors.ioResult("writevAddresses", (int)var4, WRITEV_ADDRESSES_CONNECTION_RESET_EXCEPTION, WRITEV_ADDRESSES_CLOSED_CHANNEL_EXCEPTION);
   }

   public final int read(ByteBuffer var1, int var2, int var3) throws IOException {
      int var4 = read(this.fd, var1, var2, var3);
      if (var4 > 0) {
         return var4;
      } else {
         return var4 == 0 ? -1 : Errors.ioResult("read", var4, READ_CONNECTION_RESET_EXCEPTION, READ_CLOSED_CHANNEL_EXCEPTION);
      }
   }

   public final int readAddress(long var1, int var3, int var4) throws IOException {
      int var5 = readAddress(this.fd, var1, var3, var4);
      if (var5 > 0) {
         return var5;
      } else {
         return var5 == 0 ? -1 : Errors.ioResult("readAddress", var5, READ_ADDRESS_CONNECTION_RESET_EXCEPTION, READ_ADDRESS_CLOSED_CHANNEL_EXCEPTION);
      }
   }

   public String toString() {
      return "FileDescriptor{fd=" + this.fd + '}';
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FileDescriptor)) {
         return false;
      } else {
         return this.fd == ((FileDescriptor)var1).fd;
      }
   }

   public int hashCode() {
      return this.fd;
   }

   public static FileDescriptor from(String var0) throws IOException {
      ObjectUtil.checkNotNull(var0, "path");
      int var1 = open(var0);
      if (var1 < 0) {
         throw Errors.newIOException("open", var1);
      } else {
         return new FileDescriptor(var1);
      }
   }

   public static FileDescriptor from(File var0) throws IOException {
      return from(((File)ObjectUtil.checkNotNull(var0, "file")).getPath());
   }

   public static FileDescriptor[] pipe() throws IOException {
      long var0 = newPipe();
      if (var0 < 0L) {
         throw Errors.newIOException("newPipe", (int)var0);
      } else {
         return new FileDescriptor[]{new FileDescriptor((int)(var0 >>> 32)), new FileDescriptor((int)var0)};
      }
   }

   final boolean casState(int var1, int var2) {
      return stateUpdater.compareAndSet(this, var1, var2);
   }

   static boolean isClosed(int var0) {
      return (var0 & 1) != 0;
   }

   static boolean isInputShutdown(int var0) {
      return (var0 & 2) != 0;
   }

   static boolean isOutputShutdown(int var0) {
      return (var0 & 4) != 0;
   }

   static int inputShutdown(int var0) {
      return var0 | 2;
   }

   static int outputShutdown(int var0) {
      return var0 | 4;
   }

   private static native int open(String var0);

   private static native int close(int var0);

   private static native int write(int var0, ByteBuffer var1, int var2, int var3);

   private static native int writeAddress(int var0, long var1, int var3, int var4);

   private static native long writev(int var0, ByteBuffer[] var1, int var2, int var3, long var4);

   private static native long writevAddresses(int var0, long var1, int var3);

   private static native int read(int var0, ByteBuffer var1, int var2, int var3);

   private static native int readAddress(int var0, long var1, int var3, int var4);

   private static native long newPipe();

   static {
      WRITE_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:write", Errors.ERRNO_EPIPE_NEGATIVE), FileDescriptor.class, "write(..)");
      WRITE_ADDRESS_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:write", Errors.ERRNO_EPIPE_NEGATIVE), FileDescriptor.class, "writeAddress(..)");
      WRITEV_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:writev", Errors.ERRNO_EPIPE_NEGATIVE), FileDescriptor.class, "writev(..)");
      WRITEV_ADDRESSES_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:writev", Errors.ERRNO_EPIPE_NEGATIVE), FileDescriptor.class, "writeAddresses(..)");
      READ_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:read", Errors.ERRNO_ECONNRESET_NEGATIVE), FileDescriptor.class, "read(..)");
      READ_ADDRESS_CONNECTION_RESET_EXCEPTION = (Errors.NativeIoException)ThrowableUtil.unknownStackTrace(Errors.newConnectionResetException("syscall:read", Errors.ERRNO_ECONNRESET_NEGATIVE), FileDescriptor.class, "readAddress(..)");
      stateUpdater = AtomicIntegerFieldUpdater.newUpdater(FileDescriptor.class, "state");
   }
}
