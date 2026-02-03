package net.minecraft.util.filefix.operations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import net.minecraft.util.filefix.FileFixUtil;
import net.minecraft.util.worldupdate.UpgradeProgress;

public record RegexMove(Pattern fromPattern, String toReplacement) implements FileFixOperation {
   public RegexMove(final String fromPattern, final String toPattern) {
      this(Pattern.compile(fromPattern), toPattern);
   }

   public RegexMove {
      super();
   }

   public void fix(final Path baseDirectory, final UpgradeProgress upgradeProgress) throws IOException {
      if (Files.exists(baseDirectory, new LinkOption[0]) && Files.isDirectory(baseDirectory, new LinkOption[0])) {
         Stream<Path> files = Files.list(baseDirectory);

         try {
            for(Path file : files.toList()) {
               String fileName = file.getFileName().toString();
               Matcher matcher = this.fromPattern.matcher(fileName);
               if (matcher.matches()) {
                  String newName = matcher.replaceAll(this.toReplacement);
                  FileFixUtil.moveFile(baseDirectory, fileName, newName);
               }
            }
         } catch (Throwable var10) {
            if (files != null) {
               try {
                  files.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (files != null) {
            files.close();
         }

      }
   }
}
