package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.ISaveHandler;

public class WorldServerMulti extends WorldServer {
   public WorldServerMulti(MinecraftServer var1, ISaveHandler var2, String var3, int var4, WorldSettings var5, WorldServer var6, Profiler var7) {
      super(var1, var2, var3, var4, var5, var7);
      this.field_72988_C = var6.field_72988_C;
      this.field_96442_D = var6.func_96441_U();
      this.field_72986_A = new DerivedWorldInfo(var6.func_72912_H());
   }

   @Override
   protected void func_73042_a() {
   }
}
