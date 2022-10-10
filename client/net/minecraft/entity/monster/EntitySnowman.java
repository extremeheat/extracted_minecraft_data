package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySnowman extends EntityGolem implements IRangedAttackMob {
   private static final DataParameter<Byte> field_184749_a;

   public EntitySnowman(World var1) {
      super(EntityType.field_200745_ak, var1);
      this.func_70105_a(0.7F, 1.9F);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(1, new EntityAIAttackRanged(this, 1.25D, 20, 10.0F));
      this.field_70714_bg.func_75776_a(2, new EntityAIWanderAvoidWater(this, 1.0D, 1.0000001E-5F));
      this.field_70714_bg.func_75776_a(3, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.field_70714_bg.func_75776_a(4, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAINearestAttackableTarget(this, EntityLiving.class, 10, true, false, IMob.field_82192_a));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(4.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.20000000298023224D);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184749_a, (byte)16);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74757_a("Pumpkin", this.func_184748_o());
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_74764_b("Pumpkin")) {
         this.func_184747_a(var1.func_74767_n("Pumpkin"));
      }

   }

   public void func_70636_d() {
      super.func_70636_d();
      if (!this.field_70170_p.field_72995_K) {
         int var1 = MathHelper.func_76128_c(this.field_70165_t);
         int var2 = MathHelper.func_76128_c(this.field_70163_u);
         int var3 = MathHelper.func_76128_c(this.field_70161_v);
         if (this.func_203008_ap()) {
            this.func_70097_a(DamageSource.field_76369_e, 1.0F);
         }

         if (this.field_70170_p.func_180494_b(new BlockPos(var1, 0, var3)).func_180626_a(new BlockPos(var1, var2, var3)) > 1.0F) {
            this.func_70097_a(DamageSource.field_76370_b, 1.0F);
         }

         if (!this.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            return;
         }

         IBlockState var4 = Blocks.field_150433_aE.func_176223_P();

         for(int var5 = 0; var5 < 4; ++var5) {
            var1 = MathHelper.func_76128_c(this.field_70165_t + (double)((float)(var5 % 2 * 2 - 1) * 0.25F));
            var2 = MathHelper.func_76128_c(this.field_70163_u);
            var3 = MathHelper.func_76128_c(this.field_70161_v + (double)((float)(var5 / 2 % 2 * 2 - 1) * 0.25F));
            BlockPos var6 = new BlockPos(var1, var2, var3);
            if (this.field_70170_p.func_180495_p(var6).func_196958_f() && this.field_70170_p.func_180494_b(var6).func_180626_a(var6) < 0.8F && var4.func_196955_c(this.field_70170_p, var6)) {
               this.field_70170_p.func_175656_a(var6, var4);
            }
         }
      }

   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186444_z;
   }

   public void func_82196_d(EntityLivingBase var1, float var2) {
      EntitySnowball var3 = new EntitySnowball(this.field_70170_p, this);
      double var4 = var1.field_70163_u + (double)var1.func_70047_e() - 1.100000023841858D;
      double var6 = var1.field_70165_t - this.field_70165_t;
      double var8 = var4 - var3.field_70163_u;
      double var10 = var1.field_70161_v - this.field_70161_v;
      float var12 = MathHelper.func_76133_a(var6 * var6 + var10 * var10) * 0.2F;
      var3.func_70186_c(var6, var8 + (double)var12, var10, 1.6F, 12.0F);
      this.func_184185_a(SoundEvents.field_187805_fE, 1.0F, 1.0F / (this.func_70681_au().nextFloat() * 0.4F + 0.8F));
      this.field_70170_p.func_72838_d(var3);
   }

   public float func_70047_e() {
      return 1.7F;
   }

   protected boolean func_184645_a(EntityPlayer var1, EnumHand var2) {
      ItemStack var3 = var1.func_184586_b(var2);
      if (var3.func_77973_b() == Items.field_151097_aZ && this.func_184748_o() && !this.field_70170_p.field_72995_K) {
         this.func_184747_a(false);
         var3.func_77972_a(1, var1);
      }

      return super.func_184645_a(var1, var2);
   }

   public boolean func_184748_o() {
      return ((Byte)this.field_70180_af.func_187225_a(field_184749_a) & 16) != 0;
   }

   public void func_184747_a(boolean var1) {
      byte var2 = (Byte)this.field_70180_af.func_187225_a(field_184749_a);
      if (var1) {
         this.field_70180_af.func_187227_b(field_184749_a, (byte)(var2 | 16));
      } else {
         this.field_70180_af.func_187227_b(field_184749_a, (byte)(var2 & -17));
      }

   }

   @Nullable
   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187799_fB;
   }

   @Nullable
   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187803_fD;
   }

   @Nullable
   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187801_fC;
   }

   public void func_184724_a(boolean var1) {
   }

   static {
      field_184749_a = EntityDataManager.func_187226_a(EntitySnowman.class, DataSerializers.field_187191_a);
   }
}
