package net.minecraft.world.chunk;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BitArray;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.MathHelper;

public class BlockStateContainer<T> implements IBlockStatePaletteResizer<T> {
   private final IBlockStatePalette<T> field_205521_b;
   private final IBlockStatePaletteResizer<T> field_205522_c = (var0, var1x) -> {
      return 0;
   };
   private final ObjectIntIdentityMap<T> field_205523_d;
   private final Function<NBTTagCompound, T> field_205524_e;
   private final Function<T, NBTTagCompound> field_205525_f;
   private final T field_205526_g;
   protected BitArray field_186021_b;
   private IBlockStatePalette<T> field_186022_c;
   private int field_186024_e;
   private final ReentrantLock field_210461_j = new ReentrantLock();

   private void func_210459_b() {
      if (this.field_210461_j.isLocked() && !this.field_210461_j.isHeldByCurrentThread()) {
         String var1 = (String)Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map((var0) -> {
            return var0.getName() + ": \n\tat " + (String)Arrays.stream(var0.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
         }).collect(Collectors.joining("\n"));
         CrashReport var2 = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
         CrashReportCategory var3 = var2.func_85058_a("Thread dumps");
         var3.func_71507_a("Thread dumps", var1);
         throw new ReportedException(var2);
      } else {
         this.field_210461_j.lock();
      }
   }

   private void func_210460_c() {
      this.field_210461_j.unlock();
   }

   public BlockStateContainer(IBlockStatePalette<T> var1, ObjectIntIdentityMap<T> var2, Function<NBTTagCompound, T> var3, Function<T, NBTTagCompound> var4, T var5) {
      super();
      this.field_205521_b = var1;
      this.field_205523_d = var2;
      this.field_205524_e = var3;
      this.field_205525_f = var4;
      this.field_205526_g = var5;
      this.func_186012_b(4);
   }

   private static int func_186011_b(int var0, int var1, int var2) {
      return var1 << 8 | var2 << 4 | var0;
   }

   private void func_186012_b(int var1) {
      if (var1 != this.field_186024_e) {
         this.field_186024_e = var1;
         if (this.field_186024_e <= 4) {
            this.field_186024_e = 4;
            this.field_186022_c = new BlockStatePaletteLinear(this.field_205523_d, this.field_186024_e, this, this.field_205524_e);
         } else if (this.field_186024_e < 9) {
            this.field_186022_c = new BlockStatePaletteHashMap(this.field_205523_d, this.field_186024_e, this, this.field_205524_e, this.field_205525_f);
         } else {
            this.field_186022_c = this.field_205521_b;
            this.field_186024_e = MathHelper.func_151241_e(this.field_205523_d.func_186804_a());
         }

         this.field_186022_c.func_186041_a(this.field_205526_g);
         this.field_186021_b = new BitArray(this.field_186024_e, 4096);
      }
   }

   public int onResize(int var1, T var2) {
      this.func_210459_b();
      BitArray var3 = this.field_186021_b;
      IBlockStatePalette var4 = this.field_186022_c;
      this.func_186012_b(var1);

      int var5;
      for(var5 = 0; var5 < var3.func_188144_b(); ++var5) {
         Object var6 = var4.func_186039_a(var3.func_188142_a(var5));
         if (var6 != null) {
            this.func_186014_b(var5, var6);
         }
      }

      var5 = this.field_186022_c.func_186041_a(var2);
      this.func_210460_c();
      return var5;
   }

   public void func_186013_a(int var1, int var2, int var3, T var4) {
      this.func_210459_b();
      this.func_186014_b(func_186011_b(var1, var2, var3), var4);
      this.func_210460_c();
   }

   protected void func_186014_b(int var1, T var2) {
      int var3 = this.field_186022_c.func_186041_a(var2);
      this.field_186021_b.func_188141_a(var1, var3);
   }

   public T func_186016_a(int var1, int var2, int var3) {
      return this.func_186015_a(func_186011_b(var1, var2, var3));
   }

   protected T func_186015_a(int var1) {
      Object var2 = this.field_186022_c.func_186039_a(this.field_186021_b.func_188142_a(var1));
      return var2 == null ? this.field_205526_g : var2;
   }

   public void func_186010_a(PacketBuffer var1) {
      this.func_210459_b();
      byte var2 = var1.readByte();
      if (this.field_186024_e != var2) {
         this.func_186012_b(var2);
      }

      this.field_186022_c.func_186038_a(var1);
      var1.func_186873_b(this.field_186021_b.func_188143_a());
      this.func_210460_c();
   }

   public void func_186009_b(PacketBuffer var1) {
      this.func_210459_b();
      var1.writeByte(this.field_186024_e);
      this.field_186022_c.func_186037_b(var1);
      var1.func_186865_a(this.field_186021_b.func_188143_a());
      this.func_210460_c();
   }

   public void func_196964_a(NBTTagCompound var1, String var2, String var3) {
      this.func_210459_b();
      NBTTagList var4 = var1.func_150295_c(var2, 10);
      int var5 = Math.max(4, MathHelper.func_151241_e(var4.size()));
      if (var5 != this.field_186024_e) {
         this.func_186012_b(var5);
      }

      this.field_186022_c.func_196968_a(var4);
      long[] var6 = var1.func_197645_o(var3);
      int var7 = var6.length * 64 / 4096;
      if (this.field_186022_c == this.field_205521_b) {
         BlockStatePaletteHashMap var8 = new BlockStatePaletteHashMap(this.field_205523_d, var5, this.field_205522_c, this.field_205524_e, this.field_205525_f);
         var8.func_196968_a(var4);
         BitArray var9 = new BitArray(var5, 4096, var6);

         for(int var10 = 0; var10 < 4096; ++var10) {
            this.field_186021_b.func_188141_a(var10, this.field_205521_b.func_186041_a(var8.func_186039_a(var9.func_188142_a(var10))));
         }
      } else if (var7 == this.field_186024_e) {
         System.arraycopy(var6, 0, this.field_186021_b.func_188143_a(), 0, var6.length);
      } else {
         BitArray var11 = new BitArray(var7, 4096, var6);

         for(int var12 = 0; var12 < 4096; ++var12) {
            this.field_186021_b.func_188141_a(var12, var11.func_188142_a(var12));
         }
      }

      this.func_210460_c();
   }

   public void func_196963_b(NBTTagCompound var1, String var2, String var3) {
      this.func_210459_b();
      BlockStatePaletteHashMap var4 = new BlockStatePaletteHashMap(this.field_205523_d, this.field_186024_e, this.field_205522_c, this.field_205524_e, this.field_205525_f);
      var4.func_186041_a(this.field_205526_g);
      int[] var5 = new int[4096];

      for(int var6 = 0; var6 < 4096; ++var6) {
         var5[var6] = var4.func_186041_a(this.func_186015_a(var6));
      }

      NBTTagList var10 = new NBTTagList();
      var4.func_196969_b(var10);
      var1.func_74782_a(var2, var10);
      int var7 = Math.max(4, MathHelper.func_151241_e(var10.size()));
      BitArray var8 = new BitArray(var7, 4096);

      for(int var9 = 0; var9 < var5.length; ++var9) {
         var8.func_188141_a(var9, var5[var9]);
      }

      var1.func_197644_a(var3, var8.func_188143_a());
      this.func_210460_c();
   }

   public int func_186018_a() {
      return 1 + this.field_186022_c.func_186040_a() + PacketBuffer.func_150790_a(this.field_186021_b.func_188144_b()) + this.field_186021_b.func_188143_a().length * 8;
   }
}
