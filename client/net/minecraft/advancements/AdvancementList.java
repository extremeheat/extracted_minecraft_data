package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, Advancement> advancements = Maps.newHashMap();
   private final Set<Advancement> roots = Sets.newLinkedHashSet();
   private final Set<Advancement> tasks = Sets.newLinkedHashSet();
   @Nullable
   private AdvancementList.Listener listener;

   public AdvancementList() {
      super();
   }

   private void remove(Advancement var1) {
      Iterator var2 = var1.getChildren().iterator();

      while(var2.hasNext()) {
         Advancement var3 = (Advancement)var2.next();
         this.remove(var3);
      }

      LOGGER.info("Forgot about advancement {}", var1.getId());
      this.advancements.remove(var1.getId());
      if (var1.getParent() == null) {
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
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         ResourceLocation var3 = (ResourceLocation)var2.next();
         Advancement var4 = (Advancement)this.advancements.get(var3);
         if (var4 == null) {
            LOGGER.warn("Told to remove advancement {} but I don't know what that is", var3);
         } else {
            this.remove(var4);
         }
      }

   }

   public void add(Map<ResourceLocation, Advancement.Builder> var1) {
      HashMap var2 = Maps.newHashMap(var1);

      label42:
      while(!var2.isEmpty()) {
         boolean var3 = false;
         Iterator var4 = var2.entrySet().iterator();

         Entry var5;
         while(var4.hasNext()) {
            var5 = (Entry)var4.next();
            ResourceLocation var6 = (ResourceLocation)var5.getKey();
            Advancement.Builder var7 = (Advancement.Builder)var5.getValue();
            Map var10001 = this.advancements;
            Objects.requireNonNull(var10001);
            if (var7.canBuild(var10001::get)) {
               Advancement var8 = var7.build(var6);
               this.advancements.put(var6, var8);
               var3 = true;
               var4.remove();
               if (var8.getParent() == null) {
                  this.roots.add(var8);
                  if (this.listener != null) {
                     this.listener.onAddAdvancementRoot(var8);
                  }
               } else {
                  this.tasks.add(var8);
                  if (this.listener != null) {
                     this.listener.onAddAdvancementTask(var8);
                  }
               }
            }
         }

         if (!var3) {
            var4 = var2.entrySet().iterator();

            while(true) {
               if (!var4.hasNext()) {
                  break label42;
               }

               var5 = (Entry)var4.next();
               LOGGER.error("Couldn't load advancement {}: {}", var5.getKey(), var5.getValue());
            }
         }
      }

      LOGGER.info("Loaded {} advancements", this.advancements.size());
   }

   public void clear() {
      this.advancements.clear();
      this.roots.clear();
      this.tasks.clear();
      if (this.listener != null) {
         this.listener.onAdvancementsCleared();
      }

   }

   public Iterable<Advancement> getRoots() {
      return this.roots;
   }

   public Collection<Advancement> getAllAdvancements() {
      return this.advancements.values();
   }

   @Nullable
   public Advancement get(ResourceLocation var1) {
      return (Advancement)this.advancements.get(var1);
   }

   public void setListener(@Nullable AdvancementList.Listener var1) {
      this.listener = var1;
      if (var1 != null) {
         Iterator var2 = this.roots.iterator();

         Advancement var3;
         while(var2.hasNext()) {
            var3 = (Advancement)var2.next();
            var1.onAddAdvancementRoot(var3);
         }

         var2 = this.tasks.iterator();

         while(var2.hasNext()) {
            var3 = (Advancement)var2.next();
            var1.onAddAdvancementTask(var3);
         }
      }

   }

   public interface Listener {
      void onAddAdvancementRoot(Advancement var1);

      void onRemoveAdvancementRoot(Advancement var1);

      void onAddAdvancementTask(Advancement var1);

      void onRemoveAdvancementTask(Advancement var1);

      void onAdvancementsCleared();
   }
}
