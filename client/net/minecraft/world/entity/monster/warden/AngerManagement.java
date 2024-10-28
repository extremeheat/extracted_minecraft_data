package net.minecraft.world.entity.monster.warden;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class AngerManagement {
   @VisibleForTesting
   protected static final int CONVERSION_DELAY = 2;
   @VisibleForTesting
   protected static final int MAX_ANGER = 150;
   private static final int DEFAULT_ANGER_DECREASE = 1;
   private int conversionDelay = Mth.randomBetweenInclusive(RandomSource.create(), 0, 2);
   int highestAnger;
   private static final Codec<Pair<UUID, Integer>> SUSPECT_ANGER_PAIR = RecordCodecBuilder.create((var0) -> {
      return var0.group(UUIDUtil.CODEC.fieldOf("uuid").forGetter(Pair::getFirst), ExtraCodecs.NON_NEGATIVE_INT.fieldOf("anger").forGetter(Pair::getSecond)).apply(var0, Pair::of);
   });
   private final Predicate<Entity> filter;
   @VisibleForTesting
   protected final ArrayList<Entity> suspects;
   private final Sorter suspectSorter;
   @VisibleForTesting
   protected final Object2IntMap<Entity> angerBySuspect;
   @VisibleForTesting
   protected final Object2IntMap<UUID> angerByUuid;

   public static Codec<AngerManagement> codec(Predicate<Entity> var0) {
      return RecordCodecBuilder.create((var1) -> {
         return var1.group(SUSPECT_ANGER_PAIR.listOf().fieldOf("suspects").orElse(Collections.emptyList()).forGetter(AngerManagement::createUuidAngerPairs)).apply(var1, (var1x) -> {
            return new AngerManagement(var0, var1x);
         });
      });
   }

   public AngerManagement(Predicate<Entity> var1, List<Pair<UUID, Integer>> var2) {
      super();
      this.filter = var1;
      this.suspects = new ArrayList();
      this.suspectSorter = new Sorter(this);
      this.angerBySuspect = new Object2IntOpenHashMap();
      this.angerByUuid = new Object2IntOpenHashMap(var2.size());
      var2.forEach((var1x) -> {
         this.angerByUuid.put((UUID)var1x.getFirst(), (Integer)var1x.getSecond());
      });
   }

   private List<Pair<UUID, Integer>> createUuidAngerPairs() {
      return (List)Streams.concat(new Stream[]{this.suspects.stream().map((var1) -> {
         return Pair.of(var1.getUUID(), this.angerBySuspect.getInt(var1));
      }), this.angerByUuid.object2IntEntrySet().stream().map((var0) -> {
         return Pair.of((UUID)var0.getKey(), var0.getIntValue());
      })}).collect(Collectors.toList());
   }

   public void tick(ServerLevel var1, Predicate<Entity> var2) {
      --this.conversionDelay;
      if (this.conversionDelay <= 0) {
         this.convertFromUuids(var1);
         this.conversionDelay = 2;
      }

      ObjectIterator var3 = this.angerByUuid.object2IntEntrySet().iterator();

      while(var3.hasNext()) {
         Object2IntMap.Entry var4 = (Object2IntMap.Entry)var3.next();
         int var5 = var4.getIntValue();
         if (var5 <= 1) {
            var3.remove();
         } else {
            var4.setValue(var5 - 1);
         }
      }

      ObjectIterator var9 = this.angerBySuspect.object2IntEntrySet().iterator();

      while(true) {
         while(var9.hasNext()) {
            Object2IntMap.Entry var10 = (Object2IntMap.Entry)var9.next();
            int var6 = var10.getIntValue();
            Entity var7 = (Entity)var10.getKey();
            Entity.RemovalReason var8 = var7.getRemovalReason();
            if (var6 > 1 && var2.test(var7) && var8 == null) {
               var10.setValue(var6 - 1);
            } else {
               this.suspects.remove(var7);
               var9.remove();
               if (var6 > 1 && var8 != null) {
                  switch (var8) {
                     case CHANGED_DIMENSION:
                     case UNLOADED_TO_CHUNK:
                     case UNLOADED_WITH_PLAYER:
                        this.angerByUuid.put(var7.getUUID(), var6 - 1);
                  }
               }
            }
         }

         this.sortAndUpdateHighestAnger();
         return;
      }
   }

   private void sortAndUpdateHighestAnger() {
      this.highestAnger = 0;
      this.suspects.sort(this.suspectSorter);
      if (this.suspects.size() == 1) {
         this.highestAnger = this.angerBySuspect.getInt(this.suspects.get(0));
      }

   }

   private void convertFromUuids(ServerLevel var1) {
      ObjectIterator var2 = this.angerByUuid.object2IntEntrySet().iterator();

      while(var2.hasNext()) {
         Object2IntMap.Entry var3 = (Object2IntMap.Entry)var2.next();
         int var4 = var3.getIntValue();
         Entity var5 = var1.getEntity((UUID)var3.getKey());
         if (var5 != null) {
            this.angerBySuspect.put(var5, var4);
            this.suspects.add(var5);
            var2.remove();
         }
      }

   }

   public int increaseAnger(Entity var1, int var2) {
      boolean var3 = !this.angerBySuspect.containsKey(var1);
      int var4 = this.angerBySuspect.computeInt(var1, (var1x, var2x) -> {
         return Math.min(150, (var2x == null ? 0 : var2x) + var2);
      });
      if (var3) {
         int var5 = this.angerByUuid.removeInt(var1.getUUID());
         var4 += var5;
         this.angerBySuspect.put(var1, var4);
         this.suspects.add(var1);
      }

      this.sortAndUpdateHighestAnger();
      return var4;
   }

   public void clearAnger(Entity var1) {
      this.angerBySuspect.removeInt(var1);
      this.suspects.remove(var1);
      this.sortAndUpdateHighestAnger();
   }

   @Nullable
   private Entity getTopSuspect() {
      return (Entity)this.suspects.stream().filter(this.filter).findFirst().orElse((Object)null);
   }

   public int getActiveAnger(@Nullable Entity var1) {
      return var1 == null ? this.highestAnger : this.angerBySuspect.getInt(var1);
   }

   public Optional<LivingEntity> getActiveEntity() {
      return Optional.ofNullable(this.getTopSuspect()).filter((var0) -> {
         return var0 instanceof LivingEntity;
      }).map((var0) -> {
         return (LivingEntity)var0;
      });
   }

   @VisibleForTesting
   protected static record Sorter(AngerManagement angerManagement) implements Comparator<Entity> {
      protected Sorter(AngerManagement var1) {
         super();
         this.angerManagement = var1;
      }

      public int compare(Entity var1, Entity var2) {
         if (var1.equals(var2)) {
            return 0;
         } else {
            int var3 = this.angerManagement.angerBySuspect.getOrDefault(var1, 0);
            int var4 = this.angerManagement.angerBySuspect.getOrDefault(var2, 0);
            this.angerManagement.highestAnger = Math.max(this.angerManagement.highestAnger, Math.max(var3, var4));
            boolean var5 = AngerLevel.byAnger(var3).isAngry();
            boolean var6 = AngerLevel.byAnger(var4).isAngry();
            if (var5 != var6) {
               return var5 ? -1 : 1;
            } else {
               boolean var7 = var1 instanceof Player;
               boolean var8 = var2 instanceof Player;
               if (var7 != var8) {
                  return var7 ? -1 : 1;
               } else {
                  return Integer.compare(var4, var3);
               }
            }
         }
      }

      public AngerManagement angerManagement() {
         return this.angerManagement;
      }

      // $FF: synthetic method
      public int compare(final Object var1, final Object var2) {
         return this.compare((Entity)var1, (Entity)var2);
      }
   }
}
