package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
   private final Class<? extends EntityHanging> field_82811_a;

   public ItemHangingEntity(Class<? extends EntityHanging> var1) {
      super();
      this.field_82811_a = var1;
      this.func_77637_a(CreativeTabs.field_78031_c);
   }

   public boolean func_180614_a(ItemStack var1, EntityPlayer var2, World var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (var5 == EnumFacing.DOWN) {
         return false;
      } else if (var5 == EnumFacing.UP) {
         return false;
      } else {
         BlockPos var9 = var4.func_177972_a(var5);
         if (!var2.func_175151_a(var9, var5, var1)) {
            return false;
         } else {
            EntityHanging var10 = this.func_179233_a(var3, var9, var5);
            if (var10 != null && var10.func_70518_d()) {
               if (!var3.field_72995_K) {
                  var3.func_72838_d(var10);
               }

               --var1.field_77994_a;
            }

            return true;
         }
      }
   }

   private EntityHanging func_179233_a(World var1, BlockPos var2, EnumFacing var3) {
      if (this.field_82811_a == EntityPainting.class) {
         return new EntityPainting(var1, var2, var3);
      } else {
         return this.field_82811_a == EntityItemFrame.class ? new EntityItemFrame(var1, var2, var3) : null;
      }
   }
}
