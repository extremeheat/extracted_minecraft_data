package net.minecraft.client;

import net.minecraft.client.core.registries.BuiltInClientRegistries;
import net.minecraft.client.gui.screens.dialog.DialogScreens;
import net.minecraft.client.gui.screens.dialog.body.DialogBodyHandlers;
import net.minecraft.client.gui.screens.dialog.input.InputControlHandlers;

public class ClientBootstrap {
   private static volatile boolean isBootstrapped;

   public ClientBootstrap() {
      super();
   }

   public static void bootstrap() {
      if (!isBootstrapped) {
         isBootstrapped = true;
         DialogScreens.bootstrap();
         InputControlHandlers.bootstrap();
         DialogBodyHandlers.bootstrap();
         BuiltInClientRegistries.bootStrap();
      }
   }
}
