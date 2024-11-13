package net.minecraft.client.renderer.item.properties.select;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class LocalTime implements SelectItemModelProperty<String> {
   public static final String ROOT_LOCALE = "";
   private static final long UPDATE_INTERVAL_MS;
   private static final Codec<TimeZone> TIME_ZONE_CODEC;
   public static final SelectItemModelProperty.Type<LocalTime, String> TYPE;
   private final String format;
   private final String localeId;
   private final Optional<TimeZone> timeZone;
   private final DateFormat parsedFormat;
   private long nextUpdateTimeMs;
   private String lastResult = "";

   private LocalTime(String var1, Optional<TimeZone> var2, String var3, DateFormat var4) {
      super();
      this.format = var1;
      this.timeZone = var2;
      this.localeId = var3;
      this.parsedFormat = var4;
   }

   public static LocalTime create(String var0, String var1, Optional<TimeZone> var2) {
      ULocale var3 = new ULocale(var1);
      Calendar var4 = (Calendar)var2.map((var1x) -> Calendar.getInstance(var1x, var3)).orElseGet(() -> Calendar.getInstance(var3));
      SimpleDateFormat var5 = new SimpleDateFormat(var0, var3);
      var5.setCalendar(var4);
      return new LocalTime(var0, var2, var1, var5);
   }

   @Nullable
   public String get(ItemStack var1, @Nullable ClientLevel var2, @Nullable LivingEntity var3, int var4, ItemDisplayContext var5) {
      long var6 = Util.getMillis();
      if (var6 > this.nextUpdateTimeMs) {
         this.lastResult = this.update();
         this.nextUpdateTimeMs = var6 + UPDATE_INTERVAL_MS;
      }

      return this.lastResult;
   }

   private String update() {
      return this.parsedFormat.format(new Date());
   }

   public SelectItemModelProperty.Type<LocalTime, String> type() {
      return TYPE;
   }

   // $FF: synthetic method
   @Nullable
   public Object get(final ItemStack var1, @Nullable final ClientLevel var2, @Nullable final LivingEntity var3, final int var4, final ItemDisplayContext var5) {
      return this.get(var1, var2, var3, var4, var5);
   }

   static {
      UPDATE_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1L);
      TIME_ZONE_CODEC = Codec.STRING.comapFlatMap((var0) -> {
         TimeZone var1 = TimeZone.getTimeZone(var0);
         return var1.equals(TimeZone.UNKNOWN_ZONE) ? DataResult.error(() -> "Unknown timezone: " + var0) : DataResult.success(var1);
      }, TimeZone::getID);
      TYPE = SelectItemModelProperty.Type.<LocalTime, String>create(RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("pattern").forGetter((var0x) -> var0x.format), Codec.STRING.optionalFieldOf("locale", "").forGetter((var0x) -> var0x.localeId), TIME_ZONE_CODEC.optionalFieldOf("time_zone").forGetter((var0x) -> var0x.timeZone)).apply(var0, LocalTime::create)), Codec.STRING);
   }
}
