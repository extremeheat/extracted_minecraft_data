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
   private final Pack.ResourcesSupplier resources;
   private final Pack.Metadata metadata;
   private final PackSelectionConfig selectionConfig;

   @Nullable
   public static Pack readMetaAndCreate(PackLocationInfo var0, Pack.ResourcesSupplier var1, PackType var2, PackSelectionConfig var3) {
      int var4 = SharedConstants.getCurrentVersion().getPackVersion(var2);
      Pack.Metadata var5 = readPackMetadata(var0, var1, var4);
      return var5 != null ? new Pack(var0, var1, var5, var3) : null;
   }

   public Pack(PackLocationInfo var1, Pack.ResourcesSupplier var2, Pack.Metadata var3, PackSelectionConfig var4) {
      super();
      this.location = var1;
      this.resources = var2;
      this.metadata = var3;
      this.selectionConfig = var4;
   }

   @Nullable
   public static Pack.Metadata readPackMetadata(PackLocationInfo var0, Pack.ResourcesSupplier var1, int var2) {
      try {
         Pack.Metadata var11;
         try (PackResources var3 = var1.openPrimary(var0)) {
            PackMetadataSection var4 = var3.getMetadataSection(PackMetadataSection.TYPE);
            if (var4 == null) {
               LOGGER.warn("Missing metadata in pack {}", var0.id());
               return null;
            }

            FeatureFlagsMetadataSection var5 = var3.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
            FeatureFlagSet var6 = var5 != null ? var5.flags() : FeatureFlagSet.of();
            InclusiveRange var7 = getDeclaredPackVersions(var0.id(), var4);
            PackCompatibility var8 = PackCompatibility.forVersion(var7, var2);
            OverlayMetadataSection var9 = var3.getMetadataSection(OverlayMetadataSection.TYPE);
            List var10 = var9 != null ? var9.overlaysForVersion(var2) : List.of();
            var11 = new Pack.Metadata(var4.description(), var8, var6, var10);
         }

         return var11;
      } catch (Exception var14) {
         LOGGER.warn("Failed to read pack {} metadata", var0.id(), var14);
         return null;
      }
   }

   private static InclusiveRange<Integer> getDeclaredPackVersions(String var0, PackMetadataSection var1) {
      int var2 = var1.packFormat();
      if (var1.supportedFormats().isEmpty()) {
         return new InclusiveRange<>(var2);
      } else {
         InclusiveRange var3 = var1.supportedFormats().get();
         if (!var3.isValueInRange(var2)) {
            LOGGER.warn("Pack {} declared support for versions {} but declared main format is {}, defaulting to {}", new Object[]{var0, var3, var2, var2});
            return new InclusiveRange<>(var2);
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

   public Pack.Position getDefaultPosition() {
      return this.selectionConfig.defaultPosition();
   }

   public PackSource getPackSource() {
      return this.location.source();
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof Pack var2) ? false : this.location.equals(var2.location);
      }
   }

   @Override
   public int hashCode() {
      return this.location.hashCode();
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

   public static enum Position {
      TOP,
      BOTTOM;

      private Position() {
      }

      public <T> int insert(List<T> var1, T var2, Function<T, PackSelectionConfig> var3, boolean var4) {
         Pack.Position var5 = var4 ? this.opposite() : this;
         if (var5 == BOTTOM) {
            int var8;
            for (var8 = 0; var8 < var1.size(); var8++) {
               PackSelectionConfig var9 = (PackSelectionConfig)var3.apply(var1.get(var8));
               if (!var9.fixedPosition() || var9.defaultPosition() != this) {
                  break;
               }
            }

            var1.add(var8, var2);
            return var8;
         } else {
            int var6;
            for (var6 = var1.size() - 1; var6 >= 0; var6--) {
               PackSelectionConfig var7 = (PackSelectionConfig)var3.apply(var1.get(var6));
               if (!var7.fixedPosition() || var7.defaultPosition() != this) {
                  break;
               }
            }

            var1.add(var6 + 1, var2);
            return var6 + 1;
         }
      }

      public Pack.Position opposite() {
         return this == TOP ? BOTTOM : TOP;
      }
   }

   public interface ResourcesSupplier {
      PackResources openPrimary(PackLocationInfo var1);

      PackResources openFull(PackLocationInfo var1, Pack.Metadata var2);
   }
}
