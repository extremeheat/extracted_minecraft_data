package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
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
            JsonObject var4x = GsonHelper.convertToJsonObject(var3x, "advancement");
            Advancement var5x = Advancement.fromJson(var4x, new DeserializationContext(var2x, this.lootData));
            var4.put(var2x, new AdvancementHolder(var2x, var5x));
         } catch (Exception var6) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", var2x, var6.getMessage());
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
