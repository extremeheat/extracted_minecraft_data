package net.minecraft.advancements;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
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
   private AdvancementList.Listener listener;

   public AdvancementList() {
      super();
   }

   public void add(Map<ResourceLocation, Advancement.Builder> var1) {
      Function var2 = Functions.forMap(this.advancements, (Object)null);

      label42:
      while(!var1.isEmpty()) {
         boolean var3 = false;
         Iterator var4 = var1.entrySet().iterator();

         Entry var5;
         while(var4.hasNext()) {
            var5 = (Entry)var4.next();
            ResourceLocation var6 = (ResourceLocation)var5.getKey();
            Advancement.Builder var7 = (Advancement.Builder)var5.getValue();
            if (var7.canBuild(var2)) {
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
            var4 = var1.entrySet().iterator();

            while(true) {
               if (!var4.hasNext()) {
                  break label42;
               }

               var5 = (Entry)var4.next();
               LOGGER.error("Couldn't load advancement {}: {}", var5.getKey(), var5.getValue());
            }
         }
      }

      LOGGER.info((String)"Loaded {} advancements", (Object)this.advancements.size());
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

   public interface Listener {
      void onAddAdvancementRoot(Advancement var1);

      void onAddAdvancementTask(Advancement var1);
   }
}
