package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlock extends Item {
   protected final Block field_150939_a;

   public ItemBlock(Block var1) {
      super();
      this.field_150939_a = var1;
   }

   public ItemBlock func_77655_b(String var1) {
      super.func_77655_b(var1);
      return this;
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      IBlockState var9 = var3.func_180495_p(var4);
      Block var10 = var9.func_177230_c();
      if (!var10.func_176200_f(var3, var4)) {
         var4 = var4.func_177972_a(var5);
      }

      if (var1.field_77994_a == 0) {
         return false;
      } else if (!var2.func_175151_a(var4, var5, var1)) {
         return false;
      } else if (var3.func_175716_a(this.field_150939_a, var4, false, var5, (Entity)null, var1)) {
         int var11 = this.func_77647_b(var1.func_77960_j());
         IBlockState var12 = this.field_150939_a.func_180642_a(var3, var4, var5, var6, var7, var8, var11, var2);
         if (var3.func_180501_a(var4, var12, 3)) {
            var12 = var3.func_180495_p(var4);
            if (var12.func_177230_c() == this.field_150939_a) {
               func_179224_a(var3, var2, var4, var1);
               this.field_150939_a.func_180633_a(var3, var4, var12, var2, var1);
            }

            var3.func_72908_a((double)((float)var4.func_177958_n() + 0.5F), (double)((float)var4.func_177956_o() + 0.5F), (double)((float)var4.func_177952_p() + 0.5F), this.field_150939_a.field_149762_H.func_150496_b(), (this.field_150939_a.field_149762_H.func_150497_c() + 1.0F) / 2.0F, this.field_150939_a.field_149762_H.func_150494_d() * 0.8F);
            --var1.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }

   public static boolean func_179224_a(World var0, EntityPlayer var1, BlockPos var2, ItemStack var3) {
      MinecraftServer var4 = MinecraftServer.func_71276_C();
      if (var4 == null) {
         return false;
      } else {
         if (var3.func_77942_o() && var3.func_77978_p().func_150297_b("BlockEntityTag", 10)) {
            TileEntity var5 = var0.func_175625_s(var2);
            if (var5 != null) {
               if (!var0.field_72995_K && var5.func_183000_F() && !var4.func_71203_ab().func_152596_g(var1.func_146103_bH())) {
                  return false;
               }

               NBTTagCompound var6 = new NBTTagCompound();
               NBTTagCompound var7 = (NBTTagCompound)var6.func_74737_b();
               var5.func_145841_b(var6);
               NBTTagCompound var8 = (NBTTagCompound)var3.func_77978_p().func_74781_a("BlockEntityTag");
               var6.func_179237_a(var8);
               var6.func_74768_a("x", var2.func_177958_n());
               var6.func_74768_a("y", var2.func_177956_o());
               var6.func_74768_a("z", var2.func_177952_p());
               if (!var6.equals(var7)) {
                  var5.func_145839_a(var6);
                  var5.func_70296_d();
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean func_179222_a(World var1, BlockPos var2, EnumFacing var3, EntityPlayer var4, ItemStack var5) {
      Block var6 = var1.func_180495_p(var2).func_177230_c();
      if (var6 == Blocks.field_150431_aC) {
         var3 = EnumFacing.UP;
      } else if (!var6.func_176200_f(var1, var2)) {
         var2 = var2.func_177972_a(var3);
      }

      return var1.func_175716_a(this.field_150939_a, var2, false, var3, (Entity)null, var5);
   }

   public String func_77667_c(ItemStack var1) {
      return this.field_150939_a.func_149739_a();
   }

   public String func_77658_a() {
      return this.field_150939_a.func_149739_a();
   }

   public CreativeTabs func_77640_w() {
      return this.field_150939_a.func_149708_J();
   }

   public void func_150895_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      this.field_150939_a.func_149666_a(var1, var2, var3);
   }

   public Block func_179223_d() {
      return this.field_150939_a;
   }

   // $FF: synthetic method
   public Item func_77655_b(String var1) {
      return this.func_77655_b(var1);
   }
}
