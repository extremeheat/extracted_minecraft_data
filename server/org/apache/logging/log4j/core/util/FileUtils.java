package org.apache.logging.log4j.core.util;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class FileUtils {
   private static final String PROTOCOL_FILE = "file";
   private static final String JBOSS_FILE = "vfsfile";
   private static final Logger LOGGER = StatusLogger.getLogger();

   private FileUtils() {
      super();
   }

   public static File fileFromUri(URI var0) {
      if (var0 != null && (var0.getScheme() == null || "file".equals(var0.getScheme()) || "vfsfile".equals(var0.getScheme()))) {
         String var2;
         if (var0.getScheme() == null) {
            File var1 = new File(var0.toString());
            if (var1.exists()) {
               return var1;
            }

            try {
               var2 = var0.getPath();
               var1 = new File(var2);
               if (var1.exists()) {
                  return var1;
               }

               var0 = (new File(var2)).toURI();
            } catch (Exception var5) {
               LOGGER.warn((String)"Invalid URI {}", (Object)var0);
               return null;
            }
         }

         String var6 = StandardCharsets.UTF_8.name();

         try {
            var2 = var0.toURL().getFile();
            if ((new File(var2)).exists()) {
               return new File(var2);
            }

            var2 = URLDecoder.decode(var2, var6);
            return new File(var2);
         } catch (MalformedURLException var3) {
            LOGGER.warn((String)"Invalid URL {}", (Object)var0, (Object)var3);
         } catch (UnsupportedEncodingException var4) {
            LOGGER.warn((String)"Invalid encoding: {}", (Object)var6, (Object)var4);
         }

         return null;
      } else {
         return null;
      }
   }

   public static boolean isFile(URL var0) {
      return var0 != null && (var0.getProtocol().equals("file") || var0.getProtocol().equals("vfsfile"));
   }

   public static String getFileExtension(File var0) {
      String var1 = var0.getName();
      return var1.lastIndexOf(".") != -1 && var1.lastIndexOf(".") != 0 ? var1.substring(var1.lastIndexOf(".") + 1) : null;
   }

   public static void mkdir(File var0, boolean var1) throws IOException {
      if (!var0.exists()) {
         if (!var1) {
            throw new IOException("The directory " + var0.getAbsolutePath() + " does not exist.");
         }

         if (!var0.mkdirs()) {
            throw new IOException("Could not create directory " + var0.getAbsolutePath());
         }
      }

      if (!var0.isDirectory()) {
         throw new IOException("File " + var0 + " exists and is not a directory. Unable to create directory.");
      }
   }

   public static void makeParentDirs(File var0) throws IOException {
      File var1 = ((File)Objects.requireNonNull(var0, "file")).getCanonicalFile().getParentFile();
      if (var1 != null) {
         mkdir(var1, true);
      }

   }
}
