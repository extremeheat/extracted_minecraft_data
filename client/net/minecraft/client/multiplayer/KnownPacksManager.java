package net.minecraft.client.multiplayer;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.ServerPacksSource;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;

public class KnownPacksManager {
   private final PackRepository repository = ServerPacksSource.createVanillaTrustedRepository();
   private final Map<KnownPack, String> knownPackToId;

   public KnownPacksManager() {
      super();
      this.repository.reload();
      ImmutableMap.Builder var1 = ImmutableMap.builder();
      this.repository.getAvailablePacks().forEach((var1x) -> {
         PackLocationInfo var2 = var1x.location();
         var2.knownPackInfo().ifPresent((var2x) -> {
            var1.put(var2x, var2.id());
         });
      });
      this.knownPackToId = var1.build();
   }

   public List<KnownPack> trySelectingPacks(List<KnownPack> var1) {
      ArrayList var2 = new ArrayList(var1.size());
      ArrayList var3 = new ArrayList(var1.size());
      Iterator var4 = var1.iterator();

      while(var4.hasNext()) {
         KnownPack var5 = (KnownPack)var4.next();
         String var6 = (String)this.knownPackToId.get(var5);
         if (var6 != null) {
            var3.add(var6);
            var2.add(var5);
         }
      }

      this.repository.setSelected(var3);
      return var2;
   }

   public CloseableResourceManager createResourceManager() {
      List var1 = this.repository.openAllSelected();
      return new MultiPackResourceManager(PackType.SERVER_DATA, var1);
   }
}
