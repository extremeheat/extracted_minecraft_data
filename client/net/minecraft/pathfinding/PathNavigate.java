package net.minecraft.pathfinding;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Region;
import net.minecraft.world.World;

public abstract class PathNavigate {
   protected EntityLiving field_75515_a;
   protected World field_75513_b;
   @Nullable
   protected Path field_75514_c;
   protected double field_75511_d;
   private final IAttributeInstance field_75512_e;
   protected int field_75510_g;
   protected int field_75520_h;
   protected Vec3d field_75521_i;
   protected Vec3d field_188557_k;
   protected long field_188558_l;
   protected long field_188559_m;
   protected double field_188560_n;
   protected float field_188561_o;
   protected boolean field_188562_p;
   protected long field_188563_q;
   protected NodeProcessor field_179695_a;
   private BlockPos field_188564_r;
   private PathFinder field_179681_j;

   public PathNavigate(EntityLiving var1, World var2) {
      super();
      this.field_75521_i = Vec3d.field_186680_a;
      this.field_188557_k = Vec3d.field_186680_a;
      this.field_188561_o = 0.5F;
      this.field_75515_a = var1;
      this.field_75513_b = var2;
      this.field_75512_e = var1.func_110148_a(SharedMonsterAttributes.field_111265_b);
      this.field_179681_j = this.func_179679_a();
   }

   public BlockPos func_208485_j() {
      return this.field_188564_r;
   }

   protected abstract PathFinder func_179679_a();

   public void func_75489_a(double var1) {
      this.field_75511_d = var1;
   }

   public float func_111269_d() {
      return (float)this.field_75512_e.func_111126_e();
   }

   public boolean func_188553_i() {
      return this.field_188562_p;
   }

   public void func_188554_j() {
      if (this.field_75513_b.func_82737_E() - this.field_188563_q > 20L) {
         if (this.field_188564_r != null) {
            this.field_75514_c = null;
            this.field_75514_c = this.func_179680_a(this.field_188564_r);
            this.field_188563_q = this.field_75513_b.func_82737_E();
            this.field_188562_p = false;
         }
      } else {
         this.field_188562_p = true;
      }

   }

   @Nullable
   public final Path func_75488_a(double var1, double var3, double var5) {
      return this.func_179680_a(new BlockPos(var1, var3, var5));
   }

   @Nullable
   public Path func_179680_a(BlockPos var1) {
      if (!this.func_75485_k()) {
         return null;
      } else if (this.field_75514_c != null && !this.field_75514_c.func_75879_b() && var1.equals(this.field_188564_r)) {
         return this.field_75514_c;
      } else {
         this.field_188564_r = var1;
         float var2 = this.func_111269_d();
         this.field_75513_b.field_72984_F.func_76320_a("pathfind");
         BlockPos var3 = new BlockPos(this.field_75515_a);
         int var4 = (int)(var2 + 8.0F);
         Region var5 = new Region(this.field_75513_b, var3.func_177982_a(-var4, -var4, -var4), var3.func_177982_a(var4, var4, var4), 0);
         Path var6 = this.field_179681_j.func_186336_a(var5, this.field_75515_a, this.field_188564_r, var2);
         this.field_75513_b.field_72984_F.func_76319_b();
         return var6;
      }
   }

   @Nullable
   public Path func_75494_a(Entity var1) {
      if (!this.func_75485_k()) {
         return null;
      } else {
         BlockPos var2 = new BlockPos(var1);
         if (this.field_75514_c != null && !this.field_75514_c.func_75879_b() && var2.equals(this.field_188564_r)) {
            return this.field_75514_c;
         } else {
            this.field_188564_r = var2;
            float var3 = this.func_111269_d();
            this.field_75513_b.field_72984_F.func_76320_a("pathfind");
            BlockPos var4 = (new BlockPos(this.field_75515_a)).func_177984_a();
            int var5 = (int)(var3 + 16.0F);
            Region var6 = new Region(this.field_75513_b, var4.func_177982_a(-var5, -var5, -var5), var4.func_177982_a(var5, var5, var5), 0);
            Path var7 = this.field_179681_j.func_186333_a(var6, this.field_75515_a, var1, var3);
            this.field_75513_b.field_72984_F.func_76319_b();
            return var7;
         }
      }
   }

