package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PoiSection implements Serializable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Short2ObjectMap<PoiRecord> records = new Short2ObjectOpenHashMap();
   private final Map<PoiType, Set<PoiRecord>> byType = Maps.newHashMap();
   private final Runnable setDirty;
   private boolean isValid;

   public PoiSection(Runnable var1) {
      super();
      this.setDirty = var1;
      this.isValid = true;
   }

   public <T> PoiSection(Runnable var1, Dynamic<T> var2) {
      super();
      this.setDirty = var1;

      try {
         this.isValid = var2.get("Valid").asBoolean(false);
         var2.get("Records").asStream().forEach((var2x) -> {
            this.add(new PoiRecord(var2x, var1));
         });
      } catch (Exception var4) {
         LOGGER.error("Failed to load POI chunk", var4);
         this.clear();
         this.isValid = false;
      }

   }

   public Stream<PoiRecord> getRecords(Predicate<PoiType> var1, PoiManager.Occupancy var2) {
      return this.byType.entrySet().stream().filter((var1x) -> {
         return var1.test(var1x.getKey());
      }).flatMap((var0) -> {
         return ((Set)var0.getValue()).stream();
      }).filter(var2.getTest());
   }

   public void add(BlockPos var1, PoiType var2) {
      if (this.add(new PoiRecord(var1, var2, this.setDirty))) {
         LOGGER.debug("Added POI of type {} @ {}", new Supplier[]{() -> {
            return var2;
         }, () -> {
            return var1;
         }});
         this.setDirty.run();
      }

   }

   private boolean add(PoiRecord var1) {
      BlockPos var2 = var1.getPos();
      PoiType var3 = var1.getPoiType();
      short var4 = SectionPos.sectionRelativePos(var2);
      PoiRecord var5 = (PoiRecord)this.records.get(var4);
      if (var5 != null) {
         if (var3.equals(var5.getPoiType())) {
            return false;
         } else {
            throw new IllegalStateException("POI data mismatch: already registered at " + var2);
         }
      } else {
         this.records.put(var4, var1);
         ((Set)this.byType.computeIfAbsent(var3, (var0) -> {
            return Sets.newHashSet();
         })).add(var1);
         return true;
      }
   }

   public void remove(BlockPos var1) {
      PoiRecord var2 = (PoiRecord)this.records.remove(SectionPos.sectionRelativePos(var1));
      if (var2 == null) {
         LOGGER.error("POI data mismatch: never registered at " + var1);
      } else {
         ((Set)this.byType.get(var2.getPoiType())).remove(var2);
         LOGGER.debug("Removed POI of type {} @ {}", new Supplier[]{var2::getPoiType, var2::getPos});
         this.setDirty.run();
      }
   }

   public boolean release(BlockPos var1) {
      PoiRecord var2 = (PoiRecord)this.records.get(SectionPos.sectionRelativePos(var1));
      if (var2 == null) {
         throw new IllegalStateException("POI never registered at " + var1);
      } else {
         boolean var3 = var2.releaseTicket();
         this.setDirty.run();
         return var3;
      }
   }

   public boolean exists(BlockPos var1, Predicate<PoiType> var2) {
      short var3 = SectionPos.sectionRelativePos(var1);
      PoiRecord var4 = (PoiRecord)this.records.get(var3);
      return var4 != null && var2.test(var4.getPoiType());
   }

   public Optional<PoiType> getType(BlockPos var1) {
      short var2 = SectionPos.sectionRelativePos(var1);
      PoiRecord var3 = (PoiRecord)this.records.get(var2);
      return var3 != null ? Optional.of(var3.getPoiType()) : Optional.empty();
   }

   public <T> T serialize(DynamicOps<T> var1) {
      Object var2 = var1.createList(this.records.values().stream().map((var1x) -> {
         return var1x.serialize(var1);
      }));
      return var1.createMap(ImmutableMap.of(var1.createString("Records"), var2, var1.createString("Valid"), var1.createBoolean(this.isValid)));
   }

   public void refresh(Consumer<BiConsumer<BlockPos, PoiType>> var1) {
      if (!this.isValid) {
         Short2ObjectOpenHashMap var2 = new Short2ObjectOpenHashMap(this.records);
         this.clear();
         var1.accept((var2x, var3) -> {
            short var4 = SectionPos.sectionRelativePos(var2x);
            PoiRecord var5 = (PoiRecord)var2.computeIfAbsent(var4, (var3x) -> {
               return new PoiRecord(var2x, var3, this.setDirty);
            });
            this.add(var5);
         });
         this.isValid = true;
         this.setDirty.run();
      }

   }

   private void clear() {
      this.records.clear();
      this.byType.clear();
   }
}
