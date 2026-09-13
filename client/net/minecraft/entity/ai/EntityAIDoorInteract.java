package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public abstract class EntityAIDoorInteract extends EntityAIBase {
   protected EntityLiving field_75356_a;
   protected int field_75354_b;
   protected int field_75355_c;
   protected int field_75352_d;
   protected BlockDoor field_151504_e;
   boolean field_75350_f;
   float field_75351_g;
   float field_75357_h;

   public EntityAIDoorInteract(EntityLiving var1) {
      super();
      this.field_75356_a = var1;
   }

   @Override
   public boolean func_75250_a() {
      if (!this.field_75356_a.field_70123_F) {
         return false;
      } else {
         PathNavigate var1 = this.field_75356_a.func_70661_as();
         PathEntity var2 = var1.func_75505_d();
         if (var2 != null && !var2.func_75879_b() && var1.func_75507_c()) {
            for(int var3 = 0; var3 < Math.min(var2.func_75873_e() + 2, var2.func_75874_d()); ++var3) {
               PathPoint var4 = var2.func_75877_a(var3);
               this.field_75354_b = var4.field_75839_a;
               this.field_75355_c = var4.field_75837_b + 1;
               this.field_75352_d = var4.field_75838_c;
               if (!(this.field_75356_a.func_70092_e((double)this.field_75354_b, this.field_75356_a.field_70163_u, (double)this.field_75352_d) > 2.25)) {
                  this.field_151504_e = this.func_151503_a(this.field_75354_b, this.field_75355_c, this.field_75352_d);
                  if (this.field_151504_e != null) {
                     return true;
                  }
               }
            }

            this.field_75354_b = MathHelper.func_76128_c(this.field_75356_a.field_70165_t);
            this.field_75355_c = MathHelper.func_76128_c(this.field_75356_a.field_70163_u + 1.0);
            this.field_75352_d = MathHelper.func_76128_c(this.field_75356_a.field_70161_v);
            this.field_151504_e = this.func_151503_a(this.field_75354_b, this.field_75355_c, this.field_75352_d);
            return this.field_151504_e != null;
         } else {
            return false;
         }
      }
   }

   @Override
   public boolean func_75253_b() {
      return !this.field_75350_f;
   }

   @Override
   public void func_75249_e() {
      this.field_75350_f = false;
      this.field_75351_g = (float)((double)((float)this.field_75354_b + 0.5F) - this.field_75356_a.field_70165_t);
      this.field_75357_h = (float)((double)((float)this.field_75352_d + 0.5F) - this.field_75356_a.field_70161_v);
   }

   @Override
   public void func_75246_d() {
      float var1 = (float)((double)((float)this.field_75354_b + 0.5F) - this.field_75356_a.field_70165_t);
      float var2 = (float)((double)((float)this.field_75352_d + 0.5F) - this.field_75356_a.field_70161_v);
      float var3 = this.field_75351_g * var1 + this.field_75357_h * var2;
      if (var3 < 0.0F) {
         this.field_75350_f = true;
      }
   }

   private BlockDoor func_151503_a(int var1, int var2, int var3) {
      Block var4 = this.field_75356_a.field_70170_p.func_147439_a(var1, var2, var3);
      return var4 != Blocks.field_150466_ao ? null : (BlockDoor)var4;
   }
}
