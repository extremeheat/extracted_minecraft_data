package net.minecraft.entity.player;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList$EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.EnumAction;
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
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.server.management.UserListOpsEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.JsonSerializableSet;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings$GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.io.Charsets;
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
   public final List field_71129_f = new LinkedList();
   private final List field_71130_g = new LinkedList();
   private final StatisticsFile field_147103_bO;
   private float field_130068_bO = 1.0E-45F;
   private float field_71149_ch = -1.0E8F;
   private int field_71146_ci = -99999999;
   private boolean field_71147_cj = true;
   private int field_71144_ck = -99999999;
   private int field_147101_bU = 60;
   private EntityPlayer$EnumChatVisibility field_71143_cn;
   private boolean field_71140_co = true;
   private long field_143005_bX = System.currentTimeMillis();
   private int field_71139_cq;
   public boolean field_71137_h;
   public int field_71138_i;
   public boolean field_71136_j;

   public EntityPlayerMP(MinecraftServer var1, WorldServer var2, GameProfile var3, ItemInWorldManager var4) {
      super(var2, var3);
      var4.field_73090_b = this;
      this.field_71134_c = var4;
      ChunkCoordinates var5 = var2.func_72861_E();
      int var6 = var5.field_71574_a;
      int var7 = var5.field_71573_c;
      int var8 = var5.field_71572_b;
      if (!var2.field_73011_w.field_76576_e && var2.func_72912_H().func_76077_q() != WorldSettings$GameType.ADVENTURE) {
         int var9 = Math.max(5, var1.func_82357_ak() - 6);
         var6 += this.field_70146_Z.nextInt(var9 * 2) - var9;
         var7 += this.field_70146_Z.nextInt(var9 * 2) - var9;
         var8 = var2.func_72825_h(var6, var7);
      }

      this.field_71133_b = var1;
      this.field_147103_bO = var1.func_71203_ab().func_152602_a(this);
      this.field_70138_W = 0.0F;
      this.field_70129_M = 0.0F;
      this.func_70012_b((double)var6 + 0.5, (double)var8, (double)var7 + 0.5, 0.0F, 0.0F);

      while(!var2.func_72945_a(this, this.field_70121_D).isEmpty()) {
         this.func_70107_b(this.field_70165_t, this.field_70163_u + 1.0, this.field_70161_v);
      }
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("playerGameType", 99)) {
         if (MinecraftServer.func_71276_C().func_104056_am()) {
            this.field_71134_c.func_73076_a(MinecraftServer.func_71276_C().func_71265_f());
         } else {
            this.field_71134_c.func_73076_a(WorldSettings$GameType.func_77146_a(var1.func_74762_e("playerGameType")));
         }
      }
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("playerGameType", this.field_71134_c.func_73081_b().func_77148_a());
   }

   @Override
   public void func_82242_a(int var1) {
      super.func_82242_a(var1);
      this.field_71144_ck = -1;
   }

   public void func_71116_b() {
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   protected void func_71061_d_() {
      this.field_70129_M = 0.0F;
   }

   @Override
   public float func_70047_e() {
      return 1.62F;
   }

   @Override
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
         int var1 = Math.min(this.field_71130_g.size(), 127);
         int[] var2 = new int[var1];
         Iterator var3 = this.field_71130_g.iterator();
         int var4 = 0;

         while(var3.hasNext() && var4 < var1) {
            var2[var4++] = var3.next();
            var3.remove();
         }

         this.field_71135_a.func_147359_a(new S13PacketDestroyEntities(var2));
      }

      if (!this.field_71129_f.isEmpty()) {
         ArrayList var6 = new ArrayList();
         Iterator var7 = this.field_71129_f.iterator();
         ArrayList var8 = new ArrayList();

         while(var7.hasNext() && var6.size() < S26PacketMapChunkBulk.func_149258_c()) {
            ChunkCoordIntPair var9 = (ChunkCoordIntPair)var7.next();
            if (var9 != null) {
               if (this.field_70170_p.func_72899_e(var9.field_77276_a << 4, 0, var9.field_77275_b << 4)) {
                  Chunk var5 = this.field_70170_p.func_72964_e(var9.field_77276_a, var9.field_77275_b);
                  if (var5.func_150802_k()) {
                     var6.add(var5);
                     var8.addAll(
                        ((WorldServer)this.field_70170_p)
                           .func_147486_a(var9.field_77276_a * 16, 0, var9.field_77275_b * 16, var9.field_77276_a * 16 + 16, 256, var9.field_77275_b * 16 + 16)
                     );
                     var7.remove();
                  }
               }
            } else {
               var7.remove();
            }
         }

         if (!var6.isEmpty()) {
            this.field_71135_a.func_147359_a(new S26PacketMapChunkBulk(var6));

            for(TileEntity var12 : var8) {
               this.func_147097_b(var12);
            }

            for(Chunk var13 : var6) {
               this.func_71121_q().func_73039_n().func_85172_a(this, var13);
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

         if (this.func_110143_aJ() != this.field_71149_ch
            || this.field_71146_ci != this.field_71100_bB.func_75116_a()
            || this.field_71100_bB.func_75115_e() == 0.0F != this.field_71147_cj) {
            this.field_71135_a
               .func_147359_a(new S06PacketUpdateHealth(this.func_110143_aJ(), this.field_71100_bB.func_75116_a(), this.field_71100_bB.func_75115_e()));
            this.field_71149_ch = this.func_110143_aJ();
            this.field_71146_ci = this.field_71100_bB.func_75116_a();
            this.field_71147_cj = this.field_71100_bB.func_75115_e() == 0.0F;
         }

         if (this.func_110143_aJ() + this.func_110139_bj() != this.field_130068_bO) {
            this.field_130068_bO = this.func_110143_aJ() + this.func_110139_bj();

            for(ScoreObjective var9 : this.func_96123_co().func_96520_a(IScoreObjectiveCriteria.field_96638_f)) {
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
      BiomeGenBase var1 = this.field_70170_p.func_72807_a(MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70161_v));
      if (var1 != null) {
         String var2 = var1.field_76791_y;
         JsonSerializableSet var3 = (JsonSerializableSet)this.func_147099_x().func_150870_b(AchievementList.field_150961_L);
         if (var3 == null) {
            var3 = (JsonSerializableSet)this.func_147099_x().func_150872_a(AchievementList.field_150961_L, new JsonSerializableSet());
         }

         var3.add(var2);
         if (this.func_147099_x().func_77442_b(AchievementList.field_150961_L) && var3.size() == BiomeGenBase.field_150597_n.size()) {
            HashSet var4 = Sets.newHashSet(BiomeGenBase.field_150597_n);

            for(String var6 : var3) {
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
   }

   @Override
   public void func_70645_a(DamageSource var1) {
      this.field_71133_b.func_71203_ab().func_148539_a(this.func_110142_aN().func_151521_b());
      if (!this.field_70170_p.func_82736_K().func_82766_b("keepInventory")) {
         this.field_71071_by.func_70436_m();
      }

      for(ScoreObjective var4 : this.field_70170_p.func_96441_U().func_96520_a(IScoreObjectiveCriteria.field_96642_c)) {
         Score var5 = this.func_96123_co().func_96529_a(this.func_70005_c_(), var4);
         var5.func_96648_a();
      }

      EntityLivingBase var6 = this.func_94060_bK();
      if (var6 != null) {
         int var7 = EntityList.func_75619_a(var6);
         EntityList$EntityEggInfo var8 = (EntityList$EntityEggInfo)EntityList.field_75627_a.get(var7);
         if (var8 != null) {
            this.func_71064_a(var8.field_151513_e, 1);
         }

         var6.func_70084_c(this, this.field_70744_aE);
      }

      this.func_71064_a(StatList.field_75960_y, 1);
      this.func_110142_aN().func_94549_h();
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else {
         boolean var3 = this.field_71133_b.func_71262_S() && this.field_71133_b.func_71219_W() && "fall".equals(var1.field_76373_n);
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

   @Override
   public boolean func_96122_a(EntityPlayer var1) {
      return !this.field_71133_b.func_71219_W() ? false : super.func_96122_a(var1);
   }

   @Override
   public void func_71027_c(int var1) {
      if (this.field_71093_bK == 1 && var1 == 1) {
         this.func_71029_a(AchievementList.field_76003_C);
         this.field_70170_p.func_72900_e(this);
         this.field_71136_j = true;
         this.field_71135_a.func_147359_a(new S2BPacketChangeGameState(4, 0.0F));
      } else {
         if (this.field_71093_bK == 0 && var1 == 1) {
            this.func_71029_a(AchievementList.field_76002_B);
            ChunkCoordinates var2 = this.field_71133_b.func_71218_a(var1).func_73054_j();
            if (var2 != null) {
               this.field_71135_a.func_147364_a((double)var2.field_71574_a, (double)var2.field_71572_b, (double)var2.field_71573_c, 0.0F, 0.0F);
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

   private void func_147097_b(TileEntity var1) {
      if (var1 != null) {
         Packet var2 = var1.func_145844_m();
         if (var2 != null) {
            this.field_71135_a.func_147359_a(var2);
         }
      }
   }

   @Override
   public void func_71001_a(Entity var1, int var2) {
      super.func_71001_a(var1, var2);
      this.field_71070_bA.func_75142_b();
   }

   @Override
   public EntityPlayer$EnumStatus func_71018_a(int var1, int var2, int var3) {
      EntityPlayer$EnumStatus var4 = super.func_71018_a(var1, var2, var3);
      if (var4 == EntityPlayer$EnumStatus.OK) {
         S0APacketUseBed var5 = new S0APacketUseBed(this, var1, var2, var3);
         this.func_71121_q().func_73039_n().func_151247_a(this, var5);
         this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         this.field_71135_a.func_147359_a(var5);
      }

      return var4;
   }

   @Override
   public void func_70999_a(boolean var1, boolean var2, boolean var3) {
      if (this.func_70608_bn()) {
         this.func_71121_q().func_73039_n().func_151248_b(this, new S0BPacketAnimation(this, 2));
      }

      super.func_70999_a(var1, var2, var3);
      if (this.field_71135_a != null) {
         this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
      }
   }

   @Override
   public void func_70078_a(Entity var1) {
      super.func_70078_a(var1);
      this.field_71135_a.func_147359_a(new S1BPacketEntityAttach(0, this, this.field_70154_o));
      this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
   }

   @Override
   protected void func_70064_a(double var1, boolean var3) {
   }

   public void func_71122_b(double var1, boolean var3) {
      super.func_70064_a(var1, var3);
   }

   @Override
   public void func_146100_a(TileEntity var1) {
      if (var1 instanceof TileEntitySign) {
         ((TileEntitySign)var1).func_145912_a(this);
         this.field_71135_a.func_147359_a(new S36PacketSignEditorOpen(var1.field_145851_c, var1.field_145848_d, var1.field_145849_e));
      }
   }

   private void func_71117_bO() {
      this.field_71139_cq = this.field_71139_cq % 100 + 1;
   }

   @Override
   public void func_71058_b(int var1, int var2, int var3) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 1, "Crafting", 9, true));
      this.field_71070_bA = new ContainerWorkbench(this.field_71071_by, this.field_70170_p, var1, var2, var3);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_71002_c(int var1, int var2, int var3, String var4) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 4, var4 == null ? "" : var4, 9, var4 != null));
      this.field_71070_bA = new ContainerEnchantment(this.field_71071_by, this.field_70170_p, var1, var2, var3);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_82244_d(int var1, int var2, int var3) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 8, "Repairing", 9, true));
      this.field_71070_bA = new ContainerRepair(this.field_71071_by, this.field_70170_p, var1, var2, var3, this);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_71007_a(IInventory var1) {
      if (this.field_71070_bA != this.field_71069_bz) {
         this.func_71053_j();
      }

      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 0, var1.func_145825_b(), var1.func_70302_i_(), var1.func_145818_k_()));
      this.field_71070_bA = new ContainerChest(this.field_71071_by, var1);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_146093_a(TileEntityHopper var1) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 9, var1.func_145825_b(), var1.func_70302_i_(), var1.func_145818_k_()));
      this.field_71070_bA = new ContainerHopper(this.field_71071_by, var1);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_96125_a(EntityMinecartHopper var1) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 9, var1.func_145825_b(), var1.func_70302_i_(), var1.func_145818_k_()));
      this.field_71070_bA = new ContainerHopper(this.field_71071_by, var1);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_146101_a(TileEntityFurnace var1) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 2, var1.func_145825_b(), var1.func_70302_i_(), var1.func_145818_k_()));
      this.field_71070_bA = new ContainerFurnace(this.field_71071_by, var1);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_146102_a(TileEntityDispenser var1) {
      this.func_71117_bO();
      this.field_71135_a
         .func_147359_a(
            new S2DPacketOpenWindow(
               this.field_71139_cq, var1 instanceof TileEntityDropper ? 10 : 3, var1.func_145825_b(), var1.func_70302_i_(), var1.func_145818_k_()
            )
         );
      this.field_71070_bA = new ContainerDispenser(this.field_71071_by, var1);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_146098_a(TileEntityBrewingStand var1) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 5, var1.func_145825_b(), var1.func_70302_i_(), var1.func_145818_k_()));
      this.field_71070_bA = new ContainerBrewingStand(this.field_71071_by, var1);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_146104_a(TileEntityBeacon var1) {
      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 7, var1.func_145825_b(), var1.func_70302_i_(), var1.func_145818_k_()));
      this.field_71070_bA = new ContainerBeacon(this.field_71071_by, var1);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
   public void func_71030_a(IMerchant var1, String var2) {
      this.func_71117_bO();
      this.field_71070_bA = new ContainerMerchant(this.field_71071_by, var1, this.field_70170_p);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
      InventoryMerchant var3 = ((ContainerMerchant)this.field_71070_bA).func_75174_d();
      this.field_71135_a.func_147359_a(new S2DPacketOpenWindow(this.field_71139_cq, 6, var2 == null ? "" : var2, var3.func_70302_i_(), var2 != null));
      MerchantRecipeList var4 = var1.func_70934_b(this);
      if (var4 != null) {
         PacketBuffer var5 = new PacketBuffer(Unpooled.buffer());

         try {
            var5.writeInt(this.field_71139_cq);
            var4.func_151391_a(var5);
            this.field_71135_a.func_147359_a(new S3FPacketCustomPayload("MC|TrList", var5));
         } catch (IOException var10) {
            field_147102_bM.error("Couldn't send trade list", var10);
         } finally {
            var5.release();
         }
      }
   }

   @Override
   public void func_110298_a(EntityHorse var1, IInventory var2) {
      if (this.field_71070_bA != this.field_71069_bz) {
         this.func_71053_j();
      }

      this.func_71117_bO();
      this.field_71135_a
         .func_147359_a(
            new S2DPacketOpenWindow(this.field_71139_cq, 11, var2.func_145825_b(), var2.func_70302_i_(), var2.func_145818_k_(), var1.func_145782_y())
         );
      this.field_71070_bA = new ContainerHorseInventory(this.field_71071_by, var2, var1);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   @Override
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

   @Override
   public void func_71110_a(Container var1, List var2) {
      this.field_71135_a.func_147359_a(new S30PacketWindowItems(var1.field_75152_c, var2));
      this.field_71135_a.func_147359_a(new S2FPacketSetSlot(-1, -1, this.field_71071_by.func_70445_o()));
   }

   @Override
   public void func_71112_a(Container var1, int var2, int var3) {
      this.field_71135_a.func_147359_a(new S31PacketWindowProperty(var1.field_75152_c, var2, var3));
   }

   @Override
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

   @Override
   public void func_71064_a(StatBase var1, int var2) {
      if (var1 != null) {
         this.field_147103_bO.func_150871_b(this, var1, var2);

         for(ScoreObjective var4 : this.func_96123_co().func_96520_a(var1.func_150952_k())) {
            this.func_96123_co().func_96529_a(this.func_70005_c_(), var4).func_96648_a();
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

   @Override
   public void func_146105_b(IChatComponent var1) {
      this.field_71135_a.func_147359_a(new S02PacketChat(var1));
   }

   @Override
   protected void func_71036_o() {
      this.field_71135_a.func_147359_a(new S19PacketEntityStatus(this, (byte)9));
      super.func_71036_o();
   }

   @Override
   public void func_71008_a(ItemStack var1, int var2) {
      super.func_71008_a(var1, var2);
      if (var1 != null && var1.func_77973_b() != null && var1.func_77973_b().func_77661_b(var1) == EnumAction.eat) {
         this.func_71121_q().func_73039_n().func_151248_b(this, new S0BPacketAnimation(this, 3));
      }
   }

   @Override
   public void func_71049_a(EntityPlayer var1, boolean var2) {
      super.func_71049_a(var1, var2);
      this.field_71144_ck = -1;
      this.field_71149_ch = -1.0F;
      this.field_71146_ci = -1;
      this.field_71130_g.addAll(((EntityPlayerMP)var1).field_71130_g);
   }

   @Override
   protected void func_70670_a(PotionEffect var1) {
      super.func_70670_a(var1);
      this.field_71135_a.func_147359_a(new S1DPacketEntityEffect(this.func_145782_y(), var1));
   }

   @Override
   protected void func_70695_b(PotionEffect var1, boolean var2) {
      super.func_70695_b(var1, var2);
      this.field_71135_a.func_147359_a(new S1DPacketEntityEffect(this.func_145782_y(), var1));
   }

   @Override
   protected void func_70688_c(PotionEffect var1) {
      super.func_70688_c(var1);
      this.field_71135_a.func_147359_a(new S1EPacketRemoveEntityEffect(this.func_145782_y(), var1));
   }

   @Override
   public void func_70634_a(double var1, double var3, double var5) {
      this.field_71135_a.func_147364_a(var1, var3, var5, this.field_70177_z, this.field_70125_A);
   }

   @Override
   public void func_71009_b(Entity var1) {
      this.func_71121_q().func_73039_n().func_151248_b(this, new S0BPacketAnimation(var1, 4));
   }

   @Override
   public void func_71047_c(Entity var1) {
      this.func_71121_q().func_73039_n().func_151248_b(this, new S0BPacketAnimation(var1, 5));
   }

   @Override
   public void func_71016_p() {
      if (this.field_71135_a != null) {
         this.field_71135_a.func_147359_a(new S39PacketPlayerAbilities(this.field_71075_bZ));
      }
   }

   public WorldServer func_71121_q() {
      return (WorldServer)this.field_70170_p;
   }

   @Override
   public void func_71033_a(WorldSettings$GameType var1) {
      this.field_71134_c.func_73076_a(var1);
      this.field_71135_a.func_147359_a(new S2BPacketChangeGameState(3, (float)var1.func_77148_a()));
   }

   @Override
   public void func_145747_a(IChatComponent var1) {
      this.field_71135_a.func_147359_a(new S02PacketChat(var1));
   }

   @Override
   public boolean func_70003_b(int var1, String var2) {
      if ("seed".equals(var2) && !this.field_71133_b.func_71262_S()) {
         return true;
      } else if (!"tell".equals(var2) && !"help".equals(var2) && !"me".equals(var2)) {
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
      return var1.substring(0, var1.indexOf(":"));
   }

   public void func_147100_a(C15PacketClientSettings var1) {
      this.field_71148_cg = var1.func_149524_c();
      int var2 = 256 >> var1.func_149521_d();
      if (var2 > 3 && var2 < 20) {
      }

      this.field_71143_cn = var1.func_149523_e();
      this.field_71140_co = var1.func_149520_f();
      if (this.field_71133_b.func_71264_H() && this.field_71133_b.func_71214_G().equals(this.func_70005_c_())) {
         this.field_71133_b.func_147139_a(var1.func_149518_g());
      }

      this.func_82239_b(1, !var1.func_149519_h());
   }

   public EntityPlayer$EnumChatVisibility func_147096_v() {
      return this.field_71143_cn;
   }

   public void func_147095_a(String var1) {
      this.field_71135_a.func_147359_a(new S3FPacketCustomPayload("MC|RPack", var1.getBytes(Charsets.UTF_8)));
   }

   @Override
   public ChunkCoordinates func_82114_b() {
      return new ChunkCoordinates(
         MathHelper.func_76128_c(this.field_70165_t), MathHelper.func_76128_c(this.field_70163_u + 0.5), MathHelper.func_76128_c(this.field_70161_v)
      );
   }

   public void func_143004_u() {
      this.field_143005_bX = MinecraftServer.func_130071_aq();
   }

   public StatisticsFile func_147099_x() {
      return this.field_147103_bO;
   }

   public void func_152339_d(Entity var1) {
      if (var1 instanceof EntityPlayer) {
         this.field_71135_a.func_147359_a(new S13PacketDestroyEntities(var1.func_145782_y()));
      } else {
         this.field_71130_g.add(var1.func_145782_y());
      }
   }

   public long func_154331_x() {
      return this.field_143005_bX;
   }
}
