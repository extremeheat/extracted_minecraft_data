package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;

public class SimpleBlockConfiguration implements FeatureConfiguration {
   public static final Codec<SimpleBlockConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockState.CODEC.fieldOf("to_place").forGetter((var0x) -> {
         return var0x.toPlace;
      }), BlockState.CODEC.listOf().fieldOf("place_on").forGetter((var0x) -> {
         return var0x.placeOn;
      }), BlockState.CODEC.listOf().fieldOf("place_in").forGetter((var0x) -> {
         return var0x.placeIn;
      }), BlockState.CODEC.listOf().fieldOf("place_under").forGetter((var0x) -> {
         return var0x.placeUnder;
      })).apply(var0, SimpleBlockConfiguration::new);
   });
   public final BlockState toPlace;
   public final List<BlockState> placeOn;
   public final List<BlockState> placeIn;
   public final List<BlockState> placeUnder;

   public SimpleBlockConfiguration(BlockState var1, List<BlockState> var2, List<BlockState> var3, List<BlockState> var4) {
      super();
      this.toPlace = var1;
      this.placeOn = var2;
      this.placeIn = var3;
      this.placeUnder = var4;
   }
}
