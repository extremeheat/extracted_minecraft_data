package net.minecraft.client.gui.screens.unlocks;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.server.players.PlayerUnlock;
import org.slf4j.Logger;

public class PlayerUnlocksTree {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<Holder<PlayerUnlock>, List<Holder<PlayerUnlock>>> nodes = new Object2ObjectOpenHashMap();
   private final Set<Holder<PlayerUnlock>> roots = new ObjectLinkedOpenHashSet();
   private final Set<Holder<PlayerUnlock>> tasks = new ObjectLinkedOpenHashSet();
   @Nullable
   private Listener listener;

   public PlayerUnlocksTree() {
      super();
   }

   private void remove(Holder<PlayerUnlock> var1) {
      for(Holder var4 : (List)this.nodes.get(var1)) {
         this.remove(var4);
      }

      LOGGER.info("Forgot about unlock {}", var1);
      this.nodes.remove(var1);
      if (((PlayerUnlock)var1.value()).parent().isEmpty()) {
         this.roots.remove(var1);
         if (this.listener != null) {
            this.listener.onRemoveUnlocksRoot(var1);
         }
      } else {
         this.tasks.remove(var1);
         if (this.listener != null) {
            this.listener.onRemoveUnlock(var1);
         }
      }

   }

   public void remove(Set<Holder<PlayerUnlock>> var1) {
      for(Holder var3 : var1) {
         if (!this.nodes.containsKey(var3)) {
            LOGGER.warn("Told to remove unlock {} but I don't know what that is", var3);
         } else {
            this.remove(var3);
         }
      }

   }

   public void addAll(Collection<Holder<PlayerUnlock>> var1) {
      ArrayList var2 = new ArrayList(var1);

      while(!var2.isEmpty()) {
         if (!var2.removeIf(this::tryInsert)) {
            LOGGER.error("Couldn't load unlocks: {}", var2);
            break;
         }
      }

   }

   private boolean tryInsert(Holder<PlayerUnlock> var1) {
      Optional var2 = ((PlayerUnlock)var1.value()).parent();
      Map var10001 = this.nodes;
      Objects.requireNonNull(var10001);
      List var3 = (List)var2.map(var10001::get).orElse((Object)null);
      if (var3 == null && var2.isPresent()) {
         return false;
      } else {
         if (var3 != null) {
            var3.add(var1);
         }

         this.nodes.put(var1, new ArrayList());
         if (var2.isEmpty()) {
            this.roots.add(var1);
            if (this.listener != null) {
               this.listener.onAddUnlocksRoot(var1);
            }
         } else {
            this.tasks.add(var1);
            if (this.listener != null) {
               this.listener.onAddUnlock(var1);
            }
         }

         return true;
      }
   }

   public void clear() {
      this.nodes.clear();
      this.roots.clear();
      this.tasks.clear();
      if (this.listener != null) {
         this.listener.onUnlocksCleared();
      }

   }

   public Iterable<Holder<PlayerUnlock>> roots() {
      return this.roots;
   }

   public List<Holder<PlayerUnlock>> getChildren(Holder<PlayerUnlock> var1) {
      List var2 = (List)this.nodes.get(var1);
      return var2 == null ? List.of() : var2;
   }

   public void setListener(@Nullable Listener var1) {
      this.listener = var1;
      if (var1 != null) {
         for(Holder var3 : this.roots) {
            var1.onAddUnlocksRoot(var3);
         }

         for(Holder var5 : this.tasks) {
            var1.onAddUnlock(var5);
         }
      }

   }

   public interface Listener {
      void onAddUnlocksRoot(Holder<PlayerUnlock> var1);

      void onRemoveUnlocksRoot(Holder<PlayerUnlock> var1);

      void onAddUnlock(Holder<PlayerUnlock> var1);

      void onRemoveUnlock(Holder<PlayerUnlock> var1);

      void onUnlocksCleared();
   }
}
