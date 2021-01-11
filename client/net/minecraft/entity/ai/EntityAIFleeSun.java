package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
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

   public boolean func_75250_a() {
      if (!this.field_75367_f.func_72935_r()) {
         return false;
      } else if (!this.field_75372_a.func_70027_ad()) {
         return false;
      } else if (!this.field_75367_f.func_175678_i(new BlockPos(this.field_75372_a.field_70165_t, this.field_75372_a.func_174813_aQ().field_72338_b, this.field_75372_a.field_70161_v))) {
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

   public boolean func_75253_b() {
      return !this.field_75372_a.func_70661_as().func_75500_f();
   }

   public void func_75249_e() {
      this.field_75372_a.func_70661_as().func_75492_a(this.field_75370_b, this.field_75371_c, this.field_75368_d, this.field_75369_e);
   }

   private Vec3 func_75366_f() {
      Random var1 = this.field_75372_a.func_70681_au();
      BlockPos var2 = new BlockPos(this.field_75372_a.field_70165_t, this.field_75372_a.func_174813_aQ().field_72338_b, this.field_75372_a.field_70161_v);

      for(int var3 = 0; var3 < 10; ++var3) {
         BlockPos var4 = var2.func_177982_a(var1.nextInt(20) - 10, var1.nextInt(6) - 3, var1.nextInt(20) - 10);
         if (!this.field_75367_f.func_175678_i(var4) && this.field_75372_a.func_180484_a(var4) < 0.0F) {
            return new Vec3((double)var4.func_177958_n(), (double)var4.func_177956_o(), (double)var4.func_177952_p());
         }
      }

      return null;
   }
}
