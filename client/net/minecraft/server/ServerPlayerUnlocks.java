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
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.FileUtil;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundUpdatePlayerUnlocksPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.PlayerUnlock;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.datafix.DataFixTypes;
import org.slf4j.Logger;

public class ServerPlayerUnlocks {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private final PlayerList playerList;
   private final Path playerSavePath;
   private final Map<Holder<PlayerUnlock>, Boolean> obtained = new LinkedHashMap();
   private final Map<Holder<PlayerUnlock>, PlayerUnlock.UnlockVisibility> visibility = new LinkedHashMap();
   private final Map<Holder<PlayerUnlock>, Boolean> isActiveExclusive = new LinkedHashMap();
   private final Set<Holder<PlayerUnlock>> obtainedChanged = new HashSet();
   private final Set<Holder<PlayerUnlock>> updatedNodes = new HashSet();
   private final Set<Holder<PlayerUnlock>> activeExclusiveChanged = new HashSet();
   private ServerPlayer player;
   private boolean isFirstPacket = true;
   private final Codec<Data> codec;

   public ServerPlayerUnlocks(DataFixer var1, PlayerList var2, Path var3, ServerPlayer var4) {
      super();
      this.playerList = var2;
      this.playerSavePath = var3;
      this.player = var4;
      boolean var5 = true;
      this.codec = DataFixTypes.ADVANCEMENTS.<Data>wrapCodec(ServerPlayerUnlocks.Data.CODEC, var1, 1343);
      this.load();
   }

   public void setPlayer(ServerPlayer var1) {
      this.player = var1;
   }

   public void reload() {
      this.obtained.clear();
      this.visibility.clear();
      this.isActiveExclusive.clear();
      this.activeExclusiveChanged.clear();
      this.updatedNodes.clear();
      this.isFirstPacket = true;
      this.load();
   }

   private void load() {
      if (Files.isRegularFile(this.playerSavePath, new LinkOption[0])) {
         try {
            JsonReader var1 = new JsonReader(Files.newBufferedReader(this.playerSavePath, StandardCharsets.UTF_8));

            try {
               var1.setLenient(false);
               JsonElement var2 = Streams.parse(var1);
               Data var3 = (Data)this.codec.parse(JsonOps.INSTANCE, var2).getOrThrow(JsonParseException::new);
               this.obtained.putAll(var3.obtained);
               this.visibility.putAll(var3.visibility);
               this.isActiveExclusive.putAll(var3.isActiveExclusive);
            } catch (Throwable var5) {
               try {
                  var1.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }

               throw var5;
            }

            var1.close();
         } catch (JsonIOException | IOException var6) {
            LOGGER.error("Couldn't access player advancements in {}", this.playerSavePath, var6);
         } catch (JsonParseException var7) {
            LOGGER.error("Couldn't parse player advancements in {}", this.playerSavePath, var7);
         }
      }

      for(Holder var9 : BuiltInRegistries.PLAYER_UNLOCK.listElements().toList()) {
         this.isActiveExclusive.computeIfAbsent(var9, (var0) -> false);
         if ((Boolean)this.obtained.computeIfAbsent(var9, (var0) -> false) && (((PlayerUnlock)var9.value()).exclusiveKey().isEmpty() || this.isActiveExclusive(var9))) {
            this.activate(var9);
         }

         this.visibility.computeIfAbsent(var9, (var0) -> ((PlayerUnlock)var0.value()).defaultVisibility());
         this.updatedNodes.add(var9);
      }

   }

