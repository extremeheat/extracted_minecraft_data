package net.minecraft.server.dialog;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DialogTags;

public class Dialogs {
   public static final ResourceKey<Dialog> SERVER_LINKS = create("server_links");
   public static final ResourceKey<Dialog> CUSTOM_OPTIONS = create("custom_options");
   public static final int BIG_BUTTON_WIDTH = 310;

   public Dialogs() {
      super();
   }

   private static ResourceKey<Dialog> create(String var0) {
      return ResourceKey.create(Registries.DIALOG, ResourceLocation.withDefaultNamespace(var0));
   }

   public static void bootstrap(BootstrapContext<Dialog> var0) {
      HolderGetter var1 = var0.lookup(Registries.DIALOG);
      var0.register(SERVER_LINKS, new ServerLinksDialog(new CommonDialogData(Component.translatable("menu.server_links.title"), Optional.of(Component.translatable("menu.server_links")), true, List.of()), Optional.empty(), 1, 310));
      var0.register(CUSTOM_OPTIONS, new DialogListDialog(new CommonDialogData(Component.translatable("menu.custom_options.title"), Optional.of(Component.translatable("menu.custom_options")), true, List.of()), var1.getOrThrow(DialogTags.PAUSE_SCREEN_ADDITIONS), Optional.empty(), 1, 310));
   }
}
