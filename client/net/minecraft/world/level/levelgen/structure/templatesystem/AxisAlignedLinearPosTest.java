package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class AxisAlignedLinearPosTest extends PosRuleTest {
   public static final MapCodec<AxisAlignedLinearPosTest> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.FLOAT.fieldOf("min_chance").orElse(0.0F).forGetter((var0x) -> {
         return var0x.minChance;
      }), Codec.FLOAT.fieldOf("max_chance").orElse(0.0F).forGetter((var0x) -> {
         return var0x.maxChance;
      }), Codec.INT.fieldOf("min_dist").orElse(0).forGetter((var0x) -> {
         return var0x.minDist;
      }), Codec.INT.fieldOf("max_dist").orElse(0).forGetter((var0x) -> {
         return var0x.maxDist;
      }), Direction.Axis.CODEC.fieldOf("axis").orElse(Direction.Axis.Y).forGetter((var0x) -> {
         return var0x.axis;
      })).apply(var0, AxisAlignedLinearPosTest::new);
   });
   private final float minChance;
   private final float maxChance;
   private final int minDist;
   private final int maxDist;
   private final Direction.Axis axis;

   public AxisAlignedLinearPosTest(float var1, float var2, int var3, int var4, Direction.Axis var5) {
      super();
      if (var3 >= var4) {
         throw new IllegalArgumentException("Invalid range: [" + var3 + "," + var4 + "]");
      } else {
         this.minChance = var1;
         this.maxChance = var2;
         this.minDist = var3;
         this.maxDist = var4;
         this.axis = var5;
      }
   }

   public boolean test(BlockPos var1, BlockPos var2, BlockPos var3, RandomSource var4) {
      Direction var5 = Direction.get(Direction.AxisDirection.POSITIVE, this.axis);
      float var6 = (float)Math.abs((var2.getX() - var3.getX()) * var5.getStepX());
      float var7 = (float)Math.abs((var2.getY() - var3.getY()) * var5.getStepY());
      float var8 = (float)Math.abs((var2.getZ() - var3.getZ()) * var5.getStepZ());
      int var9 = (int)(var6 + var7 + var8);
      float var10 = var4.nextFloat();
      return var10 <= Mth.clampedLerp(this.minChance, this.maxChance, Mth.inverseLerp((float)var9, (float)this.minDist, (float)this.maxDist));
   }

   protected PosRuleTestType<?> getType() {
      return PosRuleTestType.AXIS_ALIGNED_LINEAR_POS_TEST;
   }
}
