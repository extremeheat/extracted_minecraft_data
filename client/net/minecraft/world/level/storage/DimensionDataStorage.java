package net.minecraft.world.level.storage;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class DimensionDataStorage {
   private static final Logger LOGGER = LogUtils.getLogger();
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

   public <T extends SavedData> T computeIfAbsent(Function<CompoundTag, T> var1, Supplier<T> var2, String var3) {
      SavedData var4 = this.get(var1, var3);
      if (var4 != null) {
         return (T)var4;
      } else {
         SavedData var5 = (SavedData)var2.get();
         this.set(var3, var5);
         return (T)var5;
      }
   }

   @Nullable
   public <T extends SavedData> T get(Function<CompoundTag, T> var1, String var2) {
      SavedData var3 = this.cache.get(var2);
      if (var3 == null && !this.cache.containsKey(var2)) {
         var3 = this.readSavedData(var1, var2);
         this.cache.put(var2, var3);
      }

      return (T)var3;
   }

   @Nullable
   private <T extends SavedData> T readSavedData(Function<CompoundTag, T> var1, String var2) {
      try {
         File var3 = this.getDataFile(var2);
         if (var3.exists()) {
            CompoundTag var4 = this.readTagFromDisk(var2, SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            return (T)var1.apply(var4.getCompound("data"));
         }
      } catch (Exception var5) {
         LOGGER.error("Error loading saved data: {}", var2, var5);
      }

      return null;
   }

   public void set(String var1, SavedData var2) {
      this.cache.put(var1, var2);
   }

   public CompoundTag readTagFromDisk(String var1, int var2) throws IOException {
      File var3 = this.getDataFile(var1);

      CompoundTag var8;
      try (
         FileInputStream var4 = new FileInputStream(var3);
         PushbackInputStream var5 = new PushbackInputStream(var4, 2);
      ) {
         CompoundTag var6;
         if (this.isGzip(var5)) {
            var6 = NbtIo.readCompressed(var5);
         } else {
            try (DataInputStream var7 = new DataInputStream(var5)) {
               var6 = NbtIo.read(var7);
            }
         }

         int var16 = NbtUtils.getDataVersion(var6, 1343);
         var8 = DataFixTypes.SAVED_DATA.update(this.fixerUpper, var6, var16, var2);
      }

      return var8;
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
      this.cache.forEach((var1, var2) -> {
         if (var2 != null) {
            var2.save(this.getDataFile(var1));
         }
      });
   }
}
