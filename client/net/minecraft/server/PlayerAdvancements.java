package net.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;

public class PlayerAdvancements {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = new GsonBuilder()
      .registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer())
      .registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer())
      .setPrettyPrinting()
      .create();
   private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> TYPE_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>() {
   };
   private final DataFixer dataFixer;
   private final PlayerList playerList;
   private final Path playerSavePath;
   private final Map<Advancement, AdvancementProgress> progress = new LinkedHashMap<>();
   private final Set<Advancement> visible = new HashSet<>();
   private final Set<Advancement> progressChanged = new HashSet<>();
   private final Set<Advancement> rootsToUpdate = new HashSet<>();
   private ServerPlayer player;
   @Nullable
   private Advancement lastSelectedTab;
   private boolean isFirstPacket = true;

   public PlayerAdvancements(DataFixer var1, PlayerList var2, ServerAdvancementManager var3, Path var4, ServerPlayer var5) {
      super();
      this.dataFixer = var1;
      this.playerList = var2;
      this.playerSavePath = var4;
      this.player = var5;
      this.load(var3);
   }

   public void setPlayer(ServerPlayer var1) {
      this.player = var1;
   }

   public void stopListening() {
      for(CriterionTrigger var2 : CriteriaTriggers.all()) {
         var2.removePlayerListeners(this);
      }
   }

   public void reload(ServerAdvancementManager var1) {
      this.stopListening();
      this.progress.clear();
      this.visible.clear();
      this.rootsToUpdate.clear();
      this.progressChanged.clear();
      this.isFirstPacket = true;
      this.lastSelectedTab = null;
      this.load(var1);
   }

   private void registerListeners(ServerAdvancementManager var1) {
      for(Advancement var3 : var1.getAllAdvancements()) {
         this.registerListeners(var3);
      }
   }

   private void checkForAutomaticTriggers(ServerAdvancementManager var1) {
      for(Advancement var3 : var1.getAllAdvancements()) {
         if (var3.getCriteria().isEmpty()) {
            this.award(var3, "");
            var3.getRewards().grant(this.player);
         }
      }
   }

   private void load(ServerAdvancementManager var1) {
      if (Files.isRegularFile(this.playerSavePath)) {
         try {
            JsonReader var2 = new JsonReader(Files.newBufferedReader(this.playerSavePath, StandardCharsets.UTF_8));

            try {
               var2.setLenient(false);
               Dynamic var3 = new Dynamic(JsonOps.INSTANCE, Streams.parse(var2));
               int var4 = var3.get("DataVersion").asInt(1343);
               var3 = var3.remove("DataVersion");
               var3 = DataFixTypes.ADVANCEMENTS.updateToCurrentVersion(this.dataFixer, var3, var4);
               Map var5 = (Map)GSON.getAdapter(TYPE_TOKEN).fromJsonTree((JsonElement)var3.getValue());
               if (var5 == null) {
                  throw new JsonParseException("Found null for advancements");
               }

               var5.entrySet().stream().sorted(Entry.comparingByValue()).forEach(var2x -> {
                  Advancement var3x = var1.getAdvancement((ResourceLocation)var2x.getKey());
                  if (var3x == null) {
                     LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", var2x.getKey(), this.playerSavePath);
                  } else {
                     this.startProgress(var3x, (AdvancementProgress)var2x.getValue());
                     this.progressChanged.add(var3x);
                     this.markForVisibilityUpdate(var3x);
                  }
               });
            } catch (Throwable var7) {
               try {
                  var2.close();
               } catch (Throwable var6) {
                  var7.addSuppressed(var6);
               }

               throw var7;
            }

            var2.close();
         } catch (JsonParseException var8) {
            LOGGER.error("Couldn't parse player advancements in {}", this.playerSavePath, var8);
         } catch (IOException var9) {
            LOGGER.error("Couldn't access player advancements in {}", this.playerSavePath, var9);
         }
      }

      this.checkForAutomaticTriggers(var1);
      this.registerListeners(var1);
   }

   public void save() {
      LinkedHashMap var1 = new LinkedHashMap();

      for(Entry var3 : this.progress.entrySet()) {
         AdvancementProgress var4 = (AdvancementProgress)var3.getValue();
         if (var4.hasProgress()) {
            var1.put(((Advancement)var3.getKey()).getId(), var4);
         }
      }

      JsonElement var9 = GSON.toJsonTree(var1);
      var9.getAsJsonObject().addProperty("DataVersion", SharedConstants.getCurrentVersion().getDataVersion().getVersion());

      try {
         FileUtil.createDirectoriesSafe(this.playerSavePath.getParent());

         try (BufferedWriter var10 = Files.newBufferedWriter(this.playerSavePath, StandardCharsets.UTF_8)) {
            GSON.toJson(var9, var10);
         }
      } catch (IOException var8) {
         LOGGER.error("Couldn't save player advancements to {}", this.playerSavePath, var8);
      }
   }

   public boolean award(Advancement var1, String var2) {
      boolean var3 = false;
      AdvancementProgress var4 = this.getOrStartProgress(var1);
      boolean var5 = var4.isDone();
      if (var4.grantProgress(var2)) {
         this.unregisterListeners(var1);
         this.progressChanged.add(var1);
         var3 = true;
         if (!var5 && var4.isDone()) {
            var1.getRewards().grant(this.player);
            if (var1.getDisplay() != null
               && var1.getDisplay().shouldAnnounceChat()
               && this.player.level().getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
               this.playerList
                  .broadcastSystemMessage(
                     Component.translatable(
                        "chat.type.advancement." + var1.getDisplay().getFrame().getName(), this.player.getDisplayName(), var1.getChatComponent()
                     ),
                     false
                  );
            }
         }
      }

      if (!var5 && var4.isDone()) {
         this.markForVisibilityUpdate(var1);
      }

      return var3;
   }

   public boolean revoke(Advancement var1, String var2) {
      boolean var3 = false;
      AdvancementProgress var4 = this.getOrStartProgress(var1);
      boolean var5 = var4.isDone();
      if (var4.revokeProgress(var2)) {
         this.registerListeners(var1);
         this.progressChanged.add(var1);
         var3 = true;
      }

      if (var5 && !var4.isDone()) {
         this.markForVisibilityUpdate(var1);
      }

      return var3;
   }

   private void markForVisibilityUpdate(Advancement var1) {
      this.rootsToUpdate.add(var1.getRoot());
   }

   private void registerListeners(Advancement var1) {
      AdvancementProgress var2 = this.getOrStartProgress(var1);
      if (!var2.isDone()) {
         for(Entry var4 : var1.getCriteria().entrySet()) {
            CriterionProgress var5 = var2.getCriterion((String)var4.getKey());
            if (var5 != null && !var5.isDone()) {
               CriterionTriggerInstance var6 = ((Criterion)var4.getValue()).getTrigger();
               if (var6 != null) {
                  CriterionTrigger var7 = CriteriaTriggers.getCriterion(var6.getCriterion());
                  if (var7 != null) {
                     var7.addPlayerListener(this, new CriterionTrigger.Listener<>(var6, var1, (String)var4.getKey()));
                  }
               }
            }
         }
      }
   }

   private void unregisterListeners(Advancement var1) {
      AdvancementProgress var2 = this.getOrStartProgress(var1);

      for(Entry var4 : var1.getCriteria().entrySet()) {
         CriterionProgress var5 = var2.getCriterion((String)var4.getKey());
         if (var5 != null && (var5.isDone() || var2.isDone())) {
            CriterionTriggerInstance var6 = ((Criterion)var4.getValue()).getTrigger();
            if (var6 != null) {
               CriterionTrigger var7 = CriteriaTriggers.getCriterion(var6.getCriterion());
               if (var7 != null) {
                  var7.removePlayerListener(this, new CriterionTrigger.Listener<>(var6, var1, (String)var4.getKey()));
               }
            }
         }
      }
   }

   public void flushDirty(ServerPlayer var1) {
      if (this.isFirstPacket || !this.rootsToUpdate.isEmpty() || !this.progressChanged.isEmpty()) {
         HashMap var2 = new HashMap();
         HashSet var3 = new HashSet();
         HashSet var4 = new HashSet();

         for(Advancement var6 : this.rootsToUpdate) {
            this.updateTreeVisibility(var6, var3, var4);
         }

         this.rootsToUpdate.clear();

         for(Advancement var8 : this.progressChanged) {
            if (this.visible.contains(var8)) {
               var2.put(var8.getId(), this.progress.get(var8));
            }
         }

         this.progressChanged.clear();
         if (!var2.isEmpty() || !var3.isEmpty() || !var4.isEmpty()) {
            var1.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, var3, var4, var2));
         }
      }

      this.isFirstPacket = false;
   }

   public void setSelectedTab(@Nullable Advancement var1) {
      Advancement var2 = this.lastSelectedTab;
      if (var1 != null && var1.getParent() == null && var1.getDisplay() != null) {
         this.lastSelectedTab = var1;
      } else {
         this.lastSelectedTab = null;
      }

      if (var2 != this.lastSelectedTab) {
         this.player.connection.send(new ClientboundSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.getId()));
      }
   }

   public AdvancementProgress getOrStartProgress(Advancement var1) {
      AdvancementProgress var2 = this.progress.get(var1);
      if (var2 == null) {
         var2 = new AdvancementProgress();
         this.startProgress(var1, var2);
      }

      return var2;
   }

   private void startProgress(Advancement var1, AdvancementProgress var2) {
      var2.update(var1.getCriteria(), var1.getRequirements());
      this.progress.put(var1, var2);
   }

   private void updateTreeVisibility(Advancement var1, Set<Advancement> var2, Set<ResourceLocation> var3) {
      AdvancementVisibilityEvaluator.evaluateVisibility(var1, var1x -> this.getOrStartProgress(var1x).isDone(), (var3x, var4) -> {
         if (var4) {
            if (this.visible.add(var3x)) {
               var2.add(var3x);
               if (this.progress.containsKey(var3x)) {
                  this.progressChanged.add(var3x);
               }
            }
         } else if (this.visible.remove(var3x)) {
            var3.add(var3x.getId());
         }
      });
   }
}
