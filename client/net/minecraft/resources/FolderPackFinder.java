package net.minecraft.resources;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;

public class FolderPackFinder implements IPackFinder {
   private static final FileFilter field_195735_a = (var0) -> {
      boolean var1 = var0.isFile() && var0.getName().endsWith(".zip");
      boolean var2 = var0.isDirectory() && (new File(var0, "pack.mcmeta")).isFile();
      return var1 || var2;
   };
   private final File field_195736_b;

   public FolderPackFinder(File var1) {
      super();
      this.field_195736_b = var1;
   }

   public <T extends ResourcePackInfo> void func_195730_a(Map<String, T> var1, ResourcePackInfo.IFactory<T> var2) {
      if (!this.field_195736_b.isDirectory()) {
         this.field_195736_b.mkdirs();
      }

      File[] var3 = this.field_195736_b.listFiles(field_195735_a);
      if (var3 != null) {
         File[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            File var7 = var4[var6];
            String var8 = "file/" + var7.getName();
            ResourcePackInfo var9 = ResourcePackInfo.func_195793_a(var8, false, this.func_195733_a(var7), var2, ResourcePackInfo.Priority.TOP);
            if (var9 != null) {
               var1.put(var8, var9);
            }
         }

      }
   }

   private Supplier<IResourcePack> func_195733_a(File var1) {
      return var1.isDirectory() ? () -> {
         return new FolderPack(var1);
      } : () -> {
         return new FilePack(var1);
      };
   }
}
