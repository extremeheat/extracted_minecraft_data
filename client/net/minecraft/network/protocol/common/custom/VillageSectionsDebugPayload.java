package net.minecraft.network.protocol.common.custom;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.SectionPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record VillageSectionsDebugPayload(Set<SectionPos> b, Set<SectionPos> c) implements CustomPacketPayload {
   private final Set<SectionPos> villageChunks;
   private final Set<SectionPos> notVillageChunks;
   public static final ResourceLocation ID = new ResourceLocation("debug/village_sections");

   public VillageSectionsDebugPayload(FriendlyByteBuf var1) {
      this(var1.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos), var1.readCollection(HashSet::new, FriendlyByteBuf::readSectionPos));
   }

   public VillageSectionsDebugPayload(Set<SectionPos> var1, Set<SectionPos> var2) {
      super();
      this.villageChunks = var1;
      this.notVillageChunks = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeCollection(this.villageChunks, FriendlyByteBuf::writeSectionPos);
      var1.writeCollection(this.notVillageChunks, FriendlyByteBuf::writeSectionPos);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
