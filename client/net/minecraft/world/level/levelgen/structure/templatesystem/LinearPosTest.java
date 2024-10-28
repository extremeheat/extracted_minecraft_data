package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class LinearPosTest extends PosRuleTest {
   public static final MapCodec<LinearPosTest> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((var0x) -> {
         return var0x.minChance;
      }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((var0x) -> {
         return var0x.maxChance;
      }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((var0x) -> {
         return var0x.minDist;
      }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((var0x) -> {
         return var0x.maxDist;
      })).apply(var0, LinearPosTest::new);
   });
   private final float minChance;
   private final float maxChance;
   private final int minDist;
   private final int maxDist;

   public LinearPosTest(float var1, float var2, int var3, int var4) {
      super();
      if (var3 >= var4) {
         throw new IllegalArgumentException("Invalid range: [" + var3 + "," + var4 + "]");
      } else {
         this.minChance = var1;
         this.maxChance = var2;
         this.minDist = var3;
         this.maxDist = var4;
      }
   }

   public boolean test(BlockPos var1, BlockPos var2, BlockPos var3, RandomSource var4) {
      int var5 = var2.distManhattan(var3);
      float var6 = var4.nextFloat();
      return var6 <= Mth.clampedLerp(this.minChance, this.maxChance, Mth.inverseLerp((float)var5, (float)this.minDist, (float)this.maxDist));
   }

   protected PosRuleTestType<?> getType() {
      return PosRuleTestType.LINEAR_POS_TEST;
   }
}
