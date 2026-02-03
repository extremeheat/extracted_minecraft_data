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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.memory.ExpirableValue;
import net.minecraft.world.entity.ai.memory.MemoryMap;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemorySlot;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Brain<E extends LivingEntity> {
   private static final int SCHEDULE_UPDATE_DELAY = 20;
   private final Map<MemoryModuleType<?>, MemorySlot<?>> memories = Maps.newHashMap();
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
   protected Brain(final Collection<? extends MemoryModuleType<?>> memoryTypes, final Collection<? extends SensorType<? extends Sensor<? super E>>> sensorTypes, final List<ActivityData<E>> activities, final MemoryMap memories, final RandomSource randomSource) {
      super();
      this.defaultActivity = Activity.IDLE;
      this.lastScheduleUpdate = -9999L;

      for(MemoryModuleType<?> memoryType : memoryTypes) {
         this.registerMemory(memoryType);
      }

      for(SensorType<? extends Sensor<? super E>> sensorType : sensorTypes) {
         Sensor<? super E> newSensor = sensorType.create();
         newSensor.randomlyDelayStart(randomSource);
         this.sensors.put(sensorType, newSensor);

         for(MemoryModuleType<?> type : newSensor.requires()) {
            this.registerMemory(type);
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

   private void registerMemory(final MemoryModuleType<?> memoryType) {
      this.memories.putIfAbsent(memoryType, MemorySlot.create());
   }

   public Brain() {
      super();
      this.defaultActivity = Activity.IDLE;
      this.lastScheduleUpdate = -9999L;
      this.setCoreActivities(ImmutableSet.of(Activity.CORE));
      this.useDefaultActivity();
   }

   public Packed pack() {
      final MemoryMap.Builder builder = new MemoryMap.Builder();
      this.forEach(new Visitor() {
         {
            Objects.requireNonNull(Brain.this);
         }

         public <U> void acceptEmpty(final MemoryModuleType<U> type) {
         }

         public <U> void accept(final MemoryModuleType<U> type, final U value, final long timeToLive) {
            if (type.canSerialize()) {
               builder.add(type, ExpirableValue.of(value, timeToLive));
            }

         }

         public <U> void accept(final MemoryModuleType<U> type, final U value) {
            if (type.canSerialize()) {
               builder.add(type, ExpirableValue.of(value));
            }

         }
      });
      return new Packed(builder.build());
   }

   private <T> @Nullable MemorySlot<T> getMemorySlotIfPresent(final MemoryModuleType<T> memoryType) {
      return (MemorySlot)this.memories.get(memoryType);
   }

   private <T> MemorySlot<T> getMemorySlot(final MemoryModuleType<T> memoryType) {
      MemorySlot<T> result = this.<T>getMemorySlotIfPresent(memoryType);
      if (result == null) {
         throw new IllegalStateException("Unregistered memory fetched: " + String.valueOf(memoryType));
      } else {
         return result;
      }
   }

   public boolean hasMemoryValue(final MemoryModuleType<?> type) {
      return this.checkMemory(type, MemoryStatus.VALUE_PRESENT);
   }

   public void clearMemories() {
      this.memories.values().forEach(MemorySlot::clear);
   }

   public <U> void eraseMemory(final MemoryModuleType<U> type) {
      MemorySlot<U> slot = this.<U>getMemorySlotIfPresent(type);
      if (slot != null) {
         slot.clear();
      }

   }

   public <U> void setMemory(final MemoryModuleType<U> type, final @Nullable U value) {
      this.setMemoryInternal(type, value);
   }

   public <U> void setMemoryWithExpiry(final MemoryModuleType<U> type, final U value, final long timeToLive) {
      this.setMemoryInternal(type, value, timeToLive);
   }

   public <U> void setMemory(final MemoryModuleType<U> type, final Optional<? extends U> optionalValue) {
      this.setMemoryInternal(type, optionalValue.orElse((Object)null));
   }

   private <U> void setMemoryInternal(final MemoryMap.Value<U> value) {
      ExpirableValue<U> expirableValue = value.value();
      if (expirableValue.timeToLive().isPresent()) {
         this.setMemoryInternal(value.type(), expirableValue.value(), (Long)expirableValue.timeToLive().get());
      } else {
         this.setMemoryInternal(value.type(), expirableValue.value());
      }

   }

   private <U> void setMemoryInternal(final MemoryModuleType<U> type, U value, final long tileToLive) {
      MemorySlot<U> slot = this.<U>getMemorySlotIfPresent(type);
      if (slot != null) {
         if (isEmptyCollection(value)) {
            value = null;
         }

         if (value == null) {
            slot.clear();
         } else {
            slot.set(value, tileToLive);
         }
      }

   }

   private <U> void setMemoryInternal(final MemoryModuleType<U> type, @Nullable U value) {
      MemorySlot<U> slot = this.<U>getMemorySlotIfPresent(type);
      if (slot != null) {
         if (value != null && isEmptyCollection(value)) {
            value = null;
         }

         if (value == null) {
            slot.clear();
         } else {
            slot.set(value);
         }
      }

   }

   public <U> Optional<U> getMemory(final MemoryModuleType<U> type) {
      return Optional.ofNullable(this.getMemorySlot(type).value());
   }

   public <U> @Nullable Optional<U> getMemoryInternal(final MemoryModuleType<U> type) {
      MemorySlot<U> slot = this.<U>getMemorySlotIfPresent(type);
      return slot == null ? null : Optional.ofNullable(slot.value());
   }

   public <U> long getTimeUntilExpiry(final MemoryModuleType<U> type) {
      return this.getMemorySlot(type).timeToLive();
   }

   public void forEach(final Visitor visitor) {
      this.memories.forEach((memoryModuleType, slot) -> callVisitor(visitor, memoryModuleType, slot));
   }

   private static <U> void callVisitor(final Visitor visitor, final MemoryModuleType<U> memoryModuleType, final MemorySlot<?> slot) {
      slot.visit(memoryModuleType, visitor);
   }

   public <U> boolean isMemoryValue(final MemoryModuleType<U> memoryType, final U value) {
      MemorySlot<U> slot = this.<U>getMemorySlotIfPresent(memoryType);
      return slot != null && Objects.equals(value, slot.value());
   }

   public boolean checkMemory(final MemoryModuleType<?> type, final MemoryStatus status) {
      MemorySlot<?> slot = this.getMemorySlotIfPresent(type);
      if (slot == null) {
         return false;
      } else {
         return status == MemoryStatus.REGISTERED || status == MemoryStatus.VALUE_PRESENT && slot.hasValue() || status == MemoryStatus.VALUE_ABSENT && !slot.hasValue();
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
            this.registerMemory(requiredMemory);
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
      this.memories.values().forEach(MemorySlot::tick);
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

   private static boolean isEmptyCollection(final Object object) {
      boolean var10000;
      if (object instanceof Collection<?> collection) {
         if (collection.isEmpty()) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
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
         return new Brain<E>(this.memoryTypes, this.sensorTypes, activities, packed.memories, body.getRandom());
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

   public interface Visitor {
      <U> void acceptEmpty(MemoryModuleType<U> type);

      <U> void accept(MemoryModuleType<U> type, U value);

      <U> void accept(MemoryModuleType<U> type, U value, long timeToLive);
   }
}
