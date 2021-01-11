package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class CombatTracker {
   private final List<CombatEntry> field_94556_a = Lists.newArrayList();
   private final EntityLivingBase field_94554_b;
   private int field_94555_c;
   private int field_152775_d;
   private int field_152776_e;
   private boolean field_94552_d;
   private boolean field_94553_e;
   private String field_94551_f;

   public CombatTracker(EntityLivingBase var1) {
      super();
      this.field_94554_b = var1;
   }

   public void func_94545_a() {
      this.func_94542_g();
      if (this.field_94554_b.func_70617_f_()) {
         Block var1 = this.field_94554_b.field_70170_p.func_180495_p(new BlockPos(this.field_94554_b.field_70165_t, this.field_94554_b.func_174813_aQ().field_72338_b, this.field_94554_b.field_70161_v)).func_177230_c();
         if (var1 == Blocks.field_150468_ap) {
            this.field_94551_f = "ladder";
         } else if (var1 == Blocks.field_150395_bd) {
            this.field_94551_f = "vines";
         }
      } else if (this.field_94554_b.func_70090_H()) {
         this.field_94551_f = "water";
      }

   }

   public void func_94547_a(DamageSource var1, float var2, float var3) {
      this.func_94549_h();
      this.func_94545_a();
      CombatEntry var4 = new CombatEntry(var1, this.field_94554_b.field_70173_aa, var2, var3, this.field_94551_f, this.field_94554_b.field_70143_R);
      this.field_94556_a.add(var4);
      this.field_94555_c = this.field_94554_b.field_70173_aa;
      this.field_94553_e = true;
      if (var4.func_94559_f() && !this.field_94552_d && this.field_94554_b.func_70089_S()) {
         this.field_94552_d = true;
         this.field_152775_d = this.field_94554_b.field_70173_aa;
         this.field_152776_e = this.field_152775_d;
         this.field_94554_b.func_152111_bt();
      }

   }

   public IChatComponent func_151521_b() {
      if (this.field_94556_a.size() == 0) {
         return new ChatComponentTranslation("death.attack.generic", new Object[]{this.field_94554_b.func_145748_c_()});
      } else {
         CombatEntry var1 = this.func_94544_f();
         CombatEntry var2 = (CombatEntry)this.field_94556_a.get(this.field_94556_a.size() - 1);
         IChatComponent var4 = var2.func_151522_h();
         Entity var5 = var2.func_94560_a().func_76346_g();
         Object var3;
         if (var1 != null && var2.func_94560_a() == DamageSource.field_76379_h) {
            IChatComponent var6 = var1.func_151522_h();
            if (var1.func_94560_a() != DamageSource.field_76379_h && var1.func_94560_a() != DamageSource.field_76380_i) {
               if (var6 == null || var4 != null && var6.equals(var4)) {
                  if (var4 != null) {
                     ItemStack var9 = var5 instanceof EntityLivingBase ? ((EntityLivingBase)var5).func_70694_bm() : null;
                     if (var9 != null && var9.func_82837_s()) {
                        var3 = new ChatComponentTranslation("death.fell.finish.item", new Object[]{this.field_94554_b.func_145748_c_(), var4, var9.func_151000_E()});
                     } else {
                        var3 = new ChatComponentTranslation("death.fell.finish", new Object[]{this.field_94554_b.func_145748_c_(), var4});
                     }
                  } else {
                     var3 = new ChatComponentTranslation("death.fell.killer", new Object[]{this.field_94554_b.func_145748_c_()});
                  }
               } else {
                  Entity var7 = var1.func_94560_a().func_76346_g();
                  ItemStack var8 = var7 instanceof EntityLivingBase ? ((EntityLivingBase)var7).func_70694_bm() : null;
                  if (var8 != null && var8.func_82837_s()) {
                     var3 = new ChatComponentTranslation("death.fell.assist.item", new Object[]{this.field_94554_b.func_145748_c_(), var6, var8.func_151000_E()});
                  } else {
                     var3 = new ChatComponentTranslation("death.fell.assist", new Object[]{this.field_94554_b.func_145748_c_(), var6});
                  }
               }
            } else {
               var3 = new ChatComponentTranslation("death.fell.accident." + this.func_94548_b(var1), new Object[]{this.field_94554_b.func_145748_c_()});
            }
         } else {
            var3 = var2.func_94560_a().func_151519_b(this.field_94554_b);
         }

         return (IChatComponent)var3;
      }
   }

   public EntityLivingBase func_94550_c() {
      EntityLivingBase var1 = null;
      EntityPlayer var2 = null;
      float var3 = 0.0F;
      float var4 = 0.0F;
      Iterator var5 = this.field_94556_a.iterator();

      while(true) {
         CombatEntry var6;
         do {
            do {
               if (!var5.hasNext()) {
                  if (var2 != null && var4 >= var3 / 3.0F) {
                     return var2;
                  }

                  return var1;
               }

               var6 = (CombatEntry)var5.next();
               if (var6.func_94560_a().func_76346_g() instanceof EntityPlayer && (var2 == null || var6.func_94563_c() > var4)) {
                  var4 = var6.func_94563_c();
                  var2 = (EntityPlayer)var6.func_94560_a().func_76346_g();
               }
            } while(!(var6.func_94560_a().func_76346_g() instanceof EntityLivingBase));
         } while(var1 != null && var6.func_94563_c() <= var3);

         var3 = var6.func_94563_c();
         var1 = (EntityLivingBase)var6.func_94560_a().func_76346_g();
      }
   }

   private CombatEntry func_94544_f() {
      CombatEntry var1 = null;
      CombatEntry var2 = null;
      byte var3 = 0;
      float var4 = 0.0F;

      for(int var5 = 0; var5 < this.field_94556_a.size(); ++var5) {
         CombatEntry var6 = (CombatEntry)this.field_94556_a.get(var5);
         CombatEntry var7 = var5 > 0 ? (CombatEntry)this.field_94556_a.get(var5 - 1) : null;
         if ((var6.func_94560_a() == DamageSource.field_76379_h || var6.func_94560_a() == DamageSource.field_76380_i) && var6.func_94561_i() > 0.0F && (var1 == null || var6.func_94561_i() > var4)) {
            if (var5 > 0) {
               var1 = var7;
            } else {
               var1 = var6;
            }

            var4 = var6.func_94561_i();
         }

         if (var6.func_94562_g() != null && (var2 == null || var6.func_94563_c() > (float)var3)) {
            var2 = var6;
         }
      }

      if (var4 > 5.0F && var1 != null) {
         return var1;
      } else if (var3 > 5 && var2 != null) {
         return var2;
      } else {
         return null;
      }
   }

   private String func_94548_b(CombatEntry var1) {
      return var1.func_94562_g() == null ? "generic" : var1.func_94562_g();
   }

   public int func_180134_f() {
      return this.field_94552_d ? this.field_94554_b.field_70173_aa - this.field_152775_d : this.field_152776_e - this.field_152775_d;
   }

   private void func_94542_g() {
      this.field_94551_f = null;
   }

   public void func_94549_h() {
      int var1 = this.field_94552_d ? 300 : 100;
      if (this.field_94553_e && (!this.field_94554_b.func_70089_S() || this.field_94554_b.field_70173_aa - this.field_94555_c > var1)) {
         boolean var2 = this.field_94552_d;
         this.field_94553_e = false;
         this.field_94552_d = false;
         this.field_152776_e = this.field_94554_b.field_70173_aa;
         if (var2) {
            this.field_94554_b.func_152112_bu();
         }

         this.field_94556_a.clear();
      }

   }

   public EntityLivingBase func_180135_h() {
      return this.field_94554_b;
   }
}
