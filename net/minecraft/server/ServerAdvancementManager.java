package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerAdvancementManager extends SimpleJsonResourceReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(Advancement.Builder.class, (var0, var1, var2) -> {
      JsonObject var3 = GsonHelper.convertToJsonObject(var0, "advancement");
      return Advancement.Builder.fromJson(var3, var2);
   }).registerTypeAdapter(AdvancementRewards.class, new AdvancementRewards.Deserializer()).registerTypeHierarchyAdapter(Component.class, new Component.Serializer()).registerTypeHierarchyAdapter(Style.class, new Style.Serializer()).registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory()).create();
   private AdvancementList advancements = new AdvancementList();

   public ServerAdvancementManager() {
      super(GSON, "advancements");
   }

   protected void apply(Map var1, ResourceManager var2, ProfilerFiller var3) {
      HashMap var4 = Maps.newHashMap();
      var1.forEach((var1x, var2x) -> {
         try {
            Advancement.Builder var3 = (Advancement.Builder)GSON.fromJson(var2x, Advancement.Builder.class);
            var4.put(var1x, var3);
         } catch (IllegalArgumentException | JsonParseException var4x) {
            LOGGER.error("Parsing error loading custom advancement {}: {}", var1x, var4x.getMessage());
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

   public Collection getAllAdvancements() {
      return this.advancements.getAllAdvancements();
   }
}
