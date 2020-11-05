package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.SerializableUUID;

public class GossipContainer {
   private final Map<UUID, GossipContainer.EntityGossips> gossips = Maps.newHashMap();

   public GossipContainer() {
      super();
   }

   public void decay() {
      Iterator var1 = this.gossips.values().iterator();

      while(var1.hasNext()) {
         GossipContainer.EntityGossips var2 = (GossipContainer.EntityGossips)var1.next();
         var2.decay();
         if (var2.isEmpty()) {
            var1.remove();
         }
      }

   }

   private Stream<GossipContainer.GossipEntry> unpack() {
      return this.gossips.entrySet().stream().flatMap((var0) -> {
         return ((GossipContainer.EntityGossips)var0.getValue()).unpack((UUID)var0.getKey());
      });
   }

   private Collection<GossipContainer.GossipEntry> selectGossipsForTransfer(Random var1, int var2) {
      List var3 = (List)this.unpack().collect(Collectors.toList());
      if (var3.isEmpty()) {
         return Collections.emptyList();
      } else {
         int[] var4 = new int[var3.size()];
         int var5 = 0;

         for(int var6 = 0; var6 < var3.size(); ++var6) {
            GossipContainer.GossipEntry var7 = (GossipContainer.GossipEntry)var3.get(var6);
            var5 += Math.abs(var7.weightedValue());
            var4[var6] = var5 - 1;
         }

         Set var10 = Sets.newIdentityHashSet();

         for(int var11 = 0; var11 < var2; ++var11) {
            int var8 = var1.nextInt(var5);
            int var9 = Arrays.binarySearch(var4, var8);
            var10.add(var3.get(var9 < 0 ? -var9 - 1 : var9));
         }

         return var10;
      }
   }

   private GossipContainer.EntityGossips getOrCreate(UUID var1) {
      return (GossipContainer.EntityGossips)this.gossips.computeIfAbsent(var1, (var0) -> {
         return new GossipContainer.EntityGossips();
      });
   }

   public void transferFrom(GossipContainer var1, Random var2, int var3) {
      Collection var4 = var1.selectGossipsForTransfer(var2, var3);
      var4.forEach((var1x) -> {
         int var2 = var1x.value - var1x.type.decayPerTransfer;
         if (var2 >= 2) {
            this.getOrCreate(var1x.target).entries.mergeInt(var1x.type, var2, GossipContainer::mergeValuesForTransfer);
         }

      });
   }

   public int getReputation(UUID var1, Predicate<GossipType> var2) {
      GossipContainer.EntityGossips var3 = (GossipContainer.EntityGossips)this.gossips.get(var1);
      return var3 != null ? var3.weightedValue(var2) : 0;
   }

   public void add(UUID var1, GossipType var2, int var3) {
      GossipContainer.EntityGossips var4 = this.getOrCreate(var1);
      var4.entries.mergeInt(var2, var3, (var2x, var3x) -> {
         return this.mergeValuesForAddition(var2, var2x, var3x);
      });
      var4.makeSureValueIsntTooLowOrTooHigh(var2);
      if (var4.isEmpty()) {
         this.gossips.remove(var1);
      }

   }

   public <T> Dynamic<T> store(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createList(this.unpack().map((var1x) -> {
         return var1x.store(var1);
      }).map(Dynamic::getValue)));
   }

   public void update(Dynamic<?> var1) {
      var1.asStream().map(GossipContainer.GossipEntry::load).flatMap((var0) -> {
         return Util.toStream(var0.result());
      }).forEach((var1x) -> {
         this.getOrCreate(var1x.target).entries.put(var1x.type, var1x.value);
      });
   }

   private static int mergeValuesForTransfer(int var0, int var1) {
      return Math.max(var0, var1);
   }

   private int mergeValuesForAddition(GossipType var1, int var2, int var3) {
      int var4 = var2 + var3;
      return var4 > var1.max ? Math.max(var1.max, var2) : var4;
   }

   static class EntityGossips {
      private final Object2IntMap<GossipType> entries;

      private EntityGossips() {
         super();
         this.entries = new Object2IntOpenHashMap();
      }

      public int weightedValue(Predicate<GossipType> var1) {
         return this.entries.object2IntEntrySet().stream().filter((var1x) -> {
            return var1.test(var1x.getKey());
         }).mapToInt((var0) -> {
            return var0.getIntValue() * ((GossipType)var0.getKey()).weight;
         }).sum();
      }

      public Stream<GossipContainer.GossipEntry> unpack(UUID var1) {
         return this.entries.object2IntEntrySet().stream().map((var1x) -> {
            return new GossipContainer.GossipEntry(var1, (GossipType)var1x.getKey(), var1x.getIntValue());
         });
      }

      public void decay() {
         ObjectIterator var1 = this.entries.object2IntEntrySet().iterator();

         while(var1.hasNext()) {
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

      // $FF: synthetic method
      EntityGossips(Object var1) {
         this();
      }
   }

   static class GossipEntry {
      public final UUID target;
      public final GossipType type;
      public final int value;

      public GossipEntry(UUID var1, GossipType var2, int var3) {
         super();
         this.target = var1;
         this.type = var2;
         this.value = var3;
      }

      public int weightedValue() {
         return this.value * this.type.weight;
      }

      public String toString() {
         return "GossipEntry{target=" + this.target + ", type=" + this.type + ", value=" + this.value + '}';
      }

      public <T> Dynamic<T> store(DynamicOps<T> var1) {
         return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("Target"), SerializableUUID.CODEC.encodeStart(var1, this.target).result().orElseThrow(RuntimeException::new), var1.createString("Type"), var1.createString(this.type.id), var1.createString("Value"), var1.createInt(this.value))));
      }

      public static DataResult<GossipContainer.GossipEntry> load(Dynamic<?> var0) {
         return DataResult.unbox(DataResult.instance().group(var0.get("Target").read(SerializableUUID.CODEC), var0.get("Type").asString().map(GossipType::byId), var0.get("Value").asNumber().map(Number::intValue)).apply(DataResult.instance(), GossipContainer.GossipEntry::new));
      }
   }
}
