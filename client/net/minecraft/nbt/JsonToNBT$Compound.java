package net.minecraft.nbt;

import java.util.ArrayList;

class JsonToNBT$Compound extends JsonToNBT$Any {
   protected ArrayList field_150491_b = new ArrayList();

   public JsonToNBT$Compound(String var1) {
      super();
      this.field_150490_a = var1;
   }

   @Override
   public NBTBase func_150489_a() {
      NBTTagCompound var1 = new NBTTagCompound();

      for(JsonToNBT$Any var3 : this.field_150491_b) {
         var1.func_74782_a(var3.field_150490_a, var3.func_150489_a());
      }

      return var1;
   }
}
