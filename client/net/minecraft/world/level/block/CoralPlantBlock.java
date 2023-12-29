package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CoralPlantBlock extends BaseCoralPlantTypeBlock {
   public static final MapCodec<CoralPlantBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(CoralBlock.DEAD_CORAL_FIELD.forGetter(var0x -> var0x.deadBlock), propertiesCodec()).apply(var0, CoralPlantBlock::new)
   );
   private final Block deadBlock;
   protected static final float AABB_OFFSET = 6.0F;
   protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 15.0, 14.0);

   @Override
   public MapCodec<CoralPlantBlock> codec() {
      return CODEC;
   }

   protected CoralPlantBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.deadBlock = var1;
   }

   @Override
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      this.tryScheduleDieTick(var1, var2, var3);
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (!scanForWater(var1, var2, var3)) {
         var2.setBlock(var3, this.deadBlock.defaultBlockState().setValue(WATERLOGGED, Boolean.valueOf(false)), 2);
      }
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN && !var1.canSurvive(var4, var5)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         this.tryScheduleDieTick(var1, var4, var5);
         if (var1.getValue(WATERLOGGED)) {
            var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
         }

         return super.updateShape(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }
}
