package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.core.UUIDUtil;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import org.slf4j.Logger;

public class GossipContainer {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int DISCARD_THRESHOLD = 2;
   private final Map<UUID, GossipContainer.EntityGossips> gossips = Maps.newHashMap();

   public GossipContainer() {
      super();
   }

   @VisibleForDebug
   public Map<UUID, Object2IntMap<GossipType>> getGossipEntries() {
      HashMap var1 = Maps.newHashMap();
      this.gossips.keySet().forEach(var2 -> {
         GossipContainer.EntityGossips var3 = this.gossips.get(var2);
         var1.put(var2, var3.entries);
      });
      return var1;
   }

   public void decay() {
      Iterator var1 = this.gossips.values().iterator();

      while (var1.hasNext()) {
         GossipContainer.EntityGossips var2 = (GossipContainer.EntityGossips)var1.next();
         var2.decay();
         if (var2.isEmpty()) {
            var1.remove();
         }
      }
   }

   private Stream<GossipContainer.GossipEntry> unpack() {
      return this.gossips.entrySet().stream().flatMap(var0 -> var0.getValue().unpack(var0.getKey()));
   }

   private Collection<GossipContainer.GossipEntry> selectGossipsForTransfer(RandomSource var1, int var2) {
      List var3 = this.unpack().toList();
      if (var3.isEmpty()) {
         return Collections.emptyList();
      } else {
         int[] var4 = new int[var3.size()];
         int var5 = 0;

         for (int var6 = 0; var6 < var3.size(); var6++) {
            GossipContainer.GossipEntry var7 = (GossipContainer.GossipEntry)var3.get(var6);
            var5 += Math.abs(var7.weightedValue());
            var4[var6] = var5 - 1;
         }

         Set var10 = Sets.newIdentityHashSet();

         for (int var11 = 0; var11 < var2; var11++) {
            int var8 = var1.nextInt(var5);
            int var9 = Arrays.binarySearch(var4, var8);
            var10.add((GossipContainer.GossipEntry)var3.get(var9 < 0 ? -var9 - 1 : var9));
         }

         return var10;
      }
   }

   private GossipContainer.EntityGossips getOrCreate(UUID var1) {
      return this.gossips.computeIfAbsent(var1, var0 -> new GossipContainer.EntityGossips());
   }

   public void transferFrom(GossipContainer var1, RandomSource var2, int var3) {
      Collection var4 = var1.selectGossipsForTransfer(var2, var3);
      var4.forEach(var1x -> {
         int var2x = var1x.value - var1x.type.decayPerTransfer;
         if (var2x >= 2) {
            this.getOrCreate(var1x.target).entries.mergeInt(var1x.type, var2x, GossipContainer::mergeValuesForTransfer);
         }
      });
   }

   public int getReputation(UUID var1, Predicate<GossipType> var2) {
      GossipContainer.EntityGossips var3 = this.gossips.get(var1);
      return var3 != null ? var3.weightedValue(var2) : 0;
   }

   public long getCountForType(GossipType var1, DoublePredicate var2) {
      return this.gossips.values().stream().filter(var2x -> var2.test((double)(var2x.entries.getOrDefault(var1, 0) * var1.weight))).count();
   }

   public void add(UUID var1, GossipType var2, int var3) {
      GossipContainer.EntityGossips var4 = this.getOrCreate(var1);
      var4.entries.mergeInt(var2, var3, (var2x, var3x) -> this.mergeValuesForAddition(var2, var2x, var3x));
      var4.makeSureValueIsntTooLowOrTooHigh(var2);
      if (var4.isEmpty()) {
         this.gossips.remove(var1);
      }
   }

   public void remove(UUID var1, GossipType var2, int var3) {
      this.add(var1, var2, -var3);
   }

   public void remove(UUID var1, GossipType var2) {
      GossipContainer.EntityGossips var3 = this.gossips.get(var1);
      if (var3 != null) {
         var3.remove(var2);
         if (var3.isEmpty()) {
            this.gossips.remove(var1);
         }
      }
   }

