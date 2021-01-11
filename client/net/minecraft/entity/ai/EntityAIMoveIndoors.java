package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
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

   public boolean func_75250_a() {
      BlockPos var1 = new BlockPos(this.field_75424_a);
      if ((!this.field_75424_a.field_70170_p.func_72935_r() || this.field_75424_a.field_70170_p.func_72896_J() && !this.field_75424_a.field_70170_p.func_180494_b(var1).func_76738_d()) && !this.field_75424_a.field_70170_p.field_73011_w.func_177495_o()) {
         if (this.field_75424_a.func_70681_au().nextInt(50) != 0) {
            return false;
         } else if (this.field_75423_c != -1 && this.field_75424_a.func_70092_e((double)this.field_75423_c, this.field_75424_a.field_70163_u, (double)this.field_75421_d) < 4.0D) {
            return false;
         } else {
            Village var2 = this.field_75424_a.field_70170_p.func_175714_ae().func_176056_a(var1, 14);
            if (var2 == null) {
               return false;
            } else {
               this.field_75422_b = var2.func_179863_c(var1);
               return this.field_75422_b != null;
            }
         }
      } else {
         return false;
      }
   }

   public boolean func_75253_b() {
      return !this.field_75424_a.func_70661_as().func_75500_f();
   }

   public void func_75249_e() {
      this.field_75423_c = -1;
      BlockPos var1 = this.field_75422_b.func_179856_e();
      int var2 = var1.func_177958_n();
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p();
      if (this.field_75424_a.func_174818_b(var1) > 256.0D) {
         Vec3 var5 = RandomPositionGenerator.func_75464_a(this.field_75424_a, 14, 3, new Vec3((double)var2 + 0.5D, (double)var3, (double)var4 + 0.5D));
         if (var5 != null) {
            this.field_75424_a.func_70661_as().func_75492_a(var5.field_72450_a, var5.field_72448_b, var5.field_72449_c, 1.0D);
         }
      } else {
         this.field_75424_a.func_70661_as().func_75492_a((double)var2 + 0.5D, (double)var3, (double)var4 + 0.5D, 1.0D);
      }

   }

   public void func_75251_c() {
      this.field_75423_c = this.field_75422_b.func_179856_e().func_177958_n();
      this.field_75421_d = this.field_75422_b.func_179856_e().func_177952_p();
      this.field_75422_b = null;
   }
}
