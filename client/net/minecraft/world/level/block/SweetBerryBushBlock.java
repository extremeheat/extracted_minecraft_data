package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SweetBerryBushBlock extends BushBlock implements BonemealableBlock {
   public static final IntegerProperty AGE;
   private static final VoxelShape SAPLING_SHAPE;
   private static final VoxelShape MID_GROWTH_SHAPE;

   public SweetBerryBushBlock(Block.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(AGE, 0));
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(Items.SWEET_BERRIES);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if ((Integer)var1.getValue(AGE) == 0) {
         return SAPLING_SHAPE;
      } else {
         return (Integer)var1.getValue(AGE) < 3 ? MID_GROWTH_SHAPE : super.getShape(var1, var2, var3, var4);
      }
   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      super.tick(var1, var2, var3, var4);
      int var5 = (Integer)var1.getValue(AGE);
      if (var5 < 3 && var4.nextInt(5) == 0 && var2.getRawBrightness(var3.above(), 0) >= 9) {
         var2.setBlock(var3, (BlockState)var1.setValue(AGE, var5 + 1), 2);
      }

   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (var4 instanceof LivingEntity && var4.getType() != EntityType.FOX) {
         var4.makeStuckInBlock(var1, new Vec3(0.800000011920929D, 0.75D, 0.800000011920929D));
         if (!var2.isClientSide && (Integer)var1.getValue(AGE) > 0 && (var4.xOld != var4.x || var4.zOld != var4.z)) {
            double var5 = Math.abs(var4.x - var4.xOld);
            double var7 = Math.abs(var4.z - var4.zOld);
            if (var5 >= 0.003000000026077032D || var7 >= 0.003000000026077032D) {
               var4.hurt(DamageSource.SWEET_BERRY_BUSH, 1.0F);
            }
         }

      }
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      int var7 = (Integer)var1.getValue(AGE);
      boolean var8 = var7 == 3;
      if (!var8 && var4.getItemInHand(var5).getItem() == Items.BONE_MEAL) {
         return false;
      } else if (var7 > 1) {
         int var9 = 1 + var2.random.nextInt(2);
         popResource(var2, var3, new ItemStack(Items.SWEET_BERRIES, var9 + (var8 ? 1 : 0)));
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + var2.random.nextFloat() * 0.4F);
         var2.setBlock(var3, (BlockState)var1.setValue(AGE, 1), 2);
         return true;
      } else {
         return super.use(var1, var2, var3, var4, var5, var6);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(AGE);
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return (Integer)var3.getValue(AGE) < 3;
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(Level var1, Random var2, BlockPos var3, BlockState var4) {
      int var5 = Math.min(3, (Integer)var4.getValue(AGE) + 1);
      var1.setBlock(var3, (BlockState)var4.setValue(AGE, var5), 2);
   }

   static {
      AGE = BlockStateProperties.AGE_3;
      SAPLING_SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
      MID_GROWTH_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
   }
}
