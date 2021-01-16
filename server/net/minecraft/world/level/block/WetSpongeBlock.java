package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WetSpongeBlock extends Block {
   protected WetSpongeBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var2.dimensionType().ultraWarm()) {
         var2.setBlock(var3, Blocks.SPONGE.defaultBlockState(), 3);
         var2.levelEvent(2009, var3, 0);
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, (1.0F + var2.getRandom().nextFloat() * 0.2F) * 0.7F);
      }

   }
}
