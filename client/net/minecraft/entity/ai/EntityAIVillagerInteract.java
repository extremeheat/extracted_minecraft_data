package net.minecraft.entity.ai;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class EntityAIVillagerInteract extends EntityAIWatchClosestWithoutMoving {
   private int field_179478_e;
   private final EntityVillager field_179477_f;

   public EntityAIVillagerInteract(EntityVillager var1) {
      super(var1, EntityVillager.class, 3.0F, 0.02F);
      this.field_179477_f = var1;
   }

   public void func_75249_e() {
      super.func_75249_e();
      if (this.field_179477_f.func_175555_cq() && this.field_75334_a instanceof EntityVillager && ((EntityVillager)this.field_75334_a).func_175557_cr()) {
         this.field_179478_e = 10;
      } else {
         this.field_179478_e = 0;
      }

   }

   public void func_75246_d() {
      super.func_75246_d();
      if (this.field_179478_e > 0) {
         --this.field_179478_e;
         if (this.field_179478_e == 0) {
            InventoryBasic var1 = this.field_179477_f.func_175551_co();

            for(int var2 = 0; var2 < var1.func_70302_i_(); ++var2) {
               ItemStack var3 = var1.func_70301_a(var2);
               ItemStack var4 = ItemStack.field_190927_a;
               if (!var3.func_190926_b()) {
                  Item var5 = var3.func_77973_b();
                  int var6;
                  if ((var5 == Items.field_151025_P || var5 == Items.field_151174_bG || var5 == Items.field_151172_bF || var5 == Items.field_185164_cV) && var3.func_190916_E() > 3) {
                     var6 = var3.func_190916_E() / 2;
                     var3.func_190918_g(var6);
                     var4 = new ItemStack(var5, var6);
                  } else if (var5 == Items.field_151015_O && var3.func_190916_E() > 5) {
                     var6 = var3.func_190916_E() / 2 / 3 * 3;
                     int var7 = var6 / 3;
                     var3.func_190918_g(var6);
                     var4 = new ItemStack(Items.field_151025_P, var7);
                  }

                  if (var3.func_190926_b()) {
                     var1.func_70299_a(var2, ItemStack.field_190927_a);
                  }
               }

               if (!var4.func_190926_b()) {
                  double var11 = this.field_179477_f.field_70163_u - 0.30000001192092896D + (double)this.field_179477_f.func_70047_e();
                  EntityItem var12 = new EntityItem(this.field_179477_f.field_70170_p, this.field_179477_f.field_70165_t, var11, this.field_179477_f.field_70161_v, var4);
                  float var8 = 0.3F;
                  float var9 = this.field_179477_f.field_70759_as;
                  float var10 = this.field_179477_f.field_70125_A;
                  var12.field_70159_w = (double)(-MathHelper.func_76126_a(var9 * 0.017453292F) * MathHelper.func_76134_b(var10 * 0.017453292F) * 0.3F);
                  var12.field_70179_y = (double)(MathHelper.func_76134_b(var9 * 0.017453292F) * MathHelper.func_76134_b(var10 * 0.017453292F) * 0.3F);
                  var12.field_70181_x = (double)(-MathHelper.func_76126_a(var10 * 0.017453292F) * 0.3F + 0.1F);
                  var12.func_174869_p();
                  this.field_179477_f.field_70170_p.func_72838_d(var12);
                  break;
               }
            }
         }
      }

   }
}
