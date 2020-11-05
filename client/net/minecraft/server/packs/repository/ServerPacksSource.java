package net.minecraft.server.packs.repository;

import java.util.function.Consumer;
import net.minecraft.server.packs.VanillaPackResources;

public class ServerPacksSource implements RepositorySource {
   private final VanillaPackResources vanillaPack = new VanillaPackResources(new String[]{"minecraft"});

   public ServerPacksSource() {
      super();
   }

   public void loadPacks(Consumer<Pack> var1, Pack.PackConstructor var2) {
      Pack var3 = Pack.create("vanilla", false, () -> {
         return this.vanillaPack;
      }, var2, Pack.Position.BOTTOM, PackSource.BUILT_IN);
      if (var3 != null) {
         var1.accept(var3);
      }

   }
}
