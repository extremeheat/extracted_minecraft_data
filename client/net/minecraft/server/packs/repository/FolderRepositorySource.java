package net.minecraft.server.packs.repository;

import java.io.File;
import java.io.FileFilter;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;

public class FolderRepositorySource implements RepositorySource {
   private static final FileFilter RESOURCEPACK_FILTER = (var0) -> {
      boolean var1 = var0.isFile() && var0.getName().endsWith(".zip");
      boolean var2 = var0.isDirectory() && (new File(var0, "pack.mcmeta")).isFile();
      return var1 || var2;
   };
   private final File folder;
   private final PackSource packSource;

   public FolderRepositorySource(File var1, PackSource var2) {
      super();
      this.folder = var1;
      this.packSource = var2;
   }

   public void loadPacks(Consumer<Pack> var1, Pack.PackConstructor var2) {
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
            Pack var9 = Pack.create(var8, false, this.createSupplier(var7), var2, Pack.Position.TOP, this.packSource);
            if (var9 != null) {
               var1.accept(var9);
            }
         }

      }
   }

   private Supplier<PackResources> createSupplier(File var1) {
      return var1.isDirectory() ? () -> {
         return new FolderPackResources(var1);
      } : () -> {
         return new FilePackResources(var1);
      };
   }
}
