package net.minecraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic {
   public int field_98286_b = 20;
   private String field_98288_a = "Pig";
   private List field_98285_e;
   private MobSpawnerBaseLogic$WeightedRandomMinecart field_98282_f;
   public double field_98287_c;
   public double field_98284_d;
   private int field_98283_g = 200;
   private int field_98293_h = 800;
   private int field_98294_i = 4;
   private Entity field_98291_j;
   private int field_98292_k = 6;
   private int field_98289_l = 16;
   private int field_98290_m = 4;

   public MobSpawnerBaseLogic() {
      super();
   }

   public String func_98276_e() {
      if (this.func_98269_i() == null) {
         if (this.field_98288_a.equals("Minecart")) {
            this.field_98288_a = "MinecartRideable";
         }

         return this.field_98288_a;
      } else {
         return this.func_98269_i().field_98223_c;
      }
   }

   public void func_98272_a(String var1) {
      this.field_98288_a = var1;
   }

   public boolean func_98279_f() {
      return this.func_98271_a()
            .func_72977_a((double)this.func_98275_b() + 0.5, (double)this.func_98274_c() + 0.5, (double)this.func_98266_d() + 0.5, (double)this.field_98289_l)
         != null;
   }

   public void func_98278_g() {
      if (this.func_98279_f()) {
         if (this.func_98271_a().field_72995_K) {
            double var1 = (double)((float)this.func_98275_b() + this.func_98271_a().field_73012_v.nextFloat());
            double var3 = (double)((float)this.func_98274_c() + this.func_98271_a().field_73012_v.nextFloat());
            double var5 = (double)((float)this.func_98266_d() + this.func_98271_a().field_73012_v.nextFloat());
            this.func_98271_a().func_72869_a("smoke", var1, var3, var5, 0.0, 0.0, 0.0);
            this.func_98271_a().func_72869_a("flame", var1, var3, var5, 0.0, 0.0, 0.0);
            if (this.field_98286_b > 0) {
               --this.field_98286_b;
            }

            this.field_98284_d = this.field_98287_c;
            this.field_98287_c = (this.field_98287_c + (double)(1000.0F / ((float)this.field_98286_b + 200.0F))) % 360.0;
         } else {
            if (this.field_98286_b == -1) {
               this.func_98273_j();
            }

            if (this.field_98286_b > 0) {
               --this.field_98286_b;
               return;
            }

            boolean var12 = false;

            for(int var2 = 0; var2 < this.field_98294_i; ++var2) {
               Entity var13 = EntityList.func_75620_a(this.func_98276_e(), this.func_98271_a());
               if (var13 == null) {
                  return;
               }

               int var4 = this.func_98271_a()
                  .func_72872_a(
                     var13.getClass(),
                     AxisAlignedBB.func_72330_a(
                           (double)this.func_98275_b(),
                           (double)this.func_98274_c(),
                           (double)this.func_98266_d(),
                           (double)(this.func_98275_b() + 1),
                           (double)(this.func_98274_c() + 1),
                           (double)(this.func_98266_d() + 1)
                        )
                        .func_72314_b((double)(this.field_98290_m * 2), 4.0, (double)(this.field_98290_m * 2))
                  )
                  .size();
               if (var4 >= this.field_98292_k) {
                  this.func_98273_j();
                  return;
               }

               double var14 = (double)this.func_98275_b()
                  + (this.func_98271_a().field_73012_v.nextDouble() - this.func_98271_a().field_73012_v.nextDouble()) * (double)this.field_98290_m;
               double var7 = (double)(this.func_98274_c() + this.func_98271_a().field_73012_v.nextInt(3) - 1);
               double var9 = (double)this.func_98266_d()
                  + (this.func_98271_a().field_73012_v.nextDouble() - this.func_98271_a().field_73012_v.nextDouble()) * (double)this.field_98290_m;
               EntityLiving var11 = var13 instanceof EntityLiving ? (EntityLiving)var13 : null;
               var13.func_70012_b(var14, var7, var9, this.func_98271_a().field_73012_v.nextFloat() * 360.0F, 0.0F);
               if (var11 == null || var11.func_70601_bi()) {
                  this.func_98265_a(var13);
                  this.func_98271_a().func_72926_e(2004, this.func_98275_b(), this.func_98274_c(), this.func_98266_d(), 0);
                  if (var11 != null) {
                     var11.func_70656_aK();
                  }

                  var12 = true;
               }
            }

            if (var12) {
               this.func_98273_j();
            }
         }
      }
   }

   public Entity func_98265_a(Entity var1) {
      if (this.func_98269_i() != null) {
         NBTTagCompound var2 = new NBTTagCompound();
         var1.func_70039_c(var2);

         for(String var4 : this.func_98269_i().field_98222_b.func_150296_c()) {
            NBTBase var5 = this.func_98269_i().field_98222_b.func_74781_a(var4);
            var2.func_74782_a(var4, var5.func_74737_b());
         }

         var1.func_70020_e(var2);
         if (var1.field_70170_p != null) {
            var1.field_70170_p.func_72838_d(var1);
         }

         NBTTagCompound var11;
         for(Entity var10 = var1; var2.func_150297_b("Riding", 10); var2 = var11) {
            var11 = var2.func_74775_l("Riding");
            Entity var12 = EntityList.func_75620_a(var11.func_74779_i("id"), var1.field_70170_p);
            if (var12 != null) {
               NBTTagCompound var6 = new NBTTagCompound();
               var12.func_70039_c(var6);

               for(String var8 : var11.func_150296_c()) {
                  NBTBase var9 = var11.func_74781_a(var8);
                  var6.func_74782_a(var8, var9.func_74737_b());
               }

               var12.func_70020_e(var6);
               var12.func_70012_b(var10.field_70165_t, var10.field_70163_u, var10.field_70161_v, var10.field_70177_z, var10.field_70125_A);
               if (var1.field_70170_p != null) {
                  var1.field_70170_p.func_72838_d(var12);
               }

               var10.func_70078_a(var12);
            }

            var10 = var12;
         }
      } else if (var1 instanceof EntityLivingBase && var1.field_70170_p != null) {
         ((EntityLiving)var1).func_110161_a(null);
         this.func_98271_a().func_72838_d(var1);
      }

      return var1;
   }

   private void func_98273_j() {
      if (this.field_98293_h <= this.field_98283_g) {
         this.field_98286_b = this.field_98283_g;
      } else {
         this.field_98286_b = this.field_98283_g + this.func_98271_a().field_73012_v.nextInt(this.field_98293_h - this.field_98283_g);
      }

      if (this.field_98285_e != null && this.field_98285_e.size() > 0) {
         this.func_98277_a((MobSpawnerBaseLogic$WeightedRandomMinecart)WeightedRandom.func_76271_a(this.func_98271_a().field_73012_v, this.field_98285_e));
      }

      this.func_98267_a(1);
   }

   public void func_98270_a(NBTTagCompound var1) {
      this.field_98288_a = var1.func_74779_i("EntityId");
      this.field_98286_b = var1.func_74765_d("Delay");
      if (var1.func_150297_b("SpawnPotentials", 9)) {
         this.field_98285_e = new ArrayList();
         NBTTagList var2 = var1.func_150295_c("SpawnPotentials", 10);

         for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            this.field_98285_e.add(new MobSpawnerBaseLogic$WeightedRandomMinecart(this, var2.func_150305_b(var3)));
         }
      } else {
         this.field_98285_e = null;
      }

      if (var1.func_150297_b("SpawnData", 10)) {
         this.func_98277_a(new MobSpawnerBaseLogic$WeightedRandomMinecart(this, var1.func_74775_l("SpawnData"), this.field_98288_a));
      } else {
         this.func_98277_a(null);
      }

      if (var1.func_150297_b("MinSpawnDelay", 99)) {
         this.field_98283_g = var1.func_74765_d("MinSpawnDelay");
         this.field_98293_h = var1.func_74765_d("MaxSpawnDelay");
         this.field_98294_i = var1.func_74765_d("SpawnCount");
      }

      if (var1.func_150297_b("MaxNearbyEntities", 99)) {
         this.field_98292_k = var1.func_74765_d("MaxNearbyEntities");
         this.field_98289_l = var1.func_74765_d("RequiredPlayerRange");
      }

      if (var1.func_150297_b("SpawnRange", 99)) {
         this.field_98290_m = var1.func_74765_d("SpawnRange");
      }

      if (this.func_98271_a() != null && this.func_98271_a().field_72995_K) {
         this.field_98291_j = null;
      }
   }

   public void func_98280_b(NBTTagCompound var1) {
      var1.func_74778_a("EntityId", this.func_98276_e());
      var1.func_74777_a("Delay", (short)this.field_98286_b);
      var1.func_74777_a("MinSpawnDelay", (short)this.field_98283_g);
      var1.func_74777_a("MaxSpawnDelay", (short)this.field_98293_h);
      var1.func_74777_a("SpawnCount", (short)this.field_98294_i);
      var1.func_74777_a("MaxNearbyEntities", (short)this.field_98292_k);
      var1.func_74777_a("RequiredPlayerRange", (short)this.field_98289_l);
      var1.func_74777_a("SpawnRange", (short)this.field_98290_m);
      if (this.func_98269_i() != null) {
         var1.func_74782_a("SpawnData", this.func_98269_i().field_98222_b.func_74737_b());
      }

      if (this.func_98269_i() != null || this.field_98285_e != null && this.field_98285_e.size() > 0) {
         NBTTagList var2 = new NBTTagList();
         if (this.field_98285_e != null && this.field_98285_e.size() > 0) {
            for(MobSpawnerBaseLogic$WeightedRandomMinecart var4 : this.field_98285_e) {
               var2.func_74742_a(var4.func_98220_a());
            }
         } else {
            var2.func_74742_a(this.func_98269_i().func_98220_a());
         }

         var1.func_74782_a("SpawnPotentials", var2);
      }
   }

   public Entity func_98281_h() {
      if (this.field_98291_j == null) {
         Entity var1 = EntityList.func_75620_a(this.func_98276_e(), null);
         var1 = this.func_98265_a(var1);
         this.field_98291_j = var1;
      }

      return this.field_98291_j;
   }

   public boolean func_98268_b(int var1) {
      if (var1 == 1 && this.func_98271_a().field_72995_K) {
         this.field_98286_b = this.field_98283_g;
         return true;
      } else {
         return false;
      }
   }

   public MobSpawnerBaseLogic$WeightedRandomMinecart func_98269_i() {
      return this.field_98282_f;
   }

   public void func_98277_a(MobSpawnerBaseLogic$WeightedRandomMinecart var1) {
      this.field_98282_f = var1;
   }

   public abstract void func_98267_a(int var1);

   public abstract World func_98271_a();

   public abstract int func_98275_b();

   public abstract int func_98274_c();

   public abstract int func_98266_d();
}
