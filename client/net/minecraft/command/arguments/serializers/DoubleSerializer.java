package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class DoubleSerializer implements IArgumentSerializer<DoubleArgumentType> {
   public DoubleSerializer() {
      super();
   }

   public void func_197072_a(DoubleArgumentType var1, PacketBuffer var2) {
      boolean var3 = var1.getMinimum() != -1.7976931348623157E308D;
      boolean var4 = var1.getMaximum() != 1.7976931348623157E308D;
      var2.writeByte(BrigadierSerializers.func_197508_a(var3, var4));
      if (var3) {
         var2.writeDouble(var1.getMinimum());
      }

      if (var4) {
         var2.writeDouble(var1.getMaximum());
      }

   }

   public DoubleArgumentType func_197071_b(PacketBuffer var1) {
      byte var2 = var1.readByte();
      double var3 = BrigadierSerializers.func_197510_a(var2) ? var1.readDouble() : -1.7976931348623157E308D;
      double var5 = BrigadierSerializers.func_197509_b(var2) ? var1.readDouble() : 1.7976931348623157E308D;
      return DoubleArgumentType.doubleArg(var3, var5);
   }

   public void func_212244_a(DoubleArgumentType var1, JsonObject var2) {
      if (var1.getMinimum() != -1.7976931348623157E308D) {
         var2.addProperty("min", var1.getMinimum());
      }

      if (var1.getMaximum() != 1.7976931348623157E308D) {
         var2.addProperty("max", var1.getMaximum());
      }

   }

   // $FF: synthetic method
   public ArgumentType func_197071_b(PacketBuffer var1) {
      return this.func_197071_b(var1);
   }
}
