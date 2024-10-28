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

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         boolean var10000;
         if (var1 instanceof AdvancementNode) {
            AdvancementNode var2 = (AdvancementNode)var1;
            if (this.holder.equals(var2.holder)) {
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
