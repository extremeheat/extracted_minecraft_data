package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {
   public static final EnumProperty<RedstoneSide> field_176348_a;
   public static final EnumProperty<RedstoneSide> field_176347_b;
   public static final EnumProperty<RedstoneSide> field_176349_M;
   public static final EnumProperty<RedstoneSide> field_176350_N;
   public static final IntegerProperty field_176351_O;
   public static final Map<EnumFacing, EnumProperty<RedstoneSide>> field_196498_A;
   protected static final VoxelShape[] field_196499_B;
   private boolean field_150181_a = true;
   private final Set<BlockPos> field_150179_b = Sets.newHashSet();

   public BlockRedstoneWire(Block.Properties var1) {
      super(var1);
      this.func_180632_j((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)((IBlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176348_a, RedstoneSide.NONE)).func_206870_a(field_176347_b, RedstoneSide.NONE)).func_206870_a(field_176349_M, RedstoneSide.NONE)).func_206870_a(field_176350_N, RedstoneSide.NONE)).func_206870_a(field_176351_O, 0));
   }

   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return field_196499_B[func_185699_x(var1)];
   }

   private static int func_185699_x(IBlockState var0) {
      int var1 = 0;
      boolean var2 = var0.func_177229_b(field_176348_a) != RedstoneSide.NONE;
      boolean var3 = var0.func_177229_b(field_176347_b) != RedstoneSide.NONE;
      boolean var4 = var0.func_177229_b(field_176349_M) != RedstoneSide.NONE;
      boolean var5 = var0.func_177229_b(field_176350_N) != RedstoneSide.NONE;
      if (var2 || var4 && !var2 && !var3 && !var5) {
         var1 |= 1 << EnumFacing.NORTH.func_176736_b();
      }

      if (var3 || var5 && !var2 && !var3 && !var4) {
         var1 |= 1 << EnumFacing.EAST.func_176736_b();
      }

      if (var4 || var2 && !var3 && !var4 && !var5) {
         var1 |= 1 << EnumFacing.SOUTH.func_176736_b();
      }

      if (var5 || var3 && !var2 && !var4 && !var5) {
         var1 |= 1 << EnumFacing.WEST.func_176736_b();
      }

      return var1;
   }

   public IBlockState func_196258_a(BlockItemUseContext var1) {
      World var2 = var1.func_195991_k();
      BlockPos var3 = var1.func_195995_a();
      return (IBlockState)((IBlockState)((IBlockState)((IBlockState)this.func_176223_P().func_206870_a(field_176350_N, this.func_208074_a(var2, var3, EnumFacing.WEST))).func_206870_a(field_176347_b, this.func_208074_a(var2, var3, EnumFacing.EAST))).func_206870_a(field_176348_a, this.func_208074_a(var2, var3, EnumFacing.NORTH))).func_206870_a(field_176349_M, this.func_208074_a(var2, var3, EnumFacing.SOUTH));
   }

   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      if (var2 == EnumFacing.DOWN) {
         return var1;
      } else {
         return var2 == EnumFacing.UP ? (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176350_N, this.func_208074_a(var4, var5, EnumFacing.WEST))).func_206870_a(field_176347_b, this.func_208074_a(var4, var5, EnumFacing.EAST))).func_206870_a(field_176348_a, this.func_208074_a(var4, var5, EnumFacing.NORTH))).func_206870_a(field_176349_M, this.func_208074_a(var4, var5, EnumFacing.SOUTH)) : (IBlockState)var1.func_206870_a((IProperty)field_196498_A.get(var2), this.func_208074_a(var4, var5, var2));
      }
   }

   public void func_196248_b(IBlockState var1, IWorld var2, BlockPos var3, int var4) {
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var6 = null;

      try {
         Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var7.hasNext()) {
            EnumFacing var8 = (EnumFacing)var7.next();
            RedstoneSide var9 = (RedstoneSide)var1.func_177229_b((IProperty)field_196498_A.get(var8));
            if (var9 != RedstoneSide.NONE && var2.func_180495_p(var5.func_189533_g(var3).func_189536_c(var8)).func_177230_c() != this) {
               var5.func_189536_c(EnumFacing.DOWN);
               IBlockState var10 = var2.func_180495_p(var5);
               if (var10.func_177230_c() != Blocks.field_190976_dk) {
                  BlockPos var11 = var5.func_177972_a(var8.func_176734_d());
                  IBlockState var12 = var10.func_196956_a(var8.func_176734_d(), var2.func_180495_p(var11), var2, var5, var11);
                  func_196263_a(var10, var12, var2, var5, var4);
               }

               var5.func_189533_g(var3).func_189536_c(var8).func_189536_c(EnumFacing.UP);
               IBlockState var23 = var2.func_180495_p(var5);
               if (var23.func_177230_c() != Blocks.field_190976_dk) {
                  BlockPos var24 = var5.func_177972_a(var8.func_176734_d());
                  IBlockState var13 = var23.func_196956_a(var8.func_176734_d(), var2.func_180495_p(var24), var2, var5, var24);
                  func_196263_a(var23, var13, var2, var5, var4);
               }
            }
         }
      } catch (Throwable var21) {
         var6 = var21;
         throw var21;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var20) {
                  var6.addSuppressed(var20);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   private RedstoneSide func_208074_a(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      BlockPos var4 = var2.func_177972_a(var3);
      IBlockState var5 = var1.func_180495_p(var2.func_177972_a(var3));
      IBlockState var6 = var1.func_180495_p(var2.func_177984_a());
      if (!var6.func_185915_l()) {
         boolean var7 = var1.func_180495_p(var4).func_185896_q() || var1.func_180495_p(var4).func_177230_c() == Blocks.field_150426_aN;
         if (var7 && func_176346_d(var1.func_180495_p(var4.func_177984_a()))) {
            if (var5.func_185898_k()) {
               return RedstoneSide.UP;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !func_176343_a(var1.func_180495_p(var4), var3) && (var5.func_185915_l() || !func_176346_d(var1.func_180495_p(var4.func_177977_b()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   public boolean func_149686_d(IBlockState var1) {
      return false;
   }

   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      IBlockState var4 = var2.func_180495_p(var3.func_177977_b());
      return var4.func_185896_q() || var4.func_177230_c() == Blocks.field_150426_aN;
   }

   private IBlockState func_176338_e(World var1, BlockPos var2, IBlockState var3) {
      var3 = this.func_212568_b(var1, var2, var3);
      ArrayList var4 = Lists.newArrayList(this.field_150179_b);
      this.field_150179_b.clear();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         BlockPos var6 = (BlockPos)var5.next();
         var1.func_195593_d(var6, this);
      }

      return var3;
   }

   private IBlockState func_212568_b(World var1, BlockPos var2, IBlockState var3) {
      IBlockState var4 = var3;
      int var5 = (Integer)var3.func_177229_b(field_176351_O);
      byte var6 = 0;
      int var14 = this.func_212567_a(var6, var3);
      this.field_150181_a = false;
      int var7 = var1.func_175687_A(var2);
      this.field_150181_a = true;
      if (var7 > 0 && var7 > var14 - 1) {
         var14 = var7;
      }

      int var8 = 0;
      Iterator var9 = EnumFacing.Plane.HORIZONTAL.iterator();

      while(true) {
         while(var9.hasNext()) {
            EnumFacing var10 = (EnumFacing)var9.next();
            BlockPos var11 = var2.func_177972_a(var10);
            boolean var12 = var11.func_177958_n() != var2.func_177958_n() || var11.func_177952_p() != var2.func_177952_p();
            IBlockState var13 = var1.func_180495_p(var11);
            if (var12) {
               var8 = this.func_212567_a(var8, var13);
            }

            if (var13.func_185915_l() && !var1.func_180495_p(var2.func_177984_a()).func_185915_l()) {
               if (var12 && var2.func_177956_o() >= var2.func_177956_o()) {
                  var8 = this.func_212567_a(var8, var1.func_180495_p(var11.func_177984_a()));
               }
            } else if (!var13.func_185915_l() && var12 && var2.func_177956_o() <= var2.func_177956_o()) {
               var8 = this.func_212567_a(var8, var1.func_180495_p(var11.func_177977_b()));
            }
         }

         if (var8 > var14) {
            var14 = var8 - 1;
         } else if (var14 > 0) {
            --var14;
         } else {
            var14 = 0;
         }

         if (var7 > var14 - 1) {
            var14 = var7;
         }

         if (var5 != var14) {
            var3 = (IBlockState)var3.func_206870_a(field_176351_O, var14);
            if (var1.func_180495_p(var2) == var4) {
               var1.func_180501_a(var2, var3, 2);
            }

            this.field_150179_b.add(var2);
            EnumFacing[] var15 = EnumFacing.values();
            int var16 = var15.length;

            for(int var17 = 0; var17 < var16; ++var17) {
               EnumFacing var18 = var15[var17];
               this.field_150179_b.add(var2.func_177972_a(var18));
            }
         }

         return var3;
      }
   }

   private void func_176344_d(World var1, BlockPos var2) {
      if (var1.func_180495_p(var2).func_177230_c() == this) {
         var1.func_195593_d(var2, this);
         EnumFacing[] var3 = EnumFacing.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            EnumFacing var6 = var3[var5];
            var1.func_195593_d(var2.func_177972_a(var6), this);
         }

      }
   }

   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
      if (var4.func_177230_c() != var1.func_177230_c() && !var2.field_72995_K) {
         this.func_176338_e(var2, var3, var1);
         Iterator var5 = EnumFacing.Plane.VERTICAL.iterator();

         EnumFacing var6;
         while(var5.hasNext()) {
            var6 = (EnumFacing)var5.next();
            var2.func_195593_d(var3.func_177972_a(var6), this);
         }

         var5 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var5.hasNext()) {
            var6 = (EnumFacing)var5.next();
            this.func_176344_d(var2, var3.func_177972_a(var6));
         }

         var5 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var5.hasNext()) {
            var6 = (EnumFacing)var5.next();
            BlockPos var7 = var3.func_177972_a(var6);
            if (var2.func_180495_p(var7).func_185915_l()) {
               this.func_176344_d(var2, var7.func_177984_a());
            } else {
               this.func_176344_d(var2, var7.func_177977_b());
            }
         }

      }
   }

   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
      if (!var5 && var1.func_177230_c() != var4.func_177230_c()) {
         super.func_196243_a(var1, var2, var3, var4, var5);
         if (!var2.field_72995_K) {
            EnumFacing[] var6 = EnumFacing.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               EnumFacing var9 = var6[var8];
               var2.func_195593_d(var3.func_177972_a(var9), this);
            }

            this.func_176338_e(var2, var3, var1);
            Iterator var10 = EnumFacing.Plane.HORIZONTAL.iterator();

            EnumFacing var11;
            while(var10.hasNext()) {
               var11 = (EnumFacing)var10.next();
               this.func_176344_d(var2, var3.func_177972_a(var11));
            }

            var10 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var10.hasNext()) {
               var11 = (EnumFacing)var10.next();
               BlockPos var12 = var3.func_177972_a(var11);
               if (var2.func_180495_p(var12).func_185915_l()) {
                  this.func_176344_d(var2, var12.func_177984_a());
               } else {
                  this.func_176344_d(var2, var12.func_177977_b());
               }
            }

         }
      }
   }

   private int func_212567_a(int var1, IBlockState var2) {
      if (var2.func_177230_c() != this) {
         return var1;
      } else {
         int var3 = (Integer)var2.func_177229_b(field_176351_O);
         return var3 > var1 ? var3 : var1;
      }
   }

   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
      if (!var2.field_72995_K) {
         if (var1.func_196955_c(var2, var3)) {
            this.func_176338_e(var2, var3, var1);
         } else {
            var1.func_196949_c(var2, var3, 0);
            var2.func_175698_g(var3);
         }

      }
   }

   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return !this.field_150181_a ? 0 : var1.func_185911_a(var2, var3, var4);
   }

   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      if (!this.field_150181_a) {
         return 0;
      } else {
         int var5 = (Integer)var1.func_177229_b(field_176351_O);
         if (var5 == 0) {
            return 0;
         } else if (var4 == EnumFacing.UP) {
            return var5;
         } else {
            EnumSet var6 = EnumSet.noneOf(EnumFacing.class);
            Iterator var7 = EnumFacing.Plane.HORIZONTAL.iterator();

            while(var7.hasNext()) {
               EnumFacing var8 = (EnumFacing)var7.next();
               if (this.func_176339_d(var2, var3, var8)) {
                  var6.add(var8);
               }
            }

            if (var4.func_176740_k().func_176722_c() && var6.isEmpty()) {
               return var5;
            } else if (var6.contains(var4) && !var6.contains(var4.func_176735_f()) && !var6.contains(var4.func_176746_e())) {
               return var5;
            } else {
               return 0;
            }
         }
      }
   }

   private boolean func_176339_d(IBlockReader var1, BlockPos var2, EnumFacing var3) {
      BlockPos var4 = var2.func_177972_a(var3);
      IBlockState var5 = var1.func_180495_p(var4);
      boolean var6 = var5.func_185915_l();
      boolean var7 = var1.func_180495_p(var2.func_177984_a()).func_185915_l();
      if (!var7 && var6 && func_176340_e(var1, var4.func_177984_a())) {
         return true;
      } else if (func_176343_a(var5, var3)) {
         return true;
      } else if (var5.func_177230_c() == Blocks.field_196633_cV && (Boolean)var5.func_177229_b(BlockRedstoneDiode.field_196348_c) && var5.func_177229_b(BlockRedstoneDiode.field_185512_D) == var3) {
         return true;
      } else {
         return !var6 && func_176340_e(var1, var4.func_177977_b());
      }
   }

   protected static boolean func_176340_e(IBlockReader var0, BlockPos var1) {
      return func_176346_d(var0.func_180495_p(var1));
   }

   protected static boolean func_176346_d(IBlockState var0) {
      return func_176343_a(var0, (EnumFacing)null);
   }

   protected static boolean func_176343_a(IBlockState var0, @Nullable EnumFacing var1) {
      Block var2 = var0.func_177230_c();
      if (var2 == Blocks.field_150488_af) {
         return true;
      } else if (var0.func_177230_c() == Blocks.field_196633_cV) {
         EnumFacing var3 = (EnumFacing)var0.func_177229_b(BlockRedstoneRepeater.field_185512_D);
         return var3 == var1 || var3.func_176734_d() == var1;
      } else if (Blocks.field_190976_dk == var0.func_177230_c()) {
         return var1 == var0.func_177229_b(BlockObserver.field_176387_N);
      } else {
         return var0.func_185897_m() && var1 != null;
      }
   }

   public boolean func_149744_f(IBlockState var1) {
      return this.field_150181_a;
   }

   public static int func_176337_b(int var0) {
      float var1 = (float)var0 / 15.0F;
      float var2 = var1 * 0.6F + 0.4F;
      if (var0 == 0) {
         var2 = 0.3F;
      }

      float var3 = var1 * var1 * 0.7F - 0.5F;
      float var4 = var1 * var1 * 0.6F - 0.7F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var4 < 0.0F) {
         var4 = 0.0F;
      }

      int var5 = MathHelper.func_76125_a((int)(var2 * 255.0F), 0, 255);
      int var6 = MathHelper.func_76125_a((int)(var3 * 255.0F), 0, 255);
      int var7 = MathHelper.func_76125_a((int)(var4 * 255.0F), 0, 255);
      return -16777216 | var5 << 16 | var6 << 8 | var7;
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
      int var5 = (Integer)var1.func_177229_b(field_176351_O);
      if (var5 != 0) {
         double var6 = (double)var3.func_177958_n() + 0.5D + ((double)var4.nextFloat() - 0.5D) * 0.2D;
         double var8 = (double)((float)var3.func_177956_o() + 0.0625F);
         double var10 = (double)var3.func_177952_p() + 0.5D + ((double)var4.nextFloat() - 0.5D) * 0.2D;
         float var12 = (float)var5 / 15.0F;
         float var13 = var12 * 0.6F + 0.4F;
         float var14 = Math.max(0.0F, var12 * var12 * 0.7F - 0.5F);
         float var15 = Math.max(0.0F, var12 * var12 * 0.6F - 0.7F);
         var2.func_195594_a(new RedstoneParticleData(var13, var14, var15, 1.0F), var6, var8, var10, 0.0D, 0.0D, 0.0D);
      }
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.CUTOUT;
   }

   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176348_a, var1.func_177229_b(field_176349_M))).func_206870_a(field_176347_b, var1.func_177229_b(field_176350_N))).func_206870_a(field_176349_M, var1.func_177229_b(field_176348_a))).func_206870_a(field_176350_N, var1.func_177229_b(field_176347_b));
      case COUNTERCLOCKWISE_90:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176348_a, var1.func_177229_b(field_176347_b))).func_206870_a(field_176347_b, var1.func_177229_b(field_176349_M))).func_206870_a(field_176349_M, var1.func_177229_b(field_176350_N))).func_206870_a(field_176350_N, var1.func_177229_b(field_176348_a));
      case CLOCKWISE_90:
         return (IBlockState)((IBlockState)((IBlockState)((IBlockState)var1.func_206870_a(field_176348_a, var1.func_177229_b(field_176350_N))).func_206870_a(field_176347_b, var1.func_177229_b(field_176348_a))).func_206870_a(field_176349_M, var1.func_177229_b(field_176347_b))).func_206870_a(field_176350_N, var1.func_177229_b(field_176349_M));
      default:
         return var1;
      }
   }

   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      switch(var2) {
      case LEFT_RIGHT:
         return (IBlockState)((IBlockState)var1.func_206870_a(field_176348_a, var1.func_177229_b(field_176349_M))).func_206870_a(field_176349_M, var1.func_177229_b(field_176348_a));
      case FRONT_BACK:
         return (IBlockState)((IBlockState)var1.func_206870_a(field_176347_b, var1.func_177229_b(field_176350_N))).func_206870_a(field_176350_N, var1.func_177229_b(field_176347_b));
      default:
         return super.func_185471_a(var1, var2);
      }
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
      var1.func_206894_a(field_176348_a, field_176347_b, field_176349_M, field_176350_N, field_176351_O);
   }

   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.UNDEFINED;
   }

   static {
      field_176348_a = BlockStateProperties.field_208160_M;
      field_176347_b = BlockStateProperties.field_208159_L;
      field_176349_M = BlockStateProperties.field_208161_N;
      field_176350_N = BlockStateProperties.field_208162_O;
      field_176351_O = BlockStateProperties.field_208136_ak;
      field_196498_A = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, field_176348_a, EnumFacing.EAST, field_176347_b, EnumFacing.SOUTH, field_176349_M, EnumFacing.WEST, field_176350_N));
      field_196499_B = new VoxelShape[]{Block.func_208617_a(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.func_208617_a(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D), Block.func_208617_a(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Block.func_208617_a(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.func_208617_a(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D), Block.func_208617_a(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.func_208617_a(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Block.func_208617_a(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D), Block.func_208617_a(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.func_208617_a(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D), Block.func_208617_a(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)};
   }
}
