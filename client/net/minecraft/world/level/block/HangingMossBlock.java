package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HangingMossBlock extends Block implements BonemealableBlock {
   public static final MapCodec<HangingMossBlock> CODEC = simpleCodec(HangingMossBlock::new);
   private static final int SIDE_PADDING = 1;
   private static final VoxelShape TIP_SHAPE = Block.box(1.0, 2.0, 1.0, 15.0, 16.0, 15.0);
   private static final VoxelShape BASE_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
   public static final BooleanProperty TIP = BlockStateProperties.TIP;

   @Override
   public MapCodec<HangingMossBlock> codec() {
      return CODEC;
   }

   public HangingMossBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(this.stateDefinition.any().setValue(TIP, Boolean.valueOf(true)));
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return var1.getValue(TIP) ? TIP_SHAPE : BASE_SHAPE;
   }

   @Override
   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      if (var4.nextInt(500) == 0) {
         BlockState var5 = var2.getBlockState(var3.above());
         if (var5.is(Blocks.PALE_OAK_LOG) || var5.is(Blocks.PALE_OAK_LEAVES)) {
            var2.playLocalSound(
               (double)var3.getX(), (double)var3.getY(), (double)var3.getZ(), SoundEvents.PALE_HANGING_MOSS_IDLE, SoundSource.BLOCKS, 1.0F, 1.0F, false
            );
         }
      }
   }

   @Override
   protected boolean propagatesSkylightDown(BlockState var1) {
      return true;
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return this.canStayAtPosition(var2, var3);
   }

   private boolean canStayAtPosition(BlockGetter var1, BlockPos var2) {
      BlockPos var3 = var2.relative(Direction.UP);
      BlockState var4 = var1.getBlockState(var3);
      return MultifaceBlock.canAttachTo(var1, Direction.UP, var3, var4) || var4.is(Blocks.PALE_HANGING_MOSS);
   }

   @Override
   protected BlockState updateShape(
      BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8
   ) {
      return !this.canStayAtPosition(var2, var4)
         ? Blocks.AIR.defaultBlockState()
         : var1.setValue(TIP, Boolean.valueOf(!var2.getBlockState(var4.below()).is(this)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(TIP);
   }

   @Override
   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return this.canGrowInto(var1.getBlockState(this.getTip(var1, var2).below()));
   }

   private boolean canGrowInto(BlockState var1) {
      return var1.isAir();
   }

   public BlockPos getTip(BlockGetter var1, BlockPos var2) {
      BlockPos.MutableBlockPos var3 = var2.mutable();

      BlockState var4;
      do {
         var3.move(Direction.DOWN);
         var4 = var1.getBlockState(var3);
      } while (var4.is(this));

      return var3.relative(Direction.UP).immutable();
   }

   @Override
   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return true;
   }

   @Override
   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockPos var5 = this.getTip(var1, var3).below();
      if (this.canGrowInto(var1.getBlockState(var5))) {
         var1.setBlockAndUpdate(var5, var4.setValue(TIP, Boolean.valueOf(true)));
      }
   }
}
