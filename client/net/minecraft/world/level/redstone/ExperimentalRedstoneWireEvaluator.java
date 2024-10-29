package net.minecraft.world.level.redstone;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.common.custom.RedstoneWireOrientationsDebugPayload;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.RedstoneSide;

public class ExperimentalRedstoneWireEvaluator extends RedstoneWireEvaluator {
   private final Deque<BlockPos> wiresToTurnOff = new ArrayDeque();
   private final Deque<BlockPos> wiresToTurnOn = new ArrayDeque();
   private final Object2IntMap<BlockPos> updatedWires = new Object2IntLinkedOpenHashMap();

   public ExperimentalRedstoneWireEvaluator(RedStoneWireBlock var1) {
      super(var1);
   }

   public void updatePowerStrength(Level var1, BlockPos var2, BlockState var3, @Nullable Orientation var4, boolean var5) {
      Orientation var6 = getInitialOrientation(var1, var4);
      this.calculateCurrentChanges(var1, var2, var6);
      ObjectIterator var7 = this.updatedWires.object2IntEntrySet().iterator();

      for(boolean var8 = true; var7.hasNext(); var8 = false) {
         Object2IntMap.Entry var9 = (Object2IntMap.Entry)var7.next();
         BlockPos var10 = (BlockPos)var9.getKey();
         int var11 = var9.getIntValue();
         int var12 = unpackPower(var11);
         BlockState var13 = var1.getBlockState(var10);
         if (var13.is(this.wireBlock) && !((Integer)var13.getValue(RedStoneWireBlock.POWER)).equals(var12)) {
            int var14 = 2;
            if (!var5 || !var8) {
               var14 |= 128;
            }

            var1.setBlock(var10, (BlockState)var13.setValue(RedStoneWireBlock.POWER, var12), var14);
         } else {
            var7.remove();
         }
      }

      this.causeNeighborUpdates(var1);
   }

   private void causeNeighborUpdates(Level var1) {
      this.updatedWires.forEach((var2, var3) -> {
         Orientation var4 = unpackOrientation(var3);
         BlockState var5 = var1.getBlockState(var2);
         Iterator var6 = var4.getDirections().iterator();

         while(true) {
            Direction var7;
            BlockPos var8;
            BlockState var9;
            Orientation var10;
            do {
               do {
                  if (!var6.hasNext()) {
                     return;
                  }

                  var7 = (Direction)var6.next();
               } while(!isConnected(var5, var7));

               var8 = var2.relative(var7);
               var9 = var1.getBlockState(var8);
               var10 = var4.withFrontPreserveUp(var7);
               var1.neighborChanged(var9, var8, this.wireBlock, var10, false);
            } while(!var9.isRedstoneConductor(var1, var8));

            Iterator var11 = var10.getDirections().iterator();

            while(var11.hasNext()) {
               Direction var12 = (Direction)var11.next();
               if (var12 != var7.getOpposite()) {
                  var1.neighborChanged(var8.relative(var12), this.wireBlock, var10.withFrontPreserveUp(var12));
               }
            }
         }
      });
   }

   private static boolean isConnected(BlockState var0, Direction var1) {
      EnumProperty var2 = (EnumProperty)RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(var1);
      if (var2 == null) {
         return var1 == Direction.DOWN;
      } else {
         return ((RedstoneSide)var0.getValue(var2)).isConnected();
      }
   }

   private static Orientation getInitialOrientation(Level var0, @Nullable Orientation var1) {
      Orientation var2;
      if (var1 != null) {
         var2 = var1;
      } else {
         var2 = Orientation.random(var0.random);
      }

      return var2.withUp(Direction.UP).withSideBias(Orientation.SideBias.LEFT);
   }

