package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PoiSection {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Short2ObjectMap<PoiRecord> records;
   private final Map<PoiType, Set<PoiRecord>> byType;
   private final Runnable setDirty;
   private boolean isValid;

   public static Codec<PoiSection> codec(Runnable var0) {
      Codec var10000 = RecordCodecBuilder.create((var1) -> {
         return var1.group(RecordCodecBuilder.point(var0), Codec.BOOL.optionalFieldOf("Valid", false).forGetter((var0x) -> {
            return var0x.isValid;
         }), PoiRecord.codec(var0).listOf().fieldOf("Records").forGetter((var0x) -> {
            return ImmutableList.copyOf(var0x.records.values());
         })).apply(var1, PoiSection::new);
      });
      Logger var10002 = LOGGER;
      var10002.getClass();
      return var10000.orElseGet(Util.prefix("Failed to read POI section: ", var10002::error), () -> {
         return new PoiSection(var0, false, ImmutableList.of());
      });
   }

   public PoiSection(Runnable var1) {
      this(var1, true, ImmutableList.of());
   }

   private PoiSection(Runnable var1, boolean var2, List<PoiRecord> var3) {
      super();
      this.records = new Short2ObjectOpenHashMap();
      this.byType = Maps.newHashMap();
      this.setDirty = var1;
      this.isValid = var2;
      var3.forEach(this::add);
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
            throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("POI data mismatch: already registered at " + var2));
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
         LOGGER.error("POI data mismatch: never registered at {}", var1);
      } else {
         ((Set)this.byType.get(var2.getPoiType())).remove(var2);
         LOGGER.debug("Removed POI of type {} @ {}", new Supplier[]{var2::getPoiType, var2::getPos});
         this.setDirty.run();
      }
   }

   public boolean release(BlockPos var1) {
      PoiRecord var2 = (PoiRecord)this.records.get(SectionPos.sectionRelativePos(var1));
      if (var2 == null) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("POI never registered at " + var1));
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

   boolean isValid() {
      return this.isValid;
   }
}
