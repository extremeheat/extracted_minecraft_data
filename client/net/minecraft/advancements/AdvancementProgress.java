package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class AdvancementProgress implements Comparable<AdvancementProgress> {
   private static final DateTimeFormatter OBTAINED_TIME_FORMAT;
   private static final Codec<Instant> OBTAINED_TIME_CODEC;
   private static final Codec<Map<String, CriterionProgress>> CRITERIA_CODEC;
   public static final Codec<AdvancementProgress> CODEC;
   private final Map<String, CriterionProgress> criteria;
   private AdvancementRequirements requirements;

   private AdvancementProgress(final Map<String, CriterionProgress> criteria) {
      super();
      this.requirements = AdvancementRequirements.EMPTY;
      this.criteria = criteria;
   }

   public AdvancementProgress() {
      super();
      this.requirements = AdvancementRequirements.EMPTY;
      this.criteria = Maps.newHashMap();
   }

   public void update(final AdvancementRequirements requirements) {
      Set<String> names = requirements.names();
      this.criteria.entrySet().removeIf((entry) -> !names.contains(entry.getKey()));

      for(String name : names) {
         this.criteria.putIfAbsent(name, new CriterionProgress());
      }

      this.requirements = requirements;
   }

   public boolean isDone() {
      return this.requirements.test(this::isCriterionDone);
   }

   public boolean hasProgress() {
      for(CriterionProgress progress : this.criteria.values()) {
         if (progress.isDone()) {
            return true;
         }
      }

      return false;
   }

   public boolean grantProgress(final String name) {
      CriterionProgress progress = (CriterionProgress)this.criteria.get(name);
      if (progress != null && !progress.isDone()) {
         progress.grant();
         return true;
      } else {
         return false;
      }
   }

   public boolean revokeProgress(final String name) {
      CriterionProgress progress = (CriterionProgress)this.criteria.get(name);
      if (progress != null && progress.isDone()) {
         progress.revoke();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      String var10000 = String.valueOf(this.criteria);
      return "AdvancementProgress{criteria=" + var10000 + ", requirements=" + String.valueOf(this.requirements) + "}";
   }

   public void serializeToNetwork(final FriendlyByteBuf output) {
      output.writeMap(this.criteria, FriendlyByteBuf::writeUtf, (b, v) -> v.serializeToNetwork(b));
   }

   public static AdvancementProgress fromNetwork(final FriendlyByteBuf input) {
      Map<String, CriterionProgress> criteria = input.<String, CriterionProgress>readMap(FriendlyByteBuf::readUtf, CriterionProgress::fromNetwork);
      return new AdvancementProgress(criteria);
   }

   public @Nullable CriterionProgress getCriterion(final String id) {
      return (CriterionProgress)this.criteria.get(id);
   }

   private boolean isCriterionDone(final String criterion) {
      CriterionProgress progress = this.getCriterion(criterion);
      return progress != null && progress.isDone();
   }

   public float getPercent() {
      if (this.criteria.isEmpty()) {
         return 0.0F;
      } else {
         float total = (float)this.requirements.size();
         float complete = (float)this.countCompletedRequirements();
         return complete / total;
      }
   }

   public @Nullable Component getProgressText() {
      if (this.criteria.isEmpty()) {
         return null;
      } else {
         int total = this.requirements.size();
         if (total <= 1) {
            return null;
         } else {
            int complete = this.countCompletedRequirements();
            return Component.translatable("advancements.progress", complete, total);
         }
      }
   }

   private int countCompletedRequirements() {
      return this.requirements.count(this::isCriterionDone);
   }

   public Iterable<String> getRemainingCriteria() {
      List<String> remaining = Lists.newArrayList();

      for(Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
         if (!((CriterionProgress)entry.getValue()).isDone()) {
            remaining.add((String)entry.getKey());
         }
      }

      return remaining;
   }

   public Iterable<String> getCompletedCriteria() {
      List<String> completed = Lists.newArrayList();

      for(Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
         if (((CriterionProgress)entry.getValue()).isDone()) {
            completed.add((String)entry.getKey());
         }
      }

      return completed;
   }

   public @Nullable Instant getFirstProgressDate() {
      return (Instant)this.criteria.values().stream().map(CriterionProgress::getObtained).filter(Objects::nonNull).min(Comparator.naturalOrder()).orElse((Object)null);
   }

   public int compareTo(final AdvancementProgress o) {
      Instant ourSmallestDate = this.getFirstProgressDate();
      Instant theirSmallestDate = o.getFirstProgressDate();
      if (ourSmallestDate == null && theirSmallestDate != null) {
         return 1;
      } else if (ourSmallestDate != null && theirSmallestDate == null) {
         return -1;
      } else {
         return ourSmallestDate == null && theirSmallestDate == null ? 0 : ourSmallestDate.compareTo(theirSmallestDate);
      }
   }

   static {
      OBTAINED_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT);
      OBTAINED_TIME_CODEC = ExtraCodecs.temporalCodec(OBTAINED_TIME_FORMAT).xmap(Instant::from, (instant) -> instant.atZone(ZoneId.systemDefault()));
      CRITERIA_CODEC = Codec.unboundedMap(Codec.STRING, OBTAINED_TIME_CODEC).xmap((map) -> Util.mapValues(map, CriterionProgress::new), (map) -> (Map)map.entrySet().stream().filter((e) -> ((CriterionProgress)e.getValue()).isDone()).collect(Collectors.toMap(Map.Entry::getKey, (e) -> (Instant)Objects.requireNonNull(((CriterionProgress)e.getValue()).getObtained()))));
      CODEC = RecordCodecBuilder.create((i) -> i.group(CRITERIA_CODEC.optionalFieldOf("criteria", Map.of()).forGetter((a) -> a.criteria), ExtraCodecs.optionalAlwaysPresentFieldOf(Codec.BOOL, "done", true).forGetter(AdvancementProgress::isDone)).apply(i, (criteria, done) -> new AdvancementProgress(new HashMap(criteria))));
   }
}
