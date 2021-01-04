package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsScreen;

public class RealmsLongConfirmationScreen extends RealmsScreen {
   private final RealmsLongConfirmationScreen.Type type;
   private final String line2;
   private final String line3;
   protected final RealmsConfirmResultListener listener;
   protected final String yesButton;
   protected final String noButton;
   private final String okButton;
   protected final int id;
   private final boolean yesNoQuestion;

   public RealmsLongConfirmationScreen(RealmsConfirmResultListener var1, RealmsLongConfirmationScreen.Type var2, String var3, String var4, boolean var5, int var6) {
      super();
      this.listener = var1;
      this.id = var6;
      this.type = var2;
      this.line2 = var3;
      this.line3 = var4;
      this.yesNoQuestion = var5;
      this.yesButton = getLocalizedString("gui.yes");
      this.noButton = getLocalizedString("gui.no");
      this.okButton = getLocalizedString("mco.gui.ok");
   }

   public void init() {
      Realms.narrateNow(this.type.text, this.line2, this.line3);
      if (this.yesNoQuestion) {
         this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 105, RealmsConstants.row(8), 100, 20, this.yesButton) {
            public void onPress() {
               RealmsLongConfirmationScreen.this.listener.confirmResult(true, RealmsLongConfirmationScreen.this.id);
            }
         });
         this.buttonsAdd(new RealmsButton(1, this.width() / 2 + 5, RealmsConstants.row(8), 100, 20, this.noButton) {
            public void onPress() {
               RealmsLongConfirmationScreen.this.listener.confirmResult(false, RealmsLongConfirmationScreen.this.id);
            }
         });
      } else {
         this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 50, RealmsConstants.row(8), 100, 20, this.okButton) {
            public void onPress() {
               RealmsLongConfirmationScreen.this.listener.confirmResult(true, RealmsLongConfirmationScreen.this.id);
            }
         });
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.listener.confirmResult(false, this.id);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      this.drawCenteredString(this.type.text, this.width() / 2, RealmsConstants.row(2), this.type.colorCode);
      this.drawCenteredString(this.line2, this.width() / 2, RealmsConstants.row(4), 16777215);
      this.drawCenteredString(this.line3, this.width() / 2, RealmsConstants.row(6), 16777215);
      super.render(var1, var2, var3);
   }

   public static enum Type {
      Warning("Warning!", 16711680),
      Info("Info!", 8226750);

      public final int colorCode;
      public final String text;

      private Type(String var3, int var4) {
         this.text = var3;
         this.colorCode = var4;
      }
   }
}
