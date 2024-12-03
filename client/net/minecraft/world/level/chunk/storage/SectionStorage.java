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
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import org.slf4j.Logger;

public class SectionStorage<R, P> implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
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

   public SectionStorage(SimpleRegionStorage var1, Codec<P> var2, Function<R, P> var3, BiFunction<P, Runnable, R> var4, Function<Runnable, R> var5, RegistryAccess var6, ChunkIOErrorReporter var7, LevelHeightAccessor var8) {
      super();
      this.simpleRegionStorage = var1;
      this.codec = var2;
      this.packer = var3;
      this.unpacker = var4;
      this.factory = var5;
      this.registryAccess = var6;
      this.errorReporter = var7;
      this.levelHeightAccessor = var8;
   }

   protected void tick(BooleanSupplier var1) {
      LongListIterator var2 = this.dirtyChunks.iterator();

      while(var2.hasNext() && var1.getAsBoolean()) {
         ChunkPos var3 = new ChunkPos(var2.nextLong());
         var2.remove();
         this.writeChunk(var3);
      }

      this.unpackPendingLoads();
   }

   private void unpackPendingLoads() {
      synchronized(this.loadLock) {
         ObjectIterator var2 = Long2ObjectMaps.fastIterator(this.pendingLoads);

         while(var2.hasNext()) {
            Long2ObjectMap.Entry var3 = (Long2ObjectMap.Entry)var2.next();
            Optional var4 = (Optional)((CompletableFuture)var3.getValue()).getNow((Object)null);
            if (var4 != null) {
               long var5 = var3.getLongKey();
               this.unpackChunk(new ChunkPos(var5), (PackedChunk)var4.orElse((Object)null));
               var2.remove();
               this.loadedChunks.add(var5);
            }
         }

      }
   }

   public void flushAll() {
      if (!this.dirtyChunks.isEmpty()) {
         this.dirtyChunks.forEach((var1) -> this.writeChunk(new ChunkPos(var1)));
         this.dirtyChunks.clear();
      }

   }

   public boolean hasWork() {
      return !this.dirtyChunks.isEmpty();
   }

   @Nullable
   protected Optional<R> get(long var1) {
      return (Optional)this.storage.get(var1);
   }

   protected Optional<R> getOrLoad(long var1) {
      if (this.outsideStoredRange(var1)) {
         return Optional.empty();
      } else {
         Optional var3 = this.get(var1);
         if (var3 != null) {
            return var3;
         } else {
            this.unpackChunk(SectionPos.of(var1).chunk());
            var3 = this.get(var1);
            if (var3 == null) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
            } else {
               return var3;
            }
         }
      }
   }

   protected boolean outsideStoredRange(long var1) {
      int var3 = SectionPos.sectionToBlockCoord(SectionPos.y(var1));
      return this.levelHeightAccessor.isOutsideBuildHeight(var3);
   }

   protected R getOrCreate(long var1) {
      if (this.outsideStoredRange(var1)) {
         throw (IllegalArgumentException)Util.pauseInIde(new IllegalArgumentException("sectionPos out of bounds"));
      } else {
         Optional var3 = this.getOrLoad(var1);
         if (var3.isPresent()) {
            return (R)var3.get();
         } else {
            Object var4 = this.factory.apply((Runnable)() -> this.setDirty(var1));
            this.storage.put(var1, Optional.of(var4));
            return (R)var4;
         }
      }
   }

   public CompletableFuture<?> prefetch(ChunkPos var1) {
      synchronized(this.loadLock) {
         long var3 = var1.toLong();
         return this.loadedChunks.contains(var3) ? CompletableFuture.completedFuture((Object)null) : (CompletableFuture)this.pendingLoads.computeIfAbsent(var3, (var2) -> this.tryRead(var1));
      }
   }

   private void unpackChunk(ChunkPos var1) {
      long var2 = var1.toLong();
      CompletableFuture var4;
      synchronized(this.loadLock) {
         if (!this.loadedChunks.add(var2)) {
            return;
         }

         var4 = (CompletableFuture)this.pendingLoads.computeIfAbsent(var2, (var2x) -> this.tryRead(var1));
      }

      this.unpackChunk(var1, (PackedChunk)((Optional)var4.join()).orElse((Object)null));
      synchronized(this.loadLock) {
         this.pendingLoads.remove(var2);
      }
   }

   private CompletableFuture<Optional<PackedChunk<P>>> tryRead(ChunkPos var1) {
      RegistryOps var2 = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
      return this.simpleRegionStorage.read(var1).thenApplyAsync((var2x) -> var2x.map((var2xx) -> SectionStorage.PackedChunk.parse(this.codec, var2, var2xx, this.simpleRegionStorage, this.levelHeightAccessor)), Util.backgroundExecutor().forName("parseSection")).exceptionally((var2x) -> {
         if (var2x instanceof CompletionException) {
            var2x = var2x.getCause();
         }

         if (var2x instanceof IOException var3) {
            LOGGER.error("Error reading chunk {} data from disk", var1, var3);
            this.errorReporter.reportChunkLoadFailure(var3, this.simpleRegionStorage.storageInfo(), var1);
            return Optional.empty();
         } else {
            throw new CompletionException(var2x);
         }
      });
   }

   private void unpackChunk(ChunkPos var1, @Nullable PackedChunk<P> var2) {
      if (var2 == null) {
         for(int var3 = this.levelHeightAccessor.getMinSectionY(); var3 <= this.levelHeightAccessor.getMaxSectionY(); ++var3) {
            this.storage.put(getKey(var1, var3), Optional.empty());
         }
      } else {
         boolean var8 = var2.versionChanged();

         for(int var4 = this.levelHeightAccessor.getMinSectionY(); var4 <= this.levelHeightAccessor.getMaxSectionY(); ++var4) {
            long var5 = getKey(var1, var4);
            Optional var7 = Optional.ofNullable(var2.sectionsByY.get(var4)).map((var3x) -> this.unpacker.apply(var3x, (Runnable)() -> this.setDirty(var5)));
            this.storage.put(var5, var7);
            var7.ifPresent((var4x) -> {
               this.onSectionLoad(var5);
               if (var8) {
                  this.setDirty(var5);
               }

            });
         }
      }

   }

   private void writeChunk(ChunkPos var1) {
      RegistryOps var2 = this.registryAccess.createSerializationContext(NbtOps.INSTANCE);
      Dynamic var3 = this.writeChunk(var1, var2);
      Tag var4 = (Tag)var3.getValue();
      if (var4 instanceof CompoundTag) {
         this.simpleRegionStorage.write(var1, (CompoundTag)var4).exceptionally((var2x) -> {
            this.errorReporter.reportChunkSaveFailure(var2x, this.simpleRegionStorage.storageInfo(), var1);
            return null;
         });
      } else {
         LOGGER.error("Expected compound tag, got {}", var4);
      }

   }

   private <T> Dynamic<T> writeChunk(ChunkPos var1, DynamicOps<T> var2) {
      HashMap var3 = Maps.newHashMap();

      for(int var4 = this.levelHeightAccessor.getMinSectionY(); var4 <= this.levelHeightAccessor.getMaxSectionY(); ++var4) {
         long var5 = getKey(var1, var4);
         Optional var7 = (Optional)this.storage.get(var5);
         if (var7 != null && !var7.isEmpty()) {
            DataResult var8 = this.codec.encodeStart(var2, this.packer.apply(var7.get()));
            String var9 = Integer.toString(var4);
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var8.resultOrPartial(var10001::error).ifPresent((var3x) -> var3.put(var2.createString(var9), var3x));
         }
      }

      return new Dynamic(var2, var2.createMap(ImmutableMap.of(var2.createString("Sections"), var2.createMap(var3), var2.createString("DataVersion"), var2.createInt(SharedConstants.getCurrentVersion().getDataVersion().getVersion()))));
   }

   private static long getKey(ChunkPos var0, int var1) {
      return SectionPos.asLong(var0.x, var1, var0.z);
   }

   protected void onSectionLoad(long var1) {
   }

   protected void setDirty(long var1) {
      Optional var3 = (Optional)this.storage.get(var1);
      if (var3 != null && !var3.isEmpty()) {
         this.dirtyChunks.add(ChunkPos.asLong(SectionPos.x(var1), SectionPos.z(var1)));
      } else {
         LOGGER.warn("No data for position: {}", SectionPos.of(var1));
      }
   }

   static int getVersion(Dynamic<?> var0) {
      return var0.get("DataVersion").asInt(1945);
   }

   public void flush(ChunkPos var1) {
      if (this.dirtyChunks.remove(var1.toLong())) {
         this.writeChunk(var1);
      }

   }

   public void close() throws IOException {
      this.simpleRegionStorage.close();
   }

   static record PackedChunk<T>(Int2ObjectMap<T> sectionsByY, boolean versionChanged) {
      final Int2ObjectMap<T> sectionsByY;

      private PackedChunk(Int2ObjectMap<T> var1, boolean var2) {
         super();
         this.sectionsByY = var1;
         this.versionChanged = var2;
      }

      public static <T> PackedChunk<T> parse(Codec<T> var0, DynamicOps<Tag> var1, Tag var2, SimpleRegionStorage var3, LevelHeightAccessor var4) {
         Dynamic var5 = new Dynamic(var1, var2);
         int var6 = SectionStorage.getVersion(var5);
         int var7 = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
         boolean var8 = var6 != var7;
         Dynamic var9 = var3.upgradeChunkTag(var5, var6);
         OptionalDynamic var10 = var9.get("Sections");
         Int2ObjectOpenHashMap var11 = new Int2ObjectOpenHashMap();

         for(int var12 = var4.getMinSectionY(); var12 <= var4.getMaxSectionY(); ++var12) {
            Optional var13 = var10.get(Integer.toString(var12)).result().flatMap((var1x) -> {
               DataResult var10000 = var0.parse(var1x);
               Logger var10001 = SectionStorage.LOGGER;
               Objects.requireNonNull(var10001);
               return var10000.resultOrPartial(var10001::error);
            });
            if (var13.isPresent()) {
               var11.put(var12, var13.get());
            }
         }

         return new PackedChunk<T>(var11, var8);
      }
   }
}
