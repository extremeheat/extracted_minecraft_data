package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
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
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SectionStorage<R> implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String SECTIONS_TAG = "Sections";
   private final IOWorker worker;
   private final Long2ObjectMap<Optional<R>> storage = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
   private final Function<Runnable, Codec<R>> codec;
   private final Function<Runnable, R> factory;
   private final DataFixer fixerUpper;
   private final DataFixTypes type;
   protected final LevelHeightAccessor levelHeightAccessor;

   public SectionStorage(Path var1, Function<Runnable, Codec<R>> var2, Function<Runnable, R> var3, DataFixer var4, DataFixTypes var5, boolean var6, LevelHeightAccessor var7) {
      super();
      this.codec = var2;
      this.factory = var3;
      this.fixerUpper = var4;
      this.type = var5;
      this.levelHeightAccessor = var7;
      this.worker = new IOWorker(var1, var6, var1.getFileName().toString());
   }

   protected void tick(BooleanSupplier var1) {
      while(!this.dirty.isEmpty() && var1.getAsBoolean()) {
         ChunkPos var2 = SectionPos.method_74(this.dirty.firstLong()).chunk();
         this.writeColumn(var2);
      }

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
            this.readColumn(SectionPos.method_74(var1).chunk());
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
      int var3 = SectionPos.sectionToBlockCoord(SectionPos.method_76(var1));
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
      this.readColumn(var1, NbtOps.INSTANCE, this.tryRead(var1));
   }

   @Nullable
   private CompoundTag tryRead(ChunkPos var1) {
      try {
         return this.worker.load(var1);
      } catch (IOException var3) {
         LOGGER.error("Error reading chunk {} data from disk", var1, var3);
         return null;
      }
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
      Dynamic var2 = this.writeColumn(var1, NbtOps.INSTANCE);
      Tag var3 = (Tag)var2.getValue();
      if (var3 instanceof CompoundTag) {
         this.worker.store(var1, (CompoundTag)var3);
      } else {
         LOGGER.error("Expected compound tag, got {}", var3);
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
      return SectionPos.asLong(var0.field_504, var1, var0.field_505);
   }

   protected void onSectionLoad(long var1) {
   }

   protected void setDirty(long var1) {
      Optional var3 = (Optional)this.storage.get(var1);
      if (var3 != null && var3.isPresent()) {
         this.dirty.add(var1);
      } else {
         LOGGER.warn("No data for position: {}", SectionPos.method_74(var1));
      }
   }

   private static int getVersion(Dynamic<?> var0) {
      return var0.get("DataVersion").asInt(1945);
   }

   public void flush(ChunkPos var1) {
      if (!this.dirty.isEmpty()) {
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
