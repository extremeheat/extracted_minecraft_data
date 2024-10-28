package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class TreeNodePosition {
   private final AdvancementNode node;
   @Nullable
   private final TreeNodePosition parent;
   @Nullable
   private final TreeNodePosition previousSibling;
   private final int childIndex;
   private final List<TreeNodePosition> children = Lists.newArrayList();
   private TreeNodePosition ancestor;
   @Nullable
   private TreeNodePosition thread;
   private int x;
   private float y;
   private float mod;
   private float change;
   private float shift;

   public TreeNodePosition(AdvancementNode var1, @Nullable TreeNodePosition var2, @Nullable TreeNodePosition var3, int var4, int var5) {
      super();
      if (var1.advancement().display().isEmpty()) {
         throw new IllegalArgumentException("Can't position an invisible advancement!");
      } else {
         this.node = var1;
         this.parent = var2;
         this.previousSibling = var3;
         this.childIndex = var4;
         this.ancestor = this;
         this.x = var5;
         this.y = -1.0F;
         TreeNodePosition var6 = null;

         AdvancementNode var8;
         for(Iterator var7 = var1.children().iterator(); var7.hasNext(); var6 = this.addChild(var8, var6)) {
            var8 = (AdvancementNode)var7.next();
         }

      }
   }

   @Nullable
   private TreeNodePosition addChild(AdvancementNode var1, @Nullable TreeNodePosition var2) {
      AdvancementNode var4;
      if (var1.advancement().display().isPresent()) {
         var2 = new TreeNodePosition(var1, this, var2, this.children.size() + 1, this.x + 1);
         this.children.add(var2);
      } else {
         for(Iterator var3 = var1.children().iterator(); var3.hasNext(); var2 = this.addChild(var4, var2)) {
            var4 = (AdvancementNode)var3.next();
         }
      }

      return var2;
   }

   private void firstWalk() {
      if (this.children.isEmpty()) {
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
         } else {
            this.y = 0.0F;
         }

      } else {
         TreeNodePosition var1 = null;

         TreeNodePosition var3;
         for(Iterator var2 = this.children.iterator(); var2.hasNext(); var1 = var3.apportion(var1 == null ? var3 : var1)) {
            var3 = (TreeNodePosition)var2.next();
            var3.firstWalk();
         }

         this.executeShifts();
         float var4 = (((TreeNodePosition)this.children.get(0)).y + ((TreeNodePosition)this.children.get(this.children.size() - 1)).y) / 2.0F;
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
            this.mod = this.y - var4;
         } else {
            this.y = var4;
         }

      }
   }

   private float secondWalk(float var1, int var2, float var3) {
      this.y += var1;
      this.x = var2;
      if (this.y < var3) {
         var3 = this.y;
      }

      TreeNodePosition var5;
      for(Iterator var4 = this.children.iterator(); var4.hasNext(); var3 = var5.secondWalk(var1 + this.mod, var2 + 1, var3)) {
         var5 = (TreeNodePosition)var4.next();
      }

      return var3;
   }

   private void thirdWalk(float var1) {
      this.y += var1;
      Iterator var2 = this.children.iterator();

      while(var2.hasNext()) {
         TreeNodePosition var3 = (TreeNodePosition)var2.next();
         var3.thirdWalk(var1);
      }

   }

   private void executeShifts() {
      float var1 = 0.0F;
      float var2 = 0.0F;

      for(int var3 = this.children.size() - 1; var3 >= 0; --var3) {
         TreeNodePosition var4 = (TreeNodePosition)this.children.get(var3);
         var4.y += var1;
         var4.mod += var1;
         var2 += var4.change;
         var1 += var4.shift + var2;
      }

   }

   @Nullable
   private TreeNodePosition previousOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? (TreeNodePosition)this.children.get(0) : null;
      }
   }

   @Nullable
   private TreeNodePosition nextOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? (TreeNodePosition)this.children.get(this.children.size() - 1) : null;
      }
   }

   private TreeNodePosition apportion(TreeNodePosition var1) {
      if (this.previousSibling == null) {
         return var1;
      } else {
         TreeNodePosition var2 = this;
         TreeNodePosition var3 = this;
         TreeNodePosition var4 = this.previousSibling;
         TreeNodePosition var5 = (TreeNodePosition)this.parent.children.get(0);
         float var6 = this.mod;
         float var7 = this.mod;
         float var8 = var4.mod;

         float var9;
         for(var9 = var5.mod; var4.nextOrThread() != null && var2.previousOrThread() != null; var7 += var3.mod) {
            var4 = var4.nextOrThread();
            var2 = var2.previousOrThread();
            var5 = var5.previousOrThread();
            var3 = var3.nextOrThread();
            var3.ancestor = this;
            float var10 = var4.y + var8 - (var2.y + var6) + 1.0F;
            if (var10 > 0.0F) {
               var4.getAncestor(this, var1).moveSubtree(this, var10);
               var6 += var10;
               var7 += var10;
            }

            var8 += var4.mod;
            var6 += var2.mod;
            var9 += var5.mod;
         }

         if (var4.nextOrThread() != null && var3.nextOrThread() == null) {
            var3.thread = var4.nextOrThread();
            var3.mod += var8 - var7;
         } else {
            if (var2.previousOrThread() != null && var5.previousOrThread() == null) {
               var5.thread = var2.previousOrThread();
               var5.mod += var6 - var9;
            }

            var1 = this;
         }

         return var1;
      }
   }

   private void moveSubtree(TreeNodePosition var1, float var2) {
      float var3 = (float)(var1.childIndex - this.childIndex);
      if (var3 != 0.0F) {
         var1.change -= var2 / var3;
         this.change += var2 / var3;
      }

      var1.shift += var2;
      var1.y += var2;
      var1.mod += var2;
   }

   private TreeNodePosition getAncestor(TreeNodePosition var1, TreeNodePosition var2) {
      return this.ancestor != null && var1.parent.children.contains(this.ancestor) ? this.ancestor : var2;
   }

   private void finalizePosition() {
      this.node.advancement().display().ifPresent((var1x) -> {
         var1x.setLocation((float)this.x, this.y);
      });
      if (!this.children.isEmpty()) {
         Iterator var1 = this.children.iterator();

         while(var1.hasNext()) {
            TreeNodePosition var2 = (TreeNodePosition)var1.next();
            var2.finalizePosition();
         }
      }

   }

   public static void run(AdvancementNode var0) {
      if (var0.advancement().display().isEmpty()) {
         throw new IllegalArgumentException("Can't position children of an invisible root!");
      } else {
         TreeNodePosition var1 = new TreeNodePosition(var0, (TreeNodePosition)null, (TreeNodePosition)null, 1, 0);
         var1.firstWalk();
         float var2 = var1.secondWalk(0.0F, 0, var1.y);
         if (var2 < 0.0F) {
            var1.thirdWalk(-var2);
         }

         var1.finalizePosition();
      }
   }
}
