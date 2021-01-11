package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.Vec3;

public class EntityAIAvoidEntity<T extends Entity> extends EntityAIBase {
   private final Predicate<Entity> field_179509_a;
   protected EntityCreature field_75380_a;
   private double field_75378_b;
   private double field_75379_c;
   protected T field_75376_d;
   private float field_179508_f;
   private PathEntity field_75374_f;
   private PathNavigate field_75375_g;
   private Class<T> field_181064_i;
   private Predicate<? super T> field_179510_i;

   public EntityAIAvoidEntity(EntityCreature var1, Class<T> var2, float var3, double var4, double var6) {
      this(var1, var2, Predicates.alwaysTrue(), var3, var4, var6);
   }

   public EntityAIAvoidEntity(EntityCreature var1, Class<T> var2, Predicate<? super T> var3, float var4, double var5, double var7) {
      super();
      this.field_179509_a = new Predicate<Entity>() {
         public boolean apply(Entity var1) {
            return var1.func_70089_S() && EntityAIAvoidEntity.this.field_75380_a.func_70635_at().func_75522_a(var1);
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((Entity)var1);
         }
      };
      this.field_75380_a = var1;
      this.field_181064_i = var2;
      this.field_179510_i = var3;
      this.field_179508_f = var4;
      this.field_75378_b = var5;
      this.field_75379_c = var7;
      this.field_75375_g = var1.func_70661_as();
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      List var1 = this.field_75380_a.field_70170_p.func_175647_a(this.field_181064_i, this.field_75380_a.func_174813_aQ().func_72314_b((double)this.field_179508_f, 3.0D, (double)this.field_179508_f), Predicates.and(new Predicate[]{EntitySelectors.field_180132_d, this.field_179509_a, this.field_179510_i}));
      if (var1.isEmpty()) {
         return false;
      } else {
         this.field_75376_d = (Entity)var1.get(0);
         Vec3 var2 = RandomPositionGenerator.func_75461_b(this.field_75380_a, 16, 7, new Vec3(this.field_75376_d.field_70165_t, this.field_75376_d.field_70163_u, this.field_75376_d.field_70161_v));
         if (var2 == null) {
            return false;
         } else if (this.field_75376_d.func_70092_e(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c) < this.field_75376_d.func_70068_e(this.field_75380_a)) {
            return false;
         } else {
            this.field_75374_f = this.field_75375_g.func_75488_a(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c);
            if (this.field_75374_f == null) {
               return false;
            } else {
               return this.field_75374_f.func_75880_b(var2);
            }
         }
      }
   }

   public boolean func_75253_b() {
      return !this.field_75375_g.func_75500_f();
   }

   public void func_75249_e() {
      this.field_75375_g.func_75484_a(this.field_75374_f, this.field_75378_b);
   }

   public void func_75251_c() {
      this.field_75376_d = null;
   }

   public void func_75246_d() {
      if (this.field_75380_a.func_70068_e(this.field_75376_d) < 49.0D) {
         this.field_75380_a.func_70661_as().func_75489_a(this.field_75379_c);
      } else {
         this.field_75380_a.func_70661_as().func_75489_a(this.field_75378_b);
      }

   }
}
