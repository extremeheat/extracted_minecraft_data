package net.minecraft.entity;

import java.util.UUID;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class EntityCreature extends EntityLiving {
   public static final UUID field_110179_h = UUID.fromString("E199AD21-BA8A-4C53-8D13-6182D5C69D3A");
   public static final AttributeModifier field_110181_i;
   private BlockPos field_70775_bC;
   private float field_70772_bD;
   private EntityAIBase field_110178_bs;
   private boolean field_110180_bt;

   public EntityCreature(World var1) {
      super(var1);
      this.field_70775_bC = BlockPos.field_177992_a;
      this.field_70772_bD = -1.0F;
      this.field_110178_bs = new EntityAIMoveTowardsRestriction(this, 1.0D);
   }

   public float func_180484_a(BlockPos var1) {
      return 0.0F;
   }

   public boolean func_70601_bi() {
      return super.func_70601_bi() && this.func_180484_a(new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v)) >= 0.0F;
   }

   public boolean func_70781_l() {
      return !this.field_70699_by.func_75500_f();
   }

   public boolean func_110173_bK() {
      return this.func_180485_d(new BlockPos(this));
   }

   public boolean func_180485_d(BlockPos var1) {
      if (this.field_70772_bD == -1.0F) {
         return true;
      } else {
         return this.field_70775_bC.func_177951_i(var1) < (double)(this.field_70772_bD * this.field_70772_bD);
      }
   }

   public void func_175449_a(BlockPos var1, int var2) {
      this.field_70775_bC = var1;
      this.field_70772_bD = (float)var2;
   }

   public BlockPos func_180486_cf() {
      return this.field_70775_bC;
   }

   public float func_110174_bM() {
      return this.field_70772_bD;
   }

   public void func_110177_bN() {
      this.field_70772_bD = -1.0F;
   }

   public boolean func_110175_bO() {
      return this.field_70772_bD != -1.0F;
   }

   protected void func_110159_bB() {
      super.func_110159_bB();
      if (this.func_110167_bD() && this.func_110166_bE() != null && this.func_110166_bE().field_70170_p == this.field_70170_p) {
         Entity var1 = this.func_110166_bE();
         this.func_175449_a(new BlockPos((int)var1.field_70165_t, (int)var1.field_70163_u, (int)var1.field_70161_v), 5);
         float var2 = this.func_70032_d(var1);
         if (this instanceof EntityTameable && ((EntityTameable)this).func_70906_o()) {
            if (var2 > 10.0F) {
               this.func_110160_i(true, true);
            }

            return;
         }

         if (!this.field_110180_bt) {
            this.field_70714_bg.func_75776_a(2, this.field_110178_bs);
            if (this.func_70661_as() instanceof PathNavigateGround) {
               ((PathNavigateGround)this.func_70661_as()).func_179690_a(false);
            }

            this.field_110180_bt = true;
         }

         this.func_142017_o(var2);
         if (var2 > 4.0F) {
            this.func_70661_as().func_75497_a(var1, 1.0D);
         }

         if (var2 > 6.0F) {
            double var3 = (var1.field_70165_t - this.field_70165_t) / (double)var2;
            double var5 = (var1.field_70163_u - this.field_70163_u) / (double)var2;
            double var7 = (var1.field_70161_v - this.field_70161_v) / (double)var2;
            this.field_70159_w += var3 * Math.abs(var3) * 0.4D;
            this.field_70181_x += var5 * Math.abs(var5) * 0.4D;
            this.field_70179_y += var7 * Math.abs(var7) * 0.4D;
         }

         if (var2 > 10.0F) {
            this.func_110160_i(true, true);
         }
      } else if (!this.func_110167_bD() && this.field_110180_bt) {
         this.field_110180_bt = false;
         this.field_70714_bg.func_85156_a(this.field_110178_bs);
         if (this.func_70661_as() instanceof PathNavigateGround) {
            ((PathNavigateGround)this.func_70661_as()).func_179690_a(true);
         }

         this.func_110177_bN();
      }

   }

   protected void func_142017_o(float var1) {
   }

   static {
      field_110181_i = (new AttributeModifier(field_110179_h, "Fleeing speed bonus", 2.0D, 2)).func_111168_a(false);
   }
}
