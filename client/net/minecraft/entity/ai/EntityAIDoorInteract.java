package net.minecraft.entity.ai;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

public abstract class EntityAIDoorInteract extends EntityAIBase {
   protected EntityLiving field_75356_a;
   protected BlockPos field_179507_b;
   protected boolean field_195923_c;
   private boolean field_75350_f;
   private float field_75351_g;
   private float field_75357_h;

   public EntityAIDoorInteract(EntityLiving var1) {
      super();
      this.field_179507_b = BlockPos.field_177992_a;
      this.field_75356_a = var1;
      if (!(var1.func_70661_as() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   protected boolean func_195922_f() {
      if (!this.field_195923_c) {
         return false;
      } else {
         IBlockState var1 = this.field_75356_a.field_70170_p.func_180495_p(this.field_179507_b);
         if (!(var1.func_177230_c() instanceof BlockDoor)) {
            this.field_195923_c = false;
            return false;
         } else {
            return (Boolean)var1.func_177229_b(BlockDoor.field_176519_b);
         }
      }
   }

   protected void func_195921_a(boolean var1) {
      if (this.field_195923_c) {
         IBlockState var2 = this.field_75356_a.field_70170_p.func_180495_p(this.field_179507_b);
         if (var2.func_177230_c() instanceof BlockDoor) {
            ((BlockDoor)var2.func_177230_c()).func_176512_a(this.field_75356_a.field_70170_p, this.field_179507_b, var1);
         }
      }

   }

   public boolean func_75250_a() {
      if (!this.field_75356_a.field_70123_F) {
         return false;
      } else {
         PathNavigateGround var1 = (PathNavigateGround)this.field_75356_a.func_70661_as();
         Path var2 = var1.func_75505_d();
         if (var2 != null && !var2.func_75879_b() && var1.func_179686_g()) {
            for(int var3 = 0; var3 < Math.min(var2.func_75873_e() + 2, var2.func_75874_d()); ++var3) {
               PathPoint var4 = var2.func_75877_a(var3);
               this.field_179507_b = new BlockPos(var4.field_75839_a, var4.field_75837_b + 1, var4.field_75838_c);
               if (this.field_75356_a.func_70092_e((double)this.field_179507_b.func_177958_n(), this.field_75356_a.field_70163_u, (double)this.field_179507_b.func_177952_p()) <= 2.25D) {
                  this.field_195923_c = this.func_195920_a(this.field_179507_b);
                  if (this.field_195923_c) {
                     return true;
                  }
               }
            }

            this.field_179507_b = (new BlockPos(this.field_75356_a)).func_177984_a();
            this.field_195923_c = this.func_195920_a(this.field_179507_b);
            return this.field_195923_c;
         } else {
            return false;
         }
      }
   }

   public boolean func_75253_b() {
      return !this.field_75350_f;
   }

   public void func_75249_e() {
      this.field_75350_f = false;
      this.field_75351_g = (float)((double)((float)this.field_179507_b.func_177958_n() + 0.5F) - this.field_75356_a.field_70165_t);
      this.field_75357_h = (float)((double)((float)this.field_179507_b.func_177952_p() + 0.5F) - this.field_75356_a.field_70161_v);
   }

   public void func_75246_d() {
      float var1 = (float)((double)((float)this.field_179507_b.func_177958_n() + 0.5F) - this.field_75356_a.field_70165_t);
      float var2 = (float)((double)((float)this.field_179507_b.func_177952_p() + 0.5F) - this.field_75356_a.field_70161_v);
      float var3 = this.field_75351_g * var1 + this.field_75357_h * var2;
      if (var3 < 0.0F) {
         this.field_75350_f = true;
      }

   }

   private boolean func_195920_a(BlockPos var1) {
      IBlockState var2 = this.field_75356_a.field_70170_p.func_180495_p(var1);
      return var2.func_177230_c() instanceof BlockDoor && var2.func_185904_a() == Material.field_151575_d;
   }
}
