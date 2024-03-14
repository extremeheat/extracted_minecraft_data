package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallSignBlock extends SignBlock {
   public static final MapCodec<WallSignBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(WoodType.CODEC.fieldOf("wood_type").forGetter(SignBlock::type), propertiesCodec()).apply(var0, WallSignBlock::new)
   );
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   protected static final float AABB_THICKNESS = 2.0F;
   protected static final float AABB_BOTTOM = 4.5F;
   protected static final float AABB_TOP = 12.5F;
   private static final Map<Direction, VoxelShape> AABBS = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.NORTH,
         Block.box(0.0, 4.5, 14.0, 16.0, 12.5, 16.0),
         Direction.SOUTH,
         Block.box(0.0, 4.5, 0.0, 16.0, 12.5, 2.0),
         Direction.EAST,
         Block.box(0.0, 4.5, 0.0, 2.0, 12.5, 16.0),
         Direction.WEST,
         Block.box(14.0, 4.5, 0.0, 16.0, 12.5, 16.0)
      )
   );

   @Override
   public MapCodec<WallSignBlock> codec() {
      return CODEC;
   }

   public WallSignBlock(WoodType var1, BlockBehaviour.Properties var2) {
      super(var1, var2.sound(var1.soundType()));
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, Boolean.valueOf(false)));
   }

   @Override
   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return AABBS.get(var1.getValue(FACING));
   }

   @Override
   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return var2.getBlockState(var3.relative(var1.getValue(FACING).getOpposite())).isSolid();
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      FluidState var3 = var1.getLevel().getFluidState(var1.getClickedPos());
      Level var4 = var1.getLevel();
      BlockPos var5 = var1.getClickedPos();
      Direction[] var6 = var1.getNearestLookingDirections();

      for(Direction var10 : var6) {
         if (var10.getAxis().isHorizontal()) {
            Direction var11 = var10.getOpposite();
            var2 = var2.setValue(FACING, var11);
            if (var2.canSurvive(var4, var5)) {
               return var2.setValue(WATERLOGGED, Boolean.valueOf(var3.getType() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var4, var5)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public float getYRotationDegrees(BlockState var1) {
      return var1.getValue(FACING).toYRot();
   }

   @Override
   public Vec3 getSignHitboxCenterPosition(BlockState var1) {
      VoxelShape var2 = AABBS.get(var1.getValue(FACING));
      return var2.bounds().getCenter();
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, WATERLOGGED);
   }
}
