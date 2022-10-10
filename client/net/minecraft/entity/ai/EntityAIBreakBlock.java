package net.minecraft.entity.ai;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityAIBreakBlock extends EntityAIMoveToBlock {
   private final Block field_203117_f;
   private final EntityLiving field_203118_g;
   private int field_203119_h;

   public EntityAIBreakBlock(Block var1, EntityCreature var2, double var3, int var5) {
      super(var2, var3, 24, var5);
      this.field_203117_f = var1;
      this.field_203118_g = var2;
   }

   public boolean func_75250_a() {
      if (!this.field_203118_g.field_70170_p.func_82736_K().func_82766_b("mobGriefing")) {
         return false;
      } else {
         return this.field_203118_g.func_70681_au().nextInt(20) != 0 ? false : super.func_75250_a();
      }
   }

   protected int func_203109_a(EntityCreature var1) {
      return 0;
   }

   public boolean func_75253_b() {
      return super.func_75253_b();
   }

   public void func_75251_c() {
      super.func_75251_c();
      this.field_203118_g.field_70143_R = 1.0F;
   }

   public void func_75249_e() {
      super.func_75249_e();
      this.field_203119_h = 0;
   }

   public void func_203114_b(IWorld var1, BlockPos var2) {
   }

   public void func_203116_c(World var1, BlockPos var2) {
   }

   public void func_75246_d() {
      super.func_75246_d();
      World var1 = this.field_203118_g.field_70170_p;
      BlockPos var2 = new BlockPos(this.field_203118_g);
      BlockPos var3 = this.func_203115_a(var2, var1);
      Random var4 = this.field_203118_g.func_70681_au();
      if (this.func_179487_f() && var3 != null) {
         if (this.field_203119_h > 0) {
            this.field_203118_g.field_70181_x = 0.3D;
            if (!var1.field_72995_K) {
               double var5 = 0.08D;
               ((WorldServer)var1).func_195598_a(new ItemParticleData(Particles.field_197591_B, new ItemStack(Items.field_151110_aK)), (double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 0.7D, (double)var3.func_177952_p() + 0.5D, 3, ((double)var4.nextFloat() - 0.5D) * 0.08D, ((double)var4.nextFloat() - 0.5D) * 0.08D, ((double)var4.nextFloat() - 0.5D) * 0.08D, 0.15000000596046448D);
            }
         }

         if (this.field_203119_h % 2 == 0) {
            this.field_203118_g.field_70181_x = -0.3D;
            if (this.field_203119_h % 6 == 0) {
               this.func_203114_b(var1, this.field_179494_b);
            }
         }

         if (this.field_203119_h > 60) {
            var1.func_175698_g(var3);
            if (!var1.field_72995_K) {
               for(int var12 = 0; var12 < 20; ++var12) {
                  double var6 = var4.nextGaussian() * 0.02D;
                  double var8 = var4.nextGaussian() * 0.02D;
                  double var10 = var4.nextGaussian() * 0.02D;
                  ((WorldServer)var1).func_195598_a(Particles.field_197598_I, (double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o(), (double)var3.func_177952_p() + 0.5D, 1, var6, var8, var10, 0.15000000596046448D);
               }

               this.func_203116_c(var1, this.field_179494_b);
            }
         }

         ++this.field_203119_h;
      }

   }

   @Nullable
   private BlockPos func_203115_a(BlockPos var1, IBlockReader var2) {
      if (var2.func_180495_p(var1).func_177230_c() == this.field_203117_f) {
         return var1;
      } else {
         BlockPos[] var3 = new BlockPos[]{var1.func_177977_b(), var1.func_177976_e(), var1.func_177974_f(), var1.func_177978_c(), var1.func_177968_d(), var1.func_177977_b().func_177977_b()};
         BlockPos[] var4 = var3;
         int var5 = var3.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            BlockPos var7 = var4[var6];
            if (var2.func_180495_p(var7).func_177230_c() == this.field_203117_f) {
               return var7;
            }
         }

         return null;
      }
   }

   protected boolean func_179488_a(IWorldReaderBase var1, BlockPos var2) {
      Block var3 = var1.func_180495_p(var2).func_177230_c();
      return var3 == this.field_203117_f && var1.func_180495_p(var2.func_177984_a()).func_196958_f() && var1.func_180495_p(var2.func_177981_b(2)).func_196958_f();
   }
}
