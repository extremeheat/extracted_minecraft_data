package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTUtil;

public class ChunkSection {
   private static final IBlockStatePalette<IBlockState> field_205512_a;
   private final int field_76684_a;
   private int field_76682_b;
   private int field_76683_c;
   private int field_206918_e;
   private final BlockStateContainer<IBlockState> field_177488_d;
   private NibbleArray field_76679_g;
   private NibbleArray field_76685_h;

   public ChunkSection(int var1, boolean var2) {
      super();
      this.field_76684_a = var1;
      this.field_177488_d = new BlockStateContainer(field_205512_a, Block.field_176229_d, NBTUtil::func_190008_d, NBTUtil::func_190009_a, Blocks.field_150350_a.func_176223_P());
      this.field_76679_g = new NibbleArray();
      if (var2) {
         this.field_76685_h = new NibbleArray();
      }

   }

   public IBlockState func_177485_a(int var1, int var2, int var3) {
      return (IBlockState)this.field_177488_d.func_186016_a(var1, var2, var3);
   }

   public IFluidState func_206914_b(int var1, int var2, int var3) {
      return ((IBlockState)this.field_177488_d.func_186016_a(var1, var2, var3)).func_204520_s();
   }

   public void func_177484_a(int var1, int var2, int var3, IBlockState var4) {
      IBlockState var5 = this.func_177485_a(var1, var2, var3);
      IFluidState var6 = this.func_206914_b(var1, var2, var3);
      IFluidState var7 = var4.func_204520_s();
      if (!var5.func_196958_f()) {
         --this.field_76682_b;
         if (var5.func_204519_t()) {
            --this.field_76683_c;
         }
      }

      if (!var6.func_206888_e()) {
         --this.field_206918_e;
      }

      if (!var4.func_196958_f()) {
         ++this.field_76682_b;
         if (var4.func_204519_t()) {
            ++this.field_76683_c;
         }
      }

      if (!var7.func_206888_e()) {
         --this.field_206918_e;
      }

      this.field_177488_d.func_186013_a(var1, var2, var3, var4);
   }

   public boolean func_76663_a() {
      return this.field_76682_b == 0;
   }

   public boolean func_206915_b() {
      return this.func_76675_b() || this.func_206917_d();
   }

   public boolean func_76675_b() {
      return this.field_76683_c > 0;
   }

   public boolean func_206917_d() {
      return this.field_206918_e > 0;
   }

   public int func_76662_d() {
      return this.field_76684_a;
   }

   public void func_76657_c(int var1, int var2, int var3, int var4) {
      this.field_76685_h.func_76581_a(var1, var2, var3, var4);
   }

   public int func_76670_c(int var1, int var2, int var3) {
      return this.field_76685_h.func_76582_a(var1, var2, var3);
   }

   public void func_76677_d(int var1, int var2, int var3, int var4) {
      this.field_76679_g.func_76581_a(var1, var2, var3, var4);
   }

   public int func_76674_d(int var1, int var2, int var3) {
      return this.field_76679_g.func_76582_a(var1, var2, var3);
   }

   public void func_76672_e() {
      this.field_76682_b = 0;
      this.field_76683_c = 0;
      this.field_206918_e = 0;

      for(int var1 = 0; var1 < 16; ++var1) {
         for(int var2 = 0; var2 < 16; ++var2) {
            for(int var3 = 0; var3 < 16; ++var3) {
               IBlockState var4 = this.func_177485_a(var1, var2, var3);
               IFluidState var5 = this.func_206914_b(var1, var2, var3);
               if (!var4.func_196958_f()) {
                  ++this.field_76682_b;
                  if (var4.func_204519_t()) {
                     ++this.field_76683_c;
                  }
               }

               if (!var5.func_206888_e()) {
                  ++this.field_76682_b;
                  if (var5.func_206890_h()) {
                     ++this.field_206918_e;
                  }
               }
            }
         }
      }

   }

   public BlockStateContainer<IBlockState> func_186049_g() {
      return this.field_177488_d;
   }

   public NibbleArray func_76661_k() {
      return this.field_76679_g;
   }

   public NibbleArray func_76671_l() {
      return this.field_76685_h;
   }

   public void func_76659_c(NibbleArray var1) {
      this.field_76679_g = var1;
   }

   public void func_76666_d(NibbleArray var1) {
      this.field_76685_h = var1;
   }

   static {
      field_205512_a = new BlockStatePaletteRegistry(Block.field_176229_d, Blocks.field_150350_a.func_176223_P());
   }
}
