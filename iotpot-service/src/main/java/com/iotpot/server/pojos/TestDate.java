package com.iotpot.server.pojos;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.iotpot.server.serializers.IoTPotDateTimeDeserializer;
import com.iotpot.server.serializers.IoTPotDateTimeSerializer;
import org.joda.time.DateTime;

/**
 * Created by vinay on 3/3/16.
 */
public class TestDate extends Base {

  @JsonSerialize(using = IoTPotDateTimeSerializer.class)
  @JsonDeserialize(using = IoTPotDateTimeDeserializer.class)
  public DateTime date;

}
