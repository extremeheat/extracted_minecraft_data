package net.minecraft.world.level.block.piston;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MovingPistonBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING;
   public static final EnumProperty<PistonType> TYPE;

   public MovingPistonBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(TYPE, PistonType.DEFAULT));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos var1, BlockState var2) {
      return null;
   }

   public static BlockEntity newMovingBlockEntity(BlockPos var0, BlockState var1, BlockState var2, Direction var3, boolean var4, boolean var5) {
      return new PistonMovingBlockEntity(var0, var1, var2, var3, var4, var5);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level var1, BlockState var2, BlockEntityType<T> var3) {
      return createTickerHelper(var3, BlockEntityType.PISTON, PistonMovingBlockEntity::tick);
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var1.is(var4.getBlock())) {
         BlockEntity var6 = var2.getBlockEntity(var3);
         if (var6 instanceof PistonMovingBlockEntity) {
            ((PistonMovingBlockEntity)var6).finalTick();
         }

      }
   }

   public void destroy(LevelAccessor var1, BlockPos var2, BlockState var3) {
      BlockPos var4 = var2.relative(((Direction)var3.getValue(FACING)).getOpposite());
      BlockState var5 = var1.getBlockState(var4);
      if (var5.getBlock() instanceof PistonBaseBlock && (Boolean)var5.getValue(PistonBaseBlock.EXTENDED)) {
         var1.removeBlock(var4, false);
      }

   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (!var2.isClientSide && var2.getBlockEntity(var3) == null) {
         var2.removeBlock(var3, false);
         return InteractionResult.CONSUME;
      } else {
         return InteractionResult.PASS;
      }
   }

   public List<ItemStack> getDrops(BlockState var1, LootContext.Builder var2) {
      PistonMovingBlockEntity var3 = this.getBlockEntity(var2.getLevel(), new BlockPos((Vec3)var2.getParameter(LootContextParams.ORIGIN)));
      return var3 == null ? Collections.emptyList() : var3.getMovedState().getDrops(var2);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      PistonMovingBlockEntity var5 = this.getBlockEntity(var2, var3);
      return var5 != null ? var5.getCollisionShape(var2, var3) : Shapes.empty();
   }

   @Nullable
   private PistonMovingBlockEntity getBlockEntity(BlockGetter var1, BlockPos var2) {
      BlockEntity var3 = var1.getBlockEntity(var2);
      return var3 instanceof PistonMovingBlockEntity ? (PistonMovingBlockEntity)var3 : null;
   }

   public ItemStack getCloneItemStack(BlockGetter var1, BlockPos var2, BlockState var3) {
      return ItemStack.EMPTY;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, TYPE);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      FACING = PistonHeadBlock.FACING;
      TYPE = PistonHeadBlock.TYPE;
   }
}
