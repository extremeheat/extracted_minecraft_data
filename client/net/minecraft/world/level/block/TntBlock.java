package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
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
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;

public class TntBlock extends Block {
   public static final MapCodec<TntBlock> CODEC = simpleCodec(TntBlock::new);
   public static final BooleanProperty UNSTABLE = BlockStateProperties.UNSTABLE;

   @Override
   public MapCodec<TntBlock> codec() {
      return CODEC;
   }

   public TntBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.defaultBlockState().setValue(UNSTABLE, Boolean.valueOf(false)));
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         if (var2.hasNeighborSignal(var3)) {
            explode(var2, var3);
            var2.removeBlock(var3, false);
         }
      }
   }

   @Override
   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, @Nullable Orientation var5, boolean var6) {
      if (var2.hasNeighborSignal(var3)) {
         explode(var2, var3);
         var2.removeBlock(var3, false);
      }
   }

   @Override
   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide() && !var4.isCreative() && var3.getValue(UNSTABLE)) {
         explode(var1, var2);
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   @Override
   public void wasExploded(ServerLevel var1, BlockPos var2, Explosion var3) {
      PrimedTnt var4 = new PrimedTnt(var1, (double)var2.getX() + 0.5, (double)var2.getY(), (double)var2.getZ() + 0.5, var3.getIndirectSourceEntity());
      int var5 = var4.getFuse();
      var4.setFuse((short)(var1.random.nextInt(var5 / 4) + var5 / 8));
      var1.addFreshEntity(var4);
   }

   public static void explode(Level var0, BlockPos var1) {
      explode(var0, var1, null);
   }

   private static void explode(Level var0, BlockPos var1, @Nullable LivingEntity var2) {
      if (!var0.isClientSide) {
         PrimedTnt var3 = new PrimedTnt(var0, (double)var1.getX() + 0.5, (double)var1.getY(), (double)var1.getZ() + 0.5, var2);
         var0.addFreshEntity(var3);
         var0.playSound(null, var3.getX(), var3.getY(), var3.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
         var0.gameEvent(var2, GameEvent.PRIME_FUSE, var1);
      }
   }

   @Override
   protected InteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (!var1.is(Items.FLINT_AND_STEEL) && !var1.is(Items.FIRE_CHARGE)) {
         return super.useItemOn(var1, var2, var3, var4, var5, var6, var7);
      } else {
         explode(var3, var4, var5);
         var3.setBlock(var4, Blocks.AIR.defaultBlockState(), 11);
         Item var8 = var1.getItem();
         if (var1.is(Items.FLINT_AND_STEEL)) {
            var1.hurtAndBreak(1, var5, LivingEntity.getSlotForHand(var6));
         } else {
            var1.consume(1, var5);
         }

         var5.awardStat(Stats.ITEM_USED.get(var8));
         return InteractionResult.SUCCESS;
      }
   }

   @Override
   protected void onProjectileHit(Level var1, BlockState var2, BlockHitResult var3, Projectile var4) {
      if (var1 instanceof ServerLevel var5) {
         BlockPos var6 = var3.getBlockPos();
         Entity var7 = var4.getOwner();
         if (var4.isOnFire() && var4.mayInteract(var5, var6)) {
            explode(var1, var6, var7 instanceof LivingEntity ? (LivingEntity)var7 : null);
            var1.removeBlock(var6, false);
         }
      }
   }

   @Override
   public boolean dropFromExplosion(Explosion var1) {
      return false;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(UNSTABLE);
   }
}
