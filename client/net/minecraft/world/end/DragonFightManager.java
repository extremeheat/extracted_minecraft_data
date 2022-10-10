package net.minecraft.world.end;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndCrystalTowerFeature;
import net.minecraft.world.gen.feature.EndGatewayConfig;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.EndSpikes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DragonFightManager {
   private static final Logger field_186107_a = LogManager.getLogger();
   private static final Predicate<Entity> field_186108_b;
   private final BossInfoServer field_186109_c;
   private final WorldServer field_186110_d;
   private final List<Integer> field_186111_e;
   private final BlockPattern field_186112_f;
   private int field_186113_g;
   private int field_186114_h;
   private int field_186115_i;
   private int field_186116_j;
   private boolean field_186117_k;
   private boolean field_186118_l;
   private UUID field_186119_m;
   private boolean field_186120_n;
   private BlockPos field_186121_o;
   private DragonSpawnState field_186122_p;
   private int field_186123_q;
   private List<EntityEnderCrystal> field_186124_r;

   public DragonFightManager(WorldServer var1, NBTTagCompound var2) {
      super();
      this.field_186109_c = (BossInfoServer)(new BossInfoServer(new TextComponentTranslation("entity.minecraft.ender_dragon", new Object[0]), BossInfo.Color.PINK, BossInfo.Overlay.PROGRESS)).func_186742_b(true).func_186743_c(true);
      this.field_186111_e = Lists.newArrayList();
      this.field_186120_n = true;
      this.field_186110_d = var1;
      if (var2.func_150297_b("DragonKilled", 99)) {
         if (var2.func_186855_b("DragonUUID")) {
            this.field_186119_m = var2.func_186857_a("DragonUUID");
         }

         this.field_186117_k = var2.func_74767_n("DragonKilled");
         this.field_186118_l = var2.func_74767_n("PreviouslyKilled");
         if (var2.func_74767_n("IsRespawning")) {
            this.field_186122_p = DragonSpawnState.START;
         }

         if (var2.func_150297_b("ExitPortalLocation", 10)) {
            this.field_186121_o = NBTUtil.func_186861_c(var2.func_74775_l("ExitPortalLocation"));
         }
      } else {
         this.field_186117_k = true;
         this.field_186118_l = true;
      }

      if (var2.func_150297_b("Gateways", 9)) {
         NBTTagList var3 = var2.func_150295_c("Gateways", 3);

         for(int var4 = 0; var4 < var3.size(); ++var4) {
            this.field_186111_e.add(var3.func_186858_c(var4));
         }
      } else {
         this.field_186111_e.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
         Collections.shuffle(this.field_186111_e, new Random(var1.func_72905_C()));
      }

      this.field_186112_f = FactoryBlockPattern.func_177660_a().func_177659_a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").func_177659_a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").func_177659_a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").func_177659_a("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").func_177659_a("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").func_177662_a('#', BlockWorldState.func_177510_a(BlockMatcher.func_177642_a(Blocks.field_150357_h))).func_177661_b();
   }

   public NBTTagCompound func_186088_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      if (this.field_186119_m != null) {
         var1.func_186854_a("DragonUUID", this.field_186119_m);
      }

      var1.func_74757_a("DragonKilled", this.field_186117_k);
      var1.func_74757_a("PreviouslyKilled", this.field_186118_l);
      if (this.field_186121_o != null) {
         var1.func_74782_a("ExitPortalLocation", NBTUtil.func_186859_a(this.field_186121_o));
      }

      NBTTagList var2 = new NBTTagList();
      Iterator var3 = this.field_186111_e.iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         var2.add((INBTBase)(new NBTTagInt(var4)));
      }

      var1.func_74782_a("Gateways", var2);
      return var1;
   }

   public void func_186105_b() {
      this.field_186109_c.func_186758_d(!this.field_186117_k);
      if (++this.field_186116_j >= 20) {
         this.func_186100_j();
         this.field_186116_j = 0;
      }

      DragonFightManager.LoadManager var1 = new DragonFightManager.LoadManager();
      if (!this.field_186109_c.func_186757_c().isEmpty()) {
         if (this.field_186120_n && var1.func_210824_a()) {
            this.func_210827_g();
            this.field_186120_n = false;
         }

         if (this.field_186122_p != null) {
            if (this.field_186124_r == null && var1.func_210824_a()) {
               this.field_186122_p = null;
               this.func_186106_e();
            }

            this.field_186122_p.func_186079_a(this.field_186110_d, this, this.field_186124_r, this.field_186123_q++, this.field_186121_o);
         }

         if (!this.field_186117_k) {
            if ((this.field_186119_m == null || ++this.field_186113_g >= 1200) && var1.func_210824_a()) {
               this.func_210828_h();
               this.field_186113_g = 0;
            }

            if (++this.field_186115_i >= 100 && var1.func_210824_a()) {
               this.func_186101_k();
               this.field_186115_i = 0;
            }
         }
      }

   }

   private void func_210827_g() {
      field_186107_a.info("Scanning for legacy world dragon fight...");
      boolean var1 = this.func_186104_g();
      if (var1) {
         field_186107_a.info("Found that the dragon has been killed in this world already.");
         this.field_186118_l = true;
      } else {
         field_186107_a.info("Found that the dragon has not yet been killed in this world.");
         this.field_186118_l = false;
         this.func_186094_a(false);
      }

      List var2 = this.field_186110_d.func_175644_a(EntityDragon.class, EntitySelectors.field_94557_a);
      if (var2.isEmpty()) {
         this.field_186117_k = true;
      } else {
         EntityDragon var3 = (EntityDragon)var2.get(0);
         this.field_186119_m = var3.func_110124_au();
         field_186107_a.info("Found that there's a dragon still alive ({})", var3);
         this.field_186117_k = false;
         if (!var1) {
            field_186107_a.info("But we didn't have a portal, let's remove it.");
            var3.func_70106_y();
            this.field_186119_m = null;
         }
      }

      if (!this.field_186118_l && this.field_186117_k) {
         this.field_186117_k = false;
      }

   }

   private void func_210828_h() {
      List var1 = this.field_186110_d.func_175644_a(EntityDragon.class, EntitySelectors.field_94557_a);
      if (var1.isEmpty()) {
         field_186107_a.debug("Haven't seen the dragon, respawning it");
         this.func_192445_m();
      } else {
         field_186107_a.debug("Haven't seen our dragon, but found another one to use.");
         this.field_186119_m = ((EntityDragon)var1.get(0)).func_110124_au();
      }

   }

   protected void func_186095_a(DragonSpawnState var1) {
      if (this.field_186122_p == null) {
         throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
      } else {
         this.field_186123_q = 0;
         if (var1 == DragonSpawnState.END) {
            this.field_186122_p = null;
            this.field_186117_k = false;
            EntityDragon var2 = this.func_192445_m();
            Iterator var3 = this.field_186109_c.func_186757_c().iterator();

            while(var3.hasNext()) {
               EntityPlayerMP var4 = (EntityPlayerMP)var3.next();
               CriteriaTriggers.field_192133_m.func_192229_a(var4, var2);
            }
         } else {
            this.field_186122_p = var1;
         }

      }
   }

   private boolean func_186104_g() {
      for(int var1 = -8; var1 <= 8; ++var1) {
         for(int var2 = -8; var2 <= 8; ++var2) {
            Chunk var3 = this.field_186110_d.func_72964_e(var1, var2);
            Iterator var4 = var3.func_177434_r().values().iterator();

            while(var4.hasNext()) {
               TileEntity var5 = (TileEntity)var4.next();
               if (var5 instanceof TileEntityEndPortal) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   @Nullable
   private BlockPattern.PatternHelper func_186091_h() {
      int var1;
      int var2;
      for(var1 = -8; var1 <= 8; ++var1) {
         for(var2 = -8; var2 <= 8; ++var2) {
            Chunk var3 = this.field_186110_d.func_72964_e(var1, var2);
            Iterator var4 = var3.func_177434_r().values().iterator();

            while(var4.hasNext()) {
               TileEntity var5 = (TileEntity)var4.next();
               if (var5 instanceof TileEntityEndPortal) {
                  BlockPattern.PatternHelper var6 = this.field_186112_f.func_177681_a(this.field_186110_d, var5.func_174877_v());
                  if (var6 != null) {
                     BlockPos var7 = var6.func_177670_a(3, 3, 3).func_177508_d();
                     if (this.field_186121_o == null && var7.func_177958_n() == 0 && var7.func_177952_p() == 0) {
                        this.field_186121_o = var7;
                     }

                     return var6;
                  }
               }
            }
         }
      }

      var1 = this.field_186110_d.func_205770_a(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.field_186139_a).func_177956_o();

      for(var2 = var1; var2 >= 0; --var2) {
         BlockPattern.PatternHelper var8 = this.field_186112_f.func_177681_a(this.field_186110_d, new BlockPos(EndPodiumFeature.field_186139_a.func_177958_n(), var2, EndPodiumFeature.field_186139_a.func_177952_p()));
         if (var8 != null) {
            if (this.field_186121_o == null) {
               this.field_186121_o = var8.func_177670_a(3, 3, 3).func_177508_d();
            }

            return var8;
         }
      }

      return null;
   }

   private boolean func_210832_a(int var1, int var2, int var3, int var4) {
      if (this.func_210830_b(var1, var2, var3, var4)) {
         return true;
      } else {
         this.func_210831_c(var1, var2, var3, var4);
         return false;
      }
   }

   private boolean func_210830_b(int var1, int var2, int var3, int var4) {
      boolean var5 = true;

      for(int var6 = var1; var6 <= var2; ++var6) {
         for(int var7 = var3; var7 <= var4; ++var7) {
            Chunk var8 = this.field_186110_d.func_72964_e(var6, var7);
            var5 &= var8.func_201589_g() == ChunkStatus.POSTPROCESSED;
         }
      }

      return var5;
   }

   private void func_210831_c(int var1, int var2, int var3, int var4) {
      int var5;
      for(var5 = var1 - 1; var5 <= var2 + 1; ++var5) {
         this.field_186110_d.func_72964_e(var5, var3 - 1);
         this.field_186110_d.func_72964_e(var5, var4 + 1);
      }

      for(var5 = var3 - 1; var5 <= var4 + 1; ++var5) {
         this.field_186110_d.func_72964_e(var1 - 1, var5);
         this.field_186110_d.func_72964_e(var2 + 1, var5);
      }

   }

   private void func_186100_j() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.field_186110_d.func_175661_b(EntityPlayerMP.class, field_186108_b).iterator();

      while(var2.hasNext()) {
         EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
         this.field_186109_c.func_186760_a(var3);
         var1.add(var3);
      }

      HashSet var5 = Sets.newHashSet(this.field_186109_c.func_186757_c());
      var5.removeAll(var1);
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         EntityPlayerMP var4 = (EntityPlayerMP)var6.next();
         this.field_186109_c.func_186761_b(var4);
      }

   }

   private void func_186101_k() {
      this.field_186115_i = 0;
      this.field_186114_h = 0;
      EndCrystalTowerFeature.EndSpike[] var1 = EndSpikes.func_202466_a(this.field_186110_d);
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EndCrystalTowerFeature.EndSpike var4 = var1[var3];
         this.field_186114_h += this.field_186110_d.func_72872_a(EntityEnderCrystal.class, var4.func_186153_f()).size();
      }

      field_186107_a.debug("Found {} end crystals still alive", this.field_186114_h);
   }

   public void func_186096_a(EntityDragon var1) {
      if (var1.func_110124_au().equals(this.field_186119_m)) {
         this.field_186109_c.func_186735_a(0.0F);
         this.field_186109_c.func_186758_d(false);
         this.func_186094_a(true);
         this.func_186097_l();
         if (!this.field_186118_l) {
            this.field_186110_d.func_175656_a(this.field_186110_d.func_205770_a(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.field_186139_a), Blocks.field_150380_bt.func_176223_P());
         }

         this.field_186118_l = true;
         this.field_186117_k = true;
      }

   }

   private void func_186097_l() {
      if (!this.field_186111_e.isEmpty()) {
         int var1 = (Integer)this.field_186111_e.remove(this.field_186111_e.size() - 1);
         int var2 = (int)(96.0D * Math.cos(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double)var1)));
         int var3 = (int)(96.0D * Math.sin(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double)var1)));
         this.func_186089_a(new BlockPos(var2, 75, var3));
      }
   }

   private void func_186089_a(BlockPos var1) {
      this.field_186110_d.func_175718_b(3000, var1, 0);
      Feature.field_202299_as.func_212245_a(this.field_186110_d, this.field_186110_d.func_72863_F().func_201711_g(), new Random(), var1, new EndGatewayConfig(false));
   }

   private void func_186094_a(boolean var1) {
      EndPodiumFeature var2 = new EndPodiumFeature(var1);
      if (this.field_186121_o == null) {
         for(this.field_186121_o = this.field_186110_d.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.field_186139_a).func_177977_b(); this.field_186110_d.func_180495_p(this.field_186121_o).func_177230_c() == Blocks.field_150357_h && this.field_186121_o.func_177956_o() > this.field_186110_d.func_181545_F(); this.field_186121_o = this.field_186121_o.func_177977_b()) {
         }
      }

      var2.func_212245_a(this.field_186110_d, this.field_186110_d.func_72863_F().func_201711_g(), new Random(), this.field_186121_o, (NoFeatureConfig)IFeatureConfig.field_202429_e);
   }

   private EntityDragon func_192445_m() {
      this.field_186110_d.func_175726_f(new BlockPos(0, 128, 0));
      EntityDragon var1 = new EntityDragon(this.field_186110_d);
      var1.func_184670_cT().func_188758_a(PhaseType.field_188741_a);
      var1.func_70012_b(0.0D, 128.0D, 0.0D, this.field_186110_d.field_73012_v.nextFloat() * 360.0F, 0.0F);
      this.field_186110_d.func_72838_d(var1);
      this.field_186119_m = var1.func_110124_au();
      return var1;
   }

   public void func_186099_b(EntityDragon var1) {
      if (var1.func_110124_au().equals(this.field_186119_m)) {
         this.field_186109_c.func_186735_a(var1.func_110143_aJ() / var1.func_110138_aP());
         this.field_186113_g = 0;
         if (var1.func_145818_k_()) {
            this.field_186109_c.func_186739_a(var1.func_145748_c_());
         }
      }

   }

   public int func_186092_c() {
      return this.field_186114_h;
   }

   public void func_186090_a(EntityEnderCrystal var1, DamageSource var2) {
      if (this.field_186122_p != null && this.field_186124_r.contains(var1)) {
         field_186107_a.debug("Aborting respawn sequence");
         this.field_186122_p = null;
         this.field_186123_q = 0;
         this.func_186087_f();
         this.func_186094_a(true);
      } else {
         this.func_186101_k();
         Entity var3 = this.field_186110_d.func_175733_a(this.field_186119_m);
         if (var3 instanceof EntityDragon) {
            ((EntityDragon)var3).func_184672_a(var1, new BlockPos(var1), var2);
         }
      }

   }

   public boolean func_186102_d() {
      return this.field_186118_l;
   }

   public void func_186106_e() {
      if (this.field_186117_k && this.field_186122_p == null) {
         BlockPos var1 = this.field_186121_o;
         if (var1 == null) {
            field_186107_a.debug("Tried to respawn, but need to find the portal first.");
            BlockPattern.PatternHelper var2 = this.func_186091_h();
            if (var2 == null) {
               field_186107_a.debug("Couldn't find a portal, so we made one.");
               this.func_186094_a(true);
            } else {
               field_186107_a.debug("Found the exit portal & temporarily using it.");
            }

            var1 = this.field_186121_o;
         }

         ArrayList var7 = Lists.newArrayList();
         BlockPos var3 = var1.func_177981_b(1);
         Iterator var4 = EnumFacing.Plane.HORIZONTAL.iterator();

         while(var4.hasNext()) {
            EnumFacing var5 = (EnumFacing)var4.next();
            List var6 = this.field_186110_d.func_72872_a(EntityEnderCrystal.class, new AxisAlignedBB(var3.func_177967_a(var5, 2)));
            if (var6.isEmpty()) {
               return;
            }

            var7.addAll(var6);
         }

         field_186107_a.debug("Found all crystals, respawning dragon.");
         this.func_186093_a(var7);
      }

   }

   private void func_186093_a(List<EntityEnderCrystal> var1) {
      if (this.field_186117_k && this.field_186122_p == null) {
         for(BlockPattern.PatternHelper var2 = this.func_186091_h(); var2 != null; var2 = this.func_186091_h()) {
            for(int var3 = 0; var3 < this.field_186112_f.func_177684_c(); ++var3) {
               for(int var4 = 0; var4 < this.field_186112_f.func_177685_b(); ++var4) {
                  for(int var5 = 0; var5 < this.field_186112_f.func_185922_a(); ++var5) {
                     BlockWorldState var6 = var2.func_177670_a(var3, var4, var5);
                     if (var6.func_177509_a().func_177230_c() == Blocks.field_150357_h || var6.func_177509_a().func_177230_c() == Blocks.field_150384_bq) {
                        this.field_186110_d.func_175656_a(var6.func_177508_d(), Blocks.field_150377_bs.func_176223_P());
                     }
                  }
               }
            }
         }

         this.field_186122_p = DragonSpawnState.START;
         this.field_186123_q = 0;
         this.func_186094_a(false);
         this.field_186124_r = var1;
      }

   }

   public void func_186087_f() {
      EndCrystalTowerFeature.EndSpike[] var1 = EndSpikes.func_202466_a(this.field_186110_d);
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EndCrystalTowerFeature.EndSpike var4 = var1[var3];
         List var5 = this.field_186110_d.func_72872_a(EntityEnderCrystal.class, var4.func_186153_f());
         Iterator var6 = var5.iterator();

         while(var6.hasNext()) {
            EntityEnderCrystal var7 = (EntityEnderCrystal)var6.next();
            var7.func_184224_h(false);
            var7.func_184516_a((BlockPos)null);
         }
      }

   }

   static {
      field_186108_b = EntitySelectors.field_94557_a.and(EntitySelectors.func_188443_a(0.0D, 128.0D, 0.0D, 192.0D));
   }

   class LoadManager {
      private DragonFightManager.LoadState field_210826_b;

      private LoadManager() {
         super();
         this.field_210826_b = DragonFightManager.LoadState.UNKNOWN;
      }

      private boolean func_210824_a() {
         if (this.field_210826_b == DragonFightManager.LoadState.UNKNOWN) {
            this.field_210826_b = DragonFightManager.this.func_210832_a(-8, 8, -8, 8) ? DragonFightManager.LoadState.LOADED : DragonFightManager.LoadState.NOT_LOADED;
         }

         return this.field_210826_b == DragonFightManager.LoadState.LOADED;
      }

      // $FF: synthetic method
      LoadManager(Object var2) {
         this();
      }
   }

   static enum LoadState {
      UNKNOWN,
      NOT_LOADED,
      LOADED;

      private LoadState() {
      }
   }
}
