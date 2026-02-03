package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SectionStorage<R, P> implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String SECTIONS_TAG = "Sections";
   private final SimpleRegionStorage simpleRegionStorage;
   private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet dirtyChunks = new LongLinkedOpenHashSet();
   private final Codec<P> codec;
   private final Function<R, P> packer;
   private final BiFunction<P, Runnable, R> unpacker;
   private final Function<Runnable, R> factory;
   private final RegistryAccess registryAccess;
   private final ChunkIOErrorReporter errorReporter;
   protected final LevelHeightAccessor levelHeightAccessor;
   private final LongSet loadedChunks = new LongOpenHashSet();
   private final Long2ObjectMap<CompletableFuture<Optional<PackedChunk<P>>>> pendingLoads = new Long2ObjectOpenHashMap();
   private final Object loadLock = new Object();

   public SectionStorage(final SimpleRegionStorage simpleRegionStorage, final Codec<P> codec, final Function<R, P> packer, final BiFunction<P, Runnable, R> unpacker, final Function<Runnable, R> factory, final RegistryAccess registryAccess, final ChunkIOErrorReporter errorReporter, final LevelHeightAccessor levelHeightAccessor) {
      super();
      this.simpleRegionStorage = simpleRegionStorage;
      this.codec = codec;
      this.packer = packer;
      this.unpacker = unpacker;
      this.factory = factory;
      this.registryAccess = registryAccess;
      this.errorReporter = errorReporter;
      this.levelHeightAccessor = levelHeightAccessor;
   }

   protected void tick(final BooleanSupplier haveTime) {
      LongIterator iterator = this.dirtyChunks.iterator();

      while(iterator.hasNext() && haveTime.getAsBoolean()) {
         ChunkPos chunkPos = ChunkPos.unpack(iterator.nextLong());
         iterator.remove();
         this.writeChunk(chunkPos);
      }

      this.unpackPendingLoads();
   }

   private void unpackPendingLoads() {
      synchronized(this.loadLock) {
         Iterator<Long2ObjectMap.Entry<CompletableFuture<Optional<PackedChunk<P>>>>> iterator = Long2ObjectMaps.fastIterator(this.pendingLoads);

         while(iterator.hasNext()) {
            Long2ObjectMap.Entry<CompletableFuture<Optional<PackedChunk<P>>>> entry = (Long2ObjectMap.Entry)iterator.next();
            Optional<PackedChunk<P>> chunk = (Optional)((CompletableFuture)entry.getValue()).getNow((Object)null);
            if (chunk != null) {
               long chunkKey = entry.getLongKey();
               this.unpackChunk(ChunkPos.unpack(chunkKey), (PackedChunk)chunk.orElse((Object)null));
               iterator.remove();
               this.loadedChunks.add(chunkKey);
            }
         }

      }
   }

   public void flushAll() {
      if (!this.dirtyChunks.isEmpty()) {
         this.dirtyChunks.forEach((pos) -> this.writeChunk(ChunkPos.unpack(pos)));
         this.dirtyChunks.clear();
      }

   }

   public boolean hasWork() {
      return !this.dirtyChunks.isEmpty();
   }

   protected @Nullable Optional<R> get(final long sectionPos) {
      return (Optional)this.storage.get(sectionPos);
   }

   protected Optional<R> getOrLoad(final long sectionPos) {
      if (this.outsideStoredRange(sectionPos)) {
         return Optional.empty();
      } else {
         Optional<R> r = this.get(sectionPos);
         if (r != null) {
            return r;
         } else {
            this.unpackChunk(SectionPos.of(sectionPos).chunk());
            r = this.get(sectionPos);
            if (r == null) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
            } else {
               return r;
            }
         }
      }
   }

   protected boolean outsideStoredRange(final long sectionPos) {
      int y = SectionPos.sectionToBlockCoord(SectionPos.y(sectionPos));
      return this.levelHeightAccessor.isOutsideBuildHeight(y);
   }

   protected R getOrCreate(final long sectionPos) {
      if (this.outsideStoredRange(sectionPos)) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("sectionPos out of bounds"));
      } else {
         Optional<R> r = this.getOrLoad(sectionPos);
         if (r.isPresent()) {
            return (R)r.get();
         } else {
            R newR = (R)this.factory.apply((Runnable)() -> this.setDirty(sectionPos));
            this.storage.put(sectionPos, Optional.of(newR));
            return newR;
         }
      }
   }

   public CompletableFuture<?> prefetch(final ChunkPos chunkPos) {
      synchronized(this.loadLock) {
         long chunkKey = chunkPos.pack();
         return this.loadedChunks.contains(chunkKey) ? CompletableFuture.completedFuture((Object)null) : (CompletableFuture)this.pendingLoads.computeIfAbsent(chunkKey, (k) -> this.tryRead(chunkPos));
      }
   }

   private void unpackChunk(final ChunkPos chunkPos) {
      long chunkKey = chunkPos.pack();
      CompletableFuture<Optional<PackedChunk<P>>> future;
      synchronized(this.loadLock) {
         if (!this.loadedChunks.add(chunkKey)) {
            return;
         }

         future = (CompletableFuture)this.pendingLoads.computeIfAbsent(chunkKey, (k) -> this.tryRead(chunkPos));
      }

      this.unpackChunk(chunkPos, (PackedChunk)((Optional)future.join()).orElse((Object)null));
      synchronized(this.loadLock) {
         this.pendingLoads.remove(chunkKey);
      }
   }

   private CompletableFuture<Optional<PackedChunk<P>>> tryRead(final ChunkPos chunkPos) {
      RegistryOps<Tag> registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
      return this.simpleRegionStorage.read(chunkPos).thenApplyAsync((result) -> result.map((tag) -> SectionStorage.PackedChunk.parse(this.codec, registryOps, tag, this.simpleRegionStorage, this.levelHeightAccessor)), Util.backgroundExecutor().forName("parseSection")).exceptionally((throwable) -> {
         if (throwable instanceof CompletionException) {
            throwable = throwable.getCause();
         }

         if (throwable instanceof IOException e) {
            LOGGER.error("Error reading chunk {} data from disk", chunkPos, e);
            this.errorReporter.reportChunkLoadFailure(e, this.simpleRegionStorage.storageInfo(), chunkPos);
            return Optional.empty();
         } else {
            throw new CompletionException(throwable);
         }
      });
   }

   private void unpackChunk(final ChunkPos pos, final @Nullable PackedChunk<P> packedChunk) {
      if (packedChunk == null) {
         for(int sectionY = this.levelHeightAccessor.getMinSectionY(); sectionY <= this.levelHeightAccessor.getMaxSectionY(); ++sectionY) {
            this.storage.put(getKey(pos, sectionY), Optional.empty());
         }
      } else {
         boolean versionChanged = packedChunk.versionChanged();

         for(int sectionY = this.levelHeightAccessor.getMinSectionY(); sectionY <= this.levelHeightAccessor.getMaxSectionY(); ++sectionY) {
            long key = getKey(pos, sectionY);
            Optional<R> section = Optional.ofNullable(packedChunk.sectionsByY.get(sectionY)).map((packed) -> this.unpacker.apply(packed, (Runnable)() -> this.setDirty(key)));
            this.storage.put(key, section);
            section.ifPresent((s) -> {
               this.onSectionLoad(key);
               if (versionChanged) {
                  this.setDirty(key);
               }

            });
         }
      }

   }

   private void writeChunk(final ChunkPos chunkPos) {
      RegistryOps<Tag> registryOps = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
      Dynamic<Tag> tag = this.<Tag>writeChunk(chunkPos, registryOps);
      Tag value = (Tag)tag.getValue();
      if (value instanceof CompoundTag compoundTag) {
         this.simpleRegionStorage.write(chunkPos, compoundTag).exceptionally((throwable) -> {
            this.errorReporter.reportChunkSaveFailure(throwable, this.simpleRegionStorage.storageInfo(), chunkPos);
            return null;
         });
      } else {
         LOGGER.error("Expected compound tag, got {}", value);
      }

   }

   private <T> Dynamic<T> writeChunk(final ChunkPos chunkPos, final DynamicOps<T> ops) {
      Map<T, T> sections = Maps.newHashMap();

      for(int sectionY = this.levelHeightAccessor.getMinSectionY(); sectionY <= this.levelHeightAccessor.getMaxSectionY(); ++sectionY) {
         long key = getKey(chunkPos, sectionY);
         Optional<R> r = (Optional)this.storage.get(key);
         if (r != null && !r.isEmpty()) {
            DataResult<T> serializedSection = this.codec.encodeStart(ops, this.packer.apply(r.get()));
            String yName = Integer.toString(sectionY);
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            serializedSection.resultOrPartial(var10001::error).ifPresent((s) -> sections.put(ops.createString(yName), s));
         }
      }

      return new Dynamic(ops, ops.createMap(ImmutableMap.of(ops.createString("Sections"), ops.createMap(sections), ops.createString("DataVersion"), ops.createInt(SharedConstants.getCurrentVersion().dataVersion().version()))));
   }

   private static long getKey(final ChunkPos chunkPos, final int sectionY) {
      return SectionPos.asLong(chunkPos.x(), sectionY, chunkPos.z());
   }

   protected void onSectionLoad(final long sectionPos) {
   }

   protected void setDirty(final long sectionPos) {
      Optional<R> r = (Optional)this.storage.get(sectionPos);
      if (r != null && !r.isEmpty()) {
         this.dirtyChunks.add(ChunkPos.pack(SectionPos.x(sectionPos), SectionPos.z(sectionPos)));
      } else {
         LOGGER.warn("No data for position: {}", SectionPos.of(sectionPos));
      }
   }

   public void flush(final ChunkPos chunkPos) {
      if (this.dirtyChunks.remove(chunkPos.pack())) {
         this.writeChunk(chunkPos);
      }

   }

   public void close() throws IOException {
      this.simpleRegionStorage.close();
   }

   private static record PackedChunk<T>(Int2ObjectMap<T> sectionsByY, boolean versionChanged) {
      private PackedChunk {
         super();
      }

      public static <T> PackedChunk<T> parse(final Codec<T> codec, final DynamicOps<Tag> ops, final Tag tag, final SimpleRegionStorage simpleRegionStorage, final LevelHeightAccessor levelHeightAccessor) {
         Dynamic<Tag> originalTag = new Dynamic(ops, tag);
         Dynamic<Tag> fixedTag = simpleRegionStorage.upgradeChunkTag((Dynamic)originalTag, 1945);
         boolean versionChanged = originalTag != fixedTag;
         OptionalDynamic<Tag> sections = fixedTag.get("Sections");
         Int2ObjectMap<T> sectionsByY = new Int2ObjectOpenHashMap();

         for(int sectionY = levelHeightAccessor.getMinSectionY(); sectionY <= levelHeightAccessor.getMaxSectionY(); ++sectionY) {
            Optional<T> section = sections.get(Integer.toString(sectionY)).result().flatMap((sectionData) -> {
               DataResult var10000 = codec.parse(sectionData);
               Logger var10001 = SectionStorage.LOGGER;
               Objects.requireNonNull(var10001);
               return var10000.resultOrPartial(var10001::error);
            });
            if (section.isPresent()) {
               sectionsByY.put(sectionY, section.get());
            }
         }

         return new PackedChunk<T>(sectionsByY, versionChanged);
      }
   }
}
