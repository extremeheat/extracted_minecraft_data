package net.minecraft.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.world.Explosion;

public class DamageSource {
   public static DamageSource field_76372_a = (new DamageSource("inFire")).func_76361_j();
   public static DamageSource field_180137_b = new DamageSource("lightningBolt");
   public static DamageSource field_76370_b = (new DamageSource("onFire")).func_76348_h().func_76361_j();
   public static DamageSource field_76371_c = (new DamageSource("lava")).func_76361_j();
   public static DamageSource field_76368_d = (new DamageSource("inWall")).func_76348_h();
   public static DamageSource field_76369_e = (new DamageSource("drown")).func_76348_h();
   public static DamageSource field_76366_f = (new DamageSource("starve")).func_76348_h().func_151518_m();
   public static DamageSource field_76367_g = new DamageSource("cactus");
   public static DamageSource field_76379_h = (new DamageSource("fall")).func_76348_h();
   public static DamageSource field_76380_i = (new DamageSource("outOfWorld")).func_76348_h().func_76359_i();
   public static DamageSource field_76377_j = (new DamageSource("generic")).func_76348_h();
   public static DamageSource field_76376_m = (new DamageSource("magic")).func_76348_h().func_82726_p();
   public static DamageSource field_82727_n = (new DamageSource("wither")).func_76348_h();
   public static DamageSource field_82728_o = new DamageSource("anvil");
   public static DamageSource field_82729_p = new DamageSource("fallingBlock");
   private boolean field_76374_o;
   private boolean field_76385_p;
   private boolean field_151520_r;
   private float field_76384_q = 0.3F;
   private boolean field_76383_r;
   private boolean field_76382_s;
   private boolean field_76381_t;
   private boolean field_82730_x;
   private boolean field_76378_k;
   public String field_76373_n;

   public static DamageSource func_76358_a(EntityLivingBase var0) {
      return new EntityDamageSource("mob", var0);
   }

   public static DamageSource func_76365_a(EntityPlayer var0) {
      return new EntityDamageSource("player", var0);
   }

   public static DamageSource func_76353_a(EntityArrow var0, Entity var1) {
      return (new EntityDamageSourceIndirect("arrow", var0, var1)).func_76349_b();
   }

   public static DamageSource func_76362_a(EntityFireball var0, Entity var1) {
      return var1 == null ? (new EntityDamageSourceIndirect("onFire", var0, var0)).func_76361_j().func_76349_b() : (new EntityDamageSourceIndirect("fireball", var0, var1)).func_76361_j().func_76349_b();
   }

   public static DamageSource func_76356_a(Entity var0, Entity var1) {
      return (new EntityDamageSourceIndirect("thrown", var0, var1)).func_76349_b();
   }

   public static DamageSource func_76354_b(Entity var0, Entity var1) {
      return (new EntityDamageSourceIndirect("indirectMagic", var0, var1)).func_76348_h().func_82726_p();
   }

   public static DamageSource func_92087_a(Entity var0) {
      return (new EntityDamageSource("thorns", var0)).func_180138_v().func_82726_p();
   }

   public static DamageSource func_94539_a(Explosion var0) {
      return var0 != null && var0.func_94613_c() != null ? (new EntityDamageSource("explosion.player", var0.func_94613_c())).func_76351_m().func_94540_d() : (new DamageSource("explosion")).func_76351_m().func_94540_d();
   }

   public boolean func_76352_a() {
      return this.field_76382_s;
   }

   public DamageSource func_76349_b() {
      this.field_76382_s = true;
      return this;
   }

   public boolean func_94541_c() {
      return this.field_76378_k;
   }

   public DamageSource func_94540_d() {
      this.field_76378_k = true;
      return this;
   }

   public boolean func_76363_c() {
      return this.field_76374_o;
   }

   public float func_76345_d() {
      return this.field_76384_q;
   }

   public boolean func_76357_e() {
      return this.field_76385_p;
   }

   public boolean func_151517_h() {
      return this.field_151520_r;
   }

   protected DamageSource(String var1) {
      super();
      this.field_76373_n = var1;
   }

   public Entity func_76364_f() {
      return this.func_76346_g();
   }

   public Entity func_76346_g() {
      return null;
   }

   protected DamageSource func_76348_h() {
      this.field_76374_o = true;
      this.field_76384_q = 0.0F;
      return this;
   }

   protected DamageSource func_76359_i() {
      this.field_76385_p = true;
      return this;
   }

   protected DamageSource func_151518_m() {
      this.field_151520_r = true;
      this.field_76384_q = 0.0F;
      return this;
   }

   protected DamageSource func_76361_j() {
      this.field_76383_r = true;
      return this;
   }

   public IChatComponent func_151519_b(EntityLivingBase var1) {
      EntityLivingBase var2 = var1.func_94060_bK();
      String var3 = "death.attack." + this.field_76373_n;
      String var4 = var3 + ".player";
      return var2 != null && StatCollector.func_94522_b(var4) ? new ChatComponentTranslation(var4, new Object[]{var1.func_145748_c_(), var2.func_145748_c_()}) : new ChatComponentTranslation(var3, new Object[]{var1.func_145748_c_()});
   }

   public boolean func_76347_k() {
      return this.field_76383_r;
   }

   public String func_76355_l() {
      return this.field_76373_n;
   }

   public DamageSource func_76351_m() {
      this.field_76381_t = true;
      return this;
   }

   public boolean func_76350_n() {
      return this.field_76381_t;
   }

   public boolean func_82725_o() {
      return this.field_82730_x;
   }

   public DamageSource func_82726_p() {
      this.field_82730_x = true;
      return this;
   }

   public boolean func_180136_u() {
      Entity var1 = this.func_76346_g();
      return var1 instanceof EntityPlayer && ((EntityPlayer)var1).field_71075_bZ.field_75098_d;
   }
}
