package net.minecraft.world.level.saveddata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   private final String id;
   private boolean dirty;

   public SavedData(String var1) {
      super();
      this.id = var1;
   }

   public abstract void load(CompoundTag var1);

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

   public String getId() {
      return this.id;
   }

   public void save(File var1) {
      if (this.isDirty()) {
         CompoundTag var2 = new CompoundTag();
         var2.put("data", this.save(new CompoundTag()));
         var2.putInt("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

         try {
            FileOutputStream var3 = new FileOutputStream(var1);
            Throwable var4 = null;

            try {
               NbtIo.writeCompressed(var2, var3);
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (var3 != null) {
                  if (var4 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     var3.close();
                  }
               }

            }
         } catch (IOException var16) {
            LOGGER.error("Could not save data {}", this, var16);
         }

         this.setDirty(false);
      }
   }
}