   public boolean func_75492_a(double var1, double var3, double var5, double var7) {
      return this.func_75484_a(this.func_75488_a(var1, var3, var5), var7);
   }

   public boolean func_75497_a(Entity var1, double var2) {
      Path var4 = this.func_75494_a(var1);
      return var4 != null && this.func_75484_a(var4, var2);
   }

   public boolean func_75484_a(@Nullable Path var1, double var2) {
      if (var1 == null) {
         this.field_75514_c = null;
         return false;
      } else {
         if (!var1.func_75876_a(this.field_75514_c)) {
            this.field_75514_c = var1;
         }

         this.func_75487_m();
         if (this.field_75514_c.func_75874_d() <= 0) {
            return false;
         } else {
            this.field_75511_d = var2;
            Vec3d var4 = this.func_75502_i();
            this.field_75520_h = this.field_75510_g;
            this.field_75521_i = var4;
            return true;
         }
      }
   }

   @Nullable
   public Path func_75505_d() {
      return this.field_75514_c;
   }

   public void func_75501_e() {
      ++this.field_75510_g;
      if (this.field_188562_p) {
         this.func_188554_j();
      }

      if (!this.func_75500_f()) {
         Vec3d var1;
         if (this.func_75485_k()) {
            this.func_75508_h();
         } else if (this.field_75514_c != null && this.field_75514_c.func_75873_e() < this.field_75514_c.func_75874_d()) {
            var1 = this.func_75502_i();
            Vec3d var2 = this.field_75514_c.func_75881_a(this.field_75515_a, this.field_75514_c.func_75873_e());
            if (var1.field_72448_b > var2.field_72448_b && !this.field_75515_a.field_70122_E && MathHelper.func_76128_c(var1.field_72450_a) == MathHelper.func_76128_c(var2.field_72450_a) && MathHelper.func_76128_c(var1.field_72449_c) == MathHelper.func_76128_c(var2.field_72449_c)) {
               this.field_75514_c.func_75872_c(this.field_75514_c.func_75873_e() + 1);
            }
         }

         this.func_192876_m();
         if (!this.func_75500_f()) {
            var1 = this.field_75514_c.func_75878_a(this.field_75515_a);
            BlockPos var3 = new BlockPos(var1);
            this.field_75515_a.func_70605_aq().func_75642_a(var1.field_72450_a, this.field_75513_b.func_180495_p(var3.func_177977_b()).func_196958_f() ? var1.field_72448_b : WalkNodeProcessor.func_197682_a(this.field_75513_b, var3), var1.field_72449_c, this.field_75511_d);
         }
      }
   }

   protected void func_192876_m() {
   }

   protected void func_75508_h() {
      Vec3d var1 = this.func_75502_i();
      int var2 = this.field_75514_c.func_75874_d();

      for(int var3 = this.field_75514_c.func_75873_e(); var3 < this.field_75514_c.func_75874_d(); ++var3) {
         if ((double)this.field_75514_c.func_75877_a(var3).field_75837_b != Math.floor(var1.field_72448_b)) {
            var2 = var3;
            break;
         }
      }

      this.field_188561_o = this.field_75515_a.field_70130_N > 0.75F ? this.field_75515_a.field_70130_N / 2.0F : 0.75F - this.field_75515_a.field_70130_N / 2.0F;
      Vec3d var8 = this.field_75514_c.func_186310_f();
      if (MathHelper.func_76135_e((float)(this.field_75515_a.field_70165_t - (var8.field_72450_a + 0.5D))) < this.field_188561_o && MathHelper.func_76135_e((float)(this.field_75515_a.field_70161_v - (var8.field_72449_c + 0.5D))) < this.field_188561_o && Math.abs(this.field_75515_a.field_70163_u - var8.field_72448_b) < 1.0D) {
         this.field_75514_c.func_75872_c(this.field_75514_c.func_75873_e() + 1);
      }

      int var4 = MathHelper.func_76123_f(this.field_75515_a.field_70130_N);
      int var5 = MathHelper.func_76123_f(this.field_75515_a.field_70131_O);
      int var6 = var4;

      for(int var7 = var2 - 1; var7 >= this.field_75514_c.func_75873_e(); --var7) {
         if (this.func_75493_a(var1, this.field_75514_c.func_75881_a(this.field_75515_a, var7), var4, var5, var6)) {
            this.field_75514_c.func_75872_c(var7);
            break;
         }
      }

      this.func_179677_a(var1);
   }

