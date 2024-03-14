package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record VillageSectionsDebugPayload(Set<SectionPos> c, Set<SectionPos> d) implements CustomPacketPayload {
   private final Set<SectionPos> villageChunks;
   private final Set<SectionPos> notVillageChunks;
   public static final StreamCodec<FriendlyByteBuf, VillageSectionsDebugPayload> STREAM_CODEC = CustomPacketPayload.codec(
      VillageSectionsDebugPayload::write, VillageSectionsDebugPayload::new
   );
   public static final CustomPacketPayload.Type<VillageSectionsDebugPayload> TYPE = CustomPacketPayload.createType("debug/village_sections");

   private VillageSectionsDebugPayload(FriendlyByteBuf var1) {
      this(var1.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos), var1.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos));
   }

   public VillageSectionsDebugPayload(Set<SectionPos> var1, Set<SectionPos> var2) {
      super();
      this.villageChunks = var1;
      this.notVillageChunks = var2;
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.villageChunks, FriendlyByteBuf::writeSectionPos);
      var1.writeCollection(this.notVillageChunks, FriendlyByteBuf::writeSectionPos);
   }

   @Override
   public CustomPacketPayload.Type<VillageSectionsDebugPayload> type() {
      return TYPE;
   }
}
