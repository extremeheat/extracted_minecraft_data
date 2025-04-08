package net.minecraft.client.gui.render.state;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;

public class GuiRenderState {
   private final List<GuiElementRenderState> elementStates = new ArrayList();
   private final List<GuiItemRenderState> itemStates = new ArrayList();
   private final List<GuiTextRenderState> textStates = new ArrayList();
   private final List<PictureInPictureRenderState> picturesInPictureStates = new ArrayList();

   public GuiRenderState() {
      super();
   }

   public void submitItem(GuiItemRenderState var1) {
      this.itemStates.add(var1);
   }

   public void submitText(GuiTextRenderState var1) {
      this.textStates.add(var1);
   }

   public void submitPicturesInPictureState(PictureInPictureRenderState var1) {
      this.picturesInPictureStates.add(var1);
   }

   public void submitGuiElement(GuiElementRenderState var1) {
      this.elementStates.add(var1);
   }

   public List<GuiElementRenderState> getElementStates() {
      return this.elementStates;
   }

   public List<GuiItemRenderState> getItemStates() {
      return this.itemStates;
   }

   public List<GuiTextRenderState> getTextStates() {
      return this.textStates;
   }

   public List<PictureInPictureRenderState> getPicturesInPictureStates() {
      return this.picturesInPictureStates;
   }

   public void reset() {
      this.elementStates.clear();
      this.itemStates.clear();
      this.textStates.clear();
      this.picturesInPictureStates.clear();
   }
}
