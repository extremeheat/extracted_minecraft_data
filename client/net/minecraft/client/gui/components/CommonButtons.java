package net.minecraft.client.gui.components;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class CommonButtons {
   public CommonButtons() {
      super();
   }

   public static SpriteIconButton language(int var0, Button.OnPress var1, boolean var2) {
      return SpriteIconButton.builder(Component.translatable("options.language"), var1, var2).width(var0).sprite(ResourceLocation.withDefaultNamespace("icon/language"), 15, 15).build();
   }

   public static SpriteIconButton accessibility(int var0, Button.OnPress var1, boolean var2) {
      MutableComponent var3 = var2 ? Component.translatable("options.accessibility") : Component.translatable("accessibility.onboarding.accessibility.button");
      return SpriteIconButton.builder(var3, var1, var2).width(var0).sprite(ResourceLocation.withDefaultNamespace("icon/accessibility"), 15, 15).build();
   }
}
