package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class FireflyBushBlock extends VegetationBlock implements BonemealableBlock {
   private static final double FIREFLY_CHANCE_PER_TICK = 0.7;
   private static final double FIREFLY_HORIZONTAL_RANGE = 10.0;
   private static final double FIREFLY_VERTICAL_RANGE = 5.0;
   private static final int FIREFLY_SPAWN_MAX_BRIGHTNESS_LEVEL = 13;
   public static final MapCodec<FireflyBushBlock> CODEC = simpleCodec(FireflyBushBlock::new);

   public FireflyBushBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected MapCodec<? extends FireflyBushBlock> codec() {
      return CODEC;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var2.getMaxLocalRawBrightness(var3) <= 13) {
         if (var4.nextDouble() <= 0.7) {
            double var5 = (double)var3.getX() + var4.nextDouble() * 10.0 - 5.0;
            double var7 = (double)var3.getY() + var4.nextDouble() * 5.0;
            double var9 = (double)var3.getZ() + var4.nextDouble() * 10.0 - 5.0;
            var2.addParticle(ParticleTypes.FIREFLY, var5, var7, var9, 0.0, 0.0, 0.0);
         }

      }
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return true;
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      popResource(var1, var3, new ItemStack(this));
   }
}
