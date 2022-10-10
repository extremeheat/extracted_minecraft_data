package net.minecraft.entity.ai;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIFollowOwner extends EntityAIBase {
   private final EntityTameable field_75338_d;
   private EntityLivingBase field_75339_e;
   protected final IWorldReaderBase field_75342_a;
   private final double field_75336_f;
   private final PathNavigate field_75337_g;
   private int field_75343_h;
   private final float field_75340_b;
   private final float field_75341_c;
   private float field_75344_i;

   public EntityAIFollowOwner(EntityTameable var1, double var2, float var4, float var5) {
      super();
      this.field_75338_d = var1;
      this.field_75342_a = var1.field_70170_p;
      this.field_75336_f = var2;
      this.field_75337_g = var1.func_70661_as();
      this.field_75341_c = var4;
      this.field_75340_b = var5;
      this.func_75248_a(3);
      if (!(var1.func_70661_as() instanceof PathNavigateGround) && !(var1.func_70661_as() instanceof PathNavigateFlying)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   public boolean func_75250_a() {
      EntityLivingBase var1 = this.field_75338_d.func_70902_q();
      if (var1 == null) {
         return false;
      } else if (var1 instanceof EntityPlayer && ((EntityPlayer)var1).func_175149_v()) {
         return false;
      } else if (this.field_75338_d.func_70906_o()) {
         return false;
      } else if (this.field_75338_d.func_70068_e(var1) < (double)(this.field_75341_c * this.field_75341_c)) {
         return false;
      } else {
         this.field_75339_e = var1;
         return true;
      }
   }

   public boolean func_75253_b() {
      return !this.field_75337_g.func_75500_f() && this.field_75338_d.func_70068_e(this.field_75339_e) > (double)(this.field_75340_b * this.field_75340_b) && !this.field_75338_d.func_70906_o();
   }

   public void func_75249_e() {
      this.field_75343_h = 0;
      this.field_75344_i = this.field_75338_d.func_184643_a(PathNodeType.WATER);
      this.field_75338_d.func_184644_a(PathNodeType.WATER, 0.0F);
   }

   public void func_75251_c() {
      this.field_75339_e = null;
      this.field_75337_g.func_75499_g();
      this.field_75338_d.func_184644_a(PathNodeType.WATER, this.field_75344_i);
   }

   public void func_75246_d() {
      this.field_75338_d.func_70671_ap().func_75651_a(this.field_75339_e, 10.0F, (float)this.field_75338_d.func_70646_bf());
      if (!this.field_75338_d.func_70906_o()) {
         if (--this.field_75343_h <= 0) {
            this.field_75343_h = 10;
            if (!this.field_75337_g.func_75497_a(this.field_75339_e, this.field_75336_f)) {
               if (!this.field_75338_d.func_110167_bD() && !this.field_75338_d.func_184218_aH()) {
                  if (this.field_75338_d.func_70068_e(this.field_75339_e) >= 144.0D) {
                     int var1 = MathHelper.func_76128_c(this.field_75339_e.field_70165_t) - 2;
                     int var2 = MathHelper.func_76128_c(this.field_75339_e.field_70161_v) - 2;
                     int var3 = MathHelper.func_76128_c(this.field_75339_e.func_174813_aQ().field_72338_b);

                     for(int var4 = 0; var4 <= 4; ++var4) {
                        for(int var5 = 0; var5 <= 4; ++var5) {
                           if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) && this.func_192381_a(var1, var2, var3, var4, var5)) {
                              this.field_75338_d.func_70012_b((double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), this.field_75338_d.field_70177_z, this.field_75338_d.field_70125_A);
                              this.field_75337_g.func_75499_g();
                              return;
                           }
                        }
                     }

                  }
               }
            }
         }
      }
   }

   protected boolean func_192381_a(int var1, int var2, int var3, int var4, int var5) {
      BlockPos var6 = new BlockPos(var1 + var4, var3 - 1, var2 + var5);
      IBlockState var7 = this.field_75342_a.func_180495_p(var6);
      return var7.func_193401_d(this.field_75342_a, var6, EnumFacing.DOWN) == BlockFaceShape.SOLID && var7.func_189884_a(this.field_75338_d) && this.field_75342_a.func_175623_d(var6.func_177984_a()) && this.field_75342_a.func_175623_d(var6.func_177981_b(2));
   }
}
