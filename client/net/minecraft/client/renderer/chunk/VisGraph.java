package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Set;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;

public class VisGraph {
   private static final int field_178616_a = (int)Math.pow(16.0D, 0.0D);
   private static final int field_178614_b = (int)Math.pow(16.0D, 1.0D);
   private static final int field_178615_c = (int)Math.pow(16.0D, 2.0D);
   private final BitSet field_178612_d = new BitSet(4096);
   private static final int[] field_178613_e = new int[1352];
   private int field_178611_f = 4096;

   public VisGraph() {
      super();
   }

   public void func_178606_a(BlockPos var1) {
      this.field_178612_d.set(func_178608_c(var1), true);
      --this.field_178611_f;
   }

   private static int func_178608_c(BlockPos var0) {
      return func_178605_a(var0.func_177958_n() & 15, var0.func_177956_o() & 15, var0.func_177952_p() & 15);
   }

   private static int func_178605_a(int var0, int var1, int var2) {
      return var0 << 0 | var1 << 8 | var2 << 4;
   }

   public SetVisibility func_178607_a() {
      SetVisibility var1 = new SetVisibility();
      if (4096 - this.field_178611_f < 256) {
         var1.func_178618_a(true);
      } else if (this.field_178611_f == 0) {
         var1.func_178618_a(false);
      } else {
         int[] var2 = field_178613_e;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            int var5 = var2[var4];
            if (!this.field_178612_d.get(var5)) {
               var1.func_178620_a(this.func_178604_a(var5));
            }
         }
      }

      return var1;
   }

   public Set<EnumFacing> func_178609_b(BlockPos var1) {
      return this.func_178604_a(func_178608_c(var1));
   }

   private Set<EnumFacing> func_178604_a(int var1) {
      EnumSet var2 = EnumSet.noneOf(EnumFacing.class);
      LinkedList var3 = Lists.newLinkedList();
      var3.add(IntegerCache.func_181756_a(var1));
      this.field_178612_d.set(var1, true);

      while(!var3.isEmpty()) {
         int var4 = (Integer)var3.poll();
         this.func_178610_a(var4, var2);
         EnumFacing[] var5 = EnumFacing.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing var8 = var5[var7];
            int var9 = this.func_178603_a(var4, var8);
            if (var9 >= 0 && !this.field_178612_d.get(var9)) {
               this.field_178612_d.set(var9, true);
               var3.add(IntegerCache.func_181756_a(var9));
            }
         }
      }

      return var2;
   }

   private void func_178610_a(int var1, Set<EnumFacing> var2) {
      int var3 = var1 >> 0 & 15;
      if (var3 == 0) {
         var2.add(EnumFacing.WEST);
      } else if (var3 == 15) {
         var2.add(EnumFacing.EAST);
      }

      int var4 = var1 >> 8 & 15;
      if (var4 == 0) {
         var2.add(EnumFacing.DOWN);
      } else if (var4 == 15) {
         var2.add(EnumFacing.UP);
      }

      int var5 = var1 >> 4 & 15;
      if (var5 == 0) {
         var2.add(EnumFacing.NORTH);
      } else if (var5 == 15) {
         var2.add(EnumFacing.SOUTH);
      }

   }

   private int func_178603_a(int var1, EnumFacing var2) {
      switch(var2) {
      case DOWN:
         if ((var1 >> 8 & 15) == 0) {
            return -1;
         }

         return var1 - field_178615_c;
      case UP:
         if ((var1 >> 8 & 15) == 15) {
            return -1;
         }

         return var1 + field_178615_c;
      case NORTH:
         if ((var1 >> 4 & 15) == 0) {
            return -1;
         }

         return var1 - field_178614_b;
      case SOUTH:
         if ((var1 >> 4 & 15) == 15) {
            return -1;
         }

         return var1 + field_178614_b;
      case WEST:
         if ((var1 >> 0 & 15) == 0) {
            return -1;
         }

         return var1 - field_178616_a;
      case EAST:
         if ((var1 >> 0 & 15) == 15) {
            return -1;
         }

         return var1 + field_178616_a;
      default:
         return -1;
      }
   }

   static {
      boolean var0 = false;
      boolean var1 = true;
      int var2 = 0;

      for(int var3 = 0; var3 < 16; ++var3) {
         for(int var4 = 0; var4 < 16; ++var4) {
            for(int var5 = 0; var5 < 16; ++var5) {
               if (var3 == 0 || var3 == 15 || var4 == 0 || var4 == 15 || var5 == 0 || var5 == 15) {
                  field_178613_e[var2++] = func_178605_a(var3, var4, var5);
               }
            }
         }
      }

   }
}
