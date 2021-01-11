package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBanner extends BlockContainer {
   public static final PropertyDirection field_176449_a;
   public static final PropertyInteger field_176448_b;

   protected BlockBanner() {
      super(Material.field_151575_d);
      float var1 = 0.25F;
      float var2 = 1.0F;
      this.func_149676_a(0.5F - var1, 0.0F, 0.5F - var1, 0.5F + var1, var2, 0.5F + var1);
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a("item.banner.white.name");
   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return null;
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      this.func_180654_a(var1, var2);
      return super.func_180646_a(var1, var2);
   }

   public boolean func_149686_d() {
      return false;
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return true;
   }

   public boolean func_149662_c() {
      return false;
   }

   public boolean func_181623_g() {
      return true;
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new TileEntityBanner();
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Items.field_179564_cE;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Items.field_179564_cE;
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      TileEntity var6 = var1.func_175625_s(var2);
      if (var6 instanceof TileEntityBanner) {
         ItemStack var7 = new ItemStack(Items.field_179564_cE, 1, ((TileEntityBanner)var6).func_175115_b());
         NBTTagCompound var8 = new NBTTagCompound();
         var6.func_145841_b(var8);
         var8.func_82580_o("x");
         var8.func_82580_o("y");
         var8.func_82580_o("z");
         var8.func_82580_o("id");
         var7.func_77983_a("BlockEntityTag", var8);
         func_180635_a(var1, var2, var7);
      } else {
         super.func_180653_a(var1, var2, var3, var4, var5);
      }

   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return !this.func_181087_e(var1, var2) && super.func_176196_c(var1, var2);
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      if (var5 instanceof TileEntityBanner) {
         TileEntityBanner var6 = (TileEntityBanner)var5;
         ItemStack var7 = new ItemStack(Items.field_179564_cE, 1, ((TileEntityBanner)var5).func_175115_b());
         NBTTagCompound var8 = new NBTTagCompound();
         TileEntityBanner.func_181020_a(var8, var6.func_175115_b(), var6.func_181021_d());
         var7.func_77983_a("BlockEntityTag", var8);
         func_180635_a(var1, var3, var7);
      } else {
         super.func_180657_a(var1, var2, var3, var4, (TileEntity)null);
      }

   }

   static {
      field_176449_a = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
      field_176448_b = PropertyInteger.func_177719_a("rotation", 0, 15);
   }

   public static class BlockBannerStanding extends BlockBanner {
      public BlockBannerStanding() {
         super();
         this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176448_b, 0));
      }

      public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
         if (!var1.func_180495_p(var2.func_177977_b()).func_177230_c().func_149688_o().func_76220_a()) {
            this.func_176226_b(var1, var2, var3, 0);
            var1.func_175698_g(var2);
         }

         super.func_176204_a(var1, var2, var3, var4);
      }

      public IBlockState func_176203_a(int var1) {
         return this.func_176223_P().func_177226_a(field_176448_b, var1);
      }

      public int func_176201_c(IBlockState var1) {
         return (Integer)var1.func_177229_b(field_176448_b);
      }

      protected BlockState func_180661_e() {
         return new BlockState(this, new IProperty[]{field_176448_b});
      }
   }

   public static class BlockBannerHanging extends BlockBanner {
      public BlockBannerHanging() {
         super();
         this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176449_a, EnumFacing.NORTH));
      }

      public void func_180654_a(IBlockAccess var1, BlockPos var2) {
         EnumFacing var3 = (EnumFacing)var1.func_180495_p(var2).func_177229_b(field_176449_a);
         float var4 = 0.0F;
         float var5 = 0.78125F;
         float var6 = 0.0F;
         float var7 = 1.0F;
         float var8 = 0.125F;
         this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         switch(var3) {
         case NORTH:
         default:
            this.func_149676_a(var6, var4, 1.0F - var8, var7, var5, 1.0F);
            break;
         case SOUTH:
            this.func_149676_a(var6, var4, 0.0F, var7, var5, var8);
            break;
         case WEST:
            this.func_149676_a(1.0F - var8, var4, var6, 1.0F, var5, var7);
            break;
         case EAST:
            this.func_149676_a(0.0F, var4, var6, var8, var5, var7);
         }

      }

      public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
         EnumFacing var5 = (EnumFacing)var3.func_177229_b(field_176449_a);
         if (!var1.func_180495_p(var2.func_177972_a(var5.func_176734_d())).func_177230_c().func_149688_o().func_76220_a()) {
            this.func_176226_b(var1, var2, var3, 0);
            var1.func_175698_g(var2);
         }

         super.func_176204_a(var1, var2, var3, var4);
      }

      public IBlockState func_176203_a(int var1) {
         EnumFacing var2 = EnumFacing.func_82600_a(var1);
         if (var2.func_176740_k() == EnumFacing.Axis.Y) {
            var2 = EnumFacing.NORTH;
         }

         return this.func_176223_P().func_177226_a(field_176449_a, var2);
      }

      public int func_176201_c(IBlockState var1) {
         return ((EnumFacing)var1.func_177229_b(field_176449_a)).func_176745_a();
      }

      protected BlockState func_180661_e() {
         return new BlockState(this, new IProperty[]{field_176449_a});
      }
   }
}
