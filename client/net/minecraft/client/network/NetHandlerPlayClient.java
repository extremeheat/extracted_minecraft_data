package net.minecraft.client.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.toasts.RecipeToast;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.renderer.debug.DebugRendererNeighborsUpdate;
import net.minecraft.client.renderer.debug.DebugRendererWorldGenAttempts;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.NBTQueryManager;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.client.util.SearchTree;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
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
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCommandList;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketNBTQueryResponse;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerLook;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketStopSound;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTagsList;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateRecipes;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.pathfinding.Path;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.Stat;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.NetworkTagManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityConduit;
import net.minecraft.tileentity.TileEntityEndGateway;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.tileentity.TileEntityStructure;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
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
   private final ClientAdvancementManager field_191983_k;
   private final ClientSuggestionProvider field_195516_l;
   private NetworkTagManager field_199725_m = new NetworkTagManager();
   private final NBTQueryManager field_211524_l = new NBTQueryManager(this);
   private final Random field_147306_l = new Random();
   private CommandDispatcher<ISuggestionProvider> field_195517_n = new CommandDispatcher();
   private final RecipeManager field_199528_o = new RecipeManager();

   public NetHandlerPlayClient(Minecraft var1, GuiScreen var2, NetworkManager var3, GameProfile var4) {
      super();
      this.field_147299_f = var1;
      this.field_147307_j = var2;
      this.field_147302_e = var3;
      this.field_175107_d = var4;
      this.field_191983_k = new ClientAdvancementManager(var1);
      this.field_195516_l = new ClientSuggestionProvider(this, var1);
   }

   public ClientSuggestionProvider func_195513_b() {
      return this.field_195516_l;
   }

   public void func_147296_c() {
      this.field_147300_g = null;
   }

   public RecipeManager func_199526_e() {
      return this.field_199528_o;
   }

   public void func_147282_a(SPacketJoinGame var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71442_b = new PlayerControllerMP(this.field_147299_f, this);
      this.field_147300_g = new WorldClient(this, new WorldSettings(0L, var1.func_149198_e(), false, var1.func_149195_d(), var1.func_149196_i()), var1.func_212642_e(), var1.func_149192_g(), this.field_147299_f.field_71424_I);
      this.field_147299_f.field_71474_y.field_74318_M = var1.func_149192_g();
      this.field_147299_f.func_71403_a(this.field_147300_g);
      this.field_147299_f.field_71439_g.field_71093_bK = var1.func_212642_e();
      this.field_147299_f.func_147108_a(new GuiDownloadTerrain());
      this.field_147299_f.field_71439_g.func_145769_d(var1.func_149197_c());
      this.field_147299_f.field_71439_g.func_175150_k(var1.func_179744_h());
      this.field_147299_f.field_71442_b.func_78746_a(var1.func_149198_e());
      this.field_147299_f.field_71474_y.func_82879_c();
      this.field_147302_e.func_179290_a(new CPacketCustomPayload(CPacketCustomPayload.field_210344_a, (new PacketBuffer(Unpooled.buffer())).func_180714_a(ClientBrandRetriever.getClientModName())));
   }

   public void func_147235_a(SPacketSpawnObject var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = var1.func_186880_c();
      double var4 = var1.func_186882_d();
      double var6 = var1.func_186881_e();
      Object var8 = null;
      if (var1.func_148993_l() == 10) {
         var8 = EntityMinecart.func_184263_a(this.field_147300_g, var2, var4, var6, EntityMinecart.Type.func_184955_a(var1.func_149009_m()));
      } else if (var1.func_148993_l() == 90) {
         Entity var9 = this.field_147300_g.func_73045_a(var1.func_149009_m());
         if (var9 instanceof EntityPlayer) {
            var8 = new EntityFishHook(this.field_147300_g, (EntityPlayer)var9, var2, var4, var6);
         }

         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 60) {
         var8 = new EntityTippedArrow(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 91) {
         var8 = new EntitySpectralArrow(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 94) {
         var8 = new EntityTrident(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 61) {
         var8 = new EntitySnowball(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 68) {
         var8 = new EntityLlamaSpit(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
      } else if (var1.func_148993_l() == 71) {
         var8 = new EntityItemFrame(this.field_147300_g, new BlockPos(var2, var4, var6), EnumFacing.func_82600_a(var1.func_149009_m()));
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 77) {
         var8 = new EntityLeashKnot(this.field_147300_g, new BlockPos(MathHelper.func_76128_c(var2), MathHelper.func_76128_c(var4), MathHelper.func_76128_c(var6)));
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 65) {
         var8 = new EntityEnderPearl(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 72) {
         var8 = new EntityEnderEye(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 76) {
         var8 = new EntityFireworkRocket(this.field_147300_g, var2, var4, var6, ItemStack.field_190927_a);
      } else if (var1.func_148993_l() == 63) {
         var8 = new EntityLargeFireball(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 93) {
         var8 = new EntityDragonFireball(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 64) {
         var8 = new EntitySmallFireball(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 66) {
         var8 = new EntityWitherSkull(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 67) {
         var8 = new EntityShulkerBullet(this.field_147300_g, var2, var4, var6, (double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 62) {
         var8 = new EntityEgg(this.field_147300_g, var2, var4, var6);
      } else if (var1.func_148993_l() == 79) {
         var8 = new EntityEvokerFangs(this.field_147300_g, var2, var4, var6, 0.0F, 0, (EntityLivingBase)null);
      } else if (var1.func_148993_l() == 73) {
         var8 = new EntityPotion(this.field_147300_g, var2, var4, var6, ItemStack.field_190927_a);
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
         var8 = new EntityFallingBlock(this.field_147300_g, var2, var4, var6, Block.func_196257_b(var1.func_149009_m()));
         var1.func_149002_g(0);
      } else if (var1.func_148993_l() == 3) {
         var8 = new EntityAreaEffectCloud(this.field_147300_g, var2, var4, var6);
      }

      if (var8 != null) {
         EntityTracker.func_187254_a((Entity)var8, var2, var4, var6);
         ((Entity)var8).field_70125_A = (float)(var1.func_149008_j() * 360) / 256.0F;
         ((Entity)var8).field_70177_z = (float)(var1.func_149006_k() * 360) / 256.0F;
         Entity[] var15 = ((Entity)var8).func_70021_al();
         if (var15 != null) {
            int var10 = var1.func_149001_c() - ((Entity)var8).func_145782_y();
            Entity[] var11 = var15;
            int var12 = var15.length;

            for(int var13 = 0; var13 < var12; ++var13) {
               Entity var14 = var11[var13];
               var14.func_145769_d(var14.func_145782_y() + var10);
            }
         }

         ((Entity)var8).func_145769_d(var1.func_149001_c());
         ((Entity)var8).func_184221_a(var1.func_186879_b());
         this.field_147300_g.func_73027_a(var1.func_149001_c(), (Entity)var8);
         if (var1.func_149009_m() > 0) {
            if (var1.func_148993_l() == 60 || var1.func_148993_l() == 91 || var1.func_148993_l() == 94) {
               Entity var16 = this.field_147300_g.func_73045_a(var1.func_149009_m() - 1);
               if (var16 instanceof EntityLivingBase && var8 instanceof EntityArrow) {
                  EntityArrow var17 = (EntityArrow)var8;
                  var17.func_212361_a(var16);
                  if (var16 instanceof EntityPlayer) {
                     var17.field_70251_a = EntityArrow.PickupStatus.ALLOWED;
                     if (((EntityPlayer)var16).field_71075_bZ.field_75098_d) {
                        var17.field_70251_a = EntityArrow.PickupStatus.CREATIVE_ONLY;
                     }
                  }
               }
            }

            ((Entity)var8).func_70016_h((double)var1.func_149010_g() / 8000.0D, (double)var1.func_149004_h() / 8000.0D, (double)var1.func_148999_i() / 8000.0D);
         }
      }

   }

   public void func_147286_a(SPacketSpawnExperienceOrb var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = var1.func_186885_b();
      double var4 = var1.func_186886_c();
      double var6 = var1.func_186884_d();
      EntityXPOrb var8 = new EntityXPOrb(this.field_147300_g, var2, var4, var6, var1.func_148986_g());
      EntityTracker.func_187254_a(var8, var2, var4, var6);
      var8.field_70177_z = 0.0F;
      var8.field_70125_A = 0.0F;
      var8.func_145769_d(var1.func_148985_c());
      this.field_147300_g.func_73027_a(var1.func_148985_c(), var8);
   }

   public void func_147292_a(SPacketSpawnGlobalEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = var1.func_186888_b();
      double var4 = var1.func_186889_c();
      double var6 = var1.func_186887_d();
      EntityLightningBolt var8 = null;
      if (var1.func_149053_g() == 1) {
         var8 = new EntityLightningBolt(this.field_147300_g, var2, var4, var6, false);
      }

      if (var8 != null) {
         EntityTracker.func_187254_a(var8, var2, var4, var6);
         var8.field_70177_z = 0.0F;
         var8.field_70125_A = 0.0F;
         var8.func_145769_d(var1.func_149052_c());
         this.field_147300_g.func_72942_c(var8);
      }

   }

   public void func_147288_a(SPacketSpawnPainting var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPainting var2 = new EntityPainting(this.field_147300_g, var1.func_179837_b(), var1.func_179836_c(), var1.func_201063_e());
      var2.func_184221_a(var1.func_186895_b());
      this.field_147300_g.func_73027_a(var1.func_148965_c(), var2);
   }

   public void func_147244_a(SPacketEntityVelocity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149412_c());
      if (var2 != null) {
         var2.func_70016_h((double)var1.func_149411_d() / 8000.0D, (double)var1.func_149410_e() / 8000.0D, (double)var1.func_149409_f() / 8000.0D);
      }
   }

   public void func_147284_a(SPacketEntityMetadata var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149375_d());
      if (var2 != null && var1.func_149376_c() != null) {
         var2.func_184212_Q().func_187218_a(var1.func_149376_c());
      }

   }

   public void func_147237_a(SPacketSpawnPlayer var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = var1.func_186898_d();
      double var4 = var1.func_186897_e();
      double var6 = var1.func_186899_f();
      float var8 = (float)(var1.func_148941_i() * 360) / 256.0F;
      float var9 = (float)(var1.func_148945_j() * 360) / 256.0F;
      EntityOtherPlayerMP var10 = new EntityOtherPlayerMP(this.field_147299_f.field_71441_e, this.func_175102_a(var1.func_179819_c()).func_178845_a());
      var10.field_70169_q = var2;
      var10.field_70142_S = var2;
      var10.field_70167_r = var4;
      var10.field_70137_T = var4;
      var10.field_70166_s = var6;
      var10.field_70136_U = var6;
      EntityTracker.func_187254_a(var10, var2, var4, var6);
      var10.func_70080_a(var2, var4, var6, var8, var9);
      this.field_147300_g.func_73027_a(var1.func_148943_d(), var10);
      List var11 = var1.func_148944_c();
      if (var11 != null) {
         var10.func_184212_Q().func_187218_a(var11);
      }

   }

   public void func_147275_a(SPacketEntityTeleport var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149451_c());
      if (var2 != null) {
         double var3 = var1.func_186982_b();
         double var5 = var1.func_186983_c();
         double var7 = var1.func_186981_d();
         EntityTracker.func_187254_a(var2, var3, var5, var7);
         if (!var2.func_184186_bw()) {
            float var9 = (float)(var1.func_149450_g() * 360) / 256.0F;
            float var10 = (float)(var1.func_149447_h() * 360) / 256.0F;
            if (Math.abs(var2.field_70165_t - var3) < 0.03125D && Math.abs(var2.field_70163_u - var5) < 0.015625D && Math.abs(var2.field_70161_v - var7) < 0.03125D) {
               var2.func_180426_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var9, var10, 0, true);
            } else {
               var2.func_180426_a(var3, var5, var7, var9, var10, 3, true);
            }

            var2.field_70122_E = var1.func_179697_g();
         }

      }
   }

   public void func_147257_a(SPacketHeldItemChange var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (InventoryPlayer.func_184435_e(var1.func_149385_c())) {
         this.field_147299_f.field_71439_g.field_71071_by.field_70461_c = var1.func_149385_c();
      }

   }

   public void func_147259_a(SPacketEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_149065_a(this.field_147300_g);
      if (var2 != null) {
         var2.field_70118_ct += (long)var1.func_186952_a();
         var2.field_70117_cu += (long)var1.func_186953_b();
         var2.field_70116_cv += (long)var1.func_186951_c();
         double var3 = (double)var2.field_70118_ct / 4096.0D;
         double var5 = (double)var2.field_70117_cu / 4096.0D;
         double var7 = (double)var2.field_70116_cv / 4096.0D;
         if (!var2.func_184186_bw()) {
            float var9 = var1.func_149060_h() ? (float)(var1.func_149066_f() * 360) / 256.0F : var2.field_70177_z;
            float var10 = var1.func_149060_h() ? (float)(var1.func_149063_g() * 360) / 256.0F : var2.field_70125_A;
            var2.func_180426_a(var3, var5, var7, var9, var10, 3, false);
            var2.field_70122_E = var1.func_179742_g();
         }

      }
   }

   public void func_147267_a(SPacketEntityHeadLook var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_149381_a(this.field_147300_g);
      if (var2 != null) {
         float var3 = (float)(var1.func_149380_c() * 360) / 256.0F;
         var2.func_208000_a(var3, 3);
      }
   }

   public void func_147238_a(SPacketDestroyEntities var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);

      for(int var2 = 0; var2 < var1.func_149098_c().length; ++var2) {
         this.field_147300_g.func_73028_b(var1.func_149098_c()[var2]);
      }

   }

   public void func_184330_a(SPacketPlayerPosLook var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      double var3 = var1.func_148932_c();
      double var5 = var1.func_148928_d();
      double var7 = var1.func_148933_e();
      float var9 = var1.func_148931_f();
      float var10 = var1.func_148930_g();
      if (var1.func_179834_f().contains(SPacketPlayerPosLook.EnumFlags.X)) {
         var3 += var2.field_70165_t;
      } else {
         var2.field_70159_w = 0.0D;
      }

      if (var1.func_179834_f().contains(SPacketPlayerPosLook.EnumFlags.Y)) {
         var5 += var2.field_70163_u;
      } else {
         var2.field_70181_x = 0.0D;
      }

      if (var1.func_179834_f().contains(SPacketPlayerPosLook.EnumFlags.Z)) {
         var7 += var2.field_70161_v;
      } else {
         var2.field_70179_y = 0.0D;
      }

      if (var1.func_179834_f().contains(SPacketPlayerPosLook.EnumFlags.X_ROT)) {
         var10 += var2.field_70125_A;
      }

      if (var1.func_179834_f().contains(SPacketPlayerPosLook.EnumFlags.Y_ROT)) {
         var9 += var2.field_70177_z;
      }

      var2.func_70080_a(var3, var5, var7, var9, var10);
      this.field_147302_e.func_179290_a(new CPacketConfirmTeleport(var1.func_186965_f()));
      this.field_147302_e.func_179290_a(new CPacketPlayer.PositionRotation(var2.field_70165_t, var2.func_174813_aQ().field_72338_b, var2.field_70161_v, var2.field_70177_z, var2.field_70125_A, false));
      if (!this.field_147309_h) {
         this.field_147299_f.field_71439_g.field_70169_q = this.field_147299_f.field_71439_g.field_70165_t;
         this.field_147299_f.field_71439_g.field_70167_r = this.field_147299_f.field_71439_g.field_70163_u;
         this.field_147299_f.field_71439_g.field_70166_s = this.field_147299_f.field_71439_g.field_70161_v;
         this.field_147309_h = true;
         this.field_147299_f.func_147108_a((GuiScreen)null);
      }

   }

   public void func_147287_a(SPacketMultiBlockChange var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      SPacketMultiBlockChange.BlockUpdateData[] var2 = var1.func_179844_a();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         SPacketMultiBlockChange.BlockUpdateData var5 = var2[var4];
         this.field_147300_g.func_195597_b(var5.func_180090_a(), var5.func_180088_c());
      }

   }

   public void func_147263_a(SPacketChunkData var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      int var2 = var1.func_149273_e();
      int var3 = var1.func_149271_f();
      Chunk var4 = this.field_147300_g.func_72863_F().func_212474_a(var2, var3, var1.func_186946_a(), var1.func_149276_g(), var1.func_149274_i());
      this.field_147300_g.func_147458_c(var2 << 4, 0, var3 << 4, (var2 << 4) + 15, 256, (var3 << 4) + 15);
      if (!var1.func_149274_i() || !(this.field_147300_g.field_73011_w instanceof OverworldDimension)) {
         var4.func_76613_n();
      }

      Iterator var5 = var1.func_189554_f().iterator();

      while(var5.hasNext()) {
         NBTTagCompound var6 = (NBTTagCompound)var5.next();
         BlockPos var7 = new BlockPos(var6.func_74762_e("x"), var6.func_74762_e("y"), var6.func_74762_e("z"));
         TileEntity var8 = this.field_147300_g.func_175625_s(var7);
         if (var8 != null) {
            var8.func_145839_a(var6);
         }
      }

   }

   public void func_184326_a(SPacketUnloadChunk var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      int var2 = var1.func_186940_a();
      int var3 = var1.func_186941_b();
      this.field_147300_g.func_72863_F().func_73234_b(var2, var3);
      this.field_147300_g.func_147458_c(var2 << 4, 0, var3 << 4, (var2 << 4) + 15, 256, (var3 << 4) + 15);
   }

   public void func_147234_a(SPacketBlockChange var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147300_g.func_195597_b(var1.func_179827_b(), var1.func_197685_a());
   }

   public void func_147253_a(SPacketDisconnect var1) {
      this.field_147302_e.func_150718_a(var1.func_149165_c());
   }

   public void func_147231_a(ITextComponent var1) {
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

   public void func_147297_a(Packet<?> var1) {
      this.field_147302_e.func_179290_a(var1);
   }

   public void func_147246_a(SPacketCollectItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149354_c());
      Object var3 = (EntityLivingBase)this.field_147300_g.func_73045_a(var1.func_149353_d());
      if (var3 == null) {
         var3 = this.field_147299_f.field_71439_g;
      }

      if (var2 != null) {
         if (var2 instanceof EntityXPOrb) {
            this.field_147300_g.func_184134_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187604_bf, SoundCategory.PLAYERS, 0.1F, (this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 0.35F + 0.9F, false);
         } else {
            this.field_147300_g.func_184134_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187638_cR, SoundCategory.PLAYERS, 0.2F, (this.field_147306_l.nextFloat() - this.field_147306_l.nextFloat()) * 1.4F + 2.0F, false);
         }

         if (var2 instanceof EntityItem) {
            ((EntityItem)var2).func_92059_d().func_190920_e(var1.func_191208_c());
         }

         this.field_147299_f.field_71452_i.func_78873_a(new ParticleItemPickup(this.field_147300_g, var2, (Entity)var3, 0.5F));
         this.field_147300_g.func_73028_b(var1.func_149354_c());
      }

   }

   public void func_147251_a(SPacketChat var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71456_v.func_191742_a(var1.func_192590_c(), var1.func_148915_c());
   }

   public void func_147279_a(SPacketAnimation var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_148978_c());
      if (var2 != null) {
         EntityLivingBase var3;
         if (var1.func_148977_d() == 0) {
            var3 = (EntityLivingBase)var2;
            var3.func_184609_a(EnumHand.MAIN_HAND);
         } else if (var1.func_148977_d() == 3) {
            var3 = (EntityLivingBase)var2;
            var3.func_184609_a(EnumHand.OFF_HAND);
         } else if (var1.func_148977_d() == 1) {
            var2.func_70057_ab();
         } else if (var1.func_148977_d() == 2) {
            EntityPlayer var4 = (EntityPlayer)var2;
            var4.func_70999_a(false, false, false);
         } else if (var1.func_148977_d() == 4) {
            this.field_147299_f.field_71452_i.func_199282_a(var2, Particles.field_197614_g);
         } else if (var1.func_148977_d() == 5) {
            this.field_147299_f.field_71452_i.func_199282_a(var2, Particles.field_197622_o);
         }

      }
   }

   public void func_147278_a(SPacketUseBed var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      var1.func_149091_a(this.field_147300_g).func_180469_a(var1.func_179798_a());
   }

   public void func_147281_a(SPacketSpawnMob var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      double var2 = var1.func_186891_e();
      double var4 = var1.func_186892_f();
      double var6 = var1.func_186893_g();
      float var8 = (float)(var1.func_149028_l() * 360) / 256.0F;
      float var9 = (float)(var1.func_149030_m() * 360) / 256.0F;
      EntityLivingBase var10 = (EntityLivingBase)EntityType.func_200717_a(var1.func_149025_e(), this.field_147299_f.field_71441_e);
      if (var10 != null) {
         EntityTracker.func_187254_a(var10, var2, var4, var6);
         var10.field_70761_aq = (float)(var1.func_149032_n() * 360) / 256.0F;
         var10.field_70759_as = (float)(var1.func_149032_n() * 360) / 256.0F;
         Entity[] var11 = var10.func_70021_al();
         if (var11 != null) {
            int var12 = var1.func_149024_d() - var10.func_145782_y();
            Entity[] var13 = var11;
            int var14 = var11.length;

            for(int var15 = 0; var15 < var14; ++var15) {
               Entity var16 = var13[var15];
               var16.func_145769_d(var16.func_145782_y() + var12);
            }
         }

         var10.func_145769_d(var1.func_149024_d());
         var10.func_184221_a(var1.func_186890_c());
         var10.func_70080_a(var2, var4, var6, var8, var9);
         var10.field_70159_w = (double)((float)var1.func_149026_i() / 8000.0F);
         var10.field_70181_x = (double)((float)var1.func_149033_j() / 8000.0F);
         var10.field_70179_y = (double)((float)var1.func_149031_k() / 8000.0F);
         this.field_147300_g.func_73027_a(var1.func_149024_d(), var10);
         List var17 = var1.func_149027_c();
         if (var17 != null) {
            var10.func_184212_Q().func_187218_a(var17);
         }
      } else {
         field_147301_d.warn("Skipping Entity with id {}", var1.func_149025_e());
      }

   }

   public void func_147285_a(SPacketTimeUpdate var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_82738_a(var1.func_149366_c());
      this.field_147299_f.field_71441_e.func_72877_b(var1.func_149365_d());
   }

   public void func_147271_a(SPacketSpawnPosition var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71439_g.func_180473_a(var1.func_179800_a(), true);
      this.field_147299_f.field_71441_e.func_72912_H().func_176143_a(var1.func_179800_a());
   }

   public void func_184328_a(SPacketSetPassengers var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_186972_b());
      if (var2 == null) {
         field_147301_d.warn("Received passengers for unknown entity");
      } else {
         boolean var3 = var2.func_184215_y(this.field_147299_f.field_71439_g);
         var2.func_184226_ay();
         int[] var4 = var1.func_186971_a();
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var4[var6];
            Entity var8 = this.field_147300_g.func_73045_a(var7);
            if (var8 != null) {
               var8.func_184205_a(var2, true);
               if (var8 == this.field_147299_f.field_71439_g && !var3) {
                  this.field_147299_f.field_71456_v.func_110326_a(I18n.func_135052_a("mount.onboard", this.field_147299_f.field_71474_y.field_74311_E.func_197978_k()), false);
               }
            }
         }

      }
   }

   public void func_147243_a(SPacketEntityAttach var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149403_d());
      Entity var3 = this.field_147300_g.func_73045_a(var1.func_149402_e());
      if (var2 instanceof EntityLiving) {
         if (var3 != null) {
            ((EntityLiving)var2).func_110162_b(var3, false);
         } else {
            ((EntityLiving)var2).func_110160_i(false, false);
         }
      }

   }

   public void func_147236_a(SPacketEntityStatus var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_149161_a(this.field_147300_g);
      if (var2 != null) {
         if (var1.func_149160_c() == 21) {
            this.field_147299_f.func_147118_V().func_147682_a(new GuardianSound((EntityGuardian)var2));
         } else if (var1.func_149160_c() == 35) {
            boolean var3 = true;
            this.field_147299_f.field_71452_i.func_199281_a(var2, Particles.field_197604_O, 30);
            this.field_147300_g.func_184134_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_191263_gW, var2.func_184176_by(), 1.0F, 1.0F, false);
            if (var2 == this.field_147299_f.field_71439_g) {
               this.field_147299_f.field_71460_t.func_190565_a(new ItemStack(Items.field_190929_cY));
            }
         } else {
            var2.func_70103_a(var1.func_149160_c());
         }
      }

   }

   public void func_147249_a(SPacketUpdateHealth var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71439_g.func_71150_b(var1.func_149332_c());
      this.field_147299_f.field_71439_g.func_71024_bL().func_75114_a(var1.func_149330_d());
      this.field_147299_f.field_71439_g.func_71024_bL().func_75119_b(var1.func_149331_e());
   }

   public void func_147295_a(SPacketSetExperience var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71439_g.func_71152_a(var1.func_149397_c(), var1.func_149396_d(), var1.func_149395_e());
   }

   public void func_147280_a(SPacketRespawn var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      DimensionType var2 = var1.func_212643_b();
      if (var2 != this.field_147299_f.field_71439_g.field_71093_bK) {
         this.field_147309_h = false;
         Scoreboard var3 = this.field_147300_g.func_96441_U();
         this.field_147300_g = new WorldClient(this, new WorldSettings(0L, var1.func_149083_e(), false, this.field_147299_f.field_71441_e.func_72912_H().func_76093_s(), var1.func_149080_f()), var1.func_212643_b(), var1.func_149081_d(), this.field_147299_f.field_71424_I);
         this.field_147300_g.func_96443_a(var3);
         this.field_147299_f.func_71403_a(this.field_147300_g);
         this.field_147299_f.field_71439_g.field_71093_bK = var2;
         this.field_147299_f.func_147108_a(new GuiDownloadTerrain());
      }

      this.field_147299_f.func_212315_a(var1.func_212643_b());
      this.field_147299_f.field_71442_b.func_78746_a(var1.func_149083_e());
   }

   public void func_147283_a(SPacketExplosion var1) {
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

   public void func_147265_a(SPacketOpenWindow var1) {
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
         if (var3 instanceof AbstractHorse) {
            var2.func_184826_a((AbstractHorse)var3, new ContainerHorseChest(var1.func_179840_c(), var1.func_148898_f()));
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

   public void func_147266_a(SPacketSetSlot var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      ItemStack var3 = var1.func_149174_e();
      int var4 = var1.func_149173_d();
      this.field_147299_f.func_193032_ao().func_193301_a(var3);
      if (var1.func_149175_c() == -1) {
         var2.field_71071_by.func_70437_b(var3);
      } else if (var1.func_149175_c() == -2) {
         var2.field_71071_by.func_70299_a(var4, var3);
      } else {
         boolean var5 = false;
         if (this.field_147299_f.field_71462_r instanceof GuiContainerCreative) {
            GuiContainerCreative var6 = (GuiContainerCreative)this.field_147299_f.field_71462_r;
            var5 = var6.func_147056_g() != ItemGroup.field_78036_m.func_78021_a();
         }

         if (var1.func_149175_c() == 0 && var1.func_149173_d() >= 36 && var4 < 45) {
            if (!var3.func_190926_b()) {
               ItemStack var7 = var2.field_71069_bz.func_75139_a(var4).func_75211_c();
               if (var7.func_190926_b() || var7.func_190916_E() < var3.func_190916_E()) {
                  var3.func_190915_d(5);
               }
            }

            var2.field_71069_bz.func_75141_a(var4, var3);
         } else if (var1.func_149175_c() == var2.field_71070_bA.field_75152_c && (var1.func_149175_c() != 0 || !var5)) {
            var2.field_71070_bA.func_75141_a(var4, var3);
         }
      }

   }

   public void func_147239_a(SPacketConfirmTransaction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Container var2 = null;
      EntityPlayerSP var3 = this.field_147299_f.field_71439_g;
      if (var1.func_148889_c() == 0) {
         var2 = var3.field_71069_bz;
      } else if (var1.func_148889_c() == var3.field_71070_bA.field_75152_c) {
         var2 = var3.field_71070_bA;
      }

      if (var2 != null && !var1.func_148888_e()) {
         this.func_147297_a(new CPacketConfirmTransaction(var1.func_148889_c(), var1.func_148890_d(), true));
      }

   }

   public void func_147241_a(SPacketWindowItems var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      if (var1.func_148911_c() == 0) {
         var2.field_71069_bz.func_190896_a(var1.func_148910_d());
      } else if (var1.func_148911_c() == var2.field_71070_bA.field_75152_c) {
         var2.field_71070_bA.func_190896_a(var1.func_148910_d());
      }

   }

   public void func_147268_a(SPacketSignEditorOpen var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Object var2 = this.field_147300_g.func_175625_s(var1.func_179777_a());
      if (!(var2 instanceof TileEntitySign)) {
         var2 = new TileEntitySign();
         ((TileEntity)var2).func_145834_a(this.field_147300_g);
         ((TileEntity)var2).func_174878_a(var1.func_179777_a());
      }

      this.field_147299_f.field_71439_g.func_175141_a((TileEntitySign)var2);
   }

   public void func_147273_a(SPacketUpdateTileEntity var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (this.field_147299_f.field_71441_e.func_175667_e(var1.func_179823_a())) {
         TileEntity var2 = this.field_147299_f.field_71441_e.func_175625_s(var1.func_179823_a());
         int var3 = var1.func_148853_f();
         boolean var4 = var3 == 2 && var2 instanceof TileEntityCommandBlock;
         if (var3 == 1 && var2 instanceof TileEntityMobSpawner || var4 || var3 == 3 && var2 instanceof TileEntityBeacon || var3 == 4 && var2 instanceof TileEntitySkull || var3 == 6 && var2 instanceof TileEntityBanner || var3 == 7 && var2 instanceof TileEntityStructure || var3 == 8 && var2 instanceof TileEntityEndGateway || var3 == 9 && var2 instanceof TileEntitySign || var3 == 10 && var2 instanceof TileEntityShulkerBox || var3 == 11 && var2 instanceof TileEntityBed || var3 == 5 && var2 instanceof TileEntityConduit) {
            var2.func_145839_a(var1.func_148857_g());
         }

         if (var4 && this.field_147299_f.field_71462_r instanceof GuiCommandBlock) {
            ((GuiCommandBlock)this.field_147299_f.field_71462_r).func_184075_a();
         }
      }

   }

   public void func_147245_a(SPacketWindowProperty var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      if (var2.field_71070_bA != null && var2.field_71070_bA.field_75152_c == var1.func_149182_c()) {
         var2.field_71070_bA.func_75137_b(var1.func_149181_d(), var1.func_149180_e());
      }

   }

   public void func_147242_a(SPacketEntityEquipment var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149389_d());
      if (var2 != null) {
         var2.func_184201_a(var1.func_186969_c(), var1.func_149390_c());
      }

   }

   public void func_147276_a(SPacketCloseWindow var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71439_g.func_175159_q();
   }

   public void func_147261_a(SPacketBlockAction var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_175641_c(var1.func_179825_a(), var1.func_148868_c(), var1.func_148869_g(), var1.func_148864_h());
   }

   public void func_147294_a(SPacketBlockBreakAnim var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_175715_c(var1.func_148845_c(), var1.func_179821_b(), var1.func_148846_g());
   }

   public void func_147252_a(SPacketChangeGameState var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      int var3 = var1.func_149138_c();
      float var4 = var1.func_149137_d();
      int var5 = MathHelper.func_76141_d(var4 + 0.5F);
      if (var3 >= 0 && var3 < SPacketChangeGameState.field_149142_a.length && SPacketChangeGameState.field_149142_a[var3] != null) {
         var2.func_146105_b(new TextComponentTranslation(SPacketChangeGameState.field_149142_a[var3], new Object[0]), false);
      }

      if (var3 == 1) {
         this.field_147300_g.func_72912_H().func_76084_b(true);
         this.field_147300_g.func_72894_k(0.0F);
      } else if (var3 == 2) {
         this.field_147300_g.func_72912_H().func_76084_b(false);
         this.field_147300_g.func_72894_k(1.0F);
      } else if (var3 == 3) {
         this.field_147299_f.field_71442_b.func_78746_a(GameType.func_77146_a(var5));
      } else if (var3 == 4) {
         if (var5 == 0) {
            this.field_147299_f.field_71439_g.field_71174_a.func_147297_a(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            this.field_147299_f.func_147108_a(new GuiDownloadTerrain());
         } else if (var5 == 1) {
            this.field_147299_f.func_147108_a(new GuiWinGame(true, () -> {
               this.field_147299_f.field_71439_g.field_71174_a.func_147297_a(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            }));
         }
      } else if (var3 == 5) {
         GameSettings var6 = this.field_147299_f.field_71474_y;
         if (var4 == 0.0F) {
            this.field_147299_f.func_147108_a(new GuiScreenDemo());
         } else if (var4 == 101.0F) {
            this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new TextComponentTranslation("demo.help.movement", new Object[]{var6.field_74351_w.func_197978_k(), var6.field_74370_x.func_197978_k(), var6.field_74368_y.func_197978_k(), var6.field_74366_z.func_197978_k()}));
         } else if (var4 == 102.0F) {
            this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new TextComponentTranslation("demo.help.jump", new Object[]{var6.field_74314_A.func_197978_k()}));
         } else if (var4 == 103.0F) {
            this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new TextComponentTranslation("demo.help.inventory", new Object[]{var6.field_151445_Q.func_197978_k()}));
         } else if (var4 == 104.0F) {
            this.field_147299_f.field_71456_v.func_146158_b().func_146227_a(new TextComponentTranslation("demo.day.6", new Object[]{var6.field_151447_Z.func_197978_k()}));
         }
      } else if (var3 == 6) {
         this.field_147300_g.func_184148_a(var2, var2.field_70165_t, var2.field_70163_u + (double)var2.func_70047_e(), var2.field_70161_v, SoundEvents.field_187734_u, SoundCategory.PLAYERS, 0.18F, 0.45F);
      } else if (var3 == 7) {
         this.field_147300_g.func_72894_k(var4);
      } else if (var3 == 8) {
         this.field_147300_g.func_147442_i(var4);
      } else if (var3 == 9) {
         this.field_147300_g.func_184148_a(var2, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_203830_gs, SoundCategory.NEUTRAL, 1.0F, 1.0F);
      } else if (var3 == 10) {
         this.field_147300_g.func_195594_a(Particles.field_197621_n, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, 0.0D, 0.0D, 0.0D);
         this.field_147300_g.func_184148_a(var2, var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, SoundEvents.field_187514_aD, SoundCategory.HOSTILE, 1.0F, 1.0F);
      }

   }

   public void func_147264_a(SPacketMaps var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      MapItemRenderer var2 = this.field_147299_f.field_71460_t.func_147701_i();
      String var3 = "map_" + var1.func_149188_c();
      MapData var4 = ItemMap.func_195953_a(this.field_147299_f.field_71441_e, var3);
      if (var4 == null) {
         var4 = new MapData(var3);
         if (var2.func_191205_a(var3) != null) {
            MapData var5 = var2.func_191207_a(var2.func_191205_a(var3));
            if (var5 != null) {
               var4 = var5;
            }
         }

         this.field_147299_f.field_71441_e.func_212409_a(DimensionType.OVERWORLD, var3, var4);
      }

      var1.func_179734_a(var4);
      var2.func_148246_a(var4);
   }

   public void func_147277_a(SPacketEffect var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_149244_c()) {
         this.field_147299_f.field_71441_e.func_175669_a(var1.func_149242_d(), var1.func_179746_d(), var1.func_149241_e());
      } else {
         this.field_147299_f.field_71441_e.func_175718_b(var1.func_149242_d(), var1.func_179746_d(), var1.func_149241_e());
      }

   }

   public void func_191981_a(SPacketAdvancementInfo var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_191983_k.func_192799_a(var1);
   }

   public void func_194022_a(SPacketSelectAdvancementsTab var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      ResourceLocation var2 = var1.func_194154_a();
      if (var2 == null) {
         this.field_191983_k.func_194230_a((Advancement)null, false);
      } else {
         Advancement var3 = this.field_191983_k.func_194229_a().func_192084_a(var2);
         this.field_191983_k.func_194230_a(var3, false);
      }

   }

   public void func_195511_a(SPacketCommandList var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_195517_n = new CommandDispatcher(var1.func_197693_a());
   }

   public void func_195512_a(SPacketStopSound var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.func_147118_V().func_195478_a(var1.func_197703_a(), var1.func_197704_b());
   }

   public void func_195510_a(SPacketTabComplete var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_195516_l.func_197015_a(var1.func_197689_a(), var1.func_197687_b());
   }

   public void func_199525_a(SPacketUpdateRecipes var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_199528_o.func_199518_d();
      Iterator var2 = var1.func_199616_a().iterator();

      while(var2.hasNext()) {
         IRecipe var3 = (IRecipe)var2.next();
         this.field_199528_o.func_199509_a(var3);
      }

      SearchTree var4 = (SearchTree)this.field_147299_f.func_193987_a(SearchTreeManager.field_194012_b);
      var4.func_199550_b();
      RecipeBookClient var5 = this.field_147299_f.field_71439_g.func_199507_B();
      var5.func_199644_c();
      var5.func_199642_d().forEach(var4::func_194043_a);
      var4.func_194040_a();
   }

   public void func_200232_a(SPacketPlayerLook var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Vec3d var2 = var1.func_200531_a(this.field_147300_g);
      if (var2 != null) {
         this.field_147299_f.field_71439_g.func_200602_a(var1.func_201064_a(), var2);
      }

   }

   public void func_211522_a(SPacketNBTQueryResponse var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (!this.field_211524_l.func_211548_a(var1.func_211713_b(), var1.func_211712_c())) {
         field_147301_d.debug("Got unhandled response to tag query {}", var1.func_211713_b());
      }

   }

   public void func_147293_a(SPacketStatistics var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Iterator var2 = var1.func_148974_c().entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         Stat var4 = (Stat)var3.getKey();
         int var5 = (Integer)var3.getValue();
         this.field_147299_f.field_71439_g.func_146107_m().func_150873_a(this.field_147299_f.field_71439_g, var4, var5);
      }

      if (this.field_147299_f.field_71462_r instanceof IProgressMeter) {
         ((IProgressMeter)this.field_147299_f.field_71462_r).func_193026_g();
      }

   }

   public void func_191980_a(SPacketRecipeBook var1) {
      RecipeBookClient var2;
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      var2 = this.field_147299_f.field_71439_g.func_199507_B();
      var2.func_192813_a(var1.func_192593_c());
      var2.func_192810_b(var1.func_192594_d());
      var2.func_202881_c(var1.func_202492_e());
      var2.func_202882_d(var1.func_202493_f());
      SPacketRecipeBook.State var3 = var1.func_194151_e();
      Iterator var4;
      ResourceLocation var5;
      IRecipe var6;
      label57:
      switch(var3) {
      case REMOVE:
         var4 = var1.func_192595_a().iterator();

         while(true) {
            if (!var4.hasNext()) {
               break label57;
            }

            var5 = (ResourceLocation)var4.next();
            var6 = this.field_199528_o.func_199517_a(var5);
            if (var6 != null) {
               var2.func_193831_b(var6);
            }
         }
      case INIT:
         var4 = var1.func_192595_a().iterator();

         while(var4.hasNext()) {
            var5 = (ResourceLocation)var4.next();
            var6 = this.field_199528_o.func_199517_a(var5);
            if (var6 != null) {
               var2.func_194073_a(var6);
            }
         }

         var4 = var1.func_193644_b().iterator();

         while(true) {
            if (!var4.hasNext()) {
               break label57;
            }

            var5 = (ResourceLocation)var4.next();
            var6 = this.field_199528_o.func_199517_a(var5);
            if (var6 != null) {
               var2.func_193825_e(var6);
            }
         }
      case ADD:
         var4 = var1.func_192595_a().iterator();

         while(var4.hasNext()) {
            var5 = (ResourceLocation)var4.next();
            var6 = this.field_199528_o.func_199517_a(var5);
            if (var6 != null) {
               var2.func_194073_a(var6);
               var2.func_193825_e(var6);
               RecipeToast.func_193665_a(this.field_147299_f.func_193033_an(), var6);
            }
         }
      }

      var2.func_199642_d().forEach((var1x) -> {
         var1x.func_194214_a(var2);
      });
      if (this.field_147299_f.field_71462_r instanceof IRecipeShownListener) {
         ((IRecipeShownListener)this.field_147299_f.field_71462_r).func_192043_J_();
      }

   }

   public void func_147260_a(SPacketEntityEffect var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149426_d());
      if (var2 instanceof EntityLivingBase) {
         Potion var3 = Potion.func_188412_a(var1.func_149427_e());
         if (var3 != null) {
            PotionEffect var4 = new PotionEffect(var3, var1.func_180755_e(), var1.func_149428_f(), var1.func_186984_g(), var1.func_179707_f(), var1.func_205527_h());
            var4.func_100012_b(var1.func_149429_c());
            ((EntityLivingBase)var2).func_195064_c(var4);
         }
      }
   }

   public void func_199723_a(SPacketTagsList var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_199725_m = var1.func_199858_a();
      if (!this.field_147302_e.func_150731_c()) {
         BlockTags.func_199895_a(this.field_199725_m.func_199717_a());
         ItemTags.func_199902_a(this.field_199725_m.func_199715_b());
         FluidTags.func_206953_a(this.field_199725_m.func_205704_c());
      }

   }

   public void func_175098_a(SPacketCombatEvent var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.field_179776_a == SPacketCombatEvent.Event.ENTITY_DIED) {
         Entity var2 = this.field_147300_g.func_73045_a(var1.field_179774_b);
         if (var2 == this.field_147299_f.field_71439_g) {
            this.field_147299_f.func_147108_a(new GuiGameOver(var1.field_179773_e));
         }
      }

   }

   public void func_175101_a(SPacketServerDifficulty var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_72912_H().func_176144_a(var1.func_179831_b());
      this.field_147299_f.field_71441_e.func_72912_H().func_180783_e(var1.func_179830_a());
   }

   public void func_175094_a(SPacketCamera var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_179780_a(this.field_147300_g);
      if (var2 != null) {
         this.field_147299_f.func_175607_a(var2);
      }

   }

   public void func_175093_a(SPacketWorldBorder var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      var1.func_179788_a(this.field_147300_g.func_175723_af());
   }

   public void func_175099_a(SPacketTitle var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      SPacketTitle.Type var2 = var1.func_179807_a();
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
      case ACTIONBAR:
         this.field_147299_f.field_71456_v.func_110326_a(var5, false);
         return;
      case RESET:
         this.field_147299_f.field_71456_v.func_175178_a("", "", -1, -1, -1);
         this.field_147299_f.field_71456_v.func_175177_a();
         return;
      }

      this.field_147299_f.field_71456_v.func_175178_a(var3, var4, var1.func_179806_c(), var1.func_179804_d(), var1.func_179803_e());
   }

   public void func_175096_a(SPacketPlayerListHeaderFooter var1) {
      this.field_147299_f.field_71456_v.func_175181_h().func_175244_b(var1.func_179700_a().func_150254_d().isEmpty() ? null : var1.func_179700_a());
      this.field_147299_f.field_71456_v.func_175181_h().func_175248_a(var1.func_179701_b().func_150254_d().isEmpty() ? null : var1.func_179701_b());
   }

   public void func_147262_a(SPacketRemoveEntityEffect var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = var1.func_186967_a(this.field_147300_g);
      if (var2 instanceof EntityLivingBase) {
         ((EntityLivingBase)var2).func_184596_c(var1.func_186968_a());
      }

   }

   public void func_147256_a(SPacketPlayerListItem var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Iterator var2 = var1.func_179767_a().iterator();

      while(var2.hasNext()) {
         SPacketPlayerListItem.AddPlayerData var3 = (SPacketPlayerListItem.AddPlayerData)var2.next();
         if (var1.func_179768_b() == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
            this.field_147310_i.remove(var3.func_179962_a().getId());
         } else {
            NetworkPlayerInfo var4 = (NetworkPlayerInfo)this.field_147310_i.get(var3.func_179962_a().getId());
            if (var1.func_179768_b() == SPacketPlayerListItem.Action.ADD_PLAYER) {
               var4 = new NetworkPlayerInfo(var3);
               this.field_147310_i.put(var4.func_178845_a().getId(), var4);
            }

            if (var4 != null) {
               switch(var1.func_179768_b()) {
               case ADD_PLAYER:
                  var4.func_178839_a(var3.func_179960_c());
                  var4.func_178838_a(var3.func_179963_b());
                  var4.func_178859_a(var3.func_179961_d());
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

   public void func_147272_a(SPacketKeepAlive var1) {
      this.func_147297_a(new CPacketKeepAlive(var1.func_149134_c()));
   }

   public void func_147270_a(SPacketPlayerAbilities var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      EntityPlayerSP var2 = this.field_147299_f.field_71439_g;
      var2.field_71075_bZ.field_75100_b = var1.func_149106_d();
      var2.field_71075_bZ.field_75098_d = var1.func_149103_f();
      var2.field_71075_bZ.field_75102_a = var1.func_149112_c();
      var2.field_71075_bZ.field_75101_c = var1.func_149105_e();
      var2.field_71075_bZ.func_195931_a((double)var1.func_149101_g());
      var2.field_71075_bZ.func_82877_b(var1.func_149107_h());
   }

   public void func_184327_a(SPacketSoundEffect var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71441_e.func_184148_a(this.field_147299_f.field_71439_g, var1.func_149207_d(), var1.func_149211_e(), var1.func_149210_f(), var1.func_186978_a(), var1.func_186977_b(), var1.func_149208_g(), var1.func_149209_h());
   }

   public void func_184329_a(SPacketCustomSound var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.func_147118_V().func_147682_a(new SimpleSound(var1.func_197698_a(), var1.func_186929_b(), var1.func_186927_f(), var1.func_186928_g(), false, 0, ISound.AttenuationType.LINEAR, (float)var1.func_186932_c(), (float)var1.func_186926_d(), (float)var1.func_186925_e()));
   }

   public void func_175095_a(SPacketResourcePackSend var1) {
      String var2 = var1.func_179783_a();
      String var3 = var1.func_179784_b();
      if (this.func_189688_b(var2)) {
         if (var2.startsWith("level://")) {
            try {
               String var8 = URLDecoder.decode(var2.substring("level://".length()), StandardCharsets.UTF_8.toString());
               File var5 = new File(this.field_147299_f.field_71412_D, "saves");
               File var6 = new File(var5, var8);
               if (var6.isFile()) {
                  this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                  Futures.addCallback(this.field_147299_f.func_195541_I().func_195741_a(var6), this.func_189686_f());
                  return;
               }
            } catch (UnsupportedEncodingException var7) {
            }

            this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         } else {
            ServerData var4 = this.field_147299_f.func_147104_D();
            if (var4 != null && var4.func_152586_b() == ServerData.ServerResourceMode.ENABLED) {
               this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
               Futures.addCallback(this.field_147299_f.func_195541_I().func_195744_a(var2, var3), this.func_189686_f());
            } else if (var4 != null && var4.func_152586_b() != ServerData.ServerResourceMode.PROMPT) {
               this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
            } else {
               this.field_147299_f.func_152344_a(() -> {
                  this.field_147299_f.func_147108_a(new GuiYesNo((var3x, var4) -> {
                     this.field_147299_f = Minecraft.func_71410_x();
                     ServerData var5 = this.field_147299_f.func_147104_D();
                     if (var3x) {
                        if (var5 != null) {
                           var5.func_152584_a(ServerData.ServerResourceMode.ENABLED);
                        }

                        this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.ACCEPTED));
                        Futures.addCallback(this.field_147299_f.func_195541_I().func_195744_a(var2, var3), this.func_189686_f());
                     } else {
                        if (var5 != null) {
                           var5.func_152584_a(ServerData.ServerResourceMode.DISABLED);
                        }

                        this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.DECLINED));
                     }

                     ServerList.func_147414_b(var5);
                     this.field_147299_f.func_147108_a((GuiScreen)null);
                  }, I18n.func_135052_a("multiplayer.texturePrompt.line1"), I18n.func_135052_a("multiplayer.texturePrompt.line2"), 0));
               });
            }

         }
      }
   }

   private boolean func_189688_b(String var1) {
      try {
         URI var2 = new URI(var1);
         String var3 = var2.getScheme();
         boolean var4 = "level".equals(var3);
         if (!"http".equals(var3) && !"https".equals(var3) && !var4) {
            throw new URISyntaxException(var1, "Wrong protocol");
         } else if (!var4 || !var1.contains("..") && var1.endsWith("/resources.zip")) {
            return true;
         } else {
            throw new URISyntaxException(var1, "Invalid levelstorage resourcepack path");
         }
      } catch (URISyntaxException var5) {
         this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         return false;
      }
   }

   private FutureCallback<Object> func_189686_f() {
      return new FutureCallback<Object>() {
         public void onSuccess(@Nullable Object var1) {
            NetHandlerPlayClient.this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
         }

         public void onFailure(Throwable var1) {
            NetHandlerPlayClient.this.field_147302_e.func_179290_a(new CPacketResourcePackStatus(CPacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         }
      };
   }

   public void func_184325_a(SPacketUpdateBossInfo var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      this.field_147299_f.field_71456_v.func_184046_j().func_184055_a(var1);
   }

   public void func_184324_a(SPacketCooldown var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_186922_b() == 0) {
         this.field_147299_f.field_71439_g.func_184811_cZ().func_185142_b(var1.func_186920_a());
      } else {
         this.field_147299_f.field_71439_g.func_184811_cZ().func_185145_a(var1.func_186920_a(), var1.func_186922_b());
      }

   }

   public void func_184323_a(SPacketMoveVehicle var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147299_f.field_71439_g.func_184208_bv();
      if (var2 != this.field_147299_f.field_71439_g && var2.func_184186_bw()) {
         var2.func_70080_a(var1.func_186957_a(), var1.func_186955_b(), var1.func_186956_c(), var1.func_186959_d(), var1.func_186958_e());
         this.field_147302_e.func_179290_a(new CPacketVehicleMove(var2));
      }

   }

   public void func_147240_a(SPacketCustomPayload var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      ResourceLocation var2 = var1.func_149169_c();
      PacketBuffer var3 = null;

      try {
         var3 = var1.func_180735_b();
         int var4;
         if (SPacketCustomPayload.field_209910_a.equals(var2)) {
            try {
               var4 = var3.readInt();
               GuiScreen var5 = this.field_147299_f.field_71462_r;
               if (var5 instanceof GuiMerchant && var4 == this.field_147299_f.field_71439_g.field_71070_bA.field_75152_c) {
                  IMerchant var6 = ((GuiMerchant)var5).func_147035_g();
                  MerchantRecipeList var7 = MerchantRecipeList.func_151390_b(var3);
                  var6.func_70930_a(var7);
               }
            } catch (IOException var13) {
               field_147301_d.error("Couldn't load trade info", var13);
            }
         } else if (SPacketCustomPayload.field_209911_b.equals(var2)) {
            this.field_147299_f.field_71439_g.func_175158_f(var3.func_150789_c(32767));
         } else if (SPacketCustomPayload.field_209912_c.equals(var2)) {
            EnumHand var15 = (EnumHand)var3.func_179257_a(EnumHand.class);
            ItemStack var17 = var15 == EnumHand.OFF_HAND ? this.field_147299_f.field_71439_g.func_184592_cb() : this.field_147299_f.field_71439_g.func_184614_ca();
            if (var17.func_77973_b() == Items.field_151164_bB) {
               this.field_147299_f.func_147108_a(new GuiScreenBook(this.field_147299_f.field_71439_g, var17, false, var15));
            }
         } else if (SPacketCustomPayload.field_209913_d.equals(var2)) {
            var4 = var3.readInt();
            float var19 = var3.readFloat();
            Path var21 = Path.func_186311_b(var3);
            this.field_147299_f.field_184132_p.field_188286_a.func_188289_a(var4, var21, var19);
         } else if (SPacketCustomPayload.field_209914_e.equals(var2)) {
            long var16 = var3.func_179260_f();
            BlockPos var23 = var3.func_179259_c();
            ((DebugRendererNeighborsUpdate)this.field_147299_f.field_184132_p.field_191557_f).func_191553_a(var16, var23);
         } else {
            ArrayList var25;
            if (SPacketCustomPayload.field_209915_f.equals(var2)) {
               BlockPos var18 = var3.func_179259_c();
               int var20 = var3.readInt();
               ArrayList var24 = Lists.newArrayList();
               var25 = Lists.newArrayList();

               for(int var8 = 0; var8 < var20; ++var8) {
                  var24.add(var3.func_179259_c());
                  var25.add(var3.readFloat());
               }

               this.field_147299_f.field_184132_p.field_201747_g.func_201742_a(var18, var24, var25);
            } else if (SPacketCustomPayload.field_209916_g.equals(var2)) {
               var4 = var3.readInt();
               MutableBoundingBox var22 = new MutableBoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt());
               int var26 = var3.readInt();
               var25 = Lists.newArrayList();
               ArrayList var27 = Lists.newArrayList();

               for(int var9 = 0; var9 < var26; ++var9) {
                  var25.add(new MutableBoundingBox(var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt(), var3.readInt()));
                  var27.add(var3.readBoolean());
               }

               this.field_147299_f.field_184132_p.field_201748_h.func_201729_a(var22, var25, var27, var4);
            } else if (SPacketCustomPayload.field_209917_h.equals(var2)) {
               ((DebugRendererWorldGenAttempts)this.field_147299_f.field_184132_p.field_201750_j).func_201734_a(var3.func_179259_c(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat(), var3.readFloat());
               field_147301_d.warn("Unknown custom packed identifier: {}", var2);
            } else {
               field_147301_d.warn("Unknown custom packed identifier: {}", var2);
            }
         }
      } finally {
         if (var3 != null) {
            var3.release();
         }

      }

   }

   public void func_147291_a(SPacketScoreboardObjective var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      String var3 = var1.func_149339_c();
      if (var1.func_149338_e() == 0) {
         var2.func_199868_a(var3, ScoreCriteria.field_96641_b, var1.func_149337_d(), var1.func_199856_d());
      } else if (var2.func_197900_b(var3)) {
         ScoreObjective var4 = var2.func_96518_b(var3);
         if (var1.func_149338_e() == 1) {
            var2.func_96519_k(var4);
         } else if (var1.func_149338_e() == 2) {
            var4.func_199866_a(var1.func_199856_d());
            var4.func_199864_a(var1.func_149337_d());
         }
      }

   }

   public void func_147250_a(SPacketUpdateScore var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      String var3 = var1.func_149321_d();
      switch(var1.func_197701_d()) {
      case CHANGE:
         ScoreObjective var4 = var2.func_197899_c(var3);
         Score var5 = var2.func_96529_a(var1.func_149324_c(), var4);
         var5.func_96647_c(var1.func_149323_e());
         break;
      case REMOVE:
         var2.func_178822_d(var1.func_149324_c(), var2.func_96518_b(var3));
      }

   }

   public void func_147254_a(SPacketDisplayObjective var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Scoreboard var2 = this.field_147300_g.func_96441_U();
      String var3 = var1.func_149370_d();
      ScoreObjective var4 = var3 == null ? null : var2.func_197899_c(var3);
      var2.func_96530_a(var1.func_149371_c(), var4);
   }

   public void func_147247_a(SPacketTeams var1) {
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
         var3.func_178774_a(var1.func_200537_f());
         var3.func_98298_a(var1.func_149308_i());
         Team.EnumVisible var4 = Team.EnumVisible.func_178824_a(var1.func_179814_i());
         if (var4 != null) {
            var3.func_178772_a(var4);
         }

         Team.CollisionRule var5 = Team.CollisionRule.func_186686_a(var1.func_186975_j());
         if (var5 != null) {
            var3.func_186682_a(var5);
         }

         var3.func_207408_a(var1.func_207507_i());
         var3.func_207409_b(var1.func_207508_j());
      }

      Iterator var6;
      String var7;
      if (var1.func_149307_h() == 0 || var1.func_149307_h() == 3) {
         var6 = var1.func_149310_g().iterator();

         while(var6.hasNext()) {
            var7 = (String)var6.next();
            var2.func_197901_a(var7, var3);
         }
      }

      if (var1.func_149307_h() == 4) {
         var6 = var1.func_149310_g().iterator();

         while(var6.hasNext()) {
            var7 = (String)var6.next();
            var2.func_96512_b(var7, var3);
         }
      }

      if (var1.func_149307_h() == 1) {
         var2.func_96511_d(var3);
      }

   }

   public void func_147289_a(SPacketParticles var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      if (var1.func_149222_k() == 0) {
         double var2 = (double)(var1.func_149227_j() * var1.func_149221_g());
         double var4 = (double)(var1.func_149227_j() * var1.func_149224_h());
         double var6 = (double)(var1.func_149227_j() * var1.func_149223_i());

         try {
            this.field_147300_g.func_195590_a(var1.func_197699_j(), var1.func_179750_b(), var1.func_149220_d(), var1.func_149226_e(), var1.func_149225_f(), var2, var4, var6);
         } catch (Throwable var17) {
            field_147301_d.warn("Could not spawn particle effect {}", var1.func_197699_j());
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
               this.field_147300_g.func_195590_a(var1.func_197699_j(), var1.func_179750_b(), var1.func_149220_d() + var3, var1.func_149226_e() + var5, var1.func_149225_f() + var7, var9, var11, var13);
            } catch (Throwable var16) {
               field_147301_d.warn("Could not spawn particle effect {}", var1.func_197699_j());
               return;
            }
         }
      }

   }

   public void func_147290_a(SPacketEntityProperties var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Entity var2 = this.field_147300_g.func_73045_a(var1.func_149442_c());
      if (var2 != null) {
         if (!(var2 instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + var2 + ")");
         } else {
            AbstractAttributeMap var3 = ((EntityLivingBase)var2).func_110140_aT();
            Iterator var4 = var1.func_149441_d().iterator();

            while(var4.hasNext()) {
               SPacketEntityProperties.Snapshot var5 = (SPacketEntityProperties.Snapshot)var4.next();
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

   public void func_194307_a(SPacketPlaceGhostRecipe var1) {
      PacketThreadUtil.func_180031_a(var1, this, this.field_147299_f);
      Container var2 = this.field_147299_f.field_71439_g.field_71070_bA;
      if (var2.field_75152_c == var1.func_194313_b() && var2.func_75129_b(this.field_147299_f.field_71439_g)) {
         IRecipe var3 = this.field_199528_o.func_199517_a(var1.func_199615_a());
         if (var3 != null) {
            if (this.field_147299_f.field_71462_r instanceof IRecipeShownListener) {
               GuiRecipeBook var4 = ((IRecipeShownListener)this.field_147299_f.field_71462_r).func_194310_f();
               var4.func_193951_a(var3, var2.field_75151_b);
            } else if (this.field_147299_f.field_71462_r instanceof GuiFurnace) {
               ((GuiFurnace)this.field_147299_f.field_71462_r).field_201557_v.func_193951_a(var3, var2.field_75151_b);
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

   @Nullable
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

   public ClientAdvancementManager func_191982_f() {
      return this.field_191983_k;
   }

   public CommandDispatcher<ISuggestionProvider> func_195515_i() {
      return this.field_195517_n;
   }

   public WorldClient func_195514_j() {
      return this.field_147300_g;
   }

   public NetworkTagManager func_199724_l() {
      return this.field_199725_m;
   }

   public NBTQueryManager func_211523_k() {
      return this.field_211524_l;
   }
}
