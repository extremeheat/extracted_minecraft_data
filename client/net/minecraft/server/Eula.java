package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
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
         boolean var3;
         try (InputStream var1 = Files.newInputStream(this.file)) {
            Properties var2 = new Properties();
            var2.load(var1);
            var3 = Boolean.parseBoolean(var2.getProperty("eula", "false"));
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
         try (OutputStream var1 = Files.newOutputStream(this.file)) {
            Properties var2 = new Properties();
            var2.setProperty("eula", "false");
            var2.store(var1, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://aka.ms/MinecraftEULA).");
         } catch (Exception var6) {
            LOGGER.warn("Failed to save {}", this.file, var6);
         }
      }
   }
}
