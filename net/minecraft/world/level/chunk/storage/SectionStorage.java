package net.minecraft.world.level.chunk.storage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OptionalDynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongLinkedOpenHashSet;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Serializable;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SectionStorage implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IOWorker worker;
   private final Long2ObjectMap storage = new Long2ObjectOpenHashMap();
   private final LongLinkedOpenHashSet dirty = new LongLinkedOpenHashSet();
   private final BiFunction deserializer;
   private final Function factory;
   private final DataFixer fixerUpper;
   private final DataFixTypes type;

   public SectionStorage(File var1, BiFunction var2, Function var3, DataFixer var4, DataFixTypes var5) {
      this.deserializer = var2;
      this.factory = var3;
      this.fixerUpper = var4;
      this.type = var5;
      this.worker = new IOWorker(new RegionFileStorage(var1), var1.getName());
   }

   protected void tick(BooleanSupplier var1) {
      while(!this.dirty.isEmpty() && var1.getAsBoolean()) {
         ChunkPos var2 = SectionPos.of(this.dirty.firstLong()).chunk();
         this.writeColumn(var2);
      }

   }

   @Nullable
   protected Optional get(long var1) {
      return (Optional)this.storage.get(var1);
   }

   protected Optional getOrLoad(long var1) {
      SectionPos var3 = SectionPos.of(var1);
      if (this.outsideStoredRange(var3)) {
         return Optional.empty();
      } else {
         Optional var4 = this.get(var1);
         if (var4 != null) {
            return var4;
         } else {
            this.readColumn(var3.chunk());
            var4 = this.get(var1);
            if (var4 == null) {
               throw (IllegalStateException)Util.pauseInIde(new IllegalStateException());
            } else {
               return var4;
            }
         }
      }
   }

   protected boolean outsideStoredRange(SectionPos var1) {
      return Level.isOutsideBuildHeight(SectionPos.sectionToBlockCoord(var1.y()));
   }

   protected Serializable getOrCreate(long var1) {
      Optional var3 = this.getOrLoad(var1);
      if (var3.isPresent()) {
         return (Serializable)var3.get();
      } else {
         Serializable var4 = (Serializable)this.factory.apply(() -> {
            this.setDirty(var1);
         });
         this.storage.put(var1, Optional.of(var4));
         return var4;
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

   private void readColumn(ChunkPos var1, DynamicOps var2, @Nullable Object var3) {
      if (var3 == null) {
         for(int var4 = 0; var4 < 16; ++var4) {
            this.storage.put(SectionPos.of(var1, var4).asLong(), Optional.empty());
         }
      } else {
         Dynamic var14 = new Dynamic(var2, var3);
         int var5 = getVersion(var14);
         int var6 = SharedConstants.getCurrentVersion().getWorldVersion();
         boolean var7 = var5 != var6;
         Dynamic var8 = this.fixerUpper.update(this.type.getType(), var14, var5, var6);
         OptionalDynamic var9 = var8.get("Sections");

         for(int var10 = 0; var10 < 16; ++var10) {
            long var11 = SectionPos.of(var1, var10).asLong();
            Optional var13 = var9.get(Integer.toString(var10)).get().map((var3x) -> {
               return (Serializable)this.deserializer.apply(() -> {
                  this.setDirty(var11);
               }, var3x);
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

   private Dynamic writeColumn(ChunkPos var1, DynamicOps var2) {
      HashMap var3 = Maps.newHashMap();

      for(int var4 = 0; var4 < 16; ++var4) {
         long var5 = SectionPos.of(var1, var4).asLong();
         this.dirty.remove(var5);
         Optional var7 = (Optional)this.storage.get(var5);
         if (var7 != null && var7.isPresent()) {
            var3.put(var2.createString(Integer.toString(var4)), ((Serializable)var7.get()).serialize(var2));
         }
      }

      return new Dynamic(var2, var2.createMap(ImmutableMap.of(var2.createString("Sections"), var2.createMap(var3), var2.createString("DataVersion"), var2.createInt(SharedConstants.getCurrentVersion().getWorldVersion()))));
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

   private static int getVersion(Dynamic var0) {
      return ((Number)var0.get("DataVersion").asNumber().orElse(1945)).intValue();
   }

   public void flush(ChunkPos var1) {
      if (!this.dirty.isEmpty()) {
         for(int var2 = 0; var2 < 16; ++var2) {
            long var3 = SectionPos.of(var1, var2).asLong();
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
