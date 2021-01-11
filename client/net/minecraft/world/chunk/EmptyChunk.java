package net.minecraft.world.chunk;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk {
   public EmptyChunk(World var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   public boolean func_76600_a(int var1, int var2) {
      return var1 == this.field_76635_g && var2 == this.field_76647_h;
   }

   public int func_76611_b(int var1, int var2) {
      return 0;
   }

   public void func_76590_a() {
   }

   public void func_76603_b() {
   }

   public Block func_177428_a(BlockPos var1) {
      return Blocks.field_150350_a;
   }

   public int func_177437_b(BlockPos var1) {
      return 255;
   }

   public int func_177418_c(BlockPos var1) {
      return 0;
   }

   public int func_177413_a(EnumSkyBlock var1, BlockPos var2) {
      return var1.field_77198_c;
   }

   public void func_177431_a(EnumSkyBlock var1, BlockPos var2, int var3) {
   }

   public int func_177443_a(BlockPos var1, int var2) {
      return 0;
   }

   public void func_76612_a(Entity var1) {
   }

   public void func_76622_b(Entity var1) {
   }

   public void func_76608_a(Entity var1, int var2) {
   }

   public boolean func_177444_d(BlockPos var1) {
      return false;
   }

   public TileEntity func_177424_a(BlockPos var1, Chunk.EnumCreateEntityType var2) {
      return null;
   }

   public void func_150813_a(TileEntity var1) {
   }

   public void func_177426_a(BlockPos var1, TileEntity var2) {
   }

   public void func_177425_e(BlockPos var1) {
   }

   public void func_76631_c() {
   }

   public void func_76623_d() {
   }

   public void func_76630_e() {
   }

   public void func_177414_a(Entity var1, AxisAlignedBB var2, List<Entity> var3, Predicate<? super Entity> var4) {
   }

   public <T extends Entity> void func_177430_a(Class<? extends T> var1, AxisAlignedBB var2, List<T> var3, Predicate<? super T> var4) {
   }

   public boolean func_76601_a(boolean var1) {
      return false;
   }

   public Random func_76617_a(long var1) {
      return new Random(this.func_177412_p().func_72905_C() + (long)(this.field_76635_g * this.field_76635_g * 4987142) + (long)(this.field_76635_g * 5947611) + (long)(this.field_76647_h * this.field_76647_h) * 4392871L + (long)(this.field_76647_h * 389711) ^ var1);
   }

   public boolean func_76621_g() {
      return true;
   }

   public boolean func_76606_c(int var1, int var2) {
      return true;
   }
}
