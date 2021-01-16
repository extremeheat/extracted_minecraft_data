package net.minecraft.commands.synchronization.brigadier;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;

public class BrigadierArgumentSerializers {
   public static void bootstrap() {
      ArgumentTypes.register("brigadier:bool", BoolArgumentType.class, new EmptyArgumentSerializer(BoolArgumentType::bool));
      ArgumentTypes.register("brigadier:float", FloatArgumentType.class, new FloatArgumentSerializer());
      ArgumentTypes.register("brigadier:double", DoubleArgumentType.class, new DoubleArgumentSerializer());
      ArgumentTypes.register("brigadier:integer", IntegerArgumentType.class, new IntegerArgumentSerializer());
      ArgumentTypes.register("brigadier:long", LongArgumentType.class, new LongArgumentSerializer());
      ArgumentTypes.register("brigadier:string", StringArgumentType.class, new StringArgumentSerializer());
   }

   public static byte createNumberFlags(boolean var0, boolean var1) {
      byte var2 = 0;
      if (var0) {
         var2 = (byte)(var2 | 1);
      }

      if (var1) {
         var2 = (byte)(var2 | 2);
      }

      return var2;
   }

   public static boolean numberHasMin(byte var0) {
      return (var0 & 1) != 0;
   }

   public static boolean numberHasMax(byte var0) {
      return (var0 & 2) != 0;
   }
}
