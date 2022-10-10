package net.minecraft.world;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;

public class WorldServerDemo extends WorldServer {
   private static final long field_73072_L = (long)"North Carolina".hashCode();
   public static final WorldSettings field_73071_a;

   public WorldServerDemo(MinecraftServer var1, ISaveHandler var2, WorldSavedDataStorage var3, WorldInfo var4, DimensionType var5, Profiler var6) {
      super(var1, var2, var3, var4, var5, var6);
      this.field_72986_A.func_176127_a(field_73071_a);
   }

   static {
      field_73071_a = (new WorldSettings(field_73072_L, GameType.SURVIVAL, true, false, WorldType.field_77137_b)).func_77159_a();
   }
}
