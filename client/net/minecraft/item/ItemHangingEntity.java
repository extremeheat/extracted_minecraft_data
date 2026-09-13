package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
   private final Class field_82811_a;

   public ItemHangingEntity(Class var1) {
      super();
      this.field_82811_a = var1;
      this.func_77637_a(CreativeTabs.field_78031_c);
   }

   @Override
   public boolean func_77648_a(ItemStack var1, EntityPlayer var2, World var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (var7 == 0) {
         return false;
      } else if (var7 == 1) {
         return false;
      } else {
         int var11 = Direction.field_71579_d[var7];
         EntityHanging var12 = this.func_82810_a(var3, var4, var5, var6, var11);
         if (!var2.func_82247_a(var4, var5, var6, var7, var1)) {
            return false;
         } else {
            if (var12 != null && var12.func_70518_d()) {
               if (!var3.field_72995_K) {
                  var3.func_72838_d(var12);
               }

               --var1.field_77994_a;
            }

            return true;
         }
      }
   }

   private EntityHanging func_82810_a(World var1, int var2, int var3, int var4, int var5) {
      if (this.field_82811_a == EntityPainting.class) {
         return new EntityPainting(var1, var2, var3, var4, var5);
      } else {
         return this.field_82811_a == EntityItemFrame.class ? new EntityItemFrame(var1, var2, var3, var4, var5) : null;
      }
   }
}
