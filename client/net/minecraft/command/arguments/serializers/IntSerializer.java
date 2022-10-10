package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class IntSerializer implements IArgumentSerializer<IntegerArgumentType> {
   public IntSerializer() {
      super();
   }

   public void func_197072_a(IntegerArgumentType var1, PacketBuffer var2) {
      boolean var3 = var1.getMinimum() != -2147483648;
      boolean var4 = var1.getMaximum() != 2147483647;
      var2.writeByte(BrigadierSerializers.func_197508_a(var3, var4));
      if (var3) {
         var2.writeInt(var1.getMinimum());
      }

      if (var4) {
         var2.writeInt(var1.getMaximum());
      }

   }

   public IntegerArgumentType func_197071_b(PacketBuffer var1) {
      byte var2 = var1.readByte();
      int var3 = BrigadierSerializers.func_197510_a(var2) ? var1.readInt() : -2147483648;
      int var4 = BrigadierSerializers.func_197509_b(var2) ? var1.readInt() : 2147483647;
      return IntegerArgumentType.integer(var3, var4);
   }

   public void func_212244_a(IntegerArgumentType var1, JsonObject var2) {
      if (var1.getMinimum() != -2147483648) {
         var2.addProperty("min", var1.getMinimum());
      }

      if (var1.getMaximum() != 2147483647) {
         var2.addProperty("max", var1.getMaximum());
      }

   }

   // $FF: synthetic method
   public ArgumentType func_197071_b(PacketBuffer var1) {
      return this.func_197071_b(var1);
   }
}
