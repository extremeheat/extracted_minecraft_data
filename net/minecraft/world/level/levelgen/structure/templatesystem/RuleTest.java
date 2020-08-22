package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.state.BlockState;

public abstract class RuleTest {
   public abstract boolean test(BlockState var1, Random var2);

   public Dynamic serialize(DynamicOps var1) {
      return new Dynamic(var1, var1.mergeInto(this.getDynamic(var1).getValue(), var1.createString("predicate_type"), var1.createString(Registry.RULE_TEST.getKey(this.getType()).toString())));
   }

   protected abstract RuleTestType getType();

   protected abstract Dynamic getDynamic(DynamicOps var1);
}
