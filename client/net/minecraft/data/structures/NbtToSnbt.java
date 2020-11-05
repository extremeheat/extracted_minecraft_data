package net.minecraft.data.structures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NbtToSnbt implements DataProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final DataGenerator generator;

   public NbtToSnbt(DataGenerator var1) {
      super();
      this.generator = var1;
   }

   public void run(HashCache var1) throws IOException {
      Path var2 = this.generator.getOutputFolder();
      Iterator var3 = this.generator.getInputFolders().iterator();

      while(var3.hasNext()) {
         Path var4 = (Path)var3.next();
         Files.walk(var4).filter((var0) -> {
            return var0.toString().endsWith(".nbt");
         }).forEach((var3x) -> {
            convertStructure(var3x, this.getName(var4, var3x), var2);
         });
      }

   }

   public String getName() {
      return "NBT to SNBT";
   }

   private String getName(Path var1, Path var2) {
      String var3 = var1.relativize(var2).toString().replaceAll("\\\\", "/");
      return var3.substring(0, var3.length() - ".nbt".length());
   }

   @Nullable
   public static Path convertStructure(Path var0, String var1, Path var2) {
      try {
         writeSnbt(var2.resolve(var1 + ".snbt"), NbtUtils.structureToSnbt(NbtIo.readCompressed(Files.newInputStream(var0))));
         LOGGER.info("Converted {} from NBT to SNBT", var1);
         return var2.resolve(var1 + ".snbt");
      } catch (IOException var4) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", var1, var0, var4);
         return null;
      }
   }

   public static void writeSnbt(Path var0, String var1) throws IOException {
      Files.createDirectories(var0.getParent());
      BufferedWriter var2 = Files.newBufferedWriter(var0);
      Throwable var3 = null;

      try {
         var2.write(var1);
         var2.write(10);
      } catch (Throwable var12) {
         var3 = var12;
         throw var12;
      } finally {
         if (var2 != null) {
            if (var3 != null) {
               try {
                  var2.close();
               } catch (Throwable var11) {
                  var3.addSuppressed(var11);
               }
            } else {
               var2.close();
            }
         }

      }

   }
}
