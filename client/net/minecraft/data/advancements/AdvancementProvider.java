package net.minecraft.data.advancements;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class AdvancementProvider implements DataProvider {
   private final PackOutput.PathProvider pathProvider;
   private final List<AdvancementSubProvider> subProviders;
   private final CompletableFuture<HolderLookup.Provider> registries;

   public AdvancementProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2, List<AdvancementSubProvider> var3) {
      super();
      this.pathProvider = var1.createRegistryElementsPathProvider(Registries.ADVANCEMENT);
      this.subProviders = var3;
      this.registries = var2;
   }

   public CompletableFuture<?> run(CachedOutput var1) {
      return this.registries.thenCompose((var2) -> {
         HashSet var3 = new HashSet();
         ArrayList var4 = new ArrayList();
         Consumer var5 = (var5x) -> {
            if (!var3.add(var5x.id())) {
               throw new IllegalStateException("Duplicate advancement " + String.valueOf(var5x.id()));
            } else {
               Path var6 = this.pathProvider.json(var5x.id());
               var4.add(DataProvider.saveStable(var1, var2, Advancement.CODEC, var5x.value(), var6));
            }
         };
         Iterator var6 = this.subProviders.iterator();

         while(var6.hasNext()) {
            AdvancementSubProvider var7 = (AdvancementSubProvider)var6.next();
            var7.generate(var2, var5);
         }

         return CompletableFuture.allOf((CompletableFuture[])var4.toArray((var0) -> {
            return new CompletableFuture[var0];
         }));
      });
   }

   public final String getName() {
      return "Advancements";
   }
}
