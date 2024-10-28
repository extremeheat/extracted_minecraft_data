package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ColorRGBA;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ColoredFallingBlock extends FallingBlock {
   public static final MapCodec<ColoredFallingBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(ColorRGBA.CODEC.fieldOf("falling_dust_color").forGetter((var0x) -> {
         return var0x.dustColor;
      }), propertiesCodec()).apply(var0, ColoredFallingBlock::new);
   });
   private final ColorRGBA dustColor;

   public MapCodec<ColoredFallingBlock> codec() {
      return CODEC;
   }

   public ColoredFallingBlock(ColorRGBA var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.dustColor = var1;
   }

   public int getDustColor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return this.dustColor.rgba();
   }
}
