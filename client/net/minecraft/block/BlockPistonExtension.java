package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonExtension extends Block {
   public static final PropertyDirection field_176326_a = PropertyDirection.func_177714_a("facing");
   public static final PropertyEnum<BlockPistonExtension.EnumPistonType> field_176325_b = PropertyEnum.func_177709_a("type", BlockPistonExtension.EnumPistonType.class);
   public static final PropertyBool field_176327_M = PropertyBool.func_177716_a("short");

   public BlockPistonExtension() {
      super(Material.field_76233_E);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176326_a, EnumFacing.NORTH).func_177226_a(field_176325_b, BlockPistonExtension.EnumPistonType.DEFAULT).func_177226_a(field_176327_M, false));
      this.func_149672_a(field_149780_i);
      this.func_149711_c(0.5F);
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      if (var4.field_71075_bZ.field_75098_d) {
         EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176326_a);
         if (var5 != null) {
            BlockPos var6 = var2.func_177972_a(var5.func_176734_d());
            Block var7 = var1.func_180495_p(var6).func_177230_c();
            if (var7 == Blocks.field_150331_J || var7 == Blocks.field_150320_F) {
               var1.func_175698_g(var6);
            }
         }
      }

      super.func_176208_a(var1, var2, var3, var4);
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      super.func_180663_b(var1, var2, var3);
      EnumFacing var4 = ((EnumFacing)var3.func_177229_b(field_176326_a)).func_176734_d();
      var2 = var2.func_177972_a(var4);
      IBlockState var5 = var1.func_180495_p(var2);
      if ((var5.func_177230_c() == Blocks.field_150331_J || var5.func_177230_c() == Blocks.field_150320_F) && (Boolean)var5.func_177229_b(BlockPistonBase.field_176320_b)) {
         var5.func_177230_c().func_176226_b(var1, var2, var5, 0);
         var1.func_175698_g(var2);
      }

   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return false;
   }

   public boolean func_176198_a(World var1, BlockPos var2, EnumFacing var3) {
      return false;
   }

   public int func_149745_a(Random var1) {
      return 0;
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      this.func_176324_d(var3);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_176323_e(var3);
      super.func_180638_a(var1, var2, var3, var4, var5, var6);
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   private void func_176323_e(IBlockState var1) {
      float var2 = 0.25F;
      float var3 = 0.375F;
      float var4 = 0.625F;
      float var5 = 0.25F;
      float var6 = 0.75F;
      switch((EnumFacing)var1.func_177229_b(field_176326_a)) {
      case DOWN:
         this.func_149676_a(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
         break;
      case UP:
         this.func_149676_a(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
         break;
      case NORTH:
         this.func_149676_a(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
         break;
      case SOUTH:
         this.func_149676_a(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
         break;
      case WEST:
         this.func_149676_a(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
         break;
      case EAST:
         this.func_149676_a(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
      }

   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
      this.func_176324_d(var1.func_180495_p(var2));
   }

   public void func_176324_d(IBlockState var1) {
      float var2 = 0.25F;
      EnumFacing var3 = (EnumFacing)var1.func_177229_b(field_176326_a);
      if (var3 != null) {
         switch(var3) {
         case DOWN:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
            break;
         case UP:
            this.func_149676_a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
            break;
         case NORTH:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
            break;
         case SOUTH:
            this.func_149676_a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
            break;
         case WEST:
            this.func_149676_a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
            break;
         case EAST:
            this.func_149676_a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         }

      }
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176326_a);
      BlockPos var6 = var2.func_177972_a(var5.func_176734_d());
      IBlockState var7 = var1.func_180495_p(var6);
      if (var7.func_177230_c() != Blocks.field_150331_J && var7.func_177230_c() != Blocks.field_150320_F) {
         var1.func_175698_g(var2);
      } else {
         var7.func_177230_c().func_176204_a(var1, var6, var7, var4);
      }

   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return true;
   }

   public static EnumFacing func_176322_b(int var0) {
      int var1 = var0 & 7;
      return var1 > 5 ? null : EnumFacing.func_82600_a(var1);
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return var1.func_180495_p(var2).func_177229_b(field_176325_b) == BlockPistonExtension.EnumPistonType.STICKY ? Item.func_150898_a(Blocks.field_150320_F) : Item.func_150898_a(Blocks.field_150331_J);
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176326_a, func_176322_b(var1)).func_177226_a(field_176325_b, (var1 & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
   }

   public int func_176201_c(IBlockState var1) {
      byte var2 = 0;
      int var3 = var2 | ((EnumFacing)var1.func_177229_b(field_176326_a)).func_176745_a();
      if (var1.func_177229_b(field_176325_b) == BlockPistonExtension.EnumPistonType.STICKY) {
         var3 |= 8;
      }

      return var3;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176326_a, field_176325_b, field_176327_M});
   }

   public static enum EnumPistonType implements IStringSerializable {
      DEFAULT("normal"),
      STICKY("sticky");

      private final String field_176714_c;

      private EnumPistonType(String var3) {
         this.field_176714_c = var3;
      }

      public String toString() {
         return this.field_176714_c;
      }

      public String func_176610_l() {
         return this.field_176714_c;
      }
   }
}
