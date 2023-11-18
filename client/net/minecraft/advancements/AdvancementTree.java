package net.minecraft.advancements;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public class AdvancementTree {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<ResourceLocation, AdvancementNode> nodes = new Object2ObjectOpenHashMap();
   private final Set<AdvancementNode> roots = new ObjectLinkedOpenHashSet();
   private final Set<AdvancementNode> tasks = new ObjectLinkedOpenHashSet();
   @Nullable
   private AdvancementTree.Listener listener;

   public AdvancementTree() {
      super();
   }

   private void remove(AdvancementNode var1) {
      for(AdvancementNode var3 : var1.children()) {
         this.remove(var3);
      }

      LOGGER.info("Forgot about advancement {}", var1.holder());
      this.nodes.remove(var1.holder().id());
      if (var1.parent() == null) {
         this.roots.remove(var1);
         if (this.listener != null) {
            this.listener.onRemoveAdvancementRoot(var1);
         }
      } else {
         this.tasks.remove(var1);
         if (this.listener != null) {
            this.listener.onRemoveAdvancementTask(var1);
         }
      }
   }

   public void remove(Set<ResourceLocation> var1) {
      for(ResourceLocation var3 : var1) {
         AdvancementNode var4 = this.nodes.get(var3);
         if (var4 == null) {
            LOGGER.warn("Told to remove advancement {} but I don't know what that is", var3);
         } else {
            this.remove(var4);
         }
      }
   }

   public void addAll(Collection<AdvancementHolder> var1) {
      ArrayList var2 = new ArrayList(var1);

      while(!var2.isEmpty()) {
         if (!var2.removeIf(this::tryInsert)) {
            LOGGER.error("Couldn't load advancements: {}", var2);
            break;
         }
      }

      LOGGER.info("Loaded {} advancements", this.nodes.size());
   }

   private boolean tryInsert(AdvancementHolder var1) {
      Optional var2 = var1.value().parent();
      AdvancementNode var3 = var2.map(this.nodes::get).orElse(null);
      if (var3 == null && var2.isPresent()) {
         return false;
      } else {
         AdvancementNode var4 = new AdvancementNode(var1, var3);
         if (var3 != null) {
            var3.addChild(var4);
         }

         this.nodes.put(var1.id(), var4);
         if (var3 == null) {
            this.roots.add(var4);
            if (this.listener != null) {
               this.listener.onAddAdvancementRoot(var4);
            }
         } else {
            this.tasks.add(var4);
            if (this.listener != null) {
               this.listener.onAddAdvancementTask(var4);
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
         this.listener.onAdvancementsCleared();
      }
   }

   public Iterable<AdvancementNode> roots() {
      return this.roots;
   }

   public Collection<AdvancementNode> nodes() {
      return this.nodes.values();
   }

   @Nullable
   public AdvancementNode get(ResourceLocation var1) {
      return this.nodes.get(var1);
   }

   @Nullable
   public AdvancementNode get(AdvancementHolder var1) {
      return this.nodes.get(var1.id());
   }

   public void setListener(@Nullable AdvancementTree.Listener var1) {
      this.listener = var1;
      if (var1 != null) {
         for(AdvancementNode var3 : this.roots) {
            var1.onAddAdvancementRoot(var3);
         }

         for(AdvancementNode var5 : this.tasks) {
            var1.onAddAdvancementTask(var5);
         }
      }
   }

   public interface Listener {
      void onAddAdvancementRoot(AdvancementNode var1);

      void onRemoveAdvancementRoot(AdvancementNode var1);

      void onAddAdvancementTask(AdvancementNode var1);

      void onRemoveAdvancementTask(AdvancementNode var1);

      void onAdvancementsCleared();
   }
}
