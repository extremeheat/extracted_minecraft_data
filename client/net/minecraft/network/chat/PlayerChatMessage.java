package net.minecraft.network.chat;

import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.FilteredText;
import net.minecraft.world.entity.player.ProfilePublicKey;

public record PlayerChatMessage(Component a, MessageSignature b, Optional<Component> c) {
   private final Component signedContent;
   private final MessageSignature signature;
   private final Optional<Component> unsignedContent;

   public PlayerChatMessage(Component var1, MessageSignature var2, Optional<Component> var3) {
      super();
      this.signedContent = var1;
      this.signature = var2;
      this.unsignedContent = var3;
   }

   public static PlayerChatMessage signed(Component var0, MessageSignature var1) {
      return new PlayerChatMessage(var0, var1, Optional.empty());
   }

   public static PlayerChatMessage signed(String var0, MessageSignature var1) {
      return signed((Component)Component.literal(var0), var1);
   }

   public static PlayerChatMessage signed(Component var0, Component var1, MessageSignature var2, boolean var3) {
      if (var0.equals(var1)) {
         return signed(var0, var2);
      } else {
         return !var3 ? signed(var0, var2).withUnsignedContent(var1) : signed(var1, var2);
      }
   }

   public static FilteredText<PlayerChatMessage> filteredSigned(FilteredText<Component> var0, FilteredText<Component> var1, MessageSignature var2, boolean var3) {
      Component var4 = (Component)var0.raw();
      Component var5 = (Component)var1.raw();
      PlayerChatMessage var6 = signed(var4, var5, var2, var3);
      if (var1.isFiltered()) {
         PlayerChatMessage var7 = (PlayerChatMessage)Util.mapNullable((Component)var1.filtered(), PlayerChatMessage::unsigned);
         return new FilteredText(var6, var7);
      } else {
         return FilteredText.passThrough(var6);
      }
   }

   public static PlayerChatMessage unsigned(Component var0) {
      return new PlayerChatMessage(var0, MessageSignature.unsigned(), Optional.empty());
   }

   public PlayerChatMessage withUnsignedContent(Component var1) {
      return new PlayerChatMessage(this.signedContent, this.signature, Optional.of(var1));
   }

   public boolean verify(ProfilePublicKey var1) {
      return this.signature.verify(var1.createSignatureValidator(), this.signedContent);
   }

   public boolean verify(ServerPlayer var1) {
      ProfilePublicKey var2 = var1.getProfilePublicKey();
      return var2 == null || this.verify(var2);
   }

   public boolean verify(CommandSourceStack var1) {
      ServerPlayer var2 = var1.getPlayer();
      return var2 == null || this.verify(var2);
   }

   public Component serverContent() {
      return (Component)this.unsignedContent.orElse(this.signedContent);
   }

   public Component signedContent() {
      return this.signedContent;
   }

   public MessageSignature signature() {
      return this.signature;
   }

   public Optional<Component> unsignedContent() {
      return this.unsignedContent;
   }
}
