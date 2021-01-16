package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public abstract class AbstractDiskHttpData extends AbstractHttpData {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractDiskHttpData.class);
   private File file;
   private boolean isRenamed;
   private FileChannel fileChannel;

   protected AbstractDiskHttpData(String var1, Charset var2, long var3) {
      super(var1, var2, var3);
   }

   protected abstract String getDiskFilename();

   protected abstract String getPrefix();

   protected abstract String getBaseDirectory();

   protected abstract String getPostfix();

   protected abstract boolean deleteOnExit();

   private File tempFile() throws IOException {
      String var2 = this.getDiskFilename();
      String var1;
      if (var2 != null) {
         var1 = '_' + var2;
      } else {
         var1 = this.getPostfix();
      }

      File var3;
      if (this.getBaseDirectory() == null) {
         var3 = File.createTempFile(this.getPrefix(), var1);
      } else {
         var3 = File.createTempFile(this.getPrefix(), var1, new File(this.getBaseDirectory()));
      }

      if (this.deleteOnExit()) {
         var3.deleteOnExit();
      }

      return var3;
   }

   public void setContent(ByteBuf var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("buffer");
      } else {
         try {
            this.size = (long)var1.readableBytes();
            this.checkSize(this.size);
            if (this.definedSize > 0L && this.definedSize < this.size) {
               throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
            }

            if (this.file == null) {
               this.file = this.tempFile();
            }

            if (var1.readableBytes() == 0) {
               if (this.file.createNewFile()) {
                  return;
               }

               if (this.file.length() == 0L) {
                  return;
               }

               if (this.file.delete() && this.file.createNewFile()) {
                  return;
               }

               throw new IOException("file exists already: " + this.file);
            }

            FileOutputStream var2 = new FileOutputStream(this.file);

            try {
               FileChannel var3 = var2.getChannel();
               ByteBuffer var4 = var1.nioBuffer();

               int var5;
               for(var5 = 0; (long)var5 < this.size; var5 += var3.write(var4)) {
               }

               var1.readerIndex(var1.readerIndex() + var5);
               var3.force(false);
            } finally {
               var2.close();
            }

            this.setCompleted();
         } finally {
            var1.release();
         }

      }
   }

   public void addContent(ByteBuf var1, boolean var2) throws IOException {
      if (var1 != null) {
         try {
            int var3 = var1.readableBytes();
            this.checkSize(this.size + (long)var3);
            if (this.definedSize > 0L && this.definedSize < this.size + (long)var3) {
               throw new IOException("Out of size: " + (this.size + (long)var3) + " > " + this.definedSize);
            }

            ByteBuffer var4 = var1.nioBufferCount() == 1 ? var1.nioBuffer() : var1.copy().nioBuffer();
            int var5 = 0;
            if (this.file == null) {
               this.file = this.tempFile();
            }

            if (this.fileChannel == null) {
               FileOutputStream var6 = new FileOutputStream(this.file);
               this.fileChannel = var6.getChannel();
            }

            while(var5 < var3) {
               var5 += this.fileChannel.write(var4);
            }

            this.size += (long)var3;
            var1.readerIndex(var1.readerIndex() + var5);
         } finally {
            var1.release();
         }
      }

      if (var2) {
         if (this.file == null) {
            this.file = this.tempFile();
         }

         if (this.fileChannel == null) {
            FileOutputStream var10 = new FileOutputStream(this.file);
            this.fileChannel = var10.getChannel();
         }

         this.fileChannel.force(false);
         this.fileChannel.close();
         this.fileChannel = null;
         this.setCompleted();
      } else if (var1 == null) {
         throw new NullPointerException("buffer");
      }

   }

   public void setContent(File var1) throws IOException {
      if (this.file != null) {
         this.delete();
      }

      this.file = var1;
      this.size = var1.length();
      this.checkSize(this.size);
      this.isRenamed = true;
      this.setCompleted();
   }

   public void setContent(InputStream var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("inputStream");
      } else {
         if (this.file != null) {
            this.delete();
         }

         this.file = this.tempFile();
         FileOutputStream var2 = new FileOutputStream(this.file);
         int var3 = 0;

         try {
            FileChannel var4 = var2.getChannel();
            byte[] var5 = new byte[16384];
            ByteBuffer var6 = ByteBuffer.wrap(var5);
            int var7 = var1.read(var5);

            while(true) {
               if (var7 <= 0) {
                  var4.force(false);
                  break;
               }

               var6.position(var7).flip();
               var3 += var4.write(var6);
               this.checkSize((long)var3);
               var7 = var1.read(var5);
            }
         } finally {
            var2.close();
         }

         this.size = (long)var3;
         if (this.definedSize > 0L && this.definedSize < this.size) {
            if (!this.file.delete()) {
               logger.warn("Failed to delete: {}", (Object)this.file);
            }

            this.file = null;
            throw new IOException("Out of size: " + this.size + " > " + this.definedSize);
         } else {
            this.isRenamed = true;
            this.setCompleted();
         }
      }
   }

   public void delete() {
      if (this.fileChannel != null) {
         try {
            this.fileChannel.force(false);
            this.fileChannel.close();
         } catch (IOException var2) {
            logger.warn("Failed to close a file.", (Throwable)var2);
         }

         this.fileChannel = null;
      }

      if (!this.isRenamed) {
         if (this.file != null && this.file.exists() && !this.file.delete()) {
            logger.warn("Failed to delete: {}", (Object)this.file);
         }

         this.file = null;
      }

   }

   public byte[] get() throws IOException {
      return this.file == null ? EmptyArrays.EMPTY_BYTES : readFrom(this.file);
   }

   public ByteBuf getByteBuf() throws IOException {
      if (this.file == null) {
         return Unpooled.EMPTY_BUFFER;
      } else {
         byte[] var1 = readFrom(this.file);
         return Unpooled.wrappedBuffer(var1);
      }
   }

   public ByteBuf getChunk(int var1) throws IOException {
      if (this.file != null && var1 != 0) {
         if (this.fileChannel == null) {
            FileInputStream var2 = new FileInputStream(this.file);
            this.fileChannel = var2.getChannel();
         }

         int var5 = 0;

         ByteBuffer var3;
         int var4;
         for(var3 = ByteBuffer.allocate(var1); var5 < var1; var5 += var4) {
            var4 = this.fileChannel.read(var3);
            if (var4 == -1) {
               this.fileChannel.close();
               this.fileChannel = null;
               break;
            }
         }

         if (var5 == 0) {
            return Unpooled.EMPTY_BUFFER;
         } else {
            var3.flip();
            ByteBuf var6 = Unpooled.wrappedBuffer(var3);
            var6.readerIndex(0);
            var6.writerIndex(var5);
            return var6;
         }
      } else {
         return Unpooled.EMPTY_BUFFER;
      }
   }

   public String getString() throws IOException {
      return this.getString(HttpConstants.DEFAULT_CHARSET);
   }

   public String getString(Charset var1) throws IOException {
      if (this.file == null) {
         return "";
      } else {
         byte[] var2;
         if (var1 == null) {
            var2 = readFrom(this.file);
            return new String(var2, HttpConstants.DEFAULT_CHARSET.name());
         } else {
            var2 = readFrom(this.file);
            return new String(var2, var1.name());
         }
      }
   }

   public boolean isInMemory() {
      return false;
   }

   public boolean renameTo(File var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("dest");
      } else if (this.file == null) {
         throw new IOException("No file defined so cannot be renamed");
      } else if (!this.file.renameTo(var1)) {
         IOException var2 = null;
         FileInputStream var3 = null;
         FileOutputStream var4 = null;
         long var5 = 8196L;
         long var7 = 0L;

         try {
            var3 = new FileInputStream(this.file);
            var4 = new FileOutputStream(var1);
            FileChannel var9 = var3.getChannel();

            for(FileChannel var10 = var4.getChannel(); var7 < this.size; var7 += var9.transferTo(var7, var5, var10)) {
               if (var5 < this.size - var7) {
                  var5 = this.size - var7;
               }
            }
         } catch (IOException var23) {
            var2 = var23;
         } finally {
            if (var3 != null) {
               try {
                  var3.close();
               } catch (IOException var22) {
                  if (var2 == null) {
                     var2 = var22;
                  } else {
                     logger.warn("Multiple exceptions detected, the following will be suppressed {}", (Throwable)var22);
                  }
               }
            }

            if (var4 != null) {
               try {
                  var4.close();
               } catch (IOException var21) {
                  if (var2 == null) {
                     var2 = var21;
                  } else {
                     logger.warn("Multiple exceptions detected, the following will be suppressed {}", (Throwable)var21);
                  }
               }
            }

         }

         if (var2 != null) {
            throw var2;
         } else if (var7 == this.size) {
            if (!this.file.delete()) {
               logger.warn("Failed to delete: {}", (Object)this.file);
            }

            this.file = var1;
            this.isRenamed = true;
            return true;
         } else {
            if (!var1.delete()) {
               logger.warn("Failed to delete: {}", (Object)var1);
            }

            return false;
         }
      } else {
         this.file = var1;
         this.isRenamed = true;
         return true;
      }
   }

   private static byte[] readFrom(File var0) throws IOException {
      long var1 = var0.length();
      if (var1 > 2147483647L) {
         throw new IllegalArgumentException("File too big to be loaded in memory");
      } else {
         FileInputStream var3 = new FileInputStream(var0);
         byte[] var4 = new byte[(int)var1];

         try {
            FileChannel var5 = var3.getChannel();
            ByteBuffer var6 = ByteBuffer.wrap(var4);

            for(int var7 = 0; (long)var7 < var1; var7 += var5.read(var6)) {
            }
         } finally {
            var3.close();
         }

         return var4;
      }
   }

   public File getFile() throws IOException {
      return this.file;
   }

   public HttpData touch() {
      return this;
   }

   public HttpData touch(Object var1) {
      return this;
   }
}
