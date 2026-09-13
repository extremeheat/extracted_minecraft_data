package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityAIFleeSun extends EntityAIBase {
   private EntityCreature field_75372_a;
   private double field_75370_b;
   private double field_75371_c;
   private double field_75368_d;
   private double field_75369_e;
   private World field_75367_f;

   public EntityAIFleeSun(EntityCreature var1, double var2) {
      super();
      this.field_75372_a = var1;
      this.field_75369_e = var2;
      this.field_75367_f = var1.field_70170_p;
      this.func_75248_a(1);
   }

   @Override
   public boolean func_75250_a() {
      if (!this.field_75367_f.func_72935_r()) {
         return false;
      } else if (!this.field_75372_a.func_70027_ad()) {
         return false;
      } else if (!this.field_75367_f
         .func_72937_j(
            MathHelper.func_76128_c(this.field_75372_a.field_70165_t),
            (int)this.field_75372_a.field_70121_D.field_72338_b,
            MathHelper.func_76128_c(this.field_75372_a.field_70161_v)
         )) {
         return false;
      } else {
         Vec3 var1 = this.func_75366_f();
         if (var1 == null) {
            return false;
         } else {
            this.field_75370_b = var1.field_72450_a;
            this.field_75371_c = var1.field_72448_b;
            this.field_75368_d = var1.field_72449_c;
            return true;
         }
      }
   }

   @Override
   public boolean func_75253_b() {
      return !this.field_75372_a.func_70661_as().func_75500_f();
   }

   @Override
   public void func_75249_e() {
      this.field_75372_a.func_70661_as().func_75492_a(this.field_75370_b, this.field_75371_c, this.field_75368_d, this.field_75369_e);
   }

   private Vec3 func_75366_f() {
      Random var1 = this.field_75372_a.func_70681_au();

      for(int var2 = 0; var2 < 10; ++var2) {
         int var3 = MathHelper.func_76128_c(this.field_75372_a.field_70165_t + (double)var1.nextInt(20) - 10.0);
         int var4 = MathHelper.func_76128_c(this.field_75372_a.field_70121_D.field_72338_b + (double)var1.nextInt(6) - 3.0);
         int var5 = MathHelper.func_76128_c(this.field_75372_a.field_70161_v + (double)var1.nextInt(20) - 10.0);
         if (!this.field_75367_f.func_72937_j(var3, var4, var5) && this.field_75372_a.func_70783_a(var3, var4, var5) < 0.0F) {
            return Vec3.func_72443_a((double)var3, (double)var4, (double)var5);
         }
      }

      return null;
   }
}
