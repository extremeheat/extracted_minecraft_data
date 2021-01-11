package net.minecraft.nbt;

import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTBase {
   private static final Logger field_179239_b = LogManager.getLogger();
   private List<NBTBase> field_74747_a = Lists.newArrayList();
   private byte field_74746_b = 0;

   public NBTTagList() {
      super();
   }

   void func_74734_a(DataOutput var1) throws IOException {
      if (!this.field_74747_a.isEmpty()) {
         this.field_74746_b = ((NBTBase)this.field_74747_a.get(0)).func_74732_a();
      } else {
         this.field_74746_b = 0;
      }

      var1.writeByte(this.field_74746_b);
      var1.writeInt(this.field_74747_a.size());

      for(int var2 = 0; var2 < this.field_74747_a.size(); ++var2) {
         ((NBTBase)this.field_74747_a.get(var2)).func_74734_a(var1);
      }

   }

   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) throws IOException {
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
               NBTBase var6 = NBTBase.func_150284_a(this.field_74746_b);
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

         var1.append(var2).append(':').append(this.field_74747_a.get(var2));
      }

      return var1.append(']').toString();
   }

   public void func_74742_a(NBTBase var1) {
      if (var1.func_74732_a() == 0) {
         field_179239_b.warn("Invalid TagEnd added to ListTag");
      } else {
         if (this.field_74746_b == 0) {
            this.field_74746_b = var1.func_74732_a();
         } else if (this.field_74746_b != var1.func_74732_a()) {
            field_179239_b.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.field_74747_a.add(var1);
      }
   }

   public void func_150304_a(int var1, NBTBase var2) {
      if (var2.func_74732_a() == 0) {
         field_179239_b.warn("Invalid TagEnd added to ListTag");
      } else if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         if (this.field_74746_b == 0) {
            this.field_74746_b = var2.func_74732_a();
         } else if (this.field_74746_b != var2.func_74732_a()) {
            field_179239_b.warn("Adding mismatching tag types to tag list");
            return;
         }

         this.field_74747_a.set(var1, var2);
      } else {
         field_179239_b.warn("index out of bounds to set tag in tag list");
      }
   }

   public NBTBase func_74744_a(int var1) {
      return (NBTBase)this.field_74747_a.remove(var1);
   }

   public boolean func_82582_d() {
      return this.field_74747_a.isEmpty();
   }

   public NBTTagCompound func_150305_b(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         NBTBase var2 = (NBTBase)this.field_74747_a.get(var1);
         return var2.func_74732_a() == 10 ? (NBTTagCompound)var2 : new NBTTagCompound();
      } else {
         return new NBTTagCompound();
      }
   }

   public int[] func_150306_c(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         NBTBase var2 = (NBTBase)this.field_74747_a.get(var1);
         return var2.func_74732_a() == 11 ? ((NBTTagIntArray)var2).func_150302_c() : new int[0];
      } else {
         return new int[0];
      }
   }

   public double func_150309_d(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         NBTBase var2 = (NBTBase)this.field_74747_a.get(var1);
         return var2.func_74732_a() == 6 ? ((NBTTagDouble)var2).func_150286_g() : 0.0D;
      } else {
         return 0.0D;
      }
   }

   public float func_150308_e(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         NBTBase var2 = (NBTBase)this.field_74747_a.get(var1);
         return var2.func_74732_a() == 5 ? ((NBTTagFloat)var2).func_150288_h() : 0.0F;
      } else {
         return 0.0F;
      }
   }

   public String func_150307_f(int var1) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         NBTBase var2 = (NBTBase)this.field_74747_a.get(var1);
         return var2.func_74732_a() == 8 ? var2.func_150285_a_() : var2.toString();
      } else {
         return "";
      }
   }

   public NBTBase func_179238_g(int var1) {
      return (NBTBase)(var1 >= 0 && var1 < this.field_74747_a.size() ? (NBTBase)this.field_74747_a.get(var1) : new NBTTagEnd());
   }

   public int func_74745_c() {
      return this.field_74747_a.size();
   }

   public NBTBase func_74737_b() {
      NBTTagList var1 = new NBTTagList();
      var1.field_74746_b = this.field_74746_b;
      Iterator var2 = this.field_74747_a.iterator();

      while(var2.hasNext()) {
         NBTBase var3 = (NBTBase)var2.next();
         NBTBase var4 = var3.func_74737_b();
         var1.field_74747_a.add(var4);
      }

      return var1;
   }

   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagList var2 = (NBTTagList)var1;
         if (this.field_74746_b == var2.field_74746_b) {
            return this.field_74747_a.equals(var2.field_74747_a);
         }
      }

      return false;
   }

   public int hashCode() {
      return super.hashCode() ^ this.field_74747_a.hashCode();
   }

   public int func_150303_d() {
      return this.field_74746_b;
   }
}
