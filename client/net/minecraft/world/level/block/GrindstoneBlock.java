package net.minecraft.world.level.block;

import com.mojang.math.OctahedralGroup;
import com.mojang.serialization.MapCodec;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrindstoneBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final MapCodec<GrindstoneBlock> CODEC = simpleCodec(GrindstoneBlock::new);
   private static final Component CONTAINER_TITLE = Component.translatable("container.grindstone_title");
   private final Function<BlockState, VoxelShape> shapes;

   public MapCodec<GrindstoneBlock> codec() {
      return CODEC;
   }

   protected GrindstoneBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(FACE, AttachFace.WALL));
      this.shapes = this.makeShapes();
   }

   private Function<BlockState, VoxelShape> makeShapes() {
      VoxelShape var1 = Shapes.or(Block.box(2.0, 6.0, 7.0, 4.0, 10.0, 16.0), Block.box(2.0, 5.0, 3.0, 4.0, 11.0, 9.0));
      VoxelShape var2 = Shapes.rotate(var1, OctahedralGroup.INVERT_X);
      VoxelShape var3 = Shapes.or(Block.boxZ(8.0, 2.0, 14.0, 0.0, 12.0), var1, var2);
      Map var4 = Shapes.rotateAttachFace(var3);
      return this.getShapeForEachState((var1x) -> (VoxelShape)((Map)var4.get(var1x.getValue(FACE))).get(var1x.getValue(FACING)));
   }

   private VoxelShape getVoxelShape(BlockState var1) {
      return (VoxelShape)this.shapes.apply(var1);
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return this.getVoxelShape(var1);
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return true;
   }

   protected InteractionResult useWithoutItem(BlockState var1, Level var2, BlockPos var3, Player var4, BlockHitResult var5) {
      if (!var2.isClientSide) {
         var4.openMenu(var1.getMenuProvider(var2, var3));
         var4.awardStat(Stats.INTERACT_WITH_GRINDSTONE);
      }

      return InteractionResult.SUCCESS;
   }

   protected MenuProvider getMenuProvider(BlockState var1, Level var2, BlockPos var3) {
      return new SimpleMenuProvider((var2x, var3x, var4) -> new GrindstoneMenu(var2x, var3x, ContainerLevelAccess.create(var2, var3)), CONTAINER_TITLE);
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, FACE);
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }
}
