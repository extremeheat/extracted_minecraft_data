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
   SideChainPart getSideChainPart(BlockState var1);

   BlockState setSideChainPart(BlockState var1, SideChainPart var2);

   Direction getFacing(BlockState var1);

   boolean isConnectable(BlockState var1);

   int getMaxChainLength();

   default List<BlockPos> getAllBlocksConnectedTo(LevelAccessor var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      if (!this.isConnectable(var3)) {
         return List.of();
      } else {
         Neighbors var4 = this.getNeighbors(var1, var2, this.getFacing(var3));
         LinkedList var5 = new LinkedList();
         var5.add(var2);
         Objects.requireNonNull(var4);
         IntFunction var10001 = var4::left;
         SideChainPart var10002 = SideChainPart.LEFT;
         Objects.requireNonNull(var5);
         this.addBlocksConnectingTowards(var10001, var10002, var5::addFirst);
         Objects.requireNonNull(var4);
         var10001 = var4::right;
         var10002 = SideChainPart.RIGHT;
         Objects.requireNonNull(var5);
         this.addBlocksConnectingTowards(var10001, var10002, var5::addLast);
         return var5;
      }
   }

   private void addBlocksConnectingTowards(IntFunction<Neighbor> var1, SideChainPart var2, Consumer<BlockPos> var3) {
      for(int var4 = 1; var4 < this.getMaxChainLength(); ++var4) {
         Neighbor var5 = (Neighbor)var1.apply(var4);
         if (var5.connectsTowards(var2)) {
            var3.accept(var5.pos());
         }

         if (var5.isUnconnectableOrChainEnd()) {
            break;
         }
      }

   }

   default void updateNeighborsAfterPoweringDown(LevelAccessor var1, BlockPos var2, BlockState var3) {
      Neighbors var4 = this.getNeighbors(var1, var2, this.getFacing(var3));
      var4.left().disconnectFromRight();
      var4.right().disconnectFromLeft();
   }

   default void updateSelfAndNeighborsOnPoweringUp(LevelAccessor var1, BlockPos var2, BlockState var3, BlockState var4) {
      if (this.isConnectable(var3)) {
         if (!this.isBeingUpdatedByNeighbor(var3, var4)) {
            Neighbors var5 = this.getNeighbors(var1, var2, this.getFacing(var3));
            SideChainPart var6 = SideChainPart.UNCONNECTED;
            int var7 = var5.left().isConnectable() ? this.getAllBlocksConnectedTo(var1, var5.left().pos()).size() : 0;
            int var8 = var5.right().isConnectable() ? this.getAllBlocksConnectedTo(var1, var5.right().pos()).size() : 0;
            int var9 = 1;
            if (this.canConnect(var7, var9)) {
               var6 = var6.whenConnectedToTheLeft();
               var5.left().connectToTheRight();
               var9 += var7;
            }

            if (this.canConnect(var8, var9)) {
               var6 = var6.whenConnectedToTheRight();
               var5.right().connectToTheLeft();
            }

            this.setPart(var1, var2, var6);
         }
      }
   }

   private boolean canConnect(int var1, int var2) {
      return var1 > 0 && var2 + var1 <= this.getMaxChainLength();
   }

   private boolean isBeingUpdatedByNeighbor(BlockState var1, BlockState var2) {
      boolean var3 = this.getSideChainPart(var1).isConnected();
      boolean var4 = this.isConnectable(var2) && this.getSideChainPart(var2).isConnected();
      return var3 || var4;
   }

   private Neighbors getNeighbors(LevelAccessor var1, BlockPos var2, Direction var3) {
      return new Neighbors(this, var1, var3, var2, new HashMap());
   }

   default void setPart(LevelAccessor var1, BlockPos var2, SideChainPart var3) {
      BlockState var4 = var1.getBlockState(var2);
      if (this.getSideChainPart(var4) != var3) {
         var1.setBlock(var2, this.setSideChainPart(var4, var3), 3);
      }

   }

   public static record Neighbors(SideChainPartBlock block, LevelAccessor level, Direction facing, BlockPos center, Map<BlockPos, Neighbor> cache) {
      public Neighbors(SideChainPartBlock var1, LevelAccessor var2, Direction var3, BlockPos var4, Map<BlockPos, Neighbor> var5) {
         super();
         this.block = var1;
         this.level = var2;
         this.facing = var3;
         this.center = var4;
         this.cache = var5;
      }

      private boolean isConnectableToThisBlock(BlockState var1) {
         return this.block.isConnectable(var1) && this.block.getFacing(var1) == this.facing;
      }

      private Neighbor createNewNeighbor(BlockPos var1) {
         BlockState var2 = this.level.getBlockState(var1);
         SideChainPart var3 = this.isConnectableToThisBlock(var2) ? this.block.getSideChainPart(var2) : null;
         return (Neighbor)(var3 == null ? new EmptyNeighbor(var1) : new SideChainNeighbor(this.level, this.block, var1, var3));
      }

      private Neighbor getOrCreateNeighbor(Direction var1, Integer var2) {
         return (Neighbor)this.cache.computeIfAbsent(this.center.relative(var1, var2), this::createNewNeighbor);
      }

      public Neighbor left(int var1) {
         return this.getOrCreateNeighbor(this.facing.getClockWise(), var1);
      }

      public Neighbor right(int var1) {
         return this.getOrCreateNeighbor(this.facing.getCounterClockWise(), var1);
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

      boolean connectsTowards(SideChainPart var1);

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
      public EmptyNeighbor(BlockPos var1) {
         super();
         this.pos = var1;
      }

      public boolean isConnectable() {
         return false;
      }

      public boolean isUnconnectableOrChainEnd() {
         return true;
      }

      public boolean connectsTowards(SideChainPart var1) {
         return false;
      }
   }

   public static record SideChainNeighbor(LevelAccessor level, SideChainPartBlock block, BlockPos pos, SideChainPart part) implements Neighbor {
      public SideChainNeighbor(LevelAccessor var1, SideChainPartBlock var2, BlockPos var3, SideChainPart var4) {
         super();
         this.level = var1;
         this.block = var2;
         this.pos = var3;
         this.part = var4;
      }

      public boolean isConnectable() {
         return true;
      }

      public boolean isUnconnectableOrChainEnd() {
         return this.part.isChainEnd();
      }

      public boolean connectsTowards(SideChainPart var1) {
         return this.part.isConnectionTowards(var1);
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
