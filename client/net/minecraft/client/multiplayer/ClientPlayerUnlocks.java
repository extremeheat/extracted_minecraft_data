package net.minecraft.client.multiplayer;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.unlocks.PlayerUnlocksTree;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundUpdatePlayerUnlocksPacket;
import net.minecraft.server.players.PlayerUnlock;
import org.slf4j.Logger;

public class ClientPlayerUnlocks {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Object2BooleanMap<Holder<PlayerUnlock>> unlocks = new Object2BooleanOpenHashMap();
   private final Map<Holder<PlayerUnlock>, PlayerUnlock.UnlockVisibility> visibility = new HashMap();
   private final Object2BooleanOpenHashMap<Holder<PlayerUnlock>> activeExclusive = new Object2BooleanOpenHashMap();
   private final PlayerUnlocksTree tree = new PlayerUnlocksTree();
   @Nullable
   private Listener listener;
   @Nullable
   private Holder<PlayerUnlock> selectedTab;

   public ClientPlayerUnlocks() {
      super();
      HashSet var1 = new HashSet();
      BuiltInRegistries.PLAYER_UNLOCK.listElements().forEach((var2) -> this.addUnlock(var2, var1));
   }

   private void addUnlock(Holder<PlayerUnlock> var1, Set<Holder<PlayerUnlock>> var2) {
      if (!var2.contains(var1)) {
         Optional var3 = ((PlayerUnlock)var1.value()).parent();
         if (var3.isPresent()) {
            this.addUnlock((Holder)var3.get(), var2);
         }

         this.tree.addAll(List.of(var1));
         var2.add(var1);
      }
   }

   public void update(ClientboundUpdatePlayerUnlocksPacket var1) {
      if (var1.reset()) {
         this.unlocks.clear();
         this.visibility.clear();
         this.activeExclusive.clear();
      }

      this.unlocks.putAll(var1.obtained());
      this.visibility.putAll(var1.visibility());
      this.activeExclusive.putAll(var1.isActiveExclusive());
      if (this.listener != null) {
         Object2BooleanMap var10000 = this.unlocks;
         Listener var10001 = this.listener;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::onUnlockStatusChange);
         Map var2 = this.visibility;
         var10001 = this.listener;
         Objects.requireNonNull(var10001);
         var2.forEach(var10001::onVisibilityStatusChange);
         Object2BooleanOpenHashMap var3 = this.activeExclusive;
         var10001 = this.listener;
         Objects.requireNonNull(var10001);
         var3.forEach(var10001::onActiveExclusiveStatusChange);
      }

   }

   public void setSelectedTab(@Nullable Holder<PlayerUnlock> var1) {
      if (this.selectedTab != var1) {
         this.selectedTab = var1;
         if (this.listener != null) {
            this.listener.onSelectedTabChanged(var1);
         }
      }

   }

   public void setListener(@Nullable Listener var1) {
      this.listener = var1;
      this.tree.setListener(var1);
      if (var1 != null) {
         Object2BooleanMap var10000 = this.unlocks;
         Objects.requireNonNull(var1);
         var10000.forEach(var1::onUnlockStatusChange);
         Map var2 = this.visibility;
         Objects.requireNonNull(var1);
         var2.forEach(var1::onVisibilityStatusChange);
         Object2BooleanOpenHashMap var3 = this.activeExclusive;
         Objects.requireNonNull(var1);
         var3.forEach(var1::onActiveExclusiveStatusChange);
         var1.onSelectedTabChanged(this.selectedTab);
      }

   }

   public PlayerUnlocksTree getTree() {
      return this.tree;
   }

   public Set<Holder<PlayerUnlock>> getActiveUnlocks() {
      HashSet var1 = new HashSet();
      this.unlocks.forEach((var1x, var2) -> {
         if (var2) {
            var1.add(var1x);
         }

      });
      this.unlocks.forEach((var2, var3) -> {
         if (var3) {
            if (!((PlayerUnlock)var2.value()).disables().isEmpty()) {
               List var10000 = ((PlayerUnlock)var2.value()).disables();
               Objects.requireNonNull(var1);
               var10000.forEach(var1::remove);
            }

            if (!((PlayerUnlock)var2.value()).exclusiveKey().isEmpty() && !this.activeExclusive.getOrDefault(var2, false)) {
               var1.remove(var2);
            }
         }

      });
      return var1;
   }

   public boolean isUnlocked(Holder<PlayerUnlock> var1) {
      return this.unlocks.getOrDefault(var1, false);
   }

   public boolean isActive(Holder<PlayerUnlock> var1) {
      return this.getActiveUnlocks().contains(var1);
   }

   public PlayerUnlock.UnlockVisibility getVisibility(Holder<PlayerUnlock> var1) {
      return (PlayerUnlock.UnlockVisibility)this.visibility.getOrDefault(var1, PlayerUnlock.UnlockVisibility.INVISIBLE);
   }

   public boolean isActiveExclusive(Holder<PlayerUnlock> var1) {
      return this.activeExclusive.getOrDefault(var1, false);
   }

   public boolean isVisibleAtAll(Holder<PlayerUnlock> var1) {
      if (this.getVisibility(var1) == PlayerUnlock.UnlockVisibility.INVISIBLE) {
         return false;
      } else {
         return !((PlayerUnlock)var1.value()).parent().isPresent() || this.isVisibleAtAll((Holder)((PlayerUnlock)var1.value()).parent().get());
      }
   }

   public boolean isUnlockable(Holder<PlayerUnlock> var1) {
      return this.getVisibility(var1) == PlayerUnlock.UnlockVisibility.VISIBLE;
   }

   public interface Listener extends PlayerUnlocksTree.Listener {
      void onUnlockStatusChange(Holder<PlayerUnlock> var1, boolean var2);

      void onVisibilityStatusChange(Holder<PlayerUnlock> var1, PlayerUnlock.UnlockVisibility var2);

      void onActiveExclusiveStatusChange(Holder<PlayerUnlock> var1, boolean var2);

      void onSelectedTabChanged(@Nullable Holder<PlayerUnlock> var1);
   }
}
