package net.minecraft.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget {
   protected final Class<T> field_75307_b;
   private final int field_75308_c;
   protected final EntityAINearestAttackableTarget.Sorter field_75306_g;
   protected Predicate<? super T> field_82643_g;
   protected EntityLivingBase field_75309_a;

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, boolean var3) {
      this(var1, var2, var3, false);
   }

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, boolean var3, boolean var4) {
      this(var1, var2, 10, var3, var4, (Predicate)null);
   }

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, int var3, boolean var4, boolean var5, final Predicate<? super T> var6) {
      super(var1, var4, var5);
      this.field_75307_b = var2;
      this.field_75308_c = var3;
      this.field_75306_g = new EntityAINearestAttackableTarget.Sorter(var1);
      this.func_75248_a(1);
      this.field_82643_g = new Predicate<T>() {
         public boolean apply(T var1) {
            if (var6 != null && !var6.apply(var1)) {
               return false;
            } else {
               if (var1 instanceof EntityPlayer) {
                  double var2 = EntityAINearestAttackableTarget.this.func_111175_f();
                  if (var1.func_70093_af()) {
                     var2 *= 0.800000011920929D;
                  }

                  if (var1.func_82150_aj()) {
                     float var4 = ((EntityPlayer)var1).func_82243_bO();
                     if (var4 < 0.1F) {
                        var4 = 0.1F;
                     }

                     var2 *= (double)(0.7F * var4);
                  }

                  if ((double)var1.func_70032_d(EntityAINearestAttackableTarget.this.field_75299_d) > var2) {
                     return false;
                  }
               }

               return EntityAINearestAttackableTarget.this.func_75296_a(var1, false);
            }
         }

         // $FF: synthetic method
         public boolean apply(Object var1) {
            return this.apply((EntityLivingBase)var1);
         }
      };
   }

   public boolean func_75250_a() {
      if (this.field_75308_c > 0 && this.field_75299_d.func_70681_au().nextInt(this.field_75308_c) != 0) {
         return false;
      } else {
         double var1 = this.func_111175_f();
         List var3 = this.field_75299_d.field_70170_p.func_175647_a(this.field_75307_b, this.field_75299_d.func_174813_aQ().func_72314_b(var1, 4.0D, var1), Predicates.and(this.field_82643_g, EntitySelectors.field_180132_d));
         Collections.sort(var3, this.field_75306_g);
         if (var3.isEmpty()) {
            return false;
         } else {
            this.field_75309_a = (EntityLivingBase)var3.get(0);
            return true;
         }
      }
   }

   public void func_75249_e() {
      this.field_75299_d.func_70624_b(this.field_75309_a);
      super.func_75249_e();
   }

   public static class Sorter implements Comparator<Entity> {
      private final Entity field_75459_b;

      public Sorter(Entity var1) {
         super();
         this.field_75459_b = var1;
      }

      public int compare(Entity var1, Entity var2) {
         double var3 = this.field_75459_b.func_70068_e(var1);
         double var5 = this.field_75459_b.func_70068_e(var2);
         if (var3 < var5) {
            return -1;
         } else {
            return var3 > var5 ? 1 : 0;
         }
      }

      // $FF: synthetic method
      public int compare(Object var1, Object var2) {
         return this.compare((Entity)var1, (Entity)var2);
      }
   }
}
