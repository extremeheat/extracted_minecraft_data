package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockJukebox extends BlockContainer {
   public static final PropertyBool field_176432_a = PropertyBool.func_177716_a("has_record");

   protected BlockJukebox() {
      super(Material.field_151575_d, MapColor.field_151664_l);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176432_a, false));
      this.func_149647_a(CreativeTabs.field_78031_c);
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if ((Boolean)var3.func_177229_b(field_176432_a)) {
         this.func_180678_e(var1, var2, var3);
         var3 = var3.func_177226_a(field_176432_a, false);
         var1.func_180501_a(var2, var3, 2);
         return true;
      } else {
         return false;
      }
   }

   public void func_176431_a(World var1, BlockPos var2, IBlockState var3, ItemStack var4) {
      if (!var1.field_72995_K) {
         TileEntity var5 = var1.func_175625_s(var2);
         if (var5 instanceof BlockJukebox.TileEntityJukebox) {
            ((BlockJukebox.TileEntityJukebox)var5).func_145857_a(new ItemStack(var4.func_77973_b(), 1, var4.func_77960_j()));
            var1.func_180501_a(var2, var3.func_177226_a(field_176432_a, true), 2);
         }
      }
   }

   private void func_180678_e(World var1, BlockPos var2, IBlockState var3) {
      if (!var1.field_72995_K) {
         TileEntity var4 = var1.func_175625_s(var2);
         if (var4 instanceof BlockJukebox.TileEntityJukebox) {
            BlockJukebox.TileEntityJukebox var5 = (BlockJukebox.TileEntityJukebox)var4;
            ItemStack var6 = var5.func_145856_a();
            if (var6 != null) {
               var1.func_175718_b(1005, var2, 0);
               var1.func_175717_a(var2, (String)null);
               var5.func_145857_a((ItemStack)null);
               float var7 = 0.7F;
               double var8 = (double)(var1.field_73012_v.nextFloat() * var7) + (double)(1.0F - var7) * 0.5D;
               double var10 = (double)(var1.field_73012_v.nextFloat() * var7) + (double)(1.0F - var7) * 0.2D + 0.6D;
               double var12 = (double)(var1.field_73012_v.nextFloat() * var7) + (double)(1.0F - var7) * 0.5D;
               ItemStack var14 = var6.func_77946_l();
               EntityItem var15 = new EntityItem(var1, (double)var2.func_177958_n() + var8, (double)var2.func_177956_o() + var10, (double)var2.func_177952_p() + var12, var14);
               var15.func_174869_p();
               var1.func_72838_d(var15);
            }
         }
      }
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
      this.func_180678_e(var1, var2, var3);
      super.func_180663_b(var1, var2, var3);
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.field_72995_K) {
         super.func_180653_a(var1, var2, var3, var4, 0);
      }
   }

   public TileEntity func_149915_a(World var1, int var2) {
      return new BlockJukebox.TileEntityJukebox();
   }

   public boolean func_149740_M() {
      return true;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      TileEntity var3 = var1.func_175625_s(var2);
      if (var3 instanceof BlockJukebox.TileEntityJukebox) {
         ItemStack var4 = ((BlockJukebox.TileEntityJukebox)var3).func_145856_a();
         if (var4 != null) {
            return Item.func_150891_b(var4.func_77973_b()) + 1 - Item.func_150891_b(Items.field_151096_cd);
         }
      }

      return 0;
   }

   public int func_149645_b() {
      return 3;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176432_a, var1 > 0);
   }

   public int func_176201_c(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_176432_a) ? 1 : 0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176432_a});
   }

   public static class TileEntityJukebox extends TileEntity {
      private ItemStack field_145858_a;

      public TileEntityJukebox() {
         super();
      }

      public void func_145839_a(NBTTagCompound var1) {
         super.func_145839_a(var1);
         if (var1.func_150297_b("RecordItem", 10)) {
            this.func_145857_a(ItemStack.func_77949_a(var1.func_74775_l("RecordItem")));
         } else if (var1.func_74762_e("Record") > 0) {
            this.func_145857_a(new ItemStack(Item.func_150899_d(var1.func_74762_e("Record")), 1, 0));
         }

      }

      public void func_145841_b(NBTTagCompound var1) {
         super.func_145841_b(var1);
         if (this.func_145856_a() != null) {
            var1.func_74782_a("RecordItem", this.func_145856_a().func_77955_b(new NBTTagCompound()));
         }

      }

      public ItemStack func_145856_a() {
         return this.field_145858_a;
      }

      public void func_145857_a(ItemStack var1) {
         this.field_145858_a = var1;
         this.func_70296_d();
      }
   }
}
