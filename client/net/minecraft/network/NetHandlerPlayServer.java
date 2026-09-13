package net.minecraft.network;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer$EnumChatVisibility;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity$Action;
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
import net.minecraft.network.play.client.C16PacketClientStatus$EnumState;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer {
   private static final Logger field_147370_c = LogManager.getLogger();
   public final NetworkManager field_147371_a;
   private final MinecraftServer field_147367_d;
   public EntityPlayerMP field_147369_b;
   private int field_147368_e;
   private int field_147365_f;
   private boolean field_147366_g;
   private int field_147378_h;
   private long field_147379_i;
   private static Random field_147376_j = new Random();
   private long field_147377_k;
   private int field_147374_l;
   private int field_147375_m;
   private IntHashMap field_147372_n = new IntHashMap();
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

   @Override
   public void func_147233_a() {
      this.field_147366_g = false;
      ++this.field_147368_e;
      this.field_147367_d.field_71304_b.func_76320_a("keepAlive");
      if ((long)this.field_147368_e - this.field_147377_k > 40L) {
         this.field_147377_k = (long)this.field_147368_e;
         this.field_147379_i = this.func_147363_d();
         this.field_147378_h = (int)this.field_147379_i;
         this.func_147359_a(new S00PacketKeepAlive(this.field_147378_h));
      }

      if (this.field_147374_l > 0) {
         --this.field_147374_l;
      }

      if (this.field_147375_m > 0) {
         --this.field_147375_m;
      }

      if (this.field_147369_b.func_154331_x() > 0L
         && this.field_147367_d.func_143007_ar() > 0
         && MinecraftServer.func_130071_aq() - this.field_147369_b.func_154331_x() > (long)(this.field_147367_d.func_143007_ar() * 1000 * 60)) {
         this.func_147360_c("You have been idle for too long!");
      }
   }

   public NetworkManager func_147362_b() {
      return this.field_147371_a;
   }

   public void func_147360_c(String var1) {
      ChatComponentText var2 = new ChatComponentText(var1);
      this.field_147371_a.func_150725_a(new S40PacketDisconnect(var2), new NetHandlerPlayServer$1(this, var2));
      this.field_147371_a.func_150721_g();
   }

   @Override
   public void func_147358_a(C0CPacketInput var1) {
      this.field_147369_b.func_110430_a(var1.func_149620_c(), var1.func_149616_d(), var1.func_149618_e(), var1.func_149617_f());
   }

   @Override
   public void func_147347_a(C03PacketPlayer var1) {
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      this.field_147366_g = true;
      if (!this.field_147369_b.field_71136_j) {
         if (!this.field_147380_r) {
            double var3 = var1.func_149467_d() - this.field_147382_p;
            if (var1.func_149464_c() == this.field_147373_o && var3 * var3 < 0.01 && var1.func_149472_e() == this.field_147381_q) {
               this.field_147380_r = true;
            }
         }

         if (this.field_147380_r) {
            if (this.field_147369_b.field_70154_o != null) {
               float var35 = this.field_147369_b.field_70177_z;
               float var4 = this.field_147369_b.field_70125_A;
               this.field_147369_b.field_70154_o.func_70043_V();
               double var36 = this.field_147369_b.field_70165_t;
               double var37 = this.field_147369_b.field_70163_u;
               double var38 = this.field_147369_b.field_70161_v;
               if (var1.func_149463_k()) {
                  var35 = var1.func_149462_g();
                  var4 = var1.func_149470_h();
               }

               this.field_147369_b.field_70122_E = var1.func_149465_i();
               this.field_147369_b.func_71127_g();
               this.field_147369_b.field_70139_V = 0.0F;
               this.field_147369_b.func_70080_a(var36, var37, var38, var35, var4);
               if (this.field_147369_b.field_70154_o != null) {
                  this.field_147369_b.field_70154_o.func_70043_V();
               }

               this.field_147367_d.func_71203_ab().func_72358_d(this.field_147369_b);
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
               this.field_147369_b
                  .func_70080_a(
                     this.field_147373_o, this.field_147382_p, this.field_147381_q, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A
                  );
               var2.func_72870_g(this.field_147369_b);
               return;
            }

            double var34 = this.field_147369_b.field_70163_u;
            this.field_147373_o = this.field_147369_b.field_70165_t;
            this.field_147382_p = this.field_147369_b.field_70163_u;
            this.field_147381_q = this.field_147369_b.field_70161_v;
            double var5 = this.field_147369_b.field_70165_t;
            double var7 = this.field_147369_b.field_70163_u;
            double var9 = this.field_147369_b.field_70161_v;
            float var11 = this.field_147369_b.field_70177_z;
            float var12 = this.field_147369_b.field_70125_A;
            if (var1.func_149466_j() && var1.func_149467_d() == -999.0 && var1.func_149471_f() == -999.0) {
               var1.func_149469_a(false);
            }

            if (var1.func_149466_j()) {
               var5 = var1.func_149464_c();
               var7 = var1.func_149467_d();
               var9 = var1.func_149472_e();
               double var13 = var1.func_149471_f() - var1.func_149467_d();
               if (!this.field_147369_b.func_70608_bn() && (var13 > 1.65 || var13 < 0.1)) {
                  this.func_147360_c("Illegal stance");
                  field_147370_c.warn(this.field_147369_b.func_70005_c_() + " had an illegal stance: " + var13);
                  return;
               }

               if (Math.abs(var1.func_149464_c()) > 3.2E7 || Math.abs(var1.func_149472_e()) > 3.2E7) {
                  this.func_147360_c("Illegal position");
                  return;
               }
            }

            if (var1.func_149463_k()) {
               var11 = var1.func_149462_g();
               var12 = var1.func_149470_h();
            }

            this.field_147369_b.func_71127_g();
            this.field_147369_b.field_70139_V = 0.0F;
            this.field_147369_b.func_70080_a(this.field_147373_o, this.field_147382_p, this.field_147381_q, var11, var12);
            if (!this.field_147380_r) {
               return;
            }

            double var39 = var5 - this.field_147369_b.field_70165_t;
            double var15 = var7 - this.field_147369_b.field_70163_u;
            double var17 = var9 - this.field_147369_b.field_70161_v;
            double var19 = Math.min(Math.abs(var39), Math.abs(this.field_147369_b.field_70159_w));
            double var21 = Math.min(Math.abs(var15), Math.abs(this.field_147369_b.field_70181_x));
            double var23 = Math.min(Math.abs(var17), Math.abs(this.field_147369_b.field_70179_y));
            double var25 = var19 * var19 + var21 * var21 + var23 * var23;
            if (var25 > 100.0 && (!this.field_147367_d.func_71264_H() || !this.field_147367_d.func_71214_G().equals(this.field_147369_b.func_70005_c_()))) {
               field_147370_c.warn(
                  this.field_147369_b.func_70005_c_()
                     + " moved too quickly! "
                     + var39
                     + ","
                     + var15
                     + ","
                     + var17
                     + " ("
                     + var19
                     + ", "
                     + var21
                     + ", "
                     + var23
                     + ")"
               );
               this.func_147364_a(
                  this.field_147373_o, this.field_147382_p, this.field_147381_q, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A
               );
               return;
            }

            float var27 = 0.0625F;
            boolean var28 = var2.func_72945_a(
                  this.field_147369_b, this.field_147369_b.field_70121_D.func_72329_c().func_72331_e((double)var27, (double)var27, (double)var27)
               )
               .isEmpty();
            if (this.field_147369_b.field_70122_E && !var1.func_149465_i() && var15 > 0.0) {
               this.field_147369_b.func_70664_aZ();
            }

            this.field_147369_b.func_70091_d(var39, var15, var17);
            this.field_147369_b.field_70122_E = var1.func_149465_i();
            this.field_147369_b.func_71000_j(var39, var15, var17);
            var39 = var5 - this.field_147369_b.field_70165_t;
            var15 = var7 - this.field_147369_b.field_70163_u;
            if (var15 > -0.5 || var15 < 0.5) {
               var15 = 0.0;
            }

            var17 = var9 - this.field_147369_b.field_70161_v;
            var25 = var39 * var39 + var15 * var15 + var17 * var17;
            boolean var31 = false;
            if (var25 > 0.0625 && !this.field_147369_b.func_70608_bn() && !this.field_147369_b.field_71134_c.func_73083_d()) {
               var31 = true;
               field_147370_c.warn(this.field_147369_b.func_70005_c_() + " moved wrongly!");
            }

            this.field_147369_b.func_70080_a(var5, var7, var9, var11, var12);
            boolean var32 = var2.func_72945_a(
                  this.field_147369_b, this.field_147369_b.field_70121_D.func_72329_c().func_72331_e((double)var27, (double)var27, (double)var27)
               )
               .isEmpty();
            if (var28 && (var31 || !var32) && !this.field_147369_b.func_70608_bn()) {
               this.func_147364_a(this.field_147373_o, this.field_147382_p, this.field_147381_q, var11, var12);
               return;
            }

            AxisAlignedBB var33 = this.field_147369_b
               .field_70121_D
               .func_72329_c()
               .func_72314_b((double)var27, (double)var27, (double)var27)
               .func_72321_a(0.0, -0.55, 0.0);
            if (this.field_147367_d.func_71231_X() || this.field_147369_b.field_71134_c.func_73083_d() || var2.func_72829_c(var33)) {
               this.field_147365_f = 0;
            } else if (var15 >= -0.03125) {
               ++this.field_147365_f;
               if (this.field_147365_f > 80) {
                  field_147370_c.warn(this.field_147369_b.func_70005_c_() + " was kicked for floating too long!");
                  this.func_147360_c("Flying is not enabled on this server");
                  return;
               }
            }

            this.field_147369_b.field_70122_E = var1.func_149465_i();
            this.field_147367_d.func_71203_ab().func_72358_d(this.field_147369_b);
            this.field_147369_b.func_71122_b(this.field_147369_b.field_70163_u - var34, var1.func_149465_i());
         } else if (this.field_147368_e % 20 == 0) {
            this.func_147364_a(
               this.field_147373_o, this.field_147382_p, this.field_147381_q, this.field_147369_b.field_70177_z, this.field_147369_b.field_70125_A
            );
         }
      }
   }

   public void func_147364_a(double var1, double var3, double var5, float var7, float var8) {
      this.field_147380_r = false;
      this.field_147373_o = var1;
      this.field_147382_p = var3;
      this.field_147381_q = var5;
      this.field_147369_b.func_70080_a(var1, var3, var5, var7, var8);
      this.field_147369_b.field_71135_a.func_147359_a(new S08PacketPlayerPosLook(var1, var3 + 1.6200000047683716, var5, var7, var8, false));
   }

   @Override
   public void func_147345_a(C07PacketPlayerDigging var1) {
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      this.field_147369_b.func_143004_u();
      if (var1.func_149506_g() == 4) {
         this.field_147369_b.func_71040_bB(false);
      } else if (var1.func_149506_g() == 3) {
         this.field_147369_b.func_71040_bB(true);
      } else if (var1.func_149506_g() == 5) {
         this.field_147369_b.func_71034_by();
      } else {
         boolean var3 = false;
         if (var1.func_149506_g() == 0) {
            var3 = true;
         }

         if (var1.func_149506_g() == 1) {
            var3 = true;
         }

         if (var1.func_149506_g() == 2) {
            var3 = true;
         }

         int var4 = var1.func_149505_c();
         int var5 = var1.func_149503_d();
         int var6 = var1.func_149502_e();
         if (var3) {
            double var7 = this.field_147369_b.field_70165_t - ((double)var4 + 0.5);
            double var9 = this.field_147369_b.field_70163_u - ((double)var5 + 0.5) + 1.5;
            double var11 = this.field_147369_b.field_70161_v - ((double)var6 + 0.5);
            double var13 = var7 * var7 + var9 * var9 + var11 * var11;
            if (var13 > 36.0) {
               return;
            }

            if (var5 >= this.field_147367_d.func_71207_Z()) {
               return;
            }
         }

         if (var1.func_149506_g() == 0) {
            if (!this.field_147367_d.func_96290_a(var2, var4, var5, var6, this.field_147369_b)) {
               this.field_147369_b.field_71134_c.func_73074_a(var4, var5, var6, var1.func_149501_f());
            } else {
               this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var4, var5, var6, var2));
            }
         } else if (var1.func_149506_g() == 2) {
            this.field_147369_b.field_71134_c.func_73082_a(var4, var5, var6);
            if (var2.func_147439_a(var4, var5, var6).func_149688_o() != Material.field_151579_a) {
               this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var4, var5, var6, var2));
            }
         } else if (var1.func_149506_g() == 1) {
            this.field_147369_b.field_71134_c.func_73073_c(var4, var5, var6);
            if (var2.func_147439_a(var4, var5, var6).func_149688_o() != Material.field_151579_a) {
               this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var4, var5, var6, var2));
            }
         }
      }
   }

   @Override
   public void func_147346_a(C08PacketPlayerBlockPlacement var1) {
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      ItemStack var3 = this.field_147369_b.field_71071_by.func_70448_g();
      boolean var4 = false;
      int var5 = var1.func_149576_c();
      int var6 = var1.func_149571_d();
      int var7 = var1.func_149570_e();
      int var8 = var1.func_149568_f();
      this.field_147369_b.func_143004_u();
      if (var1.func_149568_f() == 255) {
         if (var3 == null) {
            return;
         }

         this.field_147369_b.field_71134_c.func_73085_a(this.field_147369_b, var2, var3);
      } else if (var1.func_149571_d() < this.field_147367_d.func_71207_Z() - 1
         || var1.func_149568_f() != 1 && var1.func_149571_d() < this.field_147367_d.func_71207_Z()) {
         if (this.field_147380_r
            && this.field_147369_b.func_70092_e((double)var5 + 0.5, (double)var6 + 0.5, (double)var7 + 0.5) < 64.0
            && !this.field_147367_d.func_96290_a(var2, var5, var6, var7, this.field_147369_b)) {
            this.field_147369_b
               .field_71134_c
               .func_73078_a(this.field_147369_b, var2, var3, var5, var6, var7, var8, var1.func_149573_h(), var1.func_149569_i(), var1.func_149575_j());
         }

         var4 = true;
      } else {
         ChatComponentTranslation var9 = new ChatComponentTranslation("build.tooHigh", this.field_147367_d.func_71207_Z());
         var9.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         this.field_147369_b.field_71135_a.func_147359_a(new S02PacketChat(var9));
         var4 = true;
      }

      if (var4) {
         this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var5, var6, var7, var2));
         if (var8 == 0) {
            --var6;
         }

         if (var8 == 1) {
            ++var6;
         }

         if (var8 == 2) {
            --var7;
         }

         if (var8 == 3) {
            ++var7;
         }

         if (var8 == 4) {
            --var5;
         }

         if (var8 == 5) {
            ++var5;
         }

         this.field_147369_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var5, var6, var7, var2));
      }

      var3 = this.field_147369_b.field_71071_by.func_70448_g();
      if (var3 != null && var3.field_77994_a == 0) {
         this.field_147369_b.field_71071_by.field_70462_a[this.field_147369_b.field_71071_by.field_70461_c] = null;
         var3 = null;
      }

      if (var3 == null || var3.func_77988_m() == 0) {
         this.field_147369_b.field_71137_h = true;
         this.field_147369_b.field_71071_by.field_70462_a[this.field_147369_b.field_71071_by.field_70461_c] = ItemStack.func_77944_b(
            this.field_147369_b.field_71071_by.field_70462_a[this.field_147369_b.field_71071_by.field_70461_c]
         );
         Slot var11 = this.field_147369_b.field_71070_bA.func_75147_a(this.field_147369_b.field_71071_by, this.field_147369_b.field_71071_by.field_70461_c);
         this.field_147369_b.field_71070_bA.func_75142_b();
         this.field_147369_b.field_71137_h = false;
         if (!ItemStack.func_77989_b(this.field_147369_b.field_71071_by.func_70448_g(), var1.func_149574_g())) {
            this.func_147359_a(
               new S2FPacketSetSlot(this.field_147369_b.field_71070_bA.field_75152_c, var11.field_75222_d, this.field_147369_b.field_71071_by.func_70448_g())
            );
         }
      }
   }

   @Override
   public void func_147231_a(IChatComponent var1) {
      field_147370_c.info(this.field_147369_b.func_70005_c_() + " lost connection: " + var1);
      this.field_147367_d.func_147132_au();
      ChatComponentTranslation var2 = new ChatComponentTranslation("multiplayer.player.left", this.field_147369_b.func_145748_c_());
      var2.func_150256_b().func_150238_a(EnumChatFormatting.YELLOW);
      this.field_147367_d.func_71203_ab().func_148539_a(var2);
      this.field_147369_b.func_71123_m();
      this.field_147367_d.func_71203_ab().func_72367_e(this.field_147369_b);
      if (this.field_147367_d.func_71264_H() && this.field_147369_b.func_70005_c_().equals(this.field_147367_d.func_71214_G())) {
         field_147370_c.info("Stopping singleplayer server as player logged out");
         this.field_147367_d.func_71263_m();
      }
   }

   public void func_147359_a(Packet var1) {
      if (var1 instanceof S02PacketChat) {
         S02PacketChat var2 = (S02PacketChat)var1;
         EntityPlayer$EnumChatVisibility var3 = this.field_147369_b.func_147096_v();
         if (var3 == EntityPlayer$EnumChatVisibility.HIDDEN) {
            return;
         }

         if (var3 == EntityPlayer$EnumChatVisibility.SYSTEM && !var2.func_148916_d()) {
            return;
         }
      }

      try {
         this.field_147371_a.func_150725_a(var1);
      } catch (Throwable var5) {
         CrashReport var6 = CrashReport.func_85055_a(var5, "Sending packet");
         CrashReportCategory var4 = var6.func_85058_a("Packet being sent");
         var4.func_71500_a("Packet class", new NetHandlerPlayServer$2(this, var1));
         throw new ReportedException(var6);
      }
   }

   @Override
   public void func_147355_a(C09PacketHeldItemChange var1) {
      if (var1.func_149614_c() >= 0 && var1.func_149614_c() < InventoryPlayer.func_70451_h()) {
         this.field_147369_b.field_71071_by.field_70461_c = var1.func_149614_c();
         this.field_147369_b.func_143004_u();
      } else {
         field_147370_c.warn(this.field_147369_b.func_70005_c_() + " tried to set an invalid carried item");
      }
   }

   @Override
   public void func_147354_a(C01PacketChatMessage var1) {
      if (this.field_147369_b.func_147096_v() == EntityPlayer$EnumChatVisibility.HIDDEN) {
         ChatComponentTranslation var5 = new ChatComponentTranslation("chat.cannotSend");
         var5.func_150256_b().func_150238_a(EnumChatFormatting.RED);
         this.func_147359_a(new S02PacketChat(var5));
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
            ChatComponentTranslation var6 = new ChatComponentTranslation("chat.type.text", this.field_147369_b.func_145748_c_(), var2);
            this.field_147367_d.func_71203_ab().func_148544_a(var6, false);
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

   @Override
   public void func_147350_a(C0APacketAnimation var1) {
      this.field_147369_b.func_143004_u();
      if (var1.func_149421_d() == 1) {
         this.field_147369_b.func_71038_i();
      }
   }

   @Override
   public void func_147357_a(C0BPacketEntityAction var1) {
      this.field_147369_b.func_143004_u();
      if (var1.func_149513_d() == 1) {
         this.field_147369_b.func_70095_a(true);
      } else if (var1.func_149513_d() == 2) {
         this.field_147369_b.func_70095_a(false);
      } else if (var1.func_149513_d() == 4) {
         this.field_147369_b.func_70031_b(true);
      } else if (var1.func_149513_d() == 5) {
         this.field_147369_b.func_70031_b(false);
      } else if (var1.func_149513_d() == 3) {
         this.field_147369_b.func_70999_a(false, true, true);
         this.field_147380_r = false;
      } else if (var1.func_149513_d() == 6) {
         if (this.field_147369_b.field_70154_o != null && this.field_147369_b.field_70154_o instanceof EntityHorse) {
            ((EntityHorse)this.field_147369_b.field_70154_o).func_110206_u(var1.func_149512_e());
         }
      } else if (var1.func_149513_d() == 7 && this.field_147369_b.field_70154_o != null && this.field_147369_b.field_70154_o instanceof EntityHorse) {
         ((EntityHorse)this.field_147369_b.field_70154_o).func_110199_f(this.field_147369_b);
      }
   }

   @Override
   public void func_147340_a(C02PacketUseEntity var1) {
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      Entity var3 = var1.func_149564_a(var2);
      this.field_147369_b.func_143004_u();
      if (var3 != null) {
         boolean var4 = this.field_147369_b.func_70685_l(var3);
         double var5 = 36.0;
         if (!var4) {
            var5 = 9.0;
         }

         if (this.field_147369_b.func_70068_e(var3) < var5) {
            if (var1.func_149565_c() == C02PacketUseEntity$Action.INTERACT) {
               this.field_147369_b.func_70998_m(var3);
            } else if (var1.func_149565_c() == C02PacketUseEntity$Action.ATTACK) {
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

   @Override
   public void func_147342_a(C16PacketClientStatus var1) {
      this.field_147369_b.func_143004_u();
      C16PacketClientStatus$EnumState var2 = var1.func_149435_c();
      switch(var2) {
         case PERFORM_RESPAWN:
            if (this.field_147369_b.field_71136_j) {
               this.field_147369_b = this.field_147367_d.func_71203_ab().func_72368_a(this.field_147369_b, 0, true);
            } else if (this.field_147369_b.func_71121_q().func_72912_H().func_76093_s()) {
               if (this.field_147367_d.func_71264_H() && this.field_147369_b.func_70005_c_().equals(this.field_147367_d.func_71214_G())) {
                  this.field_147369_b.field_71135_a.func_147360_c("You have died. Game over, man, it's game over!");
                  this.field_147367_d.func_71272_O();
               } else {
                  UserListBansEntry var3 = new UserListBansEntry(
                     this.field_147369_b.func_146103_bH(), null, "(You just lost the game)", null, "Death in Hardcore"
                  );
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

   @Override
   public void func_147356_a(C0DPacketCloseWindow var1) {
      this.field_147369_b.func_71128_l();
   }

   @Override
   public void func_147351_a(C0EPacketClickWindow var1) {
      this.field_147369_b.func_143004_u();
      if (this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149548_c() && this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b)) {
         ItemStack var2 = this.field_147369_b
            .field_71070_bA
            .func_75144_a(var1.func_149544_d(), var1.func_149543_e(), var1.func_149542_h(), this.field_147369_b);
         if (ItemStack.func_77989_b(var1.func_149546_g(), var2)) {
            this.field_147369_b.field_71135_a.func_147359_a(new S32PacketConfirmTransaction(var1.func_149548_c(), var1.func_149547_f(), true));
            this.field_147369_b.field_71137_h = true;
            this.field_147369_b.field_71070_bA.func_75142_b();
            this.field_147369_b.func_71113_k();
            this.field_147369_b.field_71137_h = false;
         } else {
            this.field_147372_n.func_76038_a(this.field_147369_b.field_71070_bA.field_75152_c, var1.func_149547_f());
            this.field_147369_b.field_71135_a.func_147359_a(new S32PacketConfirmTransaction(var1.func_149548_c(), var1.func_149547_f(), false));
            this.field_147369_b.field_71070_bA.func_75128_a(this.field_147369_b, false);
            ArrayList var3 = new ArrayList();

            for(int var4 = 0; var4 < this.field_147369_b.field_71070_bA.field_75151_b.size(); ++var4) {
               var3.add(((Slot)this.field_147369_b.field_71070_bA.field_75151_b.get(var4)).func_75211_c());
            }

            this.field_147369_b.func_71110_a(this.field_147369_b.field_71070_bA, var3);
         }
      }
   }

   @Override
   public void func_147338_a(C11PacketEnchantItem var1) {
      this.field_147369_b.func_143004_u();
      if (this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149539_c() && this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b)) {
         this.field_147369_b.field_71070_bA.func_75140_a(this.field_147369_b, var1.func_149537_d());
         this.field_147369_b.field_71070_bA.func_75142_b();
      }
   }

   @Override
   public void func_147344_a(C10PacketCreativeInventoryAction var1) {
      if (this.field_147369_b.field_71134_c.func_73083_d()) {
         boolean var2 = var1.func_149627_c() < 0;
         ItemStack var3 = var1.func_149625_d();
         boolean var4 = var1.func_149627_c() >= 1 && var1.func_149627_c() < 36 + InventoryPlayer.func_70451_h();
         boolean var5 = var3 == null || var3.func_77973_b() != null;
         boolean var6 = var3 == null || var3.func_77960_j() >= 0 && var3.field_77994_a <= 64 && var3.field_77994_a > 0;
         if (var4 && var5 && var6) {
            if (var3 == null) {
               this.field_147369_b.field_71069_bz.func_75141_a(var1.func_149627_c(), null);
            } else {
               this.field_147369_b.field_71069_bz.func_75141_a(var1.func_149627_c(), var3);
            }

            this.field_147369_b.field_71069_bz.func_75128_a(this.field_147369_b, true);
         } else if (var2 && var5 && var6 && this.field_147375_m < 200) {
            this.field_147375_m += 20;
            EntityItem var7 = this.field_147369_b.func_71019_a(var3, true);
            if (var7 != null) {
               var7.func_70288_d();
            }
         }
      }
   }

   @Override
   public void func_147339_a(C0FPacketConfirmTransaction var1) {
      Short var2 = (Short)this.field_147372_n.func_76041_a(this.field_147369_b.field_71070_bA.field_75152_c);
      if (var2 != null
         && var1.func_149533_d() == var2
         && this.field_147369_b.field_71070_bA.field_75152_c == var1.func_149532_c()
         && !this.field_147369_b.field_71070_bA.func_75129_b(this.field_147369_b)) {
         this.field_147369_b.field_71070_bA.func_75128_a(this.field_147369_b, true);
      }
   }

   @Override
   public void func_147343_a(C12PacketUpdateSign var1) {
      this.field_147369_b.func_143004_u();
      WorldServer var2 = this.field_147367_d.func_71218_a(this.field_147369_b.field_71093_bK);
      if (var2.func_72899_e(var1.func_149588_c(), var1.func_149586_d(), var1.func_149585_e())) {
         TileEntity var3 = var2.func_147438_o(var1.func_149588_c(), var1.func_149586_d(), var1.func_149585_e());
         if (var3 instanceof TileEntitySign) {
            TileEntitySign var4 = (TileEntitySign)var3;
            if (!var4.func_145914_a() || var4.func_145911_b() != this.field_147369_b) {
               this.field_147367_d.func_71236_h("Player " + this.field_147369_b.func_70005_c_() + " just tried to change non-editable sign");
               return;
            }
         }

         for(int var8 = 0; var8 < 4; ++var8) {
            boolean var5 = true;
            if (var1.func_149589_f()[var8].length() > 15) {
               var5 = false;
            } else {
               for(int var6 = 0; var6 < var1.func_149589_f()[var8].length(); ++var6) {
                  if (!ChatAllowedCharacters.func_71566_a(var1.func_149589_f()[var8].charAt(var6))) {
                     var5 = false;
                  }
               }
            }

            if (!var5) {
               var1.func_149589_f()[var8] = "!?";
            }
         }

         if (var3 instanceof TileEntitySign) {
            int var9 = var1.func_149588_c();
            int var10 = var1.func_149586_d();
            int var11 = var1.func_149585_e();
            TileEntitySign var7 = (TileEntitySign)var3;
            System.arraycopy(var1.func_149589_f(), 0, var7.field_145915_a, 0, 4);
            var7.func_70296_d();
            var2.func_147471_g(var9, var10, var11);
         }
      }
   }

   @Override
   public void func_147353_a(C00PacketKeepAlive var1) {
      if (var1.func_149460_c() == this.field_147378_h) {
         int var2 = (int)(this.func_147363_d() - this.field_147379_i);
         this.field_147369_b.field_71138_i = (this.field_147369_b.field_71138_i * 3 + var2) / 4;
      }
   }

   private long func_147363_d() {
      return System.nanoTime() / 1000000L;
   }

   @Override
   public void func_147348_a(C13PacketPlayerAbilities var1) {
      this.field_147369_b.field_71075_bZ.field_75100_b = var1.func_149488_d() && this.field_147369_b.field_71075_bZ.field_75101_c;
   }

   @Override
   public void func_147341_a(C14PacketTabComplete var1) {
      ArrayList var2 = Lists.newArrayList();

      for(String var4 : this.field_147367_d.func_71248_a(this.field_147369_b, var1.func_149419_c())) {
         var2.add(var4);
      }

      this.field_147369_b.field_71135_a.func_147359_a(new S3APacketTabComplete(var2.toArray(new String[var2.size()])));
   }

   @Override
   public void func_147352_a(C15PacketClientSettings var1) {
      this.field_147369_b.func_147100_a(var1);
   }

   @Override
   public void func_147349_a(C17PacketCustomPayload var1) {
      if ("MC|BEdit".equals(var1.func_149559_c())) {
         PacketBuffer var44 = new PacketBuffer(Unpooled.wrappedBuffer(var1.func_149558_e()));

         try {
            ItemStack var49 = var44.func_150791_c();
            if (var49 == null) {
               return;
            }

            if (!ItemWritableBook.func_150930_a(var49.func_77978_p())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack var53 = this.field_147369_b.field_71071_by.func_70448_g();
            if (var53 != null) {
               if (var49.func_77973_b() == Items.field_151099_bA && var49.func_77973_b() == var53.func_77973_b()) {
                  var53.func_77983_a("pages", var49.func_77978_p().func_150295_c("pages", 8));
               }

               return;
            }
         } catch (Exception var36) {
            field_147370_c.error("Couldn't handle book info", var36);
            return;
         } finally {
            var44.release();
         }

         return;
      } else if ("MC|BSign".equals(var1.func_149559_c())) {
         PacketBuffer var43 = new PacketBuffer(Unpooled.wrappedBuffer(var1.func_149558_e()));

         try {
            ItemStack var48 = var43.func_150791_c();
            if (var48 == null) {
               return;
            }

            if (!ItemEditableBook.func_77828_a(var48.func_77978_p())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack var52 = this.field_147369_b.field_71071_by.func_70448_g();
            if (var52 != null) {
               if (var48.func_77973_b() == Items.field_151164_bB && var52.func_77973_b() == Items.field_151099_bA) {
                  var52.func_77983_a("author", new NBTTagString(this.field_147369_b.func_70005_c_()));
                  var52.func_77983_a("title", new NBTTagString(var48.func_77978_p().func_74779_i("title")));
                  var52.func_77983_a("pages", var48.func_77978_p().func_150295_c("pages", 8));
                  var52.func_150996_a(Items.field_151164_bB);
               }

               return;
            }
         } catch (Exception var38) {
            field_147370_c.error("Couldn't sign book", var38);
            return;
         } finally {
            var43.release();
         }

         return;
      } else if ("MC|TrSel".equals(var1.func_149559_c())) {
         try {
            DataInputStream var2 = new DataInputStream(new ByteArrayInputStream(var1.func_149558_e()));
            int var3 = var2.readInt();
            Container var4 = this.field_147369_b.field_71070_bA;
            if (var4 instanceof ContainerMerchant) {
               ((ContainerMerchant)var4).func_75175_c(var3);
            }
         } catch (Exception var35) {
            field_147370_c.error("Couldn't select trade", var35);
         }
      } else if ("MC|AdvCdm".equals(var1.func_149559_c())) {
         if (!this.field_147367_d.func_82356_Z()) {
            this.field_147369_b.func_145747_a(new ChatComponentTranslation("advMode.notEnabled"));
         } else if (this.field_147369_b.func_70003_b(2, "") && this.field_147369_b.field_71075_bZ.field_75098_d) {
            PacketBuffer var40 = new PacketBuffer(Unpooled.wrappedBuffer(var1.func_149558_e()));

            try {
               byte var45 = var40.readByte();
               CommandBlockLogic var50 = null;
               if (var45 == 0) {
                  TileEntity var5 = this.field_147369_b.field_70170_p.func_147438_o(var40.readInt(), var40.readInt(), var40.readInt());
                  if (var5 instanceof TileEntityCommandBlock) {
                     var50 = ((TileEntityCommandBlock)var5).func_145993_a();
                  }
               } else if (var45 == 1) {
                  Entity var54 = this.field_147369_b.field_70170_p.func_73045_a(var40.readInt());
                  if (var54 instanceof EntityMinecartCommandBlock) {
                     var50 = ((EntityMinecartCommandBlock)var54).func_145822_e();
                  }
               }

               String var55 = var40.func_150789_c(var40.readableBytes());
               if (var50 != null) {
                  var50.func_145752_a(var55);
                  var50.func_145756_e();
                  this.field_147369_b.func_145747_a(new ChatComponentTranslation("advMode.setCommand.success", var55));
               }
            } catch (Exception var33) {
               field_147370_c.error("Couldn't set command block", var33);
            } finally {
               var40.release();
            }
         } else {
            this.field_147369_b.func_145747_a(new ChatComponentTranslation("advMode.notAllowed"));
         }
      } else if ("MC|Beacon".equals(var1.func_149559_c())) {
         if (this.field_147369_b.field_71070_bA instanceof ContainerBeacon) {
            try {
               DataInputStream var41 = new DataInputStream(new ByteArrayInputStream(var1.func_149558_e()));
               int var46 = var41.readInt();
               int var51 = var41.readInt();
               ContainerBeacon var56 = (ContainerBeacon)this.field_147369_b.field_71070_bA;
               Slot var6 = var56.func_75139_a(0);
               if (var6.func_75216_d()) {
                  var6.func_75209_a(1);
                  TileEntityBeacon var7 = var56.func_148327_e();
                  var7.func_146001_d(var46);
                  var7.func_146004_e(var51);
                  var7.func_70296_d();
               }
            } catch (Exception var32) {
               field_147370_c.error("Couldn't set beacon", var32);
            }
         }
      } else if ("MC|ItemName".equals(var1.func_149559_c()) && this.field_147369_b.field_71070_bA instanceof ContainerRepair) {
         ContainerRepair var42 = (ContainerRepair)this.field_147369_b.field_71070_bA;
         if (var1.func_149558_e() != null && var1.func_149558_e().length >= 1) {
            String var47 = ChatAllowedCharacters.func_71565_a(new String(var1.func_149558_e(), Charsets.UTF_8));
            if (var47.length() <= 30) {
               var42.func_82850_a(var47);
            }
         } else {
            var42.func_82850_a("");
         }
      }
   }

   @Override
   public void func_147232_a(EnumConnectionState var1, EnumConnectionState var2) {
      if (var2 != EnumConnectionState.PLAY) {
         throw new IllegalStateException("Unexpected change in protocol!");
      }
   }
}
