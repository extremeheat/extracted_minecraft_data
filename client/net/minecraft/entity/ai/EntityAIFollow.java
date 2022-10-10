package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;

public class EntityAIFollow extends EntityAIBase {
   private final EntityLiving field_192372_a;
   private final Predicate<EntityLiving> field_192373_b;
   private EntityLiving field_192374_c;
   private final double field_192375_d;
   private final PathNavigate field_192376_e;
   private int field_192377_f;
   private final float field_192378_g;
   private float field_192379_h;
   private final float field_192380_i;

   public EntityAIFollow(EntityLiving var1, double var2, float var4, float var5) {
      super();
      this.field_192372_a = var1;
      this.field_192373_b = (var1x) -> {
         return var1x != null && var1.getClass() != var1x.getClass();
      };
      this.field_192375_d = var2;
      this.field_192376_e = var1.func_70661_as();
      this.field_192378_g = var4;
      this.field_192380_i = var5;
      this.func_75248_a(3);
      if (!(var1.func_70661_as() instanceof PathNavigateGround) && !(var1.func_70661_as() instanceof PathNavigateFlying)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowMobGoal");
      }
   }

   public boolean func_75250_a() {
      List var1 = this.field_192372_a.field_70170_p.func_175647_a(EntityLiving.class, this.field_192372_a.func_174813_aQ().func_186662_g((double)this.field_192380_i), this.field_192373_b);
      if (!var1.isEmpty()) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            EntityLiving var3 = (EntityLiving)var2.next();
            if (!var3.func_82150_aj()) {
               this.field_192374_c = var3;
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_75253_b() {
      return this.field_192374_c != null && !this.field_192376_e.func_75500_f() && this.field_192372_a.func_70068_e(this.field_192374_c) > (double)(this.field_192378_g * this.field_192378_g);
   }

   public void func_75249_e() {
      this.field_192377_f = 0;
      this.field_192379_h = this.field_192372_a.func_184643_a(PathNodeType.WATER);
      this.field_192372_a.func_184644_a(PathNodeType.WATER, 0.0F);
   }

   public void func_75251_c() {
      this.field_192374_c = null;
      this.field_192376_e.func_75499_g();
      this.field_192372_a.func_184644_a(PathNodeType.WATER, this.field_192379_h);
   }

   public void func_75246_d() {
      if (this.field_192374_c != null && !this.field_192372_a.func_110167_bD()) {
         this.field_192372_a.func_70671_ap().func_75651_a(this.field_192374_c, 10.0F, (float)this.field_192372_a.func_70646_bf());
         if (--this.field_192377_f <= 0) {
            this.field_192377_f = 10;
            double var1 = this.field_192372_a.field_70165_t - this.field_192374_c.field_70165_t;
            double var3 = this.field_192372_a.field_70163_u - this.field_192374_c.field_70163_u;
            double var5 = this.field_192372_a.field_70161_v - this.field_192374_c.field_70161_v;
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            if (var7 > (double)(this.field_192378_g * this.field_192378_g)) {
               this.field_192376_e.func_75497_a(this.field_192374_c, this.field_192375_d);
            } else {
               this.field_192376_e.func_75499_g();
               EntityLookHelper var9 = this.field_192374_c.func_70671_ap();
               if (var7 <= (double)this.field_192378_g || var9.func_180423_e() == this.field_192372_a.field_70165_t && var9.func_180422_f() == this.field_192372_a.field_70163_u && var9.func_180421_g() == this.field_192372_a.field_70161_v) {
                  double var10 = this.field_192374_c.field_70165_t - this.field_192372_a.field_70165_t;
                  double var12 = this.field_192374_c.field_70161_v - this.field_192372_a.field_70161_v;
                  this.field_192376_e.func_75492_a(this.field_192372_a.field_70165_t - var10, this.field_192372_a.field_70163_u, this.field_192372_a.field_70161_v - var12, this.field_192375_d);
               }

            }
         }
      }
   }
}
