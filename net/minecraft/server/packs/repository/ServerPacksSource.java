package net.minecraft.server.packs.repository;

import java.util.Map;
import net.minecraft.server.packs.VanillaPack;

public class ServerPacksSource implements RepositorySource {
   private final VanillaPack vanillaPack = new VanillaPack(new String[]{"minecraft"});

   public void loadPacks(Map var1, UnopenedPack.UnopenedPackConstructor var2) {
      UnopenedPack var3 = UnopenedPack.create("vanilla", false, () -> {
         return this.vanillaPack;
      }, var2, UnopenedPack.Position.BOTTOM);
      if (var3 != null) {
         var1.put("vanilla", var3);
      }

   }
}
