package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RuleTest {
   public RuleTest() {
      super();
   }

   public abstract boolean test(BlockState var1, Random var2);

   public <T> Dynamic<T> serialize(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.mergeInto(this.getDynamic(var1).getValue(), var1.createString("predicate_type"), var1.createString(Registry.RULE_TEST.getKey(this.getType()).toString())));
   }

   protected abstract RuleTestType getType();

   protected abstract <T> Dynamic<T> getDynamic(DynamicOps<T> var1);
}
