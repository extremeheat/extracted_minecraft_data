package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   protected final boolean field_150053_a;

   public static boolean func_176562_d(World var0, BlockPos var1) {
      return func_176563_d(var0.func_180495_p(var1));
   }

   public static boolean func_176563_d(IBlockState var0) {
      Block var1 = var0.func_177230_c();
      return var1 == Blocks.field_150448_aq || var1 == Blocks.field_150318_D || var1 == Blocks.field_150319_E || var1 == Blocks.field_150408_cc;
   }

   protected BlockRailBase(boolean var1) {
      super(Material.field_151594_q);
      this.field_150053_a = var1;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.func_149647_a(CreativeTabs.field_78029_e);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public boolean func_149662_c() {
      return false;
   }

   public MovingObjectPosition func_180636_a(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      this.func_180654_a(var1, var2);
      return super.func_180636_a(var1, var2, var3, var4);
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      IBlockState var3 = var1.func_180495_p(var2);
      BlockRailBase.EnumRailDirection var4 = var3.func_177230_c() == this ? (BlockRailBase.EnumRailDirection)var3.func_177229_b(this.func_176560_l()) : null;
      if (var4 != null && var4.func_177018_c()) {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      } else {
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      }

   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return World.func_175683_a(var1, var2.func_177977_b());
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.field_72995_K) {
         var3 = this.func_176564_a(var1, var2, var3, true);
         if (this.field_150053_a) {
            this.func_176204_a(var1, var2, var3, this);
         }
      }

   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (!var1.field_72995_K) {
         BlockRailBase.EnumRailDirection var5 = (BlockRailBase.EnumRailDirection)var3.func_177229_b(this.func_176560_l());
         boolean var6 = false;
         if (!World.func_175683_a(var1, var2.func_177977_b())) {
            var6 = true;
         }

         if (var5 == BlockRailBase.EnumRailDirection.ASCENDING_EAST && !World.func_175683_a(var1, var2.func_177974_f())) {
            var6 = true;
         } else if (var5 == BlockRailBase.EnumRailDirection.ASCENDING_WEST && !World.func_175683_a(var1, var2.func_177976_e())) {
            var6 = true;
         } else if (var5 == BlockRailBase.EnumRailDirection.ASCENDING_NORTH && !World.func_175683_a(var1, var2.func_177978_c())) {
            var6 = true;
         } else if (var5 == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH && !World.func_175683_a(var1, var2.func_177968_d())) {
            var6 = true;
         }

         if (var6) {
            this.func_176226_b(var1, var2, var3, 0);
            var1.func_175698_g(var2);
         } else {
            this.func_176561_b(var1, var2, var3, var4);
         }

      }
   }

   protected void func_176561_b(World var1, BlockPos var2, IBlockState var3, Block var4) {
   }

   protected IBlockState func_176564_a(World var1, BlockPos var2, IBlockState var3, boolean var4) {
      return var1.field_72995_K ? var3 : (new BlockRailBase.Rail(var1, var2, var3)).func_180364_a(var1.func_175640_z(var2), var4).func_180362_b();
   }

   public int func_149656_h() {
      return 0;
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      super.func_180663_b(var1, var2, var3);
      if (((BlockRailBase.EnumRailDirection)var3.func_177229_b(this.func_176560_l())).func_177018_c()) {
         var1.func_175685_c(var2.func_177984_a(), this);
      }

      if (this.field_150053_a) {
         var1.func_175685_c(var2, this);
         var1.func_175685_c(var2.func_177977_b(), this);
      }

   }

   public abstract IProperty<BlockRailBase.EnumRailDirection> func_176560_l();

   public static enum EnumRailDirection implements IStringSerializable {
      NORTH_SOUTH(0, "north_south"),
      EAST_WEST(1, "east_west"),
      ASCENDING_EAST(2, "ascending_east"),
      ASCENDING_WEST(3, "ascending_west"),
      ASCENDING_NORTH(4, "ascending_north"),
      ASCENDING_SOUTH(5, "ascending_south"),
      SOUTH_EAST(6, "south_east"),
      SOUTH_WEST(7, "south_west"),
      NORTH_WEST(8, "north_west"),
      NORTH_EAST(9, "north_east");

      private static final BlockRailBase.EnumRailDirection[] field_177030_k = new BlockRailBase.EnumRailDirection[values().length];
      private final int field_177027_l;
      private final String field_177028_m;

      private EnumRailDirection(int var3, String var4) {
         this.field_177027_l = var3;
         this.field_177028_m = var4;
      }

      public int func_177015_a() {
         return this.field_177027_l;
      }

      public String toString() {
         return this.field_177028_m;
      }

      public boolean func_177018_c() {
         return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
      }

      public static BlockRailBase.EnumRailDirection func_177016_a(int var0) {
         if (var0 < 0 || var0 >= field_177030_k.length) {
            var0 = 0;
         }

         return field_177030_k[var0];
      }

      public String func_176610_l() {
         return this.field_177028_m;
      }

      static {
         BlockRailBase.EnumRailDirection[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockRailBase.EnumRailDirection var3 = var0[var2];
            field_177030_k[var3.func_177015_a()] = var3;
         }

      }
   }

   public class Rail {
      private final World field_150660_b;
      private final BlockPos field_180367_c;
      private final BlockRailBase field_180365_d;
      private IBlockState field_180366_e;
      private final boolean field_150656_f;
      private final List<BlockPos> field_150657_g = Lists.newArrayList();

      public Rail(World var2, BlockPos var3, IBlockState var4) {
         super();
         this.field_150660_b = var2;
         this.field_180367_c = var3;
         this.field_180366_e = var4;
         this.field_180365_d = (BlockRailBase)var4.func_177230_c();
         BlockRailBase.EnumRailDirection var5 = (BlockRailBase.EnumRailDirection)var4.func_177229_b(BlockRailBase.this.func_176560_l());
         this.field_150656_f = this.field_180365_d.field_150053_a;
         this.func_180360_a(var5);
      }

      private void func_180360_a(BlockRailBase.EnumRailDirection var1) {
         this.field_150657_g.clear();
         switch(var1) {
         case NORTH_SOUTH:
            this.field_150657_g.add(this.field_180367_c.func_177978_c());
            this.field_150657_g.add(this.field_180367_c.func_177968_d());
            break;
         case EAST_WEST:
            this.field_150657_g.add(this.field_180367_c.func_177976_e());
            this.field_150657_g.add(this.field_180367_c.func_177974_f());
            break;
         case ASCENDING_EAST:
            this.field_150657_g.add(this.field_180367_c.func_177976_e());
            this.field_150657_g.add(this.field_180367_c.func_177974_f().func_177984_a());
            break;
         case ASCENDING_WEST:
            this.field_150657_g.add(this.field_180367_c.func_177976_e().func_177984_a());
            this.field_150657_g.add(this.field_180367_c.func_177974_f());
            break;
         case ASCENDING_NORTH:
            this.field_150657_g.add(this.field_180367_c.func_177978_c().func_177984_a());
            this.field_150657_g.add(this.field_180367_c.func_177968_d());
            break;
         case ASCENDING_SOUTH:
            this.field_150657_g.add(this.field_180367_c.func_177978_c());
            this.field_150657_g.add(this.field_180367_c.func_177968_d().func_177984_a());
            break;
         case SOUTH_EAST:
            this.field_150657_g.add(this.field_180367_c.func_177974_f());
            this.field_150657_g.add(this.field_180367_c.func_177968_d());
            break;
         case SOUTH_WEST:
            this.field_150657_g.add(this.field_180367_c.func_177976_e());
            this.field_150657_g.add(this.field_180367_c.func_177968_d());
            break;
         case NORTH_WEST:
            this.field_150657_g.add(this.field_180367_c.func_177976_e());
            this.field_150657_g.add(this.field_180367_c.func_177978_c());
            break;
         case NORTH_EAST:
            this.field_150657_g.add(this.field_180367_c.func_177974_f());
            this.field_150657_g.add(this.field_180367_c.func_177978_c());
         }

      }

      private void func_150651_b() {
         for(int var1 = 0; var1 < this.field_150657_g.size(); ++var1) {
            BlockRailBase.Rail var2 = this.func_180697_b((BlockPos)this.field_150657_g.get(var1));
            if (var2 != null && var2.func_150653_a(this)) {
               this.field_150657_g.set(var1, var2.field_180367_c);
            } else {
               this.field_150657_g.remove(var1--);
            }
         }

      }

      private boolean func_180359_a(BlockPos var1) {
         return BlockRailBase.func_176562_d(this.field_150660_b, var1) || BlockRailBase.func_176562_d(this.field_150660_b, var1.func_177984_a()) || BlockRailBase.func_176562_d(this.field_150660_b, var1.func_177977_b());
      }

      private BlockRailBase.Rail func_180697_b(BlockPos var1) {
         IBlockState var3 = this.field_150660_b.func_180495_p(var1);
         if (BlockRailBase.func_176563_d(var3)) {
            return BlockRailBase.this.new Rail(this.field_150660_b, var1, var3);
         } else {
            BlockPos var2 = var1.func_177984_a();
            var3 = this.field_150660_b.func_180495_p(var2);
            if (BlockRailBase.func_176563_d(var3)) {
               return BlockRailBase.this.new Rail(this.field_150660_b, var2, var3);
            } else {
               var2 = var1.func_177977_b();
               var3 = this.field_150660_b.func_180495_p(var2);
               return BlockRailBase.func_176563_d(var3) ? BlockRailBase.this.new Rail(this.field_150660_b, var2, var3) : null;
            }
         }
      }

      private boolean func_150653_a(BlockRailBase.Rail var1) {
         return this.func_180363_c(var1.field_180367_c);
      }

      private boolean func_180363_c(BlockPos var1) {
         for(int var2 = 0; var2 < this.field_150657_g.size(); ++var2) {
            BlockPos var3 = (BlockPos)this.field_150657_g.get(var2);
            if (var3.func_177958_n() == var1.func_177958_n() && var3.func_177952_p() == var1.func_177952_p()) {
               return true;
            }
         }

         return false;
      }

      protected int func_150650_a() {
         int var1 = 0;
         Iterator var2 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var2.hasNext()) {
            EnumFacing var3 = (EnumFacing)var2.next();
            if (this.func_180359_a(this.field_180367_c.func_177972_a(var3))) {
               ++var1;
            }
         }

         return var1;
      }

      private boolean func_150649_b(BlockRailBase.Rail var1) {
         return this.func_150653_a(var1) || this.field_150657_g.size() != 2;
      }

      private void func_150645_c(BlockRailBase.Rail var1) {
         this.field_150657_g.add(var1.field_180367_c);
         BlockPos var2 = this.field_180367_c.func_177978_c();
         BlockPos var3 = this.field_180367_c.func_177968_d();
         BlockPos var4 = this.field_180367_c.func_177976_e();
         BlockPos var5 = this.field_180367_c.func_177974_f();
         boolean var6 = this.func_180363_c(var2);
         boolean var7 = this.func_180363_c(var3);
         boolean var8 = this.func_180363_c(var4);
         boolean var9 = this.func_180363_c(var5);
         BlockRailBase.EnumRailDirection var10 = null;
         if (var6 || var7) {
            var10 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if (var8 || var9) {
            var10 = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.field_150656_f) {
            if (var7 && var9 && !var6 && !var8) {
               var10 = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (var7 && var8 && !var6 && !var9) {
               var10 = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (var6 && var8 && !var7 && !var9) {
               var10 = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (var6 && var9 && !var7 && !var8) {
               var10 = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (var10 == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.func_176562_d(this.field_150660_b, var2.func_177984_a())) {
               var10 = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.func_176562_d(this.field_150660_b, var3.func_177984_a())) {
               var10 = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (var10 == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.func_176562_d(this.field_150660_b, var5.func_177984_a())) {
               var10 = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.func_176562_d(this.field_150660_b, var4.func_177984_a())) {
               var10 = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (var10 == null) {
            var10 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.field_180366_e = this.field_180366_e.func_177226_a(this.field_180365_d.func_176560_l(), var10);
         this.field_150660_b.func_180501_a(this.field_180367_c, this.field_180366_e, 3);
      }

      private boolean func_180361_d(BlockPos var1) {
         BlockRailBase.Rail var2 = this.func_180697_b(var1);
         if (var2 == null) {
            return false;
         } else {
            var2.func_150651_b();
            return var2.func_150649_b(this);
         }
      }

      public BlockRailBase.Rail func_180364_a(boolean var1, boolean var2) {
         BlockPos var3 = this.field_180367_c.func_177978_c();
         BlockPos var4 = this.field_180367_c.func_177968_d();
         BlockPos var5 = this.field_180367_c.func_177976_e();
         BlockPos var6 = this.field_180367_c.func_177974_f();
         boolean var7 = this.func_180361_d(var3);
         boolean var8 = this.func_180361_d(var4);
         boolean var9 = this.func_180361_d(var5);
         boolean var10 = this.func_180361_d(var6);
         BlockRailBase.EnumRailDirection var11 = null;
         if ((var7 || var8) && !var9 && !var10) {
            var11 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if ((var9 || var10) && !var7 && !var8) {
            var11 = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.field_150656_f) {
            if (var8 && var10 && !var7 && !var9) {
               var11 = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (var8 && var9 && !var7 && !var10) {
               var11 = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (var7 && var9 && !var8 && !var10) {
               var11 = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (var7 && var10 && !var8 && !var9) {
               var11 = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (var11 == null) {
            if (var7 || var8) {
               var11 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (var9 || var10) {
               var11 = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.field_150656_f) {
               if (var1) {
                  if (var8 && var10) {
                     var11 = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }

                  if (var9 && var8) {
                     var11 = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (var10 && var7) {
                     var11 = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (var7 && var9) {
                     var11 = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }
               } else {
                  if (var7 && var9) {
                     var11 = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }

                  if (var10 && var7) {
                     var11 = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (var9 && var8) {
                     var11 = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (var8 && var10) {
                     var11 = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }
               }
            }
         }

         if (var11 == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.func_176562_d(this.field_150660_b, var3.func_177984_a())) {
               var11 = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.func_176562_d(this.field_150660_b, var4.func_177984_a())) {
               var11 = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (var11 == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.func_176562_d(this.field_150660_b, var6.func_177984_a())) {
               var11 = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.func_176562_d(this.field_150660_b, var5.func_177984_a())) {
               var11 = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (var11 == null) {
            var11 = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.func_180360_a(var11);
         this.field_180366_e = this.field_180366_e.func_177226_a(this.field_180365_d.func_176560_l(), var11);
         if (var2 || this.field_150660_b.func_180495_p(this.field_180367_c) != this.field_180366_e) {
            this.field_150660_b.func_180501_a(this.field_180367_c, this.field_180366_e, 3);

            for(int var12 = 0; var12 < this.field_150657_g.size(); ++var12) {
               BlockRailBase.Rail var13 = this.func_180697_b((BlockPos)this.field_150657_g.get(var12));
               if (var13 != null) {
                  var13.func_150651_b();
                  if (var13.func_150649_b(this)) {
                     var13.func_150645_c(this);
                  }
               }
            }
         }

         return this;
      }

      public IBlockState func_180362_b() {
         return this.field_180366_e;
      }
   }
}
