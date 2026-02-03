package net.minecraft.world.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryMap;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Brain<E extends LivingEntity> {
   private static final int SCHEDULE_UPDATE_DELAY = 20;
   private final Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> memories = Maps.newHashMap();
   private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
   private final Map<Integer, Map<Activity, Set<BehaviorControl<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
   private @Nullable EnvironmentAttribute<Activity> schedule;
   private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryStatus>>> activityRequirements = Maps.newHashMap();
   private final Map<Activity, Set<MemoryModuleType<?>>> activityMemoriesToEraseWhenStopped = Maps.newHashMap();
   private Set<Activity> coreActivities = Sets.newHashSet();
   private final Set<Activity> activeActivities = Sets.newHashSet();
   private Activity defaultActivity;
   private long lastScheduleUpdate;

   public static <E extends LivingEntity> Provider<E> provider(final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes) {
      return new Provider<E>(ImmutableList.of(), sensorTypes, (var0) -> List.of());
   }

   public static <E extends LivingEntity> Provider<E> provider(final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes, final ActivitySupplier<E> activities) {
      return new Provider<E>(ImmutableList.of(), sensorTypes, activities);
   }

   /** @deprecated */
   @Deprecated
   public static <E extends LivingEntity> Provider<E> provider(final Collection<? extends MemoryModuleType<?>> memoryTypes, final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes, final ActivitySupplier<E> activities) {
      return new Provider<E>(memoryTypes, sensorTypes, activities);
   }

   @VisibleForTesting
   protected Brain(final Collection<? extends MemoryModuleType<?>> memoryTypes, final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes, final List<ActivityData<E>> activities, final MemoryMap memories) {
      super();
      this.defaultActivity = Activity.IDLE;
      this.lastScheduleUpdate = -9999L;

      for(MemoryModuleType<?> memoryType : memoryTypes) {
         this.memories.put(memoryType, Optional.empty());
      }

      for(SensorType<? extends Sensor<? super E>> sensorType : sensorTypes) {
         this.sensors.put(sensorType, sensorType.create());
      }

      for(Sensor<? super E> sensor : this.sensors.values()) {
         for(MemoryModuleType<?> type : sensor.requires()) {
            this.memories.put(type, Optional.empty());
         }
      }

      for(ActivityData<E> activity : activities) {
         this.addActivity(activity.activityType(), activity.behaviorPriorityPairs(), activity.conditions(), activity.memoriesToEraseWhenStopped());
      }

      for(MemoryMap.Value<?> memory : memories) {
         this.setMemoryInternal(memory);
      }

      this.setCoreActivities(ImmutableSet.of(Activity.CORE));
      this.useDefaultActivity();
   }

   public Brain() {
      super();
      this.defaultActivity = Activity.IDLE;
      this.lastScheduleUpdate = -9999L;
      this.setCoreActivities(ImmutableSet.of(Activity.CORE));
      this.useDefaultActivity();
   }

   public Packed pack() {
      return new Packed(MemoryMap.of(this.memories.entrySet().stream().filter((entry) -> ((MemoryModuleType)entry.getKey()).getCodec().isPresent()).flatMap((entry) -> ((Optional)entry.getValue()).map((value) -> MemoryMap.Value.createUnchecked((MemoryModuleType)entry.getKey(), value)).stream())));
   }

   public boolean hasMemoryValue(final MemoryModuleType<?> type) {
      return this.checkMemory(type, MemoryStatus.VALUE_PRESENT);
   }

   public void clearMemories() {
      this.memories.keySet().forEach((key) -> this.memories.put(key, Optional.empty()));
   }

   public <U> void eraseMemory(final MemoryModuleType<U> type) {
      this.setMemory(type, Optional.empty());
   }

   public <U> void setMemory(final MemoryModuleType<U> type, final @Nullable U value) {
      this.setMemory(type, Optional.ofNullable(value));
   }

   public <U> void setMemoryWithExpiry(final MemoryModuleType<U> type, final U value, final long timeToLive) {
      this.setMemoryInternal(type, Optional.of(ExpirableValue.of(value, timeToLive)));
   }

   public <U> void setMemory(final MemoryModuleType<U> type, final Optional<? extends U> optionalValue) {
      this.setMemoryInternal(type, optionalValue.map(ExpirableValue::of));
   }

   private <U> void setMemoryInternal(final MemoryModuleType<U> type, final Optional<? extends ExpirableValue<?>> optionalExpirableValue) {
      if (this.memories.containsKey(type)) {
         if (optionalExpirableValue.isPresent() && this.isEmptyCollection(((ExpirableValue)optionalExpirableValue.get()).getValue())) {
            this.eraseMemory(type);
         } else {
            this.memories.put(type, optionalExpirableValue);
         }
      }

   }

   private <U> void setMemoryInternal(final MemoryMap.Value<U> value) {
      this.setMemoryInternal(value.type(), Optional.of(value.value()));
   }

   public <U> Optional<U> getMemory(final MemoryModuleType<U> type) {
      Optional<? extends ExpirableValue<?>> expirableValue = (Optional)this.memories.get(type);
      if (expirableValue == null) {
         throw new IllegalStateException("Unregistered memory fetched: " + String.valueOf(type));
      } else {
         return expirableValue.map(ExpirableValue::getValue);
      }
   }

   public <U> @Nullable Optional<U> getMemoryInternal(final MemoryModuleType<U> type) {
      Optional<? extends ExpirableValue<?>> expirableValue = (Optional)this.memories.get(type);
      return expirableValue == null ? null : expirableValue.map(ExpirableValue::getValue);
   }

   public <U> long getTimeUntilExpiry(final MemoryModuleType<U> type) {
      Optional<? extends ExpirableValue<?>> memory = (Optional)this.memories.get(type);
      return (Long)memory.map(ExpirableValue::getTimeToLive).orElse(0L);
   }

   /** @deprecated */
   @Deprecated
   @VisibleForDebug
   public Map<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> getMemories() {
      return this.memories;
   }

   public <U> boolean isMemoryValue(final MemoryModuleType<U> memoryType, final U value) {
      return !this.hasMemoryValue(memoryType) ? false : this.getMemory(memoryType).filter((memory) -> memory.equals(value)).isPresent();
   }

   public boolean checkMemory(final MemoryModuleType<?> type, final MemoryStatus status) {
      Optional<? extends ExpirableValue<?>> optionalExpirableValue = (Optional)this.memories.get(type);
      if (optionalExpirableValue == null) {
         return false;
      } else {
         return status == MemoryStatus.REGISTERED || status == MemoryStatus.VALUE_PRESENT && optionalExpirableValue.isPresent() || status == MemoryStatus.VALUE_ABSENT && optionalExpirableValue.isEmpty();
      }
   }

   public void setSchedule(final EnvironmentAttribute<Activity> schedule) {
      this.schedule = schedule;
   }

   public void setCoreActivities(final Set<Activity> activities) {
      this.coreActivities = activities;
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
      List<BehaviorControl<? super E>> runningBehaviours = new ObjectArrayList();

      for(Map<Activity, Set<BehaviorControl<? super E>>> behavioursByActivities : this.availableBehaviorsByPriority.values()) {
         for(Set<BehaviorControl<? super E>> behaviors : behavioursByActivities.values()) {
            for(BehaviorControl<? super E> behavior : behaviors) {
               if (behavior.getStatus() == Behavior.Status.RUNNING) {
                  runningBehaviours.add(behavior);
               }
            }
         }
      }

      return runningBehaviours;
   }

   public void useDefaultActivity() {
      this.setActiveActivity(this.defaultActivity);
   }

   public Optional<Activity> getActiveNonCoreActivity() {
      for(Activity activity : this.activeActivities) {
         if (!this.coreActivities.contains(activity)) {
            return Optional.of(activity);
         }
      }

      return Optional.empty();
   }

   public void setActiveActivityIfPossible(final Activity activity) {
      if (this.activityRequirementsAreMet(activity)) {
         this.setActiveActivity(activity);
      } else {
         this.useDefaultActivity();
      }

   }

   private void setActiveActivity(final Activity activity) {
      if (!this.isActive(activity)) {
         this.eraseMemoriesForOtherActivitesThan(activity);
         this.activeActivities.clear();
         this.activeActivities.addAll(this.coreActivities);
         this.activeActivities.add(activity);
      }
   }

   private void eraseMemoriesForOtherActivitesThan(final Activity activity) {
      for(Activity oldActivity : this.activeActivities) {
         if (oldActivity != activity) {
            Set<MemoryModuleType<?>> memoryModuleTypes = (Set)this.activityMemoriesToEraseWhenStopped.get(oldActivity);
            if (memoryModuleTypes != null) {
               for(MemoryModuleType<?> memoryModuleType : memoryModuleTypes) {
                  this.eraseMemory(memoryModuleType);
               }
            }
         }
      }

   }

   public void updateActivityFromSchedule(final EnvironmentAttributeSystem environmentAttributes, final long gameTime, final Vec3 pos) {
      if (gameTime - this.lastScheduleUpdate > 20L) {
         this.lastScheduleUpdate = gameTime;
         Activity scheduledActivity = this.schedule != null ? (Activity)environmentAttributes.getValue(this.schedule, pos) : Activity.IDLE;
         if (!this.activeActivities.contains(scheduledActivity)) {
            this.setActiveActivityIfPossible(scheduledActivity);
         }
      }

   }

   public void setActiveActivityToFirstValid(final List<Activity> activities) {
      for(Activity activity : activities) {
         if (this.activityRequirementsAreMet(activity)) {
            this.setActiveActivity(activity);
            break;
         }
      }

   }

   public void setDefaultActivity(final Activity activity) {
      this.defaultActivity = activity;
   }

   public void addActivity(final Activity activity, final ImmutableList<? extends Pair<Integer, ? extends BehaviorControl<? super E>>> behaviorPriorityPairs, final Set<Pair<MemoryModuleType<?>, MemoryStatus>> conditions, final Set<MemoryModuleType<?>> memoriesToEraseWhenStopped) {
      this.activityRequirements.put(activity, conditions);
      if (!memoriesToEraseWhenStopped.isEmpty()) {
         this.activityMemoriesToEraseWhenStopped.put(activity, memoriesToEraseWhenStopped);
      }

      UnmodifiableIterator var5 = behaviorPriorityPairs.iterator();

      while(var5.hasNext()) {
         Pair<Integer, ? extends BehaviorControl<? super E>> pair = (Pair)var5.next();
         BehaviorControl<? super E> behavior = (BehaviorControl)pair.getSecond();

         for(MemoryModuleType<?> requiredMemory : behavior.getRequiredMemories()) {
            if (!this.memories.containsKey(requiredMemory)) {
               this.memories.put(requiredMemory, Optional.empty());
            }
         }

         ((Set)((Map)this.availableBehaviorsByPriority.computeIfAbsent((Integer)pair.getFirst(), (key) -> Maps.newHashMap())).computeIfAbsent(activity, (key) -> Sets.newLinkedHashSet())).add(behavior);
      }

   }

   @VisibleForTesting
   public void removeAllBehaviors() {
      this.availableBehaviorsByPriority.clear();
   }

   public boolean isActive(final Activity activity) {
      return this.activeActivities.contains(activity);
   }

   public void tick(final ServerLevel level, final E body) {
      this.forgetOutdatedMemories();
      this.tickSensors(level, body);
      this.startEachNonRunningBehavior(level, body);
      this.tickEachRunningBehavior(level, body);
   }

   private void tickSensors(final ServerLevel level, final E body) {
      for(Sensor<? super E> sensor : this.sensors.values()) {
         sensor.tick(level, body);
      }

   }

   private void forgetOutdatedMemories() {
      for(Map.Entry<MemoryModuleType<?>, Optional<? extends ExpirableValue<?>>> entry : this.memories.entrySet()) {
         if (((Optional)entry.getValue()).isPresent()) {
            ExpirableValue<?> memory = (ExpirableValue)((Optional)entry.getValue()).get();
            if (memory.hasExpired()) {
               this.eraseMemory((MemoryModuleType)entry.getKey());
            }

            memory.tick();
         }
      }

   }

   public void stopAll(final ServerLevel level, final E body) {
      long timestamp = body.level().getGameTime();

      for(BehaviorControl<? super E> behavior : this.getRunningBehaviors()) {
         behavior.doStop(level, body, timestamp);
      }

   }

   private void startEachNonRunningBehavior(final ServerLevel level, final E body) {
      long time = level.getGameTime();

      for(Map<Activity, Set<BehaviorControl<? super E>>> behavioursByActivities : this.availableBehaviorsByPriority.values()) {
         for(Map.Entry<Activity, Set<BehaviorControl<? super E>>> behavioursForActivity : behavioursByActivities.entrySet()) {
            Activity activity = (Activity)behavioursForActivity.getKey();
            if (this.activeActivities.contains(activity)) {
               for(BehaviorControl<? super E> behavior : (Set)behavioursForActivity.getValue()) {
                  if (behavior.getStatus() == Behavior.Status.STOPPED) {
                     behavior.tryStart(level, body, time);
                  }
               }
            }
         }
      }

   }

   private void tickEachRunningBehavior(final ServerLevel level, final E body) {
      long timestamp = level.getGameTime();

      for(BehaviorControl<? super E> behavior : this.getRunningBehaviors()) {
         behavior.tickOrStop(level, body, timestamp);
      }

   }

   private boolean activityRequirementsAreMet(final Activity activity) {
      if (!this.activityRequirements.containsKey(activity)) {
         return false;
      } else {
         for(Pair<MemoryModuleType<?>, MemoryStatus> memoryRequirement : (Set)this.activityRequirements.get(activity)) {
            MemoryModuleType<?> memoryType = (MemoryModuleType)memoryRequirement.getFirst();
            MemoryStatus memoryStatus = (MemoryStatus)memoryRequirement.getSecond();
            if (!this.checkMemory(memoryType, memoryStatus)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean isEmptyCollection(final Object object) {
      return object instanceof Collection && ((Collection)object).isEmpty();
   }

   public boolean isBrainDead() {
      return this.memories.isEmpty() && this.sensors.isEmpty() && this.availableBehaviorsByPriority.isEmpty();
   }

   public static final class Provider<E extends LivingEntity> {
      private final Collection<? extends MemoryModuleType<?>> memoryTypes;
      private final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes;
      private final ActivitySupplier<E> activities;

      private Provider(final Collection<? extends MemoryModuleType<?>> memoryTypes, final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes, final ActivitySupplier<E> activities) {
         super();
         this.memoryTypes = memoryTypes;
         this.sensorTypes = sensorTypes;
         this.activities = activities;
      }

      public Brain<E> makeBrain(final E body, final Packed packed) {
         List<ActivityData<E>> activities = this.activities.createActivities(body);
         return new Brain<E>(this.memoryTypes, this.sensorTypes, activities, packed.memories);
      }
   }

   public static record Packed(MemoryMap memories) {
      public static final Packed EMPTY;
      public static final Codec<Packed> CODEC;

      public Packed {
         super();
      }

      static {
         EMPTY = new Packed(MemoryMap.EMPTY);
         CODEC = RecordCodecBuilder.create((i) -> i.group(MemoryMap.CODEC.fieldOf("memories").forGetter(Packed::memories)).apply(i, Packed::new));
      }
   }

   @FunctionalInterface
   public interface ActivitySupplier<E extends LivingEntity> {
      List<ActivityData<E>> createActivities(E body);
   }
}
