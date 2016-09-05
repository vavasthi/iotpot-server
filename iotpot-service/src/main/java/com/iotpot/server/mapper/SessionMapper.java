/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.mapper;


import com.iotpot.server.pojos.Session;
import com.iotpot.server.dao.AccountDao;
import com.iotpot.server.entity.AccountEntity;
import com.iotpot.server.entity.SessionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinay on 1/8/16.
 */
@Service
public final class SessionMapper {

  @Autowired
  private AccountDao accountDao;

  public List<Session> mapEntitiesIntoPojos(Iterable<SessionEntity> entities) {
    List<Session> pojos = new ArrayList<>();

    entities.forEach(e -> pojos.add(mapEntityIntoPojo(e)));

    return pojos;
  }

  public Session mapEntityIntoPojo(SessionEntity session) {
    if (session == null) {
      return null;
    }
    else {

      Session  pojo = new Session(session.getAuthToken(),
          session.getRemoteAddress(),
          session.getApplicationId(),
          session.getExpiry(),
          Session.SESSION_TYPE.createFromInteger(session.getSessionType()));
      return pojo;
    }
  }

  public List<SessionEntity> mapPojosIntoEntitiess(Iterable<Session> pojos) {
    List<SessionEntity> entities = new ArrayList<>();

    pojos.forEach(e -> {
      try {
        entities.add(mapPojoIntoEntity(e));
      } catch (DatatypeConfigurationException e1) {
        e1.printStackTrace();
      }
    });

    return entities;
  }

  public SessionEntity mapPojoIntoEntity(Session pojo) throws DatatypeConfigurationException {
    AccountEntity accountEntity = accountDao.findOne(pojo.getAccountId());
    if (pojo == null) {
      return null;
    }
    else {

      SessionEntity re  = new SessionEntity(pojo.getAuthToken(),
          pojo.getRemoteAddress(),
          pojo.getApplicationId(),
          accountEntity,
          pojo.getSessionType().getIValue());
      return re;
    }
  }

}
