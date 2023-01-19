package net.minecraft;

import com.mojang.serialization.DataResult;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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

   public static DataResult<List<String>> decomposePath(String var0) {
      int var1 = var0.indexOf(47);
      if (var1 == -1) {
         return switch(var0) {
            case "", ".", ".." -> DataResult.error("Invalid path '" + var0 + "'");
            default -> !isValidStrictPathSegment(var0) ? DataResult.error("Invalid path '" + var0 + "'") : DataResult.success(List.of(var0));
         };
      } else {
         ArrayList var2 = new ArrayList();
         int var3 = 0;
         boolean var4 = false;

         while(true) {
            String var5 = var0.substring(var3, var1);
            switch(var5) {
               case "":
               case ".":
               case "..":
                  return DataResult.error("Invalid segment '" + var5 + "' in path '" + var0 + "'");
            }
         }
      }
   }

   public static Path resolvePath(Path var0, List<String> var1) {
      int var2 = var1.size();

      return switch(var2) {
         case 0 -> var0;
         case 1 -> var0.resolve((String)var1.get(0));
         default -> {
            String[] var3 = new String[var2 - 1];

            for(int var4 = 1; var4 < var2; ++var4) {
               var3[var4 - 1] = (String)var1.get(var4);
            }

            yield var0.resolve(var0.getFileSystem().getPath((String)var1.get(0), var3));
         }
      };
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
               throw new IllegalArgumentException("Illegal segment " + var4 + " in path " + Arrays.toString((Object[])var0));
            }
         }
      }
   }

   public static void createDirectoriesSafe(Path var0) throws IOException {
      Files.createDirectories(Files.exists(var0) ? var0.toRealPath() : var0);
   }
}
