package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Beta
@GwtIncompatible
public final class FileBackedOutputStream extends OutputStream {
   private final int fileThreshold;
   private final boolean resetOnFinalize;
   private final ByteSource source;
   private OutputStream out;
   private FileBackedOutputStream.MemoryOutput memory;
   private File file;

   @VisibleForTesting
   synchronized File getFile() {
      return this.file;
   }

   public FileBackedOutputStream(int var1) {
      this(var1, false);
   }

   public FileBackedOutputStream(int var1, boolean var2) {
      super();
      this.fileThreshold = var1;
      this.resetOnFinalize = var2;
      this.memory = new FileBackedOutputStream.MemoryOutput();
      this.out = this.memory;
      if (var2) {
         this.source = new ByteSource() {
            public InputStream openStream() throws IOException {
               return FileBackedOutputStream.this.openInputStream();
            }

            protected void finalize() {
               try {
                  FileBackedOutputStream.this.reset();
               } catch (Throwable var2) {
                  var2.printStackTrace(System.err);
               }

            }
         };
      } else {
         this.source = new ByteSource() {
            public InputStream openStream() throws IOException {
               return FileBackedOutputStream.this.openInputStream();
            }
         };
      }

   }

   public ByteSource asByteSource() {
      return this.source;
   }

   private synchronized InputStream openInputStream() throws IOException {
      return (InputStream)(this.file != null ? new FileInputStream(this.file) : new ByteArrayInputStream(this.memory.getBuffer(), 0, this.memory.getCount()));
   }

   public synchronized void reset() throws IOException {
      boolean var5 = false;

      try {
         var5 = true;
         this.close();
         var5 = false;
      } finally {
         if (var5) {
            if (this.memory == null) {
               this.memory = new FileBackedOutputStream.MemoryOutput();
            } else {
               this.memory.reset();
            }

            this.out = this.memory;
            if (this.file != null) {
               File var3 = this.file;
               this.file = null;
               if (!var3.delete()) {
                  throw new IOException("Could not delete: " + var3);
               }
            }

         }
      }

      if (this.memory == null) {
         this.memory = new FileBackedOutputStream.MemoryOutput();
      } else {
         this.memory.reset();
      }

      this.out = this.memory;
      if (this.file != null) {
         File var1 = this.file;
         this.file = null;
         if (!var1.delete()) {
            throw new IOException("Could not delete: " + var1);
         }
      }

   }

   public synchronized void write(int var1) throws IOException {
      this.update(1);
      this.out.write(var1);
   }

   public synchronized void write(byte[] var1) throws IOException {
      this.write(var1, 0, var1.length);
   }

   public synchronized void write(byte[] var1, int var2, int var3) throws IOException {
      this.update(var3);
      this.out.write(var1, var2, var3);
   }

   public synchronized void close() throws IOException {
      this.out.close();
   }

   public synchronized void flush() throws IOException {
      this.out.flush();
   }

   private void update(int var1) throws IOException {
      if (this.file == null && this.memory.getCount() + var1 > this.fileThreshold) {
         File var2 = File.createTempFile("FileBackedOutputStream", (String)null);
         if (this.resetOnFinalize) {
            var2.deleteOnExit();
         }

         FileOutputStream var3 = new FileOutputStream(var2);
         var3.write(this.memory.getBuffer(), 0, this.memory.getCount());
         var3.flush();
         this.out = var3;
         this.file = var2;
         this.memory = null;
      }

   }

   private static class MemoryOutput extends ByteArrayOutputStream {
      private MemoryOutput() {
         super();
      }

      byte[] getBuffer() {
         return this.buf;
      }

      int getCount() {
         return this.count;
      }

      // $FF: synthetic method
      MemoryOutput(Object var1) {
         this();
      }
   }
}
