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
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class DimensionDataStorage {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, SavedData> cache = Maps.newHashMap();
   private final DataFixer fixerUpper;
   private final HolderLookup.Provider registries;
   private final File dataFolder;

   public DimensionDataStorage(File var1, DataFixer var2, HolderLookup.Provider var3) {
      super();
      this.fixerUpper = var2;
      this.dataFolder = var1;
      this.registries = var3;
   }

   private File getDataFile(String var1) {
      return new File(this.dataFolder, var1 + ".dat");
   }

   public <T extends SavedData> T computeIfAbsent(SavedData.Factory<T> var1, String var2) {
      SavedData var3 = this.get(var1, var2);
      if (var3 != null) {
         return (T)var3;
      } else {
         SavedData var4 = (SavedData)var1.constructor().get();
         this.set(var2, var4);
         return (T)var4;
      }
   }

   @Nullable
   public <T extends SavedData> T get(SavedData.Factory<T> var1, String var2) {
      SavedData var3 = this.cache.get(var2);
      if (var3 == null && !this.cache.containsKey(var2)) {
         var3 = this.readSavedData(var1.deserializer(), var1.type(), var2);
         this.cache.put(var2, var3);
      }

      return (T)var3;
   }

   @Nullable
   private <T extends SavedData> T readSavedData(BiFunction<CompoundTag, HolderLookup.Provider, T> var1, DataFixTypes var2, String var3) {
      try {
         File var4 = this.getDataFile(var3);
         if (var4.exists()) {
            CompoundTag var5 = this.readTagFromDisk(var3, var2, SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            return (T)var1.apply(var5.getCompound("data"), this.registries);
         }
      } catch (Exception var6) {
         LOGGER.error("Error loading saved data: {}", var3, var6);
      }

      return null;
   }

   public void set(String var1, SavedData var2) {
      this.cache.put(var1, var2);
   }

   public CompoundTag readTagFromDisk(String var1, DataFixTypes var2, int var3) throws IOException {
      File var4 = this.getDataFile(var1);

      CompoundTag var9;
      try (
         FileInputStream var5 = new FileInputStream(var4);
         PushbackInputStream var6 = new PushbackInputStream(new FastBufferedInputStream(var5), 2);
      ) {
         CompoundTag var7;
         if (this.isGzip(var6)) {
            var7 = NbtIo.readCompressed(var6, NbtAccounter.unlimitedHeap());
         } else {
            try (DataInputStream var8 = new DataInputStream(var6)) {
               var7 = NbtIo.read(var8);
            }
         }

         int var17 = NbtUtils.getDataVersion(var7, 1343);
         var9 = var2.update(this.fixerUpper, var7, var17, var3);
      }

      return var9;
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
            var2.save(this.getDataFile(var1), this.registries);
         }
      });
   }
}
