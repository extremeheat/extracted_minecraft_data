package net.minecraft.block;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.EndDimension;

public class BlockFire extends Block {
   public static final IntegerProperty field_176543_a;
   public static final BooleanProperty field_176545_N;
   public static final BooleanProperty field_176546_O;
   public static final BooleanProperty field_176541_P;
   public static final BooleanProperty field_176539_Q;
   public static final BooleanProperty field_176542_R;
   private static final Map<EnumFacing, BooleanProperty> field_196449_B;
   private final Object2IntMap<Block> field_149849_a = new Object2IntOpenHashMap();
   private final Object2IntMap<Block> field_149848_b = new Object2IntOpenHashMap();

   protected BlockFire(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176543_a, 0)).func_206870_a(field_176545_N, false)).func_206870_a(field_176546_O, false)).func_206870_a(field_176541_P, false)).func_206870_a(field_176539_Q, false)).func_206870_a(field_176542_R, false));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return VoxelShapes.func_197880_a();
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return this.func_196260_a(var1, var4, var5) ? (IBlockState)this.func_196448_a(var4, var5).func_206870_a(field_176543_a, var1.func_177229_b(field_176543_a)) : Blocks.field_150350_a.func_176223_P();
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return this.func_196448_a(var1.func_195991_k(), var1.func_195995_a());
   }

   public IBlockState func_196448_a(IBlockReader var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2.func_177977_b());
      if (!var3.func_185896_q() && !this.func_196446_i(var3)) {
         IBlockState var4 = this.func_176223_P();
         EnumFacing[] var5 = EnumFacing.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            EnumFacing var8 = var5[var7];
            BooleanProperty var9 = (BooleanProperty)field_196449_B.get(var8);
            if (var9 != null) {
               var4 = (IBlockState)var4.func_206870_a(var9, this.func_196446_i(var1.func_180495_p(var2.func_177972_a(var8))));
            }
         }

         return var4;
      } else {
         return this.func_176223_P();
      }
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return var2.func_180495_p(var3.func_177977_b()).func_185896_q() || this.func_196447_a(var2, var3);
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 0;
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 30;
   }

   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var2.func_82736_K().func_82766_b("doFireTick")) {
         if (!var1.func_196955_c(var2, var3)) {
            var2.func_175698_g(var3);
         }

         Block var5 = var2.func_180495_p(var3.func_177977_b()).func_177230_c();
         boolean var6 = var2.field_73011_w instanceof EndDimension && var5 == Blocks.field_150357_h || var5 == Blocks.field_150424_aL || var5 == Blocks.field_196814_hQ;
         int var7 = (Integer)var1.func_177229_b(field_176543_a);
         if (!var6 && var2.func_72896_J() && this.func_176537_d(var2, var3) && var4.nextFloat() < 0.2F + (float)var7 * 0.03F) {
            var2.func_175698_g(var3);
         } else {
            int var8 = Math.min(15, var7 + var4.nextInt(3) / 2);
            if (var7 != var8) {
               var1 = (IBlockState)var1.func_206870_a(field_176543_a, var8);
               var2.func_180501_a(var3, var1, 4);
            }

            if (!var6) {
               var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2) + var4.nextInt(10));
               if (!this.func_196447_a(var2, var3)) {
                  if (!var2.func_180495_p(var3.func_177977_b()).func_185896_q() || var7 > 3) {
                     var2.func_175698_g(var3);
                  }

                  return;
               }

               if (var7 == 15 && var4.nextInt(4) == 0 && !this.func_196446_i(var2.func_180495_p(var3.func_177977_b()))) {
                  var2.func_175698_g(var3);
                  return;
               }
            }

            boolean var9 = var2.func_180502_D(var3);
            int var10 = var9 ? -50 : 0;
            this.func_176536_a(var2, var3.func_177974_f(), 300 + var10, var4, var7);
            this.func_176536_a(var2, var3.func_177976_e(), 300 + var10, var4, var7);
            this.func_176536_a(var2, var3.func_177977_b(), 250 + var10, var4, var7);
            this.func_176536_a(var2, var3.func_177984_a(), 250 + var10, var4, var7);
            this.func_176536_a(var2, var3.func_177978_c(), 300 + var10, var4, var7);
            this.func_176536_a(var2, var3.func_177968_d(), 300 + var10, var4, var7);
            BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

            for(int var12 = -1; var12 <= 1; ++var12) {
               for(int var13 = -1; var13 <= 1; ++var13) {
                  for(int var14 = -1; var14 <= 4; ++var14) {
                     if (var12 != 0 || var14 != 0 || var13 != 0) {
                        int var15 = 100;
                        if (var14 > 1) {
                           var15 += (var14 - 1) * 100;
                        }

                        var11.func_189533_g(var3).func_196234_d(var12, var14, var13);
                        int var16 = this.func_176538_m(var2, var11);
                        if (var16 > 0) {
                           int var17 = (var16 + 40 + var2.func_175659_aa().func_151525_a() * 7) / (var7 + 30);
                           if (var9) {
                              var17 /= 2;
                           }

                           if (var17 > 0 && var4.nextInt(var15) <= var17 && (!var2.func_72896_J() || !this.func_176537_d(var2, var11))) {
                              int var18 = Math.min(15, var7 + var4.nextInt(5) / 4);
                              var2.func_180501_a(var11, (IBlockState)this.func_196448_a(var2, var11).func_206870_a(field_176543_a, var18), 3);
                           }
                        }
                     }
                  }
               }
            }

         }
      }
   }

   protected boolean func_176537_d(World var1, BlockPos var2) {
      return var1.func_175727_C(var2) || var1.func_175727_C(var2.func_177976_e()) || var1.func_175727_C(var2.func_177974_f()) || var1.func_175727_C(var2.func_177978_c()) || var1.func_175727_C(var2.func_177968_d());
   }

   private int func_176532_c(Block var1) {
      return this.field_149848_b.getInt(var1);
   }

   private int func_176534_d(Block var1) {
      return this.field_149849_a.getInt(var1);
   }

   private void func_176536_a(World var1, BlockPos var2, int var3, Random var4, int var5) {
      int var6 = this.func_176532_c(var1.func_180495_p(var2).func_177230_c());
      if (var4.nextInt(var3) < var6) {
         IBlockState var7 = var1.func_180495_p(var2);
         if (var4.nextInt(var5 + 10) < 5 && !var1.func_175727_C(var2)) {
            int var8 = Math.min(var5 + var4.nextInt(5) / 4, 15);
            var1.func_180501_a(var2, (IBlockState)this.func_196448_a(var1, var2).func_206870_a(field_176543_a, var8), 3);
         } else {
            var1.func_175698_g(var2);
         }

         Block var9 = var7.func_177230_c();
         if (var9 instanceof BlockTNT) {
            ((BlockTNT)var9).func_196534_a(var1, var2);
         }
      }

   }

   private boolean func_196447_a(IBlockReader var1, BlockPos var2) {
      EnumFacing[] var3 = EnumFacing.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         EnumFacing var6 = var3[var5];
         if (this.func_196446_i(var1.func_180495_p(var2.func_177972_a(var6)))) {
            return true;
         }
      }

      return false;
   }

   private int func_176538_m(IWorldReaderBase var1, BlockPos var2) {
      if (!var1.func_175623_d(var2)) {
         return 0;
      } else {
         int var3 = 0;
         EnumFacing[] var4 = EnumFacing.values();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            EnumFacing var7 = var4[var6];
            var3 = Math.max(this.func_176534_d(var1.func_180495_p(var2.func_177972_a(var7)).func_177230_c()), var3);
         }

         return var3;
      }
   }

   public boolean func_149703_v() {
      return false;
   }

   public boolean func_196446_i(IBlockState var1) {
      return this.func_176534_d(var1.func_177230_c()) > 0;
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c()) {
         if (var2.field_73011_w.func_186058_p() != DimensionType.OVERWORLD && var2.field_73011_w.func_186058_p() != DimensionType.NETHER || !((BlockPortal)Blocks.field_150427_aO).func_176548_d(var2, var3)) {
            if (!var1.func_196955_c(var2, var3)) {
               var2.func_175698_g(var3);
            } else {
               var2.func_205220_G_().func_205360_a(var3, this, this.func_149738_a(var2) + var2.field_73012_v.nextInt(10));
            }
         }
      }
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      if (var4.nextInt(24) == 0) {
         var2.func_184134_a((double)((float)var3.func_177958_n() + 0.5F), (double)((float)var3.func_177956_o() + 0.5F), (double)((float)var3.func_177952_p() + 0.5F), SoundEvents.field_187643_bs, SoundCategory.BLOCKS, 1.0F + var4.nextFloat(), var4.nextFloat() * 0.7F + 0.3F, false);
      }

      int var5;
      double var6;
      double var8;
      double var10;
      if (!var2.func_180495_p(var3.func_177977_b()).func_185896_q() && !this.func_196446_i(var2.func_180495_p(var3.func_177977_b()))) {
         if (this.func_196446_i(var2.func_180495_p(var3.func_177976_e()))) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)var3.func_177958_n() + var4.nextDouble() * 0.10000000149011612D;
               var8 = (double)var3.func_177956_o() + var4.nextDouble();
               var10 = (double)var3.func_177952_p() + var4.nextDouble();
               var2.func_195594_a(Particles.field_197594_E, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_196446_i(var2.func_180495_p(var3.func_177974_f()))) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)(var3.func_177958_n() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var8 = (double)var3.func_177956_o() + var4.nextDouble();
               var10 = (double)var3.func_177952_p() + var4.nextDouble();
               var2.func_195594_a(Particles.field_197594_E, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_196446_i(var2.func_180495_p(var3.func_177978_c()))) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)var3.func_177958_n() + var4.nextDouble();
               var8 = (double)var3.func_177956_o() + var4.nextDouble();
               var10 = (double)var3.func_177952_p() + var4.nextDouble() * 0.10000000149011612D;
               var2.func_195594_a(Particles.field_197594_E, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_196446_i(var2.func_180495_p(var3.func_177968_d()))) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)var3.func_177958_n() + var4.nextDouble();
               var8 = (double)var3.func_177956_o() + var4.nextDouble();
               var10 = (double)(var3.func_177952_p() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var2.func_195594_a(Particles.field_197594_E, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }

         if (this.func_196446_i(var2.func_180495_p(var3.func_177984_a()))) {
            for(var5 = 0; var5 < 2; ++var5) {
               var6 = (double)var3.func_177958_n() + var4.nextDouble();
               var8 = (double)(var3.func_177956_o() + 1) - var4.nextDouble() * 0.10000000149011612D;
               var10 = (double)var3.func_177952_p() + var4.nextDouble();
               var2.func_195594_a(Particles.field_197594_E, var6, var8, var10, 0.0D, 0.0D, 0.0D);
            }
         }
      } else {
         for(var5 = 0; var5 < 3; ++var5) {
            var6 = (double)var3.func_177958_n() + var4.nextDouble();
            var8 = (double)var3.func_177956_o() + var4.nextDouble() * 0.5D + 0.5D;
            var10 = (double)var3.func_177952_p() + var4.nextDouble();
            var2.func_195594_a(Particles.field_197594_E, var6, var8, var10, 0.0D, 0.0D, 0.0D);
         }
      }

   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176543_a, field_176545_N, field_176546_O, field_176541_P, field_176539_Q, field_176542_R);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   public void func_180686_a(Block var1, int var2, int var3) {
      this.field_149849_a.put(var1, var2);
      this.field_149848_b.put(var1, var3);
   }

   public static void func_149843_e() {
      BlockFire var0 = (BlockFire)Blocks.field_150480_ab;
      var0.func_180686_a(Blocks.field_196662_n, 5, 20);
      var0.func_180686_a(Blocks.field_196664_o, 5, 20);
      var0.func_180686_a(Blocks.field_196666_p, 5, 20);
      var0.func_180686_a(Blocks.field_196668_q, 5, 20);
      var0.func_180686_a(Blocks.field_196670_r, 5, 20);
      var0.func_180686_a(Blocks.field_196672_s, 5, 20);
      var0.func_180686_a(Blocks.field_196622_bq, 5, 20);
      var0.func_180686_a(Blocks.field_196624_br, 5, 20);
      var0.func_180686_a(Blocks.field_196627_bs, 5, 20);
      var0.func_180686_a(Blocks.field_196630_bt, 5, 20);
      var0.func_180686_a(Blocks.field_196632_bu, 5, 20);
      var0.func_180686_a(Blocks.field_196635_bv, 5, 20);
      var0.func_180686_a(Blocks.field_180390_bo, 5, 20);
      var0.func_180686_a(Blocks.field_180391_bp, 5, 20);
      var0.func_180686_a(Blocks.field_180392_bq, 5, 20);
      var0.func_180686_a(Blocks.field_180386_br, 5, 20);
      var0.func_180686_a(Blocks.field_180385_bs, 5, 20);
      var0.func_180686_a(Blocks.field_180387_bt, 5, 20);
      var0.func_180686_a(Blocks.field_180407_aO, 5, 20);
      var0.func_180686_a(Blocks.field_180408_aP, 5, 20);
      var0.func_180686_a(Blocks.field_180404_aQ, 5, 20);
      var0.func_180686_a(Blocks.field_180403_aR, 5, 20);
      var0.func_180686_a(Blocks.field_180406_aS, 5, 20);
      var0.func_180686_a(Blocks.field_180405_aT, 5, 20);
      var0.func_180686_a(Blocks.field_150476_ad, 5, 20);
      var0.func_180686_a(Blocks.field_150487_bG, 5, 20);
      var0.func_180686_a(Blocks.field_150485_bF, 5, 20);
      var0.func_180686_a(Blocks.field_150481_bH, 5, 20);
      var0.func_180686_a(Blocks.field_150400_ck, 5, 20);
      var0.func_180686_a(Blocks.field_150401_cl, 5, 20);
      var0.func_180686_a(Blocks.field_196617_K, 5, 5);
      var0.func_180686_a(Blocks.field_196618_L, 5, 5);
      var0.func_180686_a(Blocks.field_196619_M, 5, 5);
      var0.func_180686_a(Blocks.field_196620_N, 5, 5);
      var0.func_180686_a(Blocks.field_196621_O, 5, 5);
      var0.func_180686_a(Blocks.field_196623_P, 5, 5);
      var0.func_180686_a(Blocks.field_203204_R, 5, 5);
      var0.func_180686_a(Blocks.field_203205_S, 5, 5);
      var0.func_180686_a(Blocks.field_203206_T, 5, 5);
      var0.func_180686_a(Blocks.field_203207_U, 5, 5);
      var0.func_180686_a(Blocks.field_203208_V, 5, 5);
      var0.func_180686_a(Blocks.field_203209_W, 5, 5);
      var0.func_180686_a(Blocks.field_209389_ab, 5, 5);
      var0.func_180686_a(Blocks.field_209390_ac, 5, 5);
      var0.func_180686_a(Blocks.field_209391_ad, 5, 5);
      var0.func_180686_a(Blocks.field_209392_ae, 5, 5);
      var0.func_180686_a(Blocks.field_209393_af, 5, 5);
      var0.func_180686_a(Blocks.field_209394_ag, 5, 5);
      var0.func_180686_a(Blocks.field_196626_Q, 5, 5);
      var0.func_180686_a(Blocks.field_196629_R, 5, 5);
      var0.func_180686_a(Blocks.field_196631_S, 5, 5);
      var0.func_180686_a(Blocks.field_196634_T, 5, 5);
      var0.func_180686_a(Blocks.field_196637_U, 5, 5);
      var0.func_180686_a(Blocks.field_196639_V, 5, 5);
      var0.func_180686_a(Blocks.field_196642_W, 30, 60);
      var0.func_180686_a(Blocks.field_196645_X, 30, 60);
      var0.func_180686_a(Blocks.field_196647_Y, 30, 60);
      var0.func_180686_a(Blocks.field_196648_Z, 30, 60);
      var0.func_180686_a(Blocks.field_196572_aa, 30, 60);
      var0.func_180686_a(Blocks.field_196574_ab, 30, 60);
      var0.func_180686_a(Blocks.field_150342_X, 30, 20);
      var0.func_180686_a(Blocks.field_150335_W, 15, 100);
      var0.func_180686_a(Blocks.field_150349_c, 60, 100);
      var0.func_180686_a(Blocks.field_196554_aH, 60, 100);
      var0.func_180686_a(Blocks.field_196555_aI, 60, 100);
      var0.func_180686_a(Blocks.field_196800_gd, 60, 100);
      var0.func_180686_a(Blocks.field_196801_ge, 60, 100);
      var0.func_180686_a(Blocks.field_196802_gf, 60, 100);
      var0.func_180686_a(Blocks.field_196803_gg, 60, 100);
      var0.func_180686_a(Blocks.field_196804_gh, 60, 100);
      var0.func_180686_a(Blocks.field_196805_gi, 60, 100);
      var0.func_180686_a(Blocks.field_196605_bc, 60, 100);
      var0.func_180686_a(Blocks.field_196606_bd, 60, 100);
      var0.func_180686_a(Blocks.field_196607_be, 60, 100);
      var0.func_180686_a(Blocks.field_196609_bf, 60, 100);
      var0.func_180686_a(Blocks.field_196610_bg, 60, 100);
      var0.func_180686_a(Blocks.field_196612_bh, 60, 100);
      var0.func_180686_a(Blocks.field_196613_bi, 60, 100);
      var0.func_180686_a(Blocks.field_196614_bj, 60, 100);
      var0.func_180686_a(Blocks.field_196615_bk, 60, 100);
      var0.func_180686_a(Blocks.field_196616_bl, 60, 100);
      var0.func_180686_a(Blocks.field_196556_aL, 30, 60);
      var0.func_180686_a(Blocks.field_196557_aM, 30, 60);
      var0.func_180686_a(Blocks.field_196558_aN, 30, 60);
      var0.func_180686_a(Blocks.field_196559_aO, 30, 60);
      var0.func_180686_a(Blocks.field_196560_aP, 30, 60);
      var0.func_180686_a(Blocks.field_196561_aQ, 30, 60);
      var0.func_180686_a(Blocks.field_196562_aR, 30, 60);
      var0.func_180686_a(Blocks.field_196563_aS, 30, 60);
      var0.func_180686_a(Blocks.field_196564_aT, 30, 60);
      var0.func_180686_a(Blocks.field_196565_aU, 30, 60);
      var0.func_180686_a(Blocks.field_196566_aV, 30, 60);
      var0.func_180686_a(Blocks.field_196567_aW, 30, 60);
      var0.func_180686_a(Blocks.field_196568_aX, 30, 60);
      var0.func_180686_a(Blocks.field_196569_aY, 30, 60);
      var0.func_180686_a(Blocks.field_196570_aZ, 30, 60);
      var0.func_180686_a(Blocks.field_196602_ba, 30, 60);
      var0.func_180686_a(Blocks.field_150395_bd, 15, 100);
      var0.func_180686_a(Blocks.field_150402_ci, 5, 5);
      var0.func_180686_a(Blocks.field_150407_cf, 60, 20);
      var0.func_180686_a(Blocks.field_196724_fH, 60, 20);
      var0.func_180686_a(Blocks.field_196725_fI, 60, 20);
      var0.func_180686_a(Blocks.field_196727_fJ, 60, 20);
      var0.func_180686_a(Blocks.field_196729_fK, 60, 20);
      var0.func_180686_a(Blocks.field_196731_fL, 60, 20);
      var0.func_180686_a(Blocks.field_196733_fM, 60, 20);
      var0.func_180686_a(Blocks.field_196735_fN, 60, 20);
      var0.func_180686_a(Blocks.field_196737_fO, 60, 20);
      var0.func_180686_a(Blocks.field_196739_fP, 60, 20);
      var0.func_180686_a(Blocks.field_196741_fQ, 60, 20);
      var0.func_180686_a(Blocks.field_196743_fR, 60, 20);
      var0.func_180686_a(Blocks.field_196745_fS, 60, 20);
      var0.func_180686_a(Blocks.field_196747_fT, 60, 20);
      var0.func_180686_a(Blocks.field_196749_fU, 60, 20);
      var0.func_180686_a(Blocks.field_196751_fV, 60, 20);
      var0.func_180686_a(Blocks.field_196753_fW, 60, 20);
      var0.func_180686_a(Blocks.field_203216_jz, 30, 60);
   }

   static {
      field_176543_a = BlockStateProperties.field_208171_X;
      field_176545_N = BlockSixWay.field_196488_a;
      field_176546_O = BlockSixWay.field_196490_b;
      field_176541_P = BlockSixWay.field_196492_c;
      field_176539_Q = BlockSixWay.field_196495_y;
      field_176542_R = BlockSixWay.field_196496_z;
      field_196449_B = (Map)BlockSixWay.field_196491_B.entrySet().stream().filter((var0) -> {
         return var0.getKey() != EnumFacing.DOWN;
      }).collect(Util.func_199749_a());
   }
}
