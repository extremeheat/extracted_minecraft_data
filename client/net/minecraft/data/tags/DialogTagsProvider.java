package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.tags.DialogTags;

public class DialogTagsProvider extends KeyTagProvider<Dialog> {
   public DialogTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2) {
      super(var1, Registries.DIALOG, var2);
   }

   protected void addTags(HolderLookup.Provider var1) {
      this.tag(DialogTags.PAUSE_SCREEN_ADDITIONS);
      this.tag(DialogTags.QUICK_ACTIONS);
   }
}
