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
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundBossEventPacket> STREAM_CODEC = Packet.<RegistryFriendlyByteBuf, ClientboundBossEventPacket>codec(ClientboundBossEventPacket::write, ClientboundBossEventPacket::new);
   private static final int FLAG_DARKEN = 1;
   private static final int FLAG_MUSIC = 2;
   private static final int FLAG_FOG = 4;
   private final UUID id;
   private final Operation operation;
   private static final Operation REMOVE_OPERATION = new Operation() {
      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.REMOVE;
      }

      public void dispatch(final UUID id, final Handler handler) {
         handler.remove(id);
      }

      public void write(final RegistryFriendlyByteBuf output) {
      }
   };

   private ClientboundBossEventPacket(final UUID id, final Operation operation) {
      super();
      this.id = id;
      this.operation = operation;
   }

   private ClientboundBossEventPacket(final RegistryFriendlyByteBuf input) {
      super();
      this.id = input.readUUID();
      OperationType type = (OperationType)input.readEnum(OperationType.class);
      this.operation = type.reader.decode(input);
   }

   public static ClientboundBossEventPacket createAddPacket(final BossEvent event) {
      return new ClientboundBossEventPacket(event.getId(), new AddOperation(event));
   }

   public static ClientboundBossEventPacket createRemovePacket(final UUID id) {
      return new ClientboundBossEventPacket(id, REMOVE_OPERATION);
   }

   public static ClientboundBossEventPacket createUpdateProgressPacket(final BossEvent event) {
      return new ClientboundBossEventPacket(event.getId(), new UpdateProgressOperation(event.getProgress()));
   }

   public static ClientboundBossEventPacket createUpdateNamePacket(final BossEvent event) {
      return new ClientboundBossEventPacket(event.getId(), new UpdateNameOperation(event.getName()));
   }

   public static ClientboundBossEventPacket createUpdateStylePacket(final BossEvent event) {
      return new ClientboundBossEventPacket(event.getId(), new UpdateStyleOperation(event.getColor(), event.getOverlay()));
   }

   public static ClientboundBossEventPacket createUpdatePropertiesPacket(final BossEvent event) {
      return new ClientboundBossEventPacket(event.getId(), new UpdatePropertiesOperation(event.shouldDarkenScreen(), event.shouldPlayBossMusic(), event.shouldCreateWorldFog()));
   }

   private void write(final RegistryFriendlyByteBuf output) {
      output.writeUUID(this.id);
      output.writeEnum(this.operation.getType());
      this.operation.write(output);
   }

   private static int encodeProperties(final boolean darkenScreen, final boolean playMusic, final boolean createWorldFog) {
      int properties = 0;
      if (darkenScreen) {
         properties |= 1;
      }

      if (playMusic) {
         properties |= 2;
      }

      if (createWorldFog) {
         properties |= 4;
      }

      return properties;
   }

   public PacketType<ClientboundBossEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_BOSS_EVENT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleBossUpdate(this);
   }

   public void dispatch(final Handler handler) {
      this.operation.dispatch(this.id, handler);
   }

   private static enum OperationType {
      ADD(AddOperation::new),
      REMOVE((input) -> ClientboundBossEventPacket.REMOVE_OPERATION),
      UPDATE_PROGRESS(UpdateProgressOperation::new),
      UPDATE_NAME(UpdateNameOperation::new),
      UPDATE_STYLE(UpdateStyleOperation::new),
      UPDATE_PROPERTIES(UpdatePropertiesOperation::new);

      private final StreamDecoder<RegistryFriendlyByteBuf, Operation> reader;

      private OperationType(final StreamDecoder<RegistryFriendlyByteBuf, Operation> reader) {
         this.reader = reader;
      }

      // $FF: synthetic method
      private static OperationType[] $values() {
         return new OperationType[]{ADD, REMOVE, UPDATE_PROGRESS, UPDATE_NAME, UPDATE_STYLE, UPDATE_PROPERTIES};
      }
   }

   public interface Handler {
      default void add(final UUID id, final Component name, final float progress, final BossEvent.BossBarColor color, final BossEvent.BossBarOverlay overlay, final boolean darkenScreen, final boolean playMusic, final boolean createWorldFog) {
      }

      default void remove(final UUID id) {
      }

      default void updateProgress(final UUID id, final float progress) {
      }

      default void updateName(final UUID id, final Component name) {
      }

      default void updateStyle(final UUID id, final BossEvent.BossBarColor color, final BossEvent.BossBarOverlay overlay) {
      }

      default void updateProperties(final UUID id, final boolean darkenScreen, final boolean playMusic, final boolean createWorldFog) {
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

      private AddOperation(final BossEvent event) {
         super();
         this.name = event.getName();
         this.progress = event.getProgress();
         this.color = event.getColor();
         this.overlay = event.getOverlay();
         this.darkenScreen = event.shouldDarkenScreen();
         this.playMusic = event.shouldPlayBossMusic();
         this.createWorldFog = event.shouldCreateWorldFog();
      }

      private AddOperation(final RegistryFriendlyByteBuf input) {
         super();
         this.name = (Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input);
         this.progress = input.readFloat();
         this.color = (BossEvent.BossBarColor)input.readEnum(BossEvent.BossBarColor.class);
         this.overlay = (BossEvent.BossBarOverlay)input.readEnum(BossEvent.BossBarOverlay.class);
         int flags = input.readUnsignedByte();
         this.darkenScreen = (flags & 1) > 0;
         this.playMusic = (flags & 2) > 0;
         this.createWorldFog = (flags & 4) > 0;
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.ADD;
      }

      public void dispatch(final UUID id, final Handler handler) {
         handler.add(id, this.name, this.progress, this.color, this.overlay, this.darkenScreen, this.playMusic, this.createWorldFog);
      }

      public void write(final RegistryFriendlyByteBuf output) {
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, this.name);
         output.writeFloat(this.progress);
         output.writeEnum(this.color);
         output.writeEnum(this.overlay);
         output.writeByte(ClientboundBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
      }
   }

   private static record UpdateProgressOperation(float progress) implements Operation {
      private UpdateProgressOperation(final RegistryFriendlyByteBuf input) {
         this(input.readFloat());
      }

      private UpdateProgressOperation {
         super();
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_PROGRESS;
      }

      public void dispatch(final UUID id, final Handler handler) {
         handler.updateProgress(id, this.progress);
      }

      public void write(final RegistryFriendlyByteBuf output) {
         output.writeFloat(this.progress);
      }
   }

   private static record UpdateNameOperation(Component name) implements Operation {
      private UpdateNameOperation(final RegistryFriendlyByteBuf input) {
         this((Component)ComponentSerialization.TRUSTED_STREAM_CODEC.decode(input));
      }

      private UpdateNameOperation {
         super();
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_NAME;
      }

      public void dispatch(final UUID id, final Handler handler) {
         handler.updateName(id, this.name);
      }

      public void write(final RegistryFriendlyByteBuf output) {
         ComponentSerialization.TRUSTED_STREAM_CODEC.encode(output, this.name);
      }
   }

   private static class UpdateStyleOperation implements Operation {
      private final BossEvent.BossBarColor color;
      private final BossEvent.BossBarOverlay overlay;

      private UpdateStyleOperation(final BossEvent.BossBarColor color, final BossEvent.BossBarOverlay overlay) {
         super();
         this.color = color;
         this.overlay = overlay;
      }

      private UpdateStyleOperation(final RegistryFriendlyByteBuf input) {
         super();
         this.color = (BossEvent.BossBarColor)input.readEnum(BossEvent.BossBarColor.class);
         this.overlay = (BossEvent.BossBarOverlay)input.readEnum(BossEvent.BossBarOverlay.class);
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_STYLE;
      }

      public void dispatch(final UUID id, final Handler handler) {
         handler.updateStyle(id, this.color, this.overlay);
      }

      public void write(final RegistryFriendlyByteBuf output) {
         output.writeEnum(this.color);
         output.writeEnum(this.overlay);
      }
   }

   private static class UpdatePropertiesOperation implements Operation {
      private final boolean darkenScreen;
      private final boolean playMusic;
      private final boolean createWorldFog;

      private UpdatePropertiesOperation(final boolean darkenScreen, final boolean playMusic, final boolean createWorldFog) {
         super();
         this.darkenScreen = darkenScreen;
         this.playMusic = playMusic;
         this.createWorldFog = createWorldFog;
      }

      private UpdatePropertiesOperation(final RegistryFriendlyByteBuf input) {
         super();
         int flags = input.readUnsignedByte();
         this.darkenScreen = (flags & 1) > 0;
         this.playMusic = (flags & 2) > 0;
         this.createWorldFog = (flags & 4) > 0;
      }

      public OperationType getType() {
         return ClientboundBossEventPacket.OperationType.UPDATE_PROPERTIES;
      }

      public void dispatch(final UUID id, final Handler handler) {
         handler.updateProperties(id, this.darkenScreen, this.playMusic, this.createWorldFog);
      }

      public void write(final RegistryFriendlyByteBuf output) {
         output.writeByte(ClientboundBossEventPacket.encodeProperties(this.darkenScreen, this.playMusic, this.createWorldFog));
      }
   }

   private interface Operation {
      OperationType getType();

      void dispatch(UUID id, Handler handler);

      void write(RegistryFriendlyByteBuf output);
   }
}
