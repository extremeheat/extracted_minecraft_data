package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

public class RunOne<E extends LivingEntity> extends GateBehavior<E> {
   public RunOne(List<Pair<? extends BehaviorControl<? super E>, Integer>> var1) {
      this(ImmutableMap.of(), var1);
   }

   public RunOne(Map<MemoryModuleType<?>, MemoryStatus> var1, List<Pair<? extends BehaviorControl<? super E>, Integer>> var2) {
      super(var1, ImmutableSet.of(), GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE, var2);
   }
}
