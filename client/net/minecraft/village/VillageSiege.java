package net.minecraft.village;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;

public class VillageSiege {
   private final World field_75537_a;
   private boolean field_75535_b;
   private int field_75536_c = -1;
   private int field_75533_d;
   private int field_75534_e;
   private Village field_75531_f;
   private int field_75532_g;
   private int field_75538_h;
   private int field_75539_i;

   public VillageSiege(World var1) {
      super();
      this.field_75537_a = var1;
   }

   public void func_75528_a() {
      if (this.field_75537_a.func_72935_r()) {
         this.field_75536_c = 0;
      } else if (this.field_75536_c != 2) {
         if (this.field_75536_c == 0) {
            float var1 = this.field_75537_a.func_72826_c(0.0F);
            if ((double)var1 < 0.5D || (double)var1 > 0.501D) {
               return;
            }

            this.field_75536_c = this.field_75537_a.field_73012_v.nextInt(10) == 0 ? 1 : 2;
            this.field_75535_b = false;
            if (this.field_75536_c == 2) {
               return;
            }
         }

         if (this.field_75536_c != -1) {
            if (!this.field_75535_b) {
               if (!this.func_75529_b()) {
                  return;
               }

               this.field_75535_b = true;
            }

            if (this.field_75534_e > 0) {
               --this.field_75534_e;
            } else {
               this.field_75534_e = 2;
               if (this.field_75533_d > 0) {
                  this.func_75530_c();
                  --this.field_75533_d;
               } else {
                  this.field_75536_c = 2;
               }

            }
         }
      }
   }

   private boolean func_75529_b() {
      List var1 = this.field_75537_a.field_73010_i;
      Iterator var2 = var1.iterator();

      Vec3d var11;
      do {
         do {
            do {
               do {
                  do {
                     EntityPlayer var3;
                     do {
                        if (!var2.hasNext()) {
                           return false;
                        }

                        var3 = (EntityPlayer)var2.next();
                     } while(var3.func_175149_v());

                     this.field_75531_f = this.field_75537_a.func_175714_ae().func_176056_a(new BlockPos(var3), 1);
                  } while(this.field_75531_f == null);
               } while(this.field_75531_f.func_75567_c() < 10);
            } while(this.field_75531_f.func_75561_d() < 20);
         } while(this.field_75531_f.func_75562_e() < 20);

         BlockPos var4 = this.field_75531_f.func_180608_a();
         float var5 = (float)this.field_75531_f.func_75568_b();
         boolean var6 = false;

         for(int var7 = 0; var7 < 10; ++var7) {
            float var8 = this.field_75537_a.field_73012_v.nextFloat() * 6.2831855F;
            this.field_75532_g = var4.func_177958_n() + (int)((double)(MathHelper.func_76134_b(var8) * var5) * 0.9D);
            this.field_75538_h = var4.func_177956_o();
            this.field_75539_i = var4.func_177952_p() + (int)((double)(MathHelper.func_76126_a(var8) * var5) * 0.9D);
            var6 = false;
            Iterator var9 = this.field_75537_a.func_175714_ae().func_75540_b().iterator();

            while(var9.hasNext()) {
               Village var10 = (Village)var9.next();
               if (var10 != this.field_75531_f && var10.func_179866_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i))) {
                  var6 = true;
                  break;
               }
            }

            if (!var6) {
               break;
            }
         }

         if (var6) {
            return false;
         }

         var11 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));
      } while(var11 == null);

      this.field_75534_e = 0;
      this.field_75533_d = 20;
      return true;
   }

   private boolean func_75530_c() {
      Vec3d var1 = this.func_179867_a(new BlockPos(this.field_75532_g, this.field_75538_h, this.field_75539_i));
      if (var1 == null) {
         return false;
      } else {
         EntityZombie var2;
         try {
            var2 = new EntityZombie(this.field_75537_a);
            var2.func_204210_a(this.field_75537_a.func_175649_E(new BlockPos(var2)), (IEntityLivingData)null, (NBTTagCompound)null);
         } catch (Exception var4) {
            var4.printStackTrace();
            return false;
         }

         var2.func_70012_b(var1.field_72450_a, var1.field_72448_b, var1.field_72449_c, this.field_75537_a.field_73012_v.nextFloat() * 360.0F, 0.0F);
         this.field_75537_a.func_72838_d(var2);
         BlockPos var3 = this.field_75531_f.func_180608_a();
         var2.func_175449_a(var3, this.field_75531_f.func_75568_b());
         return true;
      }
   }

   @Nullable
   private Vec3d func_179867_a(BlockPos var1) {
      for(int var2 = 0; var2 < 10; ++var2) {
         BlockPos var3 = var1.func_177982_a(this.field_75537_a.field_73012_v.nextInt(16) - 8, this.field_75537_a.field_73012_v.nextInt(6) - 3, this.field_75537_a.field_73012_v.nextInt(16) - 8);
         if (this.field_75531_f.func_179866_a(var3) && WorldEntitySpawner.func_209382_a(EntitySpawnPlacementRegistry.SpawnPlacementType.ON_GROUND, this.field_75537_a, var3, (EntityType)null)) {
            return new Vec3d((double)var3.func_177958_n(), (double)var3.func_177956_o(), (double)var3.func_177952_p());
         }
      }

      return null;
   }
}
