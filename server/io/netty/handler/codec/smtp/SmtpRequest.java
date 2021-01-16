package io.netty.handler.codec.smtp;

import java.util.List;

public interface SmtpRequest {
   SmtpCommand command();

   List<CharSequence> parameters();
}
