package net.minecraft.entity.ai;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.Vec3d;

public class EntityAIAvoidEntity<T extends Entity> extends EntityAIBase {
   private final Predicate<Entity> field_179509_a;
   protected EntityCreature field_75380_a;
   private final double field_75378_b;
   private final double field_75379_c;
   protected T field_75376_d;
   private final float field_179508_f;
   private Path field_75374_f;
   private final PathNavigate field_75375_g;
   private final Class<T> field_181064_i;
   private final Predicate<? super Entity> field_179510_i;
   private final Predicate<? super Entity> field_203784_k;

   public EntityAIAvoidEntity(EntityCreature var1, Class<T> var2, float var3, double var4, double var6) {
      this(var1, var2, (var0) -> {
         return true;
      }, var3, var4, var6, EntitySelectors.field_188444_d);
   }

   public EntityAIAvoidEntity(EntityCreature var1, Class<T> var2, Predicate<? super Entity> var3, float var4, double var5, double var7, Predicate<Entity> var9) {
      super();
      this.field_179509_a = new Predicate<Entity>() {
         public boolean test(@Nullable Entity var1) {
            return var1.func_70089_S() && EntityAIAvoidEntity.this.field_75380_a.func_70635_at().func_75522_a(var1) && !EntityAIAvoidEntity.this.field_75380_a.func_184191_r(var1);
         }

         // $FF: synthetic method
         public boolean test(@Nullable Object var1) {
            return this.test((Entity)var1);
         }
      };
      this.field_75380_a = var1;
      this.field_181064_i = var2;
      this.field_179510_i = var3;
      this.field_179508_f = var4;
      this.field_75378_b = var5;
      this.field_75379_c = var7;
      this.field_203784_k = var9;
      this.field_75375_g = var1.func_70661_as();
      this.func_75248_a(1);
   }

   public EntityAIAvoidEntity(EntityCreature var1, Class<T> var2, float var3, double var4, double var6, Predicate<Entity> var8) {
      this(var1, var2, (var0) -> {
         return true;
      }, var3, var4, var6, var8);
   }

   public boolean func_75250_a() {
      List var1 = this.field_75380_a.field_70170_p.func_175647_a(this.field_181064_i, this.field_75380_a.func_174813_aQ().func_72314_b((double)this.field_179508_f, 3.0D, (double)this.field_179508_f), (var1x) -> {
         return this.field_203784_k.test(var1x) && this.field_179509_a.test(var1x) && this.field_179510_i.test(var1x);
      });
      if (var1.isEmpty()) {
         return false;
      } else {
         this.field_75376_d = (Entity)var1.get(0);
         Vec3d var2 = RandomPositionGenerator.func_75461_b(this.field_75380_a, 16, 7, new Vec3d(this.field_75376_d.field_70165_t, this.field_75376_d.field_70163_u, this.field_75376_d.field_70161_v));
         if (var2 == null) {
            return false;
         } else if (this.field_75376_d.func_70092_e(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c) < this.field_75376_d.func_70068_e(this.field_75380_a)) {
            return false;
         } else {
            this.field_75374_f = this.field_75375_g.func_75488_a(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c);
            return this.field_75374_f != null;
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
