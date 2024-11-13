package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperFullBlock extends Block implements WeatheringCopper {
   public static final MapCodec<WeatheringCopperFullBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge), propertiesCodec()).apply(var0, WeatheringCopperFullBlock::new));
   private final WeatheringCopper.WeatherState weatherState;

   public MapCodec<WeatheringCopperFullBlock> codec() {
      return CODEC;
   }

   public WeatheringCopperFullBlock(WeatheringCopper.WeatherState var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.weatherState = var1;
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.changeOverTime(var1, var2, var3, var4);
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return WeatheringCopper.getNext(var1.getBlock()).isPresent();
   }

   public WeatheringCopper.WeatherState getAge() {
      return this.weatherState;
   }

   // $FF: synthetic method
   public Enum getAge() {
      return this.getAge();
   }
}
