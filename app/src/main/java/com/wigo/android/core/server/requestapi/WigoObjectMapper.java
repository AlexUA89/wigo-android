package com.wigo.android.core.server.requestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Created by AlexUA89 on 12/4/2016.
 */

public class WigoObjectMapper extends ObjectMapper {

    public WigoObjectMapper(){
        super();
        configure(com.fasterxml.jackson.databind.SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS , false);
        registerModule(new JodaModule());
    }



}
