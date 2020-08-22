package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.world.level.block.state.BlockState;

public class AlwaysTrueTest extends RuleTest {
   public static final AlwaysTrueTest INSTANCE = new AlwaysTrueTest();

   private AlwaysTrueTest() {
   }

   public boolean test(BlockState var1, Random var2) {
      return true;
   }

   protected RuleTestType getType() {
      return RuleTestType.ALWAYS_TRUE_TEST;
   }

   protected Dynamic getDynamic(DynamicOps var1) {
      return new Dynamic(var1, var1.emptyMap());
   }
}
