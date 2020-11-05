package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractCandleBlock extends Block {
   public static final BooleanProperty LIT;

   protected AbstractCandleBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected abstract Iterable<Vec3> getParticleOffsets(BlockState var1);

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      if (!var1.isClientSide && var4.isOnFire() && !(Boolean)var2.getValue(LIT)) {
         setLit(var1, var2, var3.getBlockPos(), true);
      }

   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if ((Boolean)var1.getValue(LIT)) {
         this.getParticleOffsets(var1).forEach((var3x) -> {
            addParticlesAndSound(var2, var3x.add((double)var3.getX(), (double)var3.getY(), (double)var3.getZ()), var4);
         });
      }
   }

   private static void addParticlesAndSound(Level var0, Vec3 var1, Random var2) {
      float var3 = var2.nextFloat();
      if (var3 < 0.3F) {
         var0.addParticle(ParticleTypes.SMOKE, var1.x, var1.y, var1.z, 0.0D, 0.0D, 0.0D);
         if (var3 < 0.17F) {
            var0.playLocalSound(var1.x + 0.5D, var1.y + 0.5D, var1.z + 0.5D, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + var2.nextFloat(), var2.nextFloat() * 0.7F + 0.3F, false);
         }
      }

      var0.addParticle(ParticleTypes.SMALL_FLAME, var1.x, var1.y, var1.z, 0.0D, 0.0D, 0.0D);
   }

   protected static void extinguish(BlockState var0, LevelAccessor var1, BlockPos var2) {
      setLit(var1, var0, var2, false);
      var1.addParticle(ParticleTypes.SMOKE, (double)var2.getX(), (double)var2.getY(), (double)var2.getZ(), 0.0D, 0.10000000149011612D, 0.0D);
      var1.playSound((Player)null, var2, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   private static void setLit(LevelAccessor var0, BlockState var1, BlockPos var2, boolean var3) {
      var0.setBlock(var2, (BlockState)var1.setValue(LIT, var3), 11);
   }

   static {
      LIT = BlockStateProperties.LIT;
   }
}
