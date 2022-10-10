package net.minecraft.pathfinding;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldEventListener;

public class PathWorldListener implements IWorldEventListener {
   private final List<PathNavigate> field_189519_a = Lists.newArrayList();

   public PathWorldListener() {
      super();
   }

   public void func_184376_a(IBlockReader var1, BlockPos var2, IBlockState var3, IBlockState var4, int var5) {
      if (this.func_184378_a(var1, var2, var3, var4)) {
         int var6 = 0;

         for(int var7 = this.field_189519_a.size(); var6 < var7; ++var6) {
            PathNavigate var8 = (PathNavigate)this.field_189519_a.get(var6);
            if (var8 != null && !var8.func_188553_i()) {
               Path var9 = var8.func_75505_d();
               if (var9 != null && !var9.func_75879_b() && var9.func_75874_d() != 0) {
                  PathPoint var10 = var8.field_75514_c.func_75870_c();
                  double var11 = var2.func_177954_c(((double)var10.field_75839_a + var8.field_75515_a.field_70165_t) / 2.0D, ((double)var10.field_75837_b + var8.field_75515_a.field_70163_u) / 2.0D, ((double)var10.field_75838_c + var8.field_75515_a.field_70161_v) / 2.0D);
                  int var13 = (var9.func_75874_d() - var9.func_75873_e()) * (var9.func_75874_d() - var9.func_75873_e());
                  if (var11 < (double)var13) {
                     var8.func_188554_j();
                  }
               }
            }
         }

      }
   }

   protected boolean func_184378_a(IBlockReader var1, BlockPos var2, IBlockState var3, IBlockState var4) {
      VoxelShape var5 = var3.func_196952_d(var1, var2);
      VoxelShape var6 = var4.func_196952_d(var1, var2);
      return VoxelShapes.func_197879_c(var5, var6, IBooleanFunction.NOT_SAME);
   }

   public void func_174959_b(BlockPos var1) {
   }

   public void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6) {
   }

   public void func_184375_a(@Nullable EntityPlayer var1, SoundEvent var2, SoundCategory var3, double var4, double var6, double var8, float var10, float var11) {
   }

   public void func_195461_a(IParticleData var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
   }

   public void func_195462_a(IParticleData var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
   }

   public void func_72703_a(Entity var1) {
      if (var1 instanceof EntityLiving) {
         this.field_189519_a.add(((EntityLiving)var1).func_70661_as());
      }

   }

   public void func_72709_b(Entity var1) {
      if (var1 instanceof EntityLiving) {
         this.field_189519_a.remove(((EntityLiving)var1).func_70661_as());
      }

   }

   public void func_184377_a(SoundEvent var1, BlockPos var2) {
   }

   public void func_180440_a(int var1, BlockPos var2, int var3) {
   }

   public void func_180439_a(EntityPlayer var1, int var2, BlockPos var3, int var4) {
   }

   public void func_180441_b(int var1, BlockPos var2, int var3) {
   }
}
