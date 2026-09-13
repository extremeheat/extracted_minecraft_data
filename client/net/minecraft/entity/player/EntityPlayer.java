package net.minecraft.entity.player;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList$EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent$Action;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.chunk.IChunkProvider;

public abstract class EntityPlayer extends EntityLivingBase implements ICommandSender {
   public InventoryPlayer field_71071_by = new InventoryPlayer(this);
   private InventoryEnderChest field_71078_a = new InventoryEnderChest();
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
   public ChunkCoordinates field_71081_bT;
   private int field_71076_b;
   public float field_71079_bU;
   public float field_71082_cx;
   public float field_71089_bV;
   private ChunkCoordinates field_71077_c;
   private boolean field_82248_d;
   private ChunkCoordinates field_71073_d;
   public PlayerCapabilities field_71075_bZ = new PlayerCapabilities();
   public int field_71068_ca;
   public int field_71067_cb;
   public float field_71106_cc;
   private ItemStack field_71074_e;
   private int field_71072_f;
   protected float field_71108_cd = 0.1F;
   protected float field_71102_ce = 0.02F;
   private int field_82249_h;
   private final GameProfile field_146106_i;
   public EntityFishHook field_71104_cf;

   public EntityPlayer(World var1, GameProfile var2) {
      super(var1);
      this.field_96093_i = func_146094_a(var2);
      this.field_146106_i = var2;
      this.field_71069_bz = new ContainerPlayer(this.field_71071_by, !var1.field_72995_K, this);
      this.field_71070_bA = this.field_71069_bz;
      this.field_70129_M = 1.62F;
      ChunkCoordinates var3 = var1.func_72861_E();
      this.func_70012_b((double)var3.field_71574_a + 0.5, (double)(var3.field_71572_b + 1), (double)var3.field_71573_c + 0.5, 0.0F, 0.0F);
      this.field_70741_aB = 180.0F;
      this.field_70174_ab = 20;
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e).func_111128_a(1.0);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, (byte)0);
      this.field_70180_af.func_75682_a(17, 0.0F);
      this.field_70180_af.func_75682_a(18, 0);
   }

   public ItemStack func_71011_bu() {
      return this.field_71074_e;
   }

   public int func_71052_bv() {
      return this.field_71072_f;
   }

   public boolean func_71039_bw() {
      return this.field_71074_e != null;
   }

   public int func_71057_bx() {
      return this.func_71039_bw() ? this.field_71074_e.func_77988_m() - this.field_71072_f : 0;
   }

   public void func_71034_by() {
      if (this.field_71074_e != null) {
         this.field_71074_e.func_77974_b(this.field_70170_p, this, this.field_71072_f);
      }

      this.func_71041_bz();
   }

   public void func_71041_bz() {
      this.field_71074_e = null;
      this.field_71072_f = 0;
      if (!this.field_70170_p.field_72995_K) {
         this.func_70019_c(false);
      }
   }

   public boolean func_70632_aY() {
      return this.func_71039_bw() && this.field_71074_e.func_77973_b().func_77661_b(this.field_71074_e) == EnumAction.block;
   }

   @Override
   public void func_70071_h_() {
      if (this.field_71074_e != null) {
         ItemStack var1 = this.field_71071_by.func_70448_g();
         if (var1 == this.field_71074_e) {
            if (this.field_71072_f <= 25 && this.field_71072_f % 4 == 0) {
               this.func_71010_c(var1, 5);
            }

            if (--this.field_71072_f == 0 && !this.field_70170_p.field_72995_K) {
               this.func_71036_o();
            }
         } else {
            this.func_71041_bz();
         }
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
            if (!this.func_71065_l()) {
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

      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K && this.field_71070_bA != null && !this.field_71070_bA.func_75145_c(this)) {
         this.func_71053_j();
         this.field_71070_bA = this.field_71069_bz;
      }

      if (this.func_70027_ad() && this.field_71075_bZ.field_75102_a) {
         this.func_70066_B();
      }

      this.field_71091_bM = this.field_71094_bP;
      this.field_71096_bN = this.field_71095_bQ;
      this.field_71097_bO = this.field_71085_bR;
      double var9 = this.field_70165_t - this.field_71094_bP;
      double var3 = this.field_70163_u - this.field_71095_bQ;
      double var5 = this.field_70161_v - this.field_71085_bR;
      double var7 = 10.0;
      if (var9 > var7) {
         this.field_71091_bM = this.field_71094_bP = this.field_70165_t;
      }

      if (var5 > var7) {
         this.field_71097_bO = this.field_71085_bR = this.field_70161_v;
      }

      if (var3 > var7) {
         this.field_71096_bN = this.field_71095_bQ = this.field_70163_u;
      }

      if (var9 < -var7) {
         this.field_71091_bM = this.field_71094_bP = this.field_70165_t;
      }

      if (var5 < -var7) {
         this.field_71097_bO = this.field_71085_bR = this.field_70161_v;
      }

      if (var3 < -var7) {
         this.field_71096_bN = this.field_71095_bQ = this.field_70163_u;
      }

      this.field_71094_bP += var9 * 0.25;
      this.field_71085_bR += var5 * 0.25;
      this.field_71095_bQ += var3 * 0.25;
      if (this.field_70154_o == null) {
         this.field_71073_d = null;
      }

      if (!this.field_70170_p.field_72995_K) {
         this.field_71100_bB.func_75118_a(this);
         this.func_71064_a(StatList.field_75948_k, 1);
      }
   }

   @Override
   public int func_82145_z() {
      return this.field_71075_bZ.field_75102_a ? 0 : 80;
   }

   @Override
   protected String func_145776_H() {
      return "game.player.swim";
   }

   @Override
   protected String func_145777_O() {
      return "game.player.swim.splash";
   }

   @Override
   public int func_82147_ab() {
      return 10;
   }

   @Override
   public void func_85030_a(String var1, float var2, float var3) {
      this.field_70170_p.func_85173_a(this, var1, var2, var3);
   }

   protected void func_71010_c(ItemStack var1, int var2) {
      if (var1.func_77975_n() == EnumAction.drink) {
         this.func_85030_a("random.drink", 0.5F, this.field_70170_p.field_73012_v.nextFloat() * 0.1F + 0.9F);
      }

      if (var1.func_77975_n() == EnumAction.eat) {
         for(int var3 = 0; var3 < var2; ++var3) {
            Vec3 var4 = Vec3.func_72443_a(((double)this.field_70146_Z.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
            var4.func_72440_a(-this.field_70125_A * 3.1415927F / 180.0F);
            var4.func_72442_b(-this.field_70177_z * 3.1415927F / 180.0F);
            Vec3 var5 = Vec3.func_72443_a(((double)this.field_70146_Z.nextFloat() - 0.5) * 0.3, (double)(-this.field_70146_Z.nextFloat()) * 0.6 - 0.3, 0.6);
            var5.func_72440_a(-this.field_70125_A * 3.1415927F / 180.0F);
            var5.func_72442_b(-this.field_70177_z * 3.1415927F / 180.0F);
            var5 = var5.func_72441_c(this.field_70165_t, this.field_70163_u + (double)this.func_70047_e(), this.field_70161_v);
            String var6 = "iconcrack_" + Item.func_150891_b(var1.func_77973_b());
            if (var1.func_77981_g()) {
               var6 = var6 + "_" + var1.func_77960_j();
            }

            this.field_70170_p
               .func_72869_a(
                  var6, var5.field_72450_a, var5.field_72448_b, var5.field_72449_c, var4.field_72450_a, var4.field_72448_b + 0.05, var4.field_72449_c
               );
         }

         this.func_85030_a(
            "random.eat", 0.5F + 0.5F * (float)this.field_70146_Z.nextInt(2), (this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.2F + 1.0F
         );
      }
   }

   protected void func_71036_o() {
      if (this.field_71074_e != null) {
         this.func_71010_c(this.field_71074_e, 16);
         int var1 = this.field_71074_e.field_77994_a;
         ItemStack var2 = this.field_71074_e.func_77950_b(this.field_70170_p, this);
         if (var2 != this.field_71074_e || var2 != null && var2.field_77994_a != var1) {
            this.field_71071_by.field_70462_a[this.field_71071_by.field_70461_c] = var2;
            if (var2.field_77994_a == 0) {
               this.field_71071_by.field_70462_a[this.field_71071_by.field_70461_c] = null;
            }
         }

         this.func_71041_bz();
      }
   }

   @Override
   public void func_70103_a(byte var1) {
      if (var1 == 9) {
         this.func_71036_o();
      } else {
         super.func_70103_a(var1);
      }
   }

   @Override
   protected boolean func_70610_aX() {
      return this.func_110143_aJ() <= 0.0F || this.func_70608_bn();
   }

   protected void func_71053_j() {
      this.field_71070_bA = this.field_71069_bz;
   }

   @Override
   public void func_70078_a(Entity var1) {
      if (this.field_70154_o != null && var1 == null) {
         if (!this.field_70170_p.field_72995_K) {
            this.func_110145_l(this.field_70154_o);
         }

         if (this.field_70154_o != null) {
            this.field_70154_o.field_70153_n = null;
         }

         this.field_70154_o = null;
      } else {
         super.func_70078_a(var1);
      }
   }

   @Override
   public void func_70098_U() {
      if (!this.field_70170_p.field_72995_K && this.func_70093_af()) {
         this.func_70078_a(null);
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
         if (this.field_70154_o instanceof EntityPig) {
            this.field_70125_A = var8;
            this.field_70177_z = var7;
            this.field_70761_aq = ((EntityPig)this.field_70154_o).field_70761_aq;
         }
      }
   }

   @Override
   public void func_70065_x() {
      this.field_70129_M = 1.62F;
      this.func_70105_a(0.6F, 1.8F);
      super.func_70065_x();
      this.func_70606_j(this.func_110138_aP());
      this.field_70725_aQ = 0;
   }

   @Override
   protected void func_70626_be() {
      super.func_70626_be();
      this.func_82168_bl();
   }

   @Override
   public void func_70636_d() {
      if (this.field_71101_bC > 0) {
         --this.field_71101_bC;
      }

      if (this.field_70170_p.field_73013_u == EnumDifficulty.PEACEFUL
         && this.func_110143_aJ() < this.func_110138_aP()
         && this.field_70170_p.func_82736_K().func_82766_b("naturalRegeneration")
         && this.field_70173_aa % 20 * 12 == 0) {
         this.func_70691_i(1.0F);
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
         this.field_70747_aH = (float)((double)this.field_70747_aH + (double)this.field_71102_ce * 0.3);
      }

      this.func_70659_e((float)var1.func_111126_e());
      float var2 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      float var3 = (float)Math.atan(-this.field_70181_x * 0.20000000298023224) * 15.0F;
      if (var2 > 0.1F) {
         var2 = 0.1F;
      }

      if (!this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
         var2 = 0.0F;
      }

      if (this.field_70122_E || this.func_110143_aJ() <= 0.0F) {
         var3 = 0.0F;
      }

      this.field_71109_bG += (var2 - this.field_71109_bG) * 0.4F;
      this.field_70726_aT += (var3 - this.field_70726_aT) * 0.8F;
      if (this.func_110143_aJ() > 0.0F) {
         Object var4 = null;
         AxisAlignedBB var8;
         if (this.field_70154_o != null && !this.field_70154_o.field_70128_L) {
            var8 = this.field_70121_D.func_111270_a(this.field_70154_o.field_70121_D).func_72314_b(1.0, 0.0, 1.0);
         } else {
            var8 = this.field_70121_D.func_72314_b(1.0, 0.5, 1.0);
         }

         List var5 = this.field_70170_p.func_72839_b(this, var8);
         if (var5 != null) {
            for(int var6 = 0; var6 < var5.size(); ++var6) {
               Entity var7 = (Entity)var5.get(var6);
               if (!var7.field_70128_L) {
                  this.func_71044_o(var7);
               }
            }
         }
      }
   }

   private void func_71044_o(Entity var1) {
      var1.func_70100_b_(this);
   }

   public int func_71037_bA() {
      return this.field_70180_af.func_75679_c(18);
   }

   public void func_85040_s(int var1) {
      this.field_70180_af.func_75692_b(18, var1);
   }

   public void func_85039_t(int var1) {
      int var2 = this.func_71037_bA();
      this.field_70180_af.func_75692_b(18, var2 + var1);
   }

   @Override
   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      this.func_70105_a(0.2F, 0.2F);
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.field_70181_x = 0.10000000149011612;
      if (this.func_70005_c_().equals("Notch")) {
         this.func_146097_a(new ItemStack(Items.field_151034_e, 1), true, false);
      }

      if (!this.field_70170_p.func_82736_K().func_82766_b("keepInventory")) {
         this.field_71071_by.func_70436_m();
      }

      if (var1 != null) {
         this.field_70159_w = (double)(-MathHelper.func_76134_b((this.field_70739_aP + this.field_70177_z) * 3.1415927F / 180.0F) * 0.1F);
         this.field_70179_y = (double)(-MathHelper.func_76126_a((this.field_70739_aP + this.field_70177_z) * 3.1415927F / 180.0F) * 0.1F);
      } else {
         this.field_70159_w = this.field_70179_y = 0.0;
      }

      this.field_70129_M = 0.1F;
      this.func_71064_a(StatList.field_75960_y, 1);
   }

   @Override
   protected String func_70621_aR() {
      return "game.player.hurt";
   }

   @Override
   protected String func_70673_aS() {
      return "game.player.die";
   }

   @Override
   public void func_70084_c(Entity var1, int var2) {
      this.func_85039_t(var2);
      Collection var3 = this.func_96123_co().func_96520_a(IScoreObjectiveCriteria.field_96640_e);
      if (var1 instanceof EntityPlayer) {
         this.func_71064_a(StatList.field_75932_A, 1);
         var3.addAll(this.func_96123_co().func_96520_a(IScoreObjectiveCriteria.field_96639_d));
      } else {
         this.func_71064_a(StatList.field_75959_z, 1);
      }

      for(ScoreObjective var5 : var3) {
         Score var6 = this.func_96123_co().func_96529_a(this.func_70005_c_(), var5);
         var6.func_96648_a();
      }
   }

   public EntityItem func_71040_bB(boolean var1) {
      return this.func_146097_a(
         this.field_71071_by
            .func_70298_a(
               this.field_71071_by.field_70461_c, var1 && this.field_71071_by.func_70448_g() != null ? this.field_71071_by.func_70448_g().field_77994_a : 1
            ),
         false,
         true
      );
   }

   public EntityItem func_71019_a(ItemStack var1, boolean var2) {
      return this.func_146097_a(var1, false, false);
   }

   public EntityItem func_146097_a(ItemStack var1, boolean var2, boolean var3) {
      if (var1 == null) {
         return null;
      } else if (var1.field_77994_a == 0) {
         return null;
      } else {
         EntityItem var4 = new EntityItem(
            this.field_70170_p, this.field_70165_t, this.field_70163_u - 0.30000001192092896 + (double)this.func_70047_e(), this.field_70161_v, var1
         );
         var4.field_145804_b = 40;
         if (var3) {
            var4.func_145799_b(this.func_70005_c_());
         }

         float var5 = 0.1F;
         if (var2) {
            float var6 = this.field_70146_Z.nextFloat() * 0.5F;
            float var7 = this.field_70146_Z.nextFloat() * 3.1415927F * 2.0F;
            var4.field_70159_w = (double)(-MathHelper.func_76126_a(var7) * var6);
            var4.field_70179_y = (double)(MathHelper.func_76134_b(var7) * var6);
            var4.field_70181_x = 0.20000000298023224;
         } else {
            var5 = 0.3F;
            var4.field_70159_w = (double)(
               -MathHelper.func_76126_a(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F) * var5
            );
            var4.field_70179_y = (double)(
               MathHelper.func_76134_b(this.field_70177_z / 180.0F * 3.1415927F) * MathHelper.func_76134_b(this.field_70125_A / 180.0F * 3.1415927F) * var5
            );
            var4.field_70181_x = (double)(-MathHelper.func_76126_a(this.field_70125_A / 180.0F * 3.1415927F) * var5 + 0.1F);
            var5 = 0.02F;
            float var11 = this.field_70146_Z.nextFloat() * 3.1415927F * 2.0F;
            var5 *= this.field_70146_Z.nextFloat();
            var4.field_70159_w += Math.cos((double)var11) * (double)var5;
            var4.field_70181_x += (double)((this.field_70146_Z.nextFloat() - this.field_70146_Z.nextFloat()) * 0.1F);
            var4.field_70179_y += Math.sin((double)var11) * (double)var5;
         }

         this.func_71012_a(var4);
         this.func_71064_a(StatList.field_75952_v, 1);
         return var4;
      }
   }

   protected void func_71012_a(EntityItem var1) {
      this.field_70170_p.func_72838_d(var1);
   }

   public float func_146096_a(Block var1, boolean var2) {
      float var3 = this.field_71071_by.func_146023_a(var1);
      if (var3 > 1.0F) {
         int var4 = EnchantmentHelper.func_77509_b(this);
         ItemStack var5 = this.field_71071_by.func_70448_g();
         if (var4 > 0 && var5 != null) {
            float var6 = (float)(var4 * var4 + 1);
            if (!var5.func_150998_b(var1) && !(var3 > 1.0F)) {
               var3 += var6 * 0.08F;
            } else {
               var3 += var6;
            }
         }
      }

      if (this.func_70644_a(Potion.field_76422_e)) {
         var3 *= 1.0F + (float)(this.func_70660_b(Potion.field_76422_e).func_76458_c() + 1) * 0.2F;
      }

      if (this.func_70644_a(Potion.field_76419_f)) {
         var3 *= 1.0F - (float)(this.func_70660_b(Potion.field_76419_f).func_76458_c() + 1) * 0.2F;
      }

      if (this.func_70055_a(Material.field_151586_h) && !EnchantmentHelper.func_77510_g(this)) {
         var3 /= 5.0F;
      }

      if (!this.field_70122_E) {
         var3 /= 5.0F;
      }

      return var3;
   }

   public boolean func_146099_a(Block var1) {
      return this.field_71071_by.func_146025_b(var1);
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_96093_i = func_146094_a(this.field_146106_i);
      NBTTagList var2 = var1.func_150295_c("Inventory", 10);
      this.field_71071_by.func_70443_b(var2);
      this.field_71071_by.field_70461_c = var1.func_74762_e("SelectedItemSlot");
      this.field_71083_bS = var1.func_74767_n("Sleeping");
      this.field_71076_b = var1.func_74765_d("SleepTimer");
      this.field_71106_cc = var1.func_74760_g("XpP");
      this.field_71068_ca = var1.func_74762_e("XpLevel");
      this.field_71067_cb = var1.func_74762_e("XpTotal");
      this.func_85040_s(var1.func_74762_e("Score"));
      if (this.field_71083_bS) {
         this.field_71081_bT = new ChunkCoordinates(
            MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
         );
         this.func_70999_a(true, true, false);
      }

      if (var1.func_150297_b("SpawnX", 99) && var1.func_150297_b("SpawnY", 99) && var1.func_150297_b("SpawnZ", 99)) {
         this.field_71077_c = new ChunkCoordinates(var1.func_74762_e("SpawnX"), var1.func_74762_e("SpawnY"), var1.func_74762_e("SpawnZ"));
         this.field_82248_d = var1.func_74767_n("SpawnForced");
      }

      this.field_71100_bB.func_75112_a(var1);
      this.field_71075_bZ.func_75095_b(var1);
      if (var1.func_150297_b("EnderItems", 9)) {
         NBTTagList var3 = var1.func_150295_c("EnderItems", 10);
         this.field_71078_a.func_70486_a(var3);
      }
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74782_a("Inventory", this.field_71071_by.func_70442_a(new NBTTagList()));
      var1.func_74768_a("SelectedItemSlot", this.field_71071_by.field_70461_c);
      var1.func_74757_a("Sleeping", this.field_71083_bS);
      var1.func_74777_a("SleepTimer", (short)this.field_71076_b);
      var1.func_74776_a("XpP", this.field_71106_cc);
      var1.func_74768_a("XpLevel", this.field_71068_ca);
      var1.func_74768_a("XpTotal", this.field_71067_cb);
      var1.func_74768_a("Score", this.func_71037_bA());
      if (this.field_71077_c != null) {
         var1.func_74768_a("SpawnX", this.field_71077_c.field_71574_a);
         var1.func_74768_a("SpawnY", this.field_71077_c.field_71572_b);
         var1.func_74768_a("SpawnZ", this.field_71077_c.field_71573_c);
         var1.func_74757_a("SpawnForced", this.field_82248_d);
      }

      this.field_71100_bB.func_75117_b(var1);
      this.field_71075_bZ.func_75091_a(var1);
      var1.func_74782_a("EnderItems", this.field_71078_a.func_70487_g());
   }

   public void func_71007_a(IInventory var1) {
   }

   public void func_146093_a(TileEntityHopper var1) {
   }

   public void func_96125_a(EntityMinecartHopper var1) {
   }

   public void func_110298_a(EntityHorse var1, IInventory var2) {
   }

   public void func_71002_c(int var1, int var2, int var3, String var4) {
   }

   public void func_82244_d(int var1, int var2, int var3) {
   }

   public void func_71058_b(int var1, int var2, int var3) {
   }

   @Override
   public float func_70047_e() {
      return 0.12F;
   }

   protected void func_71061_d_() {
      this.field_70129_M = 1.62F;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
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

            if (var1.func_76350_n()) {
               if (this.field_70170_p.field_73013_u == EnumDifficulty.PEACEFUL) {
                  var2 = 0.0F;
               }

               if (this.field_70170_p.field_73013_u == EnumDifficulty.EASY) {
                  var2 = var2 / 2.0F + 1.0F;
               }

               if (this.field_70170_p.field_73013_u == EnumDifficulty.HARD) {
                  var2 = var2 * 3.0F / 2.0F;
               }
            }

            if (var2 == 0.0F) {
               return false;
            } else {
               Entity var3 = var1.func_76346_g();
               if (var3 instanceof EntityArrow && ((EntityArrow)var3).field_70250_c != null) {
                  var3 = ((EntityArrow)var3).field_70250_c;
               }

               this.func_71064_a(StatList.field_75961_x, Math.round(var2 * 10.0F));
               return super.func_70097_a(var1, var2);
            }
         }
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

   @Override
   protected void func_70675_k(float var1) {
      this.field_71071_by.func_70449_g(var1);
   }

   @Override
   public int func_70658_aO() {
      return this.field_71071_by.func_70430_l();
   }

   public float func_82243_bO() {
      int var1 = 0;

      for(ItemStack var5 : this.field_71071_by.field_70460_b) {
         if (var5 != null) {
            ++var1;
         }
      }

      return (float)var1 / (float)this.field_71071_by.field_70460_b.length;
   }

   @Override
   protected void func_70665_d(DamageSource var1, float var2) {
      if (!this.func_85032_ar()) {
         if (!var1.func_76363_c() && this.func_70632_aY() && var2 > 0.0F) {
            var2 = (1.0F + var2) * 0.5F;
         }

         var2 = this.func_70655_b(var1, var2);
         var2 = this.func_70672_c(var1, var2);
         float var7 = Math.max(var2 - this.func_110139_bj(), 0.0F);
         this.func_110149_m(this.func_110139_bj() - (var2 - var7));
         if (var7 != 0.0F) {
            this.func_71020_j(var1.func_76345_d());
            float var4 = this.func_110143_aJ();
            this.func_70606_j(this.func_110143_aJ() - var7);
            this.func_110142_aN().func_94547_a(var1, var4, var7);
         }
      }
   }

   public void func_146101_a(TileEntityFurnace var1) {
   }

   public void func_146102_a(TileEntityDispenser var1) {
   }

   public void func_146100_a(TileEntity var1) {
   }

   public void func_146095_a(CommandBlockLogic var1) {
   }

   public void func_146098_a(TileEntityBrewingStand var1) {
   }

   public void func_146104_a(TileEntityBeacon var1) {
   }

   public void func_71030_a(IMerchant var1, String var2) {
   }

   public void func_71048_c(ItemStack var1) {
   }

   public boolean func_70998_m(Entity var1) {
      ItemStack var2 = this.func_71045_bC();
      ItemStack var3 = var2 != null ? var2.func_77946_l() : null;
      if (!var1.func_130002_c(this)) {
         if (var2 != null && var1 instanceof EntityLivingBase) {
            if (this.field_71075_bZ.field_75098_d) {
               var2 = var3;
            }

            if (var2.func_111282_a(this, (EntityLivingBase)var1)) {
               if (var2.field_77994_a <= 0 && !this.field_71075_bZ.field_75098_d) {
                  this.func_71028_bD();
               }

               return true;
            }
         }

         return false;
      } else {
         if (var2 != null && var2 == this.func_71045_bC()) {
            if (var2.field_77994_a <= 0 && !this.field_71075_bZ.field_75098_d) {
               this.func_71028_bD();
            } else if (var2.field_77994_a < var3.field_77994_a && this.field_71075_bZ.field_75098_d) {
               var2.field_77994_a = var3.field_77994_a;
            }
         }

         return true;
      }
   }

   public ItemStack func_71045_bC() {
      return this.field_71071_by.func_70448_g();
   }

   public void func_71028_bD() {
      this.field_71071_by.func_70299_a(this.field_71071_by.field_70461_c, null);
   }

   @Override
   public double func_70033_W() {
      return (double)(this.field_70129_M - 0.5F);
   }

   public void func_71059_n(Entity var1) {
      if (var1.func_70075_an()) {
         if (!var1.func_85031_j(this)) {
            float var2 = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
            int var3 = 0;
            float var4 = 0.0F;
            if (var1 instanceof EntityLivingBase) {
               var4 = EnchantmentHelper.func_77512_a(this, (EntityLivingBase)var1);
               var3 += EnchantmentHelper.func_77507_b(this, (EntityLivingBase)var1);
            }

            if (this.func_70051_ag()) {
               ++var3;
            }

            if (var2 > 0.0F || var4 > 0.0F) {
               boolean var5 = this.field_70143_R > 0.0F
                  && !this.field_70122_E
                  && !this.func_70617_f_()
                  && !this.func_70090_H()
                  && !this.func_70644_a(Potion.field_76440_q)
                  && this.field_70154_o == null
                  && var1 instanceof EntityLivingBase;
               if (var5 && var2 > 0.0F) {
                  var2 *= 1.5F;
               }

               var2 += var4;
               boolean var6 = false;
               int var7 = EnchantmentHelper.func_90036_a(this);
               if (var1 instanceof EntityLivingBase && var7 > 0 && !var1.func_70027_ad()) {
                  var6 = true;
                  var1.func_70015_d(1);
               }

               boolean var8 = var1.func_70097_a(DamageSource.func_76365_a(this), var2);
               if (var8) {
                  if (var3 > 0) {
                     var1.func_70024_g(
                        (double)(-MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F) * (float)var3 * 0.5F),
                        0.1,
                        (double)(MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F) * (float)var3 * 0.5F)
                     );
                     this.field_70159_w *= 0.6;
                     this.field_70179_y *= 0.6;
                     this.func_70031_b(false);
                  }

                  if (var5) {
                     this.func_71009_b(var1);
                  }

                  if (var4 > 0.0F) {
                     this.func_71047_c(var1);
                  }

                  if (var2 >= 18.0F) {
                     this.func_71029_a(AchievementList.field_75999_E);
                  }

                  this.func_130011_c(var1);
                  if (var1 instanceof EntityLivingBase) {
                     EnchantmentHelper.func_151384_a((EntityLivingBase)var1, this);
                  }

                  EnchantmentHelper.func_151385_b(this, var1);
                  ItemStack var9 = this.func_71045_bC();
                  Object var10 = var1;
                  if (var1 instanceof EntityDragonPart) {
                     IEntityMultiPart var11 = ((EntityDragonPart)var1).field_70259_a;
                     if (var11 != null && var11 instanceof EntityLivingBase) {
                        var10 = (EntityLivingBase)var11;
                     }
                  }

                  if (var9 != null && var10 instanceof EntityLivingBase) {
                     var9.func_77961_a((EntityLivingBase)var10, this);
                     if (var9.field_77994_a <= 0) {
                        this.func_71028_bD();
                     }
                  }

                  if (var1 instanceof EntityLivingBase) {
                     this.func_71064_a(StatList.field_75951_w, Math.round(var2 * 10.0F));
                     if (var7 > 0) {
                        var1.func_70015_d(var7 * 4);
                     }
                  }

                  this.func_71020_j(0.3F);
               } else if (var6) {
                  var1.func_70066_B();
               }
            }
         }
      }
   }

   public void func_71009_b(Entity var1) {
   }

   public void func_71047_c(Entity var1) {
   }

   public void func_71004_bE() {
   }

   @Override
   public void func_70106_y() {
      super.func_70106_y();
      this.field_71069_bz.func_75134_a(this);
      if (this.field_71070_bA != null) {
         this.field_71070_bA.func_75134_a(this);
      }
   }

   @Override
   public boolean func_70094_T() {
      return !this.field_71083_bS && super.func_70094_T();
   }

   public GameProfile func_146103_bH() {
      return this.field_146106_i;
   }

   public EntityPlayer$EnumStatus func_71018_a(int var1, int var2, int var3) {
      if (!this.field_70170_p.field_72995_K) {
         if (this.func_70608_bn() || !this.func_70089_S()) {
            return EntityPlayer$EnumStatus.OTHER_PROBLEM;
         }

         if (!this.field_70170_p.field_73011_w.func_76569_d()) {
            return EntityPlayer$EnumStatus.NOT_POSSIBLE_HERE;
         }

         if (this.field_70170_p.func_72935_r()) {
            return EntityPlayer$EnumStatus.NOT_POSSIBLE_NOW;
         }

         if (Math.abs(this.field_70165_t - (double)var1) > 3.0
            || Math.abs(this.field_70163_u - (double)var2) > 2.0
            || Math.abs(this.field_70161_v - (double)var3) > 3.0) {
            return EntityPlayer$EnumStatus.TOO_FAR_AWAY;
         }

         double var4 = 8.0;
         double var6 = 5.0;
         List var8 = this.field_70170_p
            .func_72872_a(
               EntityMob.class,
               AxisAlignedBB.func_72330_a(
                  (double)var1 - var4, (double)var2 - var6, (double)var3 - var4, (double)var1 + var4, (double)var2 + var6, (double)var3 + var4
               )
            );
         if (!var8.isEmpty()) {
            return EntityPlayer$EnumStatus.NOT_SAFE;
         }
      }

      if (this.func_70115_ae()) {
         this.func_70078_a(null);
      }

      this.func_70105_a(0.2F, 0.2F);
      this.field_70129_M = 0.2F;
      if (this.field_70170_p.func_72899_e(var1, var2, var3)) {
         int var9 = this.field_70170_p.func_72805_g(var1, var2, var3);
         int var5 = BlockBed.func_149895_l(var9);
         float var10 = 0.5F;
         float var7 = 0.5F;
         switch(var5) {
            case 0:
               var7 = 0.9F;
               break;
            case 1:
               var10 = 0.1F;
               break;
            case 2:
               var7 = 0.1F;
               break;
            case 3:
               var10 = 0.9F;
         }

         this.func_71013_b(var5);
         this.func_70107_b((double)((float)var1 + var10), (double)((float)var2 + 0.9375F), (double)((float)var3 + var7));
      } else {
         this.func_70107_b((double)((float)var1 + 0.5F), (double)((float)var2 + 0.9375F), (double)((float)var3 + 0.5F));
      }

      this.field_71083_bS = true;
      this.field_71076_b = 0;
      this.field_71081_bT = new ChunkCoordinates(var1, var2, var3);
      this.field_70159_w = this.field_70179_y = this.field_70181_x = 0.0;
      if (!this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_72854_c();
      }

      return EntityPlayer$EnumStatus.OK;
   }

   private void func_71013_b(int var1) {
      this.field_71079_bU = 0.0F;
      this.field_71089_bV = 0.0F;
      switch(var1) {
         case 0:
            this.field_71089_bV = -1.8F;
            break;
         case 1:
            this.field_71079_bU = 1.8F;
            break;
         case 2:
            this.field_71089_bV = 1.8F;
            break;
         case 3:
            this.field_71079_bU = -1.8F;
      }
   }

   public void func_70999_a(boolean var1, boolean var2, boolean var3) {
      this.func_70105_a(0.6F, 1.8F);
      this.func_71061_d_();
      ChunkCoordinates var4 = this.field_71081_bT;
      ChunkCoordinates var5 = this.field_71081_bT;
      if (var4 != null && this.field_70170_p.func_147439_a(var4.field_71574_a, var4.field_71572_b, var4.field_71573_c) == Blocks.field_150324_C) {
         BlockBed.func_149979_a(this.field_70170_p, var4.field_71574_a, var4.field_71572_b, var4.field_71573_c, false);
         var5 = BlockBed.func_149977_a(this.field_70170_p, var4.field_71574_a, var4.field_71572_b, var4.field_71573_c, 0);
         if (var5 == null) {
            var5 = new ChunkCoordinates(var4.field_71574_a, var4.field_71572_b + 1, var4.field_71573_c);
         }

         this.func_70107_b(
            (double)((float)var5.field_71574_a + 0.5F),
            (double)((float)var5.field_71572_b + this.field_70129_M + 0.1F),
            (double)((float)var5.field_71573_c + 0.5F)
         );
      }

      this.field_71083_bS = false;
      if (!this.field_70170_p.field_72995_K && var2) {
         this.field_70170_p.func_72854_c();
      }

      if (var1) {
         this.field_71076_b = 0;
      } else {
         this.field_71076_b = 100;
      }

      if (var3) {
         this.func_71063_a(this.field_71081_bT, false);
      }
   }

   private boolean func_71065_l() {
      return this.field_70170_p.func_147439_a(this.field_71081_bT.field_71574_a, this.field_71081_bT.field_71572_b, this.field_71081_bT.field_71573_c)
         == Blocks.field_150324_C;
   }

   public static ChunkCoordinates func_71056_a(World var0, ChunkCoordinates var1, boolean var2) {
      IChunkProvider var3 = var0.func_72863_F();
      var3.func_73158_c(var1.field_71574_a - 3 >> 4, var1.field_71573_c - 3 >> 4);
      var3.func_73158_c(var1.field_71574_a + 3 >> 4, var1.field_71573_c - 3 >> 4);
      var3.func_73158_c(var1.field_71574_a - 3 >> 4, var1.field_71573_c + 3 >> 4);
      var3.func_73158_c(var1.field_71574_a + 3 >> 4, var1.field_71573_c + 3 >> 4);
      if (var0.func_147439_a(var1.field_71574_a, var1.field_71572_b, var1.field_71573_c) == Blocks.field_150324_C) {
         return BlockBed.func_149977_a(var0, var1.field_71574_a, var1.field_71572_b, var1.field_71573_c, 0);
      } else {
         Material var4 = var0.func_147439_a(var1.field_71574_a, var1.field_71572_b, var1.field_71573_c).func_149688_o();
         Material var5 = var0.func_147439_a(var1.field_71574_a, var1.field_71572_b + 1, var1.field_71573_c).func_149688_o();
         boolean var6 = !var4.func_76220_a() && !var4.func_76224_d();
         boolean var7 = !var5.func_76220_a() && !var5.func_76224_d();
         return var2 && var6 && var7 ? var1 : null;
      }
   }

   public float func_71051_bG() {
      if (this.field_71081_bT != null) {
         int var1 = this.field_70170_p.func_72805_g(this.field_71081_bT.field_71574_a, this.field_71081_bT.field_71572_b, this.field_71081_bT.field_71573_c);
         int var2 = BlockBed.func_149895_l(var1);
         switch(var2) {
            case 0:
               return 90.0F;
            case 1:
               return 0.0F;
            case 2:
               return 270.0F;
            case 3:
               return 180.0F;
         }
      }

      return 0.0F;
   }

   @Override
   public boolean func_70608_bn() {
      return this.field_71083_bS;
   }

   public boolean func_71026_bH() {
      return this.field_71083_bS && this.field_71076_b >= 100;
   }

   public int func_71060_bI() {
      return this.field_71076_b;
   }

   protected boolean func_82241_s(int var1) {
      return (this.field_70180_af.func_75683_a(16) & 1 << var1) != 0;
   }

   protected void func_82239_b(int var1, boolean var2) {
      byte var3 = this.field_70180_af.func_75683_a(16);
      if (var2) {
         this.field_70180_af.func_75692_b(16, (byte)(var3 | 1 << var1));
      } else {
         this.field_70180_af.func_75692_b(16, (byte)(var3 & ~(1 << var1)));
      }
   }

   public void func_146105_b(IChatComponent var1) {
   }

   public ChunkCoordinates func_70997_bJ() {
      return this.field_71077_c;
   }

   public boolean func_82245_bX() {
      return this.field_82248_d;
   }

   public void func_71063_a(ChunkCoordinates var1, boolean var2) {
      if (var1 != null) {
         this.field_71077_c = new ChunkCoordinates(var1);
         this.field_82248_d = var2;
      } else {
         this.field_71077_c = null;
         this.field_82248_d = false;
      }
   }

   public void func_71029_a(StatBase var1) {
      this.func_71064_a(var1, 1);
   }

   public void func_71064_a(StatBase var1, int var2) {
   }

   @Override
   public void func_70664_aZ() {
      super.func_70664_aZ();
      this.func_71064_a(StatList.field_75953_u, 1);
      if (this.func_70051_ag()) {
         this.func_71020_j(0.8F);
      } else {
         this.func_71020_j(0.2F);
      }
   }

   @Override
   public void func_70612_e(float var1, float var2) {
      double var3 = this.field_70165_t;
      double var5 = this.field_70163_u;
      double var7 = this.field_70161_v;
      if (this.field_71075_bZ.field_75100_b && this.field_70154_o == null) {
         double var9 = this.field_70181_x;
         float var11 = this.field_70747_aH;
         this.field_70747_aH = this.field_71075_bZ.func_75093_a();
         super.func_70612_e(var1, var2);
         this.field_70181_x = var9 * 0.6;
         this.field_70747_aH = var11;
      } else {
         super.func_70612_e(var1, var2);
      }

      this.func_71000_j(this.field_70165_t - var3, this.field_70163_u - var5, this.field_70161_v - var7);
   }

   @Override
   public float func_70689_ay() {
      return (float)this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111126_e();
   }

   public void func_71000_j(double var1, double var3, double var5) {
      if (this.field_70154_o == null) {
         if (this.func_70055_a(Material.field_151586_h)) {
            int var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
            if (var7 > 0) {
               this.func_71064_a(StatList.field_75957_q, var7);
               this.func_71020_j(0.015F * (float)var7 * 0.01F);
            }
         } else if (this.func_70090_H()) {
            int var8 = Math.round(MathHelper.func_76133_a(var1 * var1 + var5 * var5) * 100.0F);
            if (var8 > 0) {
               this.func_71064_a(StatList.field_75946_m, var8);
               this.func_71020_j(0.015F * (float)var8 * 0.01F);
            }
         } else if (this.func_70617_f_()) {
            if (var3 > 0.0) {
               this.func_71064_a(StatList.field_75944_o, (int)Math.round(var3 * 100.0));
            }
         } else if (this.field_70122_E) {
            int var9 = Math.round(MathHelper.func_76133_a(var1 * var1 + var5 * var5) * 100.0F);
            if (var9 > 0) {
               this.func_71064_a(StatList.field_75945_l, var9);
               if (this.func_70051_ag()) {
                  this.func_71020_j(0.099999994F * (float)var9 * 0.01F);
               } else {
                  this.func_71020_j(0.01F * (float)var9 * 0.01F);
               }
            }
         } else {
            int var10 = Math.round(MathHelper.func_76133_a(var1 * var1 + var5 * var5) * 100.0F);
            if (var10 > 25) {
               this.func_71064_a(StatList.field_75958_p, var10);
            }
         }
      }
   }

   private void func_71015_k(double var1, double var3, double var5) {
      if (this.field_70154_o != null) {
         int var7 = Math.round(MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5) * 100.0F);
         if (var7 > 0) {
            if (this.field_70154_o instanceof EntityMinecart) {
               this.func_71064_a(StatList.field_75956_r, var7);
               if (this.field_71073_d == null) {
                  this.field_71073_d = new ChunkCoordinates(
                     MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
                  );
               } else if ((double)this.field_71073_d
                     .func_71569_e(
                        MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u), MathHelper.func_76128_c(this.field_70161_v)
                     )
                  >= 1000000.0) {
                  this.func_71064_a(AchievementList.field_76025_q, 1);
               }
            } else if (this.field_70154_o instanceof EntityBoat) {
               this.func_71064_a(StatList.field_75955_s, var7);
            } else if (this.field_70154_o instanceof EntityPig) {
               this.func_71064_a(StatList.field_75954_t, var7);
            } else if (this.field_70154_o instanceof EntityHorse) {
               this.func_71064_a(StatList.field_151185_q, var7);
            }
         }
      }
   }

   @Override
   protected void func_70069_a(float var1) {
      if (!this.field_71075_bZ.field_75101_c) {
         if (var1 >= 2.0F) {
            this.func_71064_a(StatList.field_75943_n, (int)Math.round((double)var1 * 100.0));
         }

         super.func_70069_a(var1);
      }
   }

   @Override
   protected String func_146067_o(int var1) {
      return var1 > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
   }

   @Override
   public void func_70074_a(EntityLivingBase var1) {
      if (var1 instanceof IMob) {
         this.func_71029_a(AchievementList.field_76023_s);
      }

      int var2 = EntityList.func_75619_a(var1);
      EntityList$EntityEggInfo var3 = (EntityList$EntityEggInfo)EntityList.field_75627_a.get(var2);
      if (var3 != null) {
         this.func_71064_a(var3.field_151512_d, 1);
      }
   }

   @Override
   public void func_70110_aj() {
      if (!this.field_71075_bZ.field_75100_b) {
         super.func_70110_aj();
      }
   }

   @Override
   public IIcon func_70620_b(ItemStack var1, int var2) {
      IIcon var3 = super.func_70620_b(var1, var2);
      if (var1.func_77973_b() == Items.field_151112_aM && this.field_71104_cf != null) {
         var3 = Items.field_151112_aM.func_94597_g();
      } else {
         if (var1.func_77973_b().func_77623_v()) {
            return var1.func_77973_b().func_77618_c(var1.func_77960_j(), var2);
         }

         if (this.field_71074_e != null && var1.func_77973_b() == Items.field_151031_f) {
            int var4 = var1.func_77988_m() - this.field_71072_f;
            if (var4 >= 18) {
               return Items.field_151031_f.func_94599_c(2);
            }

            if (var4 > 13) {
               return Items.field_151031_f.func_94599_c(1);
            }

            if (var4 > 0) {
               return Items.field_151031_f.func_94599_c(0);
            }
         }
      }

      return var3;
   }

   public ItemStack func_82169_q(int var1) {
      return this.field_71071_by.func_70440_f(var1);
   }

   public void func_71023_q(int var1) {
      this.func_85039_t(var1);
      int var2 = 2147483647 - this.field_71067_cb;
      if (var1 > var2) {
         var1 = var2;
      }

      this.field_71106_cc += (float)var1 / (float)this.func_71050_bK();

      for(this.field_71067_cb += var1; this.field_71106_cc >= 1.0F; this.field_71106_cc /= (float)this.func_71050_bK()) {
         this.field_71106_cc = (this.field_71106_cc - 1.0F) * (float)this.func_71050_bK();
         this.func_82242_a(1);
      }
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
         this.field_70170_p.func_72956_a(this, "random.levelup", var2 * 0.75F, 1.0F);
         this.field_82249_h = this.field_70173_aa;
      }
   }

   public int func_71050_bK() {
      if (this.field_71068_ca >= 30) {
         return 62 + (this.field_71068_ca - 30) * 7;
      } else {
         return this.field_71068_ca >= 15 ? 17 + (this.field_71068_ca - 15) * 3 : 17;
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
      return (var1 || this.field_71100_bB.func_75121_c()) && !this.field_71075_bZ.field_75102_a;
   }

   public boolean func_70996_bM() {
      return this.func_110143_aJ() > 0.0F && this.func_110143_aJ() < this.func_110138_aP();
   }

   public void func_71008_a(ItemStack var1, int var2) {
      if (var1 != this.field_71074_e) {
         this.field_71074_e = var1;
         this.field_71072_f = var2;
         if (!this.field_70170_p.field_72995_K) {
            this.func_70019_c(true);
         }
      }
   }

   public boolean func_82246_f(int var1, int var2, int var3) {
      if (this.field_71075_bZ.field_75099_e) {
         return true;
      } else {
         Block var4 = this.field_70170_p.func_147439_a(var1, var2, var3);
         if (var4.func_149688_o() != Material.field_151579_a) {
            if (var4.func_149688_o().func_85157_q()) {
               return true;
            }

            if (this.func_71045_bC() != null) {
               ItemStack var5 = this.func_71045_bC();
               if (var5.func_150998_b(var4) || var5.func_150997_a(var4) > 1.0F) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean func_82247_a(int var1, int var2, int var3, int var4, ItemStack var5) {
      if (this.field_71075_bZ.field_75099_e) {
         return true;
      } else {
         return var5 != null ? var5.func_82835_x() : false;
      }
   }

   @Override
   protected int func_70693_a(EntityPlayer var1) {
      if (this.field_70170_p.func_82736_K().func_82766_b("keepInventory")) {
         return 0;
      } else {
         int var2 = this.field_71068_ca * 7;
         return var2 > 100 ? 100 : var2;
      }
   }

   @Override
   protected boolean func_70684_aJ() {
      return true;
   }

   @Override
   public boolean func_94059_bO() {
      return true;
   }

   public void func_71049_a(EntityPlayer var1, boolean var2) {
      if (var2) {
         this.field_71071_by.func_70455_b(var1.field_71071_by);
         this.func_70606_j(var1.func_110143_aJ());
         this.field_71100_bB = var1.field_71100_bB;
         this.field_71068_ca = var1.field_71068_ca;
         this.field_71067_cb = var1.field_71067_cb;
         this.field_71106_cc = var1.field_71106_cc;
         this.func_85040_s(var1.func_71037_bA());
         this.field_82152_aq = var1.field_82152_aq;
      } else if (this.field_70170_p.func_82736_K().func_82766_b("keepInventory")) {
         this.field_71071_by.func_70455_b(var1.field_71071_by);
         this.field_71068_ca = var1.field_71068_ca;
         this.field_71067_cb = var1.field_71067_cb;
         this.field_71106_cc = var1.field_71106_cc;
         this.func_85040_s(var1.func_71037_bA());
      }

      this.field_71078_a = var1.field_71078_a;
   }

   @Override
   protected boolean func_70041_e_() {
      return !this.field_71075_bZ.field_75100_b;
   }

   public void func_71016_p() {
   }

   public void func_71033_a(WorldSettings$GameType var1) {
   }

   @Override
   public String func_70005_c_() {
      return this.field_146106_i.getName();
   }

   @Override
   public World func_130014_f_() {
      return this.field_70170_p;
   }

   public InventoryEnderChest func_71005_bN() {
      return this.field_71078_a;
   }

   @Override
   public ItemStack func_71124_b(int var1) {
      return var1 == 0 ? this.field_71071_by.func_70448_g() : this.field_71071_by.field_70460_b[var1 - 1];
   }

   @Override
   public ItemStack func_70694_bm() {
      return this.field_71071_by.func_70448_g();
   }

   @Override
   public void func_70062_b(int var1, ItemStack var2) {
      this.field_71071_by.field_70460_b[var1] = var2;
   }

   @Override
   public boolean func_98034_c(EntityPlayer var1) {
      if (!this.func_82150_aj()) {
         return false;
      } else {
         Team var2 = this.func_96124_cp();
         return var2 == null || var1 == null || var1.func_96124_cp() != var2 || !var2.func_98297_h();
      }
   }

   @Override
   public ItemStack[] func_70035_c() {
      return this.field_71071_by.field_70460_b;
   }

   public boolean func_82238_cc() {
      return this.func_82241_s(1);
   }

   @Override
   public boolean func_96092_aw() {
      return !this.field_71075_bZ.field_75100_b;
   }

   public Scoreboard func_96123_co() {
      return this.field_70170_p.func_96441_U();
   }

   @Override
   public Team func_96124_cp() {
      return this.func_96123_co().func_96509_i(this.func_70005_c_());
   }

   @Override
   public IChatComponent func_145748_c_() {
      ChatComponentText var1 = new ChatComponentText(ScorePlayerTeam.func_96667_a(this.func_96124_cp(), this.func_70005_c_()));
      var1.func_150256_b().func_150241_a(new ClickEvent(ClickEvent$Action.SUGGEST_COMMAND, "/msg " + this.func_70005_c_() + " "));
      return var1;
   }

   @Override
   public void func_110149_m(float var1) {
      if (var1 < 0.0F) {
         var1 = 0.0F;
      }

      this.func_70096_w().func_75692_b(17, var1);
   }

   @Override
   public float func_110139_bj() {
      return this.func_70096_w().func_111145_d(17);
   }

   public static UUID func_146094_a(GameProfile var0) {
      UUID var1 = var0.getId();
      if (var1 == null) {
         var1 = UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0.getName()).getBytes(Charsets.UTF_8));
      }

      return var1;
   }
}
