package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class EntityPlayerSP extends AbstractClientPlayer {
   public MovementInput field_71158_b;
   protected Minecraft field_71159_c;
   protected int field_71156_d;
   public int field_71157_e;
   public float field_71154_f;
   public float field_71155_g;
   public float field_71163_h;
   public float field_71164_i;
   private int field_110320_a;
   private float field_110321_bQ;
   private MouseFilter field_71162_ch = new MouseFilter();
   private MouseFilter field_71160_ci = new MouseFilter();
   private MouseFilter field_71161_cj = new MouseFilter();
   public float field_71086_bY;
   public float field_71080_cy;

   public EntityPlayerSP(Minecraft var1, World var2, Session var3, int var4) {
      super(var2, var3.func_148256_e());
      this.field_71159_c = var1;
      this.field_71093_bK = var4;
   }

   @Override
   public void func_70626_be() {
      super.func_70626_be();
      this.field_70702_br = this.field_71158_b.field_78902_a;
      this.field_70701_bs = this.field_71158_b.field_78900_b;
      this.field_70703_bu = this.field_71158_b.field_78901_c;
      this.field_71163_h = this.field_71154_f;
      this.field_71164_i = this.field_71155_g;
      this.field_71155_g = (float)((double)this.field_71155_g + (double)(this.field_70125_A - this.field_71155_g) * 0.5);
      this.field_71154_f = (float)((double)this.field_71154_f + (double)(this.field_70177_z - this.field_71154_f) * 0.5);
   }

   @Override
   public void func_70636_d() {
      if (this.field_71157_e > 0) {
         --this.field_71157_e;
         if (this.field_71157_e == 0) {
            this.func_70031_b(false);
         }
      }

      if (this.field_71156_d > 0) {
         --this.field_71156_d;
      }

      if (this.field_71159_c.field_71442_b.func_78747_a()) {
         this.field_70165_t = this.field_70161_v = 0.5;
         this.field_70165_t = 0.0;
         this.field_70161_v = 0.0;
         this.field_70177_z = (float)this.field_70173_aa / 12.0F;
         this.field_70125_A = 10.0F;
         this.field_70163_u = 68.5;
      } else {
         this.field_71080_cy = this.field_71086_bY;
         if (this.field_71087_bX) {
            if (this.field_71159_c.field_71462_r != null) {
               this.field_71159_c.func_147108_a(null);
            }

            if (this.field_71086_bY == 0.0F) {
               this.field_71159_c
                  .func_147118_V()
                  .func_147682_a(PositionedSoundRecord.func_147674_a(new ResourceLocation("portal.trigger"), this.field_70146_Z.nextFloat() * 0.4F + 0.8F));
            }

            this.field_71086_bY += 0.0125F;
            if (this.field_71086_bY >= 1.0F) {
               this.field_71086_bY = 1.0F;
            }

            this.field_71087_bX = false;
         } else if (this.func_70644_a(Potion.field_76431_k) && this.func_70660_b(Potion.field_76431_k).func_76459_b() > 60) {
            this.field_71086_bY += 0.006666667F;
            if (this.field_71086_bY > 1.0F) {
               this.field_71086_bY = 1.0F;
            }
         } else {
            if (this.field_71086_bY > 0.0F) {
               this.field_71086_bY -= 0.05F;
            }

            if (this.field_71086_bY < 0.0F) {
               this.field_71086_bY = 0.0F;
            }
         }

         if (this.field_71088_bW > 0) {
            --this.field_71088_bW;
         }

         boolean var1 = this.field_71158_b.field_78901_c;
         float var2 = 0.8F;
         boolean var3 = this.field_71158_b.field_78900_b >= var2;
         this.field_71158_b.func_78898_a();
         if (this.func_71039_bw() && !this.func_70115_ae()) {
            this.field_71158_b.field_78902_a *= 0.2F;
            this.field_71158_b.field_78900_b *= 0.2F;
            this.field_71156_d = 0;
         }

         if (this.field_71158_b.field_78899_d && this.field_70139_V < 0.2F) {
            this.field_70139_V = 0.2F;
         }

         this.func_145771_j(
            this.field_70165_t - (double)this.field_70130_N * 0.35,
            this.field_70121_D.field_72338_b + 0.5,
            this.field_70161_v + (double)this.field_70130_N * 0.35
         );
         this.func_145771_j(
            this.field_70165_t - (double)this.field_70130_N * 0.35,
            this.field_70121_D.field_72338_b + 0.5,
            this.field_70161_v - (double)this.field_70130_N * 0.35
         );
         this.func_145771_j(
            this.field_70165_t + (double)this.field_70130_N * 0.35,
            this.field_70121_D.field_72338_b + 0.5,
            this.field_70161_v - (double)this.field_70130_N * 0.35
         );
         this.func_145771_j(
            this.field_70165_t + (double)this.field_70130_N * 0.35,
            this.field_70121_D.field_72338_b + 0.5,
            this.field_70161_v + (double)this.field_70130_N * 0.35
         );
         boolean var4 = (float)this.func_71024_bL().func_75116_a() > 6.0F || this.field_71075_bZ.field_75101_c;
         if (this.field_70122_E
            && !var3
            && this.field_71158_b.field_78900_b >= var2
            && !this.func_70051_ag()
            && var4
            && !this.func_71039_bw()
            && !this.func_70644_a(Potion.field_76440_q)) {
            if (this.field_71156_d <= 0 && !this.field_71159_c.field_71474_y.field_151444_V.func_151470_d()) {
               this.field_71156_d = 7;
            } else {
               this.func_70031_b(true);
            }
         }

         if (!this.func_70051_ag()
            && this.field_71158_b.field_78900_b >= var2
            && var4
            && !this.func_71039_bw()
            && !this.func_70644_a(Potion.field_76440_q)
            && this.field_71159_c.field_71474_y.field_151444_V.func_151470_d()) {
            this.func_70031_b(true);
         }

         if (this.func_70051_ag() && (this.field_71158_b.field_78900_b < var2 || this.field_70123_F || !var4)) {
            this.func_70031_b(false);
         }

         if (this.field_71075_bZ.field_75101_c && !var1 && this.field_71158_b.field_78901_c) {
            if (this.field_71101_bC == 0) {
               this.field_71101_bC = 7;
            } else {
               this.field_71075_bZ.field_75100_b = !this.field_71075_bZ.field_75100_b;
               this.func_71016_p();
               this.field_71101_bC = 0;
            }
         }

         if (this.field_71075_bZ.field_75100_b) {
            if (this.field_71158_b.field_78899_d) {
               this.field_70181_x -= 0.15;
            }

            if (this.field_71158_b.field_78901_c) {
               this.field_70181_x += 0.15;
            }
         }

         if (this.func_110317_t()) {
            if (this.field_110320_a < 0) {
               ++this.field_110320_a;
               if (this.field_110320_a == 0) {
                  this.field_110321_bQ = 0.0F;
               }
            }

            if (var1 && !this.field_71158_b.field_78901_c) {
               this.field_110320_a = -10;
               this.func_110318_g();
            } else if (!var1 && this.field_71158_b.field_78901_c) {
               this.field_110320_a = 0;
               this.field_110321_bQ = 0.0F;
            } else if (var1) {
               ++this.field_110320_a;
               if (this.field_110320_a < 10) {
                  this.field_110321_bQ = (float)this.field_110320_a * 0.1F;
               } else {
                  this.field_110321_bQ = 0.8F + 2.0F / (float)(this.field_110320_a - 9) * 0.1F;
               }
            }
         } else {
            this.field_110321_bQ = 0.0F;
         }

         super.func_70636_d();
         if (this.field_70122_E && this.field_71075_bZ.field_75100_b) {
            this.field_71075_bZ.field_75100_b = false;
            this.func_71016_p();
         }
      }
   }

   public float func_71151_f() {
      float var1 = 1.0F;
      if (this.field_71075_bZ.field_75100_b) {
         var1 *= 1.1F;
      }

      IAttributeInstance var2 = this.func_110148_a(SharedMonsterAttributes.field_111263_d);
      var1 = (float)((double)var1 * ((var2.func_111126_e() / (double)this.field_71075_bZ.func_75094_b() + 1.0) / 2.0));
      if (this.field_71075_bZ.func_75094_b() == 0.0F || Float.isNaN(var1) || Float.isInfinite(var1)) {
         var1 = 1.0F;
      }

      if (this.func_71039_bw() && this.func_71011_bu().func_77973_b() == Items.field_151031_f) {
         int var3 = this.func_71057_bx();
         float var4 = (float)var3 / 20.0F;
         if (var4 > 1.0F) {
            var4 = 1.0F;
         } else {
            var4 *= var4;
         }

         var1 *= 1.0F - var4 * 0.15F;
      }

      return var1;
   }

   @Override
   public void func_71053_j() {
      super.func_71053_j();
      this.field_71159_c.func_147108_a(null);
   }

   @Override
   public void func_146100_a(TileEntity var1) {
      if (var1 instanceof TileEntitySign) {
         this.field_71159_c.func_147108_a(new GuiEditSign((TileEntitySign)var1));
      } else if (var1 instanceof TileEntityCommandBlock) {
         this.field_71159_c.func_147108_a(new GuiCommandBlock(((TileEntityCommandBlock)var1).func_145993_a()));
      }
   }

   @Override
   public void func_146095_a(CommandBlockLogic var1) {
      this.field_71159_c.func_147108_a(new GuiCommandBlock(var1));
   }

   @Override
   public void func_71048_c(ItemStack var1) {
      Item var2 = var1.func_77973_b();
      if (var2 == Items.field_151164_bB) {
         this.field_71159_c.func_147108_a(new GuiScreenBook(this, var1, false));
      } else if (var2 == Items.field_151099_bA) {
         this.field_71159_c.func_147108_a(new GuiScreenBook(this, var1, true));
      }
   }

   @Override
   public void func_71007_a(IInventory var1) {
      this.field_71159_c.func_147108_a(new GuiChest(this.field_71071_by, var1));
   }

   @Override
   public void func_146093_a(TileEntityHopper var1) {
      this.field_71159_c.func_147108_a(new GuiHopper(this.field_71071_by, var1));
   }

   @Override
   public void func_96125_a(EntityMinecartHopper var1) {
      this.field_71159_c.func_147108_a(new GuiHopper(this.field_71071_by, var1));
   }

   @Override
   public void func_110298_a(EntityHorse var1, IInventory var2) {
      this.field_71159_c.func_147108_a(new GuiScreenHorseInventory(this.field_71071_by, var2, var1));
   }

   @Override
   public void func_71058_b(int var1, int var2, int var3) {
      this.field_71159_c.func_147108_a(new GuiCrafting(this.field_71071_by, this.field_70170_p, var1, var2, var3));
   }

   @Override
   public void func_71002_c(int var1, int var2, int var3, String var4) {
      this.field_71159_c.func_147108_a(new GuiEnchantment(this.field_71071_by, this.field_70170_p, var1, var2, var3, var4));
   }

   @Override
   public void func_82244_d(int var1, int var2, int var3) {
      this.field_71159_c.func_147108_a(new GuiRepair(this.field_71071_by, this.field_70170_p, var1, var2, var3));
   }

   @Override
   public void func_146101_a(TileEntityFurnace var1) {
      this.field_71159_c.func_147108_a(new GuiFurnace(this.field_71071_by, var1));
   }

   @Override
   public void func_146098_a(TileEntityBrewingStand var1) {
      this.field_71159_c.func_147108_a(new GuiBrewingStand(this.field_71071_by, var1));
   }

   @Override
   public void func_146104_a(TileEntityBeacon var1) {
      this.field_71159_c.func_147108_a(new GuiBeacon(this.field_71071_by, var1));
   }

   @Override
   public void func_146102_a(TileEntityDispenser var1) {
      this.field_71159_c.func_147108_a(new GuiDispenser(this.field_71071_by, var1));
   }

   @Override
   public void func_71030_a(IMerchant var1, String var2) {
      this.field_71159_c.func_147108_a(new GuiMerchant(this.field_71071_by, var1, this.field_70170_p, var2));
   }

   @Override
   public void func_71009_b(Entity var1) {
      this.field_71159_c.field_71452_i.func_78873_a(new EntityCrit2FX(this.field_71159_c.field_71441_e, var1));
   }

   @Override
   public void func_71047_c(Entity var1) {
      EntityCrit2FX var2 = new EntityCrit2FX(this.field_71159_c.field_71441_e, var1, "magicCrit");
      this.field_71159_c.field_71452_i.func_78873_a(var2);
   }

   @Override
   public void func_71001_a(Entity var1, int var2) {
      this.field_71159_c.field_71452_i.func_78873_a(new EntityPickupFX(this.field_71159_c.field_71441_e, var1, this, -0.5F));
   }

   @Override
   public boolean func_70093_af() {
      return this.field_71158_b.field_78899_d && !this.field_71083_bS;
   }

   public void func_71150_b(float var1) {
      float var2 = this.func_110143_aJ() - var1;
      if (var2 <= 0.0F) {
         this.func_70606_j(var1);
         if (var2 < 0.0F) {
            this.field_70172_ad = this.field_70771_an / 2;
         }
      } else {
         this.field_110153_bc = var2;
         this.func_70606_j(this.func_110143_aJ());
         this.field_70172_ad = this.field_70771_an;
         this.func_70665_d(DamageSource.field_76377_j, var2);
         this.field_70737_aN = this.field_70738_aO = 10;
      }
   }

   @Override
   public void func_146105_b(IChatComponent var1) {
      this.field_71159_c.field_71456_v.func_146158_b().func_146227_a(var1);
   }

   private boolean func_71153_f(int var1, int var2, int var3) {
      return this.field_70170_p.func_147439_a(var1, var2, var3).func_149721_r();
   }

   @Override
   protected boolean func_145771_j(double var1, double var3, double var5) {
      int var7 = MathHelper.func_76128_c(var1);
      int var8 = MathHelper.func_76128_c(var3);
      int var9 = MathHelper.func_76128_c(var5);
      double var10 = var1 - (double)var7;
      double var12 = var5 - (double)var9;
      if (this.func_71153_f(var7, var8, var9) || this.func_71153_f(var7, var8 + 1, var9)) {
         boolean var14 = !this.func_71153_f(var7 - 1, var8, var9) && !this.func_71153_f(var7 - 1, var8 + 1, var9);
         boolean var15 = !this.func_71153_f(var7 + 1, var8, var9) && !this.func_71153_f(var7 + 1, var8 + 1, var9);
         boolean var16 = !this.func_71153_f(var7, var8, var9 - 1) && !this.func_71153_f(var7, var8 + 1, var9 - 1);
         boolean var17 = !this.func_71153_f(var7, var8, var9 + 1) && !this.func_71153_f(var7, var8 + 1, var9 + 1);
         byte var18 = -1;
         double var19 = 9999.0;
         if (var14 && var10 < var19) {
            var19 = var10;
            var18 = 0;
         }

         if (var15 && 1.0 - var10 < var19) {
            var19 = 1.0 - var10;
            var18 = 1;
         }

         if (var16 && var12 < var19) {
            var19 = var12;
            var18 = 4;
         }

         if (var17 && 1.0 - var12 < var19) {
            var19 = 1.0 - var12;
            var18 = 5;
         }

         float var21 = 0.1F;
         if (var18 == 0) {
            this.field_70159_w = (double)(-var21);
         }

         if (var18 == 1) {
            this.field_70159_w = (double)var21;
         }

         if (var18 == 4) {
            this.field_70179_y = (double)(-var21);
         }

         if (var18 == 5) {
            this.field_70179_y = (double)var21;
         }
      }

      return false;
   }

   @Override
   public void func_70031_b(boolean var1) {
      super.func_70031_b(var1);
      this.field_71157_e = var1 ? 600 : 0;
   }

   public void func_71152_a(float var1, int var2, int var3) {
      this.field_71106_cc = var1;
      this.field_71067_cb = var2;
      this.field_71068_ca = var3;
   }

   @Override
   public void func_145747_a(IChatComponent var1) {
      this.field_71159_c.field_71456_v.func_146158_b().func_146227_a(var1);
   }

   @Override
   public boolean func_70003_b(int var1, String var2) {
      return var1 <= 0;
   }

   @Override
   public ChunkCoordinates func_82114_b() {
      return new ChunkCoordinates(
         MathHelper.func_76128_c(this.field_70165_t + 0.5),
         MathHelper.func_76128_c(this.field_70163_u + 0.5),
         MathHelper.func_76128_c(this.field_70161_v + 0.5)
      );
   }

   @Override
   public void func_85030_a(String var1, float var2, float var3) {
      this.field_70170_p.func_72980_b(this.field_70165_t, this.field_70163_u - (double)this.field_70129_M, this.field_70161_v, var1, var2, var3, false);
   }

   @Override
   public boolean func_70613_aW() {
      return true;
   }

   public boolean func_110317_t() {
      return this.field_70154_o != null && this.field_70154_o instanceof EntityHorse;
   }

   public float func_110319_bJ() {
      return this.field_110321_bQ;
   }

   protected void func_110318_g() {
   }
}
