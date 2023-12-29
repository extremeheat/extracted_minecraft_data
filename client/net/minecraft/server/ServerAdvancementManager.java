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
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.LootDataManager;
import org.slf4j.Logger;

public class ServerAdvancementManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder().create();
   private Map<ResourceLocation, AdvancementHolder> advancements = Map.of();
   private AdvancementTree tree = new AdvancementTree();
   private final LootDataManager lootData;

   public ServerAdvancementManager(LootDataManager var1) {
      super(GSON, "advancements");
      this.lootData = var1;
   }

   protected void apply(Map<ResourceLocation, JsonElement> var1, ResourceManager var2, ProfilerFiller var3) {
      Builder var4 = ImmutableMap.builder();
      var1.forEach((var2x, var3x) -> {
         try {
            Advancement var4x = Util.getOrThrow(Advancement.CODEC.parse(JsonOps.INSTANCE, var3x), JsonParseException::new);
            this.validate(var2x, var4x);
            var4.put(var2x, new AdvancementHolder(var2x, var4x));
         } catch (Exception var5x) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", var2x, var5x.getMessage());
         }
      });
      this.advancements = var4.buildOrThrow();
      AdvancementTree var5 = new AdvancementTree();
      var5.addAll(this.advancements.values());

      for(AdvancementNode var7 : var5.roots()) {
         if (var7.holder().value().display().isPresent()) {
            TreeNodePosition.run(var7);
         }
      }

      this.tree = var5;
   }

   private void validate(ResourceLocation var1, Advancement var2) {
      ProblemReporter.Collector var3 = new ProblemReporter.Collector();
      var2.validate(var3, this.lootData);
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
