package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;

public class CompiledChunk {
   public static final CompiledChunk field_178502_a = new CompiledChunk() {
      protected void func_178486_a(BlockRenderLayer var1) {
         throw new UnsupportedOperationException();
      }

      public void func_178493_c(BlockRenderLayer var1) {
         throw new UnsupportedOperationException();
      }

      public boolean func_178495_a(EnumFacing var1, EnumFacing var2) {
         return false;
      }
   };
   private final boolean[] field_178500_b = new boolean[BlockRenderLayer.values().length];
   private final boolean[] field_178501_c = new boolean[BlockRenderLayer.values().length];
   private boolean field_178498_d = true;
   private final List<TileEntity> field_178499_e = Lists.newArrayList();
   private SetVisibility field_178496_f = new SetVisibility();
   private BufferBuilder.State field_178497_g;

   public CompiledChunk() {
      super();
   }

   public boolean func_178489_a() {
      return this.field_178498_d;
   }

   protected void func_178486_a(BlockRenderLayer var1) {
      this.field_178498_d = false;
      this.field_178500_b[var1.ordinal()] = true;
   }

   public boolean func_178491_b(BlockRenderLayer var1) {
      return !this.field_178500_b[var1.ordinal()];
   }

   public void func_178493_c(BlockRenderLayer var1) {
      this.field_178501_c[var1.ordinal()] = true;
   }

   public boolean func_178492_d(BlockRenderLayer var1) {
      return this.field_178501_c[var1.ordinal()];
   }

   public List<TileEntity> func_178485_b() {
      return this.field_178499_e;
   }

   public void func_178490_a(TileEntity var1) {
      this.field_178499_e.add(var1);
   }

   public boolean func_178495_a(EnumFacing var1, EnumFacing var2) {
      return this.field_178496_f.func_178621_a(var1, var2);
   }

   public void func_178488_a(SetVisibility var1) {
      this.field_178496_f = var1;
   }

   public BufferBuilder.State func_178487_c() {
      return this.field_178497_g;
   }

   public void func_178494_a(BufferBuilder.State var1) {
      this.field_178497_g = var1;
   }
}
