package net.minecraft.data.metadata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.DetectedVersion;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlagSet;

public class PackMetadataGenerator implements DataProvider {
   private final PackOutput output;
   private final Map<String, Supplier<JsonElement>> elements = new HashMap();

   public PackMetadataGenerator(PackOutput var1) {
      super();
      this.output = var1;
   }

   public <T> PackMetadataGenerator add(MetadataSectionType<T> var1, T var2) {
      this.elements.put(var1.getMetadataSectionName(), () -> {
         return var1.toJson(var2);
      });
      return this;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      JsonObject var2 = new JsonObject();
      this.elements.forEach((var1x, var2x) -> {
         var2.add(var1x, (JsonElement)var2x.get());
      });
      return DataProvider.saveStable(var1, var2, this.output.getOutputFolder().resolve("pack.mcmeta"));
   }

   public final String getName() {
      return "Pack Metadata";
   }

   public static PackMetadataGenerator forFeaturePack(PackOutput var0, Component var1) {
      return (new PackMetadataGenerator(var0)).add(PackMetadataSection.TYPE, new PackMetadataSection(var1, DetectedVersion.BUILT_IN.getPackVersion(PackType.SERVER_DATA), Optional.empty()));
   }

   public static PackMetadataGenerator forFeaturePack(PackOutput var0, Component var1, FeatureFlagSet var2) {
      return forFeaturePack(var0, var1).add(FeatureFlagsMetadataSection.TYPE, new FeatureFlagsMetadataSection(var2));
   }
}
