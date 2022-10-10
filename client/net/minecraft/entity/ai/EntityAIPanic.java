package net.minecraft.entity.ai;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;

public class EntityAIPanic extends EntityAIBase {
   protected final EntityCreature field_75267_a;
   protected double field_75265_b;
   protected double field_75266_c;
   protected double field_75263_d;
   protected double field_75264_e;

   public EntityAIPanic(EntityCreature var1, double var2) {
      super();
      this.field_75267_a = var1;
      this.field_75265_b = var2;
      this.func_75248_a(1);
   }

   public boolean func_75250_a() {
      if (this.field_75267_a.func_70643_av() == null && !this.field_75267_a.func_70027_ad()) {
         return false;
      } else {
         if (this.field_75267_a.func_70027_ad()) {
            BlockPos var1 = this.func_188497_a(this.field_75267_a.field_70170_p, this.field_75267_a, 5, 4);
            if (var1 != null) {
               this.field_75266_c = (double)var1.func_177958_n();
               this.field_75263_d = (double)var1.func_177956_o();
               this.field_75264_e = (double)var1.func_177952_p();
               return true;
            }
         }

         return this.func_190863_f();
      }
   }

   protected boolean func_190863_f() {
      Vec3d var1 = RandomPositionGenerator.func_75463_a(this.field_75267_a, 5, 4);
      if (var1 == null) {
         return false;
      } else {
         this.field_75266_c = var1.field_72450_a;
         this.field_75263_d = var1.field_72448_b;
         this.field_75264_e = var1.field_72449_c;
         return true;
      }
   }

   public void func_75249_e() {
      this.field_75267_a.func_70661_as().func_75492_a(this.field_75266_c, this.field_75263_d, this.field_75264_e, this.field_75265_b);
   }

   public boolean func_75253_b() {
      return !this.field_75267_a.func_70661_as().func_75500_f();
   }

   @Nullable
   protected BlockPos func_188497_a(IBlockReader var1, Entity var2, int var3, int var4) {
      BlockPos var5 = new BlockPos(var2);
      int var6 = var5.func_177958_n();
      int var7 = var5.func_177956_o();
      int var8 = var5.func_177952_p();
      float var9 = (float)(var3 * var3 * var4 * 2);
      BlockPos var10 = null;
      BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();

      for(int var12 = var6 - var3; var12 <= var6 + var3; ++var12) {
         for(int var13 = var7 - var4; var13 <= var7 + var4; ++var13) {
            for(int var14 = var8 - var3; var14 <= var8 + var3; ++var14) {
               var11.func_181079_c(var12, var13, var14);
               if (var1.func_204610_c(var11).func_206884_a(FluidTags.field_206959_a)) {
                  float var15 = (float)((var12 - var6) * (var12 - var6) + (var13 - var7) * (var13 - var7) + (var14 - var8) * (var14 - var8));
                  if (var15 < var9) {
                     var9 = var15;
                     var10 = new BlockPos(var11);
                  }
               }
            }
         }
      }

      return var10;
   }
}
