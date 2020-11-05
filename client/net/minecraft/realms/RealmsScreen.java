package net.minecraft.realms;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.TickableWidget;
import net.minecraft.client.gui.screens.Screen;

public abstract class RealmsScreen extends Screen {
   public RealmsScreen() {
      super(NarratorChatListener.NO_TITLE);
   }

   protected static int row(int var0) {
      return 40 + var0 * 13;
   }

   public void tick() {
      Iterator var1 = this.buttons.iterator();

      while(var1.hasNext()) {
         AbstractWidget var2 = (AbstractWidget)var1.next();
         if (var2 instanceof TickableWidget) {
            ((TickableWidget)var2).tick();
         }
      }

   }

   public void narrateLabels() {
      Stream var10000 = this.children.stream();
      RealmsLabel.class.getClass();
      var10000 = var10000.filter(RealmsLabel.class::isInstance);
      RealmsLabel.class.getClass();
      List var1 = (List)var10000.map(RealmsLabel.class::cast).map(RealmsLabel::getText).collect(Collectors.toList());
      NarrationHelper.now((Iterable)var1);
   }
}
