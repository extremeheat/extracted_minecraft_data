package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class ZipCompressAction extends AbstractAction {
   private static final int BUF_SIZE = 8102;
   private final File source;
   private final File destination;
   private final boolean deleteSource;
   private final int level;

   public ZipCompressAction(File var1, File var2, boolean var3, int var4) {
      super();
      Objects.requireNonNull(var1, "source");
      Objects.requireNonNull(var2, "destination");
      this.source = var1;
      this.destination = var2;
      this.deleteSource = var3;
      this.level = var4;
   }

   public boolean execute() throws IOException {
      return execute(this.source, this.destination, this.deleteSource, this.level);
   }

   public static boolean execute(File var0, File var1, boolean var2, int var3) throws IOException {
      if (var0.exists()) {
         FileInputStream var4 = new FileInputStream(var0);
         Throwable var5 = null;

         try {
            ZipOutputStream var6 = new ZipOutputStream(new FileOutputStream(var1));
            Throwable var7 = null;

            try {
               var6.setLevel(var3);
               ZipEntry var8 = new ZipEntry(var0.getName());
               var6.putNextEntry(var8);
               byte[] var9 = new byte[8102];

               int var10;
               while((var10 = var4.read(var9)) != -1) {
                  var6.write(var9, 0, var10);
               }
            } catch (Throwable var32) {
               var7 = var32;
               throw var32;
            } finally {
               if (var6 != null) {
                  if (var7 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var31) {
                        var7.addSuppressed(var31);
                     }
                  } else {
                     var6.close();
                  }
               }

            }
         } catch (Throwable var34) {
            var5 = var34;
            throw var34;
         } finally {
            if (var4 != null) {
               if (var5 != null) {
                  try {
                     var4.close();
                  } catch (Throwable var30) {
                     var5.addSuppressed(var30);
                  }
               } else {
                  var4.close();
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
      return ZipCompressAction.class.getSimpleName() + '[' + this.source + " to " + this.destination + ", level=" + this.level + ", deleteSource=" + this.deleteSource + ']';
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

   public int getLevel() {
      return this.level;
   }
}
