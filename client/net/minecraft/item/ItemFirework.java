package net.minecraft.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item {
   public ItemFirework() {
      super();
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (!var3.field_72995_K) {
         EntityFireworkRocket var9 = new EntityFireworkRocket(var3, (double)((float)var4.func_177958_n() + var6), (double)((float)var4.func_177956_o() + var7), (double)((float)var4.func_177952_p() + var8), var1);
         var3.func_72838_d(var9);
         if (!var2.field_71075_bZ.field_75098_d) {
            --var1.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }

   public void func_77624_a(ItemStack var1, EntityPlayer var2, List<String> var3, boolean var4) {
      if (var1.func_77942_o()) {
         NBTTagCompound var5 = var1.func_77978_p().func_74775_l("Fireworks");
         if (var5 != null) {
            if (var5.func_150297_b("Flight", 99)) {
               var3.add(StatCollector.func_74838_a("item.fireworks.flight") + " " + var5.func_74771_c("Flight"));
            }

            NBTTagList var6 = var5.func_150295_c("Explosions", 10);
            if (var6 != null && var6.func_74745_c() > 0) {
               for(int var7 = 0; var7 < var6.func_74745_c(); ++var7) {
                  NBTTagCompound var8 = var6.func_150305_b(var7);
                  ArrayList var9 = Lists.newArrayList();
                  ItemFireworkCharge.func_150902_a(var8, var9);
                  if (var9.size() > 0) {
                     for(int var10 = 1; var10 < var9.size(); ++var10) {
                        var9.set(var10, "  " + (String)var9.get(var10));
                     }

                     var3.addAll(var9);
                  }
               }
            }

         }
      }
   }
}
