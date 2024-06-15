package net.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
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
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.FileUtil;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
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
   private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final PlayerList playerList;
   private final Path playerSavePath;
   private AdvancementTree tree;
   private final Map<AdvancementHolder, AdvancementProgress> progress = new LinkedHashMap<>();
   private final Set<AdvancementHolder> visible = new HashSet<>();
   private final Set<AdvancementHolder> progressChanged = new HashSet<>();
   private final Set<AdvancementNode> rootsToUpdate = new HashSet<>();
   private ServerPlayer player;
   @Nullable
   private AdvancementHolder lastSelectedTab;
   private boolean isFirstPacket = true;
   private final Codec<PlayerAdvancements.Data> codec;

   public PlayerAdvancements(DataFixer var1, PlayerList var2, ServerAdvancementManager var3, Path var4, ServerPlayer var5) {
      super();
      this.playerList = var2;
      this.playerSavePath = var4;
      this.player = var5;
      this.tree = var3.tree();
      short var6 = 1343;
      this.codec = DataFixTypes.ADVANCEMENTS.wrapCodec(PlayerAdvancements.Data.CODEC, var1, 1343);
      this.load(var3);
   }

   public void setPlayer(ServerPlayer var1) {
      this.player = var1;
   }

   public void stopListening() {
      for (CriterionTrigger var2 : BuiltInRegistries.TRIGGER_TYPES) {
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
      this.tree = var1.tree();
      this.load(var1);
   }

   private void registerListeners(ServerAdvancementManager var1) {
      for (AdvancementHolder var3 : var1.getAllAdvancements()) {
         this.registerListeners(var3);
      }
   }

   private void checkForAutomaticTriggers(ServerAdvancementManager var1) {
      for (AdvancementHolder var3 : var1.getAllAdvancements()) {
         Advancement var4 = var3.value();
         if (var4.criteria().isEmpty()) {
            this.award(var3, "");
            var4.rewards().grant(this.player);
         }
      }
   }

   private void load(ServerAdvancementManager var1) {
      if (Files.isRegularFile(this.playerSavePath)) {
         try {
            JsonReader var2 = new JsonReader(Files.newBufferedReader(this.playerSavePath, StandardCharsets.UTF_8));

            try {
               var2.setLenient(false);
               JsonElement var3 = Streams.parse(var2);
               PlayerAdvancements.Data var4 = (PlayerAdvancements.Data)this.codec.parse(JsonOps.INSTANCE, var3).getOrThrow(JsonParseException::new);
               this.applyFrom(var1, var4);
            } catch (Throwable var6) {
               try {
                  var2.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            var2.close();
         } catch (JsonIOException | IOException var7) {
            LOGGER.error("Couldn't access player advancements in {}", this.playerSavePath, var7);
         } catch (JsonParseException var8) {
            LOGGER.error("Couldn't parse player advancements in {}", this.playerSavePath, var8);
         }
      }

      this.checkForAutomaticTriggers(var1);
      this.registerListeners(var1);
   }

   public void save() {
      JsonElement var1 = (JsonElement)this.codec.encodeStart(JsonOps.INSTANCE, this.asData()).getOrThrow();

      try {
         FileUtil.createDirectoriesSafe(this.playerSavePath.getParent());

         try (BufferedWriter var2 = Files.newBufferedWriter(this.playerSavePath, StandardCharsets.UTF_8)) {
            GSON.toJson(var1, GSON.newJsonWriter(var2));
         }
      } catch (IOException var7) {
         LOGGER.error("Couldn't save player advancements to {}", this.playerSavePath, var7);
      }
   }

   private void applyFrom(ServerAdvancementManager var1, PlayerAdvancements.Data var2) {
      var2.forEach((var2x, var3) -> {
         AdvancementHolder var4 = var1.get(var2x);
         if (var4 == null) {
            LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", var2x, this.playerSavePath);
         } else {
            this.startProgress(var4, var3);
            this.progressChanged.add(var4);
            this.markForVisibilityUpdate(var4);
         }
      });
   }

   private PlayerAdvancements.Data asData() {
      LinkedHashMap var1 = new LinkedHashMap();
      this.progress.forEach((var1x, var2) -> {
         if (var2.hasProgress()) {
            var1.put(var1x.id(), var2);
         }
      });
      return new PlayerAdvancements.Data(var1);
   }

   public boolean award(AdvancementHolder var1, String var2) {
      boolean var3 = false;
      AdvancementProgress var4 = this.getOrStartProgress(var1);
      boolean var5 = var4.isDone();
      if (var4.grantProgress(var2)) {
         this.unregisterListeners(var1);
         this.progressChanged.add(var1);
         var3 = true;
         if (!var5 && var4.isDone()) {
            var1.value().rewards().grant(this.player);
            var1.value().display().ifPresent(var2x -> {
               if (var2x.shouldAnnounceChat() && this.player.level().getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
                  this.playerList.broadcastSystemMessage(var2x.getType().createAnnouncement(var1, this.player), false);
               }
            });
         }
      }

      if (!var5 && var4.isDone()) {
         this.markForVisibilityUpdate(var1);
      }

      return var3;
   }

   public boolean revoke(AdvancementHolder var1, String var2) {
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

   private void markForVisibilityUpdate(AdvancementHolder var1) {
      AdvancementNode var2 = this.tree.get(var1);
      if (var2 != null) {
         this.rootsToUpdate.add(var2.root());
      }
   }

   private void registerListeners(AdvancementHolder var1) {
      AdvancementProgress var2 = this.getOrStartProgress(var1);
      if (!var2.isDone()) {
         for (Entry var4 : var1.value().criteria().entrySet()) {
            CriterionProgress var5 = var2.getCriterion((String)var4.getKey());
            if (var5 != null && !var5.isDone()) {
               this.registerListener(var1, (String)var4.getKey(), (Criterion)var4.getValue());
            }
         }
      }
   }

   private <T extends CriterionTriggerInstance> void registerListener(AdvancementHolder var1, String var2, Criterion<T> var3) {
      var3.trigger().addPlayerListener(this, new CriterionTrigger.Listener<>(var3.triggerInstance(), var1, var2));
   }

   private void unregisterListeners(AdvancementHolder var1) {
      AdvancementProgress var2 = this.getOrStartProgress(var1);

      for (Entry var4 : var1.value().criteria().entrySet()) {
         CriterionProgress var5 = var2.getCriterion((String)var4.getKey());
         if (var5 != null && (var5.isDone() || var2.isDone())) {
            this.removeListener(var1, (String)var4.getKey(), (Criterion)var4.getValue());
         }
      }
   }

   private <T extends CriterionTriggerInstance> void removeListener(AdvancementHolder var1, String var2, Criterion<T> var3) {
      var3.trigger().removePlayerListener(this, new CriterionTrigger.Listener<>(var3.triggerInstance(), var1, var2));
   }

   public void flushDirty(ServerPlayer var1) {
      if (this.isFirstPacket || !this.rootsToUpdate.isEmpty() || !this.progressChanged.isEmpty()) {
         HashMap var2 = new HashMap();
         HashSet var3 = new HashSet();
         HashSet var4 = new HashSet();

         for (AdvancementNode var6 : this.rootsToUpdate) {
            this.updateTreeVisibility(var6, var3, var4);
         }

         this.rootsToUpdate.clear();

         for (AdvancementHolder var8 : this.progressChanged) {
            if (this.visible.contains(var8)) {
               var2.put(var8.id(), this.progress.get(var8));
            }
         }

         this.progressChanged.clear();
         if (!var2.isEmpty() || !var3.isEmpty() || !var4.isEmpty()) {
            var1.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, var3, var4, var2));
         }
      }

      this.isFirstPacket = false;
   }

   public void setSelectedTab(@Nullable AdvancementHolder var1) {
      AdvancementHolder var2 = this.lastSelectedTab;
      if (var1 != null && var1.value().isRoot() && var1.value().display().isPresent()) {
         this.lastSelectedTab = var1;
      } else {
         this.lastSelectedTab = null;
      }

      if (var2 != this.lastSelectedTab) {
         this.player.connection.send(new ClientboundSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.id()));
      }
   }

   public AdvancementProgress getOrStartProgress(AdvancementHolder var1) {
      AdvancementProgress var2 = this.progress.get(var1);
      if (var2 == null) {
         var2 = new AdvancementProgress();
         this.startProgress(var1, var2);
      }

      return var2;
   }

   private void startProgress(AdvancementHolder var1, AdvancementProgress var2) {
      var2.update(var1.value().requirements());
      this.progress.put(var1, var2);
   }

   private void updateTreeVisibility(AdvancementNode var1, Set<AdvancementHolder> var2, Set<ResourceLocation> var3) {
      AdvancementVisibilityEvaluator.evaluateVisibility(var1, var1x -> this.getOrStartProgress(var1x.holder()).isDone(), (var3x, var4) -> {
         AdvancementHolder var5 = var3x.holder();
         if (var4) {
            if (this.visible.add(var5)) {
               var2.add(var5);
               if (this.progress.containsKey(var5)) {
                  this.progressChanged.add(var5);
               }
            }
         } else if (this.visible.remove(var5)) {
            var3.add(var5.id());
         }
      });
   }

   static record Data(Map<ResourceLocation, AdvancementProgress> map) {
      public static final Codec<PlayerAdvancements.Data> CODEC = Codec.unboundedMap(ResourceLocation.CODEC, AdvancementProgress.CODEC)
         .xmap(PlayerAdvancements.Data::new, PlayerAdvancements.Data::map);

      Data(Map<ResourceLocation, AdvancementProgress> map) {
         super();
         this.map = map;
      }

      public void forEach(BiConsumer<ResourceLocation, AdvancementProgress> var1) {
         this.map.entrySet().stream().sorted(Entry.comparingByValue()).forEach(var1x -> var1.accept(var1x.getKey(), var1x.getValue()));
      }
   }
}
