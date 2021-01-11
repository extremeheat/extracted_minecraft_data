package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class BlockTNT extends Block {
   public static final PropertyBool field_176246_a = PropertyBool.func_177716_a("explode");

   public BlockTNT() {
      super(Material.field_151590_u);
      this.func_180632_j(this.field_176227_L.func_177621_b().func_177226_a(field_176246_a, false));
      this.func_149647_a(CreativeTabs.field_78028_d);
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
      super.func_176213_c(var1, var2, var3);
      if (var1.func_175640_z(var2)) {
         this.func_176206_d(var1, var2, var3.func_177226_a(field_176246_a, true));
         var1.func_175698_g(var2);
      }

   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
      if (var1.func_175640_z(var2)) {
         this.func_176206_d(var1, var2, var3.func_177226_a(field_176246_a, true));
         var1.func_175698_g(var2);
      }

   }

   public void func_180652_a(World var1, BlockPos var2, Explosion var3) {
      if (!var1.field_72995_K) {
         EntityTNTPrimed var4 = new EntityTNTPrimed(var1, (double)((float)var2.func_177958_n() + 0.5F), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + 0.5F), var3.func_94613_c());
         var4.field_70516_a = var1.field_73012_v.nextInt(var4.field_70516_a / 4) + var4.field_70516_a / 8;
         var1.func_72838_d(var4);
      }
   }

   public void func_176206_d(World var1, BlockPos var2, IBlockState var3) {
      this.func_180692_a(var1, var2, var3, (EntityLivingBase)null);
   }

   public void func_180692_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4) {
      if (!var1.field_72995_K) {
         if ((Boolean)var3.func_177229_b(field_176246_a)) {
            EntityTNTPrimed var5 = new EntityTNTPrimed(var1, (double)((float)var2.func_177958_n() + 0.5F), (double)var2.func_177956_o(), (double)((float)var2.func_177952_p() + 0.5F), var4);
            var1.func_72838_d(var5);
            var1.func_72956_a(var5, "game.tnt.primed", 1.0F, 1.0F);
         }

      }
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var4.func_71045_bC() != null) {
         Item var9 = var4.func_71045_bC().func_77973_b();
         if (var9 == Items.field_151033_d || var9 == Items.field_151059_bz) {
            this.func_180692_a(var1, var2, var3.func_177226_a(field_176246_a, true), var4);
            var1.func_175698_g(var2);
            if (var9 == Items.field_151033_d) {
               var4.func_71045_bC().func_77972_a(1, var4);
            } else if (!var4.field_71075_bZ.field_75098_d) {
               --var4.func_71045_bC().field_77994_a;
            }

            return true;
         }
      }

      return super.func_180639_a(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
      if (!var1.field_72995_K && var4 instanceof EntityArrow) {
         EntityArrow var5 = (EntityArrow)var4;
         if (var5.func_70027_ad()) {
            this.func_180692_a(var1, var2, var1.func_180495_p(var2).func_177226_a(field_176246_a, true), var5.field_70250_c instanceof EntityLivingBase ? (EntityLivingBase)var5.field_70250_c : null);
            var1.func_175698_g(var2);
         }
      }

   }

   public boolean func_149659_a(Explosion var1) {
      return false;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P().func_177226_a(field_176246_a, (var1 & 1) > 0);
   }

   public int func_176201_c(IBlockState var1) {
      return (Boolean)var1.func_177229_b(field_176246_a) ? 1 : 0;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[]{field_176246_a});
   }
}
