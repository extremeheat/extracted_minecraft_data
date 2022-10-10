package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityVex extends EntityMob {
   protected static final DataParameter<Byte> field_190664_a;
   private EntityLiving field_190665_b;
   @Nullable
   private BlockPos field_190666_c;
   private boolean field_190667_bw;
   private int field_190668_bx;

   public EntityVex(World var1) {
      super(EntityType.field_200755_au, var1);
      this.field_70178_ae = true;
      this.field_70765_h = new EntityVex.AIMoveControl(this);
      this.func_70105_a(0.4F, 0.8F);
      this.field_70728_aV = 3;
   }

   public void func_70091_d(MoverType var1, double var2, double var4, double var6) {
      super.func_70091_d(var1, var2, var4, var6);
      this.func_145775_I();
   }

   public void func_70071_h_() {
      this.field_70145_X = true;
      super.func_70071_h_();
      this.field_70145_X = false;
      this.func_189654_d(true);
      if (this.field_190667_bw && --this.field_190668_bx <= 0) {
         this.field_190668_bx = 20;
         this.func_70097_a(DamageSource.field_76366_f, 1.0F);
      }

   }

   protected void func_184651_r() {
      super.func_184651_r();
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(4, new EntityVex.AIChargeAttack());
      this.field_70714_bg.func_75776_a(8, new EntityVex.AIMoveRandom());
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
      this.field_70714_bg.func_75776_a(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[]{EntityVex.class}));
      this.field_70715_bh.func_75776_a(2, new EntityVex.AICopyOwnerTarget(this));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(14.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(4.0D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_190664_a, (byte)0);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_74764_b("BoundX")) {
         this.field_190666_c = new BlockPos(var1.func_74762_e("BoundX"), var1.func_74762_e("BoundY"), var1.func_74762_e("BoundZ"));
      }

      if (var1.func_74764_b("LifeTicks")) {
         this.func_190653_a(var1.func_74762_e("LifeTicks"));
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      if (this.field_190666_c != null) {
         var1.func_74768_a("BoundX", this.field_190666_c.func_177958_n());
         var1.func_74768_a("BoundY", this.field_190666_c.func_177956_o());
         var1.func_74768_a("BoundZ", this.field_190666_c.func_177952_p());
      }

      if (this.field_190667_bw) {
         var1.func_74768_a("LifeTicks", this.field_190668_bx);
      }

   }

   public EntityLiving func_190645_o() {
      return this.field_190665_b;
   }

   @Nullable
   public BlockPos func_190646_di() {
      return this.field_190666_c;
   }

   public void func_190651_g(@Nullable BlockPos var1) {
      this.field_190666_c = var1;
   }

   private boolean func_190656_b(int var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_190664_a);
      return (var2 & var1) != 0;
   }

   private void func_190660_a(int var1, boolean var2) {
      byte var3 = (Byte)this.field_70180_af.func_187225_a(field_190664_a);
      int var4;
      if (var2) {
         var4 = var3 | var1;
      } else {
         var4 = var3 & ~var1;
      }

      this.field_70180_af.func_187227_b(field_190664_a, (byte)(var4 & 255));
   }

   public boolean func_190647_dj() {
      return this.func_190656_b(1);
   }

   public void func_190648_a(boolean var1) {
      this.func_190660_a(1, var1);
   }

   public void func_190658_a(EntityLiving var1) {
      this.field_190665_b = var1;
   }

   public void func_190653_a(int var1) {
      this.field_190667_bw = true;
      this.field_190668_bx = var1;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_191264_hc;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_191266_he;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_191267_hf;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_191188_ax;
   }

   public int func_70070_b() {
      return 15728880;
   }

   public float func_70013_c() {
      return 1.0F;
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      this.func_180481_a(var1);
      this.func_180483_b(var1);
      return super.func_204210_a(var1, var2, var3);
   }

   protected void func_180481_a(DifficultyInstance var1) {
      this.func_184201_a(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.field_151040_l));
      this.func_184642_a(EntityEquipmentSlot.MAINHAND, 0.0F);
   }

   static {
      field_190664_a = EntityDataManager.func_187226_a(EntityVex.class, DataSerializers.field_187191_a);
   }

   class AICopyOwnerTarget extends EntityAITarget {
      public AICopyOwnerTarget(EntityCreature var2) {
         super(var2, false);
      }

      public boolean func_75250_a() {
         return EntityVex.this.field_190665_b != null && EntityVex.this.field_190665_b.func_70638_az() != null && this.func_75296_a(EntityVex.this.field_190665_b.func_70638_az(), false);
      }

      public void func_75249_e() {
         EntityVex.this.func_70624_b(EntityVex.this.field_190665_b.func_70638_az());
         super.func_75249_e();
      }
   }

   class AIMoveRandom extends EntityAIBase {
      public AIMoveRandom() {
         super();
         this.func_75248_a(1);
      }

      public boolean func_75250_a() {
         return !EntityVex.this.func_70605_aq().func_75640_a() && EntityVex.this.field_70146_Z.nextInt(7) == 0;
      }

      public boolean func_75253_b() {
         return false;
      }

      public void func_75246_d() {
         BlockPos var1 = EntityVex.this.func_190646_di();
         if (var1 == null) {
            var1 = new BlockPos(EntityVex.this);
         }

         for(int var2 = 0; var2 < 3; ++var2) {
            BlockPos var3 = var1.func_177982_a(EntityVex.this.field_70146_Z.nextInt(15) - 7, EntityVex.this.field_70146_Z.nextInt(11) - 5, EntityVex.this.field_70146_Z.nextInt(15) - 7);
            if (EntityVex.this.field_70170_p.func_175623_d(var3)) {
               EntityVex.this.field_70765_h.func_75642_a((double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 0.5D, (double)var3.func_177952_p() + 0.5D, 0.25D);
               if (EntityVex.this.func_70638_az() == null) {
                  EntityVex.this.func_70671_ap().func_75650_a((double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 0.5D, (double)var3.func_177952_p() + 0.5D, 180.0F, 20.0F);
               }
               break;
            }
         }

      }
   }

   class AIChargeAttack extends EntityAIBase {
      public AIChargeAttack() {
         super();
         this.func_75248_a(1);
      }

      public boolean func_75250_a() {
         if (EntityVex.this.func_70638_az() != null && !EntityVex.this.func_70605_aq().func_75640_a() && EntityVex.this.field_70146_Z.nextInt(7) == 0) {
            return EntityVex.this.func_70068_e(EntityVex.this.func_70638_az()) > 4.0D;
         } else {
            return false;
         }
      }

      public boolean func_75253_b() {
         return EntityVex.this.func_70605_aq().func_75640_a() && EntityVex.this.func_190647_dj() && EntityVex.this.func_70638_az() != null && EntityVex.this.func_70638_az().func_70089_S();
      }

      public void func_75249_e() {
         EntityLivingBase var1 = EntityVex.this.func_70638_az();
         Vec3d var2 = var1.func_174824_e(1.0F);
         EntityVex.this.field_70765_h.func_75642_a(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, 1.0D);
         EntityVex.this.func_190648_a(true);
         EntityVex.this.func_184185_a(SoundEvents.field_191265_hd, 1.0F, 1.0F);
      }

      public void func_75251_c() {
         EntityVex.this.func_190648_a(false);
      }

      public void func_75246_d() {
         EntityLivingBase var1 = EntityVex.this.func_70638_az();
         if (EntityVex.this.func_174813_aQ().func_72326_a(var1.func_174813_aQ())) {
            EntityVex.this.func_70652_k(var1);
            EntityVex.this.func_190648_a(false);
         } else {
            double var2 = EntityVex.this.func_70068_e(var1);
            if (var2 < 9.0D) {
               Vec3d var4 = var1.func_174824_e(1.0F);
               EntityVex.this.field_70765_h.func_75642_a(var4.field_72450_a, var4.field_72448_b, var4.field_72449_c, 1.0D);
            }
         }

      }
   }

   class AIMoveControl extends EntityMoveHelper {
      public AIMoveControl(EntityVex var2) {
         super(var2);
      }

      public void func_75641_c() {
         if (this.field_188491_h == EntityMoveHelper.Action.MOVE_TO) {
            double var1 = this.field_75646_b - EntityVex.this.field_70165_t;
            double var3 = this.field_75647_c - EntityVex.this.field_70163_u;
            double var5 = this.field_75644_d - EntityVex.this.field_70161_v;
            double var7 = var1 * var1 + var3 * var3 + var5 * var5;
            var7 = (double)MathHelper.func_76133_a(var7);
            EntityVex var10000;
            if (var7 < EntityVex.this.func_174813_aQ().func_72320_b()) {
               this.field_188491_h = EntityMoveHelper.Action.WAIT;
               var10000 = EntityVex.this;
               var10000.field_70159_w *= 0.5D;
               var10000 = EntityVex.this;
               var10000.field_70181_x *= 0.5D;
               var10000 = EntityVex.this;
               var10000.field_70179_y *= 0.5D;
            } else {
               var10000 = EntityVex.this;
               var10000.field_70159_w += var1 / var7 * 0.05D * this.field_75645_e;
               var10000 = EntityVex.this;
               var10000.field_70181_x += var3 / var7 * 0.05D * this.field_75645_e;
               var10000 = EntityVex.this;
               var10000.field_70179_y += var5 / var7 * 0.05D * this.field_75645_e;
               if (EntityVex.this.func_70638_az() == null) {
                  EntityVex.this.field_70177_z = -((float)MathHelper.func_181159_b(EntityVex.this.field_70159_w, EntityVex.this.field_70179_y)) * 57.295776F;
                  EntityVex.this.field_70761_aq = EntityVex.this.field_70177_z;
               } else {
                  double var9 = EntityVex.this.func_70638_az().field_70165_t - EntityVex.this.field_70165_t;
                  double var11 = EntityVex.this.func_70638_az().field_70161_v - EntityVex.this.field_70161_v;
                  EntityVex.this.field_70177_z = -((float)MathHelper.func_181159_b(var9, var11)) * 57.295776F;
                  EntityVex.this.field_70761_aq = EntityVex.this.field_70177_z;
               }
            }

         }
      }
   }
}
