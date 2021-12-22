package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CarverDebugSettings {
   public static final CarverDebugSettings DEFAULT;
   public static final Codec<CarverDebugSettings> CODEC;
   private boolean debugMode;
   private final BlockState airState;
   private final BlockState waterState;
   private final BlockState lavaState;
   private final BlockState barrierState;

   // $FF: renamed from: of (boolean, net.minecraft.world.level.block.state.BlockState, net.minecraft.world.level.block.state.BlockState, net.minecraft.world.level.block.state.BlockState, net.minecraft.world.level.block.state.BlockState) net.minecraft.world.level.levelgen.carver.CarverDebugSettings
   public static CarverDebugSettings method_133(boolean var0, BlockState var1, BlockState var2, BlockState var3, BlockState var4) {
      return new CarverDebugSettings(var0, var1, var2, var3, var4);
   }

   // $FF: renamed from: of (net.minecraft.world.level.block.state.BlockState, net.minecraft.world.level.block.state.BlockState, net.minecraft.world.level.block.state.BlockState, net.minecraft.world.level.block.state.BlockState) net.minecraft.world.level.levelgen.carver.CarverDebugSettings
   public static CarverDebugSettings method_134(BlockState var0, BlockState var1, BlockState var2, BlockState var3) {
      return new CarverDebugSettings(false, var0, var1, var2, var3);
   }

   // $FF: renamed from: of (boolean, net.minecraft.world.level.block.state.BlockState) net.minecraft.world.level.levelgen.carver.CarverDebugSettings
   public static CarverDebugSettings method_135(boolean var0, BlockState var1) {
      return new CarverDebugSettings(var0, var1, DEFAULT.getWaterState(), DEFAULT.getLavaState(), DEFAULT.getBarrierState());
   }

   private CarverDebugSettings(boolean var1, BlockState var2, BlockState var3, BlockState var4, BlockState var5) {
      super();
      this.debugMode = var1;
      this.airState = var2;
      this.waterState = var3;
      this.lavaState = var4;
      this.barrierState = var5;
   }

   public boolean isDebugMode() {
      return this.debugMode;
   }

   public BlockState getAirState() {
      return this.airState;
   }

   public BlockState getWaterState() {
      return this.waterState;
   }

   public BlockState getLavaState() {
      return this.lavaState;
   }

   public BlockState getBarrierState() {
      return this.barrierState;
   }

   static {
      DEFAULT = new CarverDebugSettings(false, Blocks.ACACIA_BUTTON.defaultBlockState(), Blocks.CANDLE.defaultBlockState(), Blocks.ORANGE_STAINED_GLASS.defaultBlockState(), Blocks.GLASS.defaultBlockState());
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.BOOL.optionalFieldOf("debug_mode", false).forGetter(CarverDebugSettings::isDebugMode), BlockState.CODEC.optionalFieldOf("air_state", DEFAULT.getAirState()).forGetter(CarverDebugSettings::getAirState), BlockState.CODEC.optionalFieldOf("water_state", DEFAULT.getAirState()).forGetter(CarverDebugSettings::getWaterState), BlockState.CODEC.optionalFieldOf("lava_state", DEFAULT.getAirState()).forGetter(CarverDebugSettings::getLavaState), BlockState.CODEC.optionalFieldOf("barrier_state", DEFAULT.getAirState()).forGetter(CarverDebugSettings::getBarrierState)).apply(var0, CarverDebugSettings::new);
      });
   }
}
