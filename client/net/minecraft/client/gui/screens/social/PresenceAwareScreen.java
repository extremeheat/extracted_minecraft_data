package net.minecraft.client.gui.screens.social;

import com.mojang.authlib.services.response.PresenceResponse;

public interface PresenceAwareScreen {
   void applyPresenceUpdate(PresenceResponse latestPresence);
}