   private void calculateCurrentChanges(Level var1, BlockPos var2, Orientation var3) {
      BlockState var4 = var1.getBlockState(var2);
      if (var4.is(this.wireBlock)) {
         this.setPower(var2, (Integer)var4.getValue(RedStoneWireBlock.POWER), var3);
         this.wiresToTurnOff.add(var2);
      } else {
         this.propagateChangeToNeighbors(var1, var2, 0, var3, true);
      }

      BlockPos var5;
      int var6;
      Orientation var7;
      int var8;
      int var9;
      int var10;
      int var11;
      int var12;
      for(; !this.wiresToTurnOff.isEmpty(); this.propagateChangeToNeighbors(var1, var5, var12, var7, var8 > var11)) {
         var5 = (BlockPos)this.wiresToTurnOff.removeFirst();
         var6 = this.updatedWires.getInt(var5);
         var7 = unpackOrientation(var6);
         var8 = unpackPower(var6);
         var9 = this.getBlockSignal(var1, var5);
         var10 = this.getIncomingWireSignal(var1, var5);
         var11 = Math.max(var9, var10);
         if (var11 < var8) {
            if (var9 > 0 && !this.wiresToTurnOn.contains(var5)) {
               this.wiresToTurnOn.add(var5);
            }

            var12 = 0;
         } else {
            var12 = var11;
         }

         if (var12 != var8) {
            this.setPower(var5, var12, var7);
         }
      }

      Orientation var14;
      for(; !this.wiresToTurnOn.isEmpty(); this.propagateChangeToNeighbors(var1, var5, var10, var14, false)) {
         var5 = (BlockPos)this.wiresToTurnOn.removeFirst();
         var6 = this.updatedWires.getInt(var5);
         int var13 = unpackPower(var6);
         var8 = this.getBlockSignal(var1, var5);
         var9 = this.getIncomingWireSignal(var1, var5);
         var10 = Math.max(var8, var9);
         var14 = unpackOrientation(var6);
         if (var10 > var13) {
            this.setPower(var5, var10, var14);
         } else if (var10 < var13) {
            throw new IllegalStateException("Turning off wire while trying to turn it on. Should not happen.");
         }
      }

   }

   private static int packOrientationAndPower(Orientation var0, int var1) {
      return var0.getIndex() << 4 | var1;
   }

   private static Orientation unpackOrientation(int var0) {
      return Orientation.fromIndex(var0 >> 4);
   }

   private static int unpackPower(int var0) {
      return var0 & 15;
   }

   private void setPower(BlockPos var1, int var2, Orientation var3) {
      this.updatedWires.compute(var1, (var2x, var3x) -> {
         return var3x == null ? packOrientationAndPower(var3, var2) : packOrientationAndPower(unpackOrientation(var3x), var2);
      });
   }

   private void propagateChangeToNeighbors(Level var1, BlockPos var2, int var3, Orientation var4, boolean var5) {
      Iterator var6 = var4.getHorizontalDirections().iterator();

      Direction var7;
      BlockPos var8;
      while(var6.hasNext()) {
         var7 = (Direction)var6.next();
         var8 = var2.relative(var7);
         this.enqueueNeighborWire(var1, var8, var3, var4.withFront(var7), var5);
      }

      var6 = var4.getVerticalDirections().iterator();

      label35:
      while(var6.hasNext()) {
         var7 = (Direction)var6.next();
         var8 = var2.relative(var7);
         boolean var9 = var1.getBlockState(var8).isRedstoneConductor(var1, var8);
         Iterator var10 = var4.getHorizontalDirections().iterator();

         while(true) {
            while(true) {
               if (!var10.hasNext()) {
                  continue label35;
               }

               Direction var11 = (Direction)var10.next();
               BlockPos var12 = var2.relative(var11);
               BlockPos var13;
               if (var7 == Direction.UP && !var9) {
                  var13 = var8.relative(var11);
                  this.enqueueNeighborWire(var1, var13, var3, var4.withFront(var11), var5);
               } else if (var7 == Direction.DOWN && !var1.getBlockState(var12).isRedstoneConductor(var1, var12)) {
                  var13 = var8.relative(var11);
                  this.enqueueNeighborWire(var1, var13, var3, var4.withFront(var11), var5);
               }
            }
         }
      }

   }

   private void enqueueNeighborWire(Level var1, BlockPos var2, int var3, Orientation var4, boolean var5) {
      BlockState var6 = var1.getBlockState(var2);
      if (var6.is(this.wireBlock)) {
         int var7 = this.getWireSignal(var2, var6);
         if (var7 < var3 - 1 && !this.wiresToTurnOn.contains(var2)) {
            this.wiresToTurnOn.add(var2);
            this.setPower(var2, var7, var4);
         }

         if (var5 && var7 > var3 && !this.wiresToTurnOff.contains(var2)) {
            this.wiresToTurnOff.add(var2);
            this.setPower(var2, var7, var4);
         }
      }

   }

   protected int getWireSignal(BlockPos var1, BlockState var2) {
      int var3 = this.updatedWires.getOrDefault(var1, -1);
      return var3 != -1 ? unpackPower(var3) : super.getWireSignal(var1, var2);
   }

   // $FF: synthetic method
   private static void lambda$causeNeighborUpdates$1(List var0, BlockPos var1, Integer var2) {
      Orientation var3 = unpackOrientation(var2);
      var0.add(new RedstoneWireOrientationsDebugPayload.Wire(var1, var3));
   }
}
