package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import org.slf4j.Logger;

public class SectionStorage<R> implements AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final String SECTIONS_TAG = "Sections";
   private final IOWorker worker;
   private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
   private final Function<Runnable, Codec<R>> codec;
   private final Function<Runnable, R> factory;
   private final DataFixer fixerUpper;
   private final DataFixTypes type;
   private final RegistryAccess registryAccess;
   protected final LevelHeightAccessor levelHeightAccessor;

   public SectionStorage(Path var1, Function<Runnable, Codec<R>> var2, Function<Runnable, R> var3, DataFixer var4, DataFixTypes var5, boolean var6, RegistryAccess var7, LevelHeightAccessor var8) {
      super();
      this.codec = var2;
      this.factory = var3;
      this.fixerUpper = var4;
      this.type = var5;
      this.registryAccess = var7;
      this.levelHeightAccessor = var8;
      this.worker = new IOWorker(var1, var6, var1.getFileName().toString());
   }

   protected void tick(BooleanSupplier var1) {
      while(this.hasWork() && var1.getAsBoolean()) {
         ChunkPos var2 = SectionPos.of(this.dirty.firstLong()).chunk();
         this.writeColumn(var2);
      }

   }

   public boolean hasWork() {
      return !this.dirty.isEmpty();
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
            this.readColumn(SectionPos.of(var1).chunk());
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
            return var3.get();
         } else {
            Object var4 = this.factory.apply(() -> {
               this.setDirty(var1);
            });
            this.storage.put(var1, Optional.of(var4));
            return var4;
         }
      }
   }

   private void readColumn(ChunkPos var1) {
      Optional var2 = (Optional)this.tryRead(var1).join();
      RegistryOps var3 = RegistryOps.create(NbtOps.INSTANCE, this.registryAccess);
      this.readColumn(var1, var3, (Tag)var2.orElse((Object)null));
   }

   private CompletableFuture<Optional<CompoundTag>> tryRead(ChunkPos var1) {
      return this.worker.loadAsync(var1).exceptionally((var1x) -> {
         if (var1x instanceof IOException var2) {
            LOGGER.error("Error reading chunk {} data from disk", var1, var2);
            return Optional.empty();
         } else {
            throw new CompletionException(var1x);
         }
      });
   }

   private <T> void readColumn(ChunkPos var1, DynamicOps<T> var2, @Nullable T var3) {
      if (var3 == null) {
         for(int var4 = this.levelHeightAccessor.getMinSection(); var4 < this.levelHeightAccessor.getMaxSection(); ++var4) {
            this.storage.put(getKey(var1, var4), Optional.empty());
         }
      } else {
         Dynamic var14 = new Dynamic(var2, var3);
         int var5 = getVersion(var14);
         int var6 = SharedConstants.getCurrentVersion().getWorldVersion();
         boolean var7 = var5 != var6;
         Dynamic var8 = this.fixerUpper.update(this.type.getType(), var14, var5, var6);
         OptionalDynamic var9 = var8.get("Sections");

         for(int var10 = this.levelHeightAccessor.getMinSection(); var10 < this.levelHeightAccessor.getMaxSection(); ++var10) {
            long var11 = getKey(var1, var10);
            Optional var13 = var9.get(Integer.toString(var10)).result().flatMap((var3x) -> {
               DataResult var10000 = ((Codec)this.codec.apply(() -> {
                  this.setDirty(var11);
               })).parse(var3x);
               Logger var10001 = LOGGER;
               Objects.requireNonNull(var10001);
               return var10000.resultOrPartial(var10001::error);
            });
            this.storage.put(var11, var13);
            var13.ifPresent((var4x) -> {
               this.onSectionLoad(var11);
               if (var7) {
                  this.setDirty(var11);
               }

            });
         }
      }

   }

   private void writeColumn(ChunkPos var1) {
      RegistryOps var2 = RegistryOps.create(NbtOps.INSTANCE, this.registryAccess);
      Dynamic var3 = this.writeColumn(var1, var2);
      Tag var4 = (Tag)var3.getValue();
      if (var4 instanceof CompoundTag) {
         this.worker.store(var1, (CompoundTag)var4);
      } else {
         LOGGER.error("Expected compound tag, got {}", var4);
      }

   }

   private <T> Dynamic<T> writeColumn(ChunkPos var1, DynamicOps<T> var2) {
      HashMap var3 = Maps.newHashMap();

      for(int var4 = this.levelHeightAccessor.getMinSection(); var4 < this.levelHeightAccessor.getMaxSection(); ++var4) {
         long var5 = getKey(var1, var4);
         this.dirty.remove(var5);
         Optional var7 = (Optional)this.storage.get(var5);
         if (var7 != null && var7.isPresent()) {
            DataResult var8 = ((Codec)this.codec.apply(() -> {
               this.setDirty(var5);
            })).encodeStart(var2, var7.get());
            String var9 = Integer.toString(var4);
            Logger var10001 = LOGGER;
            Objects.requireNonNull(var10001);
            var8.resultOrPartial(var10001::error).ifPresent((var3x) -> {
               var3.put(var2.createString(var9), var3x);
            });
         }
      }

      return new Dynamic(var2, var2.createMap(ImmutableMap.of(var2.createString("Sections"), var2.createMap(var3), var2.createString("DataVersion"), var2.createInt(SharedConstants.getCurrentVersion().getWorldVersion()))));
   }

   private static long getKey(ChunkPos var0, int var1) {
      return SectionPos.asLong(var0.x, var1, var0.z);
   }

   protected void onSectionLoad(long var1) {
   }

   protected void setDirty(long var1) {
      Optional var3 = (Optional)this.storage.get(var1);
      if (var3 != null && var3.isPresent()) {
         this.dirty.add(var1);
      } else {
         LOGGER.warn("No data for position: {}", SectionPos.of(var1));
      }
   }

   private static int getVersion(Dynamic<?> var0) {
      return var0.get("DataVersion").asInt(1945);
   }

   public void flush(ChunkPos var1) {
      if (this.hasWork()) {
         for(int var2 = this.levelHeightAccessor.getMinSection(); var2 < this.levelHeightAccessor.getMaxSection(); ++var2) {
            long var3 = getKey(var1, var2);
            if (this.dirty.contains(var3)) {
               this.writeColumn(var1);
               return;
            }
         }
      }

   }

   public void close() throws IOException {
      this.worker.close();
   }
}
