package net.minecraft.data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTToSNBTConverter implements IDataProvider {
   private static final Logger field_200418_a = LogManager.getLogger();
   private final DataGenerator field_200419_b;

   public NBTToSNBTConverter(DataGenerator var1) {
      super();
      this.field_200419_b = var1;
   }

   public void func_200398_a(DirectoryCache var1) throws IOException {
      Path var2 = this.field_200419_b.func_200391_b();
      Iterator var3 = this.field_200419_b.func_200389_a().iterator();

      while(var3.hasNext()) {
         Path var4 = (Path)var3.next();
         Files.walk(var4).filter((var0) -> {
            return var0.toString().endsWith(".nbt");
         }).forEach((var3x) -> {
            this.func_200414_a(var3x, this.func_200417_a(var4, var3x), var2);
         });
      }

   }

   public String func_200397_b() {
      return "NBT to SNBT";
   }

   private String func_200417_a(Path var1, Path var2) {
      String var3 = var1.relativize(var2).toString().replaceAll("\\\\", "/");
      return var3.substring(0, var3.length() - ".nbt".length());
   }

   private void func_200414_a(Path var1, String var2, Path var3) {
      try {
         NBTTagCompound var4 = CompressedStreamTools.func_74796_a(Files.newInputStream(var1));
         ITextComponent var5 = var4.func_199850_a("    ", 0);
         String var6 = var5.getString();
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

         field_200418_a.info("Converted {} from NBT to SNBT", var2);
      } catch (IOException var21) {
         field_200418_a.error("Couldn't convert {} from NBT to SNBT at {}", var2, var1, var21);
      }

   }
}
