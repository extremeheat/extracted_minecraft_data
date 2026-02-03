package net.minecraft.world.level.block;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SideChainPart;

public interface SideChainPartBlock {
   SideChainPart getSideChainPart(final BlockState state);

   BlockState setSideChainPart(final BlockState state, final SideChainPart newPart);

   Direction getFacing(final BlockState state);

   boolean isConnectable(final BlockState state);

   int getMaxChainLength();

   default List<BlockPos> getAllBlocksConnectedTo(final LevelAccessor level, final BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      if (!this.isConnectable(state)) {
         return List.of();
      } else {
         Neighbors neighbors = this.getNeighbors(level, pos, this.getFacing(state));
         List<BlockPos> results = new LinkedList();
         results.add(pos);
         Objects.requireNonNull(neighbors);
         IntFunction var10001 = neighbors::left;
         SideChainPart var10002 = SideChainPart.LEFT;
         Objects.requireNonNull(results);
         this.addBlocksConnectingTowards(var10001, var10002, results::addFirst);
         Objects.requireNonNull(neighbors);
         var10001 = neighbors::right;
         var10002 = SideChainPart.RIGHT;
         Objects.requireNonNull(results);
         this.addBlocksConnectingTowards(var10001, var10002, results::addLast);
         return results;
      }
   }

   private void addBlocksConnectingTowards(final IntFunction<Neighbor> getNeighbor, final SideChainPart endPart, final Consumer<BlockPos> accumulator) {
      for(int steps = 1; steps < this.getMaxChainLength(); ++steps) {
         Neighbor neighbor = (Neighbor)getNeighbor.apply(steps);
         if (neighbor.connectsTowards(endPart)) {
            accumulator.accept(neighbor.pos());
         }

         if (neighbor.isUnconnectableOrChainEnd()) {
            break;
         }
      }

   }

   default void updateNeighborsAfterPoweringDown(final LevelAccessor level, final BlockPos pos, final BlockState state) {
      Neighbors neighbors = this.getNeighbors(level, pos, this.getFacing(state));
      neighbors.left().disconnectFromRight();
      neighbors.right().disconnectFromLeft();
   }

   default void updateSelfAndNeighborsOnPoweringUp(final LevelAccessor level, final BlockPos pos, final BlockState state, final BlockState oldState) {
      if (this.isConnectable(state)) {
         if (!this.isBeingUpdatedByNeighbor(state, oldState)) {
            Neighbors neighbors = this.getNeighbors(level, pos, this.getFacing(state));
            SideChainPart newPartForSelf = SideChainPart.UNCONNECTED;
            int existingChainOnTheLeft = neighbors.left().isConnectable() ? this.getAllBlocksConnectedTo(level, neighbors.left().pos()).size() : 0;
            int existingChainOnTheRight = neighbors.right().isConnectable() ? this.getAllBlocksConnectedTo(level, neighbors.right().pos()).size() : 0;
            int currentChainLength = 1;
            if (this.canConnect(existingChainOnTheLeft, currentChainLength)) {
               newPartForSelf = newPartForSelf.whenConnectedToTheLeft();
               neighbors.left().connectToTheRight();
               currentChainLength += existingChainOnTheLeft;
            }

            if (this.canConnect(existingChainOnTheRight, currentChainLength)) {
               newPartForSelf = newPartForSelf.whenConnectedToTheRight();
               neighbors.right().connectToTheLeft();
            }

            this.setPart(level, pos, newPartForSelf);
         }
      }
   }

   private boolean canConnect(final int newBlocksToConnectTo, final int currentChainLength) {
      return newBlocksToConnectTo > 0 && currentChainLength + newBlocksToConnectTo <= this.getMaxChainLength();
   }

   private boolean isBeingUpdatedByNeighbor(final BlockState state, final BlockState oldState) {
      boolean isGettingConnected = this.getSideChainPart(state).isConnected();
      boolean hasBeenConnectedBefore = this.isConnectable(oldState) && this.getSideChainPart(oldState).isConnected();
      return isGettingConnected || hasBeenConnectedBefore;
   }

