package net.minecraft.item;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.block.BlockSkull;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSkull extends Item {
   private static final String[] field_82807_a = new String[]{"skeleton", "wither", "zombie", "char", "creeper"};
   public static final String[] field_94587_a = new String[]{"skeleton", "wither", "zombie", "steve", "creeper"};
   private IIcon[] field_94586_c;

   public ItemSkull() {
      super();
      this.func_77637_a(CreativeTabs.field_78031_c);
      this.func_77656_e(0);
      this.func_77627_a(true);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var7 == 0) {
         return false;
      } else if (!var3.func_147439_a(var4, var5, var6).func_149688_o().func_76220_a()) {
         return false;
      } else {
         if (var7 == 1) {
            ++var5;
         }

         if (var7 == 2) {
            --var6;
         }

         if (var7 == 3) {
            ++var6;
         }

         if (var7 == 4) {
            --var4;
         }

         if (var7 == 5) {
            ++var4;
         }

         if (!var3.field_72995_K) {
            var3.func_147465_d(var4, var5, var6, Blocks.field_150465_bP, var7, 2);
            int var11 = 0;
            if (var7 == 1) {
               var11 = MathHelper.func_76128_c((double)(var2.field_70177_z * 16.0F / 360.0F) + 0.5) & 15;
            }

            TileEntity var12 = var3.func_147438_o(var4, var5, var6);
            if (var12 != null && var12 instanceof TileEntitySkull) {
               if (var1.func_77960_j() == 3) {
                  GameProfile var13 = null;
                  if (var1.func_77942_o()) {
                     NBTTagCompound var14 = var1.func_77978_p();
                     if (var14.func_150297_b("SkullOwner", 10)) {
                        var13 = NBTUtil.func_152459_a(var14.func_74775_l("SkullOwner"));
                     } else if (var14.func_150297_b("SkullOwner", 8) && var14.func_74779_i("SkullOwner").length() > 0) {
                        var13 = new GameProfile(null, var14.func_74779_i("SkullOwner"));
                     }
                  }

                  ((TileEntitySkull)var12).func_152106_a(var13);
               } else {
                  ((TileEntitySkull)var12).func_152107_a(var1.func_77960_j());
               }

               ((TileEntitySkull)var12).func_145903_a(var11);
               ((BlockSkull)Blocks.field_150465_bP).func_149965_a(var3, var4, var5, var6, (TileEntitySkull)var12);
            }

            --var1.field_77994_a;
         }

         return true;
      }
   }

   @Override
   public void func_150895_a(Item var1, CreativeTabs var2, List var3) {
      for(int var4 = 0; var4 < field_82807_a.length; ++var4) {
         var3.add(new ItemStack(var1, 1, var4));
      }
   }

   @Override
   public IIcon func_77617_a(int var1) {
      if (var1 < 0 || var1 >= field_82807_a.length) {
         var1 = 0;
      }

      return this.field_94586_c[var1];
   }

   @Override
   public int func_77647_b(int var1) {
      return var1;
   }

   @Override
   public String func_77667_c(ItemStack var1) {
      int var2 = var1.func_77960_j();
      if (var2 < 0 || var2 >= field_82807_a.length) {
         var2 = 0;
      }

      return super.func_77658_a() + "." + field_82807_a[var2];
   }

   @Override
   public String func_77653_i(ItemStack var1) {
      if (var1.func_77960_j() == 3 && var1.func_77942_o()) {
         if (var1.func_77978_p().func_150297_b("SkullOwner", 10)) {
            return StatCollector.func_74837_a("item.skull.player.name", NBTUtil.func_152459_a(var1.func_77978_p().func_74775_l("SkullOwner")).getName());
         }

         if (var1.func_77978_p().func_150297_b("SkullOwner", 8)) {
            return StatCollector.func_74837_a("item.skull.player.name", var1.func_77978_p().func_74779_i("SkullOwner"));
         }
      }

      return super.func_77653_i(var1);
   }

   @Override
   public void func_94581_a(IIconRegister var1) {
      this.field_94586_c = new IIcon[field_94587_a.length];

      for(int var2 = 0; var2 < field_94587_a.length; ++var2) {
         this.field_94586_c[var2] = var1.func_94245_a(this.func_111208_A() + "_" + field_94587_a[var2]);
      }
   }
}
