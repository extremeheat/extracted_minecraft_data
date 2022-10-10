package net.minecraft.entity.monster;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFleeSun;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIRestrictSun;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityTurtle;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public abstract class AbstractSkeleton extends EntityMob implements IRangedAttackMob {
   private static final DataParameter<Boolean> field_184728_b;
   private final EntityAIAttackRangedBow<AbstractSkeleton> field_85037_d = new EntityAIAttackRangedBow(this, 1.0D, 20, 15.0F);
   private final EntityAIAttackMelee field_85038_e = new EntityAIAttackMelee(this, 1.2D, false) {
      public void func_75251_c() {
         super.func_75251_c();
         AbstractSkeleton.this.func_184724_a(false);
      }

      public void func_75249_e() {
         super.func_75249_e();
         AbstractSkeleton.this.func_184724_a(true);
      }
   };

   protected AbstractSkeleton(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.func_70105_a(0.6F, 1.99F);
      this.func_85036_m();
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(2, new EntityAIRestrictSun(this));
      this.field_70714_bg.func_75776_a(3, new EntityAIFleeSun(this, 1.0D));
      this.field_70714_bg.func_75776_a(3, new EntityAIAvoidEntity(this, EntityWolf.class, 6.0F, 1.0D, 1.2D));
      this.field_70714_bg.func_75776_a(5, new EntityAIWanderAvoidWater(this, 1.0D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(6, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityIronGolem.class, true));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityTurtle.class, 10, true, false, EntityTurtle.field_203029_bx));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184728_b, false);
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      this.func_184185_a(this.func_190727_o(), 0.15F, 1.0F);
   }

   abstract SoundEvent func_190727_o();

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.UNDEAD;
   }

   public void func_70636_d() {
      boolean var1 = this.func_204609_dp();
      if (var1) {
         ItemStack var2 = this.func_184582_a(EntityEquipmentSlot.HEAD);
         if (!var2.func_190926_b()) {
            if (var2.func_77984_f()) {
               var2.func_196085_b(var2.func_77952_i() + this.field_70146_Z.nextInt(2));
               if (var2.func_77952_i() >= var2.func_77958_k()) {
                  this.func_70669_a(var2);
                  this.func_184201_a(EntityEquipmentSlot.HEAD, ItemStack.field_190927_a);
               }
            }

            var1 = false;
         }

         if (var1) {
            this.func_70015_d(8);
         }
      }

      super.func_70636_d();
   }

   public void func_70098_U() {
      super.func_70098_U();
      if (this.func_184187_bx() instanceof EntityCreature) {
         EntityCreature var1 = (EntityCreature)this.func_184187_bx();
         this.field_70761_aq = var1.field_70761_aq;
      }

   }

   protected void func_180481_a(DifficultyInstance var1) {
      super.func_180481_a(var1);
      this.func_184201_a(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.field_151031_f));
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      var2 = super.func_204210_a(var1, var2, var3);
      this.func_180481_a(var1);
      this.func_180483_b(var1);
      this.func_85036_m();
      this.func_98053_h(this.field_70146_Z.nextFloat() < 0.55F * var1.func_180170_c());
      if (this.func_184582_a(EntityEquipmentSlot.HEAD).func_190926_b()) {
         LocalDate var4 = LocalDate.now();
         int var5 = var4.get(ChronoField.DAY_OF_MONTH);
         int var6 = var4.get(ChronoField.MONTH_OF_YEAR);
         if (var6 == 10 && var5 == 31 && this.field_70146_Z.nextFloat() < 0.25F) {
            this.func_184201_a(EntityEquipmentSlot.HEAD, new ItemStack(this.field_70146_Z.nextFloat() < 0.1F ? Blocks.field_196628_cT : Blocks.field_196625_cS));
            this.field_184655_bs[EntityEquipmentSlot.HEAD.func_188454_b()] = 0.0F;
         }
      }

      return var2;
   }

   public void func_85036_m() {
      if (this.field_70170_p != null && !this.field_70170_p.field_72995_K) {
         this.field_70714_bg.func_85156_a(this.field_85038_e);
         this.field_70714_bg.func_85156_a(this.field_85037_d);
         ItemStack var1 = this.func_184614_ca();
         if (var1.func_77973_b() == Items.field_151031_f) {
            byte var2 = 20;
            if (this.field_70170_p.func_175659_aa() != EnumDifficulty.HARD) {
               var2 = 40;
            }

            this.field_85037_d.func_189428_b(var2);
            this.field_70714_bg.func_75776_a(4, this.field_85037_d);
         } else {
            this.field_70714_bg.func_75776_a(4, this.field_85038_e);
         }

      }
   }

   public void func_82196_d(EntityLivingBase var1, float var2) {
      EntityArrow var3 = this.func_190726_a(var2);
      double var4 = var1.field_70165_t - this.field_70165_t;
      double var6 = var1.func_174813_aQ().field_72338_b + (double)(var1.field_70131_O / 3.0F) - var3.field_70163_u;
      double var8 = var1.field_70161_v - this.field_70161_v;
      double var10 = (double)MathHelper.func_76133_a(var4 * var4 + var8 * var8);
      var3.func_70186_c(var4, var6 + var10 * 0.20000000298023224D, var8, 1.6F, (float)(14 - this.field_70170_p.func_175659_aa().func_151525_a() * 4));
      this.func_184185_a(SoundEvents.field_187866_fi, 1.0F, 1.0F / (this.func_70681_au().nextFloat() * 0.4F + 0.8F));
      this.field_70170_p.func_72838_d(var3);
   }

   protected EntityArrow func_190726_a(float var1) {
      EntityTippedArrow var2 = new EntityTippedArrow(this.field_70170_p, this);
      var2.func_190547_a(this, var1);
      return var2;
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_85036_m();
   }

   public void func_184201_a(EntityEquipmentSlot var1, ItemStack var2) {
      super.func_184201_a(var1, var2);
      if (!this.field_70170_p.field_72995_K && var1 == EntityEquipmentSlot.MAINHAND) {
         this.func_85036_m();
      }

   }

   public float func_70047_e() {
      return 1.74F;
   }

   public double func_70033_W() {
      return -0.6D;
   }

   public boolean func_184725_db() {
      return (Boolean)this.field_70180_af.func_187225_a(field_184728_b);
   }

   public void func_184724_a(boolean var1) {
      this.field_70180_af.func_187227_b(field_184728_b, var1);
   }

   static {
      field_184728_b = EntityDataManager.func_187226_a(AbstractSkeleton.class, DataSerializers.field_187198_h);
   }
}
