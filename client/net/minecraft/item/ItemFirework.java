package net.minecraft.item;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemFirework extends Item {
   public ItemFirework() {
      super();
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (!var3.field_72995_K) {
         EntityFireworkRocket var11 = new EntityFireworkRocket(
            var3, (double)((float)var4 + var8), (double)((float)var5 + var9), (double)((float)var6 + var10), var1
         );
         var3.func_72838_d(var11);
         if (!var2.field_71075_bZ.field_75098_d) {
            --var1.field_77994_a;
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void func_77624_a(ItemStack var1, EntityPlayer var2, List var3, boolean var4) {
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
                  ArrayList var9 = new ArrayList();
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
