package org.apache.logging.log4j.core.script;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.ExtensionLanguageMapping;
import org.apache.logging.log4j.core.util.FileUtils;
import org.apache.logging.log4j.core.util.IOUtils;
import org.apache.logging.log4j.core.util.NetUtils;

@Plugin(
   name = "ScriptFile",
   category = "Core",
   printObject = true
)
public class ScriptFile extends AbstractScript {
   private final Path filePath;
   private final boolean isWatched;

   public ScriptFile(String var1, Path var2, String var3, boolean var4, String var5) {
      super(var1, var3, var5);
      this.filePath = var2;
      this.isWatched = var4;
   }

   public Path getPath() {
      return this.filePath;
   }

   public boolean isWatched() {
      return this.isWatched;
   }

   @PluginFactory
   public static ScriptFile createScript(@PluginAttribute("name") String var0, @PluginAttribute("language") String var1, @PluginAttribute("path") String var2, @PluginAttribute("isWatched") Boolean var3, @PluginAttribute("charset") Charset var4) {
      if (var2 == null) {
         LOGGER.error("No script path provided for ScriptFile");
         return null;
      } else {
         if (var0 == null) {
            var0 = var2;
         }

         URI var5 = NetUtils.toURI(var2);
         File var6 = FileUtils.fileFromUri(var5);
         if (var1 == null && var6 != null) {
            String var7 = FileUtils.getFileExtension(var6);
            if (var7 != null) {
               ExtensionLanguageMapping var8 = ExtensionLanguageMapping.getByExtension(var7);
               if (var8 != null) {
                  var1 = var8.getLanguage();
               }
            }
         }

         if (var1 == null) {
            LOGGER.info((String)"No script language supplied, defaulting to {}", (Object)"JavaScript");
            var1 = "JavaScript";
         }

         Charset var23 = var4 == null ? Charset.defaultCharset() : var4;

         String var24;
         try {
            InputStreamReader var9 = new InputStreamReader((InputStream)(var6 != null ? new FileInputStream(var6) : var5.toURL().openStream()), var23);
            Throwable var10 = null;

            try {
               var24 = IOUtils.toString(var9);
            } catch (Throwable var20) {
               var10 = var20;
               throw var20;
            } finally {
               if (var9 != null) {
                  if (var10 != null) {
                     try {
                        var9.close();
                     } catch (Throwable var19) {
                        var10.addSuppressed(var19);
                     }
                  } else {
                     var9.close();
                  }
               }

            }
         } catch (IOException var22) {
            LOGGER.error((String)"{}: language={}, path={}, actualCharset={}", (Object)var22.getClass().getSimpleName(), var1, var2, var23);
            return null;
         }

         Path var25 = var6 != null ? Paths.get(var6.toURI()) : Paths.get(var5);
         if (var25 == null) {
            LOGGER.error((String)"Unable to convert {} to a Path", (Object)var5.toString());
            return null;
         } else {
            return new ScriptFile(var0, var25, var1, var3 == null ? Boolean.FALSE : var3, var24);
         }
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (!this.getName().equals(this.filePath.toString())) {
         var1.append("name=").append(this.getName()).append(", ");
      }

      var1.append("path=").append(this.filePath);
      if (this.getLanguage() != null) {
         var1.append(", language=").append(this.getLanguage());
      }

      var1.append(", isWatched=").append(this.isWatched);
      return var1.toString();
   }
}
