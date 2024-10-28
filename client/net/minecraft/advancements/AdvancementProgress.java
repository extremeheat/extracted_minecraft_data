package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
   private static final DateTimeFormatter OBTAINED_TIME_FORMAT;
   private static final Codec<Instant> OBTAINED_TIME_CODEC;
   private static final Codec<Map<String, CriterionProgress>> CRITERIA_CODEC;
   public static final Codec<AdvancementProgress> CODEC;
   private final Map<String, CriterionProgress> criteria;
   private AdvancementRequirements requirements;

   private AdvancementProgress(Map<String, CriterionProgress> var1) {
      super();
      this.requirements = AdvancementRequirements.EMPTY;
      this.criteria = var1;
   }

   public AdvancementProgress() {
      super();
      this.requirements = AdvancementRequirements.EMPTY;
      this.criteria = Maps.newHashMap();
   }

   public void update(AdvancementRequirements var1) {
      Set var2 = var1.names();
      this.criteria.entrySet().removeIf((var1x) -> {
         return !var2.contains(var1x.getKey());
      });
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         this.criteria.putIfAbsent(var4, new CriterionProgress());
      }

      this.requirements = var1;
   }

   public boolean isDone() {
      return this.requirements.test(this::isCriterionDone);
   }

   public boolean hasProgress() {
      Iterator var1 = this.criteria.values().iterator();

      CriterionProgress var2;
      do {
         if (!var1.hasNext()) {
            return false;
         }

         var2 = (CriterionProgress)var1.next();
      } while(!var2.isDone());

      return true;
   }

   public boolean grantProgress(String var1) {
      CriterionProgress var2 = (CriterionProgress)this.criteria.get(var1);
      if (var2 != null && !var2.isDone()) {
         var2.grant();
         return true;
      } else {
         return false;
      }
   }

   public boolean revokeProgress(String var1) {
      CriterionProgress var2 = (CriterionProgress)this.criteria.get(var1);
      if (var2 != null && var2.isDone()) {
         var2.revoke();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.criteria);
      return "AdvancementProgress{criteria=" + var10000 + ", requirements=" + String.valueOf(this.requirements) + "}";
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
      var1.writeMap(this.criteria, FriendlyByteBuf::writeUtf, (var0, var1x) -> {
         var1x.serializeToNetwork(var0);
      });
   }

   public static AdvancementProgress fromNetwork(FriendlyByteBuf var0) {
      Map var1 = var0.readMap(FriendlyByteBuf::readUtf, CriterionProgress::fromNetwork);
      return new AdvancementProgress(var1);
   }

   @Nullable
   public CriterionProgress getCriterion(String var1) {
      return (CriterionProgress)this.criteria.get(var1);
   }

   private boolean isCriterionDone(String var1) {
      CriterionProgress var2 = this.getCriterion(var1);
      return var2 != null && var2.isDone();
   }

   public float getPercent() {
      if (this.criteria.isEmpty()) {
         return 0.0F;
      } else {
         float var1 = (float)this.requirements.size();
         float var2 = (float)this.countCompletedRequirements();
         return var2 / var1;
      }
   }

   @Nullable
   public Component getProgressText() {
      if (this.criteria.isEmpty()) {
         return null;
      } else {
         int var1 = this.requirements.size();
         if (var1 <= 1) {
            return null;
         } else {
            int var2 = this.countCompletedRequirements();
            return Component.translatable("advancements.progress", var2, var1);
         }
      }
   }

   private int countCompletedRequirements() {
      return this.requirements.count(this::isCriterionDone);
   }

   public Iterable<String> getRemainingCriteria() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.criteria.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if (!((CriterionProgress)var3.getValue()).isDone()) {
            var1.add((String)var3.getKey());
         }
      }

      return var1;
   }

   public Iterable<String> getCompletedCriteria() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.criteria.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         if (((CriterionProgress)var3.getValue()).isDone()) {
            var1.add((String)var3.getKey());
         }
      }

      return var1;
   }

   @Nullable
   public Instant getFirstProgressDate() {
      return (Instant)this.criteria.values().stream().map(CriterionProgress::getObtained).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse((Object)null);
   }

   public int compareTo(AdvancementProgress var1) {
      Instant var2 = this.getFirstProgressDate();
      Instant var3 = var1.getFirstProgressDate();
      if (var2 == null && var3 != null) {
         return 1;
      } else if (var2 != null && var3 == null) {
         return -1;
      } else {
         return var2 == null && var3 == null ? 0 : var2.compareTo(var3);
      }
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((AdvancementProgress)var1);
   }

   static {
      OBTAINED_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
      OBTAINED_TIME_CODEC = ExtraCodecs.temporalCodec(OBTAINED_TIME_FORMAT).xmap(Instant::from, (var0) -> {
         return var0.atZone(ZoneId.systemDefault());
      });
      CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, OBTAINED_TIME_CODEC).xmap((var0) -> {
         return (Map)var0.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, (var0x) -> {
            return new CriterionProgress((Instant)var0x.getValue());
         }));
      }, (var0) -> {
         return (Map)var0.entrySet().stream().filter((var0x) -> {
            return ((CriterionProgress)var0x.getValue()).isDone();
         }).collect(Collectors.toMap(Map.Entry::getKey, (var0x) -> {
            return (Instant)Objects.requireNonNull(((CriterionProgress)var0x.getValue()).getObtained());
         }));
      });
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(CRITERIA_CODEC.optionalFieldOf("criteria", Map.of()).forGetter((var0x) -> {
            return var0x.criteria;
         }), Codec.BOOL.fieldOf("done").orElse(true).forGetter(AdvancementProgress::isDone)).apply(var0, (var0x, var1) -> {
            return new AdvancementProgress(new HashMap(var0x));
         });
      });
   }
}
