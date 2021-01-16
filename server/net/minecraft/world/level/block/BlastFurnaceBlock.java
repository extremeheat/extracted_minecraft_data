package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlastFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class BlastFurnaceBlock extends AbstractFurnaceBlock {
   protected BlastFurnaceBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public BlockEntity newBlockEntity(BlockGetter var1) {
      return new BlastFurnaceBlockEntity();
   }

   protected void openContainer(Level var1, BlockPos var2, Player var3) {
      BlockEntity var4 = var1.getBlockEntity(var2);
      if (var4 instanceof BlastFurnaceBlockEntity) {
         var3.openMenu((MenuProvider)var4);
         var3.awardStat(Stats.INTERACT_WITH_BLAST_FURNACE);
      }

   }
}
