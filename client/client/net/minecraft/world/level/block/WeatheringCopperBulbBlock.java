package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperBulbBlock extends CopperBulbBlock implements WeatheringCopper {
   public static final MapCodec<WeatheringCopperBulbBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringCopperBulbBlock::getAge), propertiesCodec())
            .apply(var0, WeatheringCopperBulbBlock::new)
   );
   private final WeatheringCopper.WeatherState weatherState;

   @Override
   protected MapCodec<WeatheringCopperBulbBlock> codec() {
      return CODEC;
   }

   public WeatheringCopperBulbBlock(WeatheringCopper.WeatherState var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.weatherState = var1;
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.changeOverTime(var1, var2, var3, var4);
   }

   @Override
   protected boolean isRandomlyTicking(BlockState var1) {
      return WeatheringCopper.getNext(var1.getBlock()).isPresent();
   }

   public WeatheringCopper.WeatherState getAge() {
      return this.weatherState;
   }
}
