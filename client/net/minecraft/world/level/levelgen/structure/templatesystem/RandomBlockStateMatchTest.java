package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class RandomBlockStateMatchTest extends RuleTest {
   public static final MapCodec<RandomBlockStateMatchTest> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               BlockState.CODEC.fieldOf("block_state").forGetter(var0x -> var0x.blockState),
               Codec.FLOAT.fieldOf("probability").forGetter(var0x -> var0x.probability)
            )
            .apply(var0, RandomBlockStateMatchTest::new)
   );
   private final BlockState blockState;
   private final float probability;

   public RandomBlockStateMatchTest(BlockState var1, float var2) {
      super();
      this.blockState = var1;
      this.probability = var2;
   }

   @Override
   public boolean test(BlockState var1, RandomSource var2) {
      return var1 == this.blockState && var2.nextFloat() < this.probability;
   }

   @Override
   protected RuleTestType<?> getType() {
      return RuleTestType.RANDOM_BLOCKSTATE_TEST;
   }
}
