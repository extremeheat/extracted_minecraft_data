package net.minecraft.client.gui.screens.options;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;

public class OnlineOptionsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.online.title");
   @Nullable
   private OptionInstance<Unit> difficultyDisplay;

   public OnlineOptionsScreen(Screen var1, Options var2) {
      super(var1, var2, TITLE);
   }

   @Override
   protected void init() {
      super.init();
      if (this.difficultyDisplay != null) {
         AbstractWidget var1 = this.list.findOption(this.difficultyDisplay);
         if (var1 != null) {
            var1.active = false;
         }
      }
   }

   private OptionInstance<?>[] options(Options var1, Minecraft var2) {
      ArrayList var3 = new ArrayList();
      var3.add(var1.realmsNotifications());
      var3.add(var1.allowServerListing());
      OptionInstance var4 = Optionull.map(
         var2.level,
         var0 -> {
            Difficulty var1x = var0.getDifficulty();
            return new OptionInstance<>(
               "options.difficulty.online",
               OptionInstance.noTooltip(),
               (var1xx, var2x) -> var1x.getDisplayName(),
               new OptionInstance.Enum<>(List.of(Unit.INSTANCE), Codec.EMPTY.codec()),
               Unit.INSTANCE,
               var0x -> {
               }
            );
         }
      );
      if (var4 != null) {
         this.difficultyDisplay = var4;
         var3.add(var4);
      }

      return var3.toArray(new OptionInstance[0]);
   }

   @Override
   protected void addOptions() {
      this.list.addSmall(this.options(this.options, this.minecraft));
   }
}
