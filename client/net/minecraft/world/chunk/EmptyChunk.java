package net.minecraft.world.chunk;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public class EmptyChunk extends Chunk {
   public EmptyChunk(World var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   @Override
   public boolean func_76600_a(int var1, int var2) {
      return var1 == this.field_76635_g && var2 == this.field_76647_h;
   }

   @Override
   public int func_76611_b(int var1, int var2) {
      return 0;
   }

   @Override
   public void func_76590_a() {
   }

   @Override
   public void func_76603_b() {
   }

   @Override
   public Block func_150810_a(int var1, int var2, int var3) {
      return Blocks.field_150350_a;
   }

   @Override
   public int func_150808_b(int var1, int var2, int var3) {
      return 255;
   }

   @Override
   public boolean func_150807_a(int var1, int var2, int var3, Block var4, int var5) {
      return true;
   }

   @Override
   public int func_76628_c(int var1, int var2, int var3) {
      return 0;
   }

   @Override
   public boolean func_76589_b(int var1, int var2, int var3, int var4) {
      return false;
   }

   @Override
   public int func_76614_a(EnumSkyBlock var1, int var2, int var3, int var4) {
      return 0;
   }

   @Override
   public void func_76633_a(EnumSkyBlock var1, int var2, int var3, int var4, int var5) {
   }

   @Override
   public int func_76629_c(int var1, int var2, int var3, int var4) {
      return 0;
   }

   @Override
   public void func_76612_a(Entity var1) {
   }

   @Override
   public void func_76622_b(Entity var1) {
   }

   @Override
   public void func_76608_a(Entity var1, int var2) {
   }

   @Override
   public boolean func_76619_d(int var1, int var2, int var3) {
      return false;
   }

   @Override
   public TileEntity func_150806_e(int var1, int var2, int var3) {
      return null;
   }

   @Override
   public void func_150813_a(TileEntity var1) {
   }

   @Override
   public void func_150812_a(int var1, int var2, int var3, TileEntity var4) {
   }

   @Override
   public void func_150805_f(int var1, int var2, int var3) {
   }

   @Override
   public void func_76631_c() {
   }

   @Override
   public void func_76623_d() {
   }

   @Override
   public void func_76630_e() {
   }

   @Override
   public void func_76588_a(Entity var1, AxisAlignedBB var2, List var3, IEntitySelector var4) {
   }

   @Override
   public void func_76618_a(Class var1, AxisAlignedBB var2, List var3, IEntitySelector var4) {
   }

   @Override
   public boolean func_76601_a(boolean var1) {
      return false;
   }

   @Override
   public Random func_76617_a(long var1) {
      return new Random(
         this.field_76637_e.func_72905_C()
               + (long)(this.field_76635_g * this.field_76635_g * 4987142)
               + (long)(this.field_76635_g * 5947611)
               + (long)(this.field_76647_h * this.field_76647_h) * 4392871L
               + (long)(this.field_76647_h * 389711)
            ^ var1
      );
   }

   @Override
   public boolean func_76621_g() {
      return true;
   }

   @Override
   public boolean func_76606_c(int var1, int var2) {
      return true;
   }
}
