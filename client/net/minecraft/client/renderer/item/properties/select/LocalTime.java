package net.minecraft.client.renderer.item.properties.select;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class LocalTime implements SelectItemModelProperty<String> {
   public static final String ROOT_LOCALE = "";
   private static final long UPDATE_INTERVAL_MS;
   public static final Codec<String> VALUE_CODEC;
   private static final Codec<TimeZone> TIME_ZONE_CODEC;
   private static final MapCodec<Data> DATA_MAP_CODEC;
   public static final SelectItemModelProperty.Type<LocalTime, String> TYPE;
   private final Data data;
   private final DateFormat parsedFormat;
   private long nextUpdateTimeMs;
   private String lastResult = "";

   private LocalTime(final Data data, final DateFormat parsedFormat) {
      super();
      this.data = data;
      this.parsedFormat = parsedFormat;
   }

   public static LocalTime create(final String format, final String localeId, final Optional<TimeZone> timeZone) {
      return (LocalTime)create(new Data(format, localeId, timeZone)).getOrThrow((msg) -> new IllegalStateException("Failed to validate format: " + msg));
   }

   private static DataResult<LocalTime> create(final Data data) {
      ULocale locale = new ULocale(data.localeId);
      Calendar calendar = (Calendar)data.timeZone.map((tz) -> Calendar.getInstance(tz, locale)).orElseGet(() -> Calendar.getInstance(locale));
      SimpleDateFormat parsedFormat = new SimpleDateFormat(data.format, locale);
      parsedFormat.setCalendar(calendar);

      try {
         parsedFormat.format(new Date());
      } catch (Exception e) {
         return DataResult.error(() -> {
            String var10000 = String.valueOf(parsedFormat);
            return "Invalid time format '" + var10000 + "': " + e.getMessage();
         });
      }

      return DataResult.success(new LocalTime(data, parsedFormat));
   }

   public @Nullable String get(final ItemStack itemStack, final @Nullable ClientLevel level, final @Nullable LivingEntity owner, final int seed, final ItemDisplayContext displayContext) {
      long currentTimeMs = Util.getMillis();
      if (currentTimeMs > this.nextUpdateTimeMs) {
         this.lastResult = this.update();
         this.nextUpdateTimeMs = currentTimeMs + UPDATE_INTERVAL_MS;
      }

      return this.lastResult;
   }

   private String update() {
      return this.parsedFormat.format(new Date());
   }

   public SelectItemModelProperty.Type<LocalTime, String> type() {
      return TYPE;
   }

   public Codec<String> valueCodec() {
      return VALUE_CODEC;
   }

   static {
      UPDATE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1L);
      VALUE_CODEC = Codec.STRING;
      TIME_ZONE_CODEC = VALUE_CODEC.comapFlatMap((s) -> {
         TimeZone tz = TimeZone.getTimeZone(s);
         return tz.equals(TimeZone.UNKNOWN_ZONE) ? DataResult.error(() -> "Unknown timezone: " + s) : DataResult.success(tz);
      }, TimeZone::getID);
      DATA_MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("pattern").forGetter((o) -> o.format), Codec.STRING.optionalFieldOf("locale", "").forGetter((o) -> o.localeId), TIME_ZONE_CODEC.optionalFieldOf("time_zone").forGetter((o) -> o.timeZone)).apply(i, Data::new));
      TYPE = SelectItemModelProperty.Type.<LocalTime, String>create(DATA_MAP_CODEC.flatXmap(LocalTime::create, (d) -> DataResult.success(d.data)), VALUE_CODEC);
   }

   private static record Data(String format, String localeId, Optional<TimeZone> timeZone) {
      private Data {
         super();
      }
   }
}