   public void save() {
      JsonElement var1 = (JsonElement)this.codec.encodeStart(JsonOps.INSTANCE, new Data(this.obtained, this.visibility, this.isActiveExclusive)).getOrThrow();

      try {
         FileUtil.createDirectoriesSafe(this.playerSavePath.getParent());
         BufferedWriter var2 = Files.newBufferedWriter(this.playerSavePath, StandardCharsets.UTF_8);

         try {
            GSON.toJson(var1, GSON.newJsonWriter(var2));
         } catch (Throwable var6) {
            if (var2 != null) {
               try {
                  ((Writer)var2).close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (var2 != null) {
            ((Writer)var2).close();
         }
      } catch (JsonIOException | IOException var7) {
         LOGGER.error("Couldn't save player advancements to {}", this.playerSavePath, var7);
      }

   }

   public Set<Holder<PlayerUnlock>> getActiveUnlocks() {
      HashSet var1 = new HashSet();
      this.obtained.forEach((var1x, var2) -> {
         if (var2) {
            var1.add(var1x);
         }

      });
      this.obtained.forEach((var2, var3) -> {
         if (var3) {
            if (!((PlayerUnlock)var2.value()).disables().isEmpty()) {
               List var10000 = ((PlayerUnlock)var2.value()).disables();
               Objects.requireNonNull(var1);
               var10000.forEach(var1::remove);
            }

            if (!((PlayerUnlock)var2.value()).exclusiveKey().isEmpty() && !(Boolean)this.isActiveExclusive.getOrDefault(var2, false)) {
               var1.remove(var2);
            }
         }

      });
      return var1;
   }

   public boolean isUnlocked(Holder<PlayerUnlock> var1) {
      return (Boolean)this.obtained.getOrDefault(var1, false);
   }

   public boolean isActive(Holder<PlayerUnlock> var1) {
      return this.getActiveUnlocks().contains(var1);
   }

   public boolean isActiveExclusive(Holder<PlayerUnlock> var1) {
      return (Boolean)this.isActiveExclusive.getOrDefault(var1, false);
   }

   public boolean unlock(Holder<PlayerUnlock> var1) {
      if ((Boolean)this.obtained.getOrDefault(var1, false)) {
         return false;
      } else {
         this.obtained.put(var1, true);
         this.obtainedChanged.add(var1);
         DisplayInfo var2 = ((PlayerUnlock)var1.value()).display();
         if (var2.shouldAnnounceChat()) {
            this.playerList.broadcastSystemMessage(var2.getType().createPlayerUnlockAnnouncement(var1, this.player), false);
         }

         this.markForVisibilityUpdate(var1, PlayerUnlock.UnlockVisibility.VISIBLE);
         this.activate(var1);
         if (this.player.serverLevel().isMine()) {
            this.onMineEnter(var1);
         }

         return true;
      }
   }

   public void reactivateUnlock(Holder<PlayerUnlock> var1) {
      if (!((PlayerUnlock)var1.value()).exclusiveKey().isEmpty()) {
         if (this.isActiveExclusive(var1)) {
            this.deactivateExclusiveUnlock(var1);
         } else {
            this.activateExclusiveUnlock(var1);
         }
      }

   }

   public void reactivateAllUnlocks() {
      for(Holder var2 : this.getActiveUnlocks()) {
         this.activate(var2);
      }

   }

   private void activate(Holder<PlayerUnlock> var1) {
      ((PlayerUnlock)var1.value()).activation().accept(this.player);
      if (!((PlayerUnlock)var1.value()).exclusiveKey().isEmpty()) {
         this.activateExclusiveUnlock(var1);
      }

   }

   private void activateExclusiveUnlock(Holder<PlayerUnlock> var1) {
      if (!((PlayerUnlock)var1.value()).exclusiveKey().isEmpty()) {
         for(Holder var3 : this.getActiveUnlocks()) {
            if (((PlayerUnlock)var3.value()).exclusiveKey().equals(((PlayerUnlock)var1.value()).exclusiveKey())) {
               this.isActiveExclusive.put(var3, false);
               this.activeExclusiveChanged.add(var3);
            }
         }

         this.isActiveExclusive.put(var1, true);
         this.activeExclusiveChanged.add(var1);
      }

   }

   private void deactivateExclusiveUnlock(Holder<PlayerUnlock> var1) {
      if (this.isActiveExclusive(var1)) {
         this.isActiveExclusive.put(var1, false);
         this.activeExclusiveChanged.add(var1);
      }

   }

   public void onMineEnter() {
      for(Holder var2 : this.getActiveUnlocks()) {
         if ((Boolean)this.obtained.getOrDefault(var2, false)) {
            this.onMineEnter(var2);
         }
      }

   }

   private void onMineEnter(Holder<PlayerUnlock> var1) {
      ((PlayerUnlock)var1.value()).onMineEnter().accept(this.player);
   }

   public void makeVisible(Holder<PlayerUnlock> var1) {
      if (this.visibility.getOrDefault(var1, PlayerUnlock.UnlockVisibility.INVISIBLE) != PlayerUnlock.UnlockVisibility.VISIBLE) {
         this.markForVisibilityUpdate(var1, PlayerUnlock.UnlockVisibility.VISIBLE);
      }
   }

   public boolean revoke(Holder<PlayerUnlock> var1) {
      if (!(Boolean)this.obtained.getOrDefault(var1, false)) {
         return false;
      } else {
         this.obtained.put(var1, false);
         this.obtainedChanged.add(var1);
         this.markForVisibilityUpdate(var1, PlayerUnlock.UnlockVisibility.VISIBLE);
         return true;
      }
   }

   private void markForVisibilityUpdate(Holder<PlayerUnlock> var1, PlayerUnlock.UnlockVisibility var2) {
      if (!this.visibility.containsKey(var1) || this.visibility.get(var1) != var2) {
         this.visibility.put(var1, var2);
         this.updatedNodes.add(var1);
      }

   }

   public void flushDirty(ServerPlayer var1) {
      if (this.isFirstPacket) {
         var1.serverLevel().theGame().playerList().broadcastAll(new ClientboundUpdatePlayerUnlocksPacket(var1.getId(), this.isFirstPacket, this.isActiveExclusive, this.visibility, this.obtained));
      }

      if (!this.updatedNodes.isEmpty() || !this.obtainedChanged.isEmpty() || !this.activeExclusiveChanged.isEmpty()) {
         HashMap var2 = new HashMap();
         HashMap var3 = new HashMap();
         HashMap var4 = new HashMap();

         for(Holder var6 : this.updatedNodes) {
            this.updateTreeVisibility(var6, var2);
            var2.put(var6, (PlayerUnlock.UnlockVisibility)this.visibility.get(var6));
         }

         this.updatedNodes.clear();

         for(Holder var9 : this.obtainedChanged) {
            var3.put(var9, (Boolean)this.obtained.getOrDefault(var9, false));
         }

         this.obtainedChanged.clear();

         for(Holder var10 : this.activeExclusiveChanged) {
            var4.put(var10, (Boolean)this.isActiveExclusive.getOrDefault(var10, false));
         }

         this.activeExclusiveChanged.clear();
         if (!var3.isEmpty() || !var2.isEmpty() || !var4.isEmpty()) {
            var1.serverLevel().theGame().playerList().broadcastAll(new ClientboundUpdatePlayerUnlocksPacket(var1.getId(), false, this.isActiveExclusive, var2, var3));
         }
      }

      this.isFirstPacket = false;
   }

   private void updateTreeVisibility(Holder<PlayerUnlock> var1, Map<Holder<PlayerUnlock>, PlayerUnlock.UnlockVisibility> var2) {
      if (((PlayerUnlock)var1.value()).defaultVisibility() != this.visibility.getOrDefault(var1, PlayerUnlock.UnlockVisibility.VISIBLE)) {
         PlayerUnlock.UnlockVisibility var3 = (PlayerUnlock.UnlockVisibility)this.visibility.getOrDefault(var1, PlayerUnlock.UnlockVisibility.VISIBLE);
         if (var3 != PlayerUnlock.UnlockVisibility.INVISIBLE) {
            for(Optional var4 = ((PlayerUnlock)var1.value()).parent(); var4.isPresent(); var4 = ((PlayerUnlock)((Holder)var4.get()).value()).parent()) {
               PlayerUnlock.UnlockVisibility var5 = (PlayerUnlock.UnlockVisibility)this.visibility.getOrDefault(var4.get(), PlayerUnlock.UnlockVisibility.VISIBLE);
               if (var5 == PlayerUnlock.UnlockVisibility.INVISIBLE) {
                  this.visibility.put((Holder)var4.get(), PlayerUnlock.UnlockVisibility.MYSTERY);
                  var2.put((Holder)var4.get(), PlayerUnlock.UnlockVisibility.MYSTERY);
               }
            }

         }
      }
   }

   public boolean isVisibleAtAll(Holder<PlayerUnlock> var1) {
      return this.visibility.getOrDefault(var1, PlayerUnlock.UnlockVisibility.INVISIBLE) == PlayerUnlock.UnlockVisibility.VISIBLE;
   }

   static record Data(Map<Holder<PlayerUnlock>, Boolean> obtained, Map<Holder<PlayerUnlock>, PlayerUnlock.UnlockVisibility> visibility, Map<Holder<PlayerUnlock>, Boolean> isActiveExclusive) {
      final Map<Holder<PlayerUnlock>, Boolean> obtained;
      final Map<Holder<PlayerUnlock>, PlayerUnlock.UnlockVisibility> visibility;
      final Map<Holder<PlayerUnlock>, Boolean> isActiveExclusive;
      public static final Codec<Data> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.unboundedMap(BuiltInRegistries.PLAYER_UNLOCK.holderByNameCodec(), Codec.BOOL).fieldOf("obtained").forGetter(Data::obtained), Codec.unboundedMap(BuiltInRegistries.PLAYER_UNLOCK.holderByNameCodec(), StringRepresentable.fromEnum(PlayerUnlock.UnlockVisibility::values)).fieldOf("visibility").forGetter(Data::visibility), Codec.unboundedMap(BuiltInRegistries.PLAYER_UNLOCK.holderByNameCodec(), Codec.BOOL).fieldOf("isActiveExclusive").forGetter(Data::isActiveExclusive)).apply(var0, Data::new));

      Data(Map<Holder<PlayerUnlock>, Boolean> var1, Map<Holder<PlayerUnlock>, PlayerUnlock.UnlockVisibility> var2, Map<Holder<PlayerUnlock>, Boolean> var3) {
         super();
         this.obtained = var1;
         this.visibility = var2;
         this.isActiveExclusive = var3;
      }
   }
}
