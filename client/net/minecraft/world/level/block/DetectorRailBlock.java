package net.minecraft.world.level.block;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.MinecartCommandBlock;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.phys.AABB;

public class DetectorRailBlock extends BaseRailBlock {
   public static final EnumProperty<RailShape> SHAPE;
   public static final BooleanProperty POWERED;
   private static final int PRESSED_CHECK_PERIOD = 20;

   public DetectorRailBlock(BlockBehaviour.Properties var1) {
      super(true, var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false)).setValue(SHAPE, RailShape.NORTH_SOUTH)).setValue(WATERLOGGED, false));
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide) {
         if (!(Boolean)var1.getValue(POWERED)) {
            this.checkPressed(var2, var3, var1);
         }
      }
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var1.getValue(POWERED)) {
         this.checkPressed(var2, var3, var1);
      }
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (!(Boolean)var1.getValue(POWERED)) {
         return 0;
      } else {
         return var4 == Direction.UP ? 15 : 0;
      }
   }

   private void checkPressed(Level var1, BlockPos var2, BlockState var3) {
      if (this.canSurvive(var3, var1, var2)) {
         boolean var4 = (Boolean)var3.getValue(POWERED);
         boolean var5 = false;
         List var6 = this.getInteractingMinecartOfType(var1, var2, AbstractMinecart.class, (var0) -> {
            return true;
         });
         if (!var6.isEmpty()) {
            var5 = true;
         }

         BlockState var7;
         if (var5 && !var4) {
            var7 = (BlockState)var3.setValue(POWERED, true);
            var1.setBlock(var2, var7, 3);
            this.updatePowerToConnected(var1, var2, var7, true);
            var1.updateNeighborsAt(var2, this);
            var1.updateNeighborsAt(var2.below(), this);
            var1.setBlocksDirty(var2, var3, var7);
         }

         if (!var5 && var4) {
            var7 = (BlockState)var3.setValue(POWERED, false);
            var1.setBlock(var2, var7, 3);
            this.updatePowerToConnected(var1, var2, var7, false);
            var1.updateNeighborsAt(var2, this);
            var1.updateNeighborsAt(var2.below(), this);
            var1.setBlocksDirty(var2, var3, var7);
         }

         if (var5) {
            var1.scheduleTick(var2, this, 20);
         }

         var1.updateNeighbourForOutputSignal(var2, this);
      }
   }

   protected void updatePowerToConnected(Level var1, BlockPos var2, BlockState var3, boolean var4) {
      RailState var5 = new RailState(var1, var2, var3);
      List var6 = var5.getConnections();
      Iterator var7 = var6.iterator();

      while(var7.hasNext()) {
         BlockPos var8 = (BlockPos)var7.next();
         BlockState var9 = var1.getBlockState(var8);
         var1.neighborChanged(var9, var8, var9.getBlock(), var2, false);
      }

   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         BlockState var6 = this.updateState(var1, var2, var3, var5);
         this.checkPressed(var2, var3, var6);
      }
   }

   public Property<RailShape> getShapeProperty() {
      return SHAPE;
   }

   public boolean hasAnalogOutputSignal(BlockState var1) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState var1, Level var2, BlockPos var3) {
      if ((Boolean)var1.getValue(POWERED)) {
         List var4 = this.getInteractingMinecartOfType(var2, var3, MinecartCommandBlock.class, (var0) -> {
            return true;
         });
         if (!var4.isEmpty()) {
            return ((MinecartCommandBlock)var4.get(0)).getCommandBlock().getSuccessCount();
         }

         List var5 = this.getInteractingMinecartOfType(var2, var3, AbstractMinecart.class, EntitySelector.CONTAINER_ENTITY_SELECTOR);
         if (!var5.isEmpty()) {
            return AbstractContainerMenu.getRedstoneSignalFromContainer((Container)var5.get(0));
         }
      }

      return 0;
   }

   private <T extends AbstractMinecart> List<T> getInteractingMinecartOfType(Level var1, BlockPos var2, Class<T> var3, Predicate<Entity> var4) {
      return var1.getEntitiesOfClass(var3, this.getSearchBB(var2), var4);
   }

   private AABB getSearchBB(BlockPos var1) {
      double var2 = 0.2;
      return new AABB((double)var1.getX() + 0.2, (double)var1.getY(), (double)var1.getZ() + 0.2, (double)(var1.getX() + 1) - 0.2, (double)(var1.getY() + 1) - 0.2, (double)(var1.getZ() + 1) - 0.2);
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case CLOCKWISE_180:
            switch ((RailShape)var1.getValue(SHAPE)) {
               case ASCENDING_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
               case ASCENDING_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
               case ASCENDING_NORTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
               case ASCENDING_SOUTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
               case SOUTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
               case SOUTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
               case NORTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
               case NORTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
            }
         case COUNTERCLOCKWISE_90:
            switch ((RailShape)var1.getValue(SHAPE)) {
               case ASCENDING_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
               case ASCENDING_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
               case ASCENDING_NORTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
               case ASCENDING_SOUTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
               case SOUTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
               case SOUTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
               case NORTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
               case NORTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
               case NORTH_SOUTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
               case EAST_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
            }
         case CLOCKWISE_90:
            switch ((RailShape)var1.getValue(SHAPE)) {
               case ASCENDING_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
               case ASCENDING_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
               case ASCENDING_NORTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
               case ASCENDING_SOUTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
               case SOUTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
               case SOUTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
               case NORTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
               case NORTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
               case NORTH_SOUTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.EAST_WEST);
               case EAST_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_SOUTH);
            }
         default:
            return var1;
      }
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      RailShape var3 = (RailShape)var1.getValue(SHAPE);
      switch (var2) {
         case LEFT_RIGHT:
            switch (var3) {
               case ASCENDING_NORTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_SOUTH);
               case ASCENDING_SOUTH:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_NORTH);
               case SOUTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
               case SOUTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
               case NORTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
               case NORTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
               default:
                  return super.mirror(var1, var2);
            }
         case FRONT_BACK:
            switch (var3) {
               case ASCENDING_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_WEST);
               case ASCENDING_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.ASCENDING_EAST);
               case ASCENDING_NORTH:
               case ASCENDING_SOUTH:
               default:
                  break;
               case SOUTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_WEST);
               case SOUTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.SOUTH_EAST);
               case NORTH_WEST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_EAST);
               case NORTH_EAST:
                  return (BlockState)var1.setValue(SHAPE, RailShape.NORTH_WEST);
            }
      }

      return super.mirror(var1, var2);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SHAPE, POWERED, WATERLOGGED);
   }

   static {
      SHAPE = BlockStateProperties.RAIL_SHAPE_STRAIGHT;
      POWERED = BlockStateProperties.POWERED;
   }
}
