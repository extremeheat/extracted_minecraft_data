package net.minecraft.world.lighting;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import javax.annotation.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.IWorldWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseLightEngine implements ILightEngine {
   private static final Logger field_202672_b = LogManager.getLogger();
   private static final EnumFacing[] field_202674_d = EnumFacing.values();
   private final IntPriorityQueue field_202673_c = new IntArrayFIFOQueue(786);

   public BaseLightEngine() {
      super();
   }

   public int func_202666_a(IWorldReaderBase var1, BlockPos var2) {
      return var1.func_175642_b(this.func_202657_a(), var2);
   }

   public void func_202667_a(IWorldWriter var1, BlockPos var2, int var3) {
      var1.func_175653_a(this.func_202657_a(), var2, var3);
   }

   protected int func_202665_b(IBlockReader var1, BlockPos var2) {
      return var1.func_180495_p(var2).func_200016_a(var1, var2);
   }

   protected int func_202670_c(IBlockReader var1, BlockPos var2) {
      return var1.func_180495_p(var2).func_185906_d();
   }

   private int func_202662_a(@Nullable EnumFacing var1, int var2, int var3, int var4, int var5) {
      int var6 = 7;
      if (var1 != null) {
         var6 = var1.ordinal();
      }

      return var6 << 24 | var2 << 18 | var3 << 10 | var4 << 4 | var5 << 0;
   }

   private int func_202660_a(int var1) {
      return var1 >> 18 & 63;
   }

   private int func_202668_b(int var1) {
      return var1 >> 10 & 255;
   }

   private int func_202658_c(int var1) {
      return var1 >> 4 & 63;
   }

   private int func_202663_d(int var1) {
      return var1 >> 0 & 15;
   }

   @Nullable
   private EnumFacing func_202661_e(int var1) {
      int var2 = var1 >> 24 & 7;
      return var2 == 7 ? null : EnumFacing.values()[var1 >> 24 & 7];
   }

   protected void func_202664_a(IWorld var1, ChunkPos var2) {
      BlockPos.PooledMutableBlockPos var3 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var4 = null;

      try {
         while(!this.field_202673_c.isEmpty()) {
            int var5 = this.field_202673_c.dequeueInt();
            int var6 = this.func_202663_d(var5);
            int var7 = this.func_202660_a(var5) - 16;
            int var8 = this.func_202668_b(var5);
            int var9 = this.func_202658_c(var5) - 16;
            EnumFacing var10 = this.func_202661_e(var5);
            EnumFacing[] var11 = field_202674_d;
            int var12 = var11.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               EnumFacing var14 = var11[var13];
               if (var14 != var10) {
                  int var15 = var7 + var14.func_82601_c();
                  int var16 = var8 + var14.func_96559_d();
                  int var17 = var9 + var14.func_82599_e();
                  if (var16 <= 255 && var16 >= 0) {
                     var3.func_181079_c(var15 + var2.func_180334_c(), var16, var17 + var2.func_180333_d());
                     int var18 = this.func_202665_b(var1, var3);
                     int var19 = var6 - Math.max(var18, 1);
                     if (var19 > 0 && var19 > this.func_202666_a(var1, var3)) {
                        this.func_202667_a(var1, var3, var19);
                        this.func_202659_a(var2, var3, var19);
                     }
                  }
               }
            }
         }
      } catch (Throwable var27) {
         var4 = var27;
         throw var27;
      } finally {
         if (var3 != null) {
            if (var4 != null) {
               try {
                  var3.close();
               } catch (Throwable var26) {
                  var4.addSuppressed(var26);
               }
            } else {
               var3.close();
            }
         }

      }

   }

   protected void func_202669_a(ChunkPos var1, int var2, int var3, int var4, int var5) {
      int var6 = var2 - var1.func_180334_c() + 16;
      int var7 = var4 - var1.func_180333_d() + 16;
      this.field_202673_c.enqueue(this.func_202662_a((EnumFacing)null, var6, var3, var7, var5));
   }

   protected void func_202659_a(ChunkPos var1, BlockPos var2, int var3) {
      this.func_202669_a(var1, var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p(), var3);
   }
}
