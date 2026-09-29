package net.minecraft.client.gui.components.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.Rect2i;

public class DebugColumn {
   private static final int MARGIN_TOP = 2;
   private final Side side;
   private List<DebugGroup> previousGroups = new ArrayList();
   private List<DebugGroup> currentGroups = new ArrayList();
   private int heightSoFar;

   public DebugColumn(final Side side) {
      super();
      this.side = side;
   }

   public void clear() {
      this.previousGroups = new ArrayList();
      this.currentGroups = new ArrayList();
   }

   public void newFrame() {
      this.previousGroups = this.currentGroups;
      this.currentGroups = new ArrayList();
      this.heightSoFar = 2;
   }

   public boolean containsGroup(final DebugGroup group) {
      return this.previousGroups.contains(group) || this.currentGroups.contains(group);
   }

   public boolean isFull(final int maxHeight) {
      return this.heightSoFar >= maxHeight;
   }

   public void add(final DebugGroupContents contents, final GuiGraphicsExtractor graphics, final Font font, final int scaledScreenWidth) {
      Rect2i rect = contents.extract(graphics, this.heightSoFar, font, this.side, scaledScreenWidth);
      int var10001 = this.heightSoFar;
      int var10002 = rect.getHeight();
      Objects.requireNonNull(font);
      this.heightSoFar = var10001 + var10002 + 9;
      this.currentGroups.add(contents.group());
   }

   public List<DebugGroup> getPreviousGroups() {
      return this.previousGroups;
   }

   public int getHeightSoFar() {
      return this.heightSoFar;
   }

   public static enum Side {
      LEFT,
      RIGHT;

      private Side() {
      }

      // $FF: synthetic method
      private static Side[] $values() {
         return new Side[]{LEFT, RIGHT};
      }
   }
}
