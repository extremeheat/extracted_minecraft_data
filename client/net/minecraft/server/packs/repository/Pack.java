package net.minecraft.server.packs.repository;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.packs.FeatureFlagsMetadataSection;
import net.minecraft.server.packs.OverlayMetadataSection;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.util.InclusiveRange;
import net.minecraft.world.flag.FeatureFlagSet;
import org.slf4j.Logger;

public class Pack {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final String id;
   private final Pack.ResourcesSupplier resources;
   private final Component title;
   private final Pack.Info info;
   private final Pack.Position defaultPosition;
   private final boolean required;
   private final boolean fixedPosition;
   private final PackSource packSource;

   @Nullable
   public static Pack readMetaAndCreate(
      String var0, Component var1, boolean var2, Pack.ResourcesSupplier var3, PackType var4, Pack.Position var5, PackSource var6
   ) {
      int var7 = SharedConstants.getCurrentVersion().getPackVersion(var4);
      Pack.Info var8 = readPackInfo(var0, var3, var7);
      return var8 != null ? create(var0, var1, var2, var3, var8, var5, false, var6) : null;
   }

   public static Pack create(
      String var0, Component var1, boolean var2, Pack.ResourcesSupplier var3, Pack.Info var4, Pack.Position var5, boolean var6, PackSource var7
   ) {
      return new Pack(var0, var2, var3, var1, var4, var5, var6, var7);
   }

   private Pack(String var1, boolean var2, Pack.ResourcesSupplier var3, Component var4, Pack.Info var5, Pack.Position var6, boolean var7, PackSource var8) {
      super();
      this.id = var1;
      this.resources = var3;
      this.title = var4;
      this.info = var5;
      this.required = var2;
      this.defaultPosition = var6;
      this.fixedPosition = var7;
      this.packSource = var8;
   }

   @Nullable
   public static Pack.Info readPackInfo(String var0, Pack.ResourcesSupplier var1, int var2) {
      try {
         Pack.Info var11;
         try (PackResources var3 = var1.openPrimary(var0)) {
            PackMetadataSection var4 = var3.getMetadataSection(PackMetadataSection.TYPE);
            if (var4 == null) {
               LOGGER.warn("Missing metadata in pack {}", var0);
               return null;
            }

            FeatureFlagsMetadataSection var5 = var3.getMetadataSection(FeatureFlagsMetadataSection.TYPE);
            FeatureFlagSet var6 = var5 != null ? var5.flags() : FeatureFlagSet.of();
            InclusiveRange var7 = getDeclaredPackVersions(var0, var4);
            PackCompatibility var8 = PackCompatibility.forVersion(var7, var2);
            OverlayMetadataSection var9 = var3.getMetadataSection(OverlayMetadataSection.TYPE);
            List var10 = var9 != null ? var9.overlaysForVersion(var2) : List.of();
            var11 = new Pack.Info(var4.description(), var8, var6, var10);
         }

         return var11;
      } catch (Exception var14) {
         LOGGER.warn("Failed to read pack {} metadata", var0, var14);
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

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.info.description();
   }

   public Component getChatLink(boolean var1) {
      return ComponentUtils.wrapInSquareBrackets(this.packSource.decorate(Component.literal(this.id)))
         .withStyle(
            var2 -> var2.withColor(var1 ? ChatFormatting.GREEN : ChatFormatting.RED)
                  .withInsertion(StringArgumentType.escapeIfRequired(this.id))
                  .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.empty().append(this.title).append("\n").append(this.info.description)))
         );
   }

   public PackCompatibility getCompatibility() {
      return this.info.compatibility();
   }

   public FeatureFlagSet getRequestedFeatures() {
      return this.info.requestedFeatures();
   }

   public PackResources open() {
      return this.resources.openFull(this.id, this.info);
   }

   public String getId() {
      return this.id;
   }

   public boolean isRequired() {
      return this.required;
   }

   public boolean isFixedPosition() {
      return this.fixedPosition;
   }

   public Pack.Position getDefaultPosition() {
      return this.defaultPosition;
   }

   public PackSource getPackSource() {
      return this.packSource;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Pack)) {
         return false;
      } else {
         Pack var2 = (Pack)var1;
         return this.id.equals(var2.id);
      }
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   public static record Info(Component a, PackCompatibility b, FeatureFlagSet c, List<String> d) {
      final Component description;
      private final PackCompatibility compatibility;
      private final FeatureFlagSet requestedFeatures;
      private final List<String> overlays;

      public Info(Component var1, PackCompatibility var2, FeatureFlagSet var3, List<String> var4) {
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

      public <T> int insert(List<T> var1, T var2, Function<T, Pack> var3, boolean var4) {
         Pack.Position var5 = var4 ? this.opposite() : this;
         if (var5 == BOTTOM) {
            int var8;
            for(var8 = 0; var8 < var1.size(); ++var8) {
               Pack var9 = (Pack)var3.apply(var1.get(var8));
               if (!var9.isFixedPosition() || var9.getDefaultPosition() != this) {
                  break;
               }
            }

            var1.add(var8, var2);
            return var8;
         } else {
            int var6;
            for(var6 = var1.size() - 1; var6 >= 0; --var6) {
               Pack var7 = (Pack)var3.apply(var1.get(var6));
               if (!var7.isFixedPosition() || var7.getDefaultPosition() != this) {
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
      PackResources openPrimary(String var1);

      PackResources openFull(String var1, Pack.Info var2);
   }
}
