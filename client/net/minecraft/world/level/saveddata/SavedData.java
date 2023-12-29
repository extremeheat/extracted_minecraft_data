package net.minecraft.world.level.saveddata;

import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;
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

   public abstract CompoundTag save(CompoundTag var1);

   public void setDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean var1) {
      this.dirty = var1;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public void save(File var1) {
      if (this.isDirty()) {
         CompoundTag var2 = new CompoundTag();
         var2.put("data", this.save(new CompoundTag()));
         NbtUtils.addCurrentDataVersion(var2);

         try {
            NbtIo.writeCompressed(var2, var1.toPath());
         } catch (IOException var4) {
            LOGGER.error("Could not save data {}", this, var4);
         }

         this.setDirty(false);
      }
   }

   public static record Factory<T extends SavedData>(Supplier<T> a, Function<CompoundTag, T> b, DataFixTypes c) {
      private final Supplier<T> constructor;
      private final Function<CompoundTag, T> deserializer;
      private final DataFixTypes type;

      public Factory(Supplier<T> var1, Function<CompoundTag, T> var2, DataFixTypes var3) {
         super();
         this.constructor = var1;
         this.deserializer = var2;
         this.type = var3;
      }
   }
}
