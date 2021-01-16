package org.apache.logging.log4j.core.appender.db.jpa.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

@Converter(
   autoApply = false
)
public class MessageAttributeConverter implements AttributeConverter<Message, String> {
   private static final StatusLogger LOGGER = StatusLogger.getLogger();

   public MessageAttributeConverter() {
      super();
   }

   public String convertToDatabaseColumn(Message var1) {
      return var1 == null ? null : var1.getFormattedMessage();
   }

   public Message convertToEntityAttribute(String var1) {
      return Strings.isEmpty(var1) ? null : LOGGER.getMessageFactory().newMessage(var1);
   }
}
