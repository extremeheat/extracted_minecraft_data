package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIBeg extends EntityAIBase {
   private final EntityWolf field_75387_a;
   private EntityPlayer field_75385_b;
   private final IWorldReaderBase field_75386_c;
   private final float field_75383_d;
   private int field_75384_e;

   public EntityAIBeg(EntityWolf var1, float var2) {
      super();
      this.field_75387_a = var1;
      this.field_75386_c = var1.field_70170_p;
      this.field_75383_d = var2;
      this.func_75248_a(2);
   }

   public boolean func_75250_a() {
      this.field_75385_b = this.field_75386_c.func_72890_a(this.field_75387_a, (double)this.field_75383_d);
      return this.field_75385_b == null ? false : this.func_75382_a(this.field_75385_b);
   }

   public boolean func_75253_b() {
      if (!this.field_75385_b.func_70089_S()) {
         return false;
      } else if (this.field_75387_a.func_70068_e(this.field_75385_b) > (double)(this.field_75383_d * this.field_75383_d)) {
         return false;
      } else {
         return this.field_75384_e > 0 && this.func_75382_a(this.field_75385_b);
      }
   }

   public void func_75249_e() {
      this.field_75387_a.func_70918_i(true);
      this.field_75384_e = 40 + this.field_75387_a.func_70681_au().nextInt(40);
   }

   public void func_75251_c() {
      this.field_75387_a.func_70918_i(false);
      this.field_75385_b = null;
   }

   public void func_75246_d() {
      this.field_75387_a.func_70671_ap().func_75650_a(this.field_75385_b.field_70165_t, this.field_75385_b.field_70163_u + (double)this.field_75385_b.func_70047_e(), this.field_75385_b.field_70161_v, 10.0F, (float)this.field_75387_a.func_70646_bf());
      --this.field_75384_e;
   }

   private boolean func_75382_a(EntityPlayer var1) {
      EnumHand[] var2 = EnumHand.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         EnumHand var5 = var2[var4];
         ItemStack var6 = var1.func_184586_b(var5);
         if (this.field_75387_a.func_70909_n() && var6.func_77973_b() == Items.field_151103_aS) {
            return true;
         }

         if (this.field_75387_a.func_70877_b(var6)) {
            return true;
         }
      }

      return false;
   }
}
