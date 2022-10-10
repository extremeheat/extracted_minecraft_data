package net.minecraft.client.entity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.audio.IAmbientSoundHandler;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.UnderwaterAmbientSoundHandler;
import net.minecraft.client.audio.UnderwaterAmbientSounds;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEditCommandBlockMinecart;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiEditStructure;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

public class EntityPlayerSP extends AbstractClientPlayer {
   public final NetHandlerPlayClient field_71174_a;
   private final StatisticsManager field_146108_bO;
   private final RecipeBookClient field_192036_cb;
   private final List<IAmbientSoundHandler> field_204232_cf = Lists.newArrayList();
   private int field_184845_bX = 0;
   private double field_175172_bI;
   private double field_175166_bJ;
   private double field_175167_bK;
   private float field_175164_bL;
   private float field_175165_bM;
   private boolean field_184841_cd;
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
   private boolean field_184842_cm;
   private EnumHand field_184843_cn;
   private boolean field_184844_co;
   private boolean field_189811_cr = true;
   private int field_189812_cs;
   private boolean field_189813_ct;
   private int field_203720_cz;

   public EntityPlayerSP(Minecraft var1, World var2, NetHandlerPlayClient var3, StatisticsManager var4, RecipeBookClient var5) {
      super(var2, var3.func_175105_e());
      this.field_71174_a = var3;
      this.field_146108_bO = var4;
      this.field_192036_cb = var5;
      this.field_71159_c = var1;
      this.field_71093_bK = DimensionType.OVERWORLD;
      this.field_204232_cf.add(new UnderwaterAmbientSoundHandler(this, var1.func_147118_V()));
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }

   public void func_70691_i(float var1) {
   }

   public boolean func_184205_a(Entity var1, boolean var2) {
      if (!super.func_184205_a(var1, var2)) {
         return false;
      } else {
         if (var1 instanceof EntityMinecart) {
            this.field_71159_c.func_147118_V().func_147682_a(new MovingSoundMinecartRiding(this, (EntityMinecart)var1));
         }

         if (var1 instanceof EntityBoat) {
            this.field_70126_B = var1.field_70177_z;
            this.field_70177_z = var1.field_70177_z;
            this.func_70034_d(var1.field_70177_z);
         }

         return true;
      }
   }

   public void func_184210_p() {
      super.func_184210_p();
      this.field_184844_co = false;
   }

   public float func_195050_f(float var1) {
      return this.field_70125_A;
   }

   public float func_195046_g(float var1) {
      return this.func_184218_aH() ? super.func_195046_g(var1) : this.field_70177_z;
   }

   public void func_70071_h_() {
      if (this.field_70170_p.func_175667_e(new BlockPos(this.field_70165_t, 0.0D, this.field_70161_v))) {
         super.func_70071_h_();
         if (this.func_184218_aH()) {
            this.field_71174_a.func_147297_a(new CPacketPlayer.Rotation(this.field_70177_z, this.field_70125_A, this.field_70122_E));
            this.field_71174_a.func_147297_a(new CPacketInput(this.field_70702_br, this.field_191988_bg, this.field_71158_b.field_78901_c, this.field_71158_b.field_78899_d));
            Entity var1 = this.func_184208_bv();
            if (var1 != this && var1.func_184186_bw()) {
               this.field_71174_a.func_147297_a(new CPacketVehicleMove(var1));
            }
         } else {
            this.func_175161_p();
         }

         Iterator var3 = this.field_204232_cf.iterator();

         while(var3.hasNext()) {
            IAmbientSoundHandler var2 = (IAmbientSoundHandler)var3.next();
            var2.func_204253_a();
         }

      }
   }

   private void func_175161_p() {
      boolean var1 = this.func_70051_ag();
      if (var1 != this.field_175171_bO) {
         if (var1) {
            this.field_71174_a.func_147297_a(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SPRINTING));
         } else {
            this.field_71174_a.func_147297_a(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SPRINTING));
         }

