package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundSelectAdvancementsTabPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerAdvancements {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.Serializer()).registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).setPrettyPrinting().create();
   private static final TypeToken<Map<ResourceLocation, AdvancementProgress>> TYPE_TOKEN = new TypeToken<Map<ResourceLocation, AdvancementProgress>>() {
   };
   private final MinecraftServer server;
   private final File file;
   private final Map<Advancement, AdvancementProgress> advancements = Maps.newLinkedHashMap();
   private final Set<Advancement> visible = Sets.newLinkedHashSet();
   private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
   private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
   private ServerPlayer player;
   @Nullable
   private Advancement lastSelectedTab;
   private boolean isFirstPacket = true;

   public PlayerAdvancements(MinecraftServer var1, File var2, ServerPlayer var3) {
      super();
      this.server = var1;
      this.file = var2;
      this.player = var3;
      this.load();
   }

   public void setPlayer(ServerPlayer var1) {
      this.player = var1;
   }

   public void stopListening() {
      Iterator var1 = CriteriaTriggers.all().iterator();

      while(var1.hasNext()) {
         CriterionTrigger var2 = (CriterionTrigger)var1.next();
         var2.removePlayerListeners(this);
      }

   }

   public void reload() {
      this.stopListening();
      this.advancements.clear();
      this.visible.clear();
      this.visibilityChanged.clear();
      this.progressChanged.clear();
      this.isFirstPacket = true;
      this.lastSelectedTab = null;
      this.load();
   }

   private void registerListeners() {
      Iterator var1 = this.server.getAdvancements().getAllAdvancements().iterator();

      while(var1.hasNext()) {
         Advancement var2 = (Advancement)var1.next();
         this.registerListeners(var2);
      }

   }

   private void ensureAllVisible() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.advancements.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((AdvancementProgress)var3.getValue()).isDone()) {
            var1.add(var3.getKey());
            this.progressChanged.add(var3.getKey());
         }
      }

      var2 = var1.iterator();

      while(var2.hasNext()) {
         Advancement var4 = (Advancement)var2.next();
         this.ensureVisibility(var4);
      }

   }

   private void checkForAutomaticTriggers() {
      Iterator var1 = this.server.getAdvancements().getAllAdvancements().iterator();

      while(var1.hasNext()) {
         Advancement var2 = (Advancement)var1.next();
         if (var2.getCriteria().isEmpty()) {
            this.award(var2, "");
            var2.getRewards().grant(this.player);
         }
      }

   }

   private void load() {
      if (this.file.isFile()) {
         try {
            JsonReader var1 = new JsonReader(new StringReader(Files.toString(this.file, StandardCharsets.UTF_8)));
            Throwable var2 = null;

            try {
               var1.setLenient(false);
               Dynamic var3 = new Dynamic(JsonOps.INSTANCE, Streams.parse(var1));
               if (!var3.get("DataVersion").asNumber().isPresent()) {
                  var3 = var3.set("DataVersion", var3.createInt(1343));
               }

               var3 = this.server.getFixerUpper().update(DataFixTypes.ADVANCEMENTS.getType(), var3, var3.get("DataVersion").asInt(0), SharedConstants.getCurrentVersion().getWorldVersion());
               var3 = var3.remove("DataVersion");
               Map var4 = (Map)GSON.getAdapter(TYPE_TOKEN).fromJsonTree((JsonElement)var3.getValue());
               if (var4 == null) {
                  throw new JsonParseException("Found null for advancements");
               }

               Stream var5 = var4.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));
               Iterator var6 = ((List)var5.collect(Collectors.toList())).iterator();

               while(var6.hasNext()) {
                  Entry var7 = (Entry)var6.next();
                  Advancement var8 = this.server.getAdvancements().getAdvancement((ResourceLocation)var7.getKey());
                  if (var8 == null) {
                     LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", var7.getKey(), this.file);
                  } else {
                     this.startProgress(var8, (AdvancementProgress)var7.getValue());
                  }
               }
            } catch (Throwable var18) {
               var2 = var18;
               throw var18;
            } finally {
               if (var1 != null) {
                  if (var2 != null) {
                     try {
                        var1.close();
                     } catch (Throwable var17) {
                        var2.addSuppressed(var17);
                     }
                  } else {
                     var1.close();
                  }
               }

            }
         } catch (JsonParseException var20) {
            LOGGER.error("Couldn't parse player advancements in {}", this.file, var20);
         } catch (IOException var21) {
            LOGGER.error("Couldn't access player advancements in {}", this.file, var21);
         }
      }

      this.checkForAutomaticTriggers();
      this.ensureAllVisible();
      this.registerListeners();
   }

   public void save() {
      HashMap var1 = Maps.newHashMap();
      Iterator var2 = this.advancements.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         AdvancementProgress var4 = (AdvancementProgress)var3.getValue();
         if (var4.hasProgress()) {
            var1.put(((Advancement)var3.getKey()).getId(), var4);
         }
      }

      if (this.file.getParentFile() != null) {
         this.file.getParentFile().mkdirs();
      }

      JsonElement var36 = GSON.toJsonTree(var1);
      var36.getAsJsonObject().addProperty("DataVersion", SharedConstants.getCurrentVersion().getWorldVersion());

      try {
         FileOutputStream var37 = new FileOutputStream(this.file);
         Throwable var38 = null;

         try {
            OutputStreamWriter var5 = new OutputStreamWriter(var37, Charsets.UTF_8.newEncoder());
            Throwable var6 = null;

            try {
               GSON.toJson(var36, var5);
            } catch (Throwable var31) {
               var6 = var31;
               throw var31;
            } finally {
               if (var5 != null) {
                  if (var6 != null) {
                     try {
                        var5.close();
                     } catch (Throwable var30) {
                        var6.addSuppressed(var30);
                     }
                  } else {
                     var5.close();
                  }
               }

            }
         } catch (Throwable var33) {
            var38 = var33;
            throw var33;
         } finally {
            if (var37 != null) {
               if (var38 != null) {
                  try {
                     var37.close();
                  } catch (Throwable var29) {
                     var38.addSuppressed(var29);
                  }
               } else {
                  var37.close();
               }
            }

         }
      } catch (IOException var35) {
         LOGGER.error("Couldn't save player advancements to {}", this.file, var35);
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
            if (var1.getDisplay() != null && var1.getDisplay().shouldAnnounceChat() && this.player.level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
               this.server.getPlayerList().broadcastMessage(new TranslatableComponent("chat.type.advancement." + var1.getDisplay().getFrame().getName(), new Object[]{this.player.getDisplayName(), var1.getChatComponent()}));
            }
         }
      }

      if (var4.isDone()) {
         this.ensureVisibility(var1);
      }

      return var3;
   }

   public boolean revoke(Advancement var1, String var2) {
      boolean var3 = false;
      AdvancementProgress var4 = this.getOrStartProgress(var1);
      if (var4.revokeProgress(var2)) {
         this.registerListeners(var1);
         this.progressChanged.add(var1);
         var3 = true;
      }

      if (!var4.hasProgress()) {
         this.ensureVisibility(var1);
      }

      return var3;
   }

   private void registerListeners(Advancement var1) {
      AdvancementProgress var2 = this.getOrStartProgress(var1);
      if (!var2.isDone()) {
         Iterator var3 = var1.getCriteria().entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            CriterionProgress var5 = var2.getCriterion((String)var4.getKey());
            if (var5 != null && !var5.isDone()) {
               CriterionTriggerInstance var6 = ((Criterion)var4.getValue()).getTrigger();
               if (var6 != null) {
                  CriterionTrigger var7 = CriteriaTriggers.getCriterion(var6.getCriterion());
                  if (var7 != null) {
                     var7.addPlayerListener(this, new CriterionTrigger.Listener(var6, var1, (String)var4.getKey()));
                  }
               }
            }
         }

      }
   }

   private void unregisterListeners(Advancement var1) {
      AdvancementProgress var2 = this.getOrStartProgress(var1);
      Iterator var3 = var1.getCriteria().entrySet().iterator();

      while(true) {
         Entry var4;
         CriterionProgress var5;
         do {
            do {
               if (!var3.hasNext()) {
                  return;
               }

               var4 = (Entry)var3.next();
               var5 = var2.getCriterion((String)var4.getKey());
            } while(var5 == null);
         } while(!var5.isDone() && !var2.isDone());

         CriterionTriggerInstance var6 = ((Criterion)var4.getValue()).getTrigger();
         if (var6 != null) {
            CriterionTrigger var7 = CriteriaTriggers.getCriterion(var6.getCriterion());
            if (var7 != null) {
               var7.removePlayerListener(this, new CriterionTrigger.Listener(var6, var1, (String)var4.getKey()));
            }
         }
      }
   }

   public void flushDirty(ServerPlayer var1) {
      if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
         HashMap var2 = Maps.newHashMap();
         LinkedHashSet var3 = Sets.newLinkedHashSet();
         LinkedHashSet var4 = Sets.newLinkedHashSet();
         Iterator var5 = this.progressChanged.iterator();

         Advancement var6;
         while(var5.hasNext()) {
            var6 = (Advancement)var5.next();
            if (this.visible.contains(var6)) {
               var2.put(var6.getId(), this.advancements.get(var6));
            }
         }

         var5 = this.visibilityChanged.iterator();

         while(var5.hasNext()) {
            var6 = (Advancement)var5.next();
            if (this.visible.contains(var6)) {
               var3.add(var6);
            } else {
               var4.add(var6.getId());
            }
         }

         if (this.isFirstPacket || !var2.isEmpty() || !var3.isEmpty() || !var4.isEmpty()) {
            var1.connection.send(new ClientboundUpdateAdvancementsPacket(this.isFirstPacket, var3, var4, var2));
            this.visibilityChanged.clear();
            this.progressChanged.clear();
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
      AdvancementProgress var2 = (AdvancementProgress)this.advancements.get(var1);
      if (var2 == null) {
         var2 = new AdvancementProgress();
         this.startProgress(var1, var2);
      }

      return var2;
   }

   private void startProgress(Advancement var1, AdvancementProgress var2) {
      var2.update(var1.getCriteria(), var1.getRequirements());
      this.advancements.put(var1, var2);
   }

   private void ensureVisibility(Advancement var1) {
      boolean var2 = this.shouldBeVisible(var1);
      boolean var3 = this.visible.contains(var1);
      if (var2 && !var3) {
         this.visible.add(var1);
         this.visibilityChanged.add(var1);
         if (this.advancements.containsKey(var1)) {
            this.progressChanged.add(var1);
         }
      } else if (!var2 && var3) {
         this.visible.remove(var1);
         this.visibilityChanged.add(var1);
      }

      if (var2 != var3 && var1.getParent() != null) {
         this.ensureVisibility(var1.getParent());
      }

      Iterator var4 = var1.getChildren().iterator();

      while(var4.hasNext()) {
         Advancement var5 = (Advancement)var4.next();
         this.ensureVisibility(var5);
      }

   }

   private boolean shouldBeVisible(Advancement var1) {
      for(int var2 = 0; var1 != null && var2 <= 2; ++var2) {
         if (var2 == 0 && this.hasCompletedChildrenOrSelf(var1)) {
            return true;
         }

         if (var1.getDisplay() == null) {
            return false;
         }

         AdvancementProgress var3 = this.getOrStartProgress(var1);
         if (var3.isDone()) {
            return true;
         }

         if (var1.getDisplay().isHidden()) {
            return false;
         }

         var1 = var1.getParent();
      }

      return false;
   }

   private boolean hasCompletedChildrenOrSelf(Advancement var1) {
      AdvancementProgress var2 = this.getOrStartProgress(var1);
      if (var2.isDone()) {
         return true;
      } else {
         Iterator var3 = var1.getChildren().iterator();

         Advancement var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (Advancement)var3.next();
         } while(!this.hasCompletedChildrenOrSelf(var4));

         return true;
      }
   }
}
