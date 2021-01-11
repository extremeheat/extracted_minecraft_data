package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSkull extends Item {
   private static final String[] field_82807_a = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};

   public ItemSkull() {
      super();
      this.func_77637_a(CreativeTabs.field_78031_c);
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      } else {
         IBlockState var9 = var3.func_180495_p(var4);
         Block var10 = var9.func_177230_c();
         boolean var11 = var10.func_176200_f(var3, var4);
         if (!var11) {
            if (!var3.func_180495_p(var4).func_177230_c().func_149688_o().func_76220_a()) {
               return false;
            }

            var4 = var4.func_177972_a(var5);
         }

         if (!var2.func_175151_a(var4, var5, var1)) {
            return false;
         } else if (!Blocks.field_150465_bP.func_176196_c(var3, var4)) {
            return false;
         } else {
            if (!var3.field_72995_K) {
               var3.func_180501_a(var4, Blocks.field_150465_bP.func_176223_P().func_177226_a(BlockSkull.field_176418_a, var5), 3);
               int var12 = 0;
               if (var5 == EnumFacing.UP) {
                  var12 = MathHelper.func_76128_c((double)(var2.field_70177_z * 16.0F / 360.0F) + 0.5D) & 15;
               }

               TileEntity var13 = var3.func_175625_s(var4);
               if (var13 instanceof TileEntitySkull) {
                  TileEntitySkull var14 = (TileEntitySkull)var13;
                  if (var1.func_77960_j() == 3) {
                     GameProfile var15 = null;
                     if (var1.func_77942_o()) {
                        NBTTagCompound var16 = var1.func_77978_p();
                        if (var16.func_150297_b("SkullOwner", 10)) {
                           var15 = NBTUtil.func_152459_a(var16.func_74775_l("SkullOwner"));
                        } else if (var16.func_150297_b("SkullOwner", 8) && var16.func_74779_i("SkullOwner").length() > 0) {
                           var15 = new GameProfile((UUID)null, var16.func_74779_i("SkullOwner"));
                        }
                     }

                     var14.func_152106_a(var15);
                  } else {
                     var14.func_152107_a(var1.func_77960_j());
                  }

                  var14.func_145903_a(var12);
                  Blocks.field_150465_bP.func_180679_a(var3, var4, var14);
               }

               --var1.field_77994_a;
            }

            return true;
         }
      }
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      for(int var4 = 0; var4 < field_82807_a.length; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }

   }

   public int func_77647_b(int var1) {
      return var1;
   }

   public String func_77667_c(ItemStack var1) {
      int var2 = var1.func_77960_j();
      if (var2 < 0 || var2 >= field_82807_a.length) {
         var2 = 0;
      }

      return super.func_77658_a() + "." + field_82807_a[var2];
   }

   public String func_77653_i(ItemStack var1) {
      if (var1.func_77960_j() == 3 && var1.func_77942_o()) {
         if (var1.func_77978_p().func_150297_b("SkullOwner", 8)) {
            return StatCollector.func_74837_a("item.skull.player.name", var1.func_77978_p().func_74779_i("SkullOwner"));
         }

         if (var1.func_77978_p().func_150297_b("SkullOwner", 10)) {
            NBTTagCompound var2 = var1.func_77978_p().func_74775_l("SkullOwner");
            if (var2.func_150297_b("Name", 8)) {
               return StatCollector.func_74837_a("item.skull.player.name", var2.func_74779_i("Name"));
            }
         }
      }

      return super.func_77653_i(var1);
   }

   public boolean func_179215_a(NBTTagCompound var1) {
      super.func_179215_a(var1);
      if (var1.func_150297_b("SkullOwner", 8) && var1.func_74779_i("SkullOwner").length() > 0) {
         GameProfile var2 = new GameProfile((UUID)null, var1.func_74779_i("SkullOwner"));
         var2 = TileEntitySkull.func_174884_b(var2);
         var1.func_74782_a("SkullOwner", NBTUtil.func_180708_a(new NBTTagCompound(), var2));
         return true;
      } else {
         return false;
      }
   }
}
