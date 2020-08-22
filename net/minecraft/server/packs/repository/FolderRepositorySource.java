package net.minecraft.server.packs.repository;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.server.packs.FileResourcePack;
import net.minecraft.server.packs.FolderResourcePack;

public class FolderRepositorySource implements RepositorySource {
   private static final FileFilter RESOURCEPACK_FILTER = (var0) -> {
      boolean var1 = var0.isFile() && var0.getName().endsWith(".zip");
      boolean var2 = var0.isDirectory() && (new File(var0, "pack.mcmeta")).isFile();
      return var1 || var2;
   };
   private final File folder;

   public FolderRepositorySource(File var1) {
      this.folder = var1;
   }

   public void loadPacks(Map var1, UnopenedPack.UnopenedPackConstructor var2) {
      if (!this.folder.isDirectory()) {
         this.folder.mkdirs();
      }

      File[] var3 = this.folder.listFiles(RESOURCEPACK_FILTER);
      if (var3 != null) {
         File[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            File var7 = var4[var6];
            String var8 = "file/" + var7.getName();
            UnopenedPack var9 = UnopenedPack.create(var8, false, this.createSupplier(var7), var2, UnopenedPack.Position.TOP);
            if (var9 != null) {
               var1.put(var8, var9);
            }
         }

      }
   }

   private Supplier createSupplier(File var1) {
      return var1.isDirectory() ? () -> {
         return new FolderResourcePack(var1);
      } : () -> {
         return new FileResourcePack(var1);
      };
   }
}
