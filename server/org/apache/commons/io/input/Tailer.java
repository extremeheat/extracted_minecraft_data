package org.apache.commons.io.input;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class Tailer implements Runnable {
   private static final int DEFAULT_DELAY_MILLIS = 1000;
   private static final String RAF_MODE = "r";
   private static final int DEFAULT_BUFSIZE = 4096;
   private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();
   private final byte[] inbuf;
   private final File file;
   private final Charset cset;
   private final long delayMillis;
   private final boolean end;
   private final TailerListener listener;
   private final boolean reOpen;
   private volatile boolean run;

   public Tailer(File var1, TailerListener var2) {
      this(var1, var2, 1000L);
   }

   public Tailer(File var1, TailerListener var2, long var3) {
      this(var1, var2, var3, false);
   }

   public Tailer(File var1, TailerListener var2, long var3, boolean var5) {
      this(var1, var2, var3, var5, 4096);
   }

   public Tailer(File var1, TailerListener var2, long var3, boolean var5, boolean var6) {
      this(var1, var2, var3, var5, var6, 4096);
   }

   public Tailer(File var1, TailerListener var2, long var3, boolean var5, int var6) {
      this(var1, var2, var3, var5, false, var6);
   }

   public Tailer(File var1, TailerListener var2, long var3, boolean var5, boolean var6, int var7) {
      this(var1, DEFAULT_CHARSET, var2, var3, var5, var6, var7);
   }

   public Tailer(File var1, Charset var2, TailerListener var3, long var4, boolean var6, boolean var7, int var8) {
      super();
      this.run = true;
      this.file = var1;
      this.delayMillis = var4;
      this.end = var6;
      this.inbuf = new byte[var8];
      this.listener = var3;
      var3.init(this);
      this.reOpen = var7;
      this.cset = var2;
   }

   public static Tailer create(File var0, TailerListener var1, long var2, boolean var4, int var5) {
      return create(var0, var1, var2, var4, false, var5);
   }

   public static Tailer create(File var0, TailerListener var1, long var2, boolean var4, boolean var5, int var6) {
      return create(var0, DEFAULT_CHARSET, var1, var2, var4, var5, var6);
   }

   public static Tailer create(File var0, Charset var1, TailerListener var2, long var3, boolean var5, boolean var6, int var7) {
      Tailer var8 = new Tailer(var0, var1, var2, var3, var5, var6, var7);
      Thread var9 = new Thread(var8);
      var9.setDaemon(true);
      var9.start();
      return var8;
   }

   public static Tailer create(File var0, TailerListener var1, long var2, boolean var4) {
      return create(var0, var1, var2, var4, 4096);
   }

   public static Tailer create(File var0, TailerListener var1, long var2, boolean var4, boolean var5) {
      return create(var0, var1, var2, var4, var5, 4096);
   }

   public static Tailer create(File var0, TailerListener var1, long var2) {
      return create(var0, var1, var2, false);
   }

   public static Tailer create(File var0, TailerListener var1) {
      return create(var0, var1, 1000L, false);
   }

   public File getFile() {
      return this.file;
   }

   protected boolean getRun() {
      return this.run;
   }

   public long getDelay() {
      return this.delayMillis;
   }

   public void run() {
      RandomAccessFile var1 = null;

      try {
         long var2 = 0L;
         long var4 = 0L;

         while(this.getRun() && var1 == null) {
            try {
               var1 = new RandomAccessFile(this.file, "r");
            } catch (FileNotFoundException var20) {
               this.listener.fileNotFound();
            }

            if (var1 == null) {
               Thread.sleep(this.delayMillis);
            } else {
               var4 = this.end ? this.file.length() : 0L;
               var2 = this.file.lastModified();
               var1.seek(var4);
            }
         }

         while(this.getRun()) {
            boolean var6 = FileUtils.isFileNewer(this.file, var2);
            long var7 = this.file.length();
            if (var7 < var4) {
               this.listener.fileRotated();

               try {
                  RandomAccessFile var9 = var1;
                  var1 = new RandomAccessFile(this.file, "r");

                  try {
                     this.readLines(var9);
                  } catch (IOException var18) {
                     this.listener.handle((Exception)var18);
                  }

                  var4 = 0L;
                  IOUtils.closeQuietly((Closeable)var9);
               } catch (FileNotFoundException var19) {
                  this.listener.fileNotFound();
               }
            } else {
               if (var7 > var4) {
                  var4 = this.readLines(var1);
                  var2 = this.file.lastModified();
               } else if (var6) {
                  var4 = 0L;
                  var1.seek(var4);
                  var4 = this.readLines(var1);
                  var2 = this.file.lastModified();
               }

               if (this.reOpen) {
                  IOUtils.closeQuietly((Closeable)var1);
               }

               Thread.sleep(this.delayMillis);
               if (this.getRun() && this.reOpen) {
                  var1 = new RandomAccessFile(this.file, "r");
                  var1.seek(var4);
               }
            }
         }
      } catch (InterruptedException var21) {
         Thread.currentThread().interrupt();
         this.stop(var21);
      } catch (Exception var22) {
         this.stop(var22);
      } finally {
         IOUtils.closeQuietly((Closeable)var1);
      }

   }

   private void stop(Exception var1) {
      this.listener.handle(var1);
      this.stop();
   }

   public void stop() {
      this.run = false;
   }

   private long readLines(RandomAccessFile var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream(64);
      long var3 = var1.getFilePointer();
      long var5 = var3;

      int var7;
      for(boolean var8 = false; this.getRun() && (var7 = var1.read(this.inbuf)) != -1; var3 = var1.getFilePointer()) {
         for(int var9 = 0; var9 < var7; ++var9) {
            byte var10 = this.inbuf[var9];
            switch(var10) {
            case 10:
               var8 = false;
               this.listener.handle(new String(var2.toByteArray(), this.cset));
               var2.reset();
               var5 = var3 + (long)var9 + 1L;
               break;
            case 13:
               if (var8) {
                  var2.write(13);
               }

               var8 = true;
               break;
            default:
               if (var8) {
                  var8 = false;
                  this.listener.handle(new String(var2.toByteArray(), this.cset));
                  var2.reset();
                  var5 = var3 + (long)var9 + 1L;
               }

               var2.write(var10);
            }
         }
      }

      IOUtils.closeQuietly((OutputStream)var2);
      var1.seek(var5);
      if (this.listener instanceof TailerListenerAdapter) {
         ((TailerListenerAdapter)this.listener).endOfFileReached();
      }

      return var5;
   }
}
