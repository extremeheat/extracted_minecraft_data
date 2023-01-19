package net.minecraft.advancements;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public class TreeNodePosition {
   private final Advancement advancement;
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

   public TreeNodePosition(Advancement var1, @Nullable TreeNodePosition var2, @Nullable TreeNodePosition var3, int var4, int var5) {
      super();
      if (var1.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position an invisible advancement!");
      } else {
         this.advancement = var1;
         this.parent = var2;
         this.previousSibling = var3;
         this.childIndex = var4;
         this.ancestor = this;
         this.x = var5;
         this.y = -1.0F;
         TreeNodePosition var6 = null;

         for(Advancement var8 : var1.getChildren()) {
            var6 = this.addChild(var8, var6);
         }
      }
   }

   @Nullable
   private TreeNodePosition addChild(Advancement var1, @Nullable TreeNodePosition var2) {
      if (var1.getDisplay() != null) {
         var2 = new TreeNodePosition(var1, this, var2, this.children.size() + 1, this.x + 1);
         this.children.add(var2);
      } else {
         for(Advancement var4 : var1.getChildren()) {
            var2 = this.addChild(var4, var2);
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

         for(TreeNodePosition var3 : this.children) {
            var3.firstWalk();
            var1 = var3.apportion(var1 == null ? var3 : var1);
         }

         this.executeShifts();
         float var4 = (this.children.get(0).y + this.children.get(this.children.size() - 1).y) / 2.0F;
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

      for(TreeNodePosition var5 : this.children) {
         var3 = var5.secondWalk(var1 + this.mod, var2 + 1, var3);
      }

      return var3;
   }

   private void thirdWalk(float var1) {
      this.y += var1;

      for(TreeNodePosition var3 : this.children) {
         var3.thirdWalk(var1);
      }
   }

   private void executeShifts() {
      float var1 = 0.0F;
      float var2 = 0.0F;

      for(int var3 = this.children.size() - 1; var3 >= 0; --var3) {
         TreeNodePosition var4 = this.children.get(var3);
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
         return !this.children.isEmpty() ? this.children.get(0) : null;
      }
   }

   @Nullable
   private TreeNodePosition nextOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? this.children.get(this.children.size() - 1) : null;
      }
   }

   private TreeNodePosition apportion(TreeNodePosition var1) {
      if (this.previousSibling == null) {
         return var1;
      } else {
         TreeNodePosition var2 = this;
         TreeNodePosition var3 = this;
         TreeNodePosition var4 = this.previousSibling;
         TreeNodePosition var5 = this.parent.children.get(0);
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
      if (this.advancement.getDisplay() != null) {
         this.advancement.getDisplay().setLocation((float)this.x, this.y);
      }

      if (!this.children.isEmpty()) {
         for(TreeNodePosition var2 : this.children) {
            var2.finalizePosition();
         }
      }
   }

   public static void run(Advancement var0) {
      if (var0.getDisplay() == null) {
         throw new IllegalArgumentException("Can't position children of an invisible root!");
      } else {
         TreeNodePosition var1 = new TreeNodePosition(var0, null, null, 1, 0);
         var1.firstWalk();
         float var2 = var1.secondWalk(0.0F, 0, var1.y);
         if (var2 < 0.0F) {
            var1.thirdWalk(-var2);
         }

         var1.finalizePosition();
      }
   }
}
