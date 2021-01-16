package it.unimi.dsi.fastutil.io;

import it.unimi.dsi.fastutil.bytes.ByteArrays;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.FileChannel;
import java.util.EnumSet;

public class FastBufferedInputStream extends MeasurableInputStream implements RepositionableStream {
   public static final int DEFAULT_BUFFER_SIZE = 8192;
   public static final EnumSet<FastBufferedInputStream.LineTerminator> ALL_TERMINATORS = EnumSet.allOf(FastBufferedInputStream.LineTerminator.class);
   protected InputStream is;
   protected byte[] buffer;
   protected int pos;
   protected long readBytes;
   protected int avail;
   private FileChannel fileChannel;
   private RepositionableStream repositionableStream;
   private MeasurableStream measurableStream;

   private static int ensureBufferSize(int var0) {
      if (var0 <= 0) {
         throw new IllegalArgumentException("Illegal buffer size: " + var0);
      } else {
         return var0;
      }
   }

   public FastBufferedInputStream(InputStream var1, byte[] var2) {
      super();
      this.is = var1;
      ensureBufferSize(var2.length);
      this.buffer = var2;
      if (var1 instanceof RepositionableStream) {
         this.repositionableStream = (RepositionableStream)var1;
      }

      if (var1 instanceof MeasurableStream) {
         this.measurableStream = (MeasurableStream)var1;
      }

      if (this.repositionableStream == null) {
         try {
            this.fileChannel = (FileChannel)var1.getClass().getMethod("getChannel").invoke(var1);
         } catch (IllegalAccessException var4) {
         } catch (IllegalArgumentException var5) {
         } catch (NoSuchMethodException var6) {
         } catch (InvocationTargetException var7) {
         } catch (ClassCastException var8) {
         }
      }

   }

   public FastBufferedInputStream(InputStream var1, int var2) {
      this(var1, new byte[ensureBufferSize(var2)]);
   }

   public FastBufferedInputStream(InputStream var1) {
      this(var1, 8192);
   }

   protected boolean noMoreCharacters() throws IOException {
      if (this.avail == 0) {
         this.avail = this.is.read(this.buffer);
         if (this.avail <= 0) {
            this.avail = 0;
            return true;
         }

         this.pos = 0;
      }

      return false;
   }

