package net.minecraft.entity.ai;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase {
   private EntityCreature field_75420_a;
   private double field_75418_b;
   private PathEntity field_75419_c;
   private VillageDoorInfo field_75416_d;
   private boolean field_75417_e;
   private List<VillageDoorInfo> field_75415_f = Lists.newArrayList();

   public EntityAIMoveThroughVillage(EntityCreature var1, double var2, boolean var4) {
      super();
      this.field_75420_a = var1;
      this.field_75418_b = var2;
      this.field_75417_e = var4;
      this.func_75248_a(1);
      if (!(var1.func_70661_as() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   public boolean func_75250_a() {
      this.func_75414_f();
      if (this.field_75417_e && this.field_75420_a.field_70170_p.func_72935_r()) {
         return false;
      } else {
         Village var1 = this.field_75420_a.field_70170_p.func_175714_ae().func_176056_a(new BlockPos(this.field_75420_a), 0);
         if (var1 == null) {
            return false;
         } else {
            this.field_75416_d = this.func_75412_a(var1);
            if (this.field_75416_d == null) {
               return false;
            } else {
               PathNavigateGround var2 = (PathNavigateGround)this.field_75420_a.func_70661_as();
               boolean var3 = var2.func_179686_g();
               var2.func_179688_b(false);
               this.field_75419_c = var2.func_179680_a(this.field_75416_d.func_179852_d());
               var2.func_179688_b(var3);
               if (this.field_75419_c != null) {
                  return true;
               } else {
                  Vec3 var4 = RandomPositionGenerator.func_75464_a(this.field_75420_a, 10, 7, new Vec3((double)this.field_75416_d.func_179852_d().func_177958_n(), (double)this.field_75416_d.func_179852_d().func_177956_o(), (double)this.field_75416_d.func_179852_d().func_177952_p()));
                  if (var4 == null) {
                     return false;
                  } else {
                     var2.func_179688_b(false);
                     this.field_75419_c = this.field_75420_a.func_70661_as().func_75488_a(var4.field_72450_a, var4.field_72448_b, var4.field_72449_c);
                     var2.func_179688_b(var3);
                     return this.field_75419_c != null;
                  }
               }
            }
         }
      }
   }

   public boolean func_75253_b() {
      if (this.field_75420_a.func_70661_as().func_75500_f()) {
         return false;
      } else {
         float var1 = this.field_75420_a.field_70130_N + 4.0F;
         return this.field_75420_a.func_174818_b(this.field_75416_d.func_179852_d()) > (double)(var1 * var1);
      }
   }

   public void func_75249_e() {
      this.field_75420_a.func_70661_as().func_75484_a(this.field_75419_c, this.field_75418_b);
   }

   public void func_75251_c() {
      if (this.field_75420_a.func_70661_as().func_75500_f() || this.field_75420_a.func_174818_b(this.field_75416_d.func_179852_d()) < 16.0D) {
         this.field_75415_f.add(this.field_75416_d);
      }

   }

   private VillageDoorInfo func_75412_a(Village var1) {
      VillageDoorInfo var2 = null;
      int var3 = 2147483647;
      List var4 = var1.func_75558_f();
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         VillageDoorInfo var6 = (VillageDoorInfo)var5.next();
         int var7 = var6.func_75474_b(MathHelper.func_76128_c(this.field_75420_a.field_70165_t), MathHelper.func_76128_c(this.field_75420_a.field_70163_u), MathHelper.func_76128_c(this.field_75420_a.field_70161_v));
         if (var7 < var3 && !this.func_75413_a(var6)) {
            var2 = var6;
            var3 = var7;
         }
      }

      return var2;
   }

   private boolean func_75413_a(VillageDoorInfo var1) {
      Iterator var2 = this.field_75415_f.iterator();

      VillageDoorInfo var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (VillageDoorInfo)var2.next();
      } while(!var1.func_179852_d().equals(var3.func_179852_d()));

      return true;
   }

   private void func_75414_f() {
      if (this.field_75415_f.size() > 15) {
         this.field_75415_f.remove(0);
      }

   }
}
