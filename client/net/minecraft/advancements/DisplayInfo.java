package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
   public static final Codec<DisplayInfo> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ItemStack.STRICT_CODEC.fieldOf("icon").forGetter(DisplayInfo::getIcon), ComponentSerialization.CODEC.fieldOf("title").forGetter(DisplayInfo::getTitle), ComponentSerialization.CODEC.fieldOf("description").forGetter(DisplayInfo::getDescription), ResourceLocation.CODEC.optionalFieldOf("background").forGetter(DisplayInfo::getBackground), AdvancementType.CODEC.optionalFieldOf("frame", AdvancementType.TASK).forGetter(DisplayInfo::getType), Codec.BOOL.optionalFieldOf("show_toast", true).forGetter(DisplayInfo::shouldShowToast), Codec.BOOL.optionalFieldOf("announce_to_chat", true).forGetter(DisplayInfo::shouldAnnounceChat), Codec.BOOL.optionalFieldOf("hidden", false).forGetter(DisplayInfo::isHidden)).apply(var0, DisplayInfo::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, DisplayInfo> STREAM_CODEC = StreamCodec.ofMember(DisplayInfo::serializeToNetwork, DisplayInfo::fromNetwork);
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

   public DisplayInfo(ItemStack var1, Component var2, Component var3, Optional<ResourceLocation> var4, AdvancementType var5, boolean var6, boolean var7, boolean var8) {
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

   private void serializeToNetwork(RegistryFriendlyByteBuf var1) {
      ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.title);
      ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.description);
      ItemStack.STREAM_CODEC.encode(var1, this.icon);
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
      Optional var10000 = this.background;
      Objects.requireNonNull(var1);
      var10000.ifPresent(var1::writeResourceLocation);
      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
   }

   private static DisplayInfo fromNetwork(RegistryFriendlyByteBuf var0) {
      Component var1 = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var0);
      Component var2 = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var0);
      ItemStack var3 = (ItemStack)ItemStack.STREAM_CODEC.decode(var0);
      AdvancementType var4 = (AdvancementType)var0.readEnum(AdvancementType.class);
      int var5 = var0.readInt();
      Optional var6 = (var5 & 1) != 0 ? Optional.of(var0.readResourceLocation()) : Optional.empty();
      boolean var7 = (var5 & 2) != 0;
      boolean var8 = (var5 & 4) != 0;
      DisplayInfo var9 = new DisplayInfo(var3, var1, var2, var6, var4, var7, false, var8);
      var9.setLocation(var0.readFloat(), var0.readFloat());
      return var9;
   }
}
