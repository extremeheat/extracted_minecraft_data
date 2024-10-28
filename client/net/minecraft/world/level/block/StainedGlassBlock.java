package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class StainedGlassBlock extends TransparentBlock implements BeaconBeamBlock {
   public static final MapCodec<StainedGlassBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(DyeColor.CODEC.fieldOf("color").forGetter(StainedGlassBlock::getColor), propertiesCodec()).apply(var0, StainedGlassBlock::new);
   });
   private final DyeColor color;

   public MapCodec<StainedGlassBlock> codec() {
      return CODEC;
   }

   public StainedGlassBlock(DyeColor var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.color = var1;
   }

   public DyeColor getColor() {
      return this.color;
   }
}
