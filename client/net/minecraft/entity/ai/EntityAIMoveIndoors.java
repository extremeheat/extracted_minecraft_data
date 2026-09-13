package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveIndoors extends EntityAIBase {
   private EntityCreature field_75424_a;
   private VillageDoorInfo field_75422_b;
   private int field_75423_c = -1;
   private int field_75421_d = -1;

   public EntityAIMoveIndoors(EntityCreature var1) {
      super();
      this.field_75424_a = var1;
      this.func_75248_a(1);
   }

   @Override
   public boolean func_75250_a() {
      int var1 = MathHelper.func_76128_c(this.field_75424_a.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_75424_a.field_70163_u);
      int var3 = MathHelper.func_76128_c(this.field_75424_a.field_70161_v);
      if ((
            !this.field_75424_a.field_70170_p.func_72935_r()
               || this.field_75424_a.field_70170_p.func_72896_J()
               || !this.field_75424_a.field_70170_p.func_72807_a(var1, var3).func_76738_d()
         )
         && !this.field_75424_a.field_70170_p.field_73011_w.field_76576_e) {
         if (this.field_75424_a.func_70681_au().nextInt(50) != 0) {
            return false;
         } else if (this.field_75423_c != -1
            && this.field_75424_a.func_70092_e((double)this.field_75423_c, this.field_75424_a.field_70163_u, (double)this.field_75421_d) < 4.0) {
            return false;
         } else {
            Village var4 = this.field_75424_a.field_70170_p.field_72982_D.func_75550_a(var1, var2, var3, 14);
            if (var4 == null) {
               return false;
            } else {
               this.field_75422_b = var4.func_75569_c(var1, var2, var3);
               return this.field_75422_b != null;
            }
         }
      } else {
         return false;
      }
   }

   @Override
   public boolean func_75253_b() {
      return !this.field_75424_a.func_70661_as().func_75500_f();
   }

   @Override
   public void func_75249_e() {
      this.field_75423_c = -1;
      if (this.field_75424_a
            .func_70092_e((double)this.field_75422_b.func_75471_a(), (double)this.field_75422_b.field_75479_b, (double)this.field_75422_b.func_75472_c())
         > 256.0) {
         Vec3 var1 = RandomPositionGenerator.func_75464_a(
            this.field_75424_a,
            14,
            3,
            Vec3.func_72443_a(
               (double)this.field_75422_b.func_75471_a() + 0.5, (double)this.field_75422_b.func_75473_b(), (double)this.field_75422_b.func_75472_c() + 0.5
            )
         );
         if (var1 != null) {
            this.field_75424_a.func_70661_as().func_75492_a(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c, 1.0);
         }
      } else {
         this.field_75424_a
            .func_70661_as()
            .func_75492_a(
               (double)this.field_75422_b.func_75471_a() + 0.5,
               (double)this.field_75422_b.func_75473_b(),
               (double)this.field_75422_b.func_75472_c() + 0.5,
               1.0
            );
      }
   }

   @Override
   public void func_75251_c() {
      this.field_75423_c = this.field_75422_b.func_75471_a();
      this.field_75421_d = this.field_75422_b.func_75472_c();
      this.field_75422_b = null;
   }
}
