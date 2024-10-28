package net.minecraft.world.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
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
   private final Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
   private Schedule schedule;
   private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements;
   private final Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped;
   private Set<Activity> coreActivities;
   private final Set<Activity> activeActivities;
   private Activity defaultActivity;
   private long lastScheduleUpdate;

   public static <E extends LivingEntity> Provider<E> provider(Collection<? extends MemoryModuleType<?>> var0, Collection<? extends SensorType<? extends Sensor<? super E>>> var1) {
      return new Provider(var0, var1);
   }

   public static <E extends LivingEntity> Codec<Brain<E>> codec(final Collection<? extends MemoryModuleType<?>> var0, final Collection<? extends SensorType<? extends Sensor<? super E>>> var1) {
      final MutableObject var2 = new MutableObject();
      var2.setValue((new MapCodec<Brain<E>>() {
         public <T> Stream<T> keys(DynamicOps<T> var1x) {
            return var0.stream().flatMap((var0x) -> {
               return var0x.getCodec().map((var1x) -> {
                  return BuiltInRegistries.MEMORY_MODULE_TYPE.getKey(var0x);
               }).stream();
            }).map((var1xx) -> {
               return var1x.createString(var1xx.toString());
            });
         }

         public <T> DataResult<Brain<E>> decode(DynamicOps<T> var1x, MapLike<T> var2x) {
            MutableObject var3 = new MutableObject(DataResult.success(ImmutableList.builder()));
            var2x.entries().forEach((var3x) -> {
               DataResult var4 = BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().parse(var1x, var3x.getFirst());
               DataResult var5 = var4.flatMap((var3xx) -> {
                  return this.captureRead(var3xx, var1x, var3x.getSecond());
               });
               var3.setValue(((DataResult)var3.getValue()).apply2(ImmutableList.Builder::add, var5));
            });
            DataResult var10000 = (DataResult)var3.getValue();
            Logger var10001 = Brain.LOGGER;
            Objects.requireNonNull(var10001);
            ImmutableList var4 = (ImmutableList)var10000.resultOrPartial(var10001::error).map(ImmutableList.Builder::build).orElseGet(ImmutableList::of);
            Collection var10002 = var0;
            Collection var10003 = var1;
            MutableObject var10005 = var2;
            Objects.requireNonNull(var10005);
            return DataResult.success(new Brain(var10002, var10003, var4, var10005::getValue));
         }

         private <T, U> DataResult<MemoryValue<U>> captureRead(MemoryModuleType<U> var1x, DynamicOps<T> var2x, T var3) {
            return ((DataResult)var1x.getCodec().map(DataResult::success).orElseGet(() -> {
               return DataResult.error(() -> {
                  return "No codec for memory: " + String.valueOf(var1x);
               });
            })).flatMap((var2xx) -> {
               return var2xx.parse(var2x, var3);
            }).map((var1xx) -> {
               return new MemoryValue(var1x, Optional.of(var1xx));
            });
         }

         public <T> RecordBuilder<T> encode(Brain<E> var1x, DynamicOps<T> var2x, RecordBuilder<T> var3) {
            var1x.memories().forEach((var2xx) -> {
               var2xx.serialize(var2x, var3);
            });
            return var3;
         }

         // $FF: synthetic method
         public RecordBuilder encode(Object var1x, DynamicOps var2x, RecordBuilder var3) {
            return this.encode((Brain)var1x, var2x, var3);
         }
      }).fieldOf("memories").codec());
      return (Codec)var2.getValue();
   }

   public Brain(Collection<? extends MemoryModuleType<?>> var1, Collection<? extends SensorType<? extends Sensor<? super E>>> var2, ImmutableList<MemoryValue<?>> var3, Supplier<Codec<Brain<E>>> var4) {
      super();
      this.schedule = Schedule.EMPTY;
      this.activityRequirements = Maps.newHashMap();
      this.activityMemoriesToEraseWhenStopped = Maps.newHashMap();
      this.coreActivities = Sets.newHashSet();
      this.activeActivities = Sets.newHashSet();
      this.defaultActivity = Activity.IDLE;
      this.lastScheduleUpdate = -9999L;
      this.codec = var4;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         MemoryModuleType var6 = (MemoryModuleType)var5.next();
         this.memories.put(var6, Optional.empty());
      }

      var5 = var2.iterator();

      while(var5.hasNext()) {
         SensorType var10 = (SensorType)var5.next();
         this.sensors.put(var10, var10.create());
      }

      var5 = this.sensors.values().iterator();

      while(var5.hasNext()) {
         Sensor var11 = (Sensor)var5.next();
         Iterator var7 = var11.requires().iterator();

         while(var7.hasNext()) {
            MemoryModuleType var8 = (MemoryModuleType)var7.next();
            this.memories.put(var8, Optional.empty());
         }
      }

      UnmodifiableIterator var9 = var3.iterator();

      while(var9.hasNext()) {
         MemoryValue var12 = (MemoryValue)var9.next();
         var12.setMemoryInternal(this);
      }

   }

   public <T> DataResult<T> serializeStart(DynamicOps<T> var1) {
      return ((Codec)this.codec.get()).encodeStart(var1, this);
   }

   Stream<MemoryValue<?>> memories() {
      return this.memories.entrySet().stream().map((var0) -> {
         return Brain.MemoryValue.createUnchecked((MemoryModuleType)var0.getKey(), (Optional)var0.getValue());
      });
   }

   public boolean hasMemoryValue(MemoryModuleType<?> var1) {
      return this.checkMemory(var1, MemoryStatus.VALUE_PRESENT);
   }

   public void clearMemories() {
      this.memories.keySet().forEach((var1) -> {
         this.memories.put(var1, Optional.empty());
      });
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
      Optional var2 = (Optional)this.memories.get(var1);
      if (var2 == null) {
         throw new IllegalStateException("Unregistered memory fetched: " + String.valueOf(var1));
      } else {
         return var2.map(ExpirableValue::getValue);
      }
   }

   @Nullable
   public <U> Optional<U> getMemoryInternal(MemoryModuleType<U> var1) {
      Optional var2 = (Optional)this.memories.get(var1);
      return var2 == null ? null : var2.map(ExpirableValue::getValue);
   }

   public <U> long getTimeUntilExpiry(MemoryModuleType<U> var1) {
      Optional var2 = (Optional)this.memories.get(var1);
      return (Long)var2.map(ExpirableValue::getTimeToLive).orElse(0L);
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMemories() {
      return this.memories;
   }

   public <U> boolean isMemoryValue(MemoryModuleType<U> var1, U var2) {
      return !this.hasMemoryValue(var1) ? false : this.getMemory(var1).filter((var1x) -> {
         return var1x.equals(var2);
      }).isPresent();
   }

   public boolean checkMemory(MemoryModuleType<?> var1, MemoryStatus var2) {
      Optional var3 = (Optional)this.memories.get(var1);
      if (var3 == null) {
         return false;
      } else {
         return var2 == MemoryStatus.REGISTERED || var2 == MemoryStatus.VALUE_PRESENT && var3.isPresent() || var2 == MemoryStatus.VALUE_ABSENT && var3.isEmpty();
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

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public Set<Activity> getActiveActivities() {
      return this.activeActivities;
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public List<BehaviorControl<? super E>> getRunningBehaviors() {
      ObjectArrayList var1 = new ObjectArrayList();
      Iterator var2 = this.availableBehaviorsByPriority.values().iterator();

      while(var2.hasNext()) {
         Map var3 = (Map)var2.next();
         Iterator var4 = var3.values().iterator();

         while(var4.hasNext()) {
            Set var5 = (Set)var4.next();
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               BehaviorControl var7 = (BehaviorControl)var6.next();
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
      Iterator var1 = this.activeActivities.iterator();

      Activity var2;
      do {
         if (!var1.hasNext()) {
            return Optional.empty();
         }

         var2 = (Activity)var1.next();
      } while(this.coreActivities.contains(var2));

      return Optional.of(var2);
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
      Iterator var2 = this.activeActivities.iterator();

      while(true) {
         Set var4;
         do {
            Activity var3;
            do {
               if (!var2.hasNext()) {
                  return;
               }

               var3 = (Activity)var2.next();
            } while(var3 == var1);

            var4 = (Set)this.activityMemoriesToEraseWhenStopped.get(var3);
         } while(var4 == null);

         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            MemoryModuleType var6 = (MemoryModuleType)var5.next();
            this.eraseMemory(var6);
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
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Activity var3 = (Activity)var2.next();
         if (this.activityRequirementsAreMet(var3)) {
            this.setActiveActivity(var3);
            break;
         }
      }

   }

   public void setDefaultActivity(Activity var1) {
      this.defaultActivity = var1;
   }

   public void addActivity(Activity var1, int var2, ImmutableList<? extends BehaviorControl<? super E>> var3) {
      this.addActivity(var1, this.createPriorityPairs(var2, var3));
   }

   public void addActivityAndRemoveMemoryWhenStopped(Activity var1, int var2, ImmutableList<? extends BehaviorControl<? super E>> var3, MemoryModuleType<?> var4) {
      ImmutableSet var5 = ImmutableSet.of(Pair.of(var4, MemoryStatus.VALUE_PRESENT));
      ImmutableSet var6 = ImmutableSet.of(var4);
      this.addActivityAndRemoveMemoriesWhenStopped(var1, this.createPriorityPairs(var2, var3), var5, var6);
   }

   public void addActivity(Activity var1, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> var2) {
      this.addActivityAndRemoveMemoriesWhenStopped(var1, var2, ImmutableSet.of(), Sets.newHashSet());
   }

   public void addActivityWithConditions(Activity var1, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> var2, Set<Pair<MemoryModuleType<?>, MemoryStatus>> var3) {
      this.addActivityAndRemoveMemoriesWhenStopped(var1, var2, var3, Sets.newHashSet());
   }

   public void addActivityAndRemoveMemoriesWhenStopped(Activity var1, ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> var2, Set<Pair<MemoryModuleType<?>, MemoryStatus>> var3, Set<MemoryModuleType<?>> var4) {
      this.activityRequirements.put(var1, var3);
      if (!var4.isEmpty()) {
         this.activityMemoriesToEraseWhenStopped.put(var1, var4);
      }

      UnmodifiableIterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Pair var6 = (Pair)var5.next();
         ((Set)((Map)this.availableBehaviorsByPriority.computeIfAbsent((Integer)var6.getFirst(), (var0) -> {
            return Maps.newHashMap();
         })).computeIfAbsent(var1, (var0) -> {
            return Sets.newLinkedHashSet();
         })).add((BehaviorControl)var6.getSecond());
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
      Brain var1 = new Brain(this.memories.keySet(), this.sensors.keySet(), ImmutableList.of(), this.codec);
      Iterator var2 = this.memories.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         MemoryModuleType var4 = (MemoryModuleType)var3.getKey();
         if (((Optional)var3.getValue()).isPresent()) {
            var1.memories.put(var4, (Optional)var3.getValue());
         }
      }

      return var1;
   }

   public void tick(ServerLevel var1, E var2) {
      this.forgetOutdatedMemories();
      this.tickSensors(var1, var2);
      this.startEachNonRunningBehavior(var1, var2);
      this.tickEachRunningBehavior(var1, var2);
   }

   private void tickSensors(ServerLevel var1, E var2) {
      Iterator var3 = this.sensors.values().iterator();

      while(var3.hasNext()) {
         Sensor var4 = (Sensor)var3.next();
         var4.tick(var1, var2);
      }

   }

   private void forgetOutdatedMemories() {
      Iterator var1 = this.memories.entrySet().iterator();

      while(var1.hasNext()) {
         Map.Entry var2 = (Map.Entry)var1.next();
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
      long var3 = var2.level().getGameTime();
      Iterator var5 = this.getRunningBehaviors().iterator();

      while(var5.hasNext()) {
         BehaviorControl var6 = (BehaviorControl)var5.next();
         var6.doStop(var1, var2, var3);
      }

   }

   private void startEachNonRunningBehavior(ServerLevel var1, E var2) {
      long var3 = var1.getGameTime();
      Iterator var5 = this.availableBehaviorsByPriority.values().iterator();

      label34:
      while(var5.hasNext()) {
         Map var6 = (Map)var5.next();
         Iterator var7 = var6.entrySet().iterator();

         while(true) {
            Map.Entry var8;
            Activity var9;
            do {
               if (!var7.hasNext()) {
                  continue label34;
               }

               var8 = (Map.Entry)var7.next();
               var9 = (Activity)var8.getKey();
            } while(!this.activeActivities.contains(var9));

            Set var10 = (Set)var8.getValue();
            Iterator var11 = var10.iterator();

            while(var11.hasNext()) {
               BehaviorControl var12 = (BehaviorControl)var11.next();
               if (var12.getStatus() == Behavior.Status.STOPPED) {
                  var12.tryStart(var1, var2, var3);
               }
            }
         }
      }

   }

   private void tickEachRunningBehavior(ServerLevel var1, E var2) {
      long var3 = var1.getGameTime();
      Iterator var5 = this.getRunningBehaviors().iterator();

      while(var5.hasNext()) {
         BehaviorControl var6 = (BehaviorControl)var5.next();
         var6.tickOrStop(var1, var2, var3);
      }

   }

   private boolean activityRequirementsAreMet(Activity var1) {
      if (!this.activityRequirements.containsKey(var1)) {
         return false;
      } else {
         Iterator var2 = ((Set)this.activityRequirements.get(var1)).iterator();

         MemoryModuleType var4;
         MemoryStatus var5;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            Pair var3 = (Pair)var2.next();
            var4 = (MemoryModuleType)var3.getFirst();
            var5 = (MemoryStatus)var3.getSecond();
         } while(this.checkMemory(var4, var5));

         return false;
      }
   }

   private boolean isEmptyCollection(Object var1) {
      return var1 instanceof Collection && ((Collection)var1).isEmpty();
   }

   ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> createPriorityPairs(int var1, ImmutableList<? extends BehaviorControl<? super E>> var2) {
      int var3 = var1;
      ImmutableList.Builder var4 = ImmutableList.builder();
      UnmodifiableIterator var5 = var2.iterator();

      while(var5.hasNext()) {
         BehaviorControl var6 = (BehaviorControl)var5.next();
         var4.add(Pair.of(var3++, var6));
      }

      return var4.build();
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
         DataResult var10000 = this.codec.parse(var1);
         Logger var10001 = Brain.LOGGER;
         Objects.requireNonNull(var10001);
         return (Brain)var10000.resultOrPartial(var10001::error).orElseGet(() -> {
            return new Brain(this.memoryTypes, this.sensorTypes, ImmutableList.of(), () -> {
               return this.codec;
            });
         });
      }
   }

   private static final class MemoryValue<U> {
      private final MemoryModuleType<U> type;
      private final Optional<? extends ExpirableValue<U>> value;

      static <U> MemoryValue<U> createUnchecked(MemoryModuleType<U> var0, Optional<? extends ExpirableValue<?>> var1) {
         return new MemoryValue(var0, var1);
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
         this.type.getCodec().ifPresent((var3) -> {
            this.value.ifPresent((var4) -> {
               var2.add(BuiltInRegistries.MEMORY_MODULE_TYPE.byNameCodec().encodeStart(var1, this.type), var3.encodeStart(var1, var4));
            });
         });
      }
   }
}
