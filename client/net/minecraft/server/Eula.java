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

   public Eula(final Path file) {
      super();
      this.file = file;
      this.agreed = SharedConstants.IS_RUNNING_IN_IDE || this.readFile();
   }

   private boolean readFile() {
      try {
         InputStream input = Files.newInputStream(this.file);

         boolean var3;
         try {
            Properties properties = new Properties();
            properties.load(input);
            var3 = Boolean.parseBoolean(properties.getProperty("eula", "false"));
         } catch (Throwable var5) {
            if (input != null) {
               try {
                  input.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (input != null) {
            input.close();
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
            OutputStream output = Files.newOutputStream(this.file);

            try {
               Properties properties = new Properties();
               properties.setProperty("eula", "false");
               properties.store(output, "By changing the setting below to TRUE you are indicating your agreement to our EULA (" + String.valueOf(CommonLinks.EULA) + ").");
            } catch (Throwable var5) {
               if (output != null) {
                  try {
                     output.close();
                  } catch (Throwable var4) {
                     var5.addSuppressed(var4);
                  }
               }

               throw var5;
            }

            if (output != null) {
               output.close();
            }
         } catch (Exception e) {
            LOGGER.warn("Failed to save {}", this.file, e);
         }

      }
   }
}
