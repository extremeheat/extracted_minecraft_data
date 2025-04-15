package net.minecraft.client.gui.render.state;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import org.apache.commons.lang3.mutable.MutableInt;

public class GuiRenderState {
   private final List<Node> strata = new ArrayList();
   private Node current;
   private final List<Node> checkpointStack = new ArrayList();
   private final Set<Object> itemModelIdentities = new HashSet();

   public GuiRenderState() {
      super();
      this.nextStratum();
   }

   public void nextStratum() {
      if (!this.checkpointStack.isEmpty()) {
         throw new IllegalStateException("Checkpoint stack is not empty");
      } else {
         this.current = new Node((Node)null);
         this.strata.add(this.current);
      }
   }

   public void up() {
      if (this.current.up == null) {
         this.current.up = new Node(this.current);
      }

      this.current = this.current.up;
   }

   public void upToTop() {
      while(this.current.up != null) {
         this.current = this.current.up;
      }

      this.current.up = new Node(this.current);
      this.current = this.current.up;
   }

   public void down() {
      if (this.current.down == null) {
         this.current.down = new Node(this.current);
      }

      this.current = this.current.down;
   }

   public void pushCheckpoint() {
      this.checkpointStack.add(this.current);
   }

   public void backToCheckpoint() {
      if (this.checkpointStack.isEmpty()) {
         throw new IllegalStateException("Checkpoint stack is empty");
      } else {
         this.current = (Node)this.checkpointStack.removeLast();
      }
   }

   public void back() {
      if (this.current.parent == null) {
         throw new IllegalStateException("Can not back out of the root node");
      } else {
         this.current = this.current.parent;
      }
   }

   public void submitItem(GuiItemRenderState var1) {
      this.itemModelIdentities.add(var1.itemStackRenderState().getModelIdentity());
      this.current.submitItem(var1);
   }

   public void submitText(GuiTextRenderState var1) {
      this.current.submitText(var1);
   }

   public void submitPicturesInPictureState(PictureInPictureRenderState var1) {
      this.current.submitPicturesInPictureState(var1);
   }

   public void submitGuiElement(GuiElementRenderState var1) {
      this.current.submitGuiElement(var1);
   }

   public Set<Object> getItemModelIdentities() {
      return this.itemModelIdentities;
   }

   public void forEachElement(LayeredElementConsumer var1) {
      if (!this.checkpointStack.isEmpty()) {
         throw new IllegalStateException("Unused checkpoints in checkpoint stack. The GUI tree is most likely corrupted.");
      } else {
         MutableInt var2 = new MutableInt(0);
         this.traverse((var2x) -> {
            if (var2x.elementStates != null) {
               var2.increment();
               int var3 = var2.intValue();

               for(GuiElementRenderState var5 : var2x.elementStates) {
                  var1.accept(var5, var3);
               }
            }

         });
      }
   }

   public void forEachItem(Consumer<GuiItemRenderState> var1) {
      Node var2 = this.current;
      this.traverse((var2x) -> {
         if (var2x.itemStates != null) {
            this.current = var2x;

            for(GuiItemRenderState var4 : var2x.itemStates) {
               var1.accept(var4);
            }
         }

      });
      this.current = var2;
   }

   public void forEachText(Consumer<GuiTextRenderState> var1) {
      Node var2 = this.current;
      this.traverse((var2x) -> {
         if (var2x.textStates != null) {
            this.current = var2x;

            for(GuiTextRenderState var4 : var2x.textStates) {
               var1.accept(var4);
            }
         }

      });
      this.current = var2;
   }

   public void forEachPictureInPicture(Consumer<PictureInPictureRenderState> var1) {
      Node var2 = this.current;
      this.traverse((var2x) -> {
         if (var2x.picturesInPictureStates != null) {
            this.current = var2x;

            for(PictureInPictureRenderState var4 : var2x.picturesInPictureStates) {
               var1.accept(var4);
            }
         }

      });
      this.current = var2;
   }

   public void sortElements(Comparator<GuiElementRenderState> var1) {
      this.traverse((var1x) -> {
         if (var1x.elementStates != null) {
            var1x.elementStates.sort(var1);
         }

      });
   }

   private void traverse(Consumer<Node> var1) {
      for(Node var3 : this.strata) {
         this.traverse(var3, var1);
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

   public interface LayeredElementConsumer {
      void accept(GuiElementRenderState var1, int var2);
   }
}
