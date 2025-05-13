package net.minecraft.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dialog.Dialog;

public class DialogTags {
   public static final TagKey<Dialog> PAUSE_SCREEN_ADDITIONS = create("pause_screen_additions");

   private DialogTags() {
      super();
   }

   private static TagKey<Dialog> create(String var0) {
      return TagKey.<Dialog>create(Registries.DIALOG, ResourceLocation.withDefaultNamespace(var0));
   }
}
