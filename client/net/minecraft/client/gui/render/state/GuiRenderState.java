package net.minecraft.client.gui.render.state;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.joml.Matrix3x2f;
import org.jspecify.annotations.Nullable;

public class GuiRenderState {
   private static final int DEBUG_RECTANGLE_COLOR = 2000962815;
   private final List<Node> strata = new ArrayList();
   private int firstStratumAfterBlur = 2147483647;
   private Node current;
   private final Set<Object> itemModelIdentities = new HashSet();
   private @Nullable ScreenRectangle lastElementBounds;

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

   public void submitItem(final GuiItemRenderState itemState) {
      if (this.findAppropriateNode(itemState)) {
         this.itemModelIdentities.add(itemState.itemStackRenderState().getModelIdentity());
         this.current.submitItem(itemState);
         this.sumbitDebugRectangleIfEnabled(itemState.bounds());
      }
   }

   public void submitText(final GuiTextRenderState textState) {
      if (this.findAppropriateNode(textState)) {
         this.current.submitText(textState);
         this.sumbitDebugRectangleIfEnabled(textState.bounds());
      }
   }

   public void submitPicturesInPictureState(final PictureInPictureRenderState picturesInPictureState) {
      if (this.findAppropriateNode(picturesInPictureState)) {
         this.current.submitPicturesInPictureState(picturesInPictureState);
         this.sumbitDebugRectangleIfEnabled(picturesInPictureState.bounds());
      }
   }

   public void submitGuiElement(final GuiElementRenderState blitState) {
      if (this.findAppropriateNode(blitState)) {
         this.current.submitGuiElement(blitState);
         this.sumbitDebugRectangleIfEnabled(blitState.bounds());
      }
   }

