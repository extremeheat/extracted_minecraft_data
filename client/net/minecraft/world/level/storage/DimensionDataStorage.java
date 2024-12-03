package net.minecraft.world.level.storage;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
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
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import org.slf4j.Logger;

public class DimensionDataStorage implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<String, Optional<SavedData>> cache = new HashMap();
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
      Optional var3 = (Optional)this.cache.get(var2);
      if (var3 == null) {
         var3 = Optional.ofNullable(this.readSavedData(var1.deserializer(), var1.type(), var2));
         this.cache.put(var2, var3);
      }

      return (T)(var3.orElse((Object)null));
   }

   @Nullable
   private <T extends SavedData> T readSavedData(BiFunction<CompoundTag, HolderLookup.Provider, T> var1, DataFixTypes var2, String var3) {
      try {
         Path var4 = this.getDataFile(var3);
         if (Files.exists(var4, new LinkOption[0])) {
            CompoundTag var5 = this.readTagFromDisk(var3, var2, SharedConstants.getCurrentVersion().getDataVersion().getVersion());
            return (T)(var1.apply(var5.getCompound("data"), this.registries));
         }
      } catch (Exception var6) {
         LOGGER.error("Error loading saved data: {}", var3, var6);
      }

      return null;
   }

   public void set(String var1, SavedData var2) {
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
            this.pendingWriteFuture = this.pendingWriteFuture.thenCompose((var3x) -> {
               ArrayList var4 = new ArrayList(var2);
               int var5 = Mth.positiveCeilDiv(var3, var2);

               for(List var7 : Iterables.partition(var1.entrySet(), var5)) {
                  var4.add(CompletableFuture.runAsync(() -> {
                     for(Map.Entry var2 : var7) {
                        tryWrite((Path)var2.getKey(), (CompoundTag)var2.getValue());
                     }

                  }, Util.ioPool()));
               }

               return CompletableFuture.allOf((CompletableFuture[])var4.toArray((var0) -> new CompletableFuture[var0]));
            });
         } else {
            this.pendingWriteFuture = this.pendingWriteFuture.thenCompose((var1x) -> CompletableFuture.allOf((CompletableFuture[])var1.entrySet().stream().map((var0) -> CompletableFuture.runAsync(() -> tryWrite((Path)var0.getKey(), (CompoundTag)var0.getValue()), Util.ioPool())).toArray((var0) -> new CompletableFuture[var0])));
         }

         return this.pendingWriteFuture;
      }
   }

   private Map<Path, CompoundTag> collectDirtyTagsToSave() {
      Object2ObjectArrayMap var1 = new Object2ObjectArrayMap();
      this.cache.forEach((var2, var3) -> var3.filter(SavedData::isDirty).ifPresent((var3x) -> var1.put(this.getDataFile(var2), var3x.save(this.registries))));
      return var1;
   }

   private static void tryWrite(Path var0, CompoundTag var1) {
      try {
         NbtIo.writeCompressed(var1, var0);
      } catch (IOException var3) {
         LOGGER.error("Could not save data to {}", var0.getFileName(), var3);
      }

   }

   public void saveAndJoin() {
      this.scheduleSave().join();
   }

   public void close() {
      this.saveAndJoin();
   }
}
