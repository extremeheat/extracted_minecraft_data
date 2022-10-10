package net.minecraft.world.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ObjectIntIdentityMap;

public class BlockStatePaletteLinear<T> implements IBlockStatePalette<T> {
   private final ObjectIntIdentityMap<T> field_205507_a;
   private final T[] field_186042_a;
   private final IBlockStatePaletteResizer<T> field_186043_b;
   private final Function<NBTTagCompound, T> field_205508_d;
   private final int field_186044_c;
   private int field_186045_d;

   public BlockStatePaletteLinear(ObjectIntIdentityMap<T> var1, int var2, IBlockStatePaletteResizer<T> var3, Function<NBTTagCompound, T> var4) {
      super();
      this.field_205507_a = var1;
      this.field_186042_a = (Object[])(new Object[1 << var2]);
      this.field_186044_c = var2;
      this.field_186043_b = var3;
      this.field_205508_d = var4;
   }

   public int func_186041_a(T var1) {
      int var2;
      for(var2 = 0; var2 < this.field_186045_d; ++var2) {
         if (this.field_186042_a[var2] == var1) {
            return var2;
         }
      }

      var2 = this.field_186045_d;
      if (var2 < this.field_186042_a.length) {
         this.field_186042_a[var2] = var1;
         ++this.field_186045_d;
         return var2;
      } else {
         return this.field_186043_b.onResize(this.field_186044_c + 1, var1);
      }
   }

   @Nullable
   public T func_186039_a(int var1) {
      return var1 >= 0 && var1 < this.field_186045_d ? this.field_186042_a[var1] : null;
   }

   public void func_186038_a(PacketBuffer var1) {
      this.field_186045_d = var1.func_150792_a();

      for(int var2 = 0; var2 < this.field_186045_d; ++var2) {
         this.field_186042_a[var2] = this.field_205507_a.func_148745_a(var1.func_150792_a());
      }

   }

   public void func_186037_b(PacketBuffer var1) {
      var1.func_150787_b(this.field_186045_d);

      for(int var2 = 0; var2 < this.field_186045_d; ++var2) {
         var1.func_150787_b(this.field_205507_a.func_148747_b(this.field_186042_a[var2]));
      }

   }

   public int func_186040_a() {
      int var1 = PacketBuffer.func_150790_a(this.func_202137_b());

      for(int var2 = 0; var2 < this.func_202137_b(); ++var2) {
         var1 += PacketBuffer.func_150790_a(this.field_205507_a.func_148747_b(this.field_186042_a[var2]));
      }

      return var1;
   }

   public int func_202137_b() {
      return this.field_186045_d;
   }

   public void func_196968_a(NBTTagList var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.field_186042_a[var2] = this.field_205508_d.apply(var1.func_150305_b(var2));
      }

      this.field_186045_d = var1.size();
   }
}
