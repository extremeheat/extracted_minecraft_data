package net.minecraft.data;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DirectoryCache {
   private static final Logger field_208324_a = LogManager.getLogger();
   private final Path field_208325_b;
   private final Path field_208326_c;
   private int field_208327_d;
   private final Map<Path, String> field_208328_e = Maps.newHashMap();
   private final Map<Path, String> field_208329_f = Maps.newHashMap();

   public DirectoryCache(Path var1, String var2) throws IOException {
      super();
      this.field_208325_b = var1;
      Path var3 = var1.resolve(".cache");
      Files.createDirectories(var3);
      this.field_208326_c = var3.resolve(var2);
      this.func_209398_c().forEach((var1x) -> {
         String var10000 = (String)this.field_208328_e.put(var1x, "");
      });
      if (Files.isReadable(this.field_208326_c)) {
         IOUtils.readLines(Files.newInputStream(this.field_208326_c), Charsets.UTF_8).forEach((var2x) -> {
            int var3 = var2x.indexOf(32);
            this.field_208328_e.put(var1.resolve(var2x.substring(var3 + 1)), var2x.substring(0, var3));
         });
      }

   }

   public void func_208317_a() throws IOException {
      this.func_209400_b();

      BufferedWriter var1;
      try {
         var1 = Files.newBufferedWriter(this.field_208326_c);
      } catch (IOException var3) {
         field_208324_a.warn("Unable write cachefile {}: {}", this.field_208326_c, var3.toString());
         return;
      }

      IOUtils.writeLines((Collection)this.field_208329_f.entrySet().stream().map((var1x) -> {
         return (String)var1x.getValue() + ' ' + this.field_208325_b.relativize((Path)var1x.getKey());
      }).collect(Collectors.toList()), System.lineSeparator(), var1);
      var1.close();
      field_208324_a.debug("Caching: cache hits: {}, created: {} removed: {}", this.field_208327_d, this.field_208329_f.size() - this.field_208327_d, this.field_208328_e.size());
   }

   @Nullable
   public String func_208323_a(Path var1) {
      return (String)this.field_208328_e.get(var1);
   }

   public void func_208316_a(Path var1, String var2) {
      this.field_208329_f.put(var1, var2);
      if (Objects.equals(this.field_208328_e.remove(var1), var2)) {
         ++this.field_208327_d;
      }

   }

   public boolean func_208320_b(Path var1) {
      return this.field_208328_e.containsKey(var1);
   }

   private void func_209400_b() throws IOException {
      this.func_209398_c().forEach((var1) -> {
         if (this.func_208320_b(var1)) {
            try {
               Files.delete(var1);
            } catch (IOException var3) {
               field_208324_a.debug("Unable to delete: {} ({})", var1, var3.toString());
            }
         }

      });
   }

   private Stream<Path> func_209398_c() throws IOException {
      return Files.walk(this.field_208325_b).filter((var1) -> {
         return !Objects.equals(this.field_208326_c, var1) && !Files.isDirectory(var1, new LinkOption[0]);
      });
   }
}
