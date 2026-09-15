package net.minecraft.advancements;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public class AdvancementNode {
   private final AdvancementHolder holder;
   private final @Nullable AdvancementNode parent;
   private final Set<AdvancementNode> children = new ReferenceOpenHashSet();
   private float x;
   private float y;

   @VisibleForTesting
   public AdvancementNode(final AdvancementHolder holder, final @Nullable AdvancementNode parent) {
      super();
      this.holder = holder;
      this.parent = parent;
   }

   public Advancement advancement() {
      return this.holder.value();
   }

   public AdvancementHolder holder() {
      return this.holder;
   }

   public boolean isTask() {
      return this.parent != null;
   }

   public boolean isRoot() {
      return this.parent == null;
   }

   public @Nullable AdvancementNode parent() {
      return this.parent;
   }

   public AdvancementNode root() {
      return getRoot(this);
   }

   public static AdvancementNode getRoot(final AdvancementNode advancement) {
      AdvancementNode root = advancement;

      while(true) {
         AdvancementNode parent = root.parent();
         if (parent == null) {
            return root;
         }

         root = parent;
      }
   }

   public Iterable<AdvancementNode> children() {
      return this.children;
   }

   @VisibleForTesting
   public void addChild(final AdvancementNode child) {
      this.children.add(child);
   }

   public void setLocation(final float x, final float y) {
      this.x = x;
      this.y = y;
   }

   public float x() {
      return this.x;
   }

   public float y() {
      return this.y;
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else {
         boolean var10000;
         if (obj instanceof AdvancementNode) {
            AdvancementNode that = (AdvancementNode)obj;
            if (this.holder.equals(that.holder)) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      }
   }

   public int hashCode() {
      return this.holder.hashCode();
   }

   public String toString() {
      return this.holder.id().toString();
   }
}
