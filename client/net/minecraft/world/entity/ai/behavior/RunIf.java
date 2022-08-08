package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunIf<E extends LivingEntity> extends Behavior<E> {
   private final Predicate<E> predicate;
   private final Behavior<? super E> wrappedBehavior;
   private final boolean checkWhileRunningAlso;

   public RunIf(Map<MemoryModuleType<?>, MemoryStatus> var1, Predicate<E> var2, Behavior<? super E> var3, boolean var4) {
      super(mergeMaps(var1, var3.entryCondition));
      this.predicate = var2;
      this.wrappedBehavior = var3;
      this.checkWhileRunningAlso = var4;
   }

   private static Map<MemoryModuleType<?>, MemoryStatus> mergeMaps(Map<MemoryModuleType<?>, MemoryStatus> var0, Map<MemoryModuleType<?>, MemoryStatus> var1) {
      HashMap var2 = Maps.newHashMap();
      var2.putAll(var0);
      var2.putAll(var1);
      return var2;
   }

   public RunIf(Predicate<E> var1, Behavior<? super E> var2, boolean var3) {
      this(ImmutableMap.of(), var1, var2, var3);
   }

   public RunIf(Predicate<E> var1, Behavior<? super E> var2) {
      this(ImmutableMap.of(), var1, var2, false);
   }

   public RunIf(Map<MemoryModuleType<?>, MemoryStatus> var1, Behavior<? super E> var2) {
      this(var1, (var0) -> {
         return true;
      }, var2, false);
   }

   protected boolean checkExtraStartConditions(ServerLevel var1, E var2) {
      return this.predicate.test(var2) && this.wrappedBehavior.checkExtraStartConditions(var1, var2);
   }

   protected boolean canStillUse(ServerLevel var1, E var2, long var3) {
      return this.checkWhileRunningAlso && this.predicate.test(var2) && this.wrappedBehavior.canStillUse(var1, var2, var3);
   }

   protected boolean timedOut(long var1) {
      return false;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      this.wrappedBehavior.start(var1, var2, var3);
   }

   protected void tick(ServerLevel var1, E var2, long var3) {
      this.wrappedBehavior.tick(var1, var2, var3);
   }

   protected void stop(ServerLevel var1, E var2, long var3) {
      this.wrappedBehavior.stop(var1, var2, var3);
   }

   public String toString() {
      return "RunIf: " + this.wrappedBehavior;
   }
}
