package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WitherRoseBlock extends FlowerBlock {
   public WitherRoseBlock(MobEffect var1, Block.Properties var2) {
      super(var1, 8, var2);
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      Block var4 = var1.getBlock();
      return super.mayPlaceOn(var1, var2, var3) || var4 == Blocks.NETHERRACK || var4 == Blocks.SOUL_SAND;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      VoxelShape var5 = this.getShape(var1, var2, var3, CollisionContext.empty());
      Vec3 var6 = var5.bounds().getCenter();
      double var7 = (double)var3.getX() + var6.x;
      double var9 = (double)var3.getZ() + var6.z;

      for(int var11 = 0; var11 < 3; ++var11) {
         if (var4.nextBoolean()) {
            var2.addParticle(ParticleTypes.SMOKE, var7 + (double)(var4.nextFloat() / 5.0F), (double)var3.getY() + (0.5D - (double)var4.nextFloat()), var9 + (double)(var4.nextFloat() / 5.0F), 0.0D, 0.0D, 0.0D);
         }
      }

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
