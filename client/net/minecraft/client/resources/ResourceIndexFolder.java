package net.minecraft.client.resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.ResourceLocation;

public class ResourceIndexFolder extends ResourceIndex {
   private final File field_188548_a;

   public ResourceIndexFolder(File var1) {
      super();
      this.field_188548_a = var1;
   }

   public File func_188547_a(ResourceLocation var1) {
      return new File(this.field_188548_a, var1.toString().replace(':', '/'));
   }

   public File func_200009_a(String var1) {
      return new File(this.field_188548_a, var1);
   }

   public Collection<String> func_211685_a(String var1, int var2, Predicate<String> var3) {
      Path var4 = this.field_188548_a.toPath().resolve("minecraft/");

      try {
         Stream var5 = Files.walk(var4.resolve(var1), var2, new FileVisitOption[0]);
         Throwable var6 = null;

         Collection var7;
         try {
            Stream var10000 = var5.filter((var0) -> {
               return Files.isRegularFile(var0, new LinkOption[0]);
            }).filter((var0) -> {
               return !var0.endsWith(".mcmeta");
            });
            var4.getClass();
            var7 = (Collection)var10000.map(var4::relativize).map(Object::toString).map((var0) -> {
               return var0.replaceAll("\\\\", "/");
            }).filter(var3).collect(Collectors.toList());
         } catch (Throwable var18) {
            var6 = var18;
            throw var18;
         } finally {
            if (var5 != null) {
               if (var6 != null) {
                  try {
                     var5.close();
                  } catch (Throwable var17) {
                     var6.addSuppressed(var17);
                  }
               } else {
                  var5.close();
               }
            }

         }

         return var7;
      } catch (NoSuchFileException var20) {
      } catch (IOException var21) {
         field_152783_a.warn("Unable to getFiles on {}", var1, var21);
      }

      return Collections.emptyList();
   }
}
