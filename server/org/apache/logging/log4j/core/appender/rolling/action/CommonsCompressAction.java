package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import org.apache.commons.compress.compressors.CompressorException;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.compress.utils.IOUtils;

public final class CommonsCompressAction extends AbstractAction {
   private static final int BUF_SIZE = 8102;
   private final String name;
   private final File source;
   private final File destination;
   private final boolean deleteSource;

   public CommonsCompressAction(String var1, File var2, File var3, boolean var4) {
      super();
      Objects.requireNonNull(var2, "source");
      Objects.requireNonNull(var3, "destination");
      this.name = var1;
      this.source = var2;
      this.destination = var3;
      this.deleteSource = var4;
   }

   public boolean execute() throws IOException {
      return execute(this.name, this.source, this.destination, this.deleteSource);
   }

   public static boolean execute(String var0, File var1, File var2, boolean var3) throws IOException {
      if (!var1.exists()) {
         return false;
      } else {
         LOGGER.debug((String)"Starting {} compression of {}", (Object)var0, (Object)var1.getPath());

         try {
            FileInputStream var4 = new FileInputStream(var1);
            Throwable var5 = null;

            try {
               BufferedOutputStream var6 = new BufferedOutputStream((new CompressorStreamFactory()).createCompressorOutputStream(var0, new FileOutputStream(var2)));
               Throwable var7 = null;

               try {
                  IOUtils.copy(var4, var6, 8102);
                  LOGGER.debug((String)"Finished {} compression of {}", (Object)var0, (Object)var1.getPath());
               } catch (Throwable var35) {
                  var7 = var35;
                  throw var35;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var33) {
                           var7.addSuppressed(var33);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }
            } catch (Throwable var37) {
               var5 = var37;
               throw var37;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var32) {
                        var5.addSuppressed(var32);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         } catch (CompressorException var39) {
            throw new IOException(var39);
         }

         if (var3) {
            try {
               if (Files.deleteIfExists(var1.toPath())) {
                  LOGGER.debug((String)"Deleted {}", (Object)var1.toString());
               } else {
                  LOGGER.warn((String)"Unable to delete {} after {} compression. File did not exist", (Object)var1.toString(), (Object)var0);
               }
            } catch (Exception var34) {
               LOGGER.warn((String)"Unable to delete {} after {} compression, {}", (Object)var1.toString(), var0, var34.getMessage());
            }
         }

         return true;
      }
   }

   protected void reportException(Exception var1) {
      LOGGER.warn((String)("Exception during " + this.name + " compression of '" + this.source.toString() + "'."), (Throwable)var1);
   }

   public String toString() {
      return CommonsCompressAction.class.getSimpleName() + '[' + this.source + " to " + this.destination + ", deleteSource=" + this.deleteSource + ']';
   }

   public String getName() {
      return this.name;
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
