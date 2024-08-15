package net.minecraft.world.level.redstone;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class ExperimentalRedstoneWireEvaluator extends RedstoneWireEvaluator {
   private final Deque<BlockPos> wiresToTurnOff = new ArrayDeque<>();
   private final Deque<BlockPos> wiresToTurnOn = new ArrayDeque<>();
   private final Object2IntMap<BlockPos> updatedWires = new Object2IntLinkedOpenHashMap();

   public ExperimentalRedstoneWireEvaluator(RedStoneWireBlock var1) {
      super(var1);
   }

   @Override
   public void updatePowerStrength(Level var1, BlockPos var2, BlockState var3, @Nullable Orientation var4) {
      Orientation var5 = getInitialOrientation(var1, var4);
      this.calculateCurrentChanges(var1, var2, var5);
      ObjectIterator var6 = this.updatedWires.object2IntEntrySet().iterator();

      while (var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         BlockPos var8 = (BlockPos)var7.getKey();
         int var9 = var7.getIntValue();
         int var10 = unpackPower(var9);
         BlockState var11 = var1.getBlockState(var8);
         if (var11.is(this.wireBlock) && !var11.getValue(RedStoneWireBlock.POWER).equals(var10)) {
            var1.setBlock(var8, var11.setValue(RedStoneWireBlock.POWER, Integer.valueOf(var10)), 2);
         } else {
            var6.remove();
         }
      }

      this.causeNeighborUpdates(var1);
   }

   private void causeNeighborUpdates(Level var1) {
      this.updatedWires.forEach((var2, var3) -> {
         Orientation var4 = unpackOrientation(var3);
         BlockState var5 = var1.getBlockState(var2);

         for (Direction var7 : var4.getDirections()) {
            if (isConnected(var5, var7)) {
               BlockPos var8 = var2.relative(var7);
               BlockState var9 = var1.getBlockState(var8);
               Orientation var10 = var4.withFront(var7);
               var1.neighborChanged(var9, var8, this.wireBlock, var10, false);
               if (var9.isRedstoneConductor(var1, var8)) {
                  for (Direction var12 : var10.getDirections()) {
                     if (var12 != var7.getOpposite()) {
                        var1.neighborChanged(var8.relative(var12), this.wireBlock, var10.withFront(var12));
                     }
                  }
               }
            }
         }
      });
   }

   private static boolean isConnected(BlockState var0, Direction var1) {
      EnumProperty var2 = RedStoneWireBlock.PROPERTY_BY_DIRECTION.get(var1);
      return var2 == null ? var1 == Direction.DOWN : var0.getValue(var2).isConnected();
   }

   private static Orientation getInitialOrientation(Level var0, @Nullable Orientation var1) {
      Orientation var2;
      if (var1 != null) {
         var2 = var1;
      } else {
         var2 = Orientation.random(var0.random);
      }

      return var2.withUp(Direction.UP);
   }

   private void calculateCurrentChanges(Level var1, BlockPos var2, Orientation var3) {
      BlockState var4 = var1.getBlockState(var2);
      if (var4.is(this.wireBlock)) {
         this.setPower(var2, var4.getValue(RedStoneWireBlock.POWER), var3);
         this.wiresToTurnOff.add(var2);
      } else {
         this.propagateChangeToNeighbors(var1, var2, 0, var3, true);
      }

      while (!this.wiresToTurnOff.isEmpty()) {
         BlockPos var5 = this.wiresToTurnOff.removeFirst();
         int var6 = this.updatedWires.getInt(var5);
         Orientation var7 = unpackOrientation(var6);
         int var8 = unpackPower(var6);
         int var9 = this.getBlockSignal(var1, var5);
         int var10 = this.getIncomingWireSignal(var1, var5);
         int var11 = Math.max(var9, var10);
         int var12;
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

         this.propagateChangeToNeighbors(var1, var5, var12, var7, true);
      }

      while (!this.wiresToTurnOn.isEmpty()) {
         BlockPos var13 = this.wiresToTurnOn.removeFirst();
         int var14 = this.updatedWires.getInt(var13);
         int var15 = unpackPower(var14);
         int var16 = this.getBlockSignal(var1, var13);
         int var17 = this.getIncomingWireSignal(var1, var13);
         int var18 = Math.max(var16, var17);
         Orientation var19 = unpackOrientation(var14);
         if (var18 > var15) {
            this.setPower(var13, var18, var19);
         } else if (var18 < var15) {
            throw new IllegalStateException("Turning off wire while trying to turn it on. Should not happen.");
         }

         this.propagateChangeToNeighbors(var1, var13, var18, var19, false);
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
      this.updatedWires
         .compute(var1, (var2x, var3x) -> var3x == null ? packOrientationAndPower(var3, var2) : packOrientationAndPower(unpackOrientation(var3x), var2));
   }

   private void propagateChangeToNeighbors(Level var1, BlockPos var2, int var3, Orientation var4, boolean var5) {
      for (Direction var7 : var4.getHorizontalDirections()) {
         BlockPos var8 = var2.relative(var7);
         this.enqueueNeighborWire(var1, var8, var3, var4.withFront(var7), var5);
      }

      for (Direction var15 : var4.getVerticalDirections()) {
         BlockPos var16 = var2.relative(var15);
         boolean var9 = var1.getBlockState(var16).isRedstoneConductor(var1, var16);

         for (Direction var11 : var4.getHorizontalDirections()) {
            BlockPos var12 = var2.relative(var11);
            if (var15 == Direction.UP && !var9 || var15 == Direction.DOWN && var9 && !var1.getBlockState(var12).isRedstoneConductor(var1, var12)) {
               BlockPos var13 = var16.relative(var11);
               this.enqueueNeighborWire(var1, var13, var3, var4.withFront(var11), var5);
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

   @Override
   protected int getWireSignal(BlockPos var1, BlockState var2) {
      int var3 = this.updatedWires.getOrDefault(var1, -1);
      return var3 != -1 ? unpackPower(var3) : super.getWireSignal(var1, var2);
   }
}
