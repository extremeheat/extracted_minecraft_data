package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PotatoPortalBlock extends Block {
   public static final MapCodec<PotatoPortalBlock> CODEC = simpleCodec(PotatoPortalBlock::new);
   protected static final VoxelShape FULL_AXIS_AABB = Block.box(3.0, 0.0, 3.0, 13.0, 24.0, 13.0);

   @Override
   public MapCodec<PotatoPortalBlock> codec() {
      return CODEC;
   }

   public PotatoPortalBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return FULL_AXIS_AABB;
   }

   @Override
   protected boolean canBeReplaced(BlockState var1, Fluid var2) {
      return false;
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4.canChangeDimensions()) {
         var4.handleInsidePortal(var3);
      }
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(100) == 0) {
         var2.playLocalSound(
            (double)var3.getX() + 0.5,
            (double)var3.getY() + 0.5,
            (double)var3.getZ() + 0.5,
            SoundEvents.PLAGUEWHALE_AMBIENT,
            SoundSource.BLOCKS,
            0.5F,
            var4.nextFloat() * 0.4F + 0.8F,
            false
         );
      }

      for(int var5 = 0; var5 < 4; ++var5) {
         double var6 = (double)var3.getX() + var4.nextDouble();
         double var8 = (double)var3.getY() + var4.nextDouble();
         double var10 = (double)var3.getZ() + var4.nextDouble();
         double var12 = ((double)var4.nextFloat() - 0.5) * 0.5;
         double var14 = ((double)var4.nextFloat() - 0.5) * 0.5;
         double var16 = ((double)var4.nextFloat() - 0.5) * 0.5;
         int var18 = var4.nextInt(2) * 2 - 1;
         if (!var2.getBlockState(var3.west()).is(this) && !var2.getBlockState(var3.east()).is(this)) {
            var6 = (double)var3.getX() + 0.5 + 0.25 * (double)var18;
            var12 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         } else {
            var10 = (double)var3.getZ() + 0.5 + 0.25 * (double)var18;
            var16 = (double)(var4.nextFloat() * 2.0F * (float)var18);
         }

         var2.addParticle(ParticleTypes.POTATO_LIGHTNING, var6, var8, var10, var12, var14, var16);
         var2.addParticle(ParticleTypes.REVERSE_POTATO_LIGHTNING, var6, var8, var10, var12, var14, var16);
      }
   }

   @Override
   public ItemStack getCloneItemStack(LevelReader var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }
}
