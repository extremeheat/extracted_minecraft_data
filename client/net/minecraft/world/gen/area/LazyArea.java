package net.minecraft.world.gen.area;

import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public final class LazyArea implements IArea {
   private final IPixelTransformer field_202681_a;
   private final Long2IntLinkedOpenHashMap field_202682_b;
   private final int field_202683_c;
   private final AreaDimension field_202684_d;

   public LazyArea(Long2IntLinkedOpenHashMap var1, int var2, AreaDimension var3, IPixelTransformer var4) {
      super();
      this.field_202682_b = var1;
      this.field_202683_c = var2;
      this.field_202684_d = var3;
      this.field_202681_a = var4;
   }

   public int func_202678_a(int var1, int var2) {
      long var3 = this.func_202679_b(var1, var2);
      synchronized(this.field_202682_b) {
         int var6 = this.field_202682_b.get(var3);
         if (var6 != -2147483648) {
            return var6;
         } else {
            int var7 = this.field_202681_a.apply(var1, var2);
            this.field_202682_b.put(var3, var7);
            if (this.field_202682_b.size() > this.field_202683_c) {
               for(int var8 = 0; var8 < this.field_202683_c / 16; ++var8) {
                  this.field_202682_b.removeFirstInt();
               }
            }

            return var7;
         }
      }
   }

   private long func_202679_b(int var1, int var2) {
      long var3 = 1L;
      var3 <<= 26;
      var3 |= (long)(var1 + this.field_202684_d.func_202690_a()) & 67108863L;
      var3 <<= 26;
      var3 |= (long)(var2 + this.field_202684_d.func_202691_b()) & 67108863L;
      return var3;
   }

   public int func_202680_a() {
      return this.field_202683_c;
   }
}
