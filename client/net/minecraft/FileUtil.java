package net.minecraft;

import com.mojang.serialization.DataResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

public class FileUtil {
   private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
   private static final int MAX_FILE_NAME = 255;
   private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);
   private static final Pattern STRICT_PATH_SEGMENT_CHECK = Pattern.compile("[-._a-z0-9]+");

   public FileUtil() {
      super();
   }

   public static String sanitizeName(String var0) {
      for(char var4 : SharedConstants.ILLEGAL_FILE_CHARACTERS) {
         var0 = var0.replace(var4, '_');
      }

      return var0.replaceAll("[./\"]", "_");
   }

   public static String findAvailableName(Path var0, String var1, String var2) throws IOException {
      var1 = sanitizeName(var1);
      if (RESERVED_WINDOWS_FILENAMES.matcher(var1).matches()) {
         var1 = "_" + var1 + "_";
      }

      Matcher var3 = COPY_COUNTER_PATTERN.matcher(var1);
      int var4 = 0;
      if (var3.matches()) {
         var1 = var3.group("name");
         var4 = Integer.parseInt(var3.group("count"));
      }

      if (var1.length() > 255 - var2.length()) {
         var1 = var1.substring(0, 255 - var2.length());
      }

      while(true) {
         String var5 = var1;
         if (var4 != 0) {
            String var6 = " (" + var4 + ")";
            int var7 = 255 - var6.length();
            if (var1.length() > var7) {
               var5 = var1.substring(0, var7);
            }

            var5 = var5 + var6;
         }

         var5 = var5 + var2;
         Path var11 = var0.resolve(var5);

         try {
            Path var12 = Files.createDirectory(var11);
            Files.deleteIfExists(var12);
            return var0.relativize(var12).toString();
         } catch (FileAlreadyExistsException var8) {
            ++var4;
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

   public static DataResult<List<String>> decomposePath(String var0) {
      int var1 = var0.indexOf(47);
      if (var1 == -1) {
         DataResult var10000;
         switch (var0) {
            case "":
            case ".":
            case "..":
               var10000 = DataResult.error(() -> "Invalid path '" + var0 + "'");
               break;
            default:
               var10000 = !isValidStrictPathSegment(var0) ? DataResult.error(() -> "Invalid path '" + var0 + "'") : DataResult.success(List.of(var0));
         }

         return var10000;
      } else {
         ArrayList var2 = new ArrayList();
         int var3 = 0;
         boolean var4 = false;

         while(true) {
            switch (var0.substring(var3, var1)) {
               case "":
               case ".":
               case "..":
                  return DataResult.error(() -> "Invalid segment '" + var5 + "' in path '" + var0 + "'");
            }

            if (!isValidStrictPathSegment(var5)) {
               return DataResult.error(() -> "Invalid segment '" + var5 + "' in path '" + var0 + "'");
            }

            var2.add(var5);
            if (var4) {
               return DataResult.success(var2);
            }

            var3 = var1 + 1;
            var1 = var0.indexOf(47, var3);
            if (var1 == -1) {
               var1 = var0.length();
               var4 = true;
            }
         }
      }
   }

   public static Path resolvePath(Path var0, List<String> var1) {
      int var2 = var1.size();
      Path var10000;
      switch (var2) {
         case 0:
            var10000 = var0;
            break;
         case 1:
            var10000 = var0.resolve((String)var1.get(0));
            break;
         default:
            String[] var3 = new String[var2 - 1];

            for(int var4 = 1; var4 < var2; ++var4) {
               var3[var4 - 1] = (String)var1.get(var4);
            }

            var10000 = var0.resolve(var0.getFileSystem().getPath((String)var1.get(0), var3));
      }

      return var10000;
   }

   public static boolean isValidStrictPathSegment(String var0) {
      return STRICT_PATH_SEGMENT_CHECK.matcher(var0).matches();
   }

   public static void validatePath(String... var0) {
      if (var0.length == 0) {
         throw new IllegalArgumentException("Path must have at least one element");
      } else {
         for(String var4 : var0) {
            if (var4.equals("..") || var4.equals(".") || !isValidStrictPathSegment(var4)) {
               throw new IllegalArgumentException("Illegal segment " + var4 + " in path " + Arrays.toString(var0));
            }
         }

      }
   }

   public static void createDirectoriesSafe(Path var0) throws IOException {
      Files.createDirectories(Files.exists(var0, new LinkOption[0]) ? var0.toRealPath() : var0);
   }
}
