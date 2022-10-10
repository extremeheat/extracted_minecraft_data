package net.minecraft.entity.ai;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAINearestAttackableTarget<T extends EntityLivingBase> extends EntityAITarget {
   protected final Class<T> field_75307_b;
   private final int field_75308_c;
   protected final EntityAINearestAttackableTarget.Sorter field_75306_g;
   protected final Predicate<? super T> field_82643_g;
   protected T field_75309_a;

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, boolean var3) {
      this(var1, var2, var3, false);
   }

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, boolean var3, boolean var4) {
      this(var1, var2, 10, var3, var4, (Predicate)null);
   }

   public EntityAINearestAttackableTarget(EntityCreature var1, Class<T> var2, int var3, boolean var4, boolean var5, @Nullable Predicate<? super T> var6) {
      super(var1, var4, var5);
      this.field_75307_b = var2;
      this.field_75308_c = var3;
      this.field_75306_g = new EntityAINearestAttackableTarget.Sorter(var1);
      this.func_75248_a(1);
      this.field_82643_g = (var2x) -> {
         if (var2x == null) {
            return false;
         } else if (var6 != null && !var6.test(var2x)) {
            return false;
         } else {
            return !EntitySelectors.field_180132_d.test(var2x) ? false : this.func_75296_a(var2x, false);
         }
      };
   }

   public boolean func_75250_a() {
      if (this.field_75308_c > 0 && this.field_75299_d.func_70681_au().nextInt(this.field_75308_c) != 0) {
         return false;
      } else if (this.field_75307_b != EntityPlayer.class && this.field_75307_b != EntityPlayerMP.class) {
         List var1 = this.field_75299_d.field_70170_p.func_175647_a(this.field_75307_b, this.func_188511_a(this.func_111175_f()), this.field_82643_g);
         if (var1.isEmpty()) {
            return false;
         } else {
            Collections.sort(var1, this.field_75306_g);
            this.field_75309_a = (EntityLivingBase)var1.get(0);
            return true;
         }
      } else {
         this.field_75309_a = this.field_75299_d.field_70170_p.func_184150_a(this.field_75299_d.field_70165_t, this.field_75299_d.field_70163_u + (double)this.field_75299_d.func_70047_e(), this.field_75299_d.field_70161_v, this.func_111175_f(), this.func_111175_f(), new Function<EntityPlayer, Double>() {
            @Nullable
            public Double apply(@Nullable EntityPlayer var1) {
               ItemStack var2 = var1.func_184582_a(EntityEquipmentSlot.HEAD);
               return (!(EntityAINearestAttackableTarget.this.field_75299_d instanceof EntitySkeleton) || var2.func_77973_b() != Items.field_196182_dv) && (!(EntityAINearestAttackableTarget.this.field_75299_d instanceof EntityZombie) || var2.func_77973_b() != Items.field_196186_dz) && (!(EntityAINearestAttackableTarget.this.field_75299_d instanceof EntityCreeper) || var2.func_77973_b() != Items.field_196185_dy) ? 1.0D : 0.5D;
            }

            // $FF: synthetic method
            @Nullable
            public Object apply(@Nullable Object var1) {
               return this.apply((EntityPlayer)var1);
            }
         }, this.field_82643_g);
         return this.field_75309_a != null;
      }
   }

   protected AxisAlignedBB func_188511_a(double var1) {
      return this.field_75299_d.func_174813_aQ().func_72314_b(var1, 4.0D, var1);
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
