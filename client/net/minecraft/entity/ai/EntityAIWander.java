package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.Vec3d;

public class EntityAIWander extends EntityAIBase {
   protected final EntityCreature field_75457_a;
   protected double field_75455_b;
   protected double field_75456_c;
   protected double field_75453_d;
   protected final double field_75454_e;
   protected int field_179481_f;
   protected boolean field_179482_g;

   public EntityAIWander(EntityCreature var1, double var2) {
      this(var1, var2, 120);
   }

   public EntityAIWander(EntityCreature var1, double var2, int var4) {
      super();
      this.field_75457_a = var1;
      this.field_75454_e = var2;
      this.field_179481_f = var4;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      if (!this.field_179482_g) {
         if (this.field_75457_a.func_70654_ax() >= 100) {
            return false;
         }

         if (this.field_75457_a.func_70681_au().nextInt(this.field_179481_f) != 0) {
            return false;
         }
      }

      Vec3d var1 = this.func_190864_f();
      if (var1 == null) {
         return false;
      } else {
         this.field_75455_b = var1.field_72450_a;
         this.field_75456_c = var1.field_72448_b;
         this.field_75453_d = var1.field_72449_c;
         this.field_179482_g = false;
         return true;
      }
   }

   @Nullable
   protected Vec3d func_190864_f() {
      return RandomPositionGenerator.func_75463_a(this.field_75457_a, 10, 7);
   }

   public boolean func_75253_b() {
      return !this.field_75457_a.func_70661_as().func_75500_f();
   }

   public void func_75249_e() {
      this.field_75457_a.func_70661_as().func_75492_a(this.field_75455_b, this.field_75456_c, this.field_75453_d, this.field_75454_e);
   }

   public void func_179480_f() {
      this.field_179482_g = true;
   }

   public void func_179479_b(int var1) {
      this.field_179481_f = var1;
   }
}
