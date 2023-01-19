package net.minecraft.world.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class Brain<E extends LivingEntity> {
   static final Logger LOGGER = LogUtils.getLogger();
   private final Supplier<Codec<Brain<E>>> codec;
   private static final int SCHEDULE_UPDATE_DELAY = 20;
   private final Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories = Maps.newHashMap();
   private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
   private final Map<Integer, Map<Activity, Set<Behavior<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
   private Schedule schedule = Schedule.EMPTY;
   private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements = Maps.newHashMap();
   private final Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped = Maps.newHashMap();
   private Set<Activity> coreActivities = Sets.newHashSet();
   private final Set<Activity> activeActivities = Sets.newHashSet();
   private Activity defaultActivity = Activity.IDLE;
   private long lastScheduleUpdate = -9999L;

   public static <E extends LivingEntity> Brain.Provider<E> provider(
      Collection<? extends MemoryModuleType<?>> var0, Collection<? extends SensorType<? extends Sensor<? super E>>> var1
   ) {
      return new Brain.Provider<>(var0, var1);
   }

   public static <E extends LivingEntity> Codec<Brain<E>> codec(
      final Collection<? extends MemoryModuleType<?>> var0, final Collection<? extends SensorType<? extends Sensor<? super E>>> var1
   ) {
      final MutableObject var2 = new MutableObject();
      var2.setValue(
         (new MapCodec<Brain<E>>() {
               public <T> Stream<T> keys(DynamicOps<T> var1x) {
                  return var0.stream()
                     .flatMap(var0xx -> var0xx.getCodec().map(var1xxxx -> Registry.MEMORY_MODULE_TYPE.getKey(var0xx)).stream())
                     .map(var1xxx -> (T)var1x.createString(var1xxx.toString()));
               }
      
               public <T> DataResult<Brain<E>> decode(DynamicOps<T> var1x, MapLike<T> var2x) {
                  MutableObject var3 = new MutableObject(DataResult.success(ImmutableList.builder()));
                  var2x.entries().forEach(var3x -> {
                     DataResult var4x = Registry.MEMORY_MODULE_TYPE.byNameCodec().parse(var1x, var3x.getFirst());
                     DataResult var5 = var4x.flatMap(var3xx -> this.captureRead(var3xx, var1x, (T)var3x.getSecond()));
                     var3.setValue(((DataResult)var3.getValue()).apply2(Builder::add, var5));
                  });
                  ImmutableList var4 = (ImmutableList)((DataResult)var3.getValue())
                     .resultOrPartial(Brain.LOGGER::error)
                     .map(Builder::build)
                     .orElseGet(ImmutableList::of);
                  return DataResult.success(new Brain(var0, var1, var4, var2::getValue));
               }
      
               private <T, U> DataResult<Brain.MemoryValue<U>> captureRead(MemoryModuleType<U> var1x, DynamicOps<T> var2x, T var3) {
                  return ((DataResult)var1x.getCodec().map(DataResult::success).orElseGet(() -> (T)DataResult.error("No codec for memory: " + var1x)))
                     .flatMap(var2xxx -> var2xxx.parse(var2x, var3))
                     .map(var1xxx -> new Brain.MemoryValue(var1x, Optional.of(var1xxx)));
               }
      
               public <T> RecordBuilder<T> encode(Brain<E> var1x, DynamicOps<T> var2x, RecordBuilder<T> var3) {
                  var1x.memories().forEach(var2xxx -> var2xxx.serialize(var2x, var3));
                  return var3;
               }
            })
            .fieldOf("memories")
            .codec()
      );
      return (Codec<Brain<E>>)var2.getValue();
   }

   public Brain(
      Collection<? extends MemoryModuleType<?>> var1,
      Collection<? extends SensorType<? extends Sensor<? super E>>> var2,
      ImmutableList<Brain.MemoryValue<?>> var3,
      Supplier<Codec<Brain<E>>> var4
   ) {
      super();
      this.codec = var4;

      for(MemoryModuleType var6 : var1) {
         this.memories.put(var6, Optional.empty());
      }

      for(SensorType var12 : var2) {
         this.sensors.put(var12, var12.create());
      }

      for(Sensor var13 : this.sensors.values()) {
         for(MemoryModuleType var8 : var13.requires()) {
            this.memories.put(var8, Optional.empty());
         }
      }

      UnmodifiableIterator var11 = var3.iterator();

      while(var11.hasNext()) {
         Brain.MemoryValue var14 = (Brain.MemoryValue)var11.next();
         var14.setMemoryInternal(this);
      }
   }

   public <T> DataResult<T> serializeStart(DynamicOps<T> var1) {
      return ((Codec)this.codec.get()).encodeStart(var1, this);
   }

   Stream<Brain.MemoryValue<?>> memories() {
      return this.memories
         .entrySet()
         .stream()
         .map(var0 -> Brain.MemoryValue.createUnchecked((MemoryModuleType<? extends ExpirableValue<?>>)var0.getKey(), var0.getValue()));
   }

   public boolean hasMemoryValue(MemoryModuleType<?> var1) {
      return this.checkMemory(var1, MemoryStatus.VALUE_PRESENT);
   }

   public <U> void eraseMemory(MemoryModuleType<U> var1) {
      this.setMemory(var1, Optional.empty());
   }

   public <U> void setMemory(MemoryModuleType<U> var1, @Nullable U var2) {
      this.setMemory(var1, Optional.ofNullable(var2));
   }

   public <U> void setMemoryWithExpiry(MemoryModuleType<U> var1, U var2, long var3) {
      this.setMemoryInternal(var1, Optional.of(ExpirableValue.of(var2, var3)));
   }

   public <U> void setMemory(MemoryModuleType<U> var1, Optional<? extends U> var2) {
      this.setMemoryInternal(var1, var2.map(ExpirableValue::of));
   }

   <U> void setMemoryInternal(MemoryModuleType<U> var1, Optional<? extends ExpirableValue<?>> var2) {
      if (this.memories.containsKey(var1)) {
         if (var2.isPresent() && this.isEmptyCollection(((ExpirableValue)var2.get()).getValue())) {
            this.eraseMemory(var1);
         } else {
            this.memories.put(var1, var2);
         }
      }
   }

   public <U> Optional<U> getMemory(MemoryModuleType<U> var1) {
      Optional var2 = this.memories.get(var1);
      if (var2 == null) {
         throw new IllegalStateException("Unregistered memory fetched: " + var1);
      } else {
         return var2.map(ExpirableValue::getValue);
      }
   }

   public <U> long getTimeUntilExpiry(MemoryModuleType<U> var1) {
      Optional var2 = this.memories.get(var1);
      return var2.map(ExpirableValue::getTimeToLive).orElse(0L);
   }

   @Deprecated
   @VisibleForDebug
   public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMemories() {
      return this.memories;
   }

   public <U> boolean isMemoryValue(MemoryModuleType<U> var1, U var2) {
      return !this.hasMemoryValue(var1) ? false : this.<Object>getMemory(var1).filter(var1x -> var1x.equals(var2)).isPresent();
   }

   public boolean checkMemory(MemoryModuleType<?> var1, MemoryStatus var2) {
      Optional var3 = this.memories.get(var1);
      if (var3 == null) {
         return false;
      } else {
         return var2 == MemoryStatus.REGISTERED
            || var2 == MemoryStatus.VALUE_PRESENT && var3.isPresent()
            || var2 == MemoryStatus.VALUE_ABSENT && !var3.isPresent();
      }
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule var1) {
      this.schedule = var1;
   }

   public void setCoreActivities(Set<Activity> var1) {
      this.coreActivities = var1;
   }

   @Deprecated
   @VisibleForDebug
   public Set<Activity> getActiveActivities() {
      return this.activeActivities;
   }

   @Deprecated
   @VisibleForDebug
   public List<Behavior<? super E>> getRunningBehaviors() {
      ObjectArrayList var1 = new ObjectArrayList();

      for(Map var3 : this.availableBehaviorsByPriority.values()) {
         for(Set var5 : var3.values()) {
            for(Behavior var7 : var5) {
               if (var7.getStatus() == Behavior.Status.RUNNING) {
                  var1.add(var7);
               }
            }
         }
      }

      return var1;
   }

   public void useDefaultActivity() {
      this.setActiveActivity(this.defaultActivity);
   }

   public Optional<Activity> getActiveNonCoreActivity() {
      for(Activity var2 : this.activeActivities) {
         if (!this.coreActivities.contains(var2)) {
            return Optional.of(var2);
         }
      }

      return Optional.empty();
   }

   public void setActiveActivityIfPossible(Activity var1) {
      if (this.activityRequirementsAreMet(var1)) {
         this.setActiveActivity(var1);
      } else {
         this.useDefaultActivity();
      }
   }

   private void setActiveActivity(Activity var1) {
      if (!this.isActive(var1)) {
         this.eraseMemoriesForOtherActivitesThan(var1);
         this.activeActivities.clear();
         this.activeActivities.addAll(this.coreActivities);
         this.activeActivities.add(var1);
      }
   }

   private void eraseMemoriesForOtherActivitesThan(Activity var1) {
      for(Activity var3 : this.activeActivities) {
         if (var3 != var1) {
            Set var4 = this.activityMemoriesToEraseWhenStopped.get(var3);
            if (var4 != null) {
               for(MemoryModuleType var6 : var4) {
                  this.eraseMemory(var6);
               }
            }
         }
      }
   }

   public void updateActivityFromSchedule(long var1, long var3) {
      if (var3 - this.lastScheduleUpdate > 20L) {
         this.lastScheduleUpdate = var3;
         Activity var5 = this.getSchedule().getActivityAt((int)(var1 % 24000L));
         if (!this.activeActivities.contains(var5)) {
            this.setActiveActivityIfPossible(var5);
         }
      }
   }

   public void setActiveActivityToFirstValid(List<Activity> var1) {
      for(Activity var3 : var1) {
         if (this.activityRequirementsAreMet(var3)) {
            this.setActiveActivity(var3);
            break;
         }
      }
   }

   public void setDefaultActivity(Activity var1) {
      this.defaultActivity = var1;
   }

   public void addActivity(Activity var1, int var2, ImmutableList<? extends Behavior<? super E>> var3) {
      this.addActivity(var1, this.createPriorityPairs(var2, var3));
   }

   public void addActivityAndRemoveMemoryWhenStopped(Activity var1, int var2, ImmutableList<? extends Behavior<? super E>> var3, MemoryModuleType<?> var4) {
      ImmutableSet var5 = ImmutableSet.of(Pair.of(var4, MemoryStatus.VALUE_PRESENT));
      ImmutableSet var6 = ImmutableSet.of(var4);
      this.addActivityAndRemoveMemoriesWhenStopped(var1, this.createPriorityPairs(var2, var3), var5, var6);
   }

   public void addActivity(Activity var1, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> var2) {
      this.addActivityAndRemoveMemoriesWhenStopped(var1, var2, ImmutableSet.of(), Sets.newHashSet());
   }

   public void addActivityWithConditions(
      Activity var1, ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> var2, Set<Pair<MemoryModuleType<?>, MemoryStatus>> var3
   ) {
      this.addActivityAndRemoveMemoriesWhenStopped(var1, var2, var3, Sets.newHashSet());
   }

   public void addActivityAndRemoveMemoriesWhenStopped(
      Activity var1,
      ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> var2,
      Set<Pair<MemoryModuleType<?>, MemoryStatus>> var3,
      Set<MemoryModuleType<?>> var4
   ) {
      this.activityRequirements.put(var1, var3);
      if (!var4.isEmpty()) {
         this.activityMemoriesToEraseWhenStopped.put(var1, var4);
      }

      UnmodifiableIterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Pair var6 = (Pair)var5.next();
         this.availableBehaviorsByPriority
            .computeIfAbsent((Integer)var6.getFirst(), var0 -> Maps.newHashMap())
            .computeIfAbsent(var1, var0 -> Sets.newLinkedHashSet())
            .add((Behavior<? super E>)var6.getSecond());
      }
   }

   @VisibleForTesting
   public void removeAllBehaviors() {
      this.availableBehaviorsByPriority.clear();
   }

   public boolean isActive(Activity var1) {
      return this.activeActivities.contains(var1);
   }

   public Brain<E> copyWithoutBehaviors() {
      Brain var1 = new Brain<>(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codec);

      for(Entry var3 : this.memories.entrySet()) {
         MemoryModuleType var4 = (MemoryModuleType)var3.getKey();
         if (((Optional)var3.getValue()).isPresent()) {
            var1.memories.put(var4, (Optional<? extends ExpirableValue<?>>)var3.getValue());
         }
      }

      return var1;
   }

   public void tick(ServerLevel var1, E var2) {
      this.forgetOutdatedMemories();
      this.tickSensors(var1, (E)var2);
      this.startEachNonRunningBehavior(var1, (E)var2);
      this.tickEachRunningBehavior(var1, (E)var2);
   }

   private void tickSensors(ServerLevel var1, E var2) {
      for(Sensor var4 : this.sensors.values()) {
         var4.tick(var1, var2);
      }
   }

   private void forgetOutdatedMemories() {
      for(Entry var2 : this.memories.entrySet()) {
         if (((Optional)var2.getValue()).isPresent()) {
            ExpirableValue var3 = (ExpirableValue)((Optional)var2.getValue()).get();
            if (var3.hasExpired()) {
               this.eraseMemory((MemoryModuleType)var2.getKey());
            }

            var3.tick();
         }
      }
   }

   public void stopAll(ServerLevel var1, E var2) {
      long var3 = var2.level.getGameTime();

      for(Behavior var6 : this.getRunningBehaviors()) {
         var6.doStop(var1, var2, var3);
      }
   }

   private void startEachNonRunningBehavior(ServerLevel var1, E var2) {
      long var3 = var1.getGameTime();

      for(Map var6 : this.availableBehaviorsByPriority.values()) {
         for(Entry var8 : var6.entrySet()) {
            Activity var9 = (Activity)var8.getKey();
            if (this.activeActivities.contains(var9)) {
               for(Behavior var12 : (Set)var8.getValue()) {
                  if (var12.getStatus() == Behavior.Status.STOPPED) {
                     var12.tryStart(var1, var2, var3);
                  }
               }
            }
         }
      }
   }

   private void tickEachRunningBehavior(ServerLevel var1, E var2) {
      long var3 = var1.getGameTime();

      for(Behavior var6 : this.getRunningBehaviors()) {
         var6.tickOrStop(var1, var2, var3);
      }
   }

   private boolean activityRequirementsAreMet(Activity var1) {
      if (!this.activityRequirements.containsKey(var1)) {
         return false;
      } else {
         for(Pair var3 : this.activityRequirements.get(var1)) {
            MemoryModuleType var4 = (MemoryModuleType)var3.getFirst();
            MemoryStatus var5 = (MemoryStatus)var3.getSecond();
            if (!this.checkMemory(var4, var5)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isEmptyCollection(Object var1) {
      return var1 instanceof Collection && ((Collection)var1).isEmpty();
   }

   ImmutableList<? extends Pair<Integer, ? extends Behavior<? super E>>> createPriorityPairs(int var1, ImmutableList<? extends Behavior<? super E>> var2) {
      int var3 = var1;
      Builder var4 = ImmutableList.builder();
      UnmodifiableIterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Behavior var6 = (Behavior)var5.next();
         var4.add(Pair.of(var3++, var6));
      }

      return var4.build();
   }

   static final class MemoryValue<U> {
      private final MemoryModuleType<U> type;
      private final Optional<? extends ExpirableValue<U>> value;

      static <U> Brain.MemoryValue<U> createUnchecked(MemoryModuleType<U> var0, Optional<? extends ExpirableValue<?>> var1) {
         return new Brain.MemoryValue<>(var0, var1);
      }

      MemoryValue(MemoryModuleType<U> var1, Optional<? extends ExpirableValue<U>> var2) {
         super();
         this.type = var1;
         this.value = var2;
      }

      void setMemoryInternal(Brain<?> var1) {
         var1.setMemoryInternal(this.type, this.value);
      }

      public <T> void serialize(DynamicOps<T> var1, RecordBuilder<T> var2) {
         this.type
            .getCodec()
            .ifPresent(
               var3 -> this.value
                     .ifPresent(var4 -> var2.add(Registry.MEMORY_MODULE_TYPE.byNameCodec().encodeStart(var1, this.type), var3.encodeStart(var1, var4)))
            );
      }
   }

   public static final class Provider<E extends LivingEntity> {
      private final Collection<? extends MemoryModuleType<?>> memoryTypes;
      private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes;
      private final Codec<Brain<E>> codec;

      Provider(Collection<? extends MemoryModuleType<?>> var1, Collection<? extends SensorType<? extends Sensor<? super E>>> var2) {
         super();
         this.memoryTypes = var1;
         this.sensorTypes = var2;
         this.codec = Brain.codec(var1, var2);
      }

      public Brain<E> makeBrain(Dynamic<?> var1) {
         return this.codec
            .parse(var1)
            .resultOrPartial(Brain.LOGGER::error)
            .orElseGet(() -> new Brain<>(this.memoryTypes, this.sensorTypes, ImmutableList.of(), () -> this.codec));
      }
   }
}
