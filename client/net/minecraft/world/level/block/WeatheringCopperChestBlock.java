package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

public class WeatheringCopperChestBlock extends CopperChestBlock implements WeatheringCopper {
   public static final MapCodec<WeatheringCopperChestBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(WeatheringCopper.WeatherState.CODEC.fieldOf("weathering_state").forGetter(CopperChestBlock::getState), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("open_sound").forGetter(ChestBlock::getOpenChestSound), BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("close_sound").forGetter(ChestBlock::getCloseChestSound), propertiesCodec()).apply(var0, WeatheringCopperChestBlock::new));

   public MapCodec<WeatheringCopperChestBlock> codec() {
      return CODEC;
   }

   public WeatheringCopperChestBlock(WeatheringCopper.WeatherState var1, SoundEvent var2, SoundEvent var3, BlockBehaviour.Properties var4) {
      super(var1, var2, var3, var4);
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return WeatheringCopper.getNext(var1.getBlock()).isPresent();
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!((ChestType)var1.getValue(ChestBlock.TYPE)).equals(ChestType.RIGHT)) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof ChestBlockEntity) {
            ChestBlockEntity var5 = (ChestBlockEntity)var6;
            if (var5.getEntitiesWithContainerOpen().isEmpty()) {
               this.changeOverTime(var1, var2, var3, var4);
            }
         }
      }

   }

   public WeatheringCopper.WeatherState getAge() {
      return this.getState();
   }

   public boolean isWaxed() {
      return false;
   }

   // $FF: synthetic method
   public Enum getAge() {
      return this.getAge();
   }
}
