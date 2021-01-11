package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer, ITickable {
   private static final Logger field_147370_c = LogManager.getLogger();
   public final NetworkManager field_147371_a;
   private final MinecraftServer field_147367_d;
   public EntityPlayerMP field_147369_b;
   private int field_147368_e;
   private int field_175090_f;
   private int field_147365_f;
   private boolean field_147366_g;
   private int field_147378_h;
   private long field_147379_i;
   private long field_147377_k;
   private int field_147374_l;
   private int field_147375_m;
   private IntHashMap<Short> field_147372_n = new IntHashMap();
   private double field_147373_o;
   private double field_147382_p;
   private double field_147381_q;
   private boolean field_147380_r = true;

   public NetHandlerPlayServer(MinecraftServer var1, NetworkManager var2, EntityPlayerMP var3) {
      super();
      this.field_147367_d = var1;
      this.field_147371_a = var2;
      var2.func_150719_a(this);
      this.field_147369_b = var3;
      var3.field_71135_a = this;
   }

   public void func_73660_a() {
      this.field_147366_g = false;
      ++this.field_147368_e;
      this.field_147367_d.field_71304_b.func_76320_a("keepAlive");
      if ((long)this.field_147368_e - this.field_147377_k > 40L) {
         this.field_147377_k = (long)this.field_147368_e;
         this.field_147379_i = this.func_147363_d();
         this.field_147378_h = (int)this.field_147379_i;
         this.func_147359_a(new S00PacketKeepAlive(this.field_147378_h));
      }

      this.field_147367_d.field_71304_b.func_76319_b();
      if (this.field_147374_l > 0) {
         --this.field_147374_l;
      }

      if (this.field_147375_m > 0) {
         --this.field_147375_m;
      }

      if (this.field_147369_b.func_154331_x() > 0L && this.field_147367_d.func_143007_ar() > 0 && MinecraftServer.func_130071_aq() - this.field_147369_b.func_154331_x() > (long)(this.field_147367_d.func_143007_ar() * 1000 * 60)) {
         this.func_147360_c("You have been idle for too long!");
      }

   }

   public NetworkManager func_147362_b() {
      return this.field_147371_a;
   }

   public void func_147360_c(String var1) {
      final ChatComponentText var2 = new ChatComponentText(var1);
      this.field_147371_a.func_179288_a(new S40PacketDisconnect(var2), new GenericFutureListener<Future<? super Void>>() {
         public void operationComplete(Future<? super Void> var1) throws Exception {
            NetHandlerPlayServer.this.field_147371_a.func_150718_a(var2);
         }
      });
      this.field_147371_a.func_150721_g();
      Futures.getUnchecked(this.field_147367_d.func_152344_a(new Runnable() {
         public void run() {
            NetHandlerPlayServer.this.field_147371_a.func_179293_l();
         }
      }));
   }

   public void func_147358_a(C0CPacketInput var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_110430_a(var1.func_149620_c(), var1.func_149616_d(), var1.func_149618_e(), var1.func_149617_f());
   }

   private boolean func_183006_b(C03PacketPlayer var1) {
      return !Doubles.isFinite(var1.func_149464_c()) || !Doubles.isFinite(var1.func_149467_d()) || !Doubles.isFinite(var1.func_149472_e()) || !Floats.isFinite(var1.func_149470_h()) || !Floats.isFinite(var1.func_149462_g());
   }

   public void func_147347_a(C03PacketPlayer var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.func_183006_b(var1)) {
         this.func_147360_c("Invalid move packet received");
      } else {
         WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
         this.field_147366_g = true;
         if (!this.field_147369_b.field_71136_j) {
            double var3 = this.field_147369_b.field_70165_t;
            double var5 = this.field_147369_b.field_70163_u;
            double var7 = this.field_147369_b.field_70161_v;
            double var9 = 0.0D;
            double var11 = var1.func_149464_c() - this.field_147373_o;
            double var13 = var1.func_149467_d() - this.field_147382_p;
            double var15 = var1.func_149472_e() - this.field_147381_q;
            if (var1.func_149466_j()) {
               var9 = var11 * var11 + var13 * var13 + var15 * var15;
               if (!this.field_147380_r && var9 < 0.25D) {
                  this.field_147380_r = true;
               }
            }

            if (this.field_147380_r) {
               this.field_175090_f = this.field_147368_e;
               double var19;
               double var21;
               double var23;
               if (this.field_147369_b.field_70154_o != null) {
                  float var44 = this.field_147369_b.field_70177_z;
                  float var18 = this.field_147369_b.field_70125_A;
                  this.field_147369_b.field_70154_o.func_70043_V();
                  var19 = this.field_147369_b.field_70165_t;
                  var21 = this.field_147369_b.field_70163_u;
                  var23 = this.field_147369_b.field_70161_v;
                  if (var1.func_149463_k()) {
                     var44 = var1.func_149462_g();
                     var18 = var1.func_149470_h();
                  }

                  this.field_147369_b.field_70122_E = var1.func_149465_i();
                  this.field_147369_b.func_71127_g();
                  this.field_147369_b.func_70080_a(var19, var21, var23, var44, var18);
                  if (this.field_147369_b.field_70154_o != null) {
                     this.field_147369_b.field_70154_o.func_70043_V();
                  }

                  this.field_147367_d.func_71203_ab().func_72358_d(this.field_147369_b);
                  if (this.field_147369_b.field_70154_o != null) {
                     if (var9 > 4.0D) {
                        Entity var45 = this.field_147369_b.field_70154_o;
                        this.field_147369_b.field_71135_a.func_147359_a(new S18PacketEntityTeleport(var45));
                        this.func_147364_a(this.field_147369_b.field_70165_t, this.field_147369_b.field_70163_u, this.field_147369_b.field_70161_v, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A);
                     }

                     this.field_147369_b.field_70154_o.field_70160_al = true;
                  }

                  if (this.field_147380_r) {
                     this.field_147373_o = this.field_147369_b.field_70165_t;
                     this.field_147382_p = this.field_147369_b.field_70163_u;
                     this.field_147381_q = this.field_147369_b.field_70161_v;
                  }

                  var2.func_72870_g(this.field_147369_b);
                  return;
               }

               if (this.field_147369_b.func_70608_bn()) {
                  this.field_147369_b.func_71127_g();
                  this.field_147369_b.func_70080_a(this.field_147373_o, this.field_147382_p, this.field_147381_q, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A);
                  var2.func_72870_g(this.field_147369_b);
                  return;
               }

               double var17 = this.field_147369_b.field_70163_u;
               this.field_147373_o = this.field_147369_b.field_70165_t;
               this.field_147382_p = this.field_147369_b.field_70163_u;
               this.field_147381_q = this.field_147369_b.field_70161_v;
               var19 = this.field_147369_b.field_70165_t;
               var21 = this.field_147369_b.field_70163_u;
               var23 = this.field_147369_b.field_70161_v;
               float var25 = this.field_147369_b.field_70177_z;
               float var26 = this.field_147369_b.field_70125_A;
               if (var1.func_149466_j() && var1.func_149467_d() == -999.0D) {
                  var1.func_149469_a(false);
               }

               if (var1.func_149466_j()) {
                  var19 = var1.func_149464_c();
                  var21 = var1.func_149467_d();
                  var23 = var1.func_149472_e();
                  if (Math.abs(var1.func_149464_c()) > 3.0E7D || Math.abs(var1.func_149472_e()) > 3.0E7D) {
                     this.func_147360_c("Illegal position");
                     return;
                  }
               }

               if (var1.func_149463_k()) {
                  var25 = var1.func_149462_g();
                  var26 = var1.func_149470_h();
               }

               this.field_147369_b.func_71127_g();
               this.field_147369_b.func_70080_a(this.field_147373_o, this.field_147382_p, this.field_147381_q, var25, var26);
               if (!this.field_147380_r) {
                  return;
               }

               double var27 = var19 - this.field_147369_b.field_70165_t;
               double var29 = var21 - this.field_147369_b.field_70163_u;
               double var31 = var23 - this.field_147369_b.field_70161_v;
               double var33 = this.field_147369_b.field_70159_w * this.field_147369_b.field_70159_w + this.field_147369_b.field_70181_x * this.field_147369_b.field_70181_x + this.field_147369_b.field_70179_y * this.field_147369_b.field_70179_y;
               double var35 = var27 * var27 + var29 * var29 + var31 * var31;
               if (var35 - var33 > 100.0D && (!this.field_147367_d.func_71264_H() || !this.field_147367_d.func_71214_G().equals(this.field_147369_b.func_70005_c_()))) {
                  field_147370_c.warn(this.field_147369_b.func_70005_c_() + " moved too quickly! " + var27 + "," + var29 + "," + var31 + " (" + var27 + ", " + var29 + ", " + var31 + ")");
                  this.func_147364_a(this.field_147373_o, this.field_147382_p, this.field_147381_q, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A);
                  return;
               }

               float var37 = 0.0625F;
               boolean var38 = var2.func_72945_a(this.field_147369_b, this.field_147369_b.func_174813_aQ().func_72331_e((double)var37, (double)var37, (double)var37)).isEmpty();
               if (this.field_147369_b.field_70122_E && !var1.func_149465_i() && var29 > 0.0D) {
                  this.field_147369_b.func_70664_aZ();
               }

               this.field_147369_b.func_70091_d(var27, var29, var31);
               this.field_147369_b.field_70122_E = var1.func_149465_i();
               double var39 = var29;
               var27 = var19 - this.field_147369_b.field_70165_t;
               var29 = var21 - this.field_147369_b.field_70163_u;
               if (var29 > -0.5D || var29 < 0.5D) {
                  var29 = 0.0D;
               }

               var31 = var23 - this.field_147369_b.field_70161_v;
               var35 = var27 * var27 + var29 * var29 + var31 * var31;
               boolean var41 = false;
               if (var35 > 0.0625D && !this.field_147369_b.func_70608_bn() && !this.field_147369_b.field_71134_c.func_73083_d()) {
                  var41 = true;
                  field_147370_c.warn(this.field_147369_b.func_70005_c_() + " moved wrongly!");
               }

               this.field_147369_b.func_70080_a(var19, var21, var23, var25, var26);
               this.field_147369_b.func_71000_j(this.field_147369_b.field_70165_t - var3, this.field_147369_b.field_70163_u - var5, this.field_147369_b.field_70161_v - var7);
               if (!this.field_147369_b.field_70145_X) {
                  boolean var42 = var2.func_72945_a(this.field_147369_b, this.field_147369_b.func_174813_aQ().func_72331_e((double)var37, (double)var37, (double)var37)).isEmpty();
                  if (var38 && (var41 || !var42) && !this.field_147369_b.func_70608_bn()) {
                     this.func_147364_a(this.field_147373_o, this.field_147382_p, this.field_147381_q, var25, var26);
                     return;
                  }
               }

               AxisAlignedBB var43 = this.field_147369_b.func_174813_aQ().func_72314_b((double)var37, (double)var37, (double)var37).func_72321_a(0.0D, -0.55D, 0.0D);
               if (!this.field_147367_d.func_71231_X() && !this.field_147369_b.field_71075_bZ.field_75101_c && !var2.func_72829_c(var43)) {
                  if (var39 >= -0.03125D) {
                     ++this.field_147365_f;
                     if (this.field_147365_f > 80) {
                        field_147370_c.warn(this.field_147369_b.func_70005_c_() + " was kicked for floating too long!");
                        this.func_147360_c("Flying is not enabled on this server");
                        return;
                     }
                  }
               } else {
                  this.field_147365_f = 0;
               }

               this.field_147369_b.field_70122_E = var1.func_149465_i();
               this.field_147367_d.func_71203_ab().func_72358_d(this.field_147369_b);
               this.field_147369_b.func_71122_b(this.field_147369_b.field_70163_u - var17, var1.func_149465_i());
            } else if (this.field_147368_e - this.field_175090_f > 20) {
               this.func_147364_a(this.field_147373_o, this.field_147382_p, this.field_147381_q, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A);
            }

         }
      }
   }

   public void func_147364_a(double var1, double var3, double var5, float var7, float var8) {
      this.func_175089_a(var1, var3, var5, var7, var8, Collections.emptySet());
   }

   public void func_175089_a(double var1, double var3, double var5, float var7, float var8, Set<S08PacketPlayerPosLook.EnumFlags> var9) {
      this.field_147380_r = false;
      this.field_147373_o = var1;
      this.field_147382_p = var3;
      this.field_147381_q = var5;
      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.X)) {
         this.field_147373_o += this.field_147369_b.field_70165_t;
      }

      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
         this.field_147382_p += this.field_147369_b.field_70163_u;
      }

      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
         this.field_147381_q += this.field_147369_b.field_70161_v;
      }

      float var10 = var7;
      float var11 = var8;
      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
         var10 = var7 + this.field_147369_b.field_70177_z;
      }

      if (var9.contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
         var11 = var8 + this.field_147369_b.field_70125_A;
      }

      this.field_147369_b.func_70080_a(this.field_147373_o, this.field_147382_p, this.field_147381_q, var10, var11);
      this.field_147369_b.field_71135_a.func_147359_a(new S08PacketPlayerPosLook(var1, var3, var5, var7, var8, var9));
   }

   public void func_147345_a(C07PacketPlayerDigging var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      BlockPos var3 = var1.func_179715_a();
      this.field_147369_b.func_143004_u();
      switch(var1.func_180762_c()) {
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
         this.field_147369_b.func_71034_by();
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
            if (var1.func_180762_c() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
               if (!this.field_147367_d.func_175579_a(var2, var3, this.field_147369_b) && var2.func_175723_af().func_177746_a(var3)) {
                  this.field_147369_b.field_71134_c.func_180784_a(var3, var1.func_179714_b());
               } else {
                  this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var2, var3));
               }
            } else {
               if (var1.func_180762_c() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                  this.field_147369_b.field_71134_c.func_180785_a(var3);
               } else if (var1.func_180762_c() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                  this.field_147369_b.field_71134_c.func_180238_e();
               }

               if (var2.func_180495_p(var3).func_177230_c().func_149688_o() != Material.field_151579_a) {
                  this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var2, var3));
               }
            }

            return;
         }
      default:
         throw new IllegalArgumentException("Invalid player action");
      }
   }

   public void func_147346_a(C08PacketPlayerBlockPlacement var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      ItemStack var3 = this.field_147369_b.field_71071_by.func_70448_g();
      boolean var4 = false;
      BlockPos var5 = var1.func_179724_a();
      EnumFacing var6 = EnumFacing.func_82600_a(var1.func_149568_f());
      this.field_147369_b.func_143004_u();
      if (var1.func_149568_f() == 255) {
         if (var3 == null) {
            return;
         }

         this.field_147369_b.field_71134_c.func_73085_a(this.field_147369_b, var2, var3);
      } else if (var5.func_177956_o() >= this.field_147367_d.func_71207_Z() - 1 && (var6 == EnumFacing.UP || var5.func_177956_o() >= this.field_147367_d.func_71207_Z())) {
         ChatComponentTranslation var7 = new ChatComponentTranslation("build.tooHigh", new Object[]{this.field_147367_d.func_71207_Z()});
         var7.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         this.field_147369_b.field_71135_a.func_147359_a(new S02PacketChat(var7));
         var4 = true;
      } else {
         if (this.field_147380_r && this.field_147369_b.func_70092_e((double)var5.func_177958_n() + 0.5D, (double)var5.func_177956_o() + 0.5D, (double)var5.func_177952_p() + 0.5D) < 64.0D && !this.field_147367_d.func_175579_a(var2, var5, this.field_147369_b) && var2.func_175723_af().func_177746_a(var5)) {
            this.field_147369_b.field_71134_c.func_180236_a(this.field_147369_b, var2, var3, var5, var6, var1.func_149573_h(), var1.func_149569_i(), var1.func_149575_j());
         }

         var4 = true;
      }

      if (var4) {
         this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var2, var5));
         this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var2, var5.func_177972_a(var6)));
      }

      var3 = this.field_147369_b.field_71071_by.func_70448_g();
      if (var3 != null && var3.field_77994_a == 0) {
         this.field_147369_b.field_71071_by.field_70462_a[this.field_147369_b.field_71071_by.field_70461_c] = null;
         var3 = null;
      }

      if (var3 == null || var3.func_77988_m() == 0) {
         this.field_147369_b.field_71137_h = true;
         this.field_147369_b.field_71071_by.field_70462_a[this.field_147369_b.field_71071_by.field_70461_c] = ItemStack.func_77944_b(this.field_147369_b.field_71071_by.field_70462_a[this.field_147369_b.field_71071_by.field_70461_c]);
         Slot var8 = this.field_147369_b.field_71070_bA.func_75147_a(this.field_147369_b.field_71071_by, this.field_147369_b.field_71071_by.field_70461_c);
         this.field_147369_b.field_71070_bA.func_75142_b();
         this.field_147369_b.field_71137_h = false;
         if (!ItemStack.func_77989_b(this.field_147369_b.field_71071_by.func_70448_g(), var1.func_149574_g())) {
            this.func_147359_a(new S2FPacketSetSlot(this.field_147369_b.field_71070_bA.field_75152_c, var8.field_75222_d, this.field_147369_b.field_71071_by.func_70448_g()));
         }
      }

   }

   public void func_175088_a(C18PacketSpectate var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.func_175149_v()) {
         Entity var2 = null;
         WorldServer[] var3 = this.field_147367_d.field_71305_c;
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            WorldServer var6 = var3[var5];
            if (var6 != null) {
               var2 = var1.func_179727_a(var6);
               if (var2 != null) {
                  break;
               }
            }
         }

         if (var2 != null) {
            this.field_147369_b.func_175399_e(this.field_147369_b);
            this.field_147369_b.func_70078_a((Entity)null);
            if (var2.field_70170_p != this.field_147369_b.field_70170_p) {
               WorldServer var7 = this.field_147369_b.func_71121_q();
               WorldServer var8 = (WorldServer)var2.field_70170_p;
               this.field_147369_b.field_71093_bK = var2.field_71093_bK;
               this.func_147359_a(new S07PacketRespawn(this.field_147369_b.field_71093_bK, var7.func_175659_aa(), var7.func_72912_H().func_76067_t(), this.field_147369_b.field_71134_c.func_73081_b()));
               var7.func_72973_f(this.field_147369_b);
               this.field_147369_b.field_70128_L = false;
               this.field_147369_b.func_70012_b(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var2.field_70177_z, var2.field_70125_A);
               if (this.field_147369_b.func_70089_S()) {
                  var7.func_72866_a(this.field_147369_b, false);
                  var8.func_72838_d(this.field_147369_b);
                  var8.func_72866_a(this.field_147369_b, false);
               }

               this.field_147369_b.func_70029_a(var8);
               this.field_147367_d.func_71203_ab().func_72375_a(this.field_147369_b, var7);
               this.field_147369_b.func_70634_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v);
               this.field_147369_b.field_71134_c.func_73080_a(var8);
               this.field_147367_d.func_71203_ab().func_72354_b(this.field_147369_b, var8);
               this.field_147367_d.func_71203_ab().func_72385_f(this.field_147369_b);
            } else {
               this.field_147369_b.func_70634_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v);
            }
         }
      }

   }

   public void func_175086_a(C19PacketResourcePackStatus var1) {
   }

   public void func_147231_a(IChatComponent var1) {
      field_147370_c.info(this.field_147369_b.func_70005_c_() + " lost connection: " + var1);
      this.field_147367_d.func_147132_au();
      ChatComponentTranslation var2 = new ChatComponentTranslation("multiplayer.player.left", new Object[]{this.field_147369_b.func_145748_c_()});
      var2.func_150256_b().func_150238_a(EnumChatFormatting.YELLOW);
      this.field_147367_d.func_71203_ab().func_148539_a(var2);
      this.field_147369_b.func_71123_m();
      this.field_147367_d.func_71203_ab().func_72367_e(this.field_147369_b);
      if (this.field_147367_d.func_71264_H() && this.field_147369_b.func_70005_c_().equals(this.field_147367_d.func_71214_G())) {
         field_147370_c.info("Stopping singleplayer server as player logged out");
         this.field_147367_d.func_71263_m();
      }

   }

   public void func_147359_a(final Packet var1) {
      if (var1 instanceof S02PacketChat) {
         S02PacketChat var2 = (S02PacketChat)var1;
         EntityPlayer.EnumChatVisibility var3 = this.field_147369_b.func_147096_v();
         if (var3 == EntityPlayer.EnumChatVisibility.HIDDEN) {
            return;
         }

         if (var3 == EntityPlayer.EnumChatVisibility.SYSTEM && !var2.func_148916_d()) {
            return;
         }
      }

      try {
         this.field_147371_a.func_179290_a(var1);
      } catch (Throwable var5) {
         CrashReport var6 = CrashReport.func_85055_a(var5, "Sending packet");
         CrashReportCategory var4 = var6.func_85058_a("Packet being sent");
         var4.func_71500_a("Packet class", new Callable<String>() {
            public String call() throws Exception {
               return var1.getClass().getCanonicalName();
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         throw new ReportedException(var6);
      }
   }

   public void func_147355_a(C09PacketHeldItemChange var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (var1.func_149614_c() >= 0 && var1.func_149614_c() < InventoryPlayer.func_70451_h()) {
         this.field_147369_b.field_71071_by.field_70461_c = var1.func_149614_c();
         this.field_147369_b.func_143004_u();
      } else {
         field_147370_c.warn(this.field_147369_b.func_70005_c_() + " tried to set an invalid carried item");
      }
   }

   public void func_147354_a(C01PacketChatMessage var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.func_147096_v() == EntityPlayer.EnumChatVisibility.HIDDEN) {
         ChatComponentTranslation var4 = new ChatComponentTranslation("chat.cannotSend", new Object[0]);
         var4.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         this.func_147359_a(new S02PacketChat(var4));
      } else {
         this.field_147369_b.func_143004_u();
         String var2 = var1.func_149439_c();
         var2 = StringUtils.normalizeSpace(var2);

         for(int var3 = 0; var3 < var2.length(); ++var3) {
            if (!ChatAllowedCharacters.func_71566_a(var2.charAt(var3))) {
               this.func_147360_c("Illegal characters in chat");
               return;
            }
         }

         if (var2.startsWith("/")) {
            this.func_147361_d(var2);
         } else {
            ChatComponentTranslation var5 = new ChatComponentTranslation("chat.type.text", new Object[]{this.field_147369_b.func_145748_c_(), var2});
            this.field_147367_d.func_71203_ab().func_148544_a(var5, false);
         }

         this.field_147374_l += 20;
         if (this.field_147374_l > 200 && !this.field_147367_d.func_71203_ab().func_152596_g(this.field_147369_b.func_146103_bH())) {
            this.func_147360_c("disconnect.spam");
         }

      }
   }

   private void func_147361_d(String var1) {
      this.field_147367_d.func_71187_D().func_71556_a(this.field_147369_b, var1);
   }

   public void func_175087_a(C0APacketAnimation var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      this.field_147369_b.func_71038_i();
   }

   public void func_147357_a(C0BPacketEntityAction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
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
         this.field_147369_b.func_70999_a(false, true, true);
         this.field_147380_r = false;
         break;
      case RIDING_JUMP:
         if (this.field_147369_b.field_70154_o instanceof EntityHorse) {
            ((EntityHorse)this.field_147369_b.field_70154_o).func_110206_u(var1.func_149512_e());
         }
         break;
      case OPEN_INVENTORY:
         if (this.field_147369_b.field_70154_o instanceof EntityHorse) {
            ((EntityHorse)this.field_147369_b.field_70154_o).func_110199_f(this.field_147369_b);
         }
         break;
      default:
         throw new IllegalArgumentException("Invalid client command!");
      }

   }

   public void func_147340_a(C02PacketUseEntity var1) {
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
            if (var1.func_149565_c() == C02PacketUseEntity.Action.INTERACT) {
               this.field_147369_b.func_70998_m(var3);
            } else if (var1.func_149565_c() == C02PacketUseEntity.Action.INTERACT_AT) {
               var3.func_174825_a(this.field_147369_b, var1.func_179712_b());
            } else if (var1.func_149565_c() == C02PacketUseEntity.Action.ATTACK) {
               if (var3 instanceof EntityItem || var3 instanceof EntityXPOrb || var3 instanceof EntityArrow || var3 == this.field_147369_b) {
                  this.func_147360_c("Attempting to attack an invalid entity");
                  this.field_147367_d.func_71236_h("Player " + this.field_147369_b.func_70005_c_() + " tried to attack an invalid entity");
                  return;
               }

               this.field_147369_b.func_71059_n(var3);
            }
         }
      }

   }

   public void func_147342_a(C16PacketClientStatus var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      C16PacketClientStatus.EnumState var2 = var1.func_149435_c();
      switch(var2) {
      case PERFORM_RESPAWN:
         if (this.field_147369_b.field_71136_j) {
            this.field_147369_b = this.field_147367_d.func_71203_ab().func_72368_a(this.field_147369_b, 0, true);
         } else if (this.field_147369_b.func_71121_q().func_72912_H().func_76093_s()) {
            if (this.field_147367_d.func_71264_H() && this.field_147369_b.func_70005_c_().equals(this.field_147367_d.func_71214_G())) {
               this.field_147369_b.field_71135_a.func_147360_c("You have died. Game over, man, it's game over!");
               this.field_147367_d.func_71272_O();
            } else {
               UserListBansEntry var3 = new UserListBansEntry(this.field_147369_b.func_146103_bH(), (Date)null, "(You just lost the game)", (Date)null, "Death in Hardcore");
               this.field_147367_d.func_71203_ab().func_152608_h().func_152687_a(var3);
               this.field_147369_b.field_71135_a.func_147360_c("You have died. Game over, man, it's game over!");
            }
         } else {
            if (this.field_147369_b.func_110143_aJ() > 0.0F) {
               return;
            }

            this.field_147369_b = this.field_147367_d.func_71203_ab().func_72368_a(this.field_147369_b, 0, false);
         }
         break;
      case REQUEST_STATS:
         this.field_147369_b.func_147099_x().func_150876_a(this.field_147369_b);
         break;
      case OPEN_INVENTORY_ACHIEVEMENT:
         this.field_147369_b.func_71029_a(AchievementList.field_76004_f);
      }

   }

   public void func_147356_a(C0DPacketCloseWindow var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_71128_l();
   }

   public void func_147351_a(C0EPacketClickWindow var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      if (this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149548_c() && this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b)) {
         if (this.field_147369_b.func_175149_v()) {
            ArrayList var2 = Lists.newArrayList();

            for(int var3 = 0; var3 < this.field_147369_b.field_71070_bA.field_75151_b.size(); ++var3) {
               var2.add(((Slot)this.field_147369_b.field_71070_bA.field_75151_b.get(var3)).func_75211_c());
            }

            this.field_147369_b.func_71110_a(this.field_147369_b.field_71070_bA, var2);
         } else {
            ItemStack var5 = this.field_147369_b.field_71070_bA.func_75144_a(var1.func_149544_d(), var1.func_149543_e(), var1.func_149542_h(), this.field_147369_b);
            if (ItemStack.func_77989_b(var1.func_149546_g(), var5)) {
               this.field_147369_b.field_71135_a.func_147359_a(new S32PacketConfirmTransaction(var1.func_149548_c(), var1.func_149547_f(), true));
               this.field_147369_b.field_71137_h = true;
               this.field_147369_b.field_71070_bA.func_75142_b();
               this.field_147369_b.func_71113_k();
               this.field_147369_b.field_71137_h = false;
            } else {
               this.field_147372_n.func_76038_a(this.field_147369_b.field_71070_bA.field_75152_c, var1.func_149547_f());
               this.field_147369_b.field_71135_a.func_147359_a(new S32PacketConfirmTransaction(var1.func_149548_c(), var1.func_149547_f(), false));
               this.field_147369_b.field_71070_bA.func_75128_a(this.field_147369_b, false);
               ArrayList var6 = Lists.newArrayList();

               for(int var4 = 0; var4 < this.field_147369_b.field_71070_bA.field_75151_b.size(); ++var4) {
                  var6.add(((Slot)this.field_147369_b.field_71070_bA.field_75151_b.get(var4)).func_75211_c());
               }

               this.field_147369_b.func_71110_a(this.field_147369_b.field_71070_bA, var6);
            }
         }
      }

   }

   public void func_147338_a(C11PacketEnchantItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      if (this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149539_c() && this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b) && !this.field_147369_b.func_175149_v()) {
         this.field_147369_b.field_71070_bA.func_75140_a(this.field_147369_b, var1.func_149537_d());
         this.field_147369_b.field_71070_bA.func_75142_b();
      }

   }

   public void func_147344_a(C10PacketCreativeInventoryAction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      if (this.field_147369_b.field_71134_c.func_73083_d()) {
         boolean var2 = var1.func_149627_c() < 0;
         ItemStack var3 = var1.func_149625_d();
         if (var3 != null && var3.func_77942_o() && var3.func_77978_p().func_150297_b("BlockEntityTag", 10)) {
            NBTTagCompound var4 = var3.func_77978_p().func_74775_l("BlockEntityTag");
            if (var4.func_74764_b("x") && var4.func_74764_b("y") && var4.func_74764_b("z")) {
               BlockPos var5 = new BlockPos(var4.func_74762_e("x"), var4.func_74762_e("y"), var4.func_74762_e("z"));
               TileEntity var6 = this.field_147369_b.field_70170_p.func_175625_s(var5);
               if (var6 != null) {
                  NBTTagCompound var7 = new NBTTagCompound();
                  var6.func_145841_b(var7);
                  var7.func_82580_o("x");
                  var7.func_82580_o("y");
                  var7.func_82580_o("z");
                  var3.func_77983_a("BlockEntityTag", var7);
               }
            }
         }

         boolean var8 = var1.func_149627_c() >= 1 && var1.func_149627_c() < 36 + InventoryPlayer.func_70451_h();
         boolean var9 = var3 == null || var3.func_77973_b() != null;
         boolean var10 = var3 == null || var3.func_77960_j() >= 0 && var3.field_77994_a <= 64 && var3.field_77994_a > 0;
         if (var8 && var9 && var10) {
            if (var3 == null) {
               this.field_147369_b.field_71069_bz.func_75141_a(var1.func_149627_c(), (ItemStack)null);
            } else {
               this.field_147369_b.field_71069_bz.func_75141_a(var1.func_149627_c(), var3);
            }

            this.field_147369_b.field_71069_bz.func_75128_a(this.field_147369_b, true);
         } else if (var2 && var9 && var10 && this.field_147375_m < 200) {
            this.field_147375_m += 20;
            EntityItem var11 = this.field_147369_b.func_71019_a(var3, true);
            if (var11 != null) {
               var11.func_70288_d();
            }
         }
      }

   }

   public void func_147339_a(C0FPacketConfirmTransaction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      Short var2 = (Short)this.field_147372_n.func_76041_a(this.field_147369_b.field_71070_bA.field_75152_c);
      if (var2 != null && var1.func_149533_d() == var2 && this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149532_c() && !this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b) && !this.field_147369_b.func_175149_v()) {
         this.field_147369_b.field_71070_bA.func_75128_a(this.field_147369_b, true);
      }

   }

   public void func_147343_a(C12PacketUpdateSign var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_143004_u();
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      BlockPos var3 = var1.func_179722_a();
      if (var2.func_175667_e(var3)) {
         TileEntity var4 = var2.func_175625_s(var3);
         if (!(var4 instanceof TileEntitySign)) {
            return;
         }

         TileEntitySign var5 = (TileEntitySign)var4;
         if (!var5.func_145914_a() || var5.func_145911_b() != this.field_147369_b) {
            this.field_147367_d.func_71236_h("Player " + this.field_147369_b.func_70005_c_() + " just tried to change non-editable sign");
            return;
         }

         IChatComponent[] var6 = var1.func_180768_b();

         for(int var7 = 0; var7 < var6.length; ++var7) {
            var5.field_145915_a[var7] = new ChatComponentText(EnumChatFormatting.func_110646_a(var6[var7].func_150260_c()));
         }

         var5.func_70296_d();
         var2.func_175689_h(var3);
      }

   }

   public void func_147353_a(C00PacketKeepAlive var1) {
      if (var1.func_149460_c() == this.field_147378_h) {
         int var2 = (int)(this.func_147363_d() - this.field_147379_i);
         this.field_147369_b.field_71138_i = (this.field_147369_b.field_71138_i * 3 + var2) / 4;
      }

   }

   private long func_147363_d() {
      return System.nanoTime() / 1000000L;
   }

   public void func_147348_a(C13PacketPlayerAbilities var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.field_71075_bZ.field_75100_b = var1.func_149488_d() && this.field_147369_b.field_71075_bZ.field_75101_c;
   }

   public void func_147341_a(C14PacketTabComplete var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.field_147367_d.func_180506_a(this.field_147369_b, var1.func_149419_c(), var1.func_179709_b()).iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var2.add(var4);
      }

      this.field_147369_b.field_71135_a.func_147359_a(new S3APacketTabComplete((String[])var2.toArray(new String[var2.size()])));
   }

   public void func_147352_a(C15PacketClientSettings var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      this.field_147369_b.func_147100_a(var1);
   }

   public void func_147349_a(C17PacketCustomPayload var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147369_b.func_71121_q());
      PacketBuffer var2;
      ItemStack var3;
      ItemStack var4;
      if ("MC|BEdit".equals(var1.func_149559_c())) {
         var2 = new PacketBuffer(Unpooled.wrappedBuffer(var1.func_180760_b()));

         try {
            var3 = var2.func_150791_c();
            if (var3 == null) {
               return;
            }

            if (!ItemWritableBook.func_150930_a(var3.func_77978_p())) {
               throw new IOException("Invalid book tag!");
            }

            var4 = this.field_147369_b.field_71071_by.func_70448_g();
            if (var4 == null) {
               return;
            }

            if (var3.func_77973_b() == Items.field_151099_bA && var3.func_77973_b() == var4.func_77973_b()) {
               var4.func_77983_a("pages", var3.func_77978_p().func_150295_c("pages", 8));
            }
         } catch (Exception var38) {
            field_147370_c.error("Couldn't handle book info", var38);
         } finally {
            var2.release();
         }
      } else if ("MC|BSign".equals(var1.func_149559_c())) {
         var2 = new PacketBuffer(Unpooled.wrappedBuffer(var1.func_180760_b()));

         try {
            var3 = var2.func_150791_c();
            if (var3 == null) {
               return;
            }

            if (!ItemEditableBook.func_77828_a(var3.func_77978_p())) {
               throw new IOException("Invalid book tag!");
            }

            var4 = this.field_147369_b.field_71071_by.func_70448_g();
            if (var4 == null) {
               return;
            }

            if (var3.func_77973_b() == Items.field_151164_bB && var4.func_77973_b() == Items.field_151099_bA) {
               var4.func_77983_a("author", new NBTTagString(this.field_147369_b.func_70005_c_()));
               var4.func_77983_a("title", new NBTTagString(var3.func_77978_p().func_74779_i("title")));
               var4.func_77983_a("pages", var3.func_77978_p().func_150295_c("pages", 8));
               var4.func_150996_a(Items.field_151164_bB);
            }
         } catch (Exception var36) {
            field_147370_c.error("Couldn't sign book", var36);
         } finally {
            var2.release();
         }
      } else if ("MC|TrSel".equals(var1.func_149559_c())) {
         try {
            int var40 = var1.func_180760_b().readInt();
            Container var41 = this.field_147369_b.field_71070_bA;
            if (var41 instanceof ContainerMerchant) {
               ((ContainerMerchant)var41).func_75175_c(var40);
            }
         } catch (Exception var35) {
            field_147370_c.error("Couldn't select trade", var35);
         }
      } else if ("MC|AdvCdm".equals(var1.func_149559_c())) {
         if (!this.field_147367_d.func_82356_Z()) {
            this.field_147369_b.func_145747_a(new ChatComponentTranslation("advMode.notEnabled", new Object[0]));
         } else if (this.field_147369_b.func_70003_b(2, "") && this.field_147369_b.field_71075_bZ.field_75098_d) {
            var2 = var1.func_180760_b();

            try {
               byte var43 = var2.readByte();
               CommandBlockLogic var46 = null;
               if (var43 == 0) {
                  TileEntity var5 = this.field_147369_b.field_70170_p.func_175625_s(new BlockPos(var2.readInt(), var2.readInt(), var2.readInt()));
                  if (var5 instanceof TileEntityCommandBlock) {
                     var46 = ((TileEntityCommandBlock)var5).func_145993_a();
                  }
               } else if (var43 == 1) {
                  Entity var47 = this.field_147369_b.field_70170_p.func_73045_a(var2.readInt());
                  if (var47 instanceof EntityMinecartCommandBlock) {
                     var46 = ((EntityMinecartCommandBlock)var47).func_145822_e();
                  }
               }

               String var48 = var2.func_150789_c(var2.readableBytes());
               boolean var6 = var2.readBoolean();
               if (var46 != null) {
                  var46.func_145752_a(var48);
                  var46.func_175573_a(var6);
                  if (!var6) {
                     var46.func_145750_b((IChatComponent)null);
                  }

                  var46.func_145756_e();
                  this.field_147369_b.func_145747_a(new ChatComponentTranslation("advMode.setCommand.success", new Object[]{var48}));
               }
            } catch (Exception var33) {
               field_147370_c.error("Couldn't set command block", var33);
            } finally {
               var2.release();
            }
         } else {
            this.field_147369_b.func_145747_a(new ChatComponentTranslation("advMode.notAllowed", new Object[0]));
         }
      } else if ("MC|Beacon".equals(var1.func_149559_c())) {
         if (this.field_147369_b.field_71070_bA instanceof ContainerBeacon) {
            try {
               var2 = var1.func_180760_b();
               int var44 = var2.readInt();
               int var49 = var2.readInt();
               ContainerBeacon var50 = (ContainerBeacon)this.field_147369_b.field_71070_bA;
               Slot var51 = var50.func_75139_a(0);
               if (var51.func_75216_d()) {
                  var51.func_75209_a(1);
                  IInventory var7 = var50.func_180611_e();
                  var7.func_174885_b(1, var44);
                  var7.func_174885_b(2, var49);
                  var7.func_70296_d();
               }
            } catch (Exception var32) {
               field_147370_c.error("Couldn't set beacon", var32);
            }
         }
      } else if ("MC|ItemName".equals(var1.func_149559_c()) && this.field_147369_b.field_71070_bA instanceof ContainerRepair) {
         ContainerRepair var42 = (ContainerRepair)this.field_147369_b.field_71070_bA;
         if (var1.func_180760_b() != null && var1.func_180760_b().readableBytes() >= 1) {
            String var45 = ChatAllowedCharacters.func_71565_a(var1.func_180760_b().func_150789_c(32767));
            if (var45.length() <= 30) {
               var42.func_82850_a(var45);
            }
         } else {
            var42.func_82850_a("");
         }
      }

   }
}
