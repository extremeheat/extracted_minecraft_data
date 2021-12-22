package net.minecraft.server.packs.repository;

import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;

public class ServerPacksSource implements RepositorySource {
   public static final PackMetadataSection BUILT_IN_METADATA;
   public static final String VANILLA_ID = "vanilla";
   private final VanillaPackResources vanillaPack;

   public ServerPacksSource() {
      super();
      this.vanillaPack = new VanillaPackResources(BUILT_IN_METADATA, new String[]{"minecraft"});
   }

   public void loadPacks(Consumer<Pack> var1, Pack.PackConstructor var2) {
      Pack var3 = Pack.create("vanilla", false, () -> {
         return this.vanillaPack;
      }, var2, Pack.Position.BOTTOM, PackSource.BUILT_IN);
      if (var3 != null) {
         var1.accept(var3);
      }

   }

   static {
      BUILT_IN_METADATA = new PackMetadataSection(new TranslatableComponent("dataPack.vanilla.description"), PackType.SERVER_DATA.getVersion(SharedConstants.getCurrentVersion()));
   }
}
