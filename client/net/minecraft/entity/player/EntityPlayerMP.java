package net.minecraft.entity.player;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMapBase;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ServerRecipeBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerLook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.CooldownTrackerServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.GameType;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.loot.ILootContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayerMP extends EntityPlayer implements IContainerListener {
   private static final Logger field_147102_bM = LogManager.getLogger();
   private String field_71148_cg = "en_US";
   public NetHandlerPlayServer field_71135_a;
   public final MinecraftServer field_71133_b;
   public final PlayerInteractionManager field_71134_c;
   public double field_71131_d;
   public double field_71132_e;
   private final List<Integer> field_71130_g = Lists.newLinkedList();
   private final PlayerAdvancements field_192042_bX;
   private final StatisticsManagerServer field_147103_bO;
   private float field_130068_bO = 1.4E-45F;
   private int field_184852_bV = -2147483648;
   private int field_184853_bW = -2147483648;
   private int field_184854_bX = -2147483648;
   private int field_184855_bY = -2147483648;
   private int field_184856_bZ = -2147483648;
   private float field_71149_ch = -1.0E8F;
   private int field_71146_ci = -99999999;
   private boolean field_71147_cj = true;
   private int field_71144_ck = -99999999;
   private int field_147101_bU = 60;
   private EntityPlayer.EnumChatVisibility field_71143_cn;
   private boolean field_71140_co = true;
   private long field_143005_bX = Util.func_211177_b();
   private Entity field_175401_bS;
   private boolean field_184851_cj;
   private boolean field_192040_cp;
   private final ServerRecipeBook field_192041_cq;
   private Vec3d field_193107_ct;
   private int field_193108_cu;
   private boolean field_193109_cv;
   private Vec3d field_193110_cw;
   private int field_71139_cq;
   public boolean field_71137_h;
   public int field_71138_i;
   public boolean field_71136_j;

   public EntityPlayerMP(MinecraftServer var1, WorldServer var2, GameProfile var3, PlayerInteractionManager var4) {
      super(var2, var3);
      var4.field_73090_b = this;
      this.field_71134_c = var4;
      this.field_71133_b = var1;
      this.field_192041_cq = new ServerRecipeBook(var1.func_199529_aN());
      this.field_147103_bO = var1.func_184103_al().func_152602_a(this);
      this.field_192042_bX = var1.func_184103_al().func_192054_h(this);
      this.field_70138_W = 1.0F;
      this.func_205734_a(var2);
   }

   private void func_205734_a(WorldServer var1) {
      BlockPos var2 = var1.func_175694_M();
      if (var1.field_73011_w.func_191066_m() && var1.func_72912_H().func_76077_q() != GameType.ADVENTURE) {
         int var3 = Math.max(0, this.field_71133_b.func_184108_a(var1));
         int var4 = MathHelper.func_76128_c(var1.func_175723_af().func_177729_b((double)var2.func_177958_n(), (double)var2.func_177952_p()));
         if (var4 < var3) {
            var3 = var4;
         }

         if (var4 <= 1) {
            var3 = 1;
         }

         int var5 = (var3 * 2 + 1) * (var3 * 2 + 1);
         int var6 = this.func_205735_q(var5);
         int var7 = (new Random()).nextInt(var5);

         for(int var8 = 0; var8 < var5; ++var8) {
            int var9 = (var7 + var6 * var8) % var5;
            int var10 = var9 % (var3 * 2 + 1);
            int var11 = var9 / (var3 * 2 + 1);
            BlockPos var12 = var1.func_201675_m().func_206921_a(var2.func_177958_n() + var10 - var3, var2.func_177952_p() + var11 - var3, false);
            if (var12 != null) {
               this.func_174828_a(var12, 0.0F, 0.0F);
               if (var1.func_195586_b(this, this.func_174813_aQ())) {
                  break;
               }
            }
         }
      } else {
         this.func_174828_a(var2, 0.0F, 0.0F);

         while(!var1.func_195586_b(this, this.func_174813_aQ()) && this.field_70163_u < 255.0D) {
            this.func_70107_b(this.field_70165_t, this.field_70163_u + 1.0D, this.field_70161_v);
         }
      }

   }

   private int func_205735_q(int var1) {
      return var1 <= 16 ? var1 - 1 : 17;
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("playerGameType", 99)) {
         if (this.func_184102_h().func_104056_am()) {
            this.field_71134_c.func_73076_a(this.func_184102_h().func_71265_f());
         } else {
            this.field_71134_c.func_73076_a(GameType.func_77146_a(var1.func_74762_e("playerGameType")));
         }
      }

      if (var1.func_150297_b("enteredNetherPosition", 10)) {
         NBTTagCompound var2 = var1.func_74775_l("enteredNetherPosition");
         this.field_193110_cw = new Vec3d(var2.func_74769_h("x"), var2.func_74769_h("y"), var2.func_74769_h("z"));
      }

      this.field_192040_cp = var1.func_74767_n("seenCredits");
      if (var1.func_150297_b("recipeBook", 10)) {
         this.field_192041_cq.func_192825_a(var1.func_74775_l("recipeBook"));
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("playerGameType", this.field_71134_c.func_73081_b().func_77148_a());
      var1.func_74757_a("seenCredits", this.field_192040_cp);
      if (this.field_193110_cw != null) {
         NBTTagCompound var2 = new NBTTagCompound();
         var2.func_74780_a("x", this.field_193110_cw.field_72450_a);
         var2.func_74780_a("y", this.field_193110_cw.field_72448_b);
         var2.func_74780_a("z", this.field_193110_cw.field_72449_c);
         var1.func_74782_a("enteredNetherPosition", var2);
      }

      Entity var6 = this.func_184208_bv();
      Entity var3 = this.func_184187_bx();
      if (var3 != null && var6 != this && var6.func_200601_bK()) {
         NBTTagCompound var4 = new NBTTagCompound();
         NBTTagCompound var5 = new NBTTagCompound();
         var6.func_70039_c(var5);
         var4.func_186854_a("Attach", var3.func_110124_au());
         var4.func_74782_a("Entity", var5);
         var1.func_74782_a("RootVehicle", var4);
      }

      var1.func_74782_a("recipeBook", this.field_192041_cq.func_192824_e());
   }

   public void func_195394_a(int var1) {
      float var2 = (float)this.func_71050_bK();
      float var3 = (var2 - 1.0F) / var2;
      this.field_71106_cc = MathHelper.func_76131_a((float)var1 / var2, 0.0F, var3);
      this.field_71144_ck = -1;
   }

   public void func_195399_b(int var1) {
      this.field_71068_ca = var1;
      this.field_71144_ck = -1;
   }

   public void func_82242_a(int var1) {
      super.func_82242_a(var1);
      this.field_71144_ck = -1;
   }

   public void func_192024_a(ItemStack var1, int var2) {
      super.func_192024_a(var1, var2);
      this.field_71144_ck = -1;
   }

   public void func_71116_b() {
      this.field_71070_bA.func_75132_a(this);
   }

   public void func_152111_bt() {
      super.func_152111_bt();
      this.field_71135_a.func_147359_a(new SPacketCombatEvent(this.func_110142_aN(), SPacketCombatEvent.Event.ENTER_COMBAT));
   }

   public void func_152112_bu() {
      super.func_152112_bu();
      this.field_71135_a.func_147359_a(new SPacketCombatEvent(this.func_110142_aN(), SPacketCombatEvent.Event.END_COMBAT));
   }

   protected void func_191955_a(IBlockState var1) {
      CriteriaTriggers.field_192124_d.func_192193_a(this, var1);
   }

   protected CooldownTracker func_184815_l() {
      return new CooldownTrackerServer(this);
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

         this.field_71135_a.func_147359_a(new SPacketDestroyEntities(var2));
      }

      Entity var5 = this.func_175398_C();
      if (var5 != this) {
         if (var5.func_70089_S()) {
            this.func_70080_a(var5.field_70165_t, var5.field_70163_u, var5.field_70161_v, var5.field_70177_z, var5.field_70125_A);
            this.field_71133_b.func_184103_al().func_72358_d(this);
            if (this.func_70093_af()) {
               this.func_175399_e(this);
            }
         } else {
            this.func_175399_e(this);
         }
      }

      CriteriaTriggers.field_193135_v.func_193182_a(this);
      if (this.field_193107_ct != null) {
         CriteriaTriggers.field_193133_t.func_193162_a(this, this.field_193107_ct, this.field_70173_aa - this.field_193108_cu);
      }

      this.field_192042_bX.func_192741_b(this);
   }

   public void func_71127_g() {
      try {
         super.func_70071_h_();

         for(int var1 = 0; var1 < this.field_71071_by.func_70302_i_(); ++var1) {
            ItemStack var5 = this.field_71071_by.func_70301_a(var1);
            if (var5.func_77973_b().func_77643_m_()) {
               Packet var6 = ((ItemMapBase)var5.func_77973_b()).func_150911_c(var5, this.field_70170_p, this);
               if (var6 != null) {
                  this.field_71135_a.func_147359_a(var6);
               }
            }
         }

         if (this.func_110143_aJ() != this.field_71149_ch || this.field_71146_ci != this.field_71100_bB.func_75116_a() || this.field_71100_bB.func_75115_e() == 0.0F != this.field_71147_cj) {
            this.field_71135_a.func_147359_a(new SPacketUpdateHealth(this.func_110143_aJ(), this.field_71100_bB.func_75116_a(), this.field_71100_bB.func_75115_e()));
            this.field_71149_ch = this.func_110143_aJ();
            this.field_71146_ci = this.field_71100_bB.func_75116_a();
            this.field_71147_cj = this.field_71100_bB.func_75115_e() == 0.0F;
         }

         if (this.func_110143_aJ() + this.func_110139_bj() != this.field_130068_bO) {
            this.field_130068_bO = this.func_110143_aJ() + this.func_110139_bj();
            this.func_184849_a(ScoreCriteria.field_96638_f, MathHelper.func_76123_f(this.field_130068_bO));
         }

         if (this.field_71100_bB.func_75116_a() != this.field_184852_bV) {
            this.field_184852_bV = this.field_71100_bB.func_75116_a();
            this.func_184849_a(ScoreCriteria.field_186698_h, MathHelper.func_76123_f((float)this.field_184852_bV));
         }

         if (this.func_70086_ai() != this.field_184853_bW) {
            this.field_184853_bW = this.func_70086_ai();
            this.func_184849_a(ScoreCriteria.field_186699_i, MathHelper.func_76123_f((float)this.field_184853_bW));
         }

         if (this.func_70658_aO() != this.field_184854_bX) {
            this.field_184854_bX = this.func_70658_aO();
            this.func_184849_a(ScoreCriteria.field_186700_j, MathHelper.func_76123_f((float)this.field_184854_bX));
         }

         if (this.field_71067_cb != this.field_184856_bZ) {
            this.field_184856_bZ = this.field_71067_cb;
            this.func_184849_a(ScoreCriteria.field_186701_k, MathHelper.func_76123_f((float)this.field_184856_bZ));
         }

         if (this.field_71068_ca != this.field_184855_bY) {
            this.field_184855_bY = this.field_71068_ca;
            this.func_184849_a(ScoreCriteria.field_186702_l, MathHelper.func_76123_f((float)this.field_184855_bY));
         }

         if (this.field_71067_cb != this.field_71144_ck) {
            this.field_71144_ck = this.field_71067_cb;
            this.field_71135_a.func_147359_a(new SPacketSetExperience(this.field_71106_cc, this.field_71067_cb, this.field_71068_ca));
         }

         if (this.field_70173_aa % 20 == 0) {
            CriteriaTriggers.field_192135_o.func_192215_a(this);
         }

      } catch (Throwable var4) {
         CrashReport var2 = CrashReport.func_85055_a(var4, "Ticking player");
         CrashReportCategory var3 = var2.func_85058_a("Player being ticked");
         this.func_85029_a(var3);
         throw new ReportedException(var2);
      }
   }

   private void func_184849_a(ScoreCriteria var1, int var2) {
      this.func_96123_co().func_197893_a(var1, this.func_195047_I_(), (var1x) -> {
         var1x.func_96647_c(var2);
      });
   }

   public void func_70645_a(DamageSource var1) {
      boolean var2 = this.field_70170_p.func_82736_K().func_82766_b("showDeathMessages");
      if (var2) {
         ITextComponent var3 = this.func_110142_aN().func_151521_b();
         this.field_71135_a.func_211148_a(new SPacketCombatEvent(this.func_110142_aN(), SPacketCombatEvent.Event.ENTITY_DIED, var3), (var2x) -> {
            if (!var2x.isSuccess()) {
               boolean var3x = true;
               String var4 = var3.func_212636_a(256);
               TextComponentTranslation var5 = new TextComponentTranslation("death.attack.message_too_long", new Object[]{(new TextComponentString(var4)).func_211708_a(TextFormatting.YELLOW)});
               ITextComponent var6 = (new TextComponentTranslation("death.attack.even_more_magic", new Object[]{this.func_145748_c_()})).func_211710_a((var1) -> {
                  var1.func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, var5));
               });
               this.field_71135_a.func_147359_a(new SPacketCombatEvent(this.func_110142_aN(), SPacketCombatEvent.Event.ENTITY_DIED, var6));
            }

         });
         Team var4 = this.func_96124_cp();
         if (var4 != null && var4.func_178771_j() != Team.EnumVisible.ALWAYS) {
            if (var4.func_178771_j() == Team.EnumVisible.HIDE_FOR_OTHER_TEAMS) {
               this.field_71133_b.func_184103_al().func_177453_a(this, var3);
            } else if (var4.func_178771_j() == Team.EnumVisible.HIDE_FOR_OWN_TEAM) {
               this.field_71133_b.func_184103_al().func_177452_b(this, var3);
            }
         } else {
            this.field_71133_b.func_184103_al().func_148539_a(var3);
         }
      } else {
         this.field_71135_a.func_147359_a(new SPacketCombatEvent(this.func_110142_aN(), SPacketCombatEvent.Event.ENTITY_DIED));
      }

      this.func_192030_dh();
      if (!this.field_70170_p.func_82736_K().func_82766_b("keepInventory") && !this.func_175149_v()) {
         this.func_190776_cN();
         this.field_71071_by.func_70436_m();
      }

      this.func_96123_co().func_197893_a(ScoreCriteria.field_96642_c, this.func_195047_I_(), Score::func_96648_a);
      EntityLivingBase var5 = this.func_94060_bK();
      if (var5 != null) {
         this.func_71029_a(StatList.field_199091_i.func_199076_b(var5.func_200600_R()));
         var5.func_191956_a(this, this.field_70744_aE, var1);
      }

      this.func_195066_a(StatList.field_188069_A);
      this.func_175145_a(StatList.field_199092_j.func_199076_b(StatList.field_188098_h));
      this.func_175145_a(StatList.field_199092_j.func_199076_b(StatList.field_203284_n));
      this.func_70066_B();
      this.func_70052_a(0, false);
      this.func_110142_aN().func_94549_h();
   }

   public void func_191956_a(Entity var1, int var2, DamageSource var3) {
      if (var1 != this) {
         super.func_191956_a(var1, var2, var3);
         this.func_85039_t(var2);
         String var4 = this.func_195047_I_();
         String var5 = var1.func_195047_I_();
         this.func_96123_co().func_197893_a(ScoreCriteria.field_96640_e, var4, Score::func_96648_a);
         if (var1 instanceof EntityPlayer) {
            this.func_195066_a(StatList.field_75932_A);
            this.func_96123_co().func_197893_a(ScoreCriteria.field_96639_d, var4, Score::func_96648_a);
         } else {
            this.func_195066_a(StatList.field_188070_B);
         }

         this.func_195398_a(var4, var5, ScoreCriteria.field_197913_m);
         this.func_195398_a(var5, var4, ScoreCriteria.field_197914_n);
         CriteriaTriggers.field_192122_b.func_192211_a(this, var1, var3);
      }
   }

   private void func_195398_a(String var1, String var2, ScoreCriteria[] var3) {
      ScorePlayerTeam var4 = this.func_96123_co().func_96509_i(var2);
      if (var4 != null) {
         int var5 = var4.func_178775_l().func_175746_b();
         if (var5 >= 0 && var5 < var3.length) {
            this.func_96123_co().func_197893_a(var3[var5], var1, Score::func_96648_a);
         }
      }

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
                  Entity var6 = var5.func_212360_k();
                  if (var6 instanceof EntityPlayer && !this.func_96122_a((EntityPlayer)var6)) {
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

   @Nullable
   public Entity func_212321_a(DimensionType var1) {
      this.field_184851_cj = true;
      if (this.field_71093_bK == DimensionType.OVERWORLD && var1 == DimensionType.NETHER) {
         this.field_193110_cw = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      } else if (this.field_71093_bK != DimensionType.NETHER && var1 != DimensionType.OVERWORLD) {
         this.field_193110_cw = null;
      }

      if (this.field_71093_bK == DimensionType.THE_END && var1 == DimensionType.THE_END) {
         this.field_70170_p.func_72900_e(this);
         if (!this.field_71136_j) {
            this.field_71136_j = true;
            this.field_71135_a.func_147359_a(new SPacketChangeGameState(4, this.field_192040_cp ? 0.0F : 1.0F));
            this.field_192040_cp = true;
         }

         return this;
      } else {
         if (this.field_71093_bK == DimensionType.OVERWORLD && var1 == DimensionType.THE_END) {
            var1 = DimensionType.THE_END;
         }

         this.field_71133_b.func_184103_al().func_187242_a(this, var1);
         this.field_71135_a.func_147359_a(new SPacketEffect(1032, BlockPos.field_177992_a, 0, false));
         this.field_71144_ck = -1;
         this.field_71149_ch = -1.0F;
         this.field_71146_ci = -1;
         return this;
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
         SPacketUpdateTileEntity var2 = var1.func_189518_D_();
         if (var2 != null) {
            this.field_71135_a.func_147359_a(var2);
         }
      }

   }

   public void func_71001_a(Entity var1, int var2) {
      super.func_71001_a(var1, var2);
      this.field_71070_bA.func_75142_b();
   }

   public EntityPlayer.SleepResult func_180469_a(BlockPos var1) {
      EntityPlayer.SleepResult var2 = super.func_180469_a(var1);
      if (var2 == EntityPlayer.SleepResult.OK) {
         this.func_195066_a(StatList.field_188064_ad);
         SPacketUseBed var3 = new SPacketUseBed(this, var1);
         this.func_71121_q().func_73039_n().func_151247_a(this, var3);
         this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         this.field_71135_a.func_147359_a(var3);
         CriteriaTriggers.field_192136_p.func_192215_a(this);
      }

      return var2;
   }

   public void func_70999_a(boolean var1, boolean var2, boolean var3) {
      if (this.func_70608_bn()) {
         this.func_71121_q().func_73039_n().func_151248_b(this, new SPacketAnimation(this, 2));
      }

      super.func_70999_a(var1, var2, var3);
      if (this.field_71135_a != null) {
         this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
      }

   }

   public boolean func_184205_a(Entity var1, boolean var2) {
      Entity var3 = this.func_184187_bx();
      if (!super.func_184205_a(var1, var2)) {
         return false;
      } else {
         Entity var4 = this.func_184187_bx();
         if (var4 != var3 && this.field_71135_a != null) {
            this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
         }

         return true;
      }
   }

   public void func_184210_p() {
      Entity var1 = this.func_184187_bx();
      super.func_184210_p();
      Entity var2 = this.func_184187_bx();
      if (var2 != var1 && this.field_71135_a != null) {
         this.field_71135_a.func_147364_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
      }

   }

   public boolean func_180431_b(DamageSource var1) {
      return super.func_180431_b(var1) || this.func_184850_K();
   }

   protected void func_184231_a(double var1, boolean var3, IBlockState var4, BlockPos var5) {
   }

   protected void func_184594_b(BlockPos var1) {
      if (!this.func_175149_v()) {
         super.func_184594_b(var1);
      }

   }

   public void func_71122_b(double var1, boolean var3) {
      int var4 = MathHelper.func_76128_c(this.field_70165_t);
      int var5 = MathHelper.func_76128_c(this.field_70163_u - 0.20000000298023224D);
      int var6 = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos var7 = new BlockPos(var4, var5, var6);
      IBlockState var8 = this.field_70170_p.func_180495_p(var7);
      if (var8.func_196958_f()) {
         BlockPos var9 = var7.func_177977_b();
         IBlockState var10 = this.field_70170_p.func_180495_p(var9);
         Block var11 = var10.func_177230_c();
         if (var11 instanceof BlockFence || var11 instanceof BlockWall || var11 instanceof BlockFenceGate) {
            var7 = var9;
            var8 = var10;
         }
      }

      super.func_184231_a(var1, var3, var8, var7);
   }

   public void func_175141_a(TileEntitySign var1) {
      var1.func_145912_a(this);
      this.field_71135_a.func_147359_a(new SPacketSignEditorOpen(var1.func_174877_v()));
   }

   private void func_71117_bO() {
      this.field_71139_cq = this.field_71139_cq % 100 + 1;
   }

   public void func_180468_a(IInteractionObject var1) {
      if (var1 instanceof ILootContainer && ((ILootContainer)var1).func_184276_b() != null && this.func_175149_v()) {
         this.func_146105_b((new TextComponentTranslation("container.spectatorCantOpen", new Object[0])).func_211708_a(TextFormatting.RED), true);
      } else {
         this.func_71117_bO();
         this.field_71135_a.func_147359_a(new SPacketOpenWindow(this.field_71139_cq, var1.func_174875_k(), var1.func_145748_c_()));
         this.field_71070_bA = var1.func_174876_a(this.field_71071_by, this);
         this.field_71070_bA.field_75152_c = this.field_71139_cq;
         this.field_71070_bA.func_75132_a(this);
      }
   }

   public void func_71007_a(IInventory var1) {
      if (var1 instanceof ILootContainer && ((ILootContainer)var1).func_184276_b() != null && this.func_175149_v()) {
         this.func_146105_b((new TextComponentTranslation("container.spectatorCantOpen", new Object[0])).func_211708_a(TextFormatting.RED), true);
      } else {
         if (this.field_71070_bA != this.field_71069_bz) {
            this.func_71053_j();
         }

         if (var1 instanceof ILockableContainer) {
            ILockableContainer var2 = (ILockableContainer)var1;
            if (var2.func_174893_q_() && !this.func_175146_a(var2.func_174891_i()) && !this.func_175149_v()) {
               this.field_71135_a.func_147359_a(new SPacketChat(new TextComponentTranslation("container.isLocked", new Object[]{var1.func_145748_c_()}), ChatType.GAME_INFO));
               this.field_71135_a.func_147359_a(new SPacketSoundEffect(SoundEvents.field_187654_U, SoundCategory.BLOCKS, this.field_70165_t, this.field_70163_u, this.field_70161_v, 1.0F, 1.0F));
               return;
            }
         }

         this.func_71117_bO();
         if (var1 instanceof IInteractionObject) {
            this.field_71135_a.func_147359_a(new SPacketOpenWindow(this.field_71139_cq, ((IInteractionObject)var1).func_174875_k(), var1.func_145748_c_(), var1.func_70302_i_()));
            this.field_71070_bA = ((IInteractionObject)var1).func_174876_a(this.field_71071_by, this);
         } else {
            this.field_71135_a.func_147359_a(new SPacketOpenWindow(this.field_71139_cq, "minecraft:container", var1.func_145748_c_(), var1.func_70302_i_()));
            this.field_71070_bA = new ContainerChest(this.field_71071_by, var1, this);
         }

         this.field_71070_bA.field_75152_c = this.field_71139_cq;
         this.field_71070_bA.func_75132_a(this);
      }
   }

   public void func_180472_a(IMerchant var1) {
      this.func_71117_bO();
      this.field_71070_bA = new ContainerMerchant(this.field_71071_by, var1, this.field_70170_p);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
      InventoryMerchant var2 = ((ContainerMerchant)this.field_71070_bA).func_75174_d();
      ITextComponent var3 = var1.func_145748_c_();
      this.field_71135_a.func_147359_a(new SPacketOpenWindow(this.field_71139_cq, "minecraft:villager", var3, var2.func_70302_i_()));
      MerchantRecipeList var4 = var1.func_70934_b(this);
      if (var4 != null) {
         PacketBuffer var5 = new PacketBuffer(Unpooled.buffer());
         var5.writeInt(this.field_71139_cq);
         var4.func_151391_a(var5);
         this.field_71135_a.func_147359_a(new SPacketCustomPayload(SPacketCustomPayload.field_209910_a, var5));
      }

   }

   public void func_184826_a(AbstractHorse var1, IInventory var2) {
      if (this.field_71070_bA != this.field_71069_bz) {
         this.func_71053_j();
      }

      this.func_71117_bO();
      this.field_71135_a.func_147359_a(new SPacketOpenWindow(this.field_71139_cq, "EntityHorse", var2.func_145748_c_(), var2.func_70302_i_(), var1.func_145782_y()));
      this.field_71070_bA = new ContainerHorseInventory(this.field_71071_by, var2, var1, this);
      this.field_71070_bA.field_75152_c = this.field_71139_cq;
      this.field_71070_bA.func_75132_a(this);
   }

   public void func_184814_a(ItemStack var1, EnumHand var2) {
      Item var3 = var1.func_77973_b();
      if (var3 == Items.field_151164_bB) {
         PacketBuffer var4 = new PacketBuffer(Unpooled.buffer());
         var4.func_179249_a(var2);
         this.field_71135_a.func_147359_a(new SPacketCustomPayload(SPacketCustomPayload.field_209912_c, var4));
      }

   }

   public void func_184824_a(TileEntityCommandBlock var1) {
      var1.func_184252_d(true);
      this.func_147097_b(var1);
   }

   public void func_71111_a(Container var1, int var2, ItemStack var3) {
      if (!(var1.func_75139_a(var2) instanceof SlotCrafting)) {
         if (var1 == this.field_71069_bz) {
            CriteriaTriggers.field_192125_e.func_192208_a(this, this.field_71071_by);
         }

         if (!this.field_71137_h) {
            this.field_71135_a.func_147359_a(new SPacketSetSlot(var1.field_75152_c, var2, var3));
         }
      }
   }

   public void func_71120_a(Container var1) {
      this.func_71110_a(var1, var1.func_75138_a());
   }

   public void func_71110_a(Container var1, NonNullList<ItemStack> var2) {
      this.field_71135_a.func_147359_a(new SPacketWindowItems(var1.field_75152_c, var2));
      this.field_71135_a.func_147359_a(new SPacketSetSlot(-1, -1, this.field_71071_by.func_70445_o()));
   }

   public void func_71112_a(Container var1, int var2, int var3) {
      this.field_71135_a.func_147359_a(new SPacketWindowProperty(var1.field_75152_c, var2, var3));
   }

   public void func_175173_a(Container var1, IInventory var2) {
      for(int var3 = 0; var3 < var2.func_174890_g(); ++var3) {
         this.field_71135_a.func_147359_a(new SPacketWindowProperty(var1.field_75152_c, var3, var2.func_174887_a_(var3)));
      }

   }

   public void func_71053_j() {
      this.field_71135_a.func_147359_a(new SPacketCloseWindow(this.field_71070_bA.field_75152_c));
      this.func_71128_l();
   }

   public void func_71113_k() {
      if (!this.field_71137_h) {
         this.field_71135_a.func_147359_a(new SPacketSetSlot(-1, -1, this.field_71071_by.func_70445_o()));
      }
   }

   public void func_71128_l() {
      this.field_71070_bA.func_75134_a(this);
      this.field_71070_bA = this.field_71069_bz;
   }

   public void func_110430_a(float var1, float var2, boolean var3, boolean var4) {
      if (this.func_184218_aH()) {
         if (var1 >= -1.0F && var1 <= 1.0F) {
            this.field_70702_br = var1;
         }

         if (var2 >= -1.0F && var2 <= 1.0F) {
            this.field_191988_bg = var2;
         }

         this.field_70703_bu = var3;
         this.func_70095_a(var4);
      }

   }

   public void func_71064_a(Stat<?> var1, int var2) {
      this.field_147103_bO.func_150871_b(this, var1, var2);
      this.func_96123_co().func_197893_a(var1, this.func_195047_I_(), (var1x) -> {
         var1x.func_96649_a(var2);
      });
   }

   public void func_175145_a(Stat<?> var1) {
      this.field_147103_bO.func_150873_a(this, var1, 0);
      this.func_96123_co().func_197893_a(var1, this.func_195047_I_(), Score::func_197891_c);
   }

   public int func_195065_a(Collection<IRecipe> var1) {
      return this.field_192041_cq.func_197926_a(var1, this);
   }

   public void func_193102_a(ResourceLocation[] var1) {
      ArrayList var2 = Lists.newArrayList();
      ResourceLocation[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         ResourceLocation var6 = var3[var5];
         IRecipe var7 = this.field_71133_b.func_199529_aN().func_199517_a(var6);
         if (var7 != null) {
            var2.add(var7);
         }
      }

      this.func_195065_a(var2);
   }

   public int func_195069_b(Collection<IRecipe> var1) {
      return this.field_192041_cq.func_197925_b(var1, this);
   }

   public void func_195068_e(int var1) {
      super.func_195068_e(var1);
      this.field_71144_ck = -1;
   }

   public void func_71123_m() {
      this.field_193109_cv = true;
      this.func_184226_ay();
      if (this.field_71083_bS) {
         this.func_70999_a(true, false, false);
      }

   }

   public boolean func_193105_t() {
      return this.field_193109_cv;
   }

   public void func_71118_n() {
      this.field_71149_ch = -1.0E8F;
   }

   public void func_146105_b(ITextComponent var1, boolean var2) {
      this.field_71135_a.func_147359_a(new SPacketChat(var1, var2 ? ChatType.GAME_INFO : ChatType.CHAT));
   }

   protected void func_71036_o() {
      if (!this.field_184627_bm.func_190926_b() && this.func_184587_cr()) {
         this.field_71135_a.func_147359_a(new SPacketEntityStatus(this, (byte)9));
         super.func_71036_o();
      }

   }

   public void func_200602_a(EntityAnchorArgument.Type var1, Vec3d var2) {
      super.func_200602_a(var1, var2);
      this.field_71135_a.func_147359_a(new SPacketPlayerLook(var1, var2.field_72450_a, var2.field_72448_b, var2.field_72449_c));
   }

   public void func_200618_a(EntityAnchorArgument.Type var1, Entity var2, EntityAnchorArgument.Type var3) {
      Vec3d var4 = var3.func_201017_a(var2);
      super.func_200602_a(var1, var4);
      this.field_71135_a.func_147359_a(new SPacketPlayerLook(var1, var2, var3));
   }

   public void func_193104_a(EntityPlayerMP var1, boolean var2) {
      if (var2) {
         this.field_71071_by.func_70455_b(var1.field_71071_by);
         this.func_70606_j(var1.func_110143_aJ());
         this.field_71100_bB = var1.field_71100_bB;
         this.field_71068_ca = var1.field_71068_ca;
         this.field_71067_cb = var1.field_71067_cb;
         this.field_71106_cc = var1.field_71106_cc;
         this.func_85040_s(var1.func_71037_bA());
         this.field_181016_an = var1.field_181016_an;
         this.field_181017_ao = var1.field_181017_ao;
         this.field_181018_ap = var1.field_181018_ap;
      } else if (this.field_70170_p.func_82736_K().func_82766_b("keepInventory") || var1.func_175149_v()) {
         this.field_71071_by.func_70455_b(var1.field_71071_by);
         this.field_71068_ca = var1.field_71068_ca;
         this.field_71067_cb = var1.field_71067_cb;
         this.field_71106_cc = var1.field_71106_cc;
         this.func_85040_s(var1.func_71037_bA());
      }

      this.field_175152_f = var1.field_175152_f;
      this.field_71078_a = var1.field_71078_a;
      this.func_184212_Q().func_187227_b(field_184827_bp, var1.func_184212_Q().func_187225_a(field_184827_bp));
      this.field_71144_ck = -1;
      this.field_71149_ch = -1.0F;
      this.field_71146_ci = -1;
      this.field_192041_cq.func_193824_a(var1.field_192041_cq);
      this.field_71130_g.addAll(var1.field_71130_g);
      this.field_192040_cp = var1.field_192040_cp;
      this.field_193110_cw = var1.field_193110_cw;
      this.func_192029_h(var1.func_192023_dk());
      this.func_192031_i(var1.func_192025_dl());
   }

   protected void func_70670_a(PotionEffect var1) {
      super.func_70670_a(var1);
      this.field_71135_a.func_147359_a(new SPacketEntityEffect(this.func_145782_y(), var1));
      if (var1.func_188419_a() == MobEffects.field_188424_y) {
         this.field_193108_cu = this.field_70173_aa;
         this.field_193107_ct = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      }

      CriteriaTriggers.field_193139_z.func_193153_a(this);
   }

   protected void func_70695_b(PotionEffect var1, boolean var2) {
      super.func_70695_b(var1, var2);
      this.field_71135_a.func_147359_a(new SPacketEntityEffect(this.func_145782_y(), var1));
      CriteriaTriggers.field_193139_z.func_193153_a(this);
   }

   protected void func_70688_c(PotionEffect var1) {
      super.func_70688_c(var1);
      this.field_71135_a.func_147359_a(new SPacketRemoveEntityEffect(this.func_145782_y(), var1.func_188419_a()));
      if (var1.func_188419_a() == MobEffects.field_188424_y) {
         this.field_193107_ct = null;
      }

      CriteriaTriggers.field_193139_z.func_193153_a(this);
   }

   public void func_70634_a(double var1, double var3, double var5) {
      this.field_71135_a.func_147364_a(var1, var3, var5, this.field_70177_z, this.field_70125_A);
   }

   public void func_71009_b(Entity var1) {
      this.func_71121_q().func_73039_n().func_151248_b(this, new SPacketAnimation(var1, 4));
   }

   public void func_71047_c(Entity var1) {
      this.func_71121_q().func_73039_n().func_151248_b(this, new SPacketAnimation(var1, 5));
   }

   public void func_71016_p() {
      if (this.field_71135_a != null) {
         this.field_71135_a.func_147359_a(new SPacketPlayerAbilities(this.field_71075_bZ));
         this.func_175135_B();
      }
   }

   public WorldServer func_71121_q() {
      return (WorldServer)this.field_70170_p;
   }

   public void func_71033_a(GameType var1) {
      this.field_71134_c.func_73076_a(var1);
      this.field_71135_a.func_147359_a(new SPacketChangeGameState(3, (float)var1.func_77148_a()));
      if (var1 == GameType.SPECTATOR) {
         this.func_192030_dh();
         this.func_184210_p();
      } else {
         this.func_175399_e(this);
      }

      this.func_71016_p();
      this.func_175136_bO();
   }

   public boolean func_175149_v() {
      return this.field_71134_c.func_73081_b() == GameType.SPECTATOR;
   }

   public boolean func_184812_l_() {
      return this.field_71134_c.func_73081_b() == GameType.CREATIVE;
   }

   public void func_145747_a(ITextComponent var1) {
      this.func_195395_a(var1, ChatType.SYSTEM);
   }

   public void func_195395_a(ITextComponent var1, ChatType var2) {
      this.field_71135_a.func_211148_a(new SPacketChat(var1, var2), (var3) -> {
         if (!var3.isSuccess() && (var2 == ChatType.GAME_INFO || var2 == ChatType.SYSTEM)) {
            boolean var4 = true;
            String var5 = var1.func_212636_a(256);
            ITextComponent var6 = (new TextComponentString(var5)).func_211708_a(TextFormatting.YELLOW);
            this.field_71135_a.func_147359_a(new SPacketChat((new TextComponentTranslation("multiplayer.message_not_delivered", new Object[]{var6})).func_211708_a(TextFormatting.RED), ChatType.SYSTEM));
         }

      });
   }

   public String func_71114_r() {
      String var1 = this.field_71135_a.field_147371_a.func_74430_c().toString();
      var1 = var1.substring(var1.indexOf("/") + 1);
      var1 = var1.substring(0, var1.indexOf(":"));
      return var1;
   }

   public void func_147100_a(CPacketClientSettings var1) {
      this.field_71148_cg = var1.func_149524_c();
      this.field_71143_cn = var1.func_149523_e();
      this.field_71140_co = var1.func_149520_f();
      this.func_184212_Q().func_187227_b(field_184827_bp, (byte)var1.func_149521_d());
      this.func_184212_Q().func_187227_b(field_184828_bq, (byte)(var1.func_186991_f() == EnumHandSide.LEFT ? 0 : 1));
   }

   public EntityPlayer.EnumChatVisibility func_147096_v() {
      return this.field_71143_cn;
   }

   public void func_175397_a(String var1, String var2) {
      this.field_71135_a.func_147359_a(new SPacketResourcePackSend(var1, var2));
   }

   protected int func_184840_I() {
      return this.field_71133_b.func_211833_a(this.func_146103_bH());
   }

   public void func_143004_u() {
      this.field_143005_bX = Util.func_211177_b();
   }

   public StatisticsManagerServer func_147099_x() {
      return this.field_147103_bO;
   }

   public ServerRecipeBook func_192037_E() {
      return this.field_192041_cq;
   }

   public void func_152339_d(Entity var1) {
      if (var1 instanceof EntityPlayer) {
         this.field_71135_a.func_147359_a(new SPacketDestroyEntities(new int[]{var1.func_145782_y()}));
      } else {
         this.field_71130_g.add(var1.func_145782_y());
      }

   }

   public void func_184848_d(Entity var1) {
      this.field_71130_g.remove(var1.func_145782_y());
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
         this.field_71135_a.func_147359_a(new SPacketCamera(this.field_175401_bS));
         this.func_70634_a(this.field_175401_bS.field_70165_t, this.field_175401_bS.field_70163_u, this.field_175401_bS.field_70161_v);
      }

   }

   protected void func_184173_H() {
      if (this.field_71088_bW > 0 && !this.field_184851_cj) {
         --this.field_71088_bW;
      }

   }

   public void func_71059_n(Entity var1) {
      if (this.field_71134_c.func_73081_b() == GameType.SPECTATOR) {
         this.func_175399_e(var1);
      } else {
         super.func_71059_n(var1);
      }

   }

   public long func_154331_x() {
      return this.field_143005_bX;
   }

   @Nullable
   public ITextComponent func_175396_E() {
      return null;
   }

   public void func_184609_a(EnumHand var1) {
      super.func_184609_a(var1);
      this.func_184821_cY();
   }

   public boolean func_184850_K() {
      return this.field_184851_cj;
   }

   public void func_184846_L() {
      this.field_184851_cj = false;
   }

   public void func_184847_M() {
      this.func_70052_a(7, true);
   }

   public void func_189103_N() {
      this.func_70052_a(7, true);
      this.func_70052_a(7, false);
   }

   public PlayerAdvancements func_192039_O() {
      return this.field_192042_bX;
   }

   @Nullable
   public Vec3d func_193106_Q() {
      return this.field_193110_cw;
   }

   public void func_200619_a(WorldServer var1, double var2, double var4, double var6, float var8, float var9) {
      this.func_175399_e(this);
      this.func_184210_p();
      if (var1 == this.field_70170_p) {
         this.field_71135_a.func_147364_a(var2, var4, var6, var8, var9);
      } else {
         WorldServer var10 = this.func_71121_q();
         this.field_71093_bK = var1.field_73011_w.func_186058_p();
         this.field_71135_a.func_147359_a(new SPacketRespawn(this.field_71093_bK, var10.func_175659_aa(), var10.func_72912_H().func_76067_t(), this.field_71134_c.func_73081_b()));
         this.field_71133_b.func_184103_al().func_187243_f(this);
         var10.func_72973_f(this);
         this.field_70128_L = false;
         this.func_70012_b(var2, var4, var6, var8, var9);
         if (this.func_70089_S()) {
            var10.func_72866_a(this, false);
            var1.func_72838_d(this);
            var1.func_72866_a(this, false);
         }

         this.func_70029_a(var1);
         this.field_71133_b.func_184103_al().func_72375_a(this, var10);
         this.field_71135_a.func_147364_a(var2, var4, var6, var8, var9);
         this.field_71134_c.func_73080_a(var1);
         this.field_71133_b.func_184103_al().func_72354_b(this, var1);
         this.field_71133_b.func_184103_al().func_72385_f(this);
      }

   }
}
