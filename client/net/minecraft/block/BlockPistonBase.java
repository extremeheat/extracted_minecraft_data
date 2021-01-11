package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block {
   public static final PropertyDirection field_176321_a = PropertyDirection.func_177714_a("facing");
   public static final PropertyBool field_176320_b = PropertyBool.func_177716_a("extended");
   private final boolean field_150082_a;

   public BlockPistonBase(boolean var1) {
      super(Material.field_76233_E);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176321_a, EnumFacing.NORTH).func_177226_a(field_176320_b, false));
      this.field_150082_a = var1;
      this.func_149672_a(field_149780_i);
      this.func_149711_c(0.5F);
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   public boolean func_149662_c() {
      return false;
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
      var1.func_180501_a(var2, var3.func_177226_a(field_176321_a, func_180695_a(var1, var2, var4)), 2);
      if (!var1.field_72995_K) {
         this.func_176316_e(var1, var2, var3);
      }

   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K) {
         this.func_176316_e(var1, var2, var3);
      }

   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.field_72995_K && var1.func_175625_s(var2) == null) {
         this.func_176316_e(var1, var2, var3);
      }

   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176223_P().func_177226_a(field_176321_a, func_180695_a(var1, var2, var8)).func_177226_a(field_176320_b, false);
   }

   private void func_176316_e(World var1, BlockPos var2, IBlockState var3) {
      EnumFacing var4 = (EnumFacing)var3.func_177229_b(field_176321_a);
      boolean var5 = this.func_176318_b(var1, var2, var4);
      if (var5 && !(Boolean)var3.func_177229_b(field_176320_b)) {
         if ((new BlockPistonStructureHelper(var1, var2, var4, true)).func_177253_a()) {
            var1.func_175641_c(var2, this, 0, var4.func_176745_a());
         }
      } else if (!var5 && (Boolean)var3.func_177229_b(field_176320_b)) {
         var1.func_180501_a(var2, var3.func_177226_a(field_176320_b, false), 2);
         var1.func_175641_c(var2, this, 1, var4.func_176745_a());
      }

   }

   private boolean func_176318_b(World var1, BlockPos var2, EnumFacing var3) {
      EnumFacing[] var4 = EnumFacing.values();
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         EnumFacing var7 = var4[var6];
         if (var7 != var3 && var1.func_175709_b(var2.func_177972_a(var7), var7)) {
            return true;
         }
      }

      if (var1.func_175709_b(var2, EnumFacing.DOWN)) {
         return true;
      } else {
         BlockPos var9 = var2.func_177984_a();
         EnumFacing[] var10 = EnumFacing.values();
         var6 = var10.length;

         for(int var11 = 0; var11 < var6; ++var11) {
            EnumFacing var8 = var10[var11];
            if (var8 != EnumFacing.DOWN && var1.func_175709_b(var9.func_177972_a(var8), var8)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean func_180648_a(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      EnumFacing var6 = (EnumFacing)var3.func_177229_b(field_176321_a);
      if (!var1.field_72995_K) {
         boolean var7 = this.func_176318_b(var1, var2, var6);
         if (var7 && var4 == 1) {
            var1.func_180501_a(var2, var3.func_177226_a(field_176320_b, true), 2);
            return false;
         }

         if (!var7 && var4 == 0) {
            return false;
         }
      }

      if (var4 == 0) {
         if (!this.func_176319_a(var1, var2, var6, true)) {
            return false;
         }

         var1.func_180501_a(var2, var3.func_177226_a(field_176320_b, true), 2);
         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "tile.piston.out", 0.5F, var1.field_73012_v.nextFloat() * 0.25F + 0.6F);
      } else if (var4 == 1) {
         TileEntity var13 = var1.func_175625_s(var2.func_177972_a(var6));
         if (var13 instanceof TileEntityPiston) {
            ((TileEntityPiston)var13).func_145866_f();
         }

         var1.func_180501_a(var2, Blocks.field_180384_M.func_176223_P().func_177226_a(BlockPistonMoving.field_176426_a, var6).func_177226_a(BlockPistonMoving.field_176425_b, this.field_150082_a ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT), 3);
         var1.func_175690_a(var2, BlockPistonMoving.func_176423_a(this.func_176203_a(var5), var6, false, true));
         if (this.field_150082_a) {
            BlockPos var8 = var2.func_177982_a(var6.func_82601_c() * 2, var6.func_96559_d() * 2, var6.func_82599_e() * 2);
            Block var9 = var1.func_180495_p(var8).func_177230_c();
            boolean var10 = false;
            if (var9 == Blocks.field_180384_M) {
               TileEntity var11 = var1.func_175625_s(var8);
               if (var11 instanceof TileEntityPiston) {
                  TileEntityPiston var12 = (TileEntityPiston)var11;
                  if (var12.func_174930_e() == var6 && var12.func_145868_b()) {
                     var12.func_145866_f();
                     var10 = true;
                  }
               }
            }

            if (!var10 && var9.func_149688_o() != Material.field_151579_a && func_180696_a(var9, var1, var8, var6.func_176734_d(), false) && (var9.func_149656_h() == 0 || var9 == Blocks.field_150331_J || var9 == Blocks.field_150320_F)) {
               this.func_176319_a(var1, var2, var6, false);
            }
         } else {
            var1.func_175698_g(var2.func_177972_a(var6));
         }

         var1.func_72908_a((double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, "tile.piston.in", 0.5F, var1.field_73012_v.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      if (var3.func_177230_c() == this && (Boolean)var3.func_177229_b(field_176320_b)) {
         float var4 = 0.25F;
         EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176321_a);
         if (var5 != null) {
            switch(var5) {
            case DOWN:
               this.func_149676_a(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case UP:
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
               break;
            case NORTH:
               this.func_149676_a(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
               break;
            case SOUTH:
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
               break;
            case WEST:
               this.func_149676_a(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case EAST:
               this.func_149676_a(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
         }
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   public void func_149683_g() {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      this.func_180654_a(var1, var2);
      return super.func_180640_a(var1, var2, var3);
   }

   public boolean func_149686_d() {
      return false;
   }

   public static EnumFacing func_176317_b(int var0) {
      int var1 = var0 & 7;
      return var1 > 5 ? null : EnumFacing.func_82600_a(var1);
   }

   public static EnumFacing func_180695_a(World var0, BlockPos var1, EntityLivingBase var2) {
      if (MathHelper.func_76135_e((float)var2.field_70165_t - (float)var1.func_177958_n()) < 2.0F && MathHelper.func_76135_e((float)var2.field_70161_v - (float)var1.func_177952_p()) < 2.0F) {
         double var3 = var2.field_70163_u + (double)var2.func_70047_e();
         if (var3 - (double)var1.func_177956_o() > 2.0D) {
            return EnumFacing.UP;
         }

         if ((double)var1.func_177956_o() - var3 > 0.0D) {
            return EnumFacing.DOWN;
         }
      }

      return var2.func_174811_aO().func_176734_d();
   }

   public static boolean func_180696_a(Block var0, World var1, BlockPos var2, EnumFacing var3, boolean var4) {
      if (var0 == Blocks.field_150343_Z) {
         return false;
      } else if (!var1.func_175723_af().func_177746_a(var2)) {
         return false;
      } else if (var2.func_177956_o() < 0 || var3 == EnumFacing.DOWN && var2.func_177956_o() == 0) {
         return false;
      } else if (var2.func_177956_o() <= var1.func_72800_K() - 1 && (var3 != EnumFacing.UP || var2.func_177956_o() != var1.func_72800_K() - 1)) {
         if (var0 != Blocks.field_150331_J && var0 != Blocks.field_150320_F) {
            if (var0.func_176195_g(var1, var2) == -1.0F) {
               return false;
            }

            if (var0.func_149656_h() == 2) {
               return false;
            }

            if (var0.func_149656_h() == 1) {
               if (!var4) {
                  return false;
               }

               return true;
            }
         } else if ((Boolean)var1.func_180495_p(var2).func_177229_b(field_176320_b)) {
            return false;
         }

         return !(var0 instanceof ITileEntityProvider);
      } else {
         return false;
      }
   }

   private boolean func_176319_a(World var1, BlockPos var2, EnumFacing var3, boolean var4) {
      if (!var4) {
         var1.func_175698_g(var2.func_177972_a(var3));
      }

      BlockPistonStructureHelper var5 = new BlockPistonStructureHelper(var1, var2, var3, var4);
      List var6 = var5.func_177254_c();
      List var7 = var5.func_177252_d();
      if (!var5.func_177253_a()) {
         return false;
      } else {
         int var8 = var6.size() + var7.size();
         Block[] var9 = new Block[var8];
         EnumFacing var10 = var4 ? var3 : var3.func_176734_d();

         int var11;
         BlockPos var12;
         for(var11 = var7.size() - 1; var11 >= 0; --var11) {
            var12 = (BlockPos)var7.get(var11);
            Block var13 = var1.func_180495_p(var12).func_177230_c();
            var13.func_176226_b(var1, var12, var1.func_180495_p(var12), 0);
            var1.func_175698_g(var12);
            --var8;
            var9[var8] = var13;
         }

         IBlockState var18;
         for(var11 = var6.size() - 1; var11 >= 0; --var11) {
            var12 = (BlockPos)var6.get(var11);
            var18 = var1.func_180495_p(var12);
            Block var14 = var18.func_177230_c();
            var14.func_176201_c(var18);
            var1.func_175698_g(var12);
            var12 = var12.func_177972_a(var10);
            var1.func_180501_a(var12, Blocks.field_180384_M.func_176223_P().func_177226_a(field_176321_a, var3), 4);
            var1.func_175690_a(var12, BlockPistonMoving.func_176423_a(var18, var3, var4, false));
            --var8;
            var9[var8] = var14;
         }

         BlockPos var16 = var2.func_177972_a(var3);
         if (var4) {
            BlockPistonExtension.EnumPistonType var17 = this.field_150082_a ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
            var18 = Blocks.field_150332_K.func_176223_P().func_177226_a(BlockPistonExtension.field_176326_a, var3).func_177226_a(BlockPistonExtension.field_176325_b, var17);
            IBlockState var20 = Blocks.field_180384_M.func_176223_P().func_177226_a(BlockPistonMoving.field_176426_a, var3).func_177226_a(BlockPistonMoving.field_176425_b, this.field_150082_a ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
            var1.func_180501_a(var16, var20, 4);
            var1.func_175690_a(var16, BlockPistonMoving.func_176423_a(var18, var3, true, false));
         }

         int var19;
         for(var19 = var7.size() - 1; var19 >= 0; --var19) {
            var1.func_175685_c((BlockPos)var7.get(var19), var9[var8++]);
         }

         for(var19 = var6.size() - 1; var19 >= 0; --var19) {
            var1.func_175685_c((BlockPos)var6.get(var19), var9[var8++]);
         }

         if (var4) {
            var1.func_175685_c(var16, Blocks.field_150332_K);
            var1.func_175685_c(var2, this);
         }

         return true;
      }
   }

   public IBlockState func_176217_b(IBlockState var1) {
      return this.func_176223_P().func_177226_a(field_176321_a, EnumFacing.UP);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176321_a, func_176317_b(var1)).func_177226_a(field_176320_b, (var1 & 8) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176321_a)).func_176745_a();
      if ((Boolean)var1.func_177229_b(field_176320_b)) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176321_a, field_176320_b});
   }
}
