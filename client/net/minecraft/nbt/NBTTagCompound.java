package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagCompound extends NBTBase {
   private static final Logger field_150301_b = LogManager.getLogger();
   private Map field_74784_a = new HashMap();

   public NBTTagCompound() {
      super();
   }

   @Override
   void func_74734_a(DataOutput var1) {
      for(String var3 : this.field_74784_a.keySet()) {
         NBTBase var4 = (NBTBase)this.field_74784_a.get(var3);
         func_150298_a(var3, var4, var1);
      }

      var1.writeByte(0);
   }

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
      if (var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.field_74784_a.clear();

         byte var4;
         while((var4 = func_152447_a(var1, var3)) != false) {
            String var5 = func_152448_b(var1, var3);
            var3.func_152450_a((long)(16 * var5.length()));
            NBTBase var6 = func_152449_a(var4, var5, var1, var2 + 1, var3);
            this.field_74784_a.put(var5, var6);
         }
      }
   }

   public Set func_150296_c() {
      return this.field_74784_a.keySet();
   }

   @Override
   public byte func_74732_a() {
      return 10;
   }

   public void func_74782_a(String var1, NBTBase var2) {
      this.field_74784_a.put(var1, var2);
   }

   public void func_74774_a(String var1, byte var2) {
      this.field_74784_a.put(var1, new NBTTagByte(var2));
   }

   public void func_74777_a(String var1, short var2) {
      this.field_74784_a.put(var1, new NBTTagShort(var2));
   }

   public void func_74768_a(String var1, int var2) {
      this.field_74784_a.put(var1, new NBTTagInt(var2));
   }

   public void func_74772_a(String var1, long var2) {
      this.field_74784_a.put(var1, new NBTTagLong(var2));
   }

   public void func_74776_a(String var1, float var2) {
      this.field_74784_a.put(var1, new NBTTagFloat(var2));
   }

   public void func_74780_a(String var1, double var2) {
      this.field_74784_a.put(var1, new NBTTagDouble(var2));
   }

   public void func_74778_a(String var1, String var2) {
      this.field_74784_a.put(var1, new NBTTagString(var2));
   }

   public void func_74773_a(String var1, byte[] var2) {
      this.field_74784_a.put(var1, new NBTTagByteArray(var2));
   }

   public void func_74783_a(String var1, int[] var2) {
      this.field_74784_a.put(var1, new NBTTagIntArray(var2));
   }

   public void func_74757_a(String var1, boolean var2) {
      this.func_74774_a(var1, (byte)(var2 ? 1 : 0));
   }

   public NBTBase func_74781_a(String var1) {
      return (NBTBase)this.field_74784_a.get(var1);
   }

   public byte func_150299_b(String var1) {
      NBTBase var2 = (NBTBase)this.field_74784_a.get(var1);
      return var2 != null ? var2.func_74732_a() : 0;
   }

   public boolean func_74764_b(String var1) {
      return this.field_74784_a.containsKey(var1);
   }

   public boolean func_150297_b(String var1, int var2) {
      byte var3 = this.func_150299_b(var1);
      if (var3 == var2) {
         return true;
      } else if (var2 != 99) {
         return false;
      } else {
         return var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4 || var3 == 5 || var3 == 6;
      }
   }

   public byte func_74771_c(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? 0 : ((NBTBase$NBTPrimitive)this.field_74784_a.get(var1)).func_150290_f();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public short func_74765_d(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? 0 : ((NBTBase$NBTPrimitive)this.field_74784_a.get(var1)).func_150289_e();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public int func_74762_e(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? 0 : ((NBTBase$NBTPrimitive)this.field_74784_a.get(var1)).func_150287_d();
      } catch (ClassCastException var3) {
         return 0;
      }
   }

   public long func_74763_f(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? 0L : ((NBTBase$NBTPrimitive)this.field_74784_a.get(var1)).func_150291_c();
      } catch (ClassCastException var3) {
         return 0L;
      }
   }

   public float func_74760_g(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? 0.0F : ((NBTBase$NBTPrimitive)this.field_74784_a.get(var1)).func_150288_h();
      } catch (ClassCastException var3) {
         return 0.0F;
      }
   }

   public double func_74769_h(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? 0.0 : ((NBTBase$NBTPrimitive)this.field_74784_a.get(var1)).func_150286_g();
      } catch (ClassCastException var3) {
         return 0.0;
      }
   }

   public String func_74779_i(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? "" : ((NBTBase)this.field_74784_a.get(var1)).func_150285_a_();
      } catch (ClassCastException var3) {
         return "";
      }
   }

   public byte[] func_74770_j(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? new byte[0] : ((NBTTagByteArray)this.field_74784_a.get(var1)).func_150292_c();
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_82581_a(var1, 7, var3));
      }
   }

   public int[] func_74759_k(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? new int[0] : ((NBTTagIntArray)this.field_74784_a.get(var1)).func_150302_c();
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_82581_a(var1, 11, var3));
      }
   }

   public NBTTagCompound func_74775_l(String var1) {
      try {
         return !this.field_74784_a.containsKey(var1) ? new NBTTagCompound() : (NBTTagCompound)this.field_74784_a.get(var1);
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_82581_a(var1, 10, var3));
      }
   }

   public NBTTagList func_150295_c(String var1, int var2) {
      try {
         if (this.func_150299_b(var1) != 9) {
            return new NBTTagList();
         } else {
            NBTTagList var3 = (NBTTagList)this.field_74784_a.get(var1);
            return var3.func_74745_c() > 0 && var3.func_150303_d() != var2 ? new NBTTagList() : var3;
         }
      } catch (ClassCastException var4) {
         throw new ReportedException(this.func_82581_a(var1, 9, var4));
      }
   }

   public boolean func_74767_n(String var1) {
      return this.func_74771_c(var1) != 0;
   }

   public void func_82580_o(String var1) {
      this.field_74784_a.remove(var1);
   }

   @Override
   public String toString() {
      String var1 = "{";

      for(String var3 : this.field_74784_a.keySet()) {
         var1 = var1 + var3 + ':' + this.field_74784_a.get(var3) + ',';
      }

      return var1 + "}";
   }

   public boolean func_82582_d() {
      return this.field_74784_a.isEmpty();
   }

   private CrashReport func_82581_a(String var1, int var2, ClassCastException var3) {
      CrashReport var4 = CrashReport.func_85055_a(var3, "Reading NBT data");
      CrashReportCategory var5 = var4.func_85057_a("Corrupt NBT tag", 1);
      var5.func_71500_a("Tag type found", new NBTTagCompound$1(this, var1));
      var5.func_71500_a("Tag type expected", new NBTTagCompound$2(this, var2));
      var5.func_71507_a("Tag name", var1);
      return var4;
   }

   @Override
   public NBTBase func_74737_b() {
      NBTTagCompound var1 = new NBTTagCompound();

      for(String var3 : this.field_74784_a.keySet()) {
         var1.func_74782_a(var3, ((NBTBase)this.field_74784_a.get(var3)).func_74737_b());
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagCompound var2 = (NBTTagCompound)var1;
         return this.field_74784_a.entrySet().equals(var2.field_74784_a.entrySet());
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.field_74784_a.hashCode();
   }

   private static void func_150298_a(String var0, NBTBase var1, DataOutput var2) {
      var2.writeByte(var1.func_74732_a());
      if (var1.func_74732_a() != 0) {
         var2.writeUTF(var0);
         var1.func_74734_a(var2);
      }
   }

   private static byte func_152447_a(DataInput var0, NBTSizeTracker var1) {
      return var0.readByte();
   }

   private static String func_152448_b(DataInput var0, NBTSizeTracker var1) {
      return var0.readUTF();
   }

   static NBTBase func_152449_a(byte var0, String var1, DataInput var2, int var3, NBTSizeTracker var4) {
      NBTBase var5 = NBTBase.func_150284_a(var0);

      try {
         var5.func_152446_a(var2, var3, var4);
         return var5;
      } catch (IOException var9) {
         CrashReport var7 = CrashReport.func_85055_a(var9, "Loading NBT data");
         CrashReportCategory var8 = var7.func_85058_a("NBT Tag");
         var8.func_71507_a("Tag name", var1);
         var8.func_71507_a("Tag type", var0);
         throw new ReportedException(var7);
      }
   }
}
