package net.minecraft.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
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
import net.minecraft.resources.Identifier;
import net.minecraft.server.advancements.AdvancementVisibilityEvaluator;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.FileUtil;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.gamerules.GameRules;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PlayerAdvancements {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final PlayerList playerList;
   private final Path playerSavePath;
   private AdvancementTree tree;
   private final Map<AdvancementHolder, AdvancementProgress> progress = new LinkedHashMap();
   private final Set<AdvancementHolder> visible = new HashSet();
   private final Set<AdvancementHolder> progressChanged = new HashSet();
   private final Set<AdvancementNode> rootsToUpdate = new HashSet();
   private ServerPlayer player;
   private @Nullable AdvancementHolder lastSelectedTab;
   private boolean isFirstPacket = true;
   private final Codec<Data> codec;

   public PlayerAdvancements(final DataFixer dataFixer, final PlayerList playerList, final ServerAdvancementManager manager, final Path playerSavePath, final ServerPlayer player) {
      super();
      this.playerList = playerList;
      this.playerSavePath = playerSavePath;
      this.player = player;
      this.tree = manager.tree();
      int defaultVersion = 1343;
      this.codec = DataFixTypes.ADVANCEMENTS.<Data>wrapCodec(PlayerAdvancements.Data.CODEC, dataFixer, 1343);
      this.load(manager);
   }

   public void setPlayer(final ServerPlayer player) {
      this.player = player;
   }

   public void stopListening() {
      for(CriterionTrigger<?> trigger : BuiltInRegistries.TRIGGER_TYPES) {
         trigger.removePlayerListeners(this);
      }

   }

   public void reload(final ServerAdvancementManager manager) {
      this.stopListening();
      this.progress.clear();
      this.visible.clear();
      this.rootsToUpdate.clear();
      this.progressChanged.clear();
      this.isFirstPacket = true;
      this.lastSelectedTab = null;
      this.tree = manager.tree();
      this.load(manager);
   }

   private void registerListeners(final ServerAdvancementManager manager) {
      for(AdvancementHolder advancement : manager.getAllAdvancements()) {
         this.registerListeners(advancement);
      }

   }

   private void checkForAutomaticTriggers(final ServerAdvancementManager manager) {
      for(AdvancementHolder holder : manager.getAllAdvancements()) {
         Advancement advancement = holder.value();
         if (advancement.criteria().isEmpty()) {
            this.award(holder, "");
            advancement.rewards().grant(this.player);
         }
      }

   }

   private void load(final ServerAdvancementManager manager) {
      if (Files.isRegularFile(this.playerSavePath, new LinkOption[0])) {
         try {
            Reader reader = Files.newBufferedReader(this.playerSavePath, StandardCharsets.UTF_8);

            try {
               JsonElement json = StrictJsonParser.parse(reader);
               Data data = (Data)this.codec.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
               this.applyFrom(manager, data);
            } catch (Throwable var6) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (reader != null) {
               reader.close();
            }
         } catch (JsonIOException | IOException e) {
            LOGGER.error("Couldn't access player advancements in {}", this.playerSavePath, e);
         } catch (JsonParseException e) {
            LOGGER.error("Couldn't parse player advancements in {}", this.playerSavePath, e);
         }
      }

      this.checkForAutomaticTriggers(manager);
      this.registerListeners(manager);
   }

   public void save() {
      JsonElement json = (JsonElement)this.codec.encodeStart(JsonOps.INSTANCE, this.asData()).getOrThrow();

      try {
         FileUtil.createDirectoriesSafe(this.playerSavePath.getParent());
         Writer outputWriter = Files.newBufferedWriter(this.playerSavePath, StandardCharsets.UTF_8);

         try {
            GSON.toJson(json, GSON.newJsonWriter(outputWriter));
         } catch (Throwable var6) {
            if (outputWriter != null) {
               try {
                  outputWriter.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (outputWriter != null) {
            outputWriter.close();
         }
      } catch (JsonIOException | IOException e) {
         LOGGER.error("Couldn't save player advancements to {}", this.playerSavePath, e);
      }

   }

   private void applyFrom(final ServerAdvancementManager manager, final Data data) {
      data.forEach((id, progress) -> {
         AdvancementHolder advancement = manager.get(id);
         if (advancement == null) {
            LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", id, this.playerSavePath);
         } else {
            this.startProgress(advancement, progress);
            this.progressChanged.add(advancement);
            this.markForVisibilityUpdate(advancement);
         }
      });
   }

   private Data asData() {
      Map<Identifier, AdvancementProgress> map = new LinkedHashMap();
      this.progress.forEach((advancement, progress) -> {
         if (progress.hasProgress()) {
            map.put(advancement.id(), progress);
         }

      });
      return new Data(map);
   }

   public boolean award(final AdvancementHolder holder, final String criterion) {
      boolean result = false;
      AdvancementProgress progress = this.getOrStartProgress(holder);
      boolean wasDone = progress.isDone();
      if (progress.grantProgress(criterion)) {
         this.unregisterListeners(holder);
         this.progressChanged.add(holder);
         result = true;
         if (!wasDone && progress.isDone()) {
            holder.value().rewards().grant(this.player);
            holder.value().display().ifPresent((display) -> {
               if (display.shouldAnnounceChat() && (Boolean)this.player.level().getGameRules().get(GameRules.SHOW_ADVANCEMENT_MESSAGES)) {
                  this.playerList.broadcastSystemMessage(display.getType().createAnnouncement(holder, this.player), false);
               }

            });
         }
      }

      if (!wasDone && progress.isDone()) {
         this.markForVisibilityUpdate(holder);
      }

      return result;
   }

   public boolean revoke(final AdvancementHolder advancement, final String criterion) {
      boolean result = false;
      AdvancementProgress progress = this.getOrStartProgress(advancement);
      boolean wasDone = progress.isDone();
      if (progress.revokeProgress(criterion)) {
         this.registerListeners(advancement);
         this.progressChanged.add(advancement);
         result = true;
      }

      if (wasDone && !progress.isDone()) {
         this.markForVisibilityUpdate(advancement);
      }

      return result;
   }

   private void markForVisibilityUpdate(final AdvancementHolder advancement) {
      AdvancementNode node = this.tree.get(advancement);
      if (node != null) {
         this.rootsToUpdate.add(node.root());
      }

   }

   private void registerListeners(final AdvancementHolder holder) {
      AdvancementProgress advancementProgress = this.getOrStartProgress(holder);
      if (!advancementProgress.isDone()) {
         for(Map.Entry<String, Criterion<?>> entry : holder.value().criteria().entrySet()) {
            CriterionProgress criterionProgress = advancementProgress.getCriterion((String)entry.getKey());
            if (criterionProgress != null && !criterionProgress.isDone()) {
               this.registerListener(holder, (String)entry.getKey(), (Criterion)entry.getValue());
            }
         }

      }
   }

   private <T extends CriterionTriggerInstance> void registerListener(final AdvancementHolder holder, final String key, final Criterion<T> criterion) {
      criterion.trigger().addPlayerListener(this, new CriterionTrigger.Listener(criterion.triggerInstance(), holder, key));
   }

   private void unregisterListeners(final AdvancementHolder holder) {
      AdvancementProgress advancementProgress = this.getOrStartProgress(holder);

      for(Map.Entry<String, Criterion<?>> entry : holder.value().criteria().entrySet()) {
         CriterionProgress criterionProgress = advancementProgress.getCriterion((String)entry.getKey());
         if (criterionProgress != null && (criterionProgress.isDone() || advancementProgress.isDone())) {
            this.removeListener(holder, (String)entry.getKey(), (Criterion)entry.getValue());
         }
      }

   }

   private <T extends CriterionTriggerInstance> void removeListener(final AdvancementHolder holder, final String key, final Criterion<T> criterion) {
      criterion.trigger().removePlayerListener(this, new CriterionTrigger.Listener(criterion.triggerInstance(), holder, key));
   }

   public void flushDirty(final ServerPlayer player, final boolean showAdvancements) {
      if (this.isFirstPacket || !this.rootsToUpdate.isEmpty() || !this.progressChanged.isEmpty()) {
         Map<Identifier, AdvancementProgress> progress = new HashMap();
         Set<AdvancementHolder> added = new HashSet();
         Set<Identifier> removed = new HashSet();

         for(AdvancementNode root : this.rootsToUpdate) {
            this.updateTreeVisibility(root, added, removed);
         }

         this.rootsToUpdate.clear();

         for(AdvancementHolder holder : this.progressChanged) {
            if (this.visible.contains(holder)) {
               progress.put(holder.id(), (AdvancementProgress)this.progress.get(holder));
            }
         }

         this.progressChanged.clear();
         if (!progress.isEmpty() || !added.isEmpty() || !removed.isEmpty()) {
            player.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, added, removed, progress, showAdvancements));
         }
      }

      this.isFirstPacket = false;
   }

   public void setSelectedTab(final @Nullable AdvancementHolder holder) {
      AdvancementHolder old = this.lastSelectedTab;
      if (holder != null && holder.value().isRoot() && holder.value().display().isPresent()) {
         this.lastSelectedTab = holder;
      } else {
         this.lastSelectedTab = null;
      }

      if (old != this.lastSelectedTab) {
         this.player.connection.send(new ClientboundSelectAdvancementsTabPacket(this.lastSelectedTab == null ? null : this.lastSelectedTab.id()));
      }

   }

   public AdvancementProgress getOrStartProgress(final AdvancementHolder advancement) {
      AdvancementProgress progress = (AdvancementProgress)this.progress.get(advancement);
      if (progress == null) {
         progress = new AdvancementProgress();
         this.startProgress(advancement, progress);
      }

      return progress;
   }

   private void startProgress(final AdvancementHolder holder, final AdvancementProgress progress) {
      progress.update(holder.value().requirements());
      this.progress.put(holder, progress);
   }

   private void updateTreeVisibility(final AdvancementNode root, final Set<AdvancementHolder> added, final Set<Identifier> removed) {
      AdvancementVisibilityEvaluator.evaluateVisibility(root, (node) -> this.getOrStartProgress(node.holder()).isDone(), (node, shouldBeVisible) -> {
         AdvancementHolder advancement = node.holder();
         if (shouldBeVisible) {
            if (this.visible.add(advancement)) {
               added.add(advancement);
               if (this.progress.containsKey(advancement)) {
                  this.progressChanged.add(advancement);
               }
            }
         } else if (this.visible.remove(advancement)) {
            removed.add(advancement.id());
         }

      });
   }

   private static record Data(Map<Identifier, AdvancementProgress> map) {
      public static final Codec<Data> CODEC;

      private Data {
         super();
      }

      public void forEach(final BiConsumer<Identifier, AdvancementProgress> consumer) {
         this.map.entrySet().stream().sorted(Entry.comparingByValue()).forEach((entry) -> consumer.accept((Identifier)entry.getKey(), (AdvancementProgress)entry.getValue()));
      }

      static {
         CODEC = Codec.unboundedMap(Identifier.CODEC, AdvancementProgress.CODEC).xmap(Data::new, Data::map);
      }
   }
}
