package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemHangingEntity extends Item {
   private final Class<? extends EntityHanging> field_82811_a;

   public ItemHangingEntity(Class<? extends EntityHanging> var1, Item.Properties var2) {
      super(var2);
      this.field_82811_a = var1;
   }

   public EnumActionResult func_195939_a(ItemUseContext var1) {
      BlockPos var2 = var1.func_195995_a();
      EnumFacing var3 = var1.func_196000_l();
      BlockPos var4 = var2.func_177972_a(var3);
      EntityPlayer var5 = var1.func_195999_j();
      if (var5 != null && !this.func_200127_a(var5, var3, var1.func_195996_i(), var4)) {
         return EnumActionResult.FAIL;
      } else {
         World var6 = var1.func_195991_k();
         EntityHanging var7 = this.func_179233_a(var6, var4, var3);
         if (var7 != null && var7.func_70518_d()) {
            if (!var6.field_72995_K) {
               var7.func_184523_o();
               var6.func_72838_d(var7);
            }

            var1.func_195996_i().func_190918_g(1);
         }

         return EnumActionResult.SUCCESS;
      }
   }

   protected boolean func_200127_a(EntityPlayer var1, EnumFacing var2, ItemStack var3, BlockPos var4) {
      return !var2.func_176740_k().func_200128_b() && var1.func_175151_a(var4, var2, var3);
   }

   @Nullable
   private EntityHanging func_179233_a(World var1, BlockPos var2, EnumFacing var3) {
      if (this.field_82811_a == EntityPainting.class) {
         return new EntityPainting(var1, var2, var3);
      } else {
         return this.field_82811_a == EntityItemFrame.class ? new EntityItemFrame(var1, var2, var3) : null;
      }
   }
}
