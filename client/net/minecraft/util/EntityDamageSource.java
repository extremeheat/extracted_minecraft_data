package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class EntityDamageSource extends DamageSource {
   protected Entity field_76386_o;
   private boolean field_180140_r = false;

   public EntityDamageSource(String var1, Entity var2) {
      super(var1);
      this.field_76386_o = var2;
   }

   public EntityDamageSource func_180138_v() {
      this.field_180140_r = true;
      return this;
   }

   public boolean func_180139_w() {
      return this.field_180140_r;
   }

   public Entity func_76346_g() {
      return this.field_76386_o;
   }

   public IChatComponent func_151519_b(EntityLivingBase var1) {
      ItemStack var2 = this.field_76386_o instanceof EntityLivingBase ? ((EntityLivingBase)this.field_76386_o).func_70694_bm() : null;
      String var3 = "death.attack." + this.field_76373_n;
      String var4 = var3 + ".item";
      return var2 != null && var2.func_82837_s() && StatCollector.func_94522_b(var4) ? new ChatComponentTranslation(var4, new Object[]{var1.func_145748_c_(), this.field_76386_o.func_145748_c_(), var2.func_151000_E()}) : new ChatComponentTranslation(var3, new Object[]{var1.func_145748_c_(), this.field_76386_o.func_145748_c_()});
   }

   public boolean func_76350_n() {
      return this.field_76386_o != null && this.field_76386_o instanceof EntityLivingBase && !(this.field_76386_o instanceof EntityPlayer);
   }
}
