package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WeatheringCopperStairBlock extends StairBlock implements WeatheringCopper {
   public static final MapCodec<WeatheringCopperStairBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(ChangeOverTimeBlock::getAge), BlockState.CODEC.fieldOf("base_state").forGetter((var0x) -> {
         return var0x.baseState;
      }), propertiesCodec()).apply(var0, WeatheringCopperStairBlock::new);
   });
   private final WeatheringCopper.WeatherState weatherState;

   public MapCodec<WeatheringCopperStairBlock> codec() {
      return CODEC;
   }

   public WeatheringCopperStairBlock(WeatheringCopper.WeatherState var1, BlockState var2, BlockBehaviour.Properties var3) {
      super(var2, var3);
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
