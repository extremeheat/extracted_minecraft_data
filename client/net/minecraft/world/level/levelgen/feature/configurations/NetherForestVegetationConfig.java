package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public class NetherForestVegetationConfig extends BlockPileConfiguration {
   public static final Codec<NetherForestVegetationConfig> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockStateProvider.CODEC.fieldOf("state_provider").forGetter((var0x) -> {
         return var0x.stateProvider;
      }), ExtraCodecs.POSITIVE_INT.fieldOf("spread_width").forGetter((var0x) -> {
         return var0x.spreadWidth;
      }), ExtraCodecs.POSITIVE_INT.fieldOf("spread_height").forGetter((var0x) -> {
         return var0x.spreadHeight;
      })).apply(var0, NetherForestVegetationConfig::new);
   });
   public final int spreadWidth;
   public final int spreadHeight;

   public NetherForestVegetationConfig(BlockStateProvider var1, int var2, int var3) {
      super(var1);
      this.spreadWidth = var2;
      this.spreadHeight = var3;
   }
}
