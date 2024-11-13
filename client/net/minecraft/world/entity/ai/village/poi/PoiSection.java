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
import java.util.Objects;
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
   private final Short2ObjectMap<PoiRecord> records;
   private final Map<Holder<PoiType>, Set<PoiRecord>> byType;
   private final Runnable setDirty;
   private boolean isValid;

   public PoiSection(Runnable var1) {
      this(var1, true, ImmutableList.of());
   }

   PoiSection(Runnable var1, boolean var2, List<PoiRecord> var3) {
      super();
      this.records = new Short2ObjectOpenHashMap();
      this.byType = Maps.newHashMap();
      this.setDirty = var1;
      this.isValid = var2;
      var3.forEach(this::add);
   }

   public Packed pack() {
      return new Packed(this.isValid, this.records.values().stream().map(PoiRecord::pack).toList());
   }

   public Stream<PoiRecord> getRecords(Predicate<Holder<PoiType>> var1, PoiManager.Occupancy var2) {
      return this.byType.entrySet().stream().filter((var1x) -> var1.test((Holder)var1x.getKey())).flatMap((var0) -> ((Set)var0.getValue()).stream()).filter(var2.getTest());
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

         Util.logAndPauseIfInIde("POI data mismatch: already registered at " + String.valueOf(var2));
      }

      this.records.put(var4, var1);
      ((Set)this.byType.computeIfAbsent(var3, (var0) -> Sets.newHashSet())).add(var1);
      return true;
   }

   public void remove(BlockPos var1) {
      PoiRecord var2 = (PoiRecord)this.records.remove(SectionPos.sectionRelativePos(var1));
      if (var2 == null) {
         LOGGER.error("POI data mismatch: never registered at {}", var1);
      } else {
         ((Set)this.byType.get(var2.getPoiType())).remove(var2);
         Logger var10000 = LOGGER;
         Objects.requireNonNull(var2);
         Object var10002 = LogUtils.defer(var2::getPoiType);
         Objects.requireNonNull(var2);
         var10000.debug("Removed POI of type {} @ {}", var10002, LogUtils.defer(var2::getPos));
         this.setDirty.run();
      }
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public int getFreeTickets(BlockPos var1) {
      return (Integer)this.getPoiRecord(var1).map(PoiRecord::getFreeTickets).orElse(0);
   }

   public boolean release(BlockPos var1) {
      PoiRecord var2 = (PoiRecord)this.records.get(SectionPos.sectionRelativePos(var1));
      if (var2 == null) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("POI never registered at " + String.valueOf(var1)));
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
         var1.accept((BiConsumer)(var2x, var3) -> {
            short var4 = SectionPos.sectionRelativePos(var2x);
            PoiRecord var5 = (PoiRecord)var2.computeIfAbsent(var4, (var3x) -> new PoiRecord(var2x, var3, this.setDirty));
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

   public static record Packed(boolean isValid, List<PoiRecord.Packed> records) {
      public static final Codec<Packed> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.BOOL.lenientOptionalFieldOf("Valid", false).forGetter(Packed::isValid), PoiRecord.Packed.CODEC.listOf().fieldOf("Records").forGetter(Packed::records)).apply(var0, Packed::new));

      public Packed(boolean var1, List<PoiRecord.Packed> var2) {
         super();
         this.isValid = var1;
         this.records = var2;
      }

      public PoiSection unpack(Runnable var1) {
         return new PoiSection(var1, this.isValid, this.records.stream().map((var1x) -> var1x.unpack(var1)).toList());
      }
   }
}
