package net.minecraft.advancements;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class AdvancementTree {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<Identifier, AdvancementNode> nodes = new Object2ObjectLinkedOpenHashMap();

   public AdvancementTree() {
      super();
   }

   private void remove(final AdvancementNode node) {
      for(AdvancementNode child : node.children()) {
         this.remove(child);
      }

      LOGGER.info("Forgot about advancement {}", node.holder());
      this.nodes.remove(node.holder().id());
   }

   public void remove(final Set<Identifier> ids) {
      for(Identifier id : ids) {
         AdvancementNode advancement = (AdvancementNode)this.nodes.get(id);
         if (advancement == null) {
            LOGGER.warn("Told to remove advancement {} but I don't know what that is", id);
         } else {
            this.remove(advancement);
         }
      }

   }

   public void addAll(final Iterable<AdvancementHolder> advancements) {
      List<AdvancementHolder> advancementsToAdd = Lists.newArrayList(advancements);

      while(!advancementsToAdd.isEmpty()) {
         if (!advancementsToAdd.removeIf(this::tryInsert)) {
            LOGGER.error("Couldn't load advancements: {}", advancementsToAdd);
            break;
         }
      }

      LOGGER.info("Loaded {} advancements", this.nodes.size());
   }

   private boolean tryInsert(final AdvancementHolder holder) {
      Identifier parentId = (Identifier)holder.value().parent().orElse((Object)null);
      AdvancementNode parentNode;
      if (parentId != null) {
         parentNode = (AdvancementNode)this.nodes.get(parentId);
         if (parentNode == null) {
            return false;
         }
      } else {
         parentNode = null;
      }

      AdvancementNode node = new AdvancementNode(holder, parentNode);
      if (parentNode != null) {
         parentNode.addChild(node);
      }

      this.nodes.put(holder.id(), node);
      return true;
   }

   public void clear() {
      this.nodes.clear();
   }

   public Iterable<AdvancementNode> roots() {
      return Iterables.filter(this.nodes(), AdvancementNode::isRoot);
   }

   public Iterable<AdvancementNode> tasks() {
      return Iterables.filter(this.nodes(), AdvancementNode::isTask);
   }

   public Collection<AdvancementNode> nodes() {
      return this.nodes.values();
   }

   public @Nullable AdvancementNode get(final Identifier id) {
      return (AdvancementNode)this.nodes.get(id);
   }

   public @Nullable AdvancementNode get(final AdvancementHolder advancement) {
      return (AdvancementNode)this.nodes.get(advancement.id());
   }

   public void repositionNodes() {
      for(AdvancementNode root : this.roots()) {
         if (root.holder().value().display().isPresent()) {
            TreeNodePosition.run(root);
         }
      }

   }
}
