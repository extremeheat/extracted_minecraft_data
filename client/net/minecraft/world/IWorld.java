package net.minecraft.world;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

public interface IWorld extends IWorldReaderBase, ISaveDataAccess, IWorldWriter {
   long func_72905_C();

   default float func_130001_d() {
      return Dimension.field_111203_a[this.func_201675_m().func_76559_b(this.func_72912_H().func_76073_f())];
   }

   default float func_72826_c(float var1) {
      return this.func_201675_m().func_76563_a(this.func_72912_H().func_76073_f(), var1);
   }

   default int func_72853_d() {
      return this.func_201675_m().func_76559_b(this.func_72912_H().func_76073_f());
   }

   ITickList<Block> func_205220_G_();

   ITickList<Fluid> func_205219_F_();

   default IChunk func_205771_y(BlockPos var1) {
      return this.func_72964_e(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }

   IChunk func_72964_e(int var1, int var2);

   World func_201672_e();

   WorldInfo func_72912_H();

   DifficultyInstance func_175649_E(BlockPos var1);

   default EnumDifficulty func_175659_aa() {
      return this.func_72912_H().func_176130_y();
   }

   IChunkProvider func_72863_F();

   ISaveHandler func_72860_G();

   Random func_201674_k();

   void func_195592_c(BlockPos var1, Block var2);

   BlockPos func_175694_M();

   void func_184133_a(@Nullable EntityPlayer var1, BlockPos var2, SoundEvent var3, SoundCategory var4, float var5, float var6);

   void func_195594_a(IParticleData var1, double var2, double var4, double var6, double var8, double var10, double var12);
}
