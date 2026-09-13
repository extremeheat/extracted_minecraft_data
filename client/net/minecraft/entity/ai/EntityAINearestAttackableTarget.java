package net.minecraft.entity.ai;

import java.util.Collections;
import java.util.List;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;

public class EntityAINearestAttackableTarget extends EntityAITarget {
   private final Class field_75307_b;
   private final int field_75308_c;
   private final EntityAINearestAttackableTarget$Sorter field_75306_g;
   private final IEntitySelector field_82643_g;
   private EntityLivingBase field_75309_a;

   public EntityAINearestAttackableTarget(EntityCreature var1, Class var2, int var3, boolean var4) {
      this(var1, var2, var3, var4, false);
   }

   public EntityAINearestAttackableTarget(EntityCreature var1, Class var2, int var3, boolean var4, boolean var5) {
      this(var1, var2, var3, var4, var5, null);
   }

   public EntityAINearestAttackableTarget(EntityCreature var1, Class var2, int var3, boolean var4, boolean var5, IEntitySelector var6) {
      super(var1, var4, var5);
      this.field_75307_b = var2;
      this.field_75308_c = var3;
      this.field_75306_g = new EntityAINearestAttackableTarget$Sorter(var1);
      this.func_75248_a(1);
      this.field_82643_g = new EntityAINearestAttackableTarget$1(this, var6);
   }

   @Override
   public boolean func_75250_a() {
      if (this.field_75308_c > 0 && this.field_75299_d.func_70681_au().nextInt(this.field_75308_c) != 0) {
         return false;
      } else {
         double var1 = this.func_111175_f();
         List var3 = this.field_75299_d
            .field_70170_p
            .func_82733_a(this.field_75307_b, this.field_75299_d.field_70121_D.func_72314_b(var1, 4.0, var1), this.field_82643_g);
         Collections.sort(var3, this.field_75306_g);
         if (var3.isEmpty()) {
            return false;
         } else {
            this.field_75309_a = (EntityLivingBase)var3.get(0);
            return true;
         }
      }
   }

   @Override
   public void func_75249_e() {
      this.field_75299_d.func_70624_b(this.field_75309_a);
      super.func_75249_e();
   }
}
