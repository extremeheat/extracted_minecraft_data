package net.minecraft.world.level.storage;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class DimensionDataStorage implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<SavedDataType<?>, Optional<SavedData>> cache = new HashMap();
   private final DataFixer fixerUpper;
   private final HolderLookup.Provider registries;
   private final Path dataFolder;
   private CompletableFuture<?> pendingWriteFuture = CompletableFuture.completedFuture((Object)null);

   public DimensionDataStorage(Path var1, DataFixer var2, HolderLookup.Provider var3) {
      super();
      this.fixerUpper = var2;
      this.dataFolder = var1;
      this.registries = var3;
   }

   private Path getDataFile(String var1) {
      return this.dataFolder.resolve(var1 + ".dat");
   }

   public <T extends SavedData> T computeIfAbsent(SavedDataType<T> var1) {
      SavedData var2 = this.get(var1);
      if (var2 != null) {
         return (T)var2;
      } else {
         SavedData var3 = (SavedData)var1.constructor().get();
         this.set(var1, var3);
         return (T)var3;
      }
   }

   public <T extends SavedData> @Nullable T get(SavedDataType<T> var1) {
      Optional var2 = (Optional)this.cache.get(var1);
      if (var2 == null) {
         var2 = Optional.ofNullable(this.readSavedData(var1));
         this.cache.put(var1, var2);
      }

      return (T)(var2.orElse((Object)null));
   }

   private <T extends SavedData> @Nullable T readSavedData(SavedDataType<T> var1) {
      try {
         Path var2 = this.getDataFile(var1.id());
         if (Files.exists(var2, new LinkOption[0])) {
            CompoundTag var3 = this.readTagFromDisk(var1.id(), var1.dataFixType(), SharedConstants.getCurrentVersion().dataVersion().version());
            RegistryOps var4 = this.registries.createSerializationContext(NbtOps.INSTANCE);
            return (T)(var1.codec().parse(var4, var3.get("data")).resultOrPartial((var1x) -> LOGGER.error("Failed to parse saved data for '{}': {}", var1, var1x)).orElse((Object)null));
         }
      } catch (Exception var5) {
         LOGGER.error("Error loading saved data: {}", var1, var5);
      }

      return null;
   }

   public <T extends SavedData> void set(SavedDataType<T> var1, T var2) {
      this.cache.put(var1, Optional.of(var2));
      var2.setDirty();
   }

   public CompoundTag readTagFromDisk(String var1, DataFixTypes var2, int var3) throws IOException {
      InputStream var4 = Files.newInputStream(this.getDataFile(var1));

      CompoundTag var8;
      try {
         PushbackInputStream var5 = new PushbackInputStream(new FastBufferedInputStream(var4), 2);

         try {
            CompoundTag var6;
            if (this.isGzip(var5)) {
               var6 = NbtIo.readCompressed((InputStream)var5, NbtAccounter.unlimitedHeap());
            } else {
               DataInputStream var7 = new DataInputStream(var5);

               try {
                  var6 = NbtIo.read((DataInput)var7);
               } catch (Throwable var13) {
                  try {
                     var7.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }

                  throw var13;
               }

               var7.close();
            }

            int var16 = NbtUtils.getDataVersion(var6, 1343);
            var8 = var2.update(this.fixerUpper, var6, var16, var3);
         } catch (Throwable var14) {
            try {
               var5.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }

            throw var14;
         }

         var5.close();
      } catch (Throwable var15) {
         if (var4 != null) {
            try {
               var4.close();
            } catch (Throwable var10) {
               var15.addSuppressed(var10);
            }
         }

         throw var15;
      }

      if (var4 != null) {
         var4.close();
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

   public CompletableFuture<?> scheduleSave() {
      Map var1 = this.collectDirtyTagsToSave();
      if (var1.isEmpty()) {
         return CompletableFuture.completedFuture((Object)null);
      } else {
         int var2 = Util.maxAllowedExecutorThreads();
         int var3 = var1.size();
         if (var3 > var2) {
            this.pendingWriteFuture = this.pendingWriteFuture.thenCompose((var4) -> {
               ArrayList var5 = new ArrayList(var2);
               int var6 = Mth.positiveCeilDiv(var3, var2);

               for(List var8 : Iterables.partition(var1.entrySet(), var6)) {
                  var5.add(CompletableFuture.runAsync(() -> {
                     for(Map.Entry var3 : var8) {
                        this.tryWrite((SavedDataType)var3.getKey(), (CompoundTag)var3.getValue());
                     }

                  }, Util.ioPool()));
               }

               return CompletableFuture.allOf((CompletableFuture[])var5.toArray((var0) -> new CompletableFuture[var0]));
            });
         } else {
            this.pendingWriteFuture = this.pendingWriteFuture.thenCompose((var2x) -> CompletableFuture.allOf((CompletableFuture[])var1.entrySet().stream().map((var1x) -> CompletableFuture.runAsync(() -> this.tryWrite((SavedDataType)var1x.getKey(), (CompoundTag)var1x.getValue()), Util.ioPool())).toArray((var0) -> new CompletableFuture[var0])));
         }

         return this.pendingWriteFuture;
      }
   }

   private Map<SavedDataType<?>, CompoundTag> collectDirtyTagsToSave() {
      Object2ObjectArrayMap var1 = new Object2ObjectArrayMap();
      RegistryOps var2 = this.registries.createSerializationContext(NbtOps.INSTANCE);
      this.cache.forEach((var3, var4) -> var4.filter(SavedData::isDirty).ifPresent((var4x) -> {
            var1.put(var3, this.encodeUnchecked(var3, var4x, var2));
            var4x.setDirty(false);
         }));
      return var1;
   }

   private <T extends SavedData> CompoundTag encodeUnchecked(SavedDataType<T> var1, SavedData var2, RegistryOps<Tag> var3) {
      Codec var4 = var1.codec();
      CompoundTag var5 = new CompoundTag();
      var5.put("data", (Tag)var4.encodeStart(var3, var2).getOrThrow());
      NbtUtils.addCurrentDataVersion(var5);
      return var5;
   }

   private void tryWrite(SavedDataType<?> var1, CompoundTag var2) {
      Path var3 = this.getDataFile(var1.id());

      try {
         NbtIo.writeCompressed(var2, var3);
      } catch (IOException var5) {
         LOGGER.error("Could not save data to {}", var3.getFileName(), var5);
      }

   }

   public void saveAndJoin() {
      this.scheduleSave().join();
   }

   public void close() {
      this.saveAndJoin();
   }
}
