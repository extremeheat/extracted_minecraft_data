package net.minecraft.world.entity.ai.gossip;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
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

public class GossipContainer {
   public static final Codec<GossipContainer> CODEC;
   public static final int DISCARD_THRESHOLD = 2;
   private final Map<UUID, EntityGossips> gossips = new HashMap();

   public GossipContainer() {
      super();
   }

   private GossipContainer(final List<GossipEntry> entries) {
      super();
      entries.forEach((e) -> this.getOrCreate(e.target).entries.put(e.type, e.value));
   }

   @VisibleForDebug
   public Map<UUID, Object2IntMap<GossipType>> getGossipEntries() {
      Map<UUID, Object2IntMap<GossipType>> result = Maps.newHashMap();
      this.gossips.keySet().forEach((uuid) -> {
         EntityGossips entityGossips = (EntityGossips)this.gossips.get(uuid);
         result.put(uuid, entityGossips.entries);
      });
      return result;
   }

   public void decay() {
      Iterator<EntityGossips> iterator = this.gossips.values().iterator();

      while(iterator.hasNext()) {
         EntityGossips entityGossips = (EntityGossips)iterator.next();
         entityGossips.decay();
         if (entityGossips.isEmpty()) {
            iterator.remove();
         }
      }

   }

   private Stream<GossipEntry> unpack() {
      return this.gossips.entrySet().stream().flatMap((e) -> ((EntityGossips)e.getValue()).unpack((UUID)e.getKey()));
   }

   private Collection<GossipEntry> selectGossipsForTransfer(final RandomSource random, final int maxCount) {
      List<GossipEntry> entries = this.unpack().toList();
      if (entries.isEmpty()) {
         return Collections.emptyList();
      } else {
         int[] ranges = new int[entries.size()];
         int rangesEnd = 0;

         for(int i = 0; i < entries.size(); ++i) {
            GossipEntry gossip = (GossipEntry)entries.get(i);
            rangesEnd += Math.abs(gossip.weightedValue());
            ranges[i] = rangesEnd - 1;
         }

         Set<GossipEntry> results = Sets.newIdentityHashSet();

         for(int i = 0; i < maxCount; ++i) {
            int choice = random.nextInt(rangesEnd);
            int selectedIndex = Arrays.binarySearch(ranges, choice);
            results.add((GossipEntry)entries.get(selectedIndex < 0 ? -selectedIndex - 1 : selectedIndex));
         }

         return results;
      }
   }

   private EntityGossips getOrCreate(final UUID target) {
      return (EntityGossips)this.gossips.computeIfAbsent(target, (uuid) -> new EntityGossips());
   }

   public void transferFrom(final GossipContainer source, final RandomSource random, final int maxCount) {
      Collection<GossipEntry> newGossips = source.selectGossipsForTransfer(random, maxCount);
      newGossips.forEach((newGossip) -> {
         int decayedValue = newGossip.value - newGossip.type.decayPerTransfer;
         if (decayedValue >= 2) {
            this.getOrCreate(newGossip.target).entries.mergeInt(newGossip.type, decayedValue, GossipContainer::mergeValuesForTransfer);
         }

      });
   }

   public int getReputation(final UUID entity, final Predicate<GossipType> types) {
      EntityGossips entry = (EntityGossips)this.gossips.get(entity);
      return entry != null ? entry.weightedValue(types) : 0;
   }

   public long getCountForType(final GossipType type, final DoublePredicate valueTest) {
      return this.gossips.values().stream().filter((e) -> valueTest.test((double)(e.entries.getOrDefault(type, 0) * type.weight))).count();
   }

   public void add(final UUID target, final GossipType type, final int amountToAdd) {
      EntityGossips entityGossips = this.getOrCreate(target);
      entityGossips.entries.mergeInt(type, amountToAdd, (o, n) -> this.mergeValuesForAddition(type, o, n));
      entityGossips.makeSureValueIsntTooLowOrTooHigh(type);
      if (entityGossips.isEmpty()) {
         this.gossips.remove(target);
      }

   }

