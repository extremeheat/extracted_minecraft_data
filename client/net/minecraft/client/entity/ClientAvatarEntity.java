package net.minecraft.client.entity;

import javax.annotation.Nullable;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.Parrot;

public interface ClientAvatarEntity {
   ClientAvatarState avatarState();

   PlayerSkin getSkin();

   @Nullable
   Component belowNameDisplay();

   @Nullable
   Parrot.Variant getParrotVariantOnShoulder(boolean var1);

   boolean showExtraEars();
}
