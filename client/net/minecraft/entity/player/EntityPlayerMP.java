package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
   private static final Logger field_147102_bM = LogManager.getLogger();
   private String field_71148_cg = "en_US";
   public NetHandlerPlayServer field_71135_a;
   public final MinecraftServer field_71133_b;
   public final ItemInWorldManager field_71134_c;
   public double field_71131_d;
   public double field_71132_e;
   public final List<ChunkCoordIntPair> field_71129_f = Lists.newLinkedList();
   private final List<Integer> field_71130_g = Lists.newLinkedList();
   private final StatisticsFile field_147103_bO;
   private float field_130068_bO = 1.4E-45F;
   private float field_71149_ch = -1.0E8F;
   private int field_71146_ci = -99999999;
   private boolean field_71147_cj = true;
   private int field_71144_ck = -99999999;
   private int field_147101_bU = 60;
   private EntityPlayer.EnumChatVisibility field_71143_cn;
   private boolean field_71140_co = true;
   private long field_143005_bX = System.currentTimeMillis();
   private Entity field_175401_bS = null;
   private int field_71139_cq;
   public boolean field_71137_h;
   public int field_71138_i;
   public boolean field_71136_j;

   public EntityPlayerMP(MinecraftServer var1, WorldServer var2, GameProfile var3, ItemInWorldManager var4) {
      super(var2, var3);
      var4.field_73090_b = this;
      this.field_71134_c = var4;
      BlockPos var5 = var2.func_175694_M();
      if (!var2.field_73011_w.func_177495_o() && var2.func_72912_H().func_76077_q() != WorldSettings.GameType.ADVENTURE) {
         int var6 = Math.max(5, var1.func_82357_ak() - 6);
         int var7 = MathHelper.func_76128_c(var2.func_175723_af().func_177729_b((double)var5.func_177958_n(), (double)var5.func_177952_p()));
         if (var7 < var6) {
            var6 = var7;
         }

         if (var7 <= 1) {
            var6 = 1;
         }

         var5 = var2.func_175672_r(var5.func_177982_a(this.field_70146_Z.nextInt(var6 * 2) - var6, 0, this.field_70146_Z.nextInt(var6 * 2) - var6));
      }

      this.field_71133_b = var1;
      this.field_147103_bO = var1.func_71203_ab().func_152602_a(this);
      this.field_70138_W = 0.0F;
      this.func_174828_a(var5, 0.0F, 0.0F);

      while(!var2.func_72945_a(this, this.func_174813_aQ()).isEmpty() && this.field_70163_u < 255.0D) {
         this.func_70107_b(this.field_70165_t, this.field_70163_u + 1.0D, this.field_70161_v);
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("playerGameType", 99)) {
         if (MinecraftServer.func_71276_C().func_104056_am()) {
            this.field_71134_c.func_73076_a(MinecraftServer.func_71276_C().func_71265_f());
         } else {
            this.field_71134_c.func_73076_a(WorldSettings.GameType.func_77146_a(var1.func_74762_e("playerGameType")));
         }
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("playerGameType", this.field_71134_c.func_73081_b().func_77148_a());
   }

   public void func_82242_a(int var1) {
      super.func_82242_a(var1);
      this.field_71144_ck = -1;
   }

   public void func_71013_b(int var1) {
      super.func_71013_b(var1);
      this.field_71144_ck = -1;
   }

   public void func_71116_b() {
      this.field_71070_bA.func_75132_a(this);
   }

   public void func_152111_bt() {
      super.func_152111_bt();
      this.field_71135_a.func_147359_a(new S42PacketCombatEvent(this.func_110142_aN(), S42PacketCombatEvent.Event.ENTER_COMBAT));
   }

   public void func_152112_bu() {
      super.func_152112_bu();
      this.field_71135_a.func_147359_a(new S42PacketCombatEvent(this.func_110142_aN(), S42PacketCombatEvent.Event.END_COMBAT));
   }

   public void func_70071_h_() {
      this.field_71134_c.func_73075_a();
      --this.field_147101_bU;
      if (this.field_70172_ad > 0) {
         --this.field_70172_ad;
      }

      this.field_71070_bA.func_75142_b();
      if (!this.field_70170_p.field_72995_K && !this.field_71070_bA.func_75145_c(this)) {
         this.func_71053_j();
         this.field_71070_bA = this.field_71069_bz;
      }

      while(!this.field_71130_g.isEmpty()) {
         int var1 = Math.min(this.field_71130_g.size(), 2147483647);
         int[] var2 = new int[var1];
         Iterator var3 = this.field_71130_g.iterator();
         int var4 = 0;

         while(var3.hasNext() && var4 < var1) {
            var2[var4++] = (Integer)var3.next();
            var3.remove();
         }

         this.field_71135_a.func_147359_a(new S13PacketDestroyEntities(var2));
      }

      if (!this.field_71129_f.isEmpty()) {
         ArrayList var6 = Lists.newArrayList();
         Iterator var8 = this.field_71129_f.iterator();
         ArrayList var9 = Lists.newArrayList();

         Chunk var5;
         while(var8.hasNext() && var6.size() < 10) {
            ChunkCoordIntPair var10 = (ChunkCoordIntPair)var8.next();
            if (var10 != null) {
               if (this.field_70170_p.func_175667_e(new BlockPos(var10.field_77276_a << 4, 0, var10.field_77275_b << 4))) {
                  var5 = this.field_70170_p.func_72964_e(var10.field_77276_a, var10.field_77275_b);
                  if (var5.func_150802_k()) {
                     var6.add(var5);
                     var9.addAll(((WorldServer)this.field_70170_p).func_147486_a(var10.field_77276_a * 16, 0, var10.field_77275_b * 16, var10.field_77276_a * 16 + 16, 256, var10.field_77275_b * 16 + 16));
                     var8.remove();
                  }
               }
            } else {
               var8.remove();
            }
         }

         if (!var6.isEmpty()) {
            if (var6.size() == 1) {
               this.field_71135_a.func_147359_a(new S21PacketChunkData((Chunk)var6.get(0), true, 65535));
            } else {
               this.field_71135_a.func_147359_a(new S26PacketMapChunkBulk(var6));
            }

            Iterator var11 = var9.iterator();

            while(var11.hasNext()) {
               TileEntity var12 = (TileEntity)var11.next();
               this.func_147097_b(var12);
            }

            var11 = var6.iterator();

            while(var11.hasNext()) {
               var5 = (Chunk)var11.next();
               this.func_71121_q().func_73039_n().func_85172_a(this, var5);
            }
         }
      }

      Entity var7 = this.func_175398_C();
      if (var7 != this) {
         if (!var7.func_70089_S()) {
            this.func_175399_e(this);
         } else {
            this.func_70080_a(var7.field_70165_t, var7.field_70163_u, var7.field_70161_v, var7.field_70177_z, var7.field_70125_A);
            this.field_71133_b.func_71203_ab().func_72358_d(this);
            if (this.func_70093_af()) {
               this.func_175399_e(this);
            }
         }
      }

   }

   public void func_71127_g() {
      try {
         super.func_70071_h_();

         for(int var1 = 0; var1 < this.field_71071_by.func_70302_i_(); ++var1) {
            ItemStack var6 = this.field_71071_by.func_70301_a(var1);
            if (var6 != null && var6.func_77973_b().func_77643_m_()) {
               Packet var8 = ((ItemMapBase)var6.func_77973_b()).func_150911_c(var6, this.field_70170_p, this);
               if (var8 != null) {
                  this.field_71135_a.func_147359_a(var8);
               }
            }
         }

         if (this.func_110143_aJ() != this.field_71149_ch || this.field_71146_ci != this.field_71100_bB.func_75116_a() || this.field_71100_bB.func_75115_e() == 0.0F != this.field_71147_cj) {
            this.field_71135_a.func_147359_a(new S06PacketUpdateHealth(this.func_110143_aJ(), this.field_71100_bB.func_75116_a(), this.field_71100_bB.func_75115_e()));
            this.field_71149_ch = this.func_110143_aJ();
            this.field_71146_ci = this.field_71100_bB.func_75116_a();
            this.field_71147_cj = this.field_71100_bB.func_75115_e() == 0.0F;
         }

         if (this.func_110143_aJ() + this.func_110139_bj() != this.field_130068_bO) {
            this.field_130068_bO = this.func_110143_aJ() + this.func_110139_bj();
            Collection var5 = this.func_96123_co().func_96520_a(IScoreObjectiveCriteria.field_96638_f);
            Iterator var7 = var5.iterator();

            while(var7.hasNext()) {
               ScoreObjective var9 = (ScoreObjective)var7.next();
               this.func_96123_co().func_96529_a(this.func_70005_c_(), var9).func_96651_a(Arrays.asList(this));
            }
         }

         if (this.field_71067_cb != this.field_71144_ck) {
            this.field_71144_ck = this.field_71067_cb;
            this.field_71135_a.func_147359_a(new S1FPacketSetExperience(this.field_71106_cc, this.field_71067_cb, this.field_71068_ca));
         }

         if (this.field_70173_aa % 20 * 5 == 0 && !this.func_147099_x().func_77443_a(AchievementList.field_150961_L)) {
            this.func_147098_j();
         }

      } catch (Throwable var4) {
         CrashReport var2 = CrashReport.func_85055_a(var4, "Ticking player");
         CrashReportCategory var3 = var2.func_85058_a("Player being ticked");
         this.func_85029_a(var3);
         throw new ReportedException(var2);
      }
   }

   protected void func_147098_j() {
      BiomeGenBase var1 = this.field_70170_p.func_180494_b(new BlockPos(MathHelper.func_76128_c(this.field_70165_t), 0, MathHelper.func_76128_c(this.field_70161_v)));
      String var2 = var1.field_76791_y;
      JsonSerializableSet var3 = (JsonSerializableSet)this.func_147099_x().func_150870_b(AchievementList.field_150961_L);
      if (var3 == null) {
         var3 = (JsonSerializableSet)this.func_147099_x().func_150872_a(AchievementList.field_150961_L, new JsonSerializableSet());
      }

      var3.add(var2);
      if (this.func_147099_x().func_77442_b(AchievementList.field_150961_L) && var3.size() >= BiomeGenBase.field_150597_n.size()) {
         HashSet var4 = Sets.newHashSet(BiomeGenBase.field_150597_n);
         Iterator var5 = var3.iterator();

         while(var5.hasNext()) {
            String var6 = (String)var5.next();
            Iterator var7 = var4.iterator();

            while(var7.hasNext()) {
               BiomeGenBase var8 = (BiomeGenBase)var7.next();
               if (var8.field_76791_y.equals(var6)) {
                  var7.remove();
               }
            }

            if (var4.isEmpty()) {
               break;
            }
         }

         if (var4.isEmpty()) {
            this.func_71029_a(AchievementList.field_150961_L);
         }
      }

   }

   public void func_70645_a(DamageSource var1) {
      if (this.field_70170_p.func_82736_K().func_82766_b("showDeathMessages")) {
         Team var2 = this.func_96124_cp();
         if (var2 != null && var2.func_178771_j() != Team.EnumVisible.ALWAYS) {
            if (var2.func_178771_j() == Team.EnumVisible.HIDE_FOR_OTHER_TEAMS) {
               this.field_71133_b.func_71203_ab().func_177453_a(this, this.func_110142_aN().func_151521_b());
            } else if (var2.func_178771_j() == Team.EnumVisible.HIDE_FOR_OWN_TEAM) {
               this.field_71133_b.func_71203_ab().func_177452_b(this, this.func_110142_aN().func_151521_b());
            }
         } else {
            this.field_71133_b.func_71203_ab().func_148539_a(this.func_110142_aN().func_151521_b());
         }
      }

      if (!this.field_70170_p.func_82736_K().func_82766_b("keepInventory")) {
         this.field_71071_by.func_70436_m();
      }

      Collection var6 = this.field_70170_p.func_96441_U().func_96520_a(IScoreObjectiveCriteria.field_96642_c);
      Iterator var3 = var6.iterator();

      while(var3.hasNext()) {
         ScoreObjective var4 = (ScoreObjective)var3.next();
         Score var5 = this.func_96123_co().func_96529_a(this.func_70005_c_(), var4);
         var5.func_96648_a();
      }

      EntityLivingBase var7 = this.func_94060_bK();
      if (var7 != null) {
         EntityList.EntityEggInfo var8 = (EntityList.EntityEggInfo)EntityList.field_75627_a.get(EntityList.func_75619_a(var7));
         if (var8 != null) {
            this.func_71029_a(var8.field_151513_e);
         }

         var7.func_70084_c(this, this.field_70744_aE);
      }

      this.func_71029_a(StatList.field_75960_y);
      this.func_175145_a(StatList.field_180209_h);
      this.func_110142_aN().func_94549_h();
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         boolean var3 = this.field_71133_b.func_71262_S() && this.func_175400_cq() && "fall".equals(var1.field_76373_n);
         if (!var3 && this.field_147101_bU > 0 && var1 != DamageSource.field_76380_i) {
            return false;
         } else {
            if (var1 instanceof EntityDamageSource) {
               Entity var4 = var1.func_76346_g();
               if (var4 instanceof EntityPlayer && !this.func_96122_a((EntityPlayer)var4)) {
                  return false;
               }

               if (var4 instanceof EntityArrow) {
                  EntityArrow var5 = (EntityArrow)var4;
                  if (var5.field_70250_c instanceof EntityPlayer && !this.func_96122_a((EntityPlayer)var5.field_70250_c)) {
                     return false;
                  }
               }
            }

            return super.func_70097_a(var1, var2);
         }
      }
   }

   public boolean func_96122_a(EntityPlayer var1) {
      return !this.func_175400_cq() ? false : super.func_96122_a(var1);
   }

   private boolean func_175400_cq() {
      return this.field_71133_b.func_71219_W();
   }

   public void func_71027_c(int var1) {
      if (this.field_71093_bK == 1 && var1 == 1) {
         this.func_71029_a(AchievementList.field_76003_C);
         this.field_70170_p.func_72900_e(this);
         this.field_71136_j = true;
         this.field_71135_a.func_147359_a(new S2BPacketChangeGameState(4, 0.0F));
      } else {
         if (this.field_71093_bK == 0 && var1 == 1) {
            this.func_71029_a(AchievementList.field_76002_B);
            BlockPos var2 = this.field_71133_b.func_71218_a(var1).func_180504_m();
            if (var2 != null) {
               this.field_71135_a.func_147364_a((double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)var2.func_177952_p(), 0.0F, 0.0F);
            }

            var1 = 1;
         } else {
            this.func_71029_a(AchievementList.field_76029_x);
         }

         this.field_71133_b.func_71203_ab().func_72356_a(this, var1);
         this.field_71144_ck = -1;
         this.field_71149_ch = -1.0F;
         this.field_71146_ci = -1;
      }

   }

   public boolean func_174827_a(EntityPlayerMP var1) {
      if (var1.func_175149_v()) {
         return this.func_175398_C() == this;
      } else {
         return this.func_175149_v() ? false : super.func_174827_a(var1);
      }
   }

   private void func_147097_b(TileEntity var1) {
      if (var1 != null) {
         Packet var2 = var1.func_145844_m();
         if (var2 != null) {
            this.field_71135_a.func_147359_a(var2);
         }
      }

   }

   public void func_71001_a(Entity var1, int var2) {
      super.func_71001_a(var1, var2);
      this.field_71070_bA.func_75142_b();
   }

   public EntityPlayer.EnumStatus func_180469_a(BlockPos var1) {
      EntityPlayer.EnumStatus var2 = super.func_180469_a(var1);
      if (var2 == EntityPlayer.EnumStatus.OK) {
         S0APacketUseBed var3 = new S0APacketUseBed(this, var1);
         this.func_71121_q().func_73039_n().func_151247_a(this, var3);
         this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         this.field_71135_a.func_147359_a(var3);
      }

      return var2;
   }

   public void func_70999_a(boolean var1, boolean var2, boolean var3) {
      if (this.func_70608_bn()) {
         this.func_71121_q().func_73039_n().func_151248_b(this, new S0BPacketAnimation(this, 2));
      }

      super.func_70999_a(var1, var2, var3);
      if (this.field_71135_a != null) {
         this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
      }

   }

   public void func_70078_a(Entity var1) {
      Entity var2 = this.field_70154_o;
      super.func_70078_a(var1);
      if (var1 != var2) {
         this.field_71135_a.func_147359_a(new S1BPacketEntityAttach(0, this, this.field_70154_o));
         this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
      }

   }

   protected void func_180433_a(double var1, boolean var3, Block var4, BlockPos var5) {
   }

   public void func_71122_b(double var1, boolean var3) {
      int var4 = MathHelper.func_76128_c(this.field_70165_t);
      int var5 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
      int var6 = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos var7 = new BlockPos(var4, var5, var6);
      Block var8 = this.field_70170_p.func_180495_p(var7).func_177230_c();
      if (var8.func_149688_o() == Material.field_151579_a) {
         Block var9 = this.field_70170_p.func_180495_p(var7.func_177977_b()).func_177230_c();
         if (var9 instanceof BlockFence || var9 instanceof BlockWall || var9 instanceof BlockFenceGate) {
            var7 = var7.func_177977_b();
            var8 = this.field_70170_p.func_180495_p(var7).func_177230_c();
         }
      }

      super.func_180433_a(var1, var3, var8, var7);
   }

   public void func_175141_a(TileEntitySign var1) {
      var1.func_145912_a(this);
      this.field_71135_a.func_147359_a(new S36PacketSignEditorOpen(var1.func_174877_v()));
   }

   private void func_71117_bO() {
      this.field_71139_cq = this.field_71139_cq % 100 + 1;
   }

   public void func_180468_a(IInteractionObject var1) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, var1.func_174875_k(), var1.func_145748_c_()));
      this.field_71070_bA = var1.func_174876_a(this.field_71071_by, this);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   public void func_71007_a(IInventory var1) {
      if (this.field_71070_bA != this.field_71069_bz) {
         this.func_71053_j();
      }

      if (var1 instanceof ILockableContainer) {
         ILockableContainer var2 = (ILockableContainer)var1;
         if (var2.func_174893_q_() && !this.func_175146_a(var2.func_174891_i()) && !this.func_175149_v()) {
            this.field_71135_a.func_147359_a(new S02PacketChat(new ChatComponentTranslation("container.isLocked", new Object[]{var1.func_145748_c_()}), (byte)2));
            this.field_71135_a.func_147359_a(new S29PacketSoundEffect("random.door_close", this.field_70165_t, this.field_70163_u, this.field_70161_v, 1.0F, 1.0F));
            return;
         }
      }

      this.func_71117_bO();
      if (var1 instanceof IInteractionObject) {
         this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, ((IInteractionObject)var1).func_174875_k(), var1.func_145748_c_(), var1.func_70302_i_()));
         this.field_71070_bA = ((IInteractionObject)var1).func_174876_a(this.field_71071_by, this);
      } else {
         this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, "minecraft:container", var1.func_145748_c_(), var1.func_70302_i_()));
         this.field_71070_bA = new ContainerChest(this.field_71071_by, var1, this);
      }

      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   public void func_180472_a(IMerchant var1) {
      this.func_71117_bO();
      this.field_71070_bA = new ContainerMerchant(this.field_71071_by, var1, this.field_70170_p);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
      InventoryMerchant var2 = ((ContainerMerchant)this.field_71070_bA).func_75174_d();
      IChatComponent var3 = var1.func_145748_c_();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, "minecraft:villager", var3, var2.func_70302_i_()));
      MerchantRecipeList var4 = var1.func_70934_b(this);
      if (var4 != null) {
         PacketBuffer var5 = new PacketBuffer(Unpooled.buffer());
         var5.writeInt(this.field_71139_cq);
         var4.func_151391_a(var5);
         this.field_71135_a.func_147359_a(new S3FPacketCustomPayload("MC|TrList", var5));
      }

   }

   public void func_110298_a(EntityHorse var1, IInventory var2) {
      if (this.field_71070_bA != this.field_71069_bz) {
         this.func_71053_j();
      }

      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, "EntityHorse", var2.func_145748_c_(), var2.func_70302_i_(), var1.func_145782_y()));
      this.field_71070_bA = new ContainerHorseInventory(this.field_71071_by, var2, var1, this);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   public void func_71048_c(ItemStack var1) {
      Item var2 = var1.func_77973_b();
      if (var2 == Items.field_151164_bB) {
         this.field_71135_a.func_147359_a(new S3FPacketCustomPayload("MC|BOpen", new PacketBuffer(Unpooled.buffer())));
      }

   }

   public void func_71111_a(Container var1, int var2, ItemStack var3) {
      if (!(var1.func_75139_a(var2) instanceof SlotCrafting)) {
         if (!this.field_71137_h) {
            this.field_71135_a.func_147359_a(new S2FPacketSetSlot(var1.field_75152_c, var2, var3));
         }
      }
   }

   public void func_71120_a(Container var1) {
      this.func_71110_a(var1, var1.func_75138_a());
   }

   public void func_71110_a(Container var1, List<ItemStack> var2) {
      this.field_71135_a.func_147359_a(new S30PacketWindowItems(var1.field_75152_c, var2));
      this.field_71135_a.func_147359_a(new S2FPacketSetSlot(-1, -1, this.field_71071_by.func_70445_o()));
   }

   public void func_71112_a(Container var1, int var2, int var3) {
      this.field_71135_a.func_147359_a(new S31PacketWindowProperty(var1.field_75152_c, var2, var3));
   }

   public void func_175173_a(Container var1, IInventory var2) {
      for(int var3 = 0; var3 < var2.func_174890_g(); ++var3) {
         this.field_71135_a.func_147359_a(new S31PacketWindowProperty(var1.field_75152_c, var3, var2.func_174887_a_(var3)));
      }

   }

   public void func_71053_j() {
      this.field_71135_a.func_147359_a(new S2EPacketCloseWindow(this.field_71070_bA.field_75152_c));
      this.func_71128_l();
   }

   public void func_71113_k() {
      if (!this.field_71137_h) {
         this.field_71135_a.func_147359_a(new S2FPacketSetSlot(-1, -1, this.field_71071_by.func_70445_o()));
      }
   }

   public void func_71128_l() {
      this.field_71070_bA.func_75134_a(this);
      this.field_71070_bA = this.field_71069_bz;
   }

   public void func_110430_a(float var1, float var2, boolean var3, boolean var4) {
      if (this.field_70154_o != null) {
         if (var1 >= -1.0F && var1 <= 1.0F) {
            this.field_70702_br = var1;
         }

         if (var2 >= -1.0F && var2 <= 1.0F) {
            this.field_70701_bs = var2;
         }

         this.field_70703_bu = var3;
         this.func_70095_a(var4);
      }

   }

   public void func_71064_a(StatBase var1, int var2) {
      if (var1 != null) {
         this.field_147103_bO.func_150871_b(this, var1, var2);
         Iterator var3 = this.func_96123_co().func_96520_a(var1.func_150952_k()).iterator();

         while(var3.hasNext()) {
            ScoreObjective var4 = (ScoreObjective)var3.next();
            this.func_96123_co().func_96529_a(this.func_70005_c_(), var4).func_96649_a(var2);
         }

         if (this.field_147103_bO.func_150879_e()) {
            this.field_147103_bO.func_150876_a(this);
         }

      }
   }

   public void func_175145_a(StatBase var1) {
      if (var1 != null) {
         this.field_147103_bO.func_150873_a(this, var1, 0);
         Iterator var2 = this.func_96123_co().func_96520_a(var1.func_150952_k()).iterator();

         while(var2.hasNext()) {
            ScoreObjective var3 = (ScoreObjective)var2.next();
            this.func_96123_co().func_96529_a(this.func_70005_c_(), var3).func_96647_c(0);
         }

         if (this.field_147103_bO.func_150879_e()) {
            this.field_147103_bO.func_150876_a(this);
         }

      }
   }

   public void func_71123_m() {
      if (this.field_70153_n != null) {
         this.field_70153_n.func_70078_a(this);
      }

      if (this.field_71083_bS) {
         this.func_70999_a(true, false, false);
      }

   }

   public void func_71118_n() {
      this.field_71149_ch = -1.0E8F;
   }

   public void func_146105_b(IChatComponent var1) {
      this.field_71135_a.func_147359_a(new S02PacketChat(var1));
   }

   protected void func_71036_o() {
      this.field_71135_a.func_147359_a(new S19PacketEntityStatus(this, (byte)9));
      super.func_71036_o();
   }

   public void func_71008_a(ItemStack var1, int var2) {
      super.func_71008_a(var1, var2);
      if (var1 != null && var1.func_77973_b() != null && var1.func_77973_b().func_77661_b(var1) == EnumAction.EAT) {
         this.func_71121_q().func_73039_n().func_151248_b(this, new S0BPacketAnimation(this, 3));
      }

   }

   public void func_71049_a(EntityPlayer var1, boolean var2) {
      super.func_71049_a(var1, var2);
      this.field_71144_ck = -1;
      this.field_71149_ch = -1.0F;
      this.field_71146_ci = -1;
      this.field_71130_g.addAll(((EntityPlayerMP)var1).field_71130_g);
   }

   protected void func_70670_a(PotionEffect var1) {
      super.func_70670_a(var1);
      this.field_71135_a.func_147359_a(new S1DPacketEntityEffect(this.func_145782_y(), var1));
   }

   protected void func_70695_b(PotionEffect var1, boolean var2) {
      super.func_70695_b(var1, var2);
      this.field_71135_a.func_147359_a(new S1DPacketEntityEffect(this.func_145782_y(), var1));
   }

   protected void func_70688_c(PotionEffect var1) {
      super.func_70688_c(var1);
      this.field_71135_a.func_147359_a(new S1EPacketRemoveEntityEffect(this.func_145782_y(), var1));
   }

   public void func_70634_a(double var1, double var3, double var5) {
      this.field_71135_a.func_147364_a(var1, var3, var5, this.field_70177_z, this.field_70125_A);
   }

   public void func_71009_b(Entity var1) {
      this.func_71121_q().func_73039_n().func_151248_b(this, new S0BPacketAnimation(var1, 4));
   }

   public void func_71047_c(Entity var1) {
      this.func_71121_q().func_73039_n().func_151248_b(this, new S0BPacketAnimation(var1, 5));
   }

   public void func_71016_p() {
      if (this.field_71135_a != null) {
         this.field_71135_a.func_147359_a(new S39PacketPlayerAbilities(this.field_71075_bZ));
         this.func_175135_B();
      }
   }

   public WorldServer func_71121_q() {
      return (WorldServer)this.field_70170_p;
   }

   public void func_71033_a(WorldSettings.GameType var1) {
      this.field_71134_c.func_73076_a(var1);
      this.field_71135_a.func_147359_a(new S2BPacketChangeGameState(3, (float)var1.func_77148_a()));
      if (var1 == WorldSettings.GameType.SPECTATOR) {
         this.func_70078_a((Entity)null);
      } else {
         this.func_175399_e(this);
      }

      this.func_71016_p();
      this.func_175136_bO();
   }

   public boolean func_175149_v() {
      return this.field_71134_c.func_73081_b() == WorldSettings.GameType.SPECTATOR;
   }

   public void func_145747_a(IChatComponent var1) {
      this.field_71135_a.func_147359_a(new S02PacketChat(var1));
   }

   public boolean func_70003_b(int var1, String var2) {
      if ("seed".equals(var2) && !this.field_71133_b.func_71262_S()) {
         return true;
      } else if (!"tell".equals(var2) && !"help".equals(var2) && !"me".equals(var2) && !"trigger".equals(var2)) {
         if (this.field_71133_b.func_71203_ab().func_152596_g(this.func_146103_bH())) {
            UserListOpsEntry var3 = (UserListOpsEntry)this.field_71133_b.func_71203_ab().func_152603_m().func_152683_b(this.func_146103_bH());
            if (var3 != null) {
               return var3.func_152644_a() >= var1;
            } else {
               return this.field_71133_b.func_110455_j() >= var1;
            }
         } else {
            return false;
         }
      } else {
         return true;
      }
   }

   public String func_71114_r() {
      String var1 = this.field_71135_a.field_147371_a.func_74430_c().toString();
      var1 = var1.substring(var1.indexOf("/") + 1);
      var1 = var1.substring(0, var1.indexOf(":"));
      return var1;
   }

   public void func_147100_a(C15PacketClientSettings var1) {
      this.field_71148_cg = var1.func_149524_c();
      this.field_71143_cn = var1.func_149523_e();
      this.field_71140_co = var1.func_149520_f();
      this.func_70096_w().func_75692_b(10, (byte)var1.func_149521_d());
   }

   public EntityPlayer.EnumChatVisibility func_147096_v() {
      return this.field_71143_cn;
   }

   public void func_175397_a(String var1, String var2) {
      this.field_71135_a.func_147359_a(new S48PacketResourcePackSend(var1, var2));
   }

   public BlockPos func_180425_c() {
      return new BlockPos(this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v);
   }

   public void func_143004_u() {
      this.field_143005_bX = MinecraftServer.func_130071_aq();
   }

   public StatisticsFile func_147099_x() {
      return this.field_147103_bO;
   }

   public void func_152339_d(Entity var1) {
      if (var1 instanceof EntityPlayer) {
         this.field_71135_a.func_147359_a(new S13PacketDestroyEntities(new int[]{var1.func_145782_y()}));
      } else {
         this.field_71130_g.add(var1.func_145782_y());
      }

   }

   protected void func_175135_B() {
      if (this.func_175149_v()) {
         this.func_175133_bi();
         this.func_82142_c(true);
      } else {
         super.func_175135_B();
      }

      this.func_71121_q().func_73039_n().func_180245_a(this);
   }

   public Entity func_175398_C() {
      return (Entity)(this.field_175401_bS == null ? this : this.field_175401_bS);
   }

   public void func_175399_e(Entity var1) {
      Entity var2 = this.func_175398_C();
      this.field_175401_bS = (Entity)(var1 == null ? this : var1);
      if (var2 != this.field_175401_bS) {
         this.field_71135_a.func_147359_a(new S43PacketCamera(this.field_175401_bS));
         this.func_70634_a(this.field_175401_bS.field_70165_t, this.field_175401_bS.field_70163_u, this.field_175401_bS.field_70161_v);
      }

   }

   public void func_71059_n(Entity var1) {
      if (this.field_71134_c.func_73081_b() == WorldSettings.GameType.SPECTATOR) {
         this.func_175399_e(var1);
      } else {
         super.func_71059_n(var1);
      }

   }

   public long func_154331_x() {
      return this.field_143005_bX;
   }

   public IChatComponent func_175396_E() {
      return null;
   }
}
