package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.flag.FeatureFlagSet;
import org.slf4j.Logger;

public class Pack {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackLocationInfo location;
   private final ResourcesSupplier resources;
   private final Metadata metadata;
   private final PackSelectionConfig selectionConfig;

   @Nullable
   public static Pack readMetaAndCreate(PackLocationInfo var0, ResourcesSupplier var1, PackType var2, PackSelectionConfig var3) {
      int var4 = SharedConstants.getCurrentVersion().getPackVersion(var2);
      Metadata var5 = readPackMetadata(var0, var1, var4);
      return var5 != null ? new Pack(var0, var1, var5, var3) : null;
   }

   public Pack(PackLocationInfo var1, ResourcesSupplier var2, Metadata var3, PackSelectionConfig var4) {
      super();
      this.location = var1;
      this.resources = var2;
      this.metadata = var3;
      this.selectionConfig = var4;
   }

   @Nullable
   public static Metadata readPackMetadata(PackLocationInfo var0, ResourcesSupplier var1, int var2) {
      try (PackResources var3 = var1.openPrimary(var0)) {
         PackMetadataSection var4 = (PackMetadataSection)var3.getMetadataSection(PackMetadataSection.TYPE);
         if (var4 == null) {
            LOGGER.warn("Missing metadata in pack {}", var0.id());
            return null;
         } else {
            FeatureFlagsMetadataSection var5 = (FeatureFlagsMetadataSection)var3.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
            FeatureFlagSet var6 = var5 != null ? var5.flags() : FeatureFlagSet.of();
            InclusiveRange var7 = getDeclaredPackVersions(var0.id(), var4);
            PackCompatibility var8 = PackCompatibility.forVersion(var7, var2);
            OverlayMetadataSection var9 = (OverlayMetadataSection)var3.getMetadataSection(OverlayMetadataSection.TYPE);
            List var10 = var9 != null ? var9.overlaysForVersion(var2) : List.of();
            return new Metadata(var4.description(), var8, var6, var10);
         }
      } catch (Exception var14) {
         LOGGER.warn("Failed to read pack {} metadata", var0.id(), var14);
         return null;
      }
   }

   private static InclusiveRange<Integer> getDeclaredPackVersions(String var0, PackMetadataSection var1) {
      int var2 = var1.packFormat();
      if (var1.supportedFormats().isEmpty()) {
         return new InclusiveRange<Integer>(var2);
      } else {
         InclusiveRange var3 = (InclusiveRange)var1.supportedFormats().get();
         if (!var3.isValueInRange(var2)) {
            LOGGER.warn("Pack {} declared support for versions {} but declared main format is {}, defaulting to {}", new Object[]{var0, var3, var2, var2});
            return new InclusiveRange<Integer>(var2);
         } else {
            return var3;
         }
      }
   }

   public PackLocationInfo location() {
      return this.location;
   }

   public Component getTitle() {
      return this.location.title();
   }

   public Component getDescription() {
      return this.metadata.description();
   }

   public Component getChatLink(boolean var1) {
      return this.location.createChatLink(var1, this.metadata.description);
   }

   public PackCompatibility getCompatibility() {
      return this.metadata.compatibility();
   }

   public FeatureFlagSet getRequestedFeatures() {
      return this.metadata.requestedFeatures();
   }

   public PackResources open() {
      return this.resources.openFull(this.location, this.metadata);
   }

   public String getId() {
      return this.location.id();
   }

   public PackSelectionConfig selectionConfig() {
      return this.selectionConfig;
   }

   public boolean isRequired() {
      return this.selectionConfig.required();
   }

   public boolean isFixedPosition() {
      return this.selectionConfig.fixedPosition();
   }

   public Position getDefaultPosition() {
      return this.selectionConfig.defaultPosition();
   }

   public PackSource getPackSource() {
      return this.location.source();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Pack)) {
         return false;
      } else {
         Pack var2 = (Pack)var1;
         return this.location.equals(var2.location);
      }
   }

   public int hashCode() {
      return this.location.hashCode();
   }

   public static record Metadata(Component description, PackCompatibility compatibility, FeatureFlagSet requestedFeatures, List<String> overlays) {
      final Component description;

      public Metadata(Component var1, PackCompatibility var2, FeatureFlagSet var3, List<String> var4) {
         super();
         this.description = var1;
         this.compatibility = var2;
         this.requestedFeatures = var3;
         this.overlays = var4;
      }
   }

   public static enum Position {
      TOP,
      BOTTOM;

      private Position() {
      }

      public <T> int insert(List<T> var1, T var2, Function<T, PackSelectionConfig> var3, boolean var4) {
         Position var5 = var4 ? this.opposite() : this;
         if (var5 == BOTTOM) {
            int var8;
            for(var8 = 0; var8 < var1.size(); ++var8) {
               PackSelectionConfig var9 = (PackSelectionConfig)var3.apply(var1.get(var8));
               if (!var9.fixedPosition() || var9.defaultPosition() != this) {
                  break;
               }
            }

            var1.add(var8, var2);
            return var8;
         } else {
            int var6;
            for(var6 = var1.size() - 1; var6 >= 0; --var6) {
               PackSelectionConfig var7 = (PackSelectionConfig)var3.apply(var1.get(var6));
               if (!var7.fixedPosition() || var7.defaultPosition() != this) {
                  break;
               }
            }

            var1.add(var6 + 1, var2);
            return var6 + 1;
         }
      }

      public Position opposite() {
         return this == TOP ? BOTTOM : TOP;
      }

      // $FF: synthetic method
      private static Position[] $values() {
         return new Position[]{TOP, BOTTOM};
      }
   }

   public interface ResourcesSupplier {
      PackResources openPrimary(PackLocationInfo var1);

      PackResources openFull(PackLocationInfo var1, Metadata var2);
   }
}
