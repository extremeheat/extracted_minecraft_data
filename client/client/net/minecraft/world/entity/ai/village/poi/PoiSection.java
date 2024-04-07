package net.minecraft.world.entity.ai.village.poi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
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
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import org.slf4j.Logger;

public class PoiSection {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Short2ObjectMap<PoiRecord> records = new Short2ObjectOpenHashMap();
   private final Map<Holder<PoiType>, Set<PoiRecord>> byType = Maps.newHashMap();
   private final Runnable setDirty;
   private boolean isValid;

   public static Codec<PoiSection> codec(Runnable var0) {
      return RecordCodecBuilder.create(
            var1 -> var1.group(
                     RecordCodecBuilder.point(var0),
                     Codec.BOOL.lenientOptionalFieldOf("Valid", false).forGetter(var0xx -> var0xx.isValid),
                     PoiRecord.codec(var0).listOf().fieldOf("Records").forGetter(var0xx -> ImmutableList.copyOf(var0xx.records.values()))
                  )
                  .apply(var1, PoiSection::new)
         )
         .orElseGet(Util.prefix("Failed to read POI section: ", LOGGER::error), () -> new PoiSection(var0, false, ImmutableList.of()));
   }

   public PoiSection(Runnable var1) {
      this(var1, true, ImmutableList.of());
   }

   private PoiSection(Runnable var1, boolean var2, List<PoiRecord> var3) {
      super();
      this.setDirty = var1;
      this.isValid = var2;
      var3.forEach(this::add);
   }

   public Stream<PoiRecord> getRecords(Predicate<Holder<PoiType>> var1, PoiManager.Occupancy var2) {
      return this.byType.entrySet().stream().filter(var1x -> var1.test(var1x.getKey())).flatMap(var0 -> var0.getValue().stream()).filter(var2.getTest());
   }

   public void add(BlockPos var1, Holder<PoiType> var2) {
      if (this.add(new PoiRecord(var1, var2, this.setDirty))) {
         LOGGER.debug("Added POI of type {} @ {}", var2.getRegisteredName(), var1);
         this.setDirty.run();
      }
   }

   private boolean add(PoiRecord var1) {
      BlockPos var2 = var1.getPos();
      Holder var3 = var1.getPoiType();
      short var4 = SectionPos.sectionRelativePos(var2);
      PoiRecord var5 = (PoiRecord)this.records.get(var4);
      if (var5 != null) {
         if (var3.equals(var5.getPoiType())) {
            return false;
         }

         Util.logAndPauseIfInIde("POI data mismatch: already registered at " + var2);
      }

      this.records.put(var4, var1);
      this.byType.computeIfAbsent(var3, var0 -> Sets.newHashSet()).add(var1);
      return true;
   }

   public void remove(BlockPos var1) {
      PoiRecord var2 = (PoiRecord)this.records.remove(SectionPos.sectionRelativePos(var1));
      if (var2 == null) {
         LOGGER.error("POI data mismatch: never registered at {}", var1);
      } else {
         this.byType.get(var2.getPoiType()).remove(var2);
         LOGGER.debug("Removed POI of type {} @ {}", LogUtils.defer(var2::getPoiType), LogUtils.defer(var2::getPos));
         this.setDirty.run();
      }
   }

   @Deprecated
   @VisibleForDebug
   public int getFreeTickets(BlockPos var1) {
      return this.getPoiRecord(var1).map(PoiRecord::getFreeTickets).orElse(0);
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

   public boolean exists(BlockPos var1, Predicate<Holder<PoiType>> var2) {
      return this.getType(var1).filter(var2).isPresent();
   }

   public Optional<Holder<PoiType>> getType(BlockPos var1) {
      return this.getPoiRecord(var1).map(PoiRecord::getPoiType);
   }

   private Optional<PoiRecord> getPoiRecord(BlockPos var1) {
      return Optional.ofNullable((PoiRecord)this.records.get(SectionPos.sectionRelativePos(var1)));
   }

   public void refresh(Consumer<BiConsumer<BlockPos, Holder<PoiType>>> var1) {
      if (!this.isValid) {
         Short2ObjectOpenHashMap var2 = new Short2ObjectOpenHashMap(this.records);
         this.clear();
         var1.accept((var2x, var3) -> {
            short var4 = SectionPos.sectionRelativePos(var2x);
            PoiRecord var5 = (PoiRecord)var2.computeIfAbsent(var4, var3x -> new PoiRecord(var2x, var3, this.setDirty));
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
