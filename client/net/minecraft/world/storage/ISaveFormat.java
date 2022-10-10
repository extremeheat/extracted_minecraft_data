package net.minecraft.world.storage;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;

public interface ISaveFormat {
   DateTimeFormatter field_197716_d = (new DateTimeFormatterBuilder()).appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendLiteral('-').appendValue(ChronoField.MONTH_OF_YEAR, 2).appendLiteral('-').appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('_').appendValue(ChronoField.HOUR_OF_DAY, 2).appendLiteral('-').appendValue(ChronoField.MINUTE_OF_HOUR, 2).appendLiteral('-').appendValue(ChronoField.SECOND_OF_MINUTE, 2).toFormatter();

   default long func_197713_h(String var1) throws IOException {
      final Path var2 = this.func_197714_g(var1);
      String var3 = LocalDateTime.now().format(field_197716_d) + "_" + var1;
      int var4 = 0;
      Path var5 = this.func_197712_e();

      try {
         Files.createDirectories(Files.exists(var5, new LinkOption[0]) ? var5.toRealPath() : var5);
      } catch (IOException var19) {
         throw new RuntimeException(var19);
      }

      Path var6;
      do {
         var6 = var5.resolve(var3 + (var4++ > 0 ? "_" + var4 : "") + ".zip");
      } while(Files.exists(var6, new LinkOption[0]));

      final ZipOutputStream var7 = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(var6.toFile())));
      Throwable var8 = null;

      try {
         final Path var9 = Paths.get(var1);
         Files.walkFileTree(var2, new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path var1, BasicFileAttributes var2x) throws IOException {
               String var3 = var9.resolve(var2.relativize(var1)).toString();
               ZipEntry var4 = new ZipEntry(var3);
               var7.putNextEntry(var4);
               com.google.common.io.Files.asByteSource(var1.toFile()).copyTo(var7);
               var7.closeEntry();
               return FileVisitResult.CONTINUE;
            }

            // $FF: synthetic method
            public FileVisitResult visitFile(Object var1, BasicFileAttributes var2x) throws IOException {
               return this.visitFile((Path)var1, var2x);
            }
         });
         var7.close();
      } catch (Throwable var18) {
         var8 = var18;
         throw var18;
      } finally {
         if (var7 != null) {
            if (var8 != null) {
               try {
                  var7.close();
               } catch (Throwable var17) {
                  var8.addSuppressed(var17);
               }
            } else {
               var7.close();
            }
         }

      }

      return Files.size(var6);
   }

   String func_207741_a();

   ISaveHandler func_197715_a(String var1, @Nullable MinecraftServer var2);

   List<WorldSummary> func_75799_b() throws AnvilConverterException;

   void func_75800_d();

   @Nullable
   WorldInfo func_75803_c(String var1);

   boolean func_207742_d(String var1);

   boolean func_75802_e(String var1);

   void func_75806_a(String var1, String var2);

   boolean func_207743_a(String var1);

   boolean func_75801_b(String var1);

   boolean func_75805_a(String var1, IProgressUpdate var2);

   boolean func_90033_f(String var1);

   File func_186352_b(String var1, String var2);

   Path func_197714_g(String var1);

   Path func_197712_e();
}
