package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Particles;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class EntityAIMate extends EntityAIBase {
   protected final EntityAnimal field_75390_d;
   private final Class<? extends EntityAnimal> field_190857_e;
   protected World field_75394_a;
   protected EntityAnimal field_75391_e;
   private int field_75392_b;
   private final double field_75393_c;

   public EntityAIMate(EntityAnimal var1, double var2) {
      this(var1, var2, var1.getClass());
   }

   public EntityAIMate(EntityAnimal var1, double var2, Class<? extends EntityAnimal> var4) {
      super();
      this.field_75390_d = var1;
      this.field_75394_a = var1.field_70170_p;
      this.field_190857_e = var4;
      this.field_75393_c = var2;
      this.func_75248_a(3);
   }

   public boolean func_75250_a() {
      if (!this.field_75390_d.func_70880_s()) {
         return false;
      } else {
         this.field_75391_e = this.func_75389_f();
         return this.field_75391_e != null;
      }
   }

   public boolean func_75253_b() {
      return this.field_75391_e.func_70089_S() && this.field_75391_e.func_70880_s() && this.field_75392_b < 60;
   }

   public void func_75251_c() {
      this.field_75391_e = null;
      this.field_75392_b = 0;
   }

   public void func_75246_d() {
      this.field_75390_d.func_70671_ap().func_75651_a(this.field_75391_e, 10.0F, (float)this.field_75390_d.func_70646_bf());
      this.field_75390_d.func_70661_as().func_75497_a(this.field_75391_e, this.field_75393_c);
      ++this.field_75392_b;
      if (this.field_75392_b >= 60 && this.field_75390_d.func_70068_e(this.field_75391_e) < 9.0D) {
         this.func_75388_i();
      }

   }

   private EntityAnimal func_75389_f() {
      List var1 = this.field_75394_a.func_72872_a(this.field_190857_e, this.field_75390_d.func_174813_aQ().func_186662_g(8.0D));
      double var2 = 1.7976931348623157E308D;
      EntityAnimal var4 = null;
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         EntityAnimal var6 = (EntityAnimal)var5.next();
         if (this.field_75390_d.func_70878_b(var6) && this.field_75390_d.func_70068_e(var6) < var2) {
            var4 = var6;
            var2 = this.field_75390_d.func_70068_e(var6);
         }
      }

      return var4;
   }

   protected void func_75388_i() {
      EntityAgeable var1 = this.field_75390_d.func_90011_a(this.field_75391_e);
      if (var1 != null) {
         EntityPlayerMP var2 = this.field_75390_d.func_191993_do();
         if (var2 == null && this.field_75391_e.func_191993_do() != null) {
            var2 = this.field_75391_e.func_191993_do();
         }

         if (var2 != null) {
            var2.func_195066_a(StatList.field_151186_x);
            CriteriaTriggers.field_192134_n.func_192168_a(var2, this.field_75390_d, this.field_75391_e, var1);
         }

         this.field_75390_d.func_70873_a(6000);
         this.field_75391_e.func_70873_a(6000);
         this.field_75390_d.func_70875_t();
         this.field_75391_e.func_70875_t();
         var1.func_70873_a(-24000);
         var1.func_70012_b(this.field_75390_d.field_70165_t, this.field_75390_d.field_70163_u, this.field_75390_d.field_70161_v, 0.0F, 0.0F);
         this.field_75394_a.func_72838_d(var1);
         Random var3 = this.field_75390_d.func_70681_au();

         for(int var4 = 0; var4 < 7; ++var4) {
            double var5 = var3.nextGaussian() * 0.02D;
            double var7 = var3.nextGaussian() * 0.02D;
            double var9 = var3.nextGaussian() * 0.02D;
            double var11 = var3.nextDouble() * (double)this.field_75390_d.field_70130_N * 2.0D - (double)this.field_75390_d.field_70130_N;
            double var13 = 0.5D + var3.nextDouble() * (double)this.field_75390_d.field_70131_O;
            double var15 = var3.nextDouble() * (double)this.field_75390_d.field_70130_N * 2.0D - (double)this.field_75390_d.field_70130_N;
            this.field_75394_a.func_195594_a(Particles.field_197633_z, this.field_75390_d.field_70165_t + var11, this.field_75390_d.field_70163_u + var13, this.field_75390_d.field_70161_v + var15, var5, var7, var9);
         }

         if (this.field_75394_a.func_82736_K().func_82766_b("doMobLoot")) {
            this.field_75394_a.func_72838_d(new EntityXPOrb(this.field_75394_a, this.field_75390_d.field_70165_t, this.field_75390_d.field_70163_u, this.field_75390_d.field_70161_v, var3.nextInt(7) + 1));
         }

      }
   }
}
