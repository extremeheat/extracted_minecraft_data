package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
   public AnvilSaveHandler(File var1, String var2, @Nullable MinecraftServer var3, DataFixer var4) {
      super(var1, var2, var3, var4);
   }

   public IChunkLoader func_75763_a(Dimension var1) {
      File var2 = var1.func_186058_p().func_212679_a(this.func_75765_b());
      var2.mkdirs();
      return new AnvilChunkLoader(var2, this.field_186341_a);
   }

   public void func_75755_a(WorldInfo var1, @Nullable NBTTagCompound var2) {
      var1.func_76078_e(19133);
      super.func_75755_a(var1, var2);
   }

   public void func_75759_a() {
      try {
         ThreadedFileIOBase.func_178779_a().func_75734_a();
      } catch (InterruptedException var2) {
         var2.printStackTrace();
      }

      RegionFileCache.func_76551_a();
   }
}
