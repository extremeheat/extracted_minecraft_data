package net.minecraft.nbt;

import java.util.ArrayList;

class JsonToNBT$List extends JsonToNBT$Any {
   protected ArrayList field_150492_b = new ArrayList();

   public JsonToNBT$List(String var1) {
      super();
      this.field_150490_a = var1;
   }

   @Override
   public NBTBase func_150489_a() {
      NBTTagList var1 = new NBTTagList();

      for(JsonToNBT$Any var3 : this.field_150492_b) {
         var1.func_74742_a(var3.func_150489_a());
      }

      return var1;
   }
}
