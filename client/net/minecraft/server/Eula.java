package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import net.minecraft.util.CommonLinks;
import org.slf4j.Logger;

public class Eula {
   private static final Logger LOGGER = LogUtils.getLogger();
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

         boolean var3;
         try {
            Properties var2 = new Properties();
            var2.load(var1);
            var3 = Boolean.parseBoolean(var2.getProperty("eula", "false"));
         } catch (Throwable var5) {
            if (var1 != null) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (var1 != null) {
            var1.close();
         }

         return var3;
      } catch (Exception var6) {
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

            try {
               Properties var2 = new Properties();
               var2.setProperty("eula", "false");
               var2.store(var1, "By changing the setting below to TRUE you are indicating your agreement to our EULA (" + String.valueOf(CommonLinks.EULA) + ").");
            } catch (Throwable var5) {
               if (var1 != null) {
                  try {
                     var1.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (var1 != null) {
               var1.close();
            }
         } catch (Exception var6) {
            LOGGER.warn("Failed to save {}", this.file, var6);
         }

      }
   }
}
