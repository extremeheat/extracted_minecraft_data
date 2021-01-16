package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.GZIPOutputStream;

public final class GzCompressAction extends AbstractAction {
   private static final int BUF_SIZE = 8102;
   private final File source;
   private final File destination;
   private final boolean deleteSource;

   public GzCompressAction(File var1, File var2, boolean var3) {
      super();
      Objects.requireNonNull(var1, "source");
      Objects.requireNonNull(var2, "destination");
      this.source = var1;
      this.destination = var2;
      this.deleteSource = var3;
   }

   public boolean execute() throws IOException {
      return execute(this.source, this.destination, this.deleteSource);
   }

   public static boolean execute(File var0, File var1, boolean var2) throws IOException {
      if (var0.exists()) {
         FileInputStream var3 = new FileInputStream(var0);
         Throwable var4 = null;

         try {
            BufferedOutputStream var5 = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(var1)));
            Throwable var6 = null;

            try {
               byte[] var7 = new byte[8102];

               int var8;
               while((var8 = var3.read(var7)) != -1) {
                  var5.write(var7, 0, var8);
               }
            } catch (Throwable var30) {
               var6 = var30;
               throw var30;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var29) {
                        var6.addSuppressed(var29);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Throwable var32) {
            var4 = var32;
            throw var32;
         } finally {
            if (var3 != null) {
               if (var4 != null) {
                  try {
                     var3.close();
                  } catch (Throwable var28) {
                     var4.addSuppressed(var28);
                  }
               } else {
                  var3.close();
               }
            }

         }

         if (var2 && !var0.delete()) {
            LOGGER.warn("Unable to delete " + var0.toString() + '.');
         }

         return true;
      } else {
         return false;
      }
   }

   protected void reportException(Exception var1) {
      LOGGER.warn((String)("Exception during compression of '" + this.source.toString() + "'."), (Throwable)var1);
   }

   public String toString() {
      return GzCompressAction.class.getSimpleName() + '[' + this.source + " to " + this.destination + ", deleteSource=" + this.deleteSource + ']';
   }

   public File getSource() {
      return this.source;
   }

   public File getDestination() {
      return this.destination;
   }

   public boolean isDeleteSource() {
      return this.deleteSource;
   }
}
