package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WallTorchBlock extends TorchBlock {
   public static final MapCodec<WallTorchBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(PARTICLE_OPTIONS_FIELD.forGetter((var0x) -> var0x.flameParticle), propertiesCodec()).apply(var0, WallTorchBlock::new));
   public static final EnumProperty<Direction> FACING;
   protected static final float AABB_OFFSET = 2.5F;
   private static final Map<Direction, VoxelShape> AABBS;

   public MapCodec<WallTorchBlock> codec() {
      return CODEC;
   }

   protected WallTorchBlock(SimpleParticleType var1, BlockBehaviour.Properties var2) {
      super(var1, var2);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return getShape(var1);
   }

   public static VoxelShape getShape(BlockState var0) {
      return (VoxelShape)AABBS.get(var0.getValue(FACING));
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return canSurvive(var2, var3, (Direction)var1.getValue(FACING));
   }

   public static boolean canSurvive(LevelReader var0, BlockPos var1, Direction var2) {
      BlockPos var3 = var1.relative(var2.getOpposite());
      BlockState var4 = var0.getBlockState(var3);
      return var4.isFaceSturdy(var0, var3, var2);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState();
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      Direction[] var5 = var1.getNearestLookingDirections();

      for(Direction var9 : var5) {
         if (var9.getAxis().isHorizontal()) {
            Direction var10 = var9.getOpposite();
            var2 = (BlockState)var2.setValue(FACING, var10);
            if (var2.canSurvive(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      return var5.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var2, var4) ? Blocks.AIR.defaultBlockState() : var1;
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, RandomSource var4) {
      Direction var5 = (Direction)var1.getValue(FACING);
      double var6 = (double)var3.getX() + 0.5;
      double var8 = (double)var3.getY() + 0.7;
      double var10 = (double)var3.getZ() + 0.5;
      double var12 = 0.22;
      double var14 = 0.27;
      Direction var16 = var5.getOpposite();
      var2.addParticle(ParticleTypes.SMOKE, var6 + 0.27 * (double)var16.getStepX(), var8 + 0.22, var10 + 0.27 * (double)var16.getStepZ(), 0.0, 0.0, 0.0);
      var2.addParticle(this.flameParticle, var6 + 0.27 * (double)var16.getStepX(), var8 + 0.22, var10 + 0.27 * (double)var16.getStepZ(), 0.0, 0.0, 0.0);
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(5.5, 3.0, 11.0, 10.5, 13.0, 16.0), Direction.SOUTH, Block.box(5.5, 3.0, 0.0, 10.5, 13.0, 5.0), Direction.WEST, Block.box(11.0, 3.0, 5.5, 16.0, 13.0, 10.5), Direction.EAST, Block.box(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)));
   }
}
