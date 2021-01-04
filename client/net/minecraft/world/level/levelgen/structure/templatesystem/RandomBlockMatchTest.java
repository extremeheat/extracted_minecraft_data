package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RandomBlockMatchTest extends RuleTest {
   private final Block block;
   private final float probability;

   public RandomBlockMatchTest(Block var1, float var2) {
      super();
      this.block = var1;
      this.probability = var2;
   }

   public <T> RandomBlockMatchTest(Dynamic<T> var1) {
      this((Block)Registry.BLOCK.get(new ResourceLocation(var1.get("block").asString(""))), var1.get("probability").asFloat(1.0F));
   }

   public boolean test(BlockState var1, Random var2) {
      return var1.getBlock() == this.block && var2.nextFloat() < this.probability;
   }

   protected RuleTestType getType() {
      return RuleTestType.RANDOM_BLOCK_TEST;
   }

   protected <T> Dynamic<T> getDynamic(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("block"), var1.createString(Registry.BLOCK.getKey(this.block).toString()), var1.createString("probability"), var1.createFloat(this.probability))));
   }
}
