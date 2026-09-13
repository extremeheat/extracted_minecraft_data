package net.minecraft.client.resources;

import java.io.File;
import java.io.FileFilter;

final class ResourcePackRepository$1 implements FileFilter {
   ResourcePackRepository$1() {
      super();
   }

   @Override
   public boolean accept(File var1) {
      boolean var2 = var1.isFile() && var1.getName().endsWith(".zip");
      boolean var3 = var1.isDirectory() && new File(var1, "pack.mcmeta").isFile();
      return var2 || var3;
   }
}
