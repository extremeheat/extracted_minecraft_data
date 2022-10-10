package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTTagCollection<INBTBase> {
   private static final Logger field_179239_b = LogManager.getLogger();
   private List<INBTBase> field_74747_a = Lists.newArrayList();
   private byte field_74746_b = 0;

   public NBTTagList() {
      super();
   }

   public void func_74734_a(DataOutput var1) throws IOException {
      if (this.field_74747_a.isEmpty()) {
         this.field_74746_b = 0;
      } else {
         this.field_74746_b = ((INBTBase)this.field_74747_a.get(0)).func_74732_a();
      }

      var1.writeByte(this.field_74746_b);
      var1.writeInt(this.field_74747_a.size());

      for(int var2 = 0; var2 < this.field_74747_a.size(); ++var2) {
         ((INBTBase)this.field_74747_a.get(var2)).func_74734_a(var1);
      }

   }

   public void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
      var3.func_152450_a(296L);
      if (var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         this.field_74746_b = var1.readByte();
         int var4 = var1.readInt();
         if (this.field_74746_b == 0 && var4 > 0) {
            throw new RuntimeException("Missing type on ListTag");
         } else {
            var3.func_152450_a(32L * (long)var4);
            this.field_74747_a = Lists.newArrayListWithCapacity(var4);

            for(int var5 = 0; var5 < var4; ++var5) {
               INBTBase var6 = INBTBase.func_150284_a(this.field_74746_b);
               var6.func_152446_a(var1, var2 + 1, var3);
               this.field_74747_a.add(var6);
            }

         }
      }
   }

   public byte func_74732_a() {
      return 9;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("[");

      for(int var2 = 0; var2 < this.field_74747_a.size(); ++var2) {
         if (var2 != 0) {
            var1.append(',');
         }

         var1.append(this.field_74747_a.get(var2));
      }

      return var1.append(']').toString();
   }

   public boolean add(INBTBase var1) {
      if (var1.func_74732_a() == 0) {
         field_179239_b.warn("Invalid TagEnd added to ListTag");
         return false;
      } else {
         if (this.field_74746_b == 0) {
            this.field_74746_b = var1.func_74732_a();
         } else if (this.field_74746_b != var1.func_74732_a()) {
            field_179239_b.warn("Adding mismatching tag types to tag list");
            return false;
         }

         this.field_74747_a.add(var1);
         return true;
      }
   }

   public INBTBase set(int var1, INBTBase var2) {
      if (var2.func_74732_a() == 0) {
         field_179239_b.warn("Invalid TagEnd added to ListTag");
         return (INBTBase)this.field_74747_a.get(var1);
      } else if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         if (this.field_74746_b == 0) {
            this.field_74746_b = var2.func_74732_a();
         } else if (this.field_74746_b != var2.func_74732_a()) {
            field_179239_b.warn("Adding mismatching tag types to tag list");
            return (INBTBase)this.field_74747_a.get(var1);
         }

         return (INBTBase)this.field_74747_a.set(var1, var2);
      } else {
         field_179239_b.warn("index out of bounds to set tag in tag list");
         return null;
      }
   }

   public INBTBase remove(int var1) {
      return (INBTBase)this.field_74747_a.remove(var1);
   }

   public boolean isEmpty() {
      return this.field_74747_a.isEmpty();
   }

   public NBTTagCompound func_150305_b(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         INBTBase var2 = (INBTBase)this.field_74747_a.get(var1);
         if (var2.func_74732_a() == 10) {
            return (NBTTagCompound)var2;
         }
      }

      return new NBTTagCompound();
   }

   public NBTTagList func_202169_e(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         INBTBase var2 = (INBTBase)this.field_74747_a.get(var1);
         if (var2.func_74732_a() == 9) {
            return (NBTTagList)var2;
         }
      }

      return new NBTTagList();
   }

   public short func_202170_f(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         INBTBase var2 = (INBTBase)this.field_74747_a.get(var1);
         if (var2.func_74732_a() == 2) {
            return ((NBTTagShort)var2).func_150289_e();
         }
      }

      return 0;
   }

   public int func_186858_c(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         INBTBase var2 = (INBTBase)this.field_74747_a.get(var1);
         if (var2.func_74732_a() == 3) {
            return ((NBTTagInt)var2).func_150287_d();
         }
      }

      return 0;
   }

   public int[] func_150306_c(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         INBTBase var2 = (INBTBase)this.field_74747_a.get(var1);
         if (var2.func_74732_a() == 11) {
            return ((NBTTagIntArray)var2).func_150302_c();
         }
      }

      return new int[0];
   }

   public double func_150309_d(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         INBTBase var2 = (INBTBase)this.field_74747_a.get(var1);
         if (var2.func_74732_a() == 6) {
            return ((NBTTagDouble)var2).func_150286_g();
         }
      }

      return 0.0D;
   }

   public float func_150308_e(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         INBTBase var2 = (INBTBase)this.field_74747_a.get(var1);
         if (var2.func_74732_a() == 5) {
            return ((NBTTagFloat)var2).func_150288_h();
         }
      }

      return 0.0F;
   }

   public String func_150307_f(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         INBTBase var2 = (INBTBase)this.field_74747_a.get(var1);
         return var2.func_74732_a() == 8 ? var2.func_150285_a_() : var2.toString();
      } else {
         return "";
      }
   }

   public INBTBase get(int var1) {
      return (INBTBase)(var1 >= 0 && var1 < this.field_74747_a.size() ? (INBTBase)this.field_74747_a.get(var1) : new NBTTagEnd());
   }

   public int size() {
      return this.field_74747_a.size();
   }

   public INBTBase func_197647_c(int var1) {
      return (INBTBase)this.field_74747_a.get(var1);
   }

   public void func_197648_a(int var1, INBTBase var2) {
      this.field_74747_a.set(var1, var2);
   }

   public void func_197649_b(int var1) {
      this.field_74747_a.remove(var1);
   }

   public NBTTagList func_74737_b() {
      NBTTagList var1 = new NBTTagList();
      var1.field_74746_b = this.field_74746_b;
      Iterator var2 = this.field_74747_a.iterator();

      while(var2.hasNext()) {
         INBTBase var3 = (INBTBase)var2.next();
         INBTBase var4 = var3.func_74737_b();
         var1.field_74747_a.add(var4);
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 instanceof NBTTagList && Objects.equals(this.field_74747_a, ((NBTTagList)var1).field_74747_a);
      }
   }

   public int hashCode() {
      return this.field_74747_a.hashCode();
   }

   public ITextComponent func_199850_a(String var1, int var2) {
      if (this.isEmpty()) {
         return new TextComponentString("[]");
      } else {
         TextComponentString var3 = new TextComponentString("[");
         if (!var1.isEmpty()) {
            var3.func_150258_a("\n");
         }

         for(int var4 = 0; var4 < this.field_74747_a.size(); ++var4) {
            TextComponentString var5 = new TextComponentString(Strings.repeat(var1, var2 + 1));
            var5.func_150257_a(((INBTBase)this.field_74747_a.get(var4)).func_199850_a(var1, var2 + 1));
            if (var4 != this.field_74747_a.size() - 1) {
               var5.func_150258_a(String.valueOf(',')).func_150258_a(var1.isEmpty() ? " " : "\n");
            }

            var3.func_150257_a(var5);
         }

         if (!var1.isEmpty()) {
            var3.func_150258_a("\n").func_150258_a(Strings.repeat(var1, var2));
         }

         var3.func_150258_a("]");
         return var3;
      }
   }

   public int func_150303_d() {
      return this.field_74746_b;
   }

   // $FF: synthetic method
   public INBTBase func_74737_b() {
      return this.func_74737_b();
   }

   // $FF: synthetic method
   public Object remove(int var1) {
      return this.remove(var1);
   }

   // $FF: synthetic method
   public Object set(int var1, Object var2) {
      return this.set(var1, (INBTBase)var2);
   }

   // $FF: synthetic method
   public Object get(int var1) {
      return this.get(var1);
   }

   // $FF: synthetic method
   public boolean add(Object var1) {
      return this.add((INBTBase)var1);
   }
}
