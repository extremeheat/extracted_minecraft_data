package net.minecraft.world.entity.livingblock.hurt;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.TntBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PrimeTnt implements OnHurt {
   public PrimeTnt() {
      super();
   }

   public void apply(final LivingBlock livingBlock, final ServerLevel level, final DamageSource source, final float damage, final boolean fatalDamage) {
      BlockState blockState = livingBlock.getBlockState();
      if (fatalDamage || source.typeHolder().is(DamageTypes.EXPLOSION) || source.typeHolder().is(DamageTypes.PLAYER_EXPLOSION) || (Boolean)blockState.getValue(TntBlock.UNSTABLE)) {
         Entity directEntity = source.getDirectEntity();
         PrimedTnt primed = new PrimedTnt(level, livingBlock.getX() + 0.5, livingBlock.getY(), livingBlock.getZ() + 0.5, directEntity != null ? directEntity.asLivingEntity() : null);
         int fuse = primed.getFuse();
         primed.setFuse((short)(level.getRandom().nextInt(fuse / 4) + fuse / 8));
         level.addFreshEntity(primed);
         livingBlock.discard();
      }

   }
}
