package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class RandomBlockStateMatchTest extends RuleTest {
   private final BlockState blockState;
   private final float probability;

   public RandomBlockStateMatchTest(BlockState var1, float var2) {
      this.blockState = var1;
      this.probability = var2;
   }

   public RandomBlockStateMatchTest(Dynamic var1) {
      this(BlockState.deserialize(var1.get("blockstate").orElseEmptyMap()), var1.get("probability").asFloat(1.0F));
   }

   public boolean test(BlockState var1, Random var2) {
      return var1 == this.blockState && var2.nextFloat() < this.probability;
   }

   protected RuleTestType getType() {
      return RuleTestType.RANDOM_BLOCKSTATE_TEST;
   }

   protected Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("blockstate"), BlockState.serialize(var1, this.blockState).getValue(), var1.createString("probability"), var1.createFloat(this.probability))));
   }
}