         this.field_175171_bO = var1;
      }

      boolean var2 = this.func_70093_af();
      if (var2 != this.field_175170_bN) {
         if (var2) {
            this.field_71174_a.func_147297_a(new CPacketEntityAction(this, CPacketEntityAction.Action.START_SNEAKING));
         } else {
            this.field_71174_a.func_147297_a(new CPacketEntityAction(this, CPacketEntityAction.Action.STOP_SNEAKING));
         }

         this.field_175170_bN = var2;
      }

      if (this.func_175160_A()) {
         AxisAlignedBB var3 = this.func_174813_aQ();
         double var4 = this.field_70165_t - this.field_175172_bI;
         double var6 = var3.field_72338_b - this.field_175166_bJ;
         double var8 = this.field_70161_v - this.field_175167_bK;
         double var10 = (double)(this.field_70177_z - this.field_175164_bL);
         double var12 = (double)(this.field_70125_A - this.field_175165_bM);
         ++this.field_175168_bP;
         boolean var14 = var4 * var4 + var6 * var6 + var8 * var8 > 9.0E-4D || this.field_175168_bP >= 20;
         boolean var15 = var10 != 0.0D || var12 != 0.0D;
         if (this.func_184218_aH()) {
            this.field_71174_a.func_147297_a(new CPacketPlayer.PositionRotation(this.field_70159_w, -999.0D, this.field_70179_y, this.field_70177_z, this.field_70125_A, this.field_70122_E));
            var14 = false;
         } else if (var14 && var15) {
            this.field_71174_a.func_147297_a(new CPacketPlayer.PositionRotation(this.field_70165_t, var3.field_72338_b, this.field_70161_v, this.field_70177_z, this.field_70125_A, this.field_70122_E));
         } else if (var14) {
            this.field_71174_a.func_147297_a(new CPacketPlayer.Position(this.field_70165_t, var3.field_72338_b, this.field_70161_v, this.field_70122_E));
         } else if (var15) {
            this.field_71174_a.func_147297_a(new CPacketPlayer.Rotation(this.field_70177_z, this.field_70125_A, this.field_70122_E));
         } else if (this.field_184841_cd != this.field_70122_E) {
            this.field_71174_a.func_147297_a(new CPacketPlayer(this.field_70122_E));
         }

         if (var14) {
            this.field_175172_bI = this.field_70165_t;
            this.field_175166_bJ = var3.field_72338_b;
            this.field_175167_bK = this.field_70161_v;
            this.field_175168_bP = 0;
         }

         if (var15) {
            this.field_175164_bL = this.field_70177_z;
            this.field_175165_bM = this.field_70125_A;
         }

         this.field_184841_cd = this.field_70122_E;
         this.field_189811_cr = this.field_71159_c.field_71474_y.field_189989_R;
      }

   }

   @Nullable
   public EntityItem func_71040_bB(boolean var1) {
      CPacketPlayerDigging.Action var2 = var1 ? CPacketPlayerDigging.Action.DROP_ALL_ITEMS : CPacketPlayerDigging.Action.DROP_ITEM;
      this.field_71174_a.func_147297_a(new CPacketPlayerDigging(var2, BlockPos.field_177992_a, EnumFacing.DOWN));
      this.field_71071_by.func_70298_a(this.field_71071_by.field_70461_c, var1 && !this.field_71071_by.func_70448_g().func_190926_b() ? this.field_71071_by.func_70448_g().func_190916_E() : 1);
      return null;
   }

   protected ItemStack func_184816_a(EntityItem var1) {
      return ItemStack.field_190927_a;
   }

   public void func_71165_d(String var1) {
      this.field_71174_a.func_147297_a(new CPacketChatMessage(var1));
   }

   public void func_184609_a(EnumHand var1) {
      super.func_184609_a(var1);
      this.field_71174_a.func_147297_a(new CPacketAnimation(var1));
   }

   public void func_71004_bE() {
      this.field_71174_a.func_147297_a(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
   }

   protected void func_70665_d(DamageSource var1, float var2) {
      if (!this.func_180431_b(var1)) {
         this.func_70606_j(this.func_110143_aJ() - var2);
      }
   }

   public void func_71053_j() {
      this.field_71174_a.func_147297_a(new CPacketCloseWindow(this.field_71070_bA.field_75152_c));
      this.func_175159_q();
   }

   public void func_175159_q() {
      this.field_71071_by.func_70437_b(ItemStack.field_190927_a);
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
            this.field_70738_aO = 10;
            this.field_70737_aN = this.field_70738_aO;
         }
      } else {
         this.func_70606_j(var1);
         this.field_175169_bQ = true;
      }

   }

   public void func_71016_p() {
      this.field_71174_a.func_147297_a(new CPacketPlayerAbilities(this.field_71075_bZ));
   }

   public boolean func_175144_cb() {
      return true;
   }

   protected void func_110318_g() {
      this.field_71174_a.func_147297_a(new CPacketEntityAction(this, CPacketEntityAction.Action.START_RIDING_JUMP, MathHelper.func_76141_d(this.func_110319_bJ() * 100.0F)));
   }

   public void func_175163_u() {
      this.field_71174_a.func_147297_a(new CPacketEntityAction(this, CPacketEntityAction.Action.OPEN_INVENTORY));
   }

   public void func_175158_f(String var1) {
      this.field_142022_ce = var1;
   }

   public String func_142021_k() {
      return this.field_142022_ce;
   }

   public StatisticsManager func_146107_m() {
      return this.field_146108_bO;
   }

   public RecipeBookClient func_199507_B() {
      return this.field_192036_cb;
   }

   public void func_193103_a(IRecipe var1) {
      if (this.field_192036_cb.func_194076_e(var1)) {
         this.field_192036_cb.func_194074_f(var1);
         this.field_71174_a.func_147297_a(new CPacketRecipeInfo(var1));
      }

   }

   protected int func_184840_I() {
      return this.field_184845_bX;
   }

   public void func_184839_n(int var1) {
      this.field_184845_bX = var1;
   }

   public void func_146105_b(ITextComponent var1, boolean var2) {
      if (var2) {
         this.field_71159_c.field_71456_v.func_175188_a(var1, false);
      } else {
         this.field_71159_c.field_71456_v.func_146158_b().func_146227_a(var1);
      }

   }

   protected boolean func_145771_j(double var1, double var3, double var5) {
      if (this.field_70145_X) {
         return false;
      } else {
         BlockPos var7 = new BlockPos(var1, var3, var5);
         double var8 = var1 - (double)var7.func_177958_n();
         double var10 = var5 - (double)var7.func_177952_p();
         if (this.func_205027_h(var7)) {
            byte var12 = -1;
            double var13 = 9999.0D;
            if (this.func_207402_f(var7.func_177976_e()) && var8 < var13) {
               var13 = var8;
               var12 = 0;
            }

            if (this.func_207402_f(var7.func_177974_f()) && 1.0D - var8 < var13) {
               var13 = 1.0D - var8;
               var12 = 1;
            }

            if (this.func_207402_f(var7.func_177978_c()) && var10 < var13) {
               var13 = var10;
               var12 = 4;
            }

            if (this.func_207402_f(var7.func_177968_d()) && 1.0D - var10 < var13) {
               var13 = 1.0D - var10;
               var12 = 5;
            }

            float var15 = 0.1F;
            if (var12 == 0) {
               this.field_70159_w = -0.10000000149011612D;
            }

            if (var12 == 1) {
               this.field_70159_w = 0.10000000149011612D;
            }

            if (var12 == 4) {
               this.field_70179_y = -0.10000000149011612D;
            }

            if (var12 == 5) {
               this.field_70179_y = 0.10000000149011612D;
            }
         }

         return false;
      }
   }

   private boolean func_205027_h(BlockPos var1) {
      if (this.func_203007_ba()) {
         return !this.func_207401_g(var1);
      } else {
         return !this.func_207402_f(var1);
      }
   }

   public void func_70031_b(boolean var1) {
      super.func_70031_b(var1);
      this.field_71157_e = 0;
   }

   public void func_71152_a(float var1, int var2, int var3) {
      this.field_71106_cc = var1;
      this.field_71067_cb = var2;
      this.field_71068_ca = var3;
   }

   public void func_145747_a(ITextComponent var1) {
      this.field_71159_c.field_71456_v.func_146158_b().func_146227_a(var1);
   }

   public void func_70103_a(byte var1) {
      if (var1 >= 24 && var1 <= 28) {
         this.func_184839_n(var1 - 24);
      } else {
         super.func_70103_a(var1);
      }

   }

   public void func_184185_a(SoundEvent var1, float var2, float var3) {
      this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, var1, this.func_184176_by(), var2, var3, false);
   }

   public boolean func_70613_aW() {
      return true;
   }

   public void func_184598_c(EnumHand var1) {
      ItemStack var2 = this.func_184586_b(var1);
      if (!var2.func_190926_b() && !this.func_184587_cr()) {
         super.func_184598_c(var1);
         this.field_184842_cm = true;
         this.field_184843_cn = var1;
      }
   }

   public boolean func_184587_cr() {
      return this.field_184842_cm;
   }

   public void func_184602_cy() {
      super.func_184602_cy();
      this.field_184842_cm = false;
   }

   public EnumHand func_184600_cs() {
      return this.field_184843_cn;
   }

   public void func_184206_a(DataParameter<?> var1) {
      super.func_184206_a(var1);
      if (field_184621_as.equals(var1)) {
         boolean var2 = ((Byte)this.field_70180_af.func_187225_a(field_184621_as) & 1) > 0;
         EnumHand var3 = ((Byte)this.field_70180_af.func_187225_a(field_184621_as) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
         if (var2 && !this.field_184842_cm) {
            this.func_184598_c(var3);
         } else if (!var2 && this.field_184842_cm) {
            this.func_184602_cy();
         }
      }

      if (field_184240_ax.equals(var1) && this.func_184613_cA() && !this.field_189813_ct) {
         this.field_71159_c.func_147118_V().func_147682_a(new ElytraSound(this));
      }

   }

   public boolean func_110317_t() {
      Entity var1 = this.func_184187_bx();
      return this.func_184218_aH() && var1 instanceof IJumpingMount && ((IJumpingMount)var1).func_184776_b();
   }

   public float func_110319_bJ() {
      return this.field_110321_bQ;
   }

   public void func_175141_a(TileEntitySign var1) {
      this.field_71159_c.func_147108_a(new GuiEditSign(var1));
   }

   public void func_184809_a(CommandBlockBaseLogic var1) {
      this.field_71159_c.func_147108_a(new GuiEditCommandBlockMinecart(var1));
   }

   public void func_184824_a(TileEntityCommandBlock var1) {
      this.field_71159_c.func_147108_a(new GuiCommandBlock(var1));
   }

   public void func_189807_a(TileEntityStructure var1) {
      this.field_71159_c.func_147108_a(new GuiEditStructure(var1));
   }

   public void func_184814_a(ItemStack var1, EnumHand var2) {
      Item var3 = var1.func_77973_b();
      if (var3 == Items.field_151099_bA) {
         this.field_71159_c.func_147108_a(new GuiScreenBook(this, var1, true, var2));
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
         if ("minecraft:shulker_box".equals(var2)) {
            this.field_71159_c.func_147108_a(new GuiShulkerBox(this.field_71071_by, var1));
         } else {
            this.field_71159_c.func_147108_a(new GuiChest(this.field_71071_by, var1));
         }
      } else {
         this.field_71159_c.func_147108_a(new GuiDispenser(this.field_71071_by, var1));
      }

   }

   public void func_184826_a(AbstractHorse var1, IInventory var2) {
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
      this.field_71159_c.field_71452_i.func_199282_a(var1, Particles.field_197614_g);
   }

   public void func_71047_c(Entity var1) {
      this.field_71159_c.field_71452_i.func_199282_a(var1, Particles.field_197622_o);
   }

   public boolean func_70093_af() {
      boolean var1 = this.field_71158_b != null && this.field_71158_b.field_78899_d;
      return var1 && !this.field_71083_bS;
   }

   public void func_70626_be() {
      super.func_70626_be();
      if (this.func_175160_A()) {
         this.field_70702_br = this.field_71158_b.field_78902_a;
         this.field_191988_bg = this.field_71158_b.field_192832_b;
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
      ++this.field_71157_e;
      if (this.field_71156_d > 0) {
         --this.field_71156_d;
      }

      this.field_71080_cy = this.field_71086_bY;
      if (this.field_71087_bX) {
         if (this.field_71159_c.field_71462_r != null && !this.field_71159_c.field_71462_r.func_73868_f()) {
            if (this.field_71159_c.field_71462_r instanceof GuiContainer) {
               this.func_71053_j();
            }

            this.field_71159_c.func_147108_a((GuiScreen)null);
         }

         if (this.field_71086_bY == 0.0F) {
            this.field_71159_c.func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187814_ei, this.field_70146_Z.nextFloat() * 0.4F + 0.8F));
         }

         this.field_71086_bY += 0.0125F;
         if (this.field_71086_bY >= 1.0F) {
            this.field_71086_bY = 1.0F;
         }

         this.field_71087_bX = false;
      } else if (this.func_70644_a(MobEffects.field_76431_k) && this.func_70660_b(MobEffects.field_76431_k).func_76459_b() > 60) {
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
      boolean var4 = this.field_71158_b.field_192832_b >= 0.8F;
      this.field_71158_b.func_78898_a();
      this.field_71159_c.func_193032_ao().func_193293_a(this.field_71158_b);
      MovementInput var10000;
      if (this.func_184587_cr() && !this.func_184218_aH()) {
         var10000 = this.field_71158_b;
         var10000.field_78902_a *= 0.2F;
         var10000 = this.field_71158_b;
         var10000.field_192832_b *= 0.2F;
         this.field_71156_d = 0;
      }

      boolean var5 = false;
      if (this.field_189812_cs > 0) {
         --this.field_189812_cs;
         var5 = true;
         this.field_71158_b.field_78901_c = true;
      }

      AxisAlignedBB var6 = this.func_174813_aQ();
      this.func_145771_j(this.field_70165_t - (double)this.field_70130_N * 0.35D, var6.field_72338_b + 0.5D, this.field_70161_v + (double)this.field_70130_N * 0.35D);
      this.func_145771_j(this.field_70165_t - (double)this.field_70130_N * 0.35D, var6.field_72338_b + 0.5D, this.field_70161_v - (double)this.field_70130_N * 0.35D);
      this.func_145771_j(this.field_70165_t + (double)this.field_70130_N * 0.35D, var6.field_72338_b + 0.5D, this.field_70161_v - (double)this.field_70130_N * 0.35D);
      this.func_145771_j(this.field_70165_t + (double)this.field_70130_N * 0.35D, var6.field_72338_b + 0.5D, this.field_70161_v + (double)this.field_70130_N * 0.35D);
      boolean var7 = (float)this.func_71024_bL().func_75116_a() > 6.0F || this.field_71075_bZ.field_75101_c;
      if ((this.field_70122_E || this.func_204231_K()) && !var2 && !var4 && this.field_71158_b.field_192832_b >= 0.8F && !this.func_70051_ag() && var7 && !this.func_184587_cr() && !this.func_70644_a(MobEffects.field_76440_q)) {
         if (this.field_71156_d <= 0 && !this.field_71159_c.field_71474_y.field_151444_V.func_151470_d()) {
            this.field_71156_d = 7;
         } else {
            this.func_70031_b(true);
         }
      }

      if (!this.func_70051_ag() && (!this.func_70090_H() || this.func_204231_K()) && this.field_71158_b.field_192832_b >= 0.8F && var7 && !this.func_184587_cr() && !this.func_70644_a(MobEffects.field_76440_q) && this.field_71159_c.field_71474_y.field_151444_V.func_151470_d()) {
         this.func_70031_b(true);
      }

      if (this.func_70051_ag()) {
         boolean var8 = this.field_71158_b.field_192832_b < 0.8F || !var7;
         boolean var9 = var8 || this.field_70123_F || this.func_70090_H() && !this.func_204231_K();
         if (this.func_203007_ba()) {
            if (!this.field_70122_E && !this.field_71158_b.field_78899_d && var8 || !this.func_70090_H()) {
               this.func_70031_b(false);
            }
         } else if (var9) {
            this.func_70031_b(false);
         }
      }

      if (this.field_71075_bZ.field_75101_c) {
         if (this.field_71159_c.field_71442_b.func_178887_k()) {
            if (!this.field_71075_bZ.field_75100_b) {
               this.field_71075_bZ.field_75100_b = true;
               this.func_71016_p();
            }
         } else if (!var1 && this.field_71158_b.field_78901_c && !var5) {
            if (this.field_71101_bC == 0) {
               this.field_71101_bC = 7;
            } else if (!this.func_203007_ba()) {
               this.field_71075_bZ.field_75100_b = !this.field_71075_bZ.field_75100_b;
               this.func_71016_p();
               this.field_71101_bC = 0;
            }
         }
      }

      if (this.field_71158_b.field_78901_c && !var1 && !this.field_70122_E && this.field_70181_x < 0.0D && !this.func_184613_cA() && !this.field_71075_bZ.field_75100_b) {
         ItemStack var10 = this.func_184582_a(EntityEquipmentSlot.CHEST);
         if (var10.func_77973_b() == Items.field_185160_cR && ItemElytra.func_185069_d(var10)) {
            this.field_71174_a.func_147297_a(new CPacketEntityAction(this, CPacketEntityAction.Action.START_FALL_FLYING));
         }
      }

      this.field_189813_ct = this.func_184613_cA();
      if (this.func_70090_H() && this.field_71158_b.field_78899_d) {
         this.func_203010_cG();
      }

      if (this.func_208600_a(FluidTags.field_206959_a)) {
         int var11 = this.func_175149_v() ? 10 : 1;
         this.field_203720_cz = MathHelper.func_76125_a(this.field_203720_cz + var11, 0, 600);
      } else if (this.field_203720_cz > 0) {
         this.func_208600_a(FluidTags.field_206959_a);
         this.field_203720_cz = MathHelper.func_76125_a(this.field_203720_cz - 10, 0, 600);
      }

      if (this.field_71075_bZ.field_75100_b && this.func_175160_A()) {
         if (this.field_71158_b.field_78899_d) {
            var10000 = this.field_71158_b;
            var10000.field_78902_a = (float)((double)var10000.field_78902_a / 0.3D);
            var10000 = this.field_71158_b;
            var10000.field_192832_b = (float)((double)var10000.field_192832_b / 0.3D);
            this.field_70181_x -= (double)(this.field_71075_bZ.func_75093_a() * 3.0F);
         }

         if (this.field_71158_b.field_78901_c) {
            this.field_70181_x += (double)(this.field_71075_bZ.func_75093_a() * 3.0F);
         }
      }

      if (this.func_110317_t()) {
         IJumpingMount var12 = (IJumpingMount)this.func_184187_bx();
         if (this.field_110320_a < 0) {
            ++this.field_110320_a;
            if (this.field_110320_a == 0) {
               this.field_110321_bQ = 0.0F;
            }
         }

         if (var1 && !this.field_71158_b.field_78901_c) {
            this.field_110320_a = -10;
            var12.func_110206_u(MathHelper.func_76141_d(this.func_110319_bJ() * 100.0F));
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

   public void func_70098_U() {
      super.func_70098_U();
      this.field_184844_co = false;
      if (this.func_184187_bx() instanceof EntityBoat) {
         EntityBoat var1 = (EntityBoat)this.func_184187_bx();
         var1.func_184442_a(this.field_71158_b.field_187257_e, this.field_71158_b.field_187258_f, this.field_71158_b.field_187255_c, this.field_71158_b.field_187256_d);
         this.field_184844_co |= this.field_71158_b.field_187257_e || this.field_71158_b.field_187258_f || this.field_71158_b.field_187255_c || this.field_71158_b.field_187256_d;
      }

   }

   public boolean func_184838_M() {
      return this.field_184844_co;
   }

   @Nullable
   public PotionEffect func_184596_c(@Nullable Potion var1) {
      if (var1 == MobEffects.field_76431_k) {
         this.field_71080_cy = 0.0F;
         this.field_71086_bY = 0.0F;
      }

      return super.func_184596_c(var1);
   }

   public void func_70091_d(MoverType var1, double var2, double var4, double var6) {
      double var8 = this.field_70165_t;
      double var10 = this.field_70161_v;
      super.func_70091_d(var1, var2, var4, var6);
      this.func_189810_i((float)(this.field_70165_t - var8), (float)(this.field_70161_v - var10));
   }

   public boolean func_189809_N() {
      return this.field_189811_cr;
   }

   protected void func_189810_i(float var1, float var2) {
      if (this.func_189809_N()) {
         if (this.field_189812_cs <= 0 && this.field_70122_E && !this.func_70093_af() && !this.func_184218_aH()) {
            Vec2f var3 = this.field_71158_b.func_190020_b();
            if (var3.field_189982_i != 0.0F || var3.field_189983_j != 0.0F) {
               Vec3d var4 = new Vec3d(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v);
               double var10002 = this.field_70165_t + (double)var1;
               double var10004 = this.field_70161_v + (double)var2;
               Vec3d var5 = new Vec3d(var10002, this.func_174813_aQ().field_72338_b, var10004);
               Vec3d var6 = new Vec3d((double)var1, 0.0D, (double)var2);
               float var7 = this.func_70689_ay();
               float var8 = (float)var6.func_189985_c();
               float var9;
               float var12;
               if (var8 <= 0.001F) {
                  var9 = var7 * var3.field_189982_i;
                  float var10 = var7 * var3.field_189983_j;
                  float var11 = MathHelper.func_76126_a(this.field_70177_z * 0.017453292F);
                  var12 = MathHelper.func_76134_b(this.field_70177_z * 0.017453292F);
                  var6 = new Vec3d((double)(var9 * var12 - var10 * var11), var6.field_72448_b, (double)(var10 * var12 + var9 * var11));
                  var8 = (float)var6.func_189985_c();
                  if (var8 <= 0.001F) {
                     return;
                  }
               }

               var9 = (float)MathHelper.func_181161_i((double)var8);
               Vec3d var40 = var6.func_186678_a((double)var9);
               Vec3d var41 = this.func_189651_aD();
               var12 = (float)(var41.field_72450_a * var40.field_72450_a + var41.field_72449_c * var40.field_72449_c);
               if (var12 >= -0.15F) {
                  BlockPos var13 = new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72337_e, this.field_70161_v);
                  IBlockState var14 = this.field_70170_p.func_180495_p(var13);
                  if (var14.func_196952_d(this.field_70170_p, var13).func_197766_b()) {
                     var13 = var13.func_177984_a();
                     IBlockState var15 = this.field_70170_p.func_180495_p(var13);
                     if (var15.func_196952_d(this.field_70170_p, var13).func_197766_b()) {
                        float var16 = 7.0F;
                        float var17 = 1.2F;
                        if (this.func_70644_a(MobEffects.field_76430_j)) {
                           var17 += (float)(this.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.75F;
                        }

                        float var18 = Math.max(var7 * 7.0F, 1.0F / var9);
                        Vec3d var20 = var5.func_178787_e(var40.func_186678_a((double)var18));
                        float var21 = this.field_70130_N;
                        float var22 = this.field_70131_O;
                        AxisAlignedBB var23 = (new AxisAlignedBB(var4, var20.func_72441_c(0.0D, (double)var22, 0.0D))).func_72314_b((double)var21, 0.0D, (double)var21);
                        Vec3d var19 = var4.func_72441_c(0.0D, 0.5099999904632568D, 0.0D);
                        var20 = var20.func_72441_c(0.0D, 0.5099999904632568D, 0.0D);
                        Vec3d var24 = var40.func_72431_c(new Vec3d(0.0D, 1.0D, 0.0D));
                        Vec3d var25 = var24.func_186678_a((double)(var21 * 0.5F));
                        Vec3d var26 = var19.func_178788_d(var25);
                        Vec3d var27 = var20.func_178788_d(var25);
                        Vec3d var28 = var19.func_178787_e(var25);
                        Vec3d var29 = var20.func_178787_e(var25);
                        Iterator var30 = this.field_70170_p.func_212388_b(this, var23).flatMap((var0) -> {
                           return var0.func_197756_d().stream();
                        }).iterator();
                        float var32 = 1.4E-45F;

                        label83:
                        while(var30.hasNext()) {
                           AxisAlignedBB var34 = (AxisAlignedBB)var30.next();
                           if (var34.func_189973_a(var26, var27) || var34.func_189973_a(var28, var29)) {
                              var32 = (float)var34.field_72337_e;
                              Vec3d var31 = var34.func_189972_c();
                              BlockPos var35 = new BlockPos(var31);
                              int var36 = 1;

                              while(true) {
                                 if ((float)var36 >= var17) {
                                    break label83;
                                 }

                                 BlockPos var37 = var35.func_177981_b(var36);
                                 IBlockState var38 = this.field_70170_p.func_180495_p(var37);
                                 VoxelShape var33;
                                 if (!(var33 = var38.func_196952_d(this.field_70170_p, var37)).func_197766_b()) {
                                    var32 = (float)var33.func_197758_c(EnumFacing.Axis.Y) + (float)var37.func_177956_o();
                                    if ((double)var32 - this.func_174813_aQ().field_72338_b > (double)var17) {
                                       return;
                                    }
                                 }

                                 if (var36 > 1) {
                                    var13 = var13.func_177984_a();
                                    IBlockState var39 = this.field_70170_p.func_180495_p(var13);
                                    if (!var39.func_196952_d(this.field_70170_p, var13).func_197766_b()) {
                                       return;
                                    }
                                 }

                                 ++var36;
                              }
                           }
                        }

                        if (var32 != 1.4E-45F) {
                           float var42 = (float)((double)var32 - this.func_174813_aQ().field_72338_b);
                           if (var42 > 0.5F && var42 <= var17) {
                              this.field_189812_cs = 1;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public float func_203719_J() {
      if (!this.func_208600_a(FluidTags.field_206959_a)) {
         return 0.0F;
      } else {
         float var1 = 600.0F;
         float var2 = 100.0F;
         if ((float)this.field_203720_cz >= 600.0F) {
            return 1.0F;
         } else {
            float var3 = MathHelper.func_76131_a((float)this.field_203720_cz / 100.0F, 0.0F, 1.0F);
            float var4 = (float)this.field_203720_cz < 100.0F ? 0.0F : MathHelper.func_76131_a(((float)this.field_203720_cz - 100.0F) / 500.0F, 0.0F, 1.0F);
            return var3 * 0.6F + var4 * 0.39999998F;
         }
      }
   }

   public boolean func_204231_K() {
      return this.field_204230_bP;
   }

   protected boolean func_204229_de() {
      boolean var1 = this.field_204230_bP;
      boolean var2 = super.func_204229_de();
      if (this.func_175149_v()) {
         return this.field_204230_bP;
      } else {
         if (!var1 && var2) {
            this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_204326_e, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
            this.field_71159_c.func_147118_V().func_147682_a(new UnderwaterAmbientSounds.UnderWaterSound(this));
         }

         if (var1 && !var2) {
            this.field_70170_p.func_184134_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, SoundEvents.field_204327_f, SoundCategory.AMBIENT, 1.0F, 1.0F, false);
         }

         return this.field_204230_bP;
      }
   }
}
