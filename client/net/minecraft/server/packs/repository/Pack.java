package net.minecraft.server.packs.repository;

import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackFormat;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class Pack {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final PackLocationInfo location;
   private final ResourcesSupplier resources;
   private final Metadata metadata;
   private final PackSelectionConfig selectionConfig;

   public static @Nullable Pack readMetaAndCreate(PackLocationInfo var0, ResourcesSupplier var1, PackType var2, PackSelectionConfig var3) {
      PackFormat var4 = SharedConstants.getCurrentVersion().packVersion(var2);
      Metadata var5 = readPackMetadata(var0, var1, var4, var2);
      return var5 != null ? new Pack(var0, var1, var5, var3) : null;
   }

   public Pack(PackLocationInfo var1, ResourcesSupplier var2, Metadata var3, PackSelectionConfig var4) {
      super();
      this.location = var1;
      this.resources = var2;
      this.metadata = var3;
      this.selectionConfig = var4;
   }

   public static @Nullable Metadata readPackMetadata(PackLocationInfo var0, ResourcesSupplier var1, PackFormat var2, PackType var3) {
      try (PackResources var4 = var1.openPrimary(var0)) {
         PackMetadataSection var5 = (PackMetadataSection)var4.getMetadataSection(PackMetadataSection.forPackType(var3));
         if (var5 == null) {
            var5 = (PackMetadataSection)var4.getMetadataSection(PackMetadataSection.FALLBACK_TYPE);
         }

         if (var5 == null) {
            LOGGER.warn("Missing metadata in pack {}", var0.id());
            return null;
         } else {
            FeatureFlagsMetadataSection var6 = (FeatureFlagsMetadataSection)var4.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
            FeatureFlagSet var7 = var6 != null ? var6.flags() : FeatureFlagSet.of();
            PackCompatibility var8 = PackCompatibility.forVersion(var5.supportedFormats(), var2);
            OverlayMetadataSection var9 = (OverlayMetadataSection)var4.getMetadataSection(OverlayMetadataSection.forPackType(var3));
            List var10 = var9 != null ? var9.overlaysForVersion(var2) : List.of();
            return new Metadata(var5.description(), var8, var7, var10);
         }
      } catch (Exception var14) {
         LOGGER.warn("Failed to read pack {} metadata", var0.id(), var14);
         return null;
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
