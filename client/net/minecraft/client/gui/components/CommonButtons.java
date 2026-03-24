package net.minecraft.client.gui.components;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class CommonButtons {
   public CommonButtons() {
      super();
   }

   public static SpriteIconButton language(final int width, final Button.OnPress onPress, final boolean iconOnly) {
      return SpriteIconButton.builder(Component.translatable("options.language"), onPress, iconOnly).width(width).sprite((Identifier)Identifier.withDefaultNamespace("icon/language"), 15, 15).build();
   }

   public static SpriteIconButton accessibility(final int width, final Button.OnPress onPress, final boolean iconOnly) {
      Component text = iconOnly ? Component.translatable("options.accessibility") : Component.translatable("accessibility.onboarding.accessibility.button");
      return SpriteIconButton.builder(text, onPress, iconOnly).width(width).sprite((Identifier)Identifier.withDefaultNamespace("icon/accessibility"), 15, 15).build();
   }
}
