package net.minecraft.world.entity.livingblock.hurt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class PlaceBlockOnDeath implements OnHurt {
   private final BlockState blockState;

   public PlaceBlockOnDeath(final Block block) {
      super();
      this.blockState = block.defaultBlockState();
   }

   public void apply(final LivingBlock livingBlock, final ServerLevel level, final DamageSource source, final float damage, final boolean fatalDamage) {
      if (fatalDamage) {
         level.setBlock(livingBlock.blockPosition(), this.blockState, 3);
      }

   }
}
