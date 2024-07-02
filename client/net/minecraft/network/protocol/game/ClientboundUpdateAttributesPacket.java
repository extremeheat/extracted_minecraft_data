package net.minecraft.network.protocol.game;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class ClientboundUpdateAttributesPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateAttributesPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.VAR_INT,
      ClientboundUpdateAttributesPacket::getEntityId,
      ClientboundUpdateAttributesPacket.AttributeSnapshot.STREAM_CODEC.apply(ByteBufCodecs.list()),
      ClientboundUpdateAttributesPacket::getValues,
      ClientboundUpdateAttributesPacket::new
   );
   private final int entityId;
   private final List<ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes;

   public ClientboundUpdateAttributesPacket(int var1, Collection<AttributeInstance> var2) {
      super();
      this.entityId = var1;
      this.attributes = Lists.newArrayList();

      for (AttributeInstance var4 : var2) {
         this.attributes.add(new ClientboundUpdateAttributesPacket.AttributeSnapshot(var4.getAttribute(), var4.getBaseValue(), var4.getModifiers()));
      }
   }

   private ClientboundUpdateAttributesPacket(int var1, List<ClientboundUpdateAttributesPacket.AttributeSnapshot> var2) {
      super();
      this.entityId = var1;
      this.attributes = var2;
   }

   @Override
   public PacketType<ClientboundUpdateAttributesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_ATTRIBUTES;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateAttributes(this);
   }

   public int getEntityId() {
      return this.entityId;
   }

   public List<ClientboundUpdateAttributesPacket.AttributeSnapshot> getValues() {
      return this.attributes;
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
