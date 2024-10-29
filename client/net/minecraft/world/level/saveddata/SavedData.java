package net.minecraft.world.level.saveddata;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;

public abstract class SavedData {
   private boolean dirty;

   public SavedData() {
      super();
   }

   public abstract CompoundTag save(CompoundTag var1, HolderLookup.Provider var2);

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean var1) {
      this.dirty = var1;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public CompoundTag save(HolderLookup.Provider var1) {
      CompoundTag var2 = new CompoundTag();
      var2.put("data", this.save(new CompoundTag(), var1));
      NbtUtils.addCurrentDataVersion(var2);
      this.setDirty(false);
      return var2;
   }

   public static record Factory<T extends SavedData>(Supplier<T> constructor, BiFunction<CompoundTag, HolderLookup.Provider, T> deserializer, DataFixTypes type) {
      public Factory(Supplier<T> var1, BiFunction<CompoundTag, HolderLookup.Provider, T> var2, DataFixTypes var3) {
         super();
         this.constructor = var1;
         this.deserializer = var2;
         this.type = var3;
      }

      public Supplier<T> constructor() {
         return this.constructor;
      }

      public BiFunction<CompoundTag, HolderLookup.Provider, T> deserializer() {
         return this.deserializer;
      }

      public DataFixTypes type() {
         return this.type;
      }
   }
}
