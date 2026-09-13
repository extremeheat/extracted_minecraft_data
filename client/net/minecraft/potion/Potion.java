package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StringUtils;

public class Potion {
   public static final Potion[] field_76425_a = new Potion[32];
   public static final Potion field_76423_b = null;
   public static final Potion field_76424_c = new Potion(1, false, 8171462)
      .func_76390_b("potion.moveSpeed")
      .func_76399_b(0, 0)
      .func_111184_a(SharedMonsterAttributes.field_111263_d, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224, 2);
   public static final Potion field_76421_d = new Potion(2, true, 5926017)
      .func_76390_b("potion.moveSlowdown")
      .func_76399_b(1, 0)
      .func_111184_a(SharedMonsterAttributes.field_111263_d, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448, 2);
   public static final Potion field_76422_e = new Potion(3, false, 14270531).func_76390_b("potion.digSpeed").func_76399_b(2, 0).func_76404_a(1.5);
   public static final Potion field_76419_f = new Potion(4, true, 4866583).func_76390_b("potion.digSlowDown").func_76399_b(3, 0);
   public static final Potion field_76420_g = new PotionAttackDamage(5, false, 9643043)
      .func_76390_b("potion.damageBoost")
      .func_76399_b(4, 0)
      .func_111184_a(SharedMonsterAttributes.field_111264_e, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 3.0, 2);
   public static final Potion field_76432_h = new PotionHealth(6, false, 16262179).func_76390_b("potion.heal");
   public static final Potion field_76433_i = new PotionHealth(7, true, 4393481).func_76390_b("potion.harm");
   public static final Potion field_76430_j = new Potion(8, false, 7889559).func_76390_b("potion.jump").func_76399_b(2, 1);
   public static final Potion field_76431_k = new Potion(9, true, 5578058).func_76390_b("potion.confusion").func_76399_b(3, 1).func_76404_a(0.25);
   public static final Potion field_76428_l = new Potion(10, false, 13458603).func_76390_b("potion.regeneration").func_76399_b(7, 0).func_76404_a(0.25);
   public static final Potion field_76429_m = new Potion(11, false, 10044730).func_76390_b("potion.resistance").func_76399_b(6, 1);
   public static final Potion field_76426_n = new Potion(12, false, 14981690).func_76390_b("potion.fireResistance").func_76399_b(7, 1);
   public static final Potion field_76427_o = new Potion(13, false, 3035801).func_76390_b("potion.waterBreathing").func_76399_b(0, 2);
   public static final Potion field_76441_p = new Potion(14, false, 8356754).func_76390_b("potion.invisibility").func_76399_b(0, 1);
   public static final Potion field_76440_q = new Potion(15, true, 2039587).func_76390_b("potion.blindness").func_76399_b(5, 1).func_76404_a(0.25);
   public static final Potion field_76439_r = new Potion(16, false, 2039713).func_76390_b("potion.nightVision").func_76399_b(4, 1);
   public static final Potion field_76438_s = new Potion(17, true, 5797459).func_76390_b("potion.hunger").func_76399_b(1, 1);
   public static final Potion field_76437_t = new PotionAttackDamage(18, true, 4738376)
      .func_76390_b("potion.weakness")
      .func_76399_b(5, 0)
      .func_111184_a(SharedMonsterAttributes.field_111264_e, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0, 0);
   public static final Potion field_76436_u = new Potion(19, true, 5149489).func_76390_b("potion.poison").func_76399_b(6, 0).func_76404_a(0.25);
   public static final Potion field_82731_v = new Potion(20, true, 3484199).func_76390_b("potion.wither").func_76399_b(1, 2).func_76404_a(0.25);
   public static final Potion field_76434_w = new PotionHealthBoost(21, false, 16284963)
      .func_76390_b("potion.healthBoost")
      .func_76399_b(2, 2)
      .func_111184_a(SharedMonsterAttributes.field_111267_a, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0, 0);
   public static final Potion field_76444_x = new PotionAbsoption(22, false, 2445989).func_76390_b("potion.absorption").func_76399_b(2, 2);
   public static final Potion field_76443_y = new PotionHealth(23, false, 16262179).func_76390_b("potion.saturation");
   public static final Potion field_76442_z = null;
   public static final Potion field_76409_A = null;
   public static final Potion field_76410_B = null;
   public static final Potion field_76411_C = null;
   public static final Potion field_76405_D = null;
   public static final Potion field_76406_E = null;
   public static final Potion field_76407_F = null;
   public static final Potion field_76408_G = null;
   public final int field_76415_H;
   private final Map field_111188_I = Maps.newHashMap();
   private final boolean field_76418_K;
   private final int field_76414_N;
   private String field_76416_I = "";
   private int field_76417_J = -1;
   private double field_76412_L;
   private boolean field_76413_M;

   protected Potion(int var1, boolean var2, int var3) {
      super();
      this.field_76415_H = var1;
      field_76425_a[var1] = this;
      this.field_76418_K = var2;
      if (var2) {
         this.field_76412_L = 0.5;
      } else {
         this.field_76412_L = 1.0;
      }

      this.field_76414_N = var3;
   }

   protected Potion func_76399_b(int var1, int var2) {
      this.field_76417_J = var1 + var2 * 8;
      return this;
   }

   public int func_76396_c() {
      return this.field_76415_H;
   }

