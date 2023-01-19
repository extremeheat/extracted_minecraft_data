package net.minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {
   private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
   private static final int MAX_FILE_NAME = 255;
   private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

   public FileUtil() {
      super();
   }

   public static String findAvailableName(Path var0, String var1, String var2) throws IOException {
      for(char var6 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         var1 = var1.replace(var6, '_');
      }

      var1 = var1.replaceAll("[./\"]", "_");
      if (RESERVED_WINDOWS_FILENAMES.matcher(var1).matches()) {
         var1 = "_" + var1 + "_";
      }

      Matcher var10 = COPY_COUNTER_PATTERN.matcher(var1);
      int var11 = 0;
      if (var10.matches()) {
         var1 = var10.group("name");
         var11 = Integer.parseInt(var10.group("count"));
      }

      if (var1.length() > 255 - var2.length()) {
         var1 = var1.substring(0, 255 - var2.length());
      }

      while(true) {
         String var12 = var1;
         if (var11 != 0) {
            String var14 = " (" + var11 + ")";
            int var7 = 255 - var14.length();
            if (var1.length() > var7) {
               var12 = var1.substring(0, var7);
            }

            var12 = var12 + var14;
         }

         var12 = var12 + var2;
         Path var15 = var0.resolve(var12);

         try {
            Path var16 = Files.createDirectory(var15);
            Files.deleteIfExists(var16);
            return var0.relativize(var16).toString();
         } catch (FileAlreadyExistsException var8) {
            ++var11;
         }
      }
   }

   public static boolean isPathNormalized(Path var0) {
      Path var1 = var0.normalize();
      return var1.equals(var0);
   }

   public static boolean isPathPortable(Path var0) {
      for(Path var2 : var0) {
         if (RESERVED_WINDOWS_FILENAMES.matcher(var2.toString()).matches()) {
            return false;
         }
      }

      return true;
   }

   public static Path createPathToResource(Path var0, String var1, String var2) {
      String var3 = var1 + var2;
      Path var4 = Paths.get(var3);
      if (var4.endsWith(var2)) {
         throw new InvalidPathException(var3, "empty resource name");
      } else {
         return var0.resolve(var4);
      }
   }

   public static String getFullResourcePath(String var0) {
      return FilenameUtils.getFullPath(var0).replace(File.separator, "/");
   }

   public static String normalizeResourcePath(String var0) {
      return FilenameUtils.normalize(var0).replace(File.separator, "/");
   }
}
