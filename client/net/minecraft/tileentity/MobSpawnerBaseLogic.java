package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Particles;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.StringUtils;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerBaseLogic {
   private static final Logger field_209160_a = LogManager.getLogger();
   private int field_98286_b = 20;
   private final List<WeightedSpawnerEntity> field_98285_e = Lists.newArrayList();
   private WeightedSpawnerEntity field_98282_f = new WeightedSpawnerEntity();
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

   @Nullable
   private ResourceLocation func_190895_g() {
      String var1 = this.field_98282_f.func_185277_b().func_74779_i("id");

      try {
         return StringUtils.func_151246_b(var1) ? null : new ResourceLocation(var1);
      } catch (ResourceLocationException var4) {
         BlockPos var3 = this.func_177221_b();
         field_209160_a.warn("Invalid entity id '{}' at spawner {}:[{},{},{}]", var1, this.func_98271_a().field_73011_w.func_186058_p(), var3.func_177958_n(), var3.func_177956_o(), var3.func_177952_p());
         return null;
      }
   }

   public void func_200876_a(EntityType<?> var1) {
      this.field_98282_f.func_185277_b().func_74778_a("id", IRegistry.field_212629_r.func_177774_c(var1).toString());
   }

   private boolean func_98279_f() {
      BlockPos var1 = this.func_177221_b();
      return this.func_98271_a().func_212417_b((double)var1.func_177958_n() + 0.5D, (double)var1.func_177956_o() + 0.5D, (double)var1.func_177952_p() + 0.5D, (double)this.field_98289_l);
   }

   public void func_98278_g() {
      if (!this.func_98279_f()) {
         this.field_98284_d = this.field_98287_c;
      } else {
         BlockPos var1 = this.func_177221_b();
         if (this.func_98271_a().field_72995_K) {
            double var17 = (double)((float)var1.func_177958_n() + this.func_98271_a().field_73012_v.nextFloat());
            double var18 = (double)((float)var1.func_177956_o() + this.func_98271_a().field_73012_v.nextFloat());
            double var19 = (double)((float)var1.func_177952_p() + this.func_98271_a().field_73012_v.nextFloat());
            this.func_98271_a().func_195594_a(Particles.field_197601_L, var17, var18, var19, 0.0D, 0.0D, 0.0D);
            this.func_98271_a().func_195594_a(Particles.field_197631_x, var17, var18, var19, 0.0D, 0.0D, 0.0D);
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

               NBTTagCompound var4 = this.field_98282_f.func_185277_b();
               NBTTagList var5 = var4.func_150295_c("Pos", 6);
               World var6 = this.func_98271_a();
               int var7 = var5.size();
               double var8 = var7 >= 1 ? var5.func_150309_d(0) : (double)var1.func_177958_n() + (var6.field_73012_v.nextDouble() - var6.field_73012_v.nextDouble()) * (double)this.field_98290_m + 0.5D;
               double var10 = var7 >= 2 ? var5.func_150309_d(1) : (double)(var1.func_177956_o() + var6.field_73012_v.nextInt(3) - 1);
               double var12 = var7 >= 3 ? var5.func_150309_d(2) : (double)var1.func_177952_p() + (var6.field_73012_v.nextDouble() - var6.field_73012_v.nextDouble()) * (double)this.field_98290_m + 0.5D;
               Entity var14 = AnvilChunkLoader.func_186054_a(var4, var6, var8, var10, var12, false);
               if (var14 == null) {
                  this.func_98273_j();
                  return;
               }

               int var15 = var6.func_72872_a(var14.getClass(), (new AxisAlignedBB((double)var1.func_177958_n(), (double)var1.func_177956_o(), (double)var1.func_177952_p(), (double)(var1.func_177958_n() + 1), (double)(var1.func_177956_o() + 1), (double)(var1.func_177952_p() + 1))).func_186662_g((double)this.field_98290_m)).size();
               if (var15 >= this.field_98292_k) {
                  this.func_98273_j();
                  return;
               }

               EntityLiving var16 = var14 instanceof EntityLiving ? (EntityLiving)var14 : null;
               var14.func_70012_b(var14.field_70165_t, var14.field_70163_u, var14.field_70161_v, var6.field_73012_v.nextFloat() * 360.0F, 0.0F);
               if (var16 == null || var16.func_205020_a(var6, true) && var16.func_70058_J()) {
                  if (this.field_98282_f.func_185277_b().func_186856_d() == 1 && this.field_98282_f.func_185277_b().func_150297_b("id", 8) && var14 instanceof EntityLiving) {
                     ((EntityLiving)var14).func_204210_a(var6.func_175649_E(new BlockPos(var14)), (IEntityLivingData)null, (NBTTagCompound)null);
                  }

                  AnvilChunkLoader.func_186052_a(var14, var6);
                  var6.func_175718_b(2004, var1, 0);
                  if (var16 != null) {
                     var16.func_70656_aK();
                  }

                  var2 = true;
               }

               ++var3;
            }
         }

      }
   }

   private void func_98273_j() {
      if (this.field_98293_h <= this.field_98283_g) {
         this.field_98286_b = this.field_98283_g;
      } else {
         int var10003 = this.field_98293_h - this.field_98283_g;
         this.field_98286_b = this.field_98283_g + this.func_98271_a().field_73012_v.nextInt(var10003);
      }

      if (!this.field_98285_e.isEmpty()) {
         this.func_184993_a((WeightedSpawnerEntity)WeightedRandom.func_76271_a(this.func_98271_a().field_73012_v, this.field_98285_e));
      }

      this.func_98267_a(1);
   }

   public void func_98270_a(NBTTagCompound var1) {
      this.field_98286_b = var1.func_74765_d("Delay");
      this.field_98285_e.clear();
      if (var1.func_150297_b("SpawnPotentials", 9)) {
         NBTTagList var2 = var1.func_150295_c("SpawnPotentials", 10);

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            this.field_98285_e.add(new WeightedSpawnerEntity(var2.func_150305_b(var3)));
         }
      }

      if (var1.func_150297_b("SpawnData", 10)) {
         this.func_184993_a(new WeightedSpawnerEntity(1, var1.func_74775_l("SpawnData")));
      } else if (!this.field_98285_e.isEmpty()) {
         this.func_184993_a((WeightedSpawnerEntity)WeightedRandom.func_76271_a(this.func_98271_a().field_73012_v, this.field_98285_e));
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

   public NBTTagCompound func_189530_b(NBTTagCompound var1) {
      ResourceLocation var2 = this.func_190895_g();
      if (var2 == null) {
         return var1;
      } else {
         var1.func_74777_a("Delay", (short)this.field_98286_b);
         var1.func_74777_a("MinSpawnDelay", (short)this.field_98283_g);
         var1.func_74777_a("MaxSpawnDelay", (short)this.field_98293_h);
         var1.func_74777_a("SpawnCount", (short)this.field_98294_i);
         var1.func_74777_a("MaxNearbyEntities", (short)this.field_98292_k);
         var1.func_74777_a("RequiredPlayerRange", (short)this.field_98289_l);
         var1.func_74777_a("SpawnRange", (short)this.field_98290_m);
         var1.func_74782_a("SpawnData", this.field_98282_f.func_185277_b().func_74737_b());
         NBTTagList var3 = new NBTTagList();
         if (this.field_98285_e.isEmpty()) {
            var3.add((INBTBase)this.field_98282_f.func_185278_a());
         } else {
            Iterator var4 = this.field_98285_e.iterator();

            while(var4.hasNext()) {
               WeightedSpawnerEntity var5 = (WeightedSpawnerEntity)var4.next();
               var3.add((INBTBase)var5.func_185278_a());
            }
         }

         var1.func_74782_a("SpawnPotentials", var3);
         return var1;
      }
   }

   public Entity func_184994_d() {
      if (this.field_98291_j == null) {
         this.field_98291_j = AnvilChunkLoader.func_186051_a(this.field_98282_f.func_185277_b(), this.func_98271_a(), false);
         if (this.field_98282_f.func_185277_b().func_186856_d() == 1 && this.field_98282_f.func_185277_b().func_150297_b("id", 8) && this.field_98291_j instanceof EntityLiving) {
            ((EntityLiving)this.field_98291_j).func_204210_a(this.func_98271_a().func_175649_E(new BlockPos(this.field_98291_j)), (IEntityLivingData)null, (NBTTagCompound)null);
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

   public void func_184993_a(WeightedSpawnerEntity var1) {
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
}
