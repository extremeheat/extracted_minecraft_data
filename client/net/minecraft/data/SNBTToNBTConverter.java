package net.minecraft.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.JsonToNBT;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SNBTToNBTConverter implements IDataProvider {
   private static final Logger field_200424_a = LogManager.getLogger();
   private final DataGenerator field_200425_b;

   public SNBTToNBTConverter(DataGenerator var1) {
      super();
      this.field_200425_b = var1;
   }

   public void func_200398_a(DirectoryCache var1) throws IOException {
      Path var2 = this.field_200425_b.func_200391_b();
      Iterator var3 = this.field_200425_b.func_200389_a().iterator();

      while(var3.hasNext()) {
         Path var4 = (Path)var3.next();
         Files.walk(var4).filter((var0) -> {
            return var0.toString().endsWith(".snbt");
         }).forEach((var4x) -> {
            this.func_208314_a(var1, var4x, this.func_200423_a(var4, var4x), var2);
         });
      }

   }

   public String func_200397_b() {
      return "SNBT -> NBT";
   }

   private String func_200423_a(Path var1, Path var2) {
      String var3 = var1.relativize(var2).toString().replaceAll("\\\\", "/");
      return var3.substring(0, var3.length() - ".snbt".length());
   }

   private void func_208314_a(DirectoryCache var1, Path var2, String var3, Path var4) {
      try {
         Path var5 = var4.resolve(var3 + ".nbt");
         BufferedReader var6 = Files.newBufferedReader(var2);
         Throwable var7 = null;

         try {
            String var8 = IOUtils.toString(var6);
            String var9 = field_208307_a.hashUnencodedChars(var8).toString();
            if (!Objects.equals(var1.func_208323_a(var5), var9) || !Files.exists(var5, new LinkOption[0])) {
               Files.createDirectories(var5.getParent());
               OutputStream var10 = Files.newOutputStream(var5);
               Throwable var11 = null;

               try {
                  CompressedStreamTools.func_74799_a(JsonToNBT.func_180713_a(var8), var10);
               } catch (Throwable var38) {
                  var11 = var38;
                  throw var38;
               } finally {
                  if (var10 != null) {
                     if (var11 != null) {
                        try {
                           var10.close();
                        } catch (Throwable var37) {
                           var11.addSuppressed(var37);
                        }
                     } else {
                        var10.close();
                     }
                  }

               }
            }

            var1.func_208316_a(var5, var9);
         } catch (Throwable var40) {
            var7 = var40;
            throw var40;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var36) {
                     var7.addSuppressed(var36);
                  }
               } else {
                  var6.close();
               }
            }

         }
      } catch (CommandSyntaxException var42) {
         field_200424_a.error("Couldn't convert {} from SNBT to NBT at {} as it's invalid SNBT", var3, var2, var42);
      } catch (IOException var43) {
         field_200424_a.error("Couldn't convert {} from SNBT to NBT at {}", var3, var2, var43);
      }

   }
}
