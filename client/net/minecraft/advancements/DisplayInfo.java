package net.minecraft.advancements;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.ClientAsset;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;

public record DisplayInfo(ItemStackTemplate icon, Component title, Component description, Optional<ClientAsset.ResourceTexture> background, AdvancementType type, boolean showToast, boolean announceToChat, boolean hidden) {
   public static final Codec<DisplayInfo> CODEC = RecordCodecBuilder.create((i) -> i.group(ItemStackTemplate.CODEC.fieldOf("icon").forGetter(DisplayInfo::icon), ComponentSerialization.CODEC.fieldOf("title").forGetter(DisplayInfo::title), ComponentSerialization.CODEC.fieldOf("description").forGetter(DisplayInfo::description), ClientAsset.ResourceTexture.CODEC.optionalFieldOf("background").forGetter(DisplayInfo::background), AdvancementType.CODEC.optionalFieldOf("frame", AdvancementType.TASK).forGetter(DisplayInfo::type), Codec.BOOL.optionalFieldOf("show_toast", true).forGetter(DisplayInfo::showToast), Codec.BOOL.optionalFieldOf("announce_to_chat", true).forGetter(DisplayInfo::announceToChat), Codec.BOOL.optionalFieldOf("hidden", false).forGetter(DisplayInfo::hidden)).apply(i, DisplayInfo::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, DisplayInfo> STREAM_CODEC = StreamCodec.<RegistryFriendlyByteBuf, DisplayInfo>ofMember(DisplayInfo::serializeToNetwork, DisplayInfo::fromNetwork);

   public DisplayInfo {
      super();
   }

   private void serializeToNetwork(final RegistryFriendlyByteBuf output) {
      ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, this.title);
      ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, this.description);
      ItemStackTemplate.STREAM_CODEC.encode(output, this.icon);
      AdvancementType.STREAM_CODEC.encode(output, this.type);
      int flags = 0;
      if (this.background.isPresent()) {
         flags |= 1;
      }

      if (this.showToast) {
         flags |= 2;
      }

      if (this.hidden) {
         flags |= 4;
      }

      output.writeInt(flags);
      Optional var10000 = this.background.map(ClientAsset::id);
      Objects.requireNonNull(output);
      var10000.ifPresent(output::writeIdentifier);
   }

   private static DisplayInfo fromNetwork(final RegistryFriendlyByteBuf input) {
      Component title = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input);
      Component description = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input);
      ItemStackTemplate icon = (ItemStackTemplate)ItemStackTemplate.STREAM_CODEC.decode(input);
      AdvancementType frame = (AdvancementType)AdvancementType.STREAM_CODEC.decode(input);
      int flags = input.readInt();
      Optional<ClientAsset.ResourceTexture> background = (flags & 1) != 0 ? Optional.of(new ClientAsset.ResourceTexture(input.readIdentifier())) : Optional.empty();
      boolean showToast = (flags & 2) != 0;
      boolean hidden = (flags & 4) != 0;
      return new DisplayInfo(icon, title, description, background, frame, showToast, false, hidden);
   }
}
