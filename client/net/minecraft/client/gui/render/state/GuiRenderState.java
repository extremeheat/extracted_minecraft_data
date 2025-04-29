package net.minecraft.client.gui.render.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.apache.commons.lang3.mutable.MutableInt;

public class GuiRenderState {
   private static final int DEBUG_RECTANGLE_COLOR = 2000962815;
   private final List<Node> strata = new ArrayList();
   private int firstStratumAfterBlur = 2147483647;
   private Node current;
   private final Set<Object> itemModelIdentities = new HashSet();
   @Nullable
   private ScreenRectangle lastElementBounds;

   public GuiRenderState() {
      super();
      this.nextStratum();
   }

   public void nextStratum() {
      this.current = new Node((Node)null);
      this.strata.add(this.current);
   }

   public void blurBeforeThisStratum() {
      if (this.firstStratumAfterBlur != 2147483647) {
         throw new IllegalStateException("Can only blur once per frame");
      } else {
         this.firstStratumAfterBlur = this.strata.size() - 1;
      }
   }

   public void up() {
      if (this.current.up == null) {
         this.current.up = new Node(this.current);
      }

      this.current = this.current.up;
   }

   public void down() {
      if (this.current.down == null) {
         this.current.down = new Node(this.current);
      }

      this.current = this.current.down;
   }

   public void submitItem(GuiItemRenderState var1) {
      if (this.findAppropriateNode(var1)) {
         this.itemModelIdentities.add(var1.itemStackRenderState().getModelIdentity());
         this.current.submitItem(var1);
         this.sumbitDebugRectangleIfEnabled(var1.bounds());
      }
   }

   public void submitText(GuiTextRenderState var1) {
      if (this.findAppropriateNode(var1)) {
         this.current.submitText(var1);
         this.sumbitDebugRectangleIfEnabled(var1.bounds());
      }
   }

   public void submitPicturesInPictureState(PictureInPictureRenderState var1) {
      if (this.findAppropriateNode(var1)) {
         this.current.submitPicturesInPictureState(var1);
         this.sumbitDebugRectangleIfEnabled(var1.bounds());
      }
   }

   public void submitGuiElement(GuiElementRenderState var1) {
      if (this.findAppropriateNode(var1)) {
         this.current.submitGuiElement(var1);
         this.sumbitDebugRectangleIfEnabled(var1.bounds());
      }
   }

   private void sumbitDebugRectangleIfEnabled(@Nullable ScreenRectangle var1) {
   }

   private boolean findAppropriateNode(ScreenArea var1) {
      ScreenRectangle var2 = var1.bounds();
      if (var2 == null) {
         return false;
      } else {
         if (this.lastElementBounds != null && this.lastElementBounds.encompasses(var2)) {
            this.up();
         } else {
            this.navigateToAboveHighestElementWithIntersectingBounds(var2);
         }

         this.lastElementBounds = var2;
         return true;
      }
   }

   private void navigateToAboveHighestElementWithIntersectingBounds(ScreenRectangle var1) {
      Node var2;
      for(var2 = (Node)this.strata.getLast(); var2.up != null; var2 = var2.up) {
      }

      boolean var3 = false;

      while(!var3) {
         var3 = this.hasIntersection(var1, var2.elementStates) || this.hasIntersection(var1, var2.itemStates) || this.hasIntersection(var1, var2.textStates) || this.hasIntersection(var1, var2.picturesInPictureStates);
         if (var2.parent == null) {
            break;
         }

         if (!var3) {
            var2 = var2.parent;
         }
      }

      this.current = var2;
      if (var3) {
         this.up();
      }

   }

   private boolean hasIntersection(ScreenRectangle var1, @Nullable List<? extends ScreenArea> var2) {
      if (var2 != null) {
         for(ScreenArea var4 : var2) {
            ScreenRectangle var5 = var4.bounds();
            if (var5 != null && var5.intersects(var1)) {
               return true;
            }
         }
      }

      return false;
   }

   public void submitGuiElementToCurrentLayer(GuiElementRenderState var1) {
      this.current.submitGuiElement(var1);
   }

   public Set<Object> getItemModelIdentities() {
      return this.itemModelIdentities;
   }

   public void forEachElement(LayeredElementConsumer var1, TraverseRange var2) {
      MutableInt var3 = new MutableInt(0);
      this.traverse((Consumer)((var2x) -> {
         if (var2x.elementStates != null) {
            var3.increment();
            int var3x = var3.intValue();

            for(GuiElementRenderState var5 : var2x.elementStates) {
               var1.accept(var5, var3x);
            }
         }

      }), var2);
   }

