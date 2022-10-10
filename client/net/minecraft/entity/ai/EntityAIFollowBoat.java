package net.minecraft.entity.ai;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIFollowBoat extends EntityAIBase {
   private int field_205143_a;
   private final EntityCreature field_205144_b;
   private EntityLivingBase field_205145_c;
   private BoatGoals field_205146_d;

   public EntityAIFollowBoat(EntityCreature var1) {
      super();
      this.field_205144_b = var1;
   }

   public boolean func_75250_a() {
      List var1 = this.field_205144_b.field_70170_p.func_72872_a(EntityBoat.class, this.field_205144_b.func_174813_aQ().func_186662_g(5.0D));
      boolean var2 = false;
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         EntityBoat var4 = (EntityBoat)var3.next();
         if (var4.func_184179_bs() != null && (MathHelper.func_76135_e(((EntityLivingBase)var4.func_184179_bs()).field_70702_br) > 0.0F || MathHelper.func_76135_e(((EntityLivingBase)var4.func_184179_bs()).field_191988_bg) > 0.0F)) {
            var2 = true;
            break;
         }
      }

      return this.field_205145_c != null && (MathHelper.func_76135_e(this.field_205145_c.field_70702_br) > 0.0F || MathHelper.func_76135_e(this.field_205145_c.field_191988_bg) > 0.0F) || var2;
   }

   public boolean func_75252_g() {
      return true;
   }

   public boolean func_75253_b() {
      return this.field_205145_c != null && this.field_205145_c.func_184218_aH() && (MathHelper.func_76135_e(this.field_205145_c.field_70702_br) > 0.0F || MathHelper.func_76135_e(this.field_205145_c.field_191988_bg) > 0.0F);
   }

   public void func_75249_e() {
      List var1 = this.field_205144_b.field_70170_p.func_72872_a(EntityBoat.class, this.field_205144_b.func_174813_aQ().func_186662_g(5.0D));
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         EntityBoat var3 = (EntityBoat)var2.next();
         if (var3.func_184179_bs() != null && var3.func_184179_bs() instanceof EntityLivingBase) {
            this.field_205145_c = (EntityLivingBase)var3.func_184179_bs();
            break;
         }
      }

      this.field_205143_a = 0;
      this.field_205146_d = BoatGoals.GO_TO_BOAT;
   }

   public void func_75251_c() {
      this.field_205145_c = null;
   }

   public void func_75246_d() {
      boolean var1 = MathHelper.func_76135_e(this.field_205145_c.field_70702_br) > 0.0F || MathHelper.func_76135_e(this.field_205145_c.field_191988_bg) > 0.0F;
      float var2 = this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION ? (var1 ? 0.17999999F : 0.0F) : 0.135F;
      this.field_205144_b.func_191958_b(this.field_205144_b.field_70702_br, this.field_205144_b.field_70701_bs, this.field_205144_b.field_191988_bg, var2);
      this.field_205144_b.func_70091_d(MoverType.SELF, this.field_205144_b.field_70159_w, this.field_205144_b.field_70181_x, this.field_205144_b.field_70179_y);
      if (--this.field_205143_a <= 0) {
         this.field_205143_a = 10;
         if (this.field_205146_d == BoatGoals.GO_TO_BOAT) {
            BlockPos var3 = (new BlockPos(this.field_205145_c)).func_177972_a(this.field_205145_c.func_174811_aO().func_176734_d());
            var3 = var3.func_177982_a(0, -1, 0);
            this.field_205144_b.func_70661_as().func_75492_a((double)var3.func_177958_n(), (double)var3.func_177956_o(), (double)var3.func_177952_p(), 1.0D);
            if (this.field_205144_b.func_70032_d(this.field_205145_c) < 4.0F) {
               this.field_205143_a = 0;
               this.field_205146_d = BoatGoals.GO_IN_BOAT_DIRECTION;
            }
         } else if (this.field_205146_d == BoatGoals.GO_IN_BOAT_DIRECTION) {
            EnumFacing var5 = this.field_205145_c.func_184172_bi();
            BlockPos var4 = (new BlockPos(this.field_205145_c)).func_177967_a(var5, 10);
            this.field_205144_b.func_70661_as().func_75492_a((double)var4.func_177958_n(), (double)(var4.func_177956_o() - 1), (double)var4.func_177952_p(), 1.0D);
            if (this.field_205144_b.func_70032_d(this.field_205145_c) > 12.0F) {
               this.field_205143_a = 0;
               this.field_205146_d = BoatGoals.GO_TO_BOAT;
            }
         }

      }
   }
}
