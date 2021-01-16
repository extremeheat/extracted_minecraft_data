package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class WitherRoseBlock extends FlowerBlock {
   public WitherRoseBlock(MobEffect var1, BlockBehaviour.Properties var2) {
      super(var1, 8, var2);
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      return super.mayPlaceOn(var1, var2, var3) || var1.is(Blocks.NETHERRACK) || var1.is(Blocks.SOUL_SAND) || var1.is(Blocks.SOUL_SOIL);
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide && var2.getDifficulty() != Difficulty.PEACEFUL) {
         if (var4 instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)var4;
            if (!var5.isInvulnerableTo(DamageSource.WITHER)) {
               var5.addEffect(new MobEffectInstance(MobEffects.WITHER, 40));
            }
         }

      }
   }
}
