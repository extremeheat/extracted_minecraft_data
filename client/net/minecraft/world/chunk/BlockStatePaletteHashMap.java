package net.minecraft.world.chunk;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.ObjectIntIdentityMap;

public class BlockStatePaletteHashMap<T> implements IBlockStatePalette<T> {
   private final ObjectIntIdentityMap<T> field_205509_a;
   private final IntIdentityHashBiMap<T> field_186046_a;
   private final IBlockStatePaletteResizer<T> field_186047_b;
   private final Function<NBTTagCompound, T> field_205510_d;
   private final Function<T, NBTTagCompound> field_205511_e;
   private final int field_186048_c;

   public BlockStatePaletteHashMap(ObjectIntIdentityMap<T> var1, int var2, IBlockStatePaletteResizer<T> var3, Function<NBTTagCompound, T> var4, Function<T, NBTTagCompound> var5) {
      super();
      this.field_205509_a = var1;
      this.field_186048_c = var2;
      this.field_186047_b = var3;
      this.field_205510_d = var4;
      this.field_205511_e = var5;
      this.field_186046_a = new IntIdentityHashBiMap(1 << var2);
   }

   public int func_186041_a(T var1) {
      int var2 = this.field_186046_a.func_186815_a(var1);
      if (var2 == -1) {
         var2 = this.field_186046_a.func_186808_c(var1);
         if (var2 >= 1 << this.field_186048_c) {
            var2 = this.field_186047_b.onResize(this.field_186048_c + 1, var1);
         }
      }

      return var2;
   }

   @Nullable
   public T func_186039_a(int var1) {
      return this.field_186046_a.func_186813_a(var1);
   }

   public void func_186038_a(PacketBuffer var1) {
      this.field_186046_a.func_186812_a();
      int var2 = var1.func_150792_a();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.field_186046_a.func_186808_c(this.field_205509_a.func_148745_a(var1.func_150792_a()));
      }

   }

   public void func_186037_b(PacketBuffer var1) {
      int var2 = this.func_202136_b();
      var1.func_150787_b(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.func_150787_b(this.field_205509_a.func_148747_b(this.field_186046_a.func_186813_a(var3)));
      }

   }

   public int func_186040_a() {
      int var1 = PacketBuffer.func_150790_a(this.func_202136_b());

      for(int var2 = 0; var2 < this.func_202136_b(); ++var2) {
         var1 += PacketBuffer.func_150790_a(this.field_205509_a.func_148747_b(this.field_186046_a.func_186813_a(var2)));
      }

      return var1;
   }

   public int func_202136_b() {
      return this.field_186046_a.func_186810_b();
   }

   public void func_196968_a(NBTTagList var1) {
      this.field_186046_a.func_186812_a();

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.field_186046_a.func_186808_c(this.field_205510_d.apply(var1.func_150305_b(var2)));
      }

   }

   public void func_196969_b(NBTTagList var1) {
      for(int var2 = 0; var2 < this.func_202136_b(); ++var2) {
         var1.add((INBTBase)this.field_205511_e.apply(this.field_186046_a.func_186813_a(var2)));
      }

   }
}