   protected void func_179677_a(Vec3d var1) {
      if (this.field_75510_g - this.field_75520_h > 100) {
         if (var1.func_72436_e(this.field_75521_i) < 2.25D) {
            this.func_75499_g();
         }

         this.field_75520_h = this.field_75510_g;
         this.field_75521_i = var1;
      }

      if (this.field_75514_c != null && !this.field_75514_c.func_75879_b()) {
         Vec3d var2 = this.field_75514_c.func_186310_f();
         if (var2.equals(this.field_188557_k)) {
            this.field_188558_l += Util.func_211177_b() - this.field_188559_m;
         } else {
            this.field_188557_k = var2;
            double var3 = var1.func_72438_d(this.field_188557_k);
            this.field_188560_n = this.field_75515_a.func_70689_ay() > 0.0F ? var3 / (double)this.field_75515_a.func_70689_ay() * 1000.0D : 0.0D;
         }

         if (this.field_188560_n > 0.0D && (double)this.field_188558_l > this.field_188560_n * 3.0D) {
            this.field_188557_k = Vec3d.field_186680_a;
            this.field_188558_l = 0L;
            this.field_188560_n = 0.0D;
            this.func_75499_g();
         }

         this.field_188559_m = Util.func_211177_b();
      }

   }

   public boolean func_75500_f() {
      return this.field_75514_c == null || this.field_75514_c.func_75879_b();
   }

   public void func_75499_g() {
      this.field_75514_c = null;
   }

   protected abstract Vec3d func_75502_i();

   protected abstract boolean func_75485_k();

   protected boolean func_75506_l() {
      return this.field_75515_a.func_203005_aq() || this.field_75515_a.func_180799_ab();
   }

   protected void func_75487_m() {
      if (this.field_75514_c != null) {
         for(int var1 = 0; var1 < this.field_75514_c.func_75874_d(); ++var1) {
            PathPoint var2 = this.field_75514_c.func_75877_a(var1);
            PathPoint var3 = var1 + 1 < this.field_75514_c.func_75874_d() ? this.field_75514_c.func_75877_a(var1 + 1) : null;
            IBlockState var4 = this.field_75513_b.func_180495_p(new BlockPos(var2.field_75839_a, var2.field_75837_b, var2.field_75838_c));
            Block var5 = var4.func_177230_c();
            if (var5 == Blocks.field_150383_bp) {
               this.field_75514_c.func_186309_a(var1, var2.func_186283_a(var2.field_75839_a, var2.field_75837_b + 1, var2.field_75838_c));
               if (var3 != null && var2.field_75837_b >= var3.field_75837_b) {
                  this.field_75514_c.func_186309_a(var1 + 1, var3.func_186283_a(var3.field_75839_a, var2.field_75837_b + 1, var3.field_75838_c));
               }
            }
         }

      }
   }

   protected abstract boolean func_75493_a(Vec3d var1, Vec3d var2, int var3, int var4, int var5);

   public boolean func_188555_b(BlockPos var1) {
      BlockPos var2 = var1.func_177977_b();
      return this.field_75513_b.func_180495_p(var2).func_200015_d(this.field_75513_b, var2);
   }

   public NodeProcessor func_189566_q() {
      return this.field_179695_a;
   }

   public void func_212239_d(boolean var1) {
      this.field_179695_a.func_186316_c(var1);
   }

   public boolean func_212238_t() {
      return this.field_179695_a.func_186322_e();
   }
}
