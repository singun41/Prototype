package com.studycloud.gateway.entity;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;

import com.fasterxml.uuid.Generators;

public class TimebasedUuidGenerator extends UUIDGenerator {
  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {
    UUID uuid = Generators.timeBasedGenerator().generate();
    String[] uuidArr = uuid.toString().split("-");
    String uuidStr = uuidArr[2]+uuidArr[1]+uuidArr[0]+uuidArr[3]+uuidArr[4];
    StringBuffer sb = new StringBuffer(uuidStr);
    sb.insert(8, "-");
    sb.insert(13, "-");
    sb.insert(18, "-");
    sb.insert(23, "-");

    return UUID.fromString(sb.toString());
  }

  public static UUID getUuid() {
    UUID uuid = Generators.timeBasedGenerator().generate();
    String[] uuidArr = uuid.toString().split("-");
    String uuidStr = uuidArr[2]+uuidArr[1]+uuidArr[0]+uuidArr[3]+uuidArr[4];
    StringBuffer sb = new StringBuffer(uuidStr);
    sb.insert(8, "-");
    sb.insert(13, "-");
    sb.insert(18, "-");
    sb.insert(23, "-");
    return UUID.fromString(sb.toString());
  }
}
