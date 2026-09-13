package net.minecraft.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.util.ArrayList;
import java.util.List;

public class NBTTagList extends NBTBase {
   private List field_74747_a = new ArrayList();
   private byte field_74746_b = 0;

   public NBTTagList() {
      super();
   }

   @Override
   void func_74734_a(DataOutput var1) {
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

   @Override
   void func_152446_a(DataInput var1, int var2, NBTSizeTracker var3) {
      if (var2 > 512) {
         throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
      } else {
         var3.func_152450_a(8L);
         this.field_74746_b = var1.readByte();
         int var4 = var1.readInt();
         this.field_74747_a = new ArrayList();

         for(int var5 = 0; var5 < var4; ++var5) {
            NBTBase var6 = NBTBase.func_150284_a(this.field_74746_b);
            var6.func_152446_a(var1, var2 + 1, var3);
            this.field_74747_a.add(var6);
         }
      }
   }

   @Override
   public byte func_74732_a() {
      return 9;
   }

   @Override
   public String toString() {
      String var1 = "[";
      int var2 = 0;

      for(NBTBase var4 : this.field_74747_a) {
         var1 = var1 + "" + var2 + ':' + var4 + ',';
         ++var2;
      }

      return var1 + "]";
   }

   public void func_74742_a(NBTBase var1) {
      if (this.field_74746_b == 0) {
         this.field_74746_b = var1.func_74732_a();
      } else if (this.field_74746_b != var1.func_74732_a()) {
         System.err.println("WARNING: Adding mismatching tag types to tag list");
         return;
      }

      this.field_74747_a.add(var1);
   }

   public void func_150304_a(int var1, NBTBase var2) {
      if (var1 >= 0 && var1 < this.field_74747_a.size()) {
         if (this.field_74746_b == 0) {
            this.field_74746_b = var2.func_74732_a();
         } else if (this.field_74746_b != var2.func_74732_a()) {
            System.err.println("WARNING: Adding mismatching tag types to tag list");
            return;
         }

         this.field_74747_a.set(var1, var2);
      } else {
         System.err.println("WARNING: index out of bounds to set tag in tag list");
      }
   }

   public NBTBase func_74744_a(int var1) {
      return (NBTBase)this.field_74747_a.remove(var1);
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
         return var2.func_74732_a() == 6 ? ((NBTTagDouble)var2).func_150286_g() : 0.0;
      } else {
         return 0.0;
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

   public int func_74745_c() {
      return this.field_74747_a.size();
   }

   @Override
   public NBTBase func_74737_b() {
      NBTTagList var1 = new NBTTagList();
      var1.field_74746_b = this.field_74746_b;

      for(NBTBase var3 : this.field_74747_a) {
         NBTBase var4 = var3.func_74737_b();
         var1.field_74747_a.add(var4);
      }

      return var1;
   }

   @Override
   public boolean equals(Object var1) {
      if (super.equals(var1)) {
         NBTTagList var2 = (NBTTagList)var1;
         if (this.field_74746_b == var2.field_74746_b) {
            return this.field_74747_a.equals(var2.field_74747_a);
         }
      }

      return false;
   }

   @Override
   public int hashCode() {
      return super.hashCode() ^ this.field_74747_a.hashCode();
   }

   public int func_150303_d() {
      return this.field_74746_b;
   }
}
