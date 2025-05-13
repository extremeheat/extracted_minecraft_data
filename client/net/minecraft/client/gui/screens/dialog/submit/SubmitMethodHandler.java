package net.minecraft.client.gui.screens.dialog.submit;

import java.util.Map;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.dialog.submit.SubmitMethod;

@FunctionalInterface
public interface SubmitMethodHandler<T extends SubmitMethod> {
   Callback createCallback(T var1);

   @FunctionalInterface
   public interface Callback {
      Callback NOP = (var0, var1) -> {
      };

      void run(ClientPacketListener var1, Map<String, String> var2);
   }
}
