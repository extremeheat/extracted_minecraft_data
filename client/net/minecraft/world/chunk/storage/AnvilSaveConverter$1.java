package net.minecraft.world.chunk.storage;

import java.io.File;
import java.io.FilenameFilter;

class AnvilSaveConverter$1 implements FilenameFilter {
   AnvilSaveConverter$1(AnvilSaveConverter var1) {
      super();
      this.field_76172_a = var1;
   }

   @Override
   public boolean accept(File var1, String var2) {
      return var2.endsWith(".mcr");
   }
}
