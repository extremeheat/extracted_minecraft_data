package net.minecraft.command.arguments.serializers;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.network.PacketBuffer;

public class FloatSerializer implements IArgumentSerializer<FloatArgumentType> {
   public FloatSerializer() {
      super();
   }

   public void func_197072_a(FloatArgumentType var1, PacketBuffer var2) {
      boolean var3 = var1.getMinimum() != -3.4028235E38F;
      boolean var4 = var1.getMaximum() != 3.4028235E38F;
      var2.writeByte(BrigadierSerializers.func_197508_a(var3, var4));
      if (var3) {
         var2.writeFloat(var1.getMinimum());
      }

      if (var4) {
         var2.writeFloat(var1.getMaximum());
      }

   }

   public FloatArgumentType func_197071_b(PacketBuffer var1) {
      byte var2 = var1.readByte();
      float var3 = BrigadierSerializers.func_197510_a(var2) ? var1.readFloat() : -3.4028235E38F;
      float var4 = BrigadierSerializers.func_197509_b(var2) ? var1.readFloat() : 3.4028235E38F;
      return FloatArgumentType.floatArg(var3, var4);
   }

   public void func_212244_a(FloatArgumentType var1, JsonObject var2) {
      if (var1.getMinimum() != -3.4028235E38F) {
         var2.addProperty("min", var1.getMinimum());
      }

      if (var1.getMaximum() != 3.4028235E38F) {
         var2.addProperty("max", var1.getMaximum());
      }

   }

   // $FF: synthetic method
   public ArgumentType func_197071_b(PacketBuffer var1) {
      return this.func_197071_b(var1);
   }
}
