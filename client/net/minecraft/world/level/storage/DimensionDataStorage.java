package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DimensionDataStorage {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<String, SavedData> cache = Maps.newHashMap();
   private final DataFixer fixerUpper;
   private final File dataFolder;

   public DimensionDataStorage(File var1, DataFixer var2) {
      super();
      this.fixerUpper = var2;
      this.dataFolder = var1;
   }

   private File getDataFile(String var1) {
      return new File(this.dataFolder, var1 + ".dat");
   }

   public <T extends SavedData> T computeIfAbsent(Supplier<T> var1, String var2) {
      SavedData var3 = this.get(var1, var2);
      if (var3 != null) {
         return var3;
      } else {
         SavedData var4 = (SavedData)var1.get();
         this.set(var4);
         return var4;
      }
   }

   @Nullable
   public <T extends SavedData> T get(Supplier<T> var1, String var2) {
      SavedData var3 = (SavedData)this.cache.get(var2);
      if (var3 == null && !this.cache.containsKey(var2)) {
         var3 = this.readSavedData(var1, var2);
         this.cache.put(var2, var3);
      }

      return var3;
   }

   @Nullable
   private <T extends SavedData> T readSavedData(Supplier<T> var1, String var2) {
      try {
         File var3 = this.getDataFile(var2);
         if (var3.exists()) {
            SavedData var4 = (SavedData)var1.get();
            CompoundTag var5 = this.readTagFromDisk(var2, SharedConstants.getCurrentVersion().getWorldVersion());
            var4.load(var5.getCompound("data"));
            return var4;
         }
      } catch (Exception var6) {
         LOGGER.error("Error loading saved data: {}", var2, var6);
      }

      return null;
   }

   public void set(SavedData var1) {
      this.cache.put(var1.getId(), var1);
   }

   public CompoundTag readTagFromDisk(String var1, int var2) throws IOException {
      File var3 = this.getDataFile(var1);
      FileInputStream var4 = new FileInputStream(var3);
      Throwable var5 = null;

      Object var10;
      try {
         PushbackInputStream var6 = new PushbackInputStream(var4, 2);
         Throwable var7 = null;

         try {
            CompoundTag var8;
            if (this.isGzip(var6)) {
               var8 = NbtIo.readCompressed((InputStream)var6);
            } else {
               DataInputStream var9 = new DataInputStream(var6);
               var10 = null;

               try {
                  var8 = NbtIo.read((DataInput)var9);
               } catch (Throwable var54) {
                  var10 = var54;
                  throw var54;
               } finally {
                  if (var9 != null) {
                     if (var10 != null) {
                        try {
                           var9.close();
                        } catch (Throwable var53) {
                           ((Throwable)var10).addSuppressed(var53);
                        }
                     } else {
                        var9.close();
                     }
                  }

               }
            }

            int var60 = var8.contains("DataVersion", 99) ? var8.getInt("DataVersion") : 1343;
            var10 = NbtUtils.update(this.fixerUpper, DataFixTypes.SAVED_DATA, var8, var60, var2);
         } catch (Throwable var56) {
            var7 = var56;
            throw var56;
         } finally {
            if (var6 != null) {
               if (var7 != null) {
                  try {
                     var6.close();
                  } catch (Throwable var52) {
                     var7.addSuppressed(var52);
                  }
               } else {
                  var6.close();
               }
            }

         }
      } catch (Throwable var58) {
         var5 = var58;
         throw var58;
      } finally {
         if (var4 != null) {
            if (var5 != null) {
               try {
                  var4.close();
               } catch (Throwable var51) {
                  var5.addSuppressed(var51);
               }
            } else {
               var4.close();
            }
         }

      }

      return (CompoundTag)var10;
   }

   private boolean isGzip(PushbackInputStream var1) throws IOException {
      byte[] var2 = new byte[2];
      boolean var3 = false;
      int var4 = var1.read(var2, 0, 2);
      if (var4 == 2) {
         int var5 = (var2[1] & 255) << 8 | var2[0] & 255;
         if (var5 == 35615) {
            var3 = true;
         }
      }

      if (var4 != 0) {
         var1.unread(var2, 0, var4);
      }

      return var3;
   }

   public void save() {
      Iterator var1 = this.cache.values().iterator();

      while(var1.hasNext()) {
         SavedData var2 = (SavedData)var1.next();
         if (var2 != null) {
            var2.save(this.getDataFile(var2.getId()));
         }
      }

   }
}
