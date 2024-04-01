package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.block.state.BlockState;

public class ColumnFeatureConfiguration implements FeatureConfiguration {
   public static final Codec<ColumnFeatureConfiguration> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               BlockState.CODEC.fieldOf("state").forGetter(var0x -> var0x.state),
               IntProvider.codec(0, 3).fieldOf("reach").forGetter(var0x -> var0x.reach),
               IntProvider.codec(1, 20).fieldOf("height").forGetter(var0x -> var0x.height)
            )
            .apply(var0, ColumnFeatureConfiguration::new)
   );
   private final IntProvider reach;
   private final IntProvider height;
   private final BlockState state;

   public ColumnFeatureConfiguration(BlockState var1, IntProvider var2, IntProvider var3) {
      super();
      this.reach = var2;
      this.height = var3;
      this.state = var1;
   }

   public IntProvider reach() {
      return this.reach;
   }

   public IntProvider height() {
      return this.height;
   }

   public BlockState state() {
      return this.state;
   }
}