   public int read() throws IOException {
      if (this.noMoreCharacters()) {
         return -1;
      } else {
         --this.avail;
         ++this.readBytes;
         return this.buffer[this.pos++] & 255;
      }
   }

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (var3 <= this.avail) {
         System.arraycopy(this.buffer, this.pos, var1, var2, var3);
         this.pos += var3;
         this.avail -= var3;
         this.readBytes += (long)var3;
         return var3;
      } else {
         int var4 = this.avail;
         System.arraycopy(this.buffer, this.pos, var1, var2, var4);
         this.pos = this.avail = 0;
         this.readBytes += (long)var4;
         int var5;
         if (var3 > this.buffer.length) {
            var5 = this.is.read(var1, var2 + var4, var3 - var4);
            if (var5 > 0) {
               this.readBytes += (long)var5;
            }

            return var5 < 0 ? (var4 == 0 ? -1 : var4) : var5 + var4;
         } else if (this.noMoreCharacters()) {
            return var4 == 0 ? -1 : var4;
         } else {
            var5 = Math.min(var3 - var4, this.avail);
            this.readBytes += (long)var5;
            System.arraycopy(this.buffer, 0, var1, var2 + var4, var5);
            this.pos = var5;
            this.avail -= var5;
            return var5 + var4;
         }
      }
   }

   public int readLine(byte[] var1) throws IOException {
      return this.readLine(var1, 0, var1.length, ALL_TERMINATORS);
   }

   public int readLine(byte[] var1, EnumSet<FastBufferedInputStream.LineTerminator> var2) throws IOException {
      return this.readLine(var1, 0, var1.length, var2);
   }

   public int readLine(byte[] var1, int var2, int var3) throws IOException {
      return this.readLine(var1, var2, var3, ALL_TERMINATORS);
   }

   public int readLine(byte[] var1, int var2, int var3, EnumSet<FastBufferedInputStream.LineTerminator> var4) throws IOException {
      ByteArrays.ensureOffsetLength(var1, var2, var3);
      if (var3 == 0) {
         return 0;
      } else if (this.noMoreCharacters()) {
         return -1;
      } else {
         byte var6 = 0;
         int var7 = var3;
         int var8 = 0;

         while(true) {
            int var5;
            for(var5 = 0; var5 < this.avail && var5 < var7 && (var6 = this.buffer[this.pos + var5]) != 10 && var6 != 13; ++var5) {
            }

            System.arraycopy(this.buffer, this.pos, var1, var2 + var8, var5);
            this.pos += var5;
            this.avail -= var5;
            var8 += var5;
            var7 -= var5;
            if (var7 == 0) {
               this.readBytes += (long)var8;
               return var8;
            }

            if (this.avail > 0) {
               if (var6 == 10) {
                  ++this.pos;
                  --this.avail;
                  if (var4.contains(FastBufferedInputStream.LineTerminator.LF)) {
                     this.readBytes += (long)(var8 + 1);
                     return var8;
                  }

                  var1[var2 + var8++] = 10;
                  --var7;
               } else if (var6 == 13) {
                  ++this.pos;
                  --this.avail;
                  if (var4.contains(FastBufferedInputStream.LineTerminator.CR_LF)) {
                     if (this.avail > 0) {
                        if (this.buffer[this.pos] == 10) {
                           ++this.pos;
                           --this.avail;
                           this.readBytes += (long)(var8 + 2);
                           return var8;
                        }
                     } else {
                        if (this.noMoreCharacters()) {
                           if (!var4.contains(FastBufferedInputStream.LineTerminator.CR)) {
                              var1[var2 + var8++] = 13;
                              --var7;
                              this.readBytes += (long)var8;
                           } else {
                              this.readBytes += (long)(var8 + 1);
                           }

                           return var8;
                        }

                        if (this.buffer[0] == 10) {
                           ++this.pos;
                           --this.avail;
                           this.readBytes += (long)(var8 + 2);
                           return var8;
                        }
                     }
                  }

                  if (var4.contains(FastBufferedInputStream.LineTerminator.CR)) {
                     this.readBytes += (long)(var8 + 1);
                     return var8;
                  }

                  var1[var2 + var8++] = 13;
                  --var7;
               }
            } else if (this.noMoreCharacters()) {
               this.readBytes += (long)var8;
               return var8;
            }
         }
      }
   }

   public void position(long var1) throws IOException {
      long var3 = this.readBytes;
      if (var1 <= var3 + (long)this.avail && var1 >= var3 - (long)this.pos) {
         this.pos = (int)((long)this.pos + (var1 - var3));
         this.avail = (int)((long)this.avail - (var1 - var3));
         this.readBytes = var1;
      } else {
         if (this.repositionableStream != null) {
            this.repositionableStream.position(var1);
         } else {
            if (this.fileChannel == null) {
               throw new UnsupportedOperationException("position() can only be called if the underlying byte stream implements the RepositionableStream interface or if the getChannel() method of the underlying byte stream exists and returns a FileChannel");
            }

            this.fileChannel.position(var1);
         }

         this.readBytes = var1;
         this.avail = this.pos = 0;
      }
   }

   public long position() throws IOException {
      return this.readBytes;
   }

   public long length() throws IOException {
      if (this.measurableStream != null) {
         return this.measurableStream.length();
      } else if (this.fileChannel != null) {
         return this.fileChannel.size();
      } else {
         throw new UnsupportedOperationException();
      }
   }

   private long skipByReading(long var1) throws IOException {
      long var3;
      int var5;
      for(var3 = var1; var3 > 0L; var3 -= (long)var5) {
         var5 = this.is.read(this.buffer, 0, (int)Math.min((long)this.buffer.length, var3));
         if (var5 <= 0) {
            break;
         }
      }

      return var1 - var3;
   }

   public long skip(long var1) throws IOException {
      if (var1 <= (long)this.avail) {
         int var9 = (int)var1;
         this.pos += var9;
         this.avail -= var9;
         this.readBytes += var1;
         return var1;
      } else {
         long var3 = var1 - (long)this.avail;
         long var5 = 0L;
         this.avail = 0;

         while(var3 != 0L && (var5 = this.is == System.in ? this.skipByReading(var3) : this.is.skip(var3)) < var3) {
            if (var5 == 0L) {
               if (this.is.read() == -1) {
                  break;
               }

               --var3;
            } else {
               var3 -= var5;
            }
         }

         long var7 = var1 - (var3 - var5);
         this.readBytes += var7;
         return var7;
      }
   }

   public int available() throws IOException {
      return (int)Math.min((long)this.is.available() + (long)this.avail, 2147483647L);
   }

   public void close() throws IOException {
      if (this.is != null) {
         if (this.is != System.in) {
            this.is.close();
         }

         this.is = null;
         this.buffer = null;
      }
   }

   public void flush() {
      if (this.is != null) {
         this.readBytes += (long)this.avail;
         this.avail = this.pos = 0;
      }
   }

   /** @deprecated */
   @Deprecated
   public void reset() {
      this.flush();
   }

   public static enum LineTerminator {
      CR,
      LF,
      CR_LF;

      private LineTerminator() {
      }
   }
}
