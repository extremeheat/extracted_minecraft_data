package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class RedStoneOreBlock extends Block {
   public static final MapCodec<RedStoneOreBlock> CODEC = simpleCodec(RedStoneOreBlock::new);
   public static final BooleanProperty LIT;

   public MapCodec<RedStoneOreBlock> codec() {
      return CODEC;
   }

   public RedStoneOreBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(LIT, false));
   }

   protected void attack(BlockState var1, Level var2, BlockPos var3, Player var4) {
      interact(var1, var2, var3);
      super.attack(var1, var2, var3, var4);
   }

   public void stepOn(Level var1, BlockPos var2, BlockState var3, Entity var4) {
      if (!var4.isSteppingCarefully()) {
         interact(var3, var1, var2);
      }

      super.stepOn(var1, var2, var3, var4);
   }

   protected ItemInteractionResult useItemOn(ItemStack var1, BlockState var2, Level var3, BlockPos var4, Player var5, InteractionHand var6, BlockHitResult var7) {
      if (var3.isClientSide) {
         spawnParticles(var3, var4);
      } else {
         interact(var2, var3, var4);
      }

      return var1.getItem() instanceof BlockItem && (new BlockPlaceContext(var5, var6, var1, var7)).canPlace() ? ItemInteractionResult.SKIP_DEFAULT_BLOCK_INTERACTION : ItemInteractionResult.SUCCESS;
   }

   private static void interact(BlockState var0, Level var1, BlockPos var2) {
      spawnParticles(var1, var2);
      if (!(Boolean)var0.getValue(LIT)) {
         var1.setBlock(var2, (BlockState)var0.setValue(LIT, true), 3);
      }

   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return (Boolean)var1.getValue(LIT);
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(LIT)) {
         var2.setBlock(var3, (BlockState)var1.setValue(LIT, false), 3);
      }

   }

   protected void spawnAfterBreak(BlockState var1, ServerLevel var2, BlockPos var3, ItemStack var4, boolean var5) {
      super.spawnAfterBreak(var1, var2, var3, var4, var5);
      if (var5 && EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, var4) == 0) {
         int var6 = 1 + var2.random.nextInt(5);
         this.popExperience(var2, var3, var6);
      }

   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(LIT)) {
         spawnParticles(var2, var3);
      }

   }

   private static void spawnParticles(Level var0, BlockPos var1) {
      double var2 = 0.5625;
      RandomSource var4 = var0.random;
      Direction[] var5 = Direction.values();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Direction var8 = var5[var7];
         BlockPos var9 = var1.relative(var8);
         if (!var0.getBlockState(var9).isSolidRender(var0, var9)) {
            Direction.Axis var10 = var8.getAxis();
            double var11 = var10 == Direction.Axis.X ? 0.5 + 0.5625 * (double)var8.getStepX() : (double)var4.nextFloat();
            double var13 = var10 == Direction.Axis.Y ? 0.5 + 0.5625 * (double)var8.getStepY() : (double)var4.nextFloat();
            double var15 = var10 == Direction.Axis.Z ? 0.5 + 0.5625 * (double)var8.getStepZ() : (double)var4.nextFloat();
            var0.addParticle(DustParticleOptions.REDSTONE, (double)var1.getX() + var11, (double)var1.getY() + var13, (double)var1.getZ() + var15, 0.0, 0.0, 0.0);
         }
      }

   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LIT);
   }

   static {
      LIT = RedstoneTorchBlock.LIT;
   }
}