   public void func_76394_a(EntityLivingBase var1, int var2) {
      if (this.field_76415_H == field_76428_l.field_76415_H) {
         if (var1.func_110143_aJ() < var1.func_110138_aP()) {
            var1.func_70691_i(1.0F);
         }
      } else if (this.field_76415_H == field_76436_u.field_76415_H) {
         if (var1.func_110143_aJ() > 1.0F) {
            var1.func_70097_a(DamageSource.field_76376_m, 1.0F);
         }
      } else if (this.field_76415_H == field_82731_v.field_76415_H) {
         var1.func_70097_a(DamageSource.field_82727_n, 1.0F);
      } else if (this.field_76415_H == field_76438_s.field_76415_H && var1 instanceof EntityPlayer) {
         ((EntityPlayer)var1).func_71020_j(0.025F * (float)(var2 + 1));
      } else if (this.field_76415_H == field_76443_y.field_76415_H && var1 instanceof EntityPlayer) {
         if (!var1.field_70170_p.field_72995_K) {
            ((EntityPlayer)var1).func_71024_bL().func_75122_a(var2 + 1, 1.0F);
         }
      } else if ((this.field_76415_H != field_76432_h.field_76415_H || var1.func_70662_br())
         && (this.field_76415_H != field_76433_i.field_76415_H || !var1.func_70662_br())) {
         if (this.field_76415_H == field_76433_i.field_76415_H && !var1.func_70662_br()
            || this.field_76415_H == field_76432_h.field_76415_H && var1.func_70662_br()) {
            var1.func_70097_a(DamageSource.field_76376_m, (float)(6 << var2));
         }
      } else {
         var1.func_70691_i((float)Math.max(4 << var2, 0));
      }
   }

   public void func_76402_a(EntityLivingBase var1, EntityLivingBase var2, int var3, double var4) {
      if ((this.field_76415_H != field_76432_h.field_76415_H || var2.func_70662_br())
         && (this.field_76415_H != field_76433_i.field_76415_H || !var2.func_70662_br())) {
         if (this.field_76415_H == field_76433_i.field_76415_H && !var2.func_70662_br()
            || this.field_76415_H == field_76432_h.field_76415_H && var2.func_70662_br()) {
            int var7 = (int)(var4 * (double)(6 << var3) + 0.5);
            if (var1 == null) {
               var2.func_70097_a(DamageSource.field_76376_m, (float)var7);
            } else {
               var2.func_70097_a(DamageSource.func_76354_b(var2, var1), (float)var7);
            }
         }
      } else {
         int var6 = (int)(var4 * (double)(4 << var3) + 0.5);
         var2.func_70691_i((float)var6);
      }
   }

   public boolean func_76403_b() {
      return false;
   }

   public boolean func_76397_a(int var1, int var2) {
      if (this.field_76415_H == field_76428_l.field_76415_H) {
         int var5 = 50 >> var2;
         if (var5 > 0) {
            return var1 % var5 == 0;
         } else {
            return true;
         }
      } else if (this.field_76415_H == field_76436_u.field_76415_H) {
         int var4 = 25 >> var2;
         if (var4 > 0) {
            return var1 % var4 == 0;
         } else {
            return true;
         }
      } else if (this.field_76415_H == field_82731_v.field_76415_H) {
         int var3 = 40 >> var2;
         if (var3 > 0) {
            return var1 % var3 == 0;
         } else {
            return true;
         }
      } else {
         return this.field_76415_H == field_76438_s.field_76415_H;
      }
   }

   public Potion func_76390_b(String var1) {
      this.field_76416_I = var1;
      return this;
   }

   public String func_76393_a() {
      return this.field_76416_I;
   }

   public boolean func_76400_d() {
      return this.field_76417_J >= 0;
   }

   public int func_76392_e() {
      return this.field_76417_J;
   }

   public boolean func_76398_f() {
      return this.field_76418_K;
   }

   public static String func_76389_a(PotionEffect var0) {
      if (var0.func_100011_g()) {
         return "**:**";
      } else {
         int var1 = var0.func_76459_b();
         return StringUtils.func_76337_a(var1);
      }
   }

   protected Potion func_76404_a(double var1) {
      this.field_76412_L = var1;
      return this;
   }

   public double func_76388_g() {
      return this.field_76412_L;
   }

   public boolean func_76395_i() {
      return this.field_76413_M;
   }

   public int func_76401_j() {
      return this.field_76414_N;
   }

   public Potion func_111184_a(IAttribute var1, String var2, double var3, int var5) {
      AttributeModifier var6 = new AttributeModifier(UUID.fromString(var2), this.func_76393_a(), var3, var5);
      this.field_111188_I.put(var1, var6);
      return this;
   }

   public Map func_111186_k() {
      return this.field_111188_I;
   }

   public void func_111187_a(EntityLivingBase var1, BaseAttributeMap var2, int var3) {
      for(Entry var5 : this.field_111188_I.entrySet()) {
         IAttributeInstance var6 = var2.func_111151_a((IAttribute)var5.getKey());
         if (var6 != null) {
            var6.func_111124_b((AttributeModifier)var5.getValue());
         }
      }
   }

   public void func_111185_a(EntityLivingBase var1, BaseAttributeMap var2, int var3) {
      for(Entry var5 : this.field_111188_I.entrySet()) {
         IAttributeInstance var6 = var2.func_111151_a((IAttribute)var5.getKey());
         if (var6 != null) {
            AttributeModifier var7 = (AttributeModifier)var5.getValue();
            var6.func_111124_b(var7);
            var6.func_111121_a(
               new AttributeModifier(var7.func_111167_a(), this.func_76393_a() + " " + var3, this.func_111183_a(var3, var7), var7.func_111169_c())
            );
         }
      }
   }

   public double func_111183_a(int var1, AttributeModifier var2) {
      return var2.func_111164_d() * (double)(var1 + 1);
   }
}
