package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class TntBlock extends Block {
   public static final BooleanProperty UNSTABLE;

   public TntBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(UNSTABLE, false));
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         if (var2.hasNeighborSignal(var3)) {
            explode(var2, var3);
            var2.removeBlock(var3, false);
         }

      }
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var2.hasNeighborSignal(var3)) {
         explode(var2, var3);
         var2.removeBlock(var3, false);
      }

   }

   public void playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide() && !var4.isCreative() && (Boolean)var3.getValue(UNSTABLE)) {
         explode(var1, var2);
      }

      super.playerWillDestroy(var1, var2, var3, var4);
   }

   public void wasExploded(Level var1, BlockPos var2, Explosion var3) {
      if (!var1.isClientSide) {
         PrimedTnt var4 = new PrimedTnt(var1, (double)var2.getX() + 0.5D, (double)var2.getY(), (double)var2.getZ() + 0.5D, var3.getSourceMob());
         var4.setFuse((short)(var1.random.nextInt(var4.getLife() / 4) + var4.getLife() / 8));
         var1.addFreshEntity(var4);
      }
   }

   public static void explode(Level var0, BlockPos var1) {
      explode(var0, var1, (LivingEntity)null);
   }

   private static void explode(Level var0, BlockPos var1, @Nullable LivingEntity var2) {
      if (!var0.isClientSide) {
         PrimedTnt var3 = new PrimedTnt(var0, (double)var1.getX() + 0.5D, (double)var1.getY(), (double)var1.getZ() + 0.5D, var2);
         var0.addFreshEntity(var3);
         var0.playSound((Player)null, var3.getX(), var3.getY(), var3.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      Item var8 = var7.getItem();
      if (var8 != Items.FLINT_AND_STEEL && var8 != Items.FIRE_CHARGE) {
         return super.use(var1, var2, var3, var4, var5, var6);
      } else {
         explode(var2, var3, var4);
         var2.setBlock(var3, Blocks.AIR.defaultBlockState(), 11);
         if (!var4.isCreative()) {
            if (var8 == Items.FLINT_AND_STEEL) {
               var7.hurtAndBreak(1, var4, (var1x) -> {
                  var1x.broadcastBreakEvent(var5);
               });
            } else {
               var7.shrink(1);
            }
         }

         return InteractionResult.sidedSuccess(var2.isClientSide);
      }
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      if (!var1.isClientSide) {
         Entity var5 = var4.getOwner();
         if (var4.isOnFire()) {
            BlockPos var6 = var3.getBlockPos();
            explode(var1, var6, var5 instanceof LivingEntity ? (LivingEntity)var5 : null);
            var1.removeBlock(var6, false);
         }
      }

   }

   public boolean dropFromExplosion(Explosion var1) {
      return false;
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UNSTABLE);
   }

   static {
      UNSTABLE = BlockStateProperties.UNSTABLE;
   }
}
