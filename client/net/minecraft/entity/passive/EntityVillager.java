package net.minecraft.entity.passive;

import java.util.Iterator;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIFollowGolem;
import net.minecraft.entity.ai.EntityAIHarvestFarmland;
import net.minecraft.entity.ai.EntityAILookAtTradePlayer;
import net.minecraft.entity.ai.EntityAIMoveIndoors;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIPlay;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITradePlayer;
import net.minecraft.entity.ai.EntityAIVillagerInteract;
import net.minecraft.entity.ai.EntityAIVillagerMate;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Tuple;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.village.Village;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityVillager extends EntityAgeable implements IMerchant, INpc {
   private int field_70955_e;
   private boolean field_70952_f;
   private boolean field_70953_g;
   Village field_70954_d;
   private EntityPlayer field_70962_h;
   private MerchantRecipeList field_70963_i;
   private int field_70961_j;
   private boolean field_70959_by;
   private boolean field_175565_bs;
   private int field_70956_bz;
   private String field_82189_bL;
   private int field_175563_bv;
   private int field_175562_bw;
   private boolean field_82190_bM;
   private boolean field_175564_by;
   private InventoryBasic field_175560_bz;
   private static final EntityVillager.ITradeList[][][][] field_175561_bA;

   public EntityVillager(World var1) {
      this(var1, 0);
   }

   public EntityVillager(World var1, int var2) {
      super(var1);
      this.field_175560_bz = new InventoryBasic("Items", false, 8);
      this.func_70938_b(var2);
      this.func_70105_a(0.6F, 1.8F);
      ((PathNavigateGround)this.func_70661_as()).func_179688_b(true);
      ((PathNavigateGround)this.func_70661_as()).func_179690_a(true);
      this.field_70714_bg.func_75776_a(0, new EntityAISwimming(this));
      this.field_70714_bg.func_75776_a(1, new EntityAIAvoidEntity(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
      this.field_70714_bg.func_75776_a(1, new EntityAITradePlayer(this));
      this.field_70714_bg.func_75776_a(1, new EntityAILookAtTradePlayer(this));
      this.field_70714_bg.func_75776_a(2, new EntityAIMoveIndoors(this));
      this.field_70714_bg.func_75776_a(3, new EntityAIRestrictOpenDoor(this));
      this.field_70714_bg.func_75776_a(4, new EntityAIOpenDoor(this, true));
      this.field_70714_bg.func_75776_a(5, new EntityAIMoveTowardsRestriction(this, 0.6D));
      this.field_70714_bg.func_75776_a(6, new EntityAIVillagerMate(this));
      this.field_70714_bg.func_75776_a(7, new EntityAIFollowGolem(this));
      this.field_70714_bg.func_75776_a(9, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
      this.field_70714_bg.func_75776_a(9, new EntityAIVillagerInteract(this));
      this.field_70714_bg.func_75776_a(9, new EntityAIWander(this, 0.6D));
      this.field_70714_bg.func_75776_a(10, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
      this.func_98053_h(true);
   }

   private void func_175552_ct() {
      if (!this.field_175564_by) {
         this.field_175564_by = true;
         if (this.func_70631_g_()) {
            this.field_70714_bg.func_75776_a(8, new EntityAIPlay(this, 0.32D));
         } else if (this.func_70946_n() == 0) {
            this.field_70714_bg.func_75776_a(6, new EntityAIHarvestFarmland(this, 0.6D));
         }

      }
   }

   protected void func_175500_n() {
      if (this.func_70946_n() == 0) {
         this.field_70714_bg.func_75776_a(8, new EntityAIHarvestFarmland(this, 0.6D));
      }

      super.func_175500_n();
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.5D);
   }

   protected void func_70619_bc() {
      if (--this.field_70955_e <= 0) {
         BlockPos var1 = new BlockPos(this);
         this.field_70170_p.func_175714_ae().func_176060_a(var1);
         this.field_70955_e = 70 + this.field_70146_Z.nextInt(50);
         this.field_70954_d = this.field_70170_p.func_175714_ae().func_176056_a(var1, 32);
         if (this.field_70954_d == null) {
            this.func_110177_bN();
         } else {
            BlockPos var2 = this.field_70954_d.func_180608_a();
            this.func_175449_a(var2, (int)((float)this.field_70954_d.func_75568_b() * 1.0F));
            if (this.field_82190_bM) {
               this.field_82190_bM = false;
               this.field_70954_d.func_82683_b(5);
            }
         }
      }

      if (!this.func_70940_q() && this.field_70961_j > 0) {
         --this.field_70961_j;
         if (this.field_70961_j <= 0) {
            if (this.field_70959_by) {
               Iterator var3 = this.field_70963_i.iterator();

               while(var3.hasNext()) {
                  MerchantRecipe var4 = (MerchantRecipe)var3.next();
                  if (var4.func_82784_g()) {
                     var4.func_82783_a(this.field_70146_Z.nextInt(6) + this.field_70146_Z.nextInt(6) + 2);
                  }
               }

               this.func_175554_cu();
               this.field_70959_by = false;
               if (this.field_70954_d != null && this.field_82189_bL != null) {
                  this.field_70170_p.func_72960_a(this, (byte)14);
                  this.field_70954_d.func_82688_a(this.field_82189_bL, 1);
               }
            }

            this.func_70690_d(new PotionEffect(Potion.field_76428_l.field_76415_H, 200, 0));
         }
      }

      super.func_70619_bc();
   }

   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      boolean var3 = var2 != null && var2.func_77973_b() == Items.field_151063_bx;
      if (!var3 && this.func_70089_S() && !this.func_70940_q() && !this.func_70631_g_()) {
         if (!this.field_70170_p.field_72995_K && (this.field_70963_i == null || this.field_70963_i.size() > 0)) {
            this.func_70932_a_(var1);
            var1.func_180472_a(this);
         }

         var1.func_71029_a(StatList.field_180205_F);
         return true;
      } else {
         return super.func_70085_c(var1);
      }
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, 0);
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("Profession", this.func_70946_n());
      var1.func_74768_a("Riches", this.field_70956_bz);
      var1.func_74768_a("Career", this.field_175563_bv);
      var1.func_74768_a("CareerLevel", this.field_175562_bw);
      var1.func_74757_a("Willing", this.field_175565_bs);
      if (this.field_70963_i != null) {
         var1.func_74782_a("Offers", this.field_70963_i.func_77202_a());
      }

      NBTTagList var2 = new NBTTagList();

      for(int var3 = 0; var3 < this.field_175560_bz.func_70302_i_(); ++var3) {
         ItemStack var4 = this.field_175560_bz.func_70301_a(var3);
         if (var4 != null) {
            var2.func_74742_a(var4.func_77955_b(new NBTTagCompound()));
         }
      }

      var1.func_74782_a("Inventory", var2);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.func_70938_b(var1.func_74762_e("Profession"));
      this.field_70956_bz = var1.func_74762_e("Riches");
      this.field_175563_bv = var1.func_74762_e("Career");
      this.field_175562_bw = var1.func_74762_e("CareerLevel");
      this.field_175565_bs = var1.func_74767_n("Willing");
      if (var1.func_150297_b("Offers", 10)) {
         NBTTagCompound var2 = var1.func_74775_l("Offers");
         this.field_70963_i = new MerchantRecipeList(var2);
      }

      NBTTagList var5 = var1.func_150295_c("Inventory", 10);

      for(int var3 = 0; var3 < var5.func_74745_c(); ++var3) {
         ItemStack var4 = ItemStack.func_77949_a(var5.func_150305_b(var3));
         if (var4 != null) {
            this.field_175560_bz.func_174894_a(var4);
         }
      }

      this.func_98053_h(true);
      this.func_175552_ct();
   }

   protected boolean func_70692_ba() {
      return false;
   }

   protected String func_70639_aQ() {
      return this.func_70940_q() ? "mob.villager.haggle" : "mob.villager.idle";
   }

   protected String func_70621_aR() {
      return "mob.villager.hit";
   }

   protected String func_70673_aS() {
      return "mob.villager.death";
   }

   public void func_70938_b(int var1) {
      this.field_70180_af.func_75692_b(16, var1);
   }

   public int func_70946_n() {
      return Math.max(this.field_70180_af.func_75679_c(16) % 5, 0);
   }

   public boolean func_70941_o() {
      return this.field_70952_f;
   }

   public void func_70947_e(boolean var1) {
      this.field_70952_f = var1;
   }

   public void func_70939_f(boolean var1) {
      this.field_70953_g = var1;
   }

   public boolean func_70945_p() {
      return this.field_70953_g;
   }

   public void func_70604_c(EntityLivingBase var1) {
      super.func_70604_c(var1);
      if (this.field_70954_d != null && var1 != null) {
         this.field_70954_d.func_75575_a(var1);
         if (var1 instanceof EntityPlayer) {
            byte var2 = -1;
            if (this.func_70631_g_()) {
               var2 = -3;
            }

            this.field_70954_d.func_82688_a(var1.func_70005_c_(), var2);
            if (this.func_70089_S()) {
               this.field_70170_p.func_72960_a(this, (byte)13);
            }
         }
      }

   }

   public void func_70645_a(DamageSource var1) {
      if (this.field_70954_d != null) {
         Entity var2 = var1.func_76346_g();
         if (var2 != null) {
            if (var2 instanceof EntityPlayer) {
               this.field_70954_d.func_82688_a(var2.func_70005_c_(), -2);
            } else if (var2 instanceof IMob) {
               this.field_70954_d.func_82692_h();
            }
         } else {
            EntityPlayer var3 = this.field_70170_p.func_72890_a(this, 16.0D);
            if (var3 != null) {
               this.field_70954_d.func_82692_h();
            }
         }
      }

      super.func_70645_a(var1);
   }

   public void func_70932_a_(EntityPlayer var1) {
      this.field_70962_h = var1;
   }

   public EntityPlayer func_70931_l_() {
      return this.field_70962_h;
   }

   public boolean func_70940_q() {
      return this.field_70962_h != null;
   }

   public boolean func_175550_n(boolean var1) {
      if (!this.field_175565_bs && var1 && this.func_175553_cp()) {
         boolean var2 = false;

         for(int var3 = 0; var3 < this.field_175560_bz.func_70302_i_(); ++var3) {
            ItemStack var4 = this.field_175560_bz.func_70301_a(var3);
            if (var4 != null) {
               if (var4.func_77973_b() == Items.field_151025_P && var4.field_77994_a >= 3) {
                  var2 = true;
                  this.field_175560_bz.func_70298_a(var3, 3);
               } else if ((var4.func_77973_b() == Items.field_151174_bG || var4.func_77973_b() == Items.field_151172_bF) && var4.field_77994_a >= 12) {
                  var2 = true;
                  this.field_175560_bz.func_70298_a(var3, 12);
               }
            }

            if (var2) {
               this.field_70170_p.func_72960_a(this, (byte)18);
               this.field_175565_bs = true;
               break;
            }
         }
      }

      return this.field_175565_bs;
   }

   public void func_175549_o(boolean var1) {
      this.field_175565_bs = var1;
   }

   public void func_70933_a(MerchantRecipe var1) {
      var1.func_77399_f();
      this.field_70757_a = -this.func_70627_aG();
      this.func_85030_a("mob.villager.yes", this.func_70599_aP(), this.func_70647_i());
      int var2 = 3 + this.field_70146_Z.nextInt(4);
      if (var1.func_180321_e() == 1 || this.field_70146_Z.nextInt(5) == 0) {
         this.field_70961_j = 40;
         this.field_70959_by = true;
         this.field_175565_bs = true;
         if (this.field_70962_h != null) {
            this.field_82189_bL = this.field_70962_h.func_70005_c_();
         } else {
            this.field_82189_bL = null;
         }

         var2 += 5;
      }

      if (var1.func_77394_a().func_77973_b() == Items.field_151166_bC) {
         this.field_70956_bz += var1.func_77394_a().field_77994_a;
      }

      if (var1.func_180322_j()) {
         this.field_70170_p.func_72838_d(new EntityXPOrb(this.field_70170_p, this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v, var2));
      }

   }

   public void func_110297_a_(ItemStack var1) {
      if (!this.field_70170_p.field_72995_K && this.field_70757_a > -this.func_70627_aG() + 20) {
         this.field_70757_a = -this.func_70627_aG();
         if (var1 != null) {
            this.func_85030_a("mob.villager.yes", this.func_70599_aP(), this.func_70647_i());
         } else {
            this.func_85030_a("mob.villager.no", this.func_70599_aP(), this.func_70647_i());
         }
      }

   }

   public MerchantRecipeList func_70934_b(EntityPlayer var1) {
      if (this.field_70963_i == null) {
         this.func_175554_cu();
      }

      return this.field_70963_i;
   }

   private void func_175554_cu() {
      EntityVillager.ITradeList[][][] var1 = field_175561_bA[this.func_70946_n()];
      if (this.field_175563_bv != 0 && this.field_175562_bw != 0) {
         ++this.field_175562_bw;
      } else {
         this.field_175563_bv = this.field_70146_Z.nextInt(var1.length) + 1;
         this.field_175562_bw = 1;
      }

      if (this.field_70963_i == null) {
         this.field_70963_i = new MerchantRecipeList();
      }

      int var2 = this.field_175563_bv - 1;
      int var3 = this.field_175562_bw - 1;
      EntityVillager.ITradeList[][] var4 = var1[var2];
      if (var3 >= 0 && var3 < var4.length) {
         EntityVillager.ITradeList[] var5 = var4[var3];
         EntityVillager.ITradeList[] var6 = var5;
         int var7 = var5.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            EntityVillager.ITradeList var9 = var6[var8];
            var9.func_179401_a(this.field_70963_i, this.field_70146_Z);
         }
      }

   }

   public void func_70930_a(MerchantRecipeList var1) {
   }

   public IChatComponent func_145748_c_() {
      String var1 = this.func_95999_t();
      if (var1 != null && var1.length() > 0) {
         ChatComponentText var4 = new ChatComponentText(var1);
         var4.func_150256_b().func_150209_a(this.func_174823_aP());
         var4.func_150256_b().func_179989_a(this.func_110124_au().toString());
         return var4;
      } else {
         if (this.field_70963_i == null) {
            this.func_175554_cu();
         }

         String var2 = null;
         switch(this.func_70946_n()) {
         case 0:
            if (this.field_175563_bv == 1) {
               var2 = "farmer";
            } else if (this.field_175563_bv == 2) {
               var2 = "fisherman";
            } else if (this.field_175563_bv == 3) {
               var2 = "shepherd";
            } else if (this.field_175563_bv == 4) {
               var2 = "fletcher";
            }
            break;
         case 1:
            var2 = "librarian";
            break;
         case 2:
            var2 = "cleric";
            break;
         case 3:
            if (this.field_175563_bv == 1) {
               var2 = "armor";
            } else if (this.field_175563_bv == 2) {
               var2 = "weapon";
            } else if (this.field_175563_bv == 3) {
               var2 = "tool";
            }
            break;
         case 4:
            if (this.field_175563_bv == 1) {
               var2 = "butcher";
            } else if (this.field_175563_bv == 2) {
               var2 = "leather";
            }
         }

         if (var2 != null) {
            ChatComponentTranslation var3 = new ChatComponentTranslation("entity.Villager." + var2, new Object[0]);
            var3.func_150256_b().func_150209_a(this.func_174823_aP());
            var3.func_150256_b().func_179989_a(this.func_110124_au().toString());
            return var3;
         } else {
            return super.func_145748_c_();
         }
      }
   }

   public float func_70047_e() {
      float var1 = 1.62F;
      if (this.func_70631_g_()) {
         var1 = (float)((double)var1 - 0.81D);
      }

      return var1;
   }

   public void func_70103_a(byte var1) {
      if (var1 == 12) {
         this.func_180489_a(EnumParticleTypes.HEART);
      } else if (var1 == 13) {
         this.func_180489_a(EnumParticleTypes.VILLAGER_ANGRY);
      } else if (var1 == 14) {
         this.func_180489_a(EnumParticleTypes.VILLAGER_HAPPY);
      } else {
         super.func_70103_a(var1);
      }

   }

   private void func_180489_a(EnumParticleTypes var1) {
      for(int var2 = 0; var2 < 5; ++var2) {
         double var3 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var5 = this.field_70146_Z.nextGaussian() * 0.02D;
         double var7 = this.field_70146_Z.nextGaussian() * 0.02D;
         this.field_70170_p.func_175688_a(var1, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 1.0D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var3, var5, var7);
      }

   }

   public IEntityLivingData func_180482_a(DifficultyInstance var1, IEntityLivingData var2) {
      var2 = super.func_180482_a(var1, var2);
      this.func_70938_b(this.field_70170_p.field_73012_v.nextInt(5));
      this.func_175552_ct();
      return var2;
   }

   public void func_82187_q() {
      this.field_82190_bM = true;
   }

   public EntityVillager func_90011_a(EntityAgeable var1) {
      EntityVillager var2 = new EntityVillager(this.field_70170_p);
      var2.func_180482_a(this.field_70170_p.func_175649_E(new BlockPos(var2)), (IEntityLivingData)null);
      return var2;
   }

   public boolean func_110164_bC() {
      return false;
   }

   public void func_70077_a(EntityLightningBolt var1) {
      if (!this.field_70170_p.field_72995_K && !this.field_70128_L) {
         EntityWitch var2 = new EntityWitch(this.field_70170_p);
         var2.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         var2.func_180482_a(this.field_70170_p.func_175649_E(new BlockPos(var2)), (IEntityLivingData)null);
         var2.func_94061_f(this.func_175446_cd());
         if (this.func_145818_k_()) {
            var2.func_96094_a(this.func_95999_t());
            var2.func_174805_g(this.func_174833_aM());
         }

         this.field_70170_p.func_72838_d(var2);
         this.func_70106_y();
      }
   }

   public InventoryBasic func_175551_co() {
      return this.field_175560_bz;
   }

   protected void func_175445_a(EntityItem var1) {
      ItemStack var2 = var1.func_92059_d();
      Item var3 = var2.func_77973_b();
      if (this.func_175558_a(var3)) {
         ItemStack var4 = this.field_175560_bz.func_174894_a(var2);
         if (var4 == null) {
            var1.func_70106_y();
         } else {
            var2.field_77994_a = var4.field_77994_a;
         }
      }

   }

   private boolean func_175558_a(Item var1) {
      return var1 == Items.field_151025_P || var1 == Items.field_151174_bG || var1 == Items.field_151172_bF || var1 == Items.field_151015_O || var1 == Items.field_151014_N;
   }

   public boolean func_175553_cp() {
      return this.func_175559_s(1);
   }

   public boolean func_175555_cq() {
      return this.func_175559_s(2);
   }

   public boolean func_175557_cr() {
      boolean var1 = this.func_70946_n() == 0;
      if (var1) {
         return !this.func_175559_s(5);
      } else {
         return !this.func_175559_s(1);
      }
   }

   private boolean func_175559_s(int var1) {
      boolean var2 = this.func_70946_n() == 0;

      for(int var3 = 0; var3 < this.field_175560_bz.func_70302_i_(); ++var3) {
         ItemStack var4 = this.field_175560_bz.func_70301_a(var3);
         if (var4 != null) {
            if (var4.func_77973_b() == Items.field_151025_P && var4.field_77994_a >= 3 * var1 || var4.func_77973_b() == Items.field_151174_bG && var4.field_77994_a >= 12 * var1 || var4.func_77973_b() == Items.field_151172_bF && var4.field_77994_a >= 12 * var1) {
               return true;
            }

            if (var2 && var4.func_77973_b() == Items.field_151015_O && var4.field_77994_a >= 9 * var1) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean func_175556_cs() {
      for(int var1 = 0; var1 < this.field_175560_bz.func_70302_i_(); ++var1) {
         ItemStack var2 = this.field_175560_bz.func_70301_a(var1);
         if (var2 != null && (var2.func_77973_b() == Items.field_151014_N || var2.func_77973_b() == Items.field_151174_bG || var2.func_77973_b() == Items.field_151172_bF)) {
            return true;
         }
      }

      return false;
   }

   public boolean func_174820_d(int var1, ItemStack var2) {
      if (super.func_174820_d(var1, var2)) {
         return true;
      } else {
         int var3 = var1 - 300;
         if (var3 >= 0 && var3 < this.field_175560_bz.func_70302_i_()) {
            this.field_175560_bz.func_70299_a(var3, var2);
            return true;
         } else {
            return false;
         }
      }
   }

   // $FF: synthetic method
   public EntityAgeable func_90011_a(EntityAgeable var1) {
      return this.func_90011_a(var1);
   }

   static {
      field_175561_bA = new EntityVillager.ITradeList[][][][]{{{{new EntityVillager.EmeraldForItems(Items.field_151015_O, new EntityVillager.PriceInfo(18, 22)), new EntityVillager.EmeraldForItems(Items.field_151174_bG, new EntityVillager.PriceInfo(15, 19)), new EntityVillager.EmeraldForItems(Items.field_151172_bF, new EntityVillager.PriceInfo(15, 19)), new EntityVillager.ListItemForEmeralds(Items.field_151025_P, new EntityVillager.PriceInfo(-4, -2))}, {new EntityVillager.EmeraldForItems(Item.func_150898_a(Blocks.field_150423_aK), new EntityVillager.PriceInfo(8, 13)), new EntityVillager.ListItemForEmeralds(Items.field_151158_bO, new EntityVillager.PriceInfo(-3, -2))}, {new EntityVillager.EmeraldForItems(Item.func_150898_a(Blocks.field_150440_ba), new EntityVillager.PriceInfo(7, 12)), new EntityVillager.ListItemForEmeralds(Items.field_151034_e, new EntityVillager.PriceInfo(-5, -7))}, {new EntityVillager.ListItemForEmeralds(Items.field_151106_aX, new EntityVillager.PriceInfo(-6, -10)), new EntityVillager.ListItemForEmeralds(Items.field_151105_aU, new EntityVillager.PriceInfo(1, 1))}}, {{new EntityVillager.EmeraldForItems(Items.field_151007_F, new EntityVillager.PriceInfo(15, 20)), new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ItemAndEmeraldToItem(Items.field_151115_aP, new EntityVillager.PriceInfo(6, 6), Items.field_179566_aV, new EntityVillager.PriceInfo(6, 6))}, {new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151112_aM, new EntityVillager.PriceInfo(7, 8))}}, {{new EntityVillager.EmeraldForItems(Item.func_150898_a(Blocks.field_150325_L), new EntityVillager.PriceInfo(16, 22)), new EntityVillager.ListItemForEmeralds(Items.field_151097_aZ, new EntityVillager.PriceInfo(3, 4))}, {new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 0), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 1), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 2), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 3), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 4), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 5), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 6), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 7), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 8), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 9), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 10), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 11), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 12), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 13), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 14), new EntityVillager.PriceInfo(1, 2)), new EntityVillager.ListItemForEmeralds(new ItemStack(Item.func_150898_a(Blocks.field_150325_L), 1, 15), new EntityVillager.PriceInfo(1, 2))}}, {{new EntityVillager.EmeraldForItems(Items.field_151007_F, new EntityVillager.PriceInfo(15, 20)), new EntityVillager.ListItemForEmeralds(Items.field_151032_g, new EntityVillager.PriceInfo(-12, -8))}, {new EntityVillager.ListItemForEmeralds(Items.field_151031_f, new EntityVillager.PriceInfo(2, 3)), new EntityVillager.ItemAndEmeraldToItem(Item.func_150898_a(Blocks.field_150351_n), new EntityVillager.PriceInfo(10, 10), Items.field_151145_ak, new EntityVillager.PriceInfo(6, 10))}}}, {{{new EntityVillager.EmeraldForItems(Items.field_151121_aF, new EntityVillager.PriceInfo(24, 36)), new EntityVillager.ListEnchantedBookForEmeralds()}, {new EntityVillager.EmeraldForItems(Items.field_151122_aG, new EntityVillager.PriceInfo(8, 10)), new EntityVillager.ListItemForEmeralds(Items.field_151111_aL, new EntityVillager.PriceInfo(10, 12)), new EntityVillager.ListItemForEmeralds(Item.func_150898_a(Blocks.field_150342_X), new EntityVillager.PriceInfo(3, 4))}, {new EntityVillager.EmeraldForItems(Items.field_151164_bB, new EntityVillager.PriceInfo(2, 2)), new EntityVillager.ListItemForEmeralds(Items.field_151113_aN, new EntityVillager.PriceInfo(10, 12)), new EntityVillager.ListItemForEmeralds(Item.func_150898_a(Blocks.field_150359_w), new EntityVillager.PriceInfo(-5, -3))}, {new EntityVillager.ListEnchantedBookForEmeralds()}, {new EntityVillager.ListEnchantedBookForEmeralds()}, {new EntityVillager.ListItemForEmeralds(Items.field_151057_cb, new EntityVillager.PriceInfo(20, 22))}}}, {{{new EntityVillager.EmeraldForItems(Items.field_151078_bh, new EntityVillager.PriceInfo(36, 40)), new EntityVillager.EmeraldForItems(Items.field_151043_k, new EntityVillager.PriceInfo(8, 10))}, {new EntityVillager.ListItemForEmeralds(Items.field_151137_ax, new EntityVillager.PriceInfo(-4, -1)), new EntityVillager.ListItemForEmeralds(new ItemStack(Items.field_151100_aR, 1, EnumDyeColor.BLUE.func_176767_b()), new EntityVillager.PriceInfo(-2, -1))}, {new EntityVillager.ListItemForEmeralds(Items.field_151061_bv, new EntityVillager.PriceInfo(7, 11)), new EntityVillager.ListItemForEmeralds(Item.func_150898_a(Blocks.field_150426_aN), new EntityVillager.PriceInfo(-3, -1))}, {new EntityVillager.ListItemForEmeralds(Items.field_151062_by, new EntityVillager.PriceInfo(3, 11))}}}, {{{new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ListItemForEmeralds(Items.field_151028_Y, new EntityVillager.PriceInfo(4, 6))}, {new EntityVillager.EmeraldForItems(Items.field_151042_j, new EntityVillager.PriceInfo(7, 9)), new EntityVillager.ListItemForEmeralds(Items.field_151030_Z, new EntityVillager.PriceInfo(10, 14))}, {new EntityVillager.EmeraldForItems(Items.field_151045_i, new EntityVillager.PriceInfo(3, 4)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151163_ad, new EntityVillager.PriceInfo(16, 19))}, {new EntityVillager.ListItemForEmeralds(Items.field_151029_X, new EntityVillager.PriceInfo(5, 7)), new EntityVillager.ListItemForEmeralds(Items.field_151022_W, new EntityVillager.PriceInfo(9, 11)), new EntityVillager.ListItemForEmeralds(Items.field_151020_U, new EntityVillager.PriceInfo(5, 7)), new EntityVillager.ListItemForEmeralds(Items.field_151023_V, new EntityVillager.PriceInfo(11, 15))}}, {{new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ListItemForEmeralds(Items.field_151036_c, new EntityVillager.PriceInfo(6, 8))}, {new EntityVillager.EmeraldForItems(Items.field_151042_j, new EntityVillager.PriceInfo(7, 9)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151040_l, new EntityVillager.PriceInfo(9, 10))}, {new EntityVillager.EmeraldForItems(Items.field_151045_i, new EntityVillager.PriceInfo(3, 4)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151048_u, new EntityVillager.PriceInfo(12, 15)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151056_x, new EntityVillager.PriceInfo(9, 12))}}, {{new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151037_a, new EntityVillager.PriceInfo(5, 7))}, {new EntityVillager.EmeraldForItems(Items.field_151042_j, new EntityVillager.PriceInfo(7, 9)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151035_b, new EntityVillager.PriceInfo(9, 11))}, {new EntityVillager.EmeraldForItems(Items.field_151045_i, new EntityVillager.PriceInfo(3, 4)), new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151046_w, new EntityVillager.PriceInfo(12, 15))}}}, {{{new EntityVillager.EmeraldForItems(Items.field_151147_al, new EntityVillager.PriceInfo(14, 18)), new EntityVillager.EmeraldForItems(Items.field_151076_bf, new EntityVillager.PriceInfo(14, 18))}, {new EntityVillager.EmeraldForItems(Items.field_151044_h, new EntityVillager.PriceInfo(16, 24)), new EntityVillager.ListItemForEmeralds(Items.field_151157_am, new EntityVillager.PriceInfo(-7, -5)), new EntityVillager.ListItemForEmeralds(Items.field_151077_bg, new EntityVillager.PriceInfo(-8, -6))}}, {{new EntityVillager.EmeraldForItems(Items.field_151116_aA, new EntityVillager.PriceInfo(9, 12)), new EntityVillager.ListItemForEmeralds(Items.field_151026_S, new EntityVillager.PriceInfo(2, 4))}, {new EntityVillager.ListEnchantedItemForEmeralds(Items.field_151027_R, new EntityVillager.PriceInfo(7, 12))}, {new EntityVillager.ListItemForEmeralds(Items.field_151141_av, new EntityVillager.PriceInfo(8, 10))}}}};
   }

   static class ItemAndEmeraldToItem implements EntityVillager.ITradeList {
      public ItemStack field_179411_a;
      public EntityVillager.PriceInfo field_179409_b;
      public ItemStack field_179410_c;
      public EntityVillager.PriceInfo field_179408_d;

      public ItemAndEmeraldToItem(Item var1, EntityVillager.PriceInfo var2, Item var3, EntityVillager.PriceInfo var4) {
         super();
         this.field_179411_a = new ItemStack(var1);
         this.field_179409_b = var2;
         this.field_179410_c = new ItemStack(var3);
         this.field_179408_d = var4;
      }

      public void func_179401_a(MerchantRecipeList var1, Random var2) {
         int var3 = 1;
         if (this.field_179409_b != null) {
            var3 = this.field_179409_b.func_179412_a(var2);
         }

         int var4 = 1;
         if (this.field_179408_d != null) {
            var4 = this.field_179408_d.func_179412_a(var2);
         }

         var1.add(new MerchantRecipe(new ItemStack(this.field_179411_a.func_77973_b(), var3, this.field_179411_a.func_77960_j()), new ItemStack(Items.field_151166_bC), new ItemStack(this.field_179410_c.func_77973_b(), var4, this.field_179410_c.func_77960_j())));
      }
   }

   static class ListEnchantedBookForEmeralds implements EntityVillager.ITradeList {
      public ListEnchantedBookForEmeralds() {
         super();
      }

      public void func_179401_a(MerchantRecipeList var1, Random var2) {
         Enchantment var3 = Enchantment.field_77331_b[var2.nextInt(Enchantment.field_77331_b.length)];
         int var4 = MathHelper.func_76136_a(var2, var3.func_77319_d(), var3.func_77325_b());
         ItemStack var5 = Items.field_151134_bR.func_92111_a(new EnchantmentData(var3, var4));
         int var6 = 2 + var2.nextInt(5 + var4 * 10) + 3 * var4;
         if (var6 > 64) {
            var6 = 64;
         }

         var1.add(new MerchantRecipe(new ItemStack(Items.field_151122_aG), new ItemStack(Items.field_151166_bC, var6), var5));
      }
   }

   static class ListEnchantedItemForEmeralds implements EntityVillager.ITradeList {
      public ItemStack field_179407_a;
      public EntityVillager.PriceInfo field_179406_b;

      public ListEnchantedItemForEmeralds(Item var1, EntityVillager.PriceInfo var2) {
         super();
         this.field_179407_a = new ItemStack(var1);
         this.field_179406_b = var2;
      }

      public void func_179401_a(MerchantRecipeList var1, Random var2) {
         int var3 = 1;
         if (this.field_179406_b != null) {
            var3 = this.field_179406_b.func_179412_a(var2);
         }

         ItemStack var4 = new ItemStack(Items.field_151166_bC, var3, 0);
         ItemStack var5 = new ItemStack(this.field_179407_a.func_77973_b(), 1, this.field_179407_a.func_77960_j());
         var5 = EnchantmentHelper.func_77504_a(var2, var5, 5 + var2.nextInt(15));
         var1.add(new MerchantRecipe(var4, var5));
      }
   }

   static class ListItemForEmeralds implements EntityVillager.ITradeList {
      public ItemStack field_179403_a;
      public EntityVillager.PriceInfo field_179402_b;

      public ListItemForEmeralds(Item var1, EntityVillager.PriceInfo var2) {
         super();
         this.field_179403_a = new ItemStack(var1);
         this.field_179402_b = var2;
      }

      public ListItemForEmeralds(ItemStack var1, EntityVillager.PriceInfo var2) {
         super();
         this.field_179403_a = var1;
         this.field_179402_b = var2;
      }

      public void func_179401_a(MerchantRecipeList var1, Random var2) {
         int var3 = 1;
         if (this.field_179402_b != null) {
            var3 = this.field_179402_b.func_179412_a(var2);
         }

         ItemStack var4;
         ItemStack var5;
         if (var3 < 0) {
            var4 = new ItemStack(Items.field_151166_bC, 1, 0);
            var5 = new ItemStack(this.field_179403_a.func_77973_b(), -var3, this.field_179403_a.func_77960_j());
         } else {
            var4 = new ItemStack(Items.field_151166_bC, var3, 0);
            var5 = new ItemStack(this.field_179403_a.func_77973_b(), 1, this.field_179403_a.func_77960_j());
         }

         var1.add(new MerchantRecipe(var4, var5));
      }
   }

   static class EmeraldForItems implements EntityVillager.ITradeList {
      public Item field_179405_a;
      public EntityVillager.PriceInfo field_179404_b;

      public EmeraldForItems(Item var1, EntityVillager.PriceInfo var2) {
         super();
         this.field_179405_a = var1;
         this.field_179404_b = var2;
      }

      public void func_179401_a(MerchantRecipeList var1, Random var2) {
         int var3 = 1;
         if (this.field_179404_b != null) {
            var3 = this.field_179404_b.func_179412_a(var2);
         }

         var1.add(new MerchantRecipe(new ItemStack(this.field_179405_a, var3, 0), Items.field_151166_bC));
      }
   }

   interface ITradeList {
      void func_179401_a(MerchantRecipeList var1, Random var2);
   }

   static class PriceInfo extends Tuple<Integer, Integer> {
      public PriceInfo(int var1, int var2) {
         super(var1, var2);
      }

      public int func_179412_a(Random var1) {
         return (Integer)this.func_76341_a() >= (Integer)this.func_76340_b() ? (Integer)this.func_76341_a() : (Integer)this.func_76341_a() + var1.nextInt((Integer)this.func_76340_b() - (Integer)this.func_76341_a() + 1);
      }
   }
}
