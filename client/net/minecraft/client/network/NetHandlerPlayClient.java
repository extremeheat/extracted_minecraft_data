package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.MetadataAchievement;
import net.minecraft.client.stream.MetadataCombat;
import net.minecraft.client.stream.MetadataPlayerDeath;
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
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
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
import net.minecraft.entity.monster.EntityGuardian;
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
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
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
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
   private static final Logger field_147301_d = LogManager.getLogger();
   private final NetworkManager field_147302_e;
   private final GameProfile field_175107_d;
   private final GuiScreen field_147307_j;
   private Minecraft field_147299_f;
   private WorldClient field_147300_g;
   private boolean field_147309_h;
   private final Map<UUID, NetworkPlayerInfo> field_147310_i = Maps.newHashMap();
   public int field_147304_c = 20;
   private boolean field_147308_k = false;
   private final Random field_147306_l = new Random();

   public NetHandlerPlayClient(Minecraft var1, GuiScreen var2, NetworkManager var3, GameProfile var4) {
      super();
      this.field_147299_f = var1;
      this.field_147307_j = var2;
      this.field_147302_e = var3;
      this.field_175107_d = var4;
   }

   public void func_147296_c() {
      this.field_147300_g = null;
   }

   public void func_147282_a(S01PacketJoinGame var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71442_b = new PlayerControllerMP(this.field_147299_f, this);
      this.field_147300_g = new WorldClient(this, new WorldSettings(0L, var1.func_149198_e(), false, var1.func_149195_d(), var1.func_149196_i()), var1.func_149194_f(), var1.func_149192_g(), this.field_147299_f.field_71424_I);
      this.field_147299_f.field_71474_y.field_74318_M = var1.func_149192_g();
      this.field_147299_f.func_71403_a(this.field_147300_g);
      this.field_147299_f.field_71439_g.field_71093_bK = var1.func_149194_f();
      this.field_147299_f.func_147108_a(new GuiDownloadTerrain(this));
      this.field_147299_f.field_71439_g.func_145769_d(var1.func_149197_c());
      this.field_147304_c = var1.func_149193_h();
      this.field_147299_f.field_71439_g.func_175150_k(var1.func_179744_h());
      this.field_147299_f.field_71442_b.func_78746_a(var1.func_149198_e());
      this.field_147299_f.field_71474_y.func_82879_c();
      this.field_147302_e.func_179290_a(new C17PacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).func_180714_a(ClientBrandRetriever.getClientModName())));
   }

   public void func_147235_a(S0EPacketSpawnObject var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = (double)var1.func_148997_d() / 32.0D;
      double var4 = (double)var1.func_148998_e() / 32.0D;
      double var6 = (double)var1.func_148994_f() / 32.0D;
      Object var8 = null;
      if (var1.func_148993_l() == 10) {
         var8 = EntityMinecart.func_180458_a(this.field_147300_g, var2, var4, var6, EntityMinecart.EnumMinecartType.func_180038_a(var1.func_149009_m()));
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
         var8 = new EntityItemFrame(this.field_147300_g, new BlockPos(MathHelper.func_76128_c(var2), MathHelper.func_76128_c(var4), MathHelper.func_76128_c(var6)), EnumFacing.func_176731_b(var1.func_149009_m()));
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 77) {
         var8 = new EntityLeashKnot(this.field_147300_g, new BlockPos(MathHelper.func_76128_c(var2), MathHelper.func_76128_c(var4), MathHelper.func_76128_c(var6)));
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 65) {
         var8 = new EntityEnderPearl(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 72) {
         var8 = new EntityEnderEye(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 76) {
         var8 = new EntityFireworkRocket(this.field_147300_g, var2, var4, var6, (ItemStack)null);
      } else if (var1.func_148993_l() == 63) {
         var8 = new EntityLargeFireball(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 64) {
         var8 = new EntitySmallFireball(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 66) {
         var8 = new EntityWitherSkull(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
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
         var8 = new EntityTNTPrimed(this.field_147300_g, var2, var4, var6, (EntityLivingBase)null);
      } else if (var1.func_148993_l() == 78) {
         var8 = new EntityArmorStand(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 51) {
         var8 = new EntityEnderCrystal(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 2) {
         var8 = new EntityItem(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 70) {
         var8 = new EntityFallingBlock(this.field_147300_g, var2, var4, var6, Block.func_176220_d(var1.func_149009_m() & '\uffff'));
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
               if (var13 instanceof EntityLivingBase && var8 instanceof EntityArrow) {
                  ((EntityArrow)var8).field_70250_c = var13;
               }
            }

            ((Entity)var8).func_70016_h((double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         }
      }

   }

   public void func_147286_a(S11PacketSpawnExperienceOrb var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityXPOrb var2 = new EntityXPOrb(this.field_147300_g, (double)var1.func_148984_d() / 32.0D, (double)var1.func_148983_e() / 32.0D, (double)var1.func_148982_f() / 32.0D, var1.func_148986_g());
      var2.field_70118_ct = var1.func_148984_d();
      var2.field_70117_cu = var1.func_148983_e();
      var2.field_70116_cv = var1.func_148982_f();
      var2.field_70177_z = 0.0F;
      var2.field_70125_A = 0.0F;
      var2.func_145769_d(var1.func_148985_c());
      this.field_147300_g.func_73027_a(var1.func_148985_c(), var2);
   }

   public void func_147292_a(S2CPacketSpawnGlobalEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = (double)var1.func_149051_d() / 32.0D;
      double var4 = (double)var1.func_149050_e() / 32.0D;
      double var6 = (double)var1.func_149049_f() / 32.0D;
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

   public void func_147288_a(S10PacketSpawnPainting var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPainting var2 = new EntityPainting(this.field_147300_g, var1.func_179837_b(), var1.func_179836_c(), var1.func_148961_h());
      this.field_147300_g.func_73027_a(var1.func_148965_c(), var2);
   }

   public void func_147244_a(S12PacketEntityVelocity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149412_c());
      if (var2 != null) {
         var2.func_70016_h((double)var1.func_149411_d() / 8000.0D, (double)var1.func_149410_e() / 8000.0D, (double)var1.func_149409_f() / 8000.0D);
      }
   }

   public void func_147284_a(S1CPacketEntityMetadata var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149375_d());
      if (var2 != null && var1.func_149376_c() != null) {
         var2.func_70096_w().func_75687_a(var1.func_149376_c());
      }

   }

   public void func_147237_a(S0CPacketSpawnPlayer var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = (double)var1.func_148942_f() / 32.0D;
      double var4 = (double)var1.func_148949_g() / 32.0D;
      double var6 = (double)var1.func_148946_h() / 32.0D;
      float var8 = (float)(var1.func_148941_i() * 360) / 256.0F;
      float var9 = (float)(var1.func_148945_j() * 360) / 256.0F;
      EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(this.field_147299_f.field_71441_e, this.func_175102_a(var1.func_179819_c()).func_178845_a());
      var10.field_70169_q = var10.field_70142_S = (double)(var10.field_70118_ct = var1.func_148942_f());
      var10.field_70167_r = var10.field_70137_T = (double)(var10.field_70117_cu = var1.func_148949_g());
      var10.field_70166_s = var10.field_70136_U = (double)(var10.field_70116_cv = var1.func_148946_h());
      int var11 = var1.func_148947_k();
      if (var11 == 0) {
         var10.field_71071_by.field_70462_a[var10.field_71071_by.field_70461_c] = null;
      } else {
         var10.field_71071_by.field_70462_a[var10.field_71071_by.field_70461_c] = new ItemStack(Item.func_150899_d(var11), 1, 0);
      }

      var10.func_70080_a(var2, var4, var6, var8, var9);
      this.field_147300_g.func_73027_a(var1.func_148943_d(), var10);
      List var12 = var1.func_148944_c();
      if (var12 != null) {
         var10.func_70096_w().func_75687_a(var12);
      }

   }

   public void func_147275_a(S18PacketEntityTeleport var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149451_c());
      if (var2 != null) {
         var2.field_70118_ct = var1.func_149449_d();
         var2.field_70117_cu = var1.func_149448_e();
         var2.field_70116_cv = var1.func_149446_f();
         double var3 = (double)var2.field_70118_ct / 32.0D;
         double var5 = (double)var2.field_70117_cu / 32.0D;
         double var7 = (double)var2.field_70116_cv / 32.0D;
         float var9 = (float)(var1.func_149450_g() * 360) / 256.0F;
         float var10 = (float)(var1.func_149447_h() * 360) / 256.0F;
         if (Math.abs(var2.field_70165_t - var3) < 0.03125D && Math.abs(var2.field_70163_u - var5) < 0.015625D && Math.abs(var2.field_70161_v - var7) < 0.03125D) {
            var2.func_180426_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var9, var10, 3, true);
         } else {
            var2.func_180426_a(var3, var5, var7, var9, var10, 3, true);
         }

         var2.field_70122_E = var1.func_179697_g();
      }
   }

   public void func_147257_a(S09PacketHeldItemChange var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_149385_c() >= 0 && var1.func_149385_c() < InventoryPlayer.func_70451_h()) {
         this.field_147299_f.field_71439_g.field_71071_by.field_70461_c = var1.func_149385_c();
      }

   }

   public void func_147259_a(S14PacketEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_149065_a(this.field_147300_g);
      if (var2 != null) {
         var2.field_70118_ct += var1.func_149062_c();
         var2.field_70117_cu += var1.func_149061_d();
         var2.field_70116_cv += var1.func_149064_e();
         double var3 = (double)var2.field_70118_ct / 32.0D;
         double var5 = (double)var2.field_70117_cu / 32.0D;
         double var7 = (double)var2.field_70116_cv / 32.0D;
         float var9 = var1.func_149060_h() ? (float)(var1.func_149066_f() * 360) / 256.0F : var2.field_70177_z;
         float var10 = var1.func_149060_h() ? (float)(var1.func_149063_g() * 360) / 256.0F : var2.field_70125_A;
         var2.func_180426_a(var3, var5, var7, var9, var10, 3, false);
         var2.field_70122_E = var1.func_179742_g();
      }
   }

   public void func_147267_a(S19PacketEntityHeadLook var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_149381_a(this.field_147300_g);
      if (var2 != null) {
         float var3 = (float)(var1.func_149380_c() * 360) / 256.0F;
         var2.func_70034_d(var3);
      }
   }

   public void func_147238_a(S13PacketDestroyEntities var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);

      for(int var2 = 0; var2 < var1.func_149098_c().length; ++var2) {
         this.field_147300_g.func_73028_b(var1.func_149098_c()[var2]);
      }

   }

   public void func_147258_a(S08PacketPlayerPosLook var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      double var3 = var1.func_148932_c();
      double var5 = var1.func_148928_d();
      double var7 = var1.func_148933_e();
      float var9 = var1.func_148931_f();
      float var10 = var1.func_148930_g();
      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X)) {
         var3 += var2.field_70165_t;
      } else {
         var2.field_70159_w = 0.0D;
      }

      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y)) {
         var5 += var2.field_70163_u;
      } else {
         var2.field_70181_x = 0.0D;
      }

      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Z)) {
         var7 += var2.field_70161_v;
      } else {
         var2.field_70179_y = 0.0D;
      }

      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.X_ROT)) {
         var10 += var2.field_70125_A;
      }

      if (var1.func_179834_f().contains(S08PacketPlayerPosLook.EnumFlags.Y_ROT)) {
         var9 += var2.field_70177_z;
      }

      var2.func_70080_a(var3, var5, var7, var9, var10);
      this.field_147302_e.func_179290_a(new C03PacketPlayer.C06PacketPlayerPosLook(var2.field_70165_t, var2.func_174813_aQ().field_72338_b, var2.field_70161_v, var2.field_70177_z, var2.field_70125_A, false));
      if (!this.field_147309_h) {
         this.field_147299_f.field_71439_g.field_70169_q = this.field_147299_f.field_71439_g.field_70165_t;
         this.field_147299_f.field_71439_g.field_70167_r = this.field_147299_f.field_71439_g.field_70163_u;
         this.field_147299_f.field_71439_g.field_70166_s = this.field_147299_f.field_71439_g.field_70161_v;
         this.field_147309_h = true;
         this.field_147299_f.func_147108_a((GuiScreen)null);
      }

   }

   public void func_147287_a(S22PacketMultiBlockChange var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      S22PacketMultiBlockChange.BlockUpdateData[] var2 = var1.func_179844_a();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         S22PacketMultiBlockChange.BlockUpdateData var5 = var2[var4];
         this.field_147300_g.func_180503_b(var5.func_180090_a(), var5.func_180088_c());
      }

   }

   public void func_147263_a(S21PacketChunkData var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_149274_i()) {
         if (var1.func_149276_g() == 0) {
            this.field_147300_g.func_73025_a(var1.func_149273_e(), var1.func_149271_f(), false);
            return;
         }

         this.field_147300_g.func_73025_a(var1.func_149273_e(), var1.func_149271_f(), true);
      }

      this.field_147300_g.func_73031_a(var1.func_149273_e() << 4, 0, var1.func_149271_f() << 4, (var1.func_149273_e() << 4) + 15, 256, (var1.func_149271_f() << 4) + 15);
      Chunk var2 = this.field_147300_g.func_72964_e(var1.func_149273_e(), var1.func_149271_f());
      var2.func_177439_a(var1.func_149272_d(), var1.func_149276_g(), var1.func_149274_i());
      this.field_147300_g.func_147458_c(var1.func_149273_e() << 4, 0, var1.func_149271_f() << 4, (var1.func_149273_e() << 4) + 15, 256, (var1.func_149271_f() << 4) + 15);
      if (!var1.func_149274_i() || !(this.field_147300_g.field_73011_w instanceof WorldProviderSurface)) {
         var2.func_76613_n();
      }

   }

   public void func_147234_a(S23PacketBlockChange var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147300_g.func_180503_b(var1.func_179827_b(), var1.func_180728_a());
   }

   public void func_147253_a(S40PacketDisconnect var1) {
      this.field_147302_e.func_150718_a(var1.func_149165_c());
   }

   public void func_147231_a(IChatComponent var1) {
      this.field_147299_f.func_71403_a((WorldClient)null);
      if (this.field_147307_j != null) {
         if (this.field_147307_j instanceof GuiScreenRealmsProxy) {
            this.field_147299_f.func_147108_a((new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.field_147307_j).func_154321_a(), "disconnect.lost", var1)).getProxy());
         } else {
            this.field_147299_f.func_147108_a(new GuiDisconnected(this.field_147307_j, "disconnect.lost", var1));
         }
      } else {
         this.field_147299_f.func_147108_a(new GuiDisconnected(new GuiMultiplayer(new GuiMainMenu()), "disconnect.lost", var1));
      }

   }

   public void func_147297_a(Packet var1) {
      this.field_147302_e.func_179290_a(var1);
   }

   public void func_147246_a(S0DPacketCollectItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149354_c());
      Object var3 = (EntityLivingBase)this.field_147300_g.func_73045_a(var1.func_149353_d());
      if (var3 == null) {
         var3 = this.field_147299_f.field_71439_g;
      }

      if (var2 != null) {
         if (var2 instanceof EntityXPOrb) {
            this.field_147300_g.func_72956_a(var2, "random.orb", 0.2F, ((this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else {
            this.field_147300_g.func_72956_a(var2, "random.pop", 0.2F, ((this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.field_147299_f.field_71452_i.func_78873_a(new EntityPickupFX(this.field_147300_g, var2, (Entity)var3, 0.5F));
         this.field_147300_g.func_73028_b(var1.func_149354_c());
      }

   }

   public void func_147251_a(S02PacketChat var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_179841_c() == 2) {
         this.field_147299_f.field_71456_v.func_175188_a(var1.func_148915_c(), false);
      } else {
         this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(var1.func_148915_c());
      }

   }

   public void func_147279_a(S0BPacketAnimation var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
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
            this.field_147299_f.field_71452_i.func_178926_a(var2, EnumParticleTypes.CRIT);
         } else if (var1.func_148977_d() == 5) {
            this.field_147299_f.field_71452_i.func_178926_a(var2, EnumParticleTypes.CRIT_MAGIC);
         }

      }
   }

   public void func_147278_a(S0APacketUseBed var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      var1.func_149091_a(this.field_147300_g).func_180469_a(var1.func_179798_a());
   }

   public void func_147281_a(S0FPacketSpawnMob var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = (double)var1.func_149023_f() / 32.0D;
      double var4 = (double)var1.func_149034_g() / 32.0D;
      double var6 = (double)var1.func_149029_h() / 32.0D;
      float var8 = (float)(var1.func_149028_l() * 360) / 256.0F;
      float var9 = (float)(var1.func_149030_m() * 360) / 256.0F;
      EntityLivingBase var10 = (EntityLivingBase)EntityList.func_75616_a(var1.func_149025_e(), this.field_147299_f.field_71441_e);
      var10.field_70118_ct = var1.func_149023_f();
      var10.field_70117_cu = var1.func_149034_g();
      var10.field_70116_cv = var1.func_149029_h();
      var10.field_70761_aq = var10.field_70759_as = (float)(var1.func_149032_n() * 360) / 256.0F;
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

   public void func_147285_a(S03PacketTimeUpdate var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_82738_a(var1.func_149366_c());
      this.field_147299_f.field_71441_e.func_72877_b(var1.func_149365_d());
   }

   public void func_147271_a(S05PacketSpawnPosition var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71439_g.func_180473_a(var1.func_179800_a(), true);
      this.field_147299_f.field_71441_e.func_72912_H().func_176143_a(var1.func_179800_a());
   }

   public void func_147243_a(S1BPacketEntityAttach var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
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
            this.field_147299_f.field_71456_v.func_110326_a(I18n.func_135052_a("mount.onboard", GameSettings.func_74298_c(var5.field_74311_E.func_151463_i())), false);
         }
      } else if (var1.func_149404_c() == 1 && var2 instanceof EntityLiving) {
         if (var3 != null) {
            ((EntityLiving)var2).func_110162_b(var3, false);
         } else {
            ((EntityLiving)var2).func_110160_i(false, false);
         }
      }

   }

   public void func_147236_a(S19PacketEntityStatus var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_149161_a(this.field_147300_g);
      if (var2 != null) {
         if (var1.func_149160_c() == 21) {
            this.field_147299_f.func_147118_V().func_147682_a(new GuardianSound((EntityGuardian)var2));
         } else {
            var2.func_70103_a(var1.func_149160_c());
         }
      }

   }

   public void func_147249_a(S06PacketUpdateHealth var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71439_g.func_71150_b(var1.func_149332_c());
      this.field_147299_f.field_71439_g.func_71024_bL().func_75114_a(var1.func_149330_d());
      this.field_147299_f.field_71439_g.func_71024_bL().func_75119_b(var1.func_149331_e());
   }

   public void func_147295_a(S1FPacketSetExperience var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71439_g.func_71152_a(var1.func_149397_c(), var1.func_149396_d(), var1.func_149395_e());
   }

   public void func_147280_a(S07PacketRespawn var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_149082_c() != this.field_147299_f.field_71439_g.field_71093_bK) {
         this.field_147309_h = false;
         Scoreboard var2 = this.field_147300_g.func_96441_U();
         this.field_147300_g = new WorldClient(this, new WorldSettings(0L, var1.func_149083_e(), false, this.field_147299_f.field_71441_e.func_72912_H().func_76093_s(), var1.func_149080_f()), var1.func_149082_c(), var1.func_149081_d(), this.field_147299_f.field_71424_I);
         this.field_147300_g.func_96443_a(var2);
         this.field_147299_f.func_71403_a(this.field_147300_g);
         this.field_147299_f.field_71439_g.field_71093_bK = var1.func_149082_c();
         this.field_147299_f.func_147108_a(new GuiDownloadTerrain(this));
      }

      this.field_147299_f.func_71354_a(var1.func_149082_c());
      this.field_147299_f.field_71442_b.func_78746_a(var1.func_149083_e());
   }

   public void func_147283_a(S27PacketExplosion var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Explosion var2 = new Explosion(this.field_147299_f.field_71441_e, (Entity)null, var1.func_149148_f(), var1.func_149143_g(), var1.func_149145_h(), var1.func_149146_i(), var1.func_149150_j());
      var2.func_77279_a(true);
      EntityPlayerSP var10000 = this.field_147299_f.field_71439_g;
      var10000.field_70159_w += (double)var1.func_149149_c();
      var10000 = this.field_147299_f.field_71439_g;
      var10000.field_70181_x += (double)var1.func_149144_d();
      var10000 = this.field_147299_f.field_71439_g;
      var10000.field_70179_y += (double)var1.func_149147_e();
   }

   public void func_147265_a(S2DPacketOpenWindow var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      if ("minecraft:container".equals(var1.func_148902_e())) {
         var2.func_71007_a(new InventoryBasic(var1.func_179840_c(), var1.func_148898_f()));
         var2.field_71070_bA.field_75152_c = var1.func_148901_c();
      } else if ("minecraft:villager".equals(var1.func_148902_e())) {
         var2.func_180472_a(new NpcMerchant(var2, var1.func_179840_c()));
         var2.field_71070_bA.field_75152_c = var1.func_148901_c();
      } else if ("EntityHorse".equals(var1.func_148902_e())) {
         Entity var3 = this.field_147300_g.func_73045_a(var1.func_148897_h());
         if (var3 instanceof EntityHorse) {
            var2.func_110298_a((EntityHorse)var3, new AnimalChest(var1.func_179840_c(), var1.func_148898_f()));
            var2.field_71070_bA.field_75152_c = var1.func_148901_c();
         }
      } else if (!var1.func_148900_g()) {
         var2.func_180468_a(new LocalBlockIntercommunication(var1.func_148902_e(), var1.func_179840_c()));
         var2.field_71070_bA.field_75152_c = var1.func_148901_c();
      } else {
         ContainerLocalMenu var4 = new ContainerLocalMenu(var1.func_148902_e(), var1.func_179840_c(), var1.func_148898_f());
         var2.func_71007_a(var4);
         var2.field_71070_bA.field_75152_c = var1.func_148901_c();
      }

   }

   public void func_147266_a(S2FPacketSetSlot var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
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

   public void func_147239_a(S32PacketConfirmTransaction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Container var2 = null;
      EntityPlayerSP var3 = this.field_147299_f.field_71439_g;
      if (var1.func_148889_c() == 0) {
         var2 = var3.field_71069_bz;
      } else if (var1.func_148889_c() == var3.field_71070_bA.field_75152_c) {
         var2 = var3.field_71070_bA;
      }

      if (var2 != null && !var1.func_148888_e()) {
         this.func_147297_a(new C0FPacketConfirmTransaction(var1.func_148889_c(), var1.func_148890_d(), true));
      }

   }

   public void func_147241_a(S30PacketWindowItems var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      if (var1.func_148911_c() == 0) {
         var2.field_71069_bz.func_75131_a(var1.func_148910_d());
      } else if (var1.func_148911_c() == var2.field_71070_bA.field_75152_c) {
         var2.field_71070_bA.func_75131_a(var1.func_148910_d());
      }

   }

   public void func_147268_a(S36PacketSignEditorOpen var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Object var2 = this.field_147300_g.func_175625_s(var1.func_179777_a());
      if (!(var2 instanceof TileEntitySign)) {
         var2 = new TileEntitySign();
         ((TileEntity)var2).func_145834_a(this.field_147300_g);
         ((TileEntity)var2).func_174878_a(var1.func_179777_a());
      }

      this.field_147299_f.field_71439_g.func_175141_a((TileEntitySign)var2);
   }

   public void func_147248_a(S33PacketUpdateSign var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      boolean var2 = false;
      if (this.field_147299_f.field_71441_e.func_175667_e(var1.func_179704_a())) {
         TileEntity var3 = this.field_147299_f.field_71441_e.func_175625_s(var1.func_179704_a());
         if (var3 instanceof TileEntitySign) {
            TileEntitySign var4 = (TileEntitySign)var3;
            if (var4.func_145914_a()) {
               System.arraycopy(var1.func_180753_b(), 0, var4.field_145915_a, 0, 4);
               var4.func_70296_d();
            }

            var2 = true;
         }
      }

      if (!var2 && this.field_147299_f.field_71439_g != null) {
         this.field_147299_f.field_71439_g.func_145747_a(new ChatComponentText("Unable to locate sign at " + var1.func_179704_a().func_177958_n() + ", " + var1.func_179704_a().func_177956_o() + ", " + var1.func_179704_a().func_177952_p()));
      }

   }

   public void func_147273_a(S35PacketUpdateTileEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (this.field_147299_f.field_71441_e.func_175667_e(var1.func_179823_a())) {
         TileEntity var2 = this.field_147299_f.field_71441_e.func_175625_s(var1.func_179823_a());
         int var3 = var1.func_148853_f();
         if (var3 == 1 && var2 instanceof TileEntityMobSpawner || var3 == 2 && var2 instanceof TileEntityCommandBlock || var3 == 3 && var2 instanceof TileEntityBeacon || var3 == 4 && var2 instanceof TileEntitySkull || var3 == 5 && var2 instanceof TileEntityFlowerPot || var3 == 6 && var2 instanceof TileEntityBanner) {
            var2.func_145839_a(var1.func_148857_g());
         }
      }

   }

   public void func_147245_a(S31PacketWindowProperty var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      if (var2.field_71070_bA != null && var2.field_71070_bA.field_75152_c == var1.func_149182_c()) {
         var2.field_71070_bA.func_75137_b(var1.func_149181_d(), var1.func_149180_e());
      }

   }

   public void func_147242_a(S04PacketEntityEquipment var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149389_d());
      if (var2 != null) {
         var2.func_70062_b(var1.func_149388_e(), var1.func_149390_c());
      }

   }

   public void func_147276_a(S2EPacketCloseWindow var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71439_g.func_175159_q();
   }

   public void func_147261_a(S24PacketBlockAction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_175641_c(var1.func_179825_a(), var1.func_148868_c(), var1.func_148869_g(), var1.func_148864_h());
   }

   public void func_147294_a(S25PacketBlockBreakAnim var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_175715_c(var1.func_148845_c(), var1.func_179821_b(), var1.func_148846_g());
   }

   public void func_147269_a(S26PacketMapChunkBulk var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);

      for(int var2 = 0; var2 < var1.func_149254_d(); ++var2) {
         int var3 = var1.func_149255_a(var2);
         int var4 = var1.func_149253_b(var2);
         this.field_147300_g.func_73025_a(var3, var4, true);
         this.field_147300_g.func_73031_a(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
         Chunk var5 = this.field_147300_g.func_72964_e(var3, var4);
         var5.func_177439_a(var1.func_149256_c(var2), var1.func_179754_d(var2), true);
         this.field_147300_g.func_147458_c(var3 << 4, 0, var4 << 4, (var3 << 4) + 15, 256, (var4 << 4) + 15);
         if (!(this.field_147300_g.field_73011_w instanceof WorldProviderSurface)) {
            var5.func_76613_n();
         }
      }

   }

   public void func_147252_a(S2BPacketChangeGameState var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      int var3 = var1.func_149138_c();
      float var4 = var1.func_149137_d();
      int var5 = MathHelper.func_76141_d(var4 + 0.5F);
      if (var3 >= 0 && var3 < S2BPacketChangeGameState.field_149142_a.length && S2BPacketChangeGameState.field_149142_a[var3] != null) {
         var2.func_146105_b(new ChatComponentTranslation(S2BPacketChangeGameState.field_149142_a[var3], new Object[0]));
      }

      if (var3 == 1) {
         this.field_147300_g.func_72912_H().func_76084_b(true);
         this.field_147300_g.func_72894_k(0.0F);
      } else if (var3 == 2) {
         this.field_147300_g.func_72912_H().func_76084_b(false);
         this.field_147300_g.func_72894_k(1.0F);
      } else if (var3 == 3) {
         this.field_147299_f.field_71442_b.func_78746_a(WorldSettings.GameType.func_77146_a(var5));
      } else if (var3 == 4) {
         this.field_147299_f.func_147108_a(new GuiWinGame());
      } else if (var3 == 5) {
         GameSettings var6 = this.field_147299_f.field_71474_y;
         if (var4 == 0.0F) {
            this.field_147299_f.func_147108_a(new GuiScreenDemo());
         } else if (var4 == 101.0F) {
            this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new ChatComponentTranslation("demo.help.movement", new Object[]{GameSettings.func_74298_c(var6.field_74351_w.func_151463_i()), GameSettings.func_74298_c(var6.field_74370_x.func_151463_i()), GameSettings.func_74298_c(var6.field_74368_y.func_151463_i()), GameSettings.func_74298_c(var6.field_74366_z.func_151463_i())}));
         } else if (var4 == 102.0F) {
            this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new ChatComponentTranslation("demo.help.jump", new Object[]{GameSettings.func_74298_c(var6.field_74314_A.func_151463_i())}));
         } else if (var4 == 103.0F) {
            this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new ChatComponentTranslation("demo.help.inventory", new Object[]{GameSettings.func_74298_c(var6.field_151445_Q.func_151463_i())}));
         }
      } else if (var3 == 6) {
         this.field_147300_g.func_72980_b(var2.field_70165_t, var2.field_70163_u + (double)var2.func_70047_e(), var2.field_70161_v, "random.successful_hit", 0.18F, 0.45F, false);
      } else if (var3 == 7) {
         this.field_147300_g.func_72894_k(var4);
      } else if (var3 == 8) {
         this.field_147300_g.func_147442_i(var4);
      } else if (var3 == 10) {
         this.field_147300_g.func_175688_a(EnumParticleTypes.MOB_APPEARANCE, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, 0.0D, 0.0D, 0.0D, new int[0]);
         this.field_147300_g.func_72980_b(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, "mob.guardian.curse", 1.0F, 1.0F, false);
      }

   }

   public void func_147264_a(S34PacketMaps var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      MapData var2 = ItemMap.func_150912_a(var1.func_149188_c(), this.field_147299_f.field_71441_e);
      var1.func_179734_a(var2);
      this.field_147299_f.field_71460_t.func_147701_i().func_148246_a(var2);
   }

   public void func_147277_a(S28PacketEffect var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_149244_c()) {
         this.field_147299_f.field_71441_e.func_175669_a(var1.func_149242_d(), var1.func_179746_d(), var1.func_149241_e());
      } else {
         this.field_147299_f.field_71441_e.func_175718_b(var1.func_149242_d(), var1.func_179746_d(), var1.func_149241_e());
      }

   }

   public void func_147293_a(S37PacketStatistics var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      boolean var2 = false;

      StatBase var5;
      int var6;
      for(Iterator var3 = var1.func_148974_c().entrySet().iterator(); var3.hasNext(); this.field_147299_f.field_71439_g.func_146107_m().func_150873_a(this.field_147299_f.field_71439_g, var5, var6)) {
         Entry var4 = (Entry)var3.next();
         var5 = (StatBase)var4.getKey();
         var6 = (Integer)var4.getValue();
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
      }

      if (!this.field_147308_k && !var2 && this.field_147299_f.field_71474_y.field_151441_H) {
         this.field_147299_f.field_71458_u.func_146255_b(AchievementList.field_76004_f);
      }

      this.field_147308_k = true;
      if (this.field_147299_f.field_71462_r instanceof IProgressMeter) {
         ((IProgressMeter)this.field_147299_f.field_71462_r).func_146509_g();
      }

   }

   public void func_147260_a(S1DPacketEntityEffect var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149426_d());
      if (var2 instanceof EntityLivingBase) {
         PotionEffect var3 = new PotionEffect(var1.func_149427_e(), var1.func_180755_e(), var1.func_149428_f(), false, var1.func_179707_f());
         var3.func_100012_b(var1.func_149429_c());
         ((EntityLivingBase)var2).func_70690_d(var3);
      }
   }

   public void func_175098_a(S42PacketCombatEvent var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.field_179775_c);
      EntityLivingBase var3 = var2 instanceof EntityLivingBase ? (EntityLivingBase)var2 : null;
      if (var1.field_179776_a == S42PacketCombatEvent.Event.END_COMBAT) {
         long var4 = (long)(1000 * var1.field_179772_d / 20);
         MetadataCombat var6 = new MetadataCombat(this.field_147299_f.field_71439_g, var3);
         this.field_147299_f.func_152346_Z().func_176026_a(var6, 0L - var4, 0L);
      } else if (var1.field_179776_a == S42PacketCombatEvent.Event.ENTITY_DIED) {
         Entity var7 = this.field_147300_g.func_73045_a(var1.field_179774_b);
         if (var7 instanceof EntityPlayer) {
            MetadataPlayerDeath var5 = new MetadataPlayerDeath((EntityPlayer)var7, var3);
            var5.func_152807_a(var1.field_179773_e);
            this.field_147299_f.func_152346_Z().func_152911_a(var5, 0L);
         }
      }

   }

   public void func_175101_a(S41PacketServerDifficulty var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_72912_H().func_176144_a(var1.func_179831_b());
      this.field_147299_f.field_71441_e.func_72912_H().func_180783_e(var1.func_179830_a());
   }

   public void func_175094_a(S43PacketCamera var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_179780_a(this.field_147300_g);
      if (var2 != null) {
         this.field_147299_f.func_175607_a(var2);
      }

   }

   public void func_175093_a(S44PacketWorldBorder var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      var1.func_179788_a(this.field_147300_g.func_175723_af());
   }

   public void func_175099_a(S45PacketTitle var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      S45PacketTitle.Type var2 = var1.func_179807_a();
      String var3 = null;
      String var4 = null;
      String var5 = var1.func_179805_b() != null ? var1.func_179805_b().func_150254_d() : "";
      switch(var2) {
      case TITLE:
         var3 = var5;
         break;
      case SUBTITLE:
         var4 = var5;
         break;
      case RESET:
         this.field_147299_f.field_71456_v.func_175178_a("", "", -1, -1, -1);
         this.field_147299_f.field_71456_v.func_175177_a();
         return;
      }

      this.field_147299_f.field_71456_v.func_175178_a(var3, var4, var1.func_179806_c(), var1.func_179804_d(), var1.func_179803_e());
   }

   public void func_175100_a(S46PacketSetCompressionLevel var1) {
      if (!this.field_147302_e.func_150731_c()) {
         this.field_147302_e.func_179289_a(var1.func_179760_a());
      }

   }

   public void func_175096_a(S47PacketPlayerListHeaderFooter var1) {
      this.field_147299_f.field_71456_v.func_175181_h().func_175244_b(var1.func_179700_a().func_150254_d().length() == 0 ? null : var1.func_179700_a());
      this.field_147299_f.field_71456_v.func_175181_h().func_175248_a(var1.func_179701_b().func_150254_d().length() == 0 ? null : var1.func_179701_b());
   }

   public void func_147262_a(S1EPacketRemoveEntityEffect var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149076_c());
      if (var2 instanceof EntityLivingBase) {
         ((EntityLivingBase)var2).func_70618_n(var1.func_149075_d());
      }

   }

   public void func_147256_a(S38PacketPlayerListItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Iterator var2 = var1.func_179767_a().iterator();

      while(var2.hasNext()) {
         S38PacketPlayerListItem.AddPlayerData var3 = (S38PacketPlayerListItem.AddPlayerData)var2.next();
         if (var1.func_179768_b() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
            this.field_147310_i.remove(var3.func_179962_a().getId());
         } else {
            NetworkPlayerInfo var4 = (NetworkPlayerInfo)this.field_147310_i.get(var3.func_179962_a().getId());
            if (var1.func_179768_b() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
               var4 = new NetworkPlayerInfo(var3);
               this.field_147310_i.put(var4.func_178845_a().getId(), var4);
            }

            if (var4 != null) {
               switch(var1.func_179768_b()) {
               case ADD_PLAYER:
                  var4.func_178839_a(var3.func_179960_c());
                  var4.func_178838_a(var3.func_179963_b());
                  break;
               case UPDATE_GAME_MODE:
                  var4.func_178839_a(var3.func_179960_c());
                  break;
               case UPDATE_LATENCY:
                  var4.func_178838_a(var3.func_179963_b());
                  break;
               case UPDATE_DISPLAY_NAME:
                  var4.func_178859_a(var3.func_179961_d());
               }
            }
         }
      }

   }

   public void func_147272_a(S00PacketKeepAlive var1) {
      this.func_147297_a(new C00PacketKeepAlive(var1.func_149134_c()));
   }

   public void func_147270_a(S39PacketPlayerAbilities var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      var2.field_71075_bZ.field_75100_b = var1.func_149106_d();
      var2.field_71075_bZ.field_75098_d = var1.func_149103_f();
      var2.field_71075_bZ.field_75102_a = var1.func_149112_c();
      var2.field_71075_bZ.field_75101_c = var1.func_149105_e();
      var2.field_71075_bZ.func_75092_a(var1.func_149101_g());
      var2.field_71075_bZ.func_82877_b(var1.func_149107_h());
   }

   public void func_147274_a(S3APacketTabComplete var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      String[] var2 = var1.func_149630_c();
      if (this.field_147299_f.field_71462_r instanceof GuiChat) {
         GuiChat var3 = (GuiChat)this.field_147299_f.field_71462_r;
         var3.func_146406_a(var2);
      }

   }

   public void func_147255_a(S29PacketSoundEffect var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_72980_b(var1.func_149207_d(), var1.func_149211_e(), var1.func_149210_f(), var1.func_149212_c(), var1.func_149208_g(), var1.func_149209_h(), false);
   }

   public void func_175095_a(S48PacketResourcePackSend var1) {
      final String var2 = var1.func_179783_a();
      final String var3 = var1.func_179784_b();
      if (var2.startsWith("level://")) {
         String var4 = var2.substring("level://".length());
         File var5 = new File(this.field_147299_f.field_71412_D, "saves");
         File var6 = new File(var5, var4);
         if (var6.isFile()) {
            this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.ACCEPTED));
            Futures.addCallback(this.field_147299_f.func_110438_M().func_177319_a(var6), new FutureCallback<Object>() {
               public void onSuccess(Object var1) {
                  NetHandlerPlayClient.this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
               }

               public void onFailure(Throwable var1) {
                  NetHandlerPlayClient.this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
               }
            });
         } else {
            this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         }

      } else {
         if (this.field_147299_f.func_147104_D() != null && this.field_147299_f.func_147104_D().func_152586_b() == ServerData.ServerResourceMode.ENABLED) {
            this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.ACCEPTED));
            Futures.addCallback(this.field_147299_f.func_110438_M().func_180601_a(var2, var3), new FutureCallback<Object>() {
               public void onSuccess(Object var1) {
                  NetHandlerPlayClient.this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
               }

               public void onFailure(Throwable var1) {
                  NetHandlerPlayClient.this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
               }
            });
         } else if (this.field_147299_f.func_147104_D() != null && this.field_147299_f.func_147104_D().func_152586_b() != ServerData.ServerResourceMode.PROMPT) {
            this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.DECLINED));
         } else {
            this.field_147299_f.func_152344_a(new Runnable() {
               public void run() {
                  NetHandlerPlayClient.this.field_147299_f.func_147108_a(new GuiYesNo(new GuiYesNoCallback() {
                     public void func_73878_a(boolean var1, int var2x) {
                        NetHandlerPlayClient.this.field_147299_f = Minecraft.func_71410_x();
                        if (var1) {
                           if (NetHandlerPlayClient.this.field_147299_f.func_147104_D() != null) {
                              NetHandlerPlayClient.this.field_147299_f.func_147104_D().func_152584_a(ServerData.ServerResourceMode.ENABLED);
                           }

                           NetHandlerPlayClient.this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.ACCEPTED));
                           Futures.addCallback(NetHandlerPlayClient.this.field_147299_f.func_110438_M().func_180601_a(var2, var3), new FutureCallback<Object>() {
                              public void onSuccess(Object var1) {
                                 NetHandlerPlayClient.this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                              }

                              public void onFailure(Throwable var1) {
                                 NetHandlerPlayClient.this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                              }
                           });
                        } else {
                           if (NetHandlerPlayClient.this.field_147299_f.func_147104_D() != null) {
                              NetHandlerPlayClient.this.field_147299_f.func_147104_D().func_152584_a(ServerData.ServerResourceMode.DISABLED);
                           }

                           NetHandlerPlayClient.this.field_147302_e.func_179290_a(new C19PacketResourcePackStatus(var3, C19PacketResourcePackStatus.Action.DECLINED));
                        }

                        ServerList.func_147414_b(NetHandlerPlayClient.this.field_147299_f.func_147104_D());
                        NetHandlerPlayClient.this.field_147299_f.func_147108_a((GuiScreen)null);
                     }
                  }, I18n.func_135052_a("multiplayer.texturePrompt.line1"), I18n.func_135052_a("multiplayer.texturePrompt.line2"), 0));
               }
            });
         }

      }
   }

   public void func_175097_a(S49PacketUpdateEntityNBT var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_179764_a(this.field_147300_g);
      if (var2 != null) {
         var2.func_174834_g(var1.func_179763_a());
      }

   }

   public void func_147240_a(S3FPacketCustomPayload var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if ("MC|TrList".equals(var1.func_149169_c())) {
         PacketBuffer var2 = var1.func_180735_b();

         try {
            int var3 = var2.readInt();
            GuiScreen var4 = this.field_147299_f.field_71462_r;
            if (var4 != null && var4 instanceof GuiMerchant && var3 == this.field_147299_f.field_71439_g.field_71070_bA.field_75152_c) {
               IMerchant var5 = ((GuiMerchant)var4).func_147035_g();
               MerchantRecipeList var6 = MerchantRecipeList.func_151390_b(var2);
               var5.func_70930_a(var6);
            }
         } catch (IOException var10) {
            field_147301_d.error("Couldn't load trade info", var10);
         } finally {
            var2.release();
         }
      } else if ("MC|Brand".equals(var1.func_149169_c())) {
         this.field_147299_f.field_71439_g.func_175158_f(var1.func_180735_b().func_150789_c(32767));
      } else if ("MC|BOpen".equals(var1.func_149169_c())) {
         ItemStack var12 = this.field_147299_f.field_71439_g.func_71045_bC();
         if (var12 != null && var12.func_77973_b() == Items.field_151164_bB) {
            this.field_147299_f.func_147108_a(new GuiScreenBook(this.field_147299_f.field_71439_g, var12, false));
         }
      }

   }

   public void func_147291_a(S3BPacketScoreboardObjective var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      ScoreObjective var3;
      if (var1.func_149338_e() == 0) {
         var3 = var2.func_96535_a(var1.func_149339_c(), IScoreObjectiveCriteria.field_96641_b);
         var3.func_96681_a(var1.func_149337_d());
         var3.func_178767_a(var1.func_179817_d());
      } else {
         var3 = var2.func_96518_b(var1.func_149339_c());
         if (var1.func_149338_e() == 1) {
            var2.func_96519_k(var3);
         } else if (var1.func_149338_e() == 2) {
            var3.func_96681_a(var1.func_149337_d());
            var3.func_178767_a(var1.func_179817_d());
         }
      }

   }

   public void func_147250_a(S3CPacketUpdateScore var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      ScoreObjective var3 = var2.func_96518_b(var1.func_149321_d());
      if (var1.func_180751_d() == S3CPacketUpdateScore.Action.CHANGE) {
         Score var4 = var2.func_96529_a(var1.func_149324_c(), var3);
         var4.func_96647_c(var1.func_149323_e());
      } else if (var1.func_180751_d() == S3CPacketUpdateScore.Action.REMOVE) {
         if (StringUtils.func_151246_b(var1.func_149321_d())) {
            var2.func_178822_d(var1.func_149324_c(), (ScoreObjective)null);
         } else if (var3 != null) {
            var2.func_178822_d(var1.func_149324_c(), var3);
         }
      }

   }

   public void func_147254_a(S3DPacketDisplayScoreboard var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      if (var1.func_149370_d().length() == 0) {
         var2.func_96530_a(var1.func_149371_c(), (ScoreObjective)null);
      } else {
         ScoreObjective var3 = var2.func_96518_b(var1.func_149370_d());
         var2.func_96530_a(var1.func_149371_c(), var3);
      }

   }

   public void func_147247_a(S3EPacketTeams var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
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
         var3.func_178774_a(EnumChatFormatting.func_175744_a(var1.func_179813_h()));
         var3.func_98298_a(var1.func_149308_i());
         Team.EnumVisible var4 = Team.EnumVisible.func_178824_a(var1.func_179814_i());
         if (var4 != null) {
            var3.func_178772_a(var4);
         }
      }

      String var5;
      Iterator var6;
      if (var1.func_149307_h() == 0 || var1.func_149307_h() == 3) {
         var6 = var1.func_149310_g().iterator();

         while(var6.hasNext()) {
            var5 = (String)var6.next();
            var2.func_151392_a(var5, var1.func_149312_c());
         }
      }

      if (var1.func_149307_h() == 4) {
         var6 = var1.func_149310_g().iterator();

         while(var6.hasNext()) {
            var5 = (String)var6.next();
            var2.func_96512_b(var5, var3);
         }
      }

      if (var1.func_149307_h() == 1) {
         var2.func_96511_d(var3);
      }

   }

   public void func_147289_a(S2APacketParticles var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_149222_k() == 0) {
         double var2 = (double)(var1.func_149227_j() * var1.func_149221_g());
         double var4 = (double)(var1.func_149227_j() * var1.func_149224_h());
         double var6 = (double)(var1.func_149227_j() * var1.func_149223_i());

         try {
            this.field_147300_g.func_175682_a(var1.func_179749_a(), var1.func_179750_b(), var1.func_149220_d(), var1.func_149226_e(), var1.func_149225_f(), var2, var4, var6, var1.func_179748_k());
         } catch (Throwable var17) {
            field_147301_d.warn("Could not spawn particle effect " + var1.func_179749_a());
         }
      } else {
         for(int var18 = 0; var18 < var1.func_149222_k(); ++var18) {
            double var3 = this.field_147306_l.nextGaussian() * (double)var1.func_149221_g();
            double var5 = this.field_147306_l.nextGaussian() * (double)var1.func_149224_h();
            double var7 = this.field_147306_l.nextGaussian() * (double)var1.func_149223_i();
            double var9 = this.field_147306_l.nextGaussian() * (double)var1.func_149227_j();
            double var11 = this.field_147306_l.nextGaussian() * (double)var1.func_149227_j();
            double var13 = this.field_147306_l.nextGaussian() * (double)var1.func_149227_j();

            try {
               this.field_147300_g.func_175682_a(var1.func_179749_a(), var1.func_179750_b(), var1.func_149220_d() + var3, var1.func_149226_e() + var5, var1.func_149225_f() + var7, var9, var11, var13, var1.func_179748_k());
            } catch (Throwable var16) {
               field_147301_d.warn("Could not spawn particle effect " + var1.func_179749_a());
               return;
            }
         }
      }

   }

   public void func_147290_a(S20PacketEntityProperties var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149442_c());
      if (var2 != null) {
         if (!(var2 instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         } else {
            BaseAttributeMap var3 = ((EntityLivingBase)var2).func_110140_aT();
            Iterator var4 = var1.func_149441_d().iterator();

            while(var4.hasNext()) {
               S20PacketEntityProperties.Snapshot var5 = (S20PacketEntityProperties.Snapshot)var4.next();
               IAttributeInstance var6 = var3.func_111152_a(var5.func_151409_a());
               if (var6 == null) {
                  var6 = var3.func_111150_b(new RangedAttribute((IAttribute)null, var5.func_151409_a(), 0.0D, 2.2250738585072014E-308D, 1.7976931348623157E308D));
               }

               var6.func_111128_a(var5.func_151410_b());
               var6.func_142049_d();
               Iterator var7 = var5.func_151408_c().iterator();

               while(var7.hasNext()) {
                  AttributeModifier var8 = (AttributeModifier)var7.next();
                  var6.func_111121_a(var8);
               }
            }

         }
      }
   }

   public NetworkManager func_147298_b() {
      return this.field_147302_e;
   }

   public Collection<NetworkPlayerInfo> func_175106_d() {
      return this.field_147310_i.values();
   }

   public NetworkPlayerInfo func_175102_a(UUID var1) {
      return (NetworkPlayerInfo)this.field_147310_i.get(var1);
   }

   public NetworkPlayerInfo func_175104_a(String var1) {
      Iterator var2 = this.field_147310_i.values().iterator();

      NetworkPlayerInfo var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (NetworkPlayerInfo)var2.next();
      } while(!var3.func_178845_a().getName().equals(var1));

      return var3;
   }

   public GameProfile func_175105_e() {
      return this.field_175107_d;
   }
}
