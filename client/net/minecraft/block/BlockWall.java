package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWall extends Block {
   public static final PropertyBool field_176256_a = PropertyBool.func_177716_a("up");
   public static final PropertyBool field_176254_b = PropertyBool.func_177716_a("north");
   public static final PropertyBool field_176257_M = PropertyBool.func_177716_a("east");
   public static final PropertyBool field_176258_N = PropertyBool.func_177716_a("south");
   public static final PropertyBool field_176259_O = PropertyBool.func_177716_a("west");
   public static final PropertyEnum<BlockWall.EnumType> field_176255_P = PropertyEnum.func_177709_a("variant", BlockWall.EnumType.class);

   public BlockWall(Block var1) {
      super(var1.field_149764_J);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176256_a, false).func_177226_a(field_176254_b, false).func_177226_a(field_176257_M, false).func_177226_a(field_176258_N, false).func_177226_a(field_176259_O, false).func_177226_a(field_176255_P, BlockWall.EnumType.NORMAL));
      this.func_149711_c(var1.field_149782_v);
      this.func_149752_b(var1.field_149781_w / 3.0F);
      this.func_149672_a(var1.field_149762_H);
      this.func_149647_a(CreativeTabs.field_78030_b);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + "." + BlockWall.EnumType.NORMAL.func_176659_c() + ".name");
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return false;
   }

   public boolean func_149662_c() {
      return false;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      boolean var3 = this.func_176253_e(var1, var2.func_177978_c());
      boolean var4 = this.func_176253_e(var1, var2.func_177968_d());
      boolean var5 = this.func_176253_e(var1, var2.func_177976_e());
      boolean var6 = this.func_176253_e(var1, var2.func_177974_f());
      float var7 = 0.25F;
      float var8 = 0.75F;
      float var9 = 0.25F;
      float var10 = 0.75F;
      float var11 = 1.0F;
      if (var3) {
         var9 = 0.0F;
      }

      if (var4) {
         var10 = 1.0F;
      }

      if (var5) {
         var7 = 0.0F;
      }

      if (var6) {
         var8 = 1.0F;
      }

      if (var3 && var4 && !var5 && !var6) {
         var11 = 0.8125F;
         var7 = 0.3125F;
         var8 = 0.6875F;
      } else if (!var3 && !var4 && var5 && var6) {
         var11 = 0.8125F;
         var9 = 0.3125F;
         var10 = 0.6875F;
      }

      this.func_149676_a(var7, 0.0F, var9, var8, var11, var10);
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      this.func_180654_a(var1, var2);
      this.field_149756_F = 1.5D;
      return super.func_180640_a(var1, var2, var3);
   }

   public boolean func_176253_e(IBlockAccess var1, BlockPos var2) {
      Block var3 = var1.func_180495_p(var2).func_177230_c();
      if (var3 == Blocks.field_180401_cv) {
         return false;
      } else if (var3 != this && !(var3 instanceof BlockFenceGate)) {
         if (var3.field_149764_J.func_76218_k() && var3.func_149686_d()) {
            return var3.field_149764_J != Material.field_151572_C;
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      BlockWall.EnumType[] var4 = BlockWall.EnumType.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         BlockWall.EnumType var7 = var4[var6];
         var3.add(new ItemStack(var1, 1, var7.func_176657_a()));
      }

   }

   public int func_180651_a(IBlockState var1) {
      return ((BlockWall.EnumType)var1.func_177229_b(field_176255_P)).func_176657_a();
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var3 == EnumFacing.DOWN ? super.func_176225_a(var1, var2, var3) : true;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176255_P, BlockWall.EnumType.func_176660_a(var1));
   }

   public int func_176201_c(IBlockState var1) {
      return ((BlockWall.EnumType)var1.func_177229_b(field_176255_P)).func_176657_a();
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1.func_177226_a(field_176256_a, !var2.func_175623_d(var3.func_177984_a())).func_177226_a(field_176254_b, this.func_176253_e(var2, var3.func_177978_c())).func_177226_a(field_176257_M, this.func_176253_e(var2, var3.func_177974_f())).func_177226_a(field_176258_N, this.func_176253_e(var2, var3.func_177968_d())).func_177226_a(field_176259_O, this.func_176253_e(var2, var3.func_177976_e()));
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176256_a, field_176254_b, field_176257_M, field_176259_O, field_176258_N, field_176255_P});
   }

   public static enum EnumType implements IStringSerializable {
      NORMAL(0, "cobblestone", "normal"),
      MOSSY(1, "mossy_cobblestone", "mossy");

      private static final BlockWall.EnumType[] field_176666_c = new BlockWall.EnumType[values().length];
      private final int field_176663_d;
      private final String field_176664_e;
      private String field_176661_f;

      private EnumType(int var3, String var4, String var5) {
         this.field_176663_d = var3;
         this.field_176664_e = var4;
         this.field_176661_f = var5;
      }

      public int func_176657_a() {
         return this.field_176663_d;
      }

      public String toString() {
         return this.field_176664_e;
      }

      public static BlockWall.EnumType func_176660_a(int var0) {
         if (var0 < 0 || var0 >= field_176666_c.length) {
            var0 = 0;
         }

         return field_176666_c[var0];
      }

      public String func_176610_l() {
         return this.field_176664_e;
      }

      public String func_176659_c() {
         return this.field_176661_f;
      }

      static {
         BlockWall.EnumType[] var0 = values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            BlockWall.EnumType var3 = var0[var2];
            field_176666_c[var3.func_176657_a()] = var3;
         }

      }
   }
}