   public void remove(final UUID target, final GossipType type, final int amountToRemove) {
      this.add(target, type, -amountToRemove);
   }

   public void remove(final UUID target, final GossipType type) {
      EntityGossips entityGossips = (EntityGossips)this.gossips.get(target);
      if (entityGossips != null) {
         entityGossips.remove(type);
         if (entityGossips.isEmpty()) {
            this.gossips.remove(target);
         }
      }

   }

   public void remove(final GossipType type) {
      Iterator<EntityGossips> iterator = this.gossips.values().iterator();

      while(iterator.hasNext()) {
         EntityGossips entityGossips = (EntityGossips)iterator.next();
         entityGossips.remove(type);
         if (entityGossips.isEmpty()) {
            iterator.remove();
         }
      }

   }

   public void clear() {
      this.gossips.clear();
   }

   public void putAll(final GossipContainer container) {
      container.gossips.forEach((target, gossips) -> this.getOrCreate(target).entries.putAll(gossips.entries));
   }

   private static int mergeValuesForTransfer(final int oldValue, final int newValue) {
      return Math.max(oldValue, newValue);
   }

   private int mergeValuesForAddition(final GossipType type, final int oldValue, final int newValue) {
      int sum = oldValue + newValue;
      return sum > type.max ? Math.max(type.max, oldValue) : sum;
   }

   public GossipContainer copy() {
      GossipContainer container = new GossipContainer();
      container.putAll(this);
      return container;
   }

   static {
      CODEC = GossipContainer.GossipEntry.CODEC.listOf().xmap(GossipContainer::new, (container) -> container.unpack().toList());
   }

   private static record GossipEntry(UUID target, GossipType type, int value) {
      public static final Codec<GossipEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(UUIDUtil.CODEC.fieldOf("Target").forGetter(GossipEntry::target), GossipType.CODEC.fieldOf("Type").forGetter(GossipEntry::type), ExtraCodecs.POSITIVE_INT.fieldOf("Value").forGetter(GossipEntry::value)).apply(i, GossipEntry::new));

      private GossipEntry {
         super();
      }

      public int weightedValue() {
         return this.value * this.type.weight;
      }
   }

   private static class EntityGossips {
      private final Object2IntMap<GossipType> entries = new Object2IntOpenHashMap();

      private EntityGossips() {
         super();
      }

      public int weightedValue(final Predicate<GossipType> types) {
         return this.entries.object2IntEntrySet().stream().filter((e) -> types.test((GossipType)e.getKey())).mapToInt((e) -> e.getIntValue() * ((GossipType)e.getKey()).weight).sum();
      }

      public Stream<GossipEntry> unpack(final UUID target) {
         return this.entries.object2IntEntrySet().stream().map((e) -> new GossipEntry(target, (GossipType)e.getKey(), e.getIntValue()));
      }

      public void decay() {
         ObjectIterator<Object2IntMap.Entry<GossipType>> it = this.entries.object2IntEntrySet().iterator();

         while(it.hasNext()) {
            Object2IntMap.Entry<GossipType> gossip = (Object2IntMap.Entry)it.next();
            int newValue = gossip.getIntValue() - ((GossipType)gossip.getKey()).decayPerDay;
            if (newValue < 2) {
               it.remove();
            } else {
               gossip.setValue(newValue);
            }
         }

      }

      public boolean isEmpty() {
         return this.entries.isEmpty();
      }

      public void makeSureValueIsntTooLowOrTooHigh(final GossipType type) {
         int value = this.entries.getInt(type);
         if (value > type.max) {
            this.entries.put(type, type.max);
         }

         if (value < 2) {
            this.remove(type);
         }

      }

      public void remove(final GossipType type) {
         this.entries.removeInt(type);
      }
   }
}
