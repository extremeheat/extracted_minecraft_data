package net.minecraft.data.advancements.packs;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;

public class UpdateOneTwentyVanillaAdvancementProvider {
   public UpdateOneTwentyVanillaAdvancementProvider() {
      super();
   }

   public static AdvancementProvider create(PackOutput var0, CompletableFuture<HolderLookup.Provider> var1) {
      return new AdvancementProvider(var0, var1, List.of(new UpdateOneTwentyAdventureAdvancements(), new UpdateOneTwentyHusbandryAdvancements()));
   }
}
