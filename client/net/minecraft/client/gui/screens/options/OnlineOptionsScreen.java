package net.minecraft.client.gui.screens.options;

import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;
import org.jspecify.annotations.Nullable;

public class OnlineOptionsScreen extends OptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.online.title");
   private @Nullable OptionInstance<Unit> difficultyDisplay;

   public OnlineOptionsScreen(final Screen lastScreen, final Options options) {
      super(lastScreen, options, TITLE);
   }

   protected void init() {
      super.init();
      if (this.difficultyDisplay != null) {
         AbstractWidget difficultyButton = this.list.findOption(this.difficultyDisplay);
         if (difficultyButton != null) {
            difficultyButton.active = false;
         }
      }

   }

   private OptionInstance<?>[] options(final Options options, final Minecraft minecraft) {
      List<OptionInstance<?>> optionList = new ArrayList();
      optionList.add(options.realmsNotifications());
      optionList.add(options.allowServerListing());
      OptionInstance<Unit> difficultyDisplay = (OptionInstance)Optionull.map(minecraft.level, (level) -> {
         Difficulty difficulty = level.getDifficulty();
         return new OptionInstance("options.difficulty.online", OptionInstance.noTooltip(), (caption, value) -> difficulty.getDisplayName(), new OptionInstance.Enum(List.of(Unit.INSTANCE), Codec.EMPTY.codec()), Unit.INSTANCE, (value) -> {
         });
      });
      if (difficultyDisplay != null) {
         this.difficultyDisplay = difficultyDisplay;
         optionList.add(difficultyDisplay);
      }

      return (OptionInstance[])optionList.toArray(new OptionInstance[0]);
   }

   protected void addOptions() {
      this.list.addSmall(this.options(this.options, this.minecraft));
   }
}
