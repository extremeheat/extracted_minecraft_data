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
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.FileUtil;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SavedDataStorage implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<SavedDataType<?>, Optional<SavedData>> cache = new HashMap();
   private final DataFixer fixerUpper;
   private final HolderLookup.Provider registries;
   private final Path dataFolder;
   private CompletableFuture<?> pendingWriteFuture = CompletableFuture.completedFuture((Object)null);
   private boolean closed;

   public SavedDataStorage(final Path dataFolder, final DataFixer fixerUpper, final HolderLookup.Provider registries) {
      super();
      this.fixerUpper = fixerUpper;
      this.dataFolder = dataFolder;
      this.registries = registries;
   }

   private Path getDataFile(final Identifier id) {
      Path path = id.withSuffix(".dat").resolveAgainst(this.dataFolder);
      if (!path.toAbsolutePath().startsWith(this.dataFolder.toAbsolutePath())) {
         throw new IllegalArgumentException("SavedDataStorage attempted file access outside of data directory: {}" + String.valueOf(path));
      } else {
         return path;
      }
   }

   public <T extends SavedData> T computeIfAbsent(final SavedDataType<T> type) {
      T data = this.get(type);
      if (data != null) {
         return data;
      } else {
         T newData = (T)(type.constructor().get());
         this.set(type, newData);
         return newData;
      }
   }

   public <T extends SavedData> @Nullable T get(final SavedDataType<T> type) {
      Optional<SavedData> data = (Optional)this.cache.get(type);
      if (data == null) {
         data = Optional.ofNullable(this.readSavedData(type));
         this.cache.put(type, data);
      }

      return (T)(data.orElse((Object)null));
   }

   private <T extends SavedData> @Nullable T readSavedData(final SavedDataType<T> type) {
      try {
         Path file = this.getDataFile(type.id());
         if (Files.exists(file, new LinkOption[0])) {
            CompoundTag tag = this.readTagFromDisk(file, type.dataFixType(), SharedConstants.getCurrentVersion().dataVersion().version());
            RegistryOps<Tag> ops = this.registries.<Tag>createSerializationContext(NbtOps.INSTANCE);
            return (T)(type.codec().parse(ops, tag.get("data")).resultOrPartial((error) -> LOGGER.error("Failed to parse saved data for '{}': {}", type, error)).orElse((Object)null));
         }
      } catch (Exception e) {
         LOGGER.error("Error loading saved data: {}", type, e);
      }

      return null;
   }

   public <T extends SavedData> void set(final SavedDataType<T> type, final T data) {
      this.cache.put(type, Optional.of(data));
      data.setDirty();
   }

   public CompoundTag readTagFromDisk(final Path dataFile, final DataFixTypes type, final int newVersion) throws IOException {
      InputStream in = Files.newInputStream(dataFile);

      CompoundTag var8;
      try {
         PushbackInputStream inputStream = new PushbackInputStream(new FastBufferedInputStream(in), 2);

         try {
            CompoundTag tag;
            if (this.isGzip(inputStream)) {
               tag = NbtIo.readCompressed((InputStream)inputStream, NbtAccounter.unlimitedHeap());
            } else {
               DataInputStream dis = new DataInputStream(inputStream);

               try {
                  tag = NbtIo.read((DataInput)dis);
               } catch (Throwable var13) {
                  try {
                     dis.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }

                  throw var13;
               }

               dis.close();
            }

            int version = NbtUtils.getDataVersion(tag, 1343);
            var8 = type.update(this.fixerUpper, tag, version, newVersion);
         } catch (Throwable var14) {
            try {
               inputStream.close();
            } catch (Throwable var11) {
               var14.addSuppressed(var11);
            }

            throw var14;
         }

         inputStream.close();
      } catch (Throwable var15) {
         if (in != null) {
            try {
               in.close();
            } catch (Throwable var10) {
               var15.addSuppressed(var10);
            }
         }

         throw var15;
      }

      if (in != null) {
         in.close();
      }

      return var8;
   }

   private boolean isGzip(final PushbackInputStream inputStream) throws IOException {
      byte[] header = new byte[2];
      boolean gzip = false;
      int read = inputStream.read(header, 0, 2);
      if (read == 2) {
         int fullHeader = (header[1] & 255) << 8 | header[0] & 255;
         if (fullHeader == 35615) {
            gzip = true;
         }
      }

      if (read != 0) {
         inputStream.unread(header, 0, read);
      }

      return gzip;
   }

   public CompletableFuture<?> scheduleSave() {
      if (this.closed) {
         throw new IllegalStateException("Trying to schedule save when SavedDataStorage is already closed");
      } else {
         Map<SavedDataType<?>, CompoundTag> tagsToSave = this.collectDirtyTagsToSave();
         if (tagsToSave.isEmpty()) {
            return CompletableFuture.completedFuture((Object)null);
         } else {
            int threads = Util.maxAllowedExecutorThreads();
            int taskCount = tagsToSave.size();
            if (taskCount > threads) {
               this.pendingWriteFuture = this.pendingWriteFuture.thenCompose((ignored) -> {
                  List<CompletableFuture<?>> tasks = new ArrayList(threads);
                  int bucketSize = Mth.positiveCeilDiv(taskCount, threads);

                  for(List<Map.Entry<SavedDataType<?>, CompoundTag>> entries : Iterables.partition(tagsToSave.entrySet(), bucketSize)) {
                     tasks.add(CompletableFuture.runAsync(() -> {
                        for(Map.Entry<SavedDataType<?>, CompoundTag> entry : entries) {
                           this.tryWrite((SavedDataType)entry.getKey(), (CompoundTag)entry.getValue());
                        }

                     }, Util.ioPool()));
                  }

                  return CompletableFuture.allOf((CompletableFuture[])tasks.toArray((x$0) -> new CompletableFuture[x$0]));
               });
            } else {
               this.pendingWriteFuture = this.pendingWriteFuture.thenCompose((ignored) -> CompletableFuture.allOf((CompletableFuture[])tagsToSave.entrySet().stream().map((entry) -> CompletableFuture.runAsync(() -> this.tryWrite((SavedDataType)entry.getKey(), (CompoundTag)entry.getValue()), Util.ioPool())).toArray((x$0) -> new CompletableFuture[x$0])));
            }

            return this.pendingWriteFuture;
         }
      }
   }

   private Map<SavedDataType<?>, CompoundTag> collectDirtyTagsToSave() {
      Map<SavedDataType<?>, CompoundTag> tagsToSave = new Object2ObjectArrayMap();
      RegistryOps<Tag> ops = this.registries.<Tag>createSerializationContext(NbtOps.INSTANCE);
      this.cache.forEach((type, optional) -> optional.filter(SavedData::isDirty).ifPresent((data) -> {
            tagsToSave.put(type, this.encodeUnchecked(type, data, ops));
            data.setDirty(false);
         }));
      return tagsToSave;
   }

   private <T extends SavedData> CompoundTag encodeUnchecked(final SavedDataType<T> type, final SavedData data, final RegistryOps<Tag> ops) {
      Codec<T> codec = type.codec();
      CompoundTag tag = new CompoundTag();
      tag.put("data", (Tag)codec.encodeStart(ops, data).getOrThrow());
      NbtUtils.addCurrentDataVersion(tag);
      return tag;
   }

   private void tryWrite(final SavedDataType<?> type, final CompoundTag tag) {
      Path path = this.getDataFile(type.id());

      try {
         FileUtil.createDirectoriesSafe(path.getParent());
         NbtIo.writeCompressed(tag, path);
      } catch (IOException e) {
         LOGGER.error("Could not save data to {}", path.getFileName(), e);
      }

   }

   public void saveAndJoin() {
      this.scheduleSave().join();
   }

   public void close() {
      if (this.closed) {
         throw new IllegalStateException("Trying to close SavedDataStorage when it is already closed");
      } else {
         this.saveAndJoin();
         this.closed = true;
      }
   }
}
