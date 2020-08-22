package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateMatchTest extends RuleTest {
   private final BlockState blockState;

   public BlockStateMatchTest(BlockState var1) {
      this.blockState = var1;
   }

   public BlockStateMatchTest(Dynamic var1) {
      this(BlockState.deserialize(var1.get("blockstate").orElseEmptyMap()));
   }

   public boolean test(BlockState var1, Random var2) {
      return var1 == this.blockState;
   }

   protected RuleTestType getType() {
      return RuleTestType.BLOCKSTATE_TEST;
   }

   protected Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("blockstate"), BlockState.serialize(var1, this.blockState).getValue())));
   }
}
