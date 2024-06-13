package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ServerAdvancementManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder().create();
   private Map<ResourceLocation, AdvancementHolder> advancements = Map.of();
   private AdvancementTree tree = new AdvancementTree();
   private final HolderLookup.Provider registries;

   public ServerAdvancementManager(HolderLookup.Provider var1) {
      super(GSON, "advancements");
      this.registries = var1;
   }

   protected void apply(Map<ResourceLocation, JsonElement> var1, ResourceManager var2, ProfilerFiller var3) {
      RegistryOps var4 = this.registries.createSerializationContext(JsonOps.INSTANCE);
      Builder var5 = ImmutableMap.builder();
      var1.forEach((var3x, var4x) -> {
         try {
            Advancement var5x = (Advancement)Advancement.CODEC.parse(var4, var4x).getOrThrow(JsonParseException::new);
            this.validate(var3x, var5x);
            var5.put(var3x, new AdvancementHolder(var3x, var5x));
         } catch (Exception var6x) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", var3x, var6x.getMessage());
         }
      });
      this.advancements = var5.buildOrThrow();
      AdvancementTree var6 = new AdvancementTree();
      var6.addAll(this.advancements.values());

      for (AdvancementNode var8 : var6.roots()) {
         if (var8.holder().value().display().isPresent()) {
            TreeNodePosition.run(var8);
         }
      }

      this.tree = var6;
   }

   private void validate(ResourceLocation var1, Advancement var2) {
      ProblemReporter.Collector var3 = new ProblemReporter.Collector();
      var2.validate(var3, this.registries.asGetterLookup());
      Multimap var4 = var3.get();
      if (!var4.isEmpty()) {
         String var5 = var4.asMap()
            .entrySet()
            .stream()
            .map(var0 -> "  at " + (String)var0.getKey() + ": " + String.join("; ", (Iterable<? extends CharSequence>)var0.getValue()))
            .collect(Collectors.joining("\n"));
         LOGGER.warn("Found validation problems in advancement {}: \n{}", var1, var5);
      }
   }

   @Nullable
   public AdvancementHolder get(ResourceLocation var1) {
      return this.advancements.get(var1);
   }

   public AdvancementTree tree() {
      return this.tree;
   }

   public Collection<AdvancementHolder> getAllAdvancements() {
      return this.advancements.values();
   }
}