   private void sumbitDebugRectangleIfEnabled(final @Nullable ScreenRectangle bounds) {
      if (SharedConstants.DEBUG_RENDER_UI_LAYERING_RECTANGLES && bounds != null) {
         this.up();
         this.current.submitGuiElement(new ColoredRectangleRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), new Matrix3x2f(), 0, 0, 10000, 10000, 2000962815, 2000962815, bounds));
      }
   }

   private boolean findAppropriateNode(final ScreenArea screenArea) {
      ScreenRectangle bounds = screenArea.bounds();
      if (bounds == null) {
         return false;
      } else {
         if (this.lastElementBounds != null && this.lastElementBounds.encompasses(bounds)) {
            this.up();
         } else {
            this.navigateToAboveHighestElementWithIntersectingBounds(bounds);
         }

         this.lastElementBounds = bounds;
         return true;
      }
   }

   private void navigateToAboveHighestElementWithIntersectingBounds(final ScreenRectangle bounds) {
      Node node;
      for(node = (Node)this.strata.getLast(); node.up != null; node = node.up) {
      }

      boolean found = false;

      while(!found) {
         found = this.hasIntersection(bounds, node.elementStates) || this.hasIntersection(bounds, node.itemStates) || this.hasIntersection(bounds, node.textStates) || this.hasIntersection(bounds, node.picturesInPictureStates);
         if (node.parent == null) {
            break;
         }

         if (!found) {
            node = node.parent;
         }
      }

      this.current = node;
      if (found) {
         this.up();
      }

   }

   private boolean hasIntersection(final ScreenRectangle bounds, final @Nullable List<? extends ScreenArea> states) {
      if (states != null) {
         for(ScreenArea area : states) {
            ScreenRectangle existingBounds = area.bounds();
            if (existingBounds != null && existingBounds.intersects(bounds)) {
               return true;
            }
         }
      }

      return false;
   }

   public void submitBlitToCurrentLayer(final BlitRenderState blitState) {
      this.current.submitGuiElement(blitState);
   }

   public void submitGlyphToCurrentLayer(final GuiElementRenderState glyphState) {
      this.current.submitGlyph(glyphState);
   }

   public Set<Object> getItemModelIdentities() {
      return this.itemModelIdentities;
   }

   public void forEachElement(final Consumer<GuiElementRenderState> consumer, final TraverseRange range) {
      this.traverse((Consumer)((node) -> {
         if (node.elementStates != null || node.glyphStates != null) {
            if (node.elementStates != null) {
               for(GuiElementRenderState elementState : node.elementStates) {
                  consumer.accept(elementState);
               }
            }

            if (node.glyphStates != null) {
               for(GuiElementRenderState glyphState : node.glyphStates) {
                  consumer.accept(glyphState);
               }
            }

         }
      }), range);
   }

   public void forEachItem(final Consumer<GuiItemRenderState> consumer) {
      Node currentBackup = this.current;
      this.traverse((Consumer)((node) -> {
         if (node.itemStates != null) {
            this.current = node;

            for(GuiItemRenderState itemState : node.itemStates) {
               consumer.accept(itemState);
            }
         }

      }), GuiRenderState.TraverseRange.ALL);
      this.current = currentBackup;
   }

   public void forEachText(final Consumer<GuiTextRenderState> consumer) {
      Node currentBackup = this.current;
      this.traverse((Consumer)((node) -> {
         if (node.textStates != null) {
            for(GuiTextRenderState textState : node.textStates) {
               this.current = node;
               consumer.accept(textState);
            }
         }

      }), GuiRenderState.TraverseRange.ALL);
      this.current = currentBackup;
   }

   public void forEachPictureInPicture(final Consumer<PictureInPictureRenderState> consumer) {
      Node currentBackup = this.current;
      this.traverse((Consumer)((node) -> {
         if (node.picturesInPictureStates != null) {
            this.current = node;

            for(PictureInPictureRenderState pictureInPictureState : node.picturesInPictureStates) {
               consumer.accept(pictureInPictureState);
            }
         }

      }), GuiRenderState.TraverseRange.ALL);
      this.current = currentBackup;
   }

   public void sortElements(final Comparator<GuiElementRenderState> comparator) {
      this.traverse((Consumer)((node) -> {
         if (node.elementStates != null) {
            if (SharedConstants.DEBUG_SHUFFLE_UI_RENDERING_ORDER) {
               Collections.shuffle(node.elementStates);
            }

            node.elementStates.sort(comparator);
         }

      }), GuiRenderState.TraverseRange.ALL);
   }

   private void traverse(final Consumer<Node> consumer, final TraverseRange range) {
      int startIndex = 0;
      int endIndex = this.strata.size();
      if (range == GuiRenderState.TraverseRange.BEFORE_BLUR) {
         endIndex = Math.min(this.firstStratumAfterBlur, this.strata.size());
      } else if (range == GuiRenderState.TraverseRange.AFTER_BLUR) {
         startIndex = this.firstStratumAfterBlur;
      }

      for(int i = startIndex; i < endIndex; ++i) {
         Node stratum = (Node)this.strata.get(i);
         this.traverse(stratum, consumer);
      }

   }

   private void traverse(final Node node, final Consumer<Node> consumer) {
      consumer.accept(node);
      if (node.up != null) {
         this.traverse(node.up, consumer);
      }

   }

   public void reset() {
      this.itemModelIdentities.clear();
      this.strata.clear();
      this.firstStratumAfterBlur = 2147483647;
      this.nextStratum();
   }

   private static class Node {
      public final @Nullable Node parent;
      public @Nullable Node up;
      public @Nullable List<GuiElementRenderState> elementStates;
      public @Nullable List<GuiElementRenderState> glyphStates;
      public @Nullable List<GuiItemRenderState> itemStates;
      public @Nullable List<GuiTextRenderState> textStates;
      public @Nullable List<PictureInPictureRenderState> picturesInPictureStates;

      private Node(final @Nullable Node parent) {
         super();
         this.parent = parent;
      }

      public void submitItem(final GuiItemRenderState itemState) {
         if (this.itemStates == null) {
            this.itemStates = new ArrayList();
         }

         this.itemStates.add(itemState);
      }

      public void submitText(final GuiTextRenderState textState) {
         if (this.textStates == null) {
            this.textStates = new ArrayList();
         }

         this.textStates.add(textState);
      }

      public void submitPicturesInPictureState(final PictureInPictureRenderState picturesInPictureState) {
         if (this.picturesInPictureStates == null) {
            this.picturesInPictureStates = new ArrayList();
         }

         this.picturesInPictureStates.add(picturesInPictureState);
      }

      public void submitGuiElement(final GuiElementRenderState blitState) {
         if (this.elementStates == null) {
            this.elementStates = new ArrayList();
         }

         this.elementStates.add(blitState);
      }

      public void submitGlyph(final GuiElementRenderState glyphState) {
         if (this.glyphStates == null) {
            this.glyphStates = new ArrayList();
         }

         this.glyphStates.add(glyphState);
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
}
