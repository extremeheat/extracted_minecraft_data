package net.minecraft.world;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.WorldSavedDataStorage;

public interface ISaveDataAccess {
   @Nullable
   WorldSavedDataStorage func_175693_T();

   @Nullable
   default <T extends WorldSavedData> T func_212411_a(DimensionType var1, Function<String, T> var2, String var3) {
      WorldSavedDataStorage var4 = this.func_175693_T();
      return var4 == null ? null : var4.func_212426_a(var1, var2, var3);
   }

   default void func_212409_a(DimensionType var1, String var2, WorldSavedData var3) {
      WorldSavedDataStorage var4 = this.func_175693_T();
      if (var4 != null) {
         var4.func_212424_a(var1, var2, var3);
      }

   }

   default int func_212410_a(DimensionType var1, String var2) {
      return this.func_175693_T().func_212425_a(var1, var2);
   }
}
