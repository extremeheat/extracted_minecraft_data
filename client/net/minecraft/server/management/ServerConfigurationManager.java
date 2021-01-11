package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager {
   public static final File field_152613_a = new File("banned-players.json");
   public static final File field_152614_b = new File("banned-ips.json");
   public static final File field_152615_c = new File("ops.json");
   public static final File field_152616_d = new File("whitelist.json");
   private static final Logger field_148546_d = LogManager.getLogger();
   private static final SimpleDateFormat field_72403_e = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer field_72400_f;
   private final List<EntityPlayerMP> field_72404_b = Lists.newArrayList();
   private final Map<UUID, EntityPlayerMP> field_177454_f = Maps.newHashMap();
   private final UserListBans field_72401_g;
   private final BanList field_72413_h;
   private final UserListOps field_72414_i;
   private final UserListWhitelist field_72411_j;
   private final Map<UUID, StatisticsFile> field_148547_k;
   private IPlayerFileData field_72412_k;
   private boolean field_72409_l;
   protected int field_72405_c;
   private int field_72402_d;
   private WorldSettings.GameType field_72410_m;
   private boolean field_72407_n;
   private int field_72408_o;

   public ServerConfigurationManager(MinecraftServer var1) {
      super();
      this.field_72401_g = new UserListBans(field_152613_a);
      this.field_72413_h = new BanList(field_152614_b);
      this.field_72414_i = new UserListOps(field_152615_c);
      this.field_72411_j = new UserListWhitelist(field_152616_d);
      this.field_148547_k = Maps.newHashMap();
      this.field_72400_f = var1;
      this.field_72401_g.func_152686_a(false);
      this.field_72413_h.func_152686_a(false);
      this.field_72405_c = 8;
   }

   public void func_72355_a(NetworkManager var1, EntityPlayerMP var2) {
      GameProfile var3 = var2.func_146103_bH();
      PlayerProfileCache var4 = this.field_72400_f.func_152358_ax();
      GameProfile var5 = var4.func_152652_a(var3.getId());
      String var6 = var5 == null ? var3.getName() : var5.getName();
      var4.func_152649_a(var3);
      NBTTagCompound var7 = this.func_72380_a(var2);
      var2.func_70029_a(this.field_72400_f.func_71218_a(var2.field_71093_bK));
      var2.field_71134_c.func_73080_a((WorldServer)var2.field_70170_p);
      String var8 = "local";
      if (var1.func_74430_c() != null) {
         var8 = var1.func_74430_c().toString();
      }

      field_148546_d.info(var2.func_70005_c_() + "[" + var8 + "] logged in with entity id " + var2.func_145782_y() + " at (" + var2.field_70165_t + ", " + var2.field_70163_u + ", " + var2.field_70161_v + ")");
      WorldServer var9 = this.field_72400_f.func_71218_a(var2.field_71093_bK);
      WorldInfo var10 = var9.func_72912_H();
      BlockPos var11 = var9.func_175694_M();
      this.func_72381_a(var2, (EntityPlayerMP)null, var9);
      NetHandlerPlayServer var12 = new NetHandlerPlayServer(this.field_72400_f, var1, var2);
      var12.func_147359_a(new S01PacketJoinGame(var2.func_145782_y(), var2.field_71134_c.func_73081_b(), var10.func_76093_s(), var9.field_73011_w.func_177502_q(), var9.func_175659_aa(), this.func_72352_l(), var10.func_76067_t(), var9.func_82736_K().func_82766_b("reducedDebugInfo")));
      var12.func_147359_a(new S3FPacketCustomPayload("MC|Brand", (new PacketBuffer(Unpooled.buffer())).func_180714_a(this.func_72365_p().getServerModName())));
      var12.func_147359_a(new S41PacketServerDifficulty(var10.func_176130_y(), var10.func_176123_z()));
      var12.func_147359_a(new S05PacketSpawnPosition(var11));
      var12.func_147359_a(new S39PacketPlayerAbilities(var2.field_71075_bZ));
      var12.func_147359_a(new S09PacketHeldItemChange(var2.field_71071_by.field_70461_c));
      var2.func_147099_x().func_150877_d();
      var2.func_147099_x().func_150884_b(var2);
      this.func_96456_a((ServerScoreboard)var9.func_96441_U(), var2);
      this.field_72400_f.func_147132_au();
      ChatComponentTranslation var13;
      if (!var2.func_70005_c_().equalsIgnoreCase(var6)) {
         var13 = new ChatComponentTranslation("multiplayer.player.joined.renamed", new Object[]{var2.func_145748_c_(), var6});
      } else {
         var13 = new ChatComponentTranslation("multiplayer.player.joined", new Object[]{var2.func_145748_c_()});
      }

      var13.func_150256_b().func_150238_a(EnumChatFormatting.YELLOW);
      this.func_148539_a(var13);
      this.func_72377_c(var2);
      var12.func_147364_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var2.field_70177_z, var2.field_70125_A);
      this.func_72354_b(var2, var9);
      if (this.field_72400_f.func_147133_T().length() > 0) {
         var2.func_175397_a(this.field_72400_f.func_147133_T(), this.field_72400_f.func_175581_ab());
      }

      Iterator var14 = var2.func_70651_bq().iterator();

      while(var14.hasNext()) {
         PotionEffect var15 = (PotionEffect)var14.next();
         var12.func_147359_a(new S1DPacketEntityEffect(var2.func_145782_y(), var15));
      }

      var2.func_71116_b();
      if (var7 != null && var7.func_150297_b("Riding", 10)) {
         Entity var16 = EntityList.func_75615_a(var7.func_74775_l("Riding"), var9);
         if (var16 != null) {
            var16.field_98038_p = true;
            var9.func_72838_d(var16);
            var2.func_70078_a(var16);
            var16.field_98038_p = false;
         }
      }

   }

   protected void func_96456_a(ServerScoreboard var1, EntityPlayerMP var2) {
      HashSet var3 = Sets.newHashSet();
      Iterator var4 = var1.func_96525_g().iterator();

      while(var4.hasNext()) {
         ScorePlayerTeam var5 = (ScorePlayerTeam)var4.next();
         var2.field_71135_a.func_147359_a(new S3EPacketTeams(var5, 0));
      }

      for(int var9 = 0; var9 < 19; ++var9) {
         ScoreObjective var10 = var1.func_96539_a(var9);
         if (var10 != null && !var3.contains(var10)) {
            List var6 = var1.func_96550_d(var10);
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               Packet var8 = (Packet)var7.next();
               var2.field_71135_a.func_147359_a(var8);
            }

            var3.add(var10);
         }
      }

   }

   public void func_72364_a(WorldServer[] var1) {
      this.field_72412_k = var1[0].func_72860_G().func_75756_e();
      var1[0].func_175723_af().func_177737_a(new IBorderListener() {
         public void func_177694_a(WorldBorder var1, double var2) {
            ServerConfigurationManager.this.func_148540_a(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.SET_SIZE));
         }

         public void func_177692_a(WorldBorder var1, double var2, double var4, long var6) {
            ServerConfigurationManager.this.func_148540_a(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.LERP_SIZE));
         }

         public void func_177693_a(WorldBorder var1, double var2, double var4) {
            ServerConfigurationManager.this.func_148540_a(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.SET_CENTER));
         }

         public void func_177691_a(WorldBorder var1, int var2) {
            ServerConfigurationManager.this.func_148540_a(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.SET_WARNING_TIME));
         }

         public void func_177690_b(WorldBorder var1, int var2) {
            ServerConfigurationManager.this.func_148540_a(new S44PacketWorldBorder(var1, S44PacketWorldBorder.Action.SET_WARNING_BLOCKS));
         }

         public void func_177696_b(WorldBorder var1, double var2) {
         }

         public void func_177695_c(WorldBorder var1, double var2) {
         }
      });
   }

   public void func_72375_a(EntityPlayerMP var1, WorldServer var2) {
      WorldServer var3 = var1.func_71121_q();
      if (var2 != null) {
         var2.func_73040_p().func_72695_c(var1);
      }

      var3.func_73040_p().func_72683_a(var1);
      var3.field_73059_b.func_73158_c((int)var1.field_70165_t >> 4, (int)var1.field_70161_v >> 4);
   }

   public int func_72372_a() {
      return PlayerManager.func_72686_a(this.func_72395_o());
   }

   public NBTTagCompound func_72380_a(EntityPlayerMP var1) {
      NBTTagCompound var2 = this.field_72400_f.field_71305_c[0].func_72912_H().func_76072_h();
      NBTTagCompound var3;
      if (var1.func_70005_c_().equals(this.field_72400_f.func_71214_G()) && var2 != null) {
         var1.func_70020_e(var2);
         var3 = var2;
         field_148546_d.debug("loading single player");
      } else {
         var3 = this.field_72412_k.func_75752_b(var1);
      }

      return var3;
   }

   protected void func_72391_b(EntityPlayerMP var1) {
      this.field_72412_k.func_75753_a(var1);
      StatisticsFile var2 = (StatisticsFile)this.field_148547_k.get(var1.func_110124_au());
      if (var2 != null) {
         var2.func_150883_b();
      }

   }

   public void func_72377_c(EntityPlayerMP var1) {
      this.field_72404_b.add(var1);
      this.field_177454_f.put(var1.func_110124_au(), var1);
      this.func_148540_a(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[]{var1}));
      WorldServer var2 = this.field_72400_f.func_71218_a(var1.field_71093_bK);
      var2.func_72838_d(var1);
      this.func_72375_a(var1, (WorldServer)null);

      for(int var3 = 0; var3 < this.field_72404_b.size(); ++var3) {
         EntityPlayerMP var4 = (EntityPlayerMP)this.field_72404_b.get(var3);
         var1.field_71135_a.func_147359_a(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[]{var4}));
      }

   }

   public void func_72358_d(EntityPlayerMP var1) {
      var1.func_71121_q().func_73040_p().func_72685_d(var1);
   }

   public void func_72367_e(EntityPlayerMP var1) {
      var1.func_71029_a(StatList.field_75947_j);
      this.func_72391_b(var1);
      WorldServer var2 = var1.func_71121_q();
      if (var1.field_70154_o != null) {
         var2.func_72973_f(var1.field_70154_o);
         field_148546_d.debug("removing player mount");
      }

      var2.func_72900_e(var1);
      var2.func_73040_p().func_72695_c(var1);
      this.field_72404_b.remove(var1);
      UUID var3 = var1.func_110124_au();
      EntityPlayerMP var4 = (EntityPlayerMP)this.field_177454_f.get(var3);
      if (var4 == var1) {
         this.field_177454_f.remove(var3);
         this.field_148547_k.remove(var3);
      }

      this.func_148540_a(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.REMOVE_PLAYER, new EntityPlayerMP[]{var1}));
   }

   public String func_148542_a(SocketAddress var1, GameProfile var2) {
      String var4;
      if (this.field_72401_g.func_152702_a(var2)) {
         UserListBansEntry var5 = (UserListBansEntry)this.field_72401_g.func_152683_b(var2);
         var4 = "You are banned from this server!\nReason: " + var5.func_73686_f();
         if (var5.func_73680_d() != null) {
            var4 = var4 + "\nYour ban will be removed on " + field_72403_e.format(var5.func_73680_d());
         }

         return var4;
      } else if (!this.func_152607_e(var2)) {
         return "You are not white-listed on this server!";
      } else if (this.field_72413_h.func_152708_a(var1)) {
         IPBanEntry var3 = this.field_72413_h.func_152709_b(var1);
         var4 = "Your IP address is banned from this server!\nReason: " + var3.func_73686_f();
         if (var3.func_73680_d() != null) {
            var4 = var4 + "\nYour ban will be removed on " + field_72403_e.format(var3.func_73680_d());
         }

         return var4;
      } else {
         return this.field_72404_b.size() >= this.field_72405_c && !this.func_183023_f(var2) ? "The server is full!" : null;
      }
   }

   public EntityPlayerMP func_148545_a(GameProfile var1) {
      UUID var2 = EntityPlayer.func_146094_a(var1);
      ArrayList var3 = Lists.newArrayList();

      for(int var4 = 0; var4 < this.field_72404_b.size(); ++var4) {
         EntityPlayerMP var5 = (EntityPlayerMP)this.field_72404_b.get(var4);
         if (var5.func_110124_au().equals(var2)) {
            var3.add(var5);
         }
      }

      EntityPlayerMP var7 = (EntityPlayerMP)this.field_177454_f.get(var1.getId());
      if (var7 != null && !var3.contains(var7)) {
         var3.add(var7);
      }

      Iterator var8 = var3.iterator();

      while(var8.hasNext()) {
         EntityPlayerMP var6 = (EntityPlayerMP)var8.next();
         var6.field_71135_a.func_147360_c("You logged in from another location");
      }

      Object var9;
      if (this.field_72400_f.func_71242_L()) {
         var9 = new DemoWorldManager(this.field_72400_f.func_71218_a(0));
      } else {
         var9 = new ItemInWorldManager(this.field_72400_f.func_71218_a(0));
      }

      return new EntityPlayerMP(this.field_72400_f, this.field_72400_f.func_71218_a(0), var1, (ItemInWorldManager)var9);
   }

   public EntityPlayerMP func_72368_a(EntityPlayerMP var1, int var2, boolean var3) {
      var1.func_71121_q().func_73039_n().func_72787_a(var1);
      var1.func_71121_q().func_73039_n().func_72790_b(var1);
      var1.func_71121_q().func_73040_p().func_72695_c(var1);
      this.field_72404_b.remove(var1);
      this.field_72400_f.func_71218_a(var1.field_71093_bK).func_72973_f(var1);
      BlockPos var4 = var1.func_180470_cg();
      boolean var5 = var1.func_82245_bX();
      var1.field_71093_bK = var2;
      Object var6;
      if (this.field_72400_f.func_71242_L()) {
         var6 = new DemoWorldManager(this.field_72400_f.func_71218_a(var1.field_71093_bK));
      } else {
         var6 = new ItemInWorldManager(this.field_72400_f.func_71218_a(var1.field_71093_bK));
      }

      EntityPlayerMP var7 = new EntityPlayerMP(this.field_72400_f, this.field_72400_f.func_71218_a(var1.field_71093_bK), var1.func_146103_bH(), (ItemInWorldManager)var6);
      var7.field_71135_a = var1.field_71135_a;
      var7.func_71049_a(var1, var3);
      var7.func_145769_d(var1.func_145782_y());
      var7.func_174817_o(var1);
      WorldServer var8 = this.field_72400_f.func_71218_a(var1.field_71093_bK);
      this.func_72381_a(var7, var1, var8);
      BlockPos var9;
      if (var4 != null) {
         var9 = EntityPlayer.func_180467_a(this.field_72400_f.func_71218_a(var1.field_71093_bK), var4, var5);
         if (var9 != null) {
            var7.func_70012_b((double)((float)var9.func_177958_n() + 0.5F), (double)((float)var9.func_177956_o() + 0.1F), (double)((float)var9.func_177952_p() + 0.5F), 0.0F, 0.0F);
            var7.func_180473_a(var4, var5);
         } else {
            var7.field_71135_a.func_147359_a(new S2BPacketChangeGameState(0, 0.0F));
         }
      }

      var8.field_73059_b.func_73158_c((int)var7.field_70165_t >> 4, (int)var7.field_70161_v >> 4);

      while(!var8.func_72945_a(var7, var7.func_174813_aQ()).isEmpty() && var7.field_70163_u < 256.0D) {
         var7.func_70107_b(var7.field_70165_t, var7.field_70163_u + 1.0D, var7.field_70161_v);
      }

      var7.field_71135_a.func_147359_a(new S07PacketRespawn(var7.field_71093_bK, var7.field_70170_p.func_175659_aa(), var7.field_70170_p.func_72912_H().func_76067_t(), var7.field_71134_c.func_73081_b()));
      var9 = var8.func_175694_M();
      var7.field_71135_a.func_147364_a(var7.field_70165_t, var7.field_70163_u, var7.field_70161_v, var7.field_70177_z, var7.field_70125_A);
      var7.field_71135_a.func_147359_a(new S05PacketSpawnPosition(var9));
      var7.field_71135_a.func_147359_a(new S1FPacketSetExperience(var7.field_71106_cc, var7.field_71067_cb, var7.field_71068_ca));
      this.func_72354_b(var7, var8);
      var8.func_73040_p().func_72683_a(var7);
      var8.func_72838_d(var7);
      this.field_72404_b.add(var7);
      this.field_177454_f.put(var7.func_110124_au(), var7);
      var7.func_71116_b();
      var7.func_70606_j(var7.func_110143_aJ());
      return var7;
   }

   public void func_72356_a(EntityPlayerMP var1, int var2) {
      int var3 = var1.field_71093_bK;
      WorldServer var4 = this.field_72400_f.func_71218_a(var1.field_71093_bK);
      var1.field_71093_bK = var2;
      WorldServer var5 = this.field_72400_f.func_71218_a(var1.field_71093_bK);
      var1.field_71135_a.func_147359_a(new S07PacketRespawn(var1.field_71093_bK, var1.field_70170_p.func_175659_aa(), var1.field_70170_p.func_72912_H().func_76067_t(), var1.field_71134_c.func_73081_b()));
      var4.func_72973_f(var1);
      var1.field_70128_L = false;
      this.func_82448_a(var1, var3, var4, var5);
      this.func_72375_a(var1, var4);
      var1.field_71135_a.func_147364_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var1.field_70177_z, var1.field_70125_A);
      var1.field_71134_c.func_73080_a(var5);
      this.func_72354_b(var1, var5);
      this.func_72385_f(var1);
      Iterator var6 = var1.func_70651_bq().iterator();

      while(var6.hasNext()) {
         PotionEffect var7 = (PotionEffect)var6.next();
         var1.field_71135_a.func_147359_a(new S1DPacketEntityEffect(var1.func_145782_y(), var7));
      }

   }

   public void func_82448_a(Entity var1, int var2, WorldServer var3, WorldServer var4) {
      double var5 = var1.field_70165_t;
      double var7 = var1.field_70161_v;
      double var9 = 8.0D;
      float var11 = var1.field_70177_z;
      var3.field_72984_F.func_76320_a("moving");
      if (var1.field_71093_bK == -1) {
         var5 = MathHelper.func_151237_a(var5 / var9, var4.func_175723_af().func_177726_b() + 16.0D, var4.func_175723_af().func_177728_d() - 16.0D);
         var7 = MathHelper.func_151237_a(var7 / var9, var4.func_175723_af().func_177736_c() + 16.0D, var4.func_175723_af().func_177733_e() - 16.0D);
         var1.func_70012_b(var5, var1.field_70163_u, var7, var1.field_70177_z, var1.field_70125_A);
         if (var1.func_70089_S()) {
            var3.func_72866_a(var1, false);
         }
      } else if (var1.field_71093_bK == 0) {
         var5 = MathHelper.func_151237_a(var5 * var9, var4.func_175723_af().func_177726_b() + 16.0D, var4.func_175723_af().func_177728_d() - 16.0D);
         var7 = MathHelper.func_151237_a(var7 * var9, var4.func_175723_af().func_177736_c() + 16.0D, var4.func_175723_af().func_177733_e() - 16.0D);
         var1.func_70012_b(var5, var1.field_70163_u, var7, var1.field_70177_z, var1.field_70125_A);
         if (var1.func_70089_S()) {
            var3.func_72866_a(var1, false);
         }
      } else {
         BlockPos var12;
         if (var2 == 1) {
            var12 = var4.func_175694_M();
         } else {
            var12 = var4.func_180504_m();
         }

         var5 = (double)var12.func_177958_n();
         var1.field_70163_u = (double)var12.func_177956_o();
         var7 = (double)var12.func_177952_p();
         var1.func_70012_b(var5, var1.field_70163_u, var7, 90.0F, 0.0F);
         if (var1.func_70089_S()) {
            var3.func_72866_a(var1, false);
         }
      }

      var3.field_72984_F.func_76319_b();
      if (var2 != 1) {
         var3.field_72984_F.func_76320_a("placing");
         var5 = (double)MathHelper.func_76125_a((int)var5, -29999872, 29999872);
         var7 = (double)MathHelper.func_76125_a((int)var7, -29999872, 29999872);
         if (var1.func_70089_S()) {
            var1.func_70012_b(var5, var1.field_70163_u, var7, var1.field_70177_z, var1.field_70125_A);
            var4.func_85176_s().func_180266_a(var1, var11);
            var4.func_72838_d(var1);
            var4.func_72866_a(var1, false);
         }

         var3.field_72984_F.func_76319_b();
      }

      var1.func_70029_a(var4);
   }

   public void func_72374_b() {
      if (++this.field_72408_o > 600) {
         this.func_148540_a(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_LATENCY, this.field_72404_b));
         this.field_72408_o = 0;
      }

   }

   public void func_148540_a(Packet var1) {
      for(int var2 = 0; var2 < this.field_72404_b.size(); ++var2) {
         ((EntityPlayerMP)this.field_72404_b.get(var2)).field_71135_a.func_147359_a(var1);
      }

   }

   public void func_148537_a(Packet var1, int var2) {
      for(int var3 = 0; var3 < this.field_72404_b.size(); ++var3) {
         EntityPlayerMP var4 = (EntityPlayerMP)this.field_72404_b.get(var3);
         if (var4.field_71093_bK == var2) {
            var4.field_71135_a.func_147359_a(var1);
         }
      }

   }

   public void func_177453_a(EntityPlayer var1, IChatComponent var2) {
      Team var3 = var1.func_96124_cp();
      if (var3 != null) {
         Collection var4 = var3.func_96670_d();
         Iterator var5 = var4.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            EntityPlayerMP var7 = this.func_152612_a(var6);
            if (var7 != null && var7 != var1) {
               var7.func_145747_a(var2);
            }
         }

      }
   }

   public void func_177452_b(EntityPlayer var1, IChatComponent var2) {
      Team var3 = var1.func_96124_cp();
      if (var3 == null) {
         this.func_148539_a(var2);
      } else {
         for(int var4 = 0; var4 < this.field_72404_b.size(); ++var4) {
            EntityPlayerMP var5 = (EntityPlayerMP)this.field_72404_b.get(var4);
            if (var5.func_96124_cp() != var3) {
               var5.func_145747_a(var2);
            }
         }

      }
   }

   public String func_181058_b(boolean var1) {
      String var2 = "";
      ArrayList var3 = Lists.newArrayList(this.field_72404_b);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         if (var4 > 0) {
            var2 = var2 + ", ";
         }

         var2 = var2 + ((EntityPlayerMP)var3.get(var4)).func_70005_c_();
         if (var1) {
            var2 = var2 + " (" + ((EntityPlayerMP)var3.get(var4)).func_110124_au().toString() + ")";
         }
      }

      return var2;
   }

   public String[] func_72369_d() {
      String[] var1 = new String[this.field_72404_b.size()];

      for(int var2 = 0; var2 < this.field_72404_b.size(); ++var2) {
         var1[var2] = ((EntityPlayerMP)this.field_72404_b.get(var2)).func_70005_c_();
      }

      return var1;
   }

   public GameProfile[] func_152600_g() {
      GameProfile[] var1 = new GameProfile[this.field_72404_b.size()];

      for(int var2 = 0; var2 < this.field_72404_b.size(); ++var2) {
         var1[var2] = ((EntityPlayerMP)this.field_72404_b.get(var2)).func_146103_bH();
      }

      return var1;
   }

   public UserListBans func_152608_h() {
      return this.field_72401_g;
   }

   public BanList func_72363_f() {
      return this.field_72413_h;
   }

   public void func_152605_a(GameProfile var1) {
      this.field_72414_i.func_152687_a(new UserListOpsEntry(var1, this.field_72400_f.func_110455_j(), this.field_72414_i.func_183026_b(var1)));
   }

   public void func_152610_b(GameProfile var1) {
      this.field_72414_i.func_152684_c(var1);
   }

   public boolean func_152607_e(GameProfile var1) {
      return !this.field_72409_l || this.field_72414_i.func_152692_d(var1) || this.field_72411_j.func_152692_d(var1);
   }

   public boolean func_152596_g(GameProfile var1) {
      return this.field_72414_i.func_152692_d(var1) || this.field_72400_f.func_71264_H() && this.field_72400_f.field_71305_c[0].func_72912_H().func_76086_u() && this.field_72400_f.func_71214_G().equalsIgnoreCase(var1.getName()) || this.field_72407_n;
   }

   public EntityPlayerMP func_152612_a(String var1) {
      Iterator var2 = this.field_72404_b.iterator();

      EntityPlayerMP var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (EntityPlayerMP)var2.next();
      } while(!var3.func_70005_c_().equalsIgnoreCase(var1));

      return var3;
   }

   public void func_148541_a(double var1, double var3, double var5, double var7, int var9, Packet var10) {
      this.func_148543_a((EntityPlayer)null, var1, var3, var5, var7, var9, var10);
   }

   public void func_148543_a(EntityPlayer var1, double var2, double var4, double var6, double var8, int var10, Packet var11) {
      for(int var12 = 0; var12 < this.field_72404_b.size(); ++var12) {
         EntityPlayerMP var13 = (EntityPlayerMP)this.field_72404_b.get(var12);
         if (var13 != var1 && var13.field_71093_bK == var10) {
            double var14 = var2 - var13.field_70165_t;
            double var16 = var4 - var13.field_70163_u;
            double var18 = var6 - var13.field_70161_v;
            if (var14 * var14 + var16 * var16 + var18 * var18 < var8 * var8) {
               var13.field_71135_a.func_147359_a(var11);
            }
         }
      }

   }

   public void func_72389_g() {
      for(int var1 = 0; var1 < this.field_72404_b.size(); ++var1) {
         this.func_72391_b((EntityPlayerMP)this.field_72404_b.get(var1));
      }

   }

   public void func_152601_d(GameProfile var1) {
      this.field_72411_j.func_152687_a(new UserListWhitelistEntry(var1));
   }

   public void func_152597_c(GameProfile var1) {
      this.field_72411_j.func_152684_c(var1);
   }

   public UserListWhitelist func_152599_k() {
      return this.field_72411_j;
   }

   public String[] func_152598_l() {
      return this.field_72411_j.func_152685_a();
   }

   public UserListOps func_152603_m() {
      return this.field_72414_i;
   }

   public String[] func_152606_n() {
      return this.field_72414_i.func_152685_a();
   }

   public void func_72362_j() {
   }

   public void func_72354_b(EntityPlayerMP var1, WorldServer var2) {
      WorldBorder var3 = this.field_72400_f.field_71305_c[0].func_175723_af();
      var1.field_71135_a.func_147359_a(new S44PacketWorldBorder(var3, S44PacketWorldBorder.Action.INITIALIZE));
      var1.field_71135_a.func_147359_a(new S03PacketTimeUpdate(var2.func_82737_E(), var2.func_72820_D(), var2.func_82736_K().func_82766_b("doDaylightCycle")));
      if (var2.func_72896_J()) {
         var1.field_71135_a.func_147359_a(new S2BPacketChangeGameState(1, 0.0F));
         var1.field_71135_a.func_147359_a(new S2BPacketChangeGameState(7, var2.func_72867_j(1.0F)));
         var1.field_71135_a.func_147359_a(new S2BPacketChangeGameState(8, var2.func_72819_i(1.0F)));
      }

   }

   public void func_72385_f(EntityPlayerMP var1) {
      var1.func_71120_a(var1.field_71069_bz);
      var1.func_71118_n();
      var1.field_71135_a.func_147359_a(new S09PacketHeldItemChange(var1.field_71071_by.field_70461_c));
   }

   public int func_72394_k() {
      return this.field_72404_b.size();
   }

   public int func_72352_l() {
      return this.field_72405_c;
   }

   public String[] func_72373_m() {
      return this.field_72400_f.field_71305_c[0].func_72860_G().func_75756_e().func_75754_f();
   }

   public void func_72371_a(boolean var1) {
      this.field_72409_l = var1;
   }

   public List<EntityPlayerMP> func_72382_j(String var1) {
      ArrayList var2 = Lists.newArrayList();
      Iterator var3 = this.field_72404_b.iterator();

      while(var3.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
         if (var4.func_71114_r().equals(var1)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   public int func_72395_o() {
      return this.field_72402_d;
   }

   public MinecraftServer func_72365_p() {
      return this.field_72400_f;
   }

   public NBTTagCompound func_72378_q() {
      return null;
   }

   public void func_152604_a(WorldSettings.GameType var1) {
      this.field_72410_m = var1;
   }

   private void func_72381_a(EntityPlayerMP var1, EntityPlayerMP var2, World var3) {
      if (var2 != null) {
         var1.field_71134_c.func_73076_a(var2.field_71134_c.func_73081_b());
      } else if (this.field_72410_m != null) {
         var1.field_71134_c.func_73076_a(this.field_72410_m);
      }

      var1.field_71134_c.func_73077_b(var3.func_72912_H().func_76077_q());
   }

   public void func_72387_b(boolean var1) {
      this.field_72407_n = var1;
   }

   public void func_72392_r() {
      for(int var1 = 0; var1 < this.field_72404_b.size(); ++var1) {
         ((EntityPlayerMP)this.field_72404_b.get(var1)).field_71135_a.func_147360_c("Server closed");
      }

   }

   public void func_148544_a(IChatComponent var1, boolean var2) {
      this.field_72400_f.func_145747_a(var1);
      int var3 = var2 ? 1 : 0;
      this.func_148540_a(new S02PacketChat(var1, (byte)var3));
   }

   public void func_148539_a(IChatComponent var1) {
      this.func_148544_a(var1, true);
   }

   public StatisticsFile func_152602_a(EntityPlayer var1) {
      UUID var2 = var1.func_110124_au();
      StatisticsFile var3 = var2 == null ? null : (StatisticsFile)this.field_148547_k.get(var2);
      if (var3 == null) {
         File var4 = new File(this.field_72400_f.func_71218_a(0).func_72860_G().func_75765_b(), "stats");
         File var5 = new File(var4, var2.toString() + ".json");
         if (!var5.exists()) {
            File var6 = new File(var4, var1.func_70005_c_() + ".json");
            if (var6.exists() && var6.isFile()) {
               var6.renameTo(var5);
            }
         }

         var3 = new StatisticsFile(this.field_72400_f, var5);
         var3.func_150882_a();
         this.field_148547_k.put(var2, var3);
      }

      return var3;
   }

   public void func_152611_a(int var1) {
      this.field_72402_d = var1;
      if (this.field_72400_f.field_71305_c != null) {
         WorldServer[] var2 = this.field_72400_f.field_71305_c;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            WorldServer var5 = var2[var4];
            if (var5 != null) {
               var5.func_73040_p().func_152622_a(var1);
            }
         }

      }
   }

   public List<EntityPlayerMP> func_181057_v() {
      return this.field_72404_b;
   }

   public EntityPlayerMP func_177451_a(UUID var1) {
      return (EntityPlayerMP)this.field_177454_f.get(var1);
   }

   public boolean func_183023_f(GameProfile var1) {
      return false;
   }
}
