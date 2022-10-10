package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySpider extends EntityMob {
   private static final DataParameter<Byte> field_184729_a;

   protected EntitySpider(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.func_70105_a(1.4F, 0.9F);
   }

   public EntitySpider(World var1) {
      this(EntityType.field_200748_an, var1);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(3, new EntityAILeapAtTarget(this, 0.4F));
      this.field_70714_bg.func_75776_a(4, new EntitySpider.AISpiderAttack(this));
      this.field_70714_bg.func_75776_a(5, new EntityAIWanderAvoidWater(this, 0.8D));
      this.field_70714_bg.func_75776_a(6, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(6, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntitySpider.AISpiderTarget(this, EntityPlayer.class));
      this.field_70715_bh.func_75776_a(3, new EntitySpider.AISpiderTarget(this, EntityIronGolem.class));
   }

   public double func_70042_X() {
      return (double)(this.field_70131_O * 0.5F);
   }

   protected PathNavigate func_175447_b(World var1) {
      return new PathNavigateClimber(this, var1);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184729_a, (byte)0);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K) {
         this.func_70839_e(this.field_70123_F);
      }

   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(16.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187817_fK;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187821_fM;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187819_fL;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      this.func_184185_a(SoundEvents.field_187823_fN, 0.15F, 1.0F);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186435_q;
   }

   public boolean func_70617_f_() {
      return this.func_70841_p();
   }

   public void func_70110_aj() {
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.ARTHROPOD;
   }

   public boolean func_70687_e(PotionEffect var1) {
      return var1.func_188419_a() == MobEffects.field_76436_u ? false : super.func_70687_e(var1);
   }

   public boolean func_70841_p() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184729_a) & 1) != 0;
   }

   public void func_70839_e(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184729_a);
      if (var1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 &= -2;
      }

      this.field_70180_af.func_187227_b(field_184729_a, var2);
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      Object var5 = super.func_204210_a(var1, var2, var3);
      if (this.field_70170_p.field_73012_v.nextInt(100) == 0) {
         EntitySkeleton var4 = new EntitySkeleton(this.field_70170_p);
         var4.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, 0.0F);
         var4.func_204210_a(var1, (IEntityLivingData)null, (NBTTagCompound)null);
         this.field_70170_p.func_72838_d(var4);
         var4.func_184220_m(this);
      }

      if (var5 == null) {
         var5 = new EntitySpider.GroupData();
         if (this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD && this.field_70170_p.field_73012_v.nextFloat() < 0.1F * var1.func_180170_c()) {
            ((EntitySpider.GroupData)var5).func_111104_a(this.field_70170_p.field_73012_v);
         }
      }

      if (var5 instanceof EntitySpider.GroupData) {
         Potion var6 = ((EntitySpider.GroupData)var5).field_188478_a;
         if (var6 != null) {
            this.func_195064_c(new PotionEffect(var6, 2147483647));
         }
      }

      return (IEntityLivingData)var5;
   }

   public float func_70047_e() {
      return 0.65F;
   }

   static {
      field_184729_a = EntityDataManager.func_187226_a(EntitySpider.class, DataSerializers.field_187191_a);
   }

   static class AISpiderTarget<T extends EntityLivingBase> extends EntityAINearestAttackableTarget<T> {
      public AISpiderTarget(EntitySpider var1, Class<T> var2) {
         super(var1, var2, true);
      }

      public boolean func_75250_a() {
         float var1 = this.field_75299_d.func_70013_c();
         return var1 >= 0.5F ? false : super.func_75250_a();
      }
   }

   static class AISpiderAttack extends EntityAIAttackMelee {
      public AISpiderAttack(EntitySpider var1) {
         super(var1, 1.0D, true);
      }

      public boolean func_75253_b() {
         float var1 = this.field_75441_b.func_70013_c();
         if (var1 >= 0.5F && this.field_75441_b.func_70681_au().nextInt(100) == 0) {
            this.field_75441_b.func_70624_b((EntityLivingBase)null);
            return false;
         } else {
            return super.func_75253_b();
         }
      }

      protected double func_179512_a(EntityLivingBase var1) {
         return (double)(4.0F + var1.field_70130_N);
      }
   }

   public static class GroupData implements IEntityLivingData {
      public Potion field_188478_a;

      public GroupData() {
         super();
      }

      public void func_111104_a(Random var1) {
         int var2 = var1.nextInt(5);
         if (var2 <= 1) {
            this.field_188478_a = MobEffects.field_76424_c;
         } else if (var2 <= 2) {
            this.field_188478_a = MobEffects.field_76420_g;
         } else if (var2 <= 3) {
            this.field_188478_a = MobEffects.field_76428_l;
         } else if (var2 <= 4) {
            this.field_188478_a = MobEffects.field_76441_p;
         }

      }
   }
}
