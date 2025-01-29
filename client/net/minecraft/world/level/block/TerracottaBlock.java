package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.sounds.AmbientDesertBlockSoundsPlayer;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class TerracottaBlock extends Block {
   public static final MapCodec<TerracottaBlock> CODEC = simpleCodec(TerracottaBlock::new);

   public MapCodec<TerracottaBlock> codec() {
      return CODEC;
   }

   public TerracottaBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      AmbientDesertBlockSoundsPlayer.playAmbientBlockSounds(var1, var2, var3, var4);
   }
}
