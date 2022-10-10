package net.minecraft.entity.monster;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityBodyHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityShulker extends EntityGolem implements IMob {
   private static final UUID field_184703_bv = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
   private static final AttributeModifier field_184704_bw;
   protected static final DataParameter<EnumFacing> field_184700_a;
   protected static final DataParameter<Optional<BlockPos>> field_184701_b;
   protected static final DataParameter<Byte> field_184702_c;
   protected static final DataParameter<Byte> field_190770_bw;
   private float field_184705_bx;
   private float field_184706_by;
   private BlockPos field_184707_bz;
   private int field_184708_bA;

   public EntityShulker(World var1) {
      super(EntityType.field_200738_ad, var1);
      this.func_70105_a(1.0F, 1.0F);
      this.field_70760_ar = 180.0F;
      this.field_70761_aq = 180.0F;
      this.field_70178_ae = true;
      this.field_184707_bz = null;
      this.field_70728_aV = 5;
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      this.field_70761_aq = 180.0F;
      this.field_70760_ar = 180.0F;
      this.field_70177_z = 180.0F;
      this.field_70126_B = 180.0F;
      this.field_70759_as = 180.0F;
      this.field_70758_at = 180.0F;
      return super.func_204210_a(var1, var2, var3);
   }

   protected void func_184651_r() {
      this.field_70714_bg.func_75776_a(1, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.field_70714_bg.func_75776_a(4, new EntityShulker.AIAttack());
      this.field_70714_bg.func_75776_a(7, new EntityShulker.AIPeek());
      this.field_70714_bg.func_75776_a(8, new EntityAILookIdle(this));
      this.field_70715_bh.func_75776_a(1, new EntityAIHurtByTarget(this, true, new Class[0]));
      this.field_70715_bh.func_75776_a(2, new EntityShulker.AIAttackNearest(this));
      this.field_70715_bh.func_75776_a(3, new EntityShulker.AIDefenseAttack(this));
   }

   protected boolean func_70041_e_() {
      return false;
   }

   public SoundCategory func_184176_by() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_187773_eO;
   }

   public void func_70642_aH() {
      if (!this.func_184686_df()) {
         super.func_70642_aH();
      }

   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187781_eS;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return this.func_184686_df() ? SoundEvents.field_187785_eU : SoundEvents.field_187783_eT;
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184700_a, EnumFacing.DOWN);
      this.field_70180_af.func_187214_a(field_184701_b, Optional.empty());
      this.field_70180_af.func_187214_a(field_184702_c, (byte)0);
      this.field_70180_af.func_187214_a(field_190770_bw, (byte)16);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(30.0D);
   }

   protected EntityBodyHelper func_184650_s() {
      return new EntityShulker.BodyHelper(this);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70180_af.func_187227_b(field_184700_a, EnumFacing.func_82600_a(var1.func_74771_c("AttachFace")));
      this.field_70180_af.func_187227_b(field_184702_c, var1.func_74771_c("Peek"));
      this.field_70180_af.func_187227_b(field_190770_bw, var1.func_74771_c("Color"));
      if (var1.func_74764_b("APX")) {
         int var2 = var1.func_74762_e("APX");
         int var3 = var1.func_74762_e("APY");
         int var4 = var1.func_74762_e("APZ");
         this.field_70180_af.func_187227_b(field_184701_b, Optional.of(new BlockPos(var2, var3, var4)));
      } else {
         this.field_70180_af.func_187227_b(field_184701_b, Optional.empty());
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74774_a("AttachFace", (byte)((EnumFacing)this.field_70180_af.func_187225_a(field_184700_a)).func_176745_a());
      var1.func_74774_a("Peek", (Byte)this.field_70180_af.func_187225_a(field_184702_c));
      var1.func_74774_a("Color", (Byte)this.field_70180_af.func_187225_a(field_190770_bw));
      BlockPos var2 = this.func_184699_da();
      if (var2 != null) {
         var1.func_74768_a("APX", var2.func_177958_n());
         var1.func_74768_a("APY", var2.func_177956_o());
         var1.func_74768_a("APZ", var2.func_177952_p());
      }

   }

   public void func_70071_h_() {
      super.func_70071_h_();
      BlockPos var1 = (BlockPos)((Optional)this.field_70180_af.func_187225_a(field_184701_b)).orElse((Object)null);
      if (var1 == null && !this.field_70170_p.field_72995_K) {
         var1 = new BlockPos(this);
         this.field_70180_af.func_187227_b(field_184701_b, Optional.of(var1));
      }

      float var2;
      if (this.func_184218_aH()) {
         var1 = null;
         var2 = this.func_184187_bx().field_70177_z;
         this.field_70177_z = var2;
         this.field_70761_aq = var2;
         this.field_70760_ar = var2;
         this.field_184708_bA = 0;
      } else if (!this.field_70170_p.field_72995_K) {
         IBlockState var19 = this.field_70170_p.func_180495_p(var1);
         if (!var19.func_196958_f()) {
            EnumFacing var3;
            if (var19.func_177230_c() == Blocks.field_196603_bb) {
               var3 = (EnumFacing)var19.func_177229_b(BlockPistonBase.field_176387_N);
               if (this.field_70170_p.func_175623_d(var1.func_177972_a(var3))) {
                  var1 = var1.func_177972_a(var3);
                  this.field_70180_af.func_187227_b(field_184701_b, Optional.of(var1));
               } else {
                  this.func_184689_o();
               }
            } else if (var19.func_177230_c() == Blocks.field_150332_K) {
               var3 = (EnumFacing)var19.func_177229_b(BlockPistonExtension.field_176387_N);
               if (this.field_70170_p.func_175623_d(var1.func_177972_a(var3))) {
                  var1 = var1.func_177972_a(var3);
                  this.field_70180_af.func_187227_b(field_184701_b, Optional.of(var1));
               } else {
                  this.func_184689_o();
               }
            } else {
               this.func_184689_o();
            }
         }

         BlockPos var20 = var1.func_177972_a(this.func_184696_cZ());
         if (!this.field_70170_p.func_195595_w(var20)) {
            boolean var4 = false;
            EnumFacing[] var5 = EnumFacing.values();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               EnumFacing var8 = var5[var7];
               var20 = var1.func_177972_a(var8);
               if (this.field_70170_p.func_195595_w(var20)) {
                  this.field_70180_af.func_187227_b(field_184700_a, var8);
                  var4 = true;
                  break;
               }
            }

            if (!var4) {
               this.func_184689_o();
            }
         }

         BlockPos var22 = var1.func_177972_a(this.func_184696_cZ().func_176734_d());
         if (this.field_70170_p.func_195595_w(var22)) {
            this.func_184689_o();
         }
      }

      var2 = (float)this.func_184684_db() * 0.01F;
      this.field_184705_bx = this.field_184706_by;
      if (this.field_184706_by > var2) {
         this.field_184706_by = MathHelper.func_76131_a(this.field_184706_by - 0.05F, var2, 1.0F);
      } else if (this.field_184706_by < var2) {
         this.field_184706_by = MathHelper.func_76131_a(this.field_184706_by + 0.05F, 0.0F, var2);
      }

      if (var1 != null) {
         if (this.field_70170_p.field_72995_K) {
            if (this.field_184708_bA > 0 && this.field_184707_bz != null) {
               --this.field_184708_bA;
            } else {
               this.field_184707_bz = var1;
            }
         }

         this.field_70165_t = (double)var1.func_177958_n() + 0.5D;
         this.field_70163_u = (double)var1.func_177956_o();
         this.field_70161_v = (double)var1.func_177952_p() + 0.5D;
         this.field_70169_q = this.field_70165_t;
         this.field_70167_r = this.field_70163_u;
         this.field_70166_s = this.field_70161_v;
         this.field_70142_S = this.field_70165_t;
         this.field_70137_T = this.field_70163_u;
         this.field_70136_U = this.field_70161_v;
         double var21 = 0.5D - (double)MathHelper.func_76126_a((0.5F + this.field_184706_by) * 3.1415927F) * 0.5D;
         double var23 = 0.5D - (double)MathHelper.func_76126_a((0.5F + this.field_184705_bx) * 3.1415927F) * 0.5D;
         double var24 = var21 - var23;
         double var9 = 0.0D;
         double var11 = 0.0D;
         double var13 = 0.0D;
         EnumFacing var15 = this.func_184696_cZ();
         switch(var15) {
         case DOWN:
            this.func_174826_a(new AxisAlignedBB(this.field_70165_t - 0.5D, this.field_70163_u, this.field_70161_v - 0.5D, this.field_70165_t + 0.5D, this.field_70163_u + 1.0D + var21, this.field_70161_v + 0.5D));
            var11 = var24;
            break;
         case UP:
            this.func_174826_a(new AxisAlignedBB(this.field_70165_t - 0.5D, this.field_70163_u - var21, this.field_70161_v - 0.5D, this.field_70165_t + 0.5D, this.field_70163_u + 1.0D, this.field_70161_v + 0.5D));
            var11 = -var24;
            break;
         case NORTH:
            this.func_174826_a(new AxisAlignedBB(this.field_70165_t - 0.5D, this.field_70163_u, this.field_70161_v - 0.5D, this.field_70165_t + 0.5D, this.field_70163_u + 1.0D, this.field_70161_v + 0.5D + var21));
            var13 = var24;
            break;
         case SOUTH:
            this.func_174826_a(new AxisAlignedBB(this.field_70165_t - 0.5D, this.field_70163_u, this.field_70161_v - 0.5D - var21, this.field_70165_t + 0.5D, this.field_70163_u + 1.0D, this.field_70161_v + 0.5D));
            var13 = -var24;
            break;
         case WEST:
            this.func_174826_a(new AxisAlignedBB(this.field_70165_t - 0.5D, this.field_70163_u, this.field_70161_v - 0.5D, this.field_70165_t + 0.5D + var21, this.field_70163_u + 1.0D, this.field_70161_v + 0.5D));
            var9 = var24;
            break;
         case EAST:
            this.func_174826_a(new AxisAlignedBB(this.field_70165_t - 0.5D - var21, this.field_70163_u, this.field_70161_v - 0.5D, this.field_70165_t + 0.5D, this.field_70163_u + 1.0D, this.field_70161_v + 0.5D));
            var9 = -var24;
         }

         if (var24 > 0.0D) {
            List var16 = this.field_70170_p.func_72839_b(this, this.func_174813_aQ());
            if (!var16.isEmpty()) {
               Iterator var17 = var16.iterator();

               while(var17.hasNext()) {
                  Entity var18 = (Entity)var17.next();
                  if (!(var18 instanceof EntityShulker) && !var18.field_70145_X) {
                     var18.func_70091_d(MoverType.SHULKER, var9, var11, var13);
                  }
               }
            }
         }
      }

   }

   public void func_70091_d(MoverType var1, double var2, double var4, double var6) {
      if (var1 == MoverType.SHULKER_BOX) {
         this.func_184689_o();
      } else {
         super.func_70091_d(var1, var2, var4, var6);
      }

   }

   public void func_70107_b(double var1, double var3, double var5) {
      super.func_70107_b(var1, var3, var5);
      if (this.field_70180_af != null && this.field_70173_aa != 0) {
         Optional var7 = (Optional)this.field_70180_af.func_187225_a(field_184701_b);
         Optional var8 = Optional.of(new BlockPos(var1, var3, var5));
         if (!var8.equals(var7)) {
            this.field_70180_af.func_187227_b(field_184701_b, var8);
            this.field_70180_af.func_187227_b(field_184702_c, (byte)0);
            this.field_70160_al = true;
         }

      }
   }

   protected boolean func_184689_o() {
      if (!this.func_175446_cd() && this.func_70089_S()) {
         BlockPos var1 = new BlockPos(this);

         for(int var2 = 0; var2 < 5; ++var2) {
            BlockPos var3 = var1.func_177982_a(8 - this.field_70146_Z.nextInt(17), 8 - this.field_70146_Z.nextInt(17), 8 - this.field_70146_Z.nextInt(17));
            if (var3.func_177956_o() > 0 && this.field_70170_p.func_175623_d(var3) && this.field_70170_p.func_191503_g(this) && this.field_70170_p.func_195586_b(this, new AxisAlignedBB(var3))) {
               boolean var4 = false;
               EnumFacing[] var5 = EnumFacing.values();
               int var6 = var5.length;

               for(int var7 = 0; var7 < var6; ++var7) {
                  EnumFacing var8 = var5[var7];
                  if (this.field_70170_p.func_195595_w(var3.func_177972_a(var8))) {
                     this.field_70180_af.func_187227_b(field_184700_a, var8);
                     var4 = true;
                     break;
                  }
               }

               if (var4) {
                  this.func_184185_a(SoundEvents.field_187791_eX, 1.0F, 1.0F);
                  this.field_70180_af.func_187227_b(field_184701_b, Optional.of(var3));
                  this.field_70180_af.func_187227_b(field_184702_c, (byte)0);
                  this.func_70624_b((EntityLivingBase)null);
                  return true;
               }
            }
         }

         return false;
      } else {
         return true;
      }
   }

   public void func_70636_d() {
      super.func_70636_d();
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
      this.field_70760_ar = 180.0F;
      this.field_70761_aq = 180.0F;
      this.field_70177_z = 180.0F;
   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_184701_b.equals(var1) && this.field_70170_p.field_72995_K && !this.func_184218_aH()) {
         BlockPos var2 = this.func_184699_da();
         if (var2 != null) {
            if (this.field_184707_bz == null) {
               this.field_184707_bz = var2;
            } else {
               this.field_184708_bA = 6;
            }

            this.field_70165_t = (double)var2.func_177958_n() + 0.5D;
            this.field_70163_u = (double)var2.func_177956_o();
            this.field_70161_v = (double)var2.func_177952_p() + 0.5D;
            this.field_70169_q = this.field_70165_t;
            this.field_70167_r = this.field_70163_u;
            this.field_70166_s = this.field_70161_v;
            this.field_70142_S = this.field_70165_t;
            this.field_70137_T = this.field_70163_u;
            this.field_70136_U = this.field_70161_v;
         }
      }

      super.func_184206_a(var1);
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      this.field_70716_bi = 0;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_184686_df()) {
         Entity var3 = var1.func_76364_f();
         if (var3 instanceof EntityArrow) {
            return false;
         }
      }

      if (super.func_70097_a(var1, var2)) {
         if ((double)this.func_110143_aJ() < (double)this.func_110138_aP() * 0.5D && this.field_70146_Z.nextInt(4) == 0) {
            this.func_184689_o();
         }

         return true;
      } else {
         return false;
      }
   }

   private boolean func_184686_df() {
      return this.func_184684_db() == 0;
   }

   @Nullable
   public AxisAlignedBB func_70046_E() {
      return this.func_70089_S() ? this.func_174813_aQ() : null;
   }

   public EnumFacing func_184696_cZ() {
      return (EnumFacing)this.field_70180_af.func_187225_a(field_184700_a);
   }

   @Nullable
   public BlockPos func_184699_da() {
      return (BlockPos)((Optional)this.field_70180_af.func_187225_a(field_184701_b)).orElse((Object)null);
   }

   public void func_184694_g(@Nullable BlockPos var1) {
      this.field_70180_af.func_187227_b(field_184701_b, Optional.ofNullable(var1));
   }

   public int func_184684_db() {
      return (Byte)this.field_70180_af.func_187225_a(field_184702_c);
   }

   public void func_184691_a(int var1) {
      if (!this.field_70170_p.field_72995_K) {
         this.func_110148_a(SharedMonsterAttributes.field_188791_g).func_111124_b(field_184704_bw);
         if (var1 == 0) {
            this.func_110148_a(SharedMonsterAttributes.field_188791_g).func_111121_a(field_184704_bw);
            this.func_184185_a(SoundEvents.field_187779_eR, 1.0F, 1.0F);
         } else {
            this.func_184185_a(SoundEvents.field_187787_eV, 1.0F, 1.0F);
         }
      }

      this.field_70180_af.func_187227_b(field_184702_c, (byte)var1);
   }

   public float func_184688_a(float var1) {
      return this.field_184705_bx + (this.field_184706_by - this.field_184705_bx) * var1;
   }

   public int func_184693_dc() {
      return this.field_184708_bA;
   }

   public BlockPos func_184692_dd() {
      return this.field_184707_bz;
   }

   public float func_70047_e() {
      return 0.5F;
   }

   public int func_70646_bf() {
      return 180;
   }

   public int func_184649_cE() {
      return 180;
   }

   public void func_70108_f(Entity var1) {
   }

   public float func_70111_Y() {
      return 0.0F;
   }

   public boolean func_184697_de() {
      return this.field_184707_bz != null && this.func_184699_da() != null;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186442_x;
   }

   public EnumDyeColor func_190769_dn() {
      Byte var1 = (Byte)this.field_70180_af.func_187225_a(field_190770_bw);
      return var1 != 16 && var1 <= 15 ? EnumDyeColor.func_196056_a(var1) : null;
   }

   static {
      field_184704_bw = (new AttributeModifier(field_184703_bv, "Covered armor bonus", 20.0D, 0)).func_111168_a(false);
      field_184700_a = EntityDataManager.func_187226_a(EntityShulker.class, DataSerializers.field_187202_l);
      field_184701_b = EntityDataManager.func_187226_a(EntityShulker.class, DataSerializers.field_187201_k);
      field_184702_c = EntityDataManager.func_187226_a(EntityShulker.class, DataSerializers.field_187191_a);
      field_190770_bw = EntityDataManager.func_187226_a(EntityShulker.class, DataSerializers.field_187191_a);
   }

   static class AIDefenseAttack extends EntityAINearestAttackableTarget<EntityLivingBase> {
      public AIDefenseAttack(EntityShulker var1) {
         super(var1, EntityLivingBase.class, 10, true, false, (var0) -> {
            return var0 instanceof IMob;
         });
      }

      public boolean func_75250_a() {
         return this.field_75299_d.func_96124_cp() == null ? false : super.func_75250_a();
      }

      protected AxisAlignedBB func_188511_a(double var1) {
         EnumFacing var3 = ((EntityShulker)this.field_75299_d).func_184696_cZ();
         if (var3.func_176740_k() == EnumFacing.Axis.X) {
            return this.field_75299_d.func_174813_aQ().func_72314_b(4.0D, var1, var1);
         } else {
            return var3.func_176740_k() == EnumFacing.Axis.Z ? this.field_75299_d.func_174813_aQ().func_72314_b(var1, var1, 4.0D) : this.field_75299_d.func_174813_aQ().func_72314_b(var1, 4.0D, var1);
         }
      }
   }

   class AIAttackNearest extends EntityAINearestAttackableTarget<EntityPlayer> {
      public AIAttackNearest(EntityShulker var2) {
         super(var2, EntityPlayer.class, true);
      }

      public boolean func_75250_a() {
         return EntityShulker.this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL ? false : super.func_75250_a();
      }

      protected AxisAlignedBB func_188511_a(double var1) {
         EnumFacing var3 = ((EntityShulker)this.field_75299_d).func_184696_cZ();
         if (var3.func_176740_k() == EnumFacing.Axis.X) {
            return this.field_75299_d.func_174813_aQ().func_72314_b(4.0D, var1, var1);
         } else {
            return var3.func_176740_k() == EnumFacing.Axis.Z ? this.field_75299_d.func_174813_aQ().func_72314_b(var1, var1, 4.0D) : this.field_75299_d.func_174813_aQ().func_72314_b(var1, 4.0D, var1);
         }
      }
   }

   class AIAttack extends EntityAIBase {
      private int field_188520_b;

      public AIAttack() {
         super();
         this.func_75248_a(3);
      }

      public boolean func_75250_a() {
         EntityLivingBase var1 = EntityShulker.this.func_70638_az();
         if (var1 != null && var1.func_70089_S()) {
            return EntityShulker.this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL;
         } else {
            return false;
         }
      }

      public void func_75249_e() {
         this.field_188520_b = 20;
         EntityShulker.this.func_184691_a(100);
      }

      public void func_75251_c() {
         EntityShulker.this.func_184691_a(0);
      }

      public void func_75246_d() {
         if (EntityShulker.this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL) {
            --this.field_188520_b;
            EntityLivingBase var1 = EntityShulker.this.func_70638_az();
            EntityShulker.this.func_70671_ap().func_75651_a(var1, 180.0F, 180.0F);
            double var2 = EntityShulker.this.func_70068_e(var1);
            if (var2 < 400.0D) {
               if (this.field_188520_b <= 0) {
                  this.field_188520_b = 20 + EntityShulker.this.field_70146_Z.nextInt(10) * 20 / 2;
                  EntityShulkerBullet var4 = new EntityShulkerBullet(EntityShulker.this.field_70170_p, EntityShulker.this, var1, EntityShulker.this.func_184696_cZ().func_176740_k());
                  EntityShulker.this.field_70170_p.func_72838_d(var4);
                  EntityShulker.this.func_184185_a(SoundEvents.field_187789_eW, 2.0F, (EntityShulker.this.field_70146_Z.nextFloat() - EntityShulker.this.field_70146_Z.nextFloat()) * 0.2F + 1.0F);
               }
            } else {
               EntityShulker.this.func_70624_b((EntityLivingBase)null);
            }

            super.func_75246_d();
         }
      }
   }

   class AIPeek extends EntityAIBase {
      private int field_188522_b;

      private AIPeek() {
         super();
      }

      public boolean func_75250_a() {
         return EntityShulker.this.func_70638_az() == null && EntityShulker.this.field_70146_Z.nextInt(40) == 0;
      }

      public boolean func_75253_b() {
         return EntityShulker.this.func_70638_az() == null && this.field_188522_b > 0;
      }

      public void func_75249_e() {
         this.field_188522_b = 20 * (1 + EntityShulker.this.field_70146_Z.nextInt(3));
         EntityShulker.this.func_184691_a(30);
      }

      public void func_75251_c() {
         if (EntityShulker.this.func_70638_az() == null) {
            EntityShulker.this.func_184691_a(0);
         }

      }

      public void func_75246_d() {
         --this.field_188522_b;
      }

      // $FF: synthetic method
      AIPeek(Object var2) {
         this();
      }
   }

   class BodyHelper extends EntityBodyHelper {
      public BodyHelper(EntityLivingBase var2) {
         super(var2);
      }

      public void func_75664_a() {
      }
   }
}
