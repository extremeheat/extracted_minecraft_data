package net.minecraft;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
   private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
   private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);

   public static String findAvailableName(Path var0, String var1, String var2) throws IOException {
      char[] var3 = SharedConstants.ILLEGAL_FILE_CHARACTERS;
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         char var6 = var3[var5];
         var1 = var1.replace(var6, '_');
      }

      var1 = var1.replaceAll("[./\"]", "_");
      if (RESERVED_WINDOWS_FILENAMES.matcher(var1).matches()) {
         var1 = "_" + var1 + "_";
      }

      Matcher var9 = COPY_COUNTER_PATTERN.matcher(var1);
      var4 = 0;
      if (var9.matches()) {
         var1 = var9.group("name");
         var4 = Integer.parseInt(var9.group("count"));
      }

      if (var1.length() > 255 - var2.length()) {
         var1 = var1.substring(0, 255 - var2.length());
      }

      while(true) {
         String var10 = var1;
         if (var4 != 0) {
            String var11 = " (" + var4 + ")";
            int var7 = 255 - var11.length();
            if (var1.length() > var7) {
               var10 = var1.substring(0, var7);
            }

            var10 = var10 + var11;
         }

         var10 = var10 + var2;
         Path var12 = var0.resolve(var10);

         try {
            Path var13 = Files.createDirectory(var12);
            Files.deleteIfExists(var13);
            return var0.relativize(var13).toString();
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
      Iterator var1 = var0.iterator();

      Path var2;
      do {
         if (!var1.hasNext()) {
            return true;
         }

         var2 = (Path)var1.next();
      } while(!RESERVED_WINDOWS_FILENAMES.matcher(var2.toString()).matches());

      return false;
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
}
