package net.minecraft.pathfinding;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate {
   protected EntityLiving field_75515_a;
   protected World field_75513_b;
   protected PathEntity field_75514_c;
   protected double field_75511_d;
   private final IAttributeInstance field_75512_e;
   private int field_75510_g;
   private int field_75520_h;
   private Vec3 field_75521_i = new Vec3(0.0D, 0.0D, 0.0D);
   private float field_179682_i = 1.0F;
   private final PathFinder field_179681_j;

   public PathNavigate(EntityLiving var1, World var2) {
      super();
      this.field_75515_a = var1;
      this.field_75513_b = var2;
      this.field_75512_e = var1.func_110148_a(SharedMonsterAttributes.field_111265_b);
      this.field_179681_j = this.func_179679_a();
   }

   protected abstract PathFinder func_179679_a();

   public void func_75489_a(double var1) {
      this.field_75511_d = var1;
   }

   public float func_111269_d() {
      return (float)this.field_75512_e.func_111126_e();
   }

   public final PathEntity func_75488_a(double var1, double var3, double var5) {
      return this.func_179680_a(new BlockPos(MathHelper.func_76128_c(var1), (int)var3, MathHelper.func_76128_c(var5)));
   }

   public PathEntity func_179680_a(BlockPos var1) {
      if (!this.func_75485_k()) {
         return null;
      } else {
         float var2 = this.func_111269_d();
         this.field_75513_b.field_72984_F.func_76320_a("pathfind");
         BlockPos var3 = new BlockPos(this.field_75515_a);
         int var4 = (int)(var2 + 8.0F);
         ChunkCache var5 = new ChunkCache(this.field_75513_b, var3.func_177982_a(-var4, -var4, -var4), var3.func_177982_a(var4, var4, var4), 0);
         PathEntity var6 = this.field_179681_j.func_180782_a(var5, this.field_75515_a, var1, var2);
         this.field_75513_b.field_72984_F.func_76319_b();
         return var6;
      }
   }

   public boolean func_75492_a(double var1, double var3, double var5, double var7) {
      PathEntity var9 = this.func_75488_a((double)MathHelper.func_76128_c(var1), (double)((int)var3), (double)MathHelper.func_76128_c(var5));
      return this.func_75484_a(var9, var7);
   }

   public void func_179678_a(float var1) {
      this.field_179682_i = var1;
   }

   public PathEntity func_75494_a(Entity var1) {
      if (!this.func_75485_k()) {
         return null;
      } else {
         float var2 = this.func_111269_d();
         this.field_75513_b.field_72984_F.func_76320_a("pathfind");
         BlockPos var3 = (new BlockPos(this.field_75515_a)).func_177984_a();
         int var4 = (int)(var2 + 16.0F);
         ChunkCache var5 = new ChunkCache(this.field_75513_b, var3.func_177982_a(-var4, -var4, -var4), var3.func_177982_a(var4, var4, var4), 0);
         PathEntity var6 = this.field_179681_j.func_176188_a(var5, this.field_75515_a, var1, var2);
         this.field_75513_b.field_72984_F.func_76319_b();
         return var6;
      }
   }

   public boolean func_75497_a(Entity var1, double var2) {
      PathEntity var4 = this.func_75494_a(var1);
      return var4 != null ? this.func_75484_a(var4, var2) : false;
   }

   public boolean func_75484_a(PathEntity var1, double var2) {
      if (var1 == null) {
         this.field_75514_c = null;
         return false;
      } else {
         if (!var1.func_75876_a(this.field_75514_c)) {
            this.field_75514_c = var1;
         }

         this.func_75487_m();
         if (this.field_75514_c.func_75874_d() == 0) {
            return false;
         } else {
            this.field_75511_d = var2;
            Vec3 var4 = this.func_75502_i();
            this.field_75520_h = this.field_75510_g;
            this.field_75521_i = var4;
            return true;
         }
      }
   }

   public PathEntity func_75505_d() {
      return this.field_75514_c;
   }

   public void func_75501_e() {
      ++this.field_75510_g;
      if (!this.func_75500_f()) {
         Vec3 var1;
         if (this.func_75485_k()) {
            this.func_75508_h();
         } else if (this.field_75514_c != null && this.field_75514_c.func_75873_e() < this.field_75514_c.func_75874_d()) {
            var1 = this.func_75502_i();
            Vec3 var2 = this.field_75514_c.func_75881_a(this.field_75515_a, this.field_75514_c.func_75873_e());
            if (var1.field_72448_b > var2.field_72448_b && !this.field_75515_a.field_70122_E && MathHelper.func_76128_c(var1.field_72450_a) == MathHelper.func_76128_c(var2.field_72450_a) && MathHelper.func_76128_c(var1.field_72449_c) == MathHelper.func_76128_c(var2.field_72449_c)) {
               this.field_75514_c.func_75872_c(this.field_75514_c.func_75873_e() + 1);
            }
         }

         if (!this.func_75500_f()) {
            var1 = this.field_75514_c.func_75878_a(this.field_75515_a);
            if (var1 != null) {
               AxisAlignedBB var8 = (new AxisAlignedBB(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c, var1.field_72450_a, var1.field_72448_b, var1.field_72449_c)).func_72314_b(0.5D, 0.5D, 0.5D);
               List var3 = this.field_75513_b.func_72945_a(this.field_75515_a, var8.func_72321_a(0.0D, -1.0D, 0.0D));
               double var4 = -1.0D;
               var8 = var8.func_72317_d(0.0D, 1.0D, 0.0D);

               AxisAlignedBB var7;
               for(Iterator var6 = var3.iterator(); var6.hasNext(); var4 = var7.func_72323_b(var8, var4)) {
                  var7 = (AxisAlignedBB)var6.next();
               }

               this.field_75515_a.func_70605_aq().func_75642_a(var1.field_72450_a, var1.field_72448_b + var4, var1.field_72449_c, this.field_75511_d);
            }
         }
      }
   }

   protected void func_75508_h() {
      Vec3 var1 = this.func_75502_i();
      int var2 = this.field_75514_c.func_75874_d();

      for(int var3 = this.field_75514_c.func_75873_e(); var3 < this.field_75514_c.func_75874_d(); ++var3) {
         if (this.field_75514_c.func_75877_a(var3).field_75837_b != (int)var1.field_72448_b) {
            var2 = var3;
            break;
         }
      }

      float var8 = this.field_75515_a.field_70130_N * this.field_75515_a.field_70130_N * this.field_179682_i;

      int var4;
      for(var4 = this.field_75514_c.func_75873_e(); var4 < var2; ++var4) {
         Vec3 var5 = this.field_75514_c.func_75881_a(this.field_75515_a, var4);
         if (var1.func_72436_e(var5) < (double)var8) {
            this.field_75514_c.func_75872_c(var4 + 1);
         }
      }

      var4 = MathHelper.func_76123_f(this.field_75515_a.field_70130_N);
      int var9 = (int)this.field_75515_a.field_70131_O + 1;
      int var6 = var4;

      for(int var7 = var2 - 1; var7 >= this.field_75514_c.func_75873_e(); --var7) {
         if (this.func_75493_a(var1, this.field_75514_c.func_75881_a(this.field_75515_a, var7), var4, var9, var6)) {
            this.field_75514_c.func_75872_c(var7);
            break;
         }
      }

      this.func_179677_a(var1);
   }

   protected void func_179677_a(Vec3 var1) {
      if (this.field_75510_g - this.field_75520_h > 100) {
         if (var1.func_72436_e(this.field_75521_i) < 2.25D) {
            this.func_75499_g();
         }

         this.field_75520_h = this.field_75510_g;
         this.field_75521_i = var1;
      }

   }

   public boolean func_75500_f() {
      return this.field_75514_c == null || this.field_75514_c.func_75879_b();
   }

   public void func_75499_g() {
      this.field_75514_c = null;
   }

   protected abstract Vec3 func_75502_i();

   protected abstract boolean func_75485_k();

   protected boolean func_75506_l() {
      return this.field_75515_a.func_70090_H() || this.field_75515_a.func_180799_ab();
   }

   protected void func_75487_m() {
   }

   protected abstract boolean func_75493_a(Vec3 var1, Vec3 var2, int var3, int var4, int var5);
}
