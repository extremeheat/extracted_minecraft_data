package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityPhantom extends EntityFlying implements IMob {
   private static final DataParameter<Integer> field_203035_a;
   private Vec3d field_203036_b;
   private BlockPos field_203037_c;
   private EntityPhantom.AttackPhase field_203038_bx;

   public EntityPhantom(World var1) {
      super(EntityType.field_203097_aH, var1);
      this.field_203036_b = Vec3d.field_186680_a;
      this.field_203037_c = BlockPos.field_177992_a;
      this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
      this.field_70728_aV = 5;
      this.func_70105_a(0.9F, 0.5F);
      this.field_70765_h = new EntityPhantom.MoveHelper(this);
      this.field_70749_g = new EntityPhantom.LookHelper(this);
   }

   protected EntityBodyHelper func_184650_s() {
      return new EntityPhantom.BodyHelper(this);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(1, new EntityPhantom.AIPickAttack());
      this.field_70714_bg.func_75776_a(2, new EntityPhantom.AISweepAttack());
      this.field_70714_bg.func_75776_a(3, new EntityPhantom.AIOrbitPoint());
      this.field_70715_bh.func_75776_a(1, new EntityPhantom.AIAttackPlayer());
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_203035_a, 0);
   }

   public void func_203034_a(int var1) {
      if (var1 < 0) {
         var1 = 0;
      } else if (var1 > 64) {
         var1 = 64;
      }

      this.field_70180_af.func_187227_b(field_203035_a, var1);
      this.func_203033_m();
   }

   public void func_203033_m() {
      int var1 = (Integer)this.field_70180_af.func_187225_a(field_203035_a);
      this.func_70105_a(0.9F + 0.2F * (float)var1, 0.5F + 0.1F * (float)var1);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a((double)(6 + var1));
   }

   public int func_203032_dq() {
      return (Integer)this.field_70180_af.func_187225_a(field_203035_a);
   }

   public float func_70047_e() {
      return this.field_70131_O * 0.35F;
   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_203035_a.equals(var1)) {
         this.func_203033_m();
      }

      super.func_184206_a(var1);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_70170_p.field_72995_K) {
         float var1 = MathHelper.func_76134_b((float)(this.func_145782_y() * 3 + this.field_70173_aa) * 0.13F + 3.1415927F);
         float var2 = MathHelper.func_76134_b((float)(this.func_145782_y() * 3 + this.field_70173_aa + 1) * 0.13F + 3.1415927F);
         if (var1 > 0.0F && var2 <= 0.0F) {
            this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_206944_gn, this.func_184176_by(), 0.95F + this.field_70146_Z.nextFloat() * 0.05F, 0.95F + this.field_70146_Z.nextFloat() * 0.05F, false);
         }

         int var3 = this.func_203032_dq();
         float var4 = MathHelper.func_76134_b(this.field_70177_z * 0.017453292F) * (1.3F + 0.21F * (float)var3);
         float var5 = MathHelper.func_76126_a(this.field_70177_z * 0.017453292F) * (1.3F + 0.21F * (float)var3);
         float var6 = (0.3F + var1 * 0.45F) * ((float)var3 * 0.2F + 1.0F);
         this.field_70170_p.func_195594_a(Particles.field_197596_G, this.field_70165_t + (double)var4, this.field_70163_u + (double)var6, this.field_70161_v + (double)var5, 0.0D, 0.0D, 0.0D);
         this.field_70170_p.func_195594_a(Particles.field_197596_G, this.field_70165_t - (double)var4, this.field_70163_u + (double)var6, this.field_70161_v - (double)var5, 0.0D, 0.0D, 0.0D);
      }

      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
         this.func_70106_y();
      }

   }

   public void func_70636_d() {
      if (this.func_204609_dp()) {
         this.func_70015_d(8);
      }

      super.func_70636_d();
   }

   protected void func_70619_bc() {
      super.func_70619_bc();
   }

   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      this.field_203037_c = (new BlockPos(this)).func_177981_b(5);
      this.func_203034_a(0);
      return super.func_204210_a(var1, var2, var3);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_74764_b("AX")) {
         this.field_203037_c = new BlockPos(var1.func_74762_e("AX"), var1.func_74762_e("AY"), var1.func_74762_e("AZ"));
      }

      this.func_203034_a(var1.func_74762_e("Size"));
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("AX", this.field_203037_c.func_177958_n());
      var1.func_74768_a("AY", this.field_203037_c.func_177956_o());
      var1.func_74768_a("AZ", this.field_203037_c.func_177952_p());
      var1.func_74768_a("Size", this.func_203032_dq());
   }

   public boolean func_70112_a(double var1) {
      return true;
   }

   public SoundCategory func_184176_by() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_203256_ft;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_203259_fw;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_203258_fv;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_203250_E;
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.UNDEAD;
   }

   protected float func_70599_aP() {
      return 1.0F;
   }

   public boolean func_70686_a(Class<? extends EntityLivingBase> var1) {
      return true;
   }

   static {
      field_203035_a = EntityDataManager.func_187226_a(EntityPhantom.class, DataSerializers.field_187192_b);
   }

   class AIAttackPlayer extends EntityAIBase {
      private int field_203142_b;

      private AIAttackPlayer() {
         super();
         this.field_203142_b = 20;
      }

      public boolean func_75250_a() {
         if (this.field_203142_b > 0) {
            --this.field_203142_b;
            return false;
         } else {
            this.field_203142_b = 60;
            AxisAlignedBB var1 = EntityPhantom.this.func_174813_aQ().func_72314_b(16.0D, 64.0D, 16.0D);
            List var2 = EntityPhantom.this.field_70170_p.func_72872_a(EntityPlayer.class, var1);
            if (!var2.isEmpty()) {
               var2.sort((var0, var1x) -> {
                  return var0.field_70163_u > var1x.field_70163_u ? -1 : 1;
               });
               Iterator var3 = var2.iterator();

               while(var3.hasNext()) {
                  EntityPlayer var4 = (EntityPlayer)var3.next();
                  if (EntityAITarget.func_179445_a(EntityPhantom.this, var4, false, false)) {
                     EntityPhantom.this.func_70624_b(var4);
                     return true;
                  }
               }
            }

            return false;
         }
      }

      public boolean func_75253_b() {
         return EntityAITarget.func_179445_a(EntityPhantom.this, EntityPhantom.this.func_70638_az(), false, false);
      }

      // $FF: synthetic method
      AIAttackPlayer(Object var2) {
         this();
      }
   }

   class AIPickAttack extends EntityAIBase {
      private int field_203145_b;

      private AIPickAttack() {
         super();
      }

      public boolean func_75250_a() {
         return EntityAITarget.func_179445_a(EntityPhantom.this, EntityPhantom.this.func_70638_az(), false, false);
      }

      public void func_75249_e() {
         this.field_203145_b = 10;
         EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
         this.func_203143_f();
      }

      public void func_75251_c() {
         EntityPhantom.this.field_203037_c = EntityPhantom.this.field_70170_p.func_205770_a(Heightmap.Type.MOTION_BLOCKING, EntityPhantom.this.field_203037_c).func_177981_b(10 + EntityPhantom.this.field_70146_Z.nextInt(20));
      }

      public void func_75246_d() {
         if (EntityPhantom.this.field_203038_bx == EntityPhantom.AttackPhase.CIRCLE) {
            --this.field_203145_b;
            if (this.field_203145_b <= 0) {
               EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.SWOOP;
               this.func_203143_f();
               this.field_203145_b = (8 + EntityPhantom.this.field_70146_Z.nextInt(4)) * 20;
               EntityPhantom.this.func_184185_a(SoundEvents.field_203260_fx, 10.0F, 0.95F + EntityPhantom.this.field_70146_Z.nextFloat() * 0.1F);
            }
         }

      }

      private void func_203143_f() {
         EntityPhantom.this.field_203037_c = (new BlockPos(EntityPhantom.this.func_70638_az())).func_177981_b(20 + EntityPhantom.this.field_70146_Z.nextInt(20));
         if (EntityPhantom.this.field_203037_c.func_177956_o() < EntityPhantom.this.field_70170_p.func_181545_F()) {
            EntityPhantom.this.field_203037_c = new BlockPos(EntityPhantom.this.field_203037_c.func_177958_n(), EntityPhantom.this.field_70170_p.func_181545_F() + 1, EntityPhantom.this.field_203037_c.func_177952_p());
         }

      }

      // $FF: synthetic method
      AIPickAttack(Object var2) {
         this();
      }
   }

   class AISweepAttack extends EntityPhantom.AIMove {
      private AISweepAttack() {
         super();
      }

      public boolean func_75250_a() {
         return EntityPhantom.this.func_70638_az() != null && EntityPhantom.this.field_203038_bx == EntityPhantom.AttackPhase.SWOOP;
      }

      public boolean func_75253_b() {
         EntityLivingBase var1 = EntityPhantom.this.func_70638_az();
         if (var1 == null) {
            return false;
         } else if (!var1.func_70089_S()) {
            return false;
         } else {
            return !(var1 instanceof EntityPlayer) || !((EntityPlayer)var1).func_175149_v() && !((EntityPlayer)var1).func_184812_l_() ? this.func_75250_a() : false;
         }
      }

      public void func_75249_e() {
      }

      public void func_75251_c() {
         EntityPhantom.this.func_70624_b((EntityLivingBase)null);
         EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
      }

      public void func_75246_d() {
         EntityLivingBase var1 = EntityPhantom.this.func_70638_az();
         EntityPhantom.this.field_203036_b = new Vec3d(var1.field_70165_t, var1.field_70163_u + (double)var1.field_70131_O * 0.5D, var1.field_70161_v);
         if (EntityPhantom.this.func_174813_aQ().func_186662_g(0.20000000298023224D).func_72326_a(var1.func_174813_aQ())) {
            EntityPhantom.this.func_70652_k(var1);
            EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
            EntityPhantom.this.field_70170_p.func_175718_b(1039, new BlockPos(EntityPhantom.this), 0);
         } else if (EntityPhantom.this.field_70123_F || EntityPhantom.this.field_70737_aN > 0) {
            EntityPhantom.this.field_203038_bx = EntityPhantom.AttackPhase.CIRCLE;
         }

      }

      // $FF: synthetic method
      AISweepAttack(Object var2) {
         this();
      }
   }

   class AIOrbitPoint extends EntityPhantom.AIMove {
      private float field_203150_c;
      private float field_203151_d;
      private float field_203152_e;
      private float field_203153_f;

      private AIOrbitPoint() {
         super();
      }

      public boolean func_75250_a() {
         return EntityPhantom.this.func_70638_az() == null || EntityPhantom.this.field_203038_bx == EntityPhantom.AttackPhase.CIRCLE;
      }

      public void func_75249_e() {
         this.field_203151_d = 5.0F + EntityPhantom.this.field_70146_Z.nextFloat() * 10.0F;
         this.field_203152_e = -4.0F + EntityPhantom.this.field_70146_Z.nextFloat() * 9.0F;
         this.field_203153_f = EntityPhantom.this.field_70146_Z.nextBoolean() ? 1.0F : -1.0F;
         this.func_203148_i();
      }

      public void func_75246_d() {
         if (EntityPhantom.this.field_70146_Z.nextInt(350) == 0) {
            this.field_203152_e = -4.0F + EntityPhantom.this.field_70146_Z.nextFloat() * 9.0F;
         }

         if (EntityPhantom.this.field_70146_Z.nextInt(250) == 0) {
            ++this.field_203151_d;
            if (this.field_203151_d > 15.0F) {
               this.field_203151_d = 5.0F;
               this.field_203153_f = -this.field_203153_f;
            }
         }

         if (EntityPhantom.this.field_70146_Z.nextInt(450) == 0) {
            this.field_203150_c = EntityPhantom.this.field_70146_Z.nextFloat() * 2.0F * 3.1415927F;
            this.func_203148_i();
         }

         if (this.func_203146_f()) {
            this.func_203148_i();
         }

         if (EntityPhantom.this.field_203036_b.field_72448_b < EntityPhantom.this.field_70163_u && !EntityPhantom.this.field_70170_p.func_175623_d((new BlockPos(EntityPhantom.this)).func_177979_c(1))) {
            this.field_203152_e = Math.max(1.0F, this.field_203152_e);
            this.func_203148_i();
         }

         if (EntityPhantom.this.field_203036_b.field_72448_b > EntityPhantom.this.field_70163_u && !EntityPhantom.this.field_70170_p.func_175623_d((new BlockPos(EntityPhantom.this)).func_177981_b(1))) {
            this.field_203152_e = Math.min(-1.0F, this.field_203152_e);
            this.func_203148_i();
         }

      }

      private void func_203148_i() {
         if (BlockPos.field_177992_a.equals(EntityPhantom.this.field_203037_c)) {
            EntityPhantom.this.field_203037_c = new BlockPos(EntityPhantom.this);
         }

         this.field_203150_c += this.field_203153_f * 15.0F * 0.017453292F;
         EntityPhantom.this.field_203036_b = (new Vec3d(EntityPhantom.this.field_203037_c)).func_72441_c((double)(this.field_203151_d * MathHelper.func_76134_b(this.field_203150_c)), (double)(-4.0F + this.field_203152_e), (double)(this.field_203151_d * MathHelper.func_76126_a(this.field_203150_c)));
      }

      // $FF: synthetic method
      AIOrbitPoint(Object var2) {
         this();
      }
   }

   abstract class AIMove extends EntityAIBase {
      public AIMove() {
         super();
         this.func_75248_a(1);
      }

      protected boolean func_203146_f() {
         return EntityPhantom.this.field_203036_b.func_186679_c(EntityPhantom.this.field_70165_t, EntityPhantom.this.field_70163_u, EntityPhantom.this.field_70161_v) < 4.0D;
      }
   }

   class LookHelper extends EntityLookHelper {
      public LookHelper(EntityLiving var2) {
         super(var2);
      }

      public void func_75649_a() {
      }
   }

   class BodyHelper extends EntityBodyHelper {
      public BodyHelper(EntityLivingBase var2) {
         super(var2);
      }

      public void func_75664_a() {
         EntityPhantom.this.field_70759_as = EntityPhantom.this.field_70761_aq;
         EntityPhantom.this.field_70761_aq = EntityPhantom.this.field_70177_z;
      }
   }

   class MoveHelper extends EntityMoveHelper {
      private float field_203105_j = 0.1F;

      public MoveHelper(EntityLiving var2) {
         super(var2);
      }

      public void func_75641_c() {
         EntityPhantom var10000;
         if (EntityPhantom.this.field_70123_F) {
            var10000 = EntityPhantom.this;
            var10000.field_70177_z += 180.0F;
            this.field_203105_j = 0.1F;
         }

         float var1 = (float)(EntityPhantom.this.field_203036_b.field_72450_a - EntityPhantom.this.field_70165_t);
         float var2 = (float)(EntityPhantom.this.field_203036_b.field_72448_b - EntityPhantom.this.field_70163_u);
         float var3 = (float)(EntityPhantom.this.field_203036_b.field_72449_c - EntityPhantom.this.field_70161_v);
         double var4 = (double)MathHelper.func_76129_c(var1 * var1 + var3 * var3);
         double var6 = 1.0D - (double)MathHelper.func_76135_e(var2 * 0.7F) / var4;
         var1 = (float)((double)var1 * var6);
         var3 = (float)((double)var3 * var6);
         var4 = (double)MathHelper.func_76129_c(var1 * var1 + var3 * var3);
         double var8 = (double)MathHelper.func_76129_c(var1 * var1 + var3 * var3 + var2 * var2);
         float var10 = EntityPhantom.this.field_70177_z;
         float var11 = (float)MathHelper.func_181159_b((double)var3, (double)var1);
         float var12 = MathHelper.func_76142_g(EntityPhantom.this.field_70177_z + 90.0F);
         float var13 = MathHelper.func_76142_g(var11 * 57.295776F);
         EntityPhantom.this.field_70177_z = MathHelper.func_203303_c(var12, var13, 4.0F) - 90.0F;
         EntityPhantom.this.field_70761_aq = EntityPhantom.this.field_70177_z;
         if (MathHelper.func_203301_d(var10, EntityPhantom.this.field_70177_z) < 3.0F) {
            this.field_203105_j = MathHelper.func_203300_b(this.field_203105_j, 1.8F, 0.005F * (1.8F / this.field_203105_j));
         } else {
            this.field_203105_j = MathHelper.func_203300_b(this.field_203105_j, 0.2F, 0.025F);
         }

         float var14 = (float)(-(MathHelper.func_181159_b((double)(-var2), var4) * 57.2957763671875D));
         EntityPhantom.this.field_70125_A = var14;
         float var15 = EntityPhantom.this.field_70177_z + 90.0F;
         double var16 = (double)(this.field_203105_j * MathHelper.func_76134_b(var15 * 0.017453292F)) * Math.abs((double)var1 / var8);
         double var18 = (double)(this.field_203105_j * MathHelper.func_76126_a(var15 * 0.017453292F)) * Math.abs((double)var3 / var8);
         double var20 = (double)(this.field_203105_j * MathHelper.func_76126_a(var14 * 0.017453292F)) * Math.abs((double)var2 / var8);
         var10000 = EntityPhantom.this;
         var10000.field_70159_w += (var16 - EntityPhantom.this.field_70159_w) * 0.2D;
         var10000 = EntityPhantom.this;
         var10000.field_70181_x += (var20 - EntityPhantom.this.field_70181_x) * 0.2D;
         var10000 = EntityPhantom.this;
         var10000.field_70179_y += (var18 - EntityPhantom.this.field_70179_y) * 0.2D;
      }
   }

   static enum AttackPhase {
      CIRCLE,
      SWOOP;

      private AttackPhase() {
      }
   }
}
