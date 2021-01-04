package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class TntBlock extends Block {
   public static final BooleanProperty UNSTABLE;

   public TntBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(UNSTABLE, false));
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var4.getBlock() != var1.getBlock()) {
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
         PrimedTnt var4 = new PrimedTnt(var1, (double)((float)var2.getX() + 0.5F), (double)var2.getY(), (double)((float)var2.getZ() + 0.5F), var3.getSourceMob());
         var4.setFuse((short)(var1.random.nextInt(var4.getLife() / 4) + var4.getLife() / 8));
         var1.addFreshEntity(var4);
      }
   }

   public static void explode(Level var0, BlockPos var1) {
      explode(var0, var1, (LivingEntity)null);
   }

   private static void explode(Level var0, BlockPos var1, @Nullable LivingEntity var2) {
      if (!var0.isClientSide) {
         PrimedTnt var3 = new PrimedTnt(var0, (double)((float)var1.getX() + 0.5F), (double)var1.getY(), (double)((float)var1.getZ() + 0.5F), var2);
         var0.addFreshEntity(var3);
         var0.playSound((Player)null, var3.x, var3.y, var3.z, SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
      }
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      ItemStack var7 = var4.getItemInHand(var5);
      Item var8 = var7.getItem();
      if (var8 != Items.FLINT_AND_STEEL && var8 != Items.FIRE_CHARGE) {
         return super.use(var1, var2, var3, var4, var5, var6);
      } else {
         explode(var2, var3, var4);
         var2.setBlock(var3, Blocks.AIR.defaultBlockState(), 11);
         if (var8 == Items.FLINT_AND_STEEL) {
            var7.hurtAndBreak(1, var4, (var1x) -> {
               var1x.broadcastBreakEvent(var5);
            });
         } else {
            var7.shrink(1);
         }

         return true;
      }
   }

   public void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Entity var4) {
      if (!var1.isClientSide && var4 instanceof AbstractArrow) {
         AbstractArrow var5 = (AbstractArrow)var4;
         Entity var6 = var5.getOwner();
         if (var5.isOnFire()) {
            BlockPos var7 = var3.getBlockPos();
            explode(var1, var7, var6 instanceof LivingEntity ? (LivingEntity)var6 : null);
            var1.removeBlock(var7, false);
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