   public void forEachItem(Consumer<GuiItemRenderState> var1) {
      Node var2 = this.current;
      this.traverse((Consumer)((var2x) -> {
         if (var2x.itemStates != null) {
            this.current = var2x;

            for(GuiItemRenderState var4 : var2x.itemStates) {
               var1.accept(var4);
            }
         }

      }), GuiRenderState.TraverseRange.ALL);
      this.current = var2;
   }

   public void forEachText(Consumer<GuiTextRenderState> var1) {
      Node var2 = this.current;
      this.traverse((Consumer)((var2x) -> {
         if (var2x.textStates != null) {
            for(GuiTextRenderState var4 : var2x.textStates) {
               this.current = var2x;
               var1.accept(var4);
            }
         }

      }), GuiRenderState.TraverseRange.ALL);
      this.current = var2;
   }

   public void forEachPictureInPicture(Consumer<PictureInPictureRenderState> var1) {
      Node var2 = this.current;
      this.traverse((Consumer)((var2x) -> {
         if (var2x.picturesInPictureStates != null) {
            this.current = var2x;

            for(PictureInPictureRenderState var4 : var2x.picturesInPictureStates) {
               var1.accept(var4);
            }
         }

      }), GuiRenderState.TraverseRange.ALL);
      this.current = var2;
   }

   public void sortElements(Comparator<GuiElementRenderState> var1) {
      this.traverse((Consumer)((var1x) -> {
         if (var1x.elementStates != null) {
            var1x.elementStates.sort(var1);
         }

      }), GuiRenderState.TraverseRange.ALL);
   }

   private void traverse(Consumer<Node> var1, TraverseRange var2) {
      int var3 = 0;
      int var4 = this.strata.size();
      if (var2 == GuiRenderState.TraverseRange.BEFORE_BLUR) {
         var4 = Math.min(this.firstStratumAfterBlur, this.strata.size());
      } else if (var2 == GuiRenderState.TraverseRange.AFTER_BLUR) {
         var3 = this.firstStratumAfterBlur;
      }

      for(int var5 = var3; var5 < var4; ++var5) {
         Node var6 = (Node)this.strata.get(var5);
         this.traverse(var6, var1);
      }

   }

   private void traverse(Node var1, Consumer<Node> var2) {
      if (var1.down != null) {
         this.traverse(var1.down, var2);
      }

      var2.accept(var1);
      if (var1.up != null) {
         this.traverse(var1.up, var2);
      }

   }

   public void reset() {
      this.itemModelIdentities.clear();
      this.strata.clear();
      this.firstStratumAfterBlur = 2147483647;
      this.nextStratum();
   }

   static class Node {
      @Nullable
      public final Node parent;
      @Nullable
      public Node up;
      @Nullable
      public Node down;
      @Nullable
      public List<GuiElementRenderState> elementStates;
      @Nullable
      public List<GuiItemRenderState> itemStates;
      @Nullable
      public List<GuiTextRenderState> textStates;
      @Nullable
      public List<PictureInPictureRenderState> picturesInPictureStates;

      Node(@Nullable Node var1) {
         super();
         this.parent = var1;
      }

      public void submitItem(GuiItemRenderState var1) {
         if (this.itemStates == null) {
            this.itemStates = new ArrayList();
         }

         this.itemStates.add(var1);
      }

      public void submitText(GuiTextRenderState var1) {
         if (this.textStates == null) {
            this.textStates = new ArrayList();
         }

         this.textStates.add(var1);
      }

      public void submitPicturesInPictureState(PictureInPictureRenderState var1) {
         if (this.picturesInPictureStates == null) {
            this.picturesInPictureStates = new ArrayList();
         }

         this.picturesInPictureStates.add(var1);
      }

      public void submitGuiElement(GuiElementRenderState var1) {
         if (this.elementStates == null) {
            this.elementStates = new ArrayList();
         }

         this.elementStates.add(var1);
      }
   }

   public static enum TraverseRange {
      ALL,
      BEFORE_BLUR,
      AFTER_BLUR;

      private TraverseRange() {
      }

      // $FF: synthetic method
      private static TraverseRange[] $values() {
         return new TraverseRange[]{ALL, BEFORE_BLUR, AFTER_BLUR};
      }
   }

   public interface LayeredElementConsumer {
      void accept(GuiElementRenderState var1, int var2);
   }
}
