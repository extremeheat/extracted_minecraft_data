package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityDamageSourceIndirect extends EntityDamageSource {
   private final Entity field_76387_p;

   public EntityDamageSourceIndirect(String var1, Entity var2, @Nullable Entity var3) {
      super(var1, var2);
      this.field_76387_p = var3;
   }

   @Nullable
   public Entity func_76364_f() {
      return this.field_76386_o;
   }

   @Nullable
   public Entity func_76346_g() {
      return this.field_76387_p;
   }

   public ITextComponent func_151519_b(EntityLivingBase var1) {
      ITextComponent var2 = this.field_76387_p == null ? this.field_76386_o.func_145748_c_() : this.field_76387_p.func_145748_c_();
      ItemStack var3 = this.field_76387_p instanceof EntityLivingBase ? ((EntityLivingBase)this.field_76387_p).func_184614_ca() : ItemStack.field_190927_a;
      String var4 = "death.attack." + this.field_76373_n;
      String var5 = var4 + ".item";
      return !var3.func_190926_b() && var3.func_82837_s() ? new TextComponentTranslation(var5, new Object[]{var1.func_145748_c_(), var2, var3.func_151000_E()}) : new TextComponentTranslation(var4, new Object[]{var1.func_145748_c_(), var2});
   }
}
