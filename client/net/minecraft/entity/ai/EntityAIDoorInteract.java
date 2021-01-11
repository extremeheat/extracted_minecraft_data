package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;

public abstract class EntityAIDoorInteract extends EntityAIBase {
   protected EntityLiving field_75356_a;
   protected BlockPos field_179507_b;
   protected BlockDoor field_151504_e;
   boolean field_75350_f;
   float field_75351_g;
   float field_75357_h;

   public EntityAIDoorInteract(EntityLiving var1) {
      super();
      this.field_179507_b = BlockPos.field_177992_a;
      this.field_75356_a = var1;
      if (!(var1.func_70661_as() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   public boolean func_75250_a() {
      if (!this.field_75356_a.field_70123_F) {
         return false;
      } else {
         PathNavigateGround var1 = (PathNavigateGround)this.field_75356_a.func_70661_as();
         PathEntity var2 = var1.func_75505_d();
         if (var2 != null && !var2.func_75879_b() && var1.func_179686_g()) {
            for(int var3 = 0; var3 < Math.min(var2.func_75873_e() + 2, var2.func_75874_d()); ++var3) {
               PathPoint var4 = var2.func_75877_a(var3);
               this.field_179507_b = new BlockPos(var4.field_75839_a, var4.field_75837_b + 1, var4.field_75838_c);
               if (this.field_75356_a.func_70092_e((double)this.field_179507_b.func_177958_n(), this.field_75356_a.field_70163_u, (double)this.field_179507_b.func_177952_p()) <= 2.25D) {
                  this.field_151504_e = this.func_179506_a(this.field_179507_b);
                  if (this.field_151504_e != null) {
                     return true;
                  }
               }
            }

            this.field_179507_b = (new BlockPos(this.field_75356_a)).func_177984_a();
            this.field_151504_e = this.func_179506_a(this.field_179507_b);
            return this.field_151504_e != null;
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

   private BlockDoor func_179506_a(BlockPos var1) {
      Block var2 = this.field_75356_a.field_70170_p.func_180495_p(var1).func_177230_c();
      return var2 instanceof BlockDoor && var2.func_149688_o() == Material.field_151575_d ? (BlockDoor)var2 : null;
   }
}
