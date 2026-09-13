package net.minecraft.util;

import net.minecraft.entity.EntityLivingBase;

public class CombatEntry {
   private final DamageSource field_94569_a;
   private final int field_94567_b;
   private final float field_94568_c;
   private final float field_94565_d;
   private final String field_94566_e;
   private final float field_94564_f;

   public CombatEntry(DamageSource var1, int var2, float var3, float var4, String var5, float var6) {
      super();
      this.field_94569_a = var1;
      this.field_94567_b = var2;
      this.field_94568_c = var4;
      this.field_94565_d = var3;
      this.field_94566_e = var5;
      this.field_94564_f = var6;
   }

   public DamageSource func_94560_a() {
      return this.field_94569_a;
   }

   public float func_94563_c() {
      return this.field_94568_c;
   }

   public boolean func_94559_f() {
      return this.field_94569_a.func_76346_g() instanceof EntityLivingBase;
   }

   public String func_94562_g() {
      return this.field_94566_e;
   }

   public IChatComponent func_151522_h() {
      return this.func_94560_a().func_76346_g() == null ? null : this.func_94560_a().func_76346_g().func_145748_c_();
   }

   public float func_94561_i() {
      return this.field_94569_a == DamageSource.field_76380_i ? 3.4028235E38F : this.field_94564_f;
   }
}
