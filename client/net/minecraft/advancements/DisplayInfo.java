package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
   public static final Codec<DisplayInfo> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               ItemStack.ADVANCEMENT_ICON_CODEC.fieldOf("icon").forGetter(DisplayInfo::getIcon),
               ComponentSerialization.CODEC.fieldOf("title").forGetter(DisplayInfo::getTitle),
               ComponentSerialization.CODEC.fieldOf("description").forGetter(DisplayInfo::getDescription),
               ExtraCodecs.strictOptionalField(ResourceLocation.CODEC, "background").forGetter(DisplayInfo::getBackground),
               ExtraCodecs.strictOptionalField(AdvancementType.CODEC, "frame", AdvancementType.TASK).forGetter(DisplayInfo::getType),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "show_toast", true).forGetter(DisplayInfo::shouldShowToast),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "announce_to_chat", true).forGetter(DisplayInfo::shouldAnnounceChat),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "hidden", false).forGetter(DisplayInfo::isHidden)
            )
            .apply(var0, DisplayInfo::new)
   );
   private final Component title;
   private final Component description;
   private final ItemStack icon;
   private final Optional<ResourceLocation> background;
   private final AdvancementType type;
   private final boolean showToast;
   private final boolean announceChat;
   private final boolean hidden;
   private float x;
   private float y;

   public DisplayInfo(
      ItemStack var1, Component var2, Component var3, Optional<ResourceLocation> var4, AdvancementType var5, boolean var6, boolean var7, boolean var8
   ) {
      super();
      this.title = var2;
      this.description = var3;
      this.icon = var1;
      this.background = var4;
      this.type = var5;
      this.showToast = var6;
      this.announceChat = var7;
      this.hidden = var8;
   }

   public void setLocation(float var1, float var2) {
      this.x = var1;
      this.y = var2;
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   public Optional<ResourceLocation> getBackground() {
      return this.background;
   }

   public AdvancementType getType() {
      return this.type;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public boolean shouldShowToast() {
      return this.showToast;
   }

   public boolean shouldAnnounceChat() {
      return this.announceChat;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
      var1.writeComponent(this.title);
      var1.writeComponent(this.description);
      var1.writeItem(this.icon);
      var1.writeEnum(this.type);
      int var2 = 0;
      if (this.background.isPresent()) {
         var2 |= 1;
      }

      if (this.showToast) {
         var2 |= 2;
      }

      if (this.hidden) {
         var2 |= 4;
      }

      var1.writeInt(var2);
      this.background.ifPresent(var1::writeResourceLocation);
      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
   }

   public static DisplayInfo fromNetwork(FriendlyByteBuf var0) {
      Component var1 = var0.readComponentTrusted();
      Component var2 = var0.readComponentTrusted();
      ItemStack var3 = var0.readItem();
      AdvancementType var4 = var0.readEnum(AdvancementType.class);
      int var5 = var0.readInt();
      Optional var6 = (var5 & 1) != 0 ? Optional.of(var0.readResourceLocation()) : Optional.empty();
      boolean var7 = (var5 & 2) != 0;
      boolean var8 = (var5 & 4) != 0;
      DisplayInfo var9 = new DisplayInfo(var3, var1, var2, var6, var4, var7, false, var8);
      var9.setLocation(var0.readFloat(), var0.readFloat());
      return var9;
   }
}
