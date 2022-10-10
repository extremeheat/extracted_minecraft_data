package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySquid extends EntityWaterMob {
   public float field_70861_d;
   public float field_70862_e;
   public float field_70859_f;
   public float field_70860_g;
   public float field_70867_h;
   public float field_70868_i;
   public float field_70866_j;
   public float field_70865_by;
   private float field_70863_bz;
   private float field_70864_bA;
   private float field_70871_bB;
   private float field_70872_bC;
   private float field_70869_bD;
   private float field_70870_bE;

   public EntitySquid(World var1) {
      super(EntityType.field_200749_ao, var1);
      this.func_70105_a(0.8F, 0.8F);
      this.field_70146_Z.setSeed((long)(1 + this.func_145782_y()));
      this.field_70864_bA = 1.0F / (this.field_70146_Z.nextFloat() + 1.0F) * 0.2F;
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(0, new EntitySquid.AIMoveRandom(this));
      this.field_70714_bg.func_75776_a(1, new EntitySquid.AIFlee());
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(10.0D);
   }

   public float func_70047_e() {
      return this.field_70131_O * 0.5F;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187829_fQ;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187833_fS;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187831_fR;
   }

   protected float func_70599_aP() {
      return 0.4F;
   }

   protected boolean func_70041_e_() {
      return false;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186381_af;
   }

   public void func_70636_d() {
      super.func_70636_d();
      this.field_70862_e = this.field_70861_d;
      this.field_70860_g = this.field_70859_f;
      this.field_70868_i = this.field_70867_h;
      this.field_70865_by = this.field_70866_j;
      this.field_70867_h += this.field_70864_bA;
      if ((double)this.field_70867_h > 6.283185307179586D) {
         if (this.field_70170_p.field_72995_K) {
            this.field_70867_h = 6.2831855F;
         } else {
            this.field_70867_h = (float)((double)this.field_70867_h - 6.283185307179586D);
            if (this.field_70146_Z.nextInt(10) == 0) {
               this.field_70864_bA = 1.0F / (this.field_70146_Z.nextFloat() + 1.0F) * 0.2F;
            }

            this.field_70170_p.func_72960_a(this, (byte)19);
         }
      }

      if (this.func_203005_aq()) {
         float var1;
         if (this.field_70867_h < 3.1415927F) {
            var1 = this.field_70867_h / 3.1415927F;
            this.field_70866_j = MathHelper.func_76126_a(var1 * var1 * 3.1415927F) * 3.1415927F * 0.25F;
            if ((double)var1 > 0.75D) {
               this.field_70863_bz = 1.0F;
               this.field_70871_bB = 1.0F;
            } else {
               this.field_70871_bB *= 0.8F;
            }
         } else {
            this.field_70866_j = 0.0F;
            this.field_70863_bz *= 0.9F;
            this.field_70871_bB *= 0.99F;
         }

         if (!this.field_70170_p.field_72995_K) {
            this.field_70159_w = (double)(this.field_70872_bC * this.field_70863_bz);
            this.field_70181_x = (double)(this.field_70869_bD * this.field_70863_bz);
            this.field_70179_y = (double)(this.field_70870_bE * this.field_70863_bz);
         }

         var1 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
         this.field_70761_aq += (-((float)MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y)) * 57.295776F - this.field_70761_aq) * 0.1F;
         this.field_70177_z = this.field_70761_aq;
         this.field_70859_f = (float)((double)this.field_70859_f + 3.141592653589793D * (double)this.field_70871_bB * 1.5D);
         this.field_70861_d += (-((float)MathHelper.func_181159_b((double)var1, this.field_70181_x)) * 57.295776F - this.field_70861_d) * 0.1F;
      } else {
         this.field_70866_j = MathHelper.func_76135_e(MathHelper.func_76126_a(this.field_70867_h)) * 3.1415927F * 0.25F;
         if (!this.field_70170_p.field_72995_K) {
            this.field_70159_w = 0.0D;
            this.field_70179_y = 0.0D;
            if (this.func_70644_a(MobEffects.field_188424_y)) {
               this.field_70181_x += 0.05D * (double)(this.func_70660_b(MobEffects.field_188424_y).func_76458_c() + 1) - this.field_70181_x;
            } else if (!this.func_189652_ae()) {
               this.field_70181_x -= 0.08D;
            }

            this.field_70181_x *= 0.9800000190734863D;
         }

         this.field_70861_d = (float)((double)this.field_70861_d + (double)(-90.0F - this.field_70861_d) * 0.02D);
      }

   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (super.func_70097_a(var1, var2) && this.func_70643_av() != null) {
         this.func_203039_dq();
         return true;
      } else {
         return false;
      }
   }

   private Vec3d func_207400_b(Vec3d var1) {
      Vec3d var2 = var1.func_178789_a(this.field_70862_e * 0.017453292F);
      var2 = var2.func_178785_b(-this.field_70760_ar * 0.017453292F);
      return var2;
   }

   private void func_203039_dq() {
      this.func_184185_a(SoundEvents.field_203639_hT, this.func_70599_aP(), this.func_70647_i());
      Vec3d var1 = this.func_207400_b(new Vec3d(0.0D, -1.0D, 0.0D)).func_72441_c(this.field_70165_t, this.field_70163_u, this.field_70161_v);

      for(int var2 = 0; var2 < 30; ++var2) {
         Vec3d var3 = this.func_207400_b(new Vec3d((double)this.field_70146_Z.nextFloat() * 0.6D - 0.3D, -1.0D, (double)this.field_70146_Z.nextFloat() * 0.6D - 0.3D));
         Vec3d var4 = var3.func_186678_a(0.3D + (double)(this.field_70146_Z.nextFloat() * 2.0F));
         ((WorldServer)this.field_70170_p).func_195598_a(Particles.field_203219_V, var1.field_72450_a, var1.field_72448_b + 0.5D, var1.field_72449_c, 0, var4.field_72450_a, var4.field_72448_b, var4.field_72449_c, 0.10000000149011612D);
      }

   }

   public void func_191986_a(float var1, float var2, float var3) {
      this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      return this.field_70163_u > 45.0D && this.field_70163_u < (double)var1.func_181545_F();
   }

   public void func_70103_a(byte var1) {
      if (var1 == 19) {
         this.field_70867_h = 0.0F;
      } else {
         super.func_70103_a(var1);
      }

   }

   public void func_175568_b(float var1, float var2, float var3) {
      this.field_70872_bC = var1;
      this.field_70869_bD = var2;
      this.field_70870_bE = var3;
   }

   public boolean func_175567_n() {
      return this.field_70872_bC != 0.0F || this.field_70869_bD != 0.0F || this.field_70870_bE != 0.0F;
   }

   class AIFlee extends EntityAIBase {
      private int field_203125_b;

      private AIFlee() {
         super();
      }

      public boolean func_75250_a() {
         EntityLivingBase var1 = EntitySquid.this.func_70643_av();
         if (EntitySquid.this.func_70090_H() && var1 != null) {
            return EntitySquid.this.func_70068_e(var1) < 100.0D;
         } else {
            return false;
         }
      }

      public void func_75249_e() {
         this.field_203125_b = 0;
      }

      public void func_75246_d() {
         ++this.field_203125_b;
         EntityLivingBase var1 = EntitySquid.this.func_70643_av();
         if (var1 != null) {
            Vec3d var2 = new Vec3d(EntitySquid.this.field_70165_t - var1.field_70165_t, EntitySquid.this.field_70163_u - var1.field_70163_u, EntitySquid.this.field_70161_v - var1.field_70161_v);
            IBlockState var3 = EntitySquid.this.field_70170_p.func_180495_p(new BlockPos(EntitySquid.this.field_70165_t + var2.field_72450_a, EntitySquid.this.field_70163_u + var2.field_72448_b, EntitySquid.this.field_70161_v + var2.field_72449_c));
            IFluidState var4 = EntitySquid.this.field_70170_p.func_204610_c(new BlockPos(EntitySquid.this.field_70165_t + var2.field_72450_a, EntitySquid.this.field_70163_u + var2.field_72448_b, EntitySquid.this.field_70161_v + var2.field_72449_c));
            if (var4.func_206884_a(FluidTags.field_206959_a) || var3.func_196958_f()) {
               double var5 = var2.func_72433_c();
               if (var5 > 0.0D) {
                  var2.func_72432_b();
                  float var7 = 3.0F;
                  if (var5 > 5.0D) {
                     var7 = (float)((double)var7 - (var5 - 5.0D) / 5.0D);
                  }

                  if (var7 > 0.0F) {
                     var2 = var2.func_186678_a((double)var7);
                  }
               }

               if (var3.func_196958_f()) {
                  var2 = var2.func_178786_a(0.0D, var2.field_72448_b, 0.0D);
               }

               EntitySquid.this.func_175568_b((float)var2.field_72450_a / 20.0F, (float)var2.field_72448_b / 20.0F, (float)var2.field_72449_c / 20.0F);
            }

            if (this.field_203125_b % 10 == 5) {
               EntitySquid.this.field_70170_p.func_195594_a(Particles.field_197612_e, EntitySquid.this.field_70165_t, EntitySquid.this.field_70163_u, EntitySquid.this.field_70161_v, 0.0D, 0.0D, 0.0D);
            }

         }
      }

      // $FF: synthetic method
      AIFlee(Object var2) {
         this();
      }
   }

   class AIMoveRandom extends EntityAIBase {
      private final EntitySquid field_179476_a;

      public AIMoveRandom(EntitySquid var2) {
         super();
         this.field_179476_a = var2;
      }

      public boolean func_75250_a() {
         return true;
      }

      public void func_75246_d() {
         int var1 = this.field_179476_a.func_70654_ax();
         if (var1 > 100) {
            this.field_179476_a.func_175568_b(0.0F, 0.0F, 0.0F);
         } else if (this.field_179476_a.func_70681_au().nextInt(50) == 0 || !this.field_179476_a.field_70171_ac || !this.field_179476_a.func_175567_n()) {
            float var2 = this.field_179476_a.func_70681_au().nextFloat() * 6.2831855F;
            float var3 = MathHelper.func_76134_b(var2) * 0.2F;
            float var4 = -0.1F + this.field_179476_a.func_70681_au().nextFloat() * 0.2F;
            float var5 = MathHelper.func_76126_a(var2) * 0.2F;
            this.field_179476_a.func_175568_b(var3, var4, var5);
         }

      }
   }
}
