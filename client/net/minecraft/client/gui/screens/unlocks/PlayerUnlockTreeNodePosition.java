package net.minecraft.client.gui.screens.unlocks;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.multiplayer.ClientPlayerUnlocks;
import net.minecraft.core.Holder;
import net.minecraft.server.players.PlayerUnlock;

public class PlayerUnlockTreeNodePosition {
   private final Holder<PlayerUnlock> node;
   @Nullable
   private final PlayerUnlockTreeNodePosition parent;
   @Nullable
   private final PlayerUnlockTreeNodePosition previousSibling;
   private final int childIndex;
   private final List<PlayerUnlockTreeNodePosition> children = Lists.newArrayList();
   private PlayerUnlockTreeNodePosition ancestor;
   @Nullable
   private PlayerUnlockTreeNodePosition thread;
   private int x;
   private float y;
   private float mod;
   private float change;
   private float shift;

   public PlayerUnlockTreeNodePosition(Holder<PlayerUnlock> var1, @Nullable PlayerUnlockTreeNodePosition var2, @Nullable PlayerUnlockTreeNodePosition var3, ClientPlayerUnlocks var4, int var5, int var6) {
      super();
      this.node = var1;
      this.parent = var2;
      this.previousSibling = var3;
      this.childIndex = var5;
      this.ancestor = this;
      this.x = var6;
      this.y = -1.0F;
      PlayerUnlockTreeNodePosition var7 = null;

      for(Holder var9 : var4.getTree().getChildren(var1)) {
         var7 = this.addChild(var9, var4, var7);
      }

   }

   private PlayerUnlockTreeNodePosition addChild(Holder<PlayerUnlock> var1, ClientPlayerUnlocks var2, @Nullable PlayerUnlockTreeNodePosition var3) {
      if (var2.isVisibleAtAll(var1)) {
         var3 = new PlayerUnlockTreeNodePosition(var1, this, var3, var2, this.children.size() + 1, this.x + 1);
         this.children.add(var3);
      } else {
         for(Holder var5 : var2.getTree().getChildren(var1)) {
            var3 = this.addChild(var5, var2, var3);
         }
      }

      return var3;
   }

   private void firstWalk() {
      if (this.children.isEmpty()) {
         if (this.previousSibling != null) {
            this.y = this.previousSibling.y + 1.0F;
         } else {
            this.y = 0.0F;
         }

      } else {
         PlayerUnlockTreeNodePosition var1 = null;

         for(PlayerUnlockTreeNodePosition var3 : this.children) {
            var3.firstWalk();
            var1 = var3.apportion(var1 == null ? var3 : var1);
         }

         this.executeShifts();
         float var4 = (((PlayerUnlockTreeNodePosition)this.children.get(0)).y + ((PlayerUnlockTreeNodePosition)this.children.get(this.children.size() - 1)).y) / 2.0F;
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

      for(PlayerUnlockTreeNodePosition var5 : this.children) {
         var3 = var5.secondWalk(var1 + this.mod, var2 + 1, var3);
      }

      return var3;
   }

   private void thirdWalk(float var1) {
      this.y += var1;

      for(PlayerUnlockTreeNodePosition var3 : this.children) {
         var3.thirdWalk(var1);
      }

   }

   private void executeShifts() {
      float var1 = 0.0F;
      float var2 = 0.0F;

      for(int var3 = this.children.size() - 1; var3 >= 0; --var3) {
         PlayerUnlockTreeNodePosition var4 = (PlayerUnlockTreeNodePosition)this.children.get(var3);
         var4.y += var1;
         var4.mod += var1;
         var2 += var4.change;
         var1 += var4.shift + var2;
      }

   }

   @Nullable
   private PlayerUnlockTreeNodePosition previousOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? (PlayerUnlockTreeNodePosition)this.children.get(0) : null;
      }
   }

   @Nullable
   private PlayerUnlockTreeNodePosition nextOrThread() {
      if (this.thread != null) {
         return this.thread;
      } else {
         return !this.children.isEmpty() ? (PlayerUnlockTreeNodePosition)this.children.get(this.children.size() - 1) : null;
      }
   }

   private PlayerUnlockTreeNodePosition apportion(PlayerUnlockTreeNodePosition var1) {
      if (this.previousSibling == null) {
         return var1;
      } else {
         PlayerUnlockTreeNodePosition var2 = this;
         PlayerUnlockTreeNodePosition var3 = this;
         PlayerUnlockTreeNodePosition var4 = this.previousSibling;
         PlayerUnlockTreeNodePosition var5 = (PlayerUnlockTreeNodePosition)this.parent.children.get(0);
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

   private void moveSubtree(PlayerUnlockTreeNodePosition var1, float var2) {
      float var3 = (float)(var1.childIndex - this.childIndex);
      if (var3 != 0.0F) {
         var1.change -= var2 / var3;
         this.change += var2 / var3;
      }

      var1.shift += var2;
      var1.y += var2;
      var1.mod += var2;
   }

   private PlayerUnlockTreeNodePosition getAncestor(PlayerUnlockTreeNodePosition var1, PlayerUnlockTreeNodePosition var2) {
      return this.ancestor != null && var1.parent.children.contains(this.ancestor) ? this.ancestor : var2;
   }

   private void finalizePosition() {
      DisplayInfo var1 = ((PlayerUnlock)this.node.value()).display();
      var1.setLocation((float)this.x, this.y);
      if (!this.children.isEmpty()) {
         for(PlayerUnlockTreeNodePosition var3 : this.children) {
            var3.finalizePosition();
         }
      }

   }

   public static void run(Holder<PlayerUnlock> var0, ClientPlayerUnlocks var1) {
      PlayerUnlockTreeNodePosition var2 = new PlayerUnlockTreeNodePosition(var0, (PlayerUnlockTreeNodePosition)null, (PlayerUnlockTreeNodePosition)null, var1, 1, 0);
      var2.firstWalk();
      float var3 = var2.secondWalk(0.0F, 0, var2.y);
      if (var3 < 0.0F) {
         var2.thirdWalk(-var3);
      }

      var2.finalizePosition();
   }
}
