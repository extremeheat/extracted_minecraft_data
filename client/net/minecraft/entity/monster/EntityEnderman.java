package net.minecraft.entity.monster;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityEnderman extends EntityMob {
   private static final UUID field_110192_bp = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   private static final AttributeModifier field_110193_bq;
   private static final DataParameter<Optional<IBlockState>> field_184718_bv;
   private static final DataParameter<Boolean> field_184719_bw;
   private int field_184720_bx;
   private int field_184721_by;

   public EntityEnderman(World var1) {
      super(EntityType.field_200803_q, var1);
      this.func_70105_a(0.6F, 2.9F);
      this.field_70138_W = 1.0F;
      this.func_184644_a(PathNodeType.WATER, -1.0F);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIAttackMelee(this, 1.0D, false));
      this.field_70714_bg.func_75776_a(7, new EntityAIWanderAvoidWater(this, 1.0D, 0.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70714_bg.func_75776_a(10, new EntityEnderman.AIPlaceBlock(this));
      this.field_70714_bg.func_75776_a(11, new EntityEnderman.AITakeBlock(this));
      this.field_70715_bh.func_75776_a(1, new EntityEnderman.AIFindPlayer(this));
      this.field_70715_bh.func_75776_a(2, new EntityAIHurtByTarget(this, false, new Class[0]));
      this.field_70715_bh.func_75776_a(3, new EntityAINearestAttackableTarget(this, EntityEndermite.class, 10, true, false, EntityEndermite::func_175495_n));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(40.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(7.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111265_b).func_111128_a(64.0D);
   }

   public void func_70624_b(@Nullable EntityLivingBase var1) {
      super.func_70624_b(var1);
      IAttributeInstance var2 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
      if (var1 == null) {
         this.field_184721_by = 0;
         this.field_70180_af.func_187227_b(field_184719_bw, false);
         var2.func_111124_b(field_110193_bq);
      } else {
         this.field_184721_by = this.field_70173_aa;
         this.field_70180_af.func_187227_b(field_184719_bw, true);
         if (!var2.func_180374_a(field_110193_bq)) {
            var2.func_111121_a(field_110193_bq);
         }
      }

   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184718_bv, Optional.empty());
      this.field_70180_af.func_187214_a(field_184719_bw, false);
   }

   public void func_184716_o() {
      if (this.field_70173_aa >= this.field_184720_bx + 400) {
         this.field_184720_bx = this.field_70173_aa;
         if (!this.func_174814_R()) {
            this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v, SoundEvents.field_187533_aW, this.func_184176_by(), 2.5F, 1.0F, false);
         }
      }

   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_184719_bw.equals(var1) && this.func_70823_r() && this.field_70170_p.field_72995_K) {
         this.func_184716_o();
      }

      super.func_184206_a(var1);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      IBlockState var2 = this.func_195405_dq();
      if (var2 != null) {
         var1.func_74782_a("carriedBlockState", NBTUtil.func_190009_a(var2));
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      IBlockState var2 = null;
      if (var1.func_150297_b("carriedBlockState", 10)) {
         var2 = NBTUtil.func_190008_d(var1.func_74775_l("carriedBlockState"));
         if (var2.func_196958_f()) {
            var2 = null;
         }
      }

      this.func_195406_b(var2);
   }

   private boolean func_70821_d(EntityPlayer var1) {
      ItemStack var2 = (ItemStack)var1.field_71071_by.field_70460_b.get(3);
      if (var2.func_77973_b() == Blocks.field_196625_cS.func_199767_j()) {
         return false;
      } else {
         Vec3d var3 = var1.func_70676_i(1.0F).func_72432_b();
         Vec3d var4 = new Vec3d(this.field_70165_t - var1.field_70165_t, this.func_174813_aQ().field_72338_b + (double)this.func_70047_e() - (var1.field_70163_u + (double)var1.func_70047_e()), this.field_70161_v - var1.field_70161_v);
         double var5 = var4.func_72433_c();
         var4 = var4.func_72432_b();
         double var7 = var3.func_72430_b(var4);
         return var7 > 1.0D - 0.025D / var5 ? var1.func_70685_l(this) : false;
      }
   }

   public float func_70047_e() {
      return 2.55F;
   }

   public void func_70636_d() {
      if (this.field_70170_p.field_72995_K) {
         for(int var1 = 0; var1 < 2; ++var1) {
            this.field_70170_p.func_195594_a(Particles.field_197599_J, this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, this.field_70163_u + this.field_70146_Z.nextDouble() * (double)this.field_70131_O - 0.25D, this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * (double)this.field_70130_N, (this.field_70146_Z.nextDouble() - 0.5D) * 2.0D, -this.field_70146_Z.nextDouble(), (this.field_70146_Z.nextDouble() - 0.5D) * 2.0D);
         }
      }

      this.field_70703_bu = false;
      super.func_70636_d();
   }

   protected void func_70619_bc() {
      if (this.func_203008_ap()) {
         this.func_70097_a(DamageSource.field_76369_e, 1.0F);
      }

      if (this.field_70170_p.func_72935_r() && this.field_70173_aa >= this.field_184721_by + 600) {
         float var1 = this.func_70013_c();
         if (var1 > 0.5F && this.field_70170_p.func_175678_i(new BlockPos(this)) && this.field_70146_Z.nextFloat() * 30.0F < (var1 - 0.4F) * 2.0F) {
            this.func_70624_b((EntityLivingBase)null);
            this.func_70820_n();
         }
      }

      super.func_70619_bc();
   }

   protected boolean func_70820_n() {
      double var1 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * 64.0D;
      double var3 = this.field_70163_u + (double)(this.field_70146_Z.nextInt(64) - 32);
      double var5 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * 64.0D;
      return this.func_70825_j(var1, var3, var5);
   }

   protected boolean func_70816_c(Entity var1) {
      Vec3d var2 = new Vec3d(this.field_70165_t - var1.field_70165_t, this.func_174813_aQ().field_72338_b + (double)(this.field_70131_O / 2.0F) - var1.field_70163_u + (double)var1.func_70047_e(), this.field_70161_v - var1.field_70161_v);
      var2 = var2.func_72432_b();
      double var3 = 16.0D;
      double var5 = this.field_70165_t + (this.field_70146_Z.nextDouble() - 0.5D) * 8.0D - var2.field_72450_a * 16.0D;
      double var7 = this.field_70163_u + (double)(this.field_70146_Z.nextInt(16) - 8) - var2.field_72448_b * 16.0D;
      double var9 = this.field_70161_v + (this.field_70146_Z.nextDouble() - 0.5D) * 8.0D - var2.field_72449_c * 16.0D;
      return this.func_70825_j(var5, var7, var9);
   }

   private boolean func_70825_j(double var1, double var3, double var5) {
      boolean var7 = this.func_184595_k(var1, var3, var5);
      if (var7) {
         this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70169_q, this.field_70167_r, this.field_70166_s, SoundEvents.field_187534_aX, this.func_184176_by(), 1.0F, 1.0F);
         this.func_184185_a(SoundEvents.field_187534_aX, 1.0F, 1.0F);
      }

      return var7;
   }

   protected SoundEvent func_184639_G() {
      return this.func_70823_r() ? SoundEvents.field_187532_aV : SoundEvents.field_187529_aS;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187531_aU;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187530_aT;
   }

   protected void func_82160_b(boolean var1, int var2) {
      super.func_82160_b(var1, var2);
      IBlockState var3 = this.func_195405_dq();
      if (var3 != null) {
         this.func_199703_a(var3.func_177230_c());
      }

   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186439_u;
   }

   public void func_195406_b(@Nullable IBlockState var1) {
      this.field_70180_af.func_187227_b(field_184718_bv, Optional.ofNullable(var1));
   }

   @Nullable
   public IBlockState func_195405_dq() {
      return (IBlockState)((Optional)this.field_70180_af.func_187225_a(field_184718_bv)).orElse((Object)null);
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (var1 instanceof EntityDamageSourceIndirect) {
         for(int var4 = 0; var4 < 64; ++var4) {
            if (this.func_70820_n()) {
               return true;
            }
         }

         return false;
      } else {
         boolean var3 = super.func_70097_a(var1, var2);
         if (var1.func_76363_c() && this.field_70146_Z.nextInt(10) != 0) {
            this.func_70820_n();
         }

         return var3;
      }
   }

   public boolean func_70823_r() {
      return (Boolean)this.field_70180_af.func_187225_a(field_184719_bw);
   }

   static {
      field_110193_bq = (new AttributeModifier(field_110192_bp, "Attacking speed boost", 0.15000000596046448D, 0)).func_111168_a(false);
      field_184718_bv = EntityDataManager.func_187226_a(EntityEnderman.class, DataSerializers.field_187197_g);
      field_184719_bw = EntityDataManager.func_187226_a(EntityEnderman.class, DataSerializers.field_187198_h);
   }

   static class AITakeBlock extends EntityAIBase {
      private final EntityEnderman field_179473_a;

      public AITakeBlock(EntityEnderman var1) {
         super();
         this.field_179473_a = var1;
      }

      public boolean func_75250_a() {
         if (this.field_179473_a.func_195405_dq() != null) {
            return false;
         } else if (!this.field_179473_a.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            return false;
         } else {
            return this.field_179473_a.func_70681_au().nextInt(20) == 0;
         }
      }

      public void func_75246_d() {
         Random var1 = this.field_179473_a.func_70681_au();
         World var2 = this.field_179473_a.field_70170_p;
         int var3 = MathHelper.func_76128_c(this.field_179473_a.field_70165_t - 2.0D + var1.nextDouble() * 4.0D);
         int var4 = MathHelper.func_76128_c(this.field_179473_a.field_70163_u + var1.nextDouble() * 3.0D);
         int var5 = MathHelper.func_76128_c(this.field_179473_a.field_70161_v - 2.0D + var1.nextDouble() * 4.0D);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         IBlockState var7 = var2.func_180495_p(var6);
         Block var8 = var7.func_177230_c();
         RayTraceResult var9 = var2.func_200259_a(new Vec3d((double)((float)MathHelper.func_76128_c(this.field_179473_a.field_70165_t) + 0.5F), (double)((float)var4 + 0.5F), (double)((float)MathHelper.func_76128_c(this.field_179473_a.field_70161_v) + 0.5F)), new Vec3d((double)((float)var3 + 0.5F), (double)((float)var4 + 0.5F), (double)((float)var5 + 0.5F)), RayTraceFluidMode.NEVER, true, false);
         boolean var10 = var9 != null && var9.func_178782_a().equals(var6);
         if (var8.func_203417_a(BlockTags.field_201151_l) && var10) {
            this.field_179473_a.func_195406_b(var7);
            var2.func_175698_g(var6);
         }

      }
   }

   static class AIPlaceBlock extends EntityAIBase {
      private final EntityEnderman field_179475_a;

      public AIPlaceBlock(EntityEnderman var1) {
         super();
         this.field_179475_a = var1;
      }

      public boolean func_75250_a() {
         if (this.field_179475_a.func_195405_dq() == null) {
            return false;
         } else if (!this.field_179475_a.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
            return false;
         } else {
            return this.field_179475_a.func_70681_au().nextInt(2000) == 0;
         }
      }

      public void func_75246_d() {
         Random var1 = this.field_179475_a.func_70681_au();
         World var2 = this.field_179475_a.field_70170_p;
         int var3 = MathHelper.func_76128_c(this.field_179475_a.field_70165_t - 1.0D + var1.nextDouble() * 2.0D);
         int var4 = MathHelper.func_76128_c(this.field_179475_a.field_70163_u + var1.nextDouble() * 2.0D);
         int var5 = MathHelper.func_76128_c(this.field_179475_a.field_70161_v - 1.0D + var1.nextDouble() * 2.0D);
         BlockPos var6 = new BlockPos(var3, var4, var5);
         IBlockState var7 = var2.func_180495_p(var6);
         IBlockState var8 = var2.func_180495_p(var6.func_177977_b());
         IBlockState var9 = this.field_179475_a.func_195405_dq();
         if (var9 != null && this.func_195924_a(var2, var6, var9, var7, var8)) {
            var2.func_180501_a(var6, var9, 3);
            this.field_179475_a.func_195406_b((IBlockState)null);
         }

      }

      private boolean func_195924_a(IWorldReaderBase var1, BlockPos var2, IBlockState var3, IBlockState var4, IBlockState var5) {
         return var4.func_196958_f() && !var5.func_196958_f() && var5.func_185917_h() && var3.func_196955_c(var1, var2);
      }
   }

   static class AIFindPlayer extends EntityAINearestAttackableTarget<EntityPlayer> {
      private final EntityEnderman field_179449_j;
      private EntityPlayer field_179448_g;
      private int field_179450_h;
      private int field_179451_i;

      public AIFindPlayer(EntityEnderman var1) {
         super(var1, EntityPlayer.class, false);
         this.field_179449_j = var1;
      }

      public boolean func_75250_a() {
         double var1 = this.func_111175_f();
         this.field_179448_g = this.field_179449_j.field_70170_p.func_184150_a(this.field_179449_j.field_70165_t, this.field_179449_j.field_70163_u, this.field_179449_j.field_70161_v, var1, var1, (Function)null, (var1x) -> {
            return var1x != null && this.field_179449_j.func_70821_d(var1x);
         });
         return this.field_179448_g != null;
      }

      public void func_75249_e() {
         this.field_179450_h = 5;
         this.field_179451_i = 0;
      }

      public void func_75251_c() {
         this.field_179448_g = null;
         super.func_75251_c();
      }

      public boolean func_75253_b() {
         if (this.field_179448_g != null) {
            if (!this.field_179449_j.func_70821_d(this.field_179448_g)) {
               return false;
            } else {
               this.field_179449_j.func_70625_a(this.field_179448_g, 10.0F, 10.0F);
               return true;
            }
         } else {
            return this.field_75309_a != null && ((EntityPlayer)this.field_75309_a).func_70089_S() ? true : super.func_75253_b();
         }
      }

      public void func_75246_d() {
         if (this.field_179448_g != null) {
            if (--this.field_179450_h <= 0) {
               this.field_75309_a = this.field_179448_g;
               this.field_179448_g = null;
               super.func_75249_e();
            }
         } else {
            if (this.field_75309_a != null) {
               if (this.field_179449_j.func_70821_d((EntityPlayer)this.field_75309_a)) {
                  if (((EntityPlayer)this.field_75309_a).func_70068_e(this.field_179449_j) < 16.0D) {
                     this.field_179449_j.func_70820_n();
                  }

                  this.field_179451_i = 0;
               } else if (((EntityPlayer)this.field_75309_a).func_70068_e(this.field_179449_j) > 256.0D && this.field_179451_i++ >= 30 && this.field_179449_j.func_70816_c(this.field_75309_a)) {
                  this.field_179451_i = 0;
               }
            }

            super.func_75246_d();
         }

      }
   }
}
