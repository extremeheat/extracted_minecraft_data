package net.minecraft.client.gui.components.debug;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StrictJsonParser;
import net.minecraft.util.datafix.DataFixTypes;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;

public class DebugScreenEntryList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int DEFAULT_DEBUG_PROFILE_VERSION = 4649;
   private Map<ResourceLocation, DebugScreenEntryStatus> allStatuses;
   private final List<ResourceLocation> currentlyEnabled = new ArrayList();
   private boolean isOverlayVisible = false;
   @Nullable
   private DebugScreenProfile profile;
   private final File debugProfileFile;
   private long currentlyEnabledVersion;
   private final Codec<SerializedOptions> codec;

   public DebugScreenEntryList(File var1) {
      super();
      this.debugProfileFile = new File(var1, "debug-profile.json");
      this.codec = DataFixTypes.DEBUG_PROFILE.<SerializedOptions>wrapCodec(DebugScreenEntryList.SerializedOptions.CODEC, Minecraft.getInstance().getFixerUpper(), 4649);
      this.load();
   }

   public void load() {
      try {
         if (!this.debugProfileFile.isFile()) {
            this.loadDefaultProfile();
            this.rebuildCurrentList();
            return;
         }

         Dynamic var1 = new Dynamic(JsonOps.INSTANCE, StrictJsonParser.parse(FileUtils.readFileToString(this.debugProfileFile, StandardCharsets.UTF_8)));
         SerializedOptions var2 = (SerializedOptions)this.codec.parse(var1).getOrThrow((var0) -> new IOException("Could not parse debug profile JSON: " + var0));
         if (var2.profile().isPresent()) {
            this.loadProfile((DebugScreenProfile)var2.profile().get());
         } else {
            this.allStatuses = new HashMap();
            if (var2.custom().isPresent()) {
               this.allStatuses.putAll((Map)var2.custom().get());
            }

            this.profile = null;
         }
      } catch (JsonSyntaxException | IOException var3) {
         LOGGER.error("Couldn't read debug profile file {}, resetting to default", this.debugProfileFile, var3);
         this.loadDefaultProfile();
         this.save();
      }

      this.rebuildCurrentList();
   }

   public void loadProfile(DebugScreenProfile var1) {
      this.profile = var1;
      Map var2 = (Map)DebugScreenEntries.PROFILES.get(var1);
      this.allStatuses = new HashMap(var2);
      this.rebuildCurrentList();
   }

   private void loadDefaultProfile() {
      this.profile = DebugScreenProfile.DEFAULT;
      this.allStatuses = new HashMap((Map)DebugScreenEntries.PROFILES.get(DebugScreenProfile.DEFAULT));
   }

   public DebugScreenEntryStatus getStatus(ResourceLocation var1) {
      DebugScreenEntryStatus var2 = (DebugScreenEntryStatus)this.allStatuses.get(var1);
      return var2 == null ? DebugScreenEntryStatus.NEVER : var2;
   }

   public boolean isCurrentlyEnabled(ResourceLocation var1) {
      return this.currentlyEnabled.contains(var1);
   }

   public void setStatus(ResourceLocation var1, DebugScreenEntryStatus var2) {
      this.profile = null;
      this.allStatuses.put(var1, var2);
      this.rebuildCurrentList();
      this.save();
   }

   public boolean toggleStatus(ResourceLocation var1) {
      DebugScreenEntryStatus var2 = (DebugScreenEntryStatus)this.allStatuses.get(var1);
      byte var4 = 0;
      //$FF: var4->value
      //0->ALWAYS_ON
      //1->IN_OVERLAY
      //2->NEVER
      switch (var2.enumSwitch<invokedynamic>(var2, var4)) {
         case -1:
         default:
            this.setStatus(var1, DebugScreenEntryStatus.ALWAYS_ON);
            return true;
         case 0:
            this.setStatus(var1, DebugScreenEntryStatus.NEVER);
            return false;
         case 1:
            if (this.isOverlayVisible) {
               this.setStatus(var1, DebugScreenEntryStatus.NEVER);
               return false;
            }

            this.setStatus(var1, DebugScreenEntryStatus.ALWAYS_ON);
            return true;
         case 2:
            if (this.isOverlayVisible) {
               this.setStatus(var1, DebugScreenEntryStatus.IN_OVERLAY);
            } else {
               this.setStatus(var1, DebugScreenEntryStatus.ALWAYS_ON);
            }

            return true;
      }
   }

   public Collection<ResourceLocation> getCurrentlyEnabled() {
      return this.currentlyEnabled;
   }

   public void toggleDebugOverlay() {
      this.setOverlayVisible(!this.isOverlayVisible);
   }

   public void setOverlayVisible(boolean var1) {
      if (this.isOverlayVisible != var1) {
         this.isOverlayVisible = var1;
         this.rebuildCurrentList();
      }

   }

   public boolean isOverlayVisible() {
      return this.isOverlayVisible;
   }

   public void rebuildCurrentList() {
      this.currentlyEnabled.clear();
      boolean var1 = Minecraft.getInstance().showOnlyReducedInfo();

      for(Map.Entry var3 : this.allStatuses.entrySet()) {
         if (var3.getValue() == DebugScreenEntryStatus.ALWAYS_ON || this.isOverlayVisible && var3.getValue() == DebugScreenEntryStatus.IN_OVERLAY) {
            DebugScreenEntry var4 = DebugScreenEntries.getEntry((ResourceLocation)var3.getKey());
            if (var4 != null && var4.isAllowed(var1)) {
               this.currentlyEnabled.add((ResourceLocation)var3.getKey());
            }
         }
      }

      this.currentlyEnabled.sort(ResourceLocation::compareTo);
      ++this.currentlyEnabledVersion;
   }

   public long getCurrentlyEnabledVersion() {
      return this.currentlyEnabledVersion;
   }

   public boolean isUsingProfile(DebugScreenProfile var1) {
      return this.profile == var1;
   }

   public void save() {
      SerializedOptions var1 = new SerializedOptions(Optional.ofNullable(this.profile), this.profile == null ? Optional.of(this.allStatuses) : Optional.empty());

      try {
         FileUtils.writeStringToFile(this.debugProfileFile, ((JsonElement)this.codec.encodeStart(JsonOps.INSTANCE, var1).getOrThrow()).toString(), StandardCharsets.UTF_8);
      } catch (IOException var3) {
         LOGGER.error("Failed to save debug profile file {}", this.debugProfileFile, var3);
      }

   }

   static record SerializedOptions(Optional<DebugScreenProfile> profile, Optional<Map<ResourceLocation, DebugScreenEntryStatus>> custom) {
      private static final Codec<Map<ResourceLocation, DebugScreenEntryStatus>> CUSTOM_ENTRIES_CODEC;
      public static final Codec<SerializedOptions> CODEC;

      SerializedOptions(Optional<DebugScreenProfile> var1, Optional<Map<ResourceLocation, DebugScreenEntryStatus>> var2) {
         super();
         this.profile = var1;
         this.custom = var2;
      }

      static {
         CUSTOM_ENTRIES_CODEC = Codec.unboundedMap(ResourceLocation.CODEC, DebugScreenEntryStatus.CODEC);
         CODEC = RecordCodecBuilder.create((var0) -> var0.group(DebugScreenProfile.CODEC.optionalFieldOf("profile").forGetter(SerializedOptions::profile), CUSTOM_ENTRIES_CODEC.optionalFieldOf("custom").forGetter(SerializedOptions::custom)).apply(var0, SerializedOptions::new));
      }
   }
}
