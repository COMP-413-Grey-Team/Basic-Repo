package edu.rice.comp413fall2020grey.Common;

import java.util.Date;
import java.util.UUID;

public interface Message {

  Date getTimestamp();

  UUID getOriginSuperpeer();

}