   public void remove(GossipType var1) {
      Iterator var2 = this.gossips.values().iterator();

      while (var2.hasNext()) {
         GossipContainer.EntityGossips var3 = (GossipContainer.EntityGossips)var2.next();
         var3.remove(var1);
         if (var3.isEmpty()) {
            var2.remove();
         }
      }
   }

   public <T> T store(DynamicOps<T> var1) {
      return (T)GossipContainer.GossipEntry.LIST_CODEC
         .encodeStart(var1, this.unpack().toList())
         .resultOrPartial(var0 -> LOGGER.warn("Failed to serialize gossips: {}", var0))
         .orElseGet(var1::emptyList);
   }

   public void update(Dynamic<?> var1) {
      GossipContainer.GossipEntry.LIST_CODEC
         .decode(var1)
         .resultOrPartial(var0 -> LOGGER.warn("Failed to deserialize gossips: {}", var0))
         .stream()
         .flatMap(var0 -> ((List)var0.getFirst()).stream())
         .forEach(var1x -> this.getOrCreate(var1x.target).entries.put(var1x.type, var1x.value));
   }

   private static int mergeValuesForTransfer(int var0, int var1) {
      return Math.max(var0, var1);
   }

   private int mergeValuesForAddition(GossipType var1, int var2, int var3) {
      int var4 = var2 + var3;
      return var4 > var1.max ? Math.max(var1.max, var2) : var4;
   }

   static class EntityGossips {
      final Object2IntMap<GossipType> entries = new Object2IntOpenHashMap();

      EntityGossips() {
         super();
      }

      public int weightedValue(Predicate<GossipType> var1) {
         return this.entries
            .object2IntEntrySet()
            .stream()
            .filter(var1x -> var1.test((GossipType)var1x.getKey()))
            .mapToInt(var0 -> var0.getIntValue() * ((GossipType)var0.getKey()).weight)
            .sum();
      }

      public Stream<GossipContainer.GossipEntry> unpack(UUID var1) {
         return this.entries.object2IntEntrySet().stream().map(var1x -> new GossipContainer.GossipEntry(var1, (GossipType)var1x.getKey(), var1x.getIntValue()));
      }

      public void decay() {
         ObjectIterator var1 = this.entries.object2IntEntrySet().iterator();

         while (var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            int var3 = var2.getIntValue() - ((GossipType)var2.getKey()).decayPerDay;
            if (var3 < 2) {
               var1.remove();
            } else {
               var2.setValue(var3);
            }
         }
      }

      public boolean isEmpty() {
         return this.entries.isEmpty();
      }

      public void makeSureValueIsntTooLowOrTooHigh(GossipType var1) {
         int var2 = this.entries.getInt(var1);
         if (var2 > var1.max) {
            this.entries.put(var1, var1.max);
         }

         if (var2 < 2) {
            this.remove(var1);
         }
      }

      public void remove(GossipType var1) {
         this.entries.removeInt(var1);
      }
   }

   static record GossipEntry(UUID target, GossipType type, int value) {
      public static final Codec<GossipContainer.GossipEntry> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  UUIDUtil.CODEC.fieldOf("Target").forGetter(GossipContainer.GossipEntry::target),
                  GossipType.CODEC.fieldOf("Type").forGetter(GossipContainer.GossipEntry::type),
                  ExtraCodecs.POSITIVE_INT.fieldOf("Value").forGetter(GossipContainer.GossipEntry::value)
               )
               .apply(var0, GossipContainer.GossipEntry::new)
      );
      public static final Codec<List<GossipContainer.GossipEntry>> LIST_CODEC = CODEC.listOf();

      GossipEntry(UUID target, GossipType type, int value) {
         super();
         this.target = target;
         this.type = type;
         this.value = value;
      }

      public int weightedValue() {
         return this.value * this.type.weight;
      }
   }
}
