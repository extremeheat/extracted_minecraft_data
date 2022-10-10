package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityRabbit extends EntityAnimal {
   private static final DataParameter<Integer> field_184773_bv;
   private static final ResourceLocation field_200611_bx;
   private int field_175540_bm;
   private int field_175535_bn;
   private boolean field_175537_bp;
   private int field_175538_bq;
   private int field_175541_bs;

   public EntityRabbit(World var1) {
      super(EntityType.field_200736_ab, var1);
      this.func_70105_a(0.4F, 0.5F);
      this.field_70767_i = new EntityRabbit.RabbitJumpHelper(this);
      this.field_70765_h = new EntityRabbit.RabbitMoveHelper(this);
      this.func_175515_b(0.0D);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(1, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityRabbit.AIPanic(this, 2.2D));
      this.field_70714_bg.func_75776_a(2, new EntityAIMate(this, 0.8D));
      this.field_70714_bg.func_75776_a(3, new EntityAITempt(this, 1.0D, Ingredient.func_199804_a(Items.field_151172_bF, Items.field_151150_bK, Blocks.field_196605_bc), false));
      this.field_70714_bg.func_75776_a(4, new EntityRabbit.AIAvoidEntity(this, EntityPlayer.class, 8.0F, 2.2D, 2.2D));
      this.field_70714_bg.func_75776_a(4, new EntityRabbit.AIAvoidEntity(this, EntityWolf.class, 10.0F, 2.2D, 2.2D));
      this.field_70714_bg.func_75776_a(4, new EntityRabbit.AIAvoidEntity(this, EntityMob.class, 4.0F, 2.2D, 2.2D));
      this.field_70714_bg.func_75776_a(5, new EntityRabbit.AIRaidFarm(this));
      this.field_70714_bg.func_75776_a(6, new EntityAIWanderAvoidWater(this, 0.6D));
      this.field_70714_bg.func_75776_a(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
   }

   protected float func_175134_bD() {
      if (!this.field_70123_F && (!this.field_70765_h.func_75640_a() || this.field_70765_h.func_179919_e() <= this.field_70163_u + 0.5D)) {
         Path var1 = this.field_70699_by.func_75505_d();
         if (var1 != null && var1.func_75873_e() < var1.func_75874_d()) {
            Vec3d var2 = var1.func_75878_a(this);
            if (var2.field_72448_b > this.field_70163_u + 0.5D) {
               return 0.5F;
            }
         }

         return this.field_70765_h.func_75638_b() <= 0.6D ? 0.2F : 0.3F;
      } else {
         return 0.5F;
      }
   }

   protected void func_70664_aZ() {
      super.func_70664_aZ();
      double var1 = this.field_70765_h.func_75638_b();
      if (var1 > 0.0D) {
         double var3 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;
         if (var3 < 0.010000000000000002D) {
            this.func_191958_b(0.0F, 0.0F, 1.0F, 0.1F);
         }
      }

      if (!this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_72960_a(this, (byte)1);
      }

   }

   public float func_175521_o(float var1) {
      return this.field_175535_bn == 0 ? 0.0F : ((float)this.field_175540_bm + var1) / (float)this.field_175535_bn;
   }

   public void func_175515_b(double var1) {
      this.func_70661_as().func_75489_a(var1);
      this.field_70765_h.func_75642_a(this.field_70765_h.func_179917_d(), this.field_70765_h.func_179919_e(), this.field_70765_h.func_179918_f(), var1);
   }

   public void func_70637_d(boolean var1) {
      super.func_70637_d(var1);
      if (var1) {
         this.func_184185_a(this.func_184771_da(), this.func_70599_aP(), ((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

   }

   public void func_184770_cZ() {
      this.func_70637_d(true);
      this.field_175535_bn = 10;
      this.field_175540_bm = 0;
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184773_bv, 0);
   }

   public void func_70619_bc() {
      if (this.field_175538_bq > 0) {
         --this.field_175538_bq;
      }

      if (this.field_175541_bs > 0) {
         this.field_175541_bs -= this.field_70146_Z.nextInt(3);
         if (this.field_175541_bs < 0) {
            this.field_175541_bs = 0;
         }
      }

      if (this.field_70122_E) {
         if (!this.field_175537_bp) {
            this.func_70637_d(false);
            this.func_175517_cu();
         }

         if (this.func_175531_cl() == 99 && this.field_175538_bq == 0) {
            EntityLivingBase var1 = this.func_70638_az();
            if (var1 != null && this.func_70068_e(var1) < 16.0D) {
               this.func_175533_a(var1.field_70165_t, var1.field_70161_v);
               this.field_70765_h.func_75642_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, this.field_70765_h.func_75638_b());
               this.func_184770_cZ();
               this.field_175537_bp = true;
            }
         }

         EntityRabbit.RabbitJumpHelper var4 = (EntityRabbit.RabbitJumpHelper)this.field_70767_i;
         if (!var4.func_180067_c()) {
            if (this.field_70765_h.func_75640_a() && this.field_175538_bq == 0) {
               Path var2 = this.field_70699_by.func_75505_d();
               Vec3d var3 = new Vec3d(this.field_70765_h.func_179917_d(), this.field_70765_h.func_179919_e(), this.field_70765_h.func_179918_f());
               if (var2 != null && var2.func_75873_e() < var2.func_75874_d()) {
                  var3 = var2.func_75878_a(this);
               }

               this.func_175533_a(var3.field_72450_a, var3.field_72449_c);
               this.func_184770_cZ();
            }
         } else if (!var4.func_180065_d()) {
            this.func_175518_cr();
         }
      }

      this.field_175537_bp = this.field_70122_E;
   }

   public void func_174830_Y() {
   }

   private void func_175533_a(double var1, double var3) {
      this.field_70177_z = (float)(MathHelper.func_181159_b(var3 - this.field_70161_v, var1 - this.field_70165_t) * 57.2957763671875D) - 90.0F;
   }

   private void func_175518_cr() {
      ((EntityRabbit.RabbitJumpHelper)this.field_70767_i).func_180066_a(true);
   }

   private void func_175520_cs() {
      ((EntityRabbit.RabbitJumpHelper)this.field_70767_i).func_180066_a(false);
   }

   private void func_175530_ct() {
      if (this.field_70765_h.func_75638_b() < 2.2D) {
         this.field_175538_bq = 10;
      } else {
         this.field_175538_bq = 1;
      }

   }

   private void func_175517_cu() {
      this.func_175530_ct();
      this.func_175520_cs();
   }

   public void func_70636_d() {
      super.func_70636_d();
      if (this.field_175540_bm != this.field_175535_bn) {
         ++this.field_175540_bm;
      } else if (this.field_175535_bn != 0) {
         this.field_175540_bm = 0;
         this.field_175535_bn = 0;
         this.func_70637_d(false);
      }

   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(3.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.30000001192092896D);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("RabbitType", this.func_175531_cl());
      var1.func_74768_a("MoreCarrotTicks", this.field_175541_bs);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_175529_r(var1.func_74762_e("RabbitType"));
      this.field_175541_bs = var1.func_74762_e("MoreCarrotTicks");
   }

   protected SoundEvent func_184771_da() {
      return SoundEvents.field_187824_en;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187816_ej;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_187822_em;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187820_el;
   }

   public boolean func_70652_k(Entity var1) {
      if (this.func_175531_cl() == 99) {
         this.func_184185_a(SoundEvents.field_187818_ek, 1.0F, (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
         return var1.func_70097_a(DamageSource.func_76358_a(this), 8.0F);
      } else {
         return var1.func_70097_a(DamageSource.func_76358_a(this), 3.0F);
      }
   }

   public SoundCategory func_184176_by() {
      return this.func_175531_cl() == 99 ? SoundCategory.HOSTILE : SoundCategory.NEUTRAL;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return this.func_180431_b(var1) ? false : super.func_70097_a(var1, var2);
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186393_A;
   }

   private boolean func_175525_a(Item var1) {
      return var1 == Items.field_151172_bF || var1 == Items.field_151150_bK || var1 == Blocks.field_196605_bc.func_199767_j();
   }

   public EntityRabbit func_90011_a(EntityAgeable var1) {
      EntityRabbit var2 = new EntityRabbit(this.field_70170_p);
      int var3 = this.func_184772_dk();
      if (this.field_70146_Z.nextInt(20) != 0) {
         if (var1 instanceof EntityRabbit && this.field_70146_Z.nextBoolean()) {
            var3 = ((EntityRabbit)var1).func_175531_cl();
         } else {
            var3 = this.func_175531_cl();
         }
      }

      var2.func_175529_r(var3);
      return var2;
   }

   public boolean func_70877_b(ItemStack var1) {
      return this.func_175525_a(var1.func_77973_b());
   }

   public int func_175531_cl() {
      return (Integer)this.field_70180_af.func_187225_a(field_184773_bv);
   }

   public void func_175529_r(int var1) {
      if (var1 == 99) {
         this.func_110148_a(SharedMonsterAttributes.field_188791_g).func_111128_a(8.0D);
         this.field_70714_bg.func_75776_a(4, new EntityRabbit.AIEvilAttack(this));
         this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, false, new Class[0]));
         this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, true));
         this.field_70715_bh.func_75776_a(2, new EntityAINearestAttackableTarget(this, EntityWolf.class, true));
         if (!this.func_145818_k_()) {
            this.func_200203_b(new TextComponentTranslation(Util.func_200697_a("entity", field_200611_bx), new Object[0]));
         }
      }

      this.field_70180_af.func_187227_b(field_184773_bv, var1);
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      Object var6 = super.func_204210_a(var1, var2, var3);
      int var4 = this.func_184772_dk();
      boolean var5 = false;
      if (var6 instanceof EntityRabbit.RabbitTypeData) {
         var4 = ((EntityRabbit.RabbitTypeData)var6).field_179427_a;
         var5 = true;
      } else {
         var6 = new EntityRabbit.RabbitTypeData(var4);
      }

      this.func_175529_r(var4);
      if (var5) {
         this.func_70873_a(-24000);
      }

      return (IEntityLivingData)var6;
   }

   private int func_184772_dk() {
      Biome var1 = this.field_70170_p.func_180494_b(new BlockPos(this));
      int var2 = this.field_70146_Z.nextInt(100);
      if (var1.func_201851_b() == Biome.RainType.SNOW) {
         return var2 < 80 ? 1 : 3;
      } else if (var1.func_201856_r() == Biome.Category.DESERT) {
         return 4;
      } else {
         return var2 < 50 ? 0 : (var2 < 90 ? 5 : 2);
      }
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      int var3 = MathHelper.func_76128_c(this.field_70165_t);
      int var4 = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
      int var5 = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos var6 = new BlockPos(var3, var4, var5);
      Block var7 = var1.func_180495_p(var6.func_177977_b()).func_177230_c();
      return var7 != Blocks.field_150349_c && var7 != Blocks.field_150433_aE && var7 != Blocks.field_150354_m ? super.func_205020_a(var1, var2) : true;
   }

   private boolean func_175534_cv() {
      return this.field_175541_bs == 0;
   }

   public void func_70103_a(byte var1) {
      if (var1 == 1) {
         this.func_174808_Z();
         this.field_175535_bn = 10;
         this.field_175540_bm = 0;
      } else {
         super.func_70103_a(var1);
      }

   }

   // $FF: synthetic method
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }

   static {
      field_184773_bv = EntityDataManager.func_187226_a(EntityRabbit.class, DataSerializers.field_187192_b);
      field_200611_bx = new ResourceLocation("killer_bunny");
   }

   static class AIEvilAttack extends EntityAIAttackMelee {
      public AIEvilAttack(EntityRabbit var1) {
         super(var1, 1.4D, true);
      }

      protected double func_179512_a(EntityLivingBase var1) {
         return (double)(4.0F + var1.field_70130_N);
      }
   }

   static class AIPanic extends EntityAIPanic {
      private final EntityRabbit field_179486_b;

      public AIPanic(EntityRabbit var1, double var2) {
         super(var1, var2);
         this.field_179486_b = var1;
      }

      public void func_75246_d() {
         super.func_75246_d();
         this.field_179486_b.func_175515_b(this.field_75265_b);
      }
   }

   static class AIRaidFarm extends EntityAIMoveToBlock {
      private final EntityRabbit field_179500_c;
      private boolean field_179498_d;
      private boolean field_179499_e;

      public AIRaidFarm(EntityRabbit var1) {
         super(var1, 0.699999988079071D, 16);
         this.field_179500_c = var1;
      }

      public boolean func_75250_a() {
         if (this.field_179496_a <= 0) {
            if (!this.field_179500_c.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
               return false;
            }

            this.field_179499_e = false;
            this.field_179498_d = this.field_179500_c.func_175534_cv();
            this.field_179498_d = true;
         }

         return super.func_75250_a();
      }

      public boolean func_75253_b() {
         return this.field_179499_e && super.func_75253_b();
      }

      public void func_75246_d() {
         super.func_75246_d();
         this.field_179500_c.func_70671_ap().func_75650_a((double)this.field_179494_b.func_177958_n() + 0.5D, (double)(this.field_179494_b.func_177956_o() + 1), (double)this.field_179494_b.func_177952_p() + 0.5D, 10.0F, (float)this.field_179500_c.func_70646_bf());
         if (this.func_179487_f()) {
            World var1 = this.field_179500_c.field_70170_p;
            BlockPos var2 = this.field_179494_b.func_177984_a();
            IBlockState var3 = var1.func_180495_p(var2);
            Block var4 = var3.func_177230_c();
            if (this.field_179499_e && var4 instanceof BlockCarrot) {
               Integer var5 = (Integer)var3.func_177229_b(BlockCarrot.field_176488_a);
               if (var5 == 0) {
                  var1.func_180501_a(var2, Blocks.field_150350_a.func_176223_P(), 2);
                  var1.func_175655_b(var2, true);
               } else {
                  var1.func_180501_a(var2, (IBlockState)var3.func_206870_a(BlockCarrot.field_176488_a, var5 - 1), 2);
                  var1.func_175718_b(2001, var2, Block.func_196246_j(var3));
               }

               this.field_179500_c.field_175541_bs = 40;
            }

            this.field_179499_e = false;
            this.field_179496_a = 10;
         }

      }

      protected boolean func_179488_a(IWorldReaderBase var1, BlockPos var2) {
         Block var3 = var1.func_180495_p(var2).func_177230_c();
         if (var3 == Blocks.field_150458_ak && this.field_179498_d && !this.field_179499_e) {
            var2 = var2.func_177984_a();
            IBlockState var4 = var1.func_180495_p(var2);
            var3 = var4.func_177230_c();
            if (var3 instanceof BlockCarrot && ((BlockCarrot)var3).func_185525_y(var4)) {
               this.field_179499_e = true;
               return true;
            }
         }

         return false;
      }
   }

   static class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
      private final EntityRabbit field_179511_d;

      public AIAvoidEntity(EntityRabbit var1, Class<T> var2, float var3, double var4, double var6) {
         super(var1, var2, var3, var4, var6);
         this.field_179511_d = var1;
      }

      public boolean func_75250_a() {
         return this.field_179511_d.func_175531_cl() != 99 && super.func_75250_a();
      }
   }

   static class RabbitMoveHelper extends EntityMoveHelper {
      private final EntityRabbit field_179929_g;
      private double field_188492_j;

      public RabbitMoveHelper(EntityRabbit var1) {
         super(var1);
         this.field_179929_g = var1;
      }

      public void func_75641_c() {
         if (this.field_179929_g.field_70122_E && !this.field_179929_g.field_70703_bu && !((EntityRabbit.RabbitJumpHelper)this.field_179929_g.field_70767_i).func_180067_c()) {
            this.field_179929_g.func_175515_b(0.0D);
         } else if (this.func_75640_a()) {
            this.field_179929_g.func_175515_b(this.field_188492_j);
         }

         super.func_75641_c();
      }

      public void func_75642_a(double var1, double var3, double var5, double var7) {
         if (this.field_179929_g.func_70090_H()) {
            var7 = 1.5D;
         }

         super.func_75642_a(var1, var3, var5, var7);
         if (var7 > 0.0D) {
            this.field_188492_j = var7;
         }

      }
   }

   public class RabbitJumpHelper extends EntityJumpHelper {
      private final EntityRabbit field_180070_c;
      private boolean field_180068_d;

      public RabbitJumpHelper(EntityRabbit var2) {
         super(var2);
         this.field_180070_c = var2;
      }

      public boolean func_180067_c() {
         return this.field_75662_b;
      }

      public boolean func_180065_d() {
         return this.field_180068_d;
      }

      public void func_180066_a(boolean var1) {
         this.field_180068_d = var1;
      }

      public void func_75661_b() {
         if (this.field_75662_b) {
            this.field_180070_c.func_184770_cZ();
            this.field_75662_b = false;
         }

      }
   }

   public static class RabbitTypeData implements IEntityLivingData {
      public int field_179427_a;

      public RabbitTypeData(int var1) {
         super();
         this.field_179427_a = var1;
      }
   }
}
