package net.minecraft.network;

import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {
   public static <T extends INetHandler> void func_180031_a(Packet<T> var0, T var1, IThreadListener var2) throws ThreadQuickExitException {
      if (!var2.func_152345_ab()) {
         var2.func_152344_a(() -> {
            var0.func_148833_a(var1);
         });
         throw ThreadQuickExitException.field_179886_a;
      }
   }
}
