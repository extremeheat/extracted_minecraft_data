package net.minecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class TileEntityPiston extends TileEntity implements ITickable {
   private IBlockState field_200231_a;
   private EnumFacing field_174931_f;
   private boolean field_145875_k;
   private boolean field_145872_l;
   private static final ThreadLocal<EnumFacing> field_190613_i = new ThreadLocal<EnumFacing>() {
      protected EnumFacing initialValue() {
         return null;
      }

      // $FF: synthetic method
      protected Object initialValue() {
         return this.initialValue();
      }
   };
   private float field_145873_m;
   private float field_145870_n;
   private long field_211147_k;

   public TileEntityPiston() {
      super(TileEntityType.field_200980_k);
   }

   public TileEntityPiston(IBlockState var1, EnumFacing var2, boolean var3, boolean var4) {
      this();
      this.field_200231_a = var1;
      this.field_174931_f = var2;
      this.field_145875_k = var3;
      this.field_145872_l = var4;
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189515_b(new NBTTagCompound());
   }

   public boolean func_145868_b() {
      return this.field_145875_k;
   }

   public EnumFacing func_212363_d() {
      return this.field_174931_f;
   }

   public boolean func_145867_d() {
      return this.field_145872_l;
   }

   public float func_145860_a(float var1) {
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return this.field_145870_n + (this.field_145873_m - this.field_145870_n) * var1;
   }

   public float func_174929_b(float var1) {
      return (float)this.field_174931_f.func_82601_c() * this.func_184320_e(this.func_145860_a(var1));
   }

   public float func_174928_c(float var1) {
      return (float)this.field_174931_f.func_96559_d() * this.func_184320_e(this.func_145860_a(var1));
   }

   public float func_174926_d(float var1) {
      return (float)this.field_174931_f.func_82599_e() * this.func_184320_e(this.func_145860_a(var1));
   }

   private float func_184320_e(float var1) {
      return this.field_145875_k ? var1 - 1.0F : 1.0F - var1;
   }

   private IBlockState func_190606_j() {
      return !this.func_145868_b() && this.func_145867_d() ? (IBlockState)((IBlockState)Blocks.field_150332_K.func_176223_P().func_206870_a(BlockPistonExtension.field_176325_b, this.field_200231_a.func_177230_c() == Blocks.field_150320_F ? PistonType.STICKY : PistonType.DEFAULT)).func_206870_a(BlockPistonExtension.field_176387_N, this.field_200231_a.func_177229_b(BlockPistonBase.field_176387_N)) : this.field_200231_a;
   }

   private void func_184322_i(float var1) {
      EnumFacing var2 = this.func_195509_h();
      double var3 = (double)(var1 - this.field_145873_m);
      VoxelShape var5 = this.func_190606_j().func_196952_d(this.field_145850_b, this.func_174877_v());
      if (!var5.func_197766_b()) {
         List var6 = var5.func_197756_d();
         AxisAlignedBB var7 = this.func_190607_a(this.func_191515_a(var6));
         List var8 = this.field_145850_b.func_72839_b((Entity)null, this.func_190610_a(var7, var2, var3).func_111270_a(var7));
         if (!var8.isEmpty()) {
            boolean var9 = this.field_200231_a.func_177230_c() == Blocks.field_180399_cE;

            for(int var10 = 0; var10 < var8.size(); ++var10) {
               Entity var11 = (Entity)var8.get(var10);
               if (var11.func_184192_z() != EnumPushReaction.IGNORE) {
                  if (var9) {
                     switch(var2.func_176740_k()) {
                     case X:
                        var11.field_70159_w = (double)var2.func_82601_c();
                        break;
                     case Y:
                        var11.field_70181_x = (double)var2.func_96559_d();
                        break;
                     case Z:
                        var11.field_70179_y = (double)var2.func_82599_e();
                     }
                  }

                  double var12 = 0.0D;

                  for(int var14 = 0; var14 < var6.size(); ++var14) {
                     AxisAlignedBB var15 = this.func_190610_a(this.func_190607_a((AxisAlignedBB)var6.get(var14)), var2, var3);
                     AxisAlignedBB var16 = var11.func_174813_aQ();
                     if (var15.func_72326_a(var16)) {
                        var12 = Math.max(var12, this.func_190612_a(var15, var2, var16));
                        if (var12 >= var3) {
                           break;
                        }
                     }
                  }

                  if (var12 > 0.0D) {
                     var12 = Math.min(var12, var3) + 0.01D;
                     field_190613_i.set(var2);
                     var11.func_70091_d(MoverType.PISTON, var12 * (double)var2.func_82601_c(), var12 * (double)var2.func_96559_d(), var12 * (double)var2.func_82599_e());
                     field_190613_i.set((Object)null);
                     if (!this.field_145875_k && this.field_145872_l) {
                        this.func_190605_a(var11, var2, var3);
                     }
                  }
               }
            }

         }
      }
   }

   public EnumFacing func_195509_h() {
      return this.field_145875_k ? this.field_174931_f : this.field_174931_f.func_176734_d();
   }

   private AxisAlignedBB func_191515_a(List<AxisAlignedBB> var1) {
      double var2 = 0.0D;
      double var4 = 0.0D;
      double var6 = 0.0D;
      double var8 = 1.0D;
      double var10 = 1.0D;
      double var12 = 1.0D;

      AxisAlignedBB var15;
      for(Iterator var14 = var1.iterator(); var14.hasNext(); var12 = Math.max(var15.field_72334_f, var12)) {
         var15 = (AxisAlignedBB)var14.next();
         var2 = Math.min(var15.field_72340_a, var2);
         var4 = Math.min(var15.field_72338_b, var4);
         var6 = Math.min(var15.field_72339_c, var6);
         var8 = Math.max(var15.field_72336_d, var8);
         var10 = Math.max(var15.field_72337_e, var10);
      }

      return new AxisAlignedBB(var2, var4, var6, var8, var10, var12);
   }

   private double func_190612_a(AxisAlignedBB var1, EnumFacing var2, AxisAlignedBB var3) {
      switch(var2.func_176740_k()) {
      case X:
         return func_190611_b(var1, var2, var3);
      case Y:
      default:
         return func_190608_c(var1, var2, var3);
      case Z:
         return func_190604_d(var1, var2, var3);
      }
   }

   private AxisAlignedBB func_190607_a(AxisAlignedBB var1) {
      double var2 = (double)this.func_184320_e(this.field_145873_m);
      return var1.func_72317_d((double)this.field_174879_c.func_177958_n() + var2 * (double)this.field_174931_f.func_82601_c(), (double)this.field_174879_c.func_177956_o() + var2 * (double)this.field_174931_f.func_96559_d(), (double)this.field_174879_c.func_177952_p() + var2 * (double)this.field_174931_f.func_82599_e());
   }

   private AxisAlignedBB func_190610_a(AxisAlignedBB var1, EnumFacing var2, double var3) {
      double var5 = var3 * (double)var2.func_176743_c().func_179524_a();
      double var7 = Math.min(var5, 0.0D);
      double var9 = Math.max(var5, 0.0D);
      switch(var2) {
      case WEST:
         return new AxisAlignedBB(var1.field_72340_a + var7, var1.field_72338_b, var1.field_72339_c, var1.field_72340_a + var9, var1.field_72337_e, var1.field_72334_f);
      case EAST:
         return new AxisAlignedBB(var1.field_72336_d + var7, var1.field_72338_b, var1.field_72339_c, var1.field_72336_d + var9, var1.field_72337_e, var1.field_72334_f);
      case DOWN:
         return new AxisAlignedBB(var1.field_72340_a, var1.field_72338_b + var7, var1.field_72339_c, var1.field_72336_d, var1.field_72338_b + var9, var1.field_72334_f);
      case UP:
      default:
         return new AxisAlignedBB(var1.field_72340_a, var1.field_72337_e + var7, var1.field_72339_c, var1.field_72336_d, var1.field_72337_e + var9, var1.field_72334_f);
      case NORTH:
         return new AxisAlignedBB(var1.field_72340_a, var1.field_72338_b, var1.field_72339_c + var7, var1.field_72336_d, var1.field_72337_e, var1.field_72339_c + var9);
      case SOUTH:
         return new AxisAlignedBB(var1.field_72340_a, var1.field_72338_b, var1.field_72334_f + var7, var1.field_72336_d, var1.field_72337_e, var1.field_72334_f + var9);
      }
   }

   private void func_190605_a(Entity var1, EnumFacing var2, double var3) {
      AxisAlignedBB var5 = var1.func_174813_aQ();
      AxisAlignedBB var6 = VoxelShapes.func_197868_b().func_197752_a().func_186670_a(this.field_174879_c);
      if (var5.func_72326_a(var6)) {
         EnumFacing var7 = var2.func_176734_d();
         double var8 = this.func_190612_a(var6, var7, var5) + 0.01D;
         double var10 = this.func_190612_a(var6, var7, var5.func_191500_a(var6)) + 0.01D;
         if (Math.abs(var8 - var10) < 0.01D) {
            var8 = Math.min(var8, var3) + 0.01D;
            field_190613_i.set(var2);
            var1.func_70091_d(MoverType.PISTON, var8 * (double)var7.func_82601_c(), var8 * (double)var7.func_96559_d(), var8 * (double)var7.func_82599_e());
            field_190613_i.set((Object)null);
         }
      }

   }

   private static double func_190611_b(AxisAlignedBB var0, EnumFacing var1, AxisAlignedBB var2) {
      return var1.func_176743_c() == EnumFacing.AxisDirection.POSITIVE ? var0.field_72336_d - var2.field_72340_a : var2.field_72336_d - var0.field_72340_a;
   }

   private static double func_190608_c(AxisAlignedBB var0, EnumFacing var1, AxisAlignedBB var2) {
      return var1.func_176743_c() == EnumFacing.AxisDirection.POSITIVE ? var0.field_72337_e - var2.field_72338_b : var2.field_72337_e - var0.field_72338_b;
   }

   private static double func_190604_d(AxisAlignedBB var0, EnumFacing var1, AxisAlignedBB var2) {
      return var1.func_176743_c() == EnumFacing.AxisDirection.POSITIVE ? var0.field_72334_f - var2.field_72339_c : var2.field_72334_f - var0.field_72339_c;
   }

   public IBlockState func_200230_i() {
      return this.field_200231_a;
   }

   public void func_145866_f() {
      if (this.field_145870_n < 1.0F && this.field_145850_b != null) {
         this.field_145873_m = 1.0F;
         this.field_145870_n = this.field_145873_m;
         this.field_145850_b.func_175713_t(this.field_174879_c);
         this.func_145843_s();
         if (this.field_145850_b.func_180495_p(this.field_174879_c).func_177230_c() == Blocks.field_196603_bb) {
            IBlockState var1;
            if (this.field_145872_l) {
               var1 = Blocks.field_150350_a.func_176223_P();
            } else {
               var1 = Block.func_199770_b(this.field_200231_a, this.field_145850_b, this.field_174879_c);
            }

            this.field_145850_b.func_180501_a(this.field_174879_c, var1, 3);
            this.field_145850_b.func_190524_a(this.field_174879_c, var1.func_177230_c(), this.field_174879_c);
         }
      }

   }

   public void func_73660_a() {
      this.field_211147_k = this.field_145850_b.func_82737_E();
      this.field_145870_n = this.field_145873_m;
      if (this.field_145870_n >= 1.0F) {
         this.field_145850_b.func_175713_t(this.field_174879_c);
         this.func_145843_s();
         if (this.field_200231_a != null && this.field_145850_b.func_180495_p(this.field_174879_c).func_177230_c() == Blocks.field_196603_bb) {
            IBlockState var2 = Block.func_199770_b(this.field_200231_a, this.field_145850_b, this.field_174879_c);
            if (var2.func_196958_f()) {
               this.field_145850_b.func_180501_a(this.field_174879_c, this.field_200231_a, 84);
               Block.func_196263_a(this.field_200231_a, var2, this.field_145850_b, this.field_174879_c, 3);
            } else {
               if (var2.func_196959_b(BlockStateProperties.field_208198_y) && (Boolean)var2.func_177229_b(BlockStateProperties.field_208198_y)) {
                  var2 = (IBlockState)var2.func_206870_a(BlockStateProperties.field_208198_y, false);
               }

               this.field_145850_b.func_180501_a(this.field_174879_c, var2, 67);
               this.field_145850_b.func_190524_a(this.field_174879_c, var2.func_177230_c(), this.field_174879_c);
            }
         }

      } else {
         float var1 = this.field_145873_m + 0.5F;
         this.func_184322_i(var1);
         this.field_145873_m = var1;
         if (this.field_145873_m >= 1.0F) {
            this.field_145873_m = 1.0F;
         }

      }
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_200231_a = NBTUtil.func_190008_d(var1.func_74775_l("blockState"));
      this.field_174931_f = EnumFacing.func_82600_a(var1.func_74762_e("facing"));
      this.field_145873_m = var1.func_74760_g("progress");
      this.field_145870_n = this.field_145873_m;
      this.field_145875_k = var1.func_74767_n("extending");
      this.field_145872_l = var1.func_74767_n("source");
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      var1.func_74782_a("blockState", NBTUtil.func_190009_a(this.field_200231_a));
      var1.func_74768_a("facing", this.field_174931_f.func_176745_a());
      var1.func_74776_a("progress", this.field_145870_n);
      var1.func_74757_a("extending", this.field_145875_k);
      var1.func_74757_a("source", this.field_145872_l);
      return var1;
   }

   public VoxelShape func_195508_a(IBlockReader var1, BlockPos var2) {
      VoxelShape var3;
      if (!this.field_145875_k && this.field_145872_l) {
         var3 = ((IBlockState)this.field_200231_a.func_206870_a(BlockPistonBase.field_176320_b, true)).func_196952_d(var1, var2);
      } else {
         var3 = VoxelShapes.func_197880_a();
      }

      EnumFacing var4 = (EnumFacing)field_190613_i.get();
      if ((double)this.field_145873_m < 1.0D && var4 == this.func_195509_h()) {
         return var3;
      } else {
         IBlockState var5;
         if (this.func_145867_d()) {
            var5 = (IBlockState)((IBlockState)Blocks.field_150332_K.func_176223_P().func_206870_a(BlockPistonExtension.field_176387_N, this.field_174931_f)).func_206870_a(BlockPistonExtension.field_176327_M, this.field_145875_k != 1.0F - this.field_145873_m < 4.0F);
         } else {
            var5 = this.field_200231_a;
         }

         float var6 = this.func_184320_e(this.field_145873_m);
         double var7 = (double)((float)this.field_174931_f.func_82601_c() * var6);
         double var9 = (double)((float)this.field_174931_f.func_96559_d() * var6);
         double var11 = (double)((float)this.field_174931_f.func_82599_e() * var6);
         return VoxelShapes.func_197872_a(var3, var5.func_196952_d(var1, var2).func_197751_a(var7, var9, var11));
      }
   }

   public long func_211146_k() {
      return this.field_211147_k;
   }
}
