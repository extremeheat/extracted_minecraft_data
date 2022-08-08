package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractCandleBlock extends Block {
   public static final int LIGHT_PER_CANDLE = 3;
   public static final BooleanProperty LIT;

   protected AbstractCandleBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected abstract Iterable<Vec3> getParticleOffsets(BlockState var1);

   public static boolean isLit(BlockState var0) {
      return var0.hasProperty(LIT) && (var0.is(BlockTags.CANDLES) || var0.is(BlockTags.CANDLE_CAKES)) && (Boolean)var0.getValue(LIT);
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      if (!var1.isClientSide && var4.isOnFire() && this.canBeLit(var2)) {
         setLit(var1, var2, var3.getBlockPos(), true);
      }

   }

   protected boolean canBeLit(BlockState var1) {
      return !(Boolean)var1.getValue(LIT);
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(LIT)) {
         this.getParticleOffsets(var1).forEach((var3x) -> {
            addParticlesAndSound(var2, var3x.add((double)var3.getX(), (double)var3.getY(), (double)var3.getZ()), var4);
         });
      }
   }

   private static void addParticlesAndSound(Level var0, Vec3 var1, RandomSource var2) {
      float var3 = var2.nextFloat();
      if (var3 < 0.3F) {
         var0.addParticle(ParticleTypes.SMOKE, var1.x, var1.y, var1.z, 0.0, 0.0, 0.0);
         if (var3 < 0.17F) {
            var0.playLocalSound(var1.x + 0.5, var1.y + 0.5, var1.z + 0.5, SoundEvents.CANDLE_AMBIENT, SoundSource.BLOCKS, 1.0F + var2.nextFloat(), var2.nextFloat() * 0.7F + 0.3F, false);
         }
      }

      var0.addParticle(ParticleTypes.SMALL_FLAME, var1.x, var1.y, var1.z, 0.0, 0.0, 0.0);
   }

   public static void extinguish(@Nullable Player var0, BlockState var1, LevelAccessor var2, BlockPos var3) {
      setLit(var2, var1, var3, false);
      if (var1.getBlock() instanceof AbstractCandleBlock) {
         ((AbstractCandleBlock)var1.getBlock()).getParticleOffsets(var1).forEach((var2x) -> {
            var2.addParticle(ParticleTypes.SMOKE, (double)var3.getX() + var2x.x(), (double)var3.getY() + var2x.y(), (double)var3.getZ() + var2x.z(), 0.0, 0.10000000149011612, 0.0);
         });
      }

      var2.playSound((Player)null, var3, SoundEvents.CANDLE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
      var2.gameEvent((Entity)var0, (GameEvent)GameEvent.BLOCK_CHANGE, (BlockPos)var3);
   }

   private static void setLit(LevelAccessor var0, BlockState var1, BlockPos var2, boolean var3) {
      var0.setBlock(var2, (BlockState)var1.setValue(LIT, var3), 11);
   }

   static {
      LIT = BlockStateProperties.LIT;
   }
}
