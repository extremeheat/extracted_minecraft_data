package net.minecraft.advancements;

import com.google.common.annotations.VisibleForTesting;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import javax.annotation.Nullable;

public class AdvancementNode {
   private final AdvancementHolder holder;
   @Nullable
   private final AdvancementNode parent;
   private final Set<AdvancementNode> children = new ReferenceOpenHashSet();

   @VisibleForTesting
   public AdvancementNode(AdvancementHolder var1, @Nullable AdvancementNode var2) {
      super();
      this.holder = var1;
      this.parent = var2;
   }

   public Advancement advancement() {
      return this.holder.value();
   }

   public AdvancementHolder holder() {
      return this.holder;
   }

   @Nullable
   public AdvancementNode parent() {
      return this.parent;
   }

   public AdvancementNode root() {
      return getRoot(this);
   }

   public static AdvancementNode getRoot(AdvancementNode var0) {
      AdvancementNode var1 = var0;

      while(true) {
         AdvancementNode var2 = var1.parent();
         if (var2 == null) {
            return var1;
         }

         var1 = var2;
      }
   }

   public Iterable<AdvancementNode> children() {
      return this.children;
   }

   @VisibleForTesting
   public void addChild(AdvancementNode var1) {
      this.children.add(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof AdvancementNode var2 && this.holder.equals(var2.holder)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.holder.hashCode();
   }

   @Override
   public String toString() {
      return this.holder.id().toString();
   }
}
