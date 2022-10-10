package net.minecraft.network;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipePlacer;
import net.minecraft.item.crafting.ServerRecipePlacerFurnace;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEditBook;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketNBTQueryEntity;
import net.minecraft.network.play.client.CPacketNBTQueryTileEntity;
import net.minecraft.network.play.client.CPacketPickItem;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketRenameItem;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSelectTrade;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateBeacon;
import net.minecraft.network.play.client.CPacketUpdateCommandBlock;
import net.minecraft.network.play.client.CPacketUpdateCommandMinecart;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUpdateStructureBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketNBTQueryResponse;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer, ITickable {
   private static final Logger field_147370_c = LogManager.getLogger();
   public final NetworkManager field_147371_a;
   private final MinecraftServer field_147367_d;
   public EntityPlayerMP field_147369_b;
   private int field_147368_e;
   private long field_194402_f;
   private boolean field_194403_g;
   private long field_194404_h;
   private int field_147374_l;
   private int field_147375_m;
   private final IntHashMap<Short> field_147372_n = new IntHashMap();
   private double field_184349_l;
   private double field_184350_m;
   private double field_184351_n;
   private double field_184352_o;
   private double field_184353_p;
   private double field_184354_q;
   private Entity field_184355_r;
   private double field_184356_s;
   private double field_184357_t;
   private double field_184358_u;
   private double field_184359_v;
   private double field_184360_w;
   private double field_184361_x;
   private Vec3d field_184362_y;
   private int field_184363_z;
   private int field_184343_A;
   private boolean field_184344_B;
   private int field_147365_f;
   private boolean field_184345_D;
   private int field_184346_E;
   private int field_184347_F;
   private int field_184348_G;

   public NetHandlerPlayServer(MinecraftServer var1, NetworkManager var2, EntityPlayerMP var3) {
      super();
      this.field_147367_d = var1;
      this.field_147371_a = var2;
      var2.func_150719_a(this);
      this.field_147369_b = var3;
      var3.field_71135_a = this;
   }

   public void func_73660_a() {
      this.func_184342_d();
      this.field_147369_b.func_71127_g();
      this.field_147369_b.func_70080_a(this.field_184349_l, this.field_184350_m, this.field_184351_n, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A);
      ++this.field_147368_e;
      this.field_184348_G = this.field_184347_F;
      if (this.field_184344_B) {
         if (++this.field_147365_f > 80) {
            field_147370_c.warn("{} was kicked for floating too long!", this.field_147369_b.func_200200_C_().getString());
            this.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.flying", new Object[0]));
            return;
         }
      } else {
         this.field_184344_B = false;
         this.field_147365_f = 0;
      }

      this.field_184355_r = this.field_147369_b.func_184208_bv();
      if (this.field_184355_r != this.field_147369_b && this.field_184355_r.func_184179_bs() == this.field_147369_b) {
         this.field_184356_s = this.field_184355_r.field_70165_t;
         this.field_184357_t = this.field_184355_r.field_70163_u;
         this.field_184358_u = this.field_184355_r.field_70161_v;
         this.field_184359_v = this.field_184355_r.field_70165_t;
         this.field_184360_w = this.field_184355_r.field_70163_u;
         this.field_184361_x = this.field_184355_r.field_70161_v;
         if (this.field_184345_D && this.field_147369_b.func_184208_bv().func_184179_bs() == this.field_147369_b) {
            if (++this.field_184346_E > 80) {
               field_147370_c.warn("{} was kicked for floating a vehicle too long!", this.field_147369_b.func_200200_C_().getString());
               this.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.flying", new Object[0]));
               return;
            }
         } else {
            this.field_184345_D = false;
            this.field_184346_E = 0;
         }
      } else {
         this.field_184355_r = null;
         this.field_184345_D = false;
         this.field_184346_E = 0;
      }

      this.field_147367_d.field_71304_b.func_76320_a("keepAlive");
      long var1 = Util.func_211177_b();
      if (var1 - this.field_194402_f >= 15000L) {
         if (this.field_194403_g) {
            this.func_194028_b(new TextComponentTranslation("disconnect.timeout", new Object[0]));
         } else {
            this.field_194403_g = true;
            this.field_194402_f = var1;
            this.field_194404_h = var1;
            this.func_147359_a(new SPacketKeepAlive(this.field_194404_h));
         }
      }

      this.field_147367_d.field_71304_b.func_76319_b();
      if (this.field_147374_l > 0) {
         --this.field_147374_l;
      }

      if (this.field_147375_m > 0) {
         --this.field_147375_m;
      }

      if (this.field_147369_b.func_154331_x() > 0L && this.field_147367_d.func_143007_ar() > 0 && Util.func_211177_b() - this.field_147369_b.func_154331_x() > (long)(this.field_147367_d.func_143007_ar() * 1000 * 60)) {
         this.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.idling", new Object[0]));
      }

   }

   public void func_184342_d() {
      this.field_184349_l = this.field_147369_b.field_70165_t;
      this.field_184350_m = this.field_147369_b.field_70163_u;
      this.field_184351_n = this.field_147369_b.field_70161_v;
      this.field_184352_o = this.field_147369_b.field_70165_t;
      this.field_184353_p = this.field_147369_b.field_70163_u;
      this.field_184354_q = this.field_147369_b.field_70161_v;
   }

   public NetworkManager func_147362_b() {
      return this.field_147371_a;
   }

   public void func_194028_b(ITextComponent var1) {
      this.field_147371_a.func_201058_a(new SPacketDisconnect(var1), (var2) -> {
         this.field_147371_a.func_150718_a(var1);
      });
      this.field_147371_a.func_150721_g();
      MinecraftServer var10000 = this.field_147367_d;
      NetworkManager var10001 = this.field_147371_a;
      var10001.getClass();
      Futures.getUnchecked(var10000.func_152344_a(var10001::func_179293_l));
   }

   public void func_147358_a(CPacketInput var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_110430_a(var1.func_149620_c(), var1.func_192620_b(), var1.func_149618_e(), var1.func_149617_f());
   }

   private static boolean func_183006_b(CPacketPlayer var0) {
      if (Doubles.isFinite(var0.func_186997_a(0.0D)) && Doubles.isFinite(var0.func_186996_b(0.0D)) && Doubles.isFinite(var0.func_187000_c(0.0D)) && Floats.isFinite(var0.func_186998_b(0.0F)) && Floats.isFinite(var0.func_186999_a(0.0F))) {
         return Math.abs(var0.func_186997_a(0.0D)) > 3.0E7D || Math.abs(var0.func_186996_b(0.0D)) > 3.0E7D || Math.abs(var0.func_187000_c(0.0D)) > 3.0E7D;
      } else {
         return true;
      }
   }

   private static boolean func_184341_b(CPacketVehicleMove var0) {
      return !Doubles.isFinite(var0.func_187004_a()) || !Doubles.isFinite(var0.func_187002_b()) || !Doubles.isFinite(var0.func_187003_c()) || !Floats.isFinite(var0.func_187005_e()) || !Floats.isFinite(var0.func_187006_d());
   }

   public void func_184338_a(CPacketVehicleMove var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (func_184341_b(var1)) {
         this.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.invalid_vehicle_movement", new Object[0]));
      } else {
         Entity var2 = this.field_147369_b.func_184208_bv();
         if (var2 != this.field_147369_b && var2.func_184179_bs() == this.field_147369_b && var2 == this.field_184355_r) {
            WorldServer var3 = this.field_147369_b.func_71121_q();
            double var4 = var2.field_70165_t;
            double var6 = var2.field_70163_u;
            double var8 = var2.field_70161_v;
            double var10 = var1.func_187004_a();
            double var12 = var1.func_187002_b();
            double var14 = var1.func_187003_c();
            float var16 = var1.func_187006_d();
            float var17 = var1.func_187005_e();
            double var18 = var10 - this.field_184356_s;
            double var20 = var12 - this.field_184357_t;
            double var22 = var14 - this.field_184358_u;
            double var24 = var2.field_70159_w * var2.field_70159_w + var2.field_70181_x * var2.field_70181_x + var2.field_70179_y * var2.field_70179_y;
            double var26 = var18 * var18 + var20 * var20 + var22 * var22;
            if (var26 - var24 > 100.0D && (!this.field_147367_d.func_71264_H() || !this.field_147367_d.func_71214_G().equals(var2.func_200200_C_().getString()))) {
               field_147370_c.warn("{} (vehicle of {}) moved too quickly! {},{},{}", var2.func_200200_C_().getString(), this.field_147369_b.func_200200_C_().getString(), var18, var20, var22);
               this.field_147371_a.func_179290_a(new SPacketMoveVehicle(var2));
               return;
            }

            boolean var28 = var3.func_195586_b(var2, var2.func_174813_aQ().func_186664_h(0.0625D));
            var18 = var10 - this.field_184359_v;
            var20 = var12 - this.field_184360_w - 1.0E-6D;
            var22 = var14 - this.field_184361_x;
            var2.func_70091_d(MoverType.PLAYER, var18, var20, var22);
            double var29 = var20;
            var18 = var10 - var2.field_70165_t;
            var20 = var12 - var2.field_70163_u;
            if (var20 > -0.5D || var20 < 0.5D) {
               var20 = 0.0D;
            }

            var22 = var14 - var2.field_70161_v;
            var26 = var18 * var18 + var20 * var20 + var22 * var22;
            boolean var31 = false;
            if (var26 > 0.0625D) {
               var31 = true;
               field_147370_c.warn("{} moved wrongly!", var2.func_200200_C_().getString());
            }

            var2.func_70080_a(var10, var12, var14, var16, var17);
            boolean var32 = var3.func_195586_b(var2, var2.func_174813_aQ().func_186664_h(0.0625D));
            if (var28 && (var31 || !var32)) {
               var2.func_70080_a(var4, var6, var8, var16, var17);
               this.field_147371_a.func_179290_a(new SPacketMoveVehicle(var2));
               return;
            }

            this.field_147367_d.func_184103_al().func_72358_d(this.field_147369_b);
            this.field_147369_b.func_71000_j(this.field_147369_b.field_70165_t - var4, this.field_147369_b.field_70163_u - var6, this.field_147369_b.field_70161_v - var8);
            this.field_184345_D = var29 >= -0.03125D && !this.field_147367_d.func_71231_X() && !var3.func_72829_c(var2.func_174813_aQ().func_186662_g(0.0625D).func_72321_a(0.0D, -0.55D, 0.0D));
            this.field_184359_v = var2.field_70165_t;
            this.field_184360_w = var2.field_70163_u;
            this.field_184361_x = var2.field_70161_v;
         }

      }
   }

   public void func_184339_a(CPacketConfirmTeleport var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (var1.func_186987_a() == this.field_184363_z) {
         this.field_147369_b.func_70080_a(this.field_184362_y.field_72450_a, this.field_184362_y.field_72448_b, this.field_184362_y.field_72449_c, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A);
         this.field_184352_o = this.field_184362_y.field_72450_a;
         this.field_184353_p = this.field_184362_y.field_72448_b;
         this.field_184354_q = this.field_184362_y.field_72449_c;
         if (this.field_147369_b.func_184850_K()) {
            this.field_147369_b.func_184846_L();
         }

         this.field_184362_y = null;
      }

   }

   public void func_191984_a(CPacketRecipeInfo var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (var1.func_194156_a() == CPacketRecipeInfo.Purpose.SHOWN) {
         IRecipe var2 = this.field_147367_d.func_199529_aN().func_199517_a(var1.func_199619_b());
         if (var2 != null) {
            this.field_147369_b.func_192037_E().func_194074_f(var2);
         }
      } else if (var1.func_194156_a() == CPacketRecipeInfo.Purpose.SETTINGS) {
         this.field_147369_b.func_192037_E().func_192813_a(var1.func_192624_c());
         this.field_147369_b.func_192037_E().func_192810_b(var1.func_192625_d());
         this.field_147369_b.func_192037_E().func_202881_c(var1.func_202496_e());
         this.field_147369_b.func_192037_E().func_202882_d(var1.func_202497_f());
      }

   }

   public void func_194027_a(CPacketSeenAdvancements var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (var1.func_194162_b() == CPacketSeenAdvancements.Action.OPENED_TAB) {
         ResourceLocation var2 = var1.func_194165_c();
         Advancement var3 = this.field_147367_d.func_191949_aK().func_192778_a(var2);
         if (var3 != null) {
            this.field_147369_b.func_192039_O().func_194220_a(var3);
         }
      }

   }

   public void func_195518_a(CPacketTabComplete var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      StringReader var2 = new StringReader(var1.func_197707_b());
      if (var2.canRead() && var2.peek() == '/') {
         var2.skip();
      }

      ParseResults var3 = this.field_147367_d.func_195571_aL().func_197054_a().parse(var2, this.field_147369_b.func_195051_bN());
      this.field_147367_d.func_195571_aL().func_197054_a().getCompletionSuggestions(var3).thenAccept((var2x) -> {
         this.field_147371_a.func_179290_a(new SPacketTabComplete(var1.func_197709_a(), var2x));
      });
   }

   public void func_210153_a(CPacketUpdateCommandBlock var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (!this.field_147367_d.func_82356_Z()) {
         this.field_147369_b.func_145747_a(new TextComponentTranslation("advMode.notEnabled", new Object[0]));
      } else if (!this.field_147369_b.func_195070_dx()) {
         this.field_147369_b.func_145747_a(new TextComponentTranslation("advMode.notAllowed", new Object[0]));
      } else {
         CommandBlockBaseLogic var2 = null;
         TileEntityCommandBlock var3 = null;
         BlockPos var4 = var1.func_210361_a();
         TileEntity var5 = this.field_147369_b.field_70170_p.func_175625_s(var4);
         if (var5 instanceof TileEntityCommandBlock) {
            var3 = (TileEntityCommandBlock)var5;
            var2 = var3.func_145993_a();
         }

         String var6 = var1.func_210359_b();
         boolean var7 = var1.func_210363_c();
         if (var2 != null) {
            EnumFacing var8 = (EnumFacing)this.field_147369_b.field_70170_p.func_180495_p(var4).func_177229_b(BlockCommandBlock.field_185564_a);
            IBlockState var9;
            switch(var1.func_210360_f()) {
            case SEQUENCE:
               var9 = Blocks.field_185777_dd.func_176223_P();
               this.field_147369_b.field_70170_p.func_180501_a(var4, (IBlockState)((IBlockState)var9.func_206870_a(BlockCommandBlock.field_185564_a, var8)).func_206870_a(BlockCommandBlock.field_185565_b, var1.func_210364_d()), 2);
               break;
            case AUTO:
               var9 = Blocks.field_185776_dc.func_176223_P();
               this.field_147369_b.field_70170_p.func_180501_a(var4, (IBlockState)((IBlockState)var9.func_206870_a(BlockCommandBlock.field_185564_a, var8)).func_206870_a(BlockCommandBlock.field_185565_b, var1.func_210364_d()), 2);
               break;
            case REDSTONE:
            default:
               var9 = Blocks.field_150483_bI.func_176223_P();
               this.field_147369_b.field_70170_p.func_180501_a(var4, (IBlockState)((IBlockState)var9.func_206870_a(BlockCommandBlock.field_185564_a, var8)).func_206870_a(BlockCommandBlock.field_185565_b, var1.func_210364_d()), 2);
            }

            var5.func_145829_t();
            this.field_147369_b.field_70170_p.func_175690_a(var4, var5);
            var2.func_145752_a(var6);
            var2.func_175573_a(var7);
            if (!var7) {
               var2.func_145750_b((ITextComponent)null);
            }

            var3.func_184253_b(var1.func_210362_e());
            var2.func_145756_e();
            if (!StringUtils.func_151246_b(var6)) {
               this.field_147369_b.func_145747_a(new TextComponentTranslation("advMode.setCommand.success", new Object[]{var6}));
            }
         }

      }
   }

   public void func_210158_a(CPacketUpdateCommandMinecart var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (!this.field_147367_d.func_82356_Z()) {
         this.field_147369_b.func_145747_a(new TextComponentTranslation("advMode.notEnabled", new Object[0]));
      } else if (!this.field_147369_b.func_195070_dx()) {
         this.field_147369_b.func_145747_a(new TextComponentTranslation("advMode.notAllowed", new Object[0]));
      } else {
         CommandBlockBaseLogic var2 = var1.func_210371_a(this.field_147369_b.field_70170_p);
         if (var2 != null) {
            var2.func_145752_a(var1.func_210372_a());
            var2.func_175573_a(var1.func_210373_b());
            if (!var1.func_210373_b()) {
               var2.func_145750_b((ITextComponent)null);
            }

            var2.func_145756_e();
            this.field_147369_b.func_145747_a(new TextComponentTranslation("advMode.setCommand.success", new Object[]{var1.func_210372_a()}));
         }

      }
   }

   public void func_210152_a(CPacketPickItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.field_71071_by.func_184430_d(var1.func_210349_a());
      this.field_147369_b.field_71135_a.func_147359_a(new SPacketSetSlot(-2, this.field_147369_b.field_71071_by.field_70461_c, this.field_147369_b.field_71071_by.func_70301_a(this.field_147369_b.field_71071_by.field_70461_c)));
      this.field_147369_b.field_71135_a.func_147359_a(new SPacketSetSlot(-2, var1.func_210349_a(), this.field_147369_b.field_71071_by.func_70301_a(var1.func_210349_a())));
      this.field_147369_b.field_71135_a.func_147359_a(new SPacketHeldItemChange(this.field_147369_b.field_71071_by.field_70461_c));
   }

   public void func_210155_a(CPacketRenameItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.field_71070_bA instanceof ContainerRepair) {
         ContainerRepair var2 = (ContainerRepair)this.field_147369_b.field_71070_bA;
         String var3 = SharedConstants.func_71565_a(var1.func_210351_a());
         if (var3.length() <= 35) {
            var2.func_82850_a(var3);
         }
      }

   }

   public void func_210154_a(CPacketUpdateBeacon var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.field_71070_bA instanceof ContainerBeacon) {
         ContainerBeacon var2 = (ContainerBeacon)this.field_147369_b.field_71070_bA;
         Slot var3 = var2.func_75139_a(0);
         if (var3.func_75216_d()) {
            var3.func_75209_a(1);
            IInventory var4 = var2.func_180611_e();
            var4.func_174885_b(1, var1.func_210355_a());
            var4.func_174885_b(2, var1.func_210356_b());
            var4.func_70296_d();
         }
      }

   }

   public void func_210157_a(CPacketUpdateStructureBlock var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.func_195070_dx()) {
         BlockPos var2 = var1.func_210380_a();
         IBlockState var3 = this.field_147369_b.field_70170_p.func_180495_p(var2);
         TileEntity var4 = this.field_147369_b.field_70170_p.func_175625_s(var2);
         if (var4 instanceof TileEntityStructure) {
            TileEntityStructure var5 = (TileEntityStructure)var4;
            var5.func_184405_a(var1.func_210378_c());
            var5.func_184404_a(var1.func_210377_d());
            var5.func_184414_b(var1.func_210383_e());
            var5.func_184409_c(var1.func_210385_f());
            var5.func_184411_a(var1.func_210386_g());
            var5.func_184408_a(var1.func_210379_h());
            var5.func_184410_b(var1.func_210388_i());
            var5.func_184406_a(var1.func_210389_j());
            var5.func_189703_e(var1.func_210390_k());
            var5.func_189710_f(var1.func_210387_l());
            var5.func_189718_a(var1.func_210382_m());
            var5.func_189725_a(var1.func_210381_n());
            if (var5.func_208404_d()) {
               String var6 = var5.func_189715_d();
               if (var1.func_210384_b() == TileEntityStructure.UpdateCommand.SAVE_AREA) {
                  if (var5.func_184419_m()) {
                     this.field_147369_b.func_146105_b(new TextComponentTranslation("structure_block.save_success", new Object[]{var6}), false);
                  } else {
                     this.field_147369_b.func_146105_b(new TextComponentTranslation("structure_block.save_failure", new Object[]{var6}), false);
                  }
               } else if (var1.func_210384_b() == TileEntityStructure.UpdateCommand.LOAD_AREA) {
                  if (!var5.func_189709_F()) {
                     this.field_147369_b.func_146105_b(new TextComponentTranslation("structure_block.load_not_found", new Object[]{var6}), false);
                  } else if (var5.func_184412_n()) {
                     this.field_147369_b.func_146105_b(new TextComponentTranslation("structure_block.load_success", new Object[]{var6}), false);
                  } else {
                     this.field_147369_b.func_146105_b(new TextComponentTranslation("structure_block.load_prepare", new Object[]{var6}), false);
                  }
               } else if (var1.func_210384_b() == TileEntityStructure.UpdateCommand.SCAN_AREA) {
                  if (var5.func_184417_l()) {
                     this.field_147369_b.func_146105_b(new TextComponentTranslation("structure_block.size_success", new Object[]{var6}), false);
                  } else {
                     this.field_147369_b.func_146105_b(new TextComponentTranslation("structure_block.size_failure", new Object[0]), false);
                  }
               }
            } else {
               this.field_147369_b.func_146105_b(new TextComponentTranslation("structure_block.invalid_structure_name", new Object[]{var1.func_210377_d()}), false);
            }

            var5.func_70296_d();
            this.field_147369_b.field_70170_p.func_184138_a(var2, var3, var3, 3);
         }

      }
   }

   public void func_210159_a(CPacketSelectTrade var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      int var2 = var1.func_210353_a();
      Container var3 = this.field_147369_b.field_71070_bA;
      if (var3 instanceof ContainerMerchant) {
         ((ContainerMerchant)var3).func_75175_c(var2);
      }

   }

   public void func_210156_a(CPacketEditBook var1) {
      ItemStack var2 = var1.func_210346_a();
      if (!var2.func_190926_b()) {
         if (ItemWritableBook.func_150930_a(var2.func_77978_p())) {
            ItemStack var3 = this.field_147369_b.func_184586_b(var1.func_212644_d());
            if (!var3.func_190926_b()) {
               if (var2.func_77973_b() == Items.field_151099_bA && var3.func_77973_b() == Items.field_151099_bA) {
                  if (var1.func_210345_b()) {
                     ItemStack var4 = new ItemStack(Items.field_151164_bB);
                     var4.func_77983_a("author", new NBTTagString(this.field_147369_b.func_200200_C_().getString()));
                     var4.func_77983_a("title", new NBTTagString(var2.func_77978_p().func_74779_i("title")));
                     NBTTagList var5 = var2.func_77978_p().func_150295_c("pages", 8);

                     for(int var6 = 0; var6 < var5.size(); ++var6) {
                        String var7 = var5.func_150307_f(var6);
                        TextComponentString var8 = new TextComponentString(var7);
                        var7 = ITextComponent.Serializer.func_150696_a(var8);
                        var5.set(var6, (INBTBase)(new NBTTagString(var7)));
                     }

                     var4.func_77983_a("pages", var5);
                     EntityEquipmentSlot var9 = var1.func_212644_d() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND;
                     this.field_147369_b.func_184201_a(var9, var4);
                  } else {
                     var3.func_77983_a("pages", var2.func_77978_p().func_150295_c("pages", 8));
                  }
               }

            }
         }
      }
   }

   public void func_211526_a(CPacketNBTQueryEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.func_211513_k(2)) {
         Entity var2 = this.field_147369_b.func_71121_q().func_73045_a(var1.func_211720_c());
         if (var2 != null) {
            NBTTagCompound var3 = var2.func_189511_e(new NBTTagCompound());
            this.field_147369_b.field_71135_a.func_147359_a(new SPacketNBTQueryResponse(var1.func_211721_b(), var3));
         }

      }
   }

   public void func_211525_a(CPacketNBTQueryTileEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.func_211513_k(2)) {
         TileEntity var2 = this.field_147369_b.func_71121_q().func_175625_s(var1.func_211717_c());
         NBTTagCompound var3 = var2 != null ? var2.func_189515_b(new NBTTagCompound()) : null;
         this.field_147369_b.field_71135_a.func_147359_a(new SPacketNBTQueryResponse(var1.func_211716_b(), var3));
      }
   }

   public void func_147347_a(CPacketPlayer var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (func_183006_b(var1)) {
         this.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.invalid_player_movement", new Object[0]));
      } else {
         WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
         if (!this.field_147369_b.field_71136_j) {
            if (this.field_147368_e == 0) {
               this.func_184342_d();
            }

            if (this.field_184362_y != null) {
               if (this.field_147368_e - this.field_184343_A > 20) {
                  this.field_184343_A = this.field_147368_e;
                  this.func_147364_a(this.field_184362_y.field_72450_a, this.field_184362_y.field_72448_b, this.field_184362_y.field_72449_c, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A);
               }

            } else {
               this.field_184343_A = this.field_147368_e;
               if (this.field_147369_b.func_184218_aH()) {
                  this.field_147369_b.func_70080_a(this.field_147369_b.field_70165_t, this.field_147369_b.field_70163_u, this.field_147369_b.field_70161_v, var1.func_186999_a(this.field_147369_b.field_70177_z), var1.func_186998_b(this.field_147369_b.field_70125_A));
                  this.field_147367_d.func_184103_al().func_72358_d(this.field_147369_b);
               } else {
                  double var3 = this.field_147369_b.field_70165_t;
                  double var5 = this.field_147369_b.field_70163_u;
                  double var7 = this.field_147369_b.field_70161_v;
                  double var9 = this.field_147369_b.field_70163_u;
                  double var11 = var1.func_186997_a(this.field_147369_b.field_70165_t);
                  double var13 = var1.func_186996_b(this.field_147369_b.field_70163_u);
                  double var15 = var1.func_187000_c(this.field_147369_b.field_70161_v);
                  float var17 = var1.func_186999_a(this.field_147369_b.field_70177_z);
                  float var18 = var1.func_186998_b(this.field_147369_b.field_70125_A);
                  double var19 = var11 - this.field_184349_l;
                  double var21 = var13 - this.field_184350_m;
                  double var23 = var15 - this.field_184351_n;
                  double var25 = this.field_147369_b.field_70159_w * this.field_147369_b.field_70159_w + this.field_147369_b.field_70181_x * this.field_147369_b.field_70181_x + this.field_147369_b.field_70179_y * this.field_147369_b.field_70179_y;
                  double var27 = var19 * var19 + var21 * var21 + var23 * var23;
                  if (this.field_147369_b.func_70608_bn()) {
                     if (var27 > 1.0D) {
                        this.func_147364_a(this.field_147369_b.field_70165_t, this.field_147369_b.field_70163_u, this.field_147369_b.field_70161_v, var1.func_186999_a(this.field_147369_b.field_70177_z), var1.func_186998_b(this.field_147369_b.field_70125_A));
                     }

                  } else {
                     ++this.field_184347_F;
                     int var29 = this.field_184347_F - this.field_184348_G;
                     if (var29 > 5) {
                        field_147370_c.debug("{} is sending move packets too frequently ({} packets since last tick)", this.field_147369_b.func_200200_C_().getString(), var29);
                        var29 = 1;
                     }

                     if (!this.field_147369_b.func_184850_K() && (!this.field_147369_b.func_71121_q().func_82736_K().func_82766_b("disableElytraMovementCheck") || !this.field_147369_b.func_184613_cA())) {
                        float var30 = this.field_147369_b.func_184613_cA() ? 300.0F : 100.0F;
                        if (var27 - var25 > (double)(var30 * (float)var29) && (!this.field_147367_d.func_71264_H() || !this.field_147367_d.func_71214_G().equals(this.field_147369_b.func_146103_bH().getName()))) {
                           field_147370_c.warn("{} moved too quickly! {},{},{}", this.field_147369_b.func_200200_C_().getString(), var19, var21, var23);
                           this.func_147364_a(this.field_147369_b.field_70165_t, this.field_147369_b.field_70163_u, this.field_147369_b.field_70161_v, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A);
                           return;
                        }
                     }

                     boolean var35 = var2.func_195586_b(this.field_147369_b, this.field_147369_b.func_174813_aQ().func_186664_h(0.0625D));
                     var19 = var11 - this.field_184352_o;
                     var21 = var13 - this.field_184353_p;
                     var23 = var15 - this.field_184354_q;
                     if (this.field_147369_b.field_70122_E && !var1.func_149465_i() && var21 > 0.0D) {
                        this.field_147369_b.func_70664_aZ();
                     }

                     this.field_147369_b.func_70091_d(MoverType.PLAYER, var19, var21, var23);
                     this.field_147369_b.field_70122_E = var1.func_149465_i();
                     double var31 = var21;
                     var19 = var11 - this.field_147369_b.field_70165_t;
                     var21 = var13 - this.field_147369_b.field_70163_u;
                     if (var21 > -0.5D || var21 < 0.5D) {
                        var21 = 0.0D;
                     }

                     var23 = var15 - this.field_147369_b.field_70161_v;
                     var27 = var19 * var19 + var21 * var21 + var23 * var23;
                     boolean var33 = false;
                     if (!this.field_147369_b.func_184850_K() && var27 > 0.0625D && !this.field_147369_b.func_70608_bn() && !this.field_147369_b.field_71134_c.func_73083_d() && this.field_147369_b.field_71134_c.func_73081_b() != GameType.SPECTATOR) {
                        var33 = true;
                        field_147370_c.warn("{} moved wrongly!", this.field_147369_b.func_200200_C_().getString());
                     }

                     this.field_147369_b.func_70080_a(var11, var13, var15, var17, var18);
                     this.field_147369_b.func_71000_j(this.field_147369_b.field_70165_t - var3, this.field_147369_b.field_70163_u - var5, this.field_147369_b.field_70161_v - var7);
                     if (!this.field_147369_b.field_70145_X && !this.field_147369_b.func_70608_bn()) {
                        boolean var34 = var2.func_195586_b(this.field_147369_b, this.field_147369_b.func_174813_aQ().func_186664_h(0.0625D));
                        if (var35 && (var33 || !var34)) {
                           this.func_147364_a(var3, var5, var7, var17, var18);
                           return;
                        }
                     }

                     this.field_184344_B = var31 >= -0.03125D;
                     this.field_184344_B &= !this.field_147367_d.func_71231_X() && !this.field_147369_b.field_71075_bZ.field_75101_c;
                     this.field_184344_B &= !this.field_147369_b.func_70644_a(MobEffects.field_188424_y) && !this.field_147369_b.func_184613_cA() && !var2.func_72829_c(this.field_147369_b.func_174813_aQ().func_186662_g(0.0625D).func_72321_a(0.0D, -0.55D, 0.0D));
                     this.field_147369_b.field_70122_E = var1.func_149465_i();
                     this.field_147367_d.func_184103_al().func_72358_d(this.field_147369_b);
                     this.field_147369_b.func_71122_b(this.field_147369_b.field_70163_u - var9, var1.func_149465_i());
                     this.field_184352_o = this.field_147369_b.field_70165_t;
                     this.field_184353_p = this.field_147369_b.field_70163_u;
                     this.field_184354_q = this.field_147369_b.field_70161_v;
                  }
               }
            }
         }
      }
   }

   public void func_147364_a(double var1, double var3, double var5, float var7, float var8) {
      this.func_175089_a(var1, var3, var5, var7, var8, Collections.emptySet());
   }

   public void func_175089_a(double var1, double var3, double var5, float var7, float var8, Set<SPacketPlayerPosLook.EnumFlags> var9) {
      double var10 = var9.contains(SPacketPlayerPosLook.EnumFlags.X) ? this.field_147369_b.field_70165_t : 0.0D;
      double var12 = var9.contains(SPacketPlayerPosLook.EnumFlags.Y) ? this.field_147369_b.field_70163_u : 0.0D;
      double var14 = var9.contains(SPacketPlayerPosLook.EnumFlags.Z) ? this.field_147369_b.field_70161_v : 0.0D;
      float var16 = var9.contains(SPacketPlayerPosLook.EnumFlags.Y_ROT) ? this.field_147369_b.field_70177_z : 0.0F;
      float var17 = var9.contains(SPacketPlayerPosLook.EnumFlags.X_ROT) ? this.field_147369_b.field_70125_A : 0.0F;
      this.field_184362_y = new Vec3d(var1, var3, var5);
      if (++this.field_184363_z == 2147483647) {
         this.field_184363_z = 0;
      }

      this.field_184343_A = this.field_147368_e;
      this.field_147369_b.func_70080_a(var1, var3, var5, var7, var8);
      this.field_147369_b.field_71135_a.func_147359_a(new SPacketPlayerPosLook(var1 - var10, var3 - var12, var5 - var14, var7 - var16, var8 - var17, var9, this.field_184363_z));
   }

   public void func_147345_a(CPacketPlayerDigging var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      BlockPos var3 = var1.func_179715_a();
      this.field_147369_b.func_143004_u();
      switch(var1.func_180762_c()) {
      case SWAP_HELD_ITEMS:
         if (!this.field_147369_b.func_175149_v()) {
            ItemStack var12 = this.field_147369_b.func_184586_b(EnumHand.OFF_HAND);
            this.field_147369_b.func_184611_a(EnumHand.OFF_HAND, this.field_147369_b.func_184586_b(EnumHand.MAIN_HAND));
            this.field_147369_b.func_184611_a(EnumHand.MAIN_HAND, var12);
         }

         return;
      case DROP_ITEM:
         if (!this.field_147369_b.func_175149_v()) {
            this.field_147369_b.func_71040_bB(false);
         }

         return;
      case DROP_ALL_ITEMS:
         if (!this.field_147369_b.func_175149_v()) {
            this.field_147369_b.func_71040_bB(true);
         }

         return;
      case RELEASE_USE_ITEM:
         this.field_147369_b.func_184597_cx();
         return;
      case START_DESTROY_BLOCK:
      case ABORT_DESTROY_BLOCK:
      case STOP_DESTROY_BLOCK:
         double var4 = this.field_147369_b.field_70165_t - ((double)var3.func_177958_n() + 0.5D);
         double var6 = this.field_147369_b.field_70163_u - ((double)var3.func_177956_o() + 0.5D) + 1.5D;
         double var8 = this.field_147369_b.field_70161_v - ((double)var3.func_177952_p() + 0.5D);
         double var10 = var4 * var4 + var6 * var6 + var8 * var8;
         if (var10 > 36.0D) {
            return;
         } else if (var3.func_177956_o() >= this.field_147367_d.func_71207_Z()) {
            return;
         } else {
            if (var1.func_180762_c() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               if (!this.field_147367_d.func_175579_a(var2, var3, this.field_147369_b) && var2.func_175723_af().func_177746_a(var3)) {
                  this.field_147369_b.field_71134_c.func_180784_a(var3, var1.func_179714_b());
               } else {
                  this.field_147369_b.field_71135_a.func_147359_a(new SPacketBlockChange(var2, var3));
               }
            } else {
               if (var1.func_180762_c() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                  this.field_147369_b.field_71134_c.func_180785_a(var3);
               } else if (var1.func_180762_c() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                  this.field_147369_b.field_71134_c.func_180238_e();
               }

               if (!var2.func_180495_p(var3).func_196958_f()) {
                  this.field_147369_b.field_71135_a.func_147359_a(new SPacketBlockChange(var2, var3));
               }
            }

            return;
         }
      default:
         throw new IllegalArgumentException("Invalid player action");
      }
   }

   public void func_184337_a(CPacketPlayerTryUseItemOnBlock var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      EnumHand var3 = var1.func_187022_c();
      ItemStack var4 = this.field_147369_b.func_184586_b(var3);
      BlockPos var5 = var1.func_187023_a();
      EnumFacing var6 = var1.func_187024_b();
      this.field_147369_b.func_143004_u();
      if (var5.func_177956_o() >= this.field_147367_d.func_71207_Z() - 1 && (var6 == EnumFacing.UP || var5.func_177956_o() >= this.field_147367_d.func_71207_Z())) {
         ITextComponent var7 = (new TextComponentTranslation("build.tooHigh", new Object[]{this.field_147367_d.func_71207_Z()})).func_211708_a(TextFormatting.RED);
         this.field_147369_b.field_71135_a.func_147359_a(new SPacketChat(var7, ChatType.GAME_INFO));
      } else if (this.field_184362_y == null && this.field_147369_b.func_70092_e((double)var5.func_177958_n() + 0.5D, (double)var5.func_177956_o() + 0.5D, (double)var5.func_177952_p() + 0.5D) < 64.0D && !this.field_147367_d.func_175579_a(var2, var5, this.field_147369_b) && var2.func_175723_af().func_177746_a(var5)) {
         this.field_147369_b.field_71134_c.func_187251_a(this.field_147369_b, var2, var4, var3, var5, var6, var1.func_187026_d(), var1.func_187025_e(), var1.func_187020_f());
      }

      this.field_147369_b.field_71135_a.func_147359_a(new SPacketBlockChange(var2, var5));
      this.field_147369_b.field_71135_a.func_147359_a(new SPacketBlockChange(var2, var5.func_177972_a(var6)));
   }

   public void func_147346_a(CPacketPlayerTryUseItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      EnumHand var3 = var1.func_187028_a();
      ItemStack var4 = this.field_147369_b.func_184586_b(var3);
      this.field_147369_b.func_143004_u();
      if (!var4.func_190926_b()) {
         this.field_147369_b.field_71134_c.func_187250_a(this.field_147369_b, var2, var4, var3);
      }
   }

   public void func_175088_a(CPacketSpectate var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.func_175149_v()) {
         Entity var2 = null;
         Iterator var3 = this.field_147367_d.func_212370_w().iterator();

         while(var3.hasNext()) {
            WorldServer var4 = (WorldServer)var3.next();
            var2 = var1.func_179727_a(var4);
            if (var2 != null) {
               break;
            }
         }

         if (var2 != null) {
            this.field_147369_b.func_200619_a((WorldServer)var2.field_70170_p, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var2.field_70177_z, var2.field_70125_A);
         }
      }

   }

   public void func_175086_a(CPacketResourcePackStatus var1) {
   }

   public void func_184340_a(CPacketSteerBoat var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      Entity var2 = this.field_147369_b.func_184187_bx();
      if (var2 instanceof EntityBoat) {
         ((EntityBoat)var2).func_184445_a(var1.func_187012_a(), var1.func_187014_b());
      }

   }

   public void func_147231_a(ITextComponent var1) {
      field_147370_c.info("{} lost connection: {}", this.field_147369_b.func_200200_C_().getString(), var1.getString());
      this.field_147367_d.func_147132_au();
      this.field_147367_d.func_184103_al().func_148539_a((new TextComponentTranslation("multiplayer.player.left", new Object[]{this.field_147369_b.func_145748_c_()})).func_211708_a(TextFormatting.YELLOW));
      this.field_147369_b.func_71123_m();
      this.field_147367_d.func_184103_al().func_72367_e(this.field_147369_b);
      if (this.field_147367_d.func_71264_H() && this.field_147369_b.func_200200_C_().getString().equals(this.field_147367_d.func_71214_G())) {
         field_147370_c.info("Stopping singleplayer server as player logged out");
         this.field_147367_d.func_71263_m();
      }

   }

   public void func_147359_a(Packet<?> var1) {
      this.func_211148_a(var1, (GenericFutureListener)null);
   }

   public void func_211148_a(Packet<?> var1, @Nullable GenericFutureListener<? extends Future<? super Void>> var2) {
      if (var1 instanceof SPacketChat) {
         SPacketChat var3 = (SPacketChat)var1;
         EntityPlayer.EnumChatVisibility var4 = this.field_147369_b.func_147096_v();
         if (var4 == EntityPlayer.EnumChatVisibility.HIDDEN && var3.func_192590_c() != ChatType.GAME_INFO) {
            return;
         }

         if (var4 == EntityPlayer.EnumChatVisibility.SYSTEM && !var3.func_148916_d()) {
            return;
         }
      }

      try {
         this.field_147371_a.func_201058_a(var1, var2);
      } catch (Throwable var6) {
         CrashReport var7 = CrashReport.func_85055_a(var6, "Sending packet");
         CrashReportCategory var5 = var7.func_85058_a("Packet being sent");
         var5.func_189529_a("Packet class", () -> {
            return var1.getClass().getCanonicalName();
         });
         throw new ReportedException(var7);
      }
   }

   public void func_147355_a(CPacketHeldItemChange var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (var1.func_149614_c() >= 0 && var1.func_149614_c() < InventoryPlayer.func_70451_h()) {
         this.field_147369_b.field_71071_by.field_70461_c = var1.func_149614_c();
         this.field_147369_b.func_143004_u();
      } else {
         field_147370_c.warn("{} tried to set an invalid carried item", this.field_147369_b.func_200200_C_().getString());
      }
   }

   public void func_147354_a(CPacketChatMessage var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.func_147096_v() == EntityPlayer.EnumChatVisibility.HIDDEN) {
         this.func_147359_a(new SPacketChat((new TextComponentTranslation("chat.cannotSend", new Object[0])).func_211708_a(TextFormatting.RED)));
      } else {
         this.field_147369_b.func_143004_u();
         String var2 = var1.func_149439_c();
         var2 = org.apache.commons.lang3.StringUtils.normalizeSpace(var2);

         for(int var3 = 0; var3 < var2.length(); ++var3) {
            if (!SharedConstants.func_71566_a(var2.charAt(var3))) {
               this.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.illegal_characters", new Object[0]));
               return;
            }
         }

         if (var2.startsWith("/")) {
            this.func_147361_d(var2);
         } else {
            TextComponentTranslation var4 = new TextComponentTranslation("chat.type.text", new Object[]{this.field_147369_b.func_145748_c_(), var2});
            this.field_147367_d.func_184103_al().func_148544_a(var4, false);
         }

         this.field_147374_l += 20;
         if (this.field_147374_l > 200 && !this.field_147367_d.func_184103_al().func_152596_g(this.field_147369_b.func_146103_bH())) {
            this.func_194028_b(new TextComponentTranslation("disconnect.spam", new Object[0]));
         }

      }
   }

   private void func_147361_d(String var1) {
      this.field_147367_d.func_195571_aL().func_197059_a(this.field_147369_b.func_195051_bN(), var1);
   }

   public void func_175087_a(CPacketAnimation var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      this.field_147369_b.func_184609_a(var1.func_187018_a());
   }

   public void func_147357_a(CPacketEntityAction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      IJumpingMount var4;
      switch(var1.func_180764_b()) {
      case START_SNEAKING:
         this.field_147369_b.func_70095_a(true);
         break;
      case STOP_SNEAKING:
         this.field_147369_b.func_70095_a(false);
         break;
      case START_SPRINTING:
         this.field_147369_b.func_70031_b(true);
         break;
      case STOP_SPRINTING:
         this.field_147369_b.func_70031_b(false);
         break;
      case STOP_SLEEPING:
         if (this.field_147369_b.func_70608_bn()) {
            this.field_147369_b.func_70999_a(false, true, true);
            this.field_184362_y = new Vec3d(this.field_147369_b.field_70165_t, this.field_147369_b.field_70163_u, this.field_147369_b.field_70161_v);
         }
         break;
      case START_RIDING_JUMP:
         if (this.field_147369_b.func_184187_bx() instanceof IJumpingMount) {
            var4 = (IJumpingMount)this.field_147369_b.func_184187_bx();
            int var3 = var1.func_149512_e();
            if (var4.func_184776_b() && var3 > 0) {
               var4.func_184775_b(var3);
            }
         }
         break;
      case STOP_RIDING_JUMP:
         if (this.field_147369_b.func_184187_bx() instanceof IJumpingMount) {
            var4 = (IJumpingMount)this.field_147369_b.func_184187_bx();
            var4.func_184777_r_();
         }
         break;
      case OPEN_INVENTORY:
         if (this.field_147369_b.func_184187_bx() instanceof AbstractHorse) {
            ((AbstractHorse)this.field_147369_b.func_184187_bx()).func_110199_f(this.field_147369_b);
         }
         break;
      case START_FALL_FLYING:
         if (!this.field_147369_b.field_70122_E && this.field_147369_b.field_70181_x < 0.0D && !this.field_147369_b.func_184613_cA() && !this.field_147369_b.func_70090_H()) {
            ItemStack var2 = this.field_147369_b.func_184582_a(EntityEquipmentSlot.CHEST);
            if (var2.func_77973_b() == Items.field_185160_cR && ItemElytra.func_185069_d(var2)) {
               this.field_147369_b.func_184847_M();
            }
         } else {
            this.field_147369_b.func_189103_N();
         }
         break;
      default:
         throw new IllegalArgumentException("Invalid client command!");
      }

   }

   public void func_147340_a(CPacketUseEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      Entity var3 = var1.func_149564_a(var2);
      this.field_147369_b.func_143004_u();
      if (var3 != null) {
         boolean var4 = this.field_147369_b.func_70685_l(var3);
         double var5 = 36.0D;
         if (!var4) {
            var5 = 9.0D;
         }

         if (this.field_147369_b.func_70068_e(var3) < var5) {
            EnumHand var7;
            if (var1.func_149565_c() == CPacketUseEntity.Action.INTERACT) {
               var7 = var1.func_186994_b();
               this.field_147369_b.func_190775_a(var3, var7);
            } else if (var1.func_149565_c() == CPacketUseEntity.Action.INTERACT_AT) {
               var7 = var1.func_186994_b();
               var3.func_184199_a(this.field_147369_b, var1.func_179712_b(), var7);
            } else if (var1.func_149565_c() == CPacketUseEntity.Action.ATTACK) {
               if (var3 instanceof EntityItem || var3 instanceof EntityXPOrb || var3 instanceof EntityArrow || var3 == this.field_147369_b) {
                  this.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
                  this.field_147367_d.func_71236_h("Player " + this.field_147369_b.func_200200_C_().getString() + " tried to attack an invalid entity");
                  return;
               }

               this.field_147369_b.func_71059_n(var3);
            }
         }
      }

   }

   public void func_147342_a(CPacketClientStatus var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      CPacketClientStatus.State var2 = var1.func_149435_c();
      switch(var2) {
      case PERFORM_RESPAWN:
         if (this.field_147369_b.field_71136_j) {
            this.field_147369_b.field_71136_j = false;
            this.field_147369_b = this.field_147367_d.func_184103_al().func_72368_a(this.field_147369_b, DimensionType.OVERWORLD, true);
            CriteriaTriggers.field_193134_u.func_193143_a(this.field_147369_b, DimensionType.THE_END, DimensionType.OVERWORLD);
         } else {
            if (this.field_147369_b.func_110143_aJ() > 0.0F) {
               return;
            }

            this.field_147369_b = this.field_147367_d.func_184103_al().func_72368_a(this.field_147369_b, DimensionType.OVERWORLD, false);
            if (this.field_147367_d.func_71199_h()) {
               this.field_147369_b.func_71033_a(GameType.SPECTATOR);
               this.field_147369_b.func_71121_q().func_82736_K().func_82764_b("spectatorsGenerateChunks", "false", this.field_147367_d);
            }
         }
         break;
      case REQUEST_STATS:
         this.field_147369_b.func_147099_x().func_150876_a(this.field_147369_b);
      }

   }

   public void func_147356_a(CPacketCloseWindow var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_71128_l();
   }

   public void func_147351_a(CPacketClickWindow var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      if (this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149548_c() && this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b)) {
         if (this.field_147369_b.func_175149_v()) {
            NonNullList var2 = NonNullList.func_191196_a();

            for(int var3 = 0; var3 < this.field_147369_b.field_71070_bA.field_75151_b.size(); ++var3) {
               var2.add(((Slot)this.field_147369_b.field_71070_bA.field_75151_b.get(var3)).func_75211_c());
            }

            this.field_147369_b.func_71110_a(this.field_147369_b.field_71070_bA, var2);
         } else {
            ItemStack var6 = this.field_147369_b.field_71070_bA.func_184996_a(var1.func_149544_d(), var1.func_149543_e(), var1.func_186993_f(), this.field_147369_b);
            if (ItemStack.func_77989_b(var1.func_149546_g(), var6)) {
               this.field_147369_b.field_71135_a.func_147359_a(new SPacketConfirmTransaction(var1.func_149548_c(), var1.func_149547_f(), true));
               this.field_147369_b.field_71137_h = true;
               this.field_147369_b.field_71070_bA.func_75142_b();
               this.field_147369_b.func_71113_k();
               this.field_147369_b.field_71137_h = false;
            } else {
               this.field_147372_n.func_76038_a(this.field_147369_b.field_71070_bA.field_75152_c, var1.func_149547_f());
               this.field_147369_b.field_71135_a.func_147359_a(new SPacketConfirmTransaction(var1.func_149548_c(), var1.func_149547_f(), false));
               this.field_147369_b.field_71070_bA.func_75128_a(this.field_147369_b, false);
               NonNullList var7 = NonNullList.func_191196_a();

               for(int var4 = 0; var4 < this.field_147369_b.field_71070_bA.field_75151_b.size(); ++var4) {
                  ItemStack var5 = ((Slot)this.field_147369_b.field_71070_bA.field_75151_b.get(var4)).func_75211_c();
                  var7.add(var5.func_190926_b() ? ItemStack.field_190927_a : var5);
               }

               this.field_147369_b.func_71110_a(this.field_147369_b.field_71070_bA, var7);
            }
         }
      }

   }

   public void func_194308_a(CPacketPlaceRecipe var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      if (!this.field_147369_b.func_175149_v() && this.field_147369_b.field_71070_bA.field_75152_c == var1.func_194318_a() && this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b)) {
         IRecipe var2 = this.field_147367_d.func_199529_aN().func_199517_a(var1.func_199618_b());
         if (this.field_147369_b.field_71070_bA instanceof ContainerFurnace) {
            (new ServerRecipePlacerFurnace()).func_194327_a(this.field_147369_b, var2, var1.func_194319_c());
         } else {
            (new ServerRecipePlacer()).func_194327_a(this.field_147369_b, var2, var1.func_194319_c());
         }

      }
   }

   public void func_147338_a(CPacketEnchantItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      if (this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149539_c() && this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b) && !this.field_147369_b.func_175149_v()) {
         this.field_147369_b.field_71070_bA.func_75140_a(this.field_147369_b, var1.func_149537_d());
         this.field_147369_b.field_71070_bA.func_75142_b();
      }

   }

   public void func_147344_a(CPacketCreativeInventoryAction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.field_71134_c.func_73083_d()) {
         boolean var2 = var1.func_149627_c() < 0;
         ItemStack var3 = var1.func_149625_d();
         NBTTagCompound var4 = var3.func_179543_a("BlockEntityTag");
         if (!var3.func_190926_b() && var4 != null && var4.func_74764_b("x") && var4.func_74764_b("y") && var4.func_74764_b("z")) {
            BlockPos var5 = new BlockPos(var4.func_74762_e("x"), var4.func_74762_e("y"), var4.func_74762_e("z"));
            TileEntity var6 = this.field_147369_b.field_70170_p.func_175625_s(var5);
            if (var6 != null) {
               NBTTagCompound var7 = var6.func_189515_b(new NBTTagCompound());
               var7.func_82580_o("x");
               var7.func_82580_o("y");
               var7.func_82580_o("z");
               var3.func_77983_a("BlockEntityTag", var7);
            }
         }

         boolean var8 = var1.func_149627_c() >= 1 && var1.func_149627_c() <= 45;
         boolean var9 = var3.func_190926_b() || var3.func_77952_i() >= 0 && var3.func_190916_E() <= 64 && !var3.func_190926_b();
         if (var8 && var9) {
            if (var3.func_190926_b()) {
               this.field_147369_b.field_71069_bz.func_75141_a(var1.func_149627_c(), ItemStack.field_190927_a);
            } else {
               this.field_147369_b.field_71069_bz.func_75141_a(var1.func_149627_c(), var3);
            }

            this.field_147369_b.field_71069_bz.func_75128_a(this.field_147369_b, true);
         } else if (var2 && var9 && this.field_147375_m < 200) {
            this.field_147375_m += 20;
            EntityItem var10 = this.field_147369_b.func_71019_a(var3, true);
            if (var10 != null) {
               var10.func_70288_d();
            }
         }
      }

   }

   public void func_147339_a(CPacketConfirmTransaction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      Short var2 = (Short)this.field_147372_n.func_76041_a(this.field_147369_b.field_71070_bA.field_75152_c);
      if (var2 != null && var1.func_149533_d() == var2 && this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149532_c() && !this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b) && !this.field_147369_b.func_175149_v()) {
         this.field_147369_b.field_71070_bA.func_75128_a(this.field_147369_b, true);
      }

   }

   public void func_147343_a(CPacketUpdateSign var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      BlockPos var3 = var1.func_179722_a();
      if (var2.func_175667_e(var3)) {
         IBlockState var4 = var2.func_180495_p(var3);
         TileEntity var5 = var2.func_175625_s(var3);
         if (!(var5 instanceof TileEntitySign)) {
            return;
         }

         TileEntitySign var6 = (TileEntitySign)var5;
         if (!var6.func_145914_a() || var6.func_145911_b() != this.field_147369_b) {
            this.field_147367_d.func_71236_h("Player " + this.field_147369_b.func_200200_C_().getString() + " just tried to change non-editable sign");
            return;
         }

         String[] var7 = var1.func_187017_b();

         for(int var8 = 0; var8 < var7.length; ++var8) {
            var6.func_212365_a(var8, new TextComponentString(TextFormatting.func_110646_a(var7[var8])));
         }

         var6.func_70296_d();
         var2.func_184138_a(var3, var4, var4, 3);
      }

   }

   public void func_147353_a(CPacketKeepAlive var1) {
      if (this.field_194403_g && var1.func_149460_c() == this.field_194404_h) {
         int var2 = (int)(Util.func_211177_b() - this.field_194402_f);
         this.field_147369_b.field_71138_i = (this.field_147369_b.field_71138_i * 3 + var2) / 4;
         this.field_194403_g = false;
      } else if (!this.field_147369_b.func_200200_C_().getString().equals(this.field_147367_d.func_71214_G())) {
         this.func_194028_b(new TextComponentTranslation("disconnect.timeout", new Object[0]));
      }

   }

   public void func_147348_a(CPacketPlayerAbilities var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.field_71075_bZ.field_75100_b = var1.func_149488_d() && this.field_147369_b.field_71075_bZ.field_75101_c;
   }

   public void func_147352_a(CPacketClientSettings var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_147100_a(var1);
   }

   public void func_147349_a(CPacketCustomPayload var1) {
   }
}
