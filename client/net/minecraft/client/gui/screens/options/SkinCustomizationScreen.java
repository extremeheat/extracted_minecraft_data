package net.minecraft.client.gui.screens.options;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerModelPart;

public class SkinCustomizationScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.skinCustomisation.title");

   public SkinCustomizationScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
   }

   protected void addOptions() {
      ArrayList var1 = new ArrayList();
      PlayerModelPart[] var2 = PlayerModelPart.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         PlayerModelPart var5 = var2[var4];
         var1.add(CycleButton.onOffBuilder(this.options.isModelPartEnabled(var5)).create(var5.getName(), (var2x, var3x) -> {
            this.options.toggleModelPart(var5, var3x);
         }));
      }

      var1.add(this.options.mainHand().createButton(this.options));
      this.list.addSmall((List)var1);
   }
}
