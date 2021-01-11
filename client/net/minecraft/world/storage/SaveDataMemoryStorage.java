package net.minecraft.world.storage;

import net.minecraft.world.WorldSavedData;

public class SaveDataMemoryStorage extends MapStorage {
   public SaveDataMemoryStorage() {
      super((ISaveHandler)null);
   }

   public WorldSavedData func_75742_a(Class<? extends WorldSavedData> var1, String var2) {
      return (WorldSavedData)this.field_75749_b.get(var2);
   }

   public void func_75745_a(String var1, WorldSavedData var2) {
      this.field_75749_b.put(var1, var2);
   }

   public void func_75744_a() {
   }

   public int func_75743_a(String var1) {
      return 0;
   }
}
