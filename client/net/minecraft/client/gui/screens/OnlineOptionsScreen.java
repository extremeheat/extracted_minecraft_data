package net.minecraft.client.gui.screens;

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
import net.minecraft.network.chat.Component;
import net.minecraft.world.Difficulty;

public class OnlineOptionsScreen extends SimpleOptionsSubScreen {
   private static final Component TITLE = Component.translatable("options.online.title");
   @Nullable
   private final OptionInstance<Unit> difficultyDisplay;

   public static OnlineOptionsScreen createOnlineOptionsScreen(Minecraft var0, Screen var1, Options var2) {
      ArrayList var3 = new ArrayList();
      var3.add(var2.realmsNotifications());
      var3.add(var2.allowServerListing());
      OptionInstance var4 = (OptionInstance)Optionull.map(var0.level, (var0x) -> {
         Difficulty var1 = var0x.getDifficulty();
         return new OptionInstance("options.difficulty.online", OptionInstance.noTooltip(), (var1x, var2) -> {
            return var1.getDisplayName();
         }, new OptionInstance.Enum(List.of(Unit.INSTANCE), Codec.EMPTY.codec()), Unit.INSTANCE, (var0) -> {
         });
      });
      if (var4 != null) {
         var3.add(var4);
      }

      return new OnlineOptionsScreen(var1, var2, (OptionInstance[])var3.toArray(new OptionInstance[0]), var4);
   }

   private OnlineOptionsScreen(Screen var1, Options var2, OptionInstance<?>[] var3, @Nullable OptionInstance<Unit> var4) {
      super(var1, var2, TITLE, var3);
      this.difficultyDisplay = var4;
   }

   protected void init() {
      super.init();
      AbstractWidget var1;
      if (this.difficultyDisplay != null) {
         var1 = this.list.findOption(this.difficultyDisplay);
         if (var1 != null) {
            var1.active = false;
         }
      }

      var1 = this.list.findOption(this.options.telemetryOptInExtra());
      if (var1 != null) {
         var1.active = this.minecraft.extraTelemetryAvailable();
      }

   }
}
