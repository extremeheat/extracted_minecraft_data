package net.minecraft.world.scores;

import com.mojang.authlib.GameProfile;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;

public interface ScoreHolder {
   String WILDCARD_NAME = "*";
   ScoreHolder WILDCARD = new ScoreHolder() {
      public String getScoreboardName() {
         return "*";
      }
   };

   String getScoreboardName();

   @Nullable
   default Component getDisplayName() {
      return null;
   }

   default Component getFeedbackDisplayName() {
      Component var1 = this.getDisplayName();
      return var1 != null ? var1.copy().withStyle((var1x) -> {
         return var1x.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(this.getScoreboardName())));
      }) : Component.literal(this.getScoreboardName());
   }

   static ScoreHolder forNameOnly(final String var0) {
      if (var0.equals("*")) {
         return WILDCARD;
      } else {
         final MutableComponent var1 = Component.literal(var0);
         return new ScoreHolder() {
            public String getScoreboardName() {
               return var0;
            }

            public Component getFeedbackDisplayName() {
               return var1;
            }
         };
      }
   }

   static ScoreHolder fromGameProfile(GameProfile var0) {
      final String var1 = var0.getName();
      return new ScoreHolder() {
         public String getScoreboardName() {
            return var1;
         }
      };
   }
}
