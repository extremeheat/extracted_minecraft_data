package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BambooLeaves;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BambooSaplingBlock extends Block implements BonemealableBlock {
   protected static final VoxelShape SAPLING_SHAPE = Block.box(4.0D, 0.0D, 4.0D, 12.0D, 12.0D, 12.0D);

   public BambooSaplingBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public BlockBehaviour.OffsetType getOffsetType() {
      return BlockBehaviour.OffsetType.XZ;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Vec3 var5 = var1.getOffset(var2, var3);
      return SAPLING_SHAPE.move(var5.x, var5.y, var5.z);
   }

   public void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (var4.nextInt(3) == 0 && var2.isEmptyBlock(var3.above()) && var2.getRawBrightness(var3.above(), 0) >= 9) {
         this.growBamboo(var2, var3);
      }

   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.below()).is(BlockTags.BAMBOO_PLANTABLE_ON);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (!var1.canSurvive(var4, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         if (var2 == Direction.UP && var3.is(Blocks.BAMBOO)) {
            var4.setBlock(var5, Blocks.BAMBOO.defaultBlockState(), 2);
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return new ItemStack(Items.BAMBOO);
   }

   public boolean isValidBonemealTarget(BlockGetter var1, BlockPos var2, BlockState var3, boolean var4) {
      return var1.getBlockState(var2.above()).isAir();
   }

   public boolean isBonemealSuccess(Level var1, Random var2, BlockPos var3, BlockState var4) {
      return true;
   }

   public void performBonemeal(ServerLevel var1, Random var2, BlockPos var3, BlockState var4) {
      this.growBamboo(var1, var3);
   }

   public float getDestroyProgress(BlockState var1, Player var2, BlockGetter var3, BlockPos var4) {
      return var2.getMainHandItem().getItem() instanceof SwordItem ? 1.0F : super.getDestroyProgress(var1, var2, var3, var4);
   }

   protected void growBamboo(Level var1, BlockPos var2) {
      var1.setBlock(var2.above(), (BlockState)Blocks.BAMBOO.defaultBlockState().setValue(BambooBlock.LEAVES, BambooLeaves.SMALL), 3);
   }
}
