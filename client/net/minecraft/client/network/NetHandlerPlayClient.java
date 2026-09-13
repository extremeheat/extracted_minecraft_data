package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData$ServerResourceMode;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.MetadataAchievement;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer$C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S20PacketEntityProperties$Snapshot;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedOnlineScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
   private static final Logger field_147301_d = LogManager.getLogger();
   private final NetworkManager field_147302_e;
   private Minecraft field_147299_f;
   private WorldClient field_147300_g;
   private boolean field_147309_h;
   public MapStorage field_147305_a = new MapStorage(null);
   private Map field_147310_i = new HashMap();
   public List field_147303_b = new ArrayList();
   public int field_147304_c = 20;
   private GuiScreen field_147307_j;
   private boolean field_147308_k = false;
   private Random field_147306_l = new Random();

   public NetHandlerPlayClient(Minecraft var1, GuiScreen var2, NetworkManager var3) {
      super();
      this.field_147299_f = var1;
      this.field_147307_j = var2;
      this.field_147302_e = var3;
   }

   public void func_147296_c() {
      this.field_147300_g = null;
   }

   @Override
   public void func_147233_a() {
   }

   @Override
   public void func_147282_a(S01PacketJoinGame var1) {
      this.field_147299_f.field_71442_b = new PlayerControllerMP(this.field_147299_f, this);
      this.field_147300_g = new WorldClient(
         this,
         new WorldSettings(0L, var1.func_149198_e(), false, var1.func_149195_d(), var1.func_149196_i()),
         var1.func_149194_f(),
         var1.func_149192_g(),
         this.field_147299_f.field_71424_I
      );
      this.field_147300_g.field_72995_K = true;
      this.field_147299_f.func_71403_a(this.field_147300_g);
      this.field_147299_f.field_71439_g.field_71093_bK = var1.func_149194_f();
      this.field_147299_f.func_147108_a(new GuiDownloadTerrain(this));
      this.field_147299_f.field_71439_g.func_145769_d(var1.func_149197_c());
      this.field_147304_c = var1.func_149193_h();
      this.field_147299_f.field_71442_b.func_78746_a(var1.func_149198_e());
      this.field_147299_f.field_71474_y.func_82879_c();
      this.field_147302_e.func_150725_a(new C17PacketCustomPayload("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)));
   }

   @Override
   public void func_147235_a(S0EPacketSpawnObject var1) {
      double var2 = (double)var1.func_148997_d() / 32.0;
      double var4 = (double)var1.func_148998_e() / 32.0;
      double var6 = (double)var1.func_148994_f() / 32.0;
      Object var8 = null;
      if (var1.func_148993_l() == 10) {
         var8 = EntityMinecart.func_94090_a(this.field_147300_g, var2, var4, var6, var1.func_149009_m());
      } else if (var1.func_148993_l() == 90) {
         Entity var9 = this.field_147300_g.func_73045_a(var1.func_149009_m());
         if (var9 instanceof EntityPlayer) {
            var8 = new EntityFishHook(this.field_147300_g, var2, var4, var6, (EntityPlayer)var9);
         }

         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 60) {
         var8 = new EntityArrow(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 61) {
         var8 = new EntitySnowball(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 71) {
         var8 = new EntityItemFrame(this.field_147300_g, (int)var2, (int)var4, (int)var6, var1.func_149009_m());
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 77) {
         var8 = new EntityLeashKnot(this.field_147300_g, (int)var2, (int)var4, (int)var6);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 65) {
         var8 = new EntityEnderPearl(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 72) {
         var8 = new EntityEnderEye(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 76) {
         var8 = new EntityFireworkRocket(this.field_147300_g, var2, var4, var6, null);
      } else if (var1.func_148993_l() == 63) {
         var8 = new EntityLargeFireball(
            this.field_147300_g,
            var2,
            var4,
            var6,
            (double)var1.func_149010_g() / 8000.0,
            (double)var1.func_149004_h() / 8000.0,
            (double)var1.func_148999_i() / 8000.0
         );
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 64) {
         var8 = new EntitySmallFireball(
            this.field_147300_g,
            var2,
            var4,
            var6,
            (double)var1.func_149010_g() / 8000.0,
            (double)var1.func_149004_h() / 8000.0,
            (double)var1.func_148999_i() / 8000.0
         );
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 66) {
         var8 = new EntityWitherSkull(
            this.field_147300_g,
            var2,
            var4,
            var6,
            (double)var1.func_149010_g() / 8000.0,
            (double)var1.func_149004_h() / 8000.0,
            (double)var1.func_148999_i() / 8000.0
         );
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 62) {
         var8 = new EntityEgg(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 73) {
         var8 = new EntityPotion(this.field_147300_g, var2, var4, var6, var1.func_149009_m());
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 75) {
         var8 = new EntityExpBottle(this.field_147300_g, var2, var4, var6);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 1) {
         var8 = new EntityBoat(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 50) {
         var8 = new EntityTNTPrimed(this.field_147300_g, var2, var4, var6, null);
      } else if (var1.func_148993_l() == 51) {
         var8 = new EntityEnderCrystal(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 2) {
         var8 = new EntityItem(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 70) {
         var8 = new EntityFallingBlock(this.field_147300_g, var2, var4, var6, Block.func_149729_e(var1.func_149009_m() & 65535), var1.func_149009_m() >> 16);
         var1.func_149002_g(0);
      }

      if (var8 != null) {
         ((Entity)var8).field_70118_ct = var1.func_148997_d();
         ((Entity)var8).field_70117_cu = var1.func_148998_e();
         ((Entity)var8).field_70116_cv = var1.func_148994_f();
         ((Entity)var8).field_70125_A = (float)(var1.func_149008_j() * 360) / 256.0F;
         ((Entity)var8).field_70177_z = (float)(var1.func_149006_k() * 360) / 256.0F;
         Entity[] var12 = ((Entity)var8).func_70021_al();
         if (var12 != null) {
            int var10 = var1.func_149001_c() - ((Entity)var8).func_145782_y();

            for(int var11 = 0; var11 < var12.length; ++var11) {
               var12[var11].func_145769_d(var12[var11].func_145782_y() + var10);
            }
         }

         ((Entity)var8).func_145769_d(var1.func_149001_c());
         this.field_147300_g.func_73027_a(var1.func_149001_c(), (Entity)var8);
         if (var1.func_149009_m() > 0) {
            if (var1.func_148993_l() == 60) {
               Entity var13 = this.field_147300_g.func_73045_a(var1.func_149009_m());
               if (var13 instanceof EntityLivingBase) {
                  EntityArrow var14 = (EntityArrow)var8;
                  var14.field_70250_c = var13;
               }
            }

            ((Entity)var8).func_70016_h((double)var1.func_149010_g() / 8000.0, (double)var1.func_149004_h() / 8000.0, (double)var1.func_148999_i() / 8000.0);
         }
      }
   }

   @Override
   public void func_147286_a(S11PacketSpawnExperienceOrb var1) {
      EntityXPOrb var2 = new EntityXPOrb(
         this.field_147300_g, (double)var1.func_148984_d(), (double)var1.func_148983_e(), (double)var1.func_148982_f(), var1.func_148986_g()
      );
      var2.field_70118_ct = var1.func_148984_d();
      var2.field_70117_cu = var1.func_148983_e();
      var2.field_70116_cv = var1.func_148982_f();
      var2.field_70177_z = 0.0F;
      var2.field_70125_A = 0.0F;
      var2.func_145769_d(var1.func_148985_c());
      this.field_147300_g.func_73027_a(var1.func_148985_c(), var2);
   }

   @Override
   public void func_147292_a(S2CPacketSpawnGlobalEntity var1) {
      double var2 = (double)var1.func_149051_d() / 32.0;
      double var4 = (double)var1.func_149050_e() / 32.0;
      double var6 = (double)var1.func_149049_f() / 32.0;
      EntityLightningBolt var8 = null;
      if (var1.func_149053_g() == 1) {
         var8 = new EntityLightningBolt(this.field_147300_g, var2, var4, var6);
      }

      if (var8 != null) {
         var8.field_70118_ct = var1.func_149051_d();
         var8.field_70117_cu = var1.func_149050_e();
         var8.field_70116_cv = var1.func_149049_f();
         var8.field_70177_z = 0.0F;
         var8.field_70125_A = 0.0F;
         var8.func_145769_d(var1.func_149052_c());
         this.field_147300_g.func_72942_c(var8);
      }
   }

   @Override
   public void func_147288_a(S10PacketSpawnPainting var1) {
      EntityPainting var2 = new EntityPainting(
         this.field_147300_g, var1.func_148964_d(), var1.func_148963_e(), var1.func_148962_f(), var1.func_148966_g(), var1.func_148961_h()
      );
      this.field_147300_g.func_73027_a(var1.func_148965_c(), var2);
   }

   @Override
   public void func_147244_a(S12PacketEntityVelocity var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149412_c());
      if (var2 != null) {
         var2.func_70016_h((double)var1.func_149411_d() / 8000.0, (double)var1.func_149410_e() / 8000.0, (double)var1.func_149409_f() / 8000.0);
      }
   }

   @Override
   public void func_147284_a(S1CPacketEntityMetadata var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149375_d());
      if (var2 != null && var1.func_149376_c() != null) {
         var2.func_70096_w().func_75687_a(var1.func_149376_c());
      }
   }

   @Override
   public void func_147237_a(S0CPacketSpawnPlayer var1) {
      double var2 = (double)var1.func_148942_f() / 32.0;
      double var4 = (double)var1.func_148949_g() / 32.0;
      double var6 = (double)var1.func_148946_h() / 32.0;
      float var8 = (float)(var1.func_148941_i() * 360) / 256.0F;
      float var9 = (float)(var1.func_148945_j() * 360) / 256.0F;
      GameProfile var10 = var1.func_148948_e();
      EntityOtherPlayerMP var11 = new EntityOtherPlayerMP(this.field_147299_f.field_71441_e, var1.func_148948_e());
      var11.field_70169_q = var11.field_70142_S = (double)(var11.field_70118_ct = var1.func_148942_f());
      var11.field_70167_r = var11.field_70137_T = (double)(var11.field_70117_cu = var1.func_148949_g());
      var11.field_70166_s = var11.field_70136_U = (double)(var11.field_70116_cv = var1.func_148946_h());
      int var12 = var1.func_148947_k();
      if (var12 == 0) {
         var11.field_71071_by.field_70462_a[var11.field_71071_by.field_70461_c] = null;
      } else {
         var11.field_71071_by.field_70462_a[var11.field_71071_by.field_70461_c] = new ItemStack(Item.func_150899_d(var12), 1, 0);
      }

      var11.func_70080_a(var2, var4, var6, var8, var9);
      this.field_147300_g.func_73027_a(var1.func_148943_d(), var11);
      List var13 = var1.func_148944_c();
      if (var13 != null) {
         var11.func_70096_w().func_75687_a(var13);
      }
   }

   @Override
   public void func_147275_a(S18PacketEntityTeleport var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149451_c());
      if (var2 != null) {
         var2.field_70118_ct = var1.func_149449_d();
         var2.field_70117_cu = var1.func_149448_e();
         var2.field_70116_cv = var1.func_149446_f();
         double var3 = (double)var2.field_70118_ct / 32.0;
         double var5 = (double)var2.field_70117_cu / 32.0 + 0.015625;
         double var7 = (double)var2.field_70116_cv / 32.0;
         float var9 = (float)(var1.func_149450_g() * 360) / 256.0F;
         float var10 = (float)(var1.func_149447_h() * 360) / 256.0F;
         var2.func_70056_a(var3, var5, var7, var9, var10, 3);
      }
   }

   @Override
   public void func_147257_a(S09PacketHeldItemChange var1) {
      if (var1.func_149385_c() >= 0 && var1.func_149385_c() < InventoryPlayer.func_70451_h()) {
         this.field_147299_f.field_71439_g.field_71071_by.field_70461_c = var1.func_149385_c();
      }
   }

   @Override
   public void func_147259_a(S14PacketEntity var1) {
      Entity var2 = var1.func_149065_a(this.field_147300_g);
      if (var2 != null) {
         var2.field_70118_ct += var1.func_149062_c();
         var2.field_70117_cu += var1.func_149061_d();
         var2.field_70116_cv += var1.func_149064_e();
         double var3 = (double)var2.field_70118_ct / 32.0;
         double var5 = (double)var2.field_70117_cu / 32.0;
         double var7 = (double)var2.field_70116_cv / 32.0;
         float var9 = var1.func_149060_h() ? (float)(var1.func_149066_f() * 360) / 256.0F : var2.field_70177_z;
         float var10 = var1.func_149060_h() ? (float)(var1.func_149063_g() * 360) / 256.0F : var2.field_70125_A;
         var2.func_70056_a(var3, var5, var7, var9, var10, 3);
      }
   }

   @Override
   public void func_147267_a(S19PacketEntityHeadLook var1) {
      Entity var2 = var1.func_149381_a(this.field_147300_g);
      if (var2 != null) {
         float var3 = (float)(var1.func_149380_c() * 360) / 256.0F;
         var2.func_70034_d(var3);
      }
   }

   @Override
   public void func_147238_a(S13PacketDestroyEntities var1) {
      for(int var2 = 0; var2 < var1.func_149098_c().length; ++var2) {
         this.field_147300_g.func_73028_b(var1.func_149098_c()[var2]);
      }
   }

   @Override
   public void func_147258_a(S08PacketPlayerPosLook var1) {
      EntityClientPlayerMP var2 = this.field_147299_f.field_71439_g;
      double var3 = var1.func_148932_c();
      double var5 = var1.func_148928_d();
      double var7 = var1.func_148933_e();
      float var9 = var1.func_148931_f();
      float var10 = var1.func_148930_g();
      var2.field_70139_V = 0.0F;
      var2.field_70159_w = var2.field_70181_x = var2.field_70179_y = 0.0;
      var2.func_70080_a(var3, var5, var7, var9, var10);
      this.field_147302_e
         .func_150725_a(
            new C03PacketPlayer$C06PacketPlayerPosLook(
               var2.field_70165_t,
               var2.field_70121_D.field_72338_b,
               var2.field_70163_u,
               var2.field_70161_v,
               var1.func_148931_f(),
               var1.func_148930_g(),
               var1.func_148929_h()
            )
         );
      if (!this.field_147309_h) {
         this.field_147299_f.field_71439_g.field_70169_q = this.field_147299_f.field_71439_g.field_70165_t;
         this.field_147299_f.field_71439_g.field_70167_r = this.field_147299_f.field_71439_g.field_70163_u;
         this.field_147299_f.field_71439_g.field_70166_s = this.field_147299_f.field_71439_g.field_70161_v;
         this.field_147309_h = true;
         this.field_147299_f.func_147108_a(null);
      }
   }

   @Override
   public void func_147287_a(S22PacketMultiBlockChange var1) {
      int var2 = var1.func_148920_c().field_77276_a * 16;
      int var3 = var1.func_148920_c().field_77275_b * 16;
      if (var1.func_148921_d() != null) {
         DataInputStream var4 = new DataInputStream(new ByteArrayInputStream(var1.func_148921_d()));

         try {
            for(int var5 = 0; var5 < var1.func_148922_e(); ++var5) {
               short var6 = var4.readShort();
               short var7 = var4.readShort();
               int var8 = var7 >> 4 & 4095;
               int var9 = var7 & 15;
               int var10 = var6 >> 12 & 15;
               int var11 = var6 >> 8 & 15;
               int var12 = var6 & 255;
               this.field_147300_g.func_147492_c(var10 + var2, var12, var11 + var3, Block.func_149729_e(var8), var9);
            }
         } catch (IOException var13) {
         }
      }
   }

   @Override
   public void func_147263_a(S21PacketChunkData var1) {
      if (var1.func_149274_i()) {
         if (var1.func_149276_g() == 0) {
            this.field_147300_g.func_73025_a(var1.func_149273_e(), var1.func_149271_f(), false);
            return;
         }

         this.field_147300_g.func_73025_a(var1.func_149273_e(), var1.func_149271_f(), true);
      }

      this.field_147300_g
         .func_73031_a(var1.func_149273_e() << 4, 0, var1.func_149271_f() << 4, (var1.func_149273_e() << 4) + 15, 256, (var1.func_149271_f() << 4) + 15);
      Chunk var2 = this.field_147300_g.func_72964_e(var1.func_149273_e(), var1.func_149271_f());
      var2.func_76607_a(var1.func_149272_d(), var1.func_149276_g(), var1.func_149270_h(), var1.func_149274_i());
      this.field_147300_g
         .func_147458_c(var1.func_149273_e() << 4, 0, var1.func_149271_f() << 4, (var1.func_149273_e() << 4) + 15, 256, (var1.func_149271_f() << 4) + 15);
      if (!var1.func_149274_i() || !(this.field_147300_g.field_73011_w instanceof WorldProviderSurface)) {
         var2.func_76613_n();
      }
   }

   @Override
   public void func_147234_a(S23PacketBlockChange var1) {
      this.field_147300_g.func_147492_c(var1.func_148879_d(), var1.func_148878_e(), var1.func_148877_f(), var1.func_148880_c(), var1.func_148881_g());
   }

   @Override
   public void func_147253_a(S40PacketDisconnect var1) {
      this.field_147302_e.func_150718_a(var1.func_149165_c());
   }

   @Override
   public void func_147231_a(IChatComponent var1) {
      this.field_147299_f.func_71403_a(null);
      if (this.field_147307_j != null) {
         if (this.field_147307_j instanceof GuiScreenRealmsProxy) {
            this.field_147299_f
               .func_147108_a(new DisconnectedOnlineScreen(((GuiScreenRealmsProxy)this.field_147307_j).func_154321_a(), "disconnect.lost", var1).getProxy());
         } else {
            this.field_147299_f.func_147108_a(new GuiDisconnected(this.field_147307_j, "disconnect.lost", var1));
         }
      } else {
         this.field_147299_f.func_147108_a(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", var1));
      }
   }

   public void func_147297_a(Packet var1) {
      this.field_147302_e.func_150725_a(var1);
   }

   @Override
   public void func_147246_a(S0DPacketCollectItem var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149354_c());
      Object var3 = (EntityLivingBase)this.field_147300_g.func_73045_a(var1.func_149353_d());
      if (var3 == null) {
         var3 = this.field_147299_f.field_71439_g;
      }

      if (var2 != null) {
         if (var2 instanceof EntityXPOrb) {
            this.field_147300_g
               .func_72956_a(var2, "random.orb", 0.2F, ((this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else {
            this.field_147300_g
               .func_72956_a(var2, "random.pop", 0.2F, ((this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.field_147299_f.field_71452_i.func_78873_a(new EntityPickupFX(this.field_147299_f.field_71441_e, var2, (Entity)var3, -0.5F));
         this.field_147300_g.func_73028_b(var1.func_149354_c());
      }
   }

   @Override
   public void func_147251_a(S02PacketChat var1) {
      this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(var1.func_148915_c());
   }

   @Override
   public void func_147279_a(S0BPacketAnimation var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_148978_c());
      if (var2 != null) {
         if (var1.func_148977_d() == 0) {
            EntityLivingBase var3 = (EntityLivingBase)var2;
            var3.func_71038_i();
         } else if (var1.func_148977_d() == 1) {
            var2.func_70057_ab();
         } else if (var1.func_148977_d() == 2) {
            EntityPlayer var4 = (EntityPlayer)var2;
            var4.func_70999_a(false, false, false);
         } else if (var1.func_148977_d() == 4) {
            this.field_147299_f.field_71452_i.func_78873_a(new EntityCrit2FX(this.field_147299_f.field_71441_e, var2));
         } else if (var1.func_148977_d() == 5) {
            EntityCrit2FX var5 = new EntityCrit2FX(this.field_147299_f.field_71441_e, var2, "magicCrit");
            this.field_147299_f.field_71452_i.func_78873_a(var5);
         }
      }
   }

   @Override
   public void func_147278_a(S0APacketUseBed var1) {
      var1.func_149091_a(this.field_147300_g).func_71018_a(var1.func_149092_c(), var1.func_149090_d(), var1.func_149089_e());
   }

   @Override
   public void func_147281_a(S0FPacketSpawnMob var1) {
      double var2 = (double)var1.func_149023_f() / 32.0;
      double var4 = (double)var1.func_149034_g() / 32.0;
      double var6 = (double)var1.func_149029_h() / 32.0;
      float var8 = (float)(var1.func_149028_l() * 360) / 256.0F;
      float var9 = (float)(var1.func_149030_m() * 360) / 256.0F;
      EntityLivingBase var10 = (EntityLivingBase)EntityList.func_75616_a(var1.func_149025_e(), this.field_147299_f.field_71441_e);
      var10.field_70118_ct = var1.func_149023_f();
      var10.field_70117_cu = var1.func_149034_g();
      var10.field_70116_cv = var1.func_149029_h();
      var10.field_70759_as = (float)(var1.func_149032_n() * 360) / 256.0F;
      Entity[] var11 = var10.func_70021_al();
      if (var11 != null) {
         int var12 = var1.func_149024_d() - var10.func_145782_y();

         for(int var13 = 0; var13 < var11.length; ++var13) {
            var11[var13].func_145769_d(var11[var13].func_145782_y() + var12);
         }
      }

      var10.func_145769_d(var1.func_149024_d());
      var10.func_70080_a(var2, var4, var6, var8, var9);
      var10.field_70159_w = (double)((float)var1.func_149026_i() / 8000.0F);
      var10.field_70181_x = (double)((float)var1.func_149033_j() / 8000.0F);
      var10.field_70179_y = (double)((float)var1.func_149031_k() / 8000.0F);
      this.field_147300_g.func_73027_a(var1.func_149024_d(), var10);
      List var14 = var1.func_149027_c();
      if (var14 != null) {
         var10.func_70096_w().func_75687_a(var14);
      }
   }

   @Override
   public void func_147285_a(S03PacketTimeUpdate var1) {
      this.field_147299_f.field_71441_e.func_82738_a(var1.func_149366_c());
      this.field_147299_f.field_71441_e.func_72877_b(var1.func_149365_d());
   }

   @Override
   public void func_147271_a(S05PacketSpawnPosition var1) {
      this.field_147299_f.field_71439_g.func_71063_a(new ChunkCoordinates(var1.func_149360_c(), var1.func_149359_d(), var1.func_149358_e()), true);
      this.field_147299_f.field_71441_e.func_72912_H().func_76081_a(var1.func_149360_c(), var1.func_149359_d(), var1.func_149358_e());
   }

   @Override
   public void func_147243_a(S1BPacketEntityAttach var1) {
      Object var2 = this.field_147300_g.func_73045_a(var1.func_149403_d());
      Entity var3 = this.field_147300_g.func_73045_a(var1.func_149402_e());
      if (var1.func_149404_c() == 0) {
         boolean var4 = false;
         if (var1.func_149403_d() == this.field_147299_f.field_71439_g.func_145782_y()) {
            var2 = this.field_147299_f.field_71439_g;
            if (var3 instanceof EntityBoat) {
               ((EntityBoat)var3).func_70270_d(false);
            }

            var4 = ((Entity)var2).field_70154_o == null && var3 != null;
         } else if (var3 instanceof EntityBoat) {
            ((EntityBoat)var3).func_70270_d(true);
         }

         if (var2 == null) {
            return;
         }

         ((Entity)var2).func_70078_a(var3);
         if (var4) {
            GameSettings var5 = this.field_147299_f.field_71474_y;
            this.field_147299_f
               .field_71456_v
               .func_110326_a(I18n.func_135052_a("mount.onboard", GameSettings.func_74298_c(var5.field_74311_E.func_151463_i())), false);
         }
      } else if (var1.func_149404_c() == 1 && var2 != null && var2 instanceof EntityLiving) {
         if (var3 != null) {
            ((EntityLiving)var2).func_110162_b(var3, false);
         } else {
            ((EntityLiving)var2).func_110160_i(false, false);
         }
      }
   }

   @Override
   public void func_147236_a(S19PacketEntityStatus var1) {
      Entity var2 = var1.func_149161_a(this.field_147300_g);
      if (var2 != null) {
         var2.func_70103_a(var1.func_149160_c());
      }
   }

   @Override
   public void func_147249_a(S06PacketUpdateHealth var1) {
      this.field_147299_f.field_71439_g.func_71150_b(var1.func_149332_c());
      this.field_147299_f.field_71439_g.func_71024_bL().func_75114_a(var1.func_149330_d());
      this.field_147299_f.field_71439_g.func_71024_bL().func_75119_b(var1.func_149331_e());
   }

   @Override
   public void func_147295_a(S1FPacketSetExperience var1) {
      this.field_147299_f.field_71439_g.func_71152_a(var1.func_149397_c(), var1.func_149396_d(), var1.func_149395_e());
   }

   @Override
   public void func_147280_a(S07PacketRespawn var1) {
      if (var1.func_149082_c() != this.field_147299_f.field_71439_g.field_71093_bK) {
         this.field_147309_h = false;
         Scoreboard var2 = this.field_147300_g.func_96441_U();
         this.field_147300_g = new WorldClient(
            this,
            new WorldSettings(0L, var1.func_149083_e(), false, this.field_147299_f.field_71441_e.func_72912_H().func_76093_s(), var1.func_149080_f()),
            var1.func_149082_c(),
            var1.func_149081_d(),
            this.field_147299_f.field_71424_I
         );
         this.field_147300_g.func_96443_a(var2);
         this.field_147300_g.field_72995_K = true;
         this.field_147299_f.func_71403_a(this.field_147300_g);
         this.field_147299_f.field_71439_g.field_71093_bK = var1.func_149082_c();
         this.field_147299_f.func_147108_a(new GuiDownloadTerrain(this));
      }

      this.field_147299_f.func_71354_a(var1.func_149082_c());
      this.field_147299_f.field_71442_b.func_78746_a(var1.func_149083_e());
   }

   @Override
   public void func_147283_a(S27PacketExplosion var1) {
      Explosion var2 = new Explosion(
         this.field_147299_f.field_71441_e, null, var1.func_149148_f(), var1.func_149143_g(), var1.func_149145_h(), var1.func_149146_i()
      );
      var2.field_77281_g = var1.func_149150_j();
      var2.func_77279_a(true);
      this.field_147299_f.field_71439_g.field_70159_w += (double)var1.func_149149_c();
      this.field_147299_f.field_71439_g.field_70181_x += (double)var1.func_149144_d();
      this.field_147299_f.field_71439_g.field_70179_y += (double)var1.func_149147_e();
   }

   @Override
   public void func_147265_a(S2DPacketOpenWindow var1) {
      EntityClientPlayerMP var2 = this.field_147299_f.field_71439_g;
      switch(var1.func_148899_d()) {
         case 0:
            var2.func_71007_a(new InventoryBasic(var1.func_148902_e(), var1.func_148900_g(), var1.func_148898_f()));
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 1:
            var2.func_71058_b(
               MathHelper.func_76128_c(var2.field_70165_t), MathHelper.func_76128_c(var2.field_70163_u), MathHelper.func_76128_c(var2.field_70161_v)
            );
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 2:
            TileEntityFurnace var4 = new TileEntityFurnace();
            if (var1.func_148900_g()) {
               var4.func_145951_a(var1.func_148902_e());
            }

            var2.func_146101_a(var4);
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 3:
            TileEntityDispenser var7 = new TileEntityDispenser();
            if (var1.func_148900_g()) {
               var7.func_146018_a(var1.func_148902_e());
            }

            var2.func_146102_a(var7);
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 4:
            var2.func_71002_c(
               MathHelper.func_76128_c(var2.field_70165_t),
               MathHelper.func_76128_c(var2.field_70163_u),
               MathHelper.func_76128_c(var2.field_70161_v),
               var1.func_148900_g() ? var1.func_148902_e() : null
            );
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 5:
            TileEntityBrewingStand var5 = new TileEntityBrewingStand();
            if (var1.func_148900_g()) {
               var5.func_145937_a(var1.func_148902_e());
            }

            var2.func_146098_a(var5);
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 6:
            var2.func_71030_a(new NpcMerchant(var2), var1.func_148900_g() ? var1.func_148902_e() : null);
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 7:
            TileEntityBeacon var8 = new TileEntityBeacon();
            var2.func_146104_a(var8);
            if (var1.func_148900_g()) {
               var8.func_145999_a(var1.func_148902_e());
            }

            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 8:
            var2.func_82244_d(
               MathHelper.func_76128_c(var2.field_70165_t), MathHelper.func_76128_c(var2.field_70163_u), MathHelper.func_76128_c(var2.field_70161_v)
            );
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 9:
            TileEntityHopper var3 = new TileEntityHopper();
            if (var1.func_148900_g()) {
               var3.func_145886_a(var1.func_148902_e());
            }

            var2.func_146093_a(var3);
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 10:
            TileEntityDropper var6 = new TileEntityDropper();
            if (var1.func_148900_g()) {
               var6.func_146018_a(var1.func_148902_e());
            }

            var2.func_146102_a(var6);
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            break;
         case 11:
            Entity var9 = this.field_147300_g.func_73045_a(var1.func_148897_h());
            if (var9 != null && var9 instanceof EntityHorse) {
               var2.func_110298_a((EntityHorse)var9, new AnimalChest(var1.func_148902_e(), var1.func_148900_g(), var1.func_148898_f()));
               var2.field_71070_bA.field_75152_c = var1.func_148901_c();
            }
      }
   }

   @Override
   public void func_147266_a(S2FPacketSetSlot var1) {
      EntityClientPlayerMP var2 = this.field_147299_f.field_71439_g;
      if (var1.func_149175_c() == -1) {
         var2.field_71071_by.func_70437_b(var1.func_149174_e());
      } else {
         boolean var3 = false;
         if (this.field_147299_f.field_71462_r instanceof GuiContainerCreative) {
            GuiContainerCreative var4 = (GuiContainerCreative)this.field_147299_f.field_71462_r;
            var3 = var4.func_147056_g() != CreativeTabs.field_78036_m.func_78021_a();
         }

         if (var1.func_149175_c() == 0 && var1.func_149173_d() >= 36 && var1.func_149173_d() < 45) {
            ItemStack var5 = var2.field_71069_bz.func_75139_a(var1.func_149173_d()).func_75211_c();
            if (var1.func_149174_e() != null && (var5 == null || var5.field_77994_a < var1.func_149174_e().field_77994_a)) {
               var1.func_149174_e().field_77992_b = 5;
            }

            var2.field_71069_bz.func_75141_a(var1.func_149173_d(), var1.func_149174_e());
         } else if (var1.func_149175_c() == var2.field_71070_bA.field_75152_c && (var1.func_149175_c() != 0 || !var3)) {
            var2.field_71070_bA.func_75141_a(var1.func_149173_d(), var1.func_149174_e());
         }
      }
   }

   @Override
   public void func_147239_a(S32PacketConfirmTransaction var1) {
      Container var2 = null;
      EntityClientPlayerMP var3 = this.field_147299_f.field_71439_g;
      if (var1.func_148889_c() == 0) {
         var2 = var3.field_71069_bz;
      } else if (var1.func_148889_c() == var3.field_71070_bA.field_75152_c) {
         var2 = var3.field_71070_bA;
      }

      if (var2 != null && !var1.func_148888_e()) {
         this.func_147297_a(new C0FPacketConfirmTransaction(var1.func_148889_c(), var1.func_148890_d(), true));
      }
   }

   @Override
   public void func_147241_a(S30PacketWindowItems var1) {
      EntityClientPlayerMP var2 = this.field_147299_f.field_71439_g;
      if (var1.func_148911_c() == 0) {
         var2.field_71069_bz.func_75131_a(var1.func_148910_d());
      } else if (var1.func_148911_c() == var2.field_71070_bA.field_75152_c) {
         var2.field_71070_bA.func_75131_a(var1.func_148910_d());
      }
   }

   @Override
   public void func_147268_a(S36PacketSignEditorOpen var1) {
      Object var2 = this.field_147300_g.func_147438_o(var1.func_149129_c(), var1.func_149128_d(), var1.func_149127_e());
      if (var2 == null) {
         var2 = new TileEntitySign();
         ((TileEntity)var2).func_145834_a(this.field_147300_g);
         ((TileEntity)var2).field_145851_c = var1.func_149129_c();
         ((TileEntity)var2).field_145848_d = var1.func_149128_d();
         ((TileEntity)var2).field_145849_e = var1.func_149127_e();
      }

      this.field_147299_f.field_71439_g.func_146100_a((TileEntity)var2);
   }

   @Override
   public void func_147248_a(S33PacketUpdateSign var1) {
      boolean var2 = false;
      if (this.field_147299_f.field_71441_e.func_72899_e(var1.func_149346_c(), var1.func_149345_d(), var1.func_149344_e())) {
         TileEntity var3 = this.field_147299_f.field_71441_e.func_147438_o(var1.func_149346_c(), var1.func_149345_d(), var1.func_149344_e());
         if (var3 instanceof TileEntitySign) {
            TileEntitySign var4 = (TileEntitySign)var3;
            if (var4.func_145914_a()) {
               for(int var5 = 0; var5 < 4; ++var5) {
                  var4.field_145915_a[var5] = var1.func_149347_f()[var5];
               }

               var4.func_70296_d();
            }

            var2 = true;
         }
      }

      if (!var2 && this.field_147299_f.field_71439_g != null) {
         this.field_147299_f
            .field_71439_g
            .func_145747_a(
               new ChatComponentText("Unable to locate sign at " + var1.func_149346_c() + ", " + var1.func_149345_d() + ", " + var1.func_149344_e())
            );
      }
   }

   @Override
   public void func_147273_a(S35PacketUpdateTileEntity var1) {
      if (this.field_147299_f.field_71441_e.func_72899_e(var1.func_148856_c(), var1.func_148855_d(), var1.func_148854_e())) {
         TileEntity var2 = this.field_147299_f.field_71441_e.func_147438_o(var1.func_148856_c(), var1.func_148855_d(), var1.func_148854_e());
         if (var2 != null) {
            if (var1.func_148853_f() == 1 && var2 instanceof TileEntityMobSpawner) {
               var2.func_145839_a(var1.func_148857_g());
            } else if (var1.func_148853_f() == 2 && var2 instanceof TileEntityCommandBlock) {
               var2.func_145839_a(var1.func_148857_g());
            } else if (var1.func_148853_f() == 3 && var2 instanceof TileEntityBeacon) {
               var2.func_145839_a(var1.func_148857_g());
            } else if (var1.func_148853_f() == 4 && var2 instanceof TileEntitySkull) {
               var2.func_145839_a(var1.func_148857_g());
            } else if (var1.func_148853_f() == 5 && var2 instanceof TileEntityFlowerPot) {
               var2.func_145839_a(var1.func_148857_g());
            }
         }
      }
   }

   @Override
   public void func_147245_a(S31PacketWindowProperty var1) {
      EntityClientPlayerMP var2 = this.field_147299_f.field_71439_g;
      if (var2.field_71070_bA != null && var2.field_71070_bA.field_75152_c == var1.func_149182_c()) {
         var2.field_71070_bA.func_75137_b(var1.func_149181_d(), var1.func_149180_e());
      }
   }

   @Override
   public void func_147242_a(S04PacketEntityEquipment var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149389_d());
      if (var2 != null) {
         var2.func_70062_b(var1.func_149388_e(), var1.func_149390_c());
      }
   }

   @Override
   public void func_147276_a(S2EPacketCloseWindow var1) {
      this.field_147299_f.field_71439_g.func_92015_f();
   }

   @Override
   public void func_147261_a(S24PacketBlockAction var1) {
      this.field_147299_f
         .field_71441_e
         .func_147452_c(var1.func_148867_d(), var1.func_148866_e(), var1.func_148865_f(), var1.func_148868_c(), var1.func_148869_g(), var1.func_148864_h());
   }

   @Override
   public void func_147294_a(S25PacketBlockBreakAnim var1) {
      this.field_147299_f
         .field_71441_e
         .func_147443_d(var1.func_148845_c(), var1.func_148844_d(), var1.func_148843_e(), var1.func_148842_f(), var1.func_148846_g());
   }

   @Override
   public void func_147269_a(S26PacketMapChunkBulk var1) {
      for(int var2 = 0; var2 < var1.func_149254_d(); ++var2) {
         int var3 = var1.func_149255_a(var2);
         int var4 = var1.func_149253_b(var2);
         this.field_147300_g.func_73025_a(var3, var4, true);
         this.field_147300_g.func_73031_a(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
         Chunk var5 = this.field_147300_g.func_72964_e(var3, var4);
         var5.func_76607_a(var1.func_149256_c(var2), var1.func_149252_e()[var2], var1.func_149257_f()[var2], true);
         this.field_147300_g.func_147458_c(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
         if (!(this.field_147300_g.field_73011_w instanceof WorldProviderSurface)) {
            var5.func_76613_n();
         }
      }
   }

   @Override
   public void func_147252_a(S2BPacketChangeGameState var1) {
      EntityClientPlayerMP var2 = this.field_147299_f.field_71439_g;
      int var3 = var1.func_149138_c();
      float var4 = var1.func_149137_d();
      int var5 = MathHelper.func_76141_d(var4 + 0.5F);
      if (var3 >= 0 && var3 < S2BPacketChangeGameState.field_149142_a.length && S2BPacketChangeGameState.field_149142_a[var3] != null) {
         var2.func_146105_b(new ChatComponentTranslation(S2BPacketChangeGameState.field_149142_a[var3]));
      }

      if (var3 == 1) {
         this.field_147300_g.func_72912_H().func_76084_b(true);
         this.field_147300_g.func_72894_k(0.0F);
      } else if (var3 == 2) {
         this.field_147300_g.func_72912_H().func_76084_b(false);
         this.field_147300_g.func_72894_k(1.0F);
      } else if (var3 == 3) {
         this.field_147299_f.field_71442_b.func_78746_a(WorldSettings$GameType.func_77146_a(var5));
      } else if (var3 == 4) {
         this.field_147299_f.func_147108_a(new GuiWinGame());
      } else if (var3 == 5) {
         GameSettings var6 = this.field_147299_f.field_71474_y;
         if (var4 == 0.0F) {
            this.field_147299_f.func_147108_a(new GuiScreenDemo());
         } else if (var4 == 101.0F) {
            this.field_147299_f
               .field_71456_v
               .func_146158_b()
               .func_146227_a(
                  new ChatComponentTranslation(
                     "demo.help.movement",
                     GameSettings.func_74298_c(var6.field_74351_w.func_151463_i()),
                     GameSettings.func_74298_c(var6.field_74370_x.func_151463_i()),
                     GameSettings.func_74298_c(var6.field_74368_y.func_151463_i()),
                     GameSettings.func_74298_c(var6.field_74366_z.func_151463_i())
                  )
               );
         } else if (var4 == 102.0F) {
            this.field_147299_f
               .field_71456_v
               .func_146158_b()
               .func_146227_a(new ChatComponentTranslation("demo.help.jump", GameSettings.func_74298_c(var6.field_74314_A.func_151463_i())));
         } else if (var4 == 103.0F) {
            this.field_147299_f
               .field_71456_v
               .func_146158_b()
               .func_146227_a(new ChatComponentTranslation("demo.help.inventory", GameSettings.func_74298_c(var6.field_151445_Q.func_151463_i())));
         }
      } else if (var3 == 6) {
         this.field_147300_g
            .func_72980_b(
               var2.field_70165_t, var2.field_70163_u + (double)var2.func_70047_e(), var2.field_70161_v, "random.successful_hit", 0.18F, 0.45F, false
            );
      } else if (var3 == 7) {
         this.field_147300_g.func_72894_k(var4);
      } else if (var3 == 8) {
         this.field_147300_g.func_147442_i(var4);
      }
   }

   @Override
   public void func_147264_a(S34PacketMaps var1) {
      MapData var2 = ItemMap.func_150912_a(var1.func_149188_c(), this.field_147299_f.field_71441_e);
      var2.func_76192_a(var1.func_149187_d());
      this.field_147299_f.field_71460_t.func_147701_i().func_148246_a(var2);
   }

   @Override
   public void func_147277_a(S28PacketEffect var1) {
      if (var1.func_149244_c()) {
         this.field_147299_f
            .field_71441_e
            .func_82739_e(var1.func_149242_d(), var1.func_149240_f(), var1.func_149243_g(), var1.func_149239_h(), var1.func_149241_e());
      } else {
         this.field_147299_f
            .field_71441_e
            .func_72926_e(var1.func_149242_d(), var1.func_149240_f(), var1.func_149243_g(), var1.func_149239_h(), var1.func_149241_e());
      }
   }

   @Override
   public void func_147293_a(S37PacketStatistics var1) {
      boolean var2 = false;

      for(Entry var4 : var1.func_148974_c().entrySet()) {
         StatBase var5 = (StatBase)var4.getKey();
         int var6 = var4.getValue();
         if (var5.func_75967_d() && var6 > 0) {
            if (this.field_147308_k && this.field_147299_f.field_71439_g.func_146107_m().func_77444_a(var5) == 0) {
               Achievement var7 = (Achievement)var5;
               this.field_147299_f.field_71458_u.func_146256_a(var7);
               this.field_147299_f.func_152346_Z().func_152911_a(new MetadataAchievement(var7), 0L);
               if (var5 == AchievementList.field_76004_f) {
                  this.field_147299_f.field_71474_y.field_151441_H = false;
                  this.field_147299_f.field_71474_y.func_74303_b();
               }
            }

            var2 = true;
         }

         this.field_147299_f.field_71439_g.func_146107_m().func_150873_a(this.field_147299_f.field_71439_g, var5, var6);
      }

      if (!this.field_147308_k && !var2 && this.field_147299_f.field_71474_y.field_151441_H) {
         this.field_147299_f.field_71458_u.func_146255_b(AchievementList.field_76004_f);
      }

      this.field_147308_k = true;
      if (this.field_147299_f.field_71462_r instanceof IProgressMeter) {
         ((IProgressMeter)this.field_147299_f.field_71462_r).func_146509_g();
      }
   }

   @Override
   public void func_147260_a(S1DPacketEntityEffect var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149426_d());
      if (var2 instanceof EntityLivingBase) {
         PotionEffect var3 = new PotionEffect(var1.func_149427_e(), var1.func_149425_g(), var1.func_149428_f());
         var3.func_100012_b(var1.func_149429_c());
         ((EntityLivingBase)var2).func_70690_d(var3);
      }
   }

   @Override
   public void func_147262_a(S1EPacketRemoveEntityEffect var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149076_c());
      if (var2 instanceof EntityLivingBase) {
         ((EntityLivingBase)var2).func_70618_n(var1.func_149075_d());
      }
   }

   @Override
   public void func_147256_a(S38PacketPlayerListItem var1) {
      GuiPlayerInfo var2 = (GuiPlayerInfo)this.field_147310_i.get(var1.func_149122_c());
      if (var2 == null && var1.func_149121_d()) {
         var2 = new GuiPlayerInfo(var1.func_149122_c());
         this.field_147310_i.put(var1.func_149122_c(), var2);
         this.field_147303_b.add(var2);
      }

      if (var2 != null && !var1.func_149121_d()) {
         this.field_147310_i.remove(var1.func_149122_c());
         this.field_147303_b.remove(var2);
      }

      if (var2 != null && var1.func_149121_d()) {
         var2.field_78829_b = var1.func_149120_e();
      }
   }

   @Override
   public void func_147272_a(S00PacketKeepAlive var1) {
      this.func_147297_a(new C00PacketKeepAlive(var1.func_149134_c()));
   }

   @Override
   public void func_147232_a(EnumConnectionState var1, EnumConnectionState var2) {
      throw new IllegalStateException("Unexpected protocol change!");
   }

   @Override
   public void func_147270_a(S39PacketPlayerAbilities var1) {
      EntityClientPlayerMP var2 = this.field_147299_f.field_71439_g;
      var2.field_71075_bZ.field_75100_b = var1.func_149106_d();
      var2.field_71075_bZ.field_75098_d = var1.func_149103_f();
      var2.field_71075_bZ.field_75102_a = var1.func_149112_c();
      var2.field_71075_bZ.field_75101_c = var1.func_149105_e();
      var2.field_71075_bZ.func_75092_a(var1.func_149101_g());
      var2.field_71075_bZ.func_82877_b(var1.func_149107_h());
   }

   @Override
   public void func_147274_a(S3APacketTabComplete var1) {
      String[] var2 = var1.func_149630_c();
      if (this.field_147299_f.field_71462_r instanceof GuiChat) {
         GuiChat var3 = (GuiChat)this.field_147299_f.field_71462_r;
         var3.func_146406_a(var2);
      }
   }

   @Override
   public void func_147255_a(S29PacketSoundEffect var1) {
      this.field_147299_f
         .field_71441_e
         .func_72980_b(
            var1.func_149207_d(), var1.func_149211_e(), var1.func_149210_f(), var1.func_149212_c(), var1.func_149208_g(), var1.func_149209_h(), false
         );
   }

   @Override
   public void func_147240_a(S3FPacketCustomPayload var1) {
      if ("MC|TrList".equals(var1.func_149169_c())) {
         ByteBuf var2 = Unpooled.wrappedBuffer(var1.func_149168_d());

         try {
            int var3 = var2.readInt();
            GuiScreen var4 = this.field_147299_f.field_71462_r;
            if (var4 != null && var4 instanceof GuiMerchant && var3 == this.field_147299_f.field_71439_g.field_71070_bA.field_75152_c) {
               IMerchant var5 = ((GuiMerchant)var4).func_147035_g();
               MerchantRecipeList var6 = MerchantRecipeList.func_151390_b(new PacketBuffer(var2));
               var5.func_70930_a(var6);
            }
         } catch (IOException var10) {
            field_147301_d.error("Couldn't load trade info", var10);
         } finally {
            var2.release();
         }
      } else if ("MC|Brand".equals(var1.func_149169_c())) {
         this.field_147299_f.field_71439_g.func_142020_c(new String(var1.func_149168_d(), Charsets.UTF_8));
      } else if ("MC|RPack".equals(var1.func_149169_c())) {
         String var12 = new String(var1.func_149168_d(), Charsets.UTF_8);
         if (this.field_147299_f.func_147104_D() != null && this.field_147299_f.func_147104_D().func_152586_b() == ServerData$ServerResourceMode.ENABLED) {
            this.field_147299_f.func_110438_M().func_148526_a(var12);
         } else if (this.field_147299_f.func_147104_D() == null || this.field_147299_f.func_147104_D().func_152586_b() == ServerData$ServerResourceMode.PROMPT
            )
          {
            this.field_147299_f
               .func_147108_a(
                  new GuiYesNo(
                     new NetHandlerPlayClient$1(this, var12),
                     I18n.func_135052_a("multiplayer.texturePrompt.line1"),
                     I18n.func_135052_a("multiplayer.texturePrompt.line2"),
                     0
                  )
               );
         }
      }
   }

   @Override
   public void func_147291_a(S3BPacketScoreboardObjective var1) {
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      if (var1.func_149338_e() == 0) {
         ScoreObjective var3 = var2.func_96535_a(var1.func_149339_c(), IScoreObjectiveCriteria.field_96641_b);
         var3.func_96681_a(var1.func_149337_d());
      } else {
         ScoreObjective var4 = var2.func_96518_b(var1.func_149339_c());
         if (var1.func_149338_e() == 1) {
            var2.func_96519_k(var4);
         } else if (var1.func_149338_e() == 2) {
            var4.func_96681_a(var1.func_149337_d());
         }
      }
   }

   @Override
   public void func_147250_a(S3CPacketUpdateScore var1) {
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      ScoreObjective var3 = var2.func_96518_b(var1.func_149321_d());
      if (var1.func_149322_f() == 0) {
         Score var4 = var2.func_96529_a(var1.func_149324_c(), var3);
         var4.func_96647_c(var1.func_149323_e());
      } else if (var1.func_149322_f() == 1) {
         var2.func_96515_c(var1.func_149324_c());
      }
   }

   @Override
   public void func_147254_a(S3DPacketDisplayScoreboard var1) {
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      if (var1.func_149370_d().length() == 0) {
         var2.func_96530_a(var1.func_149371_c(), null);
      } else {
         ScoreObjective var3 = var2.func_96518_b(var1.func_149370_d());
         var2.func_96530_a(var1.func_149371_c(), var3);
      }
   }

   @Override
   public void func_147247_a(S3EPacketTeams var1) {
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      ScorePlayerTeam var3;
      if (var1.func_149307_h() == 0) {
         var3 = var2.func_96527_f(var1.func_149312_c());
      } else {
         var3 = var2.func_96508_e(var1.func_149312_c());
      }

      if (var1.func_149307_h() == 0 || var1.func_149307_h() == 2) {
         var3.func_96664_a(var1.func_149306_d());
         var3.func_96666_b(var1.func_149311_e());
         var3.func_96662_c(var1.func_149309_f());
         var3.func_98298_a(var1.func_149308_i());
      }

      if (var1.func_149307_h() == 0 || var1.func_149307_h() == 3) {
         for(String var5 : var1.func_149310_g()) {
            var2.func_151392_a(var5, var1.func_149312_c());
         }
      }

      if (var1.func_149307_h() == 4) {
         for(String var7 : var1.func_149310_g()) {
            var2.func_96512_b(var7, var3);
         }
      }

      if (var1.func_149307_h() == 1) {
         var2.func_96511_d(var3);
      }
   }

   @Override
   public void func_147289_a(S2APacketParticles var1) {
      if (var1.func_149222_k() == 0) {
         double var2 = (double)(var1.func_149227_j() * var1.func_149221_g());
         double var4 = (double)(var1.func_149227_j() * var1.func_149224_h());
         double var6 = (double)(var1.func_149227_j() * var1.func_149223_i());
         this.field_147300_g.func_72869_a(var1.func_149228_c(), var1.func_149220_d(), var1.func_149226_e(), var1.func_149225_f(), var2, var4, var6);
      } else {
         for(int var15 = 0; var15 < var1.func_149222_k(); ++var15) {
            double var3 = this.field_147306_l.nextGaussian() * (double)var1.func_149221_g();
            double var5 = this.field_147306_l.nextGaussian() * (double)var1.func_149224_h();
            double var7 = this.field_147306_l.nextGaussian() * (double)var1.func_149223_i();
            double var9 = this.field_147306_l.nextGaussian() * (double)var1.func_149227_j();
            double var11 = this.field_147306_l.nextGaussian() * (double)var1.func_149227_j();
            double var13 = this.field_147306_l.nextGaussian() * (double)var1.func_149227_j();
            this.field_147300_g
               .func_72869_a(var1.func_149228_c(), var1.func_149220_d() + var3, var1.func_149226_e() + var5, var1.func_149225_f() + var7, var9, var11, var13);
         }
      }
   }

   @Override
   public void func_147290_a(S20PacketEntityProperties var1) {
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149442_c());
      if (var2 != null) {
         if (!(var2 instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         } else {
            BaseAttributeMap var3 = ((EntityLivingBase)var2).func_110140_aT();

            for(S20PacketEntityProperties$Snapshot var5 : var1.func_149441_d()) {
               IAttributeInstance var6 = var3.func_111152_a(var5.func_151409_a());
               if (var6 == null) {
                  var6 = var3.func_111150_b(new RangedAttribute(var5.func_151409_a(), 0.0, 2.2250738585072014E-308, 1.7976931348623157E308));
               }

               var6.func_111128_a(var5.func_151410_b());
               var6.func_142049_d();

               for(AttributeModifier var8 : var5.func_151408_c()) {
                  var6.func_111121_a(var8);
               }
            }
         }
      }
   }

   public NetworkManager func_147298_b() {
      return this.field_147302_e;
   }
}
