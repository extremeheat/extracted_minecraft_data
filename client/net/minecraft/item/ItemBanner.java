package net.minecraft.item;

import java.util.List;
import net.minecraft.block.BlockStandingSign;
import net.minecraft.block.BlockWallSign;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBanner extends ItemBlock {
   public ItemBanner() {
      super(Blocks.field_180393_cK);
      this.field_77777_bU = 16;
      this.func_77637_a(CreativeTabs.field_78031_c);
      this.func_77627_a(true);
      this.func_77656_e(0);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      } else if (!var3.func_180495_p(var4).func_177230_c().func_149688_o().func_76220_a()) {
         return false;
      } else {
         var4 = var4.func_177972_a(var5);
         if (!var2.func_175151_a(var4, var5, var1)) {
            return false;
         } else if (!Blocks.field_180393_cK.func_176196_c(var3, var4)) {
            return false;
         } else if (var3.field_72995_K) {
            return true;
         } else {
            if (var5 == EnumFacing.UP) {
               int var9 = MathHelper.func_76128_c((double)((var2.field_70177_z + 180.0F) * 16.0F / 360.0F) + 0.5D) & 15;
               var3.func_180501_a(var4, Blocks.field_180393_cK.func_176223_P().func_177226_a(BlockStandingSign.field_176413_a, var9), 3);
            } else {
               var3.func_180501_a(var4, Blocks.field_180394_cL.func_176223_P().func_177226_a(BlockWallSign.field_176412_a, var5), 3);
            }

            --var1.field_77994_a;
            TileEntity var10 = var3.func_175625_s(var4);
            if (var10 instanceof TileEntityBanner) {
               ((TileEntityBanner)var10).func_175112_a(var1);
            }

            return true;
         }
      }
   }

   public String func_77653_i(ItemStack var1) {
      String var2 = "item.banner.";
      EnumDyeColor var3 = this.func_179225_h(var1);
      var2 = var2 + var3.func_176762_d() + ".name";
      return StatCollector.func_74838_a(var2);
   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      NBTTagCompound var5 = var1.func_179543_a("BlockEntityTag", false);
      if (var5 != null && var5.func_74764_b("Patterns")) {
         NBTTagList var6 = var5.func_150295_c("Patterns", 10);

         for(int var7 = 0; var7 < var6.func_74745_c() && var7 < 6; ++var7) {
            NBTTagCompound var8 = var6.func_150305_b(var7);
            EnumDyeColor var9 = EnumDyeColor.func_176766_a(var8.func_74762_e("Color"));
            TileEntityBanner.EnumBannerPattern var10 = TileEntityBanner.EnumBannerPattern.func_177268_a(var8.func_74779_i("Pattern"));
            if (var10 != null) {
               var3.add(StatCollector.func_74838_a("item.banner." + var10.func_177271_a() + "." + var9.func_176762_d()));
            }
         }

      }
   }

   public int func_82790_a(ItemStack var1, int var2) {
      if (var2 == 0) {
         return 16777215;
      } else {
         EnumDyeColor var3 = this.func_179225_h(var1);
         return var3.func_176768_e().field_76291_p;
      }
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      EnumDyeColor[] var4 = EnumDyeColor.values();
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         EnumDyeColor var7 = var4[var6];
         NBTTagCompound var8 = new NBTTagCompound();
         TileEntityBanner.func_181020_a(var8, var7.func_176767_b(), (NBTTagList)null);
         NBTTagCompound var9 = new NBTTagCompound();
         var9.func_74782_a("BlockEntityTag", var8);
         ItemStack var10 = new ItemStack(var1, 1, var7.func_176767_b());
         var10.func_77982_d(var9);
         var3.add(var10);
      }

   }

   public CreativeTabs func_77640_w() {
      return CreativeTabs.field_78031_c;
   }

   private EnumDyeColor func_179225_h(ItemStack var1) {
      NBTTagCompound var2 = var1.func_179543_a("BlockEntityTag", false);
      EnumDyeColor var3 = null;
      if (var2 != null && var2.func_74764_b("Base")) {
         var3 = EnumDyeColor.func_176766_a(var2.func_74762_e("Base"));
      } else {
         var3 = EnumDyeColor.func_176766_a(var1.func_77960_j());
      }

      return var3;
   }
}
