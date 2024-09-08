package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.logging.LogUtils;
import java.util.Collection;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.advancements.TreeNodePosition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;

public class ServerAdvancementManager extends SimpleJsonResourceReloadListener<Advancement> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private Map<ResourceLocation, AdvancementHolder> advancements = Map.of();
   private AdvancementTree tree = new AdvancementTree();
   private final HolderLookup.Provider registries;

   public ServerAdvancementManager(HolderLookup.Provider var1) {
      super(var1, Advancement.CODEC, Registries.elementsDirPath(Registries.ADVANCEMENT));
      this.registries = var1;
   }

   protected void apply(Map<ResourceLocation, Advancement> var1, ResourceManager var2, ProfilerFiller var3) {
      Builder var4 = ImmutableMap.builder();
      var1.forEach((var2x, var3x) -> {
         this.validate(var2x, var3x);
         var4.put(var2x, new AdvancementHolder(var2x, var3x));
      });
      this.advancements = var4.buildOrThrow();
      AdvancementTree var5 = new AdvancementTree();
      var5.addAll(this.advancements.values());

      for (AdvancementNode var7 : var5.roots()) {
         if (var7.holder().value().display().isPresent()) {
            TreeNodePosition.run(var7);
         }
      }

      this.tree = var5;
   }

   private void validate(ResourceLocation var1, Advancement var2) {
      ProblemReporter.Collector var3 = new ProblemReporter.Collector();
      var2.validate(var3, this.registries);
      var3.getReport().ifPresent(var1x -> LOGGER.warn("Found validation problems in advancement {}: \n{}", var1, var1x));
   }

   @Nullable
   public AdvancementHolder get(ResourceLocation var1) {
      return this.advancements.get(var1);
   }

   public AdvancementTree tree() {
      return this.tree;
   }

   public Collection<AdvancementHolder> getAllAdvancements() {
      return this.advancements.values();
   }
}
