package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class AmethystBlock extends Block {
   public static final MapCodec<AmethystBlock> CODEC = simpleCodec(AmethystBlock::new);

   public MapCodec<? extends AmethystBlock> codec() {
      return CODEC;
   }

   public AmethystBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      if (!var1.isClientSide) {
         BlockPos var5 = var3.getBlockPos();
         var1.playSound((Player)null, (BlockPos)var5, SoundEvents.AMETHYST_BLOCK_HIT, SoundSource.BLOCKS, 1.0F, 0.5F + var1.random.nextFloat() * 1.2F);
         var1.playSound((Player)null, (BlockPos)var5, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, 0.5F + var1.random.nextFloat() * 1.2F);
      }

   }
}
