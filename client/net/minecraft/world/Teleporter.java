package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Random;
import net.minecraft.block.BlockPortal;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;

public class Teleporter {
   private static final BlockPortal field_196236_a;
   private final WorldServer field_85192_a;
   private final Random field_77187_a;
   private final Long2ObjectMap<Teleporter.PortalPosition> field_85191_c = new Long2ObjectOpenHashMap(4096);

   public Teleporter(WorldServer var1) {
      super();
      this.field_85192_a = var1;
      this.field_77187_a = new Random(var1.func_72905_C());
   }

   public void func_180266_a(Entity var1, float var2) {
      if (this.field_85192_a.field_73011_w.func_186058_p() != DimensionType.THE_END) {
         if (!this.func_180620_b(var1, var2)) {
            this.func_85188_a(var1);
            this.func_180620_b(var1, var2);
         }
      } else {
         int var3 = MathHelper.func_76128_c(var1.field_70165_t);
         int var4 = MathHelper.func_76128_c(var1.field_70163_u) - 1;
         int var5 = MathHelper.func_76128_c(var1.field_70161_v);
         boolean var6 = true;
         boolean var7 = false;

         for(int var8 = -2; var8 <= 2; ++var8) {
            for(int var9 = -2; var9 <= 2; ++var9) {
               for(int var10 = -1; var10 < 3; ++var10) {
                  int var11 = var3 + var9 * 1 + var8 * 0;
                  int var12 = var4 + var10;
                  int var13 = var5 + var9 * 0 - var8 * 1;
                  boolean var14 = var10 < 0;
                  this.field_85192_a.func_175656_a(new BlockPos(var11, var12, var13), var14 ? Blocks.field_150343_Z.func_176223_P() : Blocks.field_150350_a.func_176223_P());
               }
            }
         }

         var1.func_70012_b((double)var3, (double)var4, (double)var5, var1.field_70177_z, 0.0F);
         var1.field_70159_w = 0.0D;
         var1.field_70181_x = 0.0D;
         var1.field_70179_y = 0.0D;
      }
   }

