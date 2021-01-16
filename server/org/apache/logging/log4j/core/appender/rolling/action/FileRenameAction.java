package org.apache.logging.log4j.core.appender.rolling.action;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileRenameAction extends AbstractAction {
   private final File source;
   private final File destination;
   private final boolean renameEmptyFiles;

   public FileRenameAction(File var1, File var2, boolean var3) {
      super();
      this.source = var1;
      this.destination = var2;
      this.renameEmptyFiles = var3;
   }

   public boolean execute() {
      return execute(this.source, this.destination, this.renameEmptyFiles);
   }

   public File getDestination() {
      return this.destination;
   }

   public File getSource() {
      return this.source;
   }

   public boolean isRenameEmptyFiles() {
      return this.renameEmptyFiles;
   }

   public static boolean execute(File var0, File var1, boolean var2) {
      if (!var2 && var0.length() <= 0L) {
         try {
            var0.delete();
         } catch (Exception var11) {
            LOGGER.error((String)"Unable to delete empty file {}: {} {}", (Object)var0.getAbsolutePath(), var11.getClass().getName(), var11.getMessage());
         }
      } else {
         File var3 = var1.getParentFile();
         if (var3 != null && !var3.exists()) {
            var3.mkdirs();
            if (!var3.exists()) {
               LOGGER.error((String)"Unable to create directory {}", (Object)var3.getAbsolutePath());
               return false;
            }
         }

         try {
            try {
               Files.move(Paths.get(var0.getAbsolutePath()), Paths.get(var1.getAbsolutePath()), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
               LOGGER.trace((String)"Renamed file {} to {} with Files.move", (Object)var0.getAbsolutePath(), (Object)var1.getAbsolutePath());
               return true;
            } catch (IOException var12) {
               LOGGER.error((String)"Unable to move file {} to {}: {} {}", (Object)var0.getAbsolutePath(), var1.getAbsolutePath(), var12.getClass().getName(), var12.getMessage());
               boolean var5 = var0.renameTo(var1);
               if (!var5) {
                  try {
                     Files.copy(Paths.get(var0.getAbsolutePath()), Paths.get(var1.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);

                     try {
                        Files.delete(Paths.get(var0.getAbsolutePath()));
                        LOGGER.trace((String)"Renamed file {} to {} using copy and delete", (Object)var0.getAbsolutePath(), (Object)var1.getAbsolutePath());
                     } catch (IOException var9) {
                        LOGGER.error((String)"Unable to delete file {}: {} {}", (Object)var0.getAbsolutePath(), var9.getClass().getName(), var9.getMessage());

                        try {
                           (new PrintWriter(var0.getAbsolutePath())).close();
                           LOGGER.trace((String)"Renamed file {} to {} with copy and truncation", (Object)var0.getAbsolutePath(), (Object)var1.getAbsolutePath());
                        } catch (IOException var8) {
                           LOGGER.error((String)"Unable to overwrite file {}: {} {}", (Object)var0.getAbsolutePath(), var8.getClass().getName(), var8.getMessage());
                        }
                     }
                  } catch (IOException var10) {
                     LOGGER.error((String)"Unable to copy file {} to {}: {} {}", (Object)var0.getAbsolutePath(), var1.getAbsolutePath(), var10.getClass().getName(), var10.getMessage());
                  }
               } else {
                  LOGGER.trace((String)"Renamed file {} to {} with source.renameTo", (Object)var0.getAbsolutePath(), (Object)var1.getAbsolutePath());
               }

               return var5;
            }
         } catch (RuntimeException var13) {
            LOGGER.error((String)"Unable to rename file {} to {}: {} {}", (Object)var0.getAbsolutePath(), var1.getAbsolutePath(), var13.getClass().getName(), var13.getMessage());
         }
      }

      return false;
   }

   public String toString() {
      return FileRenameAction.class.getSimpleName() + '[' + this.source + " to " + this.destination + ", renameEmptyFiles=" + this.renameEmptyFiles + ']';
   }
}
