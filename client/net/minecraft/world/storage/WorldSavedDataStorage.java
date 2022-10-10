package net.minecraft.world.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.dimension.DimensionType;

public class WorldSavedDataStorage {
   private final Map<DimensionType, DimensionSavedDataManager> field_212427_a;
   @Nullable
   private final ISaveHandler field_75751_a;

   public WorldSavedDataStorage(@Nullable ISaveHandler var1) {
      super();
      this.field_75751_a = var1;
      Builder var2 = ImmutableMap.builder();
      Iterator var3 = DimensionType.func_212681_b().iterator();

      while(var3.hasNext()) {
         DimensionType var4 = (DimensionType)var3.next();
         DimensionSavedDataManager var5 = new DimensionSavedDataManager(var4, var1);
         var2.put(var4, var5);
         var5.func_75746_b();
      }

      this.field_212427_a = var2.build();
   }

   @Nullable
   public <T extends WorldSavedData> T func_212426_a(DimensionType var1, Function<String, T> var2, String var3) {
      return ((DimensionSavedDataManager)this.field_212427_a.get(var1)).func_201067_a(var2, var3);
   }

   public void func_212424_a(DimensionType var1, String var2, WorldSavedData var3) {
      ((DimensionSavedDataManager)this.field_212427_a.get(var1)).func_75745_a(var2, var3);
   }

   public void func_75744_a() {
      this.field_212427_a.values().forEach(DimensionSavedDataManager::func_212775_b);
   }

   public int func_212425_a(DimensionType var1, String var2) {
      return ((DimensionSavedDataManager)this.field_212427_a.get(var1)).func_75743_a(var2);
   }

   public NBTTagCompound func_208028_a(String var1, int var2) throws IOException {
      return DimensionSavedDataManager.func_212774_a(this.field_75751_a, DimensionType.OVERWORLD, var1, var2);
   }
}
