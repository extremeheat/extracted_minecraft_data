package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsSubscriptionInfoScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsServer serverData;
   private final RealmsScreen mainScreen;
   private final int BUTTON_BACK_ID = 0;
   private final int BUTTON_DELETE_ID = 1;
   private final int BUTTON_SUBSCRIPTION_ID = 2;
   private final String subscriptionTitle;
   private final String subscriptionStartLabelText;
   private final String timeLeftLabelText;
   private final String daysLeftLabelText;
   private int daysLeft;
   private String startDate;
   private Subscription.SubscriptionType type;
   private final String PURCHASE_LINK = "https://account.mojang.com/buy/realms";

   public RealmsSubscriptionInfoScreen(RealmsScreen var1, RealmsServer var2, RealmsScreen var3) {
      super();
      this.lastScreen = var1;
      this.serverData = var2;
      this.mainScreen = var3;
      this.subscriptionTitle = getLocalizedString("mco.configure.world.subscription.title");
      this.subscriptionStartLabelText = getLocalizedString("mco.configure.world.subscription.start");
      this.timeLeftLabelText = getLocalizedString("mco.configure.world.subscription.timeleft");
      this.daysLeftLabelText = getLocalizedString("mco.configure.world.subscription.recurring.daysleft");
   }

   public void init() {
      this.getSubscription(this.serverData.id);
      Realms.narrateNow(this.subscriptionTitle, this.subscriptionStartLabelText, this.startDate, this.timeLeftLabelText, this.daysLeftPresentation(this.daysLeft));
      this.setKeyboardHandlerSendRepeatsToGui(true);
      this.buttonsAdd(new RealmsButton(2, this.width() / 2 - 100, RealmsConstants.row(6), getLocalizedString("mco.configure.world.subscription.extend")) {
         public void onPress() {
            String var1 = "https://account.mojang.com/buy/realms?sid=" + RealmsSubscriptionInfoScreen.this.serverData.remoteSubscriptionId + "&pid=" + Realms.getUUID();
            Realms.setClipboard(var1);
            RealmsUtil.browseTo(var1);
         }
      });
      this.buttonsAdd(new RealmsButton(0, this.width() / 2 - 100, RealmsConstants.row(12), getLocalizedString("gui.back")) {
         public void onPress() {
            Realms.setScreen(RealmsSubscriptionInfoScreen.this.lastScreen);
         }
      });
      if (this.serverData.expired) {
         this.buttonsAdd(new RealmsButton(1, this.width() / 2 - 100, RealmsConstants.row(10), getLocalizedString("mco.configure.world.delete.button")) {
            public void onPress() {
               String var1 = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line1");
               String var2 = RealmsScreen.getLocalizedString("mco.configure.world.delete.question.line2");
               Realms.setScreen(new RealmsLongConfirmationScreen(RealmsSubscriptionInfoScreen.this, RealmsLongConfirmationScreen.Type.Warning, var1, var2, true, 1));
            }
         });
      }

   }

   private void getSubscription(long var1) {
      RealmsClient var3 = RealmsClient.createRealmsClient();

      try {
         Subscription var4 = var3.subscriptionFor(var1);
         this.daysLeft = var4.daysLeft;
         this.startDate = this.localPresentation(var4.startDate);
         this.type = var4.type;
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn't get subscription");
         Realms.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
      } catch (IOException var6) {
         LOGGER.error("Couldn't parse response subscribing");
      }

   }

   public void confirmResult(boolean var1, int var2) {
      if (var2 == 1 && var1) {
         (new Thread("Realms-delete-realm") {
            public void run() {
               try {
                  RealmsClient var1 = RealmsClient.createRealmsClient();
                  var1.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
               } catch (RealmsServiceException var2) {
                  RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
                  RealmsSubscriptionInfoScreen.LOGGER.error(var2);
               } catch (IOException var3) {
                  RealmsSubscriptionInfoScreen.LOGGER.error("Couldn't delete world");
                  var3.printStackTrace();
               }

               Realms.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen);
            }
         }).start();
      }

      Realms.setScreen(this);
   }

   private String localPresentation(long var1) {
      GregorianCalendar var3 = new GregorianCalendar(TimeZone.getDefault());
      var3.setTimeInMillis(var1);
      return DateFormat.getDateTimeInstance().format(var3.getTime());
   }

   public void removed() {
      this.setKeyboardHandlerSendRepeatsToGui(false);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         Realms.setScreen(this.lastScreen);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      int var4 = this.width() / 2 - 100;
      this.drawCenteredString(this.subscriptionTitle, this.width() / 2, 17, 16777215);
      this.drawString(this.subscriptionStartLabelText, var4, RealmsConstants.row(0), 10526880);
      this.drawString(this.startDate, var4, RealmsConstants.row(1), 16777215);
      if (this.type == Subscription.SubscriptionType.NORMAL) {
         this.drawString(this.timeLeftLabelText, var4, RealmsConstants.row(3), 10526880);
      } else if (this.type == Subscription.SubscriptionType.RECURRING) {
         this.drawString(this.daysLeftLabelText, var4, RealmsConstants.row(3), 10526880);
      }

      this.drawString(this.daysLeftPresentation(this.daysLeft), var4, RealmsConstants.row(4), 16777215);
      super.render(var1, var2, var3);
   }

   private String daysLeftPresentation(int var1) {
      if (var1 == -1 && this.serverData.expired) {
         return getLocalizedString("mco.configure.world.subscription.expired");
      } else if (var1 <= 1) {
         return getLocalizedString("mco.configure.world.subscription.less_than_a_day");
      } else {
         int var2 = var1 / 30;
         int var3 = var1 % 30;
         StringBuilder var4 = new StringBuilder();
         if (var2 > 0) {
            var4.append(var2).append(" ");
            if (var2 == 1) {
               var4.append(getLocalizedString("mco.configure.world.subscription.month").toLowerCase(Locale.ROOT));
            } else {
               var4.append(getLocalizedString("mco.configure.world.subscription.months").toLowerCase(Locale.ROOT));
            }
         }

         if (var3 > 0) {
            if (var4.length() > 0) {
               var4.append(", ");
            }

            var4.append(var3).append(" ");
            if (var3 == 1) {
               var4.append(getLocalizedString("mco.configure.world.subscription.day").toLowerCase(Locale.ROOT));
            } else {
               var4.append(getLocalizedString("mco.configure.world.subscription.days").toLowerCase(Locale.ROOT));
            }
         }

         return var4.toString();
      }
   }
}
