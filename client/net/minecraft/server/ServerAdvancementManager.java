package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.PredicateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAdvancementManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).create();
   private AdvancementList advancements = new AdvancementList();
   private final PredicateManager predicateManager;

   public ServerAdvancementManager(PredicateManager var1) {
      super(GSON, "advancements");
      this.predicateManager = var1;
   }

   protected void apply(Map<ResourceLocation, JsonElement> var1, ResourceManager var2, ProfilerFiller var3) {
      HashMap var4 = Maps.newHashMap();
      var1.forEach((var2x, var3x) -> {
         try {
            JsonObject var4x = GsonHelper.convertToJsonObject(var3x, "advancement");
            Advancement.Builder var5 = Advancement.Builder.fromJson(var4x, new DeserializationContext(var2x, this.predicateManager));
            var4.put(var2x, var5);
         } catch (IllegalArgumentException | JsonParseException var6) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", var2x, var6.getMessage());
         }

      });
      AdvancementList var5 = new AdvancementList();
      var5.add(var4);
      Iterator var6 = var5.getRoots().iterator();

      while(var6.hasNext()) {
         Advancement var7 = (Advancement)var6.next();
         if (var7.getDisplay() != null) {
            TreeNodePosition.run(var7);
         }
      }

      this.advancements = var5;
   }

   @Nullable
   public Advancement getAdvancement(ResourceLocation var1) {
      return this.advancements.get(var1);
   }

   public Collection<Advancement> getAllAdvancements() {
      return this.advancements.getAllAdvancements();
   }
}
