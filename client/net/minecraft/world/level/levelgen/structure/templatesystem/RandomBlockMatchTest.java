package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RandomBlockMatchTest extends RuleTest {
   public static final MapCodec<RandomBlockMatchTest> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").forGetter((var0x) -> {
         return var0x.block;
      }), Codec.FLOAT.fieldOf("probability").forGetter((var0x) -> {
         return var0x.probability;
      })).apply(var0, RandomBlockMatchTest::new);
   });
   private final Block block;
   private final float probability;

   public RandomBlockMatchTest(Block var1, float var2) {
      super();
      this.block = var1;
      this.probability = var2;
   }

   public boolean test(BlockState var1, RandomSource var2) {
      return var1.is(this.block) && var2.nextFloat() < this.probability;
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.RANDOM_BLOCK_TEST;
   }
}
