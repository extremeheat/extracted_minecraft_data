package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockBubbleColumn;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityParrot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityPlayer extends EntityLivingBase {
   private static final DataParameter<Float> field_184829_a;
   private static final DataParameter<Integer> field_184830_b;
   protected static final DataParameter<Byte> field_184827_bp;
   protected static final DataParameter<Byte> field_184828_bq;
   protected static final DataParameter<NBTTagCompound> field_192032_bt;
   protected static final DataParameter<NBTTagCompound> field_192033_bu;
   public InventoryPlayer field_71071_by = new InventoryPlayer(this);
   protected InventoryEnderChest field_71078_a = new InventoryEnderChest();
   public Container field_71069_bz;
   public Container field_71070_bA;
   protected FoodStats field_71100_bB = new FoodStats();
   protected int field_71101_bC;
   public float field_71107_bF;
   public float field_71109_bG;
   public int field_71090_bL;
   public double field_71091_bM;
   public double field_71096_bN;
   public double field_71097_bO;
   public double field_71094_bP;
   public double field_71095_bQ;
   public double field_71085_bR;
   protected boolean field_71083_bS;
   public BlockPos field_71081_bT;
   private int field_71076_b;
   public float field_71079_bU;
   public float field_71082_cx;
   public float field_71089_bV;
   private boolean field_203042_d;
   protected boolean field_204230_bP;
   private BlockPos field_71077_c;
   private boolean field_82248_d;
   public PlayerCapabilities field_71075_bZ = new PlayerCapabilities();
   public int field_71068_ca;
   public int field_71067_cb;
   public float field_71106_cc;
   protected int field_175152_f;
   protected float field_71102_ce = 0.02F;
   private int field_82249_h;
   private final GameProfile field_146106_i;
   private boolean field_175153_bG;
   private ItemStack field_184831_bT;
   private final CooldownTracker field_184832_bU;
   @Nullable
   public EntityFishHook field_71104_cf;

   public EntityPlayer(World var1, GameProfile var2) {
      super(EntityType.field_200729_aH, var1);
      this.field_184831_bT = ItemStack.field_190927_a;
      this.field_184832_bU = this.func_184815_l();
      this.func_184221_a(func_146094_a(var2));
      this.field_146106_i = var2;
      this.field_71069_bz = new ContainerPlayer(this.field_71071_by, !var1.field_72995_K, this);
      this.field_71070_bA = this.field_71069_bz;
      BlockPos var3 = var1.func_175694_M();
      this.func_70012_b((double)var3.func_177958_n() + 0.5D, (double)(var3.func_177956_o() + 1), (double)var3.func_177952_p() + 0.5D, 0.0F, 0.0F);
      this.field_70741_aB = 180.0F;
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0D);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.10000000149011612D);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_188790_f);
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_188792_h);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_187214_a(field_184829_a, 0.0F);
      this.field_70180_af.func_187214_a(field_184830_b, 0);
      this.field_70180_af.func_187214_a(field_184827_bp, (byte)0);
      this.field_70180_af.func_187214_a(field_184828_bq, (byte)1);
      this.field_70180_af.func_187214_a(field_192032_bt, new NBTTagCompound());
      this.field_70180_af.func_187214_a(field_192033_bu, new NBTTagCompound());
   }

   public void func_70071_h_() {
      this.field_70145_X = this.func_175149_v();
      if (this.func_175149_v()) {
         this.field_70122_E = false;
      }

      if (this.field_71090_bL > 0) {
         --this.field_71090_bL;
      }

      if (this.func_70608_bn()) {
         ++this.field_71076_b;
         if (this.field_71076_b > 100) {
            this.field_71076_b = 100;
         }

         if (!this.field_70170_p.field_72995_K) {
            if (!this.func_175143_p()) {
               this.func_70999_a(true, true, false);
            } else if (this.field_70170_p.func_72935_r()) {
               this.func_70999_a(false, true, true);
            }
         }
      } else if (this.field_71076_b > 0) {
         ++this.field_71076_b;
         if (this.field_71076_b >= 110) {
            this.field_71076_b = 0;
         }
      }

      this.func_203040_o();
      this.func_204229_de();
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K && this.field_71070_bA != null && !this.field_71070_bA.func_75145_c(this)) {
         this.func_71053_j();
         this.field_71070_bA = this.field_71069_bz;
      }

      if (this.func_70027_ad() && this.field_71075_bZ.field_75102_a) {
         this.func_70066_B();
      }

      this.func_184820_o();
      if (!this.field_70170_p.field_72995_K) {
         this.field_71100_bB.func_75118_a(this);
         this.func_195066_a(StatList.field_188097_g);
         if (this.func_70089_S()) {
            this.func_195066_a(StatList.field_188098_h);
         }

         if (this.func_70093_af()) {
            this.func_195066_a(StatList.field_188099_i);
         }

         if (!this.func_70608_bn()) {
            this.func_195066_a(StatList.field_203284_n);
         }
      }

      int var1 = 29999999;
      double var2 = MathHelper.func_151237_a(this.field_70165_t, -2.9999999E7D, 2.9999999E7D);
      double var4 = MathHelper.func_151237_a(this.field_70161_v, -2.9999999E7D, 2.9999999E7D);
      if (var2 != this.field_70165_t || var4 != this.field_70161_v) {
         this.func_70107_b(var2, this.field_70163_u, var4);
      }

      ++this.field_184617_aD;
      ItemStack var6 = this.func_184614_ca();
      if (!ItemStack.func_77989_b(this.field_184831_bT, var6)) {
         if (!ItemStack.func_185132_d(this.field_184831_bT, var6)) {
            this.func_184821_cY();
         }

         this.field_184831_bT = var6.func_190926_b() ? ItemStack.field_190927_a : var6.func_77946_l();
      }

      this.func_203041_m();
      this.field_184832_bU.func_185144_a();
      this.func_184808_cD();
   }

   protected boolean func_204229_de() {
      this.field_204230_bP = this.func_208600_a(FluidTags.field_206959_a);
      return this.field_204230_bP;
   }

   private void func_203041_m() {
      ItemStack var1 = this.func_184582_a(EntityEquipmentSlot.HEAD);
      if (var1.func_77973_b() == Items.field_203179_ao && !this.func_208600_a(FluidTags.field_206959_a)) {
         this.func_195064_c(new PotionEffect(MobEffects.field_76427_o, 200, 0, false, false, true));
      }

   }

   protected CooldownTracker func_184815_l() {
      return new CooldownTracker();
   }

   private void func_203040_o() {
      IBlockState var1 = this.field_70170_p.func_203067_a(this.func_174813_aQ().func_72314_b(0.0D, -0.4000000059604645D, 0.0D).func_186664_h(0.001D), Blocks.field_203203_C);
      if (var1 != null) {
         if (!this.field_203042_d && !this.field_70148_d && var1.func_177230_c() == Blocks.field_203203_C && !this.func_175149_v()) {
            boolean var2 = (Boolean)var1.func_177229_b(BlockBubbleColumn.field_203160_a);
            if (var2) {
               this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_203283_jd, this.func_184176_by(), 1.0F, 1.0F, false);
            } else {
               this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_203252_T, this.func_184176_by(), 1.0F, 1.0F, false);
            }
         }

         this.field_203042_d = true;
      } else {
         this.field_203042_d = false;
      }

   }

   private void func_184820_o() {
      this.field_71091_bM = this.field_71094_bP;
      this.field_71096_bN = this.field_71095_bQ;
      this.field_71097_bO = this.field_71085_bR;
      double var1 = this.field_70165_t - this.field_71094_bP;
      double var3 = this.field_70163_u - this.field_71095_bQ;
      double var5 = this.field_70161_v - this.field_71085_bR;
      double var7 = 10.0D;
      if (var1 > 10.0D) {
         this.field_71094_bP = this.field_70165_t;
         this.field_71091_bM = this.field_71094_bP;
      }

      if (var5 > 10.0D) {
         this.field_71085_bR = this.field_70161_v;
         this.field_71097_bO = this.field_71085_bR;
      }

      if (var3 > 10.0D) {
         this.field_71095_bQ = this.field_70163_u;
         this.field_71096_bN = this.field_71095_bQ;
      }

      if (var1 < -10.0D) {
         this.field_71094_bP = this.field_70165_t;
         this.field_71091_bM = this.field_71094_bP;
      }

      if (var5 < -10.0D) {
         this.field_71085_bR = this.field_70161_v;
         this.field_71097_bO = this.field_71085_bR;
      }

      if (var3 < -10.0D) {
         this.field_71095_bQ = this.field_70163_u;
         this.field_71096_bN = this.field_71095_bQ;
      }

      this.field_71094_bP += var1 * 0.25D;
      this.field_71085_bR += var5 * 0.25D;
      this.field_71095_bQ += var3 * 0.25D;
   }

   protected void func_184808_cD() {
      float var1;
      float var2;
      if (this.func_184613_cA()) {
         var1 = 0.6F;
         var2 = 0.6F;
      } else if (this.func_70608_bn()) {
         var1 = 0.2F;
         var2 = 0.2F;
      } else if (!this.func_203007_ba() && !this.func_204805_cN()) {
         if (this.func_70093_af()) {
            var1 = 0.6F;
            var2 = 1.65F;
         } else {
            var1 = 0.6F;
            var2 = 1.8F;
         }
      } else {
         var1 = 0.6F;
         var2 = 0.6F;
      }

      if (var1 != this.field_70130_N || var2 != this.field_70131_O) {
         AxisAlignedBB var3 = this.func_174813_aQ();
         var3 = new AxisAlignedBB(var3.field_72340_a, var3.field_72338_b, var3.field_72339_c, var3.field_72340_a + (double)var1, var3.field_72338_b + (double)var2, var3.field_72339_c + (double)var1);
         if (this.field_70170_p.func_195586_b((Entity)null, var3)) {
            this.func_70105_a(var1, var2);
         }
      }

   }

   public int func_82145_z() {
      return this.field_71075_bZ.field_75102_a ? 1 : 80;
   }

   protected SoundEvent func_184184_Z() {
      return SoundEvents.field_187808_ef;
   }

   protected SoundEvent func_184181_aa() {
      return SoundEvents.field_187806_ee;
   }

   protected SoundEvent func_204208_ah() {
      return SoundEvents.field_204328_gh;
   }

   public int func_82147_ab() {
      return 10;
   }

   public void func_184185_a(SoundEvent var1, float var2, float var3) {
      this.field_70170_p.func_184148_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, var1, this.func_184176_by(), var2, var3);
   }

   public SoundCategory func_184176_by() {
      return SoundCategory.PLAYERS;
   }

   protected int func_190531_bD() {
      return 20;
   }

   public void func_70103_a(byte var1) {
      if (var1 == 9) {
         this.func_71036_o();
      } else if (var1 == 23) {
         this.field_175153_bG = false;
      } else if (var1 == 22) {
         this.field_175153_bG = true;
      } else {
         super.func_70103_a(var1);
      }

   }

   protected boolean func_70610_aX() {
      return this.func_110143_aJ() <= 0.0F || this.func_70608_bn();
   }

   protected void func_71053_j() {
      this.field_71070_bA = this.field_71069_bz;
   }

   public void func_70098_U() {
      if (!this.field_70170_p.field_72995_K && this.func_70093_af() && this.func_184218_aH()) {
         this.func_184210_p();
         this.func_70095_a(false);
      } else {
         double var1 = this.field_70165_t;
         double var3 = this.field_70163_u;
         double var5 = this.field_70161_v;
         float var7 = this.field_70177_z;
         float var8 = this.field_70125_A;
         super.func_70098_U();
         this.field_71107_bF = this.field_71109_bG;
         this.field_71109_bG = 0.0F;
         this.func_71015_k(this.field_70165_t - var1, this.field_70163_u - var3, this.field_70161_v - var5);
         if (this.func_184187_bx() instanceof EntityPig) {
            this.field_70125_A = var8;
            this.field_70177_z = var7;
            this.field_70761_aq = ((EntityPig)this.func_184187_bx()).field_70761_aq;
         }

      }
   }

   public void func_70065_x() {
      this.func_70105_a(0.6F, 1.8F);
      super.func_70065_x();
      this.func_70606_j(this.func_110138_aP());
      this.field_70725_aQ = 0;
   }

   protected void func_70626_be() {
      super.func_70626_be();
      this.func_82168_bl();
      this.field_70759_as = this.field_70177_z;
   }

   public void func_70636_d() {
      if (this.field_71101_bC > 0) {
         --this.field_71101_bC;
      }

      if (this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL && this.field_70170_p.func_82736_K().func_82766_b("naturalRegeneration")) {
         if (this.func_110143_aJ() < this.func_110138_aP() && this.field_70173_aa % 20 == 0) {
            this.func_70691_i(1.0F);
         }

         if (this.field_71100_bB.func_75121_c() && this.field_70173_aa % 10 == 0) {
            this.field_71100_bB.func_75114_a(this.field_71100_bB.func_75116_a() + 1);
         }
      }

      this.field_71071_by.func_70429_k();
      this.field_71107_bF = this.field_71109_bG;
      super.func_70636_d();
      IAttributeInstance var1 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
      if (!this.field_70170_p.field_72995_K) {
         var1.func_111128_a((double)this.field_71075_bZ.func_75094_b());
      }

      this.field_70747_aH = this.field_71102_ce;
      if (this.func_70051_ag()) {
         this.field_70747_aH = (float)((double)this.field_70747_aH + (double)this.field_71102_ce * 0.3D);
      }

      this.func_70659_e((float)var1.func_111126_e());
      float var2 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      float var3 = (float)(Math.atan(-this.field_70181_x * 0.20000000298023224D) * 15.0D);
      if (var2 > 0.1F) {
         var2 = 0.1F;
      }

      if (!this.field_70122_E || this.func_110143_aJ() <= 0.0F || this.func_203007_ba()) {
         var2 = 0.0F;
      }

      if (this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
         var3 = 0.0F;
      }

      this.field_71109_bG += (var2 - this.field_71109_bG) * 0.4F;
      this.field_70726_aT += (var3 - this.field_70726_aT) * 0.8F;
      if (this.func_110143_aJ() > 0.0F && !this.func_175149_v()) {
         AxisAlignedBB var4;
         if (this.func_184218_aH() && !this.func_184187_bx().field_70128_L) {
            var4 = this.func_174813_aQ().func_111270_a(this.func_184187_bx().func_174813_aQ()).func_72314_b(1.0D, 0.0D, 1.0D);
         } else {
            var4 = this.func_174813_aQ().func_72314_b(1.0D, 0.5D, 1.0D);
         }

         List var5 = this.field_70170_p.func_72839_b(this, var4);

         for(int var6 = 0; var6 < var5.size(); ++var6) {
            Entity var7 = (Entity)var5.get(var6);
            if (!var7.field_70128_L) {
               this.func_71044_o(var7);
            }
         }
      }

      this.func_192028_j(this.func_192023_dk());
      this.func_192028_j(this.func_192025_dl());
      if (!this.field_70170_p.field_72995_K && (this.field_70143_R > 0.5F || this.func_70090_H() || this.func_184218_aH()) || this.field_71075_bZ.field_75100_b) {
         this.func_192030_dh();
      }

   }

   private void func_192028_j(@Nullable NBTTagCompound var1) {
      if (var1 != null && !var1.func_74764_b("Silent") || !var1.func_74767_n("Silent")) {
         String var2 = var1.func_74779_i("id");
         if (EntityType.func_200713_a(var2) == EntityType.field_200783_W) {
            EntityParrot.func_192005_a(this.field_70170_p, this);
         }
      }

   }

   private void func_71044_o(Entity var1) {
      var1.func_70100_b_(this);
   }

   public int func_71037_bA() {
      return (Integer)this.field_70180_af.func_187225_a(field_184830_b);
   }

   public void func_85040_s(int var1) {
      this.field_70180_af.func_187227_b(field_184830_b, var1);
   }

   public void func_85039_t(int var1) {
      int var2 = this.func_71037_bA();
      this.field_70180_af.func_187227_b(field_184830_b, var2 + var1);
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      this.func_70105_a(0.2F, 0.2F);
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.field_70181_x = 0.10000000149011612D;
      if ("Notch".equals(this.func_200200_C_().getString())) {
         this.func_146097_a(new ItemStack(Items.field_151034_e), true, false);
      }

      if (!this.field_70170_p.func_82736_K().func_82766_b("keepInventory") && !this.func_175149_v()) {
         this.func_190776_cN();
         this.field_71071_by.func_70436_m();
      }

      if (var1 != null) {
         this.field_70159_w = (double)(-MathHelper.func_76134_b((this.field_70739_aP + this.field_70177_z) * 0.017453292F) * 0.1F);
         this.field_70179_y = (double)(-MathHelper.func_76126_a((this.field_70739_aP + this.field_70177_z) * 0.017453292F) * 0.1F);
      } else {
         this.field_70159_w = 0.0D;
         this.field_70179_y = 0.0D;
      }

      this.func_195066_a(StatList.field_188069_A);
      this.func_175145_a(StatList.field_199092_j.func_199076_b(StatList.field_188098_h));
      this.func_175145_a(StatList.field_199092_j.func_199076_b(StatList.field_203284_n));
      this.func_70066_B();
      this.func_70052_a(0, false);
   }

   protected void func_190776_cN() {
      for(int var1 = 0; var1 < this.field_71071_by.func_70302_i_(); ++var1) {
         ItemStack var2 = this.field_71071_by.func_70301_a(var1);
         if (!var2.func_190926_b() && EnchantmentHelper.func_190939_c(var2)) {
            this.field_71071_by.func_70304_b(var1);
         }
      }

   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      if (var1 == DamageSource.field_76370_b) {
         return SoundEvents.field_193806_fH;
      } else {
         return var1 == DamageSource.field_76369_e ? SoundEvents.field_193805_fG : SoundEvents.field_187800_eb;
      }
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_187798_ea;
   }

   @Nullable
   public EntityItem func_71040_bB(boolean var1) {
      return this.func_146097_a(this.field_71071_by.func_70298_a(this.field_71071_by.field_70461_c, var1 && !this.field_71071_by.func_70448_g().func_190926_b() ? this.field_71071_by.func_70448_g().func_190916_E() : 1), false, true);
   }

   @Nullable
   public EntityItem func_71019_a(ItemStack var1, boolean var2) {
      return this.func_146097_a(var1, false, var2);
   }

   @Nullable
   public EntityItem func_146097_a(ItemStack var1, boolean var2, boolean var3) {
      if (var1.func_190926_b()) {
         return null;
      } else {
         double var4 = this.field_70163_u - 0.30000001192092896D + (double)this.func_70047_e();
         EntityItem var6 = new EntityItem(this.field_70170_p, this.field_70165_t, var4, this.field_70161_v, var1);
         var6.func_174867_a(40);
         if (var3) {
            var6.func_200216_c(this.func_110124_au());
         }

         float var7;
         float var8;
         if (var2) {
            var7 = this.field_70146_Z.nextFloat() * 0.5F;
            var8 = this.field_70146_Z.nextFloat() * 6.2831855F;
            var6.field_70159_w = (double)(-MathHelper.func_76126_a(var8) * var7);
            var6.field_70179_y = (double)(MathHelper.func_76134_b(var8) * var7);
            var6.field_70181_x = 0.20000000298023224D;
         } else {
            var7 = 0.3F;
            var6.field_70159_w = (double)(-MathHelper.func_76126_a(this.field_70177_z * 0.017453292F) * MathHelper.func_76134_b(this.field_70125_A * 0.017453292F) * var7);
            var6.field_70179_y = (double)(MathHelper.func_76134_b(this.field_70177_z * 0.017453292F) * MathHelper.func_76134_b(this.field_70125_A * 0.017453292F) * var7);
            var6.field_70181_x = (double)(-MathHelper.func_76126_a(this.field_70125_A * 0.017453292F) * var7 + 0.1F);
            var8 = this.field_70146_Z.nextFloat() * 6.2831855F;
            var7 = 0.02F * this.field_70146_Z.nextFloat();
            var6.field_70159_w += Math.cos((double)var8) * (double)var7;
            var6.field_70181_x += (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.1F);
            var6.field_70179_y += Math.sin((double)var8) * (double)var7;
         }

         ItemStack var9 = this.func_184816_a(var6);
         if (var3) {
            if (!var9.func_190926_b()) {
               this.func_71064_a(StatList.field_188068_aj.func_199076_b(var9.func_77973_b()), var1.func_190916_E());
            }

            this.func_195066_a(StatList.field_75952_v);
         }

         return var6;
      }
   }

   protected ItemStack func_184816_a(EntityItem var1) {
      this.field_70170_p.func_72838_d(var1);
      return var1.func_92059_d();
   }

   public float func_184813_a(IBlockState var1) {
      float var2 = this.field_71071_by.func_184438_a(var1);
      if (var2 > 1.0F) {
         int var3 = EnchantmentHelper.func_185293_e(this);
         ItemStack var4 = this.func_184614_ca();
         if (var3 > 0 && !var4.func_190926_b()) {
            var2 += (float)(var3 * var3 + 1);
         }
      }

      if (PotionUtil.func_205135_a(this)) {
         var2 *= 1.0F + (float)(PotionUtil.func_205134_b(this) + 1) * 0.2F;
      }

      if (this.func_70644_a(MobEffects.field_76419_f)) {
         float var5;
         switch(this.func_70660_b(MobEffects.field_76419_f).func_76458_c()) {
         case 0:
            var5 = 0.3F;
            break;
         case 1:
            var5 = 0.09F;
            break;
         case 2:
            var5 = 0.0027F;
            break;
         case 3:
         default:
            var5 = 8.1E-4F;
         }

         var2 *= var5;
      }

      if (this.func_208600_a(FluidTags.field_206959_a) && !EnchantmentHelper.func_185287_i(this)) {
         var2 /= 5.0F;
      }

      if (!this.field_70122_E) {
         var2 /= 5.0F;
      }

      return var2;
   }

   public boolean func_184823_b(IBlockState var1) {
      return var1.func_185904_a().func_76229_l() || this.field_71071_by.func_184432_b(var1);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_184221_a(func_146094_a(this.field_146106_i));
      NBTTagList var2 = var1.func_150295_c("Inventory", 10);
      this.field_71071_by.func_70443_b(var2);
      this.field_71071_by.field_70461_c = var1.func_74762_e("SelectedItemSlot");
      this.field_71083_bS = var1.func_74767_n("Sleeping");
      this.field_71076_b = var1.func_74765_d("SleepTimer");
      this.field_71106_cc = var1.func_74760_g("XpP");
      this.field_71068_ca = var1.func_74762_e("XpLevel");
      this.field_71067_cb = var1.func_74762_e("XpTotal");
      this.field_175152_f = var1.func_74762_e("XpSeed");
      if (this.field_175152_f == 0) {
         this.field_175152_f = this.field_70146_Z.nextInt();
      }

      this.func_85040_s(var1.func_74762_e("Score"));
      if (this.field_71083_bS) {
         this.field_71081_bT = new BlockPos(this);
         this.func_70999_a(true, true, false);
      }

      if (var1.func_150297_b("SpawnX", 99) && var1.func_150297_b("SpawnY", 99) && var1.func_150297_b("SpawnZ", 99)) {
         this.field_71077_c = new BlockPos(var1.func_74762_e("SpawnX"), var1.func_74762_e("SpawnY"), var1.func_74762_e("SpawnZ"));
         this.field_82248_d = var1.func_74767_n("SpawnForced");
      }

      this.field_71100_bB.func_75112_a(var1);
      this.field_71075_bZ.func_75095_b(var1);
      if (var1.func_150297_b("EnderItems", 9)) {
         this.field_71078_a.func_70486_a(var1.func_150295_c("EnderItems", 10));
      }

      if (var1.func_150297_b("ShoulderEntityLeft", 10)) {
         this.func_192029_h(var1.func_74775_l("ShoulderEntityLeft"));
      }

      if (var1.func_150297_b("ShoulderEntityRight", 10)) {
         this.func_192031_i(var1.func_74775_l("ShoulderEntityRight"));
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("DataVersion", 1631);
      var1.func_74782_a("Inventory", this.field_71071_by.func_70442_a(new NBTTagList()));
      var1.func_74768_a("SelectedItemSlot", this.field_71071_by.field_70461_c);
      var1.func_74757_a("Sleeping", this.field_71083_bS);
      var1.func_74777_a("SleepTimer", (short)this.field_71076_b);
      var1.func_74776_a("XpP", this.field_71106_cc);
      var1.func_74768_a("XpLevel", this.field_71068_ca);
      var1.func_74768_a("XpTotal", this.field_71067_cb);
      var1.func_74768_a("XpSeed", this.field_175152_f);
      var1.func_74768_a("Score", this.func_71037_bA());
      if (this.field_71077_c != null) {
         var1.func_74768_a("SpawnX", this.field_71077_c.func_177958_n());
         var1.func_74768_a("SpawnY", this.field_71077_c.func_177956_o());
         var1.func_74768_a("SpawnZ", this.field_71077_c.func_177952_p());
         var1.func_74757_a("SpawnForced", this.field_82248_d);
      }

      this.field_71100_bB.func_75117_b(var1);
      this.field_71075_bZ.func_75091_a(var1);
      var1.func_74782_a("EnderItems", this.field_71078_a.func_70487_g());
      if (!this.func_192023_dk().isEmpty()) {
         var1.func_74782_a("ShoulderEntityLeft", this.func_192023_dk());
      }

      if (!this.func_192025_dl().isEmpty()) {
         var1.func_74782_a("ShoulderEntityRight", this.func_192025_dl());
      }

   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (this.field_71075_bZ.field_75102_a && !var1.func_76357_e()) {
         return false;
      } else {
         this.field_70708_bq = 0;
         if (this.func_110143_aJ() <= 0.0F) {
            return false;
         } else {
            if (this.func_70608_bn() && !this.field_70170_p.field_72995_K) {
               this.func_70999_a(true, true, false);
            }

            this.func_192030_dh();
            if (var1.func_76350_n()) {
               if (this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
                  var2 = 0.0F;
               }

               if (this.field_70170_p.func_175659_aa() == EnumDifficulty.EASY) {
                  var2 = Math.min(var2 / 2.0F + 1.0F, var2);
               }

               if (this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) {
                  var2 = var2 * 3.0F / 2.0F;
               }
            }

            return var2 == 0.0F ? false : super.func_70097_a(var1, var2);
         }
      }
   }

   protected void func_190629_c(EntityLivingBase var1) {
      super.func_190629_c(var1);
      if (var1.func_184614_ca().func_77973_b() instanceof ItemAxe) {
         this.func_190777_m(true);
      }

   }

   public boolean func_96122_a(EntityPlayer var1) {
      Team var2 = this.func_96124_cp();
      Team var3 = var1.func_96124_cp();
      if (var2 == null) {
         return true;
      } else {
         return !var2.func_142054_a(var3) ? true : var2.func_96665_g();
      }
   }

   protected void func_70675_k(float var1) {
      this.field_71071_by.func_70449_g(var1);
   }

   protected void func_184590_k(float var1) {
      if (var1 >= 3.0F && this.field_184627_bm.func_77973_b() == Items.field_185159_cQ) {
         int var2 = 1 + MathHelper.func_76141_d(var1);
         this.field_184627_bm.func_77972_a(var2, this);
         if (this.field_184627_bm.func_190926_b()) {
            EnumHand var3 = this.func_184600_cs();
            if (var3 == EnumHand.MAIN_HAND) {
               this.func_184201_a(EntityEquipmentSlot.MAINHAND, ItemStack.field_190927_a);
            } else {
               this.func_184201_a(EntityEquipmentSlot.OFFHAND, ItemStack.field_190927_a);
            }

            this.field_184627_bm = ItemStack.field_190927_a;
            this.func_184185_a(SoundEvents.field_187769_eM, 0.8F, 0.8F + this.field_70170_p.field_73012_v.nextFloat() * 0.4F);
         }
      }

   }

   public float func_82243_bO() {
      int var1 = 0;
      Iterator var2 = this.field_71071_by.field_70460_b.iterator();

      while(var2.hasNext()) {
         ItemStack var3 = (ItemStack)var2.next();
         if (!var3.func_190926_b()) {
            ++var1;
         }
      }

      return (float)var1 / (float)this.field_71071_by.field_70460_b.size();
   }

   protected void func_70665_d(DamageSource var1, float var2) {
      if (!this.func_180431_b(var1)) {
         var2 = this.func_70655_b(var1, var2);
         var2 = this.func_70672_c(var1, var2);
         float var3 = var2;
         var2 = Math.max(var2 - this.func_110139_bj(), 0.0F);
         this.func_110149_m(this.func_110139_bj() - (var3 - var2));
         float var4 = var3 - var2;
         if (var4 > 0.0F && var4 < 3.4028235E37F) {
            this.func_195067_a(StatList.field_212738_J, Math.round(var4 * 10.0F));
         }

         if (var2 != 0.0F) {
            this.func_71020_j(var1.func_76345_d());
            float var5 = this.func_110143_aJ();
            this.func_70606_j(this.func_110143_aJ() - var2);
            this.func_110142_aN().func_94547_a(var1, var5, var2);
            if (var2 < 3.4028235E37F) {
               this.func_195067_a(StatList.field_188112_z, Math.round(var2 * 10.0F));
            }

         }
      }
   }

   public void func_175141_a(TileEntitySign var1) {
   }

   public void func_184809_a(CommandBlockBaseLogic var1) {
   }

   public void func_184824_a(TileEntityCommandBlock var1) {
   }

   public void func_189807_a(TileEntityStructure var1) {
   }

   public void func_180472_a(IMerchant var1) {
   }

   public void func_71007_a(IInventory var1) {
   }

   public void func_184826_a(AbstractHorse var1, IInventory var2) {
   }

   public void func_180468_a(IInteractionObject var1) {
   }

   public void func_184814_a(ItemStack var1, EnumHand var2) {
   }

   public EnumActionResult func_190775_a(Entity var1, EnumHand var2) {
      if (this.func_175149_v()) {
         if (var1 instanceof IInventory) {
            this.func_71007_a((IInventory)var1);
         }

         return EnumActionResult.PASS;
      } else {
         ItemStack var3 = this.func_184586_b(var2);
         ItemStack var4 = var3.func_190926_b() ? ItemStack.field_190927_a : var3.func_77946_l();
         if (var1.func_184230_a(this, var2)) {
            if (this.field_71075_bZ.field_75098_d && var3 == this.func_184586_b(var2) && var3.func_190916_E() < var4.func_190916_E()) {
               var3.func_190920_e(var4.func_190916_E());
            }

            return EnumActionResult.SUCCESS;
         } else {
            if (!var3.func_190926_b() && var1 instanceof EntityLivingBase) {
               if (this.field_71075_bZ.field_75098_d) {
                  var3 = var4;
               }

               if (var3.func_111282_a(this, (EntityLivingBase)var1, var2)) {
                  if (var3.func_190926_b() && !this.field_71075_bZ.field_75098_d) {
                     this.func_184611_a(var2, ItemStack.field_190927_a);
                  }

                  return EnumActionResult.SUCCESS;
               }
            }

            return EnumActionResult.PASS;
         }
      }
   }

   public double func_70033_W() {
      return -0.35D;
   }

   public void func_184210_p() {
      super.func_184210_p();
      this.field_184245_j = 0;
   }

   public void func_71059_n(Entity var1) {
      if (var1.func_70075_an()) {
         if (!var1.func_85031_j(this)) {
            float var2 = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
            float var3;
            if (var1 instanceof EntityLivingBase) {
               var3 = EnchantmentHelper.func_152377_a(this.func_184614_ca(), ((EntityLivingBase)var1).func_70668_bt());
            } else {
               var3 = EnchantmentHelper.func_152377_a(this.func_184614_ca(), CreatureAttribute.UNDEFINED);
            }

            float var4 = this.func_184825_o(0.5F);
            var2 *= 0.2F + var4 * var4 * 0.8F;
            var3 *= var4;
            this.func_184821_cY();
            if (var2 > 0.0F || var3 > 0.0F) {
               boolean var5 = var4 > 0.9F;
               boolean var6 = false;
               byte var7 = 0;
               int var26 = var7 + EnchantmentHelper.func_77501_a(this);
               if (this.func_70051_ag() && var5) {
                  this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187721_dT, this.func_184176_by(), 1.0F, 1.0F);
                  ++var26;
                  var6 = true;
               }

               boolean var8 = var5 && this.field_70143_R > 0.0F && !this.field_70122_E && !this.func_70617_f_() && !this.func_70090_H() && !this.func_70644_a(MobEffects.field_76440_q) && !this.func_184218_aH() && var1 instanceof EntityLivingBase;
               var8 = var8 && !this.func_70051_ag();
               if (var8) {
                  var2 *= 1.5F;
               }

               var2 += var3;
               boolean var9 = false;
               double var10 = (double)(this.field_70140_Q - this.field_70141_P);
               if (var5 && !var8 && !var6 && this.field_70122_E && var10 < (double)this.func_70689_ay()) {
                  ItemStack var12 = this.func_184586_b(EnumHand.MAIN_HAND);
                  if (var12.func_77973_b() instanceof ItemSword) {
                     var9 = true;
                  }
               }

               float var27 = 0.0F;
               boolean var13 = false;
               int var14 = EnchantmentHelper.func_90036_a(this);
               if (var1 instanceof EntityLivingBase) {
                  var27 = ((EntityLivingBase)var1).func_110143_aJ();
                  if (var14 > 0 && !var1.func_70027_ad()) {
                     var13 = true;
                     var1.func_70015_d(1);
                  }
               }

               double var15 = var1.field_70159_w;
               double var17 = var1.field_70181_x;
               double var19 = var1.field_70179_y;
               boolean var21 = var1.func_70097_a(DamageSource.func_76365_a(this), var2);
               if (var21) {
                  if (var26 > 0) {
                     if (var1 instanceof EntityLivingBase) {
                        ((EntityLivingBase)var1).func_70653_a(this, (float)var26 * 0.5F, (double)MathHelper.func_76126_a(this.field_70177_z * 0.017453292F), (double)(-MathHelper.func_76134_b(this.field_70177_z * 0.017453292F)));
                     } else {
                        var1.func_70024_g((double)(-MathHelper.func_76126_a(this.field_70177_z * 0.017453292F) * (float)var26 * 0.5F), 0.1D, (double)(MathHelper.func_76134_b(this.field_70177_z * 0.017453292F) * (float)var26 * 0.5F));
                     }

                     this.field_70159_w *= 0.6D;
                     this.field_70179_y *= 0.6D;
                     this.func_70031_b(false);
                  }

                  if (var9) {
                     float var22 = 1.0F + EnchantmentHelper.func_191527_a(this) * var2;
                     List var23 = this.field_70170_p.func_72872_a(EntityLivingBase.class, var1.func_174813_aQ().func_72314_b(1.0D, 0.25D, 1.0D));
                     Iterator var24 = var23.iterator();

                     label166:
                     while(true) {
                        EntityLivingBase var25;
                        do {
                           do {
                              do {
                                 do {
                                    if (!var24.hasNext()) {
                                       this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187730_dW, this.func_184176_by(), 1.0F, 1.0F);
                                       this.func_184810_cG();
                                       break label166;
                                    }

                                    var25 = (EntityLivingBase)var24.next();
                                 } while(var25 == this);
                              } while(var25 == var1);
                           } while(this.func_184191_r(var25));
                        } while(var25 instanceof EntityArmorStand && ((EntityArmorStand)var25).func_181026_s());

                        if (this.func_70068_e(var25) < 9.0D) {
                           var25.func_70653_a(this, 0.4F, (double)MathHelper.func_76126_a(this.field_70177_z * 0.017453292F), (double)(-MathHelper.func_76134_b(this.field_70177_z * 0.017453292F)));
                           var25.func_70097_a(DamageSource.func_76365_a(this), var22);
                        }
                     }
                  }

                  if (var1 instanceof EntityPlayerMP && var1.field_70133_I) {
                     ((EntityPlayerMP)var1).field_71135_a.func_147359_a(new SPacketEntityVelocity(var1));
                     var1.field_70133_I = false;
                     var1.field_70159_w = var15;
                     var1.field_70181_x = var17;
                     var1.field_70179_y = var19;
                  }

                  if (var8) {
                     this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187718_dS, this.func_184176_by(), 1.0F, 1.0F);
                     this.func_71009_b(var1);
                  }

                  if (!var8 && !var9) {
                     if (var5) {
                        this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187727_dV, this.func_184176_by(), 1.0F, 1.0F);
                     } else {
                        this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187733_dX, this.func_184176_by(), 1.0F, 1.0F);
                     }
                  }

                  if (var3 > 0.0F) {
                     this.func_71047_c(var1);
                  }

                  this.func_130011_c(var1);
                  if (var1 instanceof EntityLivingBase) {
                     EnchantmentHelper.func_151384_a((EntityLivingBase)var1, this);
                  }

                  EnchantmentHelper.func_151385_b(this, var1);
                  ItemStack var28 = this.func_184614_ca();
                  Object var29 = var1;
                  if (var1 instanceof MultiPartEntityPart) {
                     IEntityMultiPart var30 = ((MultiPartEntityPart)var1).field_70259_a;
                     if (var30 instanceof EntityLivingBase) {
                        var29 = (EntityLivingBase)var30;
                     }
                  }

                  if (!var28.func_190926_b() && var29 instanceof EntityLivingBase) {
                     var28.func_77961_a((EntityLivingBase)var29, this);
                     if (var28.func_190926_b()) {
                        this.func_184611_a(EnumHand.MAIN_HAND, ItemStack.field_190927_a);
                     }
                  }

                  if (var1 instanceof EntityLivingBase) {
                     float var31 = var27 - ((EntityLivingBase)var1).func_110143_aJ();
                     this.func_195067_a(StatList.field_188111_y, Math.round(var31 * 10.0F));
                     if (var14 > 0) {
                        var1.func_70015_d(var14 * 4);
                     }

                     if (this.field_70170_p instanceof WorldServer && var31 > 2.0F) {
                        int var32 = (int)((double)var31 * 0.5D);
                        ((WorldServer)this.field_70170_p).func_195598_a(Particles.field_197615_h, var1.field_70165_t, var1.field_70163_u + (double)(var1.field_70131_O * 0.5F), var1.field_70161_v, var32, 0.1D, 0.0D, 0.1D, 0.2D);
                     }
                  }

                  this.func_71020_j(0.1F);
               } else {
                  this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187724_dU, this.func_184176_by(), 1.0F, 1.0F);
                  if (var13) {
                     var1.func_70066_B();
                  }
               }
            }

         }
      }
   }

   protected void func_204804_d(EntityLivingBase var1) {
      this.func_71059_n(var1);
   }

   public void func_190777_m(boolean var1) {
      float var2 = 0.25F + (float)EnchantmentHelper.func_185293_e(this) * 0.05F;
      if (var1) {
         var2 += 0.75F;
      }

      if (this.field_70146_Z.nextFloat() < var2) {
         this.func_184811_cZ().func_185145_a(Items.field_185159_cQ, 100);
         this.func_184602_cy();
         this.field_70170_p.func_72960_a(this, (byte)30);
      }

   }

   public void func_71009_b(Entity var1) {
   }

   public void func_71047_c(Entity var1) {
   }

   public void func_184810_cG() {
      double var1 = (double)(-MathHelper.func_76126_a(this.field_70177_z * 0.017453292F));
      double var3 = (double)MathHelper.func_76134_b(this.field_70177_z * 0.017453292F);
      if (this.field_70170_p instanceof WorldServer) {
         ((WorldServer)this.field_70170_p).func_195598_a(Particles.field_197603_N, this.field_70165_t + var1, this.field_70163_u + (double)this.field_70131_O * 0.5D, this.field_70161_v + var3, 0, var1, 0.0D, var3, 0.0D);
      }

   }

   public void func_71004_bE() {
   }

   public void func_70106_y() {
      super.func_70106_y();
      this.field_71069_bz.func_75134_a(this);
      if (this.field_71070_bA != null) {
         this.field_71070_bA.func_75134_a(this);
      }

   }

   public boolean func_70094_T() {
      return !this.field_71083_bS && super.func_70094_T();
   }

   public boolean func_175144_cb() {
      return false;
   }

   public GameProfile func_146103_bH() {
      return this.field_146106_i;
   }

   public EntityPlayer.SleepResult func_180469_a(BlockPos var1) {
      EnumFacing var2 = (EnumFacing)this.field_70170_p.func_180495_p(var1).func_177229_b(BlockHorizontal.field_185512_D);
      if (!this.field_70170_p.field_72995_K) {
         if (this.func_70608_bn() || !this.func_70089_S()) {
            return EntityPlayer.SleepResult.OTHER_PROBLEM;
         }

         if (!this.field_70170_p.field_73011_w.func_76569_d()) {
            return EntityPlayer.SleepResult.NOT_POSSIBLE_HERE;
         }

         if (this.field_70170_p.func_72935_r()) {
            return EntityPlayer.SleepResult.NOT_POSSIBLE_NOW;
         }

         if (!this.func_190774_a(var1, var2)) {
            return EntityPlayer.SleepResult.TOO_FAR_AWAY;
         }

         if (!this.func_184812_l_()) {
            double var3 = 8.0D;
            double var5 = 5.0D;
            List var7 = this.field_70170_p.func_175647_a(EntityMob.class, new AxisAlignedBB((double)var1.func_177958_n() - 8.0D, (double)var1.func_177956_o() - 5.0D, (double)var1.func_177952_p() - 8.0D, (double)var1.func_177958_n() + 8.0D, (double)var1.func_177956_o() + 5.0D, (double)var1.func_177952_p() + 8.0D), new EntityPlayer.SleepEnemyPredicate(this));
            if (!var7.isEmpty()) {
               return EntityPlayer.SleepResult.NOT_SAFE;
            }
         }
      }

      if (this.func_184218_aH()) {
         this.func_184210_p();
      }

      this.func_192030_dh();
      this.func_175145_a(StatList.field_199092_j.func_199076_b(StatList.field_203284_n));
      this.func_70105_a(0.2F, 0.2F);
      if (this.field_70170_p.func_175667_e(var1)) {
         float var8 = 0.5F + (float)var2.func_82601_c() * 0.4F;
         float var4 = 0.5F + (float)var2.func_82599_e() * 0.4F;
         this.func_175139_a(var2);
         this.func_70107_b((double)((float)var1.func_177958_n() + var8), (double)((float)var1.func_177956_o() + 0.6875F), (double)((float)var1.func_177952_p() + var4));
      } else {
         this.func_70107_b((double)((float)var1.func_177958_n() + 0.5F), (double)((float)var1.func_177956_o() + 0.6875F), (double)((float)var1.func_177952_p() + 0.5F));
      }

      this.field_71083_bS = true;
      this.field_71076_b = 0;
      this.field_71081_bT = var1;
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
      if (!this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_72854_c();
      }

      return EntityPlayer.SleepResult.OK;
   }

   private boolean func_190774_a(BlockPos var1, EnumFacing var2) {
      if (Math.abs(this.field_70165_t - (double)var1.func_177958_n()) <= 3.0D && Math.abs(this.field_70163_u - (double)var1.func_177956_o()) <= 2.0D && Math.abs(this.field_70161_v - (double)var1.func_177952_p()) <= 3.0D) {
         return true;
      } else {
         BlockPos var3 = var1.func_177972_a(var2.func_176734_d());
         return Math.abs(this.field_70165_t - (double)var3.func_177958_n()) <= 3.0D && Math.abs(this.field_70163_u - (double)var3.func_177956_o()) <= 2.0D && Math.abs(this.field_70161_v - (double)var3.func_177952_p()) <= 3.0D;
      }
   }

   private void func_175139_a(EnumFacing var1) {
      this.field_71079_bU = -1.8F * (float)var1.func_82601_c();
      this.field_71089_bV = -1.8F * (float)var1.func_82599_e();
   }

   public void func_70999_a(boolean var1, boolean var2, boolean var3) {
      this.func_70105_a(0.6F, 1.8F);
      IBlockState var4 = this.field_70170_p.func_180495_p(this.field_71081_bT);
      if (this.field_71081_bT != null && var4.func_177230_c() instanceof BlockBed) {
         this.field_70170_p.func_180501_a(this.field_71081_bT, (IBlockState)var4.func_206870_a(BlockBed.field_176471_b, false), 4);
         BlockPos var5 = BlockBed.func_176468_a(this.field_70170_p, this.field_71081_bT, 0);
         if (var5 == null) {
            var5 = this.field_71081_bT.func_177984_a();
         }

         this.func_70107_b((double)((float)var5.func_177958_n() + 0.5F), (double)((float)var5.func_177956_o() + 0.1F), (double)((float)var5.func_177952_p() + 0.5F));
      }

      this.field_71083_bS = false;
      if (!this.field_70170_p.field_72995_K && var2) {
         this.field_70170_p.func_72854_c();
      }

      this.field_71076_b = var1 ? 0 : 100;
      if (var3) {
         this.func_180473_a(this.field_71081_bT, false);
      }

   }

   private boolean func_175143_p() {
      return this.field_70170_p.func_180495_p(this.field_71081_bT).func_177230_c() instanceof BlockBed;
   }

   @Nullable
   public static BlockPos func_180467_a(IBlockReader var0, BlockPos var1, boolean var2) {
      Block var3 = var0.func_180495_p(var1).func_177230_c();
      if (!(var3 instanceof BlockBed)) {
         if (!var2) {
            return null;
         } else {
            boolean var4 = var3.func_181623_g();
            boolean var5 = var0.func_180495_p(var1.func_177984_a()).func_177230_c().func_181623_g();
            return var4 && var5 ? var1 : null;
         }
      } else {
         return BlockBed.func_176468_a(var0, var1, 0);
      }
   }

   public float func_71051_bG() {
      if (this.field_71081_bT != null) {
         EnumFacing var1 = (EnumFacing)this.field_70170_p.func_180495_p(this.field_71081_bT).func_177229_b(BlockHorizontal.field_185512_D);
         switch(var1) {
         case SOUTH:
            return 90.0F;
         case WEST:
            return 0.0F;
         case NORTH:
            return 270.0F;
         case EAST:
            return 180.0F;
         }
      }

      return 0.0F;
   }

   public boolean func_70608_bn() {
      return this.field_71083_bS;
   }

   public boolean func_71026_bH() {
      return this.field_71083_bS && this.field_71076_b >= 100;
   }

   public int func_71060_bI() {
      return this.field_71076_b;
   }

   public void func_146105_b(ITextComponent var1, boolean var2) {
   }

   public BlockPos func_180470_cg() {
      return this.field_71077_c;
   }

   public boolean func_82245_bX() {
      return this.field_82248_d;
   }

   public void func_180473_a(BlockPos var1, boolean var2) {
      if (var1 != null) {
         this.field_71077_c = var1;
         this.field_82248_d = var2;
      } else {
         this.field_71077_c = null;
         this.field_82248_d = false;
      }

   }

   public void func_195066_a(ResourceLocation var1) {
      this.func_71029_a(StatList.field_199092_j.func_199076_b(var1));
   }

   public void func_195067_a(ResourceLocation var1, int var2) {
      this.func_71064_a(StatList.field_199092_j.func_199076_b(var1), var2);
   }

   public void func_71029_a(Stat<?> var1) {
      this.func_71064_a(var1, 1);
   }

   public void func_71064_a(Stat<?> var1, int var2) {
   }

   public void func_175145_a(Stat<?> var1) {
   }

   public int func_195065_a(Collection<IRecipe> var1) {
      return 0;
   }

   public void func_193102_a(ResourceLocation[] var1) {
   }

   public int func_195069_b(Collection<IRecipe> var1) {
      return 0;
   }

   public void func_70664_aZ() {
      super.func_70664_aZ();
      this.func_195066_a(StatList.field_75953_u);
      if (this.func_70051_ag()) {
         this.func_71020_j(0.2F);
      } else {
         this.func_71020_j(0.05F);
      }

   }

   public void func_191986_a(float var1, float var2, float var3) {
      double var4 = this.field_70165_t;
      double var6 = this.field_70163_u;
      double var8 = this.field_70161_v;
      double var10;
      if (this.func_203007_ba() && !this.func_184218_aH()) {
         var10 = this.func_70040_Z().field_72448_b;
         double var12 = var10 < -0.2D ? 0.085D : 0.06D;
         if (var10 <= 0.0D || this.field_70703_bu || !this.field_70170_p.func_180495_p(new BlockPos(this.field_70165_t, this.field_70163_u + 1.0D - 0.1D, this.field_70161_v)).func_204520_s().func_206888_e()) {
            this.field_70181_x += (var10 - this.field_70181_x) * var12;
         }
      }

      if (this.field_71075_bZ.field_75100_b && !this.func_184218_aH()) {
         var10 = this.field_70181_x;
         float var14 = this.field_70747_aH;
         this.field_70747_aH = this.field_71075_bZ.func_75093_a() * (float)(this.func_70051_ag() ? 2 : 1);
         super.func_191986_a(var1, var2, var3);
         this.field_70181_x = var10 * 0.6D;
         this.field_70747_aH = var14;
         this.field_70143_R = 0.0F;
         this.func_70052_a(7, false);
      } else {
         super.func_191986_a(var1, var2, var3);
      }

      this.func_71000_j(this.field_70165_t - var4, this.field_70163_u - var6, this.field_70161_v - var8);
   }

   public void func_205343_av() {
      if (this.field_71075_bZ.field_75100_b) {
         this.func_204711_a(false);
      } else {
         super.func_205343_av();
      }

   }

   protected boolean func_207402_f(BlockPos var1) {
      return this.func_207401_g(var1) && !this.field_70170_p.func_180495_p(var1.func_177984_a()).func_185915_l();
   }

   protected boolean func_207401_g(BlockPos var1) {
      return !this.field_70170_p.func_180495_p(var1).func_185915_l();
   }

   public float func_70689_ay() {
      return (float)this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e();
   }

   public void func_71000_j(double var1, double var3, double var5) {
      if (!this.func_184218_aH()) {
         int var7;
         if (this.func_203007_ba()) {
            var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               this.func_195067_a(StatList.field_75946_m, var7);
               this.func_71020_j(0.01F * (float)var7 * 0.01F);
            }
         } else if (this.func_208600_a(FluidTags.field_206959_a)) {
            var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               this.func_195067_a(StatList.field_211756_w, var7);
               this.func_71020_j(0.01F * (float)var7 * 0.01F);
            }
         } else if (this.func_70090_H()) {
            var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               this.func_195067_a(StatList.field_211755_s, var7);
               this.func_71020_j(0.01F * (float)var7 * 0.01F);
            }
         } else if (this.func_70617_f_()) {
            if (var3 > 0.0D) {
               this.func_195067_a(StatList.field_188103_o, (int)Math.round(var3 * 100.0D));
            }
         } else if (this.field_70122_E) {
            var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               if (this.func_70051_ag()) {
                  this.func_195067_a(StatList.field_188102_l, var7);
                  this.func_71020_j(0.1F * (float)var7 * 0.01F);
               } else if (this.func_70093_af()) {
                  this.func_195067_a(StatList.field_188101_k, var7);
                  this.func_71020_j(0.0F * (float)var7 * 0.01F);
               } else {
                  this.func_195067_a(StatList.field_188100_j, var7);
                  this.func_71020_j(0.0F * (float)var7 * 0.01F);
               }
            }
         } else if (this.func_184613_cA()) {
            var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            this.func_195067_a(StatList.field_188110_v, var7);
         } else {
            var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var5 * var5) * 100.0F);
            if (var7 > 25) {
               this.func_195067_a(StatList.field_188104_p, var7);
            }
         }

      }
   }

   private void func_71015_k(double var1, double var3, double var5) {
      if (this.func_184218_aH()) {
         int var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
         if (var7 > 0) {
            if (this.func_184187_bx() instanceof EntityMinecart) {
               this.func_195067_a(StatList.field_188106_r, var7);
            } else if (this.func_184187_bx() instanceof EntityBoat) {
               this.func_195067_a(StatList.field_188107_s, var7);
            } else if (this.func_184187_bx() instanceof EntityPig) {
               this.func_195067_a(StatList.field_188108_t, var7);
            } else if (this.func_184187_bx() instanceof AbstractHorse) {
               this.func_195067_a(StatList.field_188109_u, var7);
            }
         }
      }

   }

   public void func_180430_e(float var1, float var2) {
      if (!this.field_71075_bZ.field_75101_c) {
         if (var1 >= 2.0F) {
            this.func_195067_a(StatList.field_75943_n, (int)Math.round((double)var1 * 100.0D));
         }

         super.func_180430_e(var1, var2);
      }
   }

   protected void func_71061_d_() {
      if (!this.func_175149_v()) {
         super.func_71061_d_();
      }

   }

   protected SoundEvent func_184588_d(int var1) {
      return var1 > 4 ? SoundEvents.field_187736_dY : SoundEvents.field_187804_ed;
   }

   public void func_70074_a(EntityLivingBase var1) {
      this.func_71029_a(StatList.field_199090_h.func_199076_b(var1.func_200600_R()));
   }

   public void func_70110_aj() {
      if (!this.field_71075_bZ.field_75100_b) {
         super.func_70110_aj();
      }

   }

   public void func_195068_e(int var1) {
      this.func_85039_t(var1);
      this.field_71106_cc += (float)var1 / (float)this.func_71050_bK();
      this.field_71067_cb = MathHelper.func_76125_a(this.field_71067_cb + var1, 0, 2147483647);

      while(this.field_71106_cc < 0.0F) {
         float var2 = this.field_71106_cc * (float)this.func_71050_bK();
         if (this.field_71068_ca > 0) {
            this.func_82242_a(-1);
            this.field_71106_cc = 1.0F + var2 / (float)this.func_71050_bK();
         } else {
            this.func_82242_a(-1);
            this.field_71106_cc = 0.0F;
         }
      }

      while(this.field_71106_cc >= 1.0F) {
         this.field_71106_cc = (this.field_71106_cc - 1.0F) * (float)this.func_71050_bK();
         this.func_82242_a(1);
         this.field_71106_cc /= (float)this.func_71050_bK();
      }

   }

   public int func_175138_ci() {
      return this.field_175152_f;
   }

   public void func_192024_a(ItemStack var1, int var2) {
      this.field_71068_ca -= var2;
      if (this.field_71068_ca < 0) {
         this.field_71068_ca = 0;
         this.field_71106_cc = 0.0F;
         this.field_71067_cb = 0;
      }

      this.field_175152_f = this.field_70146_Z.nextInt();
   }

   public void func_82242_a(int var1) {
      this.field_71068_ca += var1;
      if (this.field_71068_ca < 0) {
         this.field_71068_ca = 0;
         this.field_71106_cc = 0.0F;
         this.field_71067_cb = 0;
      }

      if (var1 > 0 && this.field_71068_ca % 5 == 0 && (float)this.field_82249_h < (float)this.field_70173_aa - 100.0F) {
         float var2 = this.field_71068_ca > 30 ? 1.0F : (float)this.field_71068_ca / 30.0F;
         this.field_70170_p.func_184148_a((EntityPlayer)null, this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_187802_ec, this.func_184176_by(), var2 * 0.75F, 1.0F);
         this.field_82249_h = this.field_70173_aa;
      }

   }

   public int func_71050_bK() {
      if (this.field_71068_ca >= 30) {
         return 112 + (this.field_71068_ca - 30) * 9;
      } else {
         return this.field_71068_ca >= 15 ? 37 + (this.field_71068_ca - 15) * 5 : 7 + this.field_71068_ca * 2;
      }
   }

   public void func_71020_j(float var1) {
      if (!this.field_71075_bZ.field_75102_a) {
         if (!this.field_70170_p.field_72995_K) {
            this.field_71100_bB.func_75113_a(var1);
         }

      }
   }

   public FoodStats func_71024_bL() {
      return this.field_71100_bB;
   }

   public boolean func_71043_e(boolean var1) {
      return !this.field_71075_bZ.field_75102_a && (var1 || this.field_71100_bB.func_75121_c());
   }

   public boolean func_70996_bM() {
      return this.func_110143_aJ() > 0.0F && this.func_110143_aJ() < this.func_110138_aP();
   }

   public boolean func_175142_cm() {
      return this.field_71075_bZ.field_75099_e;
   }

   public boolean func_175151_a(BlockPos var1, EnumFacing var2, ItemStack var3) {
      if (this.field_71075_bZ.field_75099_e) {
         return true;
      } else {
         BlockPos var4 = var1.func_177972_a(var2.func_176734_d());
         BlockWorldState var5 = new BlockWorldState(this.field_70170_p, var4, false);
         return var3.func_206847_b(this.field_70170_p.func_205772_D(), var5);
      }
   }

   protected int func_70693_a(EntityPlayer var1) {
      if (!this.field_70170_p.func_82736_K().func_82766_b("keepInventory") && !this.func_175149_v()) {
         int var2 = this.field_71068_ca * 7;
         return var2 > 100 ? 100 : var2;
      } else {
         return 0;
      }
   }

   protected boolean func_70684_aJ() {
      return true;
   }

   public boolean func_94059_bO() {
      return true;
   }

   protected boolean func_70041_e_() {
      return !this.field_71075_bZ.field_75100_b;
   }

   public void func_71016_p() {
   }

   public void func_71033_a(GameType var1) {
   }

   public ITextComponent func_200200_C_() {
      return new TextComponentString(this.field_146106_i.getName());
   }

   public InventoryEnderChest func_71005_bN() {
      return this.field_71078_a;
   }

   public ItemStack func_184582_a(EntityEquipmentSlot var1) {
      if (var1 == EntityEquipmentSlot.MAINHAND) {
         return this.field_71071_by.func_70448_g();
      } else if (var1 == EntityEquipmentSlot.OFFHAND) {
         return (ItemStack)this.field_71071_by.field_184439_c.get(0);
      } else {
         return var1.func_188453_a() == EntityEquipmentSlot.Type.ARMOR ? (ItemStack)this.field_71071_by.field_70460_b.get(var1.func_188454_b()) : ItemStack.field_190927_a;
      }
   }

   public void func_184201_a(EntityEquipmentSlot var1, ItemStack var2) {
      if (var1 == EntityEquipmentSlot.MAINHAND) {
         this.func_184606_a_(var2);
         this.field_71071_by.field_70462_a.set(this.field_71071_by.field_70461_c, var2);
      } else if (var1 == EntityEquipmentSlot.OFFHAND) {
         this.func_184606_a_(var2);
         this.field_71071_by.field_184439_c.set(0, var2);
      } else if (var1.func_188453_a() == EntityEquipmentSlot.Type.ARMOR) {
         this.func_184606_a_(var2);
         this.field_71071_by.field_70460_b.set(var1.func_188454_b(), var2);
      }

   }

   public boolean func_191521_c(ItemStack var1) {
      this.func_184606_a_(var1);
      return this.field_71071_by.func_70441_a(var1);
   }

   public Iterable<ItemStack> func_184214_aD() {
      return Lists.newArrayList(new ItemStack[]{this.func_184614_ca(), this.func_184592_cb()});
   }

   public Iterable<ItemStack> func_184193_aE() {
      return this.field_71071_by.field_70460_b;
   }

   public boolean func_192027_g(NBTTagCompound var1) {
      if (!this.func_184218_aH() && this.field_70122_E && !this.func_70090_H()) {
         if (this.func_192023_dk().isEmpty()) {
            this.func_192029_h(var1);
            return true;
         } else if (this.func_192025_dl().isEmpty()) {
            this.func_192031_i(var1);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   protected void func_192030_dh() {
      this.func_192026_k(this.func_192023_dk());
      this.func_192029_h(new NBTTagCompound());
      this.func_192026_k(this.func_192025_dl());
      this.func_192031_i(new NBTTagCompound());
   }

   private void func_192026_k(@Nullable NBTTagCompound var1) {
      if (!this.field_70170_p.field_72995_K && !var1.isEmpty()) {
         Entity var2 = EntityType.func_200716_a(var1, this.field_70170_p);
         if (var2 instanceof EntityTameable) {
            ((EntityTameable)var2).func_184754_b(this.field_96093_i);
         }

         var2.func_70107_b(this.field_70165_t, this.field_70163_u + 0.699999988079071D, this.field_70161_v);
         this.field_70170_p.func_72838_d(var2);
      }

   }

   public boolean func_98034_c(EntityPlayer var1) {
      if (!this.func_82150_aj()) {
         return false;
      } else if (var1.func_175149_v()) {
         return false;
      } else {
         Team var2 = this.func_96124_cp();
         return var2 == null || var1 == null || var1.func_96124_cp() != var2 || !var2.func_98297_h();
      }
   }

   public abstract boolean func_175149_v();

   public boolean func_203007_ba() {
      return !this.field_71075_bZ.field_75100_b && !this.func_175149_v() && super.func_203007_ba();
   }

   public abstract boolean func_184812_l_();

   public boolean func_96092_aw() {
      return !this.field_71075_bZ.field_75100_b;
   }

   public Scoreboard func_96123_co() {
      return this.field_70170_p.func_96441_U();
   }

   public ITextComponent func_145748_c_() {
      ITextComponent var1 = ScorePlayerTeam.func_200541_a(this.func_96124_cp(), this.func_200200_C_());
      return this.func_208016_c(var1);
   }

   public ITextComponent func_208017_dF() {
      return (new TextComponentString("")).func_150257_a(this.func_200200_C_()).func_150258_a(" (").func_150258_a(this.field_146106_i.getId().toString()).func_150258_a(")");
   }

   private ITextComponent func_208016_c(ITextComponent var1) {
      String var2 = this.func_146103_bH().getName();
      return var1.func_211710_a((var2x) -> {
         var2x.func_150241_a(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tell " + var2 + " ")).func_150209_a(this.func_174823_aP()).func_179989_a(var2);
      });
   }

   public String func_195047_I_() {
      return this.func_146103_bH().getName();
   }

   public float func_70047_e() {
      float var1 = 1.62F;
      if (this.func_70608_bn()) {
         var1 = 0.2F;
      } else if (!this.func_203007_ba() && !this.func_184613_cA() && this.field_70131_O != 0.6F) {
         if (this.func_70093_af() || this.field_70131_O == 1.65F) {
            var1 -= 0.08F;
         }
      } else {
         var1 = 0.4F;
      }

      return var1;
   }

   public void func_110149_m(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.func_184212_Q().func_187227_b(field_184829_a, var1);
   }

   public float func_110139_bj() {
      return (Float)this.func_184212_Q().func_187225_a(field_184829_a);
   }

   public static UUID func_146094_a(GameProfile var0) {
      UUID var1 = var0.getId();
      if (var1 == null) {
         var1 = func_175147_b(var0.getName());
      }

      return var1;
   }

   public static UUID func_175147_b(String var0) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0).getBytes(StandardCharsets.UTF_8));
   }

   public boolean func_175146_a(LockCode var1) {
      if (var1.func_180160_a()) {
         return true;
      } else {
         ItemStack var2 = this.func_184614_ca();
         return !var2.func_190926_b() && var2.func_82837_s() ? var2.func_200301_q().getString().equals(var1.func_180159_b()) : false;
      }
   }

   public boolean func_175148_a(EnumPlayerModelParts var1) {
      return ((Byte)this.func_184212_Q().func_187225_a(field_184827_bp) & var1.func_179327_a()) == var1.func_179327_a();
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      if (var1 >= 0 && var1 < this.field_71071_by.field_70462_a.size()) {
         this.field_71071_by.func_70299_a(var1, var2);
         return true;
      } else {
         EntityEquipmentSlot var3;
         if (var1 == 100 + EntityEquipmentSlot.HEAD.func_188454_b()) {
            var3 = EntityEquipmentSlot.HEAD;
         } else if (var1 == 100 + EntityEquipmentSlot.CHEST.func_188454_b()) {
            var3 = EntityEquipmentSlot.CHEST;
         } else if (var1 == 100 + EntityEquipmentSlot.LEGS.func_188454_b()) {
            var3 = EntityEquipmentSlot.LEGS;
         } else if (var1 == 100 + EntityEquipmentSlot.FEET.func_188454_b()) {
            var3 = EntityEquipmentSlot.FEET;
         } else {
            var3 = null;
         }

         if (var1 == 98) {
            this.func_184201_a(EntityEquipmentSlot.MAINHAND, var2);
            return true;
         } else if (var1 == 99) {
            this.func_184201_a(EntityEquipmentSlot.OFFHAND, var2);
            return true;
         } else if (var3 == null) {
            int var4 = var1 - 200;
            if (var4 >= 0 && var4 < this.field_71078_a.func_70302_i_()) {
               this.field_71078_a.func_70299_a(var4, var2);
               return true;
            } else {
               return false;
            }
         } else {
            if (!var2.func_190926_b()) {
               if (!(var2.func_77973_b() instanceof ItemArmor) && !(var2.func_77973_b() instanceof ItemElytra)) {
                  if (var3 != EntityEquipmentSlot.HEAD) {
                     return false;
                  }
               } else if (EntityLiving.func_184640_d(var2) != var3) {
                  return false;
               }
            }

            this.field_71071_by.func_70299_a(var3.func_188454_b() + this.field_71071_by.field_70462_a.size(), var2);
            return true;
         }
      }
   }

   public boolean func_175140_cp() {
      return this.field_175153_bG;
   }

   public void func_175150_k(boolean var1) {
      this.field_175153_bG = var1;
   }

   public EnumHandSide func_184591_cq() {
      return (Byte)this.field_70180_af.func_187225_a(field_184828_bq) == 0 ? EnumHandSide.LEFT : EnumHandSide.RIGHT;
   }

   public void func_184819_a(EnumHandSide var1) {
      this.field_70180_af.func_187227_b(field_184828_bq, (byte)(var1 == EnumHandSide.LEFT ? 0 : 1));
   }

   public NBTTagCompound func_192023_dk() {
      return (NBTTagCompound)this.field_70180_af.func_187225_a(field_192032_bt);
   }

   protected void func_192029_h(NBTTagCompound var1) {
      this.field_70180_af.func_187227_b(field_192032_bt, var1);
   }

   public NBTTagCompound func_192025_dl() {
      return (NBTTagCompound)this.field_70180_af.func_187225_a(field_192033_bu);
   }

   protected void func_192031_i(NBTTagCompound var1) {
      this.field_70180_af.func_187227_b(field_192033_bu, var1);
   }

   public float func_184818_cX() {
      return (float)(1.0D / this.func_110148_a(SharedMonsterAttributes.field_188790_f).func_111126_e() * 20.0D);
   }

   public float func_184825_o(float var1) {
      return MathHelper.func_76131_a(((float)this.field_184617_aD + var1) / this.func_184818_cX(), 0.0F, 1.0F);
   }

   public void func_184821_cY() {
      this.field_184617_aD = 0;
   }

   public CooldownTracker func_184811_cZ() {
      return this.field_184832_bU;
   }

   public void func_70108_f(Entity var1) {
      if (!this.func_70608_bn()) {
         super.func_70108_f(var1);
      }

   }

   public float func_184817_da() {
      return (float)this.func_110148_a(SharedMonsterAttributes.field_188792_h).func_111126_e();
   }

   public boolean func_195070_dx() {
      return this.field_71075_bZ.field_75098_d && this.func_184840_I() >= 2;
   }

   static {
      field_184829_a = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_187193_c);
      field_184830_b = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_187192_b);
      field_184827_bp = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_187191_a);
      field_184828_bq = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_187191_a);
      field_192032_bt = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_192734_n);
      field_192033_bu = EntityDataManager.func_187226_a(EntityPlayer.class, DataSerializers.field_192734_n);
   }

   static class SleepEnemyPredicate implements Predicate<EntityMob> {
      private final EntityPlayer field_192387_a;

      private SleepEnemyPredicate(EntityPlayer var1) {
         super();
         this.field_192387_a = var1;
      }

      public boolean test(@Nullable EntityMob var1) {
         return var1.func_191990_c(this.field_192387_a);
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((EntityMob)var1);
      }

      // $FF: synthetic method
      SleepEnemyPredicate(EntityPlayer var1, Object var2) {
         this(var1);
      }
   }

   public static enum SleepResult {
      OK,
      NOT_POSSIBLE_HERE,
      NOT_POSSIBLE_NOW,
      TOO_FAR_AWAY,
      OTHER_PROBLEM,
      NOT_SAFE;

      private SleepResult() {
      }
   }

   public static enum EnumChatVisibility {
      FULL(0, "options.chat.visibility.full"),
      SYSTEM(1, "options.chat.visibility.system"),
      HIDDEN(2, "options.chat.visibility.hidden");

      private static final EntityPlayer.EnumChatVisibility[] field_151432_d = (EntityPlayer.EnumChatVisibility[])Arrays.stream(values()).sorted(Comparator.comparingInt(EntityPlayer.EnumChatVisibility::func_151428_a)).toArray((var0) -> {
         return new EntityPlayer.EnumChatVisibility[var0];
      });
      private final int field_151433_e;
      private final String field_151430_f;

      private EnumChatVisibility(int var3, String var4) {
         this.field_151433_e = var3;
         this.field_151430_f = var4;
      }

      public int func_151428_a() {
         return this.field_151433_e;
      }

      public static EntityPlayer.EnumChatVisibility func_151426_a(int var0) {
         return field_151432_d[var0 % field_151432_d.length];
      }

      public String func_151429_b() {
         return this.field_151430_f;
      }
   }
}
