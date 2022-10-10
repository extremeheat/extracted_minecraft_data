package net.minecraft.client.renderer.texture;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import net.minecraft.resources.IResource;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public class PngSizeInfo {
   public final int field_188533_a;
   public final int field_188534_b;

   public PngSizeInfo(IResource var1) throws IOException {
      super();
      MemoryStack var2 = MemoryStack.stackPush();
      Throwable var3 = null;

      try {
         PngSizeInfo.Reader var4 = func_195695_a(var1.func_199027_b());
         Throwable var5 = null;

         try {
            var4.getClass();
            STBIReadCallback var6 = STBIReadCallback.create(var4::func_195682_a);
            Throwable var7 = null;

            try {
               var4.getClass();
               STBISkipCallback var8 = STBISkipCallback.create(var4::func_195686_a);
               Throwable var9 = null;

               try {
                  var4.getClass();
                  STBIEOFCallback var10 = STBIEOFCallback.create(var4::func_195685_a);
                  Throwable var11 = null;

                  try {
                     STBIIOCallbacks var12 = STBIIOCallbacks.mallocStack(var2);
                     var12.read(var6);
                     var12.skip(var8);
                     var12.eof(var10);
                     IntBuffer var13 = var2.mallocInt(1);
                     IntBuffer var14 = var2.mallocInt(1);
                     IntBuffer var15 = var2.mallocInt(1);
                     if (!STBImage.stbi_info_from_callbacks(var12, 0L, var13, var14, var15)) {
                        throw new IOException("Could not read info from the PNG file " + var1 + " " + STBImage.stbi_failure_reason());
                     }

                     this.field_188533_a = var13.get(0);
                     this.field_188534_b = var14.get(0);
                  } catch (Throwable var121) {
                     var11 = var121;
                     throw var121;
                  } finally {
                     if (var10 != null) {
                        if (var11 != null) {
                           try {
                              var10.close();
                           } catch (Throwable var120) {
                              var11.addSuppressed(var120);
                           }
                        } else {
                           var10.close();
                        }
                     }

                  }
               } catch (Throwable var123) {
                  var9 = var123;
                  throw var123;
               } finally {
                  if (var8 != null) {
                     if (var9 != null) {
                        try {
                           var8.close();
                        } catch (Throwable var119) {
                           var9.addSuppressed(var119);
                        }
                     } else {
                        var8.close();
                     }
                  }

               }
            } catch (Throwable var125) {
               var7 = var125;
               throw var125;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var118) {
                        var7.addSuppressed(var118);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var127) {
            var5 = var127;
            throw var127;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var117) {
                     var5.addSuppressed(var117);
                  }
               } else {
                  var4.close();
               }
            }

         }
      } catch (Throwable var129) {
         var3 = var129;
         throw var129;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var116) {
                  var3.addSuppressed(var116);
               }
            } else {
               var2.close();
            }
         }

      }

   }

   private static PngSizeInfo.Reader func_195695_a(InputStream var0) {
      return (PngSizeInfo.Reader)(var0 instanceof FileInputStream ? new PngSizeInfo.ReaderSeekable(((FileInputStream)var0).getChannel()) : new PngSizeInfo.ReaderBuffer(Channels.newChannel(var0)));
   }

   static class ReaderBuffer extends PngSizeInfo.Reader {
      private final ReadableByteChannel field_195689_b;
      private long field_195690_c;
      private int field_195691_d;
      private int field_195692_e;
      private int field_195693_f;

      private ReaderBuffer(ReadableByteChannel var1) {
         super(null);
         this.field_195690_c = MemoryUtil.nmemAlloc(128L);
         this.field_195691_d = 128;
         this.field_195689_b = var1;
      }

      private void func_195688_b(int var1) throws IOException {
         ByteBuffer var2 = MemoryUtil.memByteBuffer(this.field_195690_c, this.field_195691_d);
         if (var1 + this.field_195693_f > this.field_195691_d) {
            this.field_195691_d = var1 + this.field_195693_f;
            var2 = MemoryUtil.memRealloc(var2, this.field_195691_d);
            this.field_195690_c = MemoryUtil.memAddress(var2);
         }

         var2.position(this.field_195692_e);

         while(var1 + this.field_195693_f > this.field_195692_e) {
            try {
               int var3 = this.field_195689_b.read(var2);
               if (var3 == -1) {
                  break;
               }
            } finally {
               this.field_195692_e = var2.position();
            }
         }

      }

      public int func_195683_b(long var1, int var3) throws IOException {
         this.func_195688_b(var3);
         if (var3 + this.field_195693_f > this.field_195692_e) {
            var3 = this.field_195692_e - this.field_195693_f;
         }

         MemoryUtil.memCopy(this.field_195690_c + (long)this.field_195693_f, var1, (long)var3);
         this.field_195693_f += var3;
         return var3;
      }

      public void func_195684_a(int var1) throws IOException {
         if (var1 > 0) {
            this.func_195688_b(var1);
            if (var1 + this.field_195693_f > this.field_195692_e) {
               throw new EOFException("Can't skip past the EOF.");
            }
         }

         if (this.field_195693_f + var1 < 0) {
            throw new IOException("Can't seek before the beginning: " + (this.field_195693_f + var1));
         } else {
            this.field_195693_f += var1;
         }
      }

      public void close() throws IOException {
         MemoryUtil.nmemFree(this.field_195690_c);
         this.field_195689_b.close();
      }

      // $FF: synthetic method
      ReaderBuffer(ReadableByteChannel var1, Object var2) {
         this(var1);
      }
   }

   static class ReaderSeekable extends PngSizeInfo.Reader {
      private final SeekableByteChannel field_195694_b;

      private ReaderSeekable(SeekableByteChannel var1) {
         super(null);
         this.field_195694_b = var1;
      }

      public int func_195683_b(long var1, int var3) throws IOException {
         ByteBuffer var4 = MemoryUtil.memByteBuffer(var1, var3);
         return this.field_195694_b.read(var4);
      }

      public void func_195684_a(int var1) throws IOException {
         this.field_195694_b.position(this.field_195694_b.position() + (long)var1);
      }

      public int func_195685_a(long var1) {
         return super.func_195685_a(var1) != 0 && this.field_195694_b.isOpen() ? 1 : 0;
      }

      public void close() throws IOException {
         this.field_195694_b.close();
      }

      // $FF: synthetic method
      ReaderSeekable(SeekableByteChannel var1, Object var2) {
         this(var1);
      }
   }

   abstract static class Reader implements AutoCloseable {
      protected boolean field_195687_a;

      private Reader() {
         super();
      }

      int func_195682_a(long var1, long var3, int var5) {
         try {
            return this.func_195683_b(var3, var5);
         } catch (IOException var7) {
            this.field_195687_a = true;
            return 0;
         }
      }

      void func_195686_a(long var1, int var3) {
         try {
            this.func_195684_a(var3);
         } catch (IOException var5) {
            this.field_195687_a = true;
         }

      }

      int func_195685_a(long var1) {
         return this.field_195687_a ? 1 : 0;
      }

      protected abstract int func_195683_b(long var1, int var3) throws IOException;

      protected abstract void func_195684_a(int var1) throws IOException;

      public abstract void close() throws IOException;

      // $FF: synthetic method
      Reader(Object var1) {
         this();
      }
   }
}
