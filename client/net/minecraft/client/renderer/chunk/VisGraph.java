package net.minecraft.client.renderer.chunk;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class VisGraph {
   private static final int SIZE_IN_BITS = 4;
   private static final int LEN = 16;
   private static final int MASK = 15;
   private static final int SIZE = 4096;
   private static final int X_SHIFT = 0;
   private static final int Z_SHIFT = 4;
   private static final int Y_SHIFT = 8;
   private static final int DX = (int)Math.pow(16.0, 0.0);
   private static final int DZ = (int)Math.pow(16.0, 1.0);
   private static final int DY = (int)Math.pow(16.0, 2.0);
   private static final int INVALID_INDEX = -1;
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BitSet bitSet = new BitSet(4096);
   private static final int[] INDEX_OF_EDGES = Util.make(new int[1352], var0 -> {
      boolean var1 = false;
      boolean var2 = true;
      int var3 = 0;

      for(int var4 = 0; var4 < 16; ++var4) {
         for(int var5 = 0; var5 < 16; ++var5) {
            for(int var6 = 0; var6 < 16; ++var6) {
               if (var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15 || var6 == 0 || var6 == 15) {
                  var0[var3++] = getIndex(var4, var5, var6);
               }
            }
         }
      }
   });
   private int empty = 4096;

   public VisGraph() {
      super();
   }

   public void setOpaque(BlockPos var1) {
      this.bitSet.set(getIndex(var1), true);
      --this.empty;
   }

   private static int getIndex(BlockPos var0) {
      return getIndex(var0.getX() & 15, var0.getY() & 15, var0.getZ() & 15);
   }

   private static int getIndex(int var0, int var1, int var2) {
      return var0 << 0 | var1 << 8 | var2 << 4;
   }

   public VisibilitySet resolve() {
      VisibilitySet var1 = new VisibilitySet();
      if (4096 - this.empty < 256) {
         var1.setAll(true);
      } else if (this.empty == 0) {
         var1.setAll(false);
      } else {
         for(int var5 : INDEX_OF_EDGES) {
            if (!this.bitSet.get(var5)) {
               var1.add(this.floodFill(var5));
            }
         }
      }

      return var1;
   }

   private Set<Direction> floodFill(int var1) {
      EnumSet var2 = EnumSet.noneOf(Direction.class);
      IntArrayFIFOQueue var3 = new IntArrayFIFOQueue();
      var3.enqueue(var1);
      this.bitSet.set(var1, true);

      while(!var3.isEmpty()) {
         int var4 = var3.dequeueInt();
         this.addEdges(var4, var2);

         for(Direction var8 : DIRECTIONS) {
            int var9 = this.getNeighborIndexAtFace(var4, var8);
            if (var9 >= 0 && !this.bitSet.get(var9)) {
               this.bitSet.set(var9, true);
               var3.enqueue(var9);
            }
         }
      }

      return var2;
   }

   private void addEdges(int var1, Set<Direction> var2) {
      int var3 = var1 >> 0 & 15;
      if (var3 == 0) {
         var2.add(Direction.WEST);
      } else if (var3 == 15) {
         var2.add(Direction.EAST);
      }

      int var4 = var1 >> 8 & 15;
      if (var4 == 0) {
         var2.add(Direction.DOWN);
      } else if (var4 == 15) {
         var2.add(Direction.UP);
      }

      int var5 = var1 >> 4 & 15;
      if (var5 == 0) {
         var2.add(Direction.NORTH);
      } else if (var5 == 15) {
         var2.add(Direction.SOUTH);
      }
   }

   private int getNeighborIndexAtFace(int var1, Direction var2) {
      switch(var2) {
         case DOWN:
            if ((var1 >> 8 & 15) == 0) {
               return -1;
            }

            return var1 - DY;
         case UP:
            if ((var1 >> 8 & 15) == 15) {
               return -1;
            }

            return var1 + DY;
         case NORTH:
            if ((var1 >> 4 & 15) == 0) {
               return -1;
            }

            return var1 - DZ;
         case SOUTH:
            if ((var1 >> 4 & 15) == 15) {
               return -1;
            }

            return var1 + DZ;
         case WEST:
            if ((var1 >> 0 & 15) == 0) {
               return -1;
            }

            return var1 - DX;
         case EAST:
            if ((var1 >> 0 & 15) == 15) {
               return -1;
            }

            return var1 + DX;
         default:
            return -1;
      }
   }
}
