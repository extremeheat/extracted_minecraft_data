package net.minecraft.potion;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

public class Potion {
   public static final Potion[] field_76425_a = new Potion[32];
   private static final Map<ResourceLocation, Potion> field_180150_I = Maps.newHashMap();
   public static final Potion field_180151_b = null;
   public static final Potion field_76424_c;
   public static final Potion field_76421_d;
   public static final Potion field_76422_e;
   public static final Potion field_76419_f;
   public static final Potion field_76420_g;
   public static final Potion field_76432_h;
   public static final Potion field_76433_i;
   public static final Potion field_76430_j;
   public static final Potion field_76431_k;
   public static final Potion field_76428_l;
   public static final Potion field_76429_m;
   public static final Potion field_76426_n;
   public static final Potion field_76427_o;
   public static final Potion field_76441_p;
   public static final Potion field_76440_q;
   public static final Potion field_76439_r;
   public static final Potion field_76438_s;
   public static final Potion field_76437_t;
   public static final Potion field_76436_u;
   public static final Potion field_82731_v;
   public static final Potion field_180152_w;
   public static final Potion field_76444_x;
   public static final Potion field_76443_y;
   public static final Potion field_180153_z;
   public static final Potion field_180147_A;
   public static final Potion field_180148_B;
   public static final Potion field_180149_C;
   public static final Potion field_180143_D;
   public static final Potion field_180144_E;
   public static final Potion field_180145_F;
   public static final Potion field_180146_G;
   public final int field_76415_H;
   private final Map<IAttribute, AttributeModifier> field_111188_I = Maps.newHashMap();
   private final boolean field_76418_K;
   private final int field_76414_N;
   private String field_76416_I = "";
   private int field_76417_J = -1;
   private double field_76412_L;
   private boolean field_76413_M;

   protected Potion(int var1, ResourceLocation var2, boolean var3, int var4) {
      super();
      this.field_76415_H = var1;
      field_76425_a[var1] = this;
      field_180150_I.put(var2, this);
      this.field_76418_K = var3;
      if (var3) {
         this.field_76412_L = 0.5D;
      } else {
         this.field_76412_L = 1.0D;
      }

      this.field_76414_N = var4;
   }

   public static Potion func_180142_b(String var0) {
      return (Potion)field_180150_I.get(new ResourceLocation(var0));
   }

