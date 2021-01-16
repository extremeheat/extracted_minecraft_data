package io.netty.channel;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class DefaultFileRegion extends AbstractReferenceCounted implements FileRegion {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
   private final File f;
   private final long position;
   private final long count;
   private long transferred;
   private FileChannel file;

   public DefaultFileRegion(FileChannel var1, long var2, long var4) {
      super();
      if (var1 == null) {
         throw new NullPointerException("file");
      } else if (var2 < 0L) {
         throw new IllegalArgumentException("position must be >= 0 but was " + var2);
      } else if (var4 < 0L) {
         throw new IllegalArgumentException("count must be >= 0 but was " + var4);
      } else {
         this.file = var1;
         this.position = var2;
         this.count = var4;
         this.f = null;
      }
   }

   public DefaultFileRegion(File var1, long var2, long var4) {
      super();
      if (var1 == null) {
         throw new NullPointerException("f");
      } else if (var2 < 0L) {
         throw new IllegalArgumentException("position must be >= 0 but was " + var2);
      } else if (var4 < 0L) {
         throw new IllegalArgumentException("count must be >= 0 but was " + var4);
      } else {
         this.position = var2;
         this.count = var4;
         this.f = var1;
      }
   }

   public boolean isOpen() {
      return this.file != null;
   }

   public void open() throws IOException {
      if (!this.isOpen() && this.refCnt() > 0) {
         this.file = (new RandomAccessFile(this.f, "r")).getChannel();
      }

   }

   public long position() {
      return this.position;
   }

   public long count() {
      return this.count;
   }

   /** @deprecated */
   @Deprecated
   public long transfered() {
      return this.transferred;
   }

   public long transferred() {
      return this.transferred;
   }

   public long transferTo(WritableByteChannel var1, long var2) throws IOException {
      long var4 = this.count - var2;
      if (var4 >= 0L && var2 >= 0L) {
         if (var4 == 0L) {
            return 0L;
         } else if (this.refCnt() == 0) {
            throw new IllegalReferenceCountException(0);
         } else {
            this.open();
            long var6 = this.file.transferTo(this.position + var2, var4, var1);
            if (var6 > 0L) {
               this.transferred += var6;
            }

            return var6;
         }
      } else {
         throw new IllegalArgumentException("position out of range: " + var2 + " (expected: 0 - " + (this.count - 1L) + ')');
      }
   }

   protected void deallocate() {
      FileChannel var1 = this.file;
      if (var1 != null) {
         this.file = null;

         try {
            var1.close();
         } catch (IOException var3) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to close a file.", (Throwable)var3);
            }
         }

      }
   }

   public FileRegion retain() {
      super.retain();
      return this;
   }

   public FileRegion retain(int var1) {
      super.retain(var1);
      return this;
   }

   public FileRegion touch() {
      return this;
   }

   public FileRegion touch(Object var1) {
      return this;
   }
}