   private Neighbors getNeighbors(final LevelAccessor level, final BlockPos center, final Direction facing) {
      return new Neighbors(this, level, facing, center, new HashMap());
   }

   private void setPart(final LevelAccessor level, final BlockPos pos, final SideChainPart newPart) {
      BlockState state = level.getBlockState(pos);
      if (this.getSideChainPart(state) != newPart) {
         level.setBlock(pos, this.setSideChainPart(state, newPart), 3);
      }

   }

   public static record Neighbors(SideChainPartBlock block, LevelAccessor level, Direction facing, BlockPos center, Map<BlockPos, Neighbor> cache) {
      public Neighbors {
         super();
      }

      private boolean isConnectableToThisBlock(final BlockState neighbor) {
         return this.block.isConnectable(neighbor) && this.block.getFacing(neighbor) == this.facing;
      }

      private Neighbor createNewNeighbor(final BlockPos pos) {
         BlockState neighbor = this.level.getBlockState(pos);
         SideChainPart part = this.isConnectableToThisBlock(neighbor) ? this.block.getSideChainPart(neighbor) : null;
         return (Neighbor)(part == null ? new EmptyNeighbor(pos) : new SideChainNeighbor(this.level, this.block, pos, part));
      }

      private Neighbor getOrCreateNeighbor(final Direction dir, final Integer steps) {
         return (Neighbor)this.cache.computeIfAbsent(this.center.relative(dir, steps), this::createNewNeighbor);
      }

      public Neighbor left(final int steps) {
         return this.getOrCreateNeighbor(this.facing.getClockWise(), steps);
      }

      public Neighbor right(final int steps) {
         return this.getOrCreateNeighbor(this.facing.getCounterClockWise(), steps);
      }

      public Neighbor left() {
         return this.left(1);
      }

      public Neighbor right() {
         return this.right(1);
      }
   }

   public sealed interface Neighbor permits SideChainPartBlock.EmptyNeighbor, SideChainPartBlock.SideChainNeighbor {
      BlockPos pos();

      boolean isConnectable();

      boolean isUnconnectableOrChainEnd();

      boolean connectsTowards(final SideChainPart endPart);

      default void connectToTheRight() {
      }

      default void connectToTheLeft() {
      }

      default void disconnectFromRight() {
      }

      default void disconnectFromLeft() {
      }
   }

   public static record EmptyNeighbor(BlockPos pos) implements Neighbor {
      public EmptyNeighbor {
         super();
      }

      public boolean isConnectable() {
         return false;
      }

      public boolean isUnconnectableOrChainEnd() {
         return true;
      }

      public boolean connectsTowards(final SideChainPart endPart) {
         return false;
      }
   }

   public static record SideChainNeighbor(LevelAccessor level, SideChainPartBlock block, BlockPos pos, SideChainPart part) implements Neighbor {
      public SideChainNeighbor {
         super();
      }

      public boolean isConnectable() {
         return true;
      }

      public boolean isUnconnectableOrChainEnd() {
         return this.part.isChainEnd();
      }

      public boolean connectsTowards(final SideChainPart endPart) {
         return this.part.isConnectionTowards(endPart);
      }

      public void connectToTheRight() {
         this.block.setPart(this.level, this.pos, this.part.whenConnectedToTheRight());
      }

      public void connectToTheLeft() {
         this.block.setPart(this.level, this.pos, this.part.whenConnectedToTheLeft());
      }

      public void disconnectFromRight() {
         this.block.setPart(this.level, this.pos, this.part.whenDisconnectedFromTheRight());
      }

      public void disconnectFromLeft() {
         this.block.setPart(this.level, this.pos, this.part.whenDisconnectedFromTheLeft());
      }
   }
}
