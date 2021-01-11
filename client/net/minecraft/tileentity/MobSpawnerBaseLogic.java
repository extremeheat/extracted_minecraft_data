package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;

public abstract class MobSpawnerBaseLogic {
   private int field_98286_b = 20;
   private String field_98288_a = "Pig";
   private final List<MobSpawnerBaseLogic.WeightedRandomMinecart> field_98285_e = Lists.newArrayList();
   private MobSpawnerBaseLogic.WeightedRandomMinecart field_98282_f;
   private double field_98287_c;
   private double field_98284_d;
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

   private String func_98276_e() {
      if (this.func_98269_i() == null) {
         if (this.field_98288_a != null && this.field_98288_a.equals("Minecart")) {
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

   private boolean func_98279_f() {
      BlockPos var1 = this.func_177221_b();
      return this.func_98271_a().func_175636_b((double)var1.func_177958_n() + 0.5D, (double)var1.func_177956_o() + 0.5D, (double)var1.func_177952_p() + 0.5D, (double)this.field_98289_l);
   }

   public void func_98278_g() {
      if (this.func_98279_f()) {
         BlockPos var1 = this.func_177221_b();
         double var6;
         if (this.func_98271_a().field_72995_K) {
            double var13 = (double)((float)var1.func_177958_n() + this.func_98271_a().field_73012_v.nextFloat());
            double var14 = (double)((float)var1.func_177956_o() + this.func_98271_a().field_73012_v.nextFloat());
            var6 = (double)((float)var1.func_177952_p() + this.func_98271_a().field_73012_v.nextFloat());
            this.func_98271_a().func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var13, var14, var6, 0.0D, 0.0D, 0.0D);
            this.func_98271_a().func_175688_a(EnumParticleTypes.FLAME, var13, var14, var6, 0.0D, 0.0D, 0.0D);
            if (this.field_98286_b > 0) {
               --this.field_98286_b;
            }

            this.field_98284_d = this.field_98287_c;
            this.field_98287_c = (this.field_98287_c + (double)(1000.0F / ((float)this.field_98286_b + 200.0F))) % 360.0D;
         } else {
            if (this.field_98286_b == -1) {
               this.func_98273_j();
            }

            if (this.field_98286_b > 0) {
               --this.field_98286_b;
               return;
            }

            boolean var2 = false;
            int var3 = 0;

            while(true) {
               if (var3 >= this.field_98294_i) {
                  if (var2) {
                     this.func_98273_j();
                  }
                  break;
               }

               Entity var4 = EntityList.func_75620_a(this.func_98276_e(), this.func_98271_a());
               if (var4 == null) {
                  return;
               }

               int var5 = this.func_98271_a().func_72872_a(var4.getClass(), (new AxisAlignedBB((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p(), (double)(var1.func_177958_n() + 1), (double)(var1.func_177956_o() + 1), (double)(var1.func_177952_p() + 1))).func_72314_b((double)this.field_98290_m, (double)this.field_98290_m, (double)this.field_98290_m)).size();
               if (var5 >= this.field_98292_k) {
                  this.func_98273_j();
                  return;
               }

               var6 = (double)var1.func_177958_n() + (this.func_98271_a().field_73012_v.nextDouble() - this.func_98271_a().field_73012_v.nextDouble()) * (double)this.field_98290_m + 0.5D;
               double var8 = (double)(var1.func_177956_o() + this.func_98271_a().field_73012_v.nextInt(3) - 1);
               double var10 = (double)var1.func_177952_p() + (this.func_98271_a().field_73012_v.nextDouble() - this.func_98271_a().field_73012_v.nextDouble()) * (double)this.field_98290_m + 0.5D;
               EntityLiving var12 = var4 instanceof EntityLiving ? (EntityLiving)var4 : null;
               var4.func_70012_b(var6, var8, var10, this.func_98271_a().field_73012_v.nextFloat() * 360.0F, 0.0F);
               if (var12 == null || var12.func_70601_bi() && var12.func_70058_J()) {
                  this.func_180613_a(var4, true);
                  this.func_98271_a().func_175718_b(2004, var1, 0);
                  if (var12 != null) {
                     var12.func_70656_aK();
                  }

                  var2 = true;
               }

               ++var3;
            }
         }

      }
   }

   private Entity func_180613_a(Entity var1, boolean var2) {
      if (this.func_98269_i() != null) {
         NBTTagCompound var3 = new NBTTagCompound();
         var1.func_70039_c(var3);
         Iterator var4 = this.func_98269_i().field_98222_b.func_150296_c().iterator();

         while(var4.hasNext()) {
            String var5 = (String)var4.next();
            NBTBase var6 = this.func_98269_i().field_98222_b.func_74781_a(var5);
            var3.func_74782_a(var5, var6.func_74737_b());
         }

         var1.func_70020_e(var3);
         if (var1.field_70170_p != null && var2) {
            var1.field_70170_p.func_72838_d(var1);
         }

         NBTTagCompound var12;
         for(Entity var11 = var1; var3.func_150297_b("Riding", 10); var3 = var12) {
            var12 = var3.func_74775_l("Riding");
            Entity var13 = EntityList.func_75620_a(var12.func_74779_i("id"), var1.field_70170_p);
            if (var13 != null) {
               NBTTagCompound var7 = new NBTTagCompound();
               var13.func_70039_c(var7);
               Iterator var8 = var12.func_150296_c().iterator();

               while(var8.hasNext()) {
                  String var9 = (String)var8.next();
                  NBTBase var10 = var12.func_74781_a(var9);
                  var7.func_74782_a(var9, var10.func_74737_b());
               }

               var13.func_70020_e(var7);
               var13.func_70012_b(var11.field_70165_t, var11.field_70163_u, var11.field_70161_v, var11.field_70177_z, var11.field_70125_A);
               if (var1.field_70170_p != null && var2) {
                  var1.field_70170_p.func_72838_d(var13);
               }

               var11.func_70078_a(var13);
            }

            var11 = var13;
         }
      } else if (var1 instanceof EntityLivingBase && var1.field_70170_p != null && var2) {
         if (var1 instanceof EntityLiving) {
            ((EntityLiving)var1).func_180482_a(var1.field_70170_p.func_175649_E(new BlockPos(var1)), (IEntityLivingData)null);
         }

         var1.field_70170_p.func_72838_d(var1);
      }

      return var1;
   }

   private void func_98273_j() {
      if (this.field_98293_h <= this.field_98283_g) {
         this.field_98286_b = this.field_98283_g;
      } else {
         int var10003 = this.field_98293_h - this.field_98283_g;
         this.field_98286_b = this.field_98283_g + this.func_98271_a().field_73012_v.nextInt(var10003);
      }

      if (this.field_98285_e.size() > 0) {
         this.func_98277_a((MobSpawnerBaseLogic.WeightedRandomMinecart)WeightedRandom.func_76271_a(this.func_98271_a().field_73012_v, this.field_98285_e));
      }

      this.func_98267_a(1);
   }

   public void func_98270_a(NBTTagCompound var1) {
      this.field_98288_a = var1.func_74779_i("EntityId");
      this.field_98286_b = var1.func_74765_d("Delay");
      this.field_98285_e.clear();
      if (var1.func_150297_b("SpawnPotentials", 9)) {
         NBTTagList var2 = var1.func_150295_c("SpawnPotentials", 10);

         for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
            this.field_98285_e.add(new MobSpawnerBaseLogic.WeightedRandomMinecart(var2.func_150305_b(var3)));
         }
      }

      if (var1.func_150297_b("SpawnData", 10)) {
         this.func_98277_a(new MobSpawnerBaseLogic.WeightedRandomMinecart(var1.func_74775_l("SpawnData"), this.field_98288_a));
      } else {
         this.func_98277_a((MobSpawnerBaseLogic.WeightedRandomMinecart)null);
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

      if (this.func_98271_a() != null) {
         this.field_98291_j = null;
      }

   }

   public void func_98280_b(NBTTagCompound var1) {
      String var2 = this.func_98276_e();
      if (!StringUtils.func_151246_b(var2)) {
         var1.func_74778_a("EntityId", var2);
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

         if (this.func_98269_i() != null || this.field_98285_e.size() > 0) {
            NBTTagList var3 = new NBTTagList();
            if (this.field_98285_e.size() > 0) {
               Iterator var4 = this.field_98285_e.iterator();

               while(var4.hasNext()) {
                  MobSpawnerBaseLogic.WeightedRandomMinecart var5 = (MobSpawnerBaseLogic.WeightedRandomMinecart)var4.next();
                  var3.func_74742_a(var5.func_98220_a());
               }
            } else {
               var3.func_74742_a(this.func_98269_i().func_98220_a());
            }

            var1.func_74782_a("SpawnPotentials", var3);
         }

      }
   }

   public Entity func_180612_a(World var1) {
      if (this.field_98291_j == null) {
         Entity var2 = EntityList.func_75620_a(this.func_98276_e(), var1);
         if (var2 != null) {
            var2 = this.func_180613_a(var2, false);
            this.field_98291_j = var2;
         }
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

   private MobSpawnerBaseLogic.WeightedRandomMinecart func_98269_i() {
      return this.field_98282_f;
   }

   public void func_98277_a(MobSpawnerBaseLogic.WeightedRandomMinecart var1) {
      this.field_98282_f = var1;
   }

   public abstract void func_98267_a(int var1);

   public abstract World func_98271_a();

   public abstract BlockPos func_177221_b();

   public double func_177222_d() {
      return this.field_98287_c;
   }

   public double func_177223_e() {
      return this.field_98284_d;
   }

   public class WeightedRandomMinecart extends WeightedRandom.Item {
      private final NBTTagCompound field_98222_b;
      private final String field_98223_c;

      public WeightedRandomMinecart(NBTTagCompound var2) {
         this(var2.func_74775_l("Properties"), var2.func_74779_i("Type"), var2.func_74762_e("Weight"));
      }

      public WeightedRandomMinecart(NBTTagCompound var2, String var3) {
         this(var2, var3, 1);
      }

      private WeightedRandomMinecart(NBTTagCompound var2, String var3, int var4) {
         super(var4);
         if (var3.equals("Minecart")) {
            if (var2 != null) {
               var3 = EntityMinecart.EnumMinecartType.func_180038_a(var2.func_74762_e("Type")).func_180040_b();
            } else {
               var3 = "MinecartRideable";
            }
         }

         this.field_98222_b = var2;
         this.field_98223_c = var3;
      }

      public NBTTagCompound func_98220_a() {
         NBTTagCompound var1 = new NBTTagCompound();
         var1.func_74782_a("Properties", this.field_98222_b);
         var1.func_74778_a("Type", this.field_98223_c);
         var1.func_74768_a("Weight", this.field_76292_a);
         return var1;
      }
   }
}
