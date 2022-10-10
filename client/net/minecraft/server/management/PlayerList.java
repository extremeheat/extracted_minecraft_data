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
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketTagsList;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketUpdateRecipes;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
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
   private final UserListIPBans field_72413_h;
   private final UserListOps field_72414_i;
   private final UserListWhitelist field_72411_j;
   private final Map<UUID, StatisticsManagerServer> field_148547_k;
   private final Map<UUID, PlayerAdvancements> field_192055_p;
   private IPlayerFileData field_72412_k;
   private boolean field_72409_l;
   protected int field_72405_c;
   private int field_72402_d;
   private GameType field_72410_m;
   private boolean field_72407_n;
   private int field_72408_o;

   public PlayerList(MinecraftServer var1) {
      super();
      this.field_72401_g = new UserListBans(field_152613_a);
      this.field_72413_h = new UserListIPBans(field_152614_b);
      this.field_72414_i = new UserListOps(field_152615_c);
      this.field_72411_j = new UserListWhitelist(field_152616_d);
      this.field_148547_k = Maps.newHashMap();
      this.field_192055_p = Maps.newHashMap();
      this.field_72400_f = var1;
      this.func_152608_h().func_152686_a(true);
      this.func_72363_f().func_152686_a(true);
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

      field_148546_d.info("{}[{}] logged in with entity id {} at ({}, {}, {})", var2.func_200200_C_().getString(), var8, var2.func_145782_y(), var2.field_70165_t, var2.field_70163_u, var2.field_70161_v);
      WorldServer var9 = this.field_72400_f.func_71218_a(var2.field_71093_bK);
      WorldInfo var10 = var9.func_72912_H();
      this.func_72381_a(var2, (EntityPlayerMP)null, var9);
      NetHandlerPlayServer var11 = new NetHandlerPlayServer(this.field_72400_f, var1, var2);
      var11.func_147359_a(new SPacketJoinGame(var2.func_145782_y(), var2.field_71134_c.func_73081_b(), var10.func_76093_s(), var9.field_73011_w.func_186058_p(), var9.func_175659_aa(), this.func_72352_l(), var10.func_76067_t(), var9.func_82736_K().func_82766_b("reducedDebugInfo")));
      var11.func_147359_a(new SPacketCustomPayload(SPacketCustomPayload.field_209911_b, (new PacketBuffer(Unpooled.buffer())).func_180714_a(this.func_72365_p().getServerModName())));
      var11.func_147359_a(new SPacketServerDifficulty(var10.func_176130_y(), var10.func_176123_z()));
      var11.func_147359_a(new SPacketPlayerAbilities(var2.field_71075_bZ));
      var11.func_147359_a(new SPacketHeldItemChange(var2.field_71071_by.field_70461_c));
      var11.func_147359_a(new SPacketUpdateRecipes(this.field_72400_f.func_199529_aN().func_199510_b()));
      var11.func_147359_a(new SPacketTagsList(this.field_72400_f.func_199731_aO()));
      this.func_187243_f(var2);
      var2.func_147099_x().func_150877_d();
      var2.func_192037_E().func_192826_c(var2);
      this.func_96456_a(var9.func_96441_U(), var2);
      this.field_72400_f.func_147132_au();
      TextComponentTranslation var12;
      if (var2.func_146103_bH().getName().equalsIgnoreCase(var6)) {
         var12 = new TextComponentTranslation("multiplayer.player.joined", new Object[]{var2.func_145748_c_()});
      } else {
         var12 = new TextComponentTranslation("multiplayer.player.joined.renamed", new Object[]{var2.func_145748_c_(), var6});
      }

      this.func_148539_a(var12.func_211708_a(TextFormatting.YELLOW));
      this.func_72377_c(var2);
      var11.func_147364_a(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var2.field_70177_z, var2.field_70125_A);
      this.func_72354_b(var2, var9);
      if (!this.field_72400_f.func_147133_T().isEmpty()) {
         var2.func_175397_a(this.field_72400_f.func_147133_T(), this.field_72400_f.func_175581_ab());
      }

      Iterator var13 = var2.func_70651_bq().iterator();

      while(var13.hasNext()) {
         PotionEffect var14 = (PotionEffect)var13.next();
         var11.func_147359_a(new SPacketEntityEffect(var2.func_145782_y(), var14));
      }

      if (var7 != null && var7.func_150297_b("RootVehicle", 10)) {
         NBTTagCompound var18 = var7.func_74775_l("RootVehicle");
         Entity var19 = AnvilChunkLoader.func_186051_a(var18.func_74775_l("Entity"), var9, true);
         if (var19 != null) {
            UUID var15 = var18.func_186857_a("Attach");
            Iterator var16;
            Entity var17;
            if (var19.func_110124_au().equals(var15)) {
               var2.func_184205_a(var19, true);
            } else {
               var16 = var19.func_184182_bu().iterator();

               while(var16.hasNext()) {
                  var17 = (Entity)var16.next();
                  if (var17.func_110124_au().equals(var15)) {
                     var2.func_184205_a(var17, true);
                     break;
                  }
               }
            }

            if (!var2.func_184218_aH()) {
               field_148546_d.warn("Couldn't reattach entity to player");
               var9.func_72973_f(var19);
               var16 = var19.func_184182_bu().iterator();

               while(var16.hasNext()) {
                  var17 = (Entity)var16.next();
                  var9.func_72973_f(var17);
               }
            }
         }
      }

      var2.func_71116_b();
   }

   protected void func_96456_a(ServerScoreboard var1, EntityPlayerMP var2) {
      HashSet var3 = Sets.newHashSet();
      Iterator var4 = var1.func_96525_g().iterator();

      while(var4.hasNext()) {
         ScorePlayerTeam var5 = (ScorePlayerTeam)var4.next();
         var2.field_71135_a.func_147359_a(new SPacketTeams(var5, 0));
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

   public void func_212504_a(WorldServer var1) {
      this.field_72412_k = var1.func_72860_G().func_75756_e();
      var1.func_175723_af().func_177737_a(new IBorderListener() {
         public void func_177694_a(WorldBorder var1, double var2) {
            PlayerList.this.func_148540_a(new SPacketWorldBorder(var1, SPacketWorldBorder.Action.SET_SIZE));
         }

         public void func_177692_a(WorldBorder var1, double var2, double var4, long var6) {
            PlayerList.this.func_148540_a(new SPacketWorldBorder(var1, SPacketWorldBorder.Action.LERP_SIZE));
         }

         public void func_177693_a(WorldBorder var1, double var2, double var4) {
            PlayerList.this.func_148540_a(new SPacketWorldBorder(var1, SPacketWorldBorder.Action.SET_CENTER));
         }

         public void func_177691_a(WorldBorder var1, int var2) {
            PlayerList.this.func_148540_a(new SPacketWorldBorder(var1, SPacketWorldBorder.Action.SET_WARNING_TIME));
         }

         public void func_177690_b(WorldBorder var1, int var2) {
            PlayerList.this.func_148540_a(new SPacketWorldBorder(var1, SPacketWorldBorder.Action.SET_WARNING_BLOCKS));
         }

         public void func_177696_b(WorldBorder var1, double var2) {
         }

         public void func_177695_c(WorldBorder var1, double var2) {
         }
      });
   }

   public void func_72375_a(EntityPlayerMP var1, @Nullable WorldServer var2) {
      WorldServer var3 = var1.func_71121_q();
      if (var2 != null) {
         var2.func_184164_w().func_72695_c(var1);
      }

      var3.func_184164_w().func_72683_a(var1);
      var3.func_72863_F().func_186025_d((int)var1.field_70165_t >> 4, (int)var1.field_70161_v >> 4, true, true);
      if (var2 != null) {
         CriteriaTriggers.field_193134_u.func_193143_a(var1, var2.field_73011_w.func_186058_p(), var3.field_73011_w.func_186058_p());
         if (var2.field_73011_w.func_186058_p() == DimensionType.NETHER && var1.field_70170_p.field_73011_w.func_186058_p() == DimensionType.OVERWORLD && var1.func_193106_Q() != null) {
            CriteriaTriggers.field_193131_B.func_193168_a(var1, var1.func_193106_Q());
         }
      }

   }

   public int func_72372_a() {
      return PlayerChunkMap.func_72686_a(this.func_72395_o());
   }

   @Nullable
   public NBTTagCompound func_72380_a(EntityPlayerMP var1) {
      NBTTagCompound var2 = this.field_72400_f.func_71218_a(DimensionType.OVERWORLD).func_72912_H().func_76072_h();
      NBTTagCompound var3;
      if (var1.func_200200_C_().getString().equals(this.field_72400_f.func_71214_G()) && var2 != null) {
         var3 = var2;
         var1.func_70020_e(var2);
         field_148546_d.debug("loading single player");
      } else {
         var3 = this.field_72412_k.func_75752_b(var1);
      }

      return var3;
   }

   protected void func_72391_b(EntityPlayerMP var1) {
      this.field_72412_k.func_75753_a(var1);
      StatisticsManagerServer var2 = (StatisticsManagerServer)this.field_148547_k.get(var1.func_110124_au());
      if (var2 != null) {
         var2.func_150883_b();
      }

      PlayerAdvancements var3 = (PlayerAdvancements)this.field_192055_p.get(var1.func_110124_au());
      if (var3 != null) {
         var3.func_192749_b();
      }

   }

   public void func_72377_c(EntityPlayerMP var1) {
      this.field_72404_b.add(var1);
      this.field_177454_f.put(var1.func_110124_au(), var1);
      this.func_148540_a(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[]{var1}));
      WorldServer var2 = this.field_72400_f.func_71218_a(var1.field_71093_bK);

      for(int var3 = 0; var3 < this.field_72404_b.size(); ++var3) {
         var1.field_71135_a.func_147359_a(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[]{(EntityPlayerMP)this.field_72404_b.get(var3)}));
      }

      var2.func_72838_d(var1);
      this.func_72375_a(var1, (WorldServer)null);
      this.field_72400_f.func_201300_aS().func_201383_a(var1);
   }

   public void func_72358_d(EntityPlayerMP var1) {
      var1.func_71121_q().func_184164_w().func_72685_d(var1);
   }

   public void func_72367_e(EntityPlayerMP var1) {
      WorldServer var2 = var1.func_71121_q();
      var1.func_195066_a(StatList.field_75947_j);
      this.func_72391_b(var1);
      if (var1.func_184218_aH()) {
         Entity var3 = var1.func_184208_bv();
         if (var3.func_200601_bK()) {
            field_148546_d.debug("Removing player mount");
            var1.func_184210_p();
            var2.func_72973_f(var3);
            Iterator var4 = var3.func_184182_bu().iterator();

            while(var4.hasNext()) {
               Entity var5 = (Entity)var4.next();
               var2.func_72973_f(var5);
            }

            var2.func_72964_e(var1.field_70176_ah, var1.field_70164_aj).func_76630_e();
         }
      }

      var2.func_72900_e(var1);
      var2.func_184164_w().func_72695_c(var1);
      var1.func_192039_O().func_192745_a();
      this.field_72404_b.remove(var1);
      this.field_72400_f.func_201300_aS().func_201382_b(var1);
      UUID var6 = var1.func_110124_au();
      EntityPlayerMP var7 = (EntityPlayerMP)this.field_177454_f.get(var6);
      if (var7 == var1) {
         this.field_177454_f.remove(var6);
         this.field_148547_k.remove(var6);
         this.field_192055_p.remove(var6);
      }

      this.func_148540_a(new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER, new EntityPlayerMP[]{var1}));
   }

   @Nullable
   public ITextComponent func_206258_a(SocketAddress var1, GameProfile var2) {
      TextComponentTranslation var4;
      if (this.field_72401_g.func_152702_a(var2)) {
         UserListBansEntry var5 = (UserListBansEntry)this.field_72401_g.func_152683_b(var2);
         var4 = new TextComponentTranslation("multiplayer.disconnect.banned.reason", new Object[]{var5.func_73686_f()});
         if (var5.func_73680_d() != null) {
            var4.func_150257_a(new TextComponentTranslation("multiplayer.disconnect.banned.expiration", new Object[]{field_72403_e.format(var5.func_73680_d())}));
         }

         return var4;
      } else if (!this.func_152607_e(var2)) {
         return new TextComponentTranslation("multiplayer.disconnect.not_whitelisted", new Object[0]);
      } else if (this.field_72413_h.func_152708_a(var1)) {
         UserListIPBansEntry var3 = this.field_72413_h.func_152709_b(var1);
         var4 = new TextComponentTranslation("multiplayer.disconnect.banned_ip.reason", new Object[]{var3.func_73686_f()});
         if (var3.func_73680_d() != null) {
            var4.func_150257_a(new TextComponentTranslation("multiplayer.disconnect.banned_ip.expiration", new Object[]{field_72403_e.format(var3.func_73680_d())}));
         }

         return var4;
      } else {
         return this.field_72404_b.size() >= this.field_72405_c && !this.func_183023_f(var2) ? new TextComponentTranslation("multiplayer.disconnect.server_full", new Object[0]) : null;
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
         var6.field_71135_a.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.duplicate_login", new Object[0]));
      }

      Object var9;
      if (this.field_72400_f.func_71242_L()) {
         var9 = new DemoPlayerInteractionManager(this.field_72400_f.func_71218_a(DimensionType.OVERWORLD));
      } else {
         var9 = new PlayerInteractionManager(this.field_72400_f.func_71218_a(DimensionType.OVERWORLD));
      }

      return new EntityPlayerMP(this.field_72400_f, this.field_72400_f.func_71218_a(DimensionType.OVERWORLD), var1, (PlayerInteractionManager)var9);
   }

   public EntityPlayerMP func_72368_a(EntityPlayerMP var1, DimensionType var2, boolean var3) {
      var1.func_71121_q().func_73039_n().func_72787_a(var1);
      var1.func_71121_q().func_73039_n().func_72790_b(var1);
      var1.func_71121_q().func_184164_w().func_72695_c(var1);
      this.field_72404_b.remove(var1);
      this.field_72400_f.func_71218_a(var1.field_71093_bK).func_72973_f(var1);
      BlockPos var4 = var1.func_180470_cg();
      boolean var5 = var1.func_82245_bX();
      var1.field_71093_bK = var2;
      Object var6;
      if (this.field_72400_f.func_71242_L()) {
         var6 = new DemoPlayerInteractionManager(this.field_72400_f.func_71218_a(var1.field_71093_bK));
      } else {
         var6 = new PlayerInteractionManager(this.field_72400_f.func_71218_a(var1.field_71093_bK));
      }

      EntityPlayerMP var7 = new EntityPlayerMP(this.field_72400_f, this.field_72400_f.func_71218_a(var1.field_71093_bK), var1.func_146103_bH(), (PlayerInteractionManager)var6);
      var7.field_71135_a = var1.field_71135_a;
      var7.func_193104_a(var1, var3);
      var7.func_145769_d(var1.func_145782_y());
      var7.func_184819_a(var1.func_184591_cq());
      Iterator var8 = var1.func_184216_O().iterator();

      while(var8.hasNext()) {
         String var9 = (String)var8.next();
         var7.func_184211_a(var9);
      }

      WorldServer var10 = this.field_72400_f.func_71218_a(var1.field_71093_bK);
      this.func_72381_a(var7, var1, var10);
      BlockPos var11;
      if (var4 != null) {
         var11 = EntityPlayer.func_180467_a(this.field_72400_f.func_71218_a(var1.field_71093_bK), var4, var5);
         if (var11 != null) {
            var7.func_70012_b((double)((float)var11.func_177958_n() + 0.5F), (double)((float)var11.func_177956_o() + 0.1F), (double)((float)var11.func_177952_p() + 0.5F), 0.0F, 0.0F);
            var7.func_180473_a(var4, var5);
         } else {
            var7.field_71135_a.func_147359_a(new SPacketChangeGameState(0, 0.0F));
         }
      }

      var10.func_72863_F().func_186025_d((int)var7.field_70165_t >> 4, (int)var7.field_70161_v >> 4, true, true);

      while(!var10.func_195586_b(var7, var7.func_174813_aQ()) && var7.field_70163_u < 256.0D) {
         var7.func_70107_b(var7.field_70165_t, var7.field_70163_u + 1.0D, var7.field_70161_v);
      }

      var7.field_71135_a.func_147359_a(new SPacketRespawn(var7.field_71093_bK, var7.field_70170_p.func_175659_aa(), var7.field_70170_p.func_72912_H().func_76067_t(), var7.field_71134_c.func_73081_b()));
      var11 = var10.func_175694_M();
      var7.field_71135_a.func_147364_a(var7.field_70165_t, var7.field_70163_u, var7.field_70161_v, var7.field_70177_z, var7.field_70125_A);
      var7.field_71135_a.func_147359_a(new SPacketSpawnPosition(var11));
      var7.field_71135_a.func_147359_a(new SPacketSetExperience(var7.field_71106_cc, var7.field_71067_cb, var7.field_71068_ca));
      this.func_72354_b(var7, var10);
      this.func_187243_f(var7);
      var10.func_184164_w().func_72683_a(var7);
      var10.func_72838_d(var7);
      this.field_72404_b.add(var7);
      this.field_177454_f.put(var7.func_110124_au(), var7);
      var7.func_71116_b();
      var7.func_70606_j(var7.func_110143_aJ());
      return var7;
   }

   public void func_187243_f(EntityPlayerMP var1) {
      GameProfile var2 = var1.func_146103_bH();
      int var3 = this.field_72400_f.func_211833_a(var2);
      this.func_187245_a(var1, var3);
   }

   public void func_187242_a(EntityPlayerMP var1, DimensionType var2) {
      DimensionType var3 = var1.field_71093_bK;
      WorldServer var4 = this.field_72400_f.func_71218_a(var1.field_71093_bK);
      var1.field_71093_bK = var2;
      WorldServer var5 = this.field_72400_f.func_71218_a(var1.field_71093_bK);
      var1.field_71135_a.func_147359_a(new SPacketRespawn(var1.field_71093_bK, var1.field_70170_p.func_175659_aa(), var1.field_70170_p.func_72912_H().func_76067_t(), var1.field_71134_c.func_73081_b()));
      this.func_187243_f(var1);
      var4.func_72973_f(var1);
      var1.field_70128_L = false;
      this.func_82448_a(var1, var3, var4, var5);
      this.func_72375_a(var1, var4);
      var1.field_71135_a.func_147364_a(var1.field_70165_t, var1.field_70163_u, var1.field_70161_v, var1.field_70177_z, var1.field_70125_A);
      var1.field_71134_c.func_73080_a(var5);
      var1.field_71135_a.func_147359_a(new SPacketPlayerAbilities(var1.field_71075_bZ));
      this.func_72354_b(var1, var5);
      this.func_72385_f(var1);
      Iterator var6 = var1.func_70651_bq().iterator();

      while(var6.hasNext()) {
         PotionEffect var7 = (PotionEffect)var6.next();
         var1.field_71135_a.func_147359_a(new SPacketEntityEffect(var1.func_145782_y(), var7));
      }

   }

   public void func_82448_a(Entity var1, DimensionType var2, WorldServer var3, WorldServer var4) {
      double var5 = var1.field_70165_t;
      double var7 = var1.field_70161_v;
      double var9 = 8.0D;
      float var11 = var1.field_70177_z;
      var3.field_72984_F.func_76320_a("moving");
      if (var1.field_71093_bK == DimensionType.NETHER) {
         var5 = MathHelper.func_151237_a(var5 / 8.0D, var4.func_175723_af().func_177726_b() + 16.0D, var4.func_175723_af().func_177728_d() - 16.0D);
         var7 = MathHelper.func_151237_a(var7 / 8.0D, var4.func_175723_af().func_177736_c() + 16.0D, var4.func_175723_af().func_177733_e() - 16.0D);
         var1.func_70012_b(var5, var1.field_70163_u, var7, var1.field_70177_z, var1.field_70125_A);
         if (var1.func_70089_S()) {
            var3.func_72866_a(var1, false);
         }
      } else if (var1.field_71093_bK == DimensionType.OVERWORLD) {
         var5 = MathHelper.func_151237_a(var5 * 8.0D, var4.func_175723_af().func_177726_b() + 16.0D, var4.func_175723_af().func_177728_d() - 16.0D);
         var7 = MathHelper.func_151237_a(var7 * 8.0D, var4.func_175723_af().func_177736_c() + 16.0D, var4.func_175723_af().func_177733_e() - 16.0D);
         var1.func_70012_b(var5, var1.field_70163_u, var7, var1.field_70177_z, var1.field_70125_A);
         if (var1.func_70089_S()) {
            var3.func_72866_a(var1, false);
         }
      } else {
         BlockPos var12;
         if (var2 == DimensionType.THE_END) {
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
      if (var2 != DimensionType.THE_END) {
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
         this.func_148540_a(new SPacketPlayerListItem(SPacketPlayerListItem.Action.UPDATE_LATENCY, this.field_72404_b));
         this.field_72408_o = 0;
      }

   }

   public void func_148540_a(Packet<?> var1) {
      for(int var2 = 0; var2 < this.field_72404_b.size(); ++var2) {
         ((EntityPlayerMP)this.field_72404_b.get(var2)).field_71135_a.func_147359_a(var1);
      }

   }

   public void func_148537_a(Packet<?> var1, DimensionType var2) {
      for(int var3 = 0; var3 < this.field_72404_b.size(); ++var3) {
         EntityPlayerMP var4 = (EntityPlayerMP)this.field_72404_b.get(var3);
         if (var4.field_71093_bK == var2) {
            var4.field_71135_a.func_147359_a(var1);
         }
      }

   }

   public void func_177453_a(EntityPlayer var1, ITextComponent var2) {
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

   public void func_177452_b(EntityPlayer var1, ITextComponent var2) {
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

   public String[] func_72369_d() {
      String[] var1 = new String[this.field_72404_b.size()];

      for(int var2 = 0; var2 < this.field_72404_b.size(); ++var2) {
         var1[var2] = ((EntityPlayerMP)this.field_72404_b.get(var2)).func_146103_bH().getName();
      }

      return var1;
   }

   public UserListBans func_152608_h() {
      return this.field_72401_g;
   }

   public UserListIPBans func_72363_f() {
      return this.field_72413_h;
   }

   public void func_152605_a(GameProfile var1) {
      this.field_72414_i.func_152687_a(new UserListOpsEntry(var1, this.field_72400_f.func_110455_j(), this.field_72414_i.func_183026_b(var1)));
      EntityPlayerMP var2 = this.func_177451_a(var1.getId());
      if (var2 != null) {
         this.func_187243_f(var2);
      }

   }

   public void func_152610_b(GameProfile var1) {
      this.field_72414_i.func_152684_c(var1);
      EntityPlayerMP var2 = this.func_177451_a(var1.getId());
      if (var2 != null) {
         this.func_187243_f(var2);
      }

   }

   private void func_187245_a(EntityPlayerMP var1, int var2) {
      if (var1.field_71135_a != null) {
         byte var3;
         if (var2 <= 0) {
            var3 = 24;
         } else if (var2 >= 4) {
            var3 = 28;
         } else {
            var3 = (byte)(24 + var2);
         }

         var1.field_71135_a.func_147359_a(new SPacketEntityStatus(var1, var3));
      }

      this.field_72400_f.func_195571_aL().func_197051_a(var1);
   }

   public boolean func_152607_e(GameProfile var1) {
      return !this.field_72409_l || this.field_72414_i.func_152692_d(var1) || this.field_72411_j.func_152692_d(var1);
   }

   public boolean func_152596_g(GameProfile var1) {
      return this.field_72414_i.func_152692_d(var1) || this.field_72400_f.func_71264_H() && this.field_72400_f.func_71218_a(DimensionType.OVERWORLD).func_72912_H().func_76086_u() && this.field_72400_f.func_71214_G().equalsIgnoreCase(var1.getName()) || this.field_72407_n;
   }

   @Nullable
   public EntityPlayerMP func_152612_a(String var1) {
      Iterator var2 = this.field_72404_b.iterator();

      EntityPlayerMP var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (EntityPlayerMP)var2.next();
      } while(!var3.func_146103_bH().getName().equalsIgnoreCase(var1));

      return var3;
   }

   public void func_148543_a(@Nullable EntityPlayer var1, double var2, double var4, double var6, double var8, DimensionType var10, Packet<?> var11) {
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

   public void func_187244_a() {
   }

   public void func_72354_b(EntityPlayerMP var1, WorldServer var2) {
      WorldBorder var3 = this.field_72400_f.func_71218_a(DimensionType.OVERWORLD).func_175723_af();
      var1.field_71135_a.func_147359_a(new SPacketWorldBorder(var3, SPacketWorldBorder.Action.INITIALIZE));
      var1.field_71135_a.func_147359_a(new SPacketTimeUpdate(var2.func_82737_E(), var2.func_72820_D(), var2.func_82736_K().func_82766_b("doDaylightCycle")));
      BlockPos var4 = var2.func_175694_M();
      var1.field_71135_a.func_147359_a(new SPacketSpawnPosition(var4));
      if (var2.func_72896_J()) {
         var1.field_71135_a.func_147359_a(new SPacketChangeGameState(1, 0.0F));
         var1.field_71135_a.func_147359_a(new SPacketChangeGameState(7, var2.func_72867_j(1.0F)));
         var1.field_71135_a.func_147359_a(new SPacketChangeGameState(8, var2.func_72819_i(1.0F)));
      }

   }

   public void func_72385_f(EntityPlayerMP var1) {
      var1.func_71120_a(var1.field_71069_bz);
      var1.func_71118_n();
      var1.field_71135_a.func_147359_a(new SPacketHeldItemChange(var1.field_71071_by.field_70461_c));
   }

   public int func_72394_k() {
      return this.field_72404_b.size();
   }

   public int func_72352_l() {
      return this.field_72405_c;
   }

   public String[] func_72373_m() {
      return this.field_72400_f.func_71218_a(DimensionType.OVERWORLD).func_72860_G().func_75756_e().func_75754_f();
   }

   public boolean func_72383_n() {
      return this.field_72409_l;
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

   public void func_152604_a(GameType var1) {
      this.field_72410_m = var1;
   }

   private void func_72381_a(EntityPlayerMP var1, EntityPlayerMP var2, IWorld var3) {
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
         ((EntityPlayerMP)this.field_72404_b.get(var1)).field_71135_a.func_194028_b(new TextComponentTranslation("multiplayer.disconnect.server_shutdown", new Object[0]));
      }

   }

   public void func_148544_a(ITextComponent var1, boolean var2) {
      this.field_72400_f.func_145747_a(var1);
      ChatType var3 = var2 ? ChatType.SYSTEM : ChatType.CHAT;
      this.func_148540_a(new SPacketChat(var1, var3));
   }

   public void func_148539_a(ITextComponent var1) {
      this.func_148544_a(var1, true);
   }

   public StatisticsManagerServer func_152602_a(EntityPlayer var1) {
      UUID var2 = var1.func_110124_au();
      StatisticsManagerServer var3 = var2 == null ? null : (StatisticsManagerServer)this.field_148547_k.get(var2);
      if (var3 == null) {
         File var4 = new File(this.field_72400_f.func_71218_a(DimensionType.OVERWORLD).func_72860_G().func_75765_b(), "stats");
         File var5 = new File(var4, var2 + ".json");
         if (!var5.exists()) {
            File var6 = new File(var4, var1.func_200200_C_().getString() + ".json");
            if (var6.exists() && var6.isFile()) {
               var6.renameTo(var5);
            }
         }

         var3 = new StatisticsManagerServer(this.field_72400_f, var5);
         this.field_148547_k.put(var2, var3);
      }

      return var3;
   }

   public PlayerAdvancements func_192054_h(EntityPlayerMP var1) {
      UUID var2 = var1.func_110124_au();
      PlayerAdvancements var3 = (PlayerAdvancements)this.field_192055_p.get(var2);
      if (var3 == null) {
         File var4 = new File(this.field_72400_f.func_71218_a(DimensionType.OVERWORLD).func_72860_G().func_75765_b(), "advancements");
         File var5 = new File(var4, var2 + ".json");
         var3 = new PlayerAdvancements(this.field_72400_f, var5, var1);
         this.field_192055_p.put(var2, var3);
      }

      var3.func_192739_a(var1);
      return var3;
   }

   public void func_152611_a(int var1) {
      this.field_72402_d = var1;
      Iterator var2 = this.field_72400_f.func_212370_w().iterator();

      while(var2.hasNext()) {
         WorldServer var3 = (WorldServer)var2.next();
         if (var3 != null) {
            var3.func_184164_w().func_152622_a(var1);
            var3.func_73039_n().func_187252_a(var1);
         }
      }

   }

   public List<EntityPlayerMP> func_181057_v() {
      return this.field_72404_b;
   }

   @Nullable
   public EntityPlayerMP func_177451_a(UUID var1) {
      return (EntityPlayerMP)this.field_177454_f.get(var1);
   }

   public boolean func_183023_f(GameProfile var1) {
      return false;
   }

   public void func_193244_w() {
      Iterator var1 = this.field_192055_p.values().iterator();

      while(var1.hasNext()) {
         PlayerAdvancements var2 = (PlayerAdvancements)var1.next();
         var2.func_193766_b();
      }

      this.func_148540_a(new SPacketTagsList(this.field_72400_f.func_199731_aO()));
      SPacketUpdateRecipes var4 = new SPacketUpdateRecipes(this.field_72400_f.func_199529_aN().func_199510_b());
      Iterator var5 = this.field_72404_b.iterator();

      while(var5.hasNext()) {
         EntityPlayerMP var3 = (EntityPlayerMP)var5.next();
         var3.field_71135_a.func_147359_a(var4);
         var3.func_192037_E().func_192826_c(var3);
      }

   }

   public boolean func_206257_x() {
      return this.field_72407_n;
   }
}
