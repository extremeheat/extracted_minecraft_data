package net.minecraft.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Eula {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Path file;
   private final boolean agreed;

   public Eula(Path var1) {
      super();
      this.file = var1;
      this.agreed = SharedConstants.IS_RUNNING_IN_IDE || this.readFile();
   }

   private boolean readFile() {
      try {
         InputStream var1 = Files.newInputStream(this.file);
         Throwable var2 = null;

         boolean var4;
         try {
            Properties var3 = new Properties();
            var3.load(var1);
            var4 = Boolean.parseBoolean(var3.getProperty("eula", "false"));
         } catch (Throwable var14) {
            var2 = var14;
            throw var14;
         } finally {
            if (var1 != null) {
               if (var2 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var13) {
                     var2.addSuppressed(var13);
                  }
               } else {
                  var1.close();
               }
            }

         }

         return var4;
      } catch (Exception var16) {
         LOGGER.warn("Failed to load {}", this.file);
         this.saveDefaults();
         return false;
      }
   }

   public boolean hasAgreedToEULA() {
      return this.agreed;
   }

   private void saveDefaults() {
      if (!SharedConstants.IS_RUNNING_IN_IDE) {
         try {
            OutputStream var1 = Files.newOutputStream(this.file);
            Throwable var2 = null;

            try {
               Properties var3 = new Properties();
               var3.setProperty("eula", "false");
               var3.store(var1, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
            } catch (Throwable var12) {
               var2 = var12;
               throw var12;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var11) {
                        var2.addSuppressed(var11);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (Exception var14) {
            LOGGER.warn("Failed to save {}", this.file, var14);
         }

      }
   }
}
