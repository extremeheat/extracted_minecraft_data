package net.minecraft.world.demo;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class DemoWorldServer extends WorldServer {
   private static final long field_73072_L = (long)"North Carolina".hashCode();
   public static final WorldSettings field_73071_a;

   public DemoWorldServer(MinecraftServer var1, ISaveHandler var2, WorldInfo var3, int var4, Profiler var5) {
      super(var1, var2, var3, var4, var5);
      this.field_72986_A.func_176127_a(field_73071_a);
   }

   static {
      field_73071_a = (new WorldSettings(field_73072_L, WorldSettings.GameType.SURVIVAL, true, false, WorldType.field_77137_b)).func_77159_a();
   }
}
