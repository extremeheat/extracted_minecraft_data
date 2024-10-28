package net.minecraft.network.protocol.game;

import java.util.UUID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.BossEvent;

public class ClientboundBossEventPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBossEventPacket> STREAM_CODEC = Packet.codec(ClientboundBossEventPacket::write, ClientboundBossEventPacket::new);
   private static final int FLAG_DARKEN = 1;
   private static final int FLAG_MUSIC = 2;
   private static final int FLAG_FOG = 4;
   private final UUID id;
   private final Operation operation;
   static final Operation REMOVE_OPERATION = new Operation() {
      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.REMOVE;
      }

      public void dispatch(UUID var1, Handler var2) {
         var2.remove(var1);
      }

      public void write(RegistryFriendlyByteBuf var1) {
      }
   };

   private ClientboundBossEventPacket(UUID var1, Operation var2) {
      super();
      this.id = var1;
      this.operation = var2;
   }

   private ClientboundBossEventPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.id = var1.readUUID();
      OperationType var2 = (OperationType)var1.readEnum(OperationType.class);
      this.operation = (Operation)var2.reader.decode(var1);
   }

   public static ClientboundBossEventPacket createAddPacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new AddOperation(var0));
   }

   public static ClientboundBossEventPacket createRemovePacket(UUID var0) {
      return new ClientboundBossEventPacket(var0, REMOVE_OPERATION);
   }

   public static ClientboundBossEventPacket createUpdateProgressPacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new UpdateProgressOperation(var0.getProgress()));
   }

   public static ClientboundBossEventPacket createUpdateNamePacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new UpdateNameOperation(var0.getName()));
   }

   public static ClientboundBossEventPacket createUpdateStylePacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new UpdateStyleOperation(var0.getColor(), var0.getOverlay()));
   }

   public static ClientboundBossEventPacket createUpdatePropertiesPacket(BossEvent var0) {
      return new ClientboundBossEventPacket(var0.getId(), new UpdatePropertiesOperation(var0.shouldDarkenScreen(), var0.shouldPlayBossMusic(), var0.shouldCreateWorldFog()));
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeUUID(this.id);
      var1.writeEnum(this.operation.getType());
      this.operation.write(var1);
   }

   static int encodeProperties(boolean var0, boolean var1, boolean var2) {
      int var3 = 0;
      if (var0) {
         var3 |= 1;
      }

      if (var1) {
         var3 |= 2;
      }

      if (var2) {
         var3 |= 4;
      }

      return var3;
   }

   public PacketType<ClientboundBossEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_BOSS_EVENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleBossUpdate(this);
   }

   public void dispatch(Handler var1) {
      this.operation.dispatch(this.id, var1);
   }

   interface Operation {
      OperationType getType();

      void dispatch(UUID var1, Handler var2);

      void write(RegistryFriendlyByteBuf var1);
   }

   private static enum OperationType {
      ADD(AddOperation::new),
      REMOVE((var0) -> {
         return ClientboundBossEventPacket.REMOVE_OPERATION;
      }),
      UPDATE_PROGRESS(UpdateProgressOperation::new),
      UPDATE_NAME(UpdateNameOperation::new),
      UPDATE_STYLE(UpdateStyleOperation::new),
      UPDATE_PROPERTIES(UpdatePropertiesOperation::new);

      final StreamDecoder<RegistryFriendlyByteBuf, Operation> reader;

      private OperationType(final StreamDecoder var3) {
         this.reader = var3;
      }

      // $FF: synthetic method
      private static OperationType[] $values() {
         return new OperationType[]{ADD, REMOVE, UPDATE_PROGRESS, UPDATE_NAME, UPDATE_STYLE, UPDATE_PROPERTIES};
      }
   }

   private static class AddOperation implements Operation {
      private final Component name;
      private final float progress;
      private final BossEvent.BossBarColor color;
      private final BossEvent.BossBarOverlay overlay;
      private final boolean darkenScreen;
      private final boolean playMusic;
      private final boolean createWorldFog;

      AddOperation(BossEvent var1) {
         super();
         this.name = var1.getName();
         this.progress = var1.getProgress();
         this.color = var1.getColor();
         this.overlay = var1.getOverlay();
         this.darkenScreen = var1.shouldDarkenScreen();
         this.playMusic = var1.shouldPlayBossMusic();
         this.createWorldFog = var1.shouldCreateWorldFog();
      }

      private AddOperation(RegistryFriendlyByteBuf var1) {
         super();
         this.name = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var1);
         this.progress = var1.readFloat();
         this.color = (BossEvent.BossBarColor)var1.readEnum(BossEvent.BossBarColor.class);
         this.overlay = (BossEvent.BossBarOverlay)var1.readEnum(BossEvent.BossBarOverlay.class);
         short var2 = var1.readUnsignedByte();
         this.darkenScreen = (var2 & 1) > 0;
         this.playMusic = (var2 & 2) > 0;
         this.createWorldFog = (var2 & 4) > 0;
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.ADD;
      }

      public void dispatch(UUID var1, Handler var2) {
         var2.add(var1, this.name, this.progress, this.color, this.overlay, this.darkenScreen, this.playMusic, this.createWorldFog);
      }

      public void write(RegistryFriendlyByteBuf var1) {
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.name);
         var1.writeFloat(this.progress);
         var1.writeEnum(this.color);
         var1.writeEnum(this.overlay);
         var1.writeByte(ClientboundBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
      }
   }

   static record UpdateProgressOperation(float progress) implements Operation {
      private UpdateProgressOperation(RegistryFriendlyByteBuf var1) {
         this(var1.readFloat());
      }

      UpdateProgressOperation(float progress) {
         super();
         this.progress = progress;
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_PROGRESS;
      }

      public void dispatch(UUID var1, Handler var2) {
         var2.updateProgress(var1, this.progress);
      }

      public void write(RegistryFriendlyByteBuf var1) {
         var1.writeFloat(this.progress);
      }

      public float progress() {
         return this.progress;
      }
   }

   private static record UpdateNameOperation(Component name) implements Operation {
      private UpdateNameOperation(RegistryFriendlyByteBuf var1) {
         this((Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(var1));
      }

      UpdateNameOperation(Component name) {
         super();
         this.name = name;
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_NAME;
      }

      public void dispatch(UUID var1, Handler var2) {
         var2.updateName(var1, this.name);
      }

      public void write(RegistryFriendlyByteBuf var1) {
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(var1, this.name);
      }

      public Component name() {
         return this.name;
      }
   }

   static class UpdateStyleOperation implements Operation {
      private final BossEvent.BossBarColor color;
      private final BossEvent.BossBarOverlay overlay;

      UpdateStyleOperation(BossEvent.BossBarColor var1, BossEvent.BossBarOverlay var2) {
         super();
         this.color = var1;
         this.overlay = var2;
      }

      private UpdateStyleOperation(RegistryFriendlyByteBuf var1) {
         super();
         this.color = (BossEvent.BossBarColor)var1.readEnum(BossEvent.BossBarColor.class);
         this.overlay = (BossEvent.BossBarOverlay)var1.readEnum(BossEvent.BossBarOverlay.class);
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_STYLE;
      }

      public void dispatch(UUID var1, Handler var2) {
         var2.updateStyle(var1, this.color, this.overlay);
      }

      public void write(RegistryFriendlyByteBuf var1) {
         var1.writeEnum(this.color);
         var1.writeEnum(this.overlay);
      }
   }

   private static class UpdatePropertiesOperation implements Operation {
      private final boolean darkenScreen;
      private final boolean playMusic;
      private final boolean createWorldFog;

      UpdatePropertiesOperation(boolean var1, boolean var2, boolean var3) {
         super();
         this.darkenScreen = var1;
         this.playMusic = var2;
         this.createWorldFog = var3;
      }

      private UpdatePropertiesOperation(RegistryFriendlyByteBuf var1) {
         super();
         short var2 = var1.readUnsignedByte();
         this.darkenScreen = (var2 & 1) > 0;
         this.playMusic = (var2 & 2) > 0;
         this.createWorldFog = (var2 & 4) > 0;
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_PROPERTIES;
      }

      public void dispatch(UUID var1, Handler var2) {
         var2.updateProperties(var1, this.darkenScreen, this.playMusic, this.createWorldFog);
      }

      public void write(RegistryFriendlyByteBuf var1) {
         var1.writeByte(ClientboundBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
      }
   }

   public interface Handler {
      default void add(UUID var1, Component var2, float var3, BossEvent.BossBarColor var4, BossEvent.BossBarOverlay var5, boolean var6, boolean var7, boolean var8) {
      }

      default void remove(UUID var1) {
      }

      default void updateProgress(UUID var1, float var2) {
      }

      default void updateName(UUID var1, Component var2) {
      }

      default void updateStyle(UUID var1, BossEvent.BossBarColor var2, BossEvent.BossBarOverlay var3) {
      }

      default void updateProperties(UUID var1, boolean var2, boolean var3, boolean var4) {
      }
   }
}
