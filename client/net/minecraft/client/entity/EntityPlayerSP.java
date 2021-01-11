package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class EntityPlayerSP extends AbstractClientPlayer {
   public final NetHandlerPlayClient field_71174_a;
   private final StatFileWriter field_146108_bO;
   private double field_175172_bI;
   private double field_175166_bJ;
   private double field_175167_bK;
   private float field_175164_bL;
   private float field_175165_bM;
   private boolean field_175170_bN;
   private boolean field_175171_bO;
   private int field_175168_bP;
   private boolean field_175169_bQ;
   private String field_142022_ce;
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
   public float field_71086_bY;
   public float field_71080_cy;

   public EntityPlayerSP(Minecraft var1, World var2, NetHandlerPlayClient var3, StatFileWriter var4) {
      super(var2, var3.func_175105_e());
      this.field_71174_a = var3;
      this.field_146108_bO = var4;
      this.field_71159_c = var1;
      this.field_71093_bK = 0;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }

   public void func_70691_i(float var1) {
   }

   public void func_70078_a(Entity var1) {
      super.func_70078_a(var1);
      if (var1 instanceof EntityMinecart) {
         this.field_71159_c.func_147118_V().func_147682_a(new MovingSoundMinecartRiding(this, (EntityMinecart)var1));
      }

   }

   public void func_70071_h_() {
      if (this.field_70170_p.func_175667_e(new BlockPos(this.field_70165_t, 0.0D, this.field_70161_v))) {
         super.func_70071_h_();
         if (this.func_70115_ae()) {
            this.field_71174_a.func_147297_a(new C03PacketPlayer.C05PacketPlayerLook(this.field_70177_z, this.field_70125_A, this.field_70122_E));
            this.field_71174_a.func_147297_a(new C0CPacketInput(this.field_70702_br, this.field_70701_bs, this.field_71158_b.field_78901_c, this.field_71158_b.field_78899_d));
         } else {
            this.func_175161_p();
         }

      }
   }

   public void func_175161_p() {
      boolean var1 = this.func_70051_ag();
      if (var1 != this.field_175171_bO) {
         if (var1) {
            this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
         } else {
            this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
         }

         this.field_175171_bO = var1;
      }

      boolean var2 = this.func_70093_af();
      if (var2 != this.field_175170_bN) {
         if (var2) {
            this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
         } else {
            this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
         }

         this.field_175170_bN = var2;
      }

      if (this.func_175160_A()) {
         double var3 = this.field_70165_t - this.field_175172_bI;
         double var5 = this.func_174813_aQ().field_72338_b - this.field_175166_bJ;
         double var7 = this.field_70161_v - this.field_175167_bK;
         double var9 = (double)(this.field_70177_z - this.field_175164_bL);
         double var11 = (double)(this.field_70125_A - this.field_175165_bM);
         boolean var13 = var3 * var3 + var5 * var5 + var7 * var7 > 9.0E-4D || this.field_175168_bP >= 20;
         boolean var14 = var9 != 0.0D || var11 != 0.0D;
         if (this.field_70154_o == null) {
            if (var13 && var14) {
               this.field_71174_a.func_147297_a(new C03PacketPlayer.C06PacketPlayerPosLook(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v, this.field_70177_z, this.field_70125_A, this.field_70122_E));
            } else if (var13) {
               this.field_71174_a.func_147297_a(new C03PacketPlayer.C04PacketPlayerPosition(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v, this.field_70122_E));
            } else if (var14) {
               this.field_71174_a.func_147297_a(new C03PacketPlayer.C05PacketPlayerLook(this.field_70177_z, this.field_70125_A, this.field_70122_E));
            } else {
               this.field_71174_a.func_147297_a(new C03PacketPlayer(this.field_70122_E));
            }
         } else {
            this.field_71174_a.func_147297_a(new C03PacketPlayer.C06PacketPlayerPosLook(this.field_70159_w, -999.0D, this.field_70179_y, this.field_70177_z, this.field_70125_A, this.field_70122_E));
            var13 = false;
         }

         ++this.field_175168_bP;
         if (var13) {
            this.field_175172_bI = this.field_70165_t;
            this.field_175166_bJ = this.func_174813_aQ().field_72338_b;
            this.field_175167_bK = this.field_70161_v;
            this.field_175168_bP = 0;
         }

         if (var14) {
            this.field_175164_bL = this.field_70177_z;
            this.field_175165_bM = this.field_70125_A;
         }
      }

   }

   public EntityItem func_71040_bB(boolean var1) {
      C07PacketPlayerDigging.Action var2 = var1 ? C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
      this.field_71174_a.func_147297_a(new C07PacketPlayerDigging(var2, BlockPos.field_177992_a, EnumFacing.DOWN));
      return null;
   }

   protected void func_71012_a(EntityItem var1) {
   }

   public void func_71165_d(String var1) {
      this.field_71174_a.func_147297_a(new C01PacketChatMessage(var1));
   }

   public void func_71038_i() {
      super.func_71038_i();
      this.field_71174_a.func_147297_a(new C0APacketAnimation());
   }

   public void func_71004_bE() {
      this.field_71174_a.func_147297_a(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
   }

   protected void func_70665_d(DamageSource var1, float var2) {
      if (!this.func_180431_b(var1)) {
         this.func_70606_j(this.func_110143_aJ() - var2);
      }
   }

   public void func_71053_j() {
      this.field_71174_a.func_147297_a(new C0DPacketCloseWindow(this.field_71070_bA.field_75152_c));
      this.func_175159_q();
   }

   public void func_175159_q() {
      this.field_71071_by.func_70437_b((ItemStack)null);
      super.func_71053_j();
      this.field_71159_c.func_147108_a((GuiScreen)null);
   }

   public void func_71150_b(float var1) {
      if (this.field_175169_bQ) {
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
      } else {
         this.func_70606_j(var1);
         this.field_175169_bQ = true;
      }

   }

   public void func_71064_a(StatBase var1, int var2) {
      if (var1 != null) {
         if (var1.field_75972_f) {
            super.func_71064_a(var1, var2);
         }

      }
   }

   public void func_71016_p() {
      this.field_71174_a.func_147297_a(new C13PacketPlayerAbilities(this.field_71075_bZ));
   }

   public boolean func_175144_cb() {
      return true;
   }

   protected void func_110318_g() {
      this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.func_110319_bJ() * 100.0F)));
   }

   public void func_175163_u() {
      this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
   }

   public void func_175158_f(String var1) {
      this.field_142022_ce = var1;
   }

   public String func_142021_k() {
      return this.field_142022_ce;
   }

   public StatFileWriter func_146107_m() {
      return this.field_146108_bO;
   }

   public void func_146105_b(IChatComponent var1) {
      this.field_71159_c.field_71456_v.func_146158_b().func_146227_a(var1);
   }

   protected boolean func_145771_j(double var1, double var3, double var5) {
      if (this.field_70145_X) {
         return false;
      } else {
         BlockPos var7 = new BlockPos(var1, var3, var5);
         double var8 = var1 - (double)var7.func_177958_n();
         double var10 = var5 - (double)var7.func_177952_p();
         if (!this.func_175162_d(var7)) {
            byte var12 = -1;
            double var13 = 9999.0D;
            if (this.func_175162_d(var7.func_177976_e()) && var8 < var13) {
               var13 = var8;
               var12 = 0;
            }

            if (this.func_175162_d(var7.func_177974_f()) && 1.0D - var8 < var13) {
               var13 = 1.0D - var8;
               var12 = 1;
            }

            if (this.func_175162_d(var7.func_177978_c()) && var10 < var13) {
               var13 = var10;
               var12 = 4;
            }

            if (this.func_175162_d(var7.func_177968_d()) && 1.0D - var10 < var13) {
               var13 = 1.0D - var10;
               var12 = 5;
            }

            float var15 = 0.1F;
            if (var12 == 0) {
               this.field_70159_w = (double)(-var15);
            }

            if (var12 == 1) {
               this.field_70159_w = (double)var15;
            }

            if (var12 == 4) {
               this.field_70179_y = (double)(-var15);
            }

            if (var12 == 5) {
               this.field_70179_y = (double)var15;
            }
         }

         return false;
      }
   }

   private boolean func_175162_d(BlockPos var1) {
      return !this.field_70170_p.func_180495_p(var1).func_177230_c().func_149721_r() && !this.field_70170_p.func_180495_p(var1.func_177984_a()).func_177230_c().func_149721_r();
   }

   public void func_70031_b(boolean var1) {
      super.func_70031_b(var1);
      this.field_71157_e = var1 ? 600 : 0;
   }

   public void func_71152_a(float var1, int var2, int var3) {
      this.field_71106_cc = var1;
      this.field_71067_cb = var2;
      this.field_71068_ca = var3;
   }

   public void func_145747_a(IChatComponent var1) {
      this.field_71159_c.field_71456_v.func_146158_b().func_146227_a(var1);
   }

   public boolean func_70003_b(int var1, String var2) {
      return var1 <= 0;
   }

   public BlockPos func_180425_c() {
      return new BlockPos(this.field_70165_t + 0.5D, this.field_70163_u + 0.5D, this.field_70161_v + 0.5D);
   }

   public void func_85030_a(String var1, float var2, float var3) {
      this.field_70170_p.func_72980_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, var1, var2, var3, false);
   }

   public boolean func_70613_aW() {
      return true;
   }

   public boolean func_110317_t() {
      return this.field_70154_o != null && this.field_70154_o instanceof EntityHorse && ((EntityHorse)this.field_70154_o).func_110257_ck();
   }

   public float func_110319_bJ() {
      return this.field_110321_bQ;
   }

   public void func_175141_a(TileEntitySign var1) {
      this.field_71159_c.func_147108_a(new GuiEditSign(var1));
   }

   public void func_146095_a(CommandBlockLogic var1) {
      this.field_71159_c.func_147108_a(new GuiCommandBlock(var1));
   }

   public void func_71048_c(ItemStack var1) {
      Item var2 = var1.func_77973_b();
      if (var2 == Items.field_151099_bA) {
         this.field_71159_c.func_147108_a(new GuiScreenBook(this, var1, true));
      }

   }

   public void func_71007_a(IInventory var1) {
      String var2 = var1 instanceof IInteractionObject ? ((IInteractionObject)var1).func_174875_k() : "minecraft:container";
      if ("minecraft:chest".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiChest(this.field_71071_by, var1));
      } else if ("minecraft:hopper".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiHopper(this.field_71071_by, var1));
      } else if ("minecraft:furnace".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiFurnace(this.field_71071_by, var1));
      } else if ("minecraft:brewing_stand".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiBrewingStand(this.field_71071_by, var1));
      } else if ("minecraft:beacon".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiBeacon(this.field_71071_by, var1));
      } else if (!"minecraft:dispenser".equals(var2) && !"minecraft:dropper".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiChest(this.field_71071_by, var1));
      } else {
         this.field_71159_c.func_147108_a(new GuiDispenser(this.field_71071_by, var1));
      }

   }

   public void func_110298_a(EntityHorse var1, IInventory var2) {
      this.field_71159_c.func_147108_a(new GuiScreenHorseInventory(this.field_71071_by, var2, var1));
   }

   public void func_180468_a(IInteractionObject var1) {
      String var2 = var1.func_174875_k();
      if ("minecraft:crafting_table".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiCrafting(this.field_71071_by, this.field_70170_p));
      } else if ("minecraft:enchanting_table".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiEnchantment(this.field_71071_by, this.field_70170_p, var1));
      } else if ("minecraft:anvil".equals(var2)) {
         this.field_71159_c.func_147108_a(new GuiRepair(this.field_71071_by, this.field_70170_p));
      }

   }

   public void func_180472_a(IMerchant var1) {
      this.field_71159_c.func_147108_a(new GuiMerchant(this.field_71071_by, var1, this.field_70170_p));
   }

   public void func_71009_b(Entity var1) {
      this.field_71159_c.field_71452_i.func_178926_a(var1, EnumParticleTypes.CRIT);
   }

   public void func_71047_c(Entity var1) {
      this.field_71159_c.field_71452_i.func_178926_a(var1, EnumParticleTypes.CRIT_MAGIC);
   }

   public boolean func_70093_af() {
      boolean var1 = this.field_71158_b != null ? this.field_71158_b.field_78899_d : false;
      return var1 && !this.field_71083_bS;
   }

   public void func_70626_be() {
      super.func_70626_be();
      if (this.func_175160_A()) {
         this.field_70702_br = this.field_71158_b.field_78902_a;
         this.field_70701_bs = this.field_71158_b.field_78900_b;
         this.field_70703_bu = this.field_71158_b.field_78901_c;
         this.field_71163_h = this.field_71154_f;
         this.field_71164_i = this.field_71155_g;
         this.field_71155_g = (float)((double)this.field_71155_g + (double)(this.field_70125_A - this.field_71155_g) * 0.5D);
         this.field_71154_f = (float)((double)this.field_71154_f + (double)(this.field_70177_z - this.field_71154_f) * 0.5D);
      }

   }

   protected boolean func_175160_A() {
      return this.field_71159_c.func_175606_aa() == this;
   }

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

      this.field_71080_cy = this.field_71086_bY;
      if (this.field_71087_bX) {
         if (this.field_71159_c.field_71462_r != null && !this.field_71159_c.field_71462_r.func_73868_f()) {
            this.field_71159_c.func_147108_a((GuiScreen)null);
         }

         if (this.field_71086_bY == 0.0F) {
            this.field_71159_c.func_147118_V().func_147682_a(PositionedSoundRecord.func_147674_a(new ResourceLocation("portal.trigger"), this.field_70146_Z.nextFloat() * 0.4F + 0.8F));
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
      boolean var2 = this.field_71158_b.field_78899_d;
      float var3 = 0.8F;
      boolean var4 = this.field_71158_b.field_78900_b >= var3;
      this.field_71158_b.func_78898_a();
      if (this.func_71039_bw() && !this.func_70115_ae()) {
         MovementInput var10000 = this.field_71158_b;
         var10000.field_78902_a *= 0.2F;
         var10000 = this.field_71158_b;
         var10000.field_78900_b *= 0.2F;
         this.field_71156_d = 0;
      }

      this.func_145771_j(this.field_70165_t - (double)this.field_70130_N * 0.35D, this.func_174813_aQ().field_72338_b + 0.5D, this.field_70161_v + (double)this.field_70130_N * 0.35D);
      this.func_145771_j(this.field_70165_t - (double)this.field_70130_N * 0.35D, this.func_174813_aQ().field_72338_b + 0.5D, this.field_70161_v - (double)this.field_70130_N * 0.35D);
      this.func_145771_j(this.field_70165_t + (double)this.field_70130_N * 0.35D, this.func_174813_aQ().field_72338_b + 0.5D, this.field_70161_v - (double)this.field_70130_N * 0.35D);
      this.func_145771_j(this.field_70165_t + (double)this.field_70130_N * 0.35D, this.func_174813_aQ().field_72338_b + 0.5D, this.field_70161_v + (double)this.field_70130_N * 0.35D);
      boolean var5 = (float)this.func_71024_bL().func_75116_a() > 6.0F || this.field_71075_bZ.field_75101_c;
      if (this.field_70122_E && !var2 && !var4 && this.field_71158_b.field_78900_b >= var3 && !this.func_70051_ag() && var5 && !this.func_71039_bw() && !this.func_70644_a(Potion.field_76440_q)) {
         if (this.field_71156_d <= 0 && !this.field_71159_c.field_71474_y.field_151444_V.func_151470_d()) {
            this.field_71156_d = 7;
         } else {
            this.func_70031_b(true);
         }
      }

      if (!this.func_70051_ag() && this.field_71158_b.field_78900_b >= var3 && var5 && !this.func_71039_bw() && !this.func_70644_a(Potion.field_76440_q) && this.field_71159_c.field_71474_y.field_151444_V.func_151470_d()) {
         this.func_70031_b(true);
      }

      if (this.func_70051_ag() && (this.field_71158_b.field_78900_b < var3 || this.field_70123_F || !var5)) {
         this.func_70031_b(false);
      }

      if (this.field_71075_bZ.field_75101_c) {
         if (this.field_71159_c.field_71442_b.func_178887_k()) {
            if (!this.field_71075_bZ.field_75100_b) {
               this.field_71075_bZ.field_75100_b = true;
               this.func_71016_p();
            }
         } else if (!var1 && this.field_71158_b.field_78901_c) {
            if (this.field_71101_bC == 0) {
               this.field_71101_bC = 7;
            } else {
               this.field_71075_bZ.field_75100_b = !this.field_71075_bZ.field_75100_b;
               this.func_71016_p();
               this.field_71101_bC = 0;
            }
         }
      }

      if (this.field_71075_bZ.field_75100_b && this.func_175160_A()) {
         if (this.field_71158_b.field_78899_d) {
            this.field_70181_x -= (double)(this.field_71075_bZ.func_75093_a() * 3.0F);
         }

         if (this.field_71158_b.field_78901_c) {
            this.field_70181_x += (double)(this.field_71075_bZ.func_75093_a() * 3.0F);
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
      if (this.field_70122_E && this.field_71075_bZ.field_75100_b && !this.field_71159_c.field_71442_b.func_178887_k()) {
         this.field_71075_bZ.field_75100_b = false;
         this.func_71016_p();
      }

   }
}
