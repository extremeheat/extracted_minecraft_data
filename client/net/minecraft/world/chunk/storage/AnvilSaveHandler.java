package net.minecraft.world.chunk.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
   public AnvilSaveHandler(File var1, String var2, boolean var3) {
      super(var1, var2, var3);
   }

   @Override
   public IChunkLoader func_75763_a(WorldProvider var1) {
      File var2 = this.func_75765_b();
      if (var1 instanceof WorldProviderHell) {
         File var4 = new File(var2, "DIM-1");
         var4.mkdirs();
         return new AnvilChunkLoader(var4);
      } else if (var1 instanceof WorldProviderEnd) {
         File var3 = new File(var2, "DIM1");
         var3.mkdirs();
         return new AnvilChunkLoader(var3);
      } else {
         return new AnvilChunkLoader(var2);
      }
   }

   @Override
   public void func_75755_a(WorldInfo var1, NBTTagCompound var2) {
      var1.func_76078_e(19133);
      super.func_75755_a(var1, var2);
   }

   @Override
   public void func_75759_a() {
      try {
         ThreadedFileIOBase.field_75741_a.func_75734_a();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      RegionFileCache.func_76551_a();
   }
}
