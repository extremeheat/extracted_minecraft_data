package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ColorRGBA;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.sounds.AmbientDesertBlockSoundsPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SandBlock extends ColoredFallingBlock {
   public static final MapCodec<SandBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ColorRGBA.CODEC.fieldOf("falling_dust_color").forGetter((var0x) -> var0x.dustColor), propertiesCodec()).apply(var0, SandBlock::new));

   public MapCodec<SandBlock> codec() {
      return CODEC;
   }

   public SandBlock(ColorRGBA var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      super.animateTick(var1, var2, var3, var4);
      AmbientDesertBlockSoundsPlayer.playAmbientSandSounds(var2, var3, var4);
   }
}
