package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagCompound implements INBTBase {
   private static final Logger field_191551_b = LogManager.getLogger();
   private static final Pattern field_193583_c = Pattern.compile("[A-Za-z0-9._+-]+");
   private final Map<String, INBTBase> field_74784_a = Maps.newHashMap();

   public NBTTagCompound() {
      super();
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      Iterator var2 = this.field_74784_a.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         INBTBase var4 = (INBTBase)this.field_74784_a.get(var3);
         func_150298_a(var3, var4, var1);
      }

      var1.writeByte(0);
   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(384L);
      if (var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.field_74784_a.clear();

         byte var4;
         while((var4 = func_152447_a(var1, var3)) != 0) {
            String var5 = func_152448_b(var1, var3);
            var3.func_152450_a((long)(224 + 16 * var5.length()));
            INBTBase var6 = func_152449_a(var4, var5, var1, var2 + 1, var3);
            if (this.field_74784_a.put(var5, var6) != null) {
               var3.func_152450_a(288L);
            }
         }

      }
   }

   public Set<String> func_150296_c() {
      return this.field_74784_a.keySet();
   }

   public byte func_74732_a() {
      return 10;
   }

   public int func_186856_d() {
      return this.field_74784_a.size();
   }

   public void func_74782_a(String var1, INBTBase var2) {
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

   public void func_186854_a(String var1, UUID var2) {
      this.func_74772_a(var1 + "Most", var2.getMostSignificantBits());
      this.func_74772_a(var1 + "Least", var2.getLeastSignificantBits());
   }

   @Nullable
   public UUID func_186857_a(String var1) {
      return new UUID(this.func_74763_f(var1 + "Most"), this.func_74763_f(var1 + "Least"));
   }

   public boolean func_186855_b(String var1) {
      return this.func_150297_b(var1 + "Most", 99) && this.func_150297_b(var1 + "Least", 99);
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

   public void func_197646_b(String var1, List<Integer> var2) {
      this.field_74784_a.put(var1, new NBTTagIntArray(var2));
   }

   public void func_197644_a(String var1, long[] var2) {
      this.field_74784_a.put(var1, new NBTTagLongArray(var2));
   }

   public void func_202168_c(String var1, List<Long> var2) {
      this.field_74784_a.put(var1, new NBTTagLongArray(var2));
   }

   public void func_74757_a(String var1, boolean var2) {
      this.func_74774_a(var1, (byte)(var2 ? 1 : 0));
   }

   public INBTBase func_74781_a(String var1) {
      return (INBTBase)this.field_74784_a.get(var1);
   }

   public byte func_150299_b(String var1) {
      INBTBase var2 = (INBTBase)this.field_74784_a.get(var1);
      return var2 == null ? 0 : var2.func_74732_a();
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
         if (this.func_150297_b(var1, 99)) {
            return ((NBTPrimitive)this.field_74784_a.get(var1)).func_150290_f();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public short func_74765_d(String var1) {
      try {
         if (this.func_150297_b(var1, 99)) {
            return ((NBTPrimitive)this.field_74784_a.get(var1)).func_150289_e();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public int func_74762_e(String var1) {
      try {
         if (this.func_150297_b(var1, 99)) {
            return ((NBTPrimitive)this.field_74784_a.get(var1)).func_150287_d();
         }
      } catch (ClassCastException var3) {
      }

      return 0;
   }

   public long func_74763_f(String var1) {
      try {
         if (this.func_150297_b(var1, 99)) {
            return ((NBTPrimitive)this.field_74784_a.get(var1)).func_150291_c();
         }
      } catch (ClassCastException var3) {
      }

      return 0L;
   }

   public float func_74760_g(String var1) {
      try {
         if (this.func_150297_b(var1, 99)) {
            return ((NBTPrimitive)this.field_74784_a.get(var1)).func_150288_h();
         }
      } catch (ClassCastException var3) {
      }

      return 0.0F;
   }

   public double func_74769_h(String var1) {
      try {
         if (this.func_150297_b(var1, 99)) {
            return ((NBTPrimitive)this.field_74784_a.get(var1)).func_150286_g();
         }
      } catch (ClassCastException var3) {
      }

      return 0.0D;
   }

   public String func_74779_i(String var1) {
      try {
         if (this.func_150297_b(var1, 8)) {
            return ((INBTBase)this.field_74784_a.get(var1)).func_150285_a_();
         }
      } catch (ClassCastException var3) {
      }

      return "";
   }

   public byte[] func_74770_j(String var1) {
      try {
         if (this.func_150297_b(var1, 7)) {
            return ((NBTTagByteArray)this.field_74784_a.get(var1)).func_150292_c();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_82581_a(var1, 7, var3));
      }

      return new byte[0];
   }

   public int[] func_74759_k(String var1) {
      try {
         if (this.func_150297_b(var1, 11)) {
            return ((NBTTagIntArray)this.field_74784_a.get(var1)).func_150302_c();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_82581_a(var1, 11, var3));
      }

      return new int[0];
   }

   public long[] func_197645_o(String var1) {
      try {
         if (this.func_150297_b(var1, 12)) {
            return ((NBTTagLongArray)this.field_74784_a.get(var1)).func_197652_h();
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_82581_a(var1, 12, var3));
      }

      return new long[0];
   }

   public NBTTagCompound func_74775_l(String var1) {
      try {
         if (this.func_150297_b(var1, 10)) {
            return (NBTTagCompound)this.field_74784_a.get(var1);
         }
      } catch (ClassCastException var3) {
         throw new ReportedException(this.func_82581_a(var1, 10, var3));
      }

      return new NBTTagCompound();
   }

   public NBTTagList func_150295_c(String var1, int var2) {
      try {
         if (this.func_150299_b(var1) == 9) {
            NBTTagList var3 = (NBTTagList)this.field_74784_a.get(var1);
            if (!var3.isEmpty() && var3.func_150303_d() != var2) {
               return new NBTTagList();
            }

            return var3;
         }
      } catch (ClassCastException var4) {
         throw new ReportedException(this.func_82581_a(var1, 9, var4));
      }

      return new NBTTagList();
   }

   public boolean func_74767_n(String var1) {
      return this.func_74771_c(var1) != 0;
   }

   public void func_82580_o(String var1) {
      this.field_74784_a.remove(var1);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("{");
      Object var2 = this.field_74784_a.keySet();
      if (field_191551_b.isDebugEnabled()) {
         ArrayList var3 = Lists.newArrayList(this.field_74784_a.keySet());
         Collections.sort(var3);
         var2 = var3;
      }

      String var4;
      for(Iterator var5 = ((Collection)var2).iterator(); var5.hasNext(); var1.append(func_193582_s(var4)).append(':').append(this.field_74784_a.get(var4))) {
         var4 = (String)var5.next();
         if (var1.length() != 1) {
            var1.append(',');
         }
      }

      return var1.append('}').toString();
   }

   public boolean isEmpty() {
      return this.field_74784_a.isEmpty();
   }

   private CrashReport func_82581_a(String var1, int var2, ClassCastException var3) {
      CrashReport var4 = CrashReport.func_85055_a(var3, "Reading NBT data");
      CrashReportCategory var5 = var4.func_85057_a("Corrupt NBT tag", 1);
      var5.func_189529_a("Tag type found", () -> {
         return field_82578_b[((INBTBase)this.field_74784_a.get(var1)).func_74732_a()];
      });
      var5.func_189529_a("Tag type expected", () -> {
         return field_82578_b[var2];
      });
      var5.func_71507_a("Tag name", var1);
      return var4;
   }

   public NBTTagCompound func_74737_b() {
      NBTTagCompound var1 = new NBTTagCompound();
      Iterator var2 = this.field_74784_a.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         var1.func_74782_a(var3, ((INBTBase)this.field_74784_a.get(var3)).func_74737_b());
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagCompound && Objects.equals(this.field_74784_a, ((NBTTagCompound)var1).field_74784_a);
      }
   }

   public int hashCode() {
      return this.field_74784_a.hashCode();
   }

   private static void func_150298_a(String var0, INBTBase var1, DataOutput var2) throws IOException {
      var2.writeByte(var1.func_74732_a());
      if (var1.func_74732_a() != 0) {
         var2.writeUTF(var0);
         var1.func_74734_a(var2);
      }
   }

   private static byte func_152447_a(DataInput var0, NBTSizeTracker var1) throws IOException {
      return var0.readByte();
   }

   private static String func_152448_b(DataInput var0, NBTSizeTracker var1) throws IOException {
      return var0.readUTF();
   }

   static INBTBase func_152449_a(byte var0, String var1, DataInput var2, int var3, NBTSizeTracker var4) throws IOException {
      INBTBase var5 = INBTBase.func_150284_a(var0);

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

   public NBTTagCompound func_197643_a(NBTTagCompound var1) {
      Iterator var2 = var1.field_74784_a.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         INBTBase var4 = (INBTBase)var1.field_74784_a.get(var3);
         if (var4.func_74732_a() == 10) {
            if (this.func_150297_b(var3, 10)) {
               NBTTagCompound var5 = this.func_74775_l(var3);
               var5.func_197643_a((NBTTagCompound)var4);
            } else {
               this.func_74782_a(var3, var4.func_74737_b());
            }
         } else {
            this.func_74782_a(var3, var4.func_74737_b());
         }
      }

      return this;
   }

   protected static String func_193582_s(String var0) {
      return field_193583_c.matcher(var0).matches() ? var0 : NBTTagString.func_197654_a(var0, true);
   }

   protected static ITextComponent func_197642_t(String var0) {
      if (field_193583_c.matcher(var0).matches()) {
         return (new TextComponentString(var0)).func_211708_a(field_197638_b);
      } else {
         ITextComponent var1 = (new TextComponentString(NBTTagString.func_197654_a(var0, false))).func_211708_a(field_197638_b);
         return (new TextComponentString("\"")).func_150257_a(var1).func_150258_a("\"");
      }
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      if (this.field_74784_a.isEmpty()) {
         return new TextComponentString("{}");
      } else {
         TextComponentString var3 = new TextComponentString("{");
         Object var4 = this.field_74784_a.keySet();
         if (field_191551_b.isDebugEnabled()) {
            ArrayList var5 = Lists.newArrayList(this.field_74784_a.keySet());
            Collections.sort(var5);
            var4 = var5;
         }

         if (!var1.isEmpty()) {
            var3.func_150258_a("\n");
         }

         ITextComponent var7;
         for(Iterator var8 = ((Collection)var4).iterator(); var8.hasNext(); var3.func_150257_a(var7)) {
            String var6 = (String)var8.next();
            var7 = (new TextComponentString(Strings.repeat(var1, var2 + 1))).func_150257_a(func_197642_t(var6)).func_150258_a(String.valueOf(':')).func_150258_a(" ").func_150257_a(((INBTBase)this.field_74784_a.get(var6)).func_199850_a(var1, var2 + 1));
            if (var8.hasNext()) {
               var7.func_150258_a(String.valueOf(',')).func_150258_a(var1.isEmpty() ? " " : "\n");
            }
         }

         if (!var1.isEmpty()) {
            var3.func_150258_a("\n").func_150258_a(Strings.repeat(var1, var2));
         }

         var3.func_150258_a("}");
         return var3;
      }
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }
}
