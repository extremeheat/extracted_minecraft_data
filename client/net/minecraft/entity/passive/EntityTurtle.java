package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTurtleEgg;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityTurtle extends EntityAnimal {
   private static final DataParameter<BlockPos> field_203030_by;
   private static final DataParameter<Boolean> field_203031_bz;
   private static final DataParameter<Boolean> field_203024_bB;
   private static final DataParameter<BlockPos> field_203025_bC;
   private static final DataParameter<Boolean> field_203026_bD;
   private static final DataParameter<Boolean> field_203027_bE;
   private int field_203028_bF;
   public static final Predicate<Entity> field_203029_bx;

   public EntityTurtle(World var1) {
      super(EntityType.field_203099_aq, var1);
      this.func_70105_a(1.2F, 0.4F);
      this.field_70765_h = new EntityTurtle.MoveHelper(this);
      this.field_175506_bl = Blocks.field_150354_m;
      this.field_70138_W = 1.0F;
   }

   public void func_203011_g(BlockPos var1) {
      this.field_70180_af.func_187227_b(field_203030_by, var1);
   }

   private BlockPos func_203018_dA() {
      return (BlockPos)this.field_70180_af.func_187225_a(field_203030_by);
   }

   private void func_203019_h(BlockPos var1) {
      this.field_70180_af.func_187227_b(field_203025_bC, var1);
   }

   private BlockPos func_203013_dB() {
      return (BlockPos)this.field_70180_af.func_187225_a(field_203025_bC);
   }

   public boolean func_203020_dx() {
      return (Boolean)this.field_70180_af.func_187225_a(field_203031_bz);
   }

   private void func_203017_r(boolean var1) {
      this.field_70180_af.func_187227_b(field_203031_bz, var1);
   }

   public boolean func_203023_dy() {
      return (Boolean)this.field_70180_af.func_187225_a(field_203024_bB);
   }

   private void func_203015_s(boolean var1) {
      this.field_203028_bF = var1 ? 1 : 0;
      this.field_70180_af.func_187227_b(field_203024_bB, var1);
   }

   private boolean func_203022_dF() {
      return (Boolean)this.field_70180_af.func_187225_a(field_203026_bD);
   }

   private void func_203012_t(boolean var1) {
      this.field_70180_af.func_187227_b(field_203026_bD, var1);
   }

   private boolean func_203014_dG() {
      return (Boolean)this.field_70180_af.func_187225_a(field_203027_bE);
   }

   private void func_203021_u(boolean var1) {
      this.field_70180_af.func_187227_b(field_203027_bE, var1);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_203030_by, BlockPos.field_177992_a);
      this.field_70180_af.func_187214_a(field_203031_bz, false);
      this.field_70180_af.func_187214_a(field_203025_bC, BlockPos.field_177992_a);
      this.field_70180_af.func_187214_a(field_203026_bD, false);
      this.field_70180_af.func_187214_a(field_203027_bE, false);
      this.field_70180_af.func_187214_a(field_203024_bB, false);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("HomePosX", this.func_203018_dA().func_177958_n());
      var1.func_74768_a("HomePosY", this.func_203018_dA().func_177956_o());
      var1.func_74768_a("HomePosZ", this.func_203018_dA().func_177952_p());
      var1.func_74757_a("HasEgg", this.func_203020_dx());
      var1.func_74768_a("TravelPosX", this.func_203013_dB().func_177958_n());
      var1.func_74768_a("TravelPosY", this.func_203013_dB().func_177956_o());
      var1.func_74768_a("TravelPosZ", this.func_203013_dB().func_177952_p());
   }

   public void func_70037_a(NBTTagCompound var1) {
      int var2 = var1.func_74762_e("HomePosX");
      int var3 = var1.func_74762_e("HomePosY");
      int var4 = var1.func_74762_e("HomePosZ");
      this.func_203011_g(new BlockPos(var2, var3, var4));
      super.func_70037_a(var1);
      this.func_203017_r(var1.func_74767_n("HasEgg"));
      int var5 = var1.func_74762_e("TravelPosX");
      int var6 = var1.func_74762_e("TravelPosY");
      int var7 = var1.func_74762_e("TravelPosZ");
      this.func_203019_h(new BlockPos(var5, var6, var7));
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      this.func_203011_g(new BlockPos(this.field_70165_t, this.field_70163_u, this.field_70161_v));
      this.func_203019_h(BlockPos.field_177992_a);
      return super.func_204210_a(var1, var2, var3);
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      BlockPos var3 = new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v);
      return var3.func_177956_o() < var1.func_181545_F() + 4 && super.func_205020_a(var1, var2);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(0, new EntityTurtle.AIPanic(this, 1.2D));
      this.field_70714_bg.func_75776_a(1, new EntityTurtle.AIMate(this, 1.0D));
      this.field_70714_bg.func_75776_a(1, new EntityTurtle.AILayEgg(this, 1.0D));
      this.field_70714_bg.func_75776_a(2, new EntityTurtle.AIPlayerTempt(this, 1.1D, Blocks.field_203198_aQ.func_199767_j()));
      this.field_70714_bg.func_75776_a(3, new EntityTurtle.AIGoToWater(this, 1.0D));
      this.field_70714_bg.func_75776_a(4, new EntityTurtle.AIGoHome(this, 1.0D));
      this.field_70714_bg.func_75776_a(7, new EntityTurtle.AITravel(this, 1.0D));
      this.field_70714_bg.func_75776_a(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(9, new EntityTurtle.AIWander(this, 1.0D, 100));
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(30.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.25D);
   }

   public boolean func_96092_aw() {
      return false;
   }

   public boolean func_70648_aU() {
      return true;
   }

   public CreatureAttribute func_70668_bt() {
      return CreatureAttribute.field_203100_e;
   }

   public int func_70627_aG() {
      return 200;
   }

   @Nullable
   protected SoundEvent func_184639_G() {
      return !this.func_70090_H() && this.field_70122_E && !this.func_70631_g_() ? SoundEvents.field_203277_iv : super.func_184639_G();
   }

   protected void func_203006_d(float var1) {
      super.func_203006_d(var1 * 1.5F);
   }

   protected SoundEvent func_184184_Z() {
      return SoundEvents.field_203265_iE;
   }

   @Nullable
   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return this.func_70631_g_() ? SoundEvents.field_203262_iB : SoundEvents.field_203261_iA;
   }

   @Nullable
   protected SoundEvent func_184615_bR() {
      return this.func_70631_g_() ? SoundEvents.field_203264_iD : SoundEvents.field_203263_iC;
   }

   protected void func_180429_a(BlockPos var1, IBlockState var2) {
      SoundEvent var3 = this.func_70631_g_() ? SoundEvents.field_203267_iG : SoundEvents.field_203266_iF;
      this.func_184185_a(var3, 0.15F, 1.0F);
   }

   public boolean func_204701_dC() {
      return super.func_204701_dC() && !this.func_203020_dx();
   }

   protected float func_203009_ad() {
      return this.field_82151_R + 0.15F;
   }

   public void func_98054_a(boolean var1) {
      this.func_98055_j(var1 ? 0.3F : 1.0F);
   }

   protected PathNavigate func_175447_b(World var1) {
      return new EntityTurtle.PathNavigater(this, var1);
   }

   @Nullable
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return new EntityTurtle(this.field_70170_p);
   }

   public boolean func_70877_b(ItemStack var1) {
      return var1.func_77973_b() == Blocks.field_203198_aQ.func_199767_j();
   }

   public float func_205022_a(BlockPos var1, IWorldReaderBase var2) {
      return !this.func_203022_dF() && var2.func_204610_c(var1).func_206884_a(FluidTags.field_206959_a) ? 10.0F : super.func_205022_a(var1, var2);
   }

   public void func_70636_d() {
      super.func_70636_d();
      if (this.func_203023_dy() && this.field_203028_bF >= 1 && this.field_203028_bF % 5 == 0) {
         BlockPos var1 = new BlockPos(this);
         if (this.field_70170_p.func_180495_p(var1.func_177977_b()).func_177230_c() == Blocks.field_150354_m) {
            this.field_70170_p.func_175718_b(2001, var1, Block.func_196246_j(Blocks.field_150354_m.func_176223_P()));
         }
      }

   }

   protected void func_175500_n() {
      super.func_175500_n();
      if (this.field_70170_p.func_82736_K().func_82766_b("doMobLoot")) {
         this.func_199702_a(Items.field_203183_eM, 1);
      }

   }

   public void func_191986_a(float var1, float var2, float var3) {
      if (this.func_70613_aW() && this.func_70090_H()) {
         this.func_191958_b(var1, var2, var3, 0.1F);
         this.func_70091_d(MoverType.SELF, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         this.field_70159_w *= 0.8999999761581421D;
         this.field_70181_x *= 0.8999999761581421D;
         this.field_70179_y *= 0.8999999761581421D;
         if (this.func_70638_az() == null && (!this.func_203022_dF() || this.func_174818_b(this.func_203018_dA()) >= 400.0D)) {
            this.field_70181_x -= 0.005D;
         }
      } else {
         super.func_191986_a(var1, var2, var3);
      }

   }

   public boolean func_184652_a(EntityPlayer var1) {
      return false;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_203249_aB;
   }

   public void func_70077_a(EntityLightningBolt var1) {
      this.func_70097_a(DamageSource.field_180137_b, 3.4028235E38F);
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (var1 == DamageSource.field_180137_b) {
         this.func_70099_a(new ItemStack(Items.field_151054_z, 1), 0.0F);
      }

   }

   static {
      field_203030_by = EntityDataManager.func_187226_a(EntityTurtle.class, DataSerializers.field_187200_j);
      field_203031_bz = EntityDataManager.func_187226_a(EntityTurtle.class, DataSerializers.field_187198_h);
      field_203024_bB = EntityDataManager.func_187226_a(EntityTurtle.class, DataSerializers.field_187198_h);
      field_203025_bC = EntityDataManager.func_187226_a(EntityTurtle.class, DataSerializers.field_187200_j);
      field_203026_bD = EntityDataManager.func_187226_a(EntityTurtle.class, DataSerializers.field_187198_h);
      field_203027_bE = EntityDataManager.func_187226_a(EntityTurtle.class, DataSerializers.field_187198_h);
      field_203029_bx = (var0) -> {
         if (!(var0 instanceof EntityLivingBase)) {
            return false;
         } else {
            return ((EntityLivingBase)var0).func_70631_g_() && !var0.func_70090_H();
         }
      };
   }

   static class PathNavigater extends PathNavigateSwimmer {
      PathNavigater(EntityTurtle var1, World var2) {
         super(var1, var2);
      }

      protected boolean func_75485_k() {
         return true;
      }

      protected PathFinder func_179679_a() {
         return new PathFinder(new WalkAndSwimNodeProcessor());
      }

      public boolean func_188555_b(BlockPos var1) {
         if (this.field_75515_a instanceof EntityTurtle) {
            EntityTurtle var2 = (EntityTurtle)this.field_75515_a;
            if (var2.func_203014_dG()) {
               return this.field_75513_b.func_180495_p(var1).func_177230_c() == Blocks.field_150355_j;
            }
         }

         return !this.field_75513_b.func_180495_p(var1.func_177977_b()).func_196958_f();
      }
   }

   static class MoveHelper extends EntityMoveHelper {
      private final EntityTurtle field_203103_i;

      MoveHelper(EntityTurtle var1) {
         super(var1);
         this.field_203103_i = var1;
      }

      private void func_203102_g() {
         if (this.field_203103_i.func_70090_H()) {
            EntityTurtle var10000 = this.field_203103_i;
            var10000.field_70181_x += 0.005D;
            if (this.field_203103_i.func_174818_b(this.field_203103_i.func_203018_dA()) > 256.0D) {
               this.field_203103_i.func_70659_e(Math.max(this.field_203103_i.func_70689_ay() / 2.0F, 0.08F));
            }

            if (this.field_203103_i.func_70631_g_()) {
               this.field_203103_i.func_70659_e(Math.max(this.field_203103_i.func_70689_ay() / 3.0F, 0.06F));
            }
         } else if (this.field_203103_i.field_70122_E) {
            this.field_203103_i.func_70659_e(Math.max(this.field_203103_i.func_70689_ay() / 2.0F, 0.06F));
         }

      }

      public void func_75641_c() {
         this.func_203102_g();
         if (this.field_188491_h == EntityMoveHelper.Action.MOVE_TO && !this.field_203103_i.func_70661_as().func_75500_f()) {
            double var1 = this.field_75646_b - this.field_203103_i.field_70165_t;
            double var3 = this.field_75647_c - this.field_203103_i.field_70163_u;
            double var5 = this.field_75644_d - this.field_203103_i.field_70161_v;
            double var7 = (double)MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5);
            var3 /= var7;
            float var9 = (float)(MathHelper.func_181159_b(var5, var1) * 57.2957763671875D) - 90.0F;
            this.field_203103_i.field_70177_z = this.func_75639_a(this.field_203103_i.field_70177_z, var9, 90.0F);
            this.field_203103_i.field_70761_aq = this.field_203103_i.field_70177_z;
            float var10 = (float)(this.field_75645_e * this.field_203103_i.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e());
            this.field_203103_i.func_70659_e(this.field_203103_i.func_70689_ay() + (var10 - this.field_203103_i.func_70689_ay()) * 0.125F);
            EntityTurtle var10000 = this.field_203103_i;
            var10000.field_70181_x += (double)this.field_203103_i.func_70689_ay() * var3 * 0.1D;
         } else {
            this.field_203103_i.func_70659_e(0.0F);
         }
      }
   }

   static class AIGoToWater extends EntityAIMoveToBlock {
      private final EntityTurtle field_203121_f;

      private AIGoToWater(EntityTurtle var1, double var2) {
         super(var1, var1.func_70631_g_() ? 2.0D : var2, 24);
         this.field_203121_f = var1;
         this.field_203112_e = -1;
      }

      public boolean func_75253_b() {
         return !this.field_203121_f.func_70090_H() && this.field_179493_e <= 1200 && this.func_179488_a(this.field_203121_f.field_70170_p, this.field_179494_b);
      }

      public boolean func_75250_a() {
         if (this.field_203121_f.func_70631_g_() && !this.field_203121_f.func_70090_H()) {
            return super.func_75250_a();
         } else {
            return !this.field_203121_f.func_203022_dF() && !this.field_203121_f.func_70090_H() && !this.field_203121_f.func_203020_dx() ? super.func_75250_a() : false;
         }
      }

      public int func_203111_j() {
         return 1;
      }

      public boolean func_203108_i() {
         return this.field_179493_e % 160 == 0;
      }

      protected boolean func_179488_a(IWorldReaderBase var1, BlockPos var2) {
         Block var3 = var1.func_180495_p(var2).func_177230_c();
         return var3 == Blocks.field_150355_j;
      }

      // $FF: synthetic method
      AIGoToWater(EntityTurtle var1, double var2, Object var4) {
         this(var1, var2);
      }
   }

   static class AIWander extends EntityAIWander {
      private final EntityTurtle field_203123_h;

      private AIWander(EntityTurtle var1, double var2, int var4) {
         super(var1, var2, var4);
         this.field_203123_h = var1;
      }

      public boolean func_75250_a() {
         return !this.field_75457_a.func_70090_H() && !this.field_203123_h.func_203022_dF() && !this.field_203123_h.func_203020_dx() ? super.func_75250_a() : false;
      }

      // $FF: synthetic method
      AIWander(EntityTurtle var1, double var2, int var4, Object var5) {
         this(var1, var2, var4);
      }
   }

   static class AILayEgg extends EntityAIMoveToBlock {
      private final EntityTurtle field_203122_f;

      AILayEgg(EntityTurtle var1, double var2) {
         super(var1, var2, 16);
         this.field_203122_f = var1;
      }

      public boolean func_75250_a() {
         return this.field_203122_f.func_203020_dx() && this.field_203122_f.func_174818_b(this.field_203122_f.func_203018_dA()) < 81.0D ? super.func_75250_a() : false;
      }

      public boolean func_75253_b() {
         return super.func_75253_b() && this.field_203122_f.func_203020_dx() && this.field_203122_f.func_174818_b(this.field_203122_f.func_203018_dA()) < 81.0D;
      }

      public void func_75246_d() {
         super.func_75246_d();
         BlockPos var1 = new BlockPos(this.field_203122_f);
         if (!this.field_203122_f.func_70090_H() && this.func_179487_f()) {
            if (this.field_203122_f.field_203028_bF < 1) {
               this.field_203122_f.func_203015_s(true);
            } else if (this.field_203122_f.field_203028_bF > 200) {
               World var2 = this.field_203122_f.field_70170_p;
               var2.func_184133_a((EntityPlayer)null, var1, SoundEvents.field_203278_iw, SoundCategory.BLOCKS, 0.3F, 0.9F + var2.field_73012_v.nextFloat() * 0.2F);
               var2.func_180501_a(this.field_179494_b.func_177984_a(), (IBlockState)Blocks.field_203213_jA.func_176223_P().func_206870_a(BlockTurtleEgg.field_203171_b, this.field_203122_f.field_70146_Z.nextInt(4) + 1), 3);
               this.field_203122_f.func_203017_r(false);
               this.field_203122_f.func_203015_s(false);
               this.field_203122_f.func_204700_e(600);
            }

            if (this.field_203122_f.func_203023_dy()) {
               this.field_203122_f.field_203028_bF++;
            }
         }

      }

      protected boolean func_179488_a(IWorldReaderBase var1, BlockPos var2) {
         if (!var1.func_175623_d(var2.func_177984_a())) {
            return false;
         } else {
            Block var3 = var1.func_180495_p(var2).func_177230_c();
            return var3 == Blocks.field_150354_m;
         }
      }
   }

   static class AIMate extends EntityAIMate {
      private final EntityTurtle field_203107_f;

      AIMate(EntityTurtle var1, double var2) {
         super(var1, var2);
         this.field_203107_f = var1;
      }

      public boolean func_75250_a() {
         return super.func_75250_a() && !this.field_203107_f.func_203020_dx();
      }

      protected void func_75388_i() {
         EntityPlayerMP var1 = this.field_75390_d.func_191993_do();
         if (var1 == null && this.field_75391_e.func_191993_do() != null) {
            var1 = this.field_75391_e.func_191993_do();
         }

         if (var1 != null) {
            var1.func_195066_a(StatList.field_151186_x);
            CriteriaTriggers.field_192134_n.func_192168_a(var1, this.field_75390_d, this.field_75391_e, (EntityAgeable)null);
         }

         this.field_203107_f.func_203017_r(true);
         this.field_75390_d.func_70875_t();
         this.field_75391_e.func_70875_t();
         Random var2 = this.field_75390_d.func_70681_au();
         if (this.field_75394_a.func_82736_K().func_82766_b("doMobLoot")) {
            this.field_75394_a.func_72838_d(new EntityXPOrb(this.field_75394_a, this.field_75390_d.field_70165_t, this.field_75390_d.field_70163_u, this.field_75390_d.field_70161_v, var2.nextInt(7) + 1));
         }

      }
   }

   static class AIPlayerTempt extends EntityAIBase {
      private final EntityTurtle field_203132_a;
      private final double field_203133_b;
      private EntityPlayer field_203134_c;
      private int field_203135_d;
      private final Set<Item> field_203136_e;

      AIPlayerTempt(EntityTurtle var1, double var2, Item var4) {
         super();
         this.field_203132_a = var1;
         this.field_203133_b = var2;
         this.field_203136_e = Sets.newHashSet(new Item[]{var4});
         this.func_75248_a(3);
      }

      public boolean func_75250_a() {
         if (this.field_203135_d > 0) {
            --this.field_203135_d;
            return false;
         } else {
            this.field_203134_c = this.field_203132_a.field_70170_p.func_72890_a(this.field_203132_a, 10.0D);
            if (this.field_203134_c == null) {
               return false;
            } else {
               return this.func_203131_a(this.field_203134_c.func_184614_ca()) || this.func_203131_a(this.field_203134_c.func_184592_cb());
            }
         }
      }

      private boolean func_203131_a(ItemStack var1) {
         return this.field_203136_e.contains(var1.func_77973_b());
      }

      public boolean func_75253_b() {
         return this.func_75250_a();
      }

      public void func_75251_c() {
         this.field_203134_c = null;
         this.field_203132_a.func_70661_as().func_75499_g();
         this.field_203135_d = 100;
      }

      public void func_75246_d() {
         this.field_203132_a.func_70671_ap().func_75651_a(this.field_203134_c, (float)(this.field_203132_a.func_184649_cE() + 20), (float)this.field_203132_a.func_70646_bf());
         if (this.field_203132_a.func_70068_e(this.field_203134_c) < 6.25D) {
            this.field_203132_a.func_70661_as().func_75499_g();
         } else {
            this.field_203132_a.func_70661_as().func_75497_a(this.field_203134_c, this.field_203133_b);
         }

      }
   }

   static class AIGoHome extends EntityAIBase {
      private final EntityTurtle field_203127_a;
      private final double field_203128_b;
      private boolean field_203129_c;
      private int field_203130_d;

      AIGoHome(EntityTurtle var1, double var2) {
         super();
         this.field_203127_a = var1;
         this.field_203128_b = var2;
      }

      public boolean func_75250_a() {
         if (this.field_203127_a.func_70631_g_()) {
            return false;
         } else if (this.field_203127_a.func_203020_dx()) {
            return true;
         } else if (this.field_203127_a.func_70681_au().nextInt(700) != 0) {
            return false;
         } else {
            return this.field_203127_a.func_174818_b(this.field_203127_a.func_203018_dA()) >= 4096.0D;
         }
      }

      public void func_75249_e() {
         this.field_203127_a.func_203012_t(true);
         this.field_203129_c = false;
         this.field_203130_d = 0;
      }

      public void func_75251_c() {
         this.field_203127_a.func_203012_t(false);
      }

      public boolean func_75253_b() {
         return this.field_203127_a.func_174818_b(this.field_203127_a.func_203018_dA()) >= 49.0D && !this.field_203129_c && this.field_203130_d <= 600;
      }

      public void func_75246_d() {
         BlockPos var1 = this.field_203127_a.func_203018_dA();
         boolean var2 = this.field_203127_a.func_174818_b(var1) <= 256.0D;
         if (var2) {
            ++this.field_203130_d;
         }

         if (this.field_203127_a.func_70661_as().func_75500_f()) {
            Vec3d var3 = RandomPositionGenerator.func_203155_a(this.field_203127_a, 16, 3, new Vec3d((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p()), 0.3141592741012573D);
            if (var3 == null) {
               var3 = RandomPositionGenerator.func_75464_a(this.field_203127_a, 8, 7, new Vec3d((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p()));
            }

            if (var3 != null && !var2 && this.field_203127_a.field_70170_p.func_180495_p(new BlockPos(var3)).func_177230_c() != Blocks.field_150355_j) {
               var3 = RandomPositionGenerator.func_75464_a(this.field_203127_a, 16, 5, new Vec3d((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p()));
            }

            if (var3 == null) {
               this.field_203129_c = true;
               return;
            }

            this.field_203127_a.func_70661_as().func_75492_a(var3.field_72450_a, var3.field_72448_b, var3.field_72449_c, this.field_203128_b);
         }

      }
   }

   static class AITravel extends EntityAIBase {
      private final EntityTurtle field_203137_a;
      private final double field_203138_b;
      private boolean field_203139_c;

      AITravel(EntityTurtle var1, double var2) {
         super();
         this.field_203137_a = var1;
         this.field_203138_b = var2;
      }

      public boolean func_75250_a() {
         return !this.field_203137_a.func_203022_dF() && !this.field_203137_a.func_203020_dx() && this.field_203137_a.func_70090_H();
      }

      public void func_75249_e() {
         boolean var1 = true;
         boolean var2 = true;
         Random var3 = this.field_203137_a.field_70146_Z;
         int var4 = var3.nextInt(1025) - 512;
         int var5 = var3.nextInt(9) - 4;
         int var6 = var3.nextInt(1025) - 512;
         if ((double)var5 + this.field_203137_a.field_70163_u > (double)(this.field_203137_a.field_70170_p.func_181545_F() - 1)) {
            var5 = 0;
         }

         BlockPos var7 = new BlockPos((double)var4 + this.field_203137_a.field_70165_t, (double)var5 + this.field_203137_a.field_70163_u, (double)var6 + this.field_203137_a.field_70161_v);
         this.field_203137_a.func_203019_h(var7);
         this.field_203137_a.func_203021_u(true);
         this.field_203139_c = false;
      }

      public void func_75246_d() {
         if (this.field_203137_a.func_70661_as().func_75500_f()) {
            BlockPos var1 = this.field_203137_a.func_203013_dB();
            Vec3d var2 = RandomPositionGenerator.func_203155_a(this.field_203137_a, 16, 3, new Vec3d((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p()), 0.3141592741012573D);
            if (var2 == null) {
               var2 = RandomPositionGenerator.func_75464_a(this.field_203137_a, 8, 7, new Vec3d((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p()));
            }

            if (var2 != null) {
               int var3 = MathHelper.func_76128_c(var2.field_72450_a);
               int var4 = MathHelper.func_76128_c(var2.field_72449_c);
               boolean var5 = true;
               MutableBoundingBox var6 = new MutableBoundingBox(var3 - 34, 0, var4 - 34, var3 + 34, 0, var4 + 34);
               if (!this.field_203137_a.field_70170_p.func_175711_a(var6)) {
                  var2 = null;
               }
            }

            if (var2 == null) {
               this.field_203139_c = true;
               return;
            }

            this.field_203137_a.func_70661_as().func_75492_a(var2.field_72450_a, var2.field_72448_b, var2.field_72449_c, this.field_203138_b);
         }

      }

      public boolean func_75253_b() {
         return !this.field_203137_a.func_70661_as().func_75500_f() && !this.field_203139_c && !this.field_203137_a.func_203022_dF() && !this.field_203137_a.func_70880_s() && !this.field_203137_a.func_203020_dx();
      }

      public void func_75251_c() {
         this.field_203137_a.func_203021_u(false);
         super.func_75251_c();
      }
   }

   static class AIPanic extends EntityAIPanic {
      AIPanic(EntityTurtle var1, double var2) {
         super(var1, var2);
      }

      public boolean func_75250_a() {
         if (this.field_75267_a.func_70643_av() == null && !this.field_75267_a.func_70027_ad()) {
            return false;
         } else {
            BlockPos var1 = this.func_188497_a(this.field_75267_a.field_70170_p, this.field_75267_a, 7, 4);
            if (var1 != null) {
               this.field_75266_c = (double)var1.func_177958_n();
               this.field_75263_d = (double)var1.func_177956_o();
               this.field_75264_e = (double)var1.func_177952_p();
               return true;
            } else {
               return this.func_190863_f();
            }
         }
      }
   }
}
