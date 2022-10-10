package net.minecraft.command.arguments.serializers;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.util.ResourceLocation;

public class BrigadierSerializers {
   public static void func_197511_a() {
      ArgumentTypes.func_197487_a(new ResourceLocation("brigadier:bool"), BoolArgumentType.class, new ArgumentSerializer(BoolArgumentType::bool));
      ArgumentTypes.func_197487_a(new ResourceLocation("brigadier:float"), FloatArgumentType.class, new FloatSerializer());
      ArgumentTypes.func_197487_a(new ResourceLocation("brigadier:double"), DoubleArgumentType.class, new DoubleSerializer());
      ArgumentTypes.func_197487_a(new ResourceLocation("brigadier:integer"), IntegerArgumentType.class, new IntSerializer());
      ArgumentTypes.func_197487_a(new ResourceLocation("brigadier:string"), StringArgumentType.class, new StringSerializer());
   }

   public static byte func_197508_a(boolean var0, boolean var1) {
      byte var2 = 0;
      if (var0) {
         var2 = (byte)(var2 | 1);
      }

      if (var1) {
         var2 = (byte)(var2 | 2);
      }

      return var2;
   }

   public static boolean func_197510_a(byte var0) {
      return (var0 & 1) != 0;
   }

   public static boolean func_197509_b(byte var0) {
      return (var0 & 2) != 0;
   }
}
