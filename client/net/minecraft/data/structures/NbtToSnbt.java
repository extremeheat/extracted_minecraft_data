package net.minecraft.data.structures;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
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
            this.convertStructure(var3x, this.getName(var4, var3x), var2);
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

   private void convertStructure(Path var1, String var2, Path var3) {
      try {
         CompoundTag var4 = NbtIo.readCompressed(Files.newInputStream(var1));
         Component var5 = var4.getPrettyDisplay("    ", 0);
         String var6 = var5.getString() + "\n";
         Path var7 = var3.resolve(var2 + ".snbt");
         Files.createDirectories(var7.getParent());
         BufferedWriter var8 = Files.newBufferedWriter(var7);
         Throwable var9 = null;

         try {
            var8.write(var6);
         } catch (Throwable var19) {
            var9 = var19;
            throw var19;
         } finally {
            if (var8 != null) {
               if (var9 != null) {
                  try {
                     var8.close();
                  } catch (Throwable var18) {
                     var9.addSuppressed(var18);
                  }
               } else {
                  var8.close();
               }
            }

         }

         LOGGER.info("Converted {} from NBT to SNBT", var2);
      } catch (IOException var21) {
         LOGGER.error("Couldn't convert {} from NBT to SNBT at {}", var2, var1, var21);
      }

   }
}
