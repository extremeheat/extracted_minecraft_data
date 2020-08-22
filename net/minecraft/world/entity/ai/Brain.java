package net.minecraft.world.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Serializable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;

public class Brain implements Serializable {
   private final Map memories = Maps.newHashMap();
   private final Map sensors = Maps.newLinkedHashMap();
   private final Map availableGoalsByPriority = Maps.newTreeMap();
   private Schedule schedule;
   private final Map activityRequirements;
   private Set coreActivities;
   private final Set activeActivities;
   private Activity defaultActivity;
   private long lastScheduleUpdate;

   public Brain(Collection var1, Collection var2, Dynamic var3) {
      this.schedule = Schedule.EMPTY;
      this.activityRequirements = Maps.newHashMap();
      this.coreActivities = Sets.newHashSet();
      this.activeActivities = Sets.newHashSet();
      this.defaultActivity = Activity.IDLE;
      this.lastScheduleUpdate = -9999L;
      var1.forEach((var1x) -> {
         Optional var10000 = (Optional)this.memories.put(var1x, Optional.empty());
      });
      var2.forEach((var1x) -> {
         Sensor var10000 = (Sensor)this.sensors.put(var1x, var1x.create());
      });
      this.sensors.values().forEach((var1x) -> {
         Iterator var2 = var1x.requires().iterator();

         while(var2.hasNext()) {
            MemoryModuleType var3 = (MemoryModuleType)var2.next();
            this.memories.put(var3, Optional.empty());
         }

      });
      Iterator var4 = var3.get("memories").asMap(Function.identity(), Function.identity()).entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         this.readMemory((MemoryModuleType)Registry.MEMORY_MODULE_TYPE.get(new ResourceLocation(((Dynamic)var5.getKey()).asString(""))), (Dynamic)var5.getValue());
      }

   }

   public boolean hasMemoryValue(MemoryModuleType var1) {
      return this.checkMemory(var1, MemoryStatus.VALUE_PRESENT);
   }

   private void readMemory(MemoryModuleType var1, Dynamic var2) {
      this.setMemory(var1, ((Function)var1.getDeserializer().orElseThrow(RuntimeException::new)).apply(var2));
   }

   public void eraseMemory(MemoryModuleType var1) {
      this.setMemory(var1, Optional.empty());
   }

   public void setMemory(MemoryModuleType var1, @Nullable Object var2) {
      this.setMemory(var1, Optional.ofNullable(var2));
   }

   public void setMemory(MemoryModuleType var1, Optional var2) {
      if (this.memories.containsKey(var1)) {
         if (var2.isPresent() && this.isEmptyCollection(var2.get())) {
            this.eraseMemory(var1);
         } else {
            this.memories.put(var1, var2);
         }
      }

   }

   public Optional getMemory(MemoryModuleType var1) {
      return (Optional)this.memories.get(var1);
   }

   public boolean checkMemory(MemoryModuleType var1, MemoryStatus var2) {
      Optional var3 = (Optional)this.memories.get(var1);
      if (var3 == null) {
         return false;
      } else {
         return var2 == MemoryStatus.REGISTERED || var2 == MemoryStatus.VALUE_PRESENT && var3.isPresent() || var2 == MemoryStatus.VALUE_ABSENT && !var3.isPresent();
      }
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule var1) {
      this.schedule = var1;
   }

   public void setCoreActivities(Set var1) {
      this.coreActivities = var1;
   }

   @Deprecated
   public Stream getRunningBehaviorsStream() {
      return this.availableGoalsByPriority.values().stream().flatMap((var0) -> {
         return var0.values().stream();
      }).flatMap(Collection::stream).filter((var0) -> {
         return var0.getStatus() == Behavior.Status.RUNNING;
      });
   }

   public void setActivity(Activity var1) {
      this.activeActivities.clear();
      this.activeActivities.addAll(this.coreActivities);
      boolean var2 = this.activityRequirements.keySet().contains(var1) && this.activityRequirementsAreMet(var1);
      this.activeActivities.add(var2 ? var1 : this.defaultActivity);
   }

   public void updateActivity(long var1, long var3) {
      if (var3 - this.lastScheduleUpdate > 20L) {
         this.lastScheduleUpdate = var3;
         Activity var5 = this.getSchedule().getActivityAt((int)(var1 % 24000L));
         if (!this.activeActivities.contains(var5)) {
            this.setActivity(var5);
         }
      }

   }

   public void setDefaultActivity(Activity var1) {
      this.defaultActivity = var1;
   }

   public void addActivity(Activity var1, ImmutableList var2) {
      this.addActivity(var1, var2, ImmutableSet.of());
   }

   public void addActivity(Activity var1, ImmutableList var2, Set var3) {
      this.activityRequirements.put(var1, var3);
      var2.forEach((var2x) -> {
         ((Set)((Map)this.availableGoalsByPriority.computeIfAbsent(var2x.getFirst(), (var0) -> {
            return Maps.newHashMap();
         })).computeIfAbsent(var1, (var0) -> {
            return Sets.newLinkedHashSet();
         })).add(var2x.getSecond());
      });
   }

   public boolean isActive(Activity var1) {
      return this.activeActivities.contains(var1);
   }

   public Brain copyWithoutGoals() {
      Brain var1 = new Brain(this.memories.keySet(), this.sensors.keySet(), new Dynamic(NbtOps.INSTANCE, new CompoundTag()));
      this.memories.forEach((var1x, var2) -> {
         var2.ifPresent((var2x) -> {
            Optional var10000 = (Optional)var1.memories.put(var1x, Optional.of(var2x));
         });
      });
      return var1;
   }

   public void tick(ServerLevel var1, LivingEntity var2) {
      this.tickEachSensor(var1, var2);
      this.startEachNonRunningBehavior(var1, var2);
      this.tickEachRunningBehavior(var1, var2);
   }

   public void stopAll(ServerLevel var1, LivingEntity var2) {
      long var3 = var2.level.getGameTime();
      this.getRunningBehaviorsStream().forEach((var4) -> {
         var4.doStop(var1, var2, var3);
      });
   }

   public Object serialize(DynamicOps var1) {
      Object var2 = var1.createMap((Map)this.memories.entrySet().stream().filter((var0) -> {
         return ((MemoryModuleType)var0.getKey()).getDeserializer().isPresent() && ((Optional)var0.getValue()).isPresent();
      }).map((var1x) -> {
         return Pair.of(var1.createString(Registry.MEMORY_MODULE_TYPE.getKey(var1x.getKey()).toString()), ((Serializable)((Optional)var1x.getValue()).get()).serialize(var1));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return var1.createMap(ImmutableMap.of(var1.createString("memories"), var2));
   }

   private void tickEachSensor(ServerLevel var1, LivingEntity var2) {
      this.sensors.values().forEach((var2x) -> {
         var2x.tick(var1, var2);
      });
   }

   private void startEachNonRunningBehavior(ServerLevel var1, LivingEntity var2) {
      long var3 = var1.getGameTime();
      this.availableGoalsByPriority.values().stream().flatMap((var0) -> {
         return var0.entrySet().stream();
      }).filter((var1x) -> {
         return this.activeActivities.contains(var1x.getKey());
      }).map(Entry::getValue).flatMap(Collection::stream).filter((var0) -> {
         return var0.getStatus() == Behavior.Status.STOPPED;
      }).forEach((var4) -> {
         var4.tryStart(var1, var2, var3);
      });
   }

   private void tickEachRunningBehavior(ServerLevel var1, LivingEntity var2) {
      long var3 = var1.getGameTime();
      this.getRunningBehaviorsStream().forEach((var4) -> {
         var4.tickOrStop(var1, var2, var3);
      });
   }

   private boolean activityRequirementsAreMet(Activity var1) {
      return ((Set)this.activityRequirements.get(var1)).stream().allMatch((var1x) -> {
         MemoryModuleType var2 = (MemoryModuleType)var1x.getFirst();
         MemoryStatus var3 = (MemoryStatus)var1x.getSecond();
         return this.checkMemory(var2, var3);
      });
   }

   private boolean isEmptyCollection(Object var1) {
      return var1 instanceof Collection && ((Collection)var1).isEmpty();
   }
}