   public boolean func_180620_b(Entity var1, float var2) {
      boolean var3 = true;
      double var4 = -1.0D;
      int var6 = MathHelper.func_76128_c(var1.field_70165_t);
      int var7 = MathHelper.func_76128_c(var1.field_70161_v);
      boolean var8 = true;
      Object var9 = BlockPos.field_177992_a;
      long var10 = ChunkPos.func_77272_a(var6, var7);
      if (this.field_85191_c.containsKey(var10)) {
         Teleporter.PortalPosition var12 = (Teleporter.PortalPosition)this.field_85191_c.get(var10);
         var4 = 0.0D;
         var9 = var12;
         var12.field_85087_d = this.field_85192_a.func_82737_E();
         var8 = false;
      } else {
         BlockPos var30 = new BlockPos(var1);

         for(int var13 = -128; var13 <= 128; ++var13) {
            BlockPos var16;
            for(int var14 = -128; var14 <= 128; ++var14) {
               for(BlockPos var15 = var30.func_177982_a(var13, this.field_85192_a.func_72940_L() - 1 - var30.func_177956_o(), var14); var15.func_177956_o() >= 0; var15 = var16) {
                  var16 = var15.func_177977_b();
                  if (this.field_85192_a.func_180495_p(var15).func_177230_c() == field_196236_a) {
                     for(var16 = var15.func_177977_b(); this.field_85192_a.func_180495_p(var16).func_177230_c() == field_196236_a; var16 = var16.func_177977_b()) {
                        var15 = var16;
                     }

                     double var17 = var15.func_177951_i(var30);
                     if (var4 < 0.0D || var17 < var4) {
                        var4 = var17;
                        var9 = var15;
                     }
                  }
               }
            }
         }
      }

      if (var4 >= 0.0D) {
         if (var8) {
            this.field_85191_c.put(var10, new Teleporter.PortalPosition((BlockPos)var9, this.field_85192_a.func_82737_E()));
         }

         double var31 = (double)((BlockPos)var9).func_177958_n() + 0.5D;
         double var33 = (double)((BlockPos)var9).func_177952_p() + 0.5D;
         BlockPattern.PatternHelper var18 = field_196236_a.func_181089_f(this.field_85192_a, (BlockPos)var9);
         boolean var19 = var18.func_177669_b().func_176746_e().func_176743_c() == EnumFacing.AxisDirection.NEGATIVE;
         double var20 = var18.func_177669_b().func_176740_k() == EnumFacing.Axis.X ? (double)var18.func_181117_a().func_177952_p() : (double)var18.func_181117_a().func_177958_n();
         double var32 = (double)(var18.func_181117_a().func_177956_o() + 1) - var1.func_181014_aG().field_72448_b * (double)var18.func_181119_e();
         if (var19) {
            ++var20;
         }

         if (var18.func_177669_b().func_176740_k() == EnumFacing.Axis.X) {
            var33 = var20 + (1.0D - var1.func_181014_aG().field_72450_a) * (double)var18.func_181118_d() * (double)var18.func_177669_b().func_176746_e().func_176743_c().func_179524_a();
         } else {
            var31 = var20 + (1.0D - var1.func_181014_aG().field_72450_a) * (double)var18.func_181118_d() * (double)var18.func_177669_b().func_176746_e().func_176743_c().func_179524_a();
         }

         float var22 = 0.0F;
         float var23 = 0.0F;
         float var24 = 0.0F;
         float var25 = 0.0F;
         if (var18.func_177669_b().func_176734_d() == var1.func_181012_aH()) {
            var22 = 1.0F;
            var23 = 1.0F;
         } else if (var18.func_177669_b().func_176734_d() == var1.func_181012_aH().func_176734_d()) {
            var22 = -1.0F;
            var23 = -1.0F;
         } else if (var18.func_177669_b().func_176734_d() == var1.func_181012_aH().func_176746_e()) {
            var24 = 1.0F;
            var25 = -1.0F;
         } else {
            var24 = -1.0F;
            var25 = 1.0F;
         }

         double var26 = var1.field_70159_w;
         double var28 = var1.field_70179_y;
         var1.field_70159_w = var26 * (double)var22 + var28 * (double)var25;
         var1.field_70179_y = var26 * (double)var24 + var28 * (double)var23;
         var1.field_70177_z = var2 - (float)(var1.func_181012_aH().func_176734_d().func_176736_b() * 90) + (float)(var18.func_177669_b().func_176736_b() * 90);
         if (var1 instanceof EntityPlayerMP) {
            ((EntityPlayerMP)var1).field_71135_a.func_147364_a(var31, var32, var33, var1.field_70177_z, var1.field_70125_A);
            ((EntityPlayerMP)var1).field_71135_a.func_184342_d();
         } else {
            var1.func_70012_b(var31, var32, var33, var1.field_70177_z, var1.field_70125_A);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean func_85188_a(Entity var1) {
      boolean var2 = true;
      double var3 = -1.0D;
      int var5 = MathHelper.func_76128_c(var1.field_70165_t);
      int var6 = MathHelper.func_76128_c(var1.field_70163_u);
      int var7 = MathHelper.func_76128_c(var1.field_70161_v);
      int var8 = var5;
      int var9 = var6;
      int var10 = var7;
      int var11 = 0;
      int var12 = this.field_77187_a.nextInt(4);
      BlockPos.MutableBlockPos var13 = new BlockPos.MutableBlockPos();

      int var14;
      double var15;
      int var17;
      double var18;
      int var20;
      int var21;
      int var22;
      int var23;
      int var24;
      int var25;
      int var26;
      int var27;
      int var28;
      double var33;
      double var34;
      for(var14 = var5 - 16; var14 <= var5 + 16; ++var14) {
         var15 = (double)var14 + 0.5D - var1.field_70165_t;

         for(var17 = var7 - 16; var17 <= var7 + 16; ++var17) {
            var18 = (double)var17 + 0.5D - var1.field_70161_v;

            label276:
            for(var20 = this.field_85192_a.func_72940_L() - 1; var20 >= 0; --var20) {
               if (this.field_85192_a.func_175623_d(var13.func_181079_c(var14, var20, var17))) {
                  while(var20 > 0 && this.field_85192_a.func_175623_d(var13.func_181079_c(var14, var20 - 1, var17))) {
                     --var20;
                  }

                  for(var21 = var12; var21 < var12 + 4; ++var21) {
                     var22 = var21 % 2;
                     var23 = 1 - var22;
                     if (var21 % 4 >= 2) {
                        var22 = -var22;
                        var23 = -var23;
                     }

                     for(var24 = 0; var24 < 3; ++var24) {
                        for(var25 = 0; var25 < 4; ++var25) {
                           for(var26 = -1; var26 < 4; ++var26) {
                              var27 = var14 + (var25 - 1) * var22 + var24 * var23;
                              var28 = var20 + var26;
                              int var29 = var17 + (var25 - 1) * var23 - var24 * var22;
                              var13.func_181079_c(var27, var28, var29);
                              if (var26 < 0 && !this.field_85192_a.func_180495_p(var13).func_185904_a().func_76220_a() || var26 >= 0 && !this.field_85192_a.func_175623_d(var13)) {
                                 continue label276;
                              }
                           }
                        }
                     }

                     var33 = (double)var20 + 0.5D - var1.field_70163_u;
                     var34 = var15 * var15 + var33 * var33 + var18 * var18;
                     if (var3 < 0.0D || var34 < var3) {
                        var3 = var34;
                        var8 = var14;
                        var9 = var20;
                        var10 = var17;
                        var11 = var21 % 4;
                     }
                  }
               }
            }
         }
      }

      if (var3 < 0.0D) {
         for(var14 = var5 - 16; var14 <= var5 + 16; ++var14) {
            var15 = (double)var14 + 0.5D - var1.field_70165_t;

            for(var17 = var7 - 16; var17 <= var7 + 16; ++var17) {
               var18 = (double)var17 + 0.5D - var1.field_70161_v;

               label214:
               for(var20 = this.field_85192_a.func_72940_L() - 1; var20 >= 0; --var20) {
                  if (this.field_85192_a.func_175623_d(var13.func_181079_c(var14, var20, var17))) {
                     while(var20 > 0 && this.field_85192_a.func_175623_d(var13.func_181079_c(var14, var20 - 1, var17))) {
                        --var20;
                     }

                     for(var21 = var12; var21 < var12 + 2; ++var21) {
                        var22 = var21 % 2;
                        var23 = 1 - var22;

                        for(var24 = 0; var24 < 4; ++var24) {
                           for(var25 = -1; var25 < 4; ++var25) {
                              var26 = var14 + (var24 - 1) * var22;
                              var27 = var20 + var25;
                              var28 = var17 + (var24 - 1) * var23;
                              var13.func_181079_c(var26, var27, var28);
                              if (var25 < 0 && !this.field_85192_a.func_180495_p(var13).func_185904_a().func_76220_a() || var25 >= 0 && !this.field_85192_a.func_175623_d(var13)) {
                                 continue label214;
                              }
                           }
                        }

                        var33 = (double)var20 + 0.5D - var1.field_70163_u;
                        var34 = var15 * var15 + var33 * var33 + var18 * var18;
                        if (var3 < 0.0D || var34 < var3) {
                           var3 = var34;
                           var8 = var14;
                           var9 = var20;
                           var10 = var17;
                           var11 = var21 % 2;
                        }
                     }
                  }
               }
            }
         }
      }

      int var30 = var8;
      int var16 = var9;
      var17 = var10;
      int var31 = var11 % 2;
      int var19 = 1 - var31;
      if (var11 % 4 >= 2) {
         var31 = -var31;
         var19 = -var19;
      }

      if (var3 < 0.0D) {
         var9 = MathHelper.func_76125_a(var9, 70, this.field_85192_a.func_72940_L() - 10);
         var16 = var9;

         for(var20 = -1; var20 <= 1; ++var20) {
            for(var21 = 1; var21 < 3; ++var21) {
               for(var22 = -1; var22 < 3; ++var22) {
                  var23 = var30 + (var21 - 1) * var31 + var20 * var19;
                  var24 = var16 + var22;
                  var25 = var17 + (var21 - 1) * var19 - var20 * var31;
                  boolean var35 = var22 < 0;
                  var13.func_181079_c(var23, var24, var25);
                  this.field_85192_a.func_175656_a(var13, var35 ? Blocks.field_150343_Z.func_176223_P() : Blocks.field_150350_a.func_176223_P());
               }
            }
         }
      }

      for(var20 = -1; var20 < 3; ++var20) {
         for(var21 = -1; var21 < 4; ++var21) {
            if (var20 == -1 || var20 == 2 || var21 == -1 || var21 == 3) {
               var13.func_181079_c(var30 + var20 * var31, var16 + var21, var17 + var20 * var19);
               this.field_85192_a.func_180501_a(var13, Blocks.field_150343_Z.func_176223_P(), 3);
            }
         }
      }

      IBlockState var32 = (IBlockState)field_196236_a.func_176223_P().func_206870_a(BlockPortal.field_176550_a, var31 == 0 ? EnumFacing.Axis.Z : EnumFacing.Axis.X);

      for(var21 = 0; var21 < 2; ++var21) {
         for(var22 = 0; var22 < 3; ++var22) {
            var13.func_181079_c(var30 + var21 * var31, var16 + var22, var17 + var21 * var19);
            this.field_85192_a.func_180501_a(var13, var32, 18);
         }
      }

      return true;
   }

   public void func_85189_a(long var1) {
      if (var1 % 100L == 0L) {
         long var3 = var1 - 300L;
         ObjectIterator var5 = this.field_85191_c.values().iterator();

         while(true) {
            Teleporter.PortalPosition var6;
            do {
               if (!var5.hasNext()) {
                  return;
               }

               var6 = (Teleporter.PortalPosition)var5.next();
            } while(var6 != null && var6.field_85087_d >= var3);

            var5.remove();
         }
      }
   }

   static {
      field_196236_a = (BlockPortal)Blocks.field_150427_aO;
   }

   public class PortalPosition extends BlockPos {
      public long field_85087_d;

      public PortalPosition(BlockPos var2, long var3) {
         super(var2.func_177958_n(), var2.func_177956_o(), var2.func_177952_p());
         this.field_85087_d = var3;
      }
   }
}
