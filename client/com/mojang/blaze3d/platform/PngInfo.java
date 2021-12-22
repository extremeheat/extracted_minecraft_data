package com.mojang.blaze3d.platform;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.util.Objects;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class PngInfo {
   public final int width;
   public final int height;

   public PngInfo(String var1, InputStream var2) throws IOException {
      super();
      MemoryStack var3 = MemoryStack.stackPush();

      try {
         PngInfo.StbReader var4 = createCallbacks(var2);

         try {
            Objects.requireNonNull(var4);
            STBIReadCallback var5 = STBIReadCallback.create(var4::read);

            try {
               Objects.requireNonNull(var4);
               STBISkipCallback var6 = STBISkipCallback.create(var4::skip);

               try {
                  Objects.requireNonNull(var4);
                  STBIEOFCallback var7 = STBIEOFCallback.create(var4::eof);

                  try {
                     STBIIOCallbacks var8 = STBIIOCallbacks.mallocStack(var3);
                     var8.read(var5);
                     var8.skip(var6);
                     var8.eof(var7);
                     IntBuffer var9 = var3.mallocInt(1);
                     IntBuffer var10 = var3.mallocInt(1);
                     IntBuffer var11 = var3.mallocInt(1);
                     if (!STBImage.stbi_info_from_callbacks(var8, 0L, var9, var10, var11)) {
                        throw new IOException("Could not read info from the PNG file " + var1 + " " + STBImage.stbi_failure_reason());
                     }

                     this.width = var9.get(0);
                     this.height = var10.get(0);
                  } catch (Throwable var17) {
                     if (var7 != null) {
                        try {
                           var7.close();
                        } catch (Throwable var16) {
                           var17.addSuppressed(var16);
                        }
                     }

                     throw var17;
                  }

                  if (var7 != null) {
                     var7.close();
                  }
               } catch (Throwable var18) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var15) {
                        var18.addSuppressed(var15);
                     }
                  }

                  throw var18;
               }

               if (var6 != null) {
                  var6.close();
               }
            } catch (Throwable var19) {
               if (var5 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var14) {
                     var19.addSuppressed(var14);
                  }
               }

               throw var19;
            }

            if (var5 != null) {
               var5.close();
            }
         } catch (Throwable var20) {
            if (var4 != null) {
               try {
                  var4.close();
               } catch (Throwable var13) {
                  var20.addSuppressed(var13);
               }
            }

            throw var20;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (Throwable var21) {
         if (var3 != null) {
            try {
               var3.close();
            } catch (Throwable var12) {
               var21.addSuppressed(var12);
            }
         }

         throw var21;
      }

      if (var3 != null) {
         var3.close();
      }

   }

   private static PngInfo.StbReader createCallbacks(InputStream var0) {
      return (PngInfo.StbReader)(var0 instanceof FileInputStream ? new PngInfo.StbReaderSeekableByteChannel(((FileInputStream)var0).getChannel()) : new PngInfo.StbReaderBufferedChannel(Channels.newChannel(var0)));
   }

   private abstract static class StbReader implements AutoCloseable {
      protected boolean closed;

      StbReader() {
         super();
      }

      int read(long var1, long var3, int var5) {
         try {
            return this.read(var3, var5);
         } catch (IOException var7) {
            this.closed = true;
            return 0;
         }
      }

      void skip(long var1, int var3) {
         try {
            this.skip(var3);
         } catch (IOException var5) {
            this.closed = true;
         }

      }

      int eof(long var1) {
         return this.closed ? 1 : 0;
      }

      protected abstract int read(long var1, int var3) throws IOException;

      protected abstract void skip(int var1) throws IOException;

      public abstract void close() throws IOException;
   }

   private static class StbReaderSeekableByteChannel extends PngInfo.StbReader {
      private final SeekableByteChannel channel;

      StbReaderSeekableByteChannel(SeekableByteChannel var1) {
         super();
         this.channel = var1;
      }

      public int read(long var1, int var3) throws IOException {
         ByteBuffer var4 = MemoryUtil.memByteBuffer(var1, var3);
         return this.channel.read(var4);
      }

      public void skip(int var1) throws IOException {
         this.channel.position(this.channel.position() + (long)var1);
      }

      public int eof(long var1) {
         return super.eof(var1) != 0 && this.channel.isOpen() ? 1 : 0;
      }

      public void close() throws IOException {
         this.channel.close();
      }
   }

   private static class StbReaderBufferedChannel extends PngInfo.StbReader {
      private static final int START_BUFFER_SIZE = 128;
      private final ReadableByteChannel channel;
      private long readBufferAddress = MemoryUtil.nmemAlloc(128L);
      private int bufferSize = 128;
      private int read;
      private int consumed;

      StbReaderBufferedChannel(ReadableByteChannel var1) {
         super();
         this.channel = var1;
      }

      private void fillReadBuffer(int var1) throws IOException {
         ByteBuffer var2 = MemoryUtil.memByteBuffer(this.readBufferAddress, this.bufferSize);
         if (var1 + this.consumed > this.bufferSize) {
            this.bufferSize = var1 + this.consumed;
            var2 = MemoryUtil.memRealloc(var2, this.bufferSize);
            this.readBufferAddress = MemoryUtil.memAddress(var2);
         }

         var2.position(this.read);

         while(var1 + this.consumed > this.read) {
            try {
               int var3 = this.channel.read(var2);
               if (var3 == -1) {
                  break;
               }
            } finally {
               this.read = var2.position();
            }
         }

      }

      public int read(long var1, int var3) throws IOException {
         this.fillReadBuffer(var3);
         if (var3 + this.consumed > this.read) {
            var3 = this.read - this.consumed;
         }

         MemoryUtil.memCopy(this.readBufferAddress + (long)this.consumed, var1, (long)var3);
         this.consumed += var3;
         return var3;
      }

      public void skip(int var1) throws IOException {
         if (var1 > 0) {
            this.fillReadBuffer(var1);
            if (var1 + this.consumed > this.read) {
               throw new EOFException("Can't skip past the EOF.");
            }
         }

         if (this.consumed + var1 < 0) {
            int var10002 = this.consumed + var1;
            throw new IOException("Can't seek before the beginning: " + var10002);
         } else {
            this.consumed += var1;
         }
      }

      public void close() throws IOException {
         MemoryUtil.nmemFree(this.readBufferAddress);
         this.channel.close();
      }
   }
}
