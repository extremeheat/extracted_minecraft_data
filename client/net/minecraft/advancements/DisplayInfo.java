package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
   public static final Codec<DisplayInfo> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ItemStack.STRICT_CODEC.fieldOf("icon").forGetter(DisplayInfo::getIcon), ComponentSerialization.CODEC.fieldOf("title").forGetter(DisplayInfo::getTitle), ComponentSerialization.CODEC.fieldOf("description").forGetter(DisplayInfo::getDescription), ComponentSerialization.CODEC.fieldOf("hint").forGetter(DisplayInfo::getHint), ClientAsset.CODEC.optionalFieldOf("background").forGetter(DisplayInfo::getBackground), AdvancementType.CODEC.optionalFieldOf("frame", AdvancementType.TASK).forGetter(DisplayInfo::getType), Codec.BOOL.optionalFieldOf("show_toast", true).forGetter(DisplayInfo::shouldShowToast), Codec.BOOL.optionalFieldOf("announce_to_chat", true).forGetter(DisplayInfo::shouldAnnounceChat), Codec.BOOL.optionalFieldOf("hidden", false).forGetter(DisplayInfo::isHidden)).apply(var0, DisplayInfo::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, DisplayInfo> STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, DisplayInfo>ofMember(DisplayInfo::serializeToNetwork, DisplayInfo::fromNetwork);
   private final Component title;
   private final Component description;
   private final Component hint;
   private final ItemStack icon;
   private final Optional<ClientAsset> background;
   private final AdvancementType type;
   private final boolean showToast;
   private final boolean announceChat;
   private final boolean hidden;
   private float x;
   private float y;

   public DisplayInfo(ItemStack var1, Component var2, Component var3, Component var4, Optional<ClientAsset> var5, AdvancementType var6, boolean var7, boolean var8, boolean var9) {
      super();
      this.title = var2;
      this.description = var3;
      this.hint = var4;
      this.icon = var1;
      this.background = var5;
      this.type = var6;
      this.showToast = var7;
      this.announceChat = var8;
      this.hidden = var9;
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

   public Optional<ClientAsset> getBackground() {
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

   public Component getHint() {
      return this.hint;
   }

   private void serializeToNetwork(RegistryFriendlyByteBuf var1) {
      ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.title);
      ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.description);
      ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.hint);
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
      Optional var10000 = this.background.map(ClientAsset::id);
      Objects.requireNonNull(var1);
      var10000.ifPresent(var1::writeResourceLocation);
      var1.writeFloat(this.x);
      var1.writeFloat(this.y);
   }

   private static DisplayInfo fromNetwork(RegistryFriendlyByteBuf var0) {
      Component var1 = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var0);
      Component var2 = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var0);
      Component var3 = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var0);
      ItemStack var4 = (ItemStack)ItemStack.STREAM_CODEC.decode(var0);
      AdvancementType var5 = (AdvancementType)var0.readEnum(AdvancementType.class);
      int var6 = var0.readInt();
      Optional var7 = (var6 & 1) != 0 ? Optional.of(new ClientAsset(var0.readResourceLocation())) : Optional.empty();
      boolean var8 = (var6 & 2) != 0;
      boolean var9 = (var6 & 4) != 0;
      DisplayInfo var10 = new DisplayInfo(var4, var1, var2, var3, var7, var5, var8, false, var9);
      var10.setLocation(var0.readFloat(), var0.readFloat());
      return var10;
   }

   public static class Builder {
      @Nullable
      private ItemStack icon;
      @Nullable
      private Component title;
      @Nullable
      private Component description;
      @Nullable
      private Component hint;
      private Optional<ClientAsset> background = Optional.empty();
      @Nullable
      private AdvancementType type;
      private boolean showToast = false;
      private boolean announceChat = false;
      private boolean hidden = false;

      public Builder() {
         super();
      }

      public Builder withIcon(ItemStack var1) {
         this.icon = var1;
         return this;
      }

      public Builder withTitle(Component var1) {
         this.title = var1;
         return this;
      }

      public Builder withTitleStyle(Style var1) {
         this.title = ((Component)Objects.requireNonNull(this.title)).copy().withStyle(var1);
         return this;
      }

      public Builder withDescriptionStyle(Style var1) {
         this.description = ((Component)Objects.requireNonNull(this.description)).copy().withStyle(var1);
         return this;
      }

      public Builder withDescription(Component var1) {
         this.description = var1;
         return this;
      }

      public Builder withHint(Component var1) {
         this.hint = var1;
         return this;
      }

      public Builder withBackground(ClientAsset var1) {
         this.background = Optional.of(var1);
         return this;
      }

      public Builder withType(AdvancementType var1) {
         this.type = var1;
         return this;
      }

      public Builder withShowToast(boolean var1) {
         this.showToast = var1;
         return this;
      }

      public Builder withAnnounceChat(boolean var1) {
         this.announceChat = var1;
         return this;
      }

      public Builder withHidden(boolean var1) {
         this.hidden = var1;
         return this;
      }

      public DisplayInfo build() {
         return new DisplayInfo((ItemStack)Objects.requireNonNull(this.icon), (Component)Objects.requireNonNull(this.title), (Component)Objects.requireNonNull(this.description), (Component)Objects.requireNonNull(this.hint), this.background, (AdvancementType)Objects.requireNonNull(this.type), this.showToast, this.announceChat, this.hidden);
      }
   }
}
