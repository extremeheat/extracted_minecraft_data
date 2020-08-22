package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;

public class RunOne extends GateBehavior {
   public RunOne(List var1) {
      this(ImmutableMap.of(), var1);
   }

   public RunOne(Map var1, List var2) {
      super(var1, ImmutableSet.of(), GateBehavior.OrderPolicy.SHUFFLED, GateBehavior.RunningPolicy.RUN_ONE, var2);
   }
}
