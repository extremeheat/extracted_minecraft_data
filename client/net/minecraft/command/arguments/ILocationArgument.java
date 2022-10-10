package net.minecraft.command.arguments;

import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public interface ILocationArgument {
   Vec3d func_197281_a(CommandSource var1);

   Vec2f func_197282_b(CommandSource var1);

   default BlockPos func_197280_c(CommandSource var1) {
      return new BlockPos(this.func_197281_a(var1));
   }

   boolean func_200380_a();

   boolean func_200381_b();

   boolean func_200382_c();
}
