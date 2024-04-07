package net.minecraft.world.level.saveddata;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import org.slf4j.Logger;

public abstract class SavedData {
   private static final Logger LOGGER = LogUtils.getLogger();
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

   public void save(File var1, HolderLookup.Provider var2) {
      if (this.isDirty()) {
         CompoundTag var3 = new CompoundTag();
         var3.put("data", this.save(new CompoundTag(), var2));
         NbtUtils.addCurrentDataVersion(var3);

         try {
            NbtIo.writeCompressed(var3, var1.toPath());
         } catch (IOException var5) {
            LOGGER.error("Could not save data {}", this, var5);
         }

         this.setDirty(false);
      }
   }

   public static record Factory<T extends SavedData>(Supplier<T> constructor, BiFunction<CompoundTag, HolderLookup.Provider, T> deserializer, DataFixTypes type) {
      public Factory(Supplier<T> constructor, BiFunction<CompoundTag, HolderLookup.Provider, T> deserializer, DataFixTypes type) {
         super();
         this.constructor = constructor;
         this.deserializer = deserializer;
         this.type = type;
      }
   }
}
