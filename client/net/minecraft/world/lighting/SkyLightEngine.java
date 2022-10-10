package net.minecraft.world.lighting;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.WorldGenRegion;

public class SkyLightEngine extends BaseLightEngine {
   public static final EnumFacing[] field_202676_b;

   public SkyLightEngine() {
      super();
   }

   public EnumLightType func_202657_a() {
      return EnumLightType.SKY;
   }

   public void func_202675_a(WorldGenRegion var1, IChunk var2) {
      int var3 = var2.func_76632_l().func_180334_c();
      int var4 = var2.func_76632_l().func_180333_d();
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var6 = null;

      try {
         BlockPos.PooledMutableBlockPos var7 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var8 = null;

         try {
            for(int var9 = 0; var9 < 16; ++var9) {
               for(int var10 = 0; var10 < 16; ++var10) {
                  int var11 = var2.func_201576_a(Heightmap.Type.LIGHT_BLOCKING, var9, var10) + 1;
                  int var12 = var9 + var3;
                  int var13 = var10 + var4;

                  for(int var14 = var11; var14 < var2.func_76587_i().length * 16 - 1; ++var14) {
                     var5.func_181079_c(var12, var14, var13);
                     this.func_202667_a(var1, var5, 15);
                  }

                  this.func_202669_a(var2.func_76632_l(), var12, var11, var13, 15);
                  EnumFacing[] var46 = field_202676_b;
                  int var15 = var46.length;

                  for(int var16 = 0; var16 < var15; ++var16) {
                     EnumFacing var17 = var46[var16];
                     int var18 = var1.func_201676_a(Heightmap.Type.LIGHT_BLOCKING, var12 + var17.func_82601_c(), var13 + var17.func_82599_e());
                     if (var18 - var11 >= 2) {
                        for(int var19 = var11; var19 <= var18; ++var19) {
                           var7.func_181079_c(var12 + var17.func_82601_c(), var19, var13 + var17.func_82599_e());
                           int var20 = var1.func_180495_p(var7).func_200016_a(var1, var7);
                           if (var20 != var1.func_201572_C()) {
                              this.func_202667_a(var1, var7, 15 - var20 - 1);
                              this.func_202659_a(var2.func_76632_l(), var7, 15 - var20 - 1);
                           }
                        }
                     }
                  }
               }
            }

            this.func_202664_a(var1, var2.func_76632_l());
         } catch (Throwable var42) {
            var8 = var42;
            throw var42;
         } finally {
            if (var7 != null) {
               if (var8 != null) {
                  try {
                     var7.close();
                  } catch (Throwable var41) {
                     var8.addSuppressed(var41);
                  }
               } else {
                  var7.close();
               }
            }

         }
      } catch (Throwable var44) {
         var6 = var44;
         throw var44;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var40) {
                  var6.addSuppressed(var40);
               }
            } else {
               var5.close();
            }
         }

      }
   }

   static {
      field_202676_b = new EnumFacing[]{EnumFacing.WEST, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH};
   }
}