   public static Set<ResourceLocation> func_181168_c() {
      return field_180150_I.keySet();
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
      } else if ((this.field_76415_H != field_76432_h.field_76415_H || var1.func_70662_br()) && (this.field_76415_H != field_76433_i.field_76415_H || !var1.func_70662_br())) {
         if (this.field_76415_H == field_76433_i.field_76415_H && !var1.func_70662_br() || this.field_76415_H == field_76432_h.field_76415_H && var1.func_70662_br()) {
            var1.func_70097_a(DamageSource.field_76376_m, (float)(6 << var2));
         }
      } else {
         var1.func_70691_i((float)Math.max(4 << var2, 0));
      }

   }

   public void func_180793_a(Entity var1, Entity var2, EntityLivingBase var3, int var4, double var5) {
      int var7;
      if ((this.field_76415_H != field_76432_h.field_76415_H || var3.func_70662_br()) && (this.field_76415_H != field_76433_i.field_76415_H || !var3.func_70662_br())) {
         if (this.field_76415_H == field_76433_i.field_76415_H && !var3.func_70662_br() || this.field_76415_H == field_76432_h.field_76415_H && var3.func_70662_br()) {
            var7 = (int)(var5 * (double)(6 << var4) + 0.5D);
            if (var1 == null) {
               var3.func_70097_a(DamageSource.field_76376_m, (float)var7);
            } else {
               var3.func_70097_a(DamageSource.func_76354_b(var1, var2), (float)var7);
            }
         }
      } else {
         var7 = (int)(var5 * (double)(4 << var4) + 0.5D);
         var3.func_70691_i((float)var7);
      }

   }

   public boolean func_76403_b() {
      return false;
   }

   public boolean func_76397_a(int var1, int var2) {
      int var3;
      if (this.field_76415_H == field_76428_l.field_76415_H) {
         var3 = 50 >> var2;
         if (var3 > 0) {
            return var1 % var3 == 0;
         } else {
            return true;
         }
      } else if (this.field_76415_H == field_76436_u.field_76415_H) {
         var3 = 25 >> var2;
         if (var3 > 0) {
            return var1 % var3 == 0;
         } else {
            return true;
         }
      } else if (this.field_76415_H == field_82731_v.field_76415_H) {
         var3 = 40 >> var2;
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

   public Map<IAttribute, AttributeModifier> func_111186_k() {
      return this.field_111188_I;
   }

   public void func_111187_a(EntityLivingBase var1, BaseAttributeMap var2, int var3) {
      Iterator var4 = this.field_111188_I.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         IAttributeInstance var6 = var2.func_111151_a((IAttribute)var5.getKey());
         if (var6 != null) {
            var6.func_111124_b((AttributeModifier)var5.getValue());
         }
      }

   }

   public void func_111185_a(EntityLivingBase var1, BaseAttributeMap var2, int var3) {
      Iterator var4 = this.field_111188_I.entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         IAttributeInstance var6 = var2.func_111151_a((IAttribute)var5.getKey());
         if (var6 != null) {
            AttributeModifier var7 = (AttributeModifier)var5.getValue();
            var6.func_111124_b(var7);
            var6.func_111121_a(new AttributeModifier(var7.func_111167_a(), this.func_76393_a() + " " + var3, this.func_111183_a(var3, var7), var7.func_111169_c()));
         }
      }

   }

   public double func_111183_a(int var1, AttributeModifier var2) {
      return var2.func_111164_d() * (double)(var1 + 1);
   }

   static {
      field_76424_c = (new Potion(1, new ResourceLocation("speed"), false, 8171462)).func_76390_b("potion.moveSpeed").func_76399_b(0, 0).func_111184_a(SharedMonsterAttributes.field_111263_d, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2);
      field_76421_d = (new Potion(2, new ResourceLocation("slowness"), true, 5926017)).func_76390_b("potion.moveSlowdown").func_76399_b(1, 0).func_111184_a(SharedMonsterAttributes.field_111263_d, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, 2);
      field_76422_e = (new Potion(3, new ResourceLocation("haste"), false, 14270531)).func_76390_b("potion.digSpeed").func_76399_b(2, 0).func_76404_a(1.5D);
      field_76419_f = (new Potion(4, new ResourceLocation("mining_fatigue"), true, 4866583)).func_76390_b("potion.digSlowDown").func_76399_b(3, 0);
      field_76420_g = (new PotionAttackDamage(5, new ResourceLocation("strength"), false, 9643043)).func_76390_b("potion.damageBoost").func_76399_b(4, 0).func_111184_a(SharedMonsterAttributes.field_111264_e, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 2.5D, 2);
      field_76432_h = (new PotionHealth(6, new ResourceLocation("instant_health"), false, 16262179)).func_76390_b("potion.heal");
      field_76433_i = (new PotionHealth(7, new ResourceLocation("instant_damage"), true, 4393481)).func_76390_b("potion.harm");
      field_76430_j = (new Potion(8, new ResourceLocation("jump_boost"), false, 2293580)).func_76390_b("potion.jump").func_76399_b(2, 1);
      field_76431_k = (new Potion(9, new ResourceLocation("nausea"), true, 5578058)).func_76390_b("potion.confusion").func_76399_b(3, 1).func_76404_a(0.25D);
      field_76428_l = (new Potion(10, new ResourceLocation("regeneration"), false, 13458603)).func_76390_b("potion.regeneration").func_76399_b(7, 0).func_76404_a(0.25D);
      field_76429_m = (new Potion(11, new ResourceLocation("resistance"), false, 10044730)).func_76390_b("potion.resistance").func_76399_b(6, 1);
      field_76426_n = (new Potion(12, new ResourceLocation("fire_resistance"), false, 14981690)).func_76390_b("potion.fireResistance").func_76399_b(7, 1);
      field_76427_o = (new Potion(13, new ResourceLocation("water_breathing"), false, 3035801)).func_76390_b("potion.waterBreathing").func_76399_b(0, 2);
      field_76441_p = (new Potion(14, new ResourceLocation("invisibility"), false, 8356754)).func_76390_b("potion.invisibility").func_76399_b(0, 1);
      field_76440_q = (new Potion(15, new ResourceLocation("blindness"), true, 2039587)).func_76390_b("potion.blindness").func_76399_b(5, 1).func_76404_a(0.25D);
      field_76439_r = (new Potion(16, new ResourceLocation("night_vision"), false, 2039713)).func_76390_b("potion.nightVision").func_76399_b(4, 1);
      field_76438_s = (new Potion(17, new ResourceLocation("hunger"), true, 5797459)).func_76390_b("potion.hunger").func_76399_b(1, 1);
      field_76437_t = (new PotionAttackDamage(18, new ResourceLocation("weakness"), true, 4738376)).func_76390_b("potion.weakness").func_76399_b(5, 0).func_111184_a(SharedMonsterAttributes.field_111264_e, "22653B89-116E-49DC-9B6B-9971489B5BE5", 2.0D, 0);
      field_76436_u = (new Potion(19, new ResourceLocation("poison"), true, 5149489)).func_76390_b("potion.poison").func_76399_b(6, 0).func_76404_a(0.25D);
      field_82731_v = (new Potion(20, new ResourceLocation("wither"), true, 3484199)).func_76390_b("potion.wither").func_76399_b(1, 2).func_76404_a(0.25D);
      field_180152_w = (new PotionHealthBoost(21, new ResourceLocation("health_boost"), false, 16284963)).func_76390_b("potion.healthBoost").func_76399_b(2, 2).func_111184_a(SharedMonsterAttributes.field_111267_a, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0);
      field_76444_x = (new PotionAbsorption(22, new ResourceLocation("absorption"), false, 2445989)).func_76390_b("potion.absorption").func_76399_b(2, 2);
      field_76443_y = (new PotionHealth(23, new ResourceLocation("saturation"), false, 16262179)).func_76390_b("potion.saturation");
      field_180153_z = null;
      field_180147_A = null;
      field_180148_B = null;
      field_180149_C = null;
      field_180143_D = null;
      field_180144_E = null;
      field_180145_F = null;
      field_180146_G = null;
   }
}
