package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class WeatheringCopperDoorBlock extends DoorBlock implements WeatheringCopper {
   public static final MapCodec<WeatheringCopperDoorBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BlockSetType.CODEC.fieldOf("block_set_type").forGetter(DoorBlock::type), WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(WeatheringCopperDoorBlock::getAge), propertiesCodec()).apply(var0, WeatheringCopperDoorBlock::new);
   });
   private final WeatheringCopper.WeatherState weatherState;

   public MapCodec<WeatheringCopperDoorBlock> codec() {
      return CODEC;
   }

   protected WeatheringCopperDoorBlock(BlockSetType var1, WeatheringCopper.WeatherState var2, BlockBehaviour.Properties var3) {
      super(var1, var3);
      this.weatherState = var2;
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var1.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
         this.changeOverTime(var1, var2, var3, var4);
      }

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
