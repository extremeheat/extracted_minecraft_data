package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class EntityDamageSourceIndirect extends EntityDamageSource {
   private Entity field_76387_p;

   public EntityDamageSourceIndirect(String var1, Entity var2, Entity var3) {
      super(var1, var2);
      this.field_76387_p = var3;
   }

   public Entity func_76364_f() {
      return this.field_76386_o;
   }

   public Entity func_76346_g() {
      return this.field_76387_p;
   }

   public IChatComponent func_151519_b(EntityLivingBase var1) {
      IChatComponent var2 = this.field_76387_p == null ? this.field_76386_o.func_145748_c_() : this.field_76387_p.func_145748_c_();
      ItemStack var3 = this.field_76387_p instanceof EntityLivingBase ? ((EntityLivingBase)this.field_76387_p).func_70694_bm() : null;
      String var4 = "death.attack." + this.field_76373_n;
      String var5 = var4 + ".item";
      return var3 != null && var3.func_82837_s() && StatCollector.func_94522_b(var5) ? new ChatComponentTranslation(var5, new Object[]{var1.func_145748_c_(), var2, var3.func_151000_E()}) : new ChatComponentTranslation(var4, new Object[]{var1.func_145748_c_(), var2});
   }
}
